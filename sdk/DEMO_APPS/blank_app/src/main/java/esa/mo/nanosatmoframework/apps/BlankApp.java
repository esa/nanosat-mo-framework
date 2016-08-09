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
import esa.mo.nanosatmoframework.adapters.MonitorAndControlAdapter;
import esa.mo.nanosatmoframework.connector.NanoSatMOConnectorImpl;
import esa.mo.nanosatmoframework.interfaces.NanoSatMOFrameworkInterface;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;

/**
 * This class provides a blank template for starting the development of an app
 */
public class BlankApp {

    private final NanoSatMOFrameworkInterface nanoSatMOFramework = new NanoSatMOConnectorImpl(new mcAdapter());
    private String parameterX = "";

    public BlankApp() {
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String args[]) throws Exception {
        BlankApp demo = new BlankApp();
    }

    public class mcAdapter extends MonitorAndControlAdapter {

        @Override
        public Attribute onGetValue(Identifier identifier, Byte rawType) {
            
            if ("parameterX".equals(identifier.toString())){ // parameterX was called?
                return (Attribute) HelperAttributes.javaType2Attribute("parameterX was called! The value is: "  + parameterX);
            }

            return null;
        }

        @Override
        public Boolean onSetValue(Identifier identifier, Attribute value) {
            
            if ("parameterX".equals(identifier.toString())){ // parameterX was called?
                parameterX = value.toString();
                return true;
            }
            
            return false;  // to confirm that the variable was set
        }

        @Override
        public UInteger actionArrived(Identifier name, AttributeValueList attributeValues, Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {

            if ("action1".equals(name.getValue())){ try {
                // action1 was called?
                nanoSatMOFramework.reportActionExecutionProgress(true, 0, 1, 1, actionInstanceObjId);
                } catch (IOException ex) {
                    Logger.getLogger(BlankApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            return null;  // Action service not integrated
        }

        @Override
        public void initialRegistrations() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
