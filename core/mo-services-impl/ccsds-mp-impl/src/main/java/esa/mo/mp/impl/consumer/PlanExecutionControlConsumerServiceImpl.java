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

import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mp.MPHelper;
import org.ccsds.moims.mo.mp.planexecutioncontrol.PlanExecutionControlHelper;
import org.ccsds.moims.mo.mp.planexecutioncontrol.consumer.PlanExecutionControlStub;
import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.misc.ConsumerServiceImpl;

/**
 * Plan Execution Control (PEC) Service consumer implementation
 */
public class PlanExecutionControlConsumerServiceImpl extends ConsumerServiceImpl {

    private static final Logger LOGGER = Logger.getLogger(PlanExecutionControlConsumerServiceImpl.class.getName());

    private PlanExecutionControlStub planExecutionControlService = null;
    private COMServicesConsumer comServices;

    public COMServicesConsumer getCOMServices() {
        return this.comServices;
    }

    @Override
    public Object getStub() {
        return this.getPlanExecutionControlStub();
    }

    public PlanExecutionControlStub getPlanExecutionControlStub() {
        return this.planExecutionControlService;
    }

    @Override
    public Object generateServiceStub(MALConsumer tmConsumer) {
        return new PlanExecutionControlStub(tmConsumer);
    }

    public PlanExecutionControlConsumerServiceImpl(SingleConnectionDetails connectionDetails,
        COMServicesConsumer comServices) throws MALException, MalformedURLException {
        if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
            MALHelper.init(MALContextFactory.getElementFactoryRegistry());
        }

        if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
            COMHelper.init(MALContextFactory.getElementFactoryRegistry());
        }

        if (MALContextFactory.lookupArea(MPHelper.MP_AREA_NAME, MPHelper.MP_AREA_VERSION) == null) {
            MPHelper.init(MALContextFactory.getElementFactoryRegistry());
        }

        try {
            PlanExecutionControlHelper.init(MALContextFactory.getElementFactoryRegistry());
        } catch (MALException ex) {
            // nothing to be done..
        }

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
            PlanExecutionControlHelper.PLANEXECUTIONCONTROL_SERVICE);

        this.planExecutionControlService = new PlanExecutionControlStub(tmConsumer);
    }
}
