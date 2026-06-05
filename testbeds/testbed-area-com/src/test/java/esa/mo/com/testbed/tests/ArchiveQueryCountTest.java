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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveStub;
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * End-to-end tests for the COM Archive {@code query} and {@code count}
 * operations.
 *
 * <p>
 * Each test class gets its own in-memory SQLite archive (set via the
 * {@code esa.nmf.archive.persistence.jdbc.url} system property before the
 * provider starts), so tests are fully isolated from the on-disk archive and
 * from each other test class.
 *
 * <p>
 * Each object type constant uses a unique area number so that objects stored by
 * one test method cannot accidentally appear in another method's query results.
 */
public class ArchiveQueryCountTest {

    private static final SetUpCOMServices harness = new SetUpCOMServices();

    // Unique area numbers per test method to avoid cross-test contamination within the shared
    // in-memory DB that lives for the lifetime of this test class.
    private static final ObjectType QUERY_BASIC_TYPE = new ObjectType(
            new UShort(201), new UShort(1), new UOctet((short) 1), new UShort(1));
    private static final ObjectType QUERY_BODY_TYPE = new ObjectType(
            new UShort(202), new UShort(1), new UOctet((short) 1), new UShort(1));
    private static final ObjectType COUNT_TYPE = new ObjectType(
            new UShort(203), new UShort(1), new UOctet((short) 1), new UShort(1));
    // Never stored anywhere — used to verify that query/count return empty results on no match.
    private static final ObjectType UNMATCH_TYPE = new ObjectType(
            new UShort(204), new UShort(1), new UOctet((short) 1), new UShort(1));
    // Used by sort-field error tests; objects must be present so the provider actually attempts
    // sorting — sorting an empty result set is a no-op and would not raise INVALID.
    private static final ObjectType SORT_ERROR_TYPE = new ObjectType(
            new UShort(205), new UShort(1), new UOctet((short) 1), new UShort(1));
    // Used by CompositeFilter validation error tests; objects must be present so filterQuery is
    // actually invoked — an empty result set skips filtering entirely.
    private static final ObjectType FILTER_ERROR_TYPE = new ObjectType(
            new UShort(206), new UShort(1), new UOctet((short) 1), new UShort(1));

    @BeforeClass
    public static void setUpClass() throws IOException {
        // Force the archive to use an in-memory SQLite DB so no file is created or left behind.
        System.setProperty("esa.nmf.archive.persistence.jdbc.url", "jdbc:sqlite::memory:");
        harness.setUp();
    }

    @AfterClass
    public static void tearDownClass() {
        harness.tearDown();
    }

    /**
     * Stores two objects of the same type and verifies that a wildcard query
     * returns exactly those two objects via UPDATE messages before the final
     * empty RESPONSE.
     */
    @Test
    public void testQueryReturnsStoredObjects() throws MALInteractionException, MALException, InterruptedException {
        System.out.println("Running: testQueryReturnsStoredObjects()");
        ArchiveStub stub = harness.getArchiveConsumer().getArchiveStub();
        IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        URI providerURI = harness.getCOMServicesProvider()
                .getArchiveService().getConnection().getConnectionDetails().getProviderURI();

        storeEmpty(stub, QUERY_BASIC_TYPE, domain, providerURI);
        storeEmpty(stub, QUERY_BASIC_TYPE, domain, providerURI);

        QueryResult result = runQuery(stub, QUERY_BASIC_TYPE, false);

        int objectCount = countObjects(result);
        System.out.println("Number of objects returned by query: " + objectCount);
        Assert.assertEquals("Query must return exactly 2 objects", 2, objectCount);
        System.out.flush();
    }

    /**
     * Verifies that setting {@code returnBody=true} causes the provider to
     * include object bodies in the UPDATE messages. The body content is not
     * checked here — only its presence.
     */
    @Test
    public void testQueryReturnBody() throws MALInteractionException, MALException, InterruptedException {
        System.out.println("Running: testQueryReturnBody()");
        ArchiveStub stub = harness.getArchiveConsumer().getArchiveStub();
        IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        URI providerURI = harness.getCOMServicesProvider()
                .getArchiveService().getConnection().getConnectionDetails().getProviderURI();

        ObjectKeysList keysList = new ObjectKeysList();
        keysList.add(new ObjectKeys(QUERY_BODY_TYPE, domain, new LongList()));
        HeterogeneousList bodies = new HeterogeneousList();
        bodies.add(new ConfigurationSet(keysList));
        stub.store(true, QUERY_BODY_TYPE, domain,
                HelperArchive.generateArchiveDetailsList(null, null, providerURI),
                bodies);

        QueryResult result = runQuery(stub, QUERY_BODY_TYPE, true);

        // The provider may batch objects into one or more UPDATE messages; check all of them.
        boolean bodyFound = false;
        for (HeterogeneousList updateBodies : result.objBodies) {
            if (updateBodies != null && !updateBodies.isEmpty()) {
                bodyFound = true;
                break;
            }
        }
        System.out.println("Body found in query results: " + bodyFound);
        Assert.assertTrue("Query with returnBody=true must include at least one body", bodyFound);
        System.out.flush();
    }

