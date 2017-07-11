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

import esa.mo.helpertools.misc.HelperNMF;
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

    private static final String FOLDER_LOCATION_PROPERTY = "esa.mo.nmf.package.FolderLocation";
    private static final String PACKAGES_FOLDER = "packages";  // dir name
    private File folderWithPackages = new File(PACKAGES_FOLDER);  // Location of the folder

    public NMFPackagePMBackend() {
        // If there is a property for that, then use it!! 
        if (System.getProperty(FOLDER_LOCATION_PROPERTY) != null) {
            folderWithPackages = new File(System.getProperty(FOLDER_LOCATION_PROPERTY));
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
            if (name.endsWith(HelperNMF.NMF_PACKAGE_SUFFIX)) {
                packageNames.add(name);
            }
        }

        // Return the filtered list
        return packageNames;
    }

    @Override
    public void install(final String packageName) {
        String folderLocation = this.getFolderLocation(packageName);
        Logger.getLogger(NMFPackagePMBackend.class.getName()).log(Level.INFO,
                "Installing the package from: {0}", folderLocation);

        try {
            NMFPackageManager.install(folderLocation);
        } catch (IOException ex) {
            Logger.getLogger(NMFPackagePMBackend.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void uninstall(final String packageName, final boolean keepConfigurations) {
        String folderLocation = this.getFolderLocation(packageName);
        Logger.getLogger(NMFPackagePMBackend.class.getName()).log(Level.INFO,
                "Uninstalling the package from: {0}", folderLocation);

        NMFPackageManager.uninstall(folderLocation, keepConfigurations);
    }

    @Override
    public void upgrade(final String packageName) {
        String folderLocation = this.getFolderLocation(packageName);
        Logger.getLogger(NMFPackagePMBackend.class.getName()).log(Level.INFO,
                "Upgrading the package from: {0}", folderLocation);
        
        NMFPackageManager.upgrade(folderLocation);
    }

    @Override
    public boolean isPackageInstalled(final String packageName) {
        // To do: Conclude this method

        return false;
    }

    @Override
    public boolean checkPackageIntegrity(final String packageName) throws UnsupportedOperationException {

        // To do: Check the package integrity!
        return true;
    }

    private String getFolderLocation(final String packageName) {
        return folderWithPackages.getAbsolutePath() + File.separator + packageName;
    }

}
