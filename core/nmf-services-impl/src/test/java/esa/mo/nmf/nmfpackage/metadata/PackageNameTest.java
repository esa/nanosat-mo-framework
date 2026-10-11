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
package esa.mo.nmf.nmfpackage.metadata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Holds the names a package may have.
 *
 * A package is not to be trusted: it can be built by anyone and it gives its
 * own name. The name becomes the App's directory, part of its Linux user name
 * and part of the command that starts it, so it has to be one plain file name.
 * These tests hold that the names of real packages are accepted, that any other
 * name is refused, and that a package carrying such a name cannot be read.
 */
public class PackageNameTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    /** Names of real packages: Apps, dependencies, baselines and Java runtimes. */
    private static final String[] ACCEPTED = {
        "privilege-probe",
        "hello-world",
        "camera-acquisitor-system",
        "broken-app-for-testbeds",
        "commons-lang3",
        "nmf-core",
        "OpenJDK21U-jre_x64_linux_hotspot_21.0.4_7.tar.gz",
        "jdk-21.0.4+7"
    };

    /** Names that are not one plain file name. Every one of these is refused. */
    private static final String[] REFUSED = {
        "",
        ".",
        "..",
        "...",
        ".hidden",
        "-option",
        "a/b",
        "../b",
        "a\\b",
        "a b",
        "a;b",
        "a&b",
        "a|b",
        "a$b",
        "a`b",
        "a'b",
        "a\"b",
        "a\nb",
        "a".repeat(129)
    };

    @Test
    public void namesOfRealPackagesAreAccepted() {
        for (String name : ACCEPTED) {
            Assert.assertTrue("'" + name + "' must be accepted", Metadata.isValidPackageName(name));
        }
    }

    @Test
    public void namesThatAreNotOnePlainFileNameAreRefused() {
        Assert.assertFalse("a missing name must be refused", Metadata.isValidPackageName(null));
        for (String name : REFUSED) {
            Assert.assertFalse("'" + name + "' must be refused", Metadata.isValidPackageName(name));
        }
    }

    @Test
    public void aPackageWithAValidNameCanBeRead() throws IOException {
        try (ZipFile zip = packageNamed("hello-world")) {
            Assert.assertEquals("hello-world", Metadata.parseZipFile(zip).getPackageName());
        }
    }

    @Test
    public void aPackageWithAnInvalidNameCannotBeRead() throws IOException {
        try (ZipFile zip = packageNamed("not a plain name")) {
            Metadata.parseZipFile(zip);
            Assert.fail("a package whose name is not one plain file name must not be read");
        } catch (IOException expected) {
            Assert.assertTrue(expected.getMessage(), expected.getMessage().contains("not a plain name"));
        }
    }

    /** Writes a package file holding only its metadata, with the given name. */
    private ZipFile packageNamed(String name) throws IOException {
        File file = tmp.newFile();
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(file))) {
            zip.putNextEntry(new ZipEntry(Metadata.FILENAME));
            String properties = Metadata.PACKAGE_NAME + "=" + name + "\n"
                    + Metadata.PACKAGE_TYPE + "=" + Metadata.TYPE_APP + "\n";
            zip.write(properties.getBytes(StandardCharsets.ISO_8859_1));
            zip.closeEntry();
        }
        return new ZipFile(file);
    }
}
