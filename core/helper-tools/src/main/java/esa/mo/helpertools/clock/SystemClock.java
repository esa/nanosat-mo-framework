package esa.mo.helpertools.clock;

import java.util.Objects;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.Time;

public class SystemClock {

    private static final Logger LOGGER = Logger.getLogger(SystemClock.class.getName());

    private static final String USE_PLATFORM_CLOCK_PROPERTY = "esa.mo.nmf.app.systemTimeProvidedByPlatformClockService";

    private static boolean usePlatformClock = false;

    private static PlatformClockCallback platformClockCallback;

    private SystemClock() {}

    public static void setPlatformClockCallback(PlatformClockCallback callback) {
        platformClockCallback = callback;
        if (platformClockCallback != null && Objects.equals(System.getProperty(USE_PLATFORM_CLOCK_PROPERTY), "true")) {
            usePlatformClock = true;
            LOGGER.info("Using platform clock");
        } else {
            LOGGER.info("Using system clock");
        }
    }

    public static Time getTime() {
        return usePlatformClock ? platformClockCallback.getPlatformTime() : new Time(System.currentTimeMillis());
    }

    public static int getTimeFactor() {
        return usePlatformClock ? platformClockCallback.getPlatformTimeFactor() : 1;
    }
}
