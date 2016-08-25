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

import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.nanosatmoframework.groundmoadapter.GroundMOAdapter;
import esa.mo.nanosatmoframework.groundmoadapter.SimpleDataReceivedListener;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ground consumer: Demo Ground 0
 */
public class DemoGround0 {

    private final GroundMOAdapter moGroundAdapter;

    public DemoGround0() {

        ConnectionConsumer connection = new ConnectionConsumer();

        try {
            connection.loadURIs();
        } catch (MalformedURLException ex) {
            Logger.getLogger(DemoGround0.class.getName()).log(Level.SEVERE, null, ex);
        }

        moGroundAdapter = new GroundMOAdapter(connection);
        moGroundAdapter.addDataReceivedListener(new DataReceivedAdapter());
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

    class DataReceivedAdapter extends SimpleDataReceivedListener {

        @Override
        public void onDataReceived(String parameterName, Serializable data) {
            Logger.getLogger(DemoGround0.class.getName()).log(Level.INFO, "\nParameter name: {0}" + "\n" + "Data content:\n{1}", new Object[]{parameterName, data.toString()});
        }

    }

}
