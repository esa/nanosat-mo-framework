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
package esa.mo.nmf.nmfpackage.metadata;

import java.util.Properties;

/**
 * The MetadataMission class holds the metadata of a mission software baseline
 * (the mission JARs in {@code jars-mission/&lt;version&gt;}) in an NMF Package.
 * <p>
 * A mission baseline is versioned on its own and carried on its own. The
 * bootloader builds the classpath from the two versions of the baseline
 * separately, mission before framework, so a mission can be updated without
 * the framework underneath it moving, and the other way about.
 *
 * @author Cesar Coelho
 */
public class MetadataMission extends Metadata {

    /**
     * Constructor for the MetadataMission class.
     *
     * @param packageName The name of the package.
     * @param version The version of the package.
     */
    public MetadataMission(String packageName, String version) {
        super(new Properties());
        properties.put(Metadata.PACKAGE_TYPE, Metadata.TYPE_UPDATE_MISSION);
        properties.put(Metadata.PACKAGE_NAME, packageName);
        properties.put(Metadata.PACKAGE_VERSION, version);
    }

    /**
     * Constructor for the MetadataMission class, from properties already read.
     *
     * @param properties The properties of the package.
     */
    public MetadataMission(Properties properties) {
        super(properties);
    }

}
