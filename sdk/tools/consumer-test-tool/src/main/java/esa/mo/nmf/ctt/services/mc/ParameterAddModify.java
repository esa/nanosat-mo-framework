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

import esa.mo.com.impl.util.HelperCOM;
import esa.mo.mc.impl.consumer.ParameterConsumerServiceImpl;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.ccsds.moims.mo.com.COMService;
import org.ccsds.moims.mo.com.archive.structures.ExpressionOperator;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.conversion.ConversionHelper;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterConversion;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterCreationRequest;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterCreationRequestList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.ConditionalConversion;
import org.ccsds.moims.mo.mc.structures.ConditionalConversionList;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePairList;
import org.ccsds.moims.mo.mc.structures.ParameterExpression;

/**
 *
 * @author Cesar Coelho
 */
public class ParameterAddModify extends javax.swing.JFrame {

    private ParameterConsumerServiceImpl serviceMCParameter;
    private DefaultTableModel parameterTableData;
    private Boolean isAddDef = false;
    private int parameterDefinitionSelectedIndex = 0;

    private static transient COMService service;

    // Conversion Service Object Types
    public static transient ObjectType OBJ_TYPE_CS_DISCRETECONVERSION;
    public static transient ObjectType OBJ_TYPE_CS_LINECONVERSION;
    public static transient ObjectType OBJ_TYPE_CS_POLYCONVERSION;
    public static transient ObjectType OBJ_TYPE_CS_RANGECONVERSION;

    /**
     * Creates new form ParameterAddModify
     *
     * @param parameterService
     */
    public ParameterAddModify(final ParameterConsumerServiceImpl parameterService,
        final DefaultTableModel parameterTableData) {
        initComponents();

        this.serviceMCParameter = parameterService;
        this.parameterTableData = parameterTableData;

        // Set window size for the Add and Modify Parameter Definition
        this.setSize(400, 590);
        this.setPreferredSize(new Dimension(400, 590));
        this.setResizable(false);
        this.setVisible(false);

        try {
            ConversionHelper.init(MALContextFactory.getElementFactoryRegistry());
        } catch (MALException ex) {
            // nothing to be done..
        }

        service = ConversionHelper.CONVERSION_SERVICE;

        // Conversion Service Object Types
        OBJ_TYPE_CS_DISCRETECONVERSION = HelperCOM.generateCOMObjectType(service, new UShort(1));
        OBJ_TYPE_CS_LINECONVERSION = HelperCOM.generateCOMObjectType(service, new UShort(2));
        OBJ_TYPE_CS_POLYCONVERSION = HelperCOM.generateCOMObjectType(service, new UShort(3));
        OBJ_TYPE_CS_RANGECONVERSION = HelperCOM.generateCOMObjectType(service, new UShort(4));

    }

    public void setParameterDefinitionSelectedIndex(int in) {
        this.parameterDefinitionSelectedIndex = in;
    }

    public ParameterDefinitionDetails makeNewParameterDefinition(int rawType, String rawUnit, String description,
        boolean generationEnabled, float interval, ParameterExpression validityExpression,
        ParameterConversion conversion) {
        ParameterDefinitionDetails PDef = new ParameterDefinitionDetails();
        PDef.setDescription(description);
        PDef.setRawType((byte) rawType);
        PDef.setRawUnit(rawUnit);
        PDef.setDescription(description);
        PDef.setGenerationEnabled(generationEnabled);  // shall not matter, because when we add it it will be false!
        PDef.setReportInterval(makeDuration(interval));
        PDef.setValidityExpression(validityExpression);
        PDef.setConversion(conversion);

        return PDef;
    }

    public ParameterExpression makeNewParameterExpression(Long instId, int operator, Boolean useConverted,
        String value) {
        ParameterExpression PExp = new ParameterExpression();

        PExp.setParameterId(new ObjectKey(serviceMCParameter.getConnectionDetails().getDomain(), instId));
        PExp.setOperator(ExpressionOperator.fromOrdinal(operator));
        PExp.setUseConverted(useConverted);
        PExp.setValue(new Union(value));

        return PExp;
    }

    public Duration makeDuration(float input) {
        //        Duration durationOne = new Duration(1);
        //        Object value = durationOne.getValue();

        //      return new Duration((int) Math.round(input));  // Then it is an int! (round the number before)
        return new Duration(input);
    }

    public void refreshParametersComboBox() {
        validity1.removeAllItems();
        for (int i = 0; i < parameterTableData.getRowCount(); i++) {
            validity1.addItem(parameterTableData.getValueAt(i, 1));
        }
    }

