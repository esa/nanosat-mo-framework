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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.helpertools.misc.ConsumerServiceImpl;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.platform.softwaredefinedradio.SoftwareDefinedRadioHelper;
import org.ccsds.moims.mo.platform.softwaredefinedradio.consumer.SoftwareDefinedRadioStub;

/**
 * The SoftwareDefinedRadioConsumerServiceImpl class implements the service
 * consumer for the Software Defined Radio service.
 */
public class SoftwareDefinedRadioConsumerServiceImpl extends ConsumerServiceImpl {

    private SoftwareDefinedRadioStub softwareDefinedRadioService = null;
    private COMServicesConsumer comServices;

    /**
     * Creates the SoftwareDefinedRadio service consumer and starts the consumer connection.
     *
     * @param connectionDetails the connection details of the SoftwareDefinedRadio service provider
     * @param comServices the COM services consumer used by this service
     * @param authenticationId the authentication id of the logged in user
     * @param localNamePrefix the prefix for the local name of the consumer
     * @throws MALException if the consumer cannot be created
     * @throws MALInteractionException if the service returns an error
     */
    public SoftwareDefinedRadioConsumerServiceImpl(SingleConnectionDetails connectionDetails,
            COMServicesConsumer comServices, Blob authenticationId, String localNamePrefix)
            throws MALException, MALInteractionException {
        this.connectionDetails = connectionDetails;
        this.comServices = comServices;

        // Close old connection
        if (tmConsumer != null) {
            try {
                tmConsumer.close();
            } catch (MALException ex) {
                Logger.getLogger(SoftwareDefinedRadioConsumerServiceImpl.class.getName()).log(
                        Level.SEVERE, "The consumer connection could not be closed!", ex);
            }
        }

        tmConsumer = connection.startService(
                this.connectionDetails,
                SoftwareDefinedRadioHelper.SOFTWAREDEFINEDRADIO_SERVICE,
                authenticationId,
                localNamePrefix);

        this.softwareDefinedRadioService = new SoftwareDefinedRadioStub(tmConsumer);
    }

    /**
     * Creates the SoftwareDefinedRadio service consumer with no authentication id nor local name prefix.
     *
     * @param connectionDetails the connection details of the SoftwareDefinedRadio service provider
     * @param comServices the COM services consumer used by this service
     * @throws MALException if the consumer cannot be created
     * @throws MALInteractionException if the service returns an error
     */
    public SoftwareDefinedRadioConsumerServiceImpl(SingleConnectionDetails connectionDetails,
            COMServicesConsumer comServices) throws MALException, MALInteractionException {
        this(connectionDetails, comServices, null, null);
    }

    /**
     * Returns the COM services consumer used by this service.
     *
     * @return the COM services consumer
     */
    public COMServicesConsumer getCOMServices() {
        return comServices;
    }

    @Override
    public Object getStub() {
        return this.getSoftwareDefinedRadioStub();
    }

    /**
     * Returns the SoftwareDefinedRadio service stub used to invoke operations on the provider.
     *
     * @return the SoftwareDefinedRadio service stub
     */
    public SoftwareDefinedRadioStub getSoftwareDefinedRadioStub() {
        return this.softwareDefinedRadioService;
    }

    @Override
    public Object generateServiceStub(MALConsumer tmConsumer) {
        return new SoftwareDefinedRadioStub(tmConsumer);
    }

}
