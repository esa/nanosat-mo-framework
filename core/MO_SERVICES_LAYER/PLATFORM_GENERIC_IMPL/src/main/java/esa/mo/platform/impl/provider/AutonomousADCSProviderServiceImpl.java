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
import esa.mo.reconfigurable.service.ConfigurationNotificationInterface;
import esa.mo.reconfigurable.service.ReconfigurableServiceImplInterface;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.COMService;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectDetails;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.platform.PlatformHelper;
import org.ccsds.moims.mo.platform.autonomousadcs.AutonomousADCSHelper;
import org.ccsds.moims.mo.platform.autonomousadcs.provider.AutonomousADCSInheritanceSkeleton;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinition;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionList;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.ReferenceFrame;

/**
 *
 */
public class AutonomousADCSProviderServiceImpl extends AutonomousADCSInheritanceSkeleton implements ReconfigurableServiceImplInterface {

    private MALProvider autonomousADCSServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private boolean isRegistered = false;
    private AutonomousADCSManager manager;
    private final ConnectionProvider connection = new ConnectionProvider();
    private AutonomousADCSAdapterInterface adapter;
    private boolean adcsInUse;
    private ConfigurationNotificationInterface configurationAdapter;
    private Thread autoUnsetThread = null;

    // private blablabla ... The IFineADCS implementation object
    
    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param comServices COM services provider
     * @param adapter The adapter for the ADCS unit interaction
     * @throws MALException On initialisation error.
     */
    public synchronized void init(COMServicesProvider comServices, AutonomousADCSAdapterInterface adapter) throws MALException {
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
                AutonomousADCSHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) { // nothing to be done..
            }
        }

        // Shut down old service transport
        if (null != autonomousADCSServiceProvider) {
            connection.close();
        }

        this.adapter = adapter;
        autonomousADCSServiceProvider = connection.startService(AutonomousADCSHelper.AUTONOMOUSADCS_SERVICE_NAME.toString(), AutonomousADCSHelper.AUTONOMOUSADCS_SERVICE, false, this);

        manager = new AutonomousADCSManager(comServices);
        running = true;
        initialiased = true;
        Logger.getLogger(AutonomousADCSProviderServiceImpl.class.getName()).info("GPS service READY");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != autonomousADCSServiceProvider) {
                autonomousADCSServiceProvider.close();
            }

            connection.close();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(AutonomousADCSProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }
