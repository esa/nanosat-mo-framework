package esa.mo.nmf.cmt.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import esa.mo.nmf.cmt.ConstellationManagementTool;
import esa.mo.nmf.cmt.utils.NanoSat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateGeofenceGui extends JFrame {
    private JButton btnToggleLeaveEnter;
    private JTextField tfFilePath;
    private JButton btnChooseFile;
    private JPanel createGeofencePenal;
    private JButton btnApplyGeofence;
    private boolean startOnEnter = true;
    private final ConstellationManagementTool cmt;
    private final ArrayList<NanoSat> selectedNanoSatSegments;
    private final String appName;

    public CreateGeofenceGui(ConstellationManagementTool cmt, ArrayList<NanoSat> selectedNanoSatSegments, String appName) {

        this.cmt = cmt;
        this.selectedNanoSatSegments = selectedNanoSatSegments;
        this.appName = appName;

        this.setContentPane(createGeofencePenal);
        this.setTitle("NMF App Geofence");
        this.setSize(500, 150);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setVisible(true);

        btnToggleLeaveEnter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (startOnEnter) {
                    btnToggleLeaveEnter.setText("leaving Geofence");
                    startOnEnter = !startOnEnter;
                } else {
                    btnToggleLeaveEnter.setText("entering Geofence");
                    startOnEnter = !startOnEnter;
                }
            }
        });
        btnChooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String file = "";
                JFileChooser chooser = new JFileChooser();
                int clickOpen = chooser.showOpenDialog(createGeofencePenal);

                if (clickOpen == JFileChooser.APPROVE_OPTION) {
                    file = chooser.getSelectedFile().getAbsolutePath();
                    tfFilePath.setText(file);
                }
            }
        });
        btnApplyGeofence.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                createGeofence(tfFilePath.getText());
            }
        });
    }

    /**
     * Read and process the given .csv file to create a geofence for the selected App.
     *
     * @param path .csv configuration file path
     */
    private void createGeofence(String path) {
        ArrayList<String[]> coordinats = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = "#";

            while ((line = br.readLine()) != null) {
                if (line.length() < 1 || line.charAt(0) == '#') {
                    // comment or blank - ignore this line
                } else {
                    String[] values = line.split(";");
                    if (values.length == 2) {
                        // TODO: validate GPS coordinates
                        coordinats.add(values);
                    } else {
                        throw new IllegalArgumentException("Invalid .csv Data");
                    }
                }
            }

            this.selectedNanoSatSegments.forEach(nanoSat ->  {
               nanoSat.createGeofence(coordinats, this.appName, startOnEnter);
            });

            this.dispose();

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.SEVERE, "Failed to create Geofence: ", ex.getMessage());
            JOptionPane.showMessageDialog(null, "Failed to create Geofence: Invalid .csv Data!", "Error", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.SEVERE, "Failed to create Geofence: ", ex);
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
        createGeofencePenal = new JPanel();
        createGeofencePenal.setLayout(new GridLayoutManager(6, 5, new Insets(5, 5, 5, 5), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Import GPS coordinates for App Geofence");
        createGeofencePenal.add(label1, new GridConstraints(0, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        createGeofencePenal.add(spacer1, new GridConstraints(1, 4, 5, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Start App when ");
        createGeofencePenal.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnToggleLeaveEnter = new JButton();
        btnToggleLeaveEnter.setText("entering Geofence");
        createGeofencePenal.add(btnToggleLeaveEnter, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        createGeofencePenal.add(spacer2, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tfFilePath = new JTextField();
        createGeofencePenal.add(tfFilePath, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Geofence .csv:");
        createGeofencePenal.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnChooseFile = new JButton();
        btnChooseFile.setText("Open");
        createGeofencePenal.add(btnChooseFile, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        createGeofencePenal.add(spacer3, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        createGeofencePenal.add(spacer4, new GridConstraints(4, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        btnApplyGeofence = new JButton();
        btnApplyGeofence.setText("Apply Geofence");
        createGeofencePenal.add(btnApplyGeofence, new GridConstraints(3, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return createGeofencePenal;
    }
}