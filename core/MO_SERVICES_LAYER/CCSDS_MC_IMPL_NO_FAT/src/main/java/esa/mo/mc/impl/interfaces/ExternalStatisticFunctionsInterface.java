/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 * ----------------------------------------------------------------------------
 */
package esa.mo.mc.impl.interfaces;

import org.ccsds.moims.mo.mal.structures.TimeList;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticFunctionDetails;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticValue;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;

/**
 * The user must implement this interface to get extra Statistic
 * Functions available in their implementation
 * 
 */
public interface ExternalStatisticFunctionsInterface {
    
    /**
     * The user must implement this interface to get custom Statistic
     * Functions.
     * 
     * @param statFuncId Id of the statistic function. This value will always be
     * greater than 4 because there are 4 default Statistic Functions in the 
     * MC Standard.
     * @return The corresponding Statistic Function
     */
    public StatisticFunctionDetails getCustomStatisticFunction (Long statFuncId);
    
    
    public StatisticValue generateCustomStatisticValue(Long statFuncId, TimeList times, AttributeValueList values);

    
    
}
