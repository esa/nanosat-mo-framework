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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 *
 * @author Cesar Coelho
 */
public class NMFPackageMetadata {

    public static final String FILENAME = "metadata.properties";

    public static final String METADATA_VERSION = "NMFPackageMetadataVersion";
    public static final String PACKAGE_TIMESTAMP = "PackageCreationTimestamp";
    public static final String PACKAGE_NAME = "PackageName";
    public static final String PACKAGE_VERSION = "PackageVersion";
    // PACKAGE_TYPE: "app", "nmf-update", "mission-update", "dependency", "java"
    public static final String PACKAGE_TYPE = "PackageType";

    public static final String FILE_COUNT = "FileCount";
    public static final String FILE_PATH = "FilePath";
    public static final String FILE_CRC = "FileCRC";

    public static final String APP_MAINCLASS = "MainClass";
    public static final String APP_MAIN_JAR = "MainJar";
    public static final String APP_MAX_HEAP = "MaxHeap";
    public static final String APP_MIN_HEAP = "MinHeap";

    private final Properties properties;

    public NMFPackageMetadata(final Properties properties) {
        this.properties = properties;

        // Validation needs to occur for the mandatory field values
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    public String getMetadataVersion() {
        return properties.getProperty(METADATA_VERSION);
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

    public void store(OutputStream outStream) throws IOException {
        properties.store(outStream, null);
    }

    public static NMFPackageMetadata load(InputStream inStream) throws IOException {
        Properties props = new Properties();
        props.load(inStream);
        return new NMFPackageMetadata(props);
    }

    public NMFPackageDescriptor toPackageDescriptor() {
        NMFPackageDetails details = new NMFPackageDetails(properties);
        NMFPackageDescriptor descriptor = new NMFPackageDescriptor(details);
        int size = Integer.parseInt(properties.getProperty(FILE_COUNT, "0"));

        for (int i = 0; i < size; i++) {
            String suffix = "_" + i;
            String path = properties.getProperty(FILE_PATH + suffix, "");
            long crc = Long.parseLong(properties.getProperty(FILE_CRC + suffix, "0"));
            NMFPackageFile file = new NMFPackageFile(path, crc);
            descriptor.addFile(file);
        }

        return descriptor;
    }
}
