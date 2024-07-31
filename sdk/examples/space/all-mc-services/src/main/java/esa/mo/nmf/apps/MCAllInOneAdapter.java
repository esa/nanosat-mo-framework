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
package esa.mo.nmf.apps;

import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.helpertools.misc.TaskScheduler;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MCRegistration.RegistrationMode;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.NMFInterface;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.IntegerList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Pair;
import org.ccsds.moims.mo.mal.structures.PairList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationCategory;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetails;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetailsList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSet;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSetList;
import org.ccsds.moims.mo.mc.conversion.structures.DiscreteConversionDetails;
import org.ccsds.moims.mo.mc.conversion.structures.DiscreteConversionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterConversion;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValueList;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.AttributeValue;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ConditionalConversion;
import org.ccsds.moims.mo.mc.structures.ConditionalConversionList;
import org.ccsds.moims.mo.mc.structures.ParameterExpression;
import org.ccsds.moims.mo.platform.autonomousadcs.body.GetStatusResponse;
import org.ccsds.moims.mo.platform.autonomousadcs.consumer.AutonomousADCSAdapter;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.ActuatorsTelemetry;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeMode;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeBDot;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeNadirPointing;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeSingleSpinning;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeSunPointing;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeTargetTracking;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeTelemetry;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion;
import org.ccsds.moims.mo.platform.structures.VectorF3D;
import org.ccsds.moims.mo.platform.gps.body.GetLastKnownPositionResponse;
import org.ccsds.moims.mo.platform.gps.consumer.GPSAdapter;
import org.ccsds.moims.mo.platform.gps.structures.SatelliteInfoList;

/**
 * The adapter for the NMF App
 */
public class MCAllInOneAdapter extends MonitorAndControlNMFAdapter {

    private static final String ACTION_5_STAGES = "5StagesAction";
    private static final String ACTION_NADIR_POINTING_MODE = "ADCS_NadirPointingMode";
    private static final String ACTION_SUN_POINTING_MODE = "ADCS_SunPointingMode";
    private static final String ACTION_UNSET = "ADCS_UnsetAttitude";
    private static final String AGGREGATION_GPS = "GPS_Aggregation";
    private static final String AGGREGATION_MAG = "Magnetometer_Aggregation";
    private static final String PARAMETER_ADCS_MODE = "ADCS_ModeOperation";
    private static final String PARAMETER_ADCS_DURATION = "ADCS_RemainingControlDuration";
    private static final String PARAMETER_ANGULAR_VELOCITY_X = "AngularVelocity_X";
    private static final String PARAMETER_ANGULAR_VELOCITY_Y = "AngularVelocity_Y";
    private static final String PARAMETER_ANGULAR_VELOCITY_Z = "AngularVelocity_Z";
    private static final String PARAMETER_ATTITUDE_Q_A = "AttitudeQuaternion_a";
    private static final String PARAMETER_ATTITUDE_Q_B = "AttitudeQuaternion_b";
    private static final String PARAMETER_ATTITUDE_Q_C = "AttitudeQuaternion_c";
    private static final String PARAMETER_ATTITUDE_Q_D = "AttitudeQuaternion_d";
    private static final String PARAMETER_GPS_ALTITUDE = "GPS_Altitude";
    private static final String PARAMETER_GPS_ELAPSED_TIME = "GPS_ElapsedTime";
    private static final String PARAMETER_GPS_LATITUDE = "GPS_Latitude";
    private static final String PARAMETER_GPS_LONGITUDE = "GPS_Longitude";
    private static final String PARAMETER_GPS_N_SATS_IN_VIEW = "GPS_NumberOfSatellitesInView";
    private static final String PARAMETER_MAG_X = "MagneticField_X";
    private static final String PARAMETER_MAG_Y = "MagneticField_Y";
    private static final String PARAMETER_MAG_Z = "MagneticField_Z";
    private static final String PARAMETER_MTQ_X = "MagnetorquerMoment_X";
    private static final String PARAMETER_MTQ_Y = "MagnetorquerMoment_Y";
    private static final String PARAMETER_MTQ_Z = "MagnetorquerMoment_Z";
    private static final String PARAMETER_SUN_VECTOR_X = "SunVector_X";
    private static final String PARAMETER_SUN_VECTOR_Y = "SunVector_Y";
    private static final String PARAMETER_SUN_VECTOR_Z = "SunVector_Z";

