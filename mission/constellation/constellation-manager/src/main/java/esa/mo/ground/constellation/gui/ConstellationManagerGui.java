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
package esa.mo.ground.constellation.gui;

import esa.mo.ground.constellation.ConstellationManager;
import esa.mo.ground.constellation.NanoSat;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConstellationManagerGui extends JFrame {
    private JLabel lblConstellationName;
    private JPanel cmtPanel;
    private JMenuBar menuBar;
    private JMenu menuAddNanoSat;
    private JMenuItem miCreateBasicSim;
    private JMenuItem miCreateAdvancedSim;
    private JMenuItem miConnectNanoSat;
    private JMenu menuSimulation;
    private JMenuItem miCreateConfig;
    private JMenuItem miStartAll;
    private JMenuItem miRemoveAll;
    private JLabel lblNanoSatSegmentNumber;
    private JButton btnPackageManager;
    private JTable tblNanoSatSegments;
    private JScrollPane spNanoSatSegmentsTable;
    private JButton btnAppManager;
    private final ConstellationManager ncm;
    private DefaultTableModel tableModel;

    /**
     * Initializer Constructor. Provides the Constellation Manager GUI.
     *
     * @param ncm constellation manager object
     */
    public ConstellationManagerGui(ConstellationManager ncm) {

        this.ncm = ncm;

        this.lblNanoSatSegmentNumber.setText("0");

        this.setContentPane(cmtPanel);
        this.setTitle("CMT: Constellation Management Tool");
        this.setSize(700, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setVisible(true);

        initNanoSatSegmentTable();

        miCreateBasicSim.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                AddBasicSimulationGui addBasicNmfSim = new AddBasicSimulationGui(ncm);
            }
        });
        miConnectNanoSat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ConnectNanoSatGui connectNanoSat = new ConnectNanoSatGui(ncm);
            }
        });
        miRemoveAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                removeAllSimulations();
            }
        });
        btnPackageManager.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                final ArrayList<NanoSat> selectedNanoSatSegments = new ArrayList<NanoSat>();

                for (int i = 0; i < tblNanoSatSegments.getRowCount(); i++) {
                    boolean checked = Boolean.parseBoolean(tblNanoSatSegments.getValueAt(i, 0).toString());
                    if (checked) {

                        Logger.getLogger(ConstellationManager.class.getName()).log(Level.INFO, "{0} checked", new Object[]{ncm.getNanoSatSegmentByName(tblNanoSatSegments.getValueAt(i, 1).toString()).getName()});
                        selectedNanoSatSegments.add(ncm.getNanoSatSegmentByName(tblNanoSatSegments.getValueAt(i, 1).toString()));
                    }
                }

                PackageManagerGui packageManagerGui = new PackageManagerGui(ncm, selectedNanoSatSegments);
            }
        });
        btnAppManager.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                final ArrayList<NanoSat> selectedNanoSatSegments = new ArrayList<NanoSat>();

                for (int i = 0; i < tblNanoSatSegments.getRowCount(); i++) {
                    boolean checked = Boolean.parseBoolean(tblNanoSatSegments.getValueAt(i, 0).toString());
                    if (checked) {

                        Logger.getLogger(ConstellationManager.class.getName()).log(Level.INFO, "{0} checked", new Object[]{ncm.getNanoSatSegmentByName(tblNanoSatSegments.getValueAt(i, 1).toString()).getName()});
                        selectedNanoSatSegments.add(ncm.getNanoSatSegmentByName(tblNanoSatSegments.getValueAt(i, 1).toString()));
                    }
                }

                AppManagerGui appManagerGui = new AppManagerGui(ncm, selectedNanoSatSegments);
            }
        });
    }

    /**
     * Initialize the table that shows the NanoSat segments of the constellation.
     */
    private void initNanoSatSegmentTable() {
        this.tableModel = new DefaultTableModel() {
            public Class<?> getColumnClass(int column) {
                return (column == 0) ? Boolean.class : String.class;
            }
        };

        this.tableModel.addColumn("");
        this.tableModel.addColumn("NanoSat Segment Name");
        this.tableModel.addColumn("Type");

        this.tblNanoSatSegments = new JTable();
        this.tblNanoSatSegments.setModel(tableModel);

        this.spNanoSatSegmentsTable.setViewportView(this.tblNanoSatSegments);

        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        DefaultTableCellRenderer leftRender = new DefaultTableCellRenderer();

        centerRender.setHorizontalAlignment(JLabel.CENTER);
        leftRender.setHorizontalAlignment(JLabel.LEFT);

        TableColumnModel columns = tblNanoSatSegments.getColumnModel();

        columns.getColumn(0).setMaxWidth(30);
        columns.getColumn(1).setMinWidth(250);
        columns.getColumn(2).setMinWidth(100);
        columns.getColumn(2).setMaxWidth(150);

        columns.getColumn(1).setCellRenderer(leftRender);
        columns.getColumn(2).setCellRenderer(centerRender);
    }

    /**
     * Adds a row to the table that shows the NanoSat segments of the constellation.
     *
     * @param row row to be added to the table
     */
    private void addRowToNanoSatSegmentList(Object[] row) {
        this.tableModel.addRow(row);
    }

    /**
     * Refreshes the table that shows the NanoSat segments of the constellation.
     */
    public void refreshNanoSatSegmentList() {
        this.tableModel.setRowCount(0);
        //lblNanoSatSegmentNumber.setText(String.valueOf(0));
        ncm.getConstellation().forEach(nanoSat -> {
            if (nanoSat.isActive()) {
                addRowToNanoSatSegmentList(new Object[]{false, nanoSat.getName(), "simulation"});
                int cntr = Integer.parseInt(lblNanoSatSegmentNumber.getText());
                cntr++;
                //lblNanoSatSegmentNumber.setText(String.valueOf(cntr));
            }
        });
    }

    public void removeAllSimulations() {

        JDialog dialog = new JDialog();
        dialog.setTitle("Removing Docker containers");
        dialog.setSize(420, 150);
        dialog.setLocationRelativeTo(null);
        JLabel label = new JLabel("Removing simulated NanoSat segments. Please wait...");
        // label.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(label);
        dialog.setVisible(true);

        ncm.getConstellation().forEach(nanoSat -> {
            if (nanoSat.isActive()) {

                Logger.getLogger(ConstellationManager.class.getName()).log(Level.INFO, "Removing node {0}...", new Object[]{nanoSat.getName()});
                nanoSat.delete();
            }
        });
        dialog.dispose();
        refreshNanoSatSegmentList();
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
        cmtPanel = new JPanel();
        cmtPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        cmtPanel.setForeground(new Color(-16301824));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(5, 5, 5, 5), -1, -1));
        cmtPanel.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        spNanoSatSegmentsTable = new JScrollPane();
        panel1.add(spNanoSatSegmentsTable, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        cmtPanel.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lblNanoSatSegmentNumber = new JLabel();
        lblNanoSatSegmentNumber.setHorizontalTextPosition(0);
        lblNanoSatSegmentNumber.setText("0");
        panel2.add(lblNanoSatSegmentNumber, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblConstellationName = new JLabel();
        lblConstellationName.setText("NanoSat Segments");
        lblConstellationName.setToolTipText("Constellation name");
        panel2.add(lblConstellationName, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel2.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        btnPackageManager = new JButton();
        btnPackageManager.setText("Package Manager");
        panel2.add(btnPackageManager, new com.intellij.uiDesigner.core.GridConstraints(0, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnAppManager = new JButton();
        btnAppManager.setText("App Manager");
        panel2.add(btnAppManager, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        cmtPanel.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        cmtPanel.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, new Dimension(-1, 24), 0, false));
        menuBar = new JMenuBar();
        menuBar.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(menuBar, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, new Dimension(-1, 20), 0, false));
        menuSimulation = new JMenu();
        menuSimulation.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        menuSimulation.setHorizontalAlignment(2);
        menuSimulation.setText("Simulation");
        menuBar.add(menuSimulation, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        miCreateConfig = new JMenuItem();
        miCreateConfig.setText("Create Simulation Config");
        menuSimulation.add(miCreateConfig);
        miStartAll = new JMenuItem();
        miStartAll.setText("Start all simulated Segments");
        menuSimulation.add(miStartAll);
        miRemoveAll = new JMenuItem();
        miRemoveAll.setText("Remove all simulated Segments");
        menuSimulation.add(miRemoveAll);
        menuAddNanoSat = new JMenu();
        menuAddNanoSat.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        menuAddNanoSat.setHorizontalAlignment(0);
        menuAddNanoSat.setText("NanoSat Segments");
        menuBar.add(menuAddNanoSat, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        miCreateBasicSim = new JMenuItem();
        miCreateBasicSim.setText("Create Basic NanoSat Simulation");
        menuAddNanoSat.add(miCreateBasicSim);
        miCreateAdvancedSim = new JMenuItem();
        miCreateAdvancedSim.setText("Create Advanced NanoSat Simulation");
        menuAddNanoSat.add(miCreateAdvancedSim);
        miConnectNanoSat = new JMenuItem();
        miConnectNanoSat.setText("Connect to NanoSat Segments");
        menuAddNanoSat.add(miConnectNanoSat);
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        menuBar.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return cmtPanel;
    }

}
