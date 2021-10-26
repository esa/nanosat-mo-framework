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
package esa.mo.platform.impl.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import esa.mo.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.platform.opticaldatareceiver.provider.OpticalDataReceiverInheritanceSkeleton;
import org.ccsds.moims.mo.platform.softwaredefinedradio.provider.SoftwareDefinedRadioInheritanceSkeleton;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.platform.impl.provider.gen.AutonomousADCSAdapterInterface;
import esa.mo.platform.impl.provider.gen.AutonomousADCSProviderServiceImpl;
import esa.mo.platform.impl.provider.gen.CameraAdapterInterface;
import esa.mo.platform.impl.provider.gen.CameraProviderServiceImpl;
import esa.mo.platform.impl.provider.gen.ClockAdapterInterface;
import esa.mo.platform.impl.provider.gen.ClockProviderServiceImpl;
import esa.mo.platform.impl.provider.gen.GPSAdapterInterface;
import esa.mo.platform.impl.provider.gen.GPSProviderServiceImpl;
import esa.mo.platform.impl.provider.gen.OpticalDataReceiverAdapterInterface;
import esa.mo.platform.impl.provider.gen.OpticalDataReceiverProviderServiceImpl;
import esa.mo.platform.impl.provider.gen.PowerControlAdapterInterface;
import esa.mo.platform.impl.provider.gen.PowerControlProviderServiceImpl;
import esa.mo.platform.impl.provider.gen.SoftwareDefinedRadioAdapterInterface;
import esa.mo.platform.impl.provider.gen.SoftwareDefinedRadioProviderServiceImpl;
import esa.mo.platform.impl.provider.softsim.AutonomousADCSSoftSimAdapter;
import esa.mo.platform.impl.provider.softsim.CameraSoftSimAdapter;
import esa.mo.platform.impl.provider.softsim.ClockSoftSimAdapter;
import esa.mo.platform.impl.provider.softsim.GPSSoftSimAdapter;
import esa.mo.platform.impl.provider.softsim.OpticalDataReceiverSoftSimAdapter;
import esa.mo.platform.impl.provider.softsim.PowerControlSoftSimAdapter;
import esa.mo.platform.impl.provider.softsim.SoftwareDefinedRadioSoftSimAdapter;
import opssat.simulator.main.ESASimulator;

/**
 *
 *
 */
public class PlatformServicesProviderSoftSim implements PlatformServicesProviderInterface {

  // Simulator
  private final ESASimulator instrumentsSimulator = new ESASimulator("127.0.0.1");

  // Services
  private final AutonomousADCSProviderServiceImpl autonomousADCSService = new AutonomousADCSProviderServiceImpl();
  private final CameraProviderServiceImpl cameraService = new CameraProviderServiceImpl();
  private final GPSProviderServiceImpl gpsService = new GPSProviderServiceImpl();
  private final OpticalDataReceiverProviderServiceImpl opticalDataReceiverService = new OpticalDataReceiverProviderServiceImpl();
  private final SoftwareDefinedRadioProviderServiceImpl sdrService = new SoftwareDefinedRadioProviderServiceImpl();
  private final PowerControlProviderServiceImpl powerService = new PowerControlProviderServiceImpl();
  private PowerControlAdapterInterface pcAdapter;
  private final ClockProviderServiceImpl clockService = new ClockProviderServiceImpl();

