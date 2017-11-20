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
package esa.mo.com.impl.consumer;

import esa.mo.com.impl.sync.Dictionary;
import esa.mo.com.impl.sync.EncodeDecode;
import esa.mo.com.impl.util.COMObjectStructure;
import esa.mo.helpertools.misc.ConsumerServiceImpl;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archivesync.ArchiveSyncHelper;
import org.ccsds.moims.mo.com.archivesync.consumer.ArchiveSyncStub;
import org.ccsds.moims.mo.com.structures.ObjectTypeList;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;

/**
 *
 * @author Cesar Coelho
 */
public class ArchiveSyncConsumerServiceImpl extends ConsumerServiceImpl {

    private ArchiveSyncStub archiveSyncService = null;
    private Dictionary dictionary = new Dictionary();

    @Override
    public Object generateServiceStub(MALConsumer tmConsumer) {
        return new ArchiveSyncStub(tmConsumer);
    }

    @Override
    public Object getStub() {
        return this.getArchiveSyncStub();
    }

    public ArchiveSyncStub getArchiveSyncStub() {
        return archiveSyncService;
    }

    public ArchiveSyncConsumerServiceImpl(SingleConnectionDetails connectionDetails) throws MALException, MalformedURLException {
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

        this.connectionDetails = connectionDetails;

        // Close old connection
        if (tmConsumer != null) {
            try {
                tmConsumer.close();
            } catch (MALException ex) {
                Logger.getLogger(ArchiveSyncConsumerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        tmConsumer = connection.startService(
                this.connectionDetails.getProviderURI(),
                this.connectionDetails.getBrokerURI(),
                this.connectionDetails.getDomain(),
                ArchiveSyncHelper.ARCHIVESYNC_SERVICE);

        this.archiveSyncService = new ArchiveSyncStub(tmConsumer);
    }

    public ArrayList<COMObjectStructure> retrieveCOMObjects(FineTime from, FineTime until, ObjectTypeList objTypes) {
        ArchiveSyncGenAdapter adapter = new ArchiveSyncGenAdapter();
        Long iTicket;

        try { // Do a retrieve with the correct times
            iTicket = archiveSyncService.retrieveRange(from, until, objTypes, new Identifier(""), adapter);
        } catch (MALInteractionException ex) {
            Logger.getLogger(ArchiveSyncConsumerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (MALException ex) {
            Logger.getLogger(ArchiveSyncConsumerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        // Wait until it is finished...
        boolean unfinished = true;
        long timeout = 1000;
        while (unfinished) {
            try {
                if (adapter.waitUntilResponseReceived(timeout)) {
                    unfinished = false;
                    Logger.getLogger(ArchiveSyncConsumerServiceImpl.class.getName()).log(
                            Level.INFO, "Finished the synchronization!\n");
                }
            } catch (InterruptedException ex) {
                // Still not complete...
                Logger.getLogger(ArchiveSyncConsumerServiceImpl.class.getName()).log(
                        Level.INFO, "Still unfinished...");

                // If the last piece was not received in less than x seconds,
                // then we need to do something about it
                // Ask to retransmit
                if (adapter.noUpdatesReceivedForThisDuration() > timeout * 4) {
                    Logger.getLogger(ArchiveSyncConsumerServiceImpl.class.getName()).log(
                            Level.INFO, "Asking to retransmit the missing part.", ex);
                    UIntegerList missingIndexes = new UIntegerList();
                    missingIndexes.add(adapter.getLastKnownIndex());
                    missingIndexes.add(new UInteger(0));

                    try {
                        archiveSyncService.retrieveRangeAgain(iTicket, missingIndexes, adapter);
                    } catch (MALInteractionException ex1) {
                        Logger.getLogger(ArchiveSyncConsumerServiceImpl.class.getName()).log(Level.SEVERE, null, ex1);
                    } catch (MALException ex1) {
                        Logger.getLogger(ArchiveSyncConsumerServiceImpl.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
            }
        }

        // Reretrieve the missing pieces
        boolean complete = false;

        while (!complete) {
            if (!adapter.receivedAllChunks()) {
                // Then we will have to retrieve the missing ones...
                UIntegerList missingIndexes = adapter.getMissingIndexes();

                try {
                    archiveSyncService.retrieveRangeAgain(iTicket, missingIndexes, adapter);
                } catch (MALInteractionException ex1) {
                    Logger.getLogger(ArchiveSyncConsumerServiceImpl.class.getName()).log(Level.SEVERE, null, ex1);
                } catch (MALException ex1) {
                    Logger.getLogger(ArchiveSyncConsumerServiceImpl.class.getName()).log(Level.SEVERE, null, ex1);
                }
            } else {
                complete = true;
            }
        }

        // Convert the byte arrays into COM Objects
        ArrayList<byte[]> chunks = adapter.getReceivedChunks();

        ArrayList<COMObjectStructure> objs = EncodeDecode.decodeFromByteArrayList(chunks,
                dictionary, archiveSyncService, this.connectionDetails.getDomain());

        try {
            // Free the data from the provider!
            archiveSyncService.free(iTicket);
        } catch (MALInteractionException ex) {
            Logger.getLogger(ArchiveSyncConsumerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
            Logger.getLogger(ArchiveSyncConsumerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Profit!
        return objs;
    }

}
