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
package esa.mo.nmf.apps;

import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.mc.impl.provider.ParameterInstance;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
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
import org.ccsds.moims.mo.mal.structures.Time;
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
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.AttributeValue;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ConditionalConversion;
import org.ccsds.moims.mo.mc.structures.ConditionalConversionList;
import org.ccsds.moims.mo.mc.structures.ParameterExpression;
import org.ccsds.moims.mo.platform.autonomousadcs.consumer.AutonomousADCSAdapter;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionList;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionNadirPointing;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionNadirPointingList;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionSunPointing;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionSunPointingList;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeInstance;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeInstanceNadirPointing;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeInstanceSunPointing;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeMode;
import org.ccsds.moims.mo.platform.gps.body.GetLastKnownPositionResponse;
import org.ccsds.moims.mo.platform.gps.consumer.GPSAdapter;
import org.ccsds.moims.mo.platform.magnetometer.structures.MagneticFieldInstance;
import org.ccsds.moims.mo.platform.structures.Quaternions;
import org.ccsds.moims.mo.platform.structures.Vector3D;
import org.ccsds.moims.mo.platform.structures.WheelSpeed;
import esa.mo.nmf.NMFInterface;

/**
 * The adapter for the app
 */
public class MCTriplePresentationAdapter extends MonitorAndControlNMFAdapter {

    private NMFInterface nmf;

    private static final String PARAMETER_ADCS_MODE = "ADCS.ModeOperation";
    private static final String PARAMETER_GPS_LATITUDE = "GPS.Latitude";
    private static final String PARAMETER_GPS_LONGITUDE = "GPS.Longitude";
    private static final String PARAMETER_GPS_ALTITUDE = "GPS.Altitude";
    private static final String PARAMETER_MAG_X = "Magnetometer.X";
    private static final String PARAMETER_MAG_Y = "Magnetometer.Y";
    private static final String PARAMETER_MAG_Z = "Magnetometer.Z";
    private static final String PARAMETER_GPS_ELAPSED_TIME = "GPS.ElapsedTime";
    private static final String PARAMETER_GPS_N_SATS_IN_VIEW = "GPS.NumberOfSatellitesInView";

    private static final String AGGREGATION_GPS = "GPS.Aggregation";
    private static final String AGGREGATION_MAG = "Magnetometer.Aggregation";

    private static final String ACTION_SUN_POINTING_MODE = "ADCS.SunPointingMode";
    private static final String ACTION_NADIR_POINTING_MODE = "ADCS.NadirPointingMode";
    private static final String ACTION_UNSET = "ADCS.UnsetAttitude";
    private static final String ACTION_5_STAGES = "5StagesAction";

    private boolean adcsDefsAdded = false;
    private Long sunPointingObjId = null;
    private Long nadirPointingObjId = null;
    private Timer timer;

    public void setNMF(NMFInterface nanosatmoframework) {
        this.nmf = nanosatmoframework;

        this.timer = new Timer();

        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                AttributeValueList atts = new AttributeValueList();
                AttributeValue att = new AttributeValue(new Union("Hello from the other side!"));
                atts.add(att);

                try {
                    nmf.publishAlertEvent("10SecondsAlert", atts);
                } catch (NMFException ex) {
                    Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, 0, 10 * 1000); // 10 seconds

    }

    @Override
    public void initialRegistrations(MCRegistration registration) {

        registration.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

        // ======================================================================= 
        PairList mappings = new PairList();
        mappings.add(new Pair(new UOctet((short) AttitudeMode.BDOT.getOrdinal()), new Union("BDOT")));
        mappings.add(new Pair(new UOctet((short) AttitudeMode.SUNPOINTING.getOrdinal()), new Union("SUNPOINTING_INDEX")));
        mappings.add(new Pair(new UOctet((short) AttitudeMode.SINGLESPINNING.getOrdinal()), new Union("SINGLESPINNING")));
        mappings.add(new Pair(new UOctet((short) AttitudeMode.TARGETTRACKING.getOrdinal()), new Union("TARGETTRACKING")));
        mappings.add(new Pair(new UOctet((short) AttitudeMode.NADIRPOINTING.getOrdinal()), new Union("NADIRPOINTING")));

        DiscreteConversionDetails conversion = new DiscreteConversionDetails(mappings);

        DiscreteConversionDetailsList conversions = new DiscreteConversionDetailsList();
        conversions.add(conversion);

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
        } catch (Throwable ex) {
            // ooops, ignore the parameter conversion
        }

