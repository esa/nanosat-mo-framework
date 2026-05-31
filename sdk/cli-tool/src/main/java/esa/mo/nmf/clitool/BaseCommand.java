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

import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.nmf.NMFConsumer;
import esa.mo.nmf.clitool.adapters.ArchiveToAppAdapter;
import esa.mo.nmf.clitool.adapters.QueryStatusProvider;
import esa.mo.nmf.clitool.mc.AggregationCommands;
import esa.mo.nmf.clitool.mc.ParameterCommands;
import esa.mo.nmf.clitool.sm.SoftwareManagementCommands;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveStub;
import org.ccsds.moims.mo.com.login.body.LoginResponse;
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperMisc;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.sm.SMHelper;
import org.ccsds.moims.mo.sm.appslauncher.AppsLauncherServiceInfo;

/**
 * @author marcel.mikolajko
 */
public abstract class BaseCommand {

    private static final Logger LOGGER = Logger.getLogger(BaseCommand.class.getName());

    public String providerURI;
    public String databaseFile;
    public String providerName;

    public static GroundMOAdapterImpl consumer;
    public static IdentifierList domain;

    public static ArchiveConsumerServiceImpl localArchive;
    public static ArchiveProviderServiceImpl localArchiveProvider;

    /**
     * Consumes the shared base options (-r/--remote, -l/--local, -p/--provider)
     * from the supplied Args instance, populating the corresponding fields.
     */
    protected void parseBaseOptions(Args args) {
        providerURI   = args.option("-r", "--remote");
        databaseFile  = args.option("-l", "--local");
        providerName  = args.option("-p", "--provider");
    }

    public abstract void run(Args args);

