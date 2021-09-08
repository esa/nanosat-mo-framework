package esa.mo.apps.autonomy.util;

/**
 * Used for conversion between normal and plan coordinate format (x1000)
 */
public class CoordinateConverter {

    private CoordinateConverter() {}

    public static String toPlannerCoordinate(String coordinate) {
        // "10.123" => "10123"
        if (coordinate == null) return null;
        return Integer.toString((int)(Double.parseDouble(coordinate) * 1000));
    }

    public static String fromPlannerCoordinate(String plannerCoordinate) {
        // "10123" => "10.123"
        if (plannerCoordinate == null) return null;
        double coordinate = Integer.parseInt(plannerCoordinate) / 1000.0;
        int integerCoordinate = (int) coordinate;
        return coordinate == integerCoordinate ? String.valueOf(integerCoordinate) : String.valueOf(coordinate);
    }
}
