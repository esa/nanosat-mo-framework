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
package esa.mo.nmf.nmfpackage.utils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link ChecksumGenerator}, in particular the
 * {@code verifyChecksums} check performed against baseline directories.
 */
public class ChecksumGeneratorTest {

    private static File newDirWithFiles() throws Exception {
        File dir = Files.createTempDirectory("checksum-test").toFile();
        Files.write(new File(dir, "a.jar").toPath(), "alpha".getBytes(StandardCharsets.UTF_8));
        Files.write(new File(dir, "b.jar").toPath(), "beta".getBytes(StandardCharsets.UTF_8));
        return dir;
    }

    @Test
    public void testWriteThenVerifyPasses() throws Exception {
        File dir = newDirWithFiles();
        ChecksumGenerator.writeChecksumsFile(dir);
        Assert.assertTrue(new File(dir, ChecksumGenerator.CHECKSUMS_FILENAME).isFile());
        Assert.assertTrue("A freshly generated manifest must verify",
                ChecksumGenerator.verifyChecksums(dir));
    }

    @Test
    public void testTamperedFileFailsVerification() throws Exception {
        File dir = newDirWithFiles();
        ChecksumGenerator.writeChecksumsFile(dir);
        // Corrupt one of the files after the manifest was generated
        Files.write(new File(dir, "a.jar").toPath(), "tampered".getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse("A modified file must fail verification",
                ChecksumGenerator.verifyChecksums(dir));
    }

    @Test
    public void testMissingFileFailsVerification() throws Exception {
        File dir = newDirWithFiles();
        ChecksumGenerator.writeChecksumsFile(dir);
        Assert.assertTrue(new File(dir, "b.jar").delete());
        Assert.assertFalse("A missing listed file must fail verification",
                ChecksumGenerator.verifyChecksums(dir));
    }

    @Test
    public void testMissingManifestFailsVerification() throws Exception {
        File dir = newDirWithFiles();
        Assert.assertFalse("A directory without a manifest must not verify",
                ChecksumGenerator.verifyChecksums(dir));
    }
}
