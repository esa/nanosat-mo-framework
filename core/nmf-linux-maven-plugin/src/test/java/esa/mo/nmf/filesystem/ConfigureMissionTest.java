/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft - v2.4
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * What {@code configure_mission.sh} makes of the designation of a spacecraft.
 * <p>
 * The script is run as a spacecraft runs it, so that what is tested is the
 * script itself rather than a reading of it. A machine without a shell to run
 * it with is left alone.
 */
public class ConfigureMissionTest {

    private static final String BUILT_WITH
            = "# NanoSat MO Framework - Mission and Spacecraft Designation\n"
            + "\n"
            + "mission.name=simulator-orekit\n"
            + "mission.fleet=false\n"
            + "spacecraft.name=simulator-orekit\n"
            + "spacecraft.node=1\n"
            + "organization.abbreviation=esa\n";

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private File nmfHome;

    private File properties;

    @Before
    public void layOutASpacecraft() throws IOException {
        Assume.assumeTrue("A shell is needed to run the script",
                new File("/bin/sh").canExecute());

        nmfHome = folder.newFolder("nanosat-mo-framework");
        File etc = new File(nmfHome, "etc");
        etc.mkdirs();

        properties = new File(etc, "mission.properties");
        Files.write(properties.toPath(), BUILT_WITH.getBytes(StandardCharsets.UTF_8));

        File script = new File(nmfHome, BootloaderGenerator.MISSION_SCRIPT);
        try (InputStream resource = ConfigureMissionTest.class.getClassLoader()
                .getResourceAsStream(BootloaderGenerator.MISSION_SCRIPT)) {
            assertTrue("The script is a resource of the plugin", resource != null);
            Files.copy(resource, script.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        script.setExecutable(true, false);
    }

    @Test
    public void aSpacecraftToldWhichOfAFleetItIsSaysSo() throws Exception {
        Map<String, String> environment = new HashMap<>();
        environment.put("MISSION_FLEET", "true");
        environment.put("SPACECRAFT_NODE", "2");
        environment.put("SPACECRAFT_NAME", "follower");

        run(environment);

        assertEquals("true", property("mission.fleet"));
        assertEquals("2", property("spacecraft.node"));
        assertEquals("follower", property("spacecraft.name"));
    }

    @Test
    public void aSpacecraftToldNothingKeepsWhatItWasBuiltWith() throws Exception {
        run(new HashMap<>());

        assertEquals(BUILT_WITH, new String(Files.readAllBytes(properties.toPath()),
                StandardCharsets.UTF_8));
    }

    @Test
    public void whatTheEnvironmentDoesNotSayIsLeftAlone() throws Exception {
        Map<String, String> environment = new HashMap<>();
        environment.put("SPACECRAFT_NODE", "7");

        run(environment);

        assertEquals("7", property("spacecraft.node"));
        assertEquals("false", property("mission.fleet"));
        assertEquals("simulator-orekit", property("spacecraft.name"));
        assertEquals("simulator-orekit", property("mission.name"));
        assertEquals("esa", property("organization.abbreviation"));
    }

    @Test
    public void aDesignationTheFileDoesNotCarryIsAdded() throws Exception {
        Files.write(properties.toPath(), "mission.name=barebone\n".getBytes(StandardCharsets.UTF_8));

        Map<String, String> environment = new HashMap<>();
        environment.put("SPACECRAFT_NODE", "3");

        run(environment);

        assertEquals("3", property("spacecraft.node"));
        assertEquals("barebone", property("mission.name"));
    }

    /**
     * Runs the script the way a spacecraft starts it.
     *
     * @param environment What the spacecraft is told when it is started.
     */
    private void run(Map<String, String> environment) throws Exception {
        ProcessBuilder builder = new ProcessBuilder("./" + BootloaderGenerator.MISSION_SCRIPT);
        builder.directory(nmfHome);
        builder.environment().keySet().removeAll(
                java.util.Arrays.asList("MISSION_FLEET", "SPACECRAFT_NODE", "SPACECRAFT_NAME"));
        builder.environment().putAll(environment);
        builder.redirectErrorStream(true);

        Process process = builder.start();
        String output = new String(readAll(process.getInputStream()), StandardCharsets.UTF_8);
        assertEquals("The script ended well. It said: " + output, 0, process.waitFor());
    }

    private static byte[] readAll(InputStream stream) throws IOException {
        java.io.ByteArrayOutputStream collected = new java.io.ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;
        while ((read = stream.read(buffer)) != -1) {
            collected.write(buffer, 0, read);
        }
        return collected.toByteArray();
    }

    /**
     * @param key The name of a line of the file.
     * @return What that line says, or null when the file has no such line.
     */
    private String property(String key) throws IOException {
        for (String line : Files.readAllLines(properties.toPath(), StandardCharsets.UTF_8)) {
            if (line.startsWith(key + "=")) {
                return line.substring(key.length() + 1);
            }
        }
        return null;
    }
}
