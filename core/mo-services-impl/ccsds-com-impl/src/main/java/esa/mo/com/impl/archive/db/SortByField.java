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
package esa.mo.com.impl.archive.db;

import esa.mo.com.impl.provider.ArchiveManager;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.helpertools.helpers.HelperMisc;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Composite;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Enumeration;
import org.ccsds.moims.mo.mal.structures.IdentifierList;

/**
 * Sorts a set of COM Objects based on its timestamp or on a specific field of
 * the object body.
 */
public class SortByField implements Comparator {

    private final boolean ascending;
    private final boolean timestampSorting;
    private boolean nullsLast = true;

    private final String fieldName;
    private Field field;

    SortByField(Class<?> beanClass, String fieldName, boolean ascending) throws NoSuchFieldException, SecurityException {
        this.ascending = ascending;

        if (fieldName != null) {  // Is it timestamp sorting?
            this.fieldName = fieldName;
            this.timestampSorting = false;

            if (fieldName.equals("")) {
                return;
            }
        } else {  // timestamp
            this.fieldName = "timestamp";
            this.timestampSorting = true;
            this.field = beanClass.getDeclaredField(this.fieldName);
            this.field.setAccessible(true);
        }

        if (beanClass != null && fieldName != null) {

            String[] parts = fieldName.split("\\.");

            if (parts.length == 0) {  // Something went wrong...
                throw new NoSuchFieldException();
            }

            if (parts.length == 1) {  // Normal search in field
                this.field = beanClass.getDeclaredField(fieldName);
                this.field.setAccessible(true);
            } else {
                // Rooted field within composite

                Object obj;
                for (int i = 0; i < parts.length; i++) {
                    String part = parts[i];

                    if (i == 0) {  // For the first field we do it manually...
                        this.field = beanClass.getDeclaredField(part);
                        this.field.setAccessible(true);
                    } else // Then, it is done automatically...
                    if (!part.equals("")) {
                        this.field = ((Class) this.field.getGenericType()).getDeclaredField(part);
                        this.field.setAccessible(true);
                    } else {
                        // Then it is a Enumeration
//                                obj = ((Enumeration) obj).getNumericValue();
                    }
                }
            }

        }

    }

