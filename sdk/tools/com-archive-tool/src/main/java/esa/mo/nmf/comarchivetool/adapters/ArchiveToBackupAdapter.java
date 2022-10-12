//------------------------------------------------------------------------------
//
// System : ccsds-common
//
// Sub-System : esa.mo.nmf.comarchivetool.adapters
//
// File Name : ArchiveToBackupAdapter.java
//
// Author : marcel.mikolajko
//
// Creation Date : 05.10.2022
//
//------------------------------------------------------------------------------
package esa.mo.nmf.comarchivetool.adapters;

import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.com.impl.util.ArchiveCOMObjectsOutput;
import esa.mo.nmf.comarchivetool.ArchiveBrowserHelper;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author marcel.mikolajko
 */
public class ArchiveToBackupAdapter extends ArchiveAdapter implements QueryStatusProvider {

  private static final Logger LOGGER = Logger.getLogger(ArchiveToJsonAdapter.class.getName());


  /**
   * Path to the destination database file
   */
  private final String filename;

  /**
   * List of objects to save
   */
  List<ArchiveCOMObjectsOutput> objectsToProcess = new ArrayList<>();

  /**
   * True if the query is over (response or any error received)
   */
  private boolean isQueryOver = false;

  ArchiveProviderServiceImpl archive;

  /**
   * Creates a new instance of ToBackupArchiveAdapter.
   *
   * @param filename Path of destination database file
   * */
  public ArchiveToBackupAdapter(String filename, String domain) {
    if (filename != null) {
      if (filename.endsWith(".db")) {
        this.filename= filename;
      } else {
        this.filename = filename + ".db";
      }
    } else {
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy__HH-mm-ss");
      LocalDateTime now = LocalDateTime.now();
      this.filename = domain + "__" + dtf.format(now) + ".db";
    }
  }

  public boolean saveDataToNewDatabase() {
    File dbFile = new File(this.filename);
    if (!dbFile.exists()) {
        try {
            dbFile.createNewFile();
        } catch (IOException e) {
          LOGGER.log(Level.SEVERE, "Failed to create new database file", e);
          return false;
        }
    }
    System.out.println("\nSaving data to " + this.filename + " started.\n");
    this.archive = ArchiveBrowserHelper.spawnLocalArchiveProvider(this.filename);

    boolean result = true;
    for(ArchiveCOMObjectsOutput objects : objectsToProcess) {
      try {
        this.archive.store(false, objects.getObjectType(), objects.getDomain(), objects.getArchiveDetailsList(), objects.getObjectBodies(), null);
      } catch (MALException | MALInteractionException e) {
        LOGGER.log(Level.SEVERE, "Failed to store objects of type: " + objects.getObjectType(), e);
        result = false;
      }
    }

    System.out.println("\nSaving finished.\n");
    return result;
  }

  public void closeArchiveProvider() {
    this.archive.close();
  }
  /**
   * Dumps an archive objects output received from an archive query answer (update or response).
   *
   * @param archiveObjectOutput the archive objects outputs
   */
  private synchronized void dumpArchiveObjectsOutput(ArchiveCOMObjectsOutput archiveObjectOutput) {
    // empty comType means query returned nothing
    ObjectType comType = archiveObjectOutput.getObjectType();
    if (comType == null) {
      return;
    }
    objectsToProcess.add(archiveObjectOutput);
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
      // once response or error is received, we dump current content to new database file
//      saveDataToNewDatabase();
    }
    this.isQueryOver = isQueryOver;
  }

  public List<ArchiveCOMObjectsOutput> getObjectsToProcess() {
    return objectsToProcess;
  }
}
//------------------------------------------------------------------------------