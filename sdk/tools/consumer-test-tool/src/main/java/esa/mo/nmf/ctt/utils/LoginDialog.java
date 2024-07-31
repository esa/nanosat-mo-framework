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

import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.common.impl.consumer.LoginConsumerServiceImpl;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.common.directory.structures.ServiceCapability;
import org.ccsds.moims.mo.common.login.LoginHelper;
import org.ccsds.moims.mo.common.login.body.LoginResponse;
import org.ccsds.moims.mo.common.login.structures.Profile;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author marcel.mikolajko
 */
public class LoginDialog extends JDialog {

    private Blob authenticationId;
    private final SingleConnectionDetails loginConnection;
    private final SingleConnectionDetails archiveConnection;
    private final String localNamePrefix;
    private boolean loginSuccessful;
    private Throwable loginError;
    private LoginConsumerServiceImpl loginConsumer;
    private final IdentifierList domainForArchive;

    private Map<String, Long> roleNameToId = new HashMap<>();

    private JTextField userTextField;
    private JPasswordField passwordTextField;
    private JComboBox<String> rolesComboBox;
    private JLabel userLabel;
    private JLabel passwordLabel;
    private JLabel rolesLabel;
    private JButton loginButton;
    private JButton cancelButton;
    private JButton rolesButton;

    public LoginDialog(ServiceCapability loginCapability, ServiceCapability archiveCapability,
        IdentifierList providerDomain, IdentifierList domainForArchive, String localNamePrefix) {
        createLoginDialog();

        loginConnection = new SingleConnectionDetails();
        loginConnection.setProviderURI(loginCapability.getServiceAddresses().get(0).getServiceURI());
        loginConnection.setBrokerURI(loginCapability.getServiceAddresses().get(0).getBrokerURI());
        loginConnection.setDomain(providerDomain);

        archiveConnection = new SingleConnectionDetails();
        archiveConnection.setProviderURI(archiveCapability.getServiceAddresses().get(0).getServiceURI());
        archiveConnection.setBrokerURI(archiveCapability.getServiceAddresses().get(0).getBrokerURI());
        archiveConnection.setDomain(domainForArchive);

        this.domainForArchive = domainForArchive;
        this.localNamePrefix = localNamePrefix;

        this.setVisible(true);
        this.validate();
        this.repaint();
    }

    private void createLoginDialog() {
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setTitle("Login required");
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.HORIZONTAL;

        userLabel = new JLabel("Username: ");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        panel.add(userLabel, constraints);

        userTextField = new JTextField(50);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(5, 5, 5, 5);

        panel.add(userTextField, constraints);

        passwordLabel = new JLabel("Password: ");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.insets = new Insets(5, 5, 5, 5);

        panel.add(passwordLabel, constraints);

        passwordTextField = new JPasswordField(50);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(5, 5, 5, 5);

        panel.add(passwordTextField, constraints);

        rolesLabel = new JLabel("Role: ");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.insets = new Insets(5, 5, 5, 5);

        panel.add(rolesLabel, constraints);

        rolesComboBox = new JComboBox<>();
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(5, 5, 5, 5);

        panel.add(rolesComboBox, constraints);

        loginButton = new JButton("Login");
        cancelButton = new JButton("Cancel");
        rolesButton = new JButton("Get roles");

        loginButton.addActionListener(event -> {
            try {
                LoginConsumerServiceImpl loginConsumer = getLoginConsumer();
                Long role = roleNameToId.get(rolesComboBox.getSelectedItem());
                LoginResponse response = loginConsumer.getLoginStub().login(new Profile(new Identifier(userTextField
                    .getText()), role), new String(passwordTextField.getPassword()));
                authenticationId = response.getBodyElement0();
                loginConsumer.close();
                loginSuccessful = true;
                Logger.getLogger(LoginDialog.class.getName()).log(Level.INFO, "Logged in successfully!");
            } catch (MALException | MalformedURLException | MALInteractionException e) {
                loginSuccessful = false;
                loginError = e;
            }
            dispose();
        });

        cancelButton.addActionListener(event -> dispose());

        rolesButton.addActionListener(event -> {
            try {
                LoginConsumerServiceImpl loginConsumer = getLoginConsumer();
                LongList roles = loginConsumer.getLoginStub().listRoles(new Identifier(userTextField.getText()),
                    new String(passwordTextField.getPassword()));

                ArchiveConsumerServiceImpl archiveConsumer = new ArchiveConsumerServiceImpl(archiveConnection, null,
                    localNamePrefix);
                RolesArchiveAdapter archiveAdapter = new RolesArchiveAdapter();
                archiveConsumer.getArchiveStub().retrieve(LoginHelper.LOGINROLE_OBJECT_TYPE, domainForArchive, roles,
                    archiveAdapter);
                while (!archiveAdapter.isFinished()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {

                    }
                }
                archiveConsumer.close();
                rolesComboBox.setModel(new DefaultComboBoxModel<>(roleNameToId.keySet().toArray(new String[0])));
            } catch (MALException | MALInteractionException | MalformedURLException e) {
                Logger.getLogger(LoginDialog.class.getName()).log(Level.SEVERE, "Unexpected exception during listRoles",
                    e);
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(rolesButton);
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        panel.setPreferredSize(new Dimension(200, 100));
        //        userTextField.setPreferredSize(new Dimension(200, userTextField.getHeight()));
        userTextField.setMinimumSize(new Dimension(100, 20));
        passwordTextField.setMinimumSize(new Dimension(100, 20));
        rolesComboBox.setMinimumSize(new Dimension(100, 20));

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private LoginConsumerServiceImpl getLoginConsumer() throws MALException, MALInteractionException,
        MalformedURLException {
        if (loginConsumer == null) {
            loginConsumer = new LoginConsumerServiceImpl(loginConnection, null, null, localNamePrefix);
        }
        return loginConsumer;
    }

    public Blob getAuthenticationId() {
        return authenticationId;
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    public Throwable getLoginError() {
        return loginError;
    }

    private class RolesArchiveAdapter extends ArchiveAdapter {
        boolean finished = false;

        @Override
        public void retrieveResponseReceived(MALMessageHeader msgHeader, ArchiveDetailsList objDetails,
            ElementList objBodies, Map qosProperties) {
            for (int i = 0; i < objDetails.size(); ++i) {
                roleNameToId.put(((Identifier) objBodies.get(i)).getValue(), objDetails.get(i).getInstId());
            }
            finished = true;
        }

        @Override
        public void retrieveResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
            Map qosProperties) {
            Logger.getLogger(LoginDialog.class.getName()).log(Level.SEVERE,
                "Unexpected error during roles retrieval: " + error.getErrorName());
            finished = true;
        }

        public boolean isFinished() {
            return finished;
        }
    }

}
//------------------------------------------------------------------------------