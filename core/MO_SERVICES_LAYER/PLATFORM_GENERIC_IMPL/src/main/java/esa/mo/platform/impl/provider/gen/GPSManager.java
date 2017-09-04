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
package esa.mo.platform.impl.provider.gen;

import esa.mo.com.impl.util.DefinitionsManager;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.URI;
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
        }else{
            
        }

    }
    
    @Override
    public Boolean compareName(final Long objId, final Identifier name) {
        return this.get(objId).getName().equals(name);
    }

    @Override
    public ElementList newDefinitionList() {
        return new NearbyPositionDefinitionList();
    }

    public NearbyPositionDefinition get(final Long input) {
        return (NearbyPositionDefinition) this.getDef(input);
    }

    public synchronized Boolean getPreviousStatus(final Long input) {
        return previousIsInsideStatus.get(input);
    }

    public synchronized boolean setPreviousStatus(final Long input, final boolean isInside) {
        return previousIsInsideStatus.put(input, isInside);
    }

    public NearbyPositionDefinitionList getAll(){
        return (NearbyPositionDefinitionList) this.getAllDefs();
    }

    public Long add(final NearbyPositionDefinition definition, final ObjectId source, URI uri){
        if (super.getArchiveService() == null) {
            uniqueObjIdDef++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
            this.addDef(uniqueObjIdDef, definition);
            return uniqueObjIdDef;
        }else{
            NearbyPositionDefinitionList defs = new NearbyPositionDefinitionList();
            defs.add(definition);

            try {
                LongList objIds = super.getArchiveService().store(
                        true,
                        GPSHelper.NEARBYPOSITION_OBJECT_TYPE,
                        ConfigurationProviderSingleton.getDomain(),
                        HelperArchive.generateArchiveDetailsList(null, source, uri),
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
      
    public boolean delete(final Long objId){
        return this.deleteDef(objId);
    }

    protected Long storeAndGenerateNearbyPositionAlertId(final Boolean inside, 
            final Long objId, final URI uri) {
        if (super.getArchiveService() != null) {
            BooleanList isEnteringList = new BooleanList();
            isEnteringList.add(inside);

            try {  // requirement: 3.3.4.2
                LongList objIds = super.getArchiveService().store(
                        true,
                        GPSHelper.NEARBYPOSITIONALERT_OBJECT_TYPE,
                        ConfigurationProviderSingleton.getDomain(),
                        HelperArchive.generateArchiveDetailsList(objId, null, uri),
                        isEnteringList,
                        null);

                if (objIds.size() == 1) {
                    return objIds.get(0);
                }

            } catch (MALException ex) {
                Logger.getLogger(GPSManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(GPSManager.class.getName()).log(Level.SEVERE, null, ex);
            }

            return null;
        }
        
        return new Long(0);
    }

}
