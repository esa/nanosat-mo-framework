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
package esa.mo.helpertools.helpers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.DoubleList;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.FloatList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IntegerList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.OctetList;
import org.ccsds.moims.mo.mal.structures.ShortList;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.ULong;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.Union;

/**
 * A Helper class to simplify and solve many problems related with MAL
 * Attributes.
 */
public class HelperAttributes {

  public static Byte SERIAL_OBJECT_RAW_TYPE = (byte) 127; // Selected value to represent a
                                                          // Serialized object
  public static String SERIAL_OBJECT_STRING = "SerializedObject"; // Selected the String to
                                                                  // represent a Serialized object

  /**
   * Converts any MAL Attribute data type to a Double java type
   *
   * @param in The MAL Attribute data type
   * @return The convert Double value or null if in == null
   */
  public static Double attribute2double(Attribute in) {

    if (in instanceof Union) {
      if (((Union) in).getTypeShortForm().equals(Union.BOOLEAN_TYPE_SHORT_FORM)) { // 2
        return (double) (((Union) in).getBooleanValue() ? 1 : 0);
      }

      if (((Union) in).getTypeShortForm().equals(Union.FLOAT_TYPE_SHORT_FORM)) { // 4
        return new Double(((Union) in).getFloatValue());
      }

      if (((Union) in).getTypeShortForm().equals(Union.DOUBLE_TYPE_SHORT_FORM)) { // 5
        return ((Union) in).getDoubleValue();
      }

      if (((Union) in).getTypeShortForm().equals(Union.OCTET_TYPE_SHORT_FORM)) { // 7
        return (double) (short) ((Union) in).getOctetValue();
      }

      if (((Union) in).getTypeShortForm().equals(Union.SHORT_TYPE_SHORT_FORM)) { // 9
        return new Double(((Union) in).getShortValue());
      }

      if (((Union) in).getTypeShortForm().equals(Union.INTEGER_TYPE_SHORT_FORM)) { // 11
        return new Double(((Union) in).getIntegerValue());
      }

      if (((Union) in).getTypeShortForm().equals(Union.LONG_TYPE_SHORT_FORM)) { // 13
        return new Double(((Union) in).getLongValue());
      }

      if (((Union) in).getTypeShortForm().equals(Union.STRING_TYPE_SHORT_FORM)) { // 15
        double dou;
        try {
          dou = Double.parseDouble(((Union) in).getStringValue());
        } catch (NumberFormatException ex) {
          return null; // Return a null
        }

        return dou;
      }

    }

    if (in instanceof Duration) { // 3
      return ((Duration) in).getValue();
    }

    if (in instanceof Identifier) { // 6
      try {
        return Double.parseDouble(((Identifier) in).getValue());
      } catch (NumberFormatException ex) {
        return null;
      }
    }

    if (in instanceof UOctet) { // 8
      return (double) ((UOctet) in).getValue();
    }

    if (in instanceof UShort) { // 10
      return (double) ((UShort) in).getValue();
    }

    if (in instanceof UInteger) { // 12
      return (double) ((UInteger) in).getValue();
    }

    if (in instanceof ULong) { // 14
      return ((ULong) in).getValue().doubleValue();
    }

    if (in instanceof Time) { // 16
      return (double) ((Time) in).getValue();
    }

    if (in instanceof FineTime) { // 17
      return (double) ((FineTime) in).getValue();
    }

    if (in instanceof URI) { // 18
      try {
        return Double.parseDouble(((URI) in).getValue());
      } catch (NumberFormatException ex) {
        return null;
      }
    }

    return null;
  }

