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
package esa.mo.mc.impl.provider;

import static esa.mo.com.impl.util.HelperArchive.generateArchiveDetailsList;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mc.alert.AlertServiceInfo;
import org.ccsds.moims.mo.mc.structures.AlertDefinition;

/**
 *
 * @author Cesar Coelho
 */
public final class AlertManager extends MCManager {

    private Long uniqueObjIdDef; // Counter (different for every Definition)

    public AlertManager(COMServicesProvider comServices) {
        super(comServices);

        if (super.getArchiveService() == null) {  // No Archive?
            this.uniqueObjIdDef = 0L; // The zeroth value will not be used (reserved for the wildcard)
        } else {

        }

    }

    public AlertDefinition getAlertDefinitionFromDefId(Long defId) {
        return (AlertDefinition) this.getDefinition(defId);
    }

    public Long add(AlertDefinition definition, ObjectKey source,
            SingleConnectionDetails connectionDetails) { // requirement: 3.3.2.5
        Long newIdPair = 0L;
        Identifier name = definition.getName();

        if (super.getArchiveService() == null) {
            //add to providers local list
            uniqueObjIdDef++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
            newIdPair = uniqueObjIdDef;

        } else {
            try {
                HeterogeneousList defs = new HeterogeneousList();
                defs.add(definition);
                //add to the archive; requirement: 3.4.7.a
                LongList defIds = super.getArchiveService().store(true,
                        AlertServiceInfo.ALERTDEFINITION_OBJECT_TYPE, //requirement: 3.4.4.c
                        ConfigurationProviderSingleton.getDomain(),
                        HelperArchive.generateArchiveDetailsList(null, source, connectionDetails.getProviderURI()), //requirement: 3.4.4.e, 3.4.4.h
                        defs,
                        null);

                //add to providers local list
                newIdPair = defIds.get(0);
            } catch (MALException | MALInteractionException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        this.addDefinitionLocally(name, newIdPair, definition);
        return newIdPair;
    }

    public Long update(final Long id, final AlertDefinition definition, final ObjectKey source,
            final SingleConnectionDetails connectionDetails) { // requirement: 3.3.2.5
        Long newDefId = id;

        if (super.getArchiveService() == null) { //only update locally
            //add to providers local list
            uniqueObjIdDef++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
            newDefId = uniqueObjIdDef;
        } else { // update in the COM Archive        
            try {
                HeterogeneousList defs = new HeterogeneousList();
                defs.add(definition);
                ArchiveDetailsList metadata = generateArchiveDetailsList(null, source,
                        connectionDetails.getProviderURI(), id);

                // Update a new AlertDefinition and add to the archive; requirement: 3.4.7.a
                super.getArchiveService().update(AlertServiceInfo.ALERTDEFINITION_OBJECT_TYPE,
                        ConfigurationProviderSingleton.getDomain(),
                        metadata,
                        defs, null);
            } catch (MALException | MALInteractionException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        this.updateDef(newDefId, definition);
        return newDefId;
    }

    public Long setGenerationEnabled(final Long identityId, final Boolean bool,
            final ObjectKey source, final SingleConnectionDetails connectionDetails) {
        // requirement: 3.3.2.5
        AlertDefinition def = this.getAlertDefinitionFromDefId(identityId);
        if (def == null) {
            return null;
        }

        // Is it set with the requested value already?
        if (def.getGenerationEnabled().booleanValue() == bool) {
            return identityId; // the value was not changed
        }

        AlertDefinition newDef = new AlertDefinition(def.getName(),
                def.getDescription(), def.getSeverity(), bool, def.getArguments());

        return this.update(identityId, newDef, source, connectionDetails);
    }

    public void setGenerationEnabledAll(final Boolean bool, final ObjectKey source,
            final SingleConnectionDetails connectionDetails) {
        LongList identityIds = new LongList();
        identityIds.addAll(this.listAllDefinitions());

        for (Long identityId : identityIds) {
            AlertDefinition def = this.getAlertDefinitionFromDefId(identityId);
            AlertDefinition newDef = new AlertDefinition(def.getName(),
                    def.getDescription(), def.getSeverity(), bool, def.getArguments());

            this.update(identityId, newDef, source, connectionDetails);
        }
    }

}
