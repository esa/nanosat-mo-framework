/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2021      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 *  You may not use this file except in compliance with the License.
 * 
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *  
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 *  ----------------------------------------------------------------------------
 */
package opssat.simulator.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import com.jcraft.jsch.UserInfo;

public class SFTPInformation extends JFrame implements UserInfo {

    private String password;
    private String username;
    private String knownHosts;
    private JTextField tfUsername;
    private JPasswordField passwordField;
    private SFTPInformation window;
    private GuiMainWindow parent;
    private JTextField tfKnownHosts;

    public SFTPInformation(GuiMainWindow parent) {
        this.parent = parent;
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window = this;
        setAlwaysOnTop(true);
        setTitle("Enter your connection information");
        SpringLayout springLayout = new SpringLayout();
        getContentPane().setLayout(springLayout);

        JLabel lblUsername = new JLabel("Username:");
        getContentPane().add(lblUsername);

        JLabel lblPassword = new JLabel("Password:");
        springLayout.putConstraint(SpringLayout.NORTH, lblPassword, 10, SpringLayout.SOUTH, lblUsername);
        springLayout.putConstraint(SpringLayout.EAST, lblPassword, 0, SpringLayout.EAST, lblUsername);
        getContentPane().add(lblPassword);

        tfUsername = new JTextField();
        springLayout.putConstraint(SpringLayout.NORTH, tfUsername, 0, SpringLayout.NORTH, lblUsername);
        springLayout.putConstraint(SpringLayout.WEST, tfUsername, 5, SpringLayout.EAST, lblUsername);
        getContentPane().add(tfUsername);
        tfUsername.setColumns(10);

        passwordField = new JPasswordField();
        springLayout.putConstraint(SpringLayout.EAST, tfUsername, 0, SpringLayout.EAST, passwordField);
        passwordField.setColumns(20);
        springLayout.putConstraint(SpringLayout.NORTH, passwordField, 0, SpringLayout.NORTH, lblPassword);
        springLayout.putConstraint(SpringLayout.WEST, passwordField, 5, SpringLayout.EAST, lblUsername);
        getContentPane().add(passwordField);

        JButton btnSubmit = new JButton("Submit");
        springLayout.putConstraint(SpringLayout.NORTH, btnSubmit, 6, SpringLayout.SOUTH, passwordField);
        springLayout.putConstraint(SpringLayout.EAST, btnSubmit, -189, SpringLayout.EAST, getContentPane());
        btnSubmit.addActionListener(arg0 -> {
            username = tfUsername.getText();
            password = new String(passwordField.getPassword());
            knownHosts = tfKnownHosts.getText();
            parent.connectStfp(window);
            window.dispose();
        });
        getContentPane().add(btnSubmit);
        this.getRootPane().setDefaultButton(btnSubmit);

        JLabel lblPathToKnown = new JLabel("Path to known hosts:");
        springLayout.putConstraint(SpringLayout.NORTH, lblUsername, 10, SpringLayout.SOUTH, lblPathToKnown);
        springLayout.putConstraint(SpringLayout.EAST, lblUsername, 0, SpringLayout.EAST, lblPathToKnown);
        springLayout.putConstraint(SpringLayout.NORTH, lblPathToKnown, 30, SpringLayout.NORTH, getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, lblPathToKnown, 10, SpringLayout.WEST, getContentPane());
        getContentPane().add(lblPathToKnown);

        tfKnownHosts = new JTextField();
        tfKnownHosts.setColumns(20);
        springLayout.putConstraint(SpringLayout.NORTH, tfKnownHosts, 0, SpringLayout.NORTH, lblPathToKnown);
        springLayout.putConstraint(SpringLayout.WEST, tfKnownHosts, 5, SpringLayout.EAST, lblPathToKnown);
        getContentPane().add(tfKnownHosts);

        JButton btnBrowse = new JButton("Browse");
        btnBrowse.addActionListener(arg0 -> {
            JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
            fc.setFileHidingEnabled(false);
            int res = fc.showOpenDialog(SFTPInformation.this);
            if (res == JFileChooser.APPROVE_OPTION) {
                String chosenName = fc.getSelectedFile().getAbsolutePath();
                tfKnownHosts.setText(chosenName);
            }
        });
        springLayout.putConstraint(SpringLayout.NORTH, btnBrowse, 0, SpringLayout.NORTH, tfKnownHosts);
        springLayout.putConstraint(SpringLayout.WEST, btnBrowse, 5, SpringLayout.EAST, tfKnownHosts);
        springLayout.putConstraint(SpringLayout.SOUTH, btnBrowse, 0, SpringLayout.SOUTH, tfKnownHosts);
        getContentPane().add(btnBrowse);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent windowEvent) {
                SFTPInformation.this.parent.frame.setEnabled(true);
            }
        });
    }

    public String getUsername() {
        return username;
    }

    public String getKnownHosts() {
        return knownHosts;
    }

    @Override
    public String getPassphrase() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean promptPassphrase(String msg) {
        return true;
    }

    @Override
    public boolean promptPassword(String msg) {
        return true;
    }

    @Override
    public boolean promptYesNo(String msg) {
        Object[] options = {"Yes", "No"};
        int result = JOptionPane.showOptionDialog(null, msg, "Warning", JOptionPane.DEFAULT_OPTION,
            JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        return result == 0;
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
}
