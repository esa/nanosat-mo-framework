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
package esa.mo.nanosatmoframework.apps;

import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.mc.impl.provider.ParameterInstance;
import esa.mo.nanosatmoframework.MCRegistration;
import esa.mo.nanosatmoframework.MonitorAndControlNMFAdapter;
import esa.mo.nanosatmoframework.NanoSatMOFrameworkInterface;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.IntegerList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
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
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterAdapter;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValueList;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.Severity;
import org.ccsds.moims.mo.platform.autonomousadcs.consumer.AutonomousADCSAdapter;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionList;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionNadirPointing;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionNadirPointingList;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionSunPointing;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinitionSunPointingList;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeInstance;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeInstanceNadirPointing;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeInstanceSunPointing;
import org.ccsds.moims.mo.platform.gps.body.GetLastKnownPositionResponse;
import org.ccsds.moims.mo.platform.gps.consumer.GPSAdapter;
import org.ccsds.moims.mo.platform.structures.Quaternions;
import org.ccsds.moims.mo.platform.structures.Vector3D;
import org.ccsds.moims.mo.platform.structures.WheelSpeed;

/**
 * The adapter for the app
 */
public class MCTriplePresentationAdapter extends MonitorAndControlNMFAdapter {

    private NanoSatMOFrameworkInterface nmf;

    private static final String PARAMETER_GPS_LATITUDE = "GPS.Latitude";
    private static final String PARAMETER_GPS_LONGITUDE = "GPS.Longitude";
    private static final String PARAMETER_GPS_ALTITUDE = "GPS.Altitude";
    private static final String PARAMETER_GPS_ELAPSED_TIME = "GPS.ElapsedTime";
    private static final String PARAMETER_GPS_N_SATS_IN_VIEW = "GPS.NumberOfSatellitesInView";
    private static final String AGGREGATION_GPS = "GPS.Aggregation";
    private static final String ACTION_SUN_POINTING_MODE = "ADCS.SunPointingMode";
    private static final String ACTION_NADIR_POINTING_MODE = "ADCS.NadirPointingMode";

    private boolean adcsDefsAdded = false;
    private Long sunPointingObjId = null;
    private Long nadirPointingObjId = null;

    public void setNMF(NanoSatMOFrameworkInterface nmf) {
        this.nmf = nmf;
    }

    @Override
    public void initialRegistrations(MCRegistration registration) {

        registration.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

        // Create the GPS.Latitude
        ParameterDefinitionDetails defLatitude = new ParameterDefinitionDetails(
                new Identifier(PARAMETER_GPS_LATITUDE),
                "The GPS Latitude",
                Union.DOUBLE_TYPE_SHORT_FORM.byteValue(),
                "degrees",
                false,
                new Duration(2),
                null,
                null
        );

        // Create the GPS.Longitude
        ParameterDefinitionDetails defLongitude = new ParameterDefinitionDetails(
                new Identifier(PARAMETER_GPS_LONGITUDE),
                "The GPS Longitude",
                Union.DOUBLE_TYPE_SHORT_FORM.byteValue(),
                "degrees",
                false,
                new Duration(2),
                null,
                null
        );

        // Create the GPS.Altitude
        ParameterDefinitionDetails defAltitude = new ParameterDefinitionDetails(
                new Identifier(PARAMETER_GPS_ALTITUDE),
                "The GPS Altitude",
                Union.DOUBLE_TYPE_SHORT_FORM.byteValue(),
                "degrees",
                false,
                new Duration(2),
                null,
                null
        );

        ParameterDefinitionDetailsList defs = new ParameterDefinitionDetailsList();
        defs.add(defLatitude);
        defs.add(defLongitude);
        defs.add(defAltitude);
        LongList parameterObjIds = registration.registerParameters(defs);

        // Create the Aggregation GPS
        AggregationDefinitionDetails defGPSAgg = new AggregationDefinitionDetails(
                new Identifier(AGGREGATION_GPS),
                "Aggregates: GPS Latitude, GPS Longitude, GPS Altitude.",
                AggregationCategory.GENERAL,
                true,
                new Duration(10),
                false,
                new Duration(20),
                new AggregationParameterSetList()
        );

        defGPSAgg.getParameterSets().add(new AggregationParameterSet(
                null,
                parameterObjIds,
                new Duration(3),
                null
        ));

        AggregationDefinitionDetailsList aggs = new AggregationDefinitionDetailsList();
        aggs.add(defGPSAgg);
        registration.registerAggregations(aggs);

        ActionDefinitionDetails actionDef1 = new ActionDefinitionDetails(
                new Identifier(ACTION_SUN_POINTING_MODE),
                "Changes the spacecraft's attitude to sun pointing mode.",
                Severity.INFORMATIONAL,
                new UShort(0),
                new ArgumentDefinitionDetailsList(),
                null
        );

        ActionDefinitionDetails actionDef2 = new ActionDefinitionDetails(
                new Identifier(ACTION_NADIR_POINTING_MODE),
                "Changes the spacecraft's attitude to nadir pointing mode.",
                Severity.INFORMATIONAL,
                new UShort(0),
                new ArgumentDefinitionDetailsList(),
                null
        );

        ActionDefinitionDetailsList actionDefs = new ActionDefinitionDetailsList();
        actionDefs.add(actionDef1);
        actionDefs.add(actionDef2);
        LongList actionObjIds = registration.registerActions(actionDefs);

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

                nmf.getPlatformServices().getGPSService().getSatellitesInfo(new AdapterImpl());

                try {
                    sem.acquire();
                } catch (InterruptedException ex) {
                    Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                }

                return (Attribute) HelperAttributes.javaType2Attribute(nOfSats.get(0));
            }

