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

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.File;
import org.ccsds.moims.mo.mal.structures.FileList;
import org.ccsds.moims.mo.mal.structures.ULong;

/**
 * Loads MO area XML service definitions from classpath resources. XML files are
 * bundled inside the API jars under the {@code xml/} path prefix.
 */
public class AreaXMLExtractor {

    private static final Logger LOGGER = Logger.getLogger(AreaXMLExtractor.class.getName());

    private AreaXMLExtractor() {
    }

    /**
     * Returns the XML files matching the given filename. Use {@code "*"} as a
     * wildcard to return all XML files found on the classpath.
     *
     * @param filename the exact filename (e.g. {@code "area002-COM.xml"}) or
     * {@code "*"}
     * @return a FileList, or {@code null} if no files were found for the
     * wildcard case
     * @throws IOException if scanning the classpath or reading a resource fails
     */
    public static FileList loadAreaXML(String filename) throws IOException {
        final boolean wildcard = "*".equals(filename);
        final List<String> availableXMLs = discoverXmlFilenames();

        FileList result = new FileList();
        for (String availableXML : availableXMLs) {
            if (!wildcard && !availableXML.equals(filename)) {
                continue;
            }

            URL resource = AreaXMLExtractor.class.getClassLoader().getResource("xml/" + availableXML);
            if (resource == null) {
                continue;
            }

            try (InputStream is = resource.openStream()) {
                byte[] content = is.readAllBytes();
                result.add(new File(
                        availableXML,
                        "application/xml",
                        null,
                        null,
                        new ULong(BigInteger.valueOf(content.length)),
                        new Blob(content),
                        null));
            }
        }

        return result.isEmpty() ? null : result;
    }

    private static List<String> discoverXmlFilenames() throws IOException {
        Set<String> filenames = new LinkedHashSet<>();
        Enumeration<URL> resources = AreaXMLExtractor.class.getClassLoader().getResources("xml");
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            if ("jar".equals(url.getProtocol())) {
                JarURLConnection conn = (JarURLConnection) url.openConnection();
                try (JarFile jar = conn.getJarFile()) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        String name = entries.nextElement().getName();
                        if (name.startsWith("xml/") && name.endsWith(".xml")) {
                            filenames.add(name.substring(4)); // strip "xml/" prefix
                        }
                    }
                }
            } else if ("file".equals(url.getProtocol())) {
                try {
                    java.io.File dir = new java.io.File(new URI(url.toString()));
                    java.io.File[] xmlFiles = dir.listFiles((d, n) -> n.endsWith(".xml"));
                    if (xmlFiles != null) {
                        for (java.io.File f : xmlFiles) {
                            filenames.add(f.getName());
                        }
                    }
                } catch (URISyntaxException ex) {
                    LOGGER.log(Level.WARNING, "Could not scan XML directory: " + url, ex);
                }
            }
        }
        return new ArrayList<>(filenames);
    }
}
