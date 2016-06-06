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
package esa.mo.platform.impl.util;

import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.platform.impl.consumer.CameraConsumerServiceImpl;
import esa.mo.platform.impl.consumer.GPSConsumerServiceImpl;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.platform.camera.CameraHelper;
import org.ccsds.moims.mo.platform.gps.GPSHelper;

/**
 *
 *
 */
public class PlatformServicesConsumer {

    private CameraConsumerServiceImpl cameraService;
    private GPSConsumerServiceImpl gpsService;

    public void init(ConnectionConsumer connectionConsumer, COMServicesConsumer comServices) {

        SingleConnectionDetails details;

        try {
            // Initialize the Camera service
            details = connectionConsumer.getServicesDetails().get(CameraHelper.CAMERA_SERVICE_NAME);
            if(details != null){
                cameraService = new CameraConsumerServiceImpl(details, comServices);
            }

            // Initialize the GPS service
            details = connectionConsumer.getServicesDetails().get(GPSHelper.GPS_SERVICE_NAME);
            if(details != null){
                gpsService = new GPSConsumerServiceImpl(details, comServices);
            }

        } catch (MALException  ex) {
            Logger.getLogger(COMServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException  ex) {
            Logger.getLogger(COMServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(COMServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public CameraConsumerServiceImpl getCameraService() {
        return this.cameraService;
    }

    public GPSConsumerServiceImpl getGPSService() {
        return this.gpsService;
    }

    public void setServices(
            CameraConsumerServiceImpl cameraService,
            GPSConsumerServiceImpl gpsService) {
        this.cameraService = cameraService;
        this.gpsService = gpsService;
    }

    public void setCameraService(CameraConsumerServiceImpl cameraService) {
        this.cameraService = cameraService;
    }

    public void setGPSService(GPSConsumerServiceImpl gpsService) {
        this.gpsService = gpsService;
    }

    /**
     * Closes the service consumer connections
     *
     */
    public void closeConnections() {
        if(this.cameraService != null){
            this.cameraService.closeConnection();
        }
        
        if(this.gpsService != null){
            this.gpsService.closeConnection();
        }
    }
    
    
}
