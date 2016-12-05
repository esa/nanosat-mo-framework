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
package esa.mo.platform.impl.provider.softsim;

import esa.mo.platform.impl.provider.gen.AutonomousADCSAdapterInterface;
import esa.mo.platform.impl.util.HelperIADCS100;
import java.io.IOException;
import opssat.simulator.main.ESASimulator;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinition;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionBDot;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionNadirPointing;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionSingleSpinning;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionSunPointing;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionTargetTracking;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeInstance;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeInstanceBDot;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeInstanceNadirPointing;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeInstanceSingleSpinning;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeInstanceSunPointing;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeInstanceTargetTracking;
import org.ccsds.moims.mo.platform.structures.Quaternions;
import org.ccsds.moims.mo.platform.structures.Vector3D;
import org.ccsds.moims.mo.platform.structures.WheelSpeed;

/**
 *
 * @author Cesar Coelho
 */
public class AutonomousADCSSoftSimAdapter implements AutonomousADCSAdapterInterface {

    private final ESASimulator instrumentsSimulator;
    private AttitudeDefinition attitude;

    // Time from Epoch in milliseconds
    private final static long[] BEGIN_END_TIMES = {0, Long.MAX_VALUE}; // StartTime, StopTime in (milliseconds)
    private final static byte MODE_STOP = 0;  // Zero means stop; One means start
    private final static byte MODE_START = 1;  // Zero means stop; One means start
    
    public AutonomousADCSSoftSimAdapter(ESASimulator instrumentsSimulator){
        this.instrumentsSimulator = instrumentsSimulator;
    }

    @Override
    public synchronized boolean isUnitAvailable() {
        return true;
    }

    @Override
    public synchronized void setDesiredAttitude(final AttitudeDefinition att) throws IOException {
        this.attitude = att;

        if(att instanceof AttitudeDefinitionBDot){
            instrumentsSimulator.getpFineADCS().opModeDetumble((byte) 1, BEGIN_END_TIMES);
        }
        
        if(att instanceof AttitudeDefinitionSingleSpinning){
            AttitudeDefinitionSingleSpinning singleSpinAtt = (AttitudeDefinitionSingleSpinning) att;
            Vector3D body = singleSpinAtt.getBodyAxis();
            final float[] targetVector = new float[7];
            targetVector[0] = body.getX().floatValue();
            targetVector[1] = body.getY().floatValue();
            targetVector[2] = body.getZ().floatValue();

            // Maybe we need stuff here...
            
            targetVector[6] = singleSpinAtt.getAngularVelocity().floatValue();
            
            instrumentsSimulator.getpFineADCS().opModeSetModeSpin(MODE_START, BEGIN_END_TIMES, targetVector);
        }
        
        if(att instanceof AttitudeDefinitionSunPointing){
            final byte actuatorMode = 0; // Zero because it is dummy and only RW is available
            // [0]: Mode Zero means stop; One means start 
            // [1]: Actuator mode
            final byte mode[] = {MODE_START, actuatorMode};

            final float[] targetVector = new float[3];
            targetVector[0] = 0;
            targetVector[1] = 0;
            targetVector[2] = 0;
            
            instrumentsSimulator.getpFineADCS().opModeSunPointing(mode, BEGIN_END_TIMES, targetVector);
        }
        
        if(att instanceof AttitudeDefinitionTargetTracking){
            AttitudeDefinitionTargetTracking targetTrackingAtt = (AttitudeDefinitionTargetTracking) att;

            float[] latitudeLongitude = new float[2];
            latitudeLongitude[0] = targetTrackingAtt.getLatitude().floatValue();
            latitudeLongitude[0] = targetTrackingAtt.getLongitude().floatValue();
            
            instrumentsSimulator.getpFineADCS().opModeSetFixWGS84TargetTracking(MODE_START, BEGIN_END_TIMES, latitudeLongitude);
        }
        
        if(att instanceof AttitudeDefinitionNadirPointing){
            instrumentsSimulator.getpFineADCS().opModeSetNadirTargetTracking(MODE_START, BEGIN_END_TIMES);
        }
        
    }

    @Override
    public synchronized void unset() throws IOException {
        // Set the ADCS to Idle mode
        instrumentsSimulator.getpFineADCS().opModeIdle();
    }

