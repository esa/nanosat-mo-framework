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
package esa.mo.nmf.nmfpackage.tests;

import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.nmf.nmfpackage.NMFPackageCreator;
import esa.mo.nmf.nmfpackage.descriptor.NMFPackageDetails;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.Time;

/**
 *
 * @author Cesar Coelho
 */
public class SimpleDemoPackageCreation {

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        SimpleDemoPackageCreation.createPackage();
    }

    public static void createPackage() {
        ArrayList<String> files = new ArrayList<String>();
        ArrayList<String> newLocations = new ArrayList<String>();

        String myAppFilename = "myApp.filetype";
        String dummyFolder = "myInstalledApp";
        files.add(myAppFilename);
        newLocations.add("apps" + File.separator + dummyFolder + File.separator + myAppFilename);

        // Step 1: Fill in app details...
        // App Name, Description, Category
        // Step 2: Is it a NMF app?
        // If No:s
        // Select the binary files to be installed
        // Additional libraries?
        final Time time = new Time(System.currentTimeMillis());
        final String timestamp = HelperTime.time2readableString(time);

        Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.INFO,
                "\n------------- Package 1 Generation -------------\n");

        // Package 1
        NMFPackageDetails details = new NMFPackageDetails("TestPackage", "1.0", timestamp);
        NMFPackageCreator.nmfPackageCreator(details, files, newLocations);

        Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.INFO,
                "\n------------- Package 2 Generation -------------\n");

        // Package 2
        NMFPackageDetails details2 = new NMFPackageDetails("TestPackage", "2.0", timestamp);
        NMFPackageCreator.nmfPackageCreator(details2, files, newLocations);

        Logger.getLogger(NMFPackageCreator.class.getName()).log(Level.INFO,
                "\n------------- Package 3 Generation -------------\n");

        // Package 3
        NMFPackageDetails details3 = new NMFPackageDetails("TestPackage", "3.0", timestamp);
        NMFPackageCreator.nmfPackageCreator(details3, files, newLocations);

        // If Yes:
        // Select the jar file (without dependencies)
        // Select the provider.properties file
        // 
        // Additional libraries?
    }

}
