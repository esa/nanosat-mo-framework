/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
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
import esa.mo.platform.impl.consumer.MagnetometerConsumerServiceImpl;
import esa.mo.platform.impl.consumer.OpticalDataReceiverConsumerServiceImpl;
import esa.mo.platform.impl.consumer.SoftwareDefinedRadioConsumerServiceImpl;
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
import org.ccsds.moims.mo.platform.magnetometer.MagnetometerHelper;
import org.ccsds.moims.mo.platform.magnetometer.consumer.MagnetometerStub;
import org.ccsds.moims.mo.platform.opticaldatareceiver.consumer.OpticalDataReceiverStub;
import org.ccsds.moims.mo.platform.softwaredefinedradio.consumer.SoftwareDefinedRadioStub;

/**
 *
 *
 */
public class PlatformServicesConsumer implements PlatformServicesConsumerInterface {

    private AutonomousADCSConsumerServiceImpl autonomousADCSService;
    private CameraConsumerServiceImpl cameraService;
    private GPSConsumerServiceImpl gpsService;
    private MagnetometerConsumerServiceImpl magnetometerService;
    private OpticalDataReceiverConsumerServiceImpl odrService;
    private SoftwareDefinedRadioConsumerServiceImpl sdrService;

    public void init(ConnectionConsumer connectionConsumer, COMServicesConsumer comServices) {

        SingleConnectionDetails details;

        try {
            // Initialize the AutonomousADCS service
            details = connectionConsumer.getServicesDetails().get(AutonomousADCSHelper.AUTONOMOUSADCS_SERVICE_NAME);
            if(details != null){
                autonomousADCSService = new AutonomousADCSConsumerServiceImpl(details, comServices);
            }

            // Initialize the Camera service
            details = connectionConsumer.getServicesDetails().get(CameraHelper.CAMERA_SERVICE_NAME);
            if(details != null){
                cameraService = new CameraConsumerServiceImpl(details, comServices);
            }

            // Initialize the GPS service
            details = connectionConsumer.getServicesDetails().get(GPSHelper.GPS_SERVICE_NAME);
            if(details != null){
                gpsService = new GPSConsumerServiceImpl(details, comServices);
            }

            // Initialize the GPS service
            details = connectionConsumer.getServicesDetails().get(MagnetometerHelper.MAGNETOMETER_SERVICE_NAME);
            if(details != null){
                magnetometerService = new MagnetometerConsumerServiceImpl(details, comServices);
            }

            // Initialize the Optical Data Receiver service
            details = connectionConsumer.getServicesDetails().get(CameraHelper.CAMERA_SERVICE_NAME);
            if(details != null){
                odrService = new OpticalDataReceiverConsumerServiceImpl(details, comServices);
            }

            // Initialize the Software Defined Radio service
            details = connectionConsumer.getServicesDetails().get(CameraHelper.CAMERA_SERVICE_NAME);
            if(details != null){
                sdrService = new SoftwareDefinedRadioConsumerServiceImpl(details, comServices);
            }

        } catch (MALException  ex) {
            Logger.getLogger(COMServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException  ex) {
            Logger.getLogger(COMServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(COMServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setServices(CameraConsumerServiceImpl cameraService,
            GPSConsumerServiceImpl gpsService) {
        this.cameraService = cameraService;
        this.gpsService = gpsService;
    }

    
    @Override
    public AutonomousADCSStub getAutonomousADCSService() {
        return this.autonomousADCSService.getAutonomousADCSStub();
    }

    @Override
    public CameraStub getCameraService() {
        return this.cameraService.getCameraStub();
    }

    @Override
    public GPSStub getGPSService() {
        return this.gpsService.getGPSStub();
    }

    @Override
    public MagnetometerStub getMagnetometerService() {
        return this.magnetometerService.getMagnetometerStub();
    }

    @Override
    public OpticalDataReceiverStub getOpticalDataReceiverService() {
        return this.odrService.getOpticalDataReceiverStub();
    }

    @Override
    public SoftwareDefinedRadioStub getSoftwareDefinedRadioService() {
        return this.sdrService.getSoftwareDefinedRadioStub();
    }

    // Setters

    public void setCameraService(CameraConsumerServiceImpl cameraService) {
        this.cameraService = cameraService;
    }

    public void setAutonomousADCSService(AutonomousADCSConsumerServiceImpl autonomousADCSService) {
        this.autonomousADCSService = autonomousADCSService;
    }

    public void setGPSService(GPSConsumerServiceImpl gpsService) {
        this.gpsService = gpsService;
    }

    public void setMagnetometerService(MagnetometerConsumerServiceImpl magnetometerService) {
        this.magnetometerService = magnetometerService;
    }

    public void setOpticalDataReceiverService(OpticalDataReceiverConsumerServiceImpl odrService) {
        this.odrService = odrService;
    }

    public void setSoftwareDefinedRadioService(SoftwareDefinedRadioConsumerServiceImpl sdrService) {
        this.sdrService = sdrService;
    }

    /**
     * Closes the service consumer connections
     *
     */
    public void closeConnections() {
        if(this.cameraService != null){
            this.cameraService.closeConnection();
        }
        
        if(this.gpsService != null){
            this.gpsService.closeConnection();
        }
    }    
  
}
