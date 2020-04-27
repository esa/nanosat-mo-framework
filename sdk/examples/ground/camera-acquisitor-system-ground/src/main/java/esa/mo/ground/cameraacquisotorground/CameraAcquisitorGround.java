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
package esa.mo.ground.cameraacquisotorground;

import esa.mo.com.impl.util.EventCOMObject;
import esa.mo.com.impl.util.EventReceivedListener;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.ground.restservice.GroundTrack;
import esa.mo.ground.restservice.Pass;
import esa.mo.ground.restservice.PositionAndTime;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.mc.impl.provider.ParameterInstance;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.apps.CameraAcquisitorSystemCameraTargetHandler;
import esa.mo.nmf.apps.CameraAcquisitorSystemMCAdapter;
import esa.mo.nmf.groundmoadapter.CompleteDataReceivedListener;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import esa.mo.nmf.sdk.OrekitResources;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import org.ccsds.moims.mo.com.activitytracking.ActivityTrackingHelper;
import org.ccsds.moims.mo.com.activitytracking.structures.ActivityAcceptance;
import org.ccsds.moims.mo.com.activitytracking.structures.ActivityExecution;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.action.structures.ActionInstanceDetails;
import org.ccsds.moims.mo.mc.alert.structures.AlertEventDetails;
import org.ccsds.moims.mo.mc.structures.AttributeValue;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePairList;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormat;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.data.DataProvidersManager;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Set and Command demo application. This demo should be used with the Hello World demo provider.
 * Camera Acquisition System ground application. Handles communication between user front-end and
 * space application as well as all computationally expensive calculations.
 */
