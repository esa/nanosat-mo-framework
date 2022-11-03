package esa.mo.com.impl.util;

import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

class HelperRemoteArchiveRetrieveAdapter extends ArchiveAdapter implements HelperArchiveRetrieveAdapterInterface {
    private ElementList objectBodyList;
    private ArchiveDetailsList archiveDetailsList;
    private final Semaphore semaphore = new Semaphore(0);

    final ObjectType objType;
    final IdentifierList domain;

    HelperRemoteArchiveRetrieveAdapter(final ObjectType objType, final IdentifierList domain) {
        super();
        this.objType = objType;
        this.domain = domain;
    }

    @Override
    public void retrieveAckReceived(final MALMessageHeader msgHeader, final Map qosProperties) {
    }

    @Override
    public void retrieveAckErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
        HelperArchive.LOGGER.log(Level.SEVERE, "The Archive returned the following error: {0}", error.toString());
        semaphore.release();
    }

    @Override
    public void retrieveResponseReceived(final MALMessageHeader msgHeader, final ArchiveDetailsList objDetails,
                                         final ElementList objBodies, final Map qosProperties) {

        if (objBodies != null) {
            if (!objBodies.isEmpty()) {
                this.objectBodyList = objBodies;
            }
        } else {
            this.objectBodyList = null;
        }

        if (objDetails != null) {
            if (!objDetails.isEmpty()) {
                this.archiveDetailsList = (ArchiveDetailsList) objDetails;
            }
        } else {
            this.archiveDetailsList = null;
        }

        semaphore.release();
    }

    @Override
    public void retrieveResponseErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error, final Map qosProperties) {
        HelperArchive.LOGGER.log(Level.SEVERE, "The Archive returned the following error: {0}", error.toString());

        semaphore.release();
    }

    @Override
    public void waitUntilReady() {
        try {
            semaphore.acquire();
        } catch (final InterruptedException ex) {
            HelperArchive.LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ObjectType getObjType() {
        return objType;
    }

    @Override
    public IdentifierList getDomain() {
        return domain;
    }

    @Override
    public ArchiveDetailsList getArchiveDetailsList() {
        return archiveDetailsList;
    }

    @Override
    public ElementList getObjectBodyList() {
        return objectBodyList;
    }

}
