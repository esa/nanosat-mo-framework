/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package esa.mo.nmf.nanosatmosupervisor;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.nmfpackage.NMFPackagePMBackend;
import esa.mo.platform.impl.util.PlatformServicesConsumer;
import esa.mo.platform.impl.util.PlatformServicesProviderInterface;
import esa.mo.platform.impl.util.PlatformServicesProviderSoftSim;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;

/**
 * This is a generic implementation of the NMF supervisor. Using the property nmf.platform.impl you
 * can provide the platform services implementation which shall be used by the supervisor. If no
 * such property is provided, it will use the simulated platform services by default.
 *
 * @author yannick
 */
public class NanosatMOSupervisorBasicImpl extends NanoSatMOSupervisor {

  private PlatformServicesProviderInterface platformServices;

  @Override
  public void initPlatformServices(COMServicesProvider comServices) {
    try {
      String platformProviderClass = System.getProperty("nmf.platform.impl");
      try {
        platformServices
            = (PlatformServicesProviderInterface) Class.forName(platformProviderClass).newInstance();
      } catch (NullPointerException | ClassNotFoundException | InstantiationException
          | IllegalAccessException ex) {
        // If the property for the platform implementation is not provided, an NPE will occur.
        // In this case or any other problem with reflection default to the simulated platforms
        Logger.getLogger(NanosatMOSupervisorBasicImpl.class.getName()).log(Level.SEVERE,
            "Something went wrong when initializing the platform services. Using simulated services.",
            ex);
        platformServices
            = new PlatformServicesProviderSoftSim();
      }
      platformServices.init(comServices);
    } catch (MALException ex) {
      Logger.getLogger(NanosatMOSupervisorBasicImpl.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public void init(MonitorAndControlNMFAdapter mcAdapter) {
    init(mcAdapter, new PlatformServicesConsumer(), new NMFPackagePMBackend());
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
    adapter.setNmfConnector(supervisor);
    supervisor.init(adapter);
  }

}
