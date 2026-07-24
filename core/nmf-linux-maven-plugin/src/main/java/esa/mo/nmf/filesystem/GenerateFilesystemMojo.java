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
package esa.mo.nmf.filesystem;

import esa.mo.nmf.environment.Deployment;
import esa.mo.nmf.environment.AppsIsolationMode;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 * Generates the NanoSat MO Framework filesystem structure for a Linux system.
 *
 * @author Cesar Coelho
 */
@Mojo(name = "generate-filesystem", defaultPhase = LifecyclePhase.PROCESS_RESOURCES,
        requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class GenerateFilesystemMojo extends AbstractMojo {

    /**
     * The project that the NMF Package is referring to.
     */
    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    /**
     * The version of the mission.
     */
    @Parameter(property = "generate-filesystem.missionVersion", defaultValue = "${project.version}")
    private String missionVersion;

    /**
     * The main class for the Supervisor.
     */
    @Parameter(property = "generate-filesystem.supervisorMainClass", defaultValue = "${supervisorMainClass}")
    private String supervisorMainClass;

    /**
     * The version of the NMF that the Supervisor was developed against.
     */
    @Parameter(property = "generate-filesystem.nmfVersion", defaultValue = "${esa.nmf.version}")
    private String nmfVersion;

    /**
     * The isolation mode applied to all apps managed by this Supervisor.
     * Supported values are defined in {@link AppsIsolationMode}:
     * <ul>
     * <li>{@code none} (default) — apps run as the user that launched the
     * Supervisor.</li>
     * <li>{@code linux-userspace} — each app runs under a dedicated Linux user
     * account created at install time.</li>
     * <li>{@code docker-containers} — each app runs inside a dedicated Docker
     * container (not yet implemented).</li>
     * <li>{@code bubblewrap} — each app runs inside a bubblewrap sandbox (not
     * yet implemented).</li>
     * </ul>
     */
    @Parameter(property = "generate-filesystem.appsIsolation", defaultValue = AppsIsolationMode.NONE)
    private String appsIsolation;

    /**
     * The set of libraries to be added
     */
    @Parameter(property = "generate-filesystem.libs")
    private List<String> libs;

    /**
     * The mission and spacecraft designation written into
     * {@code etc/mission.properties}. See {@link Mission}.
     */
    @Parameter
    private Mission mission;

    /**
     * Default constructor.
     */
    public GenerateFilesystemMojo() {
    }

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Generating Linux Filesystem...");

        getLog().info("\n--------- NMF Linux - Filesystem Generator ---------\n");
        getLog().info("Input values:");
        getLog().info(">> mainClass = " + supervisorMainClass);
        getLog().info(">> nmfVersion = " + nmfVersion);
        getLog().info(">> version = " + missionVersion);
        getLog().info(">> appsIsolation = " + appsIsolation);

        if (supervisorMainClass == null) {
            throw new MojoExecutionException("The supervisorMainClass tag is not defined!"
                    + " Please include in the <configuration> tag:\n"
                    + "-> \t\t<configuration>\n"
                    + "-> \t\t\t<supervisorMainClass>${assembly.mainClass}</supervisorMainClass>\n"
                    + "-> \t\t</configuration>\n\n\n"
                    + "-> Or add to the <properties> tag the mainclass. Example:\n"
                    + "-> \t\t<properties>\n"
                    + "-> \t\t\t<supervisorMainClass>esa.mo.nmf.mission.MissionSupervisor</supervisorMainClass>\n"
                    + "-> \t\t</properties>\n\n\n");
        }

        if ("${esa.nmf.version}".equals(nmfVersion)) {
            throw new MojoExecutionException("The nmfVersion property needs to "
                    + "be defined!\nPlease use the <nmfVersion> tag inside the "
                    + "<configuration> tag!\n");
        }

        File outputDir = new File(project.getBuild().getDirectory(), "space-filesystem");
        FilesystemGenerator filesystem = new FilesystemGenerator(outputDir);

        try {
            // Add the logging.properties file
            String file_logging = "logging.properties";
            getLog().info("  >> Adding DIR_ETC: " + file_logging);
            filesystem.addResource(Deployment.DIR_ETC, file_logging);
        } catch (IOException ex) {
            throw new MojoExecutionException(ex);
        }

        // Add the mission.properties file
        if (mission == null) {
            throw new MojoExecutionException("The <mission> configuration is not defined!"
                    + " Please include in the <configuration> tag:\n"
                    + "-> \t\t<mission>\n"
                    + "-> \t\t\t<missionName>...</missionName>\n"
                    + "-> \t\t\t<spacecraftName>...</spacecraftName>\n"
                    + "-> \t\t\t<organizationAbbreviation>...</organizationAbbreviation>\n"
                    + "-> \t\t</mission>\n\n\n");
        }
        try {
            mission.checkRequiredFields();
        } catch (IllegalArgumentException ex) {
            throw new MojoExecutionException("Invalid <mission> configuration: " + ex.getMessage());
        }
        try {
            getLog().info("  >> Adding DIR_ETC: " + Deployment.FILE_MISSION_PROPERTIES);
            filesystem.addGeneratedFile(Deployment.DIR_ETC, Deployment.FILE_MISSION_PROPERTIES,
                    mission.toPropertiesContent());
        } catch (IOException ex) {
            throw new MojoExecutionException(ex);
        }

        for (Object aaa : project.getDependencies()) {
            Dependency dependency = (Dependency) aaa;
            getLog().info(">> Dependency = " + dependency.toString());

            // Go inside each dependency and check if it is default!
        }

        for (Object unresolvedArtifact : this.project.getArtifacts()) {
            Artifact artifact = (Artifact) unresolvedArtifact;
            String artifactId = artifact.getGroupId();

            // Only jars belong on the classpath directories; other artifact
            // types (e.g. nmfpack, handled by install-packages) are skipped
            File artifactFile = artifact.getFile();
            if (artifactFile == null || !artifactFile.getName().endsWith(".jar")) {
                getLog().info("  >> Skipping non-jar artifact: " + artifact.toString());
                continue;
            }

            boolean isMO = artifactId.contains("int.esa.ccsds.mo");
            boolean isNMFCore = artifactId.contains("int.esa.nmf.core");

            // Resolves transitive dependencies like sqlite pulled in by nmf-composites
            boolean fromComposites = false;
            List<String> trail = artifact.getDependencyTrail();
            if (trail != null) {
                for (String step : trail) {
                    if (step.contains("nmf-composites")) {
                        fromComposites = true;
                        break;
                    }
                }
            }

            if (isMO || isNMFCore || fromComposites) {
                StringBuilder str = new StringBuilder();
                str.append(artifact.getGroupId()).append(":");
                str.append(artifact.getArtifactId()).append(":");
                str.append(artifact.getVersion());
                getLog().info("  >> Adding DIR_JARS_NMF: " + artifact.toString());
                filesystem.addArtifactNMF(artifact, nmfVersion);
            } else {
                getLog().info("---\nFor dependency:");
                getLog().info("  >> GroupId = " + artifact.getGroupId());
                getLog().info("  >> ArtifactId = " + artifact.getArtifactId());
                getLog().info("  >> Version = " + artifact.getVersion());
                getLog().info("  >> Adding DIR_JARS_MISSION: " + artifact.toString());
                filesystem.addArtifactMission(artifact, missionVersion);
            }
        }

        // Generate the bootloader script and its domain (baseline files,
        // config, checksum manifests). Must run after the jars are in place,
        // because the checksum manifests cover the final directory contents.
        getLog().info("  >> Generating the bootloader: script, baselines, checksums");
        try {
            File nmfRootDir = new File(outputDir, Deployment.DIR_NMF);
            BootloaderGenerator bootloader = new BootloaderGenerator(nmfRootDir);
            bootloader.generate(nmfVersion, missionVersion, supervisorMainClass, appsIsolation);
        } catch (IOException ex) {
            throw new MojoExecutionException(ex);
        }
    }
}
