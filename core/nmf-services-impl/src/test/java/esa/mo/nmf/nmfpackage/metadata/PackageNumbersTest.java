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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;

/**
 * Holds that the numbers in the metadata of a package are checked when the
 * metadata is read.
 *
 * A package is not to be trusted: a metadata version, a file count or a CRC
 * that is not a number refuses the package with an IOException, before
 * anything is installed, instead of failing later with a NumberFormatException.
 */
public class PackageNumbersTest {

    private static final String NAME = Metadata.PACKAGE_NAME + "=hello-world\n";

    @Test
    public void wellFormedNumbersAreRead() throws IOException {
        Metadata metadata = load(NAME
                + Metadata.PACKAGE_METADATA_VERSION + "=5\n"
                + Metadata.FILE_COUNT + "=1\n"
                + Metadata.FILE_PATH + ".0=apps/hello-world/hello-world.jar\n"
                + Metadata.FILE_CRC + ".0=12345\n");

        Assert.assertEquals(5, metadata.getMetadataVersion());
        Assert.assertEquals(1, metadata.getFiles().size());
        Assert.assertEquals(12345L, metadata.getFiles().get(0).getCRC());
    }

    @Test
    public void aMetadataVersionThatIsNotANumberIsRefused() {
        assertRefused(NAME + Metadata.PACKAGE_METADATA_VERSION + "=five\n", "five");
    }

    @Test
    public void aFileCountThatIsNotANumberIsRefused() {
        assertRefused(NAME + Metadata.FILE_COUNT + "=many\n", "many");
    }

    @Test
    public void aCRCThatIsNotANumberIsRefused() {
        assertRefused(NAME
                + Metadata.FILE_COUNT + "=1\n"
                + Metadata.FILE_PATH + ".0=apps/hello-world/hello-world.jar\n"
                + Metadata.FILE_CRC + ".0=0xBAD\n", "0xBAD");
    }

    @Test
    public void aFileWithoutAPathIsRefused() {
        assertRefused(NAME
                + Metadata.FILE_COUNT + "=1\n"
                + Metadata.FILE_CRC + ".0=12345\n", "no path");
    }

    private static Metadata load(String properties) throws IOException {
        return Metadata.load(new ByteArrayInputStream(properties.getBytes(StandardCharsets.ISO_8859_1)));
    }

    private static void assertRefused(String properties, String reason) {
        try {
            load(properties);
            Assert.fail("the metadata must be refused");
        } catch (IOException expected) {
            Assert.assertTrue(expected.getMessage(), expected.getMessage().contains(reason));
        }
    }
}
