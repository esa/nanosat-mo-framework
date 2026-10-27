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
import org.ccsds.moims.mo.mal.InternalException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.sm.packagemanagement.provider.InstallInteraction;
import org.ccsds.moims.mo.sm.packagemanagement.provider.UninstallInteraction;
import org.ccsds.moims.mo.sm.packagemanagement.provider.UpdateInteraction;
import org.ccsds.moims.mo.sm.structures.PackageApproval;
import org.ccsds.moims.mo.sm.structures.PackageApprovalList;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * A package operation that fails in the backend is reported to the consumer as
 * an Internal error, never as a success, and an update needs some version of
 * the package to be installed.
 */
public class PackageFailureTest {

    private static final String WORKS = "works-1.0.nmfpack";
    private static final String FAILS = "fails-1.0.nmfpack";
    private static final String NOT_INSTALLED = "not-installed-1.0.nmfpack";
    private static final String CHECKSUM = "ab".repeat(32);

    private PackageManagementProviderServiceImpl provider;
    private FailingBackend backend;
    private String previousRequirement;

    /** What the provider sent to the consumer. */
    private final List<String> sent = new ArrayList<>();
    private MOErrorException error;

    @Before
    public void setUp() throws Exception {
        previousRequirement = System.getProperty(PackageManagementProviderServiceImpl.PROPERTY_REQUIRE_CHECKSUM);
        System.clearProperty(PackageManagementProviderServiceImpl.PROPERTY_REQUIRE_CHECKSUM);
        provider = new PackageManagementProviderServiceImpl();
        backend = new FailingBackend();
        Field field = PackageManagementProviderServiceImpl.class.getDeclaredField("backend");
        field.setAccessible(true);
        field.set(provider, backend);
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
    public void aFailedInstallIsReportedAsAnInternalError() throws Exception {
        provider.install(approvals(WORKS, FAILS, WORKS), installInteraction());

        assertInternalErrorNaming(FAILS);
        Assert.assertEquals("The package before the failed one is installed, the one after it is not",
                List.of(WORKS, FAILS), backend.changed);
    }

    @Test
    public void aFailedUninstallIsReportedAsAnInternalError() throws Exception {
        IdentifierList names = new IdentifierList();
        names.add(new Identifier(WORKS));
        names.add(new Identifier(FAILS));
        BooleanList keep = new BooleanList();
        keep.add(false);
        keep.add(false);

        provider.uninstall(names, keep, uninstallInteraction());

        assertInternalErrorNaming(FAILS);
        Assert.assertEquals(List.of(WORKS, FAILS), backend.changed);
    }

    @Test
    public void aFailedUpdateIsReportedAsAnInternalError() throws Exception {
        provider.update(approvals(FAILS), updateInteraction());

        assertInternalErrorNaming(FAILS);
    }

    @Test
    public void aPackageWithNoVersionInstalledCannotBeUpdated() throws Exception {
        try {
            provider.update(approvals(WORKS, NOT_INSTALLED), updateInteraction());
            Assert.fail("An update of a package of which no version is installed must be refused");
        } catch (InvalidArgumentException refused) {
            UIntegerList indexes = (UIntegerList) refused.getExtraInformation();
            Assert.assertEquals(1, indexes.size());
            Assert.assertEquals(new UInteger(1), indexes.get(0));
        }

        Assert.assertTrue("The update is refused before it is acknowledged", sent.isEmpty());
        Assert.assertTrue("No package of the request may be updated", backend.changed.isEmpty());
    }

    @Test
    public void packagesThatCannotBeReadAreReportedAsAnInternalError() throws Exception {
        backend.listFails = true;

        provider.install(approvals(WORKS), installInteraction());
        Assert.assertEquals("Install fails before it is acknowledged", List.of("error"), sent);
        Assert.assertTrue(error instanceof InternalException);

        sent.clear();
        provider.update(approvals(WORKS), updateInteraction());
        Assert.assertEquals("Update fails before it is acknowledged", List.of("error"), sent);
        Assert.assertTrue(error instanceof InternalException);

        Assert.assertTrue(backend.changed.isEmpty());
    }

    private void assertInternalErrorNaming(String packageName) {
        Assert.assertEquals("The operation is acknowledged, then fails instead of succeeding",
                List.of("acknowledgement", "error"), sent);
        Assert.assertTrue("Expected an Internal error, got " + error, error instanceof InternalException);
        String reason = String.valueOf(error.getExtraInformation());
        Assert.assertTrue("The error must name the package: " + reason, reason.contains(packageName));
        Assert.assertTrue("The error must say why: " + reason, reason.contains("The disk is full"));
    }

    private static PackageApprovalList approvals(String... names) {
        PackageApprovalList approvals = new PackageApprovalList();
        for (String name : names) {
            approvals.add(new PackageApproval(new Identifier(name), CHECKSUM, null));
        }
        return approvals;
    }

    private InstallInteraction installInteraction() {
        return new InstallInteraction(null) {
            @Override
            public MALMessage sendAcknowledgement(BooleanList integrities) {
                sent.add("acknowledgement");
                return null;
            }

            @Override
            public MALMessage sendResponse() {
                sent.add("response");
                return null;
            }

            @Override
            public MALMessage sendError(MOErrorException sentError) {
                sent.add("error");
                error = sentError;
                return null;
            }
        };
    }

    private UninstallInteraction uninstallInteraction() {
        return new UninstallInteraction(null) {
            @Override
            public MALMessage sendAcknowledgement() {
                sent.add("acknowledgement");
                return null;
            }

            @Override
            public MALMessage sendResponse() {
                sent.add("response");
                return null;
            }

            @Override
            public MALMessage sendError(MOErrorException sentError) {
                sent.add("error");
                error = sentError;
                return null;
            }
        };
    }

    private UpdateInteraction updateInteraction() {
        return new UpdateInteraction(null) {
            @Override
            public MALMessage sendAcknowledgement() {
                sent.add("acknowledgement");
                return null;
            }

            @Override
            public MALMessage sendResponse() {
                sent.add("response");
                return null;
            }

            @Override
            public MALMessage sendError(MOErrorException sentError) {
                sent.add("error");
                error = sentError;
                return null;
            }
        };
    }

    /**
     * Three valid packages: one whose install, uninstall and update work, one
     * whose do not, and one of which no version is installed.
     */
    private static class FailingBackend implements PMBackend {

        private final List<String> changed = new ArrayList<>();
        private boolean listFails = false;

        @Override
        public StringList getListOfPackages() throws IOException {
            if (listFails) {
                throw new IOException("The packages folder cannot be read");
            }
            StringList list = new StringList();
            list.add(WORKS);
            list.add(FAILS);
            list.add(NOT_INSTALLED);
            return list;
        }

        @Override
        public void install(String packageName, List<String> acknowledged) throws IOException {
            change(packageName);
        }

        @Override
        public void uninstall(String packageName, boolean keepUserData) throws IOException {
            change(packageName);
        }

        @Override
        public void update(String packageName, List<String> acknowledged) throws IOException {
            change(packageName);
        }

        private void change(String packageName) throws IOException {
            changed.add(packageName);
            if (FAILS.equals(packageName)) {
                throw new IOException("The disk is full");
            }
        }

        @Override
        public boolean stopApp(String packageName) {
            return true;
        }

        @Override
        public boolean isPackageInstalled(String packageName) {
            return true;
        }

        @Override
        public boolean isFinalVersionInstalled(String packageName) {
            return false;
        }

        @Override
        public String getPackageVersion(String packageName) {
            return NOT_INSTALLED.equals(packageName) ? null : "1.0";
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
            return CHECKSUM;
        }
    }
}
