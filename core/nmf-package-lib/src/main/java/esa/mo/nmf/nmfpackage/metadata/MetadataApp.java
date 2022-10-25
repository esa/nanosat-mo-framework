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

import esa.mo.helpertools.helpers.HelperTime;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import org.ccsds.moims.mo.mal.structures.Time;

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

    /**
     *
     * @param packageName The name of the package.
     * @param version
     * @param timestamp
     * @param mainclass
     * @param mainJar
     * @param maxHeap
     * @param dependencies
     */
    public MetadataApp(final String packageName, final String version,
            final String mainclass, final String mainJar, final String maxHeap,
            final ArrayList<String> dependencies) {
        super(new Properties());
        properties.put(Metadata.PACKAGE_TYPE, Metadata.TYPE_APP);
        properties.put(Metadata.PACKAGE_NAME, packageName);
        properties.put(Metadata.PACKAGE_VERSION, version);
        properties.put(MetadataApp.APP_MAINCLASS, mainclass);
        properties.put(MetadataApp.APP_MAIN_JAR, mainJar);
        properties.put(MetadataApp.APP_MAX_HEAP, maxHeap);

        if (dependencies != null && !dependencies.isEmpty()) {
            StringBuilder str = new StringBuilder();
            for (String dep : dependencies) {
                str.append(dep).append(";");
            }
            properties.put(APP_DEPENDENCIES, removeLastChar(str.toString()));
        }
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

    public ArrayList<String> getAppDependencies() {
        String d = properties.getProperty(APP_DEPENDENCIES);
        if (d == null) {
            return new ArrayList<>();
        }

        ArrayList<String> deps = new ArrayList<>();
        deps.addAll(Arrays.asList(d.split(";")));
        return deps;
    }

    public String getAppDependenciesFullPaths(File sharedLibsFolder) {
        String d = properties.getProperty(APP_DEPENDENCIES);
        if (d == null) {
            return "";
        }

        StringBuilder out = new StringBuilder();
        String absolutePath = sharedLibsFolder.getAbsolutePath();
        String[] splits = d.split(";");
        for (String split : splits) {
            out.append(absolutePath).append(File.separator).append(split).append(":");
        }

        return removeLastChar(out.toString());
    }

    public boolean hasDependencies() {
        String dependencies = properties.getProperty(APP_DEPENDENCIES);
        return (dependencies != null) && !(dependencies.equals(""));
    }

    private String removeLastChar(String input) {
        return input.substring(0, input.length() - 1); // Removes last: ";"
    }

}
