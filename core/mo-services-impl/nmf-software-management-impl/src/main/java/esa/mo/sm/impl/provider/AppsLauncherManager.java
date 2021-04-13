/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
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
package esa.mo.sm.impl.provider;

import esa.mo.sm.impl.util.ClosingAppListener;
import esa.mo.com.impl.consumer.EventConsumerServiceImpl;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.DefinitionsManager;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.common.impl.util.HelperCommon;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.helpertools.misc.Const;
import esa.mo.sm.impl.util.OSValidator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.exec.environment.EnvironmentUtils;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.common.directory.structures.AddressDetailsList;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.common.directory.structures.ServiceCapabilityList;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.softwaremanagement.SoftwareManagementHelper;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.provider.StopAppInteraction;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.structures.AppDetails;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.structures.AppDetailsList;

/**
 *
 * @author Cesar Coelho
 */
public class AppsLauncherManager extends DefinitionsManager
{

  private static final int APP_STOP_TIMEOUT = 15000;
  private static final Logger LOGGER = Logger.getLogger(AppsLauncherManager.class.getName());

  private final OSValidator osValidator = new OSValidator();

  private static final String FOLDER_LOCATION_PROPERTY
      = "esa.mo.sm.impl.provider.appslauncher.FolderLocation";
  private static final String DEFAULT_APPS_FOLDER_PATH
      = ".." + File.separator + ".." + File.separator + "apps";
  /**
   * Location of the apps folder, relative to the MO Supervisor
   */
  private File appsFolderPath;
  private final HashMap<Long, ProcessExecutionHandler> handlers
      = new HashMap<>();

  private AtomicLong uniqueObjIdDef; // Counter

  public AppsLauncherManager(COMServicesProvider comServices)
  {
    super(comServices);

    // If there is a property for that, then use it!!
    if (System.getProperty(FOLDER_LOCATION_PROPERTY) != null) {
      appsFolderPath = new File(System.getProperty(FOLDER_LOCATION_PROPERTY));
    } else {
      LOGGER.log(Level.INFO,
          "Property not set: {0} \nUsing default apps directory: {1}",
          new Object[]{FOLDER_LOCATION_PROPERTY, DEFAULT_APPS_FOLDER_PATH});
      appsFolderPath = new File(DEFAULT_APPS_FOLDER_PATH);
    }

    if (MALContextFactory.lookupArea(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NAME,
                                     SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_VERSION) != null &&
                    MALContextFactory.lookupArea(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NAME,
                      SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_VERSION).getServiceByName(
                              AppsLauncherHelper.APPSLAUNCHER_SERVICE_NAME) == null)
    {
      try
      {
        AppsLauncherHelper.init(MALContextFactory.getElementFactoryRegistry());
      }
      catch (MALException ex)
      {
        LOGGER.log(Level.SEVERE, "Unexpectedly AppsLauncherHelper already initialized!?", ex);
      }
    }

    if (super.getArchiveService() == null) {  // No Archive?
      this.uniqueObjIdDef = new AtomicLong(0);
    } else {
      // With Archive...
    }

  }

  protected AppDetailsList getAll()
  {
    return (AppDetailsList) this.getAllDefs();
  }

  @Override
  public Boolean compareName(Long objId, Identifier name)
  {
    return this.get(objId).getName().equals(name);
  }

  @Override
  public ElementList newDefinitionList()
  {
    return new AppDetailsList();
  }

  public AppDetails get(Long input)
  {
    return (AppDetails) this.getDef(input);
  }

