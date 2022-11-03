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
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import esa.mo.tools.mowindow.MOWindow;
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
import org.ccsds.moims.mo.com.archive.structures.CompositeFilterSetList;
import org.ccsds.moims.mo.com.archive.structures.ExpressionOperator;
import org.ccsds.moims.mo.com.archive.structures.PaginationFilter;
import org.ccsds.moims.mo.com.archive.structures.PaginationFilterList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
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
import org.ccsds.moims.mo.mc.aggregation.AggregationHelper;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetails;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetailsList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSet;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSetList;
import org.ccsds.moims.mo.mc.conversion.ConversionHelper;
import org.ccsds.moims.mo.mc.conversion.structures.DiscreteConversionDetails;
import org.ccsds.moims.mo.mc.conversion.structures.DiscreteConversionDetailsList;
import org.ccsds.moims.mo.mc.conversion.structures.LineConversionDetails;
import org.ccsds.moims.mo.mc.conversion.structures.LineConversionDetailsList;
import org.ccsds.moims.mo.mc.conversion.structures.PolyConversionDetails;
import org.ccsds.moims.mo.mc.conversion.structures.PolyConversionDetailsList;
import org.ccsds.moims.mo.mc.conversion.structures.RangeConversionDetails;
import org.ccsds.moims.mo.mc.conversion.structures.RangeConversionDetailsList;
import org.ccsds.moims.mo.mc.group.structures.GroupDetails;
import org.ccsds.moims.mo.mc.group.structures.GroupDetailsList;

/**
 *
 * @author Cesar Coelho
 */
public class ArchiveConsumerManagerPanel extends javax.swing.JPanel {

    private final ArchiveConsumerServiceImpl serviceCOMArchive;
    private int location;
    private JTabbedPane serviceTabs = null;
    private GroundMOAdapterImpl services;

    /**
     * Creates new form ArchiveConsumerPanel
     *
     * @param archiveService
     */
    public ArchiveConsumerManagerPanel(final ArchiveConsumerServiceImpl archiveService) {
        initComponents();
        serviceCOMArchive = archiveService;
    }

    public void setArchiveSyncConfigs(final int count, final JTabbedPane serviceTabs, final GroundMOAdapterImpl services) {
        this.location = count;
        this.serviceTabs = serviceTabs;
        this.services = services;
    }

    public static AggregationDefinitionDetails generateAggregationDefinition(final String name) {
        // AgregationDefinition
        final AggregationDefinitionDetails aggDef = new AggregationDefinitionDetails();
        aggDef.setDescription("This is a description");
        aggDef.setCategory(new UOctet((short) 0));
        aggDef.setReportInterval(new Duration(0));
        aggDef.setSendUnchanged(Boolean.FALSE);
        aggDef.setSendDefinitions(Boolean.FALSE);
        aggDef.setFilterEnabled(Boolean.FALSE);
        aggDef.setFilteredTimeout(new Duration(0));
        aggDef.setGenerationEnabled(Boolean.FALSE);

        final AggregationParameterSetList aggParamSetList = new AggregationParameterSetList();
        final AggregationParameterSet aggParamSet = new AggregationParameterSet();
        aggParamSet.setSampleInterval(new Duration(1));
        final LongList objIdParams = new LongList();
        objIdParams.add(1L);
        aggParamSet.setParameters(objIdParams);
        aggParamSetList.add(aggParamSet);
        aggDef.setParameterSets(aggParamSetList);

        return aggDef;
    }

    private LineConversionDetails generateLineConversionDetails() {
        final LineConversionDetails convDetails = new LineConversionDetails();

        convDetails.setExtrapolate(true);

        final PairList points = new PairList();

        final Pair pair0 = new Pair();
        pair0.setFirst(new Union(1));
        pair0.setSecond(new Union(33.8));
        points.add(pair0);

        final Pair pair1 = new Pair();
        pair1.setFirst(new Union(100));
        pair1.setSecond(new Union(212));
        points.add(pair1);

        convDetails.setPoints(points);

        return convDetails;
    }

