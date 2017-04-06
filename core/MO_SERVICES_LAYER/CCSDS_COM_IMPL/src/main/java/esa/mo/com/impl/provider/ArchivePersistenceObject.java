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
package esa.mo.com.impl.provider;

import esa.mo.com.impl.util.HelperCOM;
import java.io.Serializable;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 * This class used to be a direct Entity for the db, now it is just a COM Object carrier.
 *
 * @author Cesar Coelho
 */
public class ArchivePersistenceObject implements Serializable {

    private final ObjectType objectType;
    private final IdentifierList domainId;
    private final Long objId;

    private final ObjectId sourceLink;
    private final Long relatedLink;
    private final String network;

    private final Long timestampArchiveDetails;
    private final String providerURI;
//    private final Element obj;
    
    // The Element wrapping was removed!
    private final Object object;

    /*
    protected ArchivePersistenceObject() {
    }
*/

    public ArchivePersistenceObject(final ObjectType objectType, final IdentifierList domain,
            final Long objId, final ArchiveDetails archiveDetails, final Object object) {
        this.objectType = objectType;
        this.domainId = domain;
        this.objId = objId;

        this.providerURI = archiveDetails.getProvider().getValue();
        this.network = archiveDetails.getNetwork().getValue();
        this.timestampArchiveDetails = archiveDetails.getTimestamp().getValue();

        this.sourceLink = archiveDetails.getDetails().getSource();
        this.relatedLink = archiveDetails.getDetails().getRelated();
//        this.obj = (Element) HelperAttributes.javaType2Attribute(object);
        this.object = object;
    }

    public ObjectType getObjectType() {
        return this.objectType;
    }

    public Long getObjectTypeId() {
        return HelperCOM.generateSubKey(this.objectType);
    }

    public IdentifierList getDomain() {
        return this.domainId;
    }

    public Long getObjectId() {
        return this.objId;
    }

    public ArchiveDetails getArchiveDetails() {
        final Identifier net = (this.network == null) ? null : new Identifier(network);
        final URI uri = (this.providerURI == null) ? null : new URI(providerURI);
        return new ArchiveDetails(objId, new ObjectDetails(relatedLink, sourceLink),
                net, new FineTime(timestampArchiveDetails), uri);
    }

    public Object getObject() {
//        return HelperAttributes.attribute2JavaType(obj);
        return this.object;
    }

}
