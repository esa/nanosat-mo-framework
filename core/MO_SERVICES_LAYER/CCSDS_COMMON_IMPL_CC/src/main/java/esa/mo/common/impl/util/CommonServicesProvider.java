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
package esa.mo.common.impl.util;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.common.impl.provider.ConfigurationProviderServiceImpl;
import esa.mo.common.impl.provider.DirectoryProviderServiceImpl;
import org.ccsds.moims.mo.mal.MALException;

/**
 *
 *
 */
public class CommonServicesProvider {

    private final ConfigurationProviderServiceImpl configurationService = new ConfigurationProviderServiceImpl();
    private final DirectoryProviderServiceImpl directoryService = new DirectoryProviderServiceImpl();
    
    public void init(COMServicesProvider comServices) throws MALException {

        configurationService.init(comServices);
        directoryService.init(comServices);

    }

    public ConfigurationProviderServiceImpl getConfigurationService() {
        return this.configurationService;
    }

    public DirectoryProviderServiceImpl getDirectoryService() {
        return this.directoryService;
    }
    
}
