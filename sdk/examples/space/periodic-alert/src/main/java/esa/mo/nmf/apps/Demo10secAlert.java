/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
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

import esa.mo.helpertools.misc.TaskScheduler;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValueList;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;

/**
 * This class provides a simple Hello World demo cli provider
 *
 */
public class Demo10secAlert {

    private final NanoSatMOConnectorImpl connector = new NanoSatMOConnectorImpl();
    private final TaskScheduler timer;

    public Demo10secAlert() {
        this.connector.init(new Adapter());
        this.timer = new TaskScheduler(1);

        this.timer.scheduleTask(new Thread(() -> {
            try {
                connector.publishAlertEvent("10SecondsAlert", null);
            } catch (NMFException ex) {
                Logger.getLogger(Demo10secAlert.class.getName()).log(Level.SEVERE,
                    "The Alert could not be published to the consumer!", ex);
            }
        }), 0, 10, TimeUnit.SECONDS, true); // 10 seconds
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String[] args) throws Exception {
        Demo10secAlert demo = new Demo10secAlert();
    }

    static class Adapter extends MonitorAndControlNMFAdapter {

        @Override
        public void initialRegistrations(MCRegistration registrationObject) {
        }

        @Override
        public UInteger actionArrived(Identifier idntfr, AttributeValueList avl, Long l, boolean bln,
            MALInteraction mali) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Attribute onGetValue(Identifier idntfr, Byte b) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
            throw new UnsupportedOperationException("Not supported.");
        }

    }

}
