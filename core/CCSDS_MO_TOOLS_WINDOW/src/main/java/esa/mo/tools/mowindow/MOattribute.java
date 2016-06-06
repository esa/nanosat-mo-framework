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
import javax.swing.JOptionPane;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Element;

/**
 *
 * @author Cesar Coelho
 */
public class MOattribute extends MOelement {

    private javax.swing.JTextField fieldValue;

    public MOattribute(String fieldName, Object obj, boolean editable, boolean objIsNull) {
        super(fieldName, obj, editable, objIsNull);
        
        fieldValue = new javax.swing.JTextField();
        super.middlePanel.add(fieldValue);

        if (objIsNull){
            super.makeFieldNull();
        }else{
            fieldValue.setText(HelperAttributes.attribute2string(obj));
        }

        if (!editable){
            this.fieldValue.setEnabled(false);
        }
    }
    
    @Override
    public Object getObject(){

        if (super.object == null && super.fieldSelectableAttribute.isEnabled()){  // Unknown attribute
            long index = (long) super.fieldSelectableAttribute.getSelectedIndex();
            if (index == 0){
                index = Attribute.STRING_TYPE_SHORT_FORM;  // If nothing was selected, then just consider it as a String
            }
            
            Long shortForm = Attribute.ABSOLUTE_AREA_SERVICE_NUMBER + index;
            super.object = (Element) MALContextFactory.getElementFactoryRegistry().lookupElementFactory(shortForm).createElement();
        }
        
        try {
            Object out = HelperAttributes.string2attribute(super.object, fieldValue.getText());
            
            if (nullCB.isSelected()){
                out = null;
            }
            
            return out;

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null,"The field value: '" + fieldValue.getText() + "' could not be converted into " + super.object.getClass().getSimpleName() , "NumberFormatException",JOptionPane.ERROR_MESSAGE);
        }
        
        return null;
    }
    
}
