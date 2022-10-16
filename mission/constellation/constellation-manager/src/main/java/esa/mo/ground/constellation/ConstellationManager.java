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
    ConstellationManager.class.getName()
  );

  private ArrayList<NanoSatSimulator> constellation = new ArrayList<NanoSatSimulator>();

  /**
   * Initialiser Constructor.
   * This Class manages a constellation of NanoSatellites.
   */
  public ConstellationManager() {}

  /**
   * Initializes the constellation. Creates and runs the Docker containers.
   *
   * Very WIP!
   *
   * @param name Name of the constellation. Container naming scheme: <name>-node-<1...n>
   * @param size Constellation size
   */
  private void InitConstellation(String name, int size) {
    try {
      if (this.constellation.size() == 0) {
        for (int i = 1; i <= size; i++) {
          this.constellation.add(
              new NanoSatSimulator(name + "-node-" + i, LOGGER)
            );
          this.constellation.get(i - 1).run();
        }
      } else {
        LOGGER.log(Level.INFO, "Constellation has already been initialized. ");
      }

      LOGGER.log(Level.INFO, "Successfully initialized constellation. ");
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE, "Failed to initialize the constellation: ", ex);
    }
  }

  /**
   * Connects to the providers of the constellation.
   *
   * Very WIP!
   *
   */
  private void ConnectToProviders() {
    constellation.forEach(
      nanoSat -> {
        try {
          LOGGER.log(
            Level.INFO,
            "Connecting to " + nanoSat.getDirectoryServiceURIString()
          );
          nanoSat.connectToProviders();
        } catch (IOException ex) {
          LOGGER.log(Level.SEVERE, "Failed to connect to provider: ", ex);
        }
      }
    );
  }

  /**
   * Main command line entry point.
   *
   * @param args the command line arguments
   * @throws java.lang.Exception If there is an error
   */
  public static void main(final String[] args) throws Exception {
    String app = "benchmark";
    int size = 3;

    ConstellationManager demo = new ConstellationManager();

    demo.InitConstellation("demo", size);
    // pause to start benchmark app via CTT
    // TODO: start app
    System.in.read();

    demo.ConnectToProviders();
  }
}
