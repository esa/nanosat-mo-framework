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

import esa.mo.com.impl.consumer.LoginConsumerServiceImpl;
import java.awt.*;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.ccsds.moims.mo.com.login.body.LoginResponse;

import org.ccsds.moims.mo.com.structures.ServiceCapability;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.structures.*;

/**
 * @author marcel.mikolajko
 */
public class LoginDialog extends JDialog {

    private Blob authenticationId;
    private final SingleConnectionDetails loginConnection;
    private final String localNamePrefix;
    private boolean loginSuccessful;
    private Throwable loginError;
    private LoginConsumerServiceImpl loginConsumer;

    private JTextField userTextField;
    private JPasswordField passwordTextField;
    private JLabel userLabel;
    private JLabel passwordLabel;
    private JButton loginButton;
    private JButton cancelButton;

    public LoginDialog(ServiceCapability loginCapability, IdentifierList providerDomain,
            String localNamePrefix) {
        createLoginDialog();

        loginConnection = new SingleConnectionDetails(
                loginCapability.getServiceAddresses().get(0).getServiceURI(),
                loginCapability.getServiceAddresses().get(0).getBrokerURI(),
                providerDomain);

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

        loginButton = new JButton("Login");
        cancelButton = new JButton("Cancel");

        loginButton.addActionListener(event -> {
            try {
                LoginConsumerServiceImpl loginConsumer = getLoginConsumer();
                LoginResponse response = loginConsumer.getLoginStub().login(
                        new Identifier(userTextField.getText()),
                        new String(passwordTextField.getPassword()));
                authenticationId = response.getAuthId();
                loginConsumer.closeConnection();
                loginSuccessful = true;
                Logger.getLogger(LoginDialog.class.getName()).log(Level.INFO, "Logged in successfully!");
            } catch (MALException | MalformedURLException | MALInteractionException e) {
                loginSuccessful = false;
                loginError = e;
            }
            dispose();
        });

        cancelButton.addActionListener(event -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        panel.setPreferredSize(new Dimension(200, 100));
        userTextField.setMinimumSize(new Dimension(100, 20));
        passwordTextField.setMinimumSize(new Dimension(100, 20));

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

}
//------------------------------------------------------------------------------
