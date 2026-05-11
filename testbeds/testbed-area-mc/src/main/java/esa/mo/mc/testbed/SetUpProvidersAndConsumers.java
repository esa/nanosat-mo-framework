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
package esa.mo.mc.testbed;

import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.mc.impl.consumer.ActionConsumerServiceImpl;
import esa.mo.mc.impl.consumer.AggregationConsumerServiceImpl;
import esa.mo.mc.impl.consumer.AlertConsumerServiceImpl;
import esa.mo.mc.impl.consumer.ParameterConsumerServiceImpl;
import esa.mo.mc.impl.provider.ActionProviderServiceImpl;
import esa.mo.mc.testbed.backends.Backend;
import esa.mo.mc.impl.provider.AggregationProviderServiceImpl;
import esa.mo.mc.impl.provider.AlertProviderServiceImpl;
import esa.mo.mc.impl.provider.ParameterManager;
import esa.mo.mc.impl.provider.ParameterProviderServiceImpl;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionProvider;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperMisc;

/**
 * Manages the lifecycle of MC service providers and consumers for the testbed.
 * Call {@link #setUp} before tests and {@link #tearDown} after.
 *
 * @author Cesar Coelho
 */
public class SetUpProvidersAndConsumers {

    private static final Logger LOGGER = Logger.getLogger(SetUpProvidersAndConsumers.class.getName());

    private static COMServicesProvider comServicesProvider = null;
    private static COMServicesConsumer comServicesConsumer = null;

    private static ActionProviderServiceImpl actionProvider = null;
    private static ActionConsumerServiceImpl actionConsumerStub = null;

    private static AlertProviderServiceImpl alertProvider = null;
    private static AlertConsumerServiceImpl alertConsumerStub = null;

    private static ParameterProviderServiceImpl parameterProvider = null;
    private static ParameterConsumerServiceImpl parameterConsumerStub = null;
    private static ParameterManager parameterManager = null;

    private static AggregationProviderServiceImpl aggregationProvider = null;
    private static AggregationConsumerServiceImpl aggregationConsumerStub = null;

    public void setUp(boolean startAction, boolean startAlert,
            boolean startParameter, boolean startAggregation, Backend backend) throws IOException {
        HelperMisc.loadPropertiesFile();
        ConnectionProvider.resetURILinksFile();

        try {
            NMFMCServicesFactory factory = new NMFMCServicesFactory();

            comServicesProvider = new COMServicesProvider();
            comServicesProvider.init();

            comServicesConsumer = new COMServicesConsumer();

            if (startAction) {
                actionProvider = factory.createProviderAction(comServicesProvider, backend);
                SingleConnectionDetails details = actionProvider.getConnectionProvider().getConnectionDetails();
                actionConsumerStub = factory.createConsumerStubAction(details, comServicesConsumer);
            }

            if (startAlert) {
                alertProvider = factory.createProviderAlert(comServicesProvider);
                SingleConnectionDetails details = alertProvider.getConnectionProvider().getConnectionDetails();
                alertConsumerStub = factory.createConsumerStubAlert(details, comServicesConsumer);
            }

            if (startParameter || startAggregation) {
                parameterManager = new ParameterManager(comServicesProvider, backend);
            }

            if (startParameter) {
                parameterProvider = factory.createProviderParameter(comServicesProvider, parameterManager);
                SingleConnectionDetails details = parameterProvider.getConnectionProvider().getConnectionDetails();
                parameterConsumerStub = factory.createConsumerStubParameter(details, comServicesConsumer);
            }

            if (startAggregation) {
                aggregationProvider = factory.createProviderAggregation(comServicesProvider, parameterManager);
                SingleConnectionDetails details = aggregationProvider.getConnectionProvider().getConnectionDetails();
                aggregationConsumerStub = factory.createConsumerStubAggregation(details, comServicesConsumer);
            }

        } catch (MALException | MALInteractionException | java.net.MalformedURLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void tearDown() throws IOException {
        if (actionProvider != null) {
            actionProvider.getConnectionProvider().closeAll();
        }
        if (alertProvider != null) {
            alertProvider.getConnectionProvider().closeAll();
        }
        if (parameterProvider != null) {
            parameterProvider.getConnectionProvider().closeAll();
        }
        if (aggregationProvider != null) {
            aggregationProvider.getConnectionProvider().closeAll();
        }
        if (comServicesProvider != null) {
            comServicesProvider.closeAll();
        }
    }

    public COMServicesProvider getCOMServicesProvider() {
        return comServicesProvider;
    }

    public COMServicesConsumer getCOMServicesConsumer() {
        return comServicesConsumer;
    }

    public ActionProviderServiceImpl getActionProvider() {
        return actionProvider;
    }

    public ActionConsumerServiceImpl getActionConsumerStub() {
        return actionConsumerStub;
    }

    public AlertProviderServiceImpl getAlertProvider() {
        return alertProvider;
    }

    public AlertConsumerServiceImpl getAlertConsumerStub() {
        return alertConsumerStub;
    }

    public ParameterProviderServiceImpl getParameterProvider() {
        return parameterProvider;
    }

    public ParameterConsumerServiceImpl getParameterConsumerStub() {
        return parameterConsumerStub;
    }

    public AggregationProviderServiceImpl getAggregationProvider() {
        return aggregationProvider;
    }

    public AggregationConsumerServiceImpl getAggregationConsumerStub() {
        return aggregationConsumerStub;
    }

}