  /**
   * Converts any MAL Attribute data type to a String java type
   *
   * @param in The MAL Attribute data type
   * @return The convert String value
   */
  public static String attribute2string(Object in) {

    if (in == null) {
      return "null";
    }

    if (in instanceof Union) {

      if (((Union) in).getTypeShortForm().equals(Union.DOUBLE_TYPE_SHORT_FORM)) {
        if (((Union) in).getDoubleValue() == null) {
          return "";
        }
        return ((Union) in).getDoubleValue().toString();
      }

      if (((Union) in).getTypeShortForm().equals(Union.BOOLEAN_TYPE_SHORT_FORM)) {
        if (((Union) in).getBooleanValue() == null) {
          return "";
        }
        return ((Union) in).getBooleanValue() ? "true" : "false";
      }

      if (((Union) in).getTypeShortForm().equals(Union.FLOAT_TYPE_SHORT_FORM)) {
        if (((Union) in).getFloatValue() == null) {
          return "";
        }
        return (((Union) in).getFloatValue()).toString();
      }

      if (((Union) in).getTypeShortForm().equals(Union.INTEGER_TYPE_SHORT_FORM)) {
        if (((Union) in).getIntegerValue() == null) {
          return "";
        }
        return (((Union) in).getIntegerValue()).toString();
      }

      if (((Union) in).getTypeShortForm().equals(Union.LONG_TYPE_SHORT_FORM)) {
        if (((Union) in).getLongValue() == null) {
          return "";
        }
        return (((Union) in).getLongValue()).toString();
      }

      if (((Union) in).getTypeShortForm().equals(Union.OCTET_TYPE_SHORT_FORM)) {
        if (((Union) in).getOctetValue() == null) {
          return "";
        }
        return (((Union) in).getOctetValue()).toString();
      }

      if (((Union) in).getTypeShortForm().equals(Union.SHORT_TYPE_SHORT_FORM)) {
        if (((Union) in).getShortValue() == null) {
          return "";
        }
        return (((Union) in).getShortValue()).toString();
      }

      if (((Union) in).getTypeShortForm().equals(Union.STRING_TYPE_SHORT_FORM)) {
        if (((Union) in).getStringValue() == null) {
          return "";
        }
        return ((Union) in).getStringValue();
      }

    }

    if (in instanceof Duration) {
      return String.valueOf(((Duration) in).toString());
    }

    if (in instanceof UOctet) {
      return String.valueOf(((UOctet) in).getValue());
    }

    if (in instanceof UShort) {
      return String.valueOf(((UShort) in).getValue());
    }

    if (in instanceof UInteger) {
      return String.valueOf(((UInteger) in).getValue());
    }

    if (in instanceof Blob) {
      try {
        return Arrays.toString(((Blob) in).getValue());
      } catch (MALException ex) {
      }
    }

    if (in instanceof ULong) {
      return String.valueOf(((ULong) in).getValue());
    }

    if (in instanceof Time) {
      return String.valueOf(((Time) in).getValue());
    }

    if (in instanceof Identifier) {
      return ((Identifier) in).getValue();
    }

    if (in instanceof FineTime) {
      return String.valueOf(((FineTime) in).getValue());
    }

    if (in instanceof URI) {
      return ((URI) in).toString();
    }

    if (in instanceof Long) {
      return ((Long) in).toString();
    }

    return "";
  }

