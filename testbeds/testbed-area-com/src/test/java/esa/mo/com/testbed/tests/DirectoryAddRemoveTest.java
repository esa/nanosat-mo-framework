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
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.InvalidArgumentException;
import org.ccsds.moims.mo.com.directory.consumer.DirectoryStub;
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.mal.structures.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for the add and remove operations of the Directory service: the
 * details that they refuse, and the replacement of an entry by a new one of
 * the same provider.
 */
public class DirectoryAddRemoveTest {

    private static final Logger LOGGER = Logger.getLogger(DirectoryAddRemoveTest.class.getName());

    private static final SetUpCOMServices harness = new SetUpCOMServices();
    private static DirectoryConsumerServiceImpl directoryConsumer;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.setProperty("esa.nmf.archive.persistence.jdbc.url", "jdbc:sqlite::memory:");
        harness.setUp();

        URI dirURI = harness.getCOMServicesProvider().getDirectoryService()
                .getConnection().getConnectionDetails().getProviderURI();
        directoryConsumer = new DirectoryConsumerServiceImpl(dirURI, null, null);
    }

    @AfterClass
    public static void tearDownClass() {
        if (directoryConsumer != null) {
            directoryConsumer.closeConnection();
        }
        harness.tearDown();
    }

    // Test — add refuses a provider name with the wildcard

    @Test
    public void testAddRefusesWildcardName() throws Exception {
        LOGGER.info("Running: testAddRefusesWildcardName()");
        assertAddRefused(provider("*", "test", 1, "maltcp://host:1/s"));
    }

    // Test — add refuses an empty domain

    @Test
    public void testAddRefusesEmptyDomain() throws Exception {
        LOGGER.info("Running: testAddRefusesEmptyDomain()");
        Provider details = provider("empty-domain", "test", 1, "maltcp://host:1/s");
        details.getDomain().clear();
        assertAddRefused(details);
    }

    // Test — add refuses a ServiceId with a '0'

    @Test
    public void testAddRefusesZeroServiceId() throws Exception {
        LOGGER.info("Running: testAddRefusesZeroServiceId()");
        assertAddRefused(provider("zero-service", "test", 0, "maltcp://host:1/s"));
    }

    // Test — remove refuses the id '0'

    @Test
    public void testRemoveRefusesZeroId() throws Exception {
        LOGGER.info("Running: testRemoveRefusesZeroId()");
        try {
            stub().remove(0L);
            Assert.fail("remove must refuse the id '0'");
        } catch (InvalidArgumentException expected) {
            // Refused
        }
    }

    // Test — a second add of the same provider replaces its entry

    @Test
    public void testAddReplacesTheEntryOfTheSameProvider() throws Exception {
        LOGGER.info("Running: testAddReplacesTheEntryOfTheSameProvider()");
        stub().add(provider("replaced", "test", 1, "maltcp://old:1/s"));
        Long newId = stub().add(provider("replaced", "test", 1, "maltcp://new:1/s"));

        ProviderList found = stub().lookup(filter("replaced"));
        Assert.assertEquals("The provider must have one entry", 1, found.size());
        Assert.assertEquals("The entry must be the new one", newId, found.get(0).getId());
        Assert.assertEquals("The entry must have the new address", "maltcp://new:1/s",
                found.get(0).getServiceCapabilities().get(0).getServiceAddresses().get(0).getServiceURI().getValue());
    }

    private static DirectoryStub stub() {
        return directoryConsumer.getDirectoryStub();
    }

    private static void assertAddRefused(Provider details) throws Exception {
        try {
            stub().add(details);
            Assert.fail("add must refuse " + details.getProviderName());
        } catch (InvalidArgumentException expected) {
            // Refused
        }
        Assert.assertTrue("A refused provider must not be listed",
                stub().lookup(filter("*")).stream()
                        .noneMatch(p -> p.getProviderName().equals(details.getProviderName())));
    }

    private static Provider provider(String name, String domainPart, int serviceNumber, String uri) {
        AddressDetailsList addresses = new AddressDetailsList();
        addresses.add(new AddressDetails(new URI(uri), null));
        ServiceCapabilityList capabilities = new ServiceCapabilityList();
        capabilities.add(new ServiceCapability(new ServiceId(new UShort(1), new UShort(serviceNumber),
                new UOctet((short) 1)), new NamedValueList(), addresses));
        IdentifierList domain = new IdentifierList();
        domain.add(new Identifier(domainPart));
        return new Provider(null, new Identifier(name), domain, capabilities, new AddressDetailsList(), null);
    }

    private static ServiceFilter filter(String name) {
        IdentifierList wildcardDomain = new IdentifierList();
        wildcardDomain.add(new Identifier("*"));
        return new ServiceFilter(name.equals("*") ? null : new Identifier(name), wildcardDomain,
                new ServiceId(new UShort(0), new UShort(0), new UOctet((short) 0)), null);
    }
}
