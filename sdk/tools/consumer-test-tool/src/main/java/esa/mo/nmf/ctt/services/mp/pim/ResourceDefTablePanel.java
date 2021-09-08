package esa.mo.nmf.ctt.services.mp.pim;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mp.structures.c_ResourceDefinitionDetails;

import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.nmf.ctt.utils.SharedTablePanel;

public class ResourceDefTablePanel extends SharedTablePanel {

    private static final Logger LOGGER = Logger.getLogger(ResourceDefTablePanel.class.getName());

    public ResourceDefTablePanel(ArchiveConsumerServiceImpl archiveService) {
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

        c_ResourceDefinitionDetails definition = (c_ResourceDefinitionDetails) comObject.getObject();


        String description = "Unknown";
        if (definition.getNumericResourceDef() != null) {
            description = definition.getNumericResourceDef().getDescription();
        } else if (definition.getStringResourceDef() != null) {
            description = definition.getStringResourceDef().getDescription();
        } else if (definition.getStatusResourceDef() != null) {
            description = definition.getStatusResourceDef().getDescription();
        }

        tableData.addRow(new Object[]{
            identity,
            comObject.getArchiveDetails().getInstId(),
            description,
        });

        comObjects.add(comObject);
        semaphore.release();
    }

    @Override
    public void defineTableContent() {
        String[] tableCol = new String[]{
            "Identity", "Definition ID", "Description"
        };

        tableData = new javax.swing.table.DefaultTableModel(
            new Object[][]{}, tableCol) {
                Class[] types = new Class[]{
                    java.lang.String.class, java.lang.Long.class, java.lang.String.class
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
