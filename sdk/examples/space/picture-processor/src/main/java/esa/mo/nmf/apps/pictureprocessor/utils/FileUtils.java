/*
 * ----------------------------------------------------------------------------
 * Copyright (C) 2021 European Space Agency
 * European Space Operations Centre
 * Darmstadt
 * Germany
 * ----------------------------------------------------------------------------
 * System : ESA NanoSat MO Framework
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
package esa.mo.nmf.apps.pictureprocessor.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class FileUtils {

    private static final Logger LOG = Logger.getLogger(FileUtils.class.getName());

    public static Path createDirectoriesIfNotExist(final Path directory) {
        if (Files.exists(directory)) {
            return directory;
        }
        try {
            return Files.createDirectories(directory);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static OutputStream newOutpuStreamSafe(final Path file) {
        try {
            return Files.newOutputStream(file);
        } catch (final IOException e) {
            LOG.log(Level.SEVERE, "Failed to create output stream for file " + file, e);
            return null;
        }
    }

    public static void closeSafe(final OutputStream outputStream) {
        try {
            outputStream.close();
        } catch (final IOException e) {
            LOG.log(Level.SEVERE, "Failed to close output stream", e);
        }
    }

    public static Path stripFileNameExtension(final Path path) {
        final String fileName = path.getFileName().toString();
        final int extensionSeparatorIndex = fileName.lastIndexOf(".");
        if (extensionSeparatorIndex == -1) {
            return path;
        }
        return path.resolveSibling(fileName.substring(0, extensionSeparatorIndex));
    }

}
