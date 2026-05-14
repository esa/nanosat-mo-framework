package esa.mo.mc.impl.provider;

import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mc.structures.ExecutionStageType;

/**
 * Callback interface for publishing monitorExecution PUB-SUB updates.
 */
interface ExecutionProgressPublisher {

    void publishExecutionProgress(Long definitionId, Long executionId, UOctet actionCategory,
            ExecutionStageType stageType, boolean success, UShort step, String comment);

}
