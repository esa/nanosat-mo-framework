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

    public static final String DIR_NMF = "nanosat-mo-framework";

    public static final String DIR_APPS = "apps";
    public static final String DIR_DRIVERS = "drivers";
    public static final String DIR_ETC = "etc";
    public static final String DIR_JARS_MISSION = "jars-mission";
    public static final String DIR_JARS_NMF = "jars-nmf";
    public static final String DIR_JARS_SHARED = "jars-shared-libraries";
    public static final String DIR_JAVA = "java";
    public static final String DIR_LOGS = "logs";
    public static final String DIR_PACKAGES = "packages";
    public static final String DIR_PUBLIC = "public";

    private static final String DIR_INSTALLATIONS_TRACKER = "installations-tracker";

    private static final String PROPERTY_INSTALLED_RECEIPTS_FOLDER = "esa.mo.nmf.nmfpackage.receipts";

    private static String pathNMF = null;

    public static File getInstallationsTrackerDir() {
        // Default location of the folder
        File folder = new File(DIR_ETC + File.separator + DIR_INSTALLATIONS_TRACKER);

        // Read the Property of the folder to install the packages
        if (System.getProperty(PROPERTY_INSTALLED_RECEIPTS_FOLDER) != null) {
            folder = new File(System.getProperty(PROPERTY_INSTALLED_RECEIPTS_FOLDER));
        }

        return folder;
    }

    public static File getNMFRootDir() {
        if (pathNMF != null) {
            return new File(pathNMF);
        }

        File folder = new File((new File("")).getAbsolutePath());
        String currentFolder = folder.getName();

        // Default location of the folder
        if (DIR_NMF.equals(currentFolder) || "supervisor".equals(currentFolder)) {
            pathNMF = folder.getAbsolutePath();
            return new File(pathNMF);
        }

        // We need additional logic to make sure we are in the right directory!
        return folder;
    }

}
