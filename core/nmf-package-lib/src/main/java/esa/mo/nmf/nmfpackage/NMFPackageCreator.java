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
package esa.mo.nmf.nmfpackage;

import esa.mo.nmf.nmfpackage.utils.HelperNMFPackage;
import esa.mo.helpertools.misc.Const;
import esa.mo.nmf.nmfpackage.metadata.DetailsApp;
import esa.mo.nmf.nmfpackage.metadata.Metadata;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * The NMF Package Creator class.
 *
 * @author Cesar Coelho
 */
public class NMFPackageCreator {

    private static final int BUFFER = 2048;

    public static void zipFiles(String outputPath, ArrayList<String> from,
            ArrayList<String> newLocations) {
        try {
            BufferedInputStream origin;
            FileOutputStream dest = new FileOutputStream(outputPath);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            byte[] data = new byte[BUFFER];

            for (int i = 0; i < from.size(); i++) {
                String file = from.get(i);
                String newPath = newLocations.get(i);
                System.out.println("    (" + i + ") Selecting file: " + file
                        + "\n    (" + i + ") To NMF Package path: " + newPath);
                origin = new BufferedInputStream(new FileInputStream(file), BUFFER);

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
            Logger.getLogger(NMFPackageCreator.class.getName()).log(
                    Level.SEVERE, "The Files could not be zipped!", ex);
        }
    }

    @Deprecated
    public static String nmfPackageCreator(DetailsApp details,
            ArrayList<String> filesInput, ArrayList<String> newLocationsInput) {
        return NMFPackageCreator.nmfPackageCreator(details, filesInput, newLocationsInput, null);
    }

    @Deprecated
    public static String nmfPackageCreator(DetailsApp details,
            ArrayList<String> filesInput, ArrayList<String> newLocationsInput,
            String destinationFolder) {
        Metadata metadata = new Metadata(details.getProperties());
        return NMFPackageCreator.nmfPackageCreator(metadata, filesInput, newLocationsInput, destinationFolder);
    }

    public static String nmfPackageCreator(Metadata metadata,
            ArrayList<String> filesInput, ArrayList<String> newLocationsInput,
            String destinationFolder) {
        final ArrayList<String> files = new ArrayList<>(filesInput);
        final ArrayList<String> newLocations = new ArrayList<>(newLocationsInput);
        int size = newLocations.size();

        for (int i = 0; i < size; i++) {
            try {
                String path = newLocations.get(i);
                long crc = HelperNMFPackage.calculateCRCFromFile(files.get(i));
                //descriptor.addFile(new NMFPackageFile(path, crc));
                metadata.addProperty(Metadata.FILE_PATH + "." + i, path);
                metadata.addProperty(Metadata.FILE_CRC + "." + i, String.valueOf(crc));
            } catch (IOException ex) {
                Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.SEVERE,
                        "There was a problem during the CRC calculation.", ex);
            }
        }
        
        metadata.addProperty(Metadata.FILE_COUNT, String.valueOf(size));

        // Generate metadata.properties
        Logger.getLogger(NMFPackageCreator.class.getName()).log(
                Level.INFO, "Generating metadata file...");

        File metadataFile = new File(Metadata.FILENAME);
        
        try {
            metadata.store(metadataFile); // Store the metadata file
        } catch (IOException ex) {
            Logger.getLogger(NMFPackageCreator.class.getName()).log(
                    Level.SEVERE, "The file could not be stored!", ex);
        }

        // Add the receipt file to the list of Files to be zipped
        files.add(metadataFile.getPath());
        newLocations.add(Metadata.FILENAME);
        // -------------------------------------------------------------------

        // -------------------------------------------------------------------
        // Generate digital signature
        /*
        Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.INFO,
                "Generating digital signature...");

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
        // -------------------------------------------------------------------

        // Add the signature file to the list of Files to be zipped
        File digitalSignature = new File(HelperNMFPackage.DS_FILENAME);
        files.add((new File(HelperNMFPackage.DS_FILENAME)).getPath());
        newLocations.add(HelperNMFPackage.DS_FILENAME);
         */
        // -------------------------------------------------------------------
        // -------------------------------------------------------------------
        // Compress:
        Logger.getLogger(NMFPackageCreator.class.getName()).log(
                Level.INFO, "Creating compressed NMF Package...");

        String name = metadata.getPackageName() + "-"
                + metadata.getPackageVersion() + "." + Const.NMF_PACKAGE_SUFFIX;
        String destinationPath = (destinationFolder == null) ? name
                : destinationFolder + File.separator + name;
        NMFPackageCreator.zipFiles(destinationPath, files, newLocations);

        // Output the secret privateKey into a file
        /*
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
         */
        // Delete temporary files:
        //receipt.delete();
        metadataFile.delete();
        //digitalSignature.delete();

        return destinationPath;
    }

}
