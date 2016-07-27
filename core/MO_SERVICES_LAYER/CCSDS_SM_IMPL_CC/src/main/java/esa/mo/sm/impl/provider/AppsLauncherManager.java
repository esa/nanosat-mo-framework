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

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.sm.impl.provider.AppsLauncherProviderServiceImpl.ProcessExecutionHandler;
import esa.mo.sm.impl.util.OSValidator;
import esa.mo.sm.impl.util.ShellCommander;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.structures.AppDetails;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.structures.AppDetailsList;

/**
 *
 * @author Cesar Coelho
 */
public class AppsLauncherManager extends DefinitionsManager {

    private final ShellCommander shell = new ShellCommander();
    private final OSValidator osValidator = new OSValidator();

    private static final String RUN_LIN_FILENAME = "runAppLin.sh";
    private static final String RUN_WIN_FILENAME = "runAppWin.bat";
    private static final String FOLDER_LOCATION_PROPERTY = "esa.mo.nanosatmoframework.provider.FolderLocation";
    private static final String APPS_DIRECTORY_NAME = "apps";  // dir name
    private File apps_folder_path = new File(".." + File.separator + ".." + File.separator + APPS_DIRECTORY_NAME);  // Location of the folder
    private final String runnable_filename;
    private final HashMap<Long, ProcessExecutionHandler> handlers = new HashMap<Long, ProcessExecutionHandler>();

    private Long uniqueObjIdDef; // Counter (different for every Definition)
    private Long uniqueObjIdPVal;

    public AppsLauncherManager(COMServicesProvider comServices) {
        super(comServices);

        if (System.getProperty(FOLDER_LOCATION_PROPERTY) != null) { // If there is a property for that, then use it!! 
            apps_folder_path = new File(System.getProperty(FOLDER_LOCATION_PROPERTY));
        }

        runnable_filename = osValidator.isWindows() ? RUN_WIN_FILENAME : RUN_LIN_FILENAME;

        try {
            AppsLauncherHelper.init(MALContextFactory.getElementFactoryRegistry());
        } catch (MALException ex) { // nothing to be done..
        }

        if (super.getArchiveService() == null) {  // No Archive?
            this.uniqueObjIdDef = new Long(0); // The zeroth value will not be used (reserved for the wildcard)
            this.uniqueObjIdPVal = new Long(0); // The zeroth value will not be used (reserved for the wildcard)
//            this.load(); // Load the file
        } else {
            // With Archive...
        }

    }

    protected AppDetailsList getAll() {
        return (AppDetailsList) this.getAllDefs();
    }

    @Override
    protected Boolean compareName(Long objId, Identifier name) {
        return this.get(objId).getName().equals(name);
    }

    @Override
    protected ElementList newDefinitionList() {
        return new AppDetailsList();
    }

    protected AppDetails get(Long input) {
        return (AppDetails) this.getDefs().get(input);
    }

