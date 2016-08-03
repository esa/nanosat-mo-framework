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
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.ArchiveCOMObjectsOutput;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.mc.impl.consumer.ConversionConsumerServiceImpl;
import esa.mo.helpertools.helpers.HelperMisc;
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
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.archive.structures.CompositeFilter;
import org.ccsds.moims.mo.com.archive.structures.CompositeFilterList;
import org.ccsds.moims.mo.com.archive.structures.CompositeFilterSet;
import org.ccsds.moims.mo.com.archive.structures.CompositeFilterSetList;
import org.ccsds.moims.mo.com.archive.structures.ExpressionOperator;
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
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationCategory;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetails;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetailsList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSet;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSetList;
import org.ccsds.moims.mo.mc.check.structures.CheckState;
import org.ccsds.moims.mo.mc.check.structures.CheckStateList;
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
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.Severity;

/**
 *
 * @author Cesar Coelho
 */
public class ArchiveConsumerManagerPanel extends javax.swing.JPanel {

    private final ArchiveConsumerServiceImpl serviceCOMArchive;
    private final ConversionConsumerServiceImpl conversionService = new ConversionConsumerServiceImpl();

    public final transient ObjectType OBJTYPE_AGGS_AGGREGATIONDEFINITION = HelperCOM.generateCOMObjectType(4, 6, 1, 1);

    /**
     * Creates new form ArchiveConsumerPanel
     *
     * @param archiveService
     */
    public ArchiveConsumerManagerPanel(ArchiveConsumerServiceImpl archiveService) {
        initComponents();
        serviceCOMArchive = archiveService;
    }

    public static AggregationDefinitionDetails generateAggregationDefinition(String name) {
        // AgregationDefinition
        AggregationDefinitionDetails aggDef = new AggregationDefinitionDetails();
        aggDef.setCategory(AggregationCategory.GENERAL);
        aggDef.setDescription("This is a description");
        aggDef.setFilterEnabled(Boolean.FALSE);
        aggDef.setFilteredTimeout(new Duration(0));
        aggDef.setGenerationEnabled(Boolean.FALSE);
        aggDef.setName(new Identifier(name));

        AggregationParameterSetList aggParamSetList = new AggregationParameterSetList();
        AggregationParameterSet aggParamSet = new AggregationParameterSet();
        aggParamSet.setSampleInterval(new Duration(1));
        LongList objIdParams = new LongList();
        objIdParams.add(new Long(1));
        aggParamSet.setParameters(objIdParams);
        aggParamSetList.add(aggParamSet);
        aggDef.setParameterSets(aggParamSetList);
        aggDef.setUpdateInterval(new Duration(0));

        return aggDef;
    }

    
    
    private LineConversionDetails generateLineConversionDetails(){
      LineConversionDetails convDetails = new LineConversionDetails();

      convDetails.setExtrapolate(true);

      PairList points = new PairList();
      
      Pair pair0 = new Pair();
      pair0.setFirst(new Union(1));
      pair0.setSecond(new Union(33.8));
      points.add(pair0);

      Pair pair1 = new Pair();
      pair1.setFirst(new Union(100));
      pair1.setSecond(new Union(212));
      points.add(pair1);
      
      convDetails.setPoints(points);
      
      return convDetails;
  }

  private PolyConversionDetails generatePolyConversionDetails(){
      PolyConversionDetails convDetails = new PolyConversionDetails();
      
      PairList points = new PairList();
      
      Pair pair0 = new Pair();
      pair0.setFirst(new Union(0));
      pair0.setSecond(new Union(32));
      points.add(pair0);

      Pair pair1 = new Pair();
      pair1.setFirst(new Union(1));
      pair1.setSecond(new Union(1.8));
      points.add(pair1);
      
      convDetails.setPoints(points);
      
      return convDetails;
  }
  
