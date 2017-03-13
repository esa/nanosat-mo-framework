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
import esa.mo.helpertools.helpers.HelperAttributes;
import java.io.Serializable;
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
public class ArchivePersistenceObject implements Serializable{

    private ObjectType objectType;
    private IdentifierList domainId;
    private Long objId;

    // ---------------------    
    
    private ObjectId sourceLink;
    private Long relatedLink;
    private String network;

    private Long timestampArchiveDetails;
    private String providerURI;

    private Element obj;
    
    private Long storeTimestamp;
    
//    public ObjectType getObjectType(){ return HelperCOM.objectTypeId2objectType(this.objectTypeId); }
    public ObjectType getObjectType(){ return this.objectType; }
//    public Long getObjectTypeId(){ return this.objectTypeId; }
    public Long getObjectTypeId(){ return HelperCOM.generateSubKey(this.objectType); }
    
//    public IdentifierList getDomain(){ return HelperMisc.domainId2domain(this.domainId); }
//    public String getDomainId(){ return this.domainId; }
    public IdentifierList getDomain(){ return this.domainId; }
    /*
    public String getDomainId(){ return HelperMisc.domain2domainId(this.domainId); }
    */
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

//    protected Long getStoreTimestamp(){ return storeTimestamp; }

    protected ArchivePersistenceObject() {
    }

    public ArchivePersistenceObject (ObjectType objectType, IdentifierList domain,
            Long objId, ArchiveDetails archiveDetails, Object object){

//        this.objectTypeId = HelperCOM.generateSubKey(objectType);
        this.objectType = objectType;
//        this.domainId = HelperMisc.domain2domainId(domain);
        this.domainId = domain;
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
        this.storeTimestamp = System.currentTimeMillis(); // stamp the time of the object store
    }

    /*
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
*/

}
