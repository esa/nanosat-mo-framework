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
package esa.mo.nmf.clitool;

import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.nmf.clitool.adapters.ArchiveToAppListAdapter;
import esa.mo.nmf.clitool.adapters.ArchiveToLogAdapter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Log commands implementations
 *
 * @author Tanguy Soto
 * @author Marcel Mikołajko
 */
@Command(name = "log", subcommands = {LogsCommands.ListLogs.class, LogsCommands.GetLogs.class},
        description = "Gets or lists NMF app logs using the content of a local or remote COM archive.")
public class LogsCommands {

    private static final Logger LOGGER = Logger.getLogger(LogsCommands.class.getName());

    @Command(name = "list", description = "Lists NMF apps having logs in the content of a local or remote COM archive.")
    public static class ListLogs extends BaseCommand implements Runnable {

        @Option(names = {"-d", "--domain"}, paramLabel = "<domainId>",
                description = "Restricts the list to NMF apps in a specific domain\n"
                + "  - default: search for app in all domains\n" + "  - format: key1.key2.[...].keyN.\n"
                + "  - example: esa.NMF_SDK.nanosat-mo-supervisor")
        String domainId;

        @Option(names = {"-s", "--start"}, paramLabel = "<startTime>",
                description = "Restricts the list to NMF apps having logs logged after the given time\n"
                + "  - format: \"yyyy-MM-dd HH:mm:ss.SSS\"\n" + "  - example: \"2021-03-04 08:37:58.482\"")
        String startTime;

        @Option(names = {"-e", "--end"}, paramLabel = "<endTime>",
                description = "Restricts the list to NMF apps having logs logged before the given time. "
                + "If this option is provided without the -s option, returns the single "
                + "object that has the closest timestamp to, but not greater than <endTime>\n"
                + "  - format: \"yyyy-MM-dd HH:mm:ss.SSS\"\n" + "  - example: \"2021-03-05 12:05:45.271\"")
        String endTime;

        @Override
        public void run() {
            // Query all objects from SoftwareManagement area filtering for
            // StandardOutput and StandardError events and App object is done in the query adapter
            ObjectType objectsTypes = new ObjectType(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NUMBER,
                    new UShort(0), SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_VERSION, new UShort(0));

            boolean consumerCreated = false;
            if (providerURI != null) {
                consumerCreated = initRemoteConsumer();
            } else if (databaseFile != null) {
                consumerCreated = initLocalConsumer(databaseFile);
            }

            if (!consumerCreated) {
                LOGGER.log(Level.SEVERE, "Failed to create consumer!");
                return;
            }
            // prepare domain, time and object id filters
            IdentifierList domain = domainId == null ? null : HelperMisc.domainId2domain(domainId);
            ArchiveQueryList archiveQueryList = new ArchiveQueryList();
            FineTime startTimeF = startTime == null ? null : HelperTime.readableString2FineTime(startTime);
            FineTime endTimeF = endTime == null ? null : HelperTime.readableString2FineTime(endTime);
            ArchiveQuery archiveQuery = new ArchiveQuery(domain, null, null, 0L, null, startTimeF, endTimeF, null,
                    null);
            archiveQueryList.add(archiveQuery);

            // execute query
            ArchiveToAppListAdapter adapter = new ArchiveToAppListAdapter();
            queryArchive(objectsTypes, archiveQueryList, adapter, adapter);

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
        }
    }

    @Command(name = "get",
            description = "Dumps to a LOG file an NMF app logs using the content of a local or remote COM archive.")
    public static class GetLogs extends BaseCommand implements Runnable {

        @Parameters(arity = "1", paramLabel = "<appName>", description = "Name of the NMF app we want the logs for")
        String appName;

        @Parameters(arity = "1", paramLabel = "<logFile>", description = "target LOG file")
        String logFile;

