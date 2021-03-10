// (C) 2021 European Space Agency
// European Space Operations Centre
// Darmstadt, Germany

package esa.mo.nmf.log_browser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
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
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.softwaremanagement.SoftwareManagementHelper;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;
import org.ccsds.moims.mo.softwaremanagement.commandexecutor.CommandExecutorHelper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.nmf.NMFConsumer;
import esa.mo.nmf.log_browser.adapters.ArchiveToAppAdapter;
import esa.mo.nmf.log_browser.adapters.ArchiveToAppListAdapter;
import esa.mo.nmf.log_browser.adapters.ArchiveToJsonAdapter;
import esa.mo.nmf.log_browser.adapters.ArchiveToLogAdapter;
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
  public static void dumpRawArchiveTables(String databaseFile, String jsonFile) {
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

    // spawn our local provider on top of the given database file if needed
    ArchiveProviderServiceImpl localProvider = null;
    if (providerURI == null) {
      localProvider = spawnLocalArchiveProvider(databaseFile);
      providerURI =
          localProvider.getConnection().getConnectionDetails().getProviderURI().getValue();
    }

    // execute query
    ArchiveToJsonAdapter adapter = new ArchiveToJsonAdapter(jsonFile);
    queryArchive(providerURI, objectsTypes, archiveQueryList, adapter, adapter);

    // shutdown local provider if used
    if (localProvider != null) {
      localProvider.close();
    }
  }

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
    NMFConsumer.initHelpers();

    // Query all objects from SoftwareManagement area filtering for
    // StandardOutput and StandardError events and App object is done in the query adapter
    ObjectType objectsTypes =
        new ObjectType(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NUMBER, new UShort(0),
            SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_VERSION, new UShort(0));

    // spawn our local provider on top of the given database file if needed
    ArchiveProviderServiceImpl localProvider = null;
    if (providerURI == null) {
      localProvider = spawnLocalArchiveProvider(databaseFile);
      providerURI =
          localProvider.getConnection().getConnectionDetails().getProviderURI().getValue();
    }

    // prepare domain, time and object id filters
    IdentifierList domain = domainId == null ? null : HelperMisc.domainId2domain(domainId);
    ArchiveQueryList archiveQueryList = new ArchiveQueryList();
    FineTime startTimeF = startTime == null ? null : HelperTime.readableString2FineTime(startTime);
    FineTime endTimeF = endTime == null ? null : HelperTime.readableString2FineTime(endTime);
    ArchiveQuery archiveQuery =
        new ArchiveQuery(domain, null, null, new Long(0), null, startTimeF, endTimeF, null, null);
    archiveQueryList.add(archiveQuery);

    // execute query
    ArchiveToAppListAdapter adapter = new ArchiveToAppListAdapter();
    queryArchive(providerURI, objectsTypes, archiveQueryList, adapter, adapter);

    // shutdown local provider if used
    if (localProvider != null) {
      localProvider.close();
    }

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
      String domainId, String startTime, String endTime, String logFile) {
    NMFConsumer.initHelpers();

    // Query all objects from SoftwareManagement area and CommandExecutor service,
    // filtering for StandardOutput and StandardError events is done in the query adapter
    ObjectType objectsTypes =
        new ObjectType(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NUMBER,
            CommandExecutorHelper.COMMANDEXECUTOR_SERVICE_NUMBER,
            SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_VERSION, new UShort(0));

    // spawn our local provider on top of the given database file if needed
    ArchiveProviderServiceImpl localProvider = null;
    if (providerURI == null) {
      localProvider = spawnLocalArchiveProvider(databaseFile);
      providerURI =
          localProvider.getConnection().getConnectionDetails().getProviderURI().getValue();
    }

    // Query archive for the App object id
    IdentifierList domain = domainId == null ? null : HelperMisc.domainId2domain(domainId);
    ObjectId appObjectId = getAppObjectId(providerURI, appName, domain);

    if (appObjectId == null) {
      if (databaseFile == null) {
        LOGGER.log(Level.SEVERE, String.format("Couldn't find App with name %s in provider at %s",
            appName, providerURI));
      } else {
        LOGGER.log(Level.SEVERE, String.format("Couldn't find App with name %s in database at %s",
            appName, databaseFile));
      }
      return;
    }

    // prepare domain, time and object id filters
    ArchiveQueryList archiveQueryList = new ArchiveQueryList();
    FineTime startTimeF = startTime == null ? null : HelperTime.readableString2FineTime(startTime);
    FineTime endTimeF = endTime == null ? null : HelperTime.readableString2FineTime(endTime);
    ArchiveQuery archiveQuery = new ArchiveQuery(domain, null, null, new Long(0), appObjectId,
        startTimeF, endTimeF, null, null);
    archiveQueryList.add(archiveQuery);

    // execute query
    ArchiveToLogAdapter adapter = new ArchiveToLogAdapter(logFile);
    queryArchive(providerURI, objectsTypes, archiveQueryList, adapter, adapter);

    // shutdown local provider if used
    if (localProvider != null) {
      localProvider.close();
    }
  }

  /**
   * Search a COM archive provider content to find the ObjectId of an App of the
   * CommandExecutor service of the SoftwareManagement.
   *
   * @param centralDirectoryServiceURI URI of the central directory to use
   * @param appName Name of the NMF app we want the logs for
   * @param domain Restricts the search to objects in a specific domain ID
   * @return the ObjectId of the found App or null if not found
   */
  private static ObjectId getAppObjectId(String providerURI, String appName,
      IdentifierList domain) {
    // SoftwareManagement.AppsLaunch.App object type
    ObjectType appType = new ObjectType(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NUMBER,
        AppsLauncherHelper.APPSLAUNCHER_SERVICE_NUMBER, new UOctet((short) 0),
        AppsLauncherHelper.APP_OBJECT_NUMBER);

    // prepare domain filter
    ArchiveQueryList archiveQueryList = new ArchiveQueryList();
    ArchiveQuery archiveQuery =
        new ArchiveQuery(domain, null, null, new Long(0), null, null, null, null, null);
    archiveQueryList.add(archiveQuery);

    // execute query
    ArchiveToAppAdapter adapter = new ArchiveToAppAdapter(appName);
    queryArchive(providerURI, appType, archiveQueryList, adapter, adapter);
    return adapter.getAppObjectId();
  }

  /**
   * Queries objects from a COM archive provider.
   *
   * @param providerURI The URI of the remote COM archive provider
   * @param objectsTypes COM types of objects to query
   * @param archiveQueryList Archive query object used for filtering
   * @param adapter Archive adapter receiving the query answer messages
   * @param queryStatusProvider Interface providing the status of the query
   */
  private static void queryArchive(String providerURI, ObjectType objectsTypes,
      ArchiveQueryList archiveQueryList, ArchiveAdapter adapter,
      QueryStatusProvider queryStatusProvider) {
    // connect to the provider
    SingleConnectionDetails connectionDetails = new SingleConnectionDetails();
    connectionDetails.setProviderURI(providerURI);
    IdentifierList domain = new IdentifierList();
    Identifier wildCard = new Identifier("*");
    domain.add(wildCard);
    connectionDetails.setDomain(domain);
    ArchiveConsumerServiceImpl archiveConsumer = null;
    try {
      archiveConsumer = new ArchiveConsumerServiceImpl(connectionDetails);
    } catch (MalformedURLException | MALException e) {
      LOGGER.log(Level.SEVERE,
          String.format("Error when connectiong to archive provider at %s", providerURI), e);
      return;
    }

    // run the query
    try {
      archiveConsumer.getArchiveStub().query(true, objectsTypes, archiveQueryList, null, adapter);
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
    archiveConsumer.closeConnection();
  }

  /**
   * Instantiates a COM archive service provider using the given COM archive SQLite file.
   *
   * @param databaseFile Local SQLite database file
   * @return the ArchiveProviderServiceImpl
   */
  private static ArchiveProviderServiceImpl spawnLocalArchiveProvider(String databaseFile) {
    HelperMisc.loadPropertiesFile();
    System.setProperty(HelperMisc.PROP_MO_APP_NAME, LogBrowser.APP_NAME);
    System.setProperty("esa.nmf.archive.persistence.jdbc.url", "jdbc:sqlite:" + databaseFile);

    ArchiveProviderServiceImpl archiveProvider = new ArchiveProviderServiceImpl();
    try {
      archiveProvider.init(null);
      LOGGER.log(Level.INFO, String.format("ArchiveProvider initialized at %s with file %s",
          archiveProvider.getConnection().getConnectionDetails().getProviderURI(), databaseFile));
    } catch (MALException e) {
      LOGGER.log(Level.SEVERE, "Error initializing archiveProdiver", e);
    }

    // give it time to initialize
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
    }

    return archiveProvider;
  }

  /**
   * Lists the COM archive providers URIs found in the central directory.
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
