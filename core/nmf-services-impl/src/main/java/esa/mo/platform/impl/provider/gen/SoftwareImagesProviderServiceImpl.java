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
import org.ccsds.moims.mo.mal.IncorrectStateException;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.UnknownException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionProvider;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.HeterogeneousList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.platform.DeviceInUseException;
import org.ccsds.moims.mo.platform.DeviceNotAvailableException;
import org.ccsds.moims.mo.platform.IncompatibleException;
import org.ccsds.moims.mo.platform.VerificationFailedException;
import org.ccsds.moims.mo.platform.softwareimages.SoftwareImagesHelper;
import org.ccsds.moims.mo.platform.softwareimages.SoftwareImagesServiceInfo;
import org.ccsds.moims.mo.platform.softwareimages.provider.SoftwareImagesInheritanceSkeleton;
import org.ccsds.moims.mo.platform.softwareimages.provider.StartImageInteraction;
import org.ccsds.moims.mo.platform.structures.HypervisorStatus;
import org.ccsds.moims.mo.platform.structures.SoftwareImagePartition;
import org.ccsds.moims.mo.platform.structures.SoftwareImagePartitionList;
import org.ccsds.moims.mo.platform.structures.SoftwareImagePartitionState;
import org.ccsds.moims.mo.platform.structures.SoftwareImageStartStage;
import org.ccsds.moims.mo.platform.structures.SoftwareImageStarted;
import org.ccsds.moims.mo.platform.structures.SoftwareImageStopped;

/**
 * SoftwareImages service Provider. Starts, stops and restarts software images
 * in the partitions of the platform hypervisor. The provider owns all policy —
 * image manifests, hypervisor configuration compatibility, checksum
 * verification, partition allocation and occupancy bookkeeping — while the
 * platform-specific mechanics live in the
 * {@link SoftwareImagesAdapterInterface}.
 */
public class SoftwareImagesProviderServiceImpl extends SoftwareImagesInheritanceSkeleton {

    private static final Logger LOGGER
            = Logger.getLogger(SoftwareImagesProviderServiceImpl.class.getName());

    /**
     * Overrides the directory scanned for image manifests (defaults to the
     * NMF apps directory).
     */
    public static final String MANIFEST_DIR_PROPERTY = "esa.mo.nmf.softwareimages.manifestDir";

    private MALProvider softwareImagesServiceProvider;
    private boolean running = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    private SoftwareImagesAdapterInterface adapter;
    private COMServicesProvider comServices;

    /**
     * The occupancy bookkeeping of the partitions, keyed by partition id.
     */
    private final Map<String, Occupancy> occupancies = new HashMap<>();

    /**
     * Initializes the SoftwareImages service.
     *
     * @param comServices The COM services provider, for archiving the
     * SoftwareImageStarted and SoftwareImageStopped objects. Can be null;
     * archiving is then skipped.
     * @param adapter The software images adapter.
     * @throws MALException On initialisation error.
     */
    public synchronized void init(COMServicesProvider comServices,
            SoftwareImagesAdapterInterface adapter) throws MALException {
        long timestamp = System.currentTimeMillis();

        // Shut down old service transport
        if (null != softwareImagesServiceProvider) {
            connection.closeAll();
        }

        this.comServices = comServices;
        this.adapter = adapter;
        softwareImagesServiceProvider = connection.startService(
                SoftwareImagesServiceInfo.SOFTWAREIMAGES_SERVICE_NAME.toString(),
                SoftwareImagesHelper.SOFTWAREIMAGES_SERVICE, this);

        running = true;
        timestamp = System.currentTimeMillis() - timestamp;
        LOGGER.info("Software Images service: READY! (" + timestamp + " ms)");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != softwareImagesServiceProvider) {
                softwareImagesServiceProvider.close();
            }

