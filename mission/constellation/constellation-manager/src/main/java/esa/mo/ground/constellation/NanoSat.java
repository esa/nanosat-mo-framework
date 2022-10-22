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
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.URI;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NanoSat {
    protected String name;
    protected String ipAddress = null;
    protected ProviderSummaryList providers;
    protected GroundMOAdapterImpl groundAdapter;

    /**
     * Initializer Constructor.
     * This class manages the connection to a NanoSat segment.
     *
     * @param name NanoSat segment name
     */
    public NanoSat(String name) {
        this.name = name;
    }

    /**
     * Initializer Constructor.
     * This class manages the connection to a NanoSat segment.
     */
    public NanoSat() {
    };

    /**
     *
     * @return Container name
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @return NanoSat segment IP address
     * @throws IOException
     */
    public String getIPAddress() throws IOException {

        if (this.ipAddress == null) {
            throw new IOException("IP Address is undefined");
        }

        return this.ipAddress;
    }

    /**
     *
     * @param ipAddress NanoSat segment IP address
     */
    public void setIPAddress(String ipAddress) {
        this.ipAddress = ipAddress;
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
        return ("maltcp://" +
                this.getIPAddress() +
                ":1024/nanosat-mo-supervisor-Directory");
    }

    /**
     * Overwritten by NanoSatSimulator.run()
     * 
     * @throws IOException
     */
    public void run() throws IOException {
    }

    /**
     * Connect to the service providers on the NanoSat Segment
     */
    public void connectToProviders() {
        try {
            this.providers = GroundMOAdapterImpl.retrieveProvidersFromDirectory(
                    this.getDirectoryServiceURI());

            if (!providers.isEmpty()) {
                // Connect to provider on index 0
                this.groundAdapter = new GroundMOAdapterImpl(this.providers.get(0));
                this.groundAdapter.addDataReceivedListener(
                        new NanoSat.CompleteDataReceivedAdapter(this.getName()));
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
     * Install the given NMF package on the NanoSat Segment
     *
     * @param packageName NMF package name
     */
    public void installPackage(String packageName) {
        PackageManagerGround.installPackage(this.groundAdapter, packageName);
    }

    /**
     * Get all NMF packages that are available on the NanoSat Segment
     *
     * TODO: define return type
     */
    public void getAllPackages() {
        PackageManagerGround.getAllPackages(this.groundAdapter);
    }

    protected class CompleteDataReceivedAdapter
            extends CompleteDataReceivedListener {
        private String name;

        protected CompleteDataReceivedAdapter(String name) {
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
                            });
        }
    }

    protected class SimpleDataReceivedAdapter extends SimpleDataReceivedListener {
        private String name;

        @Override
        public void onDataReceived(String parameterName, Serializable data) {
            Logger
                    .getLogger(ConstellationManager.class.getName())
                    .log(
                            Level.INFO,
                            "\nNode: {0}\nParameter name: {1}\nParameter Value: {2}",
                            new Object[] { this.name, parameterName, data.toString() });
        }
    }
}
