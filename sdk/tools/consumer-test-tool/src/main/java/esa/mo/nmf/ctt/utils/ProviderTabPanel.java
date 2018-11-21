/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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
package esa.mo.nmf.ctt.utils;

import esa.mo.nmf.ctt.services.com.ArchiveConsumerManagerPanel;
import esa.mo.nmf.ctt.services.com.EventConsumerPanel;
import esa.mo.nmf.ctt.services.common.ConfigurationConsumerPanel;
import esa.mo.nmf.ctt.services.mc.ActionConsumerPanel;
import esa.mo.nmf.ctt.services.mc.AggregationConsumerPanel;
import esa.mo.nmf.ctt.services.mc.AlertConsumerPanel;
import esa.mo.nmf.ctt.services.mc.CheckConsumerPanel;
import esa.mo.nmf.ctt.services.mc.ParameterConsumerPanel;
import esa.mo.nmf.ctt.services.mc.ParameterPublishedValues;
import esa.mo.nmf.ctt.services.mc.StatisticConsumerPanel;
import esa.mo.nmf.ctt.services.sm.AppsLauncherConsumerPanel;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.nmf.ctt.services.sm.CommandExecutorConsumerPanel;
import esa.mo.nmf.ctt.services.sm.PackageManagementConsumerPanel;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import esa.mo.sm.impl.consumer.HeartbeatConsumerServiceImpl;
import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTabbedPane;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.softwaremanagement.heartbeat.consumer.HeartbeatAdapter;

/**
 *
 * @author Cesar Coelho
 */
