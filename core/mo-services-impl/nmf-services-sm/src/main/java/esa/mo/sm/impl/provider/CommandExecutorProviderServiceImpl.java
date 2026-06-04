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
package esa.mo.sm.impl.provider;

import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.com.impl.provider.EventProviderServiceImpl;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.misc.OSValidator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.exec.environment.EnvironmentUtils;
import org.ccsds.moims.mo.com.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionProvider;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.sm.commandexecutor.CommandExecutorHelper;
import org.ccsds.moims.mo.sm.commandexecutor.CommandExecutorServiceInfo;
import org.ccsds.moims.mo.sm.commandexecutor.provider.CommandExecutorInheritanceSkeleton;
import org.ccsds.moims.mo.sm.commandexecutor.provider.MonitorOutputPublisher;
import org.ccsds.moims.mo.sm.structures.Command;
import org.ccsds.moims.mo.sm.structures.CommandOutputType;

/**
 * Command Executor service Provider.
 */
public class CommandExecutorProviderServiceImpl extends CommandExecutorInheritanceSkeleton {

    private static final Logger LOGGER = Logger.getLogger(
            CommandExecutorProviderServiceImpl.class.getName());
    private final ConnectionProvider connection = new ConnectionProvider();
    private MALProvider commandExecutorServiceProvider;
    private boolean initialiased = false;
    private EventProviderServiceImpl eventService;
    private ArchiveProviderServiceImpl archiveService;
    private MonitorOutputPublisher publisher;
    private final OSValidator osValidator = new OSValidator();
    private final Map<Long, Command> cachedCommandDetails = new HashMap<>();

    /**
     * Initializes the service provider.
     *
     * @param comServices The COM services.
     * @throws MALException On initialization error.
     */
    public synchronized void init(final COMServicesProvider comServices) throws MALException {
        long timestamp = System.currentTimeMillis();
        archiveService = comServices.getArchiveService();
        if (archiveService == null) {
            throw new MALException("Cannot access the COM Archive Service.");
        }
        eventService = comServices.getEventService();
        if (eventService == null) {
            throw new MALException("Cannot access the COM Event Service.");
        }
        // Shut down old service transport
        if (null != commandExecutorServiceProvider) {
            connection.closeAll();
        }

        commandExecutorServiceProvider = connection.startService(CommandExecutorHelper.COMMANDEXECUTOR_SERVICE, true, this);

        publisher = createMonitorOutputPublisher(
                ConfigurationProviderSingleton.getDomain(),
                null,
                SessionType.LIVE,
                ConfigurationProviderSingleton.getSourceSessionName(),
                QoSLevel.BESTEFFORT,
                null,
                new UInteger(0));
        try {
            publisher.registerWithDefaultKeys(new PublishInteractionListener());
        } catch (MALInteractionException ex) {
            LOGGER.log(Level.SEVERE, "Could not register monitorOutput publisher", ex);
        }

        initialiased = true;
        timestamp = System.currentTimeMillis() - timestamp;
        LOGGER.info("Command Executor service: READY! (" + timestamp + " ms)");
    }

    protected String[] assembleCommand(final String command) {
        ArrayList<String> ret = new ArrayList<>();
        if (osValidator.isWindows()) {
            ret.add("cmd");
            ret.add("/c");
            ret.add(command);
        } else {
            ret.add("/bin/sh");
            ret.add("-c");
            ret.add(command);
        }
        return ret.toArray(new String[0]);
    }

    @Override
    public Long runCommand(Command command, MALInteraction interaction)
            throws MALInteractionException, MALException {
        Long storedCommandObject;
        if (command == null) {
            throw new MALException("Received null Command.");
        }

        // Source could be mapped to an OperationActivity associated with this transaction, but for now
        // we don't need such fine tracking...
        final ArchiveDetailsList archDetails = HelperArchive.generateArchiveDetailsList(null,
                null, connection.getPrimaryConnectionDetails().getProviderURI());
        final HeterogeneousList objBodies = new HeterogeneousList();
        objBodies.add(command);
        LongList objIds = archiveService.store(true, CommandExecutorServiceInfo.COMMAND_OBJECT_TYPE,
                connection.getPrimaryConnectionDetails().getDomain(), archDetails, objBodies, null);

        if (objIds.size() == 1) {
            storedCommandObject = objIds.get(0);
        } else {
            throw new MALException("Unexpected return from the archive store.");
        }

        String[] shellCommand = assembleCommand(command.getCommand());
        final ProcessBuilder pb = new ProcessBuilder(shellCommand);
        Map<String, String> env = pb.environment();
        // Reuse the environment
        // env.clear();
        File workingDir = pb.directory();
        if (workingDir == null) {
            workingDir = Paths.get("").toFile();
        }
        LOGGER.log(Level.INFO, "Running ''{0}'' in dir: {1}, and env: {2}", new Object[]{
            Arrays.toString(shellCommand), workingDir.getAbsolutePath(),
            Arrays.toString(EnvironmentUtils.toStrings(env))});
        final Process proc;
        try {
            proc = pb.start();
            ProcessExecutionHandler handler = new ProcessExecutionHandler(new CallbacksImpl(),
                    storedCommandObject);
            handler.monitorProcess(proc);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new MALException("Cannot start the process!", ex);
        }
        long pid;
        try {
            pid = ProcessExecutionHandler.getProcessPid(proc);
        } catch (IOException ex) {
            pid = -1;
        }

        Command newDetails = new Command(command.getCommand(), pid, command.getExitCode());
        updateCommandDetails(storedCommandObject, newDetails);
        return storedCommandObject;
    }

