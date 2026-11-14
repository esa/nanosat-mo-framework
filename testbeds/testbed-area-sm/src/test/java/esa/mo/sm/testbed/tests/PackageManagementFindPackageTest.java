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
package esa.mo.sm.testbed.tests;

import esa.mo.nmf.nmfpackage.metadata.MetadataApp;
import esa.mo.nmf.nmfpackage.metadata.MetadataDependency;
import esa.mo.sm.testbed.SetUpSMServices;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.sm.packagemanagement.body.FindPackageResponse;
import org.ccsds.moims.mo.sm.packagemanagement.consumer.PackageManagementStub;
import org.ccsds.moims.mo.sm.structures.PackageContent;
import org.ccsds.moims.mo.sm.structures.PackageType;
import org.ccsds.moims.mo.sm.structures.PackageTypeList;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * End-to-end tests for the {@code types} filter and the {@code dependencies}
 * field of the Package Management {@code findPackage} operation, exercised over
 * a real MAL round-trip.
 *
 * <p>The provider is seeded with three package files: an App with two
 * dependencies, an App with none, and a shared dependency. The tests then check
 * that the {@link PackageType} enumeration and the dependency list survive the
 * encode and decode across the transport.
 */
public class PackageManagementFindPackageTest {

    private static final Logger LOGGER = Logger.getLogger(PackageManagementFindPackageTest.class.getName());

    private static final SetUpSMServices harness = new SetUpSMServices();
    private static PackageManagementStub stub;

    @BeforeClass
    public static void setUpClass() throws Exception {
        harness.setUp();

        ArrayList<String> dependencies = new ArrayList<>();
        dependencies.add("orekit-11.3.2.jar");
        dependencies.add("hipparchus-core-2.3.jar");
        harness.writePackage("cas-6.0.nmfpack", new MetadataApp("cas", "6.0",
                "esa.mo.nmf.apps.Cas", "cas-6.0.jar", null, null, dependencies, "6.0"));
        harness.writePackage("hello-world-5.1.nmfpack", new MetadataApp("hello-world", "5.1",
                "esa.mo.nmf.apps.Hello", "hello-world-5.1.jar", null, null, new ArrayList<>(), "6.0"));
        harness.writePackage("orekit-11.3.2.nmfpack", new MetadataDependency("orekit", "11.3.2"));

        stub = harness.getPackageManagementConsumer().getPackageManagementStub();
    }

    @AfterClass
    public static void tearDownClass() {
        harness.tearDown();
    }

    /**
     * With a NULL types filter, the provider returns every package regardless
     * of type: the two Apps and the dependency.
     */
    @Test
    public void testNullTypeFilterReturnsEveryPackage() throws MOErrorException, MALException {
        LOGGER.info("Running: testNullTypeFilterReturnsEveryPackage()");
        FindPackageResponse response = stub.findPackage(allNames(), null);

        Assert.assertEquals("NULL types filter must return all three packages",
                3, response.getContents().size());
    }

    /**
     * With {@code types = [APP]}, only the two App packages are returned; the
     * dependency package is excluded.
     */
    @Test
    public void testAppTypeFilterReturnsOnlyApps() throws MOErrorException, MALException {
        LOGGER.info("Running: testAppTypeFilterReturnsOnlyApps()");
        PackageTypeList types = new PackageTypeList();
        types.add(PackageType.APP);
        FindPackageResponse response = stub.findPackage(allNames(), types);

        Assert.assertEquals("APP filter must return exactly the two Apps",
                2, response.getContents().size());
        for (PackageContent content : response.getContents()) {
            Assert.assertEquals("Every returned package must be of type APP",
                    PackageType.APP, content.getType());
        }
    }

    /**
     * With {@code types = [DEPENDENCY]}, only the dependency package is
     * returned; the two Apps are excluded.
     */
    @Test
    public void testDependencyTypeFilterReturnsOnlyDependencies() throws MOErrorException, MALException {
        LOGGER.info("Running: testDependencyTypeFilterReturnsOnlyDependencies()");
        PackageTypeList types = new PackageTypeList();
        types.add(PackageType.DEPENDENCY);
        FindPackageResponse response = stub.findPackage(allNames(), types);

        Assert.assertEquals("DEPENDENCY filter must return exactly one package",
                1, response.getContents().size());
        Assert.assertEquals("orekit", response.getContents().get(0).getName().getValue());
        Assert.assertEquals(PackageType.DEPENDENCY, response.getContents().get(0).getType());
    }

    /**
     * An App package reports its dependency JAR names, and the list survives the
     * MAL round-trip unchanged and in order.
     */
    @Test
    public void testAppDependenciesSurviveTheRoundTrip() throws MOErrorException, MALException {
        LOGGER.info("Running: testAppDependenciesSurviveTheRoundTrip()");
        PackageContent cas = contentOf("cas");

        StringList expected = new StringList();
        expected.add("orekit-11.3.2.jar");
        expected.add("hipparchus-core-2.3.jar");
        Assert.assertEquals(expected, cas.getDependencies());
    }

    /**
     * An App with no dependencies, and a dependency package, both report a NULL
     * dependencies field.
     */
    @Test
    public void testPackagesWithoutDependenciesReportNull() throws MOErrorException, MALException {
        LOGGER.info("Running: testPackagesWithoutDependenciesReportNull()");
        Assert.assertNull("An App with no dependencies must report NULL dependencies",
                contentOf("hello-world").getDependencies());
        Assert.assertNull("A dependency package must report NULL dependencies",
                contentOf("orekit").getDependencies());
    }

    // --- Helpers ---

    private static IdentifierList allNames() {
        IdentifierList names = new IdentifierList();
        names.add(new Identifier("*"));
        return names;
    }

    private static PackageContent contentOf(String packageName) throws MOErrorException, MALException {
        FindPackageResponse response = stub.findPackage(allNames(), null);
        for (PackageContent content : response.getContents()) {
            if (content.getName() != null && packageName.equals(content.getName().getValue())) {
                return content;
            }
        }
        throw new AssertionError("No package with name: " + packageName);
    }
}