    private PolyConversionDetails generatePolyConversionDetails() {
        final PolyConversionDetails convDetails = new PolyConversionDetails();

        final PairList points = new PairList();

        final Pair pair0 = new Pair();
        pair0.setFirst(new Union(0));
        pair0.setSecond(new Union(32));
        points.add(pair0);

        final Pair pair1 = new Pair();
        pair1.setFirst(new Union(1));
        pair1.setSecond(new Union(1.8));
        points.add(pair1);

        convDetails.setPoints(points);

        return convDetails;
    }

    private DiscreteConversionDetails generateDiscreteConversionDetails() {
        final PairList mapping = new PairList();

        final Pair pair0 = new Pair();
        pair0.setFirst(new Union(0));
        pair0.setSecond(new Union("Mode 0"));
        mapping.add(pair0);

        final Pair pair1 = new Pair();
        pair1.setFirst(new Union(1));
        pair1.setSecond(new Union("Mode 1"));
        mapping.add(pair1);

        final Pair pair2 = new Pair();
        pair2.setFirst(new Union(2));
        pair2.setSecond(new Union("Mode 2"));
        mapping.add(pair2);

        final Pair pair3 = new Pair();
        pair3.setFirst(new Union(3));
        pair3.setSecond(new Union("Mode 3"));
        mapping.add(pair3);

        return new DiscreteConversionDetails(mapping);
    }

    private RangeConversionDetails generateRangeConversionDetails() {
        final RangeConversionDetails convDetails = new RangeConversionDetails();

        final PairList points = new PairList();

        final Pair pair0 = new Pair();
        pair0.setFirst(new Union(0));
        pair0.setSecond(new Union("Between 0-100"));
        points.add(pair0);

        final Pair pair1 = new Pair();
        pair1.setFirst(new Union(100));
        pair1.setSecond(new Union("Between 100-inf"));
        points.add(pair1);

        convDetails.setPoints(points);

        return convDetails;
    }

    protected class ArchiveConsumerAdapter extends ArchiveAdapter {

        private final ArchiveTablePanel archiveTablePanel = new ArchiveTablePanel(null, serviceCOMArchive);
        private ObjectType objType;
        private IdentifierList domain;
        private final Semaphore isOver = new Semaphore(0);
        private int n_objs_counter = 0;
        private final javax.swing.JPanel pnlTab = new javax.swing.JPanel();
        private final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
        private final Date date = new Date(System.currentTimeMillis());
        private final String functionName;

        ArchiveConsumerAdapter(final String stringLabel) {
            pnlTab.setOpaque(false);
            functionName = stringLabel;
            this.refreshTabCounter();
            tabs.addTab("", archiveTablePanel);
            tabs.setTabComponentAt(tabs.getTabCount() - 1, pnlTab);
            tabs.setSelectedIndex(tabs.getTabCount() - 1);
        }

        private void refreshTabCounter() {
            final JLabel label = new JLabel(functionName + " (" + dateFormat.format(date) + ")" + " (" + n_objs_counter + ")");
            final JLabel closeLabel = new JLabel("x");
            closeLabel.addMouseListener(new CloseMouseHandler(this));
            closeLabel.setFont(closeLabel.getFont().deriveFont(closeLabel.getFont().getStyle() | Font.BOLD));

            final GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1;

            gbc.gridx++;
            gbc.weightx = 0;

            pnlTab.removeAll();
            pnlTab.add(label, gbc);
            pnlTab.add(closeLabel, gbc);

            final int index = tabs.indexOfTabComponent(archiveTablePanel);

            if (index == -1) {
                return;
            }

            tabs.setTabComponentAt(index, pnlTab);
        }

