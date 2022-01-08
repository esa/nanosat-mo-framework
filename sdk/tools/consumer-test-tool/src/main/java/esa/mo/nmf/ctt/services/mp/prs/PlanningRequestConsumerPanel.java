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

import java.io.InterruptedIOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mp.planinformationmanagement.PlanInformationManagementHelper;
import org.ccsds.moims.mo.mp.planningrequest.PlanningRequestHelper;
import org.ccsds.moims.mo.mp.planningrequest.consumer.PlanningRequestAdapter;
import org.ccsds.moims.mo.mp.structures.ActivityDetails;
import org.ccsds.moims.mo.mp.structures.ActivityNode;
import org.ccsds.moims.mo.mp.structures.ArgSpec;
import org.ccsds.moims.mo.mp.structures.ArgSpecList;
import org.ccsds.moims.mo.mp.structures.ArgType;
import org.ccsds.moims.mo.mp.structures.Argument;
import org.ccsds.moims.mo.mp.structures.ConstraintNode;
import org.ccsds.moims.mo.mp.structures.DurationExpression;
import org.ccsds.moims.mo.mp.structures.EventWindowList;
import org.ccsds.moims.mo.mp.structures.PositionExpression;
import org.ccsds.moims.mo.mp.structures.RequestFilter;
import org.ccsds.moims.mo.mp.structures.RequestUpdateDetailsList;
import org.ccsds.moims.mo.mp.structures.RequestVersionDetails;
import org.ccsds.moims.mo.mp.structures.RequestVersionDetailsList;
import org.ccsds.moims.mo.mp.structures.SimpleActivityDetails;
import org.ccsds.moims.mo.mp.structures.TemporalRepetition;
import org.ccsds.moims.mo.mp.structures.TimeExpression;
import org.ccsds.moims.mo.mp.structures.TimeReference;
import org.ccsds.moims.mo.mp.structures.TimeWindow;
import org.ccsds.moims.mo.mp.structures.TimeWindowList;
import org.ccsds.moims.mo.mp.structures.c_ActivityDetails;
import org.ccsds.moims.mo.mp.structures.c_ActivityDetailsList;
import org.ccsds.moims.mo.mp.structures.c_Expression;
import org.ccsds.moims.mo.mp.structures.ArgumentList;
import org.ccsds.moims.mo.mp.structures.c_Repetition;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.helpertools.clock.SystemClock;
import esa.mo.mp.impl.com.COMObjectIdHelper;
import esa.mo.mp.impl.consumer.PlanningRequestConsumerServiceImpl;
import esa.mo.mp.impl.util.MPFactory;
import esa.mo.mp.impl.util.MPPolyFix;
import esa.mo.tools.mowindow.MOWindow;

/**
 * PlanningRequestConsumerPanel
 */
