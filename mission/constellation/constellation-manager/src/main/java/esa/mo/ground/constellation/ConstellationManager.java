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
package esa.mo.ground.constellation;

import esa.mo.ground.constellation.gui.ConstellationManagerGui;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ground consumer: Constellation Manager
 * An orchestration tool for a constellation of NanoSatellites.
 *
 */
public class ConstellationManager {
  private final Logger LOGGER = Logger.getLogger(
      ConstellationManager.class.getName());

  private final ArrayList<NanoSat> constellation = new ArrayList<NanoSat>();
  private final ConstellationManagerGui ncmGui;

  /**
   * Initializer Constructor.
   * This Class manages a constellation of NanoSatellites.
   */
  public ConstellationManager() {

    try {
      // Set cross-platform Java L&F (also called "Metal")
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    } catch (Exception ex) {
    }

    this.ncmGui = new ConstellationManagerGui(this);
  }

  /**
   * Initializes the constellation. Creates and runs the Docker containers.
   *
   * Very WIP!
   *
   * @param name Name of the constellation. Container naming scheme:
   *             <name>-node-<1...n>
   * @param size Constellation size
   */
  public void addNodesToConstellation(String name, int size) {
    try {
      for (int i = 1; i <= size; i++) {
        this.constellation.add(new NanoSatSimulator(name + "-node-" + i));
        this.constellation.get(this.constellation.size() - 1).run();
      }

      LOGGER.log(Level.INFO, "Successfully added nodes to constellation. ");
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE, "Failed to add nodes to constellation: ", ex);
      if (ex.toString().contains("permission denied")) {
        JOptionPane.showMessageDialog(null,
            "Failed to initialize the constellation: Do you have permission to use Docker?",
            "Error", JOptionPane.INFORMATION_MESSAGE);
      }
    }
    this.ncmGui.refreshNanoSatSegmentList();
  }

  /**
   * Connect to NanoSat segment nodes of an existing constellation.
   *
   * @param file connection string CSV for NanoSat segments
   */
  public void connectToConstellation(String file) {
    // TODO: implement logic
  }

  /**
   * Connects to the providers of the constellation.
   *
   */
  public void connectToProviders() {
    constellation.forEach(
        nanoSat -> {
          try {
            LOGGER.log(
                Level.INFO,
                "Connecting to " + nanoSat.getDirectoryServiceURIString());
            nanoSat.connectToProviders();
          } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Failed to connect to provider: ", ex);
          }
        });
  }

  /**
   * Install a NMF package on all the nodes of the constellation.
   * 
   * @param packageName Name of the NMF package
   */
  public void installPackageOnAllNodes(String packageName) {
    constellation.forEach(
        nanoSat -> {
          nanoSat.installPackage(packageName);
        });
  }

  /**
   * Gets the Constellation
   * 
   * @return constellation
   */
  public ArrayList<NanoSat> getConstellation() {
    return this.constellation;
  }

  /**
   * Main command line entry point.
   *
   * @param args the command line arguments
   * @throws Exception If there is an error
   */
  public static void main(final String[] args) {

    ConstellationManager ncm = new ConstellationManager();

  }

}