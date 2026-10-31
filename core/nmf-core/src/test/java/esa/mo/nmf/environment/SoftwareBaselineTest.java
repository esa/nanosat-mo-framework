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
package esa.mo.nmf.environment;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link SoftwareBaseline}, the read/write model of an NMF
 * Software Baseline file.
 */
public class SoftwareBaselineTest {

    @Test
    public void testStoreAndLoadRoundTrip() throws Exception {
        File dir = Files.createTempDirectory("baseline-test").toFile();
        File file = new File(dir, "baseline-primary.properties");

        SoftwareBaseline original = new SoftwareBaseline("5.0-SNAPSHOT", "1.0",
                "java/17/bin/java", "esa.mo.nmf.MyMission");
        original.store(file);

        SoftwareBaseline loaded = SoftwareBaseline.load(file);
        Assert.assertEquals(SoftwareBaseline.SCHEMA_VERSION, loaded.getSchemaVersion());
        Assert.assertEquals("5.0-SNAPSHOT", loaded.getNmfVersion());
        Assert.assertEquals("1.0", loaded.getMissionVersion());
        Assert.assertEquals("java/17/bin/java", loaded.getJava());
        Assert.assertEquals("esa.mo.nmf.MyMission", loaded.getMainClass());
    }

    @Test
    public void testStoreOverwritesAtomically() throws Exception {
        File dir = Files.createTempDirectory("baseline-test").toFile();
        File file = new File(dir, "baseline-primary.properties");

        new SoftwareBaseline("5.0", "1.0", "system", "A").store(file);
        new SoftwareBaseline("5.1", "1.1", "system", "B").store(file);

        SoftwareBaseline loaded = SoftwareBaseline.load(file);
        Assert.assertEquals("5.1", loaded.getNmfVersion());
        Assert.assertEquals("1.1", loaded.getMissionVersion());
        Assert.assertEquals("B", loaded.getMainClass());
        // No temporary file must be left behind
        Assert.assertFalse(new File(dir, "baseline-primary.properties.tmp").exists());
    }

    @Test
    public void testStoreRejectsLineBreaksInAField() throws Exception {
        File dir = Files.createTempDirectory("baseline-test").toFile();
        File file = new File(dir, "baseline-primary.properties");

        // A newline in a field would inject extra "key=value" lines
        SoftwareBaseline injected = new SoftwareBaseline("5.0", "1.0", "system",
                "Foo\njava=/tmp/evil");
        try {
            injected.store(file);
            Assert.fail("Storing a field with a line break must throw");
        } catch (IOException expected) {
            // The file must not have been created with the injected content
            Assert.assertFalse("No baseline file must be written on rejection", file.exists());
        }
    }

    @Test
    public void testLoadIncompleteFileYieldsNullFields() throws Exception {
        File dir = Files.createTempDirectory("baseline-test").toFile();
        File file = new File(dir, "baseline-secondary.properties");
        // A file missing the mission-version, java and main-class keys
        Files.write(file.toPath(),
                "schema-version=1\nnmf-version=5.0\n".getBytes(StandardCharsets.UTF_8));

        SoftwareBaseline loaded = SoftwareBaseline.load(file);
        Assert.assertEquals("5.0", loaded.getNmfVersion());
        Assert.assertNull("Missing keys must load as null", loaded.getMissionVersion());
        Assert.assertNull(loaded.getJava());
        Assert.assertNull(loaded.getMainClass());
    }
}
