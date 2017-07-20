/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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

import esa.mo.nmf.nmfpackage.HelperNMFPackage;
import esa.mo.nmf.nmfpackage.NMFPackageManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cesar Coelho
 */
public class NMFPackageDescriptor {

    private final NMFPackageDetails details;
    private final ArrayList<NMFPackageFile> files;

    public NMFPackageDescriptor(NMFPackageDetails details) {
        this.details = details;
        this.files = new ArrayList<NMFPackageFile>();
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

    public static NMFPackageDescriptor parseInputStream(final InputStream stream) {
        NMFPackageDescriptor newDescriptor = null;
        InputStreamReader isr = new InputStreamReader(stream, Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(isr);

        try {
            String line = br.readLine(); // Reads the first line!

            if (line != null) {
                String version;
                // Check the version of the Installation procedure
                if (line.startsWith(line)) {
                    version = line.substring(HelperNMFPackage.NMF_PACKAGE_DESCRIPTOR_VERSION.length());
                } else {
                    throw new IOException("Could not read the NMF Package Descriptor version!");
                }

                if ("1".equals(version)) {
                    newDescriptor = ReceiptVersion1.readReceiptVersion1(br);
                } else {
                    throw new IOException("Unknown version: " + version);
                }
            } else {
                throw new IOException("The receipt file is empty!");
            }

            br.close();
        } catch (IOException ex) {
            Logger.getLogger(NMFPackageDescriptor.class.getName()).log(Level.SEVERE, "An error happened!", ex);
        }

        return newDescriptor;
    }

}
