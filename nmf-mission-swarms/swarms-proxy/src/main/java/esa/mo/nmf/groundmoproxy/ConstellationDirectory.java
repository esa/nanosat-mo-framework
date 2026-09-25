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
package esa.mo.nmf.groundmoproxy;

import esa.mo.com.impl.proxy.DirectoryProxyServiceImpl;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.nmf.NMFConsumer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.NMFProviderType;
import org.ccsds.moims.mo.com.structures.Provider;
import org.ccsds.moims.mo.com.structures.ProviderList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperMisc;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 * One Directory service for a whole constellation.
 * <p>
 * Without this, a consumer has to be given the address of every spacecraft in
 * the constellation, one at a time. This service is given the Directory service
 * of each node once, queries each of them, and publishes the results together
 * in a Directory service of its own. A consumer connects to this single address
 * and finds the whole constellation.
 * <p>
 * Only the <em>Supervisor</em> of each node is published. The apps of a node are
 * found through that node once it has been selected; publishing them all here
 * would hide the spacecraft among their apps.
 * <p>
 * The published addresses are the ones the nodes provide, so a consumer that
 * selects a node talks to that spacecraft directly. No traffic is routed
 * through this service.
 * <p>
 * This is not a {@link GroundMOProxy} and does not replace it. That class serves
 * a single spacecraft and does much more: it bridges protocols, mirrors the COM
 * Archive, and re-routes the Action service of every app. This class only
 * reports which spacecraft exist.
 *
 * @author Cesar Coelho
 */
public class ConstellationDirectory {

    private static final Logger LOGGER = Logger.getLogger(ConstellationDirectory.class.getName());

    /**
     * The name this service registers itself under in its own Directory
     * service, so that a consumer can identify it.
     */
    public static final String NAME = "Constellation Directory";

    /**
     * How often a node that has not answered yet is queried again, in
     * milliseconds.
     * <p>
     * The nodes are started just before this service, and a spacecraft needs a
     * moment before its Directory service is ready. A node that does not answer
     * at startup is therefore usually still starting up rather than missing.
     */
    private static final long RETRY_PERIOD = 5000;

    private final COMServicesProvider localCOMServices = new COMServicesProvider();

    private final DirectoryProxyServiceImpl directoryService = new DirectoryProxyServiceImpl();

    /**
     * The nodes that have not answered yet. A node is removed from this set
     * once its Supervisor has been published, and is not queried again.
     */
    private final Set<URI> pending = new LinkedHashSet<>();

    private Timer timer;

    /**
     * Reads the configuration: which transport to use, and what this service
     * calls itself.
     * <p>
     * Defaults are applied first, and only for properties that are not already
     * set. This service is started from wherever the constellation is raised,
     * which does not always contain a provider.properties file, and the
     * transport it would otherwise fall back to is not the one a constellation
     * is reached over.
     */
    public ConstellationDirectory() {
        setIfUnset("org.ccsds.moims.mo.mal.factory.class",
                "esa.mo.mal.impl.MALContextFactoryImpl");
        setIfUnset("org.ccsds.moims.mo.mal.transport.default.protocol", "maltcp://");
        setIfUnset("org.ccsds.moims.mo.mal.transport.protocol.maltcp",
                "esa.mo.mal.transport.tcpip.TCPIPTransportFactoryImpl");
        setIfUnset("org.ccsds.moims.mo.mal.encoding.protocol.maltcp",
                "esa.mo.mal.encoder.binary.fixed.FixedBinaryStreamFactory");
        setIfUnset("org.ccsds.moims.mo.mal.transport.tcpip.autohost", "true");
        setIfUnset("helpertools.configurations.MOappName", "constellation-directory");
        setIfUnset("helpertools.configurations.OrganizationName", "esa");
        setIfUnset("helpertools.configurations.MissionName", "constellation");
        setIfUnset("helpertools.configurations.NetworkZone", "Ground");
        setIfUnset("helpertools.configurations.DeviceName", "Workstation");

        // Keep the COM Archive in memory. This service rebuilds what it knows
        // from the segments every time it starts, so there is nothing worth
        // keeping on disk. Without this, the archive would be written as a
        // file in whatever directory the service was started from.
        setIfUnset("esa.nmf.archive.persistence.jdbc.url", "jdbc:sqlite::memory:");

        // A provider.properties beside whoever started this still has the last
        // word, so a deployment can say something else.
        HelperMisc.loadPropertiesFile();
    }

    /**
     * Sets a property only where nothing has set it already.
     *
     * @param key The property.
     * @param value The value to set when the property is not set.
     */
    private static void setIfUnset(final String key, final String value) {
        if (System.getProperty(key) == null) {
            System.setProperty(key, value);
        }
    }

