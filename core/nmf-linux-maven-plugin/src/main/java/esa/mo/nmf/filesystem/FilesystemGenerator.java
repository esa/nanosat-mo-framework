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
package esa.mo.nmf.filesystem;

import esa.mo.nmf.environment.Deployment;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.artifact.Artifact;

/**
 * The FilesystemBuilder class.
 *
 * @author Cesar Coelho
 */
public class FilesystemGenerator {

    private final static File DIR_TARGET = new File("target");
    private final static File DIR_FILESYSTEM = new File(DIR_TARGET, "space-filesystem");
    private final static File DIR_NMF = new File(DIR_FILESYSTEM, Deployment.DIR_NMF);

    /**
     * The Constructor.
     */
    public FilesystemGenerator() {
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
            File destination = new File(DIR_NMF, nest);
            File newFile = new File(destination, f.getName());
            try {
                Files.copy(f.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                Logger.getLogger(FilesystemGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("File copied to: " + newFile.getAbsolutePath());
        }
    }

    public void addFileOrDirectory(File file) {
        addFileOrDirectory(file.getAbsolutePath(), "");
    }

    public void addResource(String directory, String filename) {
        ClassLoader classLoader = GenerateFilesystemMojo.class.getClassLoader();
        File destinationDirectory = new File(DIR_NMF, directory);
        destinationDirectory.mkdirs();
        File destinationFile = new File(destinationDirectory, filename);

        try (InputStream inputStream = classLoader.getResourceAsStream(filename)) {
            if (inputStream == null) {
                System.out.println("Resource not found.");
                return;
            }

            Files.copy(inputStream, destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Resource copied to: " + destinationFile.getAbsolutePath());
        } catch (IOException ex) {
            Logger.getLogger(GenerateFilesystemMojo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addArtifact(String directory, Artifact artifact) {
        File from = artifact.getFile();
        File destinationDirectory = new File(DIR_NMF, directory);
        destinationDirectory.mkdirs();
        File destinationFile = new File(destinationDirectory, from.getName());

        try {
            Files.copy(from.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(FilesystemGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
