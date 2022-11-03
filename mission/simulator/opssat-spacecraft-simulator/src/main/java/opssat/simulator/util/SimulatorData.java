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

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Cezar Suteu
 */
public class SimulatorData implements Serializable {
	


    /**
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(final Date date1, final Date date2, final TimeUnit timeUnit) {
        final long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }
    public static Map<TimeUnit,Long> computeTimeUnit(final long date1)
    {
        final List<TimeUnit> units = new ArrayList<>(EnumSet.allOf(TimeUnit.class));
        Collections.reverse(units);
        final Map<TimeUnit,Long> result = new LinkedHashMap<>();
        long milliesRest = date1;
        for ( final TimeUnit unit : units ) {
            final long diff = unit.convert(milliesRest,TimeUnit.MILLISECONDS);
            final long diffInMilliesForUnit = unit.toMillis(diff);
            milliesRest = milliesRest - diffInMilliesForUnit;
            result.put(unit,diff);
        }
        return result; 
    }
    public static Map<TimeUnit,Long> computeDiff(final Date date1, final Date date2) {
        final long diffInMillies = date2.getTime() - date1.getTime();
        final List<TimeUnit> units = new ArrayList<>(EnumSet.allOf(TimeUnit.class));
        Collections.reverse(units);
        final Map<TimeUnit,Long> result = new LinkedHashMap<>();
        long milliesRest = diffInMillies;
        for ( final TimeUnit unit : units ) {
            final long diff = unit.convert(milliesRest,TimeUnit.MILLISECONDS);
            final long diffInMilliesForUnit = unit.toMillis(diff);
            milliesRest = milliesRest - diffInMilliesForUnit;
            result.put(unit,diff);
        }
        return result;
    }
    private int timeFactor = 1;
    private int counter;
    private int methodsExecuted;
    private Date currentTime;   
    private boolean timeRunning;
    private boolean simulatorRunning;
    private long currentTimeLong;
    private long utcOffsetInMillis = -18000;

    public void setMethodsExecuted(final int methodsExecuted) {
        this.methodsExecuted = methodsExecuted;
    }

    @Override
    public String toString() {
        return "{" + "counter=" + counter + ", methodsExecuted=" + methodsExecuted + ", currentTime=" + currentTime + '}';
    }

    public void incrementMethods() {
        methodsExecuted++;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(final int counter) {
        this.counter = counter;
    }

    public SimulatorData(final int counter) {
        this.counter = counter;
        this.currentTime = new Date();
        this.simulatorRunning = true;
    }

    public void feedTimeElapsed(final long timeElapsed) {
        this.currentTime.setTime(this.currentTime.getTime() + timeElapsed * timeFactor);
        this.currentTimeLong=this.currentTimeLong+timeElapsed;
    }

    public SimulatorData(final int counter, final Date currentTime) {
        this.counter = counter;
        this.currentTime = currentTime;
    }

    public boolean isTimeRunning() {
        return timeRunning;
    }

    public void toggleTimeRunning() {
        this.timeRunning = !this.timeRunning;
    }

    public boolean isSimulatorRunning() {
        return simulatorRunning;
    }

    public void toggleSimulatorRunning() {
        this.simulatorRunning = !this.simulatorRunning;
    }

    public void initFromHeader(final SimulatorHeader data) {
        this.simulatorRunning = data.isAutoStartSystem();
        this.timeRunning = data.isAutoStartTime();
        this.timeFactor = data.getTimeFactor();
        this.currentTime = new Date(data.getStartDate().getTime());
        this.currentTimeLong=0;
    }

    public int getTimeFactor() {
        return timeFactor;
    }

    public void setTimeFactor(final int timeFactor) {
        this.timeFactor = timeFactor;
    }

    public Date getCurrentTime() {
        return currentTime;
    }
    
    public long getCurrentTimeMillis() {
        return currentTimeLong;
    }
    public String getUTCCurrentTime() {
        final DateFormat df = new SimpleDateFormat("HHmmss.SS");
        return df.format(currentTime);
    }
    public String getUTCCurrentTime2() {
        final DateFormat df = new SimpleDateFormat("HHmm");
        return df.format(currentTime);
    }
    public String getUTCCurrentHour() {
        final DateFormat df = new SimpleDateFormat("H");
        return df.format(currentTime);
    }
    public String getUTCCurrentMinute() {
        final DateFormat df = new SimpleDateFormat("m");
        return df.format(currentTime);
    }
    public String getUTCCurrentSecond() {
        final DateFormat df = new SimpleDateFormat("s");
        return df.format(currentTime);
    }
    public String getUTCCurrentMillis() {
        final DateFormat df = new SimpleDateFormat("SSS");
        return df.format(currentTime);
    }
    public String getCurrentDay() {
        final DateFormat df = new SimpleDateFormat("dd");
        return df.format(currentTime);
    }
    public String getCurrentMonth() {
        final DateFormat df = new SimpleDateFormat("MM");
        return df.format(currentTime);
    }
    public String getCurrentYear() {
        final DateFormat df = new SimpleDateFormat("yyyy");
        return df.format(currentTime);
    }

    public long getUtcOffsetInMillis(){
        return utcOffsetInMillis;
    }
    
}
