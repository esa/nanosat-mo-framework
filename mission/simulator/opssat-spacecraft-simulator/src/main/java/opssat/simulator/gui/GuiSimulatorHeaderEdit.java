/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2021      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 *  You may not use this file except in compliance with the License.
 * 
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *  
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 *  ----------------------------------------------------------------------------
 */
package opssat.simulator.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import opssat.simulator.util.SimulatorHeader;

/**
 *
 * @author Cezar Suteu
 */
public class GuiSimulatorHeaderEdit {

    private GuiMainWindow parent;
    private JCheckBox chkSystemEnableDefault;
    private JCheckBox chkTimeRunDefault;
    private SimulatorHeader simulatorHeader;
    private JTextField txtTimeFactor;
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JFrame frame;
    private JButton submitButton;
    private boolean startDateOK, endDateOK, timeFactorOK;

    public JFrame getFrame() {
        return frame;
    }

    public void dispose() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(1000);

            } catch (final InterruptedException ex) {
                Logger.getLogger(GuiSimulatorHeaderEdit.class.getName()).log(Level.SEVERE, null, ex);
            }
            frame.dispose();
        });
    }

    private boolean validateForm() {
        return startDateOK && endDateOK && timeFactorOK;
    }

    public void makeFormColor(final Color color) {
        txtTimeFactor.setBackground(color);
        txtStartDate.setBackground(color);
        txtEndDate.setBackground(color);
        chkSystemEnableDefault.setBackground(color);
        chkTimeRunDefault.setBackground(color);
    }

    private void submitButtonPressed() {

        txtTimeFactor.setEnabled(false);
        txtStartDate.setEnabled(false);
        txtEndDate.setEnabled(false);
        chkSystemEnableDefault.setEnabled(false);
        chkTimeRunDefault.setEnabled(false);
        txtTimeFactor.setDisabledTextColor(Color.black);
        txtStartDate.setDisabledTextColor(Color.black);
        txtEndDate.setDisabledTextColor(Color.black);
        //chkSystemEnableDefault.setd(Color.black);
        //chkTimeRunDefault.setDisabledTextColor(Color.black);
        GuiSimulatorHeaderEdit.this.parent.editHeaderReady();

    }

    private Date parseStringIntoDate(final JTextField txtField) {
        Date result = null;
        Date originalResult = null;
        DateFormat dateFormat = new SimpleDateFormat(simulatorHeader.DATE_FORMAT);
        dateFormat.setLenient(true);
        final String originalText = txtField.getText();
        try {
            result = dateFormat.parse(originalText);
            originalResult = dateFormat.parse(originalText);
        } catch (final ParseException ex) {
            Logger.getLogger(GuiSimulatorHeaderEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (result != null) {
            final DateFormat dateFormatYear = new SimpleDateFormat("yyyy");
            final String originalYear = dateFormatYear.format(result);
            //System.out.println("Original year "+originalYear+Integer.parseInt(originalYear));

            if (Integer.parseInt(originalYear) >= 2016 && Integer.parseInt(originalYear) < 2030) {
                //Input year is ok, shift it back so it can be accepted with FALSE lenient
                //System.out.println("Original year is ok");
                final String shiftedYear = String.valueOf(Integer.parseInt(originalYear) - 10);
                //System.out.println("Shifted year "+shiftedYear);
                String shiftedText = originalText;
                shiftedText = shiftedText.replaceAll(originalYear, shiftedYear);
                dateFormat = new SimpleDateFormat(simulatorHeader.DATE_FORMAT);
                dateFormat.setLenient(false);
                result = null;
                try {
                    result = dateFormat.parse(shiftedText);
                } catch (final ParseException ex) {
                    Logger.getLogger(GuiSimulatorHeaderEdit.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (result != null) {
                    //Lenient false was ok, return original parse with lenient true
                    result = originalResult;
                }
            } else {
                //Input year is out of range
                //System.out.println("Original year is bad");
                result = null;
            }
        }
        return result;
    }

    private void processTextFieldFocusGained(final JTextField textField) {
        textField.setBackground(Color.blue);
        textField.setForeground(Color.white);
    }

    private void processTextFieldTimeFactorInput(final JTextField textField) {
        final boolean dataOk;
        final String data = textField.getText();
        int newValue = 0;
        try {
            newValue = Integer.parseInt(data);
        } catch (final NumberFormatException ex) {
        }
        dataOk = (newValue >= 1 && newValue <= 1000);
        if (dataOk) {
            GuiSimulatorHeaderEdit.this.simulatorHeader.setTimeFactor(newValue);
            textField.setBackground(Color.white);
            textField.setForeground(Color.black);
            timeFactorOK = true;
        } else {
            textField.setBackground(Color.red);
            timeFactorOK = false;
        }

    }

    private Date processTextFieldDateInput(final JTextField textFieldStart, final JTextField textFieldEnd, int origin) {
        final Date newDateStart = parseStringIntoDate(textFieldStart);
        final Date newDateEnd = parseStringIntoDate(textFieldEnd);
        if (newDateStart != null && newDateEnd != null && newDateStart.before(newDateEnd)) {
            textFieldStart.setBackground(Color.white);
        } else {
            origin = 0;
            startDateOK = false;
            textFieldStart.setBackground(Color.red);
        }
        if (newDateStart != null && newDateEnd != null && newDateStart.before(newDateEnd)) {
            textFieldEnd.setBackground(Color.white);
        } else {
            origin = 0;
            endDateOK = false;
            textFieldEnd.setBackground(Color.red);
        }
        textFieldStart.setForeground(Color.black);
        textFieldEnd.setForeground(Color.black);
        if (origin == 1) {
            startDateOK = newDateStart != null;
            return newDateStart;
        } else if (origin == 2) {
            endDateOK = true;
            return newDateEnd;

        } else {
            return null;
        }
    }

    private void createAndShowGUI() {

        final String[] labels = {"System enabled: ",
            "Time running: ",
            "Time factor: ",
            "Start date: ",
            "End date: "};
        final ArrayList<Object> data;
        data = new ArrayList<>();

        final int timeFactor = simulatorHeader.getTimeFactor();
        data.add(timeFactor);
        final Date startDate = simulatorHeader.getStartDate();
        data.add(startDate);
        final Date endDate = simulatorHeader.getEndDate();
        data.add(endDate);

        final int labelsLength = labels.length;
        final List<JTextField> textFields = new ArrayList<>();

        // Create and populate the panel.
        final JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        for (int i = 0; i < labelsLength; i++) {
            final JPanel localPanel = new JPanel();

            final JLabel l = new JLabel(labels[i]);
            localPanel.add(l);
            if (i == 0) {
                chkSystemEnableDefault = new JCheckBox("", simulatorHeader.isAutoStartSystem());
                chkSystemEnableDefault.addItemListener(e -> {
                    GuiSimulatorHeaderEdit.this.simulatorHeader.setAutoStartSystem(e.getStateChange() == ItemEvent.SELECTED);
                    submitButton.setEnabled(true);
                });
                localPanel.add(chkSystemEnableDefault);
            } else if (i == 1) {
                chkTimeRunDefault = new JCheckBox("", simulatorHeader.isAutoStartTime());
                chkTimeRunDefault.addItemListener(e -> {
                    GuiSimulatorHeaderEdit.this.simulatorHeader.setAutoStartTime(e.getStateChange() == ItemEvent.SELECTED);
                    submitButton.setEnabled(true);
                });
                localPanel.add(chkTimeRunDefault);
            } else if (i == 2) {
                txtTimeFactor = new JTextField(String.valueOf(simulatorHeader.getTimeFactor()));
                txtTimeFactor.setPreferredSize(new Dimension(35, 20));
                txtTimeFactor.addActionListener(e -> processTextFieldTimeFactorInput(txtTimeFactor));
                txtTimeFactor.addFocusListener(new FocusListener() {
                    public void focusGained(final FocusEvent e) {
                        processTextFieldFocusGained(txtTimeFactor);
                    }

                    public void focusLost(final FocusEvent e) {
                        processTextFieldTimeFactorInput(txtTimeFactor);
                    }

                });

                localPanel.add(txtTimeFactor);
            } else if (i == 3) {
                txtStartDate = new JTextField(simulatorHeader.getStartDateString());
                txtStartDate.setPreferredSize(new Dimension(265, 20));
                txtStartDate.addActionListener(e -> {
                    final Date newDateStart = processTextFieldDateInput(txtStartDate, txtEndDate, 1);
                    if (newDateStart != null) {
                        GuiSimulatorHeaderEdit.this.simulatorHeader.setStartDate(newDateStart);
                    }
                });
                txtStartDate.addFocusListener(new FocusListener() {
                    public void focusGained(final FocusEvent e) {
                        processTextFieldFocusGained(txtStartDate);
                    }

                    public void focusLost(final FocusEvent e) {
                        final Date newDate = processTextFieldDateInput(txtStartDate, txtEndDate, 1);
                        if (newDate != null) {
                            GuiSimulatorHeaderEdit.this.simulatorHeader.setStartDate(newDate);

                        }
                    }

                });
                localPanel.add(txtStartDate);
            } else if (i == 4) {
                txtEndDate = new JTextField(simulatorHeader.getEndDateString());
                txtEndDate.setPreferredSize(new Dimension(265, 20));
                txtEndDate.addActionListener(e -> {
                    final Date newDate = processTextFieldDateInput(txtStartDate, txtEndDate, 2);
                    if (newDate != null) {
                        GuiSimulatorHeaderEdit.this.simulatorHeader.setEndDate(newDate);
                    }
                });
                txtEndDate.addFocusListener(new FocusListener() {
                    public void focusGained(final FocusEvent e) {
                        processTextFieldFocusGained(txtEndDate);
                    }

                    public void focusLost(final FocusEvent e) {
                        final Date newDate = processTextFieldDateInput(txtStartDate, txtEndDate, 2);
                        if (newDate != null) {
                            GuiSimulatorHeaderEdit.this.simulatorHeader.setEndDate(newDate);
                        }
                    }

                });
                localPanel.add(txtEndDate);
            }

            p.add(localPanel);
        }
        submitButton = new JButton("Submit to server");
        p.add(new JLabel());
        p.add(submitButton);
        p.setBorder(new TitledBorder(new EtchedBorder(), "Default values"));

        submitButton.addActionListener(e -> {
            if (validateForm()) {

                submitButtonPressed();

            } else {
                JOptionPane.showMessageDialog(frame, "Data not ok!"
                    + "\nTime factor:" + timeFactorOK
                    + "\nStart date:" + startDateOK
                    + "\nEnd date: " + endDateOK + ".");
            }
        });

        // Create and set up the window.
        frame = new JFrame("Simulator Header");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Set up the content pane.
        p.setOpaque(true); // content panes must be opaque
        frame.setContentPane(p);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public GuiSimulatorHeaderEdit(final SimulatorHeader simulatorHeader, final GuiMainWindow parent) {
        this.parent = parent;
        this.simulatorHeader = simulatorHeader;

        createAndShowGUI();
        timeFactorOK = true;
        startDateOK = true;
        endDateOK = true;
    }

}
