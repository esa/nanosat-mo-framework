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
package esa.mo.nmf.nanosatmoconnector;

import esa.mo.com.impl.util.EventCOMObject;
import esa.mo.com.impl.util.EventReceivedListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;

/**
 * The Listener for Event requests to close the NMF App. This class is used
 * internally, after receiving an event to close, this class will trigger the
 * closeGracefully method of the provider. To set a dedicated adapter, please
 * use the setCloseAppListener method of the NanoSatMOConnectorImpl.
 */
public class CloseAppEventListener extends EventReceivedListener {

    private final NanoSatMOConnectorImpl provider;

    public CloseAppEventListener(final NanoSatMOConnectorImpl provider) {
        this.provider = provider;
    }

    @Override
    public void onDataReceived(final EventCOMObject eventCOMObject) {
        // Make sure that it is indeed a Close App event for us!
        final Identifier name = (Identifier) eventCOMObject.getBody();

        if (!provider.providerName.equals(name.getValue())) {
            Logger.getLogger(CloseAppEventListener.class.getName()).log(Level.FINE,
                    "This Event is not for us! App Directory Id: " + provider.getAppDirectoryId()
                    + " , Related: " + eventCOMObject.getRelated() + " name: " + name.getValue());

            return; // If not, get out..
        }

        Logger.getLogger(CloseAppEventListener.class.getName()).log(Level.FINE,
                "This Event is for us! App Directory Id: " + provider.getAppDirectoryId()
                + " , Related: " + eventCOMObject.getRelated() + " name: " + name.getValue());

        // Even though the subscription will guarantee that...
        // It is better to double-check if it is a Close App Event request...
        if (!eventCOMObject.getObjType().equals(AppsLauncherHelper.STOPAPP_OBJECT_TYPE)) {
            return; // If not, get out..
        }

        Logger.getLogger(CloseAppEventListener.class.getName()).log(Level.INFO,
                "New StopApp Event Received! " + "For provider: '" + name.getValue()
                + "' (Related link: " + eventCOMObject.getRelated() + ")");

        final ObjectId source = eventCOMObject.getObjectId();
        this.provider.closeGracefully(source);
    }

}
