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

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mp.plandistribution.PlanDistributionHelper;
import org.ccsds.moims.mo.mp.planedit.PlanEditHelper;
import org.ccsds.moims.mo.mp.planexecutioncontrol.PlanExecutionControlHelper;
import org.ccsds.moims.mo.mp.planinformationmanagement.PlanInformationManagementHelper;
import org.ccsds.moims.mo.mp.planningrequest.PlanningRequestHelper;

import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.SingleConnectionDetails;

/**
 * The Mission Planning consumer services to be available on the NMF
 * consumers.
 */
public class MPServicesConsumer {

    private static final Logger LOGGER = Logger.getLogger(MPServicesConsumer.class.getName());

    private PlanInformationManagementConsumerServiceImpl pimService;
    private PlanningRequestConsumerServiceImpl planningRequestService;
    private PlanDistributionConsumerServiceImpl planDistributionService;
    private PlanEditConsumerServiceImpl planEditService;
    private PlanExecutionControlConsumerServiceImpl planExecutionControlService;

    public void init(ConnectionConsumer connectionConsumer, COMServicesConsumer comServices) {
        SingleConnectionDetails details;

        try {
            // Initialize the Plan Information Management service
            details = connectionConsumer.getServicesDetails().get(
                PlanInformationManagementHelper.PLANINFORMATIONMANAGEMENT_SERVICE_NAME);
            if (details != null) {
                this.pimService = new PlanInformationManagementConsumerServiceImpl(details, comServices);
            }

            // Initialize the Planning Request service
            details = connectionConsumer.getServicesDetails().get(PlanningRequestHelper.PLANNINGREQUEST_SERVICE_NAME);
            if (details != null) {
                this.planningRequestService = new PlanningRequestConsumerServiceImpl(details, comServices);
            }

            // Initialize the Plan Distribution Service
            details = connectionConsumer.getServicesDetails().get(PlanDistributionHelper.PLANDISTRIBUTION_SERVICE_NAME);
            if (details != null) {
                this.planDistributionService = new PlanDistributionConsumerServiceImpl(details, comServices);
            }

            // Initialize the Plan Edit Service
            details = connectionConsumer.getServicesDetails().get(PlanEditHelper.PLANEDIT_SERVICE_NAME);
            if (details != null) {
                this.planEditService = new PlanEditConsumerServiceImpl(details, comServices);
            }

            // Initialize the Plan Execution Control Service
            details = connectionConsumer.getServicesDetails().get(
                PlanExecutionControlHelper.PLANEXECUTIONCONTROL_SERVICE_NAME);
            if (details != null) {
                this.planExecutionControlService = new PlanExecutionControlConsumerServiceImpl(details, comServices);
            }
        } catch (MALException | MalformedURLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public PlanInformationManagementConsumerServiceImpl getPlanInformationManagementService() {
        return this.pimService;
    }

    public PlanningRequestConsumerServiceImpl getPlanningRequestService() {
        return this.planningRequestService;
    }

    public PlanDistributionConsumerServiceImpl getPlanDistributionService() {
        return this.planDistributionService;
    }

    public PlanEditConsumerServiceImpl getPlanEditService() {
        return this.planEditService;
    }

    public PlanExecutionControlConsumerServiceImpl getPlanExecutionControlService() {
        return this.planExecutionControlService;
    }

    /**
     * Closes the service consumer connections
     */
    public void closeConnections() {
        if (this.pimService != null) {
            this.pimService.close();
        }

        if (this.planningRequestService != null) {
            this.planningRequestService.close();
        }

        if (this.planDistributionService != null) {
            this.planDistributionService.close();
        }

        if (this.planEditService != null) {
            this.planEditService.close();
        }

        if (this.planExecutionControlService != null) {
            this.planExecutionControlService.close();
        }
    }
}