  /**
   * Creates an object instance from attribute name
   *
   * @param attributeName The Attribute name
   * @return The object
   * @throws java.lang.IllegalArgumentException If attributeName == null
   */
  public static Object attributeName2object(String attributeName) throws IllegalArgumentException {

    if (attributeName == null) {
      throw new IllegalArgumentException("AttributeName must not be null.");
    }
    if (attributeName.equals("Blob")) {
      return new Blob();
    }
    if (attributeName.equals("Boolean")) {
      return Boolean.FALSE;
    }
    if (attributeName.equals("Duration")) {
      return new Duration();
    }
    if (attributeName.equals("Float")) {
      return (float) 0;
    }
    if (attributeName.equals("Double")) {
      return (double) 0;
    }
    if (attributeName.equals("Identifier")) {
      return new Identifier();
    }
    if (attributeName.equals("Octet")) {
      return (byte) 0;
    }
    if (attributeName.equals("UOctet")) {
      return new UOctet();
    }
    if (attributeName.equals("Short")) {
      return (short) 0;
    }
    if (attributeName.equals("UShort")) {
      return new UShort();
    }
    if (attributeName.equals("Integer")) {
      return (int) 0;
    }
    if (attributeName.equals("UInteger")) {
      return new UInteger();
    }
    if (attributeName.equals("Long")) {
      return 0L;
    }
    if (attributeName.equals("ULong")) {
      return new ULong();
    }
    if (attributeName.equals("String")) {
      return new String();
    }
    if (attributeName.equals("Time")) {
      return new Time();
    }
    if (attributeName.equals("FineTime")) {
      return new FineTime();
    }
    if (attributeName.equals("URI")) {
      return new URI();
    }
    if (attributeName.equals(SERIAL_OBJECT_STRING)) {
      return new Blob();
    }

    return null;
  }

  /**
   * Creates an instance of a MAL attribute from attribute name
   *
   * @param attributeName The Attribute name
   * @return The Attribute instance
   * @throws java.lang.IllegalArgumentException If attributeName == null
   */
  public static Attribute attributeName2Attribute(String attributeName) throws IllegalArgumentException {
    Object obj = attributeName2object(attributeName);

    // We got a Attribute already
    if(obj instanceof Attribute) {
      return (Attribute) obj;
    }
    // We got a Java object
    else if(obj != null){
      // Make it an attribute
      obj = javaType2Attribute(obj);

      if(obj instanceof Attribute) {
        return (Attribute) obj;
      }
    }

    return null;
  }

  /**
   * Sets a value into any MAL Attribute object. The value provided must be a
   * string. The method takes care of doing the appropriate conversion to fit the
   * correct set type.
   *
   * @param in    The object to be set
   * @param value The string value to be used for the set
   * @return The final object with the selected value or null if in == null
   * @throws java.lang.IllegalArgumentException If value == null
   */
  public static Object string2attribute(Object in, String value)
      throws IllegalArgumentException {
    if (value == null) {
      throw new IllegalArgumentException("The value must not be null.");
    }
    if (value.isEmpty()) {
      return null;
    }
    if (in instanceof Union) {
      if (((Union) in).getTypeShortForm().equals(Union.DOUBLE_TYPE_SHORT_FORM)) {
        return new Union(Double.parseDouble(value));
      }

      if (((Union) in).getTypeShortForm().equals(Union.BOOLEAN_TYPE_SHORT_FORM)) {
        return new Union(Boolean.parseBoolean(value));
      }

      if (((Union) in).getTypeShortForm().equals(Union.FLOAT_TYPE_SHORT_FORM)) {
        return new Union(Float.parseFloat(value));
      }

      if (((Union) in).getTypeShortForm().equals(Union.INTEGER_TYPE_SHORT_FORM)) {
        return new Union(Integer.parseInt(value));
      }

      if (((Union) in).getTypeShortForm().equals(Union.LONG_TYPE_SHORT_FORM)) {
        return new Union(Long.parseLong(value));
      }

      if (((Union) in).getTypeShortForm().equals(Union.OCTET_TYPE_SHORT_FORM)) {
        return new Union(Byte.parseByte(value));
      }

      if (((Union) in).getTypeShortForm().equals(Union.SHORT_TYPE_SHORT_FORM)) {
        return new Union(Short.parseShort(value));
      }

      if (((Union) in).getTypeShortForm().equals(Union.STRING_TYPE_SHORT_FORM)) {
        return new Union(value);
      }

    }

    if (in instanceof Duration) {
      return new Duration(Double.parseDouble(value));
    }

    if (in instanceof UOctet) {
      return new UOctet(Short.parseShort(value));
    }

    if (in instanceof UShort) {
      return new UShort(Integer.parseInt(value));
    }

    if (in instanceof UInteger) {
      return new UInteger(Long.parseLong(value));
    }

    if (in instanceof ULong) {
      return new ULong(new BigInteger(value));
    }

    if (in instanceof Time) {
      return new Time(Long.parseLong(value));
    }

    if (in instanceof FineTime) {
      return new FineTime(Long.parseLong(value));
    }

    if (in instanceof Identifier) {
      return new Identifier(value);
    }

    if (in instanceof URI) {
      return new URI(value);
    }

    if (in instanceof Long) {
      return Long.parseLong(value);
    }

    if (in instanceof Boolean) {
      return Boolean.valueOf(value);
    }

    return null;
  }

