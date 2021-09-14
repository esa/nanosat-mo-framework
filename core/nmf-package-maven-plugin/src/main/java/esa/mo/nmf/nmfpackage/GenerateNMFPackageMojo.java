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
import java.util.List;
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
    @Parameter(property = "generate-nmf-package.libs")
    private List<String> libs;

    /**
     * The set of privileges that an App can have
     */
    public enum Privilege {
        normal,
        admin,
        root
    }

    /**
     * The privilege that the App needs during runtime
     */
    @Parameter(property = "generate-nmf-package.privilege", defaultValue = "normal")
    private Privilege privilege;

    final ArrayList<String> inputFiles = new ArrayList<>();
    final ArrayList<String> locations = new ArrayList<>();
    String appPath = "no-path";

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Generating NMF Package...");
        appPath = "apps" + SEPARATOR + name + SEPARATOR;
        String mainJar = "";

        try {
            File target = new File("target");
            File myAppFilename = HelperNMFPackage.findAppJarInFolder(target);
            mainJar = myAppFilename.getName();
            inputFiles.add(myAppFilename.getAbsolutePath());
            locations.add(appPath + myAppFilename.getName());

            // Add the external libs or files
            if (libs != null) {
                for (String lib : libs) {
                    getLog().info(">> lib: " + lib);
                    addFileOrDirectory(lib, "");
                }
            }
        } catch (IOException ex) {
            String error = "A problem occurred while trying to find the Jar file!";
            Logger.getLogger(GenerateNMFPackageMojo.class.getName()).log(
                    Level.SEVERE, error, ex);
            throw new MojoExecutionException(error, ex);
        }

        getLog().info("\n---------- NMF Package - Generator ----------\n");
        getLog().info("Input values:");
        getLog().info(">> name = " + name);
        getLog().info(">> version = " + version);
        getLog().info(">> mainClass = " + mainClass);
        getLog().info(">> mainJar = " + mainJar);
        getLog().info(">> privilege = " + privilege);
        getLog().info(">> nmfVersion = " + nmfVersion);
        getLog().info(">> maxHeap = " + maxHeap);

        if (mainClass == null) {
            throw new MojoExecutionException("The mainClass property needs to be defined!\n"
                    + "Please use the <mainClass> tag inside the <configuration> tag!\n");
        }

        if ("${esa.nmf.version-qualifier}".equals(nmfVersion)) {
            throw new MojoExecutionException("The nmfVersion property needs to be defined!\n"
                    + "Please use the <nmfVersion> tag inside the <configuration> tag!\n");
        }

        final Time time = new Time(System.currentTimeMillis());
        final String timestamp = HelperTime.time2readableString(time);

        // Package
        NMFPackageDetails details = new NMFPackageDetails(name, version,
                timestamp, mainClass, mainJar, maxHeap);
        NMFPackageCreator.nmfPackageCreator(details, inputFiles, locations, "target");
    }

    private void addFileOrDirectory(String path, String nest) {
        File f = new File(path);

        if (f.isDirectory()) {
            nest += f.getName() + File.separator;

            for (File n : f.listFiles()) {
                addFileOrDirectory(n.getAbsolutePath(), nest);
            }
        } else {
            inputFiles.add(f.getAbsolutePath());
            locations.add(appPath + nest + f.getName());
        }
    }

}
