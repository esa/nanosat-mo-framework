/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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

import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.platform.impl.consumer.CameraConsumerServiceImpl;
import esa.mo.platform.impl.consumer.GPSConsumerServiceImpl;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.platform.impl.consumer.AutonomousADCSConsumerServiceImpl;
import esa.mo.platform.impl.consumer.OpticalDataReceiverConsumerServiceImpl;
import esa.mo.platform.impl.consumer.PowerControlConsumerServiceImpl;
import esa.mo.platform.impl.consumer.SoftwareDefinedRadioConsumerServiceImpl;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.platform.autonomousadcs.AutonomousADCSHelper;
import org.ccsds.moims.mo.platform.autonomousadcs.consumer.AutonomousADCSStub;
import org.ccsds.moims.mo.platform.camera.CameraHelper;
import org.ccsds.moims.mo.platform.camera.consumer.CameraStub;
import org.ccsds.moims.mo.platform.gps.GPSHelper;
import org.ccsds.moims.mo.platform.gps.consumer.GPSStub;
import org.ccsds.moims.mo.platform.opticaldatareceiver.OpticalDataReceiverHelper;
import org.ccsds.moims.mo.platform.opticaldatareceiver.consumer.OpticalDataReceiverStub;
import org.ccsds.moims.mo.platform.powercontrol.PowerControlHelper;
import org.ccsds.moims.mo.platform.powercontrol.consumer.PowerControlStub;
import org.ccsds.moims.mo.platform.softwaredefinedradio.SoftwareDefinedRadioHelper;
import org.ccsds.moims.mo.platform.softwaredefinedradio.consumer.SoftwareDefinedRadioStub;

/**
 *
 *
 */
public class PlatformServicesConsumer implements PlatformServicesConsumerInterface
{

  private AutonomousADCSConsumerServiceImpl autonomousADCSService;
  private CameraConsumerServiceImpl cameraService;
  private GPSConsumerServiceImpl gpsService;
  private OpticalDataReceiverConsumerServiceImpl odrService;
  private SoftwareDefinedRadioConsumerServiceImpl sdrService;
  private PowerControlConsumerServiceImpl powerControlService;

  public void init(ConnectionConsumer connectionConsumer, COMServicesConsumer comServices)
  {
    SingleConnectionDetails details;

    try {
      // Initialize the AutonomousADCS service
      details = connectionConsumer.getServicesDetails().get(
          AutonomousADCSHelper.AUTONOMOUSADCS_SERVICE_NAME);
      if (details != null) {
        autonomousADCSService = new AutonomousADCSConsumerServiceImpl(details, comServices);
      }

      // Initialize the Camera service
      details = connectionConsumer.getServicesDetails().get(CameraHelper.CAMERA_SERVICE_NAME);
      if (details != null) {
        cameraService = new CameraConsumerServiceImpl(details, comServices);
      }

      // Initialize the GPS service
      details = connectionConsumer.getServicesDetails().get(GPSHelper.GPS_SERVICE_NAME);
      if (details != null) {
        gpsService = new GPSConsumerServiceImpl(details, comServices);
      }

      // Initialize the Optical Data Receiver service
      details = connectionConsumer.getServicesDetails().get(
          OpticalDataReceiverHelper.OPTICALDATARECEIVER_SERVICE_NAME);
      if (details != null) {
        odrService = new OpticalDataReceiverConsumerServiceImpl(details, comServices);
      }

      // Initialize the Software Defined Radio service
      details = connectionConsumer.getServicesDetails().get(
          SoftwareDefinedRadioHelper.SOFTWAREDEFINEDRADIO_SERVICE_NAME);
      if (details != null) {
        sdrService = new SoftwareDefinedRadioConsumerServiceImpl(details, comServices);
      }

      // Initialize the Power Control service
      details = connectionConsumer.getServicesDetails().get(
          PowerControlHelper.POWERCONTROL_SERVICE_NAME);
      if (details != null) {
        powerControlService = new PowerControlConsumerServiceImpl(details, comServices);
      }
    } catch (MALException ex) {
      Logger.getLogger(COMServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
    } catch (MalformedURLException ex) {
      Logger.getLogger(COMServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
    } catch (MALInteractionException ex) {
      Logger.getLogger(COMServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public AutonomousADCSStub getAutonomousADCSService() throws IOException
  {
    if (this.autonomousADCSService == null) {
      throw new IOException("The service consumer is not connected to the provider.");
    }

    return this.autonomousADCSService.getAutonomousADCSStub();
  }

  @Override
  public CameraStub getCameraService() throws IOException
  {
    if (this.cameraService == null) {
      throw new IOException("The service consumer is not connected to the provider.");
    }

    return this.cameraService.getCameraStub();
  }

  @Override
  public GPSStub getGPSService() throws IOException
  {
    if (this.gpsService == null) {
      throw new IOException("The service consumer is not connected to the provider.");
    }

    return this.gpsService.getGPSStub();
  }

  @Override
  public OpticalDataReceiverStub getOpticalDataReceiverService() throws IOException
  {
    if (this.odrService == null) {
      throw new IOException("The service consumer is not connected to the provider.");
    }

    return this.odrService.getOpticalDataReceiverStub();
  }

  @Override
  public SoftwareDefinedRadioStub getSoftwareDefinedRadioService() throws IOException
  {
    if (this.sdrService == null) {
      throw new IOException("The service consumer is not connected to the provider.");
    }

    return this.sdrService.getSoftwareDefinedRadioStub();
  }

  @Override
  public PowerControlStub getPowerControlService() throws IOException
  {
    if (this.powerControlService == null) {
      throw new IOException("The service consumer is not connected to the provider.");
    }

    return this.powerControlService.getPowerControlStub();
  }

  // Setters
  public void setAutonomousADCSService(AutonomousADCSConsumerServiceImpl autonomousADCSService)
  {
    this.autonomousADCSService = autonomousADCSService;
  }

  public void setCameraService(CameraConsumerServiceImpl cameraService)
  {
    this.cameraService = cameraService;
  }

  public void setGPSService(GPSConsumerServiceImpl gpsService)
  {
    this.gpsService = gpsService;
  }

  public void setOpticalDataReceiverService(OpticalDataReceiverConsumerServiceImpl odrService)
  {
    this.odrService = odrService;
  }

  public void setSoftwareDefinedRadioService(SoftwareDefinedRadioConsumerServiceImpl sdrService)
  {
    this.sdrService = sdrService;
  }

  public void setPowerControlService(PowerControlConsumerServiceImpl powerControlService)
  {
    this.powerControlService = powerControlService;
  }

  /**
   * Closes the service consumer connections
   *
   */
  public void closeConnections()
  {
    if (this.autonomousADCSService != null) {
      this.autonomousADCSService.closeConnection();
    }

    if (this.cameraService != null) {
      this.cameraService.closeConnection();
    }

    if (this.gpsService != null) {
      this.gpsService.closeConnection();
    }

    if (this.odrService != null) {
      this.odrService.closeConnection();
    }

    if (this.sdrService != null) {
      this.sdrService.closeConnection();
    }

    if (this.powerControlService != null) {
      this.powerControlService.closeConnection();
    }
  }

}
