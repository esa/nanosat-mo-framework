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

import esa.mo.nmf.accesscontrol.AccessControlMode;
import esa.mo.nmf.nmfpackage.metadata.InvalidPackageException;
import esa.mo.sm.impl.util.PMBackend;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.ccsds.moims.mo.com.InvalidArgumentException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.sm.ChecksumMismatchException;
import org.ccsds.moims.mo.sm.IntegrityCheckFailedException;
import org.ccsds.moims.mo.sm.PackageInstalledException;
import org.ccsds.moims.mo.sm.PackageNotInstalledException;
import org.ccsds.moims.mo.sm.PermissionsNotAcknowledgedException;
import org.ccsds.moims.mo.sm.packagemanagement.provider.UninstallInteraction;
import org.ccsds.moims.mo.sm.structures.PackageApproval;
import org.ccsds.moims.mo.sm.structures.PackageApprovalList;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Each reason for refusing a package has its own error, which names the
 * refused packages by their index in the request, and nothing is changed.
 */
public class PackageRefusalTest {

    private static final String GOOD = "good-1.0.nmfpack";
    private static final String DAMAGED = "damaged-1.0.nmfpack";
    private static final String OTHER_FILE = "other-file-1.0.nmfpack";
    private static final String UNACKNOWLEDGED = "unacknowledged-1.0.nmfpack";
    private static final String INSTALLED = "installed-1.0.nmfpack";
    private static final String NOT_INSTALLED = "not-installed-1.0.nmfpack";
    private static final String BASELINE = "baseline-1.0.nmfpack";
    private static final String INVALID = "invalid-1.0.nmfpack";
    private static final String INVALID_REASON = "The package name '../x' is not valid.";
    private static final String CHECKSUM = "ab".repeat(32);

    private PackageManagementProviderServiceImpl provider;
    private Backend backend;
    private String previousRequirement;
    private String previousMode;

    @Before
    public void setUp() throws Exception {
        previousRequirement = System.getProperty(PackageManagementProviderServiceImpl.PROPERTY_REQUIRE_CHECKSUM);
        previousMode = System.getProperty(AccessControlMode.PROPERTY);
        System.clearProperty(PackageManagementProviderServiceImpl.PROPERTY_REQUIRE_CHECKSUM);
        System.setProperty(AccessControlMode.PROPERTY, "enforce");
        provider = new PackageManagementProviderServiceImpl();
        backend = new Backend();
        Field field = PackageManagementProviderServiceImpl.class.getDeclaredField("backend");
        field.setAccessible(true);
        field.set(provider, backend);
    }

    @After
    public void restoreTheSettings() {
        restore(PackageManagementProviderServiceImpl.PROPERTY_REQUIRE_CHECKSUM, previousRequirement);
        restore(AccessControlMode.PROPERTY, previousMode);
    }

    @Test
    public void installNamesEachReasonWithItsOwnError() throws Exception {
        assertInstallRefused(PackageInstalledException.class, INSTALLED);
        assertInstallRefused(IntegrityCheckFailedException.class, DAMAGED);
        assertInstallRefused(ChecksumMismatchException.class, OTHER_FILE);
        assertInstallRefused(PermissionsNotAcknowledgedException.class, UNACKNOWLEDGED);
        Assert.assertTrue("No package may be installed", backend.changed.isEmpty());
    }

    @Test
    public void updateNamesEachReasonWithItsOwnError() throws Exception {
        assertUpdateRefused(PackageNotInstalledException.class, NOT_INSTALLED);
        assertUpdateRefused(InvalidArgumentException.class, BASELINE);
        assertUpdateRefused(IntegrityCheckFailedException.class, DAMAGED);
        assertUpdateRefused(ChecksumMismatchException.class, OTHER_FILE);
        assertUpdateRefused(PermissionsNotAcknowledgedException.class, UNACKNOWLEDGED);
        Assert.assertTrue("No package may be updated", backend.changed.isEmpty());
    }

    @Test
    public void uninstallNamesEachReasonWithItsOwnError() throws Exception {
        assertUninstallRefused(PackageNotInstalledException.class, NOT_INSTALLED);
        assertUninstallRefused(IntegrityCheckFailedException.class, DAMAGED);
        Assert.assertTrue("No package may be uninstalled", backend.changed.isEmpty());
    }

