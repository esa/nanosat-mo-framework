/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2016      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under the European Space Agency Public License, Version 2.0
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import opssat.simulator.threading.SimulatorNode;

/**
 *
 * @author Cezar Suteu
 */
public abstract class GenericFileBasedOperatingBuffer implements SimulatorOperateBuffer {

  private Object dataBuffer;
  private String dataFilePath;
  private int operatingIndex;
  private Logger logger;

  public GenericFileBasedOperatingBuffer(Logger logger) {
    byte[] tempArray = new byte[0];
    operatingIndex = 0;
    this.dataBuffer = tempArray;
    this.logger = logger;
  }

  @Override
  public boolean loadFromPath(String path) {
    try {
      String absolutePath = SimulatorNode.handleResourcePath(path, logger,
          getClass().getClassLoader(), false);
//            this.dataBuffer = Files.readAllBytes(Paths.get(absolutePath));
      RandomAccessFile f = new RandomAccessFile(absolutePath, "r");
      if (f.length() > Integer.MAX_VALUE) {
        throw new IOException("File is too large");
      }
      byte[] data = new byte[(int) f.length()];
      f.readFully(data);
      this.dataBuffer = data;

    } catch (IOException ex) {
      return false;
    }
    return true;
  }

  public boolean loadImageFromAbsolutePath(String path) {
    try {
      String[] parts = path.split("\\.");
      String ending = parts[parts.length - 1];
      if (ending.equals("raw")) {
        RandomAccessFile f = new RandomAccessFile(path, "r");
        if (f.length() > Integer.MAX_VALUE) {
          throw new IOException("File is too large");
        }
        if(f.length() != SimulatorNode.CAMERA_MAX_SIZE) {
          throw new IllegalArgumentException("RAW file does not fit camera resolution.");
        }
        byte[] data = new byte[(int) f.length()];
        f.readFully(data);
        this.dataBuffer = data;
      } else {
        byte[] image = ImageLoader.loadNonRawImage(path);
        this.dataBuffer = image;
      }
    } catch (IOException ex) {
      return false;
    }
    return true;
  }

  @Override
  public boolean preparePath(String path) {
    boolean fileExists = true;

    try {
      RandomAccessFile f = new RandomAccessFile(SimulatorNode.getResourcesPath() + path, "r");
      f.close();
    } catch (FileNotFoundException ex) {
      fileExists = false;
    } catch (IOException ex) {
      Logger.getLogger(GenericFileBasedOperatingBuffer.class.getName()).log(Level.SEVERE, null, ex);
    }

    if (fileExists) {
      this.dataFilePath = path;
      return true;
    } else {
      return false;
    }
  }

  public Object getDataBuffer() {
    return dataBuffer;
  }

  public String getDataBufferAsString() {
    if (dataBuffer instanceof byte[]) {
      byte[] tempCast = (byte[]) dataBuffer;
      StringBuilder result = new StringBuilder("byte[] {");
      int k = 0;
      for (byte b : tempCast) {
        result.append(String.format("0x%02X", b));
        if (++k < tempCast.length) {
          result.append(",");
        }
        if (k > 10) {
          result.append(
              " and [" + (tempCast.length - k) + "] more , total [" + tempCast.length + "] bytes.");
          break;
        }
      }
      result.append("}");
      return result.toString();
    }
    return "Unknown data type [" + dataBuffer.getClass().getName() + "]";
  }

  public void setDataBuffer(Object dataBuffer) {
    this.dataBuffer = dataBuffer;
  }

  @Override
  public int getOperatingIndex() {
    return operatingIndex;
  }

  public void setOperatingIndex(int operatingIndex) {
    this.operatingIndex = operatingIndex;
  }

}
