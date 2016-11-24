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
package esa.mo.nanosatmoframework;

import esa.mo.mc.impl.provider.ActionProviderServiceImpl;
import esa.mo.mc.impl.provider.AggregationProviderServiceImpl;
import esa.mo.mc.impl.provider.AlertProviderServiceImpl;
import esa.mo.mc.impl.provider.ParameterProviderServiceImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetails;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetailsList;
import org.ccsds.moims.mo.mc.alert.structures.AlertDefinitionDetails;
import org.ccsds.moims.mo.mc.alert.structures.AlertDefinitionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetailsList;

/**
 * The MCRegistration class provides methods to be implemented by the
 * registration object in order to let the registration of Parameters, Actions,
 * Aggregations and Alerts.
 *
 */
public class MCRegistration {

    public enum RegistrationMode {
        UPDATE_IF_EXISTS, DONT_UPDATE_IF_EXISTS
    };
    private RegistrationMode mode = RegistrationMode.DONT_UPDATE_IF_EXISTS; // default mode

    public final ParameterProviderServiceImpl parameterService;
    public final AggregationProviderServiceImpl aggregationService;
    public final AlertProviderServiceImpl alertService;
    public final ActionProviderServiceImpl actionService;

    public MCRegistration(
            ParameterProviderServiceImpl parameterService,
            AggregationProviderServiceImpl aggregationService,
            AlertProviderServiceImpl alertService,
            ActionProviderServiceImpl actionService
    ) {
        this.parameterService = parameterService;
        this.aggregationService = aggregationService;
        this.alertService = alertService;
        this.actionService = actionService;
    }

    public void setMode(RegistrationMode mode) {
        this.mode = mode;
    }

    public ParameterProviderServiceImpl getParameterService() {
        return this.parameterService;
    }

    public LongList registerParameters(final ParameterDefinitionDetailsList defs) {
        IdentifierList names = new IdentifierList();

        for (ParameterDefinitionDetails def : defs) {
            names.add(def.getName());
        }

        try {
            LongList objIds = parameterService.listDefinition(names, null);
            ParameterDefinitionDetailsList newDefs = new ParameterDefinitionDetailsList();
            ParameterDefinitionDetailsList duplicateDefs = new ParameterDefinitionDetailsList();
            LongList duplicateObjIds = new LongList();

            for (int i = 0; i < objIds.size(); i++) {
                if (objIds.get(i) == null) {
                    newDefs.add(defs.get(i));
                } else {
                    duplicateDefs.add(defs.get(i));
                    duplicateObjIds.add(objIds.get(i));
                }
            }
            
            parameterService.addDefinition(newDefs, null);

            if (mode == RegistrationMode.UPDATE_IF_EXISTS){
                parameterService.updateDefinition(duplicateObjIds, duplicateDefs, null);
            }
            
            return parameterService.listDefinition(names, null);
        } catch (MALException ex1) {
            Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
        } catch (MALInteractionException ex1) {
            Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
        }

        return null;
    }

    public LongList registerAggregations(AggregationDefinitionDetailsList defs) {
        IdentifierList names = new IdentifierList();

        for (AggregationDefinitionDetails def : defs) {
            names.add(def.getName());
        }

        try {
            LongList objIds = aggregationService.listDefinition(names, null);
            AggregationDefinitionDetailsList newDefs = new AggregationDefinitionDetailsList();
            AggregationDefinitionDetailsList duplicateDefs = new AggregationDefinitionDetailsList();
            LongList duplicateObjIds = new LongList();

            for (int i = 0; i < objIds.size(); i++) {
                if (objIds.get(i) == null) {
                    newDefs.add(defs.get(i));
                } else {
                    duplicateDefs.add(defs.get(i));
                    duplicateObjIds.add(objIds.get(i));
                }
            }
            
            aggregationService.addDefinition(newDefs, null);

            if (mode == RegistrationMode.UPDATE_IF_EXISTS){
                aggregationService.updateDefinition(duplicateObjIds, duplicateDefs, null);
            }
            
            return aggregationService.listDefinition(names, null);
        } catch (MALException ex1) {
            Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
        } catch (MALInteractionException ex1) {
            Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
        }

        return null;
    }

    public LongList registerAlerts(AlertDefinitionDetailsList defs) {
        IdentifierList names = new IdentifierList();

        for (AlertDefinitionDetails def : defs) {
            names.add(def.getName());
        }

        try {
            LongList objIds = alertService.listDefinition(names, null);
            AlertDefinitionDetailsList newDefs = new AlertDefinitionDetailsList();
            AlertDefinitionDetailsList duplicateDefs = new AlertDefinitionDetailsList();
            LongList duplicateObjIds = new LongList();

            for (int i = 0; i < objIds.size(); i++) {
                if (objIds.get(i) == null) {
                    newDefs.add(defs.get(i));
                } else {
                    duplicateDefs.add(defs.get(i));
                    duplicateObjIds.add(objIds.get(i));
                }
            }
            
            alertService.addDefinition(newDefs, null);

            if (mode == RegistrationMode.UPDATE_IF_EXISTS){
                alertService.updateDefinition(duplicateObjIds, duplicateDefs, null);
            }
            
            return alertService.listDefinition(names, null);
        } catch (MALException ex1) {
            Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
        } catch (MALInteractionException ex1) {
            Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
        }

        return null;
    }

    public LongList registerActions(ActionDefinitionDetailsList defs) {
        IdentifierList names = new IdentifierList();

        for (ActionDefinitionDetails def : defs) {
            names.add(def.getName());
        }

        try {
            LongList objIds = actionService.listDefinition(names, null);
            ActionDefinitionDetailsList newDefs = new ActionDefinitionDetailsList();
            ActionDefinitionDetailsList duplicateDefs = new ActionDefinitionDetailsList();
            LongList duplicateObjIds = new LongList();

            for (int i = 0; i < objIds.size(); i++) {
                if (objIds.get(i) == null) {
                    newDefs.add(defs.get(i));
                } else {
                    duplicateDefs.add(defs.get(i));
                    duplicateObjIds.add(objIds.get(i));
                }
            }
            
            actionService.addDefinition(newDefs, null);

            if (mode == RegistrationMode.UPDATE_IF_EXISTS){
                actionService.updateDefinition(duplicateObjIds, duplicateDefs, null);
            }
            
            return actionService.listDefinition(names, null);
        } catch (MALException ex1) {
            Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
        } catch (MALInteractionException ex1) {
            Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
        }

        return null;
    }

}
