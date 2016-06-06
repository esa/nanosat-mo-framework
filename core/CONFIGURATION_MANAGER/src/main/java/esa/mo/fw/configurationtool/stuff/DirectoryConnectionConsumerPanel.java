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
package esa.mo.fw.configurationtool.stuff;

import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.nanosatmoframework.ground.adapter.GroundMOAdapter;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import org.ccsds.moims.mo.common.directory.DirectoryHelper;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.common.directory.structures.ServiceCapability;
import org.ccsds.moims.mo.common.directory.structures.ServiceCapabilityList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 *
 * @author Cesar Coelho
 */
public class DirectoryConnectionConsumerPanel extends javax.swing.JPanel {

    private final static String FOLDER_LOCATION_PROPERTY = "esa.mo.fw.configurationtool.stuff.FolderLocation";
    private static final String OBSW_DIRECTORY_NAME = "APPS";  // dir name
    private static final String OBSW_ALTERNATIVE_DIRECTORY_NAME = "sandbox" + File.separator + "apps";  // Running Environment
    private File folder_location = new File(".." + File.separator + OBSW_DIRECTORY_NAME);  // Location of the folder
    private ConnectionConsumer connectionConsumer;
    private javax.swing.JTabbedPane tabs;
    private ProviderSummaryList summaryList;
    private DefaultTableModel tableData;

