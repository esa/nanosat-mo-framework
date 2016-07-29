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
package esa.mo.platform.impl.provider;

import esa.mo.com.impl.util.DefinitionsManager;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.platform.gps.GPSHelper;
import org.ccsds.moims.mo.platform.gps.structures.NearbyPositionDefinition;
import org.ccsds.moims.mo.platform.gps.structures.NearbyPositionDefinitionList;

/**
 *
 * @author Cesar Coelho
 */
public final class GPSManager extends DefinitionsManager {
    
    private Long uniqueObjIdDef; // Unique objId Definition (different for every Definition)
    private final HashMap<Long, Boolean> previousIsInsideStatus;
    
    public GPSManager(COMServicesProvider comServices){
        super(comServices);

        this.previousIsInsideStatus = new HashMap<Long, Boolean>();
        
        if (super.getArchiveService() == null) {  // No Archive?
            this.uniqueObjIdDef = new Long(0); // The zeroth value will not be used (reserved for the wildcard)
//            this.load(); // Load the file
        }else{
            
        }

    }
    
    @Override
    public Boolean compareName(Long objId, Identifier name) {
        return this.get(objId).getName().equals(name);
    }

    @Override
    public ElementList newDefinitionList() {
        return new NearbyPositionDefinitionList();
    }

    public NearbyPositionDefinition get(Long input) {
        return (NearbyPositionDefinition) this.getDefs().get(input);
    }

    public Boolean getPreviousStatus(Long input) {
        return previousIsInsideStatus.get(input);
    }

    public boolean setPreviousStatus(Long input, boolean isInside) {
        return previousIsInsideStatus.put(input, isInside);
    }

    public NearbyPositionDefinitionList getAll(){
        return (NearbyPositionDefinitionList) this.getAllDefs();
    }

    public Long add(NearbyPositionDefinition definition, ObjectId source, SingleConnectionDetails connectionDetails){
        if (super.getArchiveService() == null) {
            uniqueObjIdDef++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
            this.addDef(uniqueObjIdDef, definition);

//            this.save();
            return uniqueObjIdDef;
        }else{
            NearbyPositionDefinitionList defs = new NearbyPositionDefinitionList();
            defs.add(definition);

            try {
                LongList objIds = super.getArchiveService().store(
                        true,
                        GPSHelper.NEARBYPOSITION_OBJECT_TYPE,
                        connectionDetails.getDomain(),
                        HelperArchive.generateArchiveDetailsList(null, source, connectionDetails),
                        defs,
                        null);

                if (objIds.size() == 1) {  // Was it correctly added to the archive? Did it return a unique objId?
                    this.addDef(objIds.get(0), definition);
                    return objIds.get(0);
                }

            } catch (MALException ex) {
                Logger.getLogger(GPSManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(GPSManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }
      
    public boolean delete(Long objId){

        if (!this.deleteDef(objId)) {
            return false;
        }
        
//        if (super.getArchiveService() == null)
//            this.save();
    
        return true;
    }

 

}
