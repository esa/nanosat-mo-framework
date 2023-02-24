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
package esa.mo.nmf.apps.pictureprocessor.mo;

import static esa.mo.helpertools.helpers.HelperAttributes.attribute2JavaType;
import esa.mo.nmf.AppStorage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Duration;
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
import org.ccsds.moims.mo.platform.camera.structures.CameraSettings;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormat;
import org.ccsds.moims.mo.platform.camera.structures.PixelResolution;

import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.NMFInterface;
import esa.mo.nmf.NMFProvider;
import esa.mo.nmf.apps.pictureprocessor.process.ProcessEventListener;
import static esa.mo.nmf.apps.pictureprocessor.utils.FileUtils.createDirectoriesIfNotExist;
import java.io.File;
import java.nio.file.Paths;

/**
 * The adapter for the NMF App
 */
public class PictureProcessorMCAdapter extends MonitorAndControlNMFAdapter implements ProcessEventListener {

    private static final Logger LOG = Logger.getLogger(PictureProcessorMCAdapter.class.getName());

    private static final String ACTION_TAKE_AND_PROCESS_PICTURE = "TakeAndProcessPicture";
    private static final String ACTION_DESTROY_PROCESS = "DestroyProcess";
    private static final int TOTAL_STAGES = 1;

    private final Map<Long, PictureReceivedAdapter> processMap = new ConcurrentHashMap<>();
    private final NMFInterface connector;

    public PictureProcessorMCAdapter(NMFProvider connector) {
        this.connector = connector;
    }

    @Override
    public void initialRegistrations(MCRegistration registration) {
        registration.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

        // ------------------ Actions ------------------
        ActionDefinitionDetailsList actionDefs = new ActionDefinitionDetailsList();
        IdentifierList actionNames = new IdentifierList();

        regiserActionTakeAndProcessPicture(actionDefs, actionNames);
        regiserActionDestroyProcess(actionDefs, actionNames);
        // ----

        registration.registerActions(actionNames, actionDefs);
    }

    @Override
    public UInteger actionArrived(Identifier name, AttributeValueList attributeValues, Long actionInstanceObjId,
        boolean reportProgress, MALInteraction interaction) {

        if (ACTION_TAKE_AND_PROCESS_PICTURE.equals(name.getValue())) {
            takeAndProcessPicture(actionInstanceObjId, attributeValues);
            return null; // Success!
        } else if (ACTION_DESTROY_PROCESS.equals(name.getValue())) {
            destroyProcess(attributeValues);
            return null;
        }

        return new UInteger(0); // Action service not integrated
    }

    @Override
    public void onProcessCompleted(Long id, int exitCode) {
        processMap.remove(id);
        LOG.info("Process with Request Id: " + id + " exited with code: " + exitCode);
        publishParameter(id.toString(), exitCode);
    }

    private void regiserActionTakeAndProcessPicture(ActionDefinitionDetailsList actionDefs,
        IdentifierList actionNames) {
        ArgumentDefinitionDetailsList arguments = new ArgumentDefinitionDetailsList();
        {
            Byte rawType = Attribute._INTEGER_TYPE_SHORT_FORM;
            arguments.add(new ArgumentDefinitionDetails(
                    new Identifier("min process duration"),
                    "minimum picture processing duration",
                    rawType, "seconds", null, null, null));
        }
        {
            Byte rawType = Attribute._INTEGER_TYPE_SHORT_FORM;
            arguments.add(new ArgumentDefinitionDetails(
                    new Identifier("max process duration"),
                    "max picture processing duration",
                    rawType, "seconds", null, null, null));
        }

        actionDefs.add(new ActionDefinitionDetails(
            "Uses the NMF Camera to take a picture and process it through a python script", new UOctet((short) 0),
            new UShort(TOTAL_STAGES), arguments));
        actionNames.add(new Identifier(ACTION_TAKE_AND_PROCESS_PICTURE));
    }

    private void regiserActionDestroyProcess(ActionDefinitionDetailsList actionDefs, IdentifierList actionNames) {
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

    private void takeAndProcessPicture(Long actionInstanceObjId, AttributeValueList attributeValues) {
        int minProcessingDurationSeconds = getAs(attributeValues.get(0));
        int maxProcessingDurationSeconds = getAs(attributeValues.get(1));

        LOG.info("Requested take and process picture");
        LOG.info("Process Min duration " + minProcessingDurationSeconds);
        LOG.info("Process Max duration " + maxProcessingDurationSeconds);
        LOG.info("Process Request Id " + actionInstanceObjId);

        File userdata = AppStorage.getAppUserdataDir();
        String path = userdata + File.separator + "pictures";
        Path outputFolder = createDirectoriesIfNotExist(Paths.get(path));
        
        PictureReceivedAdapter adapter = new PictureReceivedAdapter(
                this,
                actionInstanceObjId,
                outputFolder,
                minProcessingDurationSeconds,
                maxProcessingDurationSeconds);
        try {
            connector.getPlatformServices().getCameraService().takePicture(defaultCameraSettings(), adapter);
            processMap.put(actionInstanceObjId, adapter);
        } catch (MALInteractionException | MALException | IOException | NMFException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    private void destroyProcess(AttributeValueList attributeValues) {
        Long processRequestId = getAs(attributeValues.get(0));
        LOG.info("Requested destroy process matching Process Request Id" + processRequestId);

        PictureReceivedAdapter adapter = processMap.remove(processRequestId);
        if (adapter != null) {
            LOG.info("Killing process matching Process Request Id " + processRequestId);
            adapter.stopProcess();
        } else {
            LOG.info("Process Request Id " + processRequestId + "not found");
        }
    }

    private void publishParameter(String id, int exitCode) {
        try {
            connector.pushParameterValue("Process Request ID: " + id
                    + " exitCode: " + exitCode, exitCode);
        } catch (NMFException e) {
            LOG.log(Level.SEVERE, "Failed to publish parameter", e);
        }
    }

    private static CameraSettings defaultCameraSettings() {
        final float gainR = 10;
        final float gainG = 8;
        final float gainB = 10;
        final PixelResolution resolution = new PixelResolution(new UInteger(2048), new UInteger(1944));
        final Duration exposureTime = new Duration(0.200);

        return new CameraSettings(resolution, PictureFormat.JPG, 
                exposureTime, gainR, gainG, gainB, null);
    }

    private static <T> T getAs(AttributeValue attributeValue) {
        if (attributeValue == null) {
            return null;
        }
        return (T) attribute2JavaType(attributeValue.getValue());
    }

}
