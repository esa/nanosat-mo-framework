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
import esa.mo.nmf.nmfpackage.NMFPackage;
import esa.mo.nmf.nmfpackage.NMFPackageBuilder;
import esa.mo.nmf.nmfpackage.NMFPackageManager;
import esa.mo.nmf.nmfpackage.metadata.Metadata;
import esa.mo.nmf.nmfpackage.metadata.MetadataApp;
import esa.mo.nmf.nmfpackage.metadata.MetadataDependency;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 * Installs NMF packages declared as {@code <type>nmfpack</type>} dependencies
 * into the generated space filesystem produced by the
 * {@code generate-filesystem} goal.
 *
 * <p>For each {@code .nmfpack} artifact the goal reads the package metadata to
 * discover its non-NMF JAR dependencies.  Those JARs are already present in
 * the Maven reactor as resolved {@code jar} artifacts, so the goal builds
 * lightweight dependency {@code .nmfpack} files on-the-fly in a per-app
 * staging directory and then calls {@link NMFPackageManager#install} from
 * that directory, satisfying the co-location requirement of
 * {@code installDependencies}.
 *
 * <p>Intended to run after {@code generate-filesystem} in the same build
 * phase.
 *
 * @author Cesar Coelho
 */
@Mojo(name = "install-packages", defaultPhase = LifecyclePhase.PACKAGE,
        requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class InstallPackagesMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException {
        File outputDir = new File(project.getBuild().getDirectory(), "space-filesystem");
        File nmfDir = new File(outputDir, Deployment.DIR_NMF);

        if (!nmfDir.exists()) {
            throw new MojoExecutionException("NMF filesystem directory not found: "
                    + nmfDir.getAbsolutePath()
                    + "\nRun the generate-filesystem goal first.");
        }

        Deployment.initialize(nmfDir);

        // Build filename → artifact map for every resolved JAR so we can look
        // up the file when a package metadata lists a dep by JAR filename.
        @SuppressWarnings("unchecked")
        Set<Artifact> resolvedArtifacts = project.getArtifacts();

        Map<String, Artifact> jarsByFilename = new HashMap<>();
        for (Artifact a : resolvedArtifacts) {
            if ("jar".equals(a.getType()) && a.getFile() != null) {
                jarsByFilename.put(a.getFile().getName(), a);
            }
        }

        File packagesDir = new File(nmfDir, Deployment.DIR_PACKAGES);
        packagesDir.mkdirs();

        NMFPackageManager manager = new NMFPackageManager(null);
        int count = 0;

        for (Artifact artifact : resolvedArtifacts) {
            if (!Const.NMF_PACKAGE_SUFFIX.equals(artifact.getType())) {
                continue;
            }

            File packageFile = artifact.getFile();
            if (packageFile == null || !packageFile.exists()) {
                throw new MojoExecutionException("Package file not found for artifact: "
                        + artifact + ". Build the app first.");
            }

            // Read the package metadata to find which dep JARs it needs.
            Metadata metadata;
            try {
                metadata = new NMFPackage(packageFile.getAbsolutePath()).getMetadata();
            } catch (IOException ex) {
                throw new MojoExecutionException(
                        "Failed to read package metadata: " + packageFile.getName(), ex);
            }

            if (metadata.isApp()) {
                MetadataApp appMeta = metadata.castToApp();
                for (String depJarName : appMeta.getAppDependencies()) {
                    Artifact depArtifact = jarsByFilename.get(depJarName);
                    if (depArtifact == null) {
                        throw new MojoExecutionException(
                                "No Maven artifact found for dependency '" + depJarName
                                + "' required by " + artifact.getArtifactId()
                                + ". Add it to the playground pom dependencies.");
                    }

                    MetadataDependency depMeta = new MetadataDependency(
                            depArtifact.getArtifactId(), depArtifact.getVersion());
                    NMFPackageBuilder depBuilder = new NMFPackageBuilder(depMeta);
                    depBuilder.addFileOrDirectory(depArtifact.getFile());
                    depBuilder.createPackage(packagesDir);
                    getLog().info("  Packaged dep: " + depJarName);
                }
            }

            // installDependencies looks for dep packages in the same directory
            // as the main package; both live in packagesDir.
            File packageInDir = new File(packagesDir, packageFile.getName());
            try {
                Files.copy(packageFile.toPath(), packageInDir.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                throw new MojoExecutionException(
                        "Failed to copy package: " + packageFile.getName(), ex);
            }

            getLog().info("Installing: " + packageFile.getName());
            try {
                manager.install(packageInDir.getAbsolutePath(), nmfDir);
                count++;
            } catch (IOException ex) {
                throw new MojoExecutionException(
                        "Failed to install package: " + packageFile.getName(), ex);
            }
        }

        getLog().info("Installed " + count + " NMF package(s) into " + nmfDir.getAbsolutePath());
    }
}
