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

import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.sm.impl.consumer.AppsLauncherConsumerServiceImpl;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.event.consumer.EventAdapter;
import org.ccsds.moims.mo.com.structures.ObjectDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.consumer.AppsLauncherAdapter;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.structures.AppDetails;

/**
 *
 * @author Cesar Coelho
 */
public class AppsLauncherConsumerPanel extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(AppsLauncherConsumerPanel.class.getName());
    private final AppsLauncherConsumerServiceImpl serviceSMAppsLauncher;
    private AppsLauncherTablePanel appsTable;
    private final HashMap<Long, StringBuffer> outputBuffers = new HashMap<>();
    private Subscription subscription;
    private String LAUNCH_APP_SUBSCRIPTION = "LaunchApp_";
    private IdentifierList launchAppEventListenerIds = new IdentifierList();

    /**
     *
     * @param serviceSMAppsLauncher
     */
    public AppsLauncherConsumerPanel(AppsLauncherConsumerServiceImpl serviceSMAppsLauncher) {
        initComponents();

        appsTable = new AppsLauncherTablePanel(serviceSMAppsLauncher.getCOMServices().getArchiveService());

        appsTable.getTable().addMouseListener(new MouseListener() {
            @Override
            public void mousePressed(MouseEvent evt) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // If there is a concrete row selected...
                if (appsTable.getSelectedRow() != -1) {
                    final Long objId = appsTable.getCOMObjects().get(appsTable.getSelectedRow()).getArchiveDetails()
                        .getInstId();
                    appVerboseTextArea.setText(outputBuffers.get(objId).toString());
                    appVerboseTextArea.setCaretPosition(appVerboseTextArea.getDocument().getLength());
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        jScrollPane2.setViewportView(appsTable);

        this.serviceSMAppsLauncher = serviceSMAppsLauncher;
    }

    public void init() {
        this.listAppAllButtonActionPerformed(null);

        // Subscribe to Apps
        subscription = ConnectionConsumer.subscriptionWildcard();
        try {
            serviceSMAppsLauncher.getAppsLauncherStub().monitorExecutionRegister(subscription,
                new AppsLauncherConsumerAdapter());
        } catch (MALInteractionException | MALException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void removeNotify() {
        super.removeNotify();
        IdentifierList ids = new IdentifierList();
        ids.add(subscription.getSubscriptionId());
        try {
            serviceSMAppsLauncher.getAppsLauncherStub().monitorExecutionDeregister(ids);
        } catch (MALInteractionException | MALException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        try {
            serviceSMAppsLauncher.getCOMServices().getEventService().getEventStub().monitorEventDeregister(
                launchAppEventListenerIds);
            launchAppEventListenerIds.clear();
        } catch (MALInteractionException | MALException ex) {
            LOGGER.log(Level.WARNING, null, ex);
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
        stopAppButton = new javax.swing.JButton();
        killAppButton = new javax.swing.JButton();
        listAppAllButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Apps Launcher Service");
        jLabel6.setToolTipText("");

        appVerboseTextArea.setColumns(20);
        appVerboseTextArea.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        appVerboseTextArea.setRows(5);
        jScrollPane1.setViewportView(appVerboseTextArea);

        parameterTab.setLayout(new javax.swing.BoxLayout(parameterTab, javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setMinimumSize(new java.awt.Dimension(419, 23));
        jPanel1.setPreferredSize(new java.awt.Dimension(419, 23));

        runAppButton.setText("runApp");
        runAppButton.addActionListener(this::runAppButtonActionPerformed);
        jPanel1.add(runAppButton);

        stopAppButton.setText("stopApp");
        stopAppButton.addActionListener(this::stopAppButtonActionPerformed);
        jPanel1.add(stopAppButton);

        killAppButton.setText("killApp");
        killAppButton.addActionListener(this::killAppButtonActionPerformed);
        jPanel1.add(killAppButton);

        listAppAllButton.setText("listApp(\"*\")");
        listAppAllButton.addActionListener(this::listAppAllButtonActionPerformed);
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

    private void listAppAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listAppAllButtonActionPerformed
        IdentifierList idList = new IdentifierList();
        idList.add(new Identifier("*"));

        try {
            this.serviceSMAppsLauncher.getAppsLauncherStub().asyncListApp(idList, new Identifier("*"),
                new AppsLauncherAdapter() {
                    @Override
                    public void listAppResponseReceived(MALMessageHeader msgHeader, LongList appInstIds,
                        BooleanList running, Map qosProperties) {
                        appsTable.refreshTableWithIds(appInstIds, serviceSMAppsLauncher.getConnectionDetails()
                            .getDomain(), AppsLauncherHelper.APP_OBJECT_TYPE);

                        for (int i = 0; i < appInstIds.size(); i++) {
                            Long objId = appInstIds.get(i);

                            outputBuffers.computeIfAbsent(objId, k -> new StringBuffer());
                        }

                        LOGGER.log(Level.INFO, "listApp(\"*\") returned {0} object instance identifiers", appInstIds
                            .size());
                    }

                    @Override
                    public void listAppErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
                        Map qosProperties) {
                        JOptionPane.showMessageDialog(null, "There was an error during the listApp operation.", "Error",
                            JOptionPane.PLAIN_MESSAGE);
                        LOGGER.log(Level.SEVERE, null, error);
                    }
                });
        } catch (MALInteractionException | MALException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_listAppAllButtonActionPerformed

    private void killAppButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_killAppButtonActionPerformed
        if (appsTable.getSelectedRow() == -1) { // The row is not selected?
            return;  // Well, then nothing to be done here folks!
        }

        LongList ids = new LongList();
        Long objId = appsTable.getSelectedCOMObject().getArchiveDetails().getInstId();
        ids.add(objId);

        unsubscribeFromPreviousEvents(objId);

        try {
            this.serviceSMAppsLauncher.getAppsLauncherStub().killApp(ids);
            appsTable.switchEnabledstatus(false);

            for (Long id : ids) {
                appsTable.reportStatus("Killed!", id.intValue());
            }
        } catch (MALInteractionException | MALException ex) {
            JOptionPane.showMessageDialog(null, "Error!\nException:\n" + ex + "\n" + ex.getMessage(), "Error!",
                JOptionPane.PLAIN_MESSAGE);
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_killAppButtonActionPerformed

    private void stopAppButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopAppButtonActionPerformed

        if (appsTable.getSelectedRow() == -1) { // The row is not selected?
            return;  // Well, then nothing to be done here folks!
        }
        LongList ids = new LongList();
        Long objId = appsTable.getSelectedCOMObject().getArchiveDetails().getInstId();
        ids.add(objId);

        unsubscribeFromPreviousEvents(objId);

        try {
            for (Long id : ids) {
                appsTable.reportStatus("Sending stop request.", id.intValue());
            }
            this.serviceSMAppsLauncher.getAppsLauncherStub().stopApp(ids, new StopAdapter(ids));
            appsTable.switchEnabledstatus(false);

        } catch (MALInteractionException | MALException ex) {
            JOptionPane.showMessageDialog(null, "Error!\nException:\n" + ex + "\n" + ex.getMessage(), "Error!",
                JOptionPane.PLAIN_MESSAGE);
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_stopAppButtonActionPerformed

    private void runAppButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runAppButtonActionPerformed
        if (appsTable.getSelectedRow() == -1) { // The row is not selected?
            return;  // Well, then nothing to be done here folks!
        }

        LongList ids = new LongList();
        Long objId = appsTable.getSelectedCOMObject().getArchiveDetails().getInstId();
        ids.add(objId);

        unsubscribeFromPreviousEvents(objId);

        try {
            subscribeToEvents(objId);
            this.serviceSMAppsLauncher.getAppsLauncherStub().runApp(ids);
            appsTable.switchEnabledstatus(true);
            appsTable.reportStatus("Starting...", objId.intValue());
        } catch (MALInteractionException | MALException ex) {
            JOptionPane.showMessageDialog(null, "Error!\nException:\n" + ex + "\n" + ex.getMessage(), "Error!",
                JOptionPane.PLAIN_MESSAGE);
            LOGGER.log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_runAppButtonActionPerformed

    private void subscribeToEvents(Long appId) {
        Identifier id = new Identifier(LAUNCH_APP_SUBSCRIPTION + appId.toString());
        Subscription subscription = ConnectionConsumer.subscriptionWildcard();
        try {
            serviceSMAppsLauncher.getCOMServices().getEventService().getEventStub().monitorEventRegister(subscription,
                new AppLaunchEventAdapter(appId));
            launchAppEventListenerIds.add(id);
        } catch (MALInteractionException | MALException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
    }

    private void unsubscribeFromPreviousEvents(Long appId) {
        Identifier id = new Identifier(LAUNCH_APP_SUBSCRIPTION + appId.toString());
        if (launchAppEventListenerIds.contains(id)) {
            IdentifierList subIds = new IdentifierList();
            subIds.add(id);
            try {
                serviceSMAppsLauncher.getCOMServices().getEventService().getEventStub().monitorEventDeregister(subIds);
                launchAppEventListenerIds.remove(id);
            } catch (MALInteractionException | MALException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }
        }
    }

    public class AppsLauncherConsumerAdapter extends AppsLauncherAdapter {

        @Override
        public void monitorExecutionNotifyReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
            org.ccsds.moims.mo.mal.structures.Identifier _Identifier0,
            org.ccsds.moims.mo.mal.structures.UpdateHeaderList updateHeaderList,
            org.ccsds.moims.mo.mal.structures.StringList outputStream, java.util.Map qosProperties) {

            for (int i = 0; i < updateHeaderList.size(); i++) {
                final String out = outputStream.get(i);
                final UpdateHeader updateHeader = updateHeaderList.get(i);

                StringBuffer stringBuf = outputBuffers.get(updateHeader.getKey().getSecondSubKey());
                stringBuf.append(out);
                appVerboseTextArea.append(out);
                appVerboseTextArea.setCaretPosition(appVerboseTextArea.getDocument().getLength());
            }
        }
    }

    public class StopAdapter extends AppsLauncherAdapter {
        LongList apids;

        public StopAdapter(LongList apids) {
            this.apids = apids;
        }

        @Override
        public void stopAppAckReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
            java.util.Map qosProperties) {
            for (Long apid : apids) {
                appsTable.reportStatus("Stop ACK received...", apid.intValue());
            }
        }

        @Override
        public void stopAppUpdateReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, Long appClosing,
            java.util.Map qosProperties) {
            appsTable.reportStatus("Stopped!", appClosing.intValue());
        }

        @Override
        public void stopAppAckErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
            org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties) {
            for (Long apid : apids) {
                appsTable.reportStatus("Stop App Error..." + error.toString(), apid.intValue());
            }
        }

        @Override
        public void stopAppResponseReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
            java.util.Map qosProperties) {
            for (Long apid : apids) {
                appsTable.reportStatus("Stop App Completed.", apid.intValue());
            }
        }

    }

    private class AppLaunchEventAdapter extends EventAdapter {

        private Long originalObjId;

        public AppLaunchEventAdapter(Long objId) {
            originalObjId = objId;
        }

        @Override
        public synchronized void monitorEventNotifyReceived(MALMessageHeader msgHeader, Identifier _Identifier0,
            UpdateHeaderList updateHeaderList, ObjectDetailsList objectDetailsList, ElementList objects,
            Map qosProperties) {
            if (objectDetailsList.size() != 1) {
                return;
            }

            // Does the objId received matches the one that we originally sent to the service?
            if (originalObjId.equals(objectDetailsList.get(0).getSource().getKey().getInstId())) {
                ObjectId obj = objectDetailsList.get(0).getSource();
                updateAppStatus(obj.getKey().getInstId(), "Running", "Failed to start!");
            }
        }
    }

    private void updateAppStatus(Long appId, String runningText, String notRunningText) {
        LongList ids = new LongList();
        ids.add(appId);
        try {
            int rowId = appsTable.findIndex(appId.intValue());
            serviceSMAppsLauncher.getCOMServices().getArchiveService().getArchiveStub().retrieve(
                AppsLauncherHelper.APP_OBJECT_TYPE, serviceSMAppsLauncher.getConnectionDetails().getDomain(), ids,
                new ArchiveAdapter() {
                    @Override
                    public void retrieveResponseReceived(MALMessageHeader msgHeader, ArchiveDetailsList objDetails,
                        ElementList objBodies, Map qosProperties) {
                        boolean appIsRunning = ((AppDetails) objBodies.get(0)).getRunning();
                        appsTable.reportStatus((appIsRunning ? runningText : notRunningText), appId.intValue());
                        appsTable.switchEnabledstatus(appIsRunning, rowId);
                    }
                });
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea appVerboseTextArea;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton killAppButton;
    private javax.swing.JButton listAppAllButton;
    private javax.swing.JPanel parameterTab;
    private javax.swing.JButton runAppButton;
    private javax.swing.JButton stopAppButton;
    // End of variables declaration//GEN-END:variables
}
