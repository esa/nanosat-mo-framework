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

import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.mc.impl.consumer.AlertConsumerServiceImpl;
import esa.mo.nmf.ctt.utils.TableUtils;
import esa.mo.nmf.ctt.windows.element.MOWindow;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.HierarchyEvent;
import java.io.InterruptedIOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperTime;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.alert.AlertServiceInfo;
import org.ccsds.moims.mo.mc.alert.consumer.AlertAdapter;
import org.ccsds.moims.mo.mc.alert.consumer.MonitorAlertSubscriptionKeys;
import org.ccsds.moims.mo.mc.structures.AlertDefinition;
import org.ccsds.moims.mo.mc.structures.AlertDefinitionList;
import org.ccsds.moims.mo.mc.structures.AlertEvent;

/**
 * The AlertConsumerPanel class holds a panel to interact with an Alert service.
 *
 * @author Cesar Coelho
 */
public class AlertConsumerPanel extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(AlertConsumerPanel.class.getName());

    private final AlertConsumerServiceImpl serviceMCAlert;
    private final AlertTablePanel alertTable;
    private Subscription monitorAlertSubscription;
    private DefaultTableModel alertLogModel;

    /**
     * Constructor.
     *
     * @param serviceMCAlert The Alert service consumer.
     */
    public AlertConsumerPanel(AlertConsumerServiceImpl serviceMCAlert) {
        this.serviceMCAlert = serviceMCAlert;
        alertTable = new AlertTablePanel(serviceMCAlert.getCOMServices().getArchiveService());
        initComponents();
    }

    public void init() {
        this.listDefinitionAllButtonActionPerformed(null);

        if (serviceMCAlert.getConnectionDetails().getBrokerURI() == null) {
            LOGGER.log(Level.WARNING,
                    "Alert service has no broker URI - monitorAlert subscription skipped."
                    + " Reconnect to a provider that supports monitorAlert.");
            return;
        }

        monitorAlertSubscription = ConnectionConsumer.subscriptionWildcardRandom();
        try {
            serviceMCAlert.getAlertStub().monitorAlertRegister(
                    monitorAlertSubscription, new AlertConsumerAdapter());
        } catch (MALInteractionException | MALException ex) {
            LOGGER.log(Level.SEVERE, "Failed to subscribe to monitorAlert", ex);
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (monitorAlertSubscription != null) {
            IdentifierList ids = new IdentifierList();
            ids.add(monitorAlertSubscription.getSubscriptionId());
            try {
                serviceMCAlert.getAlertStub().monitorAlertDeregister(ids);
            } catch (MALInteractionException | MALException ex) {
                LOGGER.log(Level.SEVERE, "Failed to deregister from monitorAlert", ex);
            }
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Alert Service - Definitions");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Left: definitions table + buttons
        JScrollPane definitionsScrollPane = new JScrollPane();
        definitionsScrollPane.setViewportView(alertTable);

        JButton enableDefinitionAllButton = new JButton("enableReporting(0)");
        enableDefinitionAllButton.addActionListener(this::enableDefinitionAllAggActionPerformed);

        JButton enableDefinitionButton = new JButton("enableReporting");
        enableDefinitionButton.addActionListener(this::enableDefinitionButtonAggActionPerformed);

        JButton listDefinitionButton = new JButton("listDefinition()");
        listDefinitionButton.addActionListener(this::listDefinitionButtonActionPerformed);

        JButton updateDefinitionButton = new JButton("updateDefinition");
        updateDefinitionButton.addActionListener(this::updateDefinitionButtonActionPerformed);

        JButton listDefinitionAllButton = new JButton("listDefinition(\"*\")");
        listDefinitionAllButton.addActionListener(this::listDefinitionAllButtonActionPerformed);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(enableDefinitionAllButton);
        buttonPanel.add(enableDefinitionButton);
        buttonPanel.add(listDefinitionButton);
        buttonPanel.add(updateDefinitionButton);
        buttonPanel.add(listDefinitionAllButton);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(definitionsScrollPane, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Right: incoming alerts log
        String[] cols = {"Time", "Definition ID", "Source", "Severity", "Arguments"};
        alertLogModel = new DefaultTableModel(new Object[][]{}, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable alertLogTable = new JTable(alertLogModel);
        alertLogTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        alertLogModel.addTableModelListener(
                e -> SwingUtilities.invokeLater(() -> TableUtils.packColumns(alertLogTable)));

        JLabel alertLogLabel = new JLabel("Incoming Alerts");
        alertLogLabel.setFont(alertLogLabel.getFont().deriveFont(Font.BOLD, 14f));
        alertLogLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(alertLogLabel, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(alertLogTable), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && splitPane.isShowing()) {
                splitPane.setDividerLocation(0.5);
            }
        });
        add(splitPane, BorderLayout.CENTER);
    }

    private void listDefinitionButtonActionPerformed(java.awt.event.ActionEvent evt) {
        IdentifierList alertNames = new IdentifierList();
        MOWindow alertNamesWindow = new MOWindow(alertNames, true);

        try {
            LongList objIds;
            try {
                IdentifierList names = (IdentifierList) alertNamesWindow.getObject();
                objIds = this.serviceMCAlert.getAlertStub().listDefinition(names);
            } catch (InterruptedIOException ex) {
                return;
            }

            StringBuilder str = new StringBuilder("Object instance identifiers on the provider: \n");
            for (Long objId : objIds) {
                str.append("ObjId Def: ").append(objId.toString()).append("\n");
            }

            JOptionPane.showMessageDialog(null, str.toString(),
                    "Returned List from the Provider", JOptionPane.PLAIN_MESSAGE);
        } catch (MALInteractionException | MALException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private void updateDefinitionButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (alertTable.getSelectedRow() == -1) {
            return;
        }

        ArchivePersistenceObject obj = alertTable.getSelectedCOMObject();
        MOWindow moObject = new MOWindow(obj.getObject(), true);

        LongList objIds = new LongList();
        objIds.add(alertTable.getSelectedDefinitionObjId());

        AlertDefinitionList defs = new AlertDefinitionList();
        try {
            defs.add((AlertDefinition) moObject.getObject());
        } catch (InterruptedIOException ex) {
            return;
        }

        try {
            this.serviceMCAlert.getAlertStub().updateDefinition(objIds, defs);
            this.listDefinitionAllButtonActionPerformed(null);
        } catch (MALInteractionException | MALException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private void listDefinitionAllButtonActionPerformed(java.awt.event.ActionEvent evt) {
        IdentifierList idList = new IdentifierList();
        idList.add(new Identifier("*"));

        try {
            this.serviceMCAlert.getAlertStub().asyncListDefinition(idList, new AlertAdapter() {
                @Override
                public void listDefinitionResponseReceived(MALMessageHeader msgHeader,
                        LongList alertObjInstIds, Map qosProperties) {
                    SwingUtilities.invokeLater(
                            () -> alertTable.refreshTableWithIdsPairs(
                            alertObjInstIds,
                            serviceMCAlert.getConnectionDetails().getDomain(),
                            AlertServiceInfo.ALERTDEFINITION_OBJECT_TYPE));
                    LOGGER.log(Level.INFO,
                            "listDefinition(\"*\") returned {0} object instance identifiers",
                            alertObjInstIds.size());
                }

                @Override
                public void listDefinitionErrorReceived(MALMessageHeader msgHeader,
                        MOErrorException error, Map qosProperties) {
                    JOptionPane.showMessageDialog(null,
                            "There was an error during the listDefinition operation.",
                            "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, null, error);
                }
            });
        } catch (MALInteractionException | MALException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private void enableDefinitionAllAggActionPerformed(java.awt.event.ActionEvent evt) {
        Boolean curState;

        if (alertTable.getSelectedRow() == -1) {
            AlertDefinition alertDefinition = (AlertDefinition) alertTable.getFirstCOMObject().getObject();
            curState = alertDefinition != null ? alertDefinition.getReportingEnabled() : true;
        } else {
            curState = ((AlertDefinition) alertTable.getSelectedCOMObject().getObject()).getReportingEnabled();
        }

        LongList ids = new LongList();
        ids.add(0L);

        try {
            this.serviceMCAlert.getAlertStub().enableReporting(!curState, ids);
            alertTable.switchEnabledstatusAll(!curState);
        } catch (MALInteractionException | MALException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private void enableDefinitionButtonAggActionPerformed(java.awt.event.ActionEvent evt) {
        if (alertTable.getSelectedRow() == -1) {
            return;
        }

        Boolean curState = ((AlertDefinition) alertTable.getSelectedCOMObject().getObject()).getReportingEnabled();
        LongList ids = new LongList();
        ids.add(0L);

        try {
            this.serviceMCAlert.getAlertStub().enableReporting(!curState, ids);
            alertTable.switchEnabledstatus(!curState);
        } catch (MALInteractionException | MALException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public class AlertConsumerAdapter extends AlertAdapter {

        @Override
        public void monitorAlertNotifyReceived(MALMessageHeader msgHeader,
                Identifier subscriptionId,
                UpdateHeader updateHeader,
                MonitorAlertSubscriptionKeys subscriptionKeys,
                AlertEvent alertEvent,
                ObjectKey source,
                Map qosProperties) {

            final Long definitionId = subscriptionKeys.getDefinitionId();

            final String timestamp = HelperTime.time2readableString(msgHeader.getTimestamp());
            final Long finalDefinitionId = definitionId;
            final String sourceStr = source != null ? source.toString() : "";
            final String severity = alertEvent != null
                    ? resolveAlertSeverity(finalDefinitionId) : "";
            final String arguments = alertEvent != null && alertEvent.getArgumentValues() != null
                    ? alertEvent.getArgumentValues().toString() : "";

            SwingUtilities.invokeLater(() -> alertLogModel.addRow(new Object[]{
                timestamp,
                finalDefinitionId,
                sourceStr,
                severity,
                arguments
            }));
        }

        private String resolveAlertSeverity(Long definitionId) {
            if (definitionId == null) {
                return "";
            }
            ArchivePersistenceObject obj = alertTable.getCOMObjectById(definitionId);
            if (obj == null) {
                return "";
            }
            AlertDefinition def = (AlertDefinition) obj.getObject();
            return def != null && def.getSeverity() != null ? def.getSeverity().toString() : "";
        }

@Override
        public void monitorAlertRegisterAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
            LOGGER.log(Level.INFO, "Subscribed to monitorAlert successfully.");
        }

        @Override
        public void monitorAlertRegisterErrorReceived(MALMessageHeader msgHeader,
                MOErrorException error, Map qosProperties) {
            LOGGER.log(Level.WARNING, "Failed to subscribe to monitorAlert: {0}", error);
        }
    }

}
