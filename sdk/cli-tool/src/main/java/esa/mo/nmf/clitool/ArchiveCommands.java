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
import esa.mo.com.impl.consumer.DirectoryConsumerServiceImpl;
import esa.mo.com.impl.util.ArchiveCOMObjectsOutput;
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
import org.ccsds.moims.mo.com.archive.ArchiveServiceInfo;
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.com.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperDomain;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperTime;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.structures.Time;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Archive commands implementations
 *
 * @author Tanguy Soto
 * @author Marcel Mikołajko
 */
public class ArchiveCommands {

    private static final Logger LOGGER = Logger.getLogger(ArchiveCommands.class.getName());

    public static class DumpRawArchive extends BaseCommand {

        @Override
        public void run(Args args) {
            parseBaseOptions(args);
            List<String> positionals = args.positionals();
            if (positionals.isEmpty()) {
                System.out.println("Missing required argument: <jsonFile>");
                return;
            }
            String jsonFile = positionals.get(0);

            if (databaseFile == null) {
                System.out.println("Missing required option: -l/--local <databaseFile>");
                return;
            }

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
            try ( Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFile)) {
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
            try ( FileWriter file = new FileWriter(jsonFile)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String prettyJsonString = gson.toJson(tables);
                file.write(prettyJsonString);
                file.flush();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, String.format("Error writing JSON file %s", jsonFile), e);
            }
        }
    }

    public static class DumpFormattedArchive extends BaseCommand {

        @Override
        public void run(Args args) {
            parseBaseOptions(args);
            String domainId  = args.option("-d", "--domain");
            String comType   = args.option("-t", "--type");
            String startTime = args.option("-s", "--start");
            String endTime   = args.option("-e", "--end");

            List<String> positionals = args.positionals();
            if (positionals.isEmpty()) {
                System.out.println("Missing required argument: <jsonFile>");
                return;
            }
            String jsonFile = positionals.get(0);

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
            IdentifierList domain = domainId == null ? null : HelperDomain.domainId2domain(domainId);
            Time startTimeF = startTime == null ? null : HelperTime.readableString2Time(startTime);
            Time endTimeF = endTime == null ? null : HelperTime.readableString2Time(endTime);
            ArchiveQuery archiveQuery = new ArchiveQuery(domain, null, null, 0L,
                    null, startTimeF, endTimeF, null, null);

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
            queryArchive(objectsTypes, archiveQuery, adapter, adapter);
        }
    }

    public static class ListArchiveProviders extends BaseCommand {

        @Override
        public void run(Args args) {
            parseBaseOptions(args);
            List<String> positionals = args.positionals();
            if (positionals.isEmpty()) {
                System.out.println("Missing required argument: <centralDirectoryURI>");
                return;
            }

            String centralDirectoryURI = positionals.get(0);
            ArrayList<String> archiveProviderURIs = listCOMArchiveProviders(new URI(centralDirectoryURI));

            // No provider found warning
            if (archiveProviderURIs.size() <= 0) {
                LOGGER.log(Level.WARNING, String.format(
                        "No COM archive provider found in central directory at %s",
                        centralDirectoryURI));
                return;
            }

            // List providers found
            System.out.println("Found the following COM archive providers: ");
            for (String provUri : archiveProviderURIs) {
                System.out.println(String.format(" - %s", provUri));
            }
        }
    }

    /**
     * Look up the central directory to find the list of providers that provides
     * a COM archive service.
     *
     * @param centralDirectoryServiceURI URI of the central directory to use
     * @return The list of providers
     */
    public static ArrayList<String> listCOMArchiveProviders(URI centralDirectoryServiceURI) {
        ArrayList<String> archiveProviders = new ArrayList<>();

        // Create archive provider filter
        IdentifierList domain = new IdentifierList();
        domain.add(new Identifier("*"));
        ServiceId sk = new ServiceId(COMHelper.COM_AREA_NUMBER,
                ArchiveServiceInfo.ARCHIVE_SERVICE_NUMBER, new UOctet((short) 0));
        ServiceFilter sf2 = new ServiceFilter(new Identifier("*"), domain, sk, null);

        // Query directory service with filter
        try {
            DirectoryConsumerServiceImpl centralDirectory = new DirectoryConsumerServiceImpl(centralDirectoryServiceURI);
            ProviderList providers = centralDirectory.getDirectoryStub().lookup(sf2);
            for (Provider p : providers) {
                final StringBuilder provider = new StringBuilder(p.getProviderName().getValue());

                // dump provider addresses
                for (AddressDetails addressDetails : p.getProviderAddresses()) {
                    provider.append("\n\t - ").append(addressDetails.getServiceURI().getValue());
                }

                // dump services capabilities addresses
                for (ServiceCapability serviceCapability : p.getServiceCapabilities()) {
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

    public static class BackupProvider extends BaseCommand {

        @Override
        public void run(Args args) {
            parseBaseOptions(args);
            String filename = args.option("-o", "--output");
            List<String> positionals = args.positionals();
            if (positionals.isEmpty()) {
                System.out.println("Missing required argument: <domainId>");
                return;
            }
            String domainId = positionals.get(0);

            if (!super.initRemoteConsumer()) {
                return;
            }
            ObjectType objectsTypes = new ObjectType(new UShort(0),
                    new UShort(0), new UOctet((short) 0), new UShort(0));
            IdentifierList domain = domainId == null ? null : HelperDomain.domainId2domain(domainId);
            ArchiveQuery archiveQuery = new ArchiveQuery(domain, null, null,
                    0L, null, null, null, null, null);

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
            queryArchive(objectsTypes, archiveQuery, adapter, adapter);

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
