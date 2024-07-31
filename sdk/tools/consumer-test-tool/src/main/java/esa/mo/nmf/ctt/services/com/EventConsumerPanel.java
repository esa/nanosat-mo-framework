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
import esa.mo.com.impl.consumer.EventConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.EventCOMObject;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.nmf.ctt.utils.COMObjectWindow;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.com.impl.util.EventReceivedListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.UShort;

/**
 *
 * @author Cesar Coelho
 */
public class EventConsumerPanel extends javax.swing.JPanel {

    private EventConsumerServiceImpl serviceCOMEvent;
    private List<ArchivePersistenceObject> comObjects;
    private final DefaultTableModel eventTableData;

    /**
     * Creates new form ArchiveConsumerPanel
     *
     * @param eventService
     * @param archiveService
     */
    public EventConsumerPanel(EventConsumerServiceImpl eventService, final ArchiveConsumerServiceImpl archiveService) {
        initComponents();
        serviceCOMEvent = eventService;
        comObjects = new ArrayList<>();

        String[] parameterTableCol = new String[]{"Timestamp", "Source URI", "From Service", "Event name", "Domain",
                                                  "ObjId", "Source objType", "Related: ObjDetails",
                                                  "Source: ObjDetails", "Number of events"};

        eventTableData = new javax.swing.table.DefaultTableModel(new Object[][]{}, parameterTableCol) {
            Class[] types = new Class[]{java.lang.String.class, java.lang.String.class, java.lang.String.class,
                                        java.lang.String.class, java.lang.String.class, java.lang.Long.class,
                                        java.lang.String.class, java.lang.String.class, java.lang.String.class,
                                        java.lang.Integer.class};

            @Override               //all cells false
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };

        eventTable.setModel(eventTableData);

        eventTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // Get from the list of objects the one we want and display
                    ArchivePersistenceObject comObject = comObjects.get(eventTable.getSelectedRow());
                    try {
                        COMObjectWindow comObjectWindow = new COMObjectWindow(comObject, false, archiveService
                            .getArchiveStub());
                    } catch (IOException ex) {
                        Logger.getLogger(EventConsumerPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(eventTable.getModel());
        eventTable.setRowSorter(sorter);
    }

    public void init() {
        // Subscribe to all Events
        final Subscription subscription = ConnectionConsumer.subscriptionWildcard();
        serviceCOMEvent.addEventReceivedListener(subscription, new EventReceivedAdapter());
    }

    public class EventReceivedAdapter extends EventReceivedListener {

        @Override
        public void onDataReceived(EventCOMObject eventCOMObject) {

            int n_events = 0;
            Element object = null;

            if (eventCOMObject.getBody() != null) {
                object = (Element) HelperAttributes.javaType2Attribute(eventCOMObject.getBody());
            }

            ObjectType objType2 = new ObjectType(eventCOMObject.getObjType().getArea(), eventCOMObject.getObjType()
                .getService(), eventCOMObject.getObjType().getVersion(), new UShort(0));
            String eKey2 = HelperCOM.objType2string(objType2);
            String eKey4 = (eventCOMObject.getSource() != null) ? HelperCOM.objType2string(eventCOMObject.getSource()
                .getType()) : "";
            ObjectDetails objectDetails = new ObjectDetails(eventCOMObject.getRelated(), eventCOMObject.getSource());

            String objDetailsRelated = (eventCOMObject.getRelated() != null) ? eventCOMObject.getRelated().toString() :
                "null";
            String objDetailsSource = (eventCOMObject.getSource() != null) ? eventCOMObject.getSource().getKey()
                .getInstId().toString() : "null";

            String time = HelperTime.time2readableString(eventCOMObject.getTimestamp());
            String domainName = HelperMisc.domain2domainId(eventCOMObject.getDomain());
            String eventName = HelperCOM.objType2COMObject(eventCOMObject.getObjType()).getObjectName().toString();

            eventTableData.addRow(new Object[]{time, eventCOMObject.getSourceURI().toString(), eKey2, eventName,
                                               domainName, eventCOMObject.getObjId(), eKey4, objDetailsRelated,
                                               objDetailsSource, n_events});

            ArchiveDetails archiveDetails = new ArchiveDetails(eventCOMObject.getObjId(), objectDetails, eventCOMObject
                .getNetworkZone(), HelperTime.timeToFineTime(eventCOMObject.getTimestamp()), eventCOMObject
                    .getSourceURI());

            ArchivePersistenceObject comObject = new ArchivePersistenceObject(eventCOMObject.getObjType(),
                eventCOMObject.getDomain(), eventCOMObject.getObjId(), archiveDetails, object);

            // Add to the table
            comObjects.add(comObject);
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
        jScrollPane2 = new javax.swing.JScrollPane();
        eventTable = new javax.swing.JTable();

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Event Service");
        jLabel6.setToolTipText("");

        jScrollPane2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane2.setHorizontalScrollBar(null);
        jScrollPane2.setPreferredSize(new java.awt.Dimension(796, 380));
        jScrollPane2.setRequestFocusEnabled(false);

        eventTable.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{{null, null, null, null, null, null,
                                                                                    null, null, null}, {null, null,
                                                                                                        null, null,
                                                                                                        null, null,
                                                                                                        null, null,
                                                                                                        null}},
            new String[]{"HDR - Timestamp", "HDR - uriTo", "eKey1 - Event ojb number", "eKey2 - Service",
                         "eKey3 - ObjId", "eKey4 - Source ObjType", "Related (ObjDetails)", "Source (ObjDetails)",
                         "N of event objs"}) {
            Class[] types = new Class[]{java.lang.String.class, java.lang.String.class, java.lang.String.class,
                                        java.lang.String.class, java.lang.Long.class, java.lang.String.class,
                                        java.lang.String.class, java.lang.String.class, java.lang.Integer.class};

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        });
        eventTable.setAlignmentX(0.0F);
        eventTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        eventTable.setAutoscrolls(false);
        eventTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        eventTable.setMaximumSize(null);
        eventTable.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                eventTableComponentAdded(evt);
            }
        });
        jScrollPane2.setViewportView(eventTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
            jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 824,
            Short.MAX_VALUE).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 824, Short.MAX_VALUE)));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
            .createSequentialGroup().addContainerGap().addComponent(jLabel6).addContainerGap(397, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addGap(50, 50, 50)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))));
    }// </editor-fold>//GEN-END:initComponents

    private void eventTableComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_eventTableComponentAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_eventTableComponentAdded

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable eventTable;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
