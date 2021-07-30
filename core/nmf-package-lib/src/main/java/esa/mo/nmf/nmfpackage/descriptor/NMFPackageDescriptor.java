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

import esa.mo.nmf.nmfpackage.receipt.ReceiptVersion1;
import esa.mo.nmf.nmfpackage.receipt.ReceiptVersion2;
import esa.mo.nmf.nmfpackage.HelperNMFPackage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 *
 * @author Cesar Coelho
 */
public class NMFPackageDescriptor {

    private final NMFPackageDetails details;
    private final ArrayList<NMFPackageFile> files;

    public NMFPackageDescriptor(NMFPackageDetails details) {
        this.details = details;
        this.files = new ArrayList<>();
    }

    public NMFPackageDetails getDetails() {
        return details;
    }

    public ArrayList<NMFPackageFile> getFiles() {
        return files;
    }

    public void addFile(final NMFPackageFile file) {
        this.files.add(file);
    }

    /**
     * Parses a receipt file and creates the respective NMFPackageDescriptor.
     *
     * @param stream The input stream of the file.
     * @return The descriptor of the NMF Package.
     */
    public static NMFPackageDescriptor parseInputStream(final InputStream stream) throws IOException {
        NMFPackageDescriptor newDescriptor = null;
        InputStreamReader isr = new InputStreamReader(stream, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);

            String line = br.readLine(); // Reads the first line!

            if (line != null) {
                String version;
                // Check the version of the Installation procedure
                if (line.startsWith(line)) {
                    int length = HelperNMFPackage.NMF_PACKAGE_DESCRIPTOR_VERSION.length();
                    version = line.substring(length).trim();
                } else {
                    throw new IOException("Could not read the NMF Package Descriptor version!");
                }

                if ("1".equals(version)) {
                    newDescriptor = ReceiptVersion1.readReceipt(br);
                }
                
                if ("2".equals(version)) {
                    newDescriptor = ReceiptVersion2.readReceipt(br);
                } else {
                    throw new IOException("Unknown version: " + version);
                }
            } else {
                throw new IOException("The receipt file is empty!");
            }

            br.close();

        return newDescriptor;
    }

}