        public synchronized void finalizeAdapter() {
            try {
                this.finalize();
            } catch (final Throwable ex) {
                Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public synchronized int getSelectedIndex() {
            return archiveTablePanel.getSelectedRow();
        }

        protected void setObjType(final ObjectType objType) {
            this.objType = objType;
        }

        protected void setDomain(final IdentifierList domain) {
            this.domain = domain;
        }

        protected ObjectType getObjType() {
            return this.objType;
        }

        protected IdentifierList getDomain() {
            return this.domain;
        }

        @Override
        public void retrieveAckReceived(final MALMessageHeader msgHeader, final Map qosProperties) {

            // Later on, do something...
        }

        @Override
        public synchronized void retrieveResponseReceived(final MALMessageHeader msgHeader,
                                                          final ArchiveDetailsList objDetails, final ElementList objBodies, final Map qosProperties) {
            final ArchiveCOMObjectsOutput archiveObjectOutput = new ArchiveCOMObjectsOutput(domain, objType, objDetails, objBodies);
            archiveTablePanel.addEntries(archiveObjectOutput);
            n_objs_counter = n_objs_counter + objDetails.size();
            refreshTabCounter();
        }

        @Override
        public synchronized void countResponseReceived(final MALMessageHeader msgHeader,
                                                       final LongList _LongList0, final Map qosProperties) {
            JOptionPane.showMessageDialog(null, _LongList0.toString(),
                    "The count operation returned the following data!", JOptionPane.PLAIN_MESSAGE);
        }

        @Override
        public synchronized void queryResponseReceived(final MALMessageHeader msgHeader, ObjectType objType,
                                                       final IdentifierList domain, final ArchiveDetailsList objDetails, final ElementList objBodies, final Map qosProperties) {
            // If object type is null all objects have the same type
            if(objType == null) {
                objType = this.objType;
            }
            if (objType == null || domain == null || objDetails == null) {
                refreshTabCounter();
                isOver.release();
                return;
            }
            final ArchiveCOMObjectsOutput archiveObjectOutput = new ArchiveCOMObjectsOutput(domain, objType, objDetails, objBodies);
            archiveTablePanel.addEntries(archiveObjectOutput);
            n_objs_counter = n_objs_counter + objDetails.size();
            refreshTabCounter();

            isOver.release();
        }

        @Override
        public synchronized void queryUpdateReceived(final MALMessageHeader msgHeader, ObjectType objType,
                                                     final IdentifierList domain, final ArchiveDetailsList objDetails, final ElementList objBodies, final Map qosProperties) {
            // If object type is null all objects have the same type
            if(objType == null) {
                objType = this.objType;
            }
            if (objType == null || domain == null || objDetails == null) {
                refreshTabCounter();
                repaint();
                return;
            }
            final ArchiveCOMObjectsOutput archiveObjectOutput = new ArchiveCOMObjectsOutput(domain, objType, objDetails, objBodies);
            archiveTablePanel.addEntries(archiveObjectOutput);
            n_objs_counter = n_objs_counter + objDetails.size();
            refreshTabCounter();
            repaint();
        }

        @Override
        public synchronized void queryAckErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, "queryAckErrorReceived", error);
        }

