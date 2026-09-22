/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
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
 */
package esa.mo.nmf.cmt.cli;

import esa.mo.nmf.cmt.ConstellationManagementTool;
import esa.mo.nmf.cmt.utils.ContainerApi;
import esa.mo.nmf.cmt.utils.DockerApi;
import esa.mo.nmf.cmt.utils.NanoSat;
import esa.mo.nmf.cmt.utils.NanoSatSimulator;
import esa.mo.nmf.cmt.utils.SegmentOrbits;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Raises a constellation from the command line.
 * <p>
 * Nothing here reaches for a window, so a constellation is raised the same way
 * on a workstation and on a machine that has no display at all.
 * <p>
 * The constellation lives as long as this command does: the segments are
 * removed when it is interrupted, so that a machine is not left with containers
 * nobody is watching.
 */
public class ConstellationCli {

    private static final Logger LOGGER = Logger.getLogger(ConstellationCli.class.getName());

    /**
     * The constellation was raised and has been removed again.
     */
    public static final int EXIT_OK = 0;

    /**
     * The command line did not ask for a constellation that can be raised.
     */
    public static final int EXIT_USAGE = 1;

    /**
     * The constellation was asked for but could not be raised.
     */
    public static final int EXIT_FAILED = 2;

    /**
     * Raises the constellation the arguments ask for and keeps it up until this
     * command is interrupted.
     *
     * @param args The arguments the tool was started with.
     * @return What the tool exits with.
     */
    public static int run(String[] args) {
        ConstellationOptions options;

        try {
            options = ConstellationOptions.parse(args);
        } catch (IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
            System.err.println();
            System.err.println(ConstellationOptions.usage());
            return EXIT_USAGE;
        }

        if (options.isHelp()) {
            System.out.println(ConstellationOptions.usage());
            return EXIT_OK;
        }

        if (options.getContainerTool() != null) {
            System.setProperty(ContainerApi.TOOL_PROPERTY, options.getContainerTool());
        }

        try {
            ConstellationManagementTool.checkNoConstellationIsRunning();
        } catch (IllegalStateException ex) {
            System.err.println(ex.getMessage());
            return EXIT_FAILED;
        }

        ConstellationManagementTool cmt = new ConstellationManagementTool();

        try {
            if (options.getCsv() != null) {
                Map<String, String[]> orbits = SegmentOrbits.read(options.getCsv());
                cmt.addSimulationsWithOrbits(orbits, options.getImage());
            } else {
                cmt.addBasicSimulations(options.getName(), options.getNodes(), options.getImage());
            }
        } catch (IllegalArgumentException ex) {
            System.err.println("The file does not describe a constellation that can be raised: "
                    + ex.getMessage());
            return EXIT_USAGE;
        } catch (IOException ex) {
            System.err.println("The constellation could not be raised: " + ex.getMessage());

            if (ex.toString().contains("permission denied")) {
                System.err.println("Has the user running this command been given the use of Docker?");
            }

            // The segments that did come up are of a constellation that is not
            // there, so they are taken down rather than left behind.
            cmt.removeAllSimulations();
            return EXIT_FAILED;
        }

        report(cmt);
        answerForTheConstellation(cmt);
        awaitInterruption();
        return EXIT_OK;
    }

    /**
     * The container that answers for the constellation, named so that it is
     * recognisable beside the segments and removed with them.
     */
    private static final String DIRECTORY_NAME = ConstellationManagementTool.SEGMENT_PREFIX
            + "constellation-directory";

    /**
     * Raises the one Directory service that answers for the whole
     * constellation, so that it is reached at one address rather than at one
     * address per segment.
     * <p>
     * A constellation that could not be answered for is still a constellation:
     * every segment is up and reachable at the address written out above, so a
     * failure here is said and passed over rather than taken as a failure to
     * raise it.
     *
     * @param cmt The tool holding the constellation.
     */
    private static void answerForTheConstellation(ConstellationManagementTool cmt) {
        List<String> nodes = new ArrayList<>();

        for (NanoSat nanoSat : cmt.getConstellation()) {
            try {
                nodes.add(nanoSat.getDirectoryServiceURIString());
            } catch (IOException ex) {
                System.err.println("The address of this segment could not be read, so it will not "
                        + "be in the Directory service of the constellation: " + nanoSat.getName());
            }
        }

        if (nodes.isEmpty()) {
            return;
        }

        try {
            String uri = DockerApi.runConstellationDirectory(DIRECTORY_NAME, nodes);
            LOGGER.log(Level.INFO, "The Directory service of the constellation was started in "
                    + "the container {0}. To see its log: docker logs {0}", DIRECTORY_NAME);
            System.out.println();
            System.out.println("The whole constellation is at:");
            System.out.println("    " + uri);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "The Directory service of the constellation could not be "
                    + "started, so the constellation has no one address to be reached at. Every "
                    + "segment is up and reachable at its own, written out above.\n{0}",
                    ex.getMessage());
        }
    }

    /**
     * Writes out where each segment of the constellation is to be reached.
     *
     * @param cmt The tool holding the constellation.
     */
    private static void report(ConstellationManagementTool cmt) {
        System.out.println();

        for (NanoSat nanoSat : cmt.getConstellation()) {
            String node = (nanoSat instanceof NanoSatSimulator)
                    ? String.valueOf(((NanoSatSimulator) nanoSat).getSpacecraftNode()) : "?";
            String address;

            try {
                address = nanoSat.getIPAddress();
            } catch (IOException ex) {
                address = "unknown";
            }

            String directory;

            try {
                directory = nanoSat.getDirectoryServiceURIString();
            } catch (IOException ex) {
                directory = "unknown";
            }

            System.out.println(String.format("node %-4s %-28s %-16s %s",
                    node, nanoSat.getName(), address, directory));
        }

        System.out.println();
        System.out.println("The constellation is up. Interrupt this command to remove it.");
    }

    /**
     * Holds the command until it is interrupted.
     * <p>
     * The segments are removed by the shutdown hook each of them registers, so
     * there is nothing to do here but wait for the machine to ask this command
     * to end.
     */
    private static void awaitInterruption() {
        CountDownLatch interrupted = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Removing the segments of the constellation...");

            // The one that answers for them is a container like the rest, and
            // is of no use once there is nothing left to answer for.
            try {
                DockerApi.removeConstellationDirectory(DIRECTORY_NAME);
            } catch (IOException ex) {
                System.err.println("The Directory service of the constellation could not be "
                        + "removed: " + ex.getMessage());
            }
            interrupted.countDown();
        }));

        try {
            interrupted.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private ConstellationCli() {
    }
}
