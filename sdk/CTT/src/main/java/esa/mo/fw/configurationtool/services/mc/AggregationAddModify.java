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
package esa.mo.fw.configurationtool.services.mc;

import esa.mo.helpertools.connections.ConfigurationProvider;
import esa.mo.mc.impl.consumer.AggregationConsumerServiceImpl_old;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationCategory;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetails;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetailsList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSet;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSetList;
import org.ccsds.moims.mo.mc.aggregation.structures.ThresholdFilter;
import org.ccsds.moims.mo.mc.aggregation.structures.ThresholdType;

/**
 *
 * @author Cesar Coelho
 */
public class AggregationAddModify extends javax.swing.JFrame {

    private AggregationConsumerServiceImpl_old aggregationService;
    private Boolean isAddDef = false;
    private int aggregationDefinitionSelectedIndex = 0;
    private ConfigurationProvider configuration = new ConfigurationProvider();
    private DefaultTableModel parameterTableData;
    private DefaultTableModel aggregationTableData;
    private DefaultTableModel parameterSetsTableData;
    private String[] parameterSetsTableCol = new String[]{"Parameter", "sampleInterval", "th-type", "th-value"};

    /**
     * Creates new form ParameterAddModify
     *
     * @param aggregationService
     */
    public AggregationAddModify(AggregationConsumerServiceImpl_old aggregationService) {
        initComponents();

        this.aggregationService = aggregationService;
        this.aggregationTableData = aggregationService.getAggregationTableData();
        this.parameterTableData = aggregationService.getParameterConsumer().getParameterTableData();

        // Set window size for the Add and Modify Parameter Definition
        this.setSize(400, 700);
        this.setPreferredSize(new Dimension(400, 700));
        this.setResizable(false);
        this.setVisible(false);
    }

    public void setAggregationDefinitionSelectedIndex(int in) {
        this.aggregationDefinitionSelectedIndex = in;
    }

    // Get ObjId from Name (parameter table)
    private Long getObjIdFromName(String name) {
        for (int i = 0; i < parameterTableData.getRowCount(); i++) {
            String parameter = parameterTableData.getValueAt(i, 1).toString();
            if (parameter.equals(name)) {
                return new Long(parameterTableData.getValueAt(i, 0).toString());
            }
        }
        return null; // Not found (it shouldn't occur...)
    }

    // Get ObjId from Name
    private Long getParameterSetsObjIdFromAggNameAndIndex(String name, int index) {
        for (int i = 0; i < aggregationTableData.getRowCount(); i++) {
            String aggregation = aggregationTableData.getValueAt(i, 1).toString();
            if (aggregation.equals(name) && index <= aggregationService.getParameterSetsTableDataAll().get(i).getRowCount()) {
                return getObjIdFromName(aggregationService.getParameterSetsTableDataAll().get(i).getValueAt(index, 0).toString());
            }
        }
        return null; // Not found (it shouldn't occur...)
    }

    public AggregationDefinitionDetails makeNewAggregationDefinition(String name, String description, AggregationCategory category, boolean generationEnabled,
            float updateInterval, boolean filterEnabled, float filteredTimeout, AggregationParameterSetList parameterSets) {
        AggregationDefinitionDetails aDef = new AggregationDefinitionDetails();

        aDef.setName(new Identifier(name));
        aDef.setDescription(description);
        aDef.setCategory(category);
        aDef.setGenerationEnabled(generationEnabled);

        aDef.setUpdateInterval(new Duration(updateInterval));

        aDef.setFilterEnabled(filterEnabled);  // shall not matter, because when we add it it will be false!
        aDef.setFilteredTimeout(new Duration(filteredTimeout));
        aDef.setParameterSets(parameterSets);

        return aDef;
    }

    public AggregationParameterSetList makeNewAggregationParameterSetList() {
        AggregationParameterSetList aggRefList = new AggregationParameterSetList();

        for (int i = 0; i < parameterSetsTableData.getRowCount(); i++) {
            AggregationParameterSet aggRef = new AggregationParameterSet();
            aggRef.setDomain(aggregationService.getConnectionDetails().getDomain());
            LongList longList = new LongList();
            longList.add(getObjIdFromName(parameterSetsTableData.getValueAt(i, 0).toString()));
            aggRef.setParameters(longList);
            aggRef.setSampleInterval(new Duration(Float.parseFloat(parameterSetsTableData.getValueAt(i, 1).toString())));
            if (parameterSetsTableData.getValueAt(i, 2).equals("-")) {
                aggRef.setPeriodicFilter(null);
            } else {
                ThresholdFilter periodicFilter = new ThresholdFilter();
                periodicFilter.setThresholdType(ThresholdType.fromString(parameterSetsTableData.getValueAt(i, 2).toString()));
                periodicFilter.setThresholdValue(new Duration(Float.parseFloat(parameterSetsTableData.getValueAt(i, 3).toString())));
                aggRef.setPeriodicFilter(periodicFilter);
            }
            aggRefList.add(aggRef);
        }

        return aggRefList;
    }

