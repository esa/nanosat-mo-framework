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
package esa.mo.nmf.ctt.stuff;

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
import esa.mo.nmf.ctt.services.sm.PackageManagementConsumerPanel;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import esa.mo.sm.impl.consumer.HeartbeatConsumerServiceImpl;
import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private final GroundMOAdapterImpl services;

    /**
     * Creates a new tab for a Provider and populates it.
     *
     * @param provider
     */
    public ProviderTabPanel(final ProviderSummary provider) {
        services = new GroundMOAdapterImpl(provider);
        initComponents();
//        this.insertServicesTabs(); // Insert all the tabs
    }

    public GroundMOAdapterImpl getServices() {
        return this.services;
    }

    public final void insertServicesTabs() {
        /*
        Thread t1 = new Thread() {
            @Override
            public void run() {
                startTabs();
            }

        };

        t1.start();
        */

        startTabs();
    }

    private void startTabs() {
        try {
            // Software Management
            if (services.getSMServices() != null) {
                if (services.getSMServices().getHeartbeatService() != null) {
                    HeartbeatConsumerServiceImpl heartbeat = services.getSMServices().getHeartbeatService();
                    heartbeat.startListening(new ProviderStatusAdapter(heartbeat));
                } else {
                    status.setText("Heartbeat service not available.");
                }

                if (services.getSMServices().getAppsLauncherService() != null) {
                    serviceTabs.insertTab("Apps Launcher service", null, new AppsLauncherConsumerPanel(services.getSMServices().getAppsLauncherService()), "Apps Launcher Tab", serviceTabs.getTabCount());
                }

                if (services.getSMServices().getPackageManagementService() != null) {
                    serviceTabs.insertTab("Package Management service", null, new PackageManagementConsumerPanel(services.getSMServices().getPackageManagementService()), "Package Management Tab", serviceTabs.getTabCount());
                }
            }

            // COM
            if (services.getCOMServices() != null) {
                if (services.getCOMServices().getArchiveService() != null) {
                    serviceTabs.insertTab("Archive Manager", null, new ArchiveConsumerManagerPanel(services.getCOMServices().getArchiveService()), "Archive Tab", serviceTabs.getTabCount());
                }

                if (services.getCOMServices().getEventService() != null) {
                    serviceTabs.insertTab("Event service", null, new EventConsumerPanel(services.getCOMServices().getEventService(), services.getCOMServices().getArchiveService()), "Event Tab", serviceTabs.getTabCount());
                }
            }

            // MC
            if (services.getMCServices() != null) {
                if (services.getMCServices().getActionService() != null) {
                    serviceTabs.insertTab("Action service", null, new ActionConsumerPanel(services.getMCServices().getActionService()), "Action Tab", serviceTabs.getTabCount());
                }

                if (services.getMCServices().getParameterService() != null) {
                    serviceTabs.insertTab("Parameter service", null, new ParameterConsumerPanel(services.getMCServices().getParameterService()), "Parameter Tab", serviceTabs.getTabCount());
                    serviceTabs.insertTab("Published Parameter Values", null, new ParameterPublishedValues(services.getMCServices().getParameterService()), "Published Parameters Tab", serviceTabs.getTabCount());
                }

                if (services.getMCServices().getAggregationService() != null) {
                    serviceTabs.insertTab("Aggregation service", null, new AggregationConsumerPanel(services.getMCServices().getAggregationService()), "Aggregation Tab", serviceTabs.getTabCount());
                }

                if (services.getMCServices().getAlertService() != null) {
                    serviceTabs.insertTab("Alert service", null, new AlertConsumerPanel(services.getMCServices().getAlertService()), "Alert Tab", serviceTabs.getTabCount());
                }

                if (services.getMCServices().getCheckService() != null) {
                    serviceTabs.insertTab("Check service", null, new CheckConsumerPanel(services.getMCServices().getCheckService()), "Check Tab", serviceTabs.getTabCount());
                }

                if (services.getMCServices().getStatisticService() != null) {
                    serviceTabs.insertTab("Statistic service", null, new StatisticConsumerPanel(services.getMCServices().getStatisticService(), services.getMCServices().getParameterService()), "Statistic Tab", serviceTabs.getTabCount());
                }
            }

            // Common
            if (services.getCommonServices() != null) {
                if (services.getCommonServices().getConfigurationService() != null) {
                    serviceTabs.insertTab("Configuration service", null, new ConfigurationConsumerPanel(services.getCommonServices().getConfigurationService()), "Configuration Tab", serviceTabs.getTabCount());
                }
            }
        } catch (MALInteractionException ex) {
            Logger.getLogger(ProviderTabPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
            Logger.getLogger(ProviderTabPanel.class.getName()).log(Level.SEVERE, null, ex);
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
        private final Timer timer;
        private Time lastBeatAt = HelperTime.getTimestampMillis();

        public ProviderStatusAdapter(HeartbeatConsumerServiceImpl heartbeat) throws MALInteractionException, MALException {
            double value = heartbeat.getHeartbeatStub().getPeriod().getValue();
            period = (long) (value * 1000);
            status.setText("The provider is reachable and the beat period is set to " + value + " seconds");

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    final Time currentTime = HelperTime.getTimestampMillis();

                    // If the current time has passed the last beat + the beat period + a delta error
                    long threshold = lastBeatAt.getValue() + period + DELTA_ERROR;

                    if (currentTime.getValue() > threshold) {
                        // Then the provider is unresponsive
                        status.setText("Unresponsive!");
                        status.setForeground(Color.RED);
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
            lastReceived.setText("(Clocks diff: " + iDiff + " ms"
                    + " | Last beat received at: " + HelperTime.time2readableString(lastBeatAt) + ")");
        }

    }
}
