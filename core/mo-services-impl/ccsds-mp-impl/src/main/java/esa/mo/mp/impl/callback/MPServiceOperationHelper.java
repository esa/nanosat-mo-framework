package esa.mo.mp.impl.callback;

import java.util.ArrayList;
import java.util.List;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mp.structures.ObjectIdPair;
import org.ccsds.moims.mo.mp.structures.ObjectIdPairList;

/**
 * MPServiceOperationHelper contains static helper methods to aid Long ID to MPServiceOperationArguments conversion
 */
public class MPServiceOperationHelper {

    public static List<MPServiceOperationArguments> asList(MPServiceOperationArguments arguments) {
        List<MPServiceOperationArguments> list = new ArrayList<>();
        list.add(arguments);
        return list;
    }

    public static MPServiceOperationArguments asArguments(ObjectId identityId, ObjectId definitionId, ObjectId instanceId, ObjectId statusId, MALInteraction interaction, ObjectId source) {
        return new MPServiceOperationArguments(identityId, definitionId, instanceId, statusId, interaction, source);
    }

    public static List<MPServiceOperationArguments> asArgumentsList(ObjectId identityId, ObjectId definitionId, ObjectId instanceId, ObjectId statusId, MALInteraction interaction, ObjectId source) {
        return asList(asArguments(identityId, definitionId, instanceId, statusId, interaction, source));
    }

    public static List<MPServiceOperationArguments> asArgumentsList(ObjectIdList identityIds, ObjectIdList definitionIds, ObjectIdList instanceIds, ObjectIdList statusIds, MALInteraction interaction, ObjectId source) {
        List<MPServiceOperationArguments> list = new ArrayList<>();
        for (int index = 0; index < identityIds.size(); index++) {
            list.add(new MPServiceOperationArguments(
                identityIds.get(index),
                definitionIds != null ? definitionIds.get(index) : null,
                instanceIds != null ? instanceIds.get(index) : null,
                statusIds != null ? statusIds.get(index) : null,
                interaction,
                source
            ));
        }
        return list;
    }

    public static List<MPServiceOperationArguments> asArgumentsList(ObjectIdPairList pairs, ObjectId source, MALInteraction interaction) {
        List<MPServiceOperationArguments> list = new ArrayList<>();
        for (ObjectIdPair pair : pairs) {
            list.add(new MPServiceOperationArguments(
                pair.getIdentityId(),
                pair.getObjectId(),
                null,
                null,
                interaction,
                source
            ));
        }
        return list;
    }
}
