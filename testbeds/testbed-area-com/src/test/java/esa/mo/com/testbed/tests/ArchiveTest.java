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

import esa.mo.com.impl.util.HelperArchive;
import esa.mo.com.testbed.SetUpCOMServices;
import java.io.IOException;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveStub;
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.structures.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * End-to-end test for the COM Archive store/retrieve operations.
 */
public class ArchiveTest {

    private static final SetUpCOMServices harness = new SetUpCOMServices();

    private static final ObjectType TEST_OBJECT_TYPE = new ObjectType(
            new UShort(200), new UShort(1), new UOctet((short) 1), new UShort(1));

    @BeforeClass
    public static void setUpClass() throws IOException {
        System.setProperty("esa.nmf.archive.persistence.jdbc.url", "jdbc:sqlite::memory:");
        harness.setUp();
    }

    @AfterClass
    public static void tearDownClass() {
        harness.tearDown();
    }

    @Test
    public void testStoreAndRetrieve() throws MALInteractionException, MALException {
        System.out.println("Running: testStoreAndRetrieve()");
        ArchiveStub stub = harness.getArchiveConsumer().getArchiveStub();
        IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        URI providerURI = harness.getCOMServicesProvider()
                .getArchiveService().getConnection().getConnectionDetails().getProviderURI();

        ObjectKeysList storedBody = new ObjectKeysList();
        storedBody.add(new ObjectKeys(TEST_OBJECT_TYPE, domain, new LongList()));
        HeterogeneousList bodies = new HeterogeneousList();
        bodies.add(storedBody);

        ArchiveDetailsList details = HelperArchive.generateArchiveDetailsList(null, null, providerURI);
        LongList ids = stub.store(true, TEST_OBJECT_TYPE, domain, details, bodies);

        Assert.assertNotNull("store must return a non-null ID list", ids);
        Assert.assertEquals("One ID must be returned", 1, ids.size());
        Long instId = ids.get(0);
        Assert.assertNotNull("Returned instance ID must not be null", instId);
        System.out.println("The returned instance ID is: " + instId);

        ObjectKeysList retrieved = (ObjectKeysList) HelperArchive.getObjectBodyFromArchive(
                stub, TEST_OBJECT_TYPE, domain, instId);

        Assert.assertNotNull("Retrieved object must not be null", retrieved);
        Assert.assertEquals("ObjectKeysList must contain the same number of entries",
                storedBody.size(), retrieved.size());
        Assert.assertEquals("ObjectType in entry must match",
                storedBody.get(0).getObjType(),
                retrieved.get(0).getObjType());
        System.out.flush();
    }

}
