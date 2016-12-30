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

import esa.mo.com.impl.archive.db.COMObjectPK2;
import esa.mo.com.impl.archive.db.SourceLinkContainer;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.mal.encoder.binary.BinaryDecoder;
import esa.mo.mal.encoder.binary.BinaryEncoder;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.*;
import org.ccsds.moims.mo.com.structures.ObjectType;
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
@IdClass(COMObjectPK2.class)
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
    private Long objectTypeId;

    @Id
    @Column(name = "domainId")
    private Short domainId;

    @Id
    @Column(name = "objId")
    private Long objId;
    // ---------------------    

    @Column(name = "relatedLink")
    private Long relatedLink;

    @Column(name = "network")
    private Short network;

    @Column(name = "timestampArchiveDetails")
    private Long timestampArchiveDetails;

    @Column(name = "providerURI")
    private Short providerURI;

//    @Column(name = "storeTimestamp")
//    private Long storeTimestamp;
    
    private byte[] obj;

    // ---------------------    
    @Column(name = "sourceLinkObjectTypeId")
    private Long sourceLinkObjectTypeId;

    @Column(name = "sourceLinkDomainId")
    private Short sourceLinkDomainId;

    @Column(name = "sourceLinkObjId")
    private Long sourceLinkObjId;
    // ---------------------    

    public ObjectType getObjectType() {
        return HelperCOM.objectTypeId2objectType(this.objectTypeId);
    }

    public Long getObjectTypeId() {
        return this.objectTypeId;
    }

    public Integer getDomainId() {
        return new Integer(this.domainId);
    }

    public Long getObjectId() {
        return this.objId;
    }

    public Long getRelatedLink() {
        return this.relatedLink;
    }

    public SourceLinkContainer getSourceLink() {
        final ObjectType objType = (sourceLinkObjectTypeId != null) ? HelperCOM.objectTypeId2objectType(sourceLinkObjectTypeId) : null;
        final Integer domainIdLocal = (sourceLinkDomainId != null) ? new Integer(sourceLinkDomainId) : null;
        return new SourceLinkContainer(
                objType,
                domainIdLocal,
                sourceLinkObjId
        );
    }

    public Integer getNetwork() {
        return new Integer(this.network);
    }

    public Integer getProviderURI() {
        return new Integer(this.providerURI);
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

    /*
    protected Long getStoreTimestamp() {
        return storeTimestamp;
    }
     */
    protected COMObjectEntity() {
    }

    public COMObjectEntity(ObjectType objectType,
            Integer domain,
            Long objId,
            Long timestampArchiveDetails,
            Integer providerURI,
            Integer network,
            SourceLinkContainer sourceLink,
            Long relatedLink,
            Object object) {

        this.objectTypeId = HelperCOM.generateSubKey(objectType);
        this.domainId = domain.shortValue();
        this.objId = objId;

        this.timestampArchiveDetails = timestampArchiveDetails;
        this.providerURI = providerURI.shortValue();
        this.network = network.shortValue();
        this.relatedLink = relatedLink;

        this.sourceLinkObjectTypeId = (sourceLink == null) ? null : HelperCOM.generateSubKey(sourceLink.getObjectType());
        this.sourceLinkDomainId = (sourceLink == null || sourceLink.getDomainId() == null) ? null : sourceLink.getDomainId().shortValue();
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
                Logger.getLogger(COMObjectEntity.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // This is specific to the database, not the COM object timestamp
//        this.storeTimestamp = System.currentTimeMillis(); // stamp the time of the object store
    }

    public static COMObjectPK2 generatePK(final ObjectType objectType, final Integer domain, final Long objId) {
        // Generate Primary Key
        return new COMObjectPK2(
                HelperCOM.generateSubKey(objectType),
                domain.shortValue(),
                objId);
    }

    public COMObjectPK2 getPrimaryKey() {
        return new COMObjectPK2(this.objectTypeId, this.domainId, this.objId);
    }

}
