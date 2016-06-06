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

/**
 *
 * @author Cesar Coelho
 */
public class MOelementListBlank extends MOelement{
    
    private javax.swing.JToggleButton buttonAdd;

    public MOelementListBlank(java.awt.event.ActionListener actionListener, boolean editable) {
        super("", null, editable, false);
        
        this.editable = editable;
        super.fieldName.setText("");
        super.fieldType.setText("");
        super.nullCB.setVisible(false);
        super.fieldSelectableAttribute.setVisible(false);

        // Make a button and put it in the middle Panel
        buttonAdd = new javax.swing.JToggleButton();
        buttonAdd.setText("Add");
        buttonAdd.addActionListener(actionListener);
        super.mainPanel.add(buttonAdd);

        if (!editable){
            this.buttonAdd.setEnabled(false);
        }

        // Set the text
        this.setVisible(true);
    }

    @Override
    public Object getObject() {
        // Not used
        return null;
    }

}
