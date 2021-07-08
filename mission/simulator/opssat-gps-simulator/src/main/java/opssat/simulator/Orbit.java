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


//import java.time.LocalDateTime;
//import java.time.Month;
//import java.time.ZoneOffset;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cesar Coelho
 */
public class Orbit {

    private final double a;
    private final double i;
    private final double true_anomaly;
    private final double e; // Eccentricity
    private final double Period; // Period
    private final double RAAN_init;
    private final double arg_per_init;

    private double RAAN;
    private double arg_per;
    private double n; //Mean motion
    private double M_anom; //Mean Anomaly
    private Date time;

    //Unit Vectors: P, Q
    private Vector P;
    private Vector Q;

    // Vector Position
    private Vector r;
    // Vector Vevolicty
    private Vector v;

//gravitational constant (units: km^3 kg^-1 s^-2)
    private static final double G = 6.67384E-20;
//Earth Mass (units: kg)
    private static final double M_e = 5.97219E24;
//Earth Radius (units: km)
    private static final double R_e = 6371;
//1 solar day = 1.00273(...) sideral days
    private static final double SOLAR_DAY = 1.002737909350795;

//Epoch time: '2003 June 01 - 00:00:00'
    public static String DATEFORMATSTRING = "yyyy:MM:dd HH:mm:ss z";
    private static String EPOCH_INITIAL="2003:06:01 00:00:00 CEST";
    private Date Epoch;

    public static class OrbitParameters {

        private final double longitude;
        private final double latitude;
        private final double a;
        private final Date time;
        private final Vector velocity;

        /**
         *
         * @param latitude
         * @param longitude
         * @param a Semi-major axis in Km
         * @param velocity
         * @param time
         */
        public OrbitParameters(double latitude, double longitude, double a, Vector velocity, Date time) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.a = a;
            this.velocity = velocity;
            this.time = time;
        }

        public double getlongitude() {
            return longitude;
        }

        public double getlatitude() {
            return latitude;
        }

        public double geta() {
            return a;
        }

        public double getGPSaltitude() {
            return a - R_e;
        }

        public Date gettime() {
            return time;
        }

