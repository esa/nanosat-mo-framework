/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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
package esa.mo.com.impl.archive.fast;

import esa.mo.com.impl.archive.db.DatabaseBackend;
import esa.mo.com.impl.archive.entities.ObjectTypeHolderEntity;
import esa.mo.com.impl.util.HelperCOM;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.persistence.Query;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.IntegerList;
import org.ccsds.moims.mo.mal.structures.LongList;

/**
 *
 *
 */
public class FastObjectType {

    private final static String QUERY_DELETE_OBJECTTYPE = "DELETE FROM ObjectTypeHolderEntity";
    private final static String QUERY_SELECT_OBJECTTYPE = "SELECT PU FROM ObjectTypeHolderEntity PU";
    private final DatabaseBackend dbBackend;
    private AtomicInteger uniqueId = new AtomicInteger(0);
    private HashMap<Long, Integer> fastID;
    private HashMap<Integer, Long> fastIDreverse;

    public FastObjectType(final DatabaseBackend dbBackend) {
        this.fastID = new HashMap<Long, Integer>();
        this.fastIDreverse = new HashMap<Integer, Long>();
        this.dbBackend = dbBackend;
    }

    public synchronized void resetFastObjectType() {
        this.fastID = new HashMap<Long, Integer>();
        this.fastIDreverse = new HashMap<Integer, Long>();
        uniqueId = new AtomicInteger(0);

        // To Do: Erase it from the table
        dbBackend.getEM().getTransaction().begin();
        dbBackend.getEM().createQuery(QUERY_DELETE_OBJECTTYPE).executeUpdate();
        dbBackend.getEM().getTransaction().commit();
    }

    public synchronized void init() {
        // Retrieve all the ids and objectTypes from the Database
        dbBackend.createEntityManager();

        // Get All the objectTypes available
        Query query = dbBackend.getEM().createQuery(QUERY_SELECT_OBJECTTYPE);
        List resultList = query.getResultList();
        ArrayList<ObjectTypeHolderEntity> objectTypeHolderEntities = new ArrayList<ObjectTypeHolderEntity>();
        objectTypeHolderEntities.addAll(resultList);

        dbBackend.closeEntityManager();

        final IntegerList ids = new IntegerList();
        final LongList objectTypes = new LongList();

        // From the list of entities to separate lists of ids and objectTypeIds
        for (int i = 0; i < objectTypeHolderEntities.size(); i++) {
            ids.add(objectTypeHolderEntities.get(i).getId());
            objectTypes.add(objectTypeHolderEntities.get(i).getObjectType());
        }

        int max = 0;

        // Populate the variables on this class
        for (int i = 0; i < ids.size(); i++) {
            this.fastID.put(objectTypes.get(i), ids.get(i));
            this.fastIDreverse.put(ids.get(i), objectTypes.get(i));

            if (ids.get(i) > max) { // Get the maximum value
                max = ids.get(i);
            }
        }

        uniqueId = new AtomicInteger(max);
    }

    public synchronized boolean exists(final Long objectType) {
        return (this.fastID.get(objectType) != null);
    }

    public synchronized boolean exists(final Integer id) {
        return (this.fastIDreverse.get(id) != null);
    }

    private Integer addNew(final Long objectType) {
        final int id = uniqueId.incrementAndGet();
        dbBackend.createEntityManager();

        // Create Entity
        ObjectTypeHolderEntity objectTypeEntity = new ObjectTypeHolderEntity(id, objectType);

        // Add it to the table
        dbBackend.getEM().getTransaction().begin();
        dbBackend.getEM().persist(objectTypeEntity);
        dbBackend.getEM().getTransaction().commit();
        dbBackend.closeEntityManager();

        this.fastID.put(objectType, id);
        this.fastIDreverse.put(id, objectType);

        return id;
    }

    public synchronized Integer getObjectTypeId(final ObjectType objectType) {
        final Long longObjType = HelperCOM.generateSubKey(objectType);
        final Integer id = this.fastID.get(longObjType);
        return (id == null) ? this.addNew(longObjType) : id;
    }

    public synchronized ObjectType getObjectType(final Integer id) throws Exception {
        final Long objectType = this.fastIDreverse.get(id);

        if (objectType == null) {
            throw new Exception();
        }

        return HelperCOM.objectTypeId2objectType(objectType);
    }

}
