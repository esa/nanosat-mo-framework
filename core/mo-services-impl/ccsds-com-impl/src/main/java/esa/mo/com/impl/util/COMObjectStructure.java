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
import esa.mo.helpertools.helpers.HelperMisc;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.URI;

public class COMObjectStructure {

    private final ObjectType objType;
    private final IdentifierList domain;
    private Long objId;

    private ObjectId sourceLink;
    private Long relatedLink;
    private Identifier network;
    private FineTime timestamp;
    private URI providerURI;

    private final Element object;

    public COMObjectStructure(final IdentifierList domain, final ObjectType objType,
        final ArchiveDetails archiveDetails, final Element object) {
        this.objType = objType;
        this.domain = domain;
        this.objId = archiveDetails.getInstId();

        this.sourceLink = archiveDetails.getDetails().getSource();
        this.relatedLink = archiveDetails.getDetails().getRelated();
        this.network = archiveDetails.getNetwork();
        this.timestamp = archiveDetails.getTimestamp();
        this.providerURI = archiveDetails.getProvider();

        this.object = object;
    }

    public COMObjectStructure(final ArchivePersistenceObject archivePersistenceObject) {
        this(archivePersistenceObject.getDomain(), archivePersistenceObject.getObjectType(), archivePersistenceObject
            .getArchiveDetails(), (Element) archivePersistenceObject.getObject());
    }

    public ObjectType getObjType() {
        return objType;
    }

    public IdentifierList getDomain() {
        return domain;
    }

    public Long getObjId() {
        return objId;
    }

    public ObjectId getSourceLink() {
        return sourceLink;
    }

    public Long getRelatedLink() {
        return relatedLink;
    }

    public Identifier getNetwork() {
        return network;
    }

    public FineTime getTimestamp() {
        return timestamp;
    }

    public URI getProviderURI() {
        return providerURI;
    }

    public Element getObject() {
        return object;
    }

    public ElementList getObjects() {
        ElementList bodies;

        try {
            bodies = HelperMisc.element2elementList(object);
            bodies.add(object);
        } catch (Exception ex) {
            bodies = null;
        }

        return bodies;
    }

    public ArchiveDetails getArchiveDetails() {
        return new ArchiveDetails(objId, new ObjectDetails(relatedLink, sourceLink), network, timestamp, providerURI);
    }

}