    private DiscreteConversionDetails generateDiscreteConversionDetails(){
      
      PairList mapping = new PairList();
      
      Pair pair0 = new Pair();
      pair0.setFirst(new Union(0));
      pair0.setSecond(new Union("Mode 0"));
      mapping.add(pair0);

      Pair pair1 = new Pair();
      pair1.setFirst(new Union(1));
      pair1.setSecond(new Union("Mode 1"));
      mapping.add(pair1);
      
      Pair pair2 = new Pair();
      pair2.setFirst(new Union(2));
      pair2.setSecond(new Union("Mode 2"));
      mapping.add(pair2);

      Pair pair3= new Pair();
      pair3.setFirst(new Union(3));
      pair3.setSecond(new Union("Mode 3"));
      mapping.add(pair3);
      
      return new DiscreteConversionDetails(mapping);
  }

  
  private RangeConversionDetails generateRangeConversionDetails(){
      RangeConversionDetails convDetails = new RangeConversionDetails();
      
      PairList points = new PairList();
      
      Pair pair0 = new Pair();
      pair0.setFirst(new Union(0));
      pair0.setSecond(new Union("Between 0-100"));
      points.add(pair0);

      Pair pair1 = new Pair();
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
        private Semaphore isOver = new Semaphore(0);
        private int n_objs_counter = 0;
        private final javax.swing.JPanel pnlTab = new javax.swing.JPanel();
        private final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
        private final Date date = new Date(System.currentTimeMillis());
        private final String functionName;

            
        ArchiveConsumerAdapter (String stringLabel){
            pnlTab.setOpaque(false);
            functionName = stringLabel;
/*                    
            JLabel label = new JLabel(functionName + " (" + dateFormat.format(date) + ")" + " (" + n_objs_counter + ")");
            JLabel closeLabel = new JLabel("x");
            closeLabel.addMouseListener(new CloseMouseHandler(pnlTab));
            closeLabel.setFont(closeLabel.getFont().deriveFont(closeLabel.getFont().getStyle() | Font.BOLD));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1;
            pnlTab.add(label, gbc);

            gbc.gridx++;
            gbc.weightx = 0;
            pnlTab.add(closeLabel, gbc);
*/

            this.refreshTabCounter();

            tabs.addTab("", archiveTablePanel);
                        
            tabs.setTabComponentAt(tabs.getTabCount()-1, pnlTab);
            tabs.setSelectedIndex(tabs.getTabCount()-1);

        }
        
        private void refreshTabCounter(){
            JLabel label = new JLabel(functionName + " (" + dateFormat.format(date) + ")" + " (" + n_objs_counter + ")");
            JLabel closeLabel = new JLabel("x");
            closeLabel.addMouseListener(new CloseMouseHandler(pnlTab));
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
            
            if (index == -1){
                return;
            }
                  
            tabs.setTabComponentAt(index, pnlTab);
            pnlTab.repaint();  // not working
            tabs.repaint();    // not working
            
        }
        
        
        
        public int getSelectedIndex(){
            return archiveTablePanel.getSelectedRow();
        }
        
        protected void setObjType(ObjectType objType){
            this.objType = objType;
        }

        protected void setDomain(IdentifierList domain){
            this.domain = domain;
        }

        protected ObjectType getObjType(){
            return this.objType;
        }

        protected IdentifierList getDomain(){
            return this.domain;
        }

        @Override
        public void retrieveAckReceived(MALMessageHeader msgHeader, Map qosProperties) {

            // Later on, do something...
        
        }
        
        @Override
        public void retrieveResponseReceived(MALMessageHeader msgHeader, ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {
            ArchiveCOMObjectsOutput archiveObjectOutput = new  ArchiveCOMObjectsOutput(domain, objType, objDetails, objBodies);
            archiveTablePanel.addEntries(archiveObjectOutput);
            n_objs_counter = n_objs_counter + objDetails.size();
            refreshTabCounter();
        }

        @Override
        public void countResponseReceived(MALMessageHeader msgHeader, LongList _LongList0, Map qosProperties) {
            JOptionPane.showMessageDialog(null, _LongList0.toString() , "The count operation returned the following data!", JOptionPane.PLAIN_MESSAGE);
        }
        
        @Override
        public void queryResponseReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain, ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {
            
            ArchiveCOMObjectsOutput archiveObjectOutput = new  ArchiveCOMObjectsOutput(domain, objType, objDetails, objBodies);
            archiveTablePanel.addEntries(archiveObjectOutput);
            n_objs_counter = n_objs_counter + objDetails.size();
            refreshTabCounter();

            isOver.release();
        }

        @Override
        public void queryUpdateReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain, ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {
            ArchiveCOMObjectsOutput archiveObjectOutput = new  ArchiveCOMObjectsOutput(domain, objType, objDetails, objBodies);
            archiveTablePanel.addEntries(archiveObjectOutput);
            n_objs_counter = n_objs_counter + objDetails.size();
            refreshTabCounter();
        }
        
        @Override
        public void queryAckErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, "queryAckErrorReceived", error);
        }
    
