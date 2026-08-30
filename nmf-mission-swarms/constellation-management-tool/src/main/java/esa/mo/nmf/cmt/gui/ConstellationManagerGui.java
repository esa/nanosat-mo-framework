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
import esa.mo.nmf.cmt.utils.NanoSat;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConstellationManagerGui extends JFrame {

    private final ConstellationManagementTool cmt;
    private JLabel lblConstellationName;
    private JPanel cmtPanel;
    private JMenuBar menuBar;
    private JMenu menuAddNanoSat;
    private JMenuItem miCreateBasicSim;
    private JMenuItem miCreateAdvancedSim;
    private JMenuItem miConnectNanoSat;
    private JMenu menuSimulation;
    private JMenuItem miStartAll;
    private JMenuItem miStopAll;
    private JLabel lblNanoSatSegmentNumber;
    private JButton btnPackageManager;
    private JTable tblNanoSatSegments;
    private JScrollPane spNanoSatSegmentsTable;
    private JButton btnAppManager;
    private JMenuItem miRemoveAll;
    private JTextPane tpContainerMonitoring;
    private DefaultTableModel tableModel;

    /**
     * Initializer Constructor. Provides the Constellation Manager GUI.
     *
     * @param cmt constellation manager object
     */
    public ConstellationManagerGui(ConstellationManagementTool cmt) {
        this.cmt = cmt;
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
                AddBasicSimulationGui addBasicNmfSim = new AddBasicSimulationGui(cmt);
            }
        });
        miConnectNanoSat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ConnectNanoSatGui connectNanoSat = new ConnectNanoSatGui(cmt);
            }
        });
        miCreateAdvancedSim.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                AddAdvancedSimulationGui addAdvancedSimulationGui = new AddAdvancedSimulationGui(cmt);
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
                        String id = tblNanoSatSegments.getValueAt(i, 1).toString();
                        NanoSat nanosat = cmt.getNanoSatSegmentByName(id);
                        Logger.getLogger(ConstellationManagementTool.class.getName()).log(
                                Level.INFO, "{0} checked", new Object[]{nanosat.getName()});
                        selectedNanoSatSegments.add(nanosat);
                    }
                }

                PackageManagerGui packageManagerGui = new PackageManagerGui(cmt, selectedNanoSatSegments);
            }
        });
        btnAppManager.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                final ArrayList<NanoSat> selectedNanoSatSegments = new ArrayList<NanoSat>();

                for (int i = 0; i < tblNanoSatSegments.getRowCount(); i++) {
                    boolean checked = Boolean.parseBoolean(tblNanoSatSegments.getValueAt(i, 0).toString());
                    if (checked) {
                        String id = tblNanoSatSegments.getValueAt(i, 1).toString();
                        NanoSat nanosat = cmt.getNanoSatSegmentByName(id);
                        Logger.getLogger(ConstellationManagementTool.class.getName()).log(
                                Level.INFO, "{0} checked", new Object[]{nanosat.getName()});
                        selectedNanoSatSegments.add(nanosat);
                    }
                }

                AppManagerGui appManagerGui = new AppManagerGui(cmt, selectedNanoSatSegments);
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

            public boolean isCellEditable(int row, int column) {
                // only let the user edit checkboxes in column 0
                return column == 0;
            }
        };

        this.tableModel.addColumn("");
        this.tableModel.addColumn("NanoSat Segment Name");
        this.tableModel.addColumn("IP Address");
        this.tableModel.addColumn("Type");

        this.tblNanoSatSegments = new JTable();
        this.tblNanoSatSegments.setModel(tableModel);

        this.tblNanoSatSegments.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                // Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.INFO, "{0} checked", new Object[]{cmt.getNanoSatSegmentByName(tblNanoSatSegments.getValueAt(tblNanoSatSegments.getSelectedRow(), 1).toString()).getName()});
                tpContainerMonitoring.setText("");

                if (tblNanoSatSegments.getSelectedRow() != -1) {
                    String containerName = tblNanoSatSegments.getValueAt(tblNanoSatSegments.getSelectedRow(), 1).toString();
                    // Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.INFO, "clicking " + containerName);

                    try {
                        String containerLog = cmt.getNanoSatSegmentByName(containerName).getLogs();

                        if (containerLog != "") {
                            tpContainerMonitoring.setText(containerLog);
                        }

                    } catch (IOException ex) {

                    }
                }
            }
        });

        this.spNanoSatSegmentsTable.setViewportView(this.tblNanoSatSegments);

        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        DefaultTableCellRenderer leftRender = new DefaultTableCellRenderer();

        centerRender.setHorizontalAlignment(JLabel.CENTER);
        leftRender.setHorizontalAlignment(JLabel.LEFT);

        TableColumnModel columns = tblNanoSatSegments.getColumnModel();

        columns.getColumn(0).setMaxWidth(30);
        columns.getColumn(1).setMinWidth(250);
        columns.getColumn(2).setMinWidth(120);
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
        lblNanoSatSegmentNumber.setText(String.valueOf(0));
        cmt.getConstellation().forEach(nanoSat -> {
            if (nanoSat.isActive()) {
                String ipAddress = "null";

                try {
                    ipAddress = nanoSat.getIPAddress();
                } catch (IOException ex) {
                    // ipAddress = null
                }

                String type = ("NanoSatSimulator".equals(nanoSat.getClass().getSimpleName()) ? "Simulation" : "NanoSat");

                addRowToNanoSatSegmentList(new Object[]{false, nanoSat.getName(), ipAddress, type});
                int cntr = Integer.parseInt(lblNanoSatSegmentNumber.getText());
                cntr++;
                lblNanoSatSegmentNumber.setText(String.valueOf(cntr));
            }
        });
    }

    public void removeAllSimulations() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Removing Docker containers");
        dialog.setSize(420, 150);
        dialog.setLocationRelativeTo(null);
        JLabel label = new JLabel("Removing simulated NanoSat segments. Please wait...");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(label);
        dialog.setVisible(true);

        cmt.removeAllSimulations();

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
        cmtPanel.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        cmtPanel.setForeground(new Color(-16301824));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(5, 5, 5, 5), -1, -1));
        cmtPanel.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        spNanoSatSegmentsTable = new JScrollPane();
        panel1.add(spNanoSatSegmentsTable, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setHorizontalScrollBarPolicy(31);
        panel1.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tpContainerMonitoring = new JTextPane();
        tpContainerMonitoring.setEditable(false);
        tpContainerMonitoring.setEnabled(true);
        tpContainerMonitoring.setText("");
        scrollPane1.setViewportView(tpContainerMonitoring);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 5, new Insets(0, 5, 0, 6), -1, -1));
        cmtPanel.add(panel2, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lblNanoSatSegmentNumber = new JLabel();
        lblNanoSatSegmentNumber.setHorizontalTextPosition(0);
        lblNanoSatSegmentNumber.setText("0");
        panel2.add(lblNanoSatSegmentNumber, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblConstellationName = new JLabel();
        lblConstellationName.setText("NanoSat Segments");
        lblConstellationName.setToolTipText("Constellation name");
        panel2.add(lblConstellationName, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        btnPackageManager = new JButton();
        btnPackageManager.setText("Package Manager");
        panel2.add(btnPackageManager, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnAppManager = new JButton();
        btnAppManager.setText("App Manager");
        panel2.add(btnAppManager, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        menuBar = new JMenuBar();
        menuBar.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        cmtPanel.add(menuBar, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 25), new Dimension(-1, 25), 0, false));
        menuSimulation = new JMenu();
        menuSimulation.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        menuSimulation.setHorizontalAlignment(2);
        menuSimulation.setText("Simulation");
        menuBar.add(menuSimulation, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        miRemoveAll = new JMenuItem();
        miRemoveAll.setText("Remove all simulated Segments");
        menuSimulation.add(miRemoveAll);
        menuAddNanoSat = new JMenu();
        menuAddNanoSat.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        menuAddNanoSat.setHorizontalAlignment(0);
        menuAddNanoSat.setText("NanoSat Segments");
        menuBar.add(menuAddNanoSat, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        miCreateBasicSim = new JMenuItem();
        miCreateBasicSim.setText("Create Basic NanoSat Simulation");
        menuAddNanoSat.add(miCreateBasicSim);
        miCreateAdvancedSim = new JMenuItem();
        miCreateAdvancedSim.setText("Create Advanced NanoSat Simulation");
        menuAddNanoSat.add(miCreateAdvancedSim);
        miConnectNanoSat = new JMenuItem();
        miConnectNanoSat.setText("Connect to NanoSat Segments");
        menuAddNanoSat.add(miConnectNanoSat);
        final Spacer spacer2 = new Spacer();
        menuBar.add(spacer2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        cmtPanel.add(spacer3, new GridConstraints(1, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        cmtPanel.add(spacer4, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return cmtPanel;
    }

}
