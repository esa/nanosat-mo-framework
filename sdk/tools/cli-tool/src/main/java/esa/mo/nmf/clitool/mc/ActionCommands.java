/* ----------------------------------------------------------------------------
 * Copyright (C) 2023      European Space Agency
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
package esa.mo.nmf.clitool.mc;

import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.nmf.clitool.BaseCommand;
import static esa.mo.nmf.clitool.BaseCommand.consumer;
import static esa.mo.nmf.clitool.BaseCommand.queryArchive;
import esa.mo.nmf.clitool.adapters.ArchiveToActionsAdapter;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mc.action.ActionHelper;
import picocli.CommandLine;

/**
 *
 * @author Cesar Coelho
 */
public class ActionCommands {
    
    private static final Logger LOGGER = Logger.getLogger(ActionCommands.class.getName());
    
    @CommandLine.Command(name = "trigger", description = "Triggers a specific action.")
    public static class SubmitAction extends BaseCommand implements Runnable {
        
        @CommandLine.Parameters(arity = "1", paramLabel = "<actionName>",
                index = "0", description = "Name of the action to trigger.")
        String actionName;
        
        @CommandLine.Parameters(arity = "0..*", paramLabel = "<arguments>", index = "1",
                description = "List of the arguments to use by the action.")
        List<String> inputArguments;
        
        @Override
        public void run() {
            if (!super.initRemoteConsumer()) {
                return;
            }
            
            if (consumer.getMCServices().getActionService() == null) {
                System.out.println("Action service is not available for this provider!");
                return;
            }
            
            Serializable[] objs = new Serializable[0];
            
            if (inputArguments != null) {
                objs = new Serializable[inputArguments.size()];
                
                for (int i = 0; i < inputArguments.size(); i++) {
                    String inputValue = inputArguments.get(i);
                    
                    try {
                        Long longValue = Long.valueOf(inputValue);
                        objs[i] = longValue;
                    } catch (NumberFormatException ex) {
                        objs[i] = inputArguments.get(i);
                    }
                }
            }
            
            consumer.launchAction(actionName, objs);
        }
    }
    
    @CommandLine.Command(name = "list", description = "Lists available actions in a COM archive.")
    public static class ListActions extends BaseCommand implements Runnable {
        
        @CommandLine.Option(names = {"-d", "--domain"}, paramLabel = "<domainId>",
                description = "Restricts the dump to objects in a specific domain\n"
                + "  - format: key1.key2.[...].keyN.\n" + "  - example: esa.NMF_SDK.nanosat-mo-supervisor")
        String domainId;
        
        public void run() {
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
            IdentifierList domain = domainId == null ? null : HelperMisc.domainId2domain(domainId);
            
            ArchiveQueryList archiveQueryList = new ArchiveQueryList();
            ArchiveQuery archiveQuery = new ArchiveQuery(domain, null, null, 0L, null, null, null, null, null);
            archiveQueryList.add(archiveQuery);
            
            ArchiveToActionsAdapter adapter = new ArchiveToActionsAdapter();
            queryArchive(ActionHelper.ACTIONIDENTITY_OBJECT_TYPE, archiveQueryList, adapter, adapter);

            // Display list of NMF apps that have actions
            Map<IdentifierList, List<Identifier>> actions = adapter.getActionIdentities();
            if (actions.size() <= 0) {
                System.out.println("\nNo action found in the provided archive: " + (databaseFile == null
                        ? providerURI : databaseFile));
            } else {
                System.out.println("\nFound the following actions: ");
                for (Map.Entry<IdentifierList, List<Identifier>> entry : actions.entrySet()) {
                    System.out.println("Domain: " + entry.getKey());
                    for (Identifier action : entry.getValue()) {
                        System.out.println("  - " + action);
                    }
                }
                System.out.println();
            }
        }
    }
    
}
