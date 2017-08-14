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

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mc.alert.AlertHelper;
import org.ccsds.moims.mo.mc.alert.structures.AlertDefinitionDetails;
import org.ccsds.moims.mo.mc.alert.structures.AlertDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePair;

/**
 *
 * @author Cesar Coelho
 */
public final class AlertManager extends MCManager {

    private Long uniqueObjIdIdentity;
    private Long uniqueObjIdDef; // Counter (different for every Definition)
    private Long uniqueObjIdPVal;

    public AlertManager(COMServicesProvider comServices) {
        super(comServices);

        if (super.getArchiveService() == null) {  // No Archive?
            this.uniqueObjIdDef = new Long(0); // The zeroth value will not be used (reserved for the wildcard)
        } else {

        }

    }

    public AlertDefinitionDetails getAlertDefinitionFromIdentityId(Long identityId) {
        return (AlertDefinitionDetails) this.getDefinition(identityId);
    }

    public AlertDefinitionDetails getAlertDefinitionFromDefId(Long defId) {
        return (AlertDefinitionDetails) this.getDefinitionFromObjId(defId);
    }

    public ObjectInstancePair add(Identifier name, AlertDefinitionDetails definition,
            ObjectId source, SingleConnectionDetails connectionDetails) { // requirement: 3.3.2.5
        ObjectInstancePair newIdPair = new ObjectInstancePair();
        if (super.getArchiveService() == null) {
            //add to providers local list
            uniqueObjIdIdentity++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
            uniqueObjIdDef++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
            newIdPair = new ObjectInstancePair(uniqueObjIdIdentity, uniqueObjIdDef);

        } else {
            try {
                //requirement: 3.4.10.2.e: if an AlertName ever existed before, use the old AlertIdentity-Object by retrieving it from the archive
                //check if the name existed before and retrieve id if found
                Long identityId = retrieveIdentityIdByNameFromArchive(ConfigurationProviderSingleton.getDomain(),
                        name, AlertHelper.ALERTIDENTITY_OBJECT_TYPE);

                //in case the AlertName never existed before, create a new identity
                if (identityId == null) {
                    IdentifierList names = new IdentifierList();
                    names.add(name);
                    //add to the archive; requirement: 3.4.7.a
                    LongList identityIds = super.getArchiveService().store(true,
                            AlertHelper.ALERTIDENTITY_OBJECT_TYPE, //requirement: 3.4.4.a
                            ConfigurationProviderSingleton.getDomain(),
                            HelperArchive.generateArchiveDetailsList(null, source, connectionDetails), //requirement 3.4.4.g
                            names, //requirement: 3.4.4.b
                            null);

                    //there is only one identity created, so get the id and set it as the related id
                    identityId = identityIds.get(0);
                }
                AlertDefinitionDetailsList defs = new AlertDefinitionDetailsList();
                defs.add(definition);
                //add to the archive; requirement: 3.4.7.a
                LongList defIds = super.getArchiveService().store(true,
                        AlertHelper.ALERTDEFINITION_OBJECT_TYPE, //requirement: 3.4.4.c
                        ConfigurationProviderSingleton.getDomain(),
                        HelperArchive.generateArchiveDetailsList(identityId, source, connectionDetails), //requirement: 3.4.4.e, 3.4.4.h
                        defs,
                        null);

                //add to providers local list
                newIdPair = new ObjectInstancePair(identityId, defIds.get(0));
            } catch (MALException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        this.addIdentityDefinition(name, newIdPair, definition);
        return newIdPair;
    }

    public Long update(final Long identityId, final AlertDefinitionDetails definition,
            final ObjectId source, final SingleConnectionDetails connectionDetails) { // requirement: 3.3.2.5
        Long newDefId = null;

        if (super.getArchiveService() == null) { //only update locally
            //add to providers local list
            uniqueObjIdDef++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
            newDefId = uniqueObjIdDef;
        } else { // update in the COM Archive        
            try {
                AlertDefinitionDetailsList defs = new AlertDefinitionDetailsList();
                defs.add(definition);
                //create a new AlertDefinition and add to the archive; requirement: 3.4.7.a
                LongList defIds = super.getArchiveService().store(true,
                        AlertHelper.ALERTDEFINITION_OBJECT_TYPE, //requirement: 3.4.4.c
                        ConfigurationProviderSingleton.getDomain(),
                        HelperArchive.generateArchiveDetailsList(identityId, source, connectionDetails), //requirement: 3.4.4.d, 3.4.4.h
                        defs,
                        null);

                newDefId = defIds.get(0);
            } catch (MALException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        this.updateDef(identityId, newDefId, definition);
        return newDefId;
    }

    public boolean delete(Long objId) {
        if (this.deleteIdentity(objId)) {
            return true;
        }
        return false;
    }

    public boolean setGenerationEnabled(final Long identityId, final Boolean bool,
            final ObjectId source, final SingleConnectionDetails connectionDetails) { // requirement: 3.3.2.5
        AlertDefinitionDetails def = this.getAlertDefinitionFromIdentityId(identityId);
        if (def == null) {
            return false;
        }

        if (def.getGenerationEnabled().booleanValue() == bool) { // Is it set with the requested value already?
            return false; // the value was not changed
        }

        def.setGenerationEnabled(bool);
        this.update(identityId, def, source, connectionDetails);

        return true;
    }

    public void setGenerationEnabledAll(final Boolean bool, final ObjectId source,
            final SingleConnectionDetails connectionDetails) {
        LongList identityIds = new LongList();
        identityIds.addAll(this.listAllIdentities());

        for (Long identityId : identityIds) {
            AlertDefinitionDetails def = this.getAlertDefinitionFromIdentityId(identityId);
            def.setGenerationEnabled(bool);
            this.update(identityId, def, source, connectionDetails);
        }
    }

}
