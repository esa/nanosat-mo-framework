/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
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
package esa.mo.nmf.apps;

import at.tugraz.ihf.opssat.iadcs.*;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFInterface;
import esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValueList;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ConditionalConversionList;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeMode;

/**
 *
 * @author Cesar Coelho
 */
public class MCAdapter extends MonitorAndControlNMFAdapter {

    private static final Logger LOGGER = Logger.getLogger(MCAdapter.class.getName());
    private final NMFInterface connector;
    private static final int NUMBER_OF_OBJS = 10000;
    private static final String PARAMETER_PERIODIC = "Periodic_Parameter";
    private static final String PARAMETER_ARCHIVE_SIZE = "Set_Epoch_Time";

    private static final float ANGLE_TOL_RAD = 0.0872665f;
    private static final float ANGLE_TOL_PERCENT = 20.0f;
    private static final float ANGLE_VEL_TOL_RADPS = 0.00872665f;
    private static final float TARGET_THRESHOLD_RAD = 0.261799f;

    private AttitudeMode activeAttitudeMode;
    private SEPP_IADCS_API adcsApi;
    private boolean initialized = false;

    // Additional parameters which need to be used for attitude mode changes.
    private SEPP_IADCS_API_VECTOR3_XYZ_FLOAT losVector;
    private SEPP_IADCS_API_VECTOR3_XYZ_FLOAT flightVector;
    private SEPP_IADCS_API_VECTOR3_XYZ_FLOAT targetVector; // For sun pointing
    private SEPP_IADCS_API_TARGET_POINTING_OPERATION_PARAMETERS_TELEMETRY tolerance;

    MCAdapter(NanoSatMOConnectorImpl connector) {
        this.connector = connector;
    }

    public void startADCS() {
        LOGGER.log(Level.INFO, "Initialisation");
        try {
            //System.setProperty("java.library.path", "lib");
            //System.loadLibrary("iadcs_api_jni");
            String filepath = System.getProperty("user.dir");
            filepath += File.separator + "lib" + File.separator + "iadcs_api.jar";

            // Check if the file exists:
            File file = new File(filepath);
            String fileExists = (file.exists()) ? "The file exists! :)" : "The file does not exist!!  :(";
            LOGGER.log(Level.INFO, "Loading Library from: {0}\n{1}", new Object[]{filepath, fileExists});

            // Load the file:
            System.load(filepath);
            // System.loadLibrary(filepath);
            System.out.println("Library Loaded.");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "iADCS library could not be loaded!", ex);
            initialized = false;
            return;
        }
        adcsApi = new SEPP_IADCS_API();
        activeAttitudeMode = null;
        try {
            // Try running a short command as a ping
            adcsApi.Get_Epoch_Time();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize iADCS", e);
            initialized = false;
            return;
        }

        try {
            HKTelemetry.dumpHKTelemetry(adcsApi);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to dump iADCS TM", e);
        }
        initialized = true;

