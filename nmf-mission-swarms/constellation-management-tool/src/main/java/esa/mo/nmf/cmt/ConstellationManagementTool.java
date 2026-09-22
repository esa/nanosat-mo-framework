/* ----------------------------------------------------------------------------
 * Copyright (C) 2022      European Space Agency
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
 *
 * Author: N Wiegand (https://github.com/Klabau)
 */
package esa.mo.nmf.cmt;

import esa.mo.nmf.cmt.cli.ConstellationCli;
import esa.mo.nmf.cmt.gui.ConstellationManagerGui;
import esa.mo.nmf.cmt.utils.ContainerApi;
import esa.mo.nmf.cmt.utils.DockerApi;
import esa.mo.nmf.cmt.utils.NanoSat;
import esa.mo.nmf.cmt.utils.NanoSatSimulator;
import esa.mo.nmf.cmt.utils.SegmentImage;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ground consumer: Constellation Manager An orchestration tool for a
 * constellation of NanoSatellites.
 */
public class ConstellationManagementTool {

    private final Logger LOGGER = Logger.getLogger(ConstellationManagementTool.class.getName());

    private final ArrayList<NanoSat> constellation = new ArrayList<NanoSat>();

    /**
     * The highest node handed out to a spacecraft of this constellation. It only
     * ever counts up, so that a node is not given to two of them.
     */
    private int lastSpacecraftNode = 0;

    /**
     * Told when segments are added, or null when nobody is watching, which is
     * the case when the constellation is run from the command line.
     */
    private ConstellationListener listener;

    /**
     * Main command line entry point.
     * <p>
     * Arguments raise a constellation from the command line and none opens the
     * window, so that the tool serves a workstation and a headless machine
     * alike.
     *
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        if (args.length != 0) {
            System.exit(ConstellationCli.run(args));
        }

        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("The window cannot be opened on a machine without a display. "
                    + "Run the constellation from the command line instead: --help");
            System.exit(ConstellationCli.EXIT_USAGE);
        }

        try {
            checkNoConstellationIsRunning();
        } catch (IllegalStateException ex) {
            ConstellationManagerGui.refuseToStart(ex.getMessage());
            return;
        }

        ConstellationManagementTool cmt = new ConstellationManagementTool();
        cmt.startGui();
    }

    public void startGui() {
        ConstellationManagerGui gui = new ConstellationManagerGui(this);
        this.setListener(gui);
    }

    /**
     * Sets who is told that the segments of the constellation have changed.
     *
     * @param listener The listener, or null for nobody.
     */
    public void setListener(ConstellationListener listener) {
        this.listener = listener;
    }

    /**
     * Tells the listener, if there is one, that the segments have changed.
     */
    private void constellationChanged() {
        if (this.listener != null) {
            this.listener.constellationChanged();
        }
    }

    /**
     * How long it has been since a moment, in seconds, to the millisecond.
     * <p>
     * Written with a dot for a decimal point whatever the machine is set to,
     * so that a time reads the same wherever it is reported.
     *
     * @param startedAt The moment, as given by {@link System#nanoTime()}.
     * @return The seconds since, as text.
     */
    private static String secondsSince(long startedAt) {
        double seconds = (System.nanoTime() - startedAt) / 1_000_000_000.0;
        return String.format(Locale.ROOT, "%.3f", seconds);
    }

    private String directoryServiceURI;

    private boolean removalRegistered;

    /**
     * Arranges for the constellation to be removed, and what became of it said,
     * whenever the tool ends.
     * <p>
     * Every segment takes itself down through a hook of its own and the
     * Directory service needs the same, or closing the window leaves it running
     * and the next constellation is refused for it. This is registered before
     * the first segment is raised rather than after the last, so that a tool
     * interrupted while it is still raising the constellation also says what
     * became of it.
     */
    private synchronized void removeConstellationWhenTheToolEnds() {
        if (removalRegistered) {
            return;
        }

        try {
            Runtime.getRuntime().addShutdownHook(new Thread(this::removeConstellationAndReport));
            removalRegistered = true;
        } catch (IllegalStateException ex) {
            // Asked to end before it even began. The segments raised from here
            // are taken down by the loop that raises them.
        }
    }

