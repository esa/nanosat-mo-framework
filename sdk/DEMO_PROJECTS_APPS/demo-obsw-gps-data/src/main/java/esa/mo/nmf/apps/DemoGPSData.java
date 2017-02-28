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

import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.NanoSatMOFrameworkInterface;
import esa.mo.nmf.provider.NanoSatMOMonolithicSim;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.Semaphore;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IntegerList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationCategory;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetails;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetailsList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSet;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSetList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.platform.gps.body.GetLastKnownPositionResponse;
import org.ccsds.moims.mo.platform.gps.consumer.GPSAdapter;

/**
 * This class provides a simple demo that allows data to be retrieved from a GPS
 * unit.
 *
 */
public class DemoGPSData {

//    private final NanoSatMOFrameworkInterface nanoSatMOFramework = new NanoSatMOConnectorImpl(new mcAdapter());
    private final NanoSatMOFrameworkInterface nanoSatMOFramework = new NanoSatMOMonolithicSim(new mcAdapter());
    private static final String PARAMETER_GPS_LATITUDE = "GPS.Latitude";
    private static final String PARAMETER_GPS_LONGITUDE = "GPS.Longitude";
    private static final String PARAMETER_GPS_ALTITUDE = "GPS.Altitude";
    private static final String PARAMETER_GPS_N_SATS_IN_VIEW = "GPS.NumberOfSatellitesInView";
    private static final String AGGREGATION_GPS = "GPS.Aggregation";

    public DemoGPSData() {

        /*
        InstanceBooleanPairList enableInstances = new InstanceBooleanPairList();
        InstanceBooleanPair enableInstance = new InstanceBooleanPair();
        enableInstance.setId((long) 0);
        enableInstance.setValue(true);
        enableInstances.add(enableInstance);
                

        
        try {
            nanoSatMOFramework.getMCServices().getParameterService().enableGeneration(false, enableInstances, null);
        } catch (MALException ex) {
            Logger.getLogger(DemoGPSData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(DemoGPSData.class.getName()).log(Level.SEVERE, null, ex);
        }
         */
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String args[]) throws Exception {
        DemoGPSData demo = new DemoGPSData();
    }

    public class mcAdapter extends MonitorAndControlNMFAdapter {

        @Override
        public void initialRegistrations(MCRegistration registrationObject) {

            registrationObject.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

            // ------------------ Parameters ------------------
            ParameterDefinitionDetailsList defsGPS = new ParameterDefinitionDetailsList();

            // Create the GPS.Latitude
            defsGPS.add(new ParameterDefinitionDetails(
                    new Identifier(PARAMETER_GPS_LATITUDE),
                    "The GPS Latitude",
                    Union.DOUBLE_TYPE_SHORT_FORM.byteValue(),
                    "degrees",
                    false,
                    new Duration(2),
                    null,
                    null
            ));

            // Create the GPS.Longitude
            defsGPS.add(new ParameterDefinitionDetails(
                    new Identifier(PARAMETER_GPS_LONGITUDE),
                    "The GPS Longitude",
                    Union.DOUBLE_TYPE_SHORT_FORM.byteValue(),
                    "degrees",
                    false,
                    new Duration(2),
                    null,
                    null
            ));

            // Create the GPS.Altitude
            defsGPS.add(new ParameterDefinitionDetails(
                    new Identifier(PARAMETER_GPS_ALTITUDE),
                    "The GPS Altitude",
                    Union.DOUBLE_TYPE_SHORT_FORM.byteValue(),
                    "meters",
                    false,
                    new Duration(2),
                    null,
                    null
            ));

            defsGPS.add(new ParameterDefinitionDetails(
                    new Identifier(PARAMETER_GPS_N_SATS_IN_VIEW),
                    "The number of satellites in view of GPS receiver.",
                    Union.INTEGER_SHORT_FORM.byteValue(),
                    "sats",
                    false,
                    new Duration(4),
                    null,
                    null
            ));

            registrationObject.registerParameters(defsGPS);
            LongList parameterObjIdsGPS = registrationObject.registerParameters(defsGPS);

            // ------------------ Aggregations ------------------
            AggregationDefinitionDetailsList aggs = new AggregationDefinitionDetailsList();

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
                    parameterObjIdsGPS,
                    new Duration(3),
                    null
            ));

            aggs.add(defGPSAgg);
            registrationObject.registerAggregations(aggs);

        }

        @Override
        public Attribute onGetValue(Identifier identifier, Byte rawType) {

            try {
                if (nanoSatMOFramework == null) {  // The framework is still not available
                    return null;
                }

                GetLastKnownPositionResponse pos;
                try {
                    pos = nanoSatMOFramework.getPlatformServices().getGPSService().getLastKnownPosition();

                    if (PARAMETER_GPS_LATITUDE.equals(identifier.getValue())) {
                        return (Attribute) HelperAttributes.javaType2Attribute(pos.getBodyElement0().getLatitude());
                    }

                    if (PARAMETER_GPS_LONGITUDE.equals(identifier.getValue())) {
                        return (Attribute) HelperAttributes.javaType2Attribute(pos.getBodyElement0().getLongitude());
                    }

                    if (PARAMETER_GPS_ALTITUDE.equals(identifier.getValue())) {
                        return (Attribute) HelperAttributes.javaType2Attribute(pos.getBodyElement0().getAltitude());
                    }
                } catch (IOException ex) {
                    Logger.getLogger(DemoGPSData.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NMFException ex) {
                    Logger.getLogger(DemoGPSData.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (PARAMETER_GPS_N_SATS_IN_VIEW.equals(identifier.getValue())) {

                    final Semaphore sem = new Semaphore(0);
                    final IntegerList nOfSats = new IntegerList();

                    class AdapterImpl extends GPSAdapter {

                        @Override
                        public void getSatellitesInfoResponseReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, 
                                org.ccsds.moims.mo.platform.gps.structures.SatelliteInfoList gpsSatellitesInfo, java.util.Map qosProperties) {
                            nOfSats.add(gpsSatellitesInfo.size());

                            sem.release();
                        }
                    }

                    try {
                        nanoSatMOFramework.getPlatformServices().getGPSService().getSatellitesInfo(new AdapterImpl());
                    } catch (IOException ex) {
                        Logger.getLogger(DemoGPSData.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NMFException ex) {
                        Logger.getLogger(DemoGPSData.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    try {
                        sem.acquire();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DemoGPSData.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    return (Attribute) HelperAttributes.javaType2Attribute(nOfSats.get(0));
                }

            } catch (MALException ex) {
                Logger.getLogger(DemoGPSData.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(DemoGPSData.class.getName()).log(Level.SEVERE, null, ex);
            }

            return null;
        }

        @Override
        public Boolean onSetValue(Identifier identifier, Attribute value) {
            return false;  // To confirm that the variable was set
        }

        @Override
        public UInteger actionArrived(Identifier name, AttributeValueList attributeValues, Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {
            return null;  // Action service not integrated
        }
    }
}
