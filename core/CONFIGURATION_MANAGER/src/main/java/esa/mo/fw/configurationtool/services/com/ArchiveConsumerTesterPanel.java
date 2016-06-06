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
package esa.mo.fw.configurationtool.services.com;

import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.helpers.HelperMisc;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.archive.structures.CompositeFilterList;
import org.ccsds.moims.mo.com.archive.structures.CompositeFilterSet;
import org.ccsds.moims.mo.com.archive.structures.CompositeFilterSetList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetails;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetailsList;

/**
 *
 * @author Cesar Coelho
 */
public class ArchiveConsumerTesterPanel extends javax.swing.JPanel {

    private ArchiveConsumerAdapter adapter = new ArchiveConsumerAdapter();
    private ArchiveConsumerServiceImpl serviceCOMArchive;
    private DefaultTableModel archiveTableData;
    
    public final transient ObjectType OBJTYPE_AGGS_AGGREGATIONDEFINITION = HelperCOM.generateCOMObjectType(4, 6, 1, 1);

    /**
     * Creates new form ArchiveConsumerPanel
     *
     * @param archiveService
     */
    public ArchiveConsumerTesterPanel(ArchiveConsumerServiceImpl archiveService) {
        initComponents();
        this.serviceCOMArchive = archiveService;


    String[] archiveTableCol = new String[]{
            "Operation", "Interaction Pattern", "ObjType", 
            "domain", "# objects" };

        
        archiveTableData = new javax.swing.table.DefaultTableModel(
                new Object[][]{}, archiveTableCol) {
                    Class[] types = new Class[]{
                        java.lang.String.class, java.lang.String.class, java.lang.String.class, 
                        java.lang.String.class, java.lang.String.class
                    };

                    @Override               //all cells false
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }

                    @Override
                    public Class getColumnClass(int columnIndex) {
                        return types[columnIndex];
                    }
                };

