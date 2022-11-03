/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opssat.simulator.orekit;

import org.hipparchus.ode.events.Action;
import org.orekit.errors.OrekitException;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.AbstractDetector;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.time.AbsoluteDate;

/**
 *
 * @author yannick
 */
public class AttitudeDetector extends AbstractDetector<AttitudeDetector>
{
  private int position;

  public AttitudeDetector(final int position) {
    super(DEFAULT_MAXCHECK, DEFAULT_THRESHOLD, DEFAULT_MAX_ITER, null);
    this.position = position;
  }

  @Override
  public void init(final SpacecraftState s0, final AbsoluteDate t) {
  }

  @Override
  public Action eventOccurred(final SpacecraftState s, final boolean increasing) throws OrekitException {
    return Action.CONTINUE;
  }

  @Override
  public SpacecraftState resetState(final SpacecraftState oldState) throws OrekitException {
    return oldState;
  }

  @Override
  public double g(final SpacecraftState ss)
  {
    return ss.getAdditionalState("attitude")[position];
  }

  @Override
  protected AttitudeDetector create(final double d, final double d1, final int i,
                                    final EventHandler<? super AttitudeDetector> eh)
  {
    return new AttitudeDetector(0);
  }
}
