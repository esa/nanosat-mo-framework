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
package esa.mo.nmf.nmfpackage.tests;

import esa.mo.nmf.nmfpackage.NMFPackageBuilder;
import esa.mo.nmf.nmfpackage.metadata.Metadata;
import esa.mo.nmf.nmfpackage.metadata.MetadataApp;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipFile;

/**
 * A simple demo code to test the generation of NMF Packages.
 *
 * @author Cesar Coelho
 */
public class SimpleDemoPackageAppCreation {

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        SimpleDemoPackageAppCreation.createPackage();
    }

    public static void createPackage() {
        Logger.getLogger(SimpleDemoPackageAppCreation.class.getName()).log(
                Level.INFO, "\n----------- Package 1 Generation -----------\n");

        // Package 1
        File myAppFile = new File("myApp.filetype");

        MetadataApp metadata = new MetadataApp("my-test-app", "1.0.0",
                "noclass", "myJarFile.jar", "128m", "16m", null);

        NMFPackageBuilder builder = new NMFPackageBuilder(metadata);
        builder.addFileOrDirectory(myAppFile);
        File location = builder.createPackage(new File("target"));

        try {
            // Test if the created file can be parsed
            ZipFile writtenFile = new ZipFile(location);

            // Try to open the the metadata file inside the Zip file
            // and parse it into a Metadata object
            Metadata metadataInFile = Metadata.parseZipFile(writtenFile);
            metadataInFile = null;
        } catch (IOException ex) {
            Logger.getLogger(SimpleDemoPackageAppCreation.class.getName()).log(
                    Level.SEVERE, "The file could not be processed!", ex);
        }
    }
}
