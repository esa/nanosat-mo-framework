/* ----------------------------------------------------------------------------
 * Copyright (C) 2022      European Space Agency
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
package esa.mo.com.impl.archive.db;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.PaginationFilter;
import org.ccsds.moims.mo.com.archive.structures.QueryFilter;
import org.ccsds.moims.mo.mal.structures.IntegerList;

public abstract class CallableGenericQuery<T> implements Callable<T> {

    public static Logger LOGGER = Logger.getLogger(CallableDeleteQuery.class.getName());

    protected final TransactionsProcessor transactionsProcessor;
    private final IntegerList objTypeIds;
    private final ArchiveQuery archiveQuery;
    private final IntegerList domainIds;
    private final Integer providerURIId;
    private final Integer networkId;
    private final SourceLinkContainer sourceLink;
    private final QueryFilter filter;

    public CallableGenericQuery(TransactionsProcessor transactionsProcessor, final IntegerList objTypeIds,
        final ArchiveQuery archiveQuery, final IntegerList domainIds, final Integer providerURIId,
        final Integer networkId, final SourceLinkContainer sourceLink, final QueryFilter filter) {
        this.transactionsProcessor = transactionsProcessor;
        this.objTypeIds = objTypeIds;
        this.archiveQuery = archiveQuery;
        this.domainIds = domainIds;
        this.providerURIId = providerURIId;
        this.networkId = networkId;
        this.sourceLink = sourceLink;
        this.filter = filter;
    }

    protected abstract T innerCall(String queryString);

    protected abstract String assembleQueryPrefix(String fieldsList);

    @Override
    public T call() {
        final boolean relatedContainsWildcard = (archiveQuery.getRelated().equals((long) 0));
        final boolean startTimeContainsWildcard = (archiveQuery.getStartTime() == null);
        final boolean endTimeContainsWildcard = (archiveQuery.getEndTime() == null);
        final boolean providerURIContainsWildcard = (archiveQuery.getProvider() == null);
        final boolean networkContainsWildcard = (archiveQuery.getNetwork() == null);

        final boolean sourceContainsWildcard = (archiveQuery.getSource() == null);
        boolean sourceObjIdContainsWildcard = true;

        this.transactionsProcessor.dbBackend.createIndexesIfFirstTime();

        if (!sourceContainsWildcard) {
            sourceObjIdContainsWildcard = (archiveQuery.getSource().getKey().getInstId() == null || archiveQuery
                .getSource().getKey().getInstId() == 0);
        }

        // Generate the query string
        String fieldsList = "objectTypeId, domainId, objId, timestampArchiveDetails, providerURI, " +
            "network, sourceLinkObjectTypeId, sourceLinkDomainId, sourceLinkObjId, relatedLink, objBody";
        String queryString = assembleQueryPrefix(fieldsList);

        queryString += "WHERE ";

        queryString += CallableGenericQuery.generateQueryStringFromLists("domainId", domainIds);
        queryString += CallableGenericQuery.generateQueryStringFromLists("objectTypeId", objTypeIds);

        queryString += (relatedContainsWildcard) ? "" : "relatedLink=" + archiveQuery.getRelated() + " AND ";
        queryString += (startTimeContainsWildcard) ? "" : "timestampArchiveDetails>=" + archiveQuery.getStartTime()
            .getValue() + " AND ";
        queryString += (endTimeContainsWildcard) ? "" : "timestampArchiveDetails<=" + archiveQuery.getEndTime()
            .getValue() + " AND ";
        queryString += (providerURIContainsWildcard) ? "" : "providerURI=" + providerURIId + " AND ";
        queryString += (networkContainsWildcard) ? "" : "network=" + networkId + " AND ";

        if (!sourceContainsWildcard) {
            queryString += CallableGenericQuery.generateQueryStringFromLists("sourceLinkObjectTypeId", sourceLink
                .getObjectTypeIds());
            queryString += CallableGenericQuery.generateQueryStringFromLists("sourceLinkDomainId", sourceLink
                .getDomainIds());
            queryString += (sourceObjIdContainsWildcard) ? "" : "sourceLinkObjId=" + sourceLink.getObjId() + " AND ";
        }

        queryString = queryString.substring(0, queryString.length() - 4);

        // A dedicated PaginationFilter for this particular COM Archive implementation
        // was created and implemented
        if (filter != null) {
            if (filter instanceof PaginationFilter) {
                PaginationFilter pfilter = (PaginationFilter) filter;

                // Double check if the filter fields are really not null
                if (pfilter.getLimit() != null && pfilter.getOffset() != null) {
                    String sortOrder = "ASC ";
                    if (archiveQuery.getSortOrder() != null) {
                        sortOrder = (archiveQuery.getSortOrder()) ? "ASC " : "DESC ";
                    }

                    queryString += "ORDER BY timestampArchiveDetails " + sortOrder + "LIMIT " + pfilter.getLimit()
                        .getValue() + " OFFSET " + pfilter.getOffset().getValue();
                }
            }
        }

        try {
            this.transactionsProcessor.dbBackend.getAvailability().acquire();
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        T ret = innerCall(queryString);
        this.transactionsProcessor.dbBackend.getAvailability().release();
        return ret;
        // return perObjs;
    }

    public static String generateQueryStringFromLists(final String field, final IntegerList list) {
        if (list.isEmpty()) {
            return "";
        }

        if (list.size() == 1) {
            return field + "=" + list.get(0) + " AND ";
        }

        StringBuilder stringForWildcards = new StringBuilder("(");

        for (Integer id : list) {
            stringForWildcards.append(field).append("=").append(id).append(" OR ");
        }

        // Remove the " OR " par of it!
        stringForWildcards = new StringBuilder(stringForWildcards.substring(0, stringForWildcards.length() - 4));

        return stringForWildcards + ") AND ";
    }

}
