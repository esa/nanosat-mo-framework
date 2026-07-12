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
package esa.mo.platform.impl.provider.softsim;

import esa.mo.platform.impl.provider.gen.FPGAAdapterInterface;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.platform.structures.FabricStatus;
import org.ccsds.moims.mo.platform.structures.ModuleLoadStage;
import org.ccsds.moims.mo.platform.structures.Partition;
import org.ccsds.moims.mo.platform.structures.PartitionList;
import org.ccsds.moims.mo.platform.structures.PartitionState;

/**
 * Software simulator adapter for the FPGA service. Simulates a
 * small reconfigurable fabric with two identical partitions; loads succeed
 * instantly and report every load stage.
 *
 * @author Cesar Coelho
 */
public class FPGASoftSimAdapter implements FPGAAdapterInterface {

    private static final Logger LOGGER
            = Logger.getLogger(FPGASoftSimAdapter.class.getName());

    private static final String SHELL_VERSION = "sim-v1";
    private static final String DEVICE_MODEL = "Simulated Zynq UltraScale+ fabric";
    private static final String[] PARTITION_IDS = {"slot-a", "slot-b"};

    private long scrubCycles = 0;

    @Override
    public boolean isUnitAvailable() {
        return true;
    }

    @Override
    public FabricStatus getStatus() {
        scrubCycles++;
        return new FabricStatus(SHELL_VERSION, DEVICE_MODEL,
                new UInteger(0), new UInteger(scrubCycles));
    }

    @Override
    public PartitionList listPartitions() {
        PartitionList partitions = new PartitionList();
        for (int i = 0; i < PARTITION_IDS.length; i++) {
            partitions.add(new Partition(new Identifier(PARTITION_IDS[i]),
                    "Simulated reconfigurable partition " + PARTITION_IDS[i],
                    PartitionState.FREE, null, null,
                    "/dev/uio" + i, "Simulated fabric slot"));
        }
        return partitions;
    }

    @Override
    public void loadModule(Identifier partitionId, File bitstream,
            ModuleLoadListener listener) throws IOException {
        if (!bitstream.isFile()) {
            throw new IOException("The bitstream file does not exist: " + bitstream);
        }
        listener.onStage(ModuleLoadStage.DECOUPLING);
        listener.onStage(ModuleLoadStage.WRITING);
        listener.onStage(ModuleLoadStage.RESETTING);
        LOGGER.log(Level.INFO, "Simulated load of {0} into {1}",
                new Object[]{bitstream.getName(), partitionId});
    }

    @Override
    public void unloadModule(Identifier partitionId) throws IOException {
        LOGGER.log(Level.INFO, "Simulated unload of {0}", partitionId);
    }
}
