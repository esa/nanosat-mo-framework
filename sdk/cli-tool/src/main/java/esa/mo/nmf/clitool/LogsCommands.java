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

import esa.mo.nmf.clitool.adapters.ArchiveToAppListAdapter;
import esa.mo.nmf.clitool.adapters.ArchiveToLogAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperDomain;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperTime;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.sm.SMHelper;
import org.ccsds.moims.mo.sm.appslauncher.AppsLauncherServiceInfo;
import org.ccsds.moims.mo.sm.commandexecutor.CommandExecutorServiceInfo;

/**
 * Log commands implementations
 *
 * @author Tanguy Soto
 * @author Marcel Mikołajko
 */
public class LogsCommands {

    private static final Logger LOGGER =
            Logger.getLogger(LogsCommands.class.getName());

    private LogsCommands() {
    }

    /**
     * Implements the {@code log list} CLI command.
     */
    public static class ListLogs extends BaseCommand {
        /**
         * Default constructor.
         */
        public ListLogs() {
        }


        @Override
        public void run(Args args) {
            parseBaseOptions(args);
            String domainId  = args.option("-d", "--domain");
            String startTime = args.option("-s", "--start");
            String endTime   = args.option("-e", "--end");

            // Query all objects from SoftwareManagement area filtering for
            // StandardOutput and StandardError events and App object is done in the query adapter
            ObjectType objectsTypes = new ObjectType(
                    SMHelper.SM_AREA_NUMBER,
                    new UShort(0),
                    SMHelper.SM_AREA_VERSION,
                    new UShort(0));

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
            IdentifierList domain = domainId == null ? null : HelperDomain.domainId2domain(domainId);
            Time startTimeF = startTime == null ? null : HelperTime.readableString2Time(startTime);
            Time endTimeF = endTime == null ? null : HelperTime.readableString2Time(endTime);
            ArchiveQuery archiveQuery = new ArchiveQuery(domain, null, 0L,
                    null, startTimeF, endTimeF, null, null);

            // execute query
            ArchiveToAppListAdapter adapter = new ArchiveToAppListAdapter();
            queryArchive(objectsTypes, archiveQuery, adapter, adapter);

            // Display list of NMF apps that have logs
            ArrayList<String> appsWithLogs = adapter.getAppWithLogs();
            if (appsWithLogs.size() <= 0) {
                System.out.println(
                        "No NMF apps with logs found in the provided archive");
            } else {
                System.out.println("Found the following NMF apps with logs: ");
                for (String appName : appsWithLogs) {
                    System.out.println("\t - " + appName);
                }
            }
        }
    }

    /**
     * Implements the {@code log get} CLI command.
     */
    public static class GetLogs extends BaseCommand {
        /**
         * Default constructor.
         */
        public GetLogs() {
        }


        @Override
        public void run(Args args) {
            parseBaseOptions(args);
            String domainId  = args.option("-d", "--domain");
            String startTime = args.option("-s", "--start");
            String endTime   = args.option("-e", "--end");
            boolean addTimestamps = args.flag("-t", "--timestamped");

            List<String> positionals = args.positionals();
            if (positionals.size() < 2) {
                System.out.println("Usage: log get <appName> <logFile>");
                return;
            }
            String appName = positionals.get(0);
            String logFile = positionals.get(1);

            // Query all objects from SoftwareManagement area and CommandExecutor service,
            // filtering for StandardOutput and StandardError events is done in the query adapter
            ObjectType outputObjectTypes = new ObjectType(
                    SMHelper.SM_AREA_NUMBER,
                    CommandExecutorServiceInfo.COMMANDEXECUTOR_SERVICE_NUMBER,
                    SMHelper.SM_AREA_VERSION,
                    new UShort(0));

            ObjectType eventObjectTypes = new ObjectType(
                    SMHelper.SM_AREA_NUMBER,
                    AppsLauncherServiceInfo.APPSLAUNCHER_SERVICE_NUMBER,
                    SMHelper.SM_AREA_VERSION,
                    new UShort(0));

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
            IdentifierList domain = domainId == null ? null : HelperDomain.domainId2domain(domainId);
            ObjectKey appObjectKey = getAppObjectKey(appName, domain);

            if (appObjectKey == null) {
                if (databaseFile == null) {
                    LOGGER.log(Level.SEVERE, String.format(
                            "Couldn't find App with name %s in provider at %s",
                            appName, providerURI));
                } else {
                    LOGGER.log(Level.SEVERE, String.format(
                            "Couldn't find App with name %s in database at %s",
                            appName, databaseFile));
                }
                return;
            }

            // prepare domain, time and object id filters
            Time startTimeF = startTime == null ? null : HelperTime.readableString2Time(startTime);
            Time endTimeF = endTime == null ? null : HelperTime.readableString2Time(endTime);
            ArchiveQuery outputArchiveQuery = new ArchiveQuery(domain, null,
                    0L, appObjectKey, startTimeF, endTimeF, null, null);

            // execute query
            ArchiveToLogAdapter adapter = new ArchiveToLogAdapter(logFile, addTimestamps);
            queryArchive(outputObjectTypes, outputArchiveQuery, adapter, adapter);

            ArchiveQuery eventArchiveQuery = new ArchiveQuery(domain, null,
                    appObjectKey.getId(), null, startTimeF, endTimeF, null, null);
            adapter.resetAdapter();
            queryArchive(eventObjectTypes, eventArchiveQuery, adapter, adapter);

            adapter.dumpArchiveObjectsOutput();
        }
    }
}