        protected void deleteAllInTable() {
            try {
                isOver.acquire();
            } catch (final InterruptedException ex) {
                Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
            }

            final List<ArchivePersistenceObject> comObjects = archiveTablePanel.getAllCOMObjects();

            for (final ArchivePersistenceObject comObject : comObjects) {
                final LongList objIds = new LongList();
                objIds.add(comObject.getArchiveDetails().getInstId());
                try {
                    serviceCOMArchive.getArchiveStub().delete(comObject.getObjectType(), comObject.getDomain(), objIds);
                } catch (final MALInteractionException | MALException ex) {
                    Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

        private JPanel getPanel() {
            return pnlTab;
        }

    }

    public class CloseMouseHandler implements MouseListener {

        private final ArchiveConsumerAdapter adapter;

        CloseMouseHandler(final ArchiveConsumerAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public void mouseClicked(final MouseEvent evt) {
            for (int i = 0; i < tabs.getTabCount(); i++) {
                final Component component = tabs.getTabComponentAt(i);
                final JPanel panel = adapter.getPanel();

                if (component == panel) {
                    tabs.remove(i);
                    adapter.finalizeAdapter();

                    try {
                        super.finalize();
                    } catch (final Throwable ex) {
                        Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return;
                }
            }
        }

        @Override
        public void mousePressed(final MouseEvent me) {
        }

        @Override
        public void mouseReleased(final MouseEvent me) {
        }

        @Override
        public void mouseEntered(final MouseEvent me) {
        }

        @Override
        public void mouseExited(final MouseEvent me) {
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

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonGetAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonRetrieve))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonDeleteAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonCount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonQuery, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonStoreActions, javax.swing.GroupLayout.PREFERRED_SIZE, 149, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonStoreConversions, javax.swing.GroupLayout.PREFERRED_SIZE, 178, Short.MAX_VALUE)
                    .addComponent(jButtonStoreGroups, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(open_ArchiveSync, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonStoreAggregation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TBoxStore, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(tabs, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 957, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonRetrieve)
                    .addComponent(jButtonStoreAggregation)
                    .addComponent(jButtonUpdate)
                    .addComponent(jButtonStoreGroups)
                    .addComponent(TBoxStore, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonQuery)
                    .addComponent(jButtonCount))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonGetAll)
                    .addComponent(jButtonDeleteAll)
                    .addComponent(jButtonDelete)
                    .addComponent(jButtonStoreActions)
                    .addComponent(jButtonStoreConversions)
                    .addComponent(open_ArchiveSync))
                .addContainerGap(438, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(129, 129, 129)
                    .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonStoreAggregationActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStoreAggregationActionPerformed
//        ArchiveDetailsList archiveDetailsList = HelperArchive.generateArchiveDetailsList(null, null, serviceCOMArchive.getConnectionDetails());
        final ArchiveDetailsList archiveDetailsList = HelperArchive.generateArchiveDetailsList(null, null, serviceCOMArchive.getConnectionDetails().getProviderURI());

        final AggregationDefinitionDetailsList objList = new AggregationDefinitionDetailsList();
        objList.add(ArchiveConsumerManagerPanel.generateAggregationDefinition("AggregationStore"));

        try {
            final LongList outObjId = serviceCOMArchive.getArchiveStub().store(Boolean.TRUE,
                    AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE,
                    serviceCOMArchive.getConnectionDetails().getDomain(), archiveDetailsList, objList);
            final Long received = outObjId.get(0);
            TBoxStore.setText(received.toString());
        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonStoreAggregationActionPerformed

    private void jButtonGetAllActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGetAllActionPerformed
        final ArchiveConsumerAdapter adapter = new ArchiveConsumerAdapter("Get All");

        final UShort shorty = new UShort(0);
        final UOctet octety = new UOctet((short) 0);
        final ObjectType objType = new ObjectType(shorty, shorty, octety, shorty);

        final ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        final ArchiveQuery archiveQuery = new ArchiveQuery();

        archiveQuery.setDomain(null);
        archiveQuery.setNetwork(null);
        archiveQuery.setProvider(null);
        archiveQuery.setRelated(0L);
        archiveQuery.setSource(null);
        archiveQuery.setStartTime(null);
        archiveQuery.setEndTime(null);
        archiveQuery.setSortFieldName(null);
        archiveQuery.setSortFieldName(null);

        archiveQueryList.add(archiveQuery);

        try {
            serviceCOMArchive.getArchiveStub().query(Boolean.TRUE, objType, archiveQueryList, null, adapter);
        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonGetAllActionPerformed


    private void jButtonQueryActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonQueryActionPerformed
        final ArchiveConsumerAdapter adapter = new ArchiveConsumerAdapter("Query...");

        // Object Type
        ObjectType objType = AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE;
        final MOWindow genObjType = new MOWindow(objType, true);
        try {
            objType = (ObjectType) genObjType.getObject();
        } catch (final InterruptedIOException ex) {
            return;
        }

        // Archive Query
        final ArchiveQuery archiveQuery = ArchiveConsumerManagerPanel.generateArchiveQuery();
        ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        archiveQueryList.add(archiveQuery);
        final MOWindow genArchiveQueryList = new MOWindow(archiveQueryList, true);
        try {
            archiveQueryList = (ArchiveQueryList) genArchiveQueryList.getObject();
        } catch (final InterruptedIOException ex) {
            return;
        }

        // Composite Filter
        CompositeFilterSetList compositeFilters = new CompositeFilterSetList();
        final CompositeFilterSet compositeFilterSet = new CompositeFilterSet();
        final CompositeFilterList compositeFilterList = new CompositeFilterList();
        compositeFilterList.add(ArchiveConsumerManagerPanel.generateCompositeFilter());
        compositeFilterSet.setFilters(compositeFilterList);
        compositeFilters.add(compositeFilterSet);
        final MOWindow genFilter = new MOWindow(compositeFilters, true);
        try {
            compositeFilters = (CompositeFilterSetList) genFilter.getObject();
        } catch (final InterruptedIOException ex) {
            return;
        }

        try {
            adapter.setObjType(objType);
            serviceCOMArchive.getArchiveStub().query(Boolean.TRUE, objType, archiveQueryList, compositeFilters, adapter);
        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButtonQueryActionPerformed

    private void jButtonDeleteActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed

        final ArchivePersistenceObject comObject = ((ArchiveTablePanel) tabs.getSelectedComponent()).getSelectedCOMObject();

        final LongList objIds = new LongList();
        objIds.add(comObject.getObjectId());

        try {
            serviceCOMArchive.getArchiveStub().delete(comObject.getObjectType(), comObject.getDomain(), objIds);
        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        ((ArchiveTablePanel) tabs.getSelectedComponent()).removeSelectedEntry();

    }//GEN-LAST:event_jButtonDeleteActionPerformed

    private void jButtonRetrieveActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRetrieveActionPerformed

        // Object Type
        ObjectType objType = AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE;
        final MOWindow genObjType = new MOWindow(objType, true);
        try {
            objType = (ObjectType) genObjType.getObject();
        } catch (final InterruptedIOException ex) {
            return;
        }

        // Object Instance Identifier
        final Long objId = (long) 0;
        LongList objIds = new LongList();
        objIds.add(objId);
        final MOWindow genObjId = new MOWindow(objIds, true);
        try {
            objIds = (LongList) genObjId.getObject();
        } catch (final InterruptedIOException ex) {
            return;
        }

        final IdentifierList domain = serviceCOMArchive.getConnectionDetails().getDomain();

        final ArchiveConsumerAdapter adapter = new ArchiveConsumerAdapter("Retrieve...");
        adapter.setDomain(domain);
        adapter.setObjType(objType);

        try {
            serviceCOMArchive.getArchiveStub().retrieve(objType, domain, objIds, adapter);
        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButtonRetrieveActionPerformed

    @SuppressWarnings("unchecked")
    private void jButtonUpdateActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateActionPerformed

        final ArchivePersistenceObject comObject = ((ArchiveTablePanel) tabs.getSelectedComponent()).getSelectedCOMObject();
        final MOWindow objBodyWindow = new MOWindow(comObject.getObject(), true);
        final ArchiveDetailsList archiveDetailsList = new ArchiveDetailsList();
        archiveDetailsList.add(comObject.getArchiveDetails());
        final ElementList finalObject;

        try {
            finalObject = HelperMisc.element2elementList(objBodyWindow.getObject());
            finalObject.add(objBodyWindow.getObject());

            try {
                serviceCOMArchive.getArchiveStub().update(comObject.getObjectType(), comObject.getDomain(), archiveDetailsList, finalObject);
            } catch (final MALInteractionException | MALException ex) {
                Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (final Exception ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButtonUpdateActionPerformed

    private void jButtonCountActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCountActionPerformed

        // Object Type
        ObjectType objType = AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE;
        final MOWindow genObjType = new MOWindow(objType, true);
        try {
            objType = (ObjectType) genObjType.getObject();
        } catch (final InterruptedIOException ex) {
            return;
        }

        // Archive Query
        final ArchiveQuery archiveQuery = ArchiveConsumerManagerPanel.generateArchiveQuery();
        ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        archiveQueryList.add(archiveQuery);
        final MOWindow genArchiveQueryList = new MOWindow(archiveQueryList, true);
        try {
            archiveQueryList = (ArchiveQueryList) genArchiveQueryList.getObject();
        } catch (final InterruptedIOException ex) {
            return;
        }

        // Composite Filter
        CompositeFilterSetList compositeFilters = new CompositeFilterSetList();
        final CompositeFilterSet compositeFilterSet = new CompositeFilterSet();
        final CompositeFilterList compositeFilterList = new CompositeFilterList();
        compositeFilterList.add(ArchiveConsumerManagerPanel.generateCompositeFilter());
        compositeFilterSet.setFilters(compositeFilterList);
        compositeFilters.add(compositeFilterSet);
        final MOWindow genFilter = new MOWindow(compositeFilters, true);
        try {
            compositeFilters = (CompositeFilterSetList) genFilter.getObject();
        } catch (final InterruptedIOException ex) {
            return;
        }

        final ArchiveConsumerAdapter adapter = new ArchiveConsumerAdapter("Count...");
        adapter.setObjType(objType);

        try {
            serviceCOMArchive.getArchiveStub().count(objType, archiveQueryList, compositeFilters, adapter);
        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        final JDialog frame = new JDialog();
        final int n = JOptionPane.showConfirmDialog(frame, "Would you like to automatically query and get the objects?", "Query?", JOptionPane.YES_NO_OPTION);

        if (n == JOptionPane.YES_OPTION) {
            try {
                serviceCOMArchive.getArchiveStub().query(true, objType, archiveQueryList, compositeFilters, adapter);
            } catch (final MALInteractionException | MALException ex) {
                Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_jButtonCountActionPerformed

    private void jButtonStoreConversionsActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStoreConversionsActionPerformed
        LongList outObjId;
//        ArchiveDetailsList archiveDetailsList;
        ObjectType objType;

//        archiveDetailsList = new ArchiveDetailsList();
//        archiveDetailsList.add(serviceCOMArchive.generateArchiveDetails(new Long(0)));
        ArchiveDetailsList archiveDetailsList = HelperArchive.generateArchiveDetailsList(null, null, serviceCOMArchive.getConnectionDetails());
        objType = ConversionHelper.DISCRETECONVERSION_OBJECT_TYPE;
        final DiscreteConversionDetailsList objList1 = new DiscreteConversionDetailsList();
        objList1.add(this.generateDiscreteConversionDetails());

        try {
            outObjId = serviceCOMArchive.getArchiveStub().store(Boolean.TRUE, objType, serviceCOMArchive.getConnectionDetails().getDomain(), archiveDetailsList, objList1);
        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

//        archiveDetailsList = new ArchiveDetailsList();
//        archiveDetailsList.add(serviceCOMArchive.generateArchiveDetails(new Long(0)));
        archiveDetailsList = HelperArchive.generateArchiveDetailsList(null, null, serviceCOMArchive.getConnectionDetails());
        objType = ConversionHelper.LINECONVERSION_OBJECT_TYPE;
        final LineConversionDetailsList objList2 = new LineConversionDetailsList();
        objList2.add(this.generateLineConversionDetails());

        try {
            outObjId = serviceCOMArchive.getArchiveStub().store(Boolean.TRUE, objType, serviceCOMArchive.getConnectionDetails().getDomain(), archiveDetailsList, objList2);
        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

//        archiveDetailsList = new ArchiveDetailsList();
//        archiveDetailsList.add(serviceCOMArchive.generateArchiveDetails(new Long(0)));
        archiveDetailsList = HelperArchive.generateArchiveDetailsList(null, null, serviceCOMArchive.getConnectionDetails());
        objType = ConversionHelper.POLYCONVERSION_OBJECT_TYPE;
        final PolyConversionDetailsList objList3 = new PolyConversionDetailsList();
        objList3.add(this.generatePolyConversionDetails());

        try {
            outObjId = serviceCOMArchive.getArchiveStub().store(Boolean.TRUE, objType, serviceCOMArchive.getConnectionDetails().getDomain(), archiveDetailsList, objList3);
        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

//        archiveDetailsList = new ArchiveDetailsList();
//        archiveDetailsList.add(serviceCOMArchive.generateArchiveDetails(new Long(0)));
        archiveDetailsList = HelperArchive.generateArchiveDetailsList(null, null, serviceCOMArchive.getConnectionDetails());
        objType = ConversionHelper.RANGECONVERSION_OBJECT_TYPE;
        final RangeConversionDetailsList objList4 = new RangeConversionDetailsList();
        objList4.add(this.generateRangeConversionDetails());

        try {
            outObjId = serviceCOMArchive.getArchiveStub().store(Boolean.TRUE, objType, serviceCOMArchive.getConnectionDetails().getDomain(), archiveDetailsList, objList4);
        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_jButtonStoreConversionsActionPerformed

    private void jButtonStoreActionsActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStoreActionsActionPerformed
        final ArchiveConsumerAdapter adapter = new ArchiveConsumerAdapter("Query with Pagination");

        final UShort shorty = new UShort(0);
        final UOctet octety = new UOctet((short) 0);
        final ObjectType objType = new ObjectType(shorty, shorty, octety, shorty);

        final ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        final ArchiveQuery archiveQuery = new ArchiveQuery();

        archiveQuery.setDomain(null);
        archiveQuery.setNetwork(null);
        archiveQuery.setProvider(null);
        archiveQuery.setRelated(0L);
        archiveQuery.setSource(null);
        archiveQuery.setStartTime(null);
        archiveQuery.setEndTime(null);
        archiveQuery.setSortFieldName(null);
        archiveQuery.setSortFieldName(null);

        archiveQueryList.add(archiveQuery);

        PaginationFilter filter = new PaginationFilter();
        filter.setLimit(new UInteger(5));
        filter.setOffset(new UInteger(0));

        final MOWindow genObjType = new MOWindow(filter, true);
        try {
            filter = (PaginationFilter) genObjType.getObject();
        } catch (final InterruptedIOException ex) {
            return;
        }

        final PaginationFilterList list = new PaginationFilterList();
        list.add(filter);

        try {
            serviceCOMArchive.getArchiveStub().query(Boolean.TRUE, objType, archiveQueryList, list, adapter);
        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
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

        ActionDefinitionDetails actionDefinition = new ActionDefinitionDetails();
//        actionDefinition.setName(new Identifier("Take_Picture"));
        actionDefinition.setDescription("The action takes a picture and stores it in a the 'picture' parameter.");
        actionDefinition.setProgressStepCount(new UShort(1));

        ArgumentDefinitionDetails argument = new ArgumentDefinitionDetails();
        argument.setRawType(Duration.DURATION_TYPE_SHORT_FORM.byteValue());

        ArgumentDefinitionDetailsList arguments = new ArgumentDefinitionDetailsList();
        arguments.add(argument);

        actionDefinition.setArguments(arguments);
        actionDefinition.setArgumentIds(null);

        ActionDefinitionDetails actionDefinition1 = new ActionDefinitionDetails();
//        actionDefinition1.setName(new Identifier("Take_Picture"));
        actionDefinition1.setDescription("The action takes a picture and stores it in a the 'picture' parameter.");
        actionDefinition1.setProgressStepCount(new UShort(1));

        ArgumentDefinitionDetails argument1 = new ArgumentDefinitionDetails();
        argument1.setRawType(Duration.DURATION_TYPE_SHORT_FORM.byteValue());

        ArgumentDefinitionDetailsList arguments1 = new ArgumentDefinitionDetailsList();
        arguments1.add(argument1);

        actionDefinition1.setArguments(arguments1);
        actionDefinition1.setArgumentIds(null);

        MOWindow genActionDefinition = new MOWindow(actionDefinition, true);
        try {
            actionDefinition = (ActionDefinitionDetails) genActionDefinition.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }

        ActionDefinitionDetailsList actionDefinitionList = new ActionDefinitionDetailsList();
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

    private void jButtonStoreGroupsActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStoreGroupsActionPerformed

        // Object Type
        final ObjectType objType = new ObjectType(new UShort(4), new UShort(8), new UOctet((short) 1), new UShort(1));

        // Domain
        final IdentifierList domain = serviceCOMArchive.getConnectionDetails().getDomain();

        // Archive details
        ArchiveDetailsList archiveDetailsList = HelperArchive.generateArchiveDetailsList(null, null, serviceCOMArchive.getConnectionDetails());
        final MOWindow genArchiveDetailsList = new MOWindow(archiveDetailsList, true);
        try {
            archiveDetailsList = (ArchiveDetailsList) genArchiveDetailsList.getObject();
        } catch (final InterruptedIOException ex) {
            return;
        }

        final GroupDetails group = new GroupDetails();
        final GroupDetailsList groupList = new GroupDetailsList();

//        group.setName(new Identifier("Group1"));
        group.setDescription("A group of the 3 first Parameter Definitions.");
        final ObjectType objTypeParameterDef = new ObjectType(new UShort(4), new UShort(2), new UOctet((short) 1), new UShort(1));
        group.setObjectType(objTypeParameterDef);
        group.setDomain(domain);
        final LongList objIds = new LongList();
        objIds.add(1L);
        objIds.add(2L);
        objIds.add(3L);

        group.setInstanceIds(objIds);

        groupList.add(group);

        // Actually you have to use the Action service to store the definiton
        try {
            serviceCOMArchive.getArchiveStub().store(false, objType, domain, archiveDetailsList, groupList);
        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButtonStoreGroupsActionPerformed

    private void TBoxStoreActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TBoxStoreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TBoxStoreActionPerformed

    private void jButtonDeleteAllActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteAllActionPerformed
        // Careful! Deleting all the COM Objects on a provider might cause
        // erratic behaviour. The provider might crash because there are
        // important configurations within.

        final ArchiveConsumerAdapter adapter = new ArchiveConsumerAdapter("Deleted objects");

        final UShort shorty = new UShort(0);
        final UOctet octety = new UOctet((short) 0);
        final ObjectType objType = new ObjectType(shorty, shorty, octety, shorty);

        final ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        final ArchiveQuery archiveQuery = new ArchiveQuery();

        archiveQuery.setDomain(null);
        archiveQuery.setNetwork(null);
        archiveQuery.setProvider(null);
        archiveQuery.setRelated(0L);
        archiveQuery.setSource(null);
        archiveQuery.setStartTime(null);
        archiveQuery.setEndTime(null);
        archiveQuery.setSortFieldName(null);
        archiveQuery.setSortFieldName(null);

        archiveQueryList.add(archiveQuery);

        try {
            serviceCOMArchive.getArchiveStub().query(Boolean.TRUE, objType, archiveQueryList, null, adapter);
            adapter.deleteAllInTable();  // Deletes all the objects in the table
        } catch (final MALInteractionException | MALException ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButtonDeleteAllActionPerformed

    public static ArchiveQuery generateArchiveQuery() {
        // ArchiveDetails
        final ArchiveQuery archiveQuery = new ArchiveQuery();
        archiveQuery.setDomain(null);
        archiveQuery.setNetwork(null);
        archiveQuery.setProvider(null);
        archiveQuery.setRelated(0L);
        archiveQuery.setSource(null);
        archiveQuery.setStartTime(null);
        archiveQuery.setEndTime(null);
        archiveQuery.setSortOrder(null);
        archiveQuery.setSortFieldName(null);

        return archiveQuery;
    }

    public static CompositeFilter generateCompositeFilter() {
        final CompositeFilter compositeFilter = new CompositeFilter();
        compositeFilter.setFieldName("name");
        compositeFilter.setType(ExpressionOperator.fromNumericValue(ExpressionOperator.EQUAL_NUM_VALUE));
        compositeFilter.setFieldValue(new Identifier("AggregationUpdate"));

        return compositeFilter;
    }


    private void open_ArchiveSyncActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_open_ArchiveSyncActionPerformed

        if (serviceTabs != null) {
            if (services.getCOMServices().getArchiveSyncService() != null) {
                final ArchiveSyncConsumerManagerPanel panel = new ArchiveSyncConsumerManagerPanel(
                        services.getCOMServices().getArchiveService(),
                        services.getCOMServices().getArchiveSyncService()
                );
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
