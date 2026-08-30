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
package esa.mo.nmf.provider;

import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.helpertools.misc.ShellCommander;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperAttributes;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.*;
import esa.mo.mc.impl.interfaces.ActionNotFoundException;
import org.ccsds.moims.mo.mc.ExecutionFailedException;
import org.ccsds.moims.mo.mc.structures.*;

/**
 * The Monitor and Control Adapter for the NanoSat MO Supervisor.
 */
public class MCRaspberryPiAdapter extends MonitorAndControlNMFAdapter {

    private static final Logger LOGGER = Logger.getLogger(MCRaspberryPiAdapter.class.getName());

    private final static String DATE_PATTERN = "dd MMM yyyy HH:mm:ss.SSS";

    private static final String PARAMETER_CURRENT_PARTITION = "System.CurrentPartition";
    private static final String CMD_CURRENT_PARTITION = "mount -l | grep \"on / \" | grep -o 'mmc.*[0-9]p[0-9]'";

    private static final String PARAMETER_LINUX_VERSION = "Linux.Version";
    private static final String CMD_LINUX_VERSION = "uname -a";
    private static final String CMD_LINUX_REBOOT = "reboot";

    private static final String ACTION_GPS_SENTENCE = "GPS_Sentence";
    private static final String ACTION_REBOOT = "System.Reboot";
    private static final String ACTION_CLOCK_SET_TIME = "System.SetTimeUsingDeltaMilliseconds";

    private static final String PARAMETER_GEOFENCE = "App.Geofence";
    private Geofence geofence;

    private static final String ACTION_NMF_RESTART = "NMF.Restart";

    private final ShellCommander shellCommander = new ShellCommander();

    public MCRaspberryPiAdapter(NanoSatMOSupervisorRaspberryPiImpl supervisor) {
        try {
            this.geofence = new Geofence(supervisor);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not get MC Services from Supervisor!", ex);
        }
    }

    @Override
    public void initialRegistrations(MCRegistration registration) {
        registration.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

        // ------------------ Parameters ------------------
        ParameterDefinitionList defs = new ParameterDefinitionList();

        defs.add(new ParameterDefinition(
                new Identifier(PARAMETER_CURRENT_PARTITION),
                "The Current partition where the OS is running.",
                AttributeType.STRING,
                "",
                false,
                new Duration(10),
                null,
                null,
                false
        ));

        defs.add(new ParameterDefinition(
                new Identifier(PARAMETER_LINUX_VERSION),
                "The version of Linux.",
                AttributeType.STRING,
                "",
                false,
                new Duration(10),
                null,
                null,
                false
        ));

        defs.add(new ParameterDefinition(
                new Identifier(PARAMETER_GEOFENCE),
                "App Geofence data",
                AttributeType.STRING,
                "",
                false,
                new Duration(10),
                null,
                null,
                false
        ));

        registration.registerParameters(defs);

        // ------------------ Actions ------------------
        ActionDefinitionList actionDefs = new ActionDefinitionList();

        ArgumentDefinitionList arguments1 = new ArgumentDefinitionList();
        {
            AttributeType rawType = AttributeType.STRING;
            String rawUnit = "NMEA sentence identifier";

            arguments1.add(new ArgumentDefinition(new Identifier("0"), null, rawType, rawUnit));
        }

        ActionDefinition actionDef1 = new ActionDefinition(
                new Identifier(ACTION_GPS_SENTENCE),
                "Injects the NMEA sentence identifier into the CAN bus.",
                new UShort(0),
                arguments1
        );

        ArgumentDefinitionList arguments2 = new ArgumentDefinitionList();
        {
            AttributeType rawType = AttributeType.LONG;
            String rawUnit = "milliseconds";

            arguments2.add(new ArgumentDefinition(new Identifier("0"), null, rawType, rawUnit));
        }

        ActionDefinition actionDef2 = new ActionDefinition(
                new Identifier(ACTION_CLOCK_SET_TIME),
                "Sets the clock using a diff between the on-board time and the desired time.",
                new UShort(0),
                arguments2
        );

        ActionDefinition actionDef3 = new ActionDefinition(
                new Identifier(ACTION_REBOOT),
                "Reboots the mityArm.",
                new UShort(0),
                new ArgumentDefinitionList()
        );

        actionDefs.add(actionDef1);
        actionDefs.add(actionDef2);
        actionDefs.add(actionDef3);
        registration.registerActions(actionDefs);
    }

