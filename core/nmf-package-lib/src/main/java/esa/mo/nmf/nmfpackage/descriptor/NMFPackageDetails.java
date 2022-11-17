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
package esa.mo.nmf.nmfpackage.descriptor;

/**
 *
 * @author Cesar Coelho
 */
public class NMFPackageDetails {

    private final String packageName;
    private final String version;
    private final String timestamp;
    private final String mainclass;
    private final String maxHeap;

    public NMFPackageDetails(final String packageName, final String version, final String timestamp,
        final String mainclass, final String maxHeap) {
        this.packageName = packageName;
        this.version = version;
        this.timestamp = timestamp;
        this.mainclass = mainclass;
        this.maxHeap = maxHeap;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getVersion() {
        return version;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getMainclass() {
        return mainclass;
    }

    public String getMaxHeap() {
        return maxHeap;
    }
}
