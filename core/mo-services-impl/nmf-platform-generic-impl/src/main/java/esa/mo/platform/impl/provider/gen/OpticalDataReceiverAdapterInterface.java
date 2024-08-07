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

import org.ccsds.moims.mo.mal.structures.Duration;

/**
 *
 * @author Cesar Coelho
 */
public interface OpticalDataReceiverAdapterInterface {

    /**
     * Checks if the device is present and accessible.
     *
     * @return true if the device is present and available for operations.
     */
    boolean isUnitAvailable();

    /**
     * Synchronous method to records data by the optical receiver and return the recorded buffer.
     *
     * @param recordingLength The duration of the recording.
     * @return Raw buffer of the data received from the optical receiver.
     */
    byte[] recordOpticalReceiverData(Duration recordingLength);

}
