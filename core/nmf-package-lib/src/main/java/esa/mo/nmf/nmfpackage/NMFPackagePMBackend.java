/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
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
import java.io.File;
import esa.mo.sm.impl.util.PMBackend;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.StringList;

/**
 *
 * @author Cesar Coelho
 */
public class NMFPackagePMBackend implements PMBackend {

    @Deprecated
    private static final String FOLDER_LOCATION_PROPERTY = "esa.mo.nmf.package.FolderLocation";

    @Deprecated
    private static final String PACKAGES_FOLDER = "packages";  // dir name
    
    private final File folderWithPackages;  // Location of the folder

    /**
     * Initializes a backend for NMF Packages. The backend will look for
     * packages in the folder passed as argument.
     *
     * @param folder The folder to look for packages
     */
    public NMFPackagePMBackend(String folder) {
        folderWithPackages = new File(folder);
    }

    /**
     *
     * @deprecated The folder with the packages must be passed as argument
     * instead of having the internal hidden properties for it! Please use:
     * NMFPackagePMBackend(String folder)
     */
    @Deprecated
    public NMFPackagePMBackend() {
        // If there is a property for that, then use it!! 
        if (System.getProperty(FOLDER_LOCATION_PROPERTY) != null) {
            folderWithPackages = new File(System.getProperty(FOLDER_LOCATION_PROPERTY));
        }else{
            folderWithPackages = new File(PACKAGES_FOLDER);
        }
    }

    @Override
    public StringList getListOfPackages() throws IOException {
        // Go to the folder that contains the Packages and return the list of files!

        if (!folderWithPackages.exists()) { // The folder does not exist
            // Then create a new folder with a default name
            Logger.getLogger(NMFPackagePMBackend.class.getName()).log(Level.INFO,
                    "The packages folder does not exist. It will be created in path: {0}",
                    folderWithPackages.getCanonicalPath());

            folderWithPackages.mkdir();
        }

        File[] files = folderWithPackages.listFiles();

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
        File destination = getNMFDir();

        Logger.getLogger(NMFPackagePMBackend.class.getName()).log(Level.INFO,
                "Installing the package...\nPackage name: " + packageName
                + "\nPackage location: " + packageLocation);

        try {
            NMFPackageManager.install(packageLocation, destination);
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
            NMFPackageManager.uninstall(folderLocation, keepUserData);
        } catch (IOException ex) {
            Logger.getLogger(NMFPackagePMBackend.class.getName()).log(Level.SEVERE, 
                    "The package '" + packageName + "' could not be uninstalled!", ex);
        }
    }

    @Override
    public void upgrade(final String packageName) {
        String folderLocation = this.getFolderLocation(packageName);
        Logger.getLogger(NMFPackagePMBackend.class.getName()).log(Level.INFO,
                "Upgrading the package from: {0}", folderLocation);

        // Define the location to be installed!
        File destination = getNMFDir();

        try {
            NMFPackageManager.upgrade(folderLocation, destination);
        } catch (IOException ex) {
            Logger.getLogger(NMFPackagePMBackend.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getPublicKey(final String packageName) {
        String folderLocation = this.getFolderLocation(packageName);
        return NMFPackageManager.getPublicKey(folderLocation);
    }

    @Override
    public boolean isPackageInstalled(final String packageName) {
        String folderLocation = this.getFolderLocation(packageName);
        return NMFPackageManager.isPackageInstalled(folderLocation);
    }

    @Override
    public boolean checkPackageIntegrity(final String packageName) throws UnsupportedOperationException {

        // To do: Check the package integrity!
        return true;
    }

    private String getFolderLocation(final String packageName) {
        return folderWithPackages.getAbsolutePath() + File.separator + packageName;
    }

    private File getNMFDir() {
        return new File("");
    }

}
