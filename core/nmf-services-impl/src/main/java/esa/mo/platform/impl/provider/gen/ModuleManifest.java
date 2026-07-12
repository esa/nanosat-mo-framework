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
 * A gateware module manifest. The manifest is a sidecar properties file
 * delivered next to the module's partial bitstreams (typically inside an app's
 * NMF Package) that declares the module name, the shell version the module was
 * compiled against, and one bitstream variant per reconfigurable partition:
 *
 * <pre>
 * module.name        = fft
 * module.shell       = v3
 * module.slot-a.file = module_fft_a.bin
 * module.slot-a.crc  = 0x8F21C3D0
 * module.slot-b.file = module_fft_b.bin
 * module.slot-b.crc  = 0x11A047E9
 * </pre>
 *
 * Bitstream file paths are resolved relative to the manifest's directory. The
 * checksum is the CRC-32 of the bitstream file, written in hexadecimal.
 *
 * @author Cesar Coelho
 */
public class ModuleManifest {

    /**
     * The filename suffix of module manifest files.
     */
    public static final String MANIFEST_SUFFIX = ".manifest";

    private static final String KEY_NAME = "module.name";
    private static final String KEY_SHELL = "module.shell";
    private static final String KEY_PREFIX = "module.";
    private static final String KEY_FILE_SUFFIX = ".file";
    private static final String KEY_CRC_SUFFIX = ".crc";

    private final String name;
    private final String shellVersion;
    private final Map<String, Variant> variants;

    private ModuleManifest(String name, String shellVersion, Map<String, Variant> variants) {
        this.name = name;
        this.shellVersion = shellVersion;
        this.variants = variants;
    }

    /**
     * Returns the module name declared in the manifest.
     *
     * @return The module name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the shell version the module was compiled against.
     *
     * @return The shell version.
     */
    public String getShellVersion() {
        return shellVersion;
    }

    /**
     * Returns the bitstream variant for a partition, or null if the manifest
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
     * manifest.
     */
    public static ModuleManifest load(File file) throws IOException {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(file)) {
            props.load(in);
        }

        String name = props.getProperty(KEY_NAME);
        String shell = props.getProperty(KEY_SHELL);
        if (name == null || name.trim().isEmpty()) {
            throw new IOException("The manifest is missing the " + KEY_NAME + " key: " + file);
        }
        if (shell == null || shell.trim().isEmpty()) {
            throw new IOException("The manifest is missing the " + KEY_SHELL + " key: " + file);
        }

        Map<String, Variant> variants = new HashMap<>();
        for (String key : props.stringPropertyNames()) {
            if (!key.startsWith(KEY_PREFIX) || !key.endsWith(KEY_FILE_SUFFIX)) {
                continue;
            }
            String partitionId = key.substring(KEY_PREFIX.length(),
                    key.length() - KEY_FILE_SUFFIX.length());
            if (partitionId.isEmpty() || "name".equals(partitionId) || "shell".equals(partitionId)) {
                continue;
            }
            String crcKey = KEY_PREFIX + partitionId + KEY_CRC_SUFFIX;
            String crcValue = props.getProperty(crcKey);
            if (crcValue == null) {
                throw new IOException("The manifest declares " + key
                        + " without the matching " + crcKey + " key: " + file);
            }
            File bitstream = new File(file.getParentFile(), props.getProperty(key).trim());
            long crc;
            try {
                crc = Long.decode(crcValue.trim());
            } catch (NumberFormatException ex) {
                throw new IOException("The " + crcKey + " value is not a valid checksum: "
                        + crcValue, ex);
            }
            variants.put(partitionId, new Variant(bitstream, crc));
        }

        if (variants.isEmpty()) {
            throw new IOException("The manifest declares no bitstream variants: " + file);
        }

        return new ModuleManifest(name.trim(), shell.trim(), variants);
    }

    /**
     * Finds the manifest of a module by scanning a directory tree (typically
     * the apps directory) for {@code *.manifest} files whose module name
     * matches.
     *
     * @param root The directory to scan (one level of subdirectories deep).
     * @param moduleName The module name to look for.
     * @return The parsed manifest, or null if no manifest declares the module.
     */
    public static ModuleManifest find(File root, String moduleName) {
        if (root == null || !root.isDirectory()) {
            return null;
        }
        File[] entries = root.listFiles();
        if (entries == null) {
            return null;
        }
        for (File entry : entries) {
            if (entry.isDirectory()) {
                ModuleManifest found = find(entry, moduleName);
                if (found != null) {
                    return found;
                }
            } else if (entry.getName().endsWith(MANIFEST_SUFFIX)) {
                try {
                    ModuleManifest manifest = ModuleManifest.load(entry);
                    if (manifest.getName().equals(moduleName)) {
                        return manifest;
                    }
                } catch (IOException ex) {
                    // A malformed manifest never hides other modules
                }
            }
        }
        return null;
    }

    /**
     * A bitstream variant of the module for one specific partition.
     */
    public static class Variant {

        private final File file;
        private final long crc;

        Variant(File file, long crc) {
            this.file = file;
            this.crc = crc;
        }

        /**
         * Returns the bitstream file.
         *
         * @return The bitstream file.
         */
        public File getFile() {
            return file;
        }

        /**
         * Returns the declared CRC-32 of the bitstream file.
         *
         * @return The declared checksum.
         */
        public long getCrc() {
            return crc;
        }

        /**
         * Verifies the bitstream file against the declared checksum.
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
