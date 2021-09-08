package esa.mo.apps.autonomy.vw;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import java.util.List;
import org.ccsds.moims.mo.mal.structures.Time;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orekit.propagation.analytical.tle.TLE;

public class TestVisibilityWindow {

    @BeforeClass
    public static void setup() {
        System.setProperty("orekit.navigation.folder", "src/main/external-resources/orekit");
        VisibilityWindow.init();
    }

    @Test
    public void testConverter() throws Exception {

        TLE tle = new TLE(
            "1 44878U 19092F   20159.72929773  .00000725  00000-0  41750-4 0  9990",
            "2 44878  97.4685 343.1680 0015119  36.0805 324.1445 15.15469997 26069"
        );

        Double latitude = 42d;
        Double longitude = 12d;
        Double maxAngle = 15d;
        Time start = new Time(1577836800000l); // 2020-01-01T00:00:00.000
        Time end = new Time(1578441600000l);   // 2020-01-08T00:00:00.000

        List<VisibilityWindowItem> visibilityWindows = VisibilityWindow.getWindows(tle, latitude, longitude, maxAngle, start, end);

        assertNotNull(visibilityWindows);
        assertFalse(visibilityWindows.isEmpty());
    }
}
