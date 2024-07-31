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
package opssat.simulator.celestia;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import opssat.simulator.util.DateExtraction;

/**
 *
 * @author Cezar Suteu
 */
public class CelestiaData implements Serializable {
    public static final String DATE_FORMAT = "yyyy/MM/dd-HH:mm:ss";

    float[] rv;
    float[] q;
    Date date;
    String anx, dnx, aos, los, info;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getAnx() {
        return anx;
    }

    public void setAnx(String anx) {
        this.anx = anx;
    }

    public String getDnx() {
        return dnx;
    }

    public void setDnx(String dnx) {
        this.dnx = dnx;
    }

    public String getAos() {
        return aos;
    }

    public void setAos(String aos) {
        this.aos = aos;
    }

    public String getLos() {
        return los;
    }

    public void setLos(String los) {
        this.los = los;
    }

    public CelestiaData(float[] rv, float[] q) {
        this.rv = rv;
        this.q = q;
    }

    public int getYears() {
        return DateExtraction.getYearFromDate(this.date);
    }

    public int getMonths() {
        return DateExtraction.getMonthFromDate(this.date);
    }

    public int getDays() {
        return DateExtraction.getDayFromDate(this.date);
    }

    public int getHours() {
        return DateExtraction.getHourFromDate(this.date);
    }

    public int getMinutes() {
        return DateExtraction.getMinuteFromDate(this.date);
    }

    public int getSeconds() {
        return DateExtraction.getSecondsFromDate(this.date);
    }

    public String getDate() {
        return new SimpleDateFormat(DATE_FORMAT).format(this.date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDate(String date) {
        try {
            this.date = (new SimpleDateFormat(DATE_FORMAT)).parse(date);
        } catch (ParseException ex) {
            Logger.getLogger(CelestiaData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public float[] getRv() {
        return rv;
    }

    public void setRv(float[] rv) {
        this.rv = rv;
    }

    public float[] getQ() {
        return q;
    }

    public void setQ(float[] q) {
        this.q = q;
    }

    @Override
    public String toString() {
        return "CelestiaData{" + "rv=" + "[" + rv[0] + "," + rv[1] + "," + rv[2] + "," + rv[3] + "," + rv[4] + "," +
            rv[5] + "]" + ", q0=" + q[0] + ",q1=" + q[1] + ",q2=" + q[2] + ",q3=" + q[3] + '}';
    }

}
