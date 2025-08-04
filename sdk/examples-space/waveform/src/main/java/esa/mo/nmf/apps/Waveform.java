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
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperAttributes;
import org.ccsds.moims.mo.mal.helpertools.misc.TaskScheduler;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mc.structures.*;

/**
 * This NMF App is a simple clock. It pushes the day of the week, the hours, the
 * minutes and the seconds.
 *
 */
public class Waveform {

    private final NanoSatMOConnectorImpl connector = new NanoSatMOConnectorImpl();
    private TaskScheduler tasker = null;
    private double amplitude = 1.0;
    private double frequency = 1.0;
    private long refresh = 1000;
    private boolean started = false;

    public Waveform() {
        connector.init(new MCAdapter());
        tasker = new TaskScheduler(1);
        Logger.getLogger(Waveform.class.getName()).log(Level.FINE,
                System.getProperty("app.name") + " started.");
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String[] args) throws Exception {
        Waveform demo = new Waveform();
    }

    public void pushVal() throws NMFException {
        double res = amplitude * Math.sin(((double) System.nanoTime() / 1000000000.0) * 2 * Math.PI * frequency);
        connector.pushParameterValue("Sine", res);
    }

    public void startWave() {
        if (started) {
            return;
        }
        started = true;
        Logger.getLogger(Waveform.class.getName()).log(Level.FINE, "Starting wave");
        this.tasker.scheduleTask(new Thread(() -> {
            try {
                pushVal();
            } catch (NMFException ex) {
                Logger.getLogger(Waveform.class.getName()).log(Level.SEVERE, "The sine value could not be pushed!", ex);
            }
        }), 0, refresh, TimeUnit.MICROSECONDS, true); // conversion to milliseconds
    }

    public void stopWave() {
        if (!started) {
            return;
        }
        tasker.stopLast();
        started = false;
    }

    public class MCAdapter extends MonitorAndControlNMFAdapter {

        @Override
        public void initialRegistrations(MCRegistration registrationObject) {
            registrationObject.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

            // ------------------ Parameters ------------------
            final ParameterDefinitionList defs = new ParameterDefinitionList();

            defs.add(new ParameterDefinition(new Identifier("Amplitude"),
                    "Amplitude of the wave", AttributeType.DOUBLE, "",
                    true, new Duration(3), null, null));
            defs.add(new ParameterDefinition(new Identifier("Frequency"),
                    "Frequency of the wave", AttributeType.DOUBLE, "",
                    true, new Duration(3), null, null));
            defs.add(new ParameterDefinition(new Identifier("Sine"),
                    "Result of the wave", AttributeType.DOUBLE, "", true,
                    new Duration(), null, null));
            defs.add(new ParameterDefinition(new Identifier("Refresh"),
                    "Refreshrate for publishing the result",
                    AttributeType.LONG, "us", true, new Duration(), null, null));

            registrationObject.registerParameters(defs);

            ActionDefinitionList actionDefs = new ActionDefinitionList();
            ActionDefinition actionDef1 = new ActionDefinition(
                    new Identifier("start"), "Start the plotter.",
                    ActionCategory.DEFAULT, new UShort(0), new ArgumentDefinitionList());
            actionDefs.add(actionDef1);
            ActionDefinition actionDef2 = new ActionDefinition(
                    new Identifier("stop"), "Stop the plotter.",
                    ActionCategory.DEFAULT, new UShort(0), new ArgumentDefinitionList());
            actionDefs.add(actionDef2);

            ArgumentDefinitionList argDef1 = new ArgumentDefinitionList();
            {
                AttributeType rawType = AttributeType.DOUBLE;
                String rawUnit = "Units";
                argDef1.add(new ArgumentDefinition(new Identifier("Amplitude"), null, rawType, rawUnit));
            }

            ActionDefinition actionDef3 = new ActionDefinition(
                    new Identifier("updateAmplitude"), "Update the Amplitude",
                    ActionCategory.DEFAULT, new UShort(0), argDef1);
            actionDefs.add(actionDef3);

            ArgumentDefinitionList argDef2 = new ArgumentDefinitionList();
            {
                AttributeType rawType = AttributeType.DOUBLE;
                String rawUnit = "Hz";
                argDef2.add(new ArgumentDefinition(new Identifier("Frequency"), null, rawType, rawUnit));
            }

            ActionDefinition actionDef4 = new ActionDefinition(
                    new Identifier("updateFrequency"), "Update the Frequency",
                    ActionCategory.DEFAULT, new UShort(0), argDef2);
            actionDefs.add(actionDef4);

            registrationObject.registerActions(actionDefs);
        }

        @Override
        public UInteger actionArrived(Identifier idntfr, AttributeValueList avl, Long l, boolean bln,
                MALInteraction mali) {
            if (idntfr.getValue().equals("start")) {
                Logger.getLogger(Waveform.class.getName()).log(Level.FINER, "Started wave");
                startWave();
                Enumeration<String> loggers = LogManager.getLogManager().getLoggerNames();
                StringBuilder sb = new StringBuilder();
                while (loggers.hasMoreElements()) {
                    sb.append("- " + loggers.nextElement() + "\n");
                }
                Logger.getLogger(Waveform.class.getName()).log(Level.INFO, sb.toString());
                return new UInteger(0);
            }
            if (idntfr.getValue().equals("stop")) {
                Logger.getLogger(Waveform.class.getName()).log(Level.FINER, "Stopped wave");
                stopWave();
                return new UInteger(0);
            }
            if (idntfr.getValue().equals("updateAmplitude")) {
                if (avl == null) {
                    return new UInteger(1);
                }
                if (avl.isEmpty()) {
                    return new UInteger(0);
                }
                if (avl.size() >= 1) {
                    amplitude = HelperAttributes.attribute2double(avl.get(0).getValue());
                    return new UInteger(0);
                }
            }
            if (idntfr.getValue().equals("updateFrequency")) {
                if (avl == null) {
                    return new UInteger(1);
                }
                if (avl.isEmpty()) {
                    return new UInteger(0);
                }
                if (avl.size() >= 1) {
                    frequency = HelperAttributes.attribute2double(avl.get(0).getValue());
                    return new UInteger(0);
                }
            }
            return new UInteger(1);
        }

        @Override
        public Attribute onGetValue(Identifier idntfr, AttributeType b) {
            if (idntfr.getValue().equals("Amplitude")) {
                return (Attribute) HelperAttributes.javaType2Attribute(amplitude);
            }
            if (idntfr.getValue().equals("Frequency")) {
                return (Attribute) HelperAttributes.javaType2Attribute(frequency);
            }
            if (idntfr.getValue().equals("Refresh")) {
                return (Attribute) HelperAttributes.javaType2Attribute(refresh);
            } else {
                return null;
            }
        }

        @Override
        public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
            if (identifiers.get(0).getValue().equals("Amplitude")) {
                amplitude = HelperAttributes.attribute2double(values.get(0).getRawValue());
                return true;
            }
            if (identifiers.get(0).getValue().equals("Frequency")) {
                frequency = HelperAttributes.attribute2double(values.get(0).getRawValue());
                return true;
            }
            if (identifiers.get(0).getValue().equals("Refresh")) {
                refresh = (long) HelperAttributes.attribute2JavaType(values.get(0).getRawValue());
                return true;
            } else {
                return false;
            }
        }
    }
}
