package esa.mo.apps.autonomy.planner.converter.tle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.ccsds.moims.mo.platform.gps.structures.TwoLineElementSet;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orekit.propagation.analytical.tle.TLE;

public class TestTLEConverter {

    @BeforeClass
    public static void setup() {
        System.setProperty("orekit.navigation.folder", "src/main/external-resources/orekit");
        TLEConverter.init();
    }

    @Test
    public void testConverter() {

        String LINE_1 = "1 44878U 19092F   20159.72929773  .00000725  00000-0  41750-4 0  9990";
        String LINE_2 = "2 44878  97.4685 343.1680 0015119  36.0805 324.1445 15.15469997 26069";

        TwoLineElementSet twoLineElement = TLEConverter.convert(LINE_1, LINE_2);

        assertNotNull(twoLineElement);

        TLE tle = TLEConverter.convert(twoLineElement);

        assertNotNull(tle);
        assertEquals(LINE_1, tle.getLine1());
        assertEquals(LINE_2, tle.getLine2());
    }
}
