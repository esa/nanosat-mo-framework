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

import esa.mo.nmf.nmfpackage.utils.HelperNMFPackage;
import esa.mo.nmf.nmfpackage.metadata.MetadataApp;
import esa.mo.nmf.nmfpackage.metadata.MetadataDependency;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 * Generates the NMF Package for the NMF App
 *
 * @author Cesar Coelho
 */
@Mojo(name = "generate-nmf-package", defaultPhase = LifecyclePhase.PROCESS_RESOURCES,
        requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class GenerateNMFPackageMojo extends AbstractMojo {

    /**
     * The App name of the NMF Package
     */
    @Parameter(defaultValue = "${project}")
    private MavenProject project;

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
    @Parameter(property = "generate-nmf-package.mainClass", defaultValue = "${assembly.mainClass}")
    private String mainClass;

    /**
     * The version of the NMF that the App was developed against
     */
    @Parameter(property = "generate-nmf-package.nmfVersion", defaultValue = "${esa.nmf.version-qualifier}")
    private String nmfVersion;

    /**
     * The App maximum heap size to be set on the JVM
     */
    @Parameter(property = "generate-nmf-package.maxHeap", defaultValue = "128m")
    private String maxHeap;

    /**
     * The App maximum heap size to be set on the JVM
     */
    @Parameter(property = "generate-nmf-package.maxHeap", defaultValue = "16m")
    private String minHeap;

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

    private final static File TARGET_FOLDER = new File("target");

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Generating NMF Package...");

        getLog().info("\n---------- NMF Package - Generator ----------\n");
        getLog().info("Input values:");
        getLog().info(">> name = " + name);
        getLog().info(">> version = " + version);
        getLog().info(">> mainClass = " + mainClass);
        getLog().info(">> privilege = " + privilege);
        getLog().info(">> nmfVersion = " + nmfVersion);
        getLog().info(">> maxHeap = " + maxHeap);
        getLog().info(">> minHeap = " + minHeap);

        if (mainClass == null) {
            throw new MojoExecutionException("The mainClass tag is not defined!"
                    + " Please include in the <configuration> tag:\n"
                    + "-> \t\t<configuration>\n"
                    + "-> \t\t\t<mainClass>${assembly.mainClass}</mainClass>\n"
                    + "-> \t\t</configuration>\n\n\n"
                    + "-> Or add to the <properties> tag the mainclass. Example:\n"
                    + "-> \t\t<properties>\n"
                    + "-> \t\t\t<assembly.mainClass>esa.mo.nmf.apps.myapp.ExampleApp</assembly.mainClass>\n"
                    + "-> \t\t</properties>\n\n\n");
        }

        if ("${esa.nmf.version-qualifier}".equals(nmfVersion)) {
            throw new MojoExecutionException("The nmfVersion property needs to "
                    + "be defined!\nPlease use the <nmfVersion> tag inside the "
                    + "<configuration> tag!\n");
        }

        File myAppFilename;
        String mainJar;

        try {
            myAppFilename = HelperNMFPackage.findAppJarInFolder(TARGET_FOLDER);
            mainJar = myAppFilename.getName();
            getLog().info(">> mainJar = " + mainJar);
        } catch (IOException ex) {
            String error = "A problem occurred while trying to find the Jar file!";
            Logger.getLogger(GenerateNMFPackageMojo.class.getName()).log(
                    Level.SEVERE, error, ex);
            throw new MojoExecutionException(error, ex);
        }

        // Let's start by taking care of the project dependencies
        // They must also be packaged as NMF Packages that will be shared libraries
        getLog().info("------\nGenerating shared libraries...\n");
        ArrayList<String> dependencies = new ArrayList<>();

        for (Object unresolvedArtifact : this.project.getArtifacts()) {
            Artifact artifact = (Artifact) unresolvedArtifact;
            String artifactId = artifact.getGroupId();

            boolean isKnown = artifactId.contains("int.esa.nmf");
            boolean fromConnector = false;
            List<String> trail = artifact.getDependencyTrail();
            if (trail != null && trail.size() > 2) {
                fromConnector = trail.get(1).contains("nanosat-mo-connector");
            }

            if (isKnown || fromConnector) {
                StringBuilder str = new StringBuilder();
                str.append(artifact.getGroupId()).append(":");
                str.append(artifact.getArtifactId()).append(":");
                str.append(artifact.getVersion());
                getLog().info("  >> Ignoring artifactId: " + str.toString());
            } else {
                getLog().info("---\nFor dependency:");
                getLog().info("  >> GroupId = " + artifact.getGroupId());
                getLog().info("  >> ArtifactId = " + artifact.getArtifactId());
                getLog().info("  >> Version = " + artifact.getVersion());
                dependencies.add(packageJarDependency(artifact));
            }
        }

        MetadataApp metadata = new MetadataApp(name, version,
                mainClass, mainJar, maxHeap, minHeap, dependencies);

        NMFPackageBuilder builder = new NMFPackageBuilder(metadata);
        builder.addFileOrDirectory(myAppFilename);

        // Add the external libs or files
        if (libs != null) {
            for (String lib : libs) {
                getLog().info(">> lib: " + lib);
                builder.addFileOrDirectory(lib, "");
            }
        }

        getLog().info("------\nGenerating project NMF Package...\n");
        builder.createPackage(TARGET_FOLDER);
    }

    private String packageJarDependency(Artifact artifact) {
        File file = artifact.getFile();
        String artifactId = artifact.getArtifactId();
        String ver = artifact.getVersion();
        MetadataDependency metadata = new MetadataDependency(artifactId, ver);
        NMFPackageBuilder builder = new NMFPackageBuilder(metadata);
        builder.addFileOrDirectory(file);
        builder.createPackage(TARGET_FOLDER);
        return file.getName();
    }
}
