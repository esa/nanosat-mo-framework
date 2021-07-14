/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
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
package esa.mo.nmf.nanosatmosupervisor;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.nmfpackage.NMFPackagePMBackend;
import esa.mo.platform.impl.util.PlatformServicesConsumer;
import esa.mo.platform.impl.util.PlatformServicesProviderInterface;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;

/**
 * This is a specific implementation of the NMF supervisor which is currently being used by
 * the SDK and the OPS-SAT mission. Using the property "nmf.platform.impl" one can select
 * the class of the platform services implementation which shall be used by the supervisor. If no
 * such property is provided, it will use the simulated platform services by default.
 *
 * @author yannick
 */
public class NanosatMOSupervisorBasicImpl extends NanoSatMOSupervisor {
  private static final Logger LOGGER = Logger.getLogger(NanosatMOSupervisorBasicImpl.class.getName());
  private PlatformServicesProviderInterface platformServicesProvider;
  private ConnectionConsumer connectionConsumer;

  @Override
  public void initPlatformServices(COMServicesProvider comServices) {
    try {
      String platformProviderClass = System.getProperty("nmf.platform.impl", "esa.mo.platform.impl.util.PlatformServicesProviderSoftSim");
      try {
        platformServicesProvider
            = (PlatformServicesProviderInterface) Class.forName(platformProviderClass).newInstance();
        platformServicesProvider.init(comServices);
      } catch (NullPointerException | ClassNotFoundException | InstantiationException
          | IllegalAccessException ex) {
        LOGGER.log(Level.SEVERE,
            "Something went wrong when initializing the platform services.",
            ex);
        System.exit(-1);
      }
    } catch (MALException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
    }
    // Now connect the platform services consumer loopback to it
    connectionConsumer = new ConnectionConsumer();
    try {
      connectionConsumer.loadURIs();
      super.getPlatformServices().init(connectionConsumer, null);
    } catch (MalformedURLException | NMFException | FileNotFoundException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
    }
  }

  @Override
  protected void startStatusTracking() {
    platformServicesProvider.startStatusTracking(connectionConsumer);
  }

  @Override
  public void init(MonitorAndControlNMFAdapter mcAdapter) {
    init(mcAdapter, new PlatformServicesConsumer(), new NMFPackagePMBackend("packages"));
  }

  /**
   * Main command line entry point.
   *
   * @param args the command line arguments
   * @throws java.lang.Exception If there is an error
   */
  public static void main(final String args[]) throws Exception {
    NanosatMOSupervisorBasicImpl supervisor = new NanosatMOSupervisorBasicImpl();
    MCSupervisorBasicAdapter adapter = new MCSupervisorBasicAdapter();
    adapter.setNmfSupervisor(supervisor);
    supervisor.init(adapter);
    adapter.startAdcsAttitudeMonitoring();
  }

}
