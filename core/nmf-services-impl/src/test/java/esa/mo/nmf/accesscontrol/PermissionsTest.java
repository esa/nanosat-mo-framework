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
package esa.mo.nmf.accesscontrol;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

/**
 * Holds the form of a permission: whose service it is, then the area and the
 * service exactly as in the MO service definitions.
 */
public class PermissionsTest {

    private static final String[] VALID = {
        "Supervisor.Platform.Camera",
        "Supervisor.Platform.AutonomousADCS",
        "Supervisor.SM.CommandExecutor",
        "Supervisor.SM.PackageManagement",
        "Supervisor.MC.Action",
        "Supervisor.COM.Archive",
        "App.*",
        "App.cloud-filter.*",
        "App.vesseldetector.*"
    };

    private static final String[] INVALID = {
        "",
        "Supervisor",
        "Supervisor.*",
        "Supervisor.MC",
        "Supervisor.MC.*",
        "Supervisor.Platform.Camra",
        "Supervisor.platform.camera",
        "supervisor.Platform.Camera",
        "Supervisor.MAL.Anything",
        "App",
        "App.cloud-filter",
        "App.cloud-filter.MC.Action",
        "App..*",
        "App.bad name.*",
        "App.-option.*",
        " Supervisor.Platform.Camera"
    };

    @Test
    public void permissionsOfTheRightFormAreValid() {
        for (String permission : VALID) {
            Assert.assertTrue("'" + permission + "' must be valid", Permissions.isValid(permission));
        }
    }

    @Test
    public void anythingElseIsInvalid() {
        Assert.assertFalse("a missing permission is not valid", Permissions.isValid(null));
        for (String permission : INVALID) {
            Assert.assertFalse("'" + permission + "' must not be valid", Permissions.isValid(permission));
        }
    }

    @Test
    public void theServicesAreExactlyThoseOfTheMOServiceDefinitions() throws IOException {
        File xml = new File("../mo-services-xml/src/main/resources/xml");
        Assume.assumeTrue("The MO service definitions are not next to this module", xml.isDirectory());

        Set<String> defined = new TreeSet<>();
        Pattern area = Pattern.compile("<mal:area name=\"([^\"]+)\"");
        Pattern service = Pattern.compile("<mal:service name=\"([^\"]+)\"");
        for (File f : xml.listFiles((d, n) -> n.matches("area\\d+-.*\\.xml"))) {
            String text = new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
            Matcher a = area.matcher(text);
            if (!a.find()) {
                continue;
            }
            Matcher s = service.matcher(text);
            while (s.find()) {
                defined.add(a.group(1) + "." + s.group(1));
            }
        }

        Assert.assertEquals("The services that a permission can name must be those of the MO XML",
                defined, new TreeSet<>(Permissions.services()));
    }
}
