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
package esa.mo.nmf.ctt.services.common;

import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.nmf.ctt.utils.SharedTablePanel;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;

/**
 *
 * @author Cesar Coelho
 */
public class ConfigurationTablePanel extends SharedTablePanel {

    public ConfigurationTablePanel(final ArchiveConsumerServiceImpl archiveService) {
        super(archiveService);
    }

    @Override
    public void addEntry(final Identifier name, final ArchivePersistenceObject comObject) {
        try {
            semaphore.acquire();
        } catch (final InterruptedException ex) {
            Logger.getLogger(SharedTablePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        final ActionDefinitionDetails pDef = (ActionDefinitionDetails) comObject.getObject();

        tableData.addRow(new Object[]{
            comObject.getArchiveDetails().getInstId(),
            "Dummy name...",
            pDef.getDescription(),
            "Severity removed...",
            pDef.getProgressStepCount().toString()
        });

        comObjects.add(comObject);
        semaphore.release();
    }

    @Override
    public void defineTableContent() {
        final String[] tableCol = new String[]{
            "Configuration Type", "description"
        };

        tableData = new javax.swing.table.DefaultTableModel(
                new Object[][]{}, tableCol) {
            Class[] types = new Class[]{
                java.lang.String.class, java.lang.String.class
            };

            @Override               //all cells false
            public boolean isCellEditable(final int row, final int column) {
                return false;
            }

            @Override
            public Class getColumnClass(final int columnIndex) {
                return types[columnIndex];
            }
        };

        super.getTable().setModel(tableData);
    }

}
