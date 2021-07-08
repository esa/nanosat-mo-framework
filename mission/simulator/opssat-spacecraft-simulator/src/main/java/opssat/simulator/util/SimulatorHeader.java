/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2021      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 *  You may not use this file except in compliance with the License.
 * 
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *  
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 *  ----------------------------------------------------------------------------
 */
package opssat.simulator.util;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import opssat.simulator.gui.GuiSimulatorHeaderEdit;
import opssat.simulator.threading.SimulatorNode;

/**
 *
 * @author Cezar Suteu
 */
public class SimulatorHeader implements Serializable
{

  private boolean autoStartSystem;
  private boolean autoStartTime;
  private String keplerElements;
  private boolean useOrekitPropagator;
  private boolean updateInternet;
  private String orekitPropagator;
  private String orekitTLE1;
  private String orekitTLE2;
  private boolean useCelestia;
  private int celestiaPort;
  private int timeFactor = 1;
  private Date startDate;
  private Date endDate;
  public static final String FROM_START_FORMAT = "%05d:%02d:%02d:%02d:%03d";
  public final String DATE_FORMAT = "yyyy:MM:dd HH:mm:ss z";
  private final int MIN_TIME_FACTOR = 1;
  private final int MAX_TIME_FACTOR = 1000;
  private final int MIN_DATE_YEAR = 2016;
  private final int MAX_DATE_YEAR = 2030;

