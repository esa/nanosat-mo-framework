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
package esa.mo.nmf.comarchivetool.commands.parameters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.nmf.NMFConsumer;
import esa.mo.nmf.comarchivetool.TimestampedAggregationValue;
import esa.mo.nmf.comarchivetool.TimestampedParameterValue;
import esa.mo.nmf.comarchivetool.adapters.ArchiveToAggreationsAdapter;
import esa.mo.nmf.comarchivetool.adapters.ArchiveToParametersAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.common.CommonHelper;
import org.ccsds.moims.mo.common.directory.DirectoryHelper;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.aggregation.AggregationHelper;
import org.ccsds.moims.mo.mc.aggregation.structures.*;
import org.ccsds.moims.mo.mc.parameter.ParameterHelper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static esa.mo.nmf.comarchivetool.ArchiveBrowserHelper.*;

/**
 * Parameter commands implementations
 *
 * @author Marcel Mikołajko
 */
public class ParametersCommandsImplementations {

    private static final Logger LOGGER = Logger.getLogger(ParametersCommandsImplementations.class.getName());
    private static final long MILLIS_IN_SECOND = 1000L;

    /**
     * Lists parameters for the specified NMF app.
     *
     * @param databaseFile Local SQLite database file
     * @param providerURI The URI of the remote COM archive provider
     * @param appName Name of the NMF app we want parameters for
     * @param domainId Restricts the dump to objects in a specific domain ID
     */
    public static void listParameters(String databaseFile, String providerURI, String domainId, String appName) {
        LocalOrRemoteConsumer consumers = createConsumer(providerURI, databaseFile, appName);
        ArchiveConsumerServiceImpl localConsumer = consumers.getLocalConsumer();
        NMFConsumer remoteConsumer = consumers.getRemoteConsumer();

        IdentifierList domain = domainId == null ? null : HelperMisc.domainId2domain(domainId);

        ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        ArchiveQuery archiveQuery = new ArchiveQuery(domain, null, null,
                                                     0L, null, null,
                                                     null, null, null);
        archiveQueryList.add(archiveQuery);

        ArchiveToParametersAdapter adapter = new ArchiveToParametersAdapter();
        queryArchive(ParameterHelper.PARAMETERIDENTITY_OBJECT_TYPE, archiveQueryList, adapter, adapter,
                     remoteConsumer == null ? localConsumer : remoteConsumer.getCOMServices().getArchiveService());

        // Display list of NMF apps that have parameters
        Map<IdentifierList, List<Identifier>> parameters = adapter.getParameterIdentities();
        if (parameters.size() <= 0) {
            System.out.println("\nNo parameter found in the provided archive: " + (databaseFile == null ? providerURI : databaseFile));
        } else {
            System.out.println("\nFound the following parameters: ");
            for(Map.Entry<IdentifierList, List<Identifier>> entry : parameters.entrySet()) {
                System.out.println("Domain: " + entry.getKey());
                for (Identifier parameter : entry.getValue()) {
                    System.out.println("  - " + parameter);
                }
            }
            System.out.println();
        }
        closeConsumer(consumers);
    }

