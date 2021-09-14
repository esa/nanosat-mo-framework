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

package esa.mo.nmf.com_archive_browser;

import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.nmf.NMFConsumer;
import esa.mo.nmf.com_archive_browser.adapters.ArchiveToAppAdapter;
import esa.mo.nmf.com_archive_browser.adapters.ArchiveToRolesAdapter;
import esa.mo.nmf.com_archive_browser.adapters.QueryStatusProvider;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.common.directory.structures.ServiceCapability;
import org.ccsds.moims.mo.common.login.LoginHelper;
import org.ccsds.moims.mo.common.login.body.LoginResponse;
import org.ccsds.moims.mo.common.login.structures.Profile;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.softwaremanagement.SoftwareManagementHelper;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;
import picocli.CommandLine.Option;

import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper methods for creating the consumer and querying the archive.
 *
 * @author Tanguy Soto
 */
public class ArchiveBrowserHelper {

  private static final Logger LOGGER = Logger.getLogger(ArchiveBrowserHelper.class.getName());

  public static class LocalOrRemote {
    @Option(names = {"-l", "--local"}, paramLabel = "<databaseFile>",
            description = "Local SQLite database file\n"
                          + "  - example: ../nanosat-mo-supervisor-sim/comArchive.db")
    public String databaseFile;

    @Option(names = {"-r", "--remote"}, paramLabel = "<providerURI>",
            description = "Remote COM archive provider URI\n"
                          + "  - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Archive")
    public String providerURI;
  }

  /**
   * Search a COM archive provider content to find the ObjectId of an App of the CommandExecutor
   * service of the SoftwareManagement.
   *
   * @param appName Name of the NMF app we want the logs for
   * @param domain Restricts the search to objects in a specific domain ID
   * @return the ObjectId of the found App or null if not found
   */
  public static ObjectId getAppObjectId(String appName,
      IdentifierList domain, NMFConsumer consumer) {
    // SoftwareManagement.AppsLaunch.App object type
    ObjectType appType = new ObjectType(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NUMBER,
        AppsLauncherHelper.APPSLAUNCHER_SERVICE_NUMBER, new UOctet((short) 0),
        AppsLauncherHelper.APP_OBJECT_NUMBER);

    // prepare domain filter
    ArchiveQueryList archiveQueryList = new ArchiveQueryList();
    ArchiveQuery archiveQuery =
        new ArchiveQuery(domain, null, null, 0L, null, null, null, null, null);
    archiveQueryList.add(archiveQuery);

    // execute query
    ArchiveToAppAdapter adapter = new ArchiveToAppAdapter(appName);
    queryArchive(appType, archiveQueryList, adapter, adapter, consumer);
    return adapter.getAppObjectId();
  }

  /**
   * Queries objects from a COM archive provider.
   *
   * @param objectsTypes COM types of objects to query
   * @param archiveQueryList Archive query object used for filtering
   * @param adapter Archive adapter receiving the query answer messages
   * @param queryStatusProvider Interface providing the status of the query
   */
  public static void queryArchive(ObjectType objectsTypes,
    ArchiveQueryList archiveQueryList, ArchiveAdapter adapter,
    QueryStatusProvider queryStatusProvider, NMFConsumer consumer) {
    // run the query
    try {
      consumer.getCOMServices().getArchiveService().getArchiveStub().query(true, objectsTypes, archiveQueryList, null, adapter);
    } catch (MALInteractionException | MALException e) {
      LOGGER.log(Level.SEVERE, "Error when querying archive", e);
      return;
    }

    // wait for query to end
    while (!queryStatusProvider.isQueryOver()) {
      try {
        Thread.sleep(500);
      } catch (InterruptedException ignored) {
      }
    }
  }

