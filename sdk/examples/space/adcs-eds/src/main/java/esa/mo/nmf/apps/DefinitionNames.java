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
package esa.mo.nmf.apps;

/**
 *
 * @author cbwhi
 */
public class DefinitionNames {

    // Parameters
    public static final String PARAMETER_NAME_1 = "attitudeMode";
    public static final String PARAMETER_NAME_2 = "epochTime";
    public static final String PARAMETER_NAME_3 = "attitude.attitude.a";
    public static final String PARAMETER_NAME_4 = "attitude.attitude.b";
    public static final String PARAMETER_NAME_5 = "attitude.attitude.c";
    public static final String PARAMETER_NAME_6 = "attitude.attitude.d";
    public static final String PARAMETER_NAME_7 = "attitude.angularVelocity.x";
    public static final String PARAMETER_NAME_8 = "attitude.angularVelocity.y";
    public static final String PARAMETER_NAME_9 = "attitude.angularVelocity.z";
    public static final String PARAMETER_NAME_10 = "attitude.sunVector.x";
    public static final String PARAMETER_NAME_11 = "attitude.sunVector.y";
    public static final String PARAMETER_NAME_12 = "attitude.sunVector.z";
    public static final String PARAMETER_NAME_13 = "attitude.magneticField.x";
    public static final String PARAMETER_NAME_14 = "attitude.magneticField.y";
    public static final String PARAMETER_NAME_15 = "attitude.magneticField.z";
    public static final String PARAMETER_NAME_16 = "actuators.targetWheelSpeed.x";
    public static final String PARAMETER_NAME_17 = "actuators.targetWheelSpeed.y";
    public static final String PARAMETER_NAME_18 = "actuators.targetWheelSpeed.z";
    public static final String PARAMETER_NAME_19 = "actuators.currentWheelSpeed.x";
    public static final String PARAMETER_NAME_20 = "actuators.currentWheelSpeed.y";
    public static final String PARAMETER_NAME_21 = "actuators.currentWheelSpeed.z";
    public static final String PARAMETER_NAME_22 = "actuators.mtqDipoleMoment.x";
    public static final String PARAMETER_NAME_23 = "actuators.mtqDipoleMoment.y";
    public static final String PARAMETER_NAME_24 = "actuators.mtqDipoleMoment.z";
    public static final String PARAMETER_NAME_25 = "actuators.mtqState";

    public static final String PARAMETER_DESCRIPTION_1 = "The name of the current attitude mode selected. Empty if no mode is selected.";
    public static final String PARAMETER_DESCRIPTION_2 = "The current epoch time.";
    public static final String PARAMETER_DESCRIPTION_3 = "Calculated attitude quaternion of the spacecraft frame relative to International Celestial Reference Frame (ICRF). Quaternion scalar component a.";
    public static final String PARAMETER_DESCRIPTION_4 = "Calculated attitude quaternion of the spacecraft frame relative to International Celestial Reference Frame (ICRF). Quaternion vector component b*i.";
    public static final String PARAMETER_DESCRIPTION_5 = "Calculated attitude quaternion of the spacecraft frame relative to International Celestial Reference Frame (ICRF). Quaternion vector component c*j.";
    public static final String PARAMETER_DESCRIPTION_6 = "Calculated attitude quaternion of the spacecraft frame relative to International Celestial Reference Frame (ICRF). Quaternion vector component d*k.";
    public static final String PARAMETER_DESCRIPTION_7 = "Calculated angular velocity vector (x,y,z) relative to the spacecraft frame. The x component. [rad/sec]";
    public static final String PARAMETER_DESCRIPTION_8 = "Calculated angular velocity vector (x,y,z) relative to the spacecraft frame. The y component. [rad/sec]";
    public static final String PARAMETER_DESCRIPTION_9 = "Calculated angular velocity vector (x,y,z) relative to the spacecraft frame. The z component. [rad/sec]";
    public static final String PARAMETER_DESCRIPTION_10 = "Calculated dimensionless sun vector (x,y,z) relative to the spacecraft frame. The x component.";
    public static final String PARAMETER_DESCRIPTION_11 = "Calculated dimensionless sun vector (x,y,z) relative to the spacecraft frame. The y component.";
    public static final String PARAMETER_DESCRIPTION_12 = "Calculated dimensionless sun vector (x,y,z) relative to the spacecraft frame. The z component.";
    public static final String PARAMETER_DESCRIPTION_13 = "Calculated magnetic flux density relative to the spacecraft frame. The x component. [T]";
    public static final String PARAMETER_DESCRIPTION_14 = "Calculated magnetic flux density relative to the spacecraft frame. The y component. [T]";
    public static final String PARAMETER_DESCRIPTION_15 = "Calculated magnetic flux density relative to the spacecraft frame. The z component. [T]";
    public static final String PARAMETER_DESCRIPTION_16 = "Target reaction wheels speed. The x component. [rad/sec]";
    public static final String PARAMETER_DESCRIPTION_17 = "Target reaction wheels speed. The y component. [rad/sec]";
    public static final String PARAMETER_DESCRIPTION_18 = "Target reaction wheels speed. The z component. [rad/sec]";
    public static final String PARAMETER_DESCRIPTION_19 = "Current reaction wheels speed. The x component. [rad/sec]";
    public static final String PARAMETER_DESCRIPTION_20 = "Current reaction wheels speed. The y component. [rad/sec]";
    public static final String PARAMETER_DESCRIPTION_21 = "Current reaction wheels speed. The z component. [rad/sec]";
    public static final String PARAMETER_DESCRIPTION_22 = "Target magnetorquers dipole moment relative to the spacecraft frame. The x component. [A*m^2]";
    public static final String PARAMETER_DESCRIPTION_23 = "Target magnetorquers dipole moment relative to the spacecraft frame. The y component. [A*m^2]";
    public static final String PARAMETER_DESCRIPTION_24 = "Target magnetorquers dipole moment relative to the spacecraft frame. The z component. [A*m^2]";
    public static final String PARAMETER_DESCRIPTION_25 = "The current state of the magnetorquers.";

