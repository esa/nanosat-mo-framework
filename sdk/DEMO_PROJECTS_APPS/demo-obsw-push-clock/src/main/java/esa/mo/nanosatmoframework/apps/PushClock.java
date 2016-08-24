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

import esa.mo.nanosatmoframework.connector.NanoSatMOConnectorImpl;
import esa.mo.nanosatmoframework.nanosatmomonolithic.adapters.MonitorAndControlAdapter;
import esa.mo.nanosatmoframework.nanosatmomonolithic.interfaces.MCRegistrationInterface;
import esa.mo.nanosatmoframework.nanosatmomonolithic.interfaces.NanoSatMOFrameworkInterface;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;

/**
 * This app is a simple clock. It pushes the day of the week, the hours, the
 * minutes and the seconds.
 *
 */
public class PushClock {

    private final NanoSatMOFrameworkInterface nanoSatMOFramework = new NanoSatMOConnectorImpl(new mcAdapter());
//    private final NanoSatMOFrameworkInterface nanoSatMOFramework = new NanoSatMOMonolithicSim(new mcAdapter());
    private final Timer timer = new Timer();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEEE");
    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;
    private String day_of_the_week = "";
    private static final int REFRESH_RATE = 1; // 1 second

    public PushClock() {

        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    pushClock();
                } catch (IOException ex) {
                    Logger.getLogger(PushClock.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, 0, REFRESH_RATE * 1000); // conversion to milliseconds

    }

    public void pushClock() throws IOException {
        Date now = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(now);

        if (minutes != calendar.get(Calendar.MINUTE)) {
            day_of_the_week = dateFormat.format(now);
            nanoSatMOFramework.pushParameterValue("Day of the week", day_of_the_week);

            hours = calendar.get(Calendar.HOUR_OF_DAY);
            nanoSatMOFramework.pushParameterValue("Hours", hours);

            minutes = calendar.get(Calendar.MINUTE);
            nanoSatMOFramework.pushParameterValue("Minutes", minutes);
        }

        if (seconds != calendar.get(Calendar.SECOND)) {
            seconds = calendar.get(Calendar.SECOND);
            nanoSatMOFramework.pushParameterValue("Seconds", seconds);
        }
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String args[]) throws Exception {
        PushClock demo = new PushClock();
    }

    public class mcAdapter extends MonitorAndControlAdapter {

        @Override
        public void initialRegistrations(MCRegistrationInterface registrationObject) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public UInteger actionArrived(Identifier idntfr, AttributeValueList avl, Long l, boolean bln, MALInteraction mali) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Attribute onGetValue(Identifier idntfr, Byte b) {
            return new Duration(34);
        }

        @Override
        public Boolean onSetValue(Identifier idntfr, Attribute atrbt) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    
}
