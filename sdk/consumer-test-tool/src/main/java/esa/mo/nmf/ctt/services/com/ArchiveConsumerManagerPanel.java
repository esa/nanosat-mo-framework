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
package esa.mo.nmf.ctt.services.com;

import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.ArchiveCOMObjectsOutput;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import esa.mo.nmf.ctt.windows.element.MOWindow;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.InterruptedIOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.archive.structures.CompositeFilter;
import org.ccsds.moims.mo.com.archive.structures.CompositeFilterList;
import org.ccsds.moims.mo.com.archive.structures.CompositeFilterSet;
import org.ccsds.moims.mo.com.archive.structures.ExpressionOperator;
import org.ccsds.moims.mo.com.archive.structures.PaginationFilter;
import org.ccsds.moims.mo.com.archive.structures.QueryFilterList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.HeterogeneousList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Pair;
import org.ccsds.moims.mo.mal.structures.PairList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.aggregation.AggregationServiceInfo;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinition;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSet;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSetList;
import org.ccsds.moims.mo.mc.conversion.ConversionServiceInfo;
import org.ccsds.moims.mo.mc.conversion.structures.DiscreteConversionDetails;
import org.ccsds.moims.mo.mc.conversion.structures.LineConversionDetails;
import org.ccsds.moims.mo.mc.conversion.structures.PolyConversionDetails;
import org.ccsds.moims.mo.mc.conversion.structures.RangeConversionDetails;
import org.ccsds.moims.mo.mc.group.structures.GroupDetails;

/**
 * The ArchiveConsumerManagerPanel class holds a panel to interact with an
 * Archive service.
 *
 * @author Cesar Coelho
 */
public class ArchiveConsumerManagerPanel extends javax.swing.JPanel {

    private final ArchiveConsumerServiceImpl serviceCOMArchive;
    private int location;
    private JTabbedPane serviceTabs = null;
    private GroundMOAdapterImpl services;

    /**
     * Constructor.
     *
     * @param archiveService The Archive service.
     */
    public ArchiveConsumerManagerPanel(ArchiveConsumerServiceImpl archiveService) {
        initComponents();
        serviceCOMArchive = archiveService;
    }

    public void setArchiveSyncConfigs(int count, JTabbedPane serviceTabs, GroundMOAdapterImpl services) {
        this.location = count;
        this.serviceTabs = serviceTabs;
        this.services = services;
    }

    public static AggregationDefinition generateAggregationDefinition(String name) {
        LongList objIdParams = new LongList();
        objIdParams.add(1L);

        AggregationParameterSetList aggParamSetList = new AggregationParameterSetList();
        aggParamSetList.add(new AggregationParameterSet(objIdParams, new Duration(1)));

        // AgregationDefinition
        return new AggregationDefinition(
                "This is a description",
                new UOctet((short) 0),
                new Duration(0),
                Boolean.FALSE,
                Boolean.FALSE,
                Boolean.FALSE,
                new Duration(0),
                Boolean.FALSE,
                aggParamSetList);
    }

    private LineConversionDetails generateLineConversionDetails() {
        PairList points = new PairList();
        points.add(new Pair(new Union(1), new Union(33.8)));
        points.add(new Pair(new Union(100), new Union(212)));
        return new LineConversionDetails(true, points);
    }

    private PolyConversionDetails generatePolyConversionDetails() {
        PairList points = new PairList();
        points.add(new Pair(new Union(0), new Union(32)));
        points.add(new Pair(new Union(1), new Union(1.8)));
        return new PolyConversionDetails(points);
    }

    private DiscreteConversionDetails generateDiscreteConversionDetails() {
        PairList mapping = new PairList();

        mapping.add(new Pair(new Union(0), new Union("Mode 0")));
        mapping.add(new Pair(new Union(1), new Union("Mode 1")));
        mapping.add(new Pair(new Union(2), new Union("Mode 2")));
        mapping.add(new Pair(new Union(3), new Union("Mode 3")));

        return new DiscreteConversionDetails(mapping);
    }

    private RangeConversionDetails generateRangeConversionDetails() {
        PairList points = new PairList();
        points.add(new Pair(new Union(0), new Union("Between 0-100")));
        points.add(new Pair(new Union(100), new Union("Between 100-inf")));
        return new RangeConversionDetails(points);
    }

