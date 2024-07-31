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
package esa.mo.nmf.apps.edgeai;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.NMFInterface;
import esa.mo.nmf.NMFProvider;
import java.io.File;
import java.io.IOException;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.platform.artificialintelligence.consumer.ArtificialIntelligenceStub;

/**
 * The adapter for the NMF App
 */
public class MCAdapter extends MonitorAndControlNMFAdapter {

    private static final Logger LOG = Logger.getLogger(MCAdapter.class.getName());

    private static final String ACTION_START_AI = "AI_Start";
    private static final String ACTION_CANCEL_AI = "AI_Cancel";
    private static final int TOTAL_STAGES = 1;

    private final NMFInterface connector;

    public MCAdapter(NMFProvider connector) {
        this.connector = connector;
    }

    @Override
    public void initialRegistrations(MCRegistration registration) {
        registration.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

        // ------------------ Actions ------------------
        ActionDefinitionDetailsList actionDefs = new ActionDefinitionDetailsList();
        IdentifierList actionNames = new IdentifierList();

        ArgumentDefinitionDetailsList arguments1 = new ArgumentDefinitionDetailsList();
        actionDefs.add(new ActionDefinitionDetails(
                "Starts the AI inference using the AI service",
                new UOctet((short) 0),
                new UShort(TOTAL_STAGES),
                arguments1));
        actionNames.add(new Identifier(ACTION_START_AI));
        
        ArgumentDefinitionDetailsList arguments2 = new ArgumentDefinitionDetailsList();
        {
            Byte rawType = Attribute._LONG_TYPE_SHORT_FORM;
            arguments2.add(new ArgumentDefinitionDetails(
                    new Identifier("process id"),
                    "process id",
                    rawType, "", null, null, null));
        }

        actionDefs.add(new ActionDefinitionDetails(
                "Cancel the AI processing",
                new UOctet((short) 0),
                new UShort(1),
                arguments2));
        actionNames.add(new Identifier(ACTION_CANCEL_AI));

        registration.registerActions(actionNames, actionDefs);
    }

    @Override
    public UInteger actionArrived(Identifier name, AttributeValueList attributeValues,
            Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {
        LOG.log(Level.INFO, "Action arrived, with name: " + name.getValue());
        
        if (ACTION_START_AI.equals(name.getValue())) {
            triggerAIInference(actionInstanceObjId, attributeValues);
            return null; // Success!
        } else if (ACTION_CANCEL_AI.equals(name.getValue())) {
            destroyProcess(attributeValues);
            return null;
        }

        return new UInteger(0); // Action service not integrated
    }

    public void onProcessCompleted(Long id, int exitCode) {
        LOG.info("Process with Request Id: " + id + " exited with code: " + exitCode);
        publishParameter(id.toString(), exitCode);
    }

    public void triggerAIInference(Long actionInstanceObjId, AttributeValueList attributeValues) {
        /*
        int minProcessingDurationSeconds = getAs(attributeValues.get(0));
        int maxProcessingDurationSeconds = getAs(attributeValues.get(1));

        LOG.info("Requested to run the python script...");
        LOG.info("Process Min duration " + minProcessingDurationSeconds);
        LOG.info("Process Max duration " + maxProcessingDurationSeconds);
        LOG.info("Process Request Id " + actionInstanceObjId);

        PythonScriptExecutor exec = new PythonScriptExecutor(this,
                actionInstanceObjId, minProcessingDurationSeconds,
                maxProcessingDurationSeconds
        );
        
        exec.triggerAIInference("");

        processMap.put(actionInstanceObjId, exec);
         */

        LOG.log(Level.INFO, "Triggering AI inference...");
        
        try {
            File aiModel = new File("ai-model");
            
            if(!aiModel.exists()) {
                throw new IOException("The folder does not exist: " + aiModel.getAbsolutePath());
            }
            
            if(!aiModel.isDirectory()) {
                throw new IOException("The path is not a directory: " + aiModel.getAbsolutePath());
            }
            
            String modelPath = aiModel.getAbsolutePath() + File.separator + "dummy_model.xml";
            
            File demoTiles = new File("demo-tiles");
            
            if(!demoTiles.exists()) {
                throw new IOException("The folder does not exist: " + demoTiles.getAbsolutePath());
            }
            
            if(!demoTiles.isDirectory()) {
                throw new IOException("The path is not a directory: " + demoTiles.getAbsolutePath());
            }
            
            String inputTilesPath = demoTiles.getAbsolutePath();
            
            ArtificialIntelligenceStub aiService = connector.getPlatformServices().getAIService();
            Long id = aiService.setModel(modelPath);
            LOG.log(Level.SEVERE, "The model was set with id: " + id);
            
            aiService.doInference(id, inputTilesPath);
            LOG.log(Level.SEVERE, "The AI inference was successful!");
        } catch (MALInteractionException | MALException | IOException | NMFException ex) {
            LOG.log(Level.SEVERE, "AI was not performed...", ex);
        }
    }

    private void destroyProcess(AttributeValueList attributeValues) {
        /*
        Long processRequestId = getAs(attributeValues.get(0));
        LOG.info("Requested to destroy Process with Id: " + processRequestId);

        PythonScriptExecutor adapter = processMap.remove(processRequestId);
        if (adapter != null) {
            LOG.info("Killing process Process with Id: " + processRequestId);
            adapter.destroyProcess();
        } else {
            LOG.info("Process Request Id " + processRequestId + " not found");
        }
         */
    }

    private void publishParameter(String id, int exitCode) {
        try {
            String str = "Process Request ID: " + id + " exitCode: " + exitCode;
            connector.pushParameterValue(str, exitCode);
        } catch (NMFException e) {
            LOG.log(Level.SEVERE, "Failed to publish parameter", e);
        }
    }

}
