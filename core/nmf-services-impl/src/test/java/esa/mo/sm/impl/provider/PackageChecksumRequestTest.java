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
package esa.mo.sm.impl.provider;

import esa.mo.sm.impl.util.PMBackend;
import java.io.IOException;
import java.lang.reflect.Field;
import org.ccsds.moims.mo.com.InvalidArgumentException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Holds that install and update use a package file only if it matches the
 * checksum sent with the request, and that a provider configured to require a
 * checksum refuses a request without one.
 */
public class PackageChecksumRequestTest {

    private static final String PACKAGE = "probe-1.0.nmfpack";
    private static final String ON_BOARD = "ab".repeat(32);
    private static final String OTHER = "cd".repeat(32);

    private PackageManagementProviderServiceImpl provider;
    private String previousRequirement;

    @Before
    public void setUp() throws Exception {
        previousRequirement = System.getProperty(PackageManagementProviderServiceImpl.PROPERTY_REQUIRE_CHECKSUM);
        System.clearProperty(PackageManagementProviderServiceImpl.PROPERTY_REQUIRE_CHECKSUM);
        provider = new PackageManagementProviderServiceImpl();
        Field field = PackageManagementProviderServiceImpl.class.getDeclaredField("backend");
        field.setAccessible(true);
        field.set(provider, new OnePackageBackend());
    }

    @After
    public void restoreTheRequirement() {
        if (previousRequirement == null) {
            System.clearProperty(PackageManagementProviderServiceImpl.PROPERTY_REQUIRE_CHECKSUM);
        } else {
            System.setProperty(PackageManagementProviderServiceImpl.PROPERTY_REQUIRE_CHECKSUM, previousRequirement);
        }
    }

    @Test
    public void aMatchingChecksumIsAccepted() throws IOException {
        Assert.assertTrue(provider.isChecksumAccepted(checksums(ON_BOARD), 0, PACKAGE));
        Assert.assertTrue("Case and surrounding spaces do not matter",
                provider.isChecksumAccepted(checksums(" " + ON_BOARD.toUpperCase() + " "), 0, PACKAGE));
    }

    @Test
    public void aChecksumThatDoesNotMatchIsRefused() throws IOException {
        Assert.assertFalse(provider.isChecksumAccepted(checksums(OTHER), 0, PACKAGE));
    }

    @Test
    public void aChecksumThatDoesNotMatchIsRefusedEvenWhenNoneIsRequired() throws Exception {
        try {
            provider.install(names(), checksums(OTHER), null);
            Assert.fail("An install whose package file does not match its checksum must be refused");
        } catch (InvalidArgumentException expected) {
            // The package is refused before anything is acknowledged
        }
        try {
            provider.update(names(), checksums(OTHER), null);
            Assert.fail("An update whose package file does not match its checksum must be refused");
        } catch (InvalidArgumentException expected) {
            // The package is refused before anything is acknowledged
        }
    }

    @Test
    public void noChecksumIsAcceptedWhenNoneIsRequired() throws IOException {
        Assert.assertTrue(provider.isChecksumAccepted(null, 0, PACKAGE));
    }

    @Test
    public void noChecksumIsRefusedWhenOneIsRequired() throws Exception {
        System.setProperty(PackageManagementProviderServiceImpl.PROPERTY_REQUIRE_CHECKSUM, "true");

        Assert.assertFalse(provider.isChecksumAccepted(null, 0, PACKAGE));
        Assert.assertTrue("A matching checksum is still accepted",
                provider.isChecksumAccepted(checksums(ON_BOARD), 0, PACKAGE));
        try {
            provider.install(names(), null, null);
            Assert.fail("An install without a checksum must be refused when one is required");
        } catch (InvalidArgumentException expected) {
            // The package is refused before anything is acknowledged
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void checksumsMustMatchTheNamesOneToOne() throws Exception {
        provider.install(names(), checksums(ON_BOARD, ON_BOARD), null);
    }

    private static IdentifierList names() {
        IdentifierList names = new IdentifierList();
        names.add(new Identifier(PACKAGE));
        return names;
    }

    private static StringList checksums(String... values) {
        StringList list = new StringList();
        for (String value : values) {
            list.add(value);
        }
        return list;
    }

    /** One package on board, valid, not installed, whose file has the checksum ON_BOARD. */
    private static class OnePackageBackend implements PMBackend {

        @Override
        public StringList getListOfPackages() {
            StringList list = new StringList();
            list.add(PACKAGE);
            return list;
        }

        @Override
        public void install(String packageName) {
            throw new AssertionError("Nothing may be installed in this test");
        }

        @Override
        public void uninstall(String packageName, boolean keepUserData) {
            throw new AssertionError("Nothing may be uninstalled in this test");
        }

        @Override
        public void update(String packageName) {
            throw new AssertionError("Nothing may be updated in this test");
        }

        @Override
        public boolean isPackageInstalled(String packageName) {
            return false;
        }

        @Override
        public boolean isFinalVersionInstalled(String packageName) {
            return false;
        }

        @Override
        public String getPackageVersion(String packageName) {
            return "1.0";
        }

        @Override
        public boolean checkPackageIntegrity(String packageName) {
            return true;
        }

        @Override
        public boolean isBaselineComponent(String packageName) {
            return false;
        }

        @Override
        public String getPackageChecksum(String packageName) {
            return ON_BOARD;
        }
    }
}
