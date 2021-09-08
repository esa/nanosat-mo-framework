package esa.mo.mp.impl.exec;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * TimelineItem is used by TimelineExecutionEngine to execute items in a timeline
 */
public class TimelineItem implements Comparable<TimelineItem> {

    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private long earliestStartTime;
    private long latestStartTime;
    private String itemId;
    private ItemCallback callback;

    public TimelineItem(long earliestStartTime, long latestStartTime, String itemId, ItemCallback callback) {
        this.earliestStartTime = earliestStartTime;
        this.latestStartTime = latestStartTime;
        this.itemId = itemId;
        this.callback = callback;
    }

    public long getEarliestStartTime() {
        return this.earliestStartTime;
    }

    public long getLatestStartTime() {
        return this.latestStartTime;
    }

    public String getItemId() {
        return itemId;
    }

    public ItemCallback getCallback() {
        return callback;
    }

    @Override
    public int compareTo(TimelineItem item) {
        return Long.compare(item.earliestStartTime, this.earliestStartTime);
    }

    @Override
    public String toString() {
        return this.formatTime(this.earliestStartTime) + " | " + this.formatTime(this.latestStartTime) + " | " + this.itemId;
    }

    private String formatTime(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(date);
    }
}
