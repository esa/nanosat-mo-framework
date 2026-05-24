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

import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import org.ccsds.moims.mo.com.directory.DirectoryServiceInfo;
import org.ccsds.moims.mo.com.login.LoginHelper;
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionProvider;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperMisc;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.ServiceId;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 *
 * @author Cesar Coelho
 */
public class DirectoryConnectionConsumerPanel extends javax.swing.JPanel {

    private final javax.swing.JTabbedPane tabs;
    private ConnectionConsumer connectionConsumer;
    private ProviderSummaryList summaryList;
    private DefaultTableModel tableData;
    private static final String LAST_USED_CONSUMER_PREF = "last_used_consumer";
    private static Preferences prefs = Preferences.userNodeForPackage(DirectoryConnectionConsumerPanel.class);

    /**
     * Constructor.
     *
     * @param connectionConsumer The consumer connections.
     * @param tabs The tabs object.
     */
    public DirectoryConnectionConsumerPanel(final ConnectionConsumer connectionConsumer, final JTabbedPane tabs) {
        initComponents();
        this.connectionConsumer = connectionConsumer;
        this.tabs = tabs;
        this.initTextBoxAddress();

        String[] tableCol = new String[]{"Service name", "Supported Capabilities",
            "Service Properties", "URI address", "Broker URI Address"};

        tableData = new javax.swing.table.DefaultTableModel(new Object[][]{}, tableCol) {
            Class[] types = new Class[]{
                java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class
            };

            @Override               //all cells false
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };

        jTable1.setModel(tableData);

        ListSelectionListener listSelectionListener = listSelectionEvent -> {
            // Update the jTable according to the selection of the index
            cleanTableData();

            int index = providersList.getSelectedIndex();

            if (index == -1) {
                index = 0;
            }

            ServiceCapabilityList services
                    = summaryList.get(index).getProviderDetails().getServiceCapabilities();

            // And then add the new stuff
            for (int i = 0; i < services.size(); i++) {
                ServiceCapability service = services.get(i);

                String serviceName;
                try {
                    serviceName = HelperMisc.serviceKey2name(
                            service.getServiceId().getKeyArea(),
                            service.getServiceId().getKeyAreaVersion(),
                            service.getServiceId().getKeyService());
                } catch (MALException ex) {
                    serviceName = "<Unknown service>";
                }

                String serviceURI = "";
                String brokerURI = "";

                if (!service.getServiceAddresses().isEmpty()) {
                    serviceURI = service.getServiceAddresses().get(0).getServiceURI().toString();
                    // To avoid null pointers here...
                    brokerURI = (service.getServiceAddresses().get(0).getBrokerURI() == null)
                            ? "null" : service.getServiceAddresses().get(0).getBrokerURI().toString();
                }

                String supportedCapabilities = (service.getSupportedCapabilitySets() == null)
                        ? "All Supported" : service.getSupportedCapabilitySets().toString();

                tableData.addRow(new Object[]{
                    serviceName,
                    supportedCapabilities,
                    service.getServiceProperties().toString(),
                    serviceURI,
                    brokerURI
                });
            }
        };

        providersList.addListSelectionListener(listSelectionListener);
        providersList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    connectButtonActionPerformed(null);
                }
            }
        });
        connectButton.setEnabled(false);
    }

    /**
     * Cleans the table data that contains the list of services provided by the
     * currently selected provider.
     */
    private void cleanTableData() {
        while (tableData.getRowCount() != 0) {
            tableData.removeRow(tableData.getRowCount() - 1);
        }
    }

    public void setURITextbox(final String uri) {
        if (uri.isEmpty()) {
            String freshUri = readFreshDirectoryURI();
            if (freshUri != null) {
                uriServiceDirectory.setText(freshUri);
            } else {
                uriServiceDirectory.setText(prefs.get(LAST_USED_CONSUMER_PREF, ""));
            }
        } else {
            uriServiceDirectory.setText(uri);
        }
    }

    private static String readFreshDirectoryURI() {
        File file = ConnectionProvider.getProviderURIsDirectory(HelperMisc.PROVIDER_URIS_PROPERTIES_FILENAME);
        if (!file.exists()) {
            return null;
        }
        long ageMs = System.currentTimeMillis() - file.lastModified();
        if (ageMs > 120_000) { // 2 minutes in milliseconds
            return null;
        }
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);
        } catch (IOException ex) {
            Logger.getLogger(DirectoryConnectionConsumerPanel.class.getName())
                    .log(Level.WARNING, "Could not read providerURIs.properties", ex);
            return null;
        }
        String key = DirectoryServiceInfo.DIRECTORY_SERVICE_NAME.getValue() + "_URI";
        return props.getProperty(key);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel7 = new javax.swing.JLabel();
        homeTab = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        uriServiceDirectory = new javax.swing.JTextField();
        load_URI_links1 = new javax.swing.JButton();
        connectButton = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        providersList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1280, 720));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Communication Settings");
        jLabel7.setToolTipText("");

        homeTab.setName(""); // NOI18N


        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel29.setText("Directory Service URI:");

        uriServiceDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uriServiceDirectoryActionPerformed(evt);
            }
        });

        load_URI_links1.setText("Fetch Information");
        load_URI_links1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                load_URI_links1ActionPerformed(evt);
            }
        });

        connectButton.setText("Connect to Selected Provider");
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });

        // Row 1: label + URI field
        javax.swing.JPanel uriRow = new javax.swing.JPanel(new java.awt.BorderLayout(6, 0));
        uriRow.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 6, 4, 6));
        uriRow.add(jLabel29, java.awt.BorderLayout.WEST);
        uriRow.add(uriServiceDirectory, java.awt.BorderLayout.CENTER);

        // Row 2: two buttons side by side, occupying the left 50%
        javax.swing.JPanel buttonRow = new javax.swing.JPanel(new java.awt.GridBagLayout());
        buttonRow.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 6, 6, 6));
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.insets = new java.awt.Insets(0, 0, 0, 3);
        buttonRow.add(load_URI_links1, gbc);
        gbc.insets = new java.awt.Insets(0, 3, 0, 0);
        buttonRow.add(connectButton, gbc);
        gbc.weightx = 2.0;
        gbc.insets = new java.awt.Insets(0, 0, 0, 0);
        buttonRow.add(new javax.swing.JPanel(), gbc);

        jPanel10.setLayout(new java.awt.BorderLayout());
        jPanel10.add(uriRow, java.awt.BorderLayout.NORTH);
        jPanel10.add(buttonRow, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setDividerLocation(280);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jSplitPane1.setRightComponent(jScrollPane1);

        jScrollPane2.setMinimumSize(new java.awt.Dimension(43, 43));

        providersList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jScrollPane2.setViewportView(providersList);

        jSplitPane1.setLeftComponent(jScrollPane2);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 2, 0));
        jLabel1.setText("Providers List:");

        javax.swing.JPanel headerPanel = new javax.swing.JPanel(new java.awt.BorderLayout());
        headerPanel.add(jPanel10, java.awt.BorderLayout.CENTER);
        headerPanel.add(jLabel1, java.awt.BorderLayout.SOUTH);

        homeTab.setLayout(new java.awt.BorderLayout());
        homeTab.add(headerPanel, java.awt.BorderLayout.NORTH);
        homeTab.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        this.setLayout(new java.awt.BorderLayout(0, 8));
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 0, 0, 0));
        this.add(jLabel7, java.awt.BorderLayout.NORTH);
        this.add(homeTab, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed
        if (providersList.getModel().getSize() == 0) {
            return;
        }

        final ProviderSummary summary = summaryList.get(providersList.getSelectedIndex());
        final int count = tabs.getTabCount();

        Thread t1 = new Thread() {
            @Override
            public void run() {
                this.setName("ConnectButtonActionThread");

                ServiceId loginServiceId = new ServiceId(LoginHelper.LOGIN_SERVICE.getArea().getNumber(),
                        LoginHelper.LOGIN_SERVICE.getServiceNumber(), LoginHelper.LOGIN_SERVICE.getArea().getVersion());
                ServiceCapability loginService = summary.getProviderDetails().getServiceCapabilities().stream().filter(
                        serviceCapability -> serviceCapability.getServiceId().equals(loginServiceId)).findFirst().orElse(
                                null);

                Blob authenticationId = null;
                String localNamePrefix = null;
                IdentifierList providerDomain = summary.getDomain();
                if (loginService != null) {
                    if (loginService.getServiceAddresses().get(0).getServiceURI().getValue().toLowerCase().contains("lwmcs")) {
                        localNamePrefix = "LWMCS_Consumer_" + new Random().nextInt();
                    }
                    LoginDialog loginDialog = new LoginDialog(loginService, providerDomain, localNamePrefix);
                    if (loginDialog.isLoginSuccessful()) {
                        authenticationId = loginDialog.getAuthenticationId();
                    } else {
                        errorConnectionProvider("Login", loginDialog.getLoginError());
                    }
                }

                ProviderTabPanel providerPanel = createNewProviderTabPanel(summary, authenticationId, localNamePrefix);

                // -- Close Button --
                final javax.swing.JPanel pnlTab = new javax.swing.JPanel();
                pnlTab.setOpaque(false);
                JLabel label = new JLabel(summary.getProviderId().toString());
                JLabel closeLabel = new JLabel("x");
                closeLabel.addMouseListener(new CloseMouseHandler(pnlTab, providerPanel));
                closeLabel.setFont(closeLabel.getFont().deriveFont(closeLabel.getFont().getStyle() | Font.BOLD));

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.weightx = 1;
                pnlTab.add(label, gbc);

                gbc.gridx++;
                gbc.weightx = 0;
                pnlTab.add(closeLabel, gbc);
                // ------------------

                tabs.addTab("", providerPanel);
                tabs.setSelectedIndex(count);
                tabs.setTabComponentAt(count, pnlTab);

                providerPanel.insertServicesTabs();
            }
        };

        t1.start();
    }//GEN-LAST:event_connectButtonActionPerformed

    public ProviderTabPanel createNewProviderTabPanel(ProviderSummary providerSummary,
            Blob authenticationId, String localNamePrefix) {
        return new ProviderTabPanel(providerSummary, authenticationId, localNamePrefix);
    }

    private void errorConnectionProvider(String service, Throwable ex) {
        JOptionPane.showMessageDialog(null,
                "Could not connect to " + service + " service provider!"
                + "\nException:\n" + ex + "\n" + ex.getMessage(),
                "Error!", JOptionPane.PLAIN_MESSAGE);
    }

    private void uriServiceDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uriServiceDirectoryActionPerformed
        load_URI_links1ActionPerformed(evt);
    }//GEN-LAST:event_uriServiceDirectoryActionPerformed

    @SuppressWarnings("unchecked")
    private void load_URI_links1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_load_URI_links1ActionPerformed
        try {
            summaryList = GroundMOAdapterImpl.retrieveProvidersFromDirectory(this.getAddressToBeUsed());
            DefaultListModel listOfProviders = new DefaultListModel();

            for (ProviderSummary summary : summaryList) {
                listOfProviders.addElement(summary.getInstId().toString()
                        + ". " + summary.getProviderId().toString());
            }

            providersList.setModel(listOfProviders);

            if (!listOfProviders.isEmpty()) {
                providersList.setSelectedIndex(0);
            }
            prefs.put(LAST_USED_CONSUMER_PREF, uriServiceDirectory.getText());

            connectButton.setEnabled(true);
        } catch (MalformedURLException | MALInteractionException | MALException ex) {
            errorConnectionProvider("Directory", ex);
            providersList.setModel(new DefaultListModel());
            connectButton.setEnabled(false);
            Logger.getLogger(DirectoryConnectionConsumerPanel.class.getName()).log(
                    Level.SEVERE, null, ex);
            cleanTableData();
        }
    }//GEN-LAST:event_load_URI_links1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton connectButton;
    private javax.swing.JPanel homeTab;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton load_URI_links1;
    private javax.swing.JList providersList;
    private javax.swing.JTextField uriServiceDirectory;
    // End of variables declaration//GEN-END:variables

    private URI getAddressToBeUsed() {  // updates the
        return new URI(this.uriServiceDirectory.getText());
    }

    private void initTextBoxAddress() {  // runs during the init of the app
        // Common services
        SingleConnectionDetails details = connectionConsumer.getServicesDetails().get(
                DirectoryServiceInfo.DIRECTORY_SERVICE_NAME);

        if (details != null) {
            this.uriServiceDirectory.setText(details.getProviderURI().toString());
        }
    }

    private void closeProvider(ProviderTabPanel providerPanel) {
        try {
            if (providerPanel.getServices().getAuthenticationId() != null) {
                try {
                    providerPanel.getServices().getCOMServices().getLoginService().getLoginStub().logout();
                    providerPanel.getServices().setAuthenticationId(null);
                    Logger.getLogger(DirectoryConnectionConsumerPanel.class.getName())
                            .log(Level.INFO, "Logged out successfully");
                } catch (MALInteractionException | MALException e) {
                    Logger.getLogger(DirectoryConnectionConsumerPanel.class.getName())
                            .log(Level.SEVERE, "Unexpected exception during logout!", e);
                }
            }
            providerPanel.getServices().closeConnections();
        } catch (Exception ex) {
            Logger.getLogger(DirectoryConnectionConsumerPanel.class.getName()).log(
                    Level.WARNING,
                    "The connection was not closed correctly. Maybe the provider was unreachable!");
        }

    }

    public class CloseMouseHandler implements MouseListener {

        private final javax.swing.JPanel panel;
        private final ProviderTabPanel providerPanel;

        CloseMouseHandler(final javax.swing.JPanel panel, final ProviderTabPanel providerPanel) {
            this.panel = panel;
            this.providerPanel = providerPanel;
        }

        @Override
        public void mouseClicked(MouseEvent evt) {
            for (int i = 0; i < tabs.getTabCount(); i++) {
                final Component component = tabs.getTabComponentAt(i);

                if (component == panel) {
                    tabs.remove(i);
                    tabs.revalidate();
                    tabs.repaint();

                    Thread t1 = new Thread() {
                        @Override
                        public void run() {
                            this.setName("CloseButtonTabThread");
                            closeProvider(providerPanel);
                        }
                    };

                    t1.start();
                    return;
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent me) {
        }

        @Override
        public void mouseReleased(MouseEvent me) {
        }

        @Override
        public void mouseEntered(MouseEvent me) {
        }

        @Override
        public void mouseExited(MouseEvent me) {
        }
    }
}
