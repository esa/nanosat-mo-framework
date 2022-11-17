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
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mp.plandistribution.PlanDistributionHelper;
import org.ccsds.moims.mo.mp.plandistribution.consumer.PlanDistributionAdapter;
import org.ccsds.moims.mo.mp.structures.PlanVersionDetails;
import org.ccsds.moims.mo.mp.structures.PlanVersionDetailsList;
import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.mp.impl.consumer.PlanDistributionConsumerServiceImpl;
import esa.mo.mp.impl.util.MOFactory;
import esa.mo.nmf.ctt.utils.SharedTablePanel;

public class PublishedPlansTable extends SharedTablePanel {

    private static final Logger LOGGER = Logger.getLogger(PublishedPlansTable.class.getName());

    private final ArchiveConsumerServiceImpl archiveService;
    private final PlanDistributionConsumerServiceImpl pdsService;

    public PublishedPlansTable(ArchiveConsumerServiceImpl archiveService,
        PlanDistributionConsumerServiceImpl pdsService) {
        super(archiveService);
        this.archiveService = archiveService;
        this.pdsService = pdsService;
    }

    @Override
    public void addEntry(Identifier identity, ArchivePersistenceObject comObject) {
        // Not used
    }

    public void addEntry(IdentifierList domain, Time timestamp, Long identityId, Identifier identity, Long instanceId,
        PlanVersionDetails planVersion) {

        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        int plannedActivitiesCount = 0;
        int plannedEventsCount = 0;
        String planDescription = "";
        String planComments = "";
        if (planVersion != null) {
            if (planVersion.getItems() != null) {
                if (planVersion.getItems().getPlannedActivities() != null) {
                    plannedActivitiesCount = planVersion.getItems().getPlannedActivities().size();
                }
                if (planVersion.getItems().getPlannedEvents() != null) {
                    plannedEventsCount = planVersion.getItems().getPlannedEvents().size();
                }
            }
            if (planVersion.getInformation() != null) {
                planDescription = planVersion.getInformation().getDescription();
                planComments = planVersion.getInformation().getComments();
            }
        }

        ObjectType instanceObjectType = PlanDistributionHelper.PLANVERSION_OBJECT_TYPE;
        LongList objectIds = new LongList();
        objectIds.add(0L);
        List<ArchivePersistenceObject> instanceObjects = HelperArchive.getArchiveCOMObjectList(this.archiveService
            .getArchiveStub(), instanceObjectType, domain, objectIds);

        ArchivePersistenceObject comObject = null;
        if (instanceObjects != null) {
            for (ArchivePersistenceObject instanceObject : instanceObjects) {
                PlanVersionDetails archiveInstance = (PlanVersionDetails) instanceObject.getObject();
                if (Objects.equals(archiveInstance, planVersion)) {
                    comObject = instanceObject;
                }
            }
        }

        tableData.addRow(new Object[]{HelperTime.time2readableString(timestamp), identity, identityId, instanceId,
                                      plannedActivitiesCount, plannedEventsCount, planDescription, planComments,});

        comObjects.add(comObject);
        semaphore.release();
    }

    @Override
    public void defineTableContent() {
        String[] tableCol = new String[]{"Timestamp", "Plan Identity", "Plan Identity ID", "Plan Version ID",
                                         "Planned Activities", "Planned Events", "Description", "Comments"};

        tableData = new javax.swing.table.DefaultTableModel(new Object[][]{}, tableCol) {
            Class[] types = new Class[]{java.lang.String.class, java.lang.String.class, java.lang.Long.class,
                                        java.lang.Long.class, java.lang.Long.class, java.lang.Long.class,
                                        java.lang.String.class, java.lang.String.class};

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

    public void monitorPlans() throws MALInteractionException, MALException {
        // Subscribe to Plan updates
        final Subscription subscription = MOFactory.createSubscription();
        this.pdsService.getPlanDistributionStub().monitorPlanRegister(subscription, new PlanMonitor());
    }

    class PlanMonitor extends PlanDistributionAdapter {

        @Override
        public void monitorPlanNotifyReceived(MALMessageHeader msgHeader, Identifier identifier,
            UpdateHeaderList headerList, PlanVersionDetailsList versionList, Map qosProperties) {
            for (int index = 0; index < versionList.size(); index++) {
                UpdateHeader updateHeader = headerList.get(index);
                PlanVersionDetails planVersion = versionList.get(index);

                IdentifierList domain = msgHeader.getDomain();
                Time timestamp = updateHeader.getTimestamp();

                Identifier identity = updateHeader.getKey().getFirstSubKey();
                Long identityId = updateHeader.getKey().getSecondSubKey();
                Long instanceId = updateHeader.getKey().getThirdSubKey();

                addEntry(domain, timestamp, identityId, identity, instanceId, planVersion);
            }
        }
    }
}
