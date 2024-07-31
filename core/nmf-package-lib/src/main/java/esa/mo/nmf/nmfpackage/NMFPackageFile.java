/* ----------------------------------------------------------------------------
 * Copyright (C) 2022      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft â€“ v2.4
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
package esa.mo.nmf.nmfpackage;

/**
 * Holds an entry to a file in the NMF Package.
 *
 * @author Cesar Coelho
 */
public class NMFPackageFile {

    private final String path;
    private final long crc;

    /**
     * Constructor for the NMFPackageFile object.
     *
     * @param path The path to the file in the package.
     * @param crc The CRC to the file in the package.
     */
    public NMFPackageFile(final String path, final long crc) {
        this.path = path;
        this.crc = crc;
    }

    /**
     * Returns the path to the file in the package.
     *
     * @return The path to the file in the package.
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the CRC of the file in the package.
     *
     * @return The CRC of the file in the package.
     */
    public long getCRC() {
        return crc;
    }
}
