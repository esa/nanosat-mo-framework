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
package esa.mo.com.impl.util;

import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.helpertools.helpers.HelperMisc;
import java.lang.reflect.Field;
//import java.util.Objects;
import org.ccsds.moims.mo.com.COMObject;
import org.ccsds.moims.mo.com.COMService;
import org.ccsds.moims.mo.com.archive.structures.ExpressionOperator;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.Enumeration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;

/**
 *
 * @author Cesar Coelho
 */
public class HelperCOM {

    /**
     * Evaluates an expression from 2 attribute values.
     * 
     * @param leftHandSide The left hand side value of the expression
     * @param operator The operator of the expression
     * @param rightHandSide The right hand side value of the expression
     * @return The boolean value of the evaluation. Null if not evaluated.
     */
    public static Boolean evaluateExpression(Element leftHandSide, ExpressionOperator operator, Attribute rightHandSide) {

        if (operator == null) {
            return null; // Operator cannot be null
        }
        
        if (leftHandSide == null || rightHandSide == null) {  // One of the sides is null?
            if (operator.equals(ExpressionOperator.EQUAL)) {
                return (leftHandSide == rightHandSide);
            }

            if (operator.equals(ExpressionOperator.DIFFER)) {
                return (leftHandSide != rightHandSide);
            }

            return null;
        }

        if ((leftHandSide instanceof Blob) && (rightHandSide instanceof Blob)){  // Are we comparing Blobs?
            if (operator.equals(ExpressionOperator.EQUAL)) {
                return leftHandSide.equals(rightHandSide);
            }

            if (operator.equals(ExpressionOperator.DIFFER)) {
                return !leftHandSide.equals(rightHandSide);
            }
        }
        
        // Is it an Enumeration?
        if (leftHandSide instanceof Enumeration){
            leftHandSide = new UInteger(((Enumeration) leftHandSide).getOrdinal());
        }

        if (rightHandSide instanceof Enumeration){
            rightHandSide = new UInteger(((Enumeration) rightHandSide).getOrdinal());
        }
        
        // if one of the sides is string, then we shall do a comparison between strings:
        boolean stringComparison = HelperMisc.isStringAttribute(rightHandSide) ||
                HelperMisc.isStringAttribute(rightHandSide);

        String rightHandSideString = null;
        String leftHandSideString = null;
        Double rightHandSideDouble = null;
        Double  leftHandSideDouble = null;
        
        if (stringComparison){
            rightHandSideString = HelperAttributes.attribute2string(rightHandSide);
            leftHandSideString = HelperAttributes.attribute2string(leftHandSide);
        }else{
            rightHandSideDouble = HelperAttributes.attribute2double(rightHandSide);
            leftHandSideDouble = HelperAttributes.attribute2double((Attribute) leftHandSide);
        }

        // Verify all the possible validity expression operators
        if (operator.equals(ExpressionOperator.EQUAL)) {
            if (stringComparison){
                return rightHandSideString.equals(leftHandSideString);
            }else{
//                return (Objects.equals(rightHandSideDouble, leftHandSideDouble));
                return (objectEquals(rightHandSideDouble, leftHandSideDouble));
            }
        }

        if (operator.equals(ExpressionOperator.DIFFER)) {
            if (stringComparison){
                return !(rightHandSideString.equals(leftHandSideString));
            }else{
//              return !(Objects.equals(rightHandSideDouble, leftHandSideDouble));
                return !(objectEquals(rightHandSideDouble, leftHandSideDouble));
            }
        }

        if (operator.equals(ExpressionOperator.GREATER)) {
            if (stringComparison){
                return (Double.parseDouble(leftHandSideString) > Double.parseDouble(rightHandSideString));
            }else{
                return (leftHandSideDouble > rightHandSideDouble);
            }
        }

        if (operator.equals(ExpressionOperator.GREATER_OR_EQUAL)) {
            if (stringComparison){
                return (Double.parseDouble(leftHandSideString) >= Double.parseDouble(rightHandSideString));
            }else{
                return (leftHandSideDouble >= rightHandSideDouble);
            }
        }

        if (operator.equals(ExpressionOperator.LESS)) {
            if (stringComparison){
                return (Double.parseDouble(leftHandSideString) < Double.parseDouble(rightHandSideString));
            }else{
                return (leftHandSideDouble < rightHandSideDouble);
            }
        }

        if (operator.equals(ExpressionOperator.LESS_OR_EQUAL)) {
            if (stringComparison){
                return (Double.parseDouble(leftHandSideString) <= Double.parseDouble(rightHandSideString));
            }else{
                return (leftHandSideDouble <= rightHandSideDouble);
            }
        }

        if (operator.equals(ExpressionOperator.CONTAINS)) {
            if (stringComparison){
                return leftHandSideString.contains(rightHandSideString);
            }
        }

        if (operator.equals(ExpressionOperator.ICONTAINS)) {
            if (stringComparison){
                return leftHandSideString.toLowerCase().contains(rightHandSideString.toLowerCase());
            }
        }

        return null; // The expression was not evaluated
    }
    
