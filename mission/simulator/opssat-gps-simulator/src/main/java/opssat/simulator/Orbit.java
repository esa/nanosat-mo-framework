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
package opssat.simulator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Orbit class represents an orbit using the classic orbital elements.
 *
 * @author Cesar Coelho
 */
public class Orbit {

    //Earth Radius (units: km)
    public static final double EARTH_RADIUS = 6371;
    //gravitational constant (units: km^3 kg^-1 s^-2)
    private static final double G = 6.67384E-20;
    //Earth Mass (units: kg)
    private static final double EARTH_MASS = 5.97219E24;
    //1 solar day = 1.00273(...) sideral days
    private static final double SOLAR_DAY = 1.002737909350795;
    // time_int: interval of time passed
    private double J2 = 1.08262668E-3;
    // Local Sideral Time during Epoch (16:36:17) from: http://www.jgiesen.de/astro/astroJS/siderealClock/
    private double lst_epoch = (16 * 3600 + 36 * 60 + 17) * 2 * Math.PI / (3600 * 24);

    private final double a;
    private final double i;
    private final double true_anomaly;
    private final double e; // Eccentricity
    private final double Period; // Period
    private final double init_raan;
    private final double init_arg_perigee;

    private double raan;
    private double arg_per;
    private double n; //Mean motion
    private double M_anom; //Mean Anomaly

    //Unit Vectors: P, Q
    private Vector P;
    private Vector Q;

    // Vector Position
    private Vector r;
    // Vector Vevolicty
    private Vector v;

    //Epoch time: '2003 June 01 - 00:00:00'
    public static final String DATEFORMATSTRING = "yyyy:MM:dd HH:mm:ss z";
    private static final String EPOCH_DEFAULT = "2003:06:01 00:00:00 CEST";
    private Date epoch;

