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
package esa.mo.com.impl.archive.entities;

import esa.mo.com.impl.archive.db.COMObjectEntityPK;
import esa.mo.com.impl.archive.db.SourceLinkContainer;
import esa.mo.com.impl.archive.encoding.BinaryDecoder;
import esa.mo.com.impl.archive.encoding.BinaryEncoder;
import esa.mo.helpertools.helpers.HelperAttributes;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALElementFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.FineTime;

/**
 * The entity class that holds COM objects. Used to be a persistence entity
 * however eclipse-link was removed. Now, this class is used to hold COM
 * Objects.
 *
 * @author Cesar Coelho
 */
public class COMObjectEntity implements Serializable {

    private Integer objectTypeId;
    private Integer domainId;
    private Long objId;
    private Long relatedLink;
    private Integer network;
    private Long timestampArchiveDetails;
    private Integer providerURI;
    private byte[] objBody;
    private Integer sourceLinkObjectTypeId;
    private Integer sourceLinkDomainId;
    private Long sourceLinkObjId;

    public COMObjectEntity(Integer objectTypeId, Integer domain, Long objId, Long timestampArchiveDetails,
        Integer providerURI, Integer network, SourceLinkContainer sourceLink, Long relatedLink, Object object) {
        this.objectTypeId = objectTypeId;
        this.domainId = domain;
        this.objId = objId;

        this.timestampArchiveDetails = timestampArchiveDetails;
        this.providerURI = providerURI;
        this.network = network;
        this.relatedLink = relatedLink;

        this.sourceLinkObjectTypeId = (sourceLink == null) ? null : sourceLink.getObjectTypeId();
        this.sourceLinkDomainId = (sourceLink == null || sourceLink.getDomainId() == null) ? null : sourceLink
            .getDomainId();
        this.sourceLinkObjId = (sourceLink == null) ? null : sourceLink.getObjId();

        this.objBody = null;
        final Element ele = (Element) HelperAttributes.javaType2Attribute(object);

        if (ele != null) {
            try {
                final ByteArrayOutputStream bodyBaos = new ByteArrayOutputStream();
                final BinaryEncoder be = new BinaryEncoder(bodyBaos);
                be.encodeLong(ele.getShortForm());
                be.encodeNullableElement(ele);
                this.objBody = bodyBaos.toByteArray();
                be.close();
            } catch (MALException ex) {
                Logger.getLogger(COMObjectEntity.class.getName()).log(Level.SEVERE,
                    "Could not encode COM object with object body class: " + ele.getClass().getSimpleName(), ex);
            }
        }
    }

    public COMObjectEntity(Integer objectTypeId, Integer domain, Long objId, Long timestampArchiveDetails,
        Integer providerURI, Integer network, SourceLinkContainer sourceLink, Long relatedLink, byte[] object) {
        this.objectTypeId = objectTypeId;
        this.domainId = domain;
        this.objId = objId;

        this.timestampArchiveDetails = timestampArchiveDetails;
        this.providerURI = providerURI;
        this.network = network;
        this.relatedLink = relatedLink;

        this.sourceLinkObjectTypeId = sourceLink.getObjectTypeId();
        this.sourceLinkDomainId = sourceLink.getDomainId();
        this.sourceLinkObjId = sourceLink.getObjId();

        this.objBody = object;
    }

    public static COMObjectEntityPK generatePK(final Integer objectTypeId, final Integer domain, final Long objId) {
        return new COMObjectEntityPK(objectTypeId, domain, objId);
    }

    public COMObjectEntityPK getPrimaryKey() {
        return new COMObjectEntityPK(this.objectTypeId, this.domainId, this.objId);
    }

    public Integer getObjectTypeId() {
        return this.objectTypeId;
    }

    public Integer getDomainId() {
        return this.domainId;
    }

    public Long getObjectId() {
        return this.objId;
    }

    public Long getRelatedLink() {
        return this.relatedLink;
    }

    public SourceLinkContainer getSourceLink() {
        final Integer domainIdLocal = (sourceLinkDomainId != null) ? sourceLinkDomainId : null;
        return new SourceLinkContainer(sourceLinkObjectTypeId, domainIdLocal, sourceLinkObjId);
    }

    public Integer getNetwork() {
        return (this.network);
    }

    public Integer getProviderURI() {
        return (this.providerURI);
    }

    public FineTime getTimestamp() {
        return new FineTime(this.timestampArchiveDetails);
    }

    public byte[] getObjectEncoded() {
        return this.objBody;
    }

    public Object getObject() {
        Element elem = null;

        if (this.objBody != null) {
            try {
                final BinaryDecoder binDec = new BinaryDecoder(this.objBody);
                final MALElementFactory eleFact = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(
                    binDec.decodeLong());
                elem = binDec.decodeNullableElement((Element) eleFact.createElement());
            } catch (MALException ex) {
                Logger.getLogger(COMObjectEntity.class.getName()).log(Level.SEVERE,
                    "The object body could not be decoded! Usually happens when there's " +
                        "an update in the APIs. (1) " + this.toString(), ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(COMObjectEntity.class.getName()).log(Level.SEVERE,
                    "The object body could not be decoded! Usually happens when there's " +
                        "an update in the APIs. (2) " + this.toString(), ex);
            } catch (Exception ex) {
                Logger.getLogger(COMObjectEntity.class.getName()).log(Level.SEVERE,
                    "The object body could not be decoded! Usually happens when there's " +
                        "an update in the APIs. (3) " + this.toString(), ex);
            }
        }

        return HelperAttributes.attribute2JavaType(elem);
    }

    @Override
    public String toString() {
        return "----\nFor COM Object:\nobjectTypeId=" + this.objectTypeId + "\ndomainId=" + this.domainId + "\nobjId=" +
            this.objId;
    }

}