        // ------------------ Parameters ------------------
        ParameterDefinitionDetailsList defsOther = new ParameterDefinitionDetailsList();
        IdentifierList paramOtherNames = new IdentifierList();
        ParameterDefinitionDetailsList defsGPS = new ParameterDefinitionDetailsList();
        IdentifierList paramGPSNames = new IdentifierList();
        ParameterDefinitionDetailsList defsMag = new ParameterDefinitionDetailsList();
        IdentifierList paramMagNames = new IdentifierList();

        defsOther.add(new ParameterDefinitionDetails(
                "The ADCS mode of operation",
                Union.UOCTET_SHORT_FORM.byteValue(),
                "",
                false,
                new Duration(0),
                null,
                paramConversion
        ));
        paramOtherNames.add(new Identifier(PARAMETER_ADCS_MODE));

        defsOther.add(new ParameterDefinitionDetails(
                "The number of satellites in view of GPS receiver.",
                Union.INTEGER_SHORT_FORM.byteValue(),
                "sats",
                false,
                new Duration(4),
                null,
                null
        ));
        paramOtherNames.add(new Identifier(PARAMETER_GPS_N_SATS_IN_VIEW));

        // Create the GPS.Latitude
        defsGPS.add(new ParameterDefinitionDetails(
                "The GPS Latitude",
                Union.DOUBLE_TYPE_SHORT_FORM.byteValue(),
                "degrees",
                false,
                new Duration(2),
                null,
                null
        ));
        paramGPSNames.add(new Identifier(PARAMETER_GPS_LATITUDE));

        // Create the GPS.Longitude
        defsGPS.add(new ParameterDefinitionDetails(
                "The GPS Longitude",
                Union.DOUBLE_TYPE_SHORT_FORM.byteValue(),
                "degrees",
                false,
                new Duration(2),
                null,
                null
        ));
        paramGPSNames.add(new Identifier(PARAMETER_GPS_LONGITUDE));

        // Create the GPS.Altitude
        defsGPS.add(new ParameterDefinitionDetails(
                "The GPS Altitude",
                Union.DOUBLE_TYPE_SHORT_FORM.byteValue(),
                "meters",
                false,
                new Duration(2),
                null,
                null
        ));
        paramGPSNames.add(new Identifier(PARAMETER_GPS_ALTITUDE));

        // Create the Magnetometer.X
        defsMag.add(new ParameterDefinitionDetails(
                "The Magnetometer X component",
                Union.DOUBLE_TYPE_SHORT_FORM.byteValue(),
                "microTesla",
                false,
                new Duration(2),
                null,
                null
        ));
        paramMagNames.add(new Identifier(PARAMETER_MAG_X));

        // Create the Magnetometer.Y
        defsMag.add(new ParameterDefinitionDetails(
                "The Magnetometer Y component",
                Union.DOUBLE_TYPE_SHORT_FORM.byteValue(),
                "microTesla",
                false,
                new Duration(2),
                null,
                null
        ));
        paramMagNames.add(new Identifier(PARAMETER_MAG_Y));

        // Create the Magnetometer.Z
        defsMag.add(new ParameterDefinitionDetails(
                "The Magnetometer Z component",
                Union.DOUBLE_TYPE_SHORT_FORM.byteValue(),
                "microTesla",
                false,
                new Duration(2),
                null,
                null
        ));
        paramMagNames.add(new Identifier(PARAMETER_MAG_Z));

        registration.registerParameters(paramOtherNames, defsOther);
        LongList parameterObjIdsGPS = registration.registerParameters(paramGPSNames, defsGPS);
        LongList parameterObjIdsMag = registration.registerParameters(paramMagNames, defsMag);

        // ------------------ Aggregations ------------------
        AggregationDefinitionDetailsList aggs = new AggregationDefinitionDetailsList();
        IdentifierList aggNames = new IdentifierList();

