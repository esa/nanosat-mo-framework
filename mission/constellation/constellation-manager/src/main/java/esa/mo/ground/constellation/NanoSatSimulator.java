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

import org.ccsds.moims.mo.mal.structures.URI;

import java.io.IOException;

/**
 * NanoSat Segment Simulator Object
 * This object simulates the NanoSat Segment of the NMF. It is responsible for 
 * managing the Docker Container in which the NanoSat Segment is executed.
 * 
 */
public class NanoSatSimulator 
{    
    private final String image = "nmf-rpi/nmf-supervisor"; // TODO: get image name from config

    private String name;

    /**
     * Initialiser Constructor. 
     * This Class manages the Docker Container which provides the NanoSat 
     * Segment for the simulated constellation.
     * 
     * @param name Container name
     * @param dockerClient Docker Unix Socket 
     */
    public NanoSatSimulator(String name)
    {
        this.name = name;

    }

    /**
     * Create a Docker Container that will host the NanoSat Segment and start it.
     * 
     * @throws IOException
     */
    public void run() throws IOException
    {
        DockerApi.run(this.name, this.image);
    }

    /**
     * Start the Docker Container that will host the NanoSat Segment
     * @throws IOException
     */
    public void start() throws IOException
    {  
        DockerApi.start(this.name);
    }

    /**
     * 
     * @return Supervisor Directory Service URI
     * @throws IOException
     */
    public URI getDirectoryServiceURI() throws IOException
    {
        return new URI(this.getDirectoryServiceURIString());
    }

    /**
     * 
     * @return Supervisor Directory Service URI String
     * @throws IOException
     */
    public String getDirectoryServiceURIString() throws IOException
    {
        // TODO: make this a bit prettier
        return "maltcp://" 
            + this.getIPAddress() 
            + ":1024/nanosat-mo-supervisor-Directory";
    }

    /**
     * 
     * @return Container Name
     */
    public String getName()
    {
        return name;
    }

    /**
     * 
     * @return Container IP Address
     * @throws IOException
     */
    public String getIPAddress() throws IOException
    {
        return DockerApi.getContainerIPAddress(this.name);
    }
}
