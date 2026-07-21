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
package esa.mo.com.impl.provider;

import org.ccsds.moims.mo.mal.structures.File;
import org.ccsds.moims.mo.mal.structures.FileList;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Tests for AreaXMLLoader.
 */
public class DirectoryProviderServiceImplTest {

    @Test
    public void loadAreaXML_wildcard_returnsAllXmlFiles() throws Exception {
        FileList result = AreaXMLExtractor.loadAreaXML("*");

        assertNotNull("Wildcard should return a non-null FileList", result);
        assertFalse("Wildcard should return at least one XML file", result.isEmpty());

        for (File file : result) {
            assertNotNull("File entry must not be null", file);
            assertTrue("File name must end with .xml", file.getName().endsWith(".xml"));
            assertEquals("MIME type must be application/xml", "application/xml", file.getMimeType());
            assertTrue("File content must not be empty", file.getContent().getValue().length > 0);
        }

        // The COM API jar is always on the classpath, so area051-COM.xml must be present
        Set<String> names = result.stream().map(File::getName).collect(Collectors.toSet());
        assertTrue("area051-COM.xml must be discoverable from the classpath", names.contains("area051-COM.xml"));
    }
}
