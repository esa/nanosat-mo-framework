package esa.nmf.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import java.io.IOException;
import org.junit.runners.MethodSorters;
import esa.mo.platform.impl.util.HelperGPS;
import org.ccsds.moims.mo.platform.gps.structures.Position;
import org.ccsds.moims.mo.platform.gps.structures.SatelliteInfoList;
import org.junit.Assert;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HelperGPSTest {
    public static final double EPSILON = 0.00001;
    String GPGGA_1 = "$GPGGA,113839.00,4952.2895,N,00837.3559,E,1,09,0.9,143.85,M,48.10,M,,*61";
    public static final float LAT_POS1 = 49.87149f;
    public static final float LON_POS1 = 8.622598f;
    public static final float ALT_POS1 = 143.85f;
    String GPGGA_2 = "$GPGGA,113922.00,4952.2895,N,00837.3561,E,1,09,0.9,143.55,M,48.10,M,,*6C";
    public static final float LAT_POS2 = 49.87149f;
    public static final float LON_POS2 = 8.622598f;
    public static final float ALT_POS2 = 143.55f;
    public static String GPGGALONG_3 = "$GPGGA,113913.00,4952.2895037,N,00837.3560677,E,1,09,0.9,143.369,M,48.10,M,,*51";
    public static final float LAT_POS3 = 49.87149f;
    public static final float LON_POS3 = 8.622598f;
    public static final float ALT_POS3 = 143.369f;
    public static String GPGGALONG_4 = "$GPGGA,113951.00,4952.2895953,N,00837.3561173,E,1,09,0.9,144.054,M,,,,*3A";
    public static final float LAT_POS4 = 49.87149f;
    public static final float LON_POS4 = 8.622598f;
    public static final float ALT_POS4 = 144.054f;
    public static final String BESTXYZ = "]<BESTXYZ COM1 0 81.5 FINESTEERING 2169 301296.000 00000000 fac1 13307\n" +
        "<     SOL_COMPUTED SINGLE 4072372.3638 617531.4585 4853736.2608 2.6837 1.4338 3.2390 SOL_COMPUTED DOPPLER_VELOCITY -0.0060 0.0084 -0.0109 0.3208 0.1714 0.3871 \"\" 0.150 0.000 0.000 16 9 9 0 0 02 00 01";
    public static final String TIMEA1 = "#TIMEA,COM1,0,79.0,FINESTEERING,2169,301319.000,00000000,9924,13307;VALID,1.088781766e-09,1.522570309e-09,-18.00000000000,2021,8,4,11,41,41000,VALID*c2402aae";
    public static final String TIMEA2 = "#TIMEA,COM1,0,80.0,FINESTEERING,2169,301312.000,00000000,9924,13307;VALID,3.375007292e-10,1.506055426e-09,-18.00000000000,2021,8,4,11,41,34000,VALID*f2141725";
    public static final String RANGEA = "#RANGEA,COM1,0,80.5,FINESTEERING,2169,134778.000,00000000,5103,13307;16,1,0,19864566.767,0.053,-104389005.169516,0.008,-160.420,46.6,4227.230,08009c04,19,0,24101071.925,0.062,-126651991.635917,0.009,3503.035,45.3,1346.110,18009c24,14,0,24434228.474,0.061,-128402714.514027,0.008,-2251.511,45.5,3468.150,18009c44,17,0,22523344.500,0.068,-118360993.538325,0.009,2123.431,44.5,4174.700,08009c64,4,0,24447035.410,0.167,-128470047.602822,0.026,3846.523,36.7,1095.010,08009c84,31,0,24708416.719,0.090,-129843610.416276,0.013,2782.899,42.0,532.982,08009ca4,3,0,20712112.965,0.056,-108842907.334487,0.007,1557.387,46.2,4216.490,08009cc4,32,0,23225387.747,0.043,-122050230.467475,0.006,-2532.703,48.6,4219.200,08009d04,22,0,20473933.737,0.046,-107591248.751954,0.006,59.275,47.9,4224.870,18009d44,21,0,21163512.953,0.086,-111215008.490121,0.013,-1167.646,42.5,4209.300,18009d64,21,0,28309009.259,0.079,-148764836.440423,0.013,-2006.681,43.2,4194.783,48439e84,3,0,23876088.964,0.072,-125469717.481042,0.014,933.976,44.0,4164.939,48439ec4,5,0,25460974.025,0.047,-133798306.707520,0.009,-1886.963,47.7,4165.831,48439f04,1,0,28497541.690,0.054,-149755586.915738,0.009,-32.777,46.4,2156.142,48439f24,13,0,25102781.522,0.037,-131916019.387822,0.006,2220.230,49.7,4220.406,48439c04,15,0,23006494.651,0.034,-120899943.352160,0.006,-214.754,50.4,4166.638,48439c44*b6425199";

    @Test(expected = IOException.class)
    public void testInvalidResultGPGGA() throws Throwable {
        String gpggalongResponse = "$GPGGA,081845.00,,,,,0,00,9.9,,,,,,*66";
        HelperGPS.gpggalong2Position(gpggalongResponse);
    }

    @Test(expected = IOException.class)
    public void testInvalidResultGPGSV() throws Throwable {
        String gpgsvResponse = "$GPGSV,,1,13,02,,,,03,-3,000,,11,00,121,,14,13,172,05*67";
        HelperGPS.gpgsv2SatelliteInfoList(gpgsvResponse);
    }

    @Test
    public void testGPGGA() throws Throwable {
        Position pos;
        pos = HelperGPS.gpggalong2Position(GPGGA_1);
        Assert.assertEquals(LAT_POS1, pos.getLatitude(), EPSILON);
        Assert.assertEquals(LON_POS1, pos.getLongitude(), EPSILON);
        Assert.assertEquals(ALT_POS1, pos.getAltitude(), EPSILON);
        pos = HelperGPS.gpggalong2Position(GPGGA_2);
        Assert.assertEquals(LAT_POS2, pos.getLatitude(), EPSILON);
        Assert.assertEquals(LON_POS2, pos.getLongitude(), EPSILON);
        Assert.assertEquals(ALT_POS2, pos.getAltitude(), EPSILON);
        pos = HelperGPS.gpggalong2Position(GPGGALONG_3);
        Assert.assertEquals(LAT_POS3, pos.getLatitude(), EPSILON);
        Assert.assertEquals(LON_POS3, pos.getLongitude(), EPSILON);
        Assert.assertEquals(ALT_POS3, pos.getAltitude(), EPSILON);
        pos = HelperGPS.gpggalong2Position(GPGGALONG_4);
        Assert.assertEquals(LAT_POS4, pos.getLatitude(), EPSILON);
        Assert.assertEquals(LON_POS4, pos.getLongitude(), EPSILON);
        Assert.assertEquals(ALT_POS4, pos.getAltitude(), EPSILON);
    }

    @Test
    public void testGPGSV() throws Throwable {
        SatelliteInfoList satInfo;
        String gpgsv1 = "$GPGSV,3,1,11,21,71,297,43,08,62,179,45,01,45,284,44,22,42,226,41*7C\n" +
            "$GPGSV,3,2,11,32,39,094,49,27,33,154,40,10,29,054,43,14,20,313,44*78\n" +
            "$GPGSV,3,3,11,03,20,224,41,24,01,023,46,17,00,316,*4F";
        satInfo = HelperGPS.gpgsv2SatelliteInfoList(gpgsv1);
        Assert.assertEquals(11, satInfo.size());
        Assert.assertEquals(21, satInfo.get(0).getPrn().floatValue(), EPSILON);
        Assert.assertEquals(43, satInfo.get(0).getSnr().floatValue(), EPSILON);
        Assert.assertEquals(46, satInfo.get(9).getSnr().floatValue(), EPSILON);
        Assert.assertEquals(17, satInfo.get(10).getPrn().intValue());
        Assert.assertEquals(0, satInfo.get(10).getElevation().floatValue(), EPSILON);
        Assert.assertEquals(316, satInfo.get(10).getAzimuth().floatValue(), EPSILON);
        Assert.assertEquals(null, satInfo.get(10).getSnr());
        String gpgsv2 = "$GPGSV,3,1,11,21,71,297,43,08,62,179,45,01,45,285,44,22,42,226,41*7D\n" +
            "$GPGSV,3,2,11,32,39,094,49,27,33,154,40,10,29,054,43,14,20,313,44*78\n" +
            "$GPGSV,3,3,11,03,20,224,40,24,01,023,46,17,01,316,*4F\n";
        satInfo = HelperGPS.gpgsv2SatelliteInfoList(gpgsv2);
        Assert.assertEquals(11, satInfo.size());
        Assert.assertEquals(21, satInfo.get(0).getPrn().intValue());
        Assert.assertEquals(43, satInfo.get(0).getSnr(), EPSILON);
        Assert.assertEquals(46, satInfo.get(9).getSnr().floatValue(), EPSILON);
        Assert.assertEquals(17, satInfo.get(10).getPrn().intValue());
        Assert.assertEquals(1, satInfo.get(10).getElevation().floatValue(), EPSILON);
        Assert.assertEquals(316, satInfo.get(10).getAzimuth().floatValue(), EPSILON);
        Assert.assertEquals(null, satInfo.get(10).getSnr());
    }
}