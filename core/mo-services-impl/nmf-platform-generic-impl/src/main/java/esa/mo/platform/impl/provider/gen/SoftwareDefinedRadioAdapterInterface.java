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
package esa.mo.platform.impl.provider.gen;

import org.ccsds.moims.mo.platform.softwaredefinedradio.structures.IQComponents;
import org.ccsds.moims.mo.platform.softwaredefinedradio.structures.SDRConfiguration;

/**
 *
 * @author Cesar Coelho
 */
public interface SoftwareDefinedRadioAdapterInterface {

    /**
     * Checks if the device is present and accessible.
     *
     * @return true if the device is present and available for operations.
     */
    boolean isUnitAvailable();

    /**
     * Applies SDR configuration.
     *
     * @param configuration SDR configuration
     * @return true if the configuration was accepted and set on the SDR.
     */
    boolean setConfiguration(SDRConfiguration configuration);

    /**
     * Enables or disables the SDR
     *
     * @param enable Enable flag.
     * @return true if the operation ran successfully.
     */
    boolean enableSDR(Boolean enable);

    /**
     * Gets SDR I/Q buffer
     *
     * @return Buffer of I/Q data
     */
    IQComponents getIQComponents();

}
