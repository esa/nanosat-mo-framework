package esa.mo.nmf.apps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValueList;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeMode;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeTargetTracking;
import org.ccsds.moims.mo.platform.camera.consumer.CameraAdapter;
import org.ccsds.moims.mo.platform.camera.structures.CameraSettings;
import org.ccsds.moims.mo.platform.camera.structures.Picture;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormat;
import org.ccsds.moims.mo.platform.camera.structures.PixelResolution;
import esa.mo.apps.autonomy.util.FileUtils;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.NMFInterface;

/**
 * The MC adapter for the MP Autonomy App
 * Registers SetAttitude and TakeImage Action definitions and processes received Actions
 */
public class MCAutonomyDemoAdapter extends MonitorAndControlNMFAdapter {

    private static final java.util.logging.Logger LOGGER = Logger.getLogger(MCAutonomyDemoAdapter.class.getName());

    private static final String SET_ATTITUDE_ACTION = "SetAttitude";
    private static final String TAKE_IMAGE_ACTION = "TakeImage";

    private static final String DEFAULT_IMAGES_PATH = "images";

    private File imagesDirectory = null;

    private Double setAttitudeDuration = 30d;

    private NMFInterface connector;

    public void setNMF(NMFInterface connector) {
        this.connector = connector;
    }

    @Override
    public void initialRegistrations(MCRegistration registration) {
        registration.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

        String imagesPath = System.getProperty("autonomy.app.images", DEFAULT_IMAGES_PATH);
        imagesDirectory = FileUtils.createDirectory(imagesPath, "app images", DEFAULT_IMAGES_PATH);

        setAttitudeDuration = Double.parseDouble(System.getProperty("adcs.setattitude.duration", Double.toString(setAttitudeDuration)));

        ActionDefinitionDetailsList actionDefinitions = new ActionDefinitionDetailsList();
        IdentifierList actionNames = new IdentifierList();

        ArgumentDefinitionDetails latitudeArgumentDefinition = new ArgumentDefinitionDetails(new Identifier("latitude"), "Latitude", (byte) Attribute._DOUBLE_TYPE_SHORT_FORM, "degree", null, null, null);
        ArgumentDefinitionDetails longitudeArgumentDefinition = new ArgumentDefinitionDetails(new Identifier("longitude"), "Longitude", (byte) Attribute._DOUBLE_TYPE_SHORT_FORM, "degree", null, null, null);
        ArgumentDefinitionDetails fileIdArgumentDefinition = new ArgumentDefinitionDetails(new Identifier("fileId"), "Image file ID", (byte) Attribute._STRING_TYPE_SHORT_FORM, null, null, null, null);

        // SetAttitude action
        ArgumentDefinitionDetailsList setAttitudeActionArgumentsDefs = new ArgumentDefinitionDetailsList();
        setAttitudeActionArgumentsDefs.add(latitudeArgumentDefinition);
        setAttitudeActionArgumentsDefs.add(longitudeArgumentDefinition);

        actionNames.add(new Identifier(SET_ATTITUDE_ACTION));
        actionDefinitions.add(new ActionDefinitionDetails("Set attitude using ADCS service", new UOctet((short)0), new UShort(2), setAttitudeActionArgumentsDefs));

        // TakeImage action
        ArgumentDefinitionDetailsList takeImageActionArgumentsDefs = new ArgumentDefinitionDetailsList();
        takeImageActionArgumentsDefs.add(fileIdArgumentDefinition);

        actionNames.add(new Identifier(TAKE_IMAGE_ACTION));
        actionDefinitions.add(new ActionDefinitionDetails("Take image using Camera service", new UOctet((short)0), new UShort(3), takeImageActionArgumentsDefs));

        LOGGER.info("Registering MC actions");
        registration.registerActions(actionNames, actionDefinitions);
    }

    @Override
    public UInteger actionArrived(Identifier name, AttributeValueList attributeValues, Long actionInstanceId, boolean reportProgress, MALInteraction interaction) {

        if (this.connector == null) {
            return new UInteger(0L);
        }

        switch (name.getValue()) {
            case SET_ATTITUDE_ACTION:
                LOGGER.info("SetAttitude action arrived");
                return this.setAttitudeAction(actionInstanceId, attributeValues);
            case TAKE_IMAGE_ACTION:
                LOGGER.info("TakeImage action arrived");
                return this.takeImageAction(actionInstanceId, attributeValues);
            default:
                LOGGER.warning("Unknown " + name.getValue() + " action arrived");
        }
        return new UInteger(0L);
    }

