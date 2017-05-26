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
package esa.mo.nmf.packager;

import esa.mo.helpertools.misc.HelperNMF;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Cesar Coelho
 */
public class NMFPackageCreator {

    public static final String RECEIPT_FILENAME = "nmfPackage.receipt";
    public static final String DS_FILENAME = "digitalSignature.key";
    public static final String PRIVATE_KEY_FILENAME = "privateKey.key";
    private static final int BUFFER = 2048;

    private static void zipFiles(String outputPath, ArrayList<String> from, ArrayList<String> newLocations) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(outputPath);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            //out.setMethod(ZipOutputStream.DEFLATED);
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < from.size(); i++) {
                String file = from.get(i);
                String newPath = newLocations.get(i);
                System.out.println("Adding file: " + file + "\nOn path: " + newPath);
                FileInputStream fi = new FileInputStream(file);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(newPath);
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.close();
        } catch (Exception ex) {
            Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void nmfPackageCreator(NMFPackageDetails details, ArrayList<String> files, ArrayList<String> newLocations) {
        // Get the Files to be installed

        // -----------------------------------------------------------------------------------
        // Generate nmfPackage.receipt
        Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.INFO, "Generating receipt file...");

        try { // Write down all the new paths
            FileOutputStream sigfos = new FileOutputStream(RECEIPT_FILENAME);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(sigfos));

            // Iterate the newLocations and write them down on the file
            for (String location : newLocations) {
                bw.write(location);
                bw.newLine();
            }

            bw.flush();
            sigfos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Add the receipt file to the list of Files to be zipped
        File receipt = new File(RECEIPT_FILENAME);
        files.add(receipt.getPath());
        newLocations.add(RECEIPT_FILENAME);
        // -----------------------------------------------------------------------------------

        // Generate digital signature
        Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.INFO, "Generating digital signature...");

        // Generate Public and Private keys
        KeyPair pair = NMFDigitalSignature.generateKeyPar();

        // Generate the SHA
        byte[] signature = NMFDigitalSignature.signWithData(pair.getPrivate(), RECEIPT_FILENAME);

        // Write the signature to the file: DS_FILENAME
        try {
            FileOutputStream sigfos = new FileOutputStream(DS_FILENAME);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(sigfos));
            bw.write(javax.xml.bind.DatatypeConverter.printHexBinary(signature));
            bw.flush();
            sigfos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Add the signature file to the list of Files to be zipped
        File digitalSignature = new File(DS_FILENAME);
        files.add((new File(DS_FILENAME)).getPath());
        newLocations.add(DS_FILENAME);
        // -----------------------------------------------------------------------------------

        // Compress the damm thing:
        Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.INFO, "Compressing...");

        String packageOutputPath = details.getPackageName() + "-" + details.getVersion() + "." + HelperNMF.NMF_PACKAGE_SUFFIX;
        NMFPackageCreator.zipFiles(packageOutputPath, files, newLocations);

        // Output the secret privateKey into a file
        try {
            byte[] key = pair.getPrivate().getEncoded();
            FileOutputStream keyfos = new FileOutputStream(PRIVATE_KEY_FILENAME);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(keyfos));
            bw.write(javax.xml.bind.DatatypeConverter.printHexBinary(key));
            bw.flush();
            keyfos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Delete temporary files:
        receipt.delete();
        digitalSignature.delete();
    }

}
