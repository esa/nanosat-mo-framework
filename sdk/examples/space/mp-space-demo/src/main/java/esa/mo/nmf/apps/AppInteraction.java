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
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.Time;
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
    public void setQoSProperty(String name, Object value) throws IllegalArgumentException {
    }

    @Override
    public Object getQoSProperty(String name) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Map<String, Object> getQoSProperties() {
        return null;
    }
}

class AppMessageHeader extends MALMessageHeader {

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
    public Boolean getIsErrorMessage() {
        // TODO Auto-generated method stub
        return null;
    }
}
