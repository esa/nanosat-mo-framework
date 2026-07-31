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

import static org.hipparchus.util.FastMath.toDegrees;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import opssat.simulator.GPS;
import opssat.simulator.Orbit;
import opssat.simulator.OrbitParameters;
import opssat.simulator.celestia.CelestiaData;
import opssat.simulator.interfaces.ISimulatorDeviceData;
import opssat.simulator.interfaces.InternalData;
import opssat.simulator.models.OpticalReceiverModel;
import opssat.simulator.orekit.GPSSatInView;
import opssat.simulator.orekit.OrekitCore;
import opssat.simulator.peripherals.*;
import opssat.simulator.tcp.TCPServerReceiveOnly;
import opssat.simulator.util.*;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.errors.OrekitException;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.utils.TimeStampedPVCoordinates;

/**
 *
 * @author Cezar Suteu
 */
public class SimulatorNode extends TaskNode {

    private int counter;
    private boolean sendList;
    private boolean sendHeader;
    private LinkedList<File> interfaceFilesList;
    LinkedList<CommandDescriptor> commandsList;
    HashMap<Integer, CommandDescriptor> commandsById;
    private LinkedList<CommandDescriptor> commandsQueue;
    private LinkedList<CommandResult> commandsResults;
    private LinkedList<SimulatorDeviceData> simulatorDevices;
    // Periodic feeds (SimulatorData / scheduler / device / celestia) whose timer
    // elapsed in a cycle but that have not been emitted yet. dataOut() drains one
    // per call so feeds whose timers coincide are not dropped. Accessed only from
    // the single node thread.
    private final LinkedList<Object> pendingPeriodicOut = new LinkedList<>();
    SimulatorData simulatorData;
    SimulatorHeader simulatorHeader;
    HashMap<DevDatPBind, ArgumentDescriptor> hMapSDData;
    private int schedulerDataIndex;
    LinkedList<SimulatorSchedulerPiece> schedulerData;

    // Optical Receiver
    private OpticalReceiverModel opticalReceiverModel;
    // Orekit
    OrekitCore orekitCore;

    // Models
    private Orbit darkDusk;
    private GPS gps;

    // Simulator Data Bindings
    // Below is alphabetical order of interfaces, used to map GUI data
    private final static int INTERFACE_ONBOARDROUTER = 0;
    private final static int INTERFACE_CAMERA = 1;
    private final static int INTERFACE_FINEADCS = 2;
    private final static int INTERFACE_GPS = 3;
    private final static int INTERFACE_DPU = 4;
    private final static int INTERFACE_OPTICALRECEIVER = 5;
    private final static int INTERFACE_SDR = 6;

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

    String cameraScriptPath = null;
    private final static String OPS_SAT_SIMULATOR_DATA = File.separator + ".ops-sat-simulator" + File.separator;
    private final static String OPS_SAT_SIMULATOR_RESOURCES = OPS_SAT_SIMULATOR_DATA + "resources" + File.separator;

    private final static String OPSSAT_TLE_LINE1 = "1 44878U 19092F   20159.72929773  .00000725  00000-0  41750-4 0  9990";
    private final static String OPSSAT_TLE_LINE2 = "2 44878  97.4685 343.1680 0015119  36.0805 324.1445 15.15469997 26069";

    // Platform sim properties
    private Properties platformProperties;

    /**
     * Reads the platform properties or initializes them with default
     */
    private void loadPlatformProperties() {
        try {
            platformProperties = this.readProperties("platformsim.properties");
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not initialize platformsim.properties - using defaults.");
            platformProperties = new Properties();
            platformProperties.setProperty("platform.mode", "sim");
            platformProperties.setProperty("camerasim.imagemode", "Fixed");
            platformProperties.setProperty("camerasim.imagefile", "fix/me/earth.jpg");
            platformProperties.setProperty("camerasim.imagedirectory", "fix/me");
            platformProperties.setProperty("camera.adapter",
                    "esa.mo.platform.impl.provider.opssat.CameraOPSSATAdapter");
            updatePlatformConfig();
        }
    }

    /**
     * Reads the properties of the given .properties file.
     *
     * @param filename The properties file to read.
     */
    private Properties readProperties(String filename) throws FileNotFoundException, IOException {
        InputStream input = new FileInputStream(filename);
        Properties prop = new Properties();
        prop.load(input);
        return prop;
    }

    public static String getResourcesPath() {
        return System.getProperty("user.home") + OPS_SAT_SIMULATOR_RESOURCES;
    }

    public static String getDataPath() {
        return System.getProperty("user.home") + OPS_SAT_SIMULATOR_DATA;
    }

    public static String getWorkingDir() {
        return System.getProperty("user.dir");
    }

