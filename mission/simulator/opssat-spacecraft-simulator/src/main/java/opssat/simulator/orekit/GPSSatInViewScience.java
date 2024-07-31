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

/**
 *
 * @author cezar
 */
public class GPSSatInViewScience {
    double minDistance;
    double maxDistance;
    double minElevation;
    double maxElevation;
    double avgDistance;
    double avgElevation;
    double stdDevDistance;
    double stdDevElevation;

    public double getMinDistance() {
        return minDistance;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public double getMinElevation() {
        return minElevation;
    }

    public double getMaxElevation() {
        return maxElevation;
    }

    public double getAvgDistance() {
        return avgDistance;
    }

    public double getAvgElevation() {
        return avgElevation;
    }

    public double getStdDevDistance() {
        return stdDevDistance;
    }

    public double getStdDevElevation() {
        return stdDevElevation;
    }

    public GPSSatInViewScience(double minDistance, double maxDistance, double minElevation, double maxElevation,
        double avgDistance, double avgElevation, double stdDevDistance, double stdDevElevation) {
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.minElevation = minElevation;
        this.maxElevation = maxElevation;
        this.avgDistance = avgDistance;
        this.avgElevation = avgElevation;
        this.stdDevDistance = stdDevDistance;
        this.stdDevElevation = stdDevElevation;
    }

}
