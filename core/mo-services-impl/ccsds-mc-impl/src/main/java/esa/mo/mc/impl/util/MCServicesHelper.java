/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package esa.mo.mc.impl.util;

import java.math.BigInteger;
import org.ccsds.moims.mo.com.activitytracking.structures.ActivityAcceptance;
import org.ccsds.moims.mo.com.activitytracking.structures.ActivityExecution;
import org.ccsds.moims.mo.com.activitytracking.structures.ActivityTransfer;
import org.ccsds.moims.mo.com.activitytracking.structures.OperationActivity;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.ULong;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.action.ActionHelper;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionInstanceDetails;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetails;
import org.ccsds.moims.mo.mc.alert.AlertHelper;
import org.ccsds.moims.mo.mc.alert.structures.AlertDefinitionDetails;
import org.ccsds.moims.mo.mc.alert.structures.AlertEventDetails;
import org.ccsds.moims.mo.mc.check.structures.CheckLinkDetails;
import org.ccsds.moims.mo.mc.check.structures.CheckResult;
import org.ccsds.moims.mo.mc.check.structures.CompoundCheckDefinition;
import org.ccsds.moims.mo.mc.check.structures.ConstantCheckDefinition;
import org.ccsds.moims.mo.mc.check.structures.DeltaCheckDefinition;
import org.ccsds.moims.mo.mc.check.structures.LimitCheckDefinition;
import org.ccsds.moims.mo.mc.check.structures.ReferenceCheckDefinition;
import org.ccsds.moims.mo.mc.conversion.structures.DiscreteConversionDetails;
import org.ccsds.moims.mo.mc.conversion.structures.LineConversionDetails;
import org.ccsds.moims.mo.mc.conversion.structures.PolyConversionDetails;
import org.ccsds.moims.mo.mc.conversion.structures.RangeConversionDetails;
import org.ccsds.moims.mo.mc.group.structures.GroupDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticFunctionDetails;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticLinkDetails;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticValue;
//import org.ccsds.moims.mo.mcprototype.MCPrototypeHelper;
//import org.ccsds.moims.mo.mcprototype.alerttest.AlertTestHelper;

/**
 *
 * @author wilson_pjr
 */
public class MCServicesHelper {

    public static final long ALL_ID_NUM = 0L;
    public static final Identifier ALL_ID_STR = new Identifier("*");
    public static final Identifier EMPTY_ID_STR = new Identifier("");

    public static final int ALERT_IDENTITY_OBJECT_NUMBER = 1;
    public static final int ALERT_DEFINITION_OBJECT_NUMBER = 2;
    public static final int ALERT_EVENT_OBJECT_NUMBER = 3;

    public static final int PARAMETER_IDENTITY_OBJECT_NUMBER = 1;
    public static final int PARAMETER_DEFINITION_OBJECT_NUMBER = 2;
    public static final int PARAMETER_VALUE_INSTANCE_OBJECT_NUMBER = 3;

    public static final int ACTION_IDENTITY_OBJECT_NUMBER = 1;
    public static final int ACTION_DEFINITION_OBJECT_NUMBER = 2;
    public static final int ACTION_INSTANCE_OBJECT_NUMBER = 3;
    public static final int ACTION_FAILURE_OBJECT_NUMBER = 6;

    public static final int ACTIVITY_RELEASE_OBJECT_NUMBER = 1;
    public static final int ACTIVITY_ACCEPTANCE_OBJECT_NUMBER = 4;
    public static final int ACTIVITY_EXECUTION_OBJECT_NUMBER = 5;
    public static final int OPERATION_ACTIVITY_OBJECT_NUMBER = 6;

    public static final int STATISTIC_FUNCTION_OBJECT_NUMBER = 1;
    public static final int STATISTIC_LINK_OBJECT_NUMBER = 2;
    public static final int STATISTIC_LINK_DEFINITION_OBJECT_NUMBER = 3;
    public static final int STATISTIC_VALUE_INSTANCE_OBJECT_NUMBER = 4;

