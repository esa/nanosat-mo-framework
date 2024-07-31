/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
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
package esa.mo.com.impl.consumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;

/**
 *
 * @author Cesar Coelho
 */
public class ArchiveSyncGenAdapter extends org.ccsds.moims.mo.com.archivesync.consumer.ArchiveSyncAdapter {

    private final Map<Integer, byte[]> receivedChunks;
    private final Semaphore completed;
    //    private Long interactionTicket = null;
    private UInteger numberOfChunks = null;
    private long lastTimeReceived = 0;
    private long lastknowIndex = 0;

    public ArchiveSyncGenAdapter() {
        this.receivedChunks = new HashMap<>();
        this.completed = new Semaphore(0);
    }

    public ArchiveSyncGenAdapter(int estimatedNumberOfChunks) {
        this.receivedChunks = new HashMap<>(estimatedNumberOfChunks);
        this.completed = new Semaphore(0);
    }

    @Override
    public synchronized void retrieveRangeAckReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
        Long interactionTicket, java.util.Map qosProperties) {
        // Later on, do something...
        /*
        Logger.getLogger(ArchiveSyncAdapter.class.getName()).log(Level.INFO,
                "Received Acknowledgement!");
        this.interactionTicket = interactionTicket;
        lastTimeReceived = System.currentTimeMillis();
        */
    }

    @Override
    public synchronized void retrieveRangeUpdateReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
        Blob chunk, UInteger indexReceived, java.util.Map qosProperties) {
        int index = (int) indexReceived.getValue();
        /*
        Logger.getLogger(ArchiveSyncAdapter.class.getName()).log(Level.INFO,
                "Received! Chunk index: " + index);
         */

        lastTimeReceived = System.currentTimeMillis();
        lastknowIndex = index;
        try {
            receivedChunks.put(index, chunk.getValue());
        } catch (MALException ex) {
            Logger.getLogger(ArchiveSyncGenAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void retrieveRangeResponseReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
        UInteger numberOfChunks, java.util.Map qosProperties) {
        Logger.getLogger(ArchiveSyncGenAdapter.class.getName()).log(Level.INFO,
            "Received the last stage! The total number of chunks is: " + numberOfChunks);
        this.numberOfChunks = numberOfChunks;
        completed.release();
    }

    @Override
    public void retrieveRangeAckErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
        org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties) {
        Logger.getLogger(ArchiveSyncGenAdapter.class.getName()).log(Level.SEVERE,
            "retrieveRangeAckErrorReceived: No idea on how this should be handled...", error);
    }

    @Override
    public void retrieveRangeAgainAckReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
        java.util.Map qosProperties) {
        Logger.getLogger(ArchiveSyncGenAdapter.class.getName()).log(Level.INFO,
            "Received Acknowledgement from rerequest!");
    }

    @Override
    public void retrieveRangeAgainUpdateReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
        org.ccsds.moims.mo.mal.structures.Blob chunk, org.ccsds.moims.mo.mal.structures.UInteger indexReceived,
        java.util.Map qosProperties) {
        int index = (int) indexReceived.getValue();
        Logger.getLogger(ArchiveSyncGenAdapter.class.getName()).log(Level.FINE, "Received on rerequest! Chunk index: " +
            index);

        lastTimeReceived = System.currentTimeMillis();
        try {
            receivedChunks.put(index, chunk.getValue());
        } catch (MALException ex) {
            Logger.getLogger(ArchiveSyncGenAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void retrieveRangeAgainResponseReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
        java.util.Map qosProperties) {
        Logger.getLogger(ArchiveSyncGenAdapter.class.getName()).log(Level.INFO, "Success from rerequest!");
    }

    @Override
    public void retrieveRangeAgainAckErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
        org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties) {
        Logger.getLogger(ArchiveSyncGenAdapter.class.getName()).log(Level.SEVERE,
            "retrieveRangeAgainAckErrorReceived: No idea on how this should be handled...", error);
    }

    public void waitUntilResponseReceived() throws InterruptedException {
        completed.acquire();
    }

    public boolean waitUntilResponseReceived(long waitThisMilliseconds) throws InterruptedException {
        return completed.tryAcquire(waitThisMilliseconds, TimeUnit.MILLISECONDS);
    }

    public long noUpdatesReceivedForThisDuration() {
        return System.currentTimeMillis() - lastTimeReceived;
    }

    public boolean transactionCompleted() {
        return (numberOfChunks != null);
    }

    public boolean receivedAllChunks() {
        long nOfChunks = numberOfChunks.getValue();

        for (int i = 0; i < nOfChunks; i++) {
            if (receivedChunks.get(i) == null) {
                return false;
            }
        }

        return true;
    }

    public UInteger getLastKnownIndex() {
        return new UInteger(lastknowIndex);
    }

    public UIntegerList getMissingIndexes() {
        UIntegerList missingIndexes = new UIntegerList();
        long nOfChunks = numberOfChunks.getValue();

        for (int i = 0; i < nOfChunks; i++) {
            if (receivedChunks.get(i) == null) {
                missingIndexes.add(new UInteger(i));
            }
        }

        return missingIndexes;
    }

    public ArrayList<byte[]> getReceivedChunks() {
        return new ArrayList<>(receivedChunks.values());
    }

}
