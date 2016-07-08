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
package esa.mo.mc.impl.provider;

import esa.mo.com.impl.util.DefinitionsManager;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mc.alert.AlertHelper;
import org.ccsds.moims.mo.mc.alert.structures.AlertDefinitionDetails;
import org.ccsds.moims.mo.mc.alert.structures.AlertDefinitionDetailsList;

/**
 *
 * @author Cesar Coelho
 */
public final class AlertManager extends DefinitionsManager {
    
    private static final transient int SAVING_PERIOD = 20;  // Used to store the uniqueAValObjId only once every "SAVINGPERIOD" times
    private Long uniqueObjIdDef; // Unique objId Definition (different for every Definition)

    
    public AlertManager(COMServicesProvider comServices){
        super(comServices);

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
        return new AlertDefinitionDetailsList();
    }

    public AlertDefinitionDetails get(Long input) {
        return (AlertDefinitionDetails) this.getDefs().get(input);
    }

    public AlertDefinitionDetailsList getAll(){
        return (AlertDefinitionDetailsList) this.getAllDefs();
    }

    public Long add(AlertDefinitionDetails definition, ObjectId source, SingleConnectionDetails connectionDetails){ // requirement: 3.3.2.5
        definition.setGenerationEnabled(false);  // requirement: 3.4.2.e

        if (super.getArchiveService() == null) {
            uniqueObjIdDef++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
            this.addDef(uniqueObjIdDef, definition);

//            this.save();
            return uniqueObjIdDef;
        }else{
            AlertDefinitionDetailsList defs = new AlertDefinitionDetailsList();
            defs.add(definition);

            try {
                LongList objIds = super.getArchiveService().store(
                        true,
                        AlertHelper.ALERTDEFINITION_OBJECT_TYPE,
                        connectionDetails.getDomain(),
                        HelperArchive.generateArchiveDetailsList(null, source, connectionDetails),
                        defs,
                        null);

                if (objIds.size() == 1) {  // Was it correctly added to the archive? Did it return a unique objId?
                    this.addDef(objIds.get(0), definition);
                    return objIds.get(0);
                }

            } catch (MALException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }
      
    public boolean update(Long objId, AlertDefinitionDetails definition, SingleConnectionDetails connectionDetails){ // requirement: 3.3.2.5
        Boolean success = this.updateDef(objId, definition);  // requirement: 3.7.2.13

        if (super.getArchiveService() != null) {  // It should also update on the COM Archive
            try {
                AlertDefinitionDetailsList defs = new AlertDefinitionDetailsList();
                defs.add(definition);

                ArchiveDetails archiveDetails = HelperArchive.getArchiveDetailsFromArchive(super.getArchiveService(), 
                        AlertHelper.ALERTDEFINITION_OBJECT_TYPE, connectionDetails.getDomain(), objId);

                ArchiveDetailsList archiveDetailsList = new ArchiveDetailsList();
                archiveDetailsList.add(archiveDetails);
                
                super.getArchiveService().update(
                        AlertHelper.ALERTDEFINITION_OBJECT_TYPE,
                        connectionDetails.getDomain(),
                        archiveDetailsList,
                        defs,
                        null);

            } catch (MALException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } catch (MALInteractionException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

//        this.save();
        return success;
    }

    public boolean delete(Long objId){ // requirement: 3.3.2.5

        if (!this.deleteDef(objId)) {
            return false;
        }
        
//        if (super.getArchiveService() == null)
//            this.save();
    
        return true;
    }

    public boolean setGenerationEnabled(Long objId, Boolean bool, SingleConnectionDetails connectionDetails){ // requirement: 3.3.2.5
        AlertDefinitionDetails def = this.get(objId);
        if (def == null){
            return false;
        }
        
        if (def.getGenerationEnabled().booleanValue() == bool) // Is it set with the requested value already?
            return false; // the value was not changed

        def.setGenerationEnabled(bool);
        this.update(objId, def, connectionDetails);
        
        return true;
    }

    public void setGenerationEnabledAll(Boolean bool, SingleConnectionDetails connectionDetails){ 
        LongList objIds = new LongList(); 
        objIds.addAll(this.getDefs().keySet());
        
        for (Long objId : objIds) {
            AlertDefinitionDetails def = this.get(objId);
            def.setGenerationEnabled(bool);
            this.update(objId, def, connectionDetails);
        }
    }

}
