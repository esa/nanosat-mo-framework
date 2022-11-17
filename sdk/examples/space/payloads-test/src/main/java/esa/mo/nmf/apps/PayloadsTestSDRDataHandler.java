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
import org.ccsds.moims.mo.platform.softwaredefinedradio.consumer.SoftwareDefinedRadioAdapter;
import org.ccsds.moims.mo.platform.softwaredefinedradio.structures.IQComponents;

class PayloadsTestSDRDataHandler extends SoftwareDefinedRadioAdapter {

    private static final Logger LOGGER = Logger.getLogger(PayloadsTestSDRDataHandler.class.getName());

    @Override
    public void streamRadioNotifyReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
        org.ccsds.moims.mo.mal.structures.Identifier _Identifier0,
        org.ccsds.moims.mo.mal.structures.UpdateHeaderList _UpdateHeaderList1,
        org.ccsds.moims.mo.platform.softwaredefinedradio.structures.IQComponentsList _IQComponentsList2,
        java.util.Map qosProperties) {
        if (_IQComponentsList2.isEmpty()) {
            LOGGER.log(Level.SEVERE, "empty IQComponentsList");
            return;
        }
        for (IQComponents iqComp : _IQComponentsList2) {
            LOGGER.log(Level.INFO, "Received I {0} samples and Q {1} samples.", new Object[]{iqComp.getInPhase().size(),
                                                                                             iqComp.getQuadrature()
                                                                                                 .size()});
        }
    }

    @Override
    public void streamRadioNotifyErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
        org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties) {
        LOGGER.log(Level.SEVERE, "MAL Error: {0}", error.toString());
    }

}