    public static final int STATISTIC_MAX_FUNCTION_INSTANCE_NUMBER = 1;
    public static final int STATISTIC_MIN_FUNCTION_INSTANCE_NUMBER = 2;
    public static final int STATISTIC_MEAN_FUNCTION_INSTANCE_NUMBER = 3;
    public static final int STATISTIC_STDDEV_FUNCTION_INSTANCE_NUMBER = 4;

    public static final int CHECK_IDENTITY_OBJECT_NUMBER = 1;
    public static final int CHECK_LINK_OBJECT_NUMBER = 2;
    public static final int CHECK_LINK_DEFINITION_OBJECT_NUMBER = 3;
    public static final int CHECK_TRANSITION_OBJECT_NUMBER = 4;
    public static final int CONSTANT_CHECK_OBJECT_NUMBER = 5;
    public static final int REFERENCE_CHECK_OBJECT_NUMBER = 6;
    public static final int DELTA_CHECK_OBJECT_NUMBER = 7;
    public static final int LIMIT_CHECK_OBJECT_NUMBER = 8;
    public static final int COMPOUND_CHECK_OBJECT_NUMBER = 9;

    public static final int AGGREGATION_IDENTITY_OBJECT_NUMBER = 1;
    public static final int AGGREGATION_DEFINITION_OBJECT_NUMBER = 2;
    public static final int AGGREGATION_VALUE_INSTANCE_OBJECT_NUMBER = 3;

    public static final int CONVERSION_IDENTITY_OBJECT_NUMBER = 1;
    public static final int DISCRETE_CONVERSION_OBJECT_NUMBER = 2;
    public static final int LINE_CONVERSION_OBJECT_NUMBER = 3;
    public static final int POLY_CONVERSION_OBJECT_NUMBER = 4;
    public static final int RANGE_CONVERSION_OBJECT_NUMBER = 5;

    public static final int GROUP_IDENTITY_OBJECT_NUMBER = 1;
    public static final int GROUP_DEFINITION_OBJECT_NUMBER = 2;

    public static final String STORE_IN_ARCHIVE_PROPERTY = "esa.nmf.parameters.storeInArchive";

    // alert objects
    private static final ObjectType alertIdentityObjType = new ObjectType(AlertDefinitionDetails.AREA_SHORT_FORM,
        AlertDefinitionDetails.SERVICE_SHORT_FORM, AlertDefinitionDetails.AREA_VERSION, new UShort(
            ALERT_IDENTITY_OBJECT_NUMBER));
    private static final ObjectType alertDefObjType = new ObjectType(AlertDefinitionDetails.AREA_SHORT_FORM,
        AlertDefinitionDetails.SERVICE_SHORT_FORM, AlertDefinitionDetails.AREA_VERSION, new UShort(
            ALERT_DEFINITION_OBJECT_NUMBER));
    private static final ObjectType alertEventObjType = new ObjectType(AlertEventDetails.AREA_SHORT_FORM,
        AlertEventDetails.SERVICE_SHORT_FORM, AlertEventDetails.AREA_VERSION, new UShort(ALERT_EVENT_OBJECT_NUMBER));

    // action objects
    private static final ObjectType actionIdentityObjType = new ObjectType(ActionDefinitionDetails.AREA_SHORT_FORM,
        ActionDefinitionDetails.SERVICE_SHORT_FORM, ActionDefinitionDetails.AREA_VERSION, new UShort(
            ACTION_IDENTITY_OBJECT_NUMBER));

    private static final ObjectType actionDefObjType = new ObjectType(ActionDefinitionDetails.AREA_SHORT_FORM,
        ActionDefinitionDetails.SERVICE_SHORT_FORM, ActionDefinitionDetails.AREA_VERSION, new UShort(
            ACTION_DEFINITION_OBJECT_NUMBER));

    private static final ObjectType actionInstanceObjType = new ObjectType(ActionInstanceDetails.AREA_SHORT_FORM,
        ActionInstanceDetails.SERVICE_SHORT_FORM, ActionInstanceDetails.AREA_VERSION, new UShort(
            ACTION_INSTANCE_OBJECT_NUMBER));

    private static final ObjectType actionFailureObjType = new ObjectType(MCHelper.MC_AREA_NUMBER,
        ActionHelper.ACTION_SERVICE_NUMBER, MCHelper.MC_AREA_VERSION, new UShort(ACTION_FAILURE_OBJECT_NUMBER));

