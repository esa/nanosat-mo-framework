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
package esa.mo.com.impl.archive.entities;

import esa.mo.com.impl.archive.db.COMObjectEntityPK;
import esa.mo.com.impl.archive.db.SourceLinkContainer;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.mal.encoder.binary.BinaryDecoder;
import esa.mo.mal.encoder.binary.BinaryEncoder;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.*;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALElementFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.FineTime;

/**
 *
 * @author Cesar Coelho
 */
@Entity
@IdClass(COMObjectEntityPK.class)
@Table(name = "COMObjectEntity",
        indexes = {
            @Index(name = "index_related2", columnList = "relatedLink", unique = false),
            @Index(name = "index_network2", columnList = "network", unique = false),
            @Index(name = "index_timestampArchiveDetails2", columnList = "timestampArchiveDetails", unique = false),
            @Index(name = "index_providerURI2", columnList = "providerURI", unique = false)
        })
public class COMObjectEntity implements Serializable {

    // ---------------------    
    @Id
    @Column(name = "objectTypeId")
    private Integer objectTypeId;

    @Id
    @Column(name = "domainId")
    private Integer domainId;

    @Id
    @Column(name = "objId")
    private Long objId;
    // ---------------------    

    @Column(name = "relatedLink")
    private Long relatedLink;

    @Column(name = "network")
    private Integer network;

    @Column(name = "timestampArchiveDetails")
    private Long timestampArchiveDetails;

    @Column(name = "providerURI")
    private Integer providerURI;

    private byte[] obj;

    // ---------------------    
    @Column(name = "sourceLinkObjectTypeId")
    private Integer sourceLinkObjectTypeId;

    @Column(name = "sourceLinkDomainId")
    private Integer sourceLinkDomainId;

    @Column(name = "sourceLinkObjId")
    private Long sourceLinkObjId;
    // ---------------------    

    protected COMObjectEntity() {
    }

    public COMObjectEntity(Integer objectTypeId,
            Integer domain,
            Long objId,
            Long timestampArchiveDetails,
            Integer providerURI,
            Integer network,
            SourceLinkContainer sourceLink,
            Long relatedLink,
            Object object) {

        this.objectTypeId = objectTypeId;
        this.domainId = domain;
        this.objId = objId;

        this.timestampArchiveDetails = timestampArchiveDetails;
        this.providerURI = providerURI;
        this.network = network;
        this.relatedLink = relatedLink;

        this.sourceLinkObjectTypeId = (sourceLink == null) ? null : sourceLink.getObjectTypeId();
        this.sourceLinkDomainId = (sourceLink == null || sourceLink.getDomainId() == null) ? null : sourceLink.getDomainId();
        this.sourceLinkObjId = (sourceLink == null) ? null : sourceLink.getObjId();

        this.obj = null;
        final Element ele = (Element) HelperAttributes.javaType2Attribute(object);

        if (ele != null) {
            try {
                final ByteArrayOutputStream bodyBaos = new ByteArrayOutputStream();
                final BinaryEncoder be = new BinaryEncoder(bodyBaos);
                be.encodeLong(ele.getShortForm());
                be.encodeNullableElement(ele);
                this.obj = bodyBaos.toByteArray();
            } catch (MALException ex) {
                Logger.getLogger(COMObjectEntity.class.getName()).log(Level.SEVERE,
                        "Could not encode COM object with object body class: " + ele.getClass().getSimpleName(), ex);
            }
        }
    }

    public static COMObjectEntityPK generatePK(final Integer objectTypeId, final Integer domain, final Long objId) {
        // Generate Primary Key
        return new COMObjectEntityPK(
                objectTypeId,
                domain,
                objId);
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
        return new SourceLinkContainer(
                sourceLinkObjectTypeId,
                domainIdLocal,
                sourceLinkObjId
        );
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

    public Object getObject() {
        Element elem = null;

        if (this.obj != null) {
            try {
                final BinaryDecoder binDec = new BinaryDecoder(this.obj);
                final MALElementFactory eleFact = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(binDec.decodeLong());
                elem = binDec.decodeNullableElement((Element) eleFact.createElement());
            } catch (MALException ex) {
                Logger.getLogger(COMObjectEntity.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return HelperAttributes.attribute2JavaType(elem);
    }

    @Override
    public String toString() {
        return "COM Object: this.objectTypeId=" + this.objectTypeId + ", this.domainId=" + this.domainId + ", this.objId=" + this.objId;
    }
    
}
