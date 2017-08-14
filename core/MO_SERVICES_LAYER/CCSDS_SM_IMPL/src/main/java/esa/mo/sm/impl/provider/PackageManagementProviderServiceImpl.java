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
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.softwaremanagement.SoftwareManagementHelper;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.PackageManagementHelper;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.body.CheckPackageIntegrityResponse;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.provider.InstallInteraction;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.provider.PackageManagementInheritanceSkeleton;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.provider.UninstallInteraction;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.provider.UpgradeInteraction;
import esa.mo.sm.impl.util.PMBackend;
import java.io.IOException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.body.FindPackageResponse;

/**
 * Package Management service Provider.
 */
public class PackageManagementProviderServiceImpl extends PackageManagementInheritanceSkeleton {

    private MALProvider packageManagementServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    private COMServicesProvider comServices;
    private PMBackend backend;

    /**
     * Initializes the service
     *
     * @param comServices
     * @param backend
     * @throws MALException On initialization error.
     */
    public synchronized void init(final COMServicesProvider comServices,
            final PMBackend backend) throws MALException {
        if (backend == null) {
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).severe(
                    "Package Management service could not be initialized! The backend object cannot be null.");
            return;
        }

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
        this.backend = backend;

        // shut down old service transport
        if (null != packageManagementServiceProvider) {
            connection.closeAll();
        }

        packageManagementServiceProvider = connection.startService(PackageManagementHelper.PACKAGEMANAGEMENT_SERVICE_NAME.toString(), PackageManagementHelper.PACKAGEMANAGEMENT_SERVICE, false, this);
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

