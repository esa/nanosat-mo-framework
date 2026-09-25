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
import esa.mo.nmf.environment.Deployment;
import esa.mo.nmf.nmfpackage.metadata.Metadata;
import esa.mo.sm.impl.provider.AppsLauncherProviderServiceImpl;
import esa.mo.sm.impl.util.PMBackend;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipFile;
import org.ccsds.moims.mo.mal.structures.StringList;

/**
 * The Package Management backend to handle the NMF Packages.
 *
 * @author Cesar Coelho
 */
public class NMFPackagePMBackend implements PMBackend {

    private final File packagesFolder;  // Location of the packages folder
    private final NMFPackageManager manager;

    /**
     * Initializes a backend for NMF Packages. The backend will look for
     * packages in the folder passed as argument.
     *
     * @param folder The folder to look for packages
     * @param appsLauncherService The AppsLauncher service.
     */
    public NMFPackagePMBackend(String folder, AppsLauncherProviderServiceImpl appsLauncherService) {
        this.packagesFolder = new File(folder);
        this.manager = new NMFPackageManager(appsLauncherService);
    }

    @Override
    public StringList getListOfPackages() throws IOException {
        // Go to the folder that contains the Packages and return the list of files!

        if (!packagesFolder.exists()) { // The folder does not exist
            // Then create a new folder with a default name
            Logger.getLogger(NMFPackagePMBackend.class.getName()).log(Level.INFO,
                    "The packages folder does not exist. It will be created in path: {0}",
                    packagesFolder.getCanonicalPath());

            packagesFolder.mkdir();
        }

        File[] files = packagesFolder.listFiles();

        StringList packageNames = new StringList(files.length);

        // Cycle them and find the ones with the .nmfpack extension
        for (File file : files) {
            String name = file.getName();

            // Check if the package ends with the expected suffix
            if (name.endsWith(Const.NMF_PACKAGE_SUFFIX)) {
                packageNames.add(name);
            }
        }

        // Return the filtered list
        return packageNames;
    }

    @Override
    public void install(final String packageName) {
        String packageLocation = this.getFolderLocation(packageName);
        File destination = Deployment.getNMFRootDir();

        Logger.getLogger(NMFPackagePMBackend.class.getName()).log(Level.INFO,
                "Installing the package...\nPackage name: " + packageName
                + "\nPackage location: " + packageLocation);

        try {
            manager.install(packageLocation, destination);
        } catch (IOException ex) {
            Logger.getLogger(NMFPackagePMBackend.class.getName()).log(Level.SEVERE,
                    "The package '" + packageName + "' could not be installed!", ex);
        }
    }

    @Override
    public void uninstall(final String packageName, final boolean keepUserData) {
        String folderLocation = this.getFolderLocation(packageName);
        Logger.getLogger(NMFPackagePMBackend.class.getName()).log(Level.INFO,
                "Uninstalling the package from: {0}", folderLocation);

        try {
            // Get the Package and descriptor to be uninstalled
            ZipFile zipFile = new ZipFile(folderLocation);

            // Verify integrity of the file: Are all the declared files matching their CRCs?
            Logger.getLogger(NMFPackagePMBackend.class.getName()).log(Level.INFO,
                    "Reading the metadata file that includes the list of files to be uninstalled...");

            Metadata metadata = Metadata.parseZipFile(zipFile);
            manager.uninstall(metadata, keepUserData);
        } catch (IOException ex) {
            Logger.getLogger(NMFPackagePMBackend.class.getName()).log(Level.SEVERE,
                    "The package '" + packageName + "' could not be uninstalled!", ex);
        }
    }

    @Override
    public void upgrade(final String packageName) {
        String packageLocation = this.getFolderLocation(packageName);
        Logger.getLogger(NMFPackagePMBackend.class.getName()).log(Level.INFO,
                "Upgrading the package from: {0}", packageLocation);

        // Define the location to be installed!
        File destination = Deployment.getNMFRootDir();

        try {
            manager.upgrade(packageLocation, destination);
        } catch (IOException ex) {
            Logger.getLogger(NMFPackagePMBackend.class.getName()).log(
                    Level.SEVERE, "The package could not be upgraded!", ex);
        }
    }

    @Override
    public String getPublicKey(final String packageName) {
        String folderLocation = this.getFolderLocation(packageName);
        return NMFPackageManager.getPublicKey(folderLocation);
    }

    @Override
    public String getPackageVersion(final String packageName) {
        String folderLocation = this.getFolderLocation(packageName);
        return manager.getInstalledVersion(folderLocation);
    }

    @Override
    public boolean isPackageInstalled(final String packageName) {
        String folderLocation = this.getFolderLocation(packageName);
        return manager.isPackageInstalled(folderLocation);
    }

    @Override
    public boolean isFinalVersionInstalled(final String packageName) {
        String folderLocation = this.getFolderLocation(packageName);
        return manager.isFinalVersionInstalled(folderLocation);
    }

    @Override
    public boolean checkPackageIntegrity(final String packageName) throws UnsupportedOperationException {
        // To do: Check the package integrity!
        //
        // Nothing signs an NMF Package today: the signing block in
        // NMFPackageBuilder is commented out and NMFDigitalSignature
        // (SHA1withDSA) has no callers. SHA256SUMS catches a corrupted
        // download but not a tampered one, as anyone can recompute it.
        //
        // To do: Look into Post-Quantum Cryptography before this is written.
        // Nothing has to be migrated, so the scheme can be picked outright:
        // SLH-DSA (FIPS 205) and LMS/XMSS (NIST SP 800-208) rest on the hash
        // function alone, ML-DSA (FIPS 204) is smaller and faster but rests
        // on lattices. Size hardly matters here because a package holds Jars,
        // so even the tens of kilobytes of SLH-DSA are lost in the noise.
        // LMS/XMSS hold a one-time key that must never sign twice, which the
        // build side would have to track across machines.
        //
        // Hardest is the key that verifies: it travels with the mission and
        // outlives the algorithm, so it needs a way of being replaced.
        return true;
    }

    @Override
    public boolean isBaselineComponent(final String packageName) throws IOException {
        String folderLocation = this.getFolderLocation(packageName);
        try (ZipFile zipFile = new ZipFile(folderLocation)) {
            return Metadata.parseZipFile(zipFile).isBaselineComponent();
        }
    }

    private String getFolderLocation(final String packageName) {
        return packagesFolder.getAbsolutePath() + File.separator + packageName;
    }

    /**
     * Sets the AppsLauncher service used by the underlying package manager.
     *
     * @param appsLauncherService the AppsLauncher service
     */
    public void setAppsLauncher(AppsLauncherProviderServiceImpl appsLauncherService) {
        manager.setAppsLauncher(appsLauncherService);
    }

}
