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

import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.helpertools.misc.TaskScheduler;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.AttributeType;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ParameterDefinition;
import org.ccsds.moims.mo.mc.structures.ParameterDefinitionList;
import org.ccsds.moims.mo.mc.structures.ParameterRawValueList;

/**
 * This NMF App is a simple clock. It pushes the day of the week, the hours, the
 * minutes and the seconds.
 *
 */
public class EchoSpace {

    private final NanoSatMOConnectorImpl connector = new NanoSatMOConnectorImpl();
    private final TaskScheduler timer = new TaskScheduler(1);
    private Blob data;
    private static final int REFRESH_RATE = 1; // 1 second

    private Calendar calendar;
    private Date date;

    public EchoSpace() {
        connector.init(new MCAdapter());
    }

    public void pushBlob() throws NMFException {
        connector.pushParameterValue("Data", data);
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String[] args) throws Exception {
        EchoSpace demo = new EchoSpace();
    }

    public class MCAdapter extends MonitorAndControlNMFAdapter {

        @Override
        public void initialRegistrations(MCRegistration registrationObject) {
            ParameterDefinitionList pddl = new ParameterDefinitionList();

            pddl.add(new ParameterDefinition(
                    new Identifier("Data"), "The sent data", new AttributeType(1),
                    null, true, new Duration(), null, null));
            registrationObject.registerParameters(pddl);
        }

        @Override
        public UInteger actionArrived(Identifier idntfr, AttributeValueList avl,
                Long l, boolean bln, MALInteraction mali) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Attribute onGetValue(Identifier idntfr, AttributeType b) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
            if (!identifiers.get(0).getValue().equals("Data")) {
                return false;
            }

            data = (Blob) values.get(0).getRawValue();
            try {
                pushBlob();
            } catch (NMFException ex) {
                Logger.getLogger(EchoSpace.class.getName()).log(
                        Level.SEVERE, "NMF Exception", ex);
                return false;
            }
            return true;
        }
    }
}
