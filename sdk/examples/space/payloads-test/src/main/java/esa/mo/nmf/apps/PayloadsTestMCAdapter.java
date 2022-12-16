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

import esa.mo.common.impl.consumer.DirectoryConsumerServiceImpl;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.helpertools.misc.Const;
import esa.mo.mc.impl.consumer.ParameterConsumerServiceImpl;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.NMFInterface;
import esa.mo.nmf.annotations.Action;
import esa.mo.nmf.annotations.ActionParameter;
import esa.mo.nmf.annotations.Aggregation;
import esa.mo.nmf.annotations.Parameter;
import esa.mo.nmf.commonmoadapter.SimpleCommandingInterface;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import esa.mo.sm.impl.provider.AppsLauncherManager;
import org.ccsds.moims.mo.com.COMService;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPair;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPairList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.common.directory.structures.ServiceFilter;
import org.ccsds.moims.mo.common.structures.ServiceKey;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.conversion.structures.DiscreteConversionDetails;
import org.ccsds.moims.mo.mc.conversion.structures.DiscreteConversionDetailsList;
import org.ccsds.moims.mo.mc.parameter.ParameterHelper;
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterAdapter;
import org.ccsds.moims.mo.mc.parameter.structures.*;
import org.ccsds.moims.mo.mc.structures.*;
import org.ccsds.moims.mo.platform.autonomousadcs.consumer.AutonomousADCSAdapter;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.*;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormat;
import org.ccsds.moims.mo.platform.camera.structures.PixelResolution;
import org.ccsds.moims.mo.platform.gps.consumer.GPSAdapter;
import org.ccsds.moims.mo.platform.gps.structures.Position;
import org.ccsds.moims.mo.platform.gps.structures.PositionList;
import org.ccsds.moims.mo.platform.gps.structures.SatelliteInfoList;
import org.ccsds.moims.mo.platform.structures.VectorF3D;

/**
 * The adapter for the NMF App
 */
//add aggregations:
@Aggregation(id = PayloadsTestMCAdapter.AGGREGATION_MAG, description = "Aggregates Magnetometer components: X, Y, Z.",
             reportInterval = 10, sendUnchanged = true, sampleInterval = 3)
@Aggregation(id = PayloadsTestMCAdapter.AGGREGATION_GPS,
             description = "Aggregates: GPS Latitude, GPS Longitude, GPS Altitude.", reportInterval = 10,
             sendUnchanged = true, sampleInterval = 3)
@Aggregation(id = PayloadsTestMCAdapter.AGGREGATION_ECLIPSED,
             description = "Aggregates: CADC0884, CADC0886, CADC0888, CADC0890, CADC0892, CADC0894",
             reportInterval = 10, sendUnchanged = true, sampleInterval = 3, generationEnabled = true)
@Aggregation(id = PayloadsTestMCAdapter.AGGREGATION_IADCS_TELEMETRY, description = "iADCS telemetry data",
             reportInterval = 5, sendUnchanged = true, sampleInterval = 3, generationEnabled = true)
public class PayloadsTestMCAdapter extends MonitorAndControlNMFAdapter {
    // comma separated list of supervisor parameters to proxy
    public static final String SUPERVISOR_PARAMETER_PROXY_PROP = "esa.mo.nanosatmoframework.proxy.supervisor.parameters";
    public static final String AGGREGATION_MAG = "Magnetometer_Aggregation";
    public static final String AGGREGATION_GPS = "GPS_Aggregation";
    public static final String AGGREGATION_ECLIPSED = "Eclipsed_Aggregation";
    public static final String AGGREGATION_IADCS_TELEMETRY = "iADCS_Telemetry_Aggregation";
    private static final String PARAMETER_ADCS_MODE = "ADCS_ModeOperation";
    private static final String PARAMETER_ADCS_DURATION = "ADCS_RemainingControlDuration";
    private static final String PARAMETER_ANGULAR_VELOCITY_X = "AngularVelocity_X";
    private static final String PARAMETER_ANGULAR_VELOCITY_Y = "AngularVelocity_Y";
    private static final String PARAMETER_ANGULAR_VELOCITY_Z = "AngularVelocity_Z";
    private static final String PARAMETER_ATTITUDE_Q_A = "AttitudeQuaternion_a";
    private static final String PARAMETER_ATTITUDE_Q_B = "AttitudeQuaternion_b";
    private static final String PARAMETER_ATTITUDE_Q_C = "AttitudeQuaternion_c";
    private static final String PARAMETER_ATTITUDE_Q_D = "AttitudeQuaternion_d";
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
    private static final Logger LOGGER = Logger.getLogger(PayloadsTestMCAdapter.class.getName());

    private static final float DEFAULT_CAMERA_GAIN = 1.0f;
    private static final float DEFAULT_CAMERA_EXPOSURE_TIME = 0.1f;
    public final NMFInterface nmf;

    public final AtomicInteger picturesTaken = new AtomicInteger(0);

    private final int defaultPictureWidth = 2048;
    private final int defaultPictureHeight = 1944;
    public final PixelResolution defaultCameraResolution;

    private final PayloadsTestActionsHandler actionsHandler;
    public SimpleCommandingInterface simpleCommandingInterface;
    //----------------------------------- Camera Parameters -----------------------------------------
    @Parameter(description = "The number of pictures taken", generationEnabled = false,
               onGetFunction = "onGetPicturesTaken", readOnly = true, reportIntervalSeconds = 10)
    Integer NumberOfPicturesTaken = 0;

    @Parameter(description = "Camera red channel gain", generationEnabled = false, reportIntervalSeconds = 10)
    public float cameraGainR = DEFAULT_CAMERA_GAIN;

    @Parameter(description = "Camera green channel gain", generationEnabled = false, reportIntervalSeconds = 10)
    public float cameraGainG = DEFAULT_CAMERA_GAIN;

