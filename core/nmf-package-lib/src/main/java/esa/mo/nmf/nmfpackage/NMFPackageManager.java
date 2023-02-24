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
import esa.mo.nmf.nmfpackage.utils.AuxFilesGenerator;
import esa.mo.sm.impl.provider.AppsLauncherProviderServiceImpl;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.body.ListAppResponse;

/**
 * The NMFPackageManager class allows the install, uninstall and upgrade an NMF
 * Package.
 *
 * @author Cesar Coelho
 */
public class NMFPackageManager {

    private static final String RECEIPT_ENDING = ".receipt";

    // This must match the group_nmf_apps value in the fresh_install.sh file
    private static final String GROUP_NMF_APPS = "nmf-apps";

    // The groups will be disbled in the current state of the NMF because there 
    // is no need for it at the moment and it is not supported by the Phi-Sat-2
    // mission on-board computer. This might have to be revised in the future...
    private static final boolean GROUP_FLAG = false;

    private static final String USER_NMF_ADMIN = "nmf-admin";

    private static final String USER_NMF_APP_PREFIX = "app-";

    private static final String SEPARATOR = "--------------\n";

    private static final OSValidator OS = new OSValidator();

    private AppsLauncherProviderServiceImpl appsLauncher;

    public NMFPackageManager(AppsLauncherProviderServiceImpl appsLauncher) {
        this.appsLauncher = appsLauncher;
    }

    public void setAppsLauncher(AppsLauncherProviderServiceImpl appsLauncher) {
        this.appsLauncher = appsLauncher;
    }

    /**
     * Installs an NMF Package on the specified NMF root folder.
     *
     * @param packageLocation The NMF Package location
     * @param nmfDir The NMF root directory
     * @throws FileNotFoundException if the file was not found
     * @throws IOException if there was a problem with the NMF Package
     */
    public void install(final String packageLocation,
            final File nmfDir) throws FileNotFoundException, IOException {
        long timestamp = System.currentTimeMillis();
        System.out.printf(SEPARATOR);
        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Reading the package on: " + packageLocation);

        // Get the File to be installed
        NMFPackage nmfPackage = new NMFPackage(packageLocation);
        Metadata metadata = nmfPackage.getMetadata();

        if (metadata.getMetadataVersion() < 3) {
            throw new IOException("The package version is deprecated! "
                    + "Version: " + metadata.getMetadataVersion());
        }

        nmfPackage.verifyPackageIntegrity();

        if (metadata.isApp()) {
            installDependencies(metadata.castToApp(), packageLocation, nmfDir);
        }

        // Copy the files according to the NMF statement file
        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Copying the files to the new locations...");

        nmfPackage.extractFiles(nmfDir);
        String packageName = metadata.getPackageName();

        if (metadata.isApp()) {
            String username = generateUsername(packageName);
            String password = null;

            File appDir = new File(Deployment.getAppsDir(), packageName);
            AuxFilesGenerator.generateStartScript(metadata.castToApp(), appDir, nmfDir);
            createAuxiliaryFiles(appDir, username);
            File logDir = Deployment.getLogsDirForApp(packageName);
            logDir.mkdirs();

            if (OS.isUnix()) {
                try {
                    // Create the User for this App
                    boolean withGroup = true;
                    LinuxUsersGroups.adduser(username, password, withGroup);

                    if (GROUP_FLAG) {
                        try {
                            LinuxUsersGroups.addUserToGroup(username, GROUP_NMF_APPS);
                        } catch (IOException ex) {
                            Logger.getLogger(NMFPackageManager.class.getName()).log(
                                    Level.INFO, "The User " + username
                                    + " could not be added to the Group: "
                                    + GROUP_NMF_APPS, ex);
                        }
                    }

                    // Set the right Group and Permissions to the Home Directory
                    // The owner remains with the app, the group is nmf-admin
                    try {
                        String appHomeDir = LinuxUsersGroups.findHomeDir(username);
                        LinuxUsersGroups.chgrp(true, USER_NMF_ADMIN, appHomeDir);
                        LinuxUsersGroups.chmod(true, true, "770", appHomeDir);
                    } catch (IOException ex) {
                        Logger.getLogger(NMFPackageManager.class.getName()).log(
                                Level.INFO, "The permissions of the User Home "
                                + "directory could not be set for username: "
                                + username, ex);
                    }

                    // There is a group with the same name as the username:
                    String toGroup = username;
                    // Change Group owner of the appDir...
                    // Usually something like: /nanosat-mo-framework/apps/my-app
                    changeGroupAndSetPermissions(appDir, toGroup, "750");

                    try {
                        changeGroupAndSetPermissions(logDir, toGroup, "770");
                    } catch (IOException ex) {
                        // The previous log files were created with a user that 
                        // might no longer exist, so just ignore the exception!
                    }
                } catch (IOException ex) {
                    Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                            "The User could not be created: " + username, ex);
                }
            }
        }

