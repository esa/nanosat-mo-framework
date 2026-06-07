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

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 *
 * @author Cezar Suteu
 */
public class LoggerFormatter1Line extends Formatter {

    //
    // Create a DateFormat to format the logger timestamp.
    //
    String nodeName;
    public final static String SIMULATOR_PRE_LOG = "OPS-SAT SoftSim:";

    public LoggerFormatter1Line(String nodeName) {
        this.nodeName = nodeName;
    }

    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder(1000);
        builder.append(SIMULATOR_PRE_LOG);
        builder.append(nodeName).append(";");
        builder.append(record.getLevel()).append(";");
        builder.append(record.getSourceClassName()).append(";");
        builder.append(record.getSourceMethodName()).append(";");
        builder.append(formatMessage(record));
        builder.append("\n");
        return builder.toString();
    }

    public String getHead(Handler h) {
        return super.getHead(h);
    }

    public String getTail(Handler h) {
        return super.getTail(h);
    }
}
