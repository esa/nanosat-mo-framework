/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
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
package esa.mo.platform.impl.provider.gen;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.nmf.environment.Deployment;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.UnknownException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionProvider;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.AttributeList;
import org.ccsds.moims.mo.mal.structures.HeterogeneousList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.platform.DeviceInUseException;
import org.ccsds.moims.mo.platform.DeviceNotAvailableException;
import org.ccsds.moims.mo.platform.IncompatibleException;
import org.ccsds.moims.mo.platform.VerificationFailedException;
import org.ccsds.moims.mo.platform.fpga.FPGAHelper;
import org.ccsds.moims.mo.platform.fpga.FPGAServiceInfo;
import org.ccsds.moims.mo.platform.fpga.provider.FPGAInheritanceSkeleton;
import org.ccsds.moims.mo.platform.fpga.provider.LoadModuleInteraction;
import org.ccsds.moims.mo.platform.fpga.provider.MonitorPartitionsPublisher;
import org.ccsds.moims.mo.platform.structures.FabricStatus;
import org.ccsds.moims.mo.platform.structures.ModuleLoadStage;
import org.ccsds.moims.mo.platform.structures.FPGAModuleLoaded;
import org.ccsds.moims.mo.platform.structures.FPGAModuleUnloaded;
import org.ccsds.moims.mo.platform.structures.FPGAPartition;
import org.ccsds.moims.mo.platform.structures.FPGAPartitionList;
import org.ccsds.moims.mo.platform.structures.FPGAPartitionState;

/**
 * FPGA service Provider. Loads and unloads gateware modules
 * into the reconfigurable partitions of the platform FPGA. The provider owns
 * all policy — module manifests, shell-version compatibility, checksum
 * verification, partition allocation and occupancy bookkeeping — while the
 * platform-specific mechanics live in the
 * {@link FPGAAdapterInterface}.
 */
public class FPGAProviderServiceImpl extends FPGAInheritanceSkeleton {

    private static final Logger LOGGER
            = Logger.getLogger(FPGAProviderServiceImpl.class.getName());

    /**
     * Overrides the directory scanned for module manifests (defaults to the
     * NMF apps directory).
     */
    public static final String MANIFEST_DIR_PROPERTY = "esa.mo.nmf.fpga.manifestDir";

    private MALProvider fpgaServiceProvider;
    private boolean running = false;
    private MonitorPartitionsPublisher publisher;
    private final Object lock = new Object();
    private boolean isRegistered = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    private FPGAAdapterInterface adapter;
    private COMServicesProvider comServices;

    /**
     * The occupancy bookkeeping of the partitions, keyed by partition id.
     */
    private final Map<String, Occupancy> occupancies = new HashMap<>();

    /**
     * Default constructor.
     */
    public FPGAProviderServiceImpl() {
    }

    /**
     * Initializes the FPGA service.
     *
     * @param comServices The COM services provider, for archiving the
     * FPGAModuleLoaded and FPGAModuleUnloaded objects. Can be null; archiving is then
     * skipped.
     * @param adapter The FPGA reconfiguration adapter.
     * @throws MALException On initialisation error.
     */
    public synchronized void init(COMServicesProvider comServices,
            FPGAAdapterInterface adapter) throws MALException {
        long timestamp = System.currentTimeMillis();
        publisher = createMonitorPartitionsPublisher(ConfigurationProviderSingleton.getDomain(),
                null, SessionType.LIVE,
                ConfigurationProviderSingleton.getSourceSessionName(),
                QoSLevel.BESTEFFORT, null, new UInteger(0));

        // Shut down old service transport
        if (null != fpgaServiceProvider) {
            connection.closeAll();
        }

        this.comServices = comServices;
        this.adapter = adapter;
        fpgaServiceProvider = connection.startService(
                FPGAServiceInfo.FPGA_SERVICE_NAME.toString(),
                FPGAHelper.FPGA_SERVICE, this);

        running = true;
        timestamp = System.currentTimeMillis() - timestamp;
        LOGGER.info("FPGA service: READY! (" + timestamp + " ms)");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != fpgaServiceProvider) {
                fpgaServiceProvider.close();
            }