  /**
   * Converts a Java data type into a MAL data type if possible
   *
   * @param obj The object in the Java data type
   * @return The object in the MAL data type or the original object
   */
  public static Object javaType2Attribute(Object obj) {

    if (obj instanceof java.lang.Boolean) {
      return new Union((Boolean) obj);
    }

    if (obj instanceof java.lang.Integer) {
      return new Union((Integer) obj);
    }

    if (obj instanceof java.lang.Long) {
      return new Union((Long) obj);
    }

    if (obj instanceof java.lang.String) {
      return new Union((String) obj);
    }

    if (obj instanceof java.lang.Double) {
      return new Union((Double) obj);
    }

    if (obj instanceof java.lang.Float) {
      return new Union((Float) obj);
    }

    if (obj instanceof java.lang.Byte) {
      return new Union((Byte) obj);
    }

    if (obj instanceof java.lang.Short) {
      return new Union((Short) obj);
    }

    return obj;
  }

  /**
   * Converts a MAL data type into a Java data type
   *
   * @param obj The object in the MAL data type
   * @return The object in the Java data type
   */
  public static Object attribute2JavaType(Object obj) {

    if (obj instanceof Union) {
      Integer typeShortForm = ((Union) obj).getTypeShortForm();

      if (typeShortForm.intValue() == Attribute.BOOLEAN_TYPE_SHORT_FORM.intValue()) {
        return (boolean) ((Union) obj).getBooleanValue();
      }

      if (typeShortForm.intValue() == Attribute.INTEGER_TYPE_SHORT_FORM.intValue()) {
        return (int) ((Union) obj).getIntegerValue();
      }

      if (typeShortForm.intValue() == Attribute.LONG_TYPE_SHORT_FORM.intValue()) {
        return (long) ((Union) obj).getLongValue();
      }

      if (typeShortForm.intValue() == Attribute.STRING_TYPE_SHORT_FORM.intValue()) {
        return ((Union) obj).getStringValue();
      }

      if (typeShortForm.intValue() == Attribute.DOUBLE_TYPE_SHORT_FORM.intValue()) {
        return (double) ((Union) obj).getDoubleValue();
      }

      if (typeShortForm.intValue() == Attribute.FLOAT_TYPE_SHORT_FORM.intValue()) {
        return (float) ((Union) obj).getFloatValue();
      }

      if (typeShortForm.intValue() == Attribute.OCTET_TYPE_SHORT_FORM.intValue()) {
        return (byte) ((Union) obj).getOctetValue();
      }

      if (typeShortForm.intValue() == Attribute.SHORT_TYPE_SHORT_FORM.intValue()) {
        return (short) ((Union) obj).getShortValue();
      }
    }

    return obj;
  }

  /**
   * Generates the correct Element List based on the Java type
   *
   * @param obj The object in the Java data type
   * @return A MAL data type Elements List
   */
  public static ElementList generateElementListFromJavaType(Object obj) {

    if (obj instanceof java.lang.Boolean) {
      return new BooleanList();
    }

    if (obj instanceof java.lang.Integer) {
      return new IntegerList();
    }

    if (obj instanceof java.lang.Long) {
      return new LongList();
    }

    if (obj instanceof java.lang.String) {
      return new StringList();
    }

    if (obj instanceof java.lang.Double) {
      return new DoubleList();
    }

    if (obj instanceof java.lang.Float) {
      return new FloatList();
    }

    if (obj instanceof java.lang.Byte) {
      return new OctetList();
    }

    if (obj instanceof java.lang.Short) {
      return new ShortList();
    }

    return null;
  }

