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

import java.io.IOException;
import org.ccsds.moims.mo.platform.artificialintelligence.consumer.ArtificialIntelligenceStub;
import org.ccsds.moims.mo.platform.autonomousadcs.consumer.AutonomousADCSStub;
import org.ccsds.moims.mo.platform.camera.consumer.CameraStub;
import org.ccsds.moims.mo.platform.fpga.consumer.FPGAStub;
import org.ccsds.moims.mo.platform.gps.consumer.GPSStub;
import org.ccsds.moims.mo.platform.opticaldatareceiver.consumer.OpticalDataReceiverStub;
import org.ccsds.moims.mo.platform.powercontrol.consumer.PowerControlStub;
import org.ccsds.moims.mo.platform.softwaredefinedradio.consumer.SoftwareDefinedRadioStub;
import org.ccsds.moims.mo.platform.softwareimages.consumer.SoftwareImagesStub;

/**
 * The Platform services consumer interface. Allows the retrieval of the default
 * set of services that are part of the Platform services.
 */
public interface PlatformServicesConsumerInterface {

    /**
     * Returns the AI service stub, connecting to the provider on first use.
     *
     * @return the AI service stub
     * @throws IOException if the connection to the provider cannot be established
     */
    ArtificialIntelligenceStub getAIService() throws IOException;

    /**
     * Returns the AutonomousADCS service stub, connecting to the provider on first use.
     *
     * @return the AutonomousADCS service stub
     * @throws IOException if the connection to the provider cannot be established
     */
    AutonomousADCSStub getAutonomousADCSService() throws IOException;

    /**
     * Returns the Camera service stub, connecting to the provider on first use.
     *
     * @return the Camera service stub
     * @throws IOException if the connection to the provider cannot be established
     */
    CameraStub getCameraService() throws IOException;

    /**
     * Returns the GPS service stub, connecting to the provider on first use.
     *
     * @return the GPS service stub
     * @throws IOException if the connection to the provider cannot be established
     */
    GPSStub getGPSService() throws IOException;

    /**
     * Returns the OpticalDataReceiver service stub, connecting to the provider on first use.
     *
     * @return the OpticalDataReceiver service stub
     * @throws IOException if the connection to the provider cannot be established
     */
    OpticalDataReceiverStub getOpticalDataReceiverService() throws IOException;

    /**
     * Returns the SoftwareDefinedRadio service stub, connecting to the provider on first use.
     *
     * @return the SoftwareDefinedRadio service stub
     * @throws IOException if the connection to the provider cannot be established
     */
    SoftwareDefinedRadioStub getSoftwareDefinedRadioService() throws IOException;

    /**
     * Returns the PowerControl service stub, connecting to the provider on first use.
     *
     * @return the PowerControl service stub
     * @throws IOException if the connection to the provider cannot be established
     */
    PowerControlStub getPowerControlService() throws IOException;


    /**
     * Returns the FPGA service stub, connecting to the provider on first use.
     *
     * @return the FPGA service stub
     * @throws IOException if the connection to the provider cannot be established
     */
    FPGAStub getFPGAService() throws IOException;

    /**
     * Returns the SoftwareImages service stub, connecting to the provider on first use.
     *
     * @return the SoftwareImages service stub
     * @throws IOException if the connection to the provider cannot be established
     */
    SoftwareImagesStub getSoftwareImagesService() throws IOException;

}
