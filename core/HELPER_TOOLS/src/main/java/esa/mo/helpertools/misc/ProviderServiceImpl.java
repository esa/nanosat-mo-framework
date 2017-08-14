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
package esa.mo.helpertools.misc;

import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.UShortList;

/**
 * An abstract class to be extended by specific service providers. This class is
 * not used and should be considered removed in the future.
 */
@Deprecated
public abstract class ProviderServiceImpl {

    protected MALProvider serviceProvider;
    protected ConnectionProvider connection = new ConnectionProvider();
    protected SingleConnectionDetails connectionDetails;

    public SingleConnectionDetails getConnectionDetails() {
        return connectionDetails;
    }

    public ConnectionProvider getConnectionProvider() {
        return connection;
    }

    /**
     * Closes the connection
     */
    public abstract void closeConnection();

    /**
     * Initialize the service provider
     */
    public abstract void init();

    /**
     * Get the available supported capabilities
     *
     * @return The list of capability set's numbers
     */
    public abstract UShortList getSupportedCapabilities();

}
