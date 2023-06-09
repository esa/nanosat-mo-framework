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

import esa.mo.nmf.clitool.sm.SoftwareManagementCommands;
import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.nmf.NMFConsumer;
import esa.mo.nmf.clitool.adapters.ArchiveToAppAdapter;
import esa.mo.nmf.clitool.adapters.QueryStatusProvider;
import esa.mo.nmf.clitool.mc.AggregationCommands;
import esa.mo.nmf.clitool.mc.ParameterCommands;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveStub;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.common.login.LoginHelper;
import org.ccsds.moims.mo.common.login.body.LoginResponse;
import org.ccsds.moims.mo.common.login.structures.Profile;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.softwaremanagement.SoftwareManagementHelper;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;
import picocli.CommandLine.Option;

/**
 * @author marcel.mikolajko
 */
public abstract class BaseCommand {

    private static final Logger LOGGER = Logger.getLogger(BaseCommand.class.getName());

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
    private boolean helpRequested;

    @Option(names = {"-r", "--remote"}, paramLabel = "<providerURI>", description = "Provider URI\n"
            + "  - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Directory")
    public String providerURI;

    @Option(names = {"-l", "--local"}, paramLabel = "<databaseFile>", description = "Local SQLite database file\n"
            + "  - example: ../nanosat-mo-supervisor-sim/comArchive.db")
    public String databaseFile;

    @Option(names = {"-p", "--provider"}, paramLabel = "<providerName>",
            description = "Name of the provider we want to connect to")
    public String providerName;

    public static GroundMOAdapterImpl consumer;
    public static IdentifierList domain;

    public static ArchiveConsumerServiceImpl localArchive;
    public static ArchiveProviderServiceImpl localArchiveProvider;

    public boolean initLocalArchiveProvider(String databaseFile) {
        HelperMisc.loadPropertiesFile();
        System.setProperty(HelperMisc.PROP_MO_APP_NAME, CLITool.APP_NAME);
        System.setProperty("esa.nmf.archive.persistence.jdbc.url", "jdbc:sqlite:" + databaseFile);

        localArchiveProvider = new ArchiveProviderServiceImpl();
        try {
            localArchiveProvider.init(null);
            LOGGER.log(Level.INFO, String.format("ArchiveProvider initialized at %s with file %s", localArchiveProvider
                    .getConnection().getConnectionDetails().getProviderURI(), databaseFile));
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
        String providerURI = localArchiveProvider.getConnection().getConnectionDetails().getProviderURI().getValue();
        SingleConnectionDetails connectionDetails = new SingleConnectionDetails();
        connectionDetails.setProviderURI(providerURI);
        IdentifierList domain = new IdentifierList();
        Identifier wildCard = new Identifier("*");
        domain.add(wildCard);
        connectionDetails.setDomain(domain);

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
            ProviderSummaryList providerSummaryList = NMFConsumer.retrieveProvidersFromDirectory(new URI(providerURI));
            ProviderSummary provider = null;
            if (providerSummaryList.size() == 1) {
                if (providerName != null) {
                    System.out.println("\nThere's only one provider in directory. Ignoring --provider option.\n");
                }
                provider = providerSummaryList.get(0);
            } else {
                if (providerName == null) {
                    System.out.println("\nThere's more than one provider in directory.");
                    System.out.println("--provider option is required\n");
                    System.out.println("Available providers at this uri: " + providerURI);
                    for (ProviderSummary summary : providerSummaryList) {
                        System.out.println(" - " + summary.getProviderId());
                    }
                    System.out.println();
                    return false;
                }

                for (ProviderSummary summary : providerSummaryList) {
                    if (summary.getProviderId().getValue().equals(providerName)) {
                        provider = summary;
                        break;
                    }
                }
            }

            if (provider == null) {
                System.out.println("\nProvider not found!");
                if (!providerSummaryList.isEmpty()) {
                    System.out.println("Available providers at this uri: " + providerURI);
                    for (ProviderSummary summary : providerSummaryList) {
                        System.out.println(" - " + summary.getProviderId());
                    }
                } else {
                    System.out.println("No providers available at this uri: " + providerURI);
                }
                System.out.println();
                return false;
            }

            consumer = new GroundMOAdapterImpl(provider);
            consumer.init();
            domain = provider.getProviderKey().getDomain();

            if (consumer.getCommonServices().getLoginService() != null
                    && consumer.getCommonServices().getLoginService().getLoginStub() != null) {
                System.out.println("\nLogin required for " + provider.getProviderId());

                String login = System.console().readLine("Login: ");
                char[] password = System.console().readPassword("Password: ");
                System.out.println();

                LongList ids = consumer.getCommonServices().getLoginService().getLoginStub().listRoles(new Identifier(
                        login), String.valueOf(password));

                List<Long> roleIds = new ArrayList<>();
                List<String> roleNames = new ArrayList<>();
                final Object lock = new Object();
                ArchiveAdapter adapter = new ArchiveAdapter() {
                    @Override
                    public void retrieveResponseReceived(MALMessageHeader msgHeader, ArchiveDetailsList objDetails,
                            ElementList objBodies, Map qosProperties) {
                        for (int i = 0; i < objDetails.size(); ++i) {
                            roleIds.add(objDetails.get(i).getInstId());
                            roleNames.add(objBodies.get(i).toString());
                        }
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }
                };

                consumer.getCOMServices().getArchiveService().getArchiveStub().retrieve(
                        LoginHelper.LOGINROLE_OBJECT_TYPE, consumer.getCommonServices().getLoginService()
                                .getConnectionDetails().getDomain(), ids, adapter);

                synchronized (lock) {
                    lock.wait(10000);
                }

                Long roleId = null;
                if (!roleIds.isEmpty()) {
                    System.out.println("\nAvailable roles: ");
                    for (int i = 0; i < roleIds.size(); ++i) {
                        System.out.println((i + 1) + " - " + roleNames.get(i));
                    }
                    int index = Integer.parseInt(System.console().readLine("Select role id: ")) - 1;
                    if (index >= 0 && index < roleIds.size()) {
                        roleId = roleIds.get(index);
                    }
                }

                LoginResponse response = consumer.getCommonServices().getLoginService().getLoginStub().login(
                        new Profile(new Identifier(login), roleId), String.valueOf(password));
                consumer.setAuthenticationId(response.getBodyElement0());
                System.out.println("Login successful!");
            }
        } catch (MALException | MalformedURLException | MALInteractionException | InterruptedException e) {
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
                    consumer.getSMServices().getAppsLauncherService().getAppsLauncherStub().monitorExecutionDeregister(
                            ids);
                }
            } catch (MALInteractionException | MALException e) {
                LOGGER.log(Level.SEVERE, "Failed to deregister subscription: " + ids.get(0), e);
            }

