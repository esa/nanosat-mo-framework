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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.nmf.clitool.BaseCommand;
import static esa.mo.nmf.clitool.BaseCommand.consumer;
import static esa.mo.nmf.clitool.BaseCommand.queryArchive;
import esa.mo.nmf.clitool.TimestampedAggregationValue;
import esa.mo.nmf.clitool.TimestampedParameterValue;
import esa.mo.nmf.clitool.adapters.ArchiveToAggregationsAdapter;
import esa.mo.nmf.clitool.adapters.ArchiveToParametersAdapter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPair;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPairList;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.EntityRequest;
import org.ccsds.moims.mo.mal.structures.EntityRequestList;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.aggregation.AggregationHelper;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetails;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSet;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterValue;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationSetValue;
import org.ccsds.moims.mo.mc.parameter.ParameterHelper;
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterAdapter;
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterStub;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValueList;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePair;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePairList;
import picocli.CommandLine;

/**
 *
 * @author Cesar Coelho
 */
public class ParameterCommands {

    private static final Logger LOGGER = Logger.getLogger(ParameterCommands.class.getName());
    public static Identifier parameterSubscription;

    @CommandLine.Command(name = "subscribe", description = "Subscribes to specified parameters")
    public static class ParameterMonitorValue extends BaseCommand implements Runnable {

        @CommandLine.Parameters(arity = "0..*", paramLabel = "<parameterNames>", index = "0",
                description = "Names of the parameters to subscribe to. If non are specified subscribe to all.\n"
                + " - examples: param1 or param1 param2")
        List<String> parameterNames;

        @Override
        public void run() {
            if (!super.initRemoteConsumer()) {
                return;
            }

            if (consumer.getMCServices().getParameterService() == null) {
                System.out.println("Parameter service is not available for this provider!");
                return;
            }

            Identifier subscriptionId = new Identifier("CLI-Consumer-ParameterSubscription");
            EntityKeyList entityKeys = new EntityKeyList();
            if (parameterNames == null || parameterNames.isEmpty()) {
                EntityKey entitykey = new EntityKey(new Identifier("*"), 0L, 0L, 0L);
                entityKeys.add(entitykey);
            } else {
                for (String parameter : parameterNames) {
                    EntityKey entitykey = new EntityKey(new Identifier(parameter), 0L, 0L, 0L);
                    entityKeys.add(entitykey);
                }
            }
            EntityRequest entity = new EntityRequest(null, false, false, false, false, entityKeys);
            EntityRequestList entities = new EntityRequestList();
            entities.add(entity);

            ParameterStub stub = consumer.getMCServices().getParameterService().getParameterStub();
            Subscription subscription = new Subscription(subscriptionId, entities);
            parameterSubscription = subscriptionId;
            final Object lock = new Object();
            try {
                stub.monitorValueRegister(subscription, new ParameterAdapter() {
                    @Override
                    public void monitorValueNotifyReceived(MALMessageHeader msgHeader, Identifier identifier,
                            UpdateHeaderList updateHeaderList, ObjectIdList objectIdList,
                            ParameterValueList parameterValueList, Map qosProperties) {
                        String parameterName = updateHeaderList.get(0).getKey().getFirstSubKey().getValue()
                                .toLowerCase();
                        long timestamp = updateHeaderList.get(0).getTimestamp().getValue();
                        String value = parameterValueList.get(0).getRawValue().toString();

                        System.out.println("[" + timestamp + "] - " + parameterName + ": " + value);
                    }

                    @Override
                    public void monitorValueRegisterErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
                            Map qosProperties) {
                        LOGGER.log(Level.SEVERE, "Error during monitorValueRegister!", error);
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }
                });
            } catch (MALInteractionException | MALException e) {
                LOGGER.log(Level.SEVERE, "Error during monitorValueRegister!", e);
            }