  public Date parseStringIntoDate(String fieldValue)
  {
    Date result = null;
    Date originalResult = null;
    DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    dateFormat.setLenient(true);
      try {
      result = dateFormat.parse(fieldValue);
      originalResult = dateFormat.parse(fieldValue);
    } catch (ParseException ex) {
      Logger.getLogger(GuiSimulatorHeaderEdit.class.getName()).log(Level.SEVERE, null, ex);
    }
    if (result != null) {
      DateFormat dateFormatYear = new SimpleDateFormat("yyyy");
      String originalYear = dateFormatYear.format(result);
      // System.out.println("Original year
      // "+originalYear+Integer.parseInt(originalYear));

      if (Integer.parseInt(originalYear) >= MIN_DATE_YEAR
          && Integer.parseInt(originalYear) < MAX_DATE_YEAR) {
        // Input year is ok, shift it back so it can be accepted with FALSE lenient
        // System.out.println("Original year is ok");
        String shiftedYear = String.valueOf(Integer.parseInt(originalYear) - 10);
        // System.out.println("Shifted year "+shiftedYear);
        String shiftedText = fieldValue;
        shiftedText = shiftedText.replaceAll(originalYear, shiftedYear);
        dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setLenient(false);
        result = null;
        try {
          dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
          result = dateFormat.parse(shiftedText);
        } catch (ParseException ex) {
          Logger.getLogger(GuiSimulatorHeaderEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (result != null) {
          // Lenient false was ok, return original parse with lenient true
          result = originalResult;
        }
      } else {
        // Input year is out of range
        // System.out.println("Original year is bad");
        result = null;
      }
    }
    return result;
  }

  public boolean validateTimeFactor(int newTimeFactor)
  {
    return newTimeFactor >= MIN_TIME_FACTOR && newTimeFactor <= MAX_TIME_FACTOR;
  }

  public String toFileString()
  {
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    return "#Run the processing of internal models\n" + "startModels=" + autoStartSystem + "\n"
        + "#Increment the simulated time (depends on startModels)\n" + "startTime=" + autoStartTime
        + "\n" + "#Speed up of time factor\n" + "timeFactor=" + timeFactor + "\n"
        + "#Kepler elements for orbit A[km];E;i[deg];RAAN[deg];ARG_PER[deg];TRUE_A[deg] 7021.0;0.0;98.05;340.0;0.0;0.0\n"
        + "keplerElements=" + SimulatorNode.DEFAULT_OPS_SAT_A + ";"
        + SimulatorNode.DEFAULT_OPS_SAT_E + ";" + SimulatorNode.DEFAULT_OPS_SAT_ORBIT_I + ";"
        + SimulatorNode.DEFAULT_OPS_SAT_RAAN + ";" + SimulatorNode.DEFAULT_OPS_SAT_ARG_PER + ";"
        + SimulatorNode.DEFAULT_OPS_SAT_TRUE_ANOMALY + "\n"
        + "#Enable the Orekit library for orbital and attitude simulation\n"
        + "orekit=" + useOrekitPropagator + "\n"
        + "#Enable updates from Internet (used for gps constellation TLEs)\n"
        + "updateFromInternet=" + updateInternet + "\n" + "#Configuration of the Celestia server\n"
        + "celestia=" + useCelestia + "\n" + "celestiaPort=" + celestiaPort + "\n"
        + "#Start and end dates of simulation\n" + "startDate=" + dateFormat.format(startDate)
        + "\n" + "endDate=" + dateFormat.format(endDate) + "\n"
        + "#Logging level to files found in $USER_HOME/.ops-sat-simulator/\n"
        + "#Possible values SEVERE,INFO,FINE,FINER,FINEST,ALL\n" + "centralLogLevel=INFO\n"
        + "simulatorLogLevel=INFO\n" + "consoleLogLevel=INFO";

  }

  @Override
  public String toString()
  {
    return "SimulatorHeader{" + "autoStartSystem=" + autoStartSystem + ", autoStartTime="
        + autoStartTime + ", timeFactor=" + timeFactor + ", startDate=" + startDate + ", endDate="
        + endDate + '}';
  }

  public boolean isAutoStartSystem()
  {
    return autoStartSystem;
  }

  public void setAutoStartSystem(boolean autoStartSystem)
  {
    this.autoStartSystem = autoStartSystem;
  }

  public boolean isAutoStartTime()
  {
    return autoStartTime;
  }

  public void setAutoStartTime(boolean autoStartTime)
  {
    this.autoStartTime = autoStartTime;
  }

  public int getTimeFactor()
  {
    return timeFactor;
  }

  public void setTimeFactor(int timeFactor)
  {
    this.timeFactor = timeFactor;
  }

  public Date getStartDate()
  {
    return startDate;
  }

  public String getStartDateString()
  {
    DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    return dateFormat.format(startDate);
  }

  public void setStartDate(Date startDate)
  {
    this.startDate = startDate;
  }

  public Date getEndDate()
  {
    return endDate;
  }

  public String getEndDateString()
  {
    DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    return dateFormat.format(endDate);
  }

  public void setEndDate(Date endDate)
  {
    this.endDate = endDate;
  }

  public boolean checkStartBeforeEnd()
  {
    return endDate.compareTo(startDate) >= 0;

  }

  public SimulatorHeader()
  {
    this.autoStartSystem = true;
    this.autoStartTime = true;
    this.startDate = new Date();
    this.endDate = new Date();
    this.useOrekitPropagator = true;
    this.celestiaPort = 5909;
    this.useCelestia = true;
  }

  public SimulatorHeader(boolean autoStart, Date startDate, Date endDate)
  {
    this.autoStartSystem = autoStart;

    this.startDate = startDate;
    this.endDate = endDate;
  }

  public int getYearStartDate()
  {
    return DateExtraction.getYearFromDate(this.startDate);
  }

  public int getMonthStartDate()
  {
    return DateExtraction.getMonthFromDate(this.startDate);
  }

  public int getDayStartDate()
  {
    return DateExtraction.getDayFromDate(this.startDate);
  }

  public int getHourStartDate()
  {
    return DateExtraction.getHourFromDate(this.startDate);
  }

  public int getMinuteStartDate()
  {
    return DateExtraction.getMinuteFromDate(this.startDate);
  }

  public int getSecondStartDate()
  {
    return DateExtraction.getSecondsFromDate(this.startDate);
  }

  public boolean isUseOrekitPropagator()
  {
    return useOrekitPropagator;
  }

  public void setUseOrekitPropagator(boolean useOrekitPropagator)
  {
    this.useOrekitPropagator = useOrekitPropagator;
  }

  public boolean isUseCelestia()
  {
    return useCelestia;
  }

  public void setUseCelestia(boolean useCelestia)
  {
    this.useCelestia = useCelestia;
  }

  public int getCelestiaPort()
  {
    return celestiaPort;
  }

  public void setCelestiaPort(int celestiaPort)
  {
    this.celestiaPort = celestiaPort;
  }

  public String getOrekitPropagator()
  {
    return orekitPropagator;
  }

  public void setOrekitPropagator(String orekitPropagator)
  {
    this.orekitPropagator = orekitPropagator;
  }

  public String getOrekitTLE1()
  {
    return orekitTLE1;
  }

  public void setOrekitTLE1(String orekitTLE1)
  {
    this.orekitTLE1 = orekitTLE1;
  }

  public String getOrekitTLE2()
  {
    return orekitTLE2;
  }

  public void setOrekitTLE2(String orekitTLE2)
  {
    this.orekitTLE2 = orekitTLE2;
  }

  public String getKeplerElements()
  {
    return keplerElements;
  }

  public void setKeplerElements(String keplerElements)
  {
    this.keplerElements = keplerElements;
  }

  public boolean isUpdateInternet()
  {
    return updateInternet;
  }

  public void setUpdateInternet(boolean updateInternet)
  {
    this.updateInternet = updateInternet;
  }

}
