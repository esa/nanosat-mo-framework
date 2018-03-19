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
package esa.mo.platform.impl.provider.gen;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.platform.impl.util.PositionsCalculator;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.COMService;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectDetails;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectSet;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectSetList;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.platform.PlatformHelper;
import org.ccsds.moims.mo.platform.gps.GPSHelper;
import org.ccsds.moims.mo.platform.gps.body.GetLastKnownPositionResponse;
import org.ccsds.moims.mo.platform.gps.provider.GPSInheritanceSkeleton;
import org.ccsds.moims.mo.platform.gps.provider.GetNMEASentenceInteraction;
import org.ccsds.moims.mo.platform.gps.provider.GetPositionInteraction;
import org.ccsds.moims.mo.platform.gps.provider.GetSatellitesInfoInteraction;
import org.ccsds.moims.mo.platform.gps.provider.NearbyPositionPublisher;
import org.ccsds.moims.mo.platform.gps.structures.NearbyPositionDefinition;
import org.ccsds.moims.mo.platform.gps.structures.NearbyPositionDefinitionList;
import org.ccsds.moims.mo.platform.gps.structures.Position;
import org.ccsds.moims.mo.platform.gps.structures.PositionList;
import org.ccsds.moims.mo.platform.gps.structures.SatelliteInfoList;
import esa.mo.reconfigurable.service.ReconfigurableService;
import esa.mo.reconfigurable.service.ConfigurationChangeListener;

/**
 * GPS service Provider.
 */
public class GPSProviderServiceImpl extends GPSInheritanceSkeleton implements ReconfigurableService {

    private MALProvider gpsServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private NearbyPositionPublisher publisher;
    private boolean isRegistered = false;
    private final Object lock = new Object();
    private GPSManager manager;
    private PeriodicCurrentPosition periodicCurrentPosition;
    private final ConnectionProvider connection = new ConnectionProvider();
    private GPSAdapterInterface adapter;
    private ConfigurationChangeListener configurationAdapter;

    private final Object MUTEX = new Object();
    private Position currentPosition = null;
    private long timeOfCurrentPosition;

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

        publisher = createNearbyPositionPublisher(ConfigurationProviderSingleton.getDomain(),
                ConfigurationProviderSingleton.getNetwork(),
                SessionType.LIVE,
                ConfigurationProviderSingleton.getSourceSessionName(),
                QoSLevel.BESTEFFORT,
                null,
                new UInteger(0));

        // Shut down old service transport
        if (null != gpsServiceProvider) {
            connection.closeAll();
        }

        manager = new GPSManager(comServices);
        this.adapter = adapter;
        gpsServiceProvider = connection.startService(GPSHelper.GPS_SERVICE_NAME.toString(), GPSHelper.GPS_SERVICE, this);

        periodicCurrentPosition = new PeriodicCurrentPosition();
        periodicCurrentPosition.init();
        running = true;
        initialiased = true;
        periodicCurrentPosition.start();
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

