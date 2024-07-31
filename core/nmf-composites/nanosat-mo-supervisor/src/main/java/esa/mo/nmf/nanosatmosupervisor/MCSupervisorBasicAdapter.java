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
package esa.mo.nmf.nanosatmosupervisor;

import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.misc.OSValidator;
import esa.mo.helpertools.misc.ShellCommander;
import esa.mo.nmf.annotations.Action;
import esa.mo.nmf.annotations.ActionParameter;
import esa.mo.nmf.annotations.Parameter;
import esa.mo.nmf.nanosatmosupervisor.parameter.OBSWParameterManager;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import jakarta.xml.bind.JAXBException;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValue;
import org.ccsds.moims.mo.platform.gps.consumer.GPSAdapter;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.annotations.Action;
import esa.mo.nmf.annotations.ActionParameter;
import esa.mo.nmf.annotations.Parameter;
import esa.mo.nmf.nanosatmosupervisor.parameter.OBSWParameterManager;
import esa.mo.helpertools.misc.OSValidator;
import esa.mo.helpertools.misc.ShellCommander;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.platform.autonomousadcs.consumer.AutonomousADCSAdapter;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeSunPointing;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeTelemetry;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion;
import org.ccsds.moims.mo.platform.gps.consumer.GPSAdapter;

/**
 *
 * @author yannick
 */
public class MCSupervisorBasicAdapter extends MonitorAndControlNMFAdapter {

    private static final Logger LOGGER = Logger.getLogger(MCSupervisorBasicAdapter.class.getName());

    private final static String DATE_PATTERN = "dd MMM yyyy HH:mm:ss.SSS";
    private static final String CMD_CURRENT_PARTITION = "mount -l | grep \"on / \" | grep -o 'mmc.*[0-9]p[0-9]'";
    private static final String CMD_LINUX_VERSION = "uname -a";
    private static final String CMD_WINDOWS_VERSION = "systeminfo | findstr Version";

    private final ShellCommander shellCommander = new ShellCommander();
    private NanoSatMOSupervisor nmfSupervisor;
    private final OSValidator osValidator = new OSValidator();

    @Parameter(description = "The version of the operating system.", generationEnabled = false,
               onGetFunction = "onGetOSVersion", readOnly = true, reportIntervalSeconds = 10)
    public String OSVersion = "";

    @Parameter(description = "The Current partition where the OS is running. Only works for linux",
               generationEnabled = false, onGetFunction = "onGetOSPartition", readOnly = true,
               reportIntervalSeconds = 10)
    public String OSPartition = "";

    @Parameter(generationEnabled = false, readOnly = true, reportIntervalSeconds = 10)
    public float attitudeQuatA;
    @Parameter(generationEnabled = false, readOnly = true, reportIntervalSeconds = 10)
    public float attitudeQuatB;
    @Parameter(generationEnabled = false, readOnly = true, reportIntervalSeconds = 10)
    public float attitudeQuatC;
    @Parameter(generationEnabled = false, readOnly = true, reportIntervalSeconds = 10)
    public float attitudeQuatD;

    @Parameter(generationEnabled = false, readOnly = false, reportIntervalSeconds = 10)
    public Duration attitudeMonitoringInterval = new Duration(0.0);

    /**
     * Manages the OBSW parameter provisioning
     */
    private OBSWParameterManager obswParameterManager;

    public MCSupervisorBasicAdapter() {
    }

    public void setNmfSupervisor(NanoSatMOSupervisor supervisor) {
        nmfSupervisor = supervisor;
    }

    @Override
    public void initialRegistrations(MCRegistration registrationObject) {
        super.initialRegistrations(registrationObject);

        if (registrationObject == null) {
            return;
        }

        /* OBSW PARAMETERS PROXIES */
        try {
            obswParameterManager = new OBSWParameterManager(getClass().getClassLoader().getResourceAsStream(
                "Datapool.xml"));
            obswParameterManager.registerParametersProxies(registrationObject);
        } catch (IOException | JAXBException | XMLStreamException e) {
            LOGGER.log(Level.SEVERE, "Couldn't register OBSW parameters proxies", e);
        }
    }

    @Override
    public Attribute onGetValue(Long parameterID) throws IOException {
        // see if id matches one of the OBSW parameter proxies
        if (obswParameterManager != null) {
            if (obswParameterManager.isOBSWParameterProxy(parameterID)) {
                return obswParameterManager.getValue(parameterID);
            }
        }

        // otherwise it's one of the annotated internal parameters
        return super.onGetValue(parameterID);
    }

    @Override
    public Boolean onSetValue(ParameterRawValue newRawValue) {
        boolean result = super.onSetValue(newRawValue);
        if (!result) {
            result = obswParameterManager.setValue(newRawValue);
        }

        return result;
    }

    public void startAdcsAttitudeMonitoring() {
        try {
            // Subscribe monitorAttitude
            nmfSupervisor.getPlatformServices().getAutonomousADCSService().monitorAttitudeRegister(ConnectionConsumer
                .subscriptionWildcard(), new ADCSDataHandler());
            configureMonitoring();
        } catch (IOException | MALInteractionException | MALException | NMFException ex) {
            LOGGER.log(Level.SEVERE, "Error when setting up attitude monitoring.", ex);
        }
    }