    protected class ArchiveConsumerAdapter extends ArchiveAdapter {

        private final ArchiveTablePanel archiveTablePanel = new ArchiveTablePanel(null, serviceCOMArchive);
        private final Semaphore isOver = new Semaphore(0);
        private final javax.swing.JPanel pnlTab = new javax.swing.JPanel();
        private final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
        private final Date date = new Date(System.currentTimeMillis());

        private final String functionName;
        private int n_objs_counter = 0;
        private ObjectType objType;
        private IdentifierList domain;

        ArchiveConsumerAdapter(String stringLabel) {
            pnlTab.setOpaque(false);
            functionName = stringLabel;
            this.refreshTabCounter();
            tabs.addTab("", archiveTablePanel);
            tabs.setTabComponentAt(tabs.getTabCount() - 1, pnlTab);
            tabs.setSelectedIndex(tabs.getTabCount() - 1);
        }

        private void refreshTabCounter() {
            JLabel label = new JLabel(functionName
                    + " (" + dateFormat.format(date) + ")"
                    + " (" + n_objs_counter + ")");
            JLabel closeLabel = new JLabel("x");
            closeLabel.addMouseListener(new CloseMouseHandler(this));
            closeLabel.setFont(closeLabel.getFont().deriveFont(closeLabel.getFont().getStyle() | Font.BOLD));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1;

            gbc.gridx++;
            gbc.weightx = 0;

            pnlTab.removeAll();
            pnlTab.add(label, gbc);
            pnlTab.add(closeLabel, gbc);

            int index = tabs.indexOfTabComponent(archiveTablePanel);

            if (index == -1) {
                return;
            }

            tabs.setTabComponentAt(index, pnlTab);
        }

        public synchronized void finalizeAdapter() {
            try {
                this.finalize();
            } catch (Throwable ex) {
                Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public synchronized int getSelectedIndex() {
            return archiveTablePanel.getSelectedRow();
        }

        protected void setObjType(ObjectType objType) {
            this.objType = objType;
        }

        protected void setDomain(IdentifierList domain) {
            this.domain = domain;
        }

        @Override
        public void retrieveAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
            // Later on, do something...
        }

        @Override
        public synchronized void retrieveResponseReceived(MALMessageHeader msgHeader,
                ArchiveDetailsList objDetails, HeterogeneousList objBodies, Map qosProperties) {
            ArchiveCOMObjectsOutput archiveObjectOutput = new ArchiveCOMObjectsOutput(domain, objType, objDetails, objBodies);
            archiveTablePanel.addEntries(archiveObjectOutput);
            n_objs_counter = n_objs_counter + objDetails.size();
            refreshTabCounter();
        }

        @Override
        public synchronized void countResponseReceived(MALMessageHeader msgHeader,
                LongList _LongList0, Map qosProperties) {
            JOptionPane.showMessageDialog(null, _LongList0.toString(),
                    "The count operation returned the following data!", JOptionPane.PLAIN_MESSAGE);
        }

        @Override
        public synchronized void queryResponseReceived(MALMessageHeader msgHeader,
                ObjectType objType, IdentifierList domain, ArchiveDetailsList objDetails,
                HeterogeneousList objBodies, Map qosProperties) {
            if (objType == null || domain == null || objDetails == null) {
                refreshTabCounter();
                isOver.release();
                return;
            }
            ArchiveCOMObjectsOutput archiveObjectOutput = new ArchiveCOMObjectsOutput(
                    domain, objType, objDetails, objBodies);
            archiveTablePanel.addEntries(archiveObjectOutput);
            n_objs_counter = n_objs_counter + objDetails.size();
            refreshTabCounter();

            isOver.release();
        }

        @Override
        public synchronized void queryUpdateReceived(MALMessageHeader msgHeader,
                ObjectType objType, IdentifierList domain, ArchiveDetailsList objDetails,
                HeterogeneousList objBodies, Map qosProperties) {
            // If object type is null all objects have the same type
            if (objType == null) {
                objType = this.objType;
            }
            if (objType == null || domain == null || objDetails == null) {
                refreshTabCounter();
                repaint();
                return;
            }
            ArchiveCOMObjectsOutput archiveObjectOutput = new ArchiveCOMObjectsOutput(
                    domain, objType, objDetails, objBodies);
            archiveTablePanel.addEntries(archiveObjectOutput);
            n_objs_counter = n_objs_counter + objDetails.size();
            refreshTabCounter();
            repaint();
        }

        @Override
        public synchronized void queryAckErrorReceived(MALMessageHeader msgHeader,
                MOErrorException error, Map qosProperties) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(
                    Level.SEVERE, "queryAckErrorReceived", error);
        }

