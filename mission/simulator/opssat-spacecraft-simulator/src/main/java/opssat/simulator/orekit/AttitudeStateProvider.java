package opssat.simulator.orekit;

import org.orekit.propagation.AdditionalStateProvider;
import org.orekit.propagation.SpacecraftState;
import org.orekit.time.AbsoluteDate;

public class AttitudeStateProvider implements AdditionalStateProvider
{

  private String name = "attitude";
  private double transitionTime = 1;
  private AbsoluteDate[] transitionDate = {null, null, null, null, null, null, null};
  private boolean[] switched = {false, false, false, false, false, false, false};

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
    double[] additionalState = new double[7];

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
