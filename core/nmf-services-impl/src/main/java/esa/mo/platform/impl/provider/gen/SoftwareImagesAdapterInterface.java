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

import java.io.File;
import java.io.IOException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.platform.structures.HypervisorStatus;
import org.ccsds.moims.mo.platform.structures.SoftwareImagePartitionList;
import org.ccsds.moims.mo.platform.structures.SoftwareImageStartStage;

/**
 * The SoftwareImagesAdapterInterface is an interface to create adapters that
 * connect the SoftwareImages service to the platform-specific hypervisor (for
 * example, XtratuM through a small native helper executable). The adapter
 * implements only the mechanics of the hypervisor; all policy (image
 * manifests, configuration-version compatibility, checksum verification,
 * partition allocation and occupancy) lives in the
 * {@link SoftwareImagesProviderServiceImpl}.
 *
 * @author Cesar Coelho
 */
public interface SoftwareImagesAdapterInterface {

    /**
     * Checks if the hypervisor is available on this platform.
     *
     * @return TRUE if the platform has a hypervisor, FALSE otherwise.
     */
    boolean isUnitAvailable();

    /**
     * Returns the status of the hypervisor: its product version and the
     * version of the configuration it is running.
     *
     * @return The status of the hypervisor.
     */
    HypervisorStatus getStatus();

    /**
     * Returns the partitions of the hypervisor configuration. The adapter
     * fills the identity fields (partitionId, description, resources) and
     * reports FAULTED partitions; the occupancy fields (state, loadedImage)
     * are overlaid by the provider from its own bookkeeping.
     *
     * @return The list of hypervisor partitions.
     */
    SoftwareImagePartitionList listPartitions();

    /**
     * Starts an image in a hypervisor partition. The adapter performs the
     * full start choreography: halt the partition if needed, load the image
     * into the partition memory, and start it. Adapters that can distinguish
     * the stages report them through the listener; coarse adapters may report
     * only {@link SoftwareImageStartStage#LOADING}.
     *
     * @param partitionId The partition to start the image in.
     * @param image The image file, already verified by the provider.
     * @param listener The listener to report the start stages to.
     * @throws IOException If the start failed.
     */
    void startImage(Identifier partitionId, File image, ImageStartListener listener) throws IOException;

    /**
     * Stops the image of a hypervisor partition: halts the partition and
     * clears it.
     *
     * @param partitionId The partition to stop.
     * @throws IOException If the stop failed.
     */
    void stopImage(Identifier partitionId) throws IOException;

    /**
     * Resets a hypervisor partition without reloading its image.
     *
     * @param partitionId The partition to restart.
     * @throws IOException If the restart failed.
     */
    void restartImage(Identifier partitionId) throws IOException;

    /**
     * Listener for the stages of an image start.
     */
    interface ImageStartListener {

        /**
         * Reports that the start has entered a new stage.
         *
         * @param stage The stage being entered.
         */
        void onStage(SoftwareImageStartStage stage);
    }
}
