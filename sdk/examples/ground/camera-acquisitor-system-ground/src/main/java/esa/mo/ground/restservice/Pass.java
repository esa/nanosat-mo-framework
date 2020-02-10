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
package esa.mo.ground.restservice;

import esa.mo.nmf.apps.CameraAcquisitorSystemMCAdapter;
import org.hipparchus.ode.events.Action;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.BooleanDetector;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.time.AbsoluteDate;

/**
 *
 * @author Kevin Otto <Kevin@KevinOtto.de>
 */
public class Pass implements EventHandler<BooleanDetector>
{

  private final AbsoluteDate notBeforeDate;
  private final long worstCaseRotationTimeSeconds;

  private AbsoluteDate passStart;
  private AbsoluteDate passEnd;
  private AbsoluteDate optimalTime;

  private String resultTime;

  private boolean timeFound = false;

  private boolean startIsSet = false;

  public Pass(AbsoluteDate notBeforeDate, long worstCaseRotationTimeSeconds)
  {
    this.notBeforeDate = notBeforeDate;
    this.worstCaseRotationTimeSeconds = worstCaseRotationTimeSeconds;
  }

  public String getResultTime()
  {
    return resultTime;
  }

  public AbsoluteDate getPassStart()
  {
    return passStart;
  }

  public AbsoluteDate getPassEnd()
  {
    return passEnd;
  }

  public AbsoluteDate getOptimalTime()
  {
    return optimalTime;
  }

  public boolean isTimeFound()
  {
    return timeFound;
  }

  @Override
  public Action eventOccurred(SpacecraftState s, BooleanDetector detector, boolean increasing)
  {
    if (increasing) {
      if (!startIsSet) {
        this.passStart = s.getDate();
        startIsSet = true;
      }
    } else {
      this.passEnd = s.getDate();
      double elepsedTime = this.passEnd.durationFrom(this.passStart);
      this.optimalTime = this.passStart.shiftedBy(elepsedTime / 2);
      this.resultTime = this.optimalTime.toString();
      if (this.optimalTime.durationFrom(CameraAcquisitorSystemMCAdapter.getNow()) <= worstCaseRotationTimeSeconds && this.optimalTime.compareTo(
          this.notBeforeDate) > 0) {

        //time too close, try again
        startIsSet = false;
        return Action.CONTINUE;
      }
      // time found
      timeFound = true;
      return Action.STOP;
    }
    return Action.CONTINUE;
  }
}
