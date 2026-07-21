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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.helpertools.misc.ConsumerServiceImpl;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.platform.autonomousadcs.AutonomousADCSHelper;
import org.ccsds.moims.mo.platform.autonomousadcs.consumer.AutonomousADCSStub;

/**
 * The AutonomousADCSConsumerServiceImpl class implements the service consumer
 * for the AutonomousADCS service.
 */
public class AutonomousADCSConsumerServiceImpl extends ConsumerServiceImpl {

    private AutonomousADCSStub autonomousADCSService = null;
    private COMServicesConsumer comServices;

    public AutonomousADCSConsumerServiceImpl(SingleConnectionDetails connectionDetails,
            COMServicesConsumer comServices, Blob authenticationID,
            String localNamePrefix) throws MALException, MALInteractionException {
        this.connectionDetails = connectionDetails;
        this.comServices = comServices;

        // Close old connection
        if (tmConsumer != null) {
            try {
                tmConsumer.close();
            } catch (MALException ex) {
                Logger.getLogger(AutonomousADCSConsumerServiceImpl.class.getName()).log(
                        Level.SEVERE, "The consumer connection could not be closed!", ex);
            }
        }

        tmConsumer = connection.startService(
                this.connectionDetails,
                AutonomousADCSHelper.AUTONOMOUSADCS_SERVICE,
                authenticationID,
                localNamePrefix);

        this.autonomousADCSService = new AutonomousADCSStub(tmConsumer);
    }

    public AutonomousADCSConsumerServiceImpl(SingleConnectionDetails connectionDetails,
            COMServicesConsumer comServices) throws MALException, MALInteractionException {
        this(connectionDetails, comServices, null, null);
    }

    public COMServicesConsumer getCOMServices() {
        return comServices;
    }

    @Override
    public Object getStub() {
        return this.getAutonomousADCSStub();
    }

    public AutonomousADCSStub getAutonomousADCSStub() {
        return this.autonomousADCSService;
    }

    @Override
    public Object generateServiceStub(MALConsumer tmConsumer) {
        return new AutonomousADCSStub(tmConsumer);
    }

}
