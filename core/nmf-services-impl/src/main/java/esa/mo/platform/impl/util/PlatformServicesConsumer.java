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
import esa.mo.platform.impl.consumer.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.platform.artificialintelligence.consumer.ArtificialIntelligenceStub;
import org.ccsds.moims.mo.platform.autonomousadcs.AutonomousADCSServiceInfo;
import org.ccsds.moims.mo.platform.autonomousadcs.consumer.AutonomousADCSStub;
import org.ccsds.moims.mo.platform.camera.CameraServiceInfo;
import org.ccsds.moims.mo.platform.camera.consumer.CameraStub;
import org.ccsds.moims.mo.platform.fpga.FPGAServiceInfo;
import org.ccsds.moims.mo.platform.fpga.consumer.FPGAStub;
import org.ccsds.moims.mo.platform.gps.GPSServiceInfo;
import org.ccsds.moims.mo.platform.gps.consumer.GPSStub;
import org.ccsds.moims.mo.platform.opticaldatareceiver.OpticalDataReceiverServiceInfo;
import org.ccsds.moims.mo.platform.opticaldatareceiver.consumer.OpticalDataReceiverStub;
import org.ccsds.moims.mo.platform.powercontrol.PowerControlServiceInfo;
import org.ccsds.moims.mo.platform.powercontrol.consumer.PowerControlStub;
import org.ccsds.moims.mo.platform.softwaredefinedradio.SoftwareDefinedRadioServiceInfo;
import org.ccsds.moims.mo.platform.softwareimages.SoftwareImagesServiceInfo;
import org.ccsds.moims.mo.platform.softwareimages.consumer.SoftwareImagesStub;
import org.ccsds.moims.mo.platform.softwaredefinedradio.consumer.SoftwareDefinedRadioStub;

/**
 * The PlatformServicesConsumer class holds all the consumers for the Platform
 * services.
 */
public class PlatformServicesConsumer implements PlatformServicesConsumerInterface {

    private ArtificialIntelligenceConsumerServiceImpl aiService;
    private AutonomousADCSConsumerServiceImpl autonomousADCSService;
    private CameraConsumerServiceImpl cameraService;
    private GPSConsumerServiceImpl gpsService;
    private OpticalDataReceiverConsumerServiceImpl odrService;
    private SoftwareDefinedRadioConsumerServiceImpl sdrService;
    private PowerControlConsumerServiceImpl powerControlService;
    private FPGAConsumerServiceImpl fpgaService;
    private SoftwareImagesConsumerServiceImpl softwareImagesService;

    /**
     * Default constructor.
     */
    public PlatformServicesConsumer() {
    }

    /**
     * Initializes all the Platform service consumers with no authentication id nor local
     * name prefix.
     *
     * @param connectionConsumer the connection details of the Platform service providers
     * @param comServices the COM services consumer used by the Platform services
     */
    public void init(ConnectionConsumer connectionConsumer, COMServicesConsumer comServices) {
        init(connectionConsumer, comServices, null, null);
    }

