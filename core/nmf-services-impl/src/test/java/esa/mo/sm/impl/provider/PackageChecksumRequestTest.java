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
import java.util.ArrayList;
import java.util.List;
import org.ccsds.moims.mo.com.InvalidArgumentException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.sm.structures.PackageApproval;
import org.ccsds.moims.mo.sm.structures.PackageApprovalList;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Holds that install and update use a package file only if it matches the
 * checksum sent with the request, and that a provider refuses a request
 * without one unless it is configured not to require one.
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
        Assert.assertTrue(provider.isChecksumAccepted(ON_BOARD, PACKAGE));
        Assert.assertTrue("Case and surrounding spaces do not matter",
                provider.isChecksumAccepted(" " + ON_BOARD.toUpperCase() + " ", PACKAGE));
    }

    @Test
    public void aChecksumThatDoesNotMatchIsRefused() throws IOException {
        Assert.assertFalse(provider.isChecksumAccepted(OTHER, PACKAGE));
    }

    @Test
    public void aChecksumThatDoesNotMatchIsRefusedEvenWhenNoneIsRequired() throws Exception {
        System.setProperty(PackageManagementProviderServiceImpl.PROPERTY_REQUIRE_CHECKSUM, "false");
        try {
            provider.install(approvals(OTHER), null);
            Assert.fail("An install whose package file does not match its checksum must be refused");
        } catch (InvalidArgumentException expected) {
            // The package is refused before anything is acknowledged
        }
        try {
            provider.update(approvals(OTHER), null);
            Assert.fail("An update whose package file does not match its checksum must be refused");
        } catch (InvalidArgumentException expected) {
            // The package is refused before anything is acknowledged
        }
    }

    @Test
    public void noChecksumIsAcceptedWhenNoneIsRequired() throws IOException {
        System.setProperty(PackageManagementProviderServiceImpl.PROPERTY_REQUIRE_CHECKSUM, "false");
        Assert.assertTrue(provider.isChecksumAccepted(null, PACKAGE));
        System.setProperty(PackageManagementProviderServiceImpl.PROPERTY_REQUIRE_CHECKSUM, " False ");
        Assert.assertTrue("Case and surrounding spaces do not matter", provider.isChecksumAccepted(null, PACKAGE));
    }

    @Test
    public void withoutTheSettingAChecksumIsRequired() throws IOException {
        // A Supervisor started without the bootloader, such as from an IDE
        Assert.assertTrue(PackageManagementProviderServiceImpl.isChecksumRequired());
        Assert.assertFalse(provider.isChecksumAccepted(null, PACKAGE));
    }

    @Test
    public void aSettingOtherThanFalseRequiresAChecksum() {
        for (String value : new String[]{"", "true", "no", "off", "0", "flase"}) {
            System.setProperty(PackageManagementProviderServiceImpl.PROPERTY_REQUIRE_CHECKSUM, value);
            Assert.assertTrue("The setting '" + value + "'", PackageManagementProviderServiceImpl.isChecksumRequired());
        }
    }

    @Test
    public void noChecksumIsRefusedWhenOneIsRequired() throws Exception {
        System.setProperty(PackageManagementProviderServiceImpl.PROPERTY_REQUIRE_CHECKSUM, "true");

        Assert.assertFalse(provider.isChecksumAccepted(null, PACKAGE));
        Assert.assertTrue("A matching checksum is still accepted",
                provider.isChecksumAccepted(ON_BOARD, PACKAGE));
        try {
            provider.install(approvals((String) null), null);
            Assert.fail("An install without a checksum must be refused when one is required");
        } catch (InvalidArgumentException expected) {
            // The package is refused before anything is acknowledged
        }
    }

    /** A request for the one package, with the given expected checksum and no permissions. */
    private static PackageApprovalList approvals(String checksum) {
        PackageApprovalList approvals = new PackageApprovalList();
        approvals.add(new PackageApproval(new Identifier(PACKAGE), checksum, null));
        return approvals;
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
        public void install(String packageName, List<String> acknowledged) {
            throw new AssertionError("Nothing may be installed in this test");
        }

        @Override
        public void uninstall(String packageName, boolean keepUserData) {
            throw new AssertionError("Nothing may be uninstalled in this test");
        }

        @Override
        public void update(String packageName, List<String> acknowledged) {
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
        public List<String> getDeclaredPermissions(String packageName) {
            return new ArrayList<>();
        }

        @Override
        public String getPackageChecksum(String packageName) {
            return ON_BOARD;
        }
    }
}
