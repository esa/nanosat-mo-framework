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
package esa.mo.nmf.provider;

import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl;
import esa.mo.nmf.nanosatmosupervisor.NanoSatMOSupervisor;
import esa.mo.platform.impl.util.PlatformServicesConsumer;
import esa.mo.platform.impl.util.PlatformServicesProviderSoftSim;
import esa.mo.sm.impl.util.OSValidator;
import esa.mo.sm.impl.util.ShellCommander;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValueList;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.AttributeValue;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ConditionalConversionList;
import org.ccsds.moims.mo.platform.gps.consumer.GPSAdapter;

// Specific Software Simulator Monitoring and Control
public class MCSoftwareSimulatorAdapter extends MonitorAndControlNMFAdapter {

  private static final Logger LOGGER = Logger.getLogger(MCSoftwareSimulatorAdapter.class.getName());

  private final static String DATE_PATTERN = "dd MMM yyyy HH:mm:ss.SSS";
  private static final String PARAMETER_CURRENT_PARTITION = "OSPartition";
  private static final String CMD_CURRENT_PARTITION
      = "mount -l | grep \"on / \" | grep -o 'mmc.*[0-9]p[0-9]'";
  private static final String PARAMETER_OS_VERSION = "OSVersion";
  private static final String CMD_LINUX_VERSION = "uname -a";
  private static final String CMD_WINDOWS_VERSION = "systeminfo | findstr Version";

  private static final String ACTION_GPS_SENTENCE = "GPS_Sentence";
  private static final String ACTION_CLOCK_SET_TIME = "Clock.setTimeUsingDeltaMilliseconds";

  private final ShellCommander shellCommander = new ShellCommander();
  private PlatformServicesConsumer platformsim;
  private NanoSatMOSupervisor nmfconnector;

  public void setNmfConnector(NanoSatMOSupervisor conn) {
    nmfconnector = conn;
  }

  @Override
  public void initialRegistrations(MCRegistration registrationObject) {
    registrationObject.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

    /* PARAMETERS */
    ParameterDefinitionDetailsList defs = new ParameterDefinitionDetailsList();
    IdentifierList paramIdentifiers = new IdentifierList();

    defs.add(new ParameterDefinitionDetails(
        "The Current partition where the OS is running. Only works for linux",
        Union.STRING_SHORT_FORM.byteValue(),
        "",
        false,
        new Duration(10),
        null,
        null
    ));
    paramIdentifiers.add(new Identifier(PARAMETER_CURRENT_PARTITION));

    defs.add(new ParameterDefinitionDetails(
        "The version of the operating system.",
        Union.STRING_SHORT_FORM.byteValue(),
        "",
        false,
        new Duration(10),
        null,
        null
    ));
    paramIdentifiers.add(new Identifier(PARAMETER_OS_VERSION));
    registrationObject.registerParameters(paramIdentifiers, defs);

    /* ACTIONS */
    ActionDefinitionDetailsList actionDefs = new ActionDefinitionDetailsList();
    IdentifierList actionIdentifiers = new IdentifierList();
    ArgumentDefinitionDetailsList arguments1 = new ArgumentDefinitionDetailsList();
    {
      Byte rawType = Attribute._STRING_TYPE_SHORT_FORM;
      String rawUnit = "NMEA sentence identifier";
      ConditionalConversionList conditionalConversions = null;
      Byte convertedType = null;
      String convertedUnit = null;

      arguments1.add(new ArgumentDefinitionDetails(new Identifier("0"), null,
          rawType, rawUnit, conditionalConversions, convertedType, convertedUnit));
    }

    ActionDefinitionDetails actionDef1 = new ActionDefinitionDetails(
        "Injects the NMEA sentence identifier into the CAN bus.",
        new UOctet((short) 0),
        new UShort(0),
        arguments1
    );

    ArgumentDefinitionDetailsList arguments2 = new ArgumentDefinitionDetailsList();
    {
      Byte rawType = Attribute._LONG_TYPE_SHORT_FORM;
      String rawUnit = "milliseconds";
      ConditionalConversionList conditionalConversions = null;
      Byte convertedType = null;
      String convertedUnit = null;

      arguments2.add(new ArgumentDefinitionDetails(new Identifier("0"), null,
          rawType, rawUnit, conditionalConversions, convertedType, convertedUnit));
    }

    ActionDefinitionDetails actionDef2 = new ActionDefinitionDetails(
        "Sets the clock using a diff between the on-board time and the desired time.",
        new UOctet((short) 0),
        new UShort(0),
        arguments2
    );

    actionDefs.add(actionDef1);
    actionDefs.add(actionDef2);
    actionIdentifiers.add(new Identifier(ACTION_GPS_SENTENCE));
    actionIdentifiers.add(new Identifier(ACTION_CLOCK_SET_TIME));

    registrationObject.registerActions(actionIdentifiers, actionDefs);
  }

