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
package opssat.simulator.orekit;

import java.util.Locale;

/**
 *
 * @author cezar
 */
public class GPSSatInView {
    String name;
    double distance;
    private double azimuth;
    private double elevation;
    private int prn;

    public int getPrn() {
        return prn;
    }

    public void setPrn(int prn) {
        this.prn = prn;
    }

    public void setAzimuth(double azimuth) {
        this.azimuth = azimuth;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public double getAzimuth() {
        return azimuth;
    }

    public double getElevation() {
        return elevation;
    }

    public GPSSatInView(String name, double distance) {
        this.name = name;
        this.distance = distance;
        String prn = name.substring(name.indexOf("(") + 1, name.indexOf(")"));
        String[] prnWords = prn.split(" ");
        this.prn = Integer.parseInt(prnWords[1]);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "GPSSatInView{" + "name=" + name + ", distance=" + String.format(Locale.ROOT, "%.2f", distance /
            1000.0) + "[km], azimuth=" + String.format(Locale.ROOT, "%.2f", azimuth) + "[deg], elevation=" + String
                .format(Locale.ROOT, "%.2f", elevation) + "[deg]}";
    }

}
