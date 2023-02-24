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

import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.platform.impl.consumer.CameraConsumerServiceImpl;
import esa.mo.platform.impl.consumer.ClockConsumerServiceImpl;
import esa.mo.platform.impl.consumer.GPSConsumerServiceImpl;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.platform.impl.consumer.ArtificialIntelligenceConsumerServiceImpl;
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
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.platform.artificialintelligence.ArtificialIntelligenceHelper;
import org.ccsds.moims.mo.platform.artificialintelligence.consumer.ArtificialIntelligenceStub;
import org.ccsds.moims.mo.platform.autonomousadcs.AutonomousADCSHelper;
import org.ccsds.moims.mo.platform.autonomousadcs.consumer.AutonomousADCSStub;
import org.ccsds.moims.mo.platform.camera.CameraHelper;
import org.ccsds.moims.mo.platform.camera.consumer.CameraStub;
import org.ccsds.moims.mo.platform.clock.ClockHelper;
import org.ccsds.moims.mo.platform.clock.consumer.ClockStub;
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
public class PlatformServicesConsumer implements PlatformServicesConsumerInterface {

    private ArtificialIntelligenceConsumerServiceImpl aiService;
    private AutonomousADCSConsumerServiceImpl autonomousADCSService;
    private CameraConsumerServiceImpl cameraService;
    private GPSConsumerServiceImpl gpsService;
    private OpticalDataReceiverConsumerServiceImpl odrService;
    private SoftwareDefinedRadioConsumerServiceImpl sdrService;
    private PowerControlConsumerServiceImpl powerControlService;
    private ClockConsumerServiceImpl clockService;

    public void init(ConnectionConsumer connectionConsumer, COMServicesConsumer comServices) {
        init(connectionConsumer, comServices, null, null);
    }

