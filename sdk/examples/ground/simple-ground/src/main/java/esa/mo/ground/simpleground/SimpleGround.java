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
package esa.mo.ground.simpleground;

import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import esa.mo.nmf.groundmoadapter.SimpleDataReceivedListener;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ground consumer: Demo Simple Ground
 */
public class SimpleGround
{

  private final GroundMOAdapterImpl gma;

  private final Logger LOGGER = Logger.getLogger(SimpleGround.class.getName());

  public SimpleGround()
  {
    ConnectionConsumer connection = new ConnectionConsumer();

    try {
      connection.loadURIs();
    } catch (MalformedURLException | FileNotFoundException ex) {
      LOGGER.log(Level.SEVERE, "The URIs could not be loaded from a file.", ex);
    }

    gma = new GroundMOAdapterImpl(connection);
    gma.addDataReceivedListener(new DataReceivedAdapter());
  }

  /**
   * Main command line entry point.
   *
   * @param args the command line arguments
   * @throws java.lang.Exception If there is an error
   */
  public static void main(final String args[]) throws Exception
  {
    SimpleGround demo = new SimpleGround();
  }

  class DataReceivedAdapter extends SimpleDataReceivedListener
  {

    @Override
    public void onDataReceived(String parameterName, Serializable data)
    {
      LOGGER.log(Level.INFO,
          "\nParameter name: {0}" + "\n" + "Data content:\n{1}",
          new Object[]{parameterName, data.toString()});
    }
  }
}
