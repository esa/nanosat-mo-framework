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

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mp.plandistribution.PlanDistributionHelper;
import org.ccsds.moims.mo.mp.plandistribution.consumer.PlanDistributionAdapter;
import org.ccsds.moims.mo.mp.structures.PlanFilter;
import org.ccsds.moims.mo.mp.structures.PlanInformationList;
import org.ccsds.moims.mo.mp.structures.PlanStatusList;
import org.ccsds.moims.mo.mp.structures.PlanVersionDetailsList;
import esa.mo.mp.impl.consumer.PlanDistributionConsumerServiceImpl;

/**
 * PlanDistributionConsumerPanel
 */
public class PlanDistributionConsumerPanel extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(PlanDistributionConsumerPanel.class.getName());

    private final PlanDistributionConsumerServiceImpl planDistributionService;
    private final PlanVersionTablePanel planVersionTable;

    public PlanDistributionConsumerPanel(PlanDistributionConsumerServiceImpl planDistributionService) {
        initComponents();

        this.planDistributionService = planDistributionService;

        this.planVersionTable = new PlanVersionTablePanel(planDistributionService.getCOMServices().getArchiveService());

        jScrollPane2.setViewportView(this.planVersionTable);
    }

    public void init() {
        this.listPlansButtonActionPerformed(null);
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
        listPlansButton = new javax.swing.JButton();
        getPlanButton = new javax.swing.JButton();
        getPlanStatusButton = new javax.swing.JButton();
        queryPlanButton = new javax.swing.JButton();

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Plan Distribution Service");
        jLabel6.setToolTipText("");

        jScrollPane2.setHorizontalScrollBar(null);
        jScrollPane2.setPreferredSize(new java.awt.Dimension(796, 380));
        jScrollPane2.setRequestFocusEnabled(false);

        actionDefinitionsTable.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{{}, {}}, new String[]{

        }));
        actionDefinitionsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        actionDefinitionsTable.setAutoscrolls(false);
        actionDefinitionsTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane2.setViewportView(actionDefinitionsTable);

        parameterTab.setLayout(new java.awt.GridLayout(1, 1));

        listPlansButton.setText("listPlans");
        listPlansButton.addActionListener(this::listPlansButtonActionPerformed);
        jPanel5.add(listPlansButton);

        getPlanButton.setText("getPlan(\"*\")");
        getPlanButton.addActionListener(this::getPlanButtonActionPerformed);
        jPanel5.add(getPlanButton);

        getPlanStatusButton.setText("getPlanStatus(\"*\")");
        getPlanStatusButton.addActionListener(this::getPlanStatusButtonActionPerformed);
        jPanel5.add(getPlanStatusButton);

        queryPlanButton.setText("queryPlan");
        queryPlanButton.addActionListener(this::queryPlanButtonActionPerformed);
        jPanel5.add(queryPlanButton);

        parameterTab.add(jPanel5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
            parameterTab, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE).addComponent(jScrollPane2,
                javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 882, Short.MAX_VALUE)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
            .createSequentialGroup().addContainerGap().addComponent(jLabel6).addGap(18, 18, 18).addComponent(
                jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(parameterTab,
                        javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)));
    }// </editor-fold>//GEN-END:initComponents

    private void listPlansButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listPlansButtonActionPerformed
        PlanFilter filter = new PlanFilter();
        filter.setReturnAll(true);

        try {
            this.planDistributionService.getPlanDistributionStub().asyncListPlans(filter,
                new PlanDistributionAdapter() {
                    @Override
                    public void listPlansResponseReceived(MALMessageHeader msgHeader, LongList planIdentityIds,
                        LongList planVersionIds, PlanInformationList planInformation, PlanStatusList status,
                        Map qosProperties) {
                        org.ccsds.moims.mo.mc.structures.ObjectInstancePairList ids = new org.ccsds.moims.mo.mc.structures.ObjectInstancePairList();
                        for (int index = 0; index < planIdentityIds.size(); index++) {
                            ids.add(new org.ccsds.moims.mo.mc.structures.ObjectInstancePair(planIdentityIds.get(index),
                                planVersionIds.get(index)));
                        }
                        planVersionTable.refreshTableWithIds(ids, planDistributionService.getConnectionDetails()
                            .getDomain(), PlanDistributionHelper.PLANVERSION_OBJECT_TYPE);

                        jScrollPane2.setViewportView(planVersionTable);
                        LOGGER.log(Level.INFO, "listPlans returned {0} object instance identifiers", planIdentityIds
                            .size());
                    }

                    @Override
                    public void listPlansErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
                        Map qosProperties) {
                        JOptionPane.showMessageDialog(null, "There was an error during the listPlans operation.\n" +
                            error.toString(), "Error", JOptionPane.PLAIN_MESSAGE);
                        LOGGER.log(Level.SEVERE, null, error);
                    }
                });
        } catch (MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_listPlansButtonActionPerformed

    private void getPlanButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getPlanButtonActionPerformed
        LongList identityIds = new LongList();
        identityIds.add(0L);
        try {
            this.planDistributionService.getPlanDistributionStub().asyncGetPlan(identityIds,
                new PlanDistributionAdapter() {
                    @Override
                    public void getPlanResponseReceived(MALMessageHeader msgHeader, LongList planVersionId,
                        PlanVersionDetailsList planVersion, Map qosProperties) {
                        // TODO
                        JOptionPane.showMessageDialog(null, "Operation getPlan is not currently implemented", "Info",
                            JOptionPane.PLAIN_MESSAGE);
                        LOGGER.log(Level.INFO, "getPlan(\"*\") returned {0} object instance identifiers", planVersionId
                            .size());
                    }

                    @Override
                    public void getPlanErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
                        Map qosProperties) {
                        JOptionPane.showMessageDialog(null, "There was an error during the getPlan operation.\n" + error
                            .toString(), "Error", JOptionPane.PLAIN_MESSAGE);
                        LOGGER.log(Level.SEVERE, null, error);
                    }
                });
        } catch (MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_getPlanButtonActionPerformed

    private void getPlanStatusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getPlanStatusButtonActionPerformed
        LongList identityIds = new LongList();
        identityIds.add(0L);
        try {
            this.planDistributionService.getPlanDistributionStub().asyncGetPlanStatus(identityIds,
                new PlanDistributionAdapter() {
                    @Override
                    public void getPlanStatusResponseReceived(MALMessageHeader msgHeader, LongList planIdentityId,
                        PlanStatusList planStatus, Map qosProperties) {
                        // TODO
                        JOptionPane.showMessageDialog(null, "Operation getPlanStatus is not currently implemented",
                            "Info", JOptionPane.PLAIN_MESSAGE);
                        LOGGER.log(Level.INFO, "getPlanStatus(\"*\") returned {0} object instance identifiers",
                            planIdentityId.size());
                    }

                    @Override
                    public void getPlanStatusErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
                        Map qosProperties) {
                        JOptionPane.showMessageDialog(null, "There was an error during the getPlanStatus operation.\n" +
                            error.toString(), "Error", JOptionPane.PLAIN_MESSAGE);
                        LOGGER.log(Level.SEVERE, null, error);
                    }
                });
        } catch (MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_getPlanStatusButtonActionPerformed

    private void queryPlanButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_queryPlanButtonActionPerformed
        PlanFilter filter = new PlanFilter();
        filter.setReturnAll(true);
        try {
            this.planDistributionService.getPlanDistributionStub().asyncQueryPlan(filter,
                new PlanDistributionAdapter() {
                    @Override
                    public void queryPlanResponseReceived(MALMessageHeader msgHeader, LongList planVersionId,
                        PlanVersionDetailsList planVersion, Map qosProperties) {
                        // TODO
                        JOptionPane.showMessageDialog(null, "Operation queryPlan is not currently implemented", "Info",
                            JOptionPane.PLAIN_MESSAGE);
                        LOGGER.log(Level.INFO, "queryPlan returned {0} object instance identifiers", planVersionId
                            .size());
                    }

                    @Override
                    public void queryPlanResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
                        Map qosProperties) {
                        JOptionPane.showMessageDialog(null, "There was an error during the queryPlan operation.\n" +
                            error.toString(), "Error", JOptionPane.PLAIN_MESSAGE);
                        LOGGER.log(Level.SEVERE, null, error);
                    }
                });
        } catch (MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_queryPlanButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable actionDefinitionsTable;
    private javax.swing.JButton getPlanButton;
    private javax.swing.JButton getPlanStatusButton;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton listPlansButton;
    private javax.swing.JPanel parameterTab;
    private javax.swing.JButton queryPlanButton;
    // End of variables declaration//GEN-END:variables
}
