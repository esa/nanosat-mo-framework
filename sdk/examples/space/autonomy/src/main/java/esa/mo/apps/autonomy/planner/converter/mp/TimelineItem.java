package esa.mo.apps.autonomy.planner.converter.mp;

import java.util.Arrays;
import java.util.Objects;
import org.ccsds.moims.mo.mal.structures.Time;
import esa.mo.helpertools.helpers.HelperTime;

/**
 * TimelineItem is used by MPConverter to get already planned items in a timeline
 */
public class TimelineItem {

    private String id;
    private String name;
    private long lowerBound;
    private long upperBound;
    private String[] arguments;

    public TimelineItem(String name, long lowerBound, long upperBound, String[] arguments) {
        this(null, name, lowerBound, upperBound, arguments);
    }

    public TimelineItem(String id, String name, long lowerBound, long upperBound, String[] arguments) {
        this.id = id;
        this.name = name;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.arguments = arguments;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getLowerBound() {
        return lowerBound;
    }

    public long getUpperBound() {
        return upperBound;
    }

    public String[] getArguments() {
        return arguments;
    }

    public boolean contains(TimelineItem item) {
        if (this == item) {
            return true;
        }

        if (item == null) {
            return false;
        }

        return Objects.equals(name, item.getName())
            && item.getLowerBound() >= lowerBound
            && item.getUpperBound() <= upperBound
            && Arrays.equals(arguments, item.getArguments());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null) {
            return false;
        }

        if (getClass() != object.getClass()) {
            return false;
        }

        TimelineItem item = (TimelineItem) object;
        return Objects.equals(name, item.getName())
            && Objects.equals(lowerBound, item.getLowerBound())
            && Objects.equals(upperBound, item.getUpperBound())
            && Arrays.equals(arguments, item.getArguments());
    }

    @Override
    public String toString() {
        return String.format("%s%s(%s) %s - %s",
            id == null ? "" : id + " ",
            name,
            arguments != null ? String.join(",", Arrays.asList(arguments)) : "",
            HelperTime.time2readableString(new Time(lowerBound)),
            HelperTime.time2readableString(new Time(upperBound))
        );
    }
}
