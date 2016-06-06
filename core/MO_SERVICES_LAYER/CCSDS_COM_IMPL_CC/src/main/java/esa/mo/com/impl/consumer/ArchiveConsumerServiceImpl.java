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
package esa.mo.com.impl.consumer;

import esa.mo.helpertools.connections.ConfigurationConsumer;
import esa.mo.helpertools.misc.ConsumerServiceImpl;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperTime;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.ArchiveHelper;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveStub;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.CompositeFilter;
import org.ccsds.moims.mo.com.archive.structures.ExpressionOperator;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.structures.Identifier;

/**
 *
 * @author Cesar Coelho
 */
public class ArchiveConsumerServiceImpl extends ConsumerServiceImpl {

    private ArchiveStub archiveService = null;
    private String retrieveTBox = "";
    private String countTBox = "";
    private ConfigurationConsumer configuration = new ConfigurationConsumer();

    @Override
    public Object generateServiceStub(MALConsumer tmConsumer) {
        return new ArchiveStub(tmConsumer);
    }

    @Deprecated
    protected String getRetrieveTBox() {
        return retrieveTBox;
    }

    @Deprecated
    protected String getCountTBox() {
        return countTBox;
    }

    public ArchiveStub getArchiveStub() {
        return archiveService;
    }

    public ArchiveConsumerServiceImpl(SingleConnectionDetails connectionDetails) throws MALException, MalformedURLException {

        if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
            MALHelper.init(MALContextFactory.getElementFactoryRegistry());
        }

        if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
            COMHelper.init(MALContextFactory.getElementFactoryRegistry());
        }

        try {
            ArchiveHelper.init(MALContextFactory.getElementFactoryRegistry());
        } catch (MALException ex) {
        }

        this.connectionDetails = connectionDetails;

        // Close old connection
        if (tmConsumer != null) {
            try {
                tmConsumer.close();
            } catch (MALException ex) {
                Logger.getLogger(ArchiveConsumerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        tmConsumer = connection.startService(
                this.connectionDetails.getProviderURI(),
                this.connectionDetails.getBrokerURI(),
                this.connectionDetails.getDomain(),
                ArchiveHelper.ARCHIVE_SERVICE);

        this.archiveService = new ArchiveStub(tmConsumer);

    }

    @Deprecated
    public ArchiveDetails generateArchiveDetails(Long objId) {
        // ArchiveDetails
        ArchiveDetails archiveDetails = new ArchiveDetails();
        archiveDetails.setDetails(new ObjectDetails());
        archiveDetails.setInstId(objId);
        archiveDetails.setNetwork(configuration.getNetwork());
        archiveDetails.setTimestamp(HelperTime.getTimestamp());
        archiveDetails.setProvider(connectionDetails.getProviderURI());
        return archiveDetails;
    }

    @Deprecated
    public static ArchiveQuery generateArchiveQuery() {
        // ArchiveDetails
        ArchiveQuery archiveQuery = new ArchiveQuery();
        archiveQuery.setDomain(null);
        archiveQuery.setNetwork(null);
        archiveQuery.setProvider(null);
        archiveQuery.setRelated(new Long(0));
        archiveQuery.setSource(null);
        archiveQuery.setStartTime(null);
        archiveQuery.setEndTime(null);
        archiveQuery.setSortOrder(null);
        archiveQuery.setSortFieldName(null);

        return archiveQuery;
    }

    @Deprecated
    public static CompositeFilter generateCompositeFilter() {
        CompositeFilter compositeFilter = new CompositeFilter();
        compositeFilter.setFieldName("name");
        compositeFilter.setType(ExpressionOperator.fromNumericValue(ExpressionOperator.EQUAL_NUM_VALUE));
        compositeFilter.setFieldValue(new Identifier("AggregationUpdate"));

        return compositeFilter;
    }

}
