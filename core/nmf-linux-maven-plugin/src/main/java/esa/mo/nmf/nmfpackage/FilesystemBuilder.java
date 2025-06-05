/* ----------------------------------------------------------------------------
 * Copyright (C) 2022      European Space Agency
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
import esa.mo.nmf.nmfpackage.utils.HelperNMFPackage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The FilesystemBuilder class.
 *
 * @author Cesar Coelho
 */
public class FilesystemBuilder {

    private final ArrayList<String> inputFiles = new ArrayList<>();
    private final ArrayList<String> locations = new ArrayList<>();

    /**
     * The Constructor.
     */
    public FilesystemBuilder() {
    }

    /**
     * Adds a File or Directory to the builder.
     *
     * @param path The path to the file or directory.
     * @param nest The nest of the file or directory.
     */
    public void addFileOrDirectory(String path, String nest) {
        File f = new File(path);

        if (f.isDirectory()) {
            nest += f.getName() + File.separator;

            for (File n : f.listFiles()) {
                addFileOrDirectory(n.getAbsolutePath(), nest);
            }
        } else {
            inputFiles.add(f.getAbsolutePath());
        }
    }

    public void addFileOrDirectory(File file) {
        addFileOrDirectory(file.getAbsolutePath(), "");
    }

    /**
     * Creates the filesystem for the NanoSat MO Framework.
     *
     * @param destinationFolder The destination folder to create the Filesystem.
     */
    public void createFilesystem(File destinationFolder) {
        String path = destinationFolder.getAbsolutePath();
        create(inputFiles, locations, path);
    }

    private static boolean create(ArrayList<String> filesInput,
            ArrayList<String> newLocationsInput, String destinationFolder) {
        final ArrayList<String> files = new ArrayList<>(filesInput);
        final ArrayList<String> newLocations = new ArrayList<>(newLocationsInput);
        int size = newLocations.size();

        for (int i = 0; i < size; i++) {
            try {
                String path = newLocations.get(i);
                long crc = HelperNMFPackage.calculateCRCFromFile(files.get(i));
                String index = "." + i;
            } catch (IOException ex) {
                Logger.getLogger(FilesystemBuilder.class.getName()).log(Level.SEVERE,
                        "There was a problem during the CRC calculation.", ex);
            }
        }

        // Generate metadata.properties
        Logger.getLogger(FilesystemBuilder.class.getName()).log(
                Level.INFO, "Generating metadata file...");

        File metadataFile = new File(Metadata.FILENAME);

        // Add the metadata file to the list of Files to be zipped
        files.add(metadataFile.getPath());
        newLocations.add(Metadata.FILENAME);
        // -------------------------------------------------------------------

        // Delete temporary files:
        metadataFile.delete();
        //digitalSignature.delete();

        return true;
    }

    public void addResource(String etc, String filename) {
        ClassLoader classLoader = GenerateLinuxFilesystemMojo.class.getClassLoader();
        File destinationFile = new File("target", filename);

        try (InputStream inputStream = classLoader.getResourceAsStream(filename)) {
            if (inputStream == null) {
                System.out.println("Resource not found.");
                return;
            }

            Files.copy(inputStream, destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Resource copied to: " + destinationFile.getAbsolutePath());
        } catch (IOException ex) {
            Logger.getLogger(GenerateLinuxFilesystemMojo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
