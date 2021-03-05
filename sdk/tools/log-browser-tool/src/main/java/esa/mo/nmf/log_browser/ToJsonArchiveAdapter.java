// (C) 2021 European Space Agency
// European Space Operations Centre
// Darmstadt, Germany

package esa.mo.nmf.log_browser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import esa.mo.com.impl.util.ArchiveCOMObjectsOutput;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.helpertools.helpers.HelperTime;

/**
 * Archive adapter that dumps to a JSON file every COM object received from an archive query.
 *
 * @author Tanguy Soto
 */
public class ToJsonArchiveAdapter extends ArchiveAdapter {

  private static final Logger LOGGER = Logger.getLogger(ToJsonArchiveAdapter.class.getName());

  /**
   * Google JSON Java parser
   */
  private Gson gson;

  /**
   * Destination JSON file
   */
  private FileWriter jsonFile;

  /**
   * Archived objects grouped by domain and COM object type
   */
  private HashMap<String, HashMap<String, ArrayList<CleanCOMArchiveObject>>> archiveObjects;

  /**
   * True if the query is over (response or any error received)
   */
  private boolean isQueryOver = false;


  /**
   * Creates a new instance of ToJsonArchiveAdapter.
   * 
   * @param jsonFilePath Path of destination JSON file where we dump the MAL elements
   */
  public ToJsonArchiveAdapter(String jsonFilePath) {
    gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    archiveObjects = new HashMap<String, HashMap<String, ArrayList<CleanCOMArchiveObject>>>();
    openJsonFile(jsonFilePath);
  }

  /**
   * Dumps an archive objects output received from an archive query answer (update or response).
   *
   * @param archiveObjectOutput the archive objects outputs
   */
  private synchronized void dumpArchiveObjectsOutput(ArchiveCOMObjectsOutput archiveObjectOutput) {
    if (jsonFile == null) {
      LOGGER.log(Level.SEVERE, "Can't dump MAL elements to JSON file: file is null");
      return;
    }

    // empty comType means query returned nothing
    ObjectType comType = archiveObjectOutput.getObjectType();
    if (comType == null) {
      return;
    }

    // Group objects by domain ...
    String domainKey = HelperMisc.domain2domainId(archiveObjectOutput.getDomain());
    if (!archiveObjects.containsKey(domainKey)) {
      archiveObjects.put(domainKey, new HashMap<String, ArrayList<CleanCOMArchiveObject>>());
    }

    // ... and by COM object type
    String comTypeKey = HelperCOM.objType2string(comType).replace(" - ", ".").replace(": ", ".");
    if (!archiveObjects.get(domainKey).containsKey(comTypeKey)) {
      archiveObjects.get(domainKey).put(comTypeKey, new ArrayList<CleanCOMArchiveObject>());
    }

    for (int i = 0; i < archiveObjectOutput.getArchiveDetailsList().size(); i++) {
      Object malObject = archiveObjectOutput.getObjectBodies() == null ? null
          : archiveObjectOutput.getObjectBodies().get(i);
      CleanCOMArchiveObject comObject = new CleanCOMArchiveObject(comType,
          archiveObjectOutput.getArchiveDetailsList().get(i), malObject);
      archiveObjects.get(domainKey).get(comTypeKey).add(comObject);
    }
  }

  /**
   * Opens the JSON file.
   */
  private void openJsonFile(String jsonFilePath) {
    try {
      jsonFile = new FileWriter(jsonFilePath);
      LOGGER.log(Level.INFO, String.format("Opened JSON file %s", jsonFilePath));
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Error opening the JSON file", e);
      jsonFile = null;
    }
  }

  /**
   * Safely closes the JSON file.
   */
  private synchronized void dumpAndcloseJsonFile() {
    gson.toJson(archiveObjects, jsonFile);

    // close the JSON file
    if (jsonFile != null) {
      try {
        jsonFile.close();
        LOGGER.log(Level.INFO, "Closed JSON file");
      } catch (IOException e) {
        LOGGER.log(Level.SEVERE, "Error closing the JSON file", e);
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

  /**
   * @return True if the query is over (response or any error received).
   */
  public synchronized boolean isQueryOver() {
    return isQueryOver;
  }

  private synchronized void setIsQueryOver(boolean isQueryOver) {
    if (isQueryOver) {
      // once response or error is received, we dump current content and close the JSON file
      dumpAndcloseJsonFile();
    }
    this.isQueryOver = isQueryOver;
  }

  /**
   * COM Archive object holder class. It has the proper attributes to obtain a clean format when
   * exporting with GSON.
   *
   * @author Tanguy Soto
   */
  private class CleanCOMArchiveObject {
    /**
     * ArchiveDetails content
     */
    private Long instanceId;
    private CleanObjectDetails objectDetails;
    private String networkZone;
    private String creationTime;
    private String providerURI;

    /**
     * MAL object paired to its MAL type name.
     */
    private HashMap<String, Object> object;

    public CleanCOMArchiveObject(ObjectType objectType, ArchiveDetails archiveDetails,
        Object object) {
      // archive details
      instanceId = archiveDetails.getInstId();
      objectDetails = archiveDetails.getDetails() == null ? null
          : new CleanObjectDetails(archiveDetails.getDetails());
      networkZone = archiveDetails.getNetwork().getValue();
      creationTime = HelperTime.time2readableString(archiveDetails.getTimestamp());
      providerURI = archiveDetails.getProvider().getValue();

      // pair MAL object ot its MAL type name
      this.object = new HashMap<String, Object>();
      if (object != null) {
        this.object.put(object.getClass().getName(), object);
      }
    }

    private class CleanObjectDetails {
      private Long relatedInstanceId;
      private CleanObjectId source;

      public CleanObjectDetails(ObjectDetails objectDetails) {
        relatedInstanceId = objectDetails.getRelated();
        source =
            objectDetails.getSource() == null ? null : new CleanObjectId(objectDetails.getSource());
      }

      private class CleanObjectId {
        String objectType;
        String domain;
        Long instanceId;

        public CleanObjectId(ObjectId objectId) {
          objectType =
              HelperCOM.objType2string(objectId.getType()).replace(" - ", ".").replace(": ", ".");
          domain = HelperMisc.domain2domainId(objectId.getKey().getDomain());
          instanceId = objectId.getKey().getInstId();
        }
      }
    }
  }
}
