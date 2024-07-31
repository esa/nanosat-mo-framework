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

import esa.mo.com.impl.util.COMServicesProvider;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.platform.artificialintelligence.provider.ArtificialIntelligenceInheritanceSkeleton;
import org.ccsds.moims.mo.platform.autonomousadcs.provider.AutonomousADCSInheritanceSkeleton;
import org.ccsds.moims.mo.platform.camera.provider.CameraInheritanceSkeleton;
import org.ccsds.moims.mo.platform.gps.provider.GPSInheritanceSkeleton;
import org.ccsds.moims.mo.platform.opticaldatareceiver.provider.OpticalDataReceiverInheritanceSkeleton;
import org.ccsds.moims.mo.platform.softwaredefinedradio.provider.SoftwareDefinedRadioInheritanceSkeleton;

/**
 * The Platform services provider interface. Allows the retrieval of the default
 * set of services that are part of the Platform services.
 */
public interface PlatformServicesProviderInterface {

    /**
     * The init method initializes the Platform services.
     *
     * @param comServices The COM services
     * @throws MALException if there is an error during the initialization.
     */
    default void init(COMServicesProvider comServices) throws MALException {
    }

    /**
     * Returns the Artificial Intelligence service.
     *
     * @return The Artificial Intelligence service.
     */
    ArtificialIntelligenceInheritanceSkeleton getAIService();

    /**
     * Returns the Camera service.
     *
     * @return The Camera service.
     */
    CameraInheritanceSkeleton getCameraService();

    /**
     * Returns the GPS service.
     *
     * @return The GPS service.
     */
    GPSInheritanceSkeleton getGPSService();

    /**
     * Returns the Autonomous ADCS service.
     *
     * @return The Autonomous ADCS service.
     */
    AutonomousADCSInheritanceSkeleton getAutonomousADCSService();

    /**
     * Returns the Optical Data Receiver service.
     *
     * @return The Optical Data Receiver service.
     */
    OpticalDataReceiverInheritanceSkeleton getOpticalDataReceiverService();

    /**
     * Returns the Software Defined Radio service.
     *
     * @return The Software Defined Radio service.
     */
    SoftwareDefinedRadioInheritanceSkeleton getSoftwareDefinedRadioService();

}
