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
import esa.mo.nanosatmoframework.MCRegistration;
import esa.mo.nanosatmoframework.MonitorAndControlNMFAdapter;
import esa.mo.nanosatmoframework.NanoSatMOFrameworkInterface;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.swing.ImageIcon;
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
    private final int width = 248;
    private final int height = 944;
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
                nmf.getPlatformServices().getCameraService().takePicture(resolution, PictureFormat.RAW, new Duration(0.200), new DataReceivedAdapter(actionInstanceObjId));
            } catch (MALInteractionException ex) {
                Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALException ex) {
                Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;  // Action service not integrated
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

            try {
                nmf.reportActionExecutionProgress(true, 0, STAGE_RSP, TOTAL_STAGES, actionInstanceObjId);
            } catch (IOException ex) {
                Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Store it in a file!
            if(picture.getFormat().equals(PictureFormat.RAW) || picture.getFormat().equals(PictureFormat.RAW_DEBAYERED)){
                
                try {
                    FileOutputStream fos = new FileOutputStream("myFirstPicture.raw");
                    fos.write(picture.getContent().getValue());
                    fos.flush();
                    fos.close();
                    


            BufferedImage img = null;

            try {
                ByteArrayInputStream byteArrayIS = new ByteArrayInputStream(picture.getContent().getValue());

/*
                try {
/*
                    BufferedImage image = new BufferedImage((int) picture.getDimension().getWidth().getValue(),
                            (int) picture.getDimension().getHeight().getValue(),
                            BufferedImage.TYPE_BYTE_BINARY);

                    WritableRaster raster = (WritableRaster) image.getData();

                    //3 bytes per pixel: red, green, blue
                    byte[] aByteArray = picture.getContent().getValue();
                    DataBuffer buffer = new DataBufferByte(aByteArray, aByteArray.length);

//                    WritableRaster raster = Raster.createInterleavedRaster(buffer, width, height, 3 * width, 3, new int[] {0, 1, 2}, (Point) null);
                    WritableRaster raster = Raster.createInterleavedRaster(buffer, width, height, 2 * width, 2, new int[] {0, 1}, (Point) null);
                    ColorModel cm = new ComponentColorModel(ColorModel.getRGBdefault().getColorSpace(), false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE); 
                    img = new BufferedImage(cm, raster, true, null);
                    File outputfile = new File("image.png");
                    ImageIO.write(img, "png", outputfile);
                } catch (IOException ex) {
                    Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE, null, ex);
                }
  */                  

            } catch (MALException ex) {
                Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }





                } catch (FileNotFoundException ex) {
                    Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MALException ex) {
                    Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }

            
/*            
            BufferedImage img = null;

            try {
                ByteArrayInputStream byteArrayIS = new ByteArrayInputStream(picture.getContent().getValue());

                try {
                    BufferedImage image = new BufferedImage((int) picture.getDimension().getWidth().getValue(),
                            (int) picture.getDimension().getHeight().getValue(),
                            BufferedImage.TYPE_BYTE_BINARY);

                    WritableRaster raster = (WritableRaster) image.getData();

                    
                    raster.setPixels(0, 0, width, height, picture.getContent().getValue());

                    File outputfile = new File("saved.png");
                    ImageIO.write(image, "png", outputfile);
                    
                    /*
                   img = ImageIO.read(byteArrayIS);
                    File outputfile = new File("image.jpg");
                    ImageIO.write(img, "bmp", outputfile);
                } catch (IOException ex) {
                    Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (MALException ex) {
                Logger.getLogger(MCSnapNMFAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
                     */

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
