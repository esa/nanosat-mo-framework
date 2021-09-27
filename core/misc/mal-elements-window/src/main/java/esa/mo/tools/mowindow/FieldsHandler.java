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
package esa.mo.tools.mowindow;

import esa.mo.helpertools.helpers.HelperAttributes;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Enumeration;

/**
 *
 * @author Cesar Coelho
 */
public class FieldsHandler {

    public static Object filterRawObject(Object obj) {
        try {
            return HelperAttributes.javaType2Attribute(obj);
        } catch (IllegalArgumentException ex) {
        }

        return null;
    }
    
    public static Field[] getDeclaredFields(Object obj) {

        Field[] fields = obj.getClass().getDeclaredFields();

        // Does it have a super class?
        if (!obj.getClass().getSuperclass().getSimpleName().equals("Composite")) {
            Field[] superFields = obj.getClass().getSuperclass().getDeclaredFields();
            Field[] newFields = new Field[fields.length + superFields.length];

            for (int i = 0; i < newFields.length; i++) {
                if (i < fields.length) {
                    newFields[i] = fields[i];
                } else {
                    newFields[i] = superFields[i - fields.length];
                }
            }

            fields = newFields;
        }

        return fields;
    }

    public static boolean isFieldNull(Field field, Object obj) {
        Object objectWithValue;
        try {
            field.setAccessible(true);
            objectWithValue = field.get(obj);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            return true;
        }

        return (objectWithValue == null);
    }

    public static Object generateFieldObject(Field field, Object obj) {

        Object rawObj = null;
        Attribute secondObj = null;
        field.setAccessible(true);

        // First try if we can grab it immediately
        try {
            Object objectWithValue1 = field.get(obj);
            if (objectWithValue1 != null) {
                return objectWithValue1;
            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            // Ja.. just continue the rest of the tests...
        }

        // The object is not generated, let's generate it from the field...
        try {
            rawObj = field.getType().newInstance();
            secondObj = (Attribute) rawObj;
            return secondObj;
        } catch (ClassCastException | IllegalAccessException | InstantiationException ex0) {
            FieldsHandler.generateFieldObjectFromField(rawObj, field);
        }

        return null;
    }

    private static Object generateFieldObjectFromField(Object rawObj, Field field) {
        if (rawObj != null) {
            return FieldsHandler.filterRawObject(rawObj);
        } else {
            Constructor[] constructors = field.getType().getDeclaredConstructors();
            if (constructors.length == 0) {
                return null;
            }

            // Enumeration case...
            if (constructors.length == 1) {
                Constructor constructor = constructors[0];  // Use the first constructor
                constructor.setAccessible(true);
                try {
                    return (Enumeration) constructor.newInstance(0);
                } catch (InstantiationException | InvocationTargetException | IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(MOWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            // Octet case...
            if (constructors.length == 2) {
                Constructor constructor = constructors[0];  // Use the first constructor
                constructor.setAccessible(true);
                String name = constructor.getName();
                try {
                    if (name.equals("java.lang.Boolean")) {
                        return HelperAttributes.javaType2Attribute((Boolean) constructor.newInstance(true));
                    }

                    if (name.equals("java.lang.String")) {
                        return HelperAttributes.javaType2Attribute((String) constructor.newInstance(""));
                    }

                    if (name.equals("java.lang.Byte")) {
                        return HelperAttributes.javaType2Attribute(constructor.newInstance((byte) 1));
                    }

                    return HelperAttributes.javaType2Attribute(constructor.newInstance(1));
                } catch (InstantiationException | InvocationTargetException | IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(MOWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

        return null;
    }

    
}
