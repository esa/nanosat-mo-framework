/* ----------------------------------------------------------------------------
 * Copyright (C) 2025      European Space Agency
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

import java.io.File;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 * Generates the NanoSat MO Framework filesystem structure for a Linux system
 *
 * @author Cesar Coelho
 */
@Mojo(name = "generate-linux-filesystem", defaultPhase = LifecyclePhase.PROCESS_RESOURCES,
        requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class GenerateLinuxFilesystemMojo extends AbstractMojo {

    /**
     * The project that the NMF Package is referring to
     */
    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    /**
     * The App name of the NMF Package
     */
    @Parameter(property = "generate-linux-filesystem.name", defaultValue = "${project.artifactId}")
    private String name;

    /**
     * The version of the NMF Package
     */
    @Parameter(property = "generate-linux-filesystem.version", defaultValue = "${project.version}")
    private String version;

    /**
     * The App main class
     */
    @Parameter(property = "generate-linux-filesystem.mainClass", defaultValue = "${assembly.mainClass}")
    private String mainClass;

    /**
     * The version of the NMF that the App was developed against
     */
    @Parameter(property = "generate-linux-filesystem.nmfVersion", defaultValue = "${esa.nmf.version}")
    private String nmfVersion;

    /**
     * The set of libraries to be added to the .nmfpack
     */
    @Parameter(property = "generate-linux-filesystem.libs")
    private List<String> libs;

    /**
     * The set of privileges that an App can have
     */
    public enum Privilege {
        normal,
        admin,
        root
    }

    private final static File TARGET_FOLDER = new File("target");

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Generating Linux Filesystem...");

        getLog().info("\n---------- NMF Linux - Filesystem Generator ----------\n");
        getLog().info("Input values:");
        getLog().info(">> name = " + name);
        getLog().info(">> version = " + version);
        getLog().info(">> mainClass = " + mainClass);
        getLog().info(">> nmfVersion = " + nmfVersion);

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

    }
}
