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
package esa.mo.nmf.nmfpackage.receipt;

import esa.mo.nmf.nmfpackage.metadata.Metadata;
import esa.mo.nmf.nmfpackage.metadata.MetadataApp;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author Cesar Coelho
 */
@Deprecated
public class DetailsApp {

    private final String packageName;
    private final String version;
    private final String timestamp;
    private final String mainclass;
    private final String mainJar;
    private final String maxHeap;
    private final ArrayList<String> dependencies;
    private final Properties properties;

    public DetailsApp(final String packageName, final String version,
            final String timestamp, final String mainclass, final String mainJar,
            final String maxHeap, final ArrayList<String> dependencies) {
        this.packageName = packageName;
        this.version = version;
        this.timestamp = timestamp;
        this.mainclass = mainclass;
        this.mainJar = mainJar;
        this.maxHeap = maxHeap;
        this.dependencies = dependencies;
        this.properties = null;
    }

    /*
    public DetailsApp(final Properties properties) {
        this.packageName = null;
        this.version = null;
        this.timestamp = null;
        this.mainclass = null;
        this.mainJar = null;
        this.maxHeap = null;
        this.dependencies = null;
        this.properties = properties;
    }
    */

    public Properties getProperties() {
        if (properties != null) {
            return properties;
        } else {
            Properties props = new Properties();
            props.put(Metadata.PACKAGE_NAME, packageName);
            props.put(Metadata.PACKAGE_VERSION, version);
            props.put(Metadata.PACKAGE_TIMESTAMP, timestamp);
            props.put(MetadataApp.APP_MAINCLASS, mainclass);
            props.put(MetadataApp.APP_MAIN_JAR, mainJar);
            props.put(MetadataApp.APP_MAX_HEAP, maxHeap);

            if (dependencies != null) {
                props.put(MetadataApp.APP_DEPENDENCIES, generateDependencies());
            }

            return props;
        }
    }

    private String generateDependencies() {
        StringBuilder str = new StringBuilder();

        if (dependencies != null) {
            for (String dependency : dependencies) {
                str.append(dependency).append(";");
            }
        }

        return str.toString();
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

    public String getMainclass() {
        return (properties == null) ? mainclass : properties.getProperty(MetadataApp.APP_MAINCLASS);
    }

    public String getMainJar() {
        return (properties == null) ? mainJar : properties.getProperty(MetadataApp.APP_MAIN_JAR);
    }

    public String getMaxHeap() {
        return (properties == null) ? maxHeap : properties.getProperty(MetadataApp.APP_MAX_HEAP);
    }
}
