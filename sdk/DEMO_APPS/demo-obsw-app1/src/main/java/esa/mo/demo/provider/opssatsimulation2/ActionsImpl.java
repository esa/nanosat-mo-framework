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
package esa.mo.demo.provider.opssatsimulation2;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.mc.impl.interfaces.ActionInvocationListener;
import esa.mo.mc.impl.util.MCServicesProvider;
import java.util.logging.Level;
import java.util.logging.Logger;
import opssat.simulator.InstrumentsSimulator;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;

/**
 *
 * @author Cesar Coelho
 */
public class ActionsImpl implements ActionInvocationListener {

    private final InstrumentsSimulator app;
    private final MCServicesProvider mcServices;
    private final COMServicesProvider comServices;

    private enum Action_enum {
        TAKE_PICTURE, RESET
    }

    public ActionsImpl(InstrumentsSimulator application, MCServicesProvider mcServices, COMServicesProvider comServices) {
        this.app = application;
        this.mcServices = mcServices;
        this.comServices = comServices;
    }

    private Action_enum getActionEnum(Identifier identifier) {

        final String name = identifier.toString();

        if (name == null) {
            return null;
        }

        switch (name) {
            case "Take_Picture":
                return Action_enum.TAKE_PICTURE;
            case "Reset":
                return Action_enum.RESET;

            default:
                return null;  // Action not found
        }

    }

    public Boolean exists(Identifier indentifier) {
        return (this.getActionEnum(indentifier) != null);
    }

    @Override
    public UInteger actionArrived(Identifier name, AttributeValueList attributeValues, 
            Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {

        final Action_enum action_enum = this.getActionEnum(name);

        if (action_enum == null) {
            return null;
        }

        switch (action_enum) {
            case TAKE_PICTURE:
                
                Attribute attribute = new Union(1);
                if (attributeValues != null){
                    attribute = (attributeValues.get(0) != null) ? attributeValues.get(0).getValue() : new Union(1);
                }
                
                if (attribute instanceof Union){
                    Union seconds = (Union) attribute;
                    return this.myAction1(actionInstanceObjId, seconds.getIntegerValue(), reportProgress);
                }
                
                return null;

            case RESET:
                comServices.getArchiveService().reset();
                return null;
                
            default:
                return null;  // Action not found
        }

    }

    private UInteger myAction1(Long actionInstanceObjId, int seconds, boolean reportProgress){
        try {
            Thread.sleep(5000);
            if (reportProgress){
                mcServices.getActionService().reportExecutionProgress(true, null, 1, 1, actionInstanceObjId);
            }
            boolean success = app.takePicture(seconds);
            
            Thread.sleep(10000); // 10 sec

            // Emit alert!
            mcServices.getAlertService().publishAlertEvent(null, new Identifier("Alert1"), null, null, null);
                
            Thread.sleep(10000); // 10 sec

            // Push parameter value!
            Union string = new Union("Hello World!");
            mcServices.getParameterService().pushSingleParameterValueAttribute(new Identifier("Pushed_Value"), string, null, null);

            
            return null;
        } catch (InterruptedException ex) {
            Logger.getLogger(ActionsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    
    
}
