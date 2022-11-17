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
package esa.mo.nmf.ctt.services.sm;

import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.EventCOMObject;
import esa.mo.com.impl.util.EventReceivedListener;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.sm.impl.consumer.CommandExecutorConsumerServiceImpl;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.softwaremanagement.commandexecutor.CommandExecutorHelper;
import org.ccsds.moims.mo.softwaremanagement.commandexecutor.consumer.CommandExecutorAdapter;
import org.ccsds.moims.mo.softwaremanagement.commandexecutor.structures.CommandDetails;

public class CommandExecutorConsumerPanel extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(CommandExecutorConsumerPanel.class.getName());
    private final CommandExecutorConsumerServiceImpl serviceSMCommandExecutor;
    private final CommandExecutorConsumerAdapter asyncAdapter = new CommandExecutorConsumerAdapter();
    private final CommandExecutorTablePanel recentCommandsTable;
    private final HashMap<Long, StringBuffer> outputBuffers = new HashMap<>();

    /**
     *
     * @param serviceSMCommandExecutor
     */
    public CommandExecutorConsumerPanel(CommandExecutorConsumerServiceImpl serviceSMCommandExecutor) {
        initComponents();

        recentCommandsTable = new CommandExecutorTablePanel(serviceSMCommandExecutor.getCOMServices()
            .getArchiveService());

        recentCommandsTable.getTable().addMouseListener(new MouseListener() {
            @Override
            public void mousePressed(MouseEvent evt) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                refreshOutputBufferWindow(null);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        jScrollPane2.setViewportView(recentCommandsTable);

        this.serviceSMCommandExecutor = serviceSMCommandExecutor;
    }

    public void init() {
        final Subscription subscription = HelperCOM.generateSubscriptionCOMEvent("SUB",
            CommandExecutorHelper.EXECUTIONFINISHED_OBJECT_TYPE);
        // Produce wildcard subscribtion to all event objects
        serviceSMCommandExecutor.getCOMServices().getEventService().addEventReceivedListener(subscription,
            new EventReceivedAdapter());
    }

    private void refreshOutputBufferWindow(Long justUpdatedObjId) {
        // If there is a concrete row selected...
        if (recentCommandsTable.getSelectedRow() != -1) {
            final Long objId = recentCommandsTable.getCOMObjects().get(recentCommandsTable.getSelectedRow())
                .getArchiveDetails().getInstId();
            if (justUpdatedObjId != null && !justUpdatedObjId.equals(objId)) {
                return;
            }
            StringBuffer outputBuf = outputBuffers.get(objId);
            if (outputBuf != null) {
                appVerboseTextArea.setText(outputBuffers.get(objId).toString());
            } else {
                appVerboseTextArea.setText("");
            }
            appVerboseTextArea.setCaretPosition(appVerboseTextArea.getDocument().getLength());
        }
    }

    /**
     * This method is called from within the constructor to initialize the formAddModifyParameter.
     * WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form
     * Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        appVerboseTextArea = new javax.swing.JTextArea();
        parameterTab = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        runAppButton = new javax.swing.JButton();
        listAppAllButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Command Executor Service");
        jLabel6.setToolTipText("");

        appVerboseTextArea.setColumns(20);
        appVerboseTextArea.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        appVerboseTextArea.setRows(5);
        jScrollPane1.setViewportView(appVerboseTextArea);

        parameterTab.setLayout(new javax.swing.BoxLayout(parameterTab, javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setMinimumSize(new java.awt.Dimension(419, 23));
        jPanel1.setPreferredSize(new java.awt.Dimension(419, 23));

        runAppButton.setText("runCommand");
        runAppButton.addActionListener(this::runCommandButtonActionPerformed);
        jPanel1.add(runAppButton);

        listAppAllButton.setText("List Recent Commands");
        listAppAllButton.setEnabled(false);
        listAppAllButton.addActionListener(this::listRecentCommandsButtonActionPerformed);
        jPanel1.add(listAppAllButton);

        parameterTab.add(jPanel1);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
            parameterTab, javax.swing.GroupLayout.DEFAULT_SIZE, 893, Short.MAX_VALUE).addComponent(jLabel6,
                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1).addComponent(jScrollPane2));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
            .createSequentialGroup().addContainerGap().addComponent(jLabel6).addPreferredGap(
                javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jScrollPane2,
                    javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jScrollPane1,
                javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(parameterTab,
                        javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)));
    }// </editor-fold>//GEN-END:initComponents

    private void listRecentCommandsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listRecentCommandsButtonActionPerformed
        /*
        LongList commandInstIds; // query archive
        appsTable.refreshTableWithIds(commandInstIds,
          serviceSMCommandExecutor.getConnectionDetails().getDomain(),
          CommandExecutorHelper.COMMAND_OBJECT_TYPE);
         */

    }//GEN-LAST:event_listRecentCommandsButtonActionPerformed

    private void runCommandButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runCommandButtonActionPerformed
        String commandText = JOptionPane.showInputDialog(this, "Please input the command:");
        if (commandText == null || commandText.isEmpty()) {
            // Either cancelled or didn't input any data
            return;
        }
        CommandDetails cd = new CommandDetails(commandText, null, null);
        try {
            serviceSMCommandExecutor.getCommandExecutorStub().asyncRunCommand(cd, asyncAdapter);
        } catch (MALInteractionException | MALException ex) {
            JOptionPane.showMessageDialog(null, "Error!\nException:\n" + ex + "\n" + ex.getMessage(), "Error!",
                JOptionPane.PLAIN_MESSAGE);
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_runCommandButtonActionPerformed

    public class EventReceivedAdapter extends EventReceivedListener {

        @Override
        public void onDataReceived(EventCOMObject eventCOMObject) {
            Element object = null;
            if (eventCOMObject.getBody() != null) {
                object = (Element) HelperAttributes.javaType2Attribute(eventCOMObject.getBody());
            }
            ObjectType objType = eventCOMObject.getObjType();

            String time = HelperTime.time2readableString(eventCOMObject.getTimestamp());
            Long sourceObjId = eventCOMObject.getSource().getKey().getInstId();
            if (sourceObjId == null) {
                LOGGER.log(Level.SEVERE,
                    "Missing source object in a received event (oID {0}). This should never happen.", eventCOMObject
                        .getObjId());
                return;
            }
            if (object == null) {
                LOGGER.log(Level.SEVERE, "Missing body in a received event (oID {0}). This should never happen.",
                    eventCOMObject.getObjId());
                return;
            }
            if (objType.equals(CommandExecutorHelper.EXECUTIONFINISHED_OBJECT_TYPE)) {
                recentCommandsTable.updateExitCode(sourceObjId, ((Union) object).getIntegerValue());
            } else if (objType.equals(CommandExecutorHelper.STANDARDOUTPUT_OBJECT_TYPE)) {
                addCommandOutput(sourceObjId, time + " stdout:\n" + object.toString());
            } else if (objType.equals(CommandExecutorHelper.STANDARDERROR_OBJECT_TYPE)) {
                addCommandOutput(sourceObjId, time + " stderr:\n" + object.toString());
            } else {
                LOGGER.log(Level.SEVERE, "Received an unsupported object type. {0}", objType.toString());
            }
        }

        private synchronized void addCommandOutput(Long sourceObjId, String data) {
            outputBuffers.computeIfAbsent(sourceObjId, k -> new StringBuffer());
            outputBuffers.get(sourceObjId).append(data);
            refreshOutputBufferWindow(sourceObjId);
        }
    }

    public class CommandExecutorConsumerAdapter extends CommandExecutorAdapter {

        @Override
        public void runCommandResponseReceived(MALMessageHeader msgHeader, Long commandInstId, Map qosProperties) {
            ArchivePersistenceObject comObj = HelperArchive.getArchiveCOMObject(serviceSMCommandExecutor
                .getCOMServices().getArchiveService().getArchiveStub(), CommandExecutorHelper.COMMAND_OBJECT_TYPE,
                serviceSMCommandExecutor.getConnectionDetails().getDomain(), commandInstId);
            if (comObj == null) {
                LOGGER.log(Level.SEVERE, "Retrieved null COM object for objInstId {0}", commandInstId);
            } else {
                recentCommandsTable.addEntry(new Identifier(commandInstId.toString()), comObj);
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea appVerboseTextArea;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton listAppAllButton;
    private javax.swing.JPanel parameterTab;
    private javax.swing.JButton runAppButton;
    // End of variables declaration//GEN-END:variables
}
