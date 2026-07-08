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
package esa.mo.nmf.testbed.e2e.tests;

import esa.mo.nmf.NMFConsumer;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import esa.mo.nmf.testbed.e2e.SupervisorHarness;
import java.io.IOException;
import org.ccsds.moims.mo.com.structures.Provider;
import org.ccsds.moims.mo.com.structures.ProviderList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterStub;
import org.ccsds.moims.mo.mc.structures.ParameterRawValue;
import org.ccsds.moims.mo.mc.structures.ParameterRawValueList;
import org.ccsds.moims.mo.mc.structures.ParameterValueDetailsList;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * End-to-end tests for the default Supervisor MC set: parameters that every
 * NMF Supervisor exposes regardless of mission (NMF Bootloader / default MC
 * contract). Verifies they are present alongside the mission-specific
 * parameters, readable, and read-only.
 */
public class DefaultSupervisorMCTest extends NMFTest {

    private static final SupervisorHarness harness = new SupervisorHarness();

    private static final String NMF_VERSION = "nmf.version";
    private static final String NMF_UPTIME = "nmf.uptime";
    private static final String MISSION_PARAM = "OSVersion"; // from MCSupervisorBasicAdapter

    private static GroundMOAdapterImpl adapter;
    private static ParameterStub parameterStub;

    @BeforeClass
    public static void setUpClass() throws IOException, MALInteractionException, MALException {
        LOGGER.info(SETUP_CLASS_SEP + "\n" + SETUP_CLASS_MSG + "\n" + SETUP_CLASS_SEP);
        harness.setUp();

        ProviderList providers = NMFConsumer.retrieveProvidersFromDirectory(
                new URI(harness.getDirectoryURI()));
        Assert.assertFalse("Directory must return a provider", providers.isEmpty());
        Provider supervisor = providers.get(0);
        adapter = new GroundMOAdapterImpl(supervisor);
        parameterStub = adapter.getMCServices().getParameterService().getParameterStub();
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        if (adapter != null) {
            adapter.closeConnections();
        }
        harness.tearDown();
    }

    // Test — The default parameters are registered on the Supervisor

    @Test
    public void testDefaultParametersExist() throws Exception {
        LOGGER.info(SEP + "\nRunning: testDefaultParametersExist()\n" + SEP);
        IdentifierList names = new IdentifierList();
        names.add(new Identifier(NMF_VERSION));
        names.add(new Identifier(NMF_UPTIME));

        LongList ids = parameterStub.listDefinition(names);
        Assert.assertEquals("Both default parameters must be defined", 2, ids.size());
        for (Long id : ids) {
            Assert.assertNotEquals("A default parameter id must be non-zero", Long.valueOf(0), id);
        }
    }

    // Test — The default set coexists with the mission's own parameters

    @Test
    public void testDefaultAndMissionParametersCoexist() throws Exception {
        LOGGER.info(SEP + "\nRunning: testDefaultAndMissionParametersCoexist()\n" + SEP);
        IdentifierList names = new IdentifierList();
        names.add(new Identifier(NMF_VERSION));   // default (composite)
        names.add(new Identifier(MISSION_PARAM)); // mission (MCSupervisorBasicAdapter)

        LongList ids = parameterStub.listDefinition(names);
        Assert.assertEquals("Default and mission parameters must both resolve on one provider",
                2, ids.size());
    }

    // Test — The default parameters are readable and carry sensible values

    @Test
    public void testDefaultParametersAreReadable() throws Exception {
        LOGGER.info(SEP + "\nRunning: testDefaultParametersAreReadable()\n" + SEP);
        IdentifierList names = new IdentifierList();
        names.add(new Identifier(NMF_VERSION));
        names.add(new Identifier(NMF_UPTIME));
        LongList ids = parameterStub.listDefinition(names);

        ParameterValueDetailsList values = parameterStub.getValue(ids);
        Assert.assertEquals("Two values must be returned", 2, values.size());

        String version = ((Union) values.get(0).getValue().getRawValue()).getStringValue();
        Assert.assertNotNull("nmf.version must have a value", version);
        Assert.assertFalse("nmf.version must not be empty", version.isEmpty());

        double uptime = ((Union) values.get(1).getValue().getRawValue()).getDoubleValue();
        Assert.assertTrue("nmf.uptime must be non-negative", uptime >= 0.0);
        LOGGER.info("nmf.version=" + version + ", nmf.uptime=" + uptime + "s");
    }

    // Test — The memory parameters are registered and report sensible values

    @Test
    public void testMemoryParametersAreReadable() throws Exception {
        LOGGER.info(SEP + "\nRunning: testMemoryParametersAreReadable()\n" + SEP);
        IdentifierList names = new IdentifierList();
        names.add(new Identifier("memory.ram.total"));
        names.add(new Identifier("memory.ram.percentage"));
        LongList ids = parameterStub.listDefinition(names);
        Assert.assertEquals("Both memory parameters must be defined", 2, ids.size());

        ParameterValueDetailsList values = parameterStub.getValue(ids);
        long ramTotal = ((Union) values.get(0).getValue().getRawValue()).getLongValue();
        double ramPct = ((Union) values.get(1).getValue().getRawValue()).getDoubleValue();
        Assert.assertTrue("memory.ram.total must be positive", ramTotal > 0);
        Assert.assertTrue("memory.ram.percentage must be within 0..100",
                ramPct >= 0.0 && ramPct <= 100.0);
        LOGGER.info("memory.ram.total=" + ramTotal + " bytes, memory.ram.percentage=" + ramPct + "%");
    }

    // Test — Setting a default (read-only) parameter returns a Read Only error

    @Test
    public void testDefaultParameterIsReadOnly() throws Exception {
        LOGGER.info(SEP + "\nRunning: testDefaultParameterIsReadOnly()\n" + SEP);
        IdentifierList names = new IdentifierList();
        names.add(new Identifier(NMF_VERSION));
        Long id = parameterStub.listDefinition(names).get(0);

        ParameterRawValueList values = new ParameterRawValueList();
        values.add(new ParameterRawValue(id, new Union("6.6.6")));

        try {
            parameterStub.setValue(values);
            Assert.fail("Setting a read-only default parameter must throw");
        } catch (MALInteractionException ex) {
            Assert.assertEquals("The error must be a Read Only error",
                    MCHelper.READ_ONLY_ERROR_NUMBER, ex.getStandardError().getErrorNumber());
        }
    }
}
