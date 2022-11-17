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
     * The App name of the NMF Package
     */
    @Parameter(property = "generate-nmf-package.name", defaultValue = "${project.artifactId}")
    private String name;

    /**
     * The version of the NMF Package
     */
    @Parameter(property = "generate-nmf-package.version", defaultValue = "${project.version}")
    private String version;

    /**
     * The App main class
     */
    @Parameter(property = "generate-nmf-package.mainClass")
    private String mainClass;

    /**
     * The version of the NMF that the App was developed against
     */
    @Parameter(property = "generate-nmf-package.nmfVersion", defaultValue = "${esa.nmf.version-qualifier}")
    private String nmfVersion;

    @Parameter(property = "generate-nmf-package.maxHeap")
    private String maxHeap;

    /**
     * The set of libraries to be added to the .nmfpack
     */
    @Parameter(property = "generate-nmf-package.libs", required = true)
    private String[] libs;

    /**
     * The set of privileges that an App can have
     */
    public enum Privilege {
        normal, admin, root
    }

    /**
     * The privilege that the App needs during runtime
     */
    @Parameter(property = "generate-nmf-package.privilege", defaultValue = "normal")
    private Privilege privilege;

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
            Logger.getLogger(GenerateNMFPackageMojo.class.getName()).log(Level.SEVERE, "The Jar file was not found!",
                ex);
        }

        getLog().info("\n------------- NMF Package - Generator -------------\n");
        getLog().info("Input values:");
        getLog().info(">> name = " + name);
        getLog().info(">> version = " + version);
        getLog().info(">> mainClass = " + mainClass);
        getLog().info(">> privilege = " + privilege);
        getLog().info(">> nmfVersion = " + nmfVersion);
        getLog().info(">> maxHeap = " + maxHeap);

        if (mainClass == null) {
            throw new MojoExecutionException("The mainClass property needs to be defined!\n" +
                "Please use the <mainClass> tag inside the <configuration> tag!\n");
        }

        if ("${esa.nmf.version-qualifier}".equals(nmfVersion)) {
            throw new MojoExecutionException("The nmfVersion property needs to be defined!\n" +
                "Please use the <nmfVersion> tag inside the <configuration> tag!\n");
        }

        final Time time = new Time(System.currentTimeMillis());
        final String timestamp = HelperTime.time2readableString(time);

        // Package 1
        NMFPackageDetails details = new NMFPackageDetails(name, version, timestamp, mainClass, maxHeap);
        NMFPackageCreator.nmfPackageCreator(details, inputFiles, locations, "target");
        // Additional libraries?
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
