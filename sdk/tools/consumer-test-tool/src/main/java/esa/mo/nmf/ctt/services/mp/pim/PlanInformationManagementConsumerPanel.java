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
package esa.mo.nmf.ctt.services.mp.pim;

import java.io.InterruptedIOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mp.planinformationmanagement.PlanInformationManagementHelper;
import org.ccsds.moims.mo.mp.planinformationmanagement.consumer.PlanInformationManagementAdapter;
import org.ccsds.moims.mo.mp.structures.ActivityDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.ActivityDefinitionDetailsList;
import org.ccsds.moims.mo.mp.structures.ActivityNode;
import org.ccsds.moims.mo.mp.structures.ConstraintNode;
import org.ccsds.moims.mo.mp.structures.EventDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.EventDefinitionDetailsList;
import org.ccsds.moims.mo.mp.structures.LogicOp;
import org.ccsds.moims.mo.mp.structures.NumericArgDef;
import org.ccsds.moims.mo.mp.structures.NumericResourceDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.ObjectInstancePair;
import org.ccsds.moims.mo.mp.structures.ObjectInstancePairList;
import org.ccsds.moims.mo.mp.structures.RequestTemplateDetails;
import org.ccsds.moims.mo.mp.structures.RequestTemplateDetailsList;
import org.ccsds.moims.mo.mp.structures.SimpleActivityDetails;
import org.ccsds.moims.mo.mp.structures.StatusArgDef;
import org.ccsds.moims.mo.mp.structures.StatusResourceDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.StringArgDef;
import org.ccsds.moims.mo.mp.structures.StringResourceDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.c_ActivityDetails;
import org.ccsds.moims.mo.mp.structures.c_ActivityDetailsList;
import org.ccsds.moims.mo.mp.structures.c_ArgDef;
import org.ccsds.moims.mo.mp.structures.c_ArgDefList;
import org.ccsds.moims.mo.mp.structures.c_ConstraintList;
import org.ccsds.moims.mo.mp.structures.c_ResourceDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.c_ResourceDefinitionDetailsList;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.mp.impl.consumer.PlanInformationManagementConsumerServiceImpl;
import esa.mo.tools.mowindow.MOWindow;

/**
 * PlanInformationManagementConsumerPanel
 */