    /**
     * Dumps parameters of an NMF app to a file
     *
     * @param databaseFile Local SQLite database file
     * @param providerURI The URI of the remote COM archive provider
     * @param domainId Restricts the dump to objects in a specific domain ID
     * @param startTime Restricts the dump to objects created after the given time
     * @param endTime Restricts the dump to objects created before the given time. If this option is
     *        provided without the -s option, returns the single object that has the closest time
     *        stamp to, but not greater than endTime.
     * @param file Target file
     * @param appName Name of the NMF app we want parameters for
     * @param parameterNames List of parameters to get
     * @param json If true output will be in json format
     */
    public static void getParameters(String databaseFile, String providerURI, String domainId,
                                     String startTime, String endTime, String file, String appName,
                                     List<String> parameterNames, boolean json) {
        LocalOrRemoteConsumer consumers = createConsumer(providerURI, databaseFile, appName);
        ArchiveConsumerServiceImpl localConsumer = consumers.getLocalConsumer();
        NMFConsumer remoteConsumer = consumers.getRemoteConsumer();

        // prepare domain, time and object id filters
        ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        FineTime startTimeF = startTime == null ? null : HelperTime.readableString2FineTime(startTime);
        FineTime endTimeF = endTime == null ? null : HelperTime.readableString2FineTime(endTime);
        IdentifierList domain = domainId == null ? null : HelperMisc.domainId2domain(domainId);

        ArchiveQuery archiveQuery = new ArchiveQuery(domain, null, null, 0L, null,
                                                     startTimeF, endTimeF, null, null);
        archiveQueryList.add(archiveQuery);

        ArchiveToParametersAdapter parametersAdapter = new ArchiveToParametersAdapter();
        ObjectType parameterObjectType = new ObjectType(MCHelper.MC_AREA_NUMBER,
                                                        ParameterHelper.PARAMETER_SERVICE_NUMBER,
                                                        MCHelper.MC_AREA_VERSION,
                                                        new UShort(0));
        queryArchive(parameterObjectType, archiveQueryList, parametersAdapter, parametersAdapter,
                     remoteConsumer == null ? localConsumer : remoteConsumer.getCOMServices().getArchiveService());

        ArchiveToAggreationsAdapter aggregationsAdapter = new ArchiveToAggreationsAdapter();
        ObjectType aggregationObjectType = new ObjectType(MCHelper.MC_AREA_NUMBER,
                                                          AggregationHelper.AGGREGATION_SERVICE_NUMBER,
                                                          MCHelper.MC_AREA_VERSION,
                                                          new UShort(0));
        queryArchive(aggregationObjectType, archiveQueryList, aggregationsAdapter, aggregationsAdapter,
                     remoteConsumer == null ? localConsumer : remoteConsumer.getCOMServices().getArchiveService());


        Map<IdentifierList, Map<Identifier, List<TimestampedParameterValue>>> allParameters = parametersAdapter.getParameterValues();

        // Display list of aggregations
        Map<IdentifierList, Map<Long, List<TimestampedAggregationValue>>> aggregationValuesMap = aggregationsAdapter.getAggregationValues();
        if(aggregationValuesMap != null) {
            //Make the parameter map
            for(IdentifierList domainKey : aggregationValuesMap.keySet()) {
                for(Map.Entry<Long, List<TimestampedAggregationValue>> entry : aggregationValuesMap.get(domainKey).entrySet()) {
                    Long definitionId = entry.getKey();
                    AggregationDefinitionDetails definition = aggregationsAdapter.getAggregationDefinitions().get(domainKey).get(definitionId);

                    for(TimestampedAggregationValue aggregationValue : entry.getValue()) {
                        for(int i= 0; i < aggregationValue.getAggregationValue().getParameterSetValues().size(); i++) {
                            AggregationSetValue values = aggregationValue.getAggregationValue().getParameterSetValues().get(i);
                            AggregationParameterSet definitions = definition.getParameterSets().get(i);

                            int valueSetNumber = 0;
                            double deltaTime = values.getDeltaTime() != null ? values.getDeltaTime().getValue() : 0;
                            double intervalTime = values.getIntervalTime() != null ? values.getIntervalTime().getValue() : 0;
                            long valueSetTimestamp = aggregationValue.getTimestamp().getValue() + (long)( deltaTime * MILLIS_IN_SECOND);


                            for(int n = 0; n < values.getValues().size(); n++) {
                                // Check if we are starting a new set of values compared to the given definition list
                                if(n % definitions.getParameters().size() == 0) {
                                    valueSetNumber++;
                                }

                                AggregationParameterValue value = values.getValues().get(n);
                                Long parameterId = definitions.getParameters().get(n % definitions.getParameters().size());

                                TimestampedParameterValue paramValue = new TimestampedParameterValue(value.getValue(),
                                                                                                     new FineTime(valueSetTimestamp + (long)(valueSetNumber * intervalTime * MILLIS_IN_SECOND)));

                                Identifier parameterName = parametersAdapter.getIdentitiesMap().get(domainKey).get(parameterId);
                                if(allParameters.get(domainKey).containsKey(parameterName)) {
                                    allParameters.get(domainKey).get(parameterName).add(paramValue);
                                }
                                else {
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
            if(!allParameters.isEmpty()) {
                if(json && !file.endsWith(".json")) {
                    file = file + ".json";
                }

                FileWriter writer = new FileWriter(file);

                Map<IdentifierList, Map<Identifier, List<TimestampedParameterValue>>> parameters = new HashMap<>();
                if(parameterNames != null && !parameterNames.isEmpty()) {
                    for(String name: parameterNames) {
                        for(IdentifierList domainKey : allParameters.keySet()) {
                            List<TimestampedParameterValue> values = allParameters.get(domainKey).get(new Identifier(name));
                            if(values == null) {
                                continue;
                            }
                            values.sort(Comparator.comparingLong(TimestampedParameterValue::getTimestamp));
                            if(!parameters.containsKey(domainKey)) {
                                parameters.put(domainKey, new HashMap<>());
                            }
                            parameters.get(domainKey).put(new Identifier(name), values);
                        }
                    }
                } else {
                    parameters = allParameters;
                    for(IdentifierList domainKey : parameters.keySet()) {
                        for(Map.Entry<Identifier, List<TimestampedParameterValue>> entry : parameters.get(domainKey).entrySet()) {
                            entry.getValue().sort(Comparator.comparingLong(TimestampedParameterValue::getTimestamp));
                        }
                    }
                }

                if(json) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
                    gson.toJson(parameters, writer);
                } else {
                    for(IdentifierList domainKey : parameters.keySet()) {
                        writer.write("Domain: " + HelperMisc.domain2domainId(domainKey) + "\n");
                        List<String> keys = parameters.get(domainKey).keySet().stream().map(Identifier::getValue).sorted().collect(Collectors.toList());
                        for(String parameter : keys) {
                            for(TimestampedParameterValue value : parameters.get(domainKey).get(new Identifier(parameter))) {
                                String line = parameter + "\t" + value.getTimestamp() + "\t" + value.getParameterValue() + "\n";
                                writer.write(line);
                            }
                        }
                    }
                }
                writer.close();
                System.out.println("\nParameters successfully dumped to file: " + file + "\n");
            }
            else {
                System.out.println("\nNo parameters found\n");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error when writing to file", e);
        }

        closeConsumer(consumers);
    }
}
