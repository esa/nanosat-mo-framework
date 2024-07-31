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
package esa.mo.mp.impl.com;

import java.util.stream.Collectors;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mp.structures.ObjectIdPair;
import org.ccsds.moims.mo.mp.structures.ObjectIdPairList;
import org.ccsds.moims.mo.mp.structures.ObjectInstancePair;
import org.ccsds.moims.mo.mp.structures.ObjectInstancePairList;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;

/**
 * Utility helper class with static methods for ObjectId
 */
public class COMObjectIdHelper {

    private COMObjectIdHelper() {
    }

    /**
     * Get the ObjectId from ArchivePersistenceObject
     * @param comObject The COM ArchivePersistenceObject
     * @return The COM ObjectId
     */
    public static ObjectId getObjectId(ArchivePersistenceObject comObject) {
        if (comObject == null)
            return null;
        ObjectKey objectKey = new ObjectKey(comObject.getDomain(), comObject.getObjectId());
        return new ObjectId(comObject.getObjectType(), objectKey);
    }

    /**
     * Get the instance id from COM ObjectId
     * @param objectId The COM ObjectId
     * @return The instance id of the COM Object
     */
    public static Long getInstanceId(ObjectId objectId) {
        if (objectId == null || objectId.getKey() == null)
            return null;
        return objectId.getKey().getInstId();
    }

    /**
     * Get the instance ids from COM ObjectIds
     * @param objectIds The COM ObjectId list
     * @return The instance id list of the COM Objects
     */
    public static LongList getInstanceIds(ObjectIdList objectIds) {
        if (objectIds == null)
            return null;
        LongList result = new LongList();
        result.addAll(objectIds.stream().map(id -> getInstanceId(id)).collect(Collectors.toList()));
        return result;
    }

    /**
     * Get the ObjectId from object instance id and type
     * @param instanceId The instance id of the COM Object
     * @param objectType The type of the COM Object
     * @return The COM ObjectId
     */
    public static ObjectId getObjectId(Long instanceId, ObjectType objectType) {
        IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        return getObjectId(instanceId, objectType, domain);
    }

    /**
     * Get the ObjectId list from instance ids and type
     * @param instanceIds The instance id list of the COM Objects
     * @param objectType The type of the COM Objects
     * @return The COM ObjectId list
     */
    public static ObjectIdList getObjectIds(LongList instanceIds, ObjectType objectType) {
        IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        return getObjectIds(instanceIds, objectType, domain);
    }

    /**
     * Get the ObjectId from object instance id, type and domain
     * @param instanceId The instance id of the COM Object
     * @param objectType The type of the COM Object
     * @param domain The domain of the COM Object
     * @return The COM ObjectId
     */
    public static ObjectId getObjectId(Long instanceId, ObjectType objectType, IdentifierList domain) {
        ObjectKey objectKey = new ObjectKey(domain, instanceId);
        return new ObjectId(objectType, objectKey);
    }

    /**
     * Get the ObjectIds from object instance ids, type and domain
     * @param instanceIds The instance ids of the COM Objects
     * @param objectType The type of the COM Objects
     * @param domain The domain of the COM Objects
     * @return the COM ObjectId list
     */
    public static ObjectIdList getObjectIds(LongList instanceIds, ObjectType objectType, IdentifierList domain) {
        if (instanceIds == null || objectType == null)
            return null;
        ObjectIdList result = new ObjectIdList();
        result.addAll(instanceIds.stream().map(id -> {
            ObjectKey objectKey = new ObjectKey(domain, id);
            return new ObjectId(objectType, objectKey);
        }).collect(Collectors.toList()));
        return result;
    }

    /**
     * Takes related id list and object id list and returns a list of corresponding pairs (relatedId, objectId)
     * @param relatedIds The ids of the related objects
     * @param objectIds The ids of the object
     * @return The corresponding pairs list
     */
    public static ObjectIdPairList getObjectIdPairs(ObjectIdList relatedIds, ObjectIdList objectIds) {
        ObjectIdPairList pairs = new ObjectIdPairList();
        for (int index = 0; index < relatedIds.size(); index++) {
            pairs.add(new ObjectIdPair(relatedIds.get(index), objectIds.get(index)));
        }
        return pairs;
    }

    /**
     * Takes pairs of related id and object id and returns corresponding instance pairs
     * @param pairs The related id and object id pairs
     * @return The corresponding instance id pairs list
     */
    public static ObjectInstancePairList getInstancePairIds(ObjectIdPairList pairs) {
        ObjectInstancePairList list = new ObjectInstancePairList();
        for (ObjectIdPair pair : pairs) {
            list.add(new ObjectInstancePair(getInstanceId(pair.getIdentityId()), getInstanceId(pair.getObjectId())));
        }
        return list;
    }
}
