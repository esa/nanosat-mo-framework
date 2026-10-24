/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft - v2.4
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
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.sm.impl.util.PMBackend;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.InvalidArgumentException;
import org.ccsds.moims.mo.com.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.UnknownException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionProvider;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.sm.packagemanagement.PackageManagementHelper;
import org.ccsds.moims.mo.sm.packagemanagement.PackageManagementServiceInfo;
import org.ccsds.moims.mo.sm.packagemanagement.body.FindPackageResponse;
import org.ccsds.moims.mo.sm.packagemanagement.provider.*;
import org.ccsds.moims.mo.sm.structures.PackageInstalled;
import org.ccsds.moims.mo.sm.structures.PackageIntegrity;
import org.ccsds.moims.mo.sm.structures.PackageIntegrityList;
import org.ccsds.moims.mo.sm.structures.PackageUninstalled;
import org.ccsds.moims.mo.sm.structures.PackageUpdated;

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
     * Default constructor.
     */
    public PackageManagementProviderServiceImpl() {
    }

    /**
     * Initializes the Package Management service provider.
     *
     * @param comServices The COM services.
     * @param backend The backend to the package manager.
     * @throws MALException if the service could not be initialized.
     */
    public synchronized void init(final COMServicesProvider comServices,
            final PMBackend backend) throws MALException {
        long timestamp = System.currentTimeMillis();

        if (backend == null) {
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).severe(
                    "Package Management service could not be initialized! " + "The backend object cannot be null.");
            return;
        }

        this.comServices = comServices;
        this.backend = backend;

        // shut down old service transport
        if (null != packageManagementServiceProvider) {
            connection.closeAll();
        }

        packageManagementServiceProvider = connection.startService(
                PackageManagementServiceInfo.PACKAGEMANAGEMENT_SERVICE_NAME.toString(),
                PackageManagementHelper.PACKAGEMANAGEMENT_SERVICE, false, this);

        running = true;
        initialiased = true;
        timestamp = System.currentTimeMillis() - timestamp;
        Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).info(
                "Package Management service: READY! (" + timestamp + " ms)");
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
    public FindPackageResponse findPackage(IdentifierList names, MALInteraction interaction)
            throws UnknownException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        FindPackageResponse outList;

        if (names == null) { // Is the input null?
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

            outList = new FindPackageResponse(packages, installed);
        } catch (IOException ex) {
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(Level.SEVERE,
                    "The list of packages could not be retrieved!", ex);

            // Just return empty lists
            outList = new FindPackageResponse(new IdentifierList(), new BooleanList());
        }

        // Errors
        if (!unkIndexList.isEmpty()) {
            throw new UnknownException(unkIndexList);
        }

        return outList;
    }

    @Override
    public void install(final IdentifierList names, final StringList checksums,
            final InstallInteraction interaction)
            throws UnknownException, InvalidArgumentException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        if (names == null) { // Is the input null?
            throw new IllegalArgumentException("names argument must not be null");
        }
        checkChecksumsMatchNames(names, checksums);

        BooleanList integrities = new BooleanList(names.size());

        StringList availablePackages;
        try {
            availablePackages = backend.getListOfPackages();

            for (int i = 0; i < names.size(); i++) {
                int index = packageExistsInIndex(names.get(i).getValue(), availablePackages);

                if (index == -1) {
                    unkIndexList.add(new UInteger(i));
                    integrities.add(Boolean.FALSE);
                    continue;
                }

                // Before installing, we need to check the package integrity!
                boolean integrity = backend.checkPackageIntegrity(availablePackages.get(index));
                integrities.add(integrity);

                // Reject if the integrity is false, if the package file is not the
                // one approved on ground, or if a final (non-SNAPSHOT) build of
                // this version is already installed. A SNAPSHOT version is not
                // final and may always be re-installed (overridden).
                if (!integrity
                        || !isChecksumAccepted(checksums, i, availablePackages.get(index))
                        || backend.isFinalVersionInstalled(names.get(i).getValue())) {
                    invIndexList.add(new UInteger(i));
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Errors
        if (!unkIndexList.isEmpty()) {
            throw new UnknownException(unkIndexList);
        }

        if (!invIndexList.isEmpty()) {
            throw new InvalidArgumentException(invIndexList);
        }

        interaction.sendAcknowledgement(integrities);

        for (Identifier packageName : names) {
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(
                    Level.INFO, "Installing: {0}", packageName.getValue());

            backend.install(packageName.getValue());

            if (backend.isPackageInstalled(packageName.getValue())) {
                storePackageInstalled(packageName.getValue(), interaction.getInteraction());
            }
        }

        interaction.sendResponse();
    }

    @Override
    public void uninstall(final IdentifierList names, final BooleanList keepConfigurations,
            final UninstallInteraction interaction) throws UnknownException, InvalidArgumentException, MALException {
        interaction.sendAcknowledgement();

        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        // Add validation
        if (names == null || keepConfigurations == null) { // Is the input null?
            throw new IllegalArgumentException("names argument and keepConfigurations argument must not be null");
        }

        StringList availablePackages;
        try {
            availablePackages = backend.getListOfPackages();

            for (int i = 0; i < names.size(); i++) {
                int index = packageExistsInIndex(names.get(i).getValue(), availablePackages);

                if (index == -1) {
                    unkIndexList.add(new UInteger(i));
                    continue;
                }

                if (!backend.isPackageInstalled(names.get(i).getValue())) {
                    invIndexList.add(new UInteger(i));
                    Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(
                            Level.SEVERE, "The package is not installed!");
                    continue;
                }

                // Before uninstalling, we need to check the package integrity!
                boolean integrity = backend.checkPackageIntegrity(availablePackages.get(index));

                if (!integrity) {
                    invIndexList.add(new UInteger(i));
                    Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(
                            Level.SEVERE, "The integrity of the package is bad!");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(Level.SEVERE,
                    "The list of packages could not be retrieved!", ex);
        }

        // Errors
        if (!unkIndexList.isEmpty()) {
            throw new UnknownException(unkIndexList);
        }

        if (!invIndexList.isEmpty()) {
            throw new InvalidArgumentException(invIndexList);
        }

        for (int i = 0; i < names.size(); i++) {
            Identifier packageName = names.get(i);
            Boolean keepConfiguration = keepConfigurations.get(i);
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(
                    Level.INFO, "Uninstalling: {0}", packageName.getValue());

            // Capture the version before the receipt is removed by the uninstall
            String version = backend.getPackageVersion(packageName.getValue());
            backend.uninstall(packageName.getValue(), keepConfiguration);

            if (!backend.isPackageInstalled(packageName.getValue())) {
                storePackageUninstalled(packageName.getValue(), version,
                        keepConfiguration, interaction.getInteraction());
            }
        }

        interaction.sendResponse();
    }

    @Override
    public void update(final IdentifierList names, final StringList checksums,
            final UpdateInteraction interaction) throws UnknownException, InvalidArgumentException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        // Add validation
        if (names == null) { // Is the input null?
            throw new IllegalArgumentException("names argument must not be null");
        }
        checkChecksumsMatchNames(names, checksums);

        StringList availablePackages;
        try {
            availablePackages = backend.getListOfPackages();

            for (int i = 0; i < names.size(); i++) {
                int index = packageExistsInIndex(names.get(i).getValue(), availablePackages);

                if (index == -1) {
                    unkIndexList.add(new UInteger(i));
                    continue;
                }

                // Before updating, we need to check the package integrity!
                // The update cannot go forward here if the integrity is false!
                // Baseline components (nmf, mission, java) are also rejected:
                // they are shipped with install and activated with the
                // setPrimaryBaseline action, never updated in place.
                boolean integrity = backend.checkPackageIntegrity(availablePackages.get(index));
                if (!integrity
                        || !isChecksumAccepted(checksums, i, availablePackages.get(index))
                        || backend.isBaselineComponent(availablePackages.get(index))) {
                    invIndexList.add(new UInteger(i));
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Errors
        if (!unkIndexList.isEmpty()) {
            throw new UnknownException(unkIndexList);
        }

        if (!invIndexList.isEmpty()) {
            throw new InvalidArgumentException(invIndexList);
        }

        interaction.sendAcknowledgement();

        for (Identifier packageName : names) {
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(
                    Level.INFO, "Updating: {0}", packageName.getValue());

            String fromVersion = backend.getPackageVersion(packageName.getValue());
            backend.update(packageName.getValue());
            String toVersion = backend.getPackageVersion(packageName.getValue());

            if (toVersion != null) {
                storePackageUpdated(packageName.getValue(), fromVersion,
                        toVersion, interaction.getInteraction());
            }
        }

        interaction.sendResponse();
    }

    private void storePackageInstalled(String packageName, MALInteraction interaction) {
        if (comServices == null || comServices.getArchiveService() == null) {
            return;
        }
        try {
            URI triggeredBy = (interaction != null)
                    ? interaction.getMessageHeader().getFromURI() : null;
            String version = backend.getPackageVersion(packageName);
            HeterogeneousList bodies = new HeterogeneousList();
            bodies.add(new PackageInstalled(new Identifier(packageName), version, triggeredBy));
            ArchiveDetailsList archDetails = HelperArchive.generateArchiveDetailsList(
                    null, connection.getPrimaryConnectionDetails().getProviderURI());
            comServices.getArchiveService().store(
                    true, PackageManagementServiceInfo.PACKAGEINSTALLED_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(), archDetails, bodies, null);
        } catch (org.ccsds.moims.mo.com.DuplicateException | InvalidArgumentException | MALException ex) {
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(
                    Level.WARNING, "Could not store PackageInstalled in archive", ex);
        }
    }

    private void storePackageUninstalled(String packageName, String version,
            Boolean keptConfigurations, MALInteraction interaction) {
        if (comServices == null || comServices.getArchiveService() == null) {
            return;
        }
        try {
            URI triggeredBy = (interaction != null)
                    ? interaction.getMessageHeader().getFromURI() : null;
            HeterogeneousList bodies = new HeterogeneousList();
            bodies.add(new PackageUninstalled(new Identifier(packageName),
                    version, keptConfigurations, triggeredBy));
            ArchiveDetailsList archDetails = HelperArchive.generateArchiveDetailsList(
                    null, connection.getPrimaryConnectionDetails().getProviderURI());
            comServices.getArchiveService().store(
                    true, PackageManagementServiceInfo.PACKAGEUNINSTALLED_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(), archDetails, bodies, null);
        } catch (org.ccsds.moims.mo.com.DuplicateException | InvalidArgumentException | MALException ex) {
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(
                    Level.WARNING, "Could not store PackageUninstalled in archive", ex);
        }
    }

    private void storePackageUpdated(String packageName, String fromVersion,
            String toVersion, MALInteraction interaction) {
        if (comServices == null || comServices.getArchiveService() == null) {
            return;
        }
        try {
            URI triggeredBy = (interaction != null)
                    ? interaction.getMessageHeader().getFromURI() : null;
            HeterogeneousList bodies = new HeterogeneousList();
            bodies.add(new PackageUpdated(new Identifier(packageName),
                    fromVersion, toVersion, triggeredBy));
            ArchiveDetailsList archDetails = HelperArchive.generateArchiveDetailsList(
                    null, connection.getPrimaryConnectionDetails().getProviderURI());
            comServices.getArchiveService().store(
                    true, PackageManagementServiceInfo.PACKAGEUPDATED_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(), archDetails, bodies, null);
        } catch (org.ccsds.moims.mo.com.DuplicateException | InvalidArgumentException | MALException ex) {
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(
                    Level.WARNING, "Could not store PackageUpdated in archive", ex);
        }
    }

    @Override
    public PackageIntegrityList checkPackageIntegrity(IdentifierList names,
            MALInteraction interaction) throws UnknownException, MALException {
        UIntegerList unkIndexList = new UIntegerList();

        // Add validation
        if (names == null) { // Is the input null?
            throw new IllegalArgumentException("names argument must not be null");
        }

        // A package is checked whether or not it is installed, so that ground
        // can compare it with its own copy before installing it
        StringList availablePackages;
        try {
            availablePackages = backend.getListOfPackages();
        } catch (IOException ex) {
            throw new MALException("The list of packages could not be read!", ex);
        }

        for (int i = 0; i < names.size(); i++) {
            if (packageExistsInIndex(names.get(i).getValue(), availablePackages) == -1) {
                unkIndexList.add(new UInteger(i));
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) {
            throw new UnknownException(unkIndexList);
        }

        final PackageIntegrityList integrities = new PackageIntegrityList();

        for (Identifier packageName : names) {
            boolean valid = backend.checkPackageIntegrity(packageName.getValue());
            String checksum;
            try {
                checksum = backend.getPackageChecksum(packageName.getValue());
            } catch (IOException ex) {
                throw new MALException("The checksum of the package could not be computed: "
                        + packageName.getValue(), ex);
            }
            integrities.add(new PackageIntegrity(valid, checksum));
        }

        return integrities;
    }

    /**
     * The system property that makes install and update refuse a package that
     * comes without the checksum it is expected to have. The bootloader sets it
     * from {@code require-package-checksum} in its configuration, which the
     * mission chooses when it generates its filesystem. Off by default.
     */
    public static final String PROPERTY_REQUIRE_CHECKSUM = "esa.mo.nmf.packagemanager.requirePackageChecksum";

    /**
     * Returns whether this provider refuses a package that comes without an
     * expected checksum.
     *
     * @return true if an expected checksum is required for install and update
     */
    public static boolean isChecksumRequired() {
        return Boolean.getBoolean(PROPERTY_REQUIRE_CHECKSUM);
    }

    private static void checkChecksumsMatchNames(IdentifierList names, StringList checksums) {
        if (checksums != null && checksums.size() != names.size()) {
            throw new IllegalArgumentException("checksums must have one element for each name, or be null");
        }
    }

    /**
     * Returns whether a package file may be used, given the checksum that it is
     * expected to have. A given checksum must match the SHA-256 of the package
     * file as stored. Without checksums, the package is accepted unless this
     * provider requires them.
     *
     * @param checksums the expected checksums, one for each name, or null
     * @param index the index of the package in the request
     * @param packageName the name of the package file
     * @return true if the package file may be used
     * @throws IOException if the package file cannot be read
     */
    boolean isChecksumAccepted(StringList checksums, int index, String packageName) throws IOException {
        String expected = (checksums == null) ? null : checksums.get(index);

        if (expected == null) {
            if (isChecksumRequired()) {
                Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(Level.WARNING,
                        "The package {0} is refused: it has no expected checksum, which this provider requires",
                        packageName);
                return false;
            }
            return true;
        }

        String actual = backend.getPackageChecksum(packageName);
        if (!expected.trim().equalsIgnoreCase(actual)) {
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "The package {0} is refused: its checksum {1} is not the expected {2}",
                    new Object[]{packageName, actual, expected});
            return false;
        }
        return true;
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
