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
import org.ccsds.moims.mo.platform.clock.consumer.ClockStub;
import org.ccsds.moims.mo.platform.gps.consumer.GPSStub;
import org.ccsds.moims.mo.platform.opticaldatareceiver.consumer.OpticalDataReceiverStub;
import org.ccsds.moims.mo.platform.powercontrol.consumer.PowerControlStub;
import org.ccsds.moims.mo.platform.softwaredefinedradio.consumer.SoftwareDefinedRadioStub;

/**
 *
 * @author Cesar Coelho
 */
public interface PlatformServicesConsumerInterface {

    ArtificialIntelligenceStub getAIService() throws IOException;

    AutonomousADCSStub getAutonomousADCSService() throws IOException;
    
    CameraStub getCameraService() throws IOException;

    GPSStub getGPSService() throws IOException;

    OpticalDataReceiverStub getOpticalDataReceiverService() throws IOException;

    SoftwareDefinedRadioStub getSoftwareDefinedRadioService() throws IOException;

    PowerControlStub getPowerControlService() throws IOException;

    ClockStub getClockService() throws IOException;

}
