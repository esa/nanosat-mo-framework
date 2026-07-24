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
 * The Monitor and Control provider services to be available on the NMF
 * providers. This includes Action service, Parameter service, Alert service,
 * and Aggregation service.
 */
public class MCServicesProviderNMF {

    private final ActionProviderServiceImpl actionService = new ActionProviderServiceImpl();
    private final ParameterProviderServiceImpl parameterService = new ParameterProviderServiceImpl();
    private final AlertProviderServiceImpl alertService = new AlertProviderServiceImpl();
    private final AggregationProviderServiceImpl aggregationService = new AggregationProviderServiceImpl();
    private ParameterManager parameterManager;

    /**
     * Creates the Monitor and Control services holder. The services must be initialized
     * with one of the {@code init} methods before use.
     */
    public MCServicesProviderNMF() {
    }

    /**
     * Initializes the Monitor and Control services using separate action and parameter
     * listeners.
     *
     * @param comServices the COM services stack backing the M&amp;C services
     * @param actions the listener handling action invocations
     * @param monitoringParameters the listener providing parameter values
     * @throws MALException if any of the services fails to initialize
     */
    public void init(COMServicesProvider comServices, ActionInvocationListener actions,
            ParameterStatusListener monitoringParameters) throws MALException {
        parameterManager = new ParameterManager(comServices, monitoringParameters);
        parameterService.init(parameterManager);
        actionService.init(comServices, actions);
        alertService.init(comServices);
        aggregationService.init(comServices, parameterManager);
    }

    /**
     * Initializes the Monitor and Control services using a single Monitor and Control
     * adapter that handles both actions and parameters.
     *
     * @param comServices the COM services stack backing the M&amp;C services
     * @param adapter the Monitor and Control adapter handling actions and parameters
     * @throws MALException if any of the services fails to initialize
     */
    public void init(final COMServicesProvider comServices,
            final MonitorAndControlNMFAdapter adapter) throws MALException {
        parameterManager = new ParameterManager(comServices, adapter);
        parameterService.init(parameterManager);
        actionService.init(comServices, adapter);
        alertService.init(comServices);
        aggregationService.init(comServices, parameterManager);
    }

    /**
     * Returns the Action service provider.
     *
     * @return the Action service
     */
    public ActionProviderServiceImpl getActionService() {
        return this.actionService;
    }

    /**
     * Returns the Parameter service provider.
     *
     * @return the Parameter service
     */
    public ParameterProviderServiceImpl getParameterService() {
        return this.parameterService;
    }

    /**
     * Returns the Alert service provider.
     *
     * @return the Alert service
     */
    public AlertProviderServiceImpl getAlertService() {
        return this.alertService;
    }

    /**
     * Returns the Aggregation service provider.
     *
     * @return the Aggregation service
     */
    public AggregationProviderServiceImpl getAggregationService() {
        return this.aggregationService;
    }
}
