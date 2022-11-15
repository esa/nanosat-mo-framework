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
package opssat.simulator.gui;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import opssat.simulator.tcp.SocketClient;
import opssat.simulator.util.CommandDescriptor;
import opssat.simulator.util.LoggerFormatter1Line;

/**
 *
 * @author Cezar Suteu
 */
public class GuiApp {

    private final GuiMainWindow guiMainWindow;
    private final ConcurrentLinkedQueue<Object> toServerQueue;
    private final ConcurrentLinkedQueue<Object> fromServerQueue;
    private SocketClient socketClient;
    private String targetURL;
    private int targetPort;
    Logger logger;
    private String simulatorWorkingDir;

    public String getSimulatorWorkingDir() {
        return simulatorWorkingDir;
    }

    public void setSimulatorWorkingDir(String dir) {
        this.simulatorWorkingDir = dir;
    }

    public SocketClient getSocketClient() {
        return socketClient;
    }

    public void setTargetConnection(String targetURL, int targetPort) {
        this.targetURL = targetURL;
        this.targetPort = targetPort;
        this.socketClient.setTargetConnection(targetURL, targetPort);
    }

    public ConcurrentLinkedQueue<Object> getFromServerQueue() {
        return fromServerQueue;

    }

    public ConcurrentLinkedQueue<Object> getToServerQueue() {
        return toServerQueue;
    }

    public GuiMainWindow getGuiMainWindow() {
        return guiMainWindow;
    }

    public void restartSocket() {
        socketClient = new SocketClient(targetURL, targetPort, this);
        socketClient.start();
    }

    public Logger getLogger() {
        return logger;
    }

    public GuiApp(String targetURL, int targetPort) {
        this.logger = Logger.getLogger(GuiMainWindow.class.getName());
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);
        ConsoleHandler consoleHandler = (new ConsoleHandler());
        consoleHandler.setFormatter(new LoggerFormatter1Line(GuiMainWindow.class.getName()));
        logger.addHandler(consoleHandler);
        fromServerQueue = new ConcurrentLinkedQueue<>();
        toServerQueue = new ConcurrentLinkedQueue<>();
        this.targetURL = targetURL;
        this.targetPort = targetPort;
        guiMainWindow = new GuiMainWindow(this, targetURL, targetPort);
        (new Thread(guiMainWindow)).start();
    }

    public void startSocket() {
        socketClient = new SocketClient(targetURL, targetPort, this);
        socketClient.start();
    }

    public void addGUIInteraction(Object data) {
        toServerQueue.add(data);
        fromServerQueue.add("Local;UserInput;" + CommandDescriptor.makeConsoleDescriptionForObj(data));
    }

}