    /**
     * Whether the tool has been asked to end.
     * <p>
     * A shutdown hook runs on a thread of its own while the thread that raises
     * the constellation carries on, so an interrupted tool would otherwise go
     * on raising segments that the hooks of the segments before them have
     * already been and gone for.
     */
    private static volatile boolean shuttingDown;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shuttingDown = true));
    }

    /**
     * @return Whether the tool has been asked to end.
     */
    public static boolean isShuttingDown() {
        return shuttingDown;
    }

    /**
     * @return The one address the whole constellation is reached at, or null
     * where there is no constellation or it could not be answered for.
     */
    public String getDirectoryServiceURI() {
        return directoryServiceURI;
    }

    /**
     * Raises the one Directory service that answers for the whole
     * constellation, so that it is reached at one address rather than at one
     * address per segment.
     * <p>
     * A constellation that could not be answered for is still a constellation:
     * every segment is up and reachable at its own address, so a failure here
     * is reported and passed over rather than taken as a failure to raise it.
     */
    private void raiseDirectoryService() {
        if (shuttingDown) {
            return;
        }

        List<String> nodes = new ArrayList<>();

        for (NanoSat nanoSat : this.constellation) {
            try {
                nodes.add(nanoSat.getDirectoryServiceURIString());
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "The address of this segment could not be read, so it "
                        + "will not be in the Directory service of the constellation: {0}",
                        nanoSat.getName());
            }
        }

        if (nodes.isEmpty()) {
            return;
        }

        try {
            directoryServiceURI = DockerApi.runConstellationDirectory(DIRECTORY_NAME, nodes);
            LOGGER.log(Level.INFO, "The Directory service of the constellation was started!"
                    + "\n  >> Container: {0}"
                    + "\n  >> Logs available with: docker logs {0}\n", DIRECTORY_NAME);
            LOGGER.log(Level.INFO, "Directory URI: {0}", directoryServiceURI);

        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "The Directory service of the "
                    + "constellation could not be started!\n{0}", ex.getMessage());
        }
    }

    /**
     * Removes the Directory service that answers for the constellation. It
     * exists to serve the segments, so it goes when they do.
     */
    public void removeDirectoryService() {
        directoryServiceURI = null;

        try {
            DockerApi.removeConstellationDirectory(DIRECTORY_NAME);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "The Directory service of the constellation could not be "
                    + "removed. Its container is still running and has to be removed by hand: "
                    + "docker rm -f {0}\n{1}", new Object[]{DIRECTORY_NAME, ex.getMessage()});
        }
    }

    /**
     * How long the containers of the constellation are given to go, in
     * milliseconds, before what is left of them is reported.
     */
    private static final long REMOVAL_TIMEOUT = 15000;

    /**
     * Removes the constellation and says what became of it.
     * <p>
     * Every segment also takes itself down through a hook of its own, and the
     * hooks of a shutdown run at the same time as each other, so none of them
     * is in a position to say that the constellation is gone. This one removes
     * what is there and then asks the container tool what is left, so that
     * whoever interrupted the tool is told the answer rather than left to
     * wonder whether the segments are still running.
     * <p>
     * It is written out rather than logged. The logger keeps a shutdown hook of
     * its own which closes its handlers, and it runs alongside this one, so a
     * record logged here is as likely to be thrown away as printed.
     */
    private void removeConstellationAndReport() {
        System.out.println("Removing the constellation...");
        this.removeAllSimulations();

        List<String> left;
        long giveUpAt = System.currentTimeMillis() + REMOVAL_TIMEOUT;

        // The hooks of the segments are removing the same containers, so what
        // is still listed may be on its way out already.
        while (true) {
            try {
                left = ContainerApi.existingSegments();
            } catch (IOException | UnsupportedOperationException ex) {
                System.out.println("The constellation was removed, but the container tool could "
                        + "not be asked what is left of it: " + ex.getMessage());
                return;
            }

            if (left.isEmpty() || System.currentTimeMillis() > giveUpAt) {
                break;
            }

            try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (left.isEmpty()) {
            System.out.println("The constellation was removed. No container of it is left "
                    + "running.");
        } else {
            System.out.println(left.size() + " container(s) of the constellation are still "
                    + "there: " + String.join(", ", left));
            System.out.println("Remove them with:");
            System.out.println("    docker rm -f $(docker ps -aq --filter name=^"
                    + SEGMENT_PREFIX + ")");
        }
    }

    /**
     * Tells the listener, if there is one, that a segment has been raised.
     *
     * @param nanoSat The segment that was just raised.
     */
    private void segmentRaised(NanoSat nanoSat) {
        if (this.listener != null) {
            this.listener.segmentRaised(nanoSat);
        }
    }

    /**
     * What the name of every simulated segment opens with, so that the
     * containers of a constellation are told at a glance from whatever else the
     * machine is running.
     */
    public static final String SEGMENT_PREFIX = "nmfsim-";

    /**
     * The container that answers for the whole constellation, named so that it
     * is recognisable beside the segments and removed with them.
     */
    public static final String DIRECTORY_NAME = SEGMENT_PREFIX + "constellation-directory";

    /**
     * Returns the name a simulated segment carries.
     * <p>
     * A segment is named after what it was asked to be called, reduced to the
     * letters and digits of it, because the name reaches a container tool that
     * accepts little else.
     *
     * @param name The name it was asked to be called.
     * @return The name it carries.
     * @throws IllegalArgumentException if nothing of the name is left.
     */
    public static String segmentName(String name) {
        String reduced = name.replaceAll("[^a-zA-Z0-9]+", "");

        if (reduced.isEmpty()) {
            throw new IllegalArgumentException("The name of the segment has no letter or digit "
                    + "in it: " + name);
        }
        return SEGMENT_PREFIX + reduced;
    }

    /**
     * Makes sure this machine is not already holding a constellation.
     * <p>
     * One machine holds one: the segments of a constellation are numbered from
     * one and are addressed by that number, so a second raised beside the first
     * would be asking for addresses the first already has. Docker refuses them,
     * and what would be left is half a constellation that nobody asked for.
     * <p>
     * A machine whose container tool cannot be asked is taken to hold nothing:
     * the tool then fails where it always did, at raising the segments, rather
     * than refusing to raise any because Docker did not answer.
     *
     * @throws IllegalStateException if a constellation is already there. The
     * message names its segments and says how to be rid of them.
     */
    public static void checkNoConstellationIsRunning() {
        List<String> segments;

        try {
            segments = ContainerApi.existingSegments();
        } catch (IOException | UnsupportedOperationException ex) {
            Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.FINE,
                    "The container tool could not be asked what it holds: ", ex);
            return;
        }

        if (segments.isEmpty()) {
            return;
        }

        throw new IllegalStateException("A constellation of " + segments.size()
                + " segment(s) is already running. A machine holds one at a time.\n"
                + "Stop it first: interrupt the command holding it, or run:\n"
                + "    docker rm -f $(docker ps -aq --filter name=^" + SEGMENT_PREFIX + ")");
    }

    /**
     * Returns a NanoSat Object by its name.
     *
     * @param name NanoSat Name
     * @return NanoSat Object
     */
    public NanoSat getNanoSatSegmentByName(String name) {
        for (NanoSat nanoSat : constellation) {
            if (nanoSat.getName().equals(name)) {
                return nanoSat;
            }
        }
        return null;
    }

    /**
     * Check if a given NanoSat segment name already exists in the
     * constellation.
     *
     * @param name the name to check for uniqueness
     * @return true: name is unique, false: name already exists
     */
    public boolean isNanoSatSegmentNameUnique(String name) {
        return (getNanoSatSegmentByName(name) == null);
    }

    /**
     * Removes all simulated NanoSat Segments.
     */
    public void removeAllSimulations() {
        this.removeDirectoryService();
        this.constellation.forEach(nanoSat -> {
            if (nanoSat.isActive()) {
                Logger.getLogger(ConstellationManagementTool.class.getName()).log(
                        Level.INFO, "Removing node {0}...", new Object[]{nanoSat.getName()});
                nanoSat.deleteIfSimulation();
            }
        });
    }

    /**
     * Initialize the constellation. Creates and runs the Docker containers.
     *
     * @param name Name of the constellation. Container naming scheme:
     * <name>-sim-<1...n>
     * @param size Constellation size
     * @param image The image every segment of this constellation runs
     * @throws java.io.IOException if the simulation could not be started.
     */
    public void addBasicSimulations(String name, int size, SegmentImage image) throws IOException {
        long startedAt = System.nanoTime();
        this.removeConstellationWhenTheToolEnds();

        try {
            for (int i = 0; i < size; i++) {
                if (shuttingDown) {
                    break;
                }

                int nodeNumber = nextSpacecraftNode();
                NanoSatSimulator nanoSat = new NanoSatSimulator(
                        segmentName(name) + "-" + nodeNumber, null, image, nodeNumber);

                try {
                    nanoSat.run();
                } catch (IOException ex) {
                    // An interrupt from a terminal reaches every process of the
                    // group, the container tool among them, so the segment
                    // being raised at that moment dies of the same key that
                    // stopped this. It is the end of the constellation, not a
                    // failure to raise it.
                    if (shuttingDown) {
                        break;
                    }
                    throw ex;
                }

                // The tool may have been asked to end while this one was being
                // raised, in which case the hook that would have taken it down
                // has already run. It is taken down here instead.
                if (shuttingDown) {
                    nanoSat.deleteIfSimulation();
                    break;
                }

                this.constellation.add(nanoSat);
                this.segmentRaised(nanoSat);
            }

            LOGGER.log(Level.INFO, "Successfully added nodes to constellation in: {0} seconds",
                    secondsSince(startedAt));
            this.raiseDirectoryService();
        } catch (IOException ex) {
            if (!shuttingDown) {
                LOGGER.log(Level.SEVERE, "Failed to add nodes to constellation: ", ex);
            }
            throw ex;
        } finally {
            // The segments that were raised before the failure are part of the
            // constellation, so they are shown whether the rest arrived or not.
            this.constellationChanged();
        }
    }

    /**
     * Add NanoSat segments to the constellation and configure orbit dynamics
     * based on kepler elements.
     *
     * @param nanoSatConfigurations string: NanoSat Segment name, string[]:
     * kepler elements
     * @param image The image every segment of this constellation runs
     * @throws java.io.IOException if a segment could not be started.
     */
    public void addSimulationsWithOrbits(Map<String, String[]> nanoSatConfigurations,
            SegmentImage image) throws IOException {
        long startedAt = System.nanoTime();
        this.removeConstellationWhenTheToolEnds();

        try {
            for (Map.Entry<String, String[]> config : nanoSatConfigurations.entrySet()) {
                String name = segmentName(config.getKey());
                String[] keplerElements = config.getValue();

                if (shuttingDown) {
                    break;
                }

                NanoSatSimulator nanoSat = new NanoSatSimulator(name, keplerElements, image,
                        nextSpacecraftNode());

                try {
                    nanoSat.run();
                } catch (IOException ex) {
                    if (shuttingDown) {
                        break;
                    }
                    throw ex;
                }

                if (shuttingDown) {
                    nanoSat.deleteIfSimulation();
                    break;
                }

                this.constellation.add(nanoSat);
                this.segmentRaised(nanoSat);
            }

            int size = nanoSatConfigurations.size();
            LOGGER.log(Level.INFO, "Successfully added {0} nodes to the constellation in: "
                    + "{1} seconds", new Object[]{size, secondsSince(startedAt)});
            this.raiseDirectoryService();
        } catch (IOException ex) {
            if (!shuttingDown) {
                LOGGER.log(Level.SEVERE, "Failed to add nodes to constellation: ", ex);
            }
            throw ex;
        } finally {
            this.constellationChanged();
        }
    }

    /**
     * Connect to NanoSat segment nodes of an existing constellation.
     *
     * @param nanoSatSegmentConnections connection string CSV for NanoSat
     * segments
     */
    public void connectToNanoSatSegments(HashMap<String, String> nanoSatSegmentConnections) {
        try {
            for (Map.Entry<String, String> segment : nanoSatSegmentConnections.entrySet()) {
                String name = segment.getKey();
                String ipAddress = segment.getValue();

                NanoSat nanoSat = new NanoSat(name, ipAddress);
                nanoSat.connectToNanoSat();
                this.constellation.add(nanoSat);
            }

            int size = nanoSatSegmentConnections.size();
            LOGGER.log(Level.INFO, "Successfully added {0} nodes to the constellation!", size);
        } finally {
            this.constellationChanged();
        }
    }

    /**
     * Connects to the service providers on all the constellation's NanoSat
     * segments.
     */
    public void connectToConstellationProviders() {
        this.constellation.forEach(NanoSat::connectToNanoSat);
    }

    /**
     * Gets the Constellation
     *
     * @return constellation
     */
    /**
     * Returns the node of the next spacecraft of this constellation.
     * <p>
     * The segments are the units of one mission, told apart by their node, so a
     * node is never handed out twice: it counts up from the highest issued so
     * far rather than from the number of segments there are now, which would
     * repeat a node once one of them is removed.
     * <p>
     * The nodes are handed out in order, so the same file always describes the
     * same constellation.
     *
     * @return The node of the next spacecraft.
     */
    private synchronized int nextSpacecraftNode() {
        return ++lastSpacecraftNode;
    }

    public ArrayList<NanoSat> getConstellation() {
        return this.constellation;
    }

}