    /**
     * Verifies that querying for an object type that was never stored produces
     * an empty RESPONSE with no preceding UPDATE messages.
     */
    @Test
    public void testQueryNoMatch() throws MALInteractionException, MALException, InterruptedException {
        System.out.println("Running: testQueryNoMatch()");
        ArchiveStub stub = harness.getArchiveConsumer().getArchiveStub();

        QueryResult result = runQuery(stub, UNMATCH_TYPE, false);

        int objectCount = countObjects(result);
        System.out.println("Number of objects returned for non-matching query: " + objectCount);
        Assert.assertEquals("Query for a never-stored type must return 0 objects", 0, objectCount);
        System.out.flush();
    }

    /**
     * Stores three objects and verifies that {@code count} returns exactly
     * three.
     */
    @Test
    public void testCountMatchingObjects() throws MALInteractionException, MALException, InterruptedException {
        System.out.println("Running: testCountMatchingObjects()");
        ArchiveStub stub = harness.getArchiveConsumer().getArchiveStub();
        IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        URI providerURI = harness.getCOMServicesProvider()
                .getArchiveService().getConnection().getConnectionDetails().getProviderURI();

        storeEmpty(stub, COUNT_TYPE, domain, providerURI);
        storeEmpty(stub, COUNT_TYPE, domain, providerURI);
        storeEmpty(stub, COUNT_TYPE, domain, providerURI);

        long count = runCount(stub, COUNT_TYPE);

        System.out.println("Number of objects counted: " + count);
        Assert.assertEquals("Count must equal the number of stored objects", 3L, count);
        System.out.flush();
    }

    /**
     * Verifies that {@code count} returns zero for an object type that was
     * never stored.
     */
    @Test
    public void testCountNoMatch() throws MALInteractionException, MALException, InterruptedException {
        System.out.println("Running: testCountNoMatch()");
        ArchiveStub stub = harness.getArchiveConsumer().getArchiveStub();

        long count = runCount(stub, UNMATCH_TYPE);

        System.out.println("Count for non-matching type: " + count);
        Assert.assertEquals("Count for a never-stored type must be 0", 0L, count);
        System.out.flush();
    }

    // --- Error / non-nominal tests ---
    /**
     * Verifies that {@code query} raises INVALID when a {@link CompositeFilter}
     * fails validation inside the provider. Using
     * {@link ExpressionOperator#GREATER} with a {@code null} field value is
     * rejected by {@code isCompositeFilterValid}, which causes the provider to
     * throw an INVALID error per the COM Archive spec.
     *
     * <p>
     * At least one object of {@code FILTER_ERROR_TYPE} must be stored so that
     * the filter loop is actually entered — an empty result set bypasses filter
     * validation entirely.
     */
    @Test
    public void testQueryInvalidOnBadFilter()
            throws MALInteractionException, MALException, InterruptedException {
        System.out.println("Running: testQueryInvalidOnBadFilter()");
        ArchiveStub stub = harness.getArchiveConsumer().getArchiveStub();
        IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        URI providerURI = harness.getCOMServicesProvider()
                .getArchiveService().getConnection().getConnectionDetails().getProviderURI();

        storeEmpty(stub, FILTER_ERROR_TYPE, domain, providerURI);

        CompositeFilterList filters = new CompositeFilterList();
        filters.add(new CompositeFilter("anyField", ExpressionOperator.GREATER, null));
        CompositeFilterSet filterSet = new CompositeFilterSet(filters);

        MOErrorException error = runQueryExpectError(
                stub, FILTER_ERROR_TYPE, new ArchiveQuery(0L), filterSet);

        Assert.assertNotNull(
                "Provider must return an error for an invalid CompositeFilter", error);
        System.out.println("Error number returned: " + error.getErrorNumber());
        Assert.assertEquals(
                "Error must be INVALID", COMHelper.INVALID_ARGUMENT_ERROR_NUMBER,
                error.getErrorNumber());
        System.out.flush();
    }

