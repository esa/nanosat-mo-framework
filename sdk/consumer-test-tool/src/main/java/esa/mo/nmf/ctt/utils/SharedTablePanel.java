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
package esa.mo.nmf.ctt.utils;

import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.ArchiveCOMObjectsOutput;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.com.impl.util.HelperCOM;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.ccsds.moims.mo.com.COMObject;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.HeterogeneousList;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;

/**
 * Base panel showing a table of COM objects retrieved from a COM Archive. Subclasses define
 * the table columns and how each COM object is turned into a row.
 *
 * @author Cesar Coelho
 */
public abstract class SharedTablePanel extends javax.swing.JPanel {

    /** The table model holding the displayed rows. */
    protected DefaultTableModel tableData;
    /** The COM objects backing the table rows, in row order. */
    protected List<ArchivePersistenceObject> comObjects;
    /** Guards concurrent access to the table content. */
    protected Semaphore semaphore = new Semaphore(1);
    /** The COM Archive service the objects are retrieved from. */
    protected final ArchiveConsumerServiceImpl archiveService;

    /**
     * Constructor.
     *
     * @param archiveService The Archive service consumer.
     */
    public SharedTablePanel(final ArchiveConsumerServiceImpl archiveService) {
        initComponents();
        this.archiveService = archiveService;

        comObjects = new ArrayList<>();
        this.defineTableContent();
        tableData.addTableModelListener(e -> TableUtils.packColumnsLater(table));

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // Get from the list of objects the one we want and display
                    ArchivePersistenceObject comObject = comObjects.get(getSelectedRow());
                    try {
                        COMObjectWindow comObjectWindow = new COMObjectWindow(comObject,
                                false, archiveService.getArchiveStub());
                    } catch (IOException ex) {
                        Logger.getLogger(SharedTablePanel.class.getName()).log(
                                Level.SEVERE, null, ex);
                    }
                }
            }
        });

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
    }

    /**
     * Returns the model index of the selected row.
     *
     * @return the selected row model index, or {@code -1} if none is selected
     */
    public synchronized int getSelectedRow() {
        int index = table.getSelectedRow();
        if (index == -1) {
            return -1;
        }
        return table.getRowSorter().convertRowIndexToModel(index);
    }

    /**
     * Clears the table and repopulates it with the COM objects of the given ids, sorted by id.
     *
     * @param ids the object instance ids to retrieve
     * @param domain the domain of the objects
     * @param objType the COM object type
     */
    public void refreshTableWithIdsPairs(LongList ids, IdentifierList domain, ObjectType objType) {
        this.removeAllEntries(); // RemoveAll

        // Sort by id
        ids.sort(Comparator.comparing(Long::longValue));

        // Retrieve from the archive all the objects
        List<ArchivePersistenceObject> archiveCOMobjectList = HelperArchive.getArchiveCOMObjectList(
                archiveService.getArchiveStub(), objType, domain, ids);

        if (archiveCOMobjectList == null) {
            return;
        }

        if (archiveCOMobjectList.isEmpty()) {
            return;
        }

        COMObject comObjectInfo = HelperCOM.objType2COMObject(archiveCOMobjectList.get(0).getObjectType());

        // Add them
        for (int i = 0; i < archiveCOMobjectList.size(); i++) {
            addEntry(archiveCOMobjectList.get(i));
        }
    }

    /**
     * Clears the table and repopulates it with the COM objects of the given ids.
     *
     * @param objIds the object instance ids to retrieve
     * @param domain the domain of the objects
     * @param objType the COM object type
     */
    public void refreshTableWithIds(LongList objIds, IdentifierList domain, ObjectType objType) {
        // RemoveAll
        this.removeAllEntries();

        // Retrieve from the archive all the objects
        List<ArchivePersistenceObject> archiveCOMobjectList = HelperArchive.getArchiveCOMObjectList(
                archiveService.getArchiveStub(), objType, domain, objIds);

        if (archiveCOMobjectList == null) {
            return;
        }

        if (archiveCOMobjectList.isEmpty()) {
            return;
        }

        COMObject comObjectInfo = HelperCOM.objType2COMObject(archiveCOMobjectList.get(0).getObjectType());

        // Add them
        for (int i = 0; i < archiveCOMobjectList.size(); i++) {
            addEntry(archiveCOMobjectList.get(i));
        }
    }

    /**
     * Returns the COM Archive service the objects are retrieved from.
     *
     * @return the COM Archive service
     */
    protected ArchiveConsumerServiceImpl getArchiveService() {
        return this.archiveService;
    }

    /**
     * Returns the object instance id of the COM object in the selected row.
     *
     * @return the selected object's instance id
     */
    public Long getSelectedDefinitionObjId() {
        return comObjects.get(getSelectedRow()).getObjectId();
    }

    /**
     * Returns the COM objects backing the table rows.
     *
     * @return the COM objects, in row order
     */
    public List<ArchivePersistenceObject> getCOMObjects() {
        return comObjects;
    }

    /**
     * Returns the COM object in the selected row.
     *
     * @return the selected COM object
     */
    public ArchivePersistenceObject getSelectedCOMObject() {
        return comObjects.get(getSelectedRow());
    }

    /**
     * Returns the first COM object in the table.
     *
     * @return the first COM object, or {@code null} if the table is empty
     */
    public ArchivePersistenceObject getFirstCOMObject() {
        if (comObjects != null) {
            if (!comObjects.isEmpty()) {
                return comObjects.get(0);
            }
        }
        return null;
    }

    /**
     * Returns the COM object referenced as the source of the first COM object in the table.
     *
     * @return the source COM object, or {@code null} if the table is empty
     */
    public ArchivePersistenceObject getSourceFromFirstCOMObject() {
        if (comObjects == null || comObjects.isEmpty()) {
            return null;
        }

        ObjectKey source = comObjects.get(0).getArchiveDetails().getLinks().getSource();
        return HelperArchive.getArchiveCOMObject(
                archiveService,
                source.getType(),
                source.getDomain(),
                source.getId()
        );
    }

    /**
     * Returns the underlying Swing table.
     *
     * @return the table
     */
    public synchronized JTable getTable() {
        return table;
    }

    /**
     * Removes the selected row and its backing COM object.
     */
    public synchronized void removeSelectedEntry() {
        comObjects.remove(this.getSelectedRow());
        tableData.removeRow(this.getSelectedRow());
    }

    /**
     * Removes all rows and their backing COM objects.
     */
    public synchronized void removeAllEntries() {
        while (tableData.getRowCount() != 0) {
            comObjects.remove(tableData.getRowCount() - 1);
            tableData.removeRow(tableData.getRowCount() - 1);
        }
    }

    /**
     * Adds a row for each COM object contained in the given archive query output.
     *
     * @param archiveObjectOutput the archive query output to add, may be {@code null}
     */
    protected final void addEntries(final ArchiveCOMObjectsOutput archiveObjectOutput) {
        if (archiveObjectOutput == null) {
            return;
        }

        if (archiveObjectOutput.getArchiveDetailsList() == null) {
            return;
        }

        for (int i = 0; i < archiveObjectOutput.getArchiveDetailsList().size(); i++) {
            HeterogeneousList bodies = archiveObjectOutput.getObjectBodies();
            Element objects = (bodies == null) ? null : (Element) bodies.get(i);

            ArchivePersistenceObject comObject = new ArchivePersistenceObject(
                    archiveObjectOutput.getObjectType(),
                    archiveObjectOutput.getDomain(),
                    archiveObjectOutput.getArchiveDetailsList().get(i).getId(),
                    archiveObjectOutput.getArchiveDetailsList().get(i),
                    objects);

            addEntry(comObject);
        }
    }

    /**
     * Adds a single COM object as a row. Subclasses define how it maps to columns.
     *
     * @param comObject the COM object to add
     */
    public abstract void addEntry(final ArchivePersistenceObject comObject);

    /**
     * Defines the table columns and content model. Called by subclasses during setup.
     */
    public abstract void defineTableContent();

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane3 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();

        jScrollPane3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane3.setHorizontalScrollBar(null);
        jScrollPane3.setPreferredSize(new java.awt.Dimension(796, 280));
        jScrollPane3.setRequestFocusEnabled(false);

        table.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{{null, null, null, null, null, null}, {
                                                                                                                     null,
                                                                                                                     null,
                                                                                                                     null,
                                                                                                                     null,
                                                                                                                     null,
                                                                                                                     null}},
            new String[]{"Domain", "Object Type", "Obj Instance Id", "Timestamp", "Related", "Source"}) {
            Class[] types = new Class[]{java.lang.String.class, java.lang.String.class, java.lang.String.class,
                                        java.lang.String.class, java.lang.String.class, java.lang.String.class};

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        });
        table.setAlignmentX(0.0F);
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setAutoscrolls(false);
        table.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        table.setMaximumSize(null);
        table.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                tableComponentAdded(evt);
            }
        });
        jScrollPane3.setViewportView(table);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
            jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 575, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
            jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE));
    }// </editor-fold>//GEN-END:initComponents

    private void tableComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_tableComponentAdded
    }//GEN-LAST:event_tableComponentAdded

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