    public void setUpdateParameterForm(javax.swing.JTable aggregationTable) {
        titleEditParameter.setText("Update Aggregation Definition");
        aggregationDefinitionSelectedIndex = aggregationTable.getSelectedRow();

        nameTF.setText(aggregationTable.getValueAt(aggregationDefinitionSelectedIndex, 1).toString());
        descriptionTF.setText(aggregationTable.getValueAt(aggregationDefinitionSelectedIndex, 2).toString());
        categoryCB.setSelectedItem(aggregationTable.getValueAt(aggregationDefinitionSelectedIndex, 3).toString());

        updateIntervalTF.setText(aggregationTable.getValueAt(aggregationDefinitionSelectedIndex, 5).toString());
        filteredTimeoutTF.setText(aggregationTable.getValueAt(aggregationDefinitionSelectedIndex, 7).toString());

        filterEnabledCB.setSelected(false);

        Boolean curState = (aggregationTable.getValueAt(aggregationTable.getSelectedRow(), 4).toString().equals("true")); // String to Boolean conversion
        generationEnabledCB.setSelected(curState);
        generationEnabledCB.setEnabled(true);

        curState = (aggregationTable.getValueAt(aggregationTable.getSelectedRow(), 6).toString().equals("true")); // String to Boolean conversion
        filterEnabledCB.setSelected(curState);

        parameterSetsTableData = new DefaultTableModel();
        parameterSetsTableData.setDataVector(aggregationService.getParameterSetsTableDataAll().get(aggregationDefinitionSelectedIndex).getDataVector(),
                new Vector<String>(Arrays.asList(parameterSetsTableCol)));
        parameterSetsTable.setModel(parameterSetsTableData);

        refreshParametersComboBox();

        isAddDef = false;

    }

    public void setAddParameterForm() {
        titleEditParameter.setText("Add a new Aggregation Definition");
        nameTF.setText("");
        descriptionTF.setText("");
        categoryCB.setSelectedIndex(0);  // Default dash

        updateIntervalTF.setText("");
        filteredTimeoutTF.setText("");
        generationEnabledCB.setSelected(false);
        generationEnabledCB.setEnabled(false);
        filterEnabledCB.setSelected(false);

        thresholdTypeCB.setSelectedIndex(0);
        thresholdValueTB.setText("");

        parameterSetsTableData = new DefaultTableModel();
        parameterSetsTableData.setColumnIdentifiers(new Vector<String>(Arrays.asList(parameterSetsTableCol)));
        parameterSetsTable.setModel(parameterSetsTableData);

        refreshParametersComboBox();
        isAddDef = true;
    }
    
