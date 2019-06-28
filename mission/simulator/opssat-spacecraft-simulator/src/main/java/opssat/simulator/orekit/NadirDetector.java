package opssat.simulator.orekit;

import java.time.Duration;
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
    try {
      double att = s.getAdditionalState("attitude")[0];
      Instant now = Instant.now();
      double res = 0;
      if (att == ATT_VALUE && !transitionStarted) {
        startTime = now;
        transitionStarted = true;
        res = -1;
      } else if (att == ATT_VALUE && transitionStarted) {
        long ms = Duration.between(startTime, now).toMillis();
        res = 2.0 * ms / transitionTime - 1.0;
        if (ms >= transitionTime) {
          res = 1;
        }
      } else {
        transitionStarted = false;
        res = -1;
      }
      // System.out.println("g = " + res);
      return res;
    } catch (OrekitException oe) {
      return -1;
    }
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
