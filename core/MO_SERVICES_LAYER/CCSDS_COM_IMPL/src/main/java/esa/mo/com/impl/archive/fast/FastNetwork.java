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
import esa.mo.com.impl.archive.entities.NetworkHolderEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.persistence.Query;
import org.ccsds.moims.mo.mal.structures.Identifier;

/**
 * Holds the set of networks that the database contains in its dedicated table
 * and avoids constant checking on it which makes things go much faster.
 */
public class FastNetwork {

    private final static String QUERY_DELETE_NETWORK = "DELETE FROM NetworkHolderEntity";
    private final static String QUERY_SELECT_NETWORK = "SELECT PU FROM NetworkHolderEntity PU";
    private final DatabaseBackend dbBackend;
    private AtomicInteger uniqueId = new AtomicInteger(0);
    private HashMap<String, Integer> fastID;
    private HashMap<Integer, String> fastIDreverse;

    public FastNetwork(final DatabaseBackend dbBackend) {
        this.fastID = new HashMap<String, Integer>();
        this.fastIDreverse = new HashMap<Integer, String>();
        this.dbBackend = dbBackend;
    }

    public synchronized void resetFastNetwork() {
        this.fastID = new HashMap<String, Integer>();
        this.fastIDreverse = new HashMap<Integer, String>();
        uniqueId = new AtomicInteger(0);

        dbBackend.getEM().getTransaction().begin();
        dbBackend.getEM().createQuery(QUERY_DELETE_NETWORK).executeUpdate();
        dbBackend.getEM().getTransaction().commit();
    }

    public synchronized void init() {
        // Retrieve all the ids and networks from the Database
        dbBackend.createEntityManager();

        // Get All the networks available
        Query query = dbBackend.getEM().createQuery(QUERY_SELECT_NETWORK);
        List resultList = query.getResultList();
        ArrayList<NetworkHolderEntity> networksHolderEntities = new ArrayList<NetworkHolderEntity>();
        networksHolderEntities.addAll(resultList);

        dbBackend.closeEntityManager();

        int max = 0;

        // Populate the variables on this class
        for (int i = 0; i < networksHolderEntities.size(); i++) {
            this.fastID.put(networksHolderEntities.get(i).getNetworkString(), networksHolderEntities.get(i).getId());
            this.fastIDreverse.put(networksHolderEntities.get(i).getId(), networksHolderEntities.get(i).getNetworkString());

            if (networksHolderEntities.get(i).getId() > max) {
                max = networksHolderEntities.get(i).getId(); // Get the maximum value
            }
        }

        uniqueId = new AtomicInteger(max);
    }

    public synchronized boolean exists(final String network) {
        return (this.fastID.get(network) != null);
    }

    public synchronized boolean exists(final Integer networkId) {
        return (this.fastIDreverse.get(networkId) != null);
    }

    private Integer addNewNetwork(final String network) {
        final int networkId = uniqueId.incrementAndGet();
        dbBackend.createEntityManager();

        // Create Entity
        NetworkHolderEntity networkEntity = new NetworkHolderEntity(networkId, network);

        // Add it to the table
        dbBackend.getEM().getTransaction().begin();
        dbBackend.getEM().persist(networkEntity);
        dbBackend.getEM().getTransaction().commit();
        dbBackend.closeEntityManager();

        this.fastID.put(network, networkId);
        this.fastIDreverse.put(networkId, network);

        return networkId;
    }

    public synchronized Integer getNetworkId(final Identifier network) {
        final Integer id = this.fastID.get(network.getValue());
        return (id == null) ? this.addNewNetwork(network.getValue()) : id;
    }

    public synchronized Identifier getNetwork(final Integer id) throws Exception {
        final String network = this.fastIDreverse.get(id);

        if (network == null) {
            throw new Exception();
        }

        return new Identifier(network);
    }
}
