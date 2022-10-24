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

import esa.mo.nmf.nmfpackage.utils.LinuxUsersGroups;
import esa.mo.nmf.nmfpackage.utils.HelperNMFPackage;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.helpertools.misc.Const;
import esa.mo.helpertools.misc.OSValidator;
import esa.mo.nmf.nmfpackage.metadata.Metadata;
import esa.mo.nmf.nmfpackage.metadata.MetadataApp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * The NMFPackageManager class allows the install, uninstall and upgrade an NMF
 * Package.
 *
 * @author Cesar Coelho
 */
public class NMFPackageManager {

    @Deprecated
    private static final String INSTALLATION_FOLDER_PROPERTY = "esa.mo.nmf.nmfpackage.installationFolder";

    private static final String INSTALLED_RECEIPTS_FOLDER_PROPERTY = "esa.mo.nmf.nmfpackage.receipts";

    private static final String RECEIPT_ENDING = ".receipt";

    // This must match the group_nmf_apps value in the fresh_install.sh file
    private static final String GROUP_NMF_APPS = "nmf-apps";

    private static final String USER_NMF_ADMIN = "nmf-admin";

    private static final String USER_NMF_APP_PREFIX = "app-";

    private static final String SEPARATOR = "--------------\n";

    private static final String FOLDER_APPS = "apps";

    private static final String FOLDER_RECEIPTS = "installation_receipts";

    /**
     * Installs an NMF Package on the specified NMF root folder.
     *
     * @param packageLocation The NMF Package location
     * @param nmfDir The NMF root directory
     * @throws FileNotFoundException if the file was not found
     * @throws IOException if there was a problem with the NMF Package
     */
    public static void install(final String packageLocation,
            final File nmfDir) throws FileNotFoundException, IOException {
        System.out.printf(SEPARATOR);
        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Reading the package file to be installed...");

        // Get the File to be installed
        NMFPackage pack = new NMFPackage(packageLocation);
        Metadata metadata = pack.getMetadata();

        if (metadata.getMetadataVersion() < 3) {
            throw new IOException("The package version is deprecated! "
                    + "Version: " + metadata.getMetadataVersion());
        }

        // Verify integrity of the file: Are all the declared files matching their CRCs?
        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Verifying the integrity of the files to be installed...");

        // Do the files actually match the descriptor?
        pack.verifyPackageIntegrity();

        // Copy the files according to the NMF statement file
        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Copying the files to the new locations...");

        if(metadata.isApp()){
            installDependencies(metadata.castToApp(), packageLocation, nmfDir);
        }
        
        extractFiles(pack, nmfDir);
        String packageName = metadata.getPackageName();

        if (metadata.isApp()) {
            // We can do additional checks... for example: 
            //      1. Are we trying to install more than one App?
            //      2. Does the app name on the package matches the folder name?
            // --------------------------------------------------------------------

            // This directory should be passed in the method signature:
            File installationDir = new File(nmfDir.getAbsolutePath()
                    + File.separator + FOLDER_APPS
                    + File.separator + packageName);

            String username = null;
            String password = null;

            if ((new OSValidator()).isUnix()) {
                try {
                    // Create the User for this App
                    username = generateUsername(packageName);
                    boolean withGroup = true;
                    LinuxUsersGroups.adduser(username, password, withGroup);
                    LinuxUsersGroups.addUserToGroup(username, GROUP_NMF_APPS);
                    //LinuxUsersGroups.useradd(username, password, withGroup, GROUP_NMF_APPS);

                    // Set the right Group and Permissions to the Home Directory
                    // The owner remains with the app, the group is nmf-admin
                    String homeDir = LinuxUsersGroups.findHomeDir(username);
                    LinuxUsersGroups.chgrp(true, USER_NMF_ADMIN, homeDir);
                    LinuxUsersGroups.chmod(true, true, "770", homeDir);
                } catch (IOException ex) {
                    Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                            "The User could not be created: " + username, ex);
                    username = null;
                }

                HelperNMFPackage.generateStartScript(metadata.castToApp(), installationDir, nmfDir);
            }

            createAuxiliaryFiles(installationDir, username);

            if ((new OSValidator()).isUnix()) {
                // Change Group owner of the installationDir
                if (username != null) {
                    LinuxUsersGroups.chgrp(true, username, installationDir.getAbsolutePath());
                }

                // chmod the installation directory with recursive
                LinuxUsersGroups.chmod(false, true, "750", installationDir.getAbsolutePath());
            }
        }

