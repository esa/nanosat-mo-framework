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
import esa.mo.nmf.nmfpackage.descriptor.NMFPackageFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    private static final String INSTALLATION_FOLDER_PROPERTY = "esa.mo.nmf.nmfpackage.installationFolder";
    private static final String INSTALLED_RECEIPTS_FOLDER_PROPERTY = "esa.mo.nmf.nmfpackage.receipts";
    private static final String RECEIPT_ENDING = ".receipt";

    public static void install(final String packageLocation) throws FileNotFoundException, IOException {
        // Get the File to be installed
        ZipFile zipFile = new ZipFile(packageLocation);
        ZipEntry receipt = zipFile.getEntry(HelperNMFPackage.RECEIPT_FILENAME);

        // Verify integrity of the file: Are all the declared files matching their CRCs?
        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Reading the receipt file that includes the list of files to be installed...");

        // Get the text out of that file and parse it into a NMFPackageDescriptor object
        final InputStream stream = zipFile.getInputStream(receipt);
        final NMFPackageDescriptor descriptor = NMFPackageDescriptor.parseInputStream(stream);
        stream.close();

        // Safety check... should never happen...
        if (descriptor == null) {
            throw new IOException("The parsed descriptor is null.");
        }

        // Verify integrity of the file: Are all the declared files matching their CRCs?
        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Verifying the integrity of the files to be installed...");

        // Do the files actually match the descriptor?
        for (int i = 0; i < descriptor.getFiles().size(); i++) {
            NMFPackageFile file = descriptor.getFiles().get(i);
            ZipEntry entry = zipFile.getEntry(file.getPath());

            if (entry == null) {
                throw new IOException("The descriptor is incorrect. "
                        + "One of the files does not exist: " + file.getPath());
            }

            if (file.getCRC() != entry.getCrc()) {
                throw new IOException("The CRC does not match!");
            }
        }

        // Copy the files according to the NMF statement file
        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Copying the files to the new locations...");

        installFiles(descriptor, zipFile);

        // ---------------------------------------
        // Store a copy of the receipt to know that it has been installed!
        // Default location of the folder
        File receiptsFolder = getReceiptsFolder();
        String receiptFilename = descriptor.getDetails().getPackageName() + NMFPackageManager.RECEIPT_ENDING;
        File receiptFile = new File(receiptsFolder.getCanonicalPath() + File.separator + receiptFilename);

        //create the file otherwise we get FileNotFoundException
        new File(receiptFile.getParent()).mkdirs();
        // -----------------

        final FileOutputStream fos = new FileOutputStream(receiptFile);
        final InputStream zis = zipFile.getInputStream(receipt);
        byte[] buffer = new byte[1024];
        int len;

        while ((len = zis.read(buffer)) > 0) {
            fos.write(buffer, 0, len);
        }

        fos.close();
        zis.close();
        // ---------------------------------------

        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Package successfully installed from location: " + packageLocation);
    }

    public static void uninstall(final String packageLocation, final boolean keepConfigurations) throws IOException {
        // Get the Package to be uninstalled
        ZipFile zipFile = new ZipFile(packageLocation);
        ZipEntry receipt = zipFile.getEntry(HelperNMFPackage.RECEIPT_FILENAME);

        // Verify integrity of the file: Are all the declared files matching their CRCs?
        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Reading the receipt file that includes the list of files to be uninstalled...");

        // Get the text out of that file and parse it into a NMFPackageDescriptor object
        final InputStream stream = zipFile.getInputStream(receipt);
        final NMFPackageDescriptor descriptor = NMFPackageDescriptor.parseInputStream(stream);
        stream.close();

        // Safety check... should never happen...
        if (descriptor == null) {
            throw new IOException("The parsed descriptor is null.");
        }

        // Verify integrity of the file: Are all the declared files matching their CRCs?
        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Removing the files...");

        removeFiles(descriptor);

        File receiptsFolder = getReceiptsFolder();
        String receiptFilename = descriptor.getDetails().getPackageName() + NMFPackageManager.RECEIPT_ENDING;
        File receiptFile = new File(receiptsFolder.getCanonicalPath() + File.separator + receiptFilename);

        if (!receiptFile.delete()) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.WARNING,
                    "The receipt file could not be deleted from: " + receiptFile.getCanonicalPath());
        }
        // ---------------------------------------

        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Package successfully uninstalled from location: " + packageLocation);
    }

    public static void upgrade(final String packageLocation) throws IOException {
        // Get the Package to be uninstalled
        ZipFile zipFile = new ZipFile(packageLocation);
        ZipEntry receipt = zipFile.getEntry(HelperNMFPackage.RECEIPT_FILENAME);

        // Verify integrity of the file: Are all the declared files matching their CRCs?
        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Reading the receipt file that includes the list of files to be upgraded...");

        // Get the text out of that file and parse it into a NMFPackageDescriptor object
        final InputStream stream = zipFile.getInputStream(receipt);
        final NMFPackageDescriptor descriptorFromPackage = NMFPackageDescriptor.parseInputStream(stream);
        stream.close();

        // Safety check... should never happen...
        if (descriptorFromPackage == null) {
            throw new IOException("The parsed descriptor is null.");
        }

        File receiptsFolder = getReceiptsFolder();
        String receiptFilename = descriptorFromPackage.getDetails().getPackageName() + NMFPackageManager.RECEIPT_ENDING;
        File receiptFile = new File(receiptsFolder.getCanonicalPath() + File.separator + receiptFilename);

        // Get the text out of that file and parse it into a NMFPackageDescriptor object
        final InputStream stream2 = new FileInputStream(receiptFile);
        final NMFPackageDescriptor descriptor = NMFPackageDescriptor.parseInputStream(stream2);
        stream2.close();

        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Upgrading from version: '" + descriptor.getDetails().getVersion() + "'"
                + "   To version: '" + descriptorFromPackage.getDetails().getVersion() + "'");

        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Removing the previous files...");

        removeFiles(descriptor);

        if (!receiptFile.delete()) { // The file could not be deleted...
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.WARNING,
                    "The receipt file could not be deleted from: " + receiptFile.getCanonicalPath());
        }

        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Copying the new files to the locations...");

        installFiles(descriptor, zipFile);

        // ---------------------------------------
        // Store a copy of the receipt to know that it has been installed!
        //create the file otherwise we get FileNotFoundException
        new File(receiptFile.getParent()).mkdirs();
        // -----------------

        final FileOutputStream fos = new FileOutputStream(receiptFile);
        final InputStream zis = zipFile.getInputStream(receipt);
        byte[] buffer = new byte[1024];
        int len;

        while ((len = zis.read(buffer)) > 0) {
            fos.write(buffer, 0, len);
        }

        fos.close();
        zis.close();
        // ---------------------------------------

        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Package successfully upgraded from location: " + packageLocation);
    }

    public static boolean isPackageInstalled(final String packageLocation) {
        // based on the name, we have to go to the receipts folder and check!

        // Find the receipt and get it out of the package
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(packageLocation);
        } catch (IOException ex) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        ZipEntry receipt = zipFile.getEntry(HelperNMFPackage.RECEIPT_FILENAME);
        NMFPackageDescriptor descriptorFromPackage;
        long crcDescriptorFromPackage;

        try {
            InputStream zis = zipFile.getInputStream(receipt);
            descriptorFromPackage = NMFPackageDescriptor.parseInputStream(zis);
            zis.close();
            zis = zipFile.getInputStream(receipt);
            crcDescriptorFromPackage = HelperNMFPackage.calculateCRCFromInputStream(zis);
            zis.close();
        } catch (IOException ex) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        File temp = getReceiptsFolder();
        String receiptFilename = descriptorFromPackage.getDetails().getPackageName() + NMFPackageManager.RECEIPT_ENDING;
        File receiptFile;
        try {
            receiptFile = new File(temp.getCanonicalPath() + File.separator + receiptFilename);
        } catch (IOException ex) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        boolean exists = receiptFile.exists();

        if (!exists) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.FINE,
                    "The package "
                    + descriptorFromPackage.getDetails().getPackageName()
                    + " is not installed!");
            return false;
        }

        // Check the version of the installed receipt
        NMFPackageDescriptor descriptorFromExistingReceipt;
        long crcDescriptorFromExistingReceipt;

        try {
            FileInputStream fis = new FileInputStream(receiptFile);
            descriptorFromExistingReceipt = NMFPackageDescriptor.parseInputStream(fis);
            fis.close();
            fis = new FileInputStream(receiptFile);
            crcDescriptorFromExistingReceipt = HelperNMFPackage.calculateCRCFromInputStream(fis);
            fis.close();
        } catch (IOException ex) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        if (descriptorFromExistingReceipt == null) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.SEVERE,
                    "This is unexpected!");
            return false;
        }

        // Compare the versions
        String version = descriptorFromExistingReceipt.getDetails().getVersion();
        if (!descriptorFromPackage.getDetails().getVersion().equals(version)) {
            return false;
        }

        // We need to double check if the crc match!
        if (crcDescriptorFromPackage != crcDescriptorFromExistingReceipt) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.SEVERE,
                    "The CRC of the receipts do not match!"
                    + "Maybe the NMF Package is tainted!");
            return false;
        }

        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.FINE,
                "The package "
                + descriptorFromPackage.getDetails().getPackageName()
                + " is installed!");

        return true;
    }

    private static File getReceiptsFolder() {
        // Default location of the folder
        File folder = new File("receipts");

        // Read the Property of the folder to install the packages
        if (System.getProperty(INSTALLED_RECEIPTS_FOLDER_PROPERTY) != null) {
            folder = new File(System.getProperty(INSTALLED_RECEIPTS_FOLDER_PROPERTY));
        }

        return folder;
    }

    private static File getInstallationFolder() {
        // Default location of the folder
        File folder = new File(".." + File.separator + ".." + File.separator);

        // Read the Property of the folder to install the packages
        if (System.getProperty(INSTALLATION_FOLDER_PROPERTY) != null) {
            folder = new File(System.getProperty(INSTALLATION_FOLDER_PROPERTY));
        }

        return folder;
    }

    private static String generateFilePathForSystem(final String path) {
        String out = path.replace('/', File.separatorChar);
        String out2 = out.replace('\\', File.separatorChar);
        return out2;
    }

    private static void installFiles(final NMFPackageDescriptor descriptor,
            final ZipFile zipFile) throws IOException {
        // Default location of the folder
        File installationFolder = getInstallationFolder();

        File newFile;
        byte[] buffer = new byte[1024];

        // Iterate through the files, unpack them into the right folders
        for (int i = 0; i < descriptor.getFiles().size(); i++) {
            NMFPackageFile file = descriptor.getFiles().get(i);
            ZipEntry entry = zipFile.getEntry(file.getPath());

            final String path = generateFilePathForSystem(entry.getName());
            newFile = new File(installationFolder.getCanonicalPath() + File.separator + path);
            new File(newFile.getParent()).mkdirs();

            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                    "Copying the file to location: " + newFile.getCanonicalPath());

            final FileOutputStream fos = new FileOutputStream(newFile);

            int len;
            final InputStream zis = zipFile.getInputStream(entry);

            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }

            fos.close();

            final long crc = HelperNMFPackage.calculateCRCFromFile(newFile.getCanonicalPath());

            // We will also need to double check the CRCs again against the real files!
            // Just to double-check.. better safe than sorry!
            if (file.getCRC() != crc) {
                throw new IOException("The CRC does not match!");
            }
        }
    }

    private static void removeFiles(final NMFPackageDescriptor descriptor) throws IOException {
        File folder = getInstallationFolder();
        File file;

        // Do the files actually match the descriptor?
        for (int i = 0; i < descriptor.getFiles().size(); i++) {
            NMFPackageFile packageFile = descriptor.getFiles().get(i);

            final String path = generateFilePathForSystem(packageFile.getPath());
            file = new File(folder.getCanonicalPath() + File.separator + path);

            if (!file.exists()) {
                Logger.getLogger(NMFPackageManager.class.getName()).log(Level.WARNING,
                        "The file no longer exists: " + file.getCanonicalPath());
            } else {
                // The file exists!

                // Check the CRC
                long crc = HelperNMFPackage.calculateCRCFromFile(file.getCanonicalPath());

                if (packageFile.getCRC() != crc) {
                    Logger.getLogger(NMFPackageManager.class.getName()).log(Level.WARNING,
                            "The CRC does not match for file: " + file.getCanonicalPath());
                }

                // Remove the file!
                if (!file.delete()) {
                    Logger.getLogger(NMFPackageManager.class.getName()).log(Level.WARNING,
                            "One of the files could not be deleted: " + file.getCanonicalPath());
                }
            }
        }
    }

    public static String getPublicKey(String folderLocation) {
        /*
        // Find the receipt and get it out of the package
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(packageLocation);
        } catch (IOException ex) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
         */

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
