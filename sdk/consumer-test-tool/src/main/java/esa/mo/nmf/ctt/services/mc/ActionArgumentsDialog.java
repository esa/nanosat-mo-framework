/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
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
 */
package esa.mo.nmf.ctt.services.mc;

import java.awt.*;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperAttributes;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mc.structures.ActionDefinition;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinition;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionList;
import org.ccsds.moims.mo.mc.structures.AttributeValue;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;

/**
 * Modal dialog for filling in the arguments of an action before execution.
 * Shows each argument's name, type, unit and an input field for the value.
 */
public class ActionArgumentsDialog extends JDialog {

    private final List<ArgumentDefinition> argumentDefs;
    private final List<JTextField> valueFields = new ArrayList<>();
    private boolean cancelled = true;
    private AttributeValueList result;

    /**
     * Creates the argument-entry dialog for the given action definition.
     *
     * @param actDef the action definition whose arguments are prompted for
     */
    public ActionArgumentsDialog(ActionDefinition actDef) {
        super((Frame) null, "Execute: " + actDef.getName().getValue(), true);
        ArgumentDefinitionList args = actDef.getArguments();
        this.argumentDefs = (args != null) ? args : new ArrayList<>();
        buildUI(actDef);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void buildUI(ActionDefinition actDef) {
        JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(content);

        // Header: action name + description
        JPanel header = new JPanel(new BorderLayout(4, 2));
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        JLabel nameLabel = new JLabel("Action: " + actDef.getName().getValue());
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 14f));
        header.add(nameLabel, BorderLayout.NORTH);
        if (actDef.getDescription() != null && !actDef.getDescription().isEmpty()) {
            JLabel descLabel = new JLabel("Description: " + actDef.getDescription());
            descLabel.setForeground(Color.DARK_GRAY);
            header.add(descLabel, BorderLayout.SOUTH);
        }
        content.add(header, BorderLayout.NORTH);

        // Arguments area
        JLabel argsTitle = new JLabel("Arguments");
        argsTitle.setFont(argsTitle.getFont().deriveFont(Font.BOLD, 14f));
        argsTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        if (argumentDefs.isEmpty()) {
            JLabel noArgs = new JLabel("This action has no arguments.", SwingConstants.CENTER);
            noArgs.setForeground(Color.GRAY);
            JPanel centrePanel = new JPanel(new BorderLayout());
            centrePanel.add(argsTitle, BorderLayout.NORTH);
            centrePanel.add(noArgs, BorderLayout.CENTER);
            content.add(centrePanel, BorderLayout.CENTER);
        } else {
            JPanel argsPanel = new JPanel(new GridBagLayout());
            argsPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 6, 3, 6);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;

            // Column headers
            gbc.gridy = 0;
            gbc.weightx = 0;
            addCell(argsPanel, boldLabel("Name"), gbc, 0);
            addCell(argsPanel, boldLabel("Type"), gbc, 1);
            addCell(argsPanel, boldLabel("Unit"), gbc, 2);
            gbc.weightx = 1.0;
            addCell(argsPanel, boldLabel("Value"), gbc, 3);

            gbc.gridy = 1;
            gbc.gridx = 0;
            gbc.gridwidth = 4;
            gbc.weightx = 1.0;
            argsPanel.add(new JSeparator(), gbc);
            gbc.gridwidth = 1;

            int currentRow = 2;
            for (int i = 0; i < argumentDefs.size(); i++) {
                ArgumentDefinition arg = argumentDefs.get(i);
                String description = (arg.getDescription() != null && !arg.getDescription().isEmpty())
                        ? arg.getDescription() : null;

                if (i > 0) {
                    gbc.gridy = currentRow++;
                    gbc.gridx = 0;
                    gbc.gridwidth = 4;
                    gbc.weightx = 1.0;
                    argsPanel.add(new JSeparator(), gbc);
                    gbc.gridwidth = 1;
                }

                gbc.gridy = currentRow++;
                gbc.weightx = 0;

                String argName = (arg.getArgId() != null) ? arg.getArgId().getValue() : "arg" + i;
                addCell(argsPanel, new JLabel(argName), gbc, 0);

                int typeShortForm = arg.getRawType().getValue();
                String typeName = HelperAttributes.typeShortForm2attributeName(typeShortForm);
                addCell(argsPanel, new JLabel(typeName), gbc, 1);

                String unit = (arg.getRawUnit() != null) ? arg.getRawUnit() : "";
                addCell(argsPanel, new JLabel(unit), gbc, 2);

                gbc.weightx = 1.0;
                JTextField field = new JTextField(15);
                valueFields.add(field);
                addCell(argsPanel, field, gbc, 3);

                if (description != null) {
                    gbc.gridy = currentRow++;
                    gbc.gridx = 0;
                    gbc.gridwidth = 4;
                    gbc.weightx = 1.0;
                    JLabel descLabel = new JLabel("<html><i>" + description + "</i></html>");
                    descLabel.setForeground(Color.GRAY);
                    argsPanel.add(descLabel, gbc);
                    gbc.gridwidth = 1;
                }
            }

            argsPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            JPanel centrePanel = new JPanel(new BorderLayout());
            centrePanel.add(argsTitle, BorderLayout.NORTH);
            centrePanel.add(argsPanel, BorderLayout.CENTER);
            content.add(centrePanel, BorderLayout.CENTER);
        }

        // Buttons
        JButton submitBtn = new JButton("Submit");
        submitBtn.addActionListener(e -> onSubmit());
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnPanel.add(cancelBtn);
        btnPanel.add(submitBtn);
        content.add(btnPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(submitBtn);

        setMinimumSize(new Dimension(500, 200));
        setResizable(true);
        pack();
    }

    private static JLabel boldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(Font.BOLD));
        return l;
    }

    private static void addCell(JPanel panel, JComponent comp, GridBagConstraints gbc, int col) {
        gbc.gridx = col;
        panel.add(comp, gbc);
    }

    private void onSubmit() {
        AttributeValueList values = new AttributeValueList();

        for (int i = 0; i < argumentDefs.size(); i++) {
            ArgumentDefinition arg = argumentDefs.get(i);
            String text = valueFields.get(i).getText().trim();
            String argName = (arg.getArgId() != null) ? arg.getArgId().getValue() : "arg" + i;

            int typeShortForm = arg.getRawType().getValue();
            String typeName = HelperAttributes.typeShortForm2attributeName(typeShortForm);
            Object proto = HelperAttributes.attributeName2object(typeName);
            Attribute template = (Attribute) HelperAttributes.javaType2Attribute(proto);

            try {
                Attribute value = (Attribute) HelperAttributes.string2attribute(template, text);
                values.add(value != null ? new AttributeValue(value) : null);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid value for '" + argName + "': \"" + text + "\"\nExpected type: " + typeName,
                        "Invalid input", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        result = values;
        cancelled = false;
        dispose();
    }

    /**
     * Returns the argument values entered by the user.
     *
     * @return the entered argument values
     * @throws InterruptedIOException if the user cancelled the dialog.
     */
    public AttributeValueList getArgumentValues() throws InterruptedIOException {
        if (cancelled) {
            throw new InterruptedIOException();
        }
        return result;
    }
}
