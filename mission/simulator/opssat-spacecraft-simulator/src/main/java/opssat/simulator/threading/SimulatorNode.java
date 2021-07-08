/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2021      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 *  You may not use this file except in compliance with the License.
 * 
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *  
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 *  ----------------------------------------------------------------------------
 */
package opssat.simulator.threading;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import opssat.simulator.GPS;
import opssat.simulator.Orbit;
import opssat.simulator.celestia.CelestiaData;
import opssat.simulator.interfaces.ISimulatorDeviceData;
import opssat.simulator.interfaces.InternalData;
import opssat.simulator.models.OpticalReceiverModel;
import opssat.simulator.orekit.GPSSatInView;
import opssat.simulator.orekit.OrekitCore;
import opssat.simulator.peripherals.GenericPeripheral;
import opssat.simulator.peripherals.PCCSDSEngine;
import opssat.simulator.peripherals.PCamera;
import opssat.simulator.peripherals.PFDIR;
import opssat.simulator.peripherals.PFineADCS;
import opssat.simulator.peripherals.PFineADCS.FWRefFineADCS;
import opssat.simulator.peripherals.PGPS;
import opssat.simulator.peripherals.PMityARM;
import opssat.simulator.peripherals.PNanomind;
import opssat.simulator.peripherals.POpticalReceiver;
import opssat.simulator.peripherals.PSDR;
import opssat.simulator.tcp.TCPServerReceiveOnly;
import opssat.simulator.util.ArgumentDescriptor;
import opssat.simulator.util.ArgumentTemplate;
import opssat.simulator.util.CommandDescriptor;
import opssat.simulator.util.CommandResult;
import opssat.simulator.util.EndlessSingleStreamOperatingBuffer;
import opssat.simulator.util.EndlessWavStreamOperatingBuffer;
import opssat.simulator.util.PlatformMessage;
import opssat.simulator.util.SimulatorData;
import opssat.simulator.util.SimulatorDeviceData;
import opssat.simulator.util.SimulatorHeader;
import opssat.simulator.util.SimulatorSchedulerPiece;
import opssat.simulator.util.SimulatorSpacecraftState;
import opssat.simulator.util.SimulatorTimer;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import static org.hipparchus.util.FastMath.toDegrees;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.errors.OrekitException;
import org.orekit.propagation.analytical.tle.TLE;

/**
 *
 * @author Cezar Suteu
 */
public class SimulatorNode extends TaskNode
{

  private int counter;
  private boolean sendList;
  private boolean sendHeader;
  LinkedList<File> interfaceFilesList;
  LinkedList<CommandDescriptor> commandsList;
  LinkedList<CommandDescriptor> commandsQueue;
  LinkedList<CommandResult> commandsResults;
  LinkedList<SimulatorDeviceData> simulatorDevices;
  SimulatorData simulatorData;
  SimulatorHeader simulatorHeader;
  HashMap<DevDatPBind, ArgumentDescriptor> hMapSDData;
  int schedulerDataIndex;
  LinkedList<SimulatorSchedulerPiece> schedulerData;

  // Optical Receiver
  OpticalReceiverModel opticalReceiverModel;
  // Orekit
  OrekitCore orekitCore;

  // Models
  private Orbit darkDusk;
  private GPS gps;

  // Simulator Data Bindings
  // Below is alphabetical order of interfaces, used to map GUI data
  private final static int INTERFACE_CCSDSENGINE = 0;
  private final static int INTERFACE_CAMERA = 1;
  private final static int INTERFACE_FDIR = 2;
  private final static int INTERFACE_FINEADCS = 3;
  private final static int INTERFACE_GPS = 4;
  private final static int INTERFACE_MITTYARM = 5;
  private final static int INTERFACE_NANOMIND = 6;
  private final static int INTERFACE_OPTICALRECEIVER = 7;
  private final static int INTERFACE_SDR = 8;

  public static final double DEFAULT_OPS_SAT_A = 6886;// [km]
  public static final double EARTH_RADIUS = 6371; // [km]
  public static final double DEFAULT_OPS_SAT_R = 515; // [km]
  public final static double DEFAULT_OPS_SAT_E = 0;
  public final static double DEFAULT_OPS_SAT_ORBIT_I = 98.05;// [deg]
  public final static double DEFAULT_OPS_SAT_RAAN = 340;// [deg]
  public final static double DEFAULT_OPS_SAT_ARG_PER = 0;// [deg]
  public final static double DEFAULT_OPS_SAT_TRUE_ANOMALY = 0;// [deg]
  public static final double EARTH_RADIUS_POLAR = 6356.8; // [km]
  public static final double EARTH_RADIUS_EQUATOR = 6378.1; // [km]

  public final static int CAMERA_MAX_SIZE = 7962624;// [bytes]
  private EndlessSingleStreamOperatingBuffer cameraBuffer;
  private EndlessWavStreamOperatingBuffer sdrBuffer;

  private final static int BENCHMARK_START_COUNTER = 3000;
  private final static int BENCHMARK_COUNTER_EVALUATIONS = 1500;
  private boolean benchmarkInProgress = false;
  private boolean benchmarkFinished = false;
  private long benchmarkTimeElapsed = 0;
  private long benchmarkStartupTime = 0;
  TCPServerReceiveOnly quaternionTcpServer = null;

  private String cameraScriptPath = null;
  private final static String OPS_SAT_SIMULATOR_DATA = File.separator + ".ops-sat-simulator"
      + File.separator;
  private final static String OPS_SAT_SIMULATOR_RESOURCES = OPS_SAT_SIMULATOR_DATA + "resources"
      + File.separator;

  private final static String OPSSAT_TLE_LINE1 =
      "1 44878U 19092F   20159.72929773  .00000725  00000-0  41750-4 0  9990";
  private final static String OPSSAT_TLE_LINE2 =
      "2 44878  97.4685 343.1680 0015119  36.0805 324.1445 15.15469997 26069";

  // Platform sim properties
  private Properties platformProperties;

  /**
   * Reads the platform properties or initializes them with default
   */
  private void loadPlatformProperties()
  {
    try
    {
      platformProperties = this.readProperties("platformsim.properties");
    }
    catch(IOException e) {
      logger.log(Level.WARNING, "Could not initialize platformsim.properties - using defaults.");
      platformProperties = new Properties();
      platformProperties.setProperty("platform.mode", "sim");
      platformProperties.setProperty("camerasim.imagemode", "Fixed");
      platformProperties.setProperty("camerasim.imagefile", "fix/me/earth.jpg");
      platformProperties.setProperty("camerasim.imagedirectory", "fix/me");
      platformProperties.setProperty("camera.adapter", "esa.mo.platform.impl.provider.opssat.CameraOPSSATAdapter");
      updatePlatformConfig();
    }
  }
  /**
   * Reads the properties of the given .properties file.
   *
   * @param filename The properties file to read.
   */
  private Properties readProperties(String filename) throws FileNotFoundException, IOException
  {
    InputStream input = new FileInputStream(filename);
    Properties prop = new Properties();
    prop.load(input);
    return prop;
  }

  public static String getResourcesPath()
  {
    return System.getProperty("user.home") + OPS_SAT_SIMULATOR_RESOURCES;
  }

  public static String getDataPath()
  {
    return System.getProperty("user.home") + OPS_SAT_SIMULATOR_DATA;
  }

  public static String getWorkingDir()
  {
    return System.getProperty("user.dir");
  }

  public static String calcNMEAChecksum(String sentence)
  {
    char result = 0;
    // Trim "$" at the beginning of the sentence
    for (char c : sentence.substring(1).toCharArray()) {
      result ^= c;
    }
    return String.format("*%02X", (int) result);
  }

