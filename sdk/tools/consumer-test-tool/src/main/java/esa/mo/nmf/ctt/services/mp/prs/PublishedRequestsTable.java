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
package esa.mo.nmf.ctt.services.mp.prs;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mp.planningrequest.PlanningRequestHelper;
import org.ccsds.moims.mo.mp.planningrequest.consumer.PlanningRequestAdapter;
import org.ccsds.moims.mo.mp.structures.RequestUpdateDetails;
import org.ccsds.moims.mo.mp.structures.RequestUpdateDetailsList;
import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.mp.impl.com.COMObjectIdHelper;
import esa.mo.mp.impl.consumer.PlanningRequestConsumerServiceImpl;
import esa.mo.mp.impl.util.MOFactory;
import esa.mo.nmf.ctt.utils.SharedTablePanel;

public class PublishedRequestsTable extends SharedTablePanel {

    private static final Logger LOGGER = Logger.getLogger(PublishedRequestsTable.class.getName());

    private final ArchiveConsumerServiceImpl archiveService;
    private final PlanningRequestConsumerServiceImpl planningRequestService;

    public PublishedRequestsTable(final ArchiveConsumerServiceImpl archiveService, final PlanningRequestConsumerServiceImpl planningRequestService) {
        super(archiveService);
        this.archiveService = archiveService;
        this.planningRequestService = planningRequestService;
    }

    @Override
    public void addEntry(final Identifier identity, final ArchivePersistenceObject comObject) {
        // Not used
    }

    public void addEntry(final IdentifierList domain, final Identifier identity, final RequestUpdateDetails update) {

        try {
            semaphore.acquire();
        } catch (final InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        final ObjectType updateObjectType = PlanningRequestHelper.REQUESTSTATUSUPDATE_OBJECT_TYPE;
        final LongList objectIds = new LongList();
        objectIds.add(0L);
        final List<ArchivePersistenceObject> updateObjects = HelperArchive
            .getArchiveCOMObjectList(this.archiveService.getArchiveStub(), updateObjectType, domain, objectIds);

        ArchivePersistenceObject comObject = null;
        if (updateObjects != null) {
            for (final ArchivePersistenceObject updateObject : updateObjects) {
                final RequestUpdateDetails archiveUpdate = (RequestUpdateDetails) updateObject.getObject();
                if (Objects.equals(archiveUpdate, update)) {
                    comObject = updateObject;
                }
            }
        }

        tableData.addRow(new Object[]{
            HelperTime.time2readableString(update.getTimestamp()),
            identity,
            COMObjectIdHelper.getInstanceId(update.getRequestId()),
            COMObjectIdHelper.getInstanceId(update.getPlanRef()),
            update.getStatus(),
            update.getErrCode(),
            update.getErrInfo()
        });

        comObjects.add(comObject);
        semaphore.release();
    }

    @Override
    public void defineTableContent() {
        final String[] tableCol = new String[]{
            "Timestamp", "Request Identity", "Request Version ID",
            "Plan Ref ID", "Request Status", "Error code",
            "Error info"
        };

        tableData = new javax.swing.table.DefaultTableModel(
            new Object[][]{}, tableCol) {
                Class[] types = new Class[]{
                    java.lang.String.class, java.lang.String.class, java.lang.Long.class,
                    java.lang.Long.class, java.lang.String.class, java.lang.String.class,
                    java.lang.String.class,
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

    public void monitorRequests() throws MALInteractionException, MALException {
        // Subscribe to Request updates
        final Subscription subscription = MOFactory.createSubscription();
        this.planningRequestService.getPlanningRequestStub().monitorRequestsRegister(subscription, new PlanningRequestsMonitor());
    }

    class PlanningRequestsMonitor extends PlanningRequestAdapter {

        private int row = 0;

        @Override
        public void monitorRequestsNotifyReceived(final MALMessageHeader msgHeader, final Identifier identifier,
                                                  final UpdateHeaderList headerList, final RequestUpdateDetailsList updateList,
                                                  final Map qosProperties) {
            for (int index = 0; index < updateList.size(); index++) {
                final UpdateHeader updateHeader = headerList.get(index);
                final RequestUpdateDetails update = updateList.get(index);

                final IdentifierList domain = msgHeader.getDomain();
                final Identifier identity = updateHeader.getKey().getFirstSubKey();

                addEntry(domain, identity, update);
            }
        }
    }
}