    private static final Duration ATTITUDE_MONITORING_INTERVAL = new Duration(1.0);
    private static final Logger LOGGER = Logger.getLogger(MCAllInOneAdapter.class.getName());
    private NMFInterface nmf;

    private final TaskScheduler periodicAlertTimer = new TaskScheduler(1);

    public MCAllInOneAdapter(final NMFInterface nmfProvider) {
        this.nmf = nmfProvider;
    }

    private enum AttitudeModeEnum {
        IDLE, BDOT, SUNPOINTING, SINGLESPINNING, TARGETTRACKING, NADIRPOINTING
    }

    private UOctet attitudeModeToParamValue(AttitudeMode attitude) {
        AttitudeModeEnum modeEnum;
        if (attitude == null) {
            modeEnum = AttitudeModeEnum.IDLE;
        } else if (attitude instanceof AttitudeModeBDot) {
            modeEnum = AttitudeModeEnum.BDOT;
        } else if (attitude instanceof AttitudeModeSunPointing) {
            modeEnum = AttitudeModeEnum.SUNPOINTING;
        } else if (attitude instanceof AttitudeModeSingleSpinning) {
            modeEnum = AttitudeModeEnum.SINGLESPINNING;
        } else if (attitude instanceof AttitudeModeTargetTracking) {
            modeEnum = AttitudeModeEnum.TARGETTRACKING;
        } else if (attitude instanceof AttitudeModeNadirPointing) {
            modeEnum = AttitudeModeEnum.NADIRPOINTING;
        } else {
            throw new IllegalArgumentException("Unrecognized attitude mode type!");
        }
        return new UOctet((short) modeEnum.ordinal());
    }

