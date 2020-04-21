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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.util.logging.Level;
import java.util.logging.Logger;
import opssat.simulator.threading.SimulatorNode;
import opssat.simulator.util.wav.WavFile;
import opssat.simulator.util.wav.WavFileException;

/**
 *
 * @author Cezar Suteu
 */
public abstract class GenericWavFileBasedOperatingBuffer implements SimulatorOperateBuffer {

    private Object dataBuffer;
    private String dataFilePath;
    private int operatingIndex;
    private Logger logger;

    public GenericWavFileBasedOperatingBuffer(Logger logger) {
        operatingIndex = 0;
        this.dataBuffer = new double[0];
        this.logger = logger;
    }

    @Override
    public boolean loadFromPath(String path) {
        try {
            /*
            String absolutePath = SimulatorNode.getResourcesPath() + path;
            File f = new File(absolutePath);
            if (f.exists() && !f.isDirectory()) {
                this.logger.log(Level.INFO, "File [" + f.getAbsolutePath() + "] exists");
            } else {
                this.logger.log(Level.INFO, "File [" + absolutePath + "] does not exist");
                ClassLoader classLoader;
                classLoader = getClass().getClassLoader();
                final URL url2 = classLoader.getSystemResource(path);

                final InputStream inputStream = classLoader.getSystemResourceAsStream(path);
                try {
                    File newFile = new File(absolutePath);
                    if (newFile.createNewFile()) {
                        this.logger.log(Level.INFO, "File created");
                        OutputStream outputStream = new FileOutputStream(newFile);
                        int read = 0;
                        byte[] bytes = new byte[1024];
                        while ((read = inputStream.read(bytes)) != -1) {
                            outputStream.write(bytes, 0, read);
                        }
                        outputStream.close();
                    } else {
                        this.logger.log(Level.INFO, "File could not be created");
                    }
                } catch (IOException e) {
                    this.logger.log(Level.INFO, e.toString());
                }
            }
            */
            String absolutePath=SimulatorNode.handleResourcePath(path, logger, getClass().getClassLoader(), false);
            
//            this.dataFilePath = Paths.get(absolutePath).toString();
            this.dataFilePath = absolutePath;
            
            WavFile wavFile;
            try {
                wavFile = WavFile.openWavFile(new File(this.dataFilePath));
                String displayInfo = wavFile.getDisplayInfo();
                for (String line : displayInfo.split("\\r?\\n")) {
                    this.logger.log(Level.FINE, line);
                }
                double[] tempBuffer = new double[((int) wavFile.getNumFrames() * wavFile.getNumChannels())];
                int framesRead;
                // Read frames into buffer
                framesRead = wavFile.readFrames(tempBuffer, (int) wavFile.getNumFrames());
                this.dataBuffer = (Object) tempBuffer;
                this.operatingIndex = 0;
            } catch (WavFileException ex) {
                Logger.getLogger(GenericWavFileBasedOperatingBuffer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            this.logger.log(Level.INFO, ex.toString());
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
                    result.append(" and [" + (tempCast.length - k) + "] more , total [" + tempCast.length + "] bytes.");
                    break;
                }
            }
            result.append("}");
            return result.toString();
        }
        if (dataBuffer instanceof double[]) {
            double[] tempCast = (double[]) dataBuffer;
            StringBuilder result = new StringBuilder("double[] {");
            int k = 0;
            for (double b : tempCast) {
                result.append(String.format("%s", b));
                if (++k < tempCast.length) {
                    result.append(",");
                }
                if (k > 10) {
                    result.append(" and [" + (tempCast.length - k) + "] more , total [" + tempCast.length + "] doubles.");
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
