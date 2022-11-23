/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft â€“ v2.4
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
 *
 * @author Cesar Coelho
 */
public class HelperNMFPackage {

    public static final String RECEIPT_FILENAME = "nmfPackage.receipt";
    public static final String DS_FILENAME = "digitalSignature.key";
    public static final String PRIVATE_KEY_FILENAME = "privateKey.key";
    public static final String NMF_PACKAGE_DESCRIPTOR_VERSION = "NMFPackageDescriptorVersion=";

    public static long calculateCRCFromFile(final String filepath) throws IOException {
        long crc;
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(filepath))) {
            crc = calculateCRCFromInputStream(inputStream);
        }
        return crc;
    }

    public static long calculateCRCFromInputStream(final InputStream inputStream) throws IOException {
        CRC32 crc = new CRC32();
        int cnt;

        while ((cnt = inputStream.read()) != -1) {
            crc.update(cnt);
        }

        return crc.getValue();
    }

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

            if (!file.getAbsolutePath().endsWith(".jar")) {
                continue; // It is not a Jar file
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

    public static String sanitizePath(final String path) throws IOException {
        // Sanitize the path to prevent a ZipSlip attack:
        if (path.contains("..")) {
            throw new IOException("Warning! A ZipSlip attack was detected!");
        }

        String out = path.replace('/', File.separatorChar);
        return out.replace('\\', File.separatorChar);
    }
}
