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
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.nmf.NMFConsumer;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import esa.mo.nmf.log_browser.adapters.ArchiveToJsonAdapter;
import esa.mo.nmf.log_browser.adapters.QueryStatusProvider;

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
   * @param domainId Restricts the dump to objects in a specific domain ID
   * @param comType Restricts the dump to objects that are instances of comType
   * @param startTime Restricts the dump to objects created after the given time
   * @param endTime Restricts the dump to objects created before the given time. If this option is
   *        provided without the -s option, returns the single object that has the closest time
   *        stamp to, but not greater than endTime.
   * @param jsonFile target JSON file
   */
  public void dumpFormattedArchive(String centralDirectoryServiceURI, String providerName,
      String domainId, String comType, String startTime, String endTime, String jsonFile) {
    NMFConsumer.initHelpers();

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
        new ArchiveQuery(domain, null, null, new Long(0), null, startTimeF, endTimeF, null, null);
    archiveQueryList.add(archiveQuery);

    // execute query
    ArchiveToJsonAdapter adapter = new ArchiveToJsonAdapter(jsonFile);
    queryArchive(centralDirectoryServiceURI, providerName, objectsTypes, archiveQueryList, adapter,
        adapter);
  }

  /**
   * Dumps to a LOG file the logs of an NMF app using the content of a COM archive provider.
   * 
   * @param centralDirectoryServiceURI URI of the central directory to use
   * @param appName Name of the NMF app we want the logs for
   * @param providerName The name of the provider
   * @param startTime Restricts the dump to objects created after the given time
   * @param endTime Restricts the dump to objects created before the given time. If this option is
   *        provided without the -s option, returns the single object that has the closest time
   *        stamp to, but not greater than endTime.
   * @param logFile target LOG file
   */
  public void getLogs(String centralDirectoryServiceURI, String appName, String providerName,
      String startTime, String endTime, String logFile) {
    System.out.println("COMING SOON");
  }

  /**
   * TODO queryArchive
   *
   * @param centralDirectoryServiceURI
   * @param providerName
   * @param objectsTypes
   * @param archiveQueryList
   * @param adapter
   * @param queryStatusProvider
   */
  private void queryArchive(String centralDirectoryServiceURI, String providerName,
      ObjectType objectsTypes, ArchiveQueryList archiveQueryList, ArchiveAdapter adapter,
      QueryStatusProvider queryStatusProvider) {
    // connect to the provider
    ProviderSummary providerDetails = CentralDirectoryHelper
        .getProviderSummary(new URI(centralDirectoryServiceURI), providerName);
    GroundMOAdapterImpl gma = new GroundMOAdapterImpl(providerDetails);

    // run the query
    try {
      gma.getCOMServices().getArchiveService().getArchiveStub().query(true, objectsTypes,
          archiveQueryList, null, adapter);
    } catch (MALInteractionException | MALException e) {
      LOGGER.log(Level.SEVERE, "Error when querying archive", e);
    }

    // wait for query to end
    while (!queryStatusProvider.isQueryOver()) {
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
