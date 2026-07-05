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
package esa.mo.nmf.nmfpackage.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Generates the SHA256SUMS checksum manifest of a directory, in the format
 * accepted by {@code sha256sum -c}. The NMF Bootloader verifies the integrity
 * of the software baseline directories against these files at every boot.
 * The manifest is generated at build time by the nmf-linux-maven-plugin and
 * at install time when an update package delivers a new baseline component.
 *
 * @author Cesar Coelho
 */
public class ChecksumGenerator {

    /**
     * The file name of the checksum manifest, as verified by the Bootloader.
     */
    public static final String CHECKSUMS_FILENAME = "SHA256SUMS";

    private static final char[] HEX = "0123456789abcdef".toCharArray();

    /**
     * Writes the SHA256SUMS file of a directory, covering every regular file
     * directly inside it (subdirectories and a pre-existing SHA256SUMS file
     * are not included). The entries are sorted by file name so the output
     * is reproducible.
     *
     * @param directory The directory to generate the checksum manifest for.
     * @throws IOException if the directory cannot be listed or a file cannot
     * be read or written.
     */
    public static void writeChecksumsFile(File directory) throws IOException {
        File[] files = directory.listFiles();
        if (files == null) {
            throw new IOException("Not a listable directory: " + directory.getAbsolutePath());
        }

        Arrays.sort(files, (a, b) -> a.getName().compareTo(b.getName()));
        StringBuilder content = new StringBuilder();

        for (File file : files) {
            if (!file.isFile() || CHECKSUMS_FILENAME.equals(file.getName())) {
                continue;
            }
            // Two spaces: the sha256sum text-mode format
            content.append(sha256Hex(file)).append("  ").append(file.getName()).append('\n');
        }

        File checksumsFile = new File(directory, CHECKSUMS_FILENAME);
        Files.write(checksumsFile.toPath(), content.toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Computes the SHA-256 checksum of a file.
     *
     * @param file The file to compute the checksum of.
     * @return The checksum as a lowercase hexadecimal string.
     * @throws IOException if the file could not be read.
     */
    public static String sha256Hex(File file) throws IOException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new IOException("SHA-256 is not available!", ex);
        }

        byte[] buffer = new byte[8192];
        try (InputStream in = new FileInputStream(file)) {
            int read;
            while ((read = in.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }

        byte[] hash = digest.digest();
        char[] hex = new char[hash.length * 2];
        for (int i = 0; i < hash.length; i++) {
            hex[i * 2] = HEX[(hash[i] >> 4) & 0xF];
            hex[i * 2 + 1] = HEX[hash[i] & 0xF];
        }
        return new String(hex);
    }
}
