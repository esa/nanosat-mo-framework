/* ----------------------------------------------------------------------------
 * Copyright (C) 2016      European Space Agency
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
package esa.mo.com.impl.provider;

import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.helpertools.helpers.HelperMisc;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Composite;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Enumeration;

/**
 *
 * @author Cesar Coelho
 */
    public class SortByField implements Comparator {

        private final boolean ascending;
        private final boolean timestampSorting;
        private boolean nullsLast = true;

        private final String fieldName;
        private Field field;

        /*
         *	Sort in a specified order
         */
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
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (SecurityException ex) {
                Logger.getLogger(ArchiveManager.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Treat empty strings like nulls
            if (obj1 instanceof String && ((String) obj1).length() == 0) {
                obj1 = null;
            }

            if (obj2 instanceof String && ((String) obj2).length() == 0) {
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
            Object c1;
            Object c2;

            if (this.ascending) {
                c1 = obj1;
                c2 = obj2;
            } else {
                c1 = obj2;
                c2 = obj1;
            }

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
    }
