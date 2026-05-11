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
import esa.mo.mc.impl.interfaces.ActionInvocationListener;
import esa.mo.mc.impl.interfaces.ParameterStatusListener;
import esa.mo.mc.impl.provider.ActionProviderServiceImpl;
import esa.mo.mc.impl.provider.AggregationProviderServiceImpl;
import esa.mo.mc.impl.provider.AlertProviderServiceImpl;
import esa.mo.mc.impl.provider.ParameterManager;
import esa.mo.mc.impl.provider.ParameterProviderServiceImpl;
import java.net.MalformedURLException;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;

/**
 * Creates MC service providers and consumer stubs for the testbed.
 *
 * @author Cesar Coelho
 */
public class NMFMCServicesFactory {

    public ActionProviderServiceImpl createProviderAction(COMServicesProvider comServices,
            ActionInvocationListener listener) throws MALException {
        ActionProviderServiceImpl provider = new ActionProviderServiceImpl();
        provider.init(comServices, listener);
        return provider;
    }

    public AlertProviderServiceImpl createProviderAlert(COMServicesProvider comServices) throws MALException {
        AlertProviderServiceImpl provider = new AlertProviderServiceImpl();
        provider.init(comServices);
        return provider;
    }

    public ParameterProviderServiceImpl createProviderParameter(COMServicesProvider comServices,
            ParameterStatusListener listener) throws MALException {
        ParameterManager manager = new ParameterManager(comServices, listener);
        ParameterProviderServiceImpl provider = new ParameterProviderServiceImpl();
        provider.init(manager);
        return provider;
    }

    public AggregationProviderServiceImpl createProviderAggregation(COMServicesProvider comServices,
            ParameterManager parameterManager) throws MALException {
        AggregationProviderServiceImpl provider = new AggregationProviderServiceImpl();
        provider.init(comServices, parameterManager);
        return provider;
    }

    public ActionConsumerServiceImpl createConsumerStubAction(SingleConnectionDetails details,
            COMServicesConsumer comServices) throws MALException, MalformedURLException, MALInteractionException {
        return new ActionConsumerServiceImpl(details, comServices);
    }

    public AlertConsumerServiceImpl createConsumerStubAlert(SingleConnectionDetails details,
            COMServicesConsumer comServices) throws MALException, MalformedURLException, MALInteractionException {
        return new AlertConsumerServiceImpl(details, comServices);
    }

    public ParameterConsumerServiceImpl createConsumerStubParameter(SingleConnectionDetails details,
            COMServicesConsumer comServices) throws MALException, MalformedURLException, MALInteractionException {
        return new ParameterConsumerServiceImpl(details, comServices);
    }

    public AggregationConsumerServiceImpl createConsumerStubAggregation(SingleConnectionDetails details,
            COMServicesConsumer comServices) throws MALException, MalformedURLException, MALInteractionException {
        return new AggregationConsumerServiceImpl(details, comServices);
    }

}
