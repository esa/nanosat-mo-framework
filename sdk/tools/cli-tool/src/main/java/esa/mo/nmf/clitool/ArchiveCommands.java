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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.util.ArchiveCOMObjectsOutput;
import esa.mo.common.impl.consumer.DirectoryConsumerServiceImpl;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.nmf.clitool.adapters.ArchiveToBackupAdapter;
import esa.mo.nmf.clitool.adapters.ArchiveToJsonAdapter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.ArchiveHelper;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.common.directory.structures.*;
import org.ccsds.moims.mo.common.structures.ServiceKey;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Archive commands implementations
 *
 * @author Tanguy Soto
 * @author Marcel Mikołajko
 */
@Command(name = "archive", subcommands = {ArchiveCommands.DumpRawArchive.class,
                                          ArchiveCommands.DumpFormattedArchive.class,
                                          ArchiveCommands.ListArchiveProviders.class,
                                          ArchiveCommands.BackupProvider.class})
public class ArchiveCommands {

    private static final Logger LOGGER = Logger.getLogger(ArchiveCommands.class.getName());

    @Command(name = "dump_raw", description = "Dumps to a JSON file the raw tables content of a local COM archive")
    public static class DumpRawArchive extends BaseCommand implements Runnable {
        @Parameters(arity = "1", paramLabel = "<jsonFile>", description = "target JSON file")
        String jsonFile;

        @Override
        public void run() {
            // Test if DB file exists
            File temp = new File(databaseFile);
            if (!temp.exists() || temp.isDirectory()) {
                LOGGER.log(Level.SEVERE, String.format("Provided database file %s doesn't exist or is a directory",
                    databaseFile));
                return;
            }

            // root JSON object
            JSONArray tables = new JSONArray();

            // parse DB
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFile)) {
                // for each table
                ResultSet tablesNamesRs = conn.getMetaData().getTables(null, null, null, null);
                while (tablesNamesRs.next()) {
                    // query table
                    String table = tablesNamesRs.getString("TABLE_NAME");
                    String selectAllQuery = "SELECT  * FROM " + table;
                    ResultSet rowsRs = conn.createStatement().executeQuery(selectAllQuery);
                    ResultSetMetaData rowsRsMeta = rowsRs.getMetaData();

                    // table JSON object
                    JSONArray rows = new JSONArray();
                    JSONObject jsonTable = new JSONObject();
                    jsonTable.put(table, rows);

                    // for each row
                    while (rowsRs.next()) {
                        JSONObject rowObject = new JSONObject();

                        // for each column
                        for (int i = 0; i < rowsRsMeta.getColumnCount(); i++) {
                            String columnName = rowsRsMeta.getColumnName(i + 1);
                            String columnValue = rowsRs.getString(i + 1) == null ? "null" : rowsRs.getString(i + 1);
                            rowObject.put(columnName, columnValue);
                        }
                        rows.add(rowObject);
                    }
                    tables.add(jsonTable);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, String.format("SQL error reading %s", databaseFile), e);
            }