  protected Long addApp(final AppDetails definition, final ObjectId source, final URI uri)
  {
    Long objId = null;
    Long related = null;

    if (definition.getExtraInfo() != null) {
      try { // Read the provider.properties of the app
        objId = readAppObjectId(definition);
      } catch (IOException ex) {
        LOGGER.log(Level.SEVERE, null, ex);
      }
    }

    if (super.getArchiveService() == null) {
      if (objId == null) {
        objId = uniqueObjIdDef.incrementAndGet();
      }
      this.addDef(objId, definition);
      return objId;
    } else {
      if (objId != null) {
        try {
          // Attempt to resurrect previous app object from the archive
          updateAppInArchive(objId, definition, null);
          this.addDef(objId, definition);
          return objId;
        }
        catch(MALException | MALInteractionException ex) {
          // No previous object - fail silently and proceed to creating one
        }
      }
      if (objId == null) {
        // Ensure archive allocates the id
        objId = (long) 0;
      }
      try {
        LongList objIds = addAppToArchive(definition, source, uri, objId, related);

        if (objIds.size() == 1) {
          this.addDef(objIds.get(0), definition);
          return objIds.get(0);
        }
      } catch (MALException | MALInteractionException ex) {
        LOGGER.log(Level.SEVERE, null, ex);
      }
    }

    return null;
  }

  private Long readAppObjectId(final AppDetails definition) throws IOException {
    Long objId;
    File fileProps = new File(appsFolderPath.getCanonicalPath() + File.separator + definition.
        getName().getValue() + File.separator + definition.getExtraInfo());

    Properties props = HelperMisc.loadProperties(fileProps.getCanonicalPath());

    // Look up for apid
    String apidString = (String) props.get(HelperMisc.PROPERTY_APID);
    int apid = (apidString != null) ? Integer.parseInt(apidString) : 0;
    objId = apid != 0 ? (long) apid : null;
    return objId;
  }

  private LongList addAppToArchive(final AppDetails definition, final ObjectId source, final URI uri, Long objId,
      Long related) throws MALException, MALInteractionException {
    AppDetailsList defs = new AppDetailsList();
    defs.add(definition);
    final ArchiveDetailsList archDetails = HelperArchive.generateArchiveDetailsList(related,
        source, uri);
    archDetails.get(0).setInstId(objId);

    return super.getArchiveService().store(
        true,
        AppsLauncherHelper.APP_OBJECT_TYPE,
        ConfigurationProviderSingleton.getDomain(),
        archDetails,
        defs,
        null);
  }

  protected boolean update(final Long objId, final AppDetails definition,
      final MALInteraction interaction)
  { // requirement: 3.3.2.5
    boolean success = this.updateDef(objId, definition);

    if (super.getArchiveService() != null) {  // It should also update on the COM Archive
      try {
        updateAppInArchive(objId, definition, interaction);
      } catch (MALException | MALInteractionException ex) {
        LOGGER.log(Level.SEVERE, null, ex);
        return false;
      }
    }

    return success;
  }

  private void updateAppInArchive(final Long objId, final AppDetails definition, final MALInteraction interaction)
      throws MALException, MALInteractionException {
    AppDetailsList defs = new AppDetailsList();
    defs.add(definition);
    final IdentifierList domain = ConfigurationProviderSingleton.getDomain();

    ArchiveDetails archiveDetails = HelperArchive.getArchiveDetailsFromArchive(super.
        getArchiveService(),
        AppsLauncherHelper.APP_OBJECT_TYPE, domain, objId);
    if (archiveDetails == null) {
      throw new MALException("No object present in archive.");
    }

    ArchiveDetailsList archiveDetailsList = new ArchiveDetailsList();
    archiveDetailsList.add(archiveDetails);

    super.getArchiveService().update(
        AppsLauncherHelper.APP_OBJECT_TYPE,
        domain,
        archiveDetailsList,
        defs,
        interaction);
  }

  protected boolean delete(Long objId)
  {
    return this.deleteDef(objId);
  }

