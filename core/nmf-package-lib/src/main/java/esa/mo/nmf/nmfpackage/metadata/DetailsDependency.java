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
package esa.mo.nmf.nmfpackage.metadata;

import java.util.Properties;

/**
 *
 * @author Cesar Coelho
 */
public class DetailsDependency {

    private final String packageName;
    private final String version;
    private final String timestamp;
    private final Properties properties;

    public DetailsDependency(final String packageName, final String version,
            final String timestamp) {
        this.packageName = packageName;
        this.version = version;
        this.timestamp = timestamp;
        this.properties = null;
    }

    public DetailsDependency(final Properties properties) {
        this.packageName = null;
        this.version = null;
        this.timestamp = null;
        this.properties = properties;
    }

    public Properties getProperties() {
        if (properties != null) {
            return properties;
        } else {
            Properties props = new Properties();
            props.put(Metadata.PACKAGE_NAME, packageName);
            props.put(Metadata.PACKAGE_VERSION, version);
            props.put(Metadata.PACKAGE_TIMESTAMP, timestamp);
            return props;
        }
    }

    public String getPackageName() {
        return (properties == null) ? packageName : properties.getProperty(Metadata.PACKAGE_NAME);
    }

    public String getVersion() {
        return (properties == null) ? version : properties.getProperty(Metadata.PACKAGE_VERSION);
    }

    public String getTimestamp() {
        return (properties == null) ? timestamp : properties.getProperty(Metadata.PACKAGE_TIMESTAMP);
    }

}