    public static String calcNMEAChecksum(String sentence) {
        char result = 0;
        // Trim "$" at the beginning of the sentence
        for (char c : sentence.substring(1).toCharArray()) {
            result ^= c;
        }
        return String.format("*%02X", (int) result);
    }

    public static String handleResourcePath(String path, Logger logger, ClassLoader classLoader, boolean replace) {
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

    LinkedList<GPSSatInView> getSatsInView() {
        LinkedList<GPSSatInView> tempResult = new LinkedList<>();
        if (this.simulatorHeader.isUseOrekitPropagator()) {
            tempResult = this.orekitCore.getSatsInViewAsList();
        } else {
            tempResult.add(new GPSSatInView("test", 100000));
        }
        return tempResult;
    }

    /**
     * Returns the current TLE.
     *
     * @return The current TLE.
     */
    public TLE getTLE() {
        if (this.simulatorHeader.isUseOrekitPropagator()) {
            return this.orekitCore.getTLE();
        } else {
            Logger.getLogger(SimulatorNode.class.getCanonicalName()).log(Level.WARNING,
                    "TLE only awailable in Simulator, wenn Using Orekit propagator!");
            return new TLE(OPSSAT_TLE_LINE1, OPSSAT_TLE_LINE2);
        }
    }

    public void runVectorTargetTracking(float x, float y, float z, float margin) {
        logger.log(Level.INFO, "Vector " + x + " " + y + " " + z);
        if (simulatorHeader.isUseOrekitPropagator()) {
            this.orekitCore.changeAttitudeVectorTarget(x, y, z, margin);
        }
    }

    public enum DevDatPBind {
        Camera_CameraBuffer, Camera_CameraBufferOperatingIndex, FineADCS_ModeOperation, FineADCS_PositionInertial,
        FineADCS_VelocityInertial, FineADCS_Q1, FineADCS_Q2, FineADCS_Q3, FineADCS_Q4, FineADCS_MagneticField,
        FineADCS_Rotation, FineADCS_Magnetometer, FineADCS_SunVector, FineADCS_ReactionWheels, FineADCS_Accelerometer,
        FineADCS_Gyro1, FineADCS_Gyro2, FineADCS_Magnetorquer, FineADCS_AngularMomentum, FineADCS_AngularVelocity,
        GPS_Latitude, GPS_Longitude, GPS_Altitude, GPS_GS_Elevation, GPS_GS_Azimuth, GPS_SatsInView,
        OpticalReceiver_OperatingBuffer, OpticalReceiver_OperatingBufferIndex, OpticalReceiver_DegradationRate,
        SDR_OperatingBuffer, SDR_OperatingBufferIndex
    }

    private void makeSimulatorDeviceBindings() {
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

        this.hMapSDData.put(DevDatPBind.FineADCS_Q2, simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
        this.hMapSDData.put(DevDatPBind.FineADCS_Q3, simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
        this.hMapSDData.put(DevDatPBind.FineADCS_Q4, simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
        this.hMapSDData.put(DevDatPBind.FineADCS_MagneticField, simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
        this.hMapSDData.put(DevDatPBind.FineADCS_Rotation, simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
        this.hMapSDData.put(DevDatPBind.FineADCS_Magnetometer, simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
        this.hMapSDData.put(DevDatPBind.FineADCS_SunVector, simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
        this.hMapSDData.put(DevDatPBind.FineADCS_ReactionWheels, simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
        this.hMapSDData.put(DevDatPBind.FineADCS_Accelerometer, simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
        this.hMapSDData.put(DevDatPBind.FineADCS_Gyro1, simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
        this.hMapSDData.put(DevDatPBind.FineADCS_Gyro2, simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
        this.hMapSDData.put(DevDatPBind.FineADCS_Magnetorquer, simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
        this.hMapSDData.put(DevDatPBind.FineADCS_AngularMomentum, simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));
        this.hMapSDData.put(DevDatPBind.FineADCS_AngularVelocity, simulatorDevices.get(INTERFACE_FINEADCS).getDataList().get(i++));

        i = 0;
        this.hMapSDData.put(DevDatPBind.GPS_Latitude, simulatorDevices.get(INTERFACE_GPS).getDataList().get(i++));
        this.hMapSDData.put(DevDatPBind.GPS_Longitude, simulatorDevices.get(INTERFACE_GPS).getDataList().get(i++));
        this.hMapSDData.put(DevDatPBind.GPS_Altitude, simulatorDevices.get(INTERFACE_GPS).getDataList().get(i++));
        this.hMapSDData.put(DevDatPBind.GPS_GS_Elevation, simulatorDevices.get(INTERFACE_GPS).getDataList().get(i++));
        this.hMapSDData.put(DevDatPBind.GPS_GS_Azimuth, simulatorDevices.get(INTERFACE_GPS).getDataList().get(i++));
        this.hMapSDData.put(DevDatPBind.GPS_SatsInView, simulatorDevices.get(INTERFACE_GPS).getDataList().get(i++));

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

    private synchronized void initModels() {
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
        this.darkDusk = new Orbit(OPS_SAT_A, OPS_SAT_ORBIT_I * (Math.PI / 180), (OPS_SAT_RAAN) * (Math.PI / 180),
                (OPS_SAT_ARG_PER) * (Math.PI / 180), 0, OPS_SAT_TRUE_ANOMALY, simulatorHeader.getStartDateString());
        this.gps = new GPS(darkDusk);

        if (this.simulatorHeader.isUseOrekitPropagator()) {
            this.logger.log(Level.FINE, "Calling orekit constructor");
            try {
                this.orekitCore = new OrekitCore(OPS_SAT_A * 1000, OPS_SAT_E, OPS_SAT_ORBIT_I, OPS_SAT_ARG_PER,
                        OPS_SAT_RAAN, OPS_SAT_TRUE_ANOMALY, simulatorHeader, this.logger, this);
                this.logger.log(Level.FINE, "orekit initialized successfully");
                this.orekitCore.processPropagateStep(0);
            } catch (OrekitException exception) {
                this.logger.log(Level.SEVERE, "orekit initialization failed from [" + exception
                        + "]! Switching module off");
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

    Logger logger;

    private NMEAFormatter nmeaFormatter;
    private FineADCSCommandHandler fineADCSHandler;
    private ConfigurationManager configurationManager;

    public SimulatorNode(ConcurrentLinkedQueue<Object> queueIn, ConcurrentLinkedQueue<Object> queueOut, String name,
            int delay, Level logLevel, Level consoleLogLevel) {
        super(queueIn, queueOut, name, delay, logLevel, consoleLogLevel);
        benchmarkStartupTime = System.currentTimeMillis();
        this.logger = super.getLogObject();
        super.getTimers().put(TIMER_SIMULATOR_DATA, new SimulatorTimer(TIMER_SIMULATOR_DATA,
                TIMER_SIMULATOR_DATA_INTERVAL));
        super.getTimers().put(TIMER_DEVICE_DATA, new SimulatorTimer(TIMER_DEVICE_DATA, TIMER_DEVICE_DATA_INTERVAL));
        super.getTimers().put(TIMER_SCHEDULER_DATA, new SimulatorTimer(TIMER_SCHEDULER_DATA,
                TIMER_SCHEDULER_DATA_INTERVAL));
        super.getTimers().put(TIMER_CELESTIA_DATA, new SimulatorTimer(TIMER_CELESTIA_DATA, TIMER_CELESTIA_INTERVAL));
        // super.getTimers().put(TIMER_SCIENCE1_DATA, new
        // SimulatorTimer(TIMER_SCIENCE1_DATA, TIMER_SCIENCE1_DATA_INTERVAL));
        interfaceFilesList = new LinkedList<>();
        simulatorDevices = new LinkedList<>();
        commandsList = new LinkedList<>();
        commandsQueue = new LinkedList<>();
        commandsResults = new LinkedList<>();
        configurationManager = new ConfigurationManager(this);
        nmeaFormatter = new NMEAFormatter(this);
        fineADCSHandler = new FineADCSCommandHandler(this);
        String workingdir = System.getProperty("user.dir");
        this.logger.log(Level.ALL, "Workingdir is [" + workingdir + "]");
        File interfacesFolderCheck = new File(workingdir);
        this.logger.log(Level.ALL, "Location is " + interfacesFolderCheck.getName());
        loadMethodsFromReflection();
        configurationManager.loadTemplatesFromFile(getTemplatesFile());
        configurationManager.loadSchedulerFromFile(getSchedulerFile());
        simulatorData = new SimulatorData(0);

        configurationManager.loadSimulatorHeader();
        simulatorData.initFromHeader(simulatorHeader);
        makeSimulatorDeviceBindings();
        configurationManager.loadSimulatorCommandsFilter();
        this.cameraBuffer = new EndlessSingleStreamOperatingBuffer(this.logger);
        loadPlatformProperties();
        // Models
        initModels();

    }

    private void loadMethodsFromReflection() {
        this.logger.log(Level.FINE, "loadMethodsFromReflection");

        reflectObjectGetMethods(new POnBoardRouter(null, "On-Board Router"));
        reflectObjectGetMethods(new PCamera(null, "Camera"));
        reflectObjectGetMethods(new PFineADCS(null, "FineADCS"));
        reflectObjectGetMethods(new PGPS(null, "GPS"));
        reflectObjectGetMethods(new PDPU(null, "DPU"));
        reflectObjectGetMethods(new POpticalReceiver(null, "OpticalReceiver"));
        reflectObjectGetMethods(new PSDR(null, "SDR"));

        commandsList.sort(Comparator.comparingInt(CommandDescriptor::getInternalID));
        commandsById = new HashMap<>();
        for (CommandDescriptor c : commandsList) {
            commandsById.put(c.getInternalID(), c);
        }
        loadMethodsDescriptionFromResources();
    }

    private void putDescriptionIntoMethod(String description, int internalID) {
        CommandDescriptor c = commandsById.get(internalID);
        if (c != null) {
            c.setComment(description);
        }
    }

    /**
     * Updates the configuration parameters of the peripheral simulators (e.g.
     * Camera).
     */
    public void updatePlatformConfig() {
        try {
            this.writeProperties(new File("platformsim.properties"), this.platformProperties);
        } catch (IOException e) {
            Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, "Could not save platform properties");
        }
        reloadImageBuffer();
    }

    public void writeProperties(File file, Properties props) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        props.store(fos, null);
    }

    /**
     * Reloads the cameraBuffer to contain either the selected or a random
     * image.
     */
    private void reloadImageBuffer() {
        String mode = platformProperties.getProperty("camerasim.imagemode");
        String path;
        if (mode.equals("Fixed")) {
            path = platformProperties.getProperty("camerasim.imagefile");
            this.cameraBuffer.loadImageFromAbsolutePath(path);
        } else {
            path = platformProperties.getProperty("camerasim.imagedirectory");
            try {
                Stream<Path> walker = Files.walk(Paths.get(path));
                List<String> files = walker.map(p -> p.getFileName().toString()).filter(s -> s.toLowerCase().endsWith(
                        ".png") || s.toLowerCase().endsWith(".jpg") || s.toLowerCase().endsWith("bmp") || s.toLowerCase()
                        .endsWith(".raw")).collect(Collectors.toList());
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

    private ArrayList<String> readLinesFromInputStream(InputStream fileName) {

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
                        String[] lineWords = line.split(" ");
                        if (lineWords.length > 1) {
                            if (lineWords[0].equals("void") || lineWords[0].equals("byte[]")) {
                                String[] lineWords2 = line.split("//");
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
            this.logger.log(Level.WARNING, "InputStream [" + fileName.toString() + "] could not be accessed!");
            return null;
        }
    }

    private void loadMethodsDescriptionFromResources() {
        String fileName = "descriptions.txt";
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream istream = classLoader.getResourceAsStream(fileName);
        if (istream != null) {
            readLinesFromInputStream(istream);
        } else {
            this.logger.log(Level.WARNING, "Error reading resource file!");
        }
    }

    private void reflectObjectGetMethods(Object targetObject) {
        String name = ((GenericPeripheral) targetObject).getName();
        this.logger.log(Level.FINE, "reflectObjectGetMethods from [" + name + "]");
        SimulatorDeviceData simulatorDeviceData = new SimulatorDeviceData(name);
        simulatorDevices.add(simulatorDeviceData);
        ISimulatorDeviceData simulatorDeviceDataAnnotation = targetObject.getClass().getAnnotation(
                ISimulatorDeviceData.class);
        if (simulatorDeviceDataAnnotation != null) {
            for (String str : simulatorDeviceDataAnnotation.descriptors()) {
                String[] split = str.split(":");
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

                    CommandDescriptor commandDescriptor = new CommandDescriptor(name, body, internalData.description(),
                            internalData.internalID(), super.getLogObject());
                    this.logger.log(Level.FINE, "Added method [" + commandDescriptor.toString() + "]");
                    // System.out.println(commandDescriptor.toCustomFormat1());

                    commandsList.add(commandDescriptor);
                }

            }

        }
    }

    private File getFileFromDirAndPath(String workingDir, String targetPath) {
        // this.logger.log(Level.INFO, "Opening path [" + result.getAbsolutePath() +
        // "].");
        return new File(workingDir, targetPath);
    }

    public File getGPSOpsFile() {
        return getFileFromDirAndPath(getResourcesPath(), "gps-ops.txt");
    }

    File getSchedulerFile() {
        return getFileFromDirAndPath(getWorkingDir(), "_OPS-SAT-SIMULATOR-scheduler.txt");
    }

    File getSchedulerFileAsBackup() {
        String now = new SimpleDateFormat("yyyy_MMdd_HHmmss").format(new Date());
        return getFileFromDirAndPath(getWorkingDir(), "_OPS-SAT-SIMULATOR-scheduler_backup_" + now + ".txt");
    }

    File getTemplatesFile() {
        return getFileFromDirAndPath(getWorkingDir(), "_OPS-SAT-SIMULATOR-templates.txt");
    }

    File getHeaderFile() {
        return getFileFromDirAndPath(getWorkingDir(), "_OPS-SAT-SIMULATOR-header.txt");
    }

    File getCommandsFilterFile() {
        return getFileFromDirAndPath(getWorkingDir(), "_OPS-SAT-SIMULATOR-filter.txt");
    }

    private boolean isValidCommandID(int commandID) {
        return true;
    }


    @Override
    void dataIn(Object obj) {
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
            configurationManager.writeTemplatesToFile(obj);
            commandsList.clear();
            simulatorDevices.clear();
            loadMethodsFromReflection();
            configurationManager.loadTemplatesFromFile(getTemplatesFile());
            configurationManager.loadSimulatorCommandsFilter();
            makeSimulatorDeviceBindings();
            sendList = true;
        } else if (obj instanceof SimulatorHeader) {
            this.logger.log(Level.FINE, "SimulatorNode Received " + obj.toString());
            simulatorHeader = (SimulatorHeader) obj;
            simulatorData.initFromHeader(simulatorHeader);
            sendHeader = true;
            configurationManager.writeHeader(getHeaderFile());
            initModels();
        }

    }

    public static String dump(Object o, int callCount) {
        callCount++;
        StringBuilder tabs = new StringBuilder();
        for (int k = 0; k < callCount; k++) {
            tabs.append("\t");
        }
        StringBuilder buffer = new StringBuilder();
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
                if (value.getClass().isPrimitive()
                        || value.getClass() == java.lang.Long.class
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
                            if (value.getClass().isPrimitive()
                                    || value.getClass() == java.lang.Long.class
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

    private String getArgDescriptionForSchedulerPiece(SimulatorSchedulerPiece piece) {
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

    void schedulerPollData() {
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
    void coreRun() {
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
                    this.logger.log(Level.FINE, "BenchmarkStartup;Counter [" + counter + "];TimeElapsed [" + (System
                            .currentTimeMillis() - benchmarkStartupTime) + "] ms");

                }
                if (benchmarkInProgress) {
                    benchmarkTimeElapsed += timeElapsed;
                    if (counter >= BENCHMARK_START_COUNTER + BENCHMARK_COUNTER_EVALUATIONS) {
                        benchmarkFinished = true;
                        this.logger.log(Level.FINE, "BenchmarkFinished;TimeElapsed [" + benchmarkTimeElapsed
                                + "] ms;Counter [" + counter + "];Steps [" + BENCHMARK_COUNTER_EVALUATIONS + "]");
                        if (this.simulatorHeader.isUseOrekitPropagator()) {
                            this.logger.log(Level.FINE, "BenchmarkFinished;Orekit GPS constellation propagations ["
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
    Object dataOut() {
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
        // Collect every periodic feed whose timer elapsed this cycle and emit one
        // per call. manageTime() resets all timers each loop regardless of what
        // dataOut() returned, and DEVICE_DATA (1000 ms) and SCHEDULER_DATA (5000 ms)
        // are exact multiples of SIMULATOR_DATA (500 ms): returning only the first
        // elapsed feed let SimulatorData win every time they coincided, so device
        // and scheduler data were never sent. Queue the extras and drain one per
        // loop so they all get through.
        if (super.getTimers().get(TIMER_SIMULATOR_DATA).isElapsed()) {
            pendingPeriodicOut.add(simulatorData);
        }
        if (super.getTimers().get(TIMER_SCHEDULER_DATA).isElapsed()) {
            pendingPeriodicOut.add(schedulerData);
        }
        if (super.getTimers().get(TIMER_CELESTIA_DATA).isElapsed() && simulatorHeader.isUseCelestia() && simulatorHeader
                .isUseOrekitPropagator()) {
            if (orekitCore.isIsInitialized()) {
                SimulatorSpacecraftState simulatorSpacecraftState = getSpacecraftState();
                CelestiaData celestiaData = new CelestiaData(simulatorSpacecraftState.getRv(), simulatorSpacecraftState
                        .getQ());
                celestiaData.setDate(simulatorData.getCurrentTime());
                celestiaData.setAnx(orekitCore.getNextAnx());
                celestiaData.setDnx(orekitCore.getNextDnx());
                celestiaData.setAos(orekitCore.getNextAOS());
                celestiaData.setLos(orekitCore.getNextLOS());
                celestiaData.setInfo("Time|x" + this.simulatorData.getTimeFactor() + "|" + orekitCore.getOrekitInfo());
                pendingPeriodicOut.add(celestiaData);
            }
        }
        if (super.getTimers().get(TIMER_DEVICE_DATA).isElapsed()) {
            this.hMapSDData.get(DevDatPBind.Camera_CameraBuffer).setType(this.cameraBuffer.getDataBufferAsString());
            this.hMapSDData.get(DevDatPBind.Camera_CameraBufferOperatingIndex).setType(this.cameraBuffer.getOperatingIndex());
            SimulatorSpacecraftState simulatorSpacecraftState = getSpacecraftState();
            float[] p = new float[3];
            float[] v = new float[3];
            float[] q = new float[4];
            p = simulatorSpacecraftState.getR();
            v = simulatorSpacecraftState.getV();
            q = simulatorSpacecraftState.getQ();
            this.hMapSDData.get(DevDatPBind.FineADCS_ModeOperation).setType(simulatorSpacecraftState.getModeOperation());
            this.hMapSDData.get(DevDatPBind.FineADCS_PositionInertial).setType(p);
            this.hMapSDData.get(DevDatPBind.FineADCS_VelocityInertial).setType(v);
            this.hMapSDData.get(DevDatPBind.FineADCS_Q1).setType(String.valueOf(q[0]));
            this.hMapSDData.get(DevDatPBind.FineADCS_Q2).setType(String.valueOf(q[1]));
            this.hMapSDData.get(DevDatPBind.FineADCS_Q3).setType(String.valueOf(q[2]));
            this.hMapSDData.get(DevDatPBind.FineADCS_Q4).setType(String.valueOf(q[3]));
            this.hMapSDData.get(DevDatPBind.FineADCS_MagneticField).setType(String.valueOf(simulatorSpacecraftState.getMagField()));
            this.hMapSDData.get(DevDatPBind.FineADCS_Rotation).setType(simulatorSpacecraftState.getRotationAsString());
            this.hMapSDData.get(DevDatPBind.FineADCS_Magnetometer).setType(String.valueOf(simulatorSpacecraftState.getMagnetometerAsString()));
            this.hMapSDData.get(DevDatPBind.FineADCS_SunVector).setType(String.valueOf(simulatorSpacecraftState.getSunVectorAsString()));

            this.hMapSDData.get(DevDatPBind.GPS_Latitude).setType(simulatorSpacecraftState.getLatitude());
            this.hMapSDData.get(DevDatPBind.GPS_Longitude).setType(simulatorSpacecraftState.getLongitude());
            this.hMapSDData.get(DevDatPBind.GPS_Altitude).setType(String.valueOf(simulatorSpacecraftState.getAltitude()));

            if (simulatorHeader.isUseOrekitPropagator()) {
                this.hMapSDData.get(DevDatPBind.GPS_GS_Elevation).setType(orekitCore.getCurrentGSElevation());
                this.hMapSDData.get(DevDatPBind.GPS_GS_Azimuth).setType(orekitCore.getCurrentGSAzimuth());
                this.hMapSDData.get(DevDatPBind.GPS_SatsInView).setType(String.valueOf(orekitCore.getSatsInView()));
            }
            this.hMapSDData.get(DevDatPBind.GPS_Altitude).setType(String.valueOf(simulatorSpacecraftState.getAltitude()));
            this.hMapSDData.get(DevDatPBind.OpticalReceiver_OperatingBuffer)
                    .setType(this.opticalReceiverModel.getSingleStreamOperatingBuffer().getDataBufferAsString());
            this.hMapSDData.get(DevDatPBind.OpticalReceiver_OperatingBufferIndex)
                    .setType(this.opticalReceiverModel.getSingleStreamOperatingBuffer().getOperatingIndex());
            this.hMapSDData.get(DevDatPBind.OpticalReceiver_DegradationRate)
                    .setType(this.opticalReceiverModel.getDegradationRate());

            this.hMapSDData.get(DevDatPBind.SDR_OperatingBuffer).setType(this.sdrBuffer.getDataBufferAsString());
            this.hMapSDData.get(DevDatPBind.SDR_OperatingBufferIndex).setType(this.sdrBuffer.getOperatingIndex());

            pendingPeriodicOut.add(simulatorDevices);
        }
        return pendingPeriodicOut.poll();
    }

    public long getSimulatedTime() {
        return this.simulatorData.getCurrentTime().getTime();
    }

    public int getTimeFactor() {
        return this.simulatorData.getTimeFactor();
    }

    public SimulatorSpacecraftState getSpacecraftState() {
        if (this.simulatorHeader.isUseOrekitPropagator()) {
            GeodeticPoint result = this.orekitCore.getGeodeticPoint();
            SimulatorSpacecraftState data = new SimulatorSpacecraftState(
                    toDegrees(result.getLatitude()),
                    toDegrees(result.getLongitude()),
                    result.getAltitude());
            data.setRv(this.orekitCore.getOrbit().getPVCoordinates().getPosition(),
                    this.orekitCore.getOrbit().getPVCoordinates().getVelocity());
            float[] q = new float[4];
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
            OrbitParameters orbitData = this.gps.getPosition(simulatorData.getCurrentTime());
            return new SimulatorSpacecraftState(
                    orbitData.getLatitude(),
                    orbitData.getLongitude(),
                    orbitData.getAltitude() * 1000);
        }
    }

    // Globals
    public Object runGenericMethod(int internalID, ArrayList<Object> argObject) {
        CommandDescriptor c = new CommandDescriptor("external", "external",
                "external", internalID, super.getLogObject());
        c.setInputArgsFromArrayList(argObject);
        CommandResult r = runGenericMethodForCommand(c);
        return r.getOutput();
    }

    public Object runGenericMethod(int internalID, String argObjectDescription) {
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

    public synchronized CommandResult runGenericMethodForCommand(CommandDescriptor c) {
        simulatorData.incrementMethods();
        ArrayList<Object> argObject = c.getInputArgObjList();
        this.logger.log(Level.FINE, "runGenericMethod;identifier;" + c.getInternalID() + ";" + c.getInputArgs());
        CommandResult commandResult = new CommandResult(c, new Date(), simulatorData.getCurrentTime());
        Object globalResult = null;
        try {

            switch (c.getInternalID()) {

                case 1001: case 1002: case 1003: case 1004: case 1005: case 1006: case 1007: case 1008:
                case 1009: case 1010: case 1011: case 1012: case 1013: case 1014: case 1015: case 1016:
                case 1017: case 1018: case 1019: case 1020: case 1021: case 1022: case 1023: case 1024:
                case 1025: case 1026: case 1027: case 1028: case 1029: case 1030: case 1031: case 1032:
                case 1033: case 1034: case 1035: case 1036: case 1037: case 1038: case 1039: case 1040:
                case 1041: case 1042: case 1043: case 1044: case 1045: case 1046: case 1047: case 1048:
                case 1049: case 1050: case 1051: case 1052: case 1053: case 1054: case 1055: case 1056:
                case 1057: case 1058: case 1059: case 1060: case 1061: case 1062: case 1063: case 1064:
                case 1065: case 1066: case 1067: case 1068: case 1069: case 1070: case 1071: case 1072:
                case 1073: case 1074: case 1075: case 1076: case 1077: case 1078: case 1079: case 1080:
                case 1081: case 1082: case 1083: case 1084: case 1085: case 1086: case 1087: case 1088:
                case 1089: case 1090: case 1091: case 1092: case 1093: case 1094: case 1095: case 1096:
                case 1097: case 1098: case 1099: case 1100: case 1101: case 1102: case 1103: case 1104:
                case 1105: case 1106: case 1107: case 1108: case 1109: case 1110: case 1111: case 1112:
                case 1113: case 1114: case 1115: case 1116: case 1117: case 1118: case 1119: case 1120:
                case 1121: case 1122: case 1123: case 1124: case 1125: case 1126: case 1127: case 1128:
                case 1129: case 1130: case 1131: case 1132: case 1133: case 1134: case 1135: case 1136:
                case 1137: case 1138: case 1139: case 1140: case 1141: case 1142: case 1143: case 1144:
                case 1145: case 1146: case 1147: case 1148: case 1149: case 1150: case 1151: case 1152:
                case 1153: case 1154: case 1155: case 1156: case 1157: case 1158: case 1159: case 1160:
                case 1161: case 1162: case 1163: case 1164: case 1165: case 1166: case 1167: case 1168:
                case 1169: case 1170: case 1171: case 1172: case 1173: case 1174: case 1175: case 1176:
                case 1177: case 1178: case 1179: case 1180: case 1181: case 1182: case 1183: case 1184:
                case 1185: case 1186: case 1187: case 1188: case 1189: case 1190: case 1191: case 1192:
                case 1193: case 1194: case 1195: case 1196: case 1197: case 1198: case 1199: case 1200:
                case 1201: case 1202: case 1203: case 1204: {
                    globalResult = fineADCSHandler.handle(c.getInternalID(), argObject, commandResult);
                    break;
                }

                case 2001: {// Origin [IGPS] Method [String getNMEASentence(String
                    // inputSentence);//201//Obtain a NMEA response for a given NMEA sentence]
                    String inputSentence = (String) argObject.get(0);
                    globalResult = nmeaFormatter.format(inputSentence, commandResult);
                    break;
                }
                case 2002: {// Origin [IGPS] Method [String getLastKnownPosition();//2002//Obtain the last
                    // known position of the s/c]
                    globalResult = "Placeholder";
                    break;
                }
                case 2003: {// Origin [IGPS] Method [String getBestXYZSentence();//2003//Obtain current
                    // position in xyz coordinates]
                    SimulatorSpacecraftState simulatorSpacecraftState = getSpacecraftState();
                    double lat = Math.toRadians(simulatorSpacecraftState.getLatitude());
                    double lon = Math.toRadians(simulatorSpacecraftState.getLongitude());
                    double alt = simulatorSpacecraftState.getAltitude() / 1000.0;
                    double e = Math.sqrt(1.0 - (EARTH_RADIUS_POLAR * EARTH_RADIUS_POLAR) / (EARTH_RADIUS_EQUATOR * EARTH_RADIUS_EQUATOR));
                    double e_sqr = e * e;
                    double sinLat = Math.sin(lat);
                    double cosLat = Math.cos(lat);
                    double sinLon = Math.sin(lon);
                    double cosLon = Math.cos(lon);
                    double r_n = EARTH_RADIUS_EQUATOR / Math.sqrt(1 - e_sqr * sinLat * sinLat);
                    double x = (r_n + alt) * cosLat * cosLon * 1000; // multiply by thousand to get meters
                    double y = (r_n + alt) * cosLat * sinLon * 1000;
                    double z = ((1 - e_sqr) * r_n + alt) * sinLat * 1000;
                    org.orekit.orbits.Orbit orbit = this.orekitCore.getOrbit();
                    TimeStampedPVCoordinates coord = orbit.getPVCoordinates(this.orekitCore.earthFrameITRF);
                    Vector3D vel = coord.getVelocity();
                    // this.orekitCore.getOrbit().getPVCoordinates().getPosition();
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
                    sb.append("VALID,");
                    sb.append("0,0,-18.00000000000,");
                    String year = simulatorData.getCurrentYear();
                    String month = simulatorData.getCurrentMonth();
                    String day = simulatorData.getCurrentDay();
                    String hour = simulatorData.getUTCCurrentHour();
                    String minute = simulatorData.getUTCCurrentMinute();
                    String ms = simulatorData.getUTCCurrentSecond() + simulatorData.getUTCCurrentMillis();
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
                    globalResult = this.cameraBuffer.getDataAsByteArray(size);
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
                    globalResult = this.sdrBuffer.getDataAsDoubleArray(numberSamples * 2);
                    break;
                }
                case 7001: {// Origin [IOpticalReceiver] Method [byte[] runRawCommand(int cmdID,byte[]
                    // data);//701//Low level command to interact with OpticalReceiver.]
                    int cmdID = (Integer) argObject.get(0);
                    byte[] data = (byte[]) argObject.get(1);
                    globalResult = new byte[0];
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
                    globalResult = this.opticalReceiverModel.getBytesFromBuffer(bytesNo);
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
        } catch (Exception e) {
            Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, "Something went wrong...", e);
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
     * @param msgType Type of the message (e.g. BESTXYZA, TIMEA)
     * @param timeStatus Quality of reference time (e.g. SATTIME)
     * @return An ASCII message header.
     */
    public String generateOEMHeader(String msgType, String timeStatus) {
        StringBuilder sb = new StringBuilder("#");
        long gpsTime = simulatorData.getCurrentTime().getTime() - simulatorData.getUtcOffsetInMillis();
        Calendar fdow = Calendar.getInstance();
        fdow.setTimeInMillis(gpsTime);
        fdow.set(Calendar.HOUR_OF_DAY, 0);
        fdow.clear(Calendar.MINUTE);
        fdow.clear(Calendar.SECOND);
        fdow.clear(Calendar.MILLISECOND);
        fdow.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        Calendar current = Calendar.getInstance();
        current.setTimeInMillis(gpsTime);

        Calendar base = new GregorianCalendar(1980, 1, 5);
        Instant d1i = Instant.ofEpochMilli(base.getTimeInMillis());
        Instant d2i = Instant.ofEpochMilli(current.getTimeInMillis());

        LocalDateTime startDate = LocalDateTime.ofInstant(d1i, ZoneId.systemDefault());
        LocalDateTime endDate = LocalDateTime.ofInstant(d2i, ZoneId.systemDefault());

        long weeks = ChronoUnit.WEEKS.between(startDate, endDate);
        long seconds = current.getTimeInMillis() / 1000 - fdow.getTimeInMillis() / 1000;
        DateFormat df = new SimpleDateFormat("SSS");
        String millis = df.format(current.getTime());

        sb.append(msgType).append(",COM1,").append("0,").append(35.0).append(",").append(timeStatus);
        sb.append(",").append(weeks).append(",").append(seconds).append(".").append(millis).append(",");
        sb.append("00100000,97b7,2310;"); // last fields are dummys

        return sb.toString();
    }

}
