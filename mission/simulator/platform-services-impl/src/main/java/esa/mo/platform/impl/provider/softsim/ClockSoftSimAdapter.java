package esa.mo.platform.impl.provider.softsim;

import org.ccsds.moims.mo.mal.structures.Time;
import esa.mo.platform.impl.provider.gen.ClockAdapterInterface;
import opssat.simulator.main.ESASimulator;

public class ClockSoftSimAdapter implements ClockAdapterInterface {

    private final ESASimulator instrumentsSimulator;

    public ClockSoftSimAdapter(ESASimulator instrumentsSimulator) {
        this.instrumentsSimulator = instrumentsSimulator;
    }

    public Time getTime() {
        return new Time(this.instrumentsSimulator.getSimulatorNode().getSimulatedTime());
    }

    public int getTimeFactor() {
        return this.instrumentsSimulator.getSimulatorNode().getTimeFactor();
    }
}
