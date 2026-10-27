/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2026      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 *  You may not use this file except in compliance with the License.
 * 
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *  
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 *  ----------------------------------------------------------------------------
 */
package esa.mo.nmf.mission.orekit.simulator.tcp;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * What a {@link SocketClient} works for: the queues it fills with what the
 * simulator sends and empties of what is to be sent to it. The GUI is one; a
 * client without a window, such as one in a test, is another.
 */
public interface SocketClientOwner {

    /**
     * The queue that the client fills with what the simulator sends, and with
     * its own notes about the connection, as Strings starting with "Local;".
     *
     * @return the queue
     */
    ConcurrentLinkedQueue<Object> getFromServerQueue();

    /**
     * The queue that the client empties of what is to be sent to the simulator.
     *
     * @return the queue
     */
    ConcurrentLinkedQueue<Object> getToServerQueue();

    /**
     * The logger of the client.
     *
     * @return the logger
     */
    Logger getLogger();

    /**
     * Told when the connection to the simulator was lost.
     */
    void connectionLost();

    /**
     * Starts a new client, once the connection was lost, so that it connects again.
     */
    void restartSocket();
}
