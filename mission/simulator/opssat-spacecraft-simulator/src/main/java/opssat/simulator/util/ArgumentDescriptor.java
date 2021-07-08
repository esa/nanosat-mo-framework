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
import java.util.Locale;


/**
 *
 * @author Cezar Suteu
 */
public class ArgumentDescriptor implements Serializable {

    Object type;
    String name;
    Object defaultType;
    String defaultName;

    public ArgumentDescriptor(Object type, String name) {
        this.type = type;
        this.name = name;
        this.defaultType = type;
        this.defaultName = name;
    }
    public ArgumentDescriptor(String type, String name) {
        
        this.name = name;
        if (type.equals("Boolean")) {
            this.type = false;
        } else if (type.equals("Integer") || type.equals("int")) {
            this.type = 0;
        } else if (type.equals("Float")) {
            this.type = (float) 0.0;
        } else if (type.equals("String")) {
            this.type = "";
        } else if (type.equals("byte[]")) {
            this.type = new byte[0];
        } else if (type.equals("float")) {
            this.type = (float) 0.0;
        } else if (type.equals("float[]")) {
            this.type = new float[0];
        } else if (type.equals("long")) {
            this.type = (float) (long) 0.0;
        } else if (type.equals("long[]")) {
            this.type = new long[0];
        } else if (type.equals("int[]")) {
            this.type = new int[0];
        } else if (type.startsWith("int[")) {
            String partial=type.substring(4);
            partial=partial.substring(0,partial.length()-1);
            this.type = new int[Integer.parseInt(partial)];
        } else if (type.startsWith("float[")) {
            String partial=type.substring(6);
            partial=partial.substring(0,partial.length()-1);
            this.type = new float[Integer.parseInt(partial)];
        } else if (type.equals("double")) {
            this.type = (double) 0.0;
        } else if (type.equals("double[]")) {
            this.type = new double[0];
        } else {
            //logger.info("Error on ArgumentDescriptor(String type, String name) with ArgumentDescriptor(["+type+"],["+name+"])");
            this.name = "UnknownDeviceDataTypeString{" + type + "}";
            this.type= null;
        }
        this.defaultType = this.type;
        this.defaultName = this.name;
    }

    public void restoreArgument() {
        this.type = this.defaultType;
        this.name = this.defaultName;
    }
    public float getTypeAsFloatByIndex(int index)
    {
        return ((float []) type)[index];
    }
    public int getTypeAsIntByIndex(int index)
    {
        return ((int []) type)[index];
    }
    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }
    
    public void setIntTypeByIndex(int newValue,int index) {
        int[] cast=((int[]) this.type);
        if (cast.length<index+1)
        {
            this.type=new int[index+1];
        }
        ((int[]) this.type)[index]=newValue;
    }
    public void setFloatTypeByIndex(float newValue,int index) {
        float[] cast=((float[]) this.type);
        if (cast.length<index+1)
        {
            this.type=new float[index+1];
        }
        ((float[]) this.type)[index]=newValue;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString()
    {
        String formattedArgs="";
        if (type instanceof Byte) {
            formattedArgs = formattedArgs + "Byte " + name + "={" + String.format("0x%02X", type) + "}";
        } else if (type instanceof byte[]) {
            StringBuilder sb = new StringBuilder();
            byte[] bytes = (byte[]) type;
            int bytesLen = 0;
            for (byte b : bytes) {
                sb.append(String.format("0x%02X",b));
                if (++bytesLen < bytes.length) {
                    sb.append(",");
                }
            }
            formattedArgs = formattedArgs + "byte[] " + name + "={" + sb.toString() + "}";
        } else if (type instanceof int[]) {
            StringBuilder sb = new StringBuilder();
            int[] ints = (int[]) type;
            int intsLen = 0;
            for (int b : ints) {
                sb.append(b);
                if (++intsLen < ints.length) {
                    sb.append(",");
                }
            }
            formattedArgs = formattedArgs + "int[] " + name + "={" + sb.toString() + "}";
        } else if (type instanceof float[]) {
            StringBuilder sb = new StringBuilder();
            float[] floats = (float[]) type;
            int floatsLen = 0;
            for (float b : floats) {
                sb.append(b);
                if (++floatsLen < floats.length) {
                    sb.append(",");
                }
            }
            formattedArgs = formattedArgs + "float[] " + name + "={" + sb.toString() + "}";
        } else if (type instanceof long[]) {
            StringBuilder sb = new StringBuilder();
            long[] longs = (long[]) type;
            int longsLen = 0;
            for (long b : longs) {
                sb.append(b);
                if (++longsLen < longs.length) {
                    sb.append(",");
                }
            }
            formattedArgs = formattedArgs + "long[] " + name + "={" + sb.toString() + "}";
        } else if (type instanceof double[]) {
            StringBuilder sb = new StringBuilder();
            double[] doubles = (double[]) type;
            int doublesLen = 0;
            for (double b : doubles) {
                sb.append(b);
                if (++doublesLen < doubles.length) {
                    sb.append(",");
                }
            }
            formattedArgs = formattedArgs + "double[] " + name + "={" + sb.toString() + "}";
        }
        if (type instanceof Integer) {
            formattedArgs = formattedArgs + "int " + name + "={" + type + "}";
        }
        if (type instanceof Long) {
            formattedArgs = formattedArgs + "long " + name + "={" + type + "}";
        }

        if (type instanceof String) {
            formattedArgs = formattedArgs + "String " + name + "={" + type + "}";
        }
        if (type instanceof Float) {
            formattedArgs = formattedArgs + "float " + name + "={" + type + "}";
        }
        if (type instanceof Double) {
            formattedArgs = formattedArgs + "double " + name + "={" + String.format(Locale.ROOT, "%.17f",type) + "}";
        }
        return formattedArgs;
    }

}
