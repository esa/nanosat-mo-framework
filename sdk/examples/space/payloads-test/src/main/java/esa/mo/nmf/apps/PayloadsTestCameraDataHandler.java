/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package esa.mo.nmf.apps;

import esa.mo.nmf.NMFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.platform.camera.consumer.CameraAdapter;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormat;

/**
 *
 * @author dmars
 */
public class PayloadsTestCameraDataHandler extends CameraAdapter {
  
  private static final Logger LOGGER = Logger.getLogger(
          PayloadsTestCameraDataHandler.class.getName());
  private final int STAGE_ACK = 1;
  private final int STAGE_RSP = 2;
  private final Long actionInstanceObjId;
  private final PayloadsTestMCAdapter mcAdapter;

  PayloadsTestCameraDataHandler(Long actionInstanceObjId,
          final PayloadsTestMCAdapter mcAdapter) {
    this.mcAdapter = mcAdapter;
    this.actionInstanceObjId = actionInstanceObjId;
  }

  @Override
  public void takePictureAckReceived(
          org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
          java.util.Map qosProperties) {
    try {
      mcAdapter.nmf.reportActionExecutionProgress(true, 0, STAGE_ACK,
              PayloadsTestActionsHandler.TOTAL_STAGES, actionInstanceObjId);
    } catch (NMFException ex) {
      LOGGER.log(Level.SEVERE,
              "The action progress could not be reported!", ex);
    }
  }

  @Override
  public void takePictureResponseReceived(
          org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
          org.ccsds.moims.mo.platform.camera.structures.Picture picture,
          java.util.Map qosProperties) {
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
  public void takePictureAckErrorReceived(
          org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
          org.ccsds.moims.mo.mal.MALStandardError error,
          java.util.Map qosProperties) {
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
  public void takePictureResponseErrorReceived(
          org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
          org.ccsds.moims.mo.mal.MALStandardError error,
          java.util.Map qosProperties) {
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
  
}
