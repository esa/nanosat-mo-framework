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
import esa.mo.helpertools.helpers.HelperMisc;
import java.awt.EventQueue;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mc.MCHelper;

/**
 * This class provides a simple form for the control of the consumer.
 */
public class ConfigurationManager extends javax.swing.JFrame {

    private ConnectionConsumer connection = new ConnectionConsumer();

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     */
    public static void main(final String args[]) {

        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            // handle exception
        }

        try {
            final Properties sysProps = System.getProperties();

            final File file = new File(System.getProperty("provider.properties", "demoConsumer.properties"));
            if (file.exists()) {
                sysProps.putAll(HelperMisc.loadProperties(file.toURI().toURL(), "provider.properties"));
            }

            System.setProperties(sysProps);

            final String name = System.getProperty("application.name", "CCSDS Mission Operations - Consumer Interface");
            final ConfigurationManager gui = new ConfigurationManager(name);
       
/*
            ArchiveQuery wert = new ArchiveQuery();

            MOWindow genericObj = new MOWindow(wert, true);
            Object assfsfd = genericObj.getObject();
            assfsfd = null;
*/
            
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    gui.setVisible(true);
                }
            });
        } catch (MalformedURLException ex) {
            Logger.getLogger(ConfigurationManager.class.getName()).log(Level.SEVERE, "Exception thrown during initialisation of Demo Consumer {0}", ex);
        }
    }

    
    /**
     * Creates new form MityDemoProviderGui
     *
     * @param name The name to display on the title bar of the form.
     */
    public ConfigurationManager(final String name) {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle(name);

        try {
            connection.loadURIs();
        } catch (MalformedURLException ex) {
            JOptionPane.showMessageDialog(null, "The URIs could not be loaded from the file!", "Error", JOptionPane.PLAIN_MESSAGE);
            Logger.getLogger(ConfigurationManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            COMHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
            MCHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
        } catch (MALException ex) {
            Logger.getLogger(ConfigurationManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        tabs.insertTab("Communication Settings", null, new ConnectionConsumerPanel(connection, tabs), "Communications Tab", tabs.getTabCount());
        
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        communicationsTopPanel1 = new esa.mo.fw.configurationtool.stuff.CommunicationsTopPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        tabs = new javax.swing.JTabbedPane();
        homeTab = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jSeparator6 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        COM_button = new javax.swing.JButton();
        MC_button = new javax.swing.JButton();
        Platform_button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1000, 600));
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.BorderLayout(0, 4));

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        communicationsTopPanel1.setName("communicationsTopPanel1"); // NOI18N
        jSplitPane1.setLeftComponent(communicationsTopPanel1);

        jSplitPane2.setName("jSplitPane2"); // NOI18N

        tabs.setToolTipText("");
        tabs.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tabs.setMaximumSize(new java.awt.Dimension(800, 600));
        tabs.setMinimumSize(new java.awt.Dimension(800, 600));
        tabs.setName("tabs"); // NOI18N
        tabs.setPreferredSize(new java.awt.Dimension(800, 600));
        tabs.setRequestFocusEnabled(false);

        homeTab.setName("homeTab"); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Welcome!");
        jLabel6.setToolTipText("");
        jLabel6.setName("jLabel6"); // NOI18N
        homeTab.add(jLabel6);

        jPanel8.setName("jPanel8"); // NOI18N
        jPanel8.setPreferredSize(new java.awt.Dimension(2510, 25));

        jSeparator6.setName("jSeparator6"); // NOI18N
        jSeparator6.setPreferredSize(new java.awt.Dimension(700, 15));
        jPanel8.add(jSeparator6);

        homeTab.add(jPanel8);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esa/mo/fw/configurationtool/stuff/mo_pic.png"))); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        homeTab.add(jLabel3);

        tabs.addTab("Home", homeTab);

        jSplitPane2.setRightComponent(tabs);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        COM_button.setText("COM services");
        COM_button.setMaximumSize(new java.awt.Dimension(200, 23));
        COM_button.setMinimumSize(new java.awt.Dimension(200, 23));
        COM_button.setName("COM_button"); // NOI18N
        COM_button.setPreferredSize(new java.awt.Dimension(200, 23));
        COM_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                COM_buttonActionPerformed(evt);
            }
        });
        jPanel1.add(COM_button);

        MC_button.setText("Monitor & Control services");
        MC_button.setMaximumSize(new java.awt.Dimension(200, 23));
        MC_button.setMinimumSize(new java.awt.Dimension(200, 23));
        MC_button.setName("MC_button"); // NOI18N
        MC_button.setPreferredSize(new java.awt.Dimension(200, 23));
        MC_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MC_buttonActionPerformed(evt);
            }
        });
        jPanel1.add(MC_button);

        Platform_button.setText("Platform services");
        Platform_button.setMaximumSize(new java.awt.Dimension(200, 23));
        Platform_button.setMinimumSize(new java.awt.Dimension(200, 23));
        Platform_button.setName("Platform_button"); // NOI18N
        Platform_button.setPreferredSize(new java.awt.Dimension(200, 23));
        Platform_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Platform_buttonActionPerformed(evt);
            }
        });
        jPanel1.add(Platform_button);

        jSplitPane2.setLeftComponent(jPanel1);

        jSplitPane1.setRightComponent(jSplitPane2);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void COM_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_COM_buttonActionPerformed




    }//GEN-LAST:event_COM_buttonActionPerformed

    private void MC_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MC_buttonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_MC_buttonActionPerformed

    private void Platform_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Platform_buttonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Platform_buttonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton COM_button;
    private javax.swing.JButton MC_button;
    private javax.swing.JButton Platform_button;
    private esa.mo.fw.configurationtool.stuff.CommunicationsTopPanel communicationsTopPanel1;
    private javax.swing.JPanel homeTab;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane tabs;
    // End of variables declaration//GEN-END:variables
}
