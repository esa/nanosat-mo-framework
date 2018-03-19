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
package esa.mo.helpertools.connections;

import esa.mo.helpertools.helpers.HelperConnections;
import esa.mo.helpertools.helpers.HelperMisc;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALContext;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.broker.MALBroker;
import org.ccsds.moims.mo.mal.broker.MALBrokerBinding;
import org.ccsds.moims.mo.mal.broker.MALBrokerManager;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.UInteger;

/**
 * Holds the connection of a shared broker.
 */
public class ConnectionSharedBroker {

    private MALContextFactory malFactory;
    private MALContext mal;
    private MALBrokerManager brokerMgr;

    /**
     * Closes any existing service brokers and recreates them.
     *
     * @param brokerName
     * @return
     * @throws MALException On error.
     */
    public MALBrokerBinding startBroker(String brokerName) throws MALException {
        malFactory = MALContextFactory.newFactory();
        mal = malFactory.createMALContext(System.getProperties());
        brokerMgr = mal.createBrokerManager();

        MALBroker sharedBroker = brokerMgr.createBroker();
        MALBrokerBinding brokerBinding = brokerMgr.createBrokerBinding(
                sharedBroker,
                brokerName,
                System.getProperties().getProperty("org.ccsds.moims.mo.mal.transport.default.protocol"),
                new Blob("".getBytes()),
                new QoSLevel[]{QoSLevel.ASSURED},
                new UInteger(1),
                new Hashtable());

        Logger.getLogger(ConnectionSharedBroker.class.getName()).log(Level.INFO, "Shared Broker URI: {0}", brokerBinding.getURI());

        // Write the URIs on a text file
        BufferedWriter wrt = null;
        try {
            wrt = new BufferedWriter(new FileWriter(HelperMisc.SHARED_BROKER_URI, true));
            wrt.append(HelperConnections.PROPERTY_SHARED_BROKER + "=" + brokerBinding.getURI());
            wrt.newLine();
        } catch (IOException ex) {
            Logger.getLogger(ConnectionProvider.class.getName()).log(Level.WARNING, "Unable to write URI information to properties file {0}", ex);
        } finally {
            if (wrt != null) {
                try {
                    wrt.close();
                } catch (IOException ex) {
                }
            }
        }

        return brokerBinding;
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != brokerMgr) {
                brokerMgr.close();
            }

            if (null != mal) {
                mal.close();
            }

        } catch (MALException ex) {
            Logger.getLogger(ConnectionSharedBroker.class.getName()).log(Level.WARNING, "Exception during close down of the broker {0}", ex);
        }
    }

    /**
     * Clears the URI links file for the shared broker
     */
    public static void resetURILinksFile() {
        BufferedWriter wrt = null;
        try {
            wrt = new BufferedWriter(new FileWriter(HelperMisc.SHARED_BROKER_URI, false));
        } catch (IOException ex) {
            Logger.getLogger(ConnectionProvider.class.getName()).log(Level.WARNING, "Unable to reset URI information from properties file {0}", ex);
        } finally {
            if (wrt != null) {
                try {
                    wrt.close();
                } catch (IOException ex) {
                }
            }
        }
    }

}
