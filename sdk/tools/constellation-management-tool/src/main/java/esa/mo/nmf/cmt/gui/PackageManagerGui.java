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
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.body.FindPackageResponse;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PackageManagerGui extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(ConstellationManagementTool.class.getName());
    private final ConstellationManagementTool cmt;
    private final ArrayList<NanoSat> selectedNanoSatSegments;
    private JScrollPane spPackageTable;
    private JButton btnInstall;
    private JButton btnUninstall;
    private JButton btnUpgrade;
    private JPanel PackageManagerPanel;
    private JButton btnRefresh;
    private DefaultTableModel tableModel;
    private JTable tblPackages;

    /**
     * Initializer Constructor.
     * This Class manages the Packages that are available on the selected NanoSats.
     */
    public PackageManagerGui(ConstellationManagementTool ncm, ArrayList<NanoSat> selectedNanoSatSegments) {
        this.cmt = ncm;
        this.cmt.connectToConstellationProviders();
        this.selectedNanoSatSegments = selectedNanoSatSegments;

        this.setContentPane(PackageManagerPanel);
        this.setTitle("Package Manager");
        this.setSize(800, 300);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setVisible(true);

        initPackageTable();

        btnInstall.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    int row = tblPackages.getSelectedRow();
                    String packageName = tblPackages.getModel().getValueAt(row, 0).toString();
                    selectedNanoSatSegments.forEach(nanoSat -> {
                        nanoSat.installPackage(packageName);
                    });

                    refreshPackageList();
                } catch (ArrayIndexOutOfBoundsException ex) {
                    JOptionPane.showMessageDialog(null, "Please select a package!", "Error", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "An unexpected Error occurred!", "Error", JOptionPane.INFORMATION_MESSAGE);
                    LOGGER.log(Level.SEVERE, "Failed to install package: ", ex);
                }
            }
        });
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                refreshPackageList();
            }
        });
        btnUninstall.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    int row = tblPackages.getSelectedRow();
                    String packageName = tblPackages.getModel().getValueAt(row, 0).toString();
                    selectedNanoSatSegments.forEach(nanoSat -> {
                        nanoSat.uninstallPackage(packageName);
                    });

                    refreshPackageList();
                } catch (ArrayIndexOutOfBoundsException ex) {
                    JOptionPane.showMessageDialog(null, "Please select a package!", "Error", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "An unexpected Error occurred!", "Error", JOptionPane.INFORMATION_MESSAGE);
                    LOGGER.log(Level.SEVERE, "Failed to uninstall package: ", ex);
                }
            }
        });
        btnUpgrade.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    int row = tblPackages.getSelectedRow();
                    String packageName = tblPackages.getModel().getValueAt(row, 0).toString();
                    selectedNanoSatSegments.forEach(nanoSat -> {
                        nanoSat.upgradePackage(packageName);
                    });

                    refreshPackageList();
                } catch (ArrayIndexOutOfBoundsException ex) {
                    JOptionPane.showMessageDialog(null, "Please select a package!", "Error", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "An unexpected Error occurred!", "Error", JOptionPane.INFORMATION_MESSAGE);
                    LOGGER.log(Level.SEVERE, "Failed to upgrade package: ", ex);
                }
            }
        });
    }

    /**
     * Initialize the table that shows the Packages that are available on the selected NanoSats.
     */
    private void initPackageTable() {

        String[] tableCol = new String[]{"Package Name", "Installed"};

        this.tableModel = new DefaultTableModel(new Object[][]{}, tableCol) {
            final Class[] types = new Class[]{String.class, String.class};

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };

        this.tblPackages = new JTable();
        this.tblPackages.setModel(tableModel);

        this.spPackageTable.setViewportView(this.tblPackages);

        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        DefaultTableCellRenderer leftRender = new DefaultTableCellRenderer();

        centerRender.setHorizontalAlignment(JLabel.CENTER);
        leftRender.setHorizontalAlignment(JLabel.LEFT);

        TableColumnModel columns = tblPackages.getColumnModel();

        columns.getColumn(0).setMinWidth(250);
        columns.getColumn(1).setMinWidth(100);
        columns.getColumn(1).setMaxWidth(150);

        columns.getColumn(0).setCellRenderer(leftRender);
        columns.getColumn(1).setCellRenderer(centerRender);

        refreshPackageList();
    }

    /**
     * Refreshes the table that shows the Packages that are available on the selected NanoSats.
     */
    public void refreshPackageList() {

        LOGGER.log(Level.INFO, "Refreshing Packages List... ");

        this.tableModel.setRowCount(0);

        HashMap<String, int[]> packageMap = new HashMap<>();

        this.selectedNanoSatSegments.forEach(nanoSat -> {

            FindPackageResponse packages = nanoSat.getAllPackages();

            IdentifierList names = packages.getBodyElement0();
            BooleanList installed = packages.getBodyElement1();

            int[] installedOnNodes;

            for (int i = 0; i < names.size(); i++) {
                if (packageMap.get(String.valueOf(names.get(i))) == null) {
                    installedOnNodes = new int[]{(installed.get(i) ? 1 : 0), 1};
                } else {
                    installedOnNodes = new int[]{(installed.get(i) ? packageMap.get(String.valueOf(names.get(i)))[0] + 1 : packageMap.get(String.valueOf(names.get(i)))[0]), packageMap.get(String.valueOf(names.get(i)))[1] + 1};
                }
                packageMap.put(String.valueOf(names.get(i)), installedOnNodes);
            }
        });

        for (String name : packageMap.keySet()) {
            this.tableModel.addRow(new Object[]{name, packageMap.get(name)[0] + "/" + packageMap.get(name)[1]});
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
        PackageManagerPanel = new JPanel();
        PackageManagerPanel.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(5, 5, 5, 5), -1, -1));
        PackageManagerPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        spPackageTable = new JScrollPane();
        panel1.add(spPackageTable, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(6, 1, new Insets(5, 5, 5, 5), -1, -1));
        PackageManagerPanel.add(panel2, new GridConstraints(0, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnInstall = new JButton();
        btnInstall.setText("Install");
        panel2.add(btnInstall, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        btnUninstall = new JButton();
        btnUninstall.setText("Uninstall");
        panel2.add(btnUninstall, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnUpgrade = new JButton();
        btnUpgrade.setText("Upgrade");
        panel2.add(btnUpgrade, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnRefresh = new JButton();
        btnRefresh.setText("Refresh");
        panel2.add(btnRefresh, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel2.add(spacer2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 10), null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        PackageManagerPanel.add(spacer3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return PackageManagerPanel;
    }

}
