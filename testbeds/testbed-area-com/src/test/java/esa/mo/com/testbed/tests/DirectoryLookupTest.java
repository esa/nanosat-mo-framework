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
package esa.mo.com.testbed.tests;

import esa.mo.com.impl.consumer.DirectoryConsumerServiceImpl;
import esa.mo.com.testbed.SetUpCOMServices;
import java.io.IOException;
import java.net.MalformedURLException;
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * End-to-end tests for the {@code addressSchemeFilter} field of the Directory
 * service {@code lookup} operation.
 *
 * <p>One provider is registered in {@link #setUpClass()} with two service
 * addresses: one {@code maltcp://} and one {@code malspp://}. The three tests
 * then exercise the three cases of the filter: NULL (no filtering), a matching
 * scheme, and a non-matching scheme.
 */
public class DirectoryLookupTest {

    private static final String PROVIDER_NAME = "test-scheme-filter-provider";

    private static final SetUpCOMServices harness = new SetUpCOMServices();
    private static DirectoryConsumerServiceImpl directoryConsumer;

    @BeforeClass
    public static void setUpClass() throws IOException, MALException,
            MalformedURLException, MALInteractionException {
        System.setProperty("esa.nmf.archive.persistence.jdbc.url", "jdbc:sqlite::memory:");
        harness.setUp();

        URI dirURI = harness.getCOMServicesProvider().getDirectoryService()
                .getConnection().getConnectionDetails().getProviderURI();
        directoryConsumer = new DirectoryConsumerServiceImpl(dirURI, null, null);

        registerTestProvider();
    }

    @AfterClass
    public static void tearDownClass() {
        if (directoryConsumer != null) {
            directoryConsumer.closeConnection();
        }
        harness.tearDown();
    }

    /**
     * With a NULL {@code addressSchemeFilter}, the provider returns all registered
     * addresses regardless of their URI scheme.
     */
    @Test
    public void testNullSchemeFilterReturnsAllAddresses()
            throws MALInteractionException, MALException {
        System.out.println("Running: testNullSchemeFilterReturnsAllAddresses()");
        ServiceFilter filter = makeFilter(null);
        ProviderList results = directoryConsumer.getDirectoryStub().lookup(filter);

        Assert.assertEquals("Lookup must return exactly one provider", 1, results.size());
        int addressCount = countAddresses(results.get(0));
        System.out.println("Number of addresses returned: " + addressCount);
        Assert.assertEquals("NULL schemeFilter must return all 2 addresses", 2, addressCount);
        System.out.flush();
    }

    /**
     * With {@code addressSchemeFilter = ["malspp"]}, only addresses whose URI
     * starts with {@code "malspp"} are included; the {@code maltcp} address is
     * excluded.
     */
    @Test
    public void testSchemeFilterReturnsMalSppAddressOnly()
            throws MALInteractionException, MALException {
        System.out.println("Running: testSchemeFilterReturnsMalSppAddressOnly()");
        IdentifierList schemeFilter = new IdentifierList();
        schemeFilter.add(new Identifier("malspp"));

        ServiceFilter filter = makeFilter(schemeFilter);
        ProviderList results = directoryConsumer.getDirectoryStub().lookup(filter);

        Assert.assertEquals("Lookup must return exactly one provider", 1, results.size());
        int addressCount = countAddresses(results.get(0));
        System.out.println("Number of addresses returned: " + addressCount);
        Assert.assertEquals("malspp schemeFilter must return exactly 1 address", 1, addressCount);

        URI returnedURI = getFirstAddress(results.get(0));
        Assert.assertNotNull("Returned address URI must not be null", returnedURI);
        System.out.println("The returned address URI is: " + returnedURI);
        Assert.assertTrue("Returned address URI must start with 'malspp'",
                returnedURI.getValue().startsWith("malspp"));
        System.out.flush();
    }

    /**
     * With a scheme that matches none of the registered addresses, the provider
     * is still returned (it satisfied the other filter fields) but all its
     * service address lists are empty.
     */
    @Test
    public void testNonMatchingSchemeFilterReturnsNoAddresses()
            throws MALInteractionException, MALException {
        System.out.println("Running: testNonMatchingSchemeFilterReturnsNoAddresses()");
        IdentifierList schemeFilter = new IdentifierList();
        schemeFilter.add(new Identifier("xyz-transport"));

        ServiceFilter filter = makeFilter(schemeFilter);
        ProviderList results = directoryConsumer.getDirectoryStub().lookup(filter);

        Assert.assertEquals("Provider must still appear even when no addresses match",
                1, results.size());
        int addressCount = countAddresses(results.get(0));
        System.out.println("Number of addresses returned: " + addressCount);
        Assert.assertEquals("Non-matching schemeFilter must yield 0 addresses", 0, addressCount);
        System.out.flush();
    }

    // --- Helpers ---

    private static void registerTestProvider() throws MALInteractionException, MALException {
        AddressDetailsList addresses = new AddressDetailsList();
        addresses.add(makeAddress("maltcp://test-host:1024/test-service"));
        addresses.add(makeAddress("malspp://test-host:2048/test-service"));

        ServiceId serviceId = new ServiceId(new UShort(0), new UShort(0), new UOctet((short) 0));
        ServiceCapabilityList capabilities = new ServiceCapabilityList();
        capabilities.add(new ServiceCapability(serviceId, new NamedValueList(), addresses));

        IdentifierList domain = new IdentifierList();
        domain.add(new Identifier("test"));

        Provider pub = new Provider(
                null,
                new Identifier(PROVIDER_NAME),
                domain,
                capabilities,
                new AddressDetailsList(),
                null);

        harness.getCOMServicesProvider().getDirectoryService().add(pub, null);
    }

    private static AddressDetails makeAddress(String uri) {
        return new AddressDetails(new URI(uri), null);
    }

    private static ServiceFilter makeFilter(IdentifierList schemeFilter) {
        IdentifierList wildcardDomain = new IdentifierList();
        wildcardDomain.add(new Identifier("*"));
        return new ServiceFilter(
                new Identifier(PROVIDER_NAME),
                wildcardDomain,
                new ServiceId(new UShort(0), new UShort(0), new UOctet((short) 0)),
                schemeFilter);
    }

    private static int countAddresses(Provider provider) {
        int total = 0;
        for (ServiceCapability cap : provider.getServiceCapabilities()) {
            total += cap.getServiceAddresses().size();
        }
        return total;
    }

    private static URI getFirstAddress(Provider provider) {
        for (ServiceCapability cap : provider.getServiceCapabilities()) {
            if (!cap.getServiceAddresses().isEmpty()) {
                return cap.getServiceAddresses().get(0).getServiceURI();
            }
        }
        return null;
    }
}