    public void startPeriodicAlertsPublishing() {
        this.periodicAlertTimer.scheduleTask(new Thread(() -> {
            AttributeValueList atts = new AttributeValueList();
            AttributeValue att = new AttributeValue(new Union("This is an Alert!"));
            atts.add(att);

            try {
                nmf.publishAlertEvent("10SecondsAlert", atts);
            } catch (NMFException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }), 0, 10, TimeUnit.SECONDS, true); // 10 seconds
    }

    @Override
    public void initialRegistrations(MCRegistration registration) {
        registration.setMode(RegistrationMode.DONT_UPDATE_IF_EXISTS);

        // ===================================================================
        PairList mappings = new PairList();
        mappings.add(new Pair(new UOctet((short) AttitudeModeEnum.IDLE.ordinal()), new Union("IDLE")));
        mappings.add(new Pair(new UOctet((short) AttitudeModeEnum.BDOT.ordinal()), new Union("BDOT")));
        mappings.add(new Pair(new UOctet((short) AttitudeModeEnum.SUNPOINTING.ordinal()), new Union("SUNPOINTING")));
        mappings.add(new Pair(new UOctet((short) AttitudeModeEnum.SINGLESPINNING.ordinal()), new Union(
            "SINGLESPINNING")));
        mappings.add(new Pair(new UOctet((short) AttitudeModeEnum.TARGETTRACKING.ordinal()), new Union(
            "TARGETTRACKING")));
        mappings.add(new Pair(new UOctet((short) AttitudeModeEnum.NADIRPOINTING.ordinal()), new Union(
            "NADIRPOINTING")));

        DiscreteConversionDetailsList conversions = new DiscreteConversionDetailsList();
        conversions.add(new DiscreteConversionDetails(mappings));
        ParameterConversion paramConversion = null;

        try {
            ObjectIdList objIds = registration.registerConversions(conversions);

            if (objIds.size() == 1) {
                ObjectId objId = objIds.get(0);
                ParameterExpression paramExpr = null;

                ConditionalConversion condition = new ConditionalConversion(paramExpr, objId.getKey());
                ConditionalConversionList conditionalConversions = new ConditionalConversionList();
                conditionalConversions.add(condition);

                Byte convertedType = Attribute.STRING_TYPE_SHORT_FORM.byteValue();
                String convertedUnit = "n/a";

                paramConversion = new ParameterConversion(convertedType, convertedUnit, conditionalConversions);
            }
        } catch (NMFException | MALException | MALInteractionException ex) {
            Logger.getLogger(MCAllInOneAdapter.class.getName()).log(Level.SEVERE, "Failed to register conversion.", ex);
        }

        // ------------------ Parameters ------------------
        ParameterDefinitionDetailsList defsOther = new ParameterDefinitionDetailsList();
        IdentifierList paramOtherNames = new IdentifierList();
        ParameterDefinitionDetailsList defsGPS = new ParameterDefinitionDetailsList();
        IdentifierList paramGPSNames = new IdentifierList();
        ParameterDefinitionDetailsList defsMag = new ParameterDefinitionDetailsList();
        IdentifierList paramMagNames = new IdentifierList();

        defsOther.add(new ParameterDefinitionDetails("The ADCS mode of operation", Union.UOCTET_SHORT_FORM.byteValue(),
            "", false, new Duration(0), null, paramConversion));
        paramOtherNames.add(new Identifier(PARAMETER_ADCS_MODE));

        defsOther.add(new ParameterDefinitionDetails("The number of satellites in view of GPS receiver.",
            Union.INTEGER_SHORT_FORM.byteValue(), "sats", false, new Duration(4), null, null));
        paramOtherNames.add(new Identifier(PARAMETER_GPS_N_SATS_IN_VIEW));

        // Create the GPS.Latitude
        defsGPS.add(new ParameterDefinitionDetails("The GPS Latitude", Union.DOUBLE_TYPE_SHORT_FORM.byteValue(),
            "degrees", false, new Duration(2), null, null));
        paramGPSNames.add(new Identifier(PARAMETER_GPS_LATITUDE));

        // Create the GPS.Longitude
        defsGPS.add(new ParameterDefinitionDetails("The GPS Longitude", Union.DOUBLE_TYPE_SHORT_FORM.byteValue(),
            "degrees", false, new Duration(2), null, null));
        paramGPSNames.add(new Identifier(PARAMETER_GPS_LONGITUDE));

        // Create the GPS.Altitude
        defsGPS.add(new ParameterDefinitionDetails("The GPS Altitude", Union.DOUBLE_TYPE_SHORT_FORM.byteValue(),
            "meters", false, new Duration(2), null, null));
        paramGPSNames.add(new Identifier(PARAMETER_GPS_ALTITUDE));

        // Create the Magnetometer.X
        defsMag.add(new ParameterDefinitionDetails("The Magnetometer X component", Union.DOUBLE_TYPE_SHORT_FORM
            .byteValue(), "microTesla", false, new Duration(2), null, null));
        paramMagNames.add(new Identifier(PARAMETER_MAG_X));

        // Create the Magnetometer.Y
        defsMag.add(new ParameterDefinitionDetails("The Magnetometer Y component", Union.DOUBLE_TYPE_SHORT_FORM
            .byteValue(), "microTesla", false, new Duration(2), null, null));
        paramMagNames.add(new Identifier(PARAMETER_MAG_Y));

        // Create the Magnetometer.Z
        defsMag.add(new ParameterDefinitionDetails("The Magnetometer Z component", Union.DOUBLE_TYPE_SHORT_FORM
            .byteValue(), "microTesla", false, new Duration(2), null, null));
        paramMagNames.add(new Identifier(PARAMETER_MAG_Z));

        registration.registerParameters(paramOtherNames, defsOther);
        LongList parameterObjIdsGPS = registration.registerParameters(paramGPSNames, defsGPS);
        LongList parameterObjIdsMag = registration.registerParameters(paramMagNames, defsMag);

        // ------------------ Aggregations ------------------
        AggregationDefinitionDetailsList aggs = new AggregationDefinitionDetailsList();
        IdentifierList aggNames = new IdentifierList();

        // Create the Aggregation GPS
        AggregationDefinitionDetails defGPSAgg = new AggregationDefinitionDetails(
            "Aggregates: GPS Latitude, GPS Longitude, GPS Altitude.", new UOctet((short) AggregationCategory.GENERAL
                .getOrdinal()), new Duration(10), true, false, false, new Duration(20), false,
            new AggregationParameterSetList());
        aggNames.add(new Identifier(AGGREGATION_GPS));

        defGPSAgg.getParameterSets().add(new AggregationParameterSet(null, parameterObjIdsGPS, new Duration(3), null));

        // Create the Aggregation Magnetometer
        AggregationDefinitionDetails defMagAgg = new AggregationDefinitionDetails(
            "Aggregates Magnetometer components: X, Y, Z.", new UOctet((short) AggregationCategory.GENERAL
                .getOrdinal()), new Duration(10), true, false, false, new Duration(20), false,
            new AggregationParameterSetList());
        aggNames.add(new Identifier(AGGREGATION_MAG));

        defMagAgg.getParameterSets().add(new AggregationParameterSet(null, parameterObjIdsMag, new Duration(3), null));

        aggs.add(defGPSAgg);
        aggs.add(defMagAgg);
        registration.registerAggregations(aggNames, aggs);

        // ------------------ Actions ------------------
        ActionDefinitionDetailsList actionDefs = new ActionDefinitionDetailsList();
        IdentifierList actionNames = new IdentifierList();

        ArgumentDefinitionDetailsList arguments1 = new ArgumentDefinitionDetailsList();
        {
            Byte rawType = Attribute._DURATION_TYPE_SHORT_FORM;
            String rawUnit = "seconds";
            ConditionalConversionList conditionalConversions = null;
            Byte convertedType = null;
            String convertedUnit = null;

            arguments1.add(new ArgumentDefinitionDetails(new Identifier("0"), null, rawType, rawUnit,
                conditionalConversions, convertedType, convertedUnit));
        }

        ActionDefinitionDetails actionDef1 = new ActionDefinitionDetails(
            "Changes the spacecraft's attitude to sun pointing mode.", new UOctet((short) 0), new UShort(0),
            arguments1);
        actionNames.add(new Identifier(ACTION_SUN_POINTING_MODE));

        ActionDefinitionDetails actionDef2 = new ActionDefinitionDetails(
            "Changes the spacecraft's attitude to nadir pointing mode.", new UOctet((short) 0), new UShort(0),
            arguments1);
        actionNames.add(new Identifier(ACTION_NADIR_POINTING_MODE));

        ActionDefinitionDetails actionDef3 = new ActionDefinitionDetails("Unsets the spacecraft's attitude.",
            new UOctet((short) 0), new UShort(0), new ArgumentDefinitionDetailsList());
        actionNames.add(new Identifier(ACTION_UNSET));

        ActionDefinitionDetails actionDef4 = new ActionDefinitionDetails("Example of an Action with 5 stages.",
            new UOctet((short) 0), new UShort(5), new ArgumentDefinitionDetailsList());
        actionNames.add(new Identifier(ACTION_5_STAGES));

        actionDefs.add(actionDef1);
        actionDefs.add(actionDef2);
        actionDefs.add(actionDef3);
        actionDefs.add(actionDef4);
        registration.registerActions(actionNames, actionDefs);
    }

    @Override
    public Attribute onGetValue(Identifier identifier, Byte rawType) throws IOException {
        // Translates NMF core calls for parameter values into calls to the underlying HW
        // exposed as Platform services
        // TODO: Optimise the number of calls through a cache
        if (nmf == null) {
            return null;
        }

        if (identifier == null) {
            LOGGER.log(Level.SEVERE, "The identifier object is null! Something is wrong!");
            return null;
        }

        try {
            if (null != identifier.getValue()) {
                switch (identifier.getValue()) {
                    case PARAMETER_GPS_N_SATS_IN_VIEW:
                        final Semaphore sem = new Semaphore(0);
                        final IntegerList nOfSats = new IntegerList();

                        class AdapterImpl extends GPSAdapter {

                            @Override
                            public void getSatellitesInfoResponseReceived(MALMessageHeader msgHeader,
                                SatelliteInfoList gpsSatellitesInfo, java.util.Map qosProperties) {
                                nOfSats.add(gpsSatellitesInfo.size());
                                sem.release();
                            }
                        }

                        try {
                            nmf.getPlatformServices().getGPSService().getSatellitesInfo(new AdapterImpl());
                        } catch (NMFException ex) {
                            throw new IOException(ex);
                        }

                        try {
                            sem.acquire();
                        } catch (InterruptedException ex) {
                            LOGGER.log(Level.SEVERE, null, ex);
                        }

                        return (Attribute) HelperAttributes.javaType2Attribute(nOfSats.get(0));
                    case PARAMETER_GPS_LATITUDE:
                    case PARAMETER_GPS_LONGITUDE:
                    case PARAMETER_GPS_ALTITUDE:
                    case PARAMETER_GPS_ELAPSED_TIME:
                        GetLastKnownPositionResponse pos;
                        try {
                            pos = nmf.getPlatformServices().getGPSService().getLastKnownPosition();

                            if (PARAMETER_GPS_LATITUDE.equals(identifier.getValue())) {
                                return (Attribute) HelperAttributes.javaType2Attribute(pos.getBodyElement0()
                                    .getLatitude());
                            }

                            if (PARAMETER_GPS_LONGITUDE.equals(identifier.getValue())) {
                                return (Attribute) HelperAttributes.javaType2Attribute(pos.getBodyElement0()
                                    .getLongitude());
                            }

                            if (PARAMETER_GPS_ALTITUDE.equals(identifier.getValue())) {
                                return (Attribute) HelperAttributes.javaType2Attribute(pos.getBodyElement0()
                                    .getAltitude());
                            }

                            if (PARAMETER_GPS_ELAPSED_TIME.equals(identifier.getValue())) {
                                return pos.getBodyElement1();
                            }
                        } catch (IOException | NMFException ex) {
                            LOGGER.log(Level.SEVERE, null, ex);
                        }
                        break;
                    case PARAMETER_MAG_X:
                    case PARAMETER_MAG_Y:
                    case PARAMETER_MAG_Z:
                        try {
                            GetStatusResponse adcsStatus = nmf.getPlatformServices().getAutonomousADCSService()
                                .getStatus();
                            VectorF3D magField = adcsStatus.getBodyElement0().getMagneticField();

                            if (PARAMETER_MAG_X.equals(identifier.getValue())) {
                                return (Attribute) HelperAttributes.javaType2Attribute(magField.getX());
                            }

                            if (PARAMETER_MAG_Y.equals(identifier.getValue())) {
                                return (Attribute) HelperAttributes.javaType2Attribute(magField.getY());
                            }

                            if (PARAMETER_MAG_Z.equals(identifier.getValue())) {
                                return (Attribute) HelperAttributes.javaType2Attribute(magField.getZ());
                            }
                        } catch (IOException | NMFException ex) {
                            LOGGER.log(Level.SEVERE, null, ex);
                        }
                        break;
                    default:
                        break;
                }
            }

        } catch (MALException | MALInteractionException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
        return false; // Parameter has not been set
    }

    @Override
    public boolean isReadOnly(Identifier name) {
        return true; // No parameter is directly writable
    }

    /**
     * The user must implement this interface in order to link a certain action Identifier to the
     * method on the application
     *
     * @param name                Name of the Parameter
     * @param attributeValues
     * @param actionInstanceObjId
     * @param reportProgress      Determines if it is necessary to report the execution
     * @param interaction         The interaction object progress of the action
     *
     * @return Returns null if the Action was successful. If not null, then the returned value should
     * hold the error number
     */
    @Override
    public UInteger actionArrived(Identifier name, AttributeValueList attributeValues, Long actionInstanceObjId,
        boolean reportProgress, MALInteraction interaction) {
        if (nmf == null) {
            return new UInteger(0);
        }
        LOGGER.log(Level.INFO, "Action {0} with parameters '{'{1}'}' arrived.", new Object[]{name.toString(),
                                                                                             attributeValues.stream()
                                                                                                 .map(
                                                                                                     HelperAttributes::attribute2string)
                                                                                                 .collect(Collectors
                                                                                                     .joining(", "))});

        // Action dispatcher
        if (null != name.getValue()) {
            switch (name.getValue()) {
                case ACTION_SUN_POINTING_MODE:
                    return executeAdcsModeAction((Duration) attributeValues.get(0).getValue(),
                        new AttitudeModeSunPointing());
                case ACTION_NADIR_POINTING_MODE:
                    return executeAdcsModeAction((Duration) attributeValues.get(0).getValue(),
                        new AttitudeModeNadirPointing());
                case ACTION_UNSET:
                    return executeAdcsModeAction(null, null);
                case ACTION_5_STAGES:
                    try {
                        return multiStageAction(actionInstanceObjId, 5);
                    } catch (NMFException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                        return new UInteger(4);
                    }
                default:
                    break;
            }
        }
        return null; // Action successful
    }

    private UInteger executeAdcsModeAction(Duration duration, AttitudeMode attitudeMode) {
        if (duration != null) {
            // Negative Durations are not allowed!
            if (duration.getValue() < 0) {
                return new UInteger(1);
            }
            if (duration.getValue() == 0) {
                // Adhere to the ADCS Service interface
                duration = null;
            }
        }
        try {
            nmf.getPlatformServices().getAutonomousADCSService().setDesiredAttitude(duration, attitudeMode);
        } catch (MALInteractionException | MALException | NMFException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return new UInteger(3);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return new UInteger(4);
        }
        return null; // Success
    }

    public void startAdcsAttitudeMonitoring() {
        try {
            // Subscribe monitorAttitude
            nmf.getPlatformServices().getAutonomousADCSService().monitorAttitudeRegister(ConnectionConsumer
                .subscriptionWildcard(), new DataReceivedAdapter());
            nmf.getPlatformServices().getAutonomousADCSService().enableMonitoring(true, ATTITUDE_MONITORING_INTERVAL);
        } catch (IOException | MALInteractionException | MALException | NMFException ex) {
            LOGGER.log(Level.SEVERE, "Error when setting up attitude monitoring.", ex);
        }
    }

    /**
     * Example function implementing a non-instantaneous action.
     *
     * @param actionInstanceObjId associated action ID
     * @param totalNumberOfStages total number of stages to report through
     *
     * @return Returns null if the Action was successful. If not null, then the returned value should
     * hold the error number
     */
    private UInteger multiStageAction(Long actionInstanceObjId, int totalNumberOfStages) throws NMFException {
        final int sleepTime = 2; // 2 seconds

        UInteger errorNumber = null;

        for (int stage = 1; stage < totalNumberOfStages + 1; stage++) {
            nmf.reportActionExecutionProgress(true, 0, stage, totalNumberOfStages, actionInstanceObjId);

            try {
                Thread.sleep(sleepTime * 1000); //1000 milliseconds multiplier.
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        return errorNumber;
    }

    public class DataReceivedAdapter extends AutonomousADCSAdapter {

        @Override
        public void monitorAttitudeNotifyReceived(final MALMessageHeader msgHeader, final Identifier lIdentifier,
            final UpdateHeaderList lUpdateHeaderList,
            org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeTelemetryList attitudeTelemetryList,
            org.ccsds.moims.mo.platform.autonomousadcs.structures.ActuatorsTelemetryList actuatorsTelemetryList,
            org.ccsds.moims.mo.mal.structures.DurationList controlDurationList,
            org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeList attitudeModeList, final Map qosp) {
            for (AttitudeTelemetry attitudeTm : attitudeTelemetryList) {
                try {
                    VectorF3D sunVector = attitudeTm.getSunVector();
                    VectorF3D magneticField = attitudeTm.getMagneticField();
                    VectorF3D angularVelocity = attitudeTm.getAngularVelocity();
                    Quaternion attitude = attitudeTm.getAttitude();
                    //attMode = AttitudeMode.NADIRPOINTING;
                    nmf.pushParameterValue(PARAMETER_SUN_VECTOR_X, sunVector.getX());
                    nmf.pushParameterValue(PARAMETER_SUN_VECTOR_Y, sunVector.getY());
                    nmf.pushParameterValue(PARAMETER_SUN_VECTOR_Z, sunVector.getZ());

                    nmf.pushParameterValue(PARAMETER_MAG_X, magneticField.getX());
                    nmf.pushParameterValue(PARAMETER_MAG_Y, magneticField.getY());
                    nmf.pushParameterValue(PARAMETER_MAG_Z, magneticField.getZ());

                    nmf.pushParameterValue(PARAMETER_ANGULAR_VELOCITY_X, angularVelocity.getX());
                    nmf.pushParameterValue(PARAMETER_ANGULAR_VELOCITY_Z, angularVelocity.getY());
                    nmf.pushParameterValue(PARAMETER_ANGULAR_VELOCITY_Y, angularVelocity.getZ());

                    nmf.pushParameterValue(PARAMETER_ATTITUDE_Q_A, attitude.getA());
                    nmf.pushParameterValue(PARAMETER_ATTITUDE_Q_B, attitude.getB());
                    nmf.pushParameterValue(PARAMETER_ATTITUDE_Q_C, attitude.getC());
                    nmf.pushParameterValue(PARAMETER_ATTITUDE_Q_D, attitude.getD());
                } catch (NMFException ex) {
                    LOGGER.log(Level.SEVERE, "Error when propagating Sensors TM", ex);
                }
            }
            for (ActuatorsTelemetry actuatorsTm : actuatorsTelemetryList) {
                try {
                    VectorF3D mtqDipoleMoment = actuatorsTm.getMtqDipoleMoment();
                    //VectorF3D angularVelocity = attitudeTm.getAngularVelocity();
                    //Quaternion attitude = attitudeTm.getAttitude();
                    nmf.pushParameterValue(PARAMETER_MTQ_X, mtqDipoleMoment.getX());
                    nmf.pushParameterValue(PARAMETER_MTQ_Y, mtqDipoleMoment.getY());
                    nmf.pushParameterValue(PARAMETER_MTQ_Z, mtqDipoleMoment.getZ());
                } catch (NMFException ex) {
                    LOGGER.log(Level.SEVERE, "Error when propagating Actuators TM", ex);
                }
            }
            for (Object activeAttitudeMode : attitudeModeList) {
                try {
                    nmf.pushParameterValue(PARAMETER_ADCS_MODE, attitudeModeToParamValue(
                        (AttitudeMode) activeAttitudeMode));
                } catch (NMFException ex) {
                    LOGGER.log(Level.SEVERE, "Error when propagating active ADCS mode", ex);
                }
            }
            for (Duration remainingDuration : controlDurationList) {
                try {
                    if (remainingDuration != null) {
                        nmf.pushParameterValue(PARAMETER_ADCS_DURATION, remainingDuration);
                    } else {
                        nmf.pushParameterValue(PARAMETER_ADCS_DURATION, new Duration(0));
                    }
                } catch (NMFException ex) {
                    LOGGER.log(Level.SEVERE, "Error when propagating active ADCS mode duration", ex);
                }
            }
        }
    }
}