public class PlanInformationManagementConsumerPanel extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(PlanInformationManagementConsumerPanel.class.getName());

    private final PlanInformationManagementConsumerServiceImpl pimService;
    private final RequestDefTablePanel requestDefTable;
    private final ActivityDefTablePanel activityDefTable;
    private final EventDefTablePanel eventDefTable;
    private final ResourceDefTablePanel resourceDefTable;

    public PlanInformationManagementConsumerPanel(final PlanInformationManagementConsumerServiceImpl pimService) {
        initComponents();

        this.pimService = pimService;

        this.requestDefTable = new RequestDefTablePanel(pimService.getCOMServices().getArchiveService());
        this.activityDefTable = new ActivityDefTablePanel(pimService.getCOMServices().getArchiveService());
        this.eventDefTable = new EventDefTablePanel(pimService.getCOMServices().getArchiveService());
        this.resourceDefTable = new ResourceDefTablePanel(pimService.getCOMServices().getArchiveService());

        jScrollPane2.setViewportView(this.requestDefTable);
    }

    public void init() {
        this.listRequestDefinitionAllButtonActionPerformed(null);
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
        addRequestDefButton = new javax.swing.JButton();
        updateRequestDefButton = new javax.swing.JButton();
        removeRequestDefButton = new javax.swing.JButton();
        listRequestDefsButton = new javax.swing.JButton();
        removeAllRequestDefButton = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        addActivityDefButton = new javax.swing.JButton();
        updateActivityDefButton = new javax.swing.JButton();
        removeActivityDefButton = new javax.swing.JButton();
        listActivityDefsButton = new javax.swing.JButton();
        removeAllActivityDefButton = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        addEventDefButton = new javax.swing.JButton();
        updateEventDefButton = new javax.swing.JButton();
        removeEventDefButton = new javax.swing.JButton();
        listEventDefsButton = new javax.swing.JButton();
        removeAllEventDefButton = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        addResourceDefButton = new javax.swing.JButton();
        updateResourceDefButton = new javax.swing.JButton();
        removeResourceDefButton = new javax.swing.JButton();
        listResourceDefsButton = new javax.swing.JButton();
        removeAllResourceDefButton = new javax.swing.JButton();

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Plan Information Management Service");
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

        parameterTab.setLayout(new java.awt.GridLayout(4, 1));

        addRequestDefButton.setText("addRequestDef");
        addRequestDefButton.addActionListener(this::addRequestDefinitionButtonActionPerformed);
        jPanel5.add(addRequestDefButton);

        updateRequestDefButton.setText("updateRequestDef");
        updateRequestDefButton.addActionListener(this::updateRequestDefinitionButtonActionPerformed);
        jPanel5.add(updateRequestDefButton);

        removeRequestDefButton.setText("removeRequestDef");
        removeRequestDefButton.addActionListener(this::removeRequestDefinitionButtonActionPerformed);
        jPanel5.add(removeRequestDefButton);

        listRequestDefsButton.setText("listRequestDefs(\"*\")");
        listRequestDefsButton.addActionListener(this::listRequestDefinitionAllButtonActionPerformed);
        jPanel5.add(listRequestDefsButton);

        removeAllRequestDefButton.setText("removeRequestDef(0)");
        removeAllRequestDefButton.addActionListener(this::removeRequestDefinitionAllButtonActionPerformed);
        jPanel5.add(removeAllRequestDefButton);

        parameterTab.add(jPanel5);

        addActivityDefButton.setText("addActivityDef");
        addActivityDefButton.addActionListener(this::addActivityDefinitionButtonActionPerformed);
        jPanel6.add(addActivityDefButton);

        updateActivityDefButton.setText("updateActivityDef");
        updateActivityDefButton.addActionListener(this::updateActivityDefinitionButtonActionPerformed);
        jPanel6.add(updateActivityDefButton);

        removeActivityDefButton.setText("removeActivityDef");
        removeActivityDefButton.addActionListener(this::removeActivityDefinitionButtonActionPerformed);
        jPanel6.add(removeActivityDefButton);

        listActivityDefsButton.setText("listActivityDefs(\"*\")");
        listActivityDefsButton.addActionListener(this::listActivityDefinitionAllButtonActionPerformed);
        jPanel6.add(listActivityDefsButton);

        removeAllActivityDefButton.setText("removeActivityDef(0)");
        removeAllActivityDefButton.addActionListener(this::removeActivityDefinitionAllButtonActionPerformed);
        jPanel6.add(removeAllActivityDefButton);

        parameterTab.add(jPanel6);

        addEventDefButton.setText("addEventDef");
        addEventDefButton.addActionListener(this::addEventDefinitionButtonActionPerformed);
        jPanel7.add(addEventDefButton);

        updateEventDefButton.setText("updateEventDef");
        updateEventDefButton.addActionListener(this::updateEventDefinitionButtonActionPerformed);
        jPanel7.add(updateEventDefButton);

        removeEventDefButton.setText("removeEventDef");
        removeEventDefButton.addActionListener(this::removeEventDefinitionButtonActionPerformed);
        jPanel7.add(removeEventDefButton);

        listEventDefsButton.setText("listEventDefs(\"*\")");
        listEventDefsButton.addActionListener(this::listEventDefinitionAllButtonActionPerformed);
        jPanel7.add(listEventDefsButton);

        removeAllEventDefButton.setText("removeEventDef(0)");
        removeAllEventDefButton.addActionListener(this::removeEventDefinitionAllButtonActionPerformed);
        jPanel7.add(removeAllEventDefButton);

        parameterTab.add(jPanel7);

        addResourceDefButton.setText("addResourceDef");
        addResourceDefButton.addActionListener(this::addResourceDefinitionButtonActionPerformed);
        jPanel9.add(addResourceDefButton);

        updateResourceDefButton.setText("updateResourceDef");
        updateResourceDefButton.addActionListener(this::updateResourceDefinitionButtonActionPerformed);
        jPanel9.add(updateResourceDefButton);

        removeResourceDefButton.setText("removeResourceDef");
        removeResourceDefButton.addActionListener(this::removeResourceDefinitionButtonActionPerformed);
        jPanel9.add(removeResourceDefButton);

        listResourceDefsButton.setText("listResourceDefs(\"*\")");
        listResourceDefsButton.addActionListener(this::listResourceDefinitionAllButtonActionPerformed);
        jPanel9.add(listResourceDefsButton);

        removeAllResourceDefButton.setText("removeResourceDef(0)");
        removeAllResourceDefButton.addActionListener(this::removeResourceDefinitionAllButtonActionPerformed);
        jPanel9.add(removeAllResourceDefButton);

        parameterTab.add(jPanel9);

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(parameterTab, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 882, Short.MAX_VALUE)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(parameterTab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addRequestDefinitionButtonActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addDefinitionButtonActionPerformed
        final RequestTemplateDetails definition = new RequestTemplateDetails();

        // Activities
        final SimpleActivityDetails activity = new SimpleActivityDetails();
        activity.setComments("Test simple activity");
        activity.setTags(new StringList());

        final ActivityNode activityNode = new ActivityNode();
        activityNode.setComments("Test activity");

        final c_ActivityDetails activityDetails = new c_ActivityDetails();
        activityDetails.setSimpleActivityDetails(activity);
        final c_ActivityDetailsList detailsList = new c_ActivityDetailsList();
        detailsList.add(activityDetails);
        activityNode.setActivities(detailsList);

        // Argument defs
        final c_ArgDefList argDefList = new c_ArgDefList();

        // Numeric argument
        final c_ArgDef numericDef = new c_ArgDef();
        numericDef.setNumericArgDef((NumericArgDef) new NumericArgDef());
        argDefList.add(numericDef);

        // String argument
        final c_ArgDef stringDef = new c_ArgDef();
        stringDef.setStringArgDef((StringArgDef) new StringArgDef());
        argDefList.add(stringDef);

        // Status argument
        final c_ArgDef statusDef = new c_ArgDef();
        stringDef.setStatusArgDef((StatusArgDef) new StatusArgDef());
        argDefList.add(statusDef);

        // Constraints
        final ConstraintNode constraints = new ConstraintNode();
        constraints.setNegate(false);
        constraints.setOperator(LogicOp.AND);

        final c_ConstraintList constraintList = new c_ConstraintList();
        constraints.setConstraints(constraintList);

        definition.setActivities(activityNode);
        definition.setArgDefs(argDefList);
        definition.setConstraints(constraints);
        definition.setDescription("Test request definition");
        definition.setStandingOrder(true);
        definition.setVersion("Test version");

        final Identifier identity = new Identifier("Test request identity");
        final MOWindow identityWindow = new MOWindow(identity, true);

        final IdentifierList identities = new IdentifierList();

        try {
            identities.add((Identifier) identityWindow.getObject());
        } catch (final InterruptedIOException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return;
        }

        final MOWindow definitionWindow = new MOWindow(definition, true);

        final RequestTemplateDetailsList definitions = new RequestTemplateDetailsList();

        try {
            definitions.add((RequestTemplateDetails) definitionWindow.getObject());
        } catch (final InterruptedIOException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return;
        }

        try {
            this.pimService.getPlanInformationManagementStub().asyncAddRequestDef(identities, definitions, new PlanInformationManagementAdapter() {
                @Override
                public void addRequestDefResponseReceived(final MALMessageHeader msgHeader, final ObjectInstancePairList objIds, final Map qosProperties) {
                    listRequestDefinitionAllButtonActionPerformed(null);
                }

                @Override
                public void addRequestDefErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the addRequestDef operation.\n" + error.toString() , "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            });
        } catch (final MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (final MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    private void updateRequestDefinitionButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateRequestDefinitionButtonActionPerformed
        if (requestDefTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Please select a request definition", "Info", JOptionPane.PLAIN_MESSAGE);
            return; // No rows selected
        }

        final ArchivePersistenceObject selectedObject = requestDefTable.getSelectedCOMObject();
        final MOWindow moObject = new MOWindow(selectedObject.getObject(), true);

        final LongList objIds = new LongList();
        objIds.add(requestDefTable.getSelectedIdentityObjId());

        final RequestTemplateDetailsList definitions = new RequestTemplateDetailsList();
        try {
            definitions.add((RequestTemplateDetails) moObject.getObject());
        } catch (final InterruptedIOException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return;
        }

        try {
            this.pimService.getPlanInformationManagementStub().asyncUpdateRequestDef(objIds, definitions, new PlanInformationManagementAdapter() {
                @Override
                public void updateRequestDefResponseReceived(final MALMessageHeader msgHeader, final LongList defID, final Map qosProperties) {
                    listRequestDefinitionAllButtonActionPerformed(null);
                }

                @Override
                public void updateRequestDefErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the updateRequestDef operation.\n" + error.toString() , "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            });
        } catch (final MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (final MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_updateRequestDefinitionButtonActionPerformed

    private void removeRequestDefinitionButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeRequestDefinitionButtonActionPerformed
        if (requestDefTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Please select a request definition", "Info", JOptionPane.PLAIN_MESSAGE);
            return; // No rows selected
        }

        final LongList identityIds = new LongList();
        identityIds.add(requestDefTable.getSelectedIdentityObjId());

        try {
            this.pimService.getPlanInformationManagementStub().asyncRemoveRequestDef(identityIds, new PlanInformationManagementAdapter() {
                @Override
                public void removeRequestDefAckReceived(final MALMessageHeader msgHeader, final Map qosProperties) {
                    listRequestDefinitionAllButtonActionPerformed(null);
                }

                @Override
                public void removeRequestDefErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the removeRequestDef operation.\n" + error.toString() , "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            });
        } catch (final MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (final MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_removeRequestDefinitionButtonActionPerformed

    private void listRequestDefinitionAllButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listRequestDefinitionAllButtonActionPerformed
        final IdentifierList idList = new IdentifierList();
        idList.add(new Identifier("*"));

        try {
            this.pimService.getPlanInformationManagementStub().asyncListRequestDefs(idList, new PlanInformationManagementAdapter() {
                @Override
                public void listRequestDefsResponseReceived(final MALMessageHeader msgHeader, final ObjectInstancePairList defInstIds, final Map qosProperties) {
                    final org.ccsds.moims.mo.mc.structures.ObjectInstancePairList ids = new org.ccsds.moims.mo.mc.structures.ObjectInstancePairList();
                    for (final ObjectInstancePair defId : defInstIds) {
                        ids.add(new org.ccsds.moims.mo.mc.structures.ObjectInstancePair(
                            defId.getObjectIdentityInstanceId(),
                            defId.getObjectInstanceId()
                        ));
                    }
                    requestDefTable.refreshTableWithIds(ids, pimService.getConnectionDetails().getDomain(), PlanInformationManagementHelper.REQUESTTEMPLATE_OBJECT_TYPE);
                    jScrollPane2.setViewportView(requestDefTable);
                    LOGGER.log(Level.INFO, "listRequestDefinition(\"*\") returned {0} object instance identifiers", defInstIds.size());
                    if (ids.isEmpty() && evt != null) {
                        JOptionPane.showMessageDialog(null, "No request definitions found", "Info", JOptionPane.PLAIN_MESSAGE);
                    }
                }

                @Override
                public void listRequestDefsErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the listRequestDefinition operation.\n" + error.toString(), "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            }
            );
        } catch (final MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (final MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_listRequestDefinitionAllButtonActionPerformed

    private void removeRequestDefinitionAllButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeRequestDefinitionAllButtonActionPerformed
        final int resultConfirm = JOptionPane.showConfirmDialog(null,
            "All request definitions will be removed. Do you want to continue?",
            "Warning", JOptionPane.OK_CANCEL_OPTION);
        if (resultConfirm == JOptionPane.CANCEL_OPTION) return;

        final LongList identityIds = new LongList();
        identityIds.add(0L);

        try {
            this.pimService.getPlanInformationManagementStub().asyncRemoveRequestDef(identityIds, new PlanInformationManagementAdapter() {
                @Override
                public void removeRequestDefAckReceived(final MALMessageHeader msgHeader, final Map qosProperties) {
                    listRequestDefinitionAllButtonActionPerformed(null);
                }

                @Override
                public void removeRequestDefErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the removeRequestDef operation.\n" + error.toString() , "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            });
        } catch (final MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (final MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_removeRequestDefinitionAllButtonActionPerformed

    private void addActivityDefinitionButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActivityDefinitionButtonActionPerformed
        final ActivityDefinitionDetails definition = new ActivityDefinitionDetails();

        definition.setDescription("Test activity definition");
        definition.setVersion("Test version");

        final Identifier identity = new Identifier("Test activity identity");
        final MOWindow identityWindow = new MOWindow(identity, true);

        final IdentifierList identities = new IdentifierList();

        try {
            identities.add((Identifier) identityWindow.getObject());
        } catch (final InterruptedIOException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return;
        }

        final MOWindow definitionWindow = new MOWindow(definition, true);

        final ActivityDefinitionDetailsList definitions = new ActivityDefinitionDetailsList();

        try {
            definitions.add((ActivityDefinitionDetails) definitionWindow.getObject());
        } catch (final InterruptedIOException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return;
        }

        try {
            this.pimService.getPlanInformationManagementStub().asyncAddActivityDef(identities, definitions, new PlanInformationManagementAdapter() {
                @Override
                public void addActivityDefResponseReceived(final MALMessageHeader msgHeader, final ObjectInstancePairList objIds, final Map qosProperties) {
                    listActivityDefinitionAllButtonActionPerformed(null);
                }

                @Override
                public void addActivityDefErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the addActivityDef operation.\n" + error.toString() , "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            });
        } catch (final MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (final MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_addActivityDefinitionButtonActionPerformed

    private void updateActivityDefinitionButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateActivityDefinitionButtonActionPerformed
        if (activityDefTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Please select an activity definition", "Info", JOptionPane.PLAIN_MESSAGE);
            return; // No rows selected
        }

        final ArchivePersistenceObject selectedObject = activityDefTable.getSelectedCOMObject();
        final MOWindow moObject = new MOWindow(selectedObject.getObject(), true);

        final LongList objIds = new LongList();
        objIds.add(activityDefTable.getSelectedIdentityObjId());

        final ActivityDefinitionDetailsList definitions = new ActivityDefinitionDetailsList();
        try {
            definitions.add((ActivityDefinitionDetails) moObject.getObject());
        } catch (final InterruptedIOException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return;
        }

        try {
            this.pimService.getPlanInformationManagementStub().asyncUpdateActivityDef(objIds, definitions, new PlanInformationManagementAdapter() {
                @Override
                public void updateActivityDefResponseReceived(final MALMessageHeader msgHeader, final LongList defID, final Map qosProperties) {
                    listActivityDefinitionAllButtonActionPerformed(null);
                }

                @Override
                public void updateActivityDefErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the updateActivityDef operation.\n" + error.toString() , "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            });
        } catch (final MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (final MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_updateActivityDefinitionButtonActionPerformed

    private void removeActivityDefinitionButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeActivityDefinitionButtonActionPerformed
        if (activityDefTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Please select an activity definition", "Info", JOptionPane.PLAIN_MESSAGE);
            return; // No rows selected
        }

        final LongList identityIds = new LongList();
        identityIds.add(activityDefTable.getSelectedIdentityObjId());

        try {
            this.pimService.getPlanInformationManagementStub().asyncRemoveActivityDef(identityIds, new PlanInformationManagementAdapter() {
                @Override
                public void removeActivityDefAckReceived(final MALMessageHeader msgHeader, final Map qosProperties) {
                    listActivityDefinitionAllButtonActionPerformed(null);
                }

                @Override
                public void removeActivityDefErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the removeActivityDef operation.\n" + error.toString() , "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            });
        } catch (final MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (final MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_removeActivityDefinitionButtonActionPerformed

    private void listActivityDefinitionAllButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listActivityDefinitionAllButtonActionPerformed
        final IdentifierList idList = new IdentifierList();
        idList.add(new Identifier("*"));

        try {
            this.pimService.getPlanInformationManagementStub().asyncListActivityDefs(idList, new PlanInformationManagementAdapter() {
                @Override
                public void listActivityDefsResponseReceived(final MALMessageHeader msgHeader, final ObjectInstancePairList defInstIds, final Map qosProperties) {
                    final org.ccsds.moims.mo.mc.structures.ObjectInstancePairList ids = new org.ccsds.moims.mo.mc.structures.ObjectInstancePairList();
                    for (final ObjectInstancePair defId : defInstIds) {
                        ids.add(new org.ccsds.moims.mo.mc.structures.ObjectInstancePair(
                            defId.getObjectIdentityInstanceId(),
                            defId.getObjectInstanceId()
                        ));
                    }
                    activityDefTable.refreshTableWithIds(ids, pimService.getConnectionDetails().getDomain(), PlanInformationManagementHelper.ACTIVITYDEFINITION_OBJECT_TYPE);
                    jScrollPane2.setViewportView(activityDefTable);
                    LOGGER.log(Level.INFO, "listActivityDefinition(\"*\") returned {0} object instance identifiers", defInstIds.size());
                    if (ids.isEmpty() && evt != null) {
                        JOptionPane.showMessageDialog(null, "No activity definitions found", "Info", JOptionPane.PLAIN_MESSAGE);
                    }
                }

                @Override
                public void listRequestDefsErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the listActivityDefinition operation.\n" + error.toString(), "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            }
            );
        } catch (final MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (final MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_listActivityDefinitionAllButtonActionPerformed

    private void removeActivityDefinitionAllButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeActivityDefinitionAllButtonActionPerformed
        final int resultConfirm = JOptionPane.showConfirmDialog(null,
            "All activity definitions will be removed. Do you want to continue?",
            "Warning", JOptionPane.OK_CANCEL_OPTION);
        if (resultConfirm == JOptionPane.CANCEL_OPTION) return;

        final LongList identityIds = new LongList();
        identityIds.add(0L);

        try {
            this.pimService.getPlanInformationManagementStub().asyncRemoveActivityDef(identityIds, new PlanInformationManagementAdapter() {
                @Override
                public void removeActivityDefAckReceived(final MALMessageHeader msgHeader, final Map qosProperties) {
                    listActivityDefinitionAllButtonActionPerformed(null);
                }

                @Override
                public void removeActivityDefErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the removeActivityDef operation.\n" + error.toString() , "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            });
        } catch (final MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (final MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_removeActivityDefinitionAllButtonActionPerformed

    private void addEventDefinitionButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEventDefinitionButtonActionPerformed
        final EventDefinitionDetails definition = new EventDefinitionDetails();

        definition.setDescription("Test event definition");
        definition.setVersion("Test version");

        final Identifier identity = new Identifier("Test event identity");
        final MOWindow identityWindow = new MOWindow(identity, true);

        final IdentifierList identities = new IdentifierList();

        try {
            identities.add((Identifier) identityWindow.getObject());
        } catch (final InterruptedIOException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return;
        }

        final MOWindow definitionWindow = new MOWindow(definition, true);

        final EventDefinitionDetailsList definitions = new EventDefinitionDetailsList();

        try {
            definitions.add((EventDefinitionDetails) definitionWindow.getObject());
        } catch (final InterruptedIOException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return;
        }

        try {
            this.pimService.getPlanInformationManagementStub().asyncAddEventDef(identities, definitions, new PlanInformationManagementAdapter() {
                @Override
                public void addEventDefResponseReceived(final MALMessageHeader msgHeader, final ObjectInstancePairList objIds, final Map qosProperties) {
                    listEventDefinitionAllButtonActionPerformed(null);
                }

                @Override
                public void addEventDefErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the addEventDef operation.\n" + error.toString() , "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            });
        } catch (final MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (final MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_addEventDefinitionButtonActionPerformed

    private void updateEventDefinitionButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateEventDefinitionButtonActionPerformed
        if (eventDefTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Please select an event definition", "Info", JOptionPane.PLAIN_MESSAGE);
            return; // No rows selected
        }

        final ArchivePersistenceObject selectedObject = eventDefTable.getSelectedCOMObject();
        final MOWindow moObject = new MOWindow(selectedObject.getObject(), true);

        final LongList identityIds = new LongList();
        identityIds.add(eventDefTable.getSelectedIdentityObjId());

        final EventDefinitionDetailsList definitions = new EventDefinitionDetailsList();
        try {
            definitions.add((EventDefinitionDetails) moObject.getObject());
        } catch (final InterruptedIOException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return;
        }

        try {
            this.pimService.getPlanInformationManagementStub().asyncUpdateEventDef(identityIds, definitions, new PlanInformationManagementAdapter() {
                @Override
                public void updateEventDefResponseReceived(final MALMessageHeader msgHeader, final LongList defID, final Map qosProperties) {
                    listEventDefinitionAllButtonActionPerformed(null);
                }

                @Override
                public void updateEventDefErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the updateEventDef operation.\n" + error.toString() , "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            });
        } catch (final MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (final MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_updateEventDefinitionButtonActionPerformed

    private void removeEventDefinitionButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeEventDefinitionButtonActionPerformed
        if (eventDefTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Please select an event definition", "Info", JOptionPane.PLAIN_MESSAGE);
            return; // No rows selected
        }

        final LongList identityIds = new LongList();
        identityIds.add(eventDefTable.getSelectedIdentityObjId());

        try {
            this.pimService.getPlanInformationManagementStub().asyncRemoveEventDef(identityIds, new PlanInformationManagementAdapter() {
                @Override
                public void removeEventDefAckReceived(final MALMessageHeader msgHeader, final Map qosProperties) {
                    listEventDefinitionAllButtonActionPerformed(null);
                }

                @Override
                public void removeEventDefErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the removeEventDef operation.\n" + error.toString() , "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            });
        } catch (final MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (final MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_removeEventDefinitionButtonActionPerformed

    private void listEventDefinitionAllButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listEventDefinitionAllButtonActionPerformed
        final IdentifierList idList = new IdentifierList();
        idList.add(new Identifier("*"));

        try {
            this.pimService.getPlanInformationManagementStub().asyncListEventDefs(idList, new PlanInformationManagementAdapter() {
                @Override
                public void listEventDefsResponseReceived(final MALMessageHeader msgHeader, final ObjectInstancePairList defInstIds, final Map qosProperties) {
                    final org.ccsds.moims.mo.mc.structures.ObjectInstancePairList ids = new org.ccsds.moims.mo.mc.structures.ObjectInstancePairList();
                    for (final ObjectInstancePair defId : defInstIds) {
                        ids.add(new org.ccsds.moims.mo.mc.structures.ObjectInstancePair(
                            defId.getObjectIdentityInstanceId(),
                            defId.getObjectInstanceId()
                        ));
                    }
                    eventDefTable.refreshTableWithIds(ids, pimService.getConnectionDetails().getDomain(), PlanInformationManagementHelper.EVENTDEFINITION_OBJECT_TYPE);
                    jScrollPane2.setViewportView(eventDefTable);
                    LOGGER.log(Level.INFO, "listEventDefinition(\"*\") returned {0} object instance identifiers", defInstIds.size());
                    if (ids.isEmpty() && evt != null) {
                        JOptionPane.showMessageDialog(null, "No event definitions found", "Info", JOptionPane.PLAIN_MESSAGE);
                    }
                }

                @Override
                public void listEventDefsErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the listEventDefinition operation.\n" + error.toString(), "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            }
            );
        } catch (final MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (final MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_listEventDefinitionAllButtonActionPerformed

    private void removeEventDefinitionAllButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeEventDefinitionAllButtonActionPerformed
        final int resultConfirm = JOptionPane.showConfirmDialog(null,
            "All event definitions will be removed. Do you want to continue?",
            "Warning", JOptionPane.OK_CANCEL_OPTION);
        if (resultConfirm == JOptionPane.CANCEL_OPTION) return;

        final LongList identityIds = new LongList();
        identityIds.add(0L);

        try {
            this.pimService.getPlanInformationManagementStub().asyncRemoveEventDef(identityIds, new PlanInformationManagementAdapter() {
                @Override
                public void removeEventDefAckReceived(final MALMessageHeader msgHeader, final Map qosProperties) {
                    listEventDefinitionAllButtonActionPerformed(null);
                }

                @Override
                public void removeEventDefErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the removeEventDef operation.\n" + error.toString() , "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            });
        } catch (final MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (final MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_removeEventDefinitionAllButtonActionPerformed

    private void addResourceDefinitionButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addResourceDefinitionButtonActionPerformed
        final c_ResourceDefinitionDetails definition = new c_ResourceDefinitionDetails();

        final NumericResourceDefinitionDetails numericResourceDef = new NumericResourceDefinitionDetails();
        numericResourceDef.setDescription("Test numeric resource");
        definition.setNumericResourceDef(numericResourceDef);

        final StringResourceDefinitionDetails stringResourceDef = new StringResourceDefinitionDetails();
        stringResourceDef.setDescription("Test string resource");
        definition.setStringResourceDef(stringResourceDef);

        final StatusResourceDefinitionDetails statusResourceDef = new StatusResourceDefinitionDetails();
        statusResourceDef.setDescription("Test status resource");
        definition.setStatusResourceDef(statusResourceDef);

        final Identifier identity = new Identifier("Test resource identity");
        final MOWindow identityWindow = new MOWindow(identity, true);

        final IdentifierList identities = new IdentifierList();

        try {
            identities.add((Identifier) identityWindow.getObject());
        } catch (final InterruptedIOException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return;
        }

        final MOWindow definitionWindow = new MOWindow(definition, true);

        final c_ResourceDefinitionDetailsList definitions = new c_ResourceDefinitionDetailsList();

        try {
            definitions.add((c_ResourceDefinitionDetails) definitionWindow.getObject());
        } catch (final InterruptedIOException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return;
        }

        try {
            this.pimService.getPlanInformationManagementStub().asyncAddResourceDef(identities, definitions, new PlanInformationManagementAdapter() {
                @Override
                public void addResourceDefResponseReceived(final MALMessageHeader msgHeader, final ObjectInstancePairList objIds, final Map qosProperties) {
                    listResourceDefinitionAllButtonActionPerformed(null);
                }

                @Override
                public void addResourceDefErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the addResourceDef operation.\n" + error.toString() , "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            });
        } catch (final MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (final MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_addResourceDefinitionButtonActionPerformed

    private void updateResourceDefinitionButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateResourceDefinitionButtonActionPerformed
        if (resourceDefTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Please select a resource definition", "Info", JOptionPane.PLAIN_MESSAGE);
            return; // No rows selected
        }

        final ArchivePersistenceObject selectedObject = resourceDefTable.getSelectedCOMObject();
        final MOWindow moObject = new MOWindow(selectedObject.getObject(), true);

        final LongList objIds = new LongList();
        objIds.add(resourceDefTable.getSelectedIdentityObjId());

        final c_ResourceDefinitionDetailsList definitions = new c_ResourceDefinitionDetailsList();
        try {
            definitions.add((c_ResourceDefinitionDetails) moObject.getObject());
        } catch (final InterruptedIOException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return;
        }

        try {
            this.pimService.getPlanInformationManagementStub().asyncUpdateResourceDef(objIds, definitions, new PlanInformationManagementAdapter() {
                @Override
                public void updateResourceDefResponseReceived(final MALMessageHeader msgHeader, final LongList defID, final Map qosProperties) {
                    listResourceDefinitionAllButtonActionPerformed(null);
                }

                @Override
                public void updateResourceDefErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the updateResourceDef operation.\n" + error.toString() , "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            });
        } catch (final MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (final MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_updateResourceDefinitionButtonActionPerformed

    private void removeResourceDefinitionButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeResourceDefinitionButtonActionPerformed
        if (resourceDefTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Please select a resource definition", "Info", JOptionPane.PLAIN_MESSAGE);
            return; // No rows selected
        }

        final LongList identityIds = new LongList();
        identityIds.add(resourceDefTable.getSelectedIdentityObjId());

        try {
            this.pimService.getPlanInformationManagementStub().asyncRemoveResourceDef(identityIds, new PlanInformationManagementAdapter() {
                @Override
                public void removeResourceDefAckReceived(final MALMessageHeader msgHeader, final Map qosProperties) {
                    listResourceDefinitionAllButtonActionPerformed(null);
                }

                @Override
                public void removeResourceDefErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the removeResourceDef operation.\n" + error.toString() , "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            });
        } catch (final MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (final MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_removeResourceDefinitionButtonActionPerformed

    private void listResourceDefinitionAllButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listResourceDefinitionAllButtonActionPerformed
        final IdentifierList idList = new IdentifierList();
        idList.add(new Identifier("*"));

        try {
            this.pimService.getPlanInformationManagementStub().asyncListResourceDefs(idList, new PlanInformationManagementAdapter() {
                @Override
                public void listResourceDefsResponseReceived(final MALMessageHeader msgHeader, final ObjectInstancePairList defInstIds, final Map qosProperties) {
                    final org.ccsds.moims.mo.mc.structures.ObjectInstancePairList ids = new org.ccsds.moims.mo.mc.structures.ObjectInstancePairList();
                    for (final ObjectInstancePair defId : defInstIds) {
                        ids.add(new org.ccsds.moims.mo.mc.structures.ObjectInstancePair(
                            defId.getObjectIdentityInstanceId(),
                            defId.getObjectInstanceId()
                        ));
                    }
                    resourceDefTable.refreshTableWithIds(ids, pimService.getConnectionDetails().getDomain(), PlanInformationManagementHelper.RESOURCEDEFINITION_OBJECT_TYPE);
                    jScrollPane2.setViewportView(resourceDefTable);
                    LOGGER.log(Level.INFO, "listResourceDefinition(\"*\") returned {0} object instance identifiers", defInstIds.size());
                    if (ids.isEmpty() && evt != null) {
                        JOptionPane.showMessageDialog(null, "No resource definitions found", "Info", JOptionPane.PLAIN_MESSAGE);
                    }
                }

                @Override
                public void listResourceDefsErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the listResourceDefinition operation.\n" + error.toString(), "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            }
            );
        } catch (final MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (final MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_listResourceDefinitionAllButtonActionPerformed

    private void removeResourceDefinitionAllButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeResourceDefinitionAllButtonActionPerformed
        final int resultConfirm = JOptionPane.showConfirmDialog(null,
            "All resource definitions will be removed. Do you want to continue?",
            "Warning", JOptionPane.OK_CANCEL_OPTION);
        if (resultConfirm == JOptionPane.CANCEL_OPTION) return;

        final LongList identityIds = new LongList();
        identityIds.add(0L);

        try {
            this.pimService.getPlanInformationManagementStub().asyncRemoveResourceDef(identityIds, new PlanInformationManagementAdapter() {
                @Override
                public void removeResourceDefAckReceived(final MALMessageHeader msgHeader, final Map qosProperties) {
                    listResourceDefinitionAllButtonActionPerformed(null);
                }

                @Override
                public void removeResourceDefErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the removeResourceDef operation.\n" + error.toString() , "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            });
        } catch (final MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (final MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_removeResourceDefinitionAllButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable actionDefinitionsTable;
    private javax.swing.JButton addActivityDefButton;
    private javax.swing.JButton addEventDefButton;
    private javax.swing.JButton addRequestDefButton;
    private javax.swing.JButton addResourceDefButton;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton listActivityDefsButton;
    private javax.swing.JButton listEventDefsButton;
    private javax.swing.JButton listRequestDefsButton;
    private javax.swing.JButton listResourceDefsButton;
    private javax.swing.JPanel parameterTab;
    private javax.swing.JButton removeActivityDefButton;
    private javax.swing.JButton removeAllActivityDefButton;
    private javax.swing.JButton removeAllEventDefButton;
    private javax.swing.JButton removeAllRequestDefButton;
    private javax.swing.JButton removeAllResourceDefButton;
    private javax.swing.JButton removeEventDefButton;
    private javax.swing.JButton removeRequestDefButton;
    private javax.swing.JButton removeResourceDefButton;
    private javax.swing.JButton updateActivityDefButton;
    private javax.swing.JButton updateEventDefButton;
    private javax.swing.JButton updateRequestDefButton;
    private javax.swing.JButton updateResourceDefButton;
    // End of variables declaration//GEN-END:variables
}
