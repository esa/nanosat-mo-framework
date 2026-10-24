/*
 * Copyright (c) 2021 Cesar Coelho
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
package esa.mo.platform.impl.provider.adapters;

import esa.mo.helpertools.misc.OSValidator;
import esa.mo.helpertools.misc.ShellCommander;
import esa.mo.platform.impl.provider.gen.AIAdapterInterface;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The AIMovidiusAdapter adapter for the Intel Movidius Neural Compute
 * Stick via a python file. Note that this code was not developed for the Intel
 * Movidius Neural Compute Stick 2 and therefore will only work with the first
 * Intel Movidius Neural Compute Stick.
 *
 * @author Cesar Coelho
 */
public class AIMovidiusAdapter implements AIAdapterInterface {

    private static final Logger LOGGER = Logger.getLogger(AIMovidiusAdapter.class.getName());
    private static final String PYTHON_FILENAME = "drivers" + File.separator + "aiInference.py";
    private final OSValidator os = new OSValidator();
    private final File setupVarsPath;

    /**
     * Returns whether python3, which this adapter needs in order to run the
     * inference script, can be run here.
     *
     * @return true if python3 answers, false if it is not installed.
     */
    public static boolean isPython3Available() {
        String out = new ShellCommander().runCommandAndGetOutputMessage("python3 --version");
        return (out != null) && out.contains("Python ");
    }

    /**
     * Creates a new {@code AIMovidiusAdapter}.
     *
     * @throws IOException if the operation fails
     */
    public AIMovidiusAdapter() throws IOException {
        // Check if Python3 is installed!
        ShellCommander shellCommander = new ShellCommander();
        String cmdPython = "python3 --version";
        String out = shellCommander.runCommandAndGetOutputMessage(cmdPython);

        if (out == null || out.trim().isEmpty()) {
            throw new IOException("python3 was not found, which this adapter needs"
                    + " in order to run the inference script.");
        }

        String[] splits = out.split("Python ");

        if (splits.length <= 1) {
            throw new IOException("The python3 version could not be read from: " + out);
        }

        LOGGER.log(Level.FINE, "The Python3 version is: {0}", splits[1]);
        String[] subVersions = splits[1].trim().split("\\.");
        if (subVersions.length < 2) {
            throw new IOException("The python3 version could not be read from: " + out);
        }
        int minorVersion;
        try {
            minorVersion = Integer.parseInt(subVersions[1]);
        } catch (NumberFormatException ex) {
            throw new IOException("The python3 version could not be read from: " + out, ex);
        }

        if (minorVersion < 6) {
            throw new IOException("The installed python3 version is < 3.6.0!"
                    + "\n>>>> Please update your Python version!");
        }

        // Is it Linux or Mac?
        if (os.isUnix() || os.isMac()) {
            // Find the folder where the Intel Movidius software is installed
            String[] options = {
                "/opt/intel"
            };

            File file;
            try {
                file = this.crawlOptions(options, "setupvars.sh");
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "The setupvars.sh was not found!");
                file = new File("");
            }

            setupVarsPath = file;
            return;
        }

        if (os.isWindows()) {
            // Find the folder where the Intel Movidius software is installed
            String[] options = {
                "C:\\Program Files (x86)\\Intel",
                "C:\\Program Files (x86)\\IntelSWTools",
                "C:\\Program Files\\Intel",
                "C:\\Program Files\\IntelSWTools"
            };

            setupVarsPath = this.crawlOptions(options, "setupvars.bat");
            return;
        }

