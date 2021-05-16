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
package esa.mo.ground.setandcommand;

import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Set and Command demo application. This demo should be used with the Hello World demo provider.
 *
 */
public class DemoSetAndCommand
{

  private static final Logger LOGGER = Logger.getLogger(DemoSetAndCommand.class.getName());
  private final GroundMOAdapterImpl gma;

  public DemoSetAndCommand()
  {
    ConnectionConsumer connection = new ConnectionConsumer();

    try {
      connection.loadURIs();
    } catch (MalformedURLException | FileNotFoundException ex) {
      LOGGER.log(Level.SEVERE, "The URIs could not be loaded from a file.", ex);
    }

    gma = new GroundMOAdapterImpl(connection);

    // Set a parameter with a string value
    String parameterValue = "The parameter was set!";
    gma.setParameter("A_Parameter", parameterValue);

    // Send a command with a Double argument
    double value = 1.35565;
    Double[] values = new Double[1];
    values[0] = value;
    gma.launchAction("Go", values);
  }

  /**
   * Main command line entry point.
   *
   * @param args the command line arguments
   * @throws java.lang.Exception If there is an error
   */
  public static void main(final String args[]) throws Exception
  {
    DemoSetAndCommand demo = new DemoSetAndCommand();
  }
}
