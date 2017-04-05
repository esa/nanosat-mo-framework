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
package esa.mo.reconfigurable.provider;

import esa.mo.reconfigurable.service.ConfigurationNotificationInterface;
import esa.mo.reconfigurable.service.ReconfigurableServiceImplInterface;
import java.util.ArrayList;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectDetails;
import org.ccsds.moims.mo.mal.structures.Identifier;

/**
 * An interface to make a service provider implementation reconfigurable
 */
public interface ReconfigurableProviderImplInterface {

    /**
     * Sets a configuration adapter to receive the notification of changes in 
     * the service.
     * @param configurationAdapter The adapter to receive the notifications
     */
    void setConfigurationAdapter(ConfigurationNotificationInterface configurationAdapter);

    /**
     * Getter for COM services 
     * @return COM services provider
     */
//    public COMServicesProvider getCOMServices();

    /**
     * Get list of services available
     * @return A list of the services available
     */
    ArrayList<ReconfigurableServiceImplInterface> getServices();
    
    /**
     * Reloads the service with the provided configuration
     * @param configurationObjectDetails The configuration
     * @return True if successfully reloaded, false otherwise.
     */
    Boolean reloadConfiguration(ConfigurationObjectDetails configurationObjectDetails);

    /**
     * Retrieves the current configuration set in the service provider
     * @return The configuration of the service
     */
    ConfigurationObjectDetails getCurrentConfiguration();

    /**
     * Retrieves the name of the provider
     * @return The name of the provider
     */
    Identifier getProviderName();

}
