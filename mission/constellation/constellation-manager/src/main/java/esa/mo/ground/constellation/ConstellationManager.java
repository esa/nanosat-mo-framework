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

import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import esa.mo.nmf.commonmoadapter.SimpleDataReceivedListener;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ground consumer: Constellation Manager
 * An orchestration tool for a constellation of NanoSatellites.
 * 
 */
public class ConstellationManager
{

  private static final String APP_PREFIX = "App: ";
  private final Logger LOGGER = Logger.getLogger(ConstellationManager.class.getName());

  private NanoSatSimulator nanoSatSimulator = null;

  /**
   * Initialiser Constructor. 
   * This Class manages a constellation of NanoSatellites.
   */
  public ConstellationManager()
  {
  }

  /**
   * Initializes the constellation. Creates and runs the Docker containers.
   * 
   * Very WIP!
   * 
   * @param name Name of the constellation. Container naming scheme: <name>-node-<1...n>
   */
  private void InitConstellation(String name) 
  {
   
    try {

      if (this.nanoSatSimulator == null) {
        this.nanoSatSimulator = new NanoSatSimulator(name + "-node-01");
      }

      this.nanoSatSimulator.run();

      LOGGER.log(Level.INFO, "Initialized Container " + this.nanoSatSimulator.getName());
    }
    catch (IOException ex) {
      LOGGER.log(Level.SEVERE, "Failed to run container.", ex);
    }
  }

  /**
   * Connects to the providers of the constellation.
   * 
   * Very WIP!
   * 
   * @param providerName
   */
  private void ConnectToProvider(String providerName) 
  {
    try {

      LOGGER.log(Level.INFO, "Connecting to " + this.nanoSatSimulator.getDirectoryServiceURIString());

      ProviderSummaryList providers = GroundMOAdapterImpl
        .retrieveProvidersFromDirectory(this.nanoSatSimulator.getDirectoryServiceURI());

      GroundMOAdapterImpl gma = null;
      if (!providers.isEmpty()) {
        for (ProviderSummary provider : providers) {
          if (provider.getProviderName().toString().equals(APP_PREFIX + providerName)) {
            gma = new GroundMOAdapterImpl(provider);
            gma.addDataReceivedListener(new DataReceivedAdapter());
            break;
          }
        }
      }

      if(gma == null)
      {
        LOGGER.log(Level.SEVERE, "Failed to connect to the provider. No such provider found - " + providerName);
      } else {
        LOGGER.log(Level.INFO, "Successfully connected to " + providerName);
      }
    } catch ( MALException | MALInteractionException | IOException ex) {
      LOGGER.log(Level.SEVERE, "Failed to connect to the provider. ", ex);
    }
  }


  /**
   * Main command line entry point.
   *
   * @param args the command line arguments
   * @throws java.lang.Exception If there is an error
   */
  public static void main(final String[] args) throws Exception
  {

    String app = "benchmark";

    ConstellationManager demo = new ConstellationManager();

    demo.InitConstellation("testconstellation");
    // pause to start benchmark app via CTT
    // TODO: start app
    System.in.read();
    
    demo.ConnectToProvider(app);    
  }

  
  class DataReceivedAdapter extends SimpleDataReceivedListener
  {

    @Override
    public void onDataReceived(String parameterName, Serializable data)
    {
      LOGGER.log(Level.INFO,
          "\nParameter name: {0}" + "\n" + "Data content:\n{1}",
          new Object[]{parameterName, data.toString()});
    }
  }
}
