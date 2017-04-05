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
package esa.mo.reconfigurable.service;

import org.ccsds.moims.mo.com.COMService;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectDetails;

/**
 * An interface to make a service provider implementation reconfigurable
 */
public interface ReconfigurableServiceImplInterface {

    /**
     * Sets a configuration adapter to receive the notification of changes in 
     * the service.
     * @param configurationAdapter The adapter to receive the notifications
     */
    void setConfigurationAdapter(ConfigurationNotificationInterface configurationAdapter);

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
     * Retrieves the service key for the reconfigurable service
     * @return The service key
     */
//    public ServiceKey getServiceKey();
    
    /**
     * Retrieves the COMService object
     * @return The service key
     */
    COMService getCOMService();
    
    
}
