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
package esa.mo.nmf.cmt.utils;

import java.io.BufferedReader;
import esa.mo.nmf.environment.MissionConfiguration;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Docker API for simulating NanoSat segments.
 */
public class DockerApi extends ContainerApi {

    /**
     * The network that the segments of a constellation run on. A network of its
     * own is required because the default bridge does not accept a fixed address.
     */
    private static final String NETWORK = "nmf-constellation";

    /**
     * The first two octets of the addresses on that network.
     */
    private static final String PREFIX = "172.28";

    private static final String SUBNET = PREFIX + ".0.0/16";

    /**
     * The gateway sits at the top of the subnet, which leaves the bottom of it
     * free for the nodes; taken by default, it would occupy the first address
     * and the first node could not be given it.
     */
    private static final String GATEWAY = PREFIX + ".255.254";

    /**
     * The highest node the subnet can address, the one below the gateway.
     */
    private static final int MAX_NODE = 65533;

    private final String image;

    public DockerApi(String image) {
        this.image = image;
    }

    /**
     * Run the NanoSat Segment Docker container. Configure with orbit dynamics
     * when kepler elements are provided.
     *
     * @param name Container name
     * @param keplerElements kepler elements for orbit dynamics simulation
     * @throws IOException
     */
    @Override
    public void run(String name, String[] keplerElements, int spacecraftNode) throws IOException {
        ensureNetwork();
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("docker run ");

        if (keplerElements != null) {
            strBuilder.append(String.format("--env KEPLER_A=%s ", keplerElements[0]));
            strBuilder.append(String.format("--env KEPLER_E=%s ", keplerElements[1]));
            strBuilder.append(String.format("--env KEPLER_I=%s ", keplerElements[2]));
            strBuilder.append(String.format("--env KEPLER_RAAN=%s ", keplerElements[3]));
            strBuilder.append(String.format("--env KEPLER_ARG_PER=%s ", keplerElements[4]));
            strBuilder.append(String.format("--env KEPLER_TRUE_A=%s ", keplerElements[5]));
        }

        // The units of a constellation are one mission built from one image, so
        // each is told which spacecraft it is; without it they share a domain.
        strBuilder.append(String.format("--env %s=true ", MissionConfiguration.ENV_MISSION_FLEET));
        strBuilder.append(String.format("--env %s=%d ", MissionConfiguration.ENV_SPACECRAFT_NODE, spacecraftNode));

        // The address of a node carries its number, so that the node is reachable
        // at an address known before it is started.
        strBuilder.append(String.format("--network %s --ip %s ", NETWORK, addressOf(spacecraftNode)));

        strBuilder.append(String.format("--name %s -h %s -d %s", name, name, this.image));

        String output = executeCommand(strBuilder.toString());

        if (output.contains("command not found")) {
            throw new IOException("Please install docker Docker before running the code.");
        }

        Logger.getLogger(DockerApi.class.getName()).log(Level.INFO, "The output is: {0}", output);

        if (output.contains("permission denied")) {
            throw new IOException("Please enable permissions for the user running the code. "
                    + "Please check it online but usually is something like:\n"
                    + "\nsudo groupadd docker"
                    + "\nsudo usermod -aG docker $USER"
                    + "\nRestart the machine"
                    + "\nTest with: docker run hello-world");
        }

        if (output.contains("Unable to find image")) {
            throw new IOException(output);
        }
    }

    /**
     * Returns the address that a node of the constellation is given.
     * <p>
     * The address carries the node number so that a segment can be addressed
     * from its number alone, without the container being interrogated for it.
     *
     * @param spacecraftNode The node number, counting from 1.
     * @return The address of that node.
     */
    public static String addressOf(int spacecraftNode) {
        if (spacecraftNode < 1 || spacecraftNode > MAX_NODE) {
            throw new IllegalArgumentException("The node number must be between 1 and "
                    + MAX_NODE + ", but it is: " + spacecraftNode);
        }
        // Nodes above 255 continue into the third octet rather than the subnet
        // being exhausted at the end of the fourth.
        return String.format("%s.%d.%d", PREFIX, spacecraftNode / 256, spacecraftNode % 256);
    }

    /**
     * Creates the network of the constellation, if it does not exist yet.
     *
     * @throws IOException if the network could not be created.
     */
    private static void ensureNetwork() throws IOException {
        String cmd = String.format("docker network ls --filter name=^%s$ --format '{{.Name}}'",
                NETWORK);

        if (NETWORK.equals(executeCommand(cmd).trim())) {
            return;
        }

        executeCommand(String.format("docker network create --subnet %s --gateway %s %s",
                SUBNET, GATEWAY, NETWORK));
        Logger.getLogger(DockerApi.class.getName()).log(Level.INFO,
                "Created the network of the constellation: {0}", NETWORK);
    }

    /**
     * Start the Docker container.
     *
     * @param name Container name
     * @throws IOException
     */
    @Override
    public void start(String name) throws IOException {
        String cmd = String.format("docker start %s", name);
        String output = executeCommand(cmd);
        Logger.getLogger(DockerApi.class.getName()).log(Level.INFO, "The output is: {0}", output);
    }

    /**
     * Stop the Docker container.
     *
     * @param name Container name
     * @throws IOException
     */
    @Override
    public void stop(String name) throws IOException {
        String cmd = String.format("docker stop %s", name);
        executeCommand(cmd);
    }

    /**
     * Return the IP address of the Docker container.
     *
     * @param name Container Name
     * @return Container IP Address
     * @throws IOException
     */
    @Override
    public String getIPAddress(String name) throws IOException {
        String cmd = String.format("docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' %s", name);
        return executeCommand(cmd).trim();
    }

    /**
     * Remove a Docker container.
     *
     * @param name Container Name
     * @throws IOException
     */
    @Override
    public void remove(String name) throws IOException {
        stop(name);
        String cmd = String.format("docker rm %s", name);
        executeCommand(cmd);
    }

    /**
     * Return the logs that latest 128 lines of container logs.
     *
     * @param name Container Name
     * @return Container Logs
     * @throws IOException
     */
    @Override
    public String getLogs(String name) throws IOException {
        // docker logs mixes stdout and stderr in a pretty messy way.
        // to catch both outputs in the correct order, the temp.file solution is required.
        String cmd = String.format("docker logs -n 128 %s &> .logs.temp", name);
        executeCommand(cmd);
        cmd = "cat .logs.temp";
        return executeCommand(cmd);
    }

    /**
     * Execute a command via /bin/bash
     * <p>
     * TODO: check for injections, prettier return sequence
     *
     * @param command command line arguments for docker
     * @return command line output
     * @throws IOException
     */
    private static String executeCommand(String command) throws IOException {
        String[] cmd = {"/bin/bash", "-c", command};
        String cmdOutput = "";
        String line = null;

        try {
            ProcessBuilder builder = new ProcessBuilder(cmd);
            Process p = builder.start();

            try {
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                while ((line = stdInput.readLine()) != null) {
                    cmdOutput += line + "\n";
                }

                // try to catch any errors
                if (cmdOutput.equals("")) {
                    while ((line = stdError.readLine()) != null) {
                        cmdOutput += line + "\n";
                    }

                    // if there were errors
                    if (!cmdOutput.equals("")) {
                        throw new IOException(cmdOutput);
                    }
                }

            } catch (Exception ex) {
            }

            p.waitFor();

        } catch (IOException | InterruptedException e) {
            // Process failed;  do not attempt to continue!
            throw new IOException(e);
        }

        return cmdOutput;
    }
}