  protected boolean refreshAvailableAppsList(final URI providerURI)
  {
    // Go to all the "apps folder" and check if there are new folders
    // get all the files from a directory
    File[] fList = appsFolderPath.listFiles();

    if (fList == null) {
      LOGGER.log(Level.SEVERE, "The directory could not be found: {0} (full path: {1})", 
              new Object[]{appsFolderPath.toString(), appsFolderPath.getAbsolutePath()});

      return false;
    }

    boolean anyChanges = false;
    AppDetailsList apps = new AppDetailsList();

    for (File folder : fList) { // Roll all the apps inside the apps folder
      if (folder.isDirectory()) {
        File propsFile = new File(folder, HelperMisc.PROVIDER_PROPERTIES_FILE);
        if (propsFile.exists() && !propsFile.isDirectory()) {
          AppDetails app = this.readAppDescriptor(folder.getName(), propsFile);
          apps.add(app);
        }
      }
    }

    // Compare with the defs list!
    // Are there any differences?
    for (AppDetails singleApp : apps) {
      final Long id = super.list(singleApp.getName());
      AppDetails previousAppDetails = this.get(id);

      // It didn't exist...
      if (previousAppDetails == null) {
        LOGGER.log(Level.INFO,
            "New app found! Adding new app: {0}", singleApp.getName().getValue());

        // Either is the first time running or it is a newly installed app!
        ObjectId source = null;
        this.addApp(singleApp, source, providerURI);
        anyChanges = true;
        continue; // Check the next one...
      }

      // It did exist before. Are there any differences from the previous?
      if (!previousAppDetails.equals(singleApp)) {
        // Is it a difference just in the Running status?
        if (AppsLauncherManager.isJustRunningStatusChange(previousAppDetails, singleApp)) {
          continue;
        }

        // Then we have to update it...
        LOGGER.log(Level.INFO,
            "New update found on app: {0}\nPrevious: {1}\nNew: {2}",
            new Object[]{singleApp.getName().getValue(),
              previousAppDetails, singleApp});

        this.update(id, singleApp, null);
        anyChanges = true;
      }
    }

    // Also needs to check if we removed a folder!
    final LongList ids = this.listAll();
    final AppDetailsList localApps = this.getAll();
    for (int i = 0; i < ids.size(); i++) { // Roll all the apps inside the apps folder
      AppDetails localApp = localApps.get(i);
      boolean appStillIntact = false;
      for (File folder : fList) { // Roll all the apps inside the apps folder
        if (folder.isDirectory()) {
          if (folder.getName().equals(localApp.getName().getValue())) {
            for (File file : folder.listFiles()) { // Roll all the files inside each app folder
              // Check if the folder contains the provider properties
              if (HelperMisc.PROVIDER_PROPERTIES_FILE.equals(file.getName())) {
                // All Good!
                appStillIntact = true;
                break;
              }
            }
          }
        }
      }

      if (!appStillIntact) {
        LOGGER.log(Level.INFO,
            "The app has been removed: {0}", localApp.getName().getValue());

        this.delete(ids.get(i));
        anyChanges = true;
      }
    }

    return anyChanges;
  }

  protected boolean isAppRunning(final Long appId)
  {
    // get it from the list of available apps
    AppDetails app = (AppDetails) this.getDef(appId);
    ProcessExecutionHandler handler = handlers.get(appId);

    if (handler == null) {
      LOGGER.log(Level.FINE,
          "The Process handler could not be found!");

      app.setRunning(false);
      return false;
    }

    return this.get(appId).getRunning();
  }

  protected String[] assembleCommand(final String workDir, final String appName, final String runAs, final String prefix, final String[] env)
  {
    ArrayList<String> ret = new ArrayList<>();
    String trimmedAppName = appName.replaceAll("space-app-", "");
    if (osValidator.isWindows()) {
      ret.add("cmd");
      ret.add("/c");
      StringBuilder str = new StringBuilder();
      str.append(prefix);
      str.append(trimmedAppName);
      str.append(".bat");
      ret.add(str.toString());
    } else {
      if (runAs != null) {
        ret.add("sudo");
        ret.add("su");
        ret.add("-");
        ret.add(runAs);
        ret.add("-c");
      } else {
        ret.add("/bin/sh");
        ret.add("-c");
      }
      StringBuilder envString = new StringBuilder();
      for (String envVar : env) {
        envString.append(envVar);
        envString.append(" ");
      }

      ret.add("cd " + workDir + ";" + envString.toString() + "./" + prefix + trimmedAppName + ".sh");
    }
    return ret.toArray(new String[0]);
  }
  protected String[] assembleAppStopCommand(final String workDir, final String appName, final String runAs, final String[] env)
  {
    return assembleCommand(workDir, appName, runAs, "stop_", env);
  }

