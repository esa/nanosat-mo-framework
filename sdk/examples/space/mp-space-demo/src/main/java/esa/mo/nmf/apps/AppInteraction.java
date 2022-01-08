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
 * Used by MPSpaceDemoAdapter to create app interaction with provider
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
    public void setQoSProperty(final String name, final Object value) throws IllegalArgumentException {}

    @Override
    public Object getQoSProperty(final String name) throws IllegalArgumentException {
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
        return new URI("App");
    }

    @Override
    public void setURIFrom(final URI newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public Blob getAuthenticationId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setAuthenticationId(final Blob newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public URI getURITo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setURITo(final URI newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public Time getTimestamp() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTimestamp(final Time newValue) {
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
    public void setPriority(final UInteger newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public IdentifierList getDomain() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDomain(final IdentifierList newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public Identifier getNetworkZone() {
        return new Identifier("NanoSat");
    }

    @Override
    public void setNetworkZone(final Identifier newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public SessionType getSession() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSession(final SessionType newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public Identifier getSessionName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSessionName(final Identifier newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public InteractionType getInteractionType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setInteractionType(final InteractionType newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public UOctet getInteractionStage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setInteractionStage(final UOctet newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public Long getTransactionId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTransactionId(final Long newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public UShort getServiceArea() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setServiceArea(final UShort newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public UShort getService() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setService(final UShort newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public UShort getOperation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setOperation(final UShort newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public UOctet getAreaVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setAreaVersion(final UOctet newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public Boolean getIsErrorMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setIsErrorMessage(final Boolean newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setQoSlevel(final QoSLevel arg0) {
        // TODO Auto-generated method stub
    }
}
