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

    public static String convert(final long timestamp) {
        final Date date = new Date(timestamp);
        final SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(date);
    }

    public static FineTime convert(final String timeText) {
        FineTime fineTime = null;
        final SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            final long millis = format.parse(timeText).getTime();
            fineTime = new FineTime(millis * 1000000);
        } catch (final ParseException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
        return fineTime;
    }
}