    /**
     * Verifies that {@code query} raises INVALID (req 11) when
     * {@code sortFieldName} does not reference a field that exists on the
     * stored object type.
     *
     * <p>
     * Objects must be present in the archive before the query: the provider
     * only invokes the sort logic when the result set is non-empty, so querying
     * an empty archive would silently skip sorting and return a normal RESPONSE
     * instead of an error.
     */
    @Test
    public void testQueryInvalidOnUnknownSortField()
            throws MALInteractionException, MALException, InterruptedException {
        System.out.println("Running: testQueryInvalidOnUnknownSortField()");
        ArchiveStub stub = harness.getArchiveConsumer().getArchiveStub();
        IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        URI providerURI = harness.getCOMServicesProvider()
                .getArchiveService().getConnection().getConnectionDetails().getProviderURI();

        storeEmpty(stub, SORT_ERROR_TYPE, domain, providerURI);

        // sortOrder=true activates sorting; "nonExistentField" will not resolve to any field.
        ArchiveQuery archiveQuery = new ArchiveQuery(null, null,
                0L, null, null, null, Boolean.TRUE, "nonExistentField");

        MOErrorException error = runQueryExpectError(stub, SORT_ERROR_TYPE, archiveQuery, null);

        Assert.assertNotNull("Provider must return an error for an unknown sort field", error);
        System.out.println("Error number returned: " + error.getErrorNumber());
        Assert.assertEquals("Error must be INVALID",
                COMHelper.INVALID_ARGUMENT_ERROR_NUMBER, error.getErrorNumber());
        System.out.flush();
    }

    /**
     * Verifies that {@code count} raises INVALID when a {@link CompositeFilter}
     * fails validation. Same mechanism as {@link #testQueryInvalidOnBadFilter}
     * but exercised via the count path.
     */
    @Test
    public void testCountInvalidOnBadFilter()
            throws MALInteractionException, MALException, InterruptedException {
        System.out.println("Running: testCountInvalidOnBadFilter()");
        ArchiveStub stub = harness.getArchiveConsumer().getArchiveStub();
        IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        URI providerURI = harness.getCOMServicesProvider()
                .getArchiveService().getConnection().getConnectionDetails().getProviderURI();

        storeEmpty(stub, FILTER_ERROR_TYPE, domain, providerURI);

        CompositeFilterList filters = new CompositeFilterList();
        filters.add(new CompositeFilter("anyField", ExpressionOperator.GREATER, null));
        CompositeFilterSet filterSet = new CompositeFilterSet(filters);

        MOErrorException error = runCountExpectError(
                stub, FILTER_ERROR_TYPE, new ArchiveQuery(0L), filterSet);

        Assert.assertNotNull("Provider must return an error for an invalid CompositeFilter", error);
        System.out.println("Error number returned: " + error.getErrorNumber());
        Assert.assertEquals("Error must be INVALID",
                COMHelper.INVALID_ARGUMENT_ERROR_NUMBER, error.getErrorNumber());
        System.out.flush();
    }

    /**
     * Verifies that {@code count} raises INVALID (req 1 of count, which mirrors
     * query req 11) when {@code sortFieldName} does not reference a defined
     * field.
     *
     * <p>
     * Same caveat as {@link #testQueryInvalidOnUnknownSortField}: at least one
     * object of the queried type must exist so that sorting is actually
     * attempted.
     */
    @Test
    public void testCountInvalidOnUnknownSortField()
            throws MALInteractionException, MALException, InterruptedException {
        System.out.println("Running: testCountInvalidOnUnknownSortField()");
        ArchiveStub stub = harness.getArchiveConsumer().getArchiveStub();
        IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        URI providerURI = harness.getCOMServicesProvider()
                .getArchiveService().getConnection().getConnectionDetails().getProviderURI();

        storeEmpty(stub, SORT_ERROR_TYPE, domain, providerURI);

        ArchiveQuery archiveQuery = new ArchiveQuery(null, null,
                0L, null, null, null, Boolean.TRUE, "nonExistentField");

        MOErrorException error = runCountExpectError(stub, SORT_ERROR_TYPE, archiveQuery, null);

        Assert.assertNotNull("Provider must return an error for an unknown sort field", error);
        System.out.println("Error number returned: " + error.getErrorNumber());
        Assert.assertEquals("Error must be INVALID",
                COMHelper.INVALID_ARGUMENT_ERROR_NUMBER, error.getErrorNumber());
        System.out.flush();
    }