        @Option(names = {"-d", "--domain"}, paramLabel = "<domainId>",
                description = "Domain of the NMF app we want the logs for\n"
                + "  - default: search for app in all domains\n" + "  - format: key1.key2.[...].keyN.\n"
                + "  - example: esa.NMF_SDK.nanosat-mo-supervisor")
        String domainId;

        @Option(names = {"-s", "--start"}, paramLabel = "<startTime>",
                description = "Restricts the dump to logs logged after the given time\n"
                + "  - format: \"yyyy-MM-dd HH:mm:ss.SSS\"\n" + "  - example: \"2021-03-04 08:37:58.482\"")
        String startTime;

        @Option(names = {"-e", "--end"}, paramLabel = "<endTime>",
                description = "Restricts the dump to logs logged before the given time. "
                + "If this option is provided without the -s option, returns the single "
                + "object that has the closest timestamp to, but not greater than <endTime>\n"
                + "  - format: \"yyyy-MM-dd HH:mm:ss.SSS\"\n" + "  - example: \"2021-03-05 12:05:45.271\"")
        String endTime;

        @Option(names = {"-t", "--timestamped"}, paramLabel = "<addTimestamps>",
                description = "If specified additional timestamp will be added to each line")
        boolean addTimestamps;

        @Override
        public void run() {
            // Query all objects from SoftwareManagement area and CommandExecutor service,
            // filtering for StandardOutput and StandardError events is done in the query adapter
            ObjectType outputObjectTypes = new ObjectType(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NUMBER,
                    CommandExecutorHelper.COMMANDEXECUTOR_SERVICE_NUMBER,
                    SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_VERSION, new UShort(0));

            ObjectType eventObjectTypes = new ObjectType(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NUMBER,
                    AppsLauncherHelper.APPSLAUNCHER_SERVICE_NUMBER,
                    SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_VERSION, new UShort(0));

            boolean consumerCreated = false;
            if (providerURI != null) {
                consumerCreated = initRemoteConsumer();
            } else if (databaseFile != null) {
                consumerCreated = initLocalConsumer(databaseFile);
            }

            if (!consumerCreated) {
                LOGGER.log(Level.SEVERE, "Failed to create consumer!");
                return;
            }
            // Query archive for the App object id
            IdentifierList domain = domainId == null ? null : HelperMisc.domainId2domain(domainId);
            ObjectId appObjectId = getAppObjectId(appName, domain);

            if (appObjectId == null) {
                if (databaseFile == null) {
                    LOGGER.log(Level.SEVERE, String.format("Couldn't find App with name %s in provider at %s", appName,
                            providerURI));
                } else {
                    LOGGER.log(Level.SEVERE, String.format("Couldn't find App with name %s in database at %s", appName,
                            databaseFile));
                }
                return;
            }

            // prepare domain, time and object id filters
            ArchiveQueryList archiveQueryList = new ArchiveQueryList();
            FineTime startTimeF = startTime == null ? null : HelperTime.readableString2FineTime(startTime);
            FineTime endTimeF = endTime == null ? null : HelperTime.readableString2FineTime(endTime);
            ArchiveQuery outputArchiveQuery = new ArchiveQuery(domain, null, null, 0L, appObjectId, startTimeF,
                    endTimeF, null, null);
            archiveQueryList.add(outputArchiveQuery);

            // execute query
            ArchiveToLogAdapter adapter = new ArchiveToLogAdapter(logFile, addTimestamps);
            queryArchive(outputObjectTypes, archiveQueryList, adapter, adapter);

            archiveQueryList.clear();
            ArchiveQuery eventArchiveQuery = new ArchiveQuery(domain, null, null, appObjectId.getKey().getInstId(),
                    null, startTimeF, endTimeF, null, null);
            archiveQueryList.add(eventArchiveQuery);
            adapter.resetAdapter();
            queryArchive(eventObjectTypes, archiveQueryList, adapter, adapter);

            adapter.dumpArchiveObjectsOutput();
        }
    }
}