            try {
                synchronized (lock) {
                    lock.wait();
                }
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Interrupted exception!", e);
            }
        }
    }

    @CommandLine.Command(name = "enable", description = "Enables generation of specified parameters")
    public static class ParameterEnableGeneration extends BaseCommand implements Runnable {

        @CommandLine.Parameters(arity = "0..*", paramLabel = "<parameterNames>", index = "0",
                description = "Names of the parameters to enable. If non are specified enable all")
        List<String> parameterNames;

        @Override
        public void run() {
            if (!super.initRemoteConsumer()) {
                return;
            }

            if (consumer.getMCServices().getParameterService() == null) {
                System.out.println("Parameter service is not available for this provider!");
                return;
            }
            enableOrDisableParameterGeneration(consumer.getMCServices().getParameterService().getParameterStub(),
                    parameterNames, true);
        }
    }

    @CommandLine.Command(name = "disable", description = "Disables generation of specified parameters")
    public static class ParameterDisableGeneration extends BaseCommand implements Runnable {

        @CommandLine.Parameters(arity = "0..*", paramLabel = "<parameterNames>", index = "0",
                description = "Names of the parameters to disable. If non are specified disable all")
        List<String> parameterNames;

        @Override
        public void run() {
            if (!super.initRemoteConsumer()) {
                return;
            }

            if (consumer.getMCServices().getParameterService() == null) {
                System.out.println("Parameter service is not available for this provider!");
                return;
            }
            enableOrDisableParameterGeneration(consumer.getMCServices().getParameterService().getParameterStub(),
                    parameterNames, false);
        }
    }

    @CommandLine.Command(name = "get", description = "Dumps to a file MO parameters samples from COM archive.")
    public static class GetParameters extends BaseCommand implements Runnable {

        @CommandLine.Parameters(arity = "1", paramLabel = "<filename>", index = "0",
                description = "Target file for the parameters samples")
        String file;

        @CommandLine.Parameters(arity = "0..*", paramLabel = "<parameterNames>", index = "1",
                description = "Names of the parameters to retrieve\n" + " - examples: param1 or param1 param2")
        List<String> parameterNames;

        @CommandLine.Option(names = {"-d", "--domain"}, paramLabel = "<domainId>",
                description = "Restricts the dump to parameters in a specific domain\n"
                + "  - format: key1.key2.[...].keyN.\n" + "  - example: esa.NMF_SDK.nanosat-mo-supervisor")
        String domainId;

        @CommandLine.Option(names = {"-s", "--start"}, paramLabel = "<startTime>",
                description = "Restricts the dump to parameters generated after the given time\n"
                + "  - format: \"yyyy-MM-dd HH:mm:ss.SSS\"\n" + "  - example: \"2021-03-04 08:37:58.482\"")
        String startTime;

        @CommandLine.Option(names = {"-e", "--end"}, paramLabel = "<endTime>",
                description = "Restricts the dump to parameters generated before the given time. "
                + "If this option is provided without the -s option, returns the single object that has the closest timestamp to, but not greater than <endTime>\n"
                + "  - format: \"yyyy-MM-dd HH:mm:ss.SSS\"\n" + "  - example: \"2021-03-05 12:05:45.271\"")
        String endTime;

        @CommandLine.Option(names = {"-j", "--json"}, paramLabel = "<json>",
                description = "If specified output will be in JSON format")
        boolean json;

        @Override
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
            // prepare domain, time and object id filters
            ArchiveQueryList archiveQueryList = new ArchiveQueryList();
            FineTime startTimeF = startTime == null ? null : HelperTime.readableString2FineTime(startTime);
            FineTime endTimeF = endTime == null ? null : HelperTime.readableString2FineTime(endTime);
            IdentifierList domain = domainId == null ? null : HelperMisc.domainId2domain(domainId);

            ArchiveQuery archiveQuery = new ArchiveQuery(domain, null, null, 0L, null, startTimeF, endTimeF, null,
                    null);
            archiveQueryList.add(archiveQuery);

            ArchiveToParametersAdapter parametersAdapter = new ArchiveToParametersAdapter();
            ObjectType parameterObjectType = new ObjectType(MCHelper.MC_AREA_NUMBER,
                    ParameterHelper.PARAMETER_SERVICE_NUMBER, MCHelper.MC_AREA_VERSION, new UShort(0));
            queryArchive(parameterObjectType, archiveQueryList, parametersAdapter, parametersAdapter);

            ArchiveToAggregationsAdapter aggregationsAdapter = new ArchiveToAggregationsAdapter();
            ObjectType aggregationObjectType = new ObjectType(MCHelper.MC_AREA_NUMBER,
                    AggregationHelper.AGGREGATION_SERVICE_NUMBER, MCHelper.MC_AREA_VERSION, new UShort(0));
            queryArchive(aggregationObjectType, archiveQueryList, aggregationsAdapter, aggregationsAdapter);

            Map<IdentifierList, Map<Identifier, List<TimestampedParameterValue>>> allParameters = parametersAdapter
                    .getParameterValues();

            // Display list of aggregations
            Map<IdentifierList, Map<Long, List<TimestampedAggregationValue>>> aggregationValuesMap = aggregationsAdapter
                    .getAggregationValues();
            if (aggregationValuesMap != null) {
                //Make the parameter map
                for (IdentifierList domainKey : aggregationValuesMap.keySet()) {
                    for (Map.Entry<Long, List<TimestampedAggregationValue>> entry : aggregationValuesMap.get(domainKey)
                            .entrySet()) {
                        Long definitionId = entry.getKey();
                        AggregationDefinitionDetails definition = aggregationsAdapter.getAggregationDefinitions().get(
                                domainKey).get(definitionId);

                        for (TimestampedAggregationValue aggregationValue : entry.getValue()) {
                            for (int i = 0;
                                    i < aggregationValue.getAggregationValue().getParameterSetValues().size();
                                    i++) {
                                AggregationSetValue values = aggregationValue.getAggregationValue()
                                        .getParameterSetValues().get(i);
                                AggregationParameterSet definitions = definition.getParameterSets().get(i);

                                int valueSetNumber = 0;
                                double deltaTime = values.getDeltaTime() != null ? values.getDeltaTime().getValue() : 0;
                                double intervalTime = values.getIntervalTime() != null ? values.getIntervalTime()
                                        .getValue() : 0;
                                long valueSetTimestamp = aggregationValue.getTimestamp().getValue()
                                        + (long) (deltaTime * 1000L);

                                for (int n = 0; n < values.getValues().size(); n++) {
                                    // Check if we are starting a new set of values compared to the given definition list
                                    if (n % definitions.getParameters().size() == 0) {
                                        valueSetNumber++;
                                    }

                                    AggregationParameterValue value = values.getValues().get(n);
                                    Long parameterId = definitions.getParameters().get(n % definitions.getParameters()
                                            .size());

                                    TimestampedParameterValue paramValue = new TimestampedParameterValue(value
                                            .getValue(), new FineTime(valueSetTimestamp + (long) (valueSetNumber
                                                    * intervalTime * 1000L)));

                                    Identifier parameterName = parametersAdapter.getIdentitiesMap().get(domainKey).get(
                                            parameterId);
                                    if (allParameters.get(domainKey).containsKey(parameterName)) {
                                        allParameters.get(domainKey).get(parameterName).add(paramValue);
                                    } else {
                                        List<TimestampedParameterValue> list = new ArrayList<>();
                                        list.add(paramValue);
                                        allParameters.get(domainKey).put(parameterName, list);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            try {
                if (!allParameters.isEmpty()) {
                    if (json && !file.endsWith(".json")) {
                        file = file + ".json";
                    }

                    FileWriter writer = new FileWriter(file);

                    Map<IdentifierList, Map<Identifier, List<TimestampedParameterValue>>> parameters = new HashMap<>();
                    if (parameterNames != null && !parameterNames.isEmpty()) {
                        for (String name : parameterNames) {
                            for (IdentifierList domainKey : allParameters.keySet()) {
                                List<TimestampedParameterValue> values = allParameters.get(domainKey).get(
                                        new Identifier(name));
                                if (values == null) {
                                    continue;
                                }
                                values.sort(Comparator.comparingLong(TimestampedParameterValue::getTimestamp));
                                if (!parameters.containsKey(domainKey)) {
                                    parameters.put(domainKey, new HashMap<>());
                                }
                                parameters.get(domainKey).put(new Identifier(name), values);
                            }
                        }
                    } else {
                        parameters = allParameters;
                        for (IdentifierList domainKey : parameters.keySet()) {
                            for (Map.Entry<Identifier, List<TimestampedParameterValue>> entry : parameters.get(
                                    domainKey).entrySet()) {
                                entry.getValue().sort(Comparator.comparingLong(
                                        TimestampedParameterValue::getTimestamp));
                            }
                        }
                    }

                    if (json) {
                        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
                        gson.toJson(parameters, writer);
                    } else {
                        for (IdentifierList domainKey : parameters.keySet()) {
                            writer.write("Domain: " + HelperMisc.domain2domainId(domainKey) + "\n");
                            List<String> keys = parameters.get(domainKey).keySet().stream().map(Identifier::getValue)
                                    .sorted().collect(Collectors.toList());
                            for (String parameter : keys) {
                                for (TimestampedParameterValue value : parameters.get(domainKey).get(new Identifier(
                                        parameter))) {
                                    String line = parameter + "\t" + value.getTimestamp() + "\t" + value
                                            .getParameterValue() + "\n";
                                    writer.write(line);
                                }
                            }
                        }
                    }
                    writer.close();
                    System.out.println("\nParameters successfully dumped to file: " + file + "\n");
                } else {
                    System.out.println("\nNo parameters found\n");
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error when writing to file", e);
            }
        }
    }

    @CommandLine.Command(name = "list", description = "Lists available parameters in a COM archive.")
    public static class ListParameters extends BaseCommand implements Runnable {

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

            ArchiveToParametersAdapter adapter = new ArchiveToParametersAdapter();
            queryArchive(ParameterHelper.PARAMETERIDENTITY_OBJECT_TYPE, archiveQueryList, adapter, adapter);

            // Display list of NMF apps that have parameters
            Map<IdentifierList, List<Identifier>> parameters = adapter.getParameterIdentities();
            if (parameters.size() <= 0) {
                System.out.println("\nNo parameter found in the provided archive: " + (databaseFile == null
                        ? providerURI : databaseFile));
            } else {
                System.out.println("\nFound the following parameters: ");
                for (Map.Entry<IdentifierList, List<Identifier>> entry : parameters.entrySet()) {
                    System.out.println("Domain: " + entry.getKey());
                    for (Identifier parameter : entry.getValue()) {
                        System.out.println("  - " + parameter);
                    }
                }
                System.out.println();
            }
        }
    }

    public static void enableOrDisableParameterGeneration(ParameterStub parameterService, List<String> parameters,
            boolean enable) {
        IdentifierList request = new IdentifierList();
        if (parameters == null || parameters.isEmpty()) {
            request.add(new Identifier("*"));
        } else {
            for (String name : parameters) {
                request.add(new Identifier(name));
            }
        }

        try {
            ObjectInstancePairList ids = parameterService.listDefinition(request);
            InstanceBooleanPairList enableInstances = new InstanceBooleanPairList();

            for (ObjectInstancePair pair : ids) {
                enableInstances.add(new InstanceBooleanPair(pair.getObjIdentityInstanceId(), enable));
            }

            parameterService.enableGeneration(false, enableInstances);
            System.out.println((enable ? "Enable " : "Disable ") + "successful.");
        } catch (MALInteractionException e) {
            MALStandardError error = e.getStandardError();
            if (error.getErrorNumber().equals(MALHelper.UNKNOWN_ERROR_NUMBER)) {
                System.out.println("Provided parameters don't exist in the provider:");
                for (UInteger id : (UIntegerList) error.getExtraInformation()) {
                    System.out.println("- " + request.get((int) id.getValue()));
                }
            } else {
                LOGGER.log(Level.SEVERE, "Error during enableGeneration!", e);
            }
        } catch (MALException e) {
            LOGGER.log(Level.SEVERE, "Error during enableGeneration!", e);
        }
    }
}
