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
package esa.mo.nmf.apps.pythonscript.run;

import static esa.mo.helpertools.helpers.HelperAttributes.attribute2JavaType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
import org.ccsds.moims.mo.mc.structures.AttributeValue;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.NMFInterface;
import esa.mo.nmf.NMFProvider;

/**
 * The adapter for the NMF App
 */
public class MCAdapter extends MonitorAndControlNMFAdapter {

    private static final Logger LOG = Logger.getLogger(MCAdapter.class.getName());

    private static final String ACTION_RUN_PYTHON_SCRIPT = "RunPythonScript";
    private static final String ACTION_DESTROY_PROCESS = "DestroyProcess";
    private static final int TOTAL_STAGES = 1;

    private final Map<Long, PythonScriptExecutor> processMap = new ConcurrentHashMap<>();
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

        registerActionDestroyProcess(actionDefs, actionNames);
        registerActionRunPython(actionDefs, actionNames);
        // ----

        registration.registerActions(actionNames, actionDefs);
    }

    @Override
    public UInteger actionArrived(Identifier name, AttributeValueList attributeValues,
            Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {

        if (ACTION_RUN_PYTHON_SCRIPT.equals(name.getValue())) {
            runPythonScript(actionInstanceObjId, attributeValues);
            return null; // Success!
        } else if (ACTION_DESTROY_PROCESS.equals(name.getValue())) {
            destroyProcess(attributeValues);
            return null;
        }

        return new UInteger(0); // Action service not integrated
    }

    public void onProcessCompleted(Long id, int exitCode) {
        processMap.remove(id);
        LOG.info("Process with Request Id: " + id + " exited with code: " + exitCode);
        publishParameter(id.toString(), exitCode);
    }

    private void registerActionRunPython(ActionDefinitionDetailsList actionDefs, IdentifierList actionNames) {
        ArgumentDefinitionDetailsList arguments = new ArgumentDefinitionDetailsList();
        {
            Byte rawType = Attribute._INTEGER_TYPE_SHORT_FORM;
            arguments.add(new ArgumentDefinitionDetails(
                    new Identifier("Min Process duration"),
                    "Minimum Process duration",
                    rawType, "seconds", null, null, null));
        }
        {
            Byte rawType = Attribute._INTEGER_TYPE_SHORT_FORM;
            arguments.add(new ArgumentDefinitionDetails(
                    new Identifier("Max Process duration"),
                    "Max Picture processing duration",
                    rawType, "seconds", null, null, null));
        }

        actionDefs.add(new ActionDefinitionDetails(
                "Runs a python script",
                new UOctet((short) 0),
                new UShort(TOTAL_STAGES),
                arguments));
        actionNames.add(new Identifier(ACTION_RUN_PYTHON_SCRIPT));
    }

    private void registerActionDestroyProcess(ActionDefinitionDetailsList actionDefs, IdentifierList actionNames) {
        ArgumentDefinitionDetailsList arguments = new ArgumentDefinitionDetailsList();
        {
            Byte rawType = Attribute._LONG_TYPE_SHORT_FORM;
            arguments.add(new ArgumentDefinitionDetails(
                    new Identifier("process id"),
                    "process id",
                    rawType, "", null, null, null));
        }

        actionDefs.add(new ActionDefinitionDetails(
                "Destroy a process",
                new UOctet((short) 0),
                new UShort(1),
                arguments));
        actionNames.add(new Identifier(ACTION_DESTROY_PROCESS));
    }

    private void runPythonScript(Long actionInstanceObjId, AttributeValueList attributeValues) {
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
        
        exec.runPythonScript("");

        processMap.put(actionInstanceObjId, exec);
    }

    private void destroyProcess(AttributeValueList attributeValues) {
        Long processRequestId = getAs(attributeValues.get(0));
        LOG.info("Requested to destroy Process with Id: " + processRequestId);

        PythonScriptExecutor adapter = processMap.remove(processRequestId);
        if (adapter != null) {
            LOG.info("Killing process Process with Id: " + processRequestId);
            adapter.destroyProcess();
        } else {
            LOG.info("Process Request Id " + processRequestId + " not found");
        }
    }

    private void publishParameter(String id, int exitCode) {
        try {
            String str = "Process Request ID: " + id + " exitCode: " + exitCode;
            connector.pushParameterValue(str, exitCode);
        } catch (NMFException e) {
            LOG.log(Level.SEVERE, "Failed to publish parameter", e);
        }
    }

    private static <T> T getAs(AttributeValue attributeValue) {
        if (attributeValue == null) {
            return null;
        }
        return (T) attribute2JavaType(attributeValue.getValue());
    }

}
