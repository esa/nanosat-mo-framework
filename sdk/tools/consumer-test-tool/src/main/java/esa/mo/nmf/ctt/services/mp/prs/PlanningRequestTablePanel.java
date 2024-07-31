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
package esa.mo.nmf.ctt.services.mp.prs;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mp.structures.RequestVersionDetails;
import org.ccsds.moims.mo.mp.structures.TimeWindow;
import org.ccsds.moims.mo.mp.structures.TimeWindowList;
import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.mp.impl.com.COMObjectIdHelper;
import esa.mo.nmf.ctt.utils.SharedTablePanel;

public class PlanningRequestTablePanel extends SharedTablePanel {

    private static final Logger LOGGER = Logger.getLogger(PlanningRequestTablePanel.class.getName());

    public PlanningRequestTablePanel(ArchiveConsumerServiceImpl archiveService) {
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

        RequestVersionDetails requestVersion = (RequestVersionDetails) comObject.getObject();
        Long templateInstanceId = COMObjectIdHelper.getInstanceId(requestVersion.getTemplate());

        String validityPeriod = "Unknown";
        TimeWindowList validityWindows = requestVersion.getValidityTime();
        if (!validityWindows.isEmpty()) {
            TimeWindow validityWindow = validityWindows.get(0);
            Time validityStart = validityWindow.getStart() != null ? (Time) validityWindow.getStart().getValue() : null;
            Time validityEnd = validityWindow.getEnd() != null ? (Time) validityWindow.getEnd().getValue() : null;
            validityPeriod = String.format("%s - %s", HelperTime.time2readableString(validityStart), HelperTime
                .time2readableString(validityEnd));
        }

        tableData.addRow(new Object[]{identity.toString(), comObject.getArchiveDetails().getInstId(),
                                      templateInstanceId, validityPeriod, requestVersion.getDescription(),
                                      requestVersion.getUser(), requestVersion.getComments()});

        comObjects.add(comObject);
        semaphore.release();
    }

    @Override
    public void defineTableContent() {
        String[] tableCol = new String[]{"Request Identity", "Request Version ID", "Request Template ID",
                                         "Validity period", "Description", "User", "Comments"};

        tableData = new javax.swing.table.DefaultTableModel(new Object[][]{}, tableCol) {
            Class[] types = new Class[]{java.lang.String.class, java.lang.Long.class, java.lang.Long.class,
                                        java.lang.Long.class, java.lang.String.class, java.lang.String.class,
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
}
