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
package esa.mo.com.impl.consumer;

import esa.mo.com.impl.util.COMServicesConsumer;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.login.LoginHelper;
import org.ccsds.moims.mo.com.login.consumer.LoginStub;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.helpertools.misc.ConsumerServiceImpl;
import org.ccsds.moims.mo.mal.structures.Blob;

/**
 * Consumer of the COM Login service.
 *
 * @author Cesar Coelho
 */
public class LoginConsumerServiceImpl extends ConsumerServiceImpl {

    private LoginStub loginService = null;
    private COMServicesConsumer comServices;

    /**
     * Creates the Login service consumer and starts the consumer connection.
     *
     * @param connectionDetails the connection details of the Login service provider
     * @param comServices the COM services consumer used by this service
     * @param authenticationId the authentication id of the logged in user
     * @param localNamePrefix the prefix for the local name of the consumer
     * @throws MALException if the consumer cannot be created
     * @throws MalformedURLException if a provided URI is malformed
     * @throws MALInteractionException if the service returns an error
     */
    public LoginConsumerServiceImpl(SingleConnectionDetails connectionDetails,
            COMServicesConsumer comServices, Blob authenticationId,
            String localNamePrefix) throws MALException, MalformedURLException, MALInteractionException {
        this.connectionDetails = connectionDetails;
        this.comServices = comServices;

        // Close old connection
        if (tmConsumer != null) {
            try {
                tmConsumer.close();
            } catch (MALException ex) {
                Logger.getLogger(LoginConsumerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        tmConsumer = connection.startService(this.connectionDetails.getProviderURI(),
                this.connectionDetails.getBrokerURI(),
                this.connectionDetails.getDomain(),
                LoginHelper.LOGIN_SERVICE,
                authenticationId, localNamePrefix);

        this.loginService = new LoginStub(tmConsumer);
    }

    /**
     * Creates the Login service consumer with no authentication id nor local name prefix.
     *
     * @param connectionDetails the connection details of the Login service provider
     * @param comServices the COM services consumer used by this service
     * @throws MALException if the consumer cannot be created
     * @throws MalformedURLException if a provided URI is malformed
     * @throws MALInteractionException if the service returns an error
     */
    public LoginConsumerServiceImpl(SingleConnectionDetails connectionDetails, COMServicesConsumer comServices)
            throws MALException, MalformedURLException, MALInteractionException {
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
    public Object generateServiceStub(MALConsumer tmConsumer) {
        return new LoginStub(tmConsumer);
    }

    @Override
    public Object getStub() {
        return this.getLoginStub();
    }

    /**
     * Returns the Login service stub used to invoke operations on the provider.
     *
     * @return the Login service stub
     */
    public LoginStub getLoginStub() {
        return this.loginService;
    }
}
