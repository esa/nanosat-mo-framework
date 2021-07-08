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
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }
    public static Map<TimeUnit,Long> computeTimeUnit(long date1)
    {
        List<TimeUnit> units = new ArrayList<TimeUnit>(EnumSet.allOf(TimeUnit.class));
        Collections.reverse(units);
        Map<TimeUnit,Long> result = new LinkedHashMap<TimeUnit,Long>();
        long milliesRest = date1;
        for ( TimeUnit unit : units ) {
            long diff = unit.convert(milliesRest,TimeUnit.MILLISECONDS);
            long diffInMilliesForUnit = unit.toMillis(diff);
            milliesRest = milliesRest - diffInMilliesForUnit;
            result.put(unit,diff);
        }
        return result; 
    }
    public static Map<TimeUnit,Long> computeDiff(Date date1, Date date2) {
        long diffInMillies = date2.getTime() - date1.getTime();
        List<TimeUnit> units = new ArrayList<>(EnumSet.allOf(TimeUnit.class));
        Collections.reverse(units);
        Map<TimeUnit,Long> result = new LinkedHashMap<>();
        long milliesRest = diffInMillies;
        for ( TimeUnit unit : units ) {
            long diff = unit.convert(milliesRest,TimeUnit.MILLISECONDS);
            long diffInMilliesForUnit = unit.toMillis(diff);
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

    public void setMethodsExecuted(int methodsExecuted) {
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

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public SimulatorData(int counter) {
        this.counter = counter;
        this.currentTime = new Date();
        this.simulatorRunning = true;
    }

    public void feedTimeElapsed(long timeElapsed) {
        this.currentTime.setTime(this.currentTime.getTime() + timeElapsed * timeFactor);
        this.currentTimeLong=this.currentTimeLong+timeElapsed;
    }

    public SimulatorData(int counter, Date currentTime) {
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

    public void initFromHeader(SimulatorHeader data) {
        this.simulatorRunning = data.isAutoStartSystem();
        this.timeRunning = data.isAutoStartTime();
        this.timeFactor = data.getTimeFactor();
        this.currentTime = new Date(data.getStartDate().getTime());
        this.currentTimeLong=0;
    }

    public int getTimeFactor() {
        return timeFactor;
    }

    public void setTimeFactor(int timeFactor) {
        this.timeFactor = timeFactor;
    }

    public Date getCurrentTime() {
        return currentTime;
    }
    
    public long getCurrentTimeMillis() {
        return currentTimeLong;
    }
    public String getUTCCurrentTime() {
        DateFormat df = new SimpleDateFormat("hhmmss.SS");
        return df.format(currentTime);
    }
    public String getUTCCurrentTime2() {
        DateFormat df = new SimpleDateFormat("hhmm");
        return df.format(currentTime);
    }
    public String getCurrentDay() {
        DateFormat df = new SimpleDateFormat("dd");
        return df.format(currentTime);
    }
    public String getCurrentMonth() {
        DateFormat df = new SimpleDateFormat("MM");
        return df.format(currentTime);
    }
    public String getCurrentYear() {
        DateFormat df = new SimpleDateFormat("yyyy");
        return df.format(currentTime);
    }
    
}