    //group objects
    private static final ObjectType groupIdentityType = new ObjectType(GroupDetails.AREA_SHORT_FORM,
        GroupDetails.SERVICE_SHORT_FORM, GroupDetails.AREA_VERSION, new UShort(GROUP_IDENTITY_OBJECT_NUMBER));

    private static final ObjectType groupDefinitionObjType = new ObjectType(GroupDetails.AREA_SHORT_FORM,
        GroupDetails.SERVICE_SHORT_FORM, GroupDetails.AREA_VERSION, new UShort(GROUP_DEFINITION_OBJECT_NUMBER));

    //activity objects
    private static final ObjectType operationActivityObjType = new ObjectType(OperationActivity.AREA_SHORT_FORM,
        OperationActivity.SERVICE_SHORT_FORM, OperationActivity.AREA_VERSION, new UShort(
            OPERATION_ACTIVITY_OBJECT_NUMBER));

    private static final ObjectType activityReleaseObjType = new ObjectType(ActivityTransfer.AREA_SHORT_FORM,
        ActivityTransfer.SERVICE_SHORT_FORM, ActivityTransfer.AREA_VERSION, new UShort(ACTIVITY_RELEASE_OBJECT_NUMBER));

    private static final ObjectType activityAcceptanceObjType = new ObjectType(ActivityAcceptance.AREA_SHORT_FORM,
        ActivityAcceptance.SERVICE_SHORT_FORM, ActivityAcceptance.AREA_VERSION, new UShort(
            ACTIVITY_ACCEPTANCE_OBJECT_NUMBER));

    private static final ObjectType activityExecutionObjType = new ObjectType(ActivityExecution.AREA_SHORT_FORM,
        ActivityExecution.SERVICE_SHORT_FORM, ActivityExecution.AREA_VERSION, new UShort(
            ACTIVITY_EXECUTION_OBJECT_NUMBER));

    //conversion objects
    private static final ObjectType conversionIdentityObjType = new ObjectType(
        DiscreteConversionDetails.AREA_SHORT_FORM, DiscreteConversionDetails.SERVICE_SHORT_FORM,
        DiscreteConversionDetails.AREA_VERSION, new UShort(CONVERSION_IDENTITY_OBJECT_NUMBER));

    private static final ObjectType discreteConversionObjType = new ObjectType(
        DiscreteConversionDetails.AREA_SHORT_FORM, DiscreteConversionDetails.SERVICE_SHORT_FORM,
        DiscreteConversionDetails.AREA_VERSION, new UShort(DISCRETE_CONVERSION_OBJECT_NUMBER));

    private static final ObjectType lineConversionObjType = new ObjectType(LineConversionDetails.AREA_SHORT_FORM,
        LineConversionDetails.SERVICE_SHORT_FORM, LineConversionDetails.AREA_VERSION, new UShort(
            LINE_CONVERSION_OBJECT_NUMBER));

    private static final ObjectType polyConversionObjType = new ObjectType(PolyConversionDetails.AREA_SHORT_FORM,
        PolyConversionDetails.SERVICE_SHORT_FORM, PolyConversionDetails.AREA_VERSION, new UShort(
            POLY_CONVERSION_OBJECT_NUMBER));

    private static final ObjectType rangeConversionObjType = new ObjectType(RangeConversionDetails.AREA_SHORT_FORM,
        RangeConversionDetails.SERVICE_SHORT_FORM, RangeConversionDetails.AREA_VERSION, new UShort(
            RANGE_CONVERSION_OBJECT_NUMBER));

    //statistic object
    private static final ObjectType statisticFunctionObjType = new ObjectType(StatisticFunctionDetails.AREA_SHORT_FORM,
        StatisticFunctionDetails.SERVICE_SHORT_FORM, StatisticFunctionDetails.AREA_VERSION, new UShort(
            STATISTIC_FUNCTION_OBJECT_NUMBER));

    private static final ObjectType statisticLinkObjType = new ObjectType(StatisticLinkDetails.AREA_SHORT_FORM,
        StatisticLinkDetails.SERVICE_SHORT_FORM, StatisticLinkDetails.AREA_VERSION, new UShort(
            STATISTIC_LINK_OBJECT_NUMBER));

