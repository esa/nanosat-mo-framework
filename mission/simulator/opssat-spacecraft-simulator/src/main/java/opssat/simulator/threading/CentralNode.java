/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2021      European Space Agency
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
package opssat.simulator.threading;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import opssat.simulator.celestia.CelestiaData;
import opssat.simulator.celestia.CelestiaIf;
import opssat.simulator.main.ESASimulator;
import opssat.simulator.tcp.MultiThreadedSocketServer;
import opssat.simulator.util.SimulatorHeader;

/**
 *
 * @author Cezar Suteu
 */
public class CentralNode extends TaskNode {

    private ConcurrentLinkedQueue<Object> qFromGUI;
    private ConcurrentLinkedQueue<Object> qToCelestia;
    private ESASimulator parent;

    public synchronized ConcurrentLinkedQueue<Object> getqFromGUI() {
        return qFromGUI;
    }

    private MultiThreadedSocketServer multiThreadedSocketServer;
    CelestiaIf celestiaInterfaceServer;
    boolean celestiaInitDone;

    public CentralNode(ConcurrentLinkedQueue<Object> queueIn, ConcurrentLinkedQueue<Object> queueOut, String name,
        int delay, Level logLevel, Level consoleLogLevel, ESASimulator sim) {
        super(queueIn, queueOut, name, delay, logLevel, consoleLogLevel);
        this.qFromGUI = new ConcurrentLinkedQueue<>();
        this.qToCelestia = new ConcurrentLinkedQueue<>();
        this.parent = sim;
    }

    public ESASimulator getParentSimulator() {
        return parent;
    }

    public void callBackLogMessage(String data) {
        super.getLogObject().log(Level.INFO, data);
    }

    // Overloaded constructor, operation with TCP server
    public CentralNode(ConcurrentLinkedQueue<Object> queueIn, ConcurrentLinkedQueue<Object> queueOut, String listenURL,
        String name, int delay, Level logLevel, Level consoleLogLevel, ESASimulator sim) {
        super(queueIn, queueOut, name, delay, logLevel, consoleLogLevel);
        this.qFromGUI = new ConcurrentLinkedQueue<>();
        this.qToCelestia = new ConcurrentLinkedQueue<>();
        super.getLogObject().log(Level.FINE, "Creating listener on URL [" + listenURL + "]..");
        this.multiThreadedSocketServer = new MultiThreadedSocketServer(listenURL, this,
            MultiThreadedSocketServer.DEFAULT_SOCKET_PORT, super.getLogObject());
        multiThreadedSocketServer.start();
        this.parent = sim;
    }

    private void initCelestia(SimulatorHeader header) {
        super.getLogObject().log(Level.FINE, "Creating Celestia provider");
        this.celestiaInterfaceServer = new CelestiaIf(this.qToCelestia, header.getCelestiaPort(), "OPS-SAT",
            super.getLogObject());
        (new Thread(celestiaInterfaceServer, "sim-" + celestiaInterfaceServer.getClass().getSimpleName())).start();
    }

    @Override
    void dataIn(Object obj) {
        if (obj instanceof CelestiaData && this.celestiaInterfaceServer != null) {
            this.celestiaInterfaceServer.putDataInBuffer(obj);
        } else if (this.multiThreadedSocketServer == null) {
        } else {
            if (obj instanceof SimulatorHeader && !this.celestiaInitDone) {
                SimulatorHeader centralHeader = (SimulatorHeader) obj;
                if (centralHeader.isUseCelestia()) {
                    this.celestiaInitDone = true;
                    initCelestia(centralHeader);
                }
            }
            this.multiThreadedSocketServer.putDataOnForAllClients(obj);
        }
        // super.logMessage(i.toString());

    }

    @Override
    void coreRun() {
    }

    @Override
    Object dataOut() {
        if (this.qFromGUI != null) {
            Object data = this.qFromGUI.poll();
            if (data != null) {
                if (data instanceof Integer) {
                    return null;
                } else {
                    super.getLogObject().log(Level.ALL, "Sending something [" + data.getClass().getName() +
                        "] to server ");
                    return data;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}
