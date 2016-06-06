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
package esa.mo.sm.impl.util;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.sm.impl.provider.AppsLauncherProviderServiceImpl;
import esa.mo.sm.impl.provider.PackageManagementProviderServiceImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;

/**
 * Class holding all the COM services providers. The services can all be
 * initialized automatically or can be set manually.
 */
public class SMServicesProvider {

    private final PackageManagementProviderServiceImpl packageManagementService = new PackageManagementProviderServiceImpl();
    private final AppsLauncherProviderServiceImpl applicationsManagerService = new AppsLauncherProviderServiceImpl();

    public void init(COMServicesProvider comServices) {
        try {
            packageManagementService.init(comServices);
            applicationsManagerService.init(comServices);
        } catch (MALException ex) {
            Logger.getLogger(SMServicesProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public PackageManagementProviderServiceImpl getPackageManagementService() {
        return this.packageManagementService;
    }

    public AppsLauncherProviderServiceImpl getApplicationsManagerService() {
        return this.applicationsManagerService;
    }
    
}