    /**
     * Constructor.
     *
     * @param a The semi-major axis.
     * @param i The inclination of the orbit.
     * @param raan The right ascension of the ascending node.
     * @param arg_per The argument of perigee.
     * @param eccentricity The eccentricity o the orbit.
     * @param true_anomaly The true anomaly.
     * @param initialEpoch The initial epoch.
     */
    public Orbit(double a, double i, double raan, double arg_per,
            double eccentricity, double true_anomaly, String initialEpoch) {
        SimpleDateFormat df = new SimpleDateFormat(DATEFORMATSTRING);//DateFormat.getInstance();    
        df.setLenient(true);
        try {
            epoch = df.parse(initialEpoch);
        } catch (ParseException ex) {
            Logger.getLogger(Orbit.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.a = a;
        this.i = i;
        this.init_raan = raan;
        this.init_arg_perigee = arg_per;
        this.true_anomaly = true_anomaly;
        this.e = eccentricity;
        this.Period = 2 * Math.PI * Math.sqrt(Math.pow(a, 3) / (G * EARTH_MASS));
        this.calculateMeanMotion();
    }

    /**
     * Constructor.
     *
     * @param a The semi-major axis.
     * @param i The inclination of the orbit.
     * @param raan The right ascension of the ascending node.
     * @param arg_per The argument of perigee.
     * @param eccentricity The eccentricity o the orbit.
     * @param true_anomaly The true anomaly.
     */
    public Orbit(double a, double i, double raan, double arg_per, double eccentricity, double true_anomaly) {
        this(a, i, raan, arg_per, eccentricity, true_anomaly, EPOCH_DEFAULT);
    }

    public OrbitParameters getParametersForLatestDate() {
        return getParametersForDate(new Date());
    }

    public OrbitParameters getParametersForDate(Date time) {
        this.M_anom = calculateMeanAnomaly(time);
        double time_int = getEpochDiffSec(time);
        this.raan = this.init_raan + calculateRAANPrecession(time_int);
        this.arg_per = this.init_arg_perigee + calculatePerigeePrecession(time_int);

        // Compute the Local Sideral Time
        double delta_lst = lst_epoch + SOLAR_DAY * 2 * Math.PI * (time_int) / (24 * 3600);

        //System.out.printf("RAAN: %f\nPeriod: %f\nTime int: %f\n\n", this.RAAN, Period, time_int );
        // Generate vectors
        this.calculateUnitVectors();
        this.calculateCartesianVector();

        double newA = this.r.length();
        double lat = Math.asin(this.r.z() / newA);
        double lon = this.normalizeAngle(Math.atan2(this.r.y(), this.r.x()) - delta_lst);
        //System.out.println("lat:["+lat+"] lon:["+lon+"]");

        return new OrbitParameters(
                lat * (180 / Math.PI),
                lon * (180 / Math.PI),
                newA,
                this.v,
                time);
    }

    // Calculates ascending node change (RAAN)
    private double calculateRAANPrecession(double time_int) {
        return -3 * Math.PI * J2 * (EARTH_RADIUS * EARTH_RADIUS)
                / (Math.pow(a * (1 - e * e), 2)) * Math.cos(i)
                * (time_int / this.Period);
    }

    // Calculates Nodal Precession
    private double calculatePerigeePrecession(double time_int) {
        return -(3 * Math.PI * J2 * EARTH_RADIUS * EARTH_RADIUS
                / (2 * Math.pow(a * (1 - e * e), 2)))
                * (5 * Math.pow(Math.sin(i), 2) - 4)
                * (time_int / this.Period);
    }

    // Calculates Mean Anomaly: M
    private double calculateMeanAnomaly(Date time) {
        // t: is the time since epoch
        double t = getEpochDiffSec(time);
        return (this.n * t);
    }

    private double getEpochDiffSec(Date now) {
        return (now.getTime() - epoch.getTime()) / 1000.0;
    }
    // Calculates Mean motion variable: n

    private void calculateMeanMotion() {
        this.n = Math.sqrt(G * EARTH_MASS / (Math.pow(this.a, 3)));
    }

    // Calculates both unit vectors: P, Q
    private void calculateUnitVectors() {
        this.P = new Vector(Math.cos(this.arg_per) * Math.cos(this.raan) - Math.sin(this.arg_per) * Math.cos(this.i) * Math.sin(this.raan),
                Math.cos(this.arg_per) * Math.sin(this.raan) + Math.sin(this.arg_per) * Math.cos(this.i) * Math.cos(this.raan),
                Math.sin(this.arg_per) * Math.sin(this.i));

        this.Q = new Vector(-Math.sin(this.arg_per) * Math.cos(this.raan) - Math.cos(this.arg_per) * Math.cos(this.i)
                * Math.sin(this.raan),
                -Math.sin(this.arg_per) * Math.sin(this.raan) + Math.cos(this.arg_per) * Math.cos(this.i) * Math.cos(this.raan),
                Math.sin(this.i) * Math.cos(this.arg_per));
    }

    // Calculates the vector r
    private void calculateCartesianVector() {
        //Calculating Position vector: r
        Vector part1 = this.P.times(this.a * (Math.cos(this.M_anom) - this.e));
        Vector part2 = this.Q.times(this.a * Math.sqrt(1 - Math.pow(this.e, 2)) * (Math.sin(this.M_anom)));
        this.r = part1.add(part2);

        //Calculating Velocity vector: v
        double E_dot = Math.sqrt(G * EARTH_MASS / Math.pow(a, 3)) / (1 - this.e * Math.cos(M_anom));
        Vector part3 = this.P.times(-this.a * Math.sin(this.M_anom) * E_dot);
        Vector part4 = this.Q.times(this.a * Math.sqrt(1 - Math.pow(this.e, 2)) * (Math.cos(this.M_anom)) * E_dot);
        this.v = part3.add(part4);
    }

    private double normalizeAngle(double angle) {
        double newAngle = angle;
        while (newAngle <= -Math.PI) {
            newAngle += 2 * Math.PI;
        }
        while (newAngle > Math.PI) {
            newAngle -= 2 * Math.PI;
        }
        return newAngle;
    }
}
