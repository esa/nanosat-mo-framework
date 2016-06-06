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
package esa.mo.fw.configurationtool.services.mc;

import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.fw.configurationtool.stuff.SharedTablePanel;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticLinkDetails;

/**
 *
 * @author Cesar Coelho
 */
public class StatisticLinkTablePanel extends SharedTablePanel {

    public StatisticLinkTablePanel(ArchiveConsumerServiceImpl archiveService) {
        super(archiveService);
    }

    @Override
    public void addEntry(ArchivePersistenceObject comObject) {

        if (comObject == null){
            Logger.getLogger(SharedTablePanel.class.getName()).log(Level.SEVERE, "The table cannot process a null COM Object.");
            return;
        }

        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(SharedTablePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        StatisticLinkDetails statLink = (StatisticLinkDetails) comObject.getObject();
        
        // Get the parameter definition from the source link
        ParameterDefinitionDetails pDef = (ParameterDefinitionDetails) HelperArchive.getObjectBodyFromArchive(
                this.getArchiveService().getArchiveStub(), 
                comObject.getArchiveDetails().getDetails().getSource().getType(), 
                comObject.getArchiveDetails().getDetails().getSource().getKey().getDomain(), 
                comObject.getArchiveDetails().getDetails().getSource().getKey().getInstId());
        
        tableData.addRow(new Object[]{
            comObject.getArchiveDetails().getInstId(),
            comObject.getArchiveDetails().getDetails().getRelated().toString(),
            pDef.getName().toString(),
            statLink.getCollectionInterval().toString(),
            statLink.getReportingInterval().toString(),
            statLink.getSamplingInterval().toString(),
            statLink.getReportingEnabled()
        });

        comObjects.add(comObject);

        semaphore.release();

    }

    public void switchEnabledstatus(boolean status){
        
        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(SharedTablePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        // 6 because it is where generationEnabled is!
        tableData.setValueAt(status, this.getSelectedRow(), 6);
        ((StatisticLinkDetails) this.getSelectedCOMObject().getObject()).setReportingEnabled(status);
        
        semaphore.release();
        
    }
    
    public void switchEnabledstatusAll(boolean status){
        
        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(SharedTablePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // 6 because it is where generationEnabled is!
        for (int i = 0; i < this.getTable().getRowCount() ; i++){
            tableData.setValueAt(status, i, 6);
            ((StatisticLinkDetails) this.getCOMObjects().get(i).getObject()).setReportingEnabled(status);
        }
        
        semaphore.release();
        
    }
    
    
    @Override
    public void defineTableContent() {
    
        String[] tableCol = new String[]{
            "Obj Inst Id", "Stat Function Id", "Parameter Name", "collection interval", "reporting interval", "sampling interval", "reporting Enabled" };

        tableData = new javax.swing.table.DefaultTableModel(
                new Object[][]{}, tableCol) {
                    Class[] types = new Class[]{
                        java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                        java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
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

        super.getTable().setModel(tableData);

    }
    
}
