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
package esa.mo.nmf.cliconsumer;

import esa.mo.nmf.NMFConsumer;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.common.login.LoginHelper;
import org.ccsds.moims.mo.common.login.body.LoginResponse;
import org.ccsds.moims.mo.common.login.structures.Profile;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import picocli.CommandLine.*;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author marcel.mikolajko
 */
public abstract class BaseCommand {
    private static final Logger LOGGER = Logger.getLogger(BaseCommand.class.getName());


    @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
    private boolean helpRequested;

    @Option(names = {"-u", "--uri"}, paramLabel = "<providerURI>",
            description = "Provider URI\n"
                          + "  - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Directory")
    public String providerURI;

    @Option(names = {"-p", "--provider"}, paramLabel = "<providerName>",
            description = "Name of the provider we want to connect to")
    public String providerName;

    public static NMFConsumer consumer;
    public static IdentifierList domain;

    public boolean initConsumer()
    {
        try
        {
            ProviderSummaryList providerSummaryList = NMFConsumer.retrieveProvidersFromDirectory(new URI(providerURI));
            ProviderSummary provider = null;
            if(providerSummaryList.size() == 1)
            {
                if(providerName != null)
                {
                    System.out.println("There's only one provider in directory. Ignoring --provider option.");
                }
                provider = providerSummaryList.get(0);
            }
            else
            {
                if(providerName == null)
                {
                    System.out.println("\nThere's more than one provider in directory. In this case the --provider option is required");
                    System.out.println("Available providers at this uri: " + providerURI);
                    for(ProviderSummary summary : providerSummaryList)
                    {
                        System.out.println(" - " + summary.getProviderId());
                    }
                    System.out.println();
                    return false;
                }

                for(ProviderSummary summary : providerSummaryList)
                {
                    if(summary.getProviderId().getValue().equals(providerName))
                    {
                        provider = summary;
                        break;
                    }
                }
            }

            if(provider == null)
            {
                System.out.println("\nProvider not found!");
                if(!providerSummaryList.isEmpty())
                {
                    System.out.println("Available providers at this uri: " + providerURI);
                    for(ProviderSummary summary : providerSummaryList)
                    {
                        System.out.println(" - " + summary.getProviderId());
                    }
                }
                else
                {
                    System.out.println("No providers available at this uri: " + providerURI);
                }
                System.out.println();
                return false;
            }

            consumer = new NMFConsumer(provider);
            consumer.init();
            domain = provider.getProviderKey().getDomain();

            if(consumer.getCommonServices().getLoginService() != null &&
               consumer.getCommonServices().getLoginService().getLoginStub() != null) {
                System.out.println("\nLogin required for " + provider.getProviderId());

                String login = System.console().readLine("Login: ");
                char[] password = System.console().readPassword("Password: ");
                System.out.println();

                LongList ids = consumer.getCommonServices().getLoginService().getLoginStub().listRoles(new Identifier(login), String.valueOf(password));

                List<Long> roleIds = new ArrayList<>();
                List<String> roleNames = new ArrayList<>();
                final Object lock = new Object();
                ArchiveAdapter adapter = new ArchiveAdapter()
                {
                    @Override
                    public void retrieveResponseReceived(MALMessageHeader msgHeader, ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties)
                    {
                        for(int i = 0; i < objDetails.size(); ++i)
                        {
                            roleIds.add(objDetails.get(i).getInstId());
                            roleNames.add(objBodies.get(i).toString());
                        }
                        synchronized (lock)
                        {
                            lock.notifyAll();
                        }
                    }
                };

                consumer.getCOMServices()
                        .getArchiveService()
                        .getArchiveStub()
                        .retrieve(LoginHelper.LOGINROLE_OBJECT_TYPE,
                                  consumer.getCommonServices().getLoginService().getConnectionDetails().getDomain(),
                                  ids,
                                  adapter);

                synchronized (lock)
                {
                    lock.wait(10000);
                }

                Long roleId = null;
                if(!roleIds.isEmpty())
                {
                    System.out.println("\nAvailable roles: ");
                    for(int i = 0; i < roleIds.size(); ++i)
                    {
                        System.out.println((i + 1) + " - " + roleNames.get(i));
                    }
                    int index = Integer.parseInt(System.console().readLine("Select role id: ")) - 1;
                    if(index >= 0 && index < roleIds.size())
                    {
                        roleId = roleIds.get(index);
                    }
                }

                LoginResponse response = consumer.getCommonServices().getLoginService().getLoginStub()
                                                 .login(new Profile(new Identifier(login), roleId), String.valueOf(password));
                consumer.setAuthenticationId(response.getBodyElement0());
                System.out.println("Login successful!");
            }
        }
        catch (MALException | MalformedURLException | MALInteractionException | InterruptedException e)
        {
            LOGGER.log(Level.SEVERE, "Error when creating consumer", e);
            closeConsumer();
            return false;
        }
        System.out.println("\n");
        return true;
    }

    public static void closeConsumer()
    {
        IdentifierList ids = new IdentifierList();
        try
        {
            if(MCCommands.parameterSubscription != null)
            {
                ids.clear();
                ids.add(MCCommands.parameterSubscription);
                consumer.getMCServices().getParameterService().getParameterStub().monitorValueDeregister(ids);
            }

            if(MCCommands.aggregationSubscription != null)
            {
                ids.clear();
                ids.add(MCCommands.aggregationSubscription);
                consumer.getMCServices().getAggregationService().getAggregationStub().monitorValueDeregister(ids);
            }

            if(SoftwareManagementCommands.heartbeatSubscription != null)
            {
                ids.clear();
                ids.add(SoftwareManagementCommands.heartbeatSubscription);
                consumer.getSMServices().getHeartbeatService().getHeartbeatStub().beatDeregister(ids);
            }

            if(SoftwareManagementCommands.outputSubscription != null)
            {
                ids.clear();
                ids.add(SoftwareManagementCommands.outputSubscription);
                consumer.getSMServices().getAppsLauncherService().getAppsLauncherStub().monitorExecutionDeregister(ids);
            }
        }
        catch (MALInteractionException | MALException e)
        {
            LOGGER.log(Level.SEVERE, "Failed to deregister subscription: " + ids.get(0), e);
        }

        if(consumer != null)
        {
            consumer.getCommonServices().closeConnections();
            consumer.getCOMServices().closeConnections();
            consumer.getMCServices().closeConnections();
            consumer.getPlatformServices().closeConnections();
            consumer.getSMServices().closeConnections();
            consumer = null;
        }
        System.out.println("Consumer successfully closed.");
    }
}
//------------------------------------------------------------------------------