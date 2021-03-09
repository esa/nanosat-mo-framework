// (C) 2021 European Space Agency
// European Space Operations Centre
// Darmstadt, Germany

package esa.mo.nmf.log_browser.adapters;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.structures.AppDetails;
import esa.mo.com.impl.util.ArchiveCOMObjectsOutput;

/**
 * Archive adapter that finds by name an App object of the CommandExecutor service of the
 * SoftwareManagement from an archive query of all App objects.
 *
 * @author Tanguy Soto
 */
public class ArchiveToAppAdapter extends ArchiveAdapter implements QueryStatusProvider {

  private static final Logger LOGGER = Logger.getLogger(ArchiveToAppAdapter.class.getName());

  /**
   * Name of the App to find.
   */
  private String appName;

  /**
   * ObjectId of the found App or null if not found after the query ended.
   */
  private ObjectId appObjectId;

  /**
   * True if the query is over (response or any error received)
   */
  private boolean isQueryOver = false;

  /**
   * SoftwareManagement.AppsLaunch.App object type
   */
  private ObjectType appType =
      new ObjectType(new UShort(7), new UShort(5), new UOctet((short) 1), new UShort(1));


  /**
   * Creates a new instance of ArchiveToAppAdapter.
   * 
   * @param appName
   */
  public ArchiveToAppAdapter(String appName) {
    this.appName = appName;
  }

  /**
   * Processes archive objects output received from an archive query answer (update or response).
   *
   * @param archiveObjectOutput the archive objects outputs
   */
  private synchronized void ProcessArchiveObjectsOutput(
      ArchiveCOMObjectsOutput archiveObjectOutput) {
    // empty comType means query returned nothing
    ObjectType comType = archiveObjectOutput.getObjectType();
    if (comType == null) {
      return;
    }

    // process only app type
    if (!comType.equals(appType)) {
      return;
    }

    // if somehow we have no object bodies, stop
    if (archiveObjectOutput.getObjectBodies() == null) {
      return;
    }

    // look for the App by name
    for (int i = 0; i < archiveObjectOutput.getObjectBodies().size(); i++) {
      AppDetails appObject = (AppDetails) archiveObjectOutput.getObjectBodies().get(i);
      String appName = appObject.getName().getValue();
      Long appInstanceId = archiveObjectOutput.getArchiveDetailsList().get(i).getInstId();
      IdentifierList appDomain = archiveObjectOutput.getDomain();

      if (this.appName.equals(appName)) {
        appObjectId = new ObjectId(appType, new ObjectKey(appDomain, appInstanceId));
        setIsQueryOver(true);
      }
    }
  }

  @Override
  public void queryResponseReceived(MALMessageHeader msgHeader, ObjectType objType,
      IdentifierList domain, ArchiveDetailsList objDetails, ElementList objBodies,
      Map qosProperties) {
    ProcessArchiveObjectsOutput(
        new ArchiveCOMObjectsOutput(domain, objType, objDetails, objBodies));
    setIsQueryOver(true);
  }

  @Override
  public void queryUpdateReceived(MALMessageHeader msgHeader, ObjectType objType,
      IdentifierList domain, ArchiveDetailsList objDetails, ElementList objBodies,
      Map qosProperties) {
    ProcessArchiveObjectsOutput(
        new ArchiveCOMObjectsOutput(domain, objType, objDetails, objBodies));
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

  /**
   * Returns the ObjectId of the found App or null if not found after the query ended.
   * 
   * @return the ObjectId
   */
  public ObjectId getAppObjectId() {
    return appObjectId;
  }

  /** {@inheritDoc} */
  @Override
  public synchronized boolean isQueryOver() {
    return isQueryOver;
  }

  private synchronized void setIsQueryOver(boolean isQueryOver) {
    this.isQueryOver = isQueryOver;
  }
}
