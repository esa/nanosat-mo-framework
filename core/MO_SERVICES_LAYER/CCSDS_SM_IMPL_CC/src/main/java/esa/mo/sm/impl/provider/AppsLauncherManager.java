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
import esa.mo.sm.impl.util.OSValidator;
import esa.mo.sm.impl.util.ShellCommander;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
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

    private Long uniqueObjIdDef; // Counter (different for every Definition)
    private Long uniqueObjIdPVal;

    public AppsLauncherManager(COMServicesProvider comServices) {
        super(comServices);

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

    protected boolean update(Long objId, AppDetails definition, SingleConnectionDetails connectionDetails) { // requirement: 3.3.2.5
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
                        null);

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

    protected void refreshAvailableAppsList() {

        // Go to all the "apps folder" and check if there are new folders
    }

    protected boolean isAppRunning(Long appId) {
        return true;
    }

    void runApp(final Long appInstId) {
        AppDetails app = (AppDetails) this.getDefs().get(appInstId); // get it from the list of available apps

        // Go to the folder where the app are installed
        final String app_folder = app.getName().getValue();
        final File current_location = new File("..");  // Location of the folder
        String cmd = current_location.getAbsolutePath() + File.separator + app_folder;

        // Run: runAppLin.sh or runAppWin.bat
        if (osValidator.getOS().equals("win")) {
            cmd += "runAppWin.bat";
        } else {
            cmd += "runAppLin.sh";
        }

        shell.runCommand(cmd);
    }

}