    protected Long add(AppDetails definition, ObjectId source, SingleConnectionDetails connectionDetails) { // requirement: 3.3.2.5
        if (super.getArchiveService() == null) {
            uniqueObjIdDef++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
            this.addDef(uniqueObjIdDef, definition);
//            this.save();
            return uniqueObjIdDef;
        } else {
            AppDetailsList defs = new AppDetailsList();
            defs.add(definition);

            try {
                LongList objIds = super.getArchiveService().store(
                        true,
                        AppsLauncherHelper.APP_OBJECT_TYPE,
                        connectionDetails.getDomain(),
                        HelperArchive.generateArchiveDetailsList(null, source, connectionDetails),
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

    protected boolean update(Long objId, AppDetails definition, SingleConnectionDetails connectionDetails, MALInteraction interaction) { // requirement: 3.3.2.5
        Boolean success = this.updateDef(objId, definition);

        if (super.getArchiveService() != null) {  // It should also update on the COM Archive
            try {
                AppDetailsList defs = new AppDetailsList();
                defs.add(definition);

                ArchiveDetails archiveDetails = HelperArchive.getArchiveDetailsFromArchive(super.getArchiveService(),
                        AppsLauncherHelper.APP_OBJECT_TYPE, connectionDetails.getDomain(), objId);

                ArchiveDetailsList archiveDetailsList = new ArchiveDetailsList();
                archiveDetailsList.add(archiveDetails);

                super.getArchiveService().update(
                        AppsLauncherHelper.APP_OBJECT_TYPE,
                        connectionDetails.getDomain(),
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

//        this.save();
        return success;
    }

    protected boolean delete(Long objId) { // requirement: 3.3.2.5
        if (!this.deleteDef(objId)) {
            return false;
        }
//        this.save();
        return true;
    }

    protected void refreshAvailableAppsList(SingleConnectionDetails connectionDetails) {

        // Go to all the "apps folder" and check if there are new folders
        // get all the files from a directory
        File[] fList = apps_folder_path.listFiles();

        if (fList == null) {
            Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.SEVERE, "The directory could not be found: {0}", apps_folder_path.toString());

            // Create?
            // ????
            return;
        }

        AppDetailsList apps = new AppDetailsList();

        for (File app_folder : fList) { // Roll all the apps inside the apps folder

            if (app_folder.isDirectory()) {
                // Add app by app

                // Check if the folder contains the executable
                for (File file : app_folder.listFiles()) { // Roll all the files inside each app folder
                    if (runnable_filename.equals(file.getName())) {
                        // Found!
                        AppDetails app = new AppDetails();
                        app.setName(new Identifier(app_folder.getName()));
                        app.setDescription("A simple description");
                        app.setVersion("1.0");
                        app.setCategory(new Identifier("MyCategory"));
                        app.setRunAtStartup(false);
                        app.setRunning(false);
                        apps.add(app);
                    }
                }
            }
        }

        // Compare with the defs list!
        // Are there any differences?
        for (AppDetails single_app : apps) {
            Long id = super.list(single_app.getName());

            AppDetails previousAppDetails = (AppDetails) super.getDefs().get(id);

            if (previousAppDetails == null) { // It didn't exist...
                // Either is the first time running or it is a newly installed app!
                ObjectId source = null;

                this.add(single_app, source, connectionDetails);

                continue; // Check the next one...
            }

            // It does exist. Are there any differences?
            if (!previousAppDetails.equals(single_app)){
                // Then we have to update it...
                
                // Is it a difference just on the Running status?
                if(previousAppDetails.getRunning().equals(single_app.getRunning())){
                    continue;
                }
                
                this.update(id, single_app, connectionDetails, null);
                
            }
        }

    }

    protected boolean isAppRunning(final Long appId) {
        AppDetails app = (AppDetails) this.getDefs().get(appId); // get it from the list of available apps
        ProcessExecutionHandler handler = handlers.get(appId);

        if (handler == null) {
            app.setRunning(false);
            return false;
        }

        this.get(appId).setRunning(handler.getProcess().isAlive());

        return this.get(appId).getRunning();
    }

    protected void startAppProcess(ProcessExecutionHandler handler, MALInteraction interaction) throws IOException {

        AppDetails app = (AppDetails) this.getDefs().get(handler.getAppInstId()); // get it from the list of available apps

        // Go to the folder where the app are installed
        String app_folder = apps_folder_path + File.separator + app.getName().getValue();
        final String full_path = app_folder + File.separator + runnable_filename;
        Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.INFO, "Reading and initializing '" + app.getName().getValue() + "' app on path: " + full_path);

        BufferedReader brTest = new BufferedReader(new FileReader(new File(full_path)));
        String text = brTest.readLine();
        String splitted[] = text.split(" ");

        Process proc = Runtime.getRuntime().exec(splitted, null, new File(app_folder));
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
        
        if (proc.isAlive()) {
            handlers.put(handler.getAppInstId(), handler);
            app.setRunning(true);
            this.update(handler.getAppInstId(), app, handler.getSingleConnectionDetails(), interaction); // Update the Archive
        }

    }

    protected boolean killAppProcess(final Long appInstId, SingleConnectionDetails connectionDetails, MALInteraction interaction) {
        AppDetails app = (AppDetails) this.getDefs().get(appInstId); // get it from the list of available apps

        ProcessExecutionHandler handler = handlers.get(appInstId);

        if (handler == null) {
            app.setRunning(false);
            return false;
        }

        if (handler.getProcess() == null) {
            app.setRunning(false);
            return true;
        }

        if (handler.getProcess().isAlive()) {
            handler.close();
            app.setRunning(false);
            this.update(appInstId, app, connectionDetails, interaction); // Update the Archive
        }

        return true;

    }

}
