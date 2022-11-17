package esa.mo.com.impl.util;

import org.ccsds.moims.mo.com.archive.provider.RetrieveInteraction;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInvoke;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.transport.MALMessage;

public class HelperLocalArchiveRetrieveAdapter extends RetrieveInteraction implements
    HelperArchiveRetrieveAdapterInterface {

    private ElementList objectBodyList;
    private ArchiveDetailsList archiveDetailsList;

    final ObjectType objType;
    final IdentifierList domain;

    public HelperLocalArchiveRetrieveAdapter(MALInvoke interaction, final ObjectType objType,
        final IdentifierList domain) {
        super(interaction);
        this.objType = objType;
        this.domain = domain;
    }

    @Override
    public synchronized MALMessage sendResponse(ArchiveDetailsList objDetails, ElementList objBodies)
        throws MALInteractionException, MALException {
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

        this.notify(); // Notify that it is over
        return null;
    }

    @Override
    public MALMessage sendAcknowledgement() throws MALInteractionException, MALException {
        return null;
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

    @Override
    public void waitUntilReady() {
        // Do nothing
    }

}
