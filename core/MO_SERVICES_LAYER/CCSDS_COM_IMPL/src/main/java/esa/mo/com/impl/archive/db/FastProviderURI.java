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
package esa.mo.com.impl.archive.db;

import esa.mo.com.impl.archive.entities.ProviderURIHolderEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.persistence.Query;
import org.ccsds.moims.mo.mal.structures.IntegerList;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 *
 *
 */
public class FastProviderURI {

    private final static String QUERY_DELETE_PROVIDERURI = "DELETE FROM ProviderURIHolderEntity";
    private final static String QUERY_SELECT_PROVIDERURI = "SELECT PU FROM ProviderURIHolderEntity PU";
    private final DatabaseBackend dbBackend;
    private AtomicInteger uniqueId = new AtomicInteger(0);
    private HashMap<String, Integer> fastID;
    private HashMap<Integer, String> fastIDreverse;

    public FastProviderURI(final DatabaseBackend dbBackend) {
        this.fastID = new HashMap<String, Integer>();
        this.fastIDreverse = new HashMap<Integer, String>();
        this.dbBackend = dbBackend;
        this.loadProviderURIs();
    }

    public synchronized void resetFastProviderURI() {
        this.fastID = new HashMap<String, Integer>();
        this.fastIDreverse = new HashMap<Integer, String>();
        uniqueId = new AtomicInteger(0);
        
        // To Do: Erase it from the table
        dbBackend.getEM().getTransaction().begin();
        dbBackend.getEM().createQuery(QUERY_DELETE_PROVIDERURI).executeUpdate();
        dbBackend.getEM().getTransaction().commit();
    }

    private void loadProviderURIs() {
        // Retrieve all the ids and providerURIs from the Database
        dbBackend.createEntityManager();

        // Get All the providerURIs available
        Query query = dbBackend.getEM().createQuery(QUERY_SELECT_PROVIDERURI);

        List resultList = query.getResultList();
        ArrayList<ProviderURIHolderEntity> providerURIHolderEntities = new ArrayList<ProviderURIHolderEntity>();
        providerURIHolderEntities.addAll(resultList);

        dbBackend.closeEntityManager();

        final IntegerList ids = new IntegerList();
        final StringList providerURIs = new StringList();

        // From the list of Entities to separate lists of ids and providerURIs
        for (int i = 0; i < providerURIHolderEntities.size(); i++) {
            ids.add(providerURIHolderEntities.get(i).getId());
            providerURIs.add(providerURIHolderEntities.get(i).getProviderURIString());
        }

        int max = 0;

        // Populate the variables on this class
        for (int i = 0; i < ids.size(); i++) {
            this.fastID.put(providerURIs.get(i), ids.get(i));
            this.fastIDreverse.put(ids.get(i), providerURIs.get(i));

            if (ids.get(i) > max) { // Get the maximum value
                max = ids.get(i);
            }
        }

        uniqueId = new AtomicInteger(max);
    }

    public synchronized boolean exists(final String providerURI) {
        return (this.fastID.get(providerURI) != null);
    }

    public synchronized boolean exists(final Integer providerURIId) {
        return (this.fastIDreverse.get(providerURIId) != null);
    }

    private Integer addNewProviderURI(final String providerURI) {
        final int providerURIId = uniqueId.incrementAndGet();
        dbBackend.createEntityManager();

        // Create Entity
        ProviderURIHolderEntity providerURIEntity = new ProviderURIHolderEntity(providerURIId, providerURI);

        // Add it to the table
        dbBackend.getEM().getTransaction().begin();
        dbBackend.getEM().persist(providerURIEntity);
        dbBackend.getEM().getTransaction().commit();
        dbBackend.closeEntityManager();

        this.fastID.put(providerURI, providerURIId);
        this.fastIDreverse.put(providerURIId, providerURI);

        return providerURIId;
    }

    public synchronized Integer getProviderURIId(final URI providerURI) {
        final Integer id = this.fastID.get(providerURI.getValue());
        return (id == null) ? this.addNewProviderURI(providerURI.getValue()) : id;
    }
    
    public synchronized URI getProviderURI(final Integer id) throws Exception {
        final URI providerURI = new URI(this.fastIDreverse.get(id));

        if (providerURI == null) {
            throw new Exception();
        }

        return providerURI;
    }
}
