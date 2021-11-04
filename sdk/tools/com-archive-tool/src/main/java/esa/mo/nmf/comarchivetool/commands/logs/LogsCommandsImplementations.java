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
package esa.mo.nmf.comarchivetool.commands.logs;

import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.nmf.NMFConsumer;
import esa.mo.nmf.comarchivetool.adapters.ArchiveToAppListAdapter;
import esa.mo.nmf.comarchivetool.adapters.ArchiveToLogAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.softwaremanagement.SoftwareManagementHelper;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;
import org.ccsds.moims.mo.softwaremanagement.commandexecutor.CommandExecutorHelper;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static esa.mo.nmf.comarchivetool.ArchiveBrowserHelper.*;

/**
 * Log commands implementations
 *
 * @author Tanguy Soto
 * @author Marcel Mikołajko
 */
public class LogsCommandsImplementations {

    private static final Logger LOGGER = Logger.getLogger(LogsCommandsImplementations.class.getName());

    /**
     * Lists NMF apps having logs in the content of a local or remote COM archive.
     *
     * @param databaseFile Local SQLite database file
     * @param providerURI The URI of the remote COM archive provider
     * @param domainId Restricts the dump to objects in a specific domain ID
     * @param startTime Restricts the dump to objects created after the given time
     * @param endTime Restricts the dump to objects created before the given time. If this option is
     *        provided without the -s option, returns the single object that has the closest time
     *        stamp to, but not greater than endTime.
     */
    public static void listLogs(String databaseFile, String providerURI, String domainId,
                                String startTime, String endTime) {
        // Query all objects from SoftwareManagement area filtering for
        // StandardOutput and StandardError events and App object is done in the query adapter
        ObjectType objectsTypes =
                new ObjectType(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NUMBER, new UShort(0),
                               SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_VERSION, new UShort(0));

        LocalOrRemoteConsumer consumers = createConsumer(providerURI, databaseFile);
        ArchiveConsumerServiceImpl localConsumer = consumers.getLocalConsumer();
        NMFConsumer remoteConsumer = consumers.getRemoteConsumer();

        // prepare domain, time and object id filters
        IdentifierList domain = domainId == null ? null : HelperMisc.domainId2domain(domainId);
        ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        FineTime startTimeF = startTime == null ? null : HelperTime.readableString2FineTime(startTime);
        FineTime endTimeF = endTime == null ? null : HelperTime.readableString2FineTime(endTime);
        ArchiveQuery archiveQuery =
                new ArchiveQuery(domain, null, null, 0L, null, startTimeF, endTimeF, null, null);
        archiveQueryList.add(archiveQuery);

        // execute query
        ArchiveToAppListAdapter adapter = new ArchiveToAppListAdapter();
        queryArchive(objectsTypes, archiveQueryList, adapter, adapter, remoteConsumer == null ? localConsumer : remoteConsumer.getCOMServices().getArchiveService());

        // Display list of NMF apps that have logs
        ArrayList<String> appsWithLogs = adapter.getAppWithLogs();
        if (appsWithLogs.size() <= 0) {
            System.out.println("No NMF apps with logs found in the provided archive");
        } else {
            System.out.println("Found the following NMF apps with logs: ");
            for (String appName : appsWithLogs) {
                System.out.println("\t - " + appName);
            }
        }
        closeConsumer(consumers);
    }

    /**
     * Dumps to a LOG file an NMF app logs using the content of a local or remote COM archive.
     *
     * @param databaseFile Local SQLite database file
     * @param providerURI The URI of the remote COM archive provider
     * @param appName Name of the NMF app we want the logs for
     * @param domainId Restricts the dump to objects in a specific domain ID
     * @param startTime Restricts the dump to objects created after the given time
     * @param endTime Restricts the dump to objects created before the given time. If this option is
     *        provided without the -s option, returns the single object that has the closest time
     *        stamp to, but not greater than endTime.
     * @param logFile target LOG file
     */
    public static void getLogs(String databaseFile, String providerURI, String appName,
                               String domainId, String startTime, String endTime, String logFile, boolean addTimestamps) {
        // Query all objects from SoftwareManagement area and CommandExecutor service,
        // filtering for StandardOutput and StandardError events is done in the query adapter
        ObjectType outputObjectTypes =
                new ObjectType(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NUMBER,
                               CommandExecutorHelper.COMMANDEXECUTOR_SERVICE_NUMBER,
                               SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_VERSION, new UShort(0));

        ObjectType eventObjectTypes =
                new ObjectType(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NUMBER,
                               AppsLauncherHelper.APPSLAUNCHER_SERVICE_NUMBER,
                               SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_VERSION, new UShort(0));

        LocalOrRemoteConsumer consumers = createConsumer(providerURI, databaseFile);
        ArchiveConsumerServiceImpl localConsumer = consumers.getLocalConsumer();
        NMFConsumer remoteConsumer = consumers.getRemoteConsumer();

        // Query archive for the App object id
        IdentifierList domain = domainId == null ? null : HelperMisc.domainId2domain(domainId);
        ObjectId appObjectId = getAppObjectId(appName, domain, remoteConsumer == null ? localConsumer : remoteConsumer.getCOMServices().getArchiveService());

        if (appObjectId == null) {
            if (databaseFile == null) {
                LOGGER.log(Level.SEVERE, String.format("Couldn't find App with name %s in provider at %s",
                                                       appName, providerURI));
            } else {
                LOGGER.log(Level.SEVERE, String.format("Couldn't find App with name %s in database at %s",
                                                       appName, databaseFile));
            }
            closeConsumer(consumers);
            return;
        }

        // prepare domain, time and object id filters
        ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        FineTime startTimeF = startTime == null ? null : HelperTime.readableString2FineTime(startTime);
        FineTime endTimeF = endTime == null ? null : HelperTime.readableString2FineTime(endTime);
        ArchiveQuery outputArchiveQuery = new ArchiveQuery(domain, null, null, 0L, appObjectId,
                                                     startTimeF, endTimeF, null, null);
        archiveQueryList.add(outputArchiveQuery);

        // execute query
        ArchiveToLogAdapter adapter = new ArchiveToLogAdapter(logFile, addTimestamps);
        queryArchive(outputObjectTypes, archiveQueryList, adapter, adapter, remoteConsumer == null ? localConsumer : remoteConsumer.getCOMServices().getArchiveService());

        archiveQueryList.clear();
        ArchiveQuery eventArchiveQuery = new ArchiveQuery(domain, null, null, appObjectId.getKey().getInstId(), null,
                                                           startTimeF, endTimeF, null, null);
        archiveQueryList.add(eventArchiveQuery);
        adapter.resetAdapter();
        queryArchive(eventObjectTypes, archiveQueryList, adapter, adapter, remoteConsumer == null ? localConsumer : remoteConsumer.getCOMServices().getArchiveService());

        adapter.dumpArchiveObjectsOutput();
        closeConsumer(consumers);
    }
}
