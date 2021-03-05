// (C) 2021 European Space Agency
// European Space Operations Centre
// Darmstadt, Germany

package esa.mo.nmf.log_browser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.archive.structures.QueryFilterList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import esa.mo.nmf.NMFConsumer;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;

/**
 * Actual implementation of the available commands.
 *
 * @author Tanguy Soto
 */
public class CommandsImplementations {

  private static final Logger LOGGER = Logger.getLogger(CommandsImplementations.class.getName());

  /**
   * Dumps to a JSON file the raw SQLite tables content of a COM archive.
   * 
   * @param databaseFile source SQLite database file
   * @param jsonFile target JSON file
   */
  public void dumpRawArchiveTables(String databaseFile, String jsonFile) {
    // Test if DB file exists
    File temp = new File(databaseFile);
    if (!temp.exists() || temp.isDirectory()) {
      LOGGER.log(Level.SEVERE,
          String.format("Provided database file %s doesn't exist or is a directory", databaseFile));
      return;
    }

    // root JSON object
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

        // table JSON object
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
   * Dumps to a JSON file the formatted content of a COM archive provider.
   *
   * @param centralDirectoryServiceURI URI of the central directory to use
   * @param providerName The name of the provider
   * @param jsonFile target JSON file
   */
  public void dumpFormattedArchive(String centralDirectoryServiceURI, String providerName,
      String jsonFile) {
    NMFConsumer.initHelpers();

    ProviderSummary providerDetails = CentralDirectoryHelper
        .getProviderSummary(new URI(centralDirectoryServiceURI), providerName);
    GroundMOAdapterImpl gma = new GroundMOAdapterImpl(providerDetails);

    // prepare an entire archive query
    boolean returnBody = true;
    ObjectType allObjectsType =
        new ObjectType(new UShort(0), new UShort(0), new UOctet((short) 0), new UShort(0));
    ArchiveQueryList archiveQueryList = new ArchiveQueryList();
    archiveQueryList
        .add(new ArchiveQuery(null, null, null, new Long(0), null, null, null, null, null));
    QueryFilterList queryFilterList = null;

    // execute the query
    ToJsonArchiveAdapter toJsonArchiveAdapter = null;
    try {
      toJsonArchiveAdapter = new ToJsonArchiveAdapter(jsonFile);
      gma.getCOMServices().getArchiveService().getArchiveStub().query(returnBody, allObjectsType,
          archiveQueryList, queryFilterList, toJsonArchiveAdapter);
    } catch (MALInteractionException | MALException e) {
      LOGGER.log(Level.SEVERE, "Error when querying archive", e);
    }

    // wait for query to end
    while (toJsonArchiveAdapter != null && !toJsonArchiveAdapter.isQueryOver()) {
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
      }
    }

    gma.closeConnections();
  }

  /**
   * Lists the COM archive providers names found in the central directory.
   *
   * @param centralDirectoryServiceURI URI of the central directory to use
   */
  public void listArchiveProviders(String centralDirectoryServiceURI) {
    ArrayList<String> archiveProvidersName =
        CentralDirectoryHelper.listCOMArchiveProviders(new URI(centralDirectoryServiceURI));

    // No provider found warning
    if (archiveProvidersName.size() <= 0) {
      LOGGER.log(Level.WARNING, String.format(
          "No COM archive provider found in central directory at %s", centralDirectoryServiceURI));
      return;
    }

    // List providers found
    System.out.println("Found the following COM archive providers: ");
    for (String providerName : archiveProvidersName) {
      System.out.println(String.format("\t- %s", providerName));
    }
  }
}
