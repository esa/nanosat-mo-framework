package esa.mo.nmf.cmt.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.nmf.cmt.ConstellationManagementTool;
import esa.mo.nmf.cmt.utils.NanoSat;
import esa.mo.nmf.cmt.utils.NmfAppModel;
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
    private static final Logger LOGGER = Logger.getLogger(ConstellationManagementTool.class.getName());
    private final ConstellationManagementTool cmt;
    private final ArrayList<NanoSat> selectedNanoSatSegments;
    private final HashMap<Long, StringBuffer> outputBuffers = new HashMap<>();
    private JButton btnStart;
    private JPanel appManagerPanel;
    private JScrollPane spAppTable;
    private JButton btnRefresh;
    private JButton btnStop;
    private JButton btnSetGeofence;
    private DefaultTableModel tableModel;
    private JTable tblApps;

    /**
     * Initializer Constructor.
     * This Class manages the Apps that are available on the selected NanoSats.
     */
    public AppManagerGui(ConstellationManagementTool cmt, ArrayList<NanoSat> selectedNanoSatSegments) {

        this.cmt = cmt;
        this.cmt.connectToConstellationProviders();
        this.selectedNanoSatSegments = selectedNanoSatSegments;

        this.setContentPane(appManagerPanel);
        this.setTitle("App Manager");
        this.setSize(800, 300);
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
                try {
                    int row = tblApps.getSelectedRow();
                    String appName = tblApps.getModel().getValueAt(row, 0).toString();
                    selectedNanoSatSegments.forEach(nanoSat -> {
                        nanoSat.runAppByName(appName);
                    });

                    refreshAppList();
                } catch (ArrayIndexOutOfBoundsException ex) {
                    JOptionPane.showMessageDialog(null, "Please select an App!", "Error", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "An unexpected Error occurred!", "Error", JOptionPane.INFORMATION_MESSAGE);
                    LOGGER.log(Level.SEVERE, "Failed to start App: ", ex);
                }
            }
        });
        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    int row = tblApps.getSelectedRow();
                    String appName = tblApps.getModel().getValueAt(row, 0).toString();
                    selectedNanoSatSegments.forEach(nanoSat -> {
                        nanoSat.stopAppByName(appName);
                    });

                    refreshAppList();
                } catch (ArrayIndexOutOfBoundsException ex) {
                    JOptionPane.showMessageDialog(null, "Please select an App!", "Error", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "An unexpected Error occurred!", "Error", JOptionPane.INFORMATION_MESSAGE);
                    LOGGER.log(Level.SEVERE, "Failed to stop App: ", ex);
                }
            }
        });
        btnSetGeofence.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    int row = tblApps.getSelectedRow();
                    String appName = tblApps.getModel().getValueAt(row, 0).toString();

                    CreateGeofenceGui createGeofenceGui = new CreateGeofenceGui(cmt, selectedNanoSatSegments, appName);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    JOptionPane.showMessageDialog(null, "Please select an App!", "Error", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "An unexpected Error occurred!", "Error", JOptionPane.INFORMATION_MESSAGE);
                    LOGGER.log(Level.SEVERE, "Failed to start Geofence Gui: ", ex);
                }
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
        appManagerPanel.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(5, 5, 5, 5), -1, -1));
        appManagerPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(251, 24), null, 0, false));
        spAppTable = new JScrollPane();
        panel1.add(spAppTable, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(6, 1, new Insets(5, 5, 5, 5), -1, -1));
        appManagerPanel.add(panel2, new GridConstraints(0, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnStart = new JButton();
        btnStart.setText("Start");
        panel2.add(btnStart, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        btnRefresh = new JButton();
        btnRefresh.setText("Refresh");
        panel2.add(btnRefresh, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel2.add(spacer2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 10), null, null, 0, false));
        btnStop = new JButton();
        btnStop.setText("Stop");
        panel2.add(btnStop, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnSetGeofence = new JButton();
        btnSetGeofence.setText("Set Geofence");
        panel2.add(btnSetGeofence, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        appManagerPanel.add(spacer3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return appManagerPanel;
    }

}
