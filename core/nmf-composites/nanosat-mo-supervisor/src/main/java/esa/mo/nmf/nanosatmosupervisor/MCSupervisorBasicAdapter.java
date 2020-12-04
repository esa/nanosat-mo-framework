/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package esa.mo.nmf.nanosatmosupervisor;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.platform.gps.consumer.GPSAdapter;

import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.annotations.Action;
import esa.mo.nmf.annotations.ActionParameter;
import esa.mo.nmf.annotations.Parameter;
import esa.mo.sm.impl.util.OSValidator;
import esa.mo.sm.impl.util.ShellCommander;

/**
 *
 * @author yannick
 */
public class MCSupervisorBasicAdapter extends MonitorAndControlNMFAdapter {

  private static final Logger LOGGER = Logger.getLogger(MCSupervisorBasicAdapter.class.getName());

  private final static String DATE_PATTERN = "dd MMM yyyy HH:mm:ss.SSS";
  private static final String CMD_CURRENT_PARTITION = "mount -l | grep \"on / \" | grep -o 'mmc.*[0-9]p[0-9]'";
  private static final String CMD_LINUX_VERSION = "uname -a";
  private static final String CMD_WINDOWS_VERSION = "systeminfo | findstr Version";

  private final ShellCommander shellCommander = new ShellCommander();
  private NanoSatMOSupervisor nmfSupervisor;
  private OSValidator osValidator;

  @Parameter(description = "The version of the operating system.", generationEnabled = false, onGetFunction = "onGetOSVersion", readOnly = true, reportIntervalSeconds = 10)
  public String OSVersion = "";

  @Parameter(description = "The Current partition where the OS is running. Only works for linux", generationEnabled = false, onGetFunction = "onGetOSPartition", readOnly = true, reportIntervalSeconds = 10)
  public String OSPartition = "";

  public MCSupervisorBasicAdapter() {
    osValidator = new OSValidator();
  }

  public void setNmfSupervisor(NanoSatMOSupervisor supervisor) {
    nmfSupervisor = supervisor;
  }

  public void onGetOSVersion() {
    if (osValidator.isWindows()) {
      OSVersion = shellCommander.runCommandAndGetOutputMessage(CMD_WINDOWS_VERSION);
    } else {
      OSVersion = shellCommander.runCommandAndGetOutputMessage(CMD_LINUX_VERSION);
    }
  }

  public void onGetOSPartition() {
    if (osValidator.isUnix()) {
      OSPartition = shellCommander.runCommandAndGetOutputMessage(CMD_CURRENT_PARTITION);
    }
  }

  @Action(name = "NMEA_Sentence", description = "Forwards a raw NMEA query to the GNSS Provider")
  public UInteger nmeaAction(Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction,
      @ActionParameter(name = "arg") String arg) {
    try {
      nmfSupervisor.getPlatformServices().getGPSService().getNMEASentence(arg, new GPSConsumerAdapter());
    } catch (MALInteractionException | MALException | IOException | NMFException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
    }
    return null;
  }

  @Action(name = "Clock.setTimeUsingDeltaMilliseconds", description = "Sets the clock using a diff between the on-board time and the desired time.")
  public UInteger setTimeUsingDeltaMilliseconds(Long actionInstanceObjId, boolean reportProgress,
      MALInteraction interaction, @ActionParameter(name = "arg", rawUnit = "milliseconds") Long delta) {
    String str = (new SimpleDateFormat(DATE_PATTERN)).format(new Date(System.currentTimeMillis() + delta));

    ShellCommander shell = new ShellCommander();
    shell.runCommand("date -s \"" + str + " UTC\" | hwclock --systohc");
    return null;
  }

  private class GPSConsumerAdapter extends GPSAdapter {

    @Override
    public void getNMEASentenceResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
        Map qosProperties) {
      LOGGER.log(Level.WARNING, "Received response error");
    }

    @Override
    public void getNMEASentenceAckErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
      LOGGER.log(Level.WARNING, "Received ACK error");
    }

    @Override
    public void getNMEASentenceResponseReceived(MALMessageHeader msgHeader, String sentence, Map qosProperties) {
      LOGGER.log(Level.INFO, "Received message " + sentence);
    }

    @Override
    public void getNMEASentenceAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
      LOGGER.log(Level.INFO, "Received ack");
    }

  }

}
