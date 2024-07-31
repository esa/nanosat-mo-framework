/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
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

package esa.mo.nmf.clitool.adapters;

import esa.mo.com.impl.util.ArchiveCOMObjectsOutput;
import esa.mo.helpertools.helpers.HelperTime;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;
import org.ccsds.moims.mo.softwaremanagement.commandexecutor.CommandExecutorHelper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Archive adapter that dumps to a LOG file StandardOutput and StandardError events body of the
 * CommandExecutor service of the SoftwareManagement received from an archive query.
 *
 * @author Tanguy Soto
 */
public class ArchiveToLogAdapter extends ArchiveAdapter implements QueryStatusProvider {

    private static final Logger LOGGER = Logger.getLogger(ArchiveToLogAdapter.class.getName());

    /**
     * Destination LOG file
     */
    private FileWriter logFile;

    /**
     * True if the query is over (response or any error received)
     */
    private boolean isQueryOver = false;

    /*
     * Types of objects to show in the log
     */
    private final Map<ObjectType, String> objectTypes = new HashMap<>();

    private final List<ArchiveCOMObjectsOutput> queryResults = new ArrayList<>();

    private final boolean addTimestamps;

    private final String logFilePath;

    /**
     * Creates a new instance of ArchiveToLogAdapter.
     * 
     * @param logFilePath Path of destination LOG file where we dump the String objects
     */
    public ArchiveToLogAdapter(String logFilePath, boolean addTimestamps) {
        this.addTimestamps = addTimestamps;
        this.logFilePath = logFilePath;

        objectTypes.put(CommandExecutorHelper.STANDARDOUTPUT_OBJECT_TYPE, null);
        objectTypes.put(CommandExecutorHelper.STANDARDERROR_OBJECT_TYPE, null);
        objectTypes.put(AppsLauncherHelper.STARTAPP_OBJECT_TYPE, "Event StartApp, body: ");
        objectTypes.put(AppsLauncherHelper.STOPAPP_OBJECT_TYPE, "Event StopApp, body: ");
        objectTypes.put(AppsLauncherHelper.STOPPING_OBJECT_TYPE, "Event Stopping, body: ");
        objectTypes.put(AppsLauncherHelper.STOPPED_OBJECT_TYPE, "Event Stopped, body: ");
    }

    /**
     * Dumps an archive objects output received from an archive query answer (update or response).
     */
    public synchronized void dumpArchiveObjectsOutput() {
        openLogFile(logFilePath);

        if (logFile == null) {
            LOGGER.log(Level.SEVERE, "Can't dump MAL elements to LOG file: file is null");
            return;
        }

        queryResults.sort(Comparator.comparingLong(com -> com.getArchiveDetailsList() != null ? com
            .getArchiveDetailsList().get(0).getTimestamp().getValue() : 0));

        for (ArchiveCOMObjectsOutput archiveObjectOutput : queryResults) {
            // empty comType means query returned nothing
            ObjectType comType = archiveObjectOutput.getObjectType();
            if (comType == null) {
                return;
            }

            // only expected types
            if (!objectTypes.containsKey(comType)) {
                return;
            }

            // if somehow we have no object bodies, stop
            if (comType.getService().equals(CommandExecutorHelper.COMMANDEXECUTOR_SERVICE_NUMBER) && archiveObjectOutput
                .getObjectBodies() == null) {
                return;
            }

            String lineOffset = "                        "; // 24 is the length of the timestamp
            for (int i = 0; i < archiveObjectOutput.getArchiveDetailsList().size(); ++i) {
                String logObject;
                if (comType.getService().equals(CommandExecutorHelper.COMMANDEXECUTOR_SERVICE_NUMBER)) {
                    // write LOG message, we can safely cast to String
                    logObject = (String) archiveObjectOutput.getObjectBodies().get(i);
                } else {
                    logObject = objectTypes.get(comType);

                    if (archiveObjectOutput.getObjectBodies() != null) {
                        logObject += ((Identifier) archiveObjectOutput.getObjectBodies().get(i)).getValue() + "\n";
                    } else {
                        logObject += "empty\n";
                    }
                }
                try {
                    if (addTimestamps) {
                        FineTime timestamp = archiveObjectOutput.getArchiveDetailsList().get(i).getTimestamp();
                        String[] logLines = logObject.split("\n");
                        logLines[0] = HelperTime.time2readableString(timestamp) + " " + logLines[0];
                        if (logLines.length > 1) {
                            for (int j = 1; j < logLines.length; ++j) {
                                logLines[j] = lineOffset + logLines[j];
                            }
                        }

                        for (String line : logLines) {
                            logFile.write(line + "\n");
                        }
                    } else {
                        logFile.write(logObject);
                    }
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error writing LOG object", e);
                }
            }
        }
        closeLogFile();
    }

    /**
     * Opens the LOG file.
     * 
     * @param logFilePath to the LOG file to open/create
     */
    private void openLogFile(String logFilePath) {
        try {
            logFile = new FileWriter(logFilePath);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, String.format("Error opening the LOG file", logFilePath), e);
            logFile = null;
        }
    }

    /**
     * Safely closes the LOG file.
     */
    private synchronized void closeLogFile() {
        if (logFile != null) {
            try {
                logFile.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error closing the LOG file", e);
            }
        }
    }

    @Override
    public void queryResponseReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain,
        ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {
        queryResults.add(new ArchiveCOMObjectsOutput(domain, objType, objDetails, objBodies));
        setIsQueryOver(true);
    }

    @Override
    public void queryUpdateReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain,
        ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {
        queryResults.add(new ArchiveCOMObjectsOutput(domain, objType, objDetails, objBodies));
    }

    @Override
    public void queryAckErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
        LOGGER.log(Level.SEVERE, "queryAckErrorReceived", error);
        setIsQueryOver(true);
    }

    @Override
    public void queryUpdateErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
        LOGGER.log(Level.SEVERE, "queryUpdateErrorReceived", error);
        setIsQueryOver(true);
    }

    @Override
    public void queryResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
        LOGGER.log(Level.SEVERE, "queryResponseErrorReceived", error);
        setIsQueryOver(true);
    }

    /*
     * Resets the adapter to perform another query without creating a second instance
     */
    public void resetAdapter() {
        this.isQueryOver = false;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean isQueryOver() {
        return isQueryOver;
    }

    private synchronized void setIsQueryOver(boolean isQueryOver) {
        this.isQueryOver = isQueryOver;
    }
}
