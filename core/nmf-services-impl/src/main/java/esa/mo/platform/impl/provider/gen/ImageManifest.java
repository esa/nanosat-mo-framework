/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
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
package esa.mo.platform.impl.provider.gen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.zip.CRC32;

/**
 * A software image manifest. The manifest is a sidecar properties file
 * delivered next to the image files (typically inside an NMF Package) that
 * declares the image name, the hypervisor configuration version the image was
 * built against, and one image variant per hypervisor partition:
 *
 * <pre>
 * image.name          = payload-os
 * image.config        = xmcf-v2
 * image.p1.file       = payload_os_p1.img
 * image.p1.crc        = 0x5A1EC3D0
 * image.p2.file       = payload_os_p2.img
 * image.p2.crc        = 0x7700A4E9
 * </pre>
 *
 * Image file paths are resolved relative to the manifest's directory. The
 * checksum is the CRC-32 of the image file, written in hexadecimal.
 *
 * @author Cesar Coelho
 */
public class ImageManifest {

    /**
     * The filename suffix of image manifest files.
     */
    public static final String MANIFEST_SUFFIX = ".manifest";

    private static final String KEY_NAME = "image.name";
    private static final String KEY_CONFIG = "image.config";
    private static final String KEY_PREFIX = "image.";
    private static final String KEY_FILE_SUFFIX = ".file";
    private static final String KEY_CRC_SUFFIX = ".crc";

    private final String name;
    private final String configVersion;
    private final Map<String, Variant> variants;

    private ImageManifest(String name, String configVersion, Map<String, Variant> variants) {
        this.name = name;
        this.configVersion = configVersion;
        this.variants = variants;
    }

    /**
     * Returns the image name declared in the manifest.
     *
     * @return The image name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the hypervisor configuration version the image was built
     * against.
     *
     * @return The hypervisor configuration version.
     */
    public String getConfigVersion() {
        return configVersion;
    }

    /**
     * Returns the image variant for a partition, or null if the manifest
     * declares no variant for it.
     *
     * @param partitionId The partition id.
     * @return The variant, or null.
     */
    public Variant getVariant(String partitionId) {
        return variants.get(partitionId);
    }

    /**
     * Loads a manifest from a file.
     *
     * @param file The manifest file.
     * @return The parsed manifest.
     * @throws IOException If the file could not be read or is not a valid
     * image manifest.
     */
    public static ImageManifest load(File file) throws IOException {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(file)) {
            props.load(in);
        }

        String name = props.getProperty(KEY_NAME);
        String config = props.getProperty(KEY_CONFIG);
        if (name == null || name.trim().isEmpty()) {
            throw new IOException("The manifest is missing the " + KEY_NAME + " key: " + file);
        }
        if (config == null || config.trim().isEmpty()) {
            throw new IOException("The manifest is missing the " + KEY_CONFIG + " key: " + file);
        }

        Map<String, Variant> variants = new HashMap<>();
        for (String key : props.stringPropertyNames()) {
            if (!key.startsWith(KEY_PREFIX) || !key.endsWith(KEY_FILE_SUFFIX)) {
                continue;
            }
            String partitionId = key.substring(KEY_PREFIX.length(),
                    key.length() - KEY_FILE_SUFFIX.length());
            if (partitionId.isEmpty() || "name".equals(partitionId) || "config".equals(partitionId)) {
                continue;
            }
            String crcKey = KEY_PREFIX + partitionId + KEY_CRC_SUFFIX;
            String crcValue = props.getProperty(crcKey);
            if (crcValue == null) {
                throw new IOException("The manifest declares " + key
                        + " without the matching " + crcKey + " key: " + file);
            }
            File image = new File(file.getParentFile(), props.getProperty(key).trim());
            long crc;
            try {
                crc = Long.decode(crcValue.trim());
            } catch (NumberFormatException ex) {
                throw new IOException("The " + crcKey + " value is not a valid checksum: "
                        + crcValue, ex);
            }
            variants.put(partitionId, new Variant(image, crc));
        }

        if (variants.isEmpty()) {
            throw new IOException("The manifest declares no image variants: " + file);
        }

        return new ImageManifest(name.trim(), config.trim(), variants);
    }

    /**
     * Finds the manifest of an image by scanning a directory tree for
     * {@code *.manifest} files whose image name matches. Files that are not
     * valid image manifests (for example, gateware module manifests) are
     * skipped.
     *
     * @param root The directory to scan, recursively.
     * @param imageName The image name to look for.
     * @return The parsed manifest, or null if no manifest declares the image.
     */
    public static ImageManifest find(File root, String imageName) {
        if (root == null || !root.isDirectory()) {
            return null;
        }
        File[] entries = root.listFiles();
        if (entries == null) {
            return null;
        }
        for (File entry : entries) {
            if (entry.isDirectory()) {
                ImageManifest found = find(entry, imageName);
                if (found != null) {
                    return found;
                }
            } else if (entry.getName().endsWith(MANIFEST_SUFFIX)) {
                try {
                    ImageManifest manifest = ImageManifest.load(entry);
                    if (manifest.getName().equals(imageName)) {
                        return manifest;
                    }
                } catch (IOException ex) {
                    // A malformed or foreign manifest never hides other images
                }
            }
        }
        return null;
    }

    /**
     * An image variant for one specific hypervisor partition.
     */
    public static class Variant {

        private final File file;
        private final long crc;

        Variant(File file, long crc) {
            this.file = file;
            this.crc = crc;
        }

        /**
         * Returns the image file.
         *
         * @return The image file.
         */
        public File getFile() {
            return file;
        }

        /**
         * Returns the declared CRC-32 of the image file.
         *
         * @return The declared checksum.
         */
        public long getCrc() {
            return crc;
        }

        /**
         * Verifies the image file against the declared checksum.
         *
         * @return TRUE if the computed CRC-32 matches the declared value.
         * @throws IOException If the file could not be read.
         */
        public boolean verify() throws IOException {
            CRC32 crc32 = new CRC32();
            crc32.update(Files.readAllBytes(file.toPath()));
            return crc32.getValue() == crc;
        }
    }
}
