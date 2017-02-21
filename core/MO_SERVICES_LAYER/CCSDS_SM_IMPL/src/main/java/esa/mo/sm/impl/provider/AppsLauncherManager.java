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
package esa.mo.sm.impl.provider;

import esa.mo.sm.impl.util.ClosingAppListener;
import esa.mo.com.impl.consumer.EventConsumerServiceImpl;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.DefinitionsManager;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.sm.impl.provider.AppsLauncherProviderServiceImpl.ProcessExecutionHandler;
import esa.mo.sm.impl.util.OSValidator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.common.directory.structures.AddressDetails;
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
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.provider.StopAppInteraction;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.structures.AppDetails;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.structures.AppDetailsList;

/**
 *
 * @author Cesar Coelho
 */
public class AppsLauncherManager extends DefinitionsManager {

    private final OSValidator osValidator = new OSValidator();

    private static final String RUN_LIN_FILENAME = "runAppLin.sh";
    private static final String RUN_WIN_FILENAME = "runAppWin.bat";
    private static final String FOLDER_LOCATION_PROPERTY = "esa.mo.sm.impl.provider.appslauncher.FolderLocation";
    private static final String APPS_DIRECTORY_NAME = "apps";  // dir name
    private File apps_folder_path = new File(".." + File.separator + ".." + File.separator + APPS_DIRECTORY_NAME);  // Location of the folder
    private final String runnable_filename;
    private final HashMap<Long, ProcessExecutionHandler> handlers = new HashMap<Long, ProcessExecutionHandler>();

    private AtomicLong uniqueObjIdDef; // Counter

    public AppsLauncherManager(COMServicesProvider comServices) {
        super(comServices);

        // If there is a property for that, then use it!! 
        if (System.getProperty(FOLDER_LOCATION_PROPERTY) != null) {
            apps_folder_path = new File(System.getProperty(FOLDER_LOCATION_PROPERTY));
        }

        runnable_filename = osValidator.isWindows() ? RUN_WIN_FILENAME : RUN_LIN_FILENAME;

        try {
            AppsLauncherHelper.init(MALContextFactory.getElementFactoryRegistry());
        } catch (MALException ex) { // nothing to be done..
        }

        if (super.getArchiveService() == null) {  // No Archive?
            this.uniqueObjIdDef = new AtomicLong(0);
        } else {
            // With Archive...
        }

    }

    protected AppDetailsList getAll() {
        return (AppDetailsList) this.getAllDefs();
    }

    @Override
    public Boolean compareName(Long objId, Identifier name) {
        return this.get(objId).getName().equals(name);
    }

    @Override
    public ElementList newDefinitionList() {
        return new AppDetailsList();
    }

    public AppDetails get(Long input) {
        return (AppDetails) this.getDef(input);
    }

