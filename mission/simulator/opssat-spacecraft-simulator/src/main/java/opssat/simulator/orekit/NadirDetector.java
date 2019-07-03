package opssat.simulator.orekit;

import java.time.Instant;

import org.orekit.errors.OrekitException;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.AbstractDetector;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.propagation.events.handlers.EventHandler.Action;
import org.orekit.time.AbsoluteDate;

public class NadirDetector extends AbstractDetector<NadirDetector> {

  public static final double ATT_VALUE = 1;
  private boolean transitionStarted;
  private double transitionTime = 10000; // milliseconds
  private Instant startTime;

  public NadirDetector() {
    super(DEFAULT_MAXCHECK, DEFAULT_THRESHOLD, DEFAULT_MAX_ITER, null);
    transitionStarted = false;
  }

  @Override
  public void init(SpacecraftState s0, AbsoluteDate t) {
  }

  @Override
  public double g(SpacecraftState s) throws OrekitException {
    return s.getAdditionalState("attitude")[0];
  }

  @Override
  public Action eventOccurred(SpacecraftState s, boolean increasing) throws OrekitException {
    System.out.println(
        "SWiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiITCH");
    return Action.CONTINUE;
  }

  @Override
  public SpacecraftState resetState(SpacecraftState oldState) throws OrekitException {
    transitionStarted = false;
    return oldState;
  }

  @Override
  protected NadirDetector create(double newMaxCheck, double newThreshold, int newMaxIter,
      EventHandler<? super NadirDetector> newHandler) {
    return new NadirDetector();
  }
}