    private static final ObjectType statisticLinkDefinitionObjType = new ObjectType(
        StatisticLinkDetails.AREA_SHORT_FORM, StatisticLinkDetails.SERVICE_SHORT_FORM,
        StatisticLinkDetails.AREA_VERSION, new UShort(STATISTIC_LINK_DEFINITION_OBJECT_NUMBER));

    private static final ObjectType statisticValueInstanceObjType = new ObjectType(StatisticValue.AREA_SHORT_FORM,
        StatisticValue.SERVICE_SHORT_FORM, StatisticValue.AREA_VERSION, new UShort(
            STATISTIC_VALUE_INSTANCE_OBJECT_NUMBER));

    //statistic function instance objects

    //parameter objects
    private static final ObjectType parameterIdentityObjType = new ObjectType(
        ParameterDefinitionDetails.AREA_SHORT_FORM, ParameterDefinitionDetails.SERVICE_SHORT_FORM,
        ParameterDefinitionDetails.AREA_VERSION, new UShort(PARAMETER_IDENTITY_OBJECT_NUMBER));
    private static final ObjectType parameterDefinitionDetailsObjType = new ObjectType(
        ParameterDefinitionDetails.AREA_SHORT_FORM, ParameterDefinitionDetails.SERVICE_SHORT_FORM,
        ParameterDefinitionDetails.AREA_VERSION, new UShort(PARAMETER_DEFINITION_OBJECT_NUMBER));
    private static final ObjectType parameterValueObjType = new ObjectType(ParameterValue.AREA_SHORT_FORM,
        ParameterValue.SERVICE_SHORT_FORM, ParameterValue.AREA_VERSION, new UShort(
            PARAMETER_VALUE_INSTANCE_OBJECT_NUMBER));

    //check objects
    private static final ObjectType checkIdentityObjType = new ObjectType(CheckLinkDetails.AREA_SHORT_FORM,
        CheckLinkDetails.SERVICE_SHORT_FORM, CheckLinkDetails.AREA_VERSION, new UShort(CHECK_IDENTITY_OBJECT_NUMBER));
    private static final ObjectType checkLinkObjType = new ObjectType(CheckLinkDetails.AREA_SHORT_FORM,
        CheckLinkDetails.SERVICE_SHORT_FORM, CheckLinkDetails.AREA_VERSION, new UShort(CHECK_LINK_OBJECT_NUMBER));
    private static final ObjectType checkLinkDefinitionObjType = new ObjectType(CheckLinkDetails.AREA_SHORT_FORM,
        CheckLinkDetails.SERVICE_SHORT_FORM, CheckLinkDetails.AREA_VERSION, new UShort(
            CHECK_LINK_DEFINITION_OBJECT_NUMBER));
    private static final ObjectType constantCheckObjType = new ObjectType(ConstantCheckDefinition.AREA_SHORT_FORM,
        ConstantCheckDefinition.SERVICE_SHORT_FORM, ConstantCheckDefinition.AREA_VERSION, new UShort(
            CONSTANT_CHECK_OBJECT_NUMBER));
    private static final ObjectType referenceCheckObjType = new ObjectType(ReferenceCheckDefinition.AREA_SHORT_FORM,
        ReferenceCheckDefinition.SERVICE_SHORT_FORM, ReferenceCheckDefinition.AREA_VERSION, new UShort(
            REFERENCE_CHECK_OBJECT_NUMBER));
    private static final ObjectType deltaCheckObjType = new ObjectType(DeltaCheckDefinition.AREA_SHORT_FORM,
        DeltaCheckDefinition.SERVICE_SHORT_FORM, DeltaCheckDefinition.AREA_VERSION, new UShort(
            DELTA_CHECK_OBJECT_NUMBER));
    private static final ObjectType limitCheckObjType = new ObjectType(LimitCheckDefinition.AREA_SHORT_FORM,
        LimitCheckDefinition.SERVICE_SHORT_FORM, LimitCheckDefinition.AREA_VERSION, new UShort(
            LIMIT_CHECK_OBJECT_NUMBER));
    private static final ObjectType compoundCheckObjType = new ObjectType(CompoundCheckDefinition.AREA_SHORT_FORM,
        CompoundCheckDefinition.SERVICE_SHORT_FORM, CompoundCheckDefinition.AREA_VERSION, new UShort(
            COMPOUND_CHECK_OBJECT_NUMBER));
    private static final ObjectType checkTransitionObjType = new ObjectType(CheckResult.AREA_SHORT_FORM,
        CheckResult.SERVICE_SHORT_FORM, CheckResult.AREA_VERSION, new UShort(CHECK_TRANSITION_OBJECT_NUMBER));