            // write JSON file
            try (FileWriter file = new FileWriter(jsonFile)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String prettyJsonString = gson.toJson(tables);
                file.write(prettyJsonString);
                file.flush();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, String.format("Error writing JSON file %s", jsonFile), e);
            }
        }
    }

    @Command(name = "dump", description = "Dumps to a JSON file the formatted content of a local or remote COM archive")
    public static class DumpFormattedArchive extends BaseCommand implements Runnable {
        @Parameters(arity = "1", paramLabel = "<jsonFile>", description = "target JSON file")
        String jsonFile;

        @Option(names = {"-d", "--domain"}, paramLabel = "<domainId>",
                description = "Restricts the dump to objects in a specific domain\n" +
                    "  - format: key1.key2.[...].keyN.\n" + "  - example: esa.NMF_SDK.nanosat-mo-supervisor")
        String domainId;

        @Option(names = {"-t", "--type"}, paramLabel = "<comType>",
                description = "Restricts the dump to objects that are instances of <comType>\n" +
                    "  - format: areaNumber.serviceNumber.areaVersion.objectNumber.\n" +
                    "  - examples (0=wildcard): 4.2.1.1, 4.2.1.0 ")
        String comType;

        @Option(names = {"-s", "--start"}, paramLabel = "<startTime>",
                description = "Restricts the dump to objects created after the given time\n" +
                    "  - format: \"yyyy-MM-dd HH:mm:ss.SSS\"\n" + "  - example: \"2021-03-04 08:37:58.482\"")
        String startTime;

        @Option(names = {"-e", "--end"}, paramLabel = "<endTime>",
                description = "Restricts the dump to objects created before the given time. " +
                    "If this option is provided without the -s option, returns the single object that has the closest timestamp to, but not greater than <endTime>\n" +
                    "  - format: \"yyyy-MM-dd HH:mm:ss.SSS\"\n" + "  - example: \"2021-03-05 12:05:45.271\"")
        String endTime;

        @Override
        public void run() {
            // prepare comType filter
            int areaNumber = 0;
            int serviceNumber = 0;
            int areaVersion = 0;
            int objectNumber = 0;

            if (comType != null) {
                String[] subTypes = comType.split("\\.");
                if (subTypes.length == 4) {
                    areaNumber = Integer.parseInt(subTypes[0]);
                    serviceNumber = Integer.parseInt(subTypes[1]);
                    areaVersion = Integer.parseInt(subTypes[2]);
                    objectNumber = Integer.parseInt(subTypes[3]);
                } else {
                    LOGGER.log(Level.WARNING, String.format("Error parsing comType \"%s\", filter will be ignored",
                        comType));
                }
            }

            ObjectType objectsTypes = new ObjectType(new UShort(areaNumber), new UShort(serviceNumber), new UOctet(
                (short) areaVersion), new UShort(objectNumber));

            // prepare domain and time filters
            ArchiveQueryList archiveQueryList = new ArchiveQueryList();
            IdentifierList domain = domainId == null ? null : HelperMisc.domainId2domain(domainId);
            FineTime startTimeF = startTime == null ? null : HelperTime.readableString2FineTime(startTime);
            FineTime endTimeF = endTime == null ? null : HelperTime.readableString2FineTime(endTime);
            ArchiveQuery archiveQuery = new ArchiveQuery(domain, null, null, 0L, null, startTimeF, endTimeF, null,
                null);
            archiveQueryList.add(archiveQuery);

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
            // execute query
            ArchiveToJsonAdapter adapter = new ArchiveToJsonAdapter(jsonFile);
            queryArchive(objectsTypes, archiveQueryList, adapter, adapter);
        }
    }

    @Command(name = "list", description = "Lists the COM archive providers URIs found in a central directory")
    public static class ListArchiveProviders extends BaseCommand implements Runnable {
        @Parameters(arity = "1", paramLabel = "<centralDirectoryURI>",
                    description = "URI of the central directory to use")
        String centralDirectoryURI;

        /**
         * Lists the COM archive providers URIs found in the central directory.
         */
        @Override
        public void run() {
            ArrayList<String> archiveProviderURIs = listCOMArchiveProviders(new URI(centralDirectoryURI));

            // No provider found warning
            if (archiveProviderURIs.size() <= 0) {
                LOGGER.log(Level.WARNING, String.format("No COM archive provider found in central directory at %s",
                    centralDirectoryURI));
                return;
            }

            // List providers found
            System.out.println("Found the following COM archive providers: ");
            for (String providerURI : archiveProviderURIs) {
                System.out.println(String.format(" - %s", providerURI));
            }
        }
    }

    /**
     * Look up the central directory to find the list of providers that provides a COM archive
     * service.
     *
     * @param centralDirectoryServiceURI URI of the central directory to use
     * @return The list of providers
     */
    public static ArrayList<String> listCOMArchiveProviders(URI centralDirectoryServiceURI) {
        ArrayList<String> archiveProviders = new ArrayList<>();

        // Create archive provider filter
        IdentifierList domain = new IdentifierList();
        domain.add(new Identifier("*"));
        ServiceKey sk = new ServiceKey(COMHelper.COM_AREA_NUMBER, ArchiveHelper.ARCHIVE_SERVICE_NUMBER, new UOctet(
            (short) 0));
        ServiceFilter sf2 = new ServiceFilter(new Identifier("*"), domain, new Identifier("*"), null, new Identifier(
            "*"), sk, new UShortList());

        // Query directory service with filter
        try (DirectoryConsumerServiceImpl centralDirectory = new DirectoryConsumerServiceImpl(
            centralDirectoryServiceURI)) {
            ProviderSummaryList providersSummaries = centralDirectory.getDirectoryStub().lookupProvider(sf2);
            for (ProviderSummary providerSummary : providersSummaries) {
                final StringBuilder provider = new StringBuilder(providerSummary.getProviderId().getValue());

                ProviderDetails providerDetails = providerSummary.getProviderDetails();

                // dump provider addresses
                for (AddressDetails addressDetails : providerDetails.getProviderAddresses()) {
                    provider.append("\n\t - ").append(addressDetails.getServiceURI().getValue());
                }

                // dump services capabilities addresses
                for (ServiceCapability serviceCapability : providerDetails.getServiceCapabilities()) {
                    for (AddressDetails serviceAddressDetails : serviceCapability.getServiceAddresses()) {
                        provider.append("\n\t - ").append(serviceAddressDetails.getServiceURI().getValue());
                    }
                }
                archiveProviders.add(provider.toString());
            }
        } catch (MALInteractionException | MALException | MalformedURLException e) {
            LOGGER.log(Level.SEVERE, "Error while looking up the central directory", e);
        }

        return archiveProviders;
    }

    @Command(name = "backup_and_clean", description = "Backups the data for a specific provider")
    public static class BackupProvider extends BaseCommand implements Runnable {
        @Option(names = {"-o", "--output"}, paramLabel = "<filename>", description = "target file name")
        String filename;

        @Parameters(arity = "1", index = "0", paramLabel = "<domainId>",
                    description = "Restricts the dump to objects in a specific domain\n" +
                        "  - format: key1.key2.[...].keyN.\n" + "  - example: esa.NMF_SDK.nanosat-mo-supervisor")
        String domainId;

        @Override
        public void run() {
            if (!super.initRemoteConsumer()) {
                return;
            }
            ObjectType objectsTypes = new ObjectType(new UShort(0), new UShort(0), new UOctet((short) 0), new UShort(
                0));
            ArchiveQueryList archiveQueryList = new ArchiveQueryList();
            IdentifierList domain = domainId == null ? null : HelperMisc.domainId2domain(domainId);
            ArchiveQuery archiveQuery = new ArchiveQuery(domain, null, null, 0L, null, null, null, null, null);
            archiveQueryList.add(archiveQuery);

            if (filename != null) {
                if (!filename.endsWith(".db")) {
                    filename = filename + ".db";
                }
            } else {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd__HH-mm-ss");
                LocalDateTime now = LocalDateTime.now();
                filename = domainId + "__" + dtf.format(now) + ".db";
            }
            ArchiveToBackupAdapter adapter = new ArchiveToBackupAdapter();
            queryArchive(objectsTypes, archiveQueryList, adapter, adapter);

            File dbFile = new File(filename);
            if (!dbFile.exists()) {
                try {
                    dbFile.createNewFile();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Failed to create new database file", e);
                    return;
                }
            }
            initLocalArchiveProvider(filename);
            System.out.println("\nSaving data to " + filename + " started.\n");
            boolean success = adapter.saveDataToNewDatabase(localArchiveProvider);
            System.out.println("\nSaving finished.\n");

            if (success) {
                ArchiveConsumerServiceImpl archive = consumer.getCOMServices().getArchiveService();

                System.out.println("\nDeleting objects from provider archive started.");
                LongList ids = new LongList();
                ids.add(0L);
                try {
                    List<ArchiveCOMObjectsOutput> toDelete = adapter.getObjectsToProcess();
                    for (ArchiveCOMObjectsOutput objects : toDelete) {
                        archive.getArchiveStub().delete(objects.getObjectType(), objects.getDomain(), ids);
                    }
                } catch (MALInteractionException | MALException e) {
                    LOGGER.log(Level.SEVERE, "Error during delete!", e);
                }
                System.out.println("Deleting objects from provider finished.\n");
            } else {
                System.out.println(
                    "\nThere were errors when saving data. Not deleting objects from provider archive.\n");
            }
        }
    }
}