  public void init(COMServicesProvider comServices) throws MALException {
    // Check if hybrid setup is used
    CameraAdapterInterface camAdapter;
    AutonomousADCSAdapterInterface adcsAdapter;
    GPSAdapterInterface gpsAdapter;
    OpticalDataReceiverAdapterInterface optRxAdapter;
    SoftwareDefinedRadioAdapterInterface sdrAdapter;
    ClockAdapterInterface clockAdapter;

    Properties platformProperties = new Properties();
    try {
      platformProperties.load(new FileInputStream("platformsim.properties"));
      if (platformProperties.getProperty("platform.mode").equals("hybrid")) {
        String pcAdapterName = platformProperties.getProperty("pc.adapter");
        String camAdapterName = platformProperties.getProperty("camera.adapter");
        String adcsAdapterName = platformProperties.getProperty("adcs.adapter");
        String gpsAdapterName = platformProperties.getProperty("gps.adapter");
        String optRxAdapterName = platformProperties.getProperty("optrx.adapter");
        String sdrAdapterName = platformProperties.getProperty("sdr.adapter");
        String clockAdapterName = platformProperties.getProperty("clock.adapter");
        try {
          pcAdapter = (PowerControlAdapterInterface) Class.forName(pcAdapterName).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
          Logger.getLogger(PlatformServicesProviderSoftSim.class.getName()).log(Level.WARNING,
              "Failed to instantiate the power control adapter. Falling back to default PowerControlSoftSimAdapter.",
              e);
          pcAdapter = new PowerControlSoftSimAdapter(instrumentsSimulator);
        }
        // Camera adapter
        try {
          Constructor constructor = Class.forName(camAdapterName).getConstructor(PowerControlAdapterInterface.class);
          camAdapter = (CameraAdapterInterface) constructor.newInstance(pcAdapter);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException
                | InvocationTargetException e) {
          Logger.getLogger(PlatformServicesProviderSoftSim.class.getName()).log(Level.WARNING,
              "Failed to instantiate the camera adapter. Falling back to default CameraSoftSimAdapter.", e);
          camAdapter = new CameraSoftSimAdapter(instrumentsSimulator, pcAdapter);
        }
        // ADCS adapter
        try {
          Constructor constructor = Class.forName(adcsAdapterName).getConstructor(PowerControlAdapterInterface.class);
          adcsAdapter = (AutonomousADCSAdapterInterface) constructor.newInstance(pcAdapter);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException
                | InvocationTargetException e) {
          Logger.getLogger(PlatformServicesProviderSoftSim.class.getName()).log(Level.WARNING,
              "Failed to instantiate the iADCS adapter. Falling back to default AutonomousADCSSoftSimAdapter.", e);
          adcsAdapter = new AutonomousADCSSoftSimAdapter(instrumentsSimulator, pcAdapter);
        }
        // GPS adapter
        try {
          Constructor constructor = Class.forName(gpsAdapterName).getConstructor(PowerControlAdapterInterface.class);
          gpsAdapter = (GPSAdapterInterface) constructor.newInstance(pcAdapter);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException
                | InvocationTargetException e) {
          Logger.getLogger(PlatformServicesProviderSoftSim.class.getName()).log(Level.WARNING,
              "Failed to instantiate the GPS adapter. Falling back to default GPSSoftSimAdapter.", e);
          gpsAdapter = new GPSSoftSimAdapter(instrumentsSimulator, pcAdapter);
        }
        // Optical Data Receiver adapter
        try {
          Constructor constructor = Class.forName(optRxAdapterName).getConstructor(PowerControlAdapterInterface.class);
          optRxAdapter = (OpticalDataReceiverAdapterInterface) constructor.newInstance(pcAdapter);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException
                | InvocationTargetException e) {
          Logger.getLogger(PlatformServicesProviderSoftSim.class.getName()).log(Level.WARNING,
              "Failed to instantiate the optRX adapter. Falling back to default OpticalDataReceiverSoftSimAdapter.", e);
          optRxAdapter = new OpticalDataReceiverSoftSimAdapter(instrumentsSimulator, pcAdapter);
        }
        // Radio adapter
        try {
          Constructor constructor = Class.forName(sdrAdapterName).getConstructor(PowerControlAdapterInterface.class);
          sdrAdapter = (SoftwareDefinedRadioAdapterInterface) constructor.newInstance(pcAdapter);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException
                | InvocationTargetException e) {
          Logger.getLogger(PlatformServicesProviderSoftSim.class.getName()).log(Level.WARNING,
              "Failed to instantiate the SDR adapter. Falling back to default SoftwareDefinedRadioSoftSimAdapter.", e);
          sdrAdapter = new SoftwareDefinedRadioSoftSimAdapter(instrumentsSimulator, pcAdapter);
        }
        try {
          clockAdapter = (ClockAdapterInterface) Class.forName(clockAdapterName).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
          Logger.getLogger(PlatformServicesProviderSoftSim.class.getName()).log(Level.WARNING,
              "Failed to instantiate the clock adapter. Falling back to default ClockSoftSimAdapter", e);
          clockAdapter = new ClockSoftSimAdapter(instrumentsSimulator);
        }
      } else {
        pcAdapter = new PowerControlSoftSimAdapter(instrumentsSimulator);
        camAdapter = new CameraSoftSimAdapter(instrumentsSimulator, pcAdapter);
        adcsAdapter = new AutonomousADCSSoftSimAdapter(instrumentsSimulator, pcAdapter);
        gpsAdapter = new GPSSoftSimAdapter(instrumentsSimulator, pcAdapter);
        optRxAdapter = new OpticalDataReceiverSoftSimAdapter(instrumentsSimulator, pcAdapter);
        sdrAdapter = new SoftwareDefinedRadioSoftSimAdapter(instrumentsSimulator, pcAdapter);
        clockAdapter = new ClockSoftSimAdapter(instrumentsSimulator);
      }
    } catch (IOException e) {
      // Assume simulated environment by default
      Logger.getLogger(PlatformServicesProviderSoftSim.class.getName()).log(Level.WARNING,
          "Platform config file not found. Using simulated environment.");
      pcAdapter = new PowerControlSoftSimAdapter(instrumentsSimulator);
      camAdapter = new CameraSoftSimAdapter(instrumentsSimulator, pcAdapter);
      adcsAdapter = new AutonomousADCSSoftSimAdapter(instrumentsSimulator, pcAdapter);
      gpsAdapter = new GPSSoftSimAdapter(instrumentsSimulator, pcAdapter);
      optRxAdapter = new OpticalDataReceiverSoftSimAdapter(instrumentsSimulator, pcAdapter);
      sdrAdapter = new SoftwareDefinedRadioSoftSimAdapter(instrumentsSimulator, pcAdapter);
      clockAdapter = new ClockSoftSimAdapter(instrumentsSimulator);
    }

    autonomousADCSService.init(comServices, adcsAdapter);
    cameraService.init(comServices, camAdapter);
    gpsService.init(comServices, gpsAdapter);
    opticalDataReceiverService.init(optRxAdapter);
    sdrService.init(sdrAdapter);
    powerService.init(pcAdapter);
    clockService.init(clockAdapter);
  }

  @Override
  public void startStatusTracking(ConnectionConsumer connection) {
    pcAdapter.startStatusTracking(connection);
  }

  @Override
  public AutonomousADCSProviderServiceImpl getAutonomousADCSService() {
    return this.autonomousADCSService;
  }

  @Override
  public CameraProviderServiceImpl getCameraService() {
    return this.cameraService;
  }

  @Override
  public GPSProviderServiceImpl getGPSService() {
    System.out.println("platform");
    return this.gpsService;
  }

  @Override
  public OpticalDataReceiverInheritanceSkeleton getOpticalDataReceiverService() {
    return this.opticalDataReceiverService;
  }

  @Override
  public SoftwareDefinedRadioInheritanceSkeleton getSoftwareDefinedRadioService() {
    return this.sdrService;
  }

}
