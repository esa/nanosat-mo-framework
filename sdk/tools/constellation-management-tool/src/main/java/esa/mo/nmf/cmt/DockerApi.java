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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DockerApi {

    /**
     * Run the NanoSat Segment Docker container.
     * Configure with orbit dynamics when kepler elements are provided.
     *
     * @param name           Container name
     * @param image          Docker image name
     * @param keplerElements kepler elements for orbit dynamics simulation
     * @throws IOException
     */
    public static void run(String name, String image, String[] keplerElements) throws IOException {

        StringBuilder strBuilder = new StringBuilder();

        strBuilder.append("run ");

        if (keplerElements != null) {
            strBuilder.append(String.format("--env KEPLER_A=%s ", keplerElements[0]));
            strBuilder.append(String.format("--env KEPLER_E=%s ", keplerElements[1]));
            strBuilder.append(String.format("--env KEPLER_I=%s ", keplerElements[2]));
            strBuilder.append(String.format("--env KEPLER_RAAN=%s ", keplerElements[3]));
            strBuilder.append(String.format("--env KEPLER_ARG_PER=%s ", keplerElements[4]));
            strBuilder.append(String.format("--env KEPLER_TRUE_A=%s ", keplerElements[5]));
        }

        strBuilder.append(String.format("--name %s -h %s -d %s", name, name, image));

        executeDockerCommand(strBuilder.toString());
    }

    /**
     * Start the Docker container.
     *
     * @param name Container name
     * @throws IOException
     */
    public static void start(String name) throws IOException {
        String cmd = String.format("start %s", name);
        executeDockerCommand(cmd);
    }

    /**
     * Stop the Docker container.
     *
     * @param name Container name
     * @throws IOException
     */
    public static void stop(String name) throws IOException {
        String cmd = String.format("stop %s", name);
        executeDockerCommand(cmd);
    }

    /**
     * Return the IP address of the Docker container.
     *
     * @param name Container Name
     * @return Container IP Address
     * @throws IOException
     */
    public static String getContainerIPAddress(String name) throws IOException {
        String cmd = String.format("inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' %s", name);
        return executeDockerCommand(cmd).trim();
    }

    /**
     * Removes a Docker container.
     *
     * @param name Container Name
     * @throws IOException
     */
    public static void removeContainer(String name) throws IOException {
        stop(name);
        String cmd = String.format("rm %s", name);
        executeDockerCommand(cmd);
    }

    /**
     * Execute a Docker command via /bin/bash
     * <p>
     * TODO: check for injections, prettier return sequence
     *
     * @param command command line arguments for docker
     * @return command line output
     * @throws IOException
     */
    private static String executeDockerCommand(String command) throws IOException {
        String[] cmd = {"/bin/bash", "-c", "docker " + command};
        String cmdOutput = "";
        String line = null;

        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(cmd);

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

        // catch cmd output
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

        return cmdOutput;
    }
}