    /**
     * Initializes all the Platform service consumers advertised in the connection details.
     *
     * @param connectionConsumer the connection details of the Platform service providers
     * @param comServices the COM services consumer used by the Platform services
     * @param authenticationID the authentication id of the logged in user
     * @param localNamePrefix the prefix for the local name of the consumers
     */
    public void init(ConnectionConsumer connectionConsumer,
            COMServicesConsumer comServices, Blob authenticationID, String localNamePrefix) {
        SingleConnectionDetails details;

        try {
            // Initialize the AutonomousADCS service
            details = connectionConsumer.getServicesDetails().get(AutonomousADCSServiceInfo.AUTONOMOUSADCS_SERVICE_NAME);

            if (details != null) {
                aiService = new ArtificialIntelligenceConsumerServiceImpl(
                        details, comServices, authenticationID, localNamePrefix);
            }

            // Initialize the AutonomousADCS service
            details = connectionConsumer.getServicesDetails().get(AutonomousADCSServiceInfo.AUTONOMOUSADCS_SERVICE_NAME);
            if (details != null) {
                autonomousADCSService = new AutonomousADCSConsumerServiceImpl(
                        details, comServices, authenticationID, localNamePrefix);
            }

            // Initialize the Camera service
            details = connectionConsumer.getServicesDetails().get(CameraServiceInfo.CAMERA_SERVICE_NAME);
            if (details != null) {
                cameraService = new CameraConsumerServiceImpl(details,
                        comServices, authenticationID, localNamePrefix);
            }

            // Initialize the GPS service
            details = connectionConsumer.getServicesDetails().get(GPSServiceInfo.GPS_SERVICE_NAME);
            if (details != null) {
                gpsService = new GPSConsumerServiceImpl(details,
                        comServices, authenticationID, localNamePrefix);
            }

            // Initialize the Optical Data Receiver service
            details = connectionConsumer.getServicesDetails().get(
                    OpticalDataReceiverServiceInfo.OPTICALDATARECEIVER_SERVICE_NAME);

            if (details != null) {
                odrService = new OpticalDataReceiverConsumerServiceImpl(details,
                        comServices, authenticationID, localNamePrefix);
            }

            // Initialize the Software Defined Radio service
            details = connectionConsumer.getServicesDetails().get(
                    SoftwareDefinedRadioServiceInfo.SOFTWAREDEFINEDRADIO_SERVICE_NAME);

            if (details != null) {
                sdrService = new SoftwareDefinedRadioConsumerServiceImpl(details,
                        comServices, authenticationID, localNamePrefix);
            }

            // Initialize the Power Control service
            details = connectionConsumer.getServicesDetails().get(PowerControlServiceInfo.POWERCONTROL_SERVICE_NAME);

            if (details != null) {
                powerControlService = new PowerControlConsumerServiceImpl(details,
                        comServices, authenticationID, localNamePrefix);
            }


            // Initialize the FPGA service
            details = connectionConsumer.getServicesDetails().get(
                    FPGAServiceInfo.FPGA_SERVICE_NAME);

            if (details != null) {
                fpgaService = new FPGAConsumerServiceImpl(details,
                        comServices, authenticationID, localNamePrefix);
            }

            // Initialize the Software Images service
            details = connectionConsumer.getServicesDetails().get(
                    SoftwareImagesServiceInfo.SOFTWAREIMAGES_SERVICE_NAME);

            if (details != null) {
                softwareImagesService = new SoftwareImagesConsumerServiceImpl(details,
                        comServices, authenticationID, localNamePrefix);
            }
        } catch (MALException | MALInteractionException ex) {
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
    public FPGAStub getFPGAService() throws IOException {
        if (this.fpgaService == null) {
            throw new IOException("The service consumer is not connected to the provider.");
        }

        return this.fpgaService.getFPGAStub();
    }

    @Override
    public SoftwareImagesStub getSoftwareImagesService() throws IOException {
        if (this.softwareImagesService == null) {
            throw new IOException("The service consumer is not connected to the provider.");
        }

        return this.softwareImagesService.getSoftwareImagesStub();
    }

    // Setters
    /**
     * Sets manually the ArtificialIntelligence service consumer.
     *
     * @param aiService the ArtificialIntelligence service consumer
     */
    public void setArtificialIntelligenceService(ArtificialIntelligenceConsumerServiceImpl aiService) {
        this.aiService = aiService;
    }

    /**
     * Sets manually the AutonomousADCS service consumer.
     *
     * @param autonomousADCSService the AutonomousADCS service consumer
     */
    public void setAutonomousADCSService(AutonomousADCSConsumerServiceImpl autonomousADCSService) {
        this.autonomousADCSService = autonomousADCSService;
    }

    /**
     * Sets manually the Camera service consumer.
     *
     * @param cameraService the Camera service consumer
     */
    public void setCameraService(CameraConsumerServiceImpl cameraService) {
        this.cameraService = cameraService;
    }

    /**
     * Sets manually the GPS service consumer.
     *
     * @param gpsService the GPS service consumer
     */
    public void setGPSService(GPSConsumerServiceImpl gpsService) {
        this.gpsService = gpsService;
    }

    /**
     * Sets manually the OpticalDataReceiver service consumer.
     *
     * @param odrService the OpticalDataReceiver service consumer
     */
    public void setOpticalDataReceiverService(OpticalDataReceiverConsumerServiceImpl odrService) {
        this.odrService = odrService;
    }

    /**
     * Sets manually the SoftwareDefinedRadio service consumer.
     *
     * @param sdrService the SoftwareDefinedRadio service consumer
     */
    public void setSoftwareDefinedRadioService(SoftwareDefinedRadioConsumerServiceImpl sdrService) {
        this.sdrService = sdrService;
    }

    /**
     * Sets manually the PowerControl service consumer.
     *
     * @param powerControlService the PowerControl service consumer
     */
    public void setPowerControlService(PowerControlConsumerServiceImpl powerControlService) {
        this.powerControlService = powerControlService;
    }


    /**
     * Sets manually the FPGA service consumer.
     *
     * @param fpgaService the FPGA service consumer
     */
    public void setFPGAService(FPGAConsumerServiceImpl fpgaService) {
        this.fpgaService = fpgaService;
    }

    /**
     * Sets manually the SoftwareImages service consumer.
     *
     * @param softwareImagesService the SoftwareImages service consumer
     */
    public void setSoftwareImagesService(SoftwareImagesConsumerServiceImpl softwareImagesService) {
        this.softwareImagesService = softwareImagesService;
    }

    /**
     * Closes the service consumer connections
     *
     */
    public void closeConnections() {
        if (this.aiService != null) {
            this.aiService.closeConnection();
        }

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

        if (this.fpgaService != null) {
            this.fpgaService.closeConnection();
        }

        if (this.softwareImagesService != null) {
            this.softwareImagesService.closeConnection();
        }
    }

    /**
     * Propagates the authentication id to all the initialized Platform service consumers.
     *
     * @param authenticationId the authentication id of the logged in user
     */
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


        if (this.fpgaService != null) {
            this.fpgaService.setAuthenticationId(authenticationId);
        }

        if (this.softwareImagesService != null) {
            this.softwareImagesService.setAuthenticationId(authenticationId);
        }
    }

}
