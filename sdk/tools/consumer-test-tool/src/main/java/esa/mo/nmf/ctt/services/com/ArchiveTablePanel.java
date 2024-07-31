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

import esa.mo.com.impl.util.ArchiveCOMObjectsOutput;
import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.nmf.ctt.utils.COMObjectWindow;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.helpertools.helpers.HelperTime;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.Element;

/**
 *
 * @author Cesar Coelho
 */
public final class ArchiveTablePanel extends javax.swing.JPanel {

    private final DefaultTableModel archiveTableData;
    private List<ArchivePersistenceObject> comObjects;
    private Semaphore semaphore = new Semaphore(1);

    /**
     * Creates new form ObjectsDisplay
     *
     * @param archiveObjectOutput
     * @param archiveService
     */
    public ArchiveTablePanel(ArchiveCOMObjectsOutput archiveObjectOutput,
        final ArchiveConsumerServiceImpl archiveService) {
        initComponents();

        comObjects = new ArrayList<>();

        String[] archiveTableCol = new String[]{"Domain", "Object Type", "Object Instance Identifier", "Timestamp",
                                                "Source", "Related"};

        archiveTableData = new javax.swing.table.DefaultTableModel(new Object[][]{}, archiveTableCol) {
            Class[] types = new Class[]{java.lang.String.class, java.lang.String.class, java.lang.Integer.class,
                                        java.lang.String.class, java.lang.String.class, java.lang.String.class};

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

        archiveTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // Get from the list of objects the one we want and display
                    ArchivePersistenceObject comObject = getSelectedCOMObject();
                    try {
                        COMObjectWindow comObjectWindow = new COMObjectWindow(comObject, false, archiveService
                            .getArchiveStub());
                    } catch (IOException ex) {
                        Logger.getLogger(ArchiveTablePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(archiveTable.getModel());
        archiveTable.setRowSorter(sorter);
        this.addEntries(archiveObjectOutput);
    }

    public int getSelectedRow() {
        return archiveTable.getSelectedRow();
    }

    public ArchivePersistenceObject getSelectedCOMObject() {
        return this.comObjects.get(this.getSelectedRow());
    }

    public List<ArchivePersistenceObject> getAllCOMObjects() {
        return this.comObjects;
    }

    protected void addEntries(ArchiveCOMObjectsOutput archiveObjectOutput) {
        if (archiveObjectOutput == null) {
            return;
        }

        if (archiveObjectOutput.getArchiveDetailsList() == null) {
            return;
        }

        for (int i = 0; i < archiveObjectOutput.getArchiveDetailsList().size(); i++) {
            Element objects = (archiveObjectOutput.getObjectBodies() == null) ? null : (Element) HelperAttributes
                .javaType2Attribute(archiveObjectOutput.getObjectBodies().get(i));
            ArchivePersistenceObject comObject = new ArchivePersistenceObject(archiveObjectOutput.getObjectType(),
                archiveObjectOutput.getDomain(), archiveObjectOutput.getArchiveDetailsList().get(i).getInstId(),
                archiveObjectOutput.getArchiveDetailsList().get(i), objects);

            addEntry(comObject);
        }

    }

    private void addEntry(final ArchivePersistenceObject comObject) {
        String domain = "";
        String objType = "";
        String timestamp = "";
        String source = "null";
        String related = "null";

        if (comObject.getDomain() != null) {
            domain = HelperMisc.domain2domainId(comObject.getDomain());
        }

        if (comObject.getObjectType() != null) {
            ObjectType objTypeType = comObject.getObjectType();
            objType = HelperCOM.objType2string(objTypeType);
        }

        if (comObject.getArchiveDetails().getDetails().getSource() != null) {
            source = HelperCOM.objType2string(comObject.getArchiveDetails().getDetails().getSource().getType());
            source += " (objId: " + comObject.getArchiveDetails().getDetails().getSource().getKey().getInstId()
                .toString() + ")";
        }

        if (comObject.getArchiveDetails().getDetails().getRelated() != null) {
            related = comObject.getArchiveDetails().getDetails().getRelated().toString();
        }

        if (comObject.getArchiveDetails().getTimestamp() != null) {
            timestamp = HelperTime.time2readableString(comObject.getArchiveDetails().getTimestamp());
        }

        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(ArchiveTablePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        archiveTableData.addRow(new Object[]{domain, objType, comObject.getArchiveDetails().getInstId(), timestamp,
                                             source, related});

        comObjects.add(comObject);
        semaphore.release();
    }

    public void removeSelectedEntry() {
        archiveTableData.removeRow(this.getSelectedRow());
    }

    public void removeAllEntries() {
        while (archiveTableData.getRowCount() != 0) {
            archiveTableData.removeRow(archiveTableData.getRowCount() - 1);
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

        jScrollPane3 = new javax.swing.JScrollPane();
        archiveTable = new javax.swing.JTable();

        jScrollPane3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane3.setHorizontalScrollBar(null);
        jScrollPane3.setPreferredSize(new java.awt.Dimension(796, 380));
        jScrollPane3.setRequestFocusEnabled(false);

        archiveTable.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{{null, null, null, null, null,
                                                                                      null}, {null, null, null, null,
                                                                                              null, null}},
            new String[]{"Domain", "Object Type", "Obj Instance Id", "Timestamp", "Related", "Source"}) {
            Class[] types = new Class[]{java.lang.String.class, java.lang.String.class, java.lang.String.class,
                                        java.lang.String.class, java.lang.String.class, java.lang.String.class};

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
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
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
            jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
            jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE));
    }// </editor-fold>//GEN-END:initComponents

    private void archiveTableComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_archiveTableComponentAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_archiveTableComponentAdded

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable archiveTable;
    private javax.swing.JScrollPane jScrollPane3;
    // End of variables declaration//GEN-END:variables
}
