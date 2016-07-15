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
package esa.mo.platform.impl.provider;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.helpertools.connections.ConnectionProvider;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.platform.PlatformHelper;
import org.ccsds.moims.mo.platform.gps.GPSHelper;
import org.ccsds.moims.mo.platform.gps.body.GetLastKnownPositionResponse;
import org.ccsds.moims.mo.platform.gps.provider.GPSInheritanceSkeleton;
import org.ccsds.moims.mo.platform.gps.provider.GetNMEASentenceInteraction;
import org.ccsds.moims.mo.platform.gps.provider.GetPositionInteraction;
import org.ccsds.moims.mo.platform.gps.provider.GetSatellitesInfoInteraction;
import org.ccsds.moims.mo.platform.gps.structures.Position;
import org.ccsds.moims.mo.platform.gps.structures.PositionList;

/**
 *
 */
public class GPSProviderServiceImpl extends GPSInheritanceSkeleton {

    private MALProvider gpsServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    private GPSAdapterInterface adapter;

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param comServices
     * @param adapter
     * @throws MALException On initialisation error.
     */
    public synchronized void init(final COMServicesProvider comServices, final GPSAdapterInterface adapter) throws MALException {
        if (!initialiased) {

            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(PlatformHelper.PLATFORM_AREA_NAME, PlatformHelper.PLATFORM_AREA_VERSION) == null) {
                PlatformHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            try {
                GPSHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) { // nothing to be done..
            }
        }

        // Shut down old service transport
        if (null != gpsServiceProvider) {
            connection.close();
        }

        this.adapter = adapter;
        gpsServiceProvider = connection.startService(GPSHelper.GPS_SERVICE_NAME.toString(), GPSHelper.GPS_SERVICE, false, this);

        running = true;
        initialiased = true;
        Logger.getLogger(GPSProviderServiceImpl.class.getName()).info("GPS service READY");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != gpsServiceProvider) {
                gpsServiceProvider.close();
            }

            connection.close();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(GPSProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    @Override
    public void getNMEASentence(String sentenceIdentifier, GetNMEASentenceInteraction interaction) throws MALInteractionException, MALException {

        
        interaction.sendResponse(adapter.getNMEASentence(sentenceIdentifier));

    }

    @Override
    public GetLastKnownPositionResponse getLastKnownPosition(Boolean higherPrecision, MALInteraction interaction) throws MALInteractionException, MALException {

        long startTime = System.currentTimeMillis();
        GetLastKnownPositionResponse response = new GetLastKnownPositionResponse();
        //Simulator interaction
//        Position position =  PGPS.FirmwareReferenceOEM16.gpggartk2partialPosition(instrumentsSimulator.getpGPS().getNMEASentence("GPGGARTK"));

        Position position = new Position();  // Get it from the position that is being polled

        
        //<<<<<<<<<<<<<<<<<<<<<
        response.setBodyElement0(position);      
        //Measuring time for command
        double elapsedTime = (System.currentTimeMillis() - startTime) * 1000;
        response.setBodyElement1(new Duration(elapsedTime));
        return response;
    }

    @Override
    public void getPosition(Boolean higherPrecision, GetPositionInteraction interaction) throws MALInteractionException, MALException {

        Position position =  adapter.getCurrentPosition();

        interaction.sendResponse(position);

    }

    @Override
    public void getSatellitesInfo(GetSatellitesInfoInteraction interaction) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LongList listNearbyPosition(IdentifierList names, MALInteraction interaction) throws MALInteractionException, MALException {
        throw new MALInteractionException(new MALStandardError(MALHelper.UNSUPPORTED_OPERATION_ERROR_NUMBER, null));
    }

    @Override
    public LongList addNearbyPosition(PositionList nearbyPosition, IdentifierList names, MALInteraction interaction) throws MALInteractionException, MALException {
        throw new MALInteractionException(new MALStandardError(MALHelper.UNSUPPORTED_OPERATION_ERROR_NUMBER, null));
    }

    @Override
    public void removeNearbyPosition(LongList objInstIds, MALInteraction interaction) throws MALInteractionException, MALException {
        throw new MALInteractionException(new MALStandardError(MALHelper.UNSUPPORTED_OPERATION_ERROR_NUMBER, null));
    }

}
