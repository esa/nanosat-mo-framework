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
import esa.mo.nmf.nmfpackage.metadata.MetadataNMF;
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
 * Generates an NMF core software-baseline package (type {@code nmf}) from an
 * already-generated {@code jars-nmf/<version>/} directory.
 *
 * <p>
 * The package places the framework JARs under {@code jars-nmf/<version>/} so
 * that installing it stages a new core baseline <b>beside</b> the existing one,
 * without activating it and without touching the version kept for fallback.
 * Activation is a separate, deliberate step performed on-board with the
 * {@code bootloader.setPrimaryBaseline} action. Run this after
 * {@code generate-filesystem}, which assembles the {@code jars-nmf/<version>/}
 * directory this goal packages.
 *
 * @author Cesar Coelho
 */
@Mojo(name = "generate-nmf-core-package", defaultPhase = LifecyclePhase.PACKAGE)
public class GenerateNMFCorePackageMojo extends AbstractMojo {

    /**
     * The project that the NMF Package is referring to.
     */
    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    @Component
    private MavenProjectHelper projectHelper;

    /**
     * The name of the NMF core package.
     */
    @Parameter(property = "generate-nmf-core-package.name", defaultValue = "nmf")
    private String name;

    /**
     * The version of the NMF core baseline to package.
     */
    @Parameter(property = "generate-nmf-core-package.nmfVersion", defaultValue = "${esa.nmf.version}")
    private String nmfVersion;

    /**
     * The {@code jars-nmf/<version>/} directory to package. Defaults to the one
     * produced by {@code generate-filesystem} for this build.
     */
    @Parameter(property = "generate-nmf-core-package.jarsNmfDirectory")
    private File jarsNmfDirectory;

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Generating NMF core package...");

        if ("${esa.nmf.version}".equals(nmfVersion) || nmfVersion == null) {
            throw new MojoExecutionException("The nmfVersion property needs to be "
                    + "defined!\nPlease use the <nmfVersion> tag inside the "
                    + "<configuration> tag!\n");
        }

        File jarsNmfDir = (jarsNmfDirectory != null) ? jarsNmfDirectory
                : new File(project.getBuild().getDirectory(), "space-filesystem"
                        + File.separator + Deployment.DIR_NMF
                        + File.separator + Deployment.DIR_JARS_NMF
                        + File.separator + nmfVersion);

        getLog().info(">> name = " + name);
        getLog().info(">> nmfVersion = " + nmfVersion);
        getLog().info(">> jars-nmf directory = " + jarsNmfDir);

        if (!jarsNmfDir.isDirectory()) {
            throw new MojoExecutionException("The jars-nmf/<version> directory does "
                    + "not exist: " + jarsNmfDir + "\nRun the generate-filesystem "
                    + "goal first, or set <jarsNmfDirectory>.");
        }

        MetadataNMF metadata = new MetadataNMF(name, nmfVersion);
        NMFPackageBuilder builder = new NMFPackageBuilder(metadata);

        File[] files = jarsNmfDir.listFiles();
        if (files == null || files.length == 0) {
            throw new MojoExecutionException("The jars-nmf/<version> directory is "
                    + "empty: " + jarsNmfDir);
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
            projectHelper.attachArtifact(project, Const.NMF_PACKAGE_SUFFIX, "nmf-core", packageFile);
            getLog().info("Attached artifact: " + packageFile.getName());
        }
    }
}
