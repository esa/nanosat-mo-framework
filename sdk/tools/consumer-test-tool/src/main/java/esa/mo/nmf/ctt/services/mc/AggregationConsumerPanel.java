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
package esa.mo.nmf.ctt.services.mc;

import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.mc.impl.consumer.AggregationConsumerServiceImpl;
import esa.mo.tools.mowindow.MOWindow;
import java.io.InterruptedIOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPair;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPairList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.aggregation.AggregationHelper;
import org.ccsds.moims.mo.mc.aggregation.consumer.AggregationAdapter;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationCreationRequest;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationCreationRequestList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetails;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetailsList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSet;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSetList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationSetValue;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationValue;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationValueDetails;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationValueDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePair;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePairList;

/**
 *
 * @author Cesar Coelho
 */
public class AggregationConsumerPanel extends javax.swing.JPanel {

    private final AggregationConsumerServiceImpl serviceMCAggregation;
    private final AggregationTablePanel aggregationTable;
    private Subscription subscription;

    /**
     *
     * @param serviceMCAggregation
     */
    public AggregationConsumerPanel(final AggregationConsumerServiceImpl serviceMCAggregation) {
        initComponents();

        this.serviceMCAggregation = serviceMCAggregation;
        aggregationTable = new AggregationTablePanel(serviceMCAggregation.getCOMServices().getArchiveService());
        jScrollPane2.setViewportView(aggregationTable);
    }

