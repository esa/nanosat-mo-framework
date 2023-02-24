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
package esa.mo.platform.impl.provider.gen;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.helpertools.misc.Const;
import esa.mo.helpertools.misc.TaskScheduler;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.DurationList;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.platform.PlatformHelper;
import org.ccsds.moims.mo.platform.autonomousadcs.AutonomousADCSHelper;
import org.ccsds.moims.mo.platform.autonomousadcs.body.GetStatusResponse;
import org.ccsds.moims.mo.platform.autonomousadcs.provider.AutonomousADCSInheritanceSkeleton;
import org.ccsds.moims.mo.platform.autonomousadcs.provider.MonitorAttitudePublisher;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.ActuatorsTelemetry;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.ActuatorsTelemetryList;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeMode;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeBDotList;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeList;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeTelemetry;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeTelemetryList;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.ReactionWheelIdentifier;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.ReactionWheelParameters;

/**
 * AutonomousADCS service Provider.
 */
public class AutonomousADCSProviderServiceImpl extends AutonomousADCSInheritanceSkeleton {

    private final static Logger LOGGER = Logger.getLogger(
            AutonomousADCSProviderServiceImpl.class.getName());
    private final static Duration MINIMUM_MONITORING_PERIOD = new Duration(0.1); // 100 Milliseconds
    private int resultCacheValidityMs;
    private MALProvider autonomousADCSServiceProvider;
    private boolean initialiased = false;
    private boolean generationEnabled = false;
    private MonitorAttitudePublisher publisher;
    private boolean isRegistered = false;
    private final Object lock = new Object();
    private final ConnectionProvider connection = new ConnectionProvider();
    private AutonomousADCSAdapterInterface adapter;
    private boolean adcsInUse;
    private TaskScheduler publishTimer = new TaskScheduler(1);
    private Thread autoUnsetThread = null;
    private long attitudeControlEndTime = 0;
    private int monitoringPeriod = 5000; // Default value: 5 seconds
    private AttitudeTelemetry lastAttitudeTm = null;
    private ActuatorsTelemetry lastActuatorsTm = null;
    private IOException lastAttitudeTmException = null;
    private IOException lastActuatorsTmException = null;
    private long lastAttitudeTmTime = 0;
    private long lastActuatorsTmTime = 0;

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param comServices COM services provider
     * @param adapter The adapter for the ADCS unit interaction
     * @throws MALException On initialisation error.
     */
    public synchronized void init(COMServicesProvider comServices,
            AutonomousADCSAdapterInterface adapter) throws MALException {
        long timestamp = System.currentTimeMillis();
        
        if (!initialiased) {
            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(PlatformHelper.PLATFORM_AREA_NAME,
                    PlatformHelper.PLATFORM_AREA_VERSION) == null) {
                PlatformHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(PlatformHelper.PLATFORM_AREA_NAME,
                    PlatformHelper.PLATFORM_AREA_VERSION)
                    .getServiceByName(AutonomousADCSHelper.AUTONOMOUSADCS_SERVICE_NAME) == null) {
                AutonomousADCSHelper.init(MALContextFactory.getElementFactoryRegistry());
            }
        }
        resultCacheValidityMs = Integer.parseInt(System.getProperty(Const.PLATFORM_IADCS_CACHING_PERIOD, "1000"));

        publisher = createMonitorAttitudePublisher(ConfigurationProviderSingleton.getDomain(),
                ConfigurationProviderSingleton.getNetwork(),
                SessionType.LIVE,
                ConfigurationProviderSingleton.getSourceSessionName(),
                QoSLevel.BESTEFFORT,
                null,
                new UInteger(0));

        // Shut down old service transport
        if (null != autonomousADCSServiceProvider) {
            connection.closeAll();
        }

        this.adapter = adapter;
        autonomousADCSServiceProvider = connection.startService(
                AutonomousADCSHelper.AUTONOMOUSADCS_SERVICE_NAME.toString(),
                AutonomousADCSHelper.AUTONOMOUSADCS_SERVICE, true, this);

