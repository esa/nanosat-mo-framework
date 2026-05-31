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
package esa.mo.com.impl.util;

import java.text.MessageFormat;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperDomain;
import org.ccsds.moims.mo.mal.structures.Element;
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

    private ObjectKey source;
    private Long related;
    private Element body;

    private Time timestamp;
    private URI sourceURI;

    public EventCOMObject() {
    }

    public EventCOMObject(final IdentifierList domain, final ObjectType objType, final Long objId,
        final ObjectKey source, final Long related, final Element body, final Time timestamp,
        final URI sourceURI) {
        this.domain = domain;
        this.objType = objType;
        this.objId = objId;

        this.source = source;
        this.related = related;
        this.body = body;

        this.timestamp = timestamp;
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

    public ObjectKey getSource() {
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

    public void setSource(ObjectKey source) {
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

    public void setSourceURI(URI sourceURI) {
        this.sourceURI = sourceURI;
    }

    public ObjectKey getObjectKey() {
        return new ObjectKey(this.objType, this.domain, this.objId);
    }

    @Override
    public String toString() {
        return MessageFormat.format("EventCOMObject: domain={1}, objType={2}, objId={3}, source={4}, related={5}" +
            ", body={6}, timestamp={7}, sourceURI={8}", HelperDomain.domain2domainId(domain), objType,
            objId, source, related, body, timestamp, sourceURI);
    }
}