    //aggregation objects
    private static final ObjectType aggregationIdentityObjType = new ObjectType(
        AggregationDefinitionDetails.AREA_SHORT_FORM, AggregationDefinitionDetails.SERVICE_SHORT_FORM,
        AggregationDefinitionDetails.AREA_VERSION, new UShort(AGGREGATION_IDENTITY_OBJECT_NUMBER));

    private static final ObjectType aggregationDefinitionObjType = new ObjectType(
        AggregationDefinitionDetails.AREA_SHORT_FORM, AggregationDefinitionDetails.SERVICE_SHORT_FORM,
        AggregationDefinitionDetails.AREA_VERSION, new UShort(AGGREGATION_DEFINITION_OBJECT_NUMBER));

    private static final ObjectType aggregationValueInstanceObjType = new ObjectType(
        AggregationDefinitionDetails.AREA_SHORT_FORM, AggregationDefinitionDetails.SERVICE_SHORT_FORM,
        AggregationDefinitionDetails.AREA_VERSION, new UShort(AGGREGATION_VALUE_INSTANCE_OBJECT_NUMBER));

    // For the tests:
    // The object number for the alert handler
    //public static final UShort ALERT_HANDLER_OBJECT_NUMBER = new UShort(4000);
    // The object number for the alert test handler
    //public static final UShort ALERT_TEST_HANDLER_OBJECT_NUMBER = new UShort(4001);
    public static long getAlertObjectTypeAsKey(int objectNumber) {
        long iKey;

        iKey = objectNumber;
        iKey = iKey | (long) MCHelper._MC_AREA_NUMBER << 48;
        iKey = iKey | (long) AlertHelper._ALERT_SERVICE_NUMBER << 32;
        iKey = iKey | (long) MCHelper._MC_AREA_VERSION << 24;
        return iKey;
    }

    public static long getAlertSourceObjectTypeAsKey(int objectNumber) {
        long iKey;

        iKey = objectNumber;
        //    iKey = iKey | (long) MCPrototypeHelper._MCPROTOTYPE_AREA_NUMBER << 48;
        //    iKey = iKey | (long) AlertTestHelper._ALERTTEST_SERVICE_NUMBER << 32;
        //    iKey = iKey | (long) MCPrototypeHelper._MCPROTOTYPE_AREA_VERSION << 24;
        return iKey;
    }

    /**
     * Generate a EntityKey sub key using fields as specified in COM STD
     * 3.2.4.2b
     *
     * @param area
     * @param service
     * @param version
     * @param objectNumber
     * @return
     */
    static public Long generateSubKey(int area, int service, int version, int objectNumber) {
        long subkey = objectNumber;
        subkey = subkey | (((long) version) << 24);
        subkey = subkey | ((long) service << 32);
        subkey = subkey | ((long) area << 48);

        return subkey;
    }

    /**
     * Generate a sub key from an ObjectType, optionally including the object
     * number.
     *
     * @param objectType
     * @param includeObjectNumber
     * @return
     */
    static public Long generateSubKey(ObjectType objectType, boolean includeObjectNumber) {
        return generateSubKey(objectType.getArea().getValue(), objectType.getService().getValue(), objectType
            .getVersion().getValue(), includeObjectNumber ? objectType.getNumber().getValue() : 0);
    }

    /**
     * Create an entity key for an event for the instanceNumber of objectType,
     * with source set ti sourceObjectType
     *
     * @param objectType
     * @param instanceNumber
     * @param sourceObjectType
     * @return
     */
    static public EntityKey generateEntityKey(ObjectType objectType, long instanceNumber, ObjectType sourceObjectType) {
        return new EntityKey(new Identifier(String.valueOf(objectType.getNumber())), MCServicesHelper.generateSubKey(
            objectType, false), instanceNumber, MCServicesHelper.generateSubKey(sourceObjectType, true));

    }