        archiveTable.setModel(archiveTableData);
    }
    
    public ArchiveConsumerAdapter getArchiveAdapter(){
        return this.adapter;
    }

    public class ArchiveConsumerAdapter extends ArchiveAdapter {

        @Override
        public void retrieveAckReceived(MALMessageHeader msgHeader, Map qosProperties) {

            populateTable(msgHeader, null, null, null);
        }

        @Override
        public void retrieveResponseReceived(MALMessageHeader msgHeader, ArchiveDetailsList _ArchiveDetailsList0, ElementList _ElementList1, Map qosProperties) {

            populateTable(msgHeader, null, null, _ElementList1);

            if (_ElementList1 == null) {
                TBoxRetrieve.setText("");
                return;
            }

            if (_ElementList1.size() != 0) {
                if (_ElementList1.get(0) == null) {
                TBoxRetrieve.setText("");
                return;
                }
            }
            
            for (Object obj : _ElementList1) {
                if (obj == null) {
                    continue;
                }
                AggregationDefinitionDetails agg = (AggregationDefinitionDetails) obj;
                Identifier name = agg.getName();
                TBoxRetrieve.setText(name.toString());
            }
        }

        @Override
        public void countResponseReceived(MALMessageHeader msgHeader, LongList _LongList0, Map qosProperties) {
            TBoxCount.setText(_LongList0.get(0).toString());

            populateTable(msgHeader, null, null, _LongList0);
        }
        
        @Override
        public void queryResponseReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain, ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {

            populateTable(msgHeader, objType, domain, objBodies);

        }

        @Override
        public void queryUpdateReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain, ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {

            populateTable(msgHeader, objType, domain, objBodies);

        }
        
    }
    
    private void populateTable(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain, ElementList bodies){
        
            String op = MALContextFactory.lookupArea(msgHeader.getServiceArea(), msgHeader.getAreaVersion()).getServiceByNumber(msgHeader.getService()).getOperationByNumber(msgHeader.getOperation()).getName().toString();
            String ip = msgHeader.getInteractionType().toString() + ": " + msgHeader.getInteractionStage();
            String domain1 = "n/a";
            String objType1 = "n/a";
            String n_objs = "0";

            if (domain != null){
                domain1 = HelperMisc.domain2domainId(domain);
            }
            
            if (objType != null){
                objType1 = objType.toString();
            }

            if (bodies != null){
                n_objs = String.valueOf(bodies.size());
            }

            archiveTableData.addRow( new Object[]{
                op, ip, domain1, objType1, n_objs
                } );
            
        
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel6 = new javax.swing.JLabel();
        jButtonStore = new javax.swing.JButton();
        jButtonRetrieve = new javax.swing.JButton();
        TBoxRetrieve = new javax.swing.JTextField();
        jButtonQuery = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jButtonCount = new javax.swing.JButton();
        jButtonUpdate = new javax.swing.JButton();
        TBoxStore = new javax.swing.JTextField();
        TBoxCount = new javax.swing.JTextField();
        TBoxDelete = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        archiveTable = new javax.swing.JTable();

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Archive Tester");
        jLabel6.setToolTipText("");

        jButtonStore.setText("store()");
        jButtonStore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStoreActionPerformed(evt);
            }
        });

        jButtonRetrieve.setText("retrieve()");
        jButtonRetrieve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRetrieveActionPerformed(evt);
            }
        });

        TBoxRetrieve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TBoxRetrieveActionPerformed(evt);
            }
        });

        jButtonQuery.setText("query()");
        jButtonQuery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonQueryActionPerformed(evt);
            }
        });

        jButtonDelete.setText("delete()");
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });

        jButtonCount.setText("count()");
        jButtonCount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCountActionPerformed(evt);
            }
        });

        jButtonUpdate.setText("update()");
        jButtonUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdateActionPerformed(evt);
            }
        });

        TBoxStore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TBoxStoreActionPerformed(evt);
            }
        });

        TBoxDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TBoxDeleteActionPerformed(evt);
            }
        });

        jScrollPane3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane3.setHorizontalScrollBar(null);
        jScrollPane3.setPreferredSize(new java.awt.Dimension(796, 380));
        jScrollPane3.setRequestFocusEnabled(false);

        archiveTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "HDR - Timestamp", "HDR - uriTo", "eKey1 - Event ojb number", "eKey2 - Service", "eKey3 - ObjId", "eKey4 - Source ObjType", "Related (ObjDetails)", "Source (ObjDetails)", "N of event objs"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        archiveTable.setAlignmentX(0.0F);
        archiveTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        archiveTable.setAutoscrolls(false);
        archiveTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        archiveTable.setMaximumSize(null);
        archiveTable.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                archiveTableComponentAdded(evt);
            }
        });
        jScrollPane3.setViewportView(archiveTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 709, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonRetrieve)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TBoxRetrieve, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButtonQuery, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonCount, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TBoxCount, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonStore, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TBoxDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TBoxStore, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonRetrieve)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(TBoxRetrieve, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(30, 30, 30)
                        .addComponent(jButtonQuery)
                        .addGap(32, 32, 32)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonCount)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(TBoxCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(TBoxStore, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonStore))
                        .addGap(30, 30, 30)
                        .addComponent(jButtonUpdate)
                        .addGap(34, 34, 34)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonDelete)
                            .addComponent(TBoxDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonStoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStoreActionPerformed
        ArchiveDetailsList archiveDetailsList = new ArchiveDetailsList();
        archiveDetailsList.add(serviceCOMArchive.generateArchiveDetails(new Long(0)));
        ObjectType objType = this.OBJTYPE_AGGS_AGGREGATIONDEFINITION;
        AggregationDefinitionDetailsList objList = new AggregationDefinitionDetailsList();
        objList.add(ArchiveConsumerManagerPanel.generateAggregationDefinition("AggregationStore"));

        try {
          LongList outObjId = serviceCOMArchive.getArchiveStub().store(Boolean.TRUE, objType, serviceCOMArchive.getConnectionDetails().getDomain(), archiveDetailsList, objList);
          Long received = outObjId.get(0);
          TBoxStore.setText(received.toString());
        } catch (MALInteractionException | MALException ex) {
          Logger.getLogger(ArchiveConsumerTesterPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_jButtonStoreActionPerformed

    private void jButtonRetrieveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRetrieveActionPerformed
//        serviceCOMArchive.retrieveMethod(new Long(3));

        ObjectType objType = this.OBJTYPE_AGGS_AGGREGATIONDEFINITION;
        LongList objIds = new LongList();
        objIds.add(new Long(3));
            
        try {
            serviceCOMArchive.getArchiveStub().retrieve(objType, serviceCOMArchive.getConnectionDetails().getDomain(), objIds, adapter);
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerTesterPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_jButtonRetrieveActionPerformed


    private void jButtonQueryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonQueryActionPerformed

//        serviceCOMArchive.queryMethodEx1();

        
        ObjectType objType = this.OBJTYPE_AGGS_AGGREGATIONDEFINITION;
        ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        CompositeFilterSetList compositeFilterSetList = new CompositeFilterSetList();
        CompositeFilterSet compositeFilterSet = new CompositeFilterSet();
        CompositeFilterList compositeFilterList = new CompositeFilterList();

        archiveQueryList.add(ArchiveConsumerServiceImpl.generateArchiveQuery());
        compositeFilterList.add(ArchiveConsumerServiceImpl.generateCompositeFilter());
        compositeFilterSet.setFilters(compositeFilterList);
        compositeFilterSetList.add(compositeFilterSet);

        try {
            serviceCOMArchive.getArchiveStub().query(true, objType, archiveQueryList, compositeFilterSetList, adapter);
        } catch (MALException | MALInteractionException ex) {
            Logger.getLogger(ArchiveConsumerTesterPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }//GEN-LAST:event_jButtonQueryActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        
        ObjectType objType = this.OBJTYPE_AGGS_AGGREGATIONDEFINITION;
        LongList objIds = new LongList();
        objIds.add(Long.parseLong(TBoxDelete.getText()));
        try {
          serviceCOMArchive.getArchiveStub().delete(objType, serviceCOMArchive.getConnectionDetails().getDomain(), objIds);
        } catch (MALException | MALInteractionException ex) {
          Logger.getLogger(ArchiveConsumerTesterPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_jButtonDeleteActionPerformed

    private void jButtonCountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCountActionPerformed
//        serviceCOMArchive.countMethodEx1();
        
        ObjectType objType = this.OBJTYPE_AGGS_AGGREGATIONDEFINITION;
        ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        CompositeFilterSetList compositeFilterSetList = new CompositeFilterSetList();
        CompositeFilterSet compositeFilterSet = new CompositeFilterSet();
        CompositeFilterList compositeFilterList = new CompositeFilterList();

        archiveQueryList.add(ArchiveConsumerServiceImpl.generateArchiveQuery());
        compositeFilterList.add(ArchiveConsumerServiceImpl.generateCompositeFilter());
        compositeFilterSet.setFilters(compositeFilterList);
        compositeFilterSetList.add(compositeFilterSet);

        try {
            serviceCOMArchive.getArchiveStub().count(objType, archiveQueryList, compositeFilterSetList, adapter);
        } catch (MALException | MALInteractionException ex) {
            Logger.getLogger(ArchiveConsumerTesterPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }//GEN-LAST:event_jButtonCountActionPerformed

    private void jButtonUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateActionPerformed
//        serviceCOMArchive.updateMethod(new Long(3));
        
        
    }//GEN-LAST:event_jButtonUpdateActionPerformed

    private void TBoxStoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TBoxStoreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TBoxStoreActionPerformed

    private void TBoxDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TBoxDeleteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TBoxDeleteActionPerformed

    private void TBoxRetrieveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TBoxRetrieveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TBoxRetrieveActionPerformed

    private void archiveTableComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_archiveTableComponentAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_archiveTableComponentAdded

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField TBoxCount;
    private javax.swing.JTextField TBoxDelete;
    private javax.swing.JTextField TBoxRetrieve;
    private javax.swing.JTextField TBoxStore;
    private javax.swing.JTable archiveTable;
    private javax.swing.JButton jButtonCount;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonQuery;
    private javax.swing.JButton jButtonRetrieve;
    private javax.swing.JButton jButtonStore;
    private javax.swing.JButton jButtonUpdate;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane3;
    // End of variables declaration//GEN-END:variables
}
