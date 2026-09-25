/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
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

import esa.mo.helpertools.misc.Const;
import esa.mo.nmf.environment.Deployment;
import esa.mo.nmf.nmfpackage.NMFPackageBuilder;
import esa.mo.nmf.nmfpackage.metadata.MetadataMission;
import esa.mo.nmf.nmfpackage.utils.ChecksumGenerator;
import java.io.File;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

/**
 * Generates a mission software-baseline package (type {@code mission}) from a
 * directory of mission JARs.
 *
 * <p>
 * The package places the mission JARs under {@code jars-mission/<version>/} so
 * that installing it stages a new mission baseline <b>beside</b> the existing
 * one, without activating it and without touching the version kept for
 * fallback. Activation is a separate, deliberate step performed on-board with
 * the {@code bootloader.setPrimaryBaseline} action.
 *
 * <p>
 * A mission baseline is versioned apart from the framework one. The bootloader
 * reads {@code mission-version} and {@code nmf-version} from the baseline and
 * builds the classpath from both, mission first, so a mission can be updated
 * without the framework moving underneath it.
 *
 * @author Cesar Coelho
 */
@Mojo(name = "generate-mission-package", defaultPhase = LifecyclePhase.PACKAGE)
public class GenerateMissionPackageMojo extends AbstractMojo {

    /**
     * The project that the NMF Package is referring to.
     */
    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    @Component
    private MavenProjectHelper projectHelper;

    /**
     * The name of the mission package.
     */
    @Parameter(property = "generate-mission-package.name", defaultValue = "mission")
    private String name;

    /**
     * The version of the mission baseline to package.
     */
    @Parameter(property = "generate-mission-package.missionVersion", defaultValue = "${project.version}")
    private String missionVersion;

    /**
     * The {@code jars-mission/<version>/} directory to package. Defaults to the
     * one produced by {@code generate-filesystem} for this build, which is the
     * only place the mission set is worked out: that goal sorts the Supervisor's
     * dependencies into framework and mission, and packaging the result keeps
     * the two from being derived twice and drifting apart.
     */
    @Parameter(property = "generate-mission-package.jarsMissionDirectory")
    private File jarsMissionDirectory;

    /**
     * Default constructor.
     */
    public GenerateMissionPackageMojo() {
    }

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Generating mission package...");

        if (missionVersion == null || missionVersion.startsWith("${")) {
            throw new MojoExecutionException("The missionVersion property needs to be "
                    + "defined!\nPlease use the <missionVersion> tag inside the "
                    + "<configuration> tag!\n");
        }

        File jarsMissionDir = (jarsMissionDirectory != null) ? jarsMissionDirectory
                : new File(project.getBuild().getDirectory(), "space-filesystem"
                        + File.separator + Deployment.DIR_NMF
                        + File.separator + Deployment.DIR_JARS_MISSION
                        + File.separator + missionVersion);

        getLog().info(">> name = " + name);
        getLog().info(">> missionVersion = " + missionVersion);
        getLog().info(">> jars-mission directory = " + jarsMissionDir);

        if (!jarsMissionDir.isDirectory()) {
            throw new MojoExecutionException("The jars-mission/<version> directory does "
                    + "not exist: " + jarsMissionDir + "\nRun the generate-filesystem "
                    + "goal first, or set <jarsMissionDirectory>.");
        }

        MetadataMission metadata = new MetadataMission(name, missionVersion);
        NMFPackageBuilder builder = new NMFPackageBuilder(metadata);

        File[] files = jarsMissionDir.listFiles();

        if (files == null || files.length == 0) {
            throw new MojoExecutionException("The jars-mission/<version> directory is "
                    + "empty: " + jarsMissionDir);
        }

        for (File file : files) {
            // The SHA256SUMS manifest is regenerated by the Package Manager on
            // install, so it is not shipped inside the package.
            if (file.isFile() && !ChecksumGenerator.CHECKSUMS_FILENAME.equals(file.getName())) {
                getLog().info("  >> Adding: " + file.getName());
                builder.addFileOrDirectory(file);
            }
        }

        File targetFolder = new File(project.getBuild().getDirectory());
        File packageFile = builder.createPackage(targetFolder);

        if (packageFile != null && packageFile.exists()) {
            projectHelper.attachArtifact(project, Const.NMF_PACKAGE_SUFFIX, "mission", packageFile);
            getLog().info("Attached artifact: " + packageFile.getName());
        }
    }
}
