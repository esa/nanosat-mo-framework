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
import esa.mo.nmf.cmt.utils.NanoSat;
import esa.mo.nmf.cmt.utils.NanoSatSimulator;
import esa.mo.nmf.cmt.utils.SegmentImage;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
     * What the name of every simulated segment opens with, so that the
     * containers of a constellation are told at a glance from whatever else the
     * machine is running.
     */
    public static final String SEGMENT_PREFIX = "nmfsim-";

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
        try {
            for (int i = 0; i < size; i++) {
                int nodeNumber = nextSpacecraftNode();
                NanoSatSimulator nanoSat = new NanoSatSimulator(
                        segmentName(name) + "-" + nodeNumber, null, image, nodeNumber);
                nanoSat.run();
                this.constellation.add(nanoSat);
            }

            LOGGER.log(Level.INFO, "Successfully added nodes to constellation. ");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Failed to add nodes to constellation: ", ex);
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
        try {
            for (Map.Entry<String, String[]> config : nanoSatConfigurations.entrySet()) {
                String name = segmentName(config.getKey());
                String[] keplerElements = config.getValue();

                NanoSatSimulator nanoSat = new NanoSatSimulator(name, keplerElements, image,
                        nextSpacecraftNode());
                nanoSat.run();
                this.constellation.add(nanoSat);
            }

            int size = nanoSatConfigurations.size();
            LOGGER.log(Level.INFO, "Successfully added {0} nodes to the constellation!", size);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Failed to add nodes to constellation: ", ex);
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
