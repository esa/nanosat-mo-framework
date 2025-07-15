/* ----------------------------------------------------------------------------
 * Copyright (C) 2022      European Space Agency
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
package esa.mo.nmf.clitool.adapters;

import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.com.impl.util.ArchiveCOMObjectsOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.structures.HeterogeneousList;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * @author marcel.mikolajko
 */
public class ArchiveToBackupAdapter extends ArchiveAdapter implements QueryStatusProvider {

    private static final Logger LOGGER = Logger.getLogger(ArchiveToJsonAdapter.class.getName());

    /**
     * List of objects to save
     */
    List<ArchiveCOMObjectsOutput> objectsToProcess = new ArrayList<>();

    /**
     * True if the query is over (response or any error received)
     */
    private boolean isQueryOver = false;

    /**
     * Creates a new instance of ToBackupArchiveAdapter.
     *
     */
    public ArchiveToBackupAdapter() {
    }

    public boolean saveDataToNewDatabase(ArchiveProviderServiceImpl archive) {
        boolean result = true;
        for (ArchiveCOMObjectsOutput objects : objectsToProcess) {
            try {
                archive.store(false, objects.getObjectType(), objects.getDomain(), objects.getArchiveDetailsList(),
                        objects.getObjectBodies(), null);
            } catch (MALException | MALInteractionException e) {
                LOGGER.log(Level.SEVERE, "Failed to store objects of type: " + objects.getObjectType(), e);
                result = false;
            }
        }

        return result;
    }

    /**
     * Dumps an archive objects output received from an archive query answer
     * (update or response).
     *
     * @param archiveObjectOutput the archive objects outputs
     */
    private synchronized void dumpArchiveObjectsOutput(ArchiveCOMObjectsOutput archiveObjectOutput) {
        // empty comType means query returned nothing
        ObjectType comType = archiveObjectOutput.getObjectType();
        if (comType == null) {
            return;
        }
        objectsToProcess.add(archiveObjectOutput);
    }

    @Override
    public void queryResponseReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain,
            ArchiveDetailsList objDetails, HeterogeneousList objBodies, Map qosProperties) {
        dumpArchiveObjectsOutput(new ArchiveCOMObjectsOutput(domain, objType, objDetails, objBodies));
        setIsQueryOver(true);
    }

    @Override
    public void queryUpdateReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain,
            ArchiveDetailsList objDetails, HeterogeneousList objBodies, Map qosProperties) {
        dumpArchiveObjectsOutput(new ArchiveCOMObjectsOutput(domain, objType, objDetails, objBodies));
    }

    @Override
    public void queryAckErrorReceived(MALMessageHeader msgHeader, MOErrorException error, Map qosProperties) {
        LOGGER.log(Level.SEVERE, "queryAckErrorReceived", error);
        setIsQueryOver(true);
    }

    @Override
    public void queryUpdateErrorReceived(MALMessageHeader msgHeader, MOErrorException error, Map qosProperties) {
        LOGGER.log(Level.SEVERE, "queryUpdateErrorReceived", error);
        setIsQueryOver(true);
    }

    @Override
    public void queryResponseErrorReceived(MALMessageHeader msgHeader, MOErrorException error, Map qosProperties) {
        LOGGER.log(Level.SEVERE, "queryResponseErrorReceived", error);
        setIsQueryOver(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean isQueryOver() {
        return isQueryOver;
    }

    private synchronized void setIsQueryOver(boolean isQueryOver) {
        if (isQueryOver) {
            // once response or error is received, we dump current content to new database file
            //      saveDataToNewDatabase();
        }
        this.isQueryOver = isQueryOver;
    }

    public List<ArchiveCOMObjectsOutput> getObjectsToProcess() {
        return objectsToProcess;
    }
}
