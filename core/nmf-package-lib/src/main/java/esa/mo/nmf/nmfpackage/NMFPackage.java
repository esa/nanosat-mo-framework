/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft â€“ v2.4
 * You may not use this zipFile except in compliance with the License.
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

import esa.mo.nmf.nmfpackage.utils.HelperNMFPackage;
import esa.mo.nmf.nmfpackage.metadata.Metadata;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * The NMFPackage class holds the information contained in an NMF Package.
 *
 * @author Cesar Coelho
 */
public class NMFPackage {

    private final ZipFile zipFile;
    private Metadata metadata;

    /**
     * Constructor.
     *
     * @param filepath The path to the NMF Package file.
     * @throws IOException If there is a problem with the file.
     */
    public NMFPackage(String filepath) throws IOException {
        this.zipFile = new ZipFile(filepath);
    }

    /**
     * Returns the metadata of the NMF Package.
     *
     * @return The metadata of the NMF Package.
     * @throws IOException If the metadata from the NMF Package could not be
     * parsed.
     */
    public synchronized Metadata getMetadata() throws IOException {
        if (metadata != null) {
            return metadata;
        }

        metadata = Metadata.parseZipFile(zipFile);
        return metadata;
    }

    /**
     * Returns the CRC of the NMF Package metadata.
     *
     * @return The CRC of the NMF Package metadata.
     * @throws IOException If the CRC could not be calculated.
     */
    public long calculateMetadataCRC() throws IOException {
        ZipEntry entry = zipFile.getEntry(Metadata.FILENAME);

        if (entry == null) {
            entry = zipFile.getEntry(HelperNMFPackage.RECEIPT_FILENAME);
        }

        long crc;
        try (InputStream zis = zipFile.getInputStream(entry)) {
            crc = HelperNMFPackage.calculateCRCFromInputStream(zis);
        }

        return crc;
    }

    /**
     * Returns the zip entry for the selected path.
     *
     * @param path The path to the zip entry.
     * @return The ZipEntry for the selected path.
     */
    public ZipEntry getZipFileEntry(String path) {
        ZipEntry entry = zipFile.getEntry(path);

        if (entry == null) {
            entry = zipFile.getEntry(path.replace("\\", "/"));
        }

        return entry;
    }

    /**
     * Verifies the package integrity. Checks if the CRCs of the files match.
     * This prevents package tainting.
     *
     * @throws IOException
     */
    public void verifyPackageIntegrity() throws IOException {
        ArrayList<NMFPackageFile> files = this.getMetadata().getFiles();

        for (int i = 0; i < files.size(); i++) {
            NMFPackageFile filepack = files.get(i);
            String path = filepack.getPath().trim();
            ZipEntry entry = this.getZipFileEntry(path);

            if (entry == null) {
                StringBuilder allEntries = new StringBuilder();
                for (Object o : zipFile.stream().toArray()) {
                    allEntries.append(((ZipEntry) o).getName()).append("\n");
                }

                throw new IOException("(" + i + "/" + files.size() + ")"
                        + " The metadata is incorrect. "
                        + "One of the files does not exist: " + path
                        + "\nAvailable entries:\n" + allEntries.toString());
            }

            // Are all the declared files matching their CRCs?
            if (filepack.getCRC() != entry.getCrc()) {
                throw new IOException("The CRC does not match!");
            }
        }
    }

    /**
     * Extracts the files in this package to the selected folder.
     *
     * @param toFolder The folder where the files will be extracted to.
     * @throws IOException If the files could not be extracted.
     */
    public void extractFiles(File toFolder) throws IOException {
        File newFile;
        byte[] buffer = new byte[8192];

        // Iterate through the files, unpack them into the right folders
        ArrayList<NMFPackageFile> files = this.getMetadata().getFiles();

        for (int i = 0; i < files.size(); i++) {
            NMFPackageFile file = files.get(i);
            ZipEntry entry = this.getZipFileEntry(file.getPath());

            String path = HelperNMFPackage.sanitizePath(entry.getName());
            newFile = new File(toFolder.getCanonicalPath() + File.separator + path);
            File parent = newFile.getParentFile();

            if (!parent.exists()) {
                parent.mkdirs();
            }

            System.out.println("   >> Copying file to: " + newFile.getCanonicalPath());

            FileOutputStream fos = new FileOutputStream(newFile);
            InputStream zis = zipFile.getInputStream(entry);
            int len;

            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }

            fos.close();

            long crc = HelperNMFPackage.calculateCRCFromFile(newFile.getCanonicalPath());

            // We will also need to double check the CRCs again against the real files!
            // Just to double-check.. better safe than sorry!
            if (file.getCRC() != crc) {
                throw new IOException("The CRC does not match!");
            }
        }
    }
}
