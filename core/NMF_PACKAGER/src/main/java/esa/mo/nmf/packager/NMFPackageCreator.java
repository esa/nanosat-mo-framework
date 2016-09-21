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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Cesar Coelho
 */
public class NMFPackageCreator {

    public String RECEIPT_FILENAME = "nmfPackage.receipt";
    public String DS_FILENAME = "digitalSignature.key";
    public String PRIVATE_KEY_FILENAME = "privateKey.key";
    public String NMF_PACKAGE_SUFFIX = "nmfpack";
    private final int BUFFER = 2048;


    private void zipFiles(String outputPath, String files[]) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(outputPath);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            //out.setMethod(ZipOutputStream.DEFLATED);
            byte data[] = new byte[BUFFER];

            for (String file : files) {
                System.out.println("Adding: " + file);
                FileInputStream fi = new FileInputStream(file);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(file);
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void nmfPackageCreator(){
        // Get the Files to be installed
        
        // Create a temporary folder for the package...
        
        // Put them in the right folder
        
        // Generate nmfPackage.receipt
        File receipt = new File(RECEIPT_FILENAME);
        
        // Generate digital signature
            // Generate Public and Private keys
            KeyPair pair = NMFDigitalSignature.generateKeyPar();
        
            // Generate the SHA
            byte [] signature = NMFDigitalSignature.signWithData(pair.getPrivate(), RECEIPT_FILENAME);
        
            // Write the signature to the file: DS_FILENAME
            try {
                FileOutputStream sigfos = new FileOutputStream(DS_FILENAME);
                sigfos.write(signature);
                sigfos.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
            Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.SEVERE, null, ex);
        }

            
        // Compress the damm thing:
        String version = "1.00";
        String packageName = "demo12345";
        String packageOutputPath = packageName + "-" + version + "." + NMF_PACKAGE_SUFFIX;
        
        String[] files = null;
        this.zipFiles(packageOutputPath, files);
        
        // Output the secret privateKey into a file
        try {
            byte[] key = pair.getPrivate().getEncoded();
            FileOutputStream keyfos = new FileOutputStream(PRIVATE_KEY_FILENAME);
            keyfos.write(key);
            keyfos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

}
