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
package esa.mo.mc.impl.util;

import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.mc.impl.consumer.ActionConsumerServiceImpl;
import esa.mo.mc.impl.consumer.AggregationConsumerServiceImpl;
import esa.mo.mc.impl.consumer.AlertConsumerServiceImpl;
import esa.mo.mc.impl.consumer.CheckConsumerServiceImpl;
import esa.mo.mc.impl.consumer.ParameterConsumerServiceImpl;
import esa.mo.mc.impl.consumer.StatisticConsumerServiceImpl;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mc.action.ActionHelper;
import org.ccsds.moims.mo.mc.aggregation.AggregationHelper;
import org.ccsds.moims.mo.mc.alert.AlertHelper;
import org.ccsds.moims.mo.mc.check.CheckHelper;
import org.ccsds.moims.mo.mc.parameter.ParameterHelper;
import org.ccsds.moims.mo.mc.statistic.StatisticHelper;

/**
 *
 *
 */
public class MCServicesConsumer {

    private ActionConsumerServiceImpl actionService;
    private ParameterConsumerServiceImpl parameterService;
    private AlertConsumerServiceImpl alertService;
    private CheckConsumerServiceImpl checkService;
    private StatisticConsumerServiceImpl statisticService;
    private AggregationConsumerServiceImpl aggregationService;

    /**
     * Initializes the Monitor and Control services
     *
     * @param connectionConsumer Connection details
     * @param comServices COM services
     */
    public void init(ConnectionConsumer connectionConsumer, COMServicesConsumer comServices) {
        init(connectionConsumer, comServices, null, null);
    }

    /**
     * Initializes the Monitor and Control services
     *
     * @param connectionConsumer Connection details
     * @param comServices COM services
     * @param authenticationId authenticationId of the logged in user
     */
    public void init(ConnectionConsumer connectionConsumer, COMServicesConsumer comServices, Blob authenticationId,
        String localNamePrefix) {
        SingleConnectionDetails details;

        try {
            // Initialize the Action service
            details = connectionConsumer.getServicesDetails().get(ActionHelper.ACTION_SERVICE_NAME);
            if (details != null) {
                actionService = new ActionConsumerServiceImpl(details, comServices, authenticationId, localNamePrefix);
            }

            // Initialize the Parameter service
            details = connectionConsumer.getServicesDetails().get(ParameterHelper.PARAMETER_SERVICE_NAME);
            if (details != null) {
                parameterService = new ParameterConsumerServiceImpl(details, comServices, authenticationId,
                    localNamePrefix);
            }

            // Initialize the Alert service
            details = connectionConsumer.getServicesDetails().get(AlertHelper.ALERT_SERVICE_NAME);
            if (details != null) {
                alertService = new AlertConsumerServiceImpl(details, comServices, authenticationId, localNamePrefix);
            }

            // Initialize the Check service
            details = connectionConsumer.getServicesDetails().get(CheckHelper.CHECK_SERVICE_NAME);
            if (details != null) {
                checkService = new CheckConsumerServiceImpl(details, comServices, authenticationId, localNamePrefix);
            }

            // Initialize the Statistic service
            details = connectionConsumer.getServicesDetails().get(StatisticHelper.STATISTIC_SERVICE_NAME);
            if (details != null) {
                statisticService = new StatisticConsumerServiceImpl(details, comServices, authenticationId,
                    localNamePrefix);
            }

            // Initialize the Aggregation service
            details = connectionConsumer.getServicesDetails().get(AggregationHelper.AGGREGATION_SERVICE_NAME);
            if (details != null) {
                aggregationService = new AggregationConsumerServiceImpl(details, comServices, authenticationId,
                    localNamePrefix);
            }
        } catch (MALException | MALInteractionException | MalformedURLException ex) {
            Logger.getLogger(COMServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ActionConsumerServiceImpl getActionService() {
        return this.actionService;
    }

    public ParameterConsumerServiceImpl getParameterService() {
        return this.parameterService;
    }

    public AlertConsumerServiceImpl getAlertService() {
        return this.alertService;
    }

    public CheckConsumerServiceImpl getCheckService() {
        return this.checkService;
    }

    public StatisticConsumerServiceImpl getStatisticService() {
        return this.statisticService;
    }

    public AggregationConsumerServiceImpl getAggregationService() {
        return this.aggregationService;
    }

    public void setServices(ActionConsumerServiceImpl actionService, ParameterConsumerServiceImpl parameterService,
        AlertConsumerServiceImpl alertService, CheckConsumerServiceImpl checkService,
        StatisticConsumerServiceImpl statisticService, AggregationConsumerServiceImpl aggregationService) {
        this.actionService = actionService;
        this.parameterService = parameterService;
        this.alertService = alertService;
        this.checkService = checkService;
        this.statisticService = statisticService;
        this.aggregationService = aggregationService;
    }

    public void setActionService(ActionConsumerServiceImpl actionService) {
        this.actionService = actionService;
    }

    public void setParameterService(ParameterConsumerServiceImpl parameterService) {
        this.parameterService = parameterService;
    }

    public void setAlertService(AlertConsumerServiceImpl alertService) {
        this.alertService = alertService;
    }

    public void setCheckService(CheckConsumerServiceImpl checkService) {
        this.checkService = checkService;
    }

    public void setStatisticService(StatisticConsumerServiceImpl statisticService) {
        this.statisticService = statisticService;
    }

    public void setAggregationService(AggregationConsumerServiceImpl aggregationService) {
        this.aggregationService = aggregationService;
    }

    /**
     * Closes the service consumer connections
     *
     */
    public void closeConnections() {
        if (this.actionService != null) {
            this.actionService.close();
        }

        if (this.parameterService != null) {
            this.parameterService.close();
        }

        if (this.alertService != null) {
            this.alertService.close();
        }

        if (this.checkService != null) {
            this.checkService.close();
        }

        if (this.statisticService != null) {
            this.statisticService.close();
        }

        if (this.aggregationService != null) {
            this.aggregationService.close();
        }
    }

    public void setAuthenticationId(Blob authenticationId) {
        if (this.actionService != null) {
            this.actionService.setAuthenticationId(authenticationId);
        }

        if (this.parameterService != null) {
            this.parameterService.setAuthenticationId(authenticationId);
        }

        if (this.alertService != null) {
            this.alertService.setAuthenticationId(authenticationId);
        }

        if (this.checkService != null) {
            this.checkService.setAuthenticationId(authenticationId);
        }

        if (this.statisticService != null) {
            this.statisticService.setAuthenticationId(authenticationId);
        }

        if (this.aggregationService != null) {
            this.aggregationService.setAuthenticationId(authenticationId);
        }
    }
}
