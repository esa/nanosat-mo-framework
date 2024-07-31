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
package opssat.simulator.models;

import java.util.Random;
import java.util.logging.Logger;
import opssat.simulator.util.EndlessSingleStreamOperatingBuffer;

/**
 *
 * @author Suteu Silviu Cezar
 */
public class OpticalReceiverModel extends AbstractModel {
    private byte[] operatingBuffer;
    private int successRate;
    private EndlessSingleStreamOperatingBuffer singleStreamOperatingBuffer;

    public OpticalReceiverModel(String name, Logger logger) {
        super(name);
        successRate = 10000;
        operatingBuffer = new byte[]{(byte) 0xe0, 0x4f, (byte) 0xd0, 0x20, (byte) 0xea, 0x3a, 0x69, 0x10, (byte) 0xa2,
                                     (byte) 0xd8, 0x08, 0x00, 0x2b, 0x30, 0x30, (byte) 0x9d};
        singleStreamOperatingBuffer = new EndlessSingleStreamOperatingBuffer(logger);
        singleStreamOperatingBuffer.setDataFromByteArray(operatingBuffer);
    }

    public EndlessSingleStreamOperatingBuffer getSingleStreamOperatingBuffer() {
        return singleStreamOperatingBuffer;
    }

    public byte[] getOperatingBuffer() {
        return (byte[]) singleStreamOperatingBuffer.getDataBuffer();
    }

    public int getDegradationRate() {
        return successRate;
    }

    public void setOperatingBuffer(byte[] operatingBuffer) {
        this.singleStreamOperatingBuffer.setDataFromByteArray(operatingBuffer);
    }

    public void setSuccessRate(int successRate) {
        if (successRate >= 5000 && successRate <= 10000) {
            this.successRate = successRate;
        }
    }

    private boolean getBitFlip() {
        Random r = new Random();
        int Low = 0;
        int High = 9999;
        int Result = r.nextInt(High - Low) + Low;
        //System.out.println("getBitFlip;result["+Result+"];");
        return Result >= successRate;
    }

    private byte doFlipOnByte(byte data) {
        byte result = data;
        for (int i = 0; i <= 7; i++) {
            if (getBitFlip()) {
                result = (byte) (result ^ (1 << i));
            }
        }
        return result;
    }

    public byte[] getBytesFromBuffer(int bytesNo) {
        byte[] tempResult = singleStreamOperatingBuffer.getDataAsByteArray(bytesNo);
        byte[] result = new byte[bytesNo];
        int i = 0;
        for (byte b : tempResult) {
            result[i++] = doFlipOnByte(b);
        }
        return result;
    }
}
