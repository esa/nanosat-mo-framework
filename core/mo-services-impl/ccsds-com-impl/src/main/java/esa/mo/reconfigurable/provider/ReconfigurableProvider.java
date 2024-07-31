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
package esa.mo.reconfigurable.provider;

import java.util.ArrayList;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectDetails;
import org.ccsds.moims.mo.mal.structures.Identifier;
import esa.mo.reconfigurable.service.ReconfigurableService;
import esa.mo.reconfigurable.service.ConfigurationChangeListener;

/**
 * An interface to make a service provider implementation reconfigurable
 */
public interface ReconfigurableProvider {

    /**
     * Sets the configuration listener that receives the notification of changes
     * of a certain provider.
     *
     * @param listener The listener to receive the notifications
     */
    void setOnConfigurationChangeListener(ConfigurationChangeListener listener);

    /**
     * Reloads the service with the provided configuration
     *
     * @param configurationObjectDetails The configuration
     * @return True if successfully reloaded, false otherwise.
     */
    Boolean reloadConfiguration(ConfigurationObjectDetails configurationObjectDetails);

    /**
     * Retrieves the current configuration set in the provider
     *
     * @return The configuration of the service
     */
    ConfigurationObjectDetails getCurrentConfiguration();

    /**
     * Get list of services available
     *
     * @return A list of the services available
     */
    ArrayList<ReconfigurableService> getServices();

    /**
     * Retrieves the name of the provider
     *
     * @return The name of the provider
     */
    Identifier getProviderName();

}
