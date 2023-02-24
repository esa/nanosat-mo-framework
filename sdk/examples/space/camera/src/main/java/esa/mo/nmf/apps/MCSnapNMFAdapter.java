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
package esa.mo.nmf.apps;

import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
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
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValueList;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ConditionalConversionList;
import org.ccsds.moims.mo.platform.camera.consumer.CameraAdapter;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormat;
import org.ccsds.moims.mo.platform.camera.structures.PixelResolution;
import esa.mo.nmf.NMFInterface;
import org.ccsds.moims.mo.platform.camera.structures.CameraSettings;

/**
 * The adapter for the NMF App
 */
public class MCSnapNMFAdapter extends MonitorAndControlNMFAdapter {

    private NMFInterface connector;

    private static final String PARAMETER_SNAPS_TAKEN = "NumberOfSnapsTaken";
    private static final String ACTION_TAKE_PICTURE_RAW = "TakeSnap.RAW";
    private static final String ACTION_TAKE_PICTURE_JPG = "TakeSnap.JPG";

    private final AtomicInteger snapsTaken = new AtomicInteger(0);
    private final int width = 2048;
    private final int height = 1944;
    private final int TOTAL_STAGES = 3;
    private final float DEFAULT_GAIN_R = 10;
    private final float DEFAULT_GAIN_G = 8;
    private final float DEFAULT_GAIN_B = 10;

    public void setNMF(NMFInterface connector) {
        this.connector = connector;
    }

    @Override
    public void initialRegistrations(MCRegistration registration) {
        registration.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

        // ------------------ Parameters ------------------
        ParameterDefinitionDetailsList defs = new ParameterDefinitionDetailsList();
        IdentifierList paramNames = new IdentifierList();

        defs.add(new ParameterDefinitionDetails(
                "The number of snaps taken.",
                Union.STRING_SHORT_FORM.byteValue(),
                "",
                false,
                new Duration(10),
                null,
                null
        ));
        paramNames.add(new Identifier(PARAMETER_SNAPS_TAKEN));
        registration.registerParameters(paramNames, defs);

        // ------------------ Actions ------------------
        ActionDefinitionDetailsList actionDefs = new ActionDefinitionDetailsList();
        IdentifierList actionNames = new IdentifierList();

        ArgumentDefinitionDetailsList arguments1 = new ArgumentDefinitionDetailsList();
        {
            Byte rawType = Attribute._INTEGER_TYPE_SHORT_FORM;
            String rawUnit = "Image Format";
            ConditionalConversionList conditionalConversions = null;
            Byte convertedType = null;
            String convertedUnit = null;

            arguments1.add(new ArgumentDefinitionDetails(new Identifier("1"), null,
                    rawType, rawUnit, conditionalConversions, convertedType, convertedUnit));
        }

        actionDefs.add(new ActionDefinitionDetails(
                "Uses the NMF Camera service to take a picture.",
                new UOctet((short) 0),
                new UShort(TOTAL_STAGES),
                arguments1
        ));
        actionNames.add(new Identifier(ACTION_TAKE_PICTURE_RAW));

        actionDefs.add(new ActionDefinitionDetails(
                "Uses the NMF Camera service to take a picture.",
                new UOctet((short) 0),
                new UShort(TOTAL_STAGES),
                arguments1
        ));
        actionNames.add(new Identifier(ACTION_TAKE_PICTURE_JPG));

        registration.registerActions(actionNames, actionDefs);
    }

    @Override
    public Attribute onGetValue(Identifier identifier, Byte rawType) {
        if (connector == null) {
            return null;
        }

        if (PARAMETER_SNAPS_TAKEN.equals(identifier.getValue())) {
            return (Attribute) HelperAttributes.javaType2Attribute(snapsTaken.get());
        }

        return null;
    }

