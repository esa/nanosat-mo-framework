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
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.nmf.NMFConsumer;
import esa.mo.nmf.comarchivetool.CentralDirectoryHelper;
import esa.mo.nmf.comarchivetool.adapters.ArchiveToJsonAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
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
     * Dumps to a JSON file the raw SQLite tables content of a COM archive.
     *
     * @param databaseFile source SQLite database file
     * @param jsonFile target JSON file
     */
    public static void dumpRawArchiveTables(final String databaseFile, final String jsonFile) {
        // Test if DB file exists
        final File temp = new File(databaseFile);
        if (!temp.exists() || temp.isDirectory()) {
            LOGGER.log(Level.SEVERE,
                       String.format("Provided database file %s doesn't exist or is a directory", databaseFile));
            return;
        }

        // root JSON object
        final JSONArray tables = new JSONArray();

        // parse DB
        try (final Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFile)) {
            // for each table
            final ResultSet tablesNamesRs = conn.getMetaData().getTables(null, null, null, null);
            while (tablesNamesRs.next()) {
                // query table
                final String table = tablesNamesRs.getString("TABLE_NAME");
                final String selectAllQuery = "SELECT  * FROM " + table;
                final ResultSet rowsRs = conn.createStatement().executeQuery(selectAllQuery);
                final ResultSetMetaData rowsRsMeta = rowsRs.getMetaData();

                // table JSON object
                final JSONArray rows = new JSONArray();
                final JSONObject jsonTable = new JSONObject();
                jsonTable.put(table, rows);

                // for each row
                while (rowsRs.next()) {
                    final JSONObject rowObject = new JSONObject();

                    // for each column
                    for (int i = 0; i < rowsRsMeta.getColumnCount(); i++) {
                        final String columnName = rowsRsMeta.getColumnName(i + 1);
                        final String columnValue = rowsRs.getString(i + 1) == null ? "null" : rowsRs.getString(i + 1);
                        rowObject.put(columnName, columnValue);
                    }
                    rows.add(rowObject);
                }
                tables.add(jsonTable);
            }
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, String.format("SQL error reading %s", databaseFile), e);
        }

        // write JSON file
        try (final FileWriter file = new FileWriter(jsonFile)) {
            final Gson gson = new GsonBuilder().setPrettyPrinting().create();
            final String prettyJsonString = gson.toJson(tables);
            file.write(prettyJsonString);
            file.flush();
        } catch (final IOException e) {
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
    public static void dumpFormattedArchive(final String databaseFile, final String providerURI, final String domainId,
                                            final String comType, final String startTime, final String endTime, final String jsonFile) {
        // prepare comType filter
        int areaNumber = 0;
        int serviceNumber = 0;
        int areaVersion = 0;
        int objectNumber = 0;

        if (comType != null) {
            final String[] subTypes = comType.split("\\.");
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

        final ObjectType objectsTypes = new ObjectType(new UShort(areaNumber), new UShort(serviceNumber),
                                                 new UOctet((short) areaVersion), new UShort(objectNumber));

        // prepare domain and time filters
        final ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        final IdentifierList domain = domainId == null ? null : HelperMisc.domainId2domain(domainId);
        final FineTime startTimeF = startTime == null ? null : HelperTime.readableString2FineTime(startTime);
        final FineTime endTimeF = endTime == null ? null : HelperTime.readableString2FineTime(endTime);
        final ArchiveQuery archiveQuery =
                new ArchiveQuery(domain, null, null, 0L, null, startTimeF, endTimeF, null, null);
        archiveQueryList.add(archiveQuery);

        final LocalOrRemoteConsumer consumers = createConsumer(providerURI, databaseFile);
        final ArchiveConsumerServiceImpl localConsumer = consumers.getLocalConsumer();
        final NMFConsumer remoteConsumer = consumers.getRemoteConsumer();

        // execute query
        final ArchiveToJsonAdapter adapter = new ArchiveToJsonAdapter(jsonFile);
        queryArchive(objectsTypes, archiveQueryList, adapter, adapter, remoteConsumer == null ? localConsumer : remoteConsumer.getCOMServices().getArchiveService());

        closeConsumer(consumers);
    }

    /**
     * Lists the COM archive providers URIs found in the central directory.
     *
     * @param centralDirectoryServiceURI URI of the central directory to use
     */
    public static void listArchiveProviders(final String centralDirectoryServiceURI) {
        final ArrayList<String> archiveProviderURIs =
                CentralDirectoryHelper.listCOMArchiveProviders(new URI(centralDirectoryServiceURI));

        // No provider found warning
        if (archiveProviderURIs.size() <= 0) {
            LOGGER.log(Level.WARNING, String.format(
                    "No COM archive provider found in central directory at %s", centralDirectoryServiceURI));
            return;
        }

        // List providers found
        System.out.println("Found the following COM archive providers: ");
        for (final String providerURI : archiveProviderURIs) {
            System.out.println(String.format(" - %s", providerURI));
        }
    }
}
