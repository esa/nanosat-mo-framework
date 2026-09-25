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
package esa.mo.nmf.testbed.e2e;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * A copy of the generated NMF filesystem, for the tests that change it.
 *
 * A test that installs, upgrades or rolls something back leaves the filesystem
 * in a state that the tests written for a single, freshly generated one are not
 * prepared for. Rather than have each of those tests put back what it changed,
 * which only works for the changes it remembered to undo, they work on a copy
 * of their own and leave it as dirty as they like.
 *
 * The copy is taken rather than a second filesystem generated, so that what is
 * exercised is the same filesystem the other tests run against, down to the
 * Apps that were installed into it.
 *
 * @author Cesar Coelho
 */
public class FilesystemHarness {

    /** The root of the copy. */
    protected final File nmfDir;

    /**
     * Takes a copy of the generated filesystem, replacing any earlier copy at
     * the destination so that a run never inherits the state of the last one.
     *
     * @param destination Where to place the copy.
     * @throws IOException if the filesystem cannot be copied, or the copy
     * cannot be written.
     */
    public FilesystemHarness(final File destination) throws IOException {
        String source = System.getProperty(SupervisorHarness.PROP_FILESYSTEM);
        if (source == null) {
            throw new IOException("System property '" + SupervisorHarness.PROP_FILESYSTEM
                    + "' is not set. Run via Maven (mvn test) so the filesystem is generated first.");
        }

        this.nmfDir = destination;
        deleteRecursively(destination);
        copyRecursively(new File(source).toPath(), destination.toPath());
    }

    /**
     * Returns the root of the copied filesystem.
     *
     * @return The root of the filesystem.
     */
    public File getNmfDir() {
        return nmfDir;
    }

    private static void copyRecursively(final Path source, final Path destination) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                Files.createDirectories(destination.resolve(source.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                Path target = destination.resolve(source.relativize(file));
                Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.COPY_ATTRIBUTES);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static void deleteRecursively(final File file) throws IOException {
        if (!file.exists()) {
            return;
        }
        Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(path);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException ex) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
