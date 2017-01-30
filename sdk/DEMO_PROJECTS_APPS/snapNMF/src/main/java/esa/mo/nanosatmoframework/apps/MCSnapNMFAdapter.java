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
package esa.mo.nanosatmoframework.apps;

import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.nanosatmoframework.MCRegistration;
import esa.mo.nanosatmoframework.MonitorAndControlNMFAdapter;
import esa.mo.nanosatmoframework.NanoSatMOFrameworkInterface;
import java.io.File;
import java.io.FileNotFoundException;
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
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ConditionalReferenceList;
import org.ccsds.moims.mo.mc.structures.Severity;
import org.ccsds.moims.mo.platform.camera.consumer.CameraAdapter;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormat;
import org.ccsds.moims.mo.platform.camera.structures.PixelResolution;

/**
 * The adapter for the app
 */
public class MCSnapNMFAdapter extends MonitorAndControlNMFAdapter {

    private NanoSatMOFrameworkInterface nmf;

    private static final String PARAMETER_SNAPS_TAKEN = "NumberOfSnapsTaken";

    private static final String ACTION_TAKE_PICTURE = "Camera.TakePicture";

    private static final String ALERT_SUN_POINTING_MODE = "ADCS.SunPointingMode";

    private final AtomicInteger snapsTaken = new AtomicInteger(0);
    private final int width = 2048;
    private final int height = 1944;
    private final int TOTAL_STAGES = 3;

    public void setNMF(NanoSatMOFrameworkInterface nanosatmoframework) {
        this.nmf = nanosatmoframework;
    }

    @Override
    public void initialRegistrations(MCRegistration registration) {

        registration.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

        // ------------------ Parameters ------------------
        ParameterDefinitionDetailsList defs = new ParameterDefinitionDetailsList();

        defs.add(new ParameterDefinitionDetails(
                new Identifier(PARAMETER_SNAPS_TAKEN),
                "The nummber of snaps taken.",
                Union.STRING_SHORT_FORM.byteValue(),
                "",
                false,
                new Duration(10),
                null,
                null
        ));

        registration.registerParameters(defs);

        // ------------------ Actions ------------------
        ActionDefinitionDetailsList actionDefs = new ActionDefinitionDetailsList();

        ArgumentDefinitionDetailsList arguments1 = new ArgumentDefinitionDetailsList();
        {
            Byte rawType = Attribute._INTEGER_TYPE_SHORT_FORM;
            String rawUnit = "Image Format";
            ConditionalReferenceList conversionCondition = null;
            Byte convertedType = null;
            String convertedUnit = null;

            arguments1.add(new ArgumentDefinitionDetails(rawType, rawUnit, conversionCondition, convertedType, convertedUnit));
        }

        actionDefs.add(new ActionDefinitionDetails(
                new Identifier(ACTION_TAKE_PICTURE),
                "Uses the NMF Camera service to take a picture.",
                Severity.INFORMATIONAL,
                new UShort(0),
                arguments1,
                null
        ));

        LongList actionObjIds = registration.registerActions(actionDefs);
    }

    @Override
    public Attribute onGetValue(Identifier identifier, Byte rawType) {
        if (nmf == null) {
            return null;
        }

        if (PARAMETER_SNAPS_TAKEN.equals(identifier.getValue())) {
            return (Attribute) HelperAttributes.javaType2Attribute(snapsTaken.get());
        }

        return null;
    }

    @Override
    public Boolean onSetValue(Identifier identifier, Attribute value) {
        return false;  // to confirm that the variable was set
    }

    @Override
    public UInteger actionArrived(Identifier name, AttributeValueList attributeValues, Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {
        if (nmf == null) {
            return new UInteger(0);
        }

        if (ACTION_TAKE_PICTURE.equals(name.getValue())) {
            PixelResolution resolution = new PixelResolution(new UInteger(width), new UInteger(height));

            try {
                nmf.getPlatformServices().getCameraService().takePicture(resolution, PictureFormat.JPG, new Duration(0.200), new DataReceivedAdapter(actionInstanceObjId));
                return null; // Success!
            } catch (MALInteractionException ex) {
                Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALException ex) {
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
        public void takePictureAckReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, java.util.Map qosProperties) {
            try {
                nmf.reportActionExecutionProgress(true, 0, STAGE_ACK, TOTAL_STAGES, actionInstanceObjId);
            } catch (IOException ex) {
                Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void takePictureResponseReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.platform.camera.structures.Picture picture, java.util.Map qosProperties) {
            // The picture was received!
            snapsTaken.incrementAndGet();

            try {
                nmf.reportActionExecutionProgress(true, 0, STAGE_RSP, TOTAL_STAGES, actionInstanceObjId);
            } catch (IOException ex) {
                Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            final String folder = "snaps";
            File dir = new File(folder);
            dir.mkdirs();
                    
            Date date = new Date(System.currentTimeMillis());
            Format format = new SimpleDateFormat("yyyyMMdd_HHmmss_");
            final String timeNow = format.format(date);

            try {
                // Store it in a file!
                if (picture.getFormat().equals(PictureFormat.RAW)) {
                    FileOutputStream fos = new FileOutputStream(folder + File.separator + timeNow + "myFirstPicture.raw");
                    fos.write(picture.getContent().getValue());
                    fos.flush();
                    fos.close();
                }

                if (picture.getFormat().equals(PictureFormat.PNG)) {
                    FileOutputStream fos = new FileOutputStream(folder + File.separator + timeNow + "myFirstPicture.png");
                    fos.write(picture.getContent().getValue());
                    fos.flush();
                    fos.close();
                }

                if (picture.getFormat().equals(PictureFormat.BMP)) {
                    FileOutputStream fos = new FileOutputStream(folder + File.separator + timeNow + "myFirstPicture.bmp");
                    fos.write(picture.getContent().getValue());
                    fos.flush();
                    fos.close();
                }

                if (picture.getFormat().equals(PictureFormat.JPG)) {
                    FileOutputStream fos = new FileOutputStream(folder + File.separator + timeNow + "myFirstPicture.jpg");
                    fos.write(picture.getContent().getValue());
                    fos.flush();
                    fos.close();
                }

            } catch (FileNotFoundException ex) {
                Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALException ex) {
                Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }

            try { // Stored
                nmf.reportActionExecutionProgress(true, 0, 3, TOTAL_STAGES, actionInstanceObjId);
            } catch (IOException ex) {
                Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        @Override
        public void takePictureAckErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties) {
            try {
                nmf.reportActionExecutionProgress(false, 1, STAGE_ACK, TOTAL_STAGES, actionInstanceObjId);
            } catch (IOException ex) {
                Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        @Override
        public void takePictureResponseErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties) {
            try {
                nmf.reportActionExecutionProgress(false, 1, STAGE_RSP, TOTAL_STAGES, actionInstanceObjId);
            } catch (IOException ex) {
                Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