        initialiased = true;
        timestamp = System.currentTimeMillis() - timestamp;
        LOGGER.info("AutonomousADCS service: READY! (" + timestamp + " ms)");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public synchronized void close() {
        try {
            if (null != autonomousADCSServiceProvider) {
                autonomousADCSServiceProvider.close();
            }

            connection.closeAll();
            stopGeneration();
        } catch (MALException ex) {
            LOGGER.log(Level.WARNING, "Exception during close down of the provider.", ex);
        }
    }

    private void publishCurrentAttitude() {
        if (!adapter.isUnitAvailable()) {
            // Abort publishing
            stopGeneration();
            return;
        }

        synchronized (lock) {
            if (!isRegistered) {
                final EntityKeyList lst = new EntityKeyList();
                lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
                try {
                    publisher.register(lst, new PublishInteractionListener());
                } catch (IllegalArgumentException | MALException | MALInteractionException ex) {
                    LOGGER.log(Level.WARNING, "Error when registering the publisher!", ex);
                    return;
                }
                isRegistered = true;
            }
        }

        try {
            final AttitudeTelemetry attitudeTelemetry = getAttitudeTelemetry();
            final AttitudeTelemetryList attitudeTelemetryList
                    = (AttitudeTelemetryList) HelperMisc.element2elementList(attitudeTelemetry);
            attitudeTelemetryList.add(attitudeTelemetry);

            final ActuatorsTelemetry actuatorsTelemetry = getActuatorsTelemetry();
            final ActuatorsTelemetryList actuatorsTelemetryList
                    = (ActuatorsTelemetryList) HelperMisc.element2elementList(actuatorsTelemetry);
            actuatorsTelemetryList.add(actuatorsTelemetry);

            final AttitudeMode activeAttitudeMode = getActiveAttitudeMode();

            AttitudeModeList attitudeModeList;
            if (activeAttitudeMode == null) {
                // Pick a dummy concrete type type just to fill it with a null value
                attitudeModeList = new AttitudeModeBDotList();
            } else {
                attitudeModeList = (AttitudeModeList) HelperMisc.element2elementList(
                        activeAttitudeMode);
            }
            attitudeModeList.add(activeAttitudeMode);

            final DurationList durationList = new DurationList();
            durationList.add(getAttitudeControlRemainingDuration());

            final EntityKey ekey = new EntityKey(null, null, null, null);
            final Time timestamp = HelperTime.getTimestampMillis();
            final UpdateHeaderList hdrlst = new UpdateHeaderList();
            hdrlst.add(new UpdateHeader(timestamp, connection.getConnectionDetails().getProviderURI(),
                    UpdateType.UPDATE, ekey));

            publisher.publish(hdrlst, attitudeTelemetryList, actuatorsTelemetryList, durationList,
                    attitudeModeList);
        } catch (IOException | IllegalArgumentException | MALException | MALInteractionException ex) {
            LOGGER.log(Level.SEVERE, "Error when trying to publish data!", ex);
        }
    }

