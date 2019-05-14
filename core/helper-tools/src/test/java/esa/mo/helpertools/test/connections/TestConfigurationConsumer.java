package esa.mo.helpertools.test.connections;

import static org.junit.Assert.assertEquals;

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.junit.Before;
import org.junit.Test;

import esa.mo.helpertools.connections.ConfigurationConsumer;

public class TestConfigurationConsumer {
  
  @Before
  public void cleanProps() {
    System.getProperties().clear();
  }
  
	@Test
	public void testConfigurationConsumer1() {
	  System.setProperty("consumer.properties", getClass().getClassLoader().getResource("testProp4.properties").getPath());
	  ConfigurationConsumer uut = new ConfigurationConsumer();
	  assertEquals(new Identifier("LIVE"), uut.getSessionName());
	  assertEquals(SessionType.LIVE, uut.getSession());
	  assertEquals("esa.HelpertoolsTest.Ground.Workstation", uut.getNetwork().getValue());
	}

	@Test
  public void testConfigurationConsumer2() {
    System.setProperty("consumer.properties", getClass().getClassLoader().getResource("testProp5.properties").getPath());
    System.out.println(System.getProperty("consumer.properties"));
    ConfigurationConsumer uut = new ConfigurationConsumer();
    assertEquals(new Identifier("LIVE"), uut.getSessionName());
    assertEquals(SessionType.LIVE, uut.getSession());
    assertEquals("spacenet", uut.getNetwork().getValue());
  }
	
	@Test
  public void testConfigurationConsumer3() {
    System.setProperty("consumer.properties", getClass().getClassLoader().getResource("testProp6.properties").getPath());
    System.out.println(System.getProperty("consumer.properties"));
    ConfigurationConsumer uut = new ConfigurationConsumer();
    assertEquals(new Identifier("LIVE"), uut.getSessionName());
    assertEquals(SessionType.LIVE, uut.getSession());
    assertEquals("OrganizationName.MissionName.NetworkZone.DeviceName", uut.getNetwork().getValue());
  }
	
}
