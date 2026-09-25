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

import java.text.DecimalFormat;
import java.util.Locale;
import org.hipparchus.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author Cezar Suteu
 */
public class SimulatorSpacecraftState {
    private double latitude, longitude, altitude;
    private float[] rv;//[x, y, z, vx, vy, vz]
    private float[] q;// [qs, q1, q2, q3]
    private float[] magField;//[x,y,z]
    private float[] magnetometer;//[x,y,z]
    private double[] sunVector;//[x,y,z]
    private int satsInView;
    private String modeOperation;

    public String getModeOperation() {
        return modeOperation;
    }

    public void setModeOperation(String modeOperation) {
        this.modeOperation = modeOperation;
    }

    public int getSatsInView() {
        return satsInView;
    }

    public void setSatsInView(int satsInView) {
        this.satsInView = satsInView;
    }

    private double[][] rotation;

    public float[] getR() {
        float[] result = new float[3];
        result[0] = rv[0];
        result[1] = rv[1];
        result[2] = rv[2];
        return result;
    }

    public float[] getV() {
        float[] result = new float[3];
        result[0] = rv[3];
        result[1] = rv[4];
        result[2] = rv[5];
        return result;
    }

    public float[] getRv() {
        return rv;
    }

    public void setRv(float[] rv) {
        this.rv = rv;
    }

    public void setSunVector(double[] sunVector) {
        this.sunVector = sunVector;
    }

    public void setMagField(double[] magField) {
        int i = 0;
        for (double d : magField) {
            this.magField[i++] = (float) d;
        }
    }

    private String formatVector(String[] labels, float[] vector, String units) {
        DecimalFormat formatter = new DecimalFormat("+#00000.00;-#00000.00");
        return labels[0] + ":[" + formatter.format(vector[0]) + "] ; " + labels[1] + ":[" + formatter.format(
            vector[1]) + "] ; " + labels[2] + ": [" + formatter.format(vector[2]) + "] units " + units;
    }

    private String formatVector(String[] labels, double[] vector, String units) {
        DecimalFormat formatter = new DecimalFormat("+#00000.00;-#00000.00");
        return labels[0] + ":[" + formatter.format(vector[0]) + "] ; " + labels[1] + ":[" + formatter.format(
            vector[1]) + "] ; " + labels[2] + ": [" + formatter.format(vector[2]) + "] units " + units;
    }

    private String formatVectorDecimalsOnly(String[] labels, double[] vector, String units) {
        DecimalFormat formatter = new DecimalFormat("+#.00000;-#.00000");
        return labels[0] + ":[" + formatter.format(vector[0]) + "] ; " + labels[1] + ":[" + formatter.format(
            vector[1]) + "] ; " + labels[2] + ": [" + formatter.format(vector[2]) + "] units " + units;
    }

    public String getMagField() {
        String[] labels = {"North", "East", "Vertical"};
        return formatVector(labels, this.magField, "[nT]");//"North(X):["+formatter.format(magField[0])+"] ; East(Y):["+formatter.format(magField[1])+"] ; Vertical(Z): ["+formatter.format(magField[2])+"] units [nT]";
    }

    public float[] getMagnetometer() {
        return magnetometer;
    }

    public double[] getSunVector() {
        return sunVector;
    }

    public String getSunVectorAsString() {
        String[] labels = {"X", "Y", "Z"};
        return formatVectorDecimalsOnly(labels, this.sunVector, "[N/A]");
    }

    public String getMagnetometerAsString() {
        String[] labels = {"X", "Y", "Z"};
        return formatVector(labels, this.magnetometer, "[nT]");
        //return magnetometer[0]+";"+magnetometer[1]+";"+magnetometer[2]+"[nT]";
    }

    public void setMagnetometer(double[] magnetometer) {
        int i = 0;
        for (double d : magnetometer) {
            this.magnetometer[i++] = (float) d;
        }
    }

    public void setRv(Vector3D r, Vector3D v) {
        this.rv[0] = (float) (r.getX() / 1000.0);
        this.rv[1] = (float) (r.getY() / 1000.0);
        this.rv[2] = (float) (r.getZ() / 1000.0);
        this.rv[3] = (float) (v.getX() / 1000.0);
        this.rv[4] = (float) (v.getY() / 1000.0);
        this.rv[5] = (float) (v.getZ() / 1000.0);
    }

    public float[] getQ() {
        return q;
    }

    public void setQ(float[] q) {
        this.q = q;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public void setRotation(double[][] rotation) {
        this.rotation = rotation;
    }

    public SimulatorSpacecraftState(double latitude, double longitude, double altitude) {
        this.rv = new float[6];
        this.q = new float[4];
        this.magField = new float[3];
        this.magnetometer = new float[3];
        this.sunVector = new double[3];
        this.rotation = new double[3][3];
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    @Override
    public String toString() {
        return "SimulatorSpacecraftState{" + "latitude=" + latitude + ", longitude=" + longitude + ", altitude=" +
            altitude + '}';
    }

    public String getRotationAsString() {
        return "\n" + String.format(Locale.ROOT, "%+03f", this.rotation[0][0]) + " " + String.format(Locale.ROOT,
            "%+03f", this.rotation[0][1]) + " " + String.format(Locale.ROOT, "%+03f", this.rotation[0][2]) + "\n" +
            String.format(Locale.ROOT, "%+03f", this.rotation[1][0]) + " " + String.format(Locale.ROOT, "%+03f",
                this.rotation[1][1]) + " " + String.format(Locale.ROOT, "%+03f", this.rotation[1][2]) + "\n" + String
                    .format(Locale.ROOT, "%+03f", this.rotation[2][0]) + " " + String.format(Locale.ROOT, "%+03f",
                        this.rotation[2][1]) + " " + String.format(Locale.ROOT, "%+03f", this.rotation[2][2]);
    }

}
