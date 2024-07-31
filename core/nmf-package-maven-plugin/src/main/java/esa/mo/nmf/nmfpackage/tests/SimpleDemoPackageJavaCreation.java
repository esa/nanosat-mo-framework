/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft â€“ v2.4
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
package esa.mo.nmf.nmfpackage.tests;

import esa.mo.nmf.nmfpackage.NMFPackageBuilder;
import esa.mo.nmf.nmfpackage.metadata.MetadataJava;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.codehaus.plexus.util.FileUtils;

/**
 * A simple demo code to test the generation of NMF Packages.
 *
 * @author Cesar Coelho
 */
public class SimpleDemoPackageJavaCreation {

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        SimpleDemoPackageJavaCreation.createPackages();
    }

    public static File downloadFile(URL url) throws IOException {
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        String filename = new File(url.getPath()).getName();
        FileOutputStream fileOutputStream = new FileOutputStream(filename);

        Logger.getLogger(SimpleDemoPackageJavaCreation.class.getName()).log(
                Level.INFO, "Downloading file: " + url.toString());

        long timestamp = System.currentTimeMillis();
        fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        timestamp = System.currentTimeMillis() - timestamp;
        Logger.getLogger(SimpleDemoPackageJavaCreation.class.getName()).log(
                Level.INFO, "Downloaded in " + timestamp + " ms!");

        return new File(filename);
    }

    public static void extractFileTo(File input, File outputFolder) throws IOException {
        GZIPInputStream zipIS = new GZIPInputStream(new FileInputStream(input));
        TarArchiveInputStream tarInput = new TarArchiveInputStream(zipIS);
        outputFolder.mkdirs();
        String outputPath = outputFolder.getAbsolutePath();

        TarArchiveEntry entry;
        int BUFFER_SIZE = 1024;

        while ((entry = tarInput.getNextTarEntry()) != null) {
            File newFile = new File(outputPath, entry.getName());

            //Create directories as required
            if (entry.isDirectory()) {
                newFile.mkdirs();
            } else {
                int count;
                byte data[] = new byte[BUFFER_SIZE];
                FileOutputStream fos = new FileOutputStream(newFile);
                BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER_SIZE);
                while ((count = tarInput.read(data, 0, BUFFER_SIZE)) != -1) {
                    dest.write(data, 0, count);
                }
                dest.close();
            }
        }
    }

    public static void createJavaPackage(String link) {
        try {
            // Download files
            URL url = new URL(link);
            File downloadedFile = downloadFile(url);
            Logger.getLogger(SimpleDemoPackageJavaCreation.class.getName()).log(Level.INFO,
                    "The file was downloaded in path: " + downloadedFile.getAbsolutePath());

            // Extract them
            File extractionTarget = new File("temp_extracted_files");
            Logger.getLogger(SimpleDemoPackageJavaCreation.class.getName()).log(Level.INFO,
                    "Extracting files: " + extractionTarget.getAbsolutePath());
            extractFileTo(downloadedFile, extractionTarget);

            for (File javaDir : extractionTarget.listFiles()) {
                Logger.getLogger(SimpleDemoPackageJavaCreation.class.getName()).log(
                        Level.INFO, "Generating NMF Package...");

                // Package them
                MetadataJava metadata = new MetadataJava(downloadedFile.getName(), "1.0");
                NMFPackageBuilder builder = new NMFPackageBuilder(metadata);
                builder.addFileOrDirectory(javaDir);
                File location = builder.createPackage(new File("target"));
                Logger.getLogger(SimpleDemoPackageJavaCreation.class.getName()).log(
                        Level.INFO, "NMF Package in: " + location.getAbsolutePath());
            }

            Logger.getLogger(SimpleDemoPackageJavaCreation.class.getName()).log(
                    Level.INFO, "Deleting files...");

            // Delete the downloaded file
            FileUtils.forceDelete(downloadedFile);

            // Delete the extracted folder:
            FileUtils.forceDelete(extractionTarget);
        } catch (IOException ex) {
            Logger.getLogger(SimpleDemoPackageJavaCreation.class.getName()).log(
                    Level.SEVERE, "Something went wrong...", ex);
        }
    }

    public static void createPackages() {
        // Files can be downloaded from:
        // https://adoptium.net/temurin/archive/

        // url = new URL("https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u292-b10/OpenJDK8U-jdk_arm_linux_hotspot_8u292b10.tar.gz");
        String jdk8 = "https://github.com/adoptium/temurin8-binaries/releases/download/jdk8u352-b08/OpenJDK8U-jdk_arm_linux_hotspot_8u352b08.tar.gz";
        createJavaPackage(jdk8);

        // Repo here: https://github.com/AdoptOpenJDK/openjdk11-binaries
        String jdk11 = "https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.9.1%2B1/OpenJDK11U-jdk_arm_linux_hotspot_11.0.9.1_1.tar.gz";
        //createJavaPackage(jdk11);

        // Repo here: https://github.com/AdoptOpenJDK/openjdk17-binaries
        String jdk17 = "https://github.com/AdoptOpenJDK/openjdk17-binaries/releases/download/jdk-2021-05-07-13-31/OpenJDK-debugimage_aarch64_linux_hotspot_2021-05-06-23-30.tar.gz";
        //createJavaPackage(jdk17);
    }
}
