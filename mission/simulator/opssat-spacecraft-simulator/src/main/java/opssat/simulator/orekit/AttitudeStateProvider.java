/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2016      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under the European Space Agency Public License, Version 2.0
 *  You may not use this file except in compliance with the License.
 *
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ----------------------------------------------------------------------------
 */
package opssat.simulator.orekit;

import org.orekit.propagation.AdditionalStateProvider;
import org.orekit.propagation.SpacecraftState;
import org.orekit.time.AbsoluteDate;

public class AttitudeStateProvider implements AdditionalStateProvider
{

  private String name = "attitude";
  private double transitionTime = 1;
  private AbsoluteDate[] transitionDate = {null, null, null, null, null, null, null, null};
  private boolean[] switched = {false, false, false, false, false, false, false, false};

  public AttitudeStateProvider()
  {
  }

  public void setSwitched(boolean[] b)
  {
    switched = b;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public double[] getAdditionalState(SpacecraftState state)
  {
    double[] additionalState = new double[8];

    // Stays in its attitude if the transition button have not been pressed
    for (int i = 0; i < switched.length; i++) {
      if (!switched[i]) {
        additionalState[i] = -1.;
        transitionDate[i] = null; // reset for linear function
      } // have been pressed
      else {
        if (transitionDate[i] == null) {
          transitionDate[i] = state.getDate();
        }
        double transition = 2 * state.getDate().durationFrom(transitionDate[i]) / transitionTime - 1.;
        if (transition < 1.) {
          additionalState[i] = transition;
        } else {
          additionalState[i] = 1.;
        }
      }
    }

    return additionalState;
  }

}
