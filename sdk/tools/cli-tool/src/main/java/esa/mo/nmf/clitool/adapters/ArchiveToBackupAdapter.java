//------------------------------------------------------------------------------
//
// System : ccsds-common
//
// Sub-System : esa.mo.nmf.comarchivetool.adapters
//
// File Name : ArchiveToBackupAdapter.java
//
// Author : marcel.mikolajko
//
// Creation Date : 05.10.2022
//
//------------------------------------------------------------------------------
package esa.mo.nmf.clitool.adapters;

import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.com.impl.util.ArchiveCOMObjectsOutput;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
     * */
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
     * Dumps an archive objects output received from an archive query answer (update or response).
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
        ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {
        dumpArchiveObjectsOutput(new ArchiveCOMObjectsOutput(domain, objType, objDetails, objBodies));
        setIsQueryOver(true);
    }

    @Override
    public void queryUpdateReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain,
        ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {
        dumpArchiveObjectsOutput(new ArchiveCOMObjectsOutput(domain, objType, objDetails, objBodies));
    }

    @Override
    public void queryAckErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
        LOGGER.log(Level.SEVERE, "queryAckErrorReceived", error);
        setIsQueryOver(true);
    }

    @Override
    public void queryUpdateErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
        LOGGER.log(Level.SEVERE, "queryUpdateErrorReceived", error);
        setIsQueryOver(true);
    }

    @Override
    public void queryResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
        LOGGER.log(Level.SEVERE, "queryResponseErrorReceived", error);
        setIsQueryOver(true);
    }

    /** {@inheritDoc} */
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
//------------------------------------------------------------------------------