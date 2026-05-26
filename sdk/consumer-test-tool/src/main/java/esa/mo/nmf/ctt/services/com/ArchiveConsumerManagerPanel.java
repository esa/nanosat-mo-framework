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
import esa.mo.nmf.ctt.windows.element.MOWindow;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
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
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.aggregation.AggregationServiceInfo;
import org.ccsds.moims.mo.mc.structures.*;

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
        jLabel6.setFont(jLabel6.getFont().deriveFont(java.awt.Font.BOLD, 18f));
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
                new Identifier(name),
                "This is a description",
                AggregationCategory.GENERAL,
                new Duration(0),
                Boolean.FALSE,
                Boolean.FALSE,
                Boolean.FALSE,
                new Duration(0),
                Boolean.FALSE,
                aggParamSetList);
    }

    private LineConversion generateLineConversion() {
        PairList points = new PairList();
        points.add(new Pair(new Union(1), new Union(33.8)));
        points.add(new Pair(new Union(100), new Union(212)));
        return new LineConversion(true, points);
    }

    private PolyConversion generatePolyConversion() {
        PairList points = new PairList();
        points.add(new Pair(new Union(0), new Union(32)));
        points.add(new Pair(new Union(1), new Union(1.8)));
        return new PolyConversion(points);
    }

    private DiscreteConversion generateDiscreteConversion() {
        PairList mapping = new PairList();

        mapping.add(new Pair(new Union(0), new Union("Mode 0")));
        mapping.add(new Pair(new Union(1), new Union("Mode 1")));
        mapping.add(new Pair(new Union(2), new Union("Mode 2")));
        mapping.add(new Pair(new Union(3), new Union("Mode 3")));

        return new DiscreteConversion(mapping);
    }

    private RangeConversion generateRangeConversion() {
        PairList points = new PairList();
        points.add(new Pair(new Union(0), new Union("Between 0-100")));
        points.add(new Pair(new Union(100), new Union("Between 100-inf")));
        return new RangeConversion(points);
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
            n_objs_counter = n_objs_counter + objDetails.size();
            javax.swing.SwingUtilities.invokeLater(() -> {
                archiveTablePanel.addEntries(archiveObjectOutput);
                refreshTabCounter();
            });
        }

        @Override
        public synchronized void countResponseReceived(MALMessageHeader msgHeader,
                Long count, Map qosProperties) {
            JOptionPane.showMessageDialog(null, count.toString(),
                    "The count operation returned the following data!", JOptionPane.PLAIN_MESSAGE);
        }

        @Override
        public synchronized void queryResponseReceived(MALMessageHeader msgHeader, Map qosProperties) {
            archiveTablePanel.sortByTimestamp();
            javax.swing.SwingUtilities.invokeLater(this::refreshTabCounter);
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
                javax.swing.SwingUtilities.invokeLater(() -> {
                    refreshTabCounter();
                    repaint();
                });
                return;
            }
            ArchiveCOMObjectsOutput archiveObjectOutput = new ArchiveCOMObjectsOutput(
                    domain, objType, objDetails, objBodies);
            n_objs_counter = n_objs_counter + objDetails.size();
            javax.swing.SwingUtilities.invokeLater(() -> {
                archiveTablePanel.addEntries(archiveObjectOutput);
                refreshTabCounter();
                repaint();
            });
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
                objIds.add(comObject.getArchiveDetails().getId());
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
        jButtonStorePlaceholder = new javax.swing.JButton();
        TBoxStore = new javax.swing.JTextField();
        jButtonDeleteAll = new javax.swing.JButton();
        open_ArchiveSync = new javax.swing.JButton();

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("COM Archive Manager");
        jLabel6.setToolTipText("");

        jButtonStoreAggregation.setText("Quick Store (Aggregation)");
        jButtonStoreAggregation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStoreAggregationActionPerformed(evt);
            }
        });

        jButtonGetAll.setText("Get All");
        jButtonGetAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGetAllActionPerformed(evt);
            }
        });

        jButtonQuery.setText("Execute Query");
        jButtonQuery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonQueryActionPerformed(evt);
            }
        });

        jButtonDelete.setText("Delete Object");
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });

        jButtonRetrieve.setText("Retrieve Object");
        jButtonRetrieve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRetrieveActionPerformed(evt);
            }
        });

        jButtonUpdate.setText("Update Object");
        jButtonUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdateActionPerformed(evt);
            }
        });

        jButtonCount.setText("Count Objects");
        jButtonCount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCountActionPerformed(evt);
            }
        });

        tabs.setRequestFocusEnabled(false);
        tabs.addTab("Home", homeTab);

        jButtonStoreConversions.setText("Free Placeholder");
        jButtonStoreConversions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStoreConversionsActionPerformed(evt);
            }
        });

        jButtonStoreActions.setText("Query with Pagination");
        jButtonStoreActions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStoreActionsActionPerformed(evt);
            }
        });

        TBoxStore.setEditable(false);
        TBoxStore.setToolTipText("Last stored object ID");
        TBoxStore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TBoxStoreActionPerformed(evt);
            }
        });

        jButtonDeleteAll.setText("Delete All");
        jButtonDeleteAll.setEnabled(false);
        jButtonDeleteAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteAllActionPerformed(evt);
            }
        });

        open_ArchiveSync.setText("Open ArchiveSync Tab");
        open_ArchiveSync.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                open_ArchiveSyncActionPerformed(evt);
            }
        });

        // Row 1: Retrieve | Update | Count | Execute Query | Quick Store (Aggregation) | Open ArchiveSync Tab
        javax.swing.JPanel row1 = new javax.swing.JPanel(new java.awt.GridLayout(1, 6, 4, 0));
        row1.add(jButtonRetrieve);
        row1.add(jButtonUpdate);
        row1.add(jButtonCount);
        row1.add(jButtonQuery);
        row1.add(jButtonStoreAggregation);
        row1.add(open_ArchiveSync);

        // Row 2: Get All | Delete All | Delete Object | Query with Pagination | Quick Store (Conversions) | [Stored ID field]
        javax.swing.JPanel storeIdPanel = new javax.swing.JPanel(new java.awt.BorderLayout(3, 0));
        storeIdPanel.add(new javax.swing.JLabel("ID:"), java.awt.BorderLayout.WEST);
        storeIdPanel.add(TBoxStore, java.awt.BorderLayout.CENTER);

        javax.swing.JPanel row2 = new javax.swing.JPanel(new java.awt.GridLayout(1, 6, 4, 0));
        row2.add(jButtonGetAll);
        row2.add(jButtonDeleteAll);
        row2.add(jButtonDelete);
        row2.add(jButtonStoreActions);
        row2.add(jButtonStoreConversions);
        row2.add(storeIdPanel);

        javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
        buttonPanel.setLayout(new javax.swing.BoxLayout(buttonPanel, javax.swing.BoxLayout.Y_AXIS));
        buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));
        buttonPanel.add(row1);
        buttonPanel.add(javax.swing.Box.createVerticalStrut(4));
        buttonPanel.add(row2);

        javax.swing.JPanel northPanel = new javax.swing.JPanel(new java.awt.BorderLayout());
        northPanel.add(jLabel6, java.awt.BorderLayout.NORTH);
        northPanel.add(buttonPanel, java.awt.BorderLayout.SOUTH);

        this.setLayout(new java.awt.BorderLayout());
        this.add(northPanel, java.awt.BorderLayout.NORTH);
        this.add(tabs, java.awt.BorderLayout.CENTER);
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

        try {
            serviceCOMArchive.getArchiveStub().query(Boolean.TRUE, objType,
                    archiveQuery, null, adapter);
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
        MOWindow genArchiveQuery = new MOWindow(archiveQuery, true);
        try {
            archiveQuery = (ArchiveQuery) genArchiveQuery.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }

        // Composite Filter
        CompositeFilterList compositeFilterList = new CompositeFilterList();
        compositeFilterList.add(ArchiveConsumerManagerPanel.generateCompositeFilter());
        QueryFilter compositeFilter = new CompositeFilterSet(compositeFilterList);
        MOWindow genFilter = new MOWindow(compositeFilter, true);
        try {
            compositeFilter = (QueryFilter) genFilter.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }

        try {
            adapter.setObjType(objType);
            serviceCOMArchive.getArchiveStub().query(Boolean.TRUE, objType,
                    archiveQuery, compositeFilter, adapter);
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
        MOWindow genArchiveQuery = new MOWindow(archiveQuery, true);
        try {
            archiveQuery = (ArchiveQuery) genArchiveQuery.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }

        // Composite Filter
        CompositeFilterList compositeFilterList = new CompositeFilterList();
        compositeFilterList.add(ArchiveConsumerManagerPanel.generateCompositeFilter());
        QueryFilter compositeFilter = new CompositeFilterSet(compositeFilterList);
        MOWindow genFilter = new MOWindow(compositeFilter, true);
        try {
            compositeFilter = (QueryFilter) genFilter.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }

        ArchiveConsumerAdapter adapter = new ArchiveConsumerAdapter("Count...");
        adapter.setObjType(objType);

        try {
            serviceCOMArchive.getArchiveStub().count(objType, archiveQuery, compositeFilter, adapter);
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
                serviceCOMArchive.getArchiveStub().query(true, objType, archiveQuery, compositeFilter, adapter);
            } catch (MALInteractionException | MALException ex) {
                Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_jButtonCountActionPerformed

    private void jButtonStoreConversionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStoreConversionsActionPerformed
    }//GEN-LAST:event_jButtonStoreConversionsActionPerformed

    private void jButtonStoreActionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStoreActionsActionPerformed
        ArchiveConsumerAdapter adapter = new ArchiveConsumerAdapter("Query with Pagination");

        UShort shorty = new UShort(0);
        UOctet octety = new UOctet((short) 0);
        ObjectType objType = new ObjectType(shorty, shorty, octety, shorty);

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

        PaginationFilter filter = new PaginationFilter(new UInteger(5), new UInteger(0));

        MOWindow genObjType = new MOWindow(filter, true);
        try {
            filter = (PaginationFilter) genObjType.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }

        try {
            serviceCOMArchive.getArchiveStub().query(Boolean.TRUE, objType, archiveQuery, filter, adapter);
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(
                    Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_jButtonStoreActionsActionPerformed

    private void jButtonStorePlaceholderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStorePlaceholderActionPerformed
        // Group service removed
    }//GEN-LAST:event_jButtonStorePlaceholderActionPerformed

    private void TBoxStoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TBoxStoreActionPerformed
    }//GEN-LAST:event_TBoxStoreActionPerformed

    private void jButtonDeleteAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteAllActionPerformed
        // Careful! Deleting all the COM Objects on a provider might cause
        // erratic behaviour. The provider might crash because there are
        // important configurations within.

        ArchiveConsumerAdapter adapter = new ArchiveConsumerAdapter("Deleted objects");

        UShort shorty = new UShort(0);
        UOctet octety = new UOctet((short) 0);
        ObjectType objType = new ObjectType(shorty, shorty, octety, shorty);

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

        try {
            serviceCOMArchive.getArchiveStub().query(Boolean.TRUE, objType, archiveQuery, null, adapter);
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
    private javax.swing.JButton jButtonStorePlaceholder;
    private javax.swing.JButton jButtonUpdate;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JButton open_ArchiveSync;
    private javax.swing.JTabbedPane tabs;
    // End of variables declaration//GEN-END:variables
}
