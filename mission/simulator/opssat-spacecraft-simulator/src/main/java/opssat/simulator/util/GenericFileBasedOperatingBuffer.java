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

  public GenericFileBasedOperatingBuffer(final Logger logger) {
    final byte[] tempArray = new byte[0];
    operatingIndex = 0;
    this.dataBuffer = tempArray;
    this.logger = logger;
  }

  @Override
  public boolean loadFromPath(final String path) {
    try {
      final String absolutePath = SimulatorNode.handleResourcePath(path, logger,
          getClass().getClassLoader(), false);
//            this.dataBuffer = Files.readAllBytes(Paths.get(absolutePath));
      final RandomAccessFile f = new RandomAccessFile(absolutePath, "r");
      if (f.length() > Integer.MAX_VALUE) {
        throw new IOException("File is too large");
      }
      final byte[] data = new byte[(int) f.length()];
      f.readFully(data);
      this.dataBuffer = data;

    } catch (final IOException ex) {
      return false;
    }
    return true;
  }

  public boolean loadImageFromAbsolutePath(final String path) {
    try {
      if (path == null) {
        throw new IOException("Image path is null. Please provide correct camerasim.imagefile property.");
      }
      final String[] parts = path.split("\\.");
      final String ending = parts[parts.length - 1];
      if (ending.equals("raw")) {
        final RandomAccessFile f = new RandomAccessFile(path, "r");
        if (f.length() > Integer.MAX_VALUE) {
          throw new IOException("File is too large");
        }
        if(f.length() != SimulatorNode.CAMERA_MAX_SIZE) {
          throw new IllegalArgumentException("RAW file does not fit camera resolution.");
        }
        final byte[] data = new byte[(int) f.length()];
        f.readFully(data);
        this.dataBuffer = data;
      } else {
        this.dataBuffer = ImageLoader.loadNonRawImage(path);
      }
    } catch (final IOException ex) {
      return false;
    }
    return true;
  }

  @Override
  public boolean preparePath(final String path) {
    boolean fileExists = true;

    try {
      final RandomAccessFile f = new RandomAccessFile(SimulatorNode.getResourcesPath() + path, "r");
      f.close();
    } catch (final FileNotFoundException ex) {
      fileExists = false;
    } catch (final IOException ex) {
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
      final byte[] tempCast = (byte[]) dataBuffer;
      final StringBuilder result = new StringBuilder("byte[] {");
      int k = 0;
      for (final byte b : tempCast) {
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

  public void setDataBuffer(final Object dataBuffer) {
    this.dataBuffer = dataBuffer;
  }

  @Override
  public int getOperatingIndex() {
    return operatingIndex;
  }

  public void setOperatingIndex(final int operatingIndex) {
    this.operatingIndex = operatingIndex;
  }

}