            connection.closeAll();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Exception during close down of the provider {0}", ex);
        }
    }

    @Override
    public FindPackageResponse findPackage(IdentifierList names, MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        FindPackageResponse outList = new FindPackageResponse();

        if (null == names) { // Is the input null?
            throw new IllegalArgumentException("names argument and category argument must not be null");
        }

        boolean returnAll = false;

        // Is the call to return the full list of available packages?
        for (Identifier name : names) {
            if ("*".equals(name.getValue())) {
                returnAll = true;
            }
        }

        StringList availablePackages;
        try {
            availablePackages = backend.getListOfPackages();
            final IdentifierList packages = new IdentifierList();
            final BooleanList installed = new BooleanList();

            if (returnAll) {
                for (String pack : availablePackages) {
                    boolean isInstalled = backend.isPackageInstalled(pack);
                    packages.add(new Identifier(pack));
                    installed.add(isInstalled);
                }
            } else {
                for (int j = 0; j < names.size(); j++) {
                    // Is the name available?
                    int index = packageExistsInIndex(names.get(j).getValue(), availablePackages);

                    if (index == -1) {
                        unkIndexList.add(new UInteger(j));
                    } else {
                        boolean isInstalled = backend.isPackageInstalled(availablePackages.get(index));
                        packages.add(new Identifier(availablePackages.get(index)));
                        installed.add(isInstalled);
                    }
                }
            }

            outList.setBodyElement0(packages); // ObjIds
            outList.setBodyElement1(installed); // Installed?
        } catch (IOException ex) {
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);

            // Just return empty lists
            outList.setBodyElement0(new IdentifierList());
            outList.setBodyElement1(new BooleanList());
        }

        // Errors
        if (!unkIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        return outList;
    }

    @Override
    public void install(final IdentifierList names, final InstallInteraction interaction) throws MALInteractionException, MALException {
        interaction.sendAcknowledgement(null);
        
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        if (null == names) { // Is the input null?
            throw new IllegalArgumentException("names argument must not be null");
        }

        StringList availablePackages;
        try {
            availablePackages = backend.getListOfPackages();

            for (int i = 0; i < names.size(); i++) {
                int index = packageExistsInIndex(names.get(i).getValue(), availablePackages);

                if (index == -1) {
                    unkIndexList.add(new UInteger(i));
                }

                if (backend.isPackageInstalled(availablePackages.get(i))) {
                    invIndexList.add(new UInteger(i));
                }

                // Throw error if already installed!
                // Before installing, we need to check the package integrity!
                boolean integrity = backend.checkPackageIntegrity(availablePackages.get(i));

                // The installation cannot go forward here if the integrity is false!
            }
        } catch (IOException ex) {
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Errors
        if (!unkIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        if (!invIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        for (Identifier packageName : names) {
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(Level.INFO,
                    "Installing: {0}", packageName.getValue());

            backend.install(packageName.getValue());
        }

        interaction.sendResponse();
    }

    @Override
    public void uninstall(final IdentifierList names, final BooleanList keepConfigurations,
            final UninstallInteraction interaction) throws MALInteractionException, MALException {
        interaction.sendAcknowledgement();

        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        // Add validation
        if (null == names || keepConfigurations == null) { // Is the input null?
            throw new IllegalArgumentException("names argument and keepConfigurations argument must not be null");
        }

        StringList availablePackages;
        try {
            availablePackages = backend.getListOfPackages();

            for (int i = 0; i < names.size(); i++) {
                int index = packageExistsInIndex(names.get(i).getValue(), availablePackages);

                if (index == -1) {
                    unkIndexList.add(new UInteger(i));
                }

                if (!backend.isPackageInstalled(availablePackages.get(i))) {
                    invIndexList.add(new UInteger(i));
                }

                // Throw error if already installed!
                // Before installing, we need to check the package integrity!
                boolean integrity = backend.checkPackageIntegrity(availablePackages.get(i));

                // The installation cannot go forward here if the integrity is false!
            }
        } catch (IOException ex) {
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Errors
        if (!unkIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        if (!invIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        for (Identifier packageName : names) {
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(Level.INFO,
                    "Uninstalling: {0}", packageName.getValue());

            backend.uninstall(packageName.getValue(), true);
        }

        interaction.sendResponse();
    }

    @Override
    public void upgrade(final IdentifierList names, final UpgradeInteraction interaction) throws MALInteractionException, MALException {
        interaction.sendAcknowledgement();

        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        // Add validation
        if (null == names) { // Is the input null?
            throw new IllegalArgumentException("names argument must not be null");
        }

        StringList availablePackages;
        try {
            availablePackages = backend.getListOfPackages();

            for (int i = 0; i < names.size(); i++) {
                int index = packageExistsInIndex(names.get(i).getValue(), availablePackages);

                if (index == -1) {
                    unkIndexList.add(new UInteger(i));
                }

                if (!backend.isPackageInstalled(availablePackages.get(i))) {
                    invIndexList.add(new UInteger(i));
                }

                // Throw error if already installed!
                // Before installing, we need to check the package integrity!
                boolean integrity = backend.checkPackageIntegrity(availablePackages.get(i));

                // The installation cannot go forward here if the integrity is false!
            }
        } catch (IOException ex) {
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Errors
        if (!unkIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        if (!invIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        for (Identifier packageName : names) {
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(Level.INFO,
                    "Upgrading: {0}", packageName.getValue());

            backend.upgrade(packageName.getValue());
        }
        
        interaction.sendResponse();
    }

    @Override
    public CheckPackageIntegrityResponse checkPackageIntegrity(IdentifierList names, MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        // Add validation
        if (null == names) { // Is the input null?
            throw new IllegalArgumentException("names argument must not be null");
        }

        StringList availablePackages;
        try {
            availablePackages = backend.getListOfPackages();

            for (int i = 0; i < names.size(); i++) {
                int index = packageExistsInIndex(names.get(i).getValue(), availablePackages);

                if (index == -1) {
                    unkIndexList.add(new UInteger(i));
                }

                if (!backend.isPackageInstalled(availablePackages.get(i))) {
                    invIndexList.add(new UInteger(i));
                }

                // Throw error if already installed!
                // Before installing, we need to check the package integrity!

                // The installation cannot go forward here if the integrity is false!
            }
        } catch (IOException ex) {
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Errors
        if (!unkIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        if (!invIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        final BooleanList integrities = new BooleanList();
        final StringList publicKeys = new StringList();
        
        for (Identifier packageName : names) {
            boolean integrity = backend.checkPackageIntegrity(packageName.getValue());
            String publicKey = backend.getPublicKey(packageName.getValue());
            integrities.add(integrity);
            publicKeys.add(publicKey);
        }
        
        CheckPackageIntegrityResponse out = new CheckPackageIntegrityResponse();
        out.setBodyElement0(integrities);
        out.setBodyElement1(publicKeys);
        
        return out;
    }

    private static int packageExistsInIndex(String name, StringList packagesList) {
        for (int k = 0; k < packagesList.size(); k++) {
            if (name.equals(packagesList.get(k))) {
                return k;
            }
        }

        return -1;
    }

}
