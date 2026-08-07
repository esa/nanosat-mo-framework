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

import esa.mo.nmf.nmfpackage.NMFPackageFile;
import esa.mo.nmf.nmfpackage.NMFPackageManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperTime;
import org.ccsds.moims.mo.mal.structures.Time;

/**
 * The Metadata class holds the metadata of a NMF Package.
 *
 * @author Cesar Coelho
 */
public class Metadata {

    /** File name of the metadata receipt bundled inside every NMF Package. */
    public static final String FILENAME = "package-metadata.properties";

    /** Property key for the package creation timestamp. */
    public static final String PACKAGE_TIMESTAMP = "info.creation-timestamp";
    /** Property key for the metadata format version. */
    public static final String PACKAGE_METADATA_VERSION = "info.metadata-version";
    /**
     * The latest metadata format version written by this library. Version 5
     * added the version of the NMF that an App was built against, so that the
     * App can be told apart from the framework it was compiled for; packages
     * written before it carry no such field.
     */
    public static final String METADATA_VERSION_LATEST = "5";

    /** Property key for the package name. */
    public static final String PACKAGE_NAME = "info.name";
    /** Property key for the package version. */
    public static final String PACKAGE_VERSION = "info.version";

    // PACKAGE_TYPE: "app", "nmf-update", "mission-update", "dependency", "java"
    /** Property key for the package type; one of the {@code TYPE_*} values. */
    public static final String PACKAGE_TYPE = "info.type";
    /** Package type value for an App. */
    public static final String TYPE_APP = "app";
    /** Package type value for a shared dependency. */
    public static final String TYPE_DEPENDENCY = "dependency";
    /** Package type value for a Java runtime update. */
    public static final String TYPE_UPDATE_JAVA = "java";
    /** Package type value for a mission baseline update. */
    public static final String TYPE_UPDATE_MISSION = "mission";
    /** Package type value for an NMF baseline update. */
    public static final String TYPE_UPDATE_NMF = "nmf";
    /** Package type value for a delta package. */
    public static final String TYPE_DELTA = "delta";

    /** Property key for the number of files listed in the package. */
    public static final String FILE_COUNT = "zipped.file.count";
    /** Property key prefix for the path of each listed file. */
    public static final String FILE_PATH = "zipped.file.path";
    /** Property key prefix for the CRC checksum of each listed file. */
    public static final String FILE_CRC = "zipped.file.crc";

    /** The backing properties holding all metadata entries. */
    protected final Properties properties;
    private ArrayList<NMFPackageFile> files;

    /**
     * Constructor of the Metadata class.
     *
     * @param properties The properties to be part of the metadata.
     */
    public Metadata(final Properties properties) {
        this.files = null;
        this.properties = this.newOrderedProperties();
        final Time time = new Time(System.currentTimeMillis());
        final String timestamp = HelperTime.time2readableString(time);
        this.properties.put(Metadata.PACKAGE_TIMESTAMP, timestamp);
        this.properties.put(PACKAGE_METADATA_VERSION, METADATA_VERSION_LATEST);
        this.properties.putAll(properties);

        /*
        if (files != null) {
            this.properties.put(FILE_COUNT, String.valueOf(files.size()));

            for (int i = 0; i < files.size(); i++) {
                NMFPackageFile file = files.get(i);
                String index = "." + i;
                String crc = String.valueOf(file.getCRC());
                this.properties.put(FILE_PATH + index, file.getPath());
                this.properties.put(FILE_CRC + index, crc);
            }
        }
        */
    }

    /**
     * Adds or overwrites a metadata property.
     *
     * @param key the property key
     * @param value the property value
     */
    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    /**
     * Returns the metadata format version of this package.
     *
     * @return the metadata version
     */
    public int getMetadataVersion() {
        String version = properties.getProperty(PACKAGE_METADATA_VERSION);
        return Integer.parseInt(version);
    }

    /**
     * Returns the package name.
     *
     * @return the package name
     */
    public String getPackageName() {
        return properties.getProperty(PACKAGE_NAME);
    }

    /**
     * Returns the package version.
     *
     * @return the package version
     */
    public String getPackageVersion() {
        return properties.getProperty(PACKAGE_VERSION);
    }

    /**
     * Returns the package creation timestamp.
     *
     * @return the creation timestamp
     */
    public String getPackageTimestamp() {
        return properties.getProperty(PACKAGE_TIMESTAMP);
    }

    /**
     * Returns the package type; one of the {@code TYPE_*} values.
     *
     * @return the package type
     */
    public String getPackageType() {
        return properties.getProperty(PACKAGE_TYPE);
    }

    /**
     * Returns this metadata as an {@link MetadataApp} if the package is an App.
     *
     * @return the app metadata, or {@code null} if this package is not an App
     */
    public MetadataApp castToApp() {
        if (!isApp()) {
            return null;
        }
        return new MetadataApp(this.properties);
    }

    /**
     * Returns the list of files declared in the package, parsing them from the properties on
     * first access.
     *
     * @return the list of package files
     */
    public synchronized ArrayList<NMFPackageFile> getFiles() {
        if (files != null) {
            return files;
        }

        files = new ArrayList<>();
        int count = Integer.parseInt(properties.getProperty(FILE_COUNT, "0"));

        for (int i = 0; i < count; i++) {
            String index = "." + i;
            String path = properties.getProperty(FILE_PATH + index);
            long crc = Long.parseLong(properties.getProperty(FILE_CRC + index));
            files.add(new NMFPackageFile(path, crc));
        }

        return files;
    }