    @Parameter(description = "Camera blue channel gain", generationEnabled = false, reportIntervalSeconds = 10)
    public float cameraGainB = DEFAULT_CAMERA_GAIN;

    @Parameter(description = "Camera exposure time", generationEnabled = false, reportIntervalSeconds = 10)
    public float cameraExposureTime = DEFAULT_CAMERA_EXPOSURE_TIME;

    //-----------------------------------------------------------------------------------------------
    //-------------------------------------- GPS Parameters -----------------------------------------
    @Parameter(description = "The number of satellites in view of GPS receiver.", rawUnit = "sats",
               generationEnabled = false, onGetFunction = "onGPSSatsInView", readOnly = true)
    Integer GPS_NumberOfSatellitesInView = 0;

    @Parameter(description = "The GPS Latitude", rawUnit = "degrees", generationEnabled = false,
               onGetFunction = "onGetLatitude", readOnly = true, reportIntervalSeconds = 2, aggregations = {
                                                                                                            AGGREGATION_GPS})
    Float GPS_Latitude = 0.0f;

    @Parameter(description = "The GPS Longitude", rawUnit = "degrees", generationEnabled = false,
               onGetFunction = "onGetLongitude", readOnly = true, reportIntervalSeconds = 2, aggregations = {
                                                                                                             AGGREGATION_GPS})
    Float GPS_Longitude = 0.0f;

    @Parameter(description = "The GPS Altitude", rawUnit = "meters", generationEnabled = false,
               onGetFunction = "onGetAltitude", readOnly = true, reportIntervalSeconds = 2, aggregations = {
                                                                                                            AGGREGATION_GPS})
    Float GPS_Altitude = 0.0f;

    @Parameter(description = "The GPS elapsed Time", rawUnit = "seconds", generationEnabled = false,
               onGetFunction = "onGetGPSElapsedTime", readOnly = true)
    Duration GPS_ElapsedTime = new Duration();

    //-----------------------------------------------------------------------------------------------
    //------------------------------------ Magnetic Field Parameters---------------------------------
    @Parameter(name = PARAMETER_MAG_X, description = "The Magnetometer X component", rawUnit = "microTesla",
               generationEnabled = false, onGetFunction = "onGetMagneticField_X", readOnly = true,
               reportIntervalSeconds = 2, aggregations = {AGGREGATION_MAG, AGGREGATION_IADCS_TELEMETRY})
    Float MagneticField_X = 0.0f;

    @Parameter(name = PARAMETER_MAG_Y, description = "The Magnetometer Y component", rawUnit = "microTesla",
               generationEnabled = false, onGetFunction = "onGetMagneticField_Y", readOnly = true,
               reportIntervalSeconds = 2, aggregations = {AGGREGATION_MAG, AGGREGATION_IADCS_TELEMETRY})
    Float MagneticField_Y = 0.0f;

    @Parameter(name = PARAMETER_MAG_Z, description = "The Magnetometer Z component", rawUnit = "microTesla",
               generationEnabled = false, onGetFunction = "onGetMagneticField_Z", readOnly = true,
               reportIntervalSeconds = 2, aggregations = {AGGREGATION_MAG, AGGREGATION_IADCS_TELEMETRY})
    Float MagneticField_Z = 0.0f;

    //-------------------------------------- Supervisor Parameters ----------------------------------
    @Parameter(name = "CADC0884", description = "I_PD1_THETA", generationEnabled = false, reportIntervalSeconds = 5,
               aggregations = {AGGREGATION_ECLIPSED})
    Float IPD1Theta = 0.0f;

    @Parameter(name = "CADC0886", description = "I_PD2_THETA", generationEnabled = false, reportIntervalSeconds = 5,
               aggregations = {AGGREGATION_ECLIPSED})
    Float IPD2Theta = 0.0f;

    @Parameter(name = "CADC0888", description = "I_PD3_THETA", generationEnabled = false, reportIntervalSeconds = 5,
               aggregations = {AGGREGATION_ECLIPSED})
    Float IPD3Theta = 0.0f;

    @Parameter(name = "CADC0890", description = "I_PD4_THETA", generationEnabled = false, reportIntervalSeconds = 5,
               aggregations = {AGGREGATION_ECLIPSED})
    Float IPD4Theta = 0.0f;

    @Parameter(name = "CADC0892", description = "I_PD5_THETA", generationEnabled = false, reportIntervalSeconds = 5,
               aggregations = {AGGREGATION_ECLIPSED})
    Float IPD5Theta = 0.0f;

    @Parameter(name = "CADC0894", description = "I_PD6_THETA", generationEnabled = false, reportIntervalSeconds = 5,
               aggregations = {AGGREGATION_ECLIPSED})
    Float IPD6Theta = 0.0f;

    @Parameter(generationEnabled = false, readOnly = true, reportIntervalSeconds = 5, onGetFunction = "onGetEclipsed")
    Boolean eclipsed = false;
    final static Float ECLIPSED_EPSILON = 0.001f;

    @Parameter(name = "Supervisor TM Polling Enabled",
               description = "Enables pooling telemetry data from the supervisor", generationEnabled = false)
    Boolean supervisorTMPollingEnabled = false;

    //-------------------------------------- ADCS Attitude Telemetry Parameters ----------------------------------------

    @Parameter(name = PARAMETER_ATTITUDE_Q_A, description = "The Attitude quaternion A component",
               generationEnabled = false, onGetFunction = "onGetAttitudeQuatA", readOnly = true,
               reportIntervalSeconds = 5, aggregations = {AGGREGATION_IADCS_TELEMETRY})
    Float attitudeQuatA = 0.0f;

