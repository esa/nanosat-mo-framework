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

package esa.mo.nmf.comarchivetool.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.structures.AppDetails;
import org.ccsds.moims.mo.softwaremanagement.commandexecutor.CommandExecutorHelper;
import esa.mo.com.impl.util.ArchiveCOMObjectsOutput;

/**
 * Archive adapter that lists App (of the AppsLauncher service) that have StandardOutput and
 * StandardError events (of the CommandExecutor service) associated to them from an archive query.
 *
 * @author Tanguy Soto
 */
public class ArchiveToAppListAdapter extends ArchiveAdapter implements QueryStatusProvider {

    private static final Logger LOGGER = Logger.getLogger(ArchiveToAppListAdapter.class.getName());

    /**
     * True if the query is over (response or any error received)
     */
    private boolean isQueryOver = false;

    /**
     * SoftwareManagement.CommandExecutor.StandardOutput object type
     */
    private ObjectType stdOutputType = CommandExecutorHelper.STANDARDOUTPUT_OBJECT_TYPE;

    /**
     * SoftwareManagement.CommandExecutor.StandardError object type
     */
    private ObjectType stdErrorType = CommandExecutorHelper.STANDARDERROR_OBJECT_TYPE;

    /**
     * List of App instance ids that are the source of logs object (StandardOutput and StandardError
     * events)
     */
    private ArrayList<Long> sourceAppInstanceIds = new ArrayList<>();

    /**
     * SoftwareManagement.AppsLaunch.App object type
     */
    private ObjectType appType = AppsLauncherHelper.APP_OBJECT_TYPE;

    /**
     * Maps of App instance id to their AppDetails object.
     */
    private HashMap<Long, AppDetails> appsDetails = new HashMap<>();

    /**
     * Set of App names that have logs associated to them.
     */
    private HashSet<String> appWithLogs = new HashSet<>();

    /**
     * Dumps an archive objects output received from an archive query answer (update or response).
     *
     * @param archiveObjectOutput the archive objects outputs
     */
    private synchronized void dumpArchiveObjectsOutput(ArchiveCOMObjectsOutput archiveObjectOutput) {
        // empty comType means query returned nothing
        ObjectType comType = archiveObjectOutput.getObjectType();
        if (comType == null) {
            return;
        }

        // we got StandardOutput or StandardError events
        if (comType.equals(stdOutputType) || comType.equals(stdErrorType)) {
            for (ArchiveDetails archiveDetails : archiveObjectOutput.getArchiveDetailsList()) {
                ObjectDetails objectDetails = archiveDetails.getDetails();
                ObjectId source = objectDetails == null ? null : objectDetails.getSource();
                ObjectType sourceType = source == null ? null : source.getType();
                // events was generated by App, stopre App instance id
                if (appType.equals(sourceType)) {
                    if (source.getKey() != null) {
                        sourceAppInstanceIds.add(source.getKey().getInstId());
                    }
                }
            }
        }
        // we got App objects
        else if (comType.equals(appType)) {
            // we store their details for names retrieval later
            if (archiveObjectOutput.getObjectBodies() != null) {
                for (int i = 0; i < archiveObjectOutput.getObjectBodies().size(); i++) {
                    ArchiveDetails archiveDetails = archiveObjectOutput.getArchiveDetailsList().get(i);
                    AppDetails appDetails = (AppDetails) archiveObjectOutput.getObjectBodies().get(i);
                    appsDetails.put(archiveDetails.getInstId(), appDetails);
                }
            }
        }
    }

    /**
     * Creates the list of App names that have logs associated to them.
     */
    private synchronized void computeAppList() {
        for (Long appInstanceId : sourceAppInstanceIds) {
            AppDetails appDetails = appsDetails.get(appInstanceId);
            if (appDetails != null) {
                appWithLogs.add(appDetails.getName().getValue());
            }
        }
    }

    @Override
    public void queryResponseReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain,
                                      ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {
        dumpArchiveObjectsOutput(new ArchiveCOMObjectsOutput(domain, objType, objDetails, objBodies));
        setIsQueryOver(true);
    }

    @Override
    public void queryUpdateReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain,
                                    ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {
        dumpArchiveObjectsOutput(new ArchiveCOMObjectsOutput(domain, objType, objDetails, objBodies));
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

    /** {@inheritDoc} */
    @Override
    public synchronized boolean isQueryOver() {
        return isQueryOver;
    }

    private synchronized void setIsQueryOver(boolean isQueryOver) {
        if (isQueryOver) {
            // once response or error is received, we close the LOG file
            computeAppList();
        }
        this.isQueryOver = isQueryOver;
    }

    /**
     * @return Returns the list of App names with logs.
     */
    public ArrayList<String> getAppWithLogs() {
        return new ArrayList<>(appWithLogs);
    }
}
