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

import esa.mo.com.impl.provider.ArchivePersistenceObject;
import org.ccsds.moims.mo.com.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectLinks;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.HeterogeneousList;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 * COM Object Structure.
 */
public class COMObjectStructure {

    private final ObjectType objType;
    private final IdentifierList domain;
    private Long objId;

    private ObjectKey sourceLink;
    private Long relatedLink;
    private Time timestamp;
    private URI providerURI;

    private final Element object;

    /**
     * Creates a new {@code COMObjectStructure}.
     *
     * @param domain the domain
     * @param objType the obj type
     * @param archiveDetails the archive details
     * @param object the object
     */
    public COMObjectStructure(final IdentifierList domain, final ObjectType objType,
            final ArchiveDetails archiveDetails, final Element object) {
        this.objType = objType;
        this.domain = domain;
        this.objId = archiveDetails.getId();

        this.sourceLink = archiveDetails.getLinks().getSource();
        this.relatedLink = archiveDetails.getLinks().getRelated();
        this.timestamp = archiveDetails.getTimestamp();
        this.providerURI = archiveDetails.getProvider();

        this.object = object;
    }

    /**
     * Creates a new {@code COMObjectStructure}.
     *
     * @param archivePersistenceObject the archive persistence object
     */
    public COMObjectStructure(final ArchivePersistenceObject archivePersistenceObject) {
        this(archivePersistenceObject.getDomain(),
                archivePersistenceObject.getObjectType(),
                archivePersistenceObject.getArchiveDetails(),
                (Element) archivePersistenceObject.getObject());
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
     * Returns the domain.
     *
     * @return the domain
     */
    public IdentifierList getDomain() {
        return domain;
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
     * Returns the source link.
     *
     * @return the source link
     */
    public ObjectKey getSourceLink() {
        return sourceLink;
    }

    /**
     * Returns the related link.
     *
     * @return the related link
     */
    public Long getRelatedLink() {
        return relatedLink;
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
     * Returns the provider uri.
     *
     * @return the provider uri
     */
    public URI getProviderURI() {
        return providerURI;
    }

    /**
     * Returns the object.
     *
     * @return the object
     */
    public Element getObject() {
        return object;
    }

    /**
     * Returns the objects heterogeneous list.
     *
     * @return the objects heterogeneous list
     */
    public HeterogeneousList getObjectsHeterogeneousList() {
        HeterogeneousList bodies = new HeterogeneousList();
        bodies.add(object);
        return bodies;
    }

    /**
     * Returns the archive details.
     *
     * @return the archive details
     */
    public ArchiveDetails getArchiveDetails() {
        ObjectLinks objDetails = new ObjectLinks(relatedLink, sourceLink);
        return new ArchiveDetails(objId, objDetails, timestamp, providerURI);
    }

}