    private void publishOutput(final Long commandId, final CommandOutputType outputType,
            final String data, final Integer exitCode) {
        // Archive the output chunk/exit event via the COM Event service (for historical queries).
        IdentifierList domain = connection.getPrimaryConnectionDetails().getDomain();
        ObjectKey source = new ObjectKey(CommandExecutorServiceInfo.COMMAND_OBJECT_TYPE, domain, commandId);
        ObjectType archiveObjType = (outputType == CommandOutputType.STDOUT)
                ? CommandExecutorServiceInfo.STANDARDOUTPUT_OBJECT_TYPE
                : (outputType == CommandOutputType.STDERR)
                ? CommandExecutorServiceInfo.STANDARDERROR_OBJECT_TYPE
                : CommandExecutorServiceInfo.EXECUTIONFINISHED_OBJECT_TYPE;
        Element archiveBody = (outputType == CommandOutputType.FINISHED)
                ? new Union(exitCode) : new Union(data);
        eventService.generateAndStoreEvent(archiveObjType, domain, archiveBody, null,
                source, connection.getPrimaryConnectionDetails().getProviderURI(), null);

        // Publish live notification via monitorOutput PUBSUB.
        if (publisher != null) {
            try {
                AttributeList keyValues = new AttributeList();
                keyValues.add(new Union(commandId));
                UpdateHeader updateHeader = new UpdateHeader(
                        new Identifier(connection.getPrimaryConnectionDetails().getProviderURI().getValue()),
                        connection.getPrimaryConnectionDetails().getDomain(),
                        keyValues.getAsNullableAttributeList());
                publisher.publish(updateHeader, outputType, data, exitCode);
            } catch (MALException | MALInteractionException ex) {
                LOGGER.log(Level.SEVERE, "Could not publish monitorOutput notification", ex);
            }
        }
    }

    private void commandExitEvent(final Long objId, final int exitCode) {
        publishOutput(objId, CommandOutputType.FINISHED, null, exitCode);
        try {
            Command command = getCommandDetails(objId);
            Command newDetails = new Command(command.getCommand(), command.getPid(), exitCode);
            updateCommandDetails(objId, newDetails);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Cannot update COM Command object", ex);
        }
    }

    public static final class PublishInteractionListener
            implements org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener {

        @Override
        public void publishDeregisterAckReceived(
                org.ccsds.moims.mo.mal.transport.MALMessageHeader header,
                java.util.Map qosProperties) throws MALException {
            LOGGER.fine("PublishInteractionListener::publishDeregisterAckReceived");
        }

        @Override
        public void publishErrorReceived(
                org.ccsds.moims.mo.mal.transport.MALMessageHeader header,
                org.ccsds.moims.mo.mal.transport.MALErrorBody body,
                java.util.Map qosProperties) throws MALException {
            LOGGER.warning("PublishInteractionListener::publishErrorReceived");
        }

        @Override
        public void publishRegisterAckReceived(
                org.ccsds.moims.mo.mal.transport.MALMessageHeader header,
                java.util.Map qosProperties) throws MALException {
            LOGGER.fine("PublishInteractionListener::publishRegisterAckReceived");
        }

        @Override
        public void publishRegisterErrorReceived(
                org.ccsds.moims.mo.mal.transport.MALMessageHeader header,
                org.ccsds.moims.mo.mal.transport.MALErrorBody body,
                java.util.Map qosProperties) throws MALException {
            LOGGER.warning("PublishInteractionListener::publishRegisterErrorReceived");
        }
    }

    private Command getCommandDetails(Long objId) throws IOException {
        if (cachedCommandDetails.containsKey(objId)) {
            return cachedCommandDetails.get(objId);
        } else {
            Element retrievedObject = HelperArchive.getObjectBodyFromArchive(archiveService,
                    CommandExecutorServiceInfo.COMMAND_OBJECT_TYPE, connection.getPrimaryConnectionDetails().getDomain(), objId);
            if (retrievedObject == null) {
                throw new IOException("Could not retrieve Command object for objId: " + objId);
            }
            Command ret = (Command) retrievedObject;
            cachedCommandDetails.put(objId, ret);
            return ret;
        }
    }

    private void updateCommandDetails(Long objId, Command command) {
        cachedCommandDetails.put(objId, command);
        final ArchiveDetailsList archDetails = HelperArchive.generateArchiveDetailsList(
                null, null,
                connection.getPrimaryConnectionDetails().getProviderURI(),
                objId);
        final HeterogeneousList objBodies = new HeterogeneousList();
        objBodies.add(command);
        try {
            archiveService.update(CommandExecutorServiceInfo.COMMAND_OBJECT_TYPE,
                    connection.getPrimaryConnectionDetails().getDomain(),
                    archDetails, objBodies, null);
        } catch (MALException | MALInteractionException ex) {
            Logger.getLogger(CommandExecutorProviderServiceImpl.class.getName()).log(Level.SEVERE,
                    "Could not update COM Command object", ex);
        }
    }

    private class CallbacksImpl implements ProcessExecutionHandler.Callbacks {

        @Override
        public void flushStdout(Long objId, String data) {
            publishOutput(objId, CommandOutputType.STDOUT, data, null);
        }

        @Override
        public void flushStderr(Long objId, String data) {
            publishOutput(objId, CommandOutputType.STDERR, data, null);
        }

        @Override
        public void processStopped(Long objId, int exitCode) {
            commandExitEvent(objId, exitCode);
        }
    }
}