    /**
     * Writes the metadata to the given output stream.
     *
     * @param outStream the stream to write to
     * @throws IOException if writing fails
     */
    public void store(OutputStream outStream) throws IOException {
        properties.store(outStream, "NMF Package Metadata");
    }

    /**
     * Writes the metadata to the given file, creating parent directories as needed.
     *
     * @param file the file to write to
     * @throws FileNotFoundException if the file cannot be created
     * @throws IOException if writing fails
     */
    public void store(File file) throws FileNotFoundException, IOException {
        String parent = file.getParent();

        if (parent != null) {
            new File(parent).mkdirs();
        }

        try ( FileOutputStream sigfos = new FileOutputStream(file)) {
            this.store(sigfos);
        }
    }

    /**
     * Parses a ZipFile, finds the receipt file and generates the respective
     * Metadata.
     *
     * @param zipFile The zip file with the receipt file.
     * @return The metadata of the NMF Package.
     * @throws IOException If the file cannot be parsed.
     */
    public static Metadata parseZipFile(final ZipFile zipFile) throws IOException {
        ZipEntry receipt = zipFile.getEntry(FILENAME);

        if (receipt == null) {
            throw new IOException("The " + FILENAME + " file does not exist!");
        }

        // Try to open the the receipt file inside the Zip file
        // and parse it into a Metadata object
        try ( InputStream stream = zipFile.getInputStream(receipt)) {
            return Metadata.load(stream);
        }
    }

    /**
     * Loads metadata from the given input stream.
     *
     * @param inStream the stream to read from
     * @return the loaded metadata
     * @throws IOException if reading fails
     */
    public static Metadata load(InputStream inStream) throws IOException {
        Properties props = new Properties();
        props.load(inStream);
        return new Metadata(props);
    }

    /**
     * Loads metadata from the given file.
     *
     * @param file the file to read from
     * @return the loaded metadata
     * @throws IOException if reading fails
     */
    public static Metadata load(File file) throws IOException {
        Metadata loadedMetadata;
        try ( InputStream stream = new FileInputStream(file)) {
            loadedMetadata = Metadata.load(stream);
        }
        return loadedMetadata;
    }

    @SuppressWarnings("serial")
    private Properties newOrderedProperties() {
        return new Properties() {
            @Override
            public synchronized Enumeration<Object> keys() {
                return Collections.enumeration(new TreeSet<>(super.keySet()));
            }

            @Override
            public synchronized Set<Map.Entry<Object, Object>> entrySet() {
                return Collections.synchronizedSet(
                        super.entrySet()
                                .stream()
                                .sorted(Comparator.comparing(e -> e.getKey().toString()))
                                .collect(Collectors.toCollection(LinkedHashSet::new)));
            }
        };
    }

    /**
     * Whether this package carries an App.
     *
     * @return {@code true} if the package is an App (or a pre-version-4 package)
     */
    public boolean isApp() {
        // Before version 4, all NMF Packages were used to carry Apps
        // Version 4 is more dynamic and allows NMF Packages to carry other
        // types of data, such as: NMF updates, NMF Mission updates, JREs, etc
        if (this.getMetadataVersion() < 4) {
            return true;
        }

        return TYPE_APP.equals(this.getPackageType());
    }

    /**
     * Whether this package carries a Java runtime update.
     *
     * @return {@code true} if the package type is {@code java}
     */
    public boolean isJava() {
        return TYPE_UPDATE_JAVA.equals(this.getPackageType());
    }

    /**
     * Whether this package carries an NMF baseline update.
     *
     * @return {@code true} if the package type is {@code nmf}
     */
    public boolean isNMF() {
        return TYPE_UPDATE_NMF.equals(this.getPackageType());
    }

    /**
     * Whether this package carries a mission baseline update.
     *
     * @return {@code true} if the package type is {@code mission}
     */
    public boolean isMission() {
        return TYPE_UPDATE_MISSION.equals(this.getPackageType());
    }

    /**
     * Whether this package carries a shared dependency.
     *
     * @return {@code true} if the package type is {@code dependency}
     */
    public boolean isDependency() {
        return TYPE_DEPENDENCY.equals(this.getPackageType());
    }

    /**
     * Whether this package delivers a component of an NMF Software Baseline
     * (framework JARs, mission JARs or a Java runtime), as opposed to an App or
     * a shared dependency. Such packages trigger the baseline checksum
     * regeneration and the primary/secondary rotation in the Package Manager.
     *
     * @return {@code true} for {@code nmf}, {@code mission} and {@code java}
     * package types.
     */
    public boolean isBaselineComponent() {
        return isNMF() || isMission() || isJava();
    }

    /**
     * Compares this metadata with another by creation timestamp, name and version.
     *
     * @param other the metadata to compare against
     * @return {@code true} if the timestamp, name and version all match
     */
    public boolean sameAs(Metadata other) {
        // Starts with the timestamp because this is most of the times unique!
        if (!this.getPackageTimestamp().equals(other.getPackageTimestamp())) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(
                    Level.FINE, "The creation timestamp does not match!");
            return false;
        }

        if (!this.getPackageName().equals(other.getPackageName())) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(
                    Level.FINE, "The name does not match!");
            return false;
        }

        if (!this.getPackageVersion().equals(other.getPackageVersion())) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(
                    Level.FINE, "The version does not match!");
            return false;
        }

        return true;
    }

    /**
     * Prints the metadata in a readable string.
     *
     * @return The metadata as a string.
     */
    public String print() {
        StringBuilder str = new StringBuilder();

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            str.append("  >> ").append(entry.getKey()).append(" = ")
                    .append(entry.getValue()).append("\n");
        }

        return str.toString();
    }
}
