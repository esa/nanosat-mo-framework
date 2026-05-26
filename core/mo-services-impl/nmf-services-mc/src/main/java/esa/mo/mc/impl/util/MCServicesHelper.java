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
