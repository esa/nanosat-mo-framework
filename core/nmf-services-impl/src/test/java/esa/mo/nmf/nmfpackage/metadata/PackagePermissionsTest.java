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
import java.util.Arrays;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Holds that the permissions an App declares are read from its package, and
 * that a package declaring something that is not a permission is refused.
 */
public class PackagePermissionsTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void theDeclaredPermissionsAreRead() throws IOException {
        try (ZipFile zip = packageDeclaring("Supervisor.Platform.Camera;App.cloud-filter.*")) {
            MetadataApp app = Metadata.parseZipFile(zip).castToApp();
            Assert.assertEquals(Arrays.asList("Supervisor.Platform.Camera", "App.cloud-filter.*"),
                    app.getAppPermissions());
        }
    }

    @Test
    public void anAppThatDeclaresNothingHasNoPermissions() throws IOException {
        try (ZipFile zip = packageDeclaring(null)) {
            Assert.assertTrue(Metadata.parseZipFile(zip).castToApp().getAppPermissions().isEmpty());
        }
    }

    @Test
    public void aPackageDeclaringSomethingThatIsNotAPermissionIsRefused() throws IOException {
        try (ZipFile zip = packageDeclaring("Supervisor.Platform.Camera;Supervisor.Everything")) {
            Metadata.parseZipFile(zip);
            Assert.fail("A package declaring something that is not a permission must not be read");
        } catch (IOException expected) {
            Assert.assertTrue(expected.getMessage(), expected.getMessage().contains("Supervisor.Everything"));
        }
    }

    /** Writes an App package holding only its metadata, declaring the given permissions. */
    private ZipFile packageDeclaring(String permissions) throws IOException {
        Properties props = new Properties();
        props.setProperty(Metadata.PACKAGE_TYPE, Metadata.TYPE_APP);
        props.setProperty(Metadata.PACKAGE_NAME, "hello-world");
        props.setProperty(MetadataApp.APP_MAINCLASS, "esa.mo.nmf.apps.DemoHelloWorldSimple");
        props.setProperty(MetadataApp.APP_MAIN_JAR, "hello-world-6.0-SNAPSHOT.jar");
        props.setProperty(MetadataApp.APP_MAX_HEAP, "128m");
        props.setProperty(MetadataApp.APP_MIN_HEAP, "32m");
        if (permissions != null) {
            props.setProperty(MetadataApp.APP_PERMISSIONS, permissions);
        }

        File file = tmp.newFile();
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(file))) {
            zip.putNextEntry(new ZipEntry(Metadata.FILENAME));
            props.store(zip, null);
            zip.closeEntry();
        }
        return new ZipFile(file);
    }
}