public class ProviderTabPanel extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(ProviderTabPanel.class.getName());
    protected final GroundMOAdapterImpl services;

    /**
     * Creates a new tab for a Provider and populates it.
     *
     * @param provider
     */
    public ProviderTabPanel(final ProviderSummary provider) {
        services = new GroundMOAdapterImpl(provider);
        initComponents();
    }

    public GroundMOAdapterImpl getServices() {
        return this.services;
    }

    public void insertServicesTabs() {
        startTabs();
    }

    public JTabbedPane getTabs() {
        return serviceTabs;
    }

    protected void startTabs() {
        try {
            // Common
            if (services.getCommonServices() != null) {
                if (services.getCommonServices().getConfigurationService() != null) {
                    ConfigurationConsumerPanel panel = new ConfigurationConsumerPanel(services.getCommonServices().getConfigurationService());
                    int count = serviceTabs.getTabCount();
                    serviceTabs.insertTab("Configuration service", null, panel, "Configuration Tab", count);
                }
            }

            // Software Management
            if (services.getSMServices() != null) {
                if (services.getSMServices().getHeartbeatService() != null) {
                    HeartbeatConsumerServiceImpl heartbeat = services.getSMServices().getHeartbeatService();
                    ProviderStatusAdapter providerStatusAdapter = new ProviderStatusAdapter(heartbeat);
                    heartbeat.startListening(providerStatusAdapter);
                } else {
                    status.setText("Heartbeat service not available.");
                }

                if (services.getSMServices().getAppsLauncherService() != null) {
                    AppsLauncherConsumerPanel panel = new AppsLauncherConsumerPanel(services.getSMServices().getAppsLauncherService());
                    int count = serviceTabs.getTabCount();
                    serviceTabs.insertTab("Apps Launcher service", null, panel, "Apps Launcher Tab", count);
                    panel.init();
                }

                if (services.getSMServices().getCommandExecutorService()!= null) {
                    CommandExecutorConsumerPanel panel = new CommandExecutorConsumerPanel(services.getSMServices().getCommandExecutorService());
                    int count = serviceTabs.getTabCount();
                    serviceTabs.insertTab("Command Executor service", null, panel, "Command Executor Tab", count);
                    panel.init();
                }

                if (services.getSMServices().getPackageManagementService() != null) {
                    PackageManagementConsumerPanel panel = new PackageManagementConsumerPanel(services.getSMServices().getPackageManagementService());
                    int count = serviceTabs.getTabCount();
                    serviceTabs.insertTab("Package Management service", null, panel, "Package Management Tab", count);
                    panel.init();
                }
            }

            // COM
            if (services.getCOMServices() != null) {
                if (services.getCOMServices().getArchiveService() != null) {
                    ArchiveConsumerManagerPanel panel = new ArchiveConsumerManagerPanel(services.getCOMServices().getArchiveService());
                    int count = serviceTabs.getTabCount();
                    serviceTabs.insertTab("Archive Manager", null, panel, "Archive Tab", count);
                    panel.setArchiveSyncConfigs(count + 1, serviceTabs, services);
                }

                /*
                if (services.getCOMServices().getArchiveSyncService() != null) {
                    ArchiveSyncConsumerManagerPanel panel = new ArchiveSyncConsumerManagerPanel(services.getCOMServices().getArchiveService(), services.getCOMServices().getArchiveSyncService());
                    int count = serviceTabs.getTabCount();
                    serviceTabs.insertTab("ArchiveSync service", null, panel, "ArchiveSync Tab", count);
                }
                 */
                if (services.getCOMServices().getEventService() != null) {
                    EventConsumerPanel panel = new EventConsumerPanel(services.getCOMServices().getEventService(), services.getCOMServices().getArchiveService());
                    int count = serviceTabs.getTabCount();
                    serviceTabs.insertTab("Event service", null, panel, "Event Tab", count);
                    panel.init();
                }
            }

            // MC
            if (services.getMCServices() != null) {
                if (services.getMCServices().getActionService() != null) {
                    ActionConsumerPanel panel = new ActionConsumerPanel(services.getMCServices().getActionService());
                    int count = serviceTabs.getTabCount();
                    serviceTabs.insertTab("Action service", null, panel, "Action Tab", count);
                    panel.init();
                }

                if (services.getMCServices().getParameterService() != null) {
                    ParameterConsumerPanel panel1 = new ParameterConsumerPanel(services.getMCServices().getParameterService());
                    ParameterPublishedValues panel2 = new ParameterPublishedValues(services.getMCServices().getParameterService());
                    int count = serviceTabs.getTabCount();
                    serviceTabs.insertTab("Parameter service", null, panel1, "Parameter Tab", count);
                    serviceTabs.insertTab("Published Parameter Values", null, panel2, "Published Parameters Tab", count + 1);
                    panel1.init();
                    panel2.subscribeToParameters();
                }

                if (services.getMCServices().getAggregationService() != null) {
                    AggregationConsumerPanel panel = new AggregationConsumerPanel(services.getMCServices().getAggregationService());
                    int count = serviceTabs.getTabCount();
                    serviceTabs.insertTab("Aggregation service", null, panel, "Aggregation Tab", count);
                    panel.init();
                }

                if (services.getMCServices().getAlertService() != null) {
                    AlertConsumerPanel panel = new AlertConsumerPanel(services.getMCServices().getAlertService());
                    int count = serviceTabs.getTabCount();
                    serviceTabs.insertTab("Alert service", null, panel, "Alert Tab", count);
                    panel.init();
                }

                if (services.getMCServices().getCheckService() != null) {
                    CheckConsumerPanel panel = new CheckConsumerPanel(services.getMCServices().getCheckService());
                    int count = serviceTabs.getTabCount();
                    serviceTabs.insertTab("Check service", null, panel, "Check Tab", count);
                }

                if (services.getMCServices().getStatisticService() != null) {
                    StatisticConsumerPanel panel = new StatisticConsumerPanel(services.getMCServices().getStatisticService(),
                            services.getMCServices().getParameterService());
                    int count = serviceTabs.getTabCount();
                    serviceTabs.insertTab("Statistic service", null, panel, "Statistic Tab", count);
                }
            }
        } catch (MALInteractionException ex) {
            LOGGER.log(Level.SEVERE, "Could not connect to the provider.", ex);
        } catch (MALException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        serviceTabs = new javax.swing.JTabbedPane();
        providerStatus = new javax.swing.JToolBar();
        jLabel3 = new javax.swing.JLabel();
        status = new javax.swing.JLabel();
        lastReceived = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        serviceTabs.setToolTipText("");
        serviceTabs.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        serviceTabs.setMaximumSize(new java.awt.Dimension(800, 600));
        serviceTabs.setMinimumSize(new java.awt.Dimension(800, 600));
        serviceTabs.setPreferredSize(new java.awt.Dimension(800, 600));
        serviceTabs.setRequestFocusEnabled(false);
        add(serviceTabs, java.awt.BorderLayout.CENTER);

        providerStatus.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        providerStatus.setFloatable(false);
        providerStatus.setRollover(true);

        jLabel3.setText("Provider Status: ");
        providerStatus.add(jLabel3);

        status.setText("Unknown");
        providerStatus.add(status);
        providerStatus.add(lastReceived);

        add(providerStatus, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lastReceived;
    private javax.swing.JToolBar providerStatus;
    private javax.swing.JTabbedPane serviceTabs;
    private javax.swing.JLabel status;
    // End of variables declaration//GEN-END:variables

    private class ProviderStatusAdapter extends HeartbeatAdapter {

        private static final long DELTA_ERROR = 2 * 1000; // 2 seconds = 2000 milliseconds
        private final long period; // In seconds
        private long lag; // In milliseconds
        private final Timer timer;
        private Time lastBeatAt = HelperTime.getTimestampMillis();

        public ProviderStatusAdapter(final HeartbeatConsumerServiceImpl heartbeat) throws MALInteractionException, MALException {
            long timestamp = System.currentTimeMillis();
            double value = heartbeat.getHeartbeatStub().getPeriod().getValue();
            lag = System.currentTimeMillis() - timestamp;
            period = (long) (value * 1000);
            status.setText("The provider is reachable! Beat period: " + value + " seconds");

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask()
          {
            int tryNumber = 0;

            @Override
            public void run()
            {
              final Time currentTime = HelperTime.getTimestampMillis();

              // If the current time has passed the last beat + the beat period + a delta error
              long threshold = lastBeatAt.getValue() + period + DELTA_ERROR;

              if (currentTime.getValue() > threshold) {
                // Then the provider is unresponsive
                status.setText("Unresponsive! ");
                status.setForeground(Color.RED);
                // Next time the heartbeat comes, trigger the lag measurement
                tryNumber = 3;
              } else {
                if (tryNumber >= 3) {
                  // Every third try...
                  try {
                    long timestamp = System.currentTimeMillis();
                    heartbeat.getHeartbeatStub().getPeriod();
                    lag = System.currentTimeMillis() - timestamp; // Calculate the lag
                  } catch (MALInteractionException | MALException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                  }
                  tryNumber = 0;
                }
                tryNumber++;
              }
            }
          }, period, period);
        }

        @Override
        public synchronized void beatNotifyReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
                org.ccsds.moims.mo.mal.structures.Identifier _Identifier0,
                org.ccsds.moims.mo.mal.structures.UpdateHeaderList _UpdateHeaderList1,
                java.util.Map qosProperties) {
            lastBeatAt = HelperTime.getTimestampMillis();
            final Time onboardTime = msgHeader.getTimestamp();
            final long iDiff = lastBeatAt.getValue() - onboardTime.getValue();

            status.setText("Alive! ");
            status.setForeground(Color.BLUE);
            lastReceived.setText(
                    "(Clocks diff: " + iDiff + " ms"
                    + " | Round-Trip Delay time: " + lag + " ms"
                    + " | Last beat received at: " + HelperTime.time2readableString(lastBeatAt)
                    + ")"
            );
        }
    }
}