    protected Long add(final AppDetails definition, final ObjectId source, final URI uri) {
        Long objId = null;
        Long related = null;
        Properties props = null;

        if (definition.getExtraInfo() != null) {
            // Read the provider.properties of the app
            String url = definition.getExtraInfo();
            props = HelperMisc.loadProperties(url, null);

            // Look up for apid
            String apidString = (String) props.get(HelperMisc.PROPERTY_APID);
            int apid = (apidString != null) ? Integer.parseInt(apidString) : 0;
            objId = new Long(apid);

            // Look up for the packageId
            related = new Long(1234);

        }

        if (super.getArchiveService() == null) {
            if (objId == null) {
                objId = uniqueObjIdDef.incrementAndGet();
            }
            this.addDef(objId, definition);
            return objId;
        } else {
            AppDetailsList defs = new AppDetailsList();
            defs.add(definition);

            try {
                final ArchiveDetailsList archDetails = HelperArchive.generateArchiveDetailsList(related, source, uri);
                archDetails.get(0).setInstId(objId);

                LongList objIds = super.getArchiveService().store(
                        true,
                        AppsLauncherHelper.APP_OBJECT_TYPE,
                        ConfigurationProviderSingleton.getDomain(),
                        archDetails,
                        defs,
                        null);

                if (objIds.size() == 1) {
                    this.addDef(objIds.get(0), definition);
                    return objIds.get(0);
                }
            } catch (MALException ex) {
                Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }

    protected boolean update(final Long objId, final AppDetails definition,
            final MALInteraction interaction) { // requirement: 3.3.2.5
        Boolean success = this.updateDef(objId, definition);

        if (super.getArchiveService() != null) {  // It should also update on the COM Archive
            try {
                AppDetailsList defs = new AppDetailsList();
                defs.add(definition);
                final IdentifierList domain = ConfigurationProviderSingleton.getDomain();

                ArchiveDetails archiveDetails = HelperArchive.getArchiveDetailsFromArchive(super.getArchiveService(),
                        AppsLauncherHelper.APP_OBJECT_TYPE, domain, objId);

                ArchiveDetailsList archiveDetailsList = new ArchiveDetailsList();
                archiveDetailsList.add(archiveDetails);

                super.getArchiveService().update(
                        AppsLauncherHelper.APP_OBJECT_TYPE,
                        domain,
                        archiveDetailsList,
                        defs,
                        interaction);
            } catch (MALException ex) {
                Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } catch (MALInteractionException ex) {
                Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        return success;
    }

    protected boolean delete(Long objId) {
        return this.deleteDef(objId);
    }

    protected void refreshAvailableAppsList(final URI providerURI) {
        // Go to all the "apps folder" and check if there are new folders
        // get all the files from a directory
        File[] fList = apps_folder_path.listFiles();

        if (fList == null) {
            Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.SEVERE,
                    "The directory could not be found: {0}", apps_folder_path.toString());

            // Create?
            // ????
            return;
        }

        boolean anyChanges = false;
        AppDetailsList apps = new AppDetailsList();

        for (File folder : fList) { // Roll all the apps inside the apps folder
            if (folder.isDirectory()) {
                for (File file : folder.listFiles()) { // Roll all the files inside each app folder
                    // Check if the folder contains the app executable
                    if (runnable_filename.equals(file.getName())) {
                        AppDetails app = this.readAppDescriptorFromFolder(folder);
                        apps.add(app);
                    }
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
                Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.INFO,
                        "New app found! Adding new app: " + singleApp.getName().getValue());

                // Either is the first time running or it is a newly installed app!
                ObjectId source = null;
                this.add(singleApp, source, providerURI);
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
                Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.INFO,
                        "New update found on app: " + singleApp.getName().getValue()
                        + "\nPrevious: " + previousAppDetails.toString()
                        + "\nNew: " + singleApp.toString());

                this.update(id, singleApp, null);
                anyChanges = true;
            }
        }

        if (anyChanges) {
            // Do something!!

        }
    }

    protected boolean isAppRunning(final Long appId) {
        // get it from the list of available apps
        AppDetails app = (AppDetails) this.getDef(appId);
        ProcessExecutionHandler handler = handlers.get(appId);

        if (handler == null) {
            Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.INFO,
                    "The Process handler could not be found!");

            app.setRunning(false);
            return false;
        }

        return this.get(appId).getRunning();
    }

    protected void startAppProcess(final ProcessExecutionHandler handler,
            final MALInteraction interaction) throws IOException {
        // get it from the list of available apps
        AppDetails app = (AppDetails) this.getDef(handler.getAppInstId());

        // Go to the folder where the app are installed
        String app_folder = apps_folder_path + File.separator + app.getName().getValue();
        final String full_path = app_folder + File.separator + runnable_filename;
        Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.INFO,
                "Reading and initializing '" + app.getName().getValue() + "' app on path: " + full_path);

        BufferedReader brTest = new BufferedReader(new FileReader(new File(full_path)));
        String text = brTest.readLine();
        String split[] = text.split(" ");

        Process proc = Runtime.getRuntime().exec(split, null, new File(app_folder));
        handler.startPublishing(proc);

        /*
        ArrayList<String> args = new ArrayList<String>();
        args.add("java");
        args.add("-classpath");
        args.add("/home/root/software/apps/GPS_Data/Demo_GPS_data-1.0-SNAPSHOT.jar:/home/root/software/libs/NanoSat_MO_Framework/LIB_NANOSAT_MO_FRAMEWORK_OPS_SAT-jar-with-dependencies.jar");
        args.add("esa.mo.nanosatmoframework.apps.DemoGPSData");

        ProcessBuilder dfdfdf = new ProcessBuilder(args);
        dfdfdf.directory(outDir);
        Process proc = dfdfdf.start();
         */
//        if (proc.isAlive()) {
        if (proc != null) {
            handlers.put(handler.getAppInstId(), handler);
            this.setRunning(handler.getAppInstId(), true, interaction); // Update the Archive
        } else {
            Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.WARNING,
                    "The process is null! Something is wrong...");
        }
    }

    protected boolean killAppProcess(final Long appInstId, MALInteraction interaction) {
        AppDetails app = (AppDetails) this.getDef(appInstId); // get it from the list of available apps

        ProcessExecutionHandler handler = handlers.get(appInstId);

        if (handler == null) {
            app.setRunning(false);
            return false;
        }

        if (handler.getProcess() == null) {
            app.setRunning(false);
            return true;
        }

//        if (handler.getProcess().isAlive()) {
        handler.close();
        this.setRunning(handler.getAppInstId(), false, interaction); // Update the Archive
        handlers.remove(appInstId); // Get rid of it!

//        app.setRunning(false);
//        this.update(appInstId, app, connectionDetails, interaction); // Update the Archive
//        }
        return true;
    }

    protected void stopApps(final LongList appInstIds, final ArrayList<SingleConnectionDetails> appConnections,
            final ConnectionProvider connection, final StopAppInteraction interaction) throws MALException, MALInteractionException {
        Random random = new Random(); // to avoid registrations with the same name

        // Register on the Event service of the respective apps
        for (int i = 0; i < appConnections.size(); i++) {
            // Select all object numbers from the Apps Launcher service Events
            final Long secondEntityKey = 0xFFFFFFFFFF000000L & HelperCOM.generateSubKey(AppsLauncherHelper.APP_OBJECT_TYPE);
            Subscription eventSub = ConnectionConsumer.subscriptionKeys(
                    new Identifier("AppClosingEvent" + random.nextInt()),
                    new Identifier("*"), secondEntityKey, new Long(0), new Long(0));

            try { // Subscribe to events
                EventConsumerServiceImpl eventServiceConsumer = new EventConsumerServiceImpl(appConnections.get(i));
                eventServiceConsumer.addEventReceivedListener(eventSub,
                        new ClosingAppListener(interaction, eventServiceConsumer, appInstIds.get(i)));
            } catch (MalformedURLException ex) {
                Logger.getLogger(AppsLauncherProviderServiceImpl.class.getName()).log(Level.SEVERE,
                        "Could not connect to the app!");
            }
        }

        // Stop the apps...
        ObjectType objType = AppsLauncherHelper.STOPAPP_OBJECT_TYPE;
        ObjectIdList sourceList = new ObjectIdList();

        for (Long appInstId : appInstIds) {
            Logger.getLogger(AppsLauncherProviderServiceImpl.class.getName()).log(Level.INFO,
                    "Sending event to app: " + appInstId);
            this.setRunning(appInstId, false, interaction.getInteraction());
            sourceList.add(super.getCOMServices().getActivityTrackingService().storeCOMOperationActivity(interaction.getInteraction(), null));
        }

        // Generate, store and publish the events to stop the Apps...
        LongList objIds = super.getCOMServices().getEventService().generateAndStoreEvents(objType,
                ConfigurationProviderSingleton.getDomain(), appInstIds, sourceList, interaction.getInteraction());

        final URI uri = interaction.getInteraction().getMessageHeader().getURIFrom();
        super.getCOMServices().getEventService().publishEvents(uri, objIds, objType, appInstIds, sourceList, null);
    }

    public void setRunning(Long appInstId, boolean running, MALInteraction interaction) {
        this.get(appInstId).setRunning(running);
        this.update(appInstId, this.get(appInstId), interaction); // Update the Archive
    }

    public static SingleConnectionDetails getSingleConnectionDetailsFromProviderSummaryList(ProviderSummaryList providersList) throws IOException {
        if (providersList.isEmpty()) { // Throw error!
            Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.WARNING,
                    "The app could not be found in the Directory service... Possible reasons: "
                    + "1. The property 'MOappName' of the app might not match its folder name "
                    + "2. Not a NMF app! If so, one needs to use killApp!");
            throw new IllegalArgumentException();
        }

        if (providersList.size() != 1) { // Throw error!
            throw new IllegalArgumentException("There are more than 1 provider rergistered for this app! "
                    + "Most likely the app was forcefully killed before.");
        }

        // Get the service address details lists
        ServiceCapabilityList capabilities = providersList.get(0).getProviderDetails().getServiceCapabilities();

        // How many addresses do we have?
        if (capabilities.isEmpty()) { // Throw an error
            Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.WARNING,
                    "We don't have any services...");
            throw new IOException();
        }

        if (capabilities.size() != 1) {
            Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.WARNING,
                    "We have more than 1 service...");
            throw new IOException();
        }

        final AddressDetailsList addresses = capabilities.get(0).getServiceAddresses();

        int bestIndex = AppsLauncherManager.getBestIPCServiceAddressIndex(addresses);
        SingleConnectionDetails connectionDetails = new SingleConnectionDetails();
        connectionDetails.setProviderURI(addresses.get(bestIndex).getServiceURI());
        connectionDetails.setBrokerURI(addresses.get(bestIndex).getBrokerURI());
        connectionDetails.setDomain(providersList.get(0).getProviderKey().getDomain());
        return connectionDetails;
    }

    public static int getBestIPCServiceAddressIndex(AddressDetailsList addresses) throws IllegalArgumentException {
        if (addresses.isEmpty()) {
            throw new IllegalArgumentException("The addresses argument cannot be null.");
        }

        if (addresses.size() == 1) { // Well, there is only one...
            return 0;
        }

        // Well, if there are more than one, then it means we can pick...
        // My preference would be, in order: tcp/ip, rmi, other, spp
        // SPP is in last because usually this is the transport supposed
        // to be used on the ground-to-space link and not internally.
        StringList availableTransports = AppsLauncherManager.getAvailableTransports(addresses);

        int index = AppsLauncherManager.getTransportIndex(availableTransports, "tcpip");
        if (index != -1) {
            return index;
        }

        index = AppsLauncherManager.getTransportIndex(availableTransports, "rmi");
        if (index != -1) {
            return index;
        }

        index = AppsLauncherManager.getTransportIndex(availableTransports, "malspp");

        // If could not be found nor it is not the first one
        if (index == -1 || index != 0) { // Then let's pick the first one
            return 0;
        } else {
            // It was found and it is the first one (0)
            // Then let's select the second (index == 1) transport available...
            return 1;
        }
    }

    private static StringList getAvailableTransports(AddressDetailsList addresses) {
        StringList transports = new StringList(); // List of transport names

        for (AddressDetails address : addresses) {
            // The name of the transport is always before ":"
            String[] parts = address.getServiceURI().toString().split(":");
            transports.add(parts[0]);
        }

        return transports;
    }

    private static int getTransportIndex(StringList transports, String findString) {
        for (int i = 0; i < transports.size(); i++) {
            if (findString.equals(transports.get(i))) {
                return i;  // match
            }
        }
        return -1;
    }

    private static boolean isJustRunningStatusChange(final AppDetails previousAppDetails, final AppDetails single_app) {
        if (!previousAppDetails.getCategory().equals(single_app.getCategory())
                || !previousAppDetails.getDescription().equals(single_app.getDescription())
                || !previousAppDetails.getName().equals(single_app.getName())
                || previousAppDetails.getRunAtStartup().booleanValue() != single_app.getRunAtStartup().booleanValue()
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

    private AppDetails readAppDescriptorFromFolder(final File appFolder) {
        // Hard-coded values for now
        final AppDetails app = new AppDetails();
        app.setName(new Identifier(appFolder.getName()));

        app.setDescription("A simple description");
        app.setVersion("1.0");
        app.setExtraInfo(appFolder.getAbsolutePath() + File.separator + "provider.properties");
        app.setCopyright("European Space Agency");

        // go up one folder
        File cat = new File(appFolder.getAbsolutePath() + ".." + File.separator);
        app.setCategory(new Identifier(cat.getName()));
        app.setRunAtStartup(false);
        app.setRunning(false); // Default values
        return app;
    }

}