    /**
     * Finds and generates the COMObject from the ObjectType
     * 
     * @param objType COM Object Type
     * @return COMObject object
     */
    public static COMObject objType2COMObject(ObjectType objType){
        
        if (objType == null){
            return null;
        }
        
        if(objType.getService() == null){
            return null;
        }
        
        COMService service = (COMService) MALContextFactory.lookupArea(
                objType.getArea(), 
                objType.getAreaVersion()
        ).getServiceByNumber(objType.getService());        
        
        if(service == null || objType.getNumber().getValue() == 0){  // Special case for the event service...
            return null;
        }else{
            return service.getObjectByNumber(objType.getNumber());
        }
    }

    /**
     * Finds a COMService from the fields area, areaVersion and serviceNumber
     * 
     * @param area Area of the service
     * @param areaVersion Area version
     * @param serviceNumber Service number
     * @return COMService
     */
    public static COMService findCOMService(UShort area, UOctet areaVersion, UShort serviceNumber){
        
        return (COMService) MALContextFactory.lookupArea(
                area, 
                areaVersion
        ).getServiceByNumber(serviceNumber);        
        
    }
    
    /**
     * Returns the Name of the COM object from the objectType
     * 
     * @param objType Object Type
     * @return Name of the COM object
     */
    public static String objType2string(ObjectType objType){
/*
        if (objType == null){
            return "null";
        }
*/        
        COMObject comObject = HelperCOM.objType2COMObject(objType);

        String string = MALContextFactory.lookupArea(objType.getArea(), objType.getAreaVersion()).getName().toString();
        string += " - " + MALContextFactory.lookupArea(objType.getArea(), objType.getAreaVersion()).getServiceByNumber(objType.getService()).getName().toString();

        if (comObject != null){
            string += ": " + comObject.getObjectName().getValue();
        }
        
        return string;
    }
    
