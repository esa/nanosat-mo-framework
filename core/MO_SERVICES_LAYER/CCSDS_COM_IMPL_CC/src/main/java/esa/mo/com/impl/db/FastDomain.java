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
package esa.mo.com.impl.db;

import esa.mo.com.impl.entities.DomainHolderEntity;
import esa.mo.helpertools.helpers.HelperMisc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.persistence.Query;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.IntegerList;
import org.ccsds.moims.mo.mal.structures.StringList;

/**
 *
 *
 */
public class FastDomain {

    private final DatabaseBackend dbBackend;
    private AtomicInteger uniqueId = new AtomicInteger(0);
    private final HashMap<IdentifierList, Integer> fastID;
    private final HashMap<Integer, IdentifierList> fastIDreverse;
       
    public FastDomain(final DatabaseBackend dbBackend) {
        this.fastID = new HashMap<IdentifierList, Integer>();
        this.fastIDreverse = new HashMap<Integer, IdentifierList>();
        this.dbBackend = dbBackend;
        this.loadDomains();
    }
    
    private void loadDomains(){
        // Retrieve all the ids and domains from the Database
        dbBackend.createEntityManager();

        // Get All the domains available
        Query query = dbBackend.getEM().createQuery("SELECT PU FROM DomainHolderEntity PU");

        List resultList = query.getResultList();
        ArrayList<DomainHolderEntity> domainHolderEntities = new ArrayList<DomainHolderEntity>();
        domainHolderEntities.addAll(resultList);
                 
        dbBackend.closeEntityManager();

        final IntegerList ids = new IntegerList();
        final StringList domains = new StringList();

        // From the list of Entities to seperate lists of ids and domains
        for (int i = 0; i < domainHolderEntities.size(); i++){
            ids.add(domainHolderEntities.get(i).getId());
            domains.add(domainHolderEntities.get(i).getDomainString());
        }

        int max = 0;
        
        // Populate the variables on this class
        for (int i = 0; i < ids.size(); i++){
            final IdentifierList domain = HelperMisc.domainId2domain(domains.get(i));
            this.fastID.put(domain, ids.get(i));
            this.fastIDreverse.put(ids.get(i), domain);
            
            if(ids.get(i) > max){ // Get the maximum value
                max = ids.get(i);
            }
        }
        
        uniqueId = new AtomicInteger(0);
    }
    
    public synchronized boolean exists(final IdentifierList domain) {
        return (this.fastID.get(domain) != null);
    }
    
    public synchronized boolean exists(final Integer domainId) {
        return (this.fastIDreverse.get(domainId) != null);
    }
    
    private Integer addNewDomain(final IdentifierList domain){
        final int domainId = uniqueId.incrementAndGet();
        dbBackend.createEntityManager();

        // Create Entity
        DomainHolderEntity domainEntity = new DomainHolderEntity(domainId, HelperMisc.domain2domainId(domain));
        
        // Add it to the table
        dbBackend.getEM().getTransaction().begin();
        dbBackend.getEM().persist(domainEntity);
        dbBackend.getEM().getTransaction().commit();
        dbBackend.closeEntityManager();
        
        this.fastID.put(domain, domainId);
        this.fastIDreverse.put(domainId, domain);
        
        return domainId;
    }
    
    public synchronized Integer getDomainID(final IdentifierList domain) {
        final Integer id = this.fastID.get(domain);
        
        if (id == null){ // Does not exist
            this.addNewDomain(domain);
        }

        return id;
    }

    public synchronized IdentifierList getDomain(final Integer id) throws Exception {
        final IdentifierList domain = this.fastIDreverse.get(id);
        
        if (domain == null){
            throw new Exception();
        }

        return domain;
    }
    
    

/*    
    private boolean changeSingleDomain(final IdentifierList newDomain){
        // A single domain is not available. There are more than 1...
        if (this.fastID.size() != 1){
            return false;
        }

        final Long id = new Long (1);
        dbBackend.createEntityManager();
        
        final DomainHolderEntity oldDomainEntity = dbBackend.getEM().find(DomainHolderEntity.class, id);

        // Create new Entity
        DomainHolderEntity newDomainEntity = new DomainHolderEntity(fastID.size() + 1, HelperMisc.domain2domainId(newDomain));
        
        dbBackend.getEM().getTransaction().begin();
        dbBackend.getEM().remove(oldDomainEntity);
        dbBackend.getEM().getTransaction().commit();
        dbBackend.getEM().persist(newDomainEntity); 
        dbBackend.getEM().getTransaction().commit();
        
        dbBackend.closeEntityManager();
        
        return true;
    }
*/
    
}
