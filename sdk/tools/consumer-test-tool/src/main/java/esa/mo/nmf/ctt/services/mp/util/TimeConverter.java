package esa.mo.nmf.ctt.services.mp.util;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.FineTime;

public class TimeConverter {

    private static final Logger LOGGER = Logger.getLogger(TimeConverter.class.getName());

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss z";

    private TimeConverter() {}

    public static String convert(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(date);
    }

    public static FineTime convert(String timeText) {
        FineTime fineTime = null;
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            long millis = format.parse(timeText).getTime();
            fineTime = new FineTime(millis * 1000000);
        } catch (ParseException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
        return fineTime;
    }
}
