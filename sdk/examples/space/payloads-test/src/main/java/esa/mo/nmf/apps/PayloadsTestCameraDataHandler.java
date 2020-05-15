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
package esa.mo.nmf.apps;

import esa.mo.nmf.NMFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.platform.camera.consumer.CameraAdapter;
import org.ccsds.moims.mo.platform.camera.structures.Picture;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormat;

/**
 *
 * @author dmars
 */
public class PayloadsTestCameraDataHandler extends CameraAdapter
{

  private static final Logger LOGGER = Logger.getLogger(
      PayloadsTestCameraDataHandler.class.getName());
  private final int STAGE_ACK = 1;
  private final int STAGE_RSP = 2;
  private final Long actionInstanceObjId;
  private final PayloadsTestMCAdapter mcAdapter;

  PayloadsTestCameraDataHandler(Long actionInstanceObjId,
      final PayloadsTestMCAdapter mcAdapter)
  {
    this.mcAdapter = mcAdapter;
    this.actionInstanceObjId = actionInstanceObjId;
  }

  @Override
  public void takePictureAckReceived(
      org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
      java.util.Map qosProperties)
  {
    try {
      mcAdapter.nmf.reportActionExecutionProgress(true, 0, STAGE_ACK,
          PayloadsTestActionsHandler.TOTAL_STAGES, actionInstanceObjId);
    } catch (NMFException ex) {
      LOGGER.log(Level.SEVERE,
          "The action progress could not be reported!", ex);
    }
  }

  @Override
  public void takeAutoExposedPictureAckReceived(MALMessageHeader msgHeader, Map qosProperties)
  {
    takePictureAckReceived(msgHeader, qosProperties);
  }

  @Override
  public void takePictureResponseReceived(
      org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
      org.ccsds.moims.mo.platform.camera.structures.Picture picture,
      java.util.Map qosProperties)
  {
    LOGGER.log(Level.INFO, "Image has been recived");
    // The picture was received!
    mcAdapter.picturesTaken.incrementAndGet();
    try {
      mcAdapter.nmf.reportActionExecutionProgress(true, 0, STAGE_RSP,
          PayloadsTestActionsHandler.TOTAL_STAGES, actionInstanceObjId);
    } catch (NMFException ex) {
      LOGGER.log(Level.SEVERE,
          "The action progress could not be reported!", ex);
    }
    final String folder = "snaps";
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
        LOGGER.log(Level.INFO, "Image saved to {0}myPicture.raw", filenamePrefix);
      } else if (picture.getSettings().getFormat().equals(PictureFormat.PNG)) {
        FileOutputStream fos = new FileOutputStream(filenamePrefix + "myPicture.png");
        fos.write(picture.getContent().getValue());
        fos.flush();
        fos.close();
        LOGGER.log(Level.INFO, "Image saved to {0}myPicture.png", filenamePrefix);
      } else if (picture.getSettings().getFormat().equals(PictureFormat.BMP)) {
        FileOutputStream fos = new FileOutputStream(filenamePrefix + "myPicture.bmp");
        fos.write(picture.getContent().getValue());
        fos.flush();
        fos.close();
        LOGGER.log(Level.INFO, "Image saved to {0}myPicture.bmp", filenamePrefix);
      } else if (picture.getSettings().getFormat().equals(PictureFormat.JPG)) {
        FileOutputStream fos = new FileOutputStream(filenamePrefix + "myPicture.jpg");
        fos.write(picture.getContent().getValue());
        fos.flush();
        fos.close();
        LOGGER.log(Level.INFO, "Image saved to {0}myPicture.jpg", filenamePrefix);
      }
    } catch (IOException | MALException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
    }
    try {
      // Stored
      mcAdapter.nmf.reportActionExecutionProgress(true, 0, 3,
          PayloadsTestActionsHandler.TOTAL_STAGES, actionInstanceObjId);
    } catch (NMFException ex) {
      LOGGER.log(Level.SEVERE,
          "The action progress could not be reported!", ex);
    }
  }

  @Override
  public void takeAutoExposedPictureResponseReceived(MALMessageHeader msgHeader, Picture picture,
      Map qosProperties)
  {
    takePictureResponseReceived(msgHeader, picture, qosProperties);
  }

  @Override
  public void takePictureAckErrorReceived(
      org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
      org.ccsds.moims.mo.mal.MALStandardError error,
      java.util.Map qosProperties)
  {
    try {
      mcAdapter.nmf.reportActionExecutionProgress(false, 1, STAGE_ACK,
          PayloadsTestActionsHandler.TOTAL_STAGES, actionInstanceObjId);
      LOGGER.log(Level.WARNING,
          "takePicture ack error received {0}", error.toString());
    } catch (NMFException ex) {
      LOGGER.log(Level.SEVERE,
          "takePicture ack error " + error.toString() + " could not be reported!",
          ex);
    }
  }

  @Override
  public void takeAutoExposedPictureAckErrorReceived(MALMessageHeader msgHeader,
      MALStandardError error, Map qosProperties)
  {
    takePictureAckErrorReceived(msgHeader, error, qosProperties);
  }


  @Override
  public void takePictureResponseErrorReceived(
      org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
      org.ccsds.moims.mo.mal.MALStandardError error,
      java.util.Map qosProperties)
  {
    try {
      mcAdapter.nmf.reportActionExecutionProgress(false, 1, STAGE_RSP,
          PayloadsTestActionsHandler.TOTAL_STAGES, actionInstanceObjId);
      LOGGER.log(Level.WARNING,
          "takePicture response error received {0}", error.toString());
    } catch (NMFException ex) {
      LOGGER.log(Level.SEVERE,
          "takePicture response error " + error.toString() + " could not be reported!",
          ex);
    }
  }

  @Override
  public void takeAutoExposedPictureResponseErrorReceived(MALMessageHeader msgHeader,
      MALStandardError error, Map qosProperties)
  {
    takePictureResponseErrorReceived(msgHeader, error, qosProperties); //To change body of generated methods, choose Tools | Templates.
  }
}