    // --- Helpers ---
    /**
     * Stores one object of the given type with an empty body, using a
     * provider-assigned instance ID. Intended as a lightweight fixture helper —
     * the body content is irrelevant to query/count tests.
     */
    private static void storeEmpty(ArchiveStub stub, ObjectType type,
            IdentifierList domain, URI providerURI) throws MALInteractionException, MALException {
        HeterogeneousList bodies = new HeterogeneousList();
        bodies.add(new ConfigurationSet(new ObjectKeysList()));
        stub.store(true, type, domain,
                HelperArchive.generateArchiveDetailsList(null, null, providerURI),
                bodies);
    }

    /**
     * Runs an async archive query for all objects of the given type and waits
     * for completion.
     *
     * <p>
     * Uses an {@link ArchiveQuery} with {@code related=0} (wildcard) and no
     * filter, which matches all stored objects of {@code type} regardless of
     * domain or other fields.
     *
     * @param returnBody whether the provider should include object bodies in
     * UPDATE messages
     * @return the accumulated results from all UPDATE messages received before
     * the final RESPONSE
     * @throws AssertionError if the interaction does not complete within 5
     * seconds
     */
    private static QueryResult runQuery(ArchiveStub stub, ObjectType type, boolean returnBody)
            throws MALInteractionException, MALException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        QueryResult result = new QueryResult();
        // related=0 is the wildcard value; all other fields are null (no constraint).
        ArchiveQuery archiveQuery = new ArchiveQuery(0L);
        stub.asyncQuery(returnBody, type, archiveQuery, (QueryFilter) null, new QueryAdapter(latch, result));
        Assert.assertTrue("query interaction timed out after 5s", latch.await(5, TimeUnit.SECONDS));
        if (result.error != null) {
            throw result.error;
        }
        return result;
    }

    /**
     * Runs an async archive count for all objects of the given type and waits
     * for completion.
     *
     * <p>
     * Uses the same wildcard {@link ArchiveQuery} as {@link #runQuery}.
     *
     * @return the count returned by the provider, or -1 if the response was
     * unexpectedly null
     * @throws AssertionError if the interaction does not complete within 5
     * seconds
     */
    private static long runCount(ArchiveStub stub, ObjectType type)
            throws MALInteractionException, MALException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        CountAdapter adapter = new CountAdapter(latch);
        ArchiveQuery archiveQuery = new ArchiveQuery(0L);
        stub.asyncCount(type, archiveQuery, (QueryFilter) null, adapter);
        Assert.assertTrue("count interaction timed out after 5s", latch.await(5, TimeUnit.SECONDS));
        if (adapter.error != null) {
            throw adapter.error;
        }
        return adapter.get();
    }

    /**
     * Returns the total number of archive objects across all UPDATE messages in
     * {@code result}. A single query may produce multiple UPDATE messages (one
     * per domain/type pair), so all lists must be summed.
     */
    private static int countObjects(QueryResult result) {
        int total = 0;
        for (ArchiveDetailsList details : result.objDetails) {
            if (details != null) {
                total += details.size();
            }
        }
        return total;
    }

    /**
     * Accumulates the {@code objDetails} and {@code objBodies} payloads
     * received across all query UPDATE messages, and captures any error so the
     * calling thread can rethrow it.
     */
    private static class QueryResult {

        /**
         * One entry per UPDATE; each list covers one domain/type batch.
         */
        final List<ArchiveDetailsList> objDetails = new ArrayList<>();
        final List<HeterogeneousList> objBodies = new ArrayList<>();
        MALException error;
    }

    /**
     * {@link ArchiveAdapter} that collects query UPDATE payloads into a
     * {@link QueryResult} and releases a {@link CountDownLatch} when the final
     * RESPONSE (or an error) arrives.
     */
    private static class QueryAdapter extends ArchiveAdapter {

        private final CountDownLatch latch;
        private final QueryResult result;

        QueryAdapter(CountDownLatch latch, QueryResult result) {
            this.latch = latch;
            this.result = result;
        }

        @Override
        public void queryUpdateReceived(MALMessageHeader msgHeader, ObjectType objType,
                IdentifierList domain, ArchiveDetailsList objDetails,
                HeterogeneousList objBodies, Map qosProperties) {
            result.objDetails.add(objDetails);
            result.objBodies.add(objBodies);
        }

        @Override
        public void queryResponseReceived(MALMessageHeader msgHeader, Map qosProperties) {
            // Empty RESPONSE signals that all matched objects have been delivered.
            latch.countDown();
        }

        @Override
        public void queryResponseErrorReceived(MALMessageHeader msgHeader,
                MOErrorException error, Map qosProperties) {
            result.error = new MALException(error.toString());
            latch.countDown();
        }
    }

    /**
     * {@link ArchiveAdapter} that captures the {@code count} RESPONSE and
     * releases a {@link CountDownLatch} when the response (or an error)
     * arrives.
     */
    private static class CountAdapter extends ArchiveAdapter {

        private final CountDownLatch latch;
        // AtomicReference because the MAL callback arrives on a different thread.
        final AtomicReference<Long> count = new AtomicReference<>();
        MALException error;

        CountAdapter(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void countResponseReceived(MALMessageHeader msgHeader, Long count, Map qosProperties) {
            this.count.set(count);
            latch.countDown();
        }

        @Override
        public void countResponseErrorReceived(MALMessageHeader msgHeader,
                MOErrorException error, Map qosProperties) {
            this.error = new MALException(error.toString());
            latch.countDown();
        }

        long get() {
            Long c = count.get();
            return c != null ? c : -1L;
        }
    }

    /**
     * Sends an async {@code query} with the given lists and waits for either an
     * error or a normal response. Returns the captured
     * {@link MOErrorException}, or {@code null} if the provider responded
     * successfully instead of with an error.
     *
     * @throws AssertionError if neither a response nor an error arrives within
     * 5 seconds
     */
    private static MOErrorException runQueryExpectError(ArchiveStub stub, ObjectType type,
            ArchiveQuery archiveQuery, QueryFilter queryFilter)
            throws MALInteractionException, MALException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        ErrorCaptor captor = new ErrorCaptor(latch);
        stub.asyncQuery(false, type, archiveQuery, queryFilter, captor);
        Assert.assertTrue("query interaction timed out after 5s", latch.await(5, TimeUnit.SECONDS));
        return captor.error;
    }

    /**
     * Sends an async {@code count} with the given lists and waits for either an
     * error or a normal response. Returns the captured
     * {@link MOErrorException}, or {@code null} if the provider responded
     * successfully instead of with an error.
     *
     * @throws AssertionError if neither a response nor an error arrives within
     * 5 seconds
     */
    private static MOErrorException runCountExpectError(ArchiveStub stub, ObjectType type,
            ArchiveQuery archiveQuery, QueryFilter queryFilter)
            throws MALInteractionException, MALException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        ErrorCaptor captor = new ErrorCaptor(latch);
        stub.asyncCount(type, archiveQuery, queryFilter, captor);
        Assert.assertTrue("count interaction timed out after 5s", latch.await(5, TimeUnit.SECONDS));
        return captor.error;
    }

    /**
     * {@link ArchiveAdapter} that captures the first error received on any
     * query or count callback and releases a latch. Also releases the latch on
     * a normal response so that {@code runQueryExpectError} /
     * {@code runCountExpectError} can distinguish the two cases.
     */
    private static class ErrorCaptor extends ArchiveAdapter {

        private final CountDownLatch latch;
        // volatile: written by the MAL callback thread, read by the test thread after latch.await().
        volatile MOErrorException error;

        ErrorCaptor(CountDownLatch latch) {
            this.latch = latch;
        }

        // --- query error callbacks ---
        @Override
        public void queryAckErrorReceived(MALMessageHeader msgHeader,
                MOErrorException error, Map qosProperties) {
            this.error = error;
            latch.countDown();
        }

        @Override
        public void queryUpdateErrorReceived(MALMessageHeader msgHeader,
                MOErrorException error, Map qosProperties) {
            this.error = error;
            latch.countDown();
        }

        @Override
        public void queryResponseErrorReceived(MALMessageHeader msgHeader,
                MOErrorException error, Map qosProperties) {
            this.error = error;
            latch.countDown();
        }

        /**
         * Releases the latch so the caller knows the interaction completed
         * without error.
         */
        @Override
        public void queryResponseReceived(MALMessageHeader msgHeader, Map qosProperties) {
            latch.countDown();
        }

        // --- count error callbacks ---
        @Override
        public void countAckErrorReceived(MALMessageHeader msgHeader,
                MOErrorException error, Map qosProperties) {
            this.error = error;
            latch.countDown();
        }

        @Override
        public void countResponseErrorReceived(MALMessageHeader msgHeader,
                MOErrorException error, Map qosProperties) {
            this.error = error;
            latch.countDown();
        }

        /**
         * Releases the latch so the caller knows the interaction completed
         * without error.
         */
        @Override
        public void countResponseReceived(MALMessageHeader msgHeader, Long count, Map qosProperties) {
            latch.countDown();
        }
    }

}
