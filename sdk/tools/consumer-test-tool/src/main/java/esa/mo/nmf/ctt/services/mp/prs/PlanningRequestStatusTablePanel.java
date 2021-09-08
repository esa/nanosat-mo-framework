package esa.mo.nmf.ctt.services.mp.prs;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mp.structures.RequestUpdateDetails;

import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.nmf.ctt.utils.SharedTablePanel;

public class PlanningRequestStatusTablePanel extends SharedTablePanel {

    private static final Logger LOGGER = Logger.getLogger(PlanningRequestStatusTablePanel.class.getName());

    public PlanningRequestStatusTablePanel(ArchiveConsumerServiceImpl archiveService) {
        super(archiveService);
    }

    public void addEntry(Long requestIdentityId, Long requestVersionId, RequestUpdateDetails status) {
        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        tableData.addRow(new Object[]{
            requestIdentityId,
            requestVersionId,
            status.getErrCode(),
            status.getErrInfo(),
            status.getStatus().toString(),
            HelperTime.time2readableString(status.getTimestamp())
        });

        comObjects.add(null);

        semaphore.release();
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

        RequestUpdateDetails status = (RequestUpdateDetails) comObject.getObject();

        tableData.addRow(new Object[]{
            identity.toString(),
            comObject.getArchiveDetails().getInstId(),
            status.getErrCode(),
            status.getErrInfo(),
            status.getStatus().toString(),
            status.getTimestamp().toString()
        });

        comObjects.add(comObject);
        semaphore.release();
    }

    @Override
    public void defineTableContent() {
        String[] tableCol = new String[]{
            "Request Identity ID", "Request Version ID", "Error code", "Error info", "Status", "Timestamp"
        };

        tableData = new javax.swing.table.DefaultTableModel(
            new Object[][]{}, tableCol) {
                Class[] types = new Class[]{
                    java.lang.Long.class, java.lang.Long.class, java.lang.Integer.class,
                    java.lang.String.class, java.lang.String.class, java.lang.String.class
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