    @Parameter(name = PARAMETER_ATTITUDE_Q_B, description = "The Attitude quaternion B component",
               generationEnabled = false, onGetFunction = "onGetAttitudeQuatB", readOnly = true,
               reportIntervalSeconds = 5, aggregations = {AGGREGATION_IADCS_TELEMETRY})
    Float attitudeQuatB = 0.0f;

    @Parameter(name = PARAMETER_ATTITUDE_Q_C, description = "The Attitude quaternion C component",
               generationEnabled = false, onGetFunction = "onGetAttitudeQuatC", readOnly = true,
               reportIntervalSeconds = 5, aggregations = {AGGREGATION_IADCS_TELEMETRY})
    Float attitudeQuatC = 0.0f;

    @Parameter(name = PARAMETER_ATTITUDE_Q_D, description = "The Attitude quaternion D component",
               generationEnabled = false, onGetFunction = "onGetAttitudeQuatD", readOnly = true,
               reportIntervalSeconds = 5, aggregations = {AGGREGATION_IADCS_TELEMETRY})
    Float attitudeQuatD = 0.0f;

    @Parameter(name = PARAMETER_ANGULAR_VELOCITY_X, description = "The angular velocity X component",
               generationEnabled = false, onGetFunction = "onGetAngularVelocityX", readOnly = true,
               reportIntervalSeconds = 5, aggregations = {AGGREGATION_IADCS_TELEMETRY})
    Float angularVelocityX = 0.0f;

    @Parameter(name = PARAMETER_ANGULAR_VELOCITY_Y, description = "The angular velocity Y component",
               generationEnabled = false, onGetFunction = "onGetAngularVelocityY", readOnly = true,
               reportIntervalSeconds = 5, aggregations = {AGGREGATION_IADCS_TELEMETRY})
    Float angularVelocityY = 0.0f;

    @Parameter(name = PARAMETER_ANGULAR_VELOCITY_Z, description = "The angular velocity Z component",
               generationEnabled = false, onGetFunction = "onGetAngularVelocityZ", readOnly = true,
               reportIntervalSeconds = 5, aggregations = {AGGREGATION_IADCS_TELEMETRY})
    Float angularVelocityZ = 0.0f;

    @Parameter(name = PARAMETER_SUN_VECTOR_X, description = "The sun vector X component", generationEnabled = false,
               onGetFunction = "onGetSunVectorX", readOnly = true, reportIntervalSeconds = 5, aggregations = {
                                                                                                              AGGREGATION_IADCS_TELEMETRY})
    Float sunVectorX = 0.0f;

    @Parameter(name = PARAMETER_SUN_VECTOR_Y, description = "The sun vector Y component", generationEnabled = false,
               onGetFunction = "onGetSunVectorY", readOnly = true, reportIntervalSeconds = 5, aggregations = {
                                                                                                              AGGREGATION_IADCS_TELEMETRY})
    Float sunVectorY = 0.0f;

    @Parameter(name = PARAMETER_SUN_VECTOR_Z, description = "The sun vector Z component", generationEnabled = false,
               onGetFunction = "onGetSunVectorZ", readOnly = true, reportIntervalSeconds = 5, aggregations = {
                                                                                                              AGGREGATION_IADCS_TELEMETRY})
    Float sunVectorZ = 0.0f;

    @Parameter(description = "True when achieved desired pointing mode, false otherwise", generationEnabled = false,
               onGetFunction = "onGetStateTarget", readOnly = true, reportIntervalSeconds = 5, aggregations = {
                                                                                                               AGGREGATION_IADCS_TELEMETRY})
    Boolean stateTarget = false;

    //-------------------------------------- ADCS Actuators Telemetry Parameters ----------------------------------------
    @Parameter(name = PARAMETER_MTQ_X, description = "The magnetorquers dipole moment X component",
               generationEnabled = false, onGetFunction = "onGetMtqDipoleMomentX", readOnly = true,
               reportIntervalSeconds = 5, aggregations = {AGGREGATION_IADCS_TELEMETRY})
    Float mtqDipoleMomentX = 0.0f;

    @Parameter(name = PARAMETER_MTQ_Y, description = "The magnetorquers dipole moment Y component",
               generationEnabled = false, onGetFunction = "onGetMtqDipoleMomentY", readOnly = true,
               reportIntervalSeconds = 5, aggregations = {AGGREGATION_IADCS_TELEMETRY})
    Float mtqDipoleMomentY = 0.0f;

    @Parameter(name = PARAMETER_MTQ_Z, description = "The magnetorquers dipole moment Z component",
               generationEnabled = false, onGetFunction = "onGetMtqDipoleMomentZ", readOnly = true,
               reportIntervalSeconds = 5, aggregations = {AGGREGATION_IADCS_TELEMETRY})
    Float mtqDipoleMomentZ = 0.0f;

    @Parameter(description = "Current state of the magnetorquers", generationEnabled = false,
               onGetFunction = "onGetMtqState", readOnly = true, reportIntervalSeconds = 5, aggregations = {
                                                                                                            AGGREGATION_IADCS_TELEMETRY})
    UInteger mtqState = new UInteger(0);
    //-----------------------------------------------------------------------------------------------

    public PayloadsTestMCAdapter(final NMFInterface nmfProvider) {
        this.defaultCameraResolution = new PixelResolution(new UInteger(defaultPictureWidth), new UInteger(
            defaultPictureHeight));
        actionsHandler = new PayloadsTestActionsHandler(this);
        this.nmf = nmfProvider;
    }

