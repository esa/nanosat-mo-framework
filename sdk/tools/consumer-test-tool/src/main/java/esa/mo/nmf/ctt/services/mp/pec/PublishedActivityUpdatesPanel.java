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

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mp.planedit.PlanEditHelper;
import org.ccsds.moims.mo.mp.planinformationmanagement.PlanInformationManagementHelper;
import org.ccsds.moims.mo.mp.structures.ActivityUpdateDetails;
import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.mp.impl.consumer.PlanExecutionControlConsumerServiceImpl;
import esa.mo.nmf.ctt.services.mp.PublishedUpdatesPanel;
import esa.mo.nmf.ctt.services.mp.util.TimeConverter;

public class PublishedActivityUpdatesPanel extends PublishedUpdatesPanel {
    private static final Logger LOGGER = Logger.getLogger(PublishedActivityUpdatesPanel.class.getName());

    private final ArchiveConsumerServiceImpl archiveService;
    private final PlanExecutionControlConsumerServiceImpl planExecutionControlService;
    private final PublishedActivityUpdatesTable publishedActivityUpdatesTable;

    public PublishedActivityUpdatesPanel(final ArchiveConsumerServiceImpl archiveService, final PlanExecutionControlConsumerServiceImpl planExecutionControlService) {
        super();

        this.archiveService = archiveService;
        this.planExecutionControlService = planExecutionControlService;

        this.publishedActivityUpdatesTable = new PublishedActivityUpdatesTable(archiveService, planExecutionControlService);
        try {
          this.publishedActivityUpdatesTable.monitorActivities();
        } catch (final MALInteractionException | MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }

        setView(this.publishedActivityUpdatesTable);

        getRefreshButton().addActionListener(this::refreshButtonActionPerformed);
    }

    private void refreshButtonActionPerformed(final java.awt.event.ActionEvent evt) {

        this.publishedActivityUpdatesTable.removeAllEntries();

        final ObjectType updateObjectType = PlanEditHelper.ACTIVITYUPDATE_OBJECT_TYPE;

        final FineTime startTime = TimeConverter.convert(getRefreshTime());

        if (startTime == null) {
            JOptionPane.showMessageDialog(null, "Please insert date using format: yyyy-MM-dd HH:mm:ss UTC" , "Unparseable date", JOptionPane.PLAIN_MESSAGE);
        }

        final ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        final ArchiveQuery archiveQuery = new ArchiveQuery();

        archiveQuery.setDomain(null);
        archiveQuery.setNetwork(null);
        archiveQuery.setProvider(null);
        archiveQuery.setRelated(new Long(0));
        archiveQuery.setSource(null);
        archiveQuery.setStartTime(startTime);
        archiveQuery.setEndTime(null);
        archiveQuery.setSortFieldName(null);
        archiveQuery.setSortFieldName(null);

        archiveQueryList.add(archiveQuery);
        try {
            archiveService.getArchiveStub().query(true, updateObjectType, archiveQueryList, null, new ArchiveAdapter() {
                @Override
                public void queryUpdateReceived(final MALMessageHeader msgHeader, final ObjectType objType, final IdentifierList domain, final ArchiveDetailsList objDetails, final ElementList objBodies, final Map qosProperties) {
                    addEntries(domain, objDetails, objBodies);
                }
                @Override
                public void queryResponseReceived(final MALMessageHeader msgHeader, final ObjectType objType, final IdentifierList domain, final ArchiveDetailsList objDetails, final ElementList objBodies, final Map qosProperties) {
                    addEntries(domain, objDetails, objBodies);
                }
          });
        } catch (final MALInteractionException | MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    private void addEntries(final IdentifierList domain, final ArchiveDetailsList objDetails, final ElementList objBodies) {
        if (objDetails == null) return;
        for (int index = 0; index < objDetails.size(); index++) {
            final ArchiveDetails details = objDetails.get(index);
            final ActivityUpdateDetails update = (ActivityUpdateDetails) objBodies.get(index);

            final Long instanceId = details.getDetails().getRelated();
            final Identifier identity = getIdentity(domain, instanceId);

            publishedActivityUpdatesTable.addEntry(domain, identity, instanceId, update);
        }
    }

    private Identifier getIdentity(final IdentifierList domain, final Long instanceId) {
        final Identifier identity = null;

        final ObjectType instanceObjectType = PlanEditHelper.ACTIVITYINSTANCE_OBJECT_TYPE;
        final ObjectType definitionObjectType = PlanInformationManagementHelper.ACTIVITYDEFINITION_OBJECT_TYPE;
        final ObjectType identityObjectType = PlanInformationManagementHelper.ACTIVITYIDENTITY_OBJECT_TYPE;

        final ArchivePersistenceObject instanceObject = HelperArchive
            .getArchiveCOMObject(this.archiveService.getArchiveStub(), instanceObjectType, domain, instanceId);

        if (instanceObject == null) {
            return identity;
        }

        final Long definitionId = instanceObject.getArchiveDetails().getDetails().getRelated();
        final ArchivePersistenceObject definitionObject = HelperArchive
            .getArchiveCOMObject(this.archiveService.getArchiveStub(), definitionObjectType, domain, definitionId);

        if (definitionObject == null) {
            return identity;
        }

        final Long identityId = definitionObject.getArchiveDetails().getDetails().getRelated();
        final ArchivePersistenceObject identityObject = HelperArchive
            .getArchiveCOMObject(this.archiveService.getArchiveStub(), identityObjectType, domain, identityId);

        if (identityObject == null) {
            return identity;
        }

        return (Identifier) identityObject.getObject();
    }
}
