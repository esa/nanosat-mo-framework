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
 * A consumer that wants to reach a constellation has otherwise to be given the
 * address of every spacecraft in it, one at a time. This stands in front of
 * them and answers for all of them: it is given the Directory service of each
 * node once, asks each what it is, and publishes them together in a Directory
 * service of its own. A consumer connects to this one address and finds the
 * constellation.
 * <p>
 * What it publishes is the <em>Supervisor</em> of each node and nothing else.
 * The apps of a node are the node's own business, and are found through the
 * node once it has been chosen; gathered here they would bury the spacecraft
 * among their apps, which is the thing a constellation most needs to show.
 * <p>
 * The addresses published are the ones the nodes give, so a consumer that picks
 * a node talks to that spacecraft directly. Nothing is routed through here: the
 * traffic of a constellation has no reason to pass through one ground process,
 * and this one is not in the way of it.
 * <p>
 * This is not {@link GroundMOProxy}, and does not replace it. That one serves a
 * single spacecraft and does a great deal more, bridging protocols, mirroring
 * the COM Archive and re-routing the Action service of every app. What is
 * wanted here is the opposite: one thing that says what spacecraft there are.
 *
 * @author Cesar Coelho
 */
public class ConstellationDirectory {

    private static final Logger LOGGER = Logger.getLogger(ConstellationDirectory.class.getName());

    /**
     * The name this registers itself under in its own Directory service, so
     * that a consumer can tell what it is looking at.
     */
    public static final String NAME = "Constellation Directory";

    /**
     * How often a node that has not answered yet is asked again, in
     * milliseconds.
     * <p>
     * The nodes of a constellation are started just before this is, and a
     * spacecraft takes a moment to have a Directory service worth asking. A
     * node that is not there when this starts is therefore not a node that is
     * missing, it is one that is still coming up.
     */
    private static final long RETRY_PERIOD = 5000;

    private final COMServicesProvider localCOMServices = new COMServicesProvider();

    private final DirectoryProxyServiceImpl directoryService = new DirectoryProxyServiceImpl();

    /**
     * The nodes not yet answered for. A node leaves this list once its
     * Supervisor has been published, and is not asked again.
     */
    private final Set<URI> pending = new LinkedHashSet<>();

    private Timer timer;

    /**
     * Reads the configuration, as every provider here does: which transport to
     * speak, and what to call itself.
     * <p>
     * What it needs to run at all is settled first, and only where nothing has
     * settled it already. This is started from wherever a constellation is
     * raised, which is not always a directory with a provider.properties in
     * it, and the transport it would fall back to without one is not the
     * transport a constellation is reached over.
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

        // A provider.properties beside whoever started this still has the last
        // word, so a deployment can say something else.
        HelperMisc.loadPropertiesFile();
    }

    /**
     * Sets a property only where nothing has set it already.
     *
     * @param key The property.
     * @param value What it is to be, where it is nothing.
     */
    private static void setIfUnset(final String key, final String value) {
        if (System.getProperty(key) == null) {
            System.setProperty(key, value);
        }
    }

    /**
     * Starts the Directory service and publishes what the nodes say they are.
     * <p>
     * Nodes that do not answer yet are asked again every few seconds until
     * they do, so that the order in which a constellation and this are started
     * does not matter.
     *
     * @param nodes The Directory service of each node of the constellation.
     * @throws MALException if the Directory service could not be started.
     */
    public synchronized void init(final List<URI> nodes) throws MALException {
        localCOMServices.init();
        directoryService.init(localCOMServices);

        // So that a consumer looking at this list can see what is answering it.
        directoryService.loadURIs(NAME, NMFProviderType.PROXY);

        if (nodes != null) {
            pending.addAll(nodes);
        }

        LOGGER.log(Level.INFO, "The constellation has {0} node(s). Asking each what it is...",
                pending.size());
        askThePending();

        if (!pending.isEmpty()) {
            timer = new Timer("ConstellationDirectoryTimer", true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (ConstellationDirectory.this) {
                        askThePending();

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
     * Asks every node that has not answered yet, and publishes those that do.
     */
    private void askThePending() {
        for (URI node : new ArrayList<>(pending)) {
            int published = publishSupervisorOf(node);

            if (published > 0) {
                pending.remove(node);
            }
        }
    }

    /**
     * Asks one node what it is, and publishes its Supervisor.
     *
     * @param node The Directory service of the node.
     * @return How many providers of that node were published.
     */
    private int publishSupervisorOf(final URI node) {
        ProviderList providers;

        try {
            providers = NMFConsumer.retrieveProvidersFromDirectory(node);
        } catch (Exception ex) {
            // Not there yet, or not there at all. Either way it is asked again;
            // said at the level of a detail, because a constellation starting
            // up says this of every node until it is ready.
            LOGGER.log(Level.FINE, "This node has not answered yet: " + node, ex);
            return 0;
        }

        int published = 0;

        for (Provider provider : providers) {
            if (!NMFProviderType.SUPERVISOR.equals(provider.getProviderType())) {
                continue;
            }

            try {
                // Published as the node gives it, addresses and all: a consumer
                // that picks this node talks to the spacecraft itself.
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
            LOGGER.log(Level.WARNING, "The node at {0} answered with {1} provider(s), none of them "
                    + "a Supervisor. A node of a constellation is a spacecraft, and a spacecraft "
                    + "is reached through its Supervisor.",
                    new Object[]{node, providers.size()});
        }
        return published;
    }

    /**
     * The name a node is published under.
     * <p>
     * Every Supervisor of a constellation calls itself the same thing, being
     * the same software: what tells them apart is the domain, which carries
     * the node of the spacecraft. A consumer that chooses a provider by name
     * would be offered one name for the whole constellation, so the node is
     * put into the name as well.
     *
     * @param provider The provider as its node gave it.
     * @return The name to publish it under.
     */
    private static Identifier nameOf(final Provider provider) {
        IdentifierList domain = provider.getDomain();

        // A spacecraft of a fleet has its node in the domain, between the
        // mission it belongs to and what it calls itself:
        // esa.simulator-lite.3.nanosat-mo-supervisor. A mission of one
        // spacecraft has no node there, and there is nothing to tell apart.
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
     * @param args The Directory service of each node of the constellation, one
     * for each, as the Constellation Management Tool writes them out.
     * @throws Exception if the Directory service could not be started.
     */
    public static void main(final String args[]) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: give the Directory service of every node of the "
                    + "constellation, one after another:");
            System.out.println("    maltcp://172.28.0.1:1024/nanosat-mo-supervisor-Directory "
                    + "maltcp://172.28.0.2:1024/nanosat-mo-supervisor-Directory ...");
            System.out.println();
            System.out.println("They are the addresses the Constellation Management Tool prints "
                    + "when it raises the constellation.");
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
