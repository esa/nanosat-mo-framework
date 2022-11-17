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
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.nmf.ctt.utils.SharedTablePanel;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mc.check.structures.CheckDefinitionDetails;

/**
 *
 * @author Cesar Coelho
 */
public class CheckDefinitionsTablePanel extends SharedTablePanel {

    public CheckDefinitionsTablePanel(ArchiveConsumerServiceImpl archiveService) {
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

        // We are actually receiving a Check Definition that points to the right definition via the source field

        // So, let's get the source Object with the details of the definition
        ArchivePersistenceObject sourceCOMObject = HelperArchive.getArchiveCOMObject(super.getArchiveService()
            .getArchiveStub(), comObject.getArchiveDetails().getDetails().getSource().getType(), comObject
                .getArchiveDetails().getDetails().getSource().getKey().getDomain(), comObject.getArchiveDetails()
                    .getDetails().getSource().getKey().getInstId());

        CheckDefinitionDetails checkDef = (CheckDefinitionDetails) sourceCOMObject.getObject();

        tableData.addRow(new Object[]{comObject.getArchiveDetails().getInstId(), name.toString(), checkDef
            .getDescription(), checkDef.getCheckSeverity().toString(), checkDef.getMaxReportingInterval().toString(),
                                      checkDef.getNominalCount().getValue(), checkDef.getNominalTime().toString(),
                                      checkDef.getViolationCount().getValue(), checkDef.getViolationTime()});

        comObjects.add(comObject);
        semaphore.release();
    }

    @Override
    public void defineTableContent() {

        String[] tableCol = new String[]{"Obj Inst Id", "Check Definition name", "Description", "Severity",
                                         "Max Reporting Interval", "Nominal Count", "Nominal Time", "Violation Count",
                                         "Violation Time"};

        tableData = new javax.swing.table.DefaultTableModel(new Object[][]{}, tableCol) {
            Class[] types = new Class[]{java.lang.Integer.class, java.lang.String.class, java.lang.String.class,
                                        java.lang.String.class, java.lang.String.class, java.lang.Integer.class,
                                        java.lang.String.class, java.lang.Integer.class, java.lang.String.class};

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
