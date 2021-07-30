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
package esa.mo.platform.impl.provider.gen;

import java.io.IOException;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.ActuatorsTelemetry;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeMode;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeTelemetry;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.ReactionWheelIdentifier;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.ReactionWheelParameters;

/**
 *
 * @author Cesar Coelho
 */
public interface AutonomousADCSAdapterInterface
{

  /**
   * Checks if the device is present and accessible.
   *
   * @return true if the device is present and available for operations.
   */
  boolean isUnitAvailable();

  /**
   * The setDesiredAttitude operation shall set a certain attitude based on the AttitudeMode
   * descriptor
   *
   * @param attitude
   * @throws IOException in case of failure on the interface
   * @throws UnsupportedOperationException if the implementation does not support the requested ADCS
   * mode
   */
  void setDesiredAttitude(AttitudeMode attitude) throws IOException, UnsupportedOperationException;

  /**
   * The unset operation shall reset the attitude mode.
   *
   * @throws IOException
   */
  void unset() throws IOException;

  /**
   * The getAttitudeTelemetry returns an object representing the attitude information provided by an
   * ADCS unit
   *
   * @return The Attitude Telemetry from the ADCS
   * @throws IOException
   */
  AttitudeTelemetry getAttitudeTelemetry() throws IOException;

  /**
   * The getActuatorsTelemetry returns an object representing the actuators information provided by
   * an ADCS unit
   *
   * @return The Attitude Telemetry from the ADCS
   * @throws IOException
   */
  ActuatorsTelemetry getActuatorsTelemetry() throws IOException;

  /**
   * Validate Attitude Descriptor
   *
   * @param attitude requested attitude mode descriptor to be validated
   * @return null if attitude descriptor is valid. Human readable string with error message if
   * validation fails.
   */
  String validateAttitudeDescriptor(AttitudeMode attitude);

  /**
   * Get active attitude mode.
   *
   * @return Descriptor of the attitude currently set in the ADCS or NULL if none (IDLE)
   */
  AttitudeMode getActiveAttitudeMode();

  /**
   * Sets the Speeds for all Reaction wheels in rad/second
   *
   * @param wheelX speed for wheel x
   * @param wheelY speed for wheel y
   * @param wheelZ speed for wheel z
   * @param wheelU speed for wheel u
   * @param wheelV speed for wheel v
   * @param wheelW speed for wheel w
   */
  void setAllReactionWheelSpeeds(float wheelX, float wheelY, float wheelZ, float wheelU,
                                 float wheelV, float wheelW);

  /**
   * Sets the speed of one Reaction wheel
   *
   * @param wheel the wheel which the speed should be set for.
   * @param Speed the speed that should be set
   */
  void setReactionWheelSpeed(ReactionWheelIdentifier wheel, float Speed);

  /**
   * Sets the parameters for all Reaction wheels.
   *
   * @param parameters the parameters to be set
   */
  void setAllReactionWheelParameters(ReactionWheelParameters parameters);

  /**
   * Sets the dipole moments for all Magnetorquers in Am (in actuator frame)
   *
   * @param dipoleX dipole moment for Magnetorquer x
   * @param dipoleY dipole moment for Magnetorquer y
   * @param dipoleZ dipole moment for Magnetorquer z
   */
  void setAllMagnetorquersDipoleMoments(Float dipoleX, Float dipoleY, Float dipoleZ);

  /**
   * gets the current Reaction wheel Parameters.
   *
   * @return the current Reaction wheel parameters
   */
  ReactionWheelParameters getAllReactionWheelParameters();

}
