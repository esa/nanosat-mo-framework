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

import esa.mo.nmf.nmfpackage.descriptor.NMFPackageDetails;
import esa.mo.helpertools.misc.HelperNMF;
import esa.mo.nmf.nmfpackage.descriptor.NMFPackageDescriptor;
import esa.mo.nmf.nmfpackage.descriptor.NMFPackageFile;
import esa.mo.nmf.nmfpackage.descriptor.ReceiptVersion1;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Cesar Coelho
 */
public class NMFPackageCreator {

    private static final int BUFFER = 2048;

    private static ArrayList<Long> zipFiles(String outputPath, ArrayList<String> from, ArrayList<String> newLocations) {
        ArrayList<Long> list = new ArrayList<Long>();

        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(outputPath);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < from.size(); i++) {
                String file = from.get(i);
                String newPath = newLocations.get(i);
                System.out.println("Adding file: " + file + "\nOn path: " + newPath);
                origin = new BufferedInputStream(new FileInputStream(file), BUFFER);

                ZipEntry entry = new ZipEntry(newPath);
                long crc = calculateCRC(file);
                list.add(crc);
//                entry.setCrc(calculateCRC(file));
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
        
        return list;
    }

    public static void nmfPackageCreator(NMFPackageDetails details, ArrayList<String> files, ArrayList<String> newLocations) {
        NMFPackageDescriptor descriptor = new NMFPackageDescriptor(details);
        
        for(int i = 0; i < newLocations.size(); i++){
            try {
                descriptor.addFile(new NMFPackageFile(newLocations.get(i), calculateCRC(files.get(i))));
            } catch (IOException ex) {
                Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
                
        // -----------------------------------------------------------------------------------
        // Generate nmfPackage.receipt
        Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.INFO, "Generating receipt file...");

        try { // Write down all the new paths
            FileOutputStream sigfos = new FileOutputStream(HelperNMFPackage.RECEIPT_FILENAME);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(sigfos));
            bw.write(HelperNMFPackage.NMF_PACKAGE_DESCRIPTOR_VERSION + "1");
            bw.newLine();
            ReceiptVersion1.writeReceiptVersion1(bw, descriptor);
            bw.flush();
            sigfos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Add the receipt file to the list of Files to be zipped
        File receipt = new File(HelperNMFPackage.RECEIPT_FILENAME);
        files.add(receipt.getPath());
        newLocations.add(HelperNMFPackage.RECEIPT_FILENAME);
        // -----------------------------------------------------------------------------------

        // -----------------------------------------------------------------------------------        
        // Generate digital signature
        Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.INFO, "Generating digital signature...");

        // Generate Public and Private keys
        KeyPair pair = NMFDigitalSignature.generateKeyPar();
        
        // Generate the SHA (can only be done after having the receipt file)
        byte[] signature = NMFDigitalSignature.signWithData(pair.getPrivate(), HelperNMFPackage.RECEIPT_FILENAME);

        // Write the signature to the file: DS_FILENAME
        try {
            FileOutputStream sigfos = new FileOutputStream(HelperNMFPackage.DS_FILENAME);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(sigfos));
            bw.write(javax.xml.bind.DatatypeConverter.printHexBinary(signature));
            bw.flush();
            sigfos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        // -----------------------------------------------------------------------------------        

        // Add the signature file to the list of Files to be zipped
        File digitalSignature = new File(HelperNMFPackage.DS_FILENAME);
        files.add((new File(HelperNMFPackage.DS_FILENAME)).getPath());
        newLocations.add(HelperNMFPackage.DS_FILENAME);
        // -----------------------------------------------------------------------------------        
        

        // -----------------------------------------------------------------------------------        
        // Compress:
        Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.INFO, "Compressing...");

        String packageOutputPath = details.getPackageName() + "-" + details.getVersion() + "." + HelperNMF.NMF_PACKAGE_SUFFIX;
        ArrayList<Long> crcs = NMFPackageCreator.zipFiles(packageOutputPath, files, newLocations);

        
        // Output the secret privateKey into a file
        try {
            byte[] key = pair.getPrivate().getEncoded();
            FileOutputStream keyfos = new FileOutputStream(HelperNMFPackage.PRIVATE_KEY_FILENAME);
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
    
    public static long calculateCRC(final String filepath) throws IOException {
        InputStream inputStream = new BufferedInputStream(new FileInputStream(filepath));
        CRC32 crc = new CRC32();
        int cnt;

        while ((cnt = inputStream.read()) != -1) {
            crc.update(cnt);
        }
        return crc.getValue();
    }

}
