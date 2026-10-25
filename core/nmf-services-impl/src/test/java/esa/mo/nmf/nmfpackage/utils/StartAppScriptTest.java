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
package esa.mo.nmf.nmfpackage.utils;

import esa.mo.nmf.environment.Deployment;
import esa.mo.nmf.nmfpackage.metadata.Metadata;
import esa.mo.nmf.nmfpackage.metadata.MetadataApp;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * NMF-2026-008: the values of an App go into its start script, so a package
 * must not be able to have the shell run commands through them.
 * <p>
 * The values are checked when a package is read, and each one is quoted in the
 * generated {@code start_app.sh}. The last test generates the script from values
 * that hold shell syntax, runs it with a real {@code /bin/sh} and a stand-in for
 * {@code java}, and checks that no command in them was run.
 */
public class StartAppScriptTest {

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

    /** Shell syntax that runs a command or splits a value, in the forms the report lists. */
    private static final String[] PAYLOADS = {
        "$(id)", "`id`", "a;id", "a&id", "a|id", "a id", "a'id", "a\"id", "a\nid", "a$b", "a>b", "a*"
    };

    @Test
    public void launchValuesOfRealPackagesAreAccepted() throws IOException {
        new MetadataApp("hello-world", "6.0", "esa.mo.nmf.apps.DemoHelloWorldSimple",
                "hello-world-6.0-SNAPSHOT.jar", "128m", "32m",
                new ArrayList<>(Arrays.asList("orekit-11.3.2.jar", "hipparchus-core-2.3.jar")),
                "6.0").checkLaunchValues();
        Assert.assertTrue(MetadataApp.isValidMainClass("esa.mo.nmf.apps.broken.BrokenApp"));
        Assert.assertTrue(MetadataApp.isValidMainClass("Main"));
        Assert.assertTrue(MetadataApp.isValidHeapSize("1g"));
        Assert.assertTrue(MetadataApp.isValidHeapSize("512"));
    }

    @Test
    public void launchValuesWithShellSyntaxAreRefused() {
        for (String payload : PAYLOADS) {
            // '$' is part of Java names (nested classes), so it is quoted in the script rather than refused
            if (!payload.equals("a$b")) {
                Assert.assertFalse("main class '" + payload + "'", MetadataApp.isValidMainClass("esa." + payload));
            }
            Assert.assertFalse("jar '" + payload + "'", MetadataApp.isValidJarName(payload + ".jar"));
            Assert.assertFalse("heap '" + payload + "'", MetadataApp.isValidHeapSize("128m" + payload));
        }
        Assert.assertFalse(MetadataApp.isValidMainClass(null));
        Assert.assertFalse(MetadataApp.isValidMainClass(""));
        Assert.assertFalse(MetadataApp.isValidMainClass("esa..App"));
        Assert.assertFalse(MetadataApp.isValidMainClass("esa.App."));
        Assert.assertFalse(MetadataApp.isValidMainClass("esa.1App"));
        Assert.assertFalse("control characters must be refused", MetadataApp.isValidMainClass("esa.A\u0000pp"));
        Assert.assertFalse(MetadataApp.isValidJarName("../other.jar"));
        Assert.assertFalse(MetadataApp.isValidJarName("-option.jar"));
        Assert.assertFalse(MetadataApp.isValidHeapSize("m"));
        Assert.assertFalse(MetadataApp.isValidHeapSize(null));
    }

    @Test
    public void aPackageWithShellSyntaxInALaunchValueCannotBeRead() throws IOException {
        String[][] cases = {
            {MetadataApp.APP_MAINCLASS, "$(touch /tmp/marker)"},
            {MetadataApp.APP_MAIN_JAR, "`touch /tmp/marker`.jar"},
            {MetadataApp.APP_MAX_HEAP, "128m;touch /tmp/marker"},
            {MetadataApp.APP_MIN_HEAP, "32m|touch /tmp/marker"},
            {MetadataApp.APP_DEPENDENCIES, "a.jar;$(touch /tmp/marker).jar"},};

        for (String[] c : cases) {
            Properties props = validAppProperties();
            props.setProperty(c[0], c[1]);
            try (ZipFile zip = packageWith(props)) {
                Metadata.parseZipFile(zip);
                Assert.fail("a package with " + c[0] + "=" + c[1] + " must not be read");
            } catch (IOException expected) {
                Assert.assertTrue(expected.getMessage(), expected.getMessage().contains(c[0]));
            }
        }

        try (ZipFile zip = packageWith(validAppProperties())) {
            Assert.assertEquals("hello-world", Metadata.parseZipFile(zip).getPackageName());
        }
    }