  protected String[] assembleAppStartCommand(final String workDir, final String appName, final String runAs, final String[] env)
  {
    return assembleCommand(workDir, appName, runAs, "start_", env);
  }

  protected void assembleAppLauncherEnvironment(final String directoryServiceURI,
      final Map<String, String> targetEnv)
  {
    try {
      // Inherit NMF HOME and NMF LIB from the supervisor
      Map<String, String> parentEnv = EnvironmentUtils.getProcEnvironment();
      if (parentEnv.containsKey("NMF_LIB")) {
        targetEnv.put("NMF_LIB", parentEnv.get("NMF_LIB"));
      }
      if (parentEnv.containsKey("NMF_HOME")) {
        targetEnv.put("NMF_HOME", parentEnv.get("NMF_HOME"));
      }
      if (osValidator.isWindows()) {
        if (parentEnv.containsKey("PATH")) {
          targetEnv.put("PATH", parentEnv.get("PATH"));
        }
        if (parentEnv.containsKey("TEMP")) {
          targetEnv.put("TEMP", parentEnv.get("TEMP"));
        }
        if (parentEnv.containsKey("OS")) {
          targetEnv.put("OS", parentEnv.get("OS"));
        }
      }
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE, "getProcEnvironment failed!", ex);
    }
    // Extend the current environment by JAVA_OPTS
    targetEnv.put("JAVA_OPTS",
        "-D" + Const.CENTRAL_DIRECTORY_URI_PROPERTY + "=" + directoryServiceURI + "");
  }

  protected void startAppProcess(final ProcessExecutionHandler handler,
      final MALInteraction interaction, final String directoryServiceURI) throws IOException
  {
    // get it from the list of available apps
    AppDetails app = (AppDetails) this.getDef(handler.getObjId());

    // Go to the folder where the app are installed
    final File appFolder
        = new File(appsFolderPath + File.separator + app.getName().getValue());
    Map<String, String> env = new HashMap<>();
    assembleAppLauncherEnvironment(directoryServiceURI, env);
    final String[] appLauncherCommand = assembleAppStartCommand(appFolder.getAbsolutePath(),
        app.getName().getValue(),
        app.getRunAs(),
        EnvironmentUtils.toStrings(env));

    final ProcessBuilder pb = new ProcessBuilder(appLauncherCommand);
    pb.environment().clear();
    if(osValidator.isWindows()) {
        pb.environment().putAll(env);
    }
    pb.directory(appFolder);
    LOGGER.log(Level.INFO,
        "Initializing ''{0}'' app in dir: {1}, using launcher command: {2}",
        new Object[]{app.getName().getValue(), appFolder.getAbsolutePath(), Arrays.toString(
          appLauncherCommand)});
    final Process proc = pb.start();
    handler.monitorProcess(proc);
    handlers.put(handler.getObjId(), handler);
    this.setRunning(handler.getObjId(), true, interaction); // Update the Archive
  }

  protected boolean killAppProcess(final Long appInstId, MALInteraction interaction)
  {
    AppDetails app = (AppDetails) this.getDef(appInstId); // get it from the list of available apps

    LOGGER.log(Level.INFO,
        "Killing app: {0}", app.getName().getValue());
    ProcessExecutionHandler handler = handlers.get(appInstId);

    if (handler == null) {
      LOGGER.log(Level.INFO,
          "Handler of {0} app is null, setting running = false.", app.getName().getValue());
      app.setRunning(false);
      return false;
    }

    if (handler.getProcess() == null) {
      LOGGER.log(Level.INFO,
          "Process of {0} app is null, setting running = false.", app.getName().getValue());
      app.setRunning(false);
      return true;
    }

    handler.close();
    this.setRunning(handler.getObjId(), false, interaction); // Update the Archive
    handlers.remove(appInstId); // Get rid of it!

    return true;
  }

  protected boolean stopNativeApp(final Long appInstId, StopAppInteraction interaction, boolean onlyNativeComponent) throws
      IOException, MALInteractionException, MALException
  {
    AppDetails app = (AppDetails) this.getDef(appInstId); // get it from the list of available apps

    // Go to the folder where the app is installed
    final File appFolder
        = new File(appsFolderPath + File.separator + app.getName().getValue());
    Map<String, String> env = new HashMap<>();
    assembleAppLauncherEnvironment("", env);
    final String[] appLauncherCommand = assembleAppStopCommand(appFolder.getAbsolutePath(),
        app.getName().getValue(), app.getRunAs(), EnvironmentUtils.toStrings(env));

    final ProcessBuilder pb = new ProcessBuilder(appLauncherCommand);
    pb.environment().clear();
    pb.directory(appFolder);
    LOGGER.log(Level.INFO,
        "Stopping ''{0}'' app in dir: {1}, using launcher command: {2}",
        new Object[]{app.getName().getValue(), appFolder.getAbsolutePath(), Arrays.toString(
          appLauncherCommand)});
    final Process proc = pb.start();
    interaction.sendUpdate(appInstId);
    boolean exitCleanly = false;
    try {
      exitCleanly = proc.waitFor(APP_STOP_TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (InterruptedException ex) {
      LOGGER.log(Level.WARNING, null, ex);
    }
    if (!exitCleanly) {
      LOGGER.log(Level.WARNING,
          "App {0} stop script did not exit within the timeout ({1} ms). Killing the stop script and forcing the app exit.",
          new Object[]{app.getName().getValue(), APP_STOP_TIMEOUT});
      proc.destroyForcibly();
    }
    if (onlyNativeComponent) {
      return true;
    }
    return killAppProcess(appInstId, interaction.getInteraction());
  }

  protected void stopNMFApp(final Long appInstId, final Identifier appDirectoryServiceName,
      final SingleConnectionDetails appConnection,
      final StopAppInteraction interaction) throws MALException, MALInteractionException
  {
    ClosingAppListener listener = null;
    // Register on the Event service of the respective apps
    // Select all object numbers from the Apps Launcher service Events
    Subscription eventSub = HelperCOM.generateSubscriptionCOMEvent(
        "ClosingAppEvents",
        AppsLauncherHelper.APP_OBJECT_TYPE);
    try { // Subscribe to events
      EventConsumerServiceImpl eventServiceConsumer = new EventConsumerServiceImpl(appConnection);
      Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.FINE,
          "Connected to: {0}", appConnection.toString());
      listener = new ClosingAppListener(interaction, eventServiceConsumer,
          appInstId);
      eventServiceConsumer.addEventReceivedListener(eventSub, listener);
    } catch (MalformedURLException ex) {
      Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.SEVERE,
          "Could not connect to the app!");
    }

    // Stop the app...
    ObjectType objType = AppsLauncherHelper.STOPAPP_OBJECT_TYPE;
    Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.INFO,
        "Sending event to app: {0} (Name: ''{1}'')", new Object[]{appInstId, appDirectoryServiceName});
    this.setRunning(appInstId, false, interaction.getInteraction());
    ObjectId eventSource
        = super.getCOMServices().getActivityTrackingService().storeCOMOperationActivity(
            interaction.getInteraction(), null);

    // Generate, store and publish the events to stop the App...
    final Long objId = super.getCOMServices().getEventService().generateAndStoreEvent(objType,
        ConfigurationProviderSingleton.getDomain(), appDirectoryServiceName, appInstId, eventSource, interaction.
        getInteraction());

    final URI uri = interaction.getInteraction().getMessageHeader().getURIFrom();

    if (appDirectoryServiceName != null) {
      try {
        IdentifierList eventBodies = new IdentifierList(1);
        eventBodies.add(appDirectoryServiceName);
        super.getCOMServices().getEventService().publishEvent(uri, objId, objType, appInstId,
            eventSource, eventBodies);
      } catch (IOException ex) {
        LOGGER.log(Level.SEVERE, null, ex);
      }
    }

    if (listener != null) {
      try {
        listener.waitForAppClosing(APP_STOP_TIMEOUT);
      } catch (InterruptedException ex) {
        LOGGER.log(Level.WARNING, null, ex);
      }
    }
  }

  /**
   * Stops multiple apps.
   * Blocks until all applications exit or waiting for them times out.
   *
   * @param appInstIds Applications IDs
   * @param appDirectoryServiceNames Directory service app name
   * @param appConnections Application connection handlers (for NMF apps only)
   * @param interaction Source interaction
   * @throws MALException
   * @throws MALInteractionException
   */
  protected void stopApps(final LongList appInstIds, final IdentifierList appDirectoryServiceNames,
      final ArrayList<SingleConnectionDetails> appConnections,
      final StopAppInteraction interaction) throws MALException, MALInteractionException
  {
    for (int i = 0; i < appInstIds.size(); i++) {
      long appInstId = appInstIds.get(i);
      AppDetails curr = this.get(appInstId);
      String fileExt = ".sh";
      if (osValidator.isWindows()) {
        fileExt = ".bat";
      }
      File stopScript = new File(appsFolderPath + File.separator + curr.getName().getValue()
          + File.separator + "stop_" + curr.getName().getValue() + fileExt);
      boolean stopExists = stopScript.exists();
      if (curr.getCategory().getValue().equalsIgnoreCase("NMF_App")) {
        if (appDirectoryServiceNames.get(i) == null) {
          LOGGER.log(Level.WARNING, "appDirectoryServiceName null for ''{0}'' app, falling back to kill",
            new Object[]{curr.getName().getValue()});
            this.killAppProcess(appInstId, interaction.getInteraction());
        } else if (appConnections.get(i) == null) {
          LOGGER.log(Level.WARNING, "appConnection null for ''{0}'' app, falling back to kill",
            new Object[]{curr.getName().getValue()});
            this.killAppProcess(appInstId, interaction.getInteraction());
        } else {
          this.stopNMFApp(appInstId, appDirectoryServiceNames.get(i), appConnections.get(i), interaction);
        }
        if (stopExists) {
          LOGGER.log(Level.INFO,
              "Stopping native component of ''{0}'' app",
              new Object[]{curr.getName().getValue()});
          try {
            this.stopNativeApp(appInstId, interaction, true);
          } catch (IOException ex) {
            Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.SEVERE,
                "Stopping native component failed", ex);
          }
        }
      } else {
        if (!stopExists) {
          LOGGER.log(Level.INFO, "No stop script present for app {0}. Killing the process.",
              curr.getName());
          this.killAppProcess(appInstId, interaction.getInteraction());
        } else {
          try {
            LOGGER.log(Level.INFO, "Stop script present for app {0}. Invoking it.", curr.getName());
            this.stopNativeApp(appInstId, interaction, false);
          } catch (IOException ex) {
            Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.SEVERE,
                "Stopping native app failed", ex);
          }
        }
      }
    }
  }

  public void setRunning(Long appInstId, boolean running, MALInteraction interaction)
  {
    this.get(appInstId).setRunning(running);
    this.update(appInstId, this.get(appInstId), interaction); // Update the Archive
  }

  public static SingleConnectionDetails getSingleConnectionDetailsFromProviderSummaryList(
      ProviderSummaryList providersList) throws IOException
  {
    if (providersList.isEmpty()) { // Throw error!
      LOGGER.log(Level.WARNING,
          "The service could not be found in the Directory service... Possible reasons:\n"
          + "1. Wrong area number.\n"
          + "2. User is trying to control a non-NMF app! If so, one needs to use killApp operation!\n");
      throw new IOException();
    }

    if (providersList.size() != 1) { // Throw error!
      throw new IOException("There are more than 1 provider registered for this app! "
          + "Most likely the app was forcefully killed before.");
    }

    // Get the service address details lists
    ServiceCapabilityList capabilities = providersList.get(0).getProviderDetails().
        getServiceCapabilities();

    // How many addresses do we have?
    if (capabilities.isEmpty()) { // Throw an error
      throw new IOException("We don't have any services...");
    }

    if (capabilities.size() != 1) {
      throw new IOException("We have more than 1 service...");
    }

    final AddressDetailsList addresses = capabilities.get(0).getServiceAddresses();

    int bestIndex = HelperCommon.getBestIPCServiceAddressIndex(addresses);
    SingleConnectionDetails connectionDetails = new SingleConnectionDetails();
    connectionDetails.setProviderURI(addresses.get(bestIndex).getServiceURI());
    connectionDetails.setBrokerURI(addresses.get(bestIndex).getBrokerURI());
    connectionDetails.setDomain(providersList.get(0).getProviderKey().getDomain());
    return connectionDetails;
  }

  private static boolean isJustRunningStatusChange(final AppDetails previousAppDetails,
      final AppDetails single_app)
  {
    if (!previousAppDetails.getCategory().equals(single_app.getCategory())
        || !previousAppDetails.getDescription().equals(single_app.getDescription())
        || !previousAppDetails.getName().equals(single_app.getName())
        || previousAppDetails.getRunAtStartup().booleanValue() != single_app.getRunAtStartup().
        booleanValue()
        || !previousAppDetails.getVersion().equals(single_app.getVersion())) {
      return false;
    }

    // extraInfo field can be null according to the API
    if (previousAppDetails.getExtraInfo() != null && single_app.getExtraInfo() != null) {
      if (!previousAppDetails.getExtraInfo().equals(single_app.getExtraInfo())) {
        return false;
      }
    }

    return previousAppDetails.getRunning().booleanValue() != single_app.getRunning().booleanValue();
  }

  private AppDetails readAppDescriptor(final String appName, final File propertiesFile)
  {
    final AppDetails app = new AppDetails();
    app.setName(new Identifier(appName)); // Use the name of the folder

    try (FileInputStream inputStream = new FileInputStream(propertiesFile)){
      final Properties props = new Properties();
      props.load(inputStream);
      app.setExtraInfo(HelperMisc.PROVIDER_PROPERTIES_FILE);

      final String category = (props.getProperty(HelperMisc.APP_CATEGORY) != null) ? props.
          getProperty(HelperMisc.APP_CATEGORY) : "-";
      final String version = (props.getProperty(HelperMisc.APP_VERSION) != null) ? props.
          getProperty(HelperMisc.APP_VERSION) : "-";
      final String copyright = (props.getProperty(HelperMisc.APP_COPYRIGHT) != null) ? props.
          getProperty(HelperMisc.APP_COPYRIGHT) : "-";
      final String description = (props.getProperty(HelperMisc.APP_DESCRIPTION) != null) ? props.
          getProperty(HelperMisc.APP_DESCRIPTION) : "-";
      final String user = (props.getProperty(HelperMisc.APP_USER) != null) ? props.getProperty(
          HelperMisc.APP_USER)
          : null; // Since the user change is only implemented on linux this dependency is fine

      app.setCategory(new Identifier(category));
      app.setVersion(version);
      app.setCopyright(copyright);
      app.setDescription(description);
      app.setRunAs(user);

      app.setRunAtStartup(false); // This is not supported in this implementation
      app.setRunning(false); // Default values
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
    }

    return app;
  }

}
