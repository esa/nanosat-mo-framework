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
package esa.mo.common.impl.consumer;

import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.helpertools.misc.ConsumerServiceImpl;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.common.CommonHelper;
import org.ccsds.moims.mo.common.login.LoginHelper;
import org.ccsds.moims.mo.common.login.consumer.LoginStub;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;

/**
 *
 * @author Cesar Coelho
 */
public class LoginConsumerServiceImpl extends ConsumerServiceImpl {

    private LoginStub loginService = null;
    private COMServicesConsumer comServices;

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

    public LoginStub getLoginStub() {
        return this.loginService;
    }

    public LoginConsumerServiceImpl(SingleConnectionDetails connectionDetails, COMServicesConsumer comServices) throws MALException, MalformedURLException, MALInteractionException {

        if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
            MALHelper.init(MALContextFactory.getElementFactoryRegistry());
        }

        if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
            COMHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
        }

        if (MALContextFactory.lookupArea(CommonHelper.COMMON_AREA_NAME, CommonHelper.COMMON_AREA_VERSION) == null) {
            CommonHelper.init(MALContextFactory.getElementFactoryRegistry());
        }

        try {
            LoginHelper.init(MALContextFactory.getElementFactoryRegistry());
        } catch (MALException ex) {
            // nothing to be done..
        }

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

        tmConsumer = connection.startService(
                this.connectionDetails.getProviderURI(),
                this.connectionDetails.getBrokerURI(),
                this.connectionDetails.getDomain(),
                LoginHelper.LOGIN_SERVICE);

        this.loginService = new LoginStub(tmConsumer);
    }

}
