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
package esa.mo.nmf.ctt.utils;

import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.helpertools.clock.PlatformClockCallback;
import esa.mo.helpertools.clock.SystemClock;
import esa.mo.mc.impl.util.MCServicesConsumer;
import esa.mo.mp.impl.consumer.MPServicesConsumer;
import esa.mo.nmf.ctt.services.com.ArchiveConsumerManagerPanel;
import esa.mo.nmf.ctt.services.com.EventConsumerPanel;
import esa.mo.nmf.ctt.services.common.ConfigurationConsumerPanel;
import esa.mo.nmf.ctt.services.mc.ActionConsumerPanel;
import esa.mo.nmf.ctt.services.mc.AggregationConsumerPanel;
import esa.mo.nmf.ctt.services.mc.AlertConsumerPanel;
import esa.mo.nmf.ctt.services.mc.ParameterConsumerPanel;
import esa.mo.nmf.ctt.services.mc.ParameterPublishedValues;
import esa.mo.nmf.ctt.services.mp.pds.PlanDistributionConsumerPanel;
import esa.mo.nmf.ctt.services.mp.pds.PublishedPlanStatusesPanel;
import esa.mo.nmf.ctt.services.mp.pds.PublishedPlansPanel;
import esa.mo.nmf.ctt.services.mp.pec.PublishedActivityUpdatesPanel;
import esa.mo.nmf.ctt.services.mp.ped.PlanEditConsumerPanel;
import esa.mo.nmf.ctt.services.mp.pim.PlanInformationManagementConsumerPanel;
import esa.mo.nmf.ctt.services.mp.prs.PlanningRequestConsumerPanel;
import esa.mo.nmf.ctt.services.mp.prs.PublishedRequestsPanel;
import esa.mo.nmf.ctt.services.platform.clock.ClockConsumerPanel;
import esa.mo.nmf.ctt.services.sm.AppsLauncherConsumerPanel;
import esa.mo.nmf.ctt.services.sm.CommandExecutorConsumerPanel;
import esa.mo.nmf.ctt.services.sm.PackageManagementConsumerPanel;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import esa.mo.sm.impl.consumer.HeartbeatConsumerServiceImpl;
import esa.mo.sm.impl.util.SMServicesConsumer;
import java.awt.Color;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTabbedPane;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperTime;
import org.ccsds.moims.mo.mal.helpertools.misc.TaskScheduler;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.platform.clock.consumer.ClockStub;
import org.ccsds.moims.mo.softwaremanagement.heartbeat.consumer.HeartbeatAdapter;

/**
 *
 * @author Cesar Coelho
 */
public class ProviderTabPanel extends javax.swing.JPanel {

    private static final Logger LOGGER = Logger.getLogger(ProviderTabPanel.class.getName());
    protected final GroundMOAdapterImpl services;
    private final ProviderSummary providerSummary;

    /**
     * Creates a new tab for a Provider and populates it.
     *
     * @param provider The provider summary.
     * @param authenticationId The authentication Id.
     * @param localNamePrefix The local name prefix.
     */
    public ProviderTabPanel(final ProviderSummary provider, Blob authenticationId, String localNamePrefix) {
        services = new GroundMOAdapterImpl(provider, authenticationId, localNamePrefix);
        providerSummary = provider;
        initComponents();
    }

    public GroundMOAdapterImpl getServices() {
        return this.services;
    }

