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
package esa.mo.nmf.ctt.services.mp.pds;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mp.structures.PlanVersionDetails;

import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.nmf.ctt.utils.SharedTablePanel;

public class PlanVersionTablePanel extends SharedTablePanel {

    private static final Logger LOGGER = Logger.getLogger(PlanVersionTablePanel.class.getName());

    public PlanVersionTablePanel(ArchiveConsumerServiceImpl archiveService) {
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

        PlanVersionDetails planVersion = (PlanVersionDetails) comObject.getObject();

        int plannedActivitiesCount = 0;
        int plannedEventsCount = 0;
        Time planProductionDate = null;
        String planDescription = "";
        String planComments = "";
        if (planVersion != null) {
            if (planVersion.getItems() != null) {
                if (planVersion.getItems().getPlannedActivities() != null) {
                    plannedActivitiesCount = planVersion.getItems().getPlannedActivities().size();
                }
                if (planVersion.getItems().getPlannedEvents() != null) {
                    plannedEventsCount = planVersion.getItems().getPlannedEvents().size();
                }
            }
            if (planVersion.getInformation() != null) {
                planProductionDate = planVersion.getInformation().getProductionDate();
                planDescription = planVersion.getInformation().getDescription();
                planComments = planVersion.getInformation().getComments();
            }
        }

        tableData.addRow(new Object[]{identity.toString(), comObject.getArchiveDetails().getInstId(),
                                      plannedActivitiesCount, plannedEventsCount, HelperTime.time2readableString(
                                          planProductionDate), planDescription, planComments});

        comObjects.add(comObject);
        semaphore.release();
    }

    @Override
    public void defineTableContent() {
        String[] tableCol = new String[]{"Plan Identity", "Plan Version ID", "Planned activities", "Planned events",
                                         "Production date", "Description", "Comments"};

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
