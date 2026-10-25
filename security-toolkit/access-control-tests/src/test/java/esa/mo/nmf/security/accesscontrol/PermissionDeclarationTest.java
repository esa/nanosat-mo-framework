/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2026      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 *  You may not use this file except in compliance with the License.
 *
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ----------------------------------------------------------------------------
 */
package esa.mo.nmf.security.accesscontrol;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

/**
 * The permissions an App declares in the nmf-package-maven-plugin
 * configuration of its pom.xml are written into its package, where the
 * Supervisor reads them at installation.
 */
public class PermissionDeclarationTest {

    private static final File PROBE_TARGET = new File("../app-permissions-probe/target");

    @Test
    public void thePackageCarriesThePermissionsDeclaredInThePom() throws Exception {
        File[] packages = PROBE_TARGET.listFiles((d, n) -> n.endsWith(".nmfpack"));
        Assume.assumeTrue("Skipping: build the permissions probe app (produces its .nmfpack) first.",
                packages != null && packages.length > 0);

        Properties metadata = new Properties();
        try (ZipFile zip = new ZipFile(packages[0])) {
            ZipEntry entry = zip.getEntry("package-metadata.properties");
            Assert.assertNotNull("The package must have its metadata", entry);
            try (InputStream in = zip.getInputStream(entry)) {
                metadata.load(in);
            }
        }

        Assert.assertEquals("The package must list the permissions declared in the probe's pom.xml",
                "Supervisor.MC.Parameter", metadata.getProperty("pack.app.permissions"));
    }
}
