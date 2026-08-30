/* ----------------------------------------------------------------------------
 * Copyright (C) 2022      European Space Agency
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
 *
 * Author: N Wiegand (https://github.com/Klabau)
 */
package esa.mo.nmf.cmt.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import esa.mo.nmf.cmt.ConstellationManagementTool;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectNanoSatGui extends JFrame {

    private JPanel connectNanoSatPanel;
    private JTextField tfFilePath;
    private JButton btnChooseFile;
    private JButton btnConnect;
    private final ConstellationManagementTool cmt;

    /**
     * Initializer Constructor. Provides the GUI to connect the cmt to existing NanoSats.
     *
     * @param cmt constellation manager object
     */
    public ConnectNanoSatGui(ConstellationManagementTool cmt) {
        this.cmt = cmt;
        this.setContentPane(connectNanoSatPanel);
        this.setTitle("Connect to NanoSat Segments");
        this.setSize(400, 123);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setVisible(true);

        btnChooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String file = "";
                JFileChooser chooser = new JFileChooser();
                int clickOpen = chooser.showOpenDialog(connectNanoSatPanel);

                if (clickOpen == JFileChooser.APPROVE_OPTION) {
                    file = chooser.getSelectedFile().getAbsolutePath();
                    tfFilePath.setText(file);
                }
            }
        });
        btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                connectToNanoSatSegments(tfFilePath.getText());
            }
        });
    }

    /**
     * Connect to the NanoSat segments that have been provided by the .csv file.
     *
     * @param path .csv file path with NanoSat segments connection information
     */
    private void connectToNanoSatSegments(String path) {
        HashMap<String, String> nanoSatSegments = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = "#";

            while ((line = br.readLine()) != null) {
                if (line.length() < 1 || line.charAt(0) == '#') {
                    // comment or blank - ignore this line
                } else {
                    String[] values = line.split(";");
                    if (nanoSatSegments.get(values[0]) != null || !this.cmt.isNanoSatSegmentNameUnique(values[0])) {
                        throw new IllegalArgumentException("Duplicated NanoSat Segment! Names must be unique.");
                    } else {
                        nanoSatSegments.put(values[0], values[1]);
                    }
                }
            }

            this.cmt.connectToNanoSatSegments(nanoSatSegments);

            this.dispose();

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.SEVERE,
                    "Failed to add NanoSat Segments to constellation: ", ex.getMessage());
            JOptionPane.showMessageDialog(null, "Failed to add NanoSat Segments to constellation: Names must be unique!", "Error", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "File not found! Please select a valid .csv file.", "Error", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.SEVERE, "Failed to add NanoSat Segments to constellation: ", ex);
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        connectNanoSatPanel = new JPanel();
        connectNanoSatPanel.setLayout(new GridLayoutManager(4, 3, new Insets(5, 5, 5, 5), -1, -1));
        tfFilePath = new JTextField();
        connectNanoSatPanel.add(tfFilePath, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        btnChooseFile = new JButton();
        btnChooseFile.setText("Open");
        connectNanoSatPanel.add(btnChooseFile, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Connections .csv:");
        connectNanoSatPanel.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        connectNanoSatPanel.add(spacer1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        btnConnect = new JButton();
        btnConnect.setText("Connect");
        connectNanoSatPanel.add(btnConnect, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Connect to existing NanoSat Segments");
        connectNanoSatPanel.add(label2, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return connectNanoSatPanel;
    }

}
