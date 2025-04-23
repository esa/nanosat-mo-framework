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
import esa.mo.nmf.cmt.ConstellationManagementTool;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import esa.mo.sm.impl.consumer.AppsLauncherConsumerServiceImpl;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.body.ListAppResponse;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.consumer.AppsLauncherAdapter;

import javax.swing.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppManagerGround {

    /**
     * Launches an App on a NanoSat Segment
     *
     * @param groundMOAdapter Ground MO Adapter implementation
     * @return The list of ArchivePersistenceObjects.
     */
    public static List<ArchivePersistenceObject> getApps(GroundMOAdapterImpl groundMOAdapter) {
        final AppsLauncherConsumerServiceImpl serviceSMAppsLauncher = groundMOAdapter.getSMServices().getAppsLauncherService();

        IdentifierList idList = new IdentifierList();
        idList.add(new Identifier("*"));

        try {
            ListAppResponse appResponse = serviceSMAppsLauncher.getAppsLauncherStub().listApp(idList, new Identifier("*"));

            ArchiveConsumerServiceImpl archiveService = groundMOAdapter.getCOMServices().getArchiveService();
            IdentifierList domain = serviceSMAppsLauncher.getConnectionDetails().getDomain();

            return HelperArchive.getArchiveCOMObjectList(archiveService.getArchiveStub(),
                    AppsLauncherHelper.APPSLAUNCHER_SERVICE.APP_OBJECT_TYPE, domain, appResponse.getBodyElement0());
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.SEVERE, "Failed to list Apps from NanoSat: ", ex);
        }
        return null;
    }

    /**
     * Runs an App.
     *
     * @param groundMOAdapter Ground MO Adapter implementation
     * @param appId           ID of the App to run
     */
    public static void runAppById(GroundMOAdapterImpl groundMOAdapter, Long appId) {
        final AppsLauncherConsumerServiceImpl serviceSMAppsLauncher = groundMOAdapter.getSMServices().getAppsLauncherService();

        LongList ids = new LongList();
        ids.add(appId);

        try {
            serviceSMAppsLauncher.getAppsLauncherStub().runApp(ids);
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.SEVERE, "Failed to run App: ", ex);
        }
    }

    public static void stopAppById(GroundMOAdapterImpl groundMOAdapter, Long appId) {
        final AppsLauncherConsumerServiceImpl serviceSMAppsLauncher = groundMOAdapter.getSMServices().getAppsLauncherService();

        LongList ids = new LongList();
        ids.add(appId);

        // unsubscribeFromPreviousEvents(objId);

        try {
            // subscribeToEvents(objId);
            serviceSMAppsLauncher.getAppsLauncherStub().stopApp(ids, new AppsLauncherAdapter() {

                @Override
                public void stopAppAckReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, java.util.Map qosProperties) {
                    Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.INFO, "Stopping...");
                }

                @Override
                public void stopAppUpdateReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, Long appClosing, java.util.Map qosProperties) {
                    Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.INFO, "Stopped!");
                }

                @Override
                public void stopAppAckErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.MOErrorException error, java.util.Map qosProperties) {
                    String msg = "There was an error during the stop operation.";
                    JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.PLAIN_MESSAGE);
                    Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.SEVERE, msg, error);
                }

                @Override
                public void stopAppResponseReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, java.util.Map qosProperties) {
                    Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.INFO, "Stop App Completed.");
                }

            });
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.SEVERE, "Failed to run App: ", ex);
        }
    }
}