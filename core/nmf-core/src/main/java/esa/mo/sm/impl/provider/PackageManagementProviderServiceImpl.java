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
import esa.mo.nmf.accesscontrol.AccessControlMode;
import esa.mo.nmf.nmfpackage.metadata.InvalidPackageException;
import esa.mo.sm.impl.util.PMBackend;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.InvalidArgumentException;
import org.ccsds.moims.mo.com.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.mal.InternalException;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.UnknownException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionProvider;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.sm.AppRunningException;
import org.ccsds.moims.mo.sm.ChecksumMismatchException;
import org.ccsds.moims.mo.sm.IntegrityCheckFailedException;
import org.ccsds.moims.mo.sm.PackageInstalledException;
import org.ccsds.moims.mo.sm.PackageNotInstalledException;
import org.ccsds.moims.mo.sm.PermissionsNotAcknowledgedException;
import org.ccsds.moims.mo.sm.packagemanagement.PackageManagementHelper;
import org.ccsds.moims.mo.sm.packagemanagement.PackageManagementServiceInfo;
import org.ccsds.moims.mo.sm.packagemanagement.body.FindPackageResponse;
import org.ccsds.moims.mo.sm.packagemanagement.provider.*;
import org.ccsds.moims.mo.sm.structures.PackageApproval;
import org.ccsds.moims.mo.sm.structures.PackageApprovalList;
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
            throws UnknownException, InternalException, InvalidArgumentException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        FindPackageResponse outList;

        if (names == null) { // Is the input null?
            throw new InvalidArgumentException("The names field must not be NULL");
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
            throw internalError("The packages could not be read", ex);
        }

        // Errors
        if (!unkIndexList.isEmpty()) {
            throw new UnknownException(unkIndexList);
        }

        return outList;
    }

    @Override
    public void install(final PackageApprovalList packages, final InstallInteraction interaction)
            throws UnknownException, PackageInstalledException, IntegrityCheckFailedException,
            ChecksumMismatchException, PermissionsNotAcknowledgedException, InternalException, InvalidArgumentException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        Refusals refusals = new Refusals();

        if (packages == null) { // Is the input null?
            throw new InvalidArgumentException("The packages field must not be NULL");
        }

        BooleanList integrities = new BooleanList(packages.size());

        StringList availablePackages;
        try {
            availablePackages = backend.getListOfPackages();

            for (int i = 0; i < packages.size(); i++) {
                PackageApproval approval = packages.get(i);
                String name = approval.getName().getValue();
                int index = packageExistsInIndex(name, availablePackages);

                if (index == -1) {
                    unkIndexList.add(new UInteger(i));
                    integrities.add(Boolean.FALSE);
                    continue;
                }

                checkPackageIsValid(availablePackages.get(index));

                // Before installing, we need to check the package integrity!
                boolean integrity = backend.checkPackageIntegrity(availablePackages.get(index));
                integrities.add(integrity);

                // An install never replaces an installed package: an App of
                // the same name, whatever its version (except the same SNAPSHOT
                // version, which may be installed again), is replaced with an
                // update, and any other package is installed once per final
                // version.
                if (backend.replacesInstalledPackage(name)) {
                    refusals.installed.add(new UInteger(i));
                }
                refusals.check(i, integrity, approval, availablePackages.get(index));
            }
        } catch (IOException ex) {
            throw internalError("The packages could not be read", ex);
        }

        // Errors
        if (!unkIndexList.isEmpty()) {
            throw new UnknownException(unkIndexList);
        }

        if (!refusals.installed.isEmpty()) {
            throw new PackageInstalledException(refusals.installed);
        }

        refusals.throwIfAny();

        interaction.sendAcknowledgement(integrities);

        for (PackageApproval approval : packages) {
            String name = approval.getName().getValue();
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(
                    Level.INFO, "Installing: {0}", name);

            try {
                backend.install(name, toList(approval.getPermissions()));
            } catch (IOException ex) {
                // The packages before this one stay installed
                interaction.sendError(internalError("The package '" + name + "' could not be installed", ex));
                return;
            }

            if (backend.isPackageInstalled(name)) {
                storePackageInstalled(name, grantedPermissions(approval), interaction.getInteraction());
            }
        }

        interaction.sendResponse();
    }

    @Override
    public void uninstall(final IdentifierList names, final BooleanList keepConfigurations,
            final UninstallInteraction interaction)
            throws UnknownException, PackageNotInstalledException, IntegrityCheckFailedException,
            AppRunningException, InternalException, InvalidArgumentException, MALException {
        interaction.sendAcknowledgement();

        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList notInstalledIndexList = new UIntegerList();
        UIntegerList integrityIndexList = new UIntegerList();

        // Add validation
        if (names == null || keepConfigurations == null) { // Is the input null?
            throw new InvalidArgumentException("The names and keepConfigurations fields must not be NULL");
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
                    notInstalledIndexList.add(new UInteger(i));
                    Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(
                            Level.SEVERE, "The package is not installed!");
                    continue;
                }

                // Before uninstalling, we need to check the package integrity!
                boolean integrity = backend.checkPackageIntegrity(availablePackages.get(index));

                if (!integrity) {
                    integrityIndexList.add(new UInteger(i));
                    Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(
                            Level.SEVERE, "The integrity of the package is bad!");
                }
            }
        } catch (IOException ex) {
            interaction.sendError(internalError("The packages could not be read", ex));
            return;
        }

        // Errors
        if (!unkIndexList.isEmpty()) {
            throw new UnknownException(unkIndexList);
        }

        if (!notInstalledIndexList.isEmpty()) {
            throw new PackageNotInstalledException(notInstalledIndexList);
        }

        if (!integrityIndexList.isEmpty()) {
            throw new IntegrityCheckFailedException(integrityIndexList);
        }

        stopApps(names.stream().map(Identifier::getValue).toList());

        for (int i = 0; i < names.size(); i++) {
            Identifier packageName = names.get(i);
            Boolean keepConfiguration = keepConfigurations.get(i);
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(
                    Level.INFO, "Uninstalling: {0}", packageName.getValue());

            // Capture the version before the receipt is removed by the uninstall
            String version = backend.getPackageVersion(packageName.getValue());
            try {
                backend.uninstall(packageName.getValue(), keepConfiguration);
            } catch (IOException ex) {
                // The packages before this one stay uninstalled
                interaction.sendError(internalError("The package '" + packageName.getValue()
                        + "' could not be uninstalled", ex));
                return;
            }

            if (!backend.isPackageInstalled(packageName.getValue())) {
                storePackageUninstalled(packageName.getValue(), version,
                        keepConfiguration, interaction.getInteraction());
            }
        }

        interaction.sendResponse();
    }

    @Override
    public void update(final PackageApprovalList packages,
            final UpdateInteraction interaction)
            throws UnknownException, PackageNotInstalledException, InvalidArgumentException,
            IntegrityCheckFailedException, ChecksumMismatchException, PermissionsNotAcknowledgedException,
            AppRunningException, InternalException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList notInstalledIndexList = new UIntegerList();
        UIntegerList baselineIndexList = new UIntegerList();
        Refusals refusals = new Refusals();

        // Add validation
        if (packages == null) { // Is the input null?
            throw new InvalidArgumentException("The packages field must not be NULL");
        }

        StringList availablePackages;
        try {
            availablePackages = backend.getListOfPackages();

            for (int i = 0; i < packages.size(); i++) {
                PackageApproval approval = packages.get(i);
                int index = packageExistsInIndex(approval.getName().getValue(), availablePackages);

                if (index == -1) {
                    unkIndexList.add(new UInteger(i));
                    continue;
                }

                checkPackageIsValid(availablePackages.get(index));

                // The package given is the new version, so it is not installed
                // itself: some version of a package with its name must be. The
                // version is not compared, so that a package can also be
                // updated back to an older version, or to the same one.
                if (backend.getPackageVersion(availablePackages.get(index)) == null) {
                    notInstalledIndexList.add(new UInteger(i));
                    Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(
                            Level.SEVERE, "No version of the package {0} is installed!",
                            approval.getName().getValue());
                    continue;
                }

                // Baseline components (nmf, mission, java) are rejected: they
                // are shipped with install and activated with the
                // setPrimaryBaseline action, never updated in place.
                if (backend.isBaselineComponent(availablePackages.get(index))) {
                    baselineIndexList.add(new UInteger(i));
                    continue;
                }

                // Before updating, we need to check the package integrity!
                boolean integrity = backend.checkPackageIntegrity(availablePackages.get(index));
                refusals.check(i, integrity, approval, availablePackages.get(index));
            }
        } catch (IOException ex) {
            throw internalError("The packages could not be read", ex);
        }

        // Errors
        if (!unkIndexList.isEmpty()) {
            throw new UnknownException(unkIndexList);
        }

        if (!notInstalledIndexList.isEmpty()) {
            throw new PackageNotInstalledException(notInstalledIndexList);
        }

        if (!baselineIndexList.isEmpty()) {
            throw new InvalidArgumentException(baselineIndexList);
        }

        refusals.throwIfAny();

        interaction.sendAcknowledgement();

        stopApps(packages.stream().map(approval -> approval.getName().getValue()).toList());

        for (PackageApproval approval : packages) {
            String name = approval.getName().getValue();
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(
                    Level.INFO, "Updating: {0}", name);

            String fromVersion = backend.getPackageVersion(name);
            try {
                backend.update(name, toList(approval.getPermissions()));
            } catch (IOException ex) {
                // The packages before this one stay updated
                interaction.sendError(internalError("The package '" + name + "' could not be updated", ex));
                return;
            }
            String toVersion = backend.getPackageVersion(name);

            if (toVersion != null) {
                storePackageUpdated(name, fromVersion, toVersion,
                        grantedPermissions(approval), interaction.getInteraction());
            }
        }

        interaction.sendResponse();
    }

    /**
     * The packages of an install or update request that are refused, by
     * reason, as the indexes of the request.
     */
    private final class Refusals {

        private final UIntegerList installed = new UIntegerList();
        private final UIntegerList integrity = new UIntegerList();
        private final UIntegerList checksum = new UIntegerList();
        private final UIntegerList permissions = new UIntegerList();

        /**
         * Refuses the package at the given index of the request if its files
         * fail the integrity check, if its file is not the one approved on
         * ground, or if its App's permissions were not all acknowledged.
         */
        void check(int i, boolean valid, PackageApproval approval, String packageName) throws IOException {
            if (!valid) {
                integrity.add(new UInteger(i));
            } else if (!isChecksumAccepted(approval.getChecksum(), packageName)) {
                checksum.add(new UInteger(i));
            } else if (!arePermissionsAcknowledged(approval, packageName)) {
                permissions.add(new UInteger(i));
            }
        }

        void throwIfAny() throws IntegrityCheckFailedException, ChecksumMismatchException,
                PermissionsNotAcknowledgedException {
            if (!integrity.isEmpty()) {
                throw new IntegrityCheckFailedException(integrity);
            }
            if (!checksum.isEmpty()) {
                throw new ChecksumMismatchException(checksum);
            }
            if (!permissions.isEmpty()) {
                throw new PermissionsNotAcknowledgedException(permissions);
            }
        }
    }

    /**
     * Logs a failure of the backend and returns it as a MAL Internal error, so
     * that the consumer is told why the operation failed.
     *
     * @param what What failed.
     * @param ex The failure.
     * @return The error to send to the consumer.
     */
    private static InternalException internalError(String what, IOException ex) {
        Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(Level.SEVERE, what, ex);
        return new InternalException(new Union(what + ": " + ex.getMessage()));
    }

    /**
     * Stops the App of each package before any package is removed, so that a
     * request with an App that is still running removes nothing at all.
     *
     * @param names The names of the packages, in the order of the request.
     * @throws AppRunningException with the indexes of the packages whose App
     * is still running.
     */
    /**
     * Refuses a package that is not valid, such as one whose name is not one
     * plain file name, with the reason, before its name is used for anything.
     */
    private void checkPackageIsValid(final String packageFile) throws InvalidArgumentException, IOException {
        try {
            backend.checkPackageIsValid(packageFile);
        } catch (InvalidPackageException ex) {
            throw new InvalidArgumentException(packageFile + ": " + ex.getMessage());
        }
    }

    private void stopApps(final List<String> names) throws AppRunningException {
        UIntegerList runningIndexList = new UIntegerList();

        for (int i = 0; i < names.size(); i++) {
            if (!backend.stopApp(names.get(i))) {
                runningIndexList.add(new UInteger(i));
                Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(
                        Level.SEVERE, "The App of the package {0} is still running!", names.get(i));
            }
        }

        if (!runningIndexList.isEmpty()) {
            throw new AppRunningException(runningIndexList);
        }
    }

    private void storePackageInstalled(String packageName, StringList permissions,
            MALInteraction interaction) {
        if (comServices == null || comServices.getArchiveService() == null) {
            return;
        }
        try {
            URI triggeredBy = (interaction != null)
                    ? interaction.getMessageHeader().getFromURI() : null;
            String version = backend.getPackageVersion(packageName);
            HeterogeneousList bodies = new HeterogeneousList();
            bodies.add(new PackageInstalled(new Identifier(packageName), version, triggeredBy, permissions));
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
            String toVersion, StringList permissions, MALInteraction interaction) {
        if (comServices == null || comServices.getArchiveService() == null) {
            return;
        }
        try {
            URI triggeredBy = (interaction != null)
                    ? interaction.getMessageHeader().getFromURI() : null;
            HeterogeneousList bodies = new HeterogeneousList();
            bodies.add(new PackageUpdated(new Identifier(packageName),
                    fromVersion, toVersion, triggeredBy, permissions));
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
            MALInteraction interaction) throws UnknownException, InternalException, InvalidArgumentException, MALException {
        UIntegerList unkIndexList = new UIntegerList();

        // Add validation
        if (names == null) { // Is the input null?
            throw new InvalidArgumentException("The names field must not be NULL");
        }

        // A package is checked whether or not it is installed, so that ground
        // can compare it with its own copy before installing it
        StringList availablePackages;
        try {
            availablePackages = backend.getListOfPackages();
        } catch (IOException ex) {
            throw internalError("The packages could not be read", ex);
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
                throw internalError("The checksum of the package '" + packageName.getValue()
                        + "' could not be computed", ex);
            }
            integrities.add(new PackageIntegrity(valid, checksum));
        }

        return integrities;
    }

    /**
     * The system property that makes install and update refuse a package that
     * comes without the checksum it is expected to have. The bootloader sets it
     * from {@code require-package-checksum} in its configuration, which the
     * mission chooses when it generates its filesystem. A checksum is required
     * unless the property is {@code false}, also when the Supervisor starts
     * without it, such as from an IDE.
     */
    public static final String PROPERTY_REQUIRE_CHECKSUM = "esa.mo.nmf.packagemanager.requirePackageChecksum";

    /**
     * Returns whether this provider refuses a package that comes without an
     * expected checksum.
     *
     * @return true if an expected checksum is required for install and update
     */
    public static boolean isChecksumRequired() {
        String value = System.getProperty(PROPERTY_REQUIRE_CHECKSUM);
        return value == null || !"false".equalsIgnoreCase(value.trim());
    }

    /**
     * Returns whether a package file may be used, given the checksum that it is
     * expected to have. A given checksum must match the SHA-256 of the package
     * file as stored. Without checksums, the package is accepted unless this
     * provider requires them.
     *
     * @param expected the checksum that ground expects, or null for none
     * @param packageName the name of the package file
     * @return true if the package file may be used
     * @throws IOException if the package file cannot be read
     */
    boolean isChecksumAccepted(String expected, String packageName) throws IOException {

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

    /**
     * Returns whether every permission that the App in a package declares was
     * acknowledged. What is not acknowledged is refused in enforce mode, logged
     * in audit mode, and ignored when access control is off.
     *
     * @param approval what ground approved for the package
     * @param packageName the name of the package file
     * @return true if the package may be installed or updated
     */
    boolean arePermissionsAcknowledged(PackageApproval approval, String packageName) {
        List<String> declared;
        try {
            declared = backend.getDeclaredPermissions(packageName);
        } catch (IOException ex) {
            Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "The permissions of the package " + packageName + " could not be read", ex);
            return false;
        }
        if (declared == null || declared.isEmpty()) {
            return true;
        }

        List<String> missing = new ArrayList<>(declared);
        missing.removeAll(toList(approval.getPermissions()));
        if (missing.isEmpty()) {
            return true;
        }

        switch (AccessControlMode.current()) {
            case ENFORCE:
                Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(Level.WARNING,
                        "Refused: the package {0} declares permissions that were not acknowledged: {1}",
                        new Object[]{packageName, String.join(", ", missing)});
                return false;
            case AUDIT:
                Logger.getLogger(PackageManagementProviderServiceImpl.class.getName()).log(Level.WARNING,
                        "Audit: the package {0} declares permissions that were not acknowledged: {1};"
                        + " enforce mode would refuse it",
                        new Object[]{packageName, String.join(", ", missing)});
                return true;
            default:
                return true;
        }
    }

    /**
     * Returns the permissions that the App in a package is granted: those it
     * declares that were acknowledged. Null if the package is not an App.
     */
    private StringList grantedPermissions(PackageApproval approval) {
        List<String> declared;
        try {
            declared = backend.getDeclaredPermissions(approval.getName().getValue());
        } catch (IOException ex) {
            return null;
        }
        if (declared == null) {
            return null;
        }
        List<String> acknowledged = toList(approval.getPermissions());
        StringList granted = new StringList();
        for (String permission : declared) {
            if (acknowledged.contains(permission)) {
                granted.add(permission);
            }
        }
        return granted;
    }

    private static List<String> toList(StringList list) {
        return (list == null) ? new ArrayList<>() : new ArrayList<>(list);
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
