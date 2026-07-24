/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft - v2.4
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.CRC32;

/**
 * Utility methods for handling NMF Packages: CRC computation, locating the App JAR inside a
 * folder and sanitizing zip entry paths.
 *
 * @author Cesar Coelho
 */
public class HelperNMFPackage {

    /** File name of the legacy package receipt. */
    public static final String RECEIPT_FILENAME = "nmfPackage.receipt";
    /** File name of the package digital signature. */
    public static final String DS_FILENAME = "digitalSignature.key";
    /** File name of the signing private key. */
    public static final String PRIVATE_KEY_FILENAME = "privateKey.key";
    /** Prefix of the descriptor-version line in the legacy receipt. */
    public static final String NMF_PACKAGE_DESCRIPTOR_VERSION = "NMFPackageDescriptorVersion=";

    /**
     * Computes the CRC-32 checksum of a file.
     *
     * @param filepath the path to the file
     * @return the CRC-32 checksum
     * @throws IOException if the file cannot be read
     */
    public static long calculateCRCFromFile(final String filepath) throws IOException {
        long crc;
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(filepath))) {
            crc = calculateCRCFromInputStream(inputStream);
        }
        return crc;
    }

    private HelperNMFPackage() {
    }

    /**
     * Computes the CRC-32 checksum of the bytes read from an input stream.
     *
     * @param inputStream the stream to read
     * @return the CRC-32 checksum
     * @throws IOException if the stream cannot be read
     */
    public static long calculateCRCFromInputStream(final InputStream inputStream) throws IOException {
        CRC32 crc = new CRC32();
        int cnt;

        while ((cnt = inputStream.read()) != -1) {
            crc.update(cnt);
        }

        return crc.getValue();
    }

    /**
     * Finds the application JAR inside a folder, skipping directories and auxiliary artifacts
     * such as {@code -sources}, {@code -javadoc} and {@code -tests} JARs. When both a plain and
     * a {@code -jar-with-dependencies} JAR are present, the latter is returned.
     *
     * @param folder the folder to search
     * @return the application JAR file
     * @throws IOException if the folder is missing, contains no JAR, or is ambiguous
     */
    public static File findAppJarInFolder(File folder) throws IOException {
        File[] fList = folder.listFiles();

        if (fList == null) {
            throw new IOException("The directory was not found: " + folder.getAbsolutePath());
        }

        ArrayList<File> possibleOptions = new ArrayList();

        for (File file : fList) {
            if (file.isDirectory()) {
                continue; // Jump over if it is a directory
            }

            String name = file.getName();
            if (!name.endsWith(".jar")) {
                continue; // It is not a Jar file
            }

            // Skip auxiliary artifacts (e.g. the -sources/-javadoc jars produced
            // by the release profile); they are never the application jar.
            if (name.endsWith("-javadoc.jar") || name.endsWith("-sources.jar")
                    || name.endsWith("-tests.jar")) {
                continue;
            }

            possibleOptions.add(file);
        }

        if (possibleOptions.isEmpty()) {
            throw new IOException("Not found!");
        }

        if (possibleOptions.size() == 1) {
            return possibleOptions.get(0);
        }

        if (possibleOptions.size() == 2) {
            for (File option : possibleOptions) {
                if (option.getName().contains("-jar-with-dependencies.jar")) {
                    return option;
                }
            }
        }

        throw new IOException("There are too many jars inside the target folder!");
    }

    /**
     * Normalizes a zip entry path to the platform separator, rejecting paths that could be
     * used for a Zip Slip attack.
     *
     * @param path the zip entry path
     * @return the sanitized, platform-specific path
     * @throws IOException if the path contains {@code ..} (a Zip Slip attempt)
     */
    public static String sanitizePath(final String path) throws IOException {
        // Sanitize the path to prevent a ZipSlip attack:
        if (path.contains("..")) {
            throw new IOException("Warning! A ZipSlip attack was detected!");
        }

        String out = path.replace('/', File.separatorChar);
        return out.replace('\\', File.separatorChar);
    }
}
