/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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

/**
 *
 * @author Cesar Coelho
 */
public class Vector {

    private final double x;
    private final double y;
    private final double z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Add
    public Vector add(Vector addend) {
        return new Vector(x + addend.x, y + addend.y, z + addend.z);
    }

    // Cross Product
    public Vector crossProduct(Vector b) {
        return new Vector((y * b.z - b.y * z), (z * b.x - b.z * x), (x * b.y - b.x * y));
    }

    // Dot Product
    public Vector dotProduct(Vector b) {
        return new Vector(x * b.x, y * b.y, z * b.z);
    }

    // times
    public Vector times(double constant) {
        return new Vector(x * constant, y * constant, z * constant);
    }

    // Lenght
    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    // Print Vector
    public void printVector() {
        System.out.printf("Vector:\n%f, %f, %f, %f\n", x, y, z, this.length());
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public double z() {
        return this.z;
    }

}
