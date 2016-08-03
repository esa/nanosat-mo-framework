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
package esa.mo.demo.consumer.ground0;

import esa.mo.mc.impl.provider.ParameterInstance;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import esa.mo.nanosatmoframework.ground.adapter.GroundMOAdapter;
import esa.mo.nanosatmoframework.ground.listeners.CompleteDataReceivedListener;
import esa.mo.nanosatmoframework.ground.listeners.SimpleDataReceivedListener;
import java.net.MalformedURLException;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 * Ground consumer: Demo Ground 0
 */
public class DemoGround0 {

    private GroundMOAdapter moGroundAdapter;
//    private final static URI DIRECTORY_URI = new URI("rmi://131.176.85.51:1024/1024-GPSData-Directory");
    private final static URI DIRECTORY_URI = new URI("malspp:247/1/12");

    public DemoGround0() {

        System.setProperty("esa.mo.transport.can.opssat.nodeSource", "1");
        System.setProperty("esa.mo.transport.can.opssat.nodeDestination", "32");
                
        
        try {
            ProviderSummaryList providers = GroundMOAdapter.retrieveProvidersFromDirectory(DIRECTORY_URI);
            
            if (!providers.isEmpty()){
                moGroundAdapter = new GroundMOAdapter(providers.get(0));
                moGroundAdapter.addDataReceivedListener(new CompleteDataReceivedAdapter());
            }else{
                Logger.getLogger(DemoGround0.class.getName()).log(Level.SEVERE, "Something went wrong!");
            }
            
        } catch (MALException ex) {
            Logger.getLogger(DemoGround0.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(DemoGround0.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(DemoGround0.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String args[]) throws Exception {
        DemoGround0 demo = new DemoGround0();
    }

    private class SimpleDataReceivedAdapter extends SimpleDataReceivedListener {

        @Override
        public void onDataReceived(String parameterName, Serializable data) {
            Logger.getLogger(DemoGround0.class.getName()).log(Level.INFO, "\nParameter name: {0}" + "\n" + "Data content:\n{1}", new Object[]{parameterName, data.toString()});
        }

    }

    private class CompleteDataReceivedAdapter extends CompleteDataReceivedListener {

        @Override
        public void onDataReceived(ParameterInstance parameterInstance) {
            Logger.getLogger(DemoGround0.class.getName()).log(Level.INFO, "\nParameter name: {0}" + "\n" + "Parameter Value: {1}", new Object[]{parameterInstance.getName(), parameterInstance.getParameterValue().toString()});
        }

    }
    
}
