/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
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
package esa.mo.nmf.nanosatmosupervisor;

import esa.mo.helpertools.misc.OSValidator;
import esa.mo.helpertools.misc.ShellCommander;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperAttributes;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.ExecutionFailedException;
import org.ccsds.moims.mo.mc.structures.*;
import org.ccsds.moims.mo.platform.autonomousadcs.body.GetStatusResponse;
import org.ccsds.moims.mo.platform.autonomousadcs.consumer.AutonomousADCSAdapter;
import org.ccsds.moims.mo.platform.gps.consumer.GPSAdapter;
import org.ccsds.moims.mo.platform.structures.*;

/**
 * MC adapter for the NanoSat MO Supervisor. Registers the supervisor's
 * parameters and actions explicitly, mirroring the pattern used in
 * MCAllInOneAdapter rather than the annotation-based approach.
 *
 * @author Cesar Coelho
 */
public class MCSupervisorBasicAdapter extends MonitorAndControlNMFAdapter {

    private static final Logger LOGGER = Logger.getLogger(MCSupervisorBasicAdapter.class.getName());

    private static final String DATE_PATTERN = "dd MMM yyyy HH:mm:ss.SSS";
    private static final String CMD_CURRENT_PARTITION = "mount -l | grep \"on / \" | grep -o 'mmc.*[0-9]p[0-9]'";
    private static final String CMD_LINUX_VERSION = "uname -a";
    private static final String CMD_WINDOWS_VERSION = "systeminfo | findstr Version";

    private static final String PARAM_OS_VERSION = "OSVersion";
    private static final String PARAM_OS_PARTITION = "OSPartition";
    private static final String PARAM_ATTITUDE_QUAT_A = "attitudeQuatA";
    private static final String PARAM_ATTITUDE_QUAT_B = "attitudeQuatB";
    private static final String PARAM_ATTITUDE_QUAT_C = "attitudeQuatC";
    private static final String PARAM_ATTITUDE_QUAT_D = "attitudeQuatD";
    private static final String PARAM_ATTITUDE_MONITORING_INTERVAL = "attitudeMonitoringInterval";
    private static final String PARAM_GPS_LATITUDE = "GPS_Latitude";
    private static final String PARAM_GPS_LONGITUDE = "GPS_Longitude";
    private static final String PARAM_GPS_N_SATS = "GPS_NumberOfSatellitesInView";
    private static final String PARAM_MAG_X = "MagneticField_X";
    private static final String PARAM_MAG_Y = "MagneticField_Y";
    private static final String PARAM_MAG_Z = "MagneticField_Z";

    private static final String ACTION_ADCS_CONFIGURE_MONITORING = "ADCS.configureMonitoring";
    private static final String ACTION_NMEA_SENTENCE = "NMEA_Sentence";
    private static final String ACTION_CLOCK_SET_TIME = "Clock.setTimeUsingDeltaMilliseconds";
    private static final String ACTION_ADCS_SUN_POINTING = "ADCS.sunpointing";
    private static final String ACTION_ADCS_NADIR_POINTING = "ADCS.nadirPointing";
    private static final String ACTION_ADCS_UNSET_ATTITUDE = "ADCS.unsetAttitude";

    private static final Duration DEFAULT_MONITORING_INTERVAL = new Duration(1.0);

    private final ShellCommander shellCommander = new ShellCommander();
    private final OSValidator osValidator = new OSValidator();
    private NanoSatMOSupervisor nmfSupervisor;

    private float attitudeQuatA = 0f;
    private float attitudeQuatB = 0f;
    private float attitudeQuatC = 0f;
    private float attitudeQuatD = 0f;
    private Duration attitudeMonitoringInterval = DEFAULT_MONITORING_INTERVAL;

    public MCSupervisorBasicAdapter() {
    }

    public void setNmfSupervisor(NanoSatMOSupervisor supervisor) {
        nmfSupervisor = supervisor;
    }

