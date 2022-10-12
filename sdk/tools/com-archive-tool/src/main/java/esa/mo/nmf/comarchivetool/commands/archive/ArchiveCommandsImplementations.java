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
package esa.mo.nmf.comarchivetool.commands.archive;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.util.ArchiveCOMObjectsOutput;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.nmf.NMFConsumer;
import esa.mo.nmf.comarchivetool.CentralDirectoryHelper;
import esa.mo.nmf.comarchivetool.adapters.ArchiveToBackupAdapter;
import esa.mo.nmf.comarchivetool.adapters.ArchiveToJsonAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static esa.mo.nmf.comarchivetool.ArchiveBrowserHelper.*;

/**
 * Archive commands implementations
 *
 * @author Tanguy Soto
 * @author Marcel Mikołajko
 */
public class ArchiveCommandsImplementations {

    private static final Logger LOGGER = Logger.getLogger(ArchiveCommandsImplementations.class.getName());

    /**
     * Backups provider data to a sqlite database file
     *
     * @param databaseFile Local SQLite database file
     * @param providerURI The URI of the remote COM archive provider
     * @param domainId Restricts the dump to objects in a specific domain ID
     * @param filename target filename
     */
    public static void backupProvider(String databaseFile, String providerURI, String domainId, String providerName, String filename) {
        LocalOrRemoteConsumer consumers = createConsumer(providerURI, databaseFile, providerName);
        ArchiveConsumerServiceImpl localConsumer = consumers.getLocalConsumer();
        NMFConsumer remoteConsumer = consumers.getRemoteConsumer();

        ObjectType objectsTypes = new ObjectType(new UShort(0), new UShort(0),
                                                 new UOctet((short) 0), new UShort(0));
        ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        IdentifierList domain = domainId == null ? null : HelperMisc.domainId2domain(domainId);
        ArchiveQuery archiveQuery = new ArchiveQuery(domain, null, null, 0L, null,
                                                     null, null, null, null);
        archiveQueryList.add(archiveQuery);

        ArchiveToBackupAdapter adapter = new ArchiveToBackupAdapter(filename, domainId);
        queryArchive(objectsTypes, archiveQueryList, adapter, adapter, remoteConsumer == null ? localConsumer : remoteConsumer.getCOMServices().getArchiveService());

        boolean success = adapter.saveDataToNewDatabase();

        if (success) {
            ArchiveConsumerServiceImpl archive;
            if (remoteConsumer == null) {
                archive = localConsumer;
            } else {
                archive = remoteConsumer.getCOMServices().getArchiveService();
            }

            System.out.println("\nDeleting objects from provider archive started.");
            LongList ids = new LongList();
            ids.add(0L);
            try {
                List<ArchiveCOMObjectsOutput> toDelete = adapter.getObjectsToProcess();
                for(ArchiveCOMObjectsOutput objects : toDelete) {
                    archive.getArchiveStub().delete(objects.getObjectType(), objects.getDomain(), ids);
                }
            } catch (MALInteractionException | MALException e) {
                LOGGER.log(Level.SEVERE, "Error during delete!", e);
            }
            System.out.println("Deleting objects from provider finished.\n");
        } else {
            System.out.println("\nThere were errors when saving data. Not deleting objects from provider archive.\n");
        }

        closeConsumer(consumers);
        adapter.closeArchiveProvider();
    }

    /**
     * Dumps to a JSON file the raw SQLite tables content of a COM archive.
     *
     * @param databaseFile source SQLite database file
     * @param jsonFile target JSON file
     */
    public static void dumpRawArchiveTables(String databaseFile, String jsonFile) {
        // Test if DB file exists
        File temp = new File(databaseFile);
        if (!temp.exists() || temp.isDirectory()) {
            LOGGER.log(Level.SEVERE,
                       String.format("Provided database file %s doesn't exist or is a directory", databaseFile));
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

    /**
     * Dumps to a JSON file the formatted content of a local or remote COM archive.
     *
     * @param databaseFile Local SQLite database file
     * @param providerURI The URI of the remote COM archive provider
     * @param domainId Restricts the dump to objects in a specific domain ID
     * @param comType Restricts the dump to objects that are instances of comType
     * @param startTime Restricts the dump to objects created after the given time
     * @param endTime Restricts the dump to objects created before the given time. If this option is
     *        provided without the -s option, returns the single object that has the closest time
     *        stamp to, but not greater than endTime.
     * @param jsonFile target JSON file
     */
    public static void dumpFormattedArchive(String databaseFile, String providerURI, String domainId,
                                            String comType, String startTime, String endTime, String jsonFile) {
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
                LOGGER.log(Level.WARNING,
                           String.format("Error parsing comType \"%s\", filter will be ignored", comType));
            }
        }

        ObjectType objectsTypes = new ObjectType(new UShort(areaNumber), new UShort(serviceNumber),
                                                 new UOctet((short) areaVersion), new UShort(objectNumber));

        // prepare domain and time filters
        ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        IdentifierList domain = domainId == null ? null : HelperMisc.domainId2domain(domainId);
        FineTime startTimeF = startTime == null ? null : HelperTime.readableString2FineTime(startTime);
        FineTime endTimeF = endTime == null ? null : HelperTime.readableString2FineTime(endTime);
        ArchiveQuery archiveQuery =
                new ArchiveQuery(domain, null, null, 0L, null, startTimeF, endTimeF, null, null);
        archiveQueryList.add(archiveQuery);

        LocalOrRemoteConsumer consumers = createConsumer(providerURI, databaseFile);
        ArchiveConsumerServiceImpl localConsumer = consumers.getLocalConsumer();
        NMFConsumer remoteConsumer = consumers.getRemoteConsumer();

        // execute query
        ArchiveToJsonAdapter adapter = new ArchiveToJsonAdapter(jsonFile);
        queryArchive(objectsTypes, archiveQueryList, adapter, adapter, remoteConsumer == null ? localConsumer : remoteConsumer.getCOMServices().getArchiveService());

        closeConsumer(consumers);
    }

    /**
     * Lists the COM archive providers URIs found in the central directory.
     *
     * @param centralDirectoryServiceURI URI of the central directory to use
     */
    public static void listArchiveProviders(String centralDirectoryServiceURI) {
        ArrayList<String> archiveProviderURIs =
                CentralDirectoryHelper.listCOMArchiveProviders(new URI(centralDirectoryServiceURI));

        // No provider found warning
        if (archiveProviderURIs.size() <= 0) {
            LOGGER.log(Level.WARNING, String.format(
                    "No COM archive provider found in central directory at %s", centralDirectoryServiceURI));
            return;
        }

        // List providers found
        System.out.println("Found the following COM archive providers: ");
        for (String providerURI : archiveProviderURIs) {
            System.out.println(String.format(" - %s", providerURI));
        }
    }
}
