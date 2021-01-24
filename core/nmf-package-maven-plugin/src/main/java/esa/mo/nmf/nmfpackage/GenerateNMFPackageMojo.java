/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
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

import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.nmf.nmfpackage.descriptor.NMFPackageDetails;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.ccsds.moims.mo.mal.structures.Time;

/**
 * Generates the NMF Package for the NMF App
 *
 * @author Cesar Coelho
 */
@Mojo(name = "generate-nmf-package")
public class GenerateNMFPackageMojo extends AbstractMojo {

    private final static String SEPARATOR = File.separator;
    /**
     * The version of the NMF Package
     */
    @Parameter(property = "generate-nmf-package.version", defaultValue = "1.0")
    private String version;

    /**
     * The App name of the NMF Package
     */
    @Parameter(property = "generate-nmf-package.name", defaultValue = "package")
    private String name;

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Generating NMF Package...");

        ArrayList<String> inputFiles = new ArrayList<>();
        ArrayList<String> locations = new ArrayList<>();
        
        try {
            File myAppFilename = this.findAppJarInTargetFolder();
            inputFiles.add(myAppFilename.getAbsolutePath());
            locations.add("apps" + SEPARATOR + name + SEPARATOR + myAppFilename.getName());
        } catch (IOException ex) {
            Logger.getLogger(GenerateNMFPackageMojo.class.getName()).log(
                    Level.SEVERE, "The Jar file was not found!", ex);
        }
        
        // Step 1: Fill in app details...
        // App Name, Description, Category
        // Step 2: Is it a NMF app?
        // If No:s
        // Select the binary inputFiles to be installed
        // Additional libraries?
        final Time time = new Time(System.currentTimeMillis());
        final String timestamp = HelperTime.time2readableString(time);

        getLog().info("\n------------- NMF Package Generation -------------\n");

        // Package 1
        NMFPackageDetails details = new NMFPackageDetails(name, version, timestamp);
        NMFPackageCreator.nmfPackageCreator(details,
                inputFiles, locations, "target");
    }

    private File findAppJarInTargetFolder() throws IOException {

        File targetFolder = new File("target");
        File[] fList = targetFolder.listFiles();

        for (File file : fList) {
            if (file.isDirectory()) {
                continue; // Jump over if it is a directory
            }

            if (!file.getAbsolutePath().endsWith(".jar")) {
                continue; // It is not a Jar file
            }

            return file;
        }

        throw new IOException("Not found!");
    }
}
