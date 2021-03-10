// (C) 2021 European Space Agency
// European Space Operations Centre
// Darmstadt, Germany

package esa.mo.nmf.log_browser.adapters;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.softwaremanagement.commandexecutor.CommandExecutorHelper;
import esa.mo.com.impl.util.ArchiveCOMObjectsOutput;

/**
 * Archive adapter that dumps to a LOG file StandardOutput and StandardError events body of the
 * CommandExecutor service of the SoftwareManagement received from an archive query.
 *
 * @author Tanguy Soto
 */
public class ArchiveToLogAdapter extends ArchiveAdapter implements QueryStatusProvider {

  private static final Logger LOGGER = Logger.getLogger(ArchiveToLogAdapter.class.getName());

  /**
   * Destination LOG file
   */
  private FileWriter logFile;

  /**
   * True if the query is over (response or any error received)
   */
  private boolean isQueryOver = false;

  /**
   * SoftwareManagement.CommandExecutor.StandardOutput object type
   */
  private ObjectType stdOutputType = CommandExecutorHelper.STANDARDOUTPUT_OBJECT_TYPE;

  /**
   * SoftwareManagement.CommandExecutor.StandardError object type
   */
  private ObjectType stdErrorType = CommandExecutorHelper.STANDARDERROR_OBJECT_TYPE;


  /**
   * Creates a new instance of ArchiveToLogAdapter.
   * 
   * @param logFilePath Path of destination LOG file where we dump the String objects
   */
  public ArchiveToLogAdapter(String logFilePath) {
    openLogFile(logFilePath);
  }

  /**
   * Dumps an archive objects output received from an archive query answer (update or response).
   *
   * @param archiveObjectOutput the archive objects outputs
   */
  private synchronized void dumpArchiveObjectsOutput(ArchiveCOMObjectsOutput archiveObjectOutput) {
    if (logFile == null) {
      LOGGER.log(Level.SEVERE, "Can't dump MAL elements to LOG file: file is null");
      return;
    }

    // empty comType means query returned nothing
    ObjectType comType = archiveObjectOutput.getObjectType();
    if (comType == null) {
      return;
    }

    // only expected types
    if (!(comType.equals(stdOutputType) || comType.equals(stdErrorType))) {
      return;
    }

    // if somehow we have no object bodies, stop
    if (archiveObjectOutput.getObjectBodies() == null) {
      return;
    }

    // write LOG message, we can safely cast to String
    for (Object malObject : archiveObjectOutput.getObjectBodies()) {
      String logObject = (String) malObject;
      try {
        logFile.write(logObject);
      } catch (IOException e) {
        LOGGER.log(Level.SEVERE, "Error writting LOG object", e);
      }
    }
  }

  /**
   * Opens the LOG file.
   * 
   * @param Path to the LOG file to open/create
   */
  private void openLogFile(String logFilePath) {
    try {
      logFile = new FileWriter(logFilePath);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, String.format("Error opening the LOG file", logFilePath), e);
      logFile = null;
    }
  }

  /**
   * Safely closes the LOG file.
   */
  private synchronized void closeLogFile() {
    if (logFile != null) {
      try {
        logFile.close();
      } catch (IOException e) {
        LOGGER.log(Level.SEVERE, "Error closing the LOG file", e);
      }
    }
  }

  @Override
  public void queryResponseReceived(MALMessageHeader msgHeader, ObjectType objType,
      IdentifierList domain, ArchiveDetailsList objDetails, ElementList objBodies,
      Map qosProperties) {
    dumpArchiveObjectsOutput(new ArchiveCOMObjectsOutput(domain, objType, objDetails, objBodies));
    setIsQueryOver(true);
  }

  @Override
  public void queryUpdateReceived(MALMessageHeader msgHeader, ObjectType objType,
      IdentifierList domain, ArchiveDetailsList objDetails, ElementList objBodies,
      Map qosProperties) {
    dumpArchiveObjectsOutput(new ArchiveCOMObjectsOutput(domain, objType, objDetails, objBodies));
  }

  @Override
  public void queryAckErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
      Map qosProperties) {
    LOGGER.log(Level.SEVERE, "queryAckErrorReceived", error);
    setIsQueryOver(true);
  }

  @Override
  public void queryUpdateErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
      Map qosProperties) {
    LOGGER.log(Level.SEVERE, "queryUpdateErrorReceived", error);
    setIsQueryOver(true);
  }

  @Override
  public void queryResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
      Map qosProperties) {
    LOGGER.log(Level.SEVERE, "queryResponseErrorReceived", error);
    setIsQueryOver(true);
  }

  /** {@inheritDoc} */
  @Override
  public synchronized boolean isQueryOver() {
    return isQueryOver;
  }

  private synchronized void setIsQueryOver(boolean isQueryOver) {
    if (isQueryOver) {
      // once response or error is received, we close the LOG file
      closeLogFile();
    }
    this.isQueryOver = isQueryOver;
  }
}
