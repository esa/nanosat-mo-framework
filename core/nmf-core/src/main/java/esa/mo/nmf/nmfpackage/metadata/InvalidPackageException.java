/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
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
package esa.mo.nmf.nmfpackage.metadata;

import java.io.IOException;

/**
 * Thrown when the metadata of an NMF Package is not valid, such as a package
 * name that is not one plain file name. A package is not to be trusted, so
 * this is a refusal of the package, not a fault of the reader.
 */
public class InvalidPackageException extends IOException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception with the reason why the package is not valid.
     *
     * @param reason The reason.
     */
    public InvalidPackageException(String reason) {
        super(reason);
    }

    /**
     * Creates the exception with the reason why the package is not valid, and
     * the error that showed it.
     *
     * @param reason The reason.
     * @param cause The error that showed it.
     */
    public InvalidPackageException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