  @Override
  public Attribute onGetValue(Identifier identifier, Byte rawType) {
    OSValidator osv = new OSValidator();
    if (PARAMETER_CURRENT_PARTITION.equals(identifier.getValue())) {
      if (osv.isUnix()) {
        String msg = shellCommander.runCommandAndGetOutputMessage(CMD_CURRENT_PARTITION);
        return (Attribute) HelperAttributes.javaType2Attribute(msg);
      }
    }
    if(PARAMETER_OS_VERSION.equals(identifier.getValue())){
      String msg = null;
      if(osv.isUnix()){
        msg = shellCommander.runCommandAndGetOutputMessage(CMD_LINUX_VERSION);
      }
      else if(osv.isWindows()){
        msg = shellCommander.runCommandAndGetOutputMessage(CMD_WINDOWS_VERSION);
      }
      return (Attribute) HelperAttributes.javaType2Attribute(msg);
    }
    return null;
  }

  @Override
  public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
    return false;  // to confirm that no variable was set
  }

  @Override
  public UInteger actionArrived(Identifier name, AttributeValueList attributeValues,
      Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {
    if (ACTION_GPS_SENTENCE.equals(name.getValue())) {
      try {
        nmfconnector.getPlatformServices().getGPSService().getNMEASentence("GPGGALONG",
            new MCGPSAdapter());
      } catch (MALInteractionException ex) {
        LOGGER.log(Level.SEVERE, null, ex);
      } catch (MALException ex) {
        LOGGER.log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
        LOGGER.log(Level.SEVERE, null, ex);
      } catch (NMFException ex) {
        LOGGER.log(Level.SEVERE, null, ex);
      }

      return null; // Success!
    }

    if (ACTION_CLOCK_SET_TIME.equals(name.getValue())) {
      if (attributeValues.isEmpty()) {
        return new UInteger(0); // Error!
      }

      AttributeValue aVal = attributeValues.get(0); // Extract the delta!
      long delta = (Long) HelperAttributes.attribute2JavaType(aVal.getValue());

      String str = (new SimpleDateFormat(DATE_PATTERN)).format(new Date(
          System.currentTimeMillis() + delta));

      ShellCommander shell = new ShellCommander();
      shell.runCommand("date -s \"" + str + " UTC\" | hwclock --systohc");

      return null; // Success!
    }

    return new UInteger(1);  // Action service not integrated
  }

  private class MCGPSAdapter extends GPSAdapter {

    @Override
    public void getNMEASentenceResponseErrorReceived(MALMessageHeader msgHeader,
        MALStandardError error, Map qosProperties) {
      LOGGER.log(Level.WARNING, "Received response error");
    }

    @Override
    public void getNMEASentenceAckErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
        Map qosProperties) {
      LOGGER.log(Level.WARNING, "Received ACK error");
    }

    @Override
    public void getNMEASentenceResponseReceived(MALMessageHeader msgHeader, String sentence,
        Map qosProperties) {
      LOGGER.log(Level.INFO, "Received message " + sentence);
    }

    @Override
    public void getNMEASentenceAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
      LOGGER.log(Level.INFO, "Received ack");
    }

  }

}
