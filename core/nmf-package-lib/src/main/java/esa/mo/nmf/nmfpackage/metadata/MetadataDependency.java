/* ----------------------------------------------------------------------------
 * Copyright (C) 2022      European Space Agency
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
 * The MetadataApp class holds the metadata of a Dependency in a NMF Package.
 *
 * @author Cesar Coelho
 */
public class MetadataDependency extends Metadata {

    public MetadataDependency(String packageName, String version, String timestamp) {
        super(new Properties());
        properties.put(Metadata.PACKAGE_NAME, packageName);
        properties.put(Metadata.PACKAGE_VERSION, version);
        properties.put(Metadata.PACKAGE_TIMESTAMP, timestamp);
        properties.put(Metadata.PACKAGE_TYPE, Metadata.TYPE_DEPENDENCY);
    }

}
