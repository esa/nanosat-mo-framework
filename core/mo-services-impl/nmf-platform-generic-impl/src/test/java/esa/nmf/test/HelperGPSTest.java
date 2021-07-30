package esa.nmf.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import java.io.IOException;
import org.junit.runners.MethodSorters;
import esa.mo.platform.impl.util.HelperGPS;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HelperGPSTest
{

  @Test(expected = IOException.class)
  public void testInvalidResultGPGGA() throws Throwable
  {
    String gpggalongResponse = "$GPGGA,081845.00,,,,,0,00,9.9,,,,,,*66";
    HelperGPS.gpggalong2Position(gpggalongResponse);
  }
  @Test(expected = IOException.class)
  public void testInvalidResultGPGSV() throws Throwable
  {
    String gpgsvResponse = "$GPGSV,,1,13,02,,,,03,-3,000,,11,00,121,,14,13,172,05*67";
    HelperGPS.gpgsv2SatelliteInfoList(gpgsvResponse);
  }
}