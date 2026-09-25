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
package esa.mo.nmf.cmt.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * What a file of orbits is read as.
 */
public class SegmentOrbitsTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void everySegmentKeepsItsOwnOrbit() throws IOException {
        Map<String, String[]> orbits = read(
                "# name;A;E;i;RAAN;ARG_PER;TRUE_A",
                "",
                "leader;7021.0;0.0;98.05;340.0;0.0;0.0",
                "follower;7021.0;0.0;98.05;340.0;0.0;10.0");

        assertEquals(2, orbits.size());
        assertArrayEquals(new String[]{"7021.0", "0.0", "98.05", "340.0", "0.0", "0.0"},
                orbits.get("leader"));
        assertArrayEquals(new String[]{"7021.0", "0.0", "98.05", "340.0", "0.0", "10.0"},
                orbits.get("follower"));
    }

    @Test
    public void theSegmentsComeInTheOrderTheFileGivesThem() throws IOException {
        Map<String, String[]> orbits = read(
                "third;7021.0;0.0;98.05;340.0;0.0;20.0",
                "first;7021.0;0.0;98.05;340.0;0.0;0.0",
                "second;7021.0;0.0;98.05;340.0;0.0;10.0");

        assertEquals(new ArrayList<>(orbits.keySet()).toString(), "[third, first, second]");
    }

    @Test
    public void spaceAroundTheFieldsIsNotPartOfThem() throws IOException {
        Map<String, String[]> orbits = read("  leader ; 7021.0 ;0.0;98.05;340.0;0.0;0.0");

        assertArrayEquals(new String[]{"7021.0", "0.0", "98.05", "340.0", "0.0", "0.0"},
                orbits.get("leader"));
    }

    @Test
    public void aLineOfTheWrongLengthIsRefused() {
        assertRefused("leader;7021.0;0.0;98.05;340.0;0.0");
        assertRefused("leader;7021.0;0.0;98.05;340.0;0.0;0.0;0.0");
    }

    @Test
    public void anElementThatIsNoNumberIsRefused() {
        assertRefused("leader;7021.0;0.0;polar;340.0;0.0;0.0");
    }

    @Test
    public void aSegmentWithoutANameIsRefused() {
        assertRefused(";7021.0;0.0;98.05;340.0;0.0;0.0");
    }

    @Test
    public void twoSegmentsOfOneNameAreRefused() {
        assertRefused("leader;7021.0;0.0;98.05;340.0;0.0;0.0",
                "leader;7021.0;0.0;98.05;340.0;0.0;10.0");
    }

    @Test
    public void aFileDescribingNoSegmentIsRefused() {
        assertRefused("# a heading and nothing else", "");
    }

    private Map<String, String[]> read(String... lines) throws IOException {
        File file = folder.newFile();
        Files.write(file.toPath(), String.join("\n", lines).getBytes(StandardCharsets.UTF_8));
        return SegmentOrbits.read(file);
    }

    private void assertRefused(String... lines) {
        try {
            read(lines);
            fail("These lines should have been refused: " + String.join(" / ", lines));
        } catch (IllegalArgumentException ex) {
            // What is expected of a file that describes no constellation.
        } catch (IOException ex) {
            fail("The file could not be written: " + ex);
        }
    }
}
