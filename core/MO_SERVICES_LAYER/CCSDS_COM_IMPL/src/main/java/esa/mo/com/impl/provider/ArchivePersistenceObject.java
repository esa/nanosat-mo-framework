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

import esa.mo.com.impl.archive.db.COMObjectPK;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.helpertools.helpers.HelperMisc;
import java.io.Serializable;
import javax.persistence.*;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 *
 * @author Cesar Coelho
 */
@Entity
@IdClass(COMObjectPK.class)
@Table(name = "ArchivePersistenceObject", 
       indexes = {
                  @Index(name = "index_related",  columnList="relatedLink", unique = false),
                  @Index(name = "index_network", columnList="network",     unique = false),  
                  @Index(name = "index_timestampArchiveDetails",  columnList="timestampArchiveDetails", unique = false),
                  @Index(name = "index_providerURI",  columnList="providerURI", unique = false)
                } )
public class ArchivePersistenceObject implements Serializable{

    @Id
    @Column(name = "objectTypeId")
    private Long objectTypeId;
    
    @Id
    @Column(name = "domainId")
    private String domainId;
    
    @Id
    @Column(name = "objId")
    private Long objId;

    // ---------------------    
    
    @Column(name = "sourceLink")
//    private COMObjectPK sourceLink;
    private ObjectId sourceLink;
    
    @Column(name = "relatedLink")
    private Long relatedLink;

    @Column(name = "network")
    private String network;

    @Column(name = "timestampArchiveDetails")
//    @Temporal(TemporalType.TIMESTAMP)
//    private java.util.Date timestampArchiveDetails;
    private Long timestampArchiveDetails;

    @Column(name = "nanosecondsFraction")
    private int nanos;
            
    @Column(name = "providerURI")
    private String providerURI;
//    @ManyToOne
//    @JoinColumn(name = "providerURI", referencedColumnName = "id")
//    private ProviderURITable providerURI;
    
//    @OneToOne (cascade = {CascadeType.PERSIST})
//    @JoinColumn(name = "obj")
//    private ObjectBodyHolder obj;

    private Element obj;
    
    @Column(name = "storeTimestamp")
//    @Temporal(TemporalType.TIMESTAMP)
//    private java.util.Date storeTimestamp;
    private Long storeTimestamp;
    
    
    public ObjectType getObjectType(){ return HelperCOM.objectTypeId2objectType(this.objectTypeId); }
    public Long getObjectTypeId(){ return this.objectTypeId; }
    public IdentifierList getDomain(){ return HelperMisc.domainId2domain(this.domainId); }
    public String getDomainId(){ return this.domainId; }
    public Long getObjectId(){ return this.objId; }
//    public Object getObject(){ return HelperAttributes.attribute2JavaType(obj.getObjectBody()); }
    public Object getObject(){ return HelperAttributes.attribute2JavaType(obj);  }

    public ArchiveDetails getArchiveDetails(){
//        return new ArchiveDetails(objId, new ObjectDetails(relatedLink, sourceLink), new Identifier(network), new FineTime(timestampArchiveDetails.getTime()), new URI(providerURI));  
    
//        ObjectId source = (this.sourceLink == null) ? null : this.sourceLink.generateObjectId();
        final ObjectId source = this.sourceLink;
        final Identifier net = (this.network == null) ? null : new Identifier(network);
        final URI uri = (this.providerURI == null) ? null : new URI(providerURI);

//        java.sql.Timestamp ts = new java.sql.Timestamp(timestampArchiveDetails.getTime());
//        ts.setNanos(nanos);
//        return new ArchiveDetails(objId, new ObjectDetails(relatedLink, source), net, new FineTime(HelperTime.getNanosecondsFromSQLTimestamp(ts)), uri);  

        return new ArchiveDetails(objId, new ObjectDetails(relatedLink, source), net, new FineTime(timestampArchiveDetails), uri);  

    }

//    protected java.util.Date getStoreTimestamp(){ return storeTimestamp; }
    protected Long getStoreTimestamp(){ return storeTimestamp; }

    protected ArchivePersistenceObject() {
    }

    public ArchivePersistenceObject (ObjectType objectType, IdentifierList domain,
            Long objId, ArchiveDetails archiveDetails, Object object){

        this.objectTypeId = HelperCOM.generateSubKey(objectType);
        this.domainId = HelperMisc.domain2domainId(domain);
        this.objId = objId;
        
//        java.sql.Timestamp ts = new java.sql.Timestamp(HelperTime.fromNanoToMilli(archiveDetails.getTimestamp().getValue()));
//        nanos = HelperTime.getFractionalPart(archiveDetails.getTimestamp().getValue());
//        this.timestampArchiveDetails = ts;

        this.timestampArchiveDetails = archiveDetails.getTimestamp().getValue();

        this.providerURI = archiveDetails.getProvider().getValue();
        this.network = archiveDetails.getNetwork().getValue();
        this.sourceLink = archiveDetails.getDetails().getSource();
//        this.sourceLink = (archiveDetails.getDetails().getSource() == null) ? null : new COMObjectPK(archiveDetails.getDetails().getSource());
        this.relatedLink = archiveDetails.getDetails().getRelated();
        
//        this.obj = new ObjectBodyHolder((Element) HelperAttributes.javaType2Attribute(object), this.objectTypeId); // Encapsulate it as Attribute
        this.obj = (Element) HelperAttributes.javaType2Attribute(object);
        
        // This is specific to the database, not the COM object timestamp
//        this.storeTimestamp = new java.sql.Timestamp(System.currentTimeMillis()); // stamp the time of the object store
        this.storeTimestamp = System.currentTimeMillis(); // stamp the time of the object store

    }

    public static COMObjectPK generatePK (final ObjectType 
             objectType, final IdentifierList domain, final Long objId){
        // Generate Primary Key
        return new COMObjectPK(
                HelperCOM.generateSubKey(objectType), 
                HelperMisc.domain2domainId(domain), 
                objId );
    }
    
    public COMObjectPK getPrimaryKey(){
        return new COMObjectPK(this.objectTypeId, this.domainId, this.objId );
    }

}
