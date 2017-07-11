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
package esa.mo.nmf.nmfpackage.descriptor;

import java.util.ArrayList;

/**
 *
 * @author Cesar Coelho
 */
public class NMFPackageDescriptor {

    private final NMFPackageDetails details;
    private final ArrayList<NMFPackageFile> files;

    public NMFPackageDescriptor(NMFPackageDetails details) {
        this.details = details;
        this.files = new ArrayList<NMFPackageFile>();
    }

    public NMFPackageDetails getDetails() {
        return details;
    }

    public ArrayList<NMFPackageFile> getFiles() {
        return files;
    }

    public void addFile(final NMFPackageFile file) {
        this.files.add(file);
    }

}
