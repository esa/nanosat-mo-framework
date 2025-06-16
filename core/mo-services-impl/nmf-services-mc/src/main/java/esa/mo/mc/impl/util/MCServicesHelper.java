/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package esa.mo.mc.impl.util;

import java.math.BigInteger;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.ULong;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.alert.AlertServiceInfo;

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

    // For the tests:
    // The object number for the alert handler
    //public static final UShort ALERT_HANDLER_OBJECT_NUMBER = new UShort(4000);
    // The object number for the alert test handler
    //public static final UShort ALERT_TEST_HANDLER_OBJECT_NUMBER = new UShort(4001);
    public static long getAlertObjectTypeAsKey(int objectNumber) {
        long iKey;

        iKey = objectNumber;
        iKey = iKey | (long) MCHelper._MC_AREA_NUMBER << 48;
        iKey = iKey | (long) AlertServiceInfo._ALERT_SERVICE_NUMBER << 32;
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

    public static class KeyParts {

        public ObjectType objectType = new ObjectType();
        public Long objectInstance = (long) -1;
        public ObjectType sourceObjectType = new ObjectType();
    }

    @Deprecated
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
        int type = attr.getTypeId().getSFP();
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
