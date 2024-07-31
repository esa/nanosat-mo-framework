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
package opssat.simulator.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import opssat.simulator.threading.SimulatorNode;

/**
 *
 * @author Cezar Suteu
 */
public class SimulatorSchedulerPiece implements Serializable {
    long time;
    int internalID;
    boolean executed;
    String argumentTemplateDescription;

    public static String getDDDDDHHMMSSmmmFromMillis(long data) {
        Map<TimeUnit, Long> computedDiff = SimulatorData.computeTimeUnit(data);
        if (computedDiff != null) {
            return String.format(SimulatorHeader.FROM_START_FORMAT, computedDiff.get(TimeUnit.DAYS), computedDiff.get(
                TimeUnit.HOURS), computedDiff.get(TimeUnit.MINUTES), computedDiff.get(TimeUnit.SECONDS), computedDiff
                    .get(TimeUnit.MILLISECONDS));
        } else {
            return "computedDiff is null!";
        }
    }

    public static long getMillisFromDDDDDHHMMSSmmm(String data) {
        if (data.length() != 18) {
            return -1;
        } else {
            List<String> words = Arrays.asList(data.split(CommandDescriptor.SEPARATOR_TIMEDEFINITION));
            if (words.size() != 5) {
                return -1;
            } else {
                try {
                    return Long.parseLong(words.get(0)) * 24 * 60 * 60 * 1000 + Long.parseLong(words.get(1)) * 60 * 60 *
                        1000 + Long.parseLong(words.get(2)) * 60 * 1000 + Long.parseLong(words.get(3)) * 1000 + Long
                            .parseLong(words.get(4));
                } catch (NumberFormatException ex) {
                    Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
                    return -1;
                }
            }
        }
    }

    public String getFileString() {
        return getDDDDDHHMMSSmmmFromMillis(time) + CommandDescriptor.SEPARATOR_DATAFILES + String.format("%019d",
            time) + CommandDescriptor.SEPARATOR_DATAFILES + internalID + CommandDescriptor.SEPARATOR_DATAFILES +
            argumentTemplateDescription;
    }

    public String getSchedulerOutput() {
        String argTDescriptionTabbed;
        return String.format("%s  %-4s  %-12s executed %-5s   | ", getDDDDDHHMMSSmmmFromMillis(time), internalID,
            argumentTemplateDescription, executed);
        //return getDDDDDHHMMSSmmmFromMillis(time)+CommandDescriptor.SEPARATOR_DATAFILES+this.internalID+"\t"+CommandDescriptor.SEPARATOR_DATAFILES+argumentTemplateDescription+"\t\t"+CommandDescriptor.SEPARATOR_DATAFILES+"executed:"+this.executed+CommandDescriptor.SEPARATOR_DATAFILES;
    }

    public SimulatorSchedulerPiece(long time, int internalID, String argumentTemplateDescription) {
        this.time = time;
        this.internalID = internalID;
        this.argumentTemplateDescription = argumentTemplateDescription;
        this.executed = false;
    }

    public long getTime() {
        return time;
    }

    public int getInternalID() {
        return internalID;
    }

    public String getArgumentTemplateDescription() {
        return argumentTemplateDescription;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

}
