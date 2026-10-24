/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft - v2.4
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

import esa.mo.nmf.nmfpackage.metadata.Metadata;
import esa.mo.nmf.nmfpackage.utils.ChecksumGenerator;
import esa.mo.nmf.nmfpackage.utils.HelperNMFPackage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * The NMFPackage class holds the information contained in an NMF Package.
 *
 * @author Cesar Coelho
 */
public class NMFPackage implements AutoCloseable {

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
     * Closes the NMF Package file.
     *
     * @throws IOException If the file cannot be closed.
     */
    @Override
    public void close() throws IOException {
        zipFile.close();
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
        try ( InputStream zis = zipFile.getInputStream(entry)) {
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
     * Verifies the package integrity: each file listed in the metadata must be
     * in the package and match its SHA-256 (its CRC-32, in a package written
     * before metadata version 6).
     * <p>
     * This proves that the files are the ones the metadata lists. It does not
     * prove who made the package: whoever makes a package also writes its
     * metadata.
     *
     * @throws IOException If the integrity check fails.
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

            // Are all the declared files matching their checksums?
            if (filepack.getSHA256() != null) {
                String sha256;
                try (InputStream in = zipFile.getInputStream(entry)) {
                    sha256 = ChecksumGenerator.sha256Hex(in);
                }
                if (!filepack.getSHA256().equals(sha256)) {
                    throw new IOException("The SHA-256 does not match for the file: " + path);
                }
            } else if (filepack.getCRC() != entry.getCrc()) {
                throw new IOException("The CRC does not match for the file: " + path);
            }
        }
    }

    /**
     * Works out where each file of this package goes under the given NMF root,
     * holding the package to the area that belongs to its type (see
     * {@link HelperNMFPackage#resolveInsideArea}). Nothing is written.
     *
     * @param nmfRoot The NMF root the package is to be extracted to.
     * @return Where each file goes, in the order of the metadata.
     * @throws IOException If a file is missing from the package, or lies
     * outside the package's area.
     */
    public ArrayList<Path> placeFiles(File nmfRoot) throws IOException {
        final Path root = nmfRoot.getCanonicalFile().toPath();
        ArrayList<NMFPackageFile> files = this.getMetadata().getFiles();
        ArrayList<Path> targets = new ArrayList<>(files.size());

        for (NMFPackageFile file : files) {
            ZipEntry entry = this.getZipFileEntry(file.getPath());

            if (entry == null) {
                throw new IOException("The package does not hold one of the files"
                        + " that its metadata lists: " + file.getPath());
            }

            targets.add(HelperNMFPackage.resolveInsideArea(root, this.getMetadata(), entry.getName()));
        }

        return targets;
    }

    /**
     * Extracts the files in this package to the selected folder.
     *
     * @param toFolder The folder where the files will be extracted to.
     * @throws IOException If the files could not be extracted.
     */
    public void extractFiles(File toFolder) throws IOException {
        byte[] buffer = new byte[8192];

        final Path destination = toFolder.getCanonicalFile().toPath();

        // Every file is placed before any is written, so a package with one
        // file outside its area writes nothing at all
        ArrayList<Path> targets = this.placeFiles(toFolder);

        // Iterate through the files, unpack them into the right folders
        ArrayList<NMFPackageFile> files = this.getMetadata().getFiles();

        for (int i = 0; i < files.size(); i++) {
            NMFPackageFile file = files.get(i);
            ZipEntry entry = this.getZipFileEntry(file.getPath());
            Path newFile = targets.get(i).normalize();

            // placeFiles has already held the file inside the folder. It is checked
            // again here, right before it is written, in the form that code scanners
            // recognise as a containment check.
            if (!newFile.startsWith(destination)) {
                throw new IOException("The package holds an entry outside the folder"
                        + " it is extracted to: " + entry.getName());
            }

            Files.createDirectories(newFile.getParent());
            System.out.println("   >> Copying file to: " + newFile);

            try (InputStream zis = zipFile.getInputStream(entry);
                    OutputStream fos = Files.newOutputStream(newFile)) {
                int len;

                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
            }

            // The written file is checked again, against the same checksum
            if (file.getSHA256() != null) {
                if (!file.getSHA256().equals(ChecksumGenerator.sha256Hex(newFile.toFile()))) {
                    throw new IOException("The SHA-256 does not match for the file: " + newFile);
                }
            } else if (file.getCRC() != HelperNMFPackage.calculateCRCFromFile(newFile.toString())) {
                throw new IOException("The CRC does not match for the file: " + newFile);
            }
        }
    }

}
