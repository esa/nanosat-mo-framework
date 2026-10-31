/* ----------------------------------------------------------------------------
 * Copyright (C) 2022      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft - v2.4
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
    private final String sha256;

    /**
     * Constructor for a file of a package written before metadata version 6,
     * which is checked by its CRC-32.
     *
     * @param path The path to the file in the package.
     * @param crc The CRC to the file in the package.
     */
    public NMFPackageFile(final String path, final long crc) {
        this.path = path;
        this.crc = crc;
        this.sha256 = null;
    }

    /**
     * Constructor for a file that is checked by its SHA-256.
     *
     * @param path The path to the file in the package.
     * @param sha256 The SHA-256 of the file, as a lowercase hexadecimal string.
     */
    public NMFPackageFile(final String path, final String sha256) {
        this.path = path;
        this.crc = 0;
        this.sha256 = sha256;
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

    /**
     * Returns the SHA-256 of the file in the package.
     *
     * @return The SHA-256 as a lowercase hexadecimal string, or {@code null}
     * for a file of a package written before metadata version 6, which has
     * only a CRC.
     */
    public String getSHA256() {
        return sha256;
    }
}
