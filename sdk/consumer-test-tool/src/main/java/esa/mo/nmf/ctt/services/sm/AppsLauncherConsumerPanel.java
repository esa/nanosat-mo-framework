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
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import esa.mo.nmf.ctt.utils.DirectoryConnectionConsumerPanel;
import org.ccsds.moims.mo.com.structures.Provider;
import org.ccsds.moims.mo.com.structures.ProviderList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.sm.appslauncher.AppsLauncherServiceInfo;
import org.ccsds.moims.mo.sm.appslauncher.consumer.AppsLauncherAdapter;
import org.ccsds.moims.mo.sm.appslauncher.consumer.MonitorEventsSubscriptionKeys;
import org.ccsds.moims.mo.sm.appslauncher.consumer.MonitorExecutionSubscriptionKeys;
import org.ccsds.moims.mo.sm.structures.AppEventType;

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

    /**
     * The URI of the Directory service of each App that has reported one, by
     * App instance id. An App prints it once it is up, so it only appears here
     * after the App has finished starting.
     */
    private final HashMap<Long, String> appDirectoryURIs = new HashMap<>();

    /**
     * The name that an App gives to the provider of its Directory service. An
     * App builds it as its own name followed by the name of the service, so the
     * URI of the Directory of the App called "hello-world" ends in
     * "/hello-world-Directory". See ConnectionProvider, which builds it.
     */
    private static final String DIRECTORY_SERVICE = "-Directory";

    /**
     * Matches the URI of the Directory service of one named App, in the output
     * that the App prints.
     *
     * The name of the App is part of the pattern on purpose. The output carries
     * other URIs that must not be taken for the App: the Central Directory of
     * the supervisor, which the App reports while registering itself, and the
     * URIs of the connections that the App closes while shutting down.
     *
     * @param appName The name of the App.
     * @return The pattern that matches the URI of the Directory of that App.
     */
    private static java.util.regex.Pattern directoryURIPattern(final String appName) {
        return java.util.regex.Pattern.compile("(?:mal[a-z]*|rmi)://\\S*/"
                + java.util.regex.Pattern.quote(appName + DIRECTORY_SERVICE) + "\\b");
    }
    private Subscription subscription;
    private Subscription eventsSubscription;

    /**
     * Constructor.
     *
     * @param serviceSMAppsLauncher The Apps Launcher service consumer.
     */
    public AppsLauncherConsumerPanel(AppsLauncherConsumerServiceImpl serviceSMAppsLauncher) {
        initComponents();
        jLabel6.setFont(jLabel6.getFont().deriveFont(java.awt.Font.BOLD, 18f));

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
                    Long objId = appsTable.getCOMObjects().get(selectedRow).getArchiveDetails().getId();
                    appVerboseTextArea.setText(outputBuffers.get(objId).toString());
                    appVerboseTextArea.setCaretPosition(appVerboseTextArea.getDocument().getLength());
                    refreshConnectButton();
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

    /**
     * Initializes the panel and subscribes to the AppsLauncher execution monitoring and events.
     */
    public void init() {
        this.listAppAllButtonActionPerformed(null);

        subscription = ConnectionConsumer.subscriptionWildcardRandom();
        try {
            serviceSMAppsLauncher.getAppsLauncherStub().monitorExecutionRegister(
                    subscription, new AppsLauncherConsumerAdapter());
        } catch (MALInteractionException | MALException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        eventsSubscription = ConnectionConsumer.subscriptionWildcardRandom();
        try {
            serviceSMAppsLauncher.getAppsLauncherStub().monitorEventsRegister(
                    eventsSubscription, new MonitorEventsConsumerAdapter());
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

        IdentifierList eventsIds = new IdentifierList();
        eventsIds.add(eventsSubscription.getSubscriptionId());
        try {
            serviceSMAppsLauncher.getAppsLauncherStub().monitorEventsDeregister(eventsIds);
        } catch (MALInteractionException | MALException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
    }

    /**
     * Records the URI of the Directory service of an App, taken from the output
     * that the App itself prints once it is up.
     *
     * @param appId The instance id of the App.
     * @param appName The name of the App, which its own URI carries.
     * @param output A piece of the output of that App.
     */
    private void rememberDirectoryURI(final Long appId, final String appName, final String output) {
        java.util.regex.Matcher matcher = directoryURIPattern(appName).matcher(output);
        String uri = null;

        while (matcher.find()) {
            uri = matcher.group(); // The last one wins: it is the most recent
        }

        if (uri != null) {
            appDirectoryURIs.put(appId, uri);
        }
    }

    /**
     * Forgets where an App was reachable, so that a stopped App is not offered
     * for connection. An App that is started again reports its URI once more,
     * which brings the button back by itself.
     *
     * @param appId The instance id of the App.
     */
    private void forgetDirectoryURI(final Long appId) {
        appDirectoryURIs.remove(appId);
        refreshConnectButton();
    }

    /**
     * Returns the instance id of the App on the selected row, or null when no
     * row is selected.
     */
    private Long selectedAppId() {
        int selectedRow = appsTable.getSelectedRow();

        if (selectedRow == -1) {
            return null;
        }

        return appsTable.getCOMObjects().get(selectedRow).getArchiveDetails().getId();
    }

    /**
     * Enables the connect button only while the selected App has told us where
     * its Directory service is.
     */
    private void refreshConnectButton() {
        Long appId = selectedAppId();
        connectAppButton.setEnabled(appId != null && appDirectoryURIs.containsKey(appId));
    }

    /**
     * Opens a tab for the App on the selected row, by asking the Directory
     * service that the App runs for the providers it holds.
     *
     * This is the same connection that the Directory tab makes, so an App is
     * reached without having to go back there and look it up by hand.
     */
    private void connectAppButtonActionPerformed(java.awt.event.ActionEvent evt) {
        final Long appId = selectedAppId();

        if (appId == null) {
            return;
        }

        final String uri = appDirectoryURIs.get(appId);

        if (uri == null) {
            return;
        }

        final javax.swing.JTabbedPane rootTabs = findRootTabs();

        if (rootTabs == null) {
            LOGGER.log(Level.WARNING, "The tabs of the tool could not be found.");
            return;
        }

        Thread thread = new Thread(() -> {
            try {
                ProviderList providers
                        = GroundMOAdapterImpl.retrieveProvidersFromDirectory(new URI(uri));

                if (providers.isEmpty()) {
                    javax.swing.SwingUtilities.invokeLater(()
                            -> JOptionPane.showMessageDialog(this,
                                    "The App did not register any provider on:\n" + uri,
                                    "Nothing to connect to", JOptionPane.PLAIN_MESSAGE));
                    return;
                }

                for (Provider provider : providers) {
                    DirectoryConnectionConsumerPanel.openProviderTab(rootTabs, provider);
                }
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Could not connect to the App on: " + uri, ex);
                javax.swing.SwingUtilities.invokeLater(()
                        -> JOptionPane.showMessageDialog(this,
                                "Could not connect to the App on:\n" + uri
                                + "\n\nException:\n" + ex,
                                "Error!", JOptionPane.PLAIN_MESSAGE));
            }
        });
        thread.setName("ConnectToAppThread");
        thread.start();
    }

    /**
     * Returns the outermost tabbed pane of the tool, which is the one that holds
     * a tab per connected provider. This panel sits inside the service tabs of a
     * provider, so the pane it is looking for is further up than its own.
     */
    private javax.swing.JTabbedPane findRootTabs() {
        javax.swing.JTabbedPane outermost = null;

        for (java.awt.Container parent = this.getParent(); parent != null; parent = parent.getParent()) {
            if (parent instanceof javax.swing.JTabbedPane) {
                outermost = (javax.swing.JTabbedPane) parent;
            }
        }

        return outermost;
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
        connectAppButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();

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

        connectAppButton.setText("connect to App");
        connectAppButton.setToolTipText("Opens a tab for the selected App. "
                + "Available once the App has reported the URI of its Directory service.");
        connectAppButton.setEnabled(false);
        connectAppButton.addActionListener(this::connectAppButtonActionPerformed);
        jPanel1.add(connectAppButton);

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
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        appsTable.refreshTableWithIds(appInstIds,
                                serviceSMAppsLauncher.getConnectionDetails().getDomain(),
                                AppsLauncherServiceInfo.APPDETAILS_OBJECT_TYPE);

                        for (int i = 0; i < appInstIds.size(); i++) {
                            Long objId = appInstIds.get(i);
                            outputBuffers.computeIfAbsent(objId, k -> new StringBuffer());
                        }

                        LOGGER.log(Level.INFO, "listApp(\"*\") returned {0} object instance identifiers",
                                appInstIds.size());
                    });
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
        Long objId = appsTable.getSelectedCOMObject().getArchiveDetails().getId();
        ids.add(objId);

        try {
            this.serviceSMAppsLauncher.getAppsLauncherStub().killApp(ids);
            appsTable.switchEnabledstatusForApp(false, objId.intValue());
            forgetDirectoryURI(objId);

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
        Long objId = appsTable.getSelectedCOMObject().getArchiveDetails().getId();
        ids.add(objId);

        try {
            for (Long id : ids) {
                appsTable.reportStatus("Sending stop request.", id.intValue());
            }
            this.serviceSMAppsLauncher.getAppsLauncherStub().stopApp(ids, null, new StopAdapter(ids));
            forgetDirectoryURI(objId);
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
        Long objId = appsTable.getSelectedCOMObject().getArchiveDetails().getId();
        ids.add(objId);

        try {
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

    /**
     * Adapter receiving app execution-output notifications and appending them to the log.
     */
    public class AppsLauncherConsumerAdapter extends AppsLauncherAdapter {

        /**
         * Default constructor.
         */
        public AppsLauncherConsumerAdapter() {
        }

        @Override
        public void monitorExecutionNotifyReceived(MALMessageHeader msgHeader,
                Identifier subscriptionId, UpdateHeader updateHeader,
                MonitorExecutionSubscriptionKeys keys,
                String outputStream, java.util.Map qosProperties) {

            final String out = outputStream;
            Identifier appName = keys.getAppName();
            Long appId = keys.getAppId();
            LOGGER.log(Level.FINE,
                    "Received output for App Name: {0} (appId: {1})",
                    new Object[]{appName.getValue(), appId});

            StringBuffer stringBuf = outputBuffers.get(appId);
            stringBuf.append(out);
            rememberDirectoryURI(appId, appName.getValue(), out);
            javax.swing.SwingUtilities.invokeLater(() -> {
                appVerboseTextArea.append(out);
                appVerboseTextArea.setCaretPosition(appVerboseTextArea.getDocument().getLength());
                refreshConnectButton();
            });
        }
    }

    /**
     * Adapter that tracks the acknowledgements of a stop-app request for a set of apps.
     */
    public class StopAdapter extends AppsLauncherAdapter {

        LongList apids;

        /**
         * Creates the stop adapter for the given app instance ids.
         *
         * @param apids the instance ids of the apps being stopped
         */
        public StopAdapter(LongList apids) {
            this.apids = apids;
        }

        @Override
        public void stopAppAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                for (Long apid : apids) {
                    appsTable.reportStatus("Stop ACK received...", apid.intValue());
                }
            });
        }

        @Override
        public void stopAppUpdateReceived(MALMessageHeader msgHeader,
                Long appClosing, Map qosProperties) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                appsTable.reportStatus("Stopped!", appClosing.intValue());
                appsTable.switchEnabledstatusForApp(false, appClosing.intValue());
            });
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
            javax.swing.SwingUtilities.invokeLater(() -> {
                for (Long apid : apids) {
                    appsTable.reportStatus("Stop App Completed.", apid.intValue());
                    appsTable.switchEnabledstatusForApp(false, apid.intValue());
                }
            });
        }

    }

    /**
     * Adapter receiving app lifecycle events and refreshing the app statuses in the table.
     */
    public class MonitorEventsConsumerAdapter extends AppsLauncherAdapter {

        /**
         * Default constructor.
         */
        public MonitorEventsConsumerAdapter() {
        }

        @Override
        public void monitorEventsNotifyReceived(MALMessageHeader msgHeader,
                Identifier subscriptionId, UpdateHeader updateHeader,
                MonitorEventsSubscriptionKeys keys,
                AppEventType eventType, Integer exitCode, String extraInfo,
                Map qosProperties) {
            Identifier appName = keys.getAppName();
            Long appId = keys.getAppId();
            LOGGER.log(Level.INFO, "App lifecycle event for {0}: {1}",
                    new Object[]{appName, eventType});
            String statusText = formatEventStatus(eventType, exitCode);
            boolean stopped = eventType == AppEventType.STOPPED
                    || eventType == AppEventType.KILLED
                    || eventType == AppEventType.EXITED
                    || eventType == AppEventType.CRASHED;
            final Long finalAppId = appId;
            javax.swing.SwingUtilities.invokeLater(() -> {
                appsTable.reportStatus(statusText, finalAppId.intValue());
                if (stopped) {
                    appsTable.switchEnabledstatusForApp(false, finalAppId.intValue());
                }
            });
        }

        private String formatEventStatus(AppEventType eventType, Integer exitCode) {
            if (eventType == AppEventType.START_REQUESTED) return "Starting...";
            if (eventType == AppEventType.STARTED)         return "Running";
            if (eventType == AppEventType.STOP_REQUESTED)  return "Stopping...";
            if (eventType == AppEventType.STOPPED) return "Stopped (exit: " + exitCode + ")";
            if (eventType == AppEventType.KILLED)  return "Killed (exit: " + exitCode + ")";
            if (eventType == AppEventType.EXITED)  return "Exited (exit: " + exitCode + ")";
            if (eventType == AppEventType.CRASHED) return "Crashed (exit: " + exitCode + ")";
            return eventType.toString();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea appVerboseTextArea;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton killAppButton;
    private javax.swing.JButton connectAppButton;
    private javax.swing.JButton listAppAllButton;
    private javax.swing.JPanel parameterTab;
    private javax.swing.JButton runAppButton;
    private javax.swing.JButton stopAppButton;
    // End of variables declaration//GEN-END:variables
}
