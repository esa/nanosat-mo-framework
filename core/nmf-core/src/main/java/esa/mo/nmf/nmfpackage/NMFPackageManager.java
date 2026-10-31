/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft - v2.4
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

import esa.mo.helpertools.misc.Const;
import esa.mo.helpertools.misc.OSValidator;
import esa.mo.nmf.environment.AppsIsolationMode;
import esa.mo.nmf.environment.Deployment;
import esa.mo.nmf.environment.LinuxUsersGroups;
import esa.mo.nmf.environment.SoftwareBaseline;
import esa.mo.nmf.nmfpackage.metadata.Metadata;
import esa.mo.nmf.nmfpackage.metadata.MetadataApp;
import esa.mo.nmf.nmfpackage.utils.AppProcesses;
import esa.mo.nmf.nmfpackage.utils.AuxFilesGenerator;
import esa.mo.nmf.nmfpackage.utils.ChecksumGenerator;
import esa.mo.nmf.nmfpackage.utils.HelperNMFPackage;
import esa.mo.sm.impl.provider.AppsLauncherProviderServiceImpl;
import java.io.File;
import java.nio.file.Path;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperMisc;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.sm.appslauncher.body.ListAppResponse;

/**
 * The NMFPackageManager class allows the install, uninstall and update an NMF
 * Package.
 *
 * @author Cesar Coelho
 */
public class NMFPackageManager {

    private static final String RECEIPT_ENDING = ".receipt";

    private static final String USER_NMF_APP_PREFIX = "app-";

    private static final String SEPARATOR = "--------------\n";

    private static final OSValidator OS = new OSValidator();

    // How long a stopped App is given to close before its files are kept
    private static final long APP_EXIT_TIMEOUT_MS = 5000;

    private AppsLauncherProviderServiceImpl appsLauncher;

    /**
     * Creates the package manager.
     *
     * @param appsLauncher the AppsLauncher service used to register/deregister installed apps,
     * or {@code null} if not available
     */
    public NMFPackageManager(AppsLauncherProviderServiceImpl appsLauncher) {
        this.appsLauncher = appsLauncher;
    }

