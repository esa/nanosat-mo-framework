/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2021      European Space Agency
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
package opssat.simulator.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cezar Suteu
 */
public class CommandDescriptor implements Serializable {

  String intF;
  String methodBody;
  String comment;
  private boolean visible;
  ArrayList<ArgumentDescriptor> inputArgList;
  ArrayList<ArgumentTemplate> templateList;
  int internalID;
  public static final String SEPARATOR_DATAFILES = "|";
  public static final String SEPARATOR_TIMEDEFINITION = ":";
  public static final String KEYWORD_DEFAULT = "$DEFAULT";
  private transient Logger logger;

  public void setLogger(final Logger logger) {
    this.logger = logger;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(final boolean visible) {
    this.visible = visible;
  }

  public static String makeConsoleDescriptionForObj(final Object data) {
    String dataResult = "UnknownGUIData";
    if (data instanceof PlatformMessage) {
      dataResult = data.toString();
    }
    if (data instanceof Properties) {
      dataResult = data.toString();
    }
    if (data instanceof CommandDescriptor) {
      dataResult = data.toString();
    } else if (data instanceof String) {
      dataResult = (String) data;
    } else if (data instanceof LinkedList) {
      dataResult = "CommandsList;" + ((LinkedList) data).size() + ";items;";
    } else if (data instanceof SimulatorHeader) {
      dataResult = "SimulatorHeader";
    }
    return dataResult;
  }

  public void setComment(final String comment) {
    this.comment = comment;
  }

  public ArrayList<ArgumentTemplate> getTemplateList() {
    return templateList;
  }

  public boolean addNewTemplate(final ArgumentTemplate template) {
    final boolean dataOk = true;
    templateList.add(template);
    Collections.sort(templateList);
    return dataOk;
  }

  public boolean addNewEmptyTemplate(final String description) {
    boolean found = false;
    for (final ArgumentTemplate t : templateList) {
      if (t.getDescription().equals(description)) {
        found = true;
        break;
      }
    }
    if (!found) {
      this.templateList.add(new ArgumentTemplate(description, getInputArgs()));
    }
    return !found;
  }

  public boolean templateSelected(final String description) {
    boolean found = false;
    String inputArg = "";
    for (final ArgumentTemplate t : templateList) {
      if (t.getDescription().equals(description)) {
        found = true;
        inputArg = t.getArgContent();
      }
    }
    if (found) {
      this.logger.info("parseMethodBodyString for [" + inputArg + "]");
      setInputArgsFromString(inputArg);
    }
    return found;
  }

  public boolean updateTemplate(final String description, final String newContent) {
    boolean found = false;
    final String inputArg = "";
    for (final ArgumentTemplate t : templateList) {
      if (t.getDescription().equals(description)) {
        found = true;
        t.setArgContent(newContent);
      }
    }
    return found;
  }

  private ArrayList<ArgumentDescriptor> parseMethodBodyStringForArgList(final String methodBody) {
    final ArrayList<ArgumentDescriptor> result = new ArrayList<>();
    String argsBody = methodBody.substring(methodBody.indexOf("(") + 1);
    argsBody = argsBody.substring(0, argsBody.length() - 1);
    this.logger.log(Level.ALL, "argsBody is [" + argsBody + "]");
    if (argsBody.equals("")) {
      this.logger.log(Level.ALL, "argsBody is empty");
    } else {
      final String[] splitWords = argsBody.split(",");
      this.logger.log(Level.ALL, "argsBody has [" + splitWords.length + "] arguments");
      for (final String s : splitWords) {
        final String[] splitArgs = s.split(" ");

        this.logger.log(Level.ALL, "arg type [" + splitArgs[0] + "]");
        switch (splitArgs[0]) {
          case "byte": {
            final byte data = 0;
            final ArgumentDescriptor argDescriptor = new ArgumentDescriptor(data, splitArgs[1]);
            result.add(argDescriptor);
            break;
          }
          case "long": {
            final long data = 0;
            final ArgumentDescriptor argDescriptor = new ArgumentDescriptor(data, splitArgs[1]);
            result.add(argDescriptor);
            break;
          }
          case "int": {
            final int data = 0;
            final ArgumentDescriptor argDescriptor = new ArgumentDescriptor(data, splitArgs[1]);
            result.add(argDescriptor);
            break;
          }
          case "String": {
            final String data = "String";
            final ArgumentDescriptor argDescriptor = new ArgumentDescriptor(data, splitArgs[1]);
            result.add(argDescriptor);
            break;
          }
          case "byte[]": {
            final byte[] data = new byte[]{(byte) 0x00, 0x00};
            final ArgumentDescriptor argDescriptor = new ArgumentDescriptor(data, splitArgs[1]);
            result.add(argDescriptor);
            break;
          }
          case "float": {
            final float data = (float) 0.0;
            final ArgumentDescriptor argDescriptor = new ArgumentDescriptor(data, splitArgs[1]);
            result.add(argDescriptor);
            break;
          }
          case "int[]": {
            final int[] data = new int[]{0, 0};
            final ArgumentDescriptor argDescriptor = new ArgumentDescriptor(data, splitArgs[1]);
            result.add(argDescriptor);
            break;
          }
          case "long[]": {
            final long[] data = new long[]{0, 0};
            final ArgumentDescriptor argDescriptor = new ArgumentDescriptor(data, splitArgs[1]);
            result.add(argDescriptor);
            break;
          }
          case "double": {
            final double data = 0.0;
            final ArgumentDescriptor argDescriptor = new ArgumentDescriptor(data, splitArgs[1]);
            result.add(argDescriptor);
            break;
          }
          case "double[]": {
            final double[] data = new double[]{0, 0};
            final ArgumentDescriptor argDescriptor = new ArgumentDescriptor(data, splitArgs[1]);
            result.add(argDescriptor);
            break;
          }
          case "float[]": {
            final float[] data = new float[]{(float) 0.0, (float) 0.0};// (float) 0.0;

            final ArgumentDescriptor argDescriptor = new ArgumentDescriptor(data, splitArgs[1]);
            result.add(argDescriptor);
            break;
          }
        }
      }
    }
    return result;
  }

  public CommandDescriptor(final String intF, final String methodBody, final String comment, final int internalID,
                           final Logger logger) {
    this.logger = logger;
    this.intF = intF;
    this.methodBody = methodBody;
    this.comment = comment;
    this.internalID = internalID;
    this.inputArgList = parseMethodBodyStringForArgList(methodBody);
    this.templateList = new ArrayList<>();
    this.templateList.add(new ArgumentTemplate(KEYWORD_DEFAULT, getInputArgs()));

    if (comment.equals("")) {
      this.comment = "Placeholder";
    }

  }

  @Override
  public String toString() {
    return "CommandDescriptor{ID=" + internalID + " " + "intF=" + intF + ", methodBody="
        + methodBody + "}";// , inputArgs="+inputArgList+"}" ;
  }

  public String toCustomFormat1() {
    final String separator = ";";
    return internalID + separator + intF + separator + methodBody;
  }

  public String getMethodBody() {
    return methodBody;
  }

  public String getComment() {
    return comment;
  }

  public String getIntF() {
    return intF;
  }

  public void resetInputArgs() {

    for (final ArgumentDescriptor argDesc : this.inputArgList) {
      argDesc.restoreArgument();
    }
  }

  private ArrayList<ArgumentDescriptor> deepCopyArgDescriptorList(
          final ArrayList<ArgumentDescriptor> data) {
    final ArrayList<ArgumentDescriptor> newInputArgs = new ArrayList<>();
    for (final ArgumentDescriptor argDesc : data) {
      final ArgumentDescriptor newArgDesc = new ArgumentDescriptor(argDesc.getType(), argDesc.getName());
      newInputArgs.add(newArgDesc);
    }
    return newInputArgs;
  }

  private String checkSingleFloat(final String argument, final int argIndex, final boolean multiOperation,
                                  final boolean replace, final int subIndex) {
    String dataOk = "ParseOk";
    try {
      final float result = Float.parseFloat(argument);
      if (!multiOperation) {
        this.inputArgList.get(argIndex).setType(result);
      } else {
        if (replace && subIndex == 0) {
          final float[] emptyBuff = new float[0];
          this.inputArgList.get(argIndex).setType(emptyBuff);
        }
        final float[] partialList = (float[]) this.inputArgList.get(argIndex).getType();
        final float[] destination = new float[partialList.length + 1];
        System.arraycopy(partialList, 0, destination, 0, partialList.length);
        destination[destination.length - 1] = result;
        if (replace) {
          this.inputArgList.get(argIndex).setType(destination);
        }
      }
    } catch (final NumberFormatException e) {
      dataOk = "setInputArgsFromString;checkSingleFloat;-100;" + e;
    }
    return dataOk;
  }

  private String checkSingleInt(final String argument, final int argIndex, final boolean multiOperation,
                                final boolean replace, final int subIndex) {
    String dataOk = "ParseOk";
    try {
      final int result = Integer.parseInt(argument);
      if (!multiOperation) {
        this.inputArgList.get(argIndex).setType(result);
      } else {
        if (replace && subIndex == 0) {
          final int[] emptyBuff = new int[0];
          this.inputArgList.get(argIndex).setType(emptyBuff);
        }
        final int[] partialList = (int[]) this.inputArgList.get(argIndex).getType();
        final int[] destination = new int[partialList.length + 1];
        System.arraycopy(partialList, 0, destination, 0, partialList.length);
        destination[destination.length - 1] = result;
        if (replace) {
          this.inputArgList.get(argIndex).setType(destination);
        }
      }
    } catch (final NumberFormatException e) {
      dataOk = "setInputArgsFromString;checkSingleInt;-200;" + e;
    }
    return dataOk;
  }

  private String checkSingleLong(final String argument, final int argIndex, final boolean multiOperation,
                                 final boolean replace, final int subIndex) {
    String dataOk = "ParseOk";
    try {
      final long result = Long.parseLong(argument);
      if (!multiOperation) {
        this.inputArgList.get(argIndex).setType(result);
      } else {
        if (replace && subIndex == 0) {
          final long[] emptyBuff = new long[0];
          this.inputArgList.get(argIndex).setType(emptyBuff);
        }
        final long[] partialList = (long[]) this.inputArgList.get(argIndex).getType();
        final long[] destination = new long[partialList.length + 1];
        System.arraycopy(partialList, 0, destination, 0, partialList.length);
        destination[destination.length - 1] = result;
        if (replace) {
          this.inputArgList.get(argIndex).setType(destination);
        }
      }
    } catch (final NumberFormatException e) {
      dataOk = "setInputArgsFromString;checkSingleInt;-200;" + e;
    }
    return dataOk;
  }

  private String checkSingleDouble(final String argument, final int argIndex, final boolean multiOperation,
                                   final boolean replace, final int subIndex) {
    String dataOk = "ParseOk";
    try {
      final double result = Double.parseDouble(argument);
      if (!multiOperation) {
        this.inputArgList.get(argIndex).setType(result);
      } else {
        if (replace && subIndex == 0) {
          final double[] emptyBuff = new double[0];
          this.inputArgList.get(argIndex).setType(emptyBuff);
        }
        final double[] partialList = (double[]) this.inputArgList.get(argIndex).getType();
        final double[] destination = new double[partialList.length + 1];
        System.arraycopy(partialList, 0, destination, 0, partialList.length);
        destination[destination.length - 1] = result;
        if (replace) {
          this.inputArgList.get(argIndex).setType(destination);
        }
      }
    } catch (final NumberFormatException e) {
      dataOk = "setInputArgsFromString;checkSingleDouble;-300;" + e;
    }
    return dataOk;
  }

  private String checkSingleByte(final String argument, final int argIndex, final boolean multiOperation,
                                 final boolean replace, final int subIndex) {
    String dataOk = "ParseOk";
    if (argument.length() == 4) {
      final String reducedArg = argument.substring(2, 4);
      try {
        final byte result = (byte) (Integer.parseInt(reducedArg, 16) & 0xff);
        if (!multiOperation) {
          this.inputArgList.get(argIndex).setType(result);
        } else {
          if (replace && subIndex == 0) {
            final byte[] emptyBuff = new byte[0];
            this.inputArgList.get(argIndex).setType(emptyBuff);
          }
          final byte[] partialList = (byte[]) this.inputArgList.get(argIndex).getType();
          final byte[] destination = new byte[partialList.length + 1];
          System.arraycopy(partialList, 0, destination, 0, partialList.length);
          destination[destination.length - 1] = result;
          if (replace) {
            this.inputArgList.get(argIndex).setType(destination);
          }
        }

      } catch (final NumberFormatException e) {
        dataOk = "setInputArgsFromString;-1;" + e;
      }
    } else {
      dataOk = "setInputArgsFromString;-2;Incorrect argument format" + argument
          + ", should be 0x00";
    }
    return dataOk;
  }

  private String checkSingleArg(final String inputArgListString, final int argIndex) {
    String dataOk = "ParseOk";
    // Single argument
    final int index1 = inputArgListString.indexOf("{");
    final int index2 = inputArgListString.indexOf("}");
    if (index1 > -1 && index2 > -1 && index2 > index1) {
      final String argument = inputArgListString.substring(inputArgListString.indexOf("{") + 1,
          inputArgListString.indexOf("}"));
      if (this.inputArgList.get(argIndex).getType() instanceof String) {
        this.inputArgList.get(argIndex).setType(argument);
      } else if (this.inputArgList.get(argIndex).getType() instanceof Byte) {
        dataOk = checkSingleByte(argument, argIndex, false, true, 0);
      } else if (this.inputArgList.get(argIndex).getType() instanceof byte[]) {
        final String[] splitArguments = argument.split(",");
        boolean arrayOK = true;
        int k = 0;
        for (final String s : splitArguments) {
          dataOk = checkSingleByte(s, argIndex, true, false, k++);
          if (!dataOk.equals("ParseOk")) {
            arrayOK = false;
            break;
          }
        }
        if (arrayOK) {
          k = 0;
          for (final String s : splitArguments) {
            dataOk = checkSingleByte(s, argIndex, true, true, k++);
            if (!dataOk.equals("ParseOk")) {
              break;
            }
          }
        }
      } else if (this.inputArgList.get(argIndex).getType() instanceof Integer) {

        try {
          final int result = Integer.parseInt(argument, 10);
          this.inputArgList.get(argIndex).setType(result);
        } catch (final NumberFormatException e) {
          dataOk = "setInputArgsFromString;-3;" + e;
        }
      } else if (this.inputArgList.get(argIndex).getType() instanceof int[]) {
        final String[] splitArguments = argument.split(",");
        boolean arrayOK = true;
        int k = 0;
        for (final String s : splitArguments) {
          dataOk = checkSingleInt(s, argIndex, true, false, k++);
          if (!dataOk.equals("ParseOk")) {
            arrayOK = false;
            break;
          }
        }
        if (arrayOK) {
          k = 0;
          for (final String s : splitArguments) {
            dataOk = checkSingleInt(s, argIndex, true, true, k++);
            if (!dataOk.equals("ParseOk")) {
              break;
            }
          }
        }
      } else if (this.inputArgList.get(argIndex).getType() instanceof Long) {

        try {
          final long result = Long.parseLong(argument);
          this.inputArgList.get(argIndex).setType(result);
        } catch (final NumberFormatException e) {
          dataOk = "setInputArgsFromString;-4;" + e;
        }
      } else if (this.inputArgList.get(argIndex).getType() instanceof long[]) {
        final String[] splitArguments = argument.split(",");
        boolean arrayOK = true;
        int k = 0;
        for (final String s : splitArguments) {
          dataOk = checkSingleLong(s, argIndex, true, false, k++);
          if (!dataOk.equals("ParseOk")) {
            arrayOK = false;
            break;
          }
        }
        if (arrayOK) {
          k = 0;
          for (final String s : splitArguments) {
            dataOk = checkSingleLong(s, argIndex, true, true, k++);
            if (!dataOk.equals("ParseOk")) {
              break;
            }
          }
        }
      } else if (this.inputArgList.get(argIndex).getType() instanceof Double) {
        dataOk = checkSingleDouble(argument, argIndex, false, true, 0);
      } else if (this.inputArgList.get(argIndex).getType() instanceof double[]) {
        final String[] splitArguments = argument.split(",");
        boolean arrayOK = true;
        int k = 0;
        for (final String s : splitArguments) {
          dataOk = checkSingleDouble(s, argIndex, true, false, k++);
          if (!dataOk.equals("ParseOk")) {
            arrayOK = false;
            break;
          }
        }
        if (arrayOK) {
          k = 0;
          for (final String s : splitArguments) {
            dataOk = checkSingleDouble(s, argIndex, true, true, k++);
            if (!dataOk.equals("ParseOk")) {
              break;
            }
          }
        }
      } else if (this.inputArgList.get(argIndex).getType() instanceof Float) {
        dataOk = checkSingleFloat(argument, argIndex, false, true, 0);
      } else if (this.inputArgList.get(argIndex).getType() instanceof float[]) {
        final String[] splitArguments = argument.split(",");
        boolean arrayOK = true;
        int k = 0;
        for (final String s : splitArguments) {
          dataOk = checkSingleFloat(s, argIndex, true, false, k++);
          if (!dataOk.equals("ParseOk")) {
            arrayOK = false;
            break;
          }
        }
        if (arrayOK) {
          k = 0;
          for (final String s : splitArguments) {
            dataOk = checkSingleFloat(s, argIndex, true, true, k++);
            if (!dataOk.equals("ParseOk")) {
              break;
            }
          }
        }
      } else {
        dataOk = "setInputArgsFromString;-5;Class type ["
            + this.inputArgList.get(argIndex).getType().getClass().toString() + "] not treated";
      }
    } else {
      dataOk = "setInputArgsFromString;-6;No matching pair of {} found in " + inputArgListString;
    }
    return dataOk;
  }

  public void setInputArgsFromArrayList(final ArrayList<Object> data) {
    // Set input data in the blind, from an outside call
    this.inputArgList = new ArrayList<>();
    if (data != null) {
      for (final Object obj : data) {
        this.inputArgList.add(new ArgumentDescriptor(obj, "external"));
      }
    }
  }

  public String setInputArgsFromString(final String inputArgListString) {
    // this.inputArgList.clear();
    // Inverse kinematics (String -> ArrayList<ArgumentDescriptor>)
    // Can fail if argument template is not reproduced
    String dataOk = "ParseOk";

    if (this.inputArgList.isEmpty()) {
      // Do nothing, input parameters will be treated as void anyway
    } else if (this.inputArgList.size() == 1) {
      dataOk = checkSingleArg(inputArgListString, 0);
    } else {
      final String[] mainArguments = inputArgListString.split(";");
      // First check is argument number is ok
      if (mainArguments.length != this.inputArgList.size()) {
        dataOk = "setInputArgsFromString;-1;Arguments number doesn't match";
      } else {
        int k = 0;
        for (final String chkArg : mainArguments) {
          dataOk = checkSingleArg(chkArg, k++);
          if (!dataOk.equals("ParseOk")) {
            break;
          }
        }
      }
    }

    return dataOk;
  }

  public ArrayList<ArgumentDescriptor> getInputArgList() {
    return inputArgList;
  }

  public ArrayList<Object> getInputArgObjList() {
    final ArrayList<Object> inputArgObjList = new ArrayList<>();
    for (final ArgumentDescriptor obj : this.inputArgList) {
      inputArgObjList.add(obj.getType());
    }
    return inputArgObjList;
  }

  public String getInputArgs() {
    // Direct kinematics (ArrayList<ArgumentDescriptor> -> String)
    final StringBuilder formattedArgs = new StringBuilder("inputArgs=[");
    int argsCount = 0;
    if (this.logger != null)
      this.logger.log(Level.ALL, "inputArgList count is [" + inputArgList.size() + "]");
    for (final ArgumentDescriptor obj : inputArgList) {
      if (this.logger != null)
        this.logger.log(Level.ALL, "Trying to read from [" + obj.getName() + "]");
      formattedArgs.append(obj.toString());
      if (++argsCount < inputArgList.size()) {
        formattedArgs.append(";");
      }
    }
    formattedArgs.append("]");
    return formattedArgs.toString();
  }

  public int getInternalID() {
    return internalID;
  }

}
