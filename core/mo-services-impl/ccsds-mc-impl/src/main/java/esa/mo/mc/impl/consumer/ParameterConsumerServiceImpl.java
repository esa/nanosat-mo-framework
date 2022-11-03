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
package esa.mo.mc.impl.consumer;

import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.helpertools.misc.ConsumerServiceImpl;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.aggregation.AggregationHelper;
import org.ccsds.moims.mo.mc.parameter.ParameterHelper;
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterStub;

/**
 *
 * @author Cesar Coelho
 */
public class ParameterConsumerServiceImpl extends ConsumerServiceImpl {

    private ParameterStub parameterService = null;
    private COMServicesConsumer comServices;

    public COMServicesConsumer getCOMServices() {
        return comServices;
    }

    @Override
    public Object getStub() {
        return this.getParameterStub();
    }

    public ParameterStub getParameterStub() {
        return this.parameterService;
    }

    @Override
    public Object generateServiceStub(final MALConsumer tmConsumer) {
        return new ParameterStub(tmConsumer);
    }

    public ParameterConsumerServiceImpl(final SingleConnectionDetails connectionDetails, final COMServicesConsumer comServices) throws MALException, MalformedURLException, MALInteractionException {
        this(connectionDetails, comServices, null, null);
    }

    public ParameterConsumerServiceImpl(final SingleConnectionDetails connectionDetails, final COMServicesConsumer comServices,
                                        final Blob authenticationId,
                                        final String localNamePrefix) throws MALException, MalformedURLException, MALInteractionException {

        if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
            MALHelper.init(MALContextFactory.getElementFactoryRegistry());
        }

        if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
            COMHelper.init(MALContextFactory.getElementFactoryRegistry());
        }

        if (MALContextFactory.lookupArea(MCHelper.MC_AREA_NAME, MCHelper.MC_AREA_VERSION) == null) {
            MCHelper.init(MALContextFactory.getElementFactoryRegistry());
        }

        if (MALContextFactory.lookupArea(MCHelper.MC_AREA_NAME, MCHelper.MC_AREA_VERSION)
                    .getServiceByName(ParameterHelper.PARAMETER_SERVICE_NAME) == null) {
            ParameterHelper.init(MALContextFactory.getElementFactoryRegistry());
        }

        this.connectionDetails = connectionDetails;
        this.comServices = comServices;

        // Close old connection
        if (tmConsumer != null) {
            final Identifier subscriptionId = new Identifier("SUB");
            final IdentifierList subLst = new IdentifierList();
            subLst.add(subscriptionId);
            if (parameterService != null) {
                parameterService.monitorValueDeregister(subLst);
            }

            try {
                tmConsumer.close();
            } catch (final MALException ex) {
                Logger.getLogger(ParameterConsumerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        tmConsumer = connection.startService(
                this.connectionDetails.getProviderURI(),
                this.connectionDetails.getBrokerURI(),
                this.connectionDetails.getDomain(),
                ParameterHelper.PARAMETER_SERVICE,
                authenticationId, localNamePrefix);

        this.parameterService = new ParameterStub(tmConsumer);
    }

}
