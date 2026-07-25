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
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import org.ccsds.moims.mo.sm.structures.Command;

/**
 * Table panel listing the recently executed commands and their exit codes.
 *
 * @author Cesar Coelho
 */
public class CommandExecutorTablePanel extends SharedTablePanel {

    private static final Logger LOGGER = Logger.getLogger(CommandExecutorTablePanel.class.getName());

    /**
     * Creates the command-executor table panel.
     *
     * @param archiveService the COM Archive service the command records are read from
     */
    public CommandExecutorTablePanel(ArchiveConsumerServiceImpl archiveService) {
        super(archiveService);
    }

    @Override
    public void addEntry(final ArchivePersistenceObject comObject) {
        if (comObject == null) {
            Logger.getLogger(SharedTablePanel.class.getName()).log(
                    Level.SEVERE, "The table cannot process a null COM Object.");
            return;
        }

        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(SharedTablePanel.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        Command commandDetails = (Command) comObject.getObject();

        String pid = commandDetails.getPid() == null ? "not started yet" : commandDetails.getPid().toString();
        String exitCode = commandDetails.getExitCode() == null ? "not completed yet" : commandDetails.getExitCode().toString();
        tableData.addRow(new Object[]{
            comObject.getArchiveDetails().getId(),
            commandDetails.getCommand(),
            pid,
            exitCode});

        comObjects.add(comObject);
        semaphore.release();
    }

    /**
     * Updates the displayed exit code of the command with the given object instance id.
     *
     * @param objInstId the command's object instance id
     * @param exitCode the process exit code
     */
    public void updateExitCode(Long objInstId, int exitCode) {
        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        try {
            int index = findIndex(objInstId);
            tableData.setValueAt(Integer.toString(exitCode), index, 3);
        } catch (NoSuchElementException ex) {
            // It is typical to get the exit code before archive retrieval of the Command can complete
            LOGGER.log(Level.WARNING,
                    "Received exitCode update for objId {0}, but the object has not yet been received.", objInstId);
        }

        semaphore.release();
    }

    /**
     * Returns the table row index of the command with the given object instance id.
     *
     * @param objInstId the command's object instance id
     * @return the row index
     * @throws NoSuchElementException if no such command is in the table
     */
    public int findIndex(Long objInstId) throws NoSuchElementException {

        final int max = tableData.getRowCount();

        for (int i = 0; i < max; i++) {
            Long value = (Long) tableData.getValueAt(i, 0);

            if (Objects.equals(value, objInstId)) {
                return i;
            }
        }

        throw new NoSuchElementException("The objInstId could not be found on the table. objInstId: " + objInstId);
    }

    @Override
    public void defineTableContent() {

        String[] tableCol = new String[]{"Obj Inst Id", "command", "PID", "exitCode"};

        tableData = new javax.swing.table.DefaultTableModel(new Object[][]{}, tableCol) {
            Class[] columnClasses = new Class[]{
                java.lang.Long.class,
                java.lang.String.class,
                java.lang.String.class,
                java.lang.String.class};

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                return columnClasses[columnIndex];
            }
        };

        super.getTable().setModel(tableData);

    }

    /**
     * Returns the table model backing this panel.
     *
     * @return the table model
     */
    public DefaultTableModel getTableData() {
        return tableData;
    }

}
