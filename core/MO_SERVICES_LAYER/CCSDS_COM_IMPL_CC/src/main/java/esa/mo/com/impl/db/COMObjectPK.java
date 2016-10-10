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
package esa.mo.com.impl.db;

import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.helpers.HelperMisc;
import java.io.Serializable;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;

/**
 * Archive Persistence Object Primary Key
 * @author Cesar Coelho
 */
public class COMObjectPK implements Serializable {

    private final Long objectTypeId;
    private final String domainId;
    private final Long objId;

    public COMObjectPK (final Long objectTypeId, final String domain, final Long objId){
        this.objectTypeId = objectTypeId;
        this.domainId = domain;
        this.objId = objId;
    }

    public COMObjectPK (final ObjectId objectId){
        this.objectTypeId = HelperCOM.generateSubKey(objectId.getType());
        this.domainId = HelperMisc.domain2domainId(objectId.getKey().getDomain());
        this.objId = objectId.getKey().getInstId();
    }
    
    @Override
    public boolean equals(Object other) {
    	if (this == other) return true;
        if (!(other instanceof COMObjectPK)) return false;
        COMObjectPK input = (COMObjectPK) other;
        
        return (input.objectTypeId.equals(objectTypeId) &&
                input.domainId.equals(domainId) &&
                input.objId.equals(objId) );
    }

    @Override
    public int hashCode(){
    	return objectTypeId.hashCode() ^ domainId.hashCode() ^ objId.hashCode();
    }
    
    public ObjectId generateObjectId (){
        return new ObjectId(HelperCOM.objectTypeId2objectType(objId), new ObjectKey(HelperMisc.domainId2domain(domainId), objId));
    }

}
