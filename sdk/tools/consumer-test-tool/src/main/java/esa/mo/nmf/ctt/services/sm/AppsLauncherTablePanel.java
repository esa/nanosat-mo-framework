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

import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.nmf.ctt.utils.SharedTablePanel;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.structures.AppDetails;

/**
 *
 * @author Cesar Coelho
 */
public class AppsLauncherTablePanel extends SharedTablePanel {

    private static final Logger LOGGER = Logger.getLogger(AppsLauncherTablePanel.class.getName());

    public AppsLauncherTablePanel(ArchiveConsumerServiceImpl archiveService) {
        super(archiveService);
    }

    @Override
    public void addEntry(final Identifier name, final ArchivePersistenceObject comObject) {
        if (comObject == null) {
            LOGGER.log(Level.SEVERE, "The table cannot process a null COM Object.");
            return;
        }

        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        AppDetails appDetails = (AppDetails) comObject.getObject();

        tableData.addRow(new Object[]{comObject.getArchiveDetails().getInstId(), appDetails.getName().toString(),
                                      appDetails.getDescription(), appDetails.getCategory().toString(), appDetails
                                          .getRunAtStartup(), appDetails.getRunning(), ""});

        comObjects.add(comObject);
        semaphore.release();
    }

    public void switchEnabledstatus(boolean status) {
        switchEnabledstatus(status, this.getSelectedRow());
    }

    public void switchEnabledstatus(boolean status, int rowId) {
        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        // 4 because it is where generationEnabled is!
        tableData.setValueAt(status, rowId, 5);
        ((AppDetails) this.getSelectedCOMObject().getObject()).setRunning(status);

        semaphore.release();
    }

    public void switchEnabledstatusAll(boolean status) {
        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        // 5 because it is where the flag is!
        for (int i = 0; i < this.getTable().getRowCount(); i++) {
            tableData.setValueAt(status, i, 5);
            ((AppDetails) this.getCOMObjects().get(i).getObject()).setRunning(status);
        }

        semaphore.release();
    }

    public void reportStatus(final String text, final int appId) {
        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        LOGGER.log(Level.INFO, "Updating test for appId {0}: ''{1}''", new Object[]{appId, text});

        int index;
        try {
            index = this.findIndex(appId);

            // 6 because it is where the status field is!
            tableData.setValueAt(text, index, 6);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        semaphore.release();
    }

    public int findIndex(int appId) throws Exception {

        final int max = tableData.getRowCount();

        for (int i = 0; i < max; i++) {
            Long value = (Long) tableData.getValueAt(i, 0);

            if (value.intValue() == appId) {
                return i;
            }
        }

        throw new Exception("The appId could not be found on the table. AppId: " + appId);
    }

    @Override
    public void defineTableContent() {

        String[] tableCol = new String[]{"Obj Inst Id", "name", "description", "category", "runAtStartup", "running",
                                         "Status"};

        tableData = new javax.swing.table.DefaultTableModel(new Object[][]{}, tableCol) {
            Class[] types = new Class[]{java.lang.Integer.class, java.lang.String.class, java.lang.String.class,
                                        java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class,
                                        java.lang.String.class};

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
