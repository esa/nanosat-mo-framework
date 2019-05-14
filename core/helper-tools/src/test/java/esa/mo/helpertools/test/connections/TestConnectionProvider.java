package esa.mo.helpertools.test.connections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import esa.mo.helpertools.connections.ConnectionProvider;

public class TestConnectionProvider {

  /* Tests for resetURILinksFile */

  @Test
  public void resetURILinkFile1() {
    File f = new File("providerURIs.properties");
    try {
      FileOutputStream fos = new FileOutputStream(f);
      fos.write(
          "I am a provider properties file and my content is meaningless because I get deleted anyways."
              .getBytes());
      fos.close();
      ConnectionProvider.resetURILinksFile();
      assertEquals(0, f.length());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      fail("File should be found...");
    } catch (IOException e) {
      e.printStackTrace();
      fail("Should be able to write to this file");
    }
  }

}