    @SuppressWarnings("unchecked")
    private void refreshParametersComboBox(){
        parameterCB.removeAllItems();
        for (int i = 0; i < parameterTableData.getRowCount(); i++ ){
            parameterCB.addItem(parameterTableData.getValueAt(i, 1));
        }
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel13 = new javax.swing.JPanel();
        titleEditParameter = new javax.swing.JLabel();
        jSeparator9 = new javax.swing.JSeparator();
        jPanel14 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        nameTF = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        descriptionTF = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        categoryCB = new javax.swing.JComboBox();
        jLabel19 = new javax.swing.JLabel();
        updateIntervalTF = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        filteredTimeoutTF = new javax.swing.JTextField();
        generationEnabledCB = new javax.swing.JCheckBox();
        filterEnabledCB = new javax.swing.JCheckBox();
        jSeparator10 = new javax.swing.JSeparator();
        jPanel15 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        parameterCB = new javax.swing.JComboBox();
        sampleIntervalTB = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        thresholdTypeCB = new javax.swing.JComboBox();
        thresholdValueTB = new javax.swing.JTextField();
        aggregateParameterButton = new javax.swing.JButton();
        jSeparator12 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        parameterSetsTable = new javax.swing.JTable();
        removeParameter = new javax.swing.JButton();
        jSeparator13 = new javax.swing.JSeparator();
        submitButton1 = new javax.swing.JButton();

        setName("editParameter"); // NOI18N

        jPanel13.setMaximumSize(new java.awt.Dimension(400, 300));
        jPanel13.setMinimumSize(new java.awt.Dimension(400, 300));
        jPanel13.setPreferredSize(new java.awt.Dimension(400, 300));

        titleEditParameter.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        titleEditParameter.setText("Auto-change Label");
        jPanel13.add(titleEditParameter);

        jSeparator9.setPreferredSize(new java.awt.Dimension(250, 10));
        jPanel13.add(jSeparator9);

        jPanel14.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel14.setMaximumSize(new java.awt.Dimension(280, 400));
        jPanel14.setMinimumSize(new java.awt.Dimension(280, 400));
        jPanel14.setPreferredSize(new java.awt.Dimension(280, 520));

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("name");
        jLabel15.setPreferredSize(new java.awt.Dimension(100, 14));
        jPanel14.add(jLabel15);

        nameTF.setText("name");
        nameTF.setPreferredSize(new java.awt.Dimension(150, 20));
        jPanel14.add(nameTF);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("description");
        jLabel16.setPreferredSize(new java.awt.Dimension(100, 14));
        jPanel14.add(jLabel16);

        descriptionTF.setText("description");
        descriptionTF.setPreferredSize(new java.awt.Dimension(150, 20));
        jPanel14.add(descriptionTF);

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("category");
        jLabel17.setPreferredSize(new java.awt.Dimension(100, 14));
        jPanel14.add(jLabel17);

        categoryCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-", "GENERAL", "DIAGNOSTIC" }));
        categoryCB.setMaximumSize(new java.awt.Dimension(150, 20));
        categoryCB.setMinimumSize(new java.awt.Dimension(150, 20));
        categoryCB.setPreferredSize(new java.awt.Dimension(150, 20));
        jPanel14.add(categoryCB);

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("updateInterval");
        jLabel19.setPreferredSize(new java.awt.Dimension(100, 14));
        jPanel14.add(jLabel19);

        updateIntervalTF.setText("3");
        updateIntervalTF.setPreferredSize(new java.awt.Dimension(150, 20));
        updateIntervalTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateIntervalTFActionPerformed(evt);
            }
        });
        jPanel14.add(updateIntervalTF);

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("filteredTimeout");
        jLabel18.setPreferredSize(new java.awt.Dimension(100, 14));
        jPanel14.add(jLabel18);

        filteredTimeoutTF.setText("2");
        filteredTimeoutTF.setPreferredSize(new java.awt.Dimension(150, 20));
        jPanel14.add(filteredTimeoutTF);

        generationEnabledCB.setText("generationEnabled");
        generationEnabledCB.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        generationEnabledCB.setOpaque(false);
        generationEnabledCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generationEnabledCBActionPerformed(evt);
            }
        });
        jPanel14.add(generationEnabledCB);

        filterEnabledCB.setText("filterEnabled");
        filterEnabledCB.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        filterEnabledCB.setOpaque(false);
        filterEnabledCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterEnabledCBActionPerformed(evt);
            }
        });
        jPanel14.add(filterEnabledCB);

        jSeparator10.setMaximumSize(new java.awt.Dimension(250, 10));
        jSeparator10.setMinimumSize(new java.awt.Dimension(250, 10));
        jSeparator10.setPreferredSize(new java.awt.Dimension(250, 10));
        jPanel14.add(jSeparator10);

        jPanel15.setMaximumSize(new java.awt.Dimension(280, 70));
        jPanel15.setMinimumSize(new java.awt.Dimension(280, 70));
        jPanel15.setPreferredSize(new java.awt.Dimension(280, 120));

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("parameter");
        jLabel20.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel20.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel20.setPreferredSize(new java.awt.Dimension(130, 14));
        jPanel15.add(jLabel20);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("sampleInterval");
        jLabel22.setPreferredSize(new java.awt.Dimension(100, 14));
        jPanel15.add(jLabel22);

        parameterCB.setMaximumSize(new java.awt.Dimension(100, 20));
        parameterCB.setMinimumSize(new java.awt.Dimension(100, 20));
        parameterCB.setPreferredSize(new java.awt.Dimension(130, 20));
        parameterCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parameterCBActionPerformed(evt);
            }
        });
        jPanel15.add(parameterCB);

        sampleIntervalTB.setPreferredSize(new java.awt.Dimension(100, 20));
        sampleIntervalTB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sampleIntervalTBActionPerformed(evt);
            }
        });
        jPanel15.add(sampleIntervalTB);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("thresholdType");
        jLabel21.setPreferredSize(new java.awt.Dimension(130, 14));
        jPanel15.add(jLabel21);

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("thresholdValue");
        jLabel23.setPreferredSize(new java.awt.Dimension(100, 14));
        jPanel15.add(jLabel23);

        thresholdTypeCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-", "PERCENTAGE", "DELTA" }));
        thresholdTypeCB.setMaximumSize(new java.awt.Dimension(150, 20));
        thresholdTypeCB.setMinimumSize(new java.awt.Dimension(150, 20));
        thresholdTypeCB.setPreferredSize(new java.awt.Dimension(130, 20));
        jPanel15.add(thresholdTypeCB);

        thresholdValueTB.setPreferredSize(new java.awt.Dimension(100, 20));
        thresholdValueTB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thresholdValueTBActionPerformed(evt);
            }
        });
        jPanel15.add(thresholdValueTB);

        aggregateParameterButton.setText("Aggregate Parameter");
        aggregateParameterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aggregateParameterButtonActionPerformed(evt);
            }
        });
        jPanel15.add(aggregateParameterButton);

        jPanel14.add(jPanel15);

        jSeparator12.setMaximumSize(new java.awt.Dimension(250, 10));
        jSeparator12.setMinimumSize(new java.awt.Dimension(250, 10));
        jSeparator12.setPreferredSize(new java.awt.Dimension(250, 10));
        jPanel14.add(jSeparator12);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(280, 100));

        parameterSetsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "parameter", "sampleInterval", "th-Type", "th-Value"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        parameterSetsTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        parameterSetsTable.setMaximumSize(null);
        parameterSetsTable.setMinimumSize(null);
        parameterSetsTable.setPreferredSize(null);
        parameterSetsTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(parameterSetsTable);

        jPanel14.add(jScrollPane1);

        removeParameter.setText("Remove Parameter");
        removeParameter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeParameterActionPerformed(evt);
            }
        });
        jPanel14.add(removeParameter);

        jSeparator13.setMaximumSize(new java.awt.Dimension(250, 10));
        jSeparator13.setMinimumSize(new java.awt.Dimension(250, 10));
        jSeparator13.setPreferredSize(new java.awt.Dimension(250, 10));
        jPanel14.add(jSeparator13);

        submitButton1.setText("Submit");
        submitButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButton1ActionPerformed(evt);
            }
        });
        jPanel14.add(submitButton1);

        jPanel13.add(jPanel14);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 422, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(11, Short.MAX_VALUE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(11, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 579, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void updateIntervalTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateIntervalTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_updateIntervalTFActionPerformed

    private void generationEnabledCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generationEnabledCBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_generationEnabledCBActionPerformed

    private void filterEnabledCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterEnabledCBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filterEnabledCBActionPerformed

    private void parameterCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parameterCBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_parameterCBActionPerformed

    private void sampleIntervalTBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sampleIntervalTBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sampleIntervalTBActionPerformed

    private void thresholdValueTBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thresholdValueTBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_thresholdValueTBActionPerformed

    private void aggregateParameterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aggregateParameterButtonActionPerformed

        if (sampleIntervalTB.getText().equals("")
                || parameterCB.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null, "Please fill-in all the necessary fields!", "Warning!", JOptionPane.PLAIN_MESSAGE);
            return;
        }

        try {
            Double.parseDouble(sampleIntervalTB.getText());  // Check if it is a number
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null, "sampleInterval is not a number!", "Warning!", JOptionPane.PLAIN_MESSAGE);
            return;
        }

        Double thresholdValue = null;

        if (thresholdTypeCB.getSelectedIndex() != 0) {
            if (thresholdValueTB.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Please enter a thresholdValue!", "Warning!", JOptionPane.PLAIN_MESSAGE);
                return;
            }

            try {
                thresholdValue = Double.parseDouble(thresholdValueTB.getText());  // Check if it is a number
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(null, "thresholdValue is not a number!", "Warning!", JOptionPane.PLAIN_MESSAGE);
                return;
            }
        }

        parameterSetsTableData.addRow(
                new Object[]{parameterCB.getSelectedItem().toString(), Double.parseDouble(sampleIntervalTB.getText()),
                    thresholdTypeCB.getSelectedItem().toString(), thresholdValue}
        );
    }//GEN-LAST:event_aggregateParameterButtonActionPerformed

    private void removeParameterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeParameterActionPerformed
        if (parameterSetsTable.getSelectedRow() != -1) // Did we select a parameter?
        {
            parameterSetsTableData.removeRow(parameterSetsTable.getSelectedRow());
        }
    }//GEN-LAST:event_removeParameterActionPerformed

    private void submitButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButton1ActionPerformed

        if (nameTF.getText().equals("")
                || descriptionTF.getText().equals("")
                || categoryCB.getSelectedIndex() == 0
                || updateIntervalTF.getText().equals("")
                || filteredTimeoutTF.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Please fill-in all the necessary fields!", "Warning!", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        Float updateInterval;
        Float filteredTimeout;
        try {
            updateInterval = Float.parseFloat(updateIntervalTF.getText());  // Check if it is a number
            filteredTimeout = Float.parseFloat(filteredTimeoutTF.getText());  // Check if it is a number
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null, "updateInterval or filteredTimeout is not a number!", "Warning!", JOptionPane.PLAIN_MESSAGE);
            return;
        }

        AggregationDefinitionDetails aDef;
        aDef = makeNewAggregationDefinition(nameTF.getText(),
                descriptionTF.getText(),
                AggregationCategory.fromOrdinal(categoryCB.getSelectedIndex()),
                generationEnabledCB.isSelected(),
                updateInterval,
                filterEnabledCB.isSelected(),
                filteredTimeout,
                makeNewAggregationParameterSetList());

        AggregationDefinitionDetailsList aDefs = new AggregationDefinitionDetailsList();
        aDefs.add(aDef);
        this.setVisible(false);

        if (isAddDef) {  // Are we adding a new definition?
            Logger.getLogger(AggregationAddModify.class.getName()).log(Level.INFO, null, "addDefinition started (Aggregation)");
            LongList output = aggregationService.addDefinition(aDefs);
            Logger.getLogger(AggregationAddModify.class.getName()).log(Level.INFO, "addDefinition returned {0} object instance identifiers (Aggregation)", output.size());
            aggregationTableData.addRow(
                    new Object[]{output.get(0).intValue(), aDef.getName(), aDef.getDescription(),
                        categoryCB.getItemAt(aDef.getCategory().getOrdinal()).toString(), aDef.getGenerationEnabled(),
                        aDef.getUpdateInterval().getValue(), aDef.getFilterEnabled(), aDef.getFilteredTimeout().getValue()}
            );
            DefaultTableModel tmp = new DefaultTableModel();
            tmp.setDataVector(parameterSetsTableData.getDataVector(), new Vector<String>(Arrays.asList(parameterSetsTableCol)));
            aggregationService.getParameterSetsTableDataAll().add(tmp);

        } else {  // Well, then we are updating a previous selected definition
            Logger.getLogger(AggregationAddModify.class.getName()).log(Level.INFO, null, "updateDefinition started (Aggregation)");
            LongList objIds = new LongList();
            objIds.add(new Long(aggregationTableData.getValueAt(aggregationDefinitionSelectedIndex, 0).toString()));
            aggregationService.updateDefinition(objIds, aDefs);  // Execute the update
            aggregationTableData.removeRow(aggregationDefinitionSelectedIndex);
            aggregationTableData.insertRow(aggregationDefinitionSelectedIndex,
                    new Object[]{objIds.get(0).intValue(), aDef.getName(), aDef.getDescription(),
                        categoryCB.getItemAt(aDef.getCategory().getOrdinal()).toString(), aDef.getGenerationEnabled(),
                        aDef.getUpdateInterval().getValue(), aDef.getFilterEnabled(), aDef.getFilteredTimeout().getValue()}
            );
            DefaultTableModel tmp = new DefaultTableModel();
            tmp.setDataVector(parameterSetsTableData.getDataVector(), new Vector<String>(Arrays.asList(parameterSetsTableCol)));
            aggregationService.getParameterSetsTableDataAll().set(aggregationDefinitionSelectedIndex, tmp);
            Logger.getLogger(AggregationAddModify.class.getName()).log(Level.INFO, null, "updateDefinition executed (Aggregation)");
        }

//        this.save2File();
    }//GEN-LAST:event_submitButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aggregateParameterButton;
    private javax.swing.JComboBox categoryCB;
    private javax.swing.JTextField descriptionTF;
    private javax.swing.JCheckBox filterEnabledCB;
    private javax.swing.JTextField filteredTimeoutTF;
    private javax.swing.JCheckBox generationEnabledCB;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JTextField nameTF;
    private javax.swing.JComboBox parameterCB;
    private javax.swing.JTable parameterSetsTable;
    private javax.swing.JButton removeParameter;
    private javax.swing.JTextField sampleIntervalTB;
    private javax.swing.JButton submitButton1;
    private javax.swing.JComboBox thresholdTypeCB;
    private javax.swing.JTextField thresholdValueTB;
    private javax.swing.JLabel titleEditParameter;
    private javax.swing.JTextField updateIntervalTF;
    // End of variables declaration//GEN-END:variables
}
