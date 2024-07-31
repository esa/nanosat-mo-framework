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
package esa.mo.platform.impl.util;

import java.io.IOException;
import org.ccsds.moims.mo.platform.gps.structures.Position;

/**
 * This class calculates the distance between 2 points on a based on their
 * latitude, longitude and altitude on Earth.
 */
public class PositionsCalculator {

    public static final double R = 6371; // Equatorial Radius
    public static final double f = 0.00335281066474748071984552861852; // Flattening

    public static class ECEFVector {

        public final double x;
        public final double y;
        public final double z;

        public ECEFVector(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    /**
     * Calculates the distance between 2 Positions. If one of the input
     * Positions contains a 0, then the altitude won't be considered when
     * calculating the distance.
     *
     * @param p1 Position 1
     * @param p2 Position 2
     * @return
     * @throws IOException
     */
    public static double deltaDistanceFrom2Points(Position p1, Position p2) throws IOException {
        if (p1 == null || p2 == null) {
            throw new IOException("Not a valid position. Neither p1 nor p2 can be null");
        }

        ECEFVector p1ECEF = PositionsCalculator.LLA2ECEF(p1);
        ECEFVector p2ECEF = PositionsCalculator.LLA2ECEF(p2);

        double dx = Math.abs(p1ECEF.x - p2ECEF.x);
        double dy = Math.abs(p1ECEF.y - p2ECEF.y);
        double dz = Math.abs(p1ECEF.z - p2ECEF.z);

        if (p1.getAltitude() == 0 || p2.getAltitude() == 0) {
            dz = 0;
        }

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static ECEFVector LLA2ECEF(Position p) {
        // Information taken from the website:
        // http://nl.mathworks.com/help/aeroblks/llatoecefposition.html?requestedDomain=nl.mathworks.com

        double lat = p.getLatitude();
        double lon = p.getLongitude();
        double alt = p.getAltitude();

        double meanSeaLevel = Math.atan((1 - f) * (1 - f) * Math.tan(lat));
        double rs = Math.sqrt(R * R / ((1 + (1 / ((1 - f) * (1 - f)) - 1) * Math.sin(meanSeaLevel) * Math.sin(
            meanSeaLevel))));

        return new ECEFVector(rs * Math.cos(meanSeaLevel) * Math.cos(lon) + alt * Math.cos(lat) * Math.cos(lon), rs *
            Math.cos(meanSeaLevel) * Math.sin(lon) + alt * Math.cos(lat) * Math.sin(lon), rs * Math.sin(meanSeaLevel) +
                alt * Math.sin(lat));
    }

}
