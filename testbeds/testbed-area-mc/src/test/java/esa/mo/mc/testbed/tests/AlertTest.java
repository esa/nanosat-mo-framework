/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 */
package esa.mo.mc.testbed.tests;

import esa.mo.mc.impl.consumer.AlertConsumerServiceImpl;
import esa.mo.mc.testbed.SetUpProvidersAndConsumers;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.alert.consumer.AlertAdapter;
import org.ccsds.moims.mo.mc.alert.consumer.MonitorAlertSubscriptionKeys;
import org.ccsds.moims.mo.mc.structures.AlertEvent;
import org.ccsds.moims.mo.mc.structures.AttributeValue;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mal.structures.Union;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * End-to-end test for the Alert service.
 *
 * Publishes an alert via the provider and verifies it is received on the
 * consumer side via the monitorAlert PUB-SUB operation. This exercises the
 * full provider→broker→consumer pipeline.
 */
public class AlertTest {

    private static final Logger LOGGER = Logger.getLogger(AlertTest.class.getName());
    private static final SetUpProvidersAndConsumers harness = new SetUpProvidersAndConsumers();

    @BeforeClass
    public static void setUpClass() throws IOException {
        harness.setUp(false, true, false, false, null);
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        harness.tearDown();
    }

    @Test
    public void testPublishAlertEventIsReceived() throws MALException, MALInteractionException,
            java.net.MalformedURLException, InterruptedException {
        LOGGER.info("Running: testPublishAlertEventIsReceived()");

        AlertConsumerServiceImpl alertConsumer = harness.getAlertConsumerStub();

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<AlertEvent> receivedAlert = new AtomicReference<>();
        AtomicReference<Long> receivedDefinitionId = new AtomicReference<>();

        Subscription sub = ConnectionConsumer.subscriptionWildcardRandom();
        alertConsumer.getAlertStub().monitorAlertRegister(sub, new AlertAdapter() {
            @Override
            public void monitorAlertNotifyReceived(MALMessageHeader msgHeader,
                    Identifier subscriptionId,
                    UpdateHeader updateHeader,
                    MonitorAlertSubscriptionKeys keys,
                    AlertEvent alertEvent,
                    ObjectKey source,
                    Map qosProperties) {
                if (keys.getDefinitionId() != null) {
                    receivedDefinitionId.set(keys.getDefinitionId());
                }
                receivedAlert.set(alertEvent);
                latch.countDown();
            }
        });

        // Publish an alert event with a single integer argument
        AttributeValueList args = new AttributeValueList();
        args.add(new AttributeValue(new Union(42)));
        Long eventObjId = harness.getAlertProvider().publishAlertEvent(
                null, new Identifier("TestAlert"), args, null, null);

        Assert.assertNotNull("publishAlertEvent must return a non-null event object ID", eventObjId);
        LOGGER.info("The returned event object ID is: " + eventObjId);

        boolean delivered = latch.await(5, TimeUnit.SECONDS);
        Assert.assertTrue("Alert must be received by the consumer within 5 seconds", delivered);

        AlertEvent alertEvent = receivedAlert.get();
        Assert.assertNotNull("Received alert event must not be null", alertEvent);

        Long definitionId = receivedDefinitionId.get();
        Assert.assertNotNull("Definition ID subscription key must be present", definitionId);

        AttributeValueList receivedArgs = alertEvent.getArgumentValues();
        Assert.assertNotNull("Argument values must not be null", receivedArgs);
        Assert.assertEquals("Must have exactly one argument", 1, receivedArgs.size());

        Union receivedValue = (Union) receivedArgs.get(0).getValue();
        LOGGER.info("The received argument value is: " + receivedValue.getIntegerValue());
        Assert.assertEquals("Argument value must match the published value",
                Integer.valueOf(42), receivedValue.getIntegerValue());

        // Deregister
        IdentifierList ids = new IdentifierList();
        ids.add(sub.getSubscriptionId());
        alertConsumer.getAlertStub().monitorAlertDeregister(ids);
    }

}
