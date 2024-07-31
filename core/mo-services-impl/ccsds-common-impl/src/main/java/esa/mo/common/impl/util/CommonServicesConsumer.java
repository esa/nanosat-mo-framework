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
package esa.mo.common.impl.util;

import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.common.impl.consumer.ConfigurationConsumerServiceImpl;
import esa.mo.common.impl.consumer.DirectoryConsumerServiceImpl;
import esa.mo.common.impl.consumer.LoginConsumerServiceImpl;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.common.configuration.ConfigurationHelper;
import org.ccsds.moims.mo.common.directory.DirectoryHelper;
import org.ccsds.moims.mo.common.login.LoginHelper;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Blob;

/**
 *
 *
 */
public class CommonServicesConsumer {

    private DirectoryConsumerServiceImpl directoryService;
    private ConfigurationConsumerServiceImpl configurationService;
    private LoginConsumerServiceImpl loginService;

    public void init(ConnectionConsumer connectionConsumer, COMServicesConsumer comServices) {
        init(connectionConsumer, comServices, null, null);
    }

    public void init(ConnectionConsumer connectionConsumer, COMServicesConsumer comServices, Blob authenticationId,
        String localNamePrefix) {

        SingleConnectionDetails details;

        try {
            // Initialize the Directory service
            details = connectionConsumer.getServicesDetails().get(DirectoryHelper.DIRECTORY_SERVICE_NAME);
            if (details != null) {
                directoryService = new DirectoryConsumerServiceImpl(details.getProviderURI(), authenticationId,
                    localNamePrefix);
            }

            // Initialize the Configuration service
            details = connectionConsumer.getServicesDetails().get(ConfigurationHelper.CONFIGURATION_SERVICE_NAME);
            if (details != null) {
                configurationService = new ConfigurationConsumerServiceImpl(details, comServices, authenticationId,
                    localNamePrefix);
            }

            // Initialize the Login service
            details = connectionConsumer.getServicesDetails().get(LoginHelper.LOGIN_SERVICE_NAME);
            if (details != null) {
                loginService = new LoginConsumerServiceImpl(details, comServices, authenticationId, localNamePrefix);
            }
        } catch (MALException | MALInteractionException | MalformedURLException ex) {
            Logger.getLogger(CommonServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public DirectoryConsumerServiceImpl getDirectoryService() {
        return this.directoryService;
    }

    public ConfigurationConsumerServiceImpl getConfigurationService() {
        return this.configurationService;
    }

    public LoginConsumerServiceImpl getLoginService() {
        return this.loginService;
    }

    public void setServices(DirectoryConsumerServiceImpl directoryService,
        ConfigurationConsumerServiceImpl configurationService, LoginConsumerServiceImpl loginService) {
        this.directoryService = directoryService;
        this.configurationService = configurationService;
        this.loginService = loginService;
    }

    public void setDirectoryService(DirectoryConsumerServiceImpl directoryService) {
        this.directoryService = directoryService;
    }

    public void setConfigurationService(ConfigurationConsumerServiceImpl configurationService) {
        this.configurationService = configurationService;
    }

    public void setLoginService(LoginConsumerServiceImpl loginService) {
        this.loginService = loginService;
    }

    /**
     * Closes the service consumer connections
     *
     */
    public void closeConnections() {
        if (this.directoryService != null) {
            this.directoryService.close();
        }

        if (this.configurationService != null) {
            this.configurationService.close();
        }

        if (this.loginService != null) {
            this.loginService.close();
        }
    }

    public void setAuthenticationId(Blob authenticationId) {
        if (this.directoryService != null) {
            this.directoryService.setAuthenticationId(authenticationId);
        }

        if (this.configurationService != null) {
            this.configurationService.setAuthenticationId(authenticationId);
        }

        if (this.loginService != null) {
            this.loginService.setAuthenticationId(authenticationId);
        }
    }
}
