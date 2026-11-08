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
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperDomain;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mc.action.ActionServiceInfo;
import org.ccsds.moims.mo.mc.action.consumer.ActionStub;
import org.ccsds.moims.mo.mc.structures.ActionExecution;
import org.ccsds.moims.mo.mc.structures.ActionExecutionList;
import org.ccsds.moims.mo.mc.structures.ExecutionStage;
import org.ccsds.moims.mo.mc.structures.ExecutionStatus;

/**
 * Container for the {@code action} CLI subcommands.
 *
 * @author Cesar Coelho
 */
public class ActionCommands {

    private static final Logger LOGGER = Logger.getLogger(ActionCommands.class.getName());

    private ActionCommands() {
    }

    /**
     * Implements the {@code action trigger} CLI command.
     */
    public static class SubmitAction extends BaseCommand {
        /**
         * Default constructor.
         */
        public SubmitAction() {
        }


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
                objs[i] = argumentOf(inputArguments.get(i));
            }

            consumer.launchAction(actionName, objs);
        }

        /**
         * Reads an argument as the type it is written in.
         * <p>
         * A number is a number, and a number with an s after it is a length of
         * time: actions that take one ask for a MO Duration, which is not a
         * number and cannot be passed as one, so it has to be recognisable on a
         * command line. Anything else is text.
         *
         * @param value The argument as it was written.
         * @return What to send.
         */
        private static Serializable argumentOf(String value) {
            if (value.length() > 1 && value.endsWith("s")) {
                try {
                    return new Duration(Double.parseDouble(value.substring(0, value.length() - 1)));
                } catch (NumberFormatException ex) {
                    // Not a length of time after all: it only ends in an s.
                }
            }

            try {
                return Long.valueOf(value);
            } catch (NumberFormatException ex) {
                return value;
            }
        }
    }

    /**
     * Implements the {@code action executions} CLI command: the executions of
     * actions that the provider received, each with the stages that it reached.
     */
    public static class ListExecutions extends BaseCommand {
        /**
         * Default constructor.
         */
        public ListExecutions() {
        }

        @Override
        public void run(Args args) {
            parseBaseOptions(args);
            String maxCountOption = args.option("-n", "--max-count");
            List<String> positionals = args.positionals();

            if (!super.initRemoteConsumer()) {
                return;
            }
            if (consumer.getMCServices().getActionService() == null) {
                System.out.println("Action service is not available for this provider!");
                return;
            }

            ActionStub stub = consumer.getMCServices().getActionService().getActionStub();
            try {
                LongList definitionIds = new LongList();
                if (positionals.isEmpty()) {
                    definitionIds.add(0L); // All the actions
                } else {
                    IdentifierList names = new IdentifierList();
                    names.add(new Identifier(positionals.get(0)));
                    definitionIds = stub.listDefinition(names);
                }
                UInteger maxCount = (maxCountOption == null) ? null : new UInteger(Long.parseLong(maxCountOption));

                ActionExecutionList executions = stub.listExecutions(definitionIds, null, null, maxCount);
                if (executions.isEmpty()) {
                    System.out.println("No execution found.");
                }
                for (ActionExecution execution : executions) {
                    System.out.println(execution.getExecutionId() + "  "
                            + execution.getRequestTime().toReadableString()
                            + "  definition " + execution.getRequest().getDefinitionId());
                    if (execution.getStages().isEmpty()) {
                        System.out.println("    no stage reached");
                    }
                    for (ExecutionStage stage : execution.getStages()) {
                        System.out.println("    " + describe(stage));
                    }
                }
            } catch (NumberFormatException ex) {
                System.out.println("The max count is not a number: " + maxCountOption);
            } catch (MOErrorException | MALException ex) {
                LOGGER.log(Level.SEVERE, "Error during listExecutions!", ex);
            }
        }

        private static String describe(ExecutionStage stage) {
            ExecutionStatus status = stage.getStatus();
            StringBuilder line = new StringBuilder();
            line.append(stage.getTime().toReadableString()).append("  ")
                    .append(status.getStageType()).append(status.getSuccess() ? " succeeded" : " failed");
            if (status.getStep() != null) {
                line.append(", step ").append(status.getStep());
            }
            if (status.getComment() != null) {
                line.append(": ").append(status.getComment());
            }
            return line.toString();
        }
    }

    /**
     * Implements the {@code action list} CLI command.
     */
    public static class ListActions extends BaseCommand {
        /**
         * Default constructor.
         */
        public ListActions() {
        }


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

            ArchiveQuery archiveQuery = new ArchiveQuery(domain, null, 0L, null, null, null, null);

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