            connection.closeAll();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(GPSProviderServiceImpl.class.getName()).log(Level.WARNING, 
                    "Exception during close down of the provider {0}", ex);
        }
    }

    private void publishNearbyPositionUpdate(final Long objId, final Boolean isInside) {
        try {
            synchronized (lock) {
                if (!isRegistered) {
                    final EntityKeyList lst = new EntityKeyList();
                    lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
                    publisher.register(lst, new PublishInteractionListener());
                    isRegistered = true;
                }
            }

            Logger.getLogger(GPSProviderServiceImpl.class.getName()).log(Level.FINER,
                    "Generating GPS Nearby Position update for: {0} (Identifier: {1})",
                    new Object[]{
                        objId, new Identifier(manager.get(objId).getName().toString())
                    });

            final URI uri = connection.getConnectionDetails().getProviderURI();
            final Long pValObjId = manager.storeAndGenerateNearbyPositionAlertId(isInside, objId, uri);

            final EntityKey ekey = new EntityKey(
                    new Identifier(manager.get(objId).getName().toString()), 
                    objId, pValObjId, 
                    null
            );
            final Time timestamp = HelperTime.getTimestampMillis();

            final UpdateHeaderList hdrlst = new UpdateHeaderList();
            hdrlst.add(new UpdateHeader(timestamp, uri, UpdateType.UPDATE, ekey));

            BooleanList bools = new BooleanList();
            bools.add(isInside);

            publisher.publish(hdrlst, bools);

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(GPSProviderServiceImpl.class.getName()).log(Level.WARNING, 
                    "Exception during publishing process on the provider {0}", ex);
        } catch (MALException ex) {
            Logger.getLogger(GPSProviderServiceImpl.class.getName()).log(Level.WARNING, 
                    "Exception during publishing process on the provider {0}", ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(GPSProviderServiceImpl.class.getName()).log(Level.WARNING, 
                    "Exception during publishing process on the provider {0}", ex);
        }
    }

    @Override
    public void getNMEASentence(String sentenceIdentifier, GetNMEASentenceInteraction interaction) 
            throws MALInteractionException, MALException {
        if (!adapter.isUnitAvailable()) { // Is the unit available?
            throw new MALInteractionException(new MALStandardError(PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null));
        }

        interaction.sendAcknowledgement();

        try {
            String nmeaSentence = adapter.getNMEASentence(sentenceIdentifier);
            interaction.sendResponse(nmeaSentence);
        } catch (IOException ex) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
        }
    }

    @Override
    public GetLastKnownPositionResponse getLastKnownPosition(MALInteraction interaction) 
            throws MALInteractionException, MALException {
        GetLastKnownPositionResponse response = new GetLastKnownPositionResponse();
        final Position pos;
        final long startTime;

        synchronized (MUTEX) {
            pos = currentPosition;
            startTime = timeOfCurrentPosition;
        }

        if (pos == null) { // We never got a position! So we don't know the position!
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, null));
        }

        response.setBodyElement0(pos);
        double elapsedTime = (System.currentTimeMillis() - startTime) / 1000; // convert from milli to sec
        response.setBodyElement1(new Duration(elapsedTime));
        return response;
    }

    @Override
    public void getPosition(GetPositionInteraction interaction) throws MALInteractionException, MALException {
        if (!adapter.isUnitAvailable()) { // Is the unit available?
            throw new MALInteractionException(new MALStandardError(PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null));
        }

        interaction.sendAcknowledgement();

        Position position = adapter.getCurrentPosition();

        synchronized (MUTEX) { // Store the latest Position
            currentPosition = position;
            timeOfCurrentPosition = System.currentTimeMillis();
        }

        interaction.sendResponse(position);
    }

    @Override
    public void getSatellitesInfo(GetSatellitesInfoInteraction interaction) throws MALInteractionException, MALException {
        if (!adapter.isUnitAvailable()) { // Is the unit available?
            throw new MALInteractionException(new MALStandardError(PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null));
        }

        interaction.sendAcknowledgement();

        SatelliteInfoList sats = adapter.getSatelliteInfoList();
        interaction.sendResponse(sats);
    }

    @Override
    public LongList listNearbyPosition(IdentifierList names, MALInteraction interaction) throws MALInteractionException, MALException {
        LongList outLongLst = new LongList();

        if (null == names) { // Is the input null?
            throw new IllegalArgumentException("names argument must not be null");
        }

        for (Identifier attitudeName : names) {
            // Check for the wildcard
            if (attitudeName.toString().equals("*")) {
                outLongLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                outLongLst.addAll(manager.listAll()); // ... add all in a row
                break;
            }

            outLongLst.add(manager.list(attitudeName));
        }

        // Errors
        // The operation does not return any errors.
        return outLongLst;
    }

    @Override
    public LongList addNearbyPosition(final NearbyPositionDefinitionList nearbyPositionDefinitions,
            final MALInteraction interaction) throws MALInteractionException, MALException {
        LongList outLongLst = new LongList();
        UIntegerList invIndexList = new UIntegerList();
        UIntegerList dupIndexList = new UIntegerList();
        NearbyPositionDefinition def;

        if (null == nearbyPositionDefinitions) { // Is the input null?
            throw new IllegalArgumentException("nearbyPositionDefinitions argument must not be null");
        }

        for (int index = 0; index < nearbyPositionDefinitions.size(); index++) {
            def = nearbyPositionDefinitions.get(index);
            Identifier name = def.getName();

            // Check if the name field of the AttitudeDefinition is invalid.
            if (name == null
                    || name.equals(new Identifier("*"))
                    || name.equals(new Identifier(""))) {
                invIndexList.add(new UInteger(index));
            }

            if (manager.list(name) == null) { // Is the supplied name unique?
                ObjectId source = manager.storeCOMOperationActivity(interaction);
                outLongLst.add(manager.add(def, source, connection.getConnectionDetails().getProviderURI()));
            } else {
                dupIndexList.add(new UInteger(index)); //  requirement: 3.4.10.2.c
            }
        }

        // Errors
        if (!dupIndexList.isEmpty()) { // requirement: 3.4.10.3.1
            throw new MALInteractionException(new MALStandardError(COMHelper.DUPLICATE_ERROR_NUMBER, dupIndexList));
        }

        if (!invIndexList.isEmpty()) { // requirement: 3.4.10.3.2
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (configurationAdapter != null) {
            configurationAdapter.onConfigurationChanged(this);
        }

        return outLongLst;
    }

    @Override
    public void removeNearbyPosition(LongList objInstIds, MALInteraction interaction) 
            throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        Long tempLong;
        LongList tempLongLst = new LongList();

        if (null == objInstIds) { // Is the input null?
            throw new IllegalArgumentException("objInstIds argument must not be null");
        }

        for (int index = 0; index < objInstIds.size(); index++) {
            tempLong = objInstIds.get(index);

            if (tempLong == 0) {  // Is it the wildcard '0'?
                tempLongLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                tempLongLst.addAll(manager.listAll()); // ... add all in a row
                break;
            }

            if (manager.exists(tempLong)) { // Does it match an existing definition?
                tempLongLst.add(tempLong);
            } else {
                unkIndexList.add(new UInteger(index));
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        for (Long tempLong2 : tempLongLst) {
            manager.delete(tempLong2);  // COM archive is left untouched.
        }

        if (configurationAdapter != null) {
            configurationAdapter.onConfigurationChanged(this);
        }
    }

    public static final class PublishInteractionListener implements MALPublishInteractionListener {

        @Override
        public void publishDeregisterAckReceived(final MALMessageHeader header, final Map qosProperties)
                throws MALException {
            Logger.getLogger(GPSProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishDeregisterAckReceived");
        }

        @Override
        public void publishErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties)
                throws MALException {
            Logger.getLogger(GPSProviderServiceImpl.class.getName()).warning("PublishInteractionListener::publishErrorReceived");
        }

        @Override
        public void publishRegisterAckReceived(final MALMessageHeader header, final Map qosProperties)
                throws MALException {
            Logger.getLogger(GPSProviderServiceImpl.class.getName()).log(Level.INFO, "Registration Ack: {0}", header.toString());
        }

        @Override
        public void publishRegisterErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties) throws MALException {
            Logger.getLogger(GPSProviderServiceImpl.class.getName()).warning("PublishInteractionListener::publishRegisterErrorReceived");
        }
    }

    @Override
    public void setOnConfigurationChangeListener(ConfigurationChangeListener configurationAdapter) {
        this.configurationAdapter = configurationAdapter;
    }

    @Override
    public Boolean reloadConfiguration(ConfigurationObjectDetails configurationObjectDetails) {
        // Validate the returned configuration...
        if (configurationObjectDetails == null) {
            return false;
        }

        if (configurationObjectDetails.getConfigObjects() == null) {
            return false;
        }

        // 1 because we just have NearbyPosition as configuration objects in this service
        if (configurationObjectDetails.getConfigObjects().size() != 1) {
            return false;
        }

        ConfigurationObjectSet confSet = configurationObjectDetails.getConfigObjects().get(0);

        // Confirm the objType
        if (!confSet.getObjType().equals(GPSHelper.NEARBYPOSITION_OBJECT_TYPE)) {
            return false;
        }

        // Confirm the domain
        if (!confSet.getDomain().equals(ConfigurationProviderSingleton.getDomain())) {
            return false;
        }

        // If the list is empty, reconfigure the service with nothing...
        if (confSet.getObjInstIds().isEmpty()) {
            manager.reconfigureDefinitions(new LongList(), new PositionList());   // Reconfigures the Manager
            return true;
        }

        // ok, we're good to go...
        // Load the Parameter Definitions from this configuration...
        PositionList pDefs = (PositionList) HelperArchive.getObjectBodyListFromArchive(
                manager.getArchiveService(),
                GPSHelper.NEARBYPOSITION_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(),
                confSet.getObjInstIds());

        manager.reconfigureDefinitions(confSet.getObjInstIds(), pDefs);   // Reconfigures the Manager

        return true;
    }

    @Override
    public ConfigurationObjectDetails getCurrentConfiguration() {
        // Get all the current objIds in the serviceImpl
        // Create a Configuration Object with all the objs of the provider
        HashMap<Long, Element> defObjs = manager.getCurrentDefinitionsConfiguration();

        ConfigurationObjectSet objsSet = new ConfigurationObjectSet();
        objsSet.setDomain(ConfigurationProviderSingleton.getDomain());
        LongList currentObjIds = new LongList();
        currentObjIds.addAll(defObjs.keySet());
        objsSet.setObjInstIds(currentObjIds);
        objsSet.setObjType(GPSHelper.NEARBYPOSITION_OBJECT_TYPE);

        ConfigurationObjectSetList list = new ConfigurationObjectSetList();
        list.add(objsSet);

        // Needs the Common API here!
        ConfigurationObjectDetails set = new ConfigurationObjectDetails();
        set.setConfigObjects(list);

        return set;
    }

    @Override
    public COMService getCOMService() {
        return GPSHelper.GPS_SERVICE;
    }

    private class PeriodicCurrentPosition {

        private final Timer timer;
        boolean active = false; // Flag that determines if publishes or not
        private static final int PERIOD = 5000; // 5 Seconds

        public PeriodicCurrentPosition() {
            timer = new Timer("GPS_PeriodicReportingManager");
        }

        public void start() {
            active = true;
        }

        public void pause() {
            active = false;
        }

        public void init() {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (active) {
                        if (!adapter.isUnitAvailable()) { // Is the unit available?
                            return;
                        }

                        final Position pos = adapter.getCurrentPosition(); // Current Position

                        synchronized (MUTEX) {
                            currentPosition = pos;
                            timeOfCurrentPosition = System.currentTimeMillis();
                        }

                        // Compare with all the available definitions and raise 
                        // NearbyPositionAlerts in case something has changed
                        LongList ids = manager.listAll();

                        for (int i = 0; i < ids.size(); i++) {
                            Long objId = ids.get(i);
                            NearbyPositionDefinition def = manager.get(objId);
                            Boolean previousState = manager.getPreviousStatus(objId);

                            try {
                                double distance = PositionsCalculator.deltaDistanceFrom2Points(def.getPosition(), pos);
                                boolean isInside = (distance < def.getDistanceBoundary());

                                if (previousState == null) { // Maybe it's the first run...
                                    manager.setPreviousStatus(objId, isInside);
                                    continue;
                                }

                                // If the status changed, then publish a Nearby Event
                                if (previousState != isInside) {
                                    publishNearbyPositionUpdate(objId, isInside);
                                    manager.setPreviousStatus(objId, isInside);
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(GPSProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }, 0, PERIOD);
        }
    }

}
