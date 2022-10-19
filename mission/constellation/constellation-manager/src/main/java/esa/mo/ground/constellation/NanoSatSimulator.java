/* ----------------------------------------------------------------------------
 * Copyright (C) 2022      European Space Agency
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
 *
 * Author: N Wiegand (https://github.com/Klabau)
 */
package esa.mo.ground.constellation;

import esa.mo.ground.constellation.services.sm.PackageManagerGround;
import esa.mo.mc.impl.provider.ParameterInstance;
import esa.mo.nmf.commonmoadapter.CompleteDataReceivedListener;
import esa.mo.nmf.commonmoadapter.SimpleDataReceivedListener;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 * NanoSat Segment Simulator Object.
 * This object simulates the NanoSat Segment of the NMF. It is responsible for
 * managing the Docker Container in which the NanoSat Segment is executed.
 *
 */
public class NanoSatSimulator {
  private final String image = "nmf-rpi/nmf-supervisor"; // TODO: get image name from config

  private String name;
  private ProviderSummaryList providers;
  private GroundMOAdapterImpl groundAdapter;
  private PackageManagerGround packageManager = null;

  /**
   * Initialiser Constructor.
   * This Class manages the Docker Container which provides the NanoSat
   * Segment for the simulated constellation.
   *
   * @param name Container name
   */
  public NanoSatSimulator(String name) {
    this.name = name;
  }

  /**
   * Create a Docker Container that will host the NanoSat Segment and start it.
   *
   * @throws IOException
   */
  public void run() throws IOException {
    DockerApi.run(this.name, this.image);
  }

  /**
   * Start the Docker Container that will host the NanoSat Segment
   * @throws IOException
   */
  public void start() throws IOException {
    DockerApi.start(this.name);
  }

  /**
   * Connect to the service providers on the NanoSat Segment
   */
  public void connectToProviders() {
    try {
      this.providers =
        GroundMOAdapterImpl.retrieveProvidersFromDirectory(
          this.getDirectoryServiceURI()
        );

      if (!providers.isEmpty()) {
        // Connect to provider on index 0
        groundAdapter = new GroundMOAdapterImpl(providers.get(0));
        groundAdapter.addDataReceivedListener(
          new CompleteDataReceivedAdapter(this.getName())
        );
      } else {
        Logger
          .getLogger(ConstellationManager.class.getName())
          .log(Level.SEVERE, "The returned list of providers is empty!");
      }
    } catch (MALException | MALInteractionException | IOException ex) {
      Logger
        .getLogger(ConstellationManager.class.getName())
        .log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Get all NMF packages that are avaibalbe on the NanoSat Segment
   * 
   * TODO: define return type
   */
  public void getAllPackages() {
    if (this.packageManager == null) {
      this.packageManager = new PackageManagerGround();
    }
    PackageManagerGround.getAllPackages(this.groundAdapter);
  }

  /**
   * Install the given NMF package on the NanoSat Segment
   * 
   * @param packageName NMF package name
   */
  public void installPackage(String packageName) {
    if (this.packageManager == null) {
      this.packageManager = new PackageManagerGround();
    }
    PackageManagerGround.installPackage(this.groundAdapter, packageName);
  }

  /**
   *
   * @return Supervisor Directory Service URI
   * @throws IOException
   */
  public URI getDirectoryServiceURI() throws IOException {
    return new URI(this.getDirectoryServiceURIString());
  }

  /**
   *
   * @return Supervisor Directory Service URI String
   * @throws IOException
   */
  public String getDirectoryServiceURIString() throws IOException {
    // TODO: make this a bit prettier
    return (
      "maltcp://" +
      this.getIPAddress() +
      ":1024/nanosat-mo-supervisor-Directory"
    );
  }

  /**
   *
   * @return Container Name
   */
  public String getName() {
    return name;
  }

  /**
   *
   * @return Container IP Address
   * @throws IOException
   */
  public String getIPAddress() throws IOException {
    return DockerApi.getContainerIPAddress(this.name);
  }

  private class SimpleDataReceivedAdapter extends SimpleDataReceivedListener {
    private String name;

    @Override
    public void onDataReceived(String parameterName, Serializable data) {
      Logger
        .getLogger(ConstellationManager.class.getName())
        .log(
          Level.INFO,
          "\nNode: {0}\nParameter name: {1}\nParameter Value: {2}",
          new Object[] { this.name, parameterName, data.toString() }
        );
    }
  }

  private class CompleteDataReceivedAdapter
    extends CompleteDataReceivedListener {
    private String name;

    private CompleteDataReceivedAdapter(String name) {
      this.name = name;
    }

    @Override
    public void onDataReceived(ParameterInstance parameterInstance) {
      Logger
        .getLogger(ConstellationManager.class.getName())
        .log(
          Level.INFO,
          "\nNode: {0}\nParameter name: {1}\nParameter Value: {2}",
          new Object[] {
            this.name,
            parameterInstance.getName(),
            parameterInstance.getParameterValue().toString(),
          }
        );
    }
  }
}