    /**
     * Checks if the domain contains a wildcard in any of the parts
     * 
     * @param receivedDomain Domain
     * @return Name of the COM object
     */
    public static Boolean domainContainsWildcard(final IdentifierList receivedDomain) {
        // Do we have the '*' on any part of the domain?
        if (receivedDomain == null) {
            return true;
        }

        for (Identifier part : receivedDomain) {
            if (part.getValue().equals("*")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the provided domain matches the supplied domain with a 
     * wildcard
     * 
     * @param domain Domain
     * @param wilcardDomain Domain with wildcard
     * @return True if the domain matches the domain with the wildcard
     */
    public static boolean domainMatchesWildcardDomain(IdentifierList domain, IdentifierList wilcardDomain) {

        if (wilcardDomain.size() > domain.size() + 1) {  // The domain of the wildcard can never be greater than the real domain
            return false;
        }

        for (int i = 0; i < wilcardDomain.size(); i++) {  // cycle through the parts of the domains
            Identifier domainPart1 = wilcardDomain.get(i);

            if (domainPart1.toString().equals("*")) {
                return true;  // Wildcard found!
            }

            Identifier domainPart2 = domain.get(i);

            if (!domainPart1.toString().equals(domainPart2.toString())) {  // The parts are different, return false
                return false;
            }
        }

        return false;
    }

    /**
     * Generates a COM ObjectType object from the MALService and Object number
     * 
     * @param service MAL service
     * @param objNumber Object number
     * @return The ObjectType object
     */
    public static ObjectType generateCOMObjectType(MALService service, UShort objNumber) {

        if (service == null || objNumber == null) {
            return null;
        }

        return new ObjectType(service.getArea().getNumber(),
                service.getNumber(),
                service.getArea().getVersion(),
                objNumber);
    }

    /**
     * Generates a COM ObjectType object from the area, service, version and
     * object number. 
     * Deprecated because this used to be needed when the services
     * didn't provide the static COM Objects ObjectType. This can now be found 
     * in the service Helper. For example, for the Parameter service, the parameter
     * definition can be found in: ParameterHelper.PARAMETERDEFINITION_OBJECT_TYPE
     * 
     * @param area Area number
     * @param service Service number
     * @param version Version number
     * @param number Object number
     * @return The ObjectType object
     */
    @Deprecated
    public static ObjectType generateCOMObjectType(int area, int service, int version, int number) {
        return new ObjectType(
                new UShort(area), 
                new UShort(service),
                new UOctet((short) version),
                new UShort(number)
        );
    }

    /**
     * Generates a Long subkey from the ObjectType object object number
     * 
     * @param objectType Object type object
     * @return Subkey
     */
    public static Long generateSubKey(ObjectType objectType) {
        if (objectType == null){
            return null;
        }
        
        long areaVal = (long) objectType.getArea().getValue();
        long serviceVal = (long) objectType.getService().getValue();
        long versionVal = (long) objectType.getVersion().getValue();
        long numberVal = (long) objectType.getNumber().getValue();

        return (numberVal
                | (versionVal << 24)
                | (serviceVal << 32)
                | (areaVal << 48));

    }

    /**
     * Generates an ObjectType from the objectTypeId (aka subkey)
     * 
     * @param subkey Subkey of the COM object
     * @return ObjectType object
     */
    public static ObjectType objectTypeId2objectType(Long subkey) {
        final long unwrap = (long) subkey;

        return new ObjectType(new UShort((short) (unwrap >> 48)),
                new UShort((short) (unwrap >> 32)),
                new UOctet((byte) (unwrap >> 24)),
                new UShort((short) (unwrap)));
    }
    
    /*
     * Returns true if the arguments are equal to each other and false otherwise.
     * This method was created to allow compilation of the code for Java 1.6
     * <p>
     * If both arguments are null, true is returned;
     * if exactly one argument is null, false is returned.
     * Otherwise, equality is determined by using the equals method of the first argument.
     */
    private static boolean objectEquals(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }    
    
    public static Object getNestedObject(Object in, String fieldName) throws NoSuchFieldException {

        Object obj = in;
        String[] parts = fieldName.split("\\.");

        try {
            for (String part : parts) {
                if (!part.equals("")) {
                    obj = HelperCOM.getObjectInsideObject(part, obj);
                } else {
                    // Then it is a Enumeration
//                            obj = ((Enumeration) obj).getNumericValue();
                }
            }
        } catch (IllegalArgumentException ex) {
            throw new NoSuchFieldException();
        } catch (IllegalAccessException ex) {
            throw new NoSuchFieldException();
        }

        return obj;
    }

    private static Object getObjectInsideObject(String fieldName, Object obj) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        if (obj == null) {
            throw new NoSuchFieldException();
        }

        if ("".equals(fieldName)  && !(obj instanceof Enumeration)) {
            throw new NoSuchFieldException();
        }

        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

}
