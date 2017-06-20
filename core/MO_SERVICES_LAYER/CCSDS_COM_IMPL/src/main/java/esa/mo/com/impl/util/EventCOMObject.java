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
package esa.mo.com.impl.util;

import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 * The EventCOMObject class holds all the fields of a COM Event.
 *
 * @author Cesar Coelho
 */
public class EventCOMObject {

    private IdentifierList domain;
    private ObjectType objType;
    private Long objId;

    private ObjectId source;
    private Long related;
    private Element body;

    private Time timestamp;
    private Identifier networkZone;
    private URI sourceURI;

    public EventCOMObject() {
    }

    public EventCOMObject(final IdentifierList domain, final ObjectType objType,
            final Long objId, final ObjectId source, final Long related, final Element body,
            final Time timestamp, final Identifier networkZone, final URI sourceURI) {
        this.domain = domain;
        this.objType = objType;
        this.objId = objId;

        this.source = source;
        this.related = related;
        this.body = body;

        this.timestamp = timestamp;
        this.networkZone = networkZone;
        this.sourceURI = sourceURI;
    }

    public IdentifierList getDomain() {
        return domain;
    }

    public ObjectType getObjType() {
        return objType;
    }

    public Long getObjId() {
        return objId;
    }

    public ObjectId getSource() {
        return source;
    }

    public Long getRelated() {
        return related;
    }

    public Element getBody() {
        return body;
    }

    public Time getTimestamp() {
        return timestamp;
    }

    public Identifier getNetworkZone() {
        return networkZone;
    }

    public URI getSourceURI() {
        return sourceURI;
    }

    public void setDomain(IdentifierList domain) {
        this.domain = domain;
    }

    public void setObjType(ObjectType objType) {
        this.objType = objType;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public void setSource(ObjectId source) {
        this.source = source;
    }

    public void setRelated(Long related) {
        this.related = related;
    }

    public void setBody(Element body) {
        this.body = body;
    }

    public void setTimestamp(Time timestamp) {
        this.timestamp = timestamp;
    }

    public void setNetworkZone(Identifier networkZone) {
        this.networkZone = networkZone;
    }

    public void setSourceURI(URI sourceURI) {
        this.sourceURI = sourceURI;
    }

    public ObjectId getObjectId() {
        return new ObjectId(this.objType, new ObjectKey(this.domain, this.objId));
    }

}
