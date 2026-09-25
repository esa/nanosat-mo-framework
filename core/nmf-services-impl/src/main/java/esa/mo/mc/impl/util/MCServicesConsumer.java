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
import esa.mo.mc.impl.consumer.ParameterConsumerServiceImpl;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mc.action.ActionServiceInfo;
import org.ccsds.moims.mo.mc.aggregation.AggregationServiceInfo;
import org.ccsds.moims.mo.mc.alert.AlertServiceInfo;
import org.ccsds.moims.mo.mc.parameter.ParameterServiceInfo;

/**
 * The MCServicesConsumer holds the MC service consumers.
 *
 */
public class MCServicesConsumer {

    private ActionConsumerServiceImpl actionService;
    private ParameterConsumerServiceImpl parameterService;
    private AlertConsumerServiceImpl alertService;
    private AggregationConsumerServiceImpl aggregationService;

    /**
     * Default constructor.
     */
    public MCServicesConsumer() {
    }

    /**
     * Initializes the Monitor and Control services.
     *
     * @param connectionConsumer The Connection details.
     * @param comServices The COM services.
     */
    public void init(ConnectionConsumer connectionConsumer, COMServicesConsumer comServices) {
        init(connectionConsumer, comServices, null, null);
    }

    /**
     * Initializes the Monitor and Control services.
     *
     * @param connectionConsumer The Connection details
     * @param comServices The COM services
     * @param authenticationId The authenticationId of the logged in user.
     * @param localNamePrefix The local name prefix.
     */
    public void init(ConnectionConsumer connectionConsumer,
            COMServicesConsumer comServices, Blob authenticationId, String localNamePrefix) {
        SingleConnectionDetails details;

        try {
            // Initialize the Action service
            details = connectionConsumer.getServicesDetails().get(ActionServiceInfo.ACTION_SERVICE_NAME);
            if (details != null) {
                actionService = new ActionConsumerServiceImpl(details, comServices, authenticationId, localNamePrefix);
            }

            // Initialize the Parameter service
            details = connectionConsumer.getServicesDetails().get(ParameterServiceInfo.PARAMETER_SERVICE_NAME);
            if (details != null) {
                parameterService = new ParameterConsumerServiceImpl(details, comServices, authenticationId,
                    localNamePrefix);
            }

            // Initialize the Alert service
            details = connectionConsumer.getServicesDetails().get(AlertServiceInfo.ALERT_SERVICE_NAME);
            if (details != null) {
                alertService = new AlertConsumerServiceImpl(details, comServices, authenticationId, localNamePrefix);
            }

            // Initialize the Aggregation service
            details = connectionConsumer.getServicesDetails().get(AggregationServiceInfo.AGGREGATION_SERVICE_NAME);
            if (details != null) {
                aggregationService = new AggregationConsumerServiceImpl(details, comServices, authenticationId,
                    localNamePrefix);
            }
        } catch (MALException | MALInteractionException | MalformedURLException ex) {
            Logger.getLogger(COMServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns the Action service consumer.
     *
     * @return the Action service consumer, or {@code null} if not initialized
     */
    public ActionConsumerServiceImpl getActionService() {
        return this.actionService;
    }

    /**
     * Returns the Parameter service consumer.
     *
     * @return the Parameter service consumer, or {@code null} if not initialized
     */
    public ParameterConsumerServiceImpl getParameterService() {
        return this.parameterService;
    }

    /**
     * Returns the Alert service consumer.
     *
     * @return the Alert service consumer, or {@code null} if not initialized
     */
    public AlertConsumerServiceImpl getAlertService() {
        return this.alertService;
    }

    /**
     * Returns the Aggregation service consumer.
     *
     * @return the Aggregation service consumer, or {@code null} if not initialized
     */
    public AggregationConsumerServiceImpl getAggregationService() {
        return this.aggregationService;
    }

    /**
     * Sets manually the MC service consumers.
     *
     * @param actionService the Action service consumer
     * @param parameterService the Parameter service consumer
     * @param alertService the Alert service consumer
     * @param aggregationService the Aggregation service consumer
     */
    public void setServices(ActionConsumerServiceImpl actionService, ParameterConsumerServiceImpl parameterService,
        AlertConsumerServiceImpl alertService, AggregationConsumerServiceImpl aggregationService) {
        this.actionService = actionService;
        this.parameterService = parameterService;
        this.alertService = alertService;
        this.aggregationService = aggregationService;
    }

    /**
     * Closes the service consumer connections
     *
     */
    public void closeConnections() {
        if (this.actionService != null) {
            this.actionService.closeConnection();
        }

        if (this.parameterService != null) {
            this.parameterService.closeConnection();
        }

        if (this.alertService != null) {
            this.alertService.closeConnection();
        }

        if (this.aggregationService != null) {
            this.aggregationService.closeConnection();
        }
    }

    /**
     * Propagates the authentication id to all the initialized MC service consumers.
     *
     * @param authenticationId the authentication id of the logged in user
     */
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

        if (this.aggregationService != null) {
            this.aggregationService.setAuthenticationId(authenticationId);
        }
    }
}