    /*
     *  Implement the Comparable interface
     */
    @SuppressWarnings("unchecked")
    @Override
    public int compare(Object in1, Object in2) {
        Object obj1 = null;
        Object obj2 = null;

        try {
            if (!timestampSorting) {
                try {
                    obj1 = HelperCOM.getNestedObject(((ArchivePersistenceObject) in1).getObject(), this.fieldName);
                } catch (NoSuchFieldException ex) {
                    obj1 = null;
                }

                try {
                    obj2 = HelperCOM.getNestedObject(((ArchivePersistenceObject) in2).getObject(), this.fieldName);
                } catch (NoSuchFieldException ex) {
                    obj2 = null;
                }

            } else {
                // It is timestamp sorting!
                obj1 = this.field.get(((ArchivePersistenceObject) in1).getArchiveDetails());
                obj2 = this.field.get(((ArchivePersistenceObject) in2).getArchiveDetails());
            }
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (SecurityException ex) {
            Logger.getLogger(ArchiveManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Treat empty strings like nulls
        if (obj1 instanceof String && ((String) obj1).isEmpty()) {
            obj1 = null;
        }

        if (obj2 instanceof String && ((String) obj2).isEmpty()) {
            obj2 = null;
        }

        // Handle sorting of null values
        if (obj1 == null && obj2 == null) {
            return 0;
        }
        if (obj1 == null) {
            return nullsLast ? 1 : -1;
        }
        if (obj2 == null) {
            return nullsLast ? -1 : 1;
        }

        //  Compare objects
        /*
        Object c1;
        Object c2;

        if (this.ascending) {
            c1 = obj1;
            c2 = obj2;
        } else {
            c1 = obj2;
            c2 = obj1;
        }
         */
        Object c1 = (this.ascending) ? obj1 : obj2;
        Object c2 = (this.ascending) ? obj2 : obj1;

        // Don't compare if it is a List
        if (c1 instanceof ElementList) {
            return 0;
        }

        // Don't compare if it is a Blob
        if (c1 instanceof Blob || c2 instanceof Blob) {
            return 0;
        }

        // Don't compare if it is a Composite
        if (c1 instanceof Composite) {
            return 0;
        }

        if (c1 instanceof java.lang.Integer) {
//                return ((int) c1 - (int) c2);
//                return (((Integer) c1).intValue() - ((Integer) c2).intValue());
            return (((Integer) c1) - ((Integer) c2));
        }

        if (c1 instanceof Enumeration) {
            return (int) (((Enumeration) c1).getNumericValue().getValue() - ((Enumeration) c2).getNumericValue().getValue());
        }

        if (c1 instanceof Attribute) {
            if (HelperMisc.isStringAttribute((Attribute) c1)) {
                c1 = HelperAttributes.attribute2string(c1);
                c2 = HelperAttributes.attribute2string(c2);
                return ((String) c1).compareToIgnoreCase((String) c2);
            } else {
                c1 = HelperAttributes.attribute2double((Attribute) c1);
                c2 = HelperAttributes.attribute2double((Attribute) c2);
                double diff = (((Double) c1) - ((Double) c2));

                if (diff == 0) {
                    return 0;
                }

                if (((int) diff) != 0) {
                    return (int) diff;
                } else {
                    // Remove the exponencial part of the number
                    return (int) (diff / (Math.pow(10, Math.ceil(Math.log10(diff)))));
                }
            }
        }

        return ((String) c1).compareToIgnoreCase((String) c2);
    }

    public static ArrayList<ArchivePersistenceObject> sortPersistenceObjects(
            final ArrayList<ArchivePersistenceObject> perObjs, final String fieldString,
            final Boolean ascending) throws NoSuchFieldException {

        IdentifierList tmpDomain;
        ObjectType tmpObjType;
        ArrayList<ArchivePersistenceObject> stackB;
        ArrayList<ArchivePersistenceObject> stackOut = new ArrayList<>();

        // Requirement 3.4.4.2.27: 
        // "Each domain/object type pair shall be sorted separately from other domain/object type 
        //  pairs; there is no requirement for sorting to be applied across domain/object type pairs"
        while (!perObjs.isEmpty()) { // We will sweep stackA
            // What is the current zeroth pair?
            tmpDomain = perObjs.get(0).getDomain();
            tmpObjType = perObjs.get(0).getObjectType();
            stackB = new ArrayList<ArchivePersistenceObject>();

            // Make a stack B with all the equal pairs domain+objType
            for (int index = 0; index < perObjs.size(); index++) { // Let's cycle the complete stack A
                if (perObjs.get(index).getDomain().equals(tmpDomain)
                        && perObjs.get(index).getObjectType().equals(tmpObjType)) { // if the pair is the same...
                    stackB.add(perObjs.get(index));
                    perObjs.remove(index);
                    index--; // index has to be the same on next iteration; counter the index++
                }
            }

            stackB = SortByField.sortStack(stackB, fieldString, ascending); // sort stack B
            stackOut.addAll(stackB); // and add all the stack to the output
        }

        return stackOut;
    }

    private static ArrayList<ArchivePersistenceObject> sortStack(ArrayList<ArchivePersistenceObject> stack,
            final String fieldString, final Boolean ascending) throws NoSuchFieldException {

        if (stack == null) {
            return null;
        }

        if (stack.isEmpty()) {
            return stack;
        }

        Class aClass;

        // Requirement 3.4.4.2.26: 
        // "The returned lists shall be sorted based on the sorting options specified in ArchiveQuery"
        // Is it a timestamp sorting?
        if (fieldString == null) {
            aClass = stack.get(0).getArchiveDetails().getClass();
        } else if (stack.get(0).getObject() != null) {
            aClass = stack.get(0).getObject().getClass();
        } else {
            return stack;
        }

        SortByField comparator = new SortByField(aClass, fieldString, ascending);
        // stack.sort(comparator);
        // Changed to be compatible with java 6:
        java.util.Collections.sort(stack, comparator);

        return stack;
    }

}
