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

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConstellationManagerGui extends JFrame {
    private JLabel lblConstellationName;
    private JPanel statusPanel;
    private JMenuBar menuBar;
    private JMenu menuConstellation;
    private JMenu menuAddNanoSat;
    private JMenuItem miCreateBasicSim;
    private JMenuItem miCreateAdvancedSim;
    private JMenuItem miConnectNanoSat;
    private JMenu menuSimulation;
    private JMenuItem miCreateConfig;
    private JMenuItem miStartAll;
    private JMenuItem miStopAll;
    private JLabel lblNanoSatSegmentNumber;
    private JButton btnAppManager;
    private JTable tblNanoSatSegments;
    private JScrollPane spNanoSatSegments;

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

        this.setContentPane(statusPanel);
        this.setTitle("NCM: NanoSat Constellation Manager");
        this.setSize(700, 400);
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
        btnAppManager.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for (int i = 0; i < tblNanoSatSegments.getRowCount(); i++) {
                    Boolean checked = Boolean.valueOf(tblNanoSatSegments.getValueAt(i, 0).toString());
                }
            }
        });
    }

    /**
     * Initialize the table that shows the NanoSat segments of the constellation.
     */
    private void initNanoSatSegmentTable() {
        this.tableModel = new DefaultTableModel() {
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Boolean.class;
                    case 1:
                    case 2:
                    default:
                        return String.class;
                }
            }
        };

        this.tableModel.addColumn("");
        this.tableModel.addColumn("Name");
        this.tableModel.addColumn("Status");
        this.tableModel.addColumn("Type");

        this.tblNanoSatSegments = new JTable();
        this.tblNanoSatSegments.setModel(tableModel);

        this.spNanoSatSegments.setViewportView(this.tblNanoSatSegments);

        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        DefaultTableCellRenderer leftRender = new DefaultTableCellRenderer();

        centerRender.setHorizontalAlignment(JLabel.CENTER);
        leftRender.setHorizontalAlignment(JLabel.LEFT);

        TableColumnModel columns = tblNanoSatSegments.getColumnModel();

        columns.getColumn(0).setMaxWidth(50);
        columns.getColumn(1).setMinWidth(250);
        columns.getColumn(2).setMinWidth(100);
        columns.getColumn(2).setMaxWidth(150);
        columns.getColumn(3).setMinWidth(100);
        columns.getColumn(3).setMaxWidth(150);

        columns.getColumn(1).setCellRenderer(leftRender);
        columns.getColumn(2).setCellRenderer(centerRender);
        columns.getColumn(3).setCellRenderer(centerRender);

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
        ncm.getConstellation().forEach(nanoSat -> {
            addRowToNanoSatSegmentList(new Object[] { false, nanoSat.getName(), "active", "simulation" });
        });
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
        statusPanel = new JPanel();
        statusPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        statusPanel.setForeground(new Color(-16301824));
        menuBar = new JMenuBar();
        menuBar.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        statusPanel.add(menuBar,
                new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null, 0, false));
        menuConstellation = new JMenu();
        menuConstellation.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        menuConstellation.setHorizontalAlignment(0);
        menuConstellation.setText("Constellation");
        menuBar.add(menuConstellation,
                new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, new Dimension(96, 19), null, 0, false));
        menuAddNanoSat = new JMenu();
        menuAddNanoSat.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        menuAddNanoSat.setHorizontalAlignment(0);
        menuAddNanoSat.setText("Add NanoSat Segments");
        menuConstellation.add(menuAddNanoSat);
        miCreateBasicSim = new JMenuItem();
        miCreateBasicSim.setText("Create Basic NanoSat Simulation");
        menuAddNanoSat.add(miCreateBasicSim);
        miCreateAdvancedSim = new JMenuItem();
        miCreateAdvancedSim.setText("Create Advanced NanoSat Simulation");
        menuAddNanoSat.add(miCreateAdvancedSim);
        miConnectNanoSat = new JMenuItem();
        miConnectNanoSat.setText("Connect to NanoSat Segments");
        menuAddNanoSat.add(miConnectNanoSat);
        menuSimulation = new JMenu();
        menuSimulation.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        menuSimulation.setHorizontalAlignment(2);
        menuSimulation.setText("Simulation");
        menuConstellation.add(menuSimulation);
        miCreateConfig = new JMenuItem();
        miCreateConfig.setText("Create Simulation Config");
        menuSimulation.add(miCreateConfig);
        miStartAll = new JMenuItem();
        miStartAll.setText("Start all simulated Segments");
        menuSimulation.add(miStartAll);
        miStopAll = new JMenuItem();
        miStopAll.setText("Stop all simulated Segments");
        menuSimulation.add(miStopAll);
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        menuBar.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1,
                com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,
                com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(5, 5, 5, 5), -1, -1));
        statusPanel.add(panel1,
                new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null, 0, false));
        spNanoSatSegments = new JScrollPane();
        panel1.add(spNanoSatSegments,
                new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                        null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        statusPanel.add(panel2,
                new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null, 1, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        panel2.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1,
                com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,
                com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        lblConstellationName = new JLabel();
        lblConstellationName.setText("NanoSat Segments");
        lblConstellationName.setToolTipText("Constellation name");
        panel2.add(lblConstellationName,
                new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblNanoSatSegmentNumber = new JLabel();
        lblNanoSatSegmentNumber.setText("0");
        panel2.add(lblNanoSatSegmentNumber,
                new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnAppManager = new JButton();
        btnAppManager.setText("App Manager");
        panel2.add(btnAppManager,
                new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        statusPanel.add(spacer3,
                new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return statusPanel;
    }

}