    /**
     * Creates new form ConsumerPanelArchive
     *
     * @param connectionConsumer
     * @param tabs
     */
    public DirectoryConnectionConsumerPanel(ConnectionConsumer connectionConsumer, JTabbedPane tabs) {

        initComponents();
        this.connectionConsumer = connectionConsumer;
        this.tabs = tabs;
        this.initTextBoxAddress();

        if (System.getProperty(FOLDER_LOCATION_PROPERTY) != null) { // If there is a property for that, then use it!! 
            folder_location = new File(System.getProperty(FOLDER_LOCATION_PROPERTY));
        }

        this.refreshFoldersAvailable(false);

        String[] tableCol = new String[]{
            "Service name", "Supported Capabilities", "Service Properties", "URI address", "Broker URI Address"};

        tableData = new javax.swing.table.DefaultTableModel(
                new Object[][]{}, tableCol) {
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

        ListSelectionListener listSelectionListener = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {

                // Update the jTable according to the selection of the index
                // So, remove all...
                while (tableData.getRowCount() != 0) {
                    tableData.removeRow(tableData.getRowCount() - 1);
                }

                int index = providersList.getSelectedIndex();

                if (index == -1) {
                    index = 0;
                }

                ServiceCapabilityList services = summaryList.get(index).getProviderDetails().getServiceCapabilities();

                // And then add the new stuff
                for (int i = 0; i < services.size(); i++) {
                    ServiceCapability service = services.get(i);
                    String serviceName = HelperMisc.serviceKey2name(service.getServiceKey().getArea(), service.getServiceKey().getVersion(), service.getServiceKey().getService());
                    String serviceURI = "";
                    String brokerURI = "";

                    if (service.getServiceAddresses().size() > 0) {
                        serviceURI = service.getServiceAddresses().get(0).getServiceURI().toString();
                        // To avoid null pointers here...
                        brokerURI = (service.getServiceAddresses().get(0).getBrokerURI() == null) ? "null" : service.getServiceAddresses().get(0).getBrokerURI().toString();
                    }

                    String supportedCapabilities = (service.getSupportedCapabilities() == null) ? "All Supported" : service.getSupportedCapabilities().toString();
                    
                    tableData.addRow(new Object[]{
                        serviceName,
                        supportedCapabilities,
                        service.getServiceProperties().toString(),
                        serviceURI,
                        brokerURI
                    });
                }
            }
        };

        providersList.addListSelectionListener(listSelectionListener);
        connectButton.setEnabled(false);

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
        obswFolder = new javax.swing.JComboBox();
        jLabel30 = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        providersList = new javax.swing.JList();

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Communication Settings");
        jLabel7.setToolTipText("");

        homeTab.setMinimumSize(new java.awt.Dimension(200, 300));
        homeTab.setName(""); // NOI18N
        homeTab.setPreferredSize(new java.awt.Dimension(800, 600));

        jPanel10.setPreferredSize(new java.awt.Dimension(1750, 60));

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel29.setText("Directory Service URI:");
        jLabel29.setPreferredSize(new java.awt.Dimension(150, 14));

        uriServiceDirectory.setPreferredSize(new java.awt.Dimension(350, 20));
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

        obswFolder.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        obswFolder.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                obswFolderItemStateChanged(evt);
            }
        });
        obswFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                obswFolderActionPerformed(evt);
            }
        });

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel30.setText("OBSW Demo Folder:");
        jLabel30.setPreferredSize(new java.awt.Dimension(150, 14));

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(87, 87, 87)
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(obswFolder, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(uriServiceDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(load_URI_links1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(connectButton, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(uriServiceDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(load_URI_links1))))
                .addGap(11, 11, 11)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(obswFolder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(connectButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

        javax.swing.GroupLayout homeTabLayout = new javax.swing.GroupLayout(homeTab);
        homeTab.setLayout(homeTabLayout);
        homeTabLayout.setHorizontalGroup(
            homeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 948, Short.MAX_VALUE)
            .addGroup(homeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 988, Short.MAX_VALUE))
        );
        homeTabLayout.setVerticalGroup(
            homeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homeTabLayout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 442, Short.MAX_VALUE))
            .addGroup(homeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, homeTabLayout.createSequentialGroup()
                    .addGap(76, 76, 76)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 948, Short.MAX_VALUE)
            .addComponent(homeTab, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 948, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(homeTab, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed
        /*
        if (providersList.isSelectionEmpty()) {
            this.load_URI_links1ActionPerformed(null); // Fetch the info automatically...
        }
        this.load_URI_links1ActionPerformed(null); // Fetch the info automatically...
         */

        if (providersList.getModel().getSize() == 0) {
            return;
        }

        ProviderSummary summary = summaryList.get(providersList.getSelectedIndex());
        ProviderTabPanel providerPanel = new ProviderTabPanel(summary);

        javax.swing.JPanel pnlTab = new javax.swing.JPanel();
        pnlTab.setOpaque(false);
        JLabel label = new JLabel(summary.getProviderName().toString());
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

        tabs.addTab("", providerPanel);
        tabs.setTabComponentAt(tabs.getTabCount() - 1, pnlTab);
        tabs.setSelectedIndex(tabs.getTabCount() - 1);


    }//GEN-LAST:event_connectButtonActionPerformed

    private void errorConnectionProvider(String service) {
        JOptionPane.showMessageDialog(null, "Could not connect to " + service + " service provider!", "Error!", JOptionPane.PLAIN_MESSAGE);
    }

    private void uriServiceDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uriServiceDirectoryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_uriServiceDirectoryActionPerformed

    private void refreshFoldersAvailable(boolean isSecondaryCall) {

        obswFolder.removeAllItems();

        // get all the files from a directory
        File[] fList = folder_location.listFiles();

        if (fList == null) {
            Logger.getLogger(DirectoryConnectionConsumerPanel.class.getName()).log(Level.SEVERE, "The directory could not be found: {0}", folder_location.toString());
            this.changeOBSWFolder();

            if (!isSecondaryCall) { // To avoid getting the code in a stupid loop
                this.refreshFoldersAvailable(true);
            }

            return;
        }

        for (File file : fList) {
            if (file.isDirectory()) {
                obswFolder.addItem(file.getName());
            }
        }

    }

    @SuppressWarnings("unchecked")
    private void load_URI_links1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_load_URI_links1ActionPerformed

        try {
            summaryList = GroundMOAdapter.retrieveProvidersFromDirectory(this.getAddressToBeUsed());

            DefaultListModel listOfProviders = new DefaultListModel();

            for (ProviderSummary summary : summaryList) {
                listOfProviders.addElement(summary.getProviderKey().getInstId().toString() + ". " + summary.getProviderName().toString());
            }

            providersList.setModel(listOfProviders);

            if (!listOfProviders.isEmpty()) {
                providersList.setSelectedIndex(0);
            }

            connectButton.setEnabled(true);

        } catch (MALException ex) {
            errorConnectionProvider("Directory");
            providersList.setModel(new DefaultListModel());
            connectButton.setEnabled(false);
            return;
        } catch (MalformedURLException ex) {
            errorConnectionProvider("Directory");
            providersList.setModel(new DefaultListModel());
            connectButton.setEnabled(false);
            return;
        } catch (MALInteractionException ex) {
            errorConnectionProvider("Directory");
            providersList.setModel(new DefaultListModel());
            connectButton.setEnabled(false);
            return;
        }

    }//GEN-LAST:event_load_URI_links1ActionPerformed

    private void obswFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_obswFolderActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_obswFolderActionPerformed

    private void obswFolderItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_obswFolderItemStateChanged

        if (obswFolder.getSelectedItem() == null) {
            return;
        }

        // Generate the file path...
        String filePath = folder_location + File.separator + obswFolder.getSelectedItem().toString() + File.separator + "providerURIs.properties";

        try { // Load properties
            connectionConsumer.loadURIs(filePath);
        } catch (MalformedURLException ex) {
            Logger.getLogger(DirectoryConnectionConsumerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        // refres the textbox
        this.initTextBoxAddress();

    }//GEN-LAST:event_obswFolderItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton connectButton;
    private javax.swing.JPanel homeTab;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton load_URI_links1;
    private javax.swing.JComboBox obswFolder;
    private javax.swing.JList providersList;
    private javax.swing.JTextField uriServiceDirectory;
    // End of variables declaration//GEN-END:variables
/*
    private void setAddressToBeUsed() {  // updates the 
        this.providerURI = new URI(this.uriServiceDirectory.getText());
    }
     */
    private URI getAddressToBeUsed() {  // updates the 
        return new URI(this.uriServiceDirectory.getText());
    }

    private void changeOBSWFolder() {
        folder_location = new File(".." + File.separator + OBSW_ALTERNATIVE_DIRECTORY_NAME);  // Location of the folder
        Logger.getLogger(DirectoryConnectionConsumerPanel.class.getName()).log(Level.SEVERE, "The directory path was changed to: {0}", folder_location.toString());
    }

    private void initTextBoxAddress() {  // runs during the init of the app
        SingleConnectionDetails details;

        // Common services
        details = connectionConsumer.getServicesDetails().get(DirectoryHelper.DIRECTORY_SERVICE_NAME);

        if (details != null) {
            this.uriServiceDirectory.setText(details.getProviderURI().toString());
//            this.providerURI = details.getProviderURI();
        }
    }

    public class CloseMouseHandler implements MouseListener {

        private final javax.swing.JPanel panel;
        private final ProviderTabPanel providerPanel;

        CloseMouseHandler(javax.swing.JPanel panel, ProviderTabPanel providerPanel) {
            this.panel = panel;
            this.providerPanel = providerPanel;
        }

        @Override
        public void mouseClicked(MouseEvent evt) {
            for (int i = 0; i < tabs.getTabCount(); i++) {
                Component component = tabs.getTabComponentAt(i);

                if (component == panel) {
                    providerPanel.getServices().closeConnections();
                    tabs.remove(i);
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
