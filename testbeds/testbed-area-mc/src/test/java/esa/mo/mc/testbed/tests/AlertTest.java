/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
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

import esa.mo.com.impl.consumer.EventConsumerServiceImpl;
import esa.mo.com.impl.util.EventCOMObject;
import esa.mo.com.impl.util.EventReceivedListener;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.mc.testbed.SetUpProvidersAndConsumers;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mc.structures.AttributeValue;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.alert.AlertServiceInfo;
import org.ccsds.moims.mo.mc.structures.AlertEventList;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * End-to-end test for the Alert service.
 *
 * Publishes an alert event via the provider and verifies it is received on the
 * consumer side via the COM Event service. This exercises the full
 * provider→Event service→consumer pipeline, including the subscription key
 * encoding that carries the event object number (K1).
 */
public class AlertTest {

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

        // Connect an event consumer directly to the COM Event service provider
        SingleConnectionDetails eventDetails = harness.getCOMServicesProvider()
                .getEventService().getConnectionProvider().getConnectionDetails();
        EventConsumerServiceImpl eventConsumer = new EventConsumerServiceImpl(eventDetails);

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<EventCOMObject> receivedEvent = new AtomicReference<>();

        Subscription sub = HelperCOM.generateSubscriptionCOMEvent(
                "AlertTest", AlertServiceInfo.ALERTEVENT_OBJECT_TYPE);
        eventConsumer.addEventReceivedListener(sub, new EventReceivedListener() {
            @Override
            public void onDataReceived(EventCOMObject event) {
                receivedEvent.set(event);
                latch.countDown();
            }
        });

        // Publish an alert event with a single integer argument
        AttributeValueList args = new AttributeValueList();
        args.add(new AttributeValue(new Union(42)));
        Long eventObjId = harness.getAlertProvider().publishAlertEvent(
                null, new Identifier("TestAlert"), args, null, null);

        Assert.assertNotNull("publishAlertEvent must return a non-null event object ID", eventObjId);

        boolean delivered = latch.await(5, TimeUnit.SECONDS);
        Assert.assertTrue("Alert event must be received by the consumer within 5 seconds", delivered);

        EventCOMObject event = receivedEvent.get();
        Assert.assertNotNull("Received event must not be null", event);
        Assert.assertEquals("Event object ID must match the published event object ID",
                eventObjId, event.getObjId());

        // Core assertion: the ObjectType round-trips correctly through the Event
        // service subscription keys (exercises K1 encoding/decoding)
        Assert.assertEquals("Event ObjectType must match ALERTEVENT_OBJECT_TYPE",
                AlertServiceInfo.ALERTEVENT_OBJECT_TYPE, event.getObjType());

        // Verify the event body arrived intact
        Assert.assertNotNull("Event body must not be null", event.getBody());
        Assert.assertTrue("Event body must be an AlertEventList",
                event.getBody() instanceof AlertEventList);

        AlertEventList alertEvents = (AlertEventList) event.getBody();
        Assert.assertEquals("AlertEventList must contain exactly one event", 1, alertEvents.size());

        AttributeValueList receivedArgs = alertEvents.get(0).getArgumentValues();
        Assert.assertNotNull("Argument values must not be null", receivedArgs);
        Assert.assertEquals("Must have exactly one argument", 1, receivedArgs.size());

        Union receivedValue = (Union) receivedArgs.get(0).getValue();
        Assert.assertEquals("Argument value must match the published value",
                Integer.valueOf(42), receivedValue.getIntegerValue());
    }

}
