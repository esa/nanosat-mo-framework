/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft - v2.4
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
import esa.mo.nmf.ctt.utils.TableUtils;
import esa.mo.mc.impl.consumer.ActionConsumerServiceImpl;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.ctt.windows.element.MOWindow;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.HierarchyEvent;
import java.io.InterruptedIOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperTime;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.action.ActionServiceInfo;
import org.ccsds.moims.mo.mc.action.consumer.ActionAdapter;
import org.ccsds.moims.mo.mc.structures.*;

/**
 * The ActionConsumerPanel class holds a panel to interact with an Action
 * service.
 *
 * @author Cesar Coelho
 */
public class ActionConsumerPanel extends javax.swing.JPanel {

    private final ActionConsumerServiceImpl serviceMCAction;
    private final ActionTablePanel actionTable;
    private GroundMOAdapterImpl gma;
    private Subscription monitorExecutionSubscription;
    private DefaultTableModel executionLogModel;

    private JScrollPane jScrollPane2;
    private JButton executeAction;
    private JButton listDefinitionButton;
    private JButton listDefinitionAllButton;

    /**
     * Creates new ActionConsumerPanel.
     *
     * @param groundMOAdapter The Ground MO Adapter.
     */
    public ActionConsumerPanel(GroundMOAdapterImpl groundMOAdapter) {
        this.gma = groundMOAdapter;
        this.serviceMCAction = groundMOAdapter.getMCServices().getActionService();
        actionTable = new ActionTablePanel(serviceMCAction.getCOMServices().getArchiveService());
        initComponents();
    }

    /**
     * Creates new ActionConsumerPanel.
     *
     * @param serviceMCAction The Action service consumer.
     */
    public ActionConsumerPanel(ActionConsumerServiceImpl serviceMCAction) {
        this.serviceMCAction = serviceMCAction;
        actionTable = new ActionTablePanel(serviceMCAction.getCOMServices().getArchiveService());
        initComponents();
    }

