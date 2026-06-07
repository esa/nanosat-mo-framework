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
package esa.mo.nmf.testbed.e2e;

import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.HeterogeneousList;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.sm.appslauncher.AppsLauncherServiceInfo;

/**
 * COM archive queries scoped to a single NMF app's lifecycle objects.
 *
 * <p>Extracted from {@link AppHarness} to keep lifecycle management and
 * historical-data queries in separate places.
 */
class AppArchiveQueries {

    private final GroundMOAdapterImpl adapter;
    private final Long appId;

    AppArchiveQueries(GroundMOAdapterImpl adapter, Long appId) {
        this.adapter = adapter;
        this.appId = appId;
    }

    /**
     * Queries the archive for AppStarted COM objects whose related link points
     * to this app's AppDetails object.
     *
     * @return list of matching archive objects, never null.
     * @throws IOException if the query fails.
     */
    List<ArchivePersistenceObject> queryAppStarted() throws IOException {
        return queryByRelated(AppsLauncherServiceInfo.APPSTARTED_OBJECT_TYPE);
    }

    /**
     * Queries the archive for AppStopped COM objects whose related link points
     * to this app's AppDetails object.
     *
     * @return list of matching archive objects, never null.
     * @throws IOException if the query fails.
     */
    List<ArchivePersistenceObject> queryAppStopped() throws IOException {
        return queryByRelated(AppsLauncherServiceInfo.APPSTOPPED_OBJECT_TYPE);
    }

    private List<ArchivePersistenceObject> queryByRelated(ObjectType objType) throws IOException {
        List<ArchivePersistenceObject> results = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch latch = new CountDownLatch(1);

        try {
            adapter.getCOMServices().getArchiveService().getArchiveStub().query(
                    Boolean.TRUE, objType, new ArchiveQuery(appId), null,
                    new ArchiveAdapter() {
                        @Override
                        public void queryUpdateReceived(MALMessageHeader msgHeader,
                                ObjectType receivedObjType, IdentifierList domain,
                                ArchiveDetailsList details,
                                HeterogeneousList bodies,
                                java.util.Map qosProperties) {
                            if (details != null) {
                                for (int i = 0; i < details.size(); i++) {
                                    Element body = (bodies != null && i < bodies.size())
                                            ? (Element) bodies.get(i) : null;
                                    results.add(new ArchivePersistenceObject(
                                            receivedObjType, domain,
                                            details.get(i).getId(), details.get(i), body));
                                }
                            }
                        }

                        @Override
                        public void queryResponseReceived(MALMessageHeader msgHeader,
                                java.util.Map qosProperties) {
                            latch.countDown();
                        }

                        @Override
                        public void queryAckErrorReceived(MALMessageHeader msgHeader,
                                MOErrorException error, java.util.Map qosProperties) {
                            latch.countDown();
                        }
                    });
        } catch (MALException | MALInteractionException e) {
            throw new IOException("Archive query failed: " + e.getMessage(), e);
        }
        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return new ArrayList<>(results);
    }
}
