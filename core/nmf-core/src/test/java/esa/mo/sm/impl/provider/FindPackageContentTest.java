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
package esa.mo.sm.impl.provider;

import esa.mo.nmf.nmfpackage.NMFPackagePMBackend;
import esa.mo.nmf.nmfpackage.metadata.Metadata;
import esa.mo.nmf.nmfpackage.metadata.MetadataApp;
import esa.mo.nmf.nmfpackage.metadata.MetadataDependency;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.sm.packagemanagement.body.FindPackageResponse;
import org.ccsds.moims.mo.sm.structures.PackageContent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * findPackage gives, for each package file, what its metadata says that it
 * holds, so that tooling can find the App name that the Apps Launcher takes
 * from the file name that Package Management takes.
 */
public class FindPackageContentTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private File packages;
    private PackageManagementProviderServiceImpl service;

    @Before
    public void startPackageManagement() throws Exception {
        packages = tmp.newFolder("packages");
        service = new PackageManagementProviderServiceImpl();
        Field field = PackageManagementProviderServiceImpl.class.getDeclaredField("backend");
        field.setAccessible(true);
        field.set(service, new NMFPackagePMBackend(packages.getAbsolutePath(), null));
    }

    @Test
    public void eachPackageFileGivesItsNameVersionAndType() throws Exception {
        write("hello-world-5.1.nmfpack", new MetadataApp("hello-world", "5.1", "esa.mo.nmf.apps.Hello",
                "hello-world-5.1.jar", null, null, new ArrayList<>(), "6.0"));
        write("commons-lang3-3.14.nmfpack", new MetadataDependency("commons-lang3", "3.14"));

        Assert.assertEquals(new PackageContent(new Identifier("hello-world"), "5.1", Metadata.TYPE_APP),
                contentOf("hello-world-5.1.nmfpack"));
        Assert.assertEquals(new PackageContent(new Identifier("commons-lang3"), "3.14", "dependency"),
                contentOf("commons-lang3-3.14.nmfpack"));
    }

    @Test
    public void aFileThatCannotBeReadGetsAnEntryWithNoContent() throws Exception {
        Files.writeString(new File(packages, "broken-1.0.nmfpack").toPath(), "not a package");

        Assert.assertEquals(new PackageContent(null, null, null), contentOf("broken-1.0.nmfpack"));
    }

    @Test
    public void theContentsAreInTheOrderOfTheNames() throws Exception {
        write("a-1.0.nmfpack", new MetadataDependency("a", "1.0"));
        write("b-2.0.nmfpack", new MetadataDependency("b", "2.0"));

        IdentifierList all = new IdentifierList();
        all.add(new Identifier("*"));
        FindPackageResponse response = service.findPackage(all, null);

        Assert.assertEquals(response.getNames().size(), response.getContents().size());
        for (int i = 0; i < response.getNames().size(); i++) {
            String file = response.getNames().get(i).getValue();
            Assert.assertEquals(file, response.getContents().get(i).getName().getValue()
                    + "-" + response.getContents().get(i).getVersion() + ".nmfpack");
        }
    }

    private PackageContent contentOf(String file) throws Exception {
        IdentifierList names = new IdentifierList();
        names.add(new Identifier(file));
        FindPackageResponse response = service.findPackage(names, null);
        Assert.assertEquals(1, response.getContents().size());
        return response.getContents().get(0);
    }

    /** Writes a package file holding only its metadata. */
    private void write(String fileName, Metadata metadata) throws IOException {
        metadata.addProperty(Metadata.PACKAGE_METADATA_VERSION, Metadata.METADATA_VERSION_LATEST);
        metadata.addProperty(Metadata.FILE_COUNT, "0");
        ByteArrayOutputStream properties = new ByteArrayOutputStream();
        metadata.store(properties);
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(new File(packages, fileName)))) {
            zip.putNextEntry(new ZipEntry(Metadata.FILENAME));
            zip.write(properties.toByteArray());
            zip.closeEntry();
        }
    }
}
