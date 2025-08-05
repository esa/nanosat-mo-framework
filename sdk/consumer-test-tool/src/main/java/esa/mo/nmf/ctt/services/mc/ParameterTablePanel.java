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
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperAttributes;
import org.ccsds.moims.mo.mc.structures.ParameterDefinition;

/**
 *
 * @author Cesar Coelho
 */
public class ParameterTablePanel extends SharedTablePanel {

    /**
     * Constructor.
     *
     * @param archiveService The Archive service consumer.
     */
    public ParameterTablePanel(ArchiveConsumerServiceImpl archiveService) {
        super(archiveService);
    }

    @Override
    public void addEntry(final ArchivePersistenceObject comObject) {
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

        ParameterDefinition pDef = (ParameterDefinition) comObject.getObject();

        tableData.addRow(new Object[]{
            comObject.getArchiveDetails().getInstId(),
            pDef.getName().getValue(),
            pDef.getDescription(),
            HelperAttributes.typeShortForm2attributeName(pDef.getRawType().getValue()),
            pDef.getRawUnit(),
            pDef.getGenerationEnabled(),
            pDef.getReportInterval().getValue()});

        comObjects.add(comObject);
        semaphore.release();

    }

    public void switchEnabledstatus(boolean status) {
        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(SharedTablePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        // 5 because it is where generationEnabled is!
        tableData.setValueAt(status, this.getSelectedRow(), 5);
        //((ParameterDefinition) this.getSelectedCOMObject().getObject()).setGenerationEnabled(status);
        ParameterDefinition def = (ParameterDefinition) this.getSelectedCOMObject().getObject();
        ParameterDefinition newDef = this.generateNewParameterDef(def, status);
        this.getSelectedCOMObject().setObject(newDef);

        semaphore.release();
    }

    public void switchEnabledstatusAll(boolean status) {
        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(SharedTablePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        // 5 because it is where generationEnabled is!
        for (int i = 0; i < this.getTable().getRowCount(); i++) {
            tableData.setValueAt(status, i, 5);
            //((ParameterDefinition) this.getCOMObjects().get(i).getObject()).setGenerationEnabled(status);
            ParameterDefinition def = (ParameterDefinition) this.getCOMObjects().get(i).getObject();
            ParameterDefinition newDef = this.generateNewParameterDef(def, status);
            this.getCOMObjects().get(i).setObject(newDef);
        }

        semaphore.release();
    }

    public ParameterDefinition generateNewParameterDef(ParameterDefinition def, boolean generation) {
        return new ParameterDefinition(
                def.getName(),
                def.getDescription(),
                def.getRawType(),
                def.getRawUnit(),
                generation,
                def.getReportInterval(),
                def.getValidityExpression(),
                def.getConversion());
    }

    @Override
    public void defineTableContent() {
        String[] tableCol = new String[]{
            "Id",
            "name",
            "description",
            "rawType",
            "rawUnit",
            "generationEnabled", "updateInterval"};

        tableData = new javax.swing.table.DefaultTableModel(new Object[][]{}, tableCol) {
            Class[] types = new Class[]{
                java.lang.Integer.class,
                java.lang.String.class,
                java.lang.String.class,
                java.lang.Object.class,
                java.lang.String.class,
                java.lang.Boolean.class,
                java.lang.Float.class};

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