  /**
   * Translates the type short form number into the name of the element
   *
   * @param typeShortForm The type short form number
   * @return The name of the MAL Attribute
   */
  public static String typeShortForm2attributeName(Integer typeShortForm) {

    if (typeShortForm == Attribute.BLOB_TYPE_SHORT_FORM) {
      return "Blob";
    }
    if (typeShortForm == Attribute.BOOLEAN_TYPE_SHORT_FORM) {
      return "Boolean";
    }
    if (typeShortForm == Attribute.DURATION_TYPE_SHORT_FORM) {
      return "Duration";
    }
    if (typeShortForm == Attribute.FLOAT_TYPE_SHORT_FORM) {
      return "Float";
    }
    if (typeShortForm == Attribute.DOUBLE_TYPE_SHORT_FORM) {
      return "Double";
    }
    if (typeShortForm == Attribute.IDENTIFIER_TYPE_SHORT_FORM) {
      return "Identifier";
    }
    if (typeShortForm == Attribute.OCTET_TYPE_SHORT_FORM) {
      return "Octet";
    }
    if (typeShortForm == Attribute.UOCTET_TYPE_SHORT_FORM) {
      return "UOctet";
    }
    if (typeShortForm == Attribute.SHORT_TYPE_SHORT_FORM) {
      return "Short";
    }
    if (typeShortForm == Attribute.USHORT_TYPE_SHORT_FORM) {
      return "UShort";
    }
    if (typeShortForm == Attribute.INTEGER_TYPE_SHORT_FORM) {
      return "Integer";
    }
    if (typeShortForm == Attribute.UINTEGER_TYPE_SHORT_FORM) {
      return "UInteger";
    }
    if (typeShortForm == Attribute.LONG_TYPE_SHORT_FORM) {
      return "Long";
    }
    if (typeShortForm == Attribute.ULONG_TYPE_SHORT_FORM) {
      return "ULong";
    }
    if (typeShortForm == Attribute.STRING_TYPE_SHORT_FORM) {
      return "String";
    }
    if (typeShortForm == Attribute.TIME_TYPE_SHORT_FORM) {
      return "Time";
    }
    if (typeShortForm == Attribute.FINETIME_TYPE_SHORT_FORM) {
      return "FineTime";
    }
    if (typeShortForm == Attribute.URI_TYPE_SHORT_FORM) {
      return "URI";
    }
    if (typeShortForm == SERIAL_OBJECT_RAW_TYPE.intValue()) {
      return SERIAL_OBJECT_STRING;
    }

    return "";
  }

