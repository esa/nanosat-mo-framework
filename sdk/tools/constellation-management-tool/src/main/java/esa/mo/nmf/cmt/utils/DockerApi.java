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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Docker API for simulating NanoSat segments.
 */
public class DockerApi extends SimulatorApi {

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
    public void run(String name, String[] keplerElements) throws IOException {
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
