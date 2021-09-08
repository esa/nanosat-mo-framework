package esa.mo.helpertools.clock;

import org.ccsds.moims.mo.mal.structures.Time;

public interface PlatformClockCallback {
    public Time getPlatformTime();
    public int getPlatformTimeFactor();
}
