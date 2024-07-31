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
package esa.mo.sm.impl.util;

import esa.mo.com.impl.consumer.EventConsumerServiceImpl;
import esa.mo.com.impl.util.EventCOMObject;
import esa.mo.com.impl.util.EventReceivedListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.provider.StopAppInteraction;

/**
 * Create the listeners for the returned event by the app, acknowledging the
 * action to close itself.
 *
 * @author Cesar Coelho
 */
public class ClosingAppListener extends EventReceivedListener {

    private static final Logger LOGGER = Logger.getLogger(ClosingAppListener.class.getName());
    private final StopAppInteraction interaction;
    private final EventConsumerServiceImpl eventService;
    private final Long objId;
    private boolean appClosed;
    private final Object semaphore; // This should be done with a proper Semaphore... :/

    public ClosingAppListener(final StopAppInteraction interaction, final EventConsumerServiceImpl eventService,
        final Long objId) {
        this.interaction = interaction;
        this.eventService = eventService;
        this.objId = objId;
        this.appClosed = false;
        this.semaphore = new Object();
    }

    @Override
    public void onDataReceived(EventCOMObject eventCOMObject) {
        if (eventCOMObject.getObjType().equals(AppsLauncherHelper.STOPPING_OBJECT_TYPE)) {
            // Is it the ack from the app?
            LOGGER.log(Level.INFO, "The app with objId {0} is stopping...", objId);
        }

        if (eventCOMObject.getObjType().equals(AppsLauncherHelper.STOPPED_OBJECT_TYPE)) {
            LOGGER.log(Level.INFO, "The app with objId {0} is now stopped!", objId);

            try { // Send update to consumer stating that the app is stopped
                if (interaction != null) {
                    interaction.sendUpdate(objId);
                }
            } catch (MALInteractionException | MALException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }

            // If so, then close the connection to the service
            eventService.close();

            synchronized (semaphore) {
                this.appClosed = true;
                semaphore.notifyAll();
            }
        }
    }

    public boolean isAppClosed() {
        return this.appClosed;
    }

    public void waitForAppClosing(long timeout) throws InterruptedException {
        synchronized (semaphore) {
            if (!appClosed) {
                semaphore.wait(timeout);
            }
        }
    }
}
