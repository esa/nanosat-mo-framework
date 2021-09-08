package esa.mo.platform.impl.provider.gen;

import org.ccsds.moims.mo.mal.structures.Time;

public interface ClockAdapterInterface {
    public Time getTime();
    public int getTimeFactor();
}