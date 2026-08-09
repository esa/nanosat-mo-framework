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
public class AttitudeDetector extends AbstractDetector<AttitudeDetector> {

    /**
     * Which of the attitude modes this detector watches, as an index into the
     * "attitude" additional state.
     */
    private final int position;

    public AttitudeDetector(int position) {
        this(DEFAULT_MAXCHECK, DEFAULT_THRESHOLD, DEFAULT_MAX_ITER, null, position);
    }

    private AttitudeDetector(double maxCheck, double threshold, int maxIter,
            EventHandler<? super AttitudeDetector> handler, int position) {
        super(maxCheck, threshold, maxIter, handler);
        this.position = position;
    }

    @Override
    public void init(SpacecraftState s0, AbsoluteDate t) {
    }

    @Override
    public Action eventOccurred(SpacecraftState s, boolean increasing) throws OrekitException {
        return Action.CONTINUE;
    }

    @Override
    public SpacecraftState resetState(SpacecraftState oldState) throws OrekitException {
        return oldState;
    }

    @Override
    public double g(SpacecraftState ss) {
        return ss.getAdditionalState("attitude")[position];
    }

    /**
     * Copies this detector with the settings given.
     *
     * Orekit never changes a detector in place: asking for one with a different
     * handler, or a different check interval, returns a copy made here. The
     * AttitudesSequence puts its own handler on every detector it is given that
     * way, and that handler is what performs the switch between attitude
     * providers.
     *
     * Everything therefore has to be carried over. Building a fresh detector
     * instead loses the handler, so the switch never happens, and resets the
     * mode watched to the first one, so every detector ends up watching the same
     * mode. The attitude then never changes, whatever is commanded.
     */
    @Override
    protected AttitudeDetector create(double maxCheck, double threshold, int maxIter,
            EventHandler<? super AttitudeDetector> handler) {
        return new AttitudeDetector(maxCheck, threshold, maxIter, handler, this.position);
    }
}