    public void setUpdateParameterForm(javax.swing.JTable parameterTable) {
        titleEditParameter.setText("Update Parameter Definition");
        nameTF.setText(parameterTable.getValueAt(parameterDefinitionSelectedIndex, 1).toString());
        descriptionTF.setText(parameterTable.getValueAt(parameterDefinitionSelectedIndex, 2).toString());
        rawTypeCB.setSelectedItem(parameterTable.getValueAt(parameterDefinitionSelectedIndex, 3).toString());
        rawUnitTF.setText(parameterTable.getValueAt(parameterDefinitionSelectedIndex, 4).toString());
        updateIntervalTF.setText(parameterTable.getValueAt(parameterDefinitionSelectedIndex, 6).toString());
        validityExpressionCB.setSelected(false);
        validityExpressionCBActionPerformed(null);

        String str = parameterTable.getValueAt(parameterTable.getSelectedRow(), 5).toString();
        boolean curState = (str.equals("true")); // String to Boolean conversion
        generationEnabledCB.setSelected(curState);
        generationEnabledCB.setEnabled(true);
        isAddDef = false;
    }

    public void setAddParameterForm() {
        titleEditParameter.setText("Add a new Parameter Definition");
        nameTF.setText("");
        descriptionTF.setText("");
        rawTypeCB.setSelectedIndex(5);  // Double
        rawUnitTF.setText("");
        updateIntervalTF.setText("");
        generationEnabledCB.setSelected(false);
        generationEnabledCB.setEnabled(false);
        validityExpressionCB.setSelected(false);
        validityExpressionCBActionPerformed(null);

        isAddDef = true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        titleEditParameter = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        nameTF = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        descriptionTF = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        rawTypeCB = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        rawUnitTF = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        updateIntervalTF = new javax.swing.JTextField();
        generationEnabledCB = new javax.swing.JCheckBox();
        jSeparator7 = new javax.swing.JSeparator();
        validityExpressionCB = new javax.swing.JCheckBox();
        jPanel10 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        validity1 = new javax.swing.JComboBox();
        validity2 = new javax.swing.JComboBox();
        validity3 = new javax.swing.JTextField();
        validity4 = new javax.swing.JCheckBox();
        jSeparator4 = new javax.swing.JSeparator();
        conversionCB = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        referenceObjIdTF = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        convertedUnit = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        objTypeCB = new javax.swing.JComboBox();
        jSeparator8 = new javax.swing.JSeparator();
        submitButton = new javax.swing.JButton();

        setName("editParameter"); // NOI18N

        jPanel3.setMaximumSize(null);
        jPanel3.setMinimumSize(new java.awt.Dimension(290, 600));
        jPanel3.setPreferredSize(new java.awt.Dimension(400, 600));

        jPanel4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel4.setMaximumSize(new java.awt.Dimension(280, 530));
        jPanel4.setMinimumSize(new java.awt.Dimension(280, 530));
        jPanel4.setPreferredSize(new java.awt.Dimension(280, 530));

        titleEditParameter.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        titleEditParameter.setText("Auto-change Label");
        jPanel4.add(titleEditParameter);

        jSeparator2.setPreferredSize(new java.awt.Dimension(250, 10));
        jPanel4.add(jSeparator2);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("name");
        jLabel2.setPreferredSize(new java.awt.Dimension(100, 14));
        jPanel4.add(jLabel2);

        nameTF.setText("name");
        nameTF.setPreferredSize(new java.awt.Dimension(150, 20));
        jPanel4.add(nameTF);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("description");
        jLabel3.setPreferredSize(new java.awt.Dimension(100, 14));
        jPanel4.add(jLabel3);

        descriptionTF.setText("description");
        descriptionTF.setPreferredSize(new java.awt.Dimension(150, 20));
        jPanel4.add(descriptionTF);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("rawType");
        jLabel4.setPreferredSize(new java.awt.Dimension(100, 14));
        jPanel4.add(jLabel4);

        rawTypeCB.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"-", "Blob", "Boolean", "Duration",
                                                                             "Float", "Double", "Identifier", "Octet",
                                                                             "UOctet", "Short", "UShort", "Integer",
                                                                             "UInteger", "Long", "ULong", "String",
                                                                             "Time", "FineTime", "URI", " "}));
        rawTypeCB.setMaximumSize(new java.awt.Dimension(150, 20));
        rawTypeCB.setMinimumSize(new java.awt.Dimension(150, 20));
        rawTypeCB.setPreferredSize(new java.awt.Dimension(150, 20));
        jPanel4.add(rawTypeCB);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("rawUnit");
        jLabel5.setPreferredSize(new java.awt.Dimension(100, 14));
        jPanel4.add(jLabel5);

        rawUnitTF.setText("2");
        rawUnitTF.setPreferredSize(new java.awt.Dimension(150, 20));
        jPanel4.add(rawUnitTF);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("updateInterval");
        jLabel7.setPreferredSize(new java.awt.Dimension(100, 14));
        jPanel4.add(jLabel7);

        updateIntervalTF.setText("3");
        updateIntervalTF.setPreferredSize(new java.awt.Dimension(150, 20));
        updateIntervalTF.addActionListener(this::updateIntervalTFActionPerformed);
        jPanel4.add(updateIntervalTF);

        generationEnabledCB.setText("generationEnabled");
        generationEnabledCB.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        generationEnabledCB.setOpaque(false);
        generationEnabledCB.addActionListener(this::generationEnabledCBActionPerformed);
        jPanel4.add(generationEnabledCB);

        jSeparator7.setMaximumSize(new java.awt.Dimension(250, 10));
        jSeparator7.setMinimumSize(new java.awt.Dimension(250, 10));
        jSeparator7.setPreferredSize(new java.awt.Dimension(250, 10));
        jPanel4.add(jSeparator7);

        validityExpressionCB.setText("validityExpression");
        validityExpressionCB.addActionListener(this::validityExpressionCBActionPerformed);
        jPanel4.add(validityExpressionCB);

        jPanel10.setMaximumSize(new java.awt.Dimension(280, 70));
        jPanel10.setMinimumSize(new java.awt.Dimension(280, 70));
        jPanel10.setPreferredSize(new java.awt.Dimension(280, 70));

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("parameter");
        jLabel8.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel8.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel8.setPreferredSize(new java.awt.Dimension(100, 14));
        jPanel10.add(jLabel8);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("operator");
        jLabel9.setPreferredSize(new java.awt.Dimension(90, 14));
        jPanel10.add(jLabel9);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("value");
        jLabel11.setPreferredSize(new java.awt.Dimension(60, 14));
        jPanel10.add(jLabel11);

        validity1.setEnabled(false);
        validity1.setMaximumSize(new java.awt.Dimension(100, 20));
        validity1.setMinimumSize(new java.awt.Dimension(100, 20));
        validity1.setPreferredSize(new java.awt.Dimension(100, 20));
        validity1.addActionListener(this::validity1ActionPerformed);
        jPanel10.add(validity1);

        validity2.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"=", "≠", ">", "≥", "<", "≤", "CONTAINS",
                                                                             "ICONTAINS"}));
        validity2.setEnabled(false);
        validity2.setMaximumSize(new java.awt.Dimension(90, 20));
        validity2.setMinimumSize(new java.awt.Dimension(90, 20));
        validity2.setPreferredSize(new java.awt.Dimension(90, 20));
        validity2.addActionListener(this::validity2ActionPerformed);
        jPanel10.add(validity2);

        validity3.setEnabled(false);
        validity3.setPreferredSize(new java.awt.Dimension(60, 20));
        validity3.addActionListener(this::validity3ActionPerformed);
        jPanel10.add(validity3);

        validity4.setText("useConverted");
        validity4.setEnabled(false);
        validity4.setMaximumSize(new java.awt.Dimension(180, 23));
        validity4.setMinimumSize(new java.awt.Dimension(180, 23));
        validity4.setPreferredSize(null);
        validity4.addActionListener(this::validity4ActionPerformed);
        jPanel10.add(validity4);

        jPanel4.add(jPanel10);

        jSeparator4.setEnabled(false);
        jSeparator4.setMaximumSize(new java.awt.Dimension(250, 10));
        jSeparator4.setMinimumSize(new java.awt.Dimension(250, 10));
        jSeparator4.setPreferredSize(new java.awt.Dimension(250, 10));
        jPanel4.add(jSeparator4);

        conversionCB.setText("conversion");
        conversionCB.addActionListener(this::conversionCBActionPerformed);
        jPanel4.add(conversionCB);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Reference objId:");
        jLabel6.setPreferredSize(new java.awt.Dimension(100, 14));

        referenceObjIdTF.setText("name");
        referenceObjIdTF.setPreferredSize(new java.awt.Dimension(150, 20));

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Converted unit:");
        jLabel10.setPreferredSize(new java.awt.Dimension(100, 14));

        convertedUnit.setText("description");
        convertedUnit.setPreferredSize(new java.awt.Dimension(150, 20));

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Reference obj Type:");
        jLabel12.setPreferredSize(new java.awt.Dimension(100, 14));

        objTypeCB.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"-", "Discrete Conversion",
                                                                             "Line Conversion", "Poly Conversion",
                                                                             "Range Conversion"}));
        objTypeCB.setMaximumSize(new java.awt.Dimension(150, 20));
        objTypeCB.setMinimumSize(new java.awt.Dimension(150, 20));
        objTypeCB.setPreferredSize(new java.awt.Dimension(150, 20));
        objTypeCB.addActionListener(this::objTypeCBActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup().addContainerGap(16, Short.MAX_VALUE).addGroup(jPanel1Layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                    javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addComponent(
                        jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                            javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(referenceObjIdTF,
                                javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)).addGroup(
                                    javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE,
                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                            javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                                    convertedUnit, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    javax.swing.GroupLayout.PREFERRED_SIZE)).addGroup(
                                                        javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout
                                                            .createSequentialGroup().addComponent(jLabel12,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                                                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(objTypeCB,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap()));
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.BASELINE).addComponent(referenceObjIdTF,
                    javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel6,
                        javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                            javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(jPanel1Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(objTypeCB,
                                    javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel12,
                                        javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                                            javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(jPanel1Layout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(convertedUnit, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel10,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap(24,
                                                            Short.MAX_VALUE)));

        jPanel4.add(jPanel1);

        jSeparator8.setMaximumSize(new java.awt.Dimension(250, 10));
        jSeparator8.setMinimumSize(new java.awt.Dimension(250, 10));
        jSeparator8.setPreferredSize(new java.awt.Dimension(250, 10));
        jPanel4.add(jSeparator8);

        submitButton.setText("Submit");
        submitButton.addActionListener(this::submitButtonActionPerformed);
        jPanel4.add(submitButton);

        jPanel3.add(jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
            javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 410,
                    javax.swing.GroupLayout.PREFERRED_SIZE)));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
            jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 545, Short.MAX_VALUE));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void updateIntervalTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateIntervalTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_updateIntervalTFActionPerformed

    private void generationEnabledCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generationEnabledCBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_generationEnabledCBActionPerformed

    private void validityExpressionCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validityExpressionCBActionPerformed

        validity1.setEnabled(validityExpressionCB.isSelected());
        validity2.setEnabled(validityExpressionCB.isSelected());
        validity3.setEnabled(validityExpressionCB.isSelected());
        validity4.setEnabled(validityExpressionCB.isSelected());
    }//GEN-LAST:event_validityExpressionCBActionPerformed

    private void validity1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validity1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_validity1ActionPerformed

    private void validity2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validity2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_validity2ActionPerformed

    private void validity3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validity3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_validity3ActionPerformed

    private void validity4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validity4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_validity4ActionPerformed

    private void conversionCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conversionCBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_conversionCBActionPerformed

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed

        if (nameTF.getText().equals("") || descriptionTF.getText().equals("") || descriptionTF.getText().equals("") ||
            rawTypeCB.getSelectedIndex() == 0 || rawUnitTF.getText().equals("") || updateIntervalTF.getText().equals(
                "")) {
            JOptionPane.showMessageDialog(null, "Please fill-in all the necessary fields!", "Warning!",
                JOptionPane.PLAIN_MESSAGE);
            return;
        }

        try {
            Double.parseDouble(updateIntervalTF.getText());  // Check if it is a number
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null, "updateInterval is not a number!", "Warning!",
                JOptionPane.PLAIN_MESSAGE);
            return;
        }

        ParameterExpression PExp;

        if (validityExpressionCB.isSelected()) {
            if (validity2.getSelectedIndex() != -1 && !validity3.getText().equals("")) {
                Long instId = Long.valueOf(parameterTableData.getValueAt(validity1.getSelectedIndex(), 0).toString());
                PExp = makeNewParameterExpression(instId, validity2.getSelectedIndex(), validity4.isSelected(),
                    validity3.getText());
            } else {
                JOptionPane.showMessageDialog(null, "Please select an operator and a value!", "Warning!",
                    JOptionPane.PLAIN_MESSAGE);
                return;
            }
        } else {
            PExp = null;
        }

        ParameterConversion pConv;

        if (conversionCB.isSelected()) {
            // Reference to the conversion Object
            ObjectId referenceId = new ObjectId();
            referenceId.setKey(new ObjectKey(serviceMCParameter.getConnectionDetails().getDomain(), Long.valueOf(
                referenceObjIdTF.getText())));  // Get the first objId

            int index = objTypeCB.getSelectedIndex();

            switch (index) {
                case 1:
                    referenceId.setType(OBJ_TYPE_CS_DISCRETECONVERSION);
                    break;
                case 2:
                    referenceId.setType(OBJ_TYPE_CS_LINECONVERSION);
                    break;
                case 3:
                    referenceId.setType(OBJ_TYPE_CS_POLYCONVERSION);
                    break;
                case 4:
                    referenceId.setType(OBJ_TYPE_CS_RANGECONVERSION);
                    break;
                default:
                    referenceId.setType(null);
                    break;
            }

            ConditionalConversionList conversionConditions = new ConditionalConversionList();
            ConditionalConversion conversionCondition = new ConditionalConversion();
            conversionCondition.setCondition(null);
            conversionCondition.setConversionId(referenceId.getKey());
            conversionConditions.add(conversionCondition);

            pConv = new ParameterConversion();
            pConv.setConvertedType((byte) rawTypeCB.getSelectedIndex());
            pConv.setConvertedUnit(convertedUnit.getText());
            pConv.setConditionalConversions(conversionConditions);

        } else {
            pConv = null;
        }

        ParameterDefinitionDetails Pdef;
        Pdef = makeNewParameterDefinition(rawTypeCB.getSelectedIndex(), rawUnitTF.getText(), descriptionTF.getText(),
            generationEnabledCB.isSelected(), Float.parseFloat(updateIntervalTF.getText()), PExp, pConv);

        ParameterDefinitionDetailsList PDefs = new ParameterDefinitionDetailsList();
        PDefs.add(Pdef);

        ParameterCreationRequest request = new ParameterCreationRequest();
        request.setName(new Identifier(nameTF.getText()));
        request.setParamDefDetails(Pdef);

        ParameterCreationRequestList requestList = new ParameterCreationRequestList();
        requestList.add(request);

        this.setVisible(false);

        if (isAddDef) {
            try {
                // Are we adding a new definition?
                Logger.getLogger(ParameterAddModify.class.getName()).info("addDefinition started");
                ObjectInstancePairList output = serviceMCParameter.getParameterStub().addParameter(requestList);
                Logger.getLogger(ParameterAddModify.class.getName()).log(Level.INFO,
                    "addDefinition returned {0} object instance identifiers", output.size());

                parameterTableData.addRow(new Object[]{output.get(0).getObjDefInstanceId().intValue(), request.getName()
                    .toString(), Pdef.getDescription(), rawTypeCB.getItemAt(Pdef.getRawType()).toString(), Pdef
                        .getRawUnit(), Pdef.getGenerationEnabled(), Pdef.getReportInterval().getValue()});
            } catch (MALInteractionException | MALException ex) {
                Logger.getLogger(ParameterAddModify.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                // Well, then we are updating a previous selected definition
                Logger.getLogger(ParameterAddModify.class.getName()).info("updateDefinition started");
                LongList objIds = new LongList();
                objIds.add(Long.valueOf(parameterTableData.getValueAt(parameterDefinitionSelectedIndex, 0).toString()));
                serviceMCParameter.getParameterStub().updateDefinition(objIds, PDefs);
                parameterTableData.removeRow(parameterDefinitionSelectedIndex);
                parameterTableData.insertRow(parameterDefinitionSelectedIndex, new Object[]{objIds.get(0).intValue(),
                                                                                            request.getName()
                                                                                                .toString(), Pdef
                                                                                                    .getDescription(),
                                                                                            rawTypeCB.getItemAt(Pdef
                                                                                                .getRawType())
                                                                                                .toString(), Pdef
                                                                                                    .getRawUnit(), Pdef
                                                                                                        .getGenerationEnabled(),
                                                                                            Pdef.getReportInterval()
                                                                                                .getValue()});
                Logger.getLogger(ParameterAddModify.class.getName()).info("updateDefinition executed");
            } catch (MALInteractionException | MALException ex) {
                Logger.getLogger(ParameterAddModify.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_submitButtonActionPerformed

    private void objTypeCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_objTypeCBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_objTypeCBActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox conversionCB;
    private javax.swing.JTextField convertedUnit;
    private javax.swing.JTextField descriptionTF;
    private javax.swing.JCheckBox generationEnabledCB;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JTextField nameTF;
    private javax.swing.JComboBox objTypeCB;
    private javax.swing.JComboBox rawTypeCB;
    private javax.swing.JTextField rawUnitTF;
    private javax.swing.JTextField referenceObjIdTF;
    private javax.swing.JButton submitButton;
    private javax.swing.JLabel titleEditParameter;
    private javax.swing.JTextField updateIntervalTF;
    private javax.swing.JComboBox validity1;
    private javax.swing.JComboBox validity2;
    private javax.swing.JTextField validity3;
    private javax.swing.JCheckBox validity4;
    private javax.swing.JCheckBox validityExpressionCB;
    // End of variables declaration//GEN-END:variables
}
