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
package esa.mo.nmf.apps;

import esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl;

/**
 * Main Class to initiate Camera Acquisitor System
 *
 * @author Kevin Otto
 */
public class CameraAcquisitorSystemApp
{

  /**
   * Main command line entry point.
   *
   * @param args the command line arguments
   * @throws java.lang.Exception If there is an error
   */
  public static void main(final String args[]) throws Exception
  {
    final NanoSatMOConnectorImpl connector = new NanoSatMOConnectorImpl();
    CameraAcquisitorSystemMCAdapter adapter = new CameraAcquisitorSystemMCAdapter(connector);
    connector.init(adapter);
    adapter.recoverLastState();
  }
}