        // Store a copy of the newReceipt to know that it has been installed!
        File receiptsFolder = Deployment.getInstallationsTrackerDir();
        String receiptFilename = packageName + RECEIPT_ENDING;
        File receiptFile = new File(receiptsFolder, receiptFilename);
        metadata.store(receiptFile);

        if (metadata.isApp() && appsLauncher != null) {
            appsLauncher.refresh();
        }

        timestamp = System.currentTimeMillis() - timestamp;
        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Package successfully installed in " + timestamp
                + " ms!\nFrom: " + packageLocation);

        System.out.printf(SEPARATOR);
    }

    /**
     * Uninstalls an NMF Package using the respective package descriptor.
     *
     * @param packageMetadata The metadata
     * @param keepUserData A flag that sets if the user data is kept
     * @throws IOException if there was a problem during the uninstallation
     */
    public void uninstall(final Metadata packageMetadata,
            final boolean keepUserData) throws IOException {
        long timestamp = System.currentTimeMillis();
        System.out.printf(SEPARATOR);
        String packageName = packageMetadata.getPackageName();

        if (packageMetadata.isApp()) { // If the App is running, then stop it!
            this.stopAppIfRunning(packageName);
        }

        Logger.getLogger(NMFPackageManager.class.getName()).log(
                Level.INFO, "Removing the files...");

        removeFiles(packageMetadata);

        if (packageMetadata.isApp()) {
            // This directory should be passed in the method signature:
            File installationDir = new File(Deployment.getAppsDir(), packageName);
            removeAuxiliaryFiles(installationDir, packageName);

            if (OS.isUnix()) {
                if (!keepUserData) {
                    String username = generateUsername(packageName);
                    try {
                        LinuxUsersGroups.deluser(username, true);
                    } catch (IOException ex) {
                        Logger.getLogger(NMFPackageManager.class.getName()).log(
                                Level.WARNING, "The User could not be deleted: "
                                + username, ex);
                    }
                }
            }
        }

        File receiptsFolder = Deployment.getInstallationsTrackerDir();
        File receiptFile = new File(receiptsFolder, packageName + RECEIPT_ENDING);

        if (!receiptFile.delete()) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.WARNING,
                    "The receipt file could not be deleted from: " + receiptsFolder);
        }

        if (appsLauncher != null) {
            appsLauncher.refresh();
        }

        timestamp = System.currentTimeMillis() - timestamp;
        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Package successfully uninstalled in " + timestamp
                + " ms!\nPackage name: " + packageName);

        System.out.printf(SEPARATOR);
    }

    /**
     * Upgrades an NMF Package with a newer version.
     *
     * @param packageLocation The NMF Package location
     * @param nmfDir The NMF root directory
     * @throws IOException if the package could not be upgraded
     */
    public void upgrade(final String packageLocation, final File nmfDir) throws IOException {
        long timestamp = System.currentTimeMillis();
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

        File receiptsFolder = Deployment.getInstallationsTrackerDir();
        String receiptFilename = newPackMetadata.getPackageName() + RECEIPT_ENDING;
        File oldReceiptFile = new File(receiptsFolder, receiptFilename);
        Metadata oldPackMetadata = Metadata.load(oldReceiptFile);

        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO, "Upgrading..."
                + "\n  >> From version: " + oldPackMetadata.getPackageVersion()
                + " (timestamp: " + oldPackMetadata.getPackageTimestamp() + ")"
                + "\n  >>   To version: " + newPackMetadata.getPackageVersion()
                + " (timestamp: " + newPackMetadata.getPackageTimestamp() + ")"
        );

        String packageName = oldPackMetadata.getPackageName();
        boolean isApp = oldPackMetadata.isApp();

        if (isApp) { // If the App is running, then stop it!
            this.stopAppIfRunning(packageName);
        }

        Logger.getLogger(NMFPackageManager.class.getName()).log(
                Level.INFO, "Removing the previous files...");

        if (isApp) {
            // This directory should be passed in the method signature:
            File installationDir = new File(Deployment.getAppsDir(), packageName);
            removeAuxiliaryFiles(installationDir, packageName);
        }

        removeFiles(oldPackMetadata);

        if (!oldReceiptFile.delete()) { // The file could not be deleted...
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.WARNING,
                    "The receipt file could not be deleted from: "
                    + oldReceiptFile.getCanonicalPath());
        }

        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
            "Copying the new files to the locations...");

        if (isApp) {
            MetadataApp appMetadata = newPackMetadata.castToApp();
            installDependencies(appMetadata, packageLocation, nmfDir);
        }
        newPack.extractFiles(nmfDir);

        if (isApp) {
            File appDir = new File(Deployment.getAppsDir(), packageName);
            String username = generateUsername(packageName);

            MetadataApp appMetadata = newPackMetadata.castToApp();
            AuxFilesGenerator.generateStartScript(appMetadata, appDir, nmfDir);
            createAuxiliaryFiles(appDir, username);
            File logDir = Deployment.getLogsDirForApp(packageName);
            logDir.mkdirs();

            if (OS.isUnix()) { // Change Group owner of the appDir
                changeGroupAndSetPermissions(appDir, username, "750");

                try {
                    changeGroupAndSetPermissions(logDir, username, "770");
                } catch (IOException ex) {
                    // The previous log files were created with a user that 
                    // might no longer exist, so just ignore the exception!
                }
            }
        }

        // Store a copy of the newReceipt to know that it has been installed!
        newPackMetadata.store(oldReceiptFile);

        if (appsLauncher != null) {
            appsLauncher.refresh();
        }

        timestamp = System.currentTimeMillis() - timestamp;
        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Package successfully upgraded in " + timestamp
                + " ms!\nFrom: " + packageLocation);

        System.out.printf(SEPARATOR);
    }

    /**
     * Checks if a certain NMF package is installed. Based on the name, goes to
     * the receipts folder and checks.
     *
     * @param packageLocation The package location.
     * @return If it is installed or not.
     */
    public boolean isPackageInstalled(final String packageLocation) {
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

        File temp = Deployment.getInstallationsTrackerDir();
        String packageName = metadata.getPackageName();
        File receiptFile = new File(temp, packageName + RECEIPT_ENDING);

        if (!receiptFile.exists()) {
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
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.FINE,
                    "The NMF Package is not the same as the installed one!");
            return false;
        }

        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.FINE,
                "The package " + packageName + " installation folder was found!");

        return true;
    }

    private void installDependencies(MetadataApp metadata,
            String packageLocation, File nmfDir) throws IOException {
        if (!metadata.hasDependencies()) {
            return;
        }

        ArrayList<String> dependencies = metadata.getAppDependencies();
        String parent = (new File(packageLocation)).getParent();

        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "The dependencies are: " + dependencies.toString());

        ArrayList<String> toBeInstalled = new ArrayList<>();

        for (String dependency : dependencies) {
            String file = dependency.replace(".jar", "." + Const.NMF_PACKAGE_SUFFIX);
            String path = parent + File.separator + file;

            if (isPackageInstalled(path)) {
                Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                        "The dependency is already installed: " + dependency);
            } else {
                if ((new File(path)).exists()) {
                    toBeInstalled.add(path);
                } else {
                    throw new IOException("The package was not found for "
                            + "dependency: " + dependency);
                }
            }
        }

        for (String path : toBeInstalled) {
            this.install(path, nmfDir);
            // NMFPackage pack = new NMFPackage(path);
            // pack.extractFiles(nmfDir);
        }
    }

    private static void removeAuxiliaryFiles(File folder, String appName) throws IOException {
        File provider = new File(folder, HelperMisc.PROVIDER_PROPERTIES_FILE);
        File transport = new File(folder, HelperMisc.TRANSPORT_PROPERTIES_FILE);
        File linux = new File(folder, "start_" + appName + ".sh");
        File windows = new File(folder, "start_" + appName + ".bat");

        // Remove the provider.properties
        // Remove the transport.properties
        // Remove the start_myAppName.sh or start_myAppName.bat
        NMFPackageManager.removeFile(provider);
        NMFPackageManager.removeFile(windows.exists() ? windows : linux);
        if (transport.exists()) {
            NMFPackageManager.removeFile(transport);
        }
    }

    private static void createAuxiliaryFiles(File appDir, String username) throws IOException {
        File globalTransport = Deployment.getTransportFile();
        String transport;

        // Generate a dedicated transport.properties for
        // the App if the Global transport file does not exist
        if (globalTransport.exists()) {
            transport = globalTransport.getAbsolutePath();
        } else {
            // Generates the transport file in the folder of the App!
            File file = new File(appDir, HelperMisc.TRANSPORT_PROPERTIES_FILE);
            String transportContent = AuxFilesGenerator.generateTransportProperties();
            AuxFilesGenerator.writeFile(file, transportContent);
            transport = HelperMisc.TRANSPORT_PROPERTIES_FILE;
        }

        // Generate provider.properties
        File providerFile = new File(appDir, HelperMisc.PROVIDER_PROPERTIES_FILE);
        String providerContent = AuxFilesGenerator.generateProviderProperties(username, transport);
        AuxFilesGenerator.writeFile(providerFile, providerContent);

    }

    private static void removeFiles(Metadata metadata) throws IOException {
        File folder = Deployment.getNMFRootDir();
        File file;

        // Do the files actually match the descriptor?
        for (int i = 0; i < metadata.getFiles().size(); i++) {
            NMFPackageFile entry = metadata.getFiles().get(i);
            String path = HelperNMFPackage.sanitizePath(entry.getPath());
            file = new File(folder.getCanonicalPath() + File.separator + path);
            NMFPackageManager.removeFile(file);
            File parent = file.getParentFile();

            // Delete the parent folder if it is empty after removing the file:
            if (parent.isDirectory() && parent.list().length == 0) {
                NMFPackageManager.removeFile(parent);
            }
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

    private static void changeGroupAndSetPermissions(File file,
            String toGroup, String permissions) throws IOException {
        if (toGroup != null) {
            LinuxUsersGroups.chgrp(true, toGroup, file.getAbsolutePath());
        }

        // chmod the installation directory with recursive
        LinuxUsersGroups.chmod(false, true, permissions, file.getAbsolutePath());
    }

    private void stopAppIfRunning(String name) {
        if (appsLauncher == null) {
            return;
        }

        try {
            Logger.getLogger(NMFPackageManager.class.getName()).log(
                    Level.INFO, "Checking if " + name + " App is running... "
                    + " The App will be stopped if running!");

            IdentifierList myApp = new IdentifierList();
            myApp.add(new Identifier(name));
            Identifier category = new Identifier("*");
            ListAppResponse response = appsLauncher.listApp(myApp, category, null);
            LongList runningApp = new LongList();

            for (int i = 0; i < response.getBodyElement0().size(); i++) {
                if (response.getBodyElement1().get(i)) {
                    Long appId = response.getBodyElement0().get(i);
                    runningApp.add(appId);
                }
            }

            if (!runningApp.isEmpty()) {
                Logger.getLogger(NMFPackageManager.class.getName()).log(
                        Level.INFO, "Stopping the " + name + " App...");

                appsLauncher.stopApp(runningApp, null);
            }
        } catch (MALInteractionException ex) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                    "The " + name + " App was not found in the Directory service!");
        } catch (MALException ex) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(
                    Level.SEVERE, "(2) Something went wrong...", ex);
        }
    }
}
