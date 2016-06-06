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
package esa.mo.com.impl.provider;

import esa.mo.com.impl.entities.DomainHolderEntity;
import esa.mo.helpertools.helpers.HelperMisc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.StringList;

/**
 *
 *
 */
public class ArchiveFastDomain {

    private final HashMap<IdentifierList, Long> fastID;
    private final HashMap<Long, IdentifierList> fastIDreverse;
    private final EntityManagerFactory emf;
    private final Semaphore emAvailability;
    private EntityManager em;
       
    public ArchiveFastDomain(final EntityManagerFactory emf, final Semaphore emAvailability) {
        this.fastID = new HashMap<IdentifierList, Long>();
        this.fastIDreverse = new HashMap<Long, IdentifierList>();
        this.emf = emf;
        this.emAvailability = emAvailability;
    }
    
    public boolean exists(final IdentifierList domain) {
        return (this.fastID.get(domain) != null);
    }
    
    public boolean exists(final Long domainId) {
        return (this.fastIDreverse.get(domainId) != null);
    }
    
    private void populate(){
        
        // Retrieve all the ids and domains from the Database
        try {
            this.emAvailability.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(ArchiveManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.em = this.emf.createEntityManager();

        // Get All the domains available
        Query query = this.em.createQuery("SELECT PU FROM DomainHolderEntity PU");

        List resultList = query.getResultList();
        ArrayList<DomainHolderEntity> domainHolderEntities = new ArrayList<DomainHolderEntity>();
        domainHolderEntities.addAll(resultList);
                 
        final LongList ids = new LongList();
        final StringList domains = new StringList();

        // From the list of Entities to seperate lists of ids and domains
        for (int i = 0; i < domainHolderEntities.size(); i++){
            ids.add(domainHolderEntities.get(i).getId());
            domains.add(domainHolderEntities.get(i).getDomainString());
        }

        // Populate the variables on this class
        for (int i = 0; i < ids.size(); i++){
            final IdentifierList domain = HelperMisc.domainId2domain(domains.get(i));
            this.fastID.put(domain, ids.get(i));
            this.fastIDreverse.put(ids.get(i), domain);
        }

        this.em.close();
        this.emAvailability.release();
        
    }
    
    public void addNewDomain(final IdentifierList domain){       
        try {
            this.emAvailability.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(ArchiveManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.em = this.emf.createEntityManager();    
        
        // Create Entity
        DomainHolderEntity domainEntity = new DomainHolderEntity(new Long(fastID.size() + 1), HelperMisc.domain2domainId(domain));
        
        // Add it to the table
        this.em.getTransaction().begin();
        this.em.persist(domainEntity);
        this.em.getTransaction().commit();
        
        this.em.close();
        this.emAvailability.release();        
    }

    
    public Long getDomainID(final IdentifierList domain) throws Exception {
        final Long id = this.fastID.get(domain);
        
        if (id == null){
            throw new Exception();
        }

        return id;
    }

    public IdentifierList getDomain(final Long id) throws Exception {
        final IdentifierList domain = this.fastIDreverse.get(id);
        
        if (domain == null){
            throw new Exception();
        }

        return domain;
    }
    
    public boolean changeSingleDomain(final IdentifierList newDomain){
        // A single domain is not available. There are more than 1...
        if (this.fastID.size() != 1){
            return false;
        }

        final Long id = new Long (1);
        final DomainHolderEntity oldDomainEntity = this.em.find(DomainHolderEntity.class, id);

        // Create new Entity
        DomainHolderEntity newDomainEntity = new DomainHolderEntity(new Long(fastID.size() + 1), HelperMisc.domain2domainId(newDomain));
        
        // Lock it
        try {
            this.emAvailability.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(ArchiveManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.em = this.emf.createEntityManager();    
        this.em.getTransaction().begin();
        this.em.remove(oldDomainEntity);
        this.em.getTransaction().commit();
        this.em.persist(newDomainEntity); 
        this.em.getTransaction().commit();
        
        this.em.close();
        this.emAvailability.release();        
        
        return true;
    }

}