    public boolean initLocalArchiveProvider(String databaseFile) {
        HelperMisc.loadPropertiesFile();
        System.setProperty(HelperMisc.PROP_MO_APP_NAME, CLITool.APP_NAME);
        System.setProperty("esa.nmf.archive.persistence.jdbc.url", "jdbc:sqlite:" + databaseFile);

        localArchiveProvider = new ArchiveProviderServiceImpl();
        try {
            localArchiveProvider.init(null);
            LOGGER.log(Level.INFO, String.format("ArchiveProvider initialized at %s with file %s",
                    localArchiveProvider.getConnection().getConnectionDetails().getProviderURI(), databaseFile));
        } catch (MALException e) {
            LOGGER.log(Level.SEVERE, "Error initializing archiveProvider", e);
            return false;
        }

        // give it time to initialize
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }
        return true;
    }

    public boolean initLocalConsumer(String databaseFile) {
        if (!initLocalArchiveProvider(databaseFile)) {
            return false;
        }
        NMFConsumer.initHelpers();
        URI providerURI = localArchiveProvider.getConnection().getConnectionDetails().getProviderURI();
        IdentifierList domain = new IdentifierList();
        Identifier wildCard = new Identifier("*");
        domain.add(wildCard);

        SingleConnectionDetails connectionDetails = new SingleConnectionDetails(
                providerURI,
                null,
                domain);

        try {
            localArchive = new ArchiveConsumerServiceImpl(connectionDetails);
        } catch (MALException | MalformedURLException e) {
            LOGGER.log(Level.SEVERE, "Error initializing local archive", e);
            return false;
        }
        return true;
    }

    public boolean initRemoteConsumer() {
        try {
            HelperMisc.loadPropertiesFile();
            providerURI = providerURI.contains("Archive") ? providerURI.replace("Archive", "Directory") : providerURI;
            ProviderList providerList = NMFConsumer.retrieveProvidersFromDirectory(new URI(providerURI));
            Provider provider = null;
            if (providerList.size() == 1) {
                if (providerName != null) {
                    System.out.println("\nThere's only one provider in directory. Ignoring --provider option.\n");
                }
                provider = providerList.get(0);
            } else {
                if (providerName == null) {
                    System.out.println("\nThere's more than one provider in directory.");
                    System.out.println("--provider option is required\n");
                    System.out.println("Available providers at this uri: " + providerURI);
                    for (Provider p : providerList) {
                        System.out.println(" - " + p.getProviderName());
                    }
                    System.out.println();
                    return false;
                }

                for (Provider p : providerList) {
                    if (p.getProviderName().getValue().equals(providerName)) {
                        provider = p;
                        break;
                    }
                }
            }

            if (provider == null) {
                System.out.println("\nProvider not found!");
                if (!providerList.isEmpty()) {
                    System.out.println("Available providers at this uri: " + providerURI);
                    for (Provider p : providerList) {
                        System.out.println(" - " + p.getProviderName());
                    }
                } else {
                    System.out.println("No providers available at this uri: " + providerURI);
                }
                System.out.println();
                return false;
            }

            consumer = new GroundMOAdapterImpl(provider);
            consumer.init();
            domain = provider.getDomain();

            if (consumer.getCOMServices().getLoginService() != null
                    && consumer.getCOMServices().getLoginService().getLoginStub() != null) {
                System.out.println("\nLogin required for " + provider.getProviderName());

                String login = System.console().readLine("Login: ");
                char[] password = System.console().readPassword("Password: ");
                System.out.println();

                LoginResponse response = consumer.getCOMServices().getLoginService().getLoginStub().login(
                        new Identifier(login), String.valueOf(password));
                consumer.setAuthenticationId(response.getAuthId());
                System.out.println("Login successful!");
            }
        } catch (MALException | MalformedURLException | MALInteractionException e) {
            LOGGER.log(Level.SEVERE, "Error when creating consumer", e);
            closeConsumer();
            return false;
        }
        System.out.println("\n");
        return true;
    }

    public static void closeConsumer() {
        if (consumer != null) {
            IdentifierList ids = new IdentifierList();
            try {
                if (ParameterCommands.parameterSubscription != null) {
                    ids.clear();
                    ids.add(ParameterCommands.parameterSubscription);
                    consumer.getMCServices().getParameterService().getParameterStub().monitorValueDeregister(ids);
                }

                if (AggregationCommands.aggregationSubscription != null) {
                    ids.clear();
                    ids.add(AggregationCommands.aggregationSubscription);
                    consumer.getMCServices().getAggregationService().getAggregationStub().monitorValueDeregister(ids);
                }

                if (SoftwareManagementCommands.heartbeatSubscription != null) {
                    ids.clear();
                    ids.add(SoftwareManagementCommands.heartbeatSubscription);
                    consumer.getSMServices().getHeartbeatService().getHeartbeatStub().beatDeregister(ids);
                }

                if (SoftwareManagementCommands.outputSubscription != null) {
                    ids.clear();
                    ids.add(SoftwareManagementCommands.outputSubscription);
                    consumer.getSMServices().getAppsLauncherService().getAppsLauncherStub().monitorExecutionDeregister(ids);
                }
            } catch (MALInteractionException | MALException e) {
                LOGGER.log(Level.SEVERE, "Failed to deregister subscription: " + ids.get(0), e);
            }

            consumer.getCOMServices().closeConnections();
            consumer.getMCServices().closeConnections();
            consumer.getPlatformServices().closeConnections();
            consumer.getSMServices().closeConnections();
            consumer = null;
        }

        if (localArchive != null) {
            localArchive.closeConnection();
            localArchive = null;
        }

        if (localArchiveProvider != null) {
            localArchiveProvider.close();
            localArchiveProvider = null;
        }

        LOGGER.log(Level.INFO, "CLI-Tool successfully disconnected!");
    }

    /**
     * Queries objects from a COM archive provider.
     *
     * @param objectsTypes COM types of objects to query
     * @param archiveQuery Archive query object used for filtering
     * @param adapter Archive adapter receiving the query answer messages
     * @param queryStatusProvider Interface providing the status of the query
     */
    public static void queryArchive(ObjectType objectsTypes, ArchiveQuery archiveQuery,
            ArchiveAdapter adapter, QueryStatusProvider queryStatusProvider) {
        // run the query
        try {
            ArchiveStub archive = localArchive == null
                    ? consumer.getCOMServices().getArchiveService().getArchiveStub() : localArchive.getArchiveStub();
            archive.query(true, objectsTypes, archiveQuery, null, adapter);
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

    /**
     * Search a COM archive provider content to find the ObjectKey of an App of
     * the CommandExecutor service of the SoftwareManagement.
     *
     * @param appName Name of the NMF app we want the logs for
     * @param domain Restricts the search to objects in a specific domain ID
     * @return the ObjectKey of the found App or null if not found
     */
    public static ObjectKey getAppObjectKey(String appName, IdentifierList domain) {
        // SoftwareManagement.AppsLaunch.App object type
        ObjectType appType = new ObjectType(SMHelper.SM_AREA_NUMBER,
                AppsLauncherServiceInfo.APPSLAUNCHER_SERVICE_NUMBER,
                new UOctet((short) 0),
                AppsLauncherServiceInfo.APPDETAILS_OBJECT_NUMBER);

        // prepare domain filter
        ArchiveQuery archiveQuery = new ArchiveQuery(domain, null, 0L, null, null, null, null, null);

        // execute query
        ArchiveToAppAdapter adapter = new ArchiveToAppAdapter(appName);
        queryArchive(appType, archiveQuery, adapter, adapter);
        return adapter.getAppObjectKey();
    }
}