  public static NMFConsumer createConsumer(String providerURI) {
    NMFConsumer consumer = null;
    try {
      NMFConsumer.initHelpers();
      String tempURI = providerURI.contains("Archive") ?
                    providerURI.replace("Archive", "Directory") : providerURI;
      ProviderSummaryList providerSummaryList = NMFConsumer.retrieveProvidersFromDirectory(new URI(tempURI));
      ProviderSummary provider = null;
      for(ProviderSummary summary : providerSummaryList) {
          for(ServiceCapability capability : summary.getProviderDetails().getServiceCapabilities()) {

            for(int i = 0; i < capability.getServiceAddresses().size(); ++i) {
              if(capability.getServiceAddresses().get(i).getServiceURI().toString().startsWith("malspp")) {
                capability.getServiceAddresses().remove(i);
                --i;
              }
            }

            if(capability.getServiceAddresses().stream().anyMatch(address -> {
              if(tempURI.contains("localhost")) {
                return address.getServiceURI().equals(new URI(tempURI)) ||
                       address.getServiceURI().equals(new URI(tempURI.replace("localhost", "127.0.0.1")));
              } else if(tempURI.contains("127.0.0.1")) {
                return address.getServiceURI().equals(new URI(tempURI)) ||
                       address.getServiceURI().equals(new URI(tempURI.replace("127.0.0.1", "localhost")));
              } else {
                return address.getServiceURI().equals(new URI(tempURI));
              }
            })) {
              provider = summary;
              break;
            }
          }
          if(provider != null) {
            break;
          }
      }

      if(provider == null) {
        LOGGER.log(Level.SEVERE, "Provider not found!");
        return null;
      }

      consumer = new NMFConsumer(provider);
      consumer.init();

      if(consumer.getCommonServices().getLoginService() != null &&
         consumer.getCommonServices().getLoginService().getLoginStub() != null) {
        System.out.println("\nLogin required for " + provider.getProviderName());

        String login = System.console().readLine("Login: ");
        char[] password = System.console().readPassword("Password: ");

        LongList rolesIds = consumer.getCommonServices().getLoginService().getLoginStub().listRoles(new Identifier(login), String.valueOf(password));

        ArchiveToRolesAdapter adapter = new ArchiveToRolesAdapter();
        consumer.getCOMServices()
                .getArchiveService()
                .getArchiveStub()
                .retrieve(LoginHelper.LOGINROLE_OBJECT_TYPE,
                          consumer.getCommonServices().getLoginService().getConnectionDetails().getDomain(),
                          rolesIds,
                          adapter);

        while (!adapter.isQueryOver()) {
          try {
            Thread.sleep(500);
          } catch (InterruptedException ignored) {
          }
        }

        Long roleId = null;
        if(!adapter.getRolesIds().isEmpty()) {
          System.out.println("Available roles: ");
          for(int i = 0; i < adapter.getRolesIds().size(); ++i) {
            System.out.println((i + 1) + " - " + adapter.getRolesNames().get(i));
          }
          int index = Integer.parseInt(System.console().readLine("Select role id: ")) - 1;
          if(index >= 0 && index < adapter.getRolesIds().size()) {
            roleId = adapter.getRolesIds().get(index);
          }
        }

        LoginResponse response = consumer.getCommonServices().getLoginService().getLoginStub()
                                         .login(new Profile(new Identifier(login), roleId), String.valueOf(password));
        consumer.setAuthenticationId(response.getBodyElement0());
        System.out.println("Login successful!");
      }
      return consumer;
    } catch (MALException | MalformedURLException | MALInteractionException e) {
      LOGGER.log(Level.SEVERE, "Error when creating consumer", e);
      closeConsumer(consumer);
      return null;
    }
  }

  public static void closeConsumer(NMFConsumer consumer) {
    if(consumer != null) {
      consumer.getCommonServices().closeConnections();
      consumer.getCOMServices().closeConnections();
      consumer.getMCServices().closeConnections();
      consumer.getPlatformServices().closeConnections();
      consumer.getSMServices().closeConnections();
    }
  }

  /**
   * Instantiates a COM archive service provider using the given COM archive SQLite file.
   *
   * @param databaseFile Local SQLite database file
   * @return the ArchiveProviderServiceImpl
   */
  public static ArchiveProviderServiceImpl spawnLocalArchiveProvider(String databaseFile) {
    HelperMisc.loadPropertiesFile();
    System.setProperty(HelperMisc.PROP_MO_APP_NAME, COMArchiveBrowser.APP_NAME);
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
    } catch (InterruptedException ignored) {
    }

    return archiveProvider;
  }
}
