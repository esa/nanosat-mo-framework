/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
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
import esa.mo.common.impl.provider.DirectoryProviderServiceImpl;
import esa.mo.sm.impl.provider.AppsLauncherProviderServiceImpl;
import esa.mo.sm.impl.provider.HeartbeatProviderServiceImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;

/**
 * Deprecated because it is just an example class on how to start some of the
 * Platform services. Although functional, this class should not be used out of
 * the box. It's deliberately here just to demonstrate how to initialize
 * services.
 */
@Deprecated
public class SMServicesProvider {

    private final AppsLauncherProviderServiceImpl applicationsManagerService = new AppsLauncherProviderServiceImpl();
    private final HeartbeatProviderServiceImpl heartbeatService = new HeartbeatProviderServiceImpl();

    public void init(COMServicesProvider comServices, DirectoryProviderServiceImpl directoryService) {
        try {
            applicationsManagerService.init(comServices, directoryService);
            heartbeatService.init();
        } catch (MALException ex) {
            Logger.getLogger(SMServicesProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public AppsLauncherProviderServiceImpl getApplicationsManagerService() {
        return this.applicationsManagerService;
    }

    public HeartbeatProviderServiceImpl getHeartbeatService() {
        return this.heartbeatService;
    }

}
