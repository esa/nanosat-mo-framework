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
package esa.mo.nmf.cmt;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * NanoSat Segment Simulator Object.
 * This object simulates the NanoSat Segment of the NMF. It is responsible for
 * managing the Docker Container in which the NanoSat Segment is executed.
 */
public class NanoSatSimulator extends NanoSat {
    private final String image = "nmf-rpi/nanosat-segment:1.0"; // TODO: get image name from config
    private String[] keplerElements = null;

    /**
     * Initializer Constructor.
     * This class manages the Docker Container which provides the NanoSat
     * segment for the simulated constellation.
     *
     * @param name Container name
     */
    public NanoSatSimulator(String name) {
        this.name = name;
    }

    /**
     * Initializer Constructor.
     * This class manages the Docker Container which provides the NanoSat
     * segment for the simulated constellation.
     *
     * @param name           Container name
     * @param keplerElements Orbit parameters for GPS simulation
     */
    public NanoSatSimulator(String name, String[] keplerElements) {
        this.name = name;
        this.keplerElements = keplerElements;
    }

    /**
     * Create a Docker Container that will host the NanoSat Segment and start it.
     *
     * @throws IOException
     */
    @Override
    public void run() throws IOException {
        DockerApi.run(this.name, this.image, this.keplerElements);
    }

    /**
     * Start the Docker Container that hosts the NanoSat Segment.
     *
     * @throws IOException
     */
    public void start() throws IOException {
        DockerApi.start(this.name);
    }

    /**
     * Stop the Docker Container that hosts the NanoSat Segment.
     *
     * @throws IOException
     */
    public void stop() throws IOException {
        DockerApi.stop(this.name);
    }

    /**
     * @return Container IP Address
     * @throws IOException
     */
    @Override
    public String getIPAddress() throws IOException {
        if (this.ipAddress == null) {
            this.ipAddress = DockerApi.getContainerIPAddress(this.name);
        }
        return this.ipAddress;
    }

    /**
     * @param ipAddress
     */
    @Override
    public void setIPAddress(String ipAddress) {
        // do nothing - don't change container IP Address
    }

    /**
     * Deletes the Docker container and sets the NanoSat segment inactive.
     */
    @Override
    public void delete() {
        this.active = false;
        this.providers = null;
        this.groundAdapter = null;
        try {
            DockerApi.removeContainer(this.name);
        } catch (IOException ex) {
            Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.SEVERE, "{0}: could not be removed!: {1}", new Object[]{this.name, ex});
        }
    }

    /**
     * Deletes the Docker container and sets the NanoSat segment inactive.
     */
    @Override
    public void deleteIfSimulation() {
        this.delete();
    }
}
