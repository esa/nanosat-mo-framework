/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
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
package esa.mo.nmf.ctt.windows.element;

import java.io.InterruptedIOException;
import javax.swing.JPanel;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperAttributes;
import org.ccsds.moims.mo.mal.structures.ElementList;

/**
 * The ListEntry class holds an entry for a list of objects.
 *
 * @author Cesar Coelho
 */
public class ListEntry extends Entry {

    private javax.swing.JToggleButton buttonEdit;
    private final javax.swing.JToggleButton buttonRemove;
    private javax.swing.JTextField fieldValue;

    public ListEntry(final MOWindow previousWindow, String fieldNameIn,
            Object obj, boolean editable, boolean objIsNull) {
        super(fieldNameIn, obj, true, objIsNull);

        this.editable = editable;

        // Make the remove button
        buttonRemove = new javax.swing.JToggleButton();
        buttonRemove.setText("Remove");
        final ListEntry temp = this;

        java.awt.event.ActionListener actionListenerRemove = evt -> {
            JPanel panel = previousWindow.getComponentsPanel();
            panel.remove(temp);

            // Fix the indexes
            for (int i = 0; i < panel.getComponentCount() - 1; i++) {
                Entry component = (Entry) panel.getComponent(i);
                component.getFieldName().setText(String.valueOf(i));
            }

            previousWindow.refreshVerticalSize();
        };
        buttonRemove.addActionListener(actionListenerRemove);

        // Is it an Attribute?
        boolean isAttribute = (HelperAttributes.attributeName2typeShortForm(obj.getClass().getSimpleName()) != null);

        if (isAttribute && !(obj instanceof ElementList)) {
            // Make a textbox and put it in the middle Panel
            fieldValue = new javax.swing.JTextField();
            super.middlePanel.add(fieldValue);

            this.fieldValue.setEditable(editable);
            this.fieldValue.setText(HelperAttributes.attribute2string(obj));

            // Set the text
            if (!editable) {
                this.buttonRemove.setEnabled(false);
            }
        } else {
            // Make a button and put it in the middle Panel
            buttonEdit = new javax.swing.JToggleButton();
            buttonEdit.addActionListener(this::buttonEditActionPerformed);
            super.middlePanel.add(buttonEdit);

            // Set the text
            if (editable) {
                this.buttonEdit.setText("Edit");
            } else {
                this.buttonEdit.setText("View");
                this.buttonRemove.setEnabled(false);
                this.nullCB.setEnabled(false);
            }
        }
        if (editable) {
            super.middlePanel.add(buttonRemove);
        }

        if (objIsNull) {
            super.makeFieldNull();
        }

        this.setVisible(true);
    }

    @Override
    public Object getObject() {
        if (nullCB.isSelected()) {
            return null;
        }

        // Is it an Attribute?
        String name = this.object.getClass().getSimpleName();
        boolean isAttribute = (HelperAttributes.attributeName2typeShortForm(name) != null);
        return (isAttribute) ? HelperAttributes.string2attribute(this.object, this.fieldValue.getText())
                : HelperAttributes.attribute2JavaType(this.object);
    }

    private void buttonEditActionPerformed(java.awt.event.ActionEvent evt) {
        MOWindow genericObj = new MOWindow(this.object, this.editable);
        try {
            this.object = genericObj.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }

        // Set text
        if (editable) {
            this.buttonEdit.setText("Edit");
        }
    }

}