    public class ADCSDataHandler extends AutonomousADCSAdapter {
        @Override
        public void monitorAttitudeNotifyReceived(final MALMessageHeader msgHeader, final Identifier lIdentifier,
            final UpdateHeaderList lUpdateHeaderList,
            org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeTelemetryList attitudeTelemetryList,
            org.ccsds.moims.mo.platform.autonomousadcs.structures.ActuatorsTelemetryList actuatorsTelemetryList,
            org.ccsds.moims.mo.mal.structures.DurationList controlDurationList,
            org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeList attitudeModeList, final Map qosp) {
            LOGGER.log(Level.FINE, "Received monitorAttitude notify");
            for (AttitudeTelemetry attitudeTm : attitudeTelemetryList) {
                Quaternion attitude = attitudeTm.getAttitude();
                attitudeQuatA = attitude.getA();
                attitudeQuatB = attitude.getB();
                attitudeQuatC = attitude.getC();
                attitudeQuatD = attitude.getD();
            }
        }
    }

    public void onGetOSVersion() {
        if (osValidator.isWindows()) {
            OSVersion = shellCommander.runCommandAndGetOutputMessage(CMD_WINDOWS_VERSION);
        } else {
            OSVersion = shellCommander.runCommandAndGetOutputMessage(CMD_LINUX_VERSION);
        }
    }

    public void onGetOSPartition() {
        if (osValidator.isUnix()) {
            OSPartition = shellCommander.runCommandAndGetOutputMessage(CMD_CURRENT_PARTITION);
        }
    }

    private void configureMonitoring() throws IOException, MALInteractionException, MALException, NMFException {
        if (attitudeMonitoringInterval.getValue() >= 0.1) {
            nmfSupervisor.getPlatformServices().getAutonomousADCSService().enableMonitoring(true,
                attitudeMonitoringInterval);
        } else {
            nmfSupervisor.getPlatformServices().getAutonomousADCSService().enableMonitoring(false,
                attitudeMonitoringInterval);
        }
    }

    @Action(name = "ADCS.configureMonitoring")
    public UInteger configureMonitoringAction(Long actionInstanceObjId, boolean reportProgress,
        MALInteraction interaction) {
        try {
            configureMonitoring();
        } catch (IOException | MALInteractionException | MALException | NMFException ex) {
            LOGGER.log(Level.SEVERE, "Error when setting up attitude monitoring.", ex);
            return new UInteger(1);
        }
        return null;
    }

    @Action(name = "NMEA_Sentence",
            description = "Adds <CR><LF> to a raw NMEA query and forwards it to the GNSS Provider")
    public UInteger nmeaAction(Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction,
        @ActionParameter(name = "arg") String arg) {
        try {
            arg = arg + "\r\n";
            nmfSupervisor.getPlatformServices().getGPSService().getNMEASentence(arg, new GPSConsumerAdapter());
        } catch (MALInteractionException | MALException | IOException | NMFException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return new UInteger(1);
        }
        return null;
    }

    @Action(name = "Clock.setTimeUsingDeltaMilliseconds",
            description = "Sets the clock using a diff between the on-board time and the desired time.")
    public UInteger setTimeUsingDeltaMilliseconds(Long actionInstanceObjId, boolean reportProgress,
        MALInteraction interaction, @ActionParameter(name = "delta", rawUnit = "milliseconds") Long delta) {
        String str = (new SimpleDateFormat(DATE_PATTERN)).format(new Date(System.currentTimeMillis() + delta));

        ShellCommander shell = new ShellCommander();
        shell.runCommand("date -s \"" + str + " UTC\" | hwclock --systohc");
        return null;
    }

    @Action(name = "ADCS.sunpointing")
    public UInteger adcsSunPointing(Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {

        try {
            nmfSupervisor.getPlatformServices().getAutonomousADCSService().setDesiredAttitude(null,
                new AttitudeModeSunPointing());
        } catch (MALInteractionException | MALException | IOException | NMFException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return new UInteger(1);
        }
        return null;
    }

    @Action(name = "ADCS.nadirPointing")
    public UInteger adcsNadirPointing(Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction,
        @ActionParameter(name = "duration") Duration duration) {

        try {
            nmfSupervisor.getPlatformServices().getAutonomousADCSService().setDesiredAttitude(duration,
                new AttitudeModeSunPointing());
        } catch (MALInteractionException | MALException | IOException | NMFException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return new UInteger(1);
        }
        return null;
    }

    @Action(name = "ADCS.unsetAttitude")
    public UInteger adcsUnsetAttitude(Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {

        try {
            nmfSupervisor.getPlatformServices().getAutonomousADCSService().setDesiredAttitude(new Duration(0), null);
        } catch (MALInteractionException | MALException | IOException | NMFException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return new UInteger(1);
        }
        return null;
    }

    private static class GPSConsumerAdapter extends GPSAdapter {

        @Override
        public void getNMEASentenceResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
            Map qosProperties) {
            LOGGER.log(Level.WARNING, "Received response error");
        }

        @Override
        public void getNMEASentenceAckErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
            Map qosProperties) {
            LOGGER.log(Level.WARNING, "Received ACK error");
        }

        @Override
        public void getNMEASentenceResponseReceived(MALMessageHeader msgHeader, String sentence, Map qosProperties) {
            LOGGER.log(Level.INFO, "Received message " + sentence);
        }

        @Override
        public void getNMEASentenceAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
            LOGGER.log(Level.INFO, "Received ack");
        }

    }

}