    /**
     * Sets the AppsLauncher service used to register/deregister installed apps.
     *
     * @param appsLauncher the AppsLauncher service
     */
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
        install(packageLocation, nmfDir, null);
    }

    /**
     * Installs an NMF Package on the specified NMF root folder, recording the
     * permissions that its App is granted.
     *
     * @param packageLocation The NMF Package location
     * @param nmfDir The NMF root directory
     * @param acknowledged The permissions that ground acknowledged; the App is
     * granted those of them that it declares. Null grants all that it declares,
     * as when the App is part of the filesystem that the mission generates.
     * @throws FileNotFoundException if the file was not found
     * @throws IOException if there was a problem with the NMF Package
     */
    public void install(final String packageLocation, final File nmfDir,
            final List<String> acknowledged) throws FileNotFoundException, IOException {
        long timestamp = System.currentTimeMillis();
        System.out.printf(SEPARATOR);
        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Reading the package on: " + packageLocation);

        // Get the File to be installed
        try (NMFPackage nmfPackage = new NMFPackage(packageLocation)) {
            install(nmfPackage, packageLocation, nmfDir, timestamp, acknowledged);
        }
    }

    private void install(final NMFPackage nmfPackage, final String packageLocation,
            final File nmfDir, long timestamp, final List<String> acknowledged) throws IOException {
        Metadata metadata = nmfPackage.getMetadata();

        if (metadata.getMetadataVersion() < 3) {
            throw new IOException("The package version is deprecated! "
                    + "Version: " + metadata.getMetadataVersion());
        }

        nmfPackage.verifyPackageIntegrity();

        // A package only installs into its own area: refused here, before any
        // of its dependencies or files are installed
        nmfPackage.placeFiles(nmfDir);

        // The factory baseline is immutable in flight (NMF.BOOT.BMM.03)
        rejectIfTargetsFactory(metadata);

        if (metadata.isApp()) {
            installDependencies(metadata.castToApp(), packageLocation, nmfDir);
        }

        // Copy the files according to the NMF statement file
        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Copying the files to the new locations...");

        nmfPackage.extractFiles(nmfDir);
        String packageName = metadata.getPackageName();

        // Refresh the baseline checksums and rotate the baseline if this
        // package delivered a new baseline component (NMF Bootloader Spec)
        handleBaselineComponent(metadata, nmfDir);

        if (metadata.isApp()) {
            boolean linuxUserspace = AppsIsolationMode.isLinuxUserspace();
            String username = linuxUserspace ? generateUsername(packageName) : null;

            File appDir = new File(Deployment.getAppsDir(), packageName);
            AuxFilesGenerator.generateStartScript(metadata.castToApp(), appDir);
            createAuxiliaryFiles(appDir, username);
            File logDir = Deployment.getLogsDirForApp(packageName);
            logDir.mkdirs();

            if (OS.isUnix() && linuxUserspace) {
                try {
                    // Create the User for this App, with a group of its own and
                    // a home directory that the Supervisor's group can access
                    LinuxUsersGroups.addAppUser(username);

                    // Give the appDir to the App's group...
                    // Usually something like: /nanosat-mo-framework/apps/my-app
                    changeGroupAndSetPermissions(appDir, username, "750");

                    try {
                        changeGroupAndSetPermissions(logDir, username, "770");
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
        receiptWithGrants(metadata, acknowledged).store(receiptFile);

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

        // The factory baseline is immutable in flight (NMF.BOOT.BMM.03)
        rejectIfTargetsFactory(packageMetadata);

        // A package only removes files from its own area: refused here, before
        // the App is stopped or anything is deleted
        placeListedFiles(packageMetadata);

        if (packageMetadata.isApp()) { // If the App is running, then stop it!
            this.stopAppIfRunning(packageName);
            refuseIfStillRunning(packageName);
        }

        Logger.getLogger(NMFPackageManager.class.getName()).log(
                Level.INFO, "Removing the files...");

        removeFiles(packageMetadata);

        if (packageMetadata.isApp()) {
            // This directory should be passed in the method signature:
            File installationDir = new File(Deployment.getAppsDir(), packageName);
            removeAuxiliaryFiles(installationDir);

            boolean linuxUserspace = AppsIsolationMode.isLinuxUserspace();
            if (OS.isUnix() && linuxUserspace) {
                if (!keepUserData) {
                    String username = generateUsername(packageName);
                    try {
                        LinuxUsersGroups.deleteAppUser(username);
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
     * Updates an NMF Package with a newer version.
     *
     * @param packageLocation The NMF Package location
     * @param nmfDir The NMF root directory
     * @throws IOException if the package could not be updated
     */
    public void update(final String packageLocation, final File nmfDir) throws IOException {
        update(packageLocation, nmfDir, null);
    }

    /**
     * Updates an installed NMF Package, recording the permissions that its App
     * is granted after the update.
     *
     * @param packageLocation The location of the new NMF Package
     * @param nmfDir The NMF root directory
     * @param acknowledged The permissions that ground acknowledged; the App is
     * granted those of them that it declares. Null grants all that it declares.
     * @throws IOException if the package could not be updated
     */
    public void update(final String packageLocation, final File nmfDir,
            final List<String> acknowledged) throws IOException {
        long timestamp = System.currentTimeMillis();
        System.out.printf(SEPARATOR);
        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Reading the receipt file that includes the list of files to be updated...");

        // Get the Package to be uninstalled
        try (NMFPackage newPack = new NMFPackage(packageLocation)) {
            update(newPack, packageLocation, nmfDir, timestamp, acknowledged);
        }
    }

    private void update(final NMFPackage newPack, final String packageLocation,
            final File nmfDir, long timestamp, final List<String> acknowledged) throws IOException {
        Metadata newPackMetadata = newPack.getMetadata();

        if (newPackMetadata.getMetadataVersion() < 3) {
            throw new IOException("The package version is deprecated! "
                    + "Version: " + newPackMetadata.getMetadataVersion());
        }

        // Baseline components (nmf, mission, java) are never updated in place:
        // update removes the old files, which would destroy the version kept
        // for fallback. They are shipped with install (into a new versioned
        // directory) and activated later with the setPrimaryBaseline action.
        if (newPackMetadata.isBaselineComponent()) {
            throw new IOException("Baseline components cannot be updated! "
                    + "Install the new version and switch to it with the "
                    + "setPrimaryBaseline action. Package type: "
                    + newPackMetadata.getPackageType());
        }

        // The factory baseline is immutable in flight (NMF.BOOT.BMM.03)
        rejectIfTargetsFactory(newPackMetadata);

        File receiptsFolder = Deployment.getInstallationsTrackerDir();
        String receiptFilename = newPackMetadata.getPackageName() + RECEIPT_ENDING;
        File oldReceiptFile = new File(receiptsFolder, receiptFilename);
        Metadata oldPackMetadata = Metadata.load(oldReceiptFile);

        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO, "Updating..."
                + "\n  >> From version: " + oldPackMetadata.getPackageVersion()
                + " (timestamp: " + oldPackMetadata.getPackageTimestamp() + ")"
                + "\n  >>   To version: " + newPackMetadata.getPackageVersion()
                + " (timestamp: " + newPackMetadata.getPackageTimestamp() + ")"
        );

        // Both packages are held to their own areas: refused here, before the App
        // is stopped and the old files are removed
        placeListedFiles(oldPackMetadata);
        newPack.placeFiles(nmfDir);

        // The new files are checked against their checksums here too, so that
        // a bad package leaves the installed version as it is
        newPack.verifyPackageIntegrity();

        String packageName = oldPackMetadata.getPackageName();
        boolean isApp = oldPackMetadata.isApp();

        if (isApp) { // If the App is running, then stop it!
            this.stopAppIfRunning(packageName);
            refuseIfStillRunning(packageName);
        }

        Logger.getLogger(NMFPackageManager.class.getName()).log(
                Level.INFO, "Removing the previous files...");

        if (isApp) {
            // This directory should be passed in the method signature:
            File installationDir = new File(Deployment.getAppsDir(), packageName);
            removeAuxiliaryFiles(installationDir);
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

        // Refresh the baseline checksums and rotate the baseline if this
        // package delivered a new baseline component (NMF Bootloader Spec)
        handleBaselineComponent(newPackMetadata, nmfDir);

        if (isApp) {
            boolean linuxUserspace = AppsIsolationMode.isLinuxUserspace();
            String username = linuxUserspace ? generateUsername(packageName) : null;

            File appDir = new File(Deployment.getAppsDir(), packageName);
            MetadataApp appMetadata = newPackMetadata.castToApp();
            AuxFilesGenerator.generateStartScript(appMetadata, appDir);
            createAuxiliaryFiles(appDir, username);
            File logDir = Deployment.getLogsDirForApp(packageName);
            logDir.mkdirs();

            if (OS.isUnix() && linuxUserspace) { // Change Group owner of the appDir
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
        receiptWithGrants(newPackMetadata, acknowledged).store(oldReceiptFile);

        if (appsLauncher != null) {
            appsLauncher.refresh();
        }

        timestamp = System.currentTimeMillis() - timestamp;
        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                "Package successfully updated in " + timestamp
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
        Metadata metadata;

        try (NMFPackage pack = new NMFPackage(packageLocation)) {
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
            installedMetadata = Metadata.load(receiptFile);
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

    /**
     * Checks whether installing a package would replace a package that is
     * installed, and therefore may not be done with an install. Used only to
     * refuse an install; it is distinct from {@link #isPackageInstalled} (which
     * answers "is this exact package present" for uninstall and post-install
     * confirmation).
     *
     * <p>
     * An App is identified by its name: its directory, its storage, its
     * receipt and, under linux-userspace, its Linux account follow the name. A
     * package of another version, or of another publisher, with the name of an
     * installed App would take the App over, so it is refused whatever its
     * version; an App is replaced with an update instead. Only the same
     * {@code -SNAPSHOT} version may be installed again, over itself.
     *
     * <p>
     * Any other package replaces only the same final version of itself. A final
     * (release) version is immutable: once its name and version are installed,
     * re-installing the same version is rejected. A {@code -SNAPSHOT} version
     * is not final and may always be overridden. For a baseline component (nmf,
     * mission, java) "installed" means its versioned directory (e.g.
     * {@code jars-nmf/<v>}) already exists on disk; for other packages it means
     * an installation receipt with the same name and version exists.
     *
     * @param packageLocation The package location.
     * @return {@code true} if installing the package would replace an installed one.
     */
    public boolean replacesInstalledPackage(final String packageLocation) {
        Metadata metadata;
        try {
            try (NMFPackage pack = new NMFPackage(packageLocation)) {
                metadata = pack.getMetadata();
            }
        } catch (IOException ex) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.SEVERE,
                    "There was a problem while reading the NMF Package!", ex);
            return false;
        }

        String packageName = metadata.getPackageName();
        String version = metadata.getPackageVersion();

        if (metadata.isApp()) {
            return replacesInstalledApp(packageName, version);
        }

        // SNAPSHOT versions are not final: they may always be re-installed
        // (overridden), so they are never treated as an existing installation.
        if (isSnapshotVersion(version)) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.FINE,
                    "The package " + packageName + " is a SNAPSHOT and can be overridden.");
            return false;
        }

        // A baseline component is installed if its versioned directory exists.
        if (metadata.isBaselineComponent()) {
            try {
                File root = Deployment.getNMFRootDir();
                for (String dir : touchedBaselineDirs(metadata)) {
                    if (new File(root, dir).isDirectory()) {
                        return true;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(NMFPackageManager.class.getName()).log(Level.SEVERE,
                        "The baseline directory could not be checked!", ex);
            }
            return false;
        }

        // Other packages: installed if a receipt with the same name and version
        // exists in the installations tracker.
        File receiptFile = new File(Deployment.getInstallationsTrackerDir(),
                packageName + RECEIPT_ENDING);
        if (!receiptFile.exists()) {
            return false;
        }

        Metadata installedMetadata;
        try {
            installedMetadata = Metadata.load(receiptFile);
        } catch (IOException ex) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.SEVERE,
                    "The file could not be loaded!", ex);
            return false;
        }

        return installedMetadata != null
                && packageName.equals(installedMetadata.getPackageName())
                && version.equals(installedMetadata.getPackageVersion());
    }

    /**
     * Whether an App package of this name and version would replace the
     * installed App of its name: always, except the same SNAPSHOT version.
     */
    private static boolean replacesInstalledApp(final String packageName, final String version) {
        File receiptFile = new File(Deployment.getInstallationsTrackerDir(),
                packageName + RECEIPT_ENDING);
        if (!receiptFile.exists()) {
            return false;
        }

        Metadata installedMetadata;
        try {
            installedMetadata = Metadata.load(receiptFile);
        } catch (IOException ex) {
            // An App of this name is installed, even if its receipt cannot be read
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.SEVERE,
                    "The receipt of the App " + packageName + " could not be loaded!", ex);
            return true;
        }

        String installedVersion = (installedMetadata != null) ? installedMetadata.getPackageVersion() : null;
        if (isSnapshotVersion(version) && version.equals(installedVersion)) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.FINE,
                    "The package " + packageName + " is the installed SNAPSHOT and can be overridden.");
            return false;
        }

        Logger.getLogger(NMFPackageManager.class.getName()).log(Level.WARNING,
                "The App " + packageName + " " + installedVersion + " is installed: the package of version "
                + version + " may only replace it with an update");
        return true;
    }

    /**
     * Returns whether a version string denotes a non-final SNAPSHOT build.
     *
     * @param version The version string.
     * @return {@code true} if the version ends with {@code -SNAPSHOT}.
     */
    private static boolean isSnapshotVersion(final String version) {
        return version != null && version.endsWith("-SNAPSHOT");
    }

    /**
     * Returns the currently installed version of a package, read from its
     * installation receipt.
     *
     * @param packageLocation The location of the package file.
     * @return The installed version, or null if the package is not installed
     * or the version cannot be determined.
     */
    public String getInstalledVersion(final String packageLocation) {
        Metadata metadata;

        try {
            try (NMFPackage pack = new NMFPackage(packageLocation)) {
                metadata = pack.getMetadata();
            }
        } catch (IOException ex) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.SEVERE,
                    "There was a problem while reading the NMF Package!", ex);
            return null;
        }

        File temp = Deployment.getInstallationsTrackerDir();
        File receiptFile = new File(temp, metadata.getPackageName() + RECEIPT_ENDING);

        if (!receiptFile.exists()) {
            return null;
        }

        try {
            Metadata installedMetadata = Metadata.load(receiptFile);
            return (installedMetadata != null) ? installedMetadata.getPackageVersion() : null;
        } catch (IOException ex) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.SEVERE,
                    "The receipt file could not be loaded!", ex);
            return null;
        }
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

    private static void removeAuxiliaryFiles(File folder) throws IOException {
        File provider = new File(folder, HelperMisc.PROVIDER_PROPERTIES_FILE);
        File transport = new File(folder, HelperMisc.TRANSPORT_PROPERTIES_FILE);
        File linux = new File(folder, "start_app.sh");
        File windows = new File(folder, "start_app.bat");

        // Remove the provider.properties
        // Remove the transport.properties
        // Remove the start_app.sh or start_app.bat
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

    /**
     * Works out where each file listed by the metadata lies under the NMF root,
     * holding the package to the area that belongs to its type (see
     * {@link HelperNMFPackage#resolveInsideArea}). Nothing is deleted.
     */
    private static ArrayList<File> placeListedFiles(Metadata metadata) throws IOException {
        final Path root = Deployment.getNMFRootDir().getCanonicalFile().toPath();
        ArrayList<File> placed = new ArrayList<>();

        for (NMFPackageFile entry : metadata.getFiles()) {
            placed.add(HelperNMFPackage.resolveInsideArea(root, metadata, entry.getPath()).toFile());
        }

        return placed;
    }

    private static void removeFiles(Metadata metadata) throws IOException {
        // Every file is placed before any is deleted, so a package with one
        // file outside its area deletes nothing at all
        for (File file : placeListedFiles(metadata)) {
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

    /**
     * Refreshes the baseline integrity manifests after a package has been
     * extracted (NMF Bootloader Specification). For every baseline directory the
     * package wrote into ({@code jars-nmf/<v>}, {@code jars-mission/<v>},
     * {@code java/<v>}) the {@code SHA256SUMS} file is regenerated so the
     * bootloader's integrity test passes on the next boot (NMF.BOOT.BTE.02).
     * No-op for App and dependency packages.
     *
     * <p>
     * Installing a baseline component only stages its files in a new versioned
     * directory; it does <b>not</b> activate it. The switch to the new baseline
     * is a deliberate, separate step performed through the validated
     * {@code bootloader.setPrimaryBaseline} action, which rotates the primary to
     * secondary and writes the new primary. This keeps shipping a version and
     * activating it decoupled, and preserves the previous version for fallback.
     *
     * @param metadata The metadata of the installed package.
     * @param nmfDir The NMF root directory.
     * @throws IOException if a checksum manifest cannot be written.
     */
    private static void handleBaselineComponent(Metadata metadata, File nmfDir) throws IOException {
        // Only a baseline package refreshes baseline checksums; any other package
        // is already held to an area outside the baseline directories
        if (!metadata.isBaselineComponent()) {
            return;
        }

        Set<String> touched = touchedBaselineDirs(metadata);

        for (String dir : touched) {
            ChecksumGenerator.writeChecksumsFile(new File(nmfDir, dir));
        }
    }

    /**
     * Rejects an operation that would modify a component of the factory
     * baseline (NMF.BOOT.BMM.03). No-op for deployments without a factory
     * baseline file (e.g. IDE or test runs outside an NMF filesystem).
     *
     * @param metadata The metadata of the package targeted by the operation.
     * @throws IOException if the operation targets a factory baseline component.
     */
    private static void rejectIfTargetsFactory(Metadata metadata) throws IOException {
        File factoryFile = new File(Deployment.getBootloaderDir(), Deployment.FILE_BASELINE_FACTORY);

        if (!factoryFile.isFile()) {
            return; // No factory baseline to protect
        }

        SoftwareBaseline factory = SoftwareBaseline.load(factoryFile);
        Set<String> factoryDirs = new LinkedHashSet<>();
        addBaselineDir(factoryDirs, Deployment.DIR_JARS_NMF, factory.getNmfVersion());
        addBaselineDir(factoryDirs, Deployment.DIR_JARS_MISSION, factory.getMissionVersion());
        if (factory.getJava() != null) {
            String javaDir = baselineDirOf(factory.getJava().replace('\\', '/'));
            if (javaDir != null) {
                factoryDirs.add(javaDir);
            }
        }

        for (String dir : touchedBaselineDirs(metadata)) {
            if (factoryDirs.contains(dir)) {
                throw new IOException("The factory baseline is immutable in flight! "
                        + "The operation targets a factory baseline component: " + dir);
            }
        }
    }

    private static void addBaselineDir(Set<String> dirs, String top, String version) {
        if (version != null && !version.isEmpty()) {
            dirs.add(top + "/" + version);
        }
    }

    /**
     * The set of baseline component directories (relative to the NMF root, with
     * {@code /} separators) that a package writes into: {@code jars-nmf/<v>},
     * {@code jars-mission/<v>} and {@code java/<v>}. App and dependency packages
     * yield an empty set.
     */
    private static Set<String> touchedBaselineDirs(Metadata metadata) throws IOException {
        Set<String> dirs = new LinkedHashSet<>();
        final Path root = Deployment.getNMFRootDir().getCanonicalFile().toPath();

        for (NMFPackageFile entry : metadata.getFiles()) {
            Path resolved = HelperNMFPackage.resolveInside(root, entry.getPath());
            String path = root.relativize(resolved).toString().replace(File.separatorChar, '/');
            String dir = baselineDirOf(path);
            if (dir != null) {
                dirs.add(dir);
            }
        }
        return dirs;
    }

    /**
     * Returns the {@code <top>/<version>} baseline directory a file path lies
     * under, or {@code null} if the path is not inside a baseline directory.
     */
    private static String baselineDirOf(String path) {
        for (String top : new String[]{Deployment.DIR_JARS_NMF,
                Deployment.DIR_JARS_MISSION, Deployment.DIR_JAVA}) {
            String prefix = top + "/";
            if (path.startsWith(prefix)) {
                int slash = path.indexOf('/', prefix.length());
                if (slash > prefix.length()) {
                    return path.substring(0, slash);
                }
            }
        }
        return null;
    }

    /**
     * Returns the receipt to store for an installed package: its metadata, with
     * the permissions that its App is granted when it carries an App.
     */
    private static Metadata receiptWithGrants(Metadata metadata, List<String> acknowledged) {
        if (!metadata.isApp()) {
            return metadata;
        }
        // castToApp returns a copy, so the receipt is stored from that copy
        MetadataApp app = metadata.castToApp();
        app.setGrantedPermissions(acknowledged == null
                ? app.getAppPermissions() : app.grantedFrom(acknowledged));
        return app;
    }

    private static String generateUsername(String appName) {
        return USER_NMF_APP_PREFIX + appName;
    }

    private static void changeGroupAndSetPermissions(File file,
            String appUser, String permissions) throws IOException {
        if (appUser != null) {
            LinuxUsersGroups.giveToAppGroup(appUser, file.getAbsolutePath());
        }

        // chmod the installation directory with recursive
        LinuxUsersGroups.chmod(true, permissions, file.getAbsolutePath());
    }

    /**
     * Stops the App if the Apps Launcher runs it, and checks that no process
     * still runs it, wherever that process came from.
     *
     * @param packageName The name of the App's package.
     * @return True if no process runs the App, false if one still does.
     * @throws IOException if the directory of the App cannot be resolved.
     */
    public boolean stopApp(String packageName) throws IOException {
        this.stopAppIfRunning(packageName);
        return processesStillRunning(packageName).isEmpty();
    }

    /**
     * Refuses to go on while any process still runs the App, before any of its
     * files is removed. The Apps Launcher only stops the processes that it
     * started, and an App that it did not start, or that did not close when
     * asked, would otherwise keep running from files that are gone, with no
     * receipt left to show that it was ever installed.
     */
    private static void refuseIfStillRunning(String packageName) throws IOException {
        List<ProcessHandle> running = processesStillRunning(packageName);

        if (!running.isEmpty()) {
            String pids = running.stream()
                    .map(p -> String.valueOf(p.pid()))
                    .collect(Collectors.joining(", "));
            throw new IOException("The " + packageName + " App is still running"
                    + " (process " + pids + "), so its files were left as they"
                    + " are! Stop the processes and try again.");
        }
    }

    private static List<ProcessHandle> processesStillRunning(String packageName)
            throws IOException {
        File appDir = new File(Deployment.getAppsDir(), packageName);
        String username = AppsIsolationMode.isLinuxUserspace()
                ? generateUsername(packageName) : null;
        return AppProcesses.awaitExit(appDir, username, APP_EXIT_TIMEOUT_MS);
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

            for (int i = 0; i < response.getAppIds().size(); i++) {
                if (response.getRunning().get(i)) {
                    Long appId = response.getAppIds().get(i);
                    runningApp.add(appId);
                }
            }

            if (!runningApp.isEmpty()) {
                Logger.getLogger(NMFPackageManager.class.getName()).log(
                        Level.INFO, "Stopping the " + name + " App...");

                appsLauncher.stopApp(runningApp, null, null);
            }
        } catch (org.ccsds.moims.mo.mal.UnknownException | org.ccsds.moims.mo.sm.AppNotRunningException
                | org.ccsds.moims.mo.com.InvalidArgumentException ex) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(Level.INFO,
                    "The " + name + " App is not known to the Apps Launcher, or no longer runs!");
        } catch (MALException ex) {
            Logger.getLogger(NMFPackageManager.class.getName()).log(
                    Level.SEVERE, "(2) Something went wrong...", ex);
        }
    }
}
