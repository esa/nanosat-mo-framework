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
package esa.mo.ground.directory;

import esa.mo.mc.impl.provider.ParameterInstance;
import esa.mo.nmf.commonmoadapter.CompleteDataReceivedListener;
import esa.mo.nmf.commonmoadapter.SimpleDataReceivedListener;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.MalformedURLException;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 * Demo application using the Directory service
 *
 * @author Cesar Coelho
 */
public class DemoGroundDirectory
{

  private final static URI DIRECTORY_URI
      = new URI("maltcp://x:1024-nanosat-mo-supervisor-Directory");

  private GroundMOAdapterImpl gma;
  private static final Logger LOGGER = Logger.getLogger(DemoGroundDirectory.class.getName());

  public DemoGroundDirectory()
  {
    try {
      ProviderSummaryList providers = GroundMOAdapterImpl.retrieveProvidersFromDirectory(
          DIRECTORY_URI);

      if (!providers.isEmpty()) {
        // Connect to provider on index 0
        gma = new GroundMOAdapterImpl(providers.get(0));
        gma.addDataReceivedListener(new CompleteDataReceivedAdapter());
      } else {
        LOGGER.log(Level.SEVERE,
            "The returned list of providers is empty!");
      }
    } catch (MALException | MalformedURLException | MALInteractionException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Main command line entry point.
   *
   * @param args the command line arguments
   * @throws java.lang.Exception If there is an error
   */
  public static void main(final String args[]) throws Exception
  {
    DemoGroundDirectory demo = new DemoGroundDirectory();
  }

  private class SimpleDataReceivedAdapter extends SimpleDataReceivedListener
  {

    @Override
    public void onDataReceived(String parameterName, Serializable data)
    {
      LOGGER.log(Level.INFO,
          "\nParameter name: {0}" + "\n" + "Data content:\n{1}",
          new Object[]{
            parameterName,
            data.toString()
          }
      );
    }
  }

  private class CompleteDataReceivedAdapter extends CompleteDataReceivedListener
  {

    @Override
    public void onDataReceived(ParameterInstance parameterInstance)
    {
      LOGGER.log(Level.INFO,
          "\nParameter name: {0}" + "\n" + "Parameter Value: {1}",
          new Object[]{
            parameterInstance.getName(),
            parameterInstance.getParameterValue().toString()
          }
      );
    }
  }
}
