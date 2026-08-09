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

import esa.mo.platform.impl.provider.gen.SoftwareImagesAdapterInterface;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.platform.structures.HypervisorStatus;
import org.ccsds.moims.mo.platform.structures.SoftwareImagePartition;
import org.ccsds.moims.mo.platform.structures.SoftwareImagePartitionList;
import org.ccsds.moims.mo.platform.structures.SoftwareImagePartitionState;
import org.ccsds.moims.mo.platform.structures.SoftwareImageStartStage;

/**
 * Software simulator adapter for the SoftwareImages service. Simulates a
 * hypervisor with two partitions; starts succeed instantly and report every
 * stage.
 *
 * @author Cesar Coelho
 */
public class SoftwareImagesSoftSimAdapter implements SoftwareImagesAdapterInterface {

    private static final Logger LOGGER
            = Logger.getLogger(SoftwareImagesSoftSimAdapter.class.getName());

    private static final String HYPERVISOR_VERSION = "Simulated XtratuM";
    private static final String CONFIG_VERSION = "sim-v1";
    private static final String[] PARTITION_IDS = {"p1", "p2"};

    @Override
    public boolean isUnitAvailable() {
        return true;
    }

    @Override
    public HypervisorStatus getStatus() {
        return new HypervisorStatus(HYPERVISOR_VERSION, CONFIG_VERSION);
    }

    @Override
    public SoftwareImagePartitionList listPartitions() {
        SoftwareImagePartitionList partitions = new SoftwareImagePartitionList();
        for (String id : PARTITION_IDS) {
            partitions.add(new SoftwareImagePartition(new Identifier(id),
                    "Simulated hypervisor partition " + id,
                    SoftwareImagePartitionState.FREE, null, "Simulated partition"));
        }
        return partitions;
    }

    @Override
    public void startImage(Identifier partitionId, File image,
            ImageStartListener listener) throws IOException {
        if (!image.isFile()) {
            throw new IOException("The image file does not exist: " + image);
        }
        listener.onStage(SoftwareImageStartStage.LOADING);
        listener.onStage(SoftwareImageStartStage.STARTING);
        LOGGER.log(Level.INFO, "Simulated start of {0} in {1}",
                new Object[]{image.getName(), partitionId});
    }

    @Override
    public void stopImage(Identifier partitionId) throws IOException {
        LOGGER.log(Level.INFO, "Simulated stop of {0}", partitionId);
    }

    @Override
    public void restartImage(Identifier partitionId) throws IOException {
        LOGGER.log(Level.INFO, "Simulated restart of {0}", partitionId);
    }
}
