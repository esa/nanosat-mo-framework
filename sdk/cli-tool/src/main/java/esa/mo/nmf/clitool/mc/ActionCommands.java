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

import static esa.mo.nmf.clitool.BaseCommand.consumer;
import static esa.mo.nmf.clitool.BaseCommand.queryArchive;
import esa.mo.nmf.clitool.Args;
import esa.mo.nmf.clitool.BaseCommand;
import esa.mo.nmf.clitool.adapters.ArchiveToActionsAdapter;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ArchiveQuery;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperDomain;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mc.action.ActionServiceInfo;

/**
 *
 * @author Cesar Coelho
 */
public class ActionCommands {

    private static final Logger LOGGER = Logger.getLogger(ActionCommands.class.getName());

    public static class SubmitAction extends BaseCommand {

        @Override
        public void run(Args args) {
            parseBaseOptions(args);
            List<String> positionals = args.positionals();
            if (positionals.isEmpty()) {
                System.out.println("Missing required argument: <actionName>");
                return;
            }
            String actionName = positionals.get(0);
            List<String> inputArguments = positionals.subList(1, positionals.size());

            if (!super.initRemoteConsumer()) {
                return;
            }

            if (consumer.getMCServices().getActionService() == null) {
                System.out.println("Action service is not available for this provider!");
                return;
            }

            Serializable[] objs = new Serializable[inputArguments.size()];
            for (int i = 0; i < inputArguments.size(); i++) {
                String inputValue = inputArguments.get(i);
                try {
                    objs[i] = Long.valueOf(inputValue);
                } catch (NumberFormatException ex) {
                    objs[i] = inputValue;
                }
            }

            consumer.launchAction(actionName, objs);
        }
    }

    public static class ListActions extends BaseCommand {

        @Override
        public void run(Args args) {
            parseBaseOptions(args);
            String domainId = args.option("-d", "--domain");

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
            IdentifierList domain = domainId == null ? null : HelperDomain.domainId2domain(domainId);

            ArchiveQuery archiveQuery = new ArchiveQuery(domain, null, null, 0L, null, null, null, null, null);

            ArchiveToActionsAdapter adapter = new ArchiveToActionsAdapter();
            queryArchive(ActionServiceInfo.ACTIONDEFINITION_OBJECT_TYPE, archiveQuery, adapter, adapter);

            // Display list of NMF apps that have actions
            Map<IdentifierList, List<Identifier>> actions = adapter.getActionNames();
            if (actions.size() <= 0) {
                String str = (databaseFile == null) ? providerURI : databaseFile;
                System.out.println("\nNo action found in the provided archive: " + str);
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
