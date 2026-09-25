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

    /**
     * Creates a new {@code EventCOMObject}.
     */
    public EventCOMObject() {
    }

    /**
     * Creates a new {@code EventCOMObject}.
     *
     * @param domain the domain
     * @param objType the obj type
     * @param objId the object id
     * @param source the source
     * @param related the related
     * @param body the body
     * @param timestamp the timestamp
     * @param sourceURI the source uri
     */
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

    /**
     * Returns the domain.
     *
     * @return the domain
     */
    public IdentifierList getDomain() {
        return domain;
    }

    /**
     * Returns the obj type.
     *
     * @return the obj type
     */
    public ObjectType getObjType() {
        return objType;
    }

    /**
     * Returns the object id.
     *
     * @return the object id
     */
    public Long getObjId() {
        return objId;
    }

    /**
     * Returns the source.
     *
     * @return the source
     */
    public ObjectKey getSource() {
        return source;
    }

    /**
     * Returns the related.
     *
     * @return the related
     */
    public Long getRelated() {
        return related;
    }

    /**
     * Returns the body.
     *
     * @return the body
     */
    public Element getBody() {
        return body;
    }

    /**
     * Returns the timestamp.
     *
     * @return the timestamp
     */
    public Time getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the source uri.
     *
     * @return the source uri
     */
    public URI getSourceURI() {
        return sourceURI;
    }

    /**
     * Sets the domain.
     *
     * @param domain the domain
     */
    public void setDomain(IdentifierList domain) {
        this.domain = domain;
    }

    /**
     * Sets the obj type.
     *
     * @param objType the obj type
     */
    public void setObjType(ObjectType objType) {
        this.objType = objType;
    }

    /**
     * Sets the object id.
     *
     * @param objId the object id
     */
    public void setObjId(Long objId) {
        this.objId = objId;
    }

    /**
     * Sets the source.
     *
     * @param source the source
     */
    public void setSource(ObjectKey source) {
        this.source = source;
    }

    /**
     * Sets the related.
     *
     * @param related the related
     */
    public void setRelated(Long related) {
        this.related = related;
    }

    /**
     * Sets the body.
     *
     * @param body the body
     */
    public void setBody(Element body) {
        this.body = body;
    }

    /**
     * Sets the timestamp.
     *
     * @param timestamp the timestamp
     */
    public void setTimestamp(Time timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Sets the source uri.
     *
     * @param sourceURI the source uri
     */
    public void setSourceURI(URI sourceURI) {
        this.sourceURI = sourceURI;
    }

    /**
     * Returns the object key.
     *
     * @return the object key
     */
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
