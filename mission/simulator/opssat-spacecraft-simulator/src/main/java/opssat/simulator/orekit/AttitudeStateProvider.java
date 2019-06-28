package opssat.simulator.orekit;

import org.orekit.errors.PropagationException;
import org.orekit.propagation.AdditionalStateProvider;
import org.orekit.propagation.SpacecraftState;

public class AttitudeStateProvider implements AdditionalStateProvider {

  private double currentAttitude;
  private String name = "attitude";

  public AttitudeStateProvider(double initialAttitude) {
    this.currentAttitude = initialAttitude;
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
    return new double[] { currentAttitude };
  }

}