            connection.closeAll();
            running = false;
        } catch (MALException ex) {
            LOGGER.log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    @Override
    public synchronized FPGAPartitionList listPartitions(final MALInteraction interaction)
            throws DeviceNotAvailableException, MALInteractionException, MALException {
        if (!adapter.isUnitAvailable()) {
            throw new DeviceNotAvailableException(null);
        }
        FPGAPartitionList partitions = new FPGAPartitionList();
        for (FPGAPartition partition : adapter.listPartitions()) {
            partitions.add(overlayOccupancy(partition));
        }
        return partitions;
    }

    @Override
    public void loadModule(final Identifier moduleName, final Identifier preferredPartition,
            final LoadModuleInteraction interaction) throws UnknownException,
            DeviceInUseException, DeviceNotAvailableException, IncompatibleException,
            VerificationFailedException, MALInteractionException, MALException {
        final ModuleManifest manifest;
        final Identifier partitionId;
        final FPGAPartition partition;
        final ModuleManifest.Variant variant;

        synchronized (this) {
            if (!adapter.isUnitAvailable()) {
                throw new DeviceNotAvailableException(null);
            }

            manifest = ModuleManifest.find(getManifestDir(), moduleName.getValue());
            if (manifest == null) {
                throw new UnknownException(null);
            }

            String runningShell = adapter.getStatus().getShellVersion();
            if (!manifest.getShellVersion().equals(runningShell)) {
                throw new IncompatibleException(runningShell);
            }

            partition = allocatePartition(manifest, preferredPartition);
            partitionId = partition.getPartitionId();
            variant = manifest.getVariant(partitionId.getValue());

            // Reserve the partition before leaving the lock
            occupancies.put(partitionId.getValue(), new Occupancy(moduleName, true));
        }

        try {
            interaction.sendAcknowledgement();
            publishPartition(partitionId);

            interaction.sendUpdate(ModuleLoadStage.VERIFYING);
            if (!variant.verify()) {
                throw new IOException("The bitstream file failed its checksum verification: "
                        + variant.getFile());
            }

            adapter.loadModule(partitionId, variant.getFile(), stage -> {
                try {
                    interaction.sendUpdate(stage);
                } catch (MALInteractionException | MALException ex) {
                    LOGGER.log(Level.WARNING, "Could not send the load stage update", ex);
                }
            });

            Long archiveObjId;
            synchronized (this) {
                archiveObjId = storeFPGAModuleLoaded(moduleName, partitionId);
                occupancies.put(partitionId.getValue(),
                        new Occupancy(moduleName, false, archiveObjId));
            }
            publishPartition(partitionId);
            interaction.sendResponse(overlayOccupancy(findPartition(partitionId)));
            LOGGER.log(Level.INFO, "Module {0} loaded into partition {1}",
                    new Object[]{moduleName, partitionId});
        } catch (IOException ex) {
            synchronized (this) {
                occupancies.remove(partitionId.getValue());
            }
            publishPartition(partitionId);
            LOGGER.log(Level.WARNING, "The module load failed", ex);
            interaction.sendError(new VerificationFailedException(ex.getMessage()));
        }
    }

    @Override
    public synchronized void unloadModule(final Identifier partitionId,
            final MALInteraction interaction) throws UnknownException,
            DeviceNotAvailableException, MALInteractionException, MALException {
        if (!adapter.isUnitAvailable()) {
            throw new DeviceNotAvailableException(null);
        }

        FPGAPartition partition = findPartition(partitionId);
        if (partition == null) {
            throw new UnknownException(null);
        }

        Occupancy occupancy = occupancies.get(partitionId.getValue());
        if (occupancy == null) {
            return; // Already FREE: unloading is idempotent
        }

        try {
            adapter.unloadModule(partitionId);
        } catch (IOException ex) {
            throw new MALException("The unload failed: " + ex.getMessage(), ex);
        }

        storeFPGAModuleUnloaded(occupancy.moduleName, partitionId, occupancy.archiveObjId);
        occupancies.remove(partitionId.getValue());
        publishPartition(partitionId);
        LOGGER.log(Level.INFO, "Module {0} unloaded from partition {1}",
                new Object[]{occupancy.moduleName, partitionId});
    }

    @Override
    public synchronized FabricStatus getStatus(final MALInteraction interaction)
            throws DeviceNotAvailableException, MALInteractionException, MALException {
        if (!adapter.isUnitAvailable()) {
            throw new DeviceNotAvailableException(null);
        }
        return adapter.getStatus();
    }

    /**
     * Selects the partition to load a module into: the preferred one if given,
     * otherwise the first free partition for which the manifest declares a
     * bitstream variant.
     */
    private FPGAPartition allocatePartition(ModuleManifest manifest, Identifier preferredPartition)
            throws UnknownException, DeviceInUseException, DeviceNotAvailableException {
        if (preferredPartition != null) {
            FPGAPartition partition = findPartition(preferredPartition);
            if (partition == null) {
                throw new UnknownException(null);
            }
            if (occupancies.containsKey(preferredPartition.getValue())
                    || FPGAPartitionState.FAULTED.equals(partition.getState())) {
                throw new DeviceInUseException(null);
            }
            if (manifest.getVariant(preferredPartition.getValue()) == null) {
                throw new DeviceNotAvailableException(
                        "The module has no bitstream variant for the partition: "
                        + preferredPartition.getValue());
            }
            return partition;
        }

        for (FPGAPartition partition : adapter.listPartitions()) {
            String id = partition.getPartitionId().getValue();
            if (!occupancies.containsKey(id)
                    && !FPGAPartitionState.FAULTED.equals(partition.getState())
                    && manifest.getVariant(id) != null) {
                return partition;
            }
        }
        throw new DeviceNotAvailableException(
                "No free partition has a bitstream variant of the module: " + manifest.getName());
    }

    private FPGAPartition findPartition(Identifier partitionId) {
        for (FPGAPartition partition : adapter.listPartitions()) {
            if (partition.getPartitionId().equals(partitionId)) {
                return partition;
            }
        }
        return null;
    }

    /**
     * Returns a copy of the partition with the provider's occupancy
     * bookkeeping (state, loadedModule, ownerAppId) overlaid on the adapter's
     * identity fields.
     */
    private FPGAPartition overlayOccupancy(FPGAPartition partition) {
        Occupancy occupancy = occupancies.get(partition.getPartitionId().getValue());
        FPGAPartitionState state = partition.getState();
        Identifier loadedModule = null;

        if (occupancy != null) {
            state = occupancy.loading ? FPGAPartitionState.LOADING : FPGAPartitionState.LOADED;
            loadedModule = occupancy.moduleName;
        } else if (!FPGAPartitionState.FAULTED.equals(state)) {
            state = FPGAPartitionState.FREE;
        }

        return new FPGAPartition(partition.getPartitionId(), partition.getDescription(),
                state, loadedModule, null, partition.getDataPlaneRef(),
                partition.getResources());
    }

    private void publishPartition(Identifier partitionId) {
        try {
            FPGAPartition partition;
            synchronized (this) {
                FPGAPartition raw = findPartition(partitionId);
                if (raw == null) {
                    return;
                }
                partition = overlayOccupancy(raw);
            }
            synchronized (lock) {
                if (!isRegistered) {
                    publisher.registerWithDefaultKeys(
                            new SoftwareDefinedRadioProviderServiceImpl.PublishInteractionListener());
                    isRegistered = true;
                }
            }

            AttributeList keys = new AttributeList();
            keys.add(partitionId);
            URI source = connection.getConnectionDetails().getProviderURI();
            UpdateHeader updateHeader = new UpdateHeader(new Identifier(source.getValue()),
                    connection.getConnectionDetails().getDomain(), keys.getAsNullableAttributeList());

            publisher.publish(updateHeader, partition);
        } catch (IllegalArgumentException | MALInteractionException | MALException ex) {
            LOGGER.log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        }
    }

    /**
     * Stores an FPGAModuleLoaded object in the COM Archive.
     *
     * @return The archive object instance id, or null if archiving is off.
     */
    private Long storeFPGAModuleLoaded(Identifier moduleName, Identifier partitionId) {
        if (comServices == null || comServices.getArchiveService() == null) {
            return null;
        }
        try {
            HeterogeneousList bodies = new HeterogeneousList();
            bodies.add(new FPGAModuleLoaded(moduleName, partitionId, null));
            LongList objIds = comServices.getArchiveService().store(
                    true,
                    FPGAServiceInfo.FPGAMODULELOADED_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(),
                    HelperArchive.generateArchiveDetailsList(null, null,
                            connection.getConnectionDetails().getProviderURI()),
                    bodies,
                    null);
            return (objIds != null && objIds.size() == 1) ? objIds.get(0) : null;
        } catch (org.ccsds.moims.mo.com.DuplicateException
                | org.ccsds.moims.mo.com.InvalidArgumentException
                | MALException | MALInteractionException ex) {
            LOGGER.log(Level.SEVERE, "Could not store the FPGAModuleLoaded object", ex);
            return null;
        }
    }

    /**
     * Stores an FPGAModuleUnloaded object in the COM Archive, related to the
     * FPGAModuleLoaded object of the load that this unload terminates.
     */
    private void storeFPGAModuleUnloaded(Identifier moduleName, Identifier partitionId,
            Long loadedObjId) {
        if (comServices == null || comServices.getArchiveService() == null) {
            return;
        }
        try {
            HeterogeneousList bodies = new HeterogeneousList();
            bodies.add(new FPGAModuleUnloaded(moduleName, partitionId));
            comServices.getArchiveService().store(
                    false,
                    FPGAServiceInfo.FPGAMODULEUNLOADED_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(),
                    HelperArchive.generateArchiveDetailsList(loadedObjId, null,
                            connection.getConnectionDetails().getProviderURI()),
                    bodies,
                    null);
        } catch (org.ccsds.moims.mo.com.DuplicateException
                | org.ccsds.moims.mo.com.InvalidArgumentException
                | MALException | MALInteractionException ex) {
            LOGGER.log(Level.SEVERE, "Could not store the FPGAModuleUnloaded object", ex);
        }
    }

    private static File getManifestDir() {
        String override = System.getProperty(MANIFEST_DIR_PROPERTY);
        if (override != null) {
            return new File(override);
        }
        try {
            return Deployment.getAppsDir();
        } catch (RuntimeException ex) {
            // Not a deployed NMF filesystem (e.g. a bare test run)
            return new File("apps");
        }
    }

    /**
     * The occupancy of a partition: which module is in it and, once the load
     * completes, the archive id of its FPGAModuleLoaded object.
     */
    private static final class Occupancy {

        private final Identifier moduleName;
        private final boolean loading;
        private final Long archiveObjId;

        Occupancy(Identifier moduleName, boolean loading) {
            this(moduleName, loading, null);
        }

        Occupancy(Identifier moduleName, boolean loading, Long archiveObjId) {
            this.moduleName = moduleName;
            this.loading = loading;
            this.archiveObjId = archiveObjId;
        }
    }
}
