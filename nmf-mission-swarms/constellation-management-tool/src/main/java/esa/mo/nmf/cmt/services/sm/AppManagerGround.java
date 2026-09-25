/* ----------------------------------------------------------------------------
 * Copyright (C) 2022      European Space Agency
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
 *
 * Author: N Wiegand (https://github.com/Klabau)
 */
package esa.mo.nmf.cmt.services.sm;

import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import esa.mo.sm.impl.consumer.AppsLauncherConsumerServiceImpl;
import javax.swing.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.sm.appslauncher.AppsLauncherHelper;
import org.ccsds.moims.mo.sm.appslauncher.AppsLauncherServiceInfo;
import org.ccsds.moims.mo.sm.appslauncher.body.ListAppResponse;
import org.ccsds.moims.mo.sm.appslauncher.consumer.AppsLauncherAdapter;

public class AppManagerGround {

    private static final Logger LOGGER = Logger.getLogger(AppManagerGround.class.getName());
    private final AppsLauncherConsumerServiceImpl serviceSMAppsLauncher;
    private final GroundMOAdapterImpl groundMOAdapter;

    public AppManagerGround(GroundMOAdapterImpl groundMOAdapter) {
        this.groundMOAdapter = groundMOAdapter;
        this.serviceSMAppsLauncher = groundMOAdapter.getSMServices().getAppsLauncherService();
    }

    /**
     * Launches an App on a NanoSat Segment
     *
     * @return The list of ArchivePersistenceObjects.
     */
    public List<ArchivePersistenceObject> getApps() {
        IdentifierList idList = new IdentifierList();
        idList.add(new Identifier("*"));

        try {
            ListAppResponse appResponse = serviceSMAppsLauncher.getAppsLauncherStub().listApp(idList, new Identifier("*"));

            ArchiveConsumerServiceImpl archiveService = groundMOAdapter.getCOMServices().getArchiveService();
            IdentifierList domain = serviceSMAppsLauncher.getConnectionDetails().getDomain();

            return HelperArchive.getArchiveCOMObjectList(archiveService.getArchiveStub(),
                    AppsLauncherServiceInfo.APPDETAILS_OBJECT_TYPE, domain, appResponse.getAppIds());
        } catch (MALInteractionException | MALException ex) {
            LOGGER.log(Level.SEVERE, "Failed to list Apps from NanoSat: ", ex);
        }
        return null;
    }

    /**
     * Runs an App.
     *
     * @param appId ID of the App to run
     */
    public void runAppById(Long appId) {
        LongList ids = new LongList();
        ids.add(appId);

        try {
            serviceSMAppsLauncher.getAppsLauncherStub().runApp(ids);
        } catch (MALInteractionException | MALException ex) {
            LOGGER.log(Level.SEVERE, "Failed to run App: ", ex);
        }
    }

    public void stopAppById(Long appId) {
        LongList ids = new LongList();
        ids.add(appId);

        // unsubscribeFromPreviousEvents(objId);
        try {
            // subscribeToEvents(objId);
            serviceSMAppsLauncher.getAppsLauncherStub().stopApp(ids, null, new AppsLauncherAdapter() {

                @Override
                public void stopAppAckReceived(MALMessageHeader msgHeader, java.util.Map qosProperties) {
                    LOGGER.log(Level.INFO, "Stopping...");
                }

                @Override
                public void stopAppUpdateReceived(MALMessageHeader msgHeader,
                        Long appClosing, java.util.Map qosProperties) {
                    LOGGER.log(Level.INFO, "Stopped!");
                }

                @Override
                public void stopAppAckErrorReceived(MALMessageHeader msgHeader,
                        MOErrorException error, java.util.Map qosProperties) {
                    String msg = "There was an error during the stop operation.";
                    JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.PLAIN_MESSAGE);
                    LOGGER.log(Level.SEVERE, msg, error);
                }

                @Override
                public void stopAppResponseReceived(MALMessageHeader msgHeader, java.util.Map qosProperties) {
                    LOGGER.log(Level.INFO, "Stop App Completed.");
                }

            });
        } catch (MALInteractionException | MALException ex) {
            LOGGER.log(Level.SEVERE, "Failed to run App: ", ex);
        }
    }
}