    private enum AttitudeModeEnum {
        IDLE, BDOT, SUNPOINTING, SINGLESPINNING, TARGETTRACKING, NADIRPOINTING, VECTORPOINTING
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
        } else if (attitude instanceof AttitudeModeVectorPointing) {
            modeEnum = AttitudeModeEnum.VECTORPOINTING;
        } else {
            throw new IllegalArgumentException("Unrecognized attitude mode type!");
        }
        return new UOctet((short) modeEnum.ordinal());
    }

    @Override
    public void initialRegistrations(MCRegistration registration) {
        // call super for annotations to work!
        super.initialRegistrations(registration);

        // conversions are not supported by annotations yet, so these have to be done the old way
        registerParameters(registration);
    }

    private void registerParameters(MCRegistration registration) throws IllegalArgumentException {
        ParameterConversion paramConversion = registerAdcsModeConversion(registration);

        // ------------------ Parameters ------------------
        ParameterDefinitionDetailsList defsOther = new ParameterDefinitionDetailsList();
        IdentifierList paramOtherNames = new IdentifierList();

        defsOther.add(new ParameterDefinitionDetails("The ADCS mode of operation", Union.UOCTET_SHORT_FORM.byteValue(),
            "", false, new Duration(0), null, paramConversion));
        paramOtherNames.add(new Identifier(PARAMETER_ADCS_MODE));

        registration.registerParameters(paramOtherNames, defsOther);
    }

    private ParameterConversion registerAdcsModeConversion(MCRegistration registration)
        throws IllegalArgumentException {
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
        mappings.add(new Pair(new UOctet((short) AttitudeModeEnum.VECTORPOINTING.ordinal()), new Union(
            "VECTORPOINTING")));
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
            LOGGER.log(Level.SEVERE, "Failed to register conversion.", ex);
        }
        return paramConversion;
    }

    public void subscribeToSupervisorParameters(URI supervisorURI) {
        if (supervisorURI != null && supervisorURI.getValue().startsWith("malspp")) {
            LOGGER.log(Level.INFO,
                "The Central Directory service URI read is selecting 'malspp' as transport. The URI will be discarded." +
                    " To enable a better IPC communication, please enable the secondary transport protocol flag: " +
                    HelperMisc.SECONDARY_PROTOCOL);

            supervisorURI = null;
        }

        DirectoryConsumerServiceImpl centralDirectory = null;

        // Connect to the Central Directory service
        if (supervisorURI != null) {
            try {
                centralDirectory = new DirectoryConsumerServiceImpl(supervisorURI);

                IdentifierList domain = new IdentifierList();
                domain.add(new Identifier("*"));
                COMService parameterCOM = ParameterHelper.PARAMETER_SERVICE;
                final ServiceKey serviceKey = new ServiceKey(parameterCOM.getArea().getNumber(), parameterCOM
                    .getNumber(), parameterCOM.getArea().getVersion());
                final ServiceFilter sf = new ServiceFilter(new Identifier(Const.NANOSAT_MO_SUPERVISOR_NAME), domain,
                    new Identifier("*"), null, new Identifier("*"), serviceKey, new UShortList());
                final ProviderSummaryList supervisorParameterServiceConnectionDetails = centralDirectory
                    .getDirectoryStub().lookupProvider(sf);

                try {
                    final SingleConnectionDetails connectionDetails = AppsLauncherManager
                        .getSingleConnectionDetailsFromProviderSummaryList(supervisorParameterServiceConnectionDetails);
                    ParameterConsumerServiceImpl supervisorParameterService = new ParameterConsumerServiceImpl(
                        connectionDetails, null);

                    IdentifierList eclipsedParameters = new IdentifierList();
                    eclipsedParameters.add(new Identifier("CADC0884"));
                    eclipsedParameters.add(new Identifier("CADC0886"));
                    eclipsedParameters.add(new Identifier("CADC0888"));
                    eclipsedParameters.add(new Identifier("CADC0890"));
                    eclipsedParameters.add(new Identifier("CADC0892"));
                    eclipsedParameters.add(new Identifier("CADC0894"));
                    ObjectInstancePairList payloadsTestIds = this.parameterService.listDefinition(eclipsedParameters,
                        null);

                    Map<String, Long> nameToId = new HashMap<>();
                    for (int i = 0; i < eclipsedParameters.size(); ++i) {
                        nameToId.put(eclipsedParameters.get(i).getValue(), payloadsTestIds.get(i)
                            .getObjIdentityInstanceId());
                    }

                    String parametersProp = System.getProperty(SUPERVISOR_PARAMETER_PROXY_PROP, null);

                    IdentifierList supervisorParameters = new IdentifierList();
                    if (parametersProp == null) {
                        supervisorParameters.add(new Identifier("SBD6682p"));
                        supervisorParameters.add(new Identifier("SBD6692p"));
                        supervisorParameters.add(new Identifier("SBD6702p"));
                        supervisorParameters.add(new Identifier("SBD6712p"));
                        supervisorParameters.add(new Identifier("SBD6722p"));
                        supervisorParameters.add(new Identifier("SBD6732p"));
                        supervisorParameters.add(new Identifier("SBD6742p"));
                        supervisorParameters.add(new Identifier("SBD6752p"));
                        supervisorParameters.add(new Identifier("SBD6762p"));
                        supervisorParameters.add(new Identifier("SBD6772p"));
                        supervisorParameters.add(new Identifier("SBD6862p"));
                        supervisorParameters.add(new Identifier("SBD6872p"));
                    } else {
                        parametersProp = parametersProp.replace("\"", "");
                        String[] parameters = parametersProp.split(",");
                        for (String parameter : parameters) {
                            supervisorParameters.add(new Identifier(parameter.trim()));
                        }
                    }

                    IdentifierList parameterNames = new IdentifierList();
                    parameterNames.addAll(supervisorParameters);
                    parameterNames.addAll(eclipsedParameters);
                    ObjectInstancePairList supervisorIds = new ObjectInstancePairList();
                    try {
                        supervisorIds = supervisorParameterService.getParameterStub().listDefinition(parameterNames);
                    } catch (MALInteractionException e) {
                        if (e.getStandardError().getErrorNumber().equals(MALHelper.UNKNOWN_ERROR_NUMBER)) {
                            UIntegerList unknownParams = (UIntegerList) e.getStandardError().getExtraInformation();
                            for (UInteger index : unknownParams) {
                                parameterNames.set((int) index.getValue(), null);
                            }
                            parameterNames.removeIf(Objects::isNull);

                            if (!parameterNames.isEmpty()) {
                                supervisorIds = supervisorParameterService.getParameterStub().listDefinition(
                                    parameterNames);
                            }
                        }
                    }

                    InstanceBooleanPairList enable = new InstanceBooleanPairList();
                    for (ObjectInstancePair id : supervisorIds) {
                        enable.add(new InstanceBooleanPair(id.getObjIdentityInstanceId(), true));
                    }
                    supervisorParameterService.getParameterStub().enableGeneration(false, enable);

                    Identifier subscriptionId = new Identifier("PayloadsTestSupervisorSubscription");
                    EntityKeyList entityKeys = new EntityKeyList();
                    for (Identifier parameter : parameterNames) {
                        EntityKey entitykey = new EntityKey(parameter, 0L, 0L, 0L);
                        entityKeys.add(entitykey);
                    }
                    EntityRequest entity = new EntityRequest(null, false, false, false, false, entityKeys);
                    EntityRequestList entities = new EntityRequestList();
                    entities.add(entity);
                    Subscription subscription = new Subscription(subscriptionId, entities);

                    ParameterAdapter adapter = new ParameterAdapter() {
                        @Override
                        public void monitorValueNotifyReceived(MALMessageHeader msgHeader, Identifier identifier,
                            UpdateHeaderList updateHeaderList, ObjectIdList objectIdList,
                            ParameterValueList parameterValueList, Map qosProperties) {

                            for (int i = 0; i < updateHeaderList.size(); ++i) {
                                String parameterName = updateHeaderList.get(i).getKey().getFirstSubKey().getValue();
                                Attribute value = parameterValueList.get(i).getRawValue();
                                Time timestamp = updateHeaderList.get(i).getTimestamp();
                                Long id = nameToId.get(parameterName);
                                if (id != null) {
                                    PayloadsTestMCAdapter.this.onSetValue(new ParameterRawValue(id, value));
                                } else if (supervisorTMPollingEnabled) {
                                    System.out.println(HelperTime.time2readableString(timestamp) + " - " +
                                        parameterName + " - " + value.toString());
                                }
                            }
                        }
                    };
                    supervisorParameterService.getParameterStub().monitorValueRegister(subscription, adapter);
                } catch (IOException | MALException | MALInteractionException ex) {
                    LOGGER.log(Level.SEVERE,
                        "Could not retrieve supervisor COM Parameter service details from the Central Directory.", ex);
                }
                centralDirectory.close();
            } catch (MALException | MalformedURLException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                LOGGER.log(Level.SEVERE, "Could not connect to the Central Directory service! Maybe it is down...");
            }
        }

    }

    //-------------------------------------- onGet Functions----------------------------------------
    public void onGetEclipsed() {
        eclipsed = IPD1Theta < ECLIPSED_EPSILON && IPD2Theta < ECLIPSED_EPSILON && IPD3Theta < ECLIPSED_EPSILON &&
            IPD4Theta < ECLIPSED_EPSILON && IPD5Theta < ECLIPSED_EPSILON && IPD6Theta < ECLIPSED_EPSILON;
    }

    public void onGPSSatsInView() {
        final Semaphore sem = new Semaphore(0);
        class GPSAdapterImpl extends GPSAdapter {
            @Override
            public void getSatellitesInfoResponseReceived(MALMessageHeader msgHeader,
                SatelliteInfoList gpsSatellitesInfo, java.util.Map qosProperties) {
                GPS_NumberOfSatellitesInView = gpsSatellitesInfo.size();
                sem.release();
            }

            @Override
            public void getSatellitesInfoAckErrorReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
                org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties) {
                sem.release();
            }

            @Override
            public void getSatellitesInfoResponseErrorReceived(
                org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
                org.ccsds.moims.mo.mal.MALStandardError error, java.util.Map qosProperties) {
                sem.release();
            }
        }

        try {
            nmf.getPlatformServices().getGPSService().getSatellitesInfo(new GPSAdapterImpl());
        } catch (IOException | MALInteractionException | MALException | NMFException ex) {
            LOGGER.log(Level.SEVERE, "GPS error: " + ex.getMessage());
            GPS_NumberOfSatellitesInView = null;
        }

        try {
            sem.acquire();
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void onGetLatitude() {
        try {
            GPS_Latitude = nmf.getPlatformServices().getGPSService().getLastKnownPosition().getBodyElement0()
                .getLatitude();
        } catch (NMFException | IOException | MALInteractionException | MALException ex) {
            LOGGER.log(Level.SEVERE, "GPS error: " + ex.getMessage());
            GPS_Latitude = null;
        }
    }

    public void onGetLongitude() {
        try {
            GPS_Longitude = nmf.getPlatformServices().getGPSService().getLastKnownPosition().getBodyElement0()
                .getLongitude();
        } catch (NMFException | IOException | MALInteractionException | MALException ex) {
            LOGGER.log(Level.SEVERE, "GPS error: " + ex.getMessage());
            GPS_Longitude = null;
        }
    }

    public void onGetAltitude() {
        try {
            final Semaphore sem = new Semaphore(0);
            final PositionList pos = new PositionList();
            class AdapterImpl extends GPSAdapter {

                @Override
                public void getPositionResponseReceived(MALMessageHeader msgHeader, Position position,
                    Map qosProperties) {
                    pos.add(position);
                    sem.release();
                }
            }
            nmf.getPlatformServices().getGPSService().getPosition(new AdapterImpl());

            sem.acquire();
            GPS_Altitude = pos.get(0).getAltitude();
        } catch (NMFException | IOException | MALInteractionException | MALException | InterruptedException ex) {
            LOGGER.log(Level.SEVERE, "GPS error: " + ex.getMessage());
            GPS_Altitude = null;
        }
    }

    public void onGetGPSElapsedTime() {
        try {
            GPS_ElapsedTime = nmf.getPlatformServices().getGPSService().getLastKnownPosition().getBodyElement1();
        } catch (NMFException | IOException | MALInteractionException | MALException ex) {
            LOGGER.log(Level.SEVERE, "GPS error: " + ex.getMessage());
            GPS_ElapsedTime = null;
        }
    }

    public void onGetMagneticField_X() {
        try {
            MagneticField_X = nmf.getPlatformServices().getAutonomousADCSService().getStatus().getBodyElement0()
                .getMagneticField().getX();
        } catch (NMFException | IOException | MALInteractionException | MALException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            MagneticField_X = null;
        }
    }

    public void onGetMagneticField_Y() {
        try {
            MagneticField_Y = nmf.getPlatformServices().getAutonomousADCSService().getStatus().getBodyElement0()
                .getMagneticField().getY();
        } catch (NMFException | IOException | MALInteractionException | MALException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            MagneticField_Y = null;
        }
    }

    public void onGetMagneticField_Z() {
        try {
            MagneticField_Z = nmf.getPlatformServices().getAutonomousADCSService().getStatus().getBodyElement0()
                .getMagneticField().getZ();
        } catch (NMFException | IOException | MALInteractionException | MALException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            MagneticField_Z = null;
        }
    }

    public void onGetPicturesTaken() {
        NumberOfPicturesTaken = picturesTaken.get();
    }

    public void onGetAttitudeQuatA() {
        try {
            attitudeQuatA = nmf.getPlatformServices().getAutonomousADCSService().getStatus().getBodyElement0()
                .getAttitude().getA();
        } catch (MALInteractionException | MALException | IOException | NMFException e) {
            LOGGER.log(Level.SEVERE, null, e);
            attitudeQuatA = null;
        }
    }

    public void onGetAttitudeQuatB() {
        try {
            attitudeQuatB = nmf.getPlatformServices().getAutonomousADCSService().getStatus().getBodyElement0()
                .getAttitude().getB();
        } catch (MALInteractionException | MALException | IOException | NMFException e) {
            LOGGER.log(Level.SEVERE, null, e);
            attitudeQuatB = null;
        }
    }

    public void onGetAttitudeQuatC() {
        try {
            attitudeQuatC = nmf.getPlatformServices().getAutonomousADCSService().getStatus().getBodyElement0()
                .getAttitude().getC();
        } catch (MALInteractionException | MALException | IOException | NMFException e) {
            LOGGER.log(Level.SEVERE, null, e);
            attitudeQuatC = null;
        }
    }

    public void onGetAttitudeQuatD() {
        try {
            attitudeQuatD = nmf.getPlatformServices().getAutonomousADCSService().getStatus().getBodyElement0()
                .getAttitude().getD();
        } catch (MALInteractionException | MALException | IOException | NMFException e) {
            LOGGER.log(Level.SEVERE, null, e);
            attitudeQuatD = null;
        }
    }

    public void onGetAngularVelocityX() {
        try {
            angularVelocityX = nmf.getPlatformServices().getAutonomousADCSService().getStatus().getBodyElement0()
                .getAngularVelocity().getX();
        } catch (MALInteractionException | MALException | IOException | NMFException e) {
            LOGGER.log(Level.SEVERE, null, e);
            angularVelocityX = null;
        }
    }

    public void onGetAngularVelocityY() {
        try {
            angularVelocityY = nmf.getPlatformServices().getAutonomousADCSService().getStatus().getBodyElement0()
                .getAngularVelocity().getY();
        } catch (MALInteractionException | MALException | IOException | NMFException e) {
            LOGGER.log(Level.SEVERE, null, e);
            angularVelocityY = null;
        }
    }

    public void onGetAngularVelocityZ() {
        try {
            angularVelocityZ = nmf.getPlatformServices().getAutonomousADCSService().getStatus().getBodyElement0()
                .getAngularVelocity().getZ();
        } catch (MALInteractionException | MALException | IOException | NMFException e) {
            LOGGER.log(Level.SEVERE, null, e);
            angularVelocityZ = null;
        }
    }

    public void onGetSunVectorX() {
        try {
            sunVectorX = nmf.getPlatformServices().getAutonomousADCSService().getStatus().getBodyElement0()
                .getSunVector().getX();
        } catch (MALInteractionException | MALException | IOException | NMFException e) {
            LOGGER.log(Level.SEVERE, null, e);
            sunVectorX = null;
        }
    }

    public void onGetSunVectorY() {
        try {
            sunVectorY = nmf.getPlatformServices().getAutonomousADCSService().getStatus().getBodyElement0()
                .getSunVector().getY();
        } catch (MALInteractionException | MALException | IOException | NMFException e) {
            LOGGER.log(Level.SEVERE, null, e);
            sunVectorY = null;
        }
    }

    public void onGetSunVectorZ() {
        try {
            sunVectorZ = nmf.getPlatformServices().getAutonomousADCSService().getStatus().getBodyElement0()
                .getSunVector().getZ();
        } catch (MALInteractionException | MALException | IOException | NMFException e) {
            LOGGER.log(Level.SEVERE, null, e);
            sunVectorZ = null;
        }
    }

    public void onGetStateTarget() {
        try {
            stateTarget = nmf.getPlatformServices().getAutonomousADCSService().getStatus().getBodyElement0()
                .getStateTarget();
        } catch (MALInteractionException | MALException | IOException | NMFException e) {
            LOGGER.log(Level.SEVERE, null, e);
            stateTarget = null;
        }
    }

    public void onGetMtqDipoleMomentX() {
        try {
            mtqDipoleMomentX = nmf.getPlatformServices().getAutonomousADCSService().getStatus().getBodyElement1()
                .getMtqDipoleMoment().getX();
        } catch (MALInteractionException | MALException | IOException | NMFException e) {
            LOGGER.log(Level.SEVERE, null, e);
            mtqDipoleMomentX = null;
        }
    }

    public void onGetMtqDipoleMomentY() {
        try {
            mtqDipoleMomentY = nmf.getPlatformServices().getAutonomousADCSService().getStatus().getBodyElement1()
                .getMtqDipoleMoment().getY();
        } catch (MALInteractionException | MALException | IOException | NMFException e) {
            LOGGER.log(Level.SEVERE, null, e);
            mtqDipoleMomentY = null;
        }
    }

    public void onGetMtqDipoleMomentZ() {
        try {
            mtqDipoleMomentZ = nmf.getPlatformServices().getAutonomousADCSService().getStatus().getBodyElement1()
                .getMtqDipoleMoment().getZ();
        } catch (MALInteractionException | MALException | IOException | NMFException e) {
            LOGGER.log(Level.SEVERE, null, e);
            mtqDipoleMomentZ = null;
        }
    }

    public void onGetMtqState() {
        try {
            mtqState = nmf.getPlatformServices().getAutonomousADCSService().getStatus().getBodyElement1().getMtqState()
                .getNumericValue();
        } catch (MALInteractionException | MALException | IOException | NMFException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, null, e);
            mtqState = null;
        }
    }

    //-----------------------------------------------------------------------------------------------
    //----------------------------------- Actions ---------------------------------------------------

    @Action(description = "Changes the spacecraft's attitude to sun pointing mode.")
    public UInteger adcs_SunPointingMode(Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction,
        @ActionParameter(name = "Hold Duration", rawUnit = "seconds") Duration holdDuration) {
        return actionsHandler.executeAdcsModeAction(holdDuration, new AttitudeModeSunPointing(), this);
    }

    @Action(description = "Changes the spacecraft's attitude to nadir pointing mode.")
    public UInteger adcs_NadirPointingMode(Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction,
        @ActionParameter(name = "Hold Duration", rawUnit = "seconds") Duration holdDuration) {
        return actionsHandler.executeAdcsModeAction(holdDuration, new AttitudeModeNadirPointing(), this);
    }

    @Action(description = "Changes the spacecraft's attitude to vector pointing mode.")
    public UInteger adcs_VectorPointingMode(Long actionInstanceObjId, boolean reportProgress,
        MALInteraction interaction, @ActionParameter(name = "Hold Duration", rawUnit = "seconds") Duration holdDuration,
        @ActionParameter(name = "X", rawUnit = "degree") float x, @ActionParameter(name = "Y",
                                                                                   rawUnit = "degree") float y,
        @ActionParameter(name = "Z", rawUnit = "degree") float z, @ActionParameter(name = "margin",
                                                                                   rawUnit = "degree") float margin) {
        return actionsHandler.executeAdcsModeAction(holdDuration, new AttitudeModeVectorPointing(new VectorF3D(x, y, z),
            margin), this);
    }

    @Action(description = "Changes the spacecraft's attitude to inertial pointing mode")
    public UInteger adcs_InertialPointingMode(Long actionInstanceObjId, boolean reportProgress,
        MALInteraction interaction, @ActionParameter(name = "Hold Duration", rawUnit = "seconds") Duration holdDuration,
        @ActionParameter(name = "losX", rawUnit = "degree") float losx, @ActionParameter(name = "losY",
                                                                                         rawUnit = "degree") float losy,
        @ActionParameter(name = "losZ", rawUnit = "degree") float losz, @ActionParameter(name = "flightX",
                                                                                         rawUnit = "degree") float flightx,
        @ActionParameter(name = "flightY", rawUnit = "degree") float flighty, @ActionParameter(name = "flightZ",
                                                                                               rawUnit = "degree") float flightz,
        @ActionParameter(name = "quatA", rawUnit = "") float quatA, @ActionParameter(name = "quatB",
                                                                                     rawUnit = "") float quatB,
        @ActionParameter(name = "quatC", rawUnit = "") float quatC, @ActionParameter(name = "quatD",
                                                                                     rawUnit = "") float quatD) {
        return actionsHandler.executeAdcsModeAction(holdDuration, new AttitudeModeInertialPointing(new VectorF3D(losx,
            losy, losz), new VectorF3D(flightx, flighty, flightz), new Quaternion(quatA, quatB, quatC, quatD)), this);
    }

    @Action(description = "Changes the spacecraft's attitude to BDot mode")
    public UInteger adcs_BDotMode(Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction,
        @ActionParameter(name = "Hold Duration", rawUnit = "seconds") Duration holdDuration) {
        return actionsHandler.executeAdcsModeAction(holdDuration, new AttitudeModeBDot(), this);
    }

    @Action(description = "Changes the spacecraft's attitude to target tracking mode")
    public UInteger adcs_TargetTrackingMode(Long actionInstanceObjId, boolean reportProgress,
        MALInteraction interaction, @ActionParameter(name = "Hold Duration", rawUnit = "seconds") Duration holdDuration,
        @ActionParameter(name = "Latitude", rawUnit = "degree") Float latitude, @ActionParameter(name = "Longitude",
                                                                                                 rawUnit = "degree") Float longitude) {
        return actionsHandler.executeAdcsModeAction(holdDuration, new AttitudeModeTargetTracking(latitude, longitude),
            this);
    }

    @Action(description = "Changes the spacecraft's attitude to single spinning mode")
    public UInteger adcs_SingleSpinningMode(Long actionInstanceObjId, boolean reportProgress,
        MALInteraction interaction, @ActionParameter(name = "Hold Duration", rawUnit = "seconds") Duration holdDuration,
        @ActionParameter(name = "Body Axis X") Float bodyAxisX, @ActionParameter(name = "Body Axis Y") Float bodyAxisY,
        @ActionParameter(name = "Body Axis Z") Float bodyAxisZ, @ActionParameter(
                                                                                 name = "Angular Velocity") Float angularVelocity) {
        return actionsHandler.executeAdcsModeAction(holdDuration, new AttitudeModeSingleSpinning(new VectorF3D(
            bodyAxisX, bodyAxisY, bodyAxisZ), angularVelocity), this);
    }

    @Action(description = "Unsets the spacecraft's attitude.")
    public UInteger adcs_UnsetAttitude(Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {
        return actionsHandler.executeAdcsModeAction(null, null, this);
    }

    @Action(description = "Schedule JPG picture acquisition.")
    public UInteger scheduleTakePictureJPG(Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction,
        @ActionParameter(name = "Execution delay") Duration acquisitionDelay) {
        return actionsHandler.scheduleTakePicture(actionInstanceObjId, reportProgress, interaction, acquisitionDelay,
            PictureFormat.JPG, false);
    }

    @Action(description = "Schedule JPG picture acquisition.")
    public UInteger scheduleTakePictureAutoExposedJPG(Long actionInstanceObjId, boolean reportProgress,
        MALInteraction interaction, @ActionParameter(name = "Execution delay") Duration acquisitionDelay) {
        return actionsHandler.scheduleTakePicture(actionInstanceObjId, reportProgress, interaction, acquisitionDelay,
            PictureFormat.JPG, true);
    }

    @Action(description = "Uses the NMF Camera service to take a picture in RAW format.",
            stepCount = PayloadsTestActionsHandler.TOTAL_STAGES)
    public UInteger takePicture_RAW(Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {
        return actionsHandler.takePicture(actionInstanceObjId, reportProgress, interaction, PictureFormat.RAW);
    }

    @Action(description = "Uses the NMF Camera service to take a picture in JPG format.",
            stepCount = PayloadsTestActionsHandler.TOTAL_STAGES)
    public UInteger takePicture_JPG(Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {
        return actionsHandler.takePicture(actionInstanceObjId, reportProgress, interaction, PictureFormat.JPG);
    }

    @Action(description = "Uses the NMF Camera service to take a picture in BMP format.",
            stepCount = PayloadsTestActionsHandler.TOTAL_STAGES)
    public UInteger takePicture_BMP(Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {
        return actionsHandler.takePicture(actionInstanceObjId, reportProgress, interaction, PictureFormat.BMP);
    }

    @Action(description = "Uses the NMF Camera service to take an auto exposed picture in RAW format.",
            stepCount = PayloadsTestActionsHandler.TOTAL_STAGES)
    public UInteger takeAutoExposedPicture_RAW(Long actionInstanceObjId, boolean reportProgress,
        MALInteraction interaction) {
        return actionsHandler.takeAutoExposedPicture(actionInstanceObjId, reportProgress, interaction,
            PictureFormat.RAW);
    }

    @Action(description = "Uses the NMF Camera service to take an auto exposed picture in JPG format.",
            stepCount = PayloadsTestActionsHandler.TOTAL_STAGES)
    public UInteger takeAutoExposedPicture_JPG(Long actionInstanceObjId, boolean reportProgress,
        MALInteraction interaction) {
        return actionsHandler.takeAutoExposedPicture(actionInstanceObjId, reportProgress, interaction,
            PictureFormat.JPG);
    }

    @Action(description = "Uses the NMF Camera service to take an auto exposed picture in BMP format.",
            stepCount = PayloadsTestActionsHandler.TOTAL_STAGES)
    public UInteger takeAutoExposedPicture_BMP(Long actionInstanceObjId, boolean reportProgress,
        MALInteraction interaction) {
        return actionsHandler.takeAutoExposedPicture(actionInstanceObjId, reportProgress, interaction,
            PictureFormat.BMP);
    }

    @Action(description = "Use NMF PowerControl to switch a device On.")
    public UInteger powerOnDevice(Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction,
        @ActionParameter(name = "DeviceType") UInteger deviceType) {
        return actionsHandler.setDeviceState(actionInstanceObjId, reportProgress, interaction, deviceType, true);
    }

    @Action(description = "Use NMF PowerControl to switch a device Off.")
    public UInteger powerOffDevice(Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction,
        @ActionParameter(name = "DeviceType") UInteger deviceType) {
        return actionsHandler.setDeviceState(actionInstanceObjId, reportProgress, interaction, deviceType, false);
    }

    @Action(description = "Record Optical RX samples.")
    public UInteger recordOptRXData(Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {
        try {
            nmf.getPlatformServices().getOpticalDataReceiverService().recordSamples(new Duration(5),
                new PayloadsTestOpticalDataHandler());
            return null; // Success!
        } catch (MALInteractionException | MALException | IOException | NMFException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return new UInteger(1);
        }
    }

    @Action(description = "Record SDR samples.")
    public UInteger recordSDRData(Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {
        return actionsHandler.recordSDRData(actionInstanceObjId, reportProgress, interaction);
    }

    //-----------------------------------------------------------------------------------------------

    public void startAdcsAttitudeMonitoring() {
        try {
            // Subscribe monitorAttitude
            nmf.getPlatformServices().getAutonomousADCSService().monitorAttitudeRegister(ConnectionConsumer
                .subscriptionWildcard(), new ADCSDataHandler());
            nmf.getPlatformServices().getAutonomousADCSService().enableMonitoring(true, ATTITUDE_MONITORING_INTERVAL);
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

    public void setSimpleCommandingInterface(SimpleCommandingInterface arg) {
        simpleCommandingInterface = arg;
    }
}
