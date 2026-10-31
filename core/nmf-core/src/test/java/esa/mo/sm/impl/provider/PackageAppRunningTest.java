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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.sm.AppRunningException;
import org.ccsds.moims.mo.sm.packagemanagement.provider.UninstallInteraction;
import org.ccsds.moims.mo.sm.packagemanagement.provider.UpdateInteraction;
import org.ccsds.moims.mo.sm.structures.PackageApproval;
import org.ccsds.moims.mo.sm.structures.PackageApprovalList;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Holds that uninstall and update answer with the App Running error when the
 * App of a requested package is still running after it was asked to stop,
 * naming that package, and that no package of the request is changed then.
 */
public class PackageAppRunningTest {

    private static final String STOPS = "stops-1.0.nmfpack";
    private static final String KEEPS_RUNNING = "keeps-running-1.0.nmfpack";
    private static final String CHECKSUM = "ab".repeat(32);

    private PackageManagementProviderServiceImpl provider;
    private TwoPackageBackend backend;
    private String previousRequirement;

    @Before
    public void setUp() throws Exception {
        previousRequirement = System.getProperty(PackageManagementProviderServiceImpl.PROPERTY_REQUIRE_CHECKSUM);
        System.clearProperty(PackageManagementProviderServiceImpl.PROPERTY_REQUIRE_CHECKSUM);
        provider = new PackageManagementProviderServiceImpl();
        backend = new TwoPackageBackend();
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
    public void uninstallNamesThePackageWhoseAppIsStillRunning() throws Exception {
        IdentifierList names = new IdentifierList();
        names.add(new Identifier(STOPS));
        names.add(new Identifier(KEEPS_RUNNING));
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
            Assert.fail("An uninstall with an App that is still running must be refused");
        } catch (AppRunningException refused) {
            assertIndexes(refused, 1);
        }

        Assert.assertTrue("Both Apps are asked to stop", backend.stopped.containsAll(List.of(STOPS, KEEPS_RUNNING)));
        Assert.assertTrue("No package of the request may be uninstalled", backend.changed.isEmpty());
    }

    @Test
    public void updateNamesThePackageWhoseAppIsStillRunning() throws Exception {
        PackageApprovalList approvals = new PackageApprovalList();
        approvals.add(new PackageApproval(new Identifier(KEEPS_RUNNING), CHECKSUM, null));
        approvals.add(new PackageApproval(new Identifier(STOPS), CHECKSUM, null));

        try {
            provider.update(approvals, new UpdateInteraction(null) {
                @Override
                public MALMessage sendAcknowledgement() {
                    return null;
                }
            });
            Assert.fail("An update with an App that is still running must be refused");
        } catch (AppRunningException refused) {
            assertIndexes(refused, 0);
        }

        Assert.assertTrue("No package of the request may be updated", backend.changed.isEmpty());
    }

    private static void assertIndexes(AppRunningException refused, int... expected) {
        UIntegerList indexes = (UIntegerList) refused.getExtraInformation();
        Assert.assertEquals(expected.length, indexes.size());
        for (int i = 0; i < expected.length; i++) {
            Assert.assertEquals(new UInteger(expected[i]), indexes.get(i));
        }
    }

    /** Two installed packages, one whose App stops when asked and one whose App keeps running. */
    private static class TwoPackageBackend implements PMBackend {

        private final List<String> stopped = new ArrayList<>();
        private final List<String> changed = new ArrayList<>();

        @Override
        public StringList getListOfPackages() {
            StringList list = new StringList();
            list.add(STOPS);
            list.add(KEEPS_RUNNING);
            return list;
        }

        @Override
        public void install(String packageName, List<String> acknowledged) {
            throw new AssertionError("Nothing may be installed in this test");
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
            stopped.add(packageName);
            return !KEEPS_RUNNING.equals(packageName);
        }

        @Override
        public boolean isPackageInstalled(String packageName) {
            return true;
        }

        @Override
        public boolean replacesInstalledPackage(String packageName) {
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
            return CHECKSUM;
        }
    }
}
