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
package esa.mo.nmf.ctt.services.mc;

import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.nmf.ctt.utils.SharedTablePanel;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetails;

/**
 *
 * @author Cesar Coelho
 */
public class AggregationTablePanel extends SharedTablePanel {

    public AggregationTablePanel(ArchiveConsumerServiceImpl archiveService) {
        super(archiveService);
    }

    @Override
    public void addEntry(final Identifier name, final ArchivePersistenceObject comObject) {
        if (comObject == null) {
            Logger.getLogger(SharedTablePanel.class.getName()).log(Level.SEVERE,
                "The table cannot process a null COM Object.");
            return;
        }

        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(SharedTablePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        AggregationDefinitionDetails pDef = (AggregationDefinitionDetails) comObject.getObject();

        tableData.addRow(new Object[]{comObject.getArchiveDetails().getDetails().getRelated(), name.toString(), pDef
            .getDescription(), pDef.getCategory().toString(), pDef.getGenerationEnabled(), pDef.getReportInterval()
                .toString(), pDef.getFilterEnabled(), pDef.getFilteredTimeout().getValue()});

        comObjects.add(comObject);
        semaphore.release();
    }

    public void switchEnabledstatus(boolean status) {

        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(SharedTablePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        // 4 because it is where generationEnabled is!
        tableData.setValueAt(status, this.getSelectedRow(), 4);
        ((AggregationDefinitionDetails) this.getSelectedCOMObject().getObject()).setGenerationEnabled(status);

        semaphore.release();

    }

    public void switchEnabledstatusAll(boolean status) {

        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(SharedTablePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        // 4 because it is where generationEnabled is!

        for (int i = 0; i < this.getTable().getRowCount(); i++) {
            tableData.setValueAt(status, i, 4);
            ((AggregationDefinitionDetails) this.getCOMObjects().get(i).getObject()).setGenerationEnabled(status);
        }

        semaphore.release();

    }

    public void switchFilterEnabledstatus(boolean status) {

        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(SharedTablePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        // 6 because it is where filter is!
        tableData.setValueAt(status, this.getSelectedRow(), 6);
        ((AggregationDefinitionDetails) this.getSelectedCOMObject().getObject()).setFilterEnabled(status);

        semaphore.release();

    }

    public void switchFilterEnabledstatusAll(boolean status) {

        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(SharedTablePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        // 6 because it is where filter is!
        for (int i = 0; i < this.getTable().getRowCount(); i++) {
            tableData.setValueAt(status, i, 6);
            ((AggregationDefinitionDetails) this.getCOMObjects().get(i).getObject()).setFilterEnabled(status);
        }

        semaphore.release();

    }

    @Override
    public void defineTableContent() {

        String[] tableCol = new String[]{"Identity", "name", "description", "category", "generationEnabled",
                                         "updateInterval", "filterEnabled"};

        tableData = new javax.swing.table.DefaultTableModel(new Object[][]{}, tableCol) {
            Class[] types = new Class[]{java.lang.Integer.class, java.lang.String.class, java.lang.String.class,
                                        java.lang.String.class, java.lang.Boolean.class, java.lang.String.class,
                                        java.lang.Boolean.class, java.lang.Double.class};

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
