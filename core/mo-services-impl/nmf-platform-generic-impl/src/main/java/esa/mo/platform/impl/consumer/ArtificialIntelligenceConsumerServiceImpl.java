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
package esa.mo.platform.impl.consumer;

import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.helpertools.misc.ConsumerServiceImpl;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.platform.artificialintelligence.ArtificialIntelligenceHelper;
import org.ccsds.moims.mo.platform.artificialintelligence.consumer.ArtificialIntelligenceStub;

/**
 *
 * @author Cesar Coelho
 */
public class ArtificialIntelligenceConsumerServiceImpl extends ConsumerServiceImpl {

    private ArtificialIntelligenceStub aiService = null;
    private COMServicesConsumer comServices;

    public COMServicesConsumer getCOMServices() {
        return comServices;
    }

    @Override
    public Object getStub() {
        return this.getArtificialIntelligenceStub();
    }

    public ArtificialIntelligenceStub getArtificialIntelligenceStub() {
        return this.aiService;
    }

    @Override
    public Object generateServiceStub(MALConsumer tmConsumer) {
        return new ArtificialIntelligenceStub(tmConsumer);
    }

    public ArtificialIntelligenceConsumerServiceImpl(SingleConnectionDetails connectionDetails,
            COMServicesConsumer comServices)
            throws MALException, MalformedURLException, MALInteractionException {
        this(connectionDetails, comServices, null, null);
    }

    public ArtificialIntelligenceConsumerServiceImpl(SingleConnectionDetails connectionDetails,
            COMServicesConsumer comServices, Blob authenticationId,
            String localNamePrefix) throws MALException, MalformedURLException,
            MALInteractionException {
        this.connectionDetails = connectionDetails;
        this.comServices = comServices;

        // Close old connection
        if (tmConsumer != null) {
            try {
                tmConsumer.close();
            } catch (MALException ex) {
                Logger.getLogger(ArtificialIntelligenceConsumerServiceImpl.class.getName()).log(Level.SEVERE,
                        "The consumer connection could not be closed!", ex);
            }
        }

        tmConsumer = connection.startService(
                this.connectionDetails.getProviderURI(),
                this.connectionDetails.getBrokerURI(),
                this.connectionDetails.getDomain(),
                ArtificialIntelligenceHelper.ARTIFICIALINTELLIGENCE_SERVICE,
                authenticationId, localNamePrefix);

        this.aiService = new ArtificialIntelligenceStub(tmConsumer);
    }

}