    @Override
    public Attribute onGetValue(Identifier identifier, Byte rawType) throws IOException {
        return null;
    }

    @Override
    public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
        return Boolean.FALSE;
    }

    private UInteger setAttitudeAction(Long actionInstanceId, AttributeValueList attributeValues) {
        Double latitude = HelperAttributes.attribute2double(attributeValues.get(0).getValue());
        Double longitude = HelperAttributes.attribute2double(attributeValues.get(1).getValue());
        try {
            connector.reportActionExecutionProgress(true, 0, 1, 2, actionInstanceId);
            Duration duration = new Duration(setAttitudeDuration);
            AttitudeMode desiredAttitude = new AttitudeModeTargetTracking(latitude.floatValue(), longitude.floatValue());
            this.connector.getPlatformServices().getAutonomousADCSService()
                .setDesiredAttitude(duration, desiredAttitude);
            LOGGER.info(String.format("SetDesiredAttitude duration=%s seconds", setAttitudeDuration));
            connector.reportActionExecutionProgress(true, 0, 2, 2, actionInstanceId);
            return null;
        } catch (MALInteractionException | MALException | IOException | NMFException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
        return new UInteger(0L);
    }

    private UInteger takeImageAction(Long actionInstanceId, AttributeValueList attributeValues) {
        String fileId = HelperAttributes.attribute2string(attributeValues.get(0).getValue());
        try {
            connector.reportActionExecutionProgress(true, 0, 1, 3, actionInstanceId);
            PixelResolution resolution = new PixelResolution(new UInteger(2048L), new UInteger(1944L));
            CameraSettings cameraSettings = new CameraSettings(
                resolution,
                PictureFormat.JPG,
                new Duration(0.2D),
                Float.valueOf(1.0F),
                Float.valueOf(1.0F),
                Float.valueOf(1.0F)
            );
            CameraPictureDataAdapter adapter = new CameraPictureDataAdapter(actionInstanceId, fileId);
            this.connector.getPlatformServices().getCameraService()
                .takePicture(cameraSettings, adapter);
            return null;
        } catch (MALInteractionException | MALException | IOException | NMFException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
        return new UInteger(0L);
    }

    class CameraPictureDataAdapter extends CameraAdapter {

        private final Long actionInstanceId;
        private final String fileName;

        CameraPictureDataAdapter(Long actionInstanceId, String fileName) {
            this.actionInstanceId = actionInstanceId;
            this.fileName = fileName;
        }

        @Override
        public void takePictureAckErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
            LOGGER.info("Take Picture Ack Error received " + error.toString());
        }

        @Override
        public void takePictureResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
            LOGGER.info("Take Picture Response Error received " + error.toString());
        }

        @Override
        public void takePictureAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
            LOGGER.info("Take Picture Ack received");
        }

        @Override
        public void takePictureResponseReceived(MALMessageHeader msgHeader, Picture picture, Map qosProperties) {
            LOGGER.info("Take Picture Response received "  + picture.toString());

            try {
                connector.reportActionExecutionProgress(true, 0, 2, 3, actionInstanceId);
            } catch (NMFException e) {
                LOGGER.log(Level.SEVERE, null, e);
            }

            File imageFile = new File(imagesDirectory, String.format("%s.jpg", fileName));
            if (imageFile.exists()) {
                LOGGER.warning(String.format("Image file %s already exists, overwriting file..", imageFile.getAbsolutePath()));
            }

            try (FileOutputStream outputStream = new FileOutputStream(imageFile)) {
                outputStream.write(picture.getContent().getValue());
                outputStream.flush();
                LOGGER.info(String.format("Image file saved to %s", imageFile.getAbsolutePath()));
                connector.reportActionExecutionProgress(true, 0, 3, 3, actionInstanceId);
            } catch (MALException | IOException | NMFException e) {
                LOGGER.log(Level.SEVERE, null, e);
            }
        }
    }
}
