/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
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

import java.io.InterruptedIOException;
import org.ccsds.moims.mo.mal.structures.Element;

/**
 *
 * @author Cesar Coelho
 */
public class MOcomposite extends MOelement{
    
    private final javax.swing.JToggleButton button;

    public MOcomposite(String fieldNameIn, Element obj, boolean editable, boolean objIsNull) {
        super(fieldNameIn, obj, editable, objIsNull);
        
        // Make a button and put it in the middle Panel
        button = new javax.swing.JToggleButton();
        button.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonActionPerformed(evt);
            }
        });
        
        super.middlePanel.add(button);

        
        // Set the text
        if (editable == true) {
            this.button.setText("Edit");
        } else {
            this.button.setText("View");
        }

        if (objIsNull){
            super.makeFieldNull();
            this.button.setText("Add");
        }

        this.setVisible(true);
    }

    @Override
    public Object getObject() {
        if (nullCB.isSelected()){
            return null;
        }
        
        return object;
    }

    private void buttonActionPerformed(java.awt.event.ActionEvent evt) {                                       
        MOWindow genericObj = new MOWindow(object, this.editable);

        try {
            this.object = genericObj.getObject();
        } catch (InterruptedIOException ex) {
            return;
        }

        // Set text
        if (editable == true) {
            this.button.setText("Edit");
        }
        
    }                                      
    
}
