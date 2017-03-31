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
package esa.mo.mc.impl.util;

import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.mc.impl.interfaces.ActionInvocationListener;
import esa.mo.mc.impl.interfaces.AggregationTriggerListener;
import esa.mo.mc.impl.interfaces.ExternalStatisticFunctionsInterface;
import esa.mo.mc.impl.interfaces.ParameterStatusListener;
import esa.mo.mc.impl.interfaces.ParameterTestStatusListener;
import esa.mo.mc.impl.provider.ActionProviderServiceImpl;
import esa.mo.mc.impl.provider.AggregationProviderServiceImpl;
import esa.mo.mc.impl.provider.AlertProviderServiceImpl;
import esa.mo.mc.impl.provider.CheckManager;
import esa.mo.mc.impl.provider.CheckProviderServiceImpl;
import esa.mo.mc.impl.provider.ParameterManager;
import esa.mo.mc.impl.provider.ParameterProviderServiceImpl;
import esa.mo.mc.impl.provider.StatisticProviderServiceImpl;
import esa.mo.mc.impl.provider.mcprototype.action.ActionTestProviderServiceImpl;
import esa.mo.mc.impl.provider.mcprototype.aggregation.AggregationTestProviderServiceImpl;
import esa.mo.mc.impl.provider.mcprototype.alert.AlertTestProviderServiceImpl;
import esa.mo.mc.impl.provider.mcprototype.parameter.ParameterTestProviderServiceImpl;
import esa.mo.mc.impl.provider.mcprototype.statistic.StatisticTestProviderServiceImpl;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;

public class MCServicesProvider {

    private ParameterManager parameterManager;

    private final ActionProviderServiceImpl actionService = new ActionProviderServiceImpl();
	private final ActionTestProviderServiceImpl actionTestService = new ActionTestProviderServiceImpl();
    private final ParameterProviderServiceImpl parameterService = new ParameterProviderServiceImpl();
    private final ParameterTestProviderServiceImpl parameterTestService = new ParameterTestProviderServiceImpl();
    private final AlertProviderServiceImpl alertService = new AlertProviderServiceImpl();
	private final AlertTestProviderServiceImpl alertTestService = new AlertTestProviderServiceImpl();
    private final CheckProviderServiceImpl checkService = new CheckProviderServiceImpl();
    private final StatisticProviderServiceImpl statisticService = new StatisticProviderServiceImpl();
	private final StatisticTestProviderServiceImpl statisticTestService = new StatisticTestProviderServiceImpl();
    private final AggregationProviderServiceImpl aggregationService = new AggregationProviderServiceImpl();
    private final AggregationTestProviderServiceImpl aggregationTestService = new AggregationTestProviderServiceImpl();

    public void init(COMServicesProvider comServices, ActionInvocationListener actions,
            ParameterTestStatusListener monitoringParameters, AggregationTriggerListener aggregationTriggerListener, ExternalStatisticFunctionsInterface statisticFunctions) {

        try {
            parameterManager = new ParameterManager(comServices, monitoringParameters);
            parameterService.init(parameterManager);
            parameterTestService.init(monitoringParameters);
            actionService.init(comServices, actions);
			actionTestService.init(actionService, comServices, actions);
            alertService.init(comServices);
			alertTestService.init(alertService, comServices);
            statisticService.init(comServices, parameterManager, statisticFunctions);
			statisticTestService.init(statisticService, comServices, parameterService);
            aggregationService.init(comServices, parameterManager);
            aggregationTestService.init(aggregationTriggerListener);
            
            // init services as consumers sothat the provider-services can use them. see JavaDoc of function for more information.
            MCServicesConsumer mcServiceConsumer = initAsConsumers();
            checkService.init(comServices, mcServiceConsumer, parameterManager);

        } catch (MALException ex) {
            Logger.getLogger(MCServicesProvider.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * init services as consumers sothat the provider-services can use them. in
     * this case the ParameterService is needed for monitoring parameter-values
     * in the CheckService (CheckOnChange-Function). You could use another
     * method of monitoring parameters but once you have the parameter-service,
     * it makes most sense to use it.
     *
     * @return
     */
    private MCServicesConsumer initAsConsumers() {

        final MCServicesConsumer mcServiceConsumer = new MCServicesConsumer();
        try {
            ConnectionConsumer connCons = new ConnectionConsumer();
            connCons.setServicesDetails(connCons.getServicesDetails().loadURIFromFiles());
            final COMServicesConsumer comServicesConsumer = new COMServicesConsumer();
            comServicesConsumer.init(connCons);
            mcServiceConsumer.init(connCons, comServicesConsumer);
        } catch (MalformedURLException ex) {
            Logger.getLogger(CheckManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mcServiceConsumer;
    }

    public ActionProviderServiceImpl getActionService() {
        return this.actionService;
    }

    public ParameterProviderServiceImpl getParameterService() {
        return this.parameterService;
    }

    /**
     * only needed for test purposes
     *
     * @return
     */
    public ParameterTestProviderServiceImpl getParameterTestService() {
        return this.parameterTestService;
    }

    public AlertProviderServiceImpl getAlertService() {
        return this.alertService;
    }

    public CheckProviderServiceImpl getCheckService() {
        return this.checkService;
    }

    public StatisticProviderServiceImpl getStatisticService() {
        return this.statisticService;
    }

    public AggregationProviderServiceImpl getAggregationService() {
        return this.aggregationService;
    }

    /**
     * only needed for test purposes
     *
     * @return
     */
    public AggregationTestProviderServiceImpl getAggregationTestService() {
        return this.aggregationTestService;
    }

    public AlertTestProviderServiceImpl getAlertTestService() {
        return this.alertTestService;
    }

    public ActionTestProviderServiceImpl getActionTestService() {
        return this.actionTestService;
    }

    public StatisticTestProviderServiceImpl getStatisticTestService() {
        return this.statisticTestService;
    }
}