        protected void deleteAllInTable(){
            try {
                isOver.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            List<ArchivePersistenceObject> comObjects = archiveTablePanel.getAllCOMObjects();
            
            for (ArchivePersistenceObject comObject : comObjects){
                LongList objIds = new LongList();
                objIds.add(comObject.getArchiveDetails().getInstId());
                try {
                    serviceCOMArchive.getArchiveStub().delete(comObject.getObjectType(), comObject.getDomain(), objIds);
                } catch (MALInteractionException ex) {
                    Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MALException ex) {
                    Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }
        
    }

    public class CloseMouseHandler implements MouseListener {

        private javax.swing.JPanel panel;
        
        CloseMouseHandler(javax.swing.JPanel panel){
            this.panel = panel;
        }
        
        @Override
        public void mouseClicked(MouseEvent evt) {
            for (int i = 0; i < tabs.getTabCount(); i++){
                Component component = tabs.getTabComponentAt(i);

                if (component == panel){
                    tabs.remove(i);
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
        test_button = new javax.swing.JButton();

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Archive Manager");
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

        tabs.setToolTipText("");
        tabs.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tabs.setMaximumSize(new java.awt.Dimension(800, 600));
        tabs.setMinimumSize(new java.awt.Dimension(800, 600));
        tabs.setPreferredSize(new java.awt.Dimension(800, 600));
        tabs.setRequestFocusEnabled(false);
        tabs.addTab("Home", homeTab);

        jButtonStoreConversions.setText("Quick Store (Conversions)");
        jButtonStoreConversions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStoreConversionsActionPerformed(evt);
            }
        });

        jButtonStoreActions.setText("Quick Store (Actions)");
        jButtonStoreActions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStoreActionsActionPerformed(evt);
            }
        });

