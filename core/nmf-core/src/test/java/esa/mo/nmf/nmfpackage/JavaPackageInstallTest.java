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
package esa.mo.nmf.nmfpackage;

import esa.mo.helpertools.misc.OSValidator;
import esa.mo.nmf.environment.Deployment;
import esa.mo.nmf.nmfpackage.metadata.MetadataJava;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * An NMF Package keeps no file modes, so a Java runtime installed from one
 * could not be run. Holds that the install makes the programs of the runtime
 * executable, and nothing else.
 */
public class JavaPackageInstallTest {

    private static final String RUNTIME = "jre-21.0.0";

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private File root;
    private File previousRoot;

    @Before
    public void useATemporaryNMFRoot() throws Exception {
        root = tmp.newFolder(Deployment.DIR_NMF);
        previousRoot = setNMFRoot(root);
    }

    @After
    public void restoreTheNMFRoot() throws Exception {
        setNMFRoot(previousRoot);
    }

    @Test
    public void theProgramsOfAnInstalledJavaRuntimeAreExecutable() throws Exception {
        Assume.assumeTrue("File modes exist on Unix only", new OSValidator().isUnix());

        // A runtime as a distribution unpacks it, with no file executable
        File runtime = new File(tmp.newFolder("download"), RUNTIME);
        write(runtime, "bin/java");
        write(runtime, "bin/keytool");
        write(runtime, "lib/jspawnhelper");
        write(runtime, "lib/modules");
        write(runtime, "release");

        NMFPackageBuilder builder = new NMFPackageBuilder(new MetadataJava("test-jre", "1.0"));
        builder.addFileOrDirectory(runtime);
        File pack = builder.createPackage(tmp.newFolder("package"));

        new NMFPackageManager(null).install(pack.getAbsolutePath(), root);

        File installed = new File(new File(root, Deployment.DIR_JAVA), RUNTIME);
        Assert.assertTrue("The java program must be executable", new File(installed, "bin/java").canExecute());
        Assert.assertTrue("Every program in bin must be executable", new File(installed, "bin/keytool").canExecute());
        Assert.assertTrue("The jspawnhelper, with which the runtime starts processes, must be executable",
                new File(installed, "lib/jspawnhelper").canExecute());
        Assert.assertFalse("A library must not be made executable", new File(installed, "lib/modules").canExecute());
        Assert.assertFalse("Any other file must not be made executable", new File(installed, "release").canExecute());
    }

    @Test
    public void onlyTheProgramsOfAJavaRuntimeAreProgramsOfIt() {
        Assert.assertTrue(NMFPackageManager.isJavaRuntimeProgram(Path.of("java", RUNTIME, "bin", "java")));
        Assert.assertTrue(NMFPackageManager.isJavaRuntimeProgram(Path.of("java", RUNTIME, "lib", "jspawnhelper")));
        Assert.assertTrue(NMFPackageManager.isJavaRuntimeProgram(Path.of("java", RUNTIME, "lib", "jexec")));
        Assert.assertFalse(NMFPackageManager.isJavaRuntimeProgram(Path.of("java", RUNTIME, "lib", "modules")));
        Assert.assertFalse(NMFPackageManager.isJavaRuntimeProgram(Path.of("java", RUNTIME, "release")));
        Assert.assertFalse(NMFPackageManager.isJavaRuntimeProgram(Path.of("bin")));
    }

    private static void write(File folder, String path) throws Exception {
        File file = new File(folder, path);
        file.getParentFile().mkdirs();
        Files.write(file.toPath(), "content".getBytes(StandardCharsets.UTF_8));
        Assert.assertTrue(file.setExecutable(false, false));
    }

    /** Points the framework at the given NMF root, answering the one it used before. */
    private static File setNMFRoot(File dir) throws Exception {
        Field field = Deployment.class.getDeclaredField("pathNMF");
        field.setAccessible(true);
        File previous = (File) field.get(null);
        field.set(null, dir);
        return previous;
    }
}
