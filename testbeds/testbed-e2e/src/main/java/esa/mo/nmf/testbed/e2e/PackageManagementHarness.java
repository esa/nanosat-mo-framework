/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
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
package esa.mo.nmf.testbed.e2e;

import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.nmf.NMFConsumer;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.HeterogeneousList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.sm.packagemanagement.PackageManagementServiceInfo;
import org.ccsds.moims.mo.sm.packagemanagement.body.FindPackageResponse;
import org.ccsds.moims.mo.sm.packagemanagement.consumer.PackageManagementAdapter;
import org.ccsds.moims.mo.sm.packagemanagement.consumer.PackageManagementStub;

/**
 * Drives the Supervisor's Package Management service for end-to-end tests and
 * queries the COM archive for the package lifecycle objects (PackageInstalled,
 * PackageUninstalled, PackageUpgraded).
 *
 * <p>
 * Requires a running Supervisor; construct with the same
 * {@link SupervisorHarness} instance used by the test class and call
 * {@link SupervisorHarness#setUp()} before calling {@link #connect()} here.
 */
public class PackageManagementHarness {

    private static final Logger LOGGER = Logger.getLogger(PackageManagementHarness.class.getName());
    private static final long OPERATION_TIMEOUT_SECONDS = 30;
    private static final String METADATA_FILE = "package-metadata.properties";
    private static final String METADATA_VERSION_PROPERTY = "info.version";

    private final SupervisorHarness supervisorHarness;

    private GroundMOAdapterImpl adapter;
    private PackageManagementStub stub;

    public PackageManagementHarness(SupervisorHarness supervisorHarness) {
        this.supervisorHarness = supervisorHarness;
    }

    /**
     * Connects to the Supervisor's Package Management service.
     *
     * @throws IOException if the Supervisor could not be reached.
     */
    public void connect() throws IOException {
        String directoryURIStr = supervisorHarness.getDirectoryURI();
        LOGGER.info("Connecting to Directory service at: " + directoryURIStr);

        try {
            ProviderList providers = NMFConsumer.retrieveProvidersFromDirectory(
                    new URI(directoryURIStr));

            Provider supervisorProvider = findSupervisorProvider(providers);
            if (supervisorProvider == null) {
                throw new IOException("No Supervisor provider found at " + directoryURIStr);
            }

            adapter = new GroundMOAdapterImpl(supervisorProvider);
            stub = adapter.getSMServices().getPackageManagementService().getPackageManagementStub();
        } catch (MALException | MALInteractionException | java.net.MalformedURLException e) {
            throw new IOException("Failed to connect to the Package Management service: "
                    + e.getMessage(), e);
        }
    }

