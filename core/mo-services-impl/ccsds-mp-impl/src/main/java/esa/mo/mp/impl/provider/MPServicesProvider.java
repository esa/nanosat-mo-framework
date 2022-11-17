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
package esa.mo.mp.impl.provider;

import org.ccsds.moims.mo.mal.MALException;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.mp.impl.api.MPArchiveManager;
import esa.mo.mp.impl.callback.MPServiceOperationManager;
import esa.mo.mp.impl.exec.activity.ActivityExecutionEngine;

/**
 * The Mission Planning provider services to be available on the NMF
 * providers.
 */
public class MPServicesProvider {

    private final MPServiceOperationManager operationCallbackManager = new MPServiceOperationManager();
    private MPArchiveManager archiveManager;
    private ActivityExecutionEngine activityExecutionEngine;

    private final PlanInformationManagementProviderServiceImpl pimService = new PlanInformationManagementProviderServiceImpl();
    private final PlanningRequestProviderServiceImpl planningRequestService = new PlanningRequestProviderServiceImpl();
    private final PlanDistributionProviderServiceImpl planDistributionService = new PlanDistributionProviderServiceImpl();
    private final PlanEditProviderServiceImpl planEditService = new PlanEditProviderServiceImpl();
    private final PlanExecutionControlProviderServiceImpl planExecutionControlService = new PlanExecutionControlProviderServiceImpl();

    public void init(COMServicesProvider comServices) throws MALException {
        archiveManager = new MPArchiveManager(comServices);
        activityExecutionEngine = new ActivityExecutionEngine();
        this.pimService.init(comServices, archiveManager, operationCallbackManager);
        this.planningRequestService.init(comServices, archiveManager, operationCallbackManager);
        this.planDistributionService.init(comServices, archiveManager, operationCallbackManager);
        this.planEditService.init(comServices, archiveManager, operationCallbackManager);
        this.planExecutionControlService.init(comServices, archiveManager, operationCallbackManager,
            activityExecutionEngine);
    }

    public MPServiceOperationManager getOperationCallbackManager() {
        return this.operationCallbackManager;
    }

    public MPArchiveManager getArchiveManager() {
        return this.archiveManager;
    }

    public ActivityExecutionEngine getActivityExecutionEngine() {
        return this.activityExecutionEngine;
    }

    public PlanInformationManagementProviderServiceImpl getPlanInformationManagementService() {
        return this.pimService;
    }

    public PlanningRequestProviderServiceImpl getPlanningRequestService() {
        return this.planningRequestService;
    }

    public PlanDistributionProviderServiceImpl getPlanDistributionService() {
        return this.planDistributionService;
    }

    public PlanEditProviderServiceImpl getPlanEditService() {
        return this.planEditService;
    }

    public PlanExecutionControlProviderServiceImpl getPlanExecutionControlService() {
        return this.planExecutionControlService;
    }
}
