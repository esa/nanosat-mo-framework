/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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
package esa.mo.tools.mowindow;

import esa.mo.helpertools.helpers.HelperAttributes;
import java.io.InterruptedIOException;
import org.ccsds.moims.mo.mal.structures.ElementList;

/**
 *
 * @author Cesar Coelho
 */
public class MOelementList extends MOelement{
    
    private javax.swing.JToggleButton buttonEdit;
    private final javax.swing.JToggleButton buttonRemove;
    private javax.swing.JTextField fieldValue;
    
    public MOelementList(final MOWindow previousWindow, String fieldNameIn, Object obj, boolean editable, boolean objIsNull) {
        super(fieldNameIn, obj, true, objIsNull);
        
        this.editable = editable;

        // Make the remove button
        buttonRemove = new javax.swing.JToggleButton();
        buttonRemove.setText("Remove");
        final MOelementList temp = this;
        java.awt.event.ActionListener actionListenerRemove = new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    previousWindow.getComponentsPanel().remove(temp);
                    
                    // Fix the indexes
                    for (int i = 0; i < previousWindow.getComponentsPanel().getComponentCount()-1; i++){
                        ( (MOelement) previousWindow.getComponentsPanel().getComponent(i)).getFieldNameLabel().setText(String.valueOf(i));
                    }
                    
                    previousWindow.refreshVerticalSize();
                }
            };
        buttonRemove.addActionListener(actionListenerRemove);

        // Is it an Attribute?
        boolean isAttribute = (HelperAttributes.attributeName2typeShortForm(obj.getClass().getSimpleName()) != null);
        
        if(isAttribute && !(obj instanceof ElementList)){
            // Make a textbox and put it in the middle Panel
            fieldValue = new javax.swing.JTextField();
            super.middlePanel.add(fieldValue);

            this.fieldValue.setEditable(editable);
            this.fieldValue.setText(HelperAttributes.attribute2string(obj));
            
            // Set the text
            if (!editable) {
                this.buttonRemove.setEnabled(false);
            }

        }else{
            // Make a button and put it in the middle Panel
            buttonEdit = new javax.swing.JToggleButton();
            buttonEdit.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttonEditActionPerformed(evt);
                }
            });
        
            super.middlePanel.add(buttonEdit);

            // Set the text
            if (editable == true) {
                this.buttonEdit.setText("Edit");
            } else {
                this.buttonEdit.setText("View");
                this.buttonRemove.setEnabled(false);
                this.nullCB.setEnabled(false);
            }

        }

        super.middlePanel.add(buttonRemove);

        if (objIsNull){
            super.makeFieldNull();
        }

        this.setVisible(true);
    }

    @Override
    public Object getObject() {
        if (nullCB.isSelected()){
            return null;
        }else{
            // Is it an Attribute?
            boolean isAttribute = (HelperAttributes.attributeName2typeShortForm(this.object.getClass().getSimpleName()) != null);
            return (isAttribute) ? HelperAttributes.string2attribute(this.object, this.fieldValue.getText()) : HelperAttributes.attribute2JavaType(this.object);
        }
    }
    
    private void buttonEditActionPerformed(java.awt.event.ActionEvent evt) {                                       
        MOWindow genericObj = new MOWindow(this.object, this.editable);
        try {
            Object obj = genericObj.getObject();
            this.object = obj;
        } catch (InterruptedIOException ex) {
            return;
        }

        // Set text
        if (editable == true) {
            this.buttonEdit.setText("Edit");
        }
    }                                      
    
}
