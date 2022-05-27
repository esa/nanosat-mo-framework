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
import esa.mo.platform.impl.provider.gen.ArtificialIntelligenceAdapterInterface;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Artificial Intelligence adapter for the Intel Movidius Neural Compute
 * Stick via a python script.
 *
 * @author Cesar Coelho
 */
public class ArtificialIntelligenceIntelMovidiusAdapter implements ArtificialIntelligenceAdapterInterface {

    private static final Logger LOGGER = Logger.getLogger(ArtificialIntelligenceIntelMovidiusAdapter.class.getName());
    private final File setupVarsPath;

    public ArtificialIntelligenceIntelMovidiusAdapter() throws IOException {
        // Check if Python3 is installed!
        ShellCommander shellCommander = new ShellCommander();
        String cmdPython = "python3 --version";
        String out = shellCommander.runCommandAndGetOutputMessage(cmdPython);
        String[] splits = out.split("Python ");

        if (splits.length == 0) {
            throw new IOException("The Python version could not be determined!"
                    + " The command returned: " + out);
        }

        LOGGER.log(Level.INFO, "The Python3 version is: " + splits[1]);
        String[] subVersions = splits[1].split("\\.");

        if (Integer.valueOf(subVersions[1]) < 6) {
            throw new IOException("The installed python3 version is < 3.6.0!"
                    + "\n>>>> Please update your Python version!");
        }

        OSValidator os = new OSValidator();

        // Is it Linux or Windows?
        if (os.isUnix()) {
            // Find the folder where the Intel Movidius software is installed
            String[] options = {
                "/opt/intel"
            };

            setupVarsPath = this.crawlOptions(options, "setupvars.sh");
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

            // Please install version: 2020.3
            return;
        }

        throw new IOException("The current OS is not supported: " + os.getOS());
    }

    private File crawlOptions(String[] options, String filename) throws IOException {
        for (String option : options) {
            File folder = new File(option);
            File path = this.findPathToFile(folder, filename);

            if (path != null) { // Found!
                LOGGER.log(Level.INFO, "The file was found on path:\n"
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

        // It is a directory... crawl through it
        for (File entry : path.listFiles()) {
            File file = findPathToFile(entry, toBeMatched);
            if (file != null) {
                return file;
            }
        }

        return null;
    }

    @Override
    public void setModel(String modelPath, String weightsPath) throws IOException {

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void executeInference(String inputPath, String outputPath) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String generateScriptSH(String pathIntelVar, String pythonFile) {
        StringBuilder str = new StringBuilder();
        str.append("#!/bin/bash \n\n");
        str.append("source ").append(pathIntelVar);
        str.append("\n\n");
        str.append("python3 ").append(pythonFile);
        return str.toString();
    }

    public String generateScriptBAT(String pathIntelVar, String pythonFile) {
        StringBuilder str = new StringBuilder();
        str.append(pathIntelVar);
        str.append("\n\n");
        str.append(pythonFile);
        return str.toString();
    }

}
