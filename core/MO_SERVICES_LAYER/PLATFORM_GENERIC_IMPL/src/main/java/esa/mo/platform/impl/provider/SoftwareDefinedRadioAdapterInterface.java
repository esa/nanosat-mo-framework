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
package esa.mo.platform.impl.provider;

import org.ccsds.moims.mo.platform.softwaredefinedradio.structures.IQComponentsList;
import org.ccsds.moims.mo.platform.softwaredefinedradio.structures.SDRConfiguration;

/**
 *
 * @author Cesar Coelho
 */
public interface SoftwareDefinedRadioAdapterInterface {

    public boolean setConfiguration(SDRConfiguration configuration);

    public boolean enableSDR(Boolean enable);

    public IQComponentsList getIQComponents();

}
