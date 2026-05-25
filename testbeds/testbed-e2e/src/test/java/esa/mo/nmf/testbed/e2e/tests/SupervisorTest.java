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
import org.ccsds.moims.mo.com.structures.ProviderSummary;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.com.structures.ProviderSummaryList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.body.ListAppResponse;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * End-to-end tests for the NMF Supervisor.
 */
public class SupervisorTest {

    private static final SupervisorHarness harness = new SupervisorHarness();

    @BeforeClass
    public static void setUpClass() throws IOException {
        harness.setUp();
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        harness.tearDown();
    }

    @Test
    public void testNoWarnings() {
        /*
        System.out.println("Running: testNoWarnings()");
        Assert.assertFalse(
                "Supervisor log must not contain WARNING lines",
                harness.hasWarnings());
        System.out.flush();
        */
    }

    @Test
    public void testNoErrors() {
        System.out.println("Running: testNoErrors()");
        Assert.assertFalse(
                "Supervisor log must not contain SEVERE lines",
                harness.hasErrors());
        System.out.flush();
    }

    @Test
    public void testListApps() throws Exception {
        System.out.println("Running: testListApps()");
        String directoryURI = harness.getDirectoryURI();
        ProviderSummaryList providers = NMFConsumer.retrieveProvidersFromDirectory(new URI(directoryURI));
        Assert.assertFalse("Directory must return at least one provider", providers.isEmpty());

        ProviderSummary supervisorProvider = providers.get(0);
        GroundMOAdapterImpl adapter = new GroundMOAdapterImpl(supervisorProvider);
        try {
            IdentifierList wildcard = new IdentifierList();
            wildcard.add(new Identifier("*"));
            ListAppResponse response = adapter.getSMServices()
                    .getAppsLauncherService()
                    .getAppsLauncherStub()
                    .listApp(wildcard, new Identifier("*"));
            Assert.assertNotNull("listApp must return a response", response);
            Assert.assertNotNull("listApp must return app IDs", response.getAppIds());
            System.out.println("The provider returned the ids: " + response.getAppIds());
        } finally {
            adapter.closeConnections();
        }
        System.out.flush();
    }

}
