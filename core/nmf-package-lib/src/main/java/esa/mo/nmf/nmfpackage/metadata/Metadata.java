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
import esa.mo.nmf.nmfpackage.NMFPackageManager;
import esa.mo.nmf.nmfpackage.receipt.NMFPackageDescriptor;
import esa.mo.nmf.nmfpackage.NMFPackageFile;
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
import org.ccsds.moims.mo.mal.structures.Time;

/**
 * The Metadata class holds the metadata of a NMF Package.
 *
 * @author Cesar Coelho
 */
public class Metadata {

    public static final String FILENAME = "package-metadata.properties";

    public static final String PACKAGE_TIMESTAMP = "info.creation-timestamp";
    public static final String PACKAGE_METADATA_VERSION = "info.metadata-version";
    public static final String METADATA_VERSION_LATEST = "4";

    public static final String PACKAGE_NAME = "info.name";
    public static final String PACKAGE_VERSION = "info.version";

    // PACKAGE_TYPE: "app", "nmf-update", "mission-update", "dependency", "java"
    public static final String PACKAGE_TYPE = "info.type";
    public static final String TYPE_APP = "app";
    public static final String TYPE_DEPENDENCY = "dependency";
    public static final String TYPE_UPDATE_JAVA = "java";
    public static final String TYPE_UPDATE_MISSION = "mission";
    public static final String TYPE_UPDATE_NMF = "nmf";
    public static final String TYPE_DELTA = "delta";

    public static final String FILE_COUNT = "zipped.file.count";
    public static final String FILE_PATH = "zipped.file.path";
    public static final String FILE_CRC = "zipped.file.crc";

    protected final Properties properties;
    private ArrayList<NMFPackageFile> files;

    /**
     * Constructor of the Metadata class.
     *
     * @param properties The properties to be part of the metadata.
     */
    public Metadata(final Properties properties) {
        this(properties, null);
    }

    @Deprecated
    public Metadata(Properties properties, ArrayList<NMFPackageFile> files) {
        this.files = files;
        this.properties = this.newOrderedProperties();
        final Time time = new Time(System.currentTimeMillis());
        final String timestamp = HelperTime.time2readableString(time);
        this.properties.put(Metadata.PACKAGE_TIMESTAMP, timestamp);
        this.properties.put(PACKAGE_METADATA_VERSION, METADATA_VERSION_LATEST);
        this.properties.putAll(properties);

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
    }

    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    public int getMetadataVersion() {
        String version = properties.getProperty(PACKAGE_METADATA_VERSION);
        return Integer.parseInt(version);
    }

    public String getPackageName() {
        return properties.getProperty(PACKAGE_NAME);
    }

    public String getPackageVersion() {
        return properties.getProperty(PACKAGE_VERSION);
    }

    public String getPackageTimestamp() {
        return properties.getProperty(PACKAGE_TIMESTAMP);
    }

    public String getPackageType() {
        return properties.getProperty(PACKAGE_TYPE);
    }

    public MetadataApp castToApp() {
        if (!isApp()) {
            return null;
        }
        return new MetadataApp(this.properties);
    }

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

    public void store(OutputStream outStream) throws IOException {
        properties.store(outStream, "NMF Package Metadata");
    }

    public void store(File file) throws FileNotFoundException, IOException {
        String parent = file.getParent();

        if (parent != null) {
            new File(parent).mkdirs();
        }

        try (FileOutputStream sigfos = new FileOutputStream(file)) {
            this.store(sigfos);
        }
    }

    /**
     * Parses a ZipFile, finds the receipt file and generates the respective
     * Metadata.
     *
     * @param zipFile The zip file with the receipt file.
     * @return The metadata of the NMF Package.
     */
    public static Metadata parseZipFile(final ZipFile zipFile) throws IOException {
        ZipEntry receipt = zipFile.getEntry(FILENAME);

        if (receipt == null) {
            // This code can be removed in the future! It is here at the 
            // moment in order to support backward compatibility
            NMFPackageDescriptor descriptor = NMFPackageDescriptor.parseZipFile(zipFile);
            Metadata metadata = descriptor.toMetadata();

            if (metadata != null) {
                return metadata;
            }

            throw new IOException("The " + FILENAME + " file does not exist!");
        }

        // Try to open the the receipt file inside the Zip file
        // and parse it into a Metadata object
        try (InputStream stream = zipFile.getInputStream(receipt)) {
            return Metadata.load(stream);
        }
    }

    public static Metadata load(InputStream inStream) throws IOException {
        Properties props = new Properties();
        props.load(inStream);
        return new Metadata(props);
    }

    public static Metadata load(File file) throws IOException {
        Metadata loadedMetadata;
        try (InputStream stream = new FileInputStream(file)) {
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

    public boolean isApp() {
        // Before version 4, all NMF Packages were used to carry Apps
        // Version 4 is more dynamic and allows NMF Packages to carry other
        // types of data, such as: NMF updates, NMF Mission updates, JREs, etc
        if (this.getMetadataVersion() < 4) {
            return true;
        }

        return TYPE_APP.equals(this.getPackageType());
    }

    public boolean isJava() {
        return TYPE_UPDATE_JAVA.equals(this.getPackageType());
    }

    public boolean isDependency() {
        return TYPE_DEPENDENCY.equals(this.getPackageType());
    }

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