    @Override
    public void enableMonitoring(Boolean enableGeneration, Duration monitoringInterval,
            MALInteraction interaction) throws MALInteractionException, MALException {
        if (!enableGeneration) {
            stopGeneration();
            return;
        }
        if (!adapter.isUnitAvailable()) {
            throw new MALInteractionException(new MALStandardError(
                    PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null));
        }
        // Is the requested streaming rate less than the minimum period?
        if (monitoringInterval == null || monitoringInterval.getValue() < MINIMUM_MONITORING_PERIOD.getValue()) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER,
                    MINIMUM_MONITORING_PERIOD));
        }

        monitoringPeriod = (int) (monitoringInterval.getValue() * 1000); // In milliseconds
        this.startGeneration();
    }

    @Override
    public GetStatusResponse getStatus(MALInteraction interaction) throws MALInteractionException {
        if (!adapter.isUnitAvailable()) {
            throw new MALInteractionException(new MALStandardError(
                    PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null));
        }
        try {
            final AttitudeTelemetry attitudeTelemetry = getAttitudeTelemetry();
            final ActuatorsTelemetry actuatorsTelemetry = getActuatorsTelemetry();
            final AttitudeMode activeAttitudeMode = getActiveAttitudeMode();
            return new GetStatusResponse(attitudeTelemetry, actuatorsTelemetry,
                    getAttitudeControlRemainingDuration(),
                    generationEnabled, new Duration(monitoringPeriod / 1000.f), activeAttitudeMode);
        } catch (IOException ex) {
            Logger.getLogger(AutonomousADCSProviderServiceImpl.class.getName()).log(Level.SEVERE,
                    "Error when producing getStatus response", ex);
            throw new MALInteractionException(new MALStandardError(
                    PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null));
        }

    }

    private AttitudeTelemetry getAttitudeTelemetry() throws IOException {
        if (System.currentTimeMillis() - lastAttitudeTmTime < resultCacheValidityMs) {
            if (lastAttitudeTm != null) {
                return lastAttitudeTm;
            } else {
                throw lastAttitudeTmException;
            }
        }
        try {
            lastAttitudeTm = adapter.getAttitudeTelemetry();
        } catch (IOException e) {
            // Cache the exception
            lastAttitudeTm = null;
            lastAttitudeTmException = e;
            lastAttitudeTmTime = System.currentTimeMillis();
            throw e;
        }
        lastAttitudeTmTime = System.currentTimeMillis();
        return lastAttitudeTm;
    }

    private ActuatorsTelemetry getActuatorsTelemetry() throws IOException {
        if (System.currentTimeMillis() - lastActuatorsTmTime < resultCacheValidityMs) {
            if (lastActuatorsTm != null) {
                return lastActuatorsTm;
            } else {
                throw lastActuatorsTmException;
            }
        }
        try {
            lastActuatorsTm = adapter.getActuatorsTelemetry();
        } catch (IOException e) {
            // Cache the exception
            lastActuatorsTm = null;
            lastActuatorsTmException = e;
            lastActuatorsTmTime = System.currentTimeMillis();
            throw e;
        }
        lastActuatorsTmTime = System.currentTimeMillis();
        return lastActuatorsTm;
    }

    private AttitudeMode getActiveAttitudeMode() {
        final AttitudeMode activeAttitudeMode = adapter.getActiveAttitudeMode();
        return activeAttitudeMode;
    }

    @Override
    public synchronized void setDesiredAttitude(final Duration duration, AttitudeMode desiredAttitude,
            MALInteraction interaction) throws MALInteractionException, MALException {
        if (!adapter.isUnitAvailable()) {
            throw new MALInteractionException(new MALStandardError(
                    PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null));
        }

        if (desiredAttitude == null) {
            // Stop the current Thread to automatically unset the Attitude
            if (autoUnsetThread != null) {
                autoUnsetThread.interrupt();
                autoUnsetThread = null;
            }
            unsetAttitude();
        } else {
            if (adcsInUse) { // Is the ADCS unit in use?
                throw new MALInteractionException(new MALStandardError(
                        PlatformHelper.DEVICE_IN_USE_ERROR_NUMBER, getAttitudeControlRemainingDuration()));
            }

            // Validate the attitude definition...
            String validationResult = adapter.validateAttitudeDescriptor(desiredAttitude);

            if (validationResult != null) {
                throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER,
                        HelperAttributes.javaType2Attribute(validationResult)));
            }

            try {
                // Now we can finally set the desiredAttitude!
                adapter.setDesiredAttitude(desiredAttitude);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Error when setting desired attitude.", ex);
                // Operation not supported by the implementation...
                throw new MALInteractionException(new MALStandardError(
                        MALHelper.UNSUPPORTED_OPERATION_ERROR_NUMBER, null));
            }
            adcsInUse = true;

            // Set automatic unset time
            setAttitudeControlDuration(duration);
        }
    }

    private void unsetAttitude() {
        try {
            adapter.unset();
            adcsInUse = false;
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE,
                    "Cannot disengage attitude control.", ex);
        }
    }

    private void startGeneration() {
        publishTimer.stopLast(); // is this really necessary?
        //publishTimer = new TaskScheduler(1);
        generationEnabled = true;
        publishTimer.scheduleTask(new Thread(() -> {
            if (generationEnabled) {
                publishCurrentAttitude();
            }
        }), monitoringPeriod, monitoringPeriod, TimeUnit.MILLISECONDS, true);
    }

    private void stopGeneration() {
        generationEnabled = false;
        publishTimer.stopLast();
    }

    /**
     * Sets the remaining duration of the currently set attitude control mode.
     *
     * @param duration remaining duration of the iADCS control
     */
    public synchronized void setAttitudeControlDuration(final Duration duration) {
        if (duration == null) {
            attitudeControlEndTime = 0;
            return;
        }

        final long remainingMillis = (long) (duration.getValue() * 1000);
        attitudeControlEndTime = System.currentTimeMillis() + remainingMillis;
        // Start auto-timer to unset
        autoUnsetThread = new Thread(() -> {
            try {
                Thread.sleep(remainingMillis);
                attitudeControlEndTime = 0;
                unsetAttitude();
            } catch (InterruptedException ex) {
                // The unset operation was called manually, nothing wrong here, the automatic unset is disabled! :)
            }

        });
        autoUnsetThread.start();

    }

    public synchronized Duration getAttitudeControlRemainingDuration() {
        if (attitudeControlEndTime == 0) {
            return null; // Return null if the time left is unknown...
        } else {
            return new Duration((attitudeControlEndTime - System.currentTimeMillis()) / 1000.f);
        }
    }

    @Override
    public void setReactionWheelSpeed(ReactionWheelIdentifier wheel, Float speed,
            MALInteraction interaction) throws MALInteractionException, MALException {
        if (!adapter.isUnitAvailable()) {
            throw new MALInteractionException(new MALStandardError(
                    PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null));
        }

        adapter.setReactionWheelSpeed(wheel, speed);

    }

    @Override
    public void setAllReactionWheelSpeeds(Float speedX, Float speedY, Float speedZ, Float speedU,
            Float speedV, Float speedW, MALInteraction interaction) throws MALInteractionException,
            MALException {
        if (!adapter.isUnitAvailable()) {
            throw new MALInteractionException(new MALStandardError(
                    PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null));
        }
        adapter.setAllReactionWheelSpeeds(speedX, speedY, speedZ, speedU, speedV, speedW);
    }

    @Override
    public void setAllReactionWheelParameters(ReactionWheelParameters parameters,
            MALInteraction interaction) throws MALInteractionException, MALException {
        adapter.setAllReactionWheelParameters(parameters);
    }

    @Override
    public void setAllMagnetorquersDipoleMoments(Float dipoleX, Float dipoleY, Float dipoleZ,
            MALInteraction interaction) throws MALInteractionException, MALException {
        if (!adapter.isUnitAvailable()) {
            throw new MALInteractionException(new MALStandardError(
                    PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null));
        }
        adapter.setAllMagnetorquersDipoleMoments(dipoleX, dipoleY, dipoleZ);
    }

    @Override
    public ReactionWheelParameters getAllReactionWheelParameters(MALInteraction interaction) throws
            MALInteractionException, MALException {
        if (!adapter.isUnitAvailable()) {
            throw new MALInteractionException(new MALStandardError(
                    PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null));
        }
        return adapter.getAllReactionWheelParameters();
    }

    private class PublishInteractionListener implements MALPublishInteractionListener {

        @Override
        public void publishDeregisterAckReceived(final MALMessageHeader header, final Map qosProperties)
                throws MALException {
            LOGGER.fine("PublishInteractionListener::publishDeregisterAckReceived");
        }

        @Override
        public void publishErrorReceived(final MALMessageHeader header, final MALErrorBody body,
                final Map qosProperties)
                throws MALException {
            LOGGER.warning("PublishInteractionListener::publishErrorReceived");
        }

        @Override
        public void publishRegisterAckReceived(final MALMessageHeader header, final Map qosProperties)
                throws MALException {
            LOGGER.fine("PublishInteractionListener::publishRegisterAckReceived");
        }

        @Override
        public void publishRegisterErrorReceived(final MALMessageHeader header, final MALErrorBody body,
                final Map qosProperties) throws MALException {
            LOGGER.warning("PublishInteractionListener::publishRegisterErrorReceived");
        }
    }

}
