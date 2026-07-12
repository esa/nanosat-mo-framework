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
import org.ccsds.moims.mo.platform.structures.FabricStatus;
import org.ccsds.moims.mo.platform.structures.ModuleLoadStage;
import org.ccsds.moims.mo.platform.structures.PartitionList;

/**
 * The FPGAAdapterInterface is an interface to create adapters
 * that connect the FPGA service to the platform-specific
 * reconfigurable fabric (for example, the Linux FPGA Manager on a Zynq
 * UltraScale+). The adapter implements only the mechanics of the fabric; all
 * policy (module manifests, shell-version compatibility, checksum
 * verification, partition allocation and ownership) lives in the
 * {@link FPGAProviderServiceImpl}.
 *
 * @author Cesar Coelho
 */
public interface FPGAAdapterInterface {

    /**
     * Checks if the reconfigurable fabric is available on this platform.
     *
     * @return TRUE if the platform has a reconfigurable fabric, FALSE
     * otherwise.
     */
    boolean isUnitAvailable();

    /**
     * Returns the status of the reconfigurable fabric. The SEU and scrubbing
     * counters may be NULL when the platform cannot report them.
     *
     * @return The status of the reconfigurable fabric.
     */
    FabricStatus getStatus();

    /**
     * Returns the reconfigurable partitions of the platform. The adapter fills
     * the identity fields (partitionId, description, dataPlaneRef, resources)
     * and reports FAULTED partitions; the occupancy fields (state,
     * loadedModule, ownerAppId) are overlaid by the provider from its own
     * bookkeeping.
     *
     * @return The list of reconfigurable partitions.
     */
    PartitionList listPartitions();

    /**
     * Loads a partial bitstream into a reconfigurable partition. The adapter
     * performs the full load choreography: decouple the partition from the
     * static shell, write the bitstream to the fabric, reset the module and
     * couple the partition back. Adapters that can distinguish the stages
     * report them through the listener; coarse adapters may report only
     * {@link ModuleLoadStage#WRITING}.
     *
     * @param partitionId The partition to load the bitstream into.
     * @param bitstream The partial bitstream file, already verified by the
     * provider.
     * @param listener The listener to report the load stages to.
     * @throws IOException If the load failed.
     */
    void loadModule(Identifier partitionId, File bitstream, ModuleLoadListener listener) throws IOException;

    /**
     * Unloads the module of a reconfigurable partition: decouples the
     * partition from the static shell and clears it.
     *
     * @param partitionId The partition to unload.
     * @throws IOException If the unload failed.
     */
    void unloadModule(Identifier partitionId) throws IOException;

    /**
     * Listener for the stages of a module load.
     */
    interface ModuleLoadListener {

        /**
         * Reports that the load has entered a new stage.
         *
         * @param stage The stage being entered.
         */
        void onStage(ModuleLoadStage stage);
    }
}
