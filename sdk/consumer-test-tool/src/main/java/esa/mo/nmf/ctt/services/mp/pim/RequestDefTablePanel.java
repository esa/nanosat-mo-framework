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
package esa.mo.nmf.ctt.services.mp.pim;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mp.structures.RequestTemplateDetails;

import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.nmf.ctt.utils.SharedTablePanel;

public class RequestDefTablePanel extends SharedTablePanel {

    private static final Logger LOGGER = Logger.getLogger(RequestDefTablePanel.class.getName());

    public RequestDefTablePanel(ArchiveConsumerServiceImpl archiveService) {
        super(archiveService);
    }

    @Override
    public void addEntry(Identifier identity, ArchivePersistenceObject comObject) {
        if (comObject == null) {
            LOGGER.log(Level.SEVERE, "The table cannot process a null COM Object.");
            return;
        }

        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        RequestTemplateDetails requestDef = (RequestTemplateDetails) comObject.getObject();

        tableData.addRow(new Object[]{identity, comObject.getArchiveDetails().getInstId(), requestDef
            .getDescription(),});

        comObjects.add(comObject);
        semaphore.release();
    }

    @Override
    public void defineTableContent() {
        String[] tableCol = new String[]{"Identity", "Definition ID", "Description"};

        tableData = new javax.swing.table.DefaultTableModel(new Object[][]{}, tableCol) {
            Class[] types = new Class[]{java.lang.String.class, java.lang.Long.class, java.lang.String.class};

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
