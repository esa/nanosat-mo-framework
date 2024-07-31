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
package opssat.simulator.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import opssat.simulator.peripherals.PCamera;
import opssat.simulator.peripherals.PFineADCS;
import opssat.simulator.peripherals.PGPS;
import opssat.simulator.peripherals.POpticalReceiver;
import opssat.simulator.peripherals.PSDR;
import opssat.simulator.threading.CentralNode;
import opssat.simulator.threading.SimulatorNode;
import opssat.simulator.util.LoggerFormatter1Line;

/**
 *
 * @author Cezar Suteu
 */
public class ESASimulator extends GenericSimulator {

    private SimulatorNode simulatorNode;
    private CentralNode centralNode;

    Level simulatorLoggingLevel = Level.INFO;
    Level centralLoggingLevel = Level.INFO;
    Level consoleLoggingLevel = Level.INFO;

    private Level getLevelFromString(String[] split, String test) {
        if (split[0].equals(test)) {
            Level logLevel = null;
            if ((logLevel = Level.parse(split[1])) != null) {

                return logLevel;
            }
        }
        return null;

    }

    private void initProperties() {
        final String fileName = "_OPS-SAT-SIMULATOR-header.txt";
        File propertiesFile = new File(System.getProperty("user.dir"), fileName);
        if (propertiesFile.exists()) {
            // System.out.println(LoggerFormatter1Line.SIMULATOR_PRE_LOG + "PRE_INIT: Header
            // file was found");
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(fileName));
                String line;
                try {
                    while ((line = in.readLine()) != null) {

                        if (line.startsWith("#")) {
                            // comment line
                        } else {
                            String[] split = line.split("=");
                            if (split.length == 2) {
                                Level temp = null;
                                temp = getLevelFromString(split, "simulatorLogLevel");
                                if (temp != null) {
                                    this.simulatorLoggingLevel = temp;
                                }
                                temp = getLevelFromString(split, "centralLogLevel");
                                if (temp != null) {
                                    this.centralLoggingLevel = temp;
                                }
                                temp = getLevelFromString(split, "consoleLogLevel");
                                if (temp != null) {
                                    this.consoleLoggingLevel = temp;
                                }
                            }
                        }
                    }
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(ESASimulator.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (FileNotFoundException ex) {
                Logger.getLogger(ESASimulator.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println(LoggerFormatter1Line.SIMULATOR_PRE_LOG +
                "PRE_INIT: Header was not found, a default header will be created.");
        }

    }

    private void initDevices() {
        simulatorNode.getLogObject().fine("Initializing devices..");
        super.setpGPS(new PGPS(this.simulatorNode, "GPS"));
        super.setpFineADCS(new PFineADCS(this.simulatorNode, "FineADCS"));
        super.setpCamera(new PCamera(this.simulatorNode, "Camera"));
        super.setpOpticalReceiver(new POpticalReceiver(this.simulatorNode, "OpticalReceiver"));
        super.setpSDR(new PSDR(this.simulatorNode, "SDR"));
    }

    // Entry stub for lightweight component
    public ESASimulator() {
        initProperties();
        ConcurrentLinkedQueue<Object> qSimToGUI = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Object> qGUIToSim = new ConcurrentLinkedQueue<>();
        simulatorNode = new SimulatorNode(qGUIToSim, qSimToGUI, "Sim", 10, this.simulatorLoggingLevel,
            this.consoleLoggingLevel);
        initDevices();

    }

    // Entry stub for simulator running with TCP listener
    public ESASimulator(String listenURL) {
        initProperties();
        final CentralNode centralNode;

        ConcurrentLinkedQueue<Object> qSimToCentral = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Object> qCentralToSim = new ConcurrentLinkedQueue<>();

        simulatorNode = new SimulatorNode(qCentralToSim, qSimToCentral, "Sim", 100, this.simulatorLoggingLevel,
            this.consoleLoggingLevel);
        centralNode = new CentralNode(qSimToCentral, qCentralToSim, listenURL, "Cen", 10, this.centralLoggingLevel,
            this.consoleLoggingLevel, this);
        this.centralNode = centralNode;
        (new Thread(simulatorNode, "sim-" + simulatorNode.getClass().getSimpleName())).start();
        (new Thread(centralNode, "sim-" + centralNode.getClass().getSimpleName())).start();
        initDevices();
    }

    public SimulatorNode getSimulatorNode() {
        return simulatorNode;
    }

}
