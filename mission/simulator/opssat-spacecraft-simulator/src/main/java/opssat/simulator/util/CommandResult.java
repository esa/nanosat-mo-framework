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
import java.util.Date;

/**
 *
 * @author cezar
 */
public class CommandResult implements Serializable {

    CommandDescriptor commandDescriptor;
    Date executionTime;
    Date simulatorTime;
    Object output;
    private boolean commandFailed;

    public String toExtString() {
        return "CommandResult{intfName=" + commandDescriptor.getIntF() + ", methodBody=" + commandDescriptor
            .getMethodBody() + ",internalID=" + commandDescriptor.getInternalID() + ", executionTime=" + executionTime +
            ", simulatorTime=" + simulatorTime + "," + commandDescriptor.getInputArgs() + ", " + getOutputAsString() +
            "}";
    }

    public String getOutputAsString() {
        String formattedArgs = "output=[\n";
        //if (output != null) {System.out.println("output is type ["+output.getClass().getName()+"]");}
        if (output == null) {
            formattedArgs += "null";
        } else if (output instanceof Byte) {
            formattedArgs = formattedArgs + "Byte ={" + String.format("0x%02X", output) + "}";
        } else if (output instanceof byte[]) {
            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            byte[] bytes = (byte[]) output;
            int bytesLen = 0;
            int byteCount = 0;
            for (byte b : bytes) {
                sb.append(String.format("0x%02X", b));
                sb2.append(String.format("(%d)0x%02X", byteCount++, b));
                if (++bytesLen < bytes.length) {
                    sb.append(",");
                    sb2.append(",");
                }
                if (bytesLen > 1024) {
                    String continuation = "+ [" + (bytes.length - bytesLen) + "] more , total [" + bytes.length +
                        "] bytes.";
                    sb.append(continuation);
                    sb2.append(continuation);
                    break;
                }
            }
            formattedArgs = formattedArgs + "byte[] ={" + sb.toString() + "}";
            formattedArgs = formattedArgs + "\n";
            formattedArgs = formattedArgs + "byte[] ={" + sb2.toString() + "}";
        } else if (output instanceof double[]) {
            StringBuilder sb = new StringBuilder();
            double[] bytes = (double[]) output;
            int bytesLen = 0;
            int byteCount = 0;
            for (double b : bytes) {
                sb.append(String.format("%s", b));
                if (++bytesLen < bytes.length) {
                    sb.append(",");
                }
                if (bytesLen > 1024) {
                    String continuation = "+ [" + (bytes.length - bytesLen) + "] more , total [" + bytes.length +
                        "] doubles.";
                    sb.append(continuation);
                    break;
                }
            }
            formattedArgs = formattedArgs + "double[] ={" + sb.toString() + "}";
        }
        if (output instanceof Integer) {
            formattedArgs = formattedArgs + "int ={" + output + "}";
        }
        if (output instanceof Long) {
            formattedArgs = formattedArgs + "long ={" + output + "}";
        }
        if (output instanceof String) {
            formattedArgs = formattedArgs + "String ={" + output + "}";
        }
        if (output instanceof Float) {
            formattedArgs = formattedArgs + "float ={" + output + "}";
        }
        if (output instanceof Double) {
            formattedArgs = formattedArgs + "double ={" + output + "}";
        }
        return formattedArgs + "\n]";
    }

    public Object getOutput() {
        return output;
    }

    public void setOutput(Object output) {
        this.output = output;
    }

    public boolean isCommandFailed() {
        return commandFailed;
    }

    public void setCommandFailed(boolean commandFailed) {
        this.commandFailed = commandFailed;
    }

    @Override
    public String toString() {
        return "CommandResult{" + "methodBody=" + commandDescriptor.getMethodBody() + ", executionTime=" +
            executionTime + "}";
    }

    public CommandDescriptor getCommandDescriptor() {
        return commandDescriptor;
    }

    public CommandResult(CommandDescriptor commandDescriptor, Date executionTime, Date simulatorTime) {
        this.commandDescriptor = commandDescriptor;
        this.executionTime = executionTime;
        this.simulatorTime = simulatorTime;
    }

}