        /*  OLD:
        tolerance = new SEPP_IADCS_API_TARGET_POINTING_TOLERANCE_PARAMETERS();
        tolerance.setPREALIGNMENT_ANGLE_TOLERANCE_RAD(ANGLE_TOL_RAD);
        tolerance.setPREALIGNMENT_ANGLE_TOLERANCE_PERCENT(ANGLE_TOL_PERCENT);
        tolerance.setPREALIGNMENT_ANGULAR_VELOCITY_TOLERANCE_RADPS(ANGLE_VEL_TOL_RADPS);
        tolerance.setPREALIGNMENT_TARGET_THRESHOLD_RAD(TARGET_THRESHOLD_RAD); // See section 6.2.2.4 in ICD        
         */
        tolerance = new SEPP_IADCS_API_TARGET_POINTING_OPERATION_PARAMETERS_TELEMETRY();
        float[] angles = {ANGLE_TOL_RAD, ANGLE_TOL_PERCENT, ANGLE_VEL_TOL_RADPS, TARGET_THRESHOLD_RAD};
        tolerance.setPREALIGN_ANGLES(angles);
    }

    @Override
    public void initialRegistrations(MCRegistration registration) {
        registration.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

        // Used MAL Types
        Byte rawTypeString = Attribute._STRING_TYPE_SHORT_FORM;
        Byte rawTypeTime = Attribute._TIME_TYPE_SHORT_FORM;
        Byte rawTypeFloat = Attribute._FLOAT_TYPE_SHORT_FORM;
        Byte rawTypeLong = Attribute._LONG_TYPE_SHORT_FORM;

        // ------------------ Parameters ------------------
        ParameterDefinitionDetailsList parDef = new ParameterDefinitionDetailsList();
        IdentifierList paramNames = new IdentifierList();

        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_1, rawTypeString, "unit", false,
            new Duration(0), null, null));
        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_2, rawTypeTime, "time", false,
            new Duration(0), null, null));
        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_3, rawTypeFloat, "", false,
            new Duration(0), null, null));
        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_4, rawTypeFloat, "", false,
            new Duration(0), null, null));
        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_5, rawTypeFloat, "", false,
            new Duration(0), null, null));
        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_6, rawTypeFloat, "", false,
            new Duration(0), null, null));
        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_7, rawTypeFloat, "rad/sec",
            false, new Duration(0), null, null));
        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_8, rawTypeFloat, "rad/sec",
            false, new Duration(0), null, null));
        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_9, rawTypeFloat, "rad/sec",
            false, new Duration(0), null, null));
        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_10, rawTypeFloat, "", false,
            new Duration(0), null, null));
        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_11, rawTypeFloat, "", false,
            new Duration(0), null, null));
        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_12, rawTypeFloat, "", false,
            new Duration(0), null, null));
        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_13, rawTypeFloat, "T", false,
            new Duration(0), null, null));
        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_14, rawTypeFloat, "T", false,
            new Duration(0), null, null));
        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_15, rawTypeFloat, "T", false,
            new Duration(0), null, null));
        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_16, rawTypeFloat, "rad/sec",
            false, new Duration(0), null, null));
        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_17, rawTypeFloat, "rad/sec",
            false, new Duration(0), null, null));
        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_18, rawTypeFloat, "rad/sec",
            false, new Duration(0), null, null));
        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_19, rawTypeFloat, "rad/sec",
            false, new Duration(0), null, null));
        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_20, rawTypeFloat, "rad/sec",
            false, new Duration(0), null, null));
        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_21, rawTypeFloat, "rad/sec",
            false, new Duration(0), null, null));
        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_22, rawTypeFloat, "A*m^2",
            false, new Duration(0), null, null));
        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_23, rawTypeFloat, "A*m^2",
            false, new Duration(0), null, null));
        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_24, rawTypeFloat, "A*m^2",
            false, new Duration(0), null, null));
        parDef.add(new ParameterDefinitionDetails(DefinitionNames.PARAMETER_DESCRIPTION_25, rawTypeString, "", false,
            new Duration(0), null, null));

        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_1));
        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_2));
        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_3));
        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_4));
        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_5));
        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_6));
        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_7));
        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_8));
        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_9));
        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_10));
        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_11));
        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_12));
        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_13));
        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_14));
        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_15));
        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_16));
        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_17));
        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_18));
        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_19));
        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_20));
        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_21));
        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_22));
        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_23));
        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_24));
        paramNames.add(new Identifier(DefinitionNames.PARAMETER_NAME_25));

        registration.registerParameters(paramNames, parDef);

        // ------------------ Actions ------------------
        ActionDefinitionDetailsList actionDefs = new ActionDefinitionDetailsList();
        IdentifierList actionNames = new IdentifierList();

        ConditionalConversionList cc = null; // conditionalConversions
        Byte ct = null; // convertedType
        String cu = null; // convertedUnit

        ArgumentDefinitionDetailsList arguments1 = new ArgumentDefinitionDetailsList();
        arguments1.add(new ArgumentDefinitionDetails(new Identifier("iadcs"), "", rawTypeLong, "msec", cc, ct, cu));

        ArgumentDefinitionDetailsList arguments4 = new ArgumentDefinitionDetailsList();
        arguments4.add(new ArgumentDefinitionDetails(new Identifier("start_epoch_time"), "", rawTypeLong, "msec", cc,
            ct, cu));
        arguments4.add(new ArgumentDefinitionDetails(new Identifier("stop_epoch_time"), "", rawTypeLong, "msec", cc, ct,
            cu));
        arguments4.add(new ArgumentDefinitionDetails(new Identifier("start_latitude"), "", rawTypeFloat, "rad", cc, ct,
            cu));
        arguments4.add(new ArgumentDefinitionDetails(new Identifier("start_longitude"), "", rawTypeFloat, "rad", cc, ct,
            cu));
        arguments4.add(new ArgumentDefinitionDetails(new Identifier("stop_latitude"), "", rawTypeFloat, "rad", cc, ct,
            cu));
        arguments4.add(new ArgumentDefinitionDetails(new Identifier("stop_longitude"), "", rawTypeFloat, "rad", cc, ct,
            cu));

        ArgumentDefinitionDetailsList arguments5 = new ArgumentDefinitionDetailsList();
        arguments5.add(new ArgumentDefinitionDetails(new Identifier("latitude"), "", rawTypeFloat, "rad", cc, ct, cu));
        arguments5.add(new ArgumentDefinitionDetails(new Identifier("longitude"), "", rawTypeFloat, "rad", cc, ct, cu));

        ArgumentDefinitionDetailsList arguments7 = new ArgumentDefinitionDetailsList();
        arguments7.add(new ArgumentDefinitionDetails(new Identifier("body_axis"), "Options: x, y, z", rawTypeString,
            "axis", cc, ct, cu));
        arguments7.add(new ArgumentDefinitionDetails(new Identifier("angular_velocity"), "", rawTypeFloat, "rad/s", cc,
            ct, cu));

        UOctet cat = new UOctet((short) 0); // Category
        UShort steps = new UShort(0); // The total number of steps

        actionDefs.add(new ActionDefinitionDetails(DefinitionNames.ACTION_DESCRIPTION_1, cat, steps, arguments1));
        actionDefs.add(new ActionDefinitionDetails(DefinitionNames.ACTION_DESCRIPTION_2, cat, steps, null));
        actionDefs.add(new ActionDefinitionDetails(DefinitionNames.ACTION_DESCRIPTION_3, cat, steps, null));
        actionDefs.add(new ActionDefinitionDetails(DefinitionNames.ACTION_DESCRIPTION_4, cat, steps, arguments4));
        actionDefs.add(new ActionDefinitionDetails(DefinitionNames.ACTION_DESCRIPTION_5, cat, steps, arguments5));
        actionDefs.add(new ActionDefinitionDetails(DefinitionNames.ACTION_DESCRIPTION_6, cat, steps, null));
        actionDefs.add(new ActionDefinitionDetails(DefinitionNames.ACTION_DESCRIPTION_7, cat, steps, arguments7));
        actionDefs.add(new ActionDefinitionDetails(DefinitionNames.ACTION_DESCRIPTION_8, cat, steps, null));
        actionDefs.add(new ActionDefinitionDetails(DefinitionNames.ACTION_DESCRIPTION_9, cat, steps, null));
        actionDefs.add(new ActionDefinitionDetails(DefinitionNames.ACTION_DESCRIPTION_10, cat, steps, null));
        actionDefs.add(new ActionDefinitionDetails(DefinitionNames.ACTION_DESCRIPTION_11, cat, steps, null));
        actionDefs.add(new ActionDefinitionDetails(DefinitionNames.ACTION_DESCRIPTION_12, cat, steps, null));
        actionDefs.add(new ActionDefinitionDetails(DefinitionNames.ACTION_DESCRIPTION_13, cat, steps, null));
        actionDefs.add(new ActionDefinitionDetails(DefinitionNames.ACTION_DESCRIPTION_14, cat, steps, null));

        actionNames.add(new Identifier(DefinitionNames.ACTION_NAME_1));
        actionNames.add(new Identifier(DefinitionNames.ACTION_NAME_2));
        actionNames.add(new Identifier(DefinitionNames.ACTION_NAME_3));
        actionNames.add(new Identifier(DefinitionNames.ACTION_NAME_4));
        actionNames.add(new Identifier(DefinitionNames.ACTION_NAME_5));
        actionNames.add(new Identifier(DefinitionNames.ACTION_NAME_6));
        actionNames.add(new Identifier(DefinitionNames.ACTION_NAME_7));
        actionNames.add(new Identifier(DefinitionNames.ACTION_NAME_8));
        actionNames.add(new Identifier(DefinitionNames.ACTION_NAME_9));
        actionNames.add(new Identifier(DefinitionNames.ACTION_NAME_10));
        actionNames.add(new Identifier(DefinitionNames.ACTION_NAME_11));
        actionNames.add(new Identifier(DefinitionNames.ACTION_NAME_12));
        actionNames.add(new Identifier(DefinitionNames.ACTION_NAME_13));
        actionNames.add(new Identifier(DefinitionNames.ACTION_NAME_14));

        LongList actionObjIds = registration.registerActions(actionNames, actionDefs);
    }

    @Override
    public Attribute onGetValue(Identifier identifier, Byte rawType) throws IOException {
        if (DefinitionNames.PARAMETER_NAME_1.equals(identifier.getValue())) {
            return (Attribute) HelperAttributes.javaType2Attribute("Hello World!");
        }
        if (DefinitionNames.PARAMETER_NAME_2.equals(identifier.getValue())) {
            long epoch = adcsApi.Get_Epoch_Time();
            return (Attribute) HelperAttributes.javaType2Attribute(epoch);
            //            SEPP_IADCS_API_ATTITUDE_TELEMETRY tm = adcsApi.Get_Attitude_Telemetry();
            //            return (Attribute) HelperAttributes.javaType2Attribute(tm.getEPOCH_TIME_MSEC());
        }
        if (DefinitionNames.PARAMETER_NAME_3.equals(identifier.getValue())) {
            SEPP_IADCS_API_ATTITUDE_TELEMETRY tm = adcsApi.Get_Attitude_Telemetry();
            IADCS_100_Quaternion q = tm.getATTITUDE_QUATERNION_BF();
            return (Attribute) HelperAttributes.javaType2Attribute(q.getQ());
        }
        if (DefinitionNames.PARAMETER_NAME_4.equals(identifier.getValue())) {
            SEPP_IADCS_API_ATTITUDE_TELEMETRY tm = adcsApi.Get_Attitude_Telemetry();
            IADCS_100_Quaternion q = tm.getATTITUDE_QUATERNION_BF();
            return (Attribute) HelperAttributes.javaType2Attribute(q.getQI());
        }
        if (DefinitionNames.PARAMETER_NAME_5.equals(identifier.getValue())) {
            SEPP_IADCS_API_ATTITUDE_TELEMETRY tm = adcsApi.Get_Attitude_Telemetry();
            IADCS_100_Quaternion q = tm.getATTITUDE_QUATERNION_BF();
            return (Attribute) HelperAttributes.javaType2Attribute(q.getQJ());
        }
        if (DefinitionNames.PARAMETER_NAME_6.equals(identifier.getValue())) {
            SEPP_IADCS_API_ATTITUDE_TELEMETRY tm = adcsApi.Get_Attitude_Telemetry();
            IADCS_100_Quaternion q = tm.getATTITUDE_QUATERNION_BF();
            return (Attribute) HelperAttributes.javaType2Attribute(q.getQK());
        }
        if (DefinitionNames.PARAMETER_NAME_7.equals(identifier.getValue())) {
            SEPP_IADCS_API_ATTITUDE_TELEMETRY tm = adcsApi.Get_Attitude_Telemetry();
            SEPP_IADCS_API_VECTOR3_XYZ_FLOAT av = tm.getANGULAR_VELOCITY_VECTOR_RADPS();
            return (Attribute) HelperAttributes.javaType2Attribute(av.getX());
        }
        if (DefinitionNames.PARAMETER_NAME_8.equals(identifier.getValue())) {
            SEPP_IADCS_API_ATTITUDE_TELEMETRY tm = adcsApi.Get_Attitude_Telemetry();
            SEPP_IADCS_API_VECTOR3_XYZ_FLOAT av = tm.getANGULAR_VELOCITY_VECTOR_RADPS();
            return (Attribute) HelperAttributes.javaType2Attribute(av.getY());
        }
        if (DefinitionNames.PARAMETER_NAME_9.equals(identifier.getValue())) {
            SEPP_IADCS_API_ATTITUDE_TELEMETRY tm = adcsApi.Get_Attitude_Telemetry();
            SEPP_IADCS_API_VECTOR3_XYZ_FLOAT av = tm.getANGULAR_VELOCITY_VECTOR_RADPS();
            return (Attribute) HelperAttributes.javaType2Attribute(av.getZ());
        }
        if (DefinitionNames.PARAMETER_NAME_10.equals(identifier.getValue())) {
            SEPP_IADCS_API_ATTITUDE_TELEMETRY tm = adcsApi.Get_Attitude_Telemetry();
            SEPP_IADCS_API_VECTOR3_XYZ_FLOAT sv = tm.getMEASURED_SUN_VECTOR_BF();
            return (Attribute) HelperAttributes.javaType2Attribute(sv.getX());
        }
        if (DefinitionNames.PARAMETER_NAME_11.equals(identifier.getValue())) {
            SEPP_IADCS_API_ATTITUDE_TELEMETRY tm = adcsApi.Get_Attitude_Telemetry();
            SEPP_IADCS_API_VECTOR3_XYZ_FLOAT sv = tm.getMEASURED_SUN_VECTOR_BF();
            return (Attribute) HelperAttributes.javaType2Attribute(sv.getY());
        }
        if (DefinitionNames.PARAMETER_NAME_12.equals(identifier.getValue())) {
            SEPP_IADCS_API_ATTITUDE_TELEMETRY tm = adcsApi.Get_Attitude_Telemetry();
            SEPP_IADCS_API_VECTOR3_XYZ_FLOAT sv = tm.getMEASURED_SUN_VECTOR_BF();
            return (Attribute) HelperAttributes.javaType2Attribute(sv.getZ());
        }
        if (DefinitionNames.PARAMETER_NAME_13.equals(identifier.getValue())) {
            SEPP_IADCS_API_ATTITUDE_TELEMETRY tm = adcsApi.Get_Attitude_Telemetry();
            SEPP_IADCS_API_VECTOR3_XYZ_FLOAT mf = tm.getMEASURED_MAGNETIC_FIELD_VECTOR_BF_T();
            return (Attribute) HelperAttributes.javaType2Attribute(mf.getX());
        }
        if (DefinitionNames.PARAMETER_NAME_14.equals(identifier.getValue())) {
            SEPP_IADCS_API_ATTITUDE_TELEMETRY tm = adcsApi.Get_Attitude_Telemetry();
            SEPP_IADCS_API_VECTOR3_XYZ_FLOAT mf = tm.getMEASURED_MAGNETIC_FIELD_VECTOR_BF_T();
            return (Attribute) HelperAttributes.javaType2Attribute(mf.getY());
        }
        if (DefinitionNames.PARAMETER_NAME_15.equals(identifier.getValue())) {
            SEPP_IADCS_API_ATTITUDE_TELEMETRY tm = adcsApi.Get_Attitude_Telemetry();
            SEPP_IADCS_API_VECTOR3_XYZ_FLOAT mf = tm.getMEASURED_MAGNETIC_FIELD_VECTOR_BF_T();
            return (Attribute) HelperAttributes.javaType2Attribute(mf.getZ());
        }
        if (DefinitionNames.PARAMETER_NAME_16.equals(identifier.getValue())) {
            SEPP_IADCS_API_ACTUATOR_TELEMETRY tm = adcsApi.Get_Actuator_Telemetry();
            SEPP_IADCS_API_VECTOR3_XYZ_FLOAT ts = tm.getREACTIONWHEEL_TARGET_SPEED_VECTOR_XYZ_RADPS();
            return (Attribute) HelperAttributes.javaType2Attribute(ts.getX());
        }
        if (DefinitionNames.PARAMETER_NAME_17.equals(identifier.getValue())) {
            SEPP_IADCS_API_ACTUATOR_TELEMETRY tm = adcsApi.Get_Actuator_Telemetry();
            SEPP_IADCS_API_VECTOR3_XYZ_FLOAT ts = tm.getREACTIONWHEEL_TARGET_SPEED_VECTOR_XYZ_RADPS();
            return (Attribute) HelperAttributes.javaType2Attribute(ts.getY());
        }
        if (DefinitionNames.PARAMETER_NAME_18.equals(identifier.getValue())) {
            SEPP_IADCS_API_ACTUATOR_TELEMETRY tm = adcsApi.Get_Actuator_Telemetry();
            SEPP_IADCS_API_VECTOR3_XYZ_FLOAT ts = tm.getREACTIONWHEEL_TARGET_SPEED_VECTOR_XYZ_RADPS();
            return (Attribute) HelperAttributes.javaType2Attribute(ts.getZ());
        }
        if (DefinitionNames.PARAMETER_NAME_19.equals(identifier.getValue())) {
            SEPP_IADCS_API_ACTUATOR_TELEMETRY tm = adcsApi.Get_Actuator_Telemetry();
            SEPP_IADCS_API_VECTOR3_XYZ_FLOAT cs = tm.getREACTIONWHEEL_CURRENT_SPEED_VECTOR_XYZ_RADPS();
            return (Attribute) HelperAttributes.javaType2Attribute(cs.getX());
        }
        if (DefinitionNames.PARAMETER_NAME_20.equals(identifier.getValue())) {
            SEPP_IADCS_API_ACTUATOR_TELEMETRY tm = adcsApi.Get_Actuator_Telemetry();
            SEPP_IADCS_API_VECTOR3_XYZ_FLOAT cs = tm.getREACTIONWHEEL_CURRENT_SPEED_VECTOR_XYZ_RADPS();
            return (Attribute) HelperAttributes.javaType2Attribute(cs.getY());
        }
        if (DefinitionNames.PARAMETER_NAME_21.equals(identifier.getValue())) {
            SEPP_IADCS_API_ACTUATOR_TELEMETRY tm = adcsApi.Get_Actuator_Telemetry();
            SEPP_IADCS_API_VECTOR3_XYZ_FLOAT cs = tm.getREACTIONWHEEL_CURRENT_SPEED_VECTOR_XYZ_RADPS();
            return (Attribute) HelperAttributes.javaType2Attribute(cs.getZ());
        }
        if (DefinitionNames.PARAMETER_NAME_22.equals(identifier.getValue())) {
            SEPP_IADCS_API_ACTUATOR_TELEMETRY tm = adcsApi.Get_Actuator_Telemetry();
            SEPP_IADCS_API_VECTOR3_XYZ_FLOAT td = tm.getMAGNETORQUERS_TARGET_DIPOLE_MOMENT_VECTOR_AM2();
            return (Attribute) HelperAttributes.javaType2Attribute(td.getX());
        }
        if (DefinitionNames.PARAMETER_NAME_23.equals(identifier.getValue())) {
            SEPP_IADCS_API_ACTUATOR_TELEMETRY tm = adcsApi.Get_Actuator_Telemetry();
            SEPP_IADCS_API_VECTOR3_XYZ_FLOAT td = tm.getMAGNETORQUERS_TARGET_DIPOLE_MOMENT_VECTOR_AM2();
            return (Attribute) HelperAttributes.javaType2Attribute(td.getY());
        }
        if (DefinitionNames.PARAMETER_NAME_24.equals(identifier.getValue())) {
            SEPP_IADCS_API_ACTUATOR_TELEMETRY tm = adcsApi.Get_Actuator_Telemetry();
            SEPP_IADCS_API_VECTOR3_XYZ_FLOAT td = tm.getMAGNETORQUERS_TARGET_DIPOLE_MOMENT_VECTOR_AM2();
            return (Attribute) HelperAttributes.javaType2Attribute(td.getZ());
        }
        if (DefinitionNames.PARAMETER_NAME_25.equals(identifier.getValue())) {
            SEPP_IADCS_API_ACTUATOR_TELEMETRY tm = adcsApi.Get_Actuator_Telemetry();
            IADCS_100_Actuator_State state = tm.getMAGNETORQUERS_CURRENT_STATE();
            return (Attribute) HelperAttributes.javaType2Attribute(state.swigValue());
        }

        throw new IOException("The value could not be acquired!");
    }

    @Override
    public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UInteger actionArrived(Identifier name, AttributeValueList attributeValues, Long actionInstanceObjId,
        boolean reportProgress, MALInteraction interaction) {
        if (DefinitionNames.ACTION_NAME_1.equals(name.getValue())) {
            // Call the action
            Long value = (Long) HelperAttributes.attribute2JavaType(attributeValues.get(0).getValue());
            adcsApi.Set_Epoch_Time(value);
        }

        if (DefinitionNames.ACTION_NAME_2.equals(name.getValue())) {
            // Call the action
            adcsApi.Set_Operation_Mode_Idle();
        }

        if (DefinitionNames.ACTION_NAME_3.equals(name.getValue())) {
            // Call the action
            SEPP_IADCS_API_DETUMBLING_MODE_PARAMETERS values = new SEPP_IADCS_API_DETUMBLING_MODE_PARAMETERS();
            adcsApi.Start_Operation_Mode_Detumbling(values);
        }

        if (DefinitionNames.ACTION_NAME_4.equals(name.getValue())) {
            // Call the action
            SEPP_IADCS_API_EARTH_TARGET_POINTING_CONST_VELOCITY_MODE_PARAMETERS values = new SEPP_IADCS_API_EARTH_TARGET_POINTING_CONST_VELOCITY_MODE_PARAMETERS();

            Long start_Epoch_Time = (Long) HelperAttributes.attribute2JavaType(attributeValues.get(0).getValue());
            values.setSTART_EPOCH_TIME_MSEC(start_Epoch_Time);
            Long stop_Epoch_Time = (Long) HelperAttributes.attribute2JavaType(attributeValues.get(1).getValue());
            values.setSTOP_EPOCH_TIME_MSEC(stop_Epoch_Time);
            Float start_Longitude = (Float) HelperAttributes.attribute2JavaType(attributeValues.get(2).getValue());
            values.setSTART_LATITUDE_RAD(start_Longitude);
            Float start_Latitude = (Float) HelperAttributes.attribute2JavaType(attributeValues.get(3).getValue());
            values.setSTART_LONGITUDE_RAD(start_Latitude);
            Float stop_Longitude = (Float) HelperAttributes.attribute2JavaType(attributeValues.get(4).getValue());
            values.setSTOP_LATITUDE_RAD(stop_Longitude);
            Float stop_Latitude = (Float) HelperAttributes.attribute2JavaType(attributeValues.get(5).getValue());
            values.setSTOP_LONGITUDE_RAD(stop_Latitude);

            adcsApi.Start_Target_Pointing_Earth_Const_Velocity_Mode(values);
        }

        if (DefinitionNames.ACTION_NAME_5.equals(name.getValue())) {
            // Call the action
            SEPP_IADCS_API_EARTH_TARGET_POINTING_FIXED_MODE_PARAMETERS values = new SEPP_IADCS_API_EARTH_TARGET_POINTING_FIXED_MODE_PARAMETERS();

            Float lat = (Float) HelperAttributes.attribute2JavaType(attributeValues.get(0).getValue());
            values.setTARGET_LATITUDE_RAD(lat);
            Float lon = (Float) HelperAttributes.attribute2JavaType(attributeValues.get(1).getValue());
            values.setTARGET_LONGITUDE_RAD(lon);

            adcsApi.Start_Target_Pointing_Earth_Fix_Mode(values);
        }

        if (DefinitionNames.ACTION_NAME_6.equals(name.getValue())) {
            // Call the action
            SEPP_IADCS_API_TARGET_POINTING_NADIR_MODE_PARAMETERS values = new SEPP_IADCS_API_TARGET_POINTING_NADIR_MODE_PARAMETERS();
            adcsApi.Start_Target_Pointing_Nadir_Mode(values);
        }

        if (DefinitionNames.ACTION_NAME_7.equals(name.getValue())) {
            IADCS_100_Axis axis = IADCS_100_Axis.IADCS_100_AXIS_X; // Default value
            String body = (String) HelperAttributes.attribute2JavaType(attributeValues.get(0).getValue());

            if ("x".equals(body)) {
                axis = IADCS_100_Axis.IADCS_100_AXIS_X;
            }
            if ("y".equals(body)) {
                axis = IADCS_100_Axis.IADCS_100_AXIS_Y;
            }
            if ("z".equals(body)) {
                axis = IADCS_100_Axis.IADCS_100_AXIS_Z;
            }

            Float angular_velocity = (Float) HelperAttributes.attribute2JavaType(attributeValues.get(1).getValue());

            adcsApi.Start_SingleAxis_AngularVelocity_Controller(axis, angular_velocity);
        }

        if (DefinitionNames.ACTION_NAME_8.equals(name.getValue())) {
            // Call the action
            SEPP_IADCS_API_SUN_POINTING_MODE_PARAMETERS values = new SEPP_IADCS_API_SUN_POINTING_MODE_PARAMETERS();
            adcsApi.Start_Operation_Mode_Sun_Pointing(values);
        }

        if (DefinitionNames.ACTION_NAME_9.equals(name.getValue())) {
            // Call the action
            adcsApi.Stop_Operation_Mode_Detumbling();
        }

        if (DefinitionNames.ACTION_NAME_10.equals(name.getValue())) {
            // Call the action
            adcsApi.Stop_Target_Pointing_Earth_Const_Velocity_Mode();
        }

        if (DefinitionNames.ACTION_NAME_11.equals(name.getValue())) {
            // Call the action
            adcsApi.Stop_Target_Pointing_Earth_Fix_Mode();
        }

        if (DefinitionNames.ACTION_NAME_12.equals(name.getValue())) {
            // Call the action
            adcsApi.Stop_Target_Pointing_Nadir_Mode();
        }

        if (DefinitionNames.ACTION_NAME_13.equals(name.getValue())) {
            // Call the action
            adcsApi.Stop_SingleAxis_AngularVelocity_Controller(IADCS_100_Axis.IADCS_100_AXIS_X);
            adcsApi.Stop_SingleAxis_AngularVelocity_Controller(IADCS_100_Axis.IADCS_100_AXIS_Y);
            adcsApi.Stop_SingleAxis_AngularVelocity_Controller(IADCS_100_Axis.IADCS_100_AXIS_Z);
        }

        if (DefinitionNames.ACTION_NAME_14.equals(name.getValue())) {
            // Call the action
            adcsApi.Stop_Operation_Mode_Sun_Pointing();
        }

        return new UInteger(0);  // There was an Error with the action!
    }

    public void go() {
        HKTelemetry.dumpHKTelemetry(adcsApi);
    }

}
