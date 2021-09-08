package esa.mo.apps.autonomy.vw;

/**
 * VisibilityWindowItem is a data class used by VisibilityWindow
 */
public class VisibilityWindowItem {
    private Double latitude;
    private Double longitude;
    private long start;
    private long end;

    public VisibilityWindowItem(Double latitude, Double longitude, long start, long end) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.start = start;
        this.end = end;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }
}