    // Actions
    public static final String ACTION_NAME_1 = "Set_Epoch_Time";
    public static final String ACTION_NAME_2 = "Set_Operation_Mode_Idle";
    public static final String ACTION_NAME_3 = "Start_Operation_Mode_Detumbling";
    public static final String ACTION_NAME_4 = "Start_Target_Pointing_Earth_Const_Velocity_Mode";
    public static final String ACTION_NAME_5 = "Start_Target_Pointing_Earth_Fix_Mode";
    public static final String ACTION_NAME_6 = "Start_Target_Pointing_Nadir_Mode";
    public static final String ACTION_NAME_7 = "Start_SingleAxis_AngularVelocity_Controller";
    public static final String ACTION_NAME_8 = "Start_Operation_Mode_Sun_Pointing";
    public static final String ACTION_NAME_9 = "Stop_Operation_Mode_Detumbling";
    public static final String ACTION_NAME_10 = "Stop_Target_Pointing_Earth_Const_Velocity_Mode";
    public static final String ACTION_NAME_11 = "Stop_Target_Pointing_Earth_Fix_Mode";
    public static final String ACTION_NAME_12 = "Stop_Target_Pointing_Nadir_Mode";
    public static final String ACTION_NAME_13 = "Stop_SingleAxis_AngularVelocity_Controller";
    public static final String ACTION_NAME_14 = "Stop_Operation_Mode_Sun_Pointing";

    public static final String ACTION_DESCRIPTION_1 = "Set iADCS time in seconds since epoch 01.01.1970";
    public static final String ACTION_DESCRIPTION_2 = "Triggers set mode idle command execution";
    public static final String ACTION_DESCRIPTION_3 = "Starts B-dot detumbling attitude mode";
    public static final String ACTION_DESCRIPTION_4 = "Starts Constant velocity Ground Target Tracking attitude mode";
    public static final String ACTION_DESCRIPTION_5 = "Starts Fixed Ground Target Tracking attitude mode";
    public static final String ACTION_DESCRIPTION_6 = "Starts Nadir Pointing attitude mode";
    public static final String ACTION_DESCRIPTION_7 = "Starts Single-axis spinning attitude mode";
    public static final String ACTION_DESCRIPTION_8 = "Starts Sun Pointing attitude mode";
    public static final String ACTION_DESCRIPTION_9 = "Stops B-dot detumbling attitude mode";
    public static final String ACTION_DESCRIPTION_10 = "Stops Constant velocity Ground Target Tracking attitude mode";
    public static final String ACTION_DESCRIPTION_11 = "Stops Fixed Ground Target Tracking attitude mode";
    public static final String ACTION_DESCRIPTION_12 = "Stops Nadir Pointing attitude mode";
    public static final String ACTION_DESCRIPTION_13 = "Stops Single-axis spinning attitude mode";
    public static final String ACTION_DESCRIPTION_14 = "Stops Sun Pointing attitude mode";

}
