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
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.HeterogeneousList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.NullableAttributeList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherServiceInfo;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.consumer.AppsLauncherAdapter;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.structures.AppDetails;

/**
 * The AppsLauncherConsumerPanel class holds a panel to interact with an Apps
 * Launcher service.
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
     * Constructor.
     *
     * @param serviceSMAppsLauncher The Apps Launcher service consumer.
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
                int selectedRow = appsTable.getSelectedRow();
                // If there is a concrete row selected...
                if (selectedRow != -1) {
                    Long objId = appsTable.getCOMObjects().get(selectedRow).getArchiveDetails().getInstId();
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
        subscription = ConnectionConsumer.subscriptionWildcardRandom();
        try {
            serviceSMAppsLauncher.getAppsLauncherStub().monitorExecutionRegister(
                    subscription, new AppsLauncherConsumerAdapter());
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
            serviceSMAppsLauncher.getCOMServices().getEventService().getEventStub().monitorEventDeregister(launchAppEventListenerIds);
            launchAppEventListenerIds.clear();
        } catch (MALInteractionException | MALException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the
     * formAddModifyParameter. WARNING: Do NOT modify this code. The content of
     * this method is always regenerated by the Form Editor.
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
            this.serviceSMAppsLauncher.getAppsLauncherStub().asyncListApp(
                    idList, new Identifier("*"), new AppsLauncherAdapter() {
                @Override
                public void listAppResponseReceived(MALMessageHeader msgHeader, LongList appInstIds,
                        BooleanList running, Map qosProperties) {
                    appsTable.refreshTableWithIds(appInstIds,
                            serviceSMAppsLauncher.getConnectionDetails().getDomain(),
                            AppsLauncherServiceInfo.APP_OBJECT_TYPE);

                    for (int i = 0; i < appInstIds.size(); i++) {
                        Long objId = appInstIds.get(i);
                        outputBuffers.computeIfAbsent(objId, k -> new StringBuffer());
                    }

                    LOGGER.log(Level.INFO, "listApp(\"*\") returned {0} object instance identifiers",
                            appInstIds.size());
                }

                @Override
                public void listAppErrorReceived(MALMessageHeader msgHeader, MOErrorException error,
                        Map qosProperties) {
                    JOptionPane.showMessageDialog(null,
                            "There was an error during the listApp operation.",
                            "Error",
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
            appsTable.switchEnabledstatusForApp(false, objId.intValue());

            for (Long id : ids) {
                appsTable.reportStatus("Killed!", id.intValue());
            }
        } catch (MALInteractionException | MALException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error!\nException:\n" + ex + "\n" + ex.getMessage(),
                    "Error!", JOptionPane.PLAIN_MESSAGE);
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
            //appsTable.switchEnabledstatus(false);
        } catch (MALInteractionException | MALException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error!\nException:\n" + ex + "\n" + ex.getMessage(),
                    "Error!", JOptionPane.PLAIN_MESSAGE);
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
            appsTable.switchEnabledstatusForApp(true, objId.intValue());
            appsTable.reportStatus("Starting...", objId.intValue());
        } catch (MALInteractionException | MALException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error!\nException:\n" + ex + "\n" + ex.getMessage(),
                    "Error!", JOptionPane.PLAIN_MESSAGE);
            LOGGER.log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_runAppButtonActionPerformed

    private void subscribeToEvents(Long appId) {
        Identifier id = new Identifier(LAUNCH_APP_SUBSCRIPTION + appId.toString());
        Subscription subscription = ConnectionConsumer.subscriptionWildcardRandom();
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
        public void monitorExecutionNotifyReceived(MALMessageHeader msgHeader,
                Identifier subscriptionId, UpdateHeader updateHeader,
                String outputStream, java.util.Map qosProperties) {

            final String out = outputStream;
            NullableAttributeList keyValues = updateHeader.getKeyValues();
            Identifier appName = (Identifier) keyValues.get(0).getValue();
            Union appObjId = (Union) keyValues.get(1).getValue();
            LOGGER.log(Level.WARNING,
                    "Received output for App Name: {0} (appObjId: {1})",
                    new Object[]{appName.getValue(), appObjId.getLongValue()});

            StringBuffer stringBuf = outputBuffers.get(appObjId.getLongValue());
            stringBuf.append(out);
            appVerboseTextArea.append(out);
            appVerboseTextArea.setCaretPosition(appVerboseTextArea.getDocument().getLength());
        }
    }

    public class StopAdapter extends AppsLauncherAdapter {

        LongList apids;

        public StopAdapter(LongList apids) {
            this.apids = apids;
        }

        @Override
        public void stopAppAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
            for (Long apid : apids) {
                appsTable.reportStatus("Stop ACK received...", apid.intValue());
            }
        }

        @Override
        public void stopAppUpdateReceived(MALMessageHeader msgHeader,
                Long appClosing, Map qosProperties) {
            appsTable.reportStatus("Stopped!", appClosing.intValue());
            appsTable.switchEnabledstatusForApp(false, appClosing.intValue());
        }

        @Override
        public void stopAppAckErrorReceived(MALMessageHeader msgHeader,
                org.ccsds.moims.mo.mal.MOErrorException error, Map qosProperties) {
            Object extrainfo = error.getExtraInformation();
            if (extrainfo != null) {
                Long objId = (Long) extrainfo;
                appsTable.reportStatus("Error: App not stopped!", objId.intValue());
            } else {
                for (Long apid : apids) {
                    appsTable.reportStatus("Error: App not stopped!", apid.intValue());
                }
            }
            LOGGER.log(Level.SEVERE, "The App was not stopped!", error);
        }

        @Override
        public void stopAppResponseReceived(MALMessageHeader msgHeader, Map qosProperties) {
            for (Long apid : apids) {
                appsTable.reportStatus("Stop App Completed.", apid.intValue());
                appsTable.switchEnabledstatusForApp(false, apid.intValue());
            }
        }

    }

    private class AppLaunchEventAdapter extends EventAdapter {

        private Long originalObjId;

        public AppLaunchEventAdapter(Long objId) {
            originalObjId = objId;
        }

        @Override
        public synchronized void monitorEventNotifyReceived(
                org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
                org.ccsds.moims.mo.mal.structures.Identifier subscriptionId,
                org.ccsds.moims.mo.mal.structures.UpdateHeader updateHeader,
                org.ccsds.moims.mo.com.structures.ObjectLinks eventLinks,
                org.ccsds.moims.mo.mal.structures.Element eventBody,
                java.util.Map qosProperties) {
            // Does the objId received matches the one that we originally sent to the service?
            if (originalObjId.equals(eventLinks.getSource().getInstId())) {
                ObjectId obj = eventLinks.getSource();
                updateAppStatus(obj.getInstId(), "Running", "Failed to start!");
            }
        }
    }

    private void updateAppStatus(Long appId, String runningText, String notRunningText) {
        LongList ids = new LongList();
        ids.add(appId);
        try {
            int rowId = appsTable.findIndex(appId.intValue());
            ArchiveAdapter adapter = new ArchiveAdapter() {
                @Override
                public void retrieveResponseReceived(MALMessageHeader msgHeader,
                        ArchiveDetailsList objDetails, HeterogeneousList objBodies, Map qosProperties) {
                    boolean appIsRunning = ((AppDetails) objBodies.get(0)).getRunning();
                    String text = appIsRunning ? runningText : notRunningText;
                    appsTable.reportStatus(text, appId.intValue());
                    appsTable.switchEnabledstatus(appIsRunning, rowId);
                }
            };
            serviceSMAppsLauncher.getCOMServices().getArchiveService().getArchiveStub().retrieve(
                    AppsLauncherServiceInfo.APP_OBJECT_TYPE,
                    serviceSMAppsLauncher.getConnectionDetails().getDomain(),
                    ids,
                    adapter);
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Something went wrong...", ex);
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
