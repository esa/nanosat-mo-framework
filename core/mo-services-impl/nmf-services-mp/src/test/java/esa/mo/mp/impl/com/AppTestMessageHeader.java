package esa.mo.mp.impl.com;

import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * AppTestMessageHeader is used by tests to mock MALInteraction message
 */
public class AppTestMessageHeader extends MALMessageHeader {

    @Override
    public URI getFromURI() {
        return new URI("App");
    }

    @Override
    public Blob getAuthenticationId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setAuthenticationId(Blob newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public URI getToURI() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Time getTimestamp() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InteractionType getInteractionType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UOctet getInteractionStage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long getTransactionId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UShort getServiceArea() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UShort getService() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UShort getOperation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UOctet getServiceVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Boolean getIsErrorMessage() {
        // TODO Auto-generated method stub
        return null;
    }

}