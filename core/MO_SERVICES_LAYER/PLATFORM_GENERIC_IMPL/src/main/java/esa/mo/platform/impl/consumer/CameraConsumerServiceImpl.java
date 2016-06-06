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
package esa.mo.platform.impl.consumer;

import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.helpertools.misc.ConsumerServiceImpl;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.platform.camera.CameraHelper;
import org.ccsds.moims.mo.platform.camera.consumer.CameraStub;

/**
 *
 * @author Cesar Coelho
 */
public class CameraConsumerServiceImpl extends ConsumerServiceImpl {

    private CameraStub cameraService = null;
    private COMServicesConsumer comServices;

    public COMServicesConsumer getCOMServices() {
        return comServices;
    }

    public CameraStub getCameraStub() {
        return this.cameraService;
    }

    @Override
    public Object generateServiceStub(MALConsumer tmConsumer) {
        return new CameraStub(tmConsumer);
    }

    public CameraConsumerServiceImpl(SingleConnectionDetails connectionDetails, COMServicesConsumer comServices) throws MALException, MalformedURLException, MALInteractionException {

        this.connectionDetails = connectionDetails;
        this.comServices = comServices;

        // Close old connection
        if (tmConsumer != null) {
            try {
                tmConsumer.close();
            } catch (MALException ex) {
                Logger.getLogger(CameraConsumerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        tmConsumer = connection.startService(
                this.connectionDetails.getProviderURI(),
                this.connectionDetails.getBrokerURI(),
                this.connectionDetails.getDomain(),
                CameraHelper.CAMERA_SERVICE);

        this.cameraService = new CameraStub(tmConsumer);
    }

}
