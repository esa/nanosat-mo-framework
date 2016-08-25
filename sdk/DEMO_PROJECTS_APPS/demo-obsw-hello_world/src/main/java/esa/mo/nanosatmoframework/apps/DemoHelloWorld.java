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

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.nanosatmoframework.MonitorAndControlAdapter;
import esa.mo.nanosatmoframework.SimpleMonitorAndControlAdapter;
import esa.mo.nanosatmoframework.MCRegistrationInterface;
import esa.mo.nanosatmoframework.NanoSatMOFrameworkInterface;
import esa.mo.nanosatmoframework.provider.NanoSatMOMonolithicSim;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;

/**
 * This class provides a simple Hello World demo cli provider
 *
 */
public class DemoHelloWorld {

    private final NanoSatMOFrameworkInterface nanoSatMOFramework = new NanoSatMOMonolithicSim(new MCAdapterSimple());
    private String str = "Hello World!";
//    public final COMServicesProvider comServices = new COMServicesProvider();

    public DemoHelloWorld() {
        
        ConnectionProvider.resetURILinksFile(); // Resets the providerURIs.properties file
        HelperMisc.loadPropertiesFile(); // Loads: provider.properties; settings.properties; transport.properties
        /*
        try {
            this.comServices.init();
        } catch (MALException ex) {
            Logger.getLogger(DemoHelloWorld.class.getName()).log(Level.SEVERE, null, ex);
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
        DemoHelloWorld demo = new DemoHelloWorld();
    }

    public class MCAdapter extends MonitorAndControlAdapter {

        @Override
        public void initialRegistrations(MCRegistrationInterface registrationObject) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Attribute onGetValue(Identifier identifier, Byte rawType) {
            return (Attribute) HelperAttributes.javaType2Attribute(str);
        }

        @Override
        public Boolean onSetValue(Identifier identifier, Attribute value) {
            str = value.toString(); // Let's set the str variable
            return true;  // to confirm that the variable was set
        }

        @Override
        public UInteger actionArrived(Identifier name, AttributeValueList attributeValues, Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {
            return null;  // Action service not integrated
        }

    }

    public class MCAdapterSimple extends SimpleMonitorAndControlAdapter {

        @Override
        public boolean actionArrivedSimple(String name, Serializable[] values, Long actionInstanceObjId) {
            return false;
        }

        @Override
        public Serializable onGetValueSimple(String name) {
            return str;
        }

        @Override
        public boolean onSetValueSimple(String name, Serializable value) {
            str = value.toString(); // Let's set the str variable
            return true;  // to confirm that the variable was set
        }
    }
    
}