    /**
     * Starts the Directory service and publishes what the nodes report.
     * <p>
     * Nodes that do not answer are queried again every few seconds until they
     * do, so the order in which the constellation and this service are started
     * does not matter.
     *
     * @param nodes The Directory service of each node of the constellation.
     * @throws MALException if the Directory service could not be started.
     */
    public synchronized void init(final List<URI> nodes) throws MALException {
        localCOMServices.init();
        directoryService.init(localCOMServices);

        // Register this service too, so a consumer can see what is answering.
        directoryService.loadURIs(NAME, NMFProviderType.PROXY);

        if (nodes != null) {
            pending.addAll(nodes);
        }

        LOGGER.log(Level.INFO, "The constellation has {0} node(s). Asking each what it is...",
                pending.size());
        askPendingNodes();

        if (!pending.isEmpty()) {
            timer = new Timer("ConstellationDirectoryTimer", true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (ConstellationDirectory.this) {
                        askPendingNodes();

                        if (pending.isEmpty()) {
                            cancel();
                        }
                    }
                }
            }, RETRY_PERIOD, RETRY_PERIOD);
        }

        LOGGER.log(Level.INFO, "The Directory service of the constellation is at: {0}",
                getDirectoryServiceURI());
    }

    /**
     * Queries every node that has not answered yet, and publishes those that do.
     */
    private void askPendingNodes() {
        for (URI node : new ArrayList<>(pending)) {
            int published = publishSupervisorOf(node);

            if (published > 0) {
                pending.remove(node);
            }
        }
    }

    /**
     * Queries one node and publishes its Supervisor.
     *
     * @param node The Directory service of the node.
     * @return How many providers of that node were published.
     */
    private int publishSupervisorOf(final URI node) {
        ProviderList providers;

        try {
            providers = NMFConsumer.retrieveProvidersFromDirectory(node);
        } catch (Exception ex) {
            // The node is either still starting up or not running at all.
            // Either way it will be queried again, so this is logged as a
            // detail: every node reports this until the constellation is ready.
            LOGGER.log(Level.FINE, "This node has not answered yet: " + node, ex);
            return 0;
        }

        int published = 0;

        for (Provider provider : providers) {
            if (!NMFProviderType.SUPERVISOR.equals(provider.getProviderType())) {
                continue;
            }

            try {
                // Published exactly as the node provided it, addresses
                // included, so a consumer talks to the spacecraft directly.
                directoryService.add(new Provider(null,
                        nameOf(provider),
                        provider.getDomain(),
                        provider.getServiceCapabilities(),
                        provider.getProviderAddresses(),
                        provider.getProviderType()), null);
                published++;

                LOGGER.log(Level.INFO, "Added the node {0} of the constellation, at {1}",
                        new Object[]{provider.getDomain(), node});
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "This node could not be added: " + node, ex);
            }
        }

        if (published == 0 && !providers.isEmpty()) {
            LOGGER.log(Level.WARNING, "The node at {0} answered with {1} provider(s), none of "
                    + "them a Supervisor. Each node of a constellation is a spacecraft, and a "
                    + "spacecraft is reached through its Supervisor.",
                    new Object[]{node, providers.size()});
        }
        return published;
    }

    /**
     * The name a node is published under.
     * <p>
     * Every Supervisor in a constellation reports the same name, because they
     * all run the same software. What distinguishes them is the domain, which
     * contains the node number. A consumer selecting a provider by name would
     * otherwise see the same name for the whole constellation, so the node
     * number is added to the name.
     *
     * @param provider The provider as reported by its node.
     * @return The name to publish it under.
     */
    private static Identifier nameOf(final Provider provider) {
        IdentifierList domain = provider.getDomain();

        // A spacecraft in a fleet carries its node number in the domain,
        // between the mission name and the provider name, as in
        // esa.simulator-lite.3.nanosat-mo-supervisor. A single-spacecraft
        // mission has no node number there, and nothing to distinguish.
        if (domain == null || domain.size() < 4) {
            return provider.getProviderName();
        }
        return new Identifier(provider.getProviderName().getValue() + "-"
                + domain.get(domain.size() - 2).getValue());
    }

    /**
     * @return The address a consumer connects to in order to find the
     * constellation.
     */
    public URI getDirectoryServiceURI() {
        return directoryService.getConnection().getConnectionDetails().getProviderURI();
    }

    /**
     * @return The nodes that have not answered yet.
     */
    public synchronized List<URI> getPendingNodes() {
        return new ArrayList<>(pending);
    }

    /**
     * Main command line entry point.
     *
     * @param args The address of the Directory service of each node of the
     * constellation, as printed by the Constellation Management Tool.
     * @throws Exception if the Directory service could not be started.
     */
    public static void main(final String args[]) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: pass the Directory service address of every node "
                    + "of the constellation:");
            System.out.println("    maltcp://172.28.0.1:1024/nanosat-mo-supervisor-Directory "
                    + "maltcp://172.28.0.2:1024/nanosat-mo-supervisor-Directory ...");
            System.out.println();
            System.out.println("These are the addresses printed by the Constellation Management "
                    + "Tool when it raises the constellation.");
            return;
        }

        List<URI> nodes = new ArrayList<>();

        for (String arg : Arrays.asList(args)) {
            nodes.add(new URI(arg));
        }

        ConstellationDirectory constellation = new ConstellationDirectory();
        constellation.init(nodes);
    }
}
