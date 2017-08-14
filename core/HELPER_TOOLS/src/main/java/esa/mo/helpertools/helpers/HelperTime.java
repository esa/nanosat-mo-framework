/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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
package esa.mo.helpertools.helpers;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Time;

/**
 * A Helper class to simplify the conversions related with time.
 */
public class HelperTime {

    private final static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    private final static long ONE_MILLION = 1000000;

    /**
     * Converts a FineTime MAL data type timestamp into a readable string
     *
     * @param timestamp Time
     * @return The String with the time
     */
    public static String time2readableString(FineTime timestamp) {
        Date date = new Date(timestamp.getValue() / ONE_MILLION);
        Format format = new SimpleDateFormat(DATE_PATTERN);
        return format.format(date);
    }

    /**
     * Converts a Time MAL data type timestamp into a readable string
     *
     * @param timestamp Time
     * @return The String with the time
     */
    public static String time2readableString(Time timestamp) {
        Date date = new Date(timestamp.getValue());
        Format format = new SimpleDateFormat(DATE_PATTERN);
        return format.format(date);
    }

    /**
     * Returns the current time encapsulated in a FineTime type
     *
     * @return The current time
     */
    public static FineTime getTimestamp() {
        // Convert from milliseconds (10^-3) to nanoseconds (10^-9)
        return new FineTime(System.currentTimeMillis() * ONE_MILLION);
    }

    /**
     * Returns the current time encapsulated in a Time type
     *
     * @return The current time
     */
    public static Time getTimestampMillis() {
        return new Time(System.currentTimeMillis());
    }

    public static long fromMilliToNano(long milli) {
        return (milli * ONE_MILLION);
    }

    public static long fromNanoToMilli(long nano) {
        return (nano / ONE_MILLION);
    }

    public static FineTime timeToFineTime(final Time time) {
        return (time == null) ? null : new FineTime(HelperTime.fromMilliToNano(time.getValue()));
    }

    public static Time fineTimeToTime(final FineTime fineTime) {
        return (fineTime == null) ? null : new Time(HelperTime.fromNanoToMilli(fineTime.getValue()));
    }

    /**
     * Returns just the fractional part of a time value that is represented in
     * nanoseconds. So, for 12.34567890123 seconds, we would expect to get the
     * integer: 567890123 nanoseconds
     *
     * @param nano The time in nanoseconds
     * @return The fractional part of time in nanoseconds
     */
    public static int getFractionalPart(long nano) {
        return ((int) ((nano % 1000000000)));
    }

    /**
     * Remove the milliseconds component from the timestamp, then converts to
     * nano and adds the missing nano fractional part.
     *
     * @param timestamp The timestamp in SQL timestamp
     * @return The time in nanoseconds
     */
    public static long getNanosecondsFromSQLTimestamp(java.sql.Timestamp timestamp) {
        return (HelperTime.fromMilliToNano(timestamp.getTime() - (timestamp.getTime() % 1000)) + timestamp.getNanos());
    }

}
