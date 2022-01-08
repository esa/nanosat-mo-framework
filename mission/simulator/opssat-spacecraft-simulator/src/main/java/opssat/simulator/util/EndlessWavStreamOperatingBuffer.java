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

import java.util.logging.Logger;

/**
 *
 * @author Cezar Suteu
 */
public class EndlessWavStreamOperatingBuffer extends GenericWavFileBasedOperatingBuffer {

    public EndlessWavStreamOperatingBuffer(final Logger logger) {
        super(logger);
    }
    
    public double[] getDataAsDoubleArray(final int quantityOfData) {
        int bytesNo= quantityOfData;
        final double[] result=new double[bytesNo];
        final double[] tempData=(double[])super.getDataBuffer();
        final int capacity=tempData.length;
        int tempOperatingIndex=super.getOperatingIndex();
        if (tempData!=null && capacity>0)
        {
            int resultIndex=0;
            while ((bytesNo--)>0){
                result[resultIndex]=tempData[tempOperatingIndex++];
                if (tempOperatingIndex>=capacity)
                {
                    tempOperatingIndex=0;
                }
                resultIndex++;
            }
            super.setOperatingIndex(tempOperatingIndex);
        }
        //System.out.println("result has ["+result.length+"] elements");
        return result;
    }
    public void setDataFromByteArray(final byte[] directData)
    {
        super.setOperatingIndex(0);
        super.setDataBuffer(directData);
    }
}
