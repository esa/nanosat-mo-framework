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
package esa.mo.nmf.nmfpackage;

import esa.mo.nmf.nmfpackage.descriptor.NMFPackageDescriptor;
import esa.mo.nmf.nmfpackage.descriptor.NMFPackageDetails;
import esa.mo.nmf.nmfpackage.descriptor.NMFPackageFile;
import esa.mo.nmf.nmfpackage.descriptor.ReceiptVersion1;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * The NMFPackageManager class allows the install, uninstall and upgrade an NMF
 * Package
 *
 * @author Cesar Coelho
 */
public class NMFPackageManager {

    public static void install(final String packageLocation) throws FileNotFoundException, IOException {
        // Get the File to be installed
        ZipFile zipFile = new ZipFile(packageLocation);
        ZipEntry receipt = zipFile.getEntry(HelperNMFPackage.RECEIPT_FILENAME);

        // Verify integrity of the file: Are all the declared files matching their CRCs?
        Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.INFO,
                "Reading the receipt file that includes the list of files to be installed...");

        // Get the text out of that file and parse it into a NMFPackageDescriptor object
        NMFPackageDescriptor descriptor = null;

        InputStream stream = zipFile.getInputStream(receipt);
        InputStreamReader isr = new InputStreamReader(stream, Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(isr);

        try {
            String line = br.readLine(); // Reads the first line!

            if (line != null) {
                String version;
                // Check the version of the Installation procedure
                if (line.startsWith(line)) {
                    version = line.substring(HelperNMFPackage.NMF_PACKAGE_DESCRIPTOR_VERSION.length());
                }else{
                    throw new IOException("Could not read the NMF Package Descriptor verseion!");
                }
                
                if ("1".equals(version)) {
                    descriptor = ReceiptVersion1.readReceiptVersion1(br);
                } else {
                    throw new IOException("Unknown version: " + version);
                }
            } else {
                throw new IOException("The receipt file is empty!");
            }

            br.close();
        } catch (IOException ex) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.SEVERE, "An error happened!", ex);
        }

        // Safety check... should never happen...
        if (descriptor == null) {
            throw new IOException("The parsed descriptor is null.");
        }

        // Verify integrity of the file: Are all the declared files matching their CRCs?
        Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.INFO,
                "Verifying the integrity of the files to be installed...");

        // Do the files actually match the descriptor?
        for (int i = 0; i < descriptor.getFiles().size(); i++){
            NMFPackageFile file = descriptor.getFiles().get(i);
            ZipEntry entry = zipFile.getEntry(file.getPath());

            if(entry == null){
                throw new IOException("The descriptor is incorrect. One of the files does not exist: " + file.getPath());
            }
            
            if(file.getCRC() != entry.getCrc()){
                throw new IOException("The CRC does not match!");
            }
        }

        // Copy the files according to the NMF statement file
        Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.INFO,
                "Copying the files to the new locations...");

        // Delete the temporary folder
    }

    public static void uninstall(final String packageLocation, final boolean keepConfigurations) {
        // Get the Files to be installed

        // Delete the files according to the NMF statement file
        // Do we keep the previous configurations?
    }

    public static void upgrade(final String packageLocation) {
        // Get the Files to be installed

        // Upgrade the files according to the NMF statement file
        // Keep the same configurations
    }

}
