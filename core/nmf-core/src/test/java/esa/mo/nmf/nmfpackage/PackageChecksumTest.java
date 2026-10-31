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
package esa.mo.nmf.nmfpackage;

import esa.mo.nmf.nmfpackage.metadata.Metadata;
import esa.mo.nmf.nmfpackage.metadata.MetadataMission;
import esa.mo.nmf.nmfpackage.utils.ChecksumGenerator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Holds that each file of a package is checked by its SHA-256, from metadata
 * version 6, and by its CRC-32 only in a package written before it.
 * <p>
 * A CRC-32 catches accidental corruption, but a different file with the same
 * CRC-32 is easy to make. A file is now checked against its SHA-256, so it
 * must be the very file that the metadata lists.
 */
public class PackageChecksumTest {

    private static final String FILE = "jars-mission/1.0/a.jar";
    private static final byte[] CONTENT = "the jar that was listed".getBytes(StandardCharsets.UTF_8);
    private static final byte[] OTHER = "a different jar".getBytes(StandardCharsets.UTF_8);

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void aBuiltPackageListsTheSHA256OfEveryFile() throws IOException {
        File jar = tmp.newFile("a.jar");
        Files.write(jar.toPath(), CONTENT);
        NMFPackageBuilder builder = new NMFPackageBuilder(new MetadataMission("a-mission", "1.0"));
        builder.addFileOrDirectory(jar);
        File built = builder.createPackage(tmp.newFolder());

        try (NMFPackage pack = new NMFPackage(built.getAbsolutePath())) {
            Metadata metadata = pack.getMetadata();
            Assert.assertEquals(Metadata.METADATA_VERSION_SHA256, metadata.getMetadataVersion());
            Assert.assertEquals(1, metadata.getFiles().size());
            Assert.assertEquals("The metadata must list the SHA-256 of the file",
                    sha256(CONTENT), metadata.getFiles().get(0).getSHA256());
            pack.verifyPackageIntegrity();
        }

        try (ZipFile zip = new ZipFile(built)) {
            String written = new String(zip.getInputStream(zip.getEntry(Metadata.FILENAME)).readAllBytes(),
                    StandardCharsets.ISO_8859_1);
            Assert.assertFalse("No CRC-32 is written any more", written.contains(Metadata.FILE_CRC));
        }
    }

    @Test
    public void aFileThatIsNotTheListedOneIsRefused() throws IOException {
        // The metadata lists one file, the package holds another
        File pack = packageWith("6", Metadata.FILE_SHA256, sha256(CONTENT), OTHER);

        try (NMFPackage nmfPackage = new NMFPackage(pack.getAbsolutePath())) {
            nmfPackage.verifyPackageIntegrity();
            Assert.fail("A file that does not match its SHA-256 must be refused");
        } catch (IOException expected) {
            Assert.assertTrue(expected.getMessage(), expected.getMessage().contains("SHA-256 does not match"));
        }
    }

    @Test
    public void aCRCAloneNoLongerPasses() throws IOException {
        // A version 6 package that gives only the CRC-32 of its file, which
        // anyone can match, is refused before its files are even looked at
        assertRefused(packageWith("6", Metadata.FILE_CRC, crc(OTHER), OTHER), "SHA-256");
    }

    @Test
    public void aMalformedSHA256IsRefused() throws IOException {
        assertRefused(packageWith("6", Metadata.FILE_SHA256, "not-a-sha-256", CONTENT), "not-a-sha-256");
        assertRefused(packageWith("6", Metadata.FILE_SHA256, sha256(CONTENT).toUpperCase(), CONTENT),
                "lowercase");
    }

    @Test
    public void aPackageWrittenBeforeVersion6IsStillCheckedByItsCRC() throws IOException {
        File good = packageWith("5", Metadata.FILE_CRC, crc(CONTENT), CONTENT);
        try (NMFPackage nmfPackage = new NMFPackage(good.getAbsolutePath())) {
            nmfPackage.verifyPackageIntegrity();
        }

        File bad = packageWith("5", Metadata.FILE_CRC, crc(CONTENT), OTHER);
        try (NMFPackage nmfPackage = new NMFPackage(bad.getAbsolutePath())) {
            nmfPackage.verifyPackageIntegrity();
            Assert.fail("A file that does not match its CRC-32 must be refused");
        } catch (IOException expected) {
            Assert.assertTrue(expected.getMessage(), expected.getMessage().contains("CRC does not match"));
        }
    }

    /**
     * Writes a package by hand, as anyone could, holding one file with the
     * given content and listing it with the given checksum.
     */
    private File packageWith(String version, String checksumKey, String checksum, byte[] content)
            throws IOException {
        Metadata metadata = new MetadataMission("a-mission", "1.0");
        metadata.addProperty(Metadata.PACKAGE_METADATA_VERSION, version);
        metadata.addProperty(Metadata.FILE_COUNT, "1");
        metadata.addProperty(Metadata.FILE_PATH + ".0", FILE);
        metadata.addProperty(checksumKey + ".0", checksum);

        ByteArrayOutputStream properties = new ByteArrayOutputStream();
        metadata.store(properties);

        File pack = new File(tmp.newFolder(), "a-mission-1.0.nmfpack");
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(pack))) {
            zip.putNextEntry(new ZipEntry(Metadata.FILENAME));
            zip.write(properties.toByteArray());
            zip.closeEntry();
            zip.putNextEntry(new ZipEntry(FILE));
            zip.write(content);
            zip.closeEntry();
        }
        return pack;
    }

    private static void assertRefused(File pack, String reason) throws IOException {
        try (ZipFile zip = new ZipFile(pack)) {
            Metadata.parseZipFile(zip);
            Assert.fail("The package must be refused");
        } catch (IOException expected) {
            Assert.assertTrue(expected.getMessage(), expected.getMessage().contains(reason));
        }
    }

    private static String sha256(byte[] content) throws IOException {
        return ChecksumGenerator.sha256Hex(new ByteArrayInputStream(content));
    }

    private static String crc(byte[] content) {
        CRC32 crc = new CRC32();
        crc.update(content);
        return String.valueOf(crc.getValue());
    }
}