  /**
   * Translates the name of the element into the type short form number
   *
   * @param attributeName The name of the MAL Attribute
   * @return The type short form number
   * @throws java.lang.IllegalArgumentException If attributeName == null
   */
  public static Integer attributeName2typeShortForm(String attributeName)
      throws IllegalArgumentException {
    if (attributeName == null) {
      throw new IllegalArgumentException("ArgumentName must not be null.");
    }
    if (attributeName.equals("Blob")) {
      return Attribute.BLOB_TYPE_SHORT_FORM;
    }
    if (attributeName.equals("Boolean")) {
      return Attribute.BOOLEAN_TYPE_SHORT_FORM;
    }
    if (attributeName.equals("Duration")) {
      return Attribute.DURATION_TYPE_SHORT_FORM;
    }
    if (attributeName.equals("Float")) {
      return Attribute.FLOAT_TYPE_SHORT_FORM;
    }
    if (attributeName.equals("Double")) {
      return Attribute.DOUBLE_TYPE_SHORT_FORM;
    }
    if (attributeName.equals("Identifier")) {
      return Attribute.IDENTIFIER_TYPE_SHORT_FORM;
    }
    if (attributeName.equals("Octet")) {
      return Attribute.OCTET_TYPE_SHORT_FORM;
    }
    if (attributeName.equals("UOctet")) {
      return Attribute.UOCTET_TYPE_SHORT_FORM;
    }
    if (attributeName.equals("Short")) {
      return Attribute.SHORT_TYPE_SHORT_FORM;
    }
    if (attributeName.equals("UShort")) {
      return Attribute.USHORT_TYPE_SHORT_FORM;
    }
    if (attributeName.equals("Integer")) {
      return Attribute.INTEGER_TYPE_SHORT_FORM;
    }
    if (attributeName.equals("UInteger")) {
      return Attribute.UINTEGER_TYPE_SHORT_FORM;
    }
    if (attributeName.equals("Long")) {
      return Attribute.LONG_TYPE_SHORT_FORM;
    }
    if (attributeName.equals("ULong")) {
      return Attribute.ULONG_TYPE_SHORT_FORM;
    }
    if (attributeName.equals("String")) {
      return Attribute.STRING_TYPE_SHORT_FORM;
    }
    if (attributeName.equals("Time")) {
      return Attribute.TIME_TYPE_SHORT_FORM;
    }
    if (attributeName.equals("FineTime")) {
      return Attribute.FINETIME_TYPE_SHORT_FORM;
    }
    if (attributeName.equals("URI")) {
      return Attribute.URI_TYPE_SHORT_FORM;
    }
    if (attributeName.equals(SERIAL_OBJECT_STRING)) {
      return SERIAL_OBJECT_RAW_TYPE.intValue();
    }

    return null;
  }

  /**
   * Serializes an object and fits it into a Blob attribute
   *
   * @param obj The object to be serialized
   * @return The Blob with the serialized object inside
   * @throws java.io.IOException When the serialization of the object fails
   */
  public static Blob serialObject2blobAttribute(Serializable obj) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutput out = null;
    byte[] serialBytesOut = null;

    try {
      out = new ObjectOutputStream(baos);
      out.writeObject(obj);
      serialBytesOut = baos.toByteArray();
    } finally {
      try {
        if (out != null) {
          out.close();
        }
      } catch (IOException ex) {
        // ignore close exception
      }
      try {
        baos.close();
      } catch (IOException ex) {
        // ignore close exception
      }
    }

    return new Blob(serialBytesOut);
  }

  /**
   * Tries to deserialize an object inside a Blob
   *
   * @param obj The object to be serialized
   * @return The deserialized object
   * @throws java.io.IOException                When the deserialization of the
   *                                            object fails
   * @throws java.lang.IllegalArgumentException If obj == null
   */
  public static Serializable blobAttribute2serialObject(Blob obj)
      throws IOException, IllegalArgumentException {

    if (obj == null) {
      throw new IllegalArgumentException("The Blob must not be null.");
    }
    ByteArrayInputStream bis = null;
    Object o = null;

    try {
      bis = new ByteArrayInputStream(obj.getValue());
      ObjectInput in = null;
      try {
        in = new ObjectInputStream(bis);
        o = in.readObject();
      } catch (ClassNotFoundException ex) {
        Logger.getLogger(HelperAttributes.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
        try {
          bis.close();
        } catch (IOException ex) {
          // ignore close exception
        }
        try {
          if (in != null) {
            in.close();
          }
        } catch (IOException ex) {
          // ignore close exception
        }
      }
    } catch (MALException ex) {
      Logger.getLogger(HelperAttributes.class.getName()).log(Level.SEVERE, null, ex);
      return obj; // the object could not be Deserialized, so, just deliver the Blob itself
    } finally {
      try {
        bis.close();
      } catch (IOException ex) {
        Logger.getLogger(HelperAttributes.class.getName()).log(Level.SEVERE, null, ex);
        // ignore close exception
      }
    }

    return (Serializable) o;
  }

}
