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
package esa.mo.ground.constellation.services.sm;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;

import esa.mo.ground.constellation.ConstellationManager;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import esa.mo.sm.impl.consumer.AppsLauncherConsumerServiceImpl;

public class AppManagerGround {
    private static AppsLauncherConsumerServiceImpl serviceSMAppsLauncher;

    /**
     * Launches an App on a NanoSat Segment
     * 
     * Very WIP!
     * 
     * @param services
     * @param appName
     */
    public static void launchApp(GroundMOAdapterImpl services, String appName) {
        serviceSMAppsLauncher = services.getSMServices().getAppsLauncherService();

        IdentifierList ids = new IdentifierList();
        ids.add(new Identifier(appName));

        // LongList ids = new LongList();
        // Long objId =
        // appsTable.getSelectedCOMObject().getArchiveDetails().getInstId();
        // ids.add(objId);

        // unsubscribeFromPreviousEvents(objId);

        // try {
        // subscribeToEvents(objId);
        // serviceSMAppsLauncher.getAppsLauncherStub().runApp(ids);
        // } catch (MALInteractionException | MALException ex) {
        // Logger
        // .getLogger(ConstellationManager.class.getName())
        // .log(Level.SEVERE, null, ex);
        // }

    }
}
