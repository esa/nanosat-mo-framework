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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Enumeration;

/**
 * The FieldsHandler class includes a set of static method to handle fields.
 *
 * @author Cesar Coelho
 */
public class FieldsHandler {

    private FieldsHandler() {
    }

    /**
     * Converts a raw Java value into the matching MAL attribute, or returns {@code null} if it
     * cannot be converted.
     *
     * @param obj the raw value
     * @return the MAL attribute, or {@code null} if not convertible
     */
    public static Object filterRawObject(Object obj) {
        try {
            return Attribute.javaType2Attribute(obj);
        } catch (IllegalArgumentException ex) {
        }

        return null;
    }

    /**
     * Returns the declared fields of the object's class, including those of its immediate
     * super class unless that super class is {@code Composite}.
     *
     * @param obj the object whose fields to return
     * @return the declared fields
     */
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

    /**
     * Returns whether the given field of the object holds a null value.
     *
     * @param field the field to inspect
     * @param obj the object holding the field
     * @return {@code true} if the field value is null or cannot be read
     */
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

    /**
     * Reads the value of the given field from the object, defaulting to a freshly created
     * instance of the field type when the value cannot be read directly.
     *
     * @param field the field to read
     * @param obj the object holding the field
     * @return the field value, or a new instance of the field type
     */
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
        }

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
            } catch (InstantiationException
                    | InvocationTargetException
                    | IllegalArgumentException
                    | IllegalAccessException ex) {
                Logger.getLogger(MOWindow.class.getName()).log(Level.SEVERE,
                        "Could not instantiate a new Object for class: " + constructor.getName(), ex);
            }
        }

        // Boxed Java type case (Integer, Long, Short, Byte, Boolean, Double):
        // these declare two constructors, one taking the primitive and one
        // taking a String. getDeclaredConstructors() order is unspecified, so
        // pick the primitive (non-String) one explicitly rather than assuming
        // constructors[0] — otherwise Integer(String) gets selected and
        // newInstance(1) fails with an argument type mismatch.
        if (constructors.length == 2) {
            Constructor constructor = constructors[0];
            for (Constructor candidate : constructors) {
                Class<?>[] params = candidate.getParameterTypes();
                if (params.length == 1 && params[0] != String.class) {
                    constructor = candidate;
                    break;
                }
            }
            constructor.setAccessible(true);
            String name = constructor.getName();
            try {
                if (name.equals("java.lang.Boolean")) {
                    return Attribute.javaType2Attribute(constructor.newInstance(true));
                }

                if (name.equals("java.lang.String")) {
                    return Attribute.javaType2Attribute(constructor.newInstance(""));
                }

                if (name.equals("java.lang.Byte")) {
                    return Attribute.javaType2Attribute(constructor.newInstance((byte) 1));
                }

                if (name.equals("java.lang.Long")) {
                    return Attribute.javaType2Attribute(0L);
                }

                Object newObj = constructor.newInstance(1);
                return Attribute.javaType2Attribute(newObj);
            } catch (InstantiationException
                    | InvocationTargetException
                    | IllegalArgumentException
                    | IllegalAccessException ex) {
                Logger.getLogger(MOWindow.class.getName()).log(Level.SEVERE,
                        "Could not instantiate a new Object for class: " + name, ex);
            }
        }

        return null;
    }

}
