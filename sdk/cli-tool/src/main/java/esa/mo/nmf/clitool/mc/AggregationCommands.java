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
import esa.mo.nmf.clitool.BaseCommand;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveStub;
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.aggregation.AggregationServiceInfo;
import org.ccsds.moims.mo.mc.aggregation.consumer.AggregationAdapter;
import org.ccsds.moims.mo.mc.aggregation.consumer.AggregationStub;
import org.ccsds.moims.mo.mc.parameter.ParameterServiceInfo;
import org.ccsds.moims.mo.mc.structures.*;
import picocli.CommandLine;

/**
 *
 * @author Cesar Coelho
 */
public class AggregationCommands {

    private static final Logger LOGGER =
            Logger.getLogger(AggregationCommands.class.getName());
    public static Identifier aggregationSubscription;

    public static void enableOrDisableAggregationGeneration(
            AggregationStub aggregationService,
            List<String> aggregations, boolean enable) {
        IdentifierList request = new IdentifierList();
        if (aggregations == null || aggregations.isEmpty()) {
            request.add(new Identifier("*"));
        } else {
            for (String name : aggregations) {
                request.add(new Identifier(name));
            }
        }

        try {
            LongList ids = aggregationService.listDefinition(request);
            aggregationService.enableReporting(enable, ids);
            System.out.println((enable ? "Enable " : "Disable ") + "successful.");
        } catch (MALInteractionException e) {
            MOErrorException error = e.getStandardError();
            if (error.getErrorNumber().equals(MALHelper.UNKNOWN_ERROR_NUMBER)) {
                System.out.println(
                        "Provided aggregations don't exist in the provider:");
                for (UInteger id : (UIntegerList) error.getExtraInformation()) {
                    System.out.println("- " + request.get((int) id.getValue()));
                }
            } else {
                LOGGER.log(Level.SEVERE, "Error during enableReporting!", e);
            }
        } catch (MALException e) {
            LOGGER.log(Level.SEVERE, "Error during enableReporting!", e);
        }
    }

    @CommandLine.Command(
            name = "enable",
            description = "Enables generation of specified aggregations")
    public static class AggregationEnableGeneration extends BaseCommand implements Runnable {

        @CommandLine.Parameters(
                arity = "0..*",
                paramLabel = "<aggregationNames>",
                index = "0",
                description = "Names of the aggregations to enable."
                + " If non are specified enable all")
        List<String> aggregationNames;

        @Override
        public void run() {
            if (!super.initRemoteConsumer()) {
                return;
            }

            if (consumer.getMCServices().getAggregationService() == null) {
                System.out.println(
                        "Aggregation service is not available for this provider!");
                return;
            }
            enableOrDisableAggregationGeneration(
                    consumer.getMCServices().getAggregationService().getAggregationStub(),
                    aggregationNames, true);
        }
    }

    @CommandLine.Command(
            name = "disable",
            description = "Disables generation of specified aggregations")
    public static class AggregationDisableGeneration extends BaseCommand implements Runnable {

        @CommandLine.Parameters(
                arity = "0..*",
                paramLabel = "<aggregationNames>",
                index = "0",
                description = "Names of the aggregations to disable."
                + " If non are specified disable all")
        List<String> aggregationNames;

        @Override
        public void run() {
            if (!super.initRemoteConsumer()) {
                return;
            }

            if (consumer.getMCServices().getAggregationService() == null) {
                System.out.println(
                        "Aggregation service is not available for this provider!");
                return;
            }
            enableOrDisableAggregationGeneration(
                    consumer.getMCServices().getAggregationService().getAggregationStub(),
                    aggregationNames, false);
        }
    }

    @CommandLine.Command(
            name = "subscribe",
            description = "Subscribes to specified aggregations")
    public static class AggregationMonitorValue extends BaseCommand implements Runnable {

        @CommandLine.Parameters(
                arity = "0..*",
                paramLabel = "<parameterNames>",
                index = "0",
                description = "Names of the aggregations to subscribe to."
                + " If non are specified subscribe to all.\n"
                + " - examples: aggregation1 or aggregation1 aggregation2")
        List<String> aggregationNames;