    @Override
    public Attribute onGetValue(Identifier identifier, AttributeType rawType) {
        if (PARAMETER_CURRENT_PARTITION.equals(identifier.getValue())) {
            String msg = shellCommander.runCommandAndGetOutputMessage(CMD_CURRENT_PARTITION);
            return (Attribute) HelperAttributes.javaType2Attribute(msg);
        } else if (PARAMETER_LINUX_VERSION.equals(identifier.getValue())) {
            String msg = shellCommander.runCommandAndGetOutputMessage(CMD_LINUX_VERSION);
            return (Attribute) HelperAttributes.javaType2Attribute(msg);
        } else if (PARAMETER_GEOFENCE.equals(identifier.getValue())) {
            String msg = this.geofence.toString();
            return (Attribute) HelperAttributes.javaType2Attribute(msg);
        }
        return null;
    }

    @Override
    public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
        try {
            identifiers.forEach(identifier -> {
                if (PARAMETER_GEOFENCE.equals(identifier.getValue())) {
                    try {

                        // Format:  Action (ADD, REMOVE, REMOVEAPP, REMOVEALL)
                        //          App name
                        //          Lon,Lat
                        //          range in km
                        //          startWhenInsideRange
                        // Example: ADD:app1:40.123456:50.123456:100.5:true
                        //          ADD:app2:44.123456:55.123456:99.5:false
                        //          REMOVE:app1:40.123456:50.123456:100.5:true
                        //          REMOVEAPP:app1
                        //          REMOVEALL
                        String rawValueString = values.get(0).getRawValue().toString();

                        String[] geofenceData = rawValueString.split(":");
                        switch (geofenceData[0]) {
                            case "ADD":
                                LOGGER.log(Level.INFO, "Add new Geofence... ");
                                geofence.add(geofenceData);
                                break;
                            case "REMOVE":
                                LOGGER.log(Level.INFO, "Remove Geofence... ");
                                geofence.remove(geofenceData);
                                break;
                            case "REMOVEAPP":
                                LOGGER.log(Level.INFO, "Remove Geofence for App " + geofenceData[1] + "...");
                                geofence.removeApp(geofenceData);
                                break;
                            case "REMOVEALL":
                                LOGGER.log(Level.INFO, "Remove all Geofences... ");
                                geofence.removeAll();
                                break;
                            default:
                                break;
                        }
                    } catch (Exception ex) {
                        LOGGER.log(Level.SEVERE, "Error when setting value: " + ex);
                    }
                }
            });

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error when setting value: " + ex.getMessage());
        }

        return false;  // to confirm that no variable was set
    }

    @Override
    public void actionArrived(Identifier name, AttributeValueList attributeValues,
            Long executionId, MALInteraction interaction)
            throws ExecutionFailedException, ActionNotFoundException {
        if (ACTION_GPS_SENTENCE.equals(name.getValue())) {
            return;
        }

        if (ACTION_REBOOT.equals(name.getValue())) {
            ShellCommander shell = new ShellCommander();
            String output = shell.runCommandAndGetOutputMessage(CMD_LINUX_REBOOT);
            LOGGER.log(Level.INFO, "Output: " + output);
            return;
        }

        if (ACTION_CLOCK_SET_TIME.equals(name.getValue())) {
            if (attributeValues.isEmpty()) {
                throw new ExecutionFailedException(new Union(
                        "The time difference was not provided."));
            }

            AttributeValue aVal = attributeValues.get(0); // Extract the delta!
            long delta = (Long) HelperAttributes.attribute2JavaType(aVal.getValue());

            String str = (new SimpleDateFormat(DATE_PATTERN)).format(new Date(System.currentTimeMillis() + delta));

            ShellCommander shell = new ShellCommander();
            shell.runCommand("date -s \"" + str + " UTC\" | hwclock --systohc");
            return;
        }

        throw new ActionNotFoundException(name.getValue());
    }
}