    /**
     * Lists all packages known to the provider.
     *
     * @return the findPackage response (package names and installed flags).
     * @throws IOException if the operation fails.
     */
    public FindPackageResponse findAllPackages() throws IOException {
        IdentifierList names = new IdentifierList();
        names.add(new Identifier("*"));
        try {
            return stub.findPackage(names);
        } catch (MALException | MALInteractionException e) {
            throw new IOException("findPackage failed: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the file name of the single package whose name starts with the
     * given prefix, e.g. "benchmark-" resolves to
     * "benchmark-5.0-SNAPSHOT.nmfpack".
     *
     * @param prefix the package file name prefix.
     * @return the full package file name.
     * @throws IOException if no such package exists.
     */
    public String findPackageByPrefix(String prefix) throws IOException {
        FindPackageResponse response = findAllPackages();
        for (Identifier name : response.getNames()) {
            if (name.getValue().startsWith(prefix)) {
                return name.getValue();
            }
        }
        throw new IOException("No package starting with '" + prefix + "' found.");
    }

    /**
     * Invokes the install operation and waits for its outcome.
     *
     * @param packageFileName the package file name.
     * @return null on success, or the MO error returned by the provider.
     * @throws IOException if the operation could not be invoked or timed out.
     */
    public MOErrorException install(String packageFileName) throws IOException {
        return invokeOperation(packageFileName, Operation.INSTALL, null);
    }

    /**
     * Invokes the uninstall operation and waits for its outcome.
     *
     * @param packageFileName the package file name.
     * @param keepConfigurations whether the package configurations are kept.
     * @return null on success, or the MO error returned by the provider.
     * @throws IOException if the operation could not be invoked or timed out.
     */
    public MOErrorException uninstall(String packageFileName, boolean keepConfigurations)
            throws IOException {
        return invokeOperation(packageFileName, Operation.UNINSTALL, keepConfigurations);
    }

    /**
     * Invokes the upgrade operation and waits for its outcome.
     *
     * @param packageFileName the package file name.
     * @return null on success, or the MO error returned by the provider.
     * @throws IOException if the operation could not be invoked or timed out.
     */
    public MOErrorException upgrade(String packageFileName) throws IOException {
        return invokeOperation(packageFileName, Operation.UPGRADE, null);
    }

    private enum Operation {
        INSTALL, UNINSTALL, UPGRADE
    }

    private MOErrorException invokeOperation(String packageFileName, Operation op,
            Boolean keepConfigurations) throws IOException {
        IdentifierList names = new IdentifierList();
        names.add(new Identifier(packageFileName));

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<MOErrorException> error = new AtomicReference<>();

        PackageManagementAdapter callback = new PackageManagementAdapter() {
            @Override
            public void installResponseReceived(MALMessageHeader msgHeader, java.util.Map qosProperties) {
                latch.countDown();
            }

            @Override
            public void installAckErrorReceived(MALMessageHeader msgHeader,
                    MOErrorException err, java.util.Map qosProperties) {
                error.set(err);
                latch.countDown();
            }

            @Override
            public void installResponseErrorReceived(MALMessageHeader msgHeader,
                    MOErrorException err, java.util.Map qosProperties) {
                error.set(err);
                latch.countDown();
            }

            @Override
            public void uninstallResponseReceived(MALMessageHeader msgHeader, java.util.Map qosProperties) {
                latch.countDown();
            }

            @Override
            public void uninstallAckErrorReceived(MALMessageHeader msgHeader,
                    MOErrorException err, java.util.Map qosProperties) {
                error.set(err);
                latch.countDown();
            }

            @Override
            public void uninstallResponseErrorReceived(MALMessageHeader msgHeader,
                    MOErrorException err, java.util.Map qosProperties) {
                error.set(err);
                latch.countDown();
            }

            @Override
            public void upgradeResponseReceived(MALMessageHeader msgHeader, java.util.Map qosProperties) {
                latch.countDown();
            }

            @Override
            public void upgradeAckErrorReceived(MALMessageHeader msgHeader,
                    MOErrorException err, java.util.Map qosProperties) {
                error.set(err);
                latch.countDown();
            }

            @Override
            public void upgradeResponseErrorReceived(MALMessageHeader msgHeader,
                    MOErrorException err, java.util.Map qosProperties) {
                error.set(err);
                latch.countDown();
            }
        };

        try {
            // Use the async variants so the ACK is only delivered through the
            // listener. The sync variants hand the same lazily-decoded ACK body
            // to both the calling thread and the listener dispatch thread,
            // which races in the MAL layer of mo-services-java 14.0. The race
            // is fixed in 14.1 (synchronized LazyMessageBody.decodeMessageBody).
            switch (op) {
                case INSTALL:
                    stub.asyncInstall(names, callback);
                    break;
                case UNINSTALL:
                    BooleanList keep = new BooleanList();
                    keep.add(keepConfigurations);
                    stub.asyncUninstall(names, keep, callback);
                    break;
                case UPGRADE:
                    stub.asyncUpgrade(names, callback);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown operation: " + op);
            }
        } catch (MALInteractionException e) {
            // The ACK itself can carry the error for early validation failures
            return e.getStandardError();
        } catch (MALException e) {
            throw new IOException(op + " failed: " + e.getMessage(), e);
        }

        try {
            if (!latch.await(OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                throw new IOException(op + " timed out after " + OPERATION_TIMEOUT_SECONDS + " s.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(op + " was interrupted.", e);
        }

        return error.get();
    }

    /**
     * Queries the archive for all PackageInstalled COM objects.
     *
     * @return list of matching archive objects, never null.
     * @throws IOException if the query fails.
     */
    public List<ArchivePersistenceObject> queryPackageInstalled() throws IOException {
        return queryByObjectType(PackageManagementServiceInfo.PACKAGEINSTALLED_OBJECT_TYPE);
    }

    /**
     * Queries the archive for all PackageUninstalled COM objects.
     *
     * @return list of matching archive objects, never null.
     * @throws IOException if the query fails.
     */
    public List<ArchivePersistenceObject> queryPackageUninstalled() throws IOException {
        return queryByObjectType(PackageManagementServiceInfo.PACKAGEUNINSTALLED_OBJECT_TYPE);
    }

    /**
     * Queries the archive for all PackageUpgraded COM objects.
     *
     * @return list of matching archive objects, never null.
     * @throws IOException if the query fails.
     */
    public List<ArchivePersistenceObject> queryPackageUpgraded() throws IOException {
        return queryByObjectType(PackageManagementServiceInfo.PACKAGEUPGRADED_OBJECT_TYPE);
    }

    private List<ArchivePersistenceObject> queryByObjectType(ObjectType objType) throws IOException {
        List<ArchivePersistenceObject> results = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch latch = new CountDownLatch(1);

        try {
            // related=0 is the wildcard: match all objects of the given type
            adapter.getCOMServices().getArchiveService().getArchiveStub().query(
                    Boolean.TRUE, objType, new ArchiveQuery(0L), null,
                    new ArchiveAdapter() {
                        @Override
                        public void queryUpdateReceived(MALMessageHeader msgHeader,
                                ObjectType receivedObjType, IdentifierList domain,
                                ArchiveDetailsList details,
                                HeterogeneousList bodies,
                                java.util.Map qosProperties) {
                            if (details != null) {
                                for (int i = 0; i < details.size(); i++) {
                                    Element body = (bodies != null && i < bodies.size())
                                            ? (Element) bodies.get(i) : null;
                                    results.add(new ArchivePersistenceObject(
                                            receivedObjType, domain,
                                            details.get(i).getId(), details.get(i), body));
                                }
                            }
                        }

                        @Override
                        public void queryResponseReceived(MALMessageHeader msgHeader,
                                java.util.Map qosProperties) {
                            latch.countDown();
                        }

                        @Override
                        public void queryAckErrorReceived(MALMessageHeader msgHeader,
                                MOErrorException error, java.util.Map qosProperties) {
                            latch.countDown();
                        }
                    });
        } catch (MALException | MALInteractionException e) {
            throw new IOException("Archive query failed: " + e.getMessage(), e);
        }
        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return new ArrayList<>(results);
    }

    /**
     * Manufactures a newer version of an existing package in the Supervisor's
     * packages folder: copies the .nmfpack (a zip) and rewrites the version in
     * its package-metadata.properties. The jar content is unchanged, which is
     * fine for exercising the upgrade bookkeeping.
     *
     * @param packageFileName the existing package file name, e.g.
     * "benchmark-5.0-SNAPSHOT.nmfpack".
     * @param newVersion the version for the manufactured package, e.g. "5.1".
     * @return the file name of the manufactured package, e.g.
     * "benchmark-5.1.nmfpack".
     * @throws IOException if the package could not be created.
     */
    public String createUpgradedPackage(String packageFileName, String newVersion) throws IOException {
        File packagesFolder = new File(supervisorHarness.getNmfDir(), "packages");
        File original = new File(packagesFolder, packageFileName);
        if (!original.exists()) {
            throw new IOException("Package file not found: " + original.getAbsolutePath());
        }

        // benchmark-5.0-SNAPSHOT.nmfpack -> benchmark-5.1.nmfpack
        String baseName = packageFileName.substring(0, packageFileName.indexOf('-'));
        String suffix = packageFileName.substring(packageFileName.lastIndexOf('.'));
        String newFileName = baseName + "-" + newVersion + suffix;
        File upgraded = new File(packagesFolder, newFileName);

        try (ZipFile zipFile = new ZipFile(original);
                ZipOutputStream out = new ZipOutputStream(new FileOutputStream(upgraded))) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                out.putNextEntry(new ZipEntry(entry.getName()));

                try (InputStream in = zipFile.getInputStream(entry)) {
                    if (METADATA_FILE.equals(entry.getName())) {
                        Properties props = new Properties();
                        props.load(in);
                        props.setProperty(METADATA_VERSION_PROPERTY, newVersion);
                        props.store(out, null);
                    } else {
                        byte[] buffer = new byte[8192];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                    }
                }
                out.closeEntry();
            }
        }

        LOGGER.info("Created upgraded package: " + upgraded.getAbsolutePath());
        return newFileName;
    }

    private Provider findSupervisorProvider(ProviderList providers) {
        // Select the provider classified as the Supervisor
        for (Provider p : providers) {
            if (NMFProviderType.SUPERVISOR.equals(p.getProviderType())) {
                return p;
            }
        }
        return providers.isEmpty() ? null : providers.get(0);
    }
}
