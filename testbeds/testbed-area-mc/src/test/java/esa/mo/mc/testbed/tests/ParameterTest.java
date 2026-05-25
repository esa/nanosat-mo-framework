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

import esa.mo.mc.testbed.SetUpProvidersAndConsumers;
import esa.mo.mc.testbed.backends.SimpleParameterBackend;
import java.io.IOException;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.AttributeType;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterStub;
import org.ccsds.moims.mo.mc.structures.ParameterDefinition;
import org.ccsds.moims.mo.mc.structures.ParameterDefinitionList;
import org.ccsds.moims.mo.mc.structures.ParameterValueDetails;
import org.ccsds.moims.mo.mc.structures.ParameterValueDetailsList;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for the MC Parameter service getValue operation.
 */
public class ParameterTest {

    private static final SetUpProvidersAndConsumers harness = new SetUpProvidersAndConsumers();

    private static final SimpleParameterBackend BACKEND = new SimpleParameterBackend(42);

    @BeforeClass
    public static void setUpClass() throws IOException {
        harness.setUp(false, false, true, false, BACKEND);
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        harness.tearDown();
    }

    @Test
    public void testGetValue() throws MALInteractionException, MALException {
        System.out.println("Running: testGetValue()");
        ParameterDefinition def = new ParameterDefinition(
                new Identifier("TestParam"),
                "A simple integer test parameter",
                AttributeType.INTEGER,
                false,
                new Duration(0));

        ParameterDefinitionList defs = new ParameterDefinitionList();
        defs.add(def);
        LongList ids = harness.getParameterProvider().addParameters(defs, null);

        Assert.assertNotNull("addParameters must return a non-null ID list", ids);
        Assert.assertEquals("One ID must be returned", 1, ids.size());
        System.out.println("Number of parameters added: " + ids.size());

        ParameterStub stub = harness.getParameterConsumerStub().getParameterStub();
        ParameterValueDetailsList result = stub.getValue(ids);

        Assert.assertNotNull("getValue must return a non-null list", result);
        Assert.assertEquals("One value must be returned", 1, result.size());

        ParameterValueDetails details = result.get(0);
        Assert.assertNotNull("ParameterValueDetails must not be null", details);

        Union rawValue = (Union) details.getValue().getRawValue();
        System.out.println("The raw value returned is: " + rawValue.getIntegerValue());
        Assert.assertEquals("Raw value must match the listener's fixed return value",
                Integer.valueOf(42), rawValue.getIntegerValue());
        System.out.flush();
    }

}