        protected void deleteAllInTable() {
            try {
                isOver.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(
                        Level.SEVERE, null, ex);
            }

            List<ArchivePersistenceObject> comObjects = archiveTablePanel.getAllCOMObjects();

            for (ArchivePersistenceObject comObject : comObjects) {
                LongList objIds = new LongList();
                objIds.add(comObject.getArchiveDetails().getInstId());
                try {
                    serviceCOMArchive.getArchiveStub().delete(comObject.getObjectType(), comObject.getDomain(), objIds);
                } catch (MALInteractionException | MALException ex) {
                    Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(
                            Level.SEVERE, null, ex);
                }
            }
        }

        private JPanel getPanel() {
            return pnlTab;
        }

    }

    public class CloseMouseHandler implements MouseListener {

        private final ArchiveConsumerAdapter adapter;

        CloseMouseHandler(ArchiveConsumerAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public void mouseClicked(MouseEvent evt) {
            for (int i = 0; i < tabs.getTabCount(); i++) {
                Component component = tabs.getTabComponentAt(i);
                JPanel panel = adapter.getPanel();

                if (component == panel) {
                    tabs.remove(i);
                    adapter.finalizeAdapter();

                    try {
                        super.finalize();
                    } catch (Throwable ex) {
                        Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(
                                Level.SEVERE, null, ex);
                    }
                    return;
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent me) {
        }

        @Override
        public void mouseReleased(MouseEvent me) {
        }

        @Override
        public void mouseEntered(MouseEvent me) {
        }

        @Override
        public void mouseExited(MouseEvent me) {
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

        jLabel6 = new javax.swing.JLabel();
        jButtonStoreAggregation = new javax.swing.JButton();
        jButtonGetAll = new javax.swing.JButton();
        jButtonQuery = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jButtonRetrieve = new javax.swing.JButton();
        jButtonUpdate = new javax.swing.JButton();
        jButtonCount = new javax.swing.JButton();
        tabs = new javax.swing.JTabbedPane();
        homeTab = new javax.swing.JPanel();
        jButtonStoreConversions = new javax.swing.JButton();
        jButtonStoreActions = new javax.swing.JButton();
        jButtonStoreGroups = new javax.swing.JButton();
        TBoxStore = new javax.swing.JTextField();
        jButtonDeleteAll = new javax.swing.JButton();
        open_ArchiveSync = new javax.swing.JButton();

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("COM Archive Manager");
        jLabel6.setToolTipText("");

        jButtonStoreAggregation.setText("Quick Store (Aggregation)");
        jButtonStoreAggregation.addActionListener(this::jButtonStoreAggregationActionPerformed);

        jButtonGetAll.setText("Get All");
        jButtonGetAll.addActionListener(this::jButtonGetAllActionPerformed);

        jButtonQuery.setText("Execute Query");
        jButtonQuery.addActionListener(this::jButtonQueryActionPerformed);

        jButtonDelete.setText("Delete Object");
        jButtonDelete.addActionListener(this::jButtonDeleteActionPerformed);

        jButtonRetrieve.setText("Retrieve Object");
        jButtonRetrieve.addActionListener(this::jButtonRetrieveActionPerformed);

        jButtonUpdate.setText("Update Object");
        jButtonUpdate.addActionListener(this::jButtonUpdateActionPerformed);

        jButtonCount.setText("Count Objects");
        jButtonCount.addActionListener(this::jButtonCountActionPerformed);

        tabs.setToolTipText("");
        tabs.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tabs.setMaximumSize(new java.awt.Dimension(800, 600));
        tabs.setMinimumSize(new java.awt.Dimension(800, 300));
        tabs.setPreferredSize(new java.awt.Dimension(800, 300));
        tabs.setRequestFocusEnabled(false);
        tabs.addTab("Home", homeTab);

        jButtonStoreConversions.setText("Quick Store (Conversions)");
        jButtonStoreConversions.addActionListener(this::jButtonStoreConversionsActionPerformed);

        jButtonStoreActions.setText("Query with Pagination");
        jButtonStoreActions.addActionListener(this::jButtonStoreActionsActionPerformed);

        jButtonStoreGroups.setText("Quick Store (Groups)");
        jButtonStoreGroups.addActionListener(this::jButtonStoreGroupsActionPerformed);

        TBoxStore.addActionListener(this::TBoxStoreActionPerformed);

        jButtonDeleteAll.setText("Delete All");
        jButtonDeleteAll.setEnabled(false);
        jButtonDeleteAll.addActionListener(this::jButtonDeleteAllActionPerformed);

        open_ArchiveSync.setText("Open ArchiveSync Tab");
        open_ArchiveSync.addActionListener(this::open_ArchiveSyncActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
            jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE,
            javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(layout.createSequentialGroup().addGap(48,
                48, 48).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonGetAll, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(jButtonRetrieve))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout
                    .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jButtonDeleteAll,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonUpdate, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addPreferredGap(
                            javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING).addComponent(jButtonDelete,
                                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                    Short.MAX_VALUE).addComponent(jButtonCount, javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addPreferredGap(
                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(jButtonQuery, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(
                                                        jButtonStoreActions, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        149, Short.MAX_VALUE)).addPreferredGap(
                                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(
                    jButtonStoreConversions, javax.swing.GroupLayout.PREFERRED_SIZE, 178, Short.MAX_VALUE).addComponent(
                        jButtonStoreGroups, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                                open_ArchiveSync, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(
                                    jButtonStoreAggregation, javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(TBoxStore,
                                            javax.swing.GroupLayout.PREFERRED_SIZE, 28,
                                            javax.swing.GroupLayout.PREFERRED_SIZE).addGap(48, 48, 48)).addGroup(layout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(tabs, javax.swing.GroupLayout.Alignment.TRAILING,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE, 957, Short.MAX_VALUE)));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
            .createSequentialGroup().addGap(18, 18, 18).addComponent(jLabel6).addGap(20, 20, 20).addGroup(layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jButtonRetrieve)
                .addComponent(jButtonStoreAggregation).addComponent(jButtonUpdate).addComponent(jButtonStoreGroups)
                .addComponent(TBoxStore, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jButtonQuery).addComponent(jButtonCount))
            .addGap(18, 18, 18).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jButtonGetAll).addComponent(jButtonDeleteAll).addComponent(jButtonDelete).addComponent(
                    jButtonStoreActions).addComponent(jButtonStoreConversions).addComponent(open_ArchiveSync))
            .addContainerGap(438, Short.MAX_VALUE)).addGroup(layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout
                    .createSequentialGroup().addGap(129, 129, 129).addComponent(tabs,
                        javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE))));
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonStoreAggregationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStoreAggregationActionPerformed
        ArchiveDetailsList archiveDetailsList = HelperArchive.generateArchiveDetailsList(
                null, null, serviceCOMArchive.getConnectionDetails().getProviderURI());

        HeterogeneousList objList = new HeterogeneousList();
        objList.add(ArchiveConsumerManagerPanel.generateAggregationDefinition("AggregationStore"));

        try {
            LongList outObjId = serviceCOMArchive.getArchiveStub().store(Boolean.TRUE,
                    AggregationServiceInfo.AGGREGATIONDEFINITION_OBJECT_TYPE,
                    serviceCOMArchive.getConnectionDetails().getDomain(),
                    archiveDetailsList, objList);
            Long received = outObjId.get(0);
            TBoxStore.setText(received.toString());
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonStoreAggregationActionPerformed

    private void jButtonGetAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGetAllActionPerformed
        ArchiveConsumerAdapter adapter = new ArchiveConsumerAdapter("Get All");

        UShort shorty = new UShort(0);
        UOctet octety = new UOctet((short) 0);
        ObjectType objType = new ObjectType(shorty, shorty, octety, shorty);

        ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        ArchiveQuery archiveQuery = new ArchiveQuery(
                null,
                null,
                null,
                new Long(0),
                null,
                null,
                null,
                null,
                null);

        archiveQueryList.add(archiveQuery);

        try {
            serviceCOMArchive.getArchiveStub().query(Boolean.TRUE, objType,
                    archiveQueryList, null, adapter);
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonGetAllActionPerformed

    private void jButtonQueryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonQueryActionPerformed
        ArchiveConsumerAdapter adapter = new ArchiveConsumerAdapter("Query...");

        // Object Type
        ObjectType objType = AggregationServiceInfo.AGGREGATIONDEFINITION_OBJECT_TYPE;
        MOWindow genObjType = new MOWindow(objType, true);
        try {
            objType = (ObjectType) genObjType.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }

        // Archive Query
        ArchiveQuery archiveQuery = ArchiveConsumerManagerPanel.generateArchiveQuery();
        ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        archiveQueryList.add(archiveQuery);
        MOWindow genArchiveQueryList = new MOWindow(archiveQueryList, true);
        try {
            archiveQueryList = (ArchiveQueryList) genArchiveQueryList.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }

        // Composite Filter
        QueryFilterList compositeFilters = new QueryFilterList();
        CompositeFilterList compositeFilterList = new CompositeFilterList();
        compositeFilterList.add(ArchiveConsumerManagerPanel.generateCompositeFilter());
        CompositeFilterSet compositeFilterSet = new CompositeFilterSet(compositeFilterList);
        compositeFilters.add(compositeFilterSet);
        MOWindow genFilter = new MOWindow(compositeFilters, true);
        try {
            compositeFilters = (QueryFilterList) genFilter.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }

        try {
            adapter.setObjType(objType);
            serviceCOMArchive.getArchiveStub().query(Boolean.TRUE, objType,
                    archiveQueryList, compositeFilters, adapter);
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButtonQueryActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed

        ArchivePersistenceObject comObject = ((ArchiveTablePanel) tabs.getSelectedComponent()).getSelectedCOMObject();

        LongList objIds = new LongList();
        objIds.add(comObject.getObjectId());

        try {
            serviceCOMArchive.getArchiveStub().delete(comObject.getObjectType(),
                    comObject.getDomain(), objIds);
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        ((ArchiveTablePanel) tabs.getSelectedComponent()).removeSelectedEntry();

    }//GEN-LAST:event_jButtonDeleteActionPerformed

    private void jButtonRetrieveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRetrieveActionPerformed

        // Object Type
        ObjectType objType = AggregationServiceInfo.AGGREGATIONDEFINITION_OBJECT_TYPE;
        MOWindow genObjType = new MOWindow(objType, true);
        try {
            objType = (ObjectType) genObjType.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }

        // Object Instance Identifier
        Long objId = (long) 0;
        LongList objIds = new LongList();
        objIds.add(objId);
        MOWindow genObjId = new MOWindow(objIds, true);
        try {
            objIds = (LongList) genObjId.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }

        IdentifierList domain = serviceCOMArchive.getConnectionDetails().getDomain();

        ArchiveConsumerAdapter adapter = new ArchiveConsumerAdapter("Retrieve...");
        adapter.setDomain(domain);
        adapter.setObjType(objType);

        try {
            serviceCOMArchive.getArchiveStub().retrieve(objType, domain, objIds, adapter);
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButtonRetrieveActionPerformed

    @SuppressWarnings("unchecked")
    private void jButtonUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateActionPerformed

        ArchivePersistenceObject comObject = ((ArchiveTablePanel) tabs.getSelectedComponent()).getSelectedCOMObject();
        MOWindow objBodyWindow = new MOWindow(comObject.getObject(), true);
        ArchiveDetailsList archiveDetailsList = new ArchiveDetailsList();
        archiveDetailsList.add(comObject.getArchiveDetails());
        HeterogeneousList finalObject = new HeterogeneousList();

        try {
            finalObject.add((Element) objBodyWindow.getObject());

            try {
                serviceCOMArchive.getArchiveStub().update(comObject.getObjectType(),
                        comObject.getDomain(), archiveDetailsList, finalObject);
            } catch (MALInteractionException | MALException ex) {
                Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(
                        Level.SEVERE, null, ex);
            }

        } catch (Exception ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButtonUpdateActionPerformed

    private void jButtonCountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCountActionPerformed

        // Object Type
        ObjectType objType = AggregationServiceInfo.AGGREGATIONDEFINITION_OBJECT_TYPE;
        MOWindow genObjType = new MOWindow(objType, true);
        try {
            objType = (ObjectType) genObjType.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }

        // Archive Query
        ArchiveQuery archiveQuery = ArchiveConsumerManagerPanel.generateArchiveQuery();
        ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        archiveQueryList.add(archiveQuery);
        MOWindow genArchiveQueryList = new MOWindow(archiveQueryList, true);
        try {
            archiveQueryList = (ArchiveQueryList) genArchiveQueryList.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }

        // Composite Filter
        QueryFilterList compositeFilters = new QueryFilterList();
        CompositeFilterList compositeFilterList = new CompositeFilterList();
        compositeFilterList.add(ArchiveConsumerManagerPanel.generateCompositeFilter());
        CompositeFilterSet compositeFilterSet = new CompositeFilterSet(compositeFilterList);
        compositeFilters.add(compositeFilterSet);
        MOWindow genFilter = new MOWindow(compositeFilters, true);
        try {
            compositeFilters = (QueryFilterList) genFilter.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }

        ArchiveConsumerAdapter adapter = new ArchiveConsumerAdapter("Count...");
        adapter.setObjType(objType);

        try {
            serviceCOMArchive.getArchiveStub().count(objType, archiveQueryList, compositeFilters, adapter);
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        JDialog frame = new JDialog();
        int n = JOptionPane.showConfirmDialog(frame,
                "Would you like to automatically query and get the objects?",
                "Query?", JOptionPane.YES_NO_OPTION);

        if (n == JOptionPane.YES_OPTION) {
            try {
                serviceCOMArchive.getArchiveStub().query(true, objType, archiveQueryList, compositeFilters, adapter);
            } catch (MALInteractionException | MALException ex) {
                Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_jButtonCountActionPerformed

    private void jButtonStoreConversionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStoreConversionsActionPerformed
        LongList outObjId;
        //        ArchiveDetailsList archiveDetailsList;
        ObjectType objType;

//        archiveDetailsList = new ArchiveDetailsList();
//        archiveDetailsList.add(serviceCOMArchive.generateArchiveDetails(new Long(0)));
        ArchiveDetailsList archiveDetailsList = HelperArchive.generateArchiveDetailsList(null, null, serviceCOMArchive.getConnectionDetails());
        objType = ConversionServiceInfo.DISCRETECONVERSION_OBJECT_TYPE;
        HeterogeneousList objList1 = new HeterogeneousList();
        objList1.add(this.generateDiscreteConversionDetails());

        try {
            outObjId = serviceCOMArchive.getArchiveStub().store(Boolean.TRUE, objType,
                    serviceCOMArchive.getConnectionDetails().getDomain(), archiveDetailsList, objList1);
        } catch (MALInteractionException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(
                    Level.SEVERE, null, ex);
        } catch (MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

//        archiveDetailsList = new ArchiveDetailsList();
//        archiveDetailsList.add(serviceCOMArchive.generateArchiveDetails(new Long(0)));
        archiveDetailsList = HelperArchive.generateArchiveDetailsList(null, null, serviceCOMArchive.getConnectionDetails());
        objType = ConversionServiceInfo.LINECONVERSION_OBJECT_TYPE;
        HeterogeneousList objList2 = new HeterogeneousList();
        objList2.add(this.generateLineConversionDetails());

        try {
            outObjId = serviceCOMArchive.getArchiveStub().store(Boolean.TRUE, objType,
                    serviceCOMArchive.getConnectionDetails().getDomain(), archiveDetailsList, objList2);
        } catch (MALInteractionException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(
                    Level.SEVERE, null, ex);
        } catch (MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

//        archiveDetailsList = new ArchiveDetailsList();
//        archiveDetailsList.add(serviceCOMArchive.generateArchiveDetails(new Long(0)));
        archiveDetailsList = HelperArchive.generateArchiveDetailsList(null, null, serviceCOMArchive.getConnectionDetails());
        objType = ConversionServiceInfo.POLYCONVERSION_OBJECT_TYPE;
        HeterogeneousList objList3 = new HeterogeneousList();
        objList3.add(this.generatePolyConversionDetails());

        try {
            outObjId = serviceCOMArchive.getArchiveStub().store(Boolean.TRUE, objType, serviceCOMArchive
                    .getConnectionDetails().getDomain(), archiveDetailsList, objList3);
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        archiveDetailsList = HelperArchive.generateArchiveDetailsList(null, null, serviceCOMArchive.getConnectionDetails());
        objType = ConversionServiceInfo.RANGECONVERSION_OBJECT_TYPE;
        HeterogeneousList objList4 = new HeterogeneousList();
        objList4.add(this.generateRangeConversionDetails());

        try {
            outObjId = serviceCOMArchive.getArchiveStub().store(Boolean.TRUE, objType, serviceCOMArchive
                    .getConnectionDetails().getDomain(), archiveDetailsList, objList4);
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButtonStoreConversionsActionPerformed

    private void jButtonStoreActionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStoreActionsActionPerformed
        ArchiveConsumerAdapter adapter = new ArchiveConsumerAdapter("Query with Pagination");

        UShort shorty = new UShort(0);
        UOctet octety = new UOctet((short) 0);
        ObjectType objType = new ObjectType(shorty, shorty, octety, shorty);

        ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        ArchiveQuery archiveQuery = new ArchiveQuery(
                null,
                null,
                null,
                new Long(0),
                null,
                null,
                null,
                null,
                null);

        archiveQueryList.add(archiveQuery);

        PaginationFilter filter = new PaginationFilter(new UInteger(5), new UInteger(0));

        MOWindow genObjType = new MOWindow(filter, true);
        try {
            filter = (PaginationFilter) genObjType.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }

        QueryFilterList list = new QueryFilterList();
        list.add(filter);

        try {
            serviceCOMArchive.getArchiveStub().query(Boolean.TRUE, objType, archiveQueryList, list, adapter);
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        /*
        // Object Type
        ObjectType objType = new ObjectType(new UShort(4), new UShort(1), new UOctet((short) 1), new UShort(1));
        
        // Domain
        IdentifierList domain = serviceCOMArchive.getConnectionDetails().getDomain();
        
        // Archive details
        ArchiveDetailsList archiveDetailsList = HelperArchive.generateArchiveDetailsList(null, null, serviceCOMArchive.getConnectionDetails());
        archiveDetailsList.add(archiveDetailsList.get(0));
        MOWindow genArchiveDetailsList = new MOWindow(archiveDetailsList, true);
        try {
            archiveDetailsList = (ArchiveDetailsList) genArchiveDetailsList.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }
        
        ActionDefinition actionDefinition = new ActionDefinition();
        //        actionDefinition.setName(new Identifier("Take_Picture"));
        actionDefinition.setDescription("The action takes a picture and stores it in a the 'picture' parameter.");
        actionDefinition.setProgressStepCount(new UShort(1));
        
        ArgumentDefinition argument = new ArgumentDefinition();
        argument.setRawType(Duration.DURATION_TYPE_SHORT_FORM.byteValue());
        
        ArgumentDefinitionList arguments = new ArgumentDefinitionList();
        arguments.add(argument);
        
        actionDefinition.setArguments(arguments);
        actionDefinition.setArgumentIds(null);
        
        ActionDefinition actionDefinition1 = new ActionDefinition();
        //        actionDefinition1.setName(new Identifier("Take_Picture"));
        actionDefinition1.setDescription("The action takes a picture and stores it in a the 'picture' parameter.");
        actionDefinition1.setProgressStepCount(new UShort(1));
        
        ArgumentDefinition argument1 = new ArgumentDefinition();
        argument1.setRawType(Duration.DURATION_TYPE_SHORT_FORM.byteValue());
        
        ArgumentDefinitionList arguments1 = new ArgumentDefinitionList();
        arguments1.add(argument1);
        
        actionDefinition1.setArguments(arguments1);
        actionDefinition1.setArgumentIds(null);
        
        MOWindow genActionDefinition = new MOWindow(actionDefinition, true);
        try {
            actionDefinition = (ActionDefinition) genActionDefinition.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }
        
        ActionDefinitionList actionDefinitionList = new ActionDefinitionList();
        actionDefinitionList.add(actionDefinition);
        actionDefinitionList.add(actionDefinition1);
        
        // Actually you have to use the Action service to store the definiton
        try {
            LongList received = serviceCOMArchive.getArchiveStub().store(true, objType, domain, archiveDetailsList, actionDefinitionList);
        } catch (MALInteractionException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
         */

    }//GEN-LAST:event_jButtonStoreActionsActionPerformed

    private void jButtonStoreGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStoreGroupsActionPerformed

        // Object Type
        ObjectType objType = new ObjectType(new UShort(4), new UShort(8),
                new UOctet((short) 1), new UShort(1));

        // Domain
        IdentifierList domain = serviceCOMArchive.getConnectionDetails().getDomain();

        // Archive details
        ArchiveDetailsList archiveDetailsList = HelperArchive.generateArchiveDetailsList(
                null, null, serviceCOMArchive.getConnectionDetails());
        MOWindow genArchiveDetailsList = new MOWindow(archiveDetailsList, true);
        try {
            archiveDetailsList = (ArchiveDetailsList) genArchiveDetailsList.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }

        HeterogeneousList groupList = new HeterogeneousList();

        ObjectType objTypeParameterDef = new ObjectType(new UShort(4),
                new UShort(2), new UOctet((short) 1), new UShort(1));
        LongList objIds = new LongList();
        objIds.add(1L);
        objIds.add(2L);
        objIds.add(3L);

        GroupDetails group = new GroupDetails(
                "A group of the 3 first Parameter Definitions.",
                objTypeParameterDef,
                domain,
                objIds);

        groupList.add(group);

        // Actually you have to use the Action service to store the definiton
        try {
            serviceCOMArchive.getArchiveStub().store(false, objType, domain, archiveDetailsList, groupList);
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButtonStoreGroupsActionPerformed

    private void TBoxStoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TBoxStoreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TBoxStoreActionPerformed

    private void jButtonDeleteAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteAllActionPerformed
        // Careful! Deleting all the COM Objects on a provider might cause
        // erratic behaviour. The provider might crash because there are
        // important configurations within.

        ArchiveConsumerAdapter adapter = new ArchiveConsumerAdapter("Deleted objects");

        UShort shorty = new UShort(0);
        UOctet octety = new UOctet((short) 0);
        ObjectType objType = new ObjectType(shorty, shorty, octety, shorty);

        ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        ArchiveQuery archiveQuery = new ArchiveQuery(
                null,
                null,
                null,
                new Long(0),
                null,
                null,
                null,
                null,
                null);

        archiveQueryList.add(archiveQuery);

        try {
            serviceCOMArchive.getArchiveStub().query(Boolean.TRUE, objType, archiveQueryList, null, adapter);
            adapter.deleteAllInTable();  // Deletes all the objects in the table
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButtonDeleteAllActionPerformed

    public static ArchiveQuery generateArchiveQuery() {
        // ArchiveDetails
        return new ArchiveQuery(
                null,
                null,
                null,
                new Long(0),
                null,
                null,
                null,
                null,
                null);
    }

    public static CompositeFilter generateCompositeFilter() {
        CompositeFilter compositeFilter = new CompositeFilter(
                "name",
                ExpressionOperator.EQUAL,
                new Identifier("AggregationUpdate"));

        return compositeFilter;
    }

    private void open_ArchiveSyncActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_open_ArchiveSyncActionPerformed

        if (serviceTabs != null) {
            if (services.getCOMServices().getArchiveSyncService() != null) {
                ArchiveSyncConsumerManagerPanel panel = new ArchiveSyncConsumerManagerPanel(
                        services.getCOMServices().getArchiveService(),
                        services.getCOMServices().getArchiveSyncService());
                serviceTabs.insertTab("ArchiveSync service", null, panel, "ArchiveSync Tab", location);
            }
        }
    }//GEN-LAST:event_open_ArchiveSyncActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField TBoxStore;
    private javax.swing.JPanel homeTab;
    private javax.swing.JButton jButtonCount;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonDeleteAll;
    private javax.swing.JButton jButtonGetAll;
    private javax.swing.JButton jButtonQuery;
    private javax.swing.JButton jButtonRetrieve;
    private javax.swing.JButton jButtonStoreActions;
    private javax.swing.JButton jButtonStoreAggregation;
    private javax.swing.JButton jButtonStoreConversions;
    private javax.swing.JButton jButtonStoreGroups;
    private javax.swing.JButton jButtonUpdate;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JButton open_ArchiveSync;
    private javax.swing.JTabbedPane tabs;
    // End of variables declaration//GEN-END:variables
}
