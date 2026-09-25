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
package esa.mo.nmf.cmt.cli;

import esa.mo.nmf.cmt.utils.SegmentImage;
import java.io.File;
import java.io.IOException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * What the command line of the constellation is read as.
 */
public class ConstellationOptionsTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void theNumberOfSegmentsIsEnough() {
        ConstellationOptions options = ConstellationOptions.parse(new String[]{"--nodes", "3"});

        assertEquals(3, options.getNodes());
        assertEquals(ConstellationOptions.DEFAULT_NAME, options.getName());
        assertEquals(SegmentImage.getDefault(), options.getImage());
        assertNull(options.getContainerTool());
    }

    @Test
    public void everyOptionIsRead() {
        ConstellationOptions options = ConstellationOptions.parse(new String[]{
            "--nodes", "2", "--name", "swarm", "--image", "orekit",
            "--container-tool", "kubernetes"});

        assertEquals(2, options.getNodes());
        assertEquals("swarm", options.getName());
        assertEquals(SegmentImage.SIMULATOR_OREKIT, options.getImage());
        assertEquals("kubernetes", options.getContainerTool());
    }

    @Test
    public void theNameIsReducedToWhatNamesAContainer() {
        ConstellationOptions options = ConstellationOptions.parse(new String[]{
            "--nodes", "1", "--name", "my swarm/2"});

        assertEquals("myswarm2", options.getName());
    }

    @Test
    public void helpIsAskedForRatherThanAConstellation() {
        assertTrue(ConstellationOptions.parse(new String[]{"--help"}).isHelp());
        assertTrue(ConstellationOptions.parse(new String[]{"-h"}).isHelp());
    }

    @Test
    public void aFileOfOrbitsIsRead() throws IOException {
        File csv = folder.newFile("orbits.csv");
        ConstellationOptions options = ConstellationOptions.parse(new String[]{
            "--csv", csv.getPath(), "--image", "orekit"});

        assertEquals(csv, options.getCsv());
        assertEquals(0, options.getNodes());
        assertEquals(SegmentImage.SIMULATOR_OREKIT, options.getImage());
    }

    @Test
    public void aConstellationComesFromANumberOrAFileButNotBoth() throws IOException {
        File csv = folder.newFile("orbits.csv");
        assertRefused(new String[]{"--nodes", "2", "--csv", csv.getPath()});
    }

    @Test
    public void theSegmentsOfAFileAreNamedByIt() throws IOException {
        File csv = folder.newFile("orbits.csv");
        assertRefused(new String[]{"--csv", csv.getPath(), "--name", "swarm"});
    }

    @Test
    public void aFileThatIsNotThereIsRefused() {
        assertRefused(new String[]{"--csv", folder.getRoot().getPath() + "/missing.csv"});
    }

    @Test
    public void aConstellationHasToBeAskedForOneWayOrTheOther() {
        assertRefused(new String[]{"--name", "swarm"});
    }

    @Test
    public void aConstellationIsAtLeastOneSegment() {
        assertRefused(new String[]{"--nodes", "0"});
        assertRefused(new String[]{"--nodes", "-1"});
    }

    @Test
    public void theNumberOfSegmentsIsANumber() {
        assertRefused(new String[]{"--nodes", "three"});
    }

    @Test
    public void onlyAnImageOfTheFrameworkIsRun() {
        assertRefused(new String[]{"--nodes", "1", "--image", "hubble"});
    }

    @Test
    public void aNameWithNothingToItIsRefused() {
        assertRefused(new String[]{"--nodes", "1", "--name", "-/-"});
    }

    @Test
    public void anOptionOfNoValueIsRefused() {
        assertRefused(new String[]{"--nodes"});
    }

    @Test
    public void anOptionOfAnotherToolIsRefused() {
        assertRefused(new String[]{"--nodes", "1", "--detach"});
    }

    private static void assertRefused(String[] args) {
        try {
            ConstellationOptions.parse(args);
            fail("These arguments should have been refused: " + String.join(" ", args));
        } catch (IllegalArgumentException ex) {
            // What is expected of a command line that asks for no constellation.
        }
    }
}
