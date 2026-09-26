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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

/**
 * The MetadataApp class holds the metadata of an App in a NMF Package.
 *
 * @author Cesar Coelho
 */
public class MetadataApp extends Metadata {

    /** Property key for the App's main class. */
    public static final String APP_MAINCLASS = "pack.app.mainclass";
    /** Property key for the App's main JAR. */
    public static final String APP_MAIN_JAR = "pack.app.mainjar";
    /** Property key for the App's maximum JVM heap size. */
    public static final String APP_MAX_HEAP = "pack.app.maxheap";
    /** Property key for the App's minimum JVM heap size. */
    public static final String APP_MIN_HEAP = "pack.app.minheap";
    /** Property key for the App's dependency JAR filenames, separated by {@code ;}. */
    public static final String APP_DEPENDENCIES = "pack.app.dependencies";
    /**
     * Property key for the version of the NMF that the App was built against.
     * Absent from packages written before metadata version 5, where the version
     * the App was compiled for is simply not known.
     */
    public static final String APP_NMF_VERSION = "pack.app.nmf-version";

    /**
     * Constructor for the MetadataApp class.
     *
     * @param packageName The name of the package.
     * @param version The version of the package.
     * @param mainclass The mainclass of the package.
     * @param mainJar The main jar of the package.
     * @param maxHeap The Maximum Heap of the package.
     * @param minHeap The Minimum Heap of the package.
     * @param dependencies The jar filenames dependencies of the App.
     * @param nmfVersion The version of the NMF that the App was built against.
     */
    public MetadataApp(final String packageName, final String version,
            final String mainclass, final String mainJar, final String maxHeap,
            final String minHeap, final ArrayList<String> dependencies,
            final String nmfVersion) {
        super(new Properties());
        properties.put(Metadata.PACKAGE_TYPE, Metadata.TYPE_APP);
        properties.put(Metadata.PACKAGE_NAME, packageName);
        properties.put(Metadata.PACKAGE_VERSION, version);
        properties.put(MetadataApp.APP_MAINCLASS, mainclass);
        properties.put(MetadataApp.APP_MAIN_JAR, mainJar);
        properties.put(MetadataApp.APP_MAX_HEAP, maxHeap == null ? "128m" : maxHeap);
        properties.put(MetadataApp.APP_MIN_HEAP, minHeap == null ? "32m" : minHeap);

        if (nmfVersion != null) {
            properties.put(MetadataApp.APP_NMF_VERSION, nmfVersion);
        }

        if (dependencies != null && !dependencies.isEmpty()) {
            StringBuilder str = new StringBuilder();
            for (String dep : dependencies) {
                str.append(dep).append(";");
            }
            properties.put(APP_DEPENDENCIES, removeLastChar(str.toString()));
        }
    }

    /**
     * Constructor for the MetadataApp class.
     *
     * @param props The initial properties for the package.
     */
    MetadataApp(Properties props) {
        super(props);
    }

    /**
     * Returns the App's main class.
     *
     * @return the fully-qualified main class name
     */
    public String getAppMainclass() {
        return properties.getProperty(APP_MAINCLASS);
    }

    /**
     * Returns the App's main JAR filename.
     *
     * @return the main JAR filename
     */
    public String getAppMainJar() {
        return properties.getProperty(APP_MAIN_JAR);
    }

    /**
     * Returns the App's maximum JVM heap size.
     *
     * @return the maximum heap size (for example {@code 128m})
     */
    public String getAppMaxHeap() {
        return properties.getProperty(APP_MAX_HEAP);
    }

    /**
     * Returns the App's minimum JVM heap size.
     *
     * @return the minimum heap size (for example {@code 32m})
     */
    public String getAppMinHeap() {
        return properties.getProperty(APP_MIN_HEAP);
    }

    /**
     * Returns the version of the NMF that the App was built against.
     *
     * @return the NMF version, or {@code null} if the package was written
     * before metadata version 5 and therefore does not record it
     */
    public String getAppNMFVersion() {
        return properties.getProperty(APP_NMF_VERSION);
    }

    /**
     * Returns the App's dependency JAR filenames.
     *
     * @return the list of dependency filenames, empty if the App has none
     */
    public ArrayList<String> getAppDependencies() {
        String d = properties.getProperty(APP_DEPENDENCIES);
        if (d == null) {
            return new ArrayList<>();
        }

        ArrayList<String> deps = new ArrayList<>();
        deps.addAll(Arrays.asList(d.split(";")));
        return deps;
    }

    /**
     * Returns the App's dependency JARs as a classpath string of absolute paths, resolved
     * against the given shared-libraries folder.
     *
     * @param sharedLibsFolder the folder containing the shared dependency JARs
     * @return a {@code :}-separated classpath string, empty if the App has no dependencies
     */
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

    /**
     * Whether the App declares any dependency JARs.
     *
     * @return {@code true} if the App has at least one dependency
     */
    public boolean hasDependencies() {
        String dependencies = properties.getProperty(APP_DEPENDENCIES);
        return (dependencies != null) && !(dependencies.equals(""));
    }

    private String removeLastChar(String input) {
        return input.substring(0, input.length() - 1); // Removes last: ";"
    }

}
