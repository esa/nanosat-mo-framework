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
package esa.mo.fw.configurationtool.stuff;

import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.ArchiveCOMObjectsOutput;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.fw.configurationtool.stuff.COMObjectWindow;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;

/**
 *
 * @author Cesar Coelho
 */
public abstract class SharedTablePanel extends javax.swing.JPanel {

    protected DefaultTableModel tableData;
    protected List<ArchivePersistenceObject> comObjects;
    protected Semaphore semaphore = new Semaphore(1);
    private final ArchiveConsumerServiceImpl archiveService;

    /**
     * Creates new form ObjectsDisplay
     *
     * @param archiveService
     */
    public SharedTablePanel(final ArchiveConsumerServiceImpl archiveService) {
        initComponents();
        this.archiveService = archiveService;
        
        comObjects = new ArrayList<ArchivePersistenceObject>();
        this.defineTableContent();
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // Get from the list of objects the one we want and display
                    ArchivePersistenceObject comObject = comObjects.get(getSelectedRow());
                    COMObjectWindow comObjectWindow = new COMObjectWindow(comObject, false, archiveService.getArchiveStub());
                }
            }
        });
        
    }
    
    public int getSelectedRow() {
        return table.getSelectedRow();
    }

    public void refreshTableWithIds(LongList objIds, IdentifierList domain, ObjectType objType){
        // RemoveAll
        this.removeAllEntries();
        
        // Retrieve from the archive all the objects
        List<ArchivePersistenceObject> archiveCOMobjectList = HelperArchive.getArchiveCOMObjectList(archiveService.getArchiveStub(), objType, domain, objIds);
        
        if (archiveCOMobjectList == null){
            return;
        }
        
        // Add them
        for (ArchivePersistenceObject comObject : archiveCOMobjectList){
            addEntry(comObject);
        }        
    }

    protected ArchiveConsumerServiceImpl getArchiveService(){
        return this.archiveService;
    }
    
    public Long getSelectedObjId() {
        return comObjects.get(getSelectedRow()).getObjectId();
    }

    public List<ArchivePersistenceObject> getCOMObjects() {
        return comObjects;
    }
    
    public ArchivePersistenceObject getSelectedCOMObject() {
        return comObjects.get(getSelectedRow());
    }

    public ArchivePersistenceObject getFirstCOMObject() {
        if (comObjects != null){
            if (comObjects.size() != 0){
                return comObjects.get(0);
            }
        }
        return null;
    }

    public ArchivePersistenceObject getSourceFromFirstCOMObject() {
        if (comObjects != null){
            if (comObjects.size() != 0){
                ArchivePersistenceObject source = HelperArchive.getArchiveCOMObject(
                        archiveService,
                        comObjects.get(0).getArchiveDetails().getDetails().getSource().getType(), 
                        comObjects.get(0).getArchiveDetails().getDetails().getSource().getKey().getDomain(), 
                        comObjects.get(0).getArchiveDetails().getDetails().getSource().getKey().getInstId()  );
                
                return source;
            }
        }
        return null;
    }
    
    public JTable getTable() {
        return table;
    }
    
    public void removeSelectedEntry() {
        comObjects.remove(this.getSelectedRow());
        tableData.removeRow(this.getSelectedRow());
    }
    
    public void removeAllEntries() {
        while (tableData.getRowCount() != 0) {
            comObjects.remove(tableData.getRowCount() - 1);
            tableData.removeRow(tableData.getRowCount() - 1);
        }
    }

    protected final void addEntries(ArchiveCOMObjectsOutput archiveObjectOutput) {

        if (archiveObjectOutput == null) {
            return;
        }

        if (archiveObjectOutput.getArchiveDetailsList() == null) {
            return;
        }

        for (int i = 0; i < archiveObjectOutput.getArchiveDetailsList().size(); i++) {
           
            Element objects = (archiveObjectOutput.getObjectBodies() == null)? null: (Element) archiveObjectOutput.getObjectBodies().get(i);

            ArchivePersistenceObject comObject = new ArchivePersistenceObject(
                archiveObjectOutput.getObjectType(),
                archiveObjectOutput.getDomain(),
                archiveObjectOutput.getArchiveDetailsList().get(i).getInstId(),
                archiveObjectOutput.getArchiveDetailsList().get(i),
                objects
            );

            addEntry(comObject);
        }

    }

    public abstract void addEntry(ArchivePersistenceObject comObject);
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

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Domain", "Object Type", "Obj Instance Id", "Timestamp", "Related", "Source"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
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
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 575, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tableComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_tableComponentAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_tableComponentAdded


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