    public static class KeyParts {

        public ObjectType objectType = new ObjectType();
        public Long objectInstance = (long) -1;
        public ObjectType sourceObjectType = new ObjectType();
    }

    static public void getValuesFromEntityKey(EntityKey key, KeyParts keyParts) {
        // Get the area/service/version from the second sub key
        generateObjectTypeFromSubKey(keyParts.objectType, key.getSecondSubKey());
        // Add object number from first subkey
        keyParts.objectType.setNumber(new UShort(Integer.parseInt(key.getFirstSubKey().toString())));

        // Instance number is 3rd
        keyParts.objectInstance = key.getThirdSubKey();

        // Source object is all from the 4th
        generateObjectTypeFromSubKey(keyParts.sourceObjectType, key.getFourthSubKey());
    }

    //  static public Long generateSubKey(Element object, int objectNumber)
    //  {
    //    return generateSubKey(object.getAreaNumber().getValue(),
    //      object.getServiceNumber().getValue(),
    //      object.getAreaVersion().getValue(),
    //      objectNumber);
    //  }
    public static void generateObjectTypeFromSubKey(ObjectType objectType, Long secondSubKey) {
        long subkey = secondSubKey;

        // Get the object number from the bottom 3 bytes.
        objectType.setNumber(new UShort((int) (subkey & 0xFFFFFF)));
        // Version comes from the next byte.
        objectType.setVersion(new UOctet((short) ((subkey >> 24) & 0xFF)));
        // Service comes from the next 2 bytes.
        objectType.setService(new UShort((int) ((subkey >> 32) & 0xFFFF)));
        // Arean comes from the next 2 bytes.
        objectType.setArea(new UShort((int) ((subkey >> 48) & 0xFFFF)));
    }

    public static Attribute getAttribute(String value, int type) {
        //        LoggingBase.logMessage("Getting Attribute of type " + type + " with value " + value);

        Attribute ret = null;
        switch (type) {
            case Attribute._INTEGER_TYPE_SHORT_FORM:
                ret = new Union(Integer.parseInt(value));
                break;
            case Attribute._FLOAT_TYPE_SHORT_FORM:
                ret = new Union(Float.parseFloat(value));
                break;
            case Attribute._DOUBLE_TYPE_SHORT_FORM:
                ret = new Union(Double.parseDouble(value));
                break;
            case Attribute._LONG_TYPE_SHORT_FORM:
                ret = new Union(Long.parseLong(value));
                break;
            case Attribute._SHORT_TYPE_SHORT_FORM:
                ret = new Union(Short.parseShort(value));
                break;
            case Attribute._UINTEGER_TYPE_SHORT_FORM:
                ret = new UInteger(Long.parseLong(value));
                break;
            case Attribute._ULONG_TYPE_SHORT_FORM:
                ret = new ULong(new BigInteger(value));
                break;
            case Attribute._STRING_TYPE_SHORT_FORM:
                ret = new Union(value);
                break;
            default:
                break;
        }
        return ret;
    }

    public static double getDouble(Attribute attr) {
        int type = attr.getTypeShortForm();
        switch (type) {
            case Attribute._DOUBLE_TYPE_SHORT_FORM:
                //Already double.
                return ((Union) attr).getDoubleValue();
            case Attribute._SHORT_TYPE_SHORT_FORM:
                // Short.
                return ((Union) attr).getShortValue();
            case Attribute._USHORT_TYPE_SHORT_FORM:
                // UShort
                return ((UShort) attr).getValue();
            case Attribute._INTEGER_TYPE_SHORT_FORM:
                // Integer
                return ((Union) attr).getIntegerValue();
            case Attribute._UINTEGER_TYPE_SHORT_FORM:
                // UInteger
                return ((UInteger) attr).getValue();
            case Attribute._LONG_TYPE_SHORT_FORM:
                // Long
                return ((Union) attr).getLongValue();
        }
        return 0;
    }

}