/*
    private void publishCurrentAttitude(final Long objId) {
        try {
            if (!isRegistered) {
                final EntityKeyList lst = new EntityKeyList();
                lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
                publisher.register(lst, new PublishInteractionListener());

                isRegistered = true;
            }

            Logger.getLogger(AutonomousADCSProviderServiceImpl.class.getName()).log(Level.FINER,
                    "Generating Parameter update for the Parameter Definition: {0} (Identifier: {1})",
                    new Object[]{
                        objId, new Identifier(manager.get(objId).getName().toString())
                    });


            //  requirements: 3.3.5.2.1 , 3.3.5.2.2 , 3.3.5.2.3 , 3.3.5.2.4
            final EntityKey ekey = new EntityKey(new Identifier(manager.get(objId).getName().toString()), objId, pValObjId, null);
            final Time timestamp = HelperTime.getTimestampMillis(); //  requirement: 3.3.5.2.5

            
            AttitudeTM tm = adapter.getAttitudeTM();
            
            final UpdateHeaderList hdrlst = new UpdateHeaderList();
            final ObjectIdList objectIdlst = new ObjectIdList();
            final ParameterValueList pVallst = new ParameterValueList();

            hdrlst.add(new UpdateHeader(timestamp, connection.getConnectionDetails().getProviderURI(), UpdateType.UPDATE, ekey));
            objectIdlst.add(null); // requirement: 3.3.5.2.7 (3.3.5.2.6 not necessary. We will not use it for periodic updates)
            pVallst.add(parameterValue); // requirement: 3.3.5.2.8

            publisher.publish(hdrlst, objectIdlst, pVallst);

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(AutonomousADCSProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        } catch (MALException ex) {
            Logger.getLogger(AutonomousADCSProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(AutonomousADCSProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        }
    }
    */
    
    @Override
    public void configureMonitoring(Duration streamingRate, MALInteraction interaction) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
        
        
        
    }

    @Override
    public synchronized void setDesiredAttitude(final Long objInstId, final Duration autoUnset, final MALInteraction interaction) throws MALInteractionException, MALException {
        if (null == objInstId) { // Is the input null?
            throw new IllegalArgumentException("objInstId argument must not be null");
        }
        
        if (adapter.isUnitAvailable()) { // Is the ADCS unit available?
            throw new MALInteractionException(new MALStandardError(AutonomousADCSHelper.ADCS_NOT_AVAILABLE_ERROR_NUMBER, null));
        }

        if (adcsInUse) { // Is the ADCS unit in use?
            Duration duration = manager.getTimeLeft();
            throw new MALInteractionException(new MALStandardError(PlatformHelper.DEVICE_IN_USE_ERROR_NUMBER, duration));
        }
        
        AttitudeDefinition attitude = manager.get(objInstId);
        
        if(attitude == null){
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, null));
        }
        
        // Validate the attitude definition...
        String invalidField = manager.invalidField(attitude);
        
        if(invalidField != null){
            // Oppss.. there's an invalid field! Throw it!
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invalidField));
        }

        try {
            // Now we can finally set the desiredAttitude!
            adapter.setDesiredAttitude(attitude);
        } catch (IOException ex) {
            // Operation not supported by the implementation...
            throw new MALInteractionException(new MALStandardError(MALHelper.UNSUPPORTED_OPERATION_ERROR_NUMBER, null));
        }

        // Start auto-timer to unset
        autoUnsetThread = new Thread() {
            @Override
            public void run() {
                // Store current time
                manager.markAvailableTime(autoUnset);
                adcsInUse = true;

                try {
                    Thread.sleep((long) autoUnset.getValue()*1000); // Conversion to miliseconds

                    try {
                        adapter.unset();
                        adcsInUse = false;
                    } catch (IOException ex) {
                        Logger.getLogger(AutonomousADCSProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (InterruptedException ex) {
                    // The unset operation was called manually, nothing wrong here, the automatic unset is disabled! :)
                }
                
            }
        };
        
        autoUnsetThread.start();
        
    }

    @Override
    public synchronized void unsetAttitude(MALInteraction interaction) throws MALInteractionException, MALException {
        // Stop the current Thread to automatically unset the Attitude
        if (autoUnsetThread != null){
            autoUnsetThread.interrupt();
        }
        
        if (adapter.isUnitAvailable()) { // Is the ADCS unit available?
            throw new MALInteractionException(new MALStandardError(AutonomousADCSHelper.ADCS_NOT_AVAILABLE_ERROR_NUMBER, null));
        }
        
        try {
            adapter.unset();
        } catch (IOException ex) {
            Logger.getLogger(AutonomousADCSProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public LongList addAttitudeDefinition(AttitudeDefinitionList attitudeDefinitions, MALInteraction interaction) throws MALInteractionException, MALException {
        LongList outLongLst = new LongList();
        UIntegerList invIndexList = new UIntegerList();
        UIntegerList dupIndexList = new UIntegerList();
        AttitudeDefinition def;

        if (null == attitudeDefinitions) { // Is the input null?
            throw new IllegalArgumentException("attitudeDefinitions argument must not be null");
        }

        for (int index = 0; index < attitudeDefinitions.size(); index++) {
            def = (AttitudeDefinition) attitudeDefinitions.get(index);

            // Check if the name field of the AttitudeDefinition is invalid.
            if (def.getName() == null
                    || def.getName().equals(new Identifier("*"))
                    || def.getName().equals(new Identifier(""))) {
                invIndexList.add(new UInteger(index));
            }

            if (manager.list(def.getName()) == null) { // Is the supplied name unique?
                ObjectId source = manager.storeCOMOperationActivity(interaction);
                outLongLst.add(manager.add(def, source, connection.getConnectionDetails()));
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

        if (configurationAdapter != null){
            configurationAdapter.configurationChanged(this);
        }

        return outLongLst;
    }

    @Override
    public synchronized void removeAttitudeDefinition(LongList objIds, MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        Long tempLong;
        LongList tempLongLst = new LongList();

        if (null == objIds) { // Is the input null?
            throw new IllegalArgumentException("objIds argument must not be null");
        }

        for (int index = 0; index < objIds.size(); index++) {
            tempLong = objIds.get(index);

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
        if (!unkIndexList.isEmpty()) { // requirement: 3.4.12.3.1
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        // Inserting the errors before this line guarantees that the requirement is met
        for (Long tempLong2 : tempLongLst) {
            manager.delete(tempLong2);  // COM archive is left untouched.
        }

        if (configurationAdapter != null){
            configurationAdapter.configurationChanged(this);
        }        
    }

    @Override
    public LongList listAttitudeDefinition(IdentifierList names, MALInteraction interaction) throws MALInteractionException, MALException {
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
    public void setConfigurationAdapter(ConfigurationNotificationInterface configurationAdapter) {
        this.configurationAdapter = configurationAdapter;
    }

    @Override
    public Boolean reloadConfiguration(ConfigurationObjectDetails configurationObjectDetails) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ConfigurationObjectDetails getCurrentConfiguration() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public COMService getCOMService() {
        return AutonomousADCSHelper.AUTONOMOUSADCS_SERVICE;
    }

    public static final class PublishInteractionListener implements MALPublishInteractionListener {
        @Override
        public void publishDeregisterAckReceived(final MALMessageHeader header, final Map qosProperties)
                throws MALException {
            Logger.getLogger(AutonomousADCSProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishDeregisterAckReceived");
        }

        @Override
        public void publishErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties)
                throws MALException {
            Logger.getLogger(AutonomousADCSProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishErrorReceived");
        }

        @Override
        public void publishRegisterAckReceived(final MALMessageHeader header, final Map qosProperties)
                throws MALException {
            Logger.getLogger(AutonomousADCSProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishRegisterAckReceived");
        }

        @Override
        public void publishRegisterErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties) throws MALException {
            Logger.getLogger(AutonomousADCSProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishRegisterErrorReceived");
        }
    }
    
}
