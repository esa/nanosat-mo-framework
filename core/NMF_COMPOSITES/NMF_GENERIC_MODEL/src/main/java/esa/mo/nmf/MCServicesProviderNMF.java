/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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
package esa.mo.nmf;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.mc.impl.interfaces.ActionInvocationListener;
import esa.mo.mc.impl.interfaces.ParameterStatusListener;
import esa.mo.mc.impl.provider.ActionProviderServiceImpl;
import esa.mo.mc.impl.provider.AggregationProviderServiceImpl;
import esa.mo.mc.impl.provider.AlertProviderServiceImpl;
import esa.mo.mc.impl.provider.ParameterManager;
import esa.mo.mc.impl.provider.ParameterProviderServiceImpl;
import org.ccsds.moims.mo.mal.MALException;

/**
 *
 *
 */
public class MCServicesProviderNMF {

    private ParameterManager parameterManager;

    private final ActionProviderServiceImpl actionService = new ActionProviderServiceImpl();
    private final ParameterProviderServiceImpl parameterService = new ParameterProviderServiceImpl();
    private final AlertProviderServiceImpl alertService = new AlertProviderServiceImpl();
    private final AggregationProviderServiceImpl aggregationService = new AggregationProviderServiceImpl();

    public void init(COMServicesProvider comServices, ActionInvocationListener actions,
            ParameterStatusListener monitoringParameters) throws MALException {
        parameterManager = new ParameterManager(comServices, monitoringParameters);
        parameterService.init(parameterManager);
        actionService.init(comServices, actions);
        alertService.init(comServices);
        aggregationService.init(comServices, parameterManager);
    }

    public void init(final COMServicesProvider comServices,
            final MonitorAndControlNMFAdapter adapter) throws MALException {
        parameterManager = new ParameterManager(comServices, adapter);
        parameterService.init(parameterManager);
        actionService.init(comServices, adapter);
        alertService.init(comServices);
        aggregationService.init(comServices, parameterManager);
    }

    public ActionProviderServiceImpl getActionService() {
        return this.actionService;
    }

    public ParameterProviderServiceImpl getParameterService() {
        return this.parameterService;
    }

    public AlertProviderServiceImpl getAlertService() {
        return this.alertService;
    }

    public AggregationProviderServiceImpl getAggregationService() {
        return this.aggregationService;
    }
}
