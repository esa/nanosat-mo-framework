package esa.mo.nmf.apps;

import java.util.Map;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * Used by MPAutonomyDemoAdapter to create app interaction with provider
 */
public class AppInteraction implements MALInteraction {

    @Override
    public MALMessageHeader getMessageHeader() {
        return new AppMessageHeader();
    }

    @Override
    public MALOperation getOperation() {
        return null;
    }

    @Override
    public void setQoSProperty(String name, Object value) throws IllegalArgumentException {}

    @Override
    public Object getQoSProperty(String name) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Map<String, Object> getQoSProperties() {
        return null;
    }
}

class AppMessageHeader implements MALMessageHeader {

    @Override
    public URI getURIFrom() {
        return new URI("NMF Autonomy App");
    }

    @Override
    public void setURIFrom(URI newValue) {
        // TODO Auto-generated method stub

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
    public URI getURITo() {
        return new URI("NMF Autonomy App");
    }

    @Override
    public void setURITo(URI newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public Time getTimestamp() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTimestamp(Time newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public QoSLevel getQoSlevel() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UInteger getPriority() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setPriority(UInteger newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public IdentifierList getDomain() {
        IdentifierList domain = new IdentifierList();
        domain.add(new Identifier("Autonomy App"));
        return domain;
    }

    @Override
    public void setDomain(IdentifierList newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public Identifier getNetworkZone() {
        return new Identifier("NanoSat");
    }

    @Override
    public void setNetworkZone(Identifier newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public SessionType getSession() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSession(SessionType newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public Identifier getSessionName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSessionName(Identifier newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public InteractionType getInteractionType() {
        return InteractionType.SEND;
    }

    @Override
    public void setInteractionType(InteractionType newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public UOctet getInteractionStage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setInteractionStage(UOctet newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public Long getTransactionId() {
        return 1L;
    }

    @Override
    public void setTransactionId(Long newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public UShort getServiceArea() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setServiceArea(UShort newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public UShort getService() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setService(UShort newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public UShort getOperation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setOperation(UShort newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public UOctet getAreaVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setAreaVersion(UOctet newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public Boolean getIsErrorMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setIsErrorMessage(Boolean newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setQoSlevel(QoSLevel arg0) {
        // TODO Auto-generated method stub
    }
}
