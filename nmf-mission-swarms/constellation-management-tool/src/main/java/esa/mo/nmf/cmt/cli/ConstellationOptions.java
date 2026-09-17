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

import esa.mo.nmf.cmt.utils.SegmentImage;
import java.io.File;

/**
 * What a command line asks a constellation to be.
 */
public class ConstellationOptions {

    /**
     * The name a constellation carries when the command line does not give it
     * one.
     */
    public static final String DEFAULT_NAME = "constellation";

    /**
     * The tool that runs the segments unless another is named. It is the one
     * the tool falls back to itself, and is repeated here so that the help says
     * which it is.
     */
    public static final String DEFAULT_CONTAINER_TOOL = "docker";

    private final int nodes;

    private final File csv;

    private final String name;

    private final SegmentImage image;

    private final String containerTool;

    private final boolean help;

    private ConstellationOptions(int nodes, File csv, String name, SegmentImage image,
            String containerTool, boolean help) {
        this.nodes = nodes;
        this.csv = csv;
        this.name = name;
        this.image = image;
        this.containerTool = containerTool;
        this.help = help;
    }

    /**
     * Reads what the command line asks for.
     *
     * @param args The arguments the tool was started with.
     * @return What they ask the constellation to be.
     * @throws IllegalArgumentException if they do not ask for a constellation
     * that can be raised. The message says why, and is meant to be read by
     * whoever wrote the command line.
     */
    public static ConstellationOptions parse(String[] args) {
        Integer nodes = null;
        File csv = null;
        String name = null;
        SegmentImage image = SegmentImage.getDefault();
        String containerTool = null;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if ("--help".equals(arg) || "-h".equals(arg)) {
                return new ConstellationOptions(0, null, DEFAULT_NAME, image, containerTool, true);
            }

            switch (arg) {
                case "--nodes":
                    nodes = readNodes(value(args, i++));
                    break;
                case "--csv":
                    csv = new File(value(args, i++));
                    break;
                case "--name":
                    name = readName(value(args, i++));
                    break;
                case "--image":
                    image = SegmentImage.fromOption(value(args, i++));
                    break;
                case "--container-tool":
                    containerTool = value(args, i++);
                    break;
                default:
                    throw new IllegalArgumentException("This is not an option of this tool: " + arg);
            }
        }

        if (nodes == null && csv == null) {
            throw new IllegalArgumentException("The constellation to raise is not said: give "
                    + "--nodes for segments that share an orbit, or --csv for a file that gives "
                    + "each of them its own.");
        }

        if (nodes != null && csv != null) {
            throw new IllegalArgumentException("A constellation is raised either from a number of "
                    + "segments or from a file describing them, not from both: --nodes and --csv");
        }

        if (csv != null) {
            if (name != null) {
                throw new IllegalArgumentException("The segments of a file are named by the file "
                        + "itself, so there is no name to give them: --name and --csv");
            }

            if (!csv.isFile()) {
                throw new IllegalArgumentException("This file is not there to be read: "
                        + csv.getPath());
            }
        }

        return new ConstellationOptions(nodes == null ? 0 : nodes, csv,
                name == null ? DEFAULT_NAME : name, image, containerTool, false);
    }

    /**
     * Returns the value that follows an option.
     *
     * @param args The arguments the tool was started with.
     * @param index The position of the option itself.
     * @return The value written after it.
     * @throws IllegalArgumentException if nothing follows the option.
     */
    private static String value(String[] args, int index) {
        if (index + 1 >= args.length) {
            throw new IllegalArgumentException("This option was given no value: " + args[index]);
        }
        return args[index + 1];
    }

    private static int readNodes(String value) {
        int nodes;

        try {
            nodes = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("The number of segments is not a number: " + value);
        }

        if (nodes < 1) {
            throw new IllegalArgumentException("A constellation is at least one segment, "
                    + "but this many were asked for: " + nodes);
        }
        return nodes;
    }

    /**
     * Returns the name the segments are called after.
     * <p>
     * It goes into the name of a container, so it is reduced to the letters and
     * digits of what was asked for, the way the window does it.
     *
     * @param value The name as it was written.
     * @return The name as the segments carry it.
     * @throws IllegalArgumentException if nothing of it is left.
     */
    private static String readName(String value) {
        String name = value.replaceAll("[^a-zA-Z0-9]+", "");

        if (name.isEmpty()) {
            throw new IllegalArgumentException("The name of the constellation has no letter or "
                    + "digit in it: " + value);
        }
        return name;
    }

    /**
     * @return How the tool is asked for a constellation.
     */
    public static String usage() {
        return "Usage: runCMT.sh --nodes <count> [--name <name>] [--image <" + SegmentImage.options()
                + ">] [--container-tool <docker|kubernetes>]\n"
                + "       runCMT.sh --csv <file> [--image <" + SegmentImage.options()
                + ">] [--container-tool <docker|kubernetes>]\n"
                + "       runCMT.sh --help\n"
                + "       runCMT.sh                (opens the window)\n"
                + "\n"
                + "  --nodes           How many segments the constellation is made of, all of them\n"
                + "                    flying the orbit their image was built with.\n"
                + "  --csv             A file giving each segment its own orbit, a line to a segment:\n"
                + "                    name;A[km];E;i[deg];RAAN[deg];ARG_PER[deg];TRUE_A[deg]\n"
                + "  --name            What the segments are called after, with --nodes. Default: "
                + DEFAULT_NAME + "\n"
                + "  --image           The image every segment runs. Default: "
                + SegmentImage.getDefault().getOption() + "\n"
                + "  --container-tool  What runs the segments. Default: " + DEFAULT_CONTAINER_TOOL + "\n"
                + "\n"
                + "The constellation stays up until this command is interrupted, which removes it.";
    }

    /**
     * @return How many segments were asked for, or zero when a file says which
     * they are instead.
     */
    public int getNodes() {
        return nodes;
    }

    /**
     * @return The file giving each segment its own orbit, or null when a number
     * of segments was asked for instead.
     */
    public File getCsv() {
        return csv;
    }

    public String getName() {
        return name;
    }

    public SegmentImage getImage() {
        return image;
    }

    /**
     * @return The tool that runs the segments, or null to leave the choice
     * where it is.
     */
    public String getContainerTool() {
        return containerTool;
    }

    /**
     * @return Whether the command line asks how the tool is used, rather than
     * for a constellation.
     */
    public boolean isHelp() {
        return help;
    }
}