        public Vector getvelocity() {
            return velocity;
        }
    }
    public Orbit(double a, double i, double RAAN, double arg_per, double true_anomaly)
    {
        this(a,i,RAAN,arg_per,true_anomaly,EPOCH_INITIAL);
    }
    public Orbit(double a, double i, double RAAN, double arg_per, double true_anomaly, String initialEpoch) {
        SimpleDateFormat df = new SimpleDateFormat(DATEFORMATSTRING);//DateFormat.getInstance();    
        df.setLenient(true);
        try {
            Epoch = df.parse(initialEpoch);
        } catch (ParseException ex) {
            Logger.getLogger(Orbit.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.a = a;
        this.i = i;
        this.RAAN_init = RAAN;
        this.arg_per_init = arg_per;
        this.true_anomaly = true_anomaly;
        //Eccentricity set to zero:
        this.e = 0;

        this.Period = 2 * Math.PI * Math.sqrt(Math.pow(a, 3) / (G * M_e));
        this.calculateMeanMotion();

        this.time = new Date();
        double time_int = getEpochDiffSec(time);
        //time.toEpochSecond(ZoneOffset.UTC) - this.Epoch.toEpochSecond(ZoneOffset.UTC);
        this.RAAN = this.RAAN_init + calculateRAANPrecession(time_int);
        this.arg_per = this.arg_per_init + calculatePerigeePrecession(time_int);

    }
    // Returns Longitude, Latitude, Altitude

    public OrbitParameters getParameters() {
        return getParameters(new Date());
    }
    
    
    public OrbitParameters getParameters(Date currentTime) {

        this.time = currentTime;
        this.M_anom = calculateMeanAnomaly(this.time);

        double time_int = getEpochDiffSec(time);
        //System.out.println(time_int);
        this.RAAN = this.RAAN_init + calculateRAANPrecession(time_int);
        this.arg_per = this.arg_per_init + calculatePerigeePrecession(time_int);

        // Local Sideral Time during Epoch (16:36:17) from: http://www.jgiesen.de/astro/astroJS/siderealClock/
        double lst_epoch = (16 * 3600 + 36 * 60 + 17) * 2 * Math.PI / (3600 * 24);
        // Compute the Local Sideral Time
        double delta_lst = lst_epoch + SOLAR_DAY * 2 * Math.PI * (time_int) / (24 * 3600);

        //System.out.printf("RAAN: %f\nPeriod: %f\nTime int: %f\n\n", this.RAAN, Period, time_int );
        // Generate vectors
        this.calculateUnitVectors();
        this.calculateCartesianVector();

        double lat = Math.asin(this.r.z() / this.r.length());
        double lon = this.normalizeAngle(Math.atan2(this.r.y(), this.r.x()) - delta_lst);
        //System.out.println("lat:["+lat+"] lon:["+lon+"]");

        return new OrbitParameters(lat * (180 / Math.PI), lon * (180 / Math.PI),
            this.r.length(), this.v, this.time);
    }

    // Calculates ascending node change (RAAN)
    private double calculateRAANPrecession(double time_int) {
        // time_int: interval of time passed
        double J2 = 1.08262668E-3;

        return -3 * Math.PI * J2 * (R_e * R_e)
            / (Math.pow(a * (1 - e * e), 2))
            * Math.cos(i)
            * (time_int / this.Period);
    }

    // Calculates Nodal Precession
    private double calculatePerigeePrecession(double time_int) {
        // time_int: interval of time passed
        double J2 = 1.08262668E-3;

        return -(3 * Math.PI * J2 * R_e * R_e / (2 * Math.pow(a * (1 - e * e), 2)))
            * (5 * Math.pow(Math.sin(i), 2) - 4)
            * (time_int / this.Period);
    }

    // Calculates Mean Anomaly: M
    private double calculateMeanAnomaly(Date now) {
        // t: is the time since epoch
        double t = getEpochDiffSec(now);
        return (this.n * t);
    }

    private double getEpochDiffSec(Date now) {
        return (now.getTime() - Epoch.getTime()) / 1000.0;
    }
    // Calculates Mean motion variable: n

    private void calculateMeanMotion() {
        this.n = Math.sqrt(G * M_e / (Math.pow(this.a, 3)));
    }

    // Calculates both unit vectors: P, Q
    private void calculateUnitVectors() {
        this.P = new Vector(
            Math.cos(this.arg_per) * Math.cos(this.RAAN) - Math.sin(this.arg_per) * Math.cos(this.i) * Math.sin(this.RAAN),
            Math.cos(this.arg_per) * Math.sin(this.RAAN) + Math.sin(this.arg_per) * Math.cos(this.i) * Math.cos(this.RAAN),
            Math.sin(this.arg_per) * Math.sin(this.i)
        );

        this.Q = new Vector(
            -Math.sin(this.arg_per) * Math.cos(this.RAAN) - Math.cos(this.arg_per) * Math.cos(this.i) * Math.sin(this.RAAN),
            -Math.sin(this.arg_per) * Math.sin(this.RAAN) + Math.cos(this.arg_per) * Math.cos(this.i) * Math.cos(this.RAAN),
            Math.sin(this.i) * Math.cos(this.arg_per)
        );
    }

    // Calculates the vector r
    private void calculateCartesianVector() {
        //Calculating Position vector: r
        Vector part1 = this.P.times(this.a * (Math.cos(this.M_anom) - this.e));
        Vector part2 = this.Q.times(this.a * Math.sqrt(1 - Math.pow(this.e, 2)) * (Math.sin(this.M_anom)));
        this.r = part1.add(part2);

        //Calculating Velocity vector: v
        double E_dot = Math.sqrt(G * M_e / Math.pow(a, 3)) / (1 - this.e * Math.cos(M_anom));
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