    public void init() {
        this.listDefinitionAllButtonActionPerformed(null);

        // Subscribe to ParametersValues
        subscription = ConnectionConsumer.subscriptionWildcard();
        try {
            serviceMCAggregation.getAggregationStub().monitorValueRegister(subscription, new AggregationConsumerAdapter());
        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(AggregationConsumerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeNotify()
    {
        super.removeNotify();
        final IdentifierList ids = new IdentifierList();
        ids.add(subscription.getSubscriptionId());
        try {
            serviceMCAggregation.getAggregationStub().monitorValueDeregister(ids);
        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(AggregationConsumerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /**
     * This method is called from within the constructor to initialize the
     * formAddModifyParameter. WARNING: Do NOT modify this code. The content of
     * this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        actionDefinitionsTable = new javax.swing.JTable();
        parameterTab = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        getValueButtonAgg1 = new javax.swing.JButton();
        getValueAllButtonAgg = new javax.swing.JButton();
        enableDefinitionButtonAgg = new javax.swing.JButton();
        enableDefinitionAllAgg = new javax.swing.JButton();
        enableFilterButtonAgg = new javax.swing.JButton();
        enableFilterAllAgg = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        addDefinitionButton = new javax.swing.JButton();
        updateDefinitionButton = new javax.swing.JButton();
        removeDefinitionButton = new javax.swing.JButton();
        listDefinitionButton = new javax.swing.JButton();
        listDefinitionAllButton = new javax.swing.JButton();
        removeDefinitionAllButton = new javax.swing.JButton();
        msgBoxOn = new javax.swing.JCheckBox();

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Aggregation Service - Definitions");
        jLabel6.setToolTipText("");

        jScrollPane2.setHorizontalScrollBar(null);
        jScrollPane2.setPreferredSize(new java.awt.Dimension(796, 380));
        jScrollPane2.setRequestFocusEnabled(false);

        actionDefinitionsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, Boolean.TRUE, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Obj Inst Id", "name", "description", "rawType", "rawUnit", "generationEnabled", "updateInterval"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Float.class
            };

            public Class getColumnClass(final int columnIndex) {
                return types [columnIndex];
            }
        });
        actionDefinitionsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        actionDefinitionsTable.setAutoscrolls(false);
        actionDefinitionsTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        actionDefinitionsTable.setMaximumSize(null);
        actionDefinitionsTable.setMinimumSize(null);
        actionDefinitionsTable.setPreferredSize(null);
        actionDefinitionsTable.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(final java.awt.event.ContainerEvent evt) {
                actionDefinitionsTableComponentAdded(evt);
            }
        });
        jScrollPane2.setViewportView(actionDefinitionsTable);

        parameterTab.setLayout(new java.awt.GridLayout(2, 1));

        getValueButtonAgg1.setText("getValue");
        getValueButtonAgg1.addActionListener(this::getValueButtonAgg1ActionPerformed);
        jPanel1.add(getValueButtonAgg1);

        getValueAllButtonAgg.setText("getValue(0)");
        getValueAllButtonAgg.addActionListener(this::getValueAllButtonAggActionPerformed);
        jPanel1.add(getValueAllButtonAgg);

        enableDefinitionButtonAgg.setText("enableGeneration");
        enableDefinitionButtonAgg.addActionListener(this::enableDefinitionButtonAggActionPerformed);
        jPanel1.add(enableDefinitionButtonAgg);

        enableDefinitionAllAgg.setText("enableGeneration(group=false, 0)");
        enableDefinitionAllAgg.addActionListener(this::enableDefinitionAllAggActionPerformed);
        jPanel1.add(enableDefinitionAllAgg);

        enableFilterButtonAgg.setText("enableFilter");
        enableFilterButtonAgg.addActionListener(this::enableFilterButtonAggActionPerformed);
        jPanel1.add(enableFilterButtonAgg);

        enableFilterAllAgg.setText("enableFilter(group=false, 0)");
        enableFilterAllAgg.addActionListener(this::enableFilterAllAggActionPerformed);
        jPanel1.add(enableFilterAllAgg);

        parameterTab.add(jPanel1);

        addDefinitionButton.setText("addDefinition");
        addDefinitionButton.addActionListener(this::addDefinitionButtonActionPerformed);
        jPanel5.add(addDefinitionButton);

        updateDefinitionButton.setText("updateDefinition");
        updateDefinitionButton.addActionListener(this::updateDefinitionButtonActionPerformed);
        jPanel5.add(updateDefinitionButton);

        removeDefinitionButton.setText("removeDefinition");
        removeDefinitionButton.addActionListener(this::removeDefinitionButtonActionPerformed);
        jPanel5.add(removeDefinitionButton);

        listDefinitionButton.setText("listDefinition()");
        listDefinitionButton.addActionListener(this::listDefinitionButtonActionPerformed);
        jPanel5.add(listDefinitionButton);

        listDefinitionAllButton.setText("listDefinition(\"*\")");
        listDefinitionAllButton.addActionListener(this::listDefinitionAllButtonActionPerformed);
        jPanel5.add(listDefinitionAllButton);

        removeDefinitionAllButton.setText("removeDefinition(0)");
        removeDefinitionAllButton.addActionListener(this::removeDefinitionAllButtonActionPerformed);
        jPanel5.add(removeDefinitionAllButton);

        msgBoxOn.setText("Display Published AggregationValues");
        msgBoxOn.addActionListener(this::msgBoxOnActionPerformed);
        jPanel5.add(msgBoxOn);

        parameterTab.add(jPanel5);

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(parameterTab, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 963, Short.MAX_VALUE)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(parameterTab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void listDefinitionButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listDefinitionButtonActionPerformed
        final IdentifierList aggregationNames = new IdentifierList();
        final MOWindow aggregationNamesWindow = new MOWindow(aggregationNames, true);

        try {
            final IdentifierList list;
            try {
                list = (IdentifierList) aggregationNamesWindow.getObject();
            } catch (final InterruptedIOException ex) {
                return;
            }

            final ObjectInstancePairList objIds = this.serviceMCAggregation.getAggregationStub().listDefinition(list);

            final StringBuilder str = new StringBuilder("Object instance identifiers on the provider: \n");
            for (final ObjectInstancePair objId : objIds) {
                str.append("ObjId Def: ").append(objId.getObjDefInstanceId().toString()).append(" Identity: ").append(objId.getObjIdentityInstanceId().toString()).append("\n");
            }

            JOptionPane.showMessageDialog(null, str.toString(), "Returned List from the Provider", JOptionPane.PLAIN_MESSAGE);
        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(AggregationConsumerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_listDefinitionButtonActionPerformed

    private void addDefinitionButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDefinitionButtonActionPerformed
        // Create and Show the Action Definition to the user
        final AggregationDefinitionDetails aggregationDefinition = new AggregationDefinitionDetails();
        aggregationDefinition.setDescription("A aggregation of 2 parameters.");
        aggregationDefinition.setCategory(new UOctet((short) 0));
        aggregationDefinition.setGenerationEnabled(true);
        aggregationDefinition.setReportInterval(new Duration(2));
        aggregationDefinition.setFilterEnabled(false);
        aggregationDefinition.setSendDefinitions(false);
        aggregationDefinition.setSendUnchanged(false);
        aggregationDefinition.setFilteredTimeout(new Duration(5));

        final AggregationParameterSetList aggSetList = new AggregationParameterSetList();

        final AggregationParameterSet aggSet = new AggregationParameterSet();
        aggSet.setDomain(this.serviceMCAggregation.getConnectionDetails().getDomain());
        aggSet.setParameters(new LongList());
        aggSet.setSampleInterval(new Duration(2));
        aggSet.setReportFilter(null);

        aggSetList.add(aggSet);
        aggregationDefinition.setParameterSets(aggSetList);

        final AggregationCreationRequest request = new AggregationCreationRequest();
        request.setAggDefDetails(aggregationDefinition);
        request.setName(new Identifier("Map"));
        final MOWindow aggregationDefinitionWindow = new MOWindow(request, true);

        final AggregationCreationRequestList requestList = new AggregationCreationRequestList();
        try {
            requestList.add((AggregationCreationRequest) aggregationDefinitionWindow.getObject());
        } catch (final InterruptedIOException ex) {
            return;
        }

        try {
            final ObjectInstancePairList objIds = this.serviceMCAggregation.getAggregationStub().addAggregation(requestList);

            if (objIds.isEmpty()) {
                return;
            }

            // Get the stored Parameter Definition from the Archive
            final ArchivePersistenceObject comObject = HelperArchive.getArchiveCOMObject(
                    this.serviceMCAggregation.getCOMServices().getArchiveService().getArchiveStub(),
                    AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE,
                    serviceMCAggregation.getConnectionDetails().getDomain(),
                    objIds.get(0).getObjDefInstanceId());

            // Add the Action Definition to the table
            aggregationTable.addEntry(requestList.get(0).getName(), comObject);
        } catch (final MALInteractionException | MALException ex) {
            JOptionPane.showMessageDialog(null, "There was an error with the submitted Aggregation Definition.", "Error", JOptionPane.PLAIN_MESSAGE);
            Logger.getLogger(AggregationConsumerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_addDefinitionButtonActionPerformed

    private void updateDefinitionButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateDefinitionButtonActionPerformed
        if (aggregationTable.getSelectedRow() == -1) { // The row is not selected?
            return;  // Well, then nothing to be done here folks!
        }

        final ArchivePersistenceObject obj = aggregationTable.getSelectedCOMObject();
        final MOWindow moObject = new MOWindow(obj.getObject(), true);

        final LongList objIds = new LongList();
        objIds.add(aggregationTable.getSelectedIdentityObjId());

        final AggregationDefinitionDetailsList defs = new AggregationDefinitionDetailsList();
        try {
            defs.add((AggregationDefinitionDetails) moObject.getObject());
        } catch (final InterruptedIOException ex) {
            return;
        }

        try {
            this.serviceMCAggregation.getAggregationStub().updateDefinition(objIds, defs);
            this.listDefinitionAllButtonActionPerformed(null);
        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(AggregationConsumerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_updateDefinitionButtonActionPerformed

    private void removeDefinitionButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeDefinitionButtonActionPerformed
        if (aggregationTable.getSelectedRow() == -1) { // The row is not selected?
            return;  // Well, then nothing to be done here folks!
        }

        final LongList longlist = new LongList();
        longlist.add(aggregationTable.getSelectedIdentityObjId());

        try {
            this.serviceMCAggregation.getAggregationStub().removeAggregation(longlist);
            aggregationTable.removeSelectedEntry();
        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(AggregationConsumerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_removeDefinitionButtonActionPerformed

    private void listDefinitionAllButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listDefinitionAllButtonActionPerformed
        final IdentifierList idList = new IdentifierList();
        idList.add(new Identifier("*"));
        /*
        ObjectInstancePairList output;
        try {
            output = this.serviceMCAggregation.getAggregationStub().listDefinition(idList);
            aggregationTable.refreshTableWithIds(output, serviceMCAggregation.getConnectionDetails().getDomain(), AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE);
        } catch (MALInteractionException ex) {
            JOptionPane.showMessageDialog(null, "There was an error during the listDefinition operation.", "Error", JOptionPane.PLAIN_MESSAGE);
            Logger.getLogger(AggregationConsumerPanel.class.getName()).log(Level.SEVERE, null, ex);
            return;
        } catch (MALException ex) {
            JOptionPane.showMessageDialog(null, "There was an error during the listDefinition operation.", "Error", JOptionPane.PLAIN_MESSAGE);
            Logger.getLogger(AggregationConsumerPanel.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        Logger.getLogger(AggregationConsumerPanel.class.getName()).log(Level.INFO, "listDefinition(\"*\") returned {0} object instance identifiers", output.size());
         */

        try {
            this.serviceMCAggregation.getAggregationStub().asyncListDefinition(idList, new AggregationAdapter() {
                @Override
                public void listDefinitionResponseReceived(final MALMessageHeader msgHeader, final ObjectInstancePairList objInstIds, final Map qosProperties) {
                    aggregationTable.refreshTableWithIds(objInstIds, serviceMCAggregation.getConnectionDetails().getDomain(), AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE);
                    Logger.getLogger(AggregationConsumerPanel.class.getName()).log(Level.INFO, "listDefinition(\"*\") returned {0} object instance identifiers", objInstIds.size());
                }

                @Override
                public void listDefinitionErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
                    JOptionPane.showMessageDialog(null, "There was an error during the listDefinition operation.", "Error", JOptionPane.PLAIN_MESSAGE);
                    Logger.getLogger(AggregationConsumerPanel.class.getName()).log(Level.SEVERE, null, error);
                }
            }
            );
        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(AggregationConsumerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_listDefinitionAllButtonActionPerformed

    private void removeDefinitionAllButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeDefinitionAllButtonActionPerformed
        final Long objId = (long) 0;
        final LongList longlist = new LongList();
        longlist.add(objId);

        try {
            this.serviceMCAggregation.getAggregationStub().removeAggregation(longlist);
            aggregationTable.removeAllEntries();
        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(AggregationConsumerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_removeDefinitionAllButtonActionPerformed

    private void actionDefinitionsTableComponentAdded(final java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_actionDefinitionsTableComponentAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_actionDefinitionsTableComponentAdded

    private void enableDefinitionAllAggActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enableDefinitionAllAggActionPerformed
        final Boolean curState;

        if (aggregationTable.getSelectedRow() == -1) {  // Used to avoid problems if no row is selected
            final AggregationDefinitionDetails aggregationDefinition = (AggregationDefinitionDetails) aggregationTable.getFirstCOMObject().getObject();
            if (aggregationDefinition != null) {
                curState = aggregationDefinition.getGenerationEnabled();
            } else {
                curState = true;
            }
        } else {
            curState = ((AggregationDefinitionDetails) aggregationTable.getSelectedCOMObject().getObject()).getGenerationEnabled();
        }

        final InstanceBooleanPairList BoolPairList = new InstanceBooleanPairList();
        BoolPairList.add(new InstanceBooleanPair((long) 0, !curState));  // Zero is the wildcard

        try {
            this.serviceMCAggregation.getAggregationStub().enableGeneration(false, BoolPairList);
            aggregationTable.switchEnabledstatusAll(!curState);
        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(AggregationConsumerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_enableDefinitionAllAggActionPerformed

    private void enableDefinitionButtonAggActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enableDefinitionButtonAggActionPerformed
        if (aggregationTable.getSelectedRow() == -1) { // The row is not selected?
            return;  // Well, then nothing to be done here folks!
        }

        final Boolean curState = ((AggregationDefinitionDetails) aggregationTable.getSelectedCOMObject().getObject()).getGenerationEnabled();
        final InstanceBooleanPairList BoolPairList = new InstanceBooleanPairList();
        BoolPairList.add(new InstanceBooleanPair(aggregationTable.getSelectedIdentityObjId(), !curState));  // Zero is the wildcard

        try {
            this.serviceMCAggregation.getAggregationStub().enableGeneration(false, BoolPairList);
            aggregationTable.switchEnabledstatus(!curState);
        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(AggregationConsumerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_enableDefinitionButtonAggActionPerformed

    private void msgBoxOnActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_msgBoxOnActionPerformed


    }//GEN-LAST:event_msgBoxOnActionPerformed

    private void getValueAllButtonAggActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getValueAllButtonAggActionPerformed
        final Long objId = (long) 0;
        final LongList longlist = new LongList();
        longlist.add(objId);

        try {
            final AggregationValueDetailsList values = serviceMCAggregation.getAggregationStub().getValue(longlist);

            final StringBuilder str = new StringBuilder();
            for (int h = 0; h < values.size(); h++) {
                final AggregationValueDetails value = values.get(h);

                str.append("The value for objId ").append(value.getAggId().toString()).append(" (AggregationValue index: ").append(h).append(") is:").append("\n");
                for (int i = 0; i < value.getValue().getParameterSetValues().size(); i++) {
                    for (int j = 0; j < value.getValue().getParameterSetValues().get(i).getValues().size(); j++) {
                        if (value.getValue().getParameterSetValues().get(i).getValues().get(j) == null) {
                            continue;
                        }

                        final ParameterValue paramValue = value.getValue().getParameterSetValues().get(i).getValues().get(j).getValue();
                        str.append("(parameterSetValue index: ").append(i).append(") ").append("validityState: ").append(paramValue.getValidityState().toString()).append("\n");

                        if (paramValue.getRawValue() != null) {
                            str.append("(parameterSetValue index: ").append(i).append(") ").append("rawValue: ").append(paramValue.getRawValue().toString()).append("\n");
                        }
                        if (paramValue.getConvertedValue() != null) {
                            str.append("(parameterSetValue index: ").append(i).append(") ").append("convertedValue: ").append(paramValue.getConvertedValue().toString()).append("\n");
                        }
                        str.append("\n");
                    }
                }
                str.append("---------------------------------------\n");
            }

            JOptionPane.showMessageDialog(null, str.toString(), "Returned List from the Provider", JOptionPane.PLAIN_MESSAGE);

        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(AggregationConsumerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_getValueAllButtonAggActionPerformed

    private void enableFilterAllAggActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enableFilterAllAggActionPerformed

        final String str;
        if (aggregationTable.getSelectedRow() == -1) {  // Used to avoid problems if no row is selected
            str = ((AggregationDefinitionDetails) aggregationTable.getSelectedCOMObject().getObject()).getFilterEnabled().toString(); // Get the status from selection
        } else {
            str = "true";
        }
        final boolean curState = (str.equals("true")); // String to Boolean conversion
        final InstanceBooleanPairList boolPairList = new InstanceBooleanPairList();
        boolPairList.add(new InstanceBooleanPair((long) 0, !curState));  // Zero is the wildcard
        try {
            serviceMCAggregation.getAggregationStub().enableFilter(false, boolPairList);  // false: no group service
            aggregationTable.switchFilterEnabledstatusAll(!curState);
        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(AggregationConsumerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_enableFilterAllAggActionPerformed

    private void enableFilterButtonAggActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enableFilterButtonAggActionPerformed
        if (aggregationTable.getSelectedRow() == -1) { // The row is not selected?
            return;  // Well, then nothing to be done here folks!
        }

        final Long objId = aggregationTable.getSelectedCOMObject().getArchiveDetails().getInstId();
        final Boolean curState = ((AggregationDefinitionDetails) aggregationTable.getSelectedCOMObject().getObject()).getFilterEnabled(); // String to Boolean conversion
        final InstanceBooleanPairList boolPairList = new InstanceBooleanPairList();
        boolPairList.add(new InstanceBooleanPair(objId, !curState));

        try {
            serviceMCAggregation.getAggregationStub().enableFilter(false, boolPairList);
            aggregationTable.switchFilterEnabledstatus(!curState);
        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(AggregationConsumerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_enableFilterButtonAggActionPerformed

    private void getValueButtonAgg1ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getValueButtonAgg1ActionPerformed
        if (aggregationTable.getSelectedRow() == -1) { // The row is not selected?
            return;  // Well, then nothing to be done here folks!
        }

        final Long objId = aggregationTable.getSelectedCOMObject().getArchiveDetails().getInstId();
        final LongList longlist = new LongList();
        longlist.add(objId);

        try {
            final AggregationValueDetailsList values = serviceMCAggregation.getAggregationStub().getValue(longlist);

            final StringBuilder str = new StringBuilder();
            for (int h = 0; h < values.size(); h++) {
                final AggregationValueDetails value = values.get(h);

                str.append("The value for objId ").append(value.getAggId().toString()).append(" (AggregationValue index: ").append(h).append(") is:").append("\n");
                for (int i = 0; i < value.getValue().getParameterSetValues().size(); i++) {
                    for (int j = 0; j < value.getValue().getParameterSetValues().get(i).getValues().size(); j++) {
                        if (value.getValue().getParameterSetValues().get(i).getValues().get(j) == null) {
                            continue;
                        }

                        final ParameterValue paramValue = value.getValue().getParameterSetValues().get(i).getValues().get(j).getValue();
                        str.append("(parameterSetValue index: ").append(i).append(") ").append("validityState: ").append(paramValue.getValidityState().toString()).append("\n");

                        if (paramValue.getRawValue() != null) {
                            str.append("(parameterSetValue index: ").append(i).append(") ").append("rawValue: ").append(paramValue.getRawValue().toString()).append("\n");
                        }
                        if (paramValue.getConvertedValue() != null) {
                            str.append("(parameterSetValue index: ").append(i).append(") ").append("convertedValue: ").append(paramValue.getConvertedValue().toString()).append("\n");
                        }
                        str.append("\n");
                    }
                }
                str.append("---------------------------------------\n");
            }

            JOptionPane.showMessageDialog(null, str.toString(), "Returned List from the Provider", JOptionPane.PLAIN_MESSAGE);

        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(AggregationConsumerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_getValueButtonAgg1ActionPerformed

    public class AggregationConsumerAdapter extends AggregationAdapter {

        @Override
        public void monitorValueNotifyReceived(final org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
                                               final org.ccsds.moims.mo.mal.structures.Identifier identifier,
                                               final org.ccsds.moims.mo.mal.structures.UpdateHeaderList lUpdateHeaderList,
                                               final org.ccsds.moims.mo.com.structures.ObjectIdList _ObjectIdList2,
                                               final org.ccsds.moims.mo.mc.aggregation.structures.AggregationValueList lAggregationValueList,
                                               final java.util.Map qosProperties) {

            final long iDiff = System.currentTimeMillis() - msgHeader.getTimestamp().getValue();

            final UpdateHeader updateHeader = lUpdateHeaderList.get(0);
            final String Aggname = updateHeader.getKey().getFirstSubKey().getValue();
            final int objId = updateHeader.getKey().getSecondSubKey().intValue();

            try {
                if (msgBoxOn.isSelected() && !lUpdateHeaderList.isEmpty() && lAggregationValueList.size() != 0) {
                    final StringBuilder str = new StringBuilder();
                    final AggregationValue aggregationValue = lAggregationValueList.get(0);
                    str.append("AggregationValue generationMode: ").append(aggregationValue.getGenerationMode().toString()).append(" (filtered: ").append(aggregationValue.getFiltered().toString()).append(")").append("\n");

                    str.append("Aggregation objId ").append(objId).append(" (name: ").append(Aggname).append("):").append("\n");

                    for (int i = 0; i < aggregationValue.getParameterSetValues().size(); i++) {  // Cycle through parameterSetValues
                        str.append("- AggregationParameterSet values index: ").append(i).append("\n");
                        str.append("deltaTime: ").append(aggregationValue.getParameterSetValues().get(i).getDeltaTime());
                        str.append(" and intervalTime: ").append(aggregationValue.getParameterSetValues().get(i).getIntervalTime()).append("\n");
                        final AggregationSetValue parameterSetsValue = aggregationValue.getParameterSetValues().get(i);

                        for (int j = 0; j < parameterSetsValue.getValues().size(); j++) { // Cycle through the values
                            if (parameterSetsValue.getValues().get(j) == null) {
                                continue;
                            }

                            final ParameterValue paramValue = parameterSetsValue.getValues().get(j).getValue();

                            str.append("values index: ").append(j).append("\n");
                            str.append("validityState: ").append(paramValue.getValidityState().toString()).append("\n");
                            if (paramValue.getRawValue() != null) {
                                str.append("rawValue: ").append(paramValue.getRawValue().toString()).append("\n");
                            }
                            if (paramValue.getConvertedValue() != null) {
                                str.append("convertedValue: ").append(paramValue.getConvertedValue().toString()).append("\n");
                            }
                            str.append("\n");
                        }
                    }

                    JOptionPane.showMessageDialog(null, str.toString(), "Returned Values from the Provider", JOptionPane.PLAIN_MESSAGE);
                }

            } catch (final NumberFormatException ex) {
            }
        }
    }

    public AggregationTablePanel getAggregationTable() {
        return aggregationTable;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable actionDefinitionsTable;
    private javax.swing.JButton addDefinitionButton;
    private javax.swing.JButton enableDefinitionAllAgg;
    private javax.swing.JButton enableDefinitionButtonAgg;
    private javax.swing.JButton enableFilterAllAgg;
    private javax.swing.JButton enableFilterButtonAgg;
    private javax.swing.JButton getValueAllButtonAgg;
    private javax.swing.JButton getValueButtonAgg1;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton listDefinitionAllButton;
    private javax.swing.JButton listDefinitionButton;
    private javax.swing.JCheckBox msgBoxOn;
    private javax.swing.JPanel parameterTab;
    private javax.swing.JButton removeDefinitionAllButton;
    private javax.swing.JButton removeDefinitionButton;
    private javax.swing.JButton updateDefinitionButton;
    // End of variables declaration//GEN-END:variables
}
