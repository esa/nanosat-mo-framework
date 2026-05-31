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
import esa.mo.nmf.clitool.Args;
import esa.mo.nmf.clitool.BaseCommand;
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
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperDomain;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperTime;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.aggregation.AggregationServiceInfo;
import org.ccsds.moims.mo.mc.parameter.ParameterServiceInfo;
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterAdapter;
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterStub;
import org.ccsds.moims.mo.mc.structures.*;

/**
 *
 * @author Cesar Coelho
 */
public class ParameterCommands {

    private static final Logger LOGGER = Logger.getLogger(ParameterCommands.class.getName());
    public static Identifier parameterSubscription;

    public static class ParameterMonitorValue extends BaseCommand {

        @Override
        public void run(Args args) {
            parseBaseOptions(args);
            List<String> parameterNames = args.positionals();

            if (!super.initRemoteConsumer()) {
                return;
            }

            if (consumer.getMCServices().getParameterService() == null) {
                System.out.println("Parameter service is not available for this provider!");
                return;
            }

            Identifier subscriptionId =
                    new Identifier("CLI-Consumer-ParameterSubscription");
            SubscriptionFilterList filters = new SubscriptionFilterList();
            if (!parameterNames.isEmpty()) {
                AttributeList acceptableNames = new AttributeList();
                for (String parameter : parameterNames) {
                    acceptableNames.add(new Identifier(parameter));
                }
                filters.add(new SubscriptionFilter(new Identifier("name"), acceptableNames));
            }

            ParameterStub stub = consumer.getMCServices().getParameterService().getParameterStub();
            Subscription subscription = new Subscription(subscriptionId, null, null, filters);
            parameterSubscription = subscriptionId;
            final Object lock = new Object();
            try {
                stub.monitorValueRegister(subscription, new ParameterAdapter() {
                    @Override
                    public void monitorValueNotifyReceived(
                            org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
                            org.ccsds.moims.mo.mal.structures.Identifier subscriptionId,
                            org.ccsds.moims.mo.mal.structures.UpdateHeader updateHeader,
                            org.ccsds.moims.mo.com.structures.ObjectKey objKey,
                            org.ccsds.moims.mo.mc.structures.ParameterValue newValue,
                            java.util.Map qosProperties) {
                        String parameterName = ((Identifier) updateHeader
                                .getKeyValues().get(0).getValue()).getValue();
                        long timestamp = msgHeader.getTimestamp().getValue();
                        String value = newValue.getRawValue() == null
                                ? "null" : newValue.getRawValue().toString();

                        System.out.println("[" + timestamp + "] - " + parameterName + ": " + value);
                    }

                    @Override
                    public void monitorValueRegisterErrorReceived(
                            MALMessageHeader msgHeader,
                            MOErrorException error,
                            Map qosProperties) {
                        LOGGER.log(Level.SEVERE,
                                "Error during monitorValueRegister!", error);
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

    public static class ParameterEnableGeneration extends BaseCommand {

        @Override
        public void run(Args args) {
            parseBaseOptions(args);
            List<String> parameterNames = args.positionals();

            if (!super.initRemoteConsumer()) {
                return;
            }

            if (consumer.getMCServices().getParameterService() == null) {
                System.out.println("Parameter service is not available for this provider!");
                return;
            }
            enableOrDisableParameterGeneration(
                    consumer.getMCServices().getParameterService().getParameterStub(),
                    parameterNames, true);
        }
    }

    public static class ParameterDisableGeneration extends BaseCommand {

        @Override
        public void run(Args args) {
            parseBaseOptions(args);
            List<String> parameterNames = args.positionals();

            if (!super.initRemoteConsumer()) {
                return;
            }

            if (consumer.getMCServices().getParameterService() == null) {
                System.out.println("Parameter service is not available for this provider!");
                return;
            }
            enableOrDisableParameterGeneration(
                    consumer.getMCServices().getParameterService().getParameterStub(),
                    parameterNames, false);
        }
    }

    public static class GetParameters extends BaseCommand {

        @Override
        public void run(Args args) {
            parseBaseOptions(args);
            String domainId  = args.option("-d", "--domain");
            String startTime = args.option("-s", "--start");
            String endTime   = args.option("-e", "--end");
            boolean json     = args.flag("-j", "--json");

            List<String> positionals = args.positionals();
            if (positionals.isEmpty()) {
                System.out.println("Missing required argument: <filename>");
                return;
            }
            String file = positionals.get(0);
            List<String> parameterNames = positionals.subList(1, positionals.size());

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
            Time startTimeF =
                    startTime == null ? null : HelperTime.readableString2Time(startTime);
            Time endTimeF =
                    endTime == null ? null : HelperTime.readableString2Time(endTime);
            IdentifierList domain =
                    domainId == null ? null : HelperDomain.domainId2domain(domainId);

            ArchiveQuery archiveQuery = new ArchiveQuery(
                    domain, null, 0L, null, startTimeF, endTimeF, null, null);

            ArchiveToParametersAdapter parametersAdapter = new ArchiveToParametersAdapter();
            ObjectType parameterObjectType = new ObjectType(
                    MCHelper.MC_AREA_NUMBER,
                    ParameterServiceInfo.PARAMETER_SERVICE_NUMBER,
                    MCHelper.MC_AREA_VERSION,
                    new UShort(0));
            queryArchive(parameterObjectType, archiveQuery, parametersAdapter, parametersAdapter);

            ArchiveToAggregationsAdapter aggregationsAdapter =
                    new ArchiveToAggregationsAdapter();
            ObjectType aggregationObjectType = new ObjectType(
                    MCHelper.MC_AREA_NUMBER,
                    AggregationServiceInfo.AGGREGATION_SERVICE_NUMBER,
                    MCHelper.MC_AREA_VERSION,
                    new UShort(0));
            queryArchive(
                    aggregationObjectType, archiveQuery,
                    aggregationsAdapter, aggregationsAdapter);

            Map<IdentifierList, Map<Identifier, List<TimestampedParameterValue>>>
                    allParameters = parametersAdapter.getParameterValues();

            // Display list of aggregations
            Map<IdentifierList, Map<Long, List<TimestampedAggregationValue>>>
                    aggregationValuesMap = aggregationsAdapter.getAggregationValues();
            if (aggregationValuesMap != null) {
                //Make the parameter map
                for (IdentifierList domainKey : aggregationValuesMap.keySet()) {
                    for (Map.Entry<Long, List<TimestampedAggregationValue>> entry
                            : aggregationValuesMap.get(domainKey).entrySet()) {
                        Long definitionId = entry.getKey();
                        AggregationDefinition definition =
                                aggregationsAdapter.getAggregationDefinitions()
                                .get(domainKey).get(definitionId);

                        for (TimestampedAggregationValue aggregationValue : entry.getValue()) {
                            for (int i = 0; i < aggregationValue.getAggregationValue()
                                    .getParameterSetValues().size(); i++) {
                                AggregationSetValue values = aggregationValue
                                        .getAggregationValue()
                                        .getParameterSetValues().get(i);
                                AggregationParameterSet definitions =
                                        definition.getParameterSets().get(i);

                                int valueSetNumber = 0;
                                double deltaTime = values.getDeltaTime() != null
                                        ? values.getDeltaTime().getInSeconds() : 0;
                                double intervalTime = values.getIntervalTime() != null
                                        ? values.getIntervalTime().getInSeconds() : 0;
                                long valueSetTimestamp =
                                        aggregationValue.getTimestamp().getValue()
                                        + (long) (deltaTime * 1000L);

                                for (int n = 0; n < values.getValues().size(); n++) {
                                    // Check if we are starting a new set of values
                                    // compared to the given definition list
                                    if (n % definitions.getParameters().size() == 0) {
                                        valueSetNumber++;
                                    }

                                    AggregationParameterValue value =
                                            values.getValues().get(n);
                                    int paramIdx =
                                            n % definitions.getParameters().size();
                                    Long parameterId =
                                            definitions.getParameters().get(paramIdx);

                                    TimestampedParameterValue paramValue =
                                            new TimestampedParameterValue(
                                            value.getValue(),
                                            new Time(valueSetTimestamp
                                            + (long) (valueSetNumber * intervalTime
                                            * 1000L)));

                                    Identifier parameterName = parametersAdapter.getNamesMap().get(domainKey).get(parameterId);
                                    if (allParameters.get(domainKey)
                                            .containsKey(parameterName)) {
                                        allParameters.get(domainKey)
                                                .get(parameterName).add(paramValue);
                                    } else {
                                        List<TimestampedParameterValue> list =
                                                new ArrayList<>();
                                        list.add(paramValue);
                                        allParameters.get(domainKey)
                                                .put(parameterName, list);
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

                    Map<IdentifierList, Map<Identifier, List<TimestampedParameterValue>>>
                            parameters = new HashMap<>();
                    if (!parameterNames.isEmpty()) {
                        for (String name : parameterNames) {
                            for (IdentifierList domainKey : allParameters.keySet()) {
                                List<TimestampedParameterValue> values =
                                        allParameters.get(domainKey)
                                        .get(new Identifier(name));
                                if (values == null) {
                                    continue;
                                }
                                values.sort(Comparator.comparingLong(
                                        TimestampedParameterValue::getTimestamp));
                                if (!parameters.containsKey(domainKey)) {
                                    parameters.put(domainKey, new HashMap<>());
                                }
                                parameters.get(domainKey).put(new Identifier(name), values);
                            }
                        }
                    } else {
                        parameters = allParameters;
                        for (IdentifierList domainKey : parameters.keySet()) {
                            for (Map.Entry<Identifier, List<TimestampedParameterValue>>
                                    entry : parameters.get(domainKey).entrySet()) {
                                entry.getValue().sort(Comparator.comparingLong(
                                        TimestampedParameterValue::getTimestamp));
                            }
                        }
                    }

                    if (json) {
                        Gson gson = new GsonBuilder()
                                .setPrettyPrinting().disableHtmlEscaping().create();
                        gson.toJson(parameters, writer);
                    } else {
                        for (IdentifierList domainKey : parameters.keySet()) {
                            writer.write(
                                    "Domain: "
                                    + HelperDomain.domain2domainId(domainKey) + "\n");
                            List<String> keys = parameters.get(domainKey).keySet()
                                    .stream().map(Identifier::getValue)
                                    .sorted().collect(Collectors.toList());
                            for (String parameter : keys) {
                                for (TimestampedParameterValue value
                                        : parameters.get(domainKey)
                                        .get(new Identifier(parameter))) {
                                    String line = parameter + "\t"
                                            + value.getTimestamp()
                                            + "\t" + value.getParameterValue() + "\n";
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

    public static class ListParameters extends BaseCommand {

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
            IdentifierList domain =
                    domainId == null ? null : HelperDomain.domainId2domain(domainId);

            ArchiveQuery archiveQuery = new ArchiveQuery(
                    domain, null, 0L, null, null, null, null, null);

            ArchiveToParametersAdapter adapter = new ArchiveToParametersAdapter();
            queryArchive(
                    ParameterServiceInfo.PARAMETERDEFINITION_OBJECT_TYPE,
                    archiveQuery, adapter, adapter);

            // Display list of NMF apps that have parameters
            Map<IdentifierList, List<Identifier>> parameters =
                    adapter.getParameterNames();
            if (parameters.size() <= 0) {
                System.out.println(
                        "\nNo parameter found in the provided archive: "
                        + (databaseFile == null ? providerURI : databaseFile));
            } else {
                System.out.println("\nFound the following parameters: ");
                for (Map.Entry<IdentifierList, List<Identifier>> entry
                        : parameters.entrySet()) {
                    System.out.println("Domain: " + entry.getKey());
                    for (Identifier parameter : entry.getValue()) {
                        System.out.println("  - " + parameter);
                    }
                }
                System.out.println();
            }
        }
    }

    public static void enableOrDisableParameterGeneration(
            ParameterStub parameterService,
            List<String> parameters, boolean enable) {
        IdentifierList request = new IdentifierList();
        if (parameters == null || parameters.isEmpty()) {
            request.add(new Identifier("*"));
        } else {
            for (String name : parameters) {
                request.add(new Identifier(name));
            }
        }

        try {
            LongList ids = parameterService.listDefinition(request);
            parameterService.enableReporting(enable, ids);
            System.out.println((enable ? "Enable " : "Disable ") + "successful.");
        } catch (MALInteractionException e) {
            MOErrorException error = e.getStandardError();
            if (error.getErrorNumber().equals(MALHelper.UNKNOWN_ERROR_NUMBER)) {
                System.out.println(
                        "Provided parameters don't exist in the provider:");
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

    public static class SetParameter extends BaseCommand {

        enum ParameterType {
            Integer, String, Long, Float, Double, Boolean
        }

        @Override
        public void run(Args args) {
            parseBaseOptions(args);
            List<String> positionals = args.positionals();
            if (positionals.size() < 3) {
                System.out.println("Usage: parameter set <paramName> <paramType> <paramValue>");
                return;
            }
            String parameterName  = positionals.get(0);
            String typeStr        = positionals.get(1);
            String parameterValue = positionals.get(2);

            ParameterType parameterType;
            try {
                parameterType = ParameterType.valueOf(typeStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Unknown parameter type: " + typeStr
                        + ". Choose from: Integer, String, Long, Float, Double, Boolean");
                return;
            }

            if (!super.initRemoteConsumer()) {
                return;
            }

            if (consumer.getMCServices().getParameterService() == null) {
                System.out.println(
                        "Parameter service not available for this provider");
                return;
            }

            try {
                Union parameter = convertParameter(parameterType, parameterValue);
                if (parameter != null) {
                    consumer.setParameter(parameterName, parameter);
                } else {
                    System.out.println(
                            "The parameter could not be converted to the correct type and set");
                }
            } catch (Exception e) {
                System.out.println("There was an unexpected error");
                System.out.println(e.getMessage());
            }
        }

        private Union convertParameter(ParameterType type, String parameterValue) {
            try {
                if (type.equals(ParameterType.Integer)) {
                    return new Union(Integer.parseInt(parameterValue));
                }
                if (type.equals(ParameterType.Long)) {
                    return new Union(Long.parseLong(parameterValue));
                }
                if (type.equals(ParameterType.Double)) {
                    return new Union(Double.parseDouble(parameterValue));
                }
                if (type.equals(ParameterType.Float)) {
                    return new Union(Float.parseFloat(parameterValue));
                }
            } catch (NumberFormatException e) {
                System.out.println("Number format is wrong!");
                return null;
            }
            if (type.equals(ParameterType.Boolean)) {
                return new Union(Boolean.parseBoolean(parameterValue));
            }
            return new Union(parameterValue);
        }
    }

}
