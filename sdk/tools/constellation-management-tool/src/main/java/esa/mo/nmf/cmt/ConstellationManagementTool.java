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

import esa.mo.nmf.cmt.gui.ConstellationManagerGui;
import esa.mo.nmf.cmt.utils.NanoSat;
import esa.mo.nmf.cmt.utils.NanoSatSimulator;
import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
    private ConstellationManagerGui cmtGui;

    /**
     * Initializer Constructor. This Class manages a constellation of
     * NanoSatellites.
     */
    public ConstellationManagementTool() {
        try {
            // Set cross-platform Java L&F (also called "Metal")
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to start the Constellation Management Tool: ", ex);
        }
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws Exception If there is an error
     */
    public static void main(final String[] args) {
        ConstellationManagementTool cmt = new ConstellationManagementTool();
        cmt.startGui();
    }

    public void startGui() {
        this.cmtGui = new ConstellationManagerGui(this);
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
    public boolean isNaNoSatSegmentNameUnique(String name) {
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
     * @throws java.io.IOException if the simulation could not be started.
     */
    public void addBasicSimulations(String name, int size) throws IOException {
        try {
            for (int i = 1; i <= size; i++) {
                int nodeNumber = this.constellation.size() + 1;
                NanoSatSimulator nanoSat = new NanoSatSimulator("nmfsim-" + name + "-" + nodeNumber);
                nanoSat.run();
                this.constellation.add(nanoSat);
            }

            LOGGER.log(Level.INFO, "Successfully added nodes to constellation. ");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Failed to add nodes to constellation: ", ex);
            if (ex.toString().contains("permission denied")) {
                JOptionPane.showMessageDialog(null,
                        "Failed to initialize the constellation: Do you have permission to use Docker?",
                        "Error", JOptionPane.INFORMATION_MESSAGE);
            }
            throw ex;
        }
        this.cmtGui.refreshNanoSatSegmentList();
    }

    /**
     * Add NanoSat segments to the constellation and configure orbit dynamics
     * based on kepler elements.
     *
     * @param nanoSatConfigurations string: NanoSat Segment name, string[]:
     * kepler elements
     */
    public void addAdvancedSimulations(HashMap<String, String[]> nanoSatConfigurations) {
        try {
            for (Map.Entry<String, String[]> config : nanoSatConfigurations.entrySet()) {
                String name = config.getKey();
                String[] keplerElements = config.getValue();

                NanoSatSimulator nanoSat = new NanoSatSimulator(name, keplerElements);
                nanoSat.run();
                this.constellation.add(nanoSat);
            }

            int size = nanoSatConfigurations.size();

            JOptionPane.showMessageDialog(null,
                    "Successfully added " + size + " nodes to the constellation!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            LOGGER.log(Level.INFO, "Successfully added {0} nodes to the constellation!", size);

            this.cmtGui.refreshNanoSatSegmentList();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to add nodes to constellation: ", ex);
            JOptionPane.showMessageDialog(null,
                    "Failed to add nodes to the constellation: " + ex,
                    "Error", JOptionPane.INFORMATION_MESSAGE);
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

                NanoSat nanoSat = new NanoSat(name);
                nanoSat.setIPAddress(ipAddress);
                nanoSat.connectToProviders();
                this.constellation.add(nanoSat);
            }

            int size = nanoSatSegmentConnections.size();

            JOptionPane.showMessageDialog(null,
                    "Successfully added " + size + " nodes to the constellation!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            LOGGER.log(Level.INFO, "Successfully added {0} nodes to the constellation!", size);

            this.cmtGui.refreshNanoSatSegmentList();

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to add nodes to constellation: ", ex);
            JOptionPane.showMessageDialog(null,
                    "Failed to add nodes to the constellation: " + ex,
                    "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Connects to the service providers on all the constellation's NanoSat
     * segments.
     */
    public void connectToConstellationProviders() {
        this.constellation.forEach(NanoSat::connectToProviders);
    }

    /**
     * Gets the Constellation
     *
     * @return constellation
     */
    public ArrayList<NanoSat> getConstellation() {
        return this.constellation;
    }

}
