/* ----------------------------------------------------------------------------
 * Copyright (C) 2022      European Space Agency
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

import java.util.ArrayList;
import java.util.Properties;

/**
 * The MetadataApp class holds the metadata of an App in a NMF Package.
 *
 * @author Cesar Coelho
 */
public class MetadataApp extends Metadata {

    public static final String APP_MAINCLASS = "pack.app.mainclass";
    public static final String APP_MAIN_JAR = "pack.app.mainjar";
    public static final String APP_MAX_HEAP = "pack.app.maxheap";
    public static final String APP_MIN_HEAP = "pack.app.minheap";
    public static final String APP_DEPENDENCIES = "pack.app.dependencies";

    public MetadataApp(final String packageName, final String version,
            final String timestamp, final String mainclass, final String mainJar,
            final String maxHeap, final ArrayList<String> dependencies) {
        super(new Properties());
        properties.put(Metadata.PACKAGE_NAME, packageName);
        properties.put(Metadata.PACKAGE_VERSION, version);
        properties.put(Metadata.PACKAGE_TIMESTAMP, timestamp);
        properties.put(Metadata.PACKAGE_TYPE, Metadata.TYPE_APP);
        properties.put(MetadataApp.APP_MAINCLASS, mainclass);
        properties.put(MetadataApp.APP_MAIN_JAR, mainJar);
        properties.put(MetadataApp.APP_MAX_HEAP, maxHeap);
        properties.put(MetadataApp.APP_DEPENDENCIES, dependencies);
    }

    MetadataApp(Properties props) {
        super(props);
    }

    public String getAppMainclass() {
        return properties.getProperty(APP_MAINCLASS);
    }

    public String getAppMainJar() {
        return properties.getProperty(APP_MAIN_JAR);
    }

    public String getAppMaxHeap() {
        return properties.getProperty(APP_MAX_HEAP);
    }

    public String getAppMinHeap() {
        return properties.getProperty(APP_MIN_HEAP);
    }

    public String getAppDependencies() {
        return properties.getProperty(APP_DEPENDENCIES);
    }

    public boolean hasDependencies() {
        String dependencies = properties.getProperty(APP_DEPENDENCIES);
        return (dependencies != null) && (dependencies.equals(""));
    }

}
