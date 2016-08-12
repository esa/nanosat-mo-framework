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

import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 *
 * @author Cesar Coelho
 */
public abstract class ConsumerServiceImpl {

    protected ConnectionConsumer connection = new ConnectionConsumer();
    protected HashMap<Identifier, Object> servicesMap = new HashMap<Identifier, Object>();
    protected MALConsumer tmConsumer;
    protected SingleConnectionDetails connectionDetails;

    public SingleConnectionDetails getConnectionDetails() {
        return connectionDetails;
    }

    public ConnectionConsumer getConnectionConsumer() {
        return connection;
    }

    public HashMap<Identifier, Object> getServicesMap() {
        return servicesMap;
    }

    /**
     * Closes the tmConsumer connection
     *
     */
    public void closeConnection() {
        // Close old connection
        if (tmConsumer != null) {
            try {
                tmConsumer.close();
            } catch (MALException ex) {
                Logger.getLogger(ConsumerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Creates a tmConsumer connection for a specified subsystem
     *
     * @param subsystem Name of the subsystem
     * @param service Definition of the consumed service
     * @return Wrapped MALconsumer
     * @throws org.ccsds.moims.mo.mal.MALException
     * @throws java.net.MalformedURLException
     */
    public Object createConsumer(String subsystem, MALService service) throws MALException, MalformedURLException {

        Logger.getLogger(ConsumerServiceImpl.class.getName()).log(Level.INFO, "URI" + this.connectionDetails.getProviderURI().toString() + "@" + subsystem);

        MALConsumer consumer = connection.startService(
                new URI(this.connectionDetails.getProviderURI().toString() + "@" + subsystem),
                this.connectionDetails.getBrokerURI(),
                this.connectionDetails.getDomain(),
                service);

        Object stub = generateServiceStub(consumer);
        servicesMap.put(new Identifier(subsystem), stub);
        return stub;
    }

    /**
     * Wraps a MALconsumer connection with service specific methods that map
     * from the high level service API to the generic MAL API.
     *
     * @param tmConsumer The MALConsumer to use in this stub.
     * @return Wrapped MALconsumer
     */
    public abstract Object generateServiceStub(MALConsumer tmConsumer);

    /**
     * Returns the service Stub
     *
     * @return The service Stub
     */
    public abstract Object getStub();
    
}