    @Test
    public void installAndUpdateGiveTheReasonWhyAPackageIsNotValid() throws Exception {
        try {
            provider.install(approvals(GOOD, INVALID), null);
            Assert.fail("The install of a package that is not valid must be refused");
        } catch (InvalidArgumentException error) {
            Assert.assertEquals(INVALID + ": " + INVALID_REASON, error.getExtraInformation().toString());
        }
        try {
            provider.update(approvals(GOOD, INVALID), null);
            Assert.fail("The update of a package that is not valid must be refused");
        } catch (InvalidArgumentException error) {
            Assert.assertEquals(INVALID + ": " + INVALID_REASON, error.getExtraInformation().toString());
        }
        Assert.assertTrue("No package may be changed", backend.changed.isEmpty());
    }

    /** Requests the good package and then the refused one, which must be named by its index, 1. */
    private void assertInstallRefused(Class<? extends MOErrorException> expected, String refused) {
        try {
            provider.install(approvals(GOOD, refused), null);
            Assert.fail("The install of " + refused + " must be refused");
        } catch (Exception error) {
            assertRefusedAtIndexOne(expected, error);
        }
    }

    private void assertUpdateRefused(Class<? extends MOErrorException> expected, String refused) {
        try {
            provider.update(approvals(GOOD, refused), null);
            Assert.fail("The update of " + refused + " must be refused");
        } catch (Exception error) {
            assertRefusedAtIndexOne(expected, error);
        }
    }

    private void assertUninstallRefused(Class<? extends MOErrorException> expected, String refused) {
        IdentifierList names = new IdentifierList();
        names.add(new Identifier(GOOD));
        names.add(new Identifier(refused));
        BooleanList keep = new BooleanList();
        keep.add(false);
        keep.add(false);
        try {
            provider.uninstall(names, keep, new UninstallInteraction(null) {
                @Override
                public MALMessage sendAcknowledgement() {
                    return null;
                }
            });
            Assert.fail("The uninstall of " + refused + " must be refused");
        } catch (Exception error) {
            assertRefusedAtIndexOne(expected, error);
        }
    }

    private static void assertRefusedAtIndexOne(Class<? extends MOErrorException> expected, Exception error) {
        Assert.assertEquals(expected, error.getClass());
        UIntegerList indexes = (UIntegerList) ((MOErrorException) error).getExtraInformation();
        Assert.assertEquals(1, indexes.size());
        Assert.assertEquals(new UInteger(1), indexes.get(0));
    }

    private static PackageApprovalList approvals(String... names) {
        PackageApprovalList approvals = new PackageApprovalList();
        for (String name : names) {
            approvals.add(new PackageApproval(new Identifier(name), CHECKSUM, null));
        }
        return approvals;
    }

    private static void restore(String property, String value) {
        if (value == null) {
            System.clearProperty(property);
        } else {
            System.setProperty(property, value);
        }
    }

    /** Packages that are each refused for one reason, and one that is not refused. */
    private static class Backend implements PMBackend {

        private final List<String> changed = new ArrayList<>();

        @Override
        public StringList getListOfPackages() {
            StringList list = new StringList();
            list.addAll(List.of(GOOD, DAMAGED, OTHER_FILE, UNACKNOWLEDGED, INSTALLED, NOT_INSTALLED, BASELINE, INVALID));
            return list;
        }

        @Override
        public void install(String packageName, List<String> acknowledged) {
            changed.add(packageName);
        }

        @Override
        public void uninstall(String packageName, boolean keepUserData) {
            changed.add(packageName);
        }

        @Override
        public void update(String packageName, List<String> acknowledged) {
            changed.add(packageName);
        }

        @Override
        public boolean stopApp(String packageName) {
            return true;
        }

        @Override
        public boolean isPackageInstalled(String packageName) {
            return !NOT_INSTALLED.equals(packageName);
        }

        @Override
        public boolean replacesInstalledPackage(String packageName) {
            return INSTALLED.equals(packageName);
        }

        @Override
        public String getPackageVersion(String packageName) {
            return NOT_INSTALLED.equals(packageName) ? null : "1.0";
        }

        @Override
        public void checkPackageIsValid(String packageName) throws InvalidPackageException {
            if (INVALID.equals(packageName)) {
                throw new InvalidPackageException(INVALID_REASON);
            }
        }

        @Override
        public boolean checkPackageIntegrity(String packageName) {
            return !DAMAGED.equals(packageName);
        }

        @Override
        public boolean isBaselineComponent(String packageName) {
            return BASELINE.equals(packageName);
        }

        @Override
        public List<String> getDeclaredPermissions(String packageName) {
            return UNACKNOWLEDGED.equals(packageName) ? List.of("Supervisor.Platform.Camera") : new ArrayList<>();
        }

        @Override
        public String getPackageChecksum(String packageName) {
            return OTHER_FILE.equals(packageName) ? "cd".repeat(32) : CHECKSUM;
        }
    }
}
