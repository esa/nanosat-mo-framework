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

import esa.mo.com.impl.provider.ArchivePersistenceObject;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;

public class COMObjectStructure {

    private final ObjectType objType;
    private final IdentifierList domain;
    private Long objId;

    private ObjectId sourceLink;
    private Long relatedLink;
    private Identifier network;
    private FineTime timestamp;
    private String providerURI;

    private final ElementList objects;

    public COMObjectStructure(final IdentifierList domain, final ObjectType objType,
            final ArchiveDetails archiveDetails, final ElementList objects) {
        this.objType = objType;
        this.domain = domain;
        this.objId = archiveDetails.getInstId();

        this.sourceLink = archiveDetails.getDetails().getSource();
        this.relatedLink = archiveDetails.getDetails().getRelated();
        this.network = archiveDetails.getNetwork();
        this.timestamp = archiveDetails.getTimestamp();

        this.objects = objects;
    }

    public COMObjectStructure(final ArchivePersistenceObject archivePersistenceObject) {
        this(archivePersistenceObject.getDomain(), archivePersistenceObject.getObjectType(),
                archivePersistenceObject.getArchiveDetails(), (ElementList) archivePersistenceObject.getObject());
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

    public String getProviderURI() {
        return providerURI;
    }

    public ElementList getObjects() {
        return objects;
    }
}
