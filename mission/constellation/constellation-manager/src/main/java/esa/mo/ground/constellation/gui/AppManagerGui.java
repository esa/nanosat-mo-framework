package esa.mo.ground.constellation.gui;

import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.ground.constellation.ConstellationManager;
import esa.mo.ground.constellation.NanoSat;
import esa.mo.ground.constellation.utils.NmfAppModel;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.structures.AppDetails;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppManagerGui extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(ConstellationManager.class.getName());
    private final ConstellationManager cmt;
    private final ArrayList<NanoSat> selectedNanoSatSegments;
    private final HashMap<Long, StringBuffer> outputBuffers = new HashMap<>();
    private JButton btnStart;
    private JPanel appManagerPanel;
    private JScrollPane spAppTable;
    private JButton btnRefresh;
    private JButton btnStop;
    private DefaultTableModel tableModel;
    private JTable tblApps;

    /**
     * Initializer Constructor.
     * This Class manages the Apps that are available on the selected NanoSats.
     */
    public AppManagerGui(ConstellationManager cmt, ArrayList<NanoSat> selectedNanoSatSegments) {

        this.cmt = cmt;
        this.cmt.connectToConstellationProviders();
        this.selectedNanoSatSegments = selectedNanoSatSegments;

        this.setContentPane(appManagerPanel);
        this.setTitle("CMT App Manager");
        this.setSize(700, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setVisible(true);

        initAppList();

        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                refreshAppList();
            }
        });
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int row = tblApps.getSelectedRow();
                String appName = tblApps.getModel().getValueAt(row, 0).toString();
                selectedNanoSatSegments.forEach(nanoSat -> {
                    nanoSat.runAppByName(appName);
                });

                refreshAppList();
            }
        });
        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int row = tblApps.getSelectedRow();
                String appName = tblApps.getModel().getValueAt(row, 0).toString();
                selectedNanoSatSegments.forEach(nanoSat -> {
                    nanoSat.stopAppByName(appName);
                });

                refreshAppList();
            }
        });
    }

    /**
     * Initialize the table that shows the Apps that are available on the selected NanoSats.
     */
    private void initAppList() {

        String[] tableCol = new String[]{"App Name", "Description", "Category", "Running"};

        this.tableModel = new DefaultTableModel(new Object[][]{}, tableCol) {
            final Class[] types = new Class[]{String.class, String.class, String.class, String.class};

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };

        this.tblApps = new JTable();
        this.tblApps.setModel(tableModel);

        this.spAppTable.setViewportView(this.tblApps);

        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        DefaultTableCellRenderer leftRender = new DefaultTableCellRenderer();

        centerRender.setHorizontalAlignment(JLabel.CENTER);
        leftRender.setHorizontalAlignment(JLabel.LEFT);

        TableColumnModel columns = tblApps.getColumnModel();

        columns.getColumn(0).setMinWidth(150);
        columns.getColumn(1).setMinWidth(100);
        columns.getColumn(2).setMaxWidth(100);
        columns.getColumn(2).setMinWidth(100);
        columns.getColumn(3).setMaxWidth(100);
        columns.getColumn(3).setMinWidth(100);

        columns.getColumn(0).setCellRenderer(leftRender);
        columns.getColumn(1).setCellRenderer(centerRender);
        columns.getColumn(2).setCellRenderer(centerRender);
        columns.getColumn(3).setCellRenderer(centerRender);

        refreshAppList();
    }

    /**
     * Refreshes the list that shows the Apps that are available on the selected NanoSats.
     */
    private void refreshAppList() {

        LOGGER.log(Level.INFO, "Refreshing Apps List... ");

        this.tableModel.setRowCount(0);

        HashMap<String, NmfAppModel> appMap = new HashMap<>();

        try {
            this.selectedNanoSatSegments.forEach(nanoSat -> {

                List<ArchivePersistenceObject> apps = nanoSat.getAllApps();

                if (apps != null) {
                    for (ArchivePersistenceObject app : apps) {
                        AppDetails appDetails = (AppDetails) app.getObject();

                        NmfAppModel appObject;

                        if (appMap.get(appDetails.getName().toString()) == null) {
                            appObject = new NmfAppModel(appDetails.getName().toString(), appDetails.getDescription(), appDetails.getCategory().toString(), appDetails.getRunning());
                        } else {
                            appObject = appMap.get(appDetails.getName().toString());
                            if (appDetails.getRunning()) {
                                appObject.increaseInstalledAndRunning();
                            } else {
                                appObject.increaseInstalled();
                            }
                        }
                        appMap.put(appDetails.getName().toString(), appObject);
                    }
                }
            });

            for (Map.Entry<String, NmfAppModel> appEntry : appMap.entrySet()) {
                NmfAppModel app = appEntry.getValue();
                this.tableModel.addRow(new Object[]{app.getName(), app.getDescription(), app.getCategory(), app.getRunningCounter()});

            }

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to get available Apps from NanoSats: ", ex);
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
        appManagerPanel = new JPanel();
        appManagerPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(5, 5, 5, 5), -1, -1));
        appManagerPanel.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(251, 24), null, 0, false));
        spAppTable = new JScrollPane();
        panel1.add(spAppTable, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 1, new Insets(5, 5, 5, 5), -1, -1));
        appManagerPanel.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnStart = new JButton();
        btnStart.setText("Start");
        panel2.add(btnStart, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel2.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        btnRefresh = new JButton();
        btnRefresh.setText("Refresh");
        panel2.add(btnRefresh, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        panel2.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 10), null, null, 0, false));
        btnStop = new JButton();
        btnStop.setText("Stop");
        panel2.add(btnStop, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        appManagerPanel.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return appManagerPanel;
    }

}
