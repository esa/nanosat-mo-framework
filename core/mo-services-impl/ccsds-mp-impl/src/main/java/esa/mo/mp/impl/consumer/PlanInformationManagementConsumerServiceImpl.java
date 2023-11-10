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
package esa.mo.mp.impl.consumer;

import esa.mo.com.impl.util.COMServicesConsumer;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.helpertools.misc.ConsumerServiceImpl;
import org.ccsds.moims.mo.mp.planinformationmanagement.PlanInformationManagementHelper;
import org.ccsds.moims.mo.mp.planinformationmanagement.consumer.PlanInformationManagementStub;

/**
 * Plan Information Management (PIM) Service consumer implementation
 */
public class PlanInformationManagementConsumerServiceImpl extends ConsumerServiceImpl {

    private static final Logger LOGGER = Logger.getLogger(PlanInformationManagementConsumerServiceImpl.class.getName());

    private PlanInformationManagementStub pimService = null;
    private COMServicesConsumer comServices;

    public COMServicesConsumer getCOMServices() {
        return this.comServices;
    }

    @Override
    public Object getStub() {
        return this.getPlanInformationManagementStub();
    }

    public PlanInformationManagementStub getPlanInformationManagementStub() {
        return this.pimService;
    }

    @Override
    public Object generateServiceStub(MALConsumer tmConsumer) {
        return new PlanInformationManagementStub(tmConsumer);
    }

    public PlanInformationManagementConsumerServiceImpl(SingleConnectionDetails connectionDetails,
        COMServicesConsumer comServices) throws MALException, MalformedURLException {
        this.connectionDetails = connectionDetails;
        this.comServices = comServices;

        // Close old connection
        if (tmConsumer != null) {
            try {
                tmConsumer.close();
            } catch (MALException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }

        tmConsumer = connection.startService(this.connectionDetails.getProviderURI(), this.connectionDetails
            .getBrokerURI(), this.connectionDetails.getDomain(),
            PlanInformationManagementHelper.PLANINFORMATIONMANAGEMENT_SERVICE);

        this.pimService = new PlanInformationManagementStub(tmConsumer);
    }
}