public class PlanningRequestConsumerPanel extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(PlanningRequestConsumerPanel.class.getName());

    private static final String GENERIC_REQUEST = "Generic Request";
    private static final String TAKE_IMAGE_SINGLE = "TakeImage Request (10,51)";
    private static final String TAKE_IMAGE_MULTIPLE = "TakeImage Request (10,51; 75,23)";
    private static final String SCOUT_SINGLE = "Scout Request (58,26)";


    private final PlanningRequestConsumerServiceImpl planningRequestService;
    private final PlanningRequestTablePanel planningRequestTable;
    private final PlanningRequestStatusTablePanel planningRequestStatusTable;

    public PlanningRequestConsumerPanel(final PlanningRequestConsumerServiceImpl planningRequestService) {
        initComponents();

        this.planningRequestService = planningRequestService;

        this.planningRequestTable = new PlanningRequestTablePanel(planningRequestService.getCOMServices().getArchiveService());
        this.planningRequestStatusTable = new PlanningRequestStatusTablePanel(planningRequestService.getCOMServices().getArchiveService());

        jScrollPane2.setViewportView(this.planningRequestTable);
    }

    public void init() {
        this.getRequestAllButtonActionPerformed(null);
    }

    /**
     * This method is called from within the constructor to initialize the
     * formAddModifyParameter. WARNING: Do NOT modify this code. The content of this
     * method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        actionDefinitionsTable = new javax.swing.JTable();
        parameterTab = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        submitPlanningRequestButton = new javax.swing.JButton();
        updatePlanningRequestButton = new javax.swing.JButton();
        cancelPlanningRequestButton = new javax.swing.JButton();
        getRequestButton = new javax.swing.JButton();
        getRequestStatusButton = new javax.swing.JButton();

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Planning Request Service");
        jLabel6.setToolTipText("");

        jScrollPane2.setHorizontalScrollBar(null);
        jScrollPane2.setPreferredSize(new java.awt.Dimension(796, 380));
        jScrollPane2.setRequestFocusEnabled(false);

        actionDefinitionsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {}
            },
            new String [] {

            }
        ));
        actionDefinitionsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        actionDefinitionsTable.setAutoscrolls(false);
        actionDefinitionsTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane2.setViewportView(actionDefinitionsTable);

        parameterTab.setLayout(new java.awt.GridLayout(1, 1));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { GENERIC_REQUEST, TAKE_IMAGE_SINGLE, TAKE_IMAGE_MULTIPLE, SCOUT_SINGLE }));
        jPanel5.add(jComboBox1);

        submitPlanningRequestButton.setText("submitPlanningRequest");
        submitPlanningRequestButton.addActionListener(this::submitRequestButtonActionPerformed);
        jPanel5.add(submitPlanningRequestButton);

        updatePlanningRequestButton.setText("updatePlanningRequest");
        updatePlanningRequestButton.addActionListener(this::updateRequestButtonActionPerformed);
        jPanel5.add(updatePlanningRequestButton);

        cancelPlanningRequestButton.setText("cancelPlanningRequest");
        cancelPlanningRequestButton.addActionListener(this::cancelRequestButtonActionPerformed);
        jPanel5.add(cancelPlanningRequestButton);

        getRequestButton.setText("getRequest(\"*\")");
        getRequestButton.addActionListener(this::getRequestAllButtonActionPerformed);
        jPanel5.add(getRequestButton);

        getRequestStatusButton.setText("getRequestStatus(\"*\")");
        getRequestStatusButton.addActionListener(this::getRequestStatusAllButtonActionPerformed);
        jPanel5.add(getRequestStatusButton);

        parameterTab.add(jPanel5);

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(parameterTab, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1043, Short.MAX_VALUE)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(parameterTab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void submitRequestButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitRequestButtonActionPerformed
        Identifier identity = new Identifier("Request 1");
        RequestVersionDetails request = createGenericRequest();

        final String selectedItem = (String) jComboBox1.getSelectedItem();
        switch (selectedItem) {
            case GENERIC_REQUEST:
                request = createGenericRequest();
                break;
            case TAKE_IMAGE_SINGLE:
                request = createTakeImageRequest(new String[] { "10,51" });
                break;
            case TAKE_IMAGE_MULTIPLE:
                request = createTakeImageRequest(new String[] { "10,51", "75,23" });
                break;
            case SCOUT_SINGLE:
                request = createScoutRequest(new String[] { "58,26" });
                break;
            default:
                break;
        }

        final MOWindow identityWindow = new MOWindow(identity, true);
        try {
            identity = (Identifier) identityWindow.getObject();
        } catch (final InterruptedIOException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return;
        }

        final MOWindow requestWindow = new MOWindow(request, true);
        try {
            request = (RequestVersionDetails) requestWindow.getObject();
        } catch (final InterruptedIOException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return;
        }

        try {
            this.planningRequestService.getPlanningRequestStub().asyncSubmitRequest(identity, request, new PlanningRequestAdapter() {

                @Override
                public void submitRequestResponseReceived(final MALMessageHeader msgHeader, final Long requestIdentityId, final Long requestVersionId, final Map qosProperties) {
                    getRequestAllButtonActionPerformed(null);
                }

                @Override
                public void submitRequestErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the submitRequest operation.\n" + error.toString() , "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            });
        } catch (final MALInteractionException | MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_submitRequestButtonActionPerformed

    private RequestVersionDetails createGenericRequest() {
        final RequestVersionDetails requestVersion = MPFactory.createRequestVersion();

        final ObjectId requestTemplateId = COMObjectIdHelper.getObjectId(1L, PlanInformationManagementHelper.REQUESTTEMPLATE_OBJECT_TYPE);

        final SimpleActivityDetails activity = new SimpleActivityDetails();
        activity.setActivityDef(1L);
        activity.setComments("Take picture");

        final TemporalRepetition timeRepetition = new TemporalRepetition();
        timeRepetition.setCount(10);
        timeRepetition.setInitialTime(new TimeExpression("=", null, ArgType.TIME, new Time(1577836800)));
        timeRepetition.setSeparation(new DurationExpression("=", null, ArgType.DURATION, new Duration(86400)));

        final c_ActivityDetails activityDetails = new c_ActivityDetails();
        activityDetails.setSimpleActivityDetails(activity);

        final c_ActivityDetailsList activityList = new c_ActivityDetailsList();
        activityList.add(activityDetails);

        final ActivityNode activities = new ActivityNode();
        activities.setActivities(activityList);

        final c_Repetition repetition = new c_Repetition();
        repetition.setTemporalRepetition(timeRepetition);
        activities.setRepetition(repetition);

        final ArgumentList arguments = new ArgumentList();

        final ConstraintNode constraints = new ConstraintNode();

        final String planningPeriod = Integer.toString(Year.now().getValue());

        final TimeReference timeReference = TimeReference.fromOrdinal(0);

        final Identifier user = new Identifier("CTT");

        final EventWindowList validityEvent = new EventWindowList();

        final TimeWindowList validityTime = createValidityTime();

        requestVersion.setActivities(activities);
        requestVersion.setArguments(arguments);
        requestVersion.setComments("");
        requestVersion.setConstraints(constraints);
        requestVersion.setDescription("Generic request");
        requestVersion.setPlanningPeriod(new Identifier(planningPeriod));
        requestVersion.setStandingOrder(false);
        requestVersion.setTemplate(requestTemplateId);
        requestVersion.setTimeReference(timeReference);
        requestVersion.setUser(user);
        requestVersion.setValidityEvent(validityEvent);
        requestVersion.setValidityTime(validityTime);

        return requestVersion;
    }

    private RequestVersionDetails createTakeImageRequest(final String[] positions) {
        final RequestVersionDetails requestVersion = MPFactory.createRequestVersion();

        final String description = "Take picture at " + Arrays.toString(positions);

        final List<ActivityDetails> activityDetailsList = new ArrayList<>();

        for (final String position : positions) {
            final SimpleActivityDetails takeImageActivity = createTakeImageActivity(position);
            activityDetailsList.add(takeImageActivity);
        }

        final c_ActivityDetailsList activities = MPPolyFix.encodeActivityDetails(activityDetailsList);

        final ActivityNode activityNode = new ActivityNode();
        activityNode.setComments("Take picture activity");

        activityNode.setActivities(activities);

        final ObjectId requestTemplateId = COMObjectIdHelper.getObjectId(1L, PlanInformationManagementHelper.REQUESTTEMPLATE_OBJECT_TYPE);

        final ArgumentList arguments = new ArgumentList();

        final ConstraintNode constraints = new ConstraintNode();

        final String planningPeriod = Integer.toString(Year.now().getValue());

        final TimeReference timeReference = TimeReference.fromOrdinal(0);

        final Identifier user = new Identifier("CTT");

        final EventWindowList validityEvent = new EventWindowList();

        final TimeWindowList validityTime = createValidityTime();

        requestVersion.setActivities(activityNode);
        requestVersion.setArguments(arguments);
        requestVersion.setComments("");
        requestVersion.setConstraints(constraints);
        requestVersion.setDescription(description);
        requestVersion.setPlanningPeriod(new Identifier(planningPeriod));
        requestVersion.setStandingOrder(false);
        requestVersion.setTemplate(requestTemplateId);
        requestVersion.setTimeReference(timeReference);
        requestVersion.setUser(user);
        requestVersion.setValidityEvent(validityEvent);
        requestVersion.setValidityTime(validityTime);

        return requestVersion;
    }

    private SimpleActivityDetails createTakeImageActivity(final String position) {
        final SimpleActivityDetails takeImageActivity = new SimpleActivityDetails();
        takeImageActivity.setActivityDef(1L);

        final PositionExpression positionExpression = new PositionExpression("==", null, ArgType.POSITION, new Union(position));
        final c_Expression expression = MPPolyFix.encode(positionExpression);
        final ArgSpec positionArg = new ArgSpec(new Identifier("position"), expression, null);

        final ArgSpecList argSpecList = new ArgSpecList();
        argSpecList.add(positionArg);
        takeImageActivity.setArgSpecs(argSpecList);
        return takeImageActivity;
    }

    private TimeWindowList createValidityTime() {
        final long timeNow = SystemClock.getTime().getValue();
        final TimeExpression windowStart = new TimeExpression("==", null, ArgType.TIME, new Time(timeNow + 10000L));
        final TimeExpression windowEnd = new TimeExpression("==", null, ArgType.TIME, new Time(timeNow + 30 * 24 * 60 * 60 * 1000L)); // 30 days
        final TimeWindow timeWindow = new TimeWindow(windowStart, windowEnd);

        final TimeWindowList timeWindows = new TimeWindowList();
        timeWindows.add(timeWindow);
        return timeWindows;
    }

    private RequestVersionDetails createScoutRequest(final String[] positions) {
        final RequestVersionDetails requestVersion = MPFactory.createRequestVersion();

        final String description = "Scout at " + Arrays.toString(positions) + " for Volcano, Reef, Human-made, Land or Water";

        final Argument positionArgument = new Argument();
        positionArgument.setArgName(new Identifier("positions"));
        final StringList positionValues = new StringList();

        for (final String position : positions) {
            positionValues.add(position);
        }

        positionArgument.setArgValue(positionValues);
        positionArgument.setCount(positionValues.size());

        final Argument interestArgument = new Argument();
        interestArgument.setArgName(new Identifier("interests"));
        final StringList interestValues = new StringList();
        interestValues.add("Volcano");
        interestValues.add("Reef");
        interestValues.add("Human-made");
        interestValues.add("Land");
        interestValues.add("Water");
        interestArgument.setArgValue(interestValues);
        positionArgument.setCount(interestValues.size());

        final ArgumentList argumentList = new ArgumentList();
        argumentList.add(positionArgument);
        argumentList.add(interestArgument);

        final ObjectId requestTemplateId = COMObjectIdHelper.getObjectId(2L, PlanInformationManagementHelper.REQUESTTEMPLATE_OBJECT_TYPE);

        final ConstraintNode constraints = new ConstraintNode();

        final String planningPeriod = Integer.toString(Year.now().getValue());

        final TimeReference timeReference = TimeReference.fromOrdinal(0);

        final Identifier user = new Identifier("CTT");

        final EventWindowList validityEvent = new EventWindowList();

        final TimeWindowList validityTime = createValidityTime();

        requestVersion.setArguments(argumentList);
        requestVersion.setComments("");
        requestVersion.setConstraints(constraints);
        requestVersion.setDescription(description);
        requestVersion.setPlanningPeriod(new Identifier(planningPeriod));
        requestVersion.setStandingOrder(false);
        requestVersion.setTemplate(requestTemplateId);
        requestVersion.setTimeReference(timeReference);
        requestVersion.setUser(user);
        requestVersion.setValidityEvent(validityEvent);
        requestVersion.setValidityTime(validityTime);

        return requestVersion;
    }

    private void updateRequestButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateRequestButtonActionPerformed
        if (planningRequestTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Please select a request version", "Info", JOptionPane.PLAIN_MESSAGE);
            return; // No rows selected
        }

        final ArchivePersistenceObject selectedObject = planningRequestTable.getSelectedCOMObject();

        final Long identityId = planningRequestTable.getSelectedIdentityObjId();
        RequestVersionDetails updatedRequestVersion = (RequestVersionDetails) selectedObject.getObject();

        final MOWindow moObject = new MOWindow(selectedObject.getObject(), true);

        try {
            updatedRequestVersion = (RequestVersionDetails) moObject.getObject();
        } catch (final InterruptedIOException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return;
        }

        try {
            this.planningRequestService.getPlanningRequestStub().asyncUpdateRequest(identityId, updatedRequestVersion, new PlanningRequestAdapter() {
                @Override
                public void updateRequestResponseReceived(final MALMessageHeader msgHeader, final Long requestVersionId, final Map qosProperties) {
                    getRequestAllButtonActionPerformed(null);
                }

                @Override
                public void updateRequestErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the updateRequest operation.\n" + error.toString() , "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            });
        } catch (final MALInteractionException | MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_updateRequestButtonActionPerformed

    private void cancelRequestButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelRequestButtonActionPerformed
        if (planningRequestTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Please select a request version", "Info", JOptionPane.PLAIN_MESSAGE);
            return; // No rows selected
        }

        final Long identityId = planningRequestTable.getSelectedIdentityObjId();

        try {
            this.planningRequestService.getPlanningRequestStub().asyncCancelRequest(identityId, new PlanningRequestAdapter() {
                @Override
                public void cancelRequestAckReceived(final MALMessageHeader msgHeader, final Map qosProperties) {
                    getRequestStatusAllButtonActionPerformed(null);
                }

                @Override
                public void cancelRequestErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the cancelRequest operation.\n" + error.toString() , "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            });
        } catch (final MALInteractionException | MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_cancelRequestButtonActionPerformed

    private void getRequestAllButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getRequestAllButtonActionPerformed
        final RequestFilter filter = new RequestFilter();
        filter.setReturnAll(true);

        try {
            this.planningRequestService.getPlanningRequestStub().asyncGetRequest(filter, new PlanningRequestAdapter() {
                @Override
                public void getRequestResponseReceived(final MALMessageHeader msgHeader, final LongList requestIdentityIds, final LongList requestInstanceIds, final RequestVersionDetailsList requestVersions, final Map qosProperties) {
                    final org.ccsds.moims.mo.mc.structures.ObjectInstancePairList ids = new org.ccsds.moims.mo.mc.structures.ObjectInstancePairList();
                    for (int index = 0; index < requestIdentityIds.size(); index++) {
                        ids.add(new org.ccsds.moims.mo.mc.structures.ObjectInstancePair(
                            requestIdentityIds.get(index),
                            requestInstanceIds.get(index)
                        ));
                    }
                    planningRequestTable.refreshTableWithIds(ids, planningRequestService.getConnectionDetails().getDomain(), PlanningRequestHelper.REQUESTVERSION_OBJECT_TYPE);

                    jScrollPane2.setViewportView(planningRequestTable);
                    LOGGER.log(Level.INFO, "getRequest(\"*\") returned {0} object instance identifiers", requestIdentityIds.size());
                }

                @Override
                public void getRequestResponseErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the getRequest operation.\n" + error.toString(), "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            });
        } catch (final MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (final MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_getRequestAllButtonActionPerformed

    private void getRequestStatusAllButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getRequestStatusAllButtonActionPerformed
        final RequestFilter filter = new RequestFilter();
        filter.setReturnAll(true);

        try {
            this.planningRequestService.getPlanningRequestStub().asyncGetRequestStatus(filter, new PlanningRequestAdapter() {

                @Override
                public void getRequestStatusResponseReceived(final MALMessageHeader msgHeader, final LongList requestIdentityIds, final LongList requestVersionIds, final RequestUpdateDetailsList requestUpdate, final Map qosProperties) {
                    planningRequestStatusTable.removeAllEntries();
                    for (int index = 0; index < requestIdentityIds.size(); index++) {
                        planningRequestStatusTable.addEntry(
                            requestIdentityIds.get(index),
                            requestVersionIds.get(index),
                            requestUpdate.get(index)
                        );
                    }
                    jScrollPane2.setViewportView(planningRequestStatusTable);
                    LOGGER.log(Level.INFO, "getRequestStatus(\"*\") returned {0} object instance identifiers", requestVersionIds.size());
                }

                @Override
                public void getRequestStatusErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the getRequestStatus operation.\n" + error.toString(), "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            }
            );
        } catch (final MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (final MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_getRequestStatusAllButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable actionDefinitionsTable;
    private javax.swing.JButton cancelPlanningRequestButton;
    private javax.swing.JButton getRequestButton;
    private javax.swing.JButton getRequestStatusButton;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel parameterTab;
    private javax.swing.JButton submitPlanningRequestButton;
    private javax.swing.JButton updatePlanningRequestButton;
    // End of variables declaration//GEN-END:variables
}
