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
package esa.mo.demo.consumer.setandcommand;

import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.nanosatmoframework.groundmoadapter.GroundMOAdapter;
import esa.mo.nanosatmoframework.groundmoadapter.SimpleDataReceivedListener;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetails;

/**
 * Ground consumer: Demo Ground 0
 *
 */
public class DemoSetAndCommand {


    private final GroundMOAdapter moGroundAdapter;
    
    
    public DemoSetAndCommand() {

        ConnectionConsumer connection = new ConnectionConsumer();
        
        try {
            connection.loadURIs();
        } catch (MalformedURLException ex) {
            Logger.getLogger(DemoSetAndCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        moGroundAdapter = new GroundMOAdapter(connection);
        moGroundAdapter.addDataReceivedListener(new DataReceivedAdapter());
        
        // Set a parameter with a double value
        Double parameterValue = 1.2345;
//        moGroundAdapter.sendData("Parameter_name", parameterValue);
        AggregationDefinitionDetails aaa = new AggregationDefinitionDetails();
        moGroundAdapter.setParameter("Parameter_name", aaa);

        // Send a command with a Double argument
        Double value = 1.35565;
        Double[] values = new Double[1];
        values[0] = value;
        moGroundAdapter.invokeAction("Something", values);
    
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String args[]) throws Exception {
        DemoSetAndCommand demo = new DemoSetAndCommand();
    }

    
    class DataReceivedAdapter extends SimpleDataReceivedListener {
    
        @Override
        public void onDataReceived(String parameterName, Serializable data) {
            Logger.getLogger(DemoSetAndCommand.class.getName()).log(Level.INFO, "\nParameter name: {0}" + "\n" + "Data content:\n{1}", new Object[]{parameterName, data.toString()});
        }

    }
    
    
}
