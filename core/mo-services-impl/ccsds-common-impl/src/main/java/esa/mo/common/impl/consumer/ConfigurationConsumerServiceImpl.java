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
package esa.mo.common.impl.consumer;

import esa.mo.com.impl.util.COMServicesConsumer;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.common.configuration.ConfigurationHelper;
import org.ccsds.moims.mo.common.configuration.consumer.ConfigurationStub;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.helpertools.misc.ConsumerServiceImpl;
import org.ccsds.moims.mo.mal.structures.Blob;

/**
 * @author Cesar Coelho
 */
public class ConfigurationConsumerServiceImpl extends ConsumerServiceImpl {

    private ConfigurationStub configurationService = null;

    private COMServicesConsumer comServices;

    public ConfigurationConsumerServiceImpl(SingleConnectionDetails connectionDetails, COMServicesConsumer comServices)
        throws MALException, MalformedURLException, MALInteractionException {
        this(connectionDetails, comServices, null, null);
    }

    public ConfigurationConsumerServiceImpl(SingleConnectionDetails connectionDetails, COMServicesConsumer comServices,
        Blob authenticationId, String localNamePrefix) throws MALException, MalformedURLException,
        MALInteractionException {
        
        this.connectionDetails = connectionDetails;
        this.comServices = comServices;

        // Close old connection
        if (tmConsumer != null) {
            try {
                tmConsumer.close();
            } catch (MALException ex) {
                Logger.getLogger(ConfigurationConsumerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        tmConsumer = connection.startService(
                this.connectionDetails.getProviderURI(),
                this.connectionDetails.getBrokerURI(),
                this.connectionDetails.getDomain(),
                ConfigurationHelper.CONFIGURATION_SERVICE);

        this.configurationService = new ConfigurationStub(tmConsumer);
    }

    public COMServicesConsumer getCOMServices() {
        return comServices;
    }

    @Override
    public Object generateServiceStub(MALConsumer tmConsumer) {
        return new ConfigurationStub(tmConsumer);
    }

    @Override
    public Object getStub() {
        return this.getConfigurationStub();
    }

    public ConfigurationStub getConfigurationStub() {
        return this.configurationService;
    }

}
