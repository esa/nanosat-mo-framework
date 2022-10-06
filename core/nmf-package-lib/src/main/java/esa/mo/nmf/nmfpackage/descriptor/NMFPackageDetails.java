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

import java.util.Properties;

/**
 *
 * @author Cesar Coelho
 */
public class NMFPackageDetails {

    private final String packageName;
    private final String version;
    private final String timestamp;
    private final String mainclass;
    private final String mainJar;
    private final String maxHeap;
    private final Properties properties;

    public NMFPackageDetails(final String packageName, final String version,
            final String timestamp, final String mainclass,
            final String mainJar, final String maxHeap) {
        this.packageName = packageName;
        this.version = version;
        this.timestamp = timestamp;
        this.mainclass = mainclass;
        this.mainJar = mainJar;
        this.maxHeap = maxHeap;
        this.properties = null;
    }

    public NMFPackageDetails(final Properties properties) {
        this.packageName = null;
        this.version = null;
        this.timestamp = null;
        this.mainclass = null;
        this.mainJar = null;
        this.maxHeap = null;
        this.properties = properties;
    }

    public Properties getProperties() {
        if (properties != null) {
            return properties;
        } else {
            Properties props = new Properties();
            props.put(NMFPackageMetadata.PACKAGE_NAME, packageName);
            props.put(NMFPackageMetadata.PACKAGE_VERSION, version);
            props.put(NMFPackageMetadata.PACKAGE_TIMESTAMP, timestamp);
            props.put(NMFPackageMetadata.APP_MAINCLASS, mainclass);
            props.put(NMFPackageMetadata.APP_MAIN_JAR, mainJar);
            props.put(NMFPackageMetadata.APP_MAX_HEAP, maxHeap);
            return props;
        }
    }

    public String getPackageName() {
        return (properties == null) ? packageName : properties.getProperty(NMFPackageMetadata.PACKAGE_NAME);
    }

    public String getVersion() {
        return (properties == null) ? version : properties.getProperty(NMFPackageMetadata.PACKAGE_VERSION);
    }

    public String getTimestamp() {
        return (properties == null) ? timestamp : properties.getProperty(NMFPackageMetadata.PACKAGE_TIMESTAMP);
    }

    public String getMainclass() {
        return (properties == null) ? mainclass : properties.getProperty(NMFPackageMetadata.APP_MAINCLASS);
    }

    public String getMainJar() {
        return (properties == null) ? mainJar : properties.getProperty(NMFPackageMetadata.APP_MAIN_JAR);
    }

    public String getMaxHeap() {
        return (properties == null) ? maxHeap : properties.getProperty(NMFPackageMetadata.APP_MAX_HEAP);
    }
}
