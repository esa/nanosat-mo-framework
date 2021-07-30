package esa.nmf.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import opssat.simulator.threading.SimulatorNode;
import opssat.simulator.util.SimulatorHeader;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.orekit.propagation.analytical.tle.TLE;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RegressionTest2
{

  public static boolean debug = false;

  @Test
  public void test1001() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1001");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    simulatorHeader12.setOrekitTLE1("");
    java.util.Date date17 = simulatorHeader12.parseStringIntoDate("2019/05/23-15:09:35");
    boolean boolean18 = simulatorHeader12.isUseOrekitPropagator();
    java.lang.String str19 = simulatorHeader12.getStartDateString();
    simulatorHeader12.setOrekitTLE2("0000.0000");
    boolean boolean22 = simulatorHeader12.isUseOrekitPropagator();
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertNull(date17);
    org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + false + "'", !boolean18);
    org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + false + "'", !boolean22);
  }

  @Test
  public void test1002() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1002");
    }
    org.ccsds.moims.mo.mal.structures.StringList stringList0 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    org.ccsds.moims.mo.mal.structures.ShortList shortList2 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            ' ');
    java.util.ListIterator<java.lang.Short> shortItor3 = shortList2.listIterator();
    boolean boolean4 = stringList0.equals(shortList2);
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience13 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    double double14 = gPSSatInViewScience13.getMaxDistance();
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience23 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience32 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    double double33 = gPSSatInViewScience32.getMaxDistance();
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience42 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience51 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    double double52 = gPSSatInViewScience51.getStdDevElevation();
    opssat.simulator.orekit.GPSSatInViewScience[] gPSSatInViewScienceArray53 =
        new opssat.simulator.orekit.GPSSatInViewScience[]{
          gPSSatInViewScience13, gPSSatInViewScience23, gPSSatInViewScience32, gPSSatInViewScience42,
          gPSSatInViewScience51};
    opssat.simulator.orekit.GPSSatInViewScience[] gPSSatInViewScienceArray54 = shortList2
        .toArray(gPSSatInViewScienceArray53);
    shortList2.ensureCapacity(44);
    int int57 = shortList2.size();
    java.util.stream.Stream<java.lang.Short> shortStream58 = shortList2.parallelStream();
    opssat.simulator.util.SimulatorData simulatorData61 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date62 = simulatorData61.getCurrentTime();
    int int63 = opssat.simulator.util.DateExtraction.getDayFromDate(date62);
    int int64 = opssat.simulator.util.DateExtraction.getHourFromDate(date62);
    opssat.simulator.util.SimulatorData simulatorData65 = new opssat.simulator.util.SimulatorData(
        48, date62);
    boolean boolean66 = simulatorData65.isTimeRunning();
    int int67 = shortList2.lastIndexOf(simulatorData65);
    try {
      java.lang.Short short69 = shortList2.remove(0);
      org.junit.Assert.fail(
          "Expected exception of type java.lang.IndexOutOfBoundsException; message: Index: 0, Size: 0");
    } catch (java.lang.IndexOutOfBoundsException e) {
    }
    org.junit.Assert.assertNotNull(shortItor3);
    org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
    org.junit.Assert.assertTrue("'" + double14 + "' != '" + 0.0d + "'", double14 == 0.0d);
    org.junit.Assert.assertTrue("'" + double33 + "' != '" + 0.0d + "'", double33 == 0.0d);
    org.junit.Assert.assertTrue("'" + double52 + "' != '" + 11111.0d + "'", double52 == 11111.0d);
    org.junit.Assert.assertNotNull(gPSSatInViewScienceArray53);
    org.junit.Assert.assertNotNull(gPSSatInViewScienceArray54);
    org.junit.Assert.assertTrue("'" + int57 + "' != '" + 0 + "'", int57 == 0);
    org.junit.Assert.assertNotNull(shortStream58);
    org.junit.Assert.assertNotNull(date62);
    org.junit.Assert.assertTrue("'" + boolean66 + "' != '" + false + "'", !boolean66);
    org.junit.Assert.assertTrue("'" + int67 + "' != '" + (-1) + "'", int67 == (-1));
  }

  @Test
  public void test1003() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1003");
    }
    org.ccsds.moims.mo.mal.structures.Identifier identifier1 =
        new org.ccsds.moims.mo.mal.structures.Identifier(
            "[]");
    java.lang.Long long2 = identifier1.getShortForm();
    java.lang.String str3 = identifier1.getValue();
    org.ccsds.moims.mo.mal.structures.OctetList octetList4 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int5 = octetList4.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort6 = octetList4.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor7 = octetList4.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor9 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList4, "hi!");
    argumentDescriptor9.restoreArgument();
    argumentDescriptor9.restoreArgument();
    argumentDescriptor9.restoreArgument();
    java.lang.String str13 = argumentDescriptor9.getName();
    boolean boolean14 = identifier1.equals(str13);
    org.junit.Assert.assertTrue("'" + long2 + "' != '" + 281474993487878L + "'",
        long2.equals(281474993487878L));
    org.junit.Assert.assertTrue("'" + str3 + "' != '" + "[]" + "'", str3.equals("[]"));
    org.junit.Assert.assertTrue("'" + int5 + "' != '" + (-7) + "'", int5.equals((-7)));
    org.junit.Assert.assertNotNull(uShort6);
    org.junit.Assert.assertNotNull(byteItor7);
    org.junit.Assert.assertTrue("'" + str13 + "' != '" + "hi!" + "'", str13.equals("hi!"));
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
  }

  @Test
  public void test1004() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1004");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    java.lang.String str14 = simulatorHeader12.getOrekitTLE2();
    int int15 = simulatorHeader12.getMinuteStartDate();
    java.util.Date date16 = simulatorHeader12.getEndDate();
    opssat.simulator.util.SimulatorData simulatorData19 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date20 = simulatorData19.getCurrentTime();
    int int21 = opssat.simulator.util.DateExtraction.getDayFromDate(date20);
    opssat.simulator.util.SimulatorData simulatorData23 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date24 = simulatorData23.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData26 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date27 = simulatorData26.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap28 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date24, date27);
    opssat.simulator.util.SimulatorHeader simulatorHeader29 =
        new opssat.simulator.util.SimulatorHeader(
            false, date20, date27);
    simulatorHeader12.setEndDate(date20);
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertNull(str14);
    org.junit.Assert.assertNotNull(date16);
    org.junit.Assert.assertNotNull(date20);
    org.junit.Assert.assertNotNull(date24);
    org.junit.Assert.assertNotNull(date27);
    org.junit.Assert.assertNotNull(timeUnitMap28);
  }

  @Test
  public void test1005() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1005");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    int int13 = opssat.simulator.util.DateExtraction.getMinuteFromDate(date3);
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
  }

  @Test
  public void test1006() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1006");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet3 = octetList0.getAreaVersion();
    opssat.simulator.util.wav.WavFileException wavFileException5 =
        new opssat.simulator.util.wav.WavFileException(
            "UnknownGUIData");
    java.lang.Throwable[] throwableArray6 = wavFileException5.getSuppressed();
    boolean boolean7 = octetList0.equals(wavFileException5);
    org.ccsds.moims.mo.mal.structures.UShort uShort8 = octetList0.getServiceNumber();
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(uOctet3);
    org.junit.Assert.assertNotNull(throwableArray6);
    org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
    org.junit.Assert.assertNotNull(uShort8);
  }

  @Test
  public void test1007() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1007");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    opssat.simulator.util.SimulatorData simulatorData17 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date18 = simulatorData17.getCurrentTime();
    java.util.Date date19 = simulatorData17.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData20 = new opssat.simulator.util.SimulatorData(
        17, date19);
    opssat.simulator.util.SimulatorData simulatorData23 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date24 = simulatorData23.getCurrentTime();
    int int25 = opssat.simulator.util.DateExtraction.getDayFromDate(date24);
    opssat.simulator.util.SimulatorData simulatorData27 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date28 = simulatorData27.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData30 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date31 = simulatorData30.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap32 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date28, date31);
    opssat.simulator.util.SimulatorHeader simulatorHeader33 =
        new opssat.simulator.util.SimulatorHeader(
            false, date24, date31);
    opssat.simulator.util.SimulatorHeader simulatorHeader34 =
        new opssat.simulator.util.SimulatorHeader(
            false, date19, date24);
    simulatorHeader12.setEndDate(date19);
    simulatorHeader12.setUseCelestia(true);
    java.lang.String str38 = simulatorHeader12.DATE_FORMAT;
    boolean boolean39 = simulatorHeader12.checkStartBeforeEnd();
    boolean boolean40 = simulatorHeader12.isUseCelestia();
    java.lang.String str41 = simulatorHeader12.toFileString();
    int int42 = simulatorHeader12.getTimeFactor();
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertNotNull(date18);
    org.junit.Assert.assertNotNull(date19);
    org.junit.Assert.assertNotNull(date24);
    org.junit.Assert.assertNotNull(date28);
    org.junit.Assert.assertNotNull(date31);
    org.junit.Assert.assertNotNull(timeUnitMap32);
    org.junit.Assert.assertTrue("'" + str38 + "' != '" + "yyyy:MM:dd HH:mm:ss z" + "'",
        str38.equals("yyyy:MM:dd HH:mm:ss z"));
    org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + true + "'", boolean39);
    org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + true + "'", boolean40);
// flaky:         org.junit.Assert.assertTrue("'" + str41 + "' != '" + "#Run the processing of internal models\nstartModels=false\n#Increment the simulated time (depends on startModels)\nstartTime=false\n#Speed up of time factor\ntimeFactor=1\n#Kepler elements for orbit A[km];E;i[deg];RAAN[deg];ARG_PER[deg];TRUE_A[deg] 7021.0;0.0;98.05;340.0;0.0;0.0\nkeplerElements=7021.0;0.0;98.05;340.0;0.0;0.0\n#Enable the Orekit library\norekit=false\n#Enable updates from Internet (used for gps constellation TLEs)\nupdateFromInternet=false\n#Configuration of the Celestia server\ncelestia=true\ncelestiaPort=0\n#Start and end dates of simulation\nstartDate=2019:05:23 15:10:41 UTC\nendDate=2019:05:23 15:10:41 UTC\n#Logging level to files found in $USER_HOME/.ops-sat-simulator/\n#Possible values SEVERE,INFO,FINE,FINER,FINEST,ALL\ncentralLogLevel=INFO\nsimulatorLogLevel=INFO\nconsoleLogLevel=INFO" + "'", str41.equals("#Run the processing of internal models\nstartModels=false\n#Increment the simulated time (depends on startModels)\nstartTime=false\n#Speed up of time factor\ntimeFactor=1\n#Kepler elements for orbit A[km];E;i[deg];RAAN[deg];ARG_PER[deg];TRUE_A[deg] 7021.0;0.0;98.05;340.0;0.0;0.0\nkeplerElements=7021.0;0.0;98.05;340.0;0.0;0.0\n#Enable the Orekit library\norekit=false\n#Enable updates from Internet (used for gps constellation TLEs)\nupdateFromInternet=false\n#Configuration of the Celestia server\ncelestia=true\ncelestiaPort=0\n#Start and end dates of simulation\nstartDate=2019:05:23 15:10:41 UTC\nendDate=2019:05:23 15:10:41 UTC\n#Logging level to files found in $USER_HOME/.ops-sat-simulator/\n#Possible values SEVERE,INFO,FINE,FINER,FINEST,ALL\ncentralLogLevel=INFO\nsimulatorLogLevel=INFO\nconsoleLogLevel=INFO"));
    org.junit.Assert.assertTrue("'" + int42 + "' != '" + 1 + "'", int42 == 1);
  }

  @Test
  public void test1008() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1008");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    boolean boolean14 = simulatorHeader12.isAutoStartSystem();
    int int15 = simulatorHeader12.getDayStartDate();
    boolean boolean16 = simulatorHeader12.isUseCelestia();
    opssat.simulator.util.SimulatorData simulatorData19 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date20 = simulatorData19.getCurrentTime();
    int int21 = opssat.simulator.util.DateExtraction.getDayFromDate(date20);
    opssat.simulator.util.SimulatorData simulatorData23 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date24 = simulatorData23.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData26 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date27 = simulatorData26.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap28 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date24, date27);
    opssat.simulator.util.SimulatorHeader simulatorHeader29 =
        new opssat.simulator.util.SimulatorHeader(
            false, date20, date27);
    simulatorHeader12.setStartDate(date27);
    simulatorHeader12.setKeplerElements("-0100.0000000");
    boolean boolean34 = simulatorHeader12.validateTimeFactor(23);
    java.lang.String str35 = simulatorHeader12.getStartDateString();
    simulatorHeader12.setCelestiaPort((-2));
    simulatorHeader12.setUpdateInternet(false);
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
    org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
    org.junit.Assert.assertNotNull(date20);
    org.junit.Assert.assertNotNull(date24);
    org.junit.Assert.assertNotNull(date27);
    org.junit.Assert.assertNotNull(timeUnitMap28);
    org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + true + "'", boolean34);
  }

  @Test
  public void test1010() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1010");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState11 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double12 = simulatorSpacecraftState11.getLatitude();
    java.lang.String str13 = simulatorSpacecraftState11.getMagField();
    float[] floatArray14 = simulatorSpacecraftState11.getR();
    simulatorSpacecraftState3.setQ(floatArray14);
    java.lang.String str16 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray14);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState20 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double21 = simulatorSpacecraftState20.getLatitude();
    double double22 = simulatorSpacecraftState20.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState26 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double27 = simulatorSpacecraftState26.getLatitude();
    java.lang.String str28 = simulatorSpacecraftState26.getMagField();
    float[] floatArray29 = simulatorSpacecraftState26.getR();
    simulatorSpacecraftState20.setQ(floatArray29);
    float[] floatArray31 = simulatorSpacecraftState20.getV();
    opssat.simulator.celestia.CelestiaData celestiaData32 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray14, floatArray31);
    java.lang.String str33 = celestiaData32.getInfo();
    java.lang.String str34 = celestiaData32.getAnx();
    java.lang.String str35 = celestiaData32.getDnx();
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + 340.0d + "'", double12 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str13 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str13.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray14);
    org.junit.Assert.assertTrue("'" + str16 + "' != '" + "UnknownGUIData" + "'",
        str16.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double21 + "' != '" + 340.0d + "'", double21 == 340.0d);
    org.junit.Assert.assertTrue("'" + double22 + "' != '" + 340.0d + "'", double22 == 340.0d);
    org.junit.Assert.assertTrue("'" + double27 + "' != '" + 340.0d + "'", double27 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str28 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str28.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray29);
    org.junit.Assert.assertNotNull(floatArray31);
    org.junit.Assert.assertNull(str33);
    org.junit.Assert.assertNull(str34);
    org.junit.Assert.assertNull(str35);
  }

  @Test
  public void test1011() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1011");
    }
    opssat.simulator.util.SimulatorTimer simulatorTimer2 = new opssat.simulator.util.SimulatorTimer(
        "SimulatorData", 1L);
    boolean boolean3 = simulatorTimer2.isElapsed();
    simulatorTimer2.setDuration((-10));
    simulatorTimer2.setDuration((-13));
    simulatorTimer2.timeElapsed(38);
    org.junit.Assert.assertTrue("'" + boolean3 + "' != '" + false + "'", !boolean3);
  }

  @Test
  public void test1013() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1013");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    long long3 = simulatorData2.getCurrentTimeMillis();
    java.lang.String str4 = simulatorData2.getCurrentDay();
    java.lang.String str5 = simulatorData2.getUTCCurrentTime2();
    simulatorData2.toggleSimulatorRunning();
    java.lang.String str7 = simulatorData2.getUTCCurrentTime();
    java.util.Date date8 = simulatorData2.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData12 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date13 = simulatorData12.getCurrentTime();
    java.util.Date date14 = simulatorData12.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData15 = new opssat.simulator.util.SimulatorData(
        17, date14);
    opssat.simulator.util.SimulatorData simulatorData18 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date19 = simulatorData18.getCurrentTime();
    int int20 = opssat.simulator.util.DateExtraction.getDayFromDate(date19);
    opssat.simulator.util.SimulatorData simulatorData22 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date23 = simulatorData22.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData25 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date26 = simulatorData25.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap27 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date23, date26);
    opssat.simulator.util.SimulatorHeader simulatorHeader28 =
        new opssat.simulator.util.SimulatorHeader(
            false, date19, date26);
    opssat.simulator.util.SimulatorHeader simulatorHeader29 =
        new opssat.simulator.util.SimulatorHeader(
            false, date14, date19);
    opssat.simulator.util.SimulatorData simulatorData33 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date34 = simulatorData33.getCurrentTime();
    java.util.Date date35 = simulatorData33.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData36 = new opssat.simulator.util.SimulatorData(
        17, date35);
    opssat.simulator.util.SimulatorData simulatorData39 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date40 = simulatorData39.getCurrentTime();
    int int41 = opssat.simulator.util.DateExtraction.getDayFromDate(date40);
    opssat.simulator.util.SimulatorData simulatorData43 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date44 = simulatorData43.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData46 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date47 = simulatorData46.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap48 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date44, date47);
    opssat.simulator.util.SimulatorHeader simulatorHeader49 =
        new opssat.simulator.util.SimulatorHeader(
            false, date40, date47);
    opssat.simulator.util.SimulatorHeader simulatorHeader50 =
        new opssat.simulator.util.SimulatorHeader(
            false, date35, date40);
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap51 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date19, date35);
    opssat.simulator.util.SimulatorHeader simulatorHeader52 =
        new opssat.simulator.util.SimulatorHeader(
            true, date8, date35);
    boolean boolean53 = simulatorHeader52.checkStartBeforeEnd();
    simulatorHeader52.setOrekitTLE2("3800.0000000");
    org.junit.Assert.assertTrue("'" + long3 + "' != '" + 0L + "'", long3 == 0L);
    org.junit.Assert.assertNotNull(date8);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertNotNull(date14);
    org.junit.Assert.assertNotNull(date19);
    org.junit.Assert.assertNotNull(date23);
    org.junit.Assert.assertNotNull(date26);
    org.junit.Assert.assertNotNull(timeUnitMap27);
    org.junit.Assert.assertNotNull(date34);
    org.junit.Assert.assertNotNull(date35);
    org.junit.Assert.assertNotNull(date40);
    org.junit.Assert.assertNotNull(date44);
    org.junit.Assert.assertNotNull(date47);
    org.junit.Assert.assertNotNull(timeUnitMap48);
    org.junit.Assert.assertNotNull(timeUnitMap51);
    org.junit.Assert.assertTrue("'" + boolean53 + "' != '" + true + "'", boolean53);
  }

  @Test
  public void test1015() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1015");
    }
    org.ccsds.moims.mo.mal.structures.Duration duration1 =
        new org.ccsds.moims.mo.mal.structures.Duration(
            0.0d);
    java.lang.Double[] doubleArray6 = new java.lang.Double[]{(-1.0d), 100.0d, 10.0d, 10.0d};
    java.util.ArrayList<java.lang.Double> doubleList7 = new java.util.ArrayList<java.lang.Double>();
    boolean boolean8 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Double>) doubleList7, doubleArray6);
    org.ccsds.moims.mo.mal.structures.UShort uShort9 =
        org.ccsds.moims.mo.mal.structures.URIList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort[] uShortArray10 =
        new org.ccsds.moims.mo.mal.structures.UShort[]{
          uShort9};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList11 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
    boolean boolean12 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList11,
        uShortArray10);
    uShortList11.ensureCapacity(0);
    int int16 = uShortList11.indexOf((byte) 1);
    uShortList11.clear();
    java.lang.Long[] longArray20 = new java.lang.Long[]{0L, 10L};
    java.util.ArrayList<java.lang.Long> longList21 = new java.util.ArrayList<java.lang.Long>();
    boolean boolean22 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Long>) longList21, longArray20);
    java.lang.Object obj23 = longList21.clone();
    boolean boolean24 = uShortList11.contains(longList21);
    boolean boolean25 = doubleList7.equals(boolean24);
    java.lang.Integer[] intArray28 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList29 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean30 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList29, intArray28);
    int int32 = intList29.lastIndexOf((byte) 10);
    boolean boolean33 = doubleList7.removeAll(intList29);
    java.lang.Object obj34 = doubleList7.clone();
    boolean boolean35 = duration1.equals(doubleList7);
    java.lang.Object obj36 = null;
    int int37 = doubleList7.indexOf(obj36);
    java.util.Iterator<java.lang.Double> doubleItor38 = doubleList7.iterator();
    org.junit.Assert.assertNotNull(doubleArray6);
    org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + true + "'", boolean8);
    org.junit.Assert.assertNotNull(uShort9);
    org.junit.Assert.assertNotNull(uShortArray10);
    org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + true + "'", boolean12);
    org.junit.Assert.assertTrue("'" + int16 + "' != '" + (-1) + "'", int16 == (-1));
    org.junit.Assert.assertNotNull(longArray20);
    org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
    org.junit.Assert.assertNotNull(obj23);
    org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + false + "'", !boolean24);
    org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + false + "'", !boolean25);
    org.junit.Assert.assertNotNull(intArray28);
    org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + true + "'", boolean30);
    org.junit.Assert.assertTrue("'" + int32 + "' != '" + (-1) + "'", int32 == (-1));
    org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + false + "'", !boolean33);
    org.junit.Assert.assertNotNull(obj34);
    org.junit.Assert.assertTrue("'" + boolean35 + "' != '" + false + "'", !boolean35);
    org.junit.Assert.assertTrue("'" + int37 + "' != '" + (-1) + "'", int37 == (-1));
    org.junit.Assert.assertNotNull(doubleItor38);
  }

  @Test
  public void test1016() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1016");
    }
    org.ccsds.moims.mo.mal.structures.UShort uShort0 =
        org.ccsds.moims.mo.mal.structures.URIList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort[] uShortArray1 =
        new org.ccsds.moims.mo.mal.structures.UShort[]{
          uShort0};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList2 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
    boolean boolean3 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList2, uShortArray1);
    uShortList2.ensureCapacity(0);
    int int7 = uShortList2.indexOf((byte) 1);
    uShortList2.clear();
    java.lang.Long[] longArray11 = new java.lang.Long[]{0L, 10L};
    java.util.ArrayList<java.lang.Long> longList12 = new java.util.ArrayList<java.lang.Long>();
    boolean boolean13 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Long>) longList12, longArray11);
    java.lang.Object obj14 = longList12.clone();
    boolean boolean15 = uShortList2.contains(longList12);
    java.util.ListIterator<java.lang.Long> longItor16 = longList12.listIterator();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState20 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double21 = simulatorSpacecraftState20.getLatitude();
    java.lang.String str22 = simulatorSpacecraftState20.getMagField();
    java.lang.String str23 = simulatorSpacecraftState20.toString();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState27 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double28 = simulatorSpacecraftState27.getLatitude();
    java.lang.String str29 = simulatorSpacecraftState27.getMagField();
    java.lang.String str30 = simulatorSpacecraftState27.toString();
    double[] doubleArray31 = simulatorSpacecraftState27.getSunVector();
    simulatorSpacecraftState20.setMagnetometer(doubleArray31);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState36 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double37 = simulatorSpacecraftState36.getLatitude();
    java.lang.String str38 = simulatorSpacecraftState36.getMagField();
    java.lang.String str39 = simulatorSpacecraftState36.toString();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState43 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double44 = simulatorSpacecraftState43.getLatitude();
    java.lang.String str45 = simulatorSpacecraftState43.getMagField();
    java.lang.String str46 = simulatorSpacecraftState43.toString();
    double[] doubleArray47 = simulatorSpacecraftState43.getSunVector();
    simulatorSpacecraftState36.setMagnetometer(doubleArray47);
    simulatorSpacecraftState20.setMagnetometer(doubleArray47);
    float[] floatArray50 = null;
    simulatorSpacecraftState20.setRv(floatArray50);
    int int52 = longList12.lastIndexOf(simulatorSpacecraftState20);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState56 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState56.setLongitude(4);
    int int59 = simulatorSpacecraftState56.getSatsInView();
    float[] floatArray60 = simulatorSpacecraftState56.getRv();
    boolean boolean61 = longList12.equals(floatArray60);
    int int62 = longList12.size();
    java.util.stream.Stream<java.lang.Long> longStream63 = longList12.parallelStream();
    org.junit.Assert.assertNotNull(uShort0);
    org.junit.Assert.assertNotNull(uShortArray1);
    org.junit.Assert.assertTrue("'" + boolean3 + "' != '" + true + "'", boolean3);
    org.junit.Assert.assertTrue("'" + int7 + "' != '" + (-1) + "'", int7 == (-1));
    org.junit.Assert.assertNotNull(longArray11);
    org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
    org.junit.Assert.assertNotNull(obj14);
    org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
    org.junit.Assert.assertNotNull(longItor16);
    org.junit.Assert.assertTrue("'" + double21 + "' != '" + 340.0d + "'", double21 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str22 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str22.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str23 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str23.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertTrue("'" + double28 + "' != '" + 340.0d + "'", double28 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str29 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str29.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str30 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str30.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray31);
    org.junit.Assert.assertTrue("'" + double37 + "' != '" + 340.0d + "'", double37 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str38 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str38.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str39 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str39.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertTrue("'" + double44 + "' != '" + 340.0d + "'", double44 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str45 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str45.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str46 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str46.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray47);
    org.junit.Assert.assertTrue("'" + int52 + "' != '" + (-1) + "'", int52 == (-1));
    org.junit.Assert.assertTrue("'" + int59 + "' != '" + 0 + "'", int59 == 0);
    org.junit.Assert.assertNotNull(floatArray60);
    org.junit.Assert.assertTrue("'" + boolean61 + "' != '" + false + "'", !boolean61);
    org.junit.Assert.assertTrue("'" + int62 + "' != '" + 2 + "'", int62 == 2);
    org.junit.Assert.assertNotNull(longStream63);
  }

  @Test
  public void test1017() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1017");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor3 = octetList0.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor5 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList0, "hi!");
    argumentDescriptor5.restoreArgument();
    argumentDescriptor5.restoreArgument();
    argumentDescriptor5.restoreArgument();
    java.lang.String str9 = argumentDescriptor5.toString();
    try {
      int int11 = argumentDescriptor5.getTypeAsIntByIndex((-18));
      org.junit.Assert.fail(
          "Expected exception of type java.lang.ClassCastException; message: org.ccsds.moims.mo.mal.structures.OctetList cannot be cast to [I");
    } catch (java.lang.ClassCastException e) {
    }
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(byteItor3);
    org.junit.Assert.assertTrue("'" + str9 + "' != '" + "" + "'", str9.equals(""));
  }

  @Test
  public void test1018() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1018");
    }
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience8 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    double double9 = gPSSatInViewScience8.getMaxDistance();
    double double10 = gPSSatInViewScience8.getStdDevDistance();
    double double11 = gPSSatInViewScience8.getMaxElevation();
    org.junit.Assert.assertTrue("'" + double9 + "' != '" + 0.0d + "'", double9 == 0.0d);
    org.junit.Assert.assertTrue("'" + double10 + "' != '" + 58.0d + "'", double10 == 58.0d);
    org.junit.Assert.assertTrue("'" + double11 + "' != '" + 56.0d + "'", double11 == 56.0d);
  }

  @Test
  public void test1019() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1019");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    float[] floatArray8 = simulatorSpacecraftState3.getQ();
    double double9 = simulatorSpacecraftState3.getLongitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState13 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray16 = new float[]{28, 8};
    simulatorSpacecraftState13.setQ(floatArray16);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState21 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double22 = simulatorSpacecraftState21.getLatitude();
    java.lang.String str23 = simulatorSpacecraftState21.getMagField();
    float[] floatArray24 = simulatorSpacecraftState21.getR();
    simulatorSpacecraftState13.setQ(floatArray24);
    java.lang.String str26 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray24);
    simulatorSpacecraftState3.setRv(floatArray24);
    java.lang.String str28 = simulatorSpacecraftState3.getSunVectorAsString();
    float[] floatArray29 = simulatorSpacecraftState3.getRv();
    org.ccsds.moims.mo.mal.structures.Identifier identifier30 =
        new org.ccsds.moims.mo.mal.structures.Identifier();
    org.ccsds.moims.mo.mal.structures.OctetList octetList31 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int32 = octetList31.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort33 = octetList31.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor34 = octetList31.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor36 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList31, "hi!");
    argumentDescriptor36.restoreArgument();
    argumentDescriptor36.restoreArgument();
    argumentDescriptor36.restoreArgument();
    java.lang.String str40 = argumentDescriptor36.toString();
    boolean boolean41 = identifier30.equals(str40);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState45 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray48 = new float[]{28, 8};
    simulatorSpacecraftState45.setQ(floatArray48);
    float[] floatArray50 = simulatorSpacecraftState45.getQ();
    double double51 = simulatorSpacecraftState45.getLongitude();
    boolean boolean52 = identifier30.equals(simulatorSpacecraftState45);
    float[] floatArray53 = simulatorSpacecraftState45.getQ();
    double double54 = simulatorSpacecraftState45.getLongitude();
    double double55 = simulatorSpacecraftState45.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState59 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState63 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double64 = simulatorSpacecraftState63.getLatitude();
    java.lang.String str65 = simulatorSpacecraftState63.getMagField();
    java.lang.String str66 = simulatorSpacecraftState63.toString();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState70 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double71 = simulatorSpacecraftState70.getLatitude();
    java.lang.String str72 = simulatorSpacecraftState70.getMagField();
    java.lang.String str73 = simulatorSpacecraftState70.toString();
    double[] doubleArray74 = simulatorSpacecraftState70.getSunVector();
    simulatorSpacecraftState63.setMagnetometer(doubleArray74);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState79 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double80 = simulatorSpacecraftState79.getLatitude();
    java.lang.String str81 = simulatorSpacecraftState79.getMagField();
    java.lang.String str82 = simulatorSpacecraftState79.toString();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState86 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double87 = simulatorSpacecraftState86.getLatitude();
    java.lang.String str88 = simulatorSpacecraftState86.getMagField();
    java.lang.String str89 = simulatorSpacecraftState86.toString();
    double[] doubleArray90 = simulatorSpacecraftState86.getSunVector();
    simulatorSpacecraftState79.setMagnetometer(doubleArray90);
    simulatorSpacecraftState63.setMagnetometer(doubleArray90);
    simulatorSpacecraftState59.setMagField(doubleArray90);
    simulatorSpacecraftState45.setMagField(doubleArray90);
    simulatorSpacecraftState3.setMagField(doubleArray90);
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertNotNull(floatArray8);
    org.junit.Assert.assertTrue("'" + double9 + "' != '" + (-1.0d) + "'", double9 == (-1.0d));
    org.junit.Assert.assertNotNull(floatArray16);
    org.junit.Assert.assertTrue("'" + double22 + "' != '" + 340.0d + "'", double22 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str23 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str23.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray24);
    org.junit.Assert.assertTrue("'" + str26 + "' != '" + "UnknownGUIData" + "'",
        str26.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue(
        "'" + str28 + "' != '" + "X:[+.00000] ; Y:[+.00000] ; Z: [+.00000] units [N/A]" + "'",
        str28.equals("X:[+.00000] ; Y:[+.00000] ; Z: [+.00000] units [N/A]"));
    org.junit.Assert.assertNotNull(floatArray29);
    org.junit.Assert.assertTrue("'" + int32 + "' != '" + (-7) + "'", int32.equals((-7)));
    org.junit.Assert.assertNotNull(uShort33);
    org.junit.Assert.assertNotNull(byteItor34);
    org.junit.Assert.assertTrue("'" + str40 + "' != '" + "" + "'", str40.equals(""));
    org.junit.Assert.assertTrue("'" + boolean41 + "' != '" + false + "'", !boolean41);
    org.junit.Assert.assertNotNull(floatArray48);
    org.junit.Assert.assertNotNull(floatArray50);
    org.junit.Assert.assertTrue("'" + double51 + "' != '" + (-1.0d) + "'", double51 == (-1.0d));
    org.junit.Assert.assertTrue("'" + boolean52 + "' != '" + false + "'", !boolean52);
    org.junit.Assert.assertNotNull(floatArray53);
    org.junit.Assert.assertTrue("'" + double54 + "' != '" + (-1.0d) + "'", double54 == (-1.0d));
    org.junit.Assert.assertTrue("'" + double55 + "' != '" + 340.0d + "'", double55 == 340.0d);
    org.junit.Assert.assertTrue("'" + double64 + "' != '" + 340.0d + "'", double64 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str65 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str65.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str66 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str66.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertTrue("'" + double71 + "' != '" + 340.0d + "'", double71 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str72 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str72.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str73 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str73.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray74);
    org.junit.Assert.assertTrue("'" + double80 + "' != '" + 340.0d + "'", double80 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str81 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str81.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str82 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str82.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertTrue("'" + double87 + "' != '" + 340.0d + "'", double87 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str88 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str88.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str89 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str89.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray90);
  }

  @Test
  public void test1020() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1020");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    boolean boolean14 = simulatorHeader12.isAutoStartSystem();
    int int15 = simulatorHeader12.getDayStartDate();
    boolean boolean16 = simulatorHeader12.isUseCelestia();
    int int17 = simulatorHeader12.getDayStartDate();
    simulatorHeader12.setAutoStartTime(false);
    java.lang.String str20 = simulatorHeader12.getOrekitTLE1();
    java.util.Date date22 = simulatorHeader12.parseStringIntoDate("2019/05/23-15:10:09");
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
    org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
    org.junit.Assert.assertNull(str20);
    org.junit.Assert.assertNull(date22);
  }

  @Test
  public void test1021() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1021");
    }
    opssat.simulator.util.wav.WavFileException wavFileException0 =
        new opssat.simulator.util.wav.WavFileException();
    org.ccsds.moims.mo.mal.structures.OctetList octetList1 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int2 = octetList1.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort3 = octetList1.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet4 = octetList1.getAreaVersion();
    opssat.simulator.util.wav.WavFileException wavFileException6 =
        new opssat.simulator.util.wav.WavFileException(
            "UnknownGUIData");
    java.lang.Throwable[] throwableArray7 = wavFileException6.getSuppressed();
    boolean boolean8 = octetList1.equals(wavFileException6);
    wavFileException0.addSuppressed(wavFileException6);
    java.lang.String str10 = wavFileException0.toString();
    org.junit.Assert.assertTrue("'" + int2 + "' != '" + (-7) + "'", int2.equals((-7)));
    org.junit.Assert.assertNotNull(uShort3);
    org.junit.Assert.assertNotNull(uOctet4);
    org.junit.Assert.assertNotNull(throwableArray7);
    org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + false + "'", !boolean8);
    org.junit.Assert.assertTrue(
        "'" + str10 + "' != '" + "opssat.simulator.util.wav.WavFileException" + "'",
        str10.equals("opssat.simulator.util.wav.WavFileException"));
  }

  @Test
  public void test1022() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1022");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double4 = simulatorSpacecraftState3.getLatitude();
    simulatorSpacecraftState3.setLongitude(13);
    simulatorSpacecraftState3.setAltitude(66);
    simulatorSpacecraftState3.setAltitude(31);
    double double11 = simulatorSpacecraftState3.getAltitude();
    int int12 = simulatorSpacecraftState3.getSatsInView();
    org.junit.Assert.assertTrue("'" + double4 + "' != '" + 340.0d + "'", double4 == 340.0d);
    org.junit.Assert.assertTrue("'" + double11 + "' != '" + 31.0d + "'", double11 == 31.0d);
    org.junit.Assert.assertTrue("'" + int12 + "' != '" + 0 + "'", int12 == 0);
  }

  @Test
  public void test1023() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1023");
    }
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience8 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            (-30), 64, 7, 281475010265070L, (-1.0f), 281474993487879L, (byte) 1, (short) 100);
    double double9 = gPSSatInViewScience8.getAvgDistance();
    double double10 = gPSSatInViewScience8.getStdDevDistance();
    double double11 = gPSSatInViewScience8.getMaxDistance();
    double double12 = gPSSatInViewScience8.getStdDevElevation();
    double double13 = gPSSatInViewScience8.getMaxElevation();
    double double14 = gPSSatInViewScience8.getAvgElevation();
    org.junit.Assert.assertTrue("'" + double9 + "' != '" + (-1.0d) + "'", double9 == (-1.0d));
    org.junit.Assert.assertTrue("'" + double10 + "' != '" + 1.0d + "'", double10 == 1.0d);
    org.junit.Assert.assertTrue("'" + double11 + "' != '" + 64.0d + "'", double11 == 64.0d);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + 100.0d + "'", double12 == 100.0d);
    org.junit.Assert.assertTrue("'" + double13 + "' != '" + 2.8147501026507E14d + "'",
        double13 == 2.8147501026507E14d);
    org.junit.Assert.assertTrue("'" + double14 + "' != '" + 2.81474993487879E14d + "'",
        double14 == 2.81474993487879E14d);
  }

  @Test
  public void test1025() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1025");
    }
    org.ccsds.moims.mo.mal.structures.URI uRI1 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    java.lang.Integer int2 = uRI1.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.URI uRI4 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    org.ccsds.moims.mo.mal.structures.UOctet uOctet5 = uRI4.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.URI uRI7 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    org.ccsds.moims.mo.mal.structures.URI[] uRIArray8 = new org.ccsds.moims.mo.mal.structures.URI[]{
      uRI1, uRI4, uRI7};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList9 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
    boolean boolean10 = java.util.Collections
        .addAll((java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList9, uRIArray8);
    org.ccsds.moims.mo.mal.structures.FineTime fineTime11 =
        new org.ccsds.moims.mo.mal.structures.FineTime();
    int int12 = uRIList9.indexOf(fineTime11);
    uRIList9.ensureCapacity(40);
    java.lang.Object obj15 = uRIList9.clone();
    org.ccsds.moims.mo.mal.structures.URI uRI18 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    org.ccsds.moims.mo.mal.structures.UOctet uOctet19 = uRI18.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.URI uRI20 = uRIList9.set((byte) 1, uRI18);
    java.lang.String str21 = uRI20.getValue();
    org.ccsds.moims.mo.mal.structures.UShort uShort22 = uRI20.getServiceNumber();
    org.junit.Assert.assertTrue("'" + int2 + "' != '" + 18 + "'", int2.equals(18));
    org.junit.Assert.assertNotNull(uOctet5);
    org.junit.Assert.assertNotNull(uRIArray8);
    org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
    org.junit.Assert.assertTrue("'" + int12 + "' != '" + (-1) + "'", int12 == (-1));
    org.junit.Assert.assertNotNull(obj15);
    org.junit.Assert.assertNotNull(uOctet19);
    org.junit.Assert.assertNotNull(uRI20);
    org.junit.Assert.assertTrue("'" + str21 + "' != '" + "0100.0000" + "'",
        str21.equals("0100.0000"));
    org.junit.Assert.assertNotNull(uShort22);
  }

  @Test
  public void test1026() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1026");
    }
    org.ccsds.moims.mo.mal.structures.FineTime fineTime0 =
        new org.ccsds.moims.mo.mal.structures.FineTime();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet1 = fineTime0.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = fineTime0.getAreaVersion();
    long long3 = fineTime0.getValue();
    java.lang.String str4 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(fineTime0);
    org.ccsds.moims.mo.mal.structures.Element element5 = fineTime0.createElement();
    org.junit.Assert.assertNotNull(uOctet1);
    org.junit.Assert.assertNotNull(uOctet2);
    org.junit.Assert.assertTrue("'" + long3 + "' != '" + 0L + "'", long3 == 0L);
    org.junit.Assert.assertTrue("'" + str4 + "' != '" + "UnknownGUIData" + "'",
        str4.equals("UnknownGUIData"));
    org.junit.Assert.assertNotNull(element5);
  }

  @Test
  public void test1027() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1027");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet3 = octetList0.getAreaVersion();
    opssat.simulator.util.wav.WavFileException wavFileException5 =
        new opssat.simulator.util.wav.WavFileException(
            "UnknownGUIData");
    java.lang.Throwable[] throwableArray6 = wavFileException5.getSuppressed();
    boolean boolean7 = octetList0.equals(wavFileException5);
    java.lang.Integer[] intArray10 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList11 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean12 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList11, intArray10);
    int int14 = intList11.lastIndexOf((byte) 10);
    java.lang.Integer[] intArray22 = new java.lang.Integer[]{13, 10, 100, 100, 11111, 13, 11111};
    java.util.ArrayList<java.lang.Integer> intList23 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean24 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList23, intArray22);
    java.lang.Byte[] byteArray29 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList30 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean31 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList30, byteArray29);
    java.lang.Integer[] intArray34 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList35 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean36 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList35, intArray34);
    boolean boolean37 = byteList30.retainAll(intList35);
    java.lang.Integer[] intArray40 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList41 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean42 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList41, intArray40);
    int int44 = intList41.lastIndexOf((byte) 10);
    boolean boolean45 = intList35.removeAll(intList41);
    boolean boolean46 = intList23.retainAll(intList41);
    java.lang.Float[] floatArray50 = new java.lang.Float[]{1.0f, 1.0f, (-1.0f)};
    java.util.ArrayList<java.lang.Float> floatList51 = new java.util.ArrayList<java.lang.Float>();
    boolean boolean52 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Float>) floatList51, floatArray50);
    boolean boolean54 = floatList51.add((-1.0f));
    floatList51.trimToSize();
    java.lang.Integer[] intArray60 = new java.lang.Integer[]{100, 1, (-1), 10};
    java.util.ArrayList<java.lang.Integer> intList61 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean62 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList61, intArray60);
    int int64 = intList61.lastIndexOf((byte) 1);
    boolean boolean65 = floatList51.containsAll(intList61);
    boolean boolean66 = intList23.addAll(intList61);
    boolean boolean67 = intList11.removeAll(intList23);
    java.lang.Byte[] byteArray73 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList74 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean75 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList74, byteArray73);
    java.lang.Integer[] intArray78 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList79 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean80 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList79, intArray78);
    boolean boolean81 = byteList74.retainAll(intList79);
    boolean boolean82 = intList11.addAll(0, intList79);
    boolean boolean83 = octetList0.containsAll(intList79);
    org.ccsds.moims.mo.mal.structures.URI uRI85 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    java.lang.Integer int86 = uRI85.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.URI uRI88 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    org.ccsds.moims.mo.mal.structures.UOctet uOctet89 = uRI88.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.URI uRI91 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    org.ccsds.moims.mo.mal.structures.URI[] uRIArray92 =
        new org.ccsds.moims.mo.mal.structures.URI[]{
          uRI85, uRI88, uRI91};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList93 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
    boolean boolean94 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList93, uRIArray92);
    org.ccsds.moims.mo.mal.structures.FineTime fineTime95 =
        new org.ccsds.moims.mo.mal.structures.FineTime();
    int int96 = uRIList93.indexOf(fineTime95);
    uRIList93.ensureCapacity(40);
    boolean boolean99 = octetList0.contains(uRIList93);
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(uOctet3);
    org.junit.Assert.assertNotNull(throwableArray6);
    org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
    org.junit.Assert.assertNotNull(intArray10);
    org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + true + "'", boolean12);
    org.junit.Assert.assertTrue("'" + int14 + "' != '" + (-1) + "'", int14 == (-1));
    org.junit.Assert.assertNotNull(intArray22);
    org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + true + "'", boolean24);
    org.junit.Assert.assertNotNull(byteArray29);
    org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + true + "'", boolean31);
    org.junit.Assert.assertNotNull(intArray34);
    org.junit.Assert.assertTrue("'" + boolean36 + "' != '" + true + "'", boolean36);
    org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + true + "'", boolean37);
    org.junit.Assert.assertNotNull(intArray40);
    org.junit.Assert.assertTrue("'" + boolean42 + "' != '" + true + "'", boolean42);
    org.junit.Assert.assertTrue("'" + int44 + "' != '" + (-1) + "'", int44 == (-1));
    org.junit.Assert.assertTrue("'" + boolean45 + "' != '" + true + "'", boolean45);
    org.junit.Assert.assertTrue("'" + boolean46 + "' != '" + true + "'", boolean46);
    org.junit.Assert.assertNotNull(floatArray50);
    org.junit.Assert.assertTrue("'" + boolean52 + "' != '" + true + "'", boolean52);
    org.junit.Assert.assertTrue("'" + boolean54 + "' != '" + true + "'", boolean54);
    org.junit.Assert.assertNotNull(intArray60);
    org.junit.Assert.assertTrue("'" + boolean62 + "' != '" + true + "'", boolean62);
    org.junit.Assert.assertTrue("'" + int64 + "' != '" + (-1) + "'", int64 == (-1));
    org.junit.Assert.assertTrue("'" + boolean65 + "' != '" + false + "'", !boolean65);
    org.junit.Assert.assertTrue("'" + boolean66 + "' != '" + true + "'", boolean66);
    org.junit.Assert.assertTrue("'" + boolean67 + "' != '" + true + "'", boolean67);
    org.junit.Assert.assertNotNull(byteArray73);
    org.junit.Assert.assertTrue("'" + boolean75 + "' != '" + true + "'", boolean75);
    org.junit.Assert.assertNotNull(intArray78);
    org.junit.Assert.assertTrue("'" + boolean80 + "' != '" + true + "'", boolean80);
    org.junit.Assert.assertTrue("'" + boolean81 + "' != '" + true + "'", boolean81);
    org.junit.Assert.assertTrue("'" + boolean82 + "' != '" + true + "'", boolean82);
    org.junit.Assert.assertTrue("'" + boolean83 + "' != '" + false + "'", !boolean83);
    org.junit.Assert.assertTrue("'" + int86 + "' != '" + 18 + "'", int86.equals(18));
    org.junit.Assert.assertNotNull(uOctet89);
    org.junit.Assert.assertNotNull(uRIArray92);
    org.junit.Assert.assertTrue("'" + boolean94 + "' != '" + true + "'", boolean94);
    org.junit.Assert.assertTrue("'" + int96 + "' != '" + (-1) + "'", int96 == (-1));
    org.junit.Assert.assertTrue("'" + boolean99 + "' != '" + false + "'", !boolean99);
  }

  @Test
  public void test1028() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1028");
    }
    opssat.simulator.util.SimulatorData simulatorData3 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date4 = simulatorData3.getCurrentTime();
    java.util.Date date5 = simulatorData3.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(17,
        date5);
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    int int11 = opssat.simulator.util.DateExtraction.getDayFromDate(date10);
    opssat.simulator.util.SimulatorData simulatorData13 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date14 = simulatorData13.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData16 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date17 = simulatorData16.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap18 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date14, date17);
    opssat.simulator.util.SimulatorHeader simulatorHeader19 =
        new opssat.simulator.util.SimulatorHeader(
            false, date10, date17);
    opssat.simulator.util.SimulatorHeader simulatorHeader20 =
        new opssat.simulator.util.SimulatorHeader(
            false, date5, date10);
    boolean boolean21 = simulatorHeader20.isAutoStartTime();
    simulatorHeader20.setUseCelestia(false);
    simulatorHeader20.setCelestiaPort((short) 0);
    simulatorHeader20.setUseCelestia(true);
    int int28 = simulatorHeader20.getMonthStartDate();
    java.lang.String str29 = simulatorHeader20.getOrekitPropagator();
    org.junit.Assert.assertNotNull(date4);
    org.junit.Assert.assertNotNull(date5);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(date14);
    org.junit.Assert.assertNotNull(date17);
    org.junit.Assert.assertNotNull(timeUnitMap18);
    org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
    org.junit.Assert.assertNull(str29);
  }

  @Test
  public void test1029() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1029");
    }
    org.ccsds.moims.mo.mal.structures.StringList stringList0 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    org.ccsds.moims.mo.mal.structures.ShortList shortList2 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            ' ');
    java.util.ListIterator<java.lang.Short> shortItor3 = shortList2.listIterator();
    boolean boolean4 = stringList0.equals(shortList2);
    java.util.stream.BaseStream[] baseStreamArray6 = new java.util.stream.BaseStream[0];
    @SuppressWarnings("unchecked")
    java.util.stream.BaseStream<java.lang.Float, java.util.stream.Stream<java.lang.Float>>[] floatBaseStreamArray7 =
        baseStreamArray6;
    java.util.stream.BaseStream<java.lang.Float, java.util.stream.Stream<java.lang.Float>>[] floatBaseStreamArray8 =
        stringList0
            .toArray(
                (java.util.stream.BaseStream<java.lang.Float, java.util.stream.Stream<java.lang.Float>>[]) baseStreamArray6);
    java.util.stream.Stream<java.lang.String> strStream9 = stringList0.stream();
    int int10 = stringList0.size();
    org.ccsds.moims.mo.mal.structures.UShort uShort11 = stringList0.getServiceNumber();
    java.lang.Long[] longArray15 = new java.lang.Long[]{13L, 281475010265070L, 1L};
    java.util.ArrayList<java.lang.Long> longList16 = new java.util.ArrayList<java.lang.Long>();
    boolean boolean17 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Long>) longList16, longArray15);
    boolean boolean19 = longList16.add(281474993487885L);
    boolean boolean20 = longList16.isEmpty();
    opssat.simulator.util.SimulatorData simulatorData22 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date23 = simulatorData22.getCurrentTime();
    java.util.Date date24 = simulatorData22.getCurrentTime();
    int int25 = simulatorData22.getTimeFactor();
    int int26 = longList16.lastIndexOf(int25);
    boolean boolean27 = longList16.isEmpty();
    int int28 = stringList0.lastIndexOf(boolean27);
    java.lang.Boolean[] booleanArray31 = new java.lang.Boolean[]{true, true};
    java.util.ArrayList<java.lang.Boolean> booleanList32 =
        new java.util.ArrayList<java.lang.Boolean>();
    boolean boolean33 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Boolean>) booleanList32, booleanArray31);
    int int35 = booleanList32.indexOf(10);
    booleanList32.clear();
    opssat.simulator.util.DateExtraction dateExtraction37 =
        new opssat.simulator.util.DateExtraction();
    boolean boolean38 = booleanList32.equals(dateExtraction37);
    boolean boolean39 = booleanList32.isEmpty();
    java.util.stream.Stream<java.lang.Boolean> booleanStream40 = booleanList32.parallelStream();
    byte[] byteArray42 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS.int16_2ByteArray(56);
    org.ccsds.moims.mo.mal.structures.Blob blob45 = new org.ccsds.moims.mo.mal.structures.Blob(
        byteArray42, 20, 24);
    java.lang.Long long46 = blob45.getShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort47 = blob45.getServiceNumber();
    int int48 = booleanList32.indexOf(uShort47);
    java.util.stream.Stream<java.lang.Boolean> booleanStream49 = booleanList32.parallelStream();
    java.lang.Byte[] byteArray54 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList55 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean56 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList55, byteArray54);
    java.lang.Integer[] intArray59 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList60 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean61 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList60, intArray59);
    boolean boolean62 = byteList55.retainAll(intList60);
    java.lang.Integer[] intArray65 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList66 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean67 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList66, intArray65);
    int int69 = intList66.lastIndexOf((byte) 10);
    boolean boolean70 = intList60.retainAll(intList66);
    boolean boolean71 = booleanList32.retainAll(intList66);
    boolean boolean72 = stringList0.retainAll(intList66);
    org.junit.Assert.assertNotNull(shortItor3);
    org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
    org.junit.Assert.assertNotNull(baseStreamArray6);
    org.junit.Assert.assertNotNull(floatBaseStreamArray7);
    org.junit.Assert.assertNotNull(floatBaseStreamArray8);
    org.junit.Assert.assertNotNull(strStream9);
    org.junit.Assert.assertTrue("'" + int10 + "' != '" + 0 + "'", int10 == 0);
    org.junit.Assert.assertNotNull(uShort11);
    org.junit.Assert.assertNotNull(longArray15);
    org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + true + "'", boolean17);
    org.junit.Assert.assertTrue("'" + boolean19 + "' != '" + true + "'", boolean19);
    org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + false + "'", !boolean20);
    org.junit.Assert.assertNotNull(date23);
    org.junit.Assert.assertNotNull(date24);
    org.junit.Assert.assertTrue("'" + int25 + "' != '" + 1 + "'", int25 == 1);
    org.junit.Assert.assertTrue("'" + int26 + "' != '" + (-1) + "'", int26 == (-1));
    org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + false + "'", !boolean27);
    org.junit.Assert.assertTrue("'" + int28 + "' != '" + (-1) + "'", int28 == (-1));
    org.junit.Assert.assertNotNull(booleanArray31);
    org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + true + "'", boolean33);
    org.junit.Assert.assertTrue("'" + int35 + "' != '" + (-1) + "'", int35 == (-1));
    org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + false + "'", !boolean38);
    org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + true + "'", boolean39);
    org.junit.Assert.assertNotNull(booleanStream40);
    org.junit.Assert.assertNotNull(byteArray42);
    org.junit.Assert.assertTrue("'" + long46 + "' != '" + 281474993487873L + "'",
        long46.equals(281474993487873L));
    org.junit.Assert.assertNotNull(uShort47);
    org.junit.Assert.assertTrue("'" + int48 + "' != '" + (-1) + "'", int48 == (-1));
    org.junit.Assert.assertNotNull(booleanStream49);
    org.junit.Assert.assertNotNull(byteArray54);
    org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + true + "'", boolean56);
    org.junit.Assert.assertNotNull(intArray59);
    org.junit.Assert.assertTrue("'" + boolean61 + "' != '" + true + "'", boolean61);
    org.junit.Assert.assertTrue("'" + boolean62 + "' != '" + true + "'", boolean62);
    org.junit.Assert.assertNotNull(intArray65);
    org.junit.Assert.assertTrue("'" + boolean67 + "' != '" + true + "'", boolean67);
    org.junit.Assert.assertTrue("'" + int69 + "' != '" + (-1) + "'", int69 == (-1));
    org.junit.Assert.assertTrue("'" + boolean70 + "' != '" + true + "'", boolean70);
    org.junit.Assert.assertTrue("'" + boolean71 + "' != '" + false + "'", !boolean71);
    org.junit.Assert.assertTrue("'" + boolean72 + "' != '" + false + "'", !boolean72);
  }

  @Test
  public void test1030() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1030");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet3 = octetList0.getAreaVersion();
    opssat.simulator.util.wav.WavFileException wavFileException5 =
        new opssat.simulator.util.wav.WavFileException(
            "UnknownGUIData");
    java.lang.Throwable[] throwableArray6 = wavFileException5.getSuppressed();
    boolean boolean7 = octetList0.equals(wavFileException5);
    org.ccsds.moims.mo.mal.structures.FloatList floatList9 =
        new org.ccsds.moims.mo.mal.structures.FloatList(
            (byte) 0);
    java.lang.Integer int10 = floatList9.getTypeShortForm();
    java.lang.Integer int11 = floatList9.getTypeShortForm();
    floatList9.clear();
    boolean boolean13 = octetList0.remove(floatList9);
    java.lang.Long long14 = floatList9.getShortForm();
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(uOctet3);
    org.junit.Assert.assertNotNull(throwableArray6);
    org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
    org.junit.Assert.assertTrue("'" + int10 + "' != '" + (-4) + "'", int10.equals((-4)));
    org.junit.Assert.assertTrue("'" + int11 + "' != '" + (-4) + "'", int11.equals((-4)));
    org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + false + "'", !boolean13);
    org.junit.Assert.assertTrue("'" + long14 + "' != '" + 281475010265084L + "'",
        long14.equals(281475010265084L));
  }

  @Test
  public void test1031() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1031");
    }
    org.ccsds.moims.mo.mal.structures.UInteger uInteger1 =
        new org.ccsds.moims.mo.mal.structures.UInteger(
            13);
    org.ccsds.moims.mo.mal.structures.Element element2 = uInteger1.createElement();
    java.lang.Long long3 = uInteger1.getShortForm();
    long long4 = uInteger1.getValue();
    org.ccsds.moims.mo.mal.structures.Element element5 = uInteger1.createElement();
    org.ccsds.moims.mo.mal.structures.UShort uShort6 = uInteger1.getServiceNumber();
    java.lang.Long long7 = uInteger1.getShortForm();
    org.junit.Assert.assertNotNull(element2);
    org.junit.Assert.assertTrue("'" + long3 + "' != '" + 281474993487884L + "'",
        long3.equals(281474993487884L));
    org.junit.Assert.assertTrue("'" + long4 + "' != '" + 13L + "'", long4 == 13L);
    org.junit.Assert.assertNotNull(element5);
    org.junit.Assert.assertNotNull(uShort6);
    org.junit.Assert.assertTrue("'" + long7 + "' != '" + 281474993487884L + "'",
        long7.equals(281474993487884L));
  }

  @Test
  public void test1032() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1032");
    }
    org.ccsds.moims.mo.mal.structures.UShortList uShortList0 =
        new org.ccsds.moims.mo.mal.structures.UShortList();
    uShortList0.ensureCapacity(8);
    org.ccsds.moims.mo.mal.structures.UShort uShort3 = uShortList0.getServiceNumber();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState7 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray10 = new float[]{28, 8};
    simulatorSpacecraftState7.setQ(floatArray10);
    float[] floatArray12 = simulatorSpacecraftState7.getQ();
    double double13 = simulatorSpacecraftState7.getLongitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState17 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray20 = new float[]{28, 8};
    simulatorSpacecraftState17.setQ(floatArray20);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState25 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double26 = simulatorSpacecraftState25.getLatitude();
    java.lang.String str27 = simulatorSpacecraftState25.getMagField();
    float[] floatArray28 = simulatorSpacecraftState25.getR();
    simulatorSpacecraftState17.setQ(floatArray28);
    java.lang.String str30 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray28);
    simulatorSpacecraftState7.setRv(floatArray28);
    double double32 = simulatorSpacecraftState7.getLatitude();
    java.lang.String str33 = simulatorSpacecraftState7.toString();
    boolean boolean34 = uShortList0.equals(simulatorSpacecraftState7);
    boolean boolean35 = uShortList0.isEmpty();
    org.junit.Assert.assertNotNull(uShort3);
    org.junit.Assert.assertNotNull(floatArray10);
    org.junit.Assert.assertNotNull(floatArray12);
    org.junit.Assert.assertTrue("'" + double13 + "' != '" + (-1.0d) + "'", double13 == (-1.0d));
    org.junit.Assert.assertNotNull(floatArray20);
    org.junit.Assert.assertTrue("'" + double26 + "' != '" + 340.0d + "'", double26 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str27 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str27.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray28);
    org.junit.Assert.assertTrue("'" + str30 + "' != '" + "UnknownGUIData" + "'",
        str30.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double32 + "' != '" + 340.0d + "'", double32 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str33 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str33.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + false + "'", !boolean34);
    org.junit.Assert.assertTrue("'" + boolean35 + "' != '" + true + "'", boolean35);
  }

  @Test
  public void test1033() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1033");
    }
    org.ccsds.moims.mo.mal.structures.URI uRI1 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    java.lang.Integer int2 = uRI1.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.URI uRI4 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    org.ccsds.moims.mo.mal.structures.UOctet uOctet5 = uRI4.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.URI uRI7 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    org.ccsds.moims.mo.mal.structures.URI[] uRIArray8 = new org.ccsds.moims.mo.mal.structures.URI[]{
      uRI1, uRI4, uRI7};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList9 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
    boolean boolean10 = java.util.Collections
        .addAll((java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList9, uRIArray8);
    org.ccsds.moims.mo.mal.structures.FineTime fineTime11 =
        new org.ccsds.moims.mo.mal.structures.FineTime();
    int int12 = uRIList9.indexOf(fineTime11);
    uRIList9.trimToSize();
    boolean boolean14 = uRIList9.isEmpty();
    int int15 = uRIList9.size();
    opssat.simulator.util.SimulatorData simulatorData19 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date20 = simulatorData19.getCurrentTime();
    java.util.Date date21 = simulatorData19.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData22 = new opssat.simulator.util.SimulatorData(
        17, date21);
    opssat.simulator.util.SimulatorData simulatorData25 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date26 = simulatorData25.getCurrentTime();
    int int27 = opssat.simulator.util.DateExtraction.getDayFromDate(date26);
    opssat.simulator.util.SimulatorData simulatorData29 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date30 = simulatorData29.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData32 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date33 = simulatorData32.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap34 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date30, date33);
    opssat.simulator.util.SimulatorHeader simulatorHeader35 =
        new opssat.simulator.util.SimulatorHeader(
            false, date26, date33);
    opssat.simulator.util.SimulatorHeader simulatorHeader36 =
        new opssat.simulator.util.SimulatorHeader(
            false, date21, date26);
    opssat.simulator.util.SimulatorData simulatorData40 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date41 = simulatorData40.getCurrentTime();
    java.util.Date date42 = simulatorData40.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData43 = new opssat.simulator.util.SimulatorData(
        17, date42);
    opssat.simulator.util.SimulatorData simulatorData46 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date47 = simulatorData46.getCurrentTime();
    int int48 = opssat.simulator.util.DateExtraction.getDayFromDate(date47);
    opssat.simulator.util.SimulatorData simulatorData50 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date51 = simulatorData50.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData53 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date54 = simulatorData53.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap55 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date51, date54);
    opssat.simulator.util.SimulatorHeader simulatorHeader56 =
        new opssat.simulator.util.SimulatorHeader(
            false, date47, date54);
    opssat.simulator.util.SimulatorHeader simulatorHeader57 =
        new opssat.simulator.util.SimulatorHeader(
            false, date42, date47);
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap58 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date26, date42);
    int int59 = opssat.simulator.util.DateExtraction.getSecondsFromDate(date26);
    boolean boolean60 = uRIList9.equals(int59);
    org.junit.Assert.assertTrue("'" + int2 + "' != '" + 18 + "'", int2.equals(18));
    org.junit.Assert.assertNotNull(uOctet5);
    org.junit.Assert.assertNotNull(uRIArray8);
    org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
    org.junit.Assert.assertTrue("'" + int12 + "' != '" + (-1) + "'", int12 == (-1));
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
    org.junit.Assert.assertTrue("'" + int15 + "' != '" + 3 + "'", int15 == 3);
    org.junit.Assert.assertNotNull(date20);
    org.junit.Assert.assertNotNull(date21);
    org.junit.Assert.assertNotNull(date26);
    org.junit.Assert.assertNotNull(date30);
    org.junit.Assert.assertNotNull(date33);
    org.junit.Assert.assertNotNull(timeUnitMap34);
    org.junit.Assert.assertNotNull(date41);
    org.junit.Assert.assertNotNull(date42);
    org.junit.Assert.assertNotNull(date47);
    org.junit.Assert.assertNotNull(date51);
    org.junit.Assert.assertNotNull(date54);
    org.junit.Assert.assertNotNull(timeUnitMap55);
    org.junit.Assert.assertNotNull(timeUnitMap58);
    org.junit.Assert.assertTrue("'" + boolean60 + "' != '" + false + "'", !boolean60);
  }

  @Test
  public void test1034() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1034");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessSingleStreamOperatingBuffer endlessSingleStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessSingleStreamOperatingBuffer(
            logger0);
    byte[] byteArray3 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS.float2ByteArray(64);
    endlessSingleStreamOperatingBuffer1.setDataFromByteArray(byteArray3);
    boolean boolean6 = endlessSingleStreamOperatingBuffer1.preparePath("5600.0000");
    org.junit.Assert.assertNotNull(byteArray3);
    org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + false + "'", !boolean6);
  }

  @Test
  public void test1035() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1035");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    boolean boolean14 = simulatorHeader12.isAutoStartSystem();
    int int15 = simulatorHeader12.getDayStartDate();
    java.lang.String str16 = simulatorHeader12.getOrekitTLE1();
    simulatorHeader12.setTimeFactor(31);
    simulatorHeader12.setUpdateInternet(false);
    java.lang.String str21 = simulatorHeader12.getOrekitTLE2();
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
    org.junit.Assert.assertNull(str16);
    org.junit.Assert.assertNull(str21);
  }

  @Test
  public void test1036() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1036");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double4 = simulatorSpacecraftState3.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState8 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray11 = new float[]{28, 8};
    simulatorSpacecraftState8.setQ(floatArray11);
    simulatorSpacecraftState3.setRv(floatArray11);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState17 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray20 = new float[]{28, 8};
    simulatorSpacecraftState17.setQ(floatArray20);
    float[] floatArray22 = simulatorSpacecraftState17.getR();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState26 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState26.setLongitude(4);
    double[] doubleArray30 = new double[]{(-1.0f)};
    simulatorSpacecraftState26.setMagField(doubleArray30);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState35 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double36 = simulatorSpacecraftState35.getLatitude();
    java.lang.String str37 = simulatorSpacecraftState35.getMagField();
    java.lang.String str38 = simulatorSpacecraftState35.toString();
    double[] doubleArray39 = simulatorSpacecraftState35.getSunVector();
    simulatorSpacecraftState26.setMagnetometer(doubleArray39);
    simulatorSpacecraftState17.setSunVector(doubleArray39);
    float[] floatArray42 = simulatorSpacecraftState17.getQ();
    org.ccsds.moims.mo.mal.structures.Identifier identifier43 =
        new org.ccsds.moims.mo.mal.structures.Identifier();
    org.ccsds.moims.mo.mal.structures.OctetList octetList44 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int45 = octetList44.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort46 = octetList44.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor47 = octetList44.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor49 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList44, "hi!");
    argumentDescriptor49.restoreArgument();
    argumentDescriptor49.restoreArgument();
    argumentDescriptor49.restoreArgument();
    java.lang.String str53 = argumentDescriptor49.toString();
    boolean boolean54 = identifier43.equals(str53);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState58 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray61 = new float[]{28, 8};
    simulatorSpacecraftState58.setQ(floatArray61);
    float[] floatArray63 = simulatorSpacecraftState58.getQ();
    double double64 = simulatorSpacecraftState58.getLongitude();
    boolean boolean65 = identifier43.equals(simulatorSpacecraftState58);
    float[] floatArray66 = simulatorSpacecraftState58.getQ();
    opssat.simulator.celestia.CelestiaData celestiaData67 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray42, floatArray66);
    opssat.simulator.celestia.CelestiaData celestiaData68 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray11, floatArray66);
    org.junit.Assert.assertTrue("'" + double4 + "' != '" + 340.0d + "'", double4 == 340.0d);
    org.junit.Assert.assertNotNull(floatArray11);
    org.junit.Assert.assertNotNull(floatArray20);
    org.junit.Assert.assertNotNull(floatArray22);
    org.junit.Assert.assertNotNull(doubleArray30);
    org.junit.Assert.assertTrue("'" + double36 + "' != '" + 340.0d + "'", double36 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str37 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str37.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str38 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str38.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray39);
    org.junit.Assert.assertNotNull(floatArray42);
    org.junit.Assert.assertTrue("'" + int45 + "' != '" + (-7) + "'", int45.equals((-7)));
    org.junit.Assert.assertNotNull(uShort46);
    org.junit.Assert.assertNotNull(byteItor47);
    org.junit.Assert.assertTrue("'" + str53 + "' != '" + "" + "'", str53.equals(""));
    org.junit.Assert.assertTrue("'" + boolean54 + "' != '" + false + "'", !boolean54);
    org.junit.Assert.assertNotNull(floatArray61);
    org.junit.Assert.assertNotNull(floatArray63);
    org.junit.Assert.assertTrue("'" + double64 + "' != '" + (-1.0d) + "'", double64 == (-1.0d));
    org.junit.Assert.assertTrue("'" + boolean65 + "' != '" + false + "'", !boolean65);
    org.junit.Assert.assertNotNull(floatArray66);
  }

  @Test
  public void test1037() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1037");
    }
    opssat.simulator.util.wav.WavFileException wavFileException3 =
        new opssat.simulator.util.wav.WavFileException(
            "opssat.simulator.util.wav.WavFileException: 00000:00:00:00:008");
    opssat.simulator.util.wav.WavFileException wavFileException4 =
        new opssat.simulator.util.wav.WavFileException(
            "2019:05:23 15:09:34 UTC", wavFileException3);
    opssat.simulator.util.wav.WavFileException wavFileException6 =
        new opssat.simulator.util.wav.WavFileException(
            "[0, 1]");
    java.lang.Throwable[] throwableArray7 = wavFileException6.getSuppressed();
    org.ccsds.moims.mo.mal.structures.OctetList octetList8 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int9 = octetList8.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort10 = octetList8.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet11 = octetList8.getAreaVersion();
    opssat.simulator.util.wav.WavFileException wavFileException13 =
        new opssat.simulator.util.wav.WavFileException(
            "UnknownGUIData");
    java.lang.Throwable[] throwableArray14 = wavFileException13.getSuppressed();
    boolean boolean15 = octetList8.equals(wavFileException13);
    opssat.simulator.util.wav.WavFileException wavFileException17 =
        new opssat.simulator.util.wav.WavFileException(
            "00000:00:00:00:008");
    wavFileException13.addSuppressed(wavFileException17);
    java.lang.String str19 = wavFileException17.toString();
    wavFileException6.addSuppressed(wavFileException17);
    java.lang.String str21 = wavFileException17.toString();
    wavFileException3.addSuppressed(wavFileException17);
    opssat.simulator.util.wav.WavFileException wavFileException23 =
        new opssat.simulator.util.wav.WavFileException(
            "SimulatorHeader{autoStartSystem=true, autoStartTime=false, timeFactor=1, startDate=Thu May 23 15:09:48 UTC 2019, endDate=Thu May 23 15:09:48 UTC 2019}",
            wavFileException17);
    org.junit.Assert.assertNotNull(throwableArray7);
    org.junit.Assert.assertTrue("'" + int9 + "' != '" + (-7) + "'", int9.equals((-7)));
    org.junit.Assert.assertNotNull(uShort10);
    org.junit.Assert.assertNotNull(uOctet11);
    org.junit.Assert.assertNotNull(throwableArray14);
    org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
    org.junit.Assert
        .assertTrue(
            "'" + str19 + "' != '"
            + "opssat.simulator.util.wav.WavFileException: 00000:00:00:00:008" + "'",
            str19.equals("opssat.simulator.util.wav.WavFileException: 00000:00:00:00:008"));
    org.junit.Assert
        .assertTrue(
            "'" + str21 + "' != '"
            + "opssat.simulator.util.wav.WavFileException: 00000:00:00:00:008" + "'",
            str21.equals("opssat.simulator.util.wav.WavFileException: 00000:00:00:00:008"));
  }

  @Test
  public void test1038() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1038");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double4 = simulatorSpacecraftState3.getLatitude();
    double double5 = simulatorSpacecraftState3.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState9 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState9.setLongitude(4);
    double[] doubleArray13 = new double[]{(-1.0f)};
    simulatorSpacecraftState9.setMagField(doubleArray13);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState18 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double19 = simulatorSpacecraftState18.getLatitude();
    java.lang.String str20 = simulatorSpacecraftState18.getMagField();
    java.lang.String str21 = simulatorSpacecraftState18.toString();
    double[] doubleArray22 = simulatorSpacecraftState18.getSunVector();
    simulatorSpacecraftState9.setMagnetometer(doubleArray22);
    simulatorSpacecraftState9.setModeOperation("3100.0000000");
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState29 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double30 = simulatorSpacecraftState29.getLatitude();
    double[] doubleArray31 = simulatorSpacecraftState29.getSunVector();
    simulatorSpacecraftState9.setMagField(doubleArray31);
    simulatorSpacecraftState3.setSunVector(doubleArray31);
    org.junit.Assert.assertTrue("'" + double4 + "' != '" + 340.0d + "'", double4 == 340.0d);
    org.junit.Assert.assertTrue("'" + double5 + "' != '" + 340.0d + "'", double5 == 340.0d);
    org.junit.Assert.assertNotNull(doubleArray13);
    org.junit.Assert.assertTrue("'" + double19 + "' != '" + 340.0d + "'", double19 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str20 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str20.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str21 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str21.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray22);
    org.junit.Assert.assertTrue("'" + double30 + "' != '" + 340.0d + "'", double30 == 340.0d);
    org.junit.Assert.assertNotNull(doubleArray31);
  }

  @Test
  public void test1039() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1039");
    }
    org.ccsds.moims.mo.mal.structures.UShortList uShortList0 =
        new org.ccsds.moims.mo.mal.structures.UShortList();
    org.ccsds.moims.mo.mal.structures.UShort uShort1 = uShortList0.getServiceNumber();
    boolean boolean2 = uShortList0.isEmpty();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet3 = uShortList0.getAreaVersion();
    java.lang.Float[] floatArray7 = new java.lang.Float[]{1.0f, 1.0f, (-1.0f)};
    java.util.ArrayList<java.lang.Float> floatList8 = new java.util.ArrayList<java.lang.Float>();
    boolean boolean9 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Float>) floatList8, floatArray7);
    boolean boolean11 = floatList8.add((-1.0f));
    floatList8.clear();
    java.util.stream.Stream<java.lang.Float> floatStream13 = floatList8.stream();
    org.ccsds.moims.mo.mal.structures.StringList stringList14 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    opssat.simulator.threading.SimulatorNode.DevDatPBind devDatPBind15 =
        opssat.simulator.threading.SimulatorNode.DevDatPBind.FineADCS_ReactionWheels;
    boolean boolean16 = stringList14.equals(devDatPBind15);
    org.ccsds.moims.mo.mal.structures.Element element17 = stringList14.createElement();
    java.util.Iterator<java.lang.String> strItor18 = stringList14.iterator();
    boolean boolean19 = floatList8.contains(stringList14);
    java.util.stream.Stream<java.lang.String> strStream20 = stringList14.stream();
    int int21 = uShortList0.lastIndexOf(strStream20);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState25 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double26 = simulatorSpacecraftState25.getLatitude();
    java.lang.String str27 = simulatorSpacecraftState25.getMagField();
    java.lang.String str28 = simulatorSpacecraftState25.getSunVectorAsString();
    boolean boolean29 = uShortList0.equals(simulatorSpacecraftState25);
    org.junit.Assert.assertNotNull(uShort1);
    org.junit.Assert.assertTrue("'" + boolean2 + "' != '" + true + "'", boolean2);
    org.junit.Assert.assertNotNull(uOctet3);
    org.junit.Assert.assertNotNull(floatArray7);
    org.junit.Assert.assertTrue("'" + boolean9 + "' != '" + true + "'", boolean9);
    org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
    org.junit.Assert.assertNotNull(floatStream13);
    org.junit.Assert.assertTrue(
        "'" + devDatPBind15 + "' != '"
        + opssat.simulator.threading.SimulatorNode.DevDatPBind.FineADCS_ReactionWheels + "'",
        devDatPBind15
            .equals(opssat.simulator.threading.SimulatorNode.DevDatPBind.FineADCS_ReactionWheels));
    org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
    org.junit.Assert.assertNotNull(element17);
    org.junit.Assert.assertNotNull(strItor18);
    org.junit.Assert.assertTrue("'" + boolean19 + "' != '" + false + "'", !boolean19);
    org.junit.Assert.assertNotNull(strStream20);
    org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
    org.junit.Assert.assertTrue("'" + double26 + "' != '" + 340.0d + "'", double26 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str27 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str27.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str28 + "' != '" + "X:[+.00000] ; Y:[+.00000] ; Z: [+.00000] units [N/A]" + "'",
        str28.equals("X:[+.00000] ; Y:[+.00000] ; Z: [+.00000] units [N/A]"));
    org.junit.Assert.assertTrue("'" + boolean29 + "' != '" + false + "'", !boolean29);
  }

  @Test
  public void test1040() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1040");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState3.setLongitude(4);
    double double6 = simulatorSpacecraftState3.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState10 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState10.setLongitude(4);
    double[] doubleArray14 = new double[]{(-1.0f)};
    simulatorSpacecraftState10.setMagField(doubleArray14);
    double[] doubleArray16 = simulatorSpacecraftState10.getSunVector();
    simulatorSpacecraftState3.setSunVector(doubleArray16);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState21 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double22 = simulatorSpacecraftState21.getLatitude();
    double double23 = simulatorSpacecraftState21.getLongitude();
    java.lang.String str24 = simulatorSpacecraftState21.getRotationAsString();
    double[] doubleArray25 = simulatorSpacecraftState21.getSunVector();
    simulatorSpacecraftState3.setMagnetometer(doubleArray25);
    org.junit.Assert.assertTrue("'" + double6 + "' != '" + 340.0d + "'", double6 == 340.0d);
    org.junit.Assert.assertNotNull(doubleArray14);
    org.junit.Assert.assertNotNull(doubleArray16);
    org.junit.Assert.assertTrue("'" + double22 + "' != '" + 340.0d + "'", double22 == 340.0d);
    org.junit.Assert.assertTrue("'" + double23 + "' != '" + (-1.0d) + "'", double23 == (-1.0d));
    org.junit.Assert.assertTrue("'" + str24 + "' != '"
        + "\n+0.000000 +0.000000 +0.000000\n+0.000000 +0.000000 +0.000000\n+0.000000 +0.000000 +0.000000"
        + "'",
        str24.equals(
            "\n+0.000000 +0.000000 +0.000000\n+0.000000 +0.000000 +0.000000\n+0.000000 +0.000000 +0.000000"));
    org.junit.Assert.assertNotNull(doubleArray25);
  }

  @Test
  public void test1041() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1041");
    }
    opssat.simulator.util.wav.WavFileException wavFileException2 =
        new opssat.simulator.util.wav.WavFileException(
            "UnknownGUIData");
    java.lang.Throwable[] throwableArray3 = wavFileException2.getSuppressed();
    java.lang.String str4 = wavFileException2.toString();
    opssat.simulator.util.wav.WavFileException wavFileException5 =
        new opssat.simulator.util.wav.WavFileException(
            "[true]", wavFileException2);
    opssat.simulator.util.wav.WavFileException wavFileException6 =
        new opssat.simulator.util.wav.WavFileException(
            wavFileException2);
    opssat.simulator.util.wav.WavFileException wavFileException7 =
        new opssat.simulator.util.wav.WavFileException(
            wavFileException2);
    org.junit.Assert.assertNotNull(throwableArray3);
    org.junit.Assert.assertTrue(
        "'" + str4 + "' != '" + "opssat.simulator.util.wav.WavFileException: UnknownGUIData" + "'",
        str4.equals("opssat.simulator.util.wav.WavFileException: UnknownGUIData"));
  }

  @Test
  public void test1042() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1042");
    }
    org.ccsds.moims.mo.mal.structures.LongList longList0 =
        new org.ccsds.moims.mo.mal.structures.LongList();
    java.lang.Long long1 = longList0.getShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = longList0.getServiceNumber();
    java.lang.String str3 = longList0.toString();
    org.ccsds.moims.mo.mal.structures.UShort uShort4 = longList0.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet5 =
        org.ccsds.moims.mo.mal.structures.DoubleList.AREA_VERSION;
    java.lang.String str6 = uOctet5.toString();
    org.ccsds.moims.mo.mal.structures.Element element7 = uOctet5.createElement();
    int int8 = longList0.lastIndexOf(uOctet5);
    opssat.simulator.util.SimulatorData simulatorData11 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date12 = simulatorData11.getCurrentTime();
    int int13 = opssat.simulator.util.DateExtraction.getDayFromDate(date12);
    opssat.simulator.util.SimulatorData simulatorData15 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date16 = simulatorData15.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData18 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date19 = simulatorData18.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap20 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date16, date19);
    opssat.simulator.util.SimulatorHeader simulatorHeader21 =
        new opssat.simulator.util.SimulatorHeader(
            false, date12, date19);
    java.util.Date date22 = simulatorHeader21.getEndDate();
    simulatorHeader21.setOrekitTLE1("");
    java.lang.String str25 = simulatorHeader21.DATE_FORMAT;
    boolean boolean26 = longList0.remove(str25);
    org.ccsds.moims.mo.mal.structures.IntegerList integerList28 =
        new org.ccsds.moims.mo.mal.structures.IntegerList(
            48);
    integerList28.ensureCapacity(17);
    boolean boolean31 = longList0.removeAll(integerList28);
    org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265075L + "'",
        long1.equals(281475010265075L));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertTrue("'" + str3 + "' != '" + "[]" + "'", str3.equals("[]"));
    org.junit.Assert.assertNotNull(uShort4);
    org.junit.Assert.assertNotNull(uOctet5);
    org.junit.Assert.assertTrue("'" + str6 + "' != '" + "1" + "'", str6.equals("1"));
    org.junit.Assert.assertNotNull(element7);
    org.junit.Assert.assertTrue("'" + int8 + "' != '" + (-1) + "'", int8 == (-1));
    org.junit.Assert.assertNotNull(date12);
    org.junit.Assert.assertNotNull(date16);
    org.junit.Assert.assertNotNull(date19);
    org.junit.Assert.assertNotNull(timeUnitMap20);
    org.junit.Assert.assertNotNull(date22);
    org.junit.Assert.assertTrue("'" + str25 + "' != '" + "yyyy:MM:dd HH:mm:ss z" + "'",
        str25.equals("yyyy:MM:dd HH:mm:ss z"));
    org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + false + "'", !boolean26);
    org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + false + "'", !boolean31);
  }

  @Test
  public void test1043() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1043");
    }
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor2 =
        new opssat.simulator.util.ArgumentDescriptor(
            "00000:00:00:00:-02", "2019:05:23 15:09:36 UTC");
    java.lang.Float[] floatArray6 = new java.lang.Float[]{1.0f, 1.0f, (-1.0f)};
    java.util.ArrayList<java.lang.Float> floatList7 = new java.util.ArrayList<java.lang.Float>();
    boolean boolean8 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Float>) floatList7, floatArray6);
    boolean boolean10 = floatList7.add((-1.0f));
    floatList7.clear();
    java.util.stream.Stream<java.lang.Float> floatStream12 = floatList7.stream();
    java.lang.Byte[] byteArray17 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList18 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean19 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList18, byteArray17);
    java.lang.Integer[] intArray22 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList23 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean24 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList23, intArray22);
    boolean boolean25 = byteList18.retainAll(intList23);
    java.lang.Integer[] intArray28 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList29 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean30 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList29, intArray28);
    int int32 = intList29.lastIndexOf((byte) 10);
    boolean boolean33 = intList23.retainAll(intList29);
    boolean boolean34 = floatList7.containsAll(intList29);
    argumentDescriptor2.setType(floatList7);
    byte[] byteArray37 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS.int2ByteArray((-30));
    argumentDescriptor2.setType((-30));
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + true + "'", boolean8);
    org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
    org.junit.Assert.assertNotNull(floatStream12);
    org.junit.Assert.assertNotNull(byteArray17);
    org.junit.Assert.assertTrue("'" + boolean19 + "' != '" + true + "'", boolean19);
    org.junit.Assert.assertNotNull(intArray22);
    org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + true + "'", boolean24);
    org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + true + "'", boolean25);
    org.junit.Assert.assertNotNull(intArray28);
    org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + true + "'", boolean30);
    org.junit.Assert.assertTrue("'" + int32 + "' != '" + (-1) + "'", int32 == (-1));
    org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + true + "'", boolean33);
    org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + false + "'", !boolean34);
    org.junit.Assert.assertNotNull(byteArray37);
  }

  @Test
  public void test1044() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1044");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor3 = octetList0.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor5 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList0, "hi!");
    argumentDescriptor5.restoreArgument();
    argumentDescriptor5.restoreArgument();
    argumentDescriptor5.restoreArgument();
    java.lang.String str9 = argumentDescriptor5.toString();
    argumentDescriptor5.setName("OPS-SAT SoftSim:");
    java.lang.String str12 = argumentDescriptor5.toString();
    java.lang.Object obj13 = null;
    argumentDescriptor5.setType(obj13);
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(byteItor3);
    org.junit.Assert.assertTrue("'" + str9 + "' != '" + "" + "'", str9.equals(""));
    org.junit.Assert.assertTrue("'" + str12 + "' != '" + "" + "'", str12.equals(""));
  }

  @Test
  public void test1045() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1045");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessWavStreamOperatingBuffer endlessWavStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessWavStreamOperatingBuffer(
            logger0);
    opssat.simulator.util.SimulatorData simulatorData4 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date5 = simulatorData4.getCurrentTime();
    int int6 = opssat.simulator.util.DateExtraction.getDayFromDate(date5);
    opssat.simulator.util.SimulatorData simulatorData8 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date9 = simulatorData8.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData11 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date12 = simulatorData11.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap13 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date9, date12);
    opssat.simulator.util.SimulatorHeader simulatorHeader14 =
        new opssat.simulator.util.SimulatorHeader(
            false, date5, date12);
    java.util.Date date15 = simulatorHeader14.getEndDate();
    int int16 = simulatorHeader14.getMinuteStartDate();
    simulatorHeader14.setUseOrekitPropagator(true);
    java.lang.String str19 = simulatorHeader14.toFileString();
    java.util.Date date21 = simulatorHeader14.parseStringIntoDate("yyyy:MM:dd HH:mm:ss z");
    endlessWavStreamOperatingBuffer1.setDataBuffer("yyyy:MM:dd HH:mm:ss z");
    java.lang.String str23 = endlessWavStreamOperatingBuffer1.getDataBufferAsString();
    java.lang.String str24 = endlessWavStreamOperatingBuffer1.getDataBufferAsString();
    org.junit.Assert.assertNotNull(date5);
    org.junit.Assert.assertNotNull(date9);
    org.junit.Assert.assertNotNull(date12);
    org.junit.Assert.assertNotNull(timeUnitMap13);
    org.junit.Assert.assertNotNull(date15);
    org.junit.Assert.assertNull(date21);
    org.junit.Assert.assertTrue(
        "'" + str23 + "' != '" + "Unknown data type [java.lang.String]" + "'",
        str23.equals("Unknown data type [java.lang.String]"));
    org.junit.Assert.assertTrue(
        "'" + str24 + "' != '" + "Unknown data type [java.lang.String]" + "'",
        str24.equals("Unknown data type [java.lang.String]"));
  }

  @Test
  public void test1046() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1046");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessSingleStreamOperatingBuffer endlessSingleStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessSingleStreamOperatingBuffer(
            logger0);
    byte[] byteArray3 = endlessSingleStreamOperatingBuffer1.getDataAsByteArray('4');
    int int4 = endlessSingleStreamOperatingBuffer1.getOperatingIndex();
    try {
      boolean boolean6 = endlessSingleStreamOperatingBuffer1.loadFromPath("[1, 11111, 1, 11111]");
      org.junit.Assert
          .fail("Expected exception of type java.lang.NullPointerException; message: null");
    } catch (java.lang.NullPointerException e) {
    }
    org.junit.Assert.assertNotNull(byteArray3);
    org.junit.Assert.assertTrue("'" + int4 + "' != '" + 0 + "'", int4 == 0);
  }

  @Test
  public void test1047() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1047");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState11 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double12 = simulatorSpacecraftState11.getLatitude();
    java.lang.String str13 = simulatorSpacecraftState11.getMagField();
    float[] floatArray14 = simulatorSpacecraftState11.getR();
    simulatorSpacecraftState3.setQ(floatArray14);
    java.lang.String str16 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray14);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState20 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double21 = simulatorSpacecraftState20.getLatitude();
    double double22 = simulatorSpacecraftState20.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState26 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double27 = simulatorSpacecraftState26.getLatitude();
    java.lang.String str28 = simulatorSpacecraftState26.getMagField();
    float[] floatArray29 = simulatorSpacecraftState26.getR();
    simulatorSpacecraftState20.setQ(floatArray29);
    float[] floatArray31 = simulatorSpacecraftState20.getV();
    opssat.simulator.celestia.CelestiaData celestiaData32 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray14, floatArray31);
    java.lang.String str33 = celestiaData32.getInfo();
    celestiaData32.setAnx("2019:05:23 15:10:01 UTC");
    celestiaData32.setDate("Unknown data type [java.lang.Integer]");
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + 340.0d + "'", double12 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str13 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str13.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray14);
    org.junit.Assert.assertTrue("'" + str16 + "' != '" + "UnknownGUIData" + "'",
        str16.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double21 + "' != '" + 340.0d + "'", double21 == 340.0d);
    org.junit.Assert.assertTrue("'" + double22 + "' != '" + 340.0d + "'", double22 == 340.0d);
    org.junit.Assert.assertTrue("'" + double27 + "' != '" + 340.0d + "'", double27 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str28 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str28.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray29);
    org.junit.Assert.assertNotNull(floatArray31);
    org.junit.Assert.assertNull(str33);
  }

  @Test
  public void test1048() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1048");
    }
    org.ccsds.moims.mo.mal.structures.FineTime fineTime0 =
        new org.ccsds.moims.mo.mal.structures.FineTime();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet1 = fineTime0.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = fineTime0.getAreaVersion();
    long long3 = fineTime0.getValue();
    java.lang.String str4 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(fineTime0);
    org.ccsds.moims.mo.mal.structures.UShort uShort5 = fineTime0.getServiceNumber();
    java.lang.Integer int6 = fineTime0.getTypeShortForm();
    org.junit.Assert.assertNotNull(uOctet1);
    org.junit.Assert.assertNotNull(uOctet2);
    org.junit.Assert.assertTrue("'" + long3 + "' != '" + 0L + "'", long3 == 0L);
    org.junit.Assert.assertTrue("'" + str4 + "' != '" + "UnknownGUIData" + "'",
        str4.equals("UnknownGUIData"));
    org.junit.Assert.assertNotNull(uShort5);
    org.junit.Assert.assertTrue("'" + int6 + "' != '" + 17 + "'", int6.equals(17));
  }

  @Test
  public void test1049() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1049");
    }
    org.ccsds.moims.mo.mal.structures.LongList longList0 =
        new org.ccsds.moims.mo.mal.structures.LongList();
    java.util.ListIterator<java.lang.Long> longItor1 = longList0.listIterator();
    java.util.Spliterator<java.lang.Long> longSpliterator2 = longList0.spliterator();
    java.util.stream.Stream<java.lang.Long> longStream3 = longList0.stream();
    org.junit.Assert.assertNotNull(longItor1);
    org.junit.Assert.assertNotNull(longSpliterator2);
    org.junit.Assert.assertNotNull(longStream3);
  }

  @Test
  public void test1051() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1051");
    }
    org.ccsds.moims.mo.mal.structures.StringList stringList0 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    opssat.simulator.threading.SimulatorNode.DevDatPBind devDatPBind1 =
        opssat.simulator.threading.SimulatorNode.DevDatPBind.FineADCS_ReactionWheels;
    boolean boolean2 = stringList0.equals(devDatPBind1);
    org.ccsds.moims.mo.mal.structures.Element element3 = stringList0.createElement();
    stringList0.ensureCapacity(28);
    java.util.Spliterator<java.lang.String> strSpliterator6 = stringList0.spliterator();
    stringList0.clear();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor9 =
        new opssat.simulator.util.ArgumentDescriptor(
            stringList0, "3800.0000000");
    org.junit.Assert.assertTrue(
        "'" + devDatPBind1 + "' != '"
        + opssat.simulator.threading.SimulatorNode.DevDatPBind.FineADCS_ReactionWheels + "'",
        devDatPBind1
            .equals(opssat.simulator.threading.SimulatorNode.DevDatPBind.FineADCS_ReactionWheels));
    org.junit.Assert.assertTrue("'" + boolean2 + "' != '" + false + "'", !boolean2);
    org.junit.Assert.assertNotNull(element3);
    org.junit.Assert.assertNotNull(strSpliterator6);
  }

  @Test
  public void test1052() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1052");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState11 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double12 = simulatorSpacecraftState11.getLatitude();
    java.lang.String str13 = simulatorSpacecraftState11.getMagField();
    float[] floatArray14 = simulatorSpacecraftState11.getR();
    simulatorSpacecraftState3.setQ(floatArray14);
    java.lang.String str16 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray14);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState20 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double21 = simulatorSpacecraftState20.getLatitude();
    double double22 = simulatorSpacecraftState20.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState26 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double27 = simulatorSpacecraftState26.getLatitude();
    java.lang.String str28 = simulatorSpacecraftState26.getMagField();
    float[] floatArray29 = simulatorSpacecraftState26.getR();
    simulatorSpacecraftState20.setQ(floatArray29);
    float[] floatArray31 = simulatorSpacecraftState20.getV();
    opssat.simulator.celestia.CelestiaData celestiaData32 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray14, floatArray31);
    float[] floatArray33 = celestiaData32.getQ();
    opssat.simulator.util.SimulatorData simulatorData37 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date38 = simulatorData37.getCurrentTime();
    int int39 = opssat.simulator.util.DateExtraction.getDayFromDate(date38);
    opssat.simulator.util.SimulatorData simulatorData41 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date42 = simulatorData41.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData44 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date45 = simulatorData44.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap46 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date42, date45);
    opssat.simulator.util.SimulatorHeader simulatorHeader47 =
        new opssat.simulator.util.SimulatorHeader(
            false, date38, date45);
    opssat.simulator.util.SimulatorData simulatorData48 = new opssat.simulator.util.SimulatorData(
        (short) 0, date38);
    celestiaData32.setDate(date38);
    int int50 = celestiaData32.getSeconds();
    int int51 = celestiaData32.getDays();
    java.lang.String str52 = celestiaData32.getDate();
    float[] floatArray53 = celestiaData32.getQ();
    int int54 = celestiaData32.getHours();
    java.lang.String str55 = celestiaData32.getInfo();
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + 340.0d + "'", double12 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str13 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str13.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray14);
    org.junit.Assert.assertTrue("'" + str16 + "' != '" + "UnknownGUIData" + "'",
        str16.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double21 + "' != '" + 340.0d + "'", double21 == 340.0d);
    org.junit.Assert.assertTrue("'" + double22 + "' != '" + 340.0d + "'", double22 == 340.0d);
    org.junit.Assert.assertTrue("'" + double27 + "' != '" + 340.0d + "'", double27 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str28 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str28.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray29);
    org.junit.Assert.assertNotNull(floatArray31);
    org.junit.Assert.assertNotNull(floatArray33);
    org.junit.Assert.assertNotNull(date38);
    org.junit.Assert.assertNotNull(date42);
    org.junit.Assert.assertNotNull(date45);
    org.junit.Assert.assertNotNull(timeUnitMap46);
    org.junit.Assert.assertNotNull(floatArray53);
    org.junit.Assert.assertNull(str55);
  }

  @Test
  public void test1053() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1053");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor3 = octetList0.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor5 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList0, "hi!");
    argumentDescriptor5.restoreArgument();
    argumentDescriptor5.restoreArgument();
    argumentDescriptor5.restoreArgument();
    java.lang.String str9 = argumentDescriptor5.toString();
    argumentDescriptor5.setName("OPS-SAT SoftSim:");
    java.lang.String str12 = argumentDescriptor5.getName();
    java.lang.String str13 = argumentDescriptor5.getName();
    try {
      int int15 = argumentDescriptor5.getTypeAsIntByIndex(17);
      org.junit.Assert.fail(
          "Expected exception of type java.lang.ClassCastException; message: org.ccsds.moims.mo.mal.structures.OctetList cannot be cast to [I");
    } catch (java.lang.ClassCastException e) {
    }
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(byteItor3);
    org.junit.Assert.assertTrue("'" + str9 + "' != '" + "" + "'", str9.equals(""));
    org.junit.Assert.assertTrue("'" + str12 + "' != '" + "OPS-SAT SoftSim:" + "'",
        str12.equals("OPS-SAT SoftSim:"));
    org.junit.Assert.assertTrue("'" + str13 + "' != '" + "OPS-SAT SoftSim:" + "'",
        str13.equals("OPS-SAT SoftSim:"));
  }

  @Test
  public void test1054() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1054");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    boolean boolean14 = simulatorHeader12.isAutoStartSystem();
    int int15 = simulatorHeader12.getDayStartDate();
    boolean boolean16 = simulatorHeader12.isUseCelestia();
    opssat.simulator.util.SimulatorData simulatorData19 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date20 = simulatorData19.getCurrentTime();
    int int21 = opssat.simulator.util.DateExtraction.getDayFromDate(date20);
    opssat.simulator.util.SimulatorData simulatorData23 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date24 = simulatorData23.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData26 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date27 = simulatorData26.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap28 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date24, date27);
    opssat.simulator.util.SimulatorHeader simulatorHeader29 =
        new opssat.simulator.util.SimulatorHeader(
            false, date20, date27);
    simulatorHeader12.setStartDate(date27);
    simulatorHeader12.setKeplerElements("-0100.0000000");
    boolean boolean33 = simulatorHeader12.isUseOrekitPropagator();
    java.lang.String str34 = simulatorHeader12.toString();
    int int35 = simulatorHeader12.getSecondStartDate();
    java.lang.String str36 = simulatorHeader12.getEndDateString();
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
    org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
    org.junit.Assert.assertNotNull(date20);
    org.junit.Assert.assertNotNull(date24);
    org.junit.Assert.assertNotNull(date27);
    org.junit.Assert.assertNotNull(timeUnitMap28);
    org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + false + "'", !boolean33);
  }

  @Test
  public void test1055() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1055");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet3 = octetList0.getAreaVersion();
    opssat.simulator.util.wav.WavFileException wavFileException5 =
        new opssat.simulator.util.wav.WavFileException(
            "UnknownGUIData");
    java.lang.Throwable[] throwableArray6 = wavFileException5.getSuppressed();
    boolean boolean7 = octetList0.equals(wavFileException5);
    org.ccsds.moims.mo.mal.structures.FloatList floatList9 =
        new org.ccsds.moims.mo.mal.structures.FloatList(
            (byte) 0);
    java.lang.Integer int10 = floatList9.getTypeShortForm();
    java.lang.Integer int11 = floatList9.getTypeShortForm();
    floatList9.clear();
    boolean boolean13 = octetList0.remove(floatList9);
    java.util.ListIterator<java.lang.Float> floatItor14 = floatList9.listIterator();
    org.ccsds.moims.mo.mal.structures.UShort uShort15 = floatList9.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.Element element16 = floatList9.createElement();
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(uOctet3);
    org.junit.Assert.assertNotNull(throwableArray6);
    org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
    org.junit.Assert.assertTrue("'" + int10 + "' != '" + (-4) + "'", int10.equals((-4)));
    org.junit.Assert.assertTrue("'" + int11 + "' != '" + (-4) + "'", int11.equals((-4)));
    org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + false + "'", !boolean13);
    org.junit.Assert.assertNotNull(floatItor14);
    org.junit.Assert.assertNotNull(uShort15);
    org.junit.Assert.assertNotNull(element16);
  }

  @Test
  public void test1056() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1056");
    }
    org.ccsds.moims.mo.mal.structures.StringList stringList0 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    org.ccsds.moims.mo.mal.structures.ShortList shortList2 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            ' ');
    java.util.ListIterator<java.lang.Short> shortItor3 = shortList2.listIterator();
    boolean boolean4 = stringList0.equals(shortList2);
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience13 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    double double14 = gPSSatInViewScience13.getMaxDistance();
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience23 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience32 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    double double33 = gPSSatInViewScience32.getMaxDistance();
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience42 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience51 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    double double52 = gPSSatInViewScience51.getStdDevElevation();
    opssat.simulator.orekit.GPSSatInViewScience[] gPSSatInViewScienceArray53 =
        new opssat.simulator.orekit.GPSSatInViewScience[]{
          gPSSatInViewScience13, gPSSatInViewScience23, gPSSatInViewScience32, gPSSatInViewScience42,
          gPSSatInViewScience51};
    opssat.simulator.orekit.GPSSatInViewScience[] gPSSatInViewScienceArray54 = shortList2
        .toArray(gPSSatInViewScienceArray53);
    shortList2.ensureCapacity(44);
    org.ccsds.moims.mo.mal.structures.IntegerList integerList58 =
        new org.ccsds.moims.mo.mal.structures.IntegerList(
            48);
    java.lang.Long long59 = integerList58.getShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort60 = integerList58.getAreaNumber();
    opssat.simulator.util.SimulatorSchedulerPiece simulatorSchedulerPiece64 =
        new opssat.simulator.util.SimulatorSchedulerPiece(
            'a', 11, "");
    boolean boolean65 = integerList58.contains(11);
    java.lang.Long long66 = integerList58.getShortForm();
    boolean boolean67 = shortList2.containsAll(integerList58);
    org.junit.Assert.assertNotNull(shortItor3);
    org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
    org.junit.Assert.assertTrue("'" + double14 + "' != '" + 0.0d + "'", double14 == 0.0d);
    org.junit.Assert.assertTrue("'" + double33 + "' != '" + 0.0d + "'", double33 == 0.0d);
    org.junit.Assert.assertTrue("'" + double52 + "' != '" + 11111.0d + "'", double52 == 11111.0d);
    org.junit.Assert.assertNotNull(gPSSatInViewScienceArray53);
    org.junit.Assert.assertNotNull(gPSSatInViewScienceArray54);
    org.junit.Assert.assertTrue("'" + long59 + "' != '" + 281475010265077L + "'",
        long59.equals(281475010265077L));
    org.junit.Assert.assertNotNull(uShort60);
    org.junit.Assert.assertTrue("'" + boolean65 + "' != '" + false + "'", !boolean65);
    org.junit.Assert.assertTrue("'" + long66 + "' != '" + 281475010265077L + "'",
        long66.equals(281475010265077L));
    org.junit.Assert.assertTrue("'" + boolean67 + "' != '" + true + "'", boolean67);
  }

  @Test
  public void test1057() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1057");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessSingleStreamOperatingBuffer endlessSingleStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessSingleStreamOperatingBuffer(
            logger0);
    byte[] byteArray3 = endlessSingleStreamOperatingBuffer1.getDataAsByteArray('4');
    byte[] byteArray5 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS.int16_2ByteArray(15);
    endlessSingleStreamOperatingBuffer1.setDataFromByteArray(byteArray5);
    java.lang.String str7 = endlessSingleStreamOperatingBuffer1.getDataBufferAsString();
    try {
      boolean boolean9 = endlessSingleStreamOperatingBuffer1
          .loadFromPath("Unknown data type [java.lang.String]");
      org.junit.Assert
          .fail("Expected exception of type java.lang.NullPointerException; message: null");
    } catch (java.lang.NullPointerException e) {
    }
    org.junit.Assert.assertNotNull(byteArray3);
    org.junit.Assert.assertNotNull(byteArray5);
    org.junit.Assert.assertTrue("'" + str7 + "' != '" + "byte[] {0x00,0x0F}" + "'",
        str7.equals("byte[] {0x00,0x0F}"));
  }

  @Test
  public void test1058() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1058");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet3 = octetList0.getAreaVersion();
    opssat.simulator.util.wav.WavFileException wavFileException5 =
        new opssat.simulator.util.wav.WavFileException(
            "UnknownGUIData");
    java.lang.Throwable[] throwableArray6 = wavFileException5.getSuppressed();
    boolean boolean7 = octetList0.equals(wavFileException5);
    org.ccsds.moims.mo.mal.structures.FloatList floatList9 =
        new org.ccsds.moims.mo.mal.structures.FloatList(
            (byte) 0);
    java.lang.Integer int10 = floatList9.getTypeShortForm();
    java.lang.Integer int11 = floatList9.getTypeShortForm();
    floatList9.clear();
    boolean boolean13 = octetList0.remove(floatList9);
    org.ccsds.moims.mo.mal.structures.UShort uShort14 = floatList9.getServiceNumber();
    org.ccsds.moims.mo.mal.structures.UShort uShort15 =
        org.ccsds.moims.mo.mal.structures.StringList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.OctetList octetList16 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int17 = octetList16.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort18 = octetList16.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UShort uShort19 =
        org.ccsds.moims.mo.mal.structures.OctetList.AREA_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort20 =
        org.ccsds.moims.mo.mal.structures.FloatList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort21 =
        org.ccsds.moims.mo.mal.structures.StringList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort22 =
        org.ccsds.moims.mo.mal.structures.URIList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort23 =
        org.ccsds.moims.mo.mal.structures.BooleanList.AREA_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort24 =
        org.ccsds.moims.mo.mal.structures.OctetList.AREA_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort25 =
        org.ccsds.moims.mo.mal.structures.URIList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort[] uShortArray26 =
        new org.ccsds.moims.mo.mal.structures.UShort[]{
          uShort25};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList27 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
    boolean boolean28 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList27,
        uShortArray26);
    uShortList27.ensureCapacity(0);
    int int32 = uShortList27.indexOf((byte) 1);
    uShortList27.clear();
    java.lang.Long[] longArray36 = new java.lang.Long[]{0L, 10L};
    java.util.ArrayList<java.lang.Long> longList37 = new java.util.ArrayList<java.lang.Long>();
    boolean boolean38 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Long>) longList37, longArray36);
    java.lang.Object obj39 = longList37.clone();
    boolean boolean40 = uShortList27.contains(longList37);
    org.ccsds.moims.mo.mal.structures.UShort uShort41 =
        org.ccsds.moims.mo.mal.structures.UShortList.SERVICE_SHORT_FORM;
    boolean boolean42 = uShortList27.add(uShort41);
    org.ccsds.moims.mo.mal.structures.UShort[] uShortArray43 =
        new org.ccsds.moims.mo.mal.structures.UShort[]{
          uShort15, uShort18, uShort19, uShort20, uShort21, uShort22, uShort23, uShort24, uShort41};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList44 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
    boolean boolean45 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList44,
        uShortArray43);
    uShortList44.ensureCapacity(100);
    int int48 = uShortList44.size();
    org.ccsds.moims.mo.mal.structures.OctetList octetList49 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    octetList49.ensureCapacity(13);
    java.lang.Byte[] byteArray56 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList57 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean58 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList57, byteArray56);
    java.lang.Integer[] intArray61 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList62 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean63 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList62, intArray61);
    boolean boolean64 = byteList57.retainAll(intList62);
    boolean boolean65 = octetList49.containsAll(intList62);
    java.util.Iterator<java.lang.Integer> intItor66 = intList62.iterator();
    boolean boolean67 = uShortList44.remove(intList62);
    org.ccsds.moims.mo.mal.structures.UShortList uShortList68 =
        new org.ccsds.moims.mo.mal.structures.UShortList();
    java.lang.Boolean[] booleanArray71 = new java.lang.Boolean[]{true, true};
    java.util.ArrayList<java.lang.Boolean> booleanList72 =
        new java.util.ArrayList<java.lang.Boolean>();
    boolean boolean73 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Boolean>) booleanList72, booleanArray71);
    int int75 = booleanList72.indexOf(10);
    int int76 = booleanList72.size();
    java.lang.Byte[] byteArray80 = new java.lang.Byte[]{(byte) 10, (byte) 10, (byte) -1};
    java.util.ArrayList<java.lang.Byte> byteList81 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean82 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList81, byteArray80);
    java.lang.Object[] objArray83 = byteList81.toArray();
    java.util.Iterator<java.lang.Byte> byteItor84 = byteList81.iterator();
    boolean boolean85 = booleanList72.contains(byteList81);
    java.util.stream.Stream<java.lang.Boolean> booleanStream86 = booleanList72.stream();
    int int87 = uShortList68.lastIndexOf(booleanList72);
    org.ccsds.moims.mo.mal.structures.UShort uShort88 = uShortList68.getServiceNumber();
    org.ccsds.moims.mo.mal.structures.UShort uShort89 = uShortList68.getServiceNumber();
    boolean boolean90 = intList62.equals(uShortList68);
    boolean boolean91 = floatList9.removeAll(intList62);
    org.ccsds.moims.mo.mal.structures.UOctet uOctet92 = floatList9.getAreaVersion();
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(uOctet3);
    org.junit.Assert.assertNotNull(throwableArray6);
    org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
    org.junit.Assert.assertTrue("'" + int10 + "' != '" + (-4) + "'", int10.equals((-4)));
    org.junit.Assert.assertTrue("'" + int11 + "' != '" + (-4) + "'", int11.equals((-4)));
    org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + false + "'", !boolean13);
    org.junit.Assert.assertNotNull(uShort14);
    org.junit.Assert.assertNotNull(uShort15);
    org.junit.Assert.assertTrue("'" + int17 + "' != '" + (-7) + "'", int17.equals((-7)));
    org.junit.Assert.assertNotNull(uShort18);
    org.junit.Assert.assertNotNull(uShort19);
    org.junit.Assert.assertNotNull(uShort20);
    org.junit.Assert.assertNotNull(uShort21);
    org.junit.Assert.assertNotNull(uShort22);
    org.junit.Assert.assertNotNull(uShort23);
    org.junit.Assert.assertNotNull(uShort24);
    org.junit.Assert.assertNotNull(uShort25);
    org.junit.Assert.assertNotNull(uShortArray26);
    org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + true + "'", boolean28);
    org.junit.Assert.assertTrue("'" + int32 + "' != '" + (-1) + "'", int32 == (-1));
    org.junit.Assert.assertNotNull(longArray36);
    org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + true + "'", boolean38);
    org.junit.Assert.assertNotNull(obj39);
    org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + false + "'", !boolean40);
    org.junit.Assert.assertNotNull(uShort41);
    org.junit.Assert.assertTrue("'" + boolean42 + "' != '" + true + "'", boolean42);
    org.junit.Assert.assertNotNull(uShortArray43);
    org.junit.Assert.assertTrue("'" + boolean45 + "' != '" + true + "'", boolean45);
    org.junit.Assert.assertTrue("'" + int48 + "' != '" + 9 + "'", int48 == 9);
    org.junit.Assert.assertNotNull(byteArray56);
    org.junit.Assert.assertTrue("'" + boolean58 + "' != '" + true + "'", boolean58);
    org.junit.Assert.assertNotNull(intArray61);
    org.junit.Assert.assertTrue("'" + boolean63 + "' != '" + true + "'", boolean63);
    org.junit.Assert.assertTrue("'" + boolean64 + "' != '" + true + "'", boolean64);
    org.junit.Assert.assertTrue("'" + boolean65 + "' != '" + false + "'", !boolean65);
    org.junit.Assert.assertNotNull(intItor66);
    org.junit.Assert.assertTrue("'" + boolean67 + "' != '" + false + "'", !boolean67);
    org.junit.Assert.assertNotNull(booleanArray71);
    org.junit.Assert.assertTrue("'" + boolean73 + "' != '" + true + "'", boolean73);
    org.junit.Assert.assertTrue("'" + int75 + "' != '" + (-1) + "'", int75 == (-1));
    org.junit.Assert.assertTrue("'" + int76 + "' != '" + 2 + "'", int76 == 2);
    org.junit.Assert.assertNotNull(byteArray80);
    org.junit.Assert.assertTrue("'" + boolean82 + "' != '" + true + "'", boolean82);
    org.junit.Assert.assertNotNull(objArray83);
    org.junit.Assert.assertNotNull(byteItor84);
    org.junit.Assert.assertTrue("'" + boolean85 + "' != '" + false + "'", !boolean85);
    org.junit.Assert.assertNotNull(booleanStream86);
    org.junit.Assert.assertTrue("'" + int87 + "' != '" + (-1) + "'", int87 == (-1));
    org.junit.Assert.assertNotNull(uShort88);
    org.junit.Assert.assertNotNull(uShort89);
    org.junit.Assert.assertTrue("'" + boolean90 + "' != '" + false + "'", !boolean90);
    org.junit.Assert.assertTrue("'" + boolean91 + "' != '" + false + "'", !boolean91);
    org.junit.Assert.assertNotNull(uOctet92);
  }

  @Test
  public void test1059() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1059");
    }
    org.ccsds.moims.mo.mal.structures.StringList stringList0 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    java.util.Iterator<java.lang.String> strItor1 = stringList0.iterator();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = stringList0.getAreaVersion();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState6 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray9 = new float[]{28, 8};
    simulatorSpacecraftState6.setQ(floatArray9);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState14 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double15 = simulatorSpacecraftState14.getLatitude();
    java.lang.String str16 = simulatorSpacecraftState14.getMagField();
    float[] floatArray17 = simulatorSpacecraftState14.getR();
    simulatorSpacecraftState6.setQ(floatArray17);
    java.lang.String str19 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray17);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState23 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double24 = simulatorSpacecraftState23.getLatitude();
    double double25 = simulatorSpacecraftState23.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState29 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double30 = simulatorSpacecraftState29.getLatitude();
    java.lang.String str31 = simulatorSpacecraftState29.getMagField();
    float[] floatArray32 = simulatorSpacecraftState29.getR();
    simulatorSpacecraftState23.setQ(floatArray32);
    float[] floatArray34 = simulatorSpacecraftState23.getV();
    opssat.simulator.celestia.CelestiaData celestiaData35 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray17, floatArray34);
    float[] floatArray36 = celestiaData35.getQ();
    opssat.simulator.util.SimulatorData simulatorData40 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date41 = simulatorData40.getCurrentTime();
    int int42 = opssat.simulator.util.DateExtraction.getDayFromDate(date41);
    opssat.simulator.util.SimulatorData simulatorData44 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date45 = simulatorData44.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData47 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date48 = simulatorData47.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap49 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date45, date48);
    opssat.simulator.util.SimulatorHeader simulatorHeader50 =
        new opssat.simulator.util.SimulatorHeader(
            false, date41, date48);
    opssat.simulator.util.SimulatorData simulatorData51 = new opssat.simulator.util.SimulatorData(
        (short) 0, date41);
    celestiaData35.setDate(date41);
    int int53 = celestiaData35.getSeconds();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState57 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray60 = new float[]{28, 8};
    simulatorSpacecraftState57.setQ(floatArray60);
    celestiaData35.setQ(floatArray60);
    int int63 = stringList0.lastIndexOf(celestiaData35);
    boolean boolean65 = stringList0.add("[true, false]");
    org.junit.Assert.assertNotNull(strItor1);
    org.junit.Assert.assertNotNull(uOctet2);
    org.junit.Assert.assertNotNull(floatArray9);
    org.junit.Assert.assertTrue("'" + double15 + "' != '" + 340.0d + "'", double15 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str16 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str16.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray17);
    org.junit.Assert.assertTrue("'" + str19 + "' != '" + "UnknownGUIData" + "'",
        str19.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double24 + "' != '" + 340.0d + "'", double24 == 340.0d);
    org.junit.Assert.assertTrue("'" + double25 + "' != '" + 340.0d + "'", double25 == 340.0d);
    org.junit.Assert.assertTrue("'" + double30 + "' != '" + 340.0d + "'", double30 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str31 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str31.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray32);
    org.junit.Assert.assertNotNull(floatArray34);
    org.junit.Assert.assertNotNull(floatArray36);
    org.junit.Assert.assertNotNull(date41);
    org.junit.Assert.assertNotNull(date45);
    org.junit.Assert.assertNotNull(date48);
    org.junit.Assert.assertNotNull(timeUnitMap49);
    org.junit.Assert.assertNotNull(floatArray60);
    org.junit.Assert.assertTrue("'" + int63 + "' != '" + (-1) + "'", int63 == (-1));
    org.junit.Assert.assertTrue("'" + boolean65 + "' != '" + true + "'", boolean65);
  }

  @Test
  public void test1061() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1061");
    }
    org.ccsds.moims.mo.mal.structures.URI uRI1 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    java.lang.Integer int2 = uRI1.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.URI uRI4 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    org.ccsds.moims.mo.mal.structures.UOctet uOctet5 = uRI4.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.URI uRI7 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    org.ccsds.moims.mo.mal.structures.URI[] uRIArray8 = new org.ccsds.moims.mo.mal.structures.URI[]{
      uRI1, uRI4, uRI7};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList9 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
    boolean boolean10 = java.util.Collections
        .addAll((java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList9, uRIArray8);
    org.ccsds.moims.mo.mal.structures.FineTime fineTime11 =
        new org.ccsds.moims.mo.mal.structures.FineTime();
    int int12 = uRIList9.indexOf(fineTime11);
    uRIList9.ensureCapacity(40);
    boolean boolean15 = uRIList9.isEmpty();
    java.lang.String str16 = uRIList9.toString();
    java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.URI> uRIStream17 = uRIList9
        .parallelStream();
    org.ccsds.moims.mo.mal.structures.LongList longList18 =
        new org.ccsds.moims.mo.mal.structures.LongList();
    java.util.ListIterator<java.lang.Long> longItor19 = longList18.listIterator();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet20 = longList18.getAreaVersion();
    boolean boolean21 = uRIList9.contains(uOctet20);
    org.ccsds.moims.mo.mal.structures.URI uRI23 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    java.lang.Integer int24 = uRI23.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.URI uRI26 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    org.ccsds.moims.mo.mal.structures.UOctet uOctet27 = uRI26.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.URI uRI29 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    org.ccsds.moims.mo.mal.structures.URI[] uRIArray30 =
        new org.ccsds.moims.mo.mal.structures.URI[]{
          uRI23, uRI26, uRI29};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList31 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
    boolean boolean32 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList31, uRIArray30);
    org.ccsds.moims.mo.mal.structures.FineTime fineTime33 =
        new org.ccsds.moims.mo.mal.structures.FineTime();
    int int34 = uRIList31.indexOf(fineTime33);
    uRIList31.ensureCapacity(40);
    java.lang.Object obj37 = uRIList31.clone();
    org.ccsds.moims.mo.mal.structures.URI uRI39 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    java.lang.Integer int40 = uRI39.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.URI uRI42 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    org.ccsds.moims.mo.mal.structures.UOctet uOctet43 = uRI42.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.URI uRI45 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    org.ccsds.moims.mo.mal.structures.URI[] uRIArray46 =
        new org.ccsds.moims.mo.mal.structures.URI[]{
          uRI39, uRI42, uRI45};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList47 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
    boolean boolean48 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList47, uRIArray46);
    org.ccsds.moims.mo.mal.structures.FineTime fineTime49 =
        new org.ccsds.moims.mo.mal.structures.FineTime();
    int int50 = uRIList47.indexOf(fineTime49);
    uRIList47.ensureCapacity(40);
    java.lang.Object obj53 = uRIList47.clone();
    org.ccsds.moims.mo.mal.structures.URI uRI56 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    org.ccsds.moims.mo.mal.structures.UOctet uOctet57 = uRI56.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.URI uRI58 = uRIList47.set((byte) 1, uRI56);
    java.lang.Long long59 = uRI56.getShortForm();
    boolean boolean60 = uRIList31.add(uRI56);
    java.lang.String str61 = uRIList31.toString();
    java.util.Iterator<org.ccsds.moims.mo.mal.structures.URI> uRIItor62 = uRIList31.iterator();
    boolean boolean63 = uRIList9.equals(uRIItor62);
    try {
      java.util.List<org.ccsds.moims.mo.mal.structures.URI> uRIList66 = uRIList9.subList(70, 65535);
      org.junit.Assert.fail(
          "Expected exception of type java.lang.IndexOutOfBoundsException; message: toIndex = 65535");
    } catch (java.lang.IndexOutOfBoundsException e) {
    }
    org.junit.Assert.assertTrue("'" + int2 + "' != '" + 18 + "'", int2.equals(18));
    org.junit.Assert.assertNotNull(uOctet5);
    org.junit.Assert.assertNotNull(uRIArray8);
    org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
    org.junit.Assert.assertTrue("'" + int12 + "' != '" + (-1) + "'", int12 == (-1));
    org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
    org.junit.Assert.assertTrue("'" + str16 + "' != '" + "[0100.0000, 0100.0000, 0100.0000]" + "'",
        str16.equals("[0100.0000, 0100.0000, 0100.0000]"));
    org.junit.Assert.assertNotNull(uRIStream17);
    org.junit.Assert.assertNotNull(longItor19);
    org.junit.Assert.assertNotNull(uOctet20);
    org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
    org.junit.Assert.assertTrue("'" + int24 + "' != '" + 18 + "'", int24.equals(18));
    org.junit.Assert.assertNotNull(uOctet27);
    org.junit.Assert.assertNotNull(uRIArray30);
    org.junit.Assert.assertTrue("'" + boolean32 + "' != '" + true + "'", boolean32);
    org.junit.Assert.assertTrue("'" + int34 + "' != '" + (-1) + "'", int34 == (-1));
    org.junit.Assert.assertNotNull(obj37);
    org.junit.Assert.assertTrue("'" + int40 + "' != '" + 18 + "'", int40.equals(18));
    org.junit.Assert.assertNotNull(uOctet43);
    org.junit.Assert.assertNotNull(uRIArray46);
    org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + true + "'", boolean48);
    org.junit.Assert.assertTrue("'" + int50 + "' != '" + (-1) + "'", int50 == (-1));
    org.junit.Assert.assertNotNull(obj53);
    org.junit.Assert.assertNotNull(uOctet57);
    org.junit.Assert.assertNotNull(uRI58);
    org.junit.Assert.assertTrue("'" + long59 + "' != '" + 281474993487890L + "'",
        long59.equals(281474993487890L));
    org.junit.Assert.assertTrue("'" + boolean60 + "' != '" + true + "'", boolean60);
    org.junit.Assert.assertTrue(
        "'" + str61 + "' != '" + "[0100.0000, 0100.0000, 0100.0000, 0100.0000]" + "'",
        str61.equals("[0100.0000, 0100.0000, 0100.0000, 0100.0000]"));
    org.junit.Assert.assertNotNull(uRIItor62);
    org.junit.Assert.assertTrue("'" + boolean63 + "' != '" + false + "'", !boolean63);
  }

  @Test
  public void test1062() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1062");
    }
    opssat.simulator.util.SimulatorData simulatorData3 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date4 = simulatorData3.getCurrentTime();
    java.util.Date date5 = simulatorData3.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(17,
        date5);
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    int int11 = opssat.simulator.util.DateExtraction.getDayFromDate(date10);
    opssat.simulator.util.SimulatorData simulatorData13 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date14 = simulatorData13.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData16 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date17 = simulatorData16.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap18 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date14, date17);
    opssat.simulator.util.SimulatorHeader simulatorHeader19 =
        new opssat.simulator.util.SimulatorHeader(
            false, date10, date17);
    opssat.simulator.util.SimulatorHeader simulatorHeader20 =
        new opssat.simulator.util.SimulatorHeader(
            false, date5, date10);
    boolean boolean21 = simulatorHeader20.isAutoStartTime();
    simulatorHeader20.setUseCelestia(false);
    simulatorHeader20.setOrekitTLE1("[]");
    simulatorHeader20
        .setOrekitPropagator("opssat.simulator.util.wav.WavFileException: UnknownGUIData");
    java.util.Date date28 = simulatorHeader20.getEndDate();
    simulatorHeader20.setAutoStartSystem(true);
    simulatorHeader20.setTimeFactor(12);
    java.util.Date date34 = simulatorHeader20.parseStringIntoDate(
        "#Run the processing of internal models\nstartModels=false\n#Increment the simulated time (depends on startModels)\nstartTime=false\n#Speed up of time factor\ntimeFactor=31\n#Kepler elements for orbit A[km];E;i[deg];RAAN[deg];ARG_PER[deg];TRUE_A[deg] 7021.0;0.0;98.05;340.0;0.0;0.0\nkeplerElements=7021.0;0.0;98.05;340.0;0.0;0.0\n#Enable the Orekit library\norekit=false\n#Enable updates from Internet (used for gps constellation TLEs)\nupdateFromInternet=false\n#Configuration of the Celestia server\ncelestia=false\ncelestiaPort=0\n#Start and end dates of simulation\nstartDate=2019:05:23 15:10:15 UTC\nendDate=2019:05:23 15:10:15 UTC\n#Logging level to files found in $USER_HOME/.ops-sat-simulator/\n#Possible values SEVERE,INFO,FINE,FINER,FINEST,ALL\ncentralLogLevel=INFO\nsimulatorLogLevel=INFO\nconsoleLogLevel=INFO");
    java.lang.String str35 = simulatorHeader20.getOrekitTLE1();
    org.junit.Assert.assertNotNull(date4);
    org.junit.Assert.assertNotNull(date5);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(date14);
    org.junit.Assert.assertNotNull(date17);
    org.junit.Assert.assertNotNull(timeUnitMap18);
    org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
    org.junit.Assert.assertNotNull(date28);
    org.junit.Assert.assertNull(date34);
    org.junit.Assert.assertTrue("'" + str35 + "' != '" + "[]" + "'", str35.equals("[]"));
  }

  @Test
  public void test1063() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1063");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor3 = octetList0.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor5 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList0, "hi!");
    argumentDescriptor5.restoreArgument();
    argumentDescriptor5.setName("01700.0000000");
    java.lang.Object obj9 = argumentDescriptor5.getType();
    java.lang.String str10 = argumentDescriptor5.getName();
    java.lang.Short[] shortArray13 = new java.lang.Short[]{(short) -1, (short) 10};
    java.util.ArrayList<java.lang.Short> shortList14 = new java.util.ArrayList<java.lang.Short>();
    boolean boolean15 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Short>) shortList14, shortArray13);
    boolean boolean16 = shortList14.isEmpty();
    java.lang.Object obj17 = null;
    boolean boolean18 = shortList14.contains(obj17);
    shortList14.trimToSize();
    argumentDescriptor5.setType(shortList14);
    org.ccsds.moims.mo.mal.structures.OctetList octetList22 =
        new org.ccsds.moims.mo.mal.structures.OctetList(
            34);
    argumentDescriptor5.setType(octetList22);
    java.lang.Long[] longArray30 = new java.lang.Long[]{281475010265070L, 100L, 0L,
      281475010265070L, 281475010265070L, 1L};
    java.util.ArrayList<java.lang.Long> longList31 = new java.util.ArrayList<java.lang.Long>();
    boolean boolean32 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Long>) longList31, longArray30);
    boolean boolean33 = longList31.isEmpty();
    java.util.Spliterator<java.lang.Long> longSpliterator34 = longList31.spliterator();
    java.lang.Object obj35 = longList31.clone();
    java.util.stream.Stream<java.lang.Long> longStream36 = longList31.parallelStream();
    org.ccsds.moims.mo.mal.structures.Identifier identifier37 =
        new org.ccsds.moims.mo.mal.structures.Identifier();
    org.ccsds.moims.mo.mal.structures.UShort uShort38 = identifier37.getServiceNumber();
    org.ccsds.moims.mo.mal.structures.ULong uLong39 = new org.ccsds.moims.mo.mal.structures.ULong();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet40 = uLong39.getAreaVersion();
    boolean boolean41 = identifier37.equals(uOctet40);
    org.ccsds.moims.mo.mal.structures.Element element42 = identifier37.createElement();
    boolean boolean43 = longList31.equals(identifier37);
    org.ccsds.moims.mo.mal.structures.FloatList floatList45 =
        new org.ccsds.moims.mo.mal.structures.FloatList(
            (byte) 0);
    java.lang.Integer int46 = floatList45.getTypeShortForm();
    java.lang.Integer int47 = floatList45.getTypeShortForm();
    java.lang.String str48 = floatList45.toString();
    org.ccsds.moims.mo.mal.structures.UShort uShort49 = floatList45.getAreaNumber();
    int int50 = longList31.indexOf(uShort49);
    org.ccsds.moims.mo.mal.structures.UOctet uOctet51 = uShort49.getAreaVersion();
    argumentDescriptor5.setType(uShort49);
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(byteItor3);
    org.junit.Assert.assertNotNull(obj9);
    org.junit.Assert.assertTrue("'" + str10 + "' != '" + "01700.0000000" + "'",
        str10.equals("01700.0000000"));
    org.junit.Assert.assertNotNull(shortArray13);
    org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + true + "'", boolean15);
    org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
    org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + false + "'", !boolean18);
    org.junit.Assert.assertNotNull(longArray30);
    org.junit.Assert.assertTrue("'" + boolean32 + "' != '" + true + "'", boolean32);
    org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + false + "'", !boolean33);
    org.junit.Assert.assertNotNull(longSpliterator34);
    org.junit.Assert.assertNotNull(obj35);
    org.junit.Assert.assertNotNull(longStream36);
    org.junit.Assert.assertNotNull(uShort38);
    org.junit.Assert.assertNotNull(uOctet40);
    org.junit.Assert.assertTrue("'" + boolean41 + "' != '" + false + "'", !boolean41);
    org.junit.Assert.assertNotNull(element42);
    org.junit.Assert.assertTrue("'" + boolean43 + "' != '" + false + "'", !boolean43);
    org.junit.Assert.assertTrue("'" + int46 + "' != '" + (-4) + "'", int46.equals((-4)));
    org.junit.Assert.assertTrue("'" + int47 + "' != '" + (-4) + "'", int47.equals((-4)));
    org.junit.Assert.assertTrue("'" + str48 + "' != '" + "[]" + "'", str48.equals("[]"));
    org.junit.Assert.assertNotNull(uShort49);
    org.junit.Assert.assertTrue("'" + int50 + "' != '" + (-1) + "'", int50 == (-1));
    org.junit.Assert.assertNotNull(uOctet51);
  }

  @Test
  public void test1064() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1064");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double4 = simulatorSpacecraftState3.getLatitude();
    java.lang.String str5 = simulatorSpacecraftState3.getMagField();
    java.lang.String str6 = simulatorSpacecraftState3.toString();
    double[] doubleArray7 = simulatorSpacecraftState3.getSunVector();
    simulatorSpacecraftState3.setSatsInView((short) 10);
    java.lang.String str10 = simulatorSpacecraftState3.toString();
    float[] floatArray11 = null;
    simulatorSpacecraftState3.setQ(floatArray11);
    java.lang.String str13 = simulatorSpacecraftState3.getMagField();
    org.junit.Assert.assertTrue("'" + double4 + "' != '" + 340.0d + "'", double4 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str5 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str5.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str6 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str6.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray7);
    org.junit.Assert.assertTrue(
        "'" + str10 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str10.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertTrue(
        "'" + str13 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str13.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
  }

  @Test
  public void test1065() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1065");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessSingleStreamOperatingBuffer endlessSingleStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessSingleStreamOperatingBuffer(
            logger0);
    byte[] byteArray3 = endlessSingleStreamOperatingBuffer1.getDataAsByteArray('4');
    byte[] byteArray5 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS.int16_2ByteArray(15);
    endlessSingleStreamOperatingBuffer1.setDataFromByteArray(byteArray5);
    int int7 = endlessSingleStreamOperatingBuffer1.getOperatingIndex();
    byte[] byteArray9 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS.int2ByteArray(' ');
    endlessSingleStreamOperatingBuffer1.setDataFromByteArray(byteArray9);
    org.ccsds.moims.mo.mal.structures.UShort uShort11 =
        org.ccsds.moims.mo.mal.structures.URIList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort[] uShortArray12 =
        new org.ccsds.moims.mo.mal.structures.UShort[]{
          uShort11};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList13 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
    boolean boolean14 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList13,
        uShortArray12);
    uShortList13.ensureCapacity(0);
    int int18 = uShortList13.indexOf((byte) 1);
    uShortList13.clear();
    java.lang.Long[] longArray22 = new java.lang.Long[]{0L, 10L};
    java.util.ArrayList<java.lang.Long> longList23 = new java.util.ArrayList<java.lang.Long>();
    boolean boolean24 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Long>) longList23, longArray22);
    java.lang.Object obj25 = longList23.clone();
    int int26 = uShortList13.lastIndexOf(obj25);
    java.lang.Byte[] byteArray31 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList32 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean33 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList32, byteArray31);
    java.lang.Integer[] intArray36 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList37 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean38 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList37, intArray36);
    boolean boolean39 = byteList32.retainAll(intList37);
    java.lang.Integer[] intArray42 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList43 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean44 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList43, intArray42);
    int int46 = intList43.lastIndexOf((byte) 10);
    boolean boolean47 = intList37.retainAll(intList43);
    java.lang.Object obj48 = intList37.clone();
    boolean boolean49 = uShortList13.removeAll(intList37);
    java.lang.Object[] objArray50 = uShortList13.toArray();
    java.util.Spliterator<org.ccsds.moims.mo.mal.structures.UShort> uShortSpliterator51 =
        uShortList13
            .spliterator();
    java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream52 = uShortList13
        .parallelStream();
    endlessSingleStreamOperatingBuffer1.setDataBuffer(uShortStream52);
    java.lang.String str54 = endlessSingleStreamOperatingBuffer1.getDataBufferAsString();
    org.junit.Assert.assertNotNull(byteArray3);
    org.junit.Assert.assertNotNull(byteArray5);
    org.junit.Assert.assertTrue("'" + int7 + "' != '" + 0 + "'", int7 == 0);
    org.junit.Assert.assertNotNull(byteArray9);
    org.junit.Assert.assertNotNull(uShort11);
    org.junit.Assert.assertNotNull(uShortArray12);
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + true + "'", boolean14);
    org.junit.Assert.assertTrue("'" + int18 + "' != '" + (-1) + "'", int18 == (-1));
    org.junit.Assert.assertNotNull(longArray22);
    org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + true + "'", boolean24);
    org.junit.Assert.assertNotNull(obj25);
    org.junit.Assert.assertTrue("'" + int26 + "' != '" + (-1) + "'", int26 == (-1));
    org.junit.Assert.assertNotNull(byteArray31);
    org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + true + "'", boolean33);
    org.junit.Assert.assertNotNull(intArray36);
    org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + true + "'", boolean38);
    org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + true + "'", boolean39);
    org.junit.Assert.assertNotNull(intArray42);
    org.junit.Assert.assertTrue("'" + boolean44 + "' != '" + true + "'", boolean44);
    org.junit.Assert.assertTrue("'" + int46 + "' != '" + (-1) + "'", int46 == (-1));
    org.junit.Assert.assertTrue("'" + boolean47 + "' != '" + true + "'", boolean47);
    org.junit.Assert.assertNotNull(obj48);
    org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + false + "'", !boolean49);
    org.junit.Assert.assertNotNull(objArray50);
    org.junit.Assert.assertNotNull(uShortSpliterator51);
    org.junit.Assert.assertNotNull(uShortStream52);
    org.junit.Assert.assertTrue("'" + str54 + "' != '"
        + "Unknown data type [java.util.stream.ReferencePipeline$Head]" + "'",
        str54.equals("Unknown data type [java.util.stream.ReferencePipeline$Head]"));
  }

  @Test
  public void test1066() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1066");
    }
    org.ccsds.moims.mo.mal.structures.StringList stringList0 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    org.ccsds.moims.mo.mal.structures.ShortList shortList2 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            ' ');
    java.util.ListIterator<java.lang.Short> shortItor3 = shortList2.listIterator();
    boolean boolean4 = stringList0.equals(shortList2);
    java.util.stream.BaseStream[] baseStreamArray6 = new java.util.stream.BaseStream[0];
    @SuppressWarnings("unchecked")
    java.util.stream.BaseStream<java.lang.Float, java.util.stream.Stream<java.lang.Float>>[] floatBaseStreamArray7 =
        baseStreamArray6;
    java.util.stream.BaseStream<java.lang.Float, java.util.stream.Stream<java.lang.Float>>[] floatBaseStreamArray8 =
        stringList0
            .toArray(
                (java.util.stream.BaseStream<java.lang.Float, java.util.stream.Stream<java.lang.Float>>[]) baseStreamArray6);
    java.util.stream.Stream<java.lang.String> strStream9 = stringList0.stream();
    java.lang.Object obj10 = stringList0.clone();
    opssat.simulator.util.LoggerFormatter loggerFormatter11 =
        new opssat.simulator.util.LoggerFormatter();
    opssat.simulator.util.LoggerFormatter loggerFormatter12 =
        new opssat.simulator.util.LoggerFormatter();
    opssat.simulator.util.LoggerFormatter loggerFormatter13 =
        new opssat.simulator.util.LoggerFormatter();
    opssat.simulator.util.LoggerFormatter[] loggerFormatterArray14 =
        new opssat.simulator.util.LoggerFormatter[]{
          loggerFormatter11, loggerFormatter12, loggerFormatter13};
    opssat.simulator.util.LoggerFormatter[] loggerFormatterArray15 = stringList0
        .toArray(loggerFormatterArray14);
    java.util.stream.Stream<java.lang.String> strStream16 = stringList0.parallelStream();
    org.ccsds.moims.mo.mal.structures.UShort uShort17 = stringList0.getServiceNumber();
    stringList0.trimToSize();
    java.util.ListIterator<java.lang.String> strItor19 = stringList0.listIterator();
    java.util.ListIterator<java.lang.String> strItor20 = stringList0.listIterator();
    org.ccsds.moims.mo.mal.structures.UShort uShort21 = stringList0.getServiceNumber();
    org.junit.Assert.assertNotNull(shortItor3);
    org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
    org.junit.Assert.assertNotNull(baseStreamArray6);
    org.junit.Assert.assertNotNull(floatBaseStreamArray7);
    org.junit.Assert.assertNotNull(floatBaseStreamArray8);
    org.junit.Assert.assertNotNull(strStream9);
    org.junit.Assert.assertNotNull(obj10);
    org.junit.Assert.assertNotNull(loggerFormatterArray14);
    org.junit.Assert.assertNotNull(loggerFormatterArray15);
    org.junit.Assert.assertNotNull(strStream16);
    org.junit.Assert.assertNotNull(uShort17);
    org.junit.Assert.assertNotNull(strItor19);
    org.junit.Assert.assertNotNull(strItor20);
    org.junit.Assert.assertNotNull(uShort21);
  }

  @Test
  public void test1067() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1067");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double4 = simulatorSpacecraftState3.getLatitude();
    double double5 = simulatorSpacecraftState3.getLongitude();
    java.lang.String str6 = simulatorSpacecraftState3.getModeOperation();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState10 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState10.setLongitude(4);
    double[] doubleArray14 = new double[]{(-1.0f)};
    simulatorSpacecraftState10.setMagField(doubleArray14);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState19 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double20 = simulatorSpacecraftState19.getLatitude();
    java.lang.String str21 = simulatorSpacecraftState19.getMagField();
    java.lang.String str22 = simulatorSpacecraftState19.toString();
    double[] doubleArray23 = simulatorSpacecraftState19.getSunVector();
    simulatorSpacecraftState10.setMagnetometer(doubleArray23);
    simulatorSpacecraftState3.setMagField(doubleArray23);
    simulatorSpacecraftState3.setSatsInView(50);
    simulatorSpacecraftState3.setLatitude(62);
    org.junit.Assert.assertTrue("'" + double4 + "' != '" + 340.0d + "'", double4 == 340.0d);
    org.junit.Assert.assertTrue("'" + double5 + "' != '" + (-1.0d) + "'", double5 == (-1.0d));
    org.junit.Assert.assertNull(str6);
    org.junit.Assert.assertNotNull(doubleArray14);
    org.junit.Assert.assertTrue("'" + double20 + "' != '" + 340.0d + "'", double20 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str21 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str21.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str22 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str22.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray23);
  }

  @Test
  public void test1068() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1068");
    }
    opssat.simulator.util.SimulatorSchedulerPiece simulatorSchedulerPiece3 =
        new opssat.simulator.util.SimulatorSchedulerPiece(
            281475010265075L, (-1), "");
  }

  @Test
  public void test1069() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1069");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor3 = octetList0.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor5 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList0, "hi!");
    argumentDescriptor5.restoreArgument();
    argumentDescriptor5.setName("01700.0000000");
    java.lang.Object obj9 = argumentDescriptor5.getType();
    java.lang.String str10 = argumentDescriptor5.getName();
    java.lang.Short[] shortArray13 = new java.lang.Short[]{(short) -1, (short) 10};
    java.util.ArrayList<java.lang.Short> shortList14 = new java.util.ArrayList<java.lang.Short>();
    boolean boolean15 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Short>) shortList14, shortArray13);
    boolean boolean16 = shortList14.isEmpty();
    java.lang.Object obj17 = null;
    boolean boolean18 = shortList14.contains(obj17);
    shortList14.trimToSize();
    argumentDescriptor5.setType(shortList14);
    java.util.Iterator<java.lang.Short> shortItor21 = shortList14.iterator();
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(byteItor3);
    org.junit.Assert.assertNotNull(obj9);
    org.junit.Assert.assertTrue("'" + str10 + "' != '" + "01700.0000000" + "'",
        str10.equals("01700.0000000"));
    org.junit.Assert.assertNotNull(shortArray13);
    org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + true + "'", boolean15);
    org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
    org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + false + "'", !boolean18);
    org.junit.Assert.assertNotNull(shortItor21);
  }

  @Test
  public void test1070() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1070");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    int int14 = simulatorHeader12.getMinuteStartDate();
    opssat.simulator.util.SimulatorData simulatorData16 = new opssat.simulator.util.SimulatorData(
        (-18));
    long long17 = simulatorData16.getCurrentTimeMillis();
    java.lang.String str18 = simulatorData16.getCurrentDay();
    java.lang.String str19 = simulatorData16.getUTCCurrentTime2();
    java.lang.String str20 = simulatorData16.getCurrentDay();
    java.util.Date date21 = simulatorData16.getCurrentTime();
    int int22 = opssat.simulator.util.DateExtraction.getMinuteFromDate(date21);
    simulatorHeader12.setEndDate(date21);
    boolean boolean24 = simulatorHeader12.isAutoStartTime();
    java.lang.String str25 = simulatorHeader12.getKeplerElements();
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertTrue("'" + long17 + "' != '" + 0L + "'", long17 == 0L);
    org.junit.Assert.assertNotNull(date21);
    org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + false + "'", !boolean24);
    org.junit.Assert.assertNull(str25);
  }

  @Test
  public void test1071() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1071");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getServiceNumber();
    org.ccsds.moims.mo.mal.structures.OctetList octetList3 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int4 = octetList3.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort5 = octetList3.getAreaNumber();
    java.lang.Object[] objArray6 = octetList3.toArray();
    org.ccsds.moims.mo.mal.structures.OctetList octetList7 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int8 = octetList7.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort9 = octetList7.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor10 = octetList7.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor12 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList7, "hi!");
    org.ccsds.moims.mo.mal.structures.UShort uShort13 = octetList7.getAreaNumber();
    java.lang.Boolean[] booleanArray16 = new java.lang.Boolean[]{true, true};
    java.util.ArrayList<java.lang.Boolean> booleanList17 =
        new java.util.ArrayList<java.lang.Boolean>();
    boolean boolean18 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Boolean>) booleanList17, booleanArray16);
    int int20 = booleanList17.indexOf(10);
    java.util.Iterator<java.lang.Boolean> booleanItor21 = booleanList17.iterator();
    java.util.Spliterator<java.lang.Boolean> booleanSpliterator22 = booleanList17.spliterator();
    org.ccsds.moims.mo.mal.structures.OctetList octetList23 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int24 = octetList23.getTypeShortForm();
    java.lang.Object obj25 = octetList23.clone();
    octetList23.trimToSize();
    boolean boolean27 = booleanList17.contains(octetList23);
    org.ccsds.moims.mo.mal.structures.OctetList octetList28 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int29 = octetList28.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort30 = octetList28.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.OctetList octetList31 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int32 = octetList31.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort33 = octetList31.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet34 = octetList31.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.OctetList octetList35 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int36 = octetList35.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort37 = octetList35.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.OctetList[] octetListArray38 =
        new org.ccsds.moims.mo.mal.structures.OctetList[]{
          octetList3, octetList7, octetList23, octetList28, octetList31, octetList35};
    org.ccsds.moims.mo.mal.structures.OctetList[] octetListArray39 = octetList0
        .toArray(octetListArray38);
    boolean boolean40 = octetList0.isEmpty();
    java.lang.Long long41 = octetList0.getShortForm();
    org.ccsds.moims.mo.mal.structures.OctetList octetList42 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int43 = octetList42.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort44 = octetList42.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet45 = octetList42.getAreaVersion();
    opssat.simulator.util.wav.WavFileException wavFileException47 =
        new opssat.simulator.util.wav.WavFileException(
            "UnknownGUIData");
    java.lang.Throwable[] throwableArray48 = wavFileException47.getSuppressed();
    boolean boolean49 = octetList42.equals(wavFileException47);
    org.ccsds.moims.mo.mal.structures.FloatList floatList51 =
        new org.ccsds.moims.mo.mal.structures.FloatList(
            (byte) 0);
    java.lang.Integer int52 = floatList51.getTypeShortForm();
    java.lang.Integer int53 = floatList51.getTypeShortForm();
    floatList51.clear();
    boolean boolean55 = octetList42.remove(floatList51);
    boolean boolean57 = floatList51.add((-1.0f));
    floatList51.trimToSize();
    floatList51.ensureCapacity((-1));
    org.ccsds.moims.mo.mal.structures.FloatList floatList62 =
        new org.ccsds.moims.mo.mal.structures.FloatList(
            (byte) 0);
    floatList62.trimToSize();
    floatList62.clear();
    org.ccsds.moims.mo.mal.structures.FloatList floatList66 =
        new org.ccsds.moims.mo.mal.structures.FloatList(
            (byte) 0);
    java.lang.Integer int67 = floatList66.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet68 = floatList66.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.FloatList floatList70 =
        new org.ccsds.moims.mo.mal.structures.FloatList(
            (byte) 0);
    java.lang.Integer int71 = floatList70.getTypeShortForm();
    floatList70.trimToSize();
    org.ccsds.moims.mo.mal.structures.StringList stringList74 =
        new org.ccsds.moims.mo.mal.structures.StringList(
            36);
    java.lang.Byte[] byteArray79 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList80 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean81 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList80, byteArray79);
    java.lang.Integer[] intArray84 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList85 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean86 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList85, intArray84);
    boolean boolean87 = byteList80.retainAll(intList85);
    int int88 = stringList74.indexOf(intList85);
    boolean boolean90 = intList85.add(24);
    boolean boolean91 = floatList70.retainAll(intList85);
    java.lang.Object obj92 = null;
    boolean boolean93 = floatList70.contains(obj92);
    org.ccsds.moims.mo.mal.structures.FloatList[] floatListArray94 =
        new org.ccsds.moims.mo.mal.structures.FloatList[]{
          floatList51, floatList62, floatList66, floatList70};
    org.ccsds.moims.mo.mal.structures.FloatList[] floatListArray95 = octetList0
        .toArray(floatListArray94);
    org.ccsds.moims.mo.mal.structures.UOctet uOctet96 = octetList0.getAreaVersion();
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertTrue("'" + int4 + "' != '" + (-7) + "'", int4.equals((-7)));
    org.junit.Assert.assertNotNull(uShort5);
    org.junit.Assert.assertNotNull(objArray6);
    org.junit.Assert.assertTrue("'" + int8 + "' != '" + (-7) + "'", int8.equals((-7)));
    org.junit.Assert.assertNotNull(uShort9);
    org.junit.Assert.assertNotNull(byteItor10);
    org.junit.Assert.assertNotNull(uShort13);
    org.junit.Assert.assertNotNull(booleanArray16);
    org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + true + "'", boolean18);
    org.junit.Assert.assertTrue("'" + int20 + "' != '" + (-1) + "'", int20 == (-1));
    org.junit.Assert.assertNotNull(booleanItor21);
    org.junit.Assert.assertNotNull(booleanSpliterator22);
    org.junit.Assert.assertTrue("'" + int24 + "' != '" + (-7) + "'", int24.equals((-7)));
    org.junit.Assert.assertNotNull(obj25);
    org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + false + "'", !boolean27);
    org.junit.Assert.assertTrue("'" + int29 + "' != '" + (-7) + "'", int29.equals((-7)));
    org.junit.Assert.assertNotNull(uShort30);
    org.junit.Assert.assertTrue("'" + int32 + "' != '" + (-7) + "'", int32.equals((-7)));
    org.junit.Assert.assertNotNull(uShort33);
    org.junit.Assert.assertNotNull(uOctet34);
    org.junit.Assert.assertTrue("'" + int36 + "' != '" + (-7) + "'", int36.equals((-7)));
    org.junit.Assert.assertNotNull(uShort37);
    org.junit.Assert.assertNotNull(octetListArray38);
    org.junit.Assert.assertNotNull(octetListArray39);
    org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + true + "'", boolean40);
    org.junit.Assert.assertTrue("'" + long41 + "' != '" + 281475010265081L + "'",
        long41.equals(281475010265081L));
    org.junit.Assert.assertTrue("'" + int43 + "' != '" + (-7) + "'", int43.equals((-7)));
    org.junit.Assert.assertNotNull(uShort44);
    org.junit.Assert.assertNotNull(uOctet45);
    org.junit.Assert.assertNotNull(throwableArray48);
    org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + false + "'", !boolean49);
    org.junit.Assert.assertTrue("'" + int52 + "' != '" + (-4) + "'", int52.equals((-4)));
    org.junit.Assert.assertTrue("'" + int53 + "' != '" + (-4) + "'", int53.equals((-4)));
    org.junit.Assert.assertTrue("'" + boolean55 + "' != '" + false + "'", !boolean55);
    org.junit.Assert.assertTrue("'" + boolean57 + "' != '" + true + "'", boolean57);
    org.junit.Assert.assertTrue("'" + int67 + "' != '" + (-4) + "'", int67.equals((-4)));
    org.junit.Assert.assertNotNull(uOctet68);
    org.junit.Assert.assertTrue("'" + int71 + "' != '" + (-4) + "'", int71.equals((-4)));
    org.junit.Assert.assertNotNull(byteArray79);
    org.junit.Assert.assertTrue("'" + boolean81 + "' != '" + true + "'", boolean81);
    org.junit.Assert.assertNotNull(intArray84);
    org.junit.Assert.assertTrue("'" + boolean86 + "' != '" + true + "'", boolean86);
    org.junit.Assert.assertTrue("'" + boolean87 + "' != '" + true + "'", boolean87);
    org.junit.Assert.assertTrue("'" + int88 + "' != '" + (-1) + "'", int88 == (-1));
    org.junit.Assert.assertTrue("'" + boolean90 + "' != '" + true + "'", boolean90);
    org.junit.Assert.assertTrue("'" + boolean91 + "' != '" + false + "'", !boolean91);
    org.junit.Assert.assertTrue("'" + boolean93 + "' != '" + false + "'", !boolean93);
    org.junit.Assert.assertNotNull(floatListArray94);
    org.junit.Assert.assertNotNull(floatListArray95);
    org.junit.Assert.assertNotNull(uOctet96);
  }

  @Test
  public void test1072() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1072");
    }
    org.ccsds.moims.mo.mal.structures.URIList uRIList1 =
        new org.ccsds.moims.mo.mal.structures.URIList(
            11);
    org.ccsds.moims.mo.mal.structures.Element element2 = uRIList1.createElement();
    java.lang.Float[] floatArray6 = new java.lang.Float[]{1.0f, 1.0f, (-1.0f)};
    java.util.ArrayList<java.lang.Float> floatList7 = new java.util.ArrayList<java.lang.Float>();
    boolean boolean8 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Float>) floatList7, floatArray6);
    boolean boolean10 = floatList7.add((-1.0f));
    floatList7.clear();
    java.util.stream.Stream<java.lang.Float> floatStream12 = floatList7.stream();
    java.lang.Byte[] byteArray17 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList18 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean19 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList18, byteArray17);
    java.lang.Integer[] intArray22 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList23 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean24 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList23, intArray22);
    boolean boolean25 = byteList18.retainAll(intList23);
    java.lang.Integer[] intArray28 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList29 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean30 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList29, intArray28);
    int int32 = intList29.lastIndexOf((byte) 10);
    boolean boolean33 = intList23.retainAll(intList29);
    boolean boolean34 = floatList7.containsAll(intList29);
    java.util.Spliterator<java.lang.Integer> intSpliterator35 = intList29.spliterator();
    java.util.stream.Stream<java.lang.Integer> intStream36 = intList29.parallelStream();
    org.ccsds.moims.mo.mal.structures.LongList longList37 =
        new org.ccsds.moims.mo.mal.structures.LongList();
    java.lang.Integer int38 = longList37.getTypeShortForm();
    boolean boolean39 = intList29.equals(int38);
    boolean boolean40 = uRIList1.removeAll(intList29);
    java.lang.String str41 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(intList29);
    org.junit.Assert.assertNotNull(element2);
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + true + "'", boolean8);
    org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
    org.junit.Assert.assertNotNull(floatStream12);
    org.junit.Assert.assertNotNull(byteArray17);
    org.junit.Assert.assertTrue("'" + boolean19 + "' != '" + true + "'", boolean19);
    org.junit.Assert.assertNotNull(intArray22);
    org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + true + "'", boolean24);
    org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + true + "'", boolean25);
    org.junit.Assert.assertNotNull(intArray28);
    org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + true + "'", boolean30);
    org.junit.Assert.assertTrue("'" + int32 + "' != '" + (-1) + "'", int32 == (-1));
    org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + true + "'", boolean33);
    org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + false + "'", !boolean34);
    org.junit.Assert.assertNotNull(intSpliterator35);
    org.junit.Assert.assertNotNull(intStream36);
    org.junit.Assert.assertTrue("'" + int38 + "' != '" + (-13) + "'", int38.equals((-13)));
    org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + false + "'", !boolean39);
    org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + false + "'", !boolean40);
    org.junit.Assert.assertTrue("'" + str41 + "' != '" + "UnknownGUIData" + "'",
        str41.equals("UnknownGUIData"));
  }

  @Test
  public void test1073() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1073");
    }
    opssat.simulator.util.SimulatorData simulatorData1 = new opssat.simulator.util.SimulatorData(
        (-18));
    simulatorData1.setCounter((-1));
    simulatorData1.feedTimeElapsed(21);
    java.lang.String str6 = simulatorData1.getCurrentDay();
    boolean boolean7 = simulatorData1.isTimeRunning();
    int int8 = simulatorData1.getTimeFactor();
    simulatorData1.incrementMethods();
    simulatorData1.setMethodsExecuted('#');
    org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
    org.junit.Assert.assertTrue("'" + int8 + "' != '" + 1 + "'", int8 == 1);
  }

  @Test
  public void test1075() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1075");
    }
    org.ccsds.moims.mo.mal.structures.StringList stringList1 =
        new org.ccsds.moims.mo.mal.structures.StringList(
            36);
    org.ccsds.moims.mo.mal.structures.StringList stringList2 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    org.ccsds.moims.mo.mal.structures.ShortList shortList4 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            ' ');
    java.util.ListIterator<java.lang.Short> shortItor5 = shortList4.listIterator();
    boolean boolean6 = stringList2.equals(shortList4);
    org.ccsds.moims.mo.mal.structures.UShort uShort7 = shortList4.getAreaNumber();
    java.lang.Integer int8 = uShort7.getTypeShortForm();
    int int9 = stringList1.indexOf(int8);
    int int10 = stringList1.size();
    org.ccsds.moims.mo.mal.structures.UShort uShort11 = stringList1.getServiceNumber();
    java.util.stream.Stream<java.lang.String> strStream12 = stringList1.stream();
    opssat.simulator.util.SimulatorData simulatorData14 = new opssat.simulator.util.SimulatorData(
        (-18));
    simulatorData14.setCounter((-1));
    simulatorData14.feedTimeElapsed(21);
    java.lang.String str19 = simulatorData14.getCurrentDay();
    java.lang.String str20 = simulatorData14.getUTCCurrentTime();
    boolean boolean21 = stringList1.contains(simulatorData14);
    org.junit.Assert.assertNotNull(shortItor5);
    org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + true + "'", boolean6);
    org.junit.Assert.assertNotNull(uShort7);
    org.junit.Assert.assertTrue("'" + int8 + "' != '" + 10 + "'", int8.equals(10));
    org.junit.Assert.assertTrue("'" + int9 + "' != '" + (-1) + "'", int9 == (-1));
    org.junit.Assert.assertTrue("'" + int10 + "' != '" + 0 + "'", int10 == 0);
    org.junit.Assert.assertNotNull(uShort11);
    org.junit.Assert.assertNotNull(strStream12);
    org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
  }

  @Test
  public void test1077() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1077");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessSingleStreamOperatingBuffer endlessSingleStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessSingleStreamOperatingBuffer(
            logger0);
    byte[] byteArray3 = endlessSingleStreamOperatingBuffer1.getDataAsByteArray('4');
    byte[] byteArray5 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS.int16_2ByteArray(15);
    endlessSingleStreamOperatingBuffer1.setDataFromByteArray(byteArray5);
    java.lang.String str7 = endlessSingleStreamOperatingBuffer1.getDataBufferAsString();
    java.lang.Boolean[] booleanArray10 = new java.lang.Boolean[]{true, true};
    java.util.ArrayList<java.lang.Boolean> booleanList11 =
        new java.util.ArrayList<java.lang.Boolean>();
    boolean boolean12 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Boolean>) booleanList11, booleanArray10);
    int int14 = booleanList11.indexOf(10);
    int int15 = booleanList11.size();
    java.lang.Byte[] byteArray19 = new java.lang.Byte[]{(byte) 10, (byte) 10, (byte) -1};
    java.util.ArrayList<java.lang.Byte> byteList20 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean21 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList20, byteArray19);
    java.lang.Object[] objArray22 = byteList20.toArray();
    java.util.Iterator<java.lang.Byte> byteItor23 = byteList20.iterator();
    boolean boolean24 = booleanList11.contains(byteList20);
    org.ccsds.moims.mo.mal.structures.UShortList uShortList25 =
        new org.ccsds.moims.mo.mal.structures.UShortList();
    java.lang.Boolean[] booleanArray28 = new java.lang.Boolean[]{true, true};
    java.util.ArrayList<java.lang.Boolean> booleanList29 =
        new java.util.ArrayList<java.lang.Boolean>();
    boolean boolean30 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Boolean>) booleanList29, booleanArray28);
    int int32 = booleanList29.indexOf(10);
    int int33 = booleanList29.size();
    java.lang.Byte[] byteArray37 = new java.lang.Byte[]{(byte) 10, (byte) 10, (byte) -1};
    java.util.ArrayList<java.lang.Byte> byteList38 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean39 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList38, byteArray37);
    java.lang.Object[] objArray40 = byteList38.toArray();
    java.util.Iterator<java.lang.Byte> byteItor41 = byteList38.iterator();
    boolean boolean42 = booleanList29.contains(byteList38);
    java.util.stream.Stream<java.lang.Boolean> booleanStream43 = booleanList29.stream();
    int int44 = uShortList25.lastIndexOf(booleanList29);
    int int45 = byteList20.lastIndexOf(booleanList29);
    java.lang.Float[] floatArray49 = new java.lang.Float[]{1.0f, 1.0f, (-1.0f)};
    java.util.ArrayList<java.lang.Float> floatList50 = new java.util.ArrayList<java.lang.Float>();
    boolean boolean51 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Float>) floatList50, floatArray49);
    boolean boolean53 = floatList50.add((-1.0f));
    floatList50.trimToSize();
    org.ccsds.moims.mo.mal.structures.OctetList octetList55 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    octetList55.ensureCapacity(13);
    java.lang.Byte[] byteArray62 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList63 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean64 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList63, byteArray62);
    java.lang.Integer[] intArray67 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList68 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean69 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList68, intArray67);
    boolean boolean70 = byteList63.retainAll(intList68);
    boolean boolean71 = octetList55.containsAll(intList68);
    boolean boolean72 = floatList50.containsAll(intList68);
    boolean boolean73 = booleanList29.removeAll(intList68);
    java.lang.Boolean boolean75 = booleanList29.remove(0);
    endlessSingleStreamOperatingBuffer1.setDataBuffer(booleanList29);
    byte[] byteArray78 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS.int2ByteArray(4);
    endlessSingleStreamOperatingBuffer1.setDataFromByteArray(byteArray78);
    org.ccsds.moims.mo.mal.structures.Blob blob82 = new org.ccsds.moims.mo.mal.structures.Blob(
        byteArray78, (byte) 0, 19);
    org.junit.Assert.assertNotNull(byteArray3);
    org.junit.Assert.assertNotNull(byteArray5);
    org.junit.Assert.assertTrue("'" + str7 + "' != '" + "byte[] {0x00,0x0F}" + "'",
        str7.equals("byte[] {0x00,0x0F}"));
    org.junit.Assert.assertNotNull(booleanArray10);
    org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + true + "'", boolean12);
    org.junit.Assert.assertTrue("'" + int14 + "' != '" + (-1) + "'", int14 == (-1));
    org.junit.Assert.assertTrue("'" + int15 + "' != '" + 2 + "'", int15 == 2);
    org.junit.Assert.assertNotNull(byteArray19);
    org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + true + "'", boolean21);
    org.junit.Assert.assertNotNull(objArray22);
    org.junit.Assert.assertNotNull(byteItor23);
    org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + false + "'", !boolean24);
    org.junit.Assert.assertNotNull(booleanArray28);
    org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + true + "'", boolean30);
    org.junit.Assert.assertTrue("'" + int32 + "' != '" + (-1) + "'", int32 == (-1));
    org.junit.Assert.assertTrue("'" + int33 + "' != '" + 2 + "'", int33 == 2);
    org.junit.Assert.assertNotNull(byteArray37);
    org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + true + "'", boolean39);
    org.junit.Assert.assertNotNull(objArray40);
    org.junit.Assert.assertNotNull(byteItor41);
    org.junit.Assert.assertTrue("'" + boolean42 + "' != '" + false + "'", !boolean42);
    org.junit.Assert.assertNotNull(booleanStream43);
    org.junit.Assert.assertTrue("'" + int44 + "' != '" + (-1) + "'", int44 == (-1));
    org.junit.Assert.assertTrue("'" + int45 + "' != '" + (-1) + "'", int45 == (-1));
    org.junit.Assert.assertNotNull(floatArray49);
    org.junit.Assert.assertTrue("'" + boolean51 + "' != '" + true + "'", boolean51);
    org.junit.Assert.assertTrue("'" + boolean53 + "' != '" + true + "'", boolean53);
    org.junit.Assert.assertNotNull(byteArray62);
    org.junit.Assert.assertTrue("'" + boolean64 + "' != '" + true + "'", boolean64);
    org.junit.Assert.assertNotNull(intArray67);
    org.junit.Assert.assertTrue("'" + boolean69 + "' != '" + true + "'", boolean69);
    org.junit.Assert.assertTrue("'" + boolean70 + "' != '" + true + "'", boolean70);
    org.junit.Assert.assertTrue("'" + boolean71 + "' != '" + false + "'", !boolean71);
    org.junit.Assert.assertTrue("'" + boolean72 + "' != '" + false + "'", !boolean72);
    org.junit.Assert.assertTrue("'" + boolean73 + "' != '" + false + "'", !boolean73);
    org.junit.Assert.assertTrue("'" + boolean75 + "' != '" + true + "'", boolean75.equals(true));
    org.junit.Assert.assertNotNull(byteArray78);
  }

  @Test
  public void test1078() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1078");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double4 = simulatorSpacecraftState3.getLatitude();
    java.lang.String str5 = simulatorSpacecraftState3.getMagField();
    java.lang.String str6 = simulatorSpacecraftState3.toString();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState10 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double11 = simulatorSpacecraftState10.getLatitude();
    java.lang.String str12 = simulatorSpacecraftState10.getMagField();
    java.lang.String str13 = simulatorSpacecraftState10.toString();
    double[] doubleArray14 = simulatorSpacecraftState10.getSunVector();
    simulatorSpacecraftState3.setMagnetometer(doubleArray14);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState19 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double20 = simulatorSpacecraftState19.getLatitude();
    java.lang.String str21 = simulatorSpacecraftState19.getMagField();
    java.lang.String str22 = simulatorSpacecraftState19.toString();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState26 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double27 = simulatorSpacecraftState26.getLatitude();
    java.lang.String str28 = simulatorSpacecraftState26.getMagField();
    java.lang.String str29 = simulatorSpacecraftState26.toString();
    double[] doubleArray30 = simulatorSpacecraftState26.getSunVector();
    simulatorSpacecraftState19.setMagnetometer(doubleArray30);
    simulatorSpacecraftState3.setMagnetometer(doubleArray30);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState36 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState40 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double41 = simulatorSpacecraftState40.getLatitude();
    java.lang.String str42 = simulatorSpacecraftState40.getMagField();
    java.lang.String str43 = simulatorSpacecraftState40.toString();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState47 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double48 = simulatorSpacecraftState47.getLatitude();
    java.lang.String str49 = simulatorSpacecraftState47.getMagField();
    java.lang.String str50 = simulatorSpacecraftState47.toString();
    double[] doubleArray51 = simulatorSpacecraftState47.getSunVector();
    simulatorSpacecraftState40.setMagnetometer(doubleArray51);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState56 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double57 = simulatorSpacecraftState56.getLatitude();
    java.lang.String str58 = simulatorSpacecraftState56.getMagField();
    java.lang.String str59 = simulatorSpacecraftState56.toString();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState63 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double64 = simulatorSpacecraftState63.getLatitude();
    java.lang.String str65 = simulatorSpacecraftState63.getMagField();
    java.lang.String str66 = simulatorSpacecraftState63.toString();
    double[] doubleArray67 = simulatorSpacecraftState63.getSunVector();
    simulatorSpacecraftState56.setMagnetometer(doubleArray67);
    simulatorSpacecraftState40.setMagnetometer(doubleArray67);
    simulatorSpacecraftState36.setMagField(doubleArray67);
    simulatorSpacecraftState3.setSunVector(doubleArray67);
    org.junit.Assert.assertTrue("'" + double4 + "' != '" + 340.0d + "'", double4 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str5 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str5.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str6 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str6.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertTrue("'" + double11 + "' != '" + 340.0d + "'", double11 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str12 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str12.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str13 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str13.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray14);
    org.junit.Assert.assertTrue("'" + double20 + "' != '" + 340.0d + "'", double20 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str21 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str21.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str22 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str22.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertTrue("'" + double27 + "' != '" + 340.0d + "'", double27 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str28 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str28.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str29 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str29.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray30);
    org.junit.Assert.assertTrue("'" + double41 + "' != '" + 340.0d + "'", double41 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str42 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str42.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str43 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str43.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertTrue("'" + double48 + "' != '" + 340.0d + "'", double48 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str49 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str49.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str50 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str50.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray51);
    org.junit.Assert.assertTrue("'" + double57 + "' != '" + 340.0d + "'", double57 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str58 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str58.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str59 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str59.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertTrue("'" + double64 + "' != '" + 340.0d + "'", double64 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str65 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str65.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str66 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str66.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray67);
  }

  @Test
  public void test1079() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1079");
    }
    opssat.simulator.util.SimulatorData simulatorData3 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date4 = simulatorData3.getCurrentTime();
    int int5 = opssat.simulator.util.DateExtraction.getDayFromDate(date4);
    opssat.simulator.util.SimulatorData simulatorData7 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date8 = simulatorData7.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData10 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date11 = simulatorData10.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap12 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date8, date11);
    opssat.simulator.util.SimulatorHeader simulatorHeader13 =
        new opssat.simulator.util.SimulatorHeader(
            false, date4, date11);
    java.util.Date date14 = simulatorHeader13.getEndDate();
    int int15 = simulatorHeader13.getMinuteStartDate();
    opssat.simulator.util.SimulatorData simulatorData17 = new opssat.simulator.util.SimulatorData(
        (-18));
    long long18 = simulatorData17.getCurrentTimeMillis();
    java.lang.String str19 = simulatorData17.getCurrentDay();
    java.lang.String str20 = simulatorData17.getUTCCurrentTime2();
    java.lang.String str21 = simulatorData17.getCurrentDay();
    java.util.Date date22 = simulatorData17.getCurrentTime();
    int int23 = opssat.simulator.util.DateExtraction.getMinuteFromDate(date22);
    simulatorHeader13.setEndDate(date22);
    opssat.simulator.util.SimulatorData simulatorData28 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date29 = simulatorData28.getCurrentTime();
    java.util.Date date30 = simulatorData28.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData31 = new opssat.simulator.util.SimulatorData(
        17, date30);
    opssat.simulator.util.SimulatorData simulatorData34 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date35 = simulatorData34.getCurrentTime();
    int int36 = opssat.simulator.util.DateExtraction.getDayFromDate(date35);
    opssat.simulator.util.SimulatorData simulatorData38 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date39 = simulatorData38.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData41 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date42 = simulatorData41.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap43 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date39, date42);
    opssat.simulator.util.SimulatorHeader simulatorHeader44 =
        new opssat.simulator.util.SimulatorHeader(
            false, date35, date42);
    opssat.simulator.util.SimulatorHeader simulatorHeader45 =
        new opssat.simulator.util.SimulatorHeader(
            false, date30, date35);
    int int46 = opssat.simulator.util.DateExtraction.getYearFromDate(date30);
    opssat.simulator.util.SimulatorHeader simulatorHeader47 =
        new opssat.simulator.util.SimulatorHeader(
            true, date22, date30);
    org.junit.Assert.assertNotNull(date4);
    org.junit.Assert.assertNotNull(date8);
    org.junit.Assert.assertNotNull(date11);
    org.junit.Assert.assertNotNull(timeUnitMap12);
    org.junit.Assert.assertNotNull(date14);
    org.junit.Assert.assertTrue("'" + long18 + "' != '" + 0L + "'", long18 == 0L);
    org.junit.Assert.assertNotNull(date22);
    org.junit.Assert.assertNotNull(date29);
    org.junit.Assert.assertNotNull(date30);
    org.junit.Assert.assertNotNull(date35);
    org.junit.Assert.assertNotNull(date39);
    org.junit.Assert.assertNotNull(date42);
    org.junit.Assert.assertNotNull(timeUnitMap43);
  }

  @Test
  public void test1080() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1080");
    }
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience8 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    double double9 = gPSSatInViewScience8.getMaxDistance();
    double double10 = gPSSatInViewScience8.getMaxElevation();
    double double11 = gPSSatInViewScience8.getStdDevDistance();
    double double12 = gPSSatInViewScience8.getMaxDistance();
    org.junit.Assert.assertTrue("'" + double9 + "' != '" + 0.0d + "'", double9 == 0.0d);
    org.junit.Assert.assertTrue("'" + double10 + "' != '" + 56.0d + "'", double10 == 56.0d);
    org.junit.Assert.assertTrue("'" + double11 + "' != '" + 58.0d + "'", double11 == 58.0d);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + 0.0d + "'", double12 == 0.0d);
  }

  @Test
  public void test1082() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1082");
    }
    org.ccsds.moims.mo.mal.structures.StringList stringList0 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    org.ccsds.moims.mo.mal.structures.ShortList shortList2 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            ' ');
    java.util.ListIterator<java.lang.Short> shortItor3 = shortList2.listIterator();
    boolean boolean4 = stringList0.equals(shortList2);
    java.util.stream.BaseStream[] baseStreamArray6 = new java.util.stream.BaseStream[0];
    @SuppressWarnings("unchecked")
    java.util.stream.BaseStream<java.lang.Float, java.util.stream.Stream<java.lang.Float>>[] floatBaseStreamArray7 =
        baseStreamArray6;
    java.util.stream.BaseStream<java.lang.Float, java.util.stream.Stream<java.lang.Float>>[] floatBaseStreamArray8 =
        stringList0
            .toArray(
                (java.util.stream.BaseStream<java.lang.Float, java.util.stream.Stream<java.lang.Float>>[]) baseStreamArray6);
    java.util.stream.Stream<java.lang.String> strStream9 = stringList0.stream();
    java.lang.Object obj10 = stringList0.clone();
    opssat.simulator.util.LoggerFormatter loggerFormatter11 =
        new opssat.simulator.util.LoggerFormatter();
    opssat.simulator.util.LoggerFormatter loggerFormatter12 =
        new opssat.simulator.util.LoggerFormatter();
    opssat.simulator.util.LoggerFormatter loggerFormatter13 =
        new opssat.simulator.util.LoggerFormatter();
    opssat.simulator.util.LoggerFormatter[] loggerFormatterArray14 =
        new opssat.simulator.util.LoggerFormatter[]{
          loggerFormatter11, loggerFormatter12, loggerFormatter13};
    opssat.simulator.util.LoggerFormatter[] loggerFormatterArray15 = stringList0
        .toArray(loggerFormatterArray14);
    java.util.stream.Stream<java.lang.String> strStream16 = stringList0.parallelStream();
    org.ccsds.moims.mo.mal.structures.UShort uShort17 = stringList0.getServiceNumber();
    stringList0.trimToSize();
    try {
      java.util.ListIterator<java.lang.String> strItor20 = stringList0.listIterator((short) 10);
      org.junit.Assert.fail(
          "Expected exception of type java.lang.IndexOutOfBoundsException; message: Index: 10");
    } catch (java.lang.IndexOutOfBoundsException e) {
    }
    org.junit.Assert.assertNotNull(shortItor3);
    org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
    org.junit.Assert.assertNotNull(baseStreamArray6);
    org.junit.Assert.assertNotNull(floatBaseStreamArray7);
    org.junit.Assert.assertNotNull(floatBaseStreamArray8);
    org.junit.Assert.assertNotNull(strStream9);
    org.junit.Assert.assertNotNull(obj10);
    org.junit.Assert.assertNotNull(loggerFormatterArray14);
    org.junit.Assert.assertNotNull(loggerFormatterArray15);
    org.junit.Assert.assertNotNull(strStream16);
    org.junit.Assert.assertNotNull(uShort17);
  }

  @Test
  public void test1083() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1083");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    simulatorHeader12.setOrekitTLE1("");
    java.util.Date date17 = simulatorHeader12.parseStringIntoDate("2019/05/23-15:09:35");
    boolean boolean18 = simulatorHeader12.isUseOrekitPropagator();
    java.lang.String str19 = simulatorHeader12.getStartDateString();
    int int20 = simulatorHeader12.getHourStartDate();
    int int21 = simulatorHeader12.getCelestiaPort();
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertNull(date17);
    org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + false + "'", !boolean18);
    org.junit.Assert.assertTrue("'" + int21 + "' != '" + 0 + "'", int21 == 0);
  }

  @Test
  public void test1084() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1084");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    opssat.simulator.util.SimulatorData simulatorData17 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date18 = simulatorData17.getCurrentTime();
    java.util.Date date19 = simulatorData17.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData20 = new opssat.simulator.util.SimulatorData(
        17, date19);
    opssat.simulator.util.SimulatorData simulatorData23 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date24 = simulatorData23.getCurrentTime();
    int int25 = opssat.simulator.util.DateExtraction.getDayFromDate(date24);
    opssat.simulator.util.SimulatorData simulatorData27 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date28 = simulatorData27.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData30 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date31 = simulatorData30.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap32 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date28, date31);
    opssat.simulator.util.SimulatorHeader simulatorHeader33 =
        new opssat.simulator.util.SimulatorHeader(
            false, date24, date31);
    opssat.simulator.util.SimulatorHeader simulatorHeader34 =
        new opssat.simulator.util.SimulatorHeader(
            false, date19, date24);
    simulatorHeader12.setEndDate(date19);
    int int36 = simulatorHeader12.getHourStartDate();
    simulatorHeader12.setUseOrekitPropagator(false);
    boolean boolean39 = simulatorHeader12.isUseOrekitPropagator();
    int int40 = simulatorHeader12.getMonthStartDate();
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertNotNull(date18);
    org.junit.Assert.assertNotNull(date19);
    org.junit.Assert.assertNotNull(date24);
    org.junit.Assert.assertNotNull(date28);
    org.junit.Assert.assertNotNull(date31);
    org.junit.Assert.assertNotNull(timeUnitMap32);
    org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + false + "'", !boolean39);
  }

  @Test
  public void test1085() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1085");
    }
    try {
      opssat.simulator.orekit.GPSSatInView gPSSatInView2 = new opssat.simulator.orekit.GPSSatInView(
          "yyyy:MM:dd HH:mm:ss z", 281474993487884L);
      org.junit.Assert.fail(
          "Expected exception of type java.lang.StringIndexOutOfBoundsException; message: String index out of range: -1");
    } catch (java.lang.StringIndexOutOfBoundsException e) {
    }
  }

  @Test
  public void test1086() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1086");
    }
    opssat.simulator.util.SimulatorSchedulerPiece simulatorSchedulerPiece3 =
        new opssat.simulator.util.SimulatorSchedulerPiece(
            13L, 0, "hi!");
    long long4 = simulatorSchedulerPiece3.getTime();
    int int5 = simulatorSchedulerPiece3.getInternalID();
    simulatorSchedulerPiece3.setExecuted(false);
    simulatorSchedulerPiece3.setExecuted(false);
    java.lang.String str10 = simulatorSchedulerPiece3.getSchedulerOutput();
    java.lang.String str11 = simulatorSchedulerPiece3.getSchedulerOutput();
    org.junit.Assert.assertTrue("'" + long4 + "' != '" + 13L + "'", long4 == 13L);
    org.junit.Assert.assertTrue("'" + int5 + "' != '" + 0 + "'", int5 == 0);
    org.junit.Assert.assertTrue(
        "'" + str10 + "' != '" + "00000:00:00:00:013  0     hi!          executed false   | " + "'",
        str10.equals("00000:00:00:00:013  0     hi!          executed false   | "));
    org.junit.Assert.assertTrue(
        "'" + str11 + "' != '" + "00000:00:00:00:013  0     hi!          executed false   | " + "'",
        str11.equals("00000:00:00:00:013  0     hi!          executed false   | "));
  }

  @Test
  public void test1087() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1087");
    }
    opssat.simulator.util.ArgumentTemplate argumentTemplate2 =
        new opssat.simulator.util.ArgumentTemplate(
            "*36", "2019:05:23 15:10:18 UTC");
  }

  @Test
  public void test1088() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1088");
    }
    org.ccsds.moims.mo.mal.structures.UInteger uInteger1 =
        new org.ccsds.moims.mo.mal.structures.UInteger(
            13);
    org.ccsds.moims.mo.mal.structures.Element element2 = uInteger1.createElement();
    long long3 = uInteger1.getValue();
    org.ccsds.moims.mo.mal.structures.UShort uShort4 = uInteger1.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.Element element5 = uInteger1.createElement();
    java.lang.String str6 = uInteger1.toString();
    org.ccsds.moims.mo.mal.structures.UShort uShort7 = uInteger1.getServiceNumber();
    org.junit.Assert.assertNotNull(element2);
    org.junit.Assert.assertTrue("'" + long3 + "' != '" + 13L + "'", long3 == 13L);
    org.junit.Assert.assertNotNull(uShort4);
    org.junit.Assert.assertNotNull(element5);
    org.junit.Assert.assertTrue("'" + str6 + "' != '" + "13" + "'", str6.equals("13"));
    org.junit.Assert.assertNotNull(uShort7);
  }

  @Test
  public void test1089() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1089");
    }
    opssat.simulator.util.SimulatorSchedulerPiece simulatorSchedulerPiece3 =
        new opssat.simulator.util.SimulatorSchedulerPiece(
            'a', 11, "");
    int int4 = simulatorSchedulerPiece3.getInternalID();
    long long5 = simulatorSchedulerPiece3.getTime();
    org.junit.Assert.assertTrue("'" + int4 + "' != '" + 11 + "'", int4 == 11);
    org.junit.Assert.assertTrue("'" + long5 + "' != '" + 97L + "'", long5 == 97L);
  }

  @Test
  public void test1090() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1090");
    }
    org.ccsds.moims.mo.mal.structures.StringList stringList0 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    org.ccsds.moims.mo.mal.structures.ShortList shortList2 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            ' ');
    java.util.ListIterator<java.lang.Short> shortItor3 = shortList2.listIterator();
    boolean boolean4 = stringList0.equals(shortList2);
    java.lang.Integer int5 = stringList0.getTypeShortForm();
    int int6 = stringList0.size();
    org.ccsds.moims.mo.mal.structures.StringList stringList7 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    org.ccsds.moims.mo.mal.structures.ShortList shortList9 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            ' ');
    java.util.ListIterator<java.lang.Short> shortItor10 = shortList9.listIterator();
    boolean boolean11 = stringList7.equals(shortList9);
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience20 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    double double21 = gPSSatInViewScience20.getMaxDistance();
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience30 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience39 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    double double40 = gPSSatInViewScience39.getMaxDistance();
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience49 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience58 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    double double59 = gPSSatInViewScience58.getStdDevElevation();
    opssat.simulator.orekit.GPSSatInViewScience[] gPSSatInViewScienceArray60 =
        new opssat.simulator.orekit.GPSSatInViewScience[]{
          gPSSatInViewScience20, gPSSatInViewScience30, gPSSatInViewScience39, gPSSatInViewScience49,
          gPSSatInViewScience58};
    opssat.simulator.orekit.GPSSatInViewScience[] gPSSatInViewScienceArray61 = shortList9
        .toArray(gPSSatInViewScienceArray60);
    int int62 = stringList0.indexOf(gPSSatInViewScienceArray60);
    int int63 = stringList0.size();
    org.junit.Assert.assertNotNull(shortItor3);
    org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
    org.junit.Assert.assertTrue("'" + int5 + "' != '" + (-15) + "'", int5.equals((-15)));
    org.junit.Assert.assertTrue("'" + int6 + "' != '" + 0 + "'", int6 == 0);
    org.junit.Assert.assertNotNull(shortItor10);
    org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
    org.junit.Assert.assertTrue("'" + double21 + "' != '" + 0.0d + "'", double21 == 0.0d);
    org.junit.Assert.assertTrue("'" + double40 + "' != '" + 0.0d + "'", double40 == 0.0d);
    org.junit.Assert.assertTrue("'" + double59 + "' != '" + 11111.0d + "'", double59 == 11111.0d);
    org.junit.Assert.assertNotNull(gPSSatInViewScienceArray60);
    org.junit.Assert.assertNotNull(gPSSatInViewScienceArray61);
    org.junit.Assert.assertTrue("'" + int62 + "' != '" + (-1) + "'", int62 == (-1));
    org.junit.Assert.assertTrue("'" + int63 + "' != '" + 0 + "'", int63 == 0);
  }

  @Test
  public void test1091() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1091");
    }
    org.ccsds.moims.mo.mal.structures.StringList stringList0 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    java.util.Iterator<java.lang.String> strItor1 = stringList0.iterator();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = stringList0.getAreaVersion();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState6 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray9 = new float[]{28, 8};
    simulatorSpacecraftState6.setQ(floatArray9);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState14 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double15 = simulatorSpacecraftState14.getLatitude();
    java.lang.String str16 = simulatorSpacecraftState14.getMagField();
    float[] floatArray17 = simulatorSpacecraftState14.getR();
    simulatorSpacecraftState6.setQ(floatArray17);
    java.lang.String str19 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray17);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState23 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double24 = simulatorSpacecraftState23.getLatitude();
    double double25 = simulatorSpacecraftState23.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState29 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double30 = simulatorSpacecraftState29.getLatitude();
    java.lang.String str31 = simulatorSpacecraftState29.getMagField();
    float[] floatArray32 = simulatorSpacecraftState29.getR();
    simulatorSpacecraftState23.setQ(floatArray32);
    float[] floatArray34 = simulatorSpacecraftState23.getV();
    opssat.simulator.celestia.CelestiaData celestiaData35 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray17, floatArray34);
    float[] floatArray36 = celestiaData35.getQ();
    opssat.simulator.util.SimulatorData simulatorData40 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date41 = simulatorData40.getCurrentTime();
    int int42 = opssat.simulator.util.DateExtraction.getDayFromDate(date41);
    opssat.simulator.util.SimulatorData simulatorData44 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date45 = simulatorData44.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData47 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date48 = simulatorData47.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap49 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date45, date48);
    opssat.simulator.util.SimulatorHeader simulatorHeader50 =
        new opssat.simulator.util.SimulatorHeader(
            false, date41, date48);
    opssat.simulator.util.SimulatorData simulatorData51 = new opssat.simulator.util.SimulatorData(
        (short) 0, date41);
    celestiaData35.setDate(date41);
    int int53 = celestiaData35.getSeconds();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState57 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray60 = new float[]{28, 8};
    simulatorSpacecraftState57.setQ(floatArray60);
    celestiaData35.setQ(floatArray60);
    int int63 = stringList0.lastIndexOf(celestiaData35);
    celestiaData35.setDate("00000:00:00:00:097  11                 executed false   | ");
    float[] floatArray66 = celestiaData35.getQ();
    int int67 = celestiaData35.getSeconds();
    org.junit.Assert.assertNotNull(strItor1);
    org.junit.Assert.assertNotNull(uOctet2);
    org.junit.Assert.assertNotNull(floatArray9);
    org.junit.Assert.assertTrue("'" + double15 + "' != '" + 340.0d + "'", double15 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str16 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str16.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray17);
    org.junit.Assert.assertTrue("'" + str19 + "' != '" + "UnknownGUIData" + "'",
        str19.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double24 + "' != '" + 340.0d + "'", double24 == 340.0d);
    org.junit.Assert.assertTrue("'" + double25 + "' != '" + 340.0d + "'", double25 == 340.0d);
    org.junit.Assert.assertTrue("'" + double30 + "' != '" + 340.0d + "'", double30 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str31 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str31.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray32);
    org.junit.Assert.assertNotNull(floatArray34);
    org.junit.Assert.assertNotNull(floatArray36);
    org.junit.Assert.assertNotNull(date41);
    org.junit.Assert.assertNotNull(date45);
    org.junit.Assert.assertNotNull(date48);
    org.junit.Assert.assertNotNull(timeUnitMap49);
    org.junit.Assert.assertNotNull(floatArray60);
    org.junit.Assert.assertTrue("'" + int63 + "' != '" + (-1) + "'", int63 == (-1));
    org.junit.Assert.assertNotNull(floatArray66);
  }

  @Test
  public void test1092() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1092");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState11 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double12 = simulatorSpacecraftState11.getLatitude();
    java.lang.String str13 = simulatorSpacecraftState11.getMagField();
    float[] floatArray14 = simulatorSpacecraftState11.getR();
    simulatorSpacecraftState3.setQ(floatArray14);
    simulatorSpacecraftState3.setLatitude(97L);
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + 340.0d + "'", double12 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str13 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str13.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray14);
  }

  @Test
  public void test1093() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1093");
    }
    try {
      opssat.simulator.orekit.GPSSatInView gPSSatInView2 = new opssat.simulator.orekit.GPSSatInView(
          "{counter=8, methodsExecuted=0, currentTime=Thu May 23 15:09:49 UTC 2019}", 1.0f);
      org.junit.Assert.fail(
          "Expected exception of type java.lang.StringIndexOutOfBoundsException; message: String index out of range: -1");
    } catch (java.lang.StringIndexOutOfBoundsException e) {
    }
  }

  @Test
  public void test1095() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1095");
    }
    org.ccsds.moims.mo.mal.structures.StringList stringList0 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    java.util.Iterator<java.lang.String> strItor1 = stringList0.iterator();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = stringList0.getAreaVersion();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState6 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray9 = new float[]{28, 8};
    simulatorSpacecraftState6.setQ(floatArray9);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState14 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double15 = simulatorSpacecraftState14.getLatitude();
    java.lang.String str16 = simulatorSpacecraftState14.getMagField();
    float[] floatArray17 = simulatorSpacecraftState14.getR();
    simulatorSpacecraftState6.setQ(floatArray17);
    java.lang.String str19 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray17);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState23 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double24 = simulatorSpacecraftState23.getLatitude();
    double double25 = simulatorSpacecraftState23.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState29 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double30 = simulatorSpacecraftState29.getLatitude();
    java.lang.String str31 = simulatorSpacecraftState29.getMagField();
    float[] floatArray32 = simulatorSpacecraftState29.getR();
    simulatorSpacecraftState23.setQ(floatArray32);
    float[] floatArray34 = simulatorSpacecraftState23.getV();
    opssat.simulator.celestia.CelestiaData celestiaData35 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray17, floatArray34);
    float[] floatArray36 = celestiaData35.getQ();
    opssat.simulator.util.SimulatorData simulatorData40 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date41 = simulatorData40.getCurrentTime();
    int int42 = opssat.simulator.util.DateExtraction.getDayFromDate(date41);
    opssat.simulator.util.SimulatorData simulatorData44 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date45 = simulatorData44.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData47 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date48 = simulatorData47.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap49 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date45, date48);
    opssat.simulator.util.SimulatorHeader simulatorHeader50 =
        new opssat.simulator.util.SimulatorHeader(
            false, date41, date48);
    opssat.simulator.util.SimulatorData simulatorData51 = new opssat.simulator.util.SimulatorData(
        (short) 0, date41);
    celestiaData35.setDate(date41);
    int int53 = celestiaData35.getSeconds();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState57 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray60 = new float[]{28, 8};
    simulatorSpacecraftState57.setQ(floatArray60);
    celestiaData35.setQ(floatArray60);
    int int63 = stringList0.lastIndexOf(celestiaData35);
    org.ccsds.moims.mo.mal.structures.UOctet uOctet64 = stringList0.getAreaVersion();
    java.util.Iterator<java.lang.String> strItor65 = stringList0.iterator();
    java.lang.Object[] objArray66 = stringList0.toArray();
    org.junit.Assert.assertNotNull(strItor1);
    org.junit.Assert.assertNotNull(uOctet2);
    org.junit.Assert.assertNotNull(floatArray9);
    org.junit.Assert.assertTrue("'" + double15 + "' != '" + 340.0d + "'", double15 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str16 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str16.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray17);
    org.junit.Assert.assertTrue("'" + str19 + "' != '" + "UnknownGUIData" + "'",
        str19.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double24 + "' != '" + 340.0d + "'", double24 == 340.0d);
    org.junit.Assert.assertTrue("'" + double25 + "' != '" + 340.0d + "'", double25 == 340.0d);
    org.junit.Assert.assertTrue("'" + double30 + "' != '" + 340.0d + "'", double30 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str31 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str31.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray32);
    org.junit.Assert.assertNotNull(floatArray34);
    org.junit.Assert.assertNotNull(floatArray36);
    org.junit.Assert.assertNotNull(date41);
    org.junit.Assert.assertNotNull(date45);
    org.junit.Assert.assertNotNull(date48);
    org.junit.Assert.assertNotNull(timeUnitMap49);
    org.junit.Assert.assertNotNull(floatArray60);
    org.junit.Assert.assertTrue("'" + int63 + "' != '" + (-1) + "'", int63 == (-1));
    org.junit.Assert.assertNotNull(uOctet64);
    org.junit.Assert.assertNotNull(strItor65);
    org.junit.Assert.assertNotNull(objArray66);
  }

  @Test
  public void test1096() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1096");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double4 = simulatorSpacecraftState3.getLatitude();
    java.lang.String str5 = simulatorSpacecraftState3.getMagField();
    java.lang.String str6 = simulatorSpacecraftState3.toString();
    double[] doubleArray7 = simulatorSpacecraftState3.getSunVector();
    simulatorSpacecraftState3.setSatsInView((short) 10);
    java.lang.String str10 = simulatorSpacecraftState3.toString();
    double double11 = simulatorSpacecraftState3.getAltitude();
    float[] floatArray12 = simulatorSpacecraftState3.getQ();
    org.junit.Assert.assertTrue("'" + double4 + "' != '" + 340.0d + "'", double4 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str5 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str5.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str6 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str6.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray7);
    org.junit.Assert.assertTrue(
        "'" + str10 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str10.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertTrue("'" + double11 + "' != '" + 14.0d + "'", double11 == 14.0d);
    org.junit.Assert.assertNotNull(floatArray12);
  }

  @Test
  public void test1097() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1097");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessSingleStreamOperatingBuffer endlessSingleStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessSingleStreamOperatingBuffer(
            logger0);
    byte[] byteArray3 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS.float2ByteArray(64);
    endlessSingleStreamOperatingBuffer1.setDataFromByteArray(byteArray3);
    int int5 = endlessSingleStreamOperatingBuffer1.getOperatingIndex();
    boolean boolean7 = endlessSingleStreamOperatingBuffer1.preparePath("*63");
    int int8 = endlessSingleStreamOperatingBuffer1.getOperatingIndex();
    org.junit.Assert.assertNotNull(byteArray3);
    org.junit.Assert.assertTrue("'" + int5 + "' != '" + 0 + "'", int5 == 0);
    org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
    org.junit.Assert.assertTrue("'" + int8 + "' != '" + 0 + "'", int8 == 0);
  }

  @Test
  public void test1098() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1098");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double4 = simulatorSpacecraftState3.getLatitude();
    double double5 = simulatorSpacecraftState3.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState9 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double10 = simulatorSpacecraftState9.getLatitude();
    java.lang.String str11 = simulatorSpacecraftState9.getMagField();
    float[] floatArray12 = simulatorSpacecraftState9.getR();
    simulatorSpacecraftState3.setQ(floatArray12);
    float[] floatArray14 = simulatorSpacecraftState3.getQ();
    simulatorSpacecraftState3.setAltitude(0.0d);
    int int17 = simulatorSpacecraftState3.getSatsInView();
    org.junit.Assert.assertTrue("'" + double4 + "' != '" + 340.0d + "'", double4 == 340.0d);
    org.junit.Assert.assertTrue("'" + double5 + "' != '" + 340.0d + "'", double5 == 340.0d);
    org.junit.Assert.assertTrue("'" + double10 + "' != '" + 340.0d + "'", double10 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str11 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str11.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray12);
    org.junit.Assert.assertNotNull(floatArray14);
    org.junit.Assert.assertTrue("'" + int17 + "' != '" + 0 + "'", int17 == 0);
  }

  @Test
  public void test1099() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1099");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor3 = octetList0.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor5 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList0, "hi!");
    argumentDescriptor5.restoreArgument();
    argumentDescriptor5.setName("01700.0000000");
    argumentDescriptor5.restoreArgument();
    java.lang.String str10 = argumentDescriptor5.getName();
    java.lang.Object obj11 = argumentDescriptor5.getType();
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(byteItor3);
    org.junit.Assert.assertTrue("'" + str10 + "' != '" + "hi!" + "'", str10.equals("hi!"));
    org.junit.Assert.assertNotNull(obj11);
  }

  @Test
  public void test1100() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1100");
    }
    java.lang.Double[] doubleArray4 = new java.lang.Double[]{(-1.0d), 100.0d, 10.0d, 10.0d};
    java.util.ArrayList<java.lang.Double> doubleList5 = new java.util.ArrayList<java.lang.Double>();
    boolean boolean6 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Double>) doubleList5, doubleArray4);
    org.ccsds.moims.mo.mal.structures.UShort uShort7 =
        org.ccsds.moims.mo.mal.structures.URIList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort[] uShortArray8 =
        new org.ccsds.moims.mo.mal.structures.UShort[]{
          uShort7};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList9 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
    boolean boolean10 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList9, uShortArray8);
    uShortList9.ensureCapacity(0);
    int int14 = uShortList9.indexOf((byte) 1);
    uShortList9.clear();
    java.lang.Long[] longArray18 = new java.lang.Long[]{0L, 10L};
    java.util.ArrayList<java.lang.Long> longList19 = new java.util.ArrayList<java.lang.Long>();
    boolean boolean20 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Long>) longList19, longArray18);
    java.lang.Object obj21 = longList19.clone();
    boolean boolean22 = uShortList9.contains(longList19);
    boolean boolean23 = doubleList5.equals(boolean22);
    java.lang.Integer[] intArray26 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList27 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean28 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList27, intArray26);
    int int30 = intList27.lastIndexOf((byte) 10);
    boolean boolean31 = doubleList5.removeAll(intList27);
    org.ccsds.moims.mo.mal.structures.OctetList octetList32 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    octetList32.ensureCapacity(13);
    java.lang.Byte[] byteArray39 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList40 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean41 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList40, byteArray39);
    java.lang.Integer[] intArray44 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList45 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean46 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList45, intArray44);
    boolean boolean47 = byteList40.retainAll(intList45);
    boolean boolean48 = octetList32.containsAll(intList45);
    boolean boolean49 = doubleList5.removeAll(intList45);
    java.util.Spliterator<java.lang.Double> doubleSpliterator50 = doubleList5.spliterator();
    doubleList5.trimToSize();
    opssat.simulator.util.SimulatorData simulatorData53 = new opssat.simulator.util.SimulatorData(
        (-18));
    simulatorData53.setCounter((-1));
    simulatorData53.feedTimeElapsed(21);
    java.lang.String str58 = simulatorData53.getCurrentDay();
    boolean boolean59 = simulatorData53.isTimeRunning();
    java.util.Date date60 = simulatorData53.getCurrentTime();
    int int61 = doubleList5.indexOf(simulatorData53);
    simulatorData53.feedTimeElapsed(281474993487882L);
    opssat.simulator.util.SimulatorData simulatorData67 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date68 = simulatorData67.getCurrentTime();
    java.util.Date date69 = simulatorData67.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData70 = new opssat.simulator.util.SimulatorData(
        17, date69);
    opssat.simulator.util.SimulatorData simulatorData73 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date74 = simulatorData73.getCurrentTime();
    int int75 = opssat.simulator.util.DateExtraction.getDayFromDate(date74);
    opssat.simulator.util.SimulatorData simulatorData77 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date78 = simulatorData77.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData80 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date81 = simulatorData80.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap82 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date78, date81);
    opssat.simulator.util.SimulatorHeader simulatorHeader83 =
        new opssat.simulator.util.SimulatorHeader(
            false, date74, date81);
    opssat.simulator.util.SimulatorHeader simulatorHeader84 =
        new opssat.simulator.util.SimulatorHeader(
            false, date69, date74);
    boolean boolean85 = simulatorHeader84.isAutoStartTime();
    simulatorHeader84.setUseCelestia(false);
    simulatorHeader84.setOrekitTLE1("[]");
    simulatorHeader84
        .setOrekitPropagator("opssat.simulator.util.wav.WavFileException: UnknownGUIData");
    java.lang.String str92 = simulatorHeader84.getOrekitPropagator();
    simulatorHeader84.setTimeFactor(8);
    boolean boolean95 = simulatorHeader84.isUpdateInternet();
    simulatorData53.initFromHeader(simulatorHeader84);
    java.lang.String str97 = simulatorData53.getCurrentMonth();
    org.junit.Assert.assertNotNull(doubleArray4);
    org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + true + "'", boolean6);
    org.junit.Assert.assertNotNull(uShort7);
    org.junit.Assert.assertNotNull(uShortArray8);
    org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
    org.junit.Assert.assertTrue("'" + int14 + "' != '" + (-1) + "'", int14 == (-1));
    org.junit.Assert.assertNotNull(longArray18);
    org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + true + "'", boolean20);
    org.junit.Assert.assertNotNull(obj21);
    org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + false + "'", !boolean22);
    org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + false + "'", !boolean23);
    org.junit.Assert.assertNotNull(intArray26);
    org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + true + "'", boolean28);
    org.junit.Assert.assertTrue("'" + int30 + "' != '" + (-1) + "'", int30 == (-1));
    org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + false + "'", !boolean31);
    org.junit.Assert.assertNotNull(byteArray39);
    org.junit.Assert.assertTrue("'" + boolean41 + "' != '" + true + "'", boolean41);
    org.junit.Assert.assertNotNull(intArray44);
    org.junit.Assert.assertTrue("'" + boolean46 + "' != '" + true + "'", boolean46);
    org.junit.Assert.assertTrue("'" + boolean47 + "' != '" + true + "'", boolean47);
    org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + false + "'", !boolean48);
    org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + false + "'", !boolean49);
    org.junit.Assert.assertNotNull(doubleSpliterator50);
    org.junit.Assert.assertTrue("'" + boolean59 + "' != '" + false + "'", !boolean59);
    org.junit.Assert.assertNotNull(date60);
    org.junit.Assert.assertTrue("'" + int61 + "' != '" + (-1) + "'", int61 == (-1));
    org.junit.Assert.assertNotNull(date68);
    org.junit.Assert.assertNotNull(date69);
    org.junit.Assert.assertNotNull(date74);
    org.junit.Assert.assertNotNull(date78);
    org.junit.Assert.assertNotNull(date81);
    org.junit.Assert.assertNotNull(timeUnitMap82);
    org.junit.Assert.assertTrue("'" + boolean85 + "' != '" + false + "'", !boolean85);
    org.junit.Assert.assertTrue(
        "'" + str92 + "' != '" + "opssat.simulator.util.wav.WavFileException: UnknownGUIData" + "'",
        str92.equals("opssat.simulator.util.wav.WavFileException: UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + boolean95 + "' != '" + false + "'", !boolean95);
  }

  @Test
  public void test1101() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1101");
    }
    java.lang.Double[] doubleArray4 = new java.lang.Double[]{(-1.0d), 100.0d, 10.0d, 10.0d};
    java.util.ArrayList<java.lang.Double> doubleList5 = new java.util.ArrayList<java.lang.Double>();
    boolean boolean6 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Double>) doubleList5, doubleArray4);
    org.ccsds.moims.mo.mal.structures.UShort uShort7 =
        org.ccsds.moims.mo.mal.structures.URIList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort[] uShortArray8 =
        new org.ccsds.moims.mo.mal.structures.UShort[]{
          uShort7};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList9 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
    boolean boolean10 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList9, uShortArray8);
    uShortList9.ensureCapacity(0);
    int int14 = uShortList9.indexOf((byte) 1);
    uShortList9.clear();
    java.lang.Long[] longArray18 = new java.lang.Long[]{0L, 10L};
    java.util.ArrayList<java.lang.Long> longList19 = new java.util.ArrayList<java.lang.Long>();
    boolean boolean20 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Long>) longList19, longArray18);
    java.lang.Object obj21 = longList19.clone();
    boolean boolean22 = uShortList9.contains(longList19);
    boolean boolean23 = doubleList5.equals(boolean22);
    java.lang.Integer[] intArray26 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList27 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean28 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList27, intArray26);
    int int30 = intList27.lastIndexOf((byte) 10);
    boolean boolean31 = doubleList5.removeAll(intList27);
    java.lang.Short[] shortArray34 = new java.lang.Short[]{(short) -1, (short) 10};
    java.util.ArrayList<java.lang.Short> shortList35 = new java.util.ArrayList<java.lang.Short>();
    boolean boolean36 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Short>) shortList35, shortArray34);
    boolean boolean37 = shortList35.isEmpty();
    int int39 = shortList35.lastIndexOf(281475010265070L);
    org.ccsds.moims.mo.mal.structures.UShort uShort40 =
        org.ccsds.moims.mo.mal.structures.StringList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.OctetList octetList41 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int42 = octetList41.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort43 = octetList41.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UShort uShort44 =
        org.ccsds.moims.mo.mal.structures.OctetList.AREA_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort45 =
        org.ccsds.moims.mo.mal.structures.FloatList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort46 =
        org.ccsds.moims.mo.mal.structures.StringList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort47 =
        org.ccsds.moims.mo.mal.structures.URIList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort48 =
        org.ccsds.moims.mo.mal.structures.BooleanList.AREA_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort49 =
        org.ccsds.moims.mo.mal.structures.OctetList.AREA_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort50 =
        org.ccsds.moims.mo.mal.structures.URIList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort[] uShortArray51 =
        new org.ccsds.moims.mo.mal.structures.UShort[]{
          uShort50};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList52 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
    boolean boolean53 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList52,
        uShortArray51);
    uShortList52.ensureCapacity(0);
    int int57 = uShortList52.indexOf((byte) 1);
    uShortList52.clear();
    java.lang.Long[] longArray61 = new java.lang.Long[]{0L, 10L};
    java.util.ArrayList<java.lang.Long> longList62 = new java.util.ArrayList<java.lang.Long>();
    boolean boolean63 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Long>) longList62, longArray61);
    java.lang.Object obj64 = longList62.clone();
    boolean boolean65 = uShortList52.contains(longList62);
    org.ccsds.moims.mo.mal.structures.UShort uShort66 =
        org.ccsds.moims.mo.mal.structures.UShortList.SERVICE_SHORT_FORM;
    boolean boolean67 = uShortList52.add(uShort66);
    org.ccsds.moims.mo.mal.structures.UShort[] uShortArray68 =
        new org.ccsds.moims.mo.mal.structures.UShort[]{
          uShort40, uShort43, uShort44, uShort45, uShort46, uShort47, uShort48, uShort49, uShort66};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList69 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
    boolean boolean70 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList69,
        uShortArray68);
    uShortList69.ensureCapacity(100);
    boolean boolean74 = uShortList69.equals(9);
    org.ccsds.moims.mo.mal.structures.OctetList octetList75 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int76 = octetList75.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort77 = octetList75.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor78 = octetList75.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor80 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList75, "hi!");
    java.lang.Object obj81 = argumentDescriptor80.getType();
    int int82 = uShortList69.indexOf(obj81);
    int int83 = shortList35.lastIndexOf(obj81);
    boolean boolean84 = doubleList5.equals(obj81);
    doubleList5.trimToSize();
    java.util.Spliterator<java.lang.Double> doubleSpliterator86 = doubleList5.spliterator();
    java.util.stream.Stream<java.lang.Double> doubleStream87 = doubleList5.parallelStream();
    org.junit.Assert.assertNotNull(doubleArray4);
    org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + true + "'", boolean6);
    org.junit.Assert.assertNotNull(uShort7);
    org.junit.Assert.assertNotNull(uShortArray8);
    org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
    org.junit.Assert.assertTrue("'" + int14 + "' != '" + (-1) + "'", int14 == (-1));
    org.junit.Assert.assertNotNull(longArray18);
    org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + true + "'", boolean20);
    org.junit.Assert.assertNotNull(obj21);
    org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + false + "'", !boolean22);
    org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + false + "'", !boolean23);
    org.junit.Assert.assertNotNull(intArray26);
    org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + true + "'", boolean28);
    org.junit.Assert.assertTrue("'" + int30 + "' != '" + (-1) + "'", int30 == (-1));
    org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + false + "'", !boolean31);
    org.junit.Assert.assertNotNull(shortArray34);
    org.junit.Assert.assertTrue("'" + boolean36 + "' != '" + true + "'", boolean36);
    org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + false + "'", !boolean37);
    org.junit.Assert.assertTrue("'" + int39 + "' != '" + (-1) + "'", int39 == (-1));
    org.junit.Assert.assertNotNull(uShort40);
    org.junit.Assert.assertTrue("'" + int42 + "' != '" + (-7) + "'", int42.equals((-7)));
    org.junit.Assert.assertNotNull(uShort43);
    org.junit.Assert.assertNotNull(uShort44);
    org.junit.Assert.assertNotNull(uShort45);
    org.junit.Assert.assertNotNull(uShort46);
    org.junit.Assert.assertNotNull(uShort47);
    org.junit.Assert.assertNotNull(uShort48);
    org.junit.Assert.assertNotNull(uShort49);
    org.junit.Assert.assertNotNull(uShort50);
    org.junit.Assert.assertNotNull(uShortArray51);
    org.junit.Assert.assertTrue("'" + boolean53 + "' != '" + true + "'", boolean53);
    org.junit.Assert.assertTrue("'" + int57 + "' != '" + (-1) + "'", int57 == (-1));
    org.junit.Assert.assertNotNull(longArray61);
    org.junit.Assert.assertTrue("'" + boolean63 + "' != '" + true + "'", boolean63);
    org.junit.Assert.assertNotNull(obj64);
    org.junit.Assert.assertTrue("'" + boolean65 + "' != '" + false + "'", !boolean65);
    org.junit.Assert.assertNotNull(uShort66);
    org.junit.Assert.assertTrue("'" + boolean67 + "' != '" + true + "'", boolean67);
    org.junit.Assert.assertNotNull(uShortArray68);
    org.junit.Assert.assertTrue("'" + boolean70 + "' != '" + true + "'", boolean70);
    org.junit.Assert.assertTrue("'" + boolean74 + "' != '" + false + "'", !boolean74);
    org.junit.Assert.assertTrue("'" + int76 + "' != '" + (-7) + "'", int76.equals((-7)));
    org.junit.Assert.assertNotNull(uShort77);
    org.junit.Assert.assertNotNull(byteItor78);
    org.junit.Assert.assertNotNull(obj81);
    org.junit.Assert.assertTrue("'" + int82 + "' != '" + (-1) + "'", int82 == (-1));
    org.junit.Assert.assertTrue("'" + int83 + "' != '" + (-1) + "'", int83 == (-1));
    org.junit.Assert.assertTrue("'" + boolean84 + "' != '" + false + "'", !boolean84);
    org.junit.Assert.assertNotNull(doubleSpliterator86);
    org.junit.Assert.assertNotNull(doubleStream87);
  }

  @Test
  public void test1103() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1103");
    }
    opssat.simulator.util.SimulatorData simulatorData3 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date4 = simulatorData3.getCurrentTime();
    int int5 = opssat.simulator.util.DateExtraction.getDayFromDate(date4);
    opssat.simulator.util.SimulatorData simulatorData7 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date8 = simulatorData7.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData10 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date11 = simulatorData10.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap12 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date8, date11);
    opssat.simulator.util.SimulatorHeader simulatorHeader13 =
        new opssat.simulator.util.SimulatorHeader(
            false, date4, date11);
    java.util.Date date14 = simulatorHeader13.getEndDate();
    int int15 = simulatorHeader13.getMinuteStartDate();
    opssat.simulator.util.SimulatorData simulatorData17 = new opssat.simulator.util.SimulatorData(
        (-18));
    long long18 = simulatorData17.getCurrentTimeMillis();
    java.lang.String str19 = simulatorData17.getCurrentDay();
    java.lang.String str20 = simulatorData17.getUTCCurrentTime2();
    java.lang.String str21 = simulatorData17.getCurrentDay();
    java.util.Date date22 = simulatorData17.getCurrentTime();
    int int23 = opssat.simulator.util.DateExtraction.getMinuteFromDate(date22);
    simulatorHeader13.setEndDate(date22);
    int int25 = opssat.simulator.util.DateExtraction.getHourFromDate(date22);
    int int26 = opssat.simulator.util.DateExtraction.getMonthFromDate(date22);
    java.util.Date date28 = null;
    java.lang.Long[] longArray35 = new java.lang.Long[]{281475010265070L, 100L, 0L,
      281475010265070L, 281475010265070L, 1L};
    java.util.ArrayList<java.lang.Long> longList36 = new java.util.ArrayList<java.lang.Long>();
    boolean boolean37 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Long>) longList36, longArray35);
    boolean boolean38 = longList36.isEmpty();
    java.lang.Object obj39 = longList36.clone();
    opssat.simulator.util.SimulatorData simulatorData42 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date43 = simulatorData42.getCurrentTime();
    int int44 = opssat.simulator.util.DateExtraction.getDayFromDate(date43);
    opssat.simulator.util.SimulatorData simulatorData46 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date47 = simulatorData46.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData49 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date50 = simulatorData49.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap51 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date47, date50);
    opssat.simulator.util.SimulatorHeader simulatorHeader52 =
        new opssat.simulator.util.SimulatorHeader(
            false, date43, date50);
    boolean boolean53 = longList36.remove(date50);
    int int54 = opssat.simulator.util.DateExtraction.getHourFromDate(date50);
    opssat.simulator.util.SimulatorHeader simulatorHeader55 =
        new opssat.simulator.util.SimulatorHeader(
            true, date28, date50);
    int int56 = opssat.simulator.util.DateExtraction.getMonthFromDate(date50);
    opssat.simulator.util.SimulatorHeader simulatorHeader57 =
        new opssat.simulator.util.SimulatorHeader(
            false, date22, date50);
    org.junit.Assert.assertNotNull(date4);
    org.junit.Assert.assertNotNull(date8);
    org.junit.Assert.assertNotNull(date11);
    org.junit.Assert.assertNotNull(timeUnitMap12);
    org.junit.Assert.assertNotNull(date14);
    org.junit.Assert.assertTrue("'" + long18 + "' != '" + 0L + "'", long18 == 0L);
    org.junit.Assert.assertNotNull(date22);
    org.junit.Assert.assertNotNull(longArray35);
    org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + true + "'", boolean37);
    org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + false + "'", !boolean38);
    org.junit.Assert.assertNotNull(obj39);
    org.junit.Assert.assertNotNull(date43);
    org.junit.Assert.assertNotNull(date47);
    org.junit.Assert.assertNotNull(date50);
    org.junit.Assert.assertNotNull(timeUnitMap51);
    org.junit.Assert.assertTrue("'" + boolean53 + "' != '" + false + "'", !boolean53);
  }

  @Test
  public void test1104() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1104");
    }
    opssat.simulator.util.SimulatorData simulatorData1 = new opssat.simulator.util.SimulatorData(
        (-18));
    long long2 = simulatorData1.getCurrentTimeMillis();
    java.lang.String str3 = simulatorData1.getCurrentDay();
    java.lang.String str4 = simulatorData1.getCurrentYear();
    java.lang.String str5 = simulatorData1.getCurrentDay();
    simulatorData1.setCounter((-15));
    simulatorData1.setTimeFactor(1);
    boolean boolean10 = simulatorData1.isSimulatorRunning();
    org.junit.Assert.assertTrue("'" + long2 + "' != '" + 0L + "'", long2 == 0L);
    org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
  }

  @Test
  public void test1105() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1105");
    }
    opssat.simulator.util.SimulatorData simulatorData1 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date2 = simulatorData1.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData4 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date5 = simulatorData4.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap6 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date2, date5);
    int int7 = opssat.simulator.util.DateExtraction.getMinuteFromDate(date5);
    org.junit.Assert.assertNotNull(date2);
    org.junit.Assert.assertNotNull(date5);
    org.junit.Assert.assertNotNull(timeUnitMap6);
  }

  @Test
  public void test1106() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1106");
    }
    opssat.simulator.util.ArgumentTemplate argumentTemplate2 =
        new opssat.simulator.util.ArgumentTemplate(
            "05400.0000",
            "SimulatorHeader{autoStartSystem=true, autoStartTime=false, timeFactor=1, startDate=Thu May 23 15:09:44 UTC 2019, endDate=Thu May 23 15:09:44 UTC 2019}");
    java.lang.String str3 = argumentTemplate2.getDescription();
    argumentTemplate2.setDescription("OPS-SAT SoftSim:");
    org.junit.Assert.assertTrue("'" + str3 + "' != '" + "05400.0000" + "'",
        str3.equals("05400.0000"));
  }

  @Test
  public void test1107() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1107");
    }
    org.ccsds.moims.mo.mal.structures.BooleanList booleanList1 =
        new org.ccsds.moims.mo.mal.structures.BooleanList(
            36);
    java.lang.Integer int2 = booleanList1.getTypeShortForm();
    java.lang.Double[] doubleArray7 = new java.lang.Double[]{(-1.0d), 100.0d, 10.0d, 10.0d};
    java.util.ArrayList<java.lang.Double> doubleList8 = new java.util.ArrayList<java.lang.Double>();
    boolean boolean9 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Double>) doubleList8, doubleArray7);
    org.ccsds.moims.mo.mal.structures.UShort uShort10 =
        org.ccsds.moims.mo.mal.structures.URIList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort[] uShortArray11 =
        new org.ccsds.moims.mo.mal.structures.UShort[]{
          uShort10};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList12 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
    boolean boolean13 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList12,
        uShortArray11);
    uShortList12.ensureCapacity(0);
    int int17 = uShortList12.indexOf((byte) 1);
    uShortList12.clear();
    java.lang.Long[] longArray21 = new java.lang.Long[]{0L, 10L};
    java.util.ArrayList<java.lang.Long> longList22 = new java.util.ArrayList<java.lang.Long>();
    boolean boolean23 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Long>) longList22, longArray21);
    java.lang.Object obj24 = longList22.clone();
    boolean boolean25 = uShortList12.contains(longList22);
    boolean boolean26 = doubleList8.equals(boolean25);
    java.lang.Integer[] intArray29 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList30 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean31 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList30, intArray29);
    int int33 = intList30.lastIndexOf((byte) 10);
    boolean boolean34 = doubleList8.removeAll(intList30);
    java.lang.Integer[] intArray42 = new java.lang.Integer[]{13, 10, 100, 100, 11111, 13, 11111};
    java.util.ArrayList<java.lang.Integer> intList43 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean44 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList43, intArray42);
    java.lang.Byte[] byteArray49 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList50 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean51 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList50, byteArray49);
    java.lang.Integer[] intArray54 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList55 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean56 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList55, intArray54);
    boolean boolean57 = byteList50.retainAll(intList55);
    java.lang.Integer[] intArray60 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList61 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean62 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList61, intArray60);
    int int64 = intList61.lastIndexOf((byte) 10);
    boolean boolean65 = intList55.removeAll(intList61);
    boolean boolean66 = intList43.retainAll(intList61);
    boolean boolean67 = doubleList8.removeAll(intList61);
    java.lang.Float[] floatArray71 = new java.lang.Float[]{1.0f, 1.0f, (-1.0f)};
    java.util.ArrayList<java.lang.Float> floatList72 = new java.util.ArrayList<java.lang.Float>();
    boolean boolean73 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Float>) floatList72, floatArray71);
    boolean boolean75 = floatList72.add((-1.0f));
    floatList72.clear();
    boolean boolean77 = floatList72.isEmpty();
    java.util.ListIterator<java.lang.Float> floatItor78 = floatList72.listIterator();
    boolean boolean79 = doubleList8.contains(floatItor78);
    java.util.stream.Stream<java.lang.Double> doubleStream80 = doubleList8.stream();
    doubleList8.trimToSize();
    int int82 = booleanList1.lastIndexOf(doubleList8);
    org.ccsds.moims.mo.mal.structures.UShort uShort83 = booleanList1.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet84 = booleanList1.getAreaVersion();
    java.util.stream.Stream<java.lang.Boolean> booleanStream85 = booleanList1.stream();
    org.ccsds.moims.mo.mal.structures.UShort uShort86 = booleanList1.getServiceNumber();
    org.junit.Assert.assertTrue("'" + int2 + "' != '" + (-2) + "'", int2.equals((-2)));
    org.junit.Assert.assertNotNull(doubleArray7);
    org.junit.Assert.assertTrue("'" + boolean9 + "' != '" + true + "'", boolean9);
    org.junit.Assert.assertNotNull(uShort10);
    org.junit.Assert.assertNotNull(uShortArray11);
    org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
    org.junit.Assert.assertTrue("'" + int17 + "' != '" + (-1) + "'", int17 == (-1));
    org.junit.Assert.assertNotNull(longArray21);
    org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + true + "'", boolean23);
    org.junit.Assert.assertNotNull(obj24);
    org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + false + "'", !boolean25);
    org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + false + "'", !boolean26);
    org.junit.Assert.assertNotNull(intArray29);
    org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + true + "'", boolean31);
    org.junit.Assert.assertTrue("'" + int33 + "' != '" + (-1) + "'", int33 == (-1));
    org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + false + "'", !boolean34);
    org.junit.Assert.assertNotNull(intArray42);
    org.junit.Assert.assertTrue("'" + boolean44 + "' != '" + true + "'", boolean44);
    org.junit.Assert.assertNotNull(byteArray49);
    org.junit.Assert.assertTrue("'" + boolean51 + "' != '" + true + "'", boolean51);
    org.junit.Assert.assertNotNull(intArray54);
    org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + true + "'", boolean56);
    org.junit.Assert.assertTrue("'" + boolean57 + "' != '" + true + "'", boolean57);
    org.junit.Assert.assertNotNull(intArray60);
    org.junit.Assert.assertTrue("'" + boolean62 + "' != '" + true + "'", boolean62);
    org.junit.Assert.assertTrue("'" + int64 + "' != '" + (-1) + "'", int64 == (-1));
    org.junit.Assert.assertTrue("'" + boolean65 + "' != '" + true + "'", boolean65);
    org.junit.Assert.assertTrue("'" + boolean66 + "' != '" + true + "'", boolean66);
    org.junit.Assert.assertTrue("'" + boolean67 + "' != '" + false + "'", !boolean67);
    org.junit.Assert.assertNotNull(floatArray71);
    org.junit.Assert.assertTrue("'" + boolean73 + "' != '" + true + "'", boolean73);
    org.junit.Assert.assertTrue("'" + boolean75 + "' != '" + true + "'", boolean75);
    org.junit.Assert.assertTrue("'" + boolean77 + "' != '" + true + "'", boolean77);
    org.junit.Assert.assertNotNull(floatItor78);
    org.junit.Assert.assertTrue("'" + boolean79 + "' != '" + false + "'", !boolean79);
    org.junit.Assert.assertNotNull(doubleStream80);
    org.junit.Assert.assertTrue("'" + int82 + "' != '" + (-1) + "'", int82 == (-1));
    org.junit.Assert.assertNotNull(uShort83);
    org.junit.Assert.assertNotNull(uOctet84);
    org.junit.Assert.assertNotNull(booleanStream85);
    org.junit.Assert.assertNotNull(uShort86);
  }

  @Test
  public void test1108() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1108");
    }
    long long1 = opssat.simulator.util.SimulatorSchedulerPiece
        .getMillisFromDDDDDHHMMSSmmm("Unknown data type [java.util.stream.ReferencePipeline$Head]");
    org.junit.Assert.assertTrue("'" + long1 + "' != '" + (-1L) + "'", long1 == (-1L));
  }

  @Test
  public void test1109() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1109");
    }
    opssat.simulator.util.SimulatorData simulatorData3 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date4 = simulatorData3.getCurrentTime();
    java.util.Date date5 = simulatorData3.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(17,
        date5);
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    int int11 = opssat.simulator.util.DateExtraction.getDayFromDate(date10);
    opssat.simulator.util.SimulatorData simulatorData13 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date14 = simulatorData13.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData16 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date17 = simulatorData16.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap18 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date14, date17);
    opssat.simulator.util.SimulatorHeader simulatorHeader19 =
        new opssat.simulator.util.SimulatorHeader(
            false, date10, date17);
    opssat.simulator.util.SimulatorHeader simulatorHeader20 =
        new opssat.simulator.util.SimulatorHeader(
            false, date5, date10);
    boolean boolean21 = simulatorHeader20.isAutoStartTime();
    simulatorHeader20.setUseCelestia(false);
    simulatorHeader20.setOrekitTLE1("[]");
    simulatorHeader20
        .setOrekitPropagator("opssat.simulator.util.wav.WavFileException: UnknownGUIData");
    java.lang.String str28 = simulatorHeader20.getOrekitPropagator();
    simulatorHeader20.setTimeFactor(8);
    boolean boolean31 = simulatorHeader20.isUpdateInternet();
    java.util.Date date32 = simulatorHeader20.getEndDate();
    boolean boolean33 = simulatorHeader20.isAutoStartTime();
    org.junit.Assert.assertNotNull(date4);
    org.junit.Assert.assertNotNull(date5);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(date14);
    org.junit.Assert.assertNotNull(date17);
    org.junit.Assert.assertNotNull(timeUnitMap18);
    org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
    org.junit.Assert.assertTrue(
        "'" + str28 + "' != '" + "opssat.simulator.util.wav.WavFileException: UnknownGUIData" + "'",
        str28.equals("opssat.simulator.util.wav.WavFileException: UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + false + "'", !boolean31);
    org.junit.Assert.assertNotNull(date32);
    org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + false + "'", !boolean33);
  }

  @Test
  public void test1110() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1110");
    }
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience8 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    double double9 = gPSSatInViewScience8.getMinElevation();
    double double10 = gPSSatInViewScience8.getMaxElevation();
    double double11 = gPSSatInViewScience8.getAvgDistance();
    double double12 = gPSSatInViewScience8.getMinElevation();
    double double13 = gPSSatInViewScience8.getMinDistance();
    double double14 = gPSSatInViewScience8.getAvgDistance();
    double double15 = gPSSatInViewScience8.getAvgDistance();
    double double16 = gPSSatInViewScience8.getMaxElevation();
    org.junit.Assert.assertTrue("'" + double9 + "' != '" + 48.0d + "'", double9 == 48.0d);
    org.junit.Assert.assertTrue("'" + double10 + "' != '" + 56.0d + "'", double10 == 56.0d);
    org.junit.Assert.assertTrue("'" + double11 + "' != '" + 1.0d + "'", double11 == 1.0d);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + 48.0d + "'", double12 == 48.0d);
    org.junit.Assert.assertTrue("'" + double13 + "' != '" + 9.0d + "'", double13 == 9.0d);
    org.junit.Assert.assertTrue("'" + double14 + "' != '" + 1.0d + "'", double14 == 1.0d);
    org.junit.Assert.assertTrue("'" + double15 + "' != '" + 1.0d + "'", double15 == 1.0d);
    org.junit.Assert.assertTrue("'" + double16 + "' != '" + 56.0d + "'", double16 == 56.0d);
  }

  @Test
  public void test1111() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1111");
    }
    org.ccsds.moims.mo.mal.structures.DoubleList doubleList1 =
        new org.ccsds.moims.mo.mal.structures.DoubleList(
            ' ');
    org.ccsds.moims.mo.mal.structures.Element element2 = doubleList1.createElement();
    org.ccsds.moims.mo.mal.structures.DoubleList doubleList4 =
        new org.ccsds.moims.mo.mal.structures.DoubleList(
            (short) 10);
    org.ccsds.moims.mo.mal.structures.UShort uShort5 = doubleList4.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UShort uShort6 = doubleList4.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UShort uShort7 = doubleList4.getServiceNumber();
    org.ccsds.moims.mo.mal.structures.UShort uShort8 = doubleList4.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet9 = doubleList4.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.Element element10 = doubleList4.createElement();
    org.ccsds.moims.mo.mal.structures.UShort uShort11 = doubleList4.getAreaNumber();
    java.lang.Long long12 = doubleList4.getShortForm();
    org.ccsds.moims.mo.mal.structures.StringList stringList13 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    org.ccsds.moims.mo.mal.structures.ShortList shortList15 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            ' ');
    java.util.ListIterator<java.lang.Short> shortItor16 = shortList15.listIterator();
    boolean boolean17 = stringList13.equals(shortList15);
    org.ccsds.moims.mo.mal.structures.IntegerList integerList19 =
        new org.ccsds.moims.mo.mal.structures.IntegerList(
            48);
    java.lang.Long long20 = integerList19.getShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort21 = integerList19.getAreaNumber();
    int int22 = stringList13.indexOf(integerList19);
    boolean boolean23 = doubleList4.containsAll(integerList19);
    boolean boolean24 = doubleList1.containsAll(integerList19);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState28 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray31 = new float[]{28, 8};
    simulatorSpacecraftState28.setQ(floatArray31);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState36 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double37 = simulatorSpacecraftState36.getLatitude();
    java.lang.String str38 = simulatorSpacecraftState36.getMagField();
    float[] floatArray39 = simulatorSpacecraftState36.getR();
    simulatorSpacecraftState28.setQ(floatArray39);
    java.lang.String str41 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray39);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState45 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double46 = simulatorSpacecraftState45.getLatitude();
    double double47 = simulatorSpacecraftState45.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState51 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double52 = simulatorSpacecraftState51.getLatitude();
    java.lang.String str53 = simulatorSpacecraftState51.getMagField();
    float[] floatArray54 = simulatorSpacecraftState51.getR();
    simulatorSpacecraftState45.setQ(floatArray54);
    float[] floatArray56 = simulatorSpacecraftState45.getV();
    opssat.simulator.celestia.CelestiaData celestiaData57 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray39, floatArray56);
    float[] floatArray58 = celestiaData57.getQ();
    opssat.simulator.util.SimulatorData simulatorData62 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date63 = simulatorData62.getCurrentTime();
    int int64 = opssat.simulator.util.DateExtraction.getDayFromDate(date63);
    opssat.simulator.util.SimulatorData simulatorData66 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date67 = simulatorData66.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData69 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date70 = simulatorData69.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap71 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date67, date70);
    opssat.simulator.util.SimulatorHeader simulatorHeader72 =
        new opssat.simulator.util.SimulatorHeader(
            false, date63, date70);
    opssat.simulator.util.SimulatorData simulatorData73 = new opssat.simulator.util.SimulatorData(
        (short) 0, date63);
    celestiaData57.setDate(date63);
    int int75 = celestiaData57.getSeconds();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState79 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray82 = new float[]{28, 8};
    simulatorSpacecraftState79.setQ(floatArray82);
    celestiaData57.setQ(floatArray82);
    boolean boolean85 = doubleList1.remove(celestiaData57);
    int int86 = celestiaData57.getYears();
    org.junit.Assert.assertNotNull(element2);
    org.junit.Assert.assertNotNull(uShort5);
    org.junit.Assert.assertNotNull(uShort6);
    org.junit.Assert.assertNotNull(uShort7);
    org.junit.Assert.assertNotNull(uShort8);
    org.junit.Assert.assertNotNull(uOctet9);
    org.junit.Assert.assertNotNull(element10);
    org.junit.Assert.assertNotNull(uShort11);
    org.junit.Assert.assertTrue("'" + long12 + "' != '" + 281475010265083L + "'",
        long12.equals(281475010265083L));
    org.junit.Assert.assertNotNull(shortItor16);
    org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + true + "'", boolean17);
    org.junit.Assert.assertTrue("'" + long20 + "' != '" + 281475010265077L + "'",
        long20.equals(281475010265077L));
    org.junit.Assert.assertNotNull(uShort21);
    org.junit.Assert.assertTrue("'" + int22 + "' != '" + (-1) + "'", int22 == (-1));
    org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + true + "'", boolean23);
    org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + true + "'", boolean24);
    org.junit.Assert.assertNotNull(floatArray31);
    org.junit.Assert.assertTrue("'" + double37 + "' != '" + 340.0d + "'", double37 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str38 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str38.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray39);
    org.junit.Assert.assertTrue("'" + str41 + "' != '" + "UnknownGUIData" + "'",
        str41.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double46 + "' != '" + 340.0d + "'", double46 == 340.0d);
    org.junit.Assert.assertTrue("'" + double47 + "' != '" + 340.0d + "'", double47 == 340.0d);
    org.junit.Assert.assertTrue("'" + double52 + "' != '" + 340.0d + "'", double52 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str53 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str53.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray54);
    org.junit.Assert.assertNotNull(floatArray56);
    org.junit.Assert.assertNotNull(floatArray58);
    org.junit.Assert.assertNotNull(date63);
    org.junit.Assert.assertNotNull(date67);
    org.junit.Assert.assertNotNull(date70);
    org.junit.Assert.assertNotNull(timeUnitMap71);
    org.junit.Assert.assertNotNull(floatArray82);
    org.junit.Assert.assertTrue("'" + boolean85 + "' != '" + false + "'", !boolean85);
  }

  @Test
  public void test1112() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1112");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getAreaNumber();
    java.lang.Object[] objArray3 = octetList0.toArray();
    java.lang.Integer int4 = octetList0.getTypeShortForm();
    java.lang.Byte[] byteArray9 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList10 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean11 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList10, byteArray9);
    java.lang.Integer[] intArray14 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList15 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean16 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList15, intArray14);
    boolean boolean17 = byteList10.retainAll(intList15);
    java.lang.Integer[] intArray20 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList21 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean22 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList21, intArray20);
    int int24 = intList21.lastIndexOf((byte) 10);
    boolean boolean25 = intList15.retainAll(intList21);
    java.lang.Double[] doubleArray30 = new java.lang.Double[]{(-1.0d), 100.0d, 10.0d, 10.0d};
    java.util.ArrayList<java.lang.Double> doubleList31 = new java.util.ArrayList<java.lang.Double>();
    boolean boolean32 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Double>) doubleList31, doubleArray30);
    org.ccsds.moims.mo.mal.structures.UShort uShort33 =
        org.ccsds.moims.mo.mal.structures.URIList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort[] uShortArray34 =
        new org.ccsds.moims.mo.mal.structures.UShort[]{
          uShort33};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList35 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
    boolean boolean36 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList35,
        uShortArray34);
    uShortList35.ensureCapacity(0);
    int int40 = uShortList35.indexOf((byte) 1);
    uShortList35.clear();
    java.lang.Long[] longArray44 = new java.lang.Long[]{0L, 10L};
    java.util.ArrayList<java.lang.Long> longList45 = new java.util.ArrayList<java.lang.Long>();
    boolean boolean46 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Long>) longList45, longArray44);
    java.lang.Object obj47 = longList45.clone();
    boolean boolean48 = uShortList35.contains(longList45);
    boolean boolean49 = doubleList31.equals(boolean48);
    java.lang.Integer[] intArray52 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList53 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean54 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList53, intArray52);
    int int56 = intList53.lastIndexOf((byte) 10);
    boolean boolean57 = doubleList31.removeAll(intList53);
    boolean boolean58 = intList21.retainAll(intList53);
    java.lang.String str59 = intList53.toString();
    boolean boolean60 = octetList0.retainAll(intList53);
    java.util.Spliterator<java.lang.Byte> byteSpliterator61 = octetList0.spliterator();
    opssat.simulator.util.SimulatorData simulatorData64 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date65 = simulatorData64.getCurrentTime();
    int int66 = opssat.simulator.util.DateExtraction.getDayFromDate(date65);
    opssat.simulator.util.SimulatorData simulatorData68 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date69 = simulatorData68.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData71 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date72 = simulatorData71.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap73 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date69, date72);
    opssat.simulator.util.SimulatorHeader simulatorHeader74 =
        new opssat.simulator.util.SimulatorHeader(
            false, date65, date72);
    java.util.Date date75 = simulatorHeader74.getEndDate();
    boolean boolean76 = simulatorHeader74.isAutoStartSystem();
    int int77 = simulatorHeader74.getDayStartDate();
    boolean boolean78 = simulatorHeader74.isUseCelestia();
    opssat.simulator.util.SimulatorData simulatorData81 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date82 = simulatorData81.getCurrentTime();
    int int83 = opssat.simulator.util.DateExtraction.getDayFromDate(date82);
    opssat.simulator.util.SimulatorData simulatorData85 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date86 = simulatorData85.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData88 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date89 = simulatorData88.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap90 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date86, date89);
    opssat.simulator.util.SimulatorHeader simulatorHeader91 =
        new opssat.simulator.util.SimulatorHeader(
            false, date82, date89);
    simulatorHeader74.setStartDate(date89);
    simulatorHeader74.setKeplerElements("-0100.0000000");
    boolean boolean95 = simulatorHeader74.isUseOrekitPropagator();
    int int96 = octetList0.indexOf(boolean95);
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(objArray3);
    org.junit.Assert.assertTrue("'" + int4 + "' != '" + (-7) + "'", int4.equals((-7)));
    org.junit.Assert.assertNotNull(byteArray9);
    org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
    org.junit.Assert.assertNotNull(intArray14);
    org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + true + "'", boolean16);
    org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + true + "'", boolean17);
    org.junit.Assert.assertNotNull(intArray20);
    org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
    org.junit.Assert.assertTrue("'" + int24 + "' != '" + (-1) + "'", int24 == (-1));
    org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + true + "'", boolean25);
    org.junit.Assert.assertNotNull(doubleArray30);
    org.junit.Assert.assertTrue("'" + boolean32 + "' != '" + true + "'", boolean32);
    org.junit.Assert.assertNotNull(uShort33);
    org.junit.Assert.assertNotNull(uShortArray34);
    org.junit.Assert.assertTrue("'" + boolean36 + "' != '" + true + "'", boolean36);
    org.junit.Assert.assertTrue("'" + int40 + "' != '" + (-1) + "'", int40 == (-1));
    org.junit.Assert.assertNotNull(longArray44);
    org.junit.Assert.assertTrue("'" + boolean46 + "' != '" + true + "'", boolean46);
    org.junit.Assert.assertNotNull(obj47);
    org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + false + "'", !boolean48);
    org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + false + "'", !boolean49);
    org.junit.Assert.assertNotNull(intArray52);
    org.junit.Assert.assertTrue("'" + boolean54 + "' != '" + true + "'", boolean54);
    org.junit.Assert.assertTrue("'" + int56 + "' != '" + (-1) + "'", int56 == (-1));
    org.junit.Assert.assertTrue("'" + boolean57 + "' != '" + false + "'", !boolean57);
    org.junit.Assert.assertTrue("'" + boolean58 + "' != '" + false + "'", !boolean58);
    org.junit.Assert.assertTrue("'" + str59 + "' != '" + "[0, 1]" + "'", str59.equals("[0, 1]"));
    org.junit.Assert.assertTrue("'" + boolean60 + "' != '" + false + "'", !boolean60);
    org.junit.Assert.assertNotNull(byteSpliterator61);
    org.junit.Assert.assertNotNull(date65);
    org.junit.Assert.assertNotNull(date69);
    org.junit.Assert.assertNotNull(date72);
    org.junit.Assert.assertNotNull(timeUnitMap73);
    org.junit.Assert.assertNotNull(date75);
    org.junit.Assert.assertTrue("'" + boolean76 + "' != '" + false + "'", !boolean76);
    org.junit.Assert.assertTrue("'" + boolean78 + "' != '" + false + "'", !boolean78);
    org.junit.Assert.assertNotNull(date82);
    org.junit.Assert.assertNotNull(date86);
    org.junit.Assert.assertNotNull(date89);
    org.junit.Assert.assertNotNull(timeUnitMap90);
    org.junit.Assert.assertTrue("'" + boolean95 + "' != '" + false + "'", !boolean95);
    org.junit.Assert.assertTrue("'" + int96 + "' != '" + (-1) + "'", int96 == (-1));
  }

  @Test
  public void test1113() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1113");
    }
    java.lang.Short[] shortArray2 = new java.lang.Short[]{(short) 1, (short) 1};
    java.util.ArrayList<java.lang.Short> shortList3 = new java.util.ArrayList<java.lang.Short>();
    boolean boolean4 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Short>) shortList3, shortArray2);
    shortList3.clear();
    boolean boolean6 = shortList3.isEmpty();
    boolean boolean8 = shortList3.equals(5);
    java.lang.String str9 = shortList3.toString();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState13 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double14 = simulatorSpacecraftState13.getLatitude();
    java.lang.String str15 = simulatorSpacecraftState13.getMagField();
    java.lang.String str16 = simulatorSpacecraftState13.getSunVectorAsString();
    boolean boolean17 = shortList3.contains(str16);
    java.util.Iterator<java.lang.Short> shortItor18 = shortList3.iterator();
    org.junit.Assert.assertNotNull(shortArray2);
    org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
    org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + true + "'", boolean6);
    org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + false + "'", !boolean8);
    org.junit.Assert.assertTrue("'" + str9 + "' != '" + "[]" + "'", str9.equals("[]"));
    org.junit.Assert.assertTrue("'" + double14 + "' != '" + 340.0d + "'", double14 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str15 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str15.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str16 + "' != '" + "X:[+.00000] ; Y:[+.00000] ; Z: [+.00000] units [N/A]" + "'",
        str16.equals("X:[+.00000] ; Y:[+.00000] ; Z: [+.00000] units [N/A]"));
    org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + false + "'", !boolean17);
    org.junit.Assert.assertNotNull(shortItor18);
  }

  @Test
  public void test1114() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1114");
    }
    opssat.simulator.util.ArgumentTemplate argumentTemplate2 =
        new opssat.simulator.util.ArgumentTemplate(
            "030936.762", "-100.0000");
    java.lang.String str3 = argumentTemplate2.toString();
    java.lang.String str4 = argumentTemplate2.toString();
    org.junit.Assert.assertTrue("'" + str3 + "' != '" + "030936.762|-100.0000" + "'",
        str3.equals("030936.762|-100.0000"));
    org.junit.Assert.assertTrue("'" + str4 + "' != '" + "030936.762|-100.0000" + "'",
        str4.equals("030936.762|-100.0000"));
  }

  @Test
  public void test1115() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1115");
    }
    org.ccsds.moims.mo.mal.structures.StringList stringList0 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    org.ccsds.moims.mo.mal.structures.ShortList shortList2 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            ' ');
    java.util.ListIterator<java.lang.Short> shortItor3 = shortList2.listIterator();
    boolean boolean4 = stringList0.equals(shortList2);
    org.ccsds.moims.mo.mal.structures.IntegerList integerList6 =
        new org.ccsds.moims.mo.mal.structures.IntegerList(
            48);
    java.lang.Long long7 = integerList6.getShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort8 = integerList6.getAreaNumber();
    int int9 = stringList0.indexOf(integerList6);
    java.lang.Integer int10 = integerList6.getTypeShortForm();
    java.lang.Long long11 = integerList6.getShortForm();
    org.ccsds.moims.mo.mal.structures.OctetList octetList12 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int13 = octetList12.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort14 = octetList12.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet15 = octetList12.getAreaVersion();
    boolean boolean16 = integerList6.equals(uOctet15);
    org.junit.Assert.assertNotNull(shortItor3);
    org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
    org.junit.Assert.assertTrue("'" + long7 + "' != '" + 281475010265077L + "'",
        long7.equals(281475010265077L));
    org.junit.Assert.assertNotNull(uShort8);
    org.junit.Assert.assertTrue("'" + int9 + "' != '" + (-1) + "'", int9 == (-1));
    org.junit.Assert.assertTrue("'" + int10 + "' != '" + (-11) + "'", int10.equals((-11)));
    org.junit.Assert.assertTrue("'" + long11 + "' != '" + 281475010265077L + "'",
        long11.equals(281475010265077L));
    org.junit.Assert.assertTrue("'" + int13 + "' != '" + (-7) + "'", int13.equals((-7)));
    org.junit.Assert.assertNotNull(uShort14);
    org.junit.Assert.assertNotNull(uOctet15);
    org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
  }

  @Test
  public void test1116() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1116");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState11 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double12 = simulatorSpacecraftState11.getLatitude();
    java.lang.String str13 = simulatorSpacecraftState11.getMagField();
    float[] floatArray14 = simulatorSpacecraftState11.getR();
    simulatorSpacecraftState3.setQ(floatArray14);
    java.lang.String str16 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray14);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState20 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double21 = simulatorSpacecraftState20.getLatitude();
    double double22 = simulatorSpacecraftState20.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState26 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double27 = simulatorSpacecraftState26.getLatitude();
    java.lang.String str28 = simulatorSpacecraftState26.getMagField();
    float[] floatArray29 = simulatorSpacecraftState26.getR();
    simulatorSpacecraftState20.setQ(floatArray29);
    float[] floatArray31 = simulatorSpacecraftState20.getV();
    opssat.simulator.celestia.CelestiaData celestiaData32 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray14, floatArray31);
    float[] floatArray33 = celestiaData32.getQ();
    opssat.simulator.util.SimulatorData simulatorData37 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date38 = simulatorData37.getCurrentTime();
    int int39 = opssat.simulator.util.DateExtraction.getDayFromDate(date38);
    opssat.simulator.util.SimulatorData simulatorData41 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date42 = simulatorData41.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData44 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date45 = simulatorData44.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap46 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date42, date45);
    opssat.simulator.util.SimulatorHeader simulatorHeader47 =
        new opssat.simulator.util.SimulatorHeader(
            false, date38, date45);
    opssat.simulator.util.SimulatorData simulatorData48 = new opssat.simulator.util.SimulatorData(
        (short) 0, date38);
    celestiaData32.setDate(date38);
    int int50 = celestiaData32.getSeconds();
    celestiaData32.setDnx("OPS-SAT SoftSim:");
    java.lang.String str53 = celestiaData32.getAos();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState57 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double58 = simulatorSpacecraftState57.getLatitude();
    double double59 = simulatorSpacecraftState57.getLongitude();
    float[] floatArray60 = simulatorSpacecraftState57.getQ();
    celestiaData32.setQ(floatArray60);
    float[] floatArray62 = celestiaData32.getRv();
    int int63 = celestiaData32.getDays();
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + 340.0d + "'", double12 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str13 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str13.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray14);
    org.junit.Assert.assertTrue("'" + str16 + "' != '" + "UnknownGUIData" + "'",
        str16.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double21 + "' != '" + 340.0d + "'", double21 == 340.0d);
    org.junit.Assert.assertTrue("'" + double22 + "' != '" + 340.0d + "'", double22 == 340.0d);
    org.junit.Assert.assertTrue("'" + double27 + "' != '" + 340.0d + "'", double27 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str28 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str28.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray29);
    org.junit.Assert.assertNotNull(floatArray31);
    org.junit.Assert.assertNotNull(floatArray33);
    org.junit.Assert.assertNotNull(date38);
    org.junit.Assert.assertNotNull(date42);
    org.junit.Assert.assertNotNull(date45);
    org.junit.Assert.assertNotNull(timeUnitMap46);
// flaky:         org.junit.Assert.assertTrue("'" + int50 + "' != '" + 47 + "'", int50 == 47);
    org.junit.Assert.assertNull(str53);
    org.junit.Assert.assertTrue("'" + double58 + "' != '" + 340.0d + "'", double58 == 340.0d);
    org.junit.Assert.assertTrue("'" + double59 + "' != '" + (-1.0d) + "'", double59 == (-1.0d));
    org.junit.Assert.assertNotNull(floatArray60);
    org.junit.Assert.assertNotNull(floatArray62);
  }

  @Test
  public void test1117() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1117");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    boolean boolean14 = simulatorHeader12.isAutoStartSystem();
    int int15 = simulatorHeader12.getDayStartDate();
    boolean boolean16 = simulatorHeader12.isUseCelestia();
    opssat.simulator.util.SimulatorData simulatorData19 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date20 = simulatorData19.getCurrentTime();
    int int21 = opssat.simulator.util.DateExtraction.getDayFromDate(date20);
    opssat.simulator.util.SimulatorData simulatorData23 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date24 = simulatorData23.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData26 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date27 = simulatorData26.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap28 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date24, date27);
    opssat.simulator.util.SimulatorHeader simulatorHeader29 =
        new opssat.simulator.util.SimulatorHeader(
            false, date20, date27);
    simulatorHeader12.setStartDate(date27);
    boolean boolean31 = simulatorHeader12.isAutoStartTime();
    simulatorHeader12.setUpdateInternet(false);
    java.lang.String str34 = simulatorHeader12.getOrekitPropagator();
    java.util.Date date35 = simulatorHeader12.getEndDate();
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
    org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
    org.junit.Assert.assertNotNull(date20);
    org.junit.Assert.assertNotNull(date24);
    org.junit.Assert.assertNotNull(date27);
    org.junit.Assert.assertNotNull(timeUnitMap28);
    org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + false + "'", !boolean31);
    org.junit.Assert.assertNull(str34);
    org.junit.Assert.assertNotNull(date35);
  }

  @Test
  public void test1118() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1118");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessWavStreamOperatingBuffer endlessWavStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessWavStreamOperatingBuffer(
            logger0);
    java.lang.Object obj2 = endlessWavStreamOperatingBuffer1.getDataBuffer();
    boolean boolean4 = endlessWavStreamOperatingBuffer1.preparePath("03600.0000000");
    double[] doubleArray6 = endlessWavStreamOperatingBuffer1.getDataAsDoubleArray((short) 10);
    org.junit.Assert.assertNotNull(obj2);
    org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + false + "'", !boolean4);
    org.junit.Assert.assertNotNull(doubleArray6);
  }

  @Test
  public void test1119() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1119");
    }
    java.lang.Boolean[] booleanArray2 = new java.lang.Boolean[]{true, true};
    java.util.ArrayList<java.lang.Boolean> booleanList3 =
        new java.util.ArrayList<java.lang.Boolean>();
    boolean boolean4 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Boolean>) booleanList3, booleanArray2);
    int int6 = booleanList3.indexOf(10);
    booleanList3.clear();
    opssat.simulator.util.DateExtraction dateExtraction8 =
        new opssat.simulator.util.DateExtraction();
    boolean boolean9 = booleanList3.equals(dateExtraction8);
    boolean boolean10 = booleanList3.isEmpty();
    java.util.stream.Stream<java.lang.Boolean> booleanStream11 = booleanList3.parallelStream();
    try {
      java.lang.Boolean boolean13 = booleanList3.remove(50);
      org.junit.Assert.fail(
          "Expected exception of type java.lang.IndexOutOfBoundsException; message: Index: 50, Size: 0");
    } catch (java.lang.IndexOutOfBoundsException e) {
    }
    org.junit.Assert.assertNotNull(booleanArray2);
    org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
    org.junit.Assert.assertTrue("'" + int6 + "' != '" + (-1) + "'", int6 == (-1));
    org.junit.Assert.assertTrue("'" + boolean9 + "' != '" + false + "'", !boolean9);
    org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
    org.junit.Assert.assertNotNull(booleanStream11);
  }

  @Test
  public void test1120() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1120");
    }
    opssat.simulator.util.SimulatorData simulatorData3 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date4 = simulatorData3.getCurrentTime();
    java.util.Date date5 = simulatorData3.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(17,
        date5);
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    int int11 = opssat.simulator.util.DateExtraction.getDayFromDate(date10);
    opssat.simulator.util.SimulatorData simulatorData13 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date14 = simulatorData13.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData16 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date17 = simulatorData16.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap18 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date14, date17);
    opssat.simulator.util.SimulatorHeader simulatorHeader19 =
        new opssat.simulator.util.SimulatorHeader(
            false, date10, date17);
    opssat.simulator.util.SimulatorHeader simulatorHeader20 =
        new opssat.simulator.util.SimulatorHeader(
            false, date5, date10);
    boolean boolean21 = simulatorHeader20.isAutoStartTime();
    simulatorHeader20.setUseCelestia(false);
    boolean boolean24 = simulatorHeader20.isAutoStartSystem();
    int int25 = simulatorHeader20.getMinuteStartDate();
    simulatorHeader20.setOrekitTLE1(
        "SimulatorHeader{autoStartSystem=false, autoStartTime=false, timeFactor=1, startDate=Thu May 23 15:09:50 UTC 2019, endDate=Thu May 23 15:09:50 UTC 2019}");
    org.junit.Assert.assertNotNull(date4);
    org.junit.Assert.assertNotNull(date5);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(date14);
    org.junit.Assert.assertNotNull(date17);
    org.junit.Assert.assertNotNull(timeUnitMap18);
    org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
    org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + false + "'", !boolean24);
  }

  @Test
  public void test1121() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1121");
    }
    opssat.simulator.util.ArgumentTemplate argumentTemplate2 =
        new opssat.simulator.util.ArgumentTemplate(
            "0100.0000", "2019:05:23 15:10:18 UTC");
  }

  @Test
  public void test1122() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1122");
    }
    org.ccsds.moims.mo.mal.structures.StringList stringList0 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    org.ccsds.moims.mo.mal.structures.ShortList shortList2 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            ' ');
    java.util.ListIterator<java.lang.Short> shortItor3 = shortList2.listIterator();
    boolean boolean4 = stringList0.equals(shortList2);
    org.ccsds.moims.mo.mal.structures.UShort uShort5 = stringList0.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.URI uRI7 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    java.lang.Integer int8 = uRI7.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.URI uRI10 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    org.ccsds.moims.mo.mal.structures.UOctet uOctet11 = uRI10.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.URI uRI13 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    org.ccsds.moims.mo.mal.structures.URI[] uRIArray14 =
        new org.ccsds.moims.mo.mal.structures.URI[]{
          uRI7, uRI10, uRI13};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList15 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
    boolean boolean16 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList15, uRIArray14);
    org.ccsds.moims.mo.mal.structures.FineTime fineTime17 =
        new org.ccsds.moims.mo.mal.structures.FineTime();
    int int18 = uRIList15.indexOf(fineTime17);
    uRIList15.ensureCapacity(40);
    org.ccsds.moims.mo.mal.structures.UShort uShort21 =
        org.ccsds.moims.mo.mal.structures.URIList.AREA_SHORT_FORM;
    boolean boolean22 = uRIList15.remove(uShort21);
    java.lang.Byte[] byteArray27 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList28 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean29 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList28, byteArray27);
    java.lang.Integer[] intArray32 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList33 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean34 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList33, intArray32);
    boolean boolean35 = byteList28.retainAll(intList33);
    java.lang.Integer[] intArray38 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList39 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean40 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList39, intArray38);
    int int42 = intList39.lastIndexOf((byte) 10);
    boolean boolean43 = intList33.removeAll(intList39);
    boolean boolean44 = uRIList15.removeAll(intList33);
    boolean boolean45 = stringList0.removeAll(intList33);
    java.util.stream.Stream<java.lang.String> strStream46 = stringList0.parallelStream();
    java.lang.Integer[] intArray54 = new java.lang.Integer[]{(-7), 100, 18, 24, 36, 11111, 15};
    java.util.ArrayList<java.lang.Integer> intList55 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean56 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList55, intArray54);
    intList55.ensureCapacity((-18));
    boolean boolean59 = stringList0.retainAll(intList55);
    org.junit.Assert.assertNotNull(shortItor3);
    org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
    org.junit.Assert.assertNotNull(uShort5);
    org.junit.Assert.assertTrue("'" + int8 + "' != '" + 18 + "'", int8.equals(18));
    org.junit.Assert.assertNotNull(uOctet11);
    org.junit.Assert.assertNotNull(uRIArray14);
    org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + true + "'", boolean16);
    org.junit.Assert.assertTrue("'" + int18 + "' != '" + (-1) + "'", int18 == (-1));
    org.junit.Assert.assertNotNull(uShort21);
    org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + false + "'", !boolean22);
    org.junit.Assert.assertNotNull(byteArray27);
    org.junit.Assert.assertTrue("'" + boolean29 + "' != '" + true + "'", boolean29);
    org.junit.Assert.assertNotNull(intArray32);
    org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + true + "'", boolean34);
    org.junit.Assert.assertTrue("'" + boolean35 + "' != '" + true + "'", boolean35);
    org.junit.Assert.assertNotNull(intArray38);
    org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + true + "'", boolean40);
    org.junit.Assert.assertTrue("'" + int42 + "' != '" + (-1) + "'", int42 == (-1));
    org.junit.Assert.assertTrue("'" + boolean43 + "' != '" + true + "'", boolean43);
    org.junit.Assert.assertTrue("'" + boolean44 + "' != '" + false + "'", !boolean44);
    org.junit.Assert.assertTrue("'" + boolean45 + "' != '" + false + "'", !boolean45);
    org.junit.Assert.assertNotNull(strStream46);
    org.junit.Assert.assertNotNull(intArray54);
    org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + true + "'", boolean56);
    org.junit.Assert.assertTrue("'" + boolean59 + "' != '" + false + "'", !boolean59);
  }

  @Test
  public void test1123() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1123");
    }
    opssat.simulator.util.SimulatorTimer simulatorTimer2 = new opssat.simulator.util.SimulatorTimer(
        "SimulatorData", 1L);
    boolean boolean3 = simulatorTimer2.isElapsed();
    boolean boolean4 = simulatorTimer2.isElapsed();
    org.junit.Assert.assertTrue("'" + boolean3 + "' != '" + false + "'", !boolean3);
    org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + false + "'", !boolean4);
  }

  @Test
  public void test1124() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1124");
    }
    opssat.simulator.util.SimulatorData simulatorData3 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date4 = simulatorData3.getCurrentTime();
    int int5 = opssat.simulator.util.DateExtraction.getDayFromDate(date4);
    opssat.simulator.util.SimulatorData simulatorData7 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date8 = simulatorData7.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData10 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date11 = simulatorData10.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap12 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date8, date11);
    opssat.simulator.util.SimulatorHeader simulatorHeader13 =
        new opssat.simulator.util.SimulatorHeader(
            false, date4, date11);
    opssat.simulator.util.SimulatorData simulatorData14 = new opssat.simulator.util.SimulatorData(
        (short) 0, date4);
    opssat.simulator.util.SimulatorData simulatorData17 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date18 = simulatorData17.getCurrentTime();
    int int19 = opssat.simulator.util.DateExtraction.getDayFromDate(date18);
    opssat.simulator.util.SimulatorData simulatorData21 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date22 = simulatorData21.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData24 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date25 = simulatorData24.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap26 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date22, date25);
    opssat.simulator.util.SimulatorHeader simulatorHeader27 =
        new opssat.simulator.util.SimulatorHeader(
            false, date18, date25);
    java.util.Date date28 = simulatorHeader27.getEndDate();
    opssat.simulator.util.SimulatorData simulatorData32 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date33 = simulatorData32.getCurrentTime();
    java.util.Date date34 = simulatorData32.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData35 = new opssat.simulator.util.SimulatorData(
        17, date34);
    opssat.simulator.util.SimulatorData simulatorData38 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date39 = simulatorData38.getCurrentTime();
    int int40 = opssat.simulator.util.DateExtraction.getDayFromDate(date39);
    opssat.simulator.util.SimulatorData simulatorData42 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date43 = simulatorData42.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData45 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date46 = simulatorData45.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap47 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date43, date46);
    opssat.simulator.util.SimulatorHeader simulatorHeader48 =
        new opssat.simulator.util.SimulatorHeader(
            false, date39, date46);
    opssat.simulator.util.SimulatorHeader simulatorHeader49 =
        new opssat.simulator.util.SimulatorHeader(
            false, date34, date39);
    simulatorHeader27.setEndDate(date34);
    simulatorData14.initFromHeader(simulatorHeader27);
    simulatorData14.toggleSimulatorRunning();
    java.lang.String str53 = simulatorData14.toString();
    org.junit.Assert.assertNotNull(date4);
    org.junit.Assert.assertNotNull(date8);
    org.junit.Assert.assertNotNull(date11);
    org.junit.Assert.assertNotNull(timeUnitMap12);
    org.junit.Assert.assertNotNull(date18);
    org.junit.Assert.assertNotNull(date22);
    org.junit.Assert.assertNotNull(date25);
    org.junit.Assert.assertNotNull(timeUnitMap26);
    org.junit.Assert.assertNotNull(date28);
    org.junit.Assert.assertNotNull(date33);
    org.junit.Assert.assertNotNull(date34);
    org.junit.Assert.assertNotNull(date39);
    org.junit.Assert.assertNotNull(date43);
    org.junit.Assert.assertNotNull(date46);
    org.junit.Assert.assertNotNull(timeUnitMap47);
  }

  @Test
  public void test1125() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1125");
    }
    opssat.simulator.util.SimulatorSchedulerPiece simulatorSchedulerPiece3 =
        new opssat.simulator.util.SimulatorSchedulerPiece(
            13L, 0, "hi!");
    long long4 = simulatorSchedulerPiece3.getTime();
    int int5 = simulatorSchedulerPiece3.getInternalID();
    simulatorSchedulerPiece3.setExecuted(false);
    java.lang.String str8 = simulatorSchedulerPiece3.getFileString();
    simulatorSchedulerPiece3.setExecuted(false);
    java.lang.String str11 = simulatorSchedulerPiece3.getFileString();
    org.junit.Assert.assertTrue("'" + long4 + "' != '" + 13L + "'", long4 == 13L);
    org.junit.Assert.assertTrue("'" + int5 + "' != '" + 0 + "'", int5 == 0);
    org.junit.Assert.assertTrue(
        "'" + str8 + "' != '" + "00000:00:00:00:013|0000000000000000013|0|hi!" + "'",
        str8.equals("00000:00:00:00:013|0000000000000000013|0|hi!"));
    org.junit.Assert.assertTrue(
        "'" + str11 + "' != '" + "00000:00:00:00:013|0000000000000000013|0|hi!" + "'",
        str11.equals("00000:00:00:00:013|0000000000000000013|0|hi!"));
  }

  @Test
  public void test1126() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1126");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState3.setLongitude(4);
    int int6 = simulatorSpacecraftState3.getSatsInView();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState10 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double11 = simulatorSpacecraftState10.getLatitude();
    double[] doubleArray12 = simulatorSpacecraftState10.getSunVector();
    simulatorSpacecraftState3.setMagnetometer(doubleArray12);
    org.ccsds.moims.mo.mal.structures.StringList stringList14 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    java.util.Iterator<java.lang.String> strItor15 = stringList14.iterator();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet16 = stringList14.getAreaVersion();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState20 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray23 = new float[]{28, 8};
    simulatorSpacecraftState20.setQ(floatArray23);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState28 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double29 = simulatorSpacecraftState28.getLatitude();
    java.lang.String str30 = simulatorSpacecraftState28.getMagField();
    float[] floatArray31 = simulatorSpacecraftState28.getR();
    simulatorSpacecraftState20.setQ(floatArray31);
    java.lang.String str33 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray31);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState37 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double38 = simulatorSpacecraftState37.getLatitude();
    double double39 = simulatorSpacecraftState37.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState43 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double44 = simulatorSpacecraftState43.getLatitude();
    java.lang.String str45 = simulatorSpacecraftState43.getMagField();
    float[] floatArray46 = simulatorSpacecraftState43.getR();
    simulatorSpacecraftState37.setQ(floatArray46);
    float[] floatArray48 = simulatorSpacecraftState37.getV();
    opssat.simulator.celestia.CelestiaData celestiaData49 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray31, floatArray48);
    float[] floatArray50 = celestiaData49.getQ();
    opssat.simulator.util.SimulatorData simulatorData54 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date55 = simulatorData54.getCurrentTime();
    int int56 = opssat.simulator.util.DateExtraction.getDayFromDate(date55);
    opssat.simulator.util.SimulatorData simulatorData58 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date59 = simulatorData58.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData61 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date62 = simulatorData61.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap63 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date59, date62);
    opssat.simulator.util.SimulatorHeader simulatorHeader64 =
        new opssat.simulator.util.SimulatorHeader(
            false, date55, date62);
    opssat.simulator.util.SimulatorData simulatorData65 = new opssat.simulator.util.SimulatorData(
        (short) 0, date55);
    celestiaData49.setDate(date55);
    int int67 = celestiaData49.getSeconds();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState71 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray74 = new float[]{28, 8};
    simulatorSpacecraftState71.setQ(floatArray74);
    celestiaData49.setQ(floatArray74);
    int int77 = stringList14.lastIndexOf(celestiaData49);
    celestiaData49.setDate("00000:00:00:00:097  11                 executed false   | ");
    float[] floatArray80 = celestiaData49.getQ();
    simulatorSpacecraftState3.setQ(floatArray80);
    float[] floatArray82 = simulatorSpacecraftState3.getV();
    org.junit.Assert.assertTrue("'" + int6 + "' != '" + 0 + "'", int6 == 0);
    org.junit.Assert.assertTrue("'" + double11 + "' != '" + 340.0d + "'", double11 == 340.0d);
    org.junit.Assert.assertNotNull(doubleArray12);
    org.junit.Assert.assertNotNull(strItor15);
    org.junit.Assert.assertNotNull(uOctet16);
    org.junit.Assert.assertNotNull(floatArray23);
    org.junit.Assert.assertTrue("'" + double29 + "' != '" + 340.0d + "'", double29 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str30 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str30.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray31);
    org.junit.Assert.assertTrue("'" + str33 + "' != '" + "UnknownGUIData" + "'",
        str33.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double38 + "' != '" + 340.0d + "'", double38 == 340.0d);
    org.junit.Assert.assertTrue("'" + double39 + "' != '" + 340.0d + "'", double39 == 340.0d);
    org.junit.Assert.assertTrue("'" + double44 + "' != '" + 340.0d + "'", double44 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str45 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str45.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray46);
    org.junit.Assert.assertNotNull(floatArray48);
    org.junit.Assert.assertNotNull(floatArray50);
    org.junit.Assert.assertNotNull(date55);
    org.junit.Assert.assertNotNull(date59);
    org.junit.Assert.assertNotNull(date62);
    org.junit.Assert.assertNotNull(timeUnitMap63);
    org.junit.Assert.assertNotNull(floatArray74);
    org.junit.Assert.assertTrue("'" + int77 + "' != '" + (-1) + "'", int77 == (-1));
    org.junit.Assert.assertNotNull(floatArray80);
    org.junit.Assert.assertNotNull(floatArray82);
  }

  @Test
  public void test1127() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1127");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessSingleStreamOperatingBuffer endlessSingleStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessSingleStreamOperatingBuffer(
            logger0);
    byte[] byteArray3 = endlessSingleStreamOperatingBuffer1.getDataAsByteArray('4');
    byte[] byteArray5 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS.int16_2ByteArray(15);
    endlessSingleStreamOperatingBuffer1.setDataFromByteArray(byteArray5);
    java.lang.String str7 = endlessSingleStreamOperatingBuffer1.getDataBufferAsString();
    java.lang.String str8 = endlessSingleStreamOperatingBuffer1.getDataBufferAsString();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor11 =
        new opssat.simulator.util.ArgumentDescriptor(
            "281474993487885", "[-1.0, 100.0, 10.0, 10.0]");
    java.lang.String str12 = argumentDescriptor11.getName();
    endlessSingleStreamOperatingBuffer1.setDataBuffer(str12);
    int int14 = endlessSingleStreamOperatingBuffer1.getOperatingIndex();
    java.lang.Object obj15 = endlessSingleStreamOperatingBuffer1.getDataBuffer();
    org.junit.Assert.assertNotNull(byteArray3);
    org.junit.Assert.assertNotNull(byteArray5);
    org.junit.Assert.assertTrue("'" + str7 + "' != '" + "byte[] {0x00,0x0F}" + "'",
        str7.equals("byte[] {0x00,0x0F}"));
    org.junit.Assert.assertTrue("'" + str8 + "' != '" + "byte[] {0x00,0x0F}" + "'",
        str8.equals("byte[] {0x00,0x0F}"));
    org.junit.Assert.assertTrue(
        "'" + str12 + "' != '" + "UnknownDeviceDataTypeString{281474993487885}" + "'",
        str12.equals("UnknownDeviceDataTypeString{281474993487885}"));
    org.junit.Assert.assertTrue("'" + int14 + "' != '" + 0 + "'", int14 == 0);
    org.junit.Assert.assertTrue(
        "'" + obj15 + "' != '" + "UnknownDeviceDataTypeString{281474993487885}" + "'",
        obj15.equals("UnknownDeviceDataTypeString{281474993487885}"));
  }

  @Test
  public void test1128() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1128");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState11 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double12 = simulatorSpacecraftState11.getLatitude();
    java.lang.String str13 = simulatorSpacecraftState11.getMagField();
    float[] floatArray14 = simulatorSpacecraftState11.getR();
    simulatorSpacecraftState3.setQ(floatArray14);
    java.lang.String str16 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray14);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState20 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double21 = simulatorSpacecraftState20.getLatitude();
    double double22 = simulatorSpacecraftState20.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState26 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double27 = simulatorSpacecraftState26.getLatitude();
    java.lang.String str28 = simulatorSpacecraftState26.getMagField();
    float[] floatArray29 = simulatorSpacecraftState26.getR();
    simulatorSpacecraftState20.setQ(floatArray29);
    float[] floatArray31 = simulatorSpacecraftState20.getV();
    opssat.simulator.celestia.CelestiaData celestiaData32 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray14, floatArray31);
    float[] floatArray33 = celestiaData32.getQ();
    opssat.simulator.util.SimulatorData simulatorData37 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date38 = simulatorData37.getCurrentTime();
    int int39 = opssat.simulator.util.DateExtraction.getDayFromDate(date38);
    opssat.simulator.util.SimulatorData simulatorData41 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date42 = simulatorData41.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData44 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date45 = simulatorData44.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap46 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date42, date45);
    opssat.simulator.util.SimulatorHeader simulatorHeader47 =
        new opssat.simulator.util.SimulatorHeader(
            false, date38, date45);
    opssat.simulator.util.SimulatorData simulatorData48 = new opssat.simulator.util.SimulatorData(
        (short) 0, date38);
    celestiaData32.setDate(date38);
    int int50 = celestiaData32.getSeconds();
    int int51 = celestiaData32.getDays();
    java.lang.String str52 = celestiaData32.getDate();
    int int53 = celestiaData32.getDays();
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + 340.0d + "'", double12 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str13 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str13.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray14);
    org.junit.Assert.assertTrue("'" + str16 + "' != '" + "UnknownGUIData" + "'",
        str16.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double21 + "' != '" + 340.0d + "'", double21 == 340.0d);
    org.junit.Assert.assertTrue("'" + double22 + "' != '" + 340.0d + "'", double22 == 340.0d);
    org.junit.Assert.assertTrue("'" + double27 + "' != '" + 340.0d + "'", double27 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str28 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str28.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray29);
    org.junit.Assert.assertNotNull(floatArray31);
    org.junit.Assert.assertNotNull(floatArray33);
    org.junit.Assert.assertNotNull(date38);
    org.junit.Assert.assertNotNull(date42);
    org.junit.Assert.assertNotNull(date45);
    org.junit.Assert.assertNotNull(timeUnitMap46);
  }

  @Test
  public void test1129() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1129");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessWavStreamOperatingBuffer endlessWavStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessWavStreamOperatingBuffer(
            logger0);
    opssat.simulator.util.SimulatorData simulatorData4 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date5 = simulatorData4.getCurrentTime();
    int int6 = opssat.simulator.util.DateExtraction.getDayFromDate(date5);
    opssat.simulator.util.SimulatorData simulatorData8 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date9 = simulatorData8.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData11 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date12 = simulatorData11.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap13 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date9, date12);
    opssat.simulator.util.SimulatorHeader simulatorHeader14 =
        new opssat.simulator.util.SimulatorHeader(
            false, date5, date12);
    java.util.Date date15 = simulatorHeader14.getEndDate();
    int int16 = simulatorHeader14.getMinuteStartDate();
    simulatorHeader14.setUseOrekitPropagator(true);
    java.lang.String str19 = simulatorHeader14.toFileString();
    java.util.Date date21 = simulatorHeader14.parseStringIntoDate("yyyy:MM:dd HH:mm:ss z");
    endlessWavStreamOperatingBuffer1.setDataBuffer("yyyy:MM:dd HH:mm:ss z");
    boolean boolean24 = endlessWavStreamOperatingBuffer1.preparePath("");
    java.lang.Object obj25 = endlessWavStreamOperatingBuffer1.getDataBuffer();
    org.junit.Assert.assertNotNull(date5);
    org.junit.Assert.assertNotNull(date9);
    org.junit.Assert.assertNotNull(date12);
    org.junit.Assert.assertNotNull(timeUnitMap13);
    org.junit.Assert.assertNotNull(date15);
    org.junit.Assert.assertNull(date21);
    org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + false + "'", !boolean24);
    org.junit.Assert.assertTrue("'" + obj25 + "' != '" + "yyyy:MM:dd HH:mm:ss z" + "'",
        obj25.equals("yyyy:MM:dd HH:mm:ss z"));
  }

  @Test
  public void test1131() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1131");
    }
    org.ccsds.moims.mo.mal.structures.UShortList uShortList0 =
        new org.ccsds.moims.mo.mal.structures.UShortList();
    java.lang.Integer int1 = uShortList0.getTypeShortForm();
    java.lang.Integer int2 = uShortList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.OctetList octetList4 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int5 = octetList4.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort6 = octetList4.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet7 = octetList4.getAreaVersion();
    opssat.simulator.util.wav.WavFileException wavFileException9 =
        new opssat.simulator.util.wav.WavFileException(
            "UnknownGUIData");
    java.lang.Throwable[] throwableArray10 = wavFileException9.getSuppressed();
    boolean boolean11 = octetList4.equals(wavFileException9);
    org.ccsds.moims.mo.mal.structures.FloatList floatList13 =
        new org.ccsds.moims.mo.mal.structures.FloatList(
            (byte) 0);
    java.lang.Integer int14 = floatList13.getTypeShortForm();
    java.lang.Integer int15 = floatList13.getTypeShortForm();
    floatList13.clear();
    boolean boolean17 = octetList4.remove(floatList13);
    org.ccsds.moims.mo.mal.structures.UShort uShort18 = floatList13.getServiceNumber();
    try {
      uShortList0.add(14, uShort18);
      org.junit.Assert.fail(
          "Expected exception of type java.lang.IndexOutOfBoundsException; message: Index: 14, Size: 0");
    } catch (java.lang.IndexOutOfBoundsException e) {
    }
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-10) + "'", int1.equals((-10)));
    org.junit.Assert.assertTrue("'" + int2 + "' != '" + (-10) + "'", int2.equals((-10)));
    org.junit.Assert.assertTrue("'" + int5 + "' != '" + (-7) + "'", int5.equals((-7)));
    org.junit.Assert.assertNotNull(uShort6);
    org.junit.Assert.assertNotNull(uOctet7);
    org.junit.Assert.assertNotNull(throwableArray10);
    org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + false + "'", !boolean11);
    org.junit.Assert.assertTrue("'" + int14 + "' != '" + (-4) + "'", int14.equals((-4)));
    org.junit.Assert.assertTrue("'" + int15 + "' != '" + (-4) + "'", int15.equals((-4)));
    org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + false + "'", !boolean17);
    org.junit.Assert.assertNotNull(uShort18);
  }

  @Test
  public void test1132() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1132");
    }
    opssat.simulator.util.wav.WavFileException wavFileException1 =
        new opssat.simulator.util.wav.WavFileException(
            "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}");
    opssat.simulator.util.wav.WavFileException wavFileException2 =
        new opssat.simulator.util.wav.WavFileException();
    org.ccsds.moims.mo.mal.structures.OctetList octetList3 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int4 = octetList3.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort5 = octetList3.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet6 = octetList3.getAreaVersion();
    opssat.simulator.util.wav.WavFileException wavFileException8 =
        new opssat.simulator.util.wav.WavFileException(
            "UnknownGUIData");
    java.lang.Throwable[] throwableArray9 = wavFileException8.getSuppressed();
    boolean boolean10 = octetList3.equals(wavFileException8);
    wavFileException2.addSuppressed(wavFileException8);
    wavFileException1.addSuppressed(wavFileException8);
    opssat.simulator.util.wav.WavFileException wavFileException13 =
        new opssat.simulator.util.wav.WavFileException(
            wavFileException8);
    opssat.simulator.util.wav.WavFileException wavFileException15 =
        new opssat.simulator.util.wav.WavFileException(
            "00000:00:00:00:008");
    wavFileException8.addSuppressed(wavFileException15);
    opssat.simulator.util.wav.WavFileException wavFileException18 =
        new opssat.simulator.util.wav.WavFileException(
            "UnknownGUIData");
    opssat.simulator.util.wav.WavFileException wavFileException19 =
        new opssat.simulator.util.wav.WavFileException(
            wavFileException18);
    wavFileException8.addSuppressed(wavFileException18);
    java.lang.String str21 = wavFileException8.toString();
    java.lang.String str22 = wavFileException8.toString();
    org.junit.Assert.assertTrue("'" + int4 + "' != '" + (-7) + "'", int4.equals((-7)));
    org.junit.Assert.assertNotNull(uShort5);
    org.junit.Assert.assertNotNull(uOctet6);
    org.junit.Assert.assertNotNull(throwableArray9);
    org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + false + "'", !boolean10);
    org.junit.Assert.assertTrue(
        "'" + str21 + "' != '" + "opssat.simulator.util.wav.WavFileException: UnknownGUIData" + "'",
        str21.equals("opssat.simulator.util.wav.WavFileException: UnknownGUIData"));
    org.junit.Assert.assertTrue(
        "'" + str22 + "' != '" + "opssat.simulator.util.wav.WavFileException: UnknownGUIData" + "'",
        str22.equals("opssat.simulator.util.wav.WavFileException: UnknownGUIData"));
  }

  @Test
  public void test1133() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1133");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double4 = simulatorSpacecraftState3.getLatitude();
    double[] doubleArray5 = simulatorSpacecraftState3.getSunVector();
    org.ccsds.moims.mo.mal.structures.URI uRI7 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    java.lang.Integer int8 = uRI7.getTypeShortForm();
    java.lang.String str9 = uRI7.toString();
    java.lang.String str10 = uRI7.getValue();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet11 = uRI7.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.ShortList shortList13 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            ' ');
    java.lang.Long long14 = shortList13.getShortForm();
    java.util.stream.Stream<java.lang.Short> shortStream15 = shortList13.stream();
    boolean boolean16 = uOctet11.equals(shortList13);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState20 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray23 = new float[]{28, 8};
    simulatorSpacecraftState20.setQ(floatArray23);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState28 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double29 = simulatorSpacecraftState28.getLatitude();
    java.lang.String str30 = simulatorSpacecraftState28.getMagField();
    float[] floatArray31 = simulatorSpacecraftState28.getR();
    simulatorSpacecraftState20.setQ(floatArray31);
    java.lang.String str33 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray31);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState37 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double38 = simulatorSpacecraftState37.getLatitude();
    double double39 = simulatorSpacecraftState37.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState43 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double44 = simulatorSpacecraftState43.getLatitude();
    java.lang.String str45 = simulatorSpacecraftState43.getMagField();
    float[] floatArray46 = simulatorSpacecraftState43.getR();
    simulatorSpacecraftState37.setQ(floatArray46);
    float[] floatArray48 = simulatorSpacecraftState37.getV();
    opssat.simulator.celestia.CelestiaData celestiaData49 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray31, floatArray48);
    float[] floatArray50 = celestiaData49.getQ();
    opssat.simulator.util.SimulatorData simulatorData54 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date55 = simulatorData54.getCurrentTime();
    int int56 = opssat.simulator.util.DateExtraction.getDayFromDate(date55);
    opssat.simulator.util.SimulatorData simulatorData58 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date59 = simulatorData58.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData61 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date62 = simulatorData61.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap63 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date59, date62);
    opssat.simulator.util.SimulatorHeader simulatorHeader64 =
        new opssat.simulator.util.SimulatorHeader(
            false, date55, date62);
    opssat.simulator.util.SimulatorData simulatorData65 = new opssat.simulator.util.SimulatorData(
        (short) 0, date55);
    celestiaData49.setDate(date55);
    int int67 = celestiaData49.getSeconds();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState71 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray74 = new float[]{28, 8};
    simulatorSpacecraftState71.setQ(floatArray74);
    celestiaData49.setQ(floatArray74);
    boolean boolean77 = shortList13.contains(celestiaData49);
    int int78 = celestiaData49.getYears();
    float[] floatArray79 = celestiaData49.getQ();
    simulatorSpacecraftState3.setRv(floatArray79);
    org.junit.Assert.assertTrue("'" + double4 + "' != '" + 340.0d + "'", double4 == 340.0d);
    org.junit.Assert.assertNotNull(doubleArray5);
    org.junit.Assert.assertTrue("'" + int8 + "' != '" + 18 + "'", int8.equals(18));
    org.junit.Assert.assertTrue("'" + str9 + "' != '" + "0100.0000" + "'",
        str9.equals("0100.0000"));
    org.junit.Assert.assertTrue("'" + str10 + "' != '" + "0100.0000" + "'",
        str10.equals("0100.0000"));
    org.junit.Assert.assertNotNull(uOctet11);
    org.junit.Assert.assertTrue("'" + long14 + "' != '" + 281475010265079L + "'",
        long14.equals(281475010265079L));
    org.junit.Assert.assertNotNull(shortStream15);
    org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
    org.junit.Assert.assertNotNull(floatArray23);
    org.junit.Assert.assertTrue("'" + double29 + "' != '" + 340.0d + "'", double29 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str30 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str30.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray31);
    org.junit.Assert.assertTrue("'" + str33 + "' != '" + "UnknownGUIData" + "'",
        str33.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double38 + "' != '" + 340.0d + "'", double38 == 340.0d);
    org.junit.Assert.assertTrue("'" + double39 + "' != '" + 340.0d + "'", double39 == 340.0d);
    org.junit.Assert.assertTrue("'" + double44 + "' != '" + 340.0d + "'", double44 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str45 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str45.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray46);
    org.junit.Assert.assertNotNull(floatArray48);
    org.junit.Assert.assertNotNull(floatArray50);
    org.junit.Assert.assertNotNull(date55);
    org.junit.Assert.assertNotNull(date59);
    org.junit.Assert.assertNotNull(date62);
    org.junit.Assert.assertNotNull(timeUnitMap63);
    org.junit.Assert.assertNotNull(floatArray74);
    org.junit.Assert.assertTrue("'" + boolean77 + "' != '" + false + "'", !boolean77);

    int thisYear = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).getYear();

    org.junit.Assert.assertTrue("'" + int78 + "' != '" + thisYear + "'", int78 == thisYear);
    org.junit.Assert.assertNotNull(floatArray79);
  }

  @Test
  public void test1134() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1134");
    }
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor2 =
        new opssat.simulator.util.ArgumentDescriptor(
            "",
            "#Run the processing of internal models\nstartModels=false\n#Increment the simulated time (depends on startModels)\nstartTime=false\n#Speed up of time factor\ntimeFactor=1\n#Kepler elements for orbit A[km];E;i[deg];RAAN[deg];ARG_PER[deg];TRUE_A[deg] 7021.0;0.0;98.05;340.0;0.0;0.0\nkeplerElements=7021.0;0.0;98.05;340.0;0.0;0.0\n#Enable the Orekit library\norekit=false\n#Enable updates from Internet (used for gps constellation TLEs)\nupdateFromInternet=false\n#Configuration of the Celestia server\ncelestia=false\ncelestiaPort=0\n#Start and end dates of simulation\nstartDate=2019:05:23 15:09:43 UTC\nendDate=2019:05:23 15:09:43 UTC\n#Logging level to files found in $USER_HOME/.ops-sat-simulator/\n#Possible values SEVERE,INFO,FINE,FINER,FINEST,ALL\ncentralLogLevel=INFO\nsimulatorLogLevel=INFO\nconsoleLogLevel=INFO");
    java.lang.String str3 = argumentDescriptor2.getName();
    java.lang.Object obj4 = argumentDescriptor2.getType();
    org.junit.Assert.assertTrue("'" + str3 + "' != '" + "UnknownDeviceDataTypeString{}" + "'",
        str3.equals("UnknownDeviceDataTypeString{}"));
    org.junit.Assert.assertNull(obj4);
  }

  @Test
  public void test1135() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1135");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessSingleStreamOperatingBuffer endlessSingleStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessSingleStreamOperatingBuffer(
            logger0);
    byte[] byteArray3 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS.float2ByteArray(64);
    endlessSingleStreamOperatingBuffer1.setDataFromByteArray(byteArray3);
    int int5 = endlessSingleStreamOperatingBuffer1.getOperatingIndex();
    boolean boolean7 = endlessSingleStreamOperatingBuffer1.preparePath("");
    byte[] byteArray9 = endlessSingleStreamOperatingBuffer1.getDataAsByteArray(100);
    org.junit.Assert.assertNotNull(byteArray3);
    org.junit.Assert.assertTrue("'" + int5 + "' != '" + 0 + "'", int5 == 0);
    org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
    org.junit.Assert.assertNotNull(byteArray9);
  }

  @Test
  public void test1136() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1136");
    }
    java.lang.Short[] shortArray2 = new java.lang.Short[]{(short) -1, (short) 10};
    java.util.ArrayList<java.lang.Short> shortList3 = new java.util.ArrayList<java.lang.Short>();
    boolean boolean4 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Short>) shortList3, shortArray2);
    boolean boolean5 = shortList3.isEmpty();
    java.lang.Object obj6 = null;
    boolean boolean7 = shortList3.contains(obj6);
    shortList3.trimToSize();
    org.ccsds.moims.mo.mal.structures.ShortList shortList10 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            ' ');
    int int11 = shortList10.size();
    org.ccsds.moims.mo.mal.structures.Element element12 = shortList10.createElement();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet13 =
        org.ccsds.moims.mo.mal.structures.FloatList.AREA_VERSION;
    java.lang.Integer int14 = uOctet13.getTypeShortForm();
    boolean boolean15 = shortList10.contains(int14);
    org.ccsds.moims.mo.mal.structures.UShort uShort16 =
        org.ccsds.moims.mo.mal.structures.StringList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.OctetList octetList17 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int18 = octetList17.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort19 = octetList17.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UShort uShort20 =
        org.ccsds.moims.mo.mal.structures.OctetList.AREA_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort21 =
        org.ccsds.moims.mo.mal.structures.FloatList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort22 =
        org.ccsds.moims.mo.mal.structures.StringList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort23 =
        org.ccsds.moims.mo.mal.structures.URIList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort24 =
        org.ccsds.moims.mo.mal.structures.BooleanList.AREA_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort25 =
        org.ccsds.moims.mo.mal.structures.OctetList.AREA_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort26 =
        org.ccsds.moims.mo.mal.structures.URIList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort[] uShortArray27 =
        new org.ccsds.moims.mo.mal.structures.UShort[]{
          uShort26};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList28 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
    boolean boolean29 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList28,
        uShortArray27);
    uShortList28.ensureCapacity(0);
    int int33 = uShortList28.indexOf((byte) 1);
    uShortList28.clear();
    java.lang.Long[] longArray37 = new java.lang.Long[]{0L, 10L};
    java.util.ArrayList<java.lang.Long> longList38 = new java.util.ArrayList<java.lang.Long>();
    boolean boolean39 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Long>) longList38, longArray37);
    java.lang.Object obj40 = longList38.clone();
    boolean boolean41 = uShortList28.contains(longList38);
    org.ccsds.moims.mo.mal.structures.UShort uShort42 =
        org.ccsds.moims.mo.mal.structures.UShortList.SERVICE_SHORT_FORM;
    boolean boolean43 = uShortList28.add(uShort42);
    org.ccsds.moims.mo.mal.structures.UShort[] uShortArray44 =
        new org.ccsds.moims.mo.mal.structures.UShort[]{
          uShort16, uShort19, uShort20, uShort21, uShort22, uShort23, uShort24, uShort25, uShort42};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList45 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
    boolean boolean46 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList45,
        uShortArray44);
    uShortList45.ensureCapacity(100);
    boolean boolean49 = shortList10.contains(100);
    org.ccsds.moims.mo.mal.structures.UShort uShort50 = shortList10.getAreaNumber();
    java.lang.Object[] objArray51 = shortList10.toArray();
    boolean boolean52 = shortList3.remove(shortList10);
    org.ccsds.moims.mo.mal.structures.Element element53 = shortList10.createElement();
    java.lang.String str54 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(shortList10);
    org.junit.Assert.assertNotNull(shortArray2);
    org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
    org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + false + "'", !boolean5);
    org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
    org.junit.Assert.assertTrue("'" + int11 + "' != '" + 0 + "'", int11 == 0);
    org.junit.Assert.assertNotNull(element12);
    org.junit.Assert.assertNotNull(uOctet13);
    org.junit.Assert.assertTrue("'" + int14 + "' != '" + 8 + "'", int14.equals(8));
    org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
    org.junit.Assert.assertNotNull(uShort16);
    org.junit.Assert.assertTrue("'" + int18 + "' != '" + (-7) + "'", int18.equals((-7)));
    org.junit.Assert.assertNotNull(uShort19);
    org.junit.Assert.assertNotNull(uShort20);
    org.junit.Assert.assertNotNull(uShort21);
    org.junit.Assert.assertNotNull(uShort22);
    org.junit.Assert.assertNotNull(uShort23);
    org.junit.Assert.assertNotNull(uShort24);
    org.junit.Assert.assertNotNull(uShort25);
    org.junit.Assert.assertNotNull(uShort26);
    org.junit.Assert.assertNotNull(uShortArray27);
    org.junit.Assert.assertTrue("'" + boolean29 + "' != '" + true + "'", boolean29);
    org.junit.Assert.assertTrue("'" + int33 + "' != '" + (-1) + "'", int33 == (-1));
    org.junit.Assert.assertNotNull(longArray37);
    org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + true + "'", boolean39);
    org.junit.Assert.assertNotNull(obj40);
    org.junit.Assert.assertTrue("'" + boolean41 + "' != '" + false + "'", !boolean41);
    org.junit.Assert.assertNotNull(uShort42);
    org.junit.Assert.assertTrue("'" + boolean43 + "' != '" + true + "'", boolean43);
    org.junit.Assert.assertNotNull(uShortArray44);
    org.junit.Assert.assertTrue("'" + boolean46 + "' != '" + true + "'", boolean46);
    org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + false + "'", !boolean49);
    org.junit.Assert.assertNotNull(uShort50);
    org.junit.Assert.assertNotNull(objArray51);
    org.junit.Assert.assertTrue("'" + boolean52 + "' != '" + false + "'", !boolean52);
    org.junit.Assert.assertNotNull(element53);
    org.junit.Assert.assertTrue("'" + str54 + "' != '" + "UnknownGUIData" + "'",
        str54.equals("UnknownGUIData"));
  }

  @Test
  public void test1137() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1137");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double4 = simulatorSpacecraftState3.getLatitude();
    double double5 = simulatorSpacecraftState3.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState9 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double10 = simulatorSpacecraftState9.getLatitude();
    java.lang.String str11 = simulatorSpacecraftState9.getMagField();
    float[] floatArray12 = simulatorSpacecraftState9.getR();
    simulatorSpacecraftState3.setQ(floatArray12);
    float[] floatArray14 = simulatorSpacecraftState3.getQ();
    simulatorSpacecraftState3.setAltitude(0.0d);
    float[] floatArray17 = simulatorSpacecraftState3.getQ();
    org.junit.Assert.assertTrue("'" + double4 + "' != '" + 340.0d + "'", double4 == 340.0d);
    org.junit.Assert.assertTrue("'" + double5 + "' != '" + 340.0d + "'", double5 == 340.0d);
    org.junit.Assert.assertTrue("'" + double10 + "' != '" + 340.0d + "'", double10 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str11 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str11.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray12);
    org.junit.Assert.assertNotNull(floatArray14);
    org.junit.Assert.assertNotNull(floatArray17);
  }

  @Test
  public void test1138() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1138");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList1 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int2 = octetList1.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort3 = octetList1.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet4 = octetList1.getAreaVersion();
    opssat.simulator.util.wav.WavFileException wavFileException6 =
        new opssat.simulator.util.wav.WavFileException(
            "UnknownGUIData");
    java.lang.Throwable[] throwableArray7 = wavFileException6.getSuppressed();
    boolean boolean8 = octetList1.equals(wavFileException6);
    opssat.simulator.util.wav.WavFileException wavFileException9 =
        new opssat.simulator.util.wav.WavFileException(
            "#Run the processing of internal models\nstartModels=false\n#Increment the simulated time (depends on startModels)\nstartTime=false\n#Speed up of time factor\ntimeFactor=1\n#Kepler elements for orbit A[km];E;i[deg];RAAN[deg];ARG_PER[deg];TRUE_A[deg] 7021.0;0.0;98.05;340.0;0.0;0.0\nkeplerElements=7021.0;0.0;98.05;340.0;0.0;0.0\n#Enable the Orekit library\norekit=false\n#Enable updates from Internet (used for gps constellation TLEs)\nupdateFromInternet=false\n#Configuration of the Celestia server\ncelestia=false\ncelestiaPort=0\n#Start and end dates of simulation\nstartDate=2019:05:23 15:10:10 UTC\nendDate=2019:05:23 15:10:10 UTC\n#Logging level to files found in $USER_HOME/.ops-sat-simulator/\n#Possible values SEVERE,INFO,FINE,FINER,FINEST,ALL\ncentralLogLevel=INFO\nsimulatorLogLevel=INFO\nconsoleLogLevel=INFO",
            wavFileException6);
    java.lang.Throwable[] throwableArray10 = wavFileException6.getSuppressed();
    org.junit.Assert.assertTrue("'" + int2 + "' != '" + (-7) + "'", int2.equals((-7)));
    org.junit.Assert.assertNotNull(uShort3);
    org.junit.Assert.assertNotNull(uOctet4);
    org.junit.Assert.assertNotNull(throwableArray7);
    org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + false + "'", !boolean8);
    org.junit.Assert.assertNotNull(throwableArray10);
  }

  @Test
  public void test1139() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1139");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessWavStreamOperatingBuffer endlessWavStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessWavStreamOperatingBuffer(
            logger0);
    java.lang.Object obj2 = endlessWavStreamOperatingBuffer1.getDataBuffer();
    endlessWavStreamOperatingBuffer1.setOperatingIndex(39);
    org.junit.Assert.assertNotNull(obj2);
  }

  @Test
  public void test1140() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1140");
    }
    opssat.simulator.util.SimulatorData simulatorData3 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date4 = simulatorData3.getCurrentTime();
    java.util.Date date5 = simulatorData3.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(17,
        date5);
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    int int11 = opssat.simulator.util.DateExtraction.getDayFromDate(date10);
    opssat.simulator.util.SimulatorData simulatorData13 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date14 = simulatorData13.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData16 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date17 = simulatorData16.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap18 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date14, date17);
    opssat.simulator.util.SimulatorHeader simulatorHeader19 =
        new opssat.simulator.util.SimulatorHeader(
            false, date10, date17);
    opssat.simulator.util.SimulatorHeader simulatorHeader20 =
        new opssat.simulator.util.SimulatorHeader(
            false, date5, date10);
    boolean boolean21 = simulatorHeader20.isAutoStartTime();
    simulatorHeader20.setUseCelestia(false);
    simulatorHeader20.setCelestiaPort((short) 0);
    simulatorHeader20.setUseCelestia(true);
    int int28 = simulatorHeader20.getMinuteStartDate();
    opssat.simulator.util.SimulatorData simulatorData31 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date32 = simulatorData31.getCurrentTime();
    int int33 = opssat.simulator.util.DateExtraction.getDayFromDate(date32);
    opssat.simulator.util.SimulatorData simulatorData35 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date36 = simulatorData35.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData38 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date39 = simulatorData38.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap40 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date36, date39);
    opssat.simulator.util.SimulatorHeader simulatorHeader41 =
        new opssat.simulator.util.SimulatorHeader(
            false, date32, date39);
    java.util.Date date42 = simulatorHeader41.getEndDate();
    simulatorHeader41.setOrekitTLE1("");
    java.util.Date date46 = simulatorHeader41.parseStringIntoDate("2019/05/23-15:09:35");
    java.lang.String str47 = simulatorHeader41.toFileString();
    java.util.Date date48 = simulatorHeader41.getEndDate();
    simulatorHeader20.setStartDate(date48);
    simulatorHeader20.setAutoStartSystem(true);
    java.lang.String str52 = simulatorHeader20.getEndDateString();
    org.junit.Assert.assertNotNull(date4);
    org.junit.Assert.assertNotNull(date5);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(date14);
    org.junit.Assert.assertNotNull(date17);
    org.junit.Assert.assertNotNull(timeUnitMap18);
    org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
    org.junit.Assert.assertNotNull(date32);
    org.junit.Assert.assertNotNull(date36);
    org.junit.Assert.assertNotNull(date39);
    org.junit.Assert.assertNotNull(timeUnitMap40);
    org.junit.Assert.assertNotNull(date42);
    org.junit.Assert.assertNull(date46);
    org.junit.Assert.assertNotNull(date48);
  }

  @Test
  public void test1141() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1141");
    }
    org.ccsds.moims.mo.mal.structures.StringList stringList0 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    org.ccsds.moims.mo.mal.structures.ShortList shortList2 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            ' ');
    java.util.ListIterator<java.lang.Short> shortItor3 = shortList2.listIterator();
    boolean boolean4 = stringList0.equals(shortList2);
    java.util.stream.BaseStream[] baseStreamArray6 = new java.util.stream.BaseStream[0];
    @SuppressWarnings("unchecked")
    java.util.stream.BaseStream<java.lang.Float, java.util.stream.Stream<java.lang.Float>>[] floatBaseStreamArray7 =
        baseStreamArray6;
    java.util.stream.BaseStream<java.lang.Float, java.util.stream.Stream<java.lang.Float>>[] floatBaseStreamArray8 =
        stringList0
            .toArray(
                (java.util.stream.BaseStream<java.lang.Float, java.util.stream.Stream<java.lang.Float>>[]) baseStreamArray6);
    java.util.stream.Stream<java.lang.String> strStream9 = stringList0.stream();
    java.lang.Object obj10 = stringList0.clone();
    opssat.simulator.util.LoggerFormatter loggerFormatter11 =
        new opssat.simulator.util.LoggerFormatter();
    opssat.simulator.util.LoggerFormatter loggerFormatter12 =
        new opssat.simulator.util.LoggerFormatter();
    opssat.simulator.util.LoggerFormatter loggerFormatter13 =
        new opssat.simulator.util.LoggerFormatter();
    opssat.simulator.util.LoggerFormatter[] loggerFormatterArray14 =
        new opssat.simulator.util.LoggerFormatter[]{
          loggerFormatter11, loggerFormatter12, loggerFormatter13};
    opssat.simulator.util.LoggerFormatter[] loggerFormatterArray15 = stringList0
        .toArray(loggerFormatterArray14);
    java.util.stream.Stream<java.lang.String> strStream16 = stringList0.parallelStream();
    org.ccsds.moims.mo.mal.structures.UShort uShort17 = stringList0.getServiceNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet18 = uShort17.getAreaVersion();
    org.junit.Assert.assertNotNull(shortItor3);
    org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
    org.junit.Assert.assertNotNull(baseStreamArray6);
    org.junit.Assert.assertNotNull(floatBaseStreamArray7);
    org.junit.Assert.assertNotNull(floatBaseStreamArray8);
    org.junit.Assert.assertNotNull(strStream9);
    org.junit.Assert.assertNotNull(obj10);
    org.junit.Assert.assertNotNull(loggerFormatterArray14);
    org.junit.Assert.assertNotNull(loggerFormatterArray15);
    org.junit.Assert.assertNotNull(strStream16);
    org.junit.Assert.assertNotNull(uShort17);
    org.junit.Assert.assertNotNull(uOctet18);
  }

  @Test
  public void test1142() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1142");
    }
    opssat.simulator.util.ArgumentTemplate argumentTemplate2 =
        new opssat.simulator.util.ArgumentTemplate(
            "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]", "opssat");
    opssat.simulator.util.ArgumentTemplate argumentTemplate5 =
        new opssat.simulator.util.ArgumentTemplate(
            "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]", "opssat");
    int int6 = argumentTemplate2.compareTo(argumentTemplate5);
    java.lang.String str7 = argumentTemplate2.toString();
    argumentTemplate2.setArgContent("");
    java.lang.String str10 = argumentTemplate2.getArgContent();
    org.junit.Assert.assertTrue("'" + int6 + "' != '" + 0 + "'", int6 == 0);
    org.junit.Assert.assertTrue("'" + str7 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]|opssat" + "'",
        str7.equals(
            "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]|opssat"));
    org.junit.Assert.assertTrue("'" + str10 + "' != '" + "" + "'", str10.equals(""));
  }

  @Test
  public void test1143() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1143");
    }
    java.lang.Float[] floatArray3 = new java.lang.Float[]{1.0f, 1.0f, (-1.0f)};
    java.util.ArrayList<java.lang.Float> floatList4 = new java.util.ArrayList<java.lang.Float>();
    boolean boolean5 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Float>) floatList4, floatArray3);
    boolean boolean7 = floatList4.add((-1.0f));
    floatList4.clear();
    java.util.stream.Stream<java.lang.Float> floatStream9 = floatList4.stream();
    org.ccsds.moims.mo.mal.structures.StringList stringList10 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    opssat.simulator.threading.SimulatorNode.DevDatPBind devDatPBind11 =
        opssat.simulator.threading.SimulatorNode.DevDatPBind.FineADCS_ReactionWheels;
    boolean boolean12 = stringList10.equals(devDatPBind11);
    org.ccsds.moims.mo.mal.structures.Element element13 = stringList10.createElement();
    java.util.Iterator<java.lang.String> strItor14 = stringList10.iterator();
    boolean boolean15 = floatList4.contains(stringList10);
    org.ccsds.moims.mo.mal.structures.FloatList floatList17 =
        new org.ccsds.moims.mo.mal.structures.FloatList(
            (byte) 0);
    java.lang.Integer int18 = floatList17.getTypeShortForm();
    floatList17.trimToSize();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet20 = floatList17.getAreaVersion();
    java.lang.Integer int21 = floatList17.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort22 =
        org.ccsds.moims.mo.mal.structures.StringList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.OctetList octetList23 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int24 = octetList23.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort25 = octetList23.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UShort uShort26 =
        org.ccsds.moims.mo.mal.structures.OctetList.AREA_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort27 =
        org.ccsds.moims.mo.mal.structures.FloatList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort28 =
        org.ccsds.moims.mo.mal.structures.StringList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort29 =
        org.ccsds.moims.mo.mal.structures.URIList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort30 =
        org.ccsds.moims.mo.mal.structures.BooleanList.AREA_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort31 =
        org.ccsds.moims.mo.mal.structures.OctetList.AREA_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort32 =
        org.ccsds.moims.mo.mal.structures.URIList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort[] uShortArray33 =
        new org.ccsds.moims.mo.mal.structures.UShort[]{
          uShort32};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList34 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
    boolean boolean35 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList34,
        uShortArray33);
    uShortList34.ensureCapacity(0);
    int int39 = uShortList34.indexOf((byte) 1);
    uShortList34.clear();
    java.lang.Long[] longArray43 = new java.lang.Long[]{0L, 10L};
    java.util.ArrayList<java.lang.Long> longList44 = new java.util.ArrayList<java.lang.Long>();
    boolean boolean45 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Long>) longList44, longArray43);
    java.lang.Object obj46 = longList44.clone();
    boolean boolean47 = uShortList34.contains(longList44);
    org.ccsds.moims.mo.mal.structures.UShort uShort48 =
        org.ccsds.moims.mo.mal.structures.UShortList.SERVICE_SHORT_FORM;
    boolean boolean49 = uShortList34.add(uShort48);
    org.ccsds.moims.mo.mal.structures.UShort[] uShortArray50 =
        new org.ccsds.moims.mo.mal.structures.UShort[]{
          uShort22, uShort25, uShort26, uShort27, uShort28, uShort29, uShort30, uShort31, uShort48};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList51 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
    boolean boolean52 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList51,
        uShortArray50);
    uShortList51.ensureCapacity(100);
    int int55 = uShortList51.size();
    org.ccsds.moims.mo.mal.structures.OctetList octetList56 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    octetList56.ensureCapacity(13);
    java.lang.Byte[] byteArray63 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList64 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean65 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList64, byteArray63);
    java.lang.Integer[] intArray68 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList69 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean70 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList69, intArray68);
    boolean boolean71 = byteList64.retainAll(intList69);
    boolean boolean72 = octetList56.containsAll(intList69);
    java.util.Iterator<java.lang.Integer> intItor73 = intList69.iterator();
    boolean boolean74 = uShortList51.containsAll(intList69);
    boolean boolean75 = floatList17.containsAll(intList69);
    boolean boolean76 = stringList10.removeAll(intList69);
    try {
      java.util.List<java.lang.String> strList79 = stringList10.subList(100, 45);
      org.junit.Assert.fail(
          "Expected exception of type java.lang.IndexOutOfBoundsException; message: toIndex = 45");
    } catch (java.lang.IndexOutOfBoundsException e) {
    }
    org.junit.Assert.assertNotNull(floatArray3);
    org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
    org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + true + "'", boolean7);
    org.junit.Assert.assertNotNull(floatStream9);
    org.junit.Assert.assertTrue(
        "'" + devDatPBind11 + "' != '"
        + opssat.simulator.threading.SimulatorNode.DevDatPBind.FineADCS_ReactionWheels + "'",
        devDatPBind11
            .equals(opssat.simulator.threading.SimulatorNode.DevDatPBind.FineADCS_ReactionWheels));
    org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + false + "'", !boolean12);
    org.junit.Assert.assertNotNull(element13);
    org.junit.Assert.assertNotNull(strItor14);
    org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
    org.junit.Assert.assertTrue("'" + int18 + "' != '" + (-4) + "'", int18.equals((-4)));
    org.junit.Assert.assertNotNull(uOctet20);
    org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-4) + "'", int21.equals((-4)));
    org.junit.Assert.assertNotNull(uShort22);
    org.junit.Assert.assertTrue("'" + int24 + "' != '" + (-7) + "'", int24.equals((-7)));
    org.junit.Assert.assertNotNull(uShort25);
    org.junit.Assert.assertNotNull(uShort26);
    org.junit.Assert.assertNotNull(uShort27);
    org.junit.Assert.assertNotNull(uShort28);
    org.junit.Assert.assertNotNull(uShort29);
    org.junit.Assert.assertNotNull(uShort30);
    org.junit.Assert.assertNotNull(uShort31);
    org.junit.Assert.assertNotNull(uShort32);
    org.junit.Assert.assertNotNull(uShortArray33);
    org.junit.Assert.assertTrue("'" + boolean35 + "' != '" + true + "'", boolean35);
    org.junit.Assert.assertTrue("'" + int39 + "' != '" + (-1) + "'", int39 == (-1));
    org.junit.Assert.assertNotNull(longArray43);
    org.junit.Assert.assertTrue("'" + boolean45 + "' != '" + true + "'", boolean45);
    org.junit.Assert.assertNotNull(obj46);
    org.junit.Assert.assertTrue("'" + boolean47 + "' != '" + false + "'", !boolean47);
    org.junit.Assert.assertNotNull(uShort48);
    org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + true + "'", boolean49);
    org.junit.Assert.assertNotNull(uShortArray50);
    org.junit.Assert.assertTrue("'" + boolean52 + "' != '" + true + "'", boolean52);
    org.junit.Assert.assertTrue("'" + int55 + "' != '" + 9 + "'", int55 == 9);
    org.junit.Assert.assertNotNull(byteArray63);
    org.junit.Assert.assertTrue("'" + boolean65 + "' != '" + true + "'", boolean65);
    org.junit.Assert.assertNotNull(intArray68);
    org.junit.Assert.assertTrue("'" + boolean70 + "' != '" + true + "'", boolean70);
    org.junit.Assert.assertTrue("'" + boolean71 + "' != '" + true + "'", boolean71);
    org.junit.Assert.assertTrue("'" + boolean72 + "' != '" + false + "'", !boolean72);
    org.junit.Assert.assertNotNull(intItor73);
    org.junit.Assert.assertTrue("'" + boolean74 + "' != '" + false + "'", !boolean74);
    org.junit.Assert.assertTrue("'" + boolean75 + "' != '" + false + "'", !boolean75);
    org.junit.Assert.assertTrue("'" + boolean76 + "' != '" + false + "'", !boolean76);
  }

  @Test
  public void test1144() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1144");
    }
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap1 =
        opssat.simulator.util.SimulatorData
            .computeTimeUnit(281475010265086L);
    org.junit.Assert.assertNotNull(timeUnitMap1);
  }

  @Test
  public void test1145() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1145");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessSingleStreamOperatingBuffer endlessSingleStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessSingleStreamOperatingBuffer(
            logger0);
    byte[] byteArray3 = endlessSingleStreamOperatingBuffer1.getDataAsByteArray('4');
    java.lang.Object obj4 = null;
    endlessSingleStreamOperatingBuffer1.setDataBuffer(obj4);
    boolean boolean7 = endlessSingleStreamOperatingBuffer1.preparePath("*36");
    org.junit.Assert.assertNotNull(byteArray3);
    org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
  }

  @Test
  public void test1146() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1146");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double4 = simulatorSpacecraftState3.getLatitude();
    simulatorSpacecraftState3.setLongitude(13);
    simulatorSpacecraftState3.setAltitude(66);
    simulatorSpacecraftState3.setAltitude(31);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState14 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray17 = new float[]{28, 8};
    simulatorSpacecraftState14.setQ(floatArray17);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState22 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double23 = simulatorSpacecraftState22.getLatitude();
    java.lang.String str24 = simulatorSpacecraftState22.getMagField();
    float[] floatArray25 = simulatorSpacecraftState22.getR();
    simulatorSpacecraftState14.setQ(floatArray25);
    java.lang.String str27 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray25);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState31 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double32 = simulatorSpacecraftState31.getLatitude();
    double double33 = simulatorSpacecraftState31.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState37 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double38 = simulatorSpacecraftState37.getLatitude();
    java.lang.String str39 = simulatorSpacecraftState37.getMagField();
    float[] floatArray40 = simulatorSpacecraftState37.getR();
    simulatorSpacecraftState31.setQ(floatArray40);
    float[] floatArray42 = simulatorSpacecraftState31.getV();
    opssat.simulator.celestia.CelestiaData celestiaData43 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray25, floatArray42);
    celestiaData43.setDate(":");
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState49 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray52 = new float[]{28, 8};
    simulatorSpacecraftState49.setQ(floatArray52);
    float[] floatArray54 = simulatorSpacecraftState49.getR();
    celestiaData43.setQ(floatArray54);
    simulatorSpacecraftState3.setRv(floatArray54);
    simulatorSpacecraftState3.setLatitude(14);
    java.lang.String str59 = simulatorSpacecraftState3.toString();
    org.junit.Assert.assertTrue("'" + double4 + "' != '" + 340.0d + "'", double4 == 340.0d);
    org.junit.Assert.assertNotNull(floatArray17);
    org.junit.Assert.assertTrue("'" + double23 + "' != '" + 340.0d + "'", double23 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str24 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str24.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray25);
    org.junit.Assert.assertTrue("'" + str27 + "' != '" + "UnknownGUIData" + "'",
        str27.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double32 + "' != '" + 340.0d + "'", double32 == 340.0d);
    org.junit.Assert.assertTrue("'" + double33 + "' != '" + 340.0d + "'", double33 == 340.0d);
    org.junit.Assert.assertTrue("'" + double38 + "' != '" + 340.0d + "'", double38 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str39 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str39.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray40);
    org.junit.Assert.assertNotNull(floatArray42);
    org.junit.Assert.assertNotNull(floatArray52);
    org.junit.Assert.assertNotNull(floatArray54);
    org.junit.Assert.assertTrue(
        "'" + str59 + "' != '"
        + "SimulatorSpacecraftState{latitude=14.0, longitude=13.0, altitude=31.0}" + "'",
        str59.equals("SimulatorSpacecraftState{latitude=14.0, longitude=13.0, altitude=31.0}"));
  }

  @Test
  public void test1147() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1147");
    }
    org.ccsds.moims.mo.mal.structures.UShort uShort0 =
        org.ccsds.moims.mo.mal.structures.URIList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort[] uShortArray1 =
        new org.ccsds.moims.mo.mal.structures.UShort[]{
          uShort0};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList2 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
    boolean boolean3 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList2, uShortArray1);
    uShortList2.ensureCapacity(0);
    int int7 = uShortList2.indexOf((byte) 1);
    uShortList2.clear();
    java.lang.Long[] longArray11 = new java.lang.Long[]{0L, 10L};
    java.util.ArrayList<java.lang.Long> longList12 = new java.util.ArrayList<java.lang.Long>();
    boolean boolean13 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Long>) longList12, longArray11);
    java.lang.Object obj14 = longList12.clone();
    boolean boolean15 = uShortList2.contains(longList12);
    java.util.ListIterator<java.lang.Long> longItor16 = longList12.listIterator();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState20 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double21 = simulatorSpacecraftState20.getLatitude();
    java.lang.String str22 = simulatorSpacecraftState20.getMagField();
    java.lang.String str23 = simulatorSpacecraftState20.toString();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState27 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double28 = simulatorSpacecraftState27.getLatitude();
    java.lang.String str29 = simulatorSpacecraftState27.getMagField();
    java.lang.String str30 = simulatorSpacecraftState27.toString();
    double[] doubleArray31 = simulatorSpacecraftState27.getSunVector();
    simulatorSpacecraftState20.setMagnetometer(doubleArray31);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState36 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double37 = simulatorSpacecraftState36.getLatitude();
    java.lang.String str38 = simulatorSpacecraftState36.getMagField();
    java.lang.String str39 = simulatorSpacecraftState36.toString();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState43 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double44 = simulatorSpacecraftState43.getLatitude();
    java.lang.String str45 = simulatorSpacecraftState43.getMagField();
    java.lang.String str46 = simulatorSpacecraftState43.toString();
    double[] doubleArray47 = simulatorSpacecraftState43.getSunVector();
    simulatorSpacecraftState36.setMagnetometer(doubleArray47);
    simulatorSpacecraftState20.setMagnetometer(doubleArray47);
    float[] floatArray50 = null;
    simulatorSpacecraftState20.setRv(floatArray50);
    int int52 = longList12.lastIndexOf(simulatorSpacecraftState20);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState56 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState56.setLongitude(4);
    int int59 = simulatorSpacecraftState56.getSatsInView();
    float[] floatArray60 = simulatorSpacecraftState56.getRv();
    boolean boolean61 = longList12.equals(floatArray60);
    int int62 = longList12.size();
    java.util.Iterator<java.lang.Long> longItor63 = longList12.iterator();
    org.junit.Assert.assertNotNull(uShort0);
    org.junit.Assert.assertNotNull(uShortArray1);
    org.junit.Assert.assertTrue("'" + boolean3 + "' != '" + true + "'", boolean3);
    org.junit.Assert.assertTrue("'" + int7 + "' != '" + (-1) + "'", int7 == (-1));
    org.junit.Assert.assertNotNull(longArray11);
    org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
    org.junit.Assert.assertNotNull(obj14);
    org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
    org.junit.Assert.assertNotNull(longItor16);
    org.junit.Assert.assertTrue("'" + double21 + "' != '" + 340.0d + "'", double21 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str22 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str22.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str23 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str23.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertTrue("'" + double28 + "' != '" + 340.0d + "'", double28 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str29 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str29.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str30 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str30.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray31);
    org.junit.Assert.assertTrue("'" + double37 + "' != '" + 340.0d + "'", double37 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str38 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str38.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str39 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str39.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertTrue("'" + double44 + "' != '" + 340.0d + "'", double44 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str45 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str45.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str46 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str46.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray47);
    org.junit.Assert.assertTrue("'" + int52 + "' != '" + (-1) + "'", int52 == (-1));
    org.junit.Assert.assertTrue("'" + int59 + "' != '" + 0 + "'", int59 == 0);
    org.junit.Assert.assertNotNull(floatArray60);
    org.junit.Assert.assertTrue("'" + boolean61 + "' != '" + false + "'", !boolean61);
    org.junit.Assert.assertTrue("'" + int62 + "' != '" + 2 + "'", int62 == 2);
    org.junit.Assert.assertNotNull(longItor63);
  }

  @Test
  public void test1148() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1148");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessSingleStreamOperatingBuffer endlessSingleStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessSingleStreamOperatingBuffer(
            logger0);
    byte[] byteArray3 = endlessSingleStreamOperatingBuffer1.getDataAsByteArray('4');
    byte[] byteArray5 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS.int16_2ByteArray(15);
    endlessSingleStreamOperatingBuffer1.setDataFromByteArray(byteArray5);
    int int7 = endlessSingleStreamOperatingBuffer1.getOperatingIndex();
    byte[] byteArray9 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS.int2ByteArray(' ');
    endlessSingleStreamOperatingBuffer1.setDataFromByteArray(byteArray9);
    org.ccsds.moims.mo.mal.structures.UShort uShort11 =
        org.ccsds.moims.mo.mal.structures.URIList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort[] uShortArray12 =
        new org.ccsds.moims.mo.mal.structures.UShort[]{
          uShort11};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList13 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
    boolean boolean14 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList13,
        uShortArray12);
    uShortList13.ensureCapacity(0);
    int int18 = uShortList13.indexOf((byte) 1);
    uShortList13.clear();
    java.lang.Long[] longArray22 = new java.lang.Long[]{0L, 10L};
    java.util.ArrayList<java.lang.Long> longList23 = new java.util.ArrayList<java.lang.Long>();
    boolean boolean24 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Long>) longList23, longArray22);
    java.lang.Object obj25 = longList23.clone();
    int int26 = uShortList13.lastIndexOf(obj25);
    java.lang.Byte[] byteArray31 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList32 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean33 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList32, byteArray31);
    java.lang.Integer[] intArray36 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList37 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean38 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList37, intArray36);
    boolean boolean39 = byteList32.retainAll(intList37);
    java.lang.Integer[] intArray42 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList43 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean44 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList43, intArray42);
    int int46 = intList43.lastIndexOf((byte) 10);
    boolean boolean47 = intList37.retainAll(intList43);
    java.lang.Object obj48 = intList37.clone();
    boolean boolean49 = uShortList13.removeAll(intList37);
    java.lang.Object[] objArray50 = uShortList13.toArray();
    java.util.Spliterator<org.ccsds.moims.mo.mal.structures.UShort> uShortSpliterator51 =
        uShortList13
            .spliterator();
    java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream52 = uShortList13
        .parallelStream();
    endlessSingleStreamOperatingBuffer1.setDataBuffer(uShortStream52);
    byte[] byteArray55 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS
        .int16_2ByteArray((-2));
    endlessSingleStreamOperatingBuffer1.setDataFromByteArray(byteArray55);
    org.junit.Assert.assertNotNull(byteArray3);
    org.junit.Assert.assertNotNull(byteArray5);
    org.junit.Assert.assertTrue("'" + int7 + "' != '" + 0 + "'", int7 == 0);
    org.junit.Assert.assertNotNull(byteArray9);
    org.junit.Assert.assertNotNull(uShort11);
    org.junit.Assert.assertNotNull(uShortArray12);
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + true + "'", boolean14);
    org.junit.Assert.assertTrue("'" + int18 + "' != '" + (-1) + "'", int18 == (-1));
    org.junit.Assert.assertNotNull(longArray22);
    org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + true + "'", boolean24);
    org.junit.Assert.assertNotNull(obj25);
    org.junit.Assert.assertTrue("'" + int26 + "' != '" + (-1) + "'", int26 == (-1));
    org.junit.Assert.assertNotNull(byteArray31);
    org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + true + "'", boolean33);
    org.junit.Assert.assertNotNull(intArray36);
    org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + true + "'", boolean38);
    org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + true + "'", boolean39);
    org.junit.Assert.assertNotNull(intArray42);
    org.junit.Assert.assertTrue("'" + boolean44 + "' != '" + true + "'", boolean44);
    org.junit.Assert.assertTrue("'" + int46 + "' != '" + (-1) + "'", int46 == (-1));
    org.junit.Assert.assertTrue("'" + boolean47 + "' != '" + true + "'", boolean47);
    org.junit.Assert.assertNotNull(obj48);
    org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + false + "'", !boolean49);
    org.junit.Assert.assertNotNull(objArray50);
    org.junit.Assert.assertNotNull(uShortSpliterator51);
    org.junit.Assert.assertNotNull(uShortStream52);
    org.junit.Assert.assertNotNull(byteArray55);
  }

  @Test
  public void test1149() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1149");
    }
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience8 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            281475010265078L, 281474993487886L, 7, 281474993487889L, 10.0f, 0L, 56, (-36));
  }

  @Test
  public void test1150() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1150");
    }
    java.lang.String str1 = opssat.simulator.threading.SimulatorNode.calcNMEAChecksum("*63");
    org.junit.Assert.assertTrue("'" + str1 + "' != '" + "*05" + "'", str1.equals("*05"));
  }

  @Test
  public void test1151() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1151");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet3 = octetList0.getAreaVersion();
    opssat.simulator.util.wav.WavFileException wavFileException5 =
        new opssat.simulator.util.wav.WavFileException(
            "UnknownGUIData");
    java.lang.Throwable[] throwableArray6 = wavFileException5.getSuppressed();
    boolean boolean7 = octetList0.equals(wavFileException5);
    org.ccsds.moims.mo.mal.structures.FloatList floatList9 =
        new org.ccsds.moims.mo.mal.structures.FloatList(
            (byte) 0);
    java.lang.Integer int10 = floatList9.getTypeShortForm();
    java.lang.Integer int11 = floatList9.getTypeShortForm();
    floatList9.clear();
    boolean boolean13 = octetList0.remove(floatList9);
    boolean boolean15 = floatList9.add((-1.0f));
    java.util.Iterator<java.lang.Float> floatItor16 = floatList9.iterator();
    java.util.Spliterator<java.lang.Float> floatSpliterator17 = floatList9.spliterator();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState21 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double22 = simulatorSpacecraftState21.getLatitude();
    double double23 = simulatorSpacecraftState21.getLongitude();
    java.lang.String str24 = simulatorSpacecraftState21.getRotationAsString();
    boolean boolean25 = floatList9.contains(str24);
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(uOctet3);
    org.junit.Assert.assertNotNull(throwableArray6);
    org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
    org.junit.Assert.assertTrue("'" + int10 + "' != '" + (-4) + "'", int10.equals((-4)));
    org.junit.Assert.assertTrue("'" + int11 + "' != '" + (-4) + "'", int11.equals((-4)));
    org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + false + "'", !boolean13);
    org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + true + "'", boolean15);
    org.junit.Assert.assertNotNull(floatItor16);
    org.junit.Assert.assertNotNull(floatSpliterator17);
    org.junit.Assert.assertTrue("'" + double22 + "' != '" + 340.0d + "'", double22 == 340.0d);
    org.junit.Assert.assertTrue("'" + double23 + "' != '" + (-1.0d) + "'", double23 == (-1.0d));
    org.junit.Assert.assertTrue("'" + str24 + "' != '"
        + "\n+0.000000 +0.000000 +0.000000\n+0.000000 +0.000000 +0.000000\n+0.000000 +0.000000 +0.000000"
        + "'",
        str24.equals(
            "\n+0.000000 +0.000000 +0.000000\n+0.000000 +0.000000 +0.000000\n+0.000000 +0.000000 +0.000000"));
    org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + false + "'", !boolean25);
  }

  @Test
  public void test1152() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1152");
    }
    opssat.simulator.util.SimulatorData simulatorData3 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date4 = simulatorData3.getCurrentTime();
    int int5 = opssat.simulator.util.DateExtraction.getDayFromDate(date4);
    opssat.simulator.util.SimulatorData simulatorData7 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date8 = simulatorData7.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData10 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date11 = simulatorData10.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap12 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date8, date11);
    opssat.simulator.util.SimulatorHeader simulatorHeader13 =
        new opssat.simulator.util.SimulatorHeader(
            false, date4, date11);
    java.util.Date date14 = simulatorHeader13.getEndDate();
    opssat.simulator.util.SimulatorData simulatorData15 = new opssat.simulator.util.SimulatorData(
        13, date14);
    simulatorData15.incrementMethods();
    java.lang.String str17 = simulatorData15.getCurrentYear();
    java.lang.String str18 = simulatorData15.getCurrentDay();
    simulatorData15.setCounter(23);
    opssat.simulator.util.SimulatorData simulatorData23 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date24 = simulatorData23.getCurrentTime();
    int int25 = opssat.simulator.util.DateExtraction.getDayFromDate(date24);
    opssat.simulator.util.SimulatorData simulatorData27 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date28 = simulatorData27.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData30 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date31 = simulatorData30.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap32 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date28, date31);
    opssat.simulator.util.SimulatorHeader simulatorHeader33 =
        new opssat.simulator.util.SimulatorHeader(
            false, date24, date31);
    java.util.Date date34 = simulatorHeader33.getEndDate();
    int int35 = simulatorHeader33.getMinuteStartDate();
    simulatorHeader33.setUseOrekitPropagator(true);
    simulatorHeader33.setUseCelestia(false);
    boolean boolean40 = simulatorHeader33.isAutoStartTime();
    java.lang.String str41 = simulatorHeader33.getStartDateString();
    simulatorData15.initFromHeader(simulatorHeader33);
    java.lang.String str43 = simulatorHeader33.getStartDateString();
    org.junit.Assert.assertNotNull(date4);
    org.junit.Assert.assertNotNull(date8);
    org.junit.Assert.assertNotNull(date11);
    org.junit.Assert.assertNotNull(timeUnitMap12);
    org.junit.Assert.assertNotNull(date14);
    org.junit.Assert.assertNotNull(date24);
    org.junit.Assert.assertNotNull(date28);
    org.junit.Assert.assertNotNull(date31);
    org.junit.Assert.assertNotNull(timeUnitMap32);
    org.junit.Assert.assertNotNull(date34);
    org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + false + "'", !boolean40);
  }

  @Test
  public void test1153() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1153");
    }
    opssat.simulator.util.SimulatorSchedulerPiece simulatorSchedulerPiece3 =
        new opssat.simulator.util.SimulatorSchedulerPiece(
            13L, 0, "hi!");
    long long4 = simulatorSchedulerPiece3.getTime();
    long long5 = simulatorSchedulerPiece3.getTime();
    simulatorSchedulerPiece3.setExecuted(true);
    org.junit.Assert.assertTrue("'" + long4 + "' != '" + 13L + "'", long4 == 13L);
    org.junit.Assert.assertTrue("'" + long5 + "' != '" + 13L + "'", long5 == 13L);
  }

  @Test
  public void test1154() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1154");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double4 = simulatorSpacecraftState3.getLatitude();
    java.lang.String str5 = simulatorSpacecraftState3.getMagField();
    java.lang.String str6 = simulatorSpacecraftState3.toString();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState10 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double11 = simulatorSpacecraftState10.getLatitude();
    double double12 = simulatorSpacecraftState10.getLongitude();
    java.lang.String str13 = simulatorSpacecraftState10.getModeOperation();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState17 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState17.setLongitude(4);
    double[] doubleArray21 = new double[]{(-1.0f)};
    simulatorSpacecraftState17.setMagField(doubleArray21);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState26 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double27 = simulatorSpacecraftState26.getLatitude();
    java.lang.String str28 = simulatorSpacecraftState26.getMagField();
    java.lang.String str29 = simulatorSpacecraftState26.toString();
    double[] doubleArray30 = simulatorSpacecraftState26.getSunVector();
    simulatorSpacecraftState17.setMagnetometer(doubleArray30);
    simulatorSpacecraftState10.setMagField(doubleArray30);
    simulatorSpacecraftState3.setMagField(doubleArray30);
    simulatorSpacecraftState3.setSatsInView(44);
    org.ccsds.moims.mo.mal.structures.UInteger uInteger37 =
        new org.ccsds.moims.mo.mal.structures.UInteger(
            13);
    org.ccsds.moims.mo.mal.structures.Element element38 = uInteger37.createElement();
    long long39 = uInteger37.getValue();
    long long40 = uInteger37.getValue();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet41 = uInteger37.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.UShort uShort42 = uInteger37.getAreaNumber();
    java.lang.String str43 = uInteger37.toString();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState47 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double48 = simulatorSpacecraftState47.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState52 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray55 = new float[]{28, 8};
    simulatorSpacecraftState52.setQ(floatArray55);
    simulatorSpacecraftState47.setRv(floatArray55);
    double[][] doubleArray58 = new double[][]{};
    simulatorSpacecraftState47.setRotation(doubleArray58);
    boolean boolean60 = uInteger37.equals(doubleArray58);
    simulatorSpacecraftState3.setRotation(doubleArray58);
    org.junit.Assert.assertTrue("'" + double4 + "' != '" + 340.0d + "'", double4 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str5 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str5.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str6 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str6.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertTrue("'" + double11 + "' != '" + 340.0d + "'", double11 == 340.0d);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + (-1.0d) + "'", double12 == (-1.0d));
    org.junit.Assert.assertNull(str13);
    org.junit.Assert.assertNotNull(doubleArray21);
    org.junit.Assert.assertTrue("'" + double27 + "' != '" + 340.0d + "'", double27 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str28 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str28.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str29 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str29.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray30);
    org.junit.Assert.assertNotNull(element38);
    org.junit.Assert.assertTrue("'" + long39 + "' != '" + 13L + "'", long39 == 13L);
    org.junit.Assert.assertTrue("'" + long40 + "' != '" + 13L + "'", long40 == 13L);
    org.junit.Assert.assertNotNull(uOctet41);
    org.junit.Assert.assertNotNull(uShort42);
    org.junit.Assert.assertTrue("'" + str43 + "' != '" + "13" + "'", str43.equals("13"));
    org.junit.Assert.assertTrue("'" + double48 + "' != '" + 340.0d + "'", double48 == 340.0d);
    org.junit.Assert.assertNotNull(floatArray55);
    org.junit.Assert.assertNotNull(doubleArray58);
    org.junit.Assert.assertTrue("'" + boolean60 + "' != '" + false + "'", !boolean60);
  }

  @Test
  public void test1155() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1155");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessSingleStreamOperatingBuffer endlessSingleStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessSingleStreamOperatingBuffer(
            logger0);
    byte[] byteArray3 = endlessSingleStreamOperatingBuffer1.getDataAsByteArray('4');
    java.lang.Object obj4 = null;
    endlessSingleStreamOperatingBuffer1.setDataBuffer(obj4);
    int int6 = endlessSingleStreamOperatingBuffer1.getOperatingIndex();
    org.junit.Assert.assertNotNull(byteArray3);
    org.junit.Assert.assertTrue("'" + int6 + "' != '" + 0 + "'", int6 == 0);
  }

  @Test
  public void test1156() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1156");
    }
    org.ccsds.moims.mo.mal.structures.URIList uRIList1 =
        new org.ccsds.moims.mo.mal.structures.URIList(
            11);
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = uRIList1.getServiceNumber();
    java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.URI> uRIStream3 = uRIList1.stream();
    org.ccsds.moims.mo.mal.structures.Duration duration5 =
        new org.ccsds.moims.mo.mal.structures.Duration(
            0.0d);
    org.ccsds.moims.mo.mal.structures.UOctet uOctet6 = duration5.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.UShort uShort7 = duration5.getAreaNumber();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState11 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray14 = new float[]{28, 8};
    simulatorSpacecraftState11.setQ(floatArray14);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState19 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double20 = simulatorSpacecraftState19.getLatitude();
    java.lang.String str21 = simulatorSpacecraftState19.getMagField();
    float[] floatArray22 = simulatorSpacecraftState19.getR();
    simulatorSpacecraftState11.setQ(floatArray22);
    java.lang.String str24 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray22);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState28 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double29 = simulatorSpacecraftState28.getLatitude();
    double double30 = simulatorSpacecraftState28.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState34 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double35 = simulatorSpacecraftState34.getLatitude();
    java.lang.String str36 = simulatorSpacecraftState34.getMagField();
    float[] floatArray37 = simulatorSpacecraftState34.getR();
    simulatorSpacecraftState28.setQ(floatArray37);
    float[] floatArray39 = simulatorSpacecraftState28.getV();
    opssat.simulator.celestia.CelestiaData celestiaData40 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray22, floatArray39);
    float[] floatArray41 = celestiaData40.getQ();
    opssat.simulator.util.SimulatorData simulatorData45 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date46 = simulatorData45.getCurrentTime();
    int int47 = opssat.simulator.util.DateExtraction.getDayFromDate(date46);
    opssat.simulator.util.SimulatorData simulatorData49 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date50 = simulatorData49.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData52 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date53 = simulatorData52.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap54 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date50, date53);
    opssat.simulator.util.SimulatorHeader simulatorHeader55 =
        new opssat.simulator.util.SimulatorHeader(
            false, date46, date53);
    opssat.simulator.util.SimulatorData simulatorData56 = new opssat.simulator.util.SimulatorData(
        (short) 0, date46);
    celestiaData40.setDate(date46);
    boolean boolean58 = duration5.equals(date46);
    int int59 = uRIList1.lastIndexOf(duration5);
    java.lang.Integer int60 = uRIList1.getTypeShortForm();
    java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.URI> uRIStream61 = uRIList1
        .parallelStream();
    java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.URI> uRIStream62 = uRIList1.stream();
    java.util.Iterator<org.ccsds.moims.mo.mal.structures.URI> uRIItor63 = uRIList1.iterator();
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(uRIStream3);
    org.junit.Assert.assertNotNull(uOctet6);
    org.junit.Assert.assertNotNull(uShort7);
    org.junit.Assert.assertNotNull(floatArray14);
    org.junit.Assert.assertTrue("'" + double20 + "' != '" + 340.0d + "'", double20 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str21 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str21.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray22);
    org.junit.Assert.assertTrue("'" + str24 + "' != '" + "UnknownGUIData" + "'",
        str24.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double29 + "' != '" + 340.0d + "'", double29 == 340.0d);
    org.junit.Assert.assertTrue("'" + double30 + "' != '" + 340.0d + "'", double30 == 340.0d);
    org.junit.Assert.assertTrue("'" + double35 + "' != '" + 340.0d + "'", double35 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str36 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str36.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray37);
    org.junit.Assert.assertNotNull(floatArray39);
    org.junit.Assert.assertNotNull(floatArray41);
    org.junit.Assert.assertNotNull(date46);
    org.junit.Assert.assertNotNull(date50);
    org.junit.Assert.assertNotNull(date53);
    org.junit.Assert.assertNotNull(timeUnitMap54);
    org.junit.Assert.assertTrue("'" + boolean58 + "' != '" + false + "'", !boolean58);
    org.junit.Assert.assertTrue("'" + int59 + "' != '" + (-1) + "'", int59 == (-1));
    org.junit.Assert.assertTrue("'" + int60 + "' != '" + (-18) + "'", int60.equals((-18)));
    org.junit.Assert.assertNotNull(uRIStream61);
    org.junit.Assert.assertNotNull(uRIStream62);
    org.junit.Assert.assertNotNull(uRIItor63);
  }

  @Test
  public void test1157() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1157");
    }
    opssat.simulator.util.wav.WavFileException wavFileException1 =
        new opssat.simulator.util.wav.WavFileException(
            "UnknownGUIData");
    java.lang.Throwable[] throwableArray2 = wavFileException1.getSuppressed();
    java.lang.String str3 = wavFileException1.toString();
    java.lang.String str4 = wavFileException1.toString();
    java.lang.Throwable[] throwableArray5 = wavFileException1.getSuppressed();
    org.junit.Assert.assertNotNull(throwableArray2);
    org.junit.Assert.assertTrue(
        "'" + str3 + "' != '" + "opssat.simulator.util.wav.WavFileException: UnknownGUIData" + "'",
        str3.equals("opssat.simulator.util.wav.WavFileException: UnknownGUIData"));
    org.junit.Assert.assertTrue(
        "'" + str4 + "' != '" + "opssat.simulator.util.wav.WavFileException: UnknownGUIData" + "'",
        str4.equals("opssat.simulator.util.wav.WavFileException: UnknownGUIData"));
    org.junit.Assert.assertNotNull(throwableArray5);
  }

  @Test
  public void test1161() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1161");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet3 = octetList0.getAreaVersion();
    java.util.stream.Stream<java.lang.Byte> byteStream4 = octetList0.parallelStream();
    org.ccsds.moims.mo.mal.structures.UShort uShort5 = octetList0.getAreaNumber();
    java.util.stream.Stream<java.lang.Byte> byteStream6 = octetList0.parallelStream();
    octetList0.ensureCapacity(39);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState12 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray15 = new float[]{28, 8};
    simulatorSpacecraftState12.setQ(floatArray15);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState20 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double21 = simulatorSpacecraftState20.getLatitude();
    java.lang.String str22 = simulatorSpacecraftState20.getMagField();
    float[] floatArray23 = simulatorSpacecraftState20.getR();
    simulatorSpacecraftState12.setQ(floatArray23);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState28 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState28.setLongitude(4);
    double[] doubleArray32 = new double[]{(-1.0f)};
    simulatorSpacecraftState28.setMagField(doubleArray32);
    simulatorSpacecraftState28.setLatitude(0.0d);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState39 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray42 = new float[]{28, 8};
    simulatorSpacecraftState39.setQ(floatArray42);
    float[] floatArray44 = simulatorSpacecraftState39.getR();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState48 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState48.setLongitude(4);
    double[] doubleArray52 = new double[]{(-1.0f)};
    simulatorSpacecraftState48.setMagField(doubleArray52);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState57 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double58 = simulatorSpacecraftState57.getLatitude();
    java.lang.String str59 = simulatorSpacecraftState57.getMagField();
    java.lang.String str60 = simulatorSpacecraftState57.toString();
    double[] doubleArray61 = simulatorSpacecraftState57.getSunVector();
    simulatorSpacecraftState48.setMagnetometer(doubleArray61);
    simulatorSpacecraftState39.setSunVector(doubleArray61);
    simulatorSpacecraftState28.setSunVector(doubleArray61);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState68 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState68.setLongitude(4);
    double[] doubleArray72 = new double[]{(-1.0f)};
    simulatorSpacecraftState68.setMagField(doubleArray72);
    simulatorSpacecraftState28.setMagField(doubleArray72);
    simulatorSpacecraftState12.setSunVector(doubleArray72);
    int int76 = octetList0.lastIndexOf(simulatorSpacecraftState12);
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(uOctet3);
    org.junit.Assert.assertNotNull(byteStream4);
    org.junit.Assert.assertNotNull(uShort5);
    org.junit.Assert.assertNotNull(byteStream6);
    org.junit.Assert.assertNotNull(floatArray15);
    org.junit.Assert.assertTrue("'" + double21 + "' != '" + 340.0d + "'", double21 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str22 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str22.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray23);
    org.junit.Assert.assertNotNull(doubleArray32);
    org.junit.Assert.assertNotNull(floatArray42);
    org.junit.Assert.assertNotNull(floatArray44);
    org.junit.Assert.assertNotNull(doubleArray52);
    org.junit.Assert.assertTrue("'" + double58 + "' != '" + 340.0d + "'", double58 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str59 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str59.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str60 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str60.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray61);
    org.junit.Assert.assertNotNull(doubleArray72);
    org.junit.Assert.assertTrue("'" + int76 + "' != '" + (-1) + "'", int76 == (-1));
  }

  @Test
  public void test1162() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1162");
    }
    org.ccsds.moims.mo.mal.structures.StringList stringList0 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    java.util.Iterator<java.lang.String> strItor1 = stringList0.iterator();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = stringList0.getAreaVersion();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState6 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray9 = new float[]{28, 8};
    simulatorSpacecraftState6.setQ(floatArray9);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState14 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double15 = simulatorSpacecraftState14.getLatitude();
    java.lang.String str16 = simulatorSpacecraftState14.getMagField();
    float[] floatArray17 = simulatorSpacecraftState14.getR();
    simulatorSpacecraftState6.setQ(floatArray17);
    java.lang.String str19 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray17);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState23 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double24 = simulatorSpacecraftState23.getLatitude();
    double double25 = simulatorSpacecraftState23.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState29 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double30 = simulatorSpacecraftState29.getLatitude();
    java.lang.String str31 = simulatorSpacecraftState29.getMagField();
    float[] floatArray32 = simulatorSpacecraftState29.getR();
    simulatorSpacecraftState23.setQ(floatArray32);
    float[] floatArray34 = simulatorSpacecraftState23.getV();
    opssat.simulator.celestia.CelestiaData celestiaData35 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray17, floatArray34);
    float[] floatArray36 = celestiaData35.getQ();
    opssat.simulator.util.SimulatorData simulatorData40 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date41 = simulatorData40.getCurrentTime();
    int int42 = opssat.simulator.util.DateExtraction.getDayFromDate(date41);
    opssat.simulator.util.SimulatorData simulatorData44 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date45 = simulatorData44.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData47 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date48 = simulatorData47.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap49 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date45, date48);
    opssat.simulator.util.SimulatorHeader simulatorHeader50 =
        new opssat.simulator.util.SimulatorHeader(
            false, date41, date48);
    opssat.simulator.util.SimulatorData simulatorData51 = new opssat.simulator.util.SimulatorData(
        (short) 0, date41);
    celestiaData35.setDate(date41);
    int int53 = celestiaData35.getSeconds();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState57 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray60 = new float[]{28, 8};
    simulatorSpacecraftState57.setQ(floatArray60);
    celestiaData35.setQ(floatArray60);
    int int63 = stringList0.lastIndexOf(celestiaData35);
    celestiaData35.setDate("00000:00:00:00:097  11                 executed false   | ");
    java.lang.String str66 = celestiaData35.getDate();
    celestiaData35.setLos("Unknown data type [java.util.ArrayList]");
    org.junit.Assert.assertNotNull(strItor1);
    org.junit.Assert.assertNotNull(uOctet2);
    org.junit.Assert.assertNotNull(floatArray9);
    org.junit.Assert.assertTrue("'" + double15 + "' != '" + 340.0d + "'", double15 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str16 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str16.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray17);
    org.junit.Assert.assertTrue("'" + str19 + "' != '" + "UnknownGUIData" + "'",
        str19.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double24 + "' != '" + 340.0d + "'", double24 == 340.0d);
    org.junit.Assert.assertTrue("'" + double25 + "' != '" + 340.0d + "'", double25 == 340.0d);
    org.junit.Assert.assertTrue("'" + double30 + "' != '" + 340.0d + "'", double30 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str31 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str31.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray32);
    org.junit.Assert.assertNotNull(floatArray34);
    org.junit.Assert.assertNotNull(floatArray36);
    org.junit.Assert.assertNotNull(date41);
    org.junit.Assert.assertNotNull(date45);
    org.junit.Assert.assertNotNull(date48);
    org.junit.Assert.assertNotNull(timeUnitMap49);
    org.junit.Assert.assertNotNull(floatArray60);
    org.junit.Assert.assertTrue("'" + int63 + "' != '" + (-1) + "'", int63 == (-1));
  }

  @Test
  public void test1163() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1163");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState11 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double12 = simulatorSpacecraftState11.getLatitude();
    java.lang.String str13 = simulatorSpacecraftState11.getMagField();
    float[] floatArray14 = simulatorSpacecraftState11.getR();
    simulatorSpacecraftState3.setQ(floatArray14);
    java.lang.String str16 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray14);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState20 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double21 = simulatorSpacecraftState20.getLatitude();
    double double22 = simulatorSpacecraftState20.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState26 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double27 = simulatorSpacecraftState26.getLatitude();
    java.lang.String str28 = simulatorSpacecraftState26.getMagField();
    float[] floatArray29 = simulatorSpacecraftState26.getR();
    simulatorSpacecraftState20.setQ(floatArray29);
    float[] floatArray31 = simulatorSpacecraftState20.getV();
    opssat.simulator.celestia.CelestiaData celestiaData32 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray14, floatArray31);
    java.lang.String str33 = celestiaData32.getInfo();
    java.lang.String str34 = celestiaData32.getAnx();
    java.lang.String str35 = celestiaData32.getAos();
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + 340.0d + "'", double12 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str13 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str13.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray14);
    org.junit.Assert.assertTrue("'" + str16 + "' != '" + "UnknownGUIData" + "'",
        str16.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double21 + "' != '" + 340.0d + "'", double21 == 340.0d);
    org.junit.Assert.assertTrue("'" + double22 + "' != '" + 340.0d + "'", double22 == 340.0d);
    org.junit.Assert.assertTrue("'" + double27 + "' != '" + 340.0d + "'", double27 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str28 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str28.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray29);
    org.junit.Assert.assertNotNull(floatArray31);
    org.junit.Assert.assertNull(str33);
    org.junit.Assert.assertNull(str34);
    org.junit.Assert.assertNull(str35);
  }

  @Test
  public void test1164() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1164");
    }
    org.ccsds.moims.mo.mal.structures.StringList stringList0 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    org.ccsds.moims.mo.mal.structures.ShortList shortList2 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            ' ');
    java.util.ListIterator<java.lang.Short> shortItor3 = shortList2.listIterator();
    boolean boolean4 = stringList0.equals(shortList2);
    java.util.stream.BaseStream[] baseStreamArray6 = new java.util.stream.BaseStream[0];
    @SuppressWarnings("unchecked")
    java.util.stream.BaseStream<java.lang.Float, java.util.stream.Stream<java.lang.Float>>[] floatBaseStreamArray7 =
        baseStreamArray6;
    java.util.stream.BaseStream<java.lang.Float, java.util.stream.Stream<java.lang.Float>>[] floatBaseStreamArray8 =
        stringList0
            .toArray(
                (java.util.stream.BaseStream<java.lang.Float, java.util.stream.Stream<java.lang.Float>>[]) baseStreamArray6);
    java.util.stream.Stream<java.lang.String> strStream9 = stringList0.stream();
    java.lang.Object obj10 = stringList0.clone();
    opssat.simulator.util.LoggerFormatter loggerFormatter11 =
        new opssat.simulator.util.LoggerFormatter();
    opssat.simulator.util.LoggerFormatter loggerFormatter12 =
        new opssat.simulator.util.LoggerFormatter();
    opssat.simulator.util.LoggerFormatter loggerFormatter13 =
        new opssat.simulator.util.LoggerFormatter();
    opssat.simulator.util.LoggerFormatter[] loggerFormatterArray14 =
        new opssat.simulator.util.LoggerFormatter[]{
          loggerFormatter11, loggerFormatter12, loggerFormatter13};
    opssat.simulator.util.LoggerFormatter[] loggerFormatterArray15 = stringList0
        .toArray(loggerFormatterArray14);
    java.util.stream.Stream<java.lang.String> strStream16 = stringList0.parallelStream();
    org.ccsds.moims.mo.mal.structures.UShort uShort17 = stringList0.getServiceNumber();
    org.ccsds.moims.mo.mal.structures.Element element18 = stringList0.createElement();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet19 = stringList0.getAreaVersion();
    org.junit.Assert.assertNotNull(shortItor3);
    org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
    org.junit.Assert.assertNotNull(baseStreamArray6);
    org.junit.Assert.assertNotNull(floatBaseStreamArray7);
    org.junit.Assert.assertNotNull(floatBaseStreamArray8);
    org.junit.Assert.assertNotNull(strStream9);
    org.junit.Assert.assertNotNull(obj10);
    org.junit.Assert.assertNotNull(loggerFormatterArray14);
    org.junit.Assert.assertNotNull(loggerFormatterArray15);
    org.junit.Assert.assertNotNull(strStream16);
    org.junit.Assert.assertNotNull(uShort17);
    org.junit.Assert.assertNotNull(element18);
    org.junit.Assert.assertNotNull(uOctet19);
  }

  @Test
  public void test1165() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1165");
    }
    opssat.simulator.util.SimulatorData simulatorData4 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date5 = simulatorData4.getCurrentTime();
    java.util.Date date6 = simulatorData4.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData7 = new opssat.simulator.util.SimulatorData(17,
        date6);
    opssat.simulator.util.SimulatorData simulatorData10 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date11 = simulatorData10.getCurrentTime();
    int int12 = opssat.simulator.util.DateExtraction.getDayFromDate(date11);
    opssat.simulator.util.SimulatorData simulatorData14 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date15 = simulatorData14.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData17 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date18 = simulatorData17.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap19 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date15, date18);
    opssat.simulator.util.SimulatorHeader simulatorHeader20 =
        new opssat.simulator.util.SimulatorHeader(
            false, date11, date18);
    opssat.simulator.util.SimulatorHeader simulatorHeader21 =
        new opssat.simulator.util.SimulatorHeader(
            false, date6, date11);
    opssat.simulator.util.SimulatorData simulatorData22 = new opssat.simulator.util.SimulatorData(
        (-18), date11);
    org.junit.Assert.assertNotNull(date5);
    org.junit.Assert.assertNotNull(date6);
    org.junit.Assert.assertNotNull(date11);
    org.junit.Assert.assertNotNull(date15);
    org.junit.Assert.assertNotNull(date18);
    org.junit.Assert.assertNotNull(timeUnitMap19);
  }

  @Test
  public void test1166() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1166");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            281474993487890L, (byte) 0, 11111);
  }

  @Test
  public void test1167() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1167");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessWavStreamOperatingBuffer endlessWavStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessWavStreamOperatingBuffer(
            logger0);
    java.lang.Object obj2 = endlessWavStreamOperatingBuffer1.getDataBuffer();
    boolean boolean4 = endlessWavStreamOperatingBuffer1.preparePath("03600.0000000");
    int int5 = endlessWavStreamOperatingBuffer1.getOperatingIndex();
    byte[] byteArray7 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS
        .long2ByteArray((short) -1);
    endlessWavStreamOperatingBuffer1.setDataFromByteArray(byteArray7);
    try {
      double double10 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS
          .getDoubleFromByteArray(byteArray7, 19);
      org.junit.Assert
          .fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
    } catch (java.lang.IndexOutOfBoundsException e) {
    }
    org.junit.Assert.assertNotNull(obj2);
    org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + false + "'", !boolean4);
    org.junit.Assert.assertTrue("'" + int5 + "' != '" + 0 + "'", int5 == 0);
    org.junit.Assert.assertNotNull(byteArray7);
  }

  @Test
  public void test1168() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1168");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    int int14 = simulatorHeader12.getMinuteStartDate();
    simulatorHeader12.setUseOrekitPropagator(true);
    java.lang.String str17 = simulatorHeader12.toFileString();
    simulatorHeader12.setAutoStartTime(true);
    int int20 = simulatorHeader12.getTimeFactor();
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertTrue("'" + int20 + "' != '" + 1 + "'", int20 == 1);
  }

  @Test
  public void test1169() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1169");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    java.lang.Integer int2 = octetList0.getTypeShortForm();
    boolean boolean3 = octetList0.isEmpty();
    java.lang.Float[] floatArray7 = new java.lang.Float[]{1.0f, 1.0f, (-1.0f)};
    java.util.ArrayList<java.lang.Float> floatList8 = new java.util.ArrayList<java.lang.Float>();
    boolean boolean9 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Float>) floatList8, floatArray7);
    boolean boolean11 = floatList8.add((-1.0f));
    floatList8.clear();
    java.util.stream.Stream<java.lang.Float> floatStream13 = floatList8.stream();
    java.lang.Byte[] byteArray18 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList19 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean20 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList19, byteArray18);
    java.lang.Integer[] intArray23 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList24 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean25 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList24, intArray23);
    boolean boolean26 = byteList19.retainAll(intList24);
    java.lang.Integer[] intArray29 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList30 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean31 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList30, intArray29);
    int int33 = intList30.lastIndexOf((byte) 10);
    boolean boolean34 = intList24.retainAll(intList30);
    boolean boolean35 = floatList8.containsAll(intList30);
    java.util.Spliterator<java.lang.Integer> intSpliterator36 = intList30.spliterator();
    java.util.stream.Stream<java.lang.Integer> intStream37 = intList30.parallelStream();
    java.lang.String str38 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(intList30);
    java.util.ListIterator<java.lang.Integer> intItor39 = intList30.listIterator();
    boolean boolean40 = octetList0.retainAll(intList30);
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertTrue("'" + int2 + "' != '" + (-7) + "'", int2.equals((-7)));
    org.junit.Assert.assertTrue("'" + boolean3 + "' != '" + true + "'", boolean3);
    org.junit.Assert.assertNotNull(floatArray7);
    org.junit.Assert.assertTrue("'" + boolean9 + "' != '" + true + "'", boolean9);
    org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
    org.junit.Assert.assertNotNull(floatStream13);
    org.junit.Assert.assertNotNull(byteArray18);
    org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + true + "'", boolean20);
    org.junit.Assert.assertNotNull(intArray23);
    org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + true + "'", boolean25);
    org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + true + "'", boolean26);
    org.junit.Assert.assertNotNull(intArray29);
    org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + true + "'", boolean31);
    org.junit.Assert.assertTrue("'" + int33 + "' != '" + (-1) + "'", int33 == (-1));
    org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + true + "'", boolean34);
    org.junit.Assert.assertTrue("'" + boolean35 + "' != '" + false + "'", !boolean35);
    org.junit.Assert.assertNotNull(intSpliterator36);
    org.junit.Assert.assertNotNull(intStream37);
    org.junit.Assert.assertTrue("'" + str38 + "' != '" + "UnknownGUIData" + "'",
        str38.equals("UnknownGUIData"));
    org.junit.Assert.assertNotNull(intItor39);
    org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + false + "'", !boolean40);
  }

  @Test
  public void test1170() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1170");
    }
    opssat.simulator.util.SimulatorSchedulerPiece simulatorSchedulerPiece3 =
        new opssat.simulator.util.SimulatorSchedulerPiece(
            13L, 0, "hi!");
    long long4 = simulatorSchedulerPiece3.getTime();
    int int5 = simulatorSchedulerPiece3.getInternalID();
    simulatorSchedulerPiece3.setExecuted(false);
    java.lang.String str8 = simulatorSchedulerPiece3.getFileString();
    java.lang.String str9 = simulatorSchedulerPiece3.getArgumentTemplateDescription();
    java.lang.String str10 = simulatorSchedulerPiece3.getSchedulerOutput();
    int int11 = simulatorSchedulerPiece3.getInternalID();
    org.junit.Assert.assertTrue("'" + long4 + "' != '" + 13L + "'", long4 == 13L);
    org.junit.Assert.assertTrue("'" + int5 + "' != '" + 0 + "'", int5 == 0);
    org.junit.Assert.assertTrue(
        "'" + str8 + "' != '" + "00000:00:00:00:013|0000000000000000013|0|hi!" + "'",
        str8.equals("00000:00:00:00:013|0000000000000000013|0|hi!"));
    org.junit.Assert.assertTrue("'" + str9 + "' != '" + "hi!" + "'", str9.equals("hi!"));
    org.junit.Assert.assertTrue(
        "'" + str10 + "' != '" + "00000:00:00:00:013  0     hi!          executed false   | " + "'",
        str10.equals("00000:00:00:00:013  0     hi!          executed false   | "));
    org.junit.Assert.assertTrue("'" + int11 + "' != '" + 0 + "'", int11 == 0);
  }

  @Test
  public void test1171() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1171");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessWavStreamOperatingBuffer endlessWavStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessWavStreamOperatingBuffer(
            logger0);
    opssat.simulator.util.SimulatorData simulatorData4 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date5 = simulatorData4.getCurrentTime();
    int int6 = opssat.simulator.util.DateExtraction.getDayFromDate(date5);
    opssat.simulator.util.SimulatorData simulatorData8 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date9 = simulatorData8.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData11 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date12 = simulatorData11.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap13 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date9, date12);
    opssat.simulator.util.SimulatorHeader simulatorHeader14 =
        new opssat.simulator.util.SimulatorHeader(
            false, date5, date12);
    java.util.Date date15 = simulatorHeader14.getEndDate();
    int int16 = simulatorHeader14.getMinuteStartDate();
    simulatorHeader14.setUseOrekitPropagator(true);
    java.lang.String str19 = simulatorHeader14.toFileString();
    java.util.Date date21 = simulatorHeader14.parseStringIntoDate("yyyy:MM:dd HH:mm:ss z");
    endlessWavStreamOperatingBuffer1.setDataBuffer("yyyy:MM:dd HH:mm:ss z");
    boolean boolean24 = endlessWavStreamOperatingBuffer1.preparePath("");
    java.lang.Byte[] byteArray29 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList30 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean31 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList30, byteArray29);
    java.lang.Integer[] intArray34 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList35 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean36 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList35, intArray34);
    boolean boolean37 = byteList30.retainAll(intList35);
    java.lang.Integer[] intArray40 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList41 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean42 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList41, intArray40);
    int int44 = intList41.lastIndexOf((byte) 10);
    boolean boolean45 = intList35.removeAll(intList41);
    java.util.Spliterator<java.lang.Integer> intSpliterator46 = intList35.spliterator();
    intList35.trimToSize();
    org.ccsds.moims.mo.mal.structures.IntegerList integerList48 =
        new org.ccsds.moims.mo.mal.structures.IntegerList();
    java.lang.Integer int49 = integerList48.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort50 = integerList48.getAreaNumber();
    java.lang.Integer int51 = integerList48.getTypeShortForm();
    boolean boolean52 = intList35.removeAll(integerList48);
    java.lang.Integer int53 = integerList48.getTypeShortForm();
    opssat.simulator.threading.SimulatorNode.DevDatPBind devDatPBind54 =
        opssat.simulator.threading.SimulatorNode.DevDatPBind.OpticalReceiver_OperatingBuffer;
    int int55 = integerList48.lastIndexOf(devDatPBind54);
    int int56 = integerList48.size();
    java.util.stream.Stream<java.lang.Integer> intStream57 = integerList48.parallelStream();
    integerList48.ensureCapacity(54);
    org.ccsds.moims.mo.mal.structures.DoubleList doubleList61 =
        new org.ccsds.moims.mo.mal.structures.DoubleList(
            (short) 10);
    boolean boolean63 = doubleList61.add(100.0d);
    org.ccsds.moims.mo.mal.structures.UOctet uOctet64 = doubleList61.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet65 = doubleList61.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.Element element66 = doubleList61.createElement();
    org.ccsds.moims.mo.mal.structures.UShort uShort67 = doubleList61.getServiceNumber();
    java.lang.Byte[] byteArray72 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList73 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean74 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList73, byteArray72);
    java.lang.Integer[] intArray77 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList78 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean79 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList78, intArray77);
    boolean boolean80 = byteList73.retainAll(intList78);
    java.lang.Integer[] intArray83 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList84 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean85 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList84, intArray83);
    int int87 = intList84.lastIndexOf((byte) 10);
    boolean boolean88 = intList78.removeAll(intList84);
    int int90 = intList78.indexOf(281474993487879L);
    boolean boolean91 = doubleList61.containsAll(intList78);
    boolean boolean92 = integerList48.addAll(intList78);
    endlessWavStreamOperatingBuffer1.setDataBuffer(intList78);
    org.junit.Assert.assertNotNull(date5);
    org.junit.Assert.assertNotNull(date9);
    org.junit.Assert.assertNotNull(date12);
    org.junit.Assert.assertNotNull(timeUnitMap13);
    org.junit.Assert.assertNotNull(date15);
    org.junit.Assert.assertNull(date21);
    org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + false + "'", !boolean24);
    org.junit.Assert.assertNotNull(byteArray29);
    org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + true + "'", boolean31);
    org.junit.Assert.assertNotNull(intArray34);
    org.junit.Assert.assertTrue("'" + boolean36 + "' != '" + true + "'", boolean36);
    org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + true + "'", boolean37);
    org.junit.Assert.assertNotNull(intArray40);
    org.junit.Assert.assertTrue("'" + boolean42 + "' != '" + true + "'", boolean42);
    org.junit.Assert.assertTrue("'" + int44 + "' != '" + (-1) + "'", int44 == (-1));
    org.junit.Assert.assertTrue("'" + boolean45 + "' != '" + true + "'", boolean45);
    org.junit.Assert.assertNotNull(intSpliterator46);
    org.junit.Assert.assertTrue("'" + int49 + "' != '" + (-11) + "'", int49.equals((-11)));
    org.junit.Assert.assertNotNull(uShort50);
    org.junit.Assert.assertTrue("'" + int51 + "' != '" + (-11) + "'", int51.equals((-11)));
    org.junit.Assert.assertTrue("'" + boolean52 + "' != '" + false + "'", !boolean52);
    org.junit.Assert.assertTrue("'" + int53 + "' != '" + (-11) + "'", int53.equals((-11)));
    org.junit.Assert.assertTrue(
        "'" + devDatPBind54 + "' != '"
        + opssat.simulator.threading.SimulatorNode.DevDatPBind.OpticalReceiver_OperatingBuffer
        + "'",
        devDatPBind54.equals(
            opssat.simulator.threading.SimulatorNode.DevDatPBind.OpticalReceiver_OperatingBuffer));
    org.junit.Assert.assertTrue("'" + int55 + "' != '" + (-1) + "'", int55 == (-1));
    org.junit.Assert.assertTrue("'" + int56 + "' != '" + 0 + "'", int56 == 0);
    org.junit.Assert.assertNotNull(intStream57);
    org.junit.Assert.assertTrue("'" + boolean63 + "' != '" + true + "'", boolean63);
    org.junit.Assert.assertNotNull(uOctet64);
    org.junit.Assert.assertNotNull(uOctet65);
    org.junit.Assert.assertNotNull(element66);
    org.junit.Assert.assertNotNull(uShort67);
    org.junit.Assert.assertNotNull(byteArray72);
    org.junit.Assert.assertTrue("'" + boolean74 + "' != '" + true + "'", boolean74);
    org.junit.Assert.assertNotNull(intArray77);
    org.junit.Assert.assertTrue("'" + boolean79 + "' != '" + true + "'", boolean79);
    org.junit.Assert.assertTrue("'" + boolean80 + "' != '" + true + "'", boolean80);
    org.junit.Assert.assertNotNull(intArray83);
    org.junit.Assert.assertTrue("'" + boolean85 + "' != '" + true + "'", boolean85);
    org.junit.Assert.assertTrue("'" + int87 + "' != '" + (-1) + "'", int87 == (-1));
    org.junit.Assert.assertTrue("'" + boolean88 + "' != '" + true + "'", boolean88);
    org.junit.Assert.assertTrue("'" + int90 + "' != '" + (-1) + "'", int90 == (-1));
    org.junit.Assert.assertTrue("'" + boolean91 + "' != '" + false + "'", !boolean91);
    org.junit.Assert.assertTrue("'" + boolean92 + "' != '" + true + "'", boolean92);
  }

  @Test
  public void test1173() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1173");
    }
    opssat.simulator.util.SimulatorData simulatorData3 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date4 = simulatorData3.getCurrentTime();
    java.util.Date date5 = simulatorData3.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(17,
        date5);
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    int int11 = opssat.simulator.util.DateExtraction.getDayFromDate(date10);
    opssat.simulator.util.SimulatorData simulatorData13 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date14 = simulatorData13.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData16 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date17 = simulatorData16.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap18 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date14, date17);
    opssat.simulator.util.SimulatorHeader simulatorHeader19 =
        new opssat.simulator.util.SimulatorHeader(
            false, date10, date17);
    opssat.simulator.util.SimulatorHeader simulatorHeader20 =
        new opssat.simulator.util.SimulatorHeader(
            false, date5, date10);
    boolean boolean21 = simulatorHeader20.isAutoStartTime();
    simulatorHeader20.setUseCelestia(false);
    simulatorHeader20.setAutoStartTime(false);
    org.ccsds.moims.mo.mal.structures.URI uRI27 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    java.lang.Integer int28 = uRI27.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.URI uRI30 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    org.ccsds.moims.mo.mal.structures.UOctet uOctet31 = uRI30.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.URI uRI33 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    org.ccsds.moims.mo.mal.structures.URI[] uRIArray34 =
        new org.ccsds.moims.mo.mal.structures.URI[]{
          uRI27, uRI30, uRI33};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList35 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
    boolean boolean36 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList35, uRIArray34);
    org.ccsds.moims.mo.mal.structures.FineTime fineTime37 =
        new org.ccsds.moims.mo.mal.structures.FineTime();
    int int38 = uRIList35.indexOf(fineTime37);
    uRIList35.ensureCapacity(40);
    java.lang.Object obj41 = uRIList35.clone();
    org.ccsds.moims.mo.mal.structures.URI uRI44 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    org.ccsds.moims.mo.mal.structures.UOctet uOctet45 = uRI44.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.URI uRI46 = uRIList35.set((byte) 1, uRI44);
    java.lang.String str47 = uRI44.toString();
    opssat.simulator.util.SimulatorData simulatorData50 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date51 = simulatorData50.getCurrentTime();
    java.util.Date date52 = simulatorData50.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData53 = new opssat.simulator.util.SimulatorData(
        34, date52);
    boolean boolean54 = uRI44.equals(date52);
    simulatorHeader20.setStartDate(date52);
    org.junit.Assert.assertNotNull(date4);
    org.junit.Assert.assertNotNull(date5);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(date14);
    org.junit.Assert.assertNotNull(date17);
    org.junit.Assert.assertNotNull(timeUnitMap18);
    org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
    org.junit.Assert.assertNotNull(uOctet31);
    org.junit.Assert.assertNotNull(uRIArray34);
    org.junit.Assert.assertTrue("'" + boolean36 + "' != '" + true + "'", boolean36);
    org.junit.Assert.assertTrue("'" + int38 + "' != '" + (-1) + "'", int38 == (-1));
    org.junit.Assert.assertNotNull(obj41);
    org.junit.Assert.assertNotNull(uOctet45);
    org.junit.Assert.assertNotNull(uRI46);
    org.junit.Assert.assertTrue("'" + str47 + "' != '" + "0100.0000" + "'",
        str47.equals("0100.0000"));
    org.junit.Assert.assertNotNull(date51);
    org.junit.Assert.assertNotNull(date52);
    org.junit.Assert.assertTrue("'" + boolean54 + "' != '" + false + "'", !boolean54);
  }

  @Test
  public void test1174() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1174");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor3 = octetList0.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor5 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList0, "hi!");
    org.ccsds.moims.mo.mal.structures.UShort uShort6 = octetList0.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UShort uShort7 = octetList0.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UShort uShort8 = octetList0.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UShort uShort9 = octetList0.getAreaNumber();
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(byteItor3);
    org.junit.Assert.assertNotNull(uShort6);
    org.junit.Assert.assertNotNull(uShort7);
    org.junit.Assert.assertNotNull(uShort8);
    org.junit.Assert.assertNotNull(uShort9);
  }

  @Test
  public void test1175() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1175");
    }
    java.util.logging.Logger logger2 = null;
    opssat.simulator.util.EndlessSingleStreamOperatingBuffer endlessSingleStreamOperatingBuffer3 =
        new opssat.simulator.util.EndlessSingleStreamOperatingBuffer(
            logger2);
    byte[] byteArray5 = endlessSingleStreamOperatingBuffer3.getDataAsByteArray('4');
    byte[] byteArray7 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS.int16_2ByteArray(15);
    endlessSingleStreamOperatingBuffer3.setDataFromByteArray(byteArray7);
    java.lang.String str9 = endlessSingleStreamOperatingBuffer3.getDataBufferAsString();
    byte[] byteArray11 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS.int2ByteArray((-30));
    endlessSingleStreamOperatingBuffer3.setDataFromByteArray(byteArray11);
    try {
      opssat.simulator.peripherals.PFineADCS.FWRefFineADCS.putInt16InByteArray(7, (byte) 100,
          byteArray11);
      org.junit.Assert.fail(
          "Expected exception of type java.lang.ArrayIndexOutOfBoundsException; message: 100");
    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
    }
    org.junit.Assert.assertNotNull(byteArray5);
    org.junit.Assert.assertNotNull(byteArray7);
    org.junit.Assert.assertTrue("'" + str9 + "' != '" + "byte[] {0x00,0x0F}" + "'",
        str9.equals("byte[] {0x00,0x0F}"));
    org.junit.Assert.assertNotNull(byteArray11);
  }

  @Test
  public void test1176() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1176");
    }
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor2 =
        new opssat.simulator.util.ArgumentDescriptor(
            "",
            "#Run the processing of internal models\nstartModels=false\n#Increment the simulated time (depends on startModels)\nstartTime=false\n#Speed up of time factor\ntimeFactor=1\n#Kepler elements for orbit A[km];E;i[deg];RAAN[deg];ARG_PER[deg];TRUE_A[deg] 7021.0;0.0;98.05;340.0;0.0;0.0\nkeplerElements=7021.0;0.0;98.05;340.0;0.0;0.0\n#Enable the Orekit library\norekit=false\n#Enable updates from Internet (used for gps constellation TLEs)\nupdateFromInternet=false\n#Configuration of the Celestia server\ncelestia=false\ncelestiaPort=0\n#Start and end dates of simulation\nstartDate=2019:05:23 15:09:43 UTC\nendDate=2019:05:23 15:09:43 UTC\n#Logging level to files found in $USER_HOME/.ops-sat-simulator/\n#Possible values SEVERE,INFO,FINE,FINER,FINEST,ALL\ncentralLogLevel=INFO\nsimulatorLogLevel=INFO\nconsoleLogLevel=INFO");
    java.lang.String str3 = argumentDescriptor2.getName();
    java.lang.String str4 = argumentDescriptor2.getName();
    org.junit.Assert.assertTrue("'" + str3 + "' != '" + "UnknownDeviceDataTypeString{}" + "'",
        str3.equals("UnknownDeviceDataTypeString{}"));
    org.junit.Assert.assertTrue("'" + str4 + "' != '" + "UnknownDeviceDataTypeString{}" + "'",
        str4.equals("UnknownDeviceDataTypeString{}"));
  }

  @Test
  public void test1177() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1177");
    }
    org.ccsds.moims.mo.mal.structures.ShortList shortList1 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            28);
    java.util.Iterator<java.lang.Short> shortItor2 = shortList1.iterator();
    opssat.simulator.threading.SimulatorNode simulatorNode3 = null;
    opssat.simulator.peripherals.POpticalReceiver pOpticalReceiver5 =
        new opssat.simulator.peripherals.POpticalReceiver(
            simulatorNode3, "031008.320");
    java.lang.String str6 = pOpticalReceiver5.getName();
    java.lang.String str7 = pOpticalReceiver5.getName();
    opssat.simulator.threading.SimulatorNode simulatorNode8 = null;
    opssat.simulator.peripherals.POpticalReceiver pOpticalReceiver10 =
        new opssat.simulator.peripherals.POpticalReceiver(
            simulatorNode8, "031008.320");
    opssat.simulator.threading.SimulatorNode simulatorNode11 = null;
    opssat.simulator.peripherals.POpticalReceiver pOpticalReceiver13 =
        new opssat.simulator.peripherals.POpticalReceiver(
            simulatorNode11, "031008.320");
    java.lang.String str14 = pOpticalReceiver13.getName();
    java.lang.String str15 = pOpticalReceiver13.getName();
    opssat.simulator.threading.SimulatorNode simulatorNode16 = null;
    opssat.simulator.peripherals.POpticalReceiver pOpticalReceiver18 =
        new opssat.simulator.peripherals.POpticalReceiver(
            simulatorNode16, "031008.320");
    java.lang.String str19 = pOpticalReceiver18.getName();
    java.lang.String str20 = pOpticalReceiver18.getName();
    opssat.simulator.threading.SimulatorNode simulatorNode21 = null;
    opssat.simulator.peripherals.POpticalReceiver pOpticalReceiver23 =
        new opssat.simulator.peripherals.POpticalReceiver(
            simulatorNode21, "031008.320");
    opssat.simulator.interfaces.IOpticalReceiver[] iOpticalReceiverArray24 =
        new opssat.simulator.interfaces.IOpticalReceiver[]{
          pOpticalReceiver5, pOpticalReceiver10, pOpticalReceiver13, pOpticalReceiver18,
          pOpticalReceiver23};
    opssat.simulator.interfaces.IOpticalReceiver[] iOpticalReceiverArray25 = shortList1
        .toArray(iOpticalReceiverArray24);
    org.junit.Assert.assertNotNull(shortItor2);
    org.junit.Assert.assertTrue("'" + str6 + "' != '" + "031008.320" + "'",
        str6.equals("031008.320"));
    org.junit.Assert.assertTrue("'" + str7 + "' != '" + "031008.320" + "'",
        str7.equals("031008.320"));
    org.junit.Assert.assertTrue("'" + str14 + "' != '" + "031008.320" + "'",
        str14.equals("031008.320"));
    org.junit.Assert.assertTrue("'" + str15 + "' != '" + "031008.320" + "'",
        str15.equals("031008.320"));
    org.junit.Assert.assertTrue("'" + str19 + "' != '" + "031008.320" + "'",
        str19.equals("031008.320"));
    org.junit.Assert.assertTrue("'" + str20 + "' != '" + "031008.320" + "'",
        str20.equals("031008.320"));
    org.junit.Assert.assertNotNull(iOpticalReceiverArray24);
    org.junit.Assert.assertNotNull(iOpticalReceiverArray25);
  }

  @Test
  public void test1178() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1178");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double4 = simulatorSpacecraftState3.getLatitude();
    java.lang.String str5 = simulatorSpacecraftState3.getMagField();
    simulatorSpacecraftState3.setLatitude((-4));
    java.lang.String str8 = simulatorSpacecraftState3.getMagField();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState12 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState12.setLongitude(4);
    double[] doubleArray16 = new double[]{(-1.0f)};
    simulatorSpacecraftState12.setMagField(doubleArray16);
    double double18 = simulatorSpacecraftState12.getLongitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState22 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray25 = new float[]{28, 8};
    simulatorSpacecraftState22.setQ(floatArray25);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState30 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double31 = simulatorSpacecraftState30.getLatitude();
    java.lang.String str32 = simulatorSpacecraftState30.getMagField();
    float[] floatArray33 = simulatorSpacecraftState30.getR();
    simulatorSpacecraftState22.setQ(floatArray33);
    java.lang.String str35 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray33);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState39 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double40 = simulatorSpacecraftState39.getLatitude();
    double double41 = simulatorSpacecraftState39.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState45 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double46 = simulatorSpacecraftState45.getLatitude();
    java.lang.String str47 = simulatorSpacecraftState45.getMagField();
    float[] floatArray48 = simulatorSpacecraftState45.getR();
    simulatorSpacecraftState39.setQ(floatArray48);
    float[] floatArray50 = simulatorSpacecraftState39.getV();
    opssat.simulator.celestia.CelestiaData celestiaData51 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray33, floatArray50);
    float[] floatArray52 = celestiaData51.getQ();
    opssat.simulator.util.SimulatorData simulatorData56 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date57 = simulatorData56.getCurrentTime();
    int int58 = opssat.simulator.util.DateExtraction.getDayFromDate(date57);
    opssat.simulator.util.SimulatorData simulatorData60 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date61 = simulatorData60.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData63 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date64 = simulatorData63.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap65 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date61, date64);
    opssat.simulator.util.SimulatorHeader simulatorHeader66 =
        new opssat.simulator.util.SimulatorHeader(
            false, date57, date64);
    opssat.simulator.util.SimulatorData simulatorData67 = new opssat.simulator.util.SimulatorData(
        (short) 0, date57);
    celestiaData51.setDate(date57);
    int int69 = celestiaData51.getSeconds();
    celestiaData51.setDnx("OPS-SAT SoftSim:");
    java.lang.String str72 = celestiaData51.getAos();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState76 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double77 = simulatorSpacecraftState76.getLatitude();
    double double78 = simulatorSpacecraftState76.getLongitude();
    float[] floatArray79 = simulatorSpacecraftState76.getQ();
    celestiaData51.setQ(floatArray79);
    simulatorSpacecraftState12.setQ(floatArray79);
    simulatorSpacecraftState3.setRv(floatArray79);
    float[] floatArray83 = simulatorSpacecraftState3.getMagnetometer();
    simulatorSpacecraftState3.setAltitude(42);
    org.junit.Assert.assertTrue("'" + double4 + "' != '" + 340.0d + "'", double4 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str5 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str5.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str8 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str8.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(doubleArray16);
    org.junit.Assert.assertTrue("'" + double18 + "' != '" + 4.0d + "'", double18 == 4.0d);
    org.junit.Assert.assertNotNull(floatArray25);
    org.junit.Assert.assertTrue("'" + double31 + "' != '" + 340.0d + "'", double31 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str32 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str32.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray33);
    org.junit.Assert.assertTrue("'" + str35 + "' != '" + "UnknownGUIData" + "'",
        str35.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double40 + "' != '" + 340.0d + "'", double40 == 340.0d);
    org.junit.Assert.assertTrue("'" + double41 + "' != '" + 340.0d + "'", double41 == 340.0d);
    org.junit.Assert.assertTrue("'" + double46 + "' != '" + 340.0d + "'", double46 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str47 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str47.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray48);
    org.junit.Assert.assertNotNull(floatArray50);
    org.junit.Assert.assertNotNull(floatArray52);
    org.junit.Assert.assertNotNull(date57);
    org.junit.Assert.assertNotNull(date61);
    org.junit.Assert.assertNotNull(date64);
    org.junit.Assert.assertNotNull(timeUnitMap65);
    org.junit.Assert.assertNull(str72);
    org.junit.Assert.assertTrue("'" + double77 + "' != '" + 340.0d + "'", double77 == 340.0d);
    org.junit.Assert.assertTrue("'" + double78 + "' != '" + (-1.0d) + "'", double78 == (-1.0d));
    org.junit.Assert.assertNotNull(floatArray79);
    org.junit.Assert.assertNotNull(floatArray83);
  }

  @Test
  public void test1179() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1179");
    }
    org.ccsds.moims.mo.mal.structures.FloatList floatList0 =
        new org.ccsds.moims.mo.mal.structures.FloatList();
    java.lang.Short[] shortArray3 = new java.lang.Short[]{(short) 1, (short) 1};
    java.util.ArrayList<java.lang.Short> shortList4 = new java.util.ArrayList<java.lang.Short>();
    boolean boolean5 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Short>) shortList4, shortArray3);
    shortList4.clear();
    boolean boolean7 = shortList4.isEmpty();
    java.lang.Byte[] byteArray12 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList13 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean14 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList13, byteArray12);
    java.lang.Integer[] intArray17 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList18 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean19 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList18, intArray17);
    boolean boolean20 = byteList13.retainAll(intList18);
    java.lang.Integer[] intArray28 = new java.lang.Integer[]{13, 10, 100, 100, 11111, 13, 11111};
    java.util.ArrayList<java.lang.Integer> intList29 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean30 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList29, intArray28);
    java.lang.Byte[] byteArray35 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList36 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean37 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList36, byteArray35);
    java.lang.Integer[] intArray40 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList41 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean42 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList41, intArray40);
    boolean boolean43 = byteList36.retainAll(intList41);
    java.lang.Integer[] intArray46 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList47 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean48 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList47, intArray46);
    int int50 = intList47.lastIndexOf((byte) 10);
    boolean boolean51 = intList41.removeAll(intList47);
    boolean boolean52 = intList29.retainAll(intList47);
    java.lang.Float[] floatArray56 = new java.lang.Float[]{1.0f, 1.0f, (-1.0f)};
    java.util.ArrayList<java.lang.Float> floatList57 = new java.util.ArrayList<java.lang.Float>();
    boolean boolean58 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Float>) floatList57, floatArray56);
    boolean boolean60 = floatList57.add((-1.0f));
    floatList57.trimToSize();
    java.lang.Integer[] intArray66 = new java.lang.Integer[]{100, 1, (-1), 10};
    java.util.ArrayList<java.lang.Integer> intList67 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean68 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList67, intArray66);
    int int70 = intList67.lastIndexOf((byte) 1);
    boolean boolean71 = floatList57.containsAll(intList67);
    boolean boolean72 = intList29.addAll(intList67);
    boolean boolean73 = intList18.retainAll(intList29);
    boolean boolean74 = shortList4.containsAll(intList18);
    boolean boolean75 = floatList0.containsAll(intList18);
    org.ccsds.moims.mo.mal.structures.OctetList octetList76 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int77 = octetList76.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort78 = octetList76.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor79 = octetList76.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor81 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList76, "hi!");
    argumentDescriptor81.restoreArgument();
    argumentDescriptor81.restoreArgument();
    argumentDescriptor81.restoreArgument();
    java.lang.String str85 = argumentDescriptor81.toString();
    argumentDescriptor81.setName("OPS-SAT SoftSim:");
    boolean boolean88 = intList18.remove(argumentDescriptor81);
    int int89 = intList18.size();
    org.junit.Assert.assertNotNull(shortArray3);
    org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
    org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + true + "'", boolean7);
    org.junit.Assert.assertNotNull(byteArray12);
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + true + "'", boolean14);
    org.junit.Assert.assertNotNull(intArray17);
    org.junit.Assert.assertTrue("'" + boolean19 + "' != '" + true + "'", boolean19);
    org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + true + "'", boolean20);
    org.junit.Assert.assertNotNull(intArray28);
    org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + true + "'", boolean30);
    org.junit.Assert.assertNotNull(byteArray35);
    org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + true + "'", boolean37);
    org.junit.Assert.assertNotNull(intArray40);
    org.junit.Assert.assertTrue("'" + boolean42 + "' != '" + true + "'", boolean42);
    org.junit.Assert.assertTrue("'" + boolean43 + "' != '" + true + "'", boolean43);
    org.junit.Assert.assertNotNull(intArray46);
    org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + true + "'", boolean48);
    org.junit.Assert.assertTrue("'" + int50 + "' != '" + (-1) + "'", int50 == (-1));
    org.junit.Assert.assertTrue("'" + boolean51 + "' != '" + true + "'", boolean51);
    org.junit.Assert.assertTrue("'" + boolean52 + "' != '" + true + "'", boolean52);
    org.junit.Assert.assertNotNull(floatArray56);
    org.junit.Assert.assertTrue("'" + boolean58 + "' != '" + true + "'", boolean58);
    org.junit.Assert.assertTrue("'" + boolean60 + "' != '" + true + "'", boolean60);
    org.junit.Assert.assertNotNull(intArray66);
    org.junit.Assert.assertTrue("'" + boolean68 + "' != '" + true + "'", boolean68);
    org.junit.Assert.assertTrue("'" + int70 + "' != '" + (-1) + "'", int70 == (-1));
    org.junit.Assert.assertTrue("'" + boolean71 + "' != '" + false + "'", !boolean71);
    org.junit.Assert.assertTrue("'" + boolean72 + "' != '" + true + "'", boolean72);
    org.junit.Assert.assertTrue("'" + boolean73 + "' != '" + true + "'", boolean73);
    org.junit.Assert.assertTrue("'" + boolean74 + "' != '" + false + "'", !boolean74);
    org.junit.Assert.assertTrue("'" + boolean75 + "' != '" + false + "'", !boolean75);
    org.junit.Assert.assertTrue("'" + int77 + "' != '" + (-7) + "'", int77.equals((-7)));
    org.junit.Assert.assertNotNull(uShort78);
    org.junit.Assert.assertNotNull(byteItor79);
    org.junit.Assert.assertTrue("'" + str85 + "' != '" + "" + "'", str85.equals(""));
    org.junit.Assert.assertTrue("'" + boolean88 + "' != '" + false + "'", !boolean88);
    org.junit.Assert.assertTrue("'" + int89 + "' != '" + 1 + "'", int89 == 1);
  }

  @Test
  public void test1180() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1180");
    }
    opssat.simulator.util.SimulatorData simulatorData3 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date4 = simulatorData3.getCurrentTime();
    int int5 = opssat.simulator.util.DateExtraction.getDayFromDate(date4);
    opssat.simulator.util.SimulatorData simulatorData7 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date8 = simulatorData7.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData10 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date11 = simulatorData10.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap12 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date8, date11);
    opssat.simulator.util.SimulatorHeader simulatorHeader13 =
        new opssat.simulator.util.SimulatorHeader(
            false, date4, date11);
    opssat.simulator.util.SimulatorData simulatorData14 = new opssat.simulator.util.SimulatorData(
        (short) 0, date4);
    opssat.simulator.util.SimulatorData simulatorData17 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date18 = simulatorData17.getCurrentTime();
    int int19 = opssat.simulator.util.DateExtraction.getDayFromDate(date18);
    opssat.simulator.util.SimulatorData simulatorData21 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date22 = simulatorData21.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData24 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date25 = simulatorData24.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap26 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date22, date25);
    opssat.simulator.util.SimulatorHeader simulatorHeader27 =
        new opssat.simulator.util.SimulatorHeader(
            false, date18, date25);
    java.util.Date date28 = simulatorHeader27.getEndDate();
    opssat.simulator.util.SimulatorData simulatorData32 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date33 = simulatorData32.getCurrentTime();
    java.util.Date date34 = simulatorData32.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData35 = new opssat.simulator.util.SimulatorData(
        17, date34);
    opssat.simulator.util.SimulatorData simulatorData38 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date39 = simulatorData38.getCurrentTime();
    int int40 = opssat.simulator.util.DateExtraction.getDayFromDate(date39);
    opssat.simulator.util.SimulatorData simulatorData42 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date43 = simulatorData42.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData45 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date46 = simulatorData45.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap47 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date43, date46);
    opssat.simulator.util.SimulatorHeader simulatorHeader48 =
        new opssat.simulator.util.SimulatorHeader(
            false, date39, date46);
    opssat.simulator.util.SimulatorHeader simulatorHeader49 =
        new opssat.simulator.util.SimulatorHeader(
            false, date34, date39);
    simulatorHeader27.setEndDate(date34);
    simulatorData14.initFromHeader(simulatorHeader27);
    boolean boolean52 = simulatorHeader27.checkStartBeforeEnd();
    simulatorHeader27.setAutoStartSystem(true);
    int int55 = simulatorHeader27.getDayStartDate();
    org.junit.Assert.assertNotNull(date4);
    org.junit.Assert.assertNotNull(date8);
    org.junit.Assert.assertNotNull(date11);
    org.junit.Assert.assertNotNull(timeUnitMap12);
    org.junit.Assert.assertNotNull(date18);
    org.junit.Assert.assertNotNull(date22);
    org.junit.Assert.assertNotNull(date25);
    org.junit.Assert.assertNotNull(timeUnitMap26);
    org.junit.Assert.assertNotNull(date28);
    org.junit.Assert.assertNotNull(date33);
    org.junit.Assert.assertNotNull(date34);
    org.junit.Assert.assertNotNull(date39);
    org.junit.Assert.assertNotNull(date43);
    org.junit.Assert.assertNotNull(date46);
    org.junit.Assert.assertNotNull(timeUnitMap47);
    org.junit.Assert.assertTrue("'" + boolean52 + "' != '" + true + "'", boolean52);
  }

  @Test
  public void test1181() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1181");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor3 = octetList0.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor5 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList0, "hi!");
    org.ccsds.moims.mo.mal.structures.UShort uShort6 = octetList0.getAreaNumber();
    java.lang.Short[] shortArray9 = new java.lang.Short[]{(short) -1, (short) 10};
    java.util.ArrayList<java.lang.Short> shortList10 = new java.util.ArrayList<java.lang.Short>();
    boolean boolean11 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Short>) shortList10, shortArray9);
    boolean boolean12 = shortList10.isEmpty();
    java.lang.Object obj13 = null;
    boolean boolean14 = shortList10.contains(obj13);
    boolean boolean15 = octetList0.contains(boolean14);
    java.util.ListIterator<java.lang.Byte> byteItor16 = octetList0.listIterator();
    java.lang.Object obj17 = octetList0.clone();
    java.util.Iterator<java.lang.Byte> byteItor18 = octetList0.iterator();
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(byteItor3);
    org.junit.Assert.assertNotNull(uShort6);
    org.junit.Assert.assertNotNull(shortArray9);
    org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
    org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + false + "'", !boolean12);
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
    org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
    org.junit.Assert.assertNotNull(byteItor16);
    org.junit.Assert.assertNotNull(obj17);
    org.junit.Assert.assertNotNull(byteItor18);
  }

  @Test
  public void test1182() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1182");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double4 = simulatorSpacecraftState3.getLatitude();
    double double5 = simulatorSpacecraftState3.getLongitude();
    float[] floatArray6 = simulatorSpacecraftState3.getQ();
    java.lang.String str7 = simulatorSpacecraftState3.getModeOperation();
    org.junit.Assert.assertTrue("'" + double4 + "' != '" + 340.0d + "'", double4 == 340.0d);
    org.junit.Assert.assertTrue("'" + double5 + "' != '" + (-1.0d) + "'", double5 == (-1.0d));
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertNull(str7);
  }

  @Test
  public void test1183() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1183");
    }
    opssat.simulator.util.SimulatorData simulatorData3 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date4 = simulatorData3.getCurrentTime();
    java.util.Date date5 = simulatorData3.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(17,
        date5);
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    int int11 = opssat.simulator.util.DateExtraction.getDayFromDate(date10);
    opssat.simulator.util.SimulatorData simulatorData13 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date14 = simulatorData13.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData16 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date17 = simulatorData16.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap18 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date14, date17);
    opssat.simulator.util.SimulatorHeader simulatorHeader19 =
        new opssat.simulator.util.SimulatorHeader(
            false, date10, date17);
    opssat.simulator.util.SimulatorHeader simulatorHeader20 =
        new opssat.simulator.util.SimulatorHeader(
            false, date5, date10);
    boolean boolean21 = simulatorHeader20.isAutoStartTime();
    simulatorHeader20.setUseCelestia(false);
    simulatorHeader20.setAutoStartTime(true);
    java.util.Date date26 = simulatorHeader20.getEndDate();
    org.junit.Assert.assertNotNull(date4);
    org.junit.Assert.assertNotNull(date5);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(date14);
    org.junit.Assert.assertNotNull(date17);
    org.junit.Assert.assertNotNull(timeUnitMap18);
    org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
    org.junit.Assert.assertNotNull(date26);
  }

  @Test
  public void test1184() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1184");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState11 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double12 = simulatorSpacecraftState11.getLatitude();
    java.lang.String str13 = simulatorSpacecraftState11.getMagField();
    float[] floatArray14 = simulatorSpacecraftState11.getR();
    simulatorSpacecraftState3.setQ(floatArray14);
    java.lang.String str16 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray14);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState20 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double21 = simulatorSpacecraftState20.getLatitude();
    double double22 = simulatorSpacecraftState20.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState26 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double27 = simulatorSpacecraftState26.getLatitude();
    java.lang.String str28 = simulatorSpacecraftState26.getMagField();
    float[] floatArray29 = simulatorSpacecraftState26.getR();
    simulatorSpacecraftState20.setQ(floatArray29);
    float[] floatArray31 = simulatorSpacecraftState20.getV();
    opssat.simulator.celestia.CelestiaData celestiaData32 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray14, floatArray31);
    celestiaData32.setDnx("1");
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState38 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray41 = new float[]{28, 8};
    simulatorSpacecraftState38.setQ(floatArray41);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState46 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double47 = simulatorSpacecraftState46.getLatitude();
    java.lang.String str48 = simulatorSpacecraftState46.getMagField();
    float[] floatArray49 = simulatorSpacecraftState46.getR();
    simulatorSpacecraftState38.setQ(floatArray49);
    java.lang.String str51 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray49);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState55 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double56 = simulatorSpacecraftState55.getLatitude();
    double double57 = simulatorSpacecraftState55.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState61 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double62 = simulatorSpacecraftState61.getLatitude();
    java.lang.String str63 = simulatorSpacecraftState61.getMagField();
    float[] floatArray64 = simulatorSpacecraftState61.getR();
    simulatorSpacecraftState55.setQ(floatArray64);
    float[] floatArray66 = simulatorSpacecraftState55.getV();
    opssat.simulator.celestia.CelestiaData celestiaData67 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray49, floatArray66);
    celestiaData32.setRv(floatArray66);
    java.lang.String str69 = celestiaData32.getAnx();
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + 340.0d + "'", double12 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str13 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str13.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray14);
    org.junit.Assert.assertTrue("'" + str16 + "' != '" + "UnknownGUIData" + "'",
        str16.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double21 + "' != '" + 340.0d + "'", double21 == 340.0d);
    org.junit.Assert.assertTrue("'" + double22 + "' != '" + 340.0d + "'", double22 == 340.0d);
    org.junit.Assert.assertTrue("'" + double27 + "' != '" + 340.0d + "'", double27 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str28 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str28.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray29);
    org.junit.Assert.assertNotNull(floatArray31);
    org.junit.Assert.assertNotNull(floatArray41);
    org.junit.Assert.assertTrue("'" + double47 + "' != '" + 340.0d + "'", double47 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str48 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str48.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray49);
    org.junit.Assert.assertTrue("'" + str51 + "' != '" + "UnknownGUIData" + "'",
        str51.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double56 + "' != '" + 340.0d + "'", double56 == 340.0d);
    org.junit.Assert.assertTrue("'" + double57 + "' != '" + 340.0d + "'", double57 == 340.0d);
    org.junit.Assert.assertTrue("'" + double62 + "' != '" + 340.0d + "'", double62 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str63 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str63.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray64);
    org.junit.Assert.assertNotNull(floatArray66);
    org.junit.Assert.assertNull(str69);
  }

  @Test
  public void test1185() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1185");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessSingleStreamOperatingBuffer endlessSingleStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessSingleStreamOperatingBuffer(
            logger0);
    byte[] byteArray3 = endlessSingleStreamOperatingBuffer1.getDataAsByteArray('4');
    org.ccsds.moims.mo.mal.structures.Union union5 = new org.ccsds.moims.mo.mal.structures.Union(
        "$DEFAULT");
    endlessSingleStreamOperatingBuffer1.setDataBuffer("$DEFAULT");
    byte[] byteArray8 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS.float2ByteArray(20);
    endlessSingleStreamOperatingBuffer1.setDataFromByteArray(byteArray8);
    java.lang.String str10 = endlessSingleStreamOperatingBuffer1.getDataBufferAsString();
    java.lang.String str11 = endlessSingleStreamOperatingBuffer1.getDataBufferAsString();
    org.junit.Assert.assertNotNull(byteArray3);
    org.junit.Assert.assertNotNull(byteArray8);
    org.junit.Assert.assertTrue("'" + str10 + "' != '" + "byte[] {0x41,0xA0,0x00,0x00}" + "'",
        str10.equals("byte[] {0x41,0xA0,0x00,0x00}"));
    org.junit.Assert.assertTrue("'" + str11 + "' != '" + "byte[] {0x41,0xA0,0x00,0x00}" + "'",
        str11.equals("byte[] {0x41,0xA0,0x00,0x00}"));
  }

  @Test
  public void test1186() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1186");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState11 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double12 = simulatorSpacecraftState11.getLatitude();
    java.lang.String str13 = simulatorSpacecraftState11.getMagField();
    float[] floatArray14 = simulatorSpacecraftState11.getR();
    simulatorSpacecraftState3.setQ(floatArray14);
    java.lang.String str16 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray14);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState20 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double21 = simulatorSpacecraftState20.getLatitude();
    double double22 = simulatorSpacecraftState20.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState26 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double27 = simulatorSpacecraftState26.getLatitude();
    java.lang.String str28 = simulatorSpacecraftState26.getMagField();
    float[] floatArray29 = simulatorSpacecraftState26.getR();
    simulatorSpacecraftState20.setQ(floatArray29);
    float[] floatArray31 = simulatorSpacecraftState20.getV();
    opssat.simulator.celestia.CelestiaData celestiaData32 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray14, floatArray31);
    float[] floatArray33 = celestiaData32.getQ();
    opssat.simulator.util.SimulatorData simulatorData37 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date38 = simulatorData37.getCurrentTime();
    int int39 = opssat.simulator.util.DateExtraction.getDayFromDate(date38);
    opssat.simulator.util.SimulatorData simulatorData41 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date42 = simulatorData41.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData44 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date45 = simulatorData44.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap46 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date42, date45);
    opssat.simulator.util.SimulatorHeader simulatorHeader47 =
        new opssat.simulator.util.SimulatorHeader(
            false, date38, date45);
    opssat.simulator.util.SimulatorData simulatorData48 = new opssat.simulator.util.SimulatorData(
        (short) 0, date38);
    celestiaData32.setDate(date38);
    int int50 = celestiaData32.getMonths();
    celestiaData32.setInfo("2019/05/23-15:10:03");
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + 340.0d + "'", double12 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str13 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str13.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray14);
    org.junit.Assert.assertTrue("'" + str16 + "' != '" + "UnknownGUIData" + "'",
        str16.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double21 + "' != '" + 340.0d + "'", double21 == 340.0d);
    org.junit.Assert.assertTrue("'" + double22 + "' != '" + 340.0d + "'", double22 == 340.0d);
    org.junit.Assert.assertTrue("'" + double27 + "' != '" + 340.0d + "'", double27 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str28 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str28.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray29);
    org.junit.Assert.assertNotNull(floatArray31);
    org.junit.Assert.assertNotNull(floatArray33);
    org.junit.Assert.assertNotNull(date38);
    org.junit.Assert.assertNotNull(date42);
    org.junit.Assert.assertNotNull(date45);
    org.junit.Assert.assertNotNull(timeUnitMap46);
  }

  @Test
  public void test1187() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1187");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    int int5 = opssat.simulator.util.DateExtraction.getHourFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(48,
        date3);
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    int int11 = opssat.simulator.util.DateExtraction.getDayFromDate(date10);
    opssat.simulator.util.SimulatorData simulatorData13 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date14 = simulatorData13.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData16 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date17 = simulatorData16.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap18 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date14, date17);
    opssat.simulator.util.SimulatorHeader simulatorHeader19 =
        new opssat.simulator.util.SimulatorHeader(
            false, date10, date17);
    java.util.Date date20 = simulatorHeader19.getEndDate();
    opssat.simulator.util.SimulatorData simulatorData24 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date25 = simulatorData24.getCurrentTime();
    java.util.Date date26 = simulatorData24.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData27 = new opssat.simulator.util.SimulatorData(
        17, date26);
    opssat.simulator.util.SimulatorData simulatorData30 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date31 = simulatorData30.getCurrentTime();
    int int32 = opssat.simulator.util.DateExtraction.getDayFromDate(date31);
    opssat.simulator.util.SimulatorData simulatorData34 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date35 = simulatorData34.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData37 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date38 = simulatorData37.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap39 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date35, date38);
    opssat.simulator.util.SimulatorHeader simulatorHeader40 =
        new opssat.simulator.util.SimulatorHeader(
            false, date31, date38);
    opssat.simulator.util.SimulatorHeader simulatorHeader41 =
        new opssat.simulator.util.SimulatorHeader(
            false, date26, date31);
    simulatorHeader19.setEndDate(date26);
    int int43 = simulatorHeader19.getCelestiaPort();
    simulatorData6.initFromHeader(simulatorHeader19);
    simulatorHeader19.setKeplerElements("21474836472147483647.9223372036854775807");
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(date14);
    org.junit.Assert.assertNotNull(date17);
    org.junit.Assert.assertNotNull(timeUnitMap18);
    org.junit.Assert.assertNotNull(date20);
    org.junit.Assert.assertNotNull(date25);
    org.junit.Assert.assertNotNull(date26);
    org.junit.Assert.assertNotNull(date31);
    org.junit.Assert.assertNotNull(date35);
    org.junit.Assert.assertNotNull(date38);
    org.junit.Assert.assertNotNull(timeUnitMap39);
    org.junit.Assert.assertTrue("'" + int43 + "' != '" + 0 + "'", int43 == 0);
  }

  @Test
  public void test1188() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1188");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessWavStreamOperatingBuffer endlessWavStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessWavStreamOperatingBuffer(
            logger0);
    java.lang.Object obj2 = endlessWavStreamOperatingBuffer1.getDataBuffer();
    java.lang.Object obj3 = endlessWavStreamOperatingBuffer1.getDataBuffer();
    java.lang.Object obj4 = endlessWavStreamOperatingBuffer1.getDataBuffer();
    org.junit.Assert.assertNotNull(obj2);
    org.junit.Assert.assertNotNull(obj3);
    org.junit.Assert.assertNotNull(obj4);
  }

  @Test
  public void test1189() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1189");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    boolean boolean14 = simulatorHeader12.isAutoStartSystem();
    int int15 = simulatorHeader12.getDayStartDate();
    java.lang.String str16 = simulatorHeader12.getOrekitTLE1();
    simulatorHeader12.setTimeFactor(31);
    simulatorHeader12.setOrekitPropagator("Alive");
    boolean boolean21 = simulatorHeader12.checkStartBeforeEnd();
    java.lang.String str22 = simulatorHeader12.toFileString();
    int int23 = simulatorHeader12.getMinuteStartDate();
    boolean boolean24 = simulatorHeader12.isUpdateInternet();
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
    org.junit.Assert.assertNull(str16);
    org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + true + "'", boolean21);
    org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + false + "'", !boolean24);
  }

  @Test
  public void test1190() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1190");
    }
    org.ccsds.moims.mo.mal.structures.ULong uLong0 = new org.ccsds.moims.mo.mal.structures.ULong();
    java.lang.Long long1 = uLong0.getShortForm();
    java.lang.Boolean[] booleanArray3 = new java.lang.Boolean[]{true};
    java.util.ArrayList<java.lang.Boolean> booleanList4 =
        new java.util.ArrayList<java.lang.Boolean>();
    boolean boolean5 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Boolean>) booleanList4, booleanArray3);
    java.util.Spliterator<java.lang.Boolean> booleanSpliterator6 = booleanList4.spliterator();
    org.ccsds.moims.mo.mal.structures.OctetList octetList7 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    octetList7.ensureCapacity(13);
    java.lang.Byte[] byteArray14 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList15 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean16 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList15, byteArray14);
    java.lang.Integer[] intArray19 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList20 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean21 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList20, intArray19);
    boolean boolean22 = byteList15.retainAll(intList20);
    boolean boolean23 = octetList7.containsAll(intList20);
    java.util.Iterator<java.lang.Integer> intItor24 = intList20.iterator();
    boolean boolean25 = booleanList4.retainAll(intList20);
    java.lang.String str26 = booleanList4.toString();
    java.lang.String str27 = booleanList4.toString();
    boolean boolean28 = uLong0.equals(booleanList4);
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience37 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            4.0d, (short) 100, 281475010265075L, (-4), 0.0d, (-7), 281474993487888L, (short) 255);
    double double38 = gPSSatInViewScience37.getMaxDistance();
    boolean boolean39 = uLong0.equals(gPSSatInViewScience37);
    double double40 = gPSSatInViewScience37.getMaxDistance();
    double double41 = gPSSatInViewScience37.getStdDevDistance();
    double double42 = gPSSatInViewScience37.getStdDevDistance();
    org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281474993487886L + "'",
        long1.equals(281474993487886L));
    org.junit.Assert.assertNotNull(booleanArray3);
    org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
    org.junit.Assert.assertNotNull(booleanSpliterator6);
    org.junit.Assert.assertNotNull(byteArray14);
    org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + true + "'", boolean16);
    org.junit.Assert.assertNotNull(intArray19);
    org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + true + "'", boolean21);
    org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
    org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + false + "'", !boolean23);
    org.junit.Assert.assertNotNull(intItor24);
    org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + true + "'", boolean25);
    org.junit.Assert.assertTrue("'" + str26 + "' != '" + "[]" + "'", str26.equals("[]"));
    org.junit.Assert.assertTrue("'" + str27 + "' != '" + "[]" + "'", str27.equals("[]"));
    org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + false + "'", !boolean28);
    org.junit.Assert.assertTrue("'" + double38 + "' != '" + 100.0d + "'", double38 == 100.0d);
    org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + false + "'", !boolean39);
    org.junit.Assert.assertTrue("'" + double40 + "' != '" + 100.0d + "'", double40 == 100.0d);
    org.junit.Assert.assertTrue("'" + double41 + "' != '" + 2.81474993487888E14d + "'",
        double41 == 2.81474993487888E14d);
    org.junit.Assert.assertTrue("'" + double42 + "' != '" + 2.81474993487888E14d + "'",
        double42 == 2.81474993487888E14d);
  }

  @Test
  public void test1191() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1191");
    }
    org.ccsds.moims.mo.mal.structures.StringList stringList0 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    org.ccsds.moims.mo.mal.structures.ShortList shortList2 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            ' ');
    java.util.ListIterator<java.lang.Short> shortItor3 = shortList2.listIterator();
    boolean boolean4 = stringList0.equals(shortList2);
    java.util.stream.BaseStream[] baseStreamArray6 = new java.util.stream.BaseStream[0];
    @SuppressWarnings("unchecked")
    java.util.stream.BaseStream<java.lang.Float, java.util.stream.Stream<java.lang.Float>>[] floatBaseStreamArray7 =
        baseStreamArray6;
    java.util.stream.BaseStream<java.lang.Float, java.util.stream.Stream<java.lang.Float>>[] floatBaseStreamArray8 =
        stringList0
            .toArray(
                (java.util.stream.BaseStream<java.lang.Float, java.util.stream.Stream<java.lang.Float>>[]) baseStreamArray6);
    java.util.stream.Stream<java.lang.String> strStream9 = stringList0.stream();
    java.lang.Object obj10 = stringList0.clone();
    opssat.simulator.util.LoggerFormatter loggerFormatter11 =
        new opssat.simulator.util.LoggerFormatter();
    opssat.simulator.util.LoggerFormatter loggerFormatter12 =
        new opssat.simulator.util.LoggerFormatter();
    opssat.simulator.util.LoggerFormatter loggerFormatter13 =
        new opssat.simulator.util.LoggerFormatter();
    opssat.simulator.util.LoggerFormatter[] loggerFormatterArray14 =
        new opssat.simulator.util.LoggerFormatter[]{
          loggerFormatter11, loggerFormatter12, loggerFormatter13};
    opssat.simulator.util.LoggerFormatter[] loggerFormatterArray15 = stringList0
        .toArray(loggerFormatterArray14);
    java.util.stream.Stream<java.lang.String> strStream16 = stringList0.parallelStream();
    try {
      java.lang.String str19 = stringList0.set(33, "UnknownGUIData");
      org.junit.Assert.fail(
          "Expected exception of type java.lang.IndexOutOfBoundsException; message: Index: 33, Size: 0");
    } catch (java.lang.IndexOutOfBoundsException e) {
    }
    org.junit.Assert.assertNotNull(shortItor3);
    org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
    org.junit.Assert.assertNotNull(baseStreamArray6);
    org.junit.Assert.assertNotNull(floatBaseStreamArray7);
    org.junit.Assert.assertNotNull(floatBaseStreamArray8);
    org.junit.Assert.assertNotNull(strStream9);
    org.junit.Assert.assertNotNull(obj10);
    org.junit.Assert.assertNotNull(loggerFormatterArray14);
    org.junit.Assert.assertNotNull(loggerFormatterArray15);
    org.junit.Assert.assertNotNull(strStream16);
  }

  @Test
  public void test1192() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1192");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState11 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double12 = simulatorSpacecraftState11.getLatitude();
    java.lang.String str13 = simulatorSpacecraftState11.getMagField();
    float[] floatArray14 = simulatorSpacecraftState11.getR();
    simulatorSpacecraftState3.setQ(floatArray14);
    java.lang.String str16 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray14);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState20 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double21 = simulatorSpacecraftState20.getLatitude();
    double double22 = simulatorSpacecraftState20.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState26 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double27 = simulatorSpacecraftState26.getLatitude();
    java.lang.String str28 = simulatorSpacecraftState26.getMagField();
    float[] floatArray29 = simulatorSpacecraftState26.getR();
    simulatorSpacecraftState20.setQ(floatArray29);
    float[] floatArray31 = simulatorSpacecraftState20.getV();
    opssat.simulator.celestia.CelestiaData celestiaData32 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray14, floatArray31);
    float[] floatArray33 = celestiaData32.getQ();
    opssat.simulator.util.SimulatorData simulatorData37 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date38 = simulatorData37.getCurrentTime();
    int int39 = opssat.simulator.util.DateExtraction.getDayFromDate(date38);
    opssat.simulator.util.SimulatorData simulatorData41 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date42 = simulatorData41.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData44 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date45 = simulatorData44.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap46 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date42, date45);
    opssat.simulator.util.SimulatorHeader simulatorHeader47 =
        new opssat.simulator.util.SimulatorHeader(
            false, date38, date45);
    opssat.simulator.util.SimulatorData simulatorData48 = new opssat.simulator.util.SimulatorData(
        (short) 0, date38);
    celestiaData32.setDate(date38);
    int int50 = celestiaData32.getSeconds();
    celestiaData32.setDnx("OPS-SAT SoftSim:");
    celestiaData32.setAos(
        "00000:00:00:05:909  56    00000:00:00:00:013  0     hi!          executed false   |  executed false   | ");
    celestiaData32.setAnx("00000:00:00:00:021");
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + 340.0d + "'", double12 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str13 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str13.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray14);
    org.junit.Assert.assertTrue("'" + str16 + "' != '" + "UnknownGUIData" + "'",
        str16.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double21 + "' != '" + 340.0d + "'", double21 == 340.0d);
    org.junit.Assert.assertTrue("'" + double22 + "' != '" + 340.0d + "'", double22 == 340.0d);
    org.junit.Assert.assertTrue("'" + double27 + "' != '" + 340.0d + "'", double27 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str28 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str28.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray29);
    org.junit.Assert.assertNotNull(floatArray31);
    org.junit.Assert.assertNotNull(floatArray33);
    org.junit.Assert.assertNotNull(date38);
    org.junit.Assert.assertNotNull(date42);
    org.junit.Assert.assertNotNull(date45);
    org.junit.Assert.assertNotNull(timeUnitMap46);
  }

  @Test
  public void test1193() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1193");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    simulatorHeader12.setOrekitTLE1("");
    java.lang.String str16 = simulatorHeader12.DATE_FORMAT;
    java.lang.String str17 = simulatorHeader12.getOrekitTLE1();
    java.lang.String str18 = simulatorHeader12.toString();
    int int19 = simulatorHeader12.getDayStartDate();
    simulatorHeader12.setCelestiaPort(52);
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertTrue("'" + str16 + "' != '" + "yyyy:MM:dd HH:mm:ss z" + "'",
        str16.equals("yyyy:MM:dd HH:mm:ss z"));
    org.junit.Assert.assertTrue("'" + str17 + "' != '" + "" + "'", str17.equals(""));
  }

  @Test
  public void test1194() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1194");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState11 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double12 = simulatorSpacecraftState11.getLatitude();
    java.lang.String str13 = simulatorSpacecraftState11.getMagField();
    float[] floatArray14 = simulatorSpacecraftState11.getR();
    simulatorSpacecraftState3.setQ(floatArray14);
    java.lang.String str16 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray14);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState20 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double21 = simulatorSpacecraftState20.getLatitude();
    double double22 = simulatorSpacecraftState20.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState26 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double27 = simulatorSpacecraftState26.getLatitude();
    java.lang.String str28 = simulatorSpacecraftState26.getMagField();
    float[] floatArray29 = simulatorSpacecraftState26.getR();
    simulatorSpacecraftState20.setQ(floatArray29);
    float[] floatArray31 = simulatorSpacecraftState20.getV();
    opssat.simulator.celestia.CelestiaData celestiaData32 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray14, floatArray31);
    float[] floatArray33 = celestiaData32.getQ();
    java.lang.String str34 = celestiaData32.getAos();
    celestiaData32.setLos("2019:05:23 15:09:36 UTC");
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState40 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray43 = new float[]{28, 8};
    simulatorSpacecraftState40.setQ(floatArray43);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState48 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double49 = simulatorSpacecraftState48.getLatitude();
    java.lang.String str50 = simulatorSpacecraftState48.getMagField();
    float[] floatArray51 = simulatorSpacecraftState48.getR();
    simulatorSpacecraftState40.setQ(floatArray51);
    java.lang.String str53 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray51);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState57 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double58 = simulatorSpacecraftState57.getLatitude();
    double double59 = simulatorSpacecraftState57.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState63 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double64 = simulatorSpacecraftState63.getLatitude();
    java.lang.String str65 = simulatorSpacecraftState63.getMagField();
    float[] floatArray66 = simulatorSpacecraftState63.getR();
    simulatorSpacecraftState57.setQ(floatArray66);
    float[] floatArray68 = simulatorSpacecraftState57.getV();
    opssat.simulator.celestia.CelestiaData celestiaData69 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray51, floatArray68);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState73 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double74 = simulatorSpacecraftState73.getLatitude();
    double double75 = simulatorSpacecraftState73.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState79 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double80 = simulatorSpacecraftState79.getLatitude();
    java.lang.String str81 = simulatorSpacecraftState79.getMagField();
    float[] floatArray82 = simulatorSpacecraftState79.getR();
    simulatorSpacecraftState73.setQ(floatArray82);
    celestiaData69.setQ(floatArray82);
    celestiaData32.setRv(floatArray82);
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + 340.0d + "'", double12 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str13 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str13.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray14);
    org.junit.Assert.assertTrue("'" + str16 + "' != '" + "UnknownGUIData" + "'",
        str16.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double21 + "' != '" + 340.0d + "'", double21 == 340.0d);
    org.junit.Assert.assertTrue("'" + double22 + "' != '" + 340.0d + "'", double22 == 340.0d);
    org.junit.Assert.assertTrue("'" + double27 + "' != '" + 340.0d + "'", double27 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str28 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str28.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray29);
    org.junit.Assert.assertNotNull(floatArray31);
    org.junit.Assert.assertNotNull(floatArray33);
    org.junit.Assert.assertNull(str34);
    org.junit.Assert.assertNotNull(floatArray43);
    org.junit.Assert.assertTrue("'" + double49 + "' != '" + 340.0d + "'", double49 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str50 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str50.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray51);
    org.junit.Assert.assertTrue("'" + str53 + "' != '" + "UnknownGUIData" + "'",
        str53.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double58 + "' != '" + 340.0d + "'", double58 == 340.0d);
    org.junit.Assert.assertTrue("'" + double59 + "' != '" + 340.0d + "'", double59 == 340.0d);
    org.junit.Assert.assertTrue("'" + double64 + "' != '" + 340.0d + "'", double64 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str65 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str65.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray66);
    org.junit.Assert.assertNotNull(floatArray68);
    org.junit.Assert.assertTrue("'" + double74 + "' != '" + 340.0d + "'", double74 == 340.0d);
    org.junit.Assert.assertTrue("'" + double75 + "' != '" + 340.0d + "'", double75 == 340.0d);
    org.junit.Assert.assertTrue("'" + double80 + "' != '" + 340.0d + "'", double80 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str81 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str81.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray82);
  }

  @Test
  public void test1195() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1195");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    int int8 = simulatorSpacecraftState3.getSatsInView();
    simulatorSpacecraftState3.setAltitude((-5));
    double[] doubleArray15 = new double[]{281474993487879L, 66, 97, (short) 255};
    double[] doubleArray20 = new double[]{281474993487879L, 66, 97, (short) 255};
    double[] doubleArray25 = new double[]{281474993487879L, 66, 97, (short) 255};
    double[] doubleArray30 = new double[]{281474993487879L, 66, 97, (short) 255};
    double[] doubleArray35 = new double[]{281474993487879L, 66, 97, (short) 255};
    double[] doubleArray40 = new double[]{281474993487879L, 66, 97, (short) 255};
    double[][] doubleArray41 = new double[][]{doubleArray15, doubleArray20, doubleArray25,
      doubleArray30, doubleArray35, doubleArray40};
    simulatorSpacecraftState3.setRotation(doubleArray41);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState46 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double47 = simulatorSpacecraftState46.getLatitude();
    double double48 = simulatorSpacecraftState46.getLongitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState52 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray55 = new float[]{28, 8};
    simulatorSpacecraftState52.setQ(floatArray55);
    int int57 = simulatorSpacecraftState52.getSatsInView();
    simulatorSpacecraftState52.setAltitude((-5));
    double[] doubleArray60 = simulatorSpacecraftState52.getSunVector();
    simulatorSpacecraftState46.setMagField(doubleArray60);
    simulatorSpacecraftState3.setMagnetometer(doubleArray60);
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + int8 + "' != '" + 0 + "'", int8 == 0);
    org.junit.Assert.assertNotNull(doubleArray15);
    org.junit.Assert.assertNotNull(doubleArray20);
    org.junit.Assert.assertNotNull(doubleArray25);
    org.junit.Assert.assertNotNull(doubleArray30);
    org.junit.Assert.assertNotNull(doubleArray35);
    org.junit.Assert.assertNotNull(doubleArray40);
    org.junit.Assert.assertNotNull(doubleArray41);
    org.junit.Assert.assertTrue("'" + double47 + "' != '" + 340.0d + "'", double47 == 340.0d);
    org.junit.Assert.assertTrue("'" + double48 + "' != '" + (-1.0d) + "'", double48 == (-1.0d));
    org.junit.Assert.assertNotNull(floatArray55);
    org.junit.Assert.assertTrue("'" + int57 + "' != '" + 0 + "'", int57 == 0);
    org.junit.Assert.assertNotNull(doubleArray60);
  }

  @Test
  public void test1196() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1196");
    }
    java.lang.Double[] doubleArray4 = new java.lang.Double[]{(-1.0d), 100.0d, 10.0d, 10.0d};
    java.util.ArrayList<java.lang.Double> doubleList5 = new java.util.ArrayList<java.lang.Double>();
    boolean boolean6 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Double>) doubleList5, doubleArray4);
    org.ccsds.moims.mo.mal.structures.UShort uShort7 =
        org.ccsds.moims.mo.mal.structures.URIList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort[] uShortArray8 =
        new org.ccsds.moims.mo.mal.structures.UShort[]{
          uShort7};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList9 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
    boolean boolean10 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList9, uShortArray8);
    uShortList9.ensureCapacity(0);
    int int14 = uShortList9.indexOf((byte) 1);
    uShortList9.clear();
    java.lang.Long[] longArray18 = new java.lang.Long[]{0L, 10L};
    java.util.ArrayList<java.lang.Long> longList19 = new java.util.ArrayList<java.lang.Long>();
    boolean boolean20 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Long>) longList19, longArray18);
    java.lang.Object obj21 = longList19.clone();
    boolean boolean22 = uShortList9.contains(longList19);
    boolean boolean23 = doubleList5.equals(boolean22);
    java.lang.Integer[] intArray26 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList27 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean28 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList27, intArray26);
    int int30 = intList27.lastIndexOf((byte) 10);
    boolean boolean31 = doubleList5.removeAll(intList27);
    org.ccsds.moims.mo.mal.structures.OctetList octetList32 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    octetList32.ensureCapacity(13);
    java.lang.Byte[] byteArray39 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList40 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean41 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList40, byteArray39);
    java.lang.Integer[] intArray44 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList45 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean46 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList45, intArray44);
    boolean boolean47 = byteList40.retainAll(intList45);
    boolean boolean48 = octetList32.containsAll(intList45);
    boolean boolean49 = doubleList5.removeAll(intList45);
    java.util.Spliterator<java.lang.Double> doubleSpliterator50 = doubleList5.spliterator();
    doubleList5.trimToSize();
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience60 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    double double61 = gPSSatInViewScience60.getMaxDistance();
    double double62 = gPSSatInViewScience60.getMaxDistance();
    double double63 = gPSSatInViewScience60.getMinElevation();
    double double64 = gPSSatInViewScience60.getAvgDistance();
    int int65 = doubleList5.lastIndexOf(gPSSatInViewScience60);
    doubleList5.ensureCapacity(38);
    org.junit.Assert.assertNotNull(doubleArray4);
    org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + true + "'", boolean6);
    org.junit.Assert.assertNotNull(uShort7);
    org.junit.Assert.assertNotNull(uShortArray8);
    org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
    org.junit.Assert.assertTrue("'" + int14 + "' != '" + (-1) + "'", int14 == (-1));
    org.junit.Assert.assertNotNull(longArray18);
    org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + true + "'", boolean20);
    org.junit.Assert.assertNotNull(obj21);
    org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + false + "'", !boolean22);
    org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + false + "'", !boolean23);
    org.junit.Assert.assertNotNull(intArray26);
    org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + true + "'", boolean28);
    org.junit.Assert.assertTrue("'" + int30 + "' != '" + (-1) + "'", int30 == (-1));
    org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + false + "'", !boolean31);
    org.junit.Assert.assertNotNull(byteArray39);
    org.junit.Assert.assertTrue("'" + boolean41 + "' != '" + true + "'", boolean41);
    org.junit.Assert.assertNotNull(intArray44);
    org.junit.Assert.assertTrue("'" + boolean46 + "' != '" + true + "'", boolean46);
    org.junit.Assert.assertTrue("'" + boolean47 + "' != '" + true + "'", boolean47);
    org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + false + "'", !boolean48);
    org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + false + "'", !boolean49);
    org.junit.Assert.assertNotNull(doubleSpliterator50);
    org.junit.Assert.assertTrue("'" + double61 + "' != '" + 0.0d + "'", double61 == 0.0d);
    org.junit.Assert.assertTrue("'" + double62 + "' != '" + 0.0d + "'", double62 == 0.0d);
    org.junit.Assert.assertTrue("'" + double63 + "' != '" + 48.0d + "'", double63 == 48.0d);
    org.junit.Assert.assertTrue("'" + double64 + "' != '" + 1.0d + "'", double64 == 1.0d);
    org.junit.Assert.assertTrue("'" + int65 + "' != '" + (-1) + "'", int65 == (-1));
  }

  @Test
  public void test1197() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1197");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    int int14 = simulatorHeader12.getMinuteStartDate();
    simulatorHeader12.setOrekitPropagator("*0B");
    simulatorHeader12.setUseOrekitPropagator(true);
    simulatorHeader12.setOrekitTLE1("00000:00:00:00:052");
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
  }

  @Test
  public void test1198() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1198");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    boolean boolean14 = simulatorHeader12.isAutoStartSystem();
    int int15 = simulatorHeader12.getDayStartDate();
    java.lang.String str16 = simulatorHeader12.getOrekitTLE1();
    simulatorHeader12.setTimeFactor(23);
    int int19 = simulatorHeader12.getTimeFactor();
    java.lang.String str20 = simulatorHeader12.getOrekitTLE2();
    int int21 = simulatorHeader12.getYearStartDate();
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
    org.junit.Assert.assertNull(str16);
    org.junit.Assert.assertNull(str20);
  }

  @Test
  public void test1199() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1199");
    }
    java.lang.Short[] shortArray2 = new java.lang.Short[]{(short) -1, (short) 10};
    java.util.ArrayList<java.lang.Short> shortList3 = new java.util.ArrayList<java.lang.Short>();
    boolean boolean4 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Short>) shortList3, shortArray2);
    boolean boolean5 = shortList3.isEmpty();
    int int7 = shortList3.lastIndexOf(281475010265070L);
    org.ccsds.moims.mo.mal.structures.UShort uShort8 =
        org.ccsds.moims.mo.mal.structures.StringList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.OctetList octetList9 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int10 = octetList9.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort11 = octetList9.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UShort uShort12 =
        org.ccsds.moims.mo.mal.structures.OctetList.AREA_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort13 =
        org.ccsds.moims.mo.mal.structures.FloatList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort14 =
        org.ccsds.moims.mo.mal.structures.StringList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort15 =
        org.ccsds.moims.mo.mal.structures.URIList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort16 =
        org.ccsds.moims.mo.mal.structures.BooleanList.AREA_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort17 =
        org.ccsds.moims.mo.mal.structures.OctetList.AREA_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort uShort18 =
        org.ccsds.moims.mo.mal.structures.URIList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort[] uShortArray19 =
        new org.ccsds.moims.mo.mal.structures.UShort[]{
          uShort18};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList20 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
    boolean boolean21 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList20,
        uShortArray19);
    uShortList20.ensureCapacity(0);
    int int25 = uShortList20.indexOf((byte) 1);
    uShortList20.clear();
    java.lang.Long[] longArray29 = new java.lang.Long[]{0L, 10L};
    java.util.ArrayList<java.lang.Long> longList30 = new java.util.ArrayList<java.lang.Long>();
    boolean boolean31 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Long>) longList30, longArray29);
    java.lang.Object obj32 = longList30.clone();
    boolean boolean33 = uShortList20.contains(longList30);
    org.ccsds.moims.mo.mal.structures.UShort uShort34 =
        org.ccsds.moims.mo.mal.structures.UShortList.SERVICE_SHORT_FORM;
    boolean boolean35 = uShortList20.add(uShort34);
    org.ccsds.moims.mo.mal.structures.UShort[] uShortArray36 =
        new org.ccsds.moims.mo.mal.structures.UShort[]{
          uShort8, uShort11, uShort12, uShort13, uShort14, uShort15, uShort16, uShort17, uShort34};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList37 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
    boolean boolean38 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList37,
        uShortArray36);
    uShortList37.ensureCapacity(100);
    boolean boolean42 = uShortList37.equals(9);
    org.ccsds.moims.mo.mal.structures.OctetList octetList43 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int44 = octetList43.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort45 = octetList43.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor46 = octetList43.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor48 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList43, "hi!");
    java.lang.Object obj49 = argumentDescriptor48.getType();
    int int50 = uShortList37.indexOf(obj49);
    int int51 = shortList3.lastIndexOf(obj49);
    java.util.stream.Stream<java.lang.Short> shortStream52 = shortList3.parallelStream();
    opssat.simulator.util.SimulatorData simulatorData54 = new opssat.simulator.util.SimulatorData(
        8);
    java.lang.String str55 = simulatorData54.toString();
    boolean boolean56 = shortList3.contains(simulatorData54);
    simulatorData54.toggleSimulatorRunning();
    org.junit.Assert.assertNotNull(shortArray2);
    org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
    org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + false + "'", !boolean5);
    org.junit.Assert.assertTrue("'" + int7 + "' != '" + (-1) + "'", int7 == (-1));
    org.junit.Assert.assertNotNull(uShort8);
    org.junit.Assert.assertTrue("'" + int10 + "' != '" + (-7) + "'", int10.equals((-7)));
    org.junit.Assert.assertNotNull(uShort11);
    org.junit.Assert.assertNotNull(uShort12);
    org.junit.Assert.assertNotNull(uShort13);
    org.junit.Assert.assertNotNull(uShort14);
    org.junit.Assert.assertNotNull(uShort15);
    org.junit.Assert.assertNotNull(uShort16);
    org.junit.Assert.assertNotNull(uShort17);
    org.junit.Assert.assertNotNull(uShort18);
    org.junit.Assert.assertNotNull(uShortArray19);
    org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + true + "'", boolean21);
    org.junit.Assert.assertTrue("'" + int25 + "' != '" + (-1) + "'", int25 == (-1));
    org.junit.Assert.assertNotNull(longArray29);
    org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + true + "'", boolean31);
    org.junit.Assert.assertNotNull(obj32);
    org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + false + "'", !boolean33);
    org.junit.Assert.assertNotNull(uShort34);
    org.junit.Assert.assertTrue("'" + boolean35 + "' != '" + true + "'", boolean35);
    org.junit.Assert.assertNotNull(uShortArray36);
    org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + true + "'", boolean38);
    org.junit.Assert.assertTrue("'" + boolean42 + "' != '" + false + "'", !boolean42);
    org.junit.Assert.assertTrue("'" + int44 + "' != '" + (-7) + "'", int44.equals((-7)));
    org.junit.Assert.assertNotNull(uShort45);
    org.junit.Assert.assertNotNull(byteItor46);
    org.junit.Assert.assertNotNull(obj49);
    org.junit.Assert.assertTrue("'" + int50 + "' != '" + (-1) + "'", int50 == (-1));
    org.junit.Assert.assertTrue("'" + int51 + "' != '" + (-1) + "'", int51 == (-1));
    org.junit.Assert.assertNotNull(shortStream52);
// flaky:         org.junit.Assert.assertTrue("'" + str55 + "' != '" + "{counter=8, methodsExecuted=0, currentTime=Thu May 23 15:10:51 UTC 2019}" + "'", str55.equals("{counter=8, methodsExecuted=0, currentTime=Thu May 23 15:10:51 UTC 2019}"));
    org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + false + "'", !boolean56);
  }

  @Test
  public void test1200() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1200");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor3 = octetList0.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor5 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList0, "hi!");
    argumentDescriptor5.restoreArgument();
    argumentDescriptor5.setName("01700.0000000");
    java.lang.Object obj9 = argumentDescriptor5.getType();
    java.lang.String str10 = argumentDescriptor5.getName();
    java.lang.Object obj11 = argumentDescriptor5.getType();
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(byteItor3);
    org.junit.Assert.assertNotNull(obj9);
    org.junit.Assert.assertTrue("'" + str10 + "' != '" + "01700.0000000" + "'",
        str10.equals("01700.0000000"));
    org.junit.Assert.assertNotNull(obj11);
  }

  @Test
  public void test1201() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1201");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getServiceNumber();
    org.ccsds.moims.mo.mal.structures.OctetList octetList3 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int4 = octetList3.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort5 = octetList3.getAreaNumber();
    java.lang.Object[] objArray6 = octetList3.toArray();
    org.ccsds.moims.mo.mal.structures.OctetList octetList7 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int8 = octetList7.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort9 = octetList7.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor10 = octetList7.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor12 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList7, "hi!");
    org.ccsds.moims.mo.mal.structures.UShort uShort13 = octetList7.getAreaNumber();
    java.lang.Boolean[] booleanArray16 = new java.lang.Boolean[]{true, true};
    java.util.ArrayList<java.lang.Boolean> booleanList17 =
        new java.util.ArrayList<java.lang.Boolean>();
    boolean boolean18 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Boolean>) booleanList17, booleanArray16);
    int int20 = booleanList17.indexOf(10);
    java.util.Iterator<java.lang.Boolean> booleanItor21 = booleanList17.iterator();
    java.util.Spliterator<java.lang.Boolean> booleanSpliterator22 = booleanList17.spliterator();
    org.ccsds.moims.mo.mal.structures.OctetList octetList23 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int24 = octetList23.getTypeShortForm();
    java.lang.Object obj25 = octetList23.clone();
    octetList23.trimToSize();
    boolean boolean27 = booleanList17.contains(octetList23);
    org.ccsds.moims.mo.mal.structures.OctetList octetList28 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int29 = octetList28.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort30 = octetList28.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.OctetList octetList31 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int32 = octetList31.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort33 = octetList31.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet34 = octetList31.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.OctetList octetList35 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int36 = octetList35.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort37 = octetList35.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.OctetList[] octetListArray38 =
        new org.ccsds.moims.mo.mal.structures.OctetList[]{
          octetList3, octetList7, octetList23, octetList28, octetList31, octetList35};
    org.ccsds.moims.mo.mal.structures.OctetList[] octetListArray39 = octetList0
        .toArray(octetListArray38);
    boolean boolean40 = octetList0.isEmpty();
    java.lang.Integer int41 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet42 = octetList0.getAreaVersion();
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertTrue("'" + int4 + "' != '" + (-7) + "'", int4.equals((-7)));
    org.junit.Assert.assertNotNull(uShort5);
    org.junit.Assert.assertNotNull(objArray6);
    org.junit.Assert.assertTrue("'" + int8 + "' != '" + (-7) + "'", int8.equals((-7)));
    org.junit.Assert.assertNotNull(uShort9);
    org.junit.Assert.assertNotNull(byteItor10);
    org.junit.Assert.assertNotNull(uShort13);
    org.junit.Assert.assertNotNull(booleanArray16);
    org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + true + "'", boolean18);
    org.junit.Assert.assertTrue("'" + int20 + "' != '" + (-1) + "'", int20 == (-1));
    org.junit.Assert.assertNotNull(booleanItor21);
    org.junit.Assert.assertNotNull(booleanSpliterator22);
    org.junit.Assert.assertTrue("'" + int24 + "' != '" + (-7) + "'", int24.equals((-7)));
    org.junit.Assert.assertNotNull(obj25);
    org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + false + "'", !boolean27);
    org.junit.Assert.assertTrue("'" + int29 + "' != '" + (-7) + "'", int29.equals((-7)));
    org.junit.Assert.assertNotNull(uShort30);
    org.junit.Assert.assertTrue("'" + int32 + "' != '" + (-7) + "'", int32.equals((-7)));
    org.junit.Assert.assertNotNull(uShort33);
    org.junit.Assert.assertNotNull(uOctet34);
    org.junit.Assert.assertTrue("'" + int36 + "' != '" + (-7) + "'", int36.equals((-7)));
    org.junit.Assert.assertNotNull(uShort37);
    org.junit.Assert.assertNotNull(octetListArray38);
    org.junit.Assert.assertNotNull(octetListArray39);
    org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + true + "'", boolean40);
    org.junit.Assert.assertTrue("'" + int41 + "' != '" + (-7) + "'", int41.equals((-7)));
    org.junit.Assert.assertNotNull(uOctet42);
  }

  @Test
  public void test1202() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1202");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double4 = simulatorSpacecraftState3.getLatitude();
    double double5 = simulatorSpacecraftState3.getLongitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState9 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray12 = new float[]{28, 8};
    simulatorSpacecraftState9.setQ(floatArray12);
    int int14 = simulatorSpacecraftState9.getSatsInView();
    simulatorSpacecraftState9.setAltitude((-5));
    double[] doubleArray17 = simulatorSpacecraftState9.getSunVector();
    simulatorSpacecraftState3.setMagField(doubleArray17);
    double double19 = simulatorSpacecraftState3.getLongitude();
    simulatorSpacecraftState3.setAltitude((short) 0);
    org.junit.Assert.assertTrue("'" + double4 + "' != '" + 340.0d + "'", double4 == 340.0d);
    org.junit.Assert.assertTrue("'" + double5 + "' != '" + (-1.0d) + "'", double5 == (-1.0d));
    org.junit.Assert.assertNotNull(floatArray12);
    org.junit.Assert.assertTrue("'" + int14 + "' != '" + 0 + "'", int14 == 0);
    org.junit.Assert.assertNotNull(doubleArray17);
    org.junit.Assert.assertTrue("'" + double19 + "' != '" + (-1.0d) + "'", double19 == (-1.0d));
  }

  @Test
  public void test1203() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1203");
    }
    opssat.simulator.util.SimulatorData simulatorData1 = new opssat.simulator.util.SimulatorData(
        (-18));
    simulatorData1.setCounter((-1));
    simulatorData1.feedTimeElapsed(21);
    java.lang.String str6 = simulatorData1.getCurrentDay();
    boolean boolean7 = simulatorData1.isTimeRunning();
    simulatorData1.incrementMethods();
    long long9 = simulatorData1.getCurrentTimeMillis();
    org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
    org.junit.Assert.assertTrue("'" + long9 + "' != '" + 21L + "'", long9 == 21L);
  }

  @Test
  public void test1204() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1204");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    int int14 = simulatorHeader12.getMinuteStartDate();
    boolean boolean15 = simulatorHeader12.isUpdateInternet();
    java.lang.String str16 = simulatorHeader12.DATE_FORMAT;
    boolean boolean17 = simulatorHeader12.checkStartBeforeEnd();
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
    org.junit.Assert.assertTrue("'" + str16 + "' != '" + "yyyy:MM:dd HH:mm:ss z" + "'",
        str16.equals("yyyy:MM:dd HH:mm:ss z"));
    org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + true + "'", boolean17);
  }

  @Test
  public void test1205() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1205");
    }
    opssat.simulator.util.SimulatorData simulatorData1 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date2 = simulatorData1.getCurrentTime();
    java.util.Date date3 = simulatorData1.getCurrentTime();
    simulatorData1.toggleSimulatorRunning();
    long long5 = simulatorData1.getCurrentTimeMillis();
    simulatorData1.feedTimeElapsed(281474993487885L);
    org.junit.Assert.assertNotNull(date2);
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertTrue("'" + long5 + "' != '" + 0L + "'", long5 == 0L);
  }

  @Test
  public void test1206() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1206");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    boolean boolean14 = simulatorHeader12.isAutoStartSystem();
    int int15 = simulatorHeader12.getDayStartDate();
    java.lang.String str16 = simulatorHeader12.getOrekitTLE1();
    simulatorHeader12.setTimeFactor(23);
    boolean boolean19 = simulatorHeader12.checkStartBeforeEnd();
    simulatorHeader12.setOrekitPropagator("Celestia");
    java.util.Date date23 = simulatorHeader12
        .parseStringIntoDate("opssat.simulator.util.wav.WavFileException: 00000:00:00:00:008");
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
    org.junit.Assert.assertNull(str16);
    org.junit.Assert.assertTrue("'" + boolean19 + "' != '" + true + "'", boolean19);
    org.junit.Assert.assertNull(date23);
  }

  @Test
  public void test1207() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1207");
    }
    org.ccsds.moims.mo.mal.structures.Identifier identifier0 =
        new org.ccsds.moims.mo.mal.structures.Identifier();
    org.ccsds.moims.mo.mal.structures.OctetList octetList1 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int2 = octetList1.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort3 = octetList1.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor4 = octetList1.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor6 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList1, "hi!");
    argumentDescriptor6.restoreArgument();
    argumentDescriptor6.restoreArgument();
    argumentDescriptor6.restoreArgument();
    java.lang.String str10 = argumentDescriptor6.toString();
    boolean boolean11 = identifier0.equals(str10);
    java.lang.Integer int12 = identifier0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort13 = identifier0.getServiceNumber();
    java.lang.Long long14 = identifier0.getShortForm();
    org.junit.Assert.assertTrue("'" + int2 + "' != '" + (-7) + "'", int2.equals((-7)));
    org.junit.Assert.assertNotNull(uShort3);
    org.junit.Assert.assertNotNull(byteItor4);
    org.junit.Assert.assertTrue("'" + str10 + "' != '" + "" + "'", str10.equals(""));
    org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + false + "'", !boolean11);
    org.junit.Assert.assertTrue("'" + int12 + "' != '" + 6 + "'", int12.equals(6));
    org.junit.Assert.assertNotNull(uShort13);
    org.junit.Assert.assertTrue("'" + long14 + "' != '" + 281474993487878L + "'",
        long14.equals(281474993487878L));
  }

  @Test
  public void test1208() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1208");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double4 = simulatorSpacecraftState3.getLatitude();
    java.lang.String str5 = simulatorSpacecraftState3.getMagField();
    java.lang.String str6 = simulatorSpacecraftState3.getSunVectorAsString();
    simulatorSpacecraftState3.setSatsInView(13);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState12 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double13 = simulatorSpacecraftState12.getLatitude();
    java.lang.String str14 = simulatorSpacecraftState12.getMagField();
    float[] floatArray15 = simulatorSpacecraftState12.getR();
    simulatorSpacecraftState3.setRv(floatArray15);
    try {
      float[] floatArray17 = simulatorSpacecraftState3.getV();
      org.junit.Assert
          .fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException; message: 3");
    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
    }
    org.junit.Assert.assertTrue("'" + double4 + "' != '" + 340.0d + "'", double4 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str5 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str5.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str6 + "' != '" + "X:[+.00000] ; Y:[+.00000] ; Z: [+.00000] units [N/A]" + "'",
        str6.equals("X:[+.00000] ; Y:[+.00000] ; Z: [+.00000] units [N/A]"));
    org.junit.Assert.assertTrue("'" + double13 + "' != '" + 340.0d + "'", double13 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str14 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str14.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray15);
  }

  @Test
  public void test1209() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1209");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    boolean boolean14 = simulatorHeader12.isAutoStartSystem();
    int int15 = simulatorHeader12.getDayStartDate();
    boolean boolean16 = simulatorHeader12.isUseCelestia();
    opssat.simulator.util.SimulatorData simulatorData19 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date20 = simulatorData19.getCurrentTime();
    int int21 = opssat.simulator.util.DateExtraction.getDayFromDate(date20);
    opssat.simulator.util.SimulatorData simulatorData23 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date24 = simulatorData23.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData26 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date27 = simulatorData26.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap28 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date24, date27);
    opssat.simulator.util.SimulatorHeader simulatorHeader29 =
        new opssat.simulator.util.SimulatorHeader(
            false, date20, date27);
    simulatorHeader12.setStartDate(date27);
    boolean boolean31 = simulatorHeader12.isAutoStartTime();
    simulatorHeader12.setUpdateInternet(true);
    int int34 = simulatorHeader12.getTimeFactor();
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
    org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
    org.junit.Assert.assertNotNull(date20);
    org.junit.Assert.assertNotNull(date24);
    org.junit.Assert.assertNotNull(date27);
    org.junit.Assert.assertNotNull(timeUnitMap28);
    org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + false + "'", !boolean31);
    org.junit.Assert.assertTrue("'" + int34 + "' != '" + 1 + "'", int34 == 1);
  }

  @Test
  public void test1210() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1210");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            100L, 281474993487886L, (byte) 0);
    java.lang.String str4 = simulatorSpacecraftState3.getMagnetometerAsString();
    org.junit.Assert.assertTrue(
        "'" + str4 + "' != '" + "X:[+00000.00] ; Y:[+00000.00] ; Z: [+00000.00] units [nT]" + "'",
        str4.equals("X:[+00000.00] ; Y:[+00000.00] ; Z: [+00000.00] units [nT]"));
  }

  @Test
  public void test1211() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1211");
    }
    opssat.simulator.util.SimulatorData simulatorData3 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date4 = simulatorData3.getCurrentTime();
    int int5 = opssat.simulator.util.DateExtraction.getDayFromDate(date4);
    opssat.simulator.util.SimulatorData simulatorData7 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date8 = simulatorData7.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData10 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date11 = simulatorData10.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap12 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date8, date11);
    opssat.simulator.util.SimulatorHeader simulatorHeader13 =
        new opssat.simulator.util.SimulatorHeader(
            false, date4, date11);
    opssat.simulator.util.SimulatorData simulatorData14 = new opssat.simulator.util.SimulatorData(
        (short) 0, date4);
    opssat.simulator.util.SimulatorData simulatorData17 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date18 = simulatorData17.getCurrentTime();
    int int19 = opssat.simulator.util.DateExtraction.getDayFromDate(date18);
    opssat.simulator.util.SimulatorData simulatorData21 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date22 = simulatorData21.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData24 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date25 = simulatorData24.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap26 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date22, date25);
    opssat.simulator.util.SimulatorHeader simulatorHeader27 =
        new opssat.simulator.util.SimulatorHeader(
            false, date18, date25);
    java.util.Date date28 = simulatorHeader27.getEndDate();
    opssat.simulator.util.SimulatorData simulatorData32 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date33 = simulatorData32.getCurrentTime();
    java.util.Date date34 = simulatorData32.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData35 = new opssat.simulator.util.SimulatorData(
        17, date34);
    opssat.simulator.util.SimulatorData simulatorData38 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date39 = simulatorData38.getCurrentTime();
    int int40 = opssat.simulator.util.DateExtraction.getDayFromDate(date39);
    opssat.simulator.util.SimulatorData simulatorData42 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date43 = simulatorData42.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData45 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date46 = simulatorData45.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap47 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date43, date46);
    opssat.simulator.util.SimulatorHeader simulatorHeader48 =
        new opssat.simulator.util.SimulatorHeader(
            false, date39, date46);
    opssat.simulator.util.SimulatorHeader simulatorHeader49 =
        new opssat.simulator.util.SimulatorHeader(
            false, date34, date39);
    simulatorHeader27.setEndDate(date34);
    simulatorData14.initFromHeader(simulatorHeader27);
    java.lang.String str52 = simulatorData14.getCurrentDay();
    org.junit.Assert.assertNotNull(date4);
    org.junit.Assert.assertNotNull(date8);
    org.junit.Assert.assertNotNull(date11);
    org.junit.Assert.assertNotNull(timeUnitMap12);
    org.junit.Assert.assertNotNull(date18);
    org.junit.Assert.assertNotNull(date22);
    org.junit.Assert.assertNotNull(date25);
    org.junit.Assert.assertNotNull(timeUnitMap26);
    org.junit.Assert.assertNotNull(date28);
    org.junit.Assert.assertNotNull(date33);
    org.junit.Assert.assertNotNull(date34);
    org.junit.Assert.assertNotNull(date39);
    org.junit.Assert.assertNotNull(date43);
    org.junit.Assert.assertNotNull(date46);
    org.junit.Assert.assertNotNull(timeUnitMap47);
  }

  @Test
  public void test1212() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1212");
    }
    float[] floatArray0 = null;
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState4 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray7 = new float[]{28, 8};
    simulatorSpacecraftState4.setQ(floatArray7);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState12 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double13 = simulatorSpacecraftState12.getLatitude();
    java.lang.String str14 = simulatorSpacecraftState12.getMagField();
    float[] floatArray15 = simulatorSpacecraftState12.getR();
    simulatorSpacecraftState4.setQ(floatArray15);
    java.lang.String str17 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray15);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState21 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double22 = simulatorSpacecraftState21.getLatitude();
    double double23 = simulatorSpacecraftState21.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState27 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double28 = simulatorSpacecraftState27.getLatitude();
    java.lang.String str29 = simulatorSpacecraftState27.getMagField();
    float[] floatArray30 = simulatorSpacecraftState27.getR();
    simulatorSpacecraftState21.setQ(floatArray30);
    float[] floatArray32 = simulatorSpacecraftState21.getV();
    opssat.simulator.celestia.CelestiaData celestiaData33 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray15, floatArray32);
    float[] floatArray34 = celestiaData33.getQ();
    opssat.simulator.util.SimulatorData simulatorData38 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date39 = simulatorData38.getCurrentTime();
    int int40 = opssat.simulator.util.DateExtraction.getDayFromDate(date39);
    opssat.simulator.util.SimulatorData simulatorData42 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date43 = simulatorData42.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData45 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date46 = simulatorData45.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap47 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date43, date46);
    opssat.simulator.util.SimulatorHeader simulatorHeader48 =
        new opssat.simulator.util.SimulatorHeader(
            false, date39, date46);
    opssat.simulator.util.SimulatorData simulatorData49 = new opssat.simulator.util.SimulatorData(
        (short) 0, date39);
    celestiaData33.setDate(date39);
    int int51 = celestiaData33.getMonths();
    float[] floatArray52 = celestiaData33.getRv();
    opssat.simulator.celestia.CelestiaData celestiaData53 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray0, floatArray52);
    java.lang.String str54 = celestiaData53.getLos();
    celestiaData53.setAnx("0309");
    celestiaData53.setDnx("2019/05/23-15:09:37");
    try {
      int int59 = celestiaData53.getYears();
      org.junit.Assert
          .fail("Expected exception of type java.lang.NullPointerException; message: null");
    } catch (java.lang.NullPointerException e) {
    }
    org.junit.Assert.assertNotNull(floatArray7);
    org.junit.Assert.assertTrue("'" + double13 + "' != '" + 340.0d + "'", double13 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str14 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str14.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray15);
    org.junit.Assert.assertTrue("'" + str17 + "' != '" + "UnknownGUIData" + "'",
        str17.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double22 + "' != '" + 340.0d + "'", double22 == 340.0d);
    org.junit.Assert.assertTrue("'" + double23 + "' != '" + 340.0d + "'", double23 == 340.0d);
    org.junit.Assert.assertTrue("'" + double28 + "' != '" + 340.0d + "'", double28 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str29 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str29.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray30);
    org.junit.Assert.assertNotNull(floatArray32);
    org.junit.Assert.assertNotNull(floatArray34);
    org.junit.Assert.assertNotNull(date39);
    org.junit.Assert.assertNotNull(date43);
    org.junit.Assert.assertNotNull(date46);
    org.junit.Assert.assertNotNull(timeUnitMap47);
    org.junit.Assert.assertNotNull(floatArray52);
    org.junit.Assert.assertNull(str54);
  }

  @Test
  public void test1214() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1214");
    }
    opssat.simulator.threading.SimulatorNode simulatorNode0 = null;
    opssat.simulator.peripherals.POpticalReceiver pOpticalReceiver2 =
        new opssat.simulator.peripherals.POpticalReceiver(
            simulatorNode0, "031008.320");
    java.lang.String str3 = pOpticalReceiver2.getName();
    try {
      pOpticalReceiver2.simPreloadFile("2019:05:23 15:09:34 UTC");
      org.junit.Assert
          .fail("Expected exception of type java.lang.NullPointerException; message: null");
    } catch (java.lang.NullPointerException e) {
    }
    org.junit.Assert.assertTrue("'" + str3 + "' != '" + "031008.320" + "'",
        str3.equals("031008.320"));
  }

  @Test
  public void test1215() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1215");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState3.setLongitude(4);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState9 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState9.setLongitude(4);
    int int12 = simulatorSpacecraftState9.getSatsInView();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState16 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double17 = simulatorSpacecraftState16.getLatitude();
    java.lang.String str18 = simulatorSpacecraftState16.getMagField();
    java.lang.String str19 = simulatorSpacecraftState16.toString();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState23 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double24 = simulatorSpacecraftState23.getLatitude();
    java.lang.String str25 = simulatorSpacecraftState23.getMagField();
    java.lang.String str26 = simulatorSpacecraftState23.toString();
    double[] doubleArray27 = simulatorSpacecraftState23.getSunVector();
    simulatorSpacecraftState16.setMagnetometer(doubleArray27);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState32 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double33 = simulatorSpacecraftState32.getLatitude();
    java.lang.String str34 = simulatorSpacecraftState32.getMagField();
    java.lang.String str35 = simulatorSpacecraftState32.toString();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState39 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double40 = simulatorSpacecraftState39.getLatitude();
    java.lang.String str41 = simulatorSpacecraftState39.getMagField();
    java.lang.String str42 = simulatorSpacecraftState39.toString();
    double[] doubleArray43 = simulatorSpacecraftState39.getSunVector();
    simulatorSpacecraftState32.setMagnetometer(doubleArray43);
    simulatorSpacecraftState16.setMagnetometer(doubleArray43);
    simulatorSpacecraftState9.setSunVector(doubleArray43);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState50 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray53 = new float[]{28, 8};
    simulatorSpacecraftState50.setQ(floatArray53);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState58 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double59 = simulatorSpacecraftState58.getLatitude();
    java.lang.String str60 = simulatorSpacecraftState58.getMagField();
    float[] floatArray61 = simulatorSpacecraftState58.getR();
    simulatorSpacecraftState50.setQ(floatArray61);
    java.lang.String str63 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray61);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState67 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double68 = simulatorSpacecraftState67.getLatitude();
    double double69 = simulatorSpacecraftState67.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState73 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double74 = simulatorSpacecraftState73.getLatitude();
    java.lang.String str75 = simulatorSpacecraftState73.getMagField();
    float[] floatArray76 = simulatorSpacecraftState73.getR();
    simulatorSpacecraftState67.setQ(floatArray76);
    float[] floatArray78 = simulatorSpacecraftState67.getV();
    opssat.simulator.celestia.CelestiaData celestiaData79 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray61, floatArray78);
    simulatorSpacecraftState9.setQ(floatArray78);
    simulatorSpacecraftState3.setQ(floatArray78);
    java.lang.String str82 = simulatorSpacecraftState3.getRotationAsString();
    org.junit.Assert.assertTrue("'" + int12 + "' != '" + 0 + "'", int12 == 0);
    org.junit.Assert.assertTrue("'" + double17 + "' != '" + 340.0d + "'", double17 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str18 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str18.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str19 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str19.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertTrue("'" + double24 + "' != '" + 340.0d + "'", double24 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str25 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str25.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str26 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str26.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray27);
    org.junit.Assert.assertTrue("'" + double33 + "' != '" + 340.0d + "'", double33 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str34 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str34.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str35 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str35.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertTrue("'" + double40 + "' != '" + 340.0d + "'", double40 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str41 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str41.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str42 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str42.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray43);
    org.junit.Assert.assertNotNull(floatArray53);
    org.junit.Assert.assertTrue("'" + double59 + "' != '" + 340.0d + "'", double59 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str60 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str60.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray61);
    org.junit.Assert.assertTrue("'" + str63 + "' != '" + "UnknownGUIData" + "'",
        str63.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double68 + "' != '" + 340.0d + "'", double68 == 340.0d);
    org.junit.Assert.assertTrue("'" + double69 + "' != '" + 340.0d + "'", double69 == 340.0d);
    org.junit.Assert.assertTrue("'" + double74 + "' != '" + 340.0d + "'", double74 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str75 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str75.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray76);
    org.junit.Assert.assertNotNull(floatArray78);
    org.junit.Assert.assertTrue("'" + str82 + "' != '"
        + "\n+0.000000 +0.000000 +0.000000\n+0.000000 +0.000000 +0.000000\n+0.000000 +0.000000 +0.000000"
        + "'",
        str82.equals(
            "\n+0.000000 +0.000000 +0.000000\n+0.000000 +0.000000 +0.000000\n+0.000000 +0.000000 +0.000000"));
  }

  @Test
  public void test1216() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1216");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessWavStreamOperatingBuffer endlessWavStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessWavStreamOperatingBuffer(
            logger0);
    java.lang.Object obj2 = endlessWavStreamOperatingBuffer1.getDataBuffer();
    int int3 = endlessWavStreamOperatingBuffer1.getOperatingIndex();
    org.junit.Assert.assertNotNull(obj2);
    org.junit.Assert.assertTrue("'" + int3 + "' != '" + 0 + "'", int3 == 0);
  }

  @Test
  public void test1217() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1217");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    int int8 = simulatorSpacecraftState3.getSatsInView();
    simulatorSpacecraftState3.setAltitude((-5));
    java.lang.String str11 = simulatorSpacecraftState3.getSunVectorAsString();
    double double12 = simulatorSpacecraftState3.getAltitude();
    float[] floatArray13 = simulatorSpacecraftState3.getMagnetometer();
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + int8 + "' != '" + 0 + "'", int8 == 0);
    org.junit.Assert.assertTrue(
        "'" + str11 + "' != '" + "X:[+.00000] ; Y:[+.00000] ; Z: [+.00000] units [N/A]" + "'",
        str11.equals("X:[+.00000] ; Y:[+.00000] ; Z: [+.00000] units [N/A]"));
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + (-5.0d) + "'", double12 == (-5.0d));
    org.junit.Assert.assertNotNull(floatArray13);
  }

  @Test
  public void test1220() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1220");
    }
    boolean boolean1 = opssat.simulator.threading.SimulatorNode
        .isInteger("UnknownDeviceDataTypeString{281474993487885}");
    org.junit.Assert.assertTrue("'" + boolean1 + "' != '" + false + "'", !boolean1);
  }

  @Test
  public void test1221() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1221");
    }
    opssat.simulator.util.SimulatorData simulatorData3 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date4 = simulatorData3.getCurrentTime();
    java.util.Date date5 = simulatorData3.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(17,
        date5);
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    int int11 = opssat.simulator.util.DateExtraction.getDayFromDate(date10);
    opssat.simulator.util.SimulatorData simulatorData13 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date14 = simulatorData13.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData16 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date17 = simulatorData16.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap18 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date14, date17);
    opssat.simulator.util.SimulatorHeader simulatorHeader19 =
        new opssat.simulator.util.SimulatorHeader(
            false, date10, date17);
    opssat.simulator.util.SimulatorHeader simulatorHeader20 =
        new opssat.simulator.util.SimulatorHeader(
            false, date5, date10);
    boolean boolean21 = simulatorHeader20.isAutoStartTime();
    simulatorHeader20.setUseCelestia(false);
    simulatorHeader20.setOrekitTLE1("[]");
    simulatorHeader20
        .setOrekitPropagator("opssat.simulator.util.wav.WavFileException: UnknownGUIData");
    java.util.Date date28 = simulatorHeader20.getEndDate();
    simulatorHeader20.setAutoStartSystem(true);
    simulatorHeader20.setTimeFactor(12);
    java.lang.String str33 = simulatorHeader20.getOrekitTLE1();
    org.junit.Assert.assertNotNull(date4);
    org.junit.Assert.assertNotNull(date5);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(date14);
    org.junit.Assert.assertNotNull(date17);
    org.junit.Assert.assertNotNull(timeUnitMap18);
    org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
    org.junit.Assert.assertNotNull(date28);
    org.junit.Assert.assertTrue("'" + str33 + "' != '" + "[]" + "'", str33.equals("[]"));
  }

  @Test
  public void test1222() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1222");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    float[] floatArray11 = new float[]{281474993487887L, 281475010265070L, 281474993487881L};
    simulatorSpacecraftState3.setRv(floatArray11);
    double[][] doubleArray13 = null;
    simulatorSpacecraftState3.setRotation(doubleArray13);
    float[] floatArray15 = simulatorSpacecraftState3.getR();
    double[] doubleArray16 = simulatorSpacecraftState3.getSunVector();
    double[] doubleArray17 = simulatorSpacecraftState3.getSunVector();
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertNotNull(floatArray11);
    org.junit.Assert.assertNotNull(floatArray15);
    org.junit.Assert.assertNotNull(doubleArray16);
    org.junit.Assert.assertNotNull(doubleArray17);
  }

  @Test
  public void test1223() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1223");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor3 = octetList0.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor5 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList0, "hi!");
    java.lang.Object obj6 = argumentDescriptor5.getType();
    java.lang.String str7 = argumentDescriptor5.toString();
    java.lang.Object obj8 = argumentDescriptor5.getType();
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(byteItor3);
    org.junit.Assert.assertNotNull(obj6);
    org.junit.Assert.assertTrue("'" + str7 + "' != '" + "" + "'", str7.equals(""));
    org.junit.Assert.assertNotNull(obj8);
  }

  @Test
  public void test1224() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1224");
    }
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience8 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    double double9 = gPSSatInViewScience8.getMaxDistance();
    double double10 = gPSSatInViewScience8.getMaxElevation();
    double double11 = gPSSatInViewScience8.getAvgDistance();
    double double12 = gPSSatInViewScience8.getAvgDistance();
    double double13 = gPSSatInViewScience8.getAvgDistance();
    double double14 = gPSSatInViewScience8.getAvgElevation();
    org.junit.Assert.assertTrue("'" + double9 + "' != '" + 0.0d + "'", double9 == 0.0d);
    org.junit.Assert.assertTrue("'" + double10 + "' != '" + 56.0d + "'", double10 == 56.0d);
    org.junit.Assert.assertTrue("'" + double11 + "' != '" + 1.0d + "'", double11 == 1.0d);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + 1.0d + "'", double12 == 1.0d);
    org.junit.Assert.assertTrue("'" + double13 + "' != '" + 1.0d + "'", double13 == 1.0d);
    org.junit.Assert.assertTrue("'" + double14 + "' != '" + 2.81474993487878E14d + "'",
        double14 == 2.81474993487878E14d);
  }

  @Test
  public void test1225() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1225");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double4 = simulatorSpacecraftState3.getLatitude();
    java.lang.String str5 = simulatorSpacecraftState3.getMagField();
    simulatorSpacecraftState3.setLatitude((-4));
    java.lang.String str8 = simulatorSpacecraftState3.getMagField();
    float[] floatArray9 = simulatorSpacecraftState3.getRv();
    java.lang.String str10 = simulatorSpacecraftState3.getRotationAsString();
    org.junit.Assert.assertTrue("'" + double4 + "' != '" + 340.0d + "'", double4 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str5 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str5.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str8 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str8.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray9);
    org.junit.Assert.assertTrue("'" + str10 + "' != '"
        + "\n+0.000000 +0.000000 +0.000000\n+0.000000 +0.000000 +0.000000\n+0.000000 +0.000000 +0.000000"
        + "'",
        str10.equals(
            "\n+0.000000 +0.000000 +0.000000\n+0.000000 +0.000000 +0.000000\n+0.000000 +0.000000 +0.000000"));
  }

  @Test
  public void test1226() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1226");
    }
    opssat.simulator.util.SimulatorData simulatorData3 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date4 = simulatorData3.getCurrentTime();
    int int5 = opssat.simulator.util.DateExtraction.getDayFromDate(date4);
    opssat.simulator.util.SimulatorData simulatorData7 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date8 = simulatorData7.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData10 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date11 = simulatorData10.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap12 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date8, date11);
    opssat.simulator.util.SimulatorHeader simulatorHeader13 =
        new opssat.simulator.util.SimulatorHeader(
            false, date4, date11);
    java.util.Date date14 = simulatorHeader13.getEndDate();
    opssat.simulator.util.SimulatorData simulatorData15 = new opssat.simulator.util.SimulatorData(
        13, date14);
    simulatorData15.incrementMethods();
    java.lang.String str17 = simulatorData15.getCurrentYear();
    java.lang.String str18 = simulatorData15.getCurrentDay();
    simulatorData15.setCounter(23);
    opssat.simulator.util.SimulatorData simulatorData23 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date24 = simulatorData23.getCurrentTime();
    int int25 = opssat.simulator.util.DateExtraction.getDayFromDate(date24);
    opssat.simulator.util.SimulatorData simulatorData27 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date28 = simulatorData27.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData30 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date31 = simulatorData30.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap32 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date28, date31);
    opssat.simulator.util.SimulatorHeader simulatorHeader33 =
        new opssat.simulator.util.SimulatorHeader(
            false, date24, date31);
    java.util.Date date34 = simulatorHeader33.getEndDate();
    int int35 = simulatorHeader33.getMinuteStartDate();
    simulatorHeader33.setUseOrekitPropagator(true);
    simulatorHeader33.setUseCelestia(false);
    boolean boolean40 = simulatorHeader33.isAutoStartTime();
    java.lang.String str41 = simulatorHeader33.getStartDateString();
    simulatorData15.initFromHeader(simulatorHeader33);
    int int43 = simulatorHeader33.getMonthStartDate();
    org.junit.Assert.assertNotNull(date4);
    org.junit.Assert.assertNotNull(date8);
    org.junit.Assert.assertNotNull(date11);
    org.junit.Assert.assertNotNull(timeUnitMap12);
    org.junit.Assert.assertNotNull(date14);
    org.junit.Assert.assertNotNull(date24);
    org.junit.Assert.assertNotNull(date28);
    org.junit.Assert.assertNotNull(date31);
    org.junit.Assert.assertNotNull(timeUnitMap32);
    org.junit.Assert.assertNotNull(date34);
    org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + false + "'", !boolean40);
  }

  @Test
  public void test1227() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1227");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessWavStreamOperatingBuffer endlessWavStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessWavStreamOperatingBuffer(
            logger0);
    int int2 = endlessWavStreamOperatingBuffer1.getOperatingIndex();
    byte[] byteArray4 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS
        .long2ByteArray(281475010265075L);
    endlessWavStreamOperatingBuffer1.setDataFromByteArray(byteArray4);
    java.util.logging.Logger logger6 = null;
    opssat.simulator.util.EndlessWavStreamOperatingBuffer endlessWavStreamOperatingBuffer7 =
        new opssat.simulator.util.EndlessWavStreamOperatingBuffer(
            logger6);
    java.lang.Object obj8 = endlessWavStreamOperatingBuffer7.getDataBuffer();
    boolean boolean10 = endlessWavStreamOperatingBuffer7.preparePath("03600.0000000");
    int int11 = endlessWavStreamOperatingBuffer7.getOperatingIndex();
    byte[] byteArray13 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS
        .long2ByteArray((short) -1);
    endlessWavStreamOperatingBuffer7.setDataFromByteArray(byteArray13);
    endlessWavStreamOperatingBuffer1.setDataFromByteArray(byteArray13);
    java.lang.Float[] floatArray19 = new java.lang.Float[]{1.0f, 1.0f, (-1.0f)};
    java.util.ArrayList<java.lang.Float> floatList20 = new java.util.ArrayList<java.lang.Float>();
    boolean boolean21 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Float>) floatList20, floatArray19);
    boolean boolean23 = floatList20.add((-1.0f));
    floatList20.trimToSize();
    org.ccsds.moims.mo.mal.structures.URI uRI26 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    java.lang.Integer int27 = uRI26.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.URI uRI29 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    org.ccsds.moims.mo.mal.structures.UOctet uOctet30 = uRI29.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.URI uRI32 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    org.ccsds.moims.mo.mal.structures.URI[] uRIArray33 =
        new org.ccsds.moims.mo.mal.structures.URI[]{
          uRI26, uRI29, uRI32};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList34 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
    boolean boolean35 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList34, uRIArray33);
    org.ccsds.moims.mo.mal.structures.FineTime fineTime36 =
        new org.ccsds.moims.mo.mal.structures.FineTime();
    int int37 = uRIList34.indexOf(fineTime36);
    uRIList34.ensureCapacity(40);
    org.ccsds.moims.mo.mal.structures.UShort uShort40 =
        org.ccsds.moims.mo.mal.structures.URIList.AREA_SHORT_FORM;
    boolean boolean41 = uRIList34.remove(uShort40);
    java.lang.Byte[] byteArray46 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList47 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean48 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList47, byteArray46);
    java.lang.Integer[] intArray51 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList52 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean53 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList52, intArray51);
    boolean boolean54 = byteList47.retainAll(intList52);
    java.lang.Integer[] intArray57 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList58 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean59 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList58, intArray57);
    int int61 = intList58.lastIndexOf((byte) 10);
    boolean boolean62 = intList52.removeAll(intList58);
    boolean boolean63 = uRIList34.removeAll(intList52);
    boolean boolean64 = floatList20.containsAll(intList52);
    java.util.stream.Stream<java.lang.Float> floatStream65 = floatList20.stream();
    int int67 = floatList20.lastIndexOf(4);
    java.util.stream.Stream<java.lang.Float> floatStream68 = floatList20.parallelStream();
    org.ccsds.moims.mo.mal.structures.FloatList floatList70 =
        new org.ccsds.moims.mo.mal.structures.FloatList(
            (byte) 0);
    java.lang.Integer int71 = floatList70.getTypeShortForm();
    java.lang.Integer int72 = floatList70.getTypeShortForm();
    boolean boolean73 = floatList20.equals(floatList70);
    byte[] byteArray75 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS.double2ByteArray(33);
    boolean boolean76 = floatList20.contains(byteArray75);
    endlessWavStreamOperatingBuffer1.setDataFromByteArray(byteArray75);
    org.junit.Assert.assertNotNull(byteArray4);
    org.junit.Assert.assertNotNull(obj8);
    org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + false + "'", !boolean10);
    org.junit.Assert.assertNotNull(byteArray13);
    org.junit.Assert.assertNotNull(floatArray19);
    org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + true + "'", boolean21);
    org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + true + "'", boolean23);
    org.junit.Assert.assertTrue("'" + int27 + "' != '" + 18 + "'", int27.equals(18));
    org.junit.Assert.assertNotNull(uOctet30);
    org.junit.Assert.assertNotNull(uRIArray33);
    org.junit.Assert.assertTrue("'" + boolean35 + "' != '" + true + "'", boolean35);
    org.junit.Assert.assertTrue("'" + int37 + "' != '" + (-1) + "'", int37 == (-1));
    org.junit.Assert.assertNotNull(uShort40);
    org.junit.Assert.assertTrue("'" + boolean41 + "' != '" + false + "'", !boolean41);
    org.junit.Assert.assertNotNull(byteArray46);
    org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + true + "'", boolean48);
    org.junit.Assert.assertNotNull(intArray51);
    org.junit.Assert.assertTrue("'" + boolean53 + "' != '" + true + "'", boolean53);
    org.junit.Assert.assertTrue("'" + boolean54 + "' != '" + true + "'", boolean54);
    org.junit.Assert.assertNotNull(intArray57);
    org.junit.Assert.assertTrue("'" + boolean59 + "' != '" + true + "'", boolean59);
    org.junit.Assert.assertTrue("'" + int61 + "' != '" + (-1) + "'", int61 == (-1));
    org.junit.Assert.assertTrue("'" + boolean62 + "' != '" + true + "'", boolean62);
    org.junit.Assert.assertTrue("'" + boolean63 + "' != '" + false + "'", !boolean63);
    org.junit.Assert.assertTrue("'" + boolean64 + "' != '" + false + "'", !boolean64);
    org.junit.Assert.assertNotNull(floatStream65);
    org.junit.Assert.assertTrue("'" + int67 + "' != '" + (-1) + "'", int67 == (-1));
    org.junit.Assert.assertNotNull(floatStream68);
    org.junit.Assert.assertTrue("'" + int71 + "' != '" + (-4) + "'", int71.equals((-4)));
    org.junit.Assert.assertTrue("'" + int72 + "' != '" + (-4) + "'", int72.equals((-4)));
    org.junit.Assert.assertTrue("'" + boolean73 + "' != '" + false + "'", !boolean73);
    org.junit.Assert.assertNotNull(byteArray75);
    org.junit.Assert.assertTrue("'" + boolean76 + "' != '" + false + "'", !boolean76);
  }

  @Test
  public void test1229() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1229");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    octetList0.ensureCapacity(13);
    java.lang.Byte[] byteArray7 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList8 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean9 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList8, byteArray7);
    java.lang.Integer[] intArray12 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList13 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean14 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList13, intArray12);
    boolean boolean15 = byteList8.retainAll(intList13);
    boolean boolean16 = octetList0.containsAll(intList13);
    boolean boolean17 = octetList0.isEmpty();
    java.util.stream.Stream<java.lang.Byte> byteStream18 = octetList0.stream();
    org.ccsds.moims.mo.mal.structures.OctetList octetList19 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int20 = octetList19.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort21 = octetList19.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor22 = octetList19.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor24 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList19, "hi!");
    org.ccsds.moims.mo.mal.structures.UShort uShort25 = octetList19.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UShort uShort26 = octetList19.getAreaNumber();
    boolean boolean27 = octetList19.isEmpty();
    boolean boolean28 = octetList0.contains(octetList19);
    org.junit.Assert.assertNotNull(byteArray7);
    org.junit.Assert.assertTrue("'" + boolean9 + "' != '" + true + "'", boolean9);
    org.junit.Assert.assertNotNull(intArray12);
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + true + "'", boolean14);
    org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + true + "'", boolean15);
    org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
    org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + true + "'", boolean17);
    org.junit.Assert.assertNotNull(byteStream18);
    org.junit.Assert.assertTrue("'" + int20 + "' != '" + (-7) + "'", int20.equals((-7)));
    org.junit.Assert.assertNotNull(uShort21);
    org.junit.Assert.assertNotNull(byteItor22);
    org.junit.Assert.assertNotNull(uShort25);
    org.junit.Assert.assertNotNull(uShort26);
    org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + true + "'", boolean27);
    org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + false + "'", !boolean28);
  }

  @Test
  public void test1230() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1230");
    }
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor2 =
        new opssat.simulator.util.ArgumentDescriptor(
            "SimulatorHeader{autoStartSystem=false, autoStartTime=false, timeFactor=1, startDate=Thu May 23 15:09:44 UTC 2019, endDate=Thu May 23 15:09:44 UTC 2019}",
            ":");
    org.ccsds.moims.mo.mal.structures.URIList uRIList4 =
        new org.ccsds.moims.mo.mal.structures.URIList(
            11);
    org.ccsds.moims.mo.mal.structures.Element element5 = uRIList4.createElement();
    org.ccsds.moims.mo.mal.structures.URI uRI7 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    java.lang.Integer int8 = uRI7.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet9 = uRI7.getAreaVersion();
    java.lang.Long long10 = uRI7.getShortForm();
    boolean boolean11 = uRIList4.equals(long10);
    java.lang.Long long12 = uRIList4.getShortForm();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet13 = uRIList4.getAreaVersion();
    argumentDescriptor2.setType(uOctet13);
    try {
      float float16 = argumentDescriptor2.getTypeAsFloatByIndex(7);
      org.junit.Assert.fail(
          "Expected exception of type java.lang.ClassCastException; message: org.ccsds.moims.mo.mal.structures.UOctet cannot be cast to [F");
    } catch (java.lang.ClassCastException e) {
    }
    org.junit.Assert.assertNotNull(element5);
    org.junit.Assert.assertTrue("'" + int8 + "' != '" + 18 + "'", int8.equals(18));
    org.junit.Assert.assertNotNull(uOctet9);
    org.junit.Assert.assertTrue("'" + long10 + "' != '" + 281474993487890L + "'",
        long10.equals(281474993487890L));
    org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + false + "'", !boolean11);
    org.junit.Assert.assertTrue("'" + long12 + "' != '" + 281475010265070L + "'",
        long12.equals(281475010265070L));
    org.junit.Assert.assertNotNull(uOctet13);
  }

  @Test
  public void test1231() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1231");
    }
    opssat.simulator.threading.SimulatorNode simulatorNode0 = null;
    opssat.simulator.peripherals.POpticalReceiver pOpticalReceiver2 =
        new opssat.simulator.peripherals.POpticalReceiver(
            simulatorNode0, "031008.320");
    java.lang.String str3 = pOpticalReceiver2.getName();
    java.lang.String str4 = pOpticalReceiver2.getName();
    opssat.simulator.threading.SimulatorNode simulatorNode5 = pOpticalReceiver2.getSimulatorNode();
    org.junit.Assert.assertTrue("'" + str3 + "' != '" + "031008.320" + "'",
        str3.equals("031008.320"));
    org.junit.Assert.assertTrue("'" + str4 + "' != '" + "031008.320" + "'",
        str4.equals("031008.320"));
    org.junit.Assert.assertNull(simulatorNode5);
  }

  @Test
  public void test1232() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1232");
    }
    java.lang.Float[] floatArray3 = new java.lang.Float[]{1.0f, 1.0f, (-1.0f)};
    java.util.ArrayList<java.lang.Float> floatList4 = new java.util.ArrayList<java.lang.Float>();
    boolean boolean5 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Float>) floatList4, floatArray3);
    boolean boolean7 = floatList4.add((-1.0f));
    floatList4.clear();
    java.util.stream.Stream<java.lang.Float> floatStream9 = floatList4.stream();
    java.lang.Byte[] byteArray14 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList15 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean16 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList15, byteArray14);
    java.lang.Integer[] intArray19 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList20 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean21 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList20, intArray19);
    boolean boolean22 = byteList15.retainAll(intList20);
    java.lang.Integer[] intArray25 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList26 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean27 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList26, intArray25);
    int int29 = intList26.lastIndexOf((byte) 10);
    boolean boolean30 = intList20.retainAll(intList26);
    boolean boolean31 = floatList4.containsAll(intList26);
    java.util.Spliterator<java.lang.Integer> intSpliterator32 = intList26.spliterator();
    java.util.stream.Stream<java.lang.Integer> intStream33 = intList26.parallelStream();
    java.lang.String str34 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(intList26);
    java.lang.Float[] floatArray38 = new java.lang.Float[]{1.0f, 1.0f, (-1.0f)};
    java.util.ArrayList<java.lang.Float> floatList39 = new java.util.ArrayList<java.lang.Float>();
    boolean boolean40 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Float>) floatList39, floatArray38);
    boolean boolean42 = floatList39.add((-1.0f));
    floatList39.clear();
    java.util.stream.Stream<java.lang.Float> floatStream44 = floatList39.stream();
    java.lang.Boolean[] booleanArray47 = new java.lang.Boolean[]{true, true};
    java.util.ArrayList<java.lang.Boolean> booleanList48 =
        new java.util.ArrayList<java.lang.Boolean>();
    boolean boolean49 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Boolean>) booleanList48, booleanArray47);
    int int51 = booleanList48.indexOf(10);
    java.util.Spliterator<java.lang.Boolean> booleanSpliterator52 = booleanList48.spliterator();
    java.lang.Byte[] byteArray55 = new java.lang.Byte[]{(byte) 100, (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList56 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean57 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList56, byteArray55);
    java.util.Iterator<java.lang.Byte> byteItor58 = byteList56.iterator();
    java.lang.Byte[] byteArray63 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList64 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean65 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList64, byteArray63);
    java.lang.Integer[] intArray68 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList69 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean70 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList69, intArray68);
    boolean boolean71 = byteList64.retainAll(intList69);
    java.lang.Integer[] intArray74 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList75 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean76 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList75, intArray74);
    int int78 = intList75.lastIndexOf((byte) 10);
    boolean boolean79 = intList69.removeAll(intList75);
    intList75.ensureCapacity(48);
    boolean boolean82 = byteList56.retainAll(intList75);
    boolean boolean83 = booleanList48.containsAll(intList75);
    boolean boolean84 = floatList39.retainAll(intList75);
    boolean boolean85 = intList26.removeAll(intList75);
    byte[] byteArray87 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS.long2ByteArray(10);
    org.ccsds.moims.mo.mal.structures.Blob blob88 = new org.ccsds.moims.mo.mal.structures.Blob(
        byteArray87);
    int int89 = intList75.indexOf(byteArray87);
    org.junit.Assert.assertNotNull(floatArray3);
    org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
    org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + true + "'", boolean7);
    org.junit.Assert.assertNotNull(floatStream9);
    org.junit.Assert.assertNotNull(byteArray14);
    org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + true + "'", boolean16);
    org.junit.Assert.assertNotNull(intArray19);
    org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + true + "'", boolean21);
    org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
    org.junit.Assert.assertNotNull(intArray25);
    org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + true + "'", boolean27);
    org.junit.Assert.assertTrue("'" + int29 + "' != '" + (-1) + "'", int29 == (-1));
    org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + true + "'", boolean30);
    org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + false + "'", !boolean31);
    org.junit.Assert.assertNotNull(intSpliterator32);
    org.junit.Assert.assertNotNull(intStream33);
    org.junit.Assert.assertTrue("'" + str34 + "' != '" + "UnknownGUIData" + "'",
        str34.equals("UnknownGUIData"));
    org.junit.Assert.assertNotNull(floatArray38);
    org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + true + "'", boolean40);
    org.junit.Assert.assertTrue("'" + boolean42 + "' != '" + true + "'", boolean42);
    org.junit.Assert.assertNotNull(floatStream44);
    org.junit.Assert.assertNotNull(booleanArray47);
    org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + true + "'", boolean49);
    org.junit.Assert.assertTrue("'" + int51 + "' != '" + (-1) + "'", int51 == (-1));
    org.junit.Assert.assertNotNull(booleanSpliterator52);
    org.junit.Assert.assertNotNull(byteArray55);
    org.junit.Assert.assertTrue("'" + boolean57 + "' != '" + true + "'", boolean57);
    org.junit.Assert.assertNotNull(byteItor58);
    org.junit.Assert.assertNotNull(byteArray63);
    org.junit.Assert.assertTrue("'" + boolean65 + "' != '" + true + "'", boolean65);
    org.junit.Assert.assertNotNull(intArray68);
    org.junit.Assert.assertTrue("'" + boolean70 + "' != '" + true + "'", boolean70);
    org.junit.Assert.assertTrue("'" + boolean71 + "' != '" + true + "'", boolean71);
    org.junit.Assert.assertNotNull(intArray74);
    org.junit.Assert.assertTrue("'" + boolean76 + "' != '" + true + "'", boolean76);
    org.junit.Assert.assertTrue("'" + int78 + "' != '" + (-1) + "'", int78 == (-1));
    org.junit.Assert.assertTrue("'" + boolean79 + "' != '" + true + "'", boolean79);
    org.junit.Assert.assertTrue("'" + boolean82 + "' != '" + true + "'", boolean82);
    org.junit.Assert.assertTrue("'" + boolean83 + "' != '" + false + "'", !boolean83);
    org.junit.Assert.assertTrue("'" + boolean84 + "' != '" + false + "'", !boolean84);
    org.junit.Assert.assertTrue("'" + boolean85 + "' != '" + true + "'", boolean85);
    org.junit.Assert.assertNotNull(byteArray87);
    org.junit.Assert.assertTrue("'" + int89 + "' != '" + (-1) + "'", int89 == (-1));
  }

  @Test
  public void test1233() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1233");
    }
    opssat.simulator.util.SimulatorData simulatorData1 = new opssat.simulator.util.SimulatorData(
        (-18));
    simulatorData1.setCounter((-1));
    simulatorData1.feedTimeElapsed(21);
    int int6 = simulatorData1.getTimeFactor();
    org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
  }

  @Test
  public void test1234() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1234");
    }
    org.ccsds.moims.mo.mal.structures.Identifier identifier1 =
        new org.ccsds.moims.mo.mal.structures.Identifier(
            "opssat");
    org.ccsds.moims.mo.mal.structures.OctetList octetList2 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int3 = octetList2.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort4 = octetList2.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor5 = octetList2.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor7 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList2, "hi!");
    argumentDescriptor7.restoreArgument();
    argumentDescriptor7.restoreArgument();
    argumentDescriptor7.restoreArgument();
    boolean boolean11 = identifier1.equals(argumentDescriptor7);
    java.lang.Integer int12 = identifier1.getTypeShortForm();
    org.junit.Assert.assertTrue("'" + int3 + "' != '" + (-7) + "'", int3.equals((-7)));
    org.junit.Assert.assertNotNull(uShort4);
    org.junit.Assert.assertNotNull(byteItor5);
    org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + false + "'", !boolean11);
    org.junit.Assert.assertTrue("'" + int12 + "' != '" + 6 + "'", int12.equals(6));
  }

  @Test
  public void test1235() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1235");
    }
    org.ccsds.moims.mo.mal.structures.URI uRI1 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    java.lang.Integer int2 = uRI1.getTypeShortForm();
    java.lang.String str3 = uRI1.toString();
    java.lang.String str4 = uRI1.getValue();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet5 = uRI1.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.ShortList shortList7 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            ' ');
    java.lang.Long long8 = shortList7.getShortForm();
    java.util.stream.Stream<java.lang.Short> shortStream9 = shortList7.stream();
    boolean boolean10 = uOctet5.equals(shortList7);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState14 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray17 = new float[]{28, 8};
    simulatorSpacecraftState14.setQ(floatArray17);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState22 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double23 = simulatorSpacecraftState22.getLatitude();
    java.lang.String str24 = simulatorSpacecraftState22.getMagField();
    float[] floatArray25 = simulatorSpacecraftState22.getR();
    simulatorSpacecraftState14.setQ(floatArray25);
    java.lang.String str27 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray25);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState31 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double32 = simulatorSpacecraftState31.getLatitude();
    double double33 = simulatorSpacecraftState31.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState37 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double38 = simulatorSpacecraftState37.getLatitude();
    java.lang.String str39 = simulatorSpacecraftState37.getMagField();
    float[] floatArray40 = simulatorSpacecraftState37.getR();
    simulatorSpacecraftState31.setQ(floatArray40);
    float[] floatArray42 = simulatorSpacecraftState31.getV();
    opssat.simulator.celestia.CelestiaData celestiaData43 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray25, floatArray42);
    float[] floatArray44 = celestiaData43.getQ();
    opssat.simulator.util.SimulatorData simulatorData48 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date49 = simulatorData48.getCurrentTime();
    int int50 = opssat.simulator.util.DateExtraction.getDayFromDate(date49);
    opssat.simulator.util.SimulatorData simulatorData52 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date53 = simulatorData52.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData55 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date56 = simulatorData55.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap57 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date53, date56);
    opssat.simulator.util.SimulatorHeader simulatorHeader58 =
        new opssat.simulator.util.SimulatorHeader(
            false, date49, date56);
    opssat.simulator.util.SimulatorData simulatorData59 = new opssat.simulator.util.SimulatorData(
        (short) 0, date49);
    celestiaData43.setDate(date49);
    int int61 = celestiaData43.getSeconds();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState65 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray68 = new float[]{28, 8};
    simulatorSpacecraftState65.setQ(floatArray68);
    celestiaData43.setQ(floatArray68);
    boolean boolean71 = shortList7.contains(celestiaData43);
    int int72 = celestiaData43.getYears();
    celestiaData43.setAos(
        "#Run the processing of internal models\nstartModels=false\n#Increment the simulated time (depends on startModels)\nstartTime=false\n#Speed up of time factor\ntimeFactor=1\n#Kepler elements for orbit A[km];E;i[deg];RAAN[deg];ARG_PER[deg];TRUE_A[deg] 7021.0;0.0;98.05;340.0;0.0;0.0\nkeplerElements=7021.0;0.0;98.05;340.0;0.0;0.0\n#Enable the Orekit library\norekit=false\n#Enable updates from Internet (used for gps constellation TLEs)\nupdateFromInternet=false\n#Configuration of the Celestia server\ncelestia=false\ncelestiaPort=0\n#Start and end dates of simulation\nstartDate=2019:05:23 15:09:41 UTC\nendDate=2019:05:23 15:09:41 UTC\n#Logging level to files found in $USER_HOME/.ops-sat-simulator/\n#Possible values SEVERE,INFO,FINE,FINER,FINEST,ALL\ncentralLogLevel=INFO\nsimulatorLogLevel=INFO\nconsoleLogLevel=INFO");
    int int75 = celestiaData43.getHours();
    int int76 = celestiaData43.getMinutes();
    float[] floatArray77 = celestiaData43.getQ();
    celestiaData43.setAos(
        "#Run the processing of internal models\nstartModels=false\n#Increment the simulated time (depends on startModels)\nstartTime=false\n#Speed up of time factor\ntimeFactor=1\n#Kepler elements for orbit A[km];E;i[deg];RAAN[deg];ARG_PER[deg];TRUE_A[deg] 7021.0;0.0;98.05;340.0;0.0;0.0\nkeplerElements=7021.0;0.0;98.05;340.0;0.0;0.0\n#Enable the Orekit library\norekit=true\n#Enable updates from Internet (used for gps constellation TLEs)\nupdateFromInternet=false\n#Configuration of the Celestia server\ncelestia=false\ncelestiaPort=0\n#Start and end dates of simulation\nstartDate=2019:05:23 15:09:32 UTC\nendDate=2019:05:23 15:09:32 UTC\n#Logging level to files found in $USER_HOME/.ops-sat-simulator/\n#Possible values SEVERE,INFO,FINE,FINER,FINEST,ALL\ncentralLogLevel=INFO\nsimulatorLogLevel=INFO\nconsoleLogLevel=INFO");
    org.junit.Assert.assertTrue("'" + int2 + "' != '" + 18 + "'", int2.equals(18));
    org.junit.Assert.assertTrue("'" + str3 + "' != '" + "0100.0000" + "'",
        str3.equals("0100.0000"));
    org.junit.Assert.assertTrue("'" + str4 + "' != '" + "0100.0000" + "'",
        str4.equals("0100.0000"));
    org.junit.Assert.assertNotNull(uOctet5);
    org.junit.Assert.assertTrue("'" + long8 + "' != '" + 281475010265079L + "'",
        long8.equals(281475010265079L));
    org.junit.Assert.assertNotNull(shortStream9);
    org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + false + "'", !boolean10);
    org.junit.Assert.assertNotNull(floatArray17);
    org.junit.Assert.assertTrue("'" + double23 + "' != '" + 340.0d + "'", double23 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str24 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str24.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray25);
    org.junit.Assert.assertTrue("'" + str27 + "' != '" + "UnknownGUIData" + "'",
        str27.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double32 + "' != '" + 340.0d + "'", double32 == 340.0d);
    org.junit.Assert.assertTrue("'" + double33 + "' != '" + 340.0d + "'", double33 == 340.0d);
    org.junit.Assert.assertTrue("'" + double38 + "' != '" + 340.0d + "'", double38 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str39 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str39.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray40);
    org.junit.Assert.assertNotNull(floatArray42);
    org.junit.Assert.assertNotNull(floatArray44);
    org.junit.Assert.assertNotNull(date49);
    org.junit.Assert.assertNotNull(date53);
    org.junit.Assert.assertNotNull(date56);
    org.junit.Assert.assertNotNull(timeUnitMap57);
    org.junit.Assert.assertNotNull(floatArray68);
    org.junit.Assert.assertTrue("'" + boolean71 + "' != '" + false + "'", !boolean71);
    org.junit.Assert.assertNotNull(floatArray77);
  }

  @Test
  public void test1236() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1236");
    }
    opssat.simulator.util.SimulatorData simulatorData3 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date4 = simulatorData3.getCurrentTime();
    int int5 = opssat.simulator.util.DateExtraction.getDayFromDate(date4);
    opssat.simulator.util.SimulatorData simulatorData7 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date8 = simulatorData7.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData10 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date11 = simulatorData10.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap12 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date8, date11);
    opssat.simulator.util.SimulatorHeader simulatorHeader13 =
        new opssat.simulator.util.SimulatorHeader(
            false, date4, date11);
    java.util.Date date14 = simulatorHeader13.getEndDate();
    opssat.simulator.util.SimulatorData simulatorData15 = new opssat.simulator.util.SimulatorData(
        13, date14);
    simulatorData15.incrementMethods();
    java.lang.String str17 = simulatorData15.getCurrentYear();
    java.lang.String str18 = simulatorData15.getCurrentYear();
    org.junit.Assert.assertNotNull(date4);
    org.junit.Assert.assertNotNull(date8);
    org.junit.Assert.assertNotNull(date11);
    org.junit.Assert.assertNotNull(timeUnitMap12);
    org.junit.Assert.assertNotNull(date14);
  }

  @Test
  public void test1237() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1237");
    }
    org.ccsds.moims.mo.mal.structures.StringList stringList0 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    org.ccsds.moims.mo.mal.structures.ShortList shortList2 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            ' ');
    java.util.ListIterator<java.lang.Short> shortItor3 = shortList2.listIterator();
    boolean boolean4 = stringList0.equals(shortList2);
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience13 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    double double14 = gPSSatInViewScience13.getMaxDistance();
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience23 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience32 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    double double33 = gPSSatInViewScience32.getMaxDistance();
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience42 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience51 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    double double52 = gPSSatInViewScience51.getStdDevElevation();
    opssat.simulator.orekit.GPSSatInViewScience[] gPSSatInViewScienceArray53 =
        new opssat.simulator.orekit.GPSSatInViewScience[]{
          gPSSatInViewScience13, gPSSatInViewScience23, gPSSatInViewScience32, gPSSatInViewScience42,
          gPSSatInViewScience51};
    opssat.simulator.orekit.GPSSatInViewScience[] gPSSatInViewScienceArray54 = shortList2
        .toArray(gPSSatInViewScienceArray53);
    shortList2.ensureCapacity(44);
    int int57 = shortList2.size();
    java.util.stream.Stream<java.lang.Short> shortStream58 = shortList2.parallelStream();
    boolean boolean60 = shortList2.add((short) -1);
    shortList2.ensureCapacity((short) 1);
    org.junit.Assert.assertNotNull(shortItor3);
    org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
    org.junit.Assert.assertTrue("'" + double14 + "' != '" + 0.0d + "'", double14 == 0.0d);
    org.junit.Assert.assertTrue("'" + double33 + "' != '" + 0.0d + "'", double33 == 0.0d);
    org.junit.Assert.assertTrue("'" + double52 + "' != '" + 11111.0d + "'", double52 == 11111.0d);
    org.junit.Assert.assertNotNull(gPSSatInViewScienceArray53);
    org.junit.Assert.assertNotNull(gPSSatInViewScienceArray54);
    org.junit.Assert.assertTrue("'" + int57 + "' != '" + 0 + "'", int57 == 0);
    org.junit.Assert.assertNotNull(shortStream58);
    org.junit.Assert.assertTrue("'" + boolean60 + "' != '" + true + "'", boolean60);
  }

  @Test
  public void test1238() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1238");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessWavStreamOperatingBuffer endlessWavStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessWavStreamOperatingBuffer(
            logger0);
    opssat.simulator.util.SimulatorData simulatorData4 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date5 = simulatorData4.getCurrentTime();
    int int6 = opssat.simulator.util.DateExtraction.getDayFromDate(date5);
    opssat.simulator.util.SimulatorData simulatorData8 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date9 = simulatorData8.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData11 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date12 = simulatorData11.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap13 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date9, date12);
    opssat.simulator.util.SimulatorHeader simulatorHeader14 =
        new opssat.simulator.util.SimulatorHeader(
            false, date5, date12);
    java.util.Date date15 = simulatorHeader14.getEndDate();
    int int16 = simulatorHeader14.getMinuteStartDate();
    simulatorHeader14.setUseOrekitPropagator(true);
    java.lang.String str19 = simulatorHeader14.toFileString();
    java.util.Date date21 = simulatorHeader14.parseStringIntoDate("yyyy:MM:dd HH:mm:ss z");
    endlessWavStreamOperatingBuffer1.setDataBuffer("yyyy:MM:dd HH:mm:ss z");
    org.ccsds.moims.mo.mal.structures.ULong uLong23 = new org.ccsds.moims.mo.mal.structures.ULong();
    java.lang.Long long24 = uLong23.getShortForm();
    java.lang.Boolean[] booleanArray26 = new java.lang.Boolean[]{true};
    java.util.ArrayList<java.lang.Boolean> booleanList27 =
        new java.util.ArrayList<java.lang.Boolean>();
    boolean boolean28 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Boolean>) booleanList27, booleanArray26);
    java.util.Spliterator<java.lang.Boolean> booleanSpliterator29 = booleanList27.spliterator();
    org.ccsds.moims.mo.mal.structures.OctetList octetList30 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    octetList30.ensureCapacity(13);
    java.lang.Byte[] byteArray37 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList38 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean39 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList38, byteArray37);
    java.lang.Integer[] intArray42 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList43 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean44 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList43, intArray42);
    boolean boolean45 = byteList38.retainAll(intList43);
    boolean boolean46 = octetList30.containsAll(intList43);
    java.util.Iterator<java.lang.Integer> intItor47 = intList43.iterator();
    boolean boolean48 = booleanList27.retainAll(intList43);
    java.lang.String str49 = booleanList27.toString();
    java.lang.String str50 = booleanList27.toString();
    boolean boolean51 = uLong23.equals(booleanList27);
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience60 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            4.0d, (short) 100, 281475010265075L, (-4), 0.0d, (-7), 281474993487888L, (short) 255);
    double double61 = gPSSatInViewScience60.getMaxDistance();
    boolean boolean62 = uLong23.equals(gPSSatInViewScience60);
    endlessWavStreamOperatingBuffer1.setDataBuffer(gPSSatInViewScience60);
    java.lang.String str64 = endlessWavStreamOperatingBuffer1.getDataBufferAsString();
    int int65 = endlessWavStreamOperatingBuffer1.getOperatingIndex();
    boolean boolean67 = endlessWavStreamOperatingBuffer1.preparePath("2019/05/23-15:10:47");
    org.junit.Assert.assertNotNull(date5);
    org.junit.Assert.assertNotNull(date9);
    org.junit.Assert.assertNotNull(date12);
    org.junit.Assert.assertNotNull(timeUnitMap13);
    org.junit.Assert.assertNotNull(date15);
    org.junit.Assert.assertNull(date21);
    org.junit.Assert.assertTrue("'" + long24 + "' != '" + 281474993487886L + "'",
        long24.equals(281474993487886L));
    org.junit.Assert.assertNotNull(booleanArray26);
    org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + true + "'", boolean28);
    org.junit.Assert.assertNotNull(booleanSpliterator29);
    org.junit.Assert.assertNotNull(byteArray37);
    org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + true + "'", boolean39);
    org.junit.Assert.assertNotNull(intArray42);
    org.junit.Assert.assertTrue("'" + boolean44 + "' != '" + true + "'", boolean44);
    org.junit.Assert.assertTrue("'" + boolean45 + "' != '" + true + "'", boolean45);
    org.junit.Assert.assertTrue("'" + boolean46 + "' != '" + false + "'", !boolean46);
    org.junit.Assert.assertNotNull(intItor47);
    org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + true + "'", boolean48);
    org.junit.Assert.assertTrue("'" + str49 + "' != '" + "[]" + "'", str49.equals("[]"));
    org.junit.Assert.assertTrue("'" + str50 + "' != '" + "[]" + "'", str50.equals("[]"));
    org.junit.Assert.assertTrue("'" + boolean51 + "' != '" + false + "'", !boolean51);
    org.junit.Assert.assertTrue("'" + double61 + "' != '" + 100.0d + "'", double61 == 100.0d);
    org.junit.Assert.assertTrue("'" + boolean62 + "' != '" + false + "'", !boolean62);
    org.junit.Assert
        .assertTrue(
            "'" + str64 + "' != '"
            + "Unknown data type [opssat.simulator.orekit.GPSSatInViewScience]" + "'",
            str64.equals("Unknown data type [opssat.simulator.orekit.GPSSatInViewScience]"));
    org.junit.Assert.assertTrue("'" + int65 + "' != '" + 0 + "'", int65 == 0);
    org.junit.Assert.assertTrue("'" + boolean67 + "' != '" + false + "'", !boolean67);
  }

  @Test
  public void test1239() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1239");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    int int14 = simulatorHeader12.getMinuteStartDate();
    simulatorHeader12.setUseOrekitPropagator(true);
    simulatorHeader12.setUseCelestia(false);
    boolean boolean19 = simulatorHeader12.isAutoStartTime();
    java.lang.String str20 = simulatorHeader12.getStartDateString();
    simulatorHeader12.setAutoStartTime(false);
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertTrue("'" + boolean19 + "' != '" + false + "'", !boolean19);
  }

  @Test
  public void test1240() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1240");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    float[] floatArray8 = simulatorSpacecraftState3.getQ();
    double double9 = simulatorSpacecraftState3.getLongitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState13 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray16 = new float[]{28, 8};
    simulatorSpacecraftState13.setQ(floatArray16);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState21 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double22 = simulatorSpacecraftState21.getLatitude();
    java.lang.String str23 = simulatorSpacecraftState21.getMagField();
    float[] floatArray24 = simulatorSpacecraftState21.getR();
    simulatorSpacecraftState13.setQ(floatArray24);
    java.lang.String str26 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray24);
    simulatorSpacecraftState3.setRv(floatArray24);
    java.lang.String str28 = simulatorSpacecraftState3.getSunVectorAsString();
    float[] floatArray29 = simulatorSpacecraftState3.getRv();
    java.lang.String str30 = simulatorSpacecraftState3.getSunVectorAsString();
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertNotNull(floatArray8);
    org.junit.Assert.assertTrue("'" + double9 + "' != '" + (-1.0d) + "'", double9 == (-1.0d));
    org.junit.Assert.assertNotNull(floatArray16);
    org.junit.Assert.assertTrue("'" + double22 + "' != '" + 340.0d + "'", double22 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str23 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str23.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray24);
    org.junit.Assert.assertTrue("'" + str26 + "' != '" + "UnknownGUIData" + "'",
        str26.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue(
        "'" + str28 + "' != '" + "X:[+.00000] ; Y:[+.00000] ; Z: [+.00000] units [N/A]" + "'",
        str28.equals("X:[+.00000] ; Y:[+.00000] ; Z: [+.00000] units [N/A]"));
    org.junit.Assert.assertNotNull(floatArray29);
    org.junit.Assert.assertTrue(
        "'" + str30 + "' != '" + "X:[+.00000] ; Y:[+.00000] ; Z: [+.00000] units [N/A]" + "'",
        str30.equals("X:[+.00000] ; Y:[+.00000] ; Z: [+.00000] units [N/A]"));
  }

  @Test
  public void test1241() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1241");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState11 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double12 = simulatorSpacecraftState11.getLatitude();
    java.lang.String str13 = simulatorSpacecraftState11.getMagField();
    float[] floatArray14 = simulatorSpacecraftState11.getR();
    simulatorSpacecraftState3.setQ(floatArray14);
    java.lang.String str16 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray14);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState20 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double21 = simulatorSpacecraftState20.getLatitude();
    double double22 = simulatorSpacecraftState20.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState26 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double27 = simulatorSpacecraftState26.getLatitude();
    java.lang.String str28 = simulatorSpacecraftState26.getMagField();
    float[] floatArray29 = simulatorSpacecraftState26.getR();
    simulatorSpacecraftState20.setQ(floatArray29);
    float[] floatArray31 = simulatorSpacecraftState20.getV();
    opssat.simulator.celestia.CelestiaData celestiaData32 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray14, floatArray31);
    float[] floatArray33 = celestiaData32.getQ();
    opssat.simulator.util.SimulatorData simulatorData37 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date38 = simulatorData37.getCurrentTime();
    int int39 = opssat.simulator.util.DateExtraction.getDayFromDate(date38);
    opssat.simulator.util.SimulatorData simulatorData41 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date42 = simulatorData41.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData44 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date45 = simulatorData44.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap46 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date42, date45);
    opssat.simulator.util.SimulatorHeader simulatorHeader47 =
        new opssat.simulator.util.SimulatorHeader(
            false, date38, date45);
    opssat.simulator.util.SimulatorData simulatorData48 = new opssat.simulator.util.SimulatorData(
        (short) 0, date38);
    celestiaData32.setDate(date38);
    int int50 = celestiaData32.getSeconds();
    celestiaData32.setDnx("OPS-SAT SoftSim:");
    java.lang.String str53 = celestiaData32.getAos();
    celestiaData32.setLos(
        "#Run the processing of internal models\nstartModels=false\n#Increment the simulated time (depends on startModels)\nstartTime=false\n#Speed up of time factor\ntimeFactor=1\n#Kepler elements for orbit A[km];E;i[deg];RAAN[deg];ARG_PER[deg];TRUE_A[deg] 7021.0;0.0;98.05;340.0;0.0;0.0\nkeplerElements=7021.0;0.0;98.05;340.0;0.0;0.0\n#Enable the Orekit library\norekit=false\n#Enable updates from Internet (used for gps constellation TLEs)\nupdateFromInternet=false\n#Configuration of the Celestia server\ncelestia=false\ncelestiaPort=0\n#Start and end dates of simulation\nstartDate=2019:05:23 15:10:24 UTC\nendDate=2019:05:23 15:10:24 UTC\n#Logging level to files found in $USER_HOME/.ops-sat-simulator/\n#Possible values SEVERE,INFO,FINE,FINER,FINEST,ALL\ncentralLogLevel=INFO\nsimulatorLogLevel=INFO\nconsoleLogLevel=INFO");
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + 340.0d + "'", double12 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str13 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str13.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray14);
    org.junit.Assert.assertTrue("'" + str16 + "' != '" + "UnknownGUIData" + "'",
        str16.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double21 + "' != '" + 340.0d + "'", double21 == 340.0d);
    org.junit.Assert.assertTrue("'" + double22 + "' != '" + 340.0d + "'", double22 == 340.0d);
    org.junit.Assert.assertTrue("'" + double27 + "' != '" + 340.0d + "'", double27 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str28 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str28.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray29);
    org.junit.Assert.assertNotNull(floatArray31);
    org.junit.Assert.assertNotNull(floatArray33);
    org.junit.Assert.assertNotNull(date38);
    org.junit.Assert.assertNotNull(date42);
    org.junit.Assert.assertNotNull(date45);
    org.junit.Assert.assertNotNull(timeUnitMap46);
    org.junit.Assert.assertNull(str53);
  }

  @Test
  public void test1242() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1242");
    }
    try {
      opssat.simulator.orekit.GPSSatInView gPSSatInView2 = new opssat.simulator.orekit.GPSSatInView(
          "0309", 0);
      org.junit.Assert.fail(
          "Expected exception of type java.lang.StringIndexOutOfBoundsException; message: String index out of range: -1");
    } catch (java.lang.StringIndexOutOfBoundsException e) {
    }
  }

  @Test
  public void test1243() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1243");
    }
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience8 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    double double9 = gPSSatInViewScience8.getMinElevation();
    double double10 = gPSSatInViewScience8.getMaxElevation();
    double double11 = gPSSatInViewScience8.getAvgDistance();
    double double12 = gPSSatInViewScience8.getMinDistance();
    org.junit.Assert.assertTrue("'" + double9 + "' != '" + 48.0d + "'", double9 == 48.0d);
    org.junit.Assert.assertTrue("'" + double10 + "' != '" + 56.0d + "'", double10 == 56.0d);
    org.junit.Assert.assertTrue("'" + double11 + "' != '" + 1.0d + "'", double11 == 1.0d);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + 9.0d + "'", double12 == 9.0d);
  }

  @Test
  public void test1244() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1244");
    }
    opssat.simulator.util.SimulatorSchedulerPiece simulatorSchedulerPiece3 =
        new opssat.simulator.util.SimulatorSchedulerPiece(
            60, 14, "01700.0000000");
    long long4 = simulatorSchedulerPiece3.getTime();
    simulatorSchedulerPiece3.setExecuted(false);
    org.junit.Assert.assertTrue("'" + long4 + "' != '" + 60L + "'", long4 == 60L);
  }

  @Test
  public void test1245() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1245");
    }
    org.ccsds.moims.mo.mal.structures.URI uRI1 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    java.lang.Integer int2 = uRI1.getTypeShortForm();
    java.lang.String str3 = uRI1.toString();
    java.lang.String str4 = uRI1.getValue();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet5 = uRI1.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.ShortList shortList7 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            ' ');
    java.lang.Long long8 = shortList7.getShortForm();
    java.util.stream.Stream<java.lang.Short> shortStream9 = shortList7.stream();
    boolean boolean10 = uOctet5.equals(shortList7);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState14 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray17 = new float[]{28, 8};
    simulatorSpacecraftState14.setQ(floatArray17);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState22 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double23 = simulatorSpacecraftState22.getLatitude();
    java.lang.String str24 = simulatorSpacecraftState22.getMagField();
    float[] floatArray25 = simulatorSpacecraftState22.getR();
    simulatorSpacecraftState14.setQ(floatArray25);
    java.lang.String str27 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray25);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState31 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double32 = simulatorSpacecraftState31.getLatitude();
    double double33 = simulatorSpacecraftState31.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState37 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double38 = simulatorSpacecraftState37.getLatitude();
    java.lang.String str39 = simulatorSpacecraftState37.getMagField();
    float[] floatArray40 = simulatorSpacecraftState37.getR();
    simulatorSpacecraftState31.setQ(floatArray40);
    float[] floatArray42 = simulatorSpacecraftState31.getV();
    opssat.simulator.celestia.CelestiaData celestiaData43 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray25, floatArray42);
    float[] floatArray44 = celestiaData43.getQ();
    opssat.simulator.util.SimulatorData simulatorData48 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date49 = simulatorData48.getCurrentTime();
    int int50 = opssat.simulator.util.DateExtraction.getDayFromDate(date49);
    opssat.simulator.util.SimulatorData simulatorData52 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date53 = simulatorData52.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData55 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date56 = simulatorData55.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap57 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date53, date56);
    opssat.simulator.util.SimulatorHeader simulatorHeader58 =
        new opssat.simulator.util.SimulatorHeader(
            false, date49, date56);
    opssat.simulator.util.SimulatorData simulatorData59 = new opssat.simulator.util.SimulatorData(
        (short) 0, date49);
    celestiaData43.setDate(date49);
    int int61 = celestiaData43.getSeconds();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState65 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray68 = new float[]{28, 8};
    simulatorSpacecraftState65.setQ(floatArray68);
    celestiaData43.setQ(floatArray68);
    boolean boolean71 = shortList7.contains(celestiaData43);
    int int72 = celestiaData43.getYears();
    celestiaData43.setAos(
        "#Run the processing of internal models\nstartModels=false\n#Increment the simulated time (depends on startModels)\nstartTime=false\n#Speed up of time factor\ntimeFactor=1\n#Kepler elements for orbit A[km];E;i[deg];RAAN[deg];ARG_PER[deg];TRUE_A[deg] 7021.0;0.0;98.05;340.0;0.0;0.0\nkeplerElements=7021.0;0.0;98.05;340.0;0.0;0.0\n#Enable the Orekit library\norekit=false\n#Enable updates from Internet (used for gps constellation TLEs)\nupdateFromInternet=false\n#Configuration of the Celestia server\ncelestia=false\ncelestiaPort=0\n#Start and end dates of simulation\nstartDate=2019:05:23 15:09:41 UTC\nendDate=2019:05:23 15:09:41 UTC\n#Logging level to files found in $USER_HOME/.ops-sat-simulator/\n#Possible values SEVERE,INFO,FINE,FINER,FINEST,ALL\ncentralLogLevel=INFO\nsimulatorLogLevel=INFO\nconsoleLogLevel=INFO");
    java.lang.String str75 = celestiaData43.getDate();
    int int76 = celestiaData43.getMinutes();
    float[] floatArray77 = celestiaData43.getRv();
    org.junit.Assert.assertTrue("'" + int2 + "' != '" + 18 + "'", int2.equals(18));
    org.junit.Assert.assertTrue("'" + str3 + "' != '" + "0100.0000" + "'",
        str3.equals("0100.0000"));
    org.junit.Assert.assertTrue("'" + str4 + "' != '" + "0100.0000" + "'",
        str4.equals("0100.0000"));
    org.junit.Assert.assertNotNull(uOctet5);
    org.junit.Assert.assertTrue("'" + long8 + "' != '" + 281475010265079L + "'",
        long8.equals(281475010265079L));
    org.junit.Assert.assertNotNull(shortStream9);
    org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + false + "'", !boolean10);
    org.junit.Assert.assertNotNull(floatArray17);
    org.junit.Assert.assertTrue("'" + double23 + "' != '" + 340.0d + "'", double23 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str24 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str24.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray25);
    org.junit.Assert.assertTrue("'" + str27 + "' != '" + "UnknownGUIData" + "'",
        str27.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double32 + "' != '" + 340.0d + "'", double32 == 340.0d);
    org.junit.Assert.assertTrue("'" + double33 + "' != '" + 340.0d + "'", double33 == 340.0d);
    org.junit.Assert.assertTrue("'" + double38 + "' != '" + 340.0d + "'", double38 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str39 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str39.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray40);
    org.junit.Assert.assertNotNull(floatArray42);
    org.junit.Assert.assertNotNull(floatArray44);
    org.junit.Assert.assertNotNull(date49);
    org.junit.Assert.assertNotNull(date53);
    org.junit.Assert.assertNotNull(date56);
    org.junit.Assert.assertNotNull(timeUnitMap57);
    org.junit.Assert.assertNotNull(floatArray68);
    org.junit.Assert.assertTrue("'" + boolean71 + "' != '" + false + "'", !boolean71);
    org.junit.Assert.assertNotNull(floatArray77);
  }

  @Test
  public void test1246() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1246");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessSingleStreamOperatingBuffer endlessSingleStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessSingleStreamOperatingBuffer(
            logger0);
    byte[] byteArray3 = endlessSingleStreamOperatingBuffer1.getDataAsByteArray('4');
    byte[] byteArray5 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS.int16_2ByteArray(15);
    endlessSingleStreamOperatingBuffer1.setDataFromByteArray(byteArray5);
    int int7 = endlessSingleStreamOperatingBuffer1.getOperatingIndex();
    byte[] byteArray9 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS
        .long2ByteArray(281475010265075L);
    org.ccsds.moims.mo.mal.structures.Blob blob10 = new org.ccsds.moims.mo.mal.structures.Blob(
        byteArray9);
    endlessSingleStreamOperatingBuffer1.setDataFromByteArray(byteArray9);
    try {
      long long13 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS
          .getLongFromByteArray(byteArray9, (-7));
      org.junit.Assert
          .fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
    } catch (java.lang.IndexOutOfBoundsException e) {
    }
    org.junit.Assert.assertNotNull(byteArray3);
    org.junit.Assert.assertNotNull(byteArray5);
    org.junit.Assert.assertTrue("'" + int7 + "' != '" + 0 + "'", int7 == 0);
    org.junit.Assert.assertNotNull(byteArray9);
  }

  @Test
  public void test1247() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1247");
    }
    opssat.simulator.util.SimulatorSchedulerPiece simulatorSchedulerPiece3 =
        new opssat.simulator.util.SimulatorSchedulerPiece(
            (-7), 64, "0.0");
    simulatorSchedulerPiece3.setExecuted(false);
    java.lang.String str6 = simulatorSchedulerPiece3.getArgumentTemplateDescription();
    org.junit.Assert.assertTrue("'" + str6 + "' != '" + "0.0" + "'", str6.equals("0.0"));
  }

  @Test
  public void test1248() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1248");
    }
    opssat.simulator.util.SimulatorData simulatorData3 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date4 = simulatorData3.getCurrentTime();
    int int5 = opssat.simulator.util.DateExtraction.getDayFromDate(date4);
    opssat.simulator.util.SimulatorData simulatorData7 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date8 = simulatorData7.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData10 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date11 = simulatorData10.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap12 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date8, date11);
    opssat.simulator.util.SimulatorHeader simulatorHeader13 =
        new opssat.simulator.util.SimulatorHeader(
            false, date4, date11);
    opssat.simulator.util.SimulatorData simulatorData14 = new opssat.simulator.util.SimulatorData(
        (short) 0, date4);
    opssat.simulator.util.SimulatorData simulatorData17 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date18 = simulatorData17.getCurrentTime();
    int int19 = opssat.simulator.util.DateExtraction.getDayFromDate(date18);
    opssat.simulator.util.SimulatorData simulatorData21 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date22 = simulatorData21.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData24 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date25 = simulatorData24.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap26 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date22, date25);
    opssat.simulator.util.SimulatorHeader simulatorHeader27 =
        new opssat.simulator.util.SimulatorHeader(
            false, date18, date25);
    java.util.Date date28 = simulatorHeader27.getEndDate();
    opssat.simulator.util.SimulatorData simulatorData32 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date33 = simulatorData32.getCurrentTime();
    java.util.Date date34 = simulatorData32.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData35 = new opssat.simulator.util.SimulatorData(
        17, date34);
    opssat.simulator.util.SimulatorData simulatorData38 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date39 = simulatorData38.getCurrentTime();
    int int40 = opssat.simulator.util.DateExtraction.getDayFromDate(date39);
    opssat.simulator.util.SimulatorData simulatorData42 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date43 = simulatorData42.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData45 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date46 = simulatorData45.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap47 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date43, date46);
    opssat.simulator.util.SimulatorHeader simulatorHeader48 =
        new opssat.simulator.util.SimulatorHeader(
            false, date39, date46);
    opssat.simulator.util.SimulatorHeader simulatorHeader49 =
        new opssat.simulator.util.SimulatorHeader(
            false, date34, date39);
    simulatorHeader27.setEndDate(date34);
    simulatorData14.initFromHeader(simulatorHeader27);
    boolean boolean52 = simulatorHeader27.checkStartBeforeEnd();
    simulatorHeader27.setAutoStartSystem(true);
    boolean boolean55 = simulatorHeader27.checkStartBeforeEnd();
    int int56 = simulatorHeader27.getYearStartDate();
    org.junit.Assert.assertNotNull(date4);
    org.junit.Assert.assertNotNull(date8);
    org.junit.Assert.assertNotNull(date11);
    org.junit.Assert.assertNotNull(timeUnitMap12);
    org.junit.Assert.assertNotNull(date18);
    org.junit.Assert.assertNotNull(date22);
    org.junit.Assert.assertNotNull(date25);
    org.junit.Assert.assertNotNull(timeUnitMap26);
    org.junit.Assert.assertNotNull(date28);
    org.junit.Assert.assertNotNull(date33);
    org.junit.Assert.assertNotNull(date34);
    org.junit.Assert.assertNotNull(date39);
    org.junit.Assert.assertNotNull(date43);
    org.junit.Assert.assertNotNull(date46);
    org.junit.Assert.assertNotNull(timeUnitMap47);
    org.junit.Assert.assertTrue("'" + boolean52 + "' != '" + true + "'", boolean52);
    org.junit.Assert.assertTrue("'" + boolean55 + "' != '" + true + "'", boolean55);
  }

  @Test
  public void test1249() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1249");
    }
    org.ccsds.moims.mo.mal.structures.DoubleList doubleList1 =
        new org.ccsds.moims.mo.mal.structures.DoubleList(
            (short) 10);
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = doubleList1.getAreaNumber();
    boolean boolean4 = doubleList1.contains((-1));
    int int5 = doubleList1.size();
    java.lang.String str6 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(doubleList1);
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + false + "'", !boolean4);
    org.junit.Assert.assertTrue("'" + int5 + "' != '" + 0 + "'", int5 == 0);
    org.junit.Assert.assertTrue("'" + str6 + "' != '" + "UnknownGUIData" + "'",
        str6.equals("UnknownGUIData"));
  }

  @Test
  public void test1250() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1250");
    }
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience8 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    double double9 = gPSSatInViewScience8.getStdDevElevation();
    double double10 = gPSSatInViewScience8.getMinElevation();
    double double11 = gPSSatInViewScience8.getAvgElevation();
    org.junit.Assert.assertTrue("'" + double9 + "' != '" + 11111.0d + "'", double9 == 11111.0d);
    org.junit.Assert.assertTrue("'" + double10 + "' != '" + 48.0d + "'", double10 == 48.0d);
    org.junit.Assert.assertTrue("'" + double11 + "' != '" + 2.81474993487878E14d + "'",
        double11 == 2.81474993487878E14d);
  }

  @Test
  public void test1251() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1251");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    opssat.simulator.util.SimulatorData simulatorData17 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date18 = simulatorData17.getCurrentTime();
    java.util.Date date19 = simulatorData17.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData20 = new opssat.simulator.util.SimulatorData(
        17, date19);
    opssat.simulator.util.SimulatorData simulatorData23 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date24 = simulatorData23.getCurrentTime();
    int int25 = opssat.simulator.util.DateExtraction.getDayFromDate(date24);
    opssat.simulator.util.SimulatorData simulatorData27 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date28 = simulatorData27.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData30 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date31 = simulatorData30.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap32 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date28, date31);
    opssat.simulator.util.SimulatorHeader simulatorHeader33 =
        new opssat.simulator.util.SimulatorHeader(
            false, date24, date31);
    opssat.simulator.util.SimulatorHeader simulatorHeader34 =
        new opssat.simulator.util.SimulatorHeader(
            false, date19, date24);
    simulatorHeader12.setEndDate(date19);
    simulatorHeader12.setUseCelestia(true);
    java.lang.String str38 = simulatorHeader12.DATE_FORMAT;
    simulatorHeader12.setAutoStartTime(true);
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertNotNull(date18);
    org.junit.Assert.assertNotNull(date19);
    org.junit.Assert.assertNotNull(date24);
    org.junit.Assert.assertNotNull(date28);
    org.junit.Assert.assertNotNull(date31);
    org.junit.Assert.assertNotNull(timeUnitMap32);
    org.junit.Assert.assertTrue("'" + str38 + "' != '" + "yyyy:MM:dd HH:mm:ss z" + "'",
        str38.equals("yyyy:MM:dd HH:mm:ss z"));
  }

  @Test
  public void test1252() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1252");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    boolean boolean14 = simulatorHeader12.isAutoStartSystem();
    int int15 = simulatorHeader12.getDayStartDate();
    boolean boolean16 = simulatorHeader12.isUseCelestia();
    opssat.simulator.util.SimulatorData simulatorData19 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date20 = simulatorData19.getCurrentTime();
    int int21 = opssat.simulator.util.DateExtraction.getDayFromDate(date20);
    opssat.simulator.util.SimulatorData simulatorData23 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date24 = simulatorData23.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData26 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date27 = simulatorData26.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap28 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date24, date27);
    opssat.simulator.util.SimulatorHeader simulatorHeader29 =
        new opssat.simulator.util.SimulatorHeader(
            false, date20, date27);
    simulatorHeader12.setStartDate(date27);
    simulatorHeader12.setKeplerElements("-0100.0000000");
    boolean boolean34 = simulatorHeader12.validateTimeFactor(23);
    boolean boolean35 = simulatorHeader12.isUseOrekitPropagator();
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
    org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
    org.junit.Assert.assertNotNull(date20);
    org.junit.Assert.assertNotNull(date24);
    org.junit.Assert.assertNotNull(date27);
    org.junit.Assert.assertNotNull(timeUnitMap28);
    org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + true + "'", boolean34);
    org.junit.Assert.assertTrue("'" + boolean35 + "' != '" + false + "'", !boolean35);
  }

  @Test
  public void test1253() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1253");
    }
    opssat.simulator.util.SimulatorData simulatorData3 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date4 = simulatorData3.getCurrentTime();
    java.util.Date date5 = simulatorData3.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(17,
        date5);
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    int int11 = opssat.simulator.util.DateExtraction.getDayFromDate(date10);
    opssat.simulator.util.SimulatorData simulatorData13 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date14 = simulatorData13.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData16 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date17 = simulatorData16.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap18 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date14, date17);
    opssat.simulator.util.SimulatorHeader simulatorHeader19 =
        new opssat.simulator.util.SimulatorHeader(
            false, date10, date17);
    opssat.simulator.util.SimulatorHeader simulatorHeader20 =
        new opssat.simulator.util.SimulatorHeader(
            false, date5, date10);
    boolean boolean21 = simulatorHeader20.isAutoStartTime();
    simulatorHeader20.setUseCelestia(false);
    simulatorHeader20.setCelestiaPort((short) 0);
    simulatorHeader20.setUseCelestia(true);
    int int28 = simulatorHeader20.getMinuteStartDate();
    opssat.simulator.util.SimulatorData simulatorData31 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date32 = simulatorData31.getCurrentTime();
    int int33 = opssat.simulator.util.DateExtraction.getDayFromDate(date32);
    opssat.simulator.util.SimulatorData simulatorData35 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date36 = simulatorData35.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData38 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date39 = simulatorData38.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap40 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date36, date39);
    opssat.simulator.util.SimulatorHeader simulatorHeader41 =
        new opssat.simulator.util.SimulatorHeader(
            false, date32, date39);
    java.util.Date date42 = simulatorHeader41.getEndDate();
    simulatorHeader41.setOrekitTLE1("");
    java.util.Date date46 = simulatorHeader41.parseStringIntoDate("2019/05/23-15:09:35");
    java.lang.String str47 = simulatorHeader41.toFileString();
    java.util.Date date48 = simulatorHeader41.getEndDate();
    simulatorHeader20.setStartDate(date48);
    int int50 = simulatorHeader20.getDayStartDate();
    java.lang.String str51 = simulatorHeader20.getOrekitPropagator();
    org.junit.Assert.assertNotNull(date4);
    org.junit.Assert.assertNotNull(date5);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(date14);
    org.junit.Assert.assertNotNull(date17);
    org.junit.Assert.assertNotNull(timeUnitMap18);
    org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
    org.junit.Assert.assertNotNull(date32);
    org.junit.Assert.assertNotNull(date36);
    org.junit.Assert.assertNotNull(date39);
    org.junit.Assert.assertNotNull(timeUnitMap40);
    org.junit.Assert.assertNotNull(date42);
    org.junit.Assert.assertNull(date46);
    org.junit.Assert.assertNotNull(date48);
    org.junit.Assert.assertNull(str51);
  }

  @Test
  public void test1254() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1254");
    }
    org.ccsds.moims.mo.mal.structures.LongList longList0 =
        new org.ccsds.moims.mo.mal.structures.LongList();
    java.util.ListIterator<java.lang.Long> longItor1 = longList0.listIterator();
    java.util.Spliterator<java.lang.Long> longSpliterator2 = longList0.spliterator();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet3 = longList0.getAreaVersion();
    opssat.simulator.util.SimulatorData simulatorData7 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date8 = simulatorData7.getCurrentTime();
    java.util.Date date9 = simulatorData7.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData10 = new opssat.simulator.util.SimulatorData(
        17, date9);
    opssat.simulator.util.SimulatorData simulatorData13 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date14 = simulatorData13.getCurrentTime();
    int int15 = opssat.simulator.util.DateExtraction.getDayFromDate(date14);
    opssat.simulator.util.SimulatorData simulatorData17 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date18 = simulatorData17.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData20 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date21 = simulatorData20.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap22 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date18, date21);
    opssat.simulator.util.SimulatorHeader simulatorHeader23 =
        new opssat.simulator.util.SimulatorHeader(
            false, date14, date21);
    opssat.simulator.util.SimulatorHeader simulatorHeader24 =
        new opssat.simulator.util.SimulatorHeader(
            false, date9, date14);
    boolean boolean25 = simulatorHeader24.checkStartBeforeEnd();
    boolean boolean26 = uOctet3.equals(simulatorHeader24);
    int int27 = simulatorHeader24.getTimeFactor();
    java.lang.String str28 = simulatorHeader24.toString();
    org.junit.Assert.assertNotNull(longItor1);
    org.junit.Assert.assertNotNull(longSpliterator2);
    org.junit.Assert.assertNotNull(uOctet3);
    org.junit.Assert.assertNotNull(date8);
    org.junit.Assert.assertNotNull(date9);
    org.junit.Assert.assertNotNull(date14);
    org.junit.Assert.assertNotNull(date18);
    org.junit.Assert.assertNotNull(date21);
    org.junit.Assert.assertNotNull(timeUnitMap22);
    org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + true + "'", boolean25);
    org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + false + "'", !boolean26);
    org.junit.Assert.assertTrue("'" + int27 + "' != '" + 1 + "'", int27 == 1);
  }

  @Test
  public void test1255() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1255");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    boolean boolean14 = simulatorHeader12.isAutoStartSystem();
    int int15 = simulatorHeader12.getDayStartDate();
    boolean boolean16 = simulatorHeader12.isUseCelestia();
    opssat.simulator.util.SimulatorData simulatorData19 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date20 = simulatorData19.getCurrentTime();
    int int21 = opssat.simulator.util.DateExtraction.getDayFromDate(date20);
    opssat.simulator.util.SimulatorData simulatorData23 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date24 = simulatorData23.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData26 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date27 = simulatorData26.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap28 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date24, date27);
    opssat.simulator.util.SimulatorHeader simulatorHeader29 =
        new opssat.simulator.util.SimulatorHeader(
            false, date20, date27);
    simulatorHeader12.setStartDate(date27);
    simulatorHeader12.setKeplerElements("-0100.0000000");
    java.lang.String str33 = simulatorHeader12.toString();
    java.lang.String str34 = simulatorHeader12.toFileString();
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
    org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
    org.junit.Assert.assertNotNull(date20);
    org.junit.Assert.assertNotNull(date24);
    org.junit.Assert.assertNotNull(date27);
    org.junit.Assert.assertNotNull(timeUnitMap28);
  }

  @Test
  public void test1256() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1256");
    }
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience8 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            3, 0.0f, 281474993487878L, (-2), 38, 36, 0.0d, (-1));
    double double9 = gPSSatInViewScience8.getMaxDistance();
    org.junit.Assert.assertTrue("'" + double9 + "' != '" + 0.0d + "'", double9 == 0.0d);
  }

  @Test
  public void test1257() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1257");
    }
    org.ccsds.moims.mo.mal.structures.StringList stringList0 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    org.ccsds.moims.mo.mal.structures.ShortList shortList2 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            ' ');
    java.util.ListIterator<java.lang.Short> shortItor3 = shortList2.listIterator();
    boolean boolean4 = stringList0.equals(shortList2);
    java.util.stream.BaseStream[] baseStreamArray6 = new java.util.stream.BaseStream[0];
    @SuppressWarnings("unchecked")
    java.util.stream.BaseStream<java.lang.Float, java.util.stream.Stream<java.lang.Float>>[] floatBaseStreamArray7 =
        baseStreamArray6;
    java.util.stream.BaseStream<java.lang.Float, java.util.stream.Stream<java.lang.Float>>[] floatBaseStreamArray8 =
        stringList0
            .toArray(
                (java.util.stream.BaseStream<java.lang.Float, java.util.stream.Stream<java.lang.Float>>[]) baseStreamArray6);
    java.util.stream.Stream<java.lang.String> strStream9 = stringList0.stream();
    java.lang.Object obj10 = stringList0.clone();
    opssat.simulator.gui.GuiApp guiApp13 = null;
    opssat.simulator.tcp.SocketClient socketClient14 = new opssat.simulator.tcp.SocketClient(
        "{counter=8, methodsExecuted=0, currentTime=Thu May 23 15:09:32 UTC 2019}", 0, guiApp13);
    socketClient14.setTargetConnection("030936.762", 4);
    socketClient14.setTargetConnection("", 40);
    socketClient14.setTargetConnection("2019:05:23 15:10:06 UTC", 100);
    opssat.simulator.gui.GuiApp guiApp26 = null;
    opssat.simulator.tcp.SocketClient socketClient27 = new opssat.simulator.tcp.SocketClient(
        "{counter=8, methodsExecuted=0, currentTime=Thu May 23 15:09:32 UTC 2019}", 0, guiApp26);
    socketClient27.setTargetConnection("030936.762", 4);
    socketClient27.setTargetConnection("", 40);
    socketClient27.setTargetConnection("2019:05:23 15:10:06 UTC", 100);
    opssat.simulator.gui.GuiApp guiApp39 = null;
    opssat.simulator.tcp.SocketClient socketClient40 = new opssat.simulator.tcp.SocketClient(
        "{counter=8, methodsExecuted=0, currentTime=Thu May 23 15:09:32 UTC 2019}", 0, guiApp39);
    opssat.simulator.gui.GuiApp guiApp43 = null;
    opssat.simulator.tcp.SocketClient socketClient44 = new opssat.simulator.tcp.SocketClient(
        "{counter=8, methodsExecuted=0, currentTime=Thu May 23 15:09:32 UTC 2019}", 0, guiApp43);
    socketClient44.setTargetConnection("030936.762", 4);
    socketClient44.setTargetConnection("", 40);
    socketClient44.setTargetConnection("2019:05:23 15:10:06 UTC", 100);
    opssat.simulator.gui.GuiApp guiApp56 = null;
    opssat.simulator.tcp.SocketClient socketClient57 = new opssat.simulator.tcp.SocketClient(
        "{counter=8, methodsExecuted=0, currentTime=Thu May 23 15:09:32 UTC 2019}", 0, guiApp56);
    socketClient57.setTargetConnection("030936.762", 4);
    opssat.simulator.tcp.SocketClient[] socketClientArray61 =
        new opssat.simulator.tcp.SocketClient[]{
          socketClient14, socketClient27, socketClient40, socketClient44, socketClient57};
    opssat.simulator.tcp.SocketClient[] socketClientArray62 = stringList0
        .toArray(socketClientArray61);
    org.junit.Assert.assertNotNull(shortItor3);
    org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
    org.junit.Assert.assertNotNull(baseStreamArray6);
    org.junit.Assert.assertNotNull(floatBaseStreamArray7);
    org.junit.Assert.assertNotNull(floatBaseStreamArray8);
    org.junit.Assert.assertNotNull(strStream9);
    org.junit.Assert.assertNotNull(obj10);
    org.junit.Assert.assertNotNull(socketClientArray61);
    org.junit.Assert.assertNotNull(socketClientArray62);
  }

  @Test
  public void test1258() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1258");
    }
    boolean boolean1 = opssat.simulator.threading.SimulatorNode
        .isInteger("2019:05:23 15:09:43 UTC");
    org.junit.Assert.assertTrue("'" + boolean1 + "' != '" + false + "'", !boolean1);
  }

  @Test
  public void test1259() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1259");
    }
    java.util.logging.Logger logger2 = null;
    opssat.simulator.util.EndlessSingleStreamOperatingBuffer endlessSingleStreamOperatingBuffer3 =
        new opssat.simulator.util.EndlessSingleStreamOperatingBuffer(
            logger2);
    byte[] byteArray5 = endlessSingleStreamOperatingBuffer3.getDataAsByteArray('4');
    org.ccsds.moims.mo.mal.structures.Union union7 = new org.ccsds.moims.mo.mal.structures.Union(
        "$DEFAULT");
    endlessSingleStreamOperatingBuffer3.setDataBuffer("$DEFAULT");
    byte[] byteArray10 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS.float2ByteArray(20);
    endlessSingleStreamOperatingBuffer3.setDataFromByteArray(byteArray10);
    try {
      opssat.simulator.peripherals.PFineADCS.FWRefFineADCS.putLongInByteArray(281475010265084L, 39,
          byteArray10);
      org.junit.Assert
          .fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException; message: 39");
    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
    }
    org.junit.Assert.assertNotNull(byteArray5);
    org.junit.Assert.assertNotNull(byteArray10);
  }

  @Test
  public void test1260() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1260");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            100L, 281474993487886L, (byte) 0);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState7 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double8 = simulatorSpacecraftState7.getLatitude();
    double double9 = simulatorSpacecraftState7.getLongitude();
    java.lang.String str10 = simulatorSpacecraftState7.getModeOperation();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState14 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState14.setLongitude(4);
    double[] doubleArray18 = new double[]{(-1.0f)};
    simulatorSpacecraftState14.setMagField(doubleArray18);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState23 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double24 = simulatorSpacecraftState23.getLatitude();
    java.lang.String str25 = simulatorSpacecraftState23.getMagField();
    java.lang.String str26 = simulatorSpacecraftState23.toString();
    double[] doubleArray27 = simulatorSpacecraftState23.getSunVector();
    simulatorSpacecraftState14.setMagnetometer(doubleArray27);
    simulatorSpacecraftState7.setMagField(doubleArray27);
    simulatorSpacecraftState7.setModeOperation("0");
    simulatorSpacecraftState7.setModeOperation("030936.762");
    simulatorSpacecraftState7.setAltitude(60);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState39 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double40 = simulatorSpacecraftState39.getLatitude();
    java.lang.String str41 = simulatorSpacecraftState39.getMagField();
    float[] floatArray42 = simulatorSpacecraftState39.getR();
    simulatorSpacecraftState7.setQ(floatArray42);
    simulatorSpacecraftState3.setRv(floatArray42);
    float[] floatArray45 = simulatorSpacecraftState3.getQ();
    org.junit.Assert.assertTrue("'" + double8 + "' != '" + 340.0d + "'", double8 == 340.0d);
    org.junit.Assert.assertTrue("'" + double9 + "' != '" + (-1.0d) + "'", double9 == (-1.0d));
    org.junit.Assert.assertNull(str10);
    org.junit.Assert.assertNotNull(doubleArray18);
    org.junit.Assert.assertTrue("'" + double24 + "' != '" + 340.0d + "'", double24 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str25 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str25.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str26 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str26.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray27);
    org.junit.Assert.assertTrue("'" + double40 + "' != '" + 340.0d + "'", double40 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str41 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str41.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray42);
    org.junit.Assert.assertNotNull(floatArray45);
  }

  @Test
  public void test1261() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1261");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getServiceNumber();
    org.ccsds.moims.mo.mal.structures.OctetList octetList3 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int4 = octetList3.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort5 = octetList3.getAreaNumber();
    java.lang.Object[] objArray6 = octetList3.toArray();
    org.ccsds.moims.mo.mal.structures.OctetList octetList7 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int8 = octetList7.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort9 = octetList7.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor10 = octetList7.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor12 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList7, "hi!");
    org.ccsds.moims.mo.mal.structures.UShort uShort13 = octetList7.getAreaNumber();
    java.lang.Boolean[] booleanArray16 = new java.lang.Boolean[]{true, true};
    java.util.ArrayList<java.lang.Boolean> booleanList17 =
        new java.util.ArrayList<java.lang.Boolean>();
    boolean boolean18 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Boolean>) booleanList17, booleanArray16);
    int int20 = booleanList17.indexOf(10);
    java.util.Iterator<java.lang.Boolean> booleanItor21 = booleanList17.iterator();
    java.util.Spliterator<java.lang.Boolean> booleanSpliterator22 = booleanList17.spliterator();
    org.ccsds.moims.mo.mal.structures.OctetList octetList23 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int24 = octetList23.getTypeShortForm();
    java.lang.Object obj25 = octetList23.clone();
    octetList23.trimToSize();
    boolean boolean27 = booleanList17.contains(octetList23);
    org.ccsds.moims.mo.mal.structures.OctetList octetList28 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int29 = octetList28.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort30 = octetList28.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.OctetList octetList31 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int32 = octetList31.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort33 = octetList31.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet34 = octetList31.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.OctetList octetList35 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int36 = octetList35.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort37 = octetList35.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.OctetList[] octetListArray38 =
        new org.ccsds.moims.mo.mal.structures.OctetList[]{
          octetList3, octetList7, octetList23, octetList28, octetList31, octetList35};
    org.ccsds.moims.mo.mal.structures.OctetList[] octetListArray39 = octetList0
        .toArray(octetListArray38);
    java.lang.Long long40 = octetList0.getShortForm();
    java.util.ListIterator<java.lang.Byte> byteItor41 = octetList0.listIterator();
    int int42 = octetList0.size();
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertTrue("'" + int4 + "' != '" + (-7) + "'", int4.equals((-7)));
    org.junit.Assert.assertNotNull(uShort5);
    org.junit.Assert.assertNotNull(objArray6);
    org.junit.Assert.assertTrue("'" + int8 + "' != '" + (-7) + "'", int8.equals((-7)));
    org.junit.Assert.assertNotNull(uShort9);
    org.junit.Assert.assertNotNull(byteItor10);
    org.junit.Assert.assertNotNull(uShort13);
    org.junit.Assert.assertNotNull(booleanArray16);
    org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + true + "'", boolean18);
    org.junit.Assert.assertTrue("'" + int20 + "' != '" + (-1) + "'", int20 == (-1));
    org.junit.Assert.assertNotNull(booleanItor21);
    org.junit.Assert.assertNotNull(booleanSpliterator22);
    org.junit.Assert.assertTrue("'" + int24 + "' != '" + (-7) + "'", int24.equals((-7)));
    org.junit.Assert.assertNotNull(obj25);
    org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + false + "'", !boolean27);
    org.junit.Assert.assertTrue("'" + int29 + "' != '" + (-7) + "'", int29.equals((-7)));
    org.junit.Assert.assertNotNull(uShort30);
    org.junit.Assert.assertTrue("'" + int32 + "' != '" + (-7) + "'", int32.equals((-7)));
    org.junit.Assert.assertNotNull(uShort33);
    org.junit.Assert.assertNotNull(uOctet34);
    org.junit.Assert.assertTrue("'" + int36 + "' != '" + (-7) + "'", int36.equals((-7)));
    org.junit.Assert.assertNotNull(uShort37);
    org.junit.Assert.assertNotNull(octetListArray38);
    org.junit.Assert.assertNotNull(octetListArray39);
    org.junit.Assert.assertTrue("'" + long40 + "' != '" + 281475010265081L + "'",
        long40.equals(281475010265081L));
    org.junit.Assert.assertNotNull(byteItor41);
    org.junit.Assert.assertTrue("'" + int42 + "' != '" + 0 + "'", int42 == 0);
  }

  @Test
  public void test1262() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1262");
    }
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap1 =
        opssat.simulator.util.SimulatorData
            .computeTimeUnit(0);
    org.junit.Assert.assertNotNull(timeUnitMap1);
  }

  @Test
  public void test1263() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1263");
    }
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap1 =
        opssat.simulator.util.SimulatorData
            .computeTimeUnit((short) 255);
    org.junit.Assert.assertNotNull(timeUnitMap1);
  }

  @Test
  public void test1264() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1264");
    }
    opssat.simulator.threading.SimulatorNode simulatorNode0 = null;
    opssat.simulator.peripherals.POpticalReceiver pOpticalReceiver2 =
        new opssat.simulator.peripherals.POpticalReceiver(
            simulatorNode0, "031008.320");
    try {
      pOpticalReceiver2.simSetSuccessRate(32);
      org.junit.Assert
          .fail("Expected exception of type java.lang.NullPointerException; message: null");
    } catch (java.lang.NullPointerException e) {
    }
  }

  @Test
  public void test1265() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1265");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor3 = octetList0.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor5 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList0, "hi!");
    java.lang.Object obj6 = argumentDescriptor5.getType();
    java.lang.String str7 = argumentDescriptor5.toString();
    argumentDescriptor5.restoreArgument();
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(byteItor3);
    org.junit.Assert.assertNotNull(obj6);
    org.junit.Assert.assertTrue("'" + str7 + "' != '" + "" + "'", str7.equals(""));
  }

  @Test
  public void test1266() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1266");
    }
    java.lang.Boolean[] booleanArray2 = new java.lang.Boolean[]{true, true};
    java.util.ArrayList<java.lang.Boolean> booleanList3 =
        new java.util.ArrayList<java.lang.Boolean>();
    boolean boolean4 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Boolean>) booleanList3, booleanArray2);
    int int6 = booleanList3.indexOf(10);
    booleanList3.clear();
    opssat.simulator.util.DateExtraction dateExtraction8 =
        new opssat.simulator.util.DateExtraction();
    boolean boolean9 = booleanList3.equals(dateExtraction8);
    boolean boolean10 = booleanList3.isEmpty();
    java.util.stream.Stream<java.lang.Boolean> booleanStream11 = booleanList3.parallelStream();
    booleanList3.add(0, false);
    org.junit.Assert.assertNotNull(booleanArray2);
    org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
    org.junit.Assert.assertTrue("'" + int6 + "' != '" + (-1) + "'", int6 == (-1));
    org.junit.Assert.assertTrue("'" + boolean9 + "' != '" + false + "'", !boolean9);
    org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
    org.junit.Assert.assertNotNull(booleanStream11);
  }

  @Test
  public void test1267() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1267");
    }
    org.ccsds.moims.mo.mal.structures.StringList stringList0 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    org.ccsds.moims.mo.mal.structures.ShortList shortList2 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            ' ');
    java.util.ListIterator<java.lang.Short> shortItor3 = shortList2.listIterator();
    boolean boolean4 = stringList0.equals(shortList2);
    java.util.Iterator<java.lang.Short> shortItor5 = shortList2.iterator();
    java.lang.Long long6 = shortList2.getShortForm();
    java.lang.Long long7 = shortList2.getShortForm();
    opssat.simulator.orekit.GPSSatInViewScience gPSSatInViewScience16 =
        new opssat.simulator.orekit.GPSSatInViewScience(
            9, 0.0f, 48, 56, (short) 1, 281474993487878L, 58, 11111);
    double double17 = gPSSatInViewScience16.getStdDevElevation();
    double double18 = gPSSatInViewScience16.getMaxDistance();
    double double19 = gPSSatInViewScience16.getAvgElevation();
    boolean boolean20 = shortList2.contains(double19);
    java.lang.Boolean[] booleanArray22 = new java.lang.Boolean[]{true};
    java.util.ArrayList<java.lang.Boolean> booleanList23 =
        new java.util.ArrayList<java.lang.Boolean>();
    boolean boolean24 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Boolean>) booleanList23, booleanArray22);
    java.util.Spliterator<java.lang.Boolean> booleanSpliterator25 = booleanList23.spliterator();
    java.lang.Byte[] byteArray30 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList31 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean32 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList31, byteArray30);
    java.lang.Integer[] intArray35 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList36 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean37 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList36, intArray35);
    boolean boolean38 = byteList31.retainAll(intList36);
    java.lang.Integer[] intArray46 = new java.lang.Integer[]{13, 10, 100, 100, 11111, 13, 11111};
    java.util.ArrayList<java.lang.Integer> intList47 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean48 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList47, intArray46);
    java.lang.Byte[] byteArray53 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList54 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean55 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList54, byteArray53);
    java.lang.Integer[] intArray58 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList59 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean60 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList59, intArray58);
    boolean boolean61 = byteList54.retainAll(intList59);
    java.lang.Integer[] intArray64 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList65 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean66 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList65, intArray64);
    int int68 = intList65.lastIndexOf((byte) 10);
    boolean boolean69 = intList59.removeAll(intList65);
    boolean boolean70 = intList47.retainAll(intList65);
    java.lang.Float[] floatArray74 = new java.lang.Float[]{1.0f, 1.0f, (-1.0f)};
    java.util.ArrayList<java.lang.Float> floatList75 = new java.util.ArrayList<java.lang.Float>();
    boolean boolean76 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Float>) floatList75, floatArray74);
    boolean boolean78 = floatList75.add((-1.0f));
    floatList75.trimToSize();
    java.lang.Integer[] intArray84 = new java.lang.Integer[]{100, 1, (-1), 10};
    java.util.ArrayList<java.lang.Integer> intList85 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean86 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList85, intArray84);
    int int88 = intList85.lastIndexOf((byte) 1);
    boolean boolean89 = floatList75.containsAll(intList85);
    boolean boolean90 = intList47.addAll(intList85);
    boolean boolean91 = intList36.retainAll(intList47);
    boolean boolean92 = booleanList23.equals(intList47);
    intList47.clear();
    boolean boolean94 = shortList2.removeAll(intList47);
    org.junit.Assert.assertNotNull(shortItor3);
    org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
    org.junit.Assert.assertNotNull(shortItor5);
    org.junit.Assert.assertTrue("'" + long6 + "' != '" + 281475010265079L + "'",
        long6.equals(281475010265079L));
    org.junit.Assert.assertTrue("'" + long7 + "' != '" + 281475010265079L + "'",
        long7.equals(281475010265079L));
    org.junit.Assert.assertTrue("'" + double17 + "' != '" + 11111.0d + "'", double17 == 11111.0d);
    org.junit.Assert.assertTrue("'" + double18 + "' != '" + 0.0d + "'", double18 == 0.0d);
    org.junit.Assert.assertTrue("'" + double19 + "' != '" + 2.81474993487878E14d + "'",
        double19 == 2.81474993487878E14d);
    org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + false + "'", !boolean20);
    org.junit.Assert.assertNotNull(booleanArray22);
    org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + true + "'", boolean24);
    org.junit.Assert.assertNotNull(booleanSpliterator25);
    org.junit.Assert.assertNotNull(byteArray30);
    org.junit.Assert.assertTrue("'" + boolean32 + "' != '" + true + "'", boolean32);
    org.junit.Assert.assertNotNull(intArray35);
    org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + true + "'", boolean37);
    org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + true + "'", boolean38);
    org.junit.Assert.assertNotNull(intArray46);
    org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + true + "'", boolean48);
    org.junit.Assert.assertNotNull(byteArray53);
    org.junit.Assert.assertTrue("'" + boolean55 + "' != '" + true + "'", boolean55);
    org.junit.Assert.assertNotNull(intArray58);
    org.junit.Assert.assertTrue("'" + boolean60 + "' != '" + true + "'", boolean60);
    org.junit.Assert.assertTrue("'" + boolean61 + "' != '" + true + "'", boolean61);
    org.junit.Assert.assertNotNull(intArray64);
    org.junit.Assert.assertTrue("'" + boolean66 + "' != '" + true + "'", boolean66);
    org.junit.Assert.assertTrue("'" + int68 + "' != '" + (-1) + "'", int68 == (-1));
    org.junit.Assert.assertTrue("'" + boolean69 + "' != '" + true + "'", boolean69);
    org.junit.Assert.assertTrue("'" + boolean70 + "' != '" + true + "'", boolean70);
    org.junit.Assert.assertNotNull(floatArray74);
    org.junit.Assert.assertTrue("'" + boolean76 + "' != '" + true + "'", boolean76);
    org.junit.Assert.assertTrue("'" + boolean78 + "' != '" + true + "'", boolean78);
    org.junit.Assert.assertNotNull(intArray84);
    org.junit.Assert.assertTrue("'" + boolean86 + "' != '" + true + "'", boolean86);
    org.junit.Assert.assertTrue("'" + int88 + "' != '" + (-1) + "'", int88 == (-1));
    org.junit.Assert.assertTrue("'" + boolean89 + "' != '" + false + "'", !boolean89);
    org.junit.Assert.assertTrue("'" + boolean90 + "' != '" + true + "'", boolean90);
    org.junit.Assert.assertTrue("'" + boolean91 + "' != '" + true + "'", boolean91);
    org.junit.Assert.assertTrue("'" + boolean92 + "' != '" + false + "'", !boolean92);
    org.junit.Assert.assertTrue("'" + boolean94 + "' != '" + false + "'", !boolean94);
  }

  @Test
  public void test1268() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1268");
    }
    opssat.simulator.util.SimulatorData simulatorData3 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date4 = simulatorData3.getCurrentTime();
    int int5 = opssat.simulator.util.DateExtraction.getDayFromDate(date4);
    opssat.simulator.util.SimulatorData simulatorData7 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date8 = simulatorData7.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData10 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date11 = simulatorData10.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap12 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date8, date11);
    opssat.simulator.util.SimulatorHeader simulatorHeader13 =
        new opssat.simulator.util.SimulatorHeader(
            false, date4, date11);
    opssat.simulator.util.SimulatorData simulatorData14 = new opssat.simulator.util.SimulatorData(
        (short) 0, date4);
    opssat.simulator.util.SimulatorData simulatorData17 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date18 = simulatorData17.getCurrentTime();
    int int19 = opssat.simulator.util.DateExtraction.getDayFromDate(date18);
    opssat.simulator.util.SimulatorData simulatorData21 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date22 = simulatorData21.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData24 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date25 = simulatorData24.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap26 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date22, date25);
    opssat.simulator.util.SimulatorHeader simulatorHeader27 =
        new opssat.simulator.util.SimulatorHeader(
            false, date18, date25);
    java.util.Date date28 = simulatorHeader27.getEndDate();
    opssat.simulator.util.SimulatorData simulatorData32 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date33 = simulatorData32.getCurrentTime();
    java.util.Date date34 = simulatorData32.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData35 = new opssat.simulator.util.SimulatorData(
        17, date34);
    opssat.simulator.util.SimulatorData simulatorData38 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date39 = simulatorData38.getCurrentTime();
    int int40 = opssat.simulator.util.DateExtraction.getDayFromDate(date39);
    opssat.simulator.util.SimulatorData simulatorData42 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date43 = simulatorData42.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData45 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date46 = simulatorData45.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap47 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date43, date46);
    opssat.simulator.util.SimulatorHeader simulatorHeader48 =
        new opssat.simulator.util.SimulatorHeader(
            false, date39, date46);
    opssat.simulator.util.SimulatorHeader simulatorHeader49 =
        new opssat.simulator.util.SimulatorHeader(
            false, date34, date39);
    simulatorHeader27.setEndDate(date34);
    simulatorData14.initFromHeader(simulatorHeader27);
    java.lang.String str52 = simulatorHeader27.getOrekitPropagator();
    simulatorHeader27
        .setOrekitPropagator("opssat.simulator.util.wav.WavFileException: 00000:00:00:00:008");
    simulatorHeader27.setTimeFactor(18);
    org.junit.Assert.assertNotNull(date4);
    org.junit.Assert.assertNotNull(date8);
    org.junit.Assert.assertNotNull(date11);
    org.junit.Assert.assertNotNull(timeUnitMap12);
    org.junit.Assert.assertNotNull(date18);
    org.junit.Assert.assertNotNull(date22);
    org.junit.Assert.assertNotNull(date25);
    org.junit.Assert.assertNotNull(timeUnitMap26);
    org.junit.Assert.assertNotNull(date28);
    org.junit.Assert.assertNotNull(date33);
    org.junit.Assert.assertNotNull(date34);
    org.junit.Assert.assertNotNull(date39);
    org.junit.Assert.assertNotNull(date43);
    org.junit.Assert.assertNotNull(date46);
    org.junit.Assert.assertNotNull(timeUnitMap47);
    org.junit.Assert.assertNull(str52);
  }

  @Test
  public void test1269() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1269");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    float[] floatArray11 = new float[]{281474993487887L, 281475010265070L, 281474993487881L};
    simulatorSpacecraftState3.setRv(floatArray11);
    java.lang.String str13 = simulatorSpacecraftState3.getModeOperation();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState17 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray20 = new float[]{28, 8};
    simulatorSpacecraftState17.setQ(floatArray20);
    float[] floatArray22 = simulatorSpacecraftState17.getQ();
    double double23 = simulatorSpacecraftState17.getLongitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState27 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray30 = new float[]{28, 8};
    simulatorSpacecraftState27.setQ(floatArray30);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState35 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double36 = simulatorSpacecraftState35.getLatitude();
    java.lang.String str37 = simulatorSpacecraftState35.getMagField();
    float[] floatArray38 = simulatorSpacecraftState35.getR();
    simulatorSpacecraftState27.setQ(floatArray38);
    java.lang.String str40 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray38);
    simulatorSpacecraftState17.setRv(floatArray38);
    double double42 = simulatorSpacecraftState17.getLatitude();
    float[] floatArray43 = simulatorSpacecraftState17.getRv();
    simulatorSpacecraftState3.setRv(floatArray43);
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertNotNull(floatArray11);
    org.junit.Assert.assertNull(str13);
    org.junit.Assert.assertNotNull(floatArray20);
    org.junit.Assert.assertNotNull(floatArray22);
    org.junit.Assert.assertTrue("'" + double23 + "' != '" + (-1.0d) + "'", double23 == (-1.0d));
    org.junit.Assert.assertNotNull(floatArray30);
    org.junit.Assert.assertTrue("'" + double36 + "' != '" + 340.0d + "'", double36 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str37 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str37.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray38);
    org.junit.Assert.assertTrue("'" + str40 + "' != '" + "UnknownGUIData" + "'",
        str40.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double42 + "' != '" + 340.0d + "'", double42 == 340.0d);
    org.junit.Assert.assertNotNull(floatArray43);
  }

  @Test
  public void test1270() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1270");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    int int8 = simulatorSpacecraftState3.getSatsInView();
    simulatorSpacecraftState3.setAltitude((-5));
    double[] doubleArray15 = new double[]{281474993487879L, 66, 97, (short) 255};
    double[] doubleArray20 = new double[]{281474993487879L, 66, 97, (short) 255};
    double[] doubleArray25 = new double[]{281474993487879L, 66, 97, (short) 255};
    double[] doubleArray30 = new double[]{281474993487879L, 66, 97, (short) 255};
    double[] doubleArray35 = new double[]{281474993487879L, 66, 97, (short) 255};
    double[] doubleArray40 = new double[]{281474993487879L, 66, 97, (short) 255};
    double[][] doubleArray41 = new double[][]{doubleArray15, doubleArray20, doubleArray25,
      doubleArray30, doubleArray35, doubleArray40};
    simulatorSpacecraftState3.setRotation(doubleArray41);
    float[] floatArray43 = simulatorSpacecraftState3.getRv();
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + int8 + "' != '" + 0 + "'", int8 == 0);
    org.junit.Assert.assertNotNull(doubleArray15);
    org.junit.Assert.assertNotNull(doubleArray20);
    org.junit.Assert.assertNotNull(doubleArray25);
    org.junit.Assert.assertNotNull(doubleArray30);
    org.junit.Assert.assertNotNull(doubleArray35);
    org.junit.Assert.assertNotNull(doubleArray40);
    org.junit.Assert.assertNotNull(doubleArray41);
    org.junit.Assert.assertNotNull(floatArray43);
  }

  @Test
  public void test1271() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1271");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState3.setLongitude(4);
    int int6 = simulatorSpacecraftState3.getSatsInView();
    float[] floatArray7 = simulatorSpacecraftState3.getRv();
    float[] floatArray8 = simulatorSpacecraftState3.getRv();
    float[] floatArray9 = simulatorSpacecraftState3.getRv();
    org.junit.Assert.assertTrue("'" + int6 + "' != '" + 0 + "'", int6 == 0);
    org.junit.Assert.assertNotNull(floatArray7);
    org.junit.Assert.assertNotNull(floatArray8);
    org.junit.Assert.assertNotNull(floatArray9);
  }

  @Test
  public void test1272() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1272");
    }
    opssat.simulator.util.MyFormatter myFormatter0 = new opssat.simulator.util.MyFormatter();
    java.util.logging.Handler handler1 = null;
    java.lang.String str2 = myFormatter0.getHead(handler1);
    org.junit.Assert.assertTrue("'" + str2 + "' != '" + "" + "'", str2.equals(""));
  }

  @Test
  public void test1273() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1273");
    }
    org.ccsds.moims.mo.mal.structures.StringList stringList0 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    java.util.Iterator<java.lang.String> strItor1 = stringList0.iterator();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = stringList0.getAreaVersion();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState6 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray9 = new float[]{28, 8};
    simulatorSpacecraftState6.setQ(floatArray9);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState14 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double15 = simulatorSpacecraftState14.getLatitude();
    java.lang.String str16 = simulatorSpacecraftState14.getMagField();
    float[] floatArray17 = simulatorSpacecraftState14.getR();
    simulatorSpacecraftState6.setQ(floatArray17);
    java.lang.String str19 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray17);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState23 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double24 = simulatorSpacecraftState23.getLatitude();
    double double25 = simulatorSpacecraftState23.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState29 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double30 = simulatorSpacecraftState29.getLatitude();
    java.lang.String str31 = simulatorSpacecraftState29.getMagField();
    float[] floatArray32 = simulatorSpacecraftState29.getR();
    simulatorSpacecraftState23.setQ(floatArray32);
    float[] floatArray34 = simulatorSpacecraftState23.getV();
    opssat.simulator.celestia.CelestiaData celestiaData35 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray17, floatArray34);
    float[] floatArray36 = celestiaData35.getQ();
    opssat.simulator.util.SimulatorData simulatorData40 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date41 = simulatorData40.getCurrentTime();
    int int42 = opssat.simulator.util.DateExtraction.getDayFromDate(date41);
    opssat.simulator.util.SimulatorData simulatorData44 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date45 = simulatorData44.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData47 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date48 = simulatorData47.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap49 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date45, date48);
    opssat.simulator.util.SimulatorHeader simulatorHeader50 =
        new opssat.simulator.util.SimulatorHeader(
            false, date41, date48);
    opssat.simulator.util.SimulatorData simulatorData51 = new opssat.simulator.util.SimulatorData(
        (short) 0, date41);
    celestiaData35.setDate(date41);
    int int53 = celestiaData35.getSeconds();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState57 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray60 = new float[]{28, 8};
    simulatorSpacecraftState57.setQ(floatArray60);
    celestiaData35.setQ(floatArray60);
    int int63 = stringList0.lastIndexOf(celestiaData35);
    int int64 = celestiaData35.getYears();
    celestiaData35.setInfo("yyyy/MM/dd-HH:mm:ss");
    float[] floatArray67 = celestiaData35.getRv();
    org.junit.Assert.assertNotNull(strItor1);
    org.junit.Assert.assertNotNull(uOctet2);
    org.junit.Assert.assertNotNull(floatArray9);
    org.junit.Assert.assertTrue("'" + double15 + "' != '" + 340.0d + "'", double15 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str16 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str16.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray17);
    org.junit.Assert.assertTrue("'" + str19 + "' != '" + "UnknownGUIData" + "'",
        str19.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double24 + "' != '" + 340.0d + "'", double24 == 340.0d);
    org.junit.Assert.assertTrue("'" + double25 + "' != '" + 340.0d + "'", double25 == 340.0d);
    org.junit.Assert.assertTrue("'" + double30 + "' != '" + 340.0d + "'", double30 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str31 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str31.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray32);
    org.junit.Assert.assertNotNull(floatArray34);
    org.junit.Assert.assertNotNull(floatArray36);
    org.junit.Assert.assertNotNull(date41);
    org.junit.Assert.assertNotNull(date45);
    org.junit.Assert.assertNotNull(date48);
    org.junit.Assert.assertNotNull(timeUnitMap49);
    org.junit.Assert.assertNotNull(floatArray60);
    org.junit.Assert.assertTrue("'" + int63 + "' != '" + (-1) + "'", int63 == (-1));
    org.junit.Assert.assertNotNull(floatArray67);
  }

  @Test
  public void test1274() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1274");
    }
    opssat.simulator.util.SimulatorSchedulerPiece simulatorSchedulerPiece3 =
        new opssat.simulator.util.SimulatorSchedulerPiece(
            13L, 0, "hi!");
    long long4 = simulatorSchedulerPiece3.getTime();
    int int5 = simulatorSchedulerPiece3.getInternalID();
    java.lang.String str6 = simulatorSchedulerPiece3.getFileString();
    java.lang.String str7 = simulatorSchedulerPiece3.getFileString();
    simulatorSchedulerPiece3.setExecuted(true);
    simulatorSchedulerPiece3.setExecuted(true);
    long long12 = simulatorSchedulerPiece3.getTime();
    org.junit.Assert.assertTrue("'" + long4 + "' != '" + 13L + "'", long4 == 13L);
    org.junit.Assert.assertTrue("'" + int5 + "' != '" + 0 + "'", int5 == 0);
    org.junit.Assert.assertTrue(
        "'" + str6 + "' != '" + "00000:00:00:00:013|0000000000000000013|0|hi!" + "'",
        str6.equals("00000:00:00:00:013|0000000000000000013|0|hi!"));
    org.junit.Assert.assertTrue(
        "'" + str7 + "' != '" + "00000:00:00:00:013|0000000000000000013|0|hi!" + "'",
        str7.equals("00000:00:00:00:013|0000000000000000013|0|hi!"));
    org.junit.Assert.assertTrue("'" + long12 + "' != '" + 13L + "'", long12 == 13L);
  }

  @Test
  public void test1275() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1275");
    }
    opssat.simulator.util.SimulatorData simulatorData1 = new opssat.simulator.util.SimulatorData(
        (-18));
    simulatorData1.setCounter((-1));
    simulatorData1.feedTimeElapsed(21);
    java.lang.String str6 = simulatorData1.getCurrentDay();
    boolean boolean7 = simulatorData1.isTimeRunning();
    boolean boolean8 = simulatorData1.isSimulatorRunning();
    boolean boolean9 = simulatorData1.isTimeRunning();
    org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
    org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + true + "'", boolean8);
    org.junit.Assert.assertTrue("'" + boolean9 + "' != '" + false + "'", !boolean9);
  }

  @Test
  public void test1276() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1276");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessWavStreamOperatingBuffer endlessWavStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessWavStreamOperatingBuffer(
            logger0);
    opssat.simulator.util.SimulatorData simulatorData4 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date5 = simulatorData4.getCurrentTime();
    int int6 = opssat.simulator.util.DateExtraction.getDayFromDate(date5);
    opssat.simulator.util.SimulatorData simulatorData8 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date9 = simulatorData8.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData11 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date12 = simulatorData11.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap13 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date9, date12);
    opssat.simulator.util.SimulatorHeader simulatorHeader14 =
        new opssat.simulator.util.SimulatorHeader(
            false, date5, date12);
    java.util.Date date15 = simulatorHeader14.getEndDate();
    int int16 = simulatorHeader14.getMinuteStartDate();
    simulatorHeader14.setUseOrekitPropagator(true);
    java.lang.String str19 = simulatorHeader14.toFileString();
    java.util.Date date21 = simulatorHeader14.parseStringIntoDate("yyyy:MM:dd HH:mm:ss z");
    endlessWavStreamOperatingBuffer1.setDataBuffer("yyyy:MM:dd HH:mm:ss z");
    boolean boolean24 = endlessWavStreamOperatingBuffer1.preparePath("");
    byte[] byteArray26 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS
        .long2ByteArray(281475010265075L);
    org.ccsds.moims.mo.mal.structures.Blob blob27 = new org.ccsds.moims.mo.mal.structures.Blob(
        byteArray26);
    endlessWavStreamOperatingBuffer1.setDataFromByteArray(byteArray26);
    org.junit.Assert.assertNotNull(date5);
    org.junit.Assert.assertNotNull(date9);
    org.junit.Assert.assertNotNull(date12);
    org.junit.Assert.assertNotNull(timeUnitMap13);
    org.junit.Assert.assertNotNull(date15);
    org.junit.Assert.assertNull(date21);
    org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + false + "'", !boolean24);
    org.junit.Assert.assertNotNull(byteArray26);
  }

  @Test
  public void test1277() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1277");
    }
    org.ccsds.moims.mo.mal.structures.UInteger uInteger1 =
        new org.ccsds.moims.mo.mal.structures.UInteger(
            13);
    org.ccsds.moims.mo.mal.structures.Element element2 = uInteger1.createElement();
    long long3 = uInteger1.getValue();
    long long4 = uInteger1.getValue();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet5 = uInteger1.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.UShort uShort6 = uInteger1.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.OctetList octetList7 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int8 = octetList7.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort9 = octetList7.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor10 = octetList7.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor12 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList7, "hi!");
    java.lang.Integer int13 = octetList7.getTypeShortForm();
    java.lang.Boolean[] booleanArray16 = new java.lang.Boolean[]{true, true};
    java.util.ArrayList<java.lang.Boolean> booleanList17 =
        new java.util.ArrayList<java.lang.Boolean>();
    boolean boolean18 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Boolean>) booleanList17, booleanArray16);
    int int20 = booleanList17.indexOf(10);
    java.util.Iterator<java.lang.Boolean> booleanItor21 = booleanList17.iterator();
    boolean boolean23 = booleanList17.remove(10.0f);
    int int24 = octetList7.indexOf(booleanList17);
    octetList7.trimToSize();
    org.ccsds.moims.mo.mal.structures.Element element26 = octetList7.createElement();
    boolean boolean27 = uInteger1.equals(octetList7);
    org.ccsds.moims.mo.mal.structures.DoubleList doubleList29 =
        new org.ccsds.moims.mo.mal.structures.DoubleList(
            (short) 10);
    org.ccsds.moims.mo.mal.structures.UShort uShort30 = doubleList29.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UShort uShort31 = doubleList29.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UShort uShort32 = doubleList29.getServiceNumber();
    org.ccsds.moims.mo.mal.structures.UShort uShort33 = doubleList29.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet34 = doubleList29.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.Element element35 = doubleList29.createElement();
    org.ccsds.moims.mo.mal.structures.UShort uShort36 = doubleList29.getAreaNumber();
    java.lang.Long long37 = doubleList29.getShortForm();
    org.ccsds.moims.mo.mal.structures.StringList stringList38 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    org.ccsds.moims.mo.mal.structures.ShortList shortList40 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            ' ');
    java.util.ListIterator<java.lang.Short> shortItor41 = shortList40.listIterator();
    boolean boolean42 = stringList38.equals(shortList40);
    org.ccsds.moims.mo.mal.structures.IntegerList integerList44 =
        new org.ccsds.moims.mo.mal.structures.IntegerList(
            48);
    java.lang.Long long45 = integerList44.getShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort46 = integerList44.getAreaNumber();
    int int47 = stringList38.indexOf(integerList44);
    boolean boolean48 = doubleList29.containsAll(integerList44);
    java.util.Iterator<java.lang.Double> doubleItor49 = doubleList29.iterator();
    boolean boolean50 = octetList7.equals(doubleList29);
    org.junit.Assert.assertNotNull(element2);
    org.junit.Assert.assertTrue("'" + long3 + "' != '" + 13L + "'", long3 == 13L);
    org.junit.Assert.assertTrue("'" + long4 + "' != '" + 13L + "'", long4 == 13L);
    org.junit.Assert.assertNotNull(uOctet5);
    org.junit.Assert.assertNotNull(uShort6);
    org.junit.Assert.assertTrue("'" + int8 + "' != '" + (-7) + "'", int8.equals((-7)));
    org.junit.Assert.assertNotNull(uShort9);
    org.junit.Assert.assertNotNull(byteItor10);
    org.junit.Assert.assertTrue("'" + int13 + "' != '" + (-7) + "'", int13.equals((-7)));
    org.junit.Assert.assertNotNull(booleanArray16);
    org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + true + "'", boolean18);
    org.junit.Assert.assertTrue("'" + int20 + "' != '" + (-1) + "'", int20 == (-1));
    org.junit.Assert.assertNotNull(booleanItor21);
    org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + false + "'", !boolean23);
    org.junit.Assert.assertTrue("'" + int24 + "' != '" + (-1) + "'", int24 == (-1));
    org.junit.Assert.assertNotNull(element26);
    org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + false + "'", !boolean27);
    org.junit.Assert.assertNotNull(uShort30);
    org.junit.Assert.assertNotNull(uShort31);
    org.junit.Assert.assertNotNull(uShort32);
    org.junit.Assert.assertNotNull(uShort33);
    org.junit.Assert.assertNotNull(uOctet34);
    org.junit.Assert.assertNotNull(element35);
    org.junit.Assert.assertNotNull(uShort36);
    org.junit.Assert.assertTrue("'" + long37 + "' != '" + 281475010265083L + "'",
        long37.equals(281475010265083L));
    org.junit.Assert.assertNotNull(shortItor41);
    org.junit.Assert.assertTrue("'" + boolean42 + "' != '" + true + "'", boolean42);
    org.junit.Assert.assertTrue("'" + long45 + "' != '" + 281475010265077L + "'",
        long45.equals(281475010265077L));
    org.junit.Assert.assertNotNull(uShort46);
    org.junit.Assert.assertTrue("'" + int47 + "' != '" + (-1) + "'", int47 == (-1));
    org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + true + "'", boolean48);
    org.junit.Assert.assertNotNull(doubleItor49);
    org.junit.Assert.assertTrue("'" + boolean50 + "' != '" + true + "'", boolean50);
  }

  @Test
  public void test1278() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1278");
    }
    opssat.simulator.util.wav.WavFileException wavFileException2 =
        new opssat.simulator.util.wav.WavFileException(
            "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}");
    opssat.simulator.util.wav.WavFileException wavFileException3 =
        new opssat.simulator.util.wav.WavFileException();
    org.ccsds.moims.mo.mal.structures.OctetList octetList4 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int5 = octetList4.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort6 = octetList4.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet7 = octetList4.getAreaVersion();
    opssat.simulator.util.wav.WavFileException wavFileException9 =
        new opssat.simulator.util.wav.WavFileException(
            "UnknownGUIData");
    java.lang.Throwable[] throwableArray10 = wavFileException9.getSuppressed();
    boolean boolean11 = octetList4.equals(wavFileException9);
    wavFileException3.addSuppressed(wavFileException9);
    wavFileException2.addSuppressed(wavFileException9);
    opssat.simulator.util.wav.WavFileException wavFileException14 =
        new opssat.simulator.util.wav.WavFileException(
            wavFileException9);
    opssat.simulator.util.wav.WavFileException wavFileException16 =
        new opssat.simulator.util.wav.WavFileException(
            "00000:00:00:00:008");
    wavFileException9.addSuppressed(wavFileException16);
    opssat.simulator.util.wav.WavFileException wavFileException19 =
        new opssat.simulator.util.wav.WavFileException();
    org.ccsds.moims.mo.mal.structures.OctetList octetList20 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int21 = octetList20.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort22 = octetList20.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet23 = octetList20.getAreaVersion();
    opssat.simulator.util.wav.WavFileException wavFileException25 =
        new opssat.simulator.util.wav.WavFileException(
            "UnknownGUIData");
    java.lang.Throwable[] throwableArray26 = wavFileException25.getSuppressed();
    boolean boolean27 = octetList20.equals(wavFileException25);
    wavFileException19.addSuppressed(wavFileException25);
    opssat.simulator.util.wav.WavFileException wavFileException29 =
        new opssat.simulator.util.wav.WavFileException(
            wavFileException19);
    java.lang.Throwable[] throwableArray30 = wavFileException19.getSuppressed();
    opssat.simulator.util.wav.WavFileException wavFileException31 =
        new opssat.simulator.util.wav.WavFileException(
            "3257812:10:11:27:876", wavFileException19);
    wavFileException16.addSuppressed(wavFileException19);
    opssat.simulator.util.wav.WavFileException wavFileException33 =
        new opssat.simulator.util.wav.WavFileException(
            "030939.263", wavFileException19);
    org.junit.Assert.assertTrue("'" + int5 + "' != '" + (-7) + "'", int5.equals((-7)));
    org.junit.Assert.assertNotNull(uShort6);
    org.junit.Assert.assertNotNull(uOctet7);
    org.junit.Assert.assertNotNull(throwableArray10);
    org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + false + "'", !boolean11);
    org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-7) + "'", int21.equals((-7)));
    org.junit.Assert.assertNotNull(uShort22);
    org.junit.Assert.assertNotNull(uOctet23);
    org.junit.Assert.assertNotNull(throwableArray26);
    org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + false + "'", !boolean27);
    org.junit.Assert.assertNotNull(throwableArray30);
  }

  @Test
  public void test1279() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1279");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    boolean boolean14 = simulatorHeader12.isAutoStartSystem();
    int int15 = simulatorHeader12.getDayStartDate();
    boolean boolean16 = simulatorHeader12.isUseCelestia();
    java.lang.String str17 = simulatorHeader12.toString();
    boolean boolean18 = simulatorHeader12.isUpdateInternet();
    java.lang.String str19 = simulatorHeader12.getOrekitTLE2();
    simulatorHeader12.setOrekitTLE2("031033.961");
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
    org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
    org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + false + "'", !boolean18);
    org.junit.Assert.assertNull(str19);
  }

  @Test
  public void test1280() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1280");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState11 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double12 = simulatorSpacecraftState11.getLatitude();
    java.lang.String str13 = simulatorSpacecraftState11.getMagField();
    float[] floatArray14 = simulatorSpacecraftState11.getR();
    simulatorSpacecraftState3.setQ(floatArray14);
    float[] floatArray16 = simulatorSpacecraftState3.getQ();
    double double17 = simulatorSpacecraftState3.getLatitude();
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + 340.0d + "'", double12 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str13 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str13.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray14);
    org.junit.Assert.assertNotNull(floatArray16);
    org.junit.Assert.assertTrue("'" + double17 + "' != '" + 340.0d + "'", double17 == 340.0d);
  }

  @Test
  public void test1281() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1281");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState11 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double12 = simulatorSpacecraftState11.getLatitude();
    java.lang.String str13 = simulatorSpacecraftState11.getMagField();
    float[] floatArray14 = simulatorSpacecraftState11.getR();
    simulatorSpacecraftState3.setQ(floatArray14);
    java.lang.String str16 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray14);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState20 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double21 = simulatorSpacecraftState20.getLatitude();
    double double22 = simulatorSpacecraftState20.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState26 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double27 = simulatorSpacecraftState26.getLatitude();
    java.lang.String str28 = simulatorSpacecraftState26.getMagField();
    float[] floatArray29 = simulatorSpacecraftState26.getR();
    simulatorSpacecraftState20.setQ(floatArray29);
    float[] floatArray31 = simulatorSpacecraftState20.getV();
    opssat.simulator.celestia.CelestiaData celestiaData32 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray14, floatArray31);
    float[] floatArray33 = celestiaData32.getQ();
    opssat.simulator.util.SimulatorData simulatorData37 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date38 = simulatorData37.getCurrentTime();
    int int39 = opssat.simulator.util.DateExtraction.getDayFromDate(date38);
    opssat.simulator.util.SimulatorData simulatorData41 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date42 = simulatorData41.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData44 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date45 = simulatorData44.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap46 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date42, date45);
    opssat.simulator.util.SimulatorHeader simulatorHeader47 =
        new opssat.simulator.util.SimulatorHeader(
            false, date38, date45);
    opssat.simulator.util.SimulatorData simulatorData48 = new opssat.simulator.util.SimulatorData(
        (short) 0, date38);
    celestiaData32.setDate(date38);
    celestiaData32.setAnx("*5D");
    int int52 = celestiaData32.getDays();
    int int53 = celestiaData32.getMinutes();
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + 340.0d + "'", double12 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str13 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str13.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray14);
    org.junit.Assert.assertTrue("'" + str16 + "' != '" + "UnknownGUIData" + "'",
        str16.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double21 + "' != '" + 340.0d + "'", double21 == 340.0d);
    org.junit.Assert.assertTrue("'" + double22 + "' != '" + 340.0d + "'", double22 == 340.0d);
    org.junit.Assert.assertTrue("'" + double27 + "' != '" + 340.0d + "'", double27 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str28 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str28.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray29);
    org.junit.Assert.assertNotNull(floatArray31);
    org.junit.Assert.assertNotNull(floatArray33);
    org.junit.Assert.assertNotNull(date38);
    org.junit.Assert.assertNotNull(date42);
    org.junit.Assert.assertNotNull(date45);
    org.junit.Assert.assertNotNull(timeUnitMap46);
  }

  @Test
  public void test1282() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1282");
    }
    opssat.simulator.util.SimulatorData simulatorData3 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date4 = simulatorData3.getCurrentTime();
    java.util.Date date5 = simulatorData3.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(17,
        date5);
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    int int11 = opssat.simulator.util.DateExtraction.getDayFromDate(date10);
    opssat.simulator.util.SimulatorData simulatorData13 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date14 = simulatorData13.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData16 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date17 = simulatorData16.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap18 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date14, date17);
    opssat.simulator.util.SimulatorHeader simulatorHeader19 =
        new opssat.simulator.util.SimulatorHeader(
            false, date10, date17);
    opssat.simulator.util.SimulatorHeader simulatorHeader20 =
        new opssat.simulator.util.SimulatorHeader(
            false, date5, date10);
    boolean boolean21 = simulatorHeader20.isAutoStartTime();
    simulatorHeader20.setUseCelestia(false);
    simulatorHeader20.setOrekitTLE1("[]");
    simulatorHeader20
        .setOrekitPropagator("opssat.simulator.util.wav.WavFileException: UnknownGUIData");
    java.lang.String str28 = simulatorHeader20.getOrekitPropagator();
    simulatorHeader20.setTimeFactor(8);
    boolean boolean31 = simulatorHeader20.isUpdateInternet();
    int int32 = simulatorHeader20.getMinuteStartDate();
    java.lang.String str33 = simulatorHeader20.getEndDateString();
    java.lang.String str34 = simulatorHeader20.getOrekitPropagator();
    simulatorHeader20.setOrekitPropagator("1");
    boolean boolean37 = simulatorHeader20.isAutoStartSystem();
    opssat.simulator.util.SimulatorData simulatorData41 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date42 = simulatorData41.getCurrentTime();
    java.util.Date date43 = simulatorData41.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData44 = new opssat.simulator.util.SimulatorData(
        17, date43);
    opssat.simulator.util.SimulatorData simulatorData47 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date48 = simulatorData47.getCurrentTime();
    int int49 = opssat.simulator.util.DateExtraction.getDayFromDate(date48);
    opssat.simulator.util.SimulatorData simulatorData51 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date52 = simulatorData51.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData54 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date55 = simulatorData54.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap56 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date52, date55);
    opssat.simulator.util.SimulatorHeader simulatorHeader57 =
        new opssat.simulator.util.SimulatorHeader(
            false, date48, date55);
    opssat.simulator.util.SimulatorHeader simulatorHeader58 =
        new opssat.simulator.util.SimulatorHeader(
            false, date43, date48);
    int int59 = opssat.simulator.util.DateExtraction.getDayFromDate(date48);
    int int60 = opssat.simulator.util.DateExtraction.getDayFromDate(date48);
    simulatorHeader20.setStartDate(date48);
    org.junit.Assert.assertNotNull(date4);
    org.junit.Assert.assertNotNull(date5);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(date14);
    org.junit.Assert.assertNotNull(date17);
    org.junit.Assert.assertNotNull(timeUnitMap18);
    org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
    org.junit.Assert.assertTrue(
        "'" + str28 + "' != '" + "opssat.simulator.util.wav.WavFileException: UnknownGUIData" + "'",
        str28.equals("opssat.simulator.util.wav.WavFileException: UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + false + "'", !boolean31);
    org.junit.Assert.assertTrue(
        "'" + str34 + "' != '" + "opssat.simulator.util.wav.WavFileException: UnknownGUIData" + "'",
        str34.equals("opssat.simulator.util.wav.WavFileException: UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + false + "'", !boolean37);
    org.junit.Assert.assertNotNull(date42);
    org.junit.Assert.assertNotNull(date43);
    org.junit.Assert.assertNotNull(date48);
    org.junit.Assert.assertNotNull(date52);
    org.junit.Assert.assertNotNull(date55);
    org.junit.Assert.assertNotNull(timeUnitMap56);
  }

  @Test
  public void test1283() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1283");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor3 = octetList0.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor5 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList0, "hi!");
    org.ccsds.moims.mo.mal.structures.UShort uShort6 = octetList0.getAreaNumber();
    java.lang.Short[] shortArray9 = new java.lang.Short[]{(short) -1, (short) 10};
    java.util.ArrayList<java.lang.Short> shortList10 = new java.util.ArrayList<java.lang.Short>();
    boolean boolean11 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Short>) shortList10, shortArray9);
    boolean boolean12 = shortList10.isEmpty();
    java.lang.Object obj13 = null;
    boolean boolean14 = shortList10.contains(obj13);
    boolean boolean15 = octetList0.contains(boolean14);
    java.util.ListIterator<java.lang.Byte> byteItor16 = octetList0.listIterator();
    try {
      java.lang.Byte byte19 = octetList0.set(21, (byte) 1);
      org.junit.Assert.fail(
          "Expected exception of type java.lang.IndexOutOfBoundsException; message: Index: 21, Size: 0");
    } catch (java.lang.IndexOutOfBoundsException e) {
    }
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(byteItor3);
    org.junit.Assert.assertNotNull(uShort6);
    org.junit.Assert.assertNotNull(shortArray9);
    org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
    org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + false + "'", !boolean12);
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
    org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
    org.junit.Assert.assertNotNull(byteItor16);
  }

  @Test
  public void test1284() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1284");
    }
    org.ccsds.moims.mo.mal.structures.UShortList uShortList0 =
        new org.ccsds.moims.mo.mal.structures.UShortList();
    org.ccsds.moims.mo.mal.structures.UShort uShort1 = uShortList0.getServiceNumber();
    boolean boolean2 = uShortList0.isEmpty();
    org.ccsds.moims.mo.mal.structures.UShort uShort3 = uShortList0.getAreaNumber();
    java.lang.Byte[] byteArray8 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList9 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean10 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList9, byteArray8);
    java.lang.Integer[] intArray13 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList14 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean15 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList14, intArray13);
    boolean boolean16 = byteList9.retainAll(intList14);
    java.lang.Integer[] intArray19 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList20 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean21 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList20, intArray19);
    int int23 = intList20.lastIndexOf((byte) 10);
    boolean boolean24 = intList14.retainAll(intList20);
    java.lang.Double[] doubleArray29 = new java.lang.Double[]{(-1.0d), 100.0d, 10.0d, 10.0d};
    java.util.ArrayList<java.lang.Double> doubleList30 = new java.util.ArrayList<java.lang.Double>();
    boolean boolean31 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Double>) doubleList30, doubleArray29);
    org.ccsds.moims.mo.mal.structures.UShort uShort32 =
        org.ccsds.moims.mo.mal.structures.URIList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort[] uShortArray33 =
        new org.ccsds.moims.mo.mal.structures.UShort[]{
          uShort32};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList34 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
    boolean boolean35 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList34,
        uShortArray33);
    uShortList34.ensureCapacity(0);
    int int39 = uShortList34.indexOf((byte) 1);
    uShortList34.clear();
    java.lang.Long[] longArray43 = new java.lang.Long[]{0L, 10L};
    java.util.ArrayList<java.lang.Long> longList44 = new java.util.ArrayList<java.lang.Long>();
    boolean boolean45 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Long>) longList44, longArray43);
    java.lang.Object obj46 = longList44.clone();
    boolean boolean47 = uShortList34.contains(longList44);
    boolean boolean48 = doubleList30.equals(boolean47);
    java.lang.Integer[] intArray51 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList52 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean53 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList52, intArray51);
    int int55 = intList52.lastIndexOf((byte) 10);
    boolean boolean56 = doubleList30.removeAll(intList52);
    boolean boolean57 = intList20.retainAll(intList52);
    opssat.simulator.util.SimulatorData simulatorData61 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date62 = simulatorData61.getCurrentTime();
    java.util.Date date63 = simulatorData61.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData64 = new opssat.simulator.util.SimulatorData(
        17, date63);
    opssat.simulator.util.SimulatorData simulatorData67 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date68 = simulatorData67.getCurrentTime();
    int int69 = opssat.simulator.util.DateExtraction.getDayFromDate(date68);
    opssat.simulator.util.SimulatorData simulatorData71 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date72 = simulatorData71.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData74 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date75 = simulatorData74.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap76 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date72, date75);
    opssat.simulator.util.SimulatorHeader simulatorHeader77 =
        new opssat.simulator.util.SimulatorHeader(
            false, date68, date75);
    opssat.simulator.util.SimulatorHeader simulatorHeader78 =
        new opssat.simulator.util.SimulatorHeader(
            false, date63, date68);
    simulatorHeader78.setUpdateInternet(true);
    java.util.Date date81 = null;
    simulatorHeader78.setEndDate(date81);
    int int83 = intList20.lastIndexOf(date81);
    boolean boolean84 = uShortList0.removeAll(intList20);
    java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream85 = uShortList0
        .parallelStream();
    uShortList0.trimToSize();
    try {
      java.util.List<org.ccsds.moims.mo.mal.structures.UShort> uShortList89 = uShortList0
          .subList('a', 2);
      org.junit.Assert.fail(
          "Expected exception of type java.lang.IndexOutOfBoundsException; message: toIndex = 2");
    } catch (java.lang.IndexOutOfBoundsException e) {
    }
    org.junit.Assert.assertNotNull(uShort1);
    org.junit.Assert.assertTrue("'" + boolean2 + "' != '" + true + "'", boolean2);
    org.junit.Assert.assertNotNull(uShort3);
    org.junit.Assert.assertNotNull(byteArray8);
    org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
    org.junit.Assert.assertNotNull(intArray13);
    org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + true + "'", boolean15);
    org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + true + "'", boolean16);
    org.junit.Assert.assertNotNull(intArray19);
    org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + true + "'", boolean21);
    org.junit.Assert.assertTrue("'" + int23 + "' != '" + (-1) + "'", int23 == (-1));
    org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + true + "'", boolean24);
    org.junit.Assert.assertNotNull(doubleArray29);
    org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + true + "'", boolean31);
    org.junit.Assert.assertNotNull(uShort32);
    org.junit.Assert.assertNotNull(uShortArray33);
    org.junit.Assert.assertTrue("'" + boolean35 + "' != '" + true + "'", boolean35);
    org.junit.Assert.assertTrue("'" + int39 + "' != '" + (-1) + "'", int39 == (-1));
    org.junit.Assert.assertNotNull(longArray43);
    org.junit.Assert.assertTrue("'" + boolean45 + "' != '" + true + "'", boolean45);
    org.junit.Assert.assertNotNull(obj46);
    org.junit.Assert.assertTrue("'" + boolean47 + "' != '" + false + "'", !boolean47);
    org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + false + "'", !boolean48);
    org.junit.Assert.assertNotNull(intArray51);
    org.junit.Assert.assertTrue("'" + boolean53 + "' != '" + true + "'", boolean53);
    org.junit.Assert.assertTrue("'" + int55 + "' != '" + (-1) + "'", int55 == (-1));
    org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + false + "'", !boolean56);
    org.junit.Assert.assertTrue("'" + boolean57 + "' != '" + false + "'", !boolean57);
    org.junit.Assert.assertNotNull(date62);
    org.junit.Assert.assertNotNull(date63);
    org.junit.Assert.assertNotNull(date68);
    org.junit.Assert.assertNotNull(date72);
    org.junit.Assert.assertNotNull(date75);
    org.junit.Assert.assertNotNull(timeUnitMap76);
    org.junit.Assert.assertTrue("'" + int83 + "' != '" + (-1) + "'", int83 == (-1));
    org.junit.Assert.assertTrue("'" + boolean84 + "' != '" + false + "'", !boolean84);
    org.junit.Assert.assertNotNull(uShortStream85);
  }

  @Test
  public void test1285() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1285");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet3 = octetList0.getAreaVersion();
    opssat.simulator.util.wav.WavFileException wavFileException5 =
        new opssat.simulator.util.wav.WavFileException(
            "UnknownGUIData");
    java.lang.Throwable[] throwableArray6 = wavFileException5.getSuppressed();
    boolean boolean7 = octetList0.equals(wavFileException5);
    org.ccsds.moims.mo.mal.structures.Time time9 = new org.ccsds.moims.mo.mal.structures.Time('4');
    long long10 = time9.getValue();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet11 = time9.getAreaVersion();
    boolean boolean12 = octetList0.contains(uOctet11);
    java.lang.String str13 = octetList0.toString();
    try {
      java.util.ListIterator<java.lang.Byte> byteItor15 = octetList0.listIterator(28);
      org.junit.Assert.fail(
          "Expected exception of type java.lang.IndexOutOfBoundsException; message: Index: 28");
    } catch (java.lang.IndexOutOfBoundsException e) {
    }
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(uOctet3);
    org.junit.Assert.assertNotNull(throwableArray6);
    org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
    org.junit.Assert.assertTrue("'" + long10 + "' != '" + 52L + "'", long10 == 52L);
    org.junit.Assert.assertNotNull(uOctet11);
    org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + false + "'", !boolean12);
    org.junit.Assert.assertTrue("'" + str13 + "' != '" + "[]" + "'", str13.equals("[]"));
  }

  @Test
  public void test1287() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1287");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double4 = simulatorSpacecraftState3.getLatitude();
    double double5 = simulatorSpacecraftState3.getLongitude();
    java.lang.String str6 = simulatorSpacecraftState3.getModeOperation();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState10 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState10.setLongitude(4);
    double[] doubleArray14 = new double[]{(-1.0f)};
    simulatorSpacecraftState10.setMagField(doubleArray14);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState19 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double20 = simulatorSpacecraftState19.getLatitude();
    java.lang.String str21 = simulatorSpacecraftState19.getMagField();
    java.lang.String str22 = simulatorSpacecraftState19.toString();
    double[] doubleArray23 = simulatorSpacecraftState19.getSunVector();
    simulatorSpacecraftState10.setMagnetometer(doubleArray23);
    simulatorSpacecraftState3.setMagField(doubleArray23);
    simulatorSpacecraftState3.setModeOperation("0");
    simulatorSpacecraftState3.setModeOperation("030936.762");
    simulatorSpacecraftState3.setAltitude(60);
    simulatorSpacecraftState3.setAltitude((-2));
    org.junit.Assert.assertTrue("'" + double4 + "' != '" + 340.0d + "'", double4 == 340.0d);
    org.junit.Assert.assertTrue("'" + double5 + "' != '" + (-1.0d) + "'", double5 == (-1.0d));
    org.junit.Assert.assertNull(str6);
    org.junit.Assert.assertNotNull(doubleArray14);
    org.junit.Assert.assertTrue("'" + double20 + "' != '" + 340.0d + "'", double20 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str21 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str21.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str22 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str22.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray23);
  }

  @Test
  public void test1288() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1288");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState11 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double12 = simulatorSpacecraftState11.getLatitude();
    java.lang.String str13 = simulatorSpacecraftState11.getMagField();
    float[] floatArray14 = simulatorSpacecraftState11.getR();
    simulatorSpacecraftState3.setQ(floatArray14);
    java.lang.String str16 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray14);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState20 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double21 = simulatorSpacecraftState20.getLatitude();
    double double22 = simulatorSpacecraftState20.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState26 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double27 = simulatorSpacecraftState26.getLatitude();
    java.lang.String str28 = simulatorSpacecraftState26.getMagField();
    float[] floatArray29 = simulatorSpacecraftState26.getR();
    simulatorSpacecraftState20.setQ(floatArray29);
    float[] floatArray31 = simulatorSpacecraftState20.getV();
    opssat.simulator.celestia.CelestiaData celestiaData32 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray14, floatArray31);
    float[] floatArray33 = celestiaData32.getQ();
    opssat.simulator.util.SimulatorData simulatorData37 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date38 = simulatorData37.getCurrentTime();
    int int39 = opssat.simulator.util.DateExtraction.getDayFromDate(date38);
    opssat.simulator.util.SimulatorData simulatorData41 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date42 = simulatorData41.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData44 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date45 = simulatorData44.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap46 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date42, date45);
    opssat.simulator.util.SimulatorHeader simulatorHeader47 =
        new opssat.simulator.util.SimulatorHeader(
            false, date38, date45);
    opssat.simulator.util.SimulatorData simulatorData48 = new opssat.simulator.util.SimulatorData(
        (short) 0, date38);
    celestiaData32.setDate(date38);
    int int50 = celestiaData32.getSeconds();
    celestiaData32.setDnx("OPS-SAT SoftSim:");
    java.lang.String str53 = celestiaData32.getAos();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState57 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double58 = simulatorSpacecraftState57.getLatitude();
    double double59 = simulatorSpacecraftState57.getLongitude();
    float[] floatArray60 = simulatorSpacecraftState57.getQ();
    celestiaData32.setQ(floatArray60);
    float[] floatArray62 = celestiaData32.getRv();
    try {
      java.lang.String str63 = celestiaData32.toString();
      org.junit.Assert
          .fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException; message: 3");
    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
    }
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + 340.0d + "'", double12 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str13 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str13.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray14);
    org.junit.Assert.assertTrue("'" + str16 + "' != '" + "UnknownGUIData" + "'",
        str16.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double21 + "' != '" + 340.0d + "'", double21 == 340.0d);
    org.junit.Assert.assertTrue("'" + double22 + "' != '" + 340.0d + "'", double22 == 340.0d);
    org.junit.Assert.assertTrue("'" + double27 + "' != '" + 340.0d + "'", double27 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str28 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str28.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray29);
    org.junit.Assert.assertNotNull(floatArray31);
    org.junit.Assert.assertNotNull(floatArray33);
    org.junit.Assert.assertNotNull(date38);
    org.junit.Assert.assertNotNull(date42);
    org.junit.Assert.assertNotNull(date45);
    org.junit.Assert.assertNotNull(timeUnitMap46);
    org.junit.Assert.assertNull(str53);
    org.junit.Assert.assertTrue("'" + double58 + "' != '" + 340.0d + "'", double58 == 340.0d);
    org.junit.Assert.assertTrue("'" + double59 + "' != '" + (-1.0d) + "'", double59 == (-1.0d));
    org.junit.Assert.assertNotNull(floatArray60);
    org.junit.Assert.assertNotNull(floatArray62);
  }

  @Test
  public void test1289() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1289");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState3.setLongitude(4);
    double[] doubleArray7 = new double[]{(-1.0f)};
    simulatorSpacecraftState3.setMagField(doubleArray7);
    simulatorSpacecraftState3.setLatitude(0.0d);
    java.util.logging.Logger logger11 = null;
    opssat.simulator.util.EndlessWavStreamOperatingBuffer endlessWavStreamOperatingBuffer12 =
        new opssat.simulator.util.EndlessWavStreamOperatingBuffer(
            logger11);
    java.lang.Object obj13 = endlessWavStreamOperatingBuffer12.getDataBuffer();
    java.lang.Object obj14 = endlessWavStreamOperatingBuffer12.getDataBuffer();
    double[] doubleArray16 = endlessWavStreamOperatingBuffer12.getDataAsDoubleArray(45);
    try {
      simulatorSpacecraftState3.setMagField(doubleArray16);
      org.junit.Assert
          .fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException; message: 3");
    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
    }
    org.junit.Assert.assertNotNull(doubleArray7);
    org.junit.Assert.assertNotNull(obj13);
    org.junit.Assert.assertNotNull(obj14);
    org.junit.Assert.assertNotNull(doubleArray16);
  }

  @Test
  public void test1290() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1290");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState3.setLongitude(4);
    int int6 = simulatorSpacecraftState3.getSatsInView();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState10 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double11 = simulatorSpacecraftState10.getLatitude();
    java.lang.String str12 = simulatorSpacecraftState10.getMagField();
    java.lang.String str13 = simulatorSpacecraftState10.toString();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState17 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double18 = simulatorSpacecraftState17.getLatitude();
    java.lang.String str19 = simulatorSpacecraftState17.getMagField();
    java.lang.String str20 = simulatorSpacecraftState17.toString();
    double[] doubleArray21 = simulatorSpacecraftState17.getSunVector();
    simulatorSpacecraftState10.setMagnetometer(doubleArray21);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState26 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double27 = simulatorSpacecraftState26.getLatitude();
    java.lang.String str28 = simulatorSpacecraftState26.getMagField();
    java.lang.String str29 = simulatorSpacecraftState26.toString();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState33 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double34 = simulatorSpacecraftState33.getLatitude();
    java.lang.String str35 = simulatorSpacecraftState33.getMagField();
    java.lang.String str36 = simulatorSpacecraftState33.toString();
    double[] doubleArray37 = simulatorSpacecraftState33.getSunVector();
    simulatorSpacecraftState26.setMagnetometer(doubleArray37);
    simulatorSpacecraftState10.setMagnetometer(doubleArray37);
    simulatorSpacecraftState3.setSunVector(doubleArray37);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState44 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray47 = new float[]{28, 8};
    simulatorSpacecraftState44.setQ(floatArray47);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState52 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double53 = simulatorSpacecraftState52.getLatitude();
    java.lang.String str54 = simulatorSpacecraftState52.getMagField();
    float[] floatArray55 = simulatorSpacecraftState52.getR();
    simulatorSpacecraftState44.setQ(floatArray55);
    java.lang.String str57 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray55);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState61 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double62 = simulatorSpacecraftState61.getLatitude();
    double double63 = simulatorSpacecraftState61.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState67 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double68 = simulatorSpacecraftState67.getLatitude();
    java.lang.String str69 = simulatorSpacecraftState67.getMagField();
    float[] floatArray70 = simulatorSpacecraftState67.getR();
    simulatorSpacecraftState61.setQ(floatArray70);
    float[] floatArray72 = simulatorSpacecraftState61.getV();
    opssat.simulator.celestia.CelestiaData celestiaData73 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray55, floatArray72);
    simulatorSpacecraftState3.setQ(floatArray72);
    java.lang.String str75 = simulatorSpacecraftState3.getSunVectorAsString();
    double[] doubleArray76 = simulatorSpacecraftState3.getSunVector();
    org.junit.Assert.assertTrue("'" + int6 + "' != '" + 0 + "'", int6 == 0);
    org.junit.Assert.assertTrue("'" + double11 + "' != '" + 340.0d + "'", double11 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str12 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str12.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str13 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str13.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertTrue("'" + double18 + "' != '" + 340.0d + "'", double18 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str19 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str19.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str20 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str20.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray21);
    org.junit.Assert.assertTrue("'" + double27 + "' != '" + 340.0d + "'", double27 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str28 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str28.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str29 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str29.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertTrue("'" + double34 + "' != '" + 340.0d + "'", double34 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str35 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str35.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str36 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str36.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray37);
    org.junit.Assert.assertNotNull(floatArray47);
    org.junit.Assert.assertTrue("'" + double53 + "' != '" + 340.0d + "'", double53 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str54 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str54.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray55);
    org.junit.Assert.assertTrue("'" + str57 + "' != '" + "UnknownGUIData" + "'",
        str57.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double62 + "' != '" + 340.0d + "'", double62 == 340.0d);
    org.junit.Assert.assertTrue("'" + double63 + "' != '" + 340.0d + "'", double63 == 340.0d);
    org.junit.Assert.assertTrue("'" + double68 + "' != '" + 340.0d + "'", double68 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str69 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str69.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray70);
    org.junit.Assert.assertNotNull(floatArray72);
    org.junit.Assert.assertTrue(
        "'" + str75 + "' != '" + "X:[+.00000] ; Y:[+.00000] ; Z: [+.00000] units [N/A]" + "'",
        str75.equals("X:[+.00000] ; Y:[+.00000] ; Z: [+.00000] units [N/A]"));
    org.junit.Assert.assertNotNull(doubleArray76);
  }

  @Test
  public void test1291() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1291");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessSingleStreamOperatingBuffer endlessSingleStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessSingleStreamOperatingBuffer(
            logger0);
    byte[] byteArray3 = endlessSingleStreamOperatingBuffer1.getDataAsByteArray('4');
    boolean boolean5 = endlessSingleStreamOperatingBuffer1
        .preparePath("00000:00:00:00:013|0000000000000000013|0|hi!");
    int int6 = endlessSingleStreamOperatingBuffer1.getOperatingIndex();
    org.junit.Assert.assertNotNull(byteArray3);
    org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + false + "'", !boolean5);
    org.junit.Assert.assertTrue("'" + int6 + "' != '" + 0 + "'", int6 == 0);
  }

  @Test
  public void test1292() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1292");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double4 = simulatorSpacecraftState3.getLatitude();
    java.lang.String str5 = simulatorSpacecraftState3.getMagField();
    java.lang.String str6 = simulatorSpacecraftState3.getSunVectorAsString();
    simulatorSpacecraftState3.setSatsInView(13);
    double[] doubleArray9 = null;
    try {
      simulatorSpacecraftState3.setMagField(doubleArray9);
      org.junit.Assert
          .fail("Expected exception of type java.lang.NullPointerException; message: null");
    } catch (java.lang.NullPointerException e) {
    }
    org.junit.Assert.assertTrue("'" + double4 + "' != '" + 340.0d + "'", double4 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str5 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str5.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str6 + "' != '" + "X:[+.00000] ; Y:[+.00000] ; Z: [+.00000] units [N/A]" + "'",
        str6.equals("X:[+.00000] ; Y:[+.00000] ; Z: [+.00000] units [N/A]"));
  }

  @Test
  public void test1293() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1293");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    opssat.simulator.util.SimulatorData simulatorData17 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date18 = simulatorData17.getCurrentTime();
    java.util.Date date19 = simulatorData17.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData20 = new opssat.simulator.util.SimulatorData(
        17, date19);
    opssat.simulator.util.SimulatorData simulatorData23 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date24 = simulatorData23.getCurrentTime();
    int int25 = opssat.simulator.util.DateExtraction.getDayFromDate(date24);
    opssat.simulator.util.SimulatorData simulatorData27 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date28 = simulatorData27.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData30 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date31 = simulatorData30.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap32 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date28, date31);
    opssat.simulator.util.SimulatorHeader simulatorHeader33 =
        new opssat.simulator.util.SimulatorHeader(
            false, date24, date31);
    opssat.simulator.util.SimulatorHeader simulatorHeader34 =
        new opssat.simulator.util.SimulatorHeader(
            false, date19, date24);
    simulatorHeader12.setEndDate(date19);
    int int36 = simulatorHeader12.getHourStartDate();
    java.util.Date date37 = simulatorHeader12.getStartDate();
    simulatorHeader12.setUseOrekitPropagator(true);
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertNotNull(date18);
    org.junit.Assert.assertNotNull(date19);
    org.junit.Assert.assertNotNull(date24);
    org.junit.Assert.assertNotNull(date28);
    org.junit.Assert.assertNotNull(date31);
    org.junit.Assert.assertNotNull(timeUnitMap32);
    org.junit.Assert.assertNotNull(date37);
  }

  @Test
  public void test1294() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1294");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double4 = simulatorSpacecraftState3.getLatitude();
    java.lang.String str5 = simulatorSpacecraftState3.getMagField();
    java.lang.String str6 = simulatorSpacecraftState3.toString();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState10 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double11 = simulatorSpacecraftState10.getLatitude();
    double double12 = simulatorSpacecraftState10.getLongitude();
    java.lang.String str13 = simulatorSpacecraftState10.getModeOperation();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState17 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState17.setLongitude(4);
    double[] doubleArray21 = new double[]{(-1.0f)};
    simulatorSpacecraftState17.setMagField(doubleArray21);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState26 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double27 = simulatorSpacecraftState26.getLatitude();
    java.lang.String str28 = simulatorSpacecraftState26.getMagField();
    java.lang.String str29 = simulatorSpacecraftState26.toString();
    double[] doubleArray30 = simulatorSpacecraftState26.getSunVector();
    simulatorSpacecraftState17.setMagnetometer(doubleArray30);
    simulatorSpacecraftState10.setMagField(doubleArray30);
    simulatorSpacecraftState3.setMagField(doubleArray30);
    float[] floatArray34 = simulatorSpacecraftState3.getRv();
    simulatorSpacecraftState3.setLongitude(44);
    org.junit.Assert.assertTrue("'" + double4 + "' != '" + 340.0d + "'", double4 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str5 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str5.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str6 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str6.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertTrue("'" + double11 + "' != '" + 340.0d + "'", double11 == 340.0d);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + (-1.0d) + "'", double12 == (-1.0d));
    org.junit.Assert.assertNull(str13);
    org.junit.Assert.assertNotNull(doubleArray21);
    org.junit.Assert.assertTrue("'" + double27 + "' != '" + 340.0d + "'", double27 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str28 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str28.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str29 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str29.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray30);
    org.junit.Assert.assertNotNull(floatArray34);
  }

  @Test
  public void test1295() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1295");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    boolean boolean14 = simulatorHeader12.isAutoStartSystem();
    int int15 = simulatorHeader12.getDayStartDate();
    boolean boolean16 = simulatorHeader12.isUseCelestia();
    int int17 = simulatorHeader12.getDayStartDate();
    simulatorHeader12.setAutoStartTime(false);
    java.lang.String str20 = simulatorHeader12.getOrekitTLE1();
    simulatorHeader12.setTimeFactor(70);
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
    org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
    org.junit.Assert.assertNull(str20);
  }

  @Test
  public void test1296() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1296");
    }
    opssat.simulator.util.LoggerFormatter1Line loggerFormatter1Line1 =
        new opssat.simulator.util.LoggerFormatter1Line(
            "031041.629");
  }

  @Test
  public void test1297() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1297");
    }
    opssat.simulator.gui.GuiApp guiApp2 = null;
    opssat.simulator.tcp.SocketClient socketClient3 = new opssat.simulator.tcp.SocketClient(
        "{counter=8, methodsExecuted=0, currentTime=Thu May 23 15:09:32 UTC 2019}", 0, guiApp2);
    socketClient3.setTargetConnection("030936.762", 4);
    socketClient3.setTargetConnection("", 40);
    socketClient3.setTargetConnection("2019:05:23 15:10:06 UTC", 100);
    try {
      socketClient3.run();
      org.junit.Assert
          .fail("Expected exception of type java.lang.NullPointerException; message: null");
    } catch (java.lang.NullPointerException e) {
    }
  }

  @Test
  public void test1299() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1299");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState3.setLongitude(4);
    double[] doubleArray7 = new double[]{(-1.0f)};
    simulatorSpacecraftState3.setMagField(doubleArray7);
    double double9 = simulatorSpacecraftState3.getLongitude();
    float[] floatArray10 = simulatorSpacecraftState3.getMagnetometer();
    simulatorSpacecraftState3.setLongitude((-15));
    java.lang.String str13 = simulatorSpacecraftState3.getMagnetometerAsString();
    org.junit.Assert.assertNotNull(doubleArray7);
    org.junit.Assert.assertTrue("'" + double9 + "' != '" + 4.0d + "'", double9 == 4.0d);
    org.junit.Assert.assertNotNull(floatArray10);
    org.junit.Assert.assertTrue(
        "'" + str13 + "' != '" + "X:[+00000.00] ; Y:[+00000.00] ; Z: [+00000.00] units [nT]" + "'",
        str13.equals("X:[+00000.00] ; Y:[+00000.00] ; Z: [+00000.00] units [nT]"));
  }

  @Test
  public void test1300() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1300");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor3 = octetList0.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor5 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList0, "hi!");
    argumentDescriptor5.restoreArgument();
    argumentDescriptor5.restoreArgument();
    java.lang.String str8 = argumentDescriptor5.getName();
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(byteItor3);
    org.junit.Assert.assertTrue("'" + str8 + "' != '" + "hi!" + "'", str8.equals("hi!"));
  }

  @Test
  public void test1301() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1301");
    }
    org.ccsds.moims.mo.mal.structures.UInteger uInteger1 =
        new org.ccsds.moims.mo.mal.structures.UInteger(
            13);
    org.ccsds.moims.mo.mal.structures.Element element2 = uInteger1.createElement();
    java.lang.Long long3 = uInteger1.getShortForm();
    long long4 = uInteger1.getValue();
    org.ccsds.moims.mo.mal.structures.Element element5 = uInteger1.createElement();
    java.lang.String str7 = opssat.simulator.threading.SimulatorNode.dump(uInteger1, 11);
    org.junit.Assert.assertNotNull(element2);
    org.junit.Assert.assertTrue("'" + long3 + "' != '" + 281474993487884L + "'",
        long3.equals(281474993487884L));
    org.junit.Assert.assertTrue("'" + long4 + "' != '" + 13L + "'", long4 == 13L);
    org.junit.Assert.assertNotNull(element5);
// flaky:         org.junit.Assert.assertTrue("'" + str7 + "' != '" + "\n\t\t\t\t\t\t\t\t\t\t\t\t{\n\t\t\t\t\t\t\t\t\t\t\t\tvalue=13\n\t\t\t\t\t\t\t\t\t\t\t\tMAX_VALUE=4294967295\n\t\t\t\t\t\t\t\t\t\t\t\tserialVersionUID=281474993487884\n\t\t\t\t\t\t\t\t\t\t\t\trandoop_classUsedFlag=true\n\t\t\t\t\t\t\t\t\t\t\t\t}\n" + "'", str7.equals("\n\t\t\t\t\t\t\t\t\t\t\t\t{\n\t\t\t\t\t\t\t\t\t\t\t\tvalue=13\n\t\t\t\t\t\t\t\t\t\t\t\tMAX_VALUE=4294967295\n\t\t\t\t\t\t\t\t\t\t\t\tserialVersionUID=281474993487884\n\t\t\t\t\t\t\t\t\t\t\t\trandoop_classUsedFlag=true\n\t\t\t\t\t\t\t\t\t\t\t\t}\n"));
  }

  @Test
  public void test1304() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1304");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getServiceNumber();
    org.ccsds.moims.mo.mal.structures.OctetList octetList3 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int4 = octetList3.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort5 = octetList3.getAreaNumber();
    java.lang.Object[] objArray6 = octetList3.toArray();
    org.ccsds.moims.mo.mal.structures.OctetList octetList7 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int8 = octetList7.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort9 = octetList7.getServiceNumber();
    java.util.Iterator<java.lang.Byte> byteItor10 = octetList7.iterator();
    opssat.simulator.util.ArgumentDescriptor argumentDescriptor12 =
        new opssat.simulator.util.ArgumentDescriptor(
            octetList7, "hi!");
    org.ccsds.moims.mo.mal.structures.UShort uShort13 = octetList7.getAreaNumber();
    java.lang.Boolean[] booleanArray16 = new java.lang.Boolean[]{true, true};
    java.util.ArrayList<java.lang.Boolean> booleanList17 =
        new java.util.ArrayList<java.lang.Boolean>();
    boolean boolean18 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Boolean>) booleanList17, booleanArray16);
    int int20 = booleanList17.indexOf(10);
    java.util.Iterator<java.lang.Boolean> booleanItor21 = booleanList17.iterator();
    java.util.Spliterator<java.lang.Boolean> booleanSpliterator22 = booleanList17.spliterator();
    org.ccsds.moims.mo.mal.structures.OctetList octetList23 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int24 = octetList23.getTypeShortForm();
    java.lang.Object obj25 = octetList23.clone();
    octetList23.trimToSize();
    boolean boolean27 = booleanList17.contains(octetList23);
    org.ccsds.moims.mo.mal.structures.OctetList octetList28 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int29 = octetList28.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort30 = octetList28.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.OctetList octetList31 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int32 = octetList31.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort33 = octetList31.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet34 = octetList31.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.OctetList octetList35 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int36 = octetList35.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort37 = octetList35.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.OctetList[] octetListArray38 =
        new org.ccsds.moims.mo.mal.structures.OctetList[]{
          octetList3, octetList7, octetList23, octetList28, octetList31, octetList35};
    org.ccsds.moims.mo.mal.structures.OctetList[] octetListArray39 = octetList0
        .toArray(octetListArray38);
    java.lang.Long long40 = octetList0.getShortForm();
    java.util.Spliterator<java.lang.Byte> byteSpliterator41 = octetList0.spliterator();
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertTrue("'" + int4 + "' != '" + (-7) + "'", int4.equals((-7)));
    org.junit.Assert.assertNotNull(uShort5);
    org.junit.Assert.assertNotNull(objArray6);
    org.junit.Assert.assertTrue("'" + int8 + "' != '" + (-7) + "'", int8.equals((-7)));
    org.junit.Assert.assertNotNull(uShort9);
    org.junit.Assert.assertNotNull(byteItor10);
    org.junit.Assert.assertNotNull(uShort13);
    org.junit.Assert.assertNotNull(booleanArray16);
    org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + true + "'", boolean18);
    org.junit.Assert.assertTrue("'" + int20 + "' != '" + (-1) + "'", int20 == (-1));
    org.junit.Assert.assertNotNull(booleanItor21);
    org.junit.Assert.assertNotNull(booleanSpliterator22);
    org.junit.Assert.assertTrue("'" + int24 + "' != '" + (-7) + "'", int24.equals((-7)));
    org.junit.Assert.assertNotNull(obj25);
    org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + false + "'", !boolean27);
    org.junit.Assert.assertTrue("'" + int29 + "' != '" + (-7) + "'", int29.equals((-7)));
    org.junit.Assert.assertNotNull(uShort30);
    org.junit.Assert.assertTrue("'" + int32 + "' != '" + (-7) + "'", int32.equals((-7)));
    org.junit.Assert.assertNotNull(uShort33);
    org.junit.Assert.assertNotNull(uOctet34);
    org.junit.Assert.assertTrue("'" + int36 + "' != '" + (-7) + "'", int36.equals((-7)));
    org.junit.Assert.assertNotNull(uShort37);
    org.junit.Assert.assertNotNull(octetListArray38);
    org.junit.Assert.assertNotNull(octetListArray39);
    org.junit.Assert.assertTrue("'" + long40 + "' != '" + 281475010265081L + "'",
        long40.equals(281475010265081L));
    org.junit.Assert.assertNotNull(byteSpliterator41);
  }

  @Test
  public void test1305() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1305");
    }
    opssat.simulator.threading.SimulatorNode simulatorNode0 = null;
    opssat.simulator.peripherals.PCamera pCamera2 = new opssat.simulator.peripherals.PCamera(
        simulatorNode0, "031035.100");
    try {
      byte[] byteArray5 = pCamera2.takePicture(38, 37);
      org.junit.Assert
          .fail("Expected exception of type java.lang.NullPointerException; message: null");
    } catch (java.lang.NullPointerException e) {
    }
  }

  @Test
  public void test1306() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1306");
    }
    opssat.simulator.util.SimulatorSchedulerPiece simulatorSchedulerPiece3 =
        new opssat.simulator.util.SimulatorSchedulerPiece(
            (-30), 0, "opssat.simulator.util.wav.WavFileException: [0, 1]");
    java.lang.String str4 = simulatorSchedulerPiece3.getFileString();
    org.junit.Assert.assertTrue("'" + str4 + "' != '"
        + "00000:00:00:00:-30|-000000000000000030|0|opssat.simulator.util.wav.WavFileException: [0, 1]"
        + "'",
        str4.equals(
            "00000:00:00:00:-30|-000000000000000030|0|opssat.simulator.util.wav.WavFileException: [0, 1]"));
  }

  @Test
  public void test1307() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1307");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessWavStreamOperatingBuffer endlessWavStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessWavStreamOperatingBuffer(
            logger0);
    int int2 = endlessWavStreamOperatingBuffer1.getOperatingIndex();
    byte[] byteArray4 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS
        .long2ByteArray(281475010265075L);
    endlessWavStreamOperatingBuffer1.setDataFromByteArray(byteArray4);
    java.util.logging.Logger logger6 = null;
    opssat.simulator.util.EndlessWavStreamOperatingBuffer endlessWavStreamOperatingBuffer7 =
        new opssat.simulator.util.EndlessWavStreamOperatingBuffer(
            logger6);
    java.lang.Object obj8 = endlessWavStreamOperatingBuffer7.getDataBuffer();
    boolean boolean10 = endlessWavStreamOperatingBuffer7.preparePath("03600.0000000");
    int int11 = endlessWavStreamOperatingBuffer7.getOperatingIndex();
    byte[] byteArray13 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS
        .long2ByteArray((short) -1);
    endlessWavStreamOperatingBuffer7.setDataFromByteArray(byteArray13);
    endlessWavStreamOperatingBuffer1.setDataFromByteArray(byteArray13);
    java.lang.String str16 = endlessWavStreamOperatingBuffer1.getDataBufferAsString();
    org.junit.Assert.assertTrue("'" + int2 + "' != '" + 0 + "'", int2 == 0);
    org.junit.Assert.assertNotNull(byteArray4);
    org.junit.Assert.assertNotNull(obj8);
    org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + false + "'", !boolean10);
    org.junit.Assert.assertTrue("'" + int11 + "' != '" + 0 + "'", int11 == 0);
    org.junit.Assert.assertNotNull(byteArray13);
    org.junit.Assert.assertTrue(
        "'" + str16 + "' != '" + "byte[] {0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF}" + "'",
        str16.equals("byte[] {0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF}"));
  }

  @Test
  public void test1308() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1308");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    opssat.simulator.util.SimulatorData simulatorData14 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date15 = simulatorData14.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData17 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date18 = simulatorData17.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap19 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date15, date18);
    int int20 = opssat.simulator.util.DateExtraction.getHourFromDate(date15);
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap21 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date10, date15);
    int int22 = opssat.simulator.util.DateExtraction.getDayFromDate(date15);
    opssat.simulator.util.SimulatorData simulatorData26 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date27 = simulatorData26.getCurrentTime();
    java.util.Date date28 = simulatorData26.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData29 = new opssat.simulator.util.SimulatorData(
        17, date28);
    opssat.simulator.util.SimulatorData simulatorData32 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date33 = simulatorData32.getCurrentTime();
    int int34 = opssat.simulator.util.DateExtraction.getDayFromDate(date33);
    opssat.simulator.util.SimulatorData simulatorData36 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date37 = simulatorData36.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData39 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date40 = simulatorData39.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap41 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date37, date40);
    opssat.simulator.util.SimulatorHeader simulatorHeader42 =
        new opssat.simulator.util.SimulatorHeader(
            false, date33, date40);
    opssat.simulator.util.SimulatorHeader simulatorHeader43 =
        new opssat.simulator.util.SimulatorHeader(
            false, date28, date33);
    java.lang.Long[] longArray50 = new java.lang.Long[]{281475010265070L, 100L, 0L,
      281475010265070L, 281475010265070L, 1L};
    java.util.ArrayList<java.lang.Long> longList51 = new java.util.ArrayList<java.lang.Long>();
    boolean boolean52 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Long>) longList51, longArray50);
    boolean boolean53 = longList51.isEmpty();
    java.lang.Object obj54 = longList51.clone();
    opssat.simulator.util.SimulatorData simulatorData57 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date58 = simulatorData57.getCurrentTime();
    int int59 = opssat.simulator.util.DateExtraction.getDayFromDate(date58);
    opssat.simulator.util.SimulatorData simulatorData61 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date62 = simulatorData61.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData64 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date65 = simulatorData64.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap66 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date62, date65);
    opssat.simulator.util.SimulatorHeader simulatorHeader67 =
        new opssat.simulator.util.SimulatorHeader(
            false, date58, date65);
    boolean boolean68 = longList51.remove(date65);
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap69 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date33, date65);
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap70 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date15, date65);
    int int71 = opssat.simulator.util.DateExtraction.getHourFromDate(date15);
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date15);
    org.junit.Assert.assertNotNull(date18);
    org.junit.Assert.assertNotNull(timeUnitMap19);
    org.junit.Assert.assertNotNull(timeUnitMap21);
    org.junit.Assert.assertNotNull(date27);
    org.junit.Assert.assertNotNull(date28);
    org.junit.Assert.assertNotNull(date33);
    org.junit.Assert.assertNotNull(date37);
    org.junit.Assert.assertNotNull(date40);
    org.junit.Assert.assertNotNull(timeUnitMap41);
    org.junit.Assert.assertNotNull(longArray50);
    org.junit.Assert.assertTrue("'" + boolean52 + "' != '" + true + "'", boolean52);
    org.junit.Assert.assertTrue("'" + boolean53 + "' != '" + false + "'", !boolean53);
    org.junit.Assert.assertNotNull(obj54);
    org.junit.Assert.assertNotNull(date58);
    org.junit.Assert.assertNotNull(date62);
    org.junit.Assert.assertNotNull(date65);
    org.junit.Assert.assertNotNull(timeUnitMap66);
    org.junit.Assert.assertTrue("'" + boolean68 + "' != '" + false + "'", !boolean68);
    org.junit.Assert.assertNotNull(timeUnitMap69);
    org.junit.Assert.assertNotNull(timeUnitMap70);
  }

  @Test
  public void test1309() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1309");
    }
    org.ccsds.moims.mo.mal.structures.URIList uRIList1 =
        new org.ccsds.moims.mo.mal.structures.URIList(
            8);
    java.lang.Integer int2 = uRIList1.getTypeShortForm();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState6 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState6.setLongitude(4);
    int int9 = simulatorSpacecraftState6.getSatsInView();
    float[] floatArray10 = simulatorSpacecraftState6.getRv();
    boolean boolean11 = uRIList1.remove(floatArray10);
    boolean boolean12 = uRIList1.isEmpty();
    org.junit.Assert.assertTrue("'" + int2 + "' != '" + (-18) + "'", int2.equals((-18)));
    org.junit.Assert.assertTrue("'" + int9 + "' != '" + 0 + "'", int9 == 0);
    org.junit.Assert.assertNotNull(floatArray10);
    org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + false + "'", !boolean11);
    org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + true + "'", boolean12);
  }

  @Test
  public void test1310() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1310");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState11 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double12 = simulatorSpacecraftState11.getLatitude();
    java.lang.String str13 = simulatorSpacecraftState11.getMagField();
    float[] floatArray14 = simulatorSpacecraftState11.getR();
    simulatorSpacecraftState3.setQ(floatArray14);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState19 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray22 = new float[]{28, 8};
    simulatorSpacecraftState19.setQ(floatArray22);
    float[] floatArray24 = simulatorSpacecraftState19.getQ();
    opssat.simulator.celestia.CelestiaData celestiaData25 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray14, floatArray24);
    celestiaData25.setInfo("03500.0000");
    celestiaData25.setInfo("yyyy/MM/dd-HH:mm:ss");
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + 340.0d + "'", double12 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str13 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str13.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray14);
    org.junit.Assert.assertNotNull(floatArray22);
    org.junit.Assert.assertNotNull(floatArray24);
  }

  @Test
  public void test1311() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1311");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    int int8 = simulatorSpacecraftState3.getSatsInView();
    simulatorSpacecraftState3.setAltitude((-5));
    float[] floatArray11 = simulatorSpacecraftState3.getV();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState15 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double16 = simulatorSpacecraftState15.getLatitude();
    java.lang.String str17 = simulatorSpacecraftState15.getMagField();
    java.lang.String str18 = simulatorSpacecraftState15.getSunVectorAsString();
    simulatorSpacecraftState15.setSatsInView(13);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState24 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double25 = simulatorSpacecraftState24.getLatitude();
    java.lang.String str26 = simulatorSpacecraftState24.getMagField();
    float[] floatArray27 = simulatorSpacecraftState24.getR();
    simulatorSpacecraftState15.setRv(floatArray27);
    simulatorSpacecraftState3.setQ(floatArray27);
    java.lang.String str30 = simulatorSpacecraftState3.getMagField();
    double double31 = simulatorSpacecraftState3.getLatitude();
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + int8 + "' != '" + 0 + "'", int8 == 0);
    org.junit.Assert.assertNotNull(floatArray11);
    org.junit.Assert.assertTrue("'" + double16 + "' != '" + 340.0d + "'", double16 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str17 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str17.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str18 + "' != '" + "X:[+.00000] ; Y:[+.00000] ; Z: [+.00000] units [N/A]" + "'",
        str18.equals("X:[+.00000] ; Y:[+.00000] ; Z: [+.00000] units [N/A]"));
    org.junit.Assert.assertTrue("'" + double25 + "' != '" + 340.0d + "'", double25 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str26 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str26.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray27);
    org.junit.Assert.assertTrue(
        "'" + str30 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str30.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue("'" + double31 + "' != '" + 340.0d + "'", double31 == 340.0d);
  }

  @Test
  public void test1312() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1312");
    }
    java.lang.Float[] floatArray3 = new java.lang.Float[]{1.0f, 1.0f, (-1.0f)};
    java.util.ArrayList<java.lang.Float> floatList4 = new java.util.ArrayList<java.lang.Float>();
    boolean boolean5 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Float>) floatList4, floatArray3);
    boolean boolean7 = floatList4.add((-1.0f));
    floatList4.clear();
    java.util.stream.Stream<java.lang.Float> floatStream9 = floatList4.stream();
    java.lang.Byte[] byteArray14 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList15 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean16 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList15, byteArray14);
    java.lang.Integer[] intArray19 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList20 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean21 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList20, intArray19);
    boolean boolean22 = byteList15.retainAll(intList20);
    java.lang.Integer[] intArray25 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList26 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean27 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList26, intArray25);
    int int29 = intList26.lastIndexOf((byte) 10);
    boolean boolean30 = intList20.retainAll(intList26);
    boolean boolean31 = floatList4.containsAll(intList26);
    java.lang.Long[] longArray35 = new java.lang.Long[]{13L, 281475010265070L, 1L};
    java.util.ArrayList<java.lang.Long> longList36 = new java.util.ArrayList<java.lang.Long>();
    boolean boolean37 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Long>) longList36, longArray35);
    boolean boolean39 = longList36.add(281474993487885L);
    boolean boolean40 = longList36.isEmpty();
    opssat.simulator.util.SimulatorData simulatorData42 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date43 = simulatorData42.getCurrentTime();
    java.util.Date date44 = simulatorData42.getCurrentTime();
    int int45 = simulatorData42.getTimeFactor();
    int int46 = longList36.lastIndexOf(int45);
    boolean boolean48 = longList36.equals("[0, 10]");
    int int49 = floatList4.lastIndexOf(boolean48);
    java.util.stream.Stream<java.lang.Float> floatStream50 = floatList4.parallelStream();
    org.junit.Assert.assertNotNull(floatArray3);
    org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
    org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + true + "'", boolean7);
    org.junit.Assert.assertNotNull(floatStream9);
    org.junit.Assert.assertNotNull(byteArray14);
    org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + true + "'", boolean16);
    org.junit.Assert.assertNotNull(intArray19);
    org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + true + "'", boolean21);
    org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
    org.junit.Assert.assertNotNull(intArray25);
    org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + true + "'", boolean27);
    org.junit.Assert.assertTrue("'" + int29 + "' != '" + (-1) + "'", int29 == (-1));
    org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + true + "'", boolean30);
    org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + false + "'", !boolean31);
    org.junit.Assert.assertNotNull(longArray35);
    org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + true + "'", boolean37);
    org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + true + "'", boolean39);
    org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + false + "'", !boolean40);
    org.junit.Assert.assertNotNull(date43);
    org.junit.Assert.assertNotNull(date44);
    org.junit.Assert.assertTrue("'" + int45 + "' != '" + 1 + "'", int45 == 1);
    org.junit.Assert.assertTrue("'" + int46 + "' != '" + (-1) + "'", int46 == (-1));
    org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + false + "'", !boolean48);
    org.junit.Assert.assertTrue("'" + int49 + "' != '" + (-1) + "'", int49 == (-1));
    org.junit.Assert.assertNotNull(floatStream50);
  }

  @Test
  public void test1313() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1313");
    }
    opssat.simulator.util.ArgumentTemplate argumentTemplate2 =
        new opssat.simulator.util.ArgumentTemplate(
            "*76", "13");
    java.lang.String str3 = argumentTemplate2.toString();
    java.lang.String str4 = argumentTemplate2.getArgContent();
    java.lang.String str5 = argumentTemplate2.getDescription();
    argumentTemplate2.setArgContent(
        "#Run the processing of internal models\nstartModels=false\n#Increment the simulated time (depends on startModels)\nstartTime=false\n#Speed up of time factor\ntimeFactor=31\n#Kepler elements for orbit A[km];E;i[deg];RAAN[deg];ARG_PER[deg];TRUE_A[deg] 7021.0;0.0;98.05;340.0;0.0;0.0\nkeplerElements=7021.0;0.0;98.05;340.0;0.0;0.0\n#Enable the Orekit library\norekit=false\n#Enable updates from Internet (used for gps constellation TLEs)\nupdateFromInternet=false\n#Configuration of the Celestia server\ncelestia=false\ncelestiaPort=0\n#Start and end dates of simulation\nstartDate=2019:05:23 15:10:51 UTC\nendDate=2019:05:23 15:10:51 UTC\n#Logging level to files found in $USER_HOME/.ops-sat-simulator/\n#Possible values SEVERE,INFO,FINE,FINER,FINEST,ALL\ncentralLogLevel=INFO\nsimulatorLogLevel=INFO\nconsoleLogLevel=INFO");
    org.junit.Assert.assertTrue("'" + str3 + "' != '" + "*76|13" + "'", str3.equals("*76|13"));
    org.junit.Assert.assertTrue("'" + str4 + "' != '" + "13" + "'", str4.equals("13"));
    org.junit.Assert.assertTrue("'" + str5 + "' != '" + "*76" + "'", str5.equals("*76"));
  }

  @Test
  public void test1314() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1314");
    }
    org.ccsds.moims.mo.mal.structures.Time time1 = new org.ccsds.moims.mo.mal.structures.Time('4');
    long long2 = time1.getValue();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet3 = time1.getAreaVersion();
    long long4 = time1.getValue();
    org.ccsds.moims.mo.mal.structures.UShort uShort5 = time1.getServiceNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet6 = time1.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.OctetList octetList7 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int8 = octetList7.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort9 = octetList7.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet10 = octetList7.getAreaVersion();
    java.util.stream.Stream<java.lang.Byte> byteStream11 = octetList7.parallelStream();
    org.ccsds.moims.mo.mal.structures.UShort uShort12 = octetList7.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.Element element13 = octetList7.createElement();
    boolean boolean14 = time1.equals(octetList7);
    org.junit.Assert.assertTrue("'" + long2 + "' != '" + 52L + "'", long2 == 52L);
    org.junit.Assert.assertNotNull(uOctet3);
    org.junit.Assert.assertTrue("'" + long4 + "' != '" + 52L + "'", long4 == 52L);
    org.junit.Assert.assertNotNull(uShort5);
    org.junit.Assert.assertNotNull(uOctet6);
    org.junit.Assert.assertTrue("'" + int8 + "' != '" + (-7) + "'", int8.equals((-7)));
    org.junit.Assert.assertNotNull(uShort9);
    org.junit.Assert.assertNotNull(uOctet10);
    org.junit.Assert.assertNotNull(byteStream11);
    org.junit.Assert.assertNotNull(uShort12);
    org.junit.Assert.assertNotNull(element13);
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
  }

  @Test
  public void test1315() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1315");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    boolean boolean14 = simulatorHeader12.isAutoStartSystem();
    int int15 = simulatorHeader12.getDayStartDate();
    boolean boolean16 = simulatorHeader12.isUseCelestia();
    java.lang.String str17 = simulatorHeader12.toString();
    int int18 = simulatorHeader12.getYearStartDate();
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
    org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
  }

  @Test
  public void test1316() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1316");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    opssat.simulator.util.SimulatorData simulatorData17 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date18 = simulatorData17.getCurrentTime();
    java.util.Date date19 = simulatorData17.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData20 = new opssat.simulator.util.SimulatorData(
        17, date19);
    opssat.simulator.util.SimulatorData simulatorData23 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date24 = simulatorData23.getCurrentTime();
    int int25 = opssat.simulator.util.DateExtraction.getDayFromDate(date24);
    opssat.simulator.util.SimulatorData simulatorData27 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date28 = simulatorData27.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData30 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date31 = simulatorData30.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap32 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date28, date31);
    opssat.simulator.util.SimulatorHeader simulatorHeader33 =
        new opssat.simulator.util.SimulatorHeader(
            false, date24, date31);
    opssat.simulator.util.SimulatorHeader simulatorHeader34 =
        new opssat.simulator.util.SimulatorHeader(
            false, date19, date24);
    simulatorHeader12.setEndDate(date19);
    int int36 = simulatorHeader12.getHourStartDate();
    simulatorHeader12.setUseOrekitPropagator(false);
    boolean boolean39 = simulatorHeader12.isUseOrekitPropagator();
    simulatorHeader12.setOrekitTLE2(
        "{counter=-18, methodsExecuted=0, currentTime=Thu May 23 15:10:00 UTC 2019}");
    simulatorHeader12.setUpdateInternet(false);
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertNotNull(date18);
    org.junit.Assert.assertNotNull(date19);
    org.junit.Assert.assertNotNull(date24);
    org.junit.Assert.assertNotNull(date28);
    org.junit.Assert.assertNotNull(date31);
    org.junit.Assert.assertNotNull(timeUnitMap32);
    org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + false + "'", !boolean39);
  }

  @Test
  public void test1317() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1317");
    }
    opssat.simulator.util.SimulatorData simulatorData1 = new opssat.simulator.util.SimulatorData(
        38);
    boolean boolean2 = simulatorData1.isTimeRunning();
    java.lang.String str3 = simulatorData1.getUTCCurrentTime();
    simulatorData1.toggleTimeRunning();
    org.junit.Assert.assertTrue("'" + boolean2 + "' != '" + false + "'", !boolean2);
// flaky:         org.junit.Assert.assertTrue("'" + str3 + "' != '" + "031102.186" + "'", str3.equals("031102.186"));
  }

  @Test
  public void test1318() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1318");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    boolean boolean14 = simulatorHeader12.isAutoStartSystem();
    int int15 = simulatorHeader12.getDayStartDate();
    boolean boolean16 = simulatorHeader12.isUseCelestia();
    int int17 = simulatorHeader12.getDayStartDate();
    simulatorHeader12.setAutoStartTime(false);
    java.lang.String str20 = simulatorHeader12.getOrekitTLE1();
    simulatorHeader12.setTimeFactor(14);
    java.lang.String str23 = simulatorHeader12.DATE_FORMAT;
    java.lang.String str24 = simulatorHeader12.getStartDateString();
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
    org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
    org.junit.Assert.assertNull(str20);
    org.junit.Assert.assertTrue("'" + str23 + "' != '" + "yyyy:MM:dd HH:mm:ss z" + "'",
        str23.equals("yyyy:MM:dd HH:mm:ss z"));
  }

  @Test
  public void test1319() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1319");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    int int8 = simulatorSpacecraftState3.getSatsInView();
    simulatorSpacecraftState3.setAltitude((-5));
    float[] floatArray11 = simulatorSpacecraftState3.getV();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState15 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double16 = simulatorSpacecraftState15.getLatitude();
    java.lang.String str17 = simulatorSpacecraftState15.getMagField();
    java.lang.String str18 = simulatorSpacecraftState15.getSunVectorAsString();
    simulatorSpacecraftState15.setSatsInView(13);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState24 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double25 = simulatorSpacecraftState24.getLatitude();
    java.lang.String str26 = simulatorSpacecraftState24.getMagField();
    float[] floatArray27 = simulatorSpacecraftState24.getR();
    simulatorSpacecraftState15.setRv(floatArray27);
    simulatorSpacecraftState3.setQ(floatArray27);
    float[] floatArray30 = simulatorSpacecraftState3.getQ();
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + int8 + "' != '" + 0 + "'", int8 == 0);
    org.junit.Assert.assertNotNull(floatArray11);
    org.junit.Assert.assertTrue("'" + double16 + "' != '" + 340.0d + "'", double16 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str17 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str17.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str18 + "' != '" + "X:[+.00000] ; Y:[+.00000] ; Z: [+.00000] units [N/A]" + "'",
        str18.equals("X:[+.00000] ; Y:[+.00000] ; Z: [+.00000] units [N/A]"));
    org.junit.Assert.assertTrue("'" + double25 + "' != '" + 340.0d + "'", double25 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str26 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str26.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray27);
    org.junit.Assert.assertNotNull(floatArray30);
  }

  @Test
  public void test1320() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1320");
    }
    org.ccsds.moims.mo.mal.structures.StringList stringList0 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    org.ccsds.moims.mo.mal.structures.ShortList shortList2 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            ' ');
    java.util.ListIterator<java.lang.Short> shortItor3 = shortList2.listIterator();
    boolean boolean4 = stringList0.equals(shortList2);
    java.util.stream.BaseStream[] baseStreamArray6 = new java.util.stream.BaseStream[0];
    @SuppressWarnings("unchecked")
    java.util.stream.BaseStream<java.lang.Float, java.util.stream.Stream<java.lang.Float>>[] floatBaseStreamArray7 =
        baseStreamArray6;
    java.util.stream.BaseStream<java.lang.Float, java.util.stream.Stream<java.lang.Float>>[] floatBaseStreamArray8 =
        stringList0
            .toArray(
                (java.util.stream.BaseStream<java.lang.Float, java.util.stream.Stream<java.lang.Float>>[]) baseStreamArray6);
    java.util.stream.Stream<java.lang.String> strStream9 = stringList0.stream();
    java.lang.Object obj10 = stringList0.clone();
    opssat.simulator.util.LoggerFormatter loggerFormatter11 =
        new opssat.simulator.util.LoggerFormatter();
    opssat.simulator.util.LoggerFormatter loggerFormatter12 =
        new opssat.simulator.util.LoggerFormatter();
    opssat.simulator.util.LoggerFormatter loggerFormatter13 =
        new opssat.simulator.util.LoggerFormatter();
    opssat.simulator.util.LoggerFormatter[] loggerFormatterArray14 =
        new opssat.simulator.util.LoggerFormatter[]{
          loggerFormatter11, loggerFormatter12, loggerFormatter13};
    opssat.simulator.util.LoggerFormatter[] loggerFormatterArray15 = stringList0
        .toArray(loggerFormatterArray14);
    java.util.stream.Stream<java.lang.String> strStream16 = stringList0.parallelStream();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet17 = stringList0.getAreaVersion();
    java.lang.Long long18 = uOctet17.getShortForm();
    java.lang.Integer int19 = uOctet17.getTypeShortForm();
    org.junit.Assert.assertNotNull(shortItor3);
    org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
    org.junit.Assert.assertNotNull(baseStreamArray6);
    org.junit.Assert.assertNotNull(floatBaseStreamArray7);
    org.junit.Assert.assertNotNull(floatBaseStreamArray8);
    org.junit.Assert.assertNotNull(strStream9);
    org.junit.Assert.assertNotNull(obj10);
    org.junit.Assert.assertNotNull(loggerFormatterArray14);
    org.junit.Assert.assertNotNull(loggerFormatterArray15);
    org.junit.Assert.assertNotNull(strStream16);
    org.junit.Assert.assertNotNull(uOctet17);
    org.junit.Assert.assertTrue("'" + long18 + "' != '" + 281474993487880L + "'",
        long18.equals(281474993487880L));
    org.junit.Assert.assertTrue("'" + int19 + "' != '" + 8 + "'", int19.equals(8));
  }

  @Test
  public void test1321() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1321");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState11 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double12 = simulatorSpacecraftState11.getLatitude();
    java.lang.String str13 = simulatorSpacecraftState11.getMagField();
    float[] floatArray14 = simulatorSpacecraftState11.getR();
    simulatorSpacecraftState3.setQ(floatArray14);
    java.lang.String str16 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray14);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState20 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double21 = simulatorSpacecraftState20.getLatitude();
    double double22 = simulatorSpacecraftState20.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState26 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double27 = simulatorSpacecraftState26.getLatitude();
    java.lang.String str28 = simulatorSpacecraftState26.getMagField();
    float[] floatArray29 = simulatorSpacecraftState26.getR();
    simulatorSpacecraftState20.setQ(floatArray29);
    float[] floatArray31 = simulatorSpacecraftState20.getV();
    opssat.simulator.celestia.CelestiaData celestiaData32 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray14, floatArray31);
    celestiaData32.setDate(":");
    java.lang.String str35 = celestiaData32.getAnx();
    celestiaData32.setInfo("01000.0000");
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + 340.0d + "'", double12 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str13 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str13.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray14);
    org.junit.Assert.assertTrue("'" + str16 + "' != '" + "UnknownGUIData" + "'",
        str16.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double21 + "' != '" + 340.0d + "'", double21 == 340.0d);
    org.junit.Assert.assertTrue("'" + double22 + "' != '" + 340.0d + "'", double22 == 340.0d);
    org.junit.Assert.assertTrue("'" + double27 + "' != '" + 340.0d + "'", double27 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str28 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str28.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray29);
    org.junit.Assert.assertNotNull(floatArray31);
    org.junit.Assert.assertNull(str35);
  }

  @Test
  public void test1322() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1322");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    boolean boolean14 = simulatorHeader12.isAutoStartSystem();
    int int15 = simulatorHeader12.getDayStartDate();
    boolean boolean16 = simulatorHeader12.isUseCelestia();
    opssat.simulator.util.SimulatorData simulatorData19 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date20 = simulatorData19.getCurrentTime();
    int int21 = opssat.simulator.util.DateExtraction.getDayFromDate(date20);
    opssat.simulator.util.SimulatorData simulatorData23 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date24 = simulatorData23.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData26 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date27 = simulatorData26.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap28 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date24, date27);
    opssat.simulator.util.SimulatorHeader simulatorHeader29 =
        new opssat.simulator.util.SimulatorHeader(
            false, date20, date27);
    simulatorHeader12.setStartDate(date27);
    simulatorHeader12.setKeplerElements("-0100.0000000");
    java.lang.String str33 = simulatorHeader12.toString();
    boolean boolean34 = simulatorHeader12.checkStartBeforeEnd();
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
    org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
    org.junit.Assert.assertNotNull(date20);
    org.junit.Assert.assertNotNull(date24);
    org.junit.Assert.assertNotNull(date27);
    org.junit.Assert.assertNotNull(timeUnitMap28);
  }

  @Test
  public void test1323() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1323");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double4 = simulatorSpacecraftState3.getLatitude();
    java.lang.String str5 = simulatorSpacecraftState3.getMagField();
    simulatorSpacecraftState3.setLatitude((-4));
    java.lang.String str8 = simulatorSpacecraftState3.getMagField();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState12 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState12.setLongitude(4);
    double[] doubleArray16 = new double[]{(-1.0f)};
    simulatorSpacecraftState12.setMagField(doubleArray16);
    double double18 = simulatorSpacecraftState12.getLongitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState22 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray25 = new float[]{28, 8};
    simulatorSpacecraftState22.setQ(floatArray25);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState30 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double31 = simulatorSpacecraftState30.getLatitude();
    java.lang.String str32 = simulatorSpacecraftState30.getMagField();
    float[] floatArray33 = simulatorSpacecraftState30.getR();
    simulatorSpacecraftState22.setQ(floatArray33);
    java.lang.String str35 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray33);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState39 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double40 = simulatorSpacecraftState39.getLatitude();
    double double41 = simulatorSpacecraftState39.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState45 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double46 = simulatorSpacecraftState45.getLatitude();
    java.lang.String str47 = simulatorSpacecraftState45.getMagField();
    float[] floatArray48 = simulatorSpacecraftState45.getR();
    simulatorSpacecraftState39.setQ(floatArray48);
    float[] floatArray50 = simulatorSpacecraftState39.getV();
    opssat.simulator.celestia.CelestiaData celestiaData51 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray33, floatArray50);
    float[] floatArray52 = celestiaData51.getQ();
    opssat.simulator.util.SimulatorData simulatorData56 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date57 = simulatorData56.getCurrentTime();
    int int58 = opssat.simulator.util.DateExtraction.getDayFromDate(date57);
    opssat.simulator.util.SimulatorData simulatorData60 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date61 = simulatorData60.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData63 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date64 = simulatorData63.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap65 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date61, date64);
    opssat.simulator.util.SimulatorHeader simulatorHeader66 =
        new opssat.simulator.util.SimulatorHeader(
            false, date57, date64);
    opssat.simulator.util.SimulatorData simulatorData67 = new opssat.simulator.util.SimulatorData(
        (short) 0, date57);
    celestiaData51.setDate(date57);
    int int69 = celestiaData51.getSeconds();
    celestiaData51.setDnx("OPS-SAT SoftSim:");
    java.lang.String str72 = celestiaData51.getAos();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState76 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double77 = simulatorSpacecraftState76.getLatitude();
    double double78 = simulatorSpacecraftState76.getLongitude();
    float[] floatArray79 = simulatorSpacecraftState76.getQ();
    celestiaData51.setQ(floatArray79);
    simulatorSpacecraftState12.setQ(floatArray79);
    simulatorSpacecraftState3.setRv(floatArray79);
    float[] floatArray83 = simulatorSpacecraftState3.getMagnetometer();
    double double84 = simulatorSpacecraftState3.getLongitude();
    double[] doubleArray85 = simulatorSpacecraftState3.getSunVector();
    simulatorSpacecraftState3.setAltitude(30);
    org.junit.Assert.assertTrue("'" + double4 + "' != '" + 340.0d + "'", double4 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str5 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str5.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str8 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str8.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(doubleArray16);
    org.junit.Assert.assertTrue("'" + double18 + "' != '" + 4.0d + "'", double18 == 4.0d);
    org.junit.Assert.assertNotNull(floatArray25);
    org.junit.Assert.assertTrue("'" + double31 + "' != '" + 340.0d + "'", double31 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str32 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str32.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray33);
    org.junit.Assert.assertTrue("'" + str35 + "' != '" + "UnknownGUIData" + "'",
        str35.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double40 + "' != '" + 340.0d + "'", double40 == 340.0d);
    org.junit.Assert.assertTrue("'" + double41 + "' != '" + 340.0d + "'", double41 == 340.0d);
    org.junit.Assert.assertTrue("'" + double46 + "' != '" + 340.0d + "'", double46 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str47 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str47.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray48);
    org.junit.Assert.assertNotNull(floatArray50);
    org.junit.Assert.assertNotNull(floatArray52);
    org.junit.Assert.assertNotNull(date57);
    org.junit.Assert.assertNotNull(date61);
    org.junit.Assert.assertNotNull(date64);
    org.junit.Assert.assertNotNull(timeUnitMap65);
    org.junit.Assert.assertNull(str72);
    org.junit.Assert.assertTrue("'" + double77 + "' != '" + 340.0d + "'", double77 == 340.0d);
    org.junit.Assert.assertTrue("'" + double78 + "' != '" + (-1.0d) + "'", double78 == (-1.0d));
    org.junit.Assert.assertNotNull(floatArray79);
    org.junit.Assert.assertNotNull(floatArray83);
    org.junit.Assert.assertTrue("'" + double84 + "' != '" + (-1.0d) + "'", double84 == (-1.0d));
    org.junit.Assert.assertNotNull(doubleArray85);
  }

  @Test
  public void test1324() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1324");
    }
    opssat.simulator.threading.SimulatorNode simulatorNode0 = null;
    opssat.simulator.peripherals.PCamera pCamera2 = new opssat.simulator.peripherals.PCamera(
        simulatorNode0, "031035.100");
    try {
      byte[] byteArray5 = pCamera2.takePicture(10, (byte) -1);
      org.junit.Assert
          .fail("Expected exception of type java.lang.NullPointerException; message: null");
    } catch (java.lang.NullPointerException e) {
    }
  }

  @Test
  public void test1326() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1326");
    }
    org.ccsds.moims.mo.mal.structures.URIList uRIList1 =
        new org.ccsds.moims.mo.mal.structures.URIList(
            11);
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = uRIList1.getServiceNumber();
    java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.URI> uRIStream3 = uRIList1.stream();
    org.ccsds.moims.mo.mal.structures.Duration duration5 =
        new org.ccsds.moims.mo.mal.structures.Duration(
            0.0d);
    org.ccsds.moims.mo.mal.structures.UOctet uOctet6 = duration5.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.UShort uShort7 = duration5.getAreaNumber();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState11 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray14 = new float[]{28, 8};
    simulatorSpacecraftState11.setQ(floatArray14);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState19 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double20 = simulatorSpacecraftState19.getLatitude();
    java.lang.String str21 = simulatorSpacecraftState19.getMagField();
    float[] floatArray22 = simulatorSpacecraftState19.getR();
    simulatorSpacecraftState11.setQ(floatArray22);
    java.lang.String str24 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray22);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState28 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double29 = simulatorSpacecraftState28.getLatitude();
    double double30 = simulatorSpacecraftState28.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState34 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double35 = simulatorSpacecraftState34.getLatitude();
    java.lang.String str36 = simulatorSpacecraftState34.getMagField();
    float[] floatArray37 = simulatorSpacecraftState34.getR();
    simulatorSpacecraftState28.setQ(floatArray37);
    float[] floatArray39 = simulatorSpacecraftState28.getV();
    opssat.simulator.celestia.CelestiaData celestiaData40 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray22, floatArray39);
    float[] floatArray41 = celestiaData40.getQ();
    opssat.simulator.util.SimulatorData simulatorData45 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date46 = simulatorData45.getCurrentTime();
    int int47 = opssat.simulator.util.DateExtraction.getDayFromDate(date46);
    opssat.simulator.util.SimulatorData simulatorData49 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date50 = simulatorData49.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData52 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date53 = simulatorData52.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap54 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date50, date53);
    opssat.simulator.util.SimulatorHeader simulatorHeader55 =
        new opssat.simulator.util.SimulatorHeader(
            false, date46, date53);
    opssat.simulator.util.SimulatorData simulatorData56 = new opssat.simulator.util.SimulatorData(
        (short) 0, date46);
    celestiaData40.setDate(date46);
    boolean boolean58 = duration5.equals(date46);
    int int59 = uRIList1.lastIndexOf(duration5);
    java.lang.Integer int60 = uRIList1.getTypeShortForm();
    java.lang.Integer int61 = uRIList1.getTypeShortForm();
    java.lang.Integer int62 = uRIList1.getTypeShortForm();
    uRIList1.trimToSize();
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(uRIStream3);
    org.junit.Assert.assertNotNull(uOctet6);
    org.junit.Assert.assertNotNull(uShort7);
    org.junit.Assert.assertNotNull(floatArray14);
    org.junit.Assert.assertTrue("'" + double20 + "' != '" + 340.0d + "'", double20 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str21 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str21.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray22);
    org.junit.Assert.assertTrue("'" + str24 + "' != '" + "UnknownGUIData" + "'",
        str24.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double29 + "' != '" + 340.0d + "'", double29 == 340.0d);
    org.junit.Assert.assertTrue("'" + double30 + "' != '" + 340.0d + "'", double30 == 340.0d);
    org.junit.Assert.assertTrue("'" + double35 + "' != '" + 340.0d + "'", double35 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str36 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str36.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray37);
    org.junit.Assert.assertNotNull(floatArray39);
    org.junit.Assert.assertNotNull(floatArray41);
    org.junit.Assert.assertNotNull(date46);
    org.junit.Assert.assertNotNull(date50);
    org.junit.Assert.assertNotNull(date53);
    org.junit.Assert.assertNotNull(timeUnitMap54);
    org.junit.Assert.assertTrue("'" + boolean58 + "' != '" + false + "'", !boolean58);
    org.junit.Assert.assertTrue("'" + int59 + "' != '" + (-1) + "'", int59 == (-1));
    org.junit.Assert.assertTrue("'" + int60 + "' != '" + (-18) + "'", int60.equals((-18)));
    org.junit.Assert.assertTrue("'" + int61 + "' != '" + (-18) + "'", int61.equals((-18)));
    org.junit.Assert.assertTrue("'" + int62 + "' != '" + (-18) + "'", int62.equals((-18)));
  }

  @Test
  public void test1328() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1328");
    }
    java.lang.Long[] longArray6 = new java.lang.Long[]{281475010265070L, 100L, 0L,
      281475010265070L, 281475010265070L, 1L};
    java.util.ArrayList<java.lang.Long> longList7 = new java.util.ArrayList<java.lang.Long>();
    boolean boolean8 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Long>) longList7, longArray6);
    boolean boolean9 = longList7.isEmpty();
    java.lang.Long[] longArray12 = new java.lang.Long[]{0L, 10L};
    java.util.ArrayList<java.lang.Long> longList13 = new java.util.ArrayList<java.lang.Long>();
    boolean boolean14 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Long>) longList13, longArray12);
    java.lang.Object obj15 = longList13.clone();
    java.util.ListIterator<java.lang.Long> longItor17 = longList13.listIterator((byte) 1);
    boolean boolean18 = longList7.remove((java.lang.Object) (byte) 1);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState22 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double23 = simulatorSpacecraftState22.getLatitude();
    double double24 = simulatorSpacecraftState22.getLongitude();
    java.lang.String str25 = simulatorSpacecraftState22.getModeOperation();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState29 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState29.setLongitude(4);
    double[] doubleArray33 = new double[]{(-1.0f)};
    simulatorSpacecraftState29.setMagField(doubleArray33);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState38 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double39 = simulatorSpacecraftState38.getLatitude();
    java.lang.String str40 = simulatorSpacecraftState38.getMagField();
    java.lang.String str41 = simulatorSpacecraftState38.toString();
    double[] doubleArray42 = simulatorSpacecraftState38.getSunVector();
    simulatorSpacecraftState29.setMagnetometer(doubleArray42);
    simulatorSpacecraftState22.setMagField(doubleArray42);
    simulatorSpacecraftState22.setModeOperation("0");
    simulatorSpacecraftState22.setModeOperation("030936.762");
    simulatorSpacecraftState22.setAltitude(60);
    int int51 = longList7.lastIndexOf(60);
    java.lang.String str52 = longList7.toString();
    org.junit.Assert.assertNotNull(longArray6);
    org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + true + "'", boolean8);
    org.junit.Assert.assertTrue("'" + boolean9 + "' != '" + false + "'", !boolean9);
    org.junit.Assert.assertNotNull(longArray12);
    org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + true + "'", boolean14);
    org.junit.Assert.assertNotNull(obj15);
    org.junit.Assert.assertNotNull(longItor17);
    org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + false + "'", !boolean18);
    org.junit.Assert.assertTrue("'" + double23 + "' != '" + 340.0d + "'", double23 == 340.0d);
    org.junit.Assert.assertTrue("'" + double24 + "' != '" + (-1.0d) + "'", double24 == (-1.0d));
    org.junit.Assert.assertNull(str25);
    org.junit.Assert.assertNotNull(doubleArray33);
    org.junit.Assert.assertTrue("'" + double39 + "' != '" + 340.0d + "'", double39 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str40 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str40.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str41 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str41.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray42);
    org.junit.Assert.assertTrue("'" + int51 + "' != '" + (-1) + "'", int51 == (-1));
    org.junit.Assert
        .assertTrue(
            "'" + str52 + "' != '"
            + "[281475010265070, 100, 0, 281475010265070, 281475010265070, 1]" + "'",
            str52.equals("[281475010265070, 100, 0, 281475010265070, 281475010265070, 1]"));
  }

  @Test
  public void test1329() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1329");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    opssat.simulator.util.SimulatorData simulatorData17 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date18 = simulatorData17.getCurrentTime();
    java.util.Date date19 = simulatorData17.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData20 = new opssat.simulator.util.SimulatorData(
        17, date19);
    opssat.simulator.util.SimulatorData simulatorData23 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date24 = simulatorData23.getCurrentTime();
    int int25 = opssat.simulator.util.DateExtraction.getDayFromDate(date24);
    opssat.simulator.util.SimulatorData simulatorData27 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date28 = simulatorData27.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData30 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date31 = simulatorData30.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap32 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date28, date31);
    opssat.simulator.util.SimulatorHeader simulatorHeader33 =
        new opssat.simulator.util.SimulatorHeader(
            false, date24, date31);
    opssat.simulator.util.SimulatorHeader simulatorHeader34 =
        new opssat.simulator.util.SimulatorHeader(
            false, date19, date24);
    simulatorHeader12.setEndDate(date19);
    int int36 = simulatorHeader12.getHourStartDate();
    java.lang.String str37 = simulatorHeader12.getOrekitTLE2();
    simulatorHeader12.setOrekitTLE2("*0B");
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertNotNull(date18);
    org.junit.Assert.assertNotNull(date19);
    org.junit.Assert.assertNotNull(date24);
    org.junit.Assert.assertNotNull(date28);
    org.junit.Assert.assertNotNull(date31);
    org.junit.Assert.assertNotNull(timeUnitMap32);
    org.junit.Assert.assertNull(str37);
  }

  @Test
  public void test1331() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1331");
    }
    org.ccsds.moims.mo.mal.structures.URIList uRIList1 =
        new org.ccsds.moims.mo.mal.structures.URIList(
            11);
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = uRIList1.getServiceNumber();
    java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.URI> uRIStream3 = uRIList1.stream();
    org.ccsds.moims.mo.mal.structures.Duration duration5 =
        new org.ccsds.moims.mo.mal.structures.Duration(
            0.0d);
    org.ccsds.moims.mo.mal.structures.UOctet uOctet6 = duration5.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.UShort uShort7 = duration5.getAreaNumber();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState11 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray14 = new float[]{28, 8};
    simulatorSpacecraftState11.setQ(floatArray14);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState19 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double20 = simulatorSpacecraftState19.getLatitude();
    java.lang.String str21 = simulatorSpacecraftState19.getMagField();
    float[] floatArray22 = simulatorSpacecraftState19.getR();
    simulatorSpacecraftState11.setQ(floatArray22);
    java.lang.String str24 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray22);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState28 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double29 = simulatorSpacecraftState28.getLatitude();
    double double30 = simulatorSpacecraftState28.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState34 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double35 = simulatorSpacecraftState34.getLatitude();
    java.lang.String str36 = simulatorSpacecraftState34.getMagField();
    float[] floatArray37 = simulatorSpacecraftState34.getR();
    simulatorSpacecraftState28.setQ(floatArray37);
    float[] floatArray39 = simulatorSpacecraftState28.getV();
    opssat.simulator.celestia.CelestiaData celestiaData40 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray22, floatArray39);
    float[] floatArray41 = celestiaData40.getQ();
    opssat.simulator.util.SimulatorData simulatorData45 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date46 = simulatorData45.getCurrentTime();
    int int47 = opssat.simulator.util.DateExtraction.getDayFromDate(date46);
    opssat.simulator.util.SimulatorData simulatorData49 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date50 = simulatorData49.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData52 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date53 = simulatorData52.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap54 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date50, date53);
    opssat.simulator.util.SimulatorHeader simulatorHeader55 =
        new opssat.simulator.util.SimulatorHeader(
            false, date46, date53);
    opssat.simulator.util.SimulatorData simulatorData56 = new opssat.simulator.util.SimulatorData(
        (short) 0, date46);
    celestiaData40.setDate(date46);
    boolean boolean58 = duration5.equals(date46);
    int int59 = uRIList1.lastIndexOf(duration5);
    java.lang.Integer int60 = uRIList1.getTypeShortForm();
    java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.URI> uRIStream61 = uRIList1
        .parallelStream();
    java.lang.Float[] floatArray65 = new java.lang.Float[]{1.0f, 1.0f, (-1.0f)};
    java.util.ArrayList<java.lang.Float> floatList66 = new java.util.ArrayList<java.lang.Float>();
    boolean boolean67 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Float>) floatList66, floatArray65);
    boolean boolean69 = floatList66.add((-1.0f));
    floatList66.clear();
    java.util.stream.Stream<java.lang.Float> floatStream71 = floatList66.stream();
    org.ccsds.moims.mo.mal.structures.StringList stringList72 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    opssat.simulator.threading.SimulatorNode.DevDatPBind devDatPBind73 =
        opssat.simulator.threading.SimulatorNode.DevDatPBind.FineADCS_ReactionWheels;
    boolean boolean74 = stringList72.equals(devDatPBind73);
    org.ccsds.moims.mo.mal.structures.Element element75 = stringList72.createElement();
    java.util.Iterator<java.lang.String> strItor76 = stringList72.iterator();
    boolean boolean77 = floatList66.contains(stringList72);
    java.util.stream.Stream<java.lang.String> strStream78 = stringList72.stream();
    org.ccsds.moims.mo.mal.structures.FineTime fineTime79 =
        new org.ccsds.moims.mo.mal.structures.FineTime();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet80 = fineTime79.getAreaVersion();
    int int81 = stringList72.lastIndexOf(fineTime79);
    java.lang.Long long82 = fineTime79.getShortForm();
    int int83 = uRIList1.lastIndexOf(long82);
    java.lang.Integer int84 = uRIList1.getTypeShortForm();
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(uRIStream3);
    org.junit.Assert.assertNotNull(uOctet6);
    org.junit.Assert.assertNotNull(uShort7);
    org.junit.Assert.assertNotNull(floatArray14);
    org.junit.Assert.assertTrue("'" + double20 + "' != '" + 340.0d + "'", double20 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str21 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str21.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray22);
    org.junit.Assert.assertTrue("'" + str24 + "' != '" + "UnknownGUIData" + "'",
        str24.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double29 + "' != '" + 340.0d + "'", double29 == 340.0d);
    org.junit.Assert.assertTrue("'" + double30 + "' != '" + 340.0d + "'", double30 == 340.0d);
    org.junit.Assert.assertTrue("'" + double35 + "' != '" + 340.0d + "'", double35 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str36 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str36.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray37);
    org.junit.Assert.assertNotNull(floatArray39);
    org.junit.Assert.assertNotNull(floatArray41);
    org.junit.Assert.assertNotNull(date46);
    org.junit.Assert.assertNotNull(date50);
    org.junit.Assert.assertNotNull(date53);
    org.junit.Assert.assertNotNull(timeUnitMap54);
    org.junit.Assert.assertTrue("'" + boolean58 + "' != '" + false + "'", !boolean58);
    org.junit.Assert.assertTrue("'" + int59 + "' != '" + (-1) + "'", int59 == (-1));
    org.junit.Assert.assertTrue("'" + int60 + "' != '" + (-18) + "'", int60.equals((-18)));
    org.junit.Assert.assertNotNull(uRIStream61);
    org.junit.Assert.assertNotNull(floatArray65);
    org.junit.Assert.assertTrue("'" + boolean67 + "' != '" + true + "'", boolean67);
    org.junit.Assert.assertTrue("'" + boolean69 + "' != '" + true + "'", boolean69);
    org.junit.Assert.assertNotNull(floatStream71);
    org.junit.Assert.assertTrue(
        "'" + devDatPBind73 + "' != '"
        + opssat.simulator.threading.SimulatorNode.DevDatPBind.FineADCS_ReactionWheels + "'",
        devDatPBind73
            .equals(opssat.simulator.threading.SimulatorNode.DevDatPBind.FineADCS_ReactionWheels));
    org.junit.Assert.assertTrue("'" + boolean74 + "' != '" + false + "'", !boolean74);
    org.junit.Assert.assertNotNull(element75);
    org.junit.Assert.assertNotNull(strItor76);
    org.junit.Assert.assertTrue("'" + boolean77 + "' != '" + false + "'", !boolean77);
    org.junit.Assert.assertNotNull(strStream78);
    org.junit.Assert.assertNotNull(uOctet80);
    org.junit.Assert.assertTrue("'" + int81 + "' != '" + (-1) + "'", int81 == (-1));
    org.junit.Assert.assertTrue("'" + long82 + "' != '" + 281474993487889L + "'",
        long82.equals(281474993487889L));
    org.junit.Assert.assertTrue("'" + int83 + "' != '" + (-1) + "'", int83 == (-1));
    org.junit.Assert.assertTrue("'" + int84 + "' != '" + (-18) + "'", int84.equals((-18)));
  }

  @Test
  public void test1332() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1332");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState11 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double12 = simulatorSpacecraftState11.getLatitude();
    java.lang.String str13 = simulatorSpacecraftState11.getMagField();
    float[] floatArray14 = simulatorSpacecraftState11.getR();
    simulatorSpacecraftState3.setQ(floatArray14);
    java.lang.String str16 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray14);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState20 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double21 = simulatorSpacecraftState20.getLatitude();
    double double22 = simulatorSpacecraftState20.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState26 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double27 = simulatorSpacecraftState26.getLatitude();
    java.lang.String str28 = simulatorSpacecraftState26.getMagField();
    float[] floatArray29 = simulatorSpacecraftState26.getR();
    simulatorSpacecraftState20.setQ(floatArray29);
    float[] floatArray31 = simulatorSpacecraftState20.getV();
    opssat.simulator.celestia.CelestiaData celestiaData32 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray14, floatArray31);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState36 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double37 = simulatorSpacecraftState36.getLatitude();
    double double38 = simulatorSpacecraftState36.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState42 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double43 = simulatorSpacecraftState42.getLatitude();
    java.lang.String str44 = simulatorSpacecraftState42.getMagField();
    float[] floatArray45 = simulatorSpacecraftState42.getR();
    simulatorSpacecraftState36.setQ(floatArray45);
    celestiaData32.setQ(floatArray45);
    opssat.simulator.util.SimulatorData simulatorData50 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date51 = simulatorData50.getCurrentTime();
    int int52 = opssat.simulator.util.DateExtraction.getDayFromDate(date51);
    opssat.simulator.util.SimulatorData simulatorData54 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date55 = simulatorData54.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData57 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date58 = simulatorData57.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap59 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date55, date58);
    opssat.simulator.util.SimulatorHeader simulatorHeader60 =
        new opssat.simulator.util.SimulatorHeader(
            false, date51, date58);
    java.util.Date date61 = simulatorHeader60.getEndDate();
    simulatorHeader60.setOrekitTLE1("");
    java.util.Date date65 = simulatorHeader60.parseStringIntoDate("2019/05/23-15:09:35");
    java.lang.String str66 = simulatorHeader60.toFileString();
    java.util.Date date67 = simulatorHeader60.getEndDate();
    celestiaData32.setDate(date67);
    java.lang.String str69 = celestiaData32.getInfo();
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertTrue("'" + double12 + "' != '" + 340.0d + "'", double12 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str13 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str13.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray14);
    org.junit.Assert.assertTrue("'" + str16 + "' != '" + "UnknownGUIData" + "'",
        str16.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double21 + "' != '" + 340.0d + "'", double21 == 340.0d);
    org.junit.Assert.assertTrue("'" + double22 + "' != '" + 340.0d + "'", double22 == 340.0d);
    org.junit.Assert.assertTrue("'" + double27 + "' != '" + 340.0d + "'", double27 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str28 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str28.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray29);
    org.junit.Assert.assertNotNull(floatArray31);
    org.junit.Assert.assertTrue("'" + double37 + "' != '" + 340.0d + "'", double37 == 340.0d);
    org.junit.Assert.assertTrue("'" + double38 + "' != '" + 340.0d + "'", double38 == 340.0d);
    org.junit.Assert.assertTrue("'" + double43 + "' != '" + 340.0d + "'", double43 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str44 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str44.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray45);
    org.junit.Assert.assertNotNull(date51);
    org.junit.Assert.assertNotNull(date55);
    org.junit.Assert.assertNotNull(date58);
    org.junit.Assert.assertNotNull(timeUnitMap59);
    org.junit.Assert.assertNotNull(date61);
    org.junit.Assert.assertNull(date65);
    org.junit.Assert.assertNotNull(date67);
    org.junit.Assert.assertNull(str69);
  }

  @Test
  public void test1333() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1333");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    float[] floatArray11 = new float[]{281474993487887L, 281475010265070L, 281474993487881L};
    simulatorSpacecraftState3.setRv(floatArray11);
    float[] floatArray13 = simulatorSpacecraftState3.getMagnetometer();
    int int14 = simulatorSpacecraftState3.getSatsInView();
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertNotNull(floatArray11);
    org.junit.Assert.assertNotNull(floatArray13);
    org.junit.Assert.assertTrue("'" + int14 + "' != '" + 0 + "'", int14 == 0);
  }

  @Test
  public void test1334() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1334");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.lang.String str13 = simulatorHeader12.getEndDateString();
    opssat.simulator.util.SimulatorData simulatorData16 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date17 = simulatorData16.getCurrentTime();
    int int18 = opssat.simulator.util.DateExtraction.getDayFromDate(date17);
    opssat.simulator.util.SimulatorData simulatorData20 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date21 = simulatorData20.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData23 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date24 = simulatorData23.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap25 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date21, date24);
    opssat.simulator.util.SimulatorHeader simulatorHeader26 =
        new opssat.simulator.util.SimulatorHeader(
            false, date17, date24);
    java.util.Date date27 = simulatorHeader26.getEndDate();
    boolean boolean28 = simulatorHeader26.isAutoStartSystem();
    int int29 = simulatorHeader26.getDayStartDate();
    boolean boolean30 = simulatorHeader26.isUseCelestia();
    opssat.simulator.util.SimulatorData simulatorData33 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date34 = simulatorData33.getCurrentTime();
    int int35 = opssat.simulator.util.DateExtraction.getDayFromDate(date34);
    opssat.simulator.util.SimulatorData simulatorData37 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date38 = simulatorData37.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData40 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date41 = simulatorData40.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap42 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date38, date41);
    opssat.simulator.util.SimulatorHeader simulatorHeader43 =
        new opssat.simulator.util.SimulatorHeader(
            false, date34, date41);
    simulatorHeader26.setStartDate(date41);
    simulatorHeader12.setStartDate(date41);
    simulatorHeader12.setAutoStartTime(false);
    int int48 = simulatorHeader12.getTimeFactor();
    int int49 = simulatorHeader12.getSecondStartDate();
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date17);
    org.junit.Assert.assertNotNull(date21);
    org.junit.Assert.assertNotNull(date24);
    org.junit.Assert.assertNotNull(timeUnitMap25);
    org.junit.Assert.assertNotNull(date27);
    org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + false + "'", !boolean28);
    org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + false + "'", !boolean30);
    org.junit.Assert.assertNotNull(date34);
    org.junit.Assert.assertNotNull(date38);
    org.junit.Assert.assertNotNull(date41);
    org.junit.Assert.assertNotNull(timeUnitMap42);
    org.junit.Assert.assertTrue("'" + int48 + "' != '" + 1 + "'", int48 == 1);
  }

  @Test
  public void test1335() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1335");
    }
    java.util.logging.Logger logger0 = null;
    opssat.simulator.util.EndlessSingleStreamOperatingBuffer endlessSingleStreamOperatingBuffer1 =
        new opssat.simulator.util.EndlessSingleStreamOperatingBuffer(
            logger0);
    byte[] byteArray3 = endlessSingleStreamOperatingBuffer1.getDataAsByteArray('4');
    byte[] byteArray5 = opssat.simulator.peripherals.PFineADCS.FWRefFineADCS.int16_2ByteArray(15);
    org.ccsds.moims.mo.mal.structures.Blob blob6 = new org.ccsds.moims.mo.mal.structures.Blob(
        byteArray5);
    endlessSingleStreamOperatingBuffer1.setDataFromByteArray(byteArray5);
    try {
      boolean boolean9 = endlessSingleStreamOperatingBuffer1.loadFromPath("SimulatorData");
      org.junit.Assert
          .fail("Expected exception of type java.lang.NullPointerException; message: null");
    } catch (java.lang.NullPointerException e) {
    }
    org.junit.Assert.assertNotNull(byteArray3);
    org.junit.Assert.assertNotNull(byteArray5);
  }

  @Test
  public void test1336() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1336");
    }
    java.lang.Double[] doubleArray4 = new java.lang.Double[]{(-1.0d), 100.0d, 10.0d, 10.0d};
    java.util.ArrayList<java.lang.Double> doubleList5 = new java.util.ArrayList<java.lang.Double>();
    boolean boolean6 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Double>) doubleList5, doubleArray4);
    org.ccsds.moims.mo.mal.structures.UShort uShort7 =
        org.ccsds.moims.mo.mal.structures.URIList.SERVICE_SHORT_FORM;
    org.ccsds.moims.mo.mal.structures.UShort[] uShortArray8 =
        new org.ccsds.moims.mo.mal.structures.UShort[]{
          uShort7};
    java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList9 =
        new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
    boolean boolean10 = java.util.Collections.addAll(
        (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList9, uShortArray8);
    uShortList9.ensureCapacity(0);
    int int14 = uShortList9.indexOf((byte) 1);
    uShortList9.clear();
    java.lang.Long[] longArray18 = new java.lang.Long[]{0L, 10L};
    java.util.ArrayList<java.lang.Long> longList19 = new java.util.ArrayList<java.lang.Long>();
    boolean boolean20 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Long>) longList19, longArray18);
    java.lang.Object obj21 = longList19.clone();
    boolean boolean22 = uShortList9.contains(longList19);
    boolean boolean23 = doubleList5.equals(boolean22);
    java.lang.Integer[] intArray26 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList27 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean28 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList27, intArray26);
    int int30 = intList27.lastIndexOf((byte) 10);
    boolean boolean31 = doubleList5.removeAll(intList27);
    java.lang.Integer[] intArray39 = new java.lang.Integer[]{13, 10, 100, 100, 11111, 13, 11111};
    java.util.ArrayList<java.lang.Integer> intList40 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean41 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList40, intArray39);
    java.lang.Byte[] byteArray46 = new java.lang.Byte[]{(byte) 100, (byte) 0, (byte) 10,
      (byte) 1};
    java.util.ArrayList<java.lang.Byte> byteList47 = new java.util.ArrayList<java.lang.Byte>();
    boolean boolean48 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Byte>) byteList47, byteArray46);
    java.lang.Integer[] intArray51 = new java.lang.Integer[]{1, 11111};
    java.util.ArrayList<java.lang.Integer> intList52 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean53 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList52, intArray51);
    boolean boolean54 = byteList47.retainAll(intList52);
    java.lang.Integer[] intArray57 = new java.lang.Integer[]{0, 1};
    java.util.ArrayList<java.lang.Integer> intList58 = new java.util.ArrayList<java.lang.Integer>();
    boolean boolean59 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Integer>) intList58, intArray57);
    int int61 = intList58.lastIndexOf((byte) 10);
    boolean boolean62 = intList52.removeAll(intList58);
    boolean boolean63 = intList40.retainAll(intList58);
    boolean boolean64 = doubleList5.removeAll(intList58);
    java.lang.Float[] floatArray68 = new java.lang.Float[]{1.0f, 1.0f, (-1.0f)};
    java.util.ArrayList<java.lang.Float> floatList69 = new java.util.ArrayList<java.lang.Float>();
    boolean boolean70 = java.util.Collections
        .addAll((java.util.Collection<java.lang.Float>) floatList69, floatArray68);
    boolean boolean72 = floatList69.add((-1.0f));
    floatList69.clear();
    boolean boolean74 = floatList69.isEmpty();
    java.util.ListIterator<java.lang.Float> floatItor75 = floatList69.listIterator();
    boolean boolean76 = doubleList5.contains(floatItor75);
    java.util.stream.Stream<java.lang.Double> doubleStream77 = doubleList5.stream();
    int int78 = doubleList5.size();
    boolean boolean79 = doubleList5.isEmpty();
    boolean boolean80 = doubleList5.isEmpty();
    org.junit.Assert.assertNotNull(doubleArray4);
    org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + true + "'", boolean6);
    org.junit.Assert.assertNotNull(uShort7);
    org.junit.Assert.assertNotNull(uShortArray8);
    org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
    org.junit.Assert.assertTrue("'" + int14 + "' != '" + (-1) + "'", int14 == (-1));
    org.junit.Assert.assertNotNull(longArray18);
    org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + true + "'", boolean20);
    org.junit.Assert.assertNotNull(obj21);
    org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + false + "'", !boolean22);
    org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + false + "'", !boolean23);
    org.junit.Assert.assertNotNull(intArray26);
    org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + true + "'", boolean28);
    org.junit.Assert.assertTrue("'" + int30 + "' != '" + (-1) + "'", int30 == (-1));
    org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + false + "'", !boolean31);
    org.junit.Assert.assertNotNull(intArray39);
    org.junit.Assert.assertTrue("'" + boolean41 + "' != '" + true + "'", boolean41);
    org.junit.Assert.assertNotNull(byteArray46);
    org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + true + "'", boolean48);
    org.junit.Assert.assertNotNull(intArray51);
    org.junit.Assert.assertTrue("'" + boolean53 + "' != '" + true + "'", boolean53);
    org.junit.Assert.assertTrue("'" + boolean54 + "' != '" + true + "'", boolean54);
    org.junit.Assert.assertNotNull(intArray57);
    org.junit.Assert.assertTrue("'" + boolean59 + "' != '" + true + "'", boolean59);
    org.junit.Assert.assertTrue("'" + int61 + "' != '" + (-1) + "'", int61 == (-1));
    org.junit.Assert.assertTrue("'" + boolean62 + "' != '" + true + "'", boolean62);
    org.junit.Assert.assertTrue("'" + boolean63 + "' != '" + true + "'", boolean63);
    org.junit.Assert.assertTrue("'" + boolean64 + "' != '" + false + "'", !boolean64);
    org.junit.Assert.assertNotNull(floatArray68);
    org.junit.Assert.assertTrue("'" + boolean70 + "' != '" + true + "'", boolean70);
    org.junit.Assert.assertTrue("'" + boolean72 + "' != '" + true + "'", boolean72);
    org.junit.Assert.assertTrue("'" + boolean74 + "' != '" + true + "'", boolean74);
    org.junit.Assert.assertNotNull(floatItor75);
    org.junit.Assert.assertTrue("'" + boolean76 + "' != '" + false + "'", !boolean76);
    org.junit.Assert.assertNotNull(doubleStream77);
    org.junit.Assert.assertTrue("'" + int78 + "' != '" + 4 + "'", int78 == 4);
    org.junit.Assert.assertTrue("'" + boolean79 + "' != '" + false + "'", !boolean79);
    org.junit.Assert.assertTrue("'" + boolean80 + "' != '" + false + "'", !boolean80);
  }

  @Test
  public void test1337() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1337");
    }
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState3 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray6 = new float[]{28, 8};
    simulatorSpacecraftState3.setQ(floatArray6);
    float[] floatArray8 = simulatorSpacecraftState3.getR();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState12 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    simulatorSpacecraftState12.setLongitude(4);
    double[] doubleArray16 = new double[]{(-1.0f)};
    simulatorSpacecraftState12.setMagField(doubleArray16);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState21 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double22 = simulatorSpacecraftState21.getLatitude();
    java.lang.String str23 = simulatorSpacecraftState21.getMagField();
    java.lang.String str24 = simulatorSpacecraftState21.toString();
    double[] doubleArray25 = simulatorSpacecraftState21.getSunVector();
    simulatorSpacecraftState12.setMagnetometer(doubleArray25);
    simulatorSpacecraftState3.setSunVector(doubleArray25);
    java.lang.String str28 = simulatorSpacecraftState3.getRotationAsString();
    simulatorSpacecraftState3.setLongitude((short) 0);
    simulatorSpacecraftState3.setAltitude((short) 100);
    org.junit.Assert.assertNotNull(floatArray6);
    org.junit.Assert.assertNotNull(floatArray8);
    org.junit.Assert.assertNotNull(doubleArray16);
    org.junit.Assert.assertTrue("'" + double22 + "' != '" + 340.0d + "'", double22 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str23 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str23.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertTrue(
        "'" + str24 + "' != '"
        + "SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}" + "'",
        str24.equals("SimulatorSpacecraftState{latitude=340.0, longitude=-1.0, altitude=14.0}"));
    org.junit.Assert.assertNotNull(doubleArray25);
    org.junit.Assert.assertTrue("'" + str28 + "' != '"
        + "\n+0.000000 +0.000000 +0.000000\n+0.000000 +0.000000 +0.000000\n+0.000000 +0.000000 +0.000000"
        + "'",
        str28.equals(
            "\n+0.000000 +0.000000 +0.000000\n+0.000000 +0.000000 +0.000000\n+0.000000 +0.000000 +0.000000"));
  }

  @Test
  public void test1338() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1338");
    }
    org.ccsds.moims.mo.mal.structures.URI uRI1 = new org.ccsds.moims.mo.mal.structures.URI(
        "0100.0000");
    java.lang.Integer int2 = uRI1.getTypeShortForm();
    java.lang.String str3 = uRI1.toString();
    java.lang.String str4 = uRI1.getValue();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet5 = uRI1.getAreaVersion();
    org.ccsds.moims.mo.mal.structures.ShortList shortList7 =
        new org.ccsds.moims.mo.mal.structures.ShortList(
            ' ');
    java.lang.Long long8 = shortList7.getShortForm();
    java.util.stream.Stream<java.lang.Short> shortStream9 = shortList7.stream();
    boolean boolean10 = uOctet5.equals(shortList7);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState14 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray17 = new float[]{28, 8};
    simulatorSpacecraftState14.setQ(floatArray17);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState22 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double23 = simulatorSpacecraftState22.getLatitude();
    java.lang.String str24 = simulatorSpacecraftState22.getMagField();
    float[] floatArray25 = simulatorSpacecraftState22.getR();
    simulatorSpacecraftState14.setQ(floatArray25);
    java.lang.String str27 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray25);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState31 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double32 = simulatorSpacecraftState31.getLatitude();
    double double33 = simulatorSpacecraftState31.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState37 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double38 = simulatorSpacecraftState37.getLatitude();
    java.lang.String str39 = simulatorSpacecraftState37.getMagField();
    float[] floatArray40 = simulatorSpacecraftState37.getR();
    simulatorSpacecraftState31.setQ(floatArray40);
    float[] floatArray42 = simulatorSpacecraftState31.getV();
    opssat.simulator.celestia.CelestiaData celestiaData43 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray25, floatArray42);
    float[] floatArray44 = celestiaData43.getQ();
    opssat.simulator.util.SimulatorData simulatorData48 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date49 = simulatorData48.getCurrentTime();
    int int50 = opssat.simulator.util.DateExtraction.getDayFromDate(date49);
    opssat.simulator.util.SimulatorData simulatorData52 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date53 = simulatorData52.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData55 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date56 = simulatorData55.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap57 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date53, date56);
    opssat.simulator.util.SimulatorHeader simulatorHeader58 =
        new opssat.simulator.util.SimulatorHeader(
            false, date49, date56);
    opssat.simulator.util.SimulatorData simulatorData59 = new opssat.simulator.util.SimulatorData(
        (short) 0, date49);
    celestiaData43.setDate(date49);
    int int61 = celestiaData43.getSeconds();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState65 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray68 = new float[]{28, 8};
    simulatorSpacecraftState65.setQ(floatArray68);
    celestiaData43.setQ(floatArray68);
    boolean boolean71 = shortList7.contains(celestiaData43);
    java.util.Iterator<java.lang.Short> shortItor72 = shortList7.iterator();
    java.lang.Object obj73 = shortList7.clone();
    org.junit.Assert.assertTrue("'" + int2 + "' != '" + 18 + "'", int2.equals(18));
    org.junit.Assert.assertTrue("'" + str3 + "' != '" + "0100.0000" + "'",
        str3.equals("0100.0000"));
    org.junit.Assert.assertTrue("'" + str4 + "' != '" + "0100.0000" + "'",
        str4.equals("0100.0000"));
    org.junit.Assert.assertNotNull(uOctet5);
    org.junit.Assert.assertTrue("'" + long8 + "' != '" + 281475010265079L + "'",
        long8.equals(281475010265079L));
    org.junit.Assert.assertNotNull(shortStream9);
    org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + false + "'", !boolean10);
    org.junit.Assert.assertNotNull(floatArray17);
    org.junit.Assert.assertTrue("'" + double23 + "' != '" + 340.0d + "'", double23 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str24 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str24.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray25);
    org.junit.Assert.assertTrue("'" + str27 + "' != '" + "UnknownGUIData" + "'",
        str27.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double32 + "' != '" + 340.0d + "'", double32 == 340.0d);
    org.junit.Assert.assertTrue("'" + double33 + "' != '" + 340.0d + "'", double33 == 340.0d);
    org.junit.Assert.assertTrue("'" + double38 + "' != '" + 340.0d + "'", double38 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str39 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str39.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray40);
    org.junit.Assert.assertNotNull(floatArray42);
    org.junit.Assert.assertNotNull(floatArray44);
    org.junit.Assert.assertNotNull(date49);
    org.junit.Assert.assertNotNull(date53);
    org.junit.Assert.assertNotNull(date56);
    org.junit.Assert.assertNotNull(timeUnitMap57);
    org.junit.Assert.assertNotNull(floatArray68);
    org.junit.Assert.assertTrue("'" + boolean71 + "' != '" + false + "'", !boolean71);
    org.junit.Assert.assertNotNull(shortItor72);
    org.junit.Assert.assertNotNull(obj73);
  }

  @Test
  public void test1339() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1339");
    }
    org.ccsds.moims.mo.mal.structures.StringList stringList0 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    java.util.Iterator<java.lang.String> strItor1 = stringList0.iterator();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = stringList0.getAreaVersion();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState6 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray9 = new float[]{28, 8};
    simulatorSpacecraftState6.setQ(floatArray9);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState14 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double15 = simulatorSpacecraftState14.getLatitude();
    java.lang.String str16 = simulatorSpacecraftState14.getMagField();
    float[] floatArray17 = simulatorSpacecraftState14.getR();
    simulatorSpacecraftState6.setQ(floatArray17);
    java.lang.String str19 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray17);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState23 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double24 = simulatorSpacecraftState23.getLatitude();
    double double25 = simulatorSpacecraftState23.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState29 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double30 = simulatorSpacecraftState29.getLatitude();
    java.lang.String str31 = simulatorSpacecraftState29.getMagField();
    float[] floatArray32 = simulatorSpacecraftState29.getR();
    simulatorSpacecraftState23.setQ(floatArray32);
    float[] floatArray34 = simulatorSpacecraftState23.getV();
    opssat.simulator.celestia.CelestiaData celestiaData35 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray17, floatArray34);
    float[] floatArray36 = celestiaData35.getQ();
    opssat.simulator.util.SimulatorData simulatorData40 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date41 = simulatorData40.getCurrentTime();
    int int42 = opssat.simulator.util.DateExtraction.getDayFromDate(date41);
    opssat.simulator.util.SimulatorData simulatorData44 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date45 = simulatorData44.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData47 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date48 = simulatorData47.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap49 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date45, date48);
    opssat.simulator.util.SimulatorHeader simulatorHeader50 =
        new opssat.simulator.util.SimulatorHeader(
            false, date41, date48);
    opssat.simulator.util.SimulatorData simulatorData51 = new opssat.simulator.util.SimulatorData(
        (short) 0, date41);
    celestiaData35.setDate(date41);
    int int53 = celestiaData35.getSeconds();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState57 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray60 = new float[]{28, 8};
    simulatorSpacecraftState57.setQ(floatArray60);
    celestiaData35.setQ(floatArray60);
    int int63 = stringList0.lastIndexOf(celestiaData35);
    celestiaData35
        .setDate("{counter=8, methodsExecuted=0, currentTime=Thu May 23 15:09:32 UTC 2019}");
    int int66 = celestiaData35.getMonths();
    celestiaData35.setAos("");
    int int69 = celestiaData35.getDays();
    org.junit.Assert.assertNotNull(strItor1);
    org.junit.Assert.assertNotNull(uOctet2);
    org.junit.Assert.assertNotNull(floatArray9);
    org.junit.Assert.assertTrue("'" + double15 + "' != '" + 340.0d + "'", double15 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str16 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str16.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray17);
    org.junit.Assert.assertTrue("'" + str19 + "' != '" + "UnknownGUIData" + "'",
        str19.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double24 + "' != '" + 340.0d + "'", double24 == 340.0d);
    org.junit.Assert.assertTrue("'" + double25 + "' != '" + 340.0d + "'", double25 == 340.0d);
    org.junit.Assert.assertTrue("'" + double30 + "' != '" + 340.0d + "'", double30 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str31 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str31.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray32);
    org.junit.Assert.assertNotNull(floatArray34);
    org.junit.Assert.assertNotNull(floatArray36);
    org.junit.Assert.assertNotNull(date41);
    org.junit.Assert.assertNotNull(date45);
    org.junit.Assert.assertNotNull(date48);
    org.junit.Assert.assertNotNull(timeUnitMap49);
    org.junit.Assert.assertNotNull(floatArray60);
    org.junit.Assert.assertTrue("'" + int63 + "' != '" + (-1) + "'", int63 == (-1));
  }

  @Test
  public void test1340() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1340");
    }
    opssat.simulator.util.SimulatorData simulatorData2 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date3 = simulatorData2.getCurrentTime();
    int int4 = opssat.simulator.util.DateExtraction.getDayFromDate(date3);
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date7 = simulatorData6.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap11 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date7, date10);
    opssat.simulator.util.SimulatorHeader simulatorHeader12 =
        new opssat.simulator.util.SimulatorHeader(
            false, date3, date10);
    java.util.Date date13 = simulatorHeader12.getEndDate();
    simulatorHeader12.setOrekitTLE1("");
    java.lang.String str16 = simulatorHeader12.DATE_FORMAT;
    java.lang.String str17 = simulatorHeader12.getOrekitTLE1();
    java.lang.String str18 = simulatorHeader12.toString();
    int int19 = simulatorHeader12.getDayStartDate();
    java.lang.String str20 = simulatorHeader12.DATE_FORMAT;
    org.junit.Assert.assertNotNull(date3);
    org.junit.Assert.assertNotNull(date7);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(timeUnitMap11);
    org.junit.Assert.assertNotNull(date13);
    org.junit.Assert.assertTrue("'" + str16 + "' != '" + "yyyy:MM:dd HH:mm:ss z" + "'",
        str16.equals("yyyy:MM:dd HH:mm:ss z"));
    org.junit.Assert.assertTrue("'" + str17 + "' != '" + "" + "'", str17.equals(""));
    org.junit.Assert.assertTrue("'" + str20 + "' != '" + "yyyy:MM:dd HH:mm:ss z" + "'",
        str20.equals("yyyy:MM:dd HH:mm:ss z"));
  }

  @Test
  public void test1341() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1341");
    }
    org.ccsds.moims.mo.mal.structures.StringList stringList0 =
        new org.ccsds.moims.mo.mal.structures.StringList();
    java.util.Iterator<java.lang.String> strItor1 = stringList0.iterator();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = stringList0.getAreaVersion();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState6 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray9 = new float[]{28, 8};
    simulatorSpacecraftState6.setQ(floatArray9);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState14 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double15 = simulatorSpacecraftState14.getLatitude();
    java.lang.String str16 = simulatorSpacecraftState14.getMagField();
    float[] floatArray17 = simulatorSpacecraftState14.getR();
    simulatorSpacecraftState6.setQ(floatArray17);
    java.lang.String str19 = opssat.simulator.util.CommandDescriptor
        .makeConsoleDescriptionForObj(floatArray17);
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState23 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double24 = simulatorSpacecraftState23.getLatitude();
    double double25 = simulatorSpacecraftState23.getLatitude();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState29 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    double double30 = simulatorSpacecraftState29.getLatitude();
    java.lang.String str31 = simulatorSpacecraftState29.getMagField();
    float[] floatArray32 = simulatorSpacecraftState29.getR();
    simulatorSpacecraftState23.setQ(floatArray32);
    float[] floatArray34 = simulatorSpacecraftState23.getV();
    opssat.simulator.celestia.CelestiaData celestiaData35 =
        new opssat.simulator.celestia.CelestiaData(
            floatArray17, floatArray34);
    float[] floatArray36 = celestiaData35.getQ();
    opssat.simulator.util.SimulatorData simulatorData40 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date41 = simulatorData40.getCurrentTime();
    int int42 = opssat.simulator.util.DateExtraction.getDayFromDate(date41);
    opssat.simulator.util.SimulatorData simulatorData44 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date45 = simulatorData44.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData47 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date48 = simulatorData47.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap49 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date45, date48);
    opssat.simulator.util.SimulatorHeader simulatorHeader50 =
        new opssat.simulator.util.SimulatorHeader(
            false, date41, date48);
    opssat.simulator.util.SimulatorData simulatorData51 = new opssat.simulator.util.SimulatorData(
        (short) 0, date41);
    celestiaData35.setDate(date41);
    int int53 = celestiaData35.getSeconds();
    opssat.simulator.util.SimulatorSpacecraftState simulatorSpacecraftState57 =
        new opssat.simulator.util.SimulatorSpacecraftState(
            340.0d, (-1.0f), 14);
    float[] floatArray60 = new float[]{28, 8};
    simulatorSpacecraftState57.setQ(floatArray60);
    celestiaData35.setQ(floatArray60);
    int int63 = stringList0.lastIndexOf(celestiaData35);
    celestiaData35
        .setDate("{counter=8, methodsExecuted=0, currentTime=Thu May 23 15:09:32 UTC 2019}");
    int int66 = celestiaData35.getMonths();
    celestiaData35.setInfo("*36");
    org.junit.Assert.assertNotNull(strItor1);
    org.junit.Assert.assertNotNull(uOctet2);
    org.junit.Assert.assertNotNull(floatArray9);
    org.junit.Assert.assertTrue("'" + double15 + "' != '" + 340.0d + "'", double15 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str16 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str16.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray17);
    org.junit.Assert.assertTrue("'" + str19 + "' != '" + "UnknownGUIData" + "'",
        str19.equals("UnknownGUIData"));
    org.junit.Assert.assertTrue("'" + double24 + "' != '" + 340.0d + "'", double24 == 340.0d);
    org.junit.Assert.assertTrue("'" + double25 + "' != '" + 340.0d + "'", double25 == 340.0d);
    org.junit.Assert.assertTrue("'" + double30 + "' != '" + 340.0d + "'", double30 == 340.0d);
    org.junit.Assert.assertTrue(
        "'" + str31 + "' != '"
        + "North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]" + "'",
        str31.equals("North:[+00000.00] ; East:[+00000.00] ; Vertical: [+00000.00] units [nT]"));
    org.junit.Assert.assertNotNull(floatArray32);
    org.junit.Assert.assertNotNull(floatArray34);
    org.junit.Assert.assertNotNull(floatArray36);
    org.junit.Assert.assertNotNull(date41);
    org.junit.Assert.assertNotNull(date45);
    org.junit.Assert.assertNotNull(date48);
    org.junit.Assert.assertNotNull(timeUnitMap49);
    org.junit.Assert.assertNotNull(floatArray60);
    org.junit.Assert.assertTrue("'" + int63 + "' != '" + (-1) + "'", int63 == (-1));
  }

  @Test
  public void test1342() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1342");
    }
    opssat.simulator.util.SimulatorData simulatorData3 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date4 = simulatorData3.getCurrentTime();
    java.util.Date date5 = simulatorData3.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData6 = new opssat.simulator.util.SimulatorData(17,
        date5);
    opssat.simulator.util.SimulatorData simulatorData9 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date10 = simulatorData9.getCurrentTime();
    int int11 = opssat.simulator.util.DateExtraction.getDayFromDate(date10);
    opssat.simulator.util.SimulatorData simulatorData13 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date14 = simulatorData13.getCurrentTime();
    opssat.simulator.util.SimulatorData simulatorData16 = new opssat.simulator.util.SimulatorData(
        (-18));
    java.util.Date date17 = simulatorData16.getCurrentTime();
    java.util.Map<java.util.concurrent.TimeUnit, java.lang.Long> timeUnitMap18 =
        opssat.simulator.util.SimulatorData
            .computeDiff(date14, date17);
    opssat.simulator.util.SimulatorHeader simulatorHeader19 =
        new opssat.simulator.util.SimulatorHeader(
            false, date10, date17);
    opssat.simulator.util.SimulatorHeader simulatorHeader20 =
        new opssat.simulator.util.SimulatorHeader(
            false, date5, date10);
    simulatorHeader20.setUpdateInternet(true);
    int int23 = simulatorHeader20.getHourStartDate();
    simulatorHeader20.setAutoStartSystem(false);
    boolean boolean26 = simulatorHeader20.isAutoStartSystem();
    simulatorHeader20.setOrekitTLE2("5400.0000000");
    org.junit.Assert.assertNotNull(date4);
    org.junit.Assert.assertNotNull(date5);
    org.junit.Assert.assertNotNull(date10);
    org.junit.Assert.assertNotNull(date14);
    org.junit.Assert.assertNotNull(date17);
    org.junit.Assert.assertNotNull(timeUnitMap18);
    org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + false + "'", !boolean26);
  }

  @Test
  public void test1343() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1343");
    }
    org.ccsds.moims.mo.mal.structures.OctetList octetList0 =
        new org.ccsds.moims.mo.mal.structures.OctetList();
    java.lang.Integer int1 = octetList0.getTypeShortForm();
    org.ccsds.moims.mo.mal.structures.UShort uShort2 = octetList0.getAreaNumber();
    org.ccsds.moims.mo.mal.structures.UOctet uOctet3 = octetList0.getAreaVersion();
    opssat.simulator.util.wav.WavFileException wavFileException5 =
        new opssat.simulator.util.wav.WavFileException(
            "UnknownGUIData");
    java.lang.Throwable[] throwableArray6 = wavFileException5.getSuppressed();
    boolean boolean7 = octetList0.equals(wavFileException5);
    org.ccsds.moims.mo.mal.structures.FloatList floatList9 =
        new org.ccsds.moims.mo.mal.structures.FloatList(
            (byte) 0);
    java.lang.Integer int10 = floatList9.getTypeShortForm();
    java.lang.Integer int11 = floatList9.getTypeShortForm();
    floatList9.clear();
    boolean boolean13 = octetList0.remove(floatList9);
    java.util.ListIterator<java.lang.Float> floatItor14 = floatList9.listIterator();
    org.ccsds.moims.mo.mal.structures.UShort uShort15 = floatList9.getAreaNumber();
    floatList9.clear();
    floatList9.trimToSize();
    org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-7) + "'", int1.equals((-7)));
    org.junit.Assert.assertNotNull(uShort2);
    org.junit.Assert.assertNotNull(uOctet3);
    org.junit.Assert.assertNotNull(throwableArray6);
    org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
    org.junit.Assert.assertTrue("'" + int10 + "' != '" + (-4) + "'", int10.equals((-4)));
    org.junit.Assert.assertTrue("'" + int11 + "' != '" + (-4) + "'", int11.equals((-4)));
    org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + false + "'", !boolean13);
    org.junit.Assert.assertNotNull(floatItor14);
    org.junit.Assert.assertNotNull(uShort15);
  }

  final String TEST_TLE1_LINE1 =
      "1 44878U 19092F   20026.53979074 -.00000066  00000-0  00000+0 0  9998";
  final String TEST_TLE1_LINE2 =
      "2 44878  97.4628 212.2828 0015597 128.3977 231.8751 15.15220787  5895";

  final String TEST_TLE2_LINE1 =
      "1 25544U 98067A   08264.51782528 -.00002182  00000-0 -11606-4 0  2927";
  final String TEST_TLE2_LINE2 =
      "2 25544  51.6416 247.4627 0006703 130.5360 325.0288 15.72125391563537";

  /**
   * test getTLE in case that tle propagator is running
   */
  @Test
  public void test1344() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1343");
    }
    ConcurrentLinkedQueue<Object> qSimToCentral = new ConcurrentLinkedQueue<Object>();
    ConcurrentLinkedQueue<Object> qCentralToSim = new ConcurrentLinkedQueue<Object>();

    final Calendar calendar = Calendar.getInstance();
    calendar.set(2017, 1, 10);
    final Date startDate = calendar.getTime();
    calendar.set(2019, 1, 10);
    final Date endDate = calendar.getTime();

    SimulatorHeader simHeader = new SimulatorHeader();
    simHeader.setUseOrekitPropagator(true);
    simHeader.setStartDate(startDate);
    simHeader.setEndDate(endDate);
    simHeader.setOrekitPropagator("tle");
    simHeader.setOrekitTLE1(TEST_TLE2_LINE1 + " ");
    simHeader.setOrekitTLE2(TEST_TLE2_LINE2 + " ");
    simHeader.setUseCelestia(false);

    String content = simHeader.toFileString();
    content += "\norekitPropagator=tle";
    content += "\norekitTLE1= " + TEST_TLE2_LINE1 + " ";
    content += "\norekitTLE2= " + TEST_TLE2_LINE2 + " ";

    File file = new File(System.getProperty("user.dir"), "_OPS-SAT-SIMULATOR-header.txt");
    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
    writer.write(content);
    writer.flush();
    writer.close();
    SimulatorNode simNode =
        new SimulatorNode(qCentralToSim, qSimToCentral, "Sim", 100, Level.SEVERE, Level.SEVERE);

    TLE tle = simNode.getTLE();

    org.junit.Assert.assertTrue(
        "\n"
        + tle.toString() + "\n != \n" + TEST_TLE2_LINE1 + "\n" + TEST_TLE2_LINE2,
        tle.toString().equals(TEST_TLE2_LINE1 + "\n" + TEST_TLE2_LINE2));

    file.delete();
  }

  /**
   * test getTLE in case that tle propagator is NOT running
   */
  @Test
  public void test1345() throws Throwable
  {
    if (debug) {
      System.out.format("%n%s%n", "RegressionTest2.test1343");
    }
    ConcurrentLinkedQueue<Object> qSimToCentral = new ConcurrentLinkedQueue<Object>();
    ConcurrentLinkedQueue<Object> qCentralToSim = new ConcurrentLinkedQueue<Object>();

    final Calendar calendar = Calendar.getInstance();
    calendar.set(2017, 1, 10);
    final Date startDate = calendar.getTime();
    calendar.set(2019, 1, 10);
    final Date endDate = calendar.getTime();

    SimulatorHeader simHeader = new SimulatorHeader();
    simHeader.setUseOrekitPropagator(true);
    simHeader.setStartDate(startDate);
    simHeader.setEndDate(endDate);
    simHeader.setUseCelestia(false);

    String content = simHeader.toFileString();

    File file = new File(System.getProperty("user.dir"), "_OPS-SAT-SIMULATOR-header.txt");
    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
    writer.write(content);
    writer.flush();
    writer.close();
    SimulatorNode simNode =
        new SimulatorNode(qCentralToSim, qSimToCentral, "Sim", 100, Level.SEVERE, Level.SEVERE);

    TLE tle = simNode.getTLE();

    //  Catalog Number, launch year, launch number, ephemeris type, mean motion d1 and d2, number of
    // revolutions and BStart will be set to 0"
    // launch Piece will be set to "N", classification will be set to 'U'
    org.junit.Assert.assertTrue(
        "TLE BStar is " + tle.getLaunchPiece() + " instead of 0", tle.getBStar() == 0);

    org.junit.Assert.assertTrue(
        "TLE launch piece is " + tle.getLaunchPiece() + " instead of 'N'",
        tle.getLaunchPiece().equals("N"));

    org.junit.Assert.assertTrue(
        "TLE classification is " + tle.getClassification() + " instead of 'U'",
        tle.getClassification() == 'U');

    org.junit.Assert.assertTrue(
        "TLE eccentricity is " + tle.getE() + " instead of " + SimulatorNode.DEFAULT_OPS_SAT_E,
        tle.getE() == SimulatorNode.DEFAULT_OPS_SAT_E
    );

    org.junit.Assert.assertTrue(
        "TLE mean motion is " + tle.getMeanMotionFirstDerivative() + " instead of 0",
        tle.getMeanMotionFirstDerivative() == 0
    );

    org.junit.Assert.assertTrue(
        "TLE mean motion is " + tle.getMeanMotionSecondDerivative() + " instead of 0",
        tle.getMeanMotionSecondDerivative() == 0
    );

    org.junit.Assert.assertTrue(
        "TLE argument of perigee is " + tle.getPerigeeArgument() + " instead of " + SimulatorNode.DEFAULT_OPS_SAT_ARG_PER,
        tle.getPerigeeArgument() == SimulatorNode.DEFAULT_OPS_SAT_ARG_PER
    );

    file.delete();
  }
}
