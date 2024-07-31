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
package esa.mo.nmf.ctt.services.mp.pec;

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
import org.ccsds.moims.mo.mp.planedit.PlanEditHelper;
import org.ccsds.moims.mo.mp.planexecutioncontrol.consumer.PlanExecutionControlAdapter;
import org.ccsds.moims.mo.mp.structures.ActivityInstanceDetails;
import org.ccsds.moims.mo.mp.structures.ActivityUpdateDetails;
import org.ccsds.moims.mo.mp.structures.ActivityUpdateDetailsList;
import org.ccsds.moims.mo.mp.structures.TimeTrigger;
import org.ccsds.moims.mo.mp.structures.Trigger;
import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.mp.impl.com.COMObjectIdHelper;
import esa.mo.mp.impl.consumer.PlanExecutionControlConsumerServiceImpl;
import esa.mo.mp.impl.util.MOFactory;
import esa.mo.mp.impl.util.MPPolyFix;
import esa.mo.nmf.ctt.utils.SharedTablePanel;

public class PublishedActivityUpdatesTable extends SharedTablePanel {

    private static final Logger LOGGER = Logger.getLogger(PublishedActivityUpdatesTable.class.getName());

    private final ArchiveConsumerServiceImpl archiveService;
    private final PlanExecutionControlConsumerServiceImpl planExecutionControlService;

    public PublishedActivityUpdatesTable(ArchiveConsumerServiceImpl archiveService,
        PlanExecutionControlConsumerServiceImpl planExecutionControlService) {
        super(archiveService);
        this.archiveService = archiveService;
        this.planExecutionControlService = planExecutionControlService;
    }

    @Override
    public void addEntry(Identifier identity, ArchivePersistenceObject comObject) {
        // Not used
    }

    public void addEntry(IdentifierList domain, Identifier identity, Long instanceId, ActivityUpdateDetails update) {

        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        String plannedTrigger = null;
        String actualTime = null;
        if (update.getStart() != null) {
            Trigger trigger = MPPolyFix.decode(update.getStart());
            actualTime = HelperTime.time2readableString(trigger.getTime());
            if (trigger instanceof TimeTrigger) {
                TimeTrigger timeTrigger = (TimeTrigger) trigger;
                plannedTrigger = HelperTime.time2readableString(timeTrigger.getTriggerTime());
            } else {
                plannedTrigger = "Unsupported type";
            }
        }

        ObjectType updateObjectType = PlanEditHelper.ACTIVITYUPDATE_OBJECT_TYPE;
        LongList objectIds = new LongList();
        objectIds.add(0L);
        List<ArchivePersistenceObject> updateObjects = HelperArchive.getArchiveCOMObjectList(this.archiveService
            .getArchiveStub(), updateObjectType, domain, objectIds);

        ArchivePersistenceObject comObject = null;
        if (updateObjects != null) {
            for (ArchivePersistenceObject updateObject : updateObjects) {
                ActivityUpdateDetails archiveUpdate = (ActivityUpdateDetails) updateObject.getObject();
                if (Objects.equals(archiveUpdate, update)) {
                    comObject = updateObject;
                }
            }
        }

        ObjectType instanceObjectType = PlanEditHelper.ACTIVITYINSTANCE_OBJECT_TYPE;
        ArchivePersistenceObject instanceComObject = HelperArchive.getArchiveCOMObject(this.archiveService
            .getArchiveStub(), instanceObjectType, domain, instanceId);

        String info = "";
        if (instanceComObject != null) {
            ActivityInstanceDetails activityInstance = (ActivityInstanceDetails) instanceComObject.getObject();
            if (activityInstance.getComments() != null) {
                info += activityInstance.getComments();
            }
        }
        if (update.getErrInfo() != null) {
            info += info.length() > 0 ? " - " : "";
            info += update.getErrInfo();
        }

        tableData.addRow(new Object[]{HelperTime.time2readableString(update.getTimestamp()), identity, COMObjectIdHelper
            .getInstanceId(update.getPlanVersionId()), instanceId, update.getStatus(), plannedTrigger, actualTime,
                                      update.getErrCode(), info});

        comObjects.add(comObject);
        semaphore.release();
    }

    @Override
    public void defineTableContent() {
        String[] tableCol = new String[]{"Timestamp", "Activity Identity", "Plan Version Id", "Activity Id",
                                         "Activity Status", "Planned trigger", "Predicted or Actual time", "Error code",
                                         "Information"};

        tableData = new javax.swing.table.DefaultTableModel(new Object[][]{}, tableCol) {
            Class[] types = new Class[]{java.lang.String.class, java.lang.String.class, java.lang.Long.class,
                                        java.lang.Long.class, java.lang.String.class, java.lang.String.class,
                                        java.lang.String.class, java.lang.String.class, java.lang.String.class};

            @Override               //all cells false
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };

        super.getTable().setModel(tableData);
    }

    public void monitorActivities() throws MALInteractionException, MALException {
        // Subscribe to Activity updates
        final Subscription subscription = MOFactory.createSubscription();
        this.planExecutionControlService.getPlanExecutionControlStub().monitorActivitiesRegister(subscription,
            new ActivityUpdatesMonitor());
    }

    class ActivityUpdatesMonitor extends PlanExecutionControlAdapter {

        @Override
        public void monitorActivitiesNotifyReceived(MALMessageHeader msgHeader, Identifier identifier,
            UpdateHeaderList headerList, ActivityUpdateDetailsList updateList, Map qosProperties) {
            for (int index = 0; index < updateList.size(); index++) {
                UpdateHeader updateHeader = headerList.get(index);
                ActivityUpdateDetails update = updateList.get(index);

                IdentifierList domain = msgHeader.getDomain();

                Identifier identity = updateHeader.getKey().getFirstSubKey();
                Long instanceId = updateHeader.getKey().getThirdSubKey();

                addEntry(domain, identity, instanceId, update);
            }
        }
    }
}
