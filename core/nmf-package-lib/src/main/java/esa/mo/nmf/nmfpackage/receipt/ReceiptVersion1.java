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
package esa.mo.nmf.nmfpackage.receipt;

import esa.mo.nmf.nmfpackage.NMFPackageFile;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 *
 * @author Cesar Coelho
 */
@Deprecated
public class ReceiptVersion1 {

    private static final String PACKAGE_NAME = "PackageName=";
    private static final String PACKAGE_VERSION = "PackageVersion=";
    private static final String PACKAGE_TIMESTAMP = "PackageCreationTimestamp=";
    private static final String FILE_PATH = "FilePath=";
    private static final String FILE_CRC = "FileCRC=";

    public static NMFPackageDescriptor readReceipt(final BufferedReader br) throws IOException {
        String name = null;
        String version = null;
        String timestamp = null;

        String line;
        line = readLineSafe(br);

        if (line.startsWith(line)) {
            name = line.substring(PACKAGE_NAME.length());
        } else {
            throw new IOException("Could not read the package name!");
        }

        line = readLineSafe(br);

        if (line.startsWith(line)) {
            version = line.substring(PACKAGE_VERSION.length());
        } else {
            throw new IOException("Could not read the package version!");
        }

        line = readLineSafe(br);

        if (line.startsWith(line)) {
            timestamp = line.substring(PACKAGE_TIMESTAMP.length());
        } else {
            throw new IOException("Could not read the package timestamp!");
        }

        final DetailsApp details = new DetailsApp(name,
                version, timestamp, "", "", "96m", null);

        final NMFPackageDescriptor descriptor = new NMFPackageDescriptor(details);
        String path;
        long crc;

        // Deal with the rest of the lines
        while ((line = br.readLine()) != null) {
            if (line.startsWith(line)) {
                path = line.substring(FILE_PATH.length());
            } else {
                throw new IOException("Could not read the path!");
            }

            line = readLineSafe(br);

            if (line.startsWith(line)) {
                crc = Long.parseLong(line.substring(FILE_CRC.length()));
            } else {
                throw new IOException("Could not read the crc!");
            }

            descriptor.addFile(new NMFPackageFile(path, crc));
        }

        return descriptor;
    }

    public static void writeReceipt(final BufferedWriter bw,
            final NMFPackageDescriptor descriptor) throws IOException {
        bw.write(PACKAGE_NAME + descriptor.getDetails().getPackageName());
        bw.newLine();
        bw.write(PACKAGE_VERSION + descriptor.getDetails().getVersion());
        bw.newLine();
        bw.write(PACKAGE_TIMESTAMP + descriptor.getDetails().getTimestamp());
        bw.newLine();

        // Iterate the newLocations and write them down on the file
        for (NMFPackageFile f : descriptor.getFiles()) {
            bw.write(FILE_PATH + f.getPath());
            bw.newLine();
            bw.write(FILE_CRC + f.getCRC());
            bw.newLine();
        }
    }

    private static String readLineSafe(final BufferedReader br) throws IOException {
        String line = br.readLine();

        if (line == null) {
            throw new IOException("The line is null! It happens when it reaches the end of the file!");
        }

        return line;
    }

}