            connection.closeAll();
            running = false;
        } catch (MALException ex) {
            LOGGER.log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    @Override
    public synchronized SoftwareImagePartitionList listPartitions(final MALInteraction interaction)
            throws DeviceNotAvailableException, MALInteractionException, MALException {
        if (!adapter.isUnitAvailable()) {
            throw new DeviceNotAvailableException(null);
        }
        SoftwareImagePartitionList partitions = new SoftwareImagePartitionList();
        for (SoftwareImagePartition partition : adapter.listPartitions()) {
            partitions.add(overlayOccupancy(partition));
        }
        return partitions;
    }

    @Override
    public void startImage(final Identifier imageName, final Identifier preferredPartition,
            final StartImageInteraction interaction) throws UnknownException,
            DeviceInUseException, DeviceNotAvailableException, IncompatibleException,
            VerificationFailedException, MALInteractionException, MALException {
        final ImageManifest manifest;
        final Identifier partitionId;
        final ImageManifest.Variant variant;

        synchronized (this) {
            if (!adapter.isUnitAvailable()) {
                throw new DeviceNotAvailableException(null);
            }

            manifest = ImageManifest.find(getManifestDir(), imageName.getValue());
            if (manifest == null) {
                throw new UnknownException(null);
            }

            String runningConfig = adapter.getStatus().getConfigVersion();
            if (!manifest.getConfigVersion().equals(runningConfig)) {
                throw new IncompatibleException(runningConfig);
            }

            SoftwareImagePartition partition = allocatePartition(manifest, preferredPartition);
            partitionId = partition.getPartitionId();
            variant = manifest.getVariant(partitionId.getValue());

            // Reserve the partition before leaving the lock
            occupancies.put(partitionId.getValue(), new Occupancy(imageName, true));
        }

        try {
            interaction.sendAcknowledgement();

            interaction.sendUpdate(SoftwareImageStartStage.VERIFYING);
            if (!variant.verify()) {
                throw new IOException("The image file failed its checksum verification: "
                        + variant.getFile());
            }

            adapter.startImage(partitionId, variant.getFile(), stage -> {
                try {
                    interaction.sendUpdate(stage);
                } catch (MALInteractionException | MALException ex) {
                    LOGGER.log(Level.WARNING, "Could not send the start stage update", ex);
                }
            });

            synchronized (this) {
                Long archiveObjId = storeSoftwareImageStarted(imageName, partitionId);
                occupancies.put(partitionId.getValue(),
                        new Occupancy(imageName, false, archiveObjId));
            }
            interaction.sendResponse(overlayOccupancy(findPartition(partitionId)));
            LOGGER.log(Level.INFO, "Image {0} started in partition {1}",
                    new Object[]{imageName, partitionId});
        } catch (IOException ex) {
            synchronized (this) {
                occupancies.remove(partitionId.getValue());
            }
            LOGGER.log(Level.WARNING, "The image start failed", ex);
            interaction.sendError(new VerificationFailedException(ex.getMessage()));
        }
    }

    @Override
    public synchronized void stopImage(final Identifier partitionId,
            final MALInteraction interaction) throws UnknownException,
            DeviceNotAvailableException, MALInteractionException, MALException {
        if (!adapter.isUnitAvailable()) {
            throw new DeviceNotAvailableException(null);
        }

        SoftwareImagePartition partition = findPartition(partitionId);
        if (partition == null) {
            throw new UnknownException(null);
        }

        Occupancy occupancy = occupancies.get(partitionId.getValue());
        if (occupancy == null) {
            return; // Already FREE: stopping is idempotent
        }

        try {
            adapter.stopImage(partitionId);
        } catch (IOException ex) {
            throw new MALException("The stop failed: " + ex.getMessage(), ex);
        }

        storeSoftwareImageStopped(occupancy.imageName, partitionId, occupancy.archiveObjId);
        occupancies.remove(partitionId.getValue());
        LOGGER.log(Level.INFO, "Image {0} stopped in partition {1}",
                new Object[]{occupancy.imageName, partitionId});
    }

    @Override
    public synchronized void restartImage(final Identifier partitionId,
            final MALInteraction interaction) throws UnknownException,
            IncorrectStateException, DeviceNotAvailableException,
            MALInteractionException, MALException {
        if (!adapter.isUnitAvailable()) {
            throw new DeviceNotAvailableException(null);
        }

        SoftwareImagePartition partition = findPartition(partitionId);
        if (partition == null) {
            throw new UnknownException(null);
        }

        Occupancy occupancy = occupancies.get(partitionId.getValue());
        if (occupancy == null || occupancy.starting) {
            throw new IncorrectStateException("The partition has no image loaded: "
                    + partitionId.getValue());
        }

        try {
            adapter.restartImage(partitionId);
        } catch (IOException ex) {
            throw new MALException("The restart failed: " + ex.getMessage(), ex);
        }
        LOGGER.log(Level.INFO, "Image {0} restarted in partition {1}",
                new Object[]{occupancy.imageName, partitionId});
    }

    @Override
    public synchronized HypervisorStatus getStatus(final MALInteraction interaction)
            throws DeviceNotAvailableException, MALInteractionException, MALException {
        if (!adapter.isUnitAvailable()) {
            throw new DeviceNotAvailableException(null);
        }
        return adapter.getStatus();
    }

    /**
     * Selects the partition to start an image in: the preferred one if given,
     * otherwise the first free partition for which the manifest declares a
     * variant.
     */
    private SoftwareImagePartition allocatePartition(ImageManifest manifest,
            Identifier preferredPartition) throws UnknownException, DeviceInUseException,
            DeviceNotAvailableException {
        if (preferredPartition != null) {
            SoftwareImagePartition partition = findPartition(preferredPartition);
            if (partition == null) {
                throw new UnknownException(null);
            }
            if (occupancies.containsKey(preferredPartition.getValue())
                    || SoftwareImagePartitionState.FAULTED.equals(partition.getState())) {
                throw new DeviceInUseException(null);
            }
            if (manifest.getVariant(preferredPartition.getValue()) == null) {
                throw new DeviceNotAvailableException(
                        "The image has no variant for the partition: "
                        + preferredPartition.getValue());
            }
            return partition;
        }

        for (SoftwareImagePartition partition : adapter.listPartitions()) {
            String id = partition.getPartitionId().getValue();
            if (!occupancies.containsKey(id)
                    && !SoftwareImagePartitionState.FAULTED.equals(partition.getState())
                    && manifest.getVariant(id) != null) {
                return partition;
            }
        }
        throw new DeviceNotAvailableException(
                "No free partition has a variant of the image: " + manifest.getName());
    }

    private SoftwareImagePartition findPartition(Identifier partitionId) {
        for (SoftwareImagePartition partition : adapter.listPartitions()) {
            if (partition.getPartitionId().equals(partitionId)) {
                return partition;
            }
        }
        return null;
    }

    /**
     * Returns a copy of the partition with the provider's occupancy
     * bookkeeping (state, loadedImage) overlaid on the adapter's identity
     * fields.
     */
    private SoftwareImagePartition overlayOccupancy(SoftwareImagePartition partition) {
        Occupancy occupancy = occupancies.get(partition.getPartitionId().getValue());
        SoftwareImagePartitionState state = partition.getState();
        Identifier loadedImage = null;

        if (occupancy != null) {
            state = occupancy.starting
                    ? SoftwareImagePartitionState.STARTING
                    : SoftwareImagePartitionState.RUNNING;
            loadedImage = occupancy.imageName;
        } else if (!SoftwareImagePartitionState.FAULTED.equals(state)) {
            state = SoftwareImagePartitionState.FREE;
        }

        return new SoftwareImagePartition(partition.getPartitionId(),
                partition.getDescription(), state, loadedImage, partition.getResources());
    }

    /**
     * Stores a SoftwareImageStarted object in the COM Archive.
     *
     * @return The archive object instance id, or null if archiving is off.
     */
    private Long storeSoftwareImageStarted(Identifier imageName, Identifier partitionId) {
        if (comServices == null || comServices.getArchiveService() == null) {
            return null;
        }
        try {
            HeterogeneousList bodies = new HeterogeneousList();
            bodies.add(new SoftwareImageStarted(imageName, partitionId));
            LongList objIds = comServices.getArchiveService().store(
                    true,
                    SoftwareImagesServiceInfo.SOFTWAREIMAGESTARTED_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(),
                    HelperArchive.generateArchiveDetailsList(null, null,
                            connection.getConnectionDetails().getProviderURI()),
                    bodies,
                    null);
            return (objIds != null && objIds.size() == 1) ? objIds.get(0) : null;
        } catch (org.ccsds.moims.mo.com.DuplicateException
                | org.ccsds.moims.mo.com.InvalidArgumentException
                | MALException | MALInteractionException ex) {
            LOGGER.log(Level.SEVERE, "Could not store the SoftwareImageStarted object", ex);
            return null;
        }
    }

    /**
     * Stores a SoftwareImageStopped object in the COM Archive, related to the
     * SoftwareImageStarted object of the start that this stop terminates.
     */
    private void storeSoftwareImageStopped(Identifier imageName, Identifier partitionId,
            Long startedObjId) {
        if (comServices == null || comServices.getArchiveService() == null) {
            return;
        }
        try {
            HeterogeneousList bodies = new HeterogeneousList();
            bodies.add(new SoftwareImageStopped(imageName, partitionId));
            comServices.getArchiveService().store(
                    false,
                    SoftwareImagesServiceInfo.SOFTWAREIMAGESTOPPED_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(),
                    HelperArchive.generateArchiveDetailsList(startedObjId, null,
                            connection.getConnectionDetails().getProviderURI()),
                    bodies,
                    null);
        } catch (org.ccsds.moims.mo.com.DuplicateException
                | org.ccsds.moims.mo.com.InvalidArgumentException
                | MALException | MALInteractionException ex) {
            LOGGER.log(Level.SEVERE, "Could not store the SoftwareImageStopped object", ex);
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
     * The occupancy of a partition: which image is in it and, once the start
     * completes, the archive id of its SoftwareImageStarted object.
     */
    private static final class Occupancy {

        private final Identifier imageName;
        private final boolean starting;
        private final Long archiveObjId;

        Occupancy(Identifier imageName, boolean starting) {
            this(imageName, starting, null);
        }

        Occupancy(Identifier imageName, boolean starting, Long archiveObjId) {
            this.imageName = imageName;
            this.starting = starting;
            this.archiveObjId = archiveObjId;
        }
    }
}
