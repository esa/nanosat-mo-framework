package esa.mo.apps.autonomy.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TestCoordinateConverter {

    @Test
    public void testToPlannerCoordinate() {
        assertEquals("10000", CoordinateConverter.toPlannerCoordinate(("10")));
        assertEquals("10123", CoordinateConverter.toPlannerCoordinate(("10.123")));
        assertEquals("10123", CoordinateConverter.toPlannerCoordinate(("10.1234")));
        assertEquals("123", CoordinateConverter.toPlannerCoordinate(("00.1234")));
    }

    @Test
    public void testFromPlannerCoordinate() {
        assertEquals("10", CoordinateConverter.fromPlannerCoordinate(("10000")));
        assertEquals("10.1", CoordinateConverter.fromPlannerCoordinate(("10100")));
        assertEquals("10.12", CoordinateConverter.fromPlannerCoordinate(("10120")));
        assertEquals("10.123", CoordinateConverter.fromPlannerCoordinate(("10123")));
        assertEquals("0.01", CoordinateConverter.fromPlannerCoordinate(("10")));
    }
}