  public static String handleResourcePath(String path, Logger logger, ClassLoader classLoader,
      boolean replace)
  {
    String resourcesFolder = SimulatorNode.getResourcesPath();
    File folder = new File(resourcesFolder);
    if (!folder.exists()) {
      logger.log(Level.FINE, "Folder [" + folder.getAbsolutePath() + "] has been created");
      folder.mkdir();
    }
    String absolutePath = resourcesFolder + path;
    File f = new File(absolutePath);

    if (replace && !f.isDirectory()) {
      f.delete();
    }

    if (f.exists() && !f.isDirectory()) {
      logger.log(Level.FINE, "File [" + f.getAbsolutePath() + "] exists");
    } else {
      logger.log(Level.FINE, "File [" + absolutePath + "] does not exist");
      final URL url2 = classLoader.getSystemResource(path);

      final InputStream inputStream = classLoader.getSystemResourceAsStream(path);
      if (inputStream != null) {
        try {
          File newFile = new File(absolutePath);
          if (newFile.createNewFile()) {
            logger.log(Level.FINER, "File created");
            OutputStream outputStream = new FileOutputStream(newFile);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
              outputStream.write(bytes, 0, read);
            }
            outputStream.close();
          } else {
            logger.log(Level.WARNING, "File [" + absolutePath + "] could not be created");
          }
        } catch (IOException e) {
          logger.log(Level.WARNING, e.toString());
        }
      } else {
        logger.log(Level.WARNING, "Resource file [" + path + "] could not be found");
      }
    }
    return absolutePath;
  }

  private LinkedList<GPSSatInView> getSatsInView()
  {
    LinkedList<GPSSatInView> tempResult = new LinkedList<>();
    if (this.simulatorHeader.isUseOrekitPropagator()) {
      tempResult = this.orekitCore.getSatsInViewAsList();
    } else {
      tempResult.add(new GPSSatInView("test", 100000));
    }
    return tempResult;
  }

  /**
   * gets current TLE
   *
   * WARNING:
   *
   * @see OrekitCore.getTLE()
   * @return TLE
   */
  public TLE getTLE()
  {
    if (this.simulatorHeader.isUseOrekitPropagator()) {
      return this.orekitCore.getTLE();
    } else {
      Logger.getLogger(SimulatorNode.class.getCanonicalName()).log(Level.WARNING,
          "TLE only awailable in Simulator, wenn Using Orekit propagator!");
      return new TLE(OPSSAT_TLE_LINE1, OPSSAT_TLE_LINE2);
    }
  }

  public void runVectorTargetTracking(float x, float y, float z, float margin)
  {
    logger.log(Level.INFO, "Vector " + x + " " + y + " " + z);
    if (simulatorHeader.isUseOrekitPropagator()) {
      this.orekitCore.changeAttitudeVectorTarget(x, y, z, margin);
    }
  }

  public enum DevDatPBind
  {
    Camera_CameraBuffer, Camera_CameraBufferOperatingIndex,
    FineADCS_ModeOperation, FineADCS_PositionInertial, FineADCS_VelocityInertial, FineADCS_Q1,
    FineADCS_Q2, FineADCS_Q3, FineADCS_Q4, FineADCS_MagneticField, FineADCS_Rotation,
    FineADCS_Magnetometer, FineADCS_SunVector, FineADCS_ReactionWheels, FineADCS_Accelerometer,
    FineADCS_Gyro1, FineADCS_Gyro2, FineADCS_Magnetorquer, FineADCS_AngularMomentum,
    FineADCS_AngularVelocity,
    GPS_Latitude, GPS_Longitude, GPS_Altitude, GPS_GS_Elevation, GPS_GS_Azimuth, GPS_SatsInView,
    OpticalReceiver_OperatingBuffer, OpticalReceiver_OperatingBufferIndex,
    OpticalReceiver_DegradationRate,
    SDR_OperatingBuffer, SDR_OperatingBufferIndex
  }

  private void makeSimulatorDeviceBindings()
  {
    hMapSDData = new HashMap<>();
    int i = 0;
    this.hMapSDData.put(DevDatPBind.Camera_CameraBuffer,
        simulatorDevices.get(INTERFACE_CAMERA).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.Camera_CameraBufferOperatingIndex,
        simulatorDevices.get(INTERFACE_CAMERA).getDataList().get(i++));
    i = 0;
    this.hMapSDData.put(DevDatPBind.FineADCS_ModeOperation,
        simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.FineADCS_PositionInertial,
        simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.FineADCS_VelocityInertial,
        simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.FineADCS_Q1,
        simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));

    this.hMapSDData.put(DevDatPBind.FineADCS_Q2,
        simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.FineADCS_Q3,
        simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.FineADCS_Q4,
        simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.FineADCS_MagneticField,
        simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.FineADCS_Rotation,
        simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.FineADCS_Magnetometer,
        simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.FineADCS_SunVector,
        simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.FineADCS_ReactionWheels,
        simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.FineADCS_Accelerometer,
        simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.FineADCS_Gyro1,
        simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.FineADCS_Gyro2,
        simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.FineADCS_Magnetorquer,
        simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.FineADCS_AngularMomentum,
        simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.FineADCS_AngularVelocity,
        simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));

    i = 0;
    this.hMapSDData.put(DevDatPBind.GPS_Latitude,
        simulatorDevices.get(INTERFACE_GPS).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.GPS_Longitude,
        simulatorDevices.get(INTERFACE_GPS).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.GPS_Altitude,
        simulatorDevices.get(INTERFACE_GPS).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.GPS_GS_Elevation,
        simulatorDevices.get(INTERFACE_GPS).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.GPS_GS_Azimuth,
        simulatorDevices.get(INTERFACE_GPS).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.GPS_SatsInView,
        simulatorDevices.get(INTERFACE_GPS).getDataList().get(i++));

    i = 0;
    this.hMapSDData.put(DevDatPBind.OpticalReceiver_OperatingBuffer,
        simulatorDevices.get(INTERFACE_OPTICALRECEIVER).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.OpticalReceiver_OperatingBufferIndex,
        simulatorDevices.get(INTERFACE_OPTICALRECEIVER).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.OpticalReceiver_DegradationRate,
        simulatorDevices.get(INTERFACE_OPTICALRECEIVER).getDataList().get(i++));

    i = 0;
    this.hMapSDData.put(DevDatPBind.SDR_OperatingBuffer,
        simulatorDevices.get(INTERFACE_SDR).getDataList().get(i++));
    this.hMapSDData.put(DevDatPBind.SDR_OperatingBufferIndex,
        simulatorDevices.get(INTERFACE_SDR).getDataList().get(i++));

  }

  private void initModels()
  {
    benchmarkInProgress = false;
    benchmarkFinished = false;
    String imageFile = platformProperties.getProperty("camerasim.imagefile");
    this.cameraBuffer.loadImageFromAbsolutePath(imageFile);
    this.sdrBuffer = new EndlessWavStreamOperatingBuffer(this.logger);
    this.logger.log(Level.FINE, "Kepler elements [" + simulatorHeader.getKeplerElements() + "]");
    // Values from the OPS-SAT document: a = 6371+650= 7021 km ; i = 98.05 deg
    // (orbital period: 1.63 hours)
    // (double a, double i, double RAAN, double arg_per, double true_anomaly,
    // initial epoch)
    double OPS_SAT_A = DEFAULT_OPS_SAT_A;
    double OPS_SAT_E = 0;
    double OPS_SAT_ORBIT_I = 98.05;// [deg]
    double OPS_SAT_RAAN = 340;// [deg]
    double OPS_SAT_ARG_PER = 0;// [deg]
    double OPS_SAT_TRUE_ANOMALY = 0;// [deg]
    // Try to obtain kepler elements from default
    boolean keplerElementsOk = true;
    boolean displayKeplerElementsWarning = false;
    if (simulatorHeader.getKeplerElements() != null) {
      String[] list = simulatorHeader.getKeplerElements().split(";");
      if (list.length == 6) {
        OPS_SAT_A = Double.parseDouble(list[0]);
        OPS_SAT_E = Double.parseDouble(list[1]);
        OPS_SAT_ORBIT_I = Double.parseDouble(list[2]);
        OPS_SAT_RAAN = Double.parseDouble(list[3]);
        OPS_SAT_ARG_PER = Double.parseDouble(list[4]);
        OPS_SAT_TRUE_ANOMALY = Double.parseDouble(list[5]);

      } else {
        displayKeplerElementsWarning = true;
        keplerElementsOk = false;
      }
    } else {
      keplerElementsOk = false;
    }
    if (keplerElementsOk) {
      this.logger.log(Level.FINE, "Keplerian elements loaded successfuly from header file.");
    } else {
      if (displayKeplerElementsWarning) {
        this.logger.log(Level.WARNING,
            "Errors found during parsing of simulator header. Loading default OPS-SAT keplerian elements.");
      }
      OPS_SAT_A = DEFAULT_OPS_SAT_A;
      OPS_SAT_E = DEFAULT_OPS_SAT_E;
      OPS_SAT_ORBIT_I = DEFAULT_OPS_SAT_ORBIT_I;
      OPS_SAT_RAAN = DEFAULT_OPS_SAT_RAAN;
      OPS_SAT_ARG_PER = DEFAULT_OPS_SAT_ARG_PER;
      OPS_SAT_TRUE_ANOMALY = DEFAULT_OPS_SAT_TRUE_ANOMALY;

    }
    this.darkDusk = new Orbit(OPS_SAT_A, OPS_SAT_ORBIT_I * (Math.PI / 180),
        (OPS_SAT_RAAN) * (Math.PI / 180), (OPS_SAT_ARG_PER) * (Math.PI / 180), OPS_SAT_TRUE_ANOMALY,
        simulatorHeader.getStartDateString());
    this.gps = new GPS(darkDusk);

    if (this.simulatorHeader.isUseOrekitPropagator()) {
      this.logger.log(Level.FINE, "Calling orekit constructor");
      try {
        this.orekitCore = new OrekitCore(OPS_SAT_A * 1000, OPS_SAT_E, OPS_SAT_ORBIT_I,
            OPS_SAT_ARG_PER, OPS_SAT_RAAN, OPS_SAT_TRUE_ANOMALY, simulatorHeader, this.logger,
            this);
        this.logger.log(Level.FINE, "orekit initialized successfully");
        this.orekitCore.processPropagateStep(0);
      } catch (OrekitException exception) {
        this.logger.log(Level.SEVERE,
            "orekit initialization failed from [" + exception + "]! Switching module off");
        this.simulatorHeader.setUseOrekitPropagator(false);
      }
    }
    this.opticalReceiverModel = new OpticalReceiverModel("Optical Receiver", this.logger);
    schedulerDataIndex = 0;
    for (SimulatorSchedulerPiece p : schedulerData) {
      p.setExecuted(false);
    }
    simulatorData.setMethodsExecuted(0);

    this.sendHeader = true;

  }

  public static final String TIMER_SIMULATOR_DATA = "SimulatorData";
  private static final String TIMER_DEVICE_DATA = "DeviceData";
  public static final String TIMER_CELESTIA_DATA = "Celestia";
  private static final String TIMER_SCHEDULER_DATA = "SchedulerData";
  private static final String TIMER_SCIENCE1_DATA = "Science1";

  private static final int TIMER_SCIENCE1_DATA_INTERVAL = 2000;
  private static final int TIMER_DEVICE_DATA_INTERVAL = 1000;
  private static final int TIMER_SCHEDULER_DATA_INTERVAL = 5000;
  private static final int TIMER_SIMULATOR_DATA_INTERVAL = 500;
  private static final int TIMER_CELESTIA_INTERVAL = 300;

  private Logger logger;

  public SimulatorNode(ConcurrentLinkedQueue<Object> queueIn,
      ConcurrentLinkedQueue<Object> queueOut, String name, int delay, Level logLevel,
      Level consoleLogLevel)
  {
    super(queueIn, queueOut, name, delay, logLevel, consoleLogLevel);
    benchmarkStartupTime = System.currentTimeMillis();
    this.logger = super.getLogObject();
    super.getTimers().put(TIMER_SIMULATOR_DATA,
        new SimulatorTimer(TIMER_SIMULATOR_DATA, TIMER_SIMULATOR_DATA_INTERVAL));
    super.getTimers().put(TIMER_DEVICE_DATA,
        new SimulatorTimer(TIMER_DEVICE_DATA, TIMER_DEVICE_DATA_INTERVAL));
    super.getTimers().put(TIMER_SCHEDULER_DATA,
        new SimulatorTimer(TIMER_SCHEDULER_DATA, TIMER_SCHEDULER_DATA_INTERVAL));
    super.getTimers().put(TIMER_CELESTIA_DATA,
        new SimulatorTimer(TIMER_CELESTIA_DATA, TIMER_CELESTIA_INTERVAL));
    // super.getTimers().put(TIMER_SCIENCE1_DATA, new
    // SimulatorTimer(TIMER_SCIENCE1_DATA, TIMER_SCIENCE1_DATA_INTERVAL));
    interfaceFilesList = new LinkedList<>();
    simulatorDevices = new LinkedList<>();
    commandsList = new LinkedList<>();
    commandsQueue = new LinkedList<>();
    commandsResults = new LinkedList<>();
    String workingdir = System.getProperty("user.dir");
    this.logger.log(Level.ALL, "Workingdir is [" + workingdir + "]");
    File interfacesFolderCheck = new File(workingdir);
    this.logger.log(Level.ALL, "Location is " + interfacesFolderCheck.getName());
    loadMethodsFromReflection();
    loadTemplatesFromFile(getTemplatesFile());
    loadSchedulerFromFile(getSchedulerFile());
    simulatorData = new SimulatorData(0);

    loadSimulatorHeader();
    simulatorData.initFromHeader(simulatorHeader);
    makeSimulatorDeviceBindings();
    loadSimulatorCommandsFilter();
    this.cameraBuffer = new EndlessSingleStreamOperatingBuffer(this.logger);
    loadPlatformProperties();
    // Models
    initModels();

  }

  private void loadMethodsFromReflection()
  {
    this.logger.log(Level.FINE, "loadMethodsFromReflection");

    reflectObjectGetMethods(new PCCSDSEngine(null, "CCSDSEngine"));
    reflectObjectGetMethods(new PCamera(null, "Camera"));
    reflectObjectGetMethods(new PFDIR(null, "FDIR"));
    reflectObjectGetMethods(new PFineADCS(null, "FineADCS"));
    reflectObjectGetMethods(new PGPS(null, "GPS"));
    reflectObjectGetMethods(new PMityARM(null, "MityARM"));
    reflectObjectGetMethods(new PNanomind(null, "Nanomind"));
    reflectObjectGetMethods(new POpticalReceiver(null, "OpticalReceiver"));
    reflectObjectGetMethods(new PSDR(null, "SDR"));

    Collections.sort(commandsList, (o1, o2) -> o1.getInternalID() - o2.getInternalID());
    loadMethodsDescriptionFromResources();
  }

  private void putDescriptionIntoMethod(String description, int internalID)
  {
    for (CommandDescriptor c : commandsList) {
      if (c.getInternalID() == internalID) {
        c.setComment(description);
        break;
      }
    }
  }

  /**
   * Updates the configuration parameters of the peripheral simulators (e.g. Camera).
   */
  public void updatePlatformConfig()
  {
    try {
      this.writeProperties(new File("platformsim.properties"), this.platformProperties);
    } catch (IOException e) {
      Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE,
          "Could not save platform properties");
    }
    reloadImageBuffer();
  }

  public void writeProperties(File file, Properties props) throws IOException
  {
    FileOutputStream fos = new FileOutputStream(file);
    props.store(fos, null);
  }

  /**
   * Reloads the cameraBuffer to contain either the selected or a random image.
   */
  private void reloadImageBuffer()
  {
    String mode = platformProperties.getProperty("camerasim.imagemode");
    String path;
    if (mode.equals("Fixed")) {
      path = platformProperties.getProperty("camerasim.imagefile");
      this.cameraBuffer.loadImageFromAbsolutePath(path);
    } else {
      path = platformProperties.getProperty("camerasim.imagedirectory");
      try {
        Stream<Path> walker = Files.walk(Paths.get(path));
        List<String> files = walker
            .map(p -> p.getFileName().toString()).filter(s -> s.endsWith(".png")
            || s.endsWith(".jpg") || s.endsWith("bmp") || s.endsWith(".raw"))
            .collect(Collectors.toList());
        walker.close();
        Random r = new Random();
        int filenum = r.nextInt(files.size());
        String absolutePath = path + "/" + files.get(filenum);
        this.cameraBuffer.loadImageFromAbsolutePath(absolutePath);
      } catch (IOException e) {
        logger.log(Level.WARNING, "Could not reload image", e);
      }
    }
  }

  private ArrayList<String> readLinesFromInputStream(InputStream fileName)
  {

    if (fileName != null) {
      ArrayList<String> result = new ArrayList<>();
      try {
        BufferedReader in = new BufferedReader(new InputStreamReader(fileName));
        String description = null;
        boolean skipRead;
        String line;
        while ((line = in.readLine()) != null) {
          skipRead = false;
          if (line.equals("/**")) {
            description = "";
            skipRead = true;
          } else if (line.equals("*/")) {
            skipRead = true;
          } else {
            String lineWords[] = line.split(" ");
            if (lineWords.length > 1) {
              if (lineWords[0].equals("void") || lineWords[0].equals("byte[]")) {
                String lineWords2[] = line.split("//");
                if (lineWords2.length > 1) {
                  int internalID = Integer.parseInt(lineWords2[1]);
                  putDescriptionIntoMethod(description, internalID);
                }
              }
            }
          }
          if (!skipRead && !line.contains("<pre>") && !line.contains("</pre>")) {
            description += line + "\n";

          }
        }

      } catch (IOException ex) {
        Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
      }
      return result;
    } else {
      this.logger.log(Level.WARNING,
          "InputStream [" + fileName.toString() + "] could not be accessed!");
      return null;
    }
  }

  private void loadMethodsDescriptionFromResources()
  {
    String fileName = "descriptions.txt";
    ClassLoader classLoader = getClass().getClassLoader();
    InputStream istream = classLoader.getResourceAsStream(fileName);
    if (istream != null) {
      readLinesFromInputStream(istream);
    } else {
      this.logger.log(Level.WARNING, "Error reading resource file!");
    }
  }

  private void reflectObjectGetMethods(Object targetObject)
  {
    String name = ((GenericPeripheral) targetObject).getName();
    this.logger.log(Level.FINE, "reflectObjectGetMethods from [" + name + "]");
    SimulatorDeviceData simulatorDeviceData = new SimulatorDeviceData(name);
    simulatorDevices.add(simulatorDeviceData);
    ISimulatorDeviceData simulatorDeviceDataAnnotation = targetObject.getClass()
        .getAnnotation(ISimulatorDeviceData.class);
    if (simulatorDeviceDataAnnotation != null) {
      for (String str : simulatorDeviceDataAnnotation.descriptors()) {
        String split[] = str.split(":");
        this.logger.log(Level.FINEST, "Reflecting simulatorDeviceDataAnnotation [" + str + "]");
        simulatorDeviceData.getDataList().add(new ArgumentDescriptor(split[0], split[1]));
      }
    }
    this.logger.log(Level.FINE, "Reflected [" + simulatorDeviceData.getDataList().size()
        + "] simulator data pieces from annotation.");
    for (Method m : targetObject.getClass().getMethods()) {
      String body = m.toString();
      String[] bodyWords = body.split(" ");
      boolean foundData = false;
      int j = 0;
      this.logger.log(Level.FINEST, "Processing [+" + body + "+]");
      for (String test : bodyWords) {
        this.logger.log(Level.FINEST, "Processing [+" + test + "+]");
        String returnString = "";
        switch (test) {
          case "java.lang.String":
            foundData = true;
            returnString = "String";
            break;
          case "byte[]":
            foundData = true;
            returnString = "byte[]";
            break;
          case "float":
            foundData = true;
            returnString = "float";
            break;
          case "void":
            foundData = true;
            returnString = "void";
            break;
          case "double":
            foundData = true;
            returnString = "double";
            break;
          case "double[]":
            foundData = true;
            returnString = "double[]";
            break;
          case "int":
            foundData = true;
            returnString = "int";
            break;
          case "long":
            foundData = true;
            returnString = "long";
            break;
          default:
            foundData = false;
            break;
        }
        j++;
        if (foundData) {
          String[] functionWords = bodyWords[j].split("\\(");
          String functionName = functionWords[0];
          String[] functionWords2 = functionName.split("\\.");
          functionName = functionWords2[functionWords2.length - 1];
          this.logger.log(Level.FINEST, "functionName [" + functionName + "]");
          if (functionName.equals("toString") || functionName.equals("wait")) {
            // Disregard the method, it is internal to java language
            foundData = false;
          } else {
            StringBuilder arguments = new StringBuilder();
            String[] argumentsBody = functionWords[1].split("\\,");
            InternalData internalData = m.getAnnotation(InternalData.class);
            int indexArg = 0;
            for (String singleArg : argumentsBody) {
              this.logger.log(Level.FINE, "Checking singleArg [" + singleArg + "]");
              if (singleArg.equals("java.lang.String)") || singleArg.equals("java.lang.String")) {
                arguments.append("String ").append(internalData.argNames()[indexArg]);
              } else if (singleArg.contains("int[]")) {
                arguments.append("int[] ").append(internalData.argNames()[indexArg]);
              } else if (singleArg.contains("int")) {
                arguments.append("int ").append(internalData.argNames()[indexArg]);
              } else if (singleArg.contains("byte[]")) {
                arguments.append("byte[] ").append(internalData.argNames()[indexArg]);
              } else if (singleArg.contains("byte")) {
                arguments.append("byte ").append(internalData.argNames()[indexArg]);
              } else if (singleArg.contains("long[]")) {
                arguments.append("long[] ").append(internalData.argNames()[indexArg]);
              } else if (singleArg.contains("long")) {
                arguments.append("long ").append(internalData.argNames()[indexArg]);
              } else if (singleArg.contains("float[]")) {
                arguments.append("float[] ").append(internalData.argNames()[indexArg]);
              } else if (singleArg.contains("float")) {
                arguments.append("float ").append(internalData.argNames()[indexArg]);
              } else if (singleArg.contains("double")) {
                arguments.append("double ").append(internalData.argNames()[indexArg]);
              } else if (!singleArg.equals(")")) {
                this.logger.log(Level.INFO, "Unknown argument type [" + singleArg + "]");
              }
              indexArg = indexArg + 1;
              if (indexArg < argumentsBody.length) {
                arguments.append(",");
              }
            }
            body = returnString + " " + functionName + "(" + arguments + ")";
            break;
          }
        }

      }
      if (foundData) {
        StringBuilder argNames = new StringBuilder();
        InternalData internalData = m.getAnnotation(InternalData.class);
        String annotation = "";

        if (internalData != null) {
          annotation = String.valueOf(internalData.internalID());
          for (String str : internalData.argNames()) {
            argNames.append(str);
          }

          CommandDescriptor commandDescriptor = new CommandDescriptor(name, body,
              internalData.description(), internalData.internalID(), super.getLogObject());
          this.logger.log(Level.FINE, "Added method [" + commandDescriptor.toString() + "]");
          // System.out.println(commandDescriptor.toCustomFormat1());

          commandsList.add(commandDescriptor);
        }

      }

    }
  }

  private File getFileFromDirAndPath(String workingDir, String targetPath)
  {
    File result = new File(workingDir, targetPath);
    // this.logger.log(Level.INFO, "Opening path [" + result.getAbsolutePath() +
    // "].");
    return result;
  }

  public File getGPSOpsFile()
  {
    return getFileFromDirAndPath(getResourcesPath(), "gps-ops.txt");
  }

  private File getSchedulerFile()
  {
    return getFileFromDirAndPath(getWorkingDir(), "_OPS-SAT-SIMULATOR-scheduler.txt");
  }

  private File getSchedulerFileAsBackup()
  {
    String now = new SimpleDateFormat("yyyy_MMdd_HHmmss").format(new Date());
    return getFileFromDirAndPath(getWorkingDir(),
        "_OPS-SAT-SIMULATOR-scheduler_backup_" + now + ".txt");
  }

  private File getTemplatesFile()
  {
    return getFileFromDirAndPath(getWorkingDir(), "_OPS-SAT-SIMULATOR-templates.txt");
  }

  private File getHeaderFile()
  {
    return getFileFromDirAndPath(getWorkingDir(), "_OPS-SAT-SIMULATOR-header.txt");
  }

  private File getCommandsFilterFile()
  {
    return getFileFromDirAndPath(getWorkingDir(), "_OPS-SAT-SIMULATOR-filter.txt");
  }

  private boolean isValidCommandID(int commandID)
  {
    return true;
  }

  private void loadSimulatorCommandsFilter()
  {
    File filterFile = getCommandsFilterFile();
    if (filterFile.exists()) {
      this.logger.log(Level.FINE, "Filter [" + filterFile.toString() + "] found!");
      boolean dataOk = true;

      try {
        BufferedReader in = new BufferedReader(new FileReader(filterFile.getAbsolutePath()));
        String line;
        while ((line = in.readLine()) != null) {
          List<String> items = Arrays.asList(line.split(" "));
          if (line.startsWith("#")) {
            // comment line
          } else if (items.size() != 1) {
            this.logger.log(Level.SEVERE,
                "Read from filter file: size of line [" + line + "]  was invalid!");
            dataOk = false;
            break;
          } else {
            String fieldName;
            fieldName = items.get(0);
            int test = 0;
            try {
              test = Integer.parseInt(fieldName);
            } catch (NumberFormatException ex) {
              this.logger.log(Level.SEVERE, ex.toString());
            }
            if (checkInternalIDExists(test)) {
              this.logger.log(Level.FINE, "Found valid command ID [" + fieldName + "]");
              CommandDescriptor c = getCommandDescriptorForID(Integer.parseInt(fieldName));
              c.setVisible(true);
            }

          }

        }

        in.close();
      } catch (IOException ex) {
        Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
      }
      if (!dataOk) {
        this.logger.log(Level.FINE, "Data from filter file was invalid!");
        initializeFilter(filterFile);
      }

    } else {
      this.logger.log(Level.FINE, "Filter file was not found!");
      // initializeFilter(filterFile);
    }

  }

  private void loadSimulatorHeader()
  {
    // This function is responsible for initializing simulator header if it is not
    // found or reading from it
    File headerFile = getHeaderFile();
    if (headerFile.exists()) {
      this.logger.log(Level.FINE, "Header [" + headerFile.toString() + "] found!");
      boolean dataOk = true;
      simulatorHeader = new SimulatorHeader();
      try {
        BufferedReader in = new BufferedReader(new FileReader(headerFile.getAbsolutePath()));
        String line;
        label:
        while ((line = in.readLine()) != null) {

          List<String> items = Arrays.asList(line.split("="));
          if (line.startsWith("#")) {
            // nothing to be done
          } else if (items.size() != 2) {
            this.logger.log(Level.SEVERE,
                "Read from header file: size of line [" + line + "]  was invalid!");
            dataOk = false;
            break;
          } else {
            String fieldName, fieldValue;
            fieldName = items.get(0);
            fieldValue = items.get(1);

            switch (fieldName) {
              case "startModels":
                simulatorHeader.setAutoStartSystem(Boolean.parseBoolean(fieldValue));
                break;
              case "startTime":
                simulatorHeader.setAutoStartTime(Boolean.parseBoolean(fieldValue));
                break;
              case "timeFactor":
                int newTimeFactor = Integer.parseInt(fieldValue);
                if (simulatorHeader.validateTimeFactor(newTimeFactor)) {
                  simulatorHeader.setTimeFactor(newTimeFactor);
                } else {
                  this.logger.log(Level.SEVERE, "Read from header file: timeFactor is invalid!");
                  dataOk = false;
                  break label;
                }
                break;
              case "startDate":
                Date startDate = simulatorHeader.parseStringIntoDate(fieldValue);
                if (startDate != null) {
                  simulatorHeader.setStartDate(startDate);
                } else {
                  this.logger.log(Level.SEVERE, "Read from header file: startDate is invalid!");
                  dataOk = false;
                  break label;
                }
                break;
              case "endDate":
                Date endDate = simulatorHeader.parseStringIntoDate(fieldValue);
                if (endDate != null) {
                  simulatorHeader.setEndDate(endDate);
                } else {
                  this.logger.log(Level.SEVERE, "Read from header file: endDate is invalid!");
                  dataOk = false;
                  break label;
                }
                break;
              case "keplerElements":
                simulatorHeader.setKeplerElements(String.valueOf(fieldValue));
                break;
              case "orekit":
                simulatorHeader.setUseOrekitPropagator(Boolean.parseBoolean(fieldValue));
                break;
              case "orekitPropagator":
                simulatorHeader.setOrekitPropagator(String.valueOf(fieldValue));
                break;
              case "orekitTLE1": {
                String tempResult = String.valueOf(fieldValue);
                simulatorHeader.setOrekitTLE1(tempResult.substring(1, tempResult.length() - 1));
                break;
              }
              case "orekitTLE2": {
                String tempResult = String.valueOf(fieldValue);
                simulatorHeader.setOrekitTLE2(tempResult.substring(1, tempResult.length() - 1));
                break;
              }
              case "celestia":
                simulatorHeader.setUseCelestia(Boolean.parseBoolean(fieldValue));
                break;
              case "celestiaPort":
                simulatorHeader.setCelestiaPort(Integer.parseInt(fieldValue));
                break;
              case "updateFromInternet":
                simulatorHeader.setUpdateInternet(Boolean.parseBoolean(fieldValue));
                break;
            }
          }
          // validate start is before after

        }
        if (!simulatorHeader.checkStartBeforeEnd()) {
          this.logger.log(Level.SEVERE, "Read from header file: startDate is not before endDate");
          dataOk = false;

        }
        in.close();
      } catch (IOException ex) {
        Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
      }
      if (!dataOk) {
        this.logger.log(Level.FINE, "Data from header file was invalid!");
        initializeHeader(headerFile);
      }

    } else {
      this.logger.log(Level.FINE, "Header file was not found!");
      initializeHeader(headerFile);
    }
  }

  private void initializeFilter(File filterFile)
  {
    this.logger.log(Level.FINE, "initializeFilter");
    writeFilter(filterFile);
  }

  private void initializeHeader(File headerFile)
  {
    this.logger.log(Level.FINE, "initializeHeader");
    simulatorHeader = new SimulatorHeader();
    writeHeader(headerFile);
  }

  private void initializeTemplates()
  {
    this.logger.log(Level.FINE, "initializeTemplates");
    writeTemplatesToFile(null);
  }

  private void initializeScheduler()
  {
    this.logger.log(Level.FINE, "initializeScheduler");
    writeSchedulerToFile(null);
  }

  private void writeHeader(File headerFile)
  {
    BufferedWriter out = null;
    try {
      out = new BufferedWriter(new FileWriter(headerFile.getAbsolutePath()));
      out.write(simulatorHeader.toFileString());
    } catch (IOException ex) {
      Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      try {
        out.close();
      } catch (IOException ex) {
        Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  private void writeFilter(File filterFile)
  {
    BufferedWriter out = null;
    try {
      out = new BufferedWriter(new FileWriter(filterFile.getAbsolutePath()));
      // out.write("#1001 - ");
    } catch (IOException ex) {
      Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      try {
        out.close();
      } catch (IOException ex) {
        Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  private void writeTemplate(String data, BufferedWriter out)
  {

    try {
      out.write(data + "\n");

    } catch (IOException ex) {
      Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private boolean checkInternalIDExists(int internalID)
  {
    boolean found = false;

    for (CommandDescriptor c : commandsList) {
      if (c.getInternalID() == internalID) {
        found = true;
        break;
      }
    }
    return found;
  }

  private CommandDescriptor getCommandDescriptorForID(int internalID)
  {
    for (CommandDescriptor c : commandsList) {
      if (c.getInternalID() == internalID) {
        return c;
      }
    }
    return null;
  }

  private boolean checkArgumentTemplateExists(int internalID, String templateName)
  {
    boolean found = false;
    for (CommandDescriptor c : commandsList) {
      if (c.getInternalID() == internalID) {
        for (ArgumentTemplate t : c.getTemplateList()) {
          if (t.getDescription().equals(templateName)) {
            found = true;
            break;
          }
        }
        if (found) {
          break;
        }

      }
    }
    return found;
  }

  private boolean putCustomTemplateInCollection(int internalID, ArgumentTemplate template)
  {
    boolean found = false;

    for (CommandDescriptor c : commandsList) {
      if (c.getInternalID() == internalID) {
        found = true;
        this.logger.finer(
            "Adding template [" + template.toString() + "] to command [" + c.toString() + "]");
        c.addNewTemplate(template);
      }
    }
    return found;
  }

  private void loadSchedulerFromFile(final File folder)
  {
    BufferedWriter outScheduler = null;
    File schedulerFile = null;
    schedulerData = new LinkedList<>();
    boolean errorsExist = false;
    boolean minorErrorsExist = false;
    boolean sortingRequired = false;
    long currentTime = 0;
    if (folder.exists()) {
      int customSchedulerCommands = 0;
      try {
        BufferedReader in = new BufferedReader(new FileReader(folder.getAbsolutePath()));

        schedulerFile = getSchedulerFileAsBackup();
        try {
          outScheduler = new BufferedWriter(new FileWriter(schedulerFile.getAbsolutePath()));
        } catch (IOException ex) {
          Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
        }

        String line;
        while ((line = in.readLine()) != null) {
          outScheduler.write(line + "\n");
          if (!line.startsWith("#")) {
            List<String> items = Arrays
                .asList(line.split("[" + CommandDescriptor.SEPARATOR_DATAFILES + "]"));
            if (items.size() == 4) {
              // 00000:00:00:00|0000000000|1000 |GPGGA
              // DDDDD:HH:MM:SS|[ms] |intID |template name
              long def1Value = SimulatorSchedulerPiece.getMillisFromDDDDDHHMMSSmmm(items.get(0));
              long def2Value = -1;
              try {
                def2Value = Long.parseLong(items.get(1));
              } catch (NumberFormatException ex) {
                Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
              }
              boolean rowOK = def1Value >= 0 && !(def1Value == 0 && def2Value > 0);
              if (!rowOK) {
                // use def2Value if it is ok
                if (def2Value >= 0) {
                  def1Value = def2Value;
                  rowOK = true;
                  minorErrorsExist = true;
                } else {
                  // both values are not valid, disregard this row
                }
              }
              if (def1Value == def2Value) {
                // time definition is consistent
              } else {
                // def2Value is not matching a valid def1Value, update it
                def2Value = def1Value;
                minorErrorsExist = true;
              }
              int internalID = 0;
              if (rowOK) {
                if (def1Value < currentTime) {
                  sortingRequired = true;
                } else {
                  currentTime = def1Value;
                }
                try {
                  internalID = Integer.parseInt(items.get(2));
                } catch (NumberFormatException ex) {
                  Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
                  rowOK = false;
                }
                // Validate internal ID
                rowOK = rowOK && checkInternalIDExists(internalID);
                if (rowOK) {
                  // Validate argument template
                  rowOK = checkArgumentTemplateExists(internalID, items.get(3));
                  if (!rowOK) {
                    this.logger.log(Level.FINE,
                        "argumentTemplate [" + items.get(3) + "] does not exist");
                  }
                } else {
                  this.logger.log(Level.FINE, "internalID [" + internalID + "] does not exist");
                }

              }
              if (!rowOK) {
                this.logger.log(Level.WARNING, "Validation1 error with row [" + line + "]");
                errorsExist = true;
              } else {
                customSchedulerCommands++;
                schedulerData.add(new SimulatorSchedulerPiece(def2Value, internalID, items.get(3)));
              }
            } else {
              this.logger.log(Level.WARNING, "Validation2 error with row [" + line + "]");
              errorsExist = true;
            }
          }
        }
        in.close();
      } catch (IOException ex) {
        Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
      }
      this.logger.log(Level.FINE, "Loaded [" + customSchedulerCommands + "] scheduler commands");
    } else {
      this.logger.log(Level.WARNING, "Scheduler file [" + folder.getName() + "] not found!");
      initializeScheduler();
    }
    if (errorsExist || minorErrorsExist || sortingRequired) {
      this.logger.log(Level.WARNING, "Errors were found during scheduler file parsing!");
      if (minorErrorsExist) {
        this.logger.log(Level.WARNING, "Some time fields were not consistent.");
      }
      if (sortingRequired) {
        this.logger.log(Level.WARNING, "Sorting of the scheduler entries is required.");
      }
      /*
       * schedulerData.sort(new Comparator<SimulatorSchedulerPiece>() {
       * 
       * @Override public int compare(SimulatorSchedulerPiece o1,
       * SimulatorSchedulerPiece o2) { if (o1.getTime() > o2.getTime()) { return 1; }
       * else if (o1.getTime() == o2.getTime()) { return 0; } else { return -1; } }
       * });
       */
      java.util.Collections.sort(schedulerData, (o1, o2) -> {
        if (o1.getTime() > o2.getTime()) {
          return 1;
        } else if (o1.getTime() == o2.getTime()) {
          return 0;
        } else {
          return -1;
        }
      });

      writeSchedulerToFile(schedulerData);
      printSchedulerData();
    } else {
      this.logger.log(Level.FINE, "Scheduler file parsing ok!");
      if (schedulerFile != null) {
        try {
          outScheduler.close();
          outScheduler = null;
//                    Files.delete(schedulerFile.toPath());
          schedulerFile.delete();
        } catch (IOException ex) {
          Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    if (outScheduler != null) {
      try {
        outScheduler.close();
      } catch (IOException ex) {
        Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  private void printSchedulerData()
  {
    this.logger.log(Level.INFO, "Printing scheduler data");
    for (SimulatorSchedulerPiece p : schedulerData) {
      this.logger.log(Level.FINE, p.getFileString());
    }
  }

  private void loadTemplatesFromFile(final File folder)
  {
    if (folder.exists()) {
      int customTemplates = 0;
      try {
        BufferedReader in = new BufferedReader(new FileReader(folder.getAbsolutePath()));
        String line;
        while ((line = in.readLine()) != null) {
          if (!line.startsWith("#")) {
            List<String> items = Arrays
                .asList(line.split("[" + CommandDescriptor.SEPARATOR_DATAFILES + "]"));
            if (items.size() == 3) {
              ArgumentTemplate template = new ArgumentTemplate(items.get(1), items.get(2));
              if (!putCustomTemplateInCollection(Integer.parseInt(items.get(0)), template)) {
                this.logger.log(Level.WARNING,
                    "Error finding internal id [" + items.get(0) + "] in row [" + line + "]");
              } else {
                customTemplates++;
              }
            } else {
              this.logger.log(Level.WARNING, "Error reading template file row [" + line + "]");
            }
          }
        }
        in.close();
      } catch (IOException ex) {
        Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
      }
      this.logger.log(Level.FINE, "Loaded [" + customTemplates + "] custom templates");
    } else {
      this.logger.log(Level.WARNING, "Templates file [" + folder.getName() + "] not found!");
      initializeTemplates();
    }

  }

  public static boolean isInteger(String s)
  {
    try {
      Integer.parseInt(s);
    } catch (NumberFormatException | NullPointerException e) {
      return false;
    }
    // only got here if we didn't return false
    return true;
  }

  private void writeSchedulerToFile(Object obj)
  {
    BufferedWriter outScheduler = null;
    File schedulerFile = getSchedulerFile();
    try {
      outScheduler = new BufferedWriter(new FileWriter(schedulerFile.getAbsolutePath()));
    } catch (IOException ex) {
      Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
    }
    try {
      outScheduler.write("#Simulator scheduler data file\n");
      outScheduler.write(
          "#The commands below will be executed when simulator time has exceeded the defined time\n");
      outScheduler.write(
          "#There are two possible ways to define the command, either as a DDDDD:HH:MM:SS:mmm format or directly in milliseconds value\n");
      outScheduler.write(
          "#The simulator shall check the agreement between the two fields, if not equal, the DDDDD:HH:MM:SS:mmm will be used\n");
      outScheduler
          .write("#If the DDDDD:HH:MM:SS:mmm is zero, the milliseconds value will be used\n");
      outScheduler
          .write("#The simulator shall also sort the data file chronologically ascending\n");
      outScheduler.write(
          "#days:hours:minutes:seconds:milliseconds|milliseconds|internalID|argument_template_name\n");
      outScheduler.write("#00000:00:00:00:000|0000000000000000000|1001|CUSTOM\n");
    } catch (IOException ex) {
      Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
    }

    if (obj != null) {
      for (SimulatorSchedulerPiece s : schedulerData) {
        try {
          outScheduler.write(s.getFileString() + "\n");
        } catch (IOException ex) {
          Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    try {
      outScheduler.close();
    } catch (IOException ex) {
      Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void writeTemplatesToFile(Object obj)
  {
    BufferedWriter outTemplates = null;
    File templatesFile = getTemplatesFile();
    try {
      outTemplates = new BufferedWriter(new FileWriter(templatesFile.getAbsolutePath()));
    } catch (IOException ex) {
      Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
    }
    if (obj != null) {
      LinkedList<CommandDescriptor> castedObj = (LinkedList<CommandDescriptor>) obj;
      for (CommandDescriptor c : castedObj) {
        for (ArgumentTemplate t : c.getTemplateList()) {
          if (!t.getDescription().equals(CommandDescriptor.KEYWORD_DEFAULT)) {
            writeTemplate(c.getInternalID() + CommandDescriptor.SEPARATOR_DATAFILES + t.toString(),
                outTemplates);
          }
        }
      }
      try {
        outTemplates.close();
      } catch (IOException ex) {
        Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
      }
    } else {
      try {
        outTemplates
            .write("#<internalID>|<templateName>|<inputArgs=[String inputArgument={data}>]\n");
        outTemplates.write("#2001|GLMLA|inputArgs=[String inputSentence={GLMLA}]");
      } catch (IOException ex) {
        Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
      }
      try {
        outTemplates.close();
      } catch (IOException ex) {
        Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  @Override
  void dataIn(Object obj)
  {
    if (obj instanceof PlatformMessage) {
      PlatformMessage msg = (PlatformMessage) obj;
      this.platformProperties.put(msg.getKey(), msg.getValue());
    }
    if (obj instanceof Properties) { // send platform properties back to client
      this.queueOut.add(platformProperties);
    }
    if (obj instanceof String) {
      String data = (String) obj;
      this.logger.log(Level.FINE, data);
      if (data.equals("ToggleStartStop")) {
        simulatorData.toggleSimulatorRunning();
      } else if (data.equals("List")) {
        sendHeader = true;
        sendList = true;
      } else if (data.equals("TogglePauseResume")) {
        simulatorData.toggleTimeRunning();
      } else if (data.startsWith("TimeFactor")) {
        String[] bits = data.split(":");
        simulatorData.setTimeFactor(Integer.parseInt(bits[bits.length - 1]));
      }
    } else if (obj instanceof CommandDescriptor) {
      commandsQueue.offer((CommandDescriptor) obj);
    } else if (obj instanceof LinkedList) {
      writeTemplatesToFile(obj);
      commandsList.clear();
      simulatorDevices.clear();
      loadMethodsFromReflection();
      loadTemplatesFromFile(getTemplatesFile());
      loadSimulatorCommandsFilter();
      makeSimulatorDeviceBindings();
      sendList = true;
    } else if (obj instanceof SimulatorHeader) {
      this.logger.log(Level.FINE, "SimulatorNode Received " + ((SimulatorHeader) obj).toString());
      simulatorHeader = (SimulatorHeader) obj;
      simulatorData.initFromHeader(simulatorHeader);
      sendHeader = true;
      writeHeader(getHeaderFile());
      initModels();
    }

  }

  public static String dump(Object o, int callCount)
  {
    callCount++;
    StringBuffer tabs = new StringBuffer();
    for (int k = 0; k < callCount; k++) {
      tabs.append("\t");
    }
    StringBuffer buffer = new StringBuffer();
    Class oClass = o.getClass();
    if (oClass.isArray()) {
      buffer.append("\n");
      buffer.append(tabs.toString());
      buffer.append("[");
      for (int i = 0; i < Array.getLength(o); i++) {
        if (i < 0) {
          buffer.append(",");
        }
        Object value = Array.get(o, i);
        if (value.getClass().isPrimitive() || value.getClass() == java.lang.Long.class
            || value.getClass() == java.lang.String.class
            || value.getClass() == java.lang.Integer.class
            || value.getClass() == java.lang.Boolean.class) {
          buffer.append(value);
        } else {
          buffer.append(dump(value, callCount));
        }
      }
      buffer.append(tabs.toString());
      buffer.append("]\n");
    } else {
      buffer.append("\n");
      buffer.append(tabs.toString());
      buffer.append("{\n");
      while (oClass != null) {
        Field[] fields = oClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
          buffer.append(tabs.toString());
          fields[i].setAccessible(true);
          buffer.append(fields[i].getName());
          buffer.append("=");
          try {
            Object value = fields[i].get(o);
            if (value != null) {
              if (value.getClass().isPrimitive() || value.getClass() == java.lang.Long.class
                  || value.getClass() == java.lang.String.class
                  || value.getClass() == java.lang.Integer.class
                  || value.getClass() == java.lang.Boolean.class) {
                buffer.append(value);
              } else {
                buffer.append(dump(value, callCount));
              }
            }
          } catch (IllegalAccessException e) {
            buffer.append(e.getMessage());
          }
          buffer.append("\n");
        }
        oClass = oClass.getSuperclass();
      }
      buffer.append(tabs.toString());
      buffer.append("}\n");
    }
    return buffer.toString();
  }

  private String getArgDescriptionForSchedulerPiece(SimulatorSchedulerPiece piece)
  {
    for (CommandDescriptor c : commandsList) {
      if (c.getInternalID() == piece.getInternalID()) {
        for (ArgumentTemplate t : c.getTemplateList()) {
          if (t.getDescription().equals(piece.getArgumentTemplateDescription())) {
            return t.getArgContent();
          }
        }
      }
    }
    return "";
  }

  void schedulerPollData()
  {
    boolean stillHasData = true;
    while (stillHasData) {
      if (schedulerDataIndex < schedulerData.size()) {
        SimulatorSchedulerPiece piece = schedulerData.get(schedulerDataIndex);
        if (simulatorData.getCurrentTimeMillis() >= piece.getTime()) {
          runGenericMethod(piece.getInternalID(), getArgDescriptionForSchedulerPiece(piece));
          piece.setExecuted(true);
          schedulerDataIndex++;
        } else {
          stillHasData = false;
        }

      } else {
        stillHasData = false;
      }
    }
  }

  @Override
  void coreRun()
  {
    if (simulatorData.isSimulatorRunning()) {

      long timeElapsed = 0;
      if (simulatorData.isTimeRunning()) {
        timeElapsed = super.getTimeElapsed();
      }
      counter++;
      simulatorData.setCounter(counter);
      simulatorData.feedTimeElapsed(timeElapsed);
      // BENCHMARK_START_COUNTER=10000;
      // BENCHMARK_COUNTER_EVALUATIONS=500;
      if (!benchmarkFinished) {
        if (counter >= BENCHMARK_START_COUNTER && !benchmarkInProgress) {
          benchmarkInProgress = true;
          if (this.simulatorHeader.isUseOrekitPropagator()) {
            this.orekitCore.setConstellationPropagationCounter(0);
          }
          this.logger.log(Level.FINE, "BenchmarkStart;Counter [" + counter + "]");
          this.logger.log(Level.FINE, "BenchmarkStartup;Counter [" + counter + "];TimeElapsed ["
              + (System.currentTimeMillis() - benchmarkStartupTime) + "] ms");

        }
        if (benchmarkInProgress) {
          benchmarkTimeElapsed += timeElapsed;
          if (counter >= BENCHMARK_START_COUNTER + BENCHMARK_COUNTER_EVALUATIONS) {
            benchmarkFinished = true;
            this.logger.log(Level.FINE, "BenchmarkFinished;TimeElapsed [" + benchmarkTimeElapsed
                + "] ms;Counter [" + counter + "];Steps [" + BENCHMARK_COUNTER_EVALUATIONS + "]");
            if (this.simulatorHeader.isUseOrekitPropagator()) {
              this.logger.log(Level.FINE,
                  "BenchmarkFinished;Orekit GPS constellation propagations ["
                  + this.orekitCore.getConstellationPropagationCounter() + "]");
            }
          }
        }
      }
      schedulerPollData();
      if (simulatorHeader.isUseOrekitPropagator()) {
        try {
          double ts = (timeElapsed) / 1000.0 * simulatorData.getTimeFactor();
          //System.out.println("Timestep: " + ts);
          orekitCore.processPropagateStep((timeElapsed) / 1000.0 * simulatorData.getTimeFactor());
        } catch (OrekitException ex) {
          Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    // Job queue
    CommandDescriptor c = commandsQueue.poll();
    if (c != null) {
      commandsResults.offer(runGenericMethodForCommand(c));
    }
  }

  @Override
  Object dataOut()
  {
    /*
     * if (super.getTimers().get(TIMER_SCIENCE1_DATA).isElapsed()) { if
     * (simulatorHeader.isUseOrekit()) { SimulatorSpacecraftState
     * simulatorSpacecraftState = getSpacecraftState(); GPSSatInViewScience
     * gpsScience = orekitCore.getSatsInViewScience(); StringBuilder result=new
     * StringBuilder(); result.append("Science1;GPS_Latitude;"+
     * simulatorSpacecraftState.getLatitude());
     * result.append(";GPS_Longitude;"+simulatorSpacecraftState.getLongitude());
     * result.append(";GPS_Altitude;"+simulatorSpacecraftState.getAltitude());
     * result.append(";GPS_SatsInView;"+this.orekitCore.getSatsNoInView());
     * result.append(";MinDist;"+gpsScience.getMinDistance()/1000);
     * result.append(";MaxDist;"+gpsScience.getMaxDistance()/1000);
     * result.append(";MinElevation;"+gpsScience.getMinElevation());
     * result.append(";MaxElevation;"+gpsScience.getMaxElevation());
     * result.append(";AvgDist;"+gpsScience.getAvgDistance()/1000);
     * result.append(";AvgElevation;"+gpsScience.getAvgElevation());
     * result.append(";StdDevDist;"+gpsScience.getStdDevDistance()/1000);
     * result.append(";StdDevElevation;"+gpsScience.getStdDevElevation());
     * result.append(";SimulatorTime;"+ simulatorData.getUTCCurrentTime2());
     * this.logger.log(Level.INFO,result.toString()); } }
     */
    CommandResult commandResult = commandsResults.poll();
    if (commandResult != null) {
      return commandResult;
    }
    if (sendHeader) {
      sendHeader = false;
      return simulatorHeader;
    }
    if (sendList) {
      sendList = false;
      LinkedList<Object> newDataOut = new LinkedList<>();
      newDataOut.addAll(commandsList);
      newDataOut.addAll(simulatorDevices);
      return newDataOut;
    }
    if (super.getTimers().get(TIMER_SIMULATOR_DATA).isElapsed()) {
      return simulatorData;
    }
    if (super.getTimers().get(TIMER_SCHEDULER_DATA).isElapsed()) {
      return schedulerData;
    }
    if (super.getTimers().get(TIMER_CELESTIA_DATA).isElapsed() && simulatorHeader.isUseCelestia()
        && simulatorHeader.isUseOrekitPropagator()) {
      if (orekitCore.isIsInitialized()) {
        SimulatorSpacecraftState simulatorSpacecraftState = getSpacecraftState();
        CelestiaData celestiaData = new CelestiaData(simulatorSpacecraftState.getRv(),
            simulatorSpacecraftState.getQ());
        celestiaData.setDate(simulatorData.getCurrentTime());
        celestiaData.setAnx(orekitCore.getNextAnx());
        celestiaData.setDnx(orekitCore.getNextDnx());
        celestiaData.setAos(orekitCore.getNextAOS());
        celestiaData.setLos(orekitCore.getNextLOS());
        celestiaData.setInfo(
            "Time|x" + this.simulatorData.getTimeFactor() + "|" + orekitCore.getOrekitInfo());
        return celestiaData;
      }
    }
    if (super.getTimers().get(TIMER_DEVICE_DATA).isElapsed()) {

      this.hMapSDData.get(DevDatPBind.Camera_CameraBuffer)
          .setType(this.cameraBuffer.getDataBufferAsString());
      this.hMapSDData.get(DevDatPBind.Camera_CameraBufferOperatingIndex)
          .setType(this.cameraBuffer.getOperatingIndex());
      SimulatorSpacecraftState simulatorSpacecraftState = getSpacecraftState();
      float[] p = new float[3];
      float[] v = new float[3];
      float[] q = new float[4];
      p = simulatorSpacecraftState.getR();
      v = simulatorSpacecraftState.getV();
      q = simulatorSpacecraftState.getQ();
      this.hMapSDData.get(DevDatPBind.FineADCS_ModeOperation)
          .setType(simulatorSpacecraftState.getModeOperation());
      this.hMapSDData.get(DevDatPBind.FineADCS_PositionInertial).setType(p);
      this.hMapSDData.get(DevDatPBind.FineADCS_VelocityInertial).setType(v);
      this.hMapSDData.get(DevDatPBind.FineADCS_Q1).setType(String.valueOf(q[0]));
      this.hMapSDData.get(DevDatPBind.FineADCS_Q2).setType(String.valueOf(q[1]));
      this.hMapSDData.get(DevDatPBind.FineADCS_Q3).setType(String.valueOf(q[2]));
      this.hMapSDData.get(DevDatPBind.FineADCS_Q4).setType(String.valueOf(q[3]));
      this.hMapSDData.get(DevDatPBind.FineADCS_MagneticField)
          .setType(String.valueOf(simulatorSpacecraftState.getMagField()));
      this.hMapSDData.get(DevDatPBind.FineADCS_Rotation)
          .setType(simulatorSpacecraftState.getRotationAsString());
      this.hMapSDData.get(DevDatPBind.FineADCS_Magnetometer)
          .setType(String.valueOf(simulatorSpacecraftState.getMagnetometerAsString()));
      this.hMapSDData.get(DevDatPBind.FineADCS_SunVector)
          .setType(String.valueOf(simulatorSpacecraftState.getSunVectorAsString()));

      this.hMapSDData.get(DevDatPBind.GPS_Latitude).setType(simulatorSpacecraftState.getLatitude());
      this.hMapSDData.get(DevDatPBind.GPS_Longitude)
          .setType(simulatorSpacecraftState.getLongitude());
      this.hMapSDData.get(DevDatPBind.GPS_Altitude)
          .setType(String.valueOf(simulatorSpacecraftState.getAltitude()));
      if (simulatorHeader.isUseOrekitPropagator()) {
        this.hMapSDData.get(DevDatPBind.GPS_GS_Elevation)
            .setType(orekitCore.getCurrentGSElevation());
        this.hMapSDData.get(DevDatPBind.GPS_GS_Azimuth).setType(orekitCore.getCurrentGSAzimuth());
        this.hMapSDData.get(DevDatPBind.GPS_SatsInView)
            .setType(String.valueOf(orekitCore.getSatsInView()));
      }
      this.hMapSDData.get(DevDatPBind.GPS_Altitude)
          .setType(String.valueOf(simulatorSpacecraftState.getAltitude()));
      this.hMapSDData.get(DevDatPBind.OpticalReceiver_OperatingBuffer).setType(
          this.opticalReceiverModel.getSingleStreamOperatingBuffer().getDataBufferAsString());
      this.hMapSDData.get(DevDatPBind.OpticalReceiver_OperatingBufferIndex)
          .setType(this.opticalReceiverModel.getSingleStreamOperatingBuffer().getOperatingIndex());
      this.hMapSDData.get(DevDatPBind.OpticalReceiver_DegradationRate)
          .setType(this.opticalReceiverModel.getDegradationRate());

      this.hMapSDData.get(DevDatPBind.SDR_OperatingBuffer)
          .setType(this.sdrBuffer.getDataBufferAsString());
      this.hMapSDData.get(DevDatPBind.SDR_OperatingBufferIndex)
          .setType(this.sdrBuffer.getOperatingIndex());

      return simulatorDevices;
    }
    return null;
  }

  public SimulatorSpacecraftState getSpacecraftState()
  {
    if (this.simulatorHeader.isUseOrekitPropagator()) {
      GeodeticPoint result = this.orekitCore.getGeodeticPoint();
      SimulatorSpacecraftState data = new SimulatorSpacecraftState(
          toDegrees(result.getLatitude()), toDegrees(result.getLongitude()),
          result.getAltitude());
      data.setRv(this.orekitCore.getOrbit().getPVCoordinates().getPosition(),
          this.orekitCore.getOrbit().getPVCoordinates().getVelocity());
      float q[] = new float[4];
      orekitCore.putQuaternionsInVector(q);
      data.setQ(q);
      data.setMagField(orekitCore.getMagneticField());
      data.setMagnetometer(orekitCore.getMagnetometer());
      data.setRotation(orekitCore.getAttitudeRotation());
      data.setSunVector(orekitCore.getSunVector());
      data.setSatsInView(this.orekitCore.getNumberSatsInView());
      data.setModeOperation(orekitCore.getAttitudeMode().toString());

      if (quaternionTcpServer != null) {
        String qData = quaternionTcpServer.getData();
        if (qData != null) {

          String[] quaternions = qData.split(" ");
          if (quaternions.length == 4) {
            q[0] = Float.parseFloat(quaternions[0]);
            q[1] = Float.parseFloat(quaternions[1]);
            q[2] = Float.parseFloat(quaternions[2]);
            q[3] = Float.parseFloat(quaternions[3]);
            data.setQ(q);
          } else if (quaternions.length == 3) {
            // 0 heading
            // 1 roll
            // 2 pitch
            double yaw = Double.parseDouble(quaternions[0]);
            double pitch = Double.parseDouble(quaternions[2]);
            double roll = Double.parseDouble(quaternions[1]);
            // System.out.println("yaw=["+yaw+"] pitch=["+pitch+"] roll=["+roll+"]");
            this.orekitCore.putQuaternionsInVectorFromYPR(yaw, pitch, roll, q);
            data.setQ(q);
          }
        }
      }

      return data;

    } else {
      Orbit.OrbitParameters orbitData = this.gps.getPosition(simulatorData.getCurrentTime());
      return new SimulatorSpacecraftState(orbitData.getlatitude(), orbitData.getlongitude(),
          orbitData.getGPSaltitude() * 1000);
    }
  }

  // Globals
  public Object runGenericMethod(int internalID, ArrayList<Object> argObject)
  {
    CommandDescriptor c = new CommandDescriptor("external", "external", "external", internalID,
        super.getLogObject());
    c.setInputArgsFromArrayList(argObject);
    CommandResult r = runGenericMethodForCommand(c);
    return r.getOutput();
  }

  public Object runGenericMethod(int internalID, String argObjectDescription)
  {
    CommandDescriptor command = null;
    for (CommandDescriptor c : commandsList) {
      if (c.getInternalID() == internalID) {
        c.setInputArgsFromString(argObjectDescription);
        command = c;
        break;
      }
    }
    CommandResult r = runGenericMethodForCommand(command);
    return r.getOutput();
  }

  public CommandResult runGenericMethodForCommand(CommandDescriptor c)
  {
    simulatorData.incrementMethods();
    ArrayList<Object> argObject = c.getInputArgObjList();
    this.logger.log(Level.FINE,
        "runGenericMethod;identifier;" + c.getInternalID() + ";" + c.getInputArgs());
    CommandResult commandResult = new CommandResult(c, new Date(), simulatorData.getCurrentTime());
    Object globalResult = null;
    try {

      switch (c.getInternalID()) {

        case 1001: {// Origin [IFineADCS] Method [byte[] runRawCommand(int cmdID,byte[] data,int
          // iAD);//1001//Low level command to interact with FineADCS]
          int cmdID = (Integer) argObject.get(0);
          byte[] data = (byte[]) argObject.get(1);
          int iAD = (Integer) argObject.get(2);
          byte[] result = new byte[0];
          globalResult = result;
          break;
        }
        case 1002: {// Origin [IFineADCS] Method [byte[] Identify();//1002//High level command to
          // interact with FineADCS]
          byte[] result = new byte[8];
          globalResult = result;
          break;
        }
        case 1003: {// Origin [IFineADCS] Method [void SoftwareReset();//1003//High level command to
          // interact with FineADCS]
          break;
        }
        case 1004: {// Origin [IFineADCS] Method [void I2CReset();//1004//High level command to
          // interact with FineADCS]
          break;
        }
        case 1005: {// Origin [IFineADCS] Method [void SetDateTime(long seconds,int
          // subseconds);//1005//High level command to interact with FineADCS]
          long seconds = (Long) argObject.get(0);
          int subseconds = (Integer) argObject.get(1);
          break;
        }
        case 1006: {// Origin [IFineADCS] Method [byte[] GetDateTime();//1006//High level command to
          // interact with FineADCS]
          byte[] result = new byte[6];
          globalResult = result;
          break;
        }
        case 1007: {// Origin [IFineADCS] Method [void iADCSPowerCycle(byte onoff,byte
          // register);//1007//High level command to interact with FineADCS]
          byte onoff = (Byte) argObject.get(0);
          byte register = (Byte) argObject.get(1);
          break;
        }
        case 1008: {// Origin [IFineADCS] Method [void SetOperationMode(byte opmode);//1008//High
          // level command to interact with FineADCS]
          byte opmode = (Byte) argObject.get(0);
          break;
        }
        case 1009: {// Origin [IFineADCS] Method [void SetPowerUpdateInterval(long
          // miliseconds);//1009//High level command to interact with FineADCS]
          long miliseconds = (Long) argObject.get(0);
          break;
        }
        case 1010: {// Origin [IFineADCS] Method [void SetTemperatureUpdateInterval(long
          // miliseconds);//1010//High level command to interact with FineADCS]
          long miliseconds = (Long) argObject.get(0);
          break;
        }
        case 1011: {// Origin [IFineADCS] Method [byte[] GetStandardTelemetry();//1011//High level
          // command to interact with FineADCS]
          byte[] result = new byte[60];
          globalResult = result;
          break;
        }
        case 1012: {// Origin [IFineADCS] Method [byte[] GetExtendedTelemetry();//1012//High level
          // command to interact with FineADCS]
          byte[] result = new byte[183];
          globalResult = result;
          break;
        }
        case 1013: {// Origin [IFineADCS] Method [byte[] GetPowerStatus();//1013//High level command
          // to interact with FineADCS]
          byte[] result = new byte[24];
          globalResult = result;
          break;
        }
        case 1014: {// Origin [IFineADCS] Method [byte[] GetInfoTelemetry();//1014//High level
          // command to interact with FineADCS]
          byte[] result = new byte[158];
          globalResult = result;
          break;
        }
        case 1015: {// Origin [IFineADCS] Method [byte[] GetSensorTelemetry();//1015//High level
          // command to interact with FineADCS]
          byte[] result = new byte[74];
          SimulatorSpacecraftState spacecraftState = getSpacecraftState();
          float[] magneticField = spacecraftState.getMagnetometer();
          FWRefFineADCS.putFloatInByteArray(magneticField[0],
              FWRefFineADCS.SENSORTM_IDX.MAG_FIELD_X, result);
          FWRefFineADCS.putFloatInByteArray(magneticField[1],
              FWRefFineADCS.SENSORTM_IDX.MAG_FIELD_Y, result);
          FWRefFineADCS.putFloatInByteArray(magneticField[2],
              FWRefFineADCS.SENSORTM_IDX.MAG_FIELD_Z, result);

          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_Accelerometer).getTypeAsFloatByIndex(0),
              FWRefFineADCS.SENSORTM_IDX.ACCELEROMETER_X, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_Accelerometer).getTypeAsFloatByIndex(1),
              FWRefFineADCS.SENSORTM_IDX.ACCELEROMETER_Y, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_Accelerometer).getTypeAsFloatByIndex(2),
              FWRefFineADCS.SENSORTM_IDX.ACCELEROMETER_Z, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_Gyro1).getTypeAsFloatByIndex(0),
              FWRefFineADCS.SENSORTM_IDX.GYRO1_X, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_Gyro1).getTypeAsFloatByIndex(1),
              FWRefFineADCS.SENSORTM_IDX.GYRO1_Y, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_Gyro1).getTypeAsFloatByIndex(2),
              FWRefFineADCS.SENSORTM_IDX.GYRO1_Z, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_Gyro2).getTypeAsFloatByIndex(0),
              FWRefFineADCS.SENSORTM_IDX.GYRO2_X, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_Gyro2).getTypeAsFloatByIndex(1),
              FWRefFineADCS.SENSORTM_IDX.GYRO2_Y, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_Gyro2).getTypeAsFloatByIndex(2),
              FWRefFineADCS.SENSORTM_IDX.GYRO2_Z, result);
          float[] quaternions = spacecraftState.getQ();
          FWRefFineADCS.putFloatInByteArray(quaternions[0], FWRefFineADCS.SENSORTM_IDX.QUATERNION1,
              result);
          FWRefFineADCS.putFloatInByteArray(quaternions[1], FWRefFineADCS.SENSORTM_IDX.QUATERNION2,
              result);
          FWRefFineADCS.putFloatInByteArray(quaternions[2], FWRefFineADCS.SENSORTM_IDX.QUATERNION3,
              result);
          FWRefFineADCS.putFloatInByteArray(quaternions[3], FWRefFineADCS.SENSORTM_IDX.QUATERNION4,
              result);
          globalResult = result;
          break;
        }
        case 1016: {// Origin [IFineADCS] Method [byte[] GetActuatorTelemetry();//1016//High level
          // command to interact with FineADCS]
          byte[] result = new byte[21];
          FWRefFineADCS.putInt16InByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels).getTypeAsIntByIndex(0),
              FWRefFineADCS.ACTUATORTM_IDX.RW_CURRENT_SPEED_X, result);
          FWRefFineADCS.putInt16InByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels).getTypeAsIntByIndex(1),
              FWRefFineADCS.ACTUATORTM_IDX.RW_CURRENT_SPEED_Y, result);
          FWRefFineADCS.putInt16InByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels).getTypeAsIntByIndex(2),
              FWRefFineADCS.ACTUATORTM_IDX.RW_CURRENT_SPEED_Z, result);
          FWRefFineADCS.putInt16InByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_Magnetorquer).getTypeAsIntByIndex(0),
              FWRefFineADCS.ACTUATORTM_IDX.MTQ_TARGET_X, result);
          FWRefFineADCS.putInt16InByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_Magnetorquer).getTypeAsIntByIndex(1),
              FWRefFineADCS.ACTUATORTM_IDX.MTQ_TARGET_Y, result);
          FWRefFineADCS.putInt16InByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_Magnetorquer).getTypeAsIntByIndex(2),
              FWRefFineADCS.ACTUATORTM_IDX.MTQ_TARGET_Z, result);
          globalResult = result;

          break;
        }
        case 1017: {// Origin [IFineADCS] Method [byte[] GetAttitudeTelemetry();//1017//High level
          // command to interact with FineADCS]
          byte[] result = new byte[28];
          byte pointingLoopState = 1;
          if (this.simulatorHeader.isUseOrekitPropagator()) {
            pointingLoopState = orekitCore.getStateTarget();
          }
          FWRefFineADCS.putByteInByteArray(pointingLoopState,
            FWRefFineADCS.POINTING_LOOP_IDX.POINTING_LOOP_STATE, result);
          globalResult = result;
          break;
        }
        case 1018: {// Origin [IFineADCS] Method [byte[] GetExtendedSensorTelemetry();//1018//High
          // level command to interact with FineADCS]
          byte[] result = new byte[154];
          globalResult = result;
          break;
        }
        case 1019: {// Origin [IFineADCS] Method [byte[] GetExtendedActuatorTelemetry();//1019//High
          // level command to interact with FineADCS]
          byte[] result = new byte[53];
          globalResult = result;
          break;
        }
        case 1020: {// Origin [IFineADCS] Method [byte[] GetExtendedAttitudeTelemetry();//1020//High
          // level command to interact with FineADCS]
          byte[] result = new byte[36];
          globalResult = result;
          break;
        }
        case 1021: {// Origin [IFineADCS] Method [byte[] GetMagneticTelemetry();//1021//High level
          // command to interact with FineADCS]
          byte[] result = new byte[32];
          globalResult = result;
          break;
        }
        case 1022: {// Origin [IFineADCS] Method [byte[] GetSunTelemetry();//1022//High level
          // command to interact with FineADCS]
          byte[] result = new byte[13];
          globalResult = result;
          break;
        }
        case 1023: {// Origin [IFineADCS] Method [void SetThresholdValueForMagEmulation(int
          // value);//1023//High level command to interact with FineADCS]
          int value = (Integer) argObject.get(0);
          break;
        }
        case 1024: {// Origin [IFineADCS] Method [byte[]
          // GetThresholdValueForMagEmulation();//1024//High level command to interact
          // with FineADCS]
          byte[] result = new byte[4];
          globalResult = result;
          break;
        }
        case 1025: {// Origin [IFineADCS] Method [void ClearErrorRegister();//1025//High level
          // command to interact with FineADCS]
          break;
        }
        case 1026: {// Origin [IFineADCS] Method [byte[] GetSystemRegisters(byte
          // register);//1026//High level command to interact with FineADCS]
          byte register = (Byte) argObject.get(0);
          byte[] result = new byte[4];
          globalResult = result;
          break;
        }
        case 1027: {// Origin [IFineADCS] Method [byte[] GetControlRegisters(byte
          // register);//1027//High level command to interact with FineADCS]
          byte register = (Byte) argObject.get(0);
          byte[] result = new byte[4];
          globalResult = result;
          break;
        }
        case 1028: {// Origin [IFineADCS] Method [void SetSystemRegister(byte[] data);//1028//High
          // level command to interact with FineADCS]
          byte[] data = (byte[]) argObject.get(0);
          break;
        }
        case 1029: {// Origin [IFineADCS] Method [void ResetSystemRegister(byte[] data);//1029//High
          // level command to interact with FineADCS]
          byte[] data = (byte[]) argObject.get(0);
          break;
        }
        case 1030: {// Origin [IFineADCS] Method [void SetMemberUpdateInterval(byte memberID,long
          // interval);//1030//High level command to interact with FineADCS]
          byte memberID = (Byte) argObject.get(0);
          long interval = (Long) argObject.get(1);
          break;
        }
        case 1031: {// Origin [IFineADCS] Method [byte[] GetMemberUpdateInterval(byte
          // memberID);//1031//High level command to interact with FineADCS]
          byte memberID = (Byte) argObject.get(0);
          byte[] result = new byte[8];
          globalResult = result;
          break;
        }
        case 1032: {// Origin [IFineADCS] Method [void SetHILStatus(byte[]
          // HILStatusRegister);//1032//High level command to interact with FineADCS]
          byte[] HILStatusRegister = (byte[]) argObject.get(0);
          break;
        }
        case 1033: {// Origin [IFineADCS] Method [byte[] GetHILStatus();//1033//High level command
          // to interact with FineADCS]
          byte[] result = new byte[4];
          globalResult = result;
          break;
        }
        case 1034: {// Origin [IFineADCS] Method [void SetUpdateInterval(int interval);//1034//High
          // level command to interact with FineADCS]
          int interval = (Integer) argObject.get(0);
          break;
        }
        case 1035: {// Origin [IFineADCS] Method [void SetValuesToAllSensors(int[]
          // values);//1035//High level command to interact with FineADCS]
          int[] values = (int[]) argObject.get(0);
          break;
        }
        case 1036: {// Origin [IFineADCS] Method [byte[] GetValuesAllSensors();//1036//High level
          // command to interact with FineADCS]
          byte[] result = new byte[96];
          globalResult = result;
          break;
        }
        case 1037: {// Origin [IFineADCS] Method [void SetCalibrationParametersAllSensors(int[]
          // values);//1037//High level command to interact with FineADCS]
          int[] values = (int[]) argObject.get(0);
          break;
        }
        case 1038: {// Origin [IFineADCS] Method [byte[]
          // GetCalibrationParametersAllSensors();//1038//High level command to interact
          // with FineADCS]
          byte[] result = new byte[48];
          globalResult = result;
          break;
        }
        case 1039: {// Origin [IFineADCS] Method [void EnableCalibrationAllSensors();//1039//High
          // level command to interact with FineADCS]
          break;
        }
        case 1040: {// Origin [IFineADCS] Method [void DisableCalibrationAllSensors();//1040//High
          // level command to interact with FineADCS]
          break;
        }
        case 1041: {// Origin [IFineADCS] Method [void SetUpdateIntervalRW(int
          // interval);//1041//High level command to interact with FineADCS]
          int interval = (Integer) argObject.get(0);
          break;
        }
        case 1042: {// Origin [IFineADCS] Method [void SetSpeedToAllRWs(int[] values);//1042//High
          // level command to interact with FineADCS]
          int[] values = (int[]) argObject.get(0);
          break;
        }
        case 1043: {// Origin [IFineADCS] Method [byte[] GetSpeedAllRWs();//1043//High level command
          // to interact with FineADCS]
          byte[] result = new byte[6];
          globalResult = result;
          break;
        }
        case 1044: {// Origin [IFineADCS] Method [byte[] SetAccAllRWs(int[] values);//1044//High
          // level command to interact with FineADCS]
          int[] values = (int[]) argObject.get(0);
          byte[] result = new byte[6];
          globalResult = result;
          break;
        }
        case 1045: {// Origin [IFineADCS] Method [void SetSleepAllRWs(byte sleepMode);//1045//High
          // level command to interact with FineADCS]
          byte sleepMode = (Byte) argObject.get(0);
          break;
        }
        case 1046: {// Origin [IFineADCS] Method [void SetDipoleMomentAllMTQs(int[]
          // values);//1046//High level command to interact with FineADCS]
          int[] values = (int[]) argObject.get(0);
          break;
        }
        case 1047: {// Origin [IFineADCS] Method [byte[] GetDipoleMomentAllMTQs();//1047//High level
          // command to interact with FineADCS]
          byte[] result = new byte[6];
          globalResult = result;
          break;
        }
        case 1048: {// Origin [IFineADCS] Method [void SuspendAllMTQs();//1048//High level command
          // to interact with FineADCS]
          break;
        }
        case 1049: {// Origin [IFineADCS] Method [void ResumeAllMTQs();//1049//High level command to
          // interact with FineADCS]
          break;
        }
        case 1050: {// Origin [IFineADCS] Method [void ResetAllMTQs();//1050//High level command to
          // interact with FineADCS]
          break;
        }
        case 1051: {// Origin [IFineADCS] Method [void RunSelftTestAllMTQs();//1051//High level
          // command to interact with FineADCS]
          break;
        }
        case 1052: {// Origin [IFineADCS] Method [void SetMTQRelaxTime(int relaxtime);//1052//High
          // level command to interact with FineADCS]
          int relaxtime = (Integer) argObject.get(0);
          break;
        }
        case 1053: {// Origin [IFineADCS] Method [void StopAllMTQ();//1053//High level command to
          // interact with FineADCS]
          break;
        }
        case 1054: {// Origin [IFineADCS] Method [void MTQXSetDipoleMoment(int
          // dipoleValue);//1054//High level command to interact with FineADCS]
          int dipoleValue = (Integer) argObject.get(0);
          this.hMapSDData.get(DevDatPBind.FineADCS_Magnetorquer).setIntTypeByIndex(dipoleValue, 0);
          break;
        }
        case 1055: {// Origin [IFineADCS] Method [byte[] MTQXGetDipoleMoment();//1055//High level
          // command to interact with FineADCS]
          byte[] result = new byte[2];
          result = FWRefFineADCS.int16_2ByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_Magnetorquer).getTypeAsIntByIndex(0));
          globalResult = result;
          break;
        }
        case 1056: {// Origin [IFineADCS] Method [void MTQXSuspend();//1056//High level command to
          // interact with FineADCS]
          break;
        }
        case 1057: {// Origin [IFineADCS] Method [void MTQXResume();//1057//High level command to
          // interact with FineADCS]
          break;
        }
        case 1058: {// Origin [IFineADCS] Method [void MTQXRunSelfTest();//1058//High level command
          // to interact with FineADCS]
          break;
        }
        case 1059: {// Origin [IFineADCS] Method [void MTQXReset();//1059//High level command to
          // interact with FineADCS]
          break;
        }
        case 1060: {// Origin [IFineADCS] Method [void MTQXStop();//1060//High level command to
          // interact with FineADCS]
          break;
        }
        case 1061: {// Origin [IFineADCS] Method [void MTQYSetDipoleMoment(int
          // dipoleValue);//1061//High level command to interact with FineADCS]
          int dipoleValue = (Integer) argObject.get(0);
          this.hMapSDData.get(DevDatPBind.FineADCS_Magnetorquer).setIntTypeByIndex(dipoleValue, 1);
          break;
        }
        case 1062: {// Origin [IFineADCS] Method [byte[] MTQYGetDipoleMoment();//1062//High level
          // command to interact with FineADCS]
          byte[] result = new byte[2];
          globalResult = result;
          break;
        }
        case 1063: {// Origin [IFineADCS] Method [void MTQYSuspend();//1063//High level command to
          // interact with FineADCS]
          break;
        }
        case 1064: {// Origin [IFineADCS] Method [void MTQYResume();//1064//High level command to
          // interact with FineADCS]
          break;
        }
        case 1065: {// Origin [IFineADCS] Method [void MTQYRunSelfTest();//1065//High level command
          // to interact with FineADCS]
          break;
        }
        case 1066: {// Origin [IFineADCS] Method [void MTQYReset();//1066//High level command to
          // interact with FineADCS]
          break;
        }
        case 1067: {// Origin [IFineADCS] Method [void MTQYStop();//1067//High level command to
          // interact with FineADCS]
          break;
        }
        case 1068: {// Origin [IFineADCS] Method [void MTQZSetDipoleMoment(int
          // dipoleValue);//1068//High level command to interact with FineADCS]
          int dipoleValue = (Integer) argObject.get(0);
          this.hMapSDData.get(DevDatPBind.FineADCS_Magnetorquer).setIntTypeByIndex(dipoleValue, 2);
          break;
        }
        case 1069: {// Origin [IFineADCS] Method [byte[] MTQZGetDipoleMoment();//1069//High level
          // command to interact with FineADCS]
          byte[] result = new byte[2];
          globalResult = result;
          break;
        }
        case 1070: {// Origin [IFineADCS] Method [void MTQZSuspend();//1070//High level command to
          // interact with FineADCS]
          break;
        }
        case 1071: {// Origin [IFineADCS] Method [void MTQZResume();//1071//High level command to
          // interact with FineADCS]
          break;
        }
        case 1072: {// Origin [IFineADCS] Method [void MTQZRunSelfTest();//1072//High level command
          // to interact with FineADCS]
          break;
        }
        case 1073: {// Origin [IFineADCS] Method [void MTQZReset();//1073//High level command to
          // interact with FineADCS]
          break;
        }
        case 1074: {// Origin [IFineADCS] Method [void MTQZStop();//1074//High level command to
          // interact with FineADCS]
          break;
        }
        case 1075: {// Origin [IFineADCS] Method [void SetRWXSpeed(int speedValue);//1075//High
          // level command to interact with FineADCS]
          int speedValue = (Integer) argObject.get(0);
          this.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels).setIntTypeByIndex(speedValue, 0);

          break;
        }
        case 1076: {// Origin [IFineADCS] Method [byte[] GetRWXSpeed();//1076//High level command to
          // interact with FineADCS]
          byte[] result = new byte[2];
          globalResult = result;
          break;
        }
        case 1077: {// Origin [IFineADCS] Method [void SetRWXAcceleration(int
          // accelerationValue);//1077//High level command to interact with FineADCS]
          int accelerationValue = (Integer) argObject.get(0);
          break;
        }
        case 1078: {// Origin [IFineADCS] Method [void SetRWXSleep(byte sleepMode);//1078//High
          // level command to interact with FineADCS]
          byte sleepMode = (Byte) argObject.get(0);
          break;
        }
        case 1079: {// Origin [IFineADCS] Method [byte[] GetRWXID();//1079//High level command to
          // interact with FineADCS]
          byte[] result = new byte[4];
          globalResult = result;
          break;
        }
        case 1080: {// Origin [IFineADCS] Method [void SetRWYSpeed(int speedValue);//1080//High
          // level command to interact with FineADCS]
          int speedValue = (Integer) argObject.get(0);
          this.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels).setIntTypeByIndex(speedValue, 1);
          break;
        }
        case 1081: {// Origin [IFineADCS] Method [byte[] GetRWYSpeed();//1081//High level command to
          // interact with FineADCS]
          byte[] result = new byte[2];
          globalResult = result;
          break;
        }
        case 1082: {// Origin [IFineADCS] Method [void SetRWYAcceleration(int
          // accelerationValue);//1082//High level command to interact with FineADCS]
          int accelerationValue = (Integer) argObject.get(0);
          break;
        }
        case 1083: {// Origin [IFineADCS] Method [void SetRWYSleep(byte sleepMode);//1083//High
          // level command to interact with FineADCS]
          byte sleepMode = (Byte) argObject.get(0);
          break;
        }
        case 1084: {// Origin [IFineADCS] Method [byte[] GetRWYID();//1084//High level command to
          // interact with FineADCS]
          byte[] result = new byte[4];
          globalResult = result;
          break;
        }
        case 1085: {// Origin [IFineADCS] Method [void SetRWZSpeed(int speedValue);//1085//High
          // level command to interact with FineADCS]
          int speedValue = (Integer) argObject.get(0);
          this.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels).setIntTypeByIndex(speedValue, 2);
          break;
        }
        case 1086: {// Origin [IFineADCS] Method [byte[] GetRWZSpeed();//1086//High level command to
          // interact with FineADCS]
          byte[] result = new byte[2];
          globalResult = result;
          break;
        }
        case 1087: {// Origin [IFineADCS] Method [void SetRWZAcceleration(int
          // accelerationValue);//1087//High level command to interact with FineADCS]
          int accelerationValue = (Integer) argObject.get(0);
          break;
        }
        case 1088: {// Origin [IFineADCS] Method [void SetRWZSleep(byte sleepMode);//1088//High
          // level command to interact with FineADCS]
          byte sleepMode = (Byte) argObject.get(0);
          break;
        }
        case 1089: {// Origin [IFineADCS] Method [byte[] GetRWZID();//1089//High level command to
          // interact with FineADCS]
          byte[] result = new byte[4];
          globalResult = result;
          break;
        }
        case 1090: {// Origin [IFineADCS] Method [void ST200SetQuaternion(int[] values);//1090//High
          // level command to interact with FineADCS]
          int[] values = (int[]) argObject.get(0);
          break;
        }
        case 1091: {// Origin [IFineADCS] Method [void ST200UpdateInterval(long
          // interval);//1091//High level command to interact with FineADCS]
          long interval = (Long) argObject.get(0);
          break;
        }
        case 1092: {// Origin [IFineADCS] Method [void SunSensor1SetValue(int[] values);//1092//High
          // level command to interact with FineADCS]
          int[] values = (int[]) argObject.get(0);
          break;
        }
        case 1093: {// Origin [IFineADCS] Method [byte[] SunSensor1GetValue();//1093//High level
          // command to interact with FineADCS]
          byte[] result = new byte[16];
          globalResult = result;
          break;
        }
        case 1094: {// Origin [IFineADCS] Method [void SunSensor1SetValueQuaternion(int[]
          // values);//1094//High level command to interact with FineADCS]
          int[] values = (int[]) argObject.get(0);
          break;
        }
        case 1095: {// Origin [IFineADCS] Method [byte[] SunSensor1GetValueQuaternion();//1095//High
          // level command to interact with FineADCS]
          byte[] result = new byte[16];
          globalResult = result;
          break;
        }
        case 1096: {// Origin [IFineADCS] Method [void Gyro1SetRate(float[] values);//1096//High
          // level command to interact with FineADCS]
          float[] values = (float[]) argObject.get(0);
          this.hMapSDData.get(DevDatPBind.FineADCS_Gyro1).setType(values);
          this.hMapSDData.get(DevDatPBind.FineADCS_AngularMomentum).setType(values);
          break;
        }
        case 1097: {// Origin [IFineADCS] Method [byte[] Gyro1GetRate();//1097//High level command
          // to interact with FineADCS]
          byte[] result = new byte[20];
          globalResult = result;
          break;
        }
        case 1098: {// Origin [IFineADCS] Method [void Gyro1SetUpdateInterval(int
          // updateRate);//1098//High level command to interact with FineADCS]
          int updateRate = (Integer) argObject.get(0);
          break;
        }
        case 1099: {// Origin [IFineADCS] Method [void Gyro1RemoveBias();//1099//High level command
          // to interact with FineADCS]
          break;
        }
        case 1100: {// Origin [IFineADCS] Method [byte[] Gyro1GetBias();//1100//High level command
          // to interact with FineADCS]
          byte[] result = new byte[4];
          globalResult = result;
          break;
        }
        case 1101: {// Origin [IFineADCS] Method [void Gyro1SetFilter1(byte updateRate,int
          // allowedDeviation);//1101//High level command to interact with FineADCS]
          byte updateRate = (Byte) argObject.get(0);
          int allowedDeviation = (Integer) argObject.get(1);
          break;
        }
        case 1102: {// Origin [IFineADCS] Method [void Gyro1SetCalibrationParameters(int[]
          // calibrationValues);//1102//High level command to interact with FineADCS]
          int[] calibrationValues = (int[]) argObject.get(0);
          break;
        }
        case 1103: {// Origin [IFineADCS] Method [byte[]
          // Gyro1GetCalibrationParameters();//1103//High level command to interact with
          // FineADCS]
          byte[] result = new byte[48];
          globalResult = result;
          break;
        }
        case 1104: {// Origin [IFineADCS] Method [void Gyro1EnableCalibration();//1104//High level
          // command to interact with FineADCS]
          break;
        }
        case 1105: {// Origin [IFineADCS] Method [void Gyro1DisableCalibration();//1105//High level
          // command to interact with FineADCS]
          break;
        }
        case 1106: {// Origin [IFineADCS] Method [void Gyro1SetQuaternionFromSunSensor(float[]
          // quaternionValues);//1106//High level command to interact with FineADCS]
          float[] quaternionValues = (float[]) argObject.get(0);
          break;
        }
        case 1107: {// Origin [IFineADCS] Method [byte[]
          // Gyro1GetQuaternionFromSunSensor();//1107//High level command to interact with
          // FineADCS]
          byte[] result = new byte[16];
          globalResult = result;
          break;
        }
        case 1108: {// Origin [IFineADCS] Method [void accelerometerSetValues(float[]
          // values);//1108//High level command to interact with FineADCS]
          float[] values = (float[]) argObject.get(0);
          this.hMapSDData.get(DevDatPBind.FineADCS_Accelerometer).setType(values);
          break;
        }
        case 1109: {// Origin [IFineADCS] Method [byte[] accelerometerGetValues();//1109//High level
          // command to interact with FineADCS]
          byte[] result = new byte[12];
          globalResult = result;
          break;
        }
        case 1110: {// Origin [IFineADCS] Method [void accelerometerReadInterval(int
          // interval);//1110//High level command to interact with FineADCS]
          int interval = (Integer) argObject.get(0);
          break;
        }
        case 1111: {// Origin [IFineADCS] Method [void magnetometerSetMagneticField(float[]
          // values);//1111//High level command to interact with FineADCS]
          float[] values = (float[]) argObject.get(0);
          break;
        }
        case 1112: {// Origin [IFineADCS] Method [byte[] magnetometerGetMagneticField();//1112//High
          // level command to interact with FineADCS]
          byte[] result = new byte[12];
          globalResult = result;
          break;
        }
        case 1113: {// Origin [IFineADCS] Method [void magnetometerSetUpdateInterval(int
          // interval);//1113//High level command to interact with FineADCS]
          int interval = (Integer) argObject.get(0);
          break;
        }
        case 1114: {// Origin [IFineADCS] Method [void accelerometerSetCalibrationParams(float[]
          // values);//1114//High level command to interact with FineADCS]
          float[] values = (float[]) argObject.get(0);
          break;
        }
        case 1115: {// Origin [IFineADCS] Method [byte[]
          // accelerometerGetCalibrationParams();//1115//High level command to interact
          // with FineADCS]
          byte[] result = new byte[48];
          globalResult = result;
          break;
        }
        case 1116: {// Origin [IFineADCS] Method [void accelerometerEnableCalibration();//1116//High
          // level command to interact with FineADCS]
          break;
        }
        case 1117: {// Origin [IFineADCS] Method [void
          // accelerometerDisableCalibration();//1117//High level command to interact with
          // FineADCS]
          break;
        }
        case 1118: {// Origin [IFineADCS] Method [void
          // accelerometerSetQuaternionFromSunSensor(float[]
          // quaternionValues);//1118//High level command to interact with FineADCS]
          float[] quaternionValues = (float[]) argObject.get(0);
          break;
        }
        case 1119: {// Origin [IFineADCS] Method [byte[]
          // accelerometerGetQuaternionFromSunSensor();//1119//High level command to
          // interact with FineADCS]
          byte[] result = new byte[16];
          globalResult = result;
          break;
        }
        case 1120: {// Origin [IFineADCS] Method [byte[] kalman2FilterGetTelemetry(int
          // requestRegister);//1120//High level command to interact with FineADCS]
          int requestRegister = (Integer) argObject.get(0);
          byte[] result = new byte[68];
          globalResult = result;
          break;
        }
        case 1121: {// Origin [IFineADCS] Method [void kalman2FilterSelectGyro(byte
          // selectGyro);//1121//High level command to interact with FineADCS]
          byte selectGyro = (Byte) argObject.get(0);
          break;
        }
        case 1122: {// Origin [IFineADCS] Method [void kalman2FilterStart();//1122//High level
          // command to interact with FineADCS]
          break;
        }
        case 1123: {// Origin [IFineADCS] Method [void kalman2FilterStop();//1123//High level
          // command to interact with FineADCS]
          break;
        }
        case 1124: {// Origin [IFineADCS] Method [byte[] kalman4FilterGetTelemetry(int
          // requestRegister);//1124//High level command to interact with FineADCS]
          int requestRegister = (Integer) argObject.get(0);
          byte[] result = new byte[68];
          globalResult = result;
          break;
        }
        case 1125: {// Origin [IFineADCS] Method [void kalman4FilterSelectGyro(byte
          // selectGyro);//1125//High level command to interact with FineADCS]
          byte selectGyro = (Byte) argObject.get(0);
          break;
        }
        case 1126: {// Origin [IFineADCS] Method [void kalman4FilterStart();//1126//High level
          // command to interact with FineADCS]
          break;
        }
        case 1127: {// Origin [IFineADCS] Method [void kalman4FilterStop();//1127//High level
          // command to interact with FineADCS]
          break;
        }
        case 1128: {// Origin [IFineADCS] Method [void controlLoopsSetUpdateInterval(int
          // interval);//1128//High level command to interact with FineADCS]
          int interval = (Integer) argObject.get(0);
          break;
        }
        case 1129: {// Origin [IFineADCS] Method [byte[] controlLoopsGetTargetRWSpeed();//1129//High
          // level command to interact with FineADCS]
          byte[] result = new byte[6];
          globalResult = result;
          break;
        }
        case 1130: {// Origin [IFineADCS] Method [byte[]
          // controlLoopsGetTargetMTWDipoleMoment3D();//1130//High level command to
          // interact with FineADCS]
          byte[] result = new byte[6];
          globalResult = result;
          break;
        }
        case 1131: {// Origin [IFineADCS] Method [byte[] controlLoopsGetStatus();//1131//High level
          // command to interact with FineADCS]
          byte[] result = new byte[24];
          globalResult = result;
          break;
        }
        case 1132: {// Origin [IFineADCS] Method [void controlLoopsSetAntiWindup(byte axis,int
          // controlRegister,float[] values);//1132//High level command to interact with
          // FineADCS]
          byte axis = (Byte) argObject.get(0);
          int controlRegister = (Integer) argObject.get(1);
          float[] values = (float[]) argObject.get(2);
          break;
        }
        case 1133: {// Origin [IFineADCS] Method [byte[] controlLoopsGetAntiWindup(byte axis,int
          // controlRegister);//1133//High level command to interact with FineADCS]
          byte axis = (Byte) argObject.get(0);
          int controlRegister = (Integer) argObject.get(1);
          byte[] result = new byte[16];
          globalResult = result;
          break;
        }
        case 1134: {// Origin [IFineADCS] Method [void singleAxisStartControlLoop(byte axis,int
          // controlRegister,float[] targetAngle);//1134//High level command to interact
          // with FineADCS]
          byte axis = (Byte) argObject.get(0);
          int controlRegister = (Integer) argObject.get(1);
          float[] targetAngle = (float[]) argObject.get(2);
          break;
        }
        case 1135: {// Origin [IFineADCS] Method [void singleAxisStopControlLoop(byte
          // axis);//1135//High level command to interact with FineADCS]
          byte axis = (Byte) argObject.get(0);
          break;
        }
        case 1136: {// Origin [IFineADCS] Method [void singleAxisSetParameter(byte axis,int
          // controlRegister,float[] values);//1136//High level command to interact with
          // FineADCS]
          byte axis = (Byte) argObject.get(0);
          int controlRegister = (Integer) argObject.get(1);
          float[] values = (float[]) argObject.get(2);
          break;
        }
        case 1137: {// Origin [IFineADCS] Method [byte[] singleAxisGetParameter(byte axis,int
          // controlRegister);//1137//High level command to interact with FineADCS]
          byte axis = (Byte) argObject.get(0);
          int controlRegister = (Integer) argObject.get(1);
          byte[] result = new byte[28];
          globalResult = result;
          break;
        }
        case 1138: {// Origin [IFineADCS] Method [void singleAxisResetParameter(byte axis,int
          // controlRegister);//1138//High level command to interact with FineADCS]
          byte axis = (Byte) argObject.get(0);
          int controlRegister = (Integer) argObject.get(1);
          break;
        }
        case 1139: {// Origin [IFineADCS] Method [void sunPointingStartControlLoop(float[]
          // targetSunVector);//1139//High level command to interact with FineADCS]
          float[] targetSunVector = (float[]) argObject.get(0);
          break;
        }
        case 1140: {// Origin [IFineADCS] Method [void sunPointingStopControlLoop();//1140//High
          // level command to interact with FineADCS]
          break;
        }
        case 1141: {// Origin [IFineADCS] Method [void sunPointingSetParameter(float[]
          // values);//1141//High level command to interact with FineADCS]
          float[] values = (float[]) argObject.get(0);
          break;
        }
        case 1142: {// Origin [IFineADCS] Method [byte[] sunPointingGetParameter();//1142//High
          // level command to interact with FineADCS]
          byte[] result = new byte[40];
          globalResult = result;
          break;
        }
        case 1143: {// Origin [IFineADCS] Method [void sunPointingResetParameter();//1143//High
          // level command to interact with FineADCS]
          break;
        }
        case 1144: {// Origin [IFineADCS] Method [void bdotStartControlLoop(byte
          // controller);//1144//High level command to interact with FineADCS]
          byte controller = (Byte) argObject.get(0);
          break;
        }
        case 1145: {// Origin [IFineADCS] Method [void bdotStopControlLoop();//1145//High level
          // command to interact with FineADCS]
          break;
        }
        case 1146: {// Origin [IFineADCS] Method [void bdotSetParameter(float gain);//1146//High
          // level command to interact with FineADCS]
          float gain = (Float) argObject.get(0);
          break;
        }
        case 1147: {// Origin [IFineADCS] Method [byte[] bdotGetParameter();//1147//High level
          // command to interact with FineADCS]
          byte[] result = new byte[4];
          globalResult = result;
          break;
        }
        case 1148: {// Origin [IFineADCS] Method [void bdotResetParameter();//1148//High level
          // command to interact with FineADCS]
          break;
        }
        case 1149: {// Origin [IFineADCS] Method [void singleSpinningStartControlLoop(float[]
          // targetBodyAxis,float targetAngularVelocityMagnitude,float[]
          // inertialTargetVector);//1149//High level command to interact with FineADCS]
          float[] targetBodyAxis = (float[]) argObject.get(0);
          float targetAngularVelocityMagnitude = (Float) argObject.get(1);
          float[] inertialTargetVector = (float[]) argObject.get(2);
          break;
        }
        case 1150: {// Origin [IFineADCS] Method [void singleSpinningStopControlLoop();//1150//High
          // level command to interact with FineADCS]
          break;
        }
        case 1151: {// Origin [IFineADCS] Method [void singleSpinningSetParameter(float[]
          // values);//1151//High level command to interact with FineADCS]
          float[] values = (float[]) argObject.get(0);
          break;
        }
        case 1152: {// Origin [IFineADCS] Method [byte[] singleSpinningGetParameter();//1152//High
          // level command to interact with FineADCS]
          byte[] result = new byte[52];
          globalResult = result;
          break;
        }
        case 1153: {// Origin [IFineADCS] Method [void targetTrackingStartModeConstantVel(byte
          // modeType,float[] values,long[] times);//1153//High level command to interact
          // with FineADCS]
          byte modeType = (Byte) argObject.get(0);
          float[] values = (float[]) argObject.get(1);
          long[] times = (long[]) argObject.get(2);
          break;
        }
        case 1154: {// Origin [IFineADCS] Method [void targetTrackingStartModeGeneral(byte
          // modeType,float[] values,long[] times);//1154//High level command to interact
          // with FineADCS]
          byte modeType = (Byte) argObject.get(0);
          float[] values = (float[]) argObject.get(1);
          long[] times = (long[]) argObject.get(2);
          break;
        }
        case 1155: {// Origin [IFineADCS] Method [void targetTrackingStartModeWGS84(byte
          // modeType,float[] values,long[] times);//1155//High level command to interact
          // with FineADCS]
          byte modeType = (Byte) argObject.get(0);
          float[] values = (float[]) argObject.get(1);
          long[] times = (long[]) argObject.get(2);
          break;
        }
        case 1156: {// Origin [IFineADCS] Method [void targetTrackingStopMode();//1156//High level
          // command to interact with FineADCS]
          break;
        }
        case 1157: {// Origin [IFineADCS] Method [void targetTrackingSetParameters(float[]
          // values);//1157//High level command to interact with FineADCS]
          float[] values = (float[]) argObject.get(0);
          break;
        }
        case 1158: {// Origin [IFineADCS] Method [byte[] targetTrackingGetParameters();//1158//High
          // level command to interact with FineADCS]
          byte[] result = new byte[73];
          globalResult = result;
          break;
        }
        case 1159: {// Origin [IFineADCS] Method [void targetTrackingResetParameters();//1159//High
          // level command to interact with FineADCS]
          break;
        }
        case 1160: {// Origin [IFineADCS] Method [void orbitSetRV();//1160//High level command to
          // interact with FineADCS]
          break;
        }
        case 1161: {// Origin [IFineADCS] Method [byte[] orbitGetRV();//1161//High level command to
          // interact with FineADCS]
          byte[] result = new byte[32];
          globalResult = result;
          break;
        }
        case 1162: {// Origin [IFineADCS] Method [void orbitSetTLE(byte[] tleData);//1162//High
          // level command to interact with FineADCS]
          byte[] tleData = (byte[]) argObject.get(0);
          StringBuilder newTLE1 = new StringBuilder(), newTLE2 = new StringBuilder();
          boolean result = OrekitCore.parseTLEFromBytes(tleData, newTLE1, newTLE2);
          this.logger.log(Level.FINE, "Response ok to new TLEs is [" + result + "]");
          if (result) {
            simulatorHeader.setOrekitTLE1(newTLE1.toString());
            simulatorHeader.setOrekitTLE2(newTLE2.toString());
            orekitCore.setNewTLEs(simulatorHeader);
          } else {
            commandResult.setCommandFailed(true);
          }
          break;
        }
        case 1163: {// Origin [IFineADCS] Method [byte[] orbitSetUpdateInterval(int
          // updateInterval);//1163//High level command to interact with FineADCS]
          int updateInterval = (Integer) argObject.get(0);
          byte[] result = new byte[32];
          globalResult = result;
          break;
        }
        case 1164: {// Origin [IFineADCS] Method [void opModeIdle();//1164//High level command to
          // interact with FineADCS]
          break;
        }
        case 1165: {// Origin [IFineADCS] Method [void opModeSafe();//1165//High level command to
          // interact with FineADCS]
          if (simulatorHeader.isUseOrekitPropagator()) {
            this.orekitCore.changeAttitude(OrekitCore.ATTITUDE_MODE.SUN_POINTING);
          }
          break;
        }
        case 1166: {// Origin [IFineADCS] Method [void opModeMeasure();//1166//High level command to
          // interact with FineADCS]
          if (simulatorHeader.isUseOrekitPropagator()) {
            this.orekitCore.changeAttitude(OrekitCore.ATTITUDE_MODE.NADIR_POINTING);
          }
          break;
        }
        case 1167: {// Origin [IFineADCS] Method [void opModeDetumble(byte start,long[]
          // times);//1167//High level command to interact with FineADCS]
          byte start = (Byte) argObject.get(0);
          long[] times = (long[]) argObject.get(1);
          if (simulatorHeader.isUseOrekitPropagator()) {
            this.orekitCore.changeAttitude(OrekitCore.ATTITUDE_MODE.BDOT_DETUMBLE);
          }
          break;
        }
        case 1168: {// Origin [IFineADCS] Method [void opModeSunPointing(byte[] mode,long[]
          // times,float[] targetSunVector);//1168//High level command to interact with
          // FineADCS]
          byte[] mode = (byte[]) argObject.get(0);
          long[] times = (long[]) argObject.get(1);
          float[] targetSunVector = (float[]) argObject.get(2);
          if (simulatorHeader.isUseOrekitPropagator()) {
            this.orekitCore.changeAttitude(OrekitCore.ATTITUDE_MODE.SUN_POINTING);
          }
          break;
        }
        case 1169: {// Origin [IFineADCS] Method [byte[] opModeGetSunPointingStatus();//1169//High
          // level command to interact with FineADCS]
          byte[] result = new byte[25];
          SimulatorSpacecraftState spacecraftState = getSpacecraftState();
          double[] sunVector = spacecraftState.getSunVector();
          FWRefFineADCS.putFloatInByteArray((float) sunVector[0],
              FWRefFineADCS.SUNPOINTSTAT_IDX.SUN_VECTOR_X, result);
          FWRefFineADCS.putFloatInByteArray((float) sunVector[1],
              FWRefFineADCS.SUNPOINTSTAT_IDX.SUN_VECTOR_Y, result);
          FWRefFineADCS.putFloatInByteArray((float) sunVector[2],
              FWRefFineADCS.SUNPOINTSTAT_IDX.SUN_VECTOR_Z, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels).getTypeAsIntByIndex(0),
              FWRefFineADCS.SUNPOINTSTAT_IDX.ACTUATOR_X, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels).getTypeAsIntByIndex(1),
              FWRefFineADCS.SUNPOINTSTAT_IDX.ACTUATOR_Y, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels).getTypeAsIntByIndex(2),
              FWRefFineADCS.SUNPOINTSTAT_IDX.ACTUATOR_Z, result);
          globalResult = result;
          break;
        }
        case 1170: {// Origin [IFineADCS] Method [void opModeSetModeSpin(byte mode,long[]
          // times,float[] targetVector);//1170//High level command to interact with
          // FineADCS]
          byte mode = (Byte) argObject.get(0);
          long[] times = (long[]) argObject.get(1);
          float[] targetVector = (float[]) argObject.get(2);
          if (simulatorHeader.isUseOrekitPropagator()) {
            this.orekitCore.changeAttitudeLof(targetVector[0], targetVector[1], targetVector[2],
                targetVector[6]);
          }
          break;
        }
        case 1171: {// Origin [IFineADCS] Method [byte[] opModeGetSpinModeStatus();//1171//High
          // level command to interact with FineADCS]
          byte[] result = new byte[64];
          SimulatorSpacecraftState spacecraftState = getSpacecraftState();
          double[] sunVector = spacecraftState.getSunVector();
          float[] magneticField = spacecraftState.getMagnetometer();
          float[] quaternions = spacecraftState.getQ();
          FWRefFineADCS.putFloatInByteArray((float) sunVector[0],
              FWRefFineADCS.SPINMODESTAT_IDX.SUN_VECTOR_X, result);
          FWRefFineADCS.putFloatInByteArray((float) sunVector[1],
              FWRefFineADCS.SPINMODESTAT_IDX.SUN_VECTOR_Y, result);
          FWRefFineADCS.putFloatInByteArray((float) sunVector[2],
              FWRefFineADCS.SPINMODESTAT_IDX.SUN_VECTOR_Z, result);
          FWRefFineADCS.putFloatInByteArray(magneticField[0],
              FWRefFineADCS.SPINMODESTAT_IDX.MAGNETOMETER_X, result);
          FWRefFineADCS.putFloatInByteArray(magneticField[1],
              FWRefFineADCS.SPINMODESTAT_IDX.MAGNETOMETER_Y, result);
          FWRefFineADCS.putFloatInByteArray(magneticField[2],
              FWRefFineADCS.SPINMODESTAT_IDX.MAGNETOMETER_Z, result);
          FWRefFineADCS.putFloatInByteArray(quaternions[0], FWRefFineADCS.SPINMODESTAT_IDX.Q1,
              result);
          FWRefFineADCS.putFloatInByteArray(quaternions[1], FWRefFineADCS.SPINMODESTAT_IDX.Q2,
              result);
          FWRefFineADCS.putFloatInByteArray(quaternions[2], FWRefFineADCS.SPINMODESTAT_IDX.Q3,
              result);
          FWRefFineADCS.putFloatInByteArray(quaternions[3], FWRefFineADCS.SPINMODESTAT_IDX.Q4,
              result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_AngularMomentum).getTypeAsFloatByIndex(0),
              FWRefFineADCS.SPINMODESTAT_IDX.ANG_MOM_X, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_AngularMomentum).getTypeAsFloatByIndex(1),
              FWRefFineADCS.SPINMODESTAT_IDX.ANG_MOM_Y, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_AngularMomentum).getTypeAsFloatByIndex(2),
              FWRefFineADCS.SPINMODESTAT_IDX.ANG_MOM_Z, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_Magnetorquer).getTypeAsIntByIndex(0),
              FWRefFineADCS.SPINMODESTAT_IDX.MTQ_DIP_MOMENT_X, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_Magnetorquer).getTypeAsIntByIndex(1),
              FWRefFineADCS.SPINMODESTAT_IDX.MTQ_DIP_MOMENT_Y, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_Magnetorquer).getTypeAsIntByIndex(2),
              FWRefFineADCS.SPINMODESTAT_IDX.MTQ_DIP_MOMENT_Z, result);

          globalResult = result;
          break;
        }
        case 1172: {// Origin [IFineADCS] Method [void opModeSetTargetTrackingCVelocity(byte
          // mode,long[] times,float[] targetVector);//1172//High level command to
          // interact with FineADCS]
          byte mode = (Byte) argObject.get(0);
          long[] times = (long[]) argObject.get(1);
          float[] targetVector = (float[]) argObject.get(2);
          break;
        }
        case 1173: {// Origin [IFineADCS] Method [byte[]
          // opModeGetTargetTrackingCVelocityStatus();//1173//High level command to
          // interact with FineADCS]
          byte[] result = new byte[56];
          globalResult = result;
          break;
        }
        case 1174: {// Origin [IFineADCS] Method [void opModeSetNadirTargetTracking(byte mode,long[]
          // times);//1174//High level command to interact with FineADCS]
          byte mode = (Byte) argObject.get(0);
          long[] times = (long[]) argObject.get(1);
          if (simulatorHeader.isUseOrekitPropagator()) {
            this.orekitCore.changeAttitude(OrekitCore.ATTITUDE_MODE.NADIR_POINTING);
          }
          break;
        }
        case 1175: {// Origin [IFineADCS] Method [byte[]
          // opModeGetNadirTargetTrackingStatus();//1175//High level command to interact
          // with FineADCS]
          byte[] result = new byte[68];
          SimulatorSpacecraftState spacecraftState = getSpacecraftState();
          float[] positionVector = spacecraftState.getRv();
          float[] quaternions = spacecraftState.getQ();
          FWRefFineADCS.putFloatInByteArray(positionVector[0],
              FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.POSITION_VECTOR_X, result);
          FWRefFineADCS.putFloatInByteArray(positionVector[1],
              FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.POSITION_VECTOR_Y, result);
          FWRefFineADCS.putFloatInByteArray(positionVector[2],
              FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.POSITION_VECTOR_Z, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_AngularVelocity).getTypeAsFloatByIndex(0),
              FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.ANG_VEL_X, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_AngularVelocity).getTypeAsFloatByIndex(1),
              FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.ANG_VEL_Y, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_AngularVelocity).getTypeAsFloatByIndex(2),
              FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.ANG_VEL_Z, result);
          FWRefFineADCS.putFloatInByteArray(quaternions[0], FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.Q1,
              result);
          FWRefFineADCS.putFloatInByteArray(quaternions[1], FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.Q2,
              result);
          FWRefFineADCS.putFloatInByteArray(quaternions[2], FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.Q3,
              result);
          FWRefFineADCS.putFloatInByteArray(quaternions[3], FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.Q4,
              result);
          FWRefFineADCS.putFloatInByteArray(quaternions[0],
              FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.TGT_Q1, result);
          FWRefFineADCS.putFloatInByteArray(quaternions[1],
              FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.TGT_Q2, result);
          FWRefFineADCS.putFloatInByteArray(quaternions[2],
              FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.TGT_Q3, result);
          FWRefFineADCS.putFloatInByteArray(quaternions[3],
              FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.TGT_Q4, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels).getTypeAsIntByIndex(0),
              FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.RW_SPEED_X, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels).getTypeAsIntByIndex(1),
              FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.RW_SPEED_Y, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels).getTypeAsIntByIndex(2),
              FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.RW_SPEED_Z, result);
          globalResult = result;
          break;
        }
        case 1176: {// Origin [IFineADCS] Method [void opModeSetStandardTargetTracking(byte
          // mode,long[] times,float[] quaternionCoefficients);//1176//High level command
          // to interact with FineADCS]
          byte mode = (Byte) argObject.get(0);
          long[] times = (long[]) argObject.get(1);
          float[] quaternionCoefficients = (float[]) argObject.get(2);
          break;
        }
        case 1177: {// Origin [IFineADCS] Method [byte[]
          // opModeGetStandardTargetTrackingStatus();//1177//High level command to
          // interact with FineADCS]
          byte[] result = new byte[56];
          globalResult = result;
          break;
        }
        case 1178: {// Origin [IFineADCS] Method [void opModeSetFixWGS84TargetTracking(byte
          // mode,long[] times,float[] latitudeLongitude);//1178//High level command to
          // interact with FineADCS]
          byte mode = (Byte) argObject.get(0);
          long[] times = (long[]) argObject.get(1);
          float[] latitudeLongitude = (float[]) argObject.get(2);
          if (simulatorHeader.isUseOrekitPropagator()) {
            this.orekitCore.changeAttitudeTarget(latitudeLongitude[0], latitudeLongitude[1], 0);
          }
          break;
        }
        case 1179: {// Origin [IFineADCS] Method [byte[]
          // opModeGetFixWGS84TargetTracking();//1179//High level command to interact with
          // FineADCS]
          byte[] result = new byte[68];
          SimulatorSpacecraftState spacecraftState = getSpacecraftState();
          float[] positionVector = spacecraftState.getRv();
          float[] quaternions = spacecraftState.getQ();
          FWRefFineADCS.putFloatInByteArray(positionVector[0],
              FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.POSITION_VECTOR_X, result);
          FWRefFineADCS.putFloatInByteArray(positionVector[1],
              FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.POSITION_VECTOR_Y, result);
          FWRefFineADCS.putFloatInByteArray(positionVector[2],
              FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.POSITION_VECTOR_Z, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_AngularVelocity).getTypeAsFloatByIndex(0),
              FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.ANG_VEL_X, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_AngularVelocity).getTypeAsFloatByIndex(1),
              FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.ANG_VEL_Y, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_AngularVelocity).getTypeAsFloatByIndex(2),
              FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.ANG_VEL_Z, result);
          FWRefFineADCS.putFloatInByteArray(quaternions[0],
              FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.Q1, result);
          FWRefFineADCS.putFloatInByteArray(quaternions[1],
              FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.Q2, result);
          FWRefFineADCS.putFloatInByteArray(quaternions[2],
              FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.Q3, result);
          FWRefFineADCS.putFloatInByteArray(quaternions[3],
              FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.Q4, result);
          FWRefFineADCS.putFloatInByteArray(quaternions[0],
              FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.TGT_Q1, result);
          FWRefFineADCS.putFloatInByteArray(quaternions[1],
              FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.TGT_Q2, result);
          FWRefFineADCS.putFloatInByteArray(quaternions[2],
              FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.TGT_Q3, result);
          FWRefFineADCS.putFloatInByteArray(quaternions[3],
              FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.TGT_Q4, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels).getTypeAsIntByIndex(0),
              FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.RW_SPEED_X, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels).getTypeAsIntByIndex(1),
              FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.RW_SPEED_Y, result);
          FWRefFineADCS.putFloatInByteArray(
              this.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels).getTypeAsIntByIndex(2),
              FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.RW_SPEED_Z, result);
          globalResult = result;
          break;
        }
        case 1180: {// Origin [IFineADCS] Method [void opModeSetTargetCapture1(byte mode,long
          // startTime,float[] data);//1180//High level command to interact with FineADCS]
          byte mode = (Byte) argObject.get(0);
          long startTime = (Long) argObject.get(1);
          float[] data = (float[]) argObject.get(2);
          break;
        }
        case 1181: {// Origin [IFineADCS] Method [byte[] opModeGetTargetCapture1();//1181//High
          // level command to interact with FineADCS]
          byte[] result = new byte[22];
          globalResult = result;
          break;
        }
        case 1182: {// Origin [IFineADCS] Method [byte[] simGetOrbitTLEBytesFromString(String
          // tleLine1,String tleLine2);//1182//High level command to interact with
          // FineADCS]
          String tleLine1 = (String) argObject.get(0);
          String tleLine2 = (String) argObject.get(1);
          StringBuilder exception = new StringBuilder();
          byte[] result = new byte[140];
          if (!OrekitCore.parseTLEFromStrings(result, tleLine1, tleLine2, exception)) {
            commandResult.setCommandFailed(true);
            throw new Exception(exception.toString());
          }
          globalResult = result;
          break;
        }
        case 1183: {// Origin [IFineADCS] Method [float simGetFloatFromByteArray(byte[] data,int
          // byteOffset);//1183//Test command for the helper libraries]
          byte[] data = (byte[]) argObject.get(0);
          int byteOffset = (Integer) argObject.get(1);
          float result = FWRefFineADCS.getFloatFromByteArray(data, byteOffset);
          globalResult = result;
          break;
        }
        case 1184: {// Origin [IFineADCS] Method [byte[] simGetByteArrayFromFloat(float
          // data);//1184//Test command for the helper libraries]
          float data = (Float) argObject.get(0);
          byte[] result = new byte[4];
          FWRefFineADCS.putFloatInByteArray(data, 0, result);
          globalResult = result;
          break;
        }
        case 1185: {// Origin [IFineADCS] Method [double simGetDoubleFromByteArray(byte[] data,int
          // byteOffset);//1185//Test command for the helper libraries]
          byte[] data = (byte[]) argObject.get(0);
          int byteOffset = (Integer) argObject.get(1);
          double result = FWRefFineADCS.getDoubleFromByteArray(data, byteOffset);
          globalResult = result;
          break;
        }
        case 1186: {// Origin [IFineADCS] Method [byte[] simGetByteArrayFromDouble(double
          // data);//1186//Test command for the helper libraries]
          double data = (Double) argObject.get(0);
          byte[] result = new byte[8];
          FWRefFineADCS.putDoubleInByteArray(data, 0, result);
          globalResult = result;
          break;
        }
        case 1187: {// Origin [IFineADCS] Method [int simGetIntFromByteArray(byte[] data,int
          // byteOffset);//1187//Test command for the helper libraries]
          byte[] data = (byte[]) argObject.get(0);
          int byteOffset = (Integer) argObject.get(1);
          int result = FWRefFineADCS.getIntFromByteArray(data, byteOffset);
          globalResult = result;
          break;
        }
        case 1188: {// Origin [IFineADCS] Method [byte[] simGetByteArrayFromInt(int
          // data);//1188//Test command for the helper libraries]
          int data = (Integer) argObject.get(0);
          byte[] result = new byte[4];
          FWRefFineADCS.putIntInByteArray(data, 0, result);
          globalResult = result;
          break;
        }
        case 1189: {// Origin [IFineADCS] Method [long simGetLongFromByteArray(byte[] data,long
          // byteOffset);//1189//Test command for the helper libraries]
          byte[] data = (byte[]) argObject.get(0);
          int byteOffset = (Integer) argObject.get(1);
          long result = FWRefFineADCS.getLongFromByteArray(data, byteOffset);
          globalResult = result;
          break;
        }
        case 1190: {// Origin [IFineADCS] Method [byte[] simGetByteArrayFromLong(long
          // data);//1190//Test command for the helper libraries]
          long data = (Long) argObject.get(0);
          byte[] result = new byte[8];
          FWRefFineADCS.putLongInByteArray(data, 0, result);
          globalResult = result;
          break;
        }
        case 1191: {// Origin [IFineADCS] Method [void Gyro2SetRate(float[] values);//1191//High
          // level command to interact with FineADCS]
          float[] values = (float[]) argObject.get(0);
          this.hMapSDData.get(DevDatPBind.FineADCS_Gyro2).setType(values);
          this.hMapSDData.get(DevDatPBind.FineADCS_AngularMomentum).setType(values);
          break;
        }
        case 1192: {// Origin [IFineADCS] Method [byte[] Gyro2GetRate();//1192//High level command
          // to interact with FineADCS]
          byte[] result = new byte[20];
          globalResult = result;
          break;
        }
        case 1193: {// Origin [IFineADCS] Method [void Gyro2SetUpdateInterval(int
          // updateRate);//1193//High level command to interact with FineADCS]
          int updateRate = (Integer) argObject.get(0);
          break;
        }
        case 1194: {// Origin [IFineADCS] Method [void Gyro2RemoveBias();//1194//High level command
          // to interact with FineADCS]
          break;
        }
        case 1195: {// Origin [IFineADCS] Method [byte[] Gyro2GetBias();//1195//High level command
          // to interact with FineADCS]
          byte[] result = new byte[4];
          globalResult = result;
          break;
        }
        case 1196: {// Origin [IFineADCS] Method [void Gyro2SetFilter1(byte updateRate,int
          // allowedDeviation);//1196//High level command to interact with FineADCS]
          byte updateRate = (Byte) argObject.get(0);
          int allowedDeviation = (Integer) argObject.get(1);
          break;
        }
        case 1197: {// Origin [IFineADCS] Method [void Gyro2SetCalibrationParameters(int[]
          // calibrationValues);//1197//High level command to interact with FineADCS]
          int[] calibrationValues = (int[]) argObject.get(0);
          break;
        }
        case 1198: {// Origin [IFineADCS] Method [byte[]
          // Gyro2GetCalibrationParameters();//1198//High level command to interact with
          // FineADCS]
          byte[] result = new byte[48];
          globalResult = result;
          break;
        }
        case 1199: {// Origin [IFineADCS] Method [void Gyro2EnableCalibration();//1199//High level
          // command to interact with FineADCS]
          break;
        }
        case 1200: {// Origin [IFineADCS] Method [void Gyro2DisableCalibration();//1200//High level
          // command to interact with FineADCS]
          break;
        }
        case 1201: {// Origin [IFineADCS] Method [void Gyro2SetQuaternionFromSunSensor(float[]
          // quaternionValues);//1201//High level command to interact with FineADCS]
          float[] quaternionValues = (float[]) argObject.get(0);
          break;
        }
        case 1202: {// Origin [IFineADCS] Method [byte[]
          // Gyro2GetQuaternionFromSunSensor();//1202//High level command to interact with
          // FineADCS]
          byte[] result = new byte[16];
          globalResult = result;
          break;
        }
        case 1203: {// Origin [IFineADCS] Method [int simGetInt16FromByteArray(byte[] data,int
          // byteOffset);//1203//Test command for the helper libraries]
          byte[] data = (byte[]) argObject.get(0);
          int byteOffset = (Integer) argObject.get(1);
          int result = FWRefFineADCS.getInt16FromByteArray(data, byteOffset);
          globalResult = result;
          break;
        }
        case 1204: {// Origin [IFineADCS] Method [void simRunDeviceCommand(String
          // data);//1204//Generic for loading restricted subsystems]
          String data = (String) argObject.get(0);
          if (data != null) {
            String[] words = data.split(":");
            if (words.length > 0) {
              if ("quaternionServer".equals(words[0])) {
                if (this.quaternionTcpServer != null) {
                  this.quaternionTcpServer.setShouldClose(true);
                }
                this.quaternionTcpServer = new TCPServerReceiveOnly(Integer.parseInt(words[1]),
                    this.logger);
                this.quaternionTcpServer.start();
              } else if ("cameraScript".equals(words[0])) {
                this.cameraScriptPath = words[1];
              } else {
                throw new IOException("Command not recognised [" + data + "]");
              }
            }
          }
          break;
        }

        case 2001: {// Origin [IGPS] Method [String getNMEASentence(String
          // inputSentence);//201//Obtain a NMEA response for a given NMEA sentence]
          String inputSentence = ((String) argObject.get(0)).trim(); // discard white characters
          StringBuilder result = new StringBuilder();
          if (inputSentence.endsWith("GLMLA")) {
            String separator = ",";
            String separatorNewLine = "\r\n";
            LinkedList<GPSSatInView> tempResult = getSatsInView();
            int numberInSet = tempResult.size();
            for (int iSat = 1; iSat <= numberInSet; iSat++) {
              for (int i = PGPS.FirmwareReferenceOEM16.GLMLA_COL.HEADER; i <= PGPS.FirmwareReferenceOEM16.GLMLA_COL.CHECKSUM; i++) {
                if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.HEADER) {
                  result.append("$GLMLA").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.NUMBER_IN_SET) {
                  result.append(numberInSet).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.NUMBER_CURRENT) {
                  result.append(iSat).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.SLOT) {
                  result.append(tempResult.get(iSat - 1).getName()).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.CALDAY_LEAP_YEAR) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.HEALTH_FREQ) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.ECC) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.DELTAT_DOT) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.ARG_PER) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.CLK_OFFSET) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.DELTA_T_DRACONIAN) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.GLONASSTIME_ASC_NODE_EQ) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.LONG_ASC_NODE_CROSS) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.DELTA_NOMINAL_I) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.CLK_OFFSET_LSB12) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.CLK_SHIFT_COARSE) {
                  result.append("0");
                } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.CHECKSUM) {
                  result.append(calcNMEAChecksum(result.toString()));
                }
              }
              result.append(separatorNewLine);
            }
          } else if (inputSentence.endsWith("GPGRS")) {
            String separator = ",";
            String separatorNewLine = "\r\n";
            for (int i = PGPS.FirmwareReferenceOEM16.GPGRS_COL.HEADER; i <= PGPS.FirmwareReferenceOEM16.GPGRS_COL.CHECKSUM; i++) {
              if (i == PGPS.FirmwareReferenceOEM16.GPGRS_COL.HEADER) {
                result.append("$GPGRS").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGRS_COL.UTC) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGRS_COL.MODE) {
                result.append("0").append(separator);
              } else if (i >= PGPS.FirmwareReferenceOEM16.GPGRS_COL.RES1
                  && i <= PGPS.FirmwareReferenceOEM16.GPGRS_COL.RES12) {
                result.append("0");
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGRS_COL.CHECKSUM) {
                result.append(calcNMEAChecksum(result.toString()));
              }
            }
          } else if (inputSentence.endsWith("GPGRS")) {
            String separator = ",";
            String separatorNewLine = "\r\n";
            for (int i = PGPS.FirmwareReferenceOEM16.GPGRS_COL.HEADER; i <= PGPS.FirmwareReferenceOEM16.GPGRS_COL.CHECKSUM; i++) {
              if (i == PGPS.FirmwareReferenceOEM16.GPGRS_COL.HEADER) {
                result.append("$GPGRS").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGRS_COL.UTC) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGRS_COL.MODE) {
                result.append("0").append(separator);
              } else if (i >= PGPS.FirmwareReferenceOEM16.GPGRS_COL.RES1
                  && i <= PGPS.FirmwareReferenceOEM16.GPGRS_COL.RES12) {
                result.append("0");
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGRS_COL.CHECKSUM) {
                result.append(calcNMEAChecksum(result.toString()));
              }
            }
          } else if (inputSentence.endsWith("GPGST")) {
            String separator = ",";
            for (int i = PGPS.FirmwareReferenceOEM16.GPGST_COL.HEADER; i <= PGPS.FirmwareReferenceOEM16.GPGST_COL.CHECKSUM; i++) {
              if (i == PGPS.FirmwareReferenceOEM16.GPGST_COL.HEADER) {
                result.append("$GPGST").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGST_COL.UTC) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGST_COL.RMS_STD) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGST_COL.SMJR_STD) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGST_COL.SMNR_STD) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGST_COL.ORIENT_SMJR) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGST_COL.LAT_STD) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGST_COL.LON_STD) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGST_COL.ALT_STD) {
                result.append("0");
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGST_COL.CHECKSUM) {
                result.append(calcNMEAChecksum(result.toString()));
              }
            }
          } else if (inputSentence.endsWith("GPGSV")) {
            String separator = ",";
            LinkedList<GPSSatInView> tempResult = getSatsInView();
            int numberInSet = tempResult.size();
            int numberMessages = (int) Math.ceil((float) numberInSet / 4);
            int k = 0;
            for (int iSat = 1; iSat <= numberMessages; iSat++) {
              int[] tempPRN = new int[4];
              int[] tempElevation = new int[4];
              int[] tempAzimuth = new int[4];
              for (int j = 0; j < 4; j++) {
                if (k < numberInSet) {
                  tempPRN[j] = tempResult.get(k).getPrn();
                  tempElevation[j] = (int) tempResult.get(k).getElevation();
                  tempAzimuth[j] = (int) tempResult.get(k++).getAzimuth();
                }
              }
              for (int i = PGPS.FirmwareReferenceOEM16.GPGSV_COL.HEADER; i <= PGPS.FirmwareReferenceOEM16.GPGSV_COL.CHECKSUM; i++) {
                if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.HEADER) {
                  result.append("$GPGSV").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.NUMBER_MSGS) {
                  result.append(numberMessages).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.CURRENT_MSG) {
                  result.append(iSat).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.NUMBER_SATS) {
                  result.append(numberInSet).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT1_PRN) {
                  result.append(tempPRN[0]).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT1_ELEV) {
                  result.append(tempElevation[0]).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT1_AZ) {
                  result.append(tempAzimuth[0]).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT1_SNR) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT2_PRN) {
                  result.append(tempPRN[1]).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT2_ELEV) {
                  result.append(tempElevation[1]).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT2_AZ) {
                  result.append(tempAzimuth[1]).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT2_SNR) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT3_PRN) {
                  result.append(tempPRN[2]).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT3_ELEV) {
                  result.append(tempElevation[2]).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT3_AZ) {
                  result.append(tempAzimuth[2]).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT3_SNR) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT4_PRN) {
                  result.append(tempPRN[3]).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT4_ELEV) {
                  result.append(tempElevation[3]).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT4_AZ) {
                  result.append(tempAzimuth[3]).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT4_SNR) {
                  result.append("0");
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.CHECKSUM) {
                  result.append(calcNMEAChecksum(result.toString()));
                }
              }
              result.append("\n");
            }
          } else if (inputSentence.endsWith("GPHDT")) {
            String separator = ",";
            for (int i = PGPS.FirmwareReferenceOEM16.GPHDT_COL.HEADER; i <= PGPS.FirmwareReferenceOEM16.GPHDT_COL.CHECKSUM; i++) {
              if (i == PGPS.FirmwareReferenceOEM16.GPHDT_COL.HEADER) {
                result.append("$GPHDT").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPHDT_COL.HEADING) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPHDT_COL.DEGREES_TRUE) {
                result.append("T");
              } else if (i == PGPS.FirmwareReferenceOEM16.GPHDT_COL.CHECKSUM) {
                result.append(calcNMEAChecksum(result.toString()));
              }
            }
          } else if (inputSentence.endsWith("GPRMB")) {
            String separator = ",";
            for (int i = PGPS.FirmwareReferenceOEM16.GPRMB_COL.HEADER; i <= PGPS.FirmwareReferenceOEM16.GPRMB_COL.CHECKSUM; i++) {
              if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.HEADER) {
                result.append("$GPRMB").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.DATA_STATUS) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.XTRACK_ERR) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.DIR) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.ORIGIN_ID) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.DEST_ID) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.DEST_LAT) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.LAT_DIR) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.DEST_LON) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.LON_DIR) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.RANGE) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.BEARING) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.VEL) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.ARR_STATUS) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.MODE_IND) {
                result.append("0");
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.CHECKSUM) {
                result.append(calcNMEAChecksum(result.toString()));
              }
            }
          } else if (inputSentence.endsWith("GPRMC")) {
            String separator = ",";
            for (int i = PGPS.FirmwareReferenceOEM16.GPRMC_COL.HEADER; i <= PGPS.FirmwareReferenceOEM16.GPRMC_COL.CHECKSUM; i++) {
              if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.HEADER) {
                result.append("$GPRMC").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.UTC) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.POS_STATUS) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.LAT) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.LAT_DIR) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.LON) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.LON_DIR) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.SPEED_KN) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.TRACK_TRUE) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.DATE) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.MAG_VAR) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.VAR_DIR) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.MODE_IND) {
                result.append("0");
              } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.CHECKSUM) {
                result.append(calcNMEAChecksum(result.toString()));
              }
            }
          } else if (inputSentence.endsWith("GPVTG")) {
            String separator = ",";
            for (int i = PGPS.FirmwareReferenceOEM16.GPVTG_COL.HEADER; i <= PGPS.FirmwareReferenceOEM16.GPVTG_COL.CHECKSUM; i++) {
              if (i == PGPS.FirmwareReferenceOEM16.GPVTG_COL.HEADER) {
                result.append("$GPVTG").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPVTG_COL.TRACK_TRUE) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPVTG_COL.T_INDICATOR) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPVTG_COL.TRACK_GOOD_DEG_MAGNETIC) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPVTG_COL.MAGNETIC_TRACK) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPVTG_COL.SPEED_KN) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPVTG_COL.NAUTICAL_SPEED_IND) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPVTG_COL.SPEED_KMH) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPVTG_COL.SPEED_INDICATOR) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPVTG_COL.POS_MODE) {
                result.append("0");
              } else if (i == PGPS.FirmwareReferenceOEM16.GPVTG_COL.CHECKSUM) {
                result.append(calcNMEAChecksum(result.toString()));
              }
            }
          } else if (inputSentence.endsWith("GPZDA")) {
            String separator = ",";
            for (int i = PGPS.FirmwareReferenceOEM16.GPZDA_COL.HEADER; i <= PGPS.FirmwareReferenceOEM16.GPZDA_COL.CHECKSUM; i++) {
              if (i == PGPS.FirmwareReferenceOEM16.GPZDA_COL.HEADER) {
                result.append("$GPZDA").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPZDA_COL.UTC) {
                result.append(simulatorData.getUTCCurrentTime()).append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPZDA_COL.DAY) {
                result.append(simulatorData.getCurrentDay()).append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPZDA_COL.MONTH) {
                result.append(simulatorData.getCurrentMonth()).append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPZDA_COL.YEAR) {
                result.append(simulatorData.getCurrentYear()).append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPZDA_COL.NULL1) {
                result.append("").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPZDA_COL.NULL2) {
                result.append("");
              } else if (i == PGPS.FirmwareReferenceOEM16.GPZDA_COL.CHECKSUM) {
                result.append(calcNMEAChecksum(result.toString()));
              }
            }
          } else if (inputSentence.endsWith("GPALM")) {
            String separator = ",";
            String separatorNewLine = "\r\n";
            LinkedList<GPSSatInView> tempResult = getSatsInView();
            int numberInSet = tempResult.size();
            for (int iSat = 1; iSat <= numberInSet; iSat++) {
              for (int i = PGPS.FirmwareReferenceOEM16.GPALM_COL.HEADER; i <= PGPS.FirmwareReferenceOEM16.GPALM_COL.CHECKSUM; i++) {
                if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.HEADER) {
                  result.append("$GPALM").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.NUMBER_MSG_LOG) {
                  result.append(numberInSet).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.NUMBER_CURRENT) {
                  result.append(iSat).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.PRN) {
                  result.append(tempResult.get(iSat - 1).getPrn()).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.REF_WEEK_NO) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.SV_HEALTH) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.ECC) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.ALM_REF_TIME) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.INC_ANGLE) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.OMEGA_DOT) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.RT_AXIS) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.OMEGA) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.LONG_ASC_NODE) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.MO_MEAN_ANOMALY) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.AF0_CLK_PAR) {
                  result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.AF1_CLK_PAR) {
                  result.append("0");
                } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.CHECKSUM) {
                  result.append(calcNMEAChecksum(result.toString()));
                }
              }
              result.append(separatorNewLine);
            }
          } else if (inputSentence.endsWith("GPGGA")) {
            SimulatorSpacecraftState simulatorSpacecraftState = getSpacecraftState();
            String separator = ",";
            for (int i = PGPS.FirmwareReferenceOEM16.GPGGA_COL.HEADER; i <= PGPS.FirmwareReferenceOEM16.GPGGA_COL.CHECKSUM; i++) {
              if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.HEADER) {
                result.append("$GPGGA").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.UTC) {
                result.append(simulatorData.getUTCCurrentTime()).append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.LAT) {
                result.append(PGPS.FirmwareReferenceOEM16.degrees2DDMMpMMMM(
                        Math.abs(simulatorSpacecraftState.getLatitude()))).append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.LAT_DIR) {
                result.append(simulatorSpacecraftState.getLatitude() >= 0 ? "N" : "S").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.LONG) {
                result.append(PGPS.FirmwareReferenceOEM16.degrees2DDDMMpMMMM(
                        Math.abs(simulatorSpacecraftState.getLongitude()))).append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.LONG_DIR) {
                result.append(simulatorSpacecraftState.getLongitude() >= 0 ? "E" : "W").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.QUAL) {
                result.append("1").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.SATS_IN_USE) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.HDOP) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.ALTITUDE) {
                result.append(String.format(Locale.ROOT, "%.2f", simulatorSpacecraftState.getAltitude())).append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.ALTITUDE_UNITS) {
                result.append("M").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.UNDULATION) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.UNDULATION_UNITS) {
                result.append("M").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.AGE_CORR_DATA) {
                result.append("").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.DIFF_BASESID) {
                result.append("");
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.CHECKSUM) {
                result.append(calcNMEAChecksum(result.toString()));
              }
            }
            /*
             * optimal but difficult to maintain result =
             * "$GPGGA,"+simulatorData.getUTCCurrentTime()+separator+ // #0
             * PGPS.degrees2DDMMpMMMM(Math.abs(orbitData.getlatitude()))+separator+ // #1
             * (orbitData.getlatitude()>=0 ? "N" : "S")+separator+ // #2
             * PGPS.degrees2DDMMpMMMM(Math.abs(orbitData.getlongitude()))+separator+ // #3
             * (orbitData.getlongitude()>=0 ? "W" : "E")+separator+ // #4
             * "1"+separator+//quality #5 "0"+separator+//# sats in use #6 "0"+separator+//#
             * hdop horizontal dilution of precision #7 String.format(Locale.ROOT,
             * "%.2f",orbitData.getGPSaltitude())+separator+//altitude from mean sea level
             * #8 "M"+separator+//units of antenna altitude #9 "0"+separator+//undulation -
             * relation between the geoid and the WGS84 ellipsoid #10 "M"+separator+//units
             * of undulation #11 ""+separator+//age of correction data [seconds] #12
             * ""+separator+//differential base station ID #13 "*XX"//checksum #14 ;
             */
          } else if (inputSentence.endsWith("GPGGALONG")) {
            SimulatorSpacecraftState simulatorSpacecraftState = getSpacecraftState();
            String separator = ",";
            for (int i = PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.HEADER; i <= PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.CHECKSUM; i++) {
              if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.HEADER) {
                result.append("$GPGGALONG").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.UTC) {
                result.append(simulatorData.getUTCCurrentTime()).append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.LAT) {
                result.append(PGPS.FirmwareReferenceOEM16.degrees2DDMMpMMMMMMM(
                        Math.abs(simulatorSpacecraftState.getLatitude()))).append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.LAT_DIR) {
                result.append(simulatorSpacecraftState.getLatitude() >= 0 ? "N" : "S").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.LONG) {
                result.append(PGPS.FirmwareReferenceOEM16.degrees2DDDMMpMMMMMMM(
                        Math.abs(simulatorSpacecraftState.getLongitude()))).append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.LONG_DIR) {
                result.append(simulatorSpacecraftState.getLongitude() >= 0 ? "E" : "W").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.QUAL) {
                result.append("1").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.SATS_IN_USE) {
                result.append(simulatorSpacecraftState.getSatsInView()).append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.HDOP) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.ALTITUDE) {
                result.append(String.format(Locale.ROOT, "%.3f", simulatorSpacecraftState.getAltitude())).append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.ALTITUDE_UNITS) {
                result.append("M").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.UNDULATION) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.UNDULATION_UNITS) {
                result.append("M").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.AGE_CORR_DATA) {
                result.append("").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.DIFF_BASESID) {
                result.append("");
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.CHECKSUM) {
                result.append(calcNMEAChecksum(result.toString()));
              }
            }
          } else if (inputSentence.endsWith("GPGGARTK")) {
            SimulatorSpacecraftState simulatorSpacecraftState = getSpacecraftState();
            String separator = ",";
            for (int i = PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.HEADER; i <= PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.CHECKSUM; i++) {
              if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.HEADER) {
                result.append("$GPGGARTK").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.UTC) {
                result.append(simulatorData.getUTCCurrentTime()).append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.LAT) {
                result.append(PGPS.FirmwareReferenceOEM16.degrees2DDMMpMMMMMMM(
                        Math.abs(simulatorSpacecraftState.getLatitude()))).append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.LAT_DIR) {
                result.append(simulatorSpacecraftState.getLatitude() >= 0 ? "N" : "S").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.LONG) {
                result.append(PGPS.FirmwareReferenceOEM16.degrees2DDDMMpMMMMMMM(
                        Math.abs(simulatorSpacecraftState.getLongitude()))).append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.LONG_DIR) {
                result.append(simulatorSpacecraftState.getLongitude() >= 0 ? "E" : "W").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.QUAL) {
                result.append("1").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.SATS_IN_USE) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.HDOP) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.ALTITUDE) {
                result.append(String.format(Locale.ROOT, "%.3f", simulatorSpacecraftState.getAltitude())).append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.ALTITUDE_UNITS) {
                result.append("M").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.NULL1) {
                result.append("0").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.NULL2) {
                result.append("M").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.AGE_DIFF_DATA) {
                result.append("").append(separator);
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.DIFF_BASESID) {
                result.append("");
              } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.CHECKSUM) {
                result.append(calcNMEAChecksum(result.toString()));
              }
            }
          } else {
            result = new StringBuilder("Sentence identifier [" + inputSentence + "] unknown");
            commandResult.setCommandFailed(true);
          }
          globalResult = result.toString();
          break;
        }
        case 2002: {// Origin [IGPS] Method [String getLastKnownPosition();//2002//Obtain the last
          // known position of the s/c]
          String result = "Placeholder";
          globalResult = result;
          break;
        }
        case 2003: {// Origin [IGPS] Method [String getBestXYZSentence();//2003//Obtain current
          // position in xyz coordinates]
          SimulatorSpacecraftState simulatorSpacecraftState = getSpacecraftState();
          double lat = Math.toRadians(simulatorSpacecraftState.getLatitude());
          double lon = Math.toRadians(simulatorSpacecraftState.getLongitude());
          double alt = simulatorSpacecraftState.getAltitude() / 1000.0;
          double e = Math.sqrt(1.0 - (EARTH_RADIUS_POLAR * EARTH_RADIUS_POLAR)
              / (EARTH_RADIUS_EQUATOR * EARTH_RADIUS_EQUATOR));
          double e_sqr = e * e;
          double sinLat = Math.sin(lat);
          double cosLat = Math.cos(lat);
          double sinLon = Math.sin(lon);
          double cosLon = Math.cos(lon);
          double r_n = EARTH_RADIUS_EQUATOR / Math.sqrt(1 - e_sqr * sinLat * sinLat);
          double x = (r_n + alt) * cosLat * cosLon * 1000; // multiply by thousand to get meters
          double y = (r_n + alt) * cosLat * sinLon * 1000;
          double z = ((1 - e_sqr) * r_n + alt) * sinLat * 1000;
          Vector3D vel = this.orekitCore.getOrbit().getPVCoordinates(this.orekitCore.earthFrameITRF)
              .getVelocity();// this.orekitCore.getOrbit().getPVCoordinates().getPosition();
          double velX = vel.getX();
          double velY = vel.getY();
          double velZ = vel.getZ();
          StringBuilder sb = new StringBuilder(generateOEMHeader("BESTXYZA", "FINESTEERING"));
          sb.append("SOL_COMPUTED,NARROW_INT,").append(x).append(",").append(y).append(",");
          sb.append(z).append(",0,0,0,SOL_COMPUTED,NARROW_INT,").append(velX).append(",");
          sb.append(velY).append(",").append(velZ).append(",0,0,0,\"AAAA\",0.250,1.000,0.000,");
          int satsInView = orekitCore.getSatsNoInView();
          sb.append(this.orekitCore.getGpsConstellation().size()).append(",").append(satsInView);
          sb.append(",").append(satsInView).append(",").append(satsInView).append(",0,01,0,33*");
          byte[] currentData = sb.toString().getBytes();
          Checksum cs = new CRC32();
          cs.update(currentData, 0, currentData.length);
          sb.append(String.format("%x", cs.getValue()));
          globalResult = sb.toString();
          break;
        }
        case 2004: {// Origin [IGPS] Method [String getTIMEASentence();//2004//Obtain UTC time info]
          StringBuilder sb = new StringBuilder(generateOEMHeader("TIMEA", "FINESTEERING"));
          sb.append("0,0,0,");
          String utc = Instant.now().toString();
          String[] yearTime = utc.split("T");
          String[] ymd = yearTime[0].split("-");
          String[] hms = yearTime[1].split(":");
          String year = ymd[0];
          String month = ymd[1];
          if (month.charAt(0) == '0') {
            month = month.substring(1);
          }
          String day = ymd[2];
          String hour = hms[0];
          String minute = hms[1];
          String ms = hms[2];
          ms = ms.replace("\\.", "");
          ms = ms.replace("Z", "");
          sb.append(year).append(",").append(month).append(",");
          sb.append(day).append(",").append(hour).append(",").append(minute).append(",");
          sb.append(ms).append(",VALID*");
          byte[] currentData = sb.toString().getBytes();
          Checksum cs = new CRC32();
          cs.update(currentData, 0, currentData.length);
          sb.append(String.format("%x", cs.getValue()));
          globalResult = sb.toString();
          break;
        }
        case 3001: {// Origin [ICamera] Method [byte[] takePicture(int width,int
          // height);//3001//High level command: file written to filesystem to request
          // camera take a picture]
          int width = (Integer) argObject.get(0);
          int height = (Integer) argObject.get(1);
          final int maxSize = CAMERA_MAX_SIZE;
          int size = width * height * 2;// [bytes]
          size = (size > maxSize ? maxSize : size);
          reloadImageBuffer();
          byte[] result = this.cameraBuffer.getDataAsByteArray(size);
          globalResult = result;
          if (this.cameraScriptPath != null) {
            new ProcessBuilder(this.cameraScriptPath).start();
          }
          break;
        }
        case 3002: {// Origin [ICamera] Method [void simPreloadPicture(String
          // fileName);//3002//Simulator helper command: preload into memory a raw camera
          // picture]
          String fileName = (String) argObject.get(0);
          if (!this.cameraBuffer.loadFromPath(fileName)) {
            throw new IOException();
          }
          break;
        }
        case 6002: {// Origin [ISDR] Method [void simPreloadFile(String fileName);//6002//Simulator
          // helper command: preload into memory a raw data file]
          String fileName = (String) argObject.get(0);
          if (!this.sdrBuffer.loadFromPath(fileName)) {
            throw new IOException();
          }
          break;
        }
        case 6003: {// Origin [ISDR] Method [double[] readFromBuffer(int numberSamples);//6003//Read
          // samples from operating buffer]
          int numberSamples = (Integer) argObject.get(0);
          double[] result = this.sdrBuffer.getDataAsDoubleArray(numberSamples * 2);
          globalResult = result;
          break;
        }
        case 7001: {// Origin [IOpticalReceiver] Method [byte[] runRawCommand(int cmdID,byte[]
          // data);//701//Low level command to interact with OpticalReceiver.]
          int cmdID = (Integer) argObject.get(0);
          byte[] data = (byte[]) argObject.get(1);
          byte[] result = new byte[0];
          globalResult = result;
          break;
        }
        case 7002: {// Origin [IOpticalReceiver] Method [void simSetMessageBuffer(byte[]
          // buffer);//702//Simulator method only: sets the operating buffer of the
          // optical receiver.]
          byte[] buffer = (byte[]) argObject.get(0);
          this.opticalReceiverModel.setOperatingBuffer(buffer);
          break;
        }
        case 7003: {// Origin [IOpticalReceiver] Method [void simSetDegradationRate(int
          // degradationRate);//703//Simulator method only: sets the chance a bit from the
          // operating buffer will be flipped upon read.]
          int degradationRate = (Integer) argObject.get(0);
          this.opticalReceiverModel.setSuccessRate(degradationRate);
          break;
        }
        case 7004: {// Origin [IOpticalReceiver] Method [byte[] readFromMessageBuffer(int
          // bytesNo);//704//Read bytesNo from operating buffer]
          int bytesNo = (Integer) argObject.get(0);
          byte[] result = this.opticalReceiverModel.getBytesFromBuffer(bytesNo);
          globalResult = result;
          break;
        }
        case 7005: {// Origin [IOpticalReceiver] Method [void simPreloadFile(String
          // fileName);//7005//Simulator helper command: preload into memory a raw data
          // file]
          String fileName = (String) argObject.get(0);
          if (!this.opticalReceiverModel.getSingleStreamOperatingBuffer().loadFromPath(fileName)) {
            throw new IOException();
          }
          break;
        }

        default:
          globalResult = "CommandID [" + c.getInternalID() + "] unknown";
          commandResult.setCommandFailed(true);
      }
    } catch (IndexOutOfBoundsException | IOException e) {
      String errorString = e.toString();
      commandResult.setOutput(errorString);
      commandResult.setCommandFailed(true);
    } catch (Exception e) {
      String errorString = e.toString();
      commandResult.setOutput(errorString);
      commandResult.setCommandFailed(true);
    }
    if (commandResult.isCommandFailed()) {
      this.logger.severe(commandResult.toExtString());
    }
    commandResult.setOutput(globalResult);
    return commandResult;
  }

  /**
   * Constructs the OEM6 message headers.
   *
   * @param msgType    Type of the message (e.g. BESTXYZA, TIMEA)
   * @param timeStatus Quality of reference time (e.g. SATTIME)
   * @return An ASCII message header.
   */
  public String generateOEMHeader(String msgType, String timeStatus)
  {
    StringBuilder sb = new StringBuilder("#");
    Calendar fdow = Calendar.getInstance();
    fdow.set(Calendar.HOUR_OF_DAY, 0);
    fdow.clear(Calendar.MINUTE);
    fdow.clear(Calendar.SECOND);
    fdow.clear(Calendar.MILLISECOND);
    fdow.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
    Calendar current = Calendar.getInstance();
    Calendar base = new GregorianCalendar(1980, 1, 5);
    Instant d1i = Instant.ofEpochMilli(base.getTimeInMillis());
    Instant d2i = Instant.ofEpochMilli(current.getTimeInMillis());

    LocalDateTime startDate = LocalDateTime.ofInstant(d1i, ZoneId.systemDefault());
    LocalDateTime endDate = LocalDateTime.ofInstant(d2i, ZoneId.systemDefault());

    long weeks = ChronoUnit.WEEKS.between(startDate, endDate);
    long seconds = current.getTimeInMillis() / 1000 - fdow.getTimeInMillis() / 1000;
    long millis = current.get(Calendar.MILLISECOND);

    sb.append(msgType).append(",COM1,").append("0,").append(35.0).append(",").append(timeStatus);
    sb.append(",").append(weeks).append(",").append(seconds).append(".").append(millis).append(",");
    sb.append("00100000,97b7,2310;"); // last fields are dummys

    return sb.toString();
  }

}
