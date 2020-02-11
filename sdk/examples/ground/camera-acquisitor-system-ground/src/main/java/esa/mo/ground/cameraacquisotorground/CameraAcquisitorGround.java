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
import esa.mo.mc.impl.provider.ParameterInstance;
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
import java.util.Date;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import org.ccsds.moims.mo.com.activitytracking.ActivityTrackingHelper;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.Union;
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
  private final TerminalInputHandler inputHandler;
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
  private final long DEFAULT_GROUND_TRACK_DURATION = FIVE_DAYS_IN_SECONDS;
  private final long DEFAULT_STEPSIZE = MINUTE_IN_SECONDS;
  private final double DEFAULT_MAX_ANGLE = 45.0;

  private TreeSet<AbsoluteDate> schedule = new TreeSet<>();

  // cached values
  private PositionAndTime[] cachedTrack = new PositionAndTime[0];
  private AtomicLong counter = new AtomicLong(0);

  private TLE cachedTLE;
  private Instant lastTLEUpdate;
  //
  @PostConstruct
  private void start()
  {
    System.out.println("----------------------------- STARTUP --------------------------------");
    //inputHandler.start();
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

  GeodeticPoint esoc = new GeodeticPoint(49.869987, 8.622770, 0);

  public CameraAcquisitorGround(ApplicationArguments args)
  {

    System.out.println("------------------------- parsing arguments --------------------------");

    URI directoryURI = new URI(args.getSourceArgs()[0]);
    System.out.println("directoryURI = " + directoryURI);

    System.out.println("--------------------------- initialisation ----------------------------");
    this.lastTLEUpdate = Instant.MIN;

    //setup orekit
    DataProvidersManager manager = DataProvidersManager.getInstance();
    manager.addProvider(OrekitResources.getOrekitData());

    ProviderSummaryList providers;
    try {
      providers =
          GroundMOAdapterImpl.retrieveProvidersFromDirectory(
              directoryURI);

      // Connect to provider on index 0
      GroundMOAdapterImpl gmaTMP = null;
      boolean providerFound = false;
      for (ProviderSummary provider : providers) {
        if (provider.getProviderName().getValue().equals("App: camera-acquisitor-system")) {
          gmaTMP = new GroundMOAdapterImpl(provider);
          providerFound = true;
          break;
        }
      }

      gma = gmaTMP;
      gma.addDataReceivedListener(new CompleteDataReceivedAdapter());

      Subscription subscription = HelperCOM.generateSubscriptionCOMEvent(
          "ActivityTrackingListener",
          ActivityTrackingHelper.EXECUTION_OBJECT_TYPE);
      gma.getCOMServices().getEventService().addEventReceivedListener(subscription,
          new EventReceivedListenerAdapter());

      setInitialParameters();

      System.out.println("------------------------------------------------------------------------");

    } catch (MALException ex) {
      Logger.getLogger(CameraAcquisitorGround.class.getName()).log(Level.SEVERE, null, ex);
    } catch (MalformedURLException ex) {
      Logger.getLogger(CameraAcquisitorGround.class.getName()).log(Level.SEVERE, null, ex);
    } catch (MALInteractionException ex) {
      Logger.getLogger(CameraAcquisitorGround.class.getName()).log(Level.SEVERE, null, ex);
    }

    inputHandler = new TerminalInputHandler();

    orbitHandler = new OrbitHandler(getTLE());
  }

  public void sendCommandShedulePhotograph(double latitude, double longitude, double maxAngle,
      OrbitHandler.TimeModeEnum timeMode)
  {
    Union[] parameters = new Union[]{new Union(latitude), new Union(longitude), new Union(maxAngle),
      new Union(timeMode.ordinal())};

    gma.invokeAction(CameraAcquisitorSystemCameraTargetHandler.ACTION_PHOTOGRAPH_LOCATION,
        parameters);
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

  @PostMapping("/schedulePhotographPosition")
  public boolean schedulePhotographPosition(
      @RequestParam(value = "longitude") double longitude,
      @RequestParam(value = "latitude") double latitude,
      @RequestParam(value = "timeStemp") String timeStemp)
  {
    Union[] parameters =
        new Union[]{new Union(latitude), new Union(longitude), new Union(timeStemp)};

    System.out.println(timeStemp);
    AbsoluteDate scheduleDate = new AbsoluteDate(timeStemp, TimeScalesFactory.getUTC());
    System.out.println(scheduleDate);
    AbsoluteDate before = schedule.floor(scheduleDate);
    AbsoluteDate after = schedule.ceiling(scheduleDate);

    if ((before == null || scheduleDate.durationFrom(before) > DEFAULT_WORST_CASE_ROTATION_TIME_SEC)
        && (after == null || after.durationFrom(scheduleDate) > DEFAULT_WORST_CASE_ROTATION_TIME_SEC)) {
      try {
        schedule.add(scheduleDate);

        gma.invokeAction(
            CameraAcquisitorSystemCameraTargetHandler.ACTION_PHOTOGRAPH_LOCATION_MANUAL,
            parameters);
        return true;
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, e.getMessage());
      }
    }
    return false;
  }

  @GetMapping("/photographTime")
  public String getTimeOfPhotograph(
      @RequestParam(value = "longitude") double longitude,
      @RequestParam(value = "latitude") double latitude,
      @RequestParam(value = "maxAngle", defaultValue = "" + DEFAULT_MAX_ANGLE) double maxAngle,
      @RequestParam(value = "timeMode", defaultValue = "ANY") OrbitHandler.TimeModeEnum timeMode)
  {
    Pass pass = orbitHandler.getPassTime(
        longitude, latitude,
        maxAngle, timeMode,
        CameraAcquisitorSystemMCAdapter.getNow(),
        DEFAULT_WORST_CASE_ROTATION_TIME_SEC,
        MAX_SIM_RANGE);

    return pass.getResultTime();
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
      Logger.getLogger(CameraAcquisitorGround.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(CameraAcquisitorGround.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  private class CompleteDataReceivedAdapter extends CompleteDataReceivedListener
  {

    @Override
    public void onDataReceived(ParameterInstance parameterInstance)
    {
      LOGGER.log(Level.INFO,
          "\nParameter name: {0}" + "\n" + "Parameter Value: {1}",
          new Object[]{
            parameterInstance.getName(),
            parameterInstance.getParameterValue().toString()
          }
      );
    }
  }

  private class EventReceivedListenerAdapter extends EventReceivedListener
  {

    @Override
    public void onDataReceived(EventCOMObject eventCOMObject)
    {

      LOGGER.log(Level.INFO, "event:\n{0}\n{1}\n{2}", new Object[]{eventCOMObject.getBody(),
        eventCOMObject.getTimestamp(), eventCOMObject.getObjId()});
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
}
