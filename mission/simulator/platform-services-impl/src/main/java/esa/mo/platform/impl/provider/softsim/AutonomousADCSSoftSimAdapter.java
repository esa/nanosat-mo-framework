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
import org.ccsds.moims.mo.platform.autonomousadcs.structures.ActuatorsTelemetry;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeMode;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeBDot;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeNadirPointing;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeSingleSpinning;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeSunPointing;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeTargetTracking;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeVectorPointing;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeTelemetry;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.MagnetorquersState;
import org.ccsds.moims.mo.platform.structures.VectorF3D;

/**
 *
 * @author Cesar Coelho
 */
public class AutonomousADCSSoftSimAdapter implements AutonomousADCSAdapterInterface
{

  private final ESASimulator instrumentsSimulator;
  private AttitudeMode activeAttitudeMode;

  // Time from Epoch in milliseconds
  private final static long[] BEGIN_END_TIMES = {0, Long.MAX_VALUE}; // StartTime, StopTime in (milliseconds)
  private final static byte MODE_STOP = 0;  // Zero means stop; One means start
  private final static byte MODE_START = 1;  // Zero means stop; One means start

  public AutonomousADCSSoftSimAdapter(ESASimulator instrumentsSimulator)
  {
    this.instrumentsSimulator = instrumentsSimulator;
  }

  @Override
  public synchronized boolean isUnitAvailable()
  {
    return true;
  }

  @Override
  public synchronized void setDesiredAttitude(final AttitudeMode att) throws IOException
  {
    this.activeAttitudeMode = att;

    if (att instanceof AttitudeModeBDot) {
      instrumentsSimulator.getpFineADCS().opModeDetumble((byte) 1, BEGIN_END_TIMES);
    } else if (att instanceof AttitudeModeSingleSpinning) {
      AttitudeModeSingleSpinning singleSpinAtt = (AttitudeModeSingleSpinning) att;
      VectorF3D body = singleSpinAtt.getBodyAxis();
      final float[] targetVector = new float[7];
      targetVector[0] = body.getX();
      targetVector[1] = body.getY();
      targetVector[2] = body.getZ();

      // Maybe we need stuff here...
      targetVector[6] = singleSpinAtt.getAngularVelocity();

      instrumentsSimulator.getpFineADCS().opModeSetModeSpin(MODE_START, BEGIN_END_TIMES,
          targetVector);
    } else if (att instanceof AttitudeModeSunPointing) {
      final byte actuatorMode = 0; // Zero because it is dummy and only RW is available
      // [0]: Mode Zero means stop; One means start
      // [1]: Actuator mode
      final byte mode[] = {MODE_START, actuatorMode};

      final float[] targetVector = new float[3];
      targetVector[0] = 0;
      targetVector[1] = 0;
      targetVector[2] = 0;

      instrumentsSimulator.getpFineADCS().opModeSunPointing(mode, BEGIN_END_TIMES, targetVector);
    } else if (att instanceof AttitudeModeTargetTracking) {
      AttitudeModeTargetTracking targetTrackingAtt = (AttitudeModeTargetTracking) att;

      float[] latitudeLongitude = new float[2];
      latitudeLongitude[0] = targetTrackingAtt.getLatitude();
      latitudeLongitude[0] = targetTrackingAtt.getLongitude();

      instrumentsSimulator.getpFineADCS().opModeSetFixWGS84TargetTracking(MODE_START,
          BEGIN_END_TIMES, latitudeLongitude);
    } else if (att instanceof AttitudeModeNadirPointing) {
      instrumentsSimulator.getpFineADCS().opModeSetNadirTargetTracking(MODE_START, BEGIN_END_TIMES);
    } else if (att instanceof AttitudeModeVectorPointing) {
      AttitudeModeVectorPointing a = (AttitudeModeVectorPointing) att;
      VectorF3D vec = a.getTarget();
      instrumentsSimulator.getpFineADCS().getSimulatorNode().runVectorTargetTracking(
          vec.getX(), vec.getY(),
          vec.getZ(), a.getMargin());
    }

  }

  @Override
  public synchronized void unset() throws IOException
  {
    // Set the ADCS to Idle mode
    instrumentsSimulator.getpFineADCS().opModeIdle();
    this.activeAttitudeMode = null;
  }

  @Override
  public AttitudeTelemetry getAttitudeTelemetry() throws IOException
  {
    byte[] tmBuffer = instrumentsSimulator.getpFineADCS().GetSensorTelemetry();
    AttitudeTelemetry ret = new AttitudeTelemetry();
    ret.setAngularVelocity(HelperIADCS100.getAngularVelocityFromSensorTM(tmBuffer));
    ret.setAttitude(HelperIADCS100.getAttitudeFromSensorTM(tmBuffer));
    ret.setMagneticField(HelperIADCS100.getMagneticFieldFromSensorTM(tmBuffer));
    ret.setSunVector(new VectorF3D((float) 1, (float) 0, (float) 0)); // TODO provide real data
    return ret;
  }

  @Override
  public ActuatorsTelemetry getActuatorsTelemetry() throws IOException
  {
    byte[] tmBuffer = instrumentsSimulator.getpFineADCS().GetActuatorTelemetry();
    ActuatorsTelemetry ret = new ActuatorsTelemetry();
    ret.setMtqDipoleMoment(HelperIADCS100.getMTQFromActuatorTM(tmBuffer));
    ret.setMtqState(MagnetorquersState.ACTIVE); // TODO provide real data
    ret.setCurrentWheelSpeed(HelperIADCS100.getCurrentWheelSpeedFromActuatorTM(tmBuffer));
    ret.setTargetWheelSpeed(HelperIADCS100.getTargetWheelSpeedFromActuatorTM(tmBuffer));
    return ret;
  }

  @Override
  public String validateAttitudeDescriptor(AttitudeMode attitude)
  {
    // TODO do some rudimentary checks (i.e. if the angles make sense)
    return null;
  }

  @Override
  public AttitudeMode getActiveAttitudeMode()
  {
    return activeAttitudeMode;
  }

}