        @Override
        public void run() {
            if (!super.initRemoteConsumer()) {
                return;
            }

            if (consumer.getMCServices().getAggregationService() == null) {
                System.out.println(
                        "Aggregation service is not available for this provider!");
                return;
            }

            AggregationStub stub = consumer.getMCServices()
                    .getAggregationService().getAggregationStub();
            try {
                IdentifierList names = new IdentifierList();
                if (aggregationNames == null || aggregationNames.isEmpty()) {
                    names.add(new Identifier("*"));
                } else {
                    for (String name : aggregationNames) {
                        names.add(new Identifier(name));
                    }
                }

                LongList result = stub.listDefinition(names);

                ArchiveStub archive =
                        consumer.getCOMServices().getArchiveService().getArchiveStub();
                LongList aggregationIds = new LongList();
                LongList parameterIds = new LongList();

                for (Long id : result) {
                    aggregationIds.add(id);
                }

                final Object lock = new Object();

                archive.retrieve(
                        AggregationServiceInfo.AGGREGATIONDEFINITION_OBJECT_TYPE,
                        domain, aggregationIds,
                        new ArchiveAdapter() {
                    @Override
                    public void retrieveResponseReceived(
                            MALMessageHeader msgHeader,
                            ArchiveDetailsList objDetails,
                            HeterogeneousList objBodies,
                            Map qosProperties) {
                        for (int i = 0; i < objDetails.size(); ++i) {
                            AggregationDefinition def =
                                    (AggregationDefinition) objBodies.get(i);
                            if (def.getReportingEnabled()) {
                                for (AggregationParameterSet set
                                        : def.getParameterSets()) {
                                    parameterIds.addAll(set.getParameters());
                                }
                            } else {
                                System.out.println("Aggregation "
                                        + def.getName()
                                        + " is disabled!");
                            }

                            if (!def.getSendDefinitions()) {
                                System.out.println(
                                        "sendDefinitions is set to false for aggregation: "
                                        + def.getName()
                                        + ". Parameter names will not be available.");
                            }
                        }

                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }

                    @Override
                    public void retrieveResponseErrorReceived(
                            MALMessageHeader msgHeader,
                            MOErrorException error, Map qosProperties) {
                        LOGGER.log(Level.SEVERE,
                                "Error during archive retrieve!", error);
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }
                });

                synchronized (lock) {
                    lock.wait();
                }

                if (parameterIds.isEmpty()) {
                    return;
                }

                Map<Long, String> definitionIdToName = new HashMap<>();
                archive.retrieve(
                        ParameterServiceInfo.PARAMETERDEFINITION_OBJECT_TYPE,
                        domain, parameterIds,
                        new ArchiveAdapter() {
                    @Override
                    public void retrieveResponseReceived(
                            MALMessageHeader msgHeader,
                            ArchiveDetailsList objDetails,
                            HeterogeneousList objBodies,
                            Map qosProperties) {
                        for (int i = 0; i < objDetails.size(); ++i) {
                            definitionIdToName.put(
                                    objDetails.get(i).getId(),
                                    ((ParameterDefinition) objBodies.get(i)).getName().getValue());
                        }

                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }

                    @Override
                    public void retrieveResponseErrorReceived(
                            MALMessageHeader msgHeader,
                            MOErrorException error, Map qosProperties) {
                        LOGGER.log(Level.SEVERE,
                                "Error during archive retrieve!", error);
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }
                });

                synchronized (lock) {
                    lock.wait();
                }

                Identifier subscriptionId =
                        new Identifier("CLI-Consumer-AggregationSubscription");
                /*
                EntityKeyList entityKeys = new EntityKeyList();
                if (aggregationNames == null || aggregationNames.isEmpty()) {
                    EntityKey entitykey = new EntityKey(new Identifier("*"), 0L, 0L, 0L);
                    entityKeys.add(entitykey);
                } else {
                    for (String aggregation : aggregationNames) {
                        EntityKey entitykey = new EntityKey(new Identifier(aggregation), 0L, 0L, 0L);
                        entityKeys.add(entitykey);
                    }
                }
                EntityRequest entity = new EntityRequest(null, false, false, false, false, entityKeys);
                EntityRequestList entities = new EntityRequestList();
                entities.add(entity);
                 */
                SubscriptionFilterList filters = new SubscriptionFilterList();
                if (aggregationNames != null && !aggregationNames.isEmpty()) {
                    AttributeList acceptableNames = new AttributeList();
                    for (String aggregation : aggregationNames) {
                        acceptableNames.add(new Identifier(aggregation));
                    }
                    filters.add(new SubscriptionFilter(new Identifier("aggregationName"), acceptableNames));
                }

                Subscription subscription =
                        new Subscription(subscriptionId, null, null, filters);
                aggregationSubscription = subscriptionId;

                stub.monitorValueRegister(subscription, new AggregationAdapter() {
                    @Override
                    public void monitorValueNotifyReceived(
                            org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
                            org.ccsds.moims.mo.mal.structures.Identifier subscriptionId,
                            org.ccsds.moims.mo.mal.structures.UpdateHeader updateHeader,
                            org.ccsds.moims.mo.com.structures.ObjectKey objKey,
                            org.ccsds.moims.mo.mc.structures.AggregationValue newValue,
                            java.util.Map qosProperties) {
                        String aggregationName = ((Identifier) updateHeader
                                .getKeyValues().get(0).getValue()).getValue();
                        //long timestamp = updateHeaderList.get(0).getTimestamp().getValue();
                        AggregationParameterValueList values = newValue
                                .getParameterSetValues().get(0).getValues();
                        System.out.println(aggregationName + ": ");
                        int index = 1;
                        for (AggregationParameterValue value : values) {
                            String name =
                                    definitionIdToName.get(value.getParamDefinitionId());
                            System.out.println(
                                    "  " + (name == null ? "parameter " + index : name)
                                    + ": " + value.getValue().getRawValue().toString());
                            index += 1;
                        }
                        System.out.println();
                    }

                    @Override
                    public void monitorValueRegisterErrorReceived(
                            MALMessageHeader msgHeader,
                            MOErrorException error, Map qosProperties) {
                        LOGGER.log(Level.SEVERE,
                                "Error during monitorValueRegister!", error);
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }
                });

                synchronized (lock) {
                    lock.wait();
                }

            } catch (MALInteractionException | MALException | InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Error during monitorValueRegister!", e);
            }
        }
    }

}
