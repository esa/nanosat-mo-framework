/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2021      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 *  You may not use this file except in compliance with the License.
 * 
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *  
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 *  ----------------------------------------------------------------------------
 */
package opssat.simulator.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author Cezar Suteu
 */
public class DateExtraction {
    public static int getYearFromDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return Integer.parseInt(dateFormat.format(date));
    }

    public static int getMonthFromDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return Integer.parseInt(dateFormat.format(date));
    }

    public static int getDayFromDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return Integer.parseInt(dateFormat.format(date));
    }

    public static int getHourFromDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return Integer.parseInt(dateFormat.format(date));
    }

    public static int getMinuteFromDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return Integer.parseInt(dateFormat.format(date));
    }

    public static int getSecondsFromDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return Integer.parseInt(dateFormat.format(date));
    }
}