            consumer.getCommonServices().closeConnections();
            consumer.getCOMServices().closeConnections();
            consumer.getMCServices().closeConnections();
            consumer.getPlatformServices().closeConnections();
            consumer.getSMServices().closeConnections();
            consumer = null;
        }

        if (localArchive != null) {
            localArchive.close();
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
     * @param archiveQueryList Archive query object used for filtering
     * @param adapter Archive adapter receiving the query answer messages
     * @param queryStatusProvider Interface providing the status of the query
     */
    public static void queryArchive(ObjectType objectsTypes, ArchiveQueryList archiveQueryList, ArchiveAdapter adapter,
            QueryStatusProvider queryStatusProvider) {
        // run the query
        try {
            ArchiveStub archive = localArchive == null ? consumer.getCOMServices().getArchiveService()
                    .getArchiveStub() : localArchive.getArchiveStub();
            archive.query(true, objectsTypes, archiveQueryList, null, adapter);
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
     * Search a COM archive provider content to find the ObjectId of an App of
     * the CommandExecutor service of the SoftwareManagement.
     *
     * @param appName Name of the NMF app we want the logs for
     * @param domain Restricts the search to objects in a specific domain ID
     * @return the ObjectId of the found App or null if not found
     */
    public static ObjectId getAppObjectId(String appName, IdentifierList domain) {
        // SoftwareManagement.AppsLaunch.App object type
        ObjectType appType = new ObjectType(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NUMBER,
                AppsLauncherHelper.APPSLAUNCHER_SERVICE_NUMBER, new UOctet((short) 0),
                AppsLauncherHelper.APP_OBJECT_NUMBER);

        // prepare domain filter
        ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        ArchiveQuery archiveQuery = new ArchiveQuery(domain, null, null, 0L, null, null, null, null, null);
        archiveQueryList.add(archiveQuery);

        // execute query
        ArchiveToAppAdapter adapter = new ArchiveToAppAdapter(appName);
        queryArchive(appType, archiveQueryList, adapter, adapter);
        return adapter.getAppObjectId();
    }
}
