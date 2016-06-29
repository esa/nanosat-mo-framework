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

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.Enumeration;

/**
 *
 * @author Cesar Coelho
 */
public class MOenumeration extends MOelement{
    
    private javax.swing.JComboBox comboBox;

    public MOenumeration(String fieldNameIn, Element obj, boolean editable, boolean objIsNull) {
        super(fieldNameIn, obj, editable, objIsNull);
        
        comboBox = new javax.swing.JComboBox();
        super.middlePanel.add(comboBox);
        this.comboBox.setEnabled(editable);

        Enumeration enumeration = (Enumeration) obj;
        Field[] fields = enumeration.getClass().getDeclaredFields();

        Field field = fields[fields.length - 2]; // Get the string enumerations
        field.setAccessible(true);

        try {
            String[] enumerationStrings = (String[]) field.get(enumeration);

            for (int i = 0; i < enumerationStrings.length; i++){
                // Set the text
                this.comboBox.addItem(enumerationStrings[i]);
            }
            
            // Set the selected index;
            this.comboBox.setSelectedItem(enumeration.toString());
            
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(MOenumeration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MOenumeration.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (objIsNull){
            super.makeFieldNull();
        }

        this.setVisible(true);
    }

    @Override
    public Object getObject() {
        if (nullCB.isSelected()){
            return null;
        }
        
        Enumeration enumeration = (Enumeration) object;
        Field[] fields = enumeration.getClass().getDeclaredFields();

        int index = this.comboBox.getSelectedIndex();
        
        Field field = fields[8 + index*3];  // Calculation to get the correct enumeration
        field.setAccessible(true);
        
        try {
            return field.get(object);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(MOenumeration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MOenumeration.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
/*
    private void buttonActionPerformed(java.awt.event.ActionEvent evt) {                                       
        MOWindow genericObj = new MOWindow(object, this.editable);
        object = genericObj.getObject();

        // Set text
        if (editable == true){
//            this.button.setText("Edit");
        }
    }                                      
*/        
    
}
