/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2026      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 *  You may not use this file except in compliance with the License.
 *
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ----------------------------------------------------------------------------
 */
package opssat.simulator.threading;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import opssat.simulator.util.ArgumentTemplate;
import opssat.simulator.util.CommandDescriptor;
import opssat.simulator.util.SimulatorHeader;
import opssat.simulator.util.SimulatorSchedulerPiece;

/**
 * Handles loading and persisting SimulatorNode configuration (header, filter, scheduler, templates).
 */
class ConfigurationManager {

    private final SimulatorNode node;

    ConfigurationManager(SimulatorNode node) {
        this.node = node;
    }

    void loadSimulatorHeader() {
        File headerFile = node.getHeaderFile();
        if (headerFile.exists()) {
            node.logger.log(Level.FINE, "Header [" + headerFile.toString() + "] found!");
            boolean dataOk = true;
            node.simulatorHeader = new SimulatorHeader();
            try {
                BufferedReader in = new BufferedReader(new FileReader(headerFile.getAbsolutePath()));
                String line;
                label:
                while ((line = in.readLine()) != null) {

                    List<String> items = Arrays.asList(line.split("="));
                    if (line.startsWith("#")) {
                        // nothing to be done
                    } else if (items.size() != 2) {
                        node.logger.log(Level.SEVERE, "Read from header file: size of line [" + line
                                + "]  was invalid!");
                        dataOk = false;
                        break;
                    } else {
                        String fieldName, fieldValue;
                        fieldName = items.get(0);
                        fieldValue = items.get(1);

                        switch (fieldName) {
                            case "startModels":
                                node.simulatorHeader.setAutoStartSystem(Boolean.parseBoolean(fieldValue));
                                break;
                            case "startTime":
                                node.simulatorHeader.setAutoStartTime(Boolean.parseBoolean(fieldValue));
                                break;
                            case "timeFactor":
                                int newTimeFactor = Integer.parseInt(fieldValue);
                                if (node.simulatorHeader.validateTimeFactor(newTimeFactor)) {
                                    node.simulatorHeader.setTimeFactor(newTimeFactor);
                                } else {
                                    node.logger.log(Level.SEVERE, "Read from header file: timeFactor is invalid!");
                                    dataOk = false;
                                    break label;
                                }
                                break;
                            case "startDate":
                                Date startDate = node.simulatorHeader.parseStringIntoDate(fieldValue);
                                if (startDate != null) {
                                    node.simulatorHeader.setStartDate(startDate);
                                } else {
                                    node.logger.log(Level.SEVERE, "Read from header file: startDate is invalid!");
                                    dataOk = false;
                                    break label;
                                }
                                break;
                            case "endDate":
                                Date endDate = node.simulatorHeader.parseStringIntoDate(fieldValue);
                                if (endDate != null) {
                                    node.simulatorHeader.setEndDate(endDate);
                                } else {
                                    node.logger.log(Level.SEVERE, "Read from header file: endDate is invalid!");
                                    dataOk = false;
                                    break label;
                                }
                                break;
                            case "keplerElements":
                                node.simulatorHeader.setKeplerElements(String.valueOf(fieldValue));
                                break;
                            case "orekit":
                                node.simulatorHeader.setUseOrekitPropagator(Boolean.parseBoolean(fieldValue));
                                break;
                            case "orekitPropagator":
                                node.simulatorHeader.setOrekitPropagator(String.valueOf(fieldValue));
                                break;
                            case "orekitTLE1": {
                                String tempResult = String.valueOf(fieldValue);
                                node.simulatorHeader.setOrekitTLE1(tempResult.substring(1, tempResult.length() - 1));
                                break;
                            }
                            case "orekitTLE2": {
                                String tempResult = String.valueOf(fieldValue);
                                node.simulatorHeader.setOrekitTLE2(tempResult.substring(1, tempResult.length() - 1));
                                break;
                            }
                            case "celestia":
                                node.simulatorHeader.setUseCelestia(Boolean.parseBoolean(fieldValue));
                                break;
                            case "celestiaPort":
                                node.simulatorHeader.setCelestiaPort(Integer.parseInt(fieldValue));
                                break;
                            case "updateFromInternet":
                                node.simulatorHeader.setUpdateInternet(Boolean.parseBoolean(fieldValue));
                                break;
                        }
                    }
                }
                if (!node.simulatorHeader.checkStartBeforeEnd()) {
                    node.logger.log(Level.SEVERE, "Read from header file: startDate is not before endDate");
                    dataOk = false;
                }
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (!dataOk) {
                node.logger.log(Level.FINE, "Data from header file was invalid!");
                initializeHeader(headerFile);
            }
        } else {
            node.logger.log(Level.FINE, "Header file was not found!");
            initializeHeader(headerFile);
        }
    }

    void loadSimulatorCommandsFilter() {
        File filterFile = node.getCommandsFilterFile();
        if (filterFile.exists()) {
            node.logger.log(Level.FINE, "Filter [" + filterFile.toString() + "] found!");
            boolean dataOk = true;

            try {
                BufferedReader in = new BufferedReader(new FileReader(filterFile.getAbsolutePath()));
                String line;
                while ((line = in.readLine()) != null) {
                    List<String> items = Arrays.asList(line.split(" "));
                    if (line.startsWith("#")) {
                        // comment line
                    } else if (items.size() != 1) {
                        node.logger.log(Level.SEVERE, "Read from filter file: size of line [" + line
                                + "]  was invalid!");
                        dataOk = false;
                        break;
                    } else {
                        String fieldName = items.get(0);
                        int test = 0;
                        try {
                            test = Integer.parseInt(fieldName);
                        } catch (NumberFormatException ex) {
                            node.logger.log(Level.SEVERE, ex.toString());
                        }
                        if (checkInternalIDExists(test)) {
                            node.logger.log(Level.FINE, "Found valid command ID [" + fieldName + "]");
                            CommandDescriptor c = getCommandDescriptorForID(Integer.parseInt(fieldName));
                            c.setVisible(true);
                        }
                    }
                }
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (!dataOk) {
                node.logger.log(Level.FINE, "Data from filter file was invalid!");
                initializeFilter(filterFile);
            }
        } else {
            node.logger.log(Level.FINE, "Filter file was not found!");
        }
    }

    void loadSchedulerFromFile(final File folder) {
        BufferedWriter outScheduler = null;
        File schedulerFile = null;
        node.schedulerData = new LinkedList<>();
        boolean errorsExist = false;
        boolean minorErrorsExist = false;
        boolean sortingRequired = false;
        long currentTime = 0;
        if (folder.exists()) {
            int customSchedulerCommands = 0;
            try {
                BufferedReader in = new BufferedReader(new FileReader(folder.getAbsolutePath()));

                schedulerFile = node.getSchedulerFileAsBackup();
                try {
                    outScheduler = new BufferedWriter(new FileWriter(schedulerFile.getAbsolutePath()));
                } catch (IOException ex) {
                    Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
                }

                String line;
                while ((line = in.readLine()) != null) {
                    outScheduler.write(line + "\n");
                    if (!line.startsWith("#")) {
                        List<String> items = Arrays.asList(line.split("[" + CommandDescriptor.SEPARATOR_DATAFILES
                                + "]"));
                        if (items.size() == 4) {
                            long def1Value = SimulatorSchedulerPiece.getMillisFromDDDDDHHMMSSmmm(items.get(0));
                            long def2Value = -1;
                            try {
                                def2Value = Long.parseLong(items.get(1));
                            } catch (NumberFormatException ex) {
                                Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            boolean rowOK = def1Value >= 0 && !(def1Value == 0 && def2Value > 0);
                            if (!rowOK) {
                                if (def2Value >= 0) {
                                    def1Value = def2Value;
                                    rowOK = true;
                                    minorErrorsExist = true;
                                }
                            }
                            if (def1Value == def2Value) {
                                // time definition is consistent
                            } else {
                                def2Value = def1Value;
                                minorErrorsExist = true;
                            }
                            int internalID = 0;
                            if (rowOK) {
                                if (def1Value < currentTime) {
                                    sortingRequired = true;
                                } else {
                                    currentTime = def1Value;
                                }
                                try {
                                    internalID = Integer.parseInt(items.get(2));
                                } catch (NumberFormatException ex) {
                                    Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
                                    rowOK = false;
                                }
                                rowOK = rowOK && checkInternalIDExists(internalID);
                                if (rowOK) {
                                    rowOK = checkArgumentTemplateExists(internalID, items.get(3));
                                    if (!rowOK) {
                                        node.logger.log(Level.FINE, "argumentTemplate [" + items.get(3)
                                                + "] does not exist");
                                    }
                                } else {
                                    node.logger.log(Level.FINE, "internalID [" + internalID + "] does not exist");
                                }
                            }
                            if (!rowOK) {
                                node.logger.log(Level.WARNING, "Validation1 error with row [" + line + "]");
                                errorsExist = true;
                            } else {
                                customSchedulerCommands++;
                                node.schedulerData.add(new SimulatorSchedulerPiece(def2Value, internalID,
                                        items.get(3)));
                            }
                        } else {
                            node.logger.log(Level.WARNING, "Validation2 error with row [" + line + "]");
                            errorsExist = true;
                        }
                    }
                }
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
            }
            node.logger.log(Level.FINE, "Loaded [" + customSchedulerCommands + "] scheduler commands");
        } else {
            node.logger.log(Level.WARNING, "Scheduler file [" + folder.getName() + "] not found!");
            initializeScheduler();
        }
        if (errorsExist || minorErrorsExist || sortingRequired) {
            node.logger.log(Level.WARNING, "Errors were found during scheduler file parsing!");
            if (minorErrorsExist) {
                node.logger.log(Level.WARNING, "Some time fields were not consistent.");
            }
            if (sortingRequired) {
                node.logger.log(Level.WARNING, "Sorting of the scheduler entries is required.");
            }
            node.schedulerData.sort(Comparator.comparingLong(SimulatorSchedulerPiece::getTime));
            writeSchedulerToFile(node.schedulerData);
            printSchedulerData();
        } else {
            node.logger.log(Level.FINE, "Scheduler file parsing ok!");
            if (schedulerFile != null) {
                try {
                    outScheduler.close();
                    outScheduler = null;
                    schedulerFile.delete();
                } catch (IOException ex) {
                    Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if (outScheduler != null) {
            try {
                outScheduler.close();
            } catch (IOException ex) {
                Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void loadTemplatesFromFile(final File folder) {
        if (folder.exists()) {
            int customTemplates = 0;
            try {
                BufferedReader in = new BufferedReader(new FileReader(folder.getAbsolutePath()));
                String line;
                while ((line = in.readLine()) != null) {
                    if (!line.startsWith("#")) {
                        List<String> items = Arrays.asList(line.split("[" + CommandDescriptor.SEPARATOR_DATAFILES
                                + "]"));
                        if (items.size() == 3) {
                            ArgumentTemplate template = new ArgumentTemplate(items.get(1), items.get(2));
                            if (!putCustomTemplateInCollection(Integer.parseInt(items.get(0)), template)) {
                                node.logger.log(Level.WARNING, "Error finding internal id [" + items.get(0)
                                        + "] in row [" + line + "]");
                            } else {
                                customTemplates++;
                            }
                        } else {
                            node.logger.log(Level.WARNING, "Error reading template file row [" + line + "]");
                        }
                    }
                }
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
            }
            node.logger.log(Level.FINE, "Loaded [" + customTemplates + "] custom templates");
        } else {
            node.logger.log(Level.WARNING, "Templates file [" + folder.getName() + "] not found!");
            initializeTemplates();
        }
    }

    void writeHeader(File headerFile) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(headerFile.getAbsolutePath()));
            out.write(node.simulatorHeader.toFileString());
        } catch (IOException ex) {
            Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void writeFilter(File filterFile) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(filterFile.getAbsolutePath()));
        } catch (IOException ex) {
            Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void writeSchedulerToFile(Object obj) {
        BufferedWriter outScheduler = null;
        File schedulerFile = node.getSchedulerFile();
        try {
            outScheduler = new BufferedWriter(new FileWriter(schedulerFile.getAbsolutePath()));
        } catch (IOException ex) {
            Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            outScheduler.write("#Simulator scheduler data file\n");
            outScheduler.write(
                    "#The commands below will be executed when simulator time has exceeded the defined time\n");
            outScheduler.write(
                    "#There are two possible ways to define the command, either as a DDDDD:HH:MM:SS:mmm format or directly in milliseconds value\n");
            outScheduler.write(
                    "#The simulator shall check the agreement between the two fields, if not equal, the DDDDD:HH:MM:SS:mmm will be used\n");
            outScheduler.write("#If the DDDDD:HH:MM:SS:mmm is zero, the milliseconds value will be used\n");
            outScheduler.write("#The simulator shall also sort the data file chronologically ascending\n");
            outScheduler.write(
                    "#days:hours:minutes:seconds:milliseconds|milliseconds|internalID|argument_template_name\n");
            outScheduler.write("#00000:00:00:00:000|0000000000000000000|1001|CUSTOM\n");
        } catch (IOException ex) {
            Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (obj != null) {
            for (SimulatorSchedulerPiece s : node.schedulerData) {
                try {
                    outScheduler.write(s.getFileString() + "\n");
                } catch (IOException ex) {
                    Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        try {
            outScheduler.close();
        } catch (IOException ex) {
            Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    void writeTemplatesToFile(Object obj) {
        BufferedWriter outTemplates = null;
        File templatesFile = node.getTemplatesFile();
        try {
            outTemplates = new BufferedWriter(new FileWriter(templatesFile.getAbsolutePath()));
        } catch (IOException ex) {
            Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (obj != null) {
            LinkedList<CommandDescriptor> castedObj = (LinkedList<CommandDescriptor>) obj;
            for (CommandDescriptor c : castedObj) {
                for (ArgumentTemplate t : c.getTemplateList()) {
                    if (!t.getDescription().equals(CommandDescriptor.KEYWORD_DEFAULT)) {
                        writeTemplate(c.getInternalID() + CommandDescriptor.SEPARATOR_DATAFILES + t.toString(),
                                outTemplates);
                    }
                }
            }
            try {
                outTemplates.close();
            } catch (IOException ex) {
                Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                outTemplates.write("#<internalID>|<templateName>|<inputArgs=[String inputArgument={data}>]\n");
                outTemplates.write("#2001|GLMLA|inputArgs=[String inputSentence={GLMLA}]");
            } catch (IOException ex) {
                Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                outTemplates.close();
            } catch (IOException ex) {
                Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void initializeFilter(File filterFile) {
        node.logger.log(Level.FINE, "initializeFilter");
        writeFilter(filterFile);
    }

    void initializeHeader(File headerFile) {
        node.logger.log(Level.FINE, "initializeHeader");
        node.simulatorHeader = new SimulatorHeader();
        writeHeader(headerFile);
    }

    void initializeTemplates() {
        node.logger.log(Level.FINE, "initializeTemplates");
        writeTemplatesToFile(null);
    }

    void initializeScheduler() {
        node.logger.log(Level.FINE, "initializeScheduler");
        writeSchedulerToFile(null);
    }

    private void writeTemplate(String data, BufferedWriter out) {
        try {
            out.write(data + "\n");
        } catch (IOException ex) {
            Logger.getLogger(SimulatorNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void printSchedulerData() {
        node.logger.log(Level.INFO, "Printing scheduler data");
        for (SimulatorSchedulerPiece p : node.schedulerData) {
            node.logger.log(Level.FINE, p.getFileString());
        }
    }

    boolean checkInternalIDExists(int internalID) {
        for (CommandDescriptor c : node.commandsList) {
            if (c.getInternalID() == internalID) {
                return true;
            }
        }
        return false;
    }

    CommandDescriptor getCommandDescriptorForID(int internalID) {
        for (CommandDescriptor c : node.commandsList) {
            if (c.getInternalID() == internalID) {
                return c;
            }
        }
        return null;
    }

    private boolean checkArgumentTemplateExists(int internalID, String templateName) {
        for (CommandDescriptor c : node.commandsList) {
            if (c.getInternalID() == internalID) {
                for (ArgumentTemplate t : c.getTemplateList()) {
                    if (t.getDescription().equals(templateName)) {
                        return true;
                    }
                }
                break;
            }
        }
        return false;
    }

    private boolean putCustomTemplateInCollection(int internalID, ArgumentTemplate template) {
        boolean found = false;
        for (CommandDescriptor c : node.commandsList) {
            if (c.getInternalID() == internalID) {
                found = true;
                node.logger.finer("Adding template [" + template.toString() + "] to command [" + c.toString() + "]");
                c.addNewTemplate(template);
            }
        }
        return found;
    }
}
