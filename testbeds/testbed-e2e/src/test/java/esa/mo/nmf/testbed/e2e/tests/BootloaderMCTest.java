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
import esa.mo.nmf.environment.Deployment;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import esa.mo.nmf.testbed.e2e.SupervisorHarness;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.Properties;
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
 * End-to-end tests for the NMF Bootloader MC set (NMF Bootloader
 * Specification, Phase 3). Verifies that the Supervisor exposes the software
 * baseline files as read-only parameters and that the primary baseline can be
 * commanded through the validated {@code bootloader.setPrimaryBaseline} action.
 * The Supervisor runs from a real generated filesystem, so the on-disk baseline
 * files and {@code SHA256SUMS} manifests exercised here are the ones the
 * bootloader itself consumes.
 */
public class BootloaderMCTest extends NMFTest {

    private static final SupervisorHarness harness = new SupervisorHarness();

    private static final String SET_PRIMARY = "bootloader.setPrimaryBaseline";
    private static final String P_NMF = "bootloader.primary.nmf-version";
    private static final String P_MISSION = "bootloader.primary.mission-version";
    private static final String P_JAVA = "bootloader.primary.java";
    private static final String P_MAIN_CLASS = "bootloader.primary.main-class";

    private static final long EFFECT_TIMEOUT_MS = 5000;

    private static GroundMOAdapterImpl adapter;
    private static ParameterStub parameterStub;
    private static byte[] primaryBaselineBackup;

    @BeforeClass
    public static void setUpClass() throws IOException, MALInteractionException, MALException {
        LOGGER.info(SETUP_CLASS_SEP + "\n" + SETUP_CLASS_MSG + "\n" + SETUP_CLASS_SEP);
        harness.setUp();

        // These tests command the primary baseline, mutating the shared
        // filesystem's baseline-primary.properties. Snapshot it so it can be
        // restored, otherwise a later test class would boot the mutated baseline.
        primaryBaselineBackup = Files.readAllBytes(baselineFile(Deployment.ROLE_PRIMARY).toPath());

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

        // Restore the primary baseline so the shared filesystem is left as found
        if (primaryBaselineBackup != null) {
            Files.write(baselineFile(Deployment.ROLE_PRIMARY).toPath(), primaryBaselineBackup);
        }
    }

    // Test — The bootloader parameters mirror the on-disk baseline file

    @Test
    public void testBootloaderParamsMatchBaselineFile() throws Exception {
        LOGGER.info(SEP + "\nRunning: testBootloaderParamsMatchBaselineFile()\n" + SEP);
        Properties primary = readBaselineFile(Deployment.ROLE_PRIMARY);

        Assert.assertEquals(primary.getProperty("nmf-version"), readParam(P_NMF));
        Assert.assertEquals(primary.getProperty("mission-version"), readParam(P_MISSION));
        Assert.assertEquals(primary.getProperty("java"), readParam(P_JAVA));
        Assert.assertEquals(primary.getProperty("main-class"), readParam(P_MAIN_CLASS));
    }

    // Test — The bootloader parameters are read-only

    @Test
    public void testBootloaderParamsAreReadOnly() throws Exception {
        LOGGER.info(SEP + "\nRunning: testBootloaderParamsAreReadOnly()\n" + SEP);
        IdentifierList names = new IdentifierList();
        names.add(new Identifier(P_NMF));
        Long id = parameterStub.listDefinition(names).get(0);

        ParameterRawValueList values = new ParameterRawValueList();
        values.add(new ParameterRawValue(id, new Union("6.6.6")));

        try {
            parameterStub.setValue(values);
            Assert.fail("Setting a read-only bootloader parameter must throw");
        } catch (MALInteractionException ex) {
            Assert.assertEquals("The error must be a Read Only error",
                    MCHelper.READ_ONLY_ERROR_NUMBER, ex.getStandardError().getErrorNumber());
        }
    }

    // Test — A valid setPrimaryBaseline is accepted and committed to disk

    @Test
    public void testSetPrimaryBaselineValidCommits() throws Exception {
        LOGGER.info(SEP + "\nRunning: testSetPrimaryBaselineValidCommits()\n" + SEP);
        Properties primary = readBaselineFile(Deployment.ROLE_PRIMARY);
        String nmf = primary.getProperty("nmf-version");
        String mission = primary.getProperty("mission-version");
        String java = primary.getProperty("java");
        // Same (installed, intact) versions so validation passes; a sentinel
        // main-class so the commit is observable.
        String sentinel = "esa.mo.nmf.SentinelMainClass";

        Long actionId = adapter.launchAction(SET_PRIMARY,
                new Serializable[]{nmf, mission, java, sentinel});
        Assert.assertNotNull("The action must be submitted", actionId);
        Assert.assertNotEquals("The action must be found", Long.valueOf(-1), actionId);

        Assert.assertTrue("The primary main-class must be updated on disk",
                waitForParam(P_MAIN_CLASS, sentinel));
        Assert.assertEquals("The on-disk baseline file must carry the new main-class",
                sentinel, readBaselineFile(Deployment.ROLE_PRIMARY).getProperty("main-class"));
        // The validated version fields must be preserved
        Assert.assertEquals(nmf, readParam(P_NMF));
        Assert.assertEquals(mission, readParam(P_MISSION));
    }

    // Test — An invalid setPrimaryBaseline is rejected and leaves the file intact

    @Test
    public void testSetPrimaryBaselineInvalidRejected() throws Exception {
        LOGGER.info(SEP + "\nRunning: testSetPrimaryBaselineInvalidRejected()\n" + SEP);
        Properties before = readBaselineFile(Deployment.ROLE_PRIMARY);
        String mission = before.getProperty("mission-version");
        String java = before.getProperty("java");
        String mainClass = before.getProperty("main-class");
        String bogusNmf = "does-not-exist-9.9";

        adapter.launchAction(SET_PRIMARY,
                new Serializable[]{bogusNmf, mission, java, mainClass});

        // Give the provider time to (not) apply the change, then assert nothing moved
        Thread.sleep(1500);
        Assert.assertNotEquals("A bogus nmf-version must never be committed",
                bogusNmf, readParam(P_NMF));
        Assert.assertEquals("The on-disk baseline file must be unchanged",
                before.getProperty("nmf-version"),
                readBaselineFile(Deployment.ROLE_PRIMARY).getProperty("nmf-version"));
    }

    // Helpers

    private static String readParam(String name) throws Exception {
        IdentifierList names = new IdentifierList();
        names.add(new Identifier(name));
        LongList ids = parameterStub.listDefinition(names);
        Assert.assertEquals("Parameter must be defined: " + name, 1, ids.size());
        ParameterValueDetailsList values = parameterStub.getValue(ids);
        return ((Union) values.get(0).getValue().getRawValue()).getStringValue();
    }

    private static boolean waitForParam(String name, String expected) throws Exception {
        long deadline = System.currentTimeMillis() + EFFECT_TIMEOUT_MS;
        while (System.currentTimeMillis() < deadline) {
            if (expected.equals(readParam(name))) {
                return true;
            }
            Thread.sleep(200);
        }
        return expected.equals(readParam(name));
    }

    private static File baselineFile(String role) {
        return new File(new File(harness.getNmfDir(), Deployment.DIR_BOOTLOADER),
                Deployment.baselineFileName(role));
    }

    private static Properties readBaselineFile(String role) throws IOException {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(baselineFile(role))) {
            props.load(in);
        }
        return props;
    }
}
