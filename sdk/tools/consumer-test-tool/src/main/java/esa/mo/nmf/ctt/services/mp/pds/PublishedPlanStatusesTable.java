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
package esa.mo.nmf.ctt.services.mp.pds;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mp.plandistribution.PlanDistributionHelper;
import org.ccsds.moims.mo.mp.plandistribution.consumer.PlanDistributionAdapter;
import org.ccsds.moims.mo.mp.structures.PlanUpdateDetails;
import org.ccsds.moims.mo.mp.structures.PlanUpdateDetailsList;
import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.mp.impl.com.COMObjectIdHelper;
import esa.mo.mp.impl.consumer.PlanDistributionConsumerServiceImpl;
import esa.mo.mp.impl.util.MOFactory;
import esa.mo.nmf.ctt.utils.SharedTablePanel;

public class PublishedPlanStatusesTable extends SharedTablePanel {

    private static final Logger LOGGER = Logger.getLogger(PublishedPlansTable.class.getName());

    private final ArchiveConsumerServiceImpl archiveService;
    private final PlanDistributionConsumerServiceImpl pdsService;

    public PublishedPlanStatusesTable(final ArchiveConsumerServiceImpl archiveService, final PlanDistributionConsumerServiceImpl pdsService) {
        super(archiveService);
        this.archiveService = archiveService;
        this.pdsService = pdsService;
    }

    @Override
    public void addEntry(final Identifier identity, final ArchivePersistenceObject comObject) {
        // Not used
    }

    public void addEntry(final IdentifierList domain, final Long instanceId, final PlanUpdateDetails update) {

        try {
            semaphore.acquire();
        } catch (final InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        final ObjectType updateObjectType = PlanDistributionHelper.PLANUPDATE_OBJECT_TYPE;
        final LongList objectIds = new LongList();
        objectIds.add(0L);
        final List<ArchivePersistenceObject> updateObjects = HelperArchive
            .getArchiveCOMObjectList(this.archiveService.getArchiveStub(), updateObjectType, domain, objectIds);

        ArchivePersistenceObject comObject = null;
        if (updateObjects != null) {
            for (final ArchivePersistenceObject updateObject : updateObjects) {
                final PlanUpdateDetails archiveUpdate = (PlanUpdateDetails) updateObject.getObject();
                if (Objects.equals(archiveUpdate, update)) {
                    comObject = updateObject;
                }
            }
        }

        tableData.addRow(new Object[]{
            HelperTime.time2readableString(update.getTimestamp()),
            instanceId,
            update.getIsAlternate(),
            update.getStatus(),
            update.getTerminationInfo()
        });

        comObjects.add(comObject);
        semaphore.release();
    }

    @Override
    public void defineTableContent() {
        final String[] tableCol = new String[]{
            "Timestamp", "Plan Version ID", "Is alternate",
            "Plan Status", "Termination info"
        };

        tableData = new javax.swing.table.DefaultTableModel(
            new Object[][]{}, tableCol) {
                Class[] types = new Class[]{
                    java.lang.String.class, java.lang.Long.class, java.lang.String.class,
                    java.lang.String.class, java.lang.String.class
                };

                @Override               //all cells false
                public boolean isCellEditable(final int row, final int column) {
                    return false;
                }

                @Override
                public Class getColumnClass(final int columnIndex) {
                    return types[columnIndex];
                }
        };

        super.getTable().setModel(tableData);
    }

    public void monitorPlanStatuses() throws MALInteractionException, MALException {
        // Subscribe to Plan Status updates
        final Subscription subscription = MOFactory.createSubscription();
        this.pdsService.getPlanDistributionStub().monitorPlanStatusRegister(subscription, new PlanMonitor());
    }

    class PlanMonitor extends PlanDistributionAdapter {

        @Override
        public void monitorPlanStatusNotifyReceived(final MALMessageHeader msgHeader, final Identifier identifier, final UpdateHeaderList headerList, final ObjectIdList planVersionIdList, final PlanUpdateDetailsList updateList, final Map qosProperties) {
            for (int index = 0; index < headerList.size(); index++) {
                final ObjectId planVersionId = planVersionIdList.get(index);
                final PlanUpdateDetails update = updateList.get(index);

                final IdentifierList domain = msgHeader.getDomain();

                final Long instanceId = COMObjectIdHelper.getInstanceId(planVersionId);

                addEntry(domain, instanceId, update);
            }
        }
    }
}