    @Override
    public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
        return false;  // to confirm that the variable was not set
    }

    @Override
    public UInteger actionArrived(Identifier name, AttributeValueList attributeValues,
            Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {
        if (connector == null) {
            return new UInteger(0);
        }

        PixelResolution resolution = new PixelResolution(new UInteger(width), new UInteger(height));

        if (ACTION_TAKE_PICTURE_RAW.equals(name.getValue())) {
            try {
                DataReceivedAdapter adapter = new DataReceivedAdapter(actionInstanceObjId);
                connector.getPlatformServices().getCameraService().takePicture(
                        new CameraSettings(resolution, PictureFormat.RAW, new Duration(0.200),
                                DEFAULT_GAIN_R, DEFAULT_GAIN_G, DEFAULT_GAIN_B, null),
                        adapter
                );
                return null; // Success!
            } catch (MALInteractionException | MALException | IOException | NMFException ex) {
                Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (ACTION_TAKE_PICTURE_JPG.equals(name.getValue())) {
            try {
                DataReceivedAdapter adapter = new DataReceivedAdapter(actionInstanceObjId);
                connector.getPlatformServices().getCameraService().takePicture(
                        new CameraSettings(resolution, PictureFormat.JPG, new Duration(0.200),
                                DEFAULT_GAIN_R, DEFAULT_GAIN_G, DEFAULT_GAIN_B, null),
                        adapter
                );
                return null; // Success!
            } catch (MALInteractionException | MALException | IOException | NMFException ex) {
                Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return new UInteger(0);  // Action service not integrated
    }

    public class DataReceivedAdapter extends CameraAdapter {

        private final int STAGE_ACK = 1;
        private final int STAGE_RSP = 2;
        private final Long actionInstanceObjId;

        DataReceivedAdapter(Long actionInstanceObjId) {
            this.actionInstanceObjId = actionInstanceObjId;
        }

        @Override
        public void takePictureAckReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
                java.util.Map qosProperties) {
            try {
                connector.reportActionExecutionProgress(true, 0, STAGE_ACK, TOTAL_STAGES, actionInstanceObjId);
            } catch (NMFException ex) {
                Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE,
                        "The action progress could not be reported!", ex);
            }
        }

        @Override
        public void takePictureResponseReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
                org.ccsds.moims.mo.platform.camera.structures.Picture picture, java.util.Map qosProperties) {
            // The picture was received!
            snapsTaken.incrementAndGet();

            try {
                connector.reportActionExecutionProgress(true, 0, STAGE_RSP, TOTAL_STAGES, actionInstanceObjId);
            } catch (NMFException ex) {
                Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE,
                        "The action progress could not be reported!", ex);
            }

            final String folder = "toGround";
            File dir = new File(folder);
            dir.mkdirs();

            Date date = new Date(System.currentTimeMillis());
            Format format = new SimpleDateFormat("yyyyMMdd_HHmmss_");
            final String timeNow = format.format(date);
            final String filenamePrefix = folder + File.separator + timeNow;

            try {
                // Store it in a file!
                if (picture.getSettings().getFormat().equals(PictureFormat.RAW)) {
                    FileOutputStream fos = new FileOutputStream(filenamePrefix + "myPicture.raw");
                    fos.write(picture.getContent().getValue());
                    fos.flush();
                    fos.close();
                } else if (picture.getSettings().getFormat().equals(PictureFormat.PNG)) {
                    FileOutputStream fos = new FileOutputStream(filenamePrefix + "myPicture.png");
                    fos.write(picture.getContent().getValue());
                    fos.flush();
                    fos.close();
                } else if (picture.getSettings().getFormat().equals(PictureFormat.BMP)) {
                    FileOutputStream fos = new FileOutputStream(filenamePrefix + "myPicture.bmp");
                    fos.write(picture.getContent().getValue());
                    fos.flush();
                    fos.close();
                } else if (picture.getSettings().getFormat().equals(PictureFormat.JPG)) {
                    FileOutputStream fos = new FileOutputStream(filenamePrefix + "myPicture.jpg");
                    fos.write(picture.getContent().getValue());
                    fos.flush();
                    fos.close();
                }
            } catch (MALException | IOException ex) {
                Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }

            try { // Stored
                connector.reportActionExecutionProgress(true, 0, 3, TOTAL_STAGES, actionInstanceObjId);
            } catch (NMFException ex) {
                Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE,
                        "The action progress could not be reported!", ex);
            }
        }

        @Override
        public void takePictureAckErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
                org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties) {
            try {
                connector.reportActionExecutionProgress(false, 1, STAGE_ACK, TOTAL_STAGES, actionInstanceObjId);
            } catch (NMFException ex) {
                Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE,
                        "The action progress could not be reported!", ex);
            }
        }

        @Override
        public void takePictureResponseErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
                org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties) {
            try {
                connector.reportActionExecutionProgress(false, 1, STAGE_RSP, TOTAL_STAGES, actionInstanceObjId);
            } catch (NMFException ex) {
                Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE,
                        "The action progress could not be reported!", ex);
            }
        }
    }
}
