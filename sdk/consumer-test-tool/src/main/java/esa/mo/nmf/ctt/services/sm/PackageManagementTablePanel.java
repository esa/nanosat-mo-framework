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
package esa.mo.nmf.ctt.services.sm;

import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.nmf.ctt.utils.SharedTablePanel;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import org.ccsds.moims.mo.mal.structures.Identifier;

/**
 *
 * @author Cesar Coelho
 */
public class PackageManagementTablePanel extends SharedTablePanel {

    public PackageManagementTablePanel() {
        super(null);
    }

    @Override
    public void addEntry(final Identifier name, final ArchivePersistenceObject comObject) {
        Logger.getLogger(SharedTablePanel.class.getName()).log(Level.SEVERE, "This method cannot be used!");
    }

    @Override
    public void removeAllEntries() {
        while (tableData.getRowCount() != 0) {
            tableData.removeRow(tableData.getRowCount() - 1);
        }
    }

    public Identifier getSelectedPackage() {
        int index = this.getSelectedRow();
        // The name is on column 0
        return (Identifier) tableData.getValueAt(index, 0);
    }

    public void addEntry(final Identifier name, final boolean isInstalled) {
        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(SharedTablePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        tableData.addRow(new Object[]{name, isInstalled});

        semaphore.release();
    }

    public void switchEnabledstatus(boolean status) {
        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(SharedTablePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        // 1 because it is where isInstalled is!
        tableData.setValueAt(status, this.getSelectedRow(), 1);

        semaphore.release();
    }

    @Override
    public void defineTableContent() {

        String[] tableCol = new String[]{"Package name", "isInstalled"};

        tableData = new javax.swing.table.DefaultTableModel(new Object[][]{}, tableCol) {
            Class[] types = new Class[]{java.lang.String.class, java.lang.Boolean.class};

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

    public DefaultTableModel getTableData() {
        return tableData;
    }

}
