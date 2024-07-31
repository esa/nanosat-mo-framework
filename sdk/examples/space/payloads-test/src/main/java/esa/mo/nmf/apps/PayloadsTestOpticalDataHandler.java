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
package esa.mo.nmf.apps;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.platform.opticaldatareceiver.consumer.OpticalDataReceiverAdapter;

class PayloadsTestOpticalDataHandler extends OpticalDataReceiverAdapter {

    private static final Logger LOGGER = Logger.getLogger(PayloadsTestOpticalDataHandler.class.getName());

    public PayloadsTestOpticalDataHandler() {
    }

    @Override
    public void recordSamplesResponseReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
        org.ccsds.moims.mo.mal.structures.Blob data, java.util.Map qosProperties) {
        LOGGER.log(Level.INFO, "Received {0} bytes.", data.getLength());
    }

    @Override
    public void recordSamplesResponseErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
        org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties) {
        LOGGER.log(Level.SEVERE, "MAL Error: {0}", error.toString());

    }

}