        throw new IOException("The current OS is not supported: " + os.getOS());
    }

    private File crawlOptions(String[] options, String filename) throws IOException {
        for (String option : options) {
            File folder = new File(option);
            File path = this.findPathToFile(folder, filename);

            if (path != null) { // Found!
                LOGGER.log(Level.FINE, "The file was found on path:\n"
                        + " >> " + path.getAbsolutePath());
                return path;
            }
        }

        throw new IOException("The file " + filename + " was not found!");
    }

    private File findPathToFile(File path, String toBeMatched) {
        if (path.isFile()) {
            return toBeMatched.equals(path.getName()) ? path : null;
        }

        File[] list = path.listFiles();

        if (list == null) {
            return null;
        }

        // It is a directory... crawl through it
        for (File entry : list) {
            File file = findPathToFile(entry, toBeMatched);
            if (file != null) {
                return file;
            }
        }

        return null;
    }

    /**
     * A fixed shell script that sources the Intel variables file named by its
     * first argument, then replaces itself with the command given in the
     * remaining arguments. Because the script text is constant and every path is
     * passed as a separate argument (never spliced into the text), the shell
     * never parses a path as code. See NMF-2026-009.
     */
    private static final String SOURCE_AND_RUN = ". \"$1\" >/dev/null 2>&1; shift; exec \"$@\"";

    /**
     * Builds the argument vector that runs the inference, with each path as its
     * own element so that it reaches python3 as a single argument.
     */
    private String[] buildInferenceCommand(String modelPath, String weightsPath,
            String inputPath, String outputPath) {
        String pathIntelVar = setupVarsPath.getAbsolutePath();
        return new String[]{"sh", "-c", SOURCE_AND_RUN, "sh", pathIntelVar,
            "python3", PYTHON_FILENAME,
            "--model_xml", modelPath,
            "--model_bin", weightsPath,
            "--input_tiles", inputPath,
            "--output_tiles", outputPath};
    }

    @Override
    public void executeInference(String modelPath, String weightsPath,
            String inputPath, String outputPath) throws IOException {
        if (!os.isUnix() && !os.isMac()) {
            throw new IOException("Unsupported OS!");
        }

        String[] cmd = buildInferenceCommand(modelPath, weightsPath, inputPath, outputPath);

        long timestampCommand = System.currentTimeMillis();
        Logger.getLogger(AIMovidiusAdapter.class.getName()).log(
                Level.INFO, "Running inference:\n   >> {0}", String.join(" ", cmd));

        ShellCommander shellCommander = new ShellCommander();
        Process p = shellCommander.runCommand(cmd, null);

        long timeout = 2; // in minutes
        Logger.getLogger(AIMovidiusAdapter.class.getName()).log(
                Level.INFO,
                "The process is running and it is expected to take some minutes!"
                + "\nThe process will timeout after " + timeout + " minutes in "
                + "case it is still not finished. Please wait...");

        try {
            boolean terminated = p.waitFor(timeout, TimeUnit.MINUTES);

            if (!terminated) {
                Logger.getLogger(AIMovidiusAdapter.class.getName()).log(Level.SEVERE,
                        "Timeout reached: The process is stuck... "
                        + "The adapter will kill the process!");

                p.destroyForcibly();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(AIMovidiusAdapter.class.getName()).log(Level.SEVERE,
                    "The thread was interrupted while waiting....", ex);
        }

        // Get the textual message from the process
        StringBuilder buffer = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                buffer.append(line).append("\n");
            }
        }

        String message = buffer.toString();
        int exitValue = p.exitValue();
        timestampCommand = System.currentTimeMillis() - timestampCommand;

        Logger.getLogger(AIMovidiusAdapter.class.getName()).log(Level.INFO,
                "The execution of the command took " + timestampCommand + " miliseconds!\n"
                + "The exit value is: " + exitValue + "\nThe output is:\n" + message);

        if (exitValue != 0) {
            Logger.getLogger(AIMovidiusAdapter.class.getName()).log(
                    Level.SEVERE, "The execution failed!");
            throw new IOException("The execution failed!");
        }
    }

    @Override
    public void doComputerVision(String jsonPath) throws IOException {
        throw new UnsupportedOperationException("The operation needs to be "
                + "extended for specific AI applications that do Computer Vision!");
    }
}
