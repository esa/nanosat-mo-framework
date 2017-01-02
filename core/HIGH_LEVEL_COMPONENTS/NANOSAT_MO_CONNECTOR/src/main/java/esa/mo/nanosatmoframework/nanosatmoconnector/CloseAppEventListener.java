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
package esa.mo.nanosatmoframework.nanosatmoconnector;

import esa.mo.com.impl.util.EventCOMObject;
import esa.mo.com.impl.util.EventReceivedListener;
import esa.mo.common.impl.consumer.DirectoryConsumerServiceImpl;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;

/**
 *
 * @author Cesar Coelho
 */
public class CloseAppEventListener extends EventReceivedListener {

    private final NanoSatMOConnectorImpl provider;

    public CloseAppEventListener(NanoSatMOConnectorImpl provider) {
        this.provider = provider;
    }

    @Override
    public void onDataReceived(EventCOMObject eventCOMObject) {

        Logger.getLogger(CloseAppEventListener.class.getName()).log(Level.INFO, "New Close Event Received!");

        // Make sure that it is indeed a Close App event for us!
        // Even thought the subscription will guarantee that...
        // We need to check if it really is a Close App Event request...
        if (!eventCOMObject.getObjType().equals(AppsLauncherHelper.STOPAPP_OBJECT_TYPE)) {
            return; // If not, get out..
        }

        // Acknowledge the reception of the request to close (Closing...)
        // To be done...
//        Long eventId = provider.getCOMServices().getEventService().generateAndStoreEvent(objType, domain, eventObjBody, Long.MIN_VALUE, source, interaction);
//        provider.getCOMServices().getEventService().publishEvent(interaction, Long.MIN_VALUE, objType, Long.MIN_VALUE, source, eventBodies);
        // Close the app...
        // Make a call on the app layer to close nicely...
        if (provider.closeAppAdapter != null) {
            Logger.getLogger(CloseAppEventListener.class.getName()).log(Level.INFO, "Triggering the closeAppAdapter of the app business logic...");
            provider.closeAppAdapter.onClose(); // Time to sleep, boy!
        }

        // Unregister the provider from the Central Directory service...
        URI centralDirectoryURI = provider.readCentralDirectoryServiceURI();

        if (centralDirectoryURI != null) {
            try {
                DirectoryConsumerServiceImpl directoryServiceConsumer = new DirectoryConsumerServiceImpl(centralDirectoryURI);
                directoryServiceConsumer.getDirectoryStub().withdrawProvider(provider.getAppDirectoryId());
                directoryServiceConsumer.closeConnection();
            } catch (MALException ex) {
                Logger.getLogger(CloseAppEventListener.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedURLException ex) {
                Logger.getLogger(CloseAppEventListener.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE, "There was a problem while connectin to the Central Directory service on URI: " + centralDirectoryURI.getValue() + "\nException: " + ex);
            }
        }

        // Should close them safely as well...
//        provider.getMCServices().closeServices();
//        provider.getCOMServices().closeServices();
        // Exit the Java application
        Logger.getLogger(CloseAppEventListener.class.getName()).log(Level.INFO, "Success! The currently running Java Virtual Machine will now terminate.");
        System.exit(0);

    }

}