            GetLastKnownPositionResponse pos = nmf.getPlatformServices().getGPSService().getLastKnownPosition();

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

        } catch (MALException ex) {
            Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public Boolean onSetValue(Identifier identifier, Attribute value) {
        /*
        if ("parameterX".equals(identifier.toString())) { // parameterX was called?
            parameterX = value.toString();
            return true;
        }
         */
        return false;  // to confirm that the variable was set
    }

    @Override
    public UInteger actionArrived(Identifier name, AttributeValueList attributeValues, Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {

        if (nmf == null) {
            return new UInteger(0);
        }

        if (ACTION_SUN_POINTING_MODE.equals(name.getValue())) {
            synchronized (this) {
                if (!adcsDefsAdded) {
                    this.prepareADCSForApp();
                }
            }

            try {
                // Auto unset it after 10 seconds
                nmf.getPlatformServices().getAutonomousADCSService().setDesiredAttitude(sunPointingObjId, new Duration(10));
            } catch (MALInteractionException ex) {
                Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                return new UInteger(0);
            } catch (MALException ex) {
                Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                return new UInteger(0);
            }

        }

        if (ACTION_NADIR_POINTING_MODE.equals(name.getValue())) {
            synchronized (this) {
                if (!adcsDefsAdded) {
                    this.prepareADCSForApp();
                }
            }

            try {
                // Auto unset it after 10 seconds
                nmf.getPlatformServices().getAutonomousADCSService().setDesiredAttitude(nadirPointingObjId, new Duration(10));
            } catch (MALInteractionException ex) {
                Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                return new UInteger(0);
            } catch (MALException ex) {
                Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                return new UInteger(0);
            }
        }

        return null;  // Action service not integrated
    }

    public void prepareADCSForApp() {
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
            
            if (sunPointingObjId == null){ // It does not exist
                LongList sunObj = nmf.getPlatformServices().getAutonomousADCSService().addAttitudeDefinition(sunDefs);
                sunPointingObjId = sunObj.get(0);
            }

            if (nadirPointingObjId == null){ // It does not exist
                LongList nadirObj = nmf.getPlatformServices().getAutonomousADCSService().addAttitudeDefinition(nadirDefs);
                nadirPointingObjId = nadirObj.get(0);
            }

        } catch (MALInteractionException ex) {
            if (ex.getStandardError().getErrorNumber().equals(COMHelper.DUPLICATE_ERROR_NUMBER)) {
                Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.INFO, "The Attitude Definition already exists!");
            } else {
                Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (MALException ex) {
            Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            // Report every 1 second
            nmf.getPlatformServices().getAutonomousADCSService().configureMonitoring(new Duration(1));

            // Subscribe monitorAttitude
            nmf.getPlatformServices().getAutonomousADCSService().monitorAttitudeRegister(ConnectionConsumer.subscriptionWildcard(), new DataReceivedAdapter());
        } catch (MALInteractionException ex) {
            Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
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

                    // Sun Pointing
                    if (attitudeInstance instanceof AttitudeInstanceSunPointing) {
                        Vector3D sunVector = ((AttitudeInstanceSunPointing) attitudeInstance).getSunVector();
                        WheelSpeed wheelSpeed = ((AttitudeInstanceSunPointing) attitudeInstance).getWheelSpeed();

                        try {
                            nmf.pushParameterValue("sunVector3dX", sunVector.getX());
                            nmf.pushParameterValue("sunVector3dY", sunVector.getY());
                            nmf.pushParameterValue("sunVector3dZ", sunVector.getZ());

                            for (int j = 0; j < wheelSpeed.getVelocity().size(); j++) {
                                nmf.pushParameterValue("wheelSpeed_" + j, wheelSpeed.getVelocity().get(j));
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    // Nadir Pointing
                    if (attitudeInstance instanceof AttitudeInstanceNadirPointing) {
                        Vector3D positionVector = ((AttitudeInstanceNadirPointing) attitudeInstance).getPositionVector();
                        Quaternions quaternions = ((AttitudeInstanceNadirPointing) attitudeInstance).getCurrentQuaternions();

                        try {
                            nmf.pushParameterValue("positionVector3dX", positionVector.getX());
                            nmf.pushParameterValue("positionVector3dY", positionVector.getY());
                            nmf.pushParameterValue("positionVector3dZ", positionVector.getZ());

                            nmf.pushParameterValue("quaternion1", quaternions.getQ1());
                            nmf.pushParameterValue("quaternion2", quaternions.getQ2());
                            nmf.pushParameterValue("quaternion3", quaternions.getQ3());
                            nmf.pushParameterValue("quaternion4", quaternions.getQ4());
                        } catch (IOException ex) {
                            Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                }
            }
        }
    }

}
