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
package esa.mo.nmf.com_archive_browser.commands.parameters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.nmf.NMFConsumer;
import esa.mo.nmf.com_archive_browser.adapters.ArchiveToParametersAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.parameter.ParameterHelper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static esa.mo.nmf.com_archive_browser.ArchiveBrowserHelper.*;

/**
 * @author marcel.mikolajko
 */
public class ParametersCommandsImplementations {

    private static final Logger LOGGER = Logger.getLogger(ParametersCommandsImplementations.class.getName());

    /**
     * Lists parameters for the specified NMF app.
     *
     * @param databaseFile Local SQLite database file
     * @param providerURI The URI of the remote COM archive provider
     * @param appName Name of the NMF app we want the parameters for
     */
    public static void listParameters(String databaseFile, String providerURI, String appName) {
        LocalOrRemoteConsumer consumers = createConsumer(providerURI, databaseFile);
        ArchiveConsumerServiceImpl localConsumer = consumers.getLocalConsumer();
        NMFConsumer remoteConsumer = consumers.getRemoteConsumer();

        ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        ArchiveQuery archiveQuery = new ArchiveQuery(null, null, null,
                                                     0L, null, null,
                                                     null, null, null);
        archiveQueryList.add(archiveQuery);

        ArchiveToParametersAdapter adapter = new ArchiveToParametersAdapter();
        // execute query
        queryArchive(ParameterHelper.PARAMETERIDENTITY_OBJECT_TYPE, archiveQueryList, adapter, adapter, remoteConsumer == null ? localConsumer : remoteConsumer.getCOMServices().getArchiveService());

        // Display list of NMF apps that have parameters
        List<Identifier> parameters = adapter.getParameterIdentities();
        if (parameters.size() <= 0) {
            System.out.println("No parameters found in the provided archive: " + appName);
        } else {
            System.out.println("Found the following parameters: ");
            for (Identifier parameter : parameters) {
                System.out.println("\t - " + parameter);
            }
        }
        closeConsumer(consumers);
    }

    /**
     * Dumps parameters of an NMF app to a file
     *
     * @param databaseFile Local SQLite database file
     * @param providerURI The URI of the remote COM archive provider
     * @param appName Name of the NMF app we want the logs for
     * @param startTime Restricts the dump to objects created after the given time
     * @param endTime Restricts the dump to objects created before the given time. If this option is
     *        provided without the -s option, returns the single object that has the closest time
     *        stamp to, but not greater than endTime.
     * @param file target file
     */
    public static void getParameters(String databaseFile, String providerURI, String appName,
                                     String startTime, String endTime, String file,
                                     List<String> parameterNames, boolean json) {
        // Query all objects from SoftwareManagement area and CommandExecutor service,
        // filtering for StandardOutput and StandardError events is done in the query adapter
        ObjectType objectsTypes = new ObjectType(MCHelper.MC_AREA_NUMBER,
                                                 ParameterHelper.PARAMETER_SERVICE_NUMBER,
                                                 MCHelper.MC_AREA_VERSION,
                                                 new UShort(0));

        LocalOrRemoteConsumer consumers = createConsumer(providerURI, databaseFile);
        ArchiveConsumerServiceImpl localConsumer = consumers.getLocalConsumer();
        NMFConsumer remoteConsumer = consumers.getRemoteConsumer();

        // prepare domain, time and object id filters
        ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        FineTime startTimeF = startTime == null ? null : HelperTime.readableString2FineTime(startTime);
        FineTime endTimeF = endTime == null ? null : HelperTime.readableString2FineTime(endTime);
        ArchiveQuery archiveQuery = new ArchiveQuery(null, null, null, 0L, null,
                                                     startTimeF, endTimeF, null, null);
        archiveQueryList.add(archiveQuery);

        // execute query
        ArchiveToParametersAdapter adapter = new ArchiveToParametersAdapter();
        queryArchive(objectsTypes, archiveQueryList, adapter, adapter, remoteConsumer == null ? localConsumer : remoteConsumer.getCOMServices().getArchiveService());

        try {
            Map<Identifier, List<ArchiveToParametersAdapter.TimestampedParameterValue>> allParameters = adapter.getParameterValues();

            if(json && !file.endsWith(".json")) {
                file = file + ".json";
            }

            FileWriter writer = new FileWriter(file);

            Map<Identifier, List<ArchiveToParametersAdapter.TimestampedParameterValue>> parameters = new HashMap<>();
            if(parameterNames != null && !parameterNames.isEmpty()) {
                for(String name: parameterNames) {
                    List<ArchiveToParametersAdapter.TimestampedParameterValue> values = allParameters.get(new Identifier(name));
                    if(values == null) {
                        LOGGER.log(Level.WARNING, "Parameter " + name + " not found. It will be ignored.");
                        continue;
                    }
                    parameters.put(new Identifier(name), values);
                }
            } else {
                parameters = allParameters;
            }

            if(json) {
                Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
                gson.toJson(parameters, writer);
            } else {
                List<String> keys = parameters.keySet().stream().map(Identifier::getValue).sorted().collect(Collectors.toList());
                for(String parameter : keys) {
                    for(ArchiveToParametersAdapter.TimestampedParameterValue value : parameters.get(new Identifier(parameter))) {
                        String line = parameter + "\t" + value.getTimestamp() + "\t" + value.getParameterValue() + "\n";
                        writer.write(line);
                    }
                }
            }
            writer.close();
            LOGGER.log(Level.INFO, "Parameters successfully dumped to file: " + file);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error when writing to file", e);
        }

        closeConsumer(consumers);
    }
}