    public void init() {
        this.listDefinitionAllButtonActionPerformed(null);

        if (serviceMCAction.getConnectionDetails().getBrokerURI() == null) {
            Logger.getLogger(ActionConsumerPanel.class.getName()).log(Level.WARNING,
                    "Action service has no broker URI - monitorExecution subscription skipped."
                    + " Reconnect to a provider that supports monitorExecution.");
            return;
        }

        monitorExecutionSubscription = ConnectionConsumer.subscriptionWildcardRandom();
        try {
            serviceMCAction.getActionStub().monitorExecutionRegister(
                    monitorExecutionSubscription, new ActionConsumerAdapter());
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ActionConsumerPanel.class.getName()).log(Level.SEVERE,
                    "Failed to subscribe to monitorExecution", ex);
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (monitorExecutionSubscription != null) {
            IdentifierList ids = new IdentifierList();
            ids.add(monitorExecutionSubscription.getSubscriptionId());
            try {
                serviceMCAction.getActionStub().monitorExecutionDeregister(ids);
            } catch (MALInteractionException | MALException ex) {
                Logger.getLogger(ActionConsumerPanel.class.getName()).log(Level.SEVERE,
                        "Failed to deregister from monitorExecution", ex);
            }
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Action Service - Definitions");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Left: definitions table + buttons
        jScrollPane2 = new JScrollPane();
        jScrollPane2.setViewportView(actionTable);

        executeAction = new JButton("executeAction");
        executeAction.addActionListener(this::executeActionActionPerformed);

        listDefinitionButton = new JButton("listDefinition()");
        listDefinitionButton.addActionListener(this::listDefinitionButtonActionPerformed);

        listDefinitionAllButton = new JButton("listDefinition(\"*\")");
        listDefinitionAllButton.addActionListener(this::listDefinitionAllButtonActionPerformed);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(executeAction);
        buttonPanel.add(listDefinitionButton);
        buttonPanel.add(listDefinitionAllButton);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(jScrollPane2, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Right: execution log
        String[] cols = {"Time", "Action ID", "Execution ID", "Stage Type", "Success", "Step", "Comment"};
        executionLogModel = new DefaultTableModel(new Object[][]{}, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int col) {
                if (col == 4) {
                    return Boolean.class;
                }
                return String.class;
            }
        };
        JTable executionLogTable = new JTable(executionLogModel);
        executionLogTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        executionLogModel.addTableModelListener(
                e -> SwingUtilities.invokeLater(() -> TableUtils.packColumns(executionLogTable)));

        JLabel executionLogLabel = new JLabel("Execution Progress");
        executionLogLabel.setFont(executionLogLabel.getFont().deriveFont(Font.BOLD, 14f));
        executionLogLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(executionLogLabel, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(executionLogTable), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && splitPane.isShowing()) {
                splitPane.setDividerLocation(0.5);
            }
        });
        add(splitPane, BorderLayout.CENTER);
    }

    private void executeActionActionPerformed(java.awt.event.ActionEvent evt) {
        if (actionTable.getSelectedRow() == -1) {
            return;
        }

        ArchivePersistenceObject comObject = actionTable.getSelectedCOMObject();
        Long objIdDef = comObject.getObjectId();

        ActionDefinition actDef = (ActionDefinition) comObject.getObject();
        AttributeValueList argumentValueList;

        ActionArgumentsDialog dialog = new ActionArgumentsDialog(actDef);
        try {
            argumentValueList = dialog.getArgumentValues();
        } catch (InterruptedIOException ex) {
            return;
        }

        try {
            Long definitionObjId = actionTable.getSelectedDefinitionObjId();
            Logger.getLogger(ActionConsumerPanel.class.getName()).log(Level.INFO,
                    "Triggering action with id: " + definitionObjId);

            gma.launchAction(definitionObjId, argumentValueList, new ActionAdapter() {
                @Override
                public void executeActionResponseReceived(MALMessageHeader msgHeader,
                        Long executionId, Map qosProperties) {
                    JOptionPane.showMessageDialog(null,
                            "Action accepted. Execution ID: " + executionId,
                            "Success",
                            JOptionPane.PLAIN_MESSAGE);
                }

                @Override
                public void executeActionErrorReceived(MALMessageHeader msgHeader,
                        MOErrorException error, Map qosProperties) {
                    super.executeActionErrorReceived(msgHeader, error, qosProperties);
                    JOptionPane.showMessageDialog(
                            null,
                            "The submitted action failed: " + error.toString(),
                            "Error", JOptionPane.PLAIN_MESSAGE);
                }
            });
        } catch (NMFException ex) {
            JOptionPane.showMessageDialog(null, "There was an error with the submitted action.", "Error",
                    JOptionPane.PLAIN_MESSAGE);
            Logger.getLogger(ActionConsumerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void listDefinitionButtonActionPerformed(java.awt.event.ActionEvent evt) {
        IdentifierList actionNames = new IdentifierList();
        MOWindow actionNamesWindow = new MOWindow(actionNames, true);

        try {
            LongList ids;
            try {
                ids = this.serviceMCAction.getActionStub().listDefinition(
                        (IdentifierList) actionNamesWindow.getObject());
            } catch (InterruptedIOException ex) {
                return;
            }

            StringBuilder str = new StringBuilder("Definition ids on the provider: \n");
            if (ids != null) {
                for (Long objId : ids) {
                    str.append("Id Def: ").append(objId.toString()).append("\n");
                }
            }

            JOptionPane.showMessageDialog(null, str.toString(), "Returned List from the Provider",
                    JOptionPane.PLAIN_MESSAGE);

        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ActionConsumerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void listDefinitionAllButtonActionPerformed(java.awt.event.ActionEvent evt) {
        IdentifierList idList = new IdentifierList();
        idList.add(new Identifier("*"));

        try {
            this.serviceMCAction.getActionStub().asyncListDefinition(idList, new ActionAdapter() {
                @Override
                public void listDefinitionResponseReceived(MALMessageHeader msgHeader,
                        LongList definitionIds, Map qosProperties) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        actionTable.refreshTableWithIdsPairs(definitionIds,
                                serviceMCAction.getConnectionDetails().getDomain(),
                                ActionServiceInfo.ACTIONDEFINITION_OBJECT_TYPE);
                        Logger.getLogger(ActionConsumerPanel.class.getName()).log(Level.INFO,
                                "listDefinition(\"*\") returned {0} ids",
                                definitionIds.size());
                    });
                }

                @Override
                public void listDefinitionErrorReceived(MALMessageHeader msgHeader,
                        MOErrorException error, Map qosProperties) {
                    JOptionPane.showMessageDialog(null,
                            "There was an error during the listDefinition operation.",
                            "Error", JOptionPane.PLAIN_MESSAGE);
                    Logger.getLogger(ActionConsumerPanel.class.getName()).log(Level.SEVERE,
                            "There was an error during the listDefinition operation.", error);
                }
            });
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ActionConsumerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public class ActionConsumerAdapter extends ActionAdapter {

        @Override
        public void monitorExecutionNotifyReceived(MALMessageHeader msgHeader,
                Identifier subscriptionId,
                UpdateHeader updateHeader,
                ExecutionStageType stageType,
                Boolean success,
                UShort step,
                String comment,
                Map qosProperties) {

            final NullableAttributeList keys = updateHeader.getKeyValues();
            Long definitionId = null;
            Long executionId = null;

            if (keys != null && keys.size() >= 2) {
                if (keys.get(0) != null && keys.get(0).getValue() != null) {
                    definitionId = ((Union) keys.get(0).getValue()).getLongValue();
                }
                if (keys.get(1) != null && keys.get(1).getValue() != null) {
                    executionId = ((Union) keys.get(1).getValue()).getLongValue();
                }
            }

            final String timestamp = HelperTime.time2readableString(msgHeader.getTimestamp());
            final Long finalDefinitionId = definitionId;
            final Long finalExecutionId = executionId;
            final String finalStageType = stageType != null ? stageType.toString() : "";

            SwingUtilities.invokeLater(() -> executionLogModel.addRow(new Object[]{
                timestamp,
                finalDefinitionId,
                finalExecutionId,
                finalStageType,
                success,
                step,
                comment
            }));
        }

        @Override
        public void monitorExecutionRegisterAckReceived(MALMessageHeader msgHeader,
                Map qosProperties) {
            Logger.getLogger(ActionConsumerPanel.class.getName()).log(Level.INFO,
                    "Subscribed to monitorExecution successfully.");
        }

        @Override
        public void monitorExecutionRegisterErrorReceived(MALMessageHeader msgHeader,
                MOErrorException error, Map qosProperties) {
            Logger.getLogger(ActionConsumerPanel.class.getName()).log(Level.WARNING,
                    "Failed to subscribe to monitorExecution: {0}", error);
        }
    }

}