    @Override
    public void initialRegistrations(MCRegistration registration) {
        registration.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

        ParameterDefinitionList paramDefs = new ParameterDefinitionList();
        paramDefs.add(new ParameterDefinition(new Identifier(PARAM_OS_VERSION),
                "The version of the operating system.",
                AttributeType.STRING, "", false, new Duration(0), null, null));
        paramDefs.add(new ParameterDefinition(new Identifier(PARAM_OS_PARTITION),
                "The current partition where the OS is running. Only works for Linux.",
                AttributeType.STRING, "", false, new Duration(5), null, null));
        paramDefs.add(new ParameterDefinition(new Identifier(PARAM_ATTITUDE_QUAT_A),
                "Attitude quaternion component A.",
                AttributeType.FLOAT, "", false, new Duration(5), null, null));
        paramDefs.add(new ParameterDefinition(new Identifier(PARAM_ATTITUDE_QUAT_B),
                "Attitude quaternion component B.",
                AttributeType.FLOAT, "", false, new Duration(5), null, null));
        paramDefs.add(new ParameterDefinition(new Identifier(PARAM_ATTITUDE_QUAT_C),
                "Attitude quaternion component C.",
                AttributeType.FLOAT, "", false, new Duration(5), null, null));
        paramDefs.add(new ParameterDefinition(new Identifier(PARAM_ATTITUDE_QUAT_D),
                "Attitude quaternion component D.",
                AttributeType.FLOAT, "", false, new Duration(5), null, null));
        paramDefs.add(new ParameterDefinition(new Identifier(PARAM_ATTITUDE_MONITORING_INTERVAL),
                "The ADCS attitude monitoring interval.",
                AttributeType.DURATION, "seconds", false, new Duration(0), null, null));
        paramDefs.add(new ParameterDefinition(new Identifier(PARAM_GPS_LATITUDE),
                "The GPS latitude.",
                AttributeType.DOUBLE, "degrees", false, new Duration(2), null, null));
        paramDefs.add(new ParameterDefinition(new Identifier(PARAM_GPS_LONGITUDE),
                "The GPS longitude.",
                AttributeType.DOUBLE, "degrees", false, new Duration(2), null, null));
        paramDefs.add(new ParameterDefinition(new Identifier(PARAM_GPS_N_SATS),
                "The number of GPS satellites in view.",
                AttributeType.INTEGER, "sats", false, new Duration(4), null, null));
        paramDefs.add(new ParameterDefinition(new Identifier(PARAM_MAG_X),
                "The magnetometer X component.",
                AttributeType.DOUBLE, "microTesla", false, new Duration(2), null, null));
        paramDefs.add(new ParameterDefinition(new Identifier(PARAM_MAG_Y),
                "The magnetometer Y component.",
                AttributeType.DOUBLE, "microTesla", false, new Duration(2), null, null));
        paramDefs.add(new ParameterDefinition(new Identifier(PARAM_MAG_Z),
                "The magnetometer Z component.",
                AttributeType.DOUBLE, "microTesla", false, new Duration(2), null, null));
        registration.registerParameters(paramDefs);

        ActionDefinitionList actionDefs = new ActionDefinitionList();

        actionDefs.add(new ActionDefinition(new Identifier(ACTION_ADCS_CONFIGURE_MONITORING),
                "Applies the current attitudeMonitoringInterval to the ADCS monitoring configuration.",
                new UShort(0), new ArgumentDefinitionList()));

        ArgumentDefinitionList nmeaArgs = new ArgumentDefinitionList();
        nmeaArgs.add(new ArgumentDefinition(new Identifier("arg"), null, AttributeType.STRING, ""));
        actionDefs.add(new ActionDefinition(new Identifier(ACTION_NMEA_SENTENCE),
                "Adds <CR><LF> to a raw NMEA query and forwards it to the GNSS Provider.",
                new UShort(0), nmeaArgs));

        ArgumentDefinitionList clockArgs = new ArgumentDefinitionList();
        clockArgs.add(new ArgumentDefinition(new Identifier("delta"), null, AttributeType.LONG, "milliseconds"));
        actionDefs.add(new ActionDefinition(new Identifier(ACTION_CLOCK_SET_TIME),
                "Sets the clock using a diff between the on-board time and the desired time.",
                new UShort(0), clockArgs));

        actionDefs.add(new ActionDefinition(new Identifier(ACTION_ADCS_SUN_POINTING),
                "Sets the ADCS to sun pointing mode.",
                new UShort(0), new ArgumentDefinitionList()));

        ArgumentDefinitionList nadirArgs = new ArgumentDefinitionList();
        nadirArgs.add(new ArgumentDefinition(new Identifier("duration"), null, AttributeType.DURATION, "seconds"));
        actionDefs.add(new ActionDefinition(new Identifier(ACTION_ADCS_NADIR_POINTING),
                "Sets the ADCS to nadir pointing mode for the given duration.",
                new UShort(0), nadirArgs));

        actionDefs.add(new ActionDefinition(new Identifier(ACTION_ADCS_UNSET_ATTITUDE),
                "Unsets the ADCS desired attitude.",
                new UShort(0), new ArgumentDefinitionList()));

        registration.registerActions(actionDefs);
    }

