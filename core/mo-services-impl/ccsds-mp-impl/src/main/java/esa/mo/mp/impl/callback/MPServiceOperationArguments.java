package esa.mo.mp.impl.callback;

import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.provider.MALInteraction;

/**
 * MPServiceOperationArguments holds information about stored COM objects,
 * that is passed to registered operation callbacks
 */
public class MPServiceOperationArguments {

    private final ObjectId identityId;
    private final ObjectId definitionId;
    private final ObjectId instanceId;
    private final ObjectId statusId;
    private final MALInteraction interaction;
    private final ObjectId source;

    public MPServiceOperationArguments(ObjectId identityId, ObjectId definitionId, ObjectId instanceId, ObjectId statusId, MALInteraction interaction, ObjectId source) {
        this.identityId = identityId;
        this.definitionId = definitionId;
        this.instanceId = instanceId;
        this.statusId = statusId;
        this.interaction = interaction;
        this.source = source;
    }

    public ObjectId getIdentityId() {
        return this.identityId;
    }

    public ObjectId getDefinitionId() {
        return this.definitionId;
    }

    public ObjectId getInstanceId() {
        return this.instanceId;
    }

    public ObjectId getStatusId() {
        return this.statusId;
    }

    public MALInteraction getInteraction() {
        return this.interaction;
    }

    public ObjectId getSource() {
        return this.source;
    }
}
