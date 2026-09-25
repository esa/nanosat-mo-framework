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
import esa.mo.nmf.cmt.ConstellationManagementTool;
import esa.mo.nmf.environment.MissionConfiguration;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
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

    /**
     * The name in the environment that tells a segment where Celestia is.
     * <p>
     * It is the name the simulator reads, written out here rather than shared
     * with it: the constellation is run by whoever holds it, and is not built
     * against the mission a segment happens to run.
     */
    private static final String ENV_CELESTIA_HOST = "CELESTIA_HOST";

    /**
     * The image that answers for the constellation. Built from the swarms
     * proxy: mvn -pl nmf-mission-swarms/swarms-proxy install -Pdocker
     */
    private static final String DIRECTORY_IMAGE = "nmf-constellation-directory";

    /**
     * Where that one answers, which is the address below the gateway.
     * <p>
     * It is not a node and is not numbered among them: the nodes are given the
     * bottom of the subnet and count upwards, so the top of it is free. The
     * address is fixed so that the constellation is always found at the same
     * place.
     */
    private static final String DIRECTORY_ADDRESS = PREFIX + ".255.253";

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
        strBuilder.append(String.format("--env %s=%s ", MissionConfiguration.ENV_SPACECRAFT_NAME,
                spacecraftName(name)));

        // The address of a node carries its number, so that the node is reachable
        // at an address known before it is started.
        strBuilder.append(String.format("--network %s --ip %s ", NETWORK, addressOf(spacecraftNode)));

        // Celestia runs on the machine that holds the constellation, which a
        // segment reaches at the gateway of its network. Left to itself the
        // simulator dials loopback, which within a container of its own reaches
        // nothing but that container.
        strBuilder.append(String.format("--env %s=%s ", ENV_CELESTIA_HOST, GATEWAY));

        strBuilder.append(String.format("--name %s -h %s -d %s", name, name, this.image));

        String output;

        try {
            output = executeCommand(strBuilder.toString());
        } catch (IOException ex) {
            throw explain(ex);
        }

        Logger.getLogger(DockerApi.class.getName()).log(Level.FINE, "Docker said: {0}", output);
    }

    /**
     * Says what is to be done about a segment that could not be run.
     * <p>
     * Docker says what happened; the few failures that are the machine's to put
     * right are answered here with what to do about them, and everything else
     * is passed on as Docker told it.
     *
     * @param failure What Docker said.
     * @return The failure to raise instead.
     */
    private static IOException explain(IOException failure) {
        String said = String.valueOf(failure.getMessage());

        if (said.contains("command not found")) {
            return new IOException("Docker is not installed on this machine, and the segments of "
                    + "a constellation are containers it runs.");
        }

        if (said.contains("permission denied") || said.contains("Permission denied")) {
            return new IOException("The user running this is not allowed to use Docker. "
                    + "Usually:\n"
                    + "    sudo groupadd docker\n"
                    + "    sudo usermod -aG docker $USER\n"
                    + "then log in again, and test with: docker run hello-world");
        }

        if (said.contains("Unable to find image")) {
            return new IOException("The image of this segment has not been built on this machine. "
                    + "Build it with:\n"
                    + "    mvn -pl <mission>/<module> install -Pdocker");
        }
        return failure;
    }

    /**
     * Returns the segments this machine holds, running or stopped.
     * <p>
     * They are looked for by the name every segment carries, rather than by
     * anything this tool remembers: the segments of a constellation outlive the
     * run of the tool that raised them, and it is the machine that holds them.
     *
     * @return Their names, in the order Docker gives them.
     * @throws IOException if Docker could not be asked.
     */
    @Override
    public List<String> segments() throws IOException {
        String output = executeCommand(String.format(
                "docker ps -a --filter name=^%s --format '{{.Names}}'",
                ConstellationManagementTool.SEGMENT_PREFIX));

        List<String> segments = new ArrayList<>();

        for (String line : output.split("\n")) {
            String name = line.trim();

            if (!name.isEmpty()) {
                segments.add(name);
            }
        }
        return segments;
    }

    /**
     * Raises the container that answers for the whole constellation.
     * <p>
     * It is a server and runs as one, beside the segments rather than inside
     * whatever raised them: its logs are its own, the COM Archive it brings
     * with it is written inside it, and it is taken down with the segments.
     *
     * @param name The name to give the container.
     * @param nodes The Directory service of each segment, as the segments
     * themselves advertise it.
     * @return The address its Directory service will answer at.
     * @throws IOException if the container could not be run.
     */
    public static String runConstellationDirectory(String name, List<String> nodes)
            throws IOException {
        ensureNetwork();

        StringBuilder strBuilder = new StringBuilder("docker run ");
        strBuilder.append(String.format("--network %s --ip %s ", NETWORK, DIRECTORY_ADDRESS));
        strBuilder.append(String.format("--name %s -h %s -d %s", name, name, DIRECTORY_IMAGE));

        for (String node : nodes) {
            strBuilder.append(" ").append(node);
        }

        String output;

        try {
            output = executeCommand(strBuilder.toString());
        } catch (IOException ex) {
            if (String.valueOf(ex.getMessage()).contains("Unable to find image")) {
                // Said here rather than by explain(), which knows only that an
                // image is missing: this one is built from a module of the
                // framework, and saying which spares the reader the search.
                throw new IOException("The image that answers for a constellation has not been "
                        + "built on this machine. Build it with:\n"
                        + "    mvn -pl nmf-mission-swarms/swarms-proxy install -Pdocker\n"
                        + "and raise the constellation again. The segments themselves need "
                        + "nothing: they are up, and each is reachable at its own address.");
            }
            throw explain(ex);
        }

        Logger.getLogger(DockerApi.class.getName()).log(Level.FINE, "Docker said: {0}", output);
        return String.format("maltcp://%s:1024/constellation-directory-Directory", DIRECTORY_ADDRESS);
    }

    /**
     * Removes the container that answers for the constellation.
     * <p>
     * One that was never raised is not an error: the constellation may have
     * been raised before this container existed, or the image may not be
     * built on this machine.
     *
     * @param name The name it was given.
     * @throws IOException if it is there and could not be removed.
     */
    public static void removeConstellationDirectory(String name) throws IOException {
        try {
            executeCommand(String.format("docker rm -f %s", name));
        } catch (IOException ex) {
            ignoreIfGone(ex, name);
        }
    }

    /**
     * Returns the name the spacecraft of a segment carries.
     * <p>
     * A segment is a container as well as a spacecraft, and the two are named
     * for different readers: the container carries what tells it apart from
     * everything else the machine runs, and the spacecraft only what it was
     * asked to be called.
     *
     * @param name The name of the container.
     * @return The name of the spacecraft within it.
     */
    private static String spacecraftName(String name) {
        return name.startsWith(ConstellationManagementTool.SEGMENT_PREFIX)
                ? name.substring(ConstellationManagementTool.SEGMENT_PREFIX.length()) : name;
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
        Logger.getLogger(DockerApi.class.getName()).log(Level.FINE, "Docker said: {0}", output);
    }

    /**
     * Stop the Docker container.
     *
     * @param name Container name
     * @throws IOException
     */
    @Override
    public void stop(String name) throws IOException {
        try {
            executeCommand(String.format("docker stop %s", name));
        } catch (IOException ex) {
            ignoreIfGone(ex, name);
        }
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

        try {
            executeCommand(String.format("docker rm %s", name));
        } catch (IOException ex) {
            ignoreIfGone(ex, name);
        }
    }

    /**
     * Lets a segment that is no longer there pass for one that has been dealt
     * with.
     * <p>
     * A segment is removed by whoever is rid of it first: the constellation
     * clearing itself, and the shutdown hook the segment registered, both reach
     * for the same container, and the second finds it gone.
     *
     * @param failure What Docker said.
     * @param name The segment it was asked about.
     * @throws IOException if the segment is still there and something else was
     * the matter.
     */
    private static void ignoreIfGone(IOException failure, String name) throws IOException {
        String said = String.valueOf(failure.getMessage());

        if (said.contains("No such container") || said.contains("is already in progress")) {
            Logger.getLogger(DockerApi.class.getName()).log(Level.FINE,
                    "This segment was already gone: {0}", name);
            return;
        }
        throw failure;
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
     * Runs a command through /bin/bash and returns what it said.
     * <p>
     * What the command wrote, on either stream, comes back as one piece of
     * text: Docker answers on the one and explains itself on the other, and
     * which it uses is no business of the caller's.
     * <p>
     * A command that fails is an error rather than an empty answer. Docker
     * says why it failed and then exits non-zero, and a tool that reads only
     * the words it expects takes a refusal for a success: a segment that was
     * never started was once announced at an address it never had.
     * <p>
     * TODO: check for injections
     *
     * @param command The command line to run.
     * @return What it wrote, both streams together.
     * @throws IOException if it could not be run, or ended non-zero. The
     * message carries the command and what it said.
     */
    // Visible for testing: the tests run commands of their own through it.
    static String executeCommand(String command) throws IOException {
        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command);
        builder.redirectErrorStream(true);

        Process process = builder.start();
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        int status;

        try {
            status = process.waitFor();
        } catch (InterruptedException ex) {
            // Whoever is waiting for this is going away; the command is left to
            // finish on its own rather than the interruption being swallowed.
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for: " + command, ex);
        }

        if (status != 0) {
            throw new IOException("This command ended with " + status + ": " + command
                    + "\n" + output.toString().trim());
        }
        return output.toString();
    }
}
