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
package esa.mo.sm.testbed;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.nmf.nmfpackage.NMFPackagePMBackend;
import esa.mo.nmf.nmfpackage.metadata.Metadata;
import esa.mo.sm.impl.consumer.PackageManagementConsumerServiceImpl;
import esa.mo.sm.impl.provider.PackageManagementProviderServiceImpl;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionProvider;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperMisc;

/**
 * Manages the lifecycle of the Software Management service providers and
 * consumers for the testbed. It stands up a COM stack, a Package Management
 * provider backed by a temporary packages folder, and a matching consumer.
 * Call {@link #setUp()} before tests and {@link #tearDown()} after.
 *
 * <p>Tests drop {@code .nmfpack} files into the packages folder with
 * {@link #writePackage(String, Metadata)}; the backend reads the folder on
 * every call, so packages may be written after {@link #setUp()}.
 */
public class SetUpSMServices {

    private COMServicesProvider comServicesProvider;
    private PackageManagementProviderServiceImpl packageManagementProvider;
    private PackageManagementConsumerServiceImpl packageManagementConsumer;
    private File packagesFolder;

    public void setUp() throws IOException, MALException {
        HelperMisc.loadPropertiesFile();
        ConnectionProvider.resetURILinksFile();

        // Keep the COM archive in memory, and point the installed-package
        // receipts at an empty temporary folder, so that every package the
        // tests write reads back as not installed.
        System.setProperty("esa.nmf.archive.persistence.jdbc.url", "jdbc:sqlite::memory:");
        File receipts = Files.createTempDirectory("testbed-sm-receipts").toFile();
        System.setProperty("esa.mo.nmf.nmfpackage.receipts", receipts.getAbsolutePath());

        packagesFolder = Files.createTempDirectory("testbed-sm-packages").toFile();

        comServicesProvider = new COMServicesProvider();
        comServicesProvider.init();

        NMFPackagePMBackend backend = new NMFPackagePMBackend(packagesFolder.getAbsolutePath(), null);
        packageManagementProvider = new PackageManagementProviderServiceImpl();
        packageManagementProvider.init(comServicesProvider, backend);

        SingleConnectionDetails details = packageManagementProvider.getConnection().getConnectionDetails();
        packageManagementConsumer = new PackageManagementConsumerServiceImpl(details, null);
    }

    public void tearDown() {
        if (packageManagementConsumer != null) {
            packageManagementConsumer.closeConnection();
        }
        if (packageManagementProvider != null) {
            packageManagementProvider.close();
        }
        if (comServicesProvider != null) {
            comServicesProvider.closeAll();
        }
    }

    /**
     * Writes a package file holding only its metadata into the packages folder.
     *
     * @param fileName the package file name, such as hello-world-5.1.nmfpack
     * @param metadata the metadata of the package
     * @throws IOException if the package file cannot be written
     */
    public void writePackage(String fileName, Metadata metadata) throws IOException {
        metadata.addProperty(Metadata.PACKAGE_METADATA_VERSION, Metadata.METADATA_VERSION_LATEST);
        metadata.addProperty(Metadata.FILE_COUNT, "0");
        ByteArrayOutputStream properties = new ByteArrayOutputStream();
        metadata.store(properties);
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(new File(packagesFolder, fileName)))) {
            zip.putNextEntry(new ZipEntry(Metadata.FILENAME));
            zip.write(properties.toByteArray());
            zip.closeEntry();
        }
    }

    public PackageManagementConsumerServiceImpl getPackageManagementConsumer() {
        return packageManagementConsumer;
    }
}