    @Override
    public Attribute onGetValue(Identifier identifier, AttributeType rawType) throws IOException {
        if (identifier == null) {
            return null;
        }
        switch (identifier.getValue()) {
            case PARAM_OS_VERSION: {
                String cmd = osValidator.isWindows() ? CMD_WINDOWS_VERSION : CMD_LINUX_VERSION;
                return (Attribute) Attribute.javaType2Attribute(shellCommander.runCommandAndGetOutputMessage(cmd));
            }
            case PARAM_OS_PARTITION: {
                if (osValidator.isUnix()) {
                    return (Attribute) Attribute.javaType2Attribute(
                            shellCommander.runCommandAndGetOutputMessage(CMD_CURRENT_PARTITION));
                }
                return (Attribute) Attribute.javaType2Attribute("");
            }
            case PARAM_ATTITUDE_QUAT_A:
                return (Attribute) Attribute.javaType2Attribute(attitudeQuatA);
            case PARAM_ATTITUDE_QUAT_B:
                return (Attribute) Attribute.javaType2Attribute(attitudeQuatB);
            case PARAM_ATTITUDE_QUAT_C:
                return (Attribute) Attribute.javaType2Attribute(attitudeQuatC);
            case PARAM_ATTITUDE_QUAT_D:
                return (Attribute) Attribute.javaType2Attribute(attitudeQuatD);
            case PARAM_ATTITUDE_MONITORING_INTERVAL:
                return attitudeMonitoringInterval;
            case PARAM_MAG_X:
            case PARAM_MAG_Y:
            case PARAM_MAG_Z: {
                try {
                    GetStatusResponse adcsStatus = nmfSupervisor.getPlatformServices()
                            .getAutonomousADCSService().getStatus();
                    VectorF3D magField = adcsStatus.getAttitudeTelemetry().getMagneticField();
                    if (PARAM_MAG_X.equals(identifier.getValue())) {
                        return (Attribute) Attribute.javaType2Attribute(magField.getX());
                    }
                    if (PARAM_MAG_Y.equals(identifier.getValue())) {
                        return (Attribute) Attribute.javaType2Attribute(magField.getY());
                    }
                    return (Attribute) Attribute.javaType2Attribute(magField.getZ());
                } catch (MALInteractionException | MALException | NMFException ex) {
                    LOGGER.log(Level.SEVERE, "Error reading magnetometer data.", ex);
                    return null;
                }
            }
            case PARAM_GPS_N_SATS: {
                final Semaphore sem = new Semaphore(0);
                final IntegerList nOfSats = new IntegerList();
                try {
                    nmfSupervisor.getPlatformServices().getGPSService().getSatellitesInfo(
                            new GPSAdapter() {
                        @Override
                        public void getSatellitesInfoResponseReceived(
                                final MALMessageHeader msgHeader,
                                final SatelliteInfoList gpsSatellitesInfo,
                                final Map qosProperties) {
                            nOfSats.add(gpsSatellitesInfo.size());
                            sem.release();
                        }
                    });
                } catch (MALInteractionException | MALException | NMFException ex) {
                    LOGGER.log(Level.SEVERE, "Error requesting GPS satellites info.", ex);
                    return null;
                }
                try {
                    sem.acquire();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return null;
                }
                return (Attribute) Attribute.javaType2Attribute(nOfSats.get(0));
            }
            case PARAM_GPS_LATITUDE:
            case PARAM_GPS_LONGITUDE: {
                try {
                    var pos = nmfSupervisor.getPlatformServices().getGPSService().getLastKnownPosition();
                    if (PARAM_GPS_LATITUDE.equals(identifier.getValue())) {
                        return (Attribute) Attribute.javaType2Attribute(pos.getPosition().getLatitude());
                    }
                    return (Attribute) Attribute.javaType2Attribute(pos.getPosition().getLongitude());
                } catch (MALInteractionException | MALException | NMFException ex) {
                    LOGGER.log(Level.SEVERE, "Error reading GPS position.", ex);
                    return null;
                }
            }
            default:
                return null;
        }
    }

