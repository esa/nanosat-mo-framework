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

/**
 *
 * @author Cezar Suteu
 */
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import opssat.simulator.util.LoggerFormatter;
import opssat.simulator.util.LoggerFormatter1Line;
import opssat.simulator.util.SimulatorTimer;

public abstract class TaskNode implements Runnable {

    abstract void dataIn(Object obj);

    abstract void coreRun();

    abstract Object dataOut();

    private final String name;
    private final int delay;

    public static final String TIMER_ALIVE = "Alive";

    private static final int TIMER_ALIVE_INTERVAL = 10000;

    private Hashtable<String, SimulatorTimer> timers;

    private Logger logObject;
    private FileHandler fh = null;
    private FileHandler fh_static = null;
    private String LogPath;

    public Logger getLogObject() {
        return logObject;
    }

    private long timeElapsed;
    private long lastTimeElapsed;

    public long getTimeElapsed() {
        return timeElapsed;
    }

    public String getName() {
        return name;
    }

    ConcurrentLinkedQueue<Object> queueIn;
    ConcurrentLinkedQueue<Object> queueOut;

    public Hashtable<String, SimulatorTimer> getTimers() {
        return timers;
    }

    private void initLogging(Level logLevel, Level consoleLogLevel) {
        LogPath = SimulatorNode.getDataPath();
        File sourceLoc = new File(LogPath);

        if (!sourceLoc.exists()) {
            sourceLoc.mkdirs();
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        try {
            fh = new FileHandler(LogPath + name + "_" + format.format(Calendar.getInstance().getTime()) + ".log", 1024 *
                1024, 1, false);
            fh_static = new FileHandler(LogPath + name + ".log", 1024 * 1024, 1, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LoggerFormatter formatter = new LoggerFormatter();
        fh.setFormatter(formatter);
        fh_static.setFormatter(formatter);

        logObject = Logger.getLogger(name);
        logObject.addHandler(fh);
        logObject.addHandler(fh_static);
        logObject.setUseParentHandlers(false);
        logObject.setLevel(logLevel);

        ConsoleHandler consoleHandler = (new ConsoleHandler());
        consoleHandler.setLevel(consoleLogLevel);
        consoleHandler.setFormatter(new LoggerFormatter1Line(name));

        logObject.addHandler(consoleHandler);
        logObject.log(Level.INFO, "File logging level is [" + logLevel + "], Console logging level is [" +
            consoleLogLevel + "]");
    }

    public TaskNode(ConcurrentLinkedQueue<Object> queueIn, ConcurrentLinkedQueue<Object> queueOut, String name,
        int delay, Level logLevel, Level consoleLogLevel) {
        this.name = name;
        initLogging(logLevel, consoleLogLevel);

        this.timers = new Hashtable<>();

        this.timers.put(TIMER_ALIVE, new SimulatorTimer(TIMER_ALIVE, TIMER_ALIVE_INTERVAL));

        this.queueIn = queueIn;
        this.queueOut = queueOut;

        this.delay = delay;

    }

    private void logMessage(String data, Level logLevel) {

        logObject.log(logLevel, data);

    }

    private void logMessage(String data) {
        logMessage(data, Level.INFO);
    }

    private void manageTime() {
        long sysTime = System.currentTimeMillis();
        long diff = sysTime - lastTimeElapsed;
        lastTimeElapsed = sysTime;
        this.timeElapsed = diff;
        if (timeElapsed > 5000) {

            timeElapsed = 0;
        }
        for (Entry<String, SimulatorTimer> entry : timers.entrySet()) {
            entry.getValue().timeElapsed(timeElapsed);
        }
    }

    @Override
    public void run() {
        while (true) {
            manageTime();
            Object data = queueIn.poll();
            if (data != null) {
                dataIn(data);
            }
            coreRun();
            data = dataOut();
            if (data != null) {
                queueOut.offer(data);
            }
            if (timers.get(TIMER_ALIVE).isElapsed()) {
                String aliveData = name + "Alive";
                logMessage(aliveData, Level.ALL);
            }
            try {
                Thread.sleep(this.delay);
            } catch (InterruptedException ex) {
                Logger.getLogger(TaskNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