    public void init(ConnectionConsumer connectionConsumer,
            COMServicesConsumer comServices, Blob authenticationID, String localNamePrefix) {
        SingleConnectionDetails details;

        try {
            // Initialize the ArtificialIntelligence service
            details = connectionConsumer.getServicesDetails().get(
                    ArtificialIntelligenceHelper.ARTIFICIALINTELLIGENCE_SERVICE_NAME);
            if (details != null) {
                aiService = new ArtificialIntelligenceConsumerServiceImpl(
                        details, comServices, authenticationID, localNamePrefix);
            }

            // Initialize the AutonomousADCS service
            details = connectionConsumer.getServicesDetails().get(
                    AutonomousADCSHelper.AUTONOMOUSADCS_SERVICE_NAME);
            if (details != null) {
                autonomousADCSService = new AutonomousADCSConsumerServiceImpl(
                        details, comServices, authenticationID, localNamePrefix);
            }

            // Initialize the Camera service
            details = connectionConsumer.getServicesDetails().get(CameraHelper.CAMERA_SERVICE_NAME);
            if (details != null) {
                cameraService = new CameraConsumerServiceImpl(details,
                        comServices, authenticationID, localNamePrefix);
            }

            // Initialize the GPS service
            details = connectionConsumer.getServicesDetails().get(GPSHelper.GPS_SERVICE_NAME);
            if (details != null) {
                gpsService = new GPSConsumerServiceImpl(details,
                        comServices, authenticationID, localNamePrefix);
            }

            // Initialize the Optical Data Receiver service
            details = connectionConsumer.getServicesDetails().get(
                    OpticalDataReceiverHelper.OPTICALDATARECEIVER_SERVICE_NAME);
            if (details != null) {
                odrService = new OpticalDataReceiverConsumerServiceImpl(details,
                        comServices, authenticationID, localNamePrefix);
            }

            // Initialize the Software Defined Radio service
            details = connectionConsumer.getServicesDetails().get(
                    SoftwareDefinedRadioHelper.SOFTWAREDEFINEDRADIO_SERVICE_NAME);
            if (details != null) {
                sdrService = new SoftwareDefinedRadioConsumerServiceImpl(details,
                        comServices, authenticationID, localNamePrefix);
            }

            // Initialize the Power Control service
            details = connectionConsumer.getServicesDetails().get(
                    PowerControlHelper.POWERCONTROL_SERVICE_NAME);
            if (details != null) {
                powerControlService = new PowerControlConsumerServiceImpl(details,
                        comServices, authenticationID, localNamePrefix);
            }

            // Initialize the Clock service
            details = connectionConsumer.getServicesDetails().get(ClockHelper.CLOCK_SERVICE_NAME);
            if (details != null) {
                clockService = new ClockConsumerServiceImpl(details, comServices);
            }
        } catch (MALException | MALInteractionException | MalformedURLException ex) {
            Logger.getLogger(COMServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ArtificialIntelligenceStub getAIService() throws IOException {
        if (this.aiService == null) {
            throw new IOException("The service consumer is not connected to the provider.");
        }

        return this.aiService.getArtificialIntelligenceStub();
    }

    @Override
    public AutonomousADCSStub getAutonomousADCSService() throws IOException {
        if (this.autonomousADCSService == null) {
            throw new IOException("The service consumer is not connected to the provider.");
        }

        return this.autonomousADCSService.getAutonomousADCSStub();
    }

    @Override
    public CameraStub getCameraService() throws IOException {
        if (this.cameraService == null) {
            throw new IOException("The service consumer is not connected to the provider.");
        }

        return this.cameraService.getCameraStub();
    }

    @Override
    public GPSStub getGPSService() throws IOException {
        if (this.gpsService == null) {
            throw new IOException("The service consumer is not connected to the provider.");
        }

        return this.gpsService.getGPSStub();
    }

    @Override
    public OpticalDataReceiverStub getOpticalDataReceiverService() throws IOException {
        if (this.odrService == null) {
            throw new IOException("The service consumer is not connected to the provider.");
        }

        return this.odrService.getOpticalDataReceiverStub();
    }

    @Override
    public SoftwareDefinedRadioStub getSoftwareDefinedRadioService() throws IOException {
        if (this.sdrService == null) {
            throw new IOException("The service consumer is not connected to the provider.");
        }

        return this.sdrService.getSoftwareDefinedRadioStub();
    }

    @Override
    public PowerControlStub getPowerControlService() throws IOException {
        if (this.powerControlService == null) {
            throw new IOException("The service consumer is not connected to the provider.");
        }

        return this.powerControlService.getPowerControlStub();
    }

    @Override
    public ClockStub getClockService() throws IOException {
        if (this.clockService == null) {
            throw new IOException("The service consumer is not connected to the provider.");
        }

        return this.clockService.getClockStub();
    }

    // Setters
    public void setArtificialIntelligenceService(ArtificialIntelligenceConsumerServiceImpl aiService) {
        this.aiService = aiService;
    }

    public void setAutonomousADCSService(AutonomousADCSConsumerServiceImpl autonomousADCSService) {
        this.autonomousADCSService = autonomousADCSService;
    }

    public void setCameraService(CameraConsumerServiceImpl cameraService) {
        this.cameraService = cameraService;
    }

    public void setGPSService(GPSConsumerServiceImpl gpsService) {
        this.gpsService = gpsService;
    }

    public void setOpticalDataReceiverService(OpticalDataReceiverConsumerServiceImpl odrService) {
        this.odrService = odrService;
    }

    public void setSoftwareDefinedRadioService(SoftwareDefinedRadioConsumerServiceImpl sdrService) {
        this.sdrService = sdrService;
    }

    public void setPowerControlService(PowerControlConsumerServiceImpl powerControlService) {
        this.powerControlService = powerControlService;
    }

    public void setClockService(ClockConsumerServiceImpl clockService) {
        this.clockService = clockService;
    }

    /**
     * Closes the service consumer connections
     *
     */
    public void closeConnections() {
        if (this.aiService != null) {
            this.aiService.close();
        }

        if (this.autonomousADCSService != null) {
            this.autonomousADCSService.close();
        }

        if (this.cameraService != null) {
            this.cameraService.close();
        }

        if (this.gpsService != null) {
            this.gpsService.close();
        }

        if (this.odrService != null) {
            this.odrService.close();
        }

        if (this.sdrService != null) {
            this.sdrService.close();
        }

        if (this.powerControlService != null) {
            this.powerControlService.close();
        }
    }

    public void setAuthenticationId(Blob authenticationId) {
        if (this.aiService != null) {
            this.aiService.setAuthenticationId(authenticationId);
        }

        if (this.autonomousADCSService != null) {
            this.autonomousADCSService.setAuthenticationId(authenticationId);
        }

        if (this.cameraService != null) {
            this.cameraService.setAuthenticationId(authenticationId);
        }

        if (this.gpsService != null) {
            this.gpsService.setAuthenticationId(authenticationId);
        }

        if (this.odrService != null) {
            this.odrService.setAuthenticationId(authenticationId);
        }

        if (this.sdrService != null) {
            this.sdrService.setAuthenticationId(authenticationId);
        }

        if (this.powerControlService != null) {
            this.powerControlService.setAuthenticationId(authenticationId);
        }

        if (this.clockService != null) {
            this.clockService.close();
        }
    }

}
