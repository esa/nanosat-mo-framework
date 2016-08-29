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

import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.nanosatmoframework.MCRegistration;
import esa.mo.nanosatmoframework.MonitorAndControlNMFAdapter;
import esa.mo.nanosatmoframework.NanoSatMOFrameworkInterface;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
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
                new Duration (3),
                null
        ));
        
        AggregationDefinitionDetailsList aggs = new AggregationDefinitionDetailsList();
        aggs.add(defGPSAgg);
        LongList objIdAggs = registration.registerAggregations(aggs);
        
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
        /*
        if ("action1".equals(name.getValue())) {
            try { // action1 was called?
                nmf.reportActionExecutionProgress(true, 0, 1, 1, actionInstanceObjId);
            } catch (IOException ex) {
                Logger.getLogger(MCTriplePresentationAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
         */
        return null;  // Action service not integrated
    }

}
