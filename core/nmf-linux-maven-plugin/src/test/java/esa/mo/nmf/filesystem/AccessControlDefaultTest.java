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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;

/**
 * Holds that access control is enforced unless the mission chooses otherwise:
 * both when the filesystem is generated without the {@code accessControl}
 * parameter, and when the Supervisor starts from a configuration without the
 * {@code access-control} key.
 */
public class AccessControlDefaultTest {

    @Test
    public void aFilesystemIsGeneratedInEnforceModeByDefault() throws IOException {
        String descriptor = read("META-INF/maven/plugin.xml");
        Matcher m = Pattern.compile("<accessControl implementation=\"java.lang.String\" default-value=\"([a-z]+)\"")
                .matcher(descriptor);
        Assert.assertTrue("The plugin descriptor must declare the accessControl parameter", m.find());
        Assert.assertEquals("The default mode of a generated filesystem", "enforce", m.group(1));
    }

    @Test
    public void theSupervisorStartsInEnforceModeWithoutTheKey() throws IOException {
        String script = read("start_supervisor.sh");
        Assert.assertTrue("Without access-control in its configuration, the Supervisor must start in enforce mode",
                script.contains("\nACCESS_CONTROL=enforce\n"));
    }

    private static String read(String resource) throws IOException {
        try (InputStream in = AccessControlDefaultTest.class.getClassLoader().getResourceAsStream(resource)) {
            Assert.assertNotNull("Missing resource: " + resource, in);
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