    @Test
    public void aPackageWhoseJarsPointOutsideItsAppCannotBeRead() throws IOException {
        // The jar of another installed App, or any other file, must not be loaded
        String[] paths = {
            "../hello-world/hello-world-5.1.jar",
            "../../apps/hello-world/hello-world-5.1.jar",
            "/home/nmf/apps/hello-world/hello-world-5.1.jar",
            "lib/../../hello-world/hello-world-5.1.jar",
            "..\\hello-world\\hello-world-5.1.jar",
            "..",
            ".",
            ".hidden.jar"};

        for (String path : paths) {
            for (String key : new String[]{MetadataApp.APP_MAIN_JAR, MetadataApp.APP_DEPENDENCIES}) {
                Properties props = validAppProperties();
                props.setProperty(key, path);
                try (ZipFile zip = packageWith(props)) {
                    Metadata.parseZipFile(zip);
                    Assert.fail("a package with " + key + "=" + path + " must not be read");
                } catch (IOException expected) {
                    Assert.assertTrue(expected.getMessage(), expected.getMessage().contains(key));
                }
            }
        }
    }

    @Test
    public void theStartScriptRunsNoCommandFromTheLaunchValues() throws Exception {
        Assume.assumeTrue("A POSIX shell is needed", new File("/bin/sh").canExecute());

        File marker = new File(tmp.getRoot(), "marker");
        File args = new File(tmp.getRoot(), "java-args");
        String touch = "touch " + marker.getAbsolutePath();

        // A stand-in for java that records the arguments it was given, one per line.
        File java = new File(tmp.getRoot(), "java");
        Files.write(java.toPath(), ("#!/bin/sh\nfor a in \"$@\"; do printf '%s\\n' \"$a\"; done > '"
                + args.getAbsolutePath() + "'\n").getBytes(StandardCharsets.UTF_8));
        Assert.assertTrue(java.setExecutable(true));

        // The values bypass the check on purpose: the script must be safe on its own.
        String mainClass = "$(" + touch + ")";
        MetadataApp meta = new MetadataApp("probe", "1.0", mainClass,
                "`" + touch + "`.jar", "1m;" + touch, "1m\n" + touch,
                new ArrayList<>(Arrays.asList("a.jar$(" + touch + ")")), "6.0");

        File appDir = new File(root, "apps/probe");
        Assert.assertTrue(appDir.mkdirs());
        File script = new File(appDir, "start_app.sh");
        Files.write(script.toPath(), AuxFilesGenerator.generateLinuxStartAppScript(java.getAbsolutePath(), meta)
                .getBytes(StandardCharsets.UTF_8));

        ProcessBuilder pb = new ProcessBuilder("/bin/sh", script.getAbsolutePath());
        pb.environment().remove("JAVA_OPTS");
        pb.redirectErrorStream(true);
        pb.redirectOutput(new File(tmp.getRoot(), "script-output"));
        Process p = pb.start();
        Assert.assertTrue("the start script must finish", p.waitFor(30, TimeUnit.SECONDS));

        Assert.assertTrue("the stand-in for java must have run", args.isFile());
        Assert.assertFalse("no command from the launch values may run", marker.exists());
        List<String> received = Files.readAllLines(args.toPath(), StandardCharsets.UTF_8);
        Assert.assertTrue("java must get the main class as one argument, as it is: " + received,
                received.contains(mainClass));
    }

    private static Properties validAppProperties() {
        Properties props = new Properties();
        props.setProperty(Metadata.PACKAGE_TYPE, Metadata.TYPE_APP);
        props.setProperty(Metadata.PACKAGE_NAME, "hello-world");
        props.setProperty(MetadataApp.APP_MAINCLASS, "esa.mo.nmf.apps.DemoHelloWorldSimple");
        props.setProperty(MetadataApp.APP_MAIN_JAR, "hello-world-6.0-SNAPSHOT.jar");
        props.setProperty(MetadataApp.APP_MAX_HEAP, "128m");
        props.setProperty(MetadataApp.APP_MIN_HEAP, "32m");
        return props;
    }

    /** Writes a package file holding only the given metadata. */
    private ZipFile packageWith(Properties props) throws IOException {
        File file = tmp.newFile();
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(file))) {
            zip.putNextEntry(new ZipEntry(Metadata.FILENAME));
            props.store(zip, null);
            zip.closeEntry();
        }
        return new ZipFile(file);
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
