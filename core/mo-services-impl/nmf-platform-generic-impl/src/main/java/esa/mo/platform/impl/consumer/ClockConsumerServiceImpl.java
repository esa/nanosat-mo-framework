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
package esa.mo.platform.impl.consumer;

import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.helpertools.misc.ConsumerServiceImpl;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.platform.clock.ClockHelper;
import org.ccsds.moims.mo.platform.clock.consumer.ClockStub;

public class ClockConsumerServiceImpl extends ConsumerServiceImpl {

    private ClockStub clockStub = null;
    private COMServicesConsumer comServices;

    public COMServicesConsumer getCOMServices() {
        return comServices;
    }

    @Override
    public Object getStub() {
        return this.getClockStub();
    }

    public ClockStub getClockStub() {
        return this.clockStub;
    }

    @Override
    public Object generateServiceStub(MALConsumer tmConsumer) {
        return new ClockStub(tmConsumer);
    }

    public ClockConsumerServiceImpl(SingleConnectionDetails connectionDetails,
                                    COMServicesConsumer comServices) throws MALException, MalformedURLException, MALInteractionException {
        this.connectionDetails = connectionDetails;
        this.comServices = comServices;

        // Close old connection
        if (tmConsumer != null) {
            try {
                tmConsumer.close();
            } catch (MALException ex) {
                Logger.getLogger(ClockConsumerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        tmConsumer = connection.startService(this.connectionDetails.getProviderURI(), this.connectionDetails
                                                                                                            .getBrokerURI(),
                                             this.connectionDetails.getDomain(), ClockHelper.CLOCK_SERVICE);

        this.clockStub = new ClockStub(tmConsumer);
    }

}