    @Override
    public synchronized AttitudeInstance getAttitudeInstance() throws IOException {
        final AttitudeDefinition att = this.attitude;

        if(att instanceof AttitudeDefinitionBDot){
            byte[] sensorTM = instrumentsSimulator.getpFineADCS().GetSensorTelemetry();
            byte[] actuatorTM = instrumentsSimulator.getpFineADCS().GetActuatorTelemetry();
            
            Vector3D magneticField = HelperIADCS100.getMagneticFieldFromSensorTM(sensorTM);
            Vector3D mtq = HelperIADCS100.getMTQFromActuatorTM(actuatorTM);
            WheelSpeed wheelSpeed = HelperIADCS100.getWheelSpeedFromActuatorTM(actuatorTM);
            
            AttitudeInstanceBDot instance = new AttitudeInstanceBDot();
            instance.setMagneticField(magneticField);
            instance.setMtqDipoleMomentum(mtq);
            instance.setWheelSpeed(wheelSpeed);
            return instance;
        }
        
        if(att instanceof AttitudeDefinitionSingleSpinning){
            byte[] status = instrumentsSimulator.getpFineADCS().opModeGetSpinModeStatus();
                        
            Vector3D sunVector = HelperIADCS100.getSunVectorFromSpinModeStatus(status);
            Vector3D magneticField = HelperIADCS100.getMagneticFieldFromSpinModeStatus(status);
            Quaternions quaternions = HelperIADCS100.getQuaternionsFromSpinModeStatus(status);
            Vector3D angularMomentum = HelperIADCS100.getAngularMomentumFromSpinModeStatus(status);
            Vector3D mtq = HelperIADCS100.getMTQFromSpinModeStatus(status);
            
            AttitudeInstanceSingleSpinning singleSpinningAtt = new AttitudeInstanceSingleSpinning();
            singleSpinningAtt.setSunVector(sunVector);
            singleSpinningAtt.setMagneticField(magneticField);
            singleSpinningAtt.setCurrentQuaternions(quaternions);
            singleSpinningAtt.setAngularMomentum(angularMomentum);
            singleSpinningAtt.setMtqDipoleMomentum(mtq);
            
            return singleSpinningAtt;
        }
        
        if(att instanceof AttitudeDefinitionSunPointing){
            byte[] status = instrumentsSimulator.getpFineADCS().opModeGetSunPointingStatus();
            final Vector3D sunVector = HelperIADCS100.getSunVectorFromSunPointingStatus(status);
            final Boolean valid = true; // Chinese dude will answer what this means...
            
            AttitudeInstanceSunPointing sunPointingAtt = new AttitudeInstanceSunPointing();
            sunPointingAtt.setSunVector(sunVector);
            sunPointingAtt.setValid(valid);
            
            // We pick the wheel speed for now, this might have to change in
            // the future because BST's API defines this field as TBD
            boolean someFlag = false;
            
            if (someFlag){
                Vector3D mtq = HelperIADCS100.getMTQFromSunPointingStatus(status);
                sunPointingAtt.setWheelSpeed(null);
                sunPointingAtt.setMtqDipoleMomentum(mtq);
            }else{
                WheelSpeed wheelSpeed = HelperIADCS100.getWheelSpeedFromSunPointingStatus(status);
                sunPointingAtt.setWheelSpeed(wheelSpeed);
                sunPointingAtt.setMtqDipoleMomentum(null);
            }

            return sunPointingAtt;
        }

        if(att instanceof AttitudeDefinitionTargetTracking){
            byte[] status = instrumentsSimulator.getpFineADCS().opModeGetFixWGS84TargetTracking();

            Vector3D position = HelperIADCS100.getPositionFromFixWGS84TargetTrackingStatus(status);
            Vector3D angularVelocity = HelperIADCS100.getAngularVelocityFromFixWGS84TargetTrackingStatus(status);
            WheelSpeed wheelSpeed = HelperIADCS100.getWheelSpeedFromFixWGS84TargetTrackingStatus(status);
            Quaternions current = HelperIADCS100.getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(status);
            Quaternions target = HelperIADCS100.getTargetQuaternionsFromFixWGS84TargetTrackingStatus(status);

            AttitudeInstanceTargetTracking targetTrackingAtt = new AttitudeInstanceTargetTracking();
            targetTrackingAtt.setPositionVector(position);
            targetTrackingAtt.setAngularVelocity(angularVelocity);
            targetTrackingAtt.setCurrentQuaternions(current);
            targetTrackingAtt.setTargetQuaternions(target);
            targetTrackingAtt.setWheelSpeed(wheelSpeed);
            return targetTrackingAtt;
        }
        
        if(att instanceof AttitudeDefinitionNadirPointing){
            byte[] status = instrumentsSimulator.getpFineADCS().opModeGetNadirTargetTrackingStatus();
            
            Vector3D position = HelperIADCS100.getPositionFromNadirTargetTrackingStatus(status);
            Vector3D angularVelocity = HelperIADCS100.getAngularVelocityFromNadirTargetTrackingStatus(status);
            Quaternions current = HelperIADCS100.getCurrentQuaternionsFromNadirTargetTrackingStatus(status);
            Quaternions target = HelperIADCS100.getTargetQuaternionsFromNadirTargetTrackingStatus(status);
            WheelSpeed wheelSpeed = HelperIADCS100.getWheelSpeedFromNadirTargetTrackingStatus(status);

            AttitudeInstanceNadirPointing nadirPointingAtt = new AttitudeInstanceNadirPointing();
            nadirPointingAtt.setPositionVector(position);
            nadirPointingAtt.setAngularVelocity(angularVelocity);
            nadirPointingAtt.setCurrentQuaternions(current);
            nadirPointingAtt.setTargetQuaternions(target);
            nadirPointingAtt.setSpeed(wheelSpeed);
            return nadirPointingAtt;
        }

        return null;
    }
    
}
