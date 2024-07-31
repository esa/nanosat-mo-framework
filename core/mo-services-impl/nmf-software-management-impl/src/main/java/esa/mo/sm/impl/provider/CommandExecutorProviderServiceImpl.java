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
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.connections.ConnectionProvider;
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
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.IntegerList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.softwaremanagement.SoftwareManagementHelper;
import org.ccsds.moims.mo.softwaremanagement.commandexecutor.CommandExecutorHelper;
import org.ccsds.moims.mo.softwaremanagement.commandexecutor.provider.CommandExecutorInheritanceSkeleton;
import org.ccsds.moims.mo.softwaremanagement.commandexecutor.structures.CommandDetails;
import org.ccsds.moims.mo.softwaremanagement.commandexecutor.structures.CommandDetailsList;

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
    private final OSValidator osValidator = new OSValidator();
    private final Map<Long, CommandDetails> cachedCommandDetails = new HashMap<>();

    /**
     * Initializes the service provider
     *
     * @param comServices
     * @throws MALException On initialization error.
     */
    public synchronized void init(final COMServicesProvider comServices) throws MALException {
        long timestamp = System.currentTimeMillis();
        
        if (!initialiased) {
            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NAME,
                    SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_VERSION) == null) {
                SoftwareManagementHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NAME,
                    SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_VERSION)
                    .getServiceByName(CommandExecutorHelper.COMMANDEXECUTOR_SERVICE_NAME) == null) {
                CommandExecutorHelper.init(MALContextFactory.getElementFactoryRegistry());
            }
        }
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

        commandExecutorServiceProvider = connection.startService(
                CommandExecutorHelper.COMMANDEXECUTOR_SERVICE_NAME.toString(),
                CommandExecutorHelper.COMMANDEXECUTOR_SERVICE, this);
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
    public Long runCommand(CommandDetails command, MALInteraction interaction)
            throws MALInteractionException, MALException {
        Long storedCommandObject;
        if (command == null) {
            throw new MALException("Received null CommandDetails.");
        }

        // Source could be mapped to an OperationActivity associated with this transaction, but for now
        // we don't need such fine tracking...
        final ArchiveDetailsList archDetails = HelperArchive.generateArchiveDetailsList(
                null, null, connection.getPrimaryConnectionDetails().getProviderURI());
        final CommandDetailsList objBodies = new CommandDetailsList(1);
        objBodies.add(command);
        LongList objIds = archiveService.store(
                true,
                CommandExecutorHelper.COMMAND_OBJECT_TYPE,
                connection.getPrimaryConnectionDetails().getDomain(),
                archDetails,
                objBodies,
                null);

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
        command.setPid(pid);
        updateCommandDetails(storedCommandObject, command);
        return storedCommandObject;
    }

    private void commandOutputEvent(final Long objId, final String outputText,
            final ObjectType objType) {
        IdentifierList domain = connection.getPrimaryConnectionDetails().getDomain();
        URI sourceURI = connection.getPrimaryConnectionDetails().getProviderURI();
        ObjectId source = new ObjectId(CommandExecutorHelper.COMMAND_OBJECT_TYPE, new ObjectKey(domain,
                objId));
        Element eventBody = new Union(outputText);
        StringList eventBodyList = new StringList(1);
        eventBodyList.add(outputText);
        final Long eventObjId = eventService.generateAndStoreEvent(objType, domain, eventBody, null,
                source, connection.getPrimaryConnectionDetails().getProviderURI(), null);
        if (eventObjId != null) {
            try {
                eventService.publishEvent(sourceURI, eventObjId, objType, null, source, eventBodyList);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Could not publish command output event", ex);
            }
        } else {
            LOGGER.log(Level.SEVERE, "generateAndStoreEvent returned null object ID");
        }
    }

    private void commandExitEvent(final Long objId, final int exitCode) {
        IdentifierList domain = connection.getPrimaryConnectionDetails().getDomain();
        URI sourceURI = connection.getPrimaryConnectionDetails().getProviderURI();
        ObjectId source = new ObjectId(CommandExecutorHelper.COMMAND_OBJECT_TYPE, new ObjectKey(domain,
                objId));
        Element eventBody = new Union(exitCode);
        IntegerList eventBodyList = new IntegerList(1);
        eventBodyList.add(exitCode);
        final Long eventObjId = eventService.generateAndStoreEvent(
                CommandExecutorHelper.EXECUTIONFINISHED_OBJECT_TYPE, domain, eventBody, null,
                source, connection.getPrimaryConnectionDetails().getProviderURI(), null);
        if (eventObjId != null) {
            try {
                eventService.publishEvent(sourceURI, eventObjId,
                        CommandExecutorHelper.EXECUTIONFINISHED_OBJECT_TYPE, null, source, eventBodyList);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Could not publish command exit event", ex);
            }
        } else {
            LOGGER.log(Level.SEVERE, "generateAndStoreEvent returned null object ID");
        }
        try {
            CommandDetails command = getCommandDetails(objId);
            command.setExitCode(exitCode);
            updateCommandDetails(objId, command);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Cannot update COM Command object", ex);
        }
    }

    private CommandDetails getCommandDetails(Long objId) throws IOException {
        if (cachedCommandDetails.containsKey(objId)) {
            return cachedCommandDetails.get(objId);
        } else {
            Element retrievedObject = HelperArchive.getObjectBodyFromArchive(
                    archiveService, CommandExecutorHelper.COMMAND_OBJECT_TYPE,
                    connection.getPrimaryConnectionDetails().getDomain(), objId);
            if (retrievedObject == null) {
                throw new IOException("Could not retrieve Command object for objId: " + objId);
            }
            CommandDetails ret = (CommandDetails) retrievedObject;
            cachedCommandDetails.put(objId, ret);
            return ret;
        }
    }

    private void updateCommandDetails(Long objId, CommandDetails command) {
        cachedCommandDetails.put(objId, command);
        final ArchiveDetailsList archDetails = HelperArchive.generateArchiveDetailsList(null, null,
                ConfigurationProviderSingleton.getNetwork(),
                connection.getPrimaryConnectionDetails().getProviderURI(), objId);
        final CommandDetailsList objBodies = new CommandDetailsList(1);
        objBodies.add(command);
        try {
            archiveService.update(
                    CommandExecutorHelper.COMMAND_OBJECT_TYPE,
                    connection.getPrimaryConnectionDetails().getDomain(), archDetails, objBodies, null);
        } catch (MALException | MALInteractionException ex) {
            Logger.getLogger(CommandExecutorProviderServiceImpl.class.getName()).log(Level.SEVERE,
                    "Could not update COM Command object", ex);
        }
    }

    private class CallbacksImpl implements ProcessExecutionHandler.Callbacks {

        @Override
        public void flushStdout(Long objId, String data) {
            commandOutputEvent(objId, data, CommandExecutorHelper.STANDARDOUTPUT_OBJECT_TYPE);
        }

        @Override
        public void flushStderr(Long objId, String data) {
            commandOutputEvent(objId, data, CommandExecutorHelper.STANDARDERROR_OBJECT_TYPE);
        }

        @Override
        public void processStopped(Long objId, int exitCode) {
            commandExitEvent(objId, exitCode);
        }
    }
}
