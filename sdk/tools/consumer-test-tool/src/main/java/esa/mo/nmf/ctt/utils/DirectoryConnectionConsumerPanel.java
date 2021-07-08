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

import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import java.util.prefs.Preferences;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
public class DirectoryConnectionConsumerPanel extends javax.swing.JPanel
{

  private ConnectionConsumer connectionConsumer;
  private javax.swing.JTabbedPane tabs;
  private ProviderSummaryList summaryList;
  private DefaultTableModel tableData;
  private final boolean isS2G;
  private static final String LAST_USED_CONSUMER_PREF = "last_used_consumer";
  private static Preferences prefs = Preferences.userNodeForPackage(DirectoryConnectionConsumerPanel.class);

  /**
   * Creates new form ConsumerPanelArchive
   *
   * @param isS2G              Flag that defines if it is a Space to Ground link
   * @param connectionConsumer
   * @param tabs
   */
  public DirectoryConnectionConsumerPanel(final boolean isS2G,
      final ConnectionConsumer connectionConsumer, final JTabbedPane tabs)
  {
    initComponents();
    this.connectionConsumer = connectionConsumer;
    this.tabs = tabs;
    this.initTextBoxAddress();
    this.isS2G = isS2G;

    String[] tableCol = new String[]{"Service name", "Supported Capabilities",
      "Service Properties", "URI address", "Broker URI Address"};

    tableData = new javax.swing.table.DefaultTableModel(
        new Object[][]{}, tableCol)
    {
      Class[] types = new Class[]{
        java.lang.String.class, java.lang.String.class, java.lang.String.class,
        java.lang.String.class, java.lang.String.class
      };

      @Override               //all cells false
      public boolean isCellEditable(int row, int column)
      {
        return false;
      }

      @Override
      public Class getColumnClass(int columnIndex)
      {
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
          serviceName = HelperMisc.serviceKey2name(service.getServiceKey().getArea(),
              service.getServiceKey().getVersion(), service.getServiceKey().getService());
        } catch (MALException ex) {
          serviceName = "<Unknown service>";
        }

        String serviceURI = "";
        String brokerURI = "";

        if (service.getServiceAddresses().size() > 0) {
          serviceURI = service.getServiceAddresses().get(0).getServiceURI().toString();
          // To avoid null pointers here...
          brokerURI = (service.getServiceAddresses().get(0).getBrokerURI() == null)
              ? "null" : service.getServiceAddresses().get(0).getBrokerURI().toString();
        }

        String supportedCapabilities = (service.getSupportedCapabilities() == null)
            ? "All Supported" : service.getSupportedCapabilities().toString();

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
    connectButton.setEnabled(false);
  }

  /**
   * Cleans the table data that contains the list of services provided by the currently selected prodiver.
   */
  private void cleanTableData() {
    while (tableData.getRowCount() != 0) {
      tableData.removeRow(tableData.getRowCount() - 1);
    }
  }

  public void setURITextbox(final String uri)
  {
    if (uri.isEmpty()) {
      uriServiceDirectory.setText(prefs.get(LAST_USED_CONSUMER_PREF, ""));
    } else {
      uriServiceDirectory.setText(uri);
    }
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
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
        uriServiceDirectory.addActionListener(evt -> uriServiceDirectoryActionPerformed(evt));

        load_URI_links1.setText("Fetch Information");
        load_URI_links1.addActionListener(evt -> load_URI_links1ActionPerformed(evt));

        connectButton.setText("Connect to Selected Provider");
        connectButton.addActionListener(evt -> connectButtonActionPerformed(evt));

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(uriServiceDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(connectButton, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE))
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

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Providers List:");

        javax.swing.GroupLayout homeTabLayout = new javax.swing.GroupLayout(homeTab);
        homeTab.setLayout(homeTabLayout);
        homeTabLayout.setHorizontalGroup(
            homeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 828, Short.MAX_VALUE)
            .addGroup(homeTabLayout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(homeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 988, Short.MAX_VALUE))
        );
        homeTabLayout.setVerticalGroup(
            homeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homeTabLayout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 260, Short.MAX_VALUE))
            .addGroup(homeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, homeTabLayout.createSequentialGroup()
                    .addGap(99, 99, 99)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 828, Short.MAX_VALUE)
            .addComponent(homeTab, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 828, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(homeTab, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed
      if (providersList.getModel().getSize() == 0) {
        return;
      }

      final ProviderSummary summary = summaryList.get(providersList.getSelectedIndex());
      final int count = tabs.getTabCount();

      Thread t1 = new Thread()
      {
        @Override
        public void run()
        {
          this.setName("ConnectButtonActionThread");
          ProviderTabPanel providerPanel = createNewProviderTabPanel(summary);

          // -- Close Button --
          final javax.swing.JPanel pnlTab = new javax.swing.JPanel();
          pnlTab.setOpaque(false);
          JLabel label = new JLabel(summary.getProviderName().toString());
          JLabel closeLabel = new JLabel("x");
          closeLabel.addMouseListener(new CloseMouseHandler(pnlTab, providerPanel));
          closeLabel.setFont(closeLabel.getFont().deriveFont(
              closeLabel.getFont().getStyle() | Font.BOLD));

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

  public ProviderTabPanel createNewProviderTabPanel(final ProviderSummary providerSummary)
  {
    return new ProviderTabPanel(providerSummary);
  }

  private void errorConnectionProvider(String service, Throwable ex)
  {
    JOptionPane.showMessageDialog(null, "Could not connect to " + service + " service provider!"
        + "\nException:\n" + ex + "\n" + ex.getMessage(), "Error!", JOptionPane.PLAIN_MESSAGE);
  }

    private void uriServiceDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uriServiceDirectoryActionPerformed
      // TODO add your handling code here:
    }//GEN-LAST:event_uriServiceDirectoryActionPerformed

  @SuppressWarnings("unchecked")
    private void load_URI_links1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_load_URI_links1ActionPerformed
      try {
        summaryList = GroundMOAdapterImpl.retrieveProvidersFromDirectory(isS2G,
            this.getAddressToBeUsed());

        DefaultListModel listOfProviders = new DefaultListModel();

        for (ProviderSummary summary : summaryList) {
          listOfProviders.addElement(summary.getProviderKey().getInstId().toString()
              + ". " + summary.getProviderName().toString());
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
        Logger.getLogger(DirectoryConnectionConsumerPanel.class.getName()).log(Level.SEVERE, null,
            ex);
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

  private URI getAddressToBeUsed()
  {  // updates the
    return new URI(this.uriServiceDirectory.getText());
  }

  private void initTextBoxAddress()
  {  // runs during the init of the app
    // Common services
    SingleConnectionDetails details = connectionConsumer.getServicesDetails().get(
        DirectoryHelper.DIRECTORY_SERVICE_NAME);

    if (details != null) {
      this.uriServiceDirectory.setText(details.getProviderURI().toString());
    }
  }

  public class CloseMouseHandler implements MouseListener
  {

    private final javax.swing.JPanel panel;
    private final ProviderTabPanel providerPanel;

    CloseMouseHandler(final javax.swing.JPanel panel, final ProviderTabPanel providerPanel)
    {
      this.panel = panel;
      this.providerPanel = providerPanel;
    }

    @Override
    public void mouseClicked(MouseEvent evt)
    {
      Thread t1 = new Thread()
      {
        @Override
        public void run()
        {
          this.setName("CloseButtonTabThread");
          for (int i = 0; i < tabs.getTabCount(); i++) {
            Component component = tabs.getTabComponentAt(i);

            if (component == panel) {
              tabs.remove(i);

              try {
                providerPanel.getServices().closeConnections();
              } catch (Exception ex) {
                Logger.getLogger(DirectoryConnectionConsumerPanel.class.getName()).log(Level.WARNING,
                    "The connection was not closed correctly. Maybe the provider was unreachable!");
              }

              return;
            }
          }
        }
      };

      t1.start();
    }

    @Override
    public void mousePressed(MouseEvent me)
    {
    }

    @Override
    public void mouseReleased(MouseEvent me)
    {
    }

    @Override
    public void mouseEntered(MouseEvent me)
    {
    }

    @Override
    public void mouseExited(MouseEvent me)
    {
    }
  }

}
