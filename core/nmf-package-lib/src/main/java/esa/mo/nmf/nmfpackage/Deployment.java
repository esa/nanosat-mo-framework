/* ----------------------------------------------------------------------------
 * Copyright (C) 2022      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft â€“ v2.4
 * You may not use this zipFile except in compliance with the License.
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
package esa.mo.nmf.nmfpackage;

import java.io.File;

/**
 *
 * @author Cesar Coelho
 */
public class Deployment {
    
    public static final String DIR_APPS = "apps";
    public static final String DIR_DRIVERS = "drivers";
    public static final String DIR_ETC = "etc";
    public static final String DIR_JARS_MISSION = "jars-mission";
    public static final String DIR_JARS_NMF = "jars-nmf";
    public static final String DIR_JARS_SHARED = "jars-shared-dependencies";
    public static final String DIR_JAVA = "java";
    public static final String DIR_LOGS = "logs";
    public static final String DIR_PACKAGES = "packages";
    public static final String DIR_PUBLIC = "public";

    private static final String DIR_RECEIPTS = "installation_receipts";

    private static final String INSTALLED_RECEIPTS_FOLDER_PROPERTY = "esa.mo.nmf.nmfpackage.receipts";

    @Deprecated
    private static final String INSTALLATION_FOLDER_PROPERTY = "esa.mo.nmf.nmfpackage.installationFolder";
    
    public static File getReceiptsFolder() {
        // Default location of the folder
        File folder = new File(DIR_ETC + File.separator + DIR_RECEIPTS);

        // Read the Property of the folder to install the packages
        if (System.getProperty(INSTALLED_RECEIPTS_FOLDER_PROPERTY) != null) {
            folder = new File(System.getProperty(INSTALLED_RECEIPTS_FOLDER_PROPERTY));
        }

        return folder;
    }

    @Deprecated
    public static File getInstallationFolder() {
        // Default location of the folder
        File folder = new File("");

        // Read the Property of the folder to install the packages
        if (System.getProperty(INSTALLATION_FOLDER_PROPERTY) != null) {
            folder = new File(System.getProperty(INSTALLATION_FOLDER_PROPERTY));
        }

        return folder;
    }

    
}