@RestController
@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class CameraAcquisitorGround
{

  private static final Logger LOGGER = Logger.getLogger(CameraAcquisitorGround.class.getName());

  private GroundMOAdapterImpl gma;
  private final OrbitHandler orbitHandler;


  private final long MINUTE_IN_SECONDS = 60;
  private final long HOUR_IN_SECONDS = MINUTE_IN_SECONDS * 60;
  private final long DAY_IN_SECONDS = HOUR_IN_SECONDS * 24;
  private final long FIVE_DAYS_IN_SECONDS = DAY_IN_SECONDS * 5;
  private final long YEAR_IN_SECONDS = DAY_IN_SECONDS * 356;

  private final long DEFAULT_WORST_CASE_ROTATION_TIME_MS = 20000;
  private final long DEFAULT_WORST_CASE_ROTATION_TIME_SEC =
      DEFAULT_WORST_CASE_ROTATION_TIME_MS / 1000;

  private final long MAX_SIM_RANGE = YEAR_IN_SECONDS;
  private final long DEFAULT_GROUND_TRACK_DURATION = DAY_IN_SECONDS;
  private final long DEFAULT_STEPSIZE = MINUTE_IN_SECONDS / 2;
  private final double DEFAULT_MAX_ANGLE = 45.0;

  private final TreeSet<AbsoluteDate> schedule = new TreeSet<>();
  private final HashMap<Long, ActionReport[]> activeActions = new HashMap<>();

  // cached values
  private PositionAndTime[] cachedTrack = new PositionAndTime[0];
  private AtomicLong counter = new AtomicLong(0);
  private final int NUM_TRIES = 5; // number of timeslots that will maximaly be calculated.

  private TLE cachedTLE;
  private Instant lastTLEUpdate;
  //
  @PostConstruct
  private void start()
  {
    System.out.println("----------------------------- STARTUP --------------------------------");
  }

  private class Parameter
  {

    private static final String GAIN_RED = "GainRed";
    private static final String GAIN_GREEN = "GainGreen";
    private static final String GAIN_BLUE = "GainBlue";
    private static final String EXPOSURE_TYPE = "ExposureType";
    private static final String CUSTOM_EXPOSURE_TIME = "CustomExposureTime";
    private static final String WORST_CASE_ROTATION_TIME_MS = "WorstCaseRotationTimeMS";
    private static final String ATTITUDE_SAFTY_MARGIN_MS = "AttitudeSaftyMarginMS";
    private static final String MAX_RETRYS = "MaxRetrys";
    private static final String PICTURE_WIDTH = "PictureWidth";
    private static final String PICTURE_HEIGHT = "PictureHeight";
    private static final String PICTURE_TYPE = "PictureType";
  }

  /**
   * Inner class for storing action progress
   */
  private class ActionReport
  {

    public int stage;
    public boolean success;
    public String error;

    public ActionReport(int stage, boolean success, String error)
    {
      this.stage = stage;
      this.success = success;
      this.error = error;
    }

    @Override
    public String toString()
    {
      return "Stage: " + stage + ", success: " + success + (success ? "" : ", ERROR: " + error);
    }

  }

  GeodeticPoint esoc = new GeodeticPoint(49.869987, 8.622770, 0);

  public CameraAcquisitorGround(ApplicationArguments args)
  {

    System.out.println("------------------------- parsing arguments --------------------------");

    URI directoryURI = new URI(args.getSourceArgs()[0]);
    System.out.println("directoryURI = " + directoryURI);

    System.out.println("--------------------------- initialisation ----------------------------");
    this.lastTLEUpdate = Instant.MIN;

    //setup orekit
    System.out.println("Setup Orekit");
    DataProvidersManager manager = DataProvidersManager.getInstance();
    manager.addProvider(OrekitResources.getOrekitData());

    System.out.println("Setup Providers");
    ProviderSummaryList providers;
    try {
      System.out.println("retrive providers");
      providers =
          GroundMOAdapterImpl.retrieveProvidersFromDirectory(
              directoryURI);
      System.out.println("retrive providers fin");
      GroundMOAdapterImpl gmaTMP = null;
      boolean providerFound = false;
      for (ProviderSummary provider : providers) {
        System.out.println("name : " + provider.getProviderName().getValue());
        if (provider.getProviderName().getValue().equals("App: camera-acquisitor-system")) {
          gmaTMP = new GroundMOAdapterImpl(provider);
          providerFound = true;
          break;
        }
      }

      gma = gmaTMP;
      //gma.addDataReceivedListener(new CompleteDataReceivedAdapter());

      Subscription subscription = HelperCOM.generateSubscriptionCOMEvent(
          "ActivityTrackingListener",
          ActivityTrackingHelper.EXECUTION_OBJECT_TYPE);
      gma.getCOMServices().getEventService().addEventReceivedListener(subscription,
          new EventReceivedListenerAdapter());

      setInitialParameters();

      // get previous requests
      ArchiveQueryList archiveQueryList = new ArchiveQueryList();
      ArchiveQuery archiveQuery = new ArchiveQuery();

      archiveQuery.setDomain(null);
      archiveQuery.setNetwork(null);
      archiveQuery.setProvider(null);
      archiveQuery.setRelated(new Long(0));
      archiveQuery.setSource(null);
      archiveQuery.setStartTime(null);
      archiveQuery.setEndTime(null);
      archiveQuery.setSortFieldName(null);

      archiveQueryList.add(archiveQuery);

      GetAllArchiveAdapter archiveAdapter = new GetAllArchiveAdapter();

      gma.getCOMServices().getArchiveService().getArchiveStub().query(true, new ObjectType(
          //new UShort(4), new UShort(1), new UOctet((short) 1), new UShort(3)),
          new UShort(0), new UShort(0), new UOctet((short) 0), new UShort(0)),
          archiveQueryList, null, archiveAdapter);

      System.out.println("------------------------------------------------------------------------");

    } catch (MALException ex) {
      Logger.getLogger(CameraAcquisitorGround.class.getName()).log(Level.SEVERE, null, ex);
    } catch (MalformedURLException ex) {
      Logger.getLogger(CameraAcquisitorGround.class.getName()).log(Level.SEVERE, null, ex);
    } catch (MALInteractionException ex) {
      Logger.getLogger(CameraAcquisitorGround.class.getName()).log(Level.SEVERE, null, ex);
    }

    orbitHandler = new OrbitHandler(getTLE());
  }

  public boolean sendCommandShedulePhotograph(double latitude, double longitude, double maxAngle,
      OrbitHandler.TimeModeEnum timeMode)
  {
    Union[] parameters = new Union[]{new Union(latitude), new Union(longitude), new Union(maxAngle),
      new Union(timeMode.ordinal())};

    // synchronized to prevent reciving first reply of action before it was added to list
    synchronized (activeActions) {
      /*Long actionID = gma.invokeAction(
      CameraAcquisitorSystemCameraTargetHandler.ACTION_PHOTOGRAPH_LOCATION,
      parameters);*/
      IdentifierList idList = new IdentifierList();
      idList.add(
          new Identifier(CameraAcquisitorSystemCameraTargetHandler.ACTION_PHOTOGRAPH_LOCATION));
      Long actionID = null;

      try {
        ObjectInstancePairList objIds =
            gma.getMCServices().getActionService().getActionStub().listDefinition(idList);
        if (objIds == null) {
          LOGGER.log(Level.SEVERE,
              "Action does not exist, please check if space application is running");
          return false;
        }
        AttributeValueList arguments = new AttributeValueList();
        arguments.add(new AttributeValue((Attribute) HelperAttributes.javaType2Attribute(latitude)));
        arguments.add(new AttributeValue((Attribute) HelperAttributes.javaType2Attribute(longitude)));
        arguments.add(new AttributeValue((Attribute) HelperAttributes.javaType2Attribute(maxAngle)));
        arguments.add(new AttributeValue((Attribute) HelperAttributes.javaType2Attribute(
            timeMode.ordinal())));
        actionID = gma.invokeAction(objIds.get(0).getObjDefInstanceId(), arguments);
      } catch (MALInteractionException | MALException | NMFException ex) {
        Logger.getLogger(CameraAcquisitorGround.class.getName()).log(Level.SEVERE, null, ex);
      }
      if (actionID == null) {
        LOGGER.log(Level.SEVERE, "Action ID == null!");
        return false;
      } else {
        LOGGER.log(Level.INFO, "new Action: {0}", actionID);
        activeActions.put(actionID,
            new ActionReport[CameraAcquisitorSystemCameraTargetHandler.PHOTOGRAPH_LOCATION_STAGES]);
        return true;
      }
    }
  }

  private void drawOrbit()
  {

    int width = 1920;
    int height = 1080;
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics g = image.createGraphics();

    // draw background
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, width, height);

    AbsoluteDate now = new AbsoluteDate(
        Date.from(OffsetDateTime.now(ZoneOffset.UTC).toInstant()),
        TimeScalesFactory.getUTC());

    long shiftTime = (long) (60 * 60 * 1.57) * 4; // aproximatly one orbit

    PositionAndTime[] series =
        orbitHandler.getGroundTrack(now, now.shiftedBy(shiftTime), 10);
    GeodeticPoint currentPos = orbitHandler.getPosition(now);

    // draw path segments
    g.setColor(Color.BLACK);
    drawSeries(series, height, width, g);

    g.setColor(Color.GREEN);
    g.fillOval(
        imageCoordianteX(currentPos, width) - 10,
        imageCoordianteY(currentPos, height) - 10,
        20, 20);

    g.setColor(Color.BLUE);
    g.fillOval(
        imageCoordianteX(esoc, width) - 10,
        imageCoordianteY(esoc, height) - 10,
        20, 20);

    // save image
    File outputfile = new File(System.getProperty("user.home") + "/orbit.png");
    try {
      ImageIO.write(image, "png", outputfile);
      System.out.println("image saved");
    } catch (IOException ex) {
      Logger.getLogger(CameraAcquisitorGround.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  @GetMapping("/getActionStatus")
  public ActionReport[] getActionStatus(@RequestParam(value = "actionID") long actionID)
  {
    return activeActions.get(actionID);
  }

  @PostMapping("/schedulePhotographPosition")
  public Long schedulePhotographPosition(
      @RequestParam(value = "longitude") double longitude,
      @RequestParam(value = "latitude") double latitude,
      @RequestParam(value = "timeStemp") String timeStemp)
  {
    System.out.println("schedulePhotographPosition");
    System.out.println("longitude:" + longitude);
    System.out.println("latitude:" + latitude);
    System.out.println("time:" + timeStemp);

    Union[] parameters =
        new Union[]{new Union(latitude), new Union(longitude), new Union(timeStemp)};

    AbsoluteDate scheduleDate = new AbsoluteDate(timeStemp, TimeScalesFactory.getUTC());

    if (checkTimeSlot(scheduleDate)) {
      try {
        schedule.add(scheduleDate);

        IdentifierList idList = new IdentifierList();
        idList.add(
            new Identifier(
                CameraAcquisitorSystemCameraTargetHandler.ACTION_PHOTOGRAPH_LOCATION));

        ObjectInstancePairList objIds =
            gma.getMCServices().getActionService().getActionStub().listDefinition(idList);
        if (objIds == null) {
          LOGGER.log(Level.SEVERE,
              "Action does not exist, please check if space application is running");
        }
        AttributeValueList arguments = new AttributeValueList();
        arguments.add(new AttributeValue((Attribute) HelperAttributes.javaType2Attribute(latitude)));
        arguments.add(new AttributeValue((Attribute) HelperAttributes.javaType2Attribute(longitude)));
        arguments.add(new AttributeValue((Attribute) HelperAttributes.javaType2Attribute(timeStemp)));

        Long actionID = gma.invokeAction(objIds.get(0).getObjDefInstanceId(), arguments);
        if (actionID == null) {
          LOGGER.log(Level.SEVERE, "Action ID == null!");
        } else {
          LOGGER.log(Level.INFO, "new Action: {0}", actionID);
          activeActions.put(actionID,
              new ActionReport[CameraAcquisitorSystemCameraTargetHandler.PHOTOGRAPH_LOCATION_STAGES]);
        }
        return actionID;

      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, e.getMessage());
      }
    }
    LOGGER.log(Level.INFO, "Timeslot not awailable!");
    return null;
  }

  @GetMapping("/photographTime")
  public LinkedList<String> getTimeOfPhotograph(
      @RequestParam(value = "longitude") double longitude,
      @RequestParam(value = "latitude") double latitude,
      @RequestParam(value = "maxAngle", defaultValue = "" + DEFAULT_MAX_ANGLE) double maxAngle,
      @RequestParam(value = "timeMode", defaultValue = "ANY") OrbitHandler.TimeModeEnum timeMode)
  {

    System.out.println("longitude:" + longitude);
    System.out.println("latitude:" + latitude);
    System.out.println("maxAngle: " + maxAngle);
    System.out.println("timeMode: " + timeMode.ordinal());
    orbitHandler.reset();
    AbsoluteDate simTime = CameraAcquisitorSystemMCAdapter.getNow();
    AbsoluteDate simEnd = simTime.shiftedBy(MAX_SIM_RANGE);

    LinkedList<String> results = new LinkedList();
    while (simTime.compareTo(simEnd) < 0 && results.size() <= NUM_TRIES) {

      Pass pass = orbitHandler.getPassTime(
          longitude, latitude,
          maxAngle, timeMode,
          simTime,
          DEFAULT_WORST_CASE_ROTATION_TIME_SEC,
          MAX_SIM_RANGE);
      simTime = pass.getOptimalTime();
      if (simTime != null && checkTimeSlot(simTime)) {
        results.add(pass.getResultTime());
      } else if (simTime == null) {
        break;
      }
    }
    if (results.isEmpty()) {
      return null;
    }
    return results;
  }

  @GetMapping("/groundTrack")
  public GroundTrack groundTrack(
      @RequestParam(value = "duration", defaultValue = "" + DEFAULT_GROUND_TRACK_DURATION) long duration,
      @RequestParam(value = "stepsize", defaultValue = "" + DEFAULT_STEPSIZE) long stepsize)
  {
    AbsoluteDate now = CameraAcquisitorSystemMCAdapter.getNow();
    AbsoluteDate endDate = now.shiftedBy(duration);

    //cache for one hour.
    if (cachedTrack.length > 1
        && (now.durationFrom(cachedTrack[0].orekitDate) < HOUR_IN_SECONDS
        || endDate.durationFrom(cachedTrack[cachedTrack.length - 1].orekitDate) < HOUR_IN_SECONDS)) {
      return new GroundTrack(counter.incrementAndGet(), cachedTrack);
    }

    PositionAndTime[] track = orbitHandler.getGroundTrack(now, endDate, stepsize);
    cachedTrack = track;
    return new GroundTrack(counter.incrementAndGet(), track);
  }

  private void drawSeries(PositionAndTime[] series, int height, int width, Graphics g)
  {
    PositionAndTime lastPoint = series[0];
    for (PositionAndTime point : series) {
      int x1 = imageCoordianteX(lastPoint.location, width);
      int y1 = imageCoordianteY(lastPoint.location, height);
      int x2 = imageCoordianteX(point.location, width);
      int y2 = imageCoordianteY(point.location, height);

      System.out.println(x1);
      System.out.println(x2);
      System.out.println(y1);
      System.out.println(y2);

      if (x2 <= x1) {
        g.drawLine(x1, y1, x2, y2);
      } else {// wrap around case
        double m = (double) (y2 - y1) / (double) ((x2 - width) - x1);
        double b = (int) -((x1 * m) - y1);
        g.drawLine(x1, y1, 0, (int) b);//mx+b //x=0
        g.drawLine(x2, y2, width, (int) (m * (width - x2) + b));// x = width
      }

      lastPoint = point;
    }
  }

  private int imageCoordianteX(GeodeticPoint point, int width)
  {
    return (int) (((mercatorX(point) + Math.PI) / (2 * Math.PI)) * width);
  }

  private int imageCoordianteY(GeodeticPoint point, int height)
  {
    return height - (int) (((mercatorY(point) + Math.PI) / (2 * Math.PI)) * height);
  }

  private double mercatorX(GeodeticPoint point)
  {
    return (point.getLongitude());
  }

  private double mercatorY(GeodeticPoint point)
  {
    double x = Math.sin(point.getLatitude());
    return ((Math.log(1 + x) - Math.log(1 - x)) / 2);
  }

  private TLE getTLE()
  {
    if (lastTLEUpdate.until(Instant.now(), ChronoUnit.HOURS) < 1) {
      return cachedTLE;
    } else {
      return loadTLE();
    }
  }

  private TLE loadTLE()
  {
    try {
      URL url = new URL("https://celestrak.com/satcat/tle.php?CATNR=44878"); //opsat TLE
      BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

      String line0 = in.readLine();// only needed to remove first line
      String line1 = in.readLine();
      String line2 = in.readLine();

      in.close();
      lastTLEUpdate = Instant.now();
      cachedTLE = new TLE(line1, line2);
      return cachedTLE;
    } catch (MalformedURLException ex) {
      Logger.getLogger(CameraAcquisitorGround.class.getName()).log(Level.SEVERE, "loadTLE {0}",
          ex.getMessage());
    } catch (IOException ex) {
      Logger.getLogger(CameraAcquisitorGround.class.getName()).log(Level.SEVERE, "loadTLE {0}",
          ex.getMessage());
    }
    return null;
  }

  private boolean checkTimeSlot(AbsoluteDate scheduleDate)
  {
    AbsoluteDate before = schedule.floor(scheduleDate);
    AbsoluteDate after = schedule.ceiling(scheduleDate);
    System.out.println("before " + before);
    System.out.println("after " + after);
    return (before == null || scheduleDate.durationFrom(before) > DEFAULT_WORST_CASE_ROTATION_TIME_SEC && scheduleDate.compareTo(
        before) != 0)
        && (after == null || after.durationFrom(scheduleDate) > DEFAULT_WORST_CASE_ROTATION_TIME_SEC && scheduleDate.compareTo(
        after) != 0);
  }

  private class CompleteDataReceivedAdapter extends CompleteDataReceivedListener
  {

    @Override
    public void onDataReceived(ParameterInstance parameterInstance)
    {
      LOGGER.log(Level.INFO,
          "\nParameter name: {0}" + "\nParameter Value: {1}" + "\n Instance ID: {2}",
          new Object[]{
            parameterInstance.getName(),
            parameterInstance.getParameterValue().toString(),
            parameterInstance.getSource().getKey().getInstId()
          }
      );
    }
  }

  private void setInitialParameters()
  {
    gma.setParameter(Parameter.ATTITUDE_SAFTY_MARGIN_MS, 1000000);
    gma.setParameter(Parameter.CUSTOM_EXPOSURE_TIME, 1.0f);
    gma.setParameter(Parameter.EXPOSURE_TYPE, 0);//CUSTOM = 0, AUTOMATIC = 1
    gma.setParameter(Parameter.MAX_RETRYS, 5);
    gma.setParameter(Parameter.WORST_CASE_ROTATION_TIME_MS, DEFAULT_WORST_CASE_ROTATION_TIME_MS);

    gma.setParameter(Parameter.GAIN_RED, 1.0f);
    gma.setParameter(Parameter.GAIN_GREEN, 1.0f);
    gma.setParameter(Parameter.GAIN_BLUE, 1.0f);
    gma.setParameter(Parameter.PICTURE_WIDTH, 2048);
    gma.setParameter(Parameter.PICTURE_HEIGHT, 1944);
    gma.setParameter(Parameter.PICTURE_TYPE, PictureFormat.PNG.getOrdinal());
  }

  private void updateEvent(long actionID, int type, Object body)
  {
    if (type == ActivityAcceptance.TYPE_SHORT_FORM) {
      System.out.println("ActivityAcceptance");
      ActivityAcceptance event = (ActivityAcceptance) body;
    } else if (type == ActivityExecution.TYPE_SHORT_FORM) {
      System.out.println("ActivityExecution");
      ActivityExecution event = (ActivityExecution) body;
      int newState = (int) event.getExecutionStage().getValue();
      int stageCount = (int) event.getExecutionStage().getValue();
      boolean success = event.getSuccess();

      // update status of action
      if (activeActions.containsKey(actionID)) {
        synchronized (activeActions) {
          System.out.println("fill array");
          // minus 2 because stage count starts at 1 and an extra stage (for message recived) is added by the Framework
          activeActions.get(actionID)[stageCount - 2] = new ActionReport(stageCount - 1, success,
              "");
          // if some other stage is seccesfull, the command has also been transmitted to the sattelite!
          if (stageCount - 2 > 0 && success) {
            activeActions.get(actionID)[0] = new ActionReport(1, true, "");
          }
        }
        LOGGER.log(Level.INFO, "action state: {0}", Arrays.toString(activeActions.get(actionID)));

      }

      if (success) {
        LOGGER.log(Level.INFO, "Action Update: ID={0}, State={1}",
            new Object[]{actionID, newState});
      } else {
        LOGGER.log(Level.WARNING, "Action Unseccessfull: ID={0}, State={1}",
            new Object[]{actionID, newState});
      }
    } else if (type == AlertEventDetails.TYPE_SHORT_FORM) {
      System.out.println("AlertEventDetails");
      AlertEventDetails event = (AlertEventDetails) body;

      AttributeValueList attValues = event.getArgumentValues();

      String messageToDisplay = "ID: " + actionID + " ";

      if (attValues != null) {
        if (attValues.size() == 1) {
          messageToDisplay += attValues.get(0).getValue().toString();
        }
        if (attValues.size() > 1) {
          for (int i = 0; i < attValues.size(); i++) {
            AttributeValue attValue = attValues.get(i);
            messageToDisplay += "[" + i + "] " + attValue.getValue().toString() + "\n";
          }
        }
      }
      LOGGER.log(Level.WARNING, messageToDisplay);
    }
  }

  /**
   * class for handling the receiving of messages from the space Application
   */
  private class EventReceivedListenerAdapter extends EventReceivedListener
  {

    @Override
    public void onDataReceived(EventCOMObject eventCOMObject)
    {

      if (eventCOMObject.getBody() == null) {
        return;
      }

      long actionID = eventCOMObject.getSource().getKey().getInstId();

      int type = (int) eventCOMObject.getBody().getTypeShortForm();
      updateEvent(actionID, type, eventCOMObject.getBody());
    }
  }

  private class GetAllArchiveAdapter extends ArchiveAdapter
  {

    @Override
    public void queryResponseReceived(MALMessageHeader msgHeader, ObjectType objType,
        IdentifierList domain, ArchiveDetailsList objDetails, ElementList objBodies,
        Map qosProperties)
    {
      if (objBodies != null) {
        int i = 0;
        for (Object objBody : objBodies) {
          if (objBody instanceof ActionInstanceDetails) {
            ActionInstanceDetails instance = ((ActionInstanceDetails) objBody);
            try {
              IdentifierList idList = new IdentifierList();
              idList.add(
                  new Identifier(
                      CameraAcquisitorSystemCameraTargetHandler.ACTION_PHOTOGRAPH_LOCATION));

              ObjectInstancePairList objIds =
                  gma.getMCServices().getActionService().getActionStub().listDefinition(idList);
              if (objIds.size() > 0
                  && objIds.get(0).getObjDefInstanceId().longValue() == instance.getDefInstId().longValue()
                  && instance.getArgumentValues().size() == 3) {

                String timestamp = instance.getArgumentValues().get(2).getValue().toString();
                System.out.println("recovered action: " + timestamp);

                activeActions.put(objDetails.get(i).getInstId(),
                    new ActionReport[CameraAcquisitorSystemCameraTargetHandler.PHOTOGRAPH_LOCATION_STAGES]);

                AbsoluteDate scheduleDate = new AbsoluteDate(timestamp, TimeScalesFactory.getUTC());

                schedule.add(scheduleDate);
              }
            } catch (MALInteractionException | MALException ex) {
              Logger.getLogger(CameraAcquisitorGround.class.getName()).log(Level.SEVERE,
                  ex.getMessage());
            }
          } else {
            updateEvent(objDetails.get(i).getInstId(), objDetails.get(i).getTypeShortForm(), objBody);
          }
          i++;
        }
      }
    }

    @Override
    public void queryUpdateReceived(MALMessageHeader msgHeader, ObjectType objType,
        IdentifierList domain, ArchiveDetailsList objDetails,
        ElementList objBodies,
        Map qosProperties)
    {
      queryResponseReceived(msgHeader, objType, domain, objDetails, objBodies, qosProperties);
    }

  }

}
