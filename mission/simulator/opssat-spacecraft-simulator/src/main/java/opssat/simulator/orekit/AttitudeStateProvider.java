package opssat.simulator.orekit;

import org.orekit.errors.PropagationException;
import org.orekit.propagation.AdditionalStateProvider;
import org.orekit.propagation.SpacecraftState;
import org.orekit.time.AbsoluteDate;

public class AttitudeStateProvider implements AdditionalStateProvider {

  private double currentAttitude;
  private String name = "attitude";
  private double transitionTime = 10;
  private AbsoluteDate transitionDate = null;
  private boolean switched = false;

  public AttitudeStateProvider(double initialAttitude) {
    this.currentAttitude = initialAttitude;
  }

  public void setSwitched(boolean b) {
    switched = b;
  }

  @Override
  public String getName() {
    return name;
  }

  /**
   * Use this to keep the provider up to date since the attitude mode will not be
   * determined by the spacecraft state.
   * 
   * @param val The representation value of the attitude mode
   */
  public void updateAttitude(double val) {
    currentAttitude = val;
  }

  @Override
  public double[] getAdditionalState(SpacecraftState state) throws PropagationException {
    double[] additionalState = new double[1];

    // Stays in its attitude if the transition button have not been pressed
    if (!switched) {
      additionalState[0] = -1.;
    }

    // linearly switch to 1 over the transition time after the step where the button
    // have been pressed
    else {
      if (transitionDate == null) {
        transitionDate = state.getDate();
      }
      double transition = 2 * state.getDate().durationFrom(transitionDate) / transitionTime - 1.;
      System.out.println(transition);
      if (transition < 1.) {
        additionalState[0] = transition;
      } else {
        additionalState[0] = 1.;
      }
    }
    return additionalState;
  }

}
