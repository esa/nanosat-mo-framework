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

import esa.mo.com.impl.archive.entities.DomainHolderEntity;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.helpers.HelperMisc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Query;
import org.ccsds.moims.mo.mal.structures.IdentifierList;

/**
 *
 *
 */
public class FastDomain {

    private final static String QUERY_DELETE_DOMAIN = "DELETE FROM DomainHolderEntity";
    private final static String QUERY_SELECT_DOMAIN = "SELECT PU FROM DomainHolderEntity PU";
    private final DatabaseBackend dbBackend;
    private AtomicInteger uniqueId = new AtomicInteger(0);
    private HashMap<IdentifierList, Integer> fastID;
    private HashMap<Integer, IdentifierList> fastIDreverse;

    public FastDomain(final DatabaseBackend dbBackend) {
        this.fastID = new HashMap<IdentifierList, Integer>();
        this.fastIDreverse = new HashMap<Integer, IdentifierList>();
        this.dbBackend = dbBackend;
        this.loadDomains();
    }

    public synchronized void resetFastDomain() {
        this.fastID = new HashMap<IdentifierList, Integer>();
        this.fastIDreverse = new HashMap<Integer, IdentifierList>();
        uniqueId = new AtomicInteger(0);
        
        dbBackend.getEM().getTransaction().begin();
        dbBackend.getEM().createQuery(QUERY_DELETE_DOMAIN).executeUpdate();
        dbBackend.getEM().getTransaction().commit();
    }

    private void loadDomains() {
        // Retrieve all the ids and domains from the Database
        dbBackend.createEntityManager();

        // Get All the domains available
        Query query = dbBackend.getEM().createQuery(QUERY_SELECT_DOMAIN);
        List resultList = query.getResultList();
        ArrayList<DomainHolderEntity> domainHolderEntities = new ArrayList<DomainHolderEntity>();
        domainHolderEntities.addAll(resultList);
        dbBackend.closeEntityManager();

        int max = 0;

        // Populate the variables on this class
        for (int i = 0; i < domainHolderEntities.size(); i++) {
            final IdentifierList domain = HelperMisc.domainId2domain(domainHolderEntities.get(i).getDomainString());
            final Integer id = domainHolderEntities.get(i).getId();
            this.fastID.put(domain, id);
            this.fastIDreverse.put(id, domain);

            if (id > max) {
                max = id; // Get the maximum value
            }
        }

        uniqueId = new AtomicInteger(max);
    }

    public synchronized boolean exists(final IdentifierList domain) {
        return (this.fastID.get(domain) != null);
    }

    public synchronized boolean exists(final Integer domainId) {
        return (this.fastIDreverse.get(domainId) != null);
    }

    private Integer addNewDomain(final IdentifierList domain) {
        final int domainId = uniqueId.incrementAndGet();
        this.fastID.put(domain, domainId);
        this.fastIDreverse.put(domainId, domain);

        dbBackend.createEntityManager();

        // Create Entity
        DomainHolderEntity domainEntity = new DomainHolderEntity(domainId, HelperMisc.domain2domainId(domain));

        // Add it to the table
        dbBackend.getEM().getTransaction().begin();
        dbBackend.getEM().persist(domainEntity);
        dbBackend.getEM().getTransaction().commit();
        dbBackend.closeEntityManager();

        return domainId;
    }

    public synchronized Integer getDomainId(final IdentifierList domain) {
        final Integer id = this.fastID.get(domain);
        return (id == null) ? this.addNewDomain(domain) : id;
    }

    public synchronized IdentifierList getDomain(final Integer id) throws Exception {
        final IdentifierList domain = this.fastIDreverse.get(id);

        if (domain == null) {
            throw new Exception();
        }

        return domain;
    }
/*
    @Deprecated
    public synchronized String getDomainsForQuery(IdentifierList domainQuery) {

        final boolean domainContainsWildcard = HelperCOM.domainContainsWildcard(domainQuery);

        if (!domainContainsWildcard) { // Direct match!
            return fastID.get(domainQuery).toString();
        }

        // It is not a direct match! We need to iterate on all 
        // the domain to find the ones that match the query!
        String domainIdsList = "";
        
        Set<IdentifierList> domains = fastID.keySet();

        for (IdentifierList domain : domains) {
            if (HelperCOM.domainMatchesWildcardDomain(domain, domainQuery)) {  // Does the domain matches the wildcard?
                domainIdsList += fastID.get(domain) + ",";
            }
        }
        
        // Removing the last comma:
        if(domainIdsList.length() != 0){ 
            domainIdsList = domainIdsList.substring(0, domainIdsList.length()-1);
        }

        return domainIdsList;
    }

    @Deprecated
    public synchronized Integer getDomainIdsForQuery(IdentifierList domainQuery) {

        final boolean domainContainsWildcard = HelperCOM.domainContainsWildcard(domainQuery);

        if (!domainContainsWildcard) { // Direct match or any!
            if(domainQuery != null){
                Integer aaa = fastID.get(domainQuery);
                if (aaa == null){
                    Logger.getLogger(FastDomain.class.getName()).log(Level.SEVERE, "aaa is null with domainQuery: " + domainQuery.toString());
                    return this.addNewDomain(domainQuery);
                }
                
                return fastID.get(domainQuery);  // Direct match and not null
            }
        }

        // It is not a direct match! We need to iterate on all 
        // the domain to find the ones that match the query!
        String domainIdsList = "";
        
        Set<IdentifierList> domains = fastID.keySet();
        
        if(domainQuery == null){
            for (IdentifierList domain : domains) {
                domainIdsList += fastID.get(domain) + ",";
            }
        }else{
            for (IdentifierList domain : domains) {
                if (HelperCOM.domainMatchesWildcardDomain(domain, domainQuery)) {  // Does the domain matches the wildcard?
                    domainIdsList += fastID.get(domain) + ",";
                }
            }
        }
        
        // Removing the last comma:
        if(domainIdsList.length() != 0){ 
            domainIdsList = domainIdsList.substring(0, domainIdsList.length() - 1);
        }

//        return domainIdsList;
        return null;
    }
*/
}
