/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 */
package esa.mo.helpertools.clock;

import java.util.Objects;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.Time;

public class SystemClock {

    private static final Logger LOGGER = Logger.getLogger(SystemClock.class.getName());

    private static final String USE_PLATFORM_CLOCK_PROPERTY = "esa.mo.nmf.app.systemTimeProvidedByPlatformClockService";

    private static boolean usePlatformClock = false;

    private static PlatformClockCallback platformClockCallback;

    private SystemClock() {
    }

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
