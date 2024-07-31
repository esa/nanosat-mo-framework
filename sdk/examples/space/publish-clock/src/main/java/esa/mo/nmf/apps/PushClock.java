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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
 * This NMF App is a simple clock. It pushes the day of the week, the hours, the
 * minutes and the seconds.
 *
 */
public class PushClock {

    private final NanoSatMOConnectorImpl connector = new NanoSatMOConnectorImpl();
    private final TaskScheduler timer = new TaskScheduler(1);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEEE");
    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;
    private String day_of_the_week = "";
    private static final int REFRESH_RATE = 1; // 1 second

    private Calendar calendar;
    private Date date;

    public PushClock() {
        connector.init(new MCAdapter());

        this.timer.scheduleTask(new Thread(() -> {
            try {
                pushClock();
            } catch (NMFException ex) {
                Logger.getLogger(PushClock.class.getName()).log(Level.SEVERE,
                    "The Clock could not be pushed to the consumer!", ex);
            }
        }), 5, REFRESH_RATE, TimeUnit.SECONDS, true); // conversion to milliseconds
    }

    public void pushClock() throws NMFException {
        calendar = GregorianCalendar.getInstance();
        date = new Date();

        calendar.setTime(date);

        if (minutes != calendar.get(Calendar.MINUTE)) {
            day_of_the_week = dateFormat.format(date);
            connector.pushParameterValue("Day of the week", day_of_the_week);

            hours = calendar.get(Calendar.HOUR_OF_DAY);
            connector.pushParameterValue("Hours", hours);

            minutes = calendar.get(Calendar.MINUTE);
            connector.pushParameterValue("Minutes", minutes);
        }

        if (seconds != calendar.get(Calendar.SECOND)) {
            seconds = calendar.get(Calendar.SECOND);
            connector.pushParameterValue("Seconds", seconds);
        }
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String[] args) throws Exception {
        PushClock demo = new PushClock();
    }

    public static class MCAdapter extends MonitorAndControlNMFAdapter {

        @Override
        public void initialRegistrations(MCRegistration registrationObject) {
            // Not necessary because the NMF will do the registrations
            // automatically after the first push
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