    @Override
    public boolean isReadOnly(Identifier name) {
        return !PARAM_ATTITUDE_MONITORING_INTERVAL.equals(name.getValue());
    }

    @Override
    public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
        for (int i = 0; i < identifiers.size(); i++) {
            if (PARAM_ATTITUDE_MONITORING_INTERVAL.equals(identifiers.get(i).getValue())) {
                Object val = HelperAttributes.attribute2JavaType(values.get(i).getRawValue());
                if (val instanceof Duration) {
                    attitudeMonitoringInterval = (Duration) val;
                }
            }
        }
        return true;
    }

    @Override
    public void actionArrived(Identifier name, AttributeValueList attributeValues,
            Long executionId, MALInteraction interaction) throws ExecutionFailedException {
        if (name == null || name.getValue() == null) {
            throw new ExecutionFailedException("Action name is null");
        }
        switch (name.getValue()) {
            case ACTION_ADCS_CONFIGURE_MONITORING:
                configureMonitoringAction();
                break;
            case ACTION_NMEA_SENTENCE:
                nmeaAction((String) HelperAttributes.attribute2JavaType(attributeValues.get(0).getValue()));
                break;
            case ACTION_CLOCK_SET_TIME:
                setTimeUsingDeltaMilliseconds((Long) HelperAttributes.attribute2JavaType(attributeValues.get(0).getValue()));
                break;
            case ACTION_ADCS_SUN_POINTING:
                adcsSunPointing();
                break;
            case ACTION_ADCS_NADIR_POINTING:
                adcsNadirPointing((Duration) HelperAttributes.attribute2JavaType(attributeValues.get(0).getValue()));
                break;
            case ACTION_ADCS_UNSET_ATTITUDE:
                adcsUnsetAttitude();
                break;
            default:
                throw new ExecutionFailedException("Unknown action: " + name.getValue());
        }
    }

    public void startAdcsAttitudeMonitoring() {
        try {
            nmfSupervisor.getPlatformServices().getAutonomousADCSService().monitorAttitudeRegister(
                    ConnectionConsumer.subscriptionWildcardRandom(), new ADCSDataHandler());
            configureMonitoring();
        } catch (IOException | MALInteractionException | MALException | NMFException ex) {
            LOGGER.log(Level.SEVERE, "Error when setting up attitude monitoring.", ex);
        }
    }

    public class ADCSDataHandler extends AutonomousADCSAdapter {

        @Override
        public void monitorAttitudeNotifyReceived(final MALMessageHeader msgHeader,
                final Identifier lIdentifier, final UpdateHeader lUpdateHeader,
                AttitudeTelemetry attitudeTm, ActuatorsTelemetry actuatorsTelemetry,
                Duration controlDuration, AttitudeMode attitudeMode, final Map qosp) {
            LOGGER.log(Level.INFO, "Received monitorAttitude notify");
            Quaternion attitude = attitudeTm.getAttitude();
            attitudeQuatA = attitude.getA();
            attitudeQuatB = attitude.getB();
            attitudeQuatC = attitude.getC();
            attitudeQuatD = attitude.getD();
            try {
                nmfSupervisor.pushParameterValue(PARAM_ATTITUDE_QUAT_A, attitudeQuatA);
                nmfSupervisor.pushParameterValue(PARAM_ATTITUDE_QUAT_B, attitudeQuatB);
                nmfSupervisor.pushParameterValue(PARAM_ATTITUDE_QUAT_C, attitudeQuatC);
                nmfSupervisor.pushParameterValue(PARAM_ATTITUDE_QUAT_D, attitudeQuatD);
            } catch (NMFException ex) {
                LOGGER.log(Level.SEVERE, "Error pushing ADCS attitude values.", ex);
            }
        }
    }

    private void configureMonitoring() throws IOException, MALInteractionException, MALException, NMFException {
        if (attitudeMonitoringInterval.getInSeconds() >= 0.1) {
            nmfSupervisor.getPlatformServices().getAutonomousADCSService().enableMonitoring(
                    true, attitudeMonitoringInterval);
        } else {
            nmfSupervisor.getPlatformServices().getAutonomousADCSService().enableMonitoring(
                    false, attitudeMonitoringInterval);
        }
    }

    private UInteger configureMonitoringAction() {
        try {
            configureMonitoring();
        } catch (IOException | MALInteractionException | MALException | NMFException ex) {
            LOGGER.log(Level.SEVERE, "Error configuring attitude monitoring.", ex);
            return new UInteger(1);
        }
        return null;
    }

    private UInteger nmeaAction(String arg) {
        try {
            arg = arg + "\r\n";
            nmfSupervisor.getPlatformServices().getGPSService().getNMEASentence(arg, new GPSConsumerAdapter());
        } catch (MALInteractionException | MALException | IOException | NMFException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return new UInteger(1);
        }
        return null;
    }

    private UInteger setTimeUsingDeltaMilliseconds(Long delta) {
        String dateStr = new SimpleDateFormat(DATE_PATTERN, Locale.US).format(
                new Date(System.currentTimeMillis() + delta)) + " UTC";
        try {
            new ProcessBuilder("date", "-s", dateStr).inheritIO().start().waitFor();
            new ProcessBuilder("hwclock", "--systohc").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException ex) {
            LOGGER.log(Level.SEVERE, "Error setting system time.", ex);
            return new UInteger(1);
        }
        return null;
    }

    private UInteger adcsSunPointing() {
        try {
            nmfSupervisor.getPlatformServices().getAutonomousADCSService().setDesiredAttitude(
                    null, new AttitudeModeSunPointing());
        } catch (MALInteractionException | MALException | IOException | NMFException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return new UInteger(1);
        }
        return null;
    }

    private UInteger adcsNadirPointing(Duration duration) {
        try {
            nmfSupervisor.getPlatformServices().getAutonomousADCSService().setDesiredAttitude(
                    duration, new AttitudeModeNadirPointing());
        } catch (MALInteractionException | MALException | IOException | NMFException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return new UInteger(1);
        }
        return null;
    }

    private UInteger adcsUnsetAttitude() {
        try {
            nmfSupervisor.getPlatformServices().getAutonomousADCSService().setDesiredAttitude(
                    new Duration(0), null);
        } catch (MALInteractionException | MALException | IOException | NMFException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return new UInteger(1);
        }
        return null;
    }

    private static class GPSConsumerAdapter extends GPSAdapter {

        @Override
        public void getNMEASentenceResponseErrorReceived(MALMessageHeader msgHeader,
                MOErrorException error, Map qosProperties) {
            LOGGER.log(Level.WARNING, "Received response error");
        }

        @Override
        public void getNMEASentenceAckErrorReceived(MALMessageHeader msgHeader,
                MOErrorException error, Map qosProperties) {
            LOGGER.log(Level.WARNING, "Received ACK error");
        }

        @Override
        public void getNMEASentenceResponseReceived(MALMessageHeader msgHeader,
                String sentence, Map qosProperties) {
            LOGGER.log(Level.INFO, "Received message " + sentence);
        }

        @Override
        public void getNMEASentenceAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
            LOGGER.log(Level.INFO, "Received ack");
        }
    }
}
