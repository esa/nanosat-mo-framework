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
import esa.mo.nmf.cmt.utils.SegmentImage;
import esa.mo.nmf.cmt.utils.SegmentOrbits;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddSimulationFromCsvGui extends JFrame {
    private final ConstellationManagementTool cmt;
    private JPanel addSimulationFromCsvPanel;
    private JTextField tfFilePath;
    private JButton btnChooseFile;
    private JButton btnCreateSimulation;
    private JComboBox<SegmentImage> cbImage;

    public AddSimulationFromCsvGui(ConstellationManagementTool cmt) {

        this.cmt = cmt;

        this.setContentPane(addSimulationFromCsvPanel);
        this.setTitle("Create Simulation from CSV File");
        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setVisible(true);

        btnChooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String file = "";
                JFileChooser chooser = new JFileChooser();
                int clickOpen = chooser.showOpenDialog(addSimulationFromCsvPanel);

                if (clickOpen == JFileChooser.APPROVE_OPTION) {
                    file = chooser.getSelectedFile().getAbsolutePath();
                    tfFilePath.setText(file);
                }
            }
        });
        btnCreateSimulation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                addSimulationFromCsv(tfFilePath.getText());
            }
        });
    }

    /**
     * Read and process the given .csv configuration file to add the segments to the constellation
     *
     * @param path .csv configuration file path
     */
    private void addSimulationFromCsv(String path) {
        try {
            Map<String, String[]> nanoSatConfigurations = SegmentOrbits.read(new File(path));

            for (String name : nanoSatConfigurations.keySet()) {
                if (!this.cmt.isNanoSatSegmentNameUnique(ConstellationManagementTool.segmentName(name))) {
                    throw new IllegalArgumentException("A segment of the constellation already goes "
                            + "by this name: " + name);
                }
            }

            this.cmt.addSimulationsWithOrbits(nanoSatConfigurations,
                    (SegmentImage) cbImage.getSelectedItem());

            this.dispose();
            JOptionPane.showMessageDialog(null,
                    "Successfully added " + nanoSatConfigurations.size()
                    + " nodes to the constellation!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.SEVERE, "Failed to add NanoSat Segments to constellation: ", ex.getMessage());
            JOptionPane.showMessageDialog(null, "Failed to add NanoSat Segments to constellation: "
                    + ex.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "File not found! Please select a valid .csv file.", "Error", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.SEVERE, "Failed to add NanoSat Segments to constellation: ", ex);
            JOptionPane.showMessageDialog(null,
                    "Failed to add nodes to the constellation: " + ex,
                    "Error", JOptionPane.INFORMATION_MESSAGE);
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
     * &gt;&gt;&gt; IMPORTANT!! &lt;&lt;&lt;
     * DO NOT edit this method OR call it in your code!
     */
    private void $$$setupUI$$$() {
        addSimulationFromCsvPanel = new JPanel();
        addSimulationFromCsvPanel.setLayout(new GridLayoutManager(5, 3, new Insets(5, 5, 5, 5), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Import a .csv configuration file to create a simulation");
        addSimulationFromCsvPanel.add(label1, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Configuration .csv:");
        addSimulationFromCsvPanel.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfFilePath = new JTextField();
        addSimulationFromCsvPanel.add(tfFilePath, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        btnChooseFile = new JButton();
        btnChooseFile.setText("Open");
        addSimulationFromCsvPanel.add(btnChooseFile, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Image:");
        addSimulationFromCsvPanel.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbImage = new JComboBox<>(SegmentImage.values());
        addSimulationFromCsvPanel.add(cbImage, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        btnCreateSimulation = new JButton();
        btnCreateSimulation.setText("Create Simulation");
        addSimulationFromCsvPanel.add(btnCreateSimulation, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        addSimulationFromCsvPanel.add(spacer1, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    public JComponent $$$getRootComponent$$$() {
        return addSimulationFromCsvPanel;
    }

}