        jButtonStoreGroups.setText("Quick Store (Groups)");
        jButtonStoreGroups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStoreGroupsActionPerformed(evt);
            }
        });

        TBoxStore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TBoxStoreActionPerformed(evt);
            }
        });

        jButtonDeleteAll.setText("Delete All");
        jButtonDeleteAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteAllActionPerformed(evt);
            }
        });

        test_button.setText("Test Button");
        test_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                test_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
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
                    .addComponent(jButtonDeleteAll, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                    .addComponent(jButtonUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonCount))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonQuery, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonStoreActions, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonStoreGroups, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonStoreAggregation))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonStoreConversions)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(test_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TBoxStore, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(tabs, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1005, Short.MAX_VALUE))
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
                    .addComponent(jButtonStoreActions, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonStoreConversions)
                    .addComponent(test_button))
                .addContainerGap(438, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(129, 129, 129)
                    .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 426, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonStoreAggregationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStoreAggregationActionPerformed

       ArchiveDetailsList archiveDetailsList = new ArchiveDetailsList();
        archiveDetailsList.add(serviceCOMArchive.generateArchiveDetails(new Long(0)));
        ObjectType objType = this.OBJTYPE_AGGS_AGGREGATIONDEFINITION;
        AggregationDefinitionDetailsList objList = new AggregationDefinitionDetailsList();
        objList.add(ArchiveConsumerManagerPanel.generateAggregationDefinition("AggregationStore"));

        try {
          LongList outObjId = serviceCOMArchive.getArchiveStub().store(Boolean.TRUE, objType, serviceCOMArchive.getConnectionDetails().getDomain(), archiveDetailsList, objList);
          Long received = outObjId.get(0);
          TBoxStore.setText(received.toString());
        } catch (MALInteractionException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButtonStoreAggregationActionPerformed

    private void jButtonGetAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGetAllActionPerformed
        
        ArchiveConsumerAdapter adapter = new ArchiveConsumerAdapter("Get All");
        
        UShort shorty = new UShort(0);
        UOctet octety = new UOctet((short) 0);
        ObjectType objType = new ObjectType(shorty, shorty, octety, shorty);

        ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        ArchiveQuery archiveQuery = new ArchiveQuery();
        
        archiveQuery.setDomain(null);
        archiveQuery.setNetwork(null);
        archiveQuery.setProvider(null);
        archiveQuery.setRelated(new Long(0));
        archiveQuery.setSource(null);
        archiveQuery.setStartTime(null);
        archiveQuery.setEndTime(null);
        archiveQuery.setSortFieldName(null);
        archiveQuery.setSortFieldName(null);
        
        archiveQueryList.add(archiveQuery);
        
        try {
            serviceCOMArchive.getArchiveStub().query(Boolean.TRUE, objType, archiveQueryList, null, adapter);
        } catch (MALInteractionException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_jButtonGetAllActionPerformed


    private void jButtonQueryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonQueryActionPerformed

        ArchiveConsumerAdapter adapter = new ArchiveConsumerAdapter("Query...");

        // Object Type
        ObjectType objType = this.OBJTYPE_AGGS_AGGREGATIONDEFINITION;
        MOWindow genObjType = new MOWindow(objType, true);
        try {
            objType = (ObjectType) genObjType.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }

        // Archive Query
        ArchiveQuery archiveQuery = ArchiveConsumerServiceImpl.generateArchiveQuery();
        ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        archiveQueryList.add(archiveQuery);
        MOWindow genArchiveQueryList = new MOWindow(archiveQueryList, true);
        try {
            archiveQueryList = (ArchiveQueryList) genArchiveQueryList.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }

        // Composite Filter
        CompositeFilterSetList compositeFilters = new CompositeFilterSetList();
        CompositeFilterSet compositeFilterSet = new CompositeFilterSet();
        CompositeFilterList compositeFilterList = new CompositeFilterList();
        compositeFilterList.add(ArchiveConsumerServiceImpl.generateCompositeFilter());
        compositeFilterSet.setFilters(compositeFilterList);
        compositeFilters.add(compositeFilterSet);
        MOWindow genFilter = new MOWindow(compositeFilters, true);
        try {
            compositeFilters = (CompositeFilterSetList) genFilter.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }
        
        try {
            serviceCOMArchive.getArchiveStub().query(Boolean.TRUE, objType, archiveQueryList, compositeFilters, adapter);
        } catch (MALInteractionException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButtonQueryActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        
        ArchivePersistenceObject comObject = ((ArchiveTablePanel) tabs.getSelectedComponent()).getSelectedCOMObject();
        
        LongList objIds = new LongList();
        objIds.add(comObject.getObjectId());
        
        try {
            serviceCOMArchive.getArchiveStub().delete(comObject.getObjectType(), comObject.getDomain(), objIds);
        } catch (MALInteractionException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ((ArchiveTablePanel) tabs.getSelectedComponent()).removeSelectedEntry();
        
    }//GEN-LAST:event_jButtonDeleteActionPerformed

    private void jButtonRetrieveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRetrieveActionPerformed
        
        // Object Type
        ObjectType objType = this.OBJTYPE_AGGS_AGGREGATIONDEFINITION;
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
        } catch (MALInteractionException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_jButtonRetrieveActionPerformed

    @SuppressWarnings("unchecked")
    private void jButtonUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateActionPerformed

        ArchivePersistenceObject comObject = ((ArchiveTablePanel) tabs.getSelectedComponent()).getSelectedCOMObject();
        MOWindow objBodyWindow = new MOWindow(comObject.getObject(), true);
        ArchiveDetailsList archiveDetailsList = new ArchiveDetailsList();
        archiveDetailsList.add(comObject.getArchiveDetails());
        ElementList finalObject;
        
        try {
            finalObject = HelperMisc.element2elementList((Element) objBodyWindow.getObject());
            finalObject.add(objBodyWindow.getObject());

            try {
                serviceCOMArchive.getArchiveStub().update(comObject.getObjectType(), comObject.getDomain(), archiveDetailsList, finalObject);
        } catch (MALInteractionException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        } catch (Exception ex) {
            Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_jButtonUpdateActionPerformed

    private void jButtonCountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCountActionPerformed

        // Object Type
        ObjectType objType = this.OBJTYPE_AGGS_AGGREGATIONDEFINITION;
        MOWindow genObjType = new MOWindow(objType, true);
        try {
            objType = (ObjectType) genObjType.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }

        // Archive Query
        ArchiveQuery archiveQuery = ArchiveConsumerServiceImpl.generateArchiveQuery();
        ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        archiveQueryList.add(archiveQuery);
        MOWindow genArchiveQueryList = new MOWindow(archiveQueryList, true);
        try {
            archiveQueryList = (ArchiveQueryList) genArchiveQueryList.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }
        
        // Composite Filter
        CompositeFilterSetList compositeFilters = new CompositeFilterSetList();
        CompositeFilterSet compositeFilterSet = new CompositeFilterSet();
        CompositeFilterList compositeFilterList = new CompositeFilterList();
        compositeFilterList.add(ArchiveConsumerServiceImpl.generateCompositeFilter());
        compositeFilterSet.setFilters(compositeFilterList);
        compositeFilters.add(compositeFilterSet);
        MOWindow genFilter = new MOWindow(compositeFilters, true);
        try {
            compositeFilters = (CompositeFilterSetList) genFilter.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }

        ArchiveConsumerAdapter adapter = new ArchiveConsumerAdapter("Count...");
        adapter.setObjType(objType);

        try {
            serviceCOMArchive.getArchiveStub().count(objType, archiveQueryList, compositeFilters, adapter);
        } catch (MALInteractionException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        JDialog frame = new JDialog();
        int n = JOptionPane.showConfirmDialog( frame, "Would you like to automatically query and get the objects?", "Query?", JOptionPane.YES_NO_OPTION);

        if (n == JOptionPane.YES_OPTION){
            try {
                serviceCOMArchive.getArchiveStub().query(true, objType, archiveQueryList, compositeFilters, adapter);
        } catch (MALInteractionException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
        
    }//GEN-LAST:event_jButtonCountActionPerformed

    private void jButtonStoreConversionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStoreConversionsActionPerformed
        LongList outObjId;
        ArchiveDetailsList archiveDetailsList;
        ObjectType objType;
        
        archiveDetailsList = new ArchiveDetailsList();
        archiveDetailsList.add(serviceCOMArchive.generateArchiveDetails(new Long (0)));
        objType = ConversionHelper.DISCRETECONVERSION_OBJECT_TYPE;
        DiscreteConversionDetailsList objList1 = new DiscreteConversionDetailsList();
        objList1.add(this.generateDiscreteConversionDetails());

        try {
          outObjId = serviceCOMArchive.getArchiveStub().store(Boolean.TRUE, objType, serviceCOMArchive.getConnectionDetails().getDomain(), archiveDetailsList, objList1);
        } catch (MALInteractionException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        archiveDetailsList = new ArchiveDetailsList();
        archiveDetailsList.add(serviceCOMArchive.generateArchiveDetails(new Long (0)));
        objType = ConversionHelper.LINECONVERSION_OBJECT_TYPE;
        LineConversionDetailsList objList2 = new LineConversionDetailsList();
        objList2.add(this.generateLineConversionDetails());

        try {
          outObjId = serviceCOMArchive.getArchiveStub().store(Boolean.TRUE, objType, serviceCOMArchive.getConnectionDetails().getDomain(), archiveDetailsList, objList2);
         } catch (MALInteractionException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        
        archiveDetailsList = new ArchiveDetailsList();
        archiveDetailsList.add(serviceCOMArchive.generateArchiveDetails(new Long (0)));
        objType = ConversionHelper.POLYCONVERSION_OBJECT_TYPE;
        PolyConversionDetailsList objList3 = new PolyConversionDetailsList();
        objList3.add(this.generatePolyConversionDetails());

        try {
          outObjId = serviceCOMArchive.getArchiveStub().store(Boolean.TRUE, objType, serviceCOMArchive.getConnectionDetails().getDomain(), archiveDetailsList, objList3);
        } catch (MALInteractionException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        archiveDetailsList = new ArchiveDetailsList();
        archiveDetailsList.add(serviceCOMArchive.generateArchiveDetails(new Long (0)));
        objType = ConversionHelper.RANGECONVERSION_OBJECT_TYPE;
        RangeConversionDetailsList objList4 = new RangeConversionDetailsList();
        objList4.add(this.generateRangeConversionDetails());

        try {
          outObjId = serviceCOMArchive.getArchiveStub().store(Boolean.TRUE, objType, serviceCOMArchive.getConnectionDetails().getDomain(), archiveDetailsList, objList4);
        } catch (MALInteractionException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_jButtonStoreConversionsActionPerformed

    private void jButtonStoreActionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStoreActionsActionPerformed

        // Object Type
        ObjectType objType = new ObjectType(new UShort (4), new UShort(1), new UOctet ((short) 1), new UShort(1));

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
        actionDefinition.setName(new Identifier("Take_Picture"));
        actionDefinition.setDescription("The action takes a picture and stores it in a the 'picture' parameter.");
        actionDefinition.setSeverity(Severity.INFORMATIONAL);
        actionDefinition.setProgressStepCount(new UShort(1));

        ArgumentDefinitionDetails argument = new ArgumentDefinitionDetails();
        argument.setRawType(Duration.DURATION_TYPE_SHORT_FORM.byteValue());

        ArgumentDefinitionDetailsList arguments = new ArgumentDefinitionDetailsList();
        arguments.add(argument);

        actionDefinition.setArguments(arguments);
        actionDefinition.setArgumentIds(null);

        
        
        ActionDefinitionDetails actionDefinition1 = new ActionDefinitionDetails();
        actionDefinition1.setName(new Identifier("Take_Picture"));
        actionDefinition1.setDescription("The action takes a picture and stores it in a the 'picture' parameter.");
        actionDefinition1.setSeverity(Severity.INFORMATIONAL);
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

    }//GEN-LAST:event_jButtonStoreActionsActionPerformed

    private void jButtonStoreGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStoreGroupsActionPerformed

        // Object Type
        ObjectType objType = new ObjectType(new UShort (4), new UShort(8), new UOctet ((short) 1), new UShort(1));

        // Domain
        IdentifierList domain = serviceCOMArchive.getConnectionDetails().getDomain();

        // Archive details
        ArchiveDetailsList archiveDetailsList = HelperArchive.generateArchiveDetailsList(null, null, serviceCOMArchive.getConnectionDetails());
        MOWindow genArchiveDetailsList = new MOWindow(archiveDetailsList, true);
        try {
            archiveDetailsList = (ArchiveDetailsList) genArchiveDetailsList.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }

        GroupDetails group = new GroupDetails();
        GroupDetailsList groupList = new GroupDetailsList();
        
        group.setName(new Identifier("Group1"));
        group.setDescription("A group of the 3 first Parameter Definitions.");
        ObjectType objTypeParameterDef = new ObjectType(new UShort (4), new UShort(2), new UOctet ((short) 1), new UShort(1));
        group.setObjectType(objTypeParameterDef);
        group.setDomain(domain);
        LongList objIds = new LongList();
        objIds.add(new Long(1));
        objIds.add(new Long(2));
        objIds.add(new Long(3));
        
        group.setInstanceIds(objIds);
        
        
        groupList.add(group);
        
        // Actually you have to use the Action service to store the definiton

        try {
            serviceCOMArchive.getArchiveStub().store(false, objType, domain, archiveDetailsList, groupList);
        } catch (MALInteractionException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButtonStoreGroupsActionPerformed

    private void TBoxStoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TBoxStoreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TBoxStoreActionPerformed

    private void jButtonDeleteAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteAllActionPerformed

        ArchiveConsumerAdapter adapter = new ArchiveConsumerAdapter("Deleted objects");
        
        UShort shorty = new UShort(0);
        UOctet octety = new UOctet((short) 0);
        ObjectType objType = new ObjectType(shorty, shorty, octety, shorty);

        ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        ArchiveQuery archiveQuery = new ArchiveQuery();
        
        archiveQuery.setDomain(null);
        archiveQuery.setNetwork(null);
        archiveQuery.setProvider(null);
        archiveQuery.setRelated(new Long(0));
        archiveQuery.setSource(null);
        archiveQuery.setStartTime(null);
        archiveQuery.setEndTime(null);
        archiveQuery.setSortFieldName(null);
        archiveQuery.setSortFieldName(null);
        
        archiveQueryList.add(archiveQuery);
        
        try {
            serviceCOMArchive.getArchiveStub().query(Boolean.TRUE, objType, archiveQueryList, null, adapter);
            adapter.deleteAllInTable();  // Deletes all the objects in the table
        } catch (MALInteractionException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButtonDeleteAllActionPerformed

    
    public static ArchiveQuery generateArchiveQuery() {
        // ArchiveDetails
        ArchiveQuery archiveQuery = new ArchiveQuery();
        archiveQuery.setDomain(null);
        archiveQuery.setNetwork(null);
        archiveQuery.setProvider(null);
        archiveQuery.setRelated(new Long(0));
        archiveQuery.setSource(null);
        archiveQuery.setStartTime(null);
        archiveQuery.setEndTime(null);
        archiveQuery.setSortOrder(null);
        archiveQuery.setSortFieldName(null);

        return archiveQuery;
    }

    public static CompositeFilter generateCompositeFilter() {
        CompositeFilter compositeFilter = new CompositeFilter();
        compositeFilter.setFieldName("name");
        compositeFilter.setType(ExpressionOperator.fromNumericValue(ExpressionOperator.EQUAL_NUM_VALUE));
        compositeFilter.setFieldValue(new Identifier("AggregationUpdate"));

        return compositeFilter;
    }
    
    
    private void test_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_test_buttonActionPerformed

        // Object Type
        ObjectType objType = new ObjectType(new UShort (4), new UShort(8), new UOctet ((short) 1), new UShort(1));

        // Domain
        IdentifierList domain = serviceCOMArchive.getConnectionDetails().getDomain();

        // Archive details
        ArchiveDetailsList archiveDetailsList = HelperArchive.generateArchiveDetailsList(null, null, serviceCOMArchive.getConnectionDetails());

/*        
        ParameterDefinitionDetails objectBody = new ParameterDefinitionDetails();
        objectBody.setName(new Identifier("ffdsv"));
        objectBody.setDescription("dfvfdv");
        objectBody.setRawType((byte) 4343);
        objectBody.setRawUnit("fggfd");
        objectBody.setGenerationEnabled(true);
        objectBody.setUpdateInterval(new Duration(67));
        objectBody.setValidityExpression(null);
        objectBody.setConversion(new ParameterConversion());

        objectBody.getConversion().setConvertedType((byte) 3);
        objectBody.getConversion().setConvertedUnit("fdfd");
        objectBody.getConversion().setConversionConditions(new ConditionalReferenceList());
        
        ParameterDefinitionDetailsList objectBodyList = new ParameterDefinitionDetailsList();
        objectBodyList.add(objectBody);
*/
//        CheckState objectBody = new CheckState();
        CheckStateList objectBodyList = new CheckStateList();
        objectBodyList.add(CheckState.DISABLED);
        
        
        try {
            serviceCOMArchive.getArchiveStub().store(false, objType, domain, archiveDetailsList, objectBodyList);
        } catch (MALInteractionException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        ArchiveConsumerAdapter adapter = new ArchiveConsumerAdapter("Get All");
        
 
        ArchiveQueryList archiveQueryList = new ArchiveQueryList();
        ArchiveQuery archiveQuery = new ArchiveQuery();
        
        archiveQuery.setDomain(domain);
        archiveQuery.setNetwork(null);
        archiveQuery.setProvider(null);
        archiveQuery.setRelated(new Long(0));
        archiveQuery.setSource(null);
        archiveQuery.setStartTime(null);
        archiveQuery.setEndTime(null);
        archiveQuery.setSortOrder(false);
        archiveQuery.setSortFieldName(null);
        archiveQueryList.add(archiveQuery);

        
        CompositeFilterList compositeFilterList = new CompositeFilterList();
        CompositeFilter compositeFilter = new CompositeFilter();

        compositeFilter.setFieldName("");
        compositeFilter.setType(ExpressionOperator.GREATER);
        compositeFilter.setFieldValue(new UInteger( 0 ));
        
        compositeFilterList.add(compositeFilter);
        CompositeFilterSetList filterSetList = new CompositeFilterSetList();
        CompositeFilterSet filterSet = new CompositeFilterSet();
        filterSet.setFilters(compositeFilterList);
        filterSetList.add(filterSet);
        
        objType = new ObjectType(new UShort (0), new UShort(8), new UOctet ((short) 1), new UShort(1));
        
        try {
            serviceCOMArchive.getArchiveStub().query(Boolean.TRUE, objType, archiveQueryList, filterSetList, adapter);
        } catch (MALInteractionException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
          Logger.getLogger(ArchiveConsumerManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        



    }//GEN-LAST:event_test_buttonActionPerformed

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
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JButton test_button;
    // End of variables declaration//GEN-END:variables
}
