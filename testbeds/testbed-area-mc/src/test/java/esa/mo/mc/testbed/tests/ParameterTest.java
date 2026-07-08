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
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.AttributeType;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterStub;
import org.ccsds.moims.mo.mc.structures.ParameterDefinition;
import org.ccsds.moims.mo.mc.structures.ParameterDefinitionList;
import org.ccsds.moims.mo.mc.structures.ParameterRawValue;
import org.ccsds.moims.mo.mc.structures.ParameterRawValueList;
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

    private static final Logger LOGGER = Logger.getLogger(ParameterTest.class.getName());
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
    public void testGetValue() throws MALInteractionException, MALException, org.ccsds.moims.mo.com.InvalidArgumentException, org.ccsds.moims.mo.com.DuplicateException {
        LOGGER.info("Running: testGetValue()");
        ParameterDefinition def = new ParameterDefinition(
                new Identifier("TestParam"),
                "A simple integer test parameter",
                AttributeType.INTEGER,
                false,
                new Duration(0), false);

        ParameterDefinitionList defs = new ParameterDefinitionList();
        defs.add(def);
        LongList ids = harness.getParameterProvider().addParameters(defs, null);

        Assert.assertNotNull("addParameters must return a non-null ID list", ids);
        Assert.assertEquals("One ID must be returned", 1, ids.size());
        LOGGER.info("Number of parameters added: " + ids.size());

        ParameterStub stub = harness.getParameterConsumerStub().getParameterStub();
        ParameterValueDetailsList result = stub.getValue(ids);

        Assert.assertNotNull("getValue must return a non-null list", result);
        Assert.assertEquals("One value must be returned", 1, result.size());

        ParameterValueDetails details = result.get(0);
        Assert.assertNotNull("ParameterValueDetails must not be null", details);

        Union rawValue = (Union) details.getValue().getRawValue();
        LOGGER.info("The raw value returned is: " + rawValue.getIntegerValue());
        Assert.assertEquals("Raw value must match the listener's fixed return value",
                Integer.valueOf(42), rawValue.getIntegerValue());
    }

    /**
     * Registers a single parameter and returns its id.
     */
    private static Long addParameter(String name, boolean readOnly) throws Exception {
        ParameterDefinition def = new ParameterDefinition(new Identifier(name),
                "A test parameter", AttributeType.INTEGER, true, new Duration(0), readOnly);
        ParameterDefinitionList defs = new ParameterDefinitionList();
        defs.add(def);
        LongList ids = harness.getParameterProvider().addParameters(defs, null);
        Assert.assertEquals("One id must be returned", 1, ids.size());
        return ids.get(0);
    }

    private static ParameterRawValueList rawValue(Long id, int value) {
        ParameterRawValueList list = new ParameterRawValueList();
        list.add(new ParameterRawValue(id, new Union(value)));
        return list;
    }

    // Test — Setting a read-write parameter succeeds

    @Test
    public void testSetReadWriteParameterSucceeds() throws Exception {
        LOGGER.info("Running: testSetReadWriteParameterSucceeds()");
        Long id = addParameter("WritableParam", false);
        ParameterStub stub = harness.getParameterConsumerStub().getParameterStub();

        stub.setValue(rawValue(id, 7)); // Must not throw
    }

    // Test — Setting a read-only parameter is rejected with a Read Only error

    @Test
    public void testSetReadOnlyParameterReturnsError() throws Exception {
        LOGGER.info("Running: testSetReadOnlyParameterReturnsError()");
        Long id = addParameter("ReadOnlyParam", true);
        ParameterStub stub = harness.getParameterConsumerStub().getParameterStub();

        try {
            stub.setValue(rawValue(id, 7));
            Assert.fail("setValue on a read-only parameter must throw");
        } catch (MALInteractionException ex) {
            Assert.assertEquals("The error must be a Read Only error",
                    MCHelper.READ_ONLY_ERROR_NUMBER,
                    ex.getStandardError().getErrorNumber());
        }
    }

    // Test — In a mixed batch, one read-only parameter rejects the whole set

    @Test
    public void testSetMixedBatchRejectedWhenOneIsReadOnly() throws Exception {
        LOGGER.info("Running: testSetMixedBatchRejectedWhenOneIsReadOnly()");
        Long writable = addParameter("WritableInBatch", false);
        Long readOnly = addParameter("ReadOnlyInBatch", true);
        ParameterStub stub = harness.getParameterConsumerStub().getParameterStub();

        ParameterRawValueList values = new ParameterRawValueList();
        values.add(new ParameterRawValue(writable, new Union(1)));
        values.add(new ParameterRawValue(readOnly, new Union(2)));

        try {
            stub.setValue(values);
            Assert.fail("A batch containing a read-only parameter must be rejected");
        } catch (MALInteractionException ex) {
            Assert.assertEquals("The error must be a Read Only error",
                    MCHelper.READ_ONLY_ERROR_NUMBER,
                    ex.getStandardError().getErrorNumber());
        }
    }

}
