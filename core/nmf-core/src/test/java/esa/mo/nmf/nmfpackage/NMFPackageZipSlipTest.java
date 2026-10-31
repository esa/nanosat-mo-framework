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
package esa.mo.nmf.nmfpackage;

import java.io.IOException;
import java.nio.file.Path;
import esa.mo.nmf.nmfpackage.utils.HelperNMFPackage;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;

/**
 * Holds the extraction of a package to the folder it is being extracted to.
 *
 * A package is not to be trusted: it can be built by anyone and it names the
 * files it carries. A name such as "../../etc/passwd" would write outside the
 * folder if it were joined to it, which is the attack known as Zip Slip.
 *
 * The names are therefore never joined. They are taken apart, and each piece
 * has to be a plain file name before it is appended, so these tests hold that
 * every way of writing a name that leaves the folder is refused, and that the
 * names of real files still arrive where they belong.
 */
public class NMFPackageZipSlipTest {

    private static final Path DESTINATION
            = Paths.get("/tmp/nmf-extract-test").toAbsolutePath().normalize();

    /**
     * Names that climb out of the folder, in each of the ways they can be
     * written. Every one of these has to be refused.
     */
    private static final String[] CLIMBS_OUT = {
        "../sneaky-file",
        "../../etc/passwd",
        "a/../../b",
        "a/b/../../../c",
        "a/./../../b",
        "..",
        "../",
        "/../etc/passwd",
        // Written with the separator of the other system
        "\\..\\sneaky",
        "..\\..\\windows\\system32\\evil.dll",
        "a\\..\\..\\b",
        // Nothing but dots: Windows drops the dots at the end of a name, so
        // these can still be read as the folder above once the file is opened
        "...",
        "....",
        "a/.../b",
        "..../../x",
        "....//....//etc/passwd"
    };

    /**
     * Names that a package carries in the ordinary course of things.
     */
    private static final String[] ORDINARY = {
        "normal.txt",
        "a/b/c.txt",
        "lib/nested/deep/file.jar",
        "a//b",
        "./a/b"
    };

    /**
     * Every way of naming the folder above is refused.
     */
    @Test
    public void namesThatClimbOutAreRefused() {
        for (String name : CLIMBS_OUT) {
            try {
                Path resolved = HelperNMFPackage.resolveInside(DESTINATION, name);
                Assert.fail("The name \"" + name + "\" was allowed, and resolved to: " + resolved);
            } catch (IOException expected) {
                // This is what a name that leaves the folder has to give
            }
        }
    }

    /**
     * Whatever a package names, the file lands under the folder it is being
     * extracted to. This holds for the names above and for anything else that
     * is allowed through.
     */
    @Test
    public void nothingIsEverWrittenOutsideTheFolder() throws IOException {
        for (String name : ORDINARY) {
            Path resolved = HelperNMFPackage.resolveInside(DESTINATION, name);
            Assert.assertTrue("The name \"" + name + "\" resolved outside the folder: " + resolved,
                    resolved.normalize().startsWith(DESTINATION));
        }
    }

    /**
     * A name that starts again from a root is not refused but stripped of it,
     * so the file is kept rather than the install failing, and it still lands
     * under the folder.
     */
    @Test
    public void namesThatStartFromARootAreBroughtInside() throws IOException {
        Assert.assertEquals(DESTINATION.resolve("etc").resolve("passwd"),
                HelperNMFPackage.resolveInside(DESTINATION, "/etc/passwd"));
        Assert.assertEquals(DESTINATION.resolve("etc").resolve("passwd"),
                HelperNMFPackage.resolveInside(DESTINATION, "//etc/passwd"));
    }

    /**
     * The names of real files arrive where the package says they go.
     */
    @Test
    public void ordinaryNamesResolveWhereTheyBelong() throws IOException {
        Assert.assertEquals(DESTINATION.resolve("normal.txt"),
                HelperNMFPackage.resolveInside(DESTINATION, "normal.txt"));
        Assert.assertEquals(DESTINATION.resolve("a").resolve("b").resolve("c.txt"),
                HelperNMFPackage.resolveInside(DESTINATION, "a/b/c.txt"));
        Assert.assertEquals(DESTINATION.resolve("lib").resolve("nested").resolve("file.jar"),
                HelperNMFPackage.resolveInside(DESTINATION, "lib/nested/file.jar"));
    }

    /**
     * A separator that names nothing is passed over, so a name written with one
     * too many of them still reaches the same file.
     */
    @Test
    public void separatorsThatNameNothingArePassedOver() throws IOException {
        Path expected = DESTINATION.resolve("a").resolve("b");
        Assert.assertEquals(expected, HelperNMFPackage.resolveInside(DESTINATION, "a//b"));
        Assert.assertEquals(expected, HelperNMFPackage.resolveInside(DESTINATION, "./a/b"));
        Assert.assertEquals(expected, HelperNMFPackage.resolveInside(DESTINATION, "a/./b"));
    }

    /**
     * A name that names no file at all is refused rather than answered with the
     * folder itself, which would then be opened as if it were a file.
     */
    @Test
    public void namesThatNameNoFileAreRefused() {
        for (String name : new String[]{"", ".", "/", "//", "./"}) {
            try {
                Path resolved = HelperNMFPackage.resolveInside(DESTINATION, name);
                Assert.fail("The name \"" + name + "\" was allowed, and resolved to: " + resolved);
            } catch (IOException expected) {
                // This is what a name that names no file has to give
            }
        }
    }

    /**
     * A file whose name begins with a dot is an ordinary file and is kept.
     */
    @Test
    public void hiddenFilesAreStillFiles() throws IOException {
        Assert.assertEquals(DESTINATION.resolve(".hidden"),
                HelperNMFPackage.resolveInside(DESTINATION, ".hidden"));
        Assert.assertEquals(DESTINATION.resolve("a").resolve(".config"),
                HelperNMFPackage.resolveInside(DESTINATION, "a/.config"));
    }
}