        // Store a copy of the newReceipt to know that it has been installed!
        File receiptsFolder = getReceiptsFolder();
        String receiptFilename = packageName + RECEIPT_ENDING;
        String receiptPath = receiptsFolder.getCanonicalPath() + File.separator + receiptFilename;
        File receiptFile = new File(receiptPath);
        metadata.store(receiptFile);

        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Package successfully installed from: {0}", packageLocation);

        System.out.printf(SEPARATOR);
    }

    /**
     * Uninstalls an NMF Package using the respective package descriptor.
     *
     * @param descriptor The descriptor
     * @param keepUserData A flag that sets if the user data is kept
     * @throws IOException if there was a problem during the uninstallation
     */
    public static void uninstall(final Metadata packageMetadata,
            final boolean keepUserData) throws IOException {
        System.out.printf(SEPARATOR);
        Logger.getLogger(NMFPackageManager.class.getName()).log(
                Level.INFO, "Removing the files...");

        removeFiles(packageMetadata);
        String packageName = packageMetadata.getPackageName();

        if (packageMetadata.isApp()) {
            // This directory should be passed in the method signature:
            File nmfDir = getInstallationFolder();
            File installationDir = new File(nmfDir.getAbsolutePath()
                    + File.separator + FOLDER_APPS
                    + File.separator + packageName);

            removeAuxiliaryFiles(installationDir, packageName);

            String receiptsPath = getReceiptsFolder().getCanonicalPath();
            String receiptFilename = packageName + RECEIPT_ENDING;
            File receiptFile = new File(receiptsPath + File.separator + receiptFilename);

            if (!receiptFile.delete()) {
                Logger.getLogger(NMFPackageManager.class.getName()).log(Level.WARNING,
                        "The receipt file could not be deleted from: " + receiptsPath);
            }
            // ---------------------------------------

            if ((new OSValidator()).isUnix()) {
                if (!keepUserData) {
                    String username = generateUsername(packageName);
                    LinuxUsersGroups.deluser(username, true);
                }
            }
        }

        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Package successfully uninstalled: " + packageName);

        System.out.printf(SEPARATOR);
    }

    /**
     * Upgrades an NMF Package with a newer version.
     *
     * @param packageLocation The NMF Package location
     * @param nmfDir The NMF root directory
     * @throws IOException if the package could not be upgraded
     */
    public static void upgrade(final String packageLocation, final File nmfDir) throws IOException {
        System.out.printf(SEPARATOR);
        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
            "Reading the receipt file that includes the list of files to be upgraded...");

        // Get the Package to be uninstalled
        NMFPackage newPack = new NMFPackage(packageLocation);
        Metadata newPackMetadata = newPack.getMetadata();

        if (newPackMetadata.getMetadataVersion() < 3) {
            throw new IOException("The package version is deprecated! "
                    + "Version: " + newPackMetadata.getMetadataVersion());
        }

        File receiptsFolder = getReceiptsFolder();
        String receiptFilename = newPackMetadata.getPackageName() + RECEIPT_ENDING;
        File oldReceiptFile = new File(receiptsFolder.getCanonicalPath() + File.separator + receiptFilename);

        // Get the text out of that file and parse it into a NMFPackageDescriptor object
        InputStream stream2 = new FileInputStream(oldReceiptFile);
        Metadata oldPackMetadata = Metadata.load(stream2);
        stream2.close();

        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Upgrading..."
                + "\n  >> From version: " + oldPackMetadata.getPackageVersion()
                + " (timestamp: " + oldPackMetadata.getPackageTimestamp() + ")"
                + "\n  >>   To version: " + newPackMetadata.getPackageVersion()
                + " (timestamp: " + newPackMetadata.getPackageTimestamp() + ")"
        );

        Logger.getLogger(NMFPackageManager.class.getName()).log(
                Level.INFO, "Removing the previous files...");

        removeFiles(oldPackMetadata);

        String packageName = oldPackMetadata.getPackageName();
        boolean isApp = oldPackMetadata.isApp();

        if (isApp) {
            // This directory should be passed in the method signature:
            File installationDir = new File(nmfDir.getAbsolutePath()
                    + File.separator + FOLDER_APPS
                    + File.separator + packageName);

            removeAuxiliaryFiles(installationDir, packageName);

            if (!oldReceiptFile.delete()) { // The file could not be deleted...
                Logger.getLogger(NMFPackageManager.class.getName()).log(Level.WARNING,
                        "The receipt file could not be deleted from: "
                        + oldReceiptFile.getCanonicalPath());
            }
        }

        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
            "Copying the new files to the locations...");

        MetadataApp appMetadata = newPackMetadata.castToApp();
        installDependencies(appMetadata, packageLocation, nmfDir);
        extractFiles(newPack, nmfDir);

        if (isApp) {
            String username = null;
            File installationDir = new File(nmfDir.getAbsolutePath()
                    + File.separator + FOLDER_APPS
                    + File.separator + packageName);

            if ((new OSValidator()).isUnix()) {
                username = generateUsername(packageName);
                HelperNMFPackage.generateStartScript(appMetadata, installationDir, nmfDir);
            }

            createAuxiliaryFiles(installationDir, username);

            if ((new OSValidator()).isUnix()) {
                String path = installationDir.getAbsolutePath();
                // Change Group owner of the installationDir
                if (username != null) {
                    LinuxUsersGroups.chgrp(true, username, path);
                }

                // chmod the installation directory with recursive
                LinuxUsersGroups.chmod(false, true, "750", path);
            }
        }

        // Store a copy of the newReceipt to know that it has been installed!
        newPackMetadata.store(oldReceiptFile);

        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Package successfully upgraded from location: " + packageLocation);

        System.out.printf(SEPARATOR);
    }

    private static void removeAuxiliaryFiles(File installationDir, String appName) throws IOException {
        String providerPath = installationDir.getAbsolutePath()
                + File.separator + HelperMisc.PROVIDER_PROPERTIES_FILE;
        String transportPath = installationDir.getAbsolutePath()
                + File.separator + HelperMisc.TRANSPORT_PROPERTIES_FILE;
        String startPath = installationDir.getAbsolutePath()
                + File.separator + "start_" + appName + ".sh";

        // Remove the provider.properties
        // Remove the transport.properties
        // Remove the start_myAppName.sh
        NMFPackageManager.removeFile(new File(providerPath));
        NMFPackageManager.removeFile(new File(transportPath));
        NMFPackageManager.removeFile(new File(startPath));
    }

    private static void createAuxiliaryFiles(File installationDir, String username) throws IOException {
        // Generate provider.properties
        String providerPath = installationDir.getAbsolutePath()
                + File.separator + HelperMisc.PROVIDER_PROPERTIES_FILE;
        String providerContent = HelperNMFPackage.generateProviderProperties(username);
        HelperNMFPackage.writeFile(providerPath, providerContent);

        // Generate transport.properties
        String transportPath = installationDir.getAbsolutePath()
                + File.separator + HelperMisc.TRANSPORT_PROPERTIES_FILE;
        String transportContent = HelperNMFPackage.generateTransportProperties();
        HelperNMFPackage.writeFile(transportPath, transportContent);
    }

    /**
     * Checks if a certain NMF package is installed. Based on the name, goes to
     * the receipts folder and checks.
     *
     * @param packageLocation The package location.
     * @return If it is installed or not.
     */
    public static boolean isPackageInstalled(final String packageLocation) {
        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.FINE,
                "Verifying if the package is installed...");

        // Find the newReceipt and get it out of the package
        NMFPackage pack;
        Metadata metadata;

        try {
            pack = new NMFPackage(packageLocation);
            metadata = pack.getMetadata();
        } catch (IOException ex) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.SEVERE,
                    "There was a problem while reading the NMF Package!", ex);
            return false;
        }

        File temp = getReceiptsFolder();
        String packageName = metadata.getPackageName();
        String receiptFilename = packageName + RECEIPT_ENDING;
        File receiptFile;
        try {
            receiptFile = new File(temp.getCanonicalPath() + File.separator + receiptFilename);
        } catch (IOException ex) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.SEVERE,
                "Something is wrong with the receipt file!", ex);
            return false;
        }

        boolean exists = receiptFile.exists();

        if (!exists) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.FINE,
                    "The package " + packageName + " is not installed!");
            return false;
        }

        // Check the version of the installed newReceipt
        Metadata installedMetadata;
        try {
            installedMetadata = Metadata.load(new FileInputStream(receiptFile));
        } catch (IOException ex) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.SEVERE,
                    "The file could not be loaded!", ex);
            return false;
        }

        if (installedMetadata == null) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.SEVERE,
                    "This is unexpected! The installedMetadata should not be null");
            return false;
        }

        // We need to check if the metadatas are the same!
        if (!metadata.sameAs(installedMetadata)) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.SEVERE,
                    "The NMF Package is not the same as the installed one!");
            return false;
        }

        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.FINE,
                "The package " + metadata.getPackageName() + " installation folder was found!");

        return true;
    }

    private static File getReceiptsFolder() {
        // Default location of the folder
        File folder = new File(FOLDER_RECEIPTS);

        // Read the Property of the folder to install the packages
        if (System.getProperty(INSTALLED_RECEIPTS_FOLDER_PROPERTY) != null) {
            folder = new File(System.getProperty(INSTALLED_RECEIPTS_FOLDER_PROPERTY));
        }

        return folder;
    }

    @Deprecated
    private static File getInstallationFolder() {
        // Default location of the folder
        File folder = new File("");

        // Read the Property of the folder to install the packages
        if (System.getProperty(INSTALLATION_FOLDER_PROPERTY) != null) {
            folder = new File(System.getProperty(INSTALLATION_FOLDER_PROPERTY));
        }

        return folder;
    }

    private static String generateFilePathForSystem(final String path) throws IOException {
        // Sanitize the path to prevent a ZipSlip attack:
        if (path.contains("..")) {
            throw new IOException("Warning! A ZipSlip attack was detected!");
        }

        String out = path.replace('/', File.separatorChar);
        return out.replace('\\', File.separatorChar);
    }

    private static void installDependencies(MetadataApp metadata, String packageLocation,
            File installationDir) throws IOException {
        String dependencies = metadata.getAppDependencies();
        String parent = (new File(packageLocation)).getParent();

        if (dependencies != null && !dependencies.isEmpty()) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(
                    Level.INFO, "Dependencies are:  " + dependencies);

            for (String file : dependencies.split(";")) {
                file = file.replace(".jar", "." + Const.NMF_PACKAGE_SUFFIX);
                String path = parent + File.separator + file;
                extractFiles(new NMFPackage(path), installationDir);
            }
        }
    }

    private static void extractFiles(NMFPackage pack, File to) throws IOException {
        Metadata metadata = pack.getMetadata();
        ZipFile zipFile = pack.getZipFile();

        File newFile;
        byte[] buffer = new byte[1024];

        // Iterate through the files, unpack them into the right folders
        ArrayList<NMFPackageFile> files = metadata.getFiles();

        for (int i = 0; i < files.size(); i++) {
            NMFPackageFile file = files.get(i);
            ZipEntry entry = pack.getZipFileEntry(file.getPath());

            String path = generateFilePathForSystem(entry.getName());
            newFile = new File(to.getCanonicalPath() + File.separator + path);
            File parent = new File(newFile.getParent());

            if (!parent.exists()) {
                new File(newFile.getParent()).mkdirs();
            }

            System.out.println("   >> Copying file to: " + newFile.getCanonicalPath());

            FileOutputStream fos = new FileOutputStream(newFile);
            InputStream zis = zipFile.getInputStream(entry);
            int len;

            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }

            fos.close();

            long crc = HelperNMFPackage.calculateCRCFromFile(newFile.getCanonicalPath());

            // We will also need to double check the CRCs again against the real files!
            // Just to double-check.. better safe than sorry!
            if (file.getCRC() != crc) {
                throw new IOException("The CRC does not match!");
            }
        }
    }

    private static void removeFiles(Metadata metadata) throws IOException {
        File folder = getInstallationFolder();
        File file;

        // Do the files actually match the descriptor?
        for (int i = 0; i < metadata.getFiles().size(); i++) {
            NMFPackageFile packageFile = metadata.getFiles().get(i);
            String path = generateFilePathForSystem(packageFile.getPath());
            file = new File(folder.getCanonicalPath() + File.separator + path);
            NMFPackageManager.removeFile(file);
        }
    }

    private static void removeFile(File file) throws IOException {
        String path = file.getCanonicalPath();
        System.out.println("   >> Removing: " + path);

        if (!file.exists()) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.WARNING,
                    "Not Found / Does not exist: " + path);
            return;
        }

        if (!file.delete()) { // Remove the file!
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.WARNING,
                    "One of the files could not be deleted: " + path);
        }
    }

    public static String getPublicKey(String folderLocation) {
        /*
        // Find the newReceipt and get it out of the package
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(packageLocation);
        } catch (IOException ex) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
         */

        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static String generateUsername(String appName) {
        return USER_NMF_APP_PREFIX + appName;
    }

}
