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
package esa.mo.nmf.testbed.e2e;

import esa.mo.nmf.NMFConsumer;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ProviderSummary;
import org.ccsds.moims.mo.com.structures.ProviderSummaryList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.body.ListAppResponse;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.consumer.AppsLauncherAdapter;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.consumer.AppsLauncherStub;

/**
 * Manages the lifecycle of a named NMF App for end-to-end tests.
 *
 * <p>Requires a running Supervisor; construct with the same
 * {@link SupervisorHarness} instance used by the test class and call
 * {@link SupervisorHarness#setUp()} before calling {@link #setUp()} here.
 *
 * @author Cesar Coelho
 */
public class AppHarness {

    private static final Logger LOGGER = Logger.getLogger(AppHarness.class.getName());
    private static final int STARTUP_TIMEOUT_SECONDS = 60;
    private static final String WILDCARD = "*";

    private final String appName;
    private final SupervisorHarness supervisorHarness;

    private GroundMOAdapterImpl adapter;
    private AppsLauncherStub stub;
    private Long appId;

    /**
     * Creates a harness for the named app.
     *
     * @param appName          the NMF app name as known to the AppsLauncher.
     * @param supervisorHarness the already-constructed supervisor harness.
     */
    public AppHarness(String appName, SupervisorHarness supervisorHarness) {
        this.appName = appName;
        this.supervisorHarness = supervisorHarness;
    }

    /**
     * Requests the Supervisor to start the app and waits until it is running.
     *
     * @throws IOException if the app could not be started.
     */
    public void setUp() throws IOException {
        String directoryURIStr = supervisorHarness.getDirectoryURI();
        LOGGER.info("Connecting to Directory service at: " + directoryURIStr);

        try {
            ProviderSummaryList providers = NMFConsumer.retrieveProvidersFromDirectory(
                    new URI(directoryURIStr));

            ProviderSummary supervisorProvider = findSupervisorProvider(providers);
            if (supervisorProvider == null) {
                throw new IOException("No provider with AppsLauncher found at " + directoryURIStr);
            }

            adapter = new GroundMOAdapterImpl(supervisorProvider);
            stub = adapter.getSMServices().getAppsLauncherService().getAppsLauncherStub();

            appId = resolveAppId();
            LOGGER.info("Found app '" + appName + "' with id=" + appId);

            LongList ids = new LongList();
            ids.add(appId);
            stub.runApp(ids);
            LOGGER.info("runApp('" + appName + "') submitted.");

            waitUntilRunning();
            LOGGER.info("App '" + appName + "' is running.");
        } catch (MALException | MALInteractionException | java.net.MalformedURLException e) {
            throw new IOException("Failed to start app '" + appName + "': " + e.getMessage(), e);
        }
    }

    /**
     * Requests the Supervisor to stop the app.
     *
     * @throws IOException if the app could not be stopped.
     */
    public void tearDown() throws IOException {
        if (stub != null && appId != null) {
            try {
                LongList ids = new LongList();
                ids.add(appId);
                stub.stopApp(ids, new AppsLauncherAdapter() {});
                LOGGER.info("stopApp('" + appName + "') submitted.");
            } catch (MALException | MALInteractionException e) {
                LOGGER.log(Level.WARNING, "Error stopping app '" + appName + "': " + e.getMessage(), e);
            }
        }
        if (adapter != null) {
            adapter.closeConnections();
            adapter = null;
        }
        stub = null;
        appId = null;
    }

    /**
     * Returns the app name as registered with the Supervisor.
     *
     * @return the app name.
     */
    public String getAppName() {
        return appName;
    }

    /**
     * Returns the COM object instance ID of the app, available after {@link #setUp()}.
     *
     * @return the app's object instance ID, or {@code null} if setUp has not been called.
     */
    public Long getAppId() {
        return appId;
    }

    /**
     * Returns {@code true} if the app is currently reported as running by the AppsLauncher.
     *
     * @return true if running.
     * @throws IOException if the query fails.
     */
    public boolean isRunning() throws IOException {
        if (stub == null || appId == null) {
            return false;
        }
        try {
            IdentifierList names = new IdentifierList();
            names.add(new Identifier(appName));
            ListAppResponse response = stub.listApp(names, new Identifier(WILDCARD));
            if (response.getRunning() != null && !response.getRunning().isEmpty()) {
                return Boolean.TRUE.equals(response.getRunning().get(0));
            }
            return false;
        } catch (MALException | MALInteractionException e) {
            throw new IOException("Failed to query app running state: " + e.getMessage(), e);
        }
    }

    private ProviderSummary findSupervisorProvider(ProviderSummaryList providers) {
        // App providers are prefixed with "App: "; skip them
        for (ProviderSummary p : providers) {
            String id = p.getProviderId() != null ? p.getProviderId().getValue() : "";
            if (!id.startsWith("App: ")) {
                return p;
            }
        }
        return providers.isEmpty() ? null : providers.get(0);
    }

    private Long resolveAppId() throws MALException, MALInteractionException, IOException {
        IdentifierList names = new IdentifierList();
        names.add(new Identifier(appName));
        ListAppResponse response = stub.listApp(names, new Identifier(WILDCARD));
        LongList ids = response.getAppIds();
        if (ids == null || ids.isEmpty()) {
            throw new IOException("App '" + appName + "' not found in AppsLauncher.");
        }
        return ids.get(0);
    }

    private void waitUntilRunning() throws IOException {
        long deadline = System.currentTimeMillis() + STARTUP_TIMEOUT_SECONDS * 1000L;
        while (System.currentTimeMillis() < deadline) {
            if (isRunning()) {
                return;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Interrupted while waiting for app '" + appName + "' to start.");
            }
        }
        throw new IOException("App '" + appName + "' did not become running within "
                + STARTUP_TIMEOUT_SECONDS + " seconds.");
    }

}
