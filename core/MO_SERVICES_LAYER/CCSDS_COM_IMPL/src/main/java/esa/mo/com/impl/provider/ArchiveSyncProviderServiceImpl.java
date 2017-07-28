/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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
package esa.mo.com.impl.provider;

import esa.mo.com.impl.archive.entities.COMObjectEntity;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperTime;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archivesync.ArchiveSyncHelper;
import org.ccsds.moims.mo.com.archivesync.body.GetTimeResponse;
import org.ccsds.moims.mo.com.archivesync.provider.ArchiveSyncInheritanceSkeleton;
import org.ccsds.moims.mo.com.archivesync.provider.RetrieveRangeAgainInteraction;
import org.ccsds.moims.mo.com.archivesync.provider.RetrieveRangeInteraction;
import org.ccsds.moims.mo.com.structures.ObjectTypeList;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.UIntegerList;

/**
 *
 */
public class ArchiveSyncProviderServiceImpl extends ArchiveSyncInheritanceSkeleton {

    private ArchiveManager manager;
    private MALProvider archiveSyncServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    private final AtomicLong lastSync = new AtomicLong(0);

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param manager the Archive Manager
     * @throws MALException if initialization error.
     */
    public synchronized void init(ArchiveManager manager) throws MALException {
        if (!initialiased) {
            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            try {
                ArchiveSyncHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
            }
        }

        this.manager = manager;

        // shut down old service transport
        if (null != archiveSyncServiceProvider) {
            connection.closeAll();
        }

        archiveSyncServiceProvider = connection.startService(ArchiveSyncHelper.ARCHIVESYNC_SERVICE_NAME.toString(), ArchiveSyncHelper.ARCHIVESYNC_SERVICE, false, this);
        running = true;
        initialiased = true;
        Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).info("ArchiveSync service READY");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != archiveSyncServiceProvider) {
                archiveSyncServiceProvider.close();
            }

            manager.close();

            connection.closeAll();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Exception during close down of the provider {0}", ex);
        }
    }

    @Override
    public GetTimeResponse getTime(final MALInteraction interaction) throws MALInteractionException, MALException {
        final FineTime currentTime = HelperTime.getTimestamp();
        final FineTime lastSyncTime = new FineTime(lastSync.get());
         
        return new GetTimeResponse(currentTime, lastSyncTime);
    }

    @Override
    public void retrieveRange(final FineTime from, final FineTime until, final ObjectTypeList objectTypes,
            final RetrieveRangeInteraction interaction) throws MALInteractionException, MALException {

        for(int i = 0 ; i < objectTypes.size(); i++){
            ArchiveQuery archiveQuery = new ArchiveQuery();

            archiveQuery.setStartTime(from);
            archiveQuery.setEndTime(until);

            archiveQuery.setDomain(null);
            archiveQuery.setNetwork(null);
            archiveQuery.setProvider(null);
            archiveQuery.setRelated(new Long(0));
            archiveQuery.setSource(null);
            archiveQuery.setSortFieldName(null);

            final ArrayList<COMObjectEntity> perObjs = manager.queryCOMObjectEntity(objectTypes.get(i), archiveQuery, null);
            // Put it into a queue
            // Have a dedicated thread taking care of the transmission!
            
            
            // We will need to hold the buffer of the transmissions because we
            // might have to retransmit
            
        }
        
        

    }

    @Override
    public void retrieveRangeAgain(final Long transactionTicket, final UIntegerList missingIndexes,
            final RetrieveRangeAgainInteraction interaction) throws MALInteractionException, MALException {
        
        

    }

}