        // Create the Aggregation GPS
        AggregationDefinitionDetails defGPSAgg = new AggregationDefinitionDetails(
                "Aggregates: GPS Latitude, GPS Longitude, GPS Altitude.",
                new UOctet((short) AggregationCategory.GENERAL.getOrdinal()),
                new Duration(10),
                true,
                false,
                false,
                new Duration(20),
                false,
                new AggregationParameterSetList()
        );
        aggNames.add(new Identifier(AGGREGATION_GPS));

        defGPSAgg.getParameterSets().add(new AggregationParameterSet(
                null,
                parameterObjIdsGPS,
                new Duration(3),
                null
        ));

        // Create the Aggregation Magnetometer
        AggregationDefinitionDetails defMagAgg = new AggregationDefinitionDetails(
                "Aggregates Magnetometer components: X, Y, Z.",
                new UOctet((short) AggregationCategory.GENERAL.getOrdinal()),
                new Duration(10),
                true,
                false,
                false,
                new Duration(20),
                false,
                new AggregationParameterSetList()
        );
        aggNames.add(new Identifier(AGGREGATION_MAG));

        defMagAgg.getParameterSets().add(new AggregationParameterSet(
                null,
                parameterObjIdsMag,
                new Duration(3),
                null
        ));

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

            arguments1.add(new ArgumentDefinitionDetails(rawType, rawUnit, conditionalConversions, convertedType, convertedUnit));
        }

        ActionDefinitionDetails actionDef1 = new ActionDefinitionDetails(
                "Changes the spacecraft's attitude to sun pointing mode.",
                new UOctet((short) 0),
                new UShort(0),
                arguments1,
                null
        );
        actionNames.add(new Identifier(ACTION_SUN_POINTING_MODE));
            
        ActionDefinitionDetails actionDef2 = new ActionDefinitionDetails(
                "Changes the spacecraft's attitude to nadir pointing mode.",
                new UOctet((short) 0),
                new UShort(0),
                //                arguments2,
                arguments1,
                null
        );
        actionNames.add(new Identifier(ACTION_NADIR_POINTING_MODE));

        ActionDefinitionDetails actionDef3 = new ActionDefinitionDetails(
                "Unsets the spacecraft's attitude.",
                new UOctet((short) 0),
                new UShort(0),
                //                detailsList,
                new ArgumentDefinitionDetailsList(),
                null
        );
        actionNames.add(new Identifier(ACTION_UNSET));

        ActionDefinitionDetails actionDef4 = new ActionDefinitionDetails(
                "Example of an Action with 5 stages.",
                new UOctet((short) 0),
                new UShort(5),
                //                detailsList,
                new ArgumentDefinitionDetailsList(),
                null
        );
        actionNames.add(new Identifier(ACTION_5_STAGES));

        actionDefs.add(actionDef1);
        actionDefs.add(actionDef2);
        actionDefs.add(actionDef3);
        actionDefs.add(actionDef4);
        LongList actionObjIds = registration.registerActions(actionNames, actionDefs);
    }

    @Override
    public Attribute onGetValue(Identifier identifier, Byte rawType) {
        if (nmf == null) {
            return null;
        }

        try {
            if (PARAMETER_GPS_N_SATS_IN_VIEW.equals(identifier.getValue())) {
                final Semaphore sem = new Semaphore(0);
                final IntegerList nOfSats = new IntegerList();

                class AdapterImpl extends GPSAdapter {

                    @Override
                    public void getSatellitesInfoResponseReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.platform.gps.structures.SatelliteInfoList gpsSatellitesInfo, java.util.Map qosProperties) {
                        nOfSats.add(gpsSatellitesInfo.size());
                        sem.release();
                    }
                }

                try {
                    nmf.getPlatformServices().getGPSService().getSatellitesInfo(new AdapterImpl());
                } catch (IOException ex) {
                    Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NMFException ex) {
                    Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                }

                try {
                    sem.acquire();
                } catch (InterruptedException ex) {
                    Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                }

                return (Attribute) HelperAttributes.javaType2Attribute(nOfSats.get(0));
            }

            if (PARAMETER_GPS_LATITUDE.equals(identifier.getValue())
                    || PARAMETER_GPS_LONGITUDE.equals(identifier.getValue())
                    || PARAMETER_GPS_ALTITUDE.equals(identifier.getValue())
                    || PARAMETER_GPS_ELAPSED_TIME.equals(identifier.getValue())) {
                GetLastKnownPositionResponse pos;

                try {
                    pos = nmf.getPlatformServices().getGPSService().getLastKnownPosition();

                    if (PARAMETER_GPS_LATITUDE.equals(identifier.getValue())) {
                        return (Attribute) HelperAttributes.javaType2Attribute(pos.getBodyElement0().getLatitude());
                    }

                    if (PARAMETER_GPS_LONGITUDE.equals(identifier.getValue())) {
                        return (Attribute) HelperAttributes.javaType2Attribute(pos.getBodyElement0().getLongitude());
                    }

                    if (PARAMETER_GPS_ALTITUDE.equals(identifier.getValue())) {
                        return (Attribute) HelperAttributes.javaType2Attribute(pos.getBodyElement0().getAltitude());
                    }

                    if (PARAMETER_GPS_ELAPSED_TIME.equals(identifier.getValue())) {
                        return pos.getBodyElement1();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NMFException ex) {
                    Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (PARAMETER_MAG_X.equals(identifier.getValue())
                    || PARAMETER_MAG_Y.equals(identifier.getValue())
                    || PARAMETER_MAG_Z.equals(identifier.getValue())) {
                MagneticFieldInstance magField;
                try {
                    magField = nmf.getPlatformServices().getMagnetometerService().getMagneticField();

                    if (PARAMETER_MAG_X.equals(identifier.getValue())) {
                        return (Attribute) HelperAttributes.javaType2Attribute(magField.getX());
                    }

                    if (PARAMETER_MAG_Y.equals(identifier.getValue())) {
                        return (Attribute) HelperAttributes.javaType2Attribute(magField.getY());
                    }

                    if (PARAMETER_MAG_Z.equals(identifier.getValue())) {
                        return (Attribute) HelperAttributes.javaType2Attribute(magField.getZ());
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NMFException ex) {
                    Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } catch (MALException ex) {
            Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
        return false;  // to confirm that the variable was set
    }

    /**
     * The user must implement this interface in order to link a certain
     * action Identifier to the method on the application
     *
     * @param identifier Name of the Parameter
     * @param attributeValues
     * @param actionInstanceObjId
     * @param reportProgress Determines if it is necessary to report the execution
     * @param interaction The interaction object progress of the action
     *
     * @return Returns null if the Action was successful. If not null, then the
     * returned value should hold the error number
     */
    @Override
    public UInteger actionArrived(Identifier name, AttributeValueList attributeValues,
            Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {

        if (nmf == null) {
            return new UInteger(0);
        }

        if (ACTION_SUN_POINTING_MODE.equals(name.getValue())) {
            synchronized (this) {
                if (!adcsDefsAdded) {
                    this.prepareADCSServiceForApp();
                }
            }

            try {
                Attribute argValue = attributeValues.get(0).getValue();

                // Negative Durations are not allowed!
                if (((Duration) argValue).getValue() < 0) {
                    return new UInteger(123);
                }

                System.out.println(ACTION_SUN_POINTING_MODE + " with value is ["
                        + esa.mo.helpertools.helpers.HelperAttributes.attribute2string(argValue) + "]");

                nmf.getPlatformServices().getAutonomousADCSService().setDesiredAttitude(sunPointingObjId, (Duration) argValue, new Duration(2));
            } catch (MALInteractionException ex) {
                Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                return new UInteger(0);
            } catch (IOException ex) {
                Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALException ex) {
                Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                return new UInteger(0);
            } catch (NMFException ex) {
                Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                return new UInteger(0);
            }

        }

        if (ACTION_NADIR_POINTING_MODE.equals(name.getValue())) {
            synchronized (this) {
                if (!adcsDefsAdded) {
                    this.prepareADCSServiceForApp();
                }
            }

            try {
                Attribute argValue = attributeValues.get(0).getValue();

                // Negative Durations are not allowed!
                if (((Duration) argValue).getValue() < 0) {
                    return new UInteger(123);
                }

                System.out.println(ACTION_NADIR_POINTING_MODE + " with value is [" + esa.mo.helpertools.helpers.HelperAttributes.attribute2string(argValue) + "]");
                nmf.getPlatformServices().getAutonomousADCSService().setDesiredAttitude(nadirPointingObjId, (Duration) argValue, new Duration(2));
            } catch (MALInteractionException ex) {
                Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                return new UInteger(0);
            } catch (IOException ex) {
                Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALException ex) {
                Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                return new UInteger(0);
            } catch (NMFException ex) {
                Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                return new UInteger(0);
            }
        }

        if (ACTION_UNSET.equals(name.getValue())) {
            synchronized (this) {
                if (!adcsDefsAdded) {
                    this.prepareADCSServiceForApp();
                }
            }

            try {
                // if PUSH_USING_PARAMETER_SERVICE
                pushAdcsModeParam(AttitudeMode.BDOT);
                // else
                //try {
                //nmf.pushParameterValue(PARAMETER_ADCS_MODE, new UOctet((short) AttitudeMode.BDOT.getOrdinal()));
                //} catch (IOException ex) {
                //Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                //}
                // endif

                System.out.println(ACTION_UNSET + " was called");
                nmf.getPlatformServices().getAutonomousADCSService().unsetAttitude();
            } catch (MALInteractionException ex) {
                Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                return new UInteger(0);
            } catch (IOException ex) {
                Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALException ex) {
                Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                return new UInteger(0);
            } catch (NMFException ex) {
                Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                return new UInteger(0);
            }
        }

        if (ACTION_5_STAGES.equals(name.getValue())) {
            try {
                return multiStageAction(actionInstanceObjId, 5);
            } catch (NMFException ex) {
                Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                return new UInteger(0);
            }
        }

        return null;  // Action service not integrated
    }

    private void prepareADCSServiceForApp() {
        AttitudeDefinitionList nadirDefs = new AttitudeDefinitionNadirPointingList();
        AttitudeDefinitionNadirPointing nadirDef = new AttitudeDefinitionNadirPointing();
        nadirDef.setName(new Identifier("NadirPointing"));
        nadirDef.setDescription("A definition pointing to Nadir");
        nadirDefs.add(nadirDef);

        AttitudeDefinitionSunPointingList sunDefs = new AttitudeDefinitionSunPointingList();
        AttitudeDefinitionSunPointing sunDef = new AttitudeDefinitionSunPointing();
        sunDef.setName(new Identifier("SunPointing"));
        sunDef.setDescription("A definition pointing to the sun");
        sunDefs.add(sunDef);

        try {
            IdentifierList names = new IdentifierList();
            names.add(sunDef.getName());
            names.add(nadirDef.getName());
            LongList objIds = nmf.getPlatformServices().getAutonomousADCSService().listAttitudeDefinition(names);
            sunPointingObjId = objIds.get(0);
            nadirPointingObjId = objIds.get(1);

            if (sunPointingObjId == null) { // It does not exist
                LongList sunObj = nmf.getPlatformServices().getAutonomousADCSService().addAttitudeDefinition(sunDefs);
                sunPointingObjId = sunObj.get(0);
            }

            if (nadirPointingObjId == null) { // It does not exist
                LongList nadirObj = nmf.getPlatformServices().getAutonomousADCSService().addAttitudeDefinition(nadirDefs);
                nadirPointingObjId = nadirObj.get(0);
            }

        } catch (IOException ex) {
            Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            if (ex.getStandardError().getErrorNumber().equals(COMHelper.DUPLICATE_ERROR_NUMBER)) {
                Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.INFO, "The Attitude Definition already exists!");
            } else {
                Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (MALException ex) {
            Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NMFException ex) {
            Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            // Subscribe monitorAttitude
            nmf.getPlatformServices().getAutonomousADCSService().monitorAttitudeRegister(ConnectionConsumer.subscriptionWildcard(), new DataReceivedAdapter());
        } catch (IOException ex) {
            Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
            Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NMFException ex) {
            Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        adcsDefsAdded = true;
    }

    public class DataReceivedAdapter extends AutonomousADCSAdapter {

        @Override
        public void monitorAttitudeNotifyReceived(final MALMessageHeader msgHeader,
                final Identifier lIdentifier, final UpdateHeaderList lUpdateHeaderList,
                final org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeInstanceList attitudeInstanceList,
                final Map qosp) {

            if (attitudeInstanceList.size() == lUpdateHeaderList.size()) {
                for (int i = 0; i < lUpdateHeaderList.size(); i++) {

                    AttitudeInstance attitudeInstance = (AttitudeInstance) attitudeInstanceList.get(i);
                    Long mode = lUpdateHeaderList.get(i).getKey().getThirdSubKey();
                    AttitudeMode attMode = AttitudeMode.fromNumericValue(new UInteger(mode));

                    // Sun Pointing
                    if (attitudeInstance instanceof AttitudeInstanceSunPointing) {
                        Vector3D sunVector = ((AttitudeInstanceSunPointing) attitudeInstance).getSunVector();
                        WheelSpeed wheelSpeed = ((AttitudeInstanceSunPointing) attitudeInstance).getWheelSpeed();

                        try {
                            // if PUSH_USING_PARAMETER_SERVICE
                            pushAdcsModeParam(attMode);
                            // else
                            //     nmf.pushParameterValue(PARAMETER_ADCS_MODE, attMode);
                            // endif
                            if (sunVector != null) {
                                nmf.pushParameterValue("sunVector3D_X", sunVector.getX());
                                nmf.pushParameterValue("sunVector3D_Y", sunVector.getY());
                                nmf.pushParameterValue("sunVector3D_Z", sunVector.getZ());
                            }

                        } catch (NMFException ex) {
                            Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    // Nadir Pointing
                    if (attitudeInstance instanceof AttitudeInstanceNadirPointing) {
                        Vector3D positionVector = ((AttitudeInstanceNadirPointing) attitudeInstance).getPositionVector();
                        Quaternions quaternions = ((AttitudeInstanceNadirPointing) attitudeInstance).getCurrentQuaternions();
                        attMode = AttitudeMode.NADIRPOINTING;

                        try {
                            // if PUSH_USING_PARAMETER_SERVICE
                            pushAdcsModeParam(attMode);
                            // else
                            //     nmf.pushParameterValue(PARAMETER_ADCS_MODE, new UOctet((short) attMode.getOrdinal()));
                            // endif
                            nmf.pushParameterValue("positionVector3D_X", positionVector.getX());
                            nmf.pushParameterValue("positionVector3D_Y", positionVector.getY());
                            nmf.pushParameterValue("positionVector3D_Z", positionVector.getZ());

                            nmf.pushParameterValue("quaternion1", quaternions.getQ1());
                            nmf.pushParameterValue("quaternion2", quaternions.getQ2());
                            nmf.pushParameterValue("quaternion3", quaternions.getQ3());
                            nmf.pushParameterValue("quaternion4", quaternions.getQ4());
                        } catch (NMFException ex) {
                            Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                }
            }
        }
    }

    // push a sample of the ADCS Mode using the Parameter Service
    private void pushAdcsModeParam(AttitudeMode attMode) throws NMFException {
        Identifier name = new Identifier(PARAMETER_ADCS_MODE);
        ObjectId source = null;
        UOctet validityState = new UOctet((short) 0);
        Attribute rawValue = new UOctet((short) attMode.getOrdinal());
        Attribute convertedValue = new Identifier(attMode.toString());

        ParameterValue pValue = new ParameterValue(validityState, rawValue, convertedValue);

        final ArrayList<ParameterInstance> instances = new ArrayList<ParameterInstance>();
        instances.add(new ParameterInstance(name, pValue, null, new Time(System.currentTimeMillis())));

        nmf.getMCServices().getParameterService().pushMultipleParameterValues(instances);
    }

    /*
     * @param actionInstanceObjId
     * @param total_n_of_stages
     *
     * @return Returns null if the Action was successful. If not null, then the
     * returned value should hold the error number
     */
    public UInteger multiStageAction(Long actionInstanceObjId, int total_n_of_stages) throws NMFException {
        final int sleepTime = 2; // 2 seconds

        UInteger errorNumber = null;

        for (int stage = 1; stage < total_n_of_stages + 1; stage++) {
            nmf.reportActionExecutionProgress(true, 0, stage, total_n_of_stages, actionInstanceObjId);

            try {
                Thread.sleep(sleepTime * 1000); //1000 milliseconds multiplier.
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        return errorNumber;
    }

}
