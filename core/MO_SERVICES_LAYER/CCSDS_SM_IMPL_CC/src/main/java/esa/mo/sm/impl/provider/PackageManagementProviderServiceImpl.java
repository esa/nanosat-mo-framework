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
package esa.mo.sm.impl.provider;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.helpertools.connections.ConnectionProvider;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.softwaremanagement.SoftwareManagementHelper;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.PackageManagementHelper;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.body.CheckPackageIntegrityResponse;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.body.ListPackageResponse;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.provider.InstallInteraction;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.provider.PackageManagementInheritanceSkeleton;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.provider.UninstallInteraction;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.provider.UpgradeInteraction;

/**
 *
 */
public class PackageManagementProviderServiceImpl extends PackageManagementInheritanceSkeleton {

    private final static MALService SERVICE = PackageManagementHelper.PACKAGEMANAGEMENT_SERVICE;
    private MALProvider packageManagementServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    private COMServicesProvider comServices;

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param comServices
     * @throws MALException On initialization error.
     */
    public synchronized void init(COMServicesProvider comServices) throws MALException {
        if (!initialiased) {

            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NAME, SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_VERSION) == null) {
                SoftwareManagementHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            try {
                PackageManagementHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
            }

        }
        
        this.comServices = comServices;

        // shut down old service transport
        if (null != packageManagementServiceProvider) {
            connection.close();
        }

        packageManagementServiceProvider = connection.startService(PackageManagementHelper.PACKAGEMANAGEMENT_SERVICE_NAME.toString(), PackageManagementHelper.PACKAGEMANAGEMENT_SERVICE, this);
        running = true;
        initialiased = true;
        Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).info("Package Management service READY");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != packageManagementServiceProvider) {
                packageManagementServiceProvider.close();
            }

            connection.close();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    @Override
    public ListPackageResponse listPackage(IdentifierList names, Identifier category, MALInteraction interaction) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void install(Long packageObjInstId, InstallInteraction interaction) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void uninstall(Long instId, Boolean leaveConfigurationsIntact, UninstallInteraction interaction) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void upgrade(LongList packageObjInstIds, UpgradeInteraction interaction) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IdentifierList listCategories(MALInteraction interaction) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CheckPackageIntegrityResponse checkPackageIntegrity(LongList packageIds, MALInteraction interaction) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