    public void insertServicesTabs() {
        try {
            startTabs();
        } catch (MALInteractionException ex) {
            LOGGER.log(Level.SEVERE, "Could not connect to the provider.", ex);
        } catch (MALException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public JTabbedPane getTabs() {
        return serviceTabs;
    }

    private void startTabs() throws MALInteractionException, MALException {
        // Common
        if (services.getCommonServices() != null) {
            if (services.getCommonServices().getConfigurationService() != null) {
                ConfigurationConsumerPanel panel = new ConfigurationConsumerPanel(
                        services.getCommonServices().getConfigurationService(), providerSummary);
                int count = serviceTabs.getTabCount();
                serviceTabs.insertTab("Configuration service", null, panel, "Configuration Tab", count);
            }
        }

        // Software Management
        if (services.getSMServices() != null) {
            SMServicesConsumer sm = services.getSMServices();

            if (sm.getHeartbeatService() != null) {
                HeartbeatConsumerServiceImpl heartbeat = sm.getHeartbeatService();
                ProviderStatusAdapter providerStatusAdapter = new ProviderStatusAdapter(heartbeat);
                heartbeat.startListening(providerStatusAdapter);
            } else {
                status.setText("Heartbeat service not available.");
            }

            if (sm.getAppsLauncherService() != null) {
                AppsLauncherConsumerPanel panel = new AppsLauncherConsumerPanel(sm.getAppsLauncherService());
                int count = serviceTabs.getTabCount();
                serviceTabs.insertTab("Apps Launcher service", null, panel, "Apps Launcher Tab", count);
                panel.init();
            }

            if (sm.getCommandExecutorService() != null) {
                CommandExecutorConsumerPanel panel = new CommandExecutorConsumerPanel(sm.getCommandExecutorService());
                int count = serviceTabs.getTabCount();
                serviceTabs.insertTab("Command Executor service", null, panel, "Command Executor Tab", count);
                panel.init();
            }

            if (sm.getPackageManagementService() != null) {
                PackageManagementConsumerPanel panel = new PackageManagementConsumerPanel(sm.getPackageManagementService());
                int count = serviceTabs.getTabCount();
                serviceTabs.insertTab("Package Management service", null, panel, "Package Management Tab", count);
                panel.init();
            }
        }

        // COM
        if (services.getCOMServices() != null) {
            COMServicesConsumer com = services.getCOMServices();

            if (com.getArchiveService() != null) {
                ArchiveConsumerManagerPanel panel = new ArchiveConsumerManagerPanel(com.getArchiveService());
                int count = serviceTabs.getTabCount();
                serviceTabs.insertTab("Archive Manager", null, panel, "Archive Tab", count);
                panel.setArchiveSyncConfigs(count + 1, serviceTabs, services);
            }

            /*
            if (services.getCOMServices().getArchiveSyncService() != null) {
                ArchiveSyncConsumerManagerPanel panel = new ArchiveSyncConsumerManagerPanel(com.getArchiveService(), com.getArchiveSyncService());
                int count = serviceTabs.getTabCount();
                serviceTabs.insertTab("ArchiveSync service", null, panel, "ArchiveSync Tab", count);
            }
             */
            if (com.getEventService() != null) {
                EventConsumerPanel panel = new EventConsumerPanel(com.getEventService(), com.getArchiveService());
                int count = serviceTabs.getTabCount();
                serviceTabs.insertTab("Event service", null, panel, "Event Tab", count);
                panel.init();
            }
        }

        // MC
        if (services.getMCServices() != null) {
            MCServicesConsumer mc = services.getMCServices();

            if (mc.getActionService() != null) {
                ActionConsumerPanel panel = new ActionConsumerPanel(services);
                int count = serviceTabs.getTabCount();
                serviceTabs.insertTab("Action service", null, panel, "Action Tab", count);
                panel.init();
            }

            if (mc.getParameterService() != null) {
                ParameterConsumerPanel panel1 = new ParameterConsumerPanel(mc.getParameterService());
                ParameterPublishedValues panel2 = new ParameterPublishedValues(mc.getParameterService());
                int count = serviceTabs.getTabCount();
                serviceTabs.insertTab("Parameter service", null, panel1, "Parameter Tab", count);
                serviceTabs.insertTab("Published Parameter Values", null, panel2, "Published Parameters Tab",
                        count + 1);
                panel1.init();
                panel2.subscribeToParameters();
            }

            if (mc.getAggregationService() != null) {
                AggregationConsumerPanel panel = new AggregationConsumerPanel(mc.getAggregationService());
                int count = serviceTabs.getTabCount();
                serviceTabs.insertTab("Aggregation service", null, panel, "Aggregation Tab", count);
                panel.init();
            }

            if (mc.getAlertService() != null) {
                AlertConsumerPanel panel = new AlertConsumerPanel(mc.getAlertService());
                int count = serviceTabs.getTabCount();
                serviceTabs.insertTab("Alert service", null, panel, "Alert Tab", count);
                panel.init();
            }

            /*
            if (mc.getCheckService() != null) {
                CheckConsumerPanel panel = new CheckConsumerPanel(mc.getCheckService());
                int count = serviceTabs.getTabCount();
                serviceTabs.insertTab("Check service", null, panel, "Check Tab", count);
            }

            if (mc.getStatisticService() != null) {
                StatisticConsumerPanel panel = new StatisticConsumerPanel(
                        mc.getStatisticService(),
                        mc.getParameterService());
                int count = serviceTabs.getTabCount();
                serviceTabs.insertTab("Statistic service", null, panel, "Statistic Tab", count);
            }
            */
        }

        // MP
        if (services.getMPServices() != null) {
            MPServicesConsumer mps = services.getMPServices();

            if (mps.getPlanInformationManagementService() != null) {
                PlanInformationManagementConsumerPanel consumerPanel = new PlanInformationManagementConsumerPanel(
                        mps.getPlanInformationManagementService());
                int count = serviceTabs.getTabCount();
                serviceTabs.insertTab("Plan Information Management service", null, consumerPanel, "PIM Tab", count);
                consumerPanel.init();
            }

            if (mps.getPlanningRequestService() != null) {
                PlanningRequestConsumerPanel consumerPanel = new PlanningRequestConsumerPanel(mps.getPlanningRequestService());
                PublishedRequestsPanel publishedPanel = new PublishedRequestsPanel(
                        services.getCOMServices().getArchiveService(),
                        mps.getPlanningRequestService()
                );
                int count = serviceTabs.getTabCount();
                serviceTabs.insertTab("Planning Request service", null, consumerPanel, "PRS Tab", count);
                serviceTabs.insertTab("Published planning requests", null, publishedPanel, "Published Requests Tab",
                        count + 1);
                consumerPanel.init();
            }

            if (mps.getPlanDistributionService() != null) {
                PlanDistributionConsumerPanel consumerPanel = new PlanDistributionConsumerPanel(mps.getPlanDistributionService());
                PublishedPlansPanel publishedPlansPanel = new PublishedPlansPanel(
                        services.getCOMServices().getArchiveService(),
                        mps.getPlanDistributionService()
                );
                PublishedPlanStatusesPanel publishedPlanStatusesPanel = new PublishedPlanStatusesPanel(
                        services.getCOMServices().getArchiveService(),
                        mps.getPlanDistributionService()
                );
                int count = serviceTabs.getTabCount();
                serviceTabs.insertTab("Plan Distribution service", null, consumerPanel, "PDS Tab", count);
                serviceTabs.insertTab("Published plan versions", null, publishedPlansPanel, "Published Plans Tab",
                        count + 1);
                serviceTabs.insertTab("Published plan statuses", null,
                        publishedPlanStatusesPanel, "Published Plan Statuses Tab", count + 2);
                consumerPanel.init();
            }

            if (mps.getPlanEditService() != null) {
                PlanEditConsumerPanel consumerPanel = new PlanEditConsumerPanel(mps.getPlanEditService());
                int count = serviceTabs.getTabCount();
                serviceTabs.insertTab("Plan Edit service", null, consumerPanel, "PED Tab", count);
                consumerPanel.init();
            }

            if (mps.getPlanExecutionControlService() != null) {
                PublishedActivityUpdatesPanel publishedPanel = new PublishedActivityUpdatesPanel(
                        services.getCOMServices().getArchiveService(),
                        mps.getPlanExecutionControlService()
                );
                int count = serviceTabs.getTabCount();
                serviceTabs.insertTab("Published activity updates", null, publishedPanel,
                        "Published activity updates", count);
            }
        }

        // Platform
        if (services.getPlatformServices() != null) {
            try {
                ClockStub clock = services.getPlatformServices().getClockService();

                if (clock != null) {
                    System.setProperty("esa.mo.nmf.app.systemTimeProvidedByPlatformClockService", "true");
                    SystemClock.setPlatformClockCallback(new PlatformClockCallback() {
                        @Override
                        public Time getPlatformTime() {
                            try {
                                return clock.getTime();
                            } catch (MALInteractionException | MALException e) {
                                LOGGER.log(Level.SEVERE, null, e);
                            }
                            return new Time(System.currentTimeMillis());
                        }

                        @Override
                        public int getPlatformTimeFactor() {
                            try {
                                return clock.getTimeFactor();
                            } catch (MALInteractionException | MALException e) {
                                LOGGER.log(Level.SEVERE, null, e);
                            }
                            return 1;
                        }
                    });

                    ClockConsumerPanel consumerPanel = new ClockConsumerPanel(clock);
                    int count = serviceTabs.getTabCount();
                    serviceTabs.insertTab("Clock service", null, consumerPanel, "Clock Tab", count);
                    consumerPanel.init();
                }
            } catch (IOException ex) {
                LOGGER.log(Level.INFO, "The Clock Service is not available");
            }
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
        private final TaskScheduler timer;
        private Time lastBeatAt = Time.now();

        public ProviderStatusAdapter(final HeartbeatConsumerServiceImpl heartbeat)
                throws MALInteractionException, MALException {
            long timestamp = System.currentTimeMillis();
            double value = heartbeat.getHeartbeatStub().getPeriod().getValue();
            lag = System.currentTimeMillis() - timestamp;
            period = (long) (value * 1000);
            status.setText("The provider is reachable! Beat period: " + value + " seconds");

            timer = new TaskScheduler(1);
            timer.scheduleTask(new Thread() {
                int tryNumber = 0;

                @Override
                public void run() {
                    this.setPriority(MAX_PRIORITY);
                    final Time currentTime = Time.now();

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
                            // Every three tries...
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
            }, period, period, TimeUnit.MILLISECONDS, true);
        }

        @Override
        public synchronized void beatNotifyReceived(MALMessageHeader msgHeader,
                org.ccsds.moims.mo.mal.structures.Identifier _Identifier0,
                org.ccsds.moims.mo.mal.structures.UpdateHeader updateHeader,
                java.util.Map qosProperties) {
            lastBeatAt = Time.now();
            final Time onboardTime = msgHeader.getTimestamp();
            final long iDiff = lastBeatAt.getValue() - onboardTime.getValue();

            status.setText("Alive! ");
            status.setForeground(Color.BLUE);

            StringBuilder buf = new StringBuilder();
            buf.append("(Clocks diff: ");
            buf.append(iDiff);
            buf.append(" ms | Round-Trip Delay time: ");
            buf.append(lag);
            buf.append(" ms | Last beat received at: ");
            buf.append(HelperTime.time2readableString(lastBeatAt));
            buf.append(")");
            lastReceived.setText(buf.toString());
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (getServices().getAuthenticationId() != null) {
            try {
                getServices().getCommonServices().getLoginService().getLoginStub().logout();
                getServices().setAuthenticationId(null);
                LOGGER.log(Level.INFO, "Logged out successfully");
            } catch (MALInteractionException | MALException e) {
                LOGGER.log(Level.SEVERE, "Unexpected exception during logout!", e);
            }
        }
        getServices().closeConnections();
    }
}
