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
import esa.mo.nanosatmoframework.nanosatmoconnector.NanoSatMOConnectorImpl;
import esa.mo.nanosatmoframework.MonitorAndControlAdapter;
import esa.mo.nanosatmoframework.MCRegistrationInterface;
import esa.mo.nanosatmoframework.NanoSatMOFrameworkInterface;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.Semaphore;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPair;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPairList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IntegerList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.ReferenceFrame;
import org.ccsds.moims.mo.platform.gps.body.GetLastKnownPositionResponse;
import org.ccsds.moims.mo.platform.gps.consumer.GPSAdapter;
import org.ccsds.moims.mo.platform.gps.provider.GetSatellitesInfoInteraction;

/**
 * This class provides a simple demo that allows data to be retrieved from a GPS
 * unit.
 *
 */
public class DemoGPSData {

    private final NanoSatMOFrameworkInterface nanoSatMOFramework = new NanoSatMOConnectorImpl(new mcAdapter());
//    private final NanoSatMOFrameworkInterface nanoSatMOFramework = new NanoSatMOMonolithicSim(new mcAdapter());

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

    public class mcAdapter extends MonitorAndControlAdapter {

        @Override
        public void initialRegistrations(MCRegistrationInterface registrationObject) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Attribute onGetValue(Identifier identifier, Byte rawType) {

            try {
                if (nanoSatMOFramework == null) {  // The framework is still not available
                    return null;
                }

                GetLastKnownPositionResponse pos = nanoSatMOFramework.getPlatformServices().getGPSService().getLastKnownPosition();

                if (identifier.getValue().equals("GPS.Latitude")) {
                    return (Attribute) HelperAttributes.javaType2Attribute(pos.getBodyElement0().getLatitude());
                }

                if (identifier.getValue().equals("GPS.Longitude")) {
                    return (Attribute) HelperAttributes.javaType2Attribute(pos.getBodyElement0().getLongitude());
                }

                if (identifier.getValue().equals("GPS.Altitude")) {
                    return (Attribute) HelperAttributes.javaType2Attribute(pos.getBodyElement0().getAltitude());
                }

                if (identifier.getValue().equals("GPS.ElapsedTime")) {
                    return pos.getBodyElement1();
                }

                if (identifier.getValue().equals("GPS.NumberOfSatellitesInView")) {

                    final Semaphore sem = new Semaphore(0);
                    final IntegerList nOfSats = new IntegerList();

                    class AdapterImpl extends GPSAdapter {

                        @Override
                        public void getSatellitesInfoResponseReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.platform.gps.structures.SatelliteInfoList gpsSatellitesInfo, java.util.Map qosProperties) {
                            nOfSats.add(gpsSatellitesInfo.size());

                            sem.release();

                        }
                    }

                    nanoSatMOFramework.getPlatformServices().getGPSService().getSatellitesInfo(new AdapterImpl());

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
