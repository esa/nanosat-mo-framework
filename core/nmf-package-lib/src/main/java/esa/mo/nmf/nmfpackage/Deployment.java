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

import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.nmf.nmfpackage.utils.HelperNMFPackage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Deployment class is contains the information on how the NMF root
 * directory is expected to look like and where the NMF files and folders can be
 * found.
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
    @Deprecated
    public static final String DIR_JARS_NMF_OLD = "libs";
    public static final String DIR_JARS_SHARED = "jars-shared-libraries";
    public static final String DIR_JAVA = "java";
    public static final String DIR_LOGS = "logs";
    public static final String DIR_PACKAGES = "packages";
    public static final String DIR_PUBLIC = "public";

    private static final String DIR_INSTALLATIONS_TRACKER = "installations-tracker";

    private static final String PROPERTY_INSTALLED_RECEIPTS_FOLDER = "esa.mo.nmf.nmfpackage.receipts";

    private static File pathNMF = null;

    public synchronized static File getNMFRootDir() {
        if (pathNMF != null) {
            return pathNMF;
        }

        File folder = new File((new File("")).getAbsolutePath());
        String currentFolder = folder.getName();

        // Default location of the folder
        if (DIR_NMF.equals(currentFolder) || "supervisor".equals(currentFolder)) {
            pathNMF = folder;
            return pathNMF;
        }

        // We need additional logic to make sure we are in the right directory!
        return folder;
    }

    public static File getAppsDir() {
        return new File(getNMFRootDir(), DIR_APPS);
    }

    public static File getDriversDir() {
        return new File(getNMFRootDir(), DIR_DRIVERS);
    }

    public static File getEtcDir() {
        return new File(getNMFRootDir(), DIR_ETC);
    }

    public static File getJarsNMFDir() {
        File newFolder = new File(getNMFRootDir(), DIR_JARS_NMF);

        if (newFolder.exists()) {
            return newFolder;
        }

        // Otherwise return the old folder:
        return new File(getNMFRootDir(), DIR_JARS_NMF_OLD);
    }

    public static File getJarsSharedDir() {
        return new File(getNMFRootDir(), DIR_JARS_SHARED);
    }

    public static File getJarsMissionDir() {
        return new File(getNMFRootDir(), DIR_JARS_MISSION);
    }

    public static File getJavaDir() {
        return new File(getNMFRootDir(), DIR_JAVA);
    }

    public static File getLogsDir() {
        return new File(getNMFRootDir(), DIR_LOGS);
    }

    public static File getLogsDirForApp(String appName) {
        return new File(getLogsDir(), "app_" + appName);
    }

    public static File getTransportFile() {
        return new File(getEtcDir(), HelperMisc.TRANSPORT_PROPERTIES_FILE);
    }

    public static File getInstallationsTrackerDir() {
        // Default location of the folder
        File folder = new File(getEtcDir(), DIR_INSTALLATIONS_TRACKER);

        // Read the Property of the folder to install the packages
        if (System.getProperty(PROPERTY_INSTALLED_RECEIPTS_FOLDER) != null) {
            folder = new File(System.getProperty(PROPERTY_INSTALLED_RECEIPTS_FOLDER));
        }

        return folder;
    }

    /**
     * Find the best Java Runtime Environment inside an nmf directory. The best
     * JRE will be determined based on arguments containing a recommended
     * version, a minimum version, and a maximum version.
     *
     * @param recommended The recommended version to run the App.
     * @param min The minimum version needed to run the App.
     * @param max The maximum version to run the App.
     * @return The Best JRE available for .
     */
    public static String findJREPath(int recommended, int min, int max) {
        if (min < max) {
            Logger.getLogger(HelperNMFPackage.class.getName()).log(Level.WARNING,
                    "The JRE minimum version cannot be greater than the maximum!");
        }

        if (recommended < min || recommended > max) {
            Logger.getLogger(HelperNMFPackage.class.getName()).log(Level.WARNING,
                    "The JRE recommended version should be between the min and max!");
        }

        File javaNMFDir = getJavaDir();
        String bestJRE = "java"; // Default value and worst-case scenario

        if (!javaNMFDir.exists()) {
            return bestJRE; // Return the default value
        }

        final String VERSION_PROP = "JAVA_VERSION";
        File[] list = javaNMFDir.listFiles();

        for (File dir : list) {
            if (!dir.isDirectory()) {
                continue; // Jump over if it is not a directory
            }

            try {
                // Check which java version it is from the release file...
                String path = dir.getAbsolutePath();
                File release = new File(dir, "release");

                if (!release.exists()) {
                    Logger.getLogger(HelperNMFPackage.class.getName()).log(
                            Level.WARNING, "The JRE release file does not "
                            + "exist in: " + release.getAbsolutePath());
                    continue;
                }

                Properties props = new Properties();
                props.load(new FileInputStream(release));
                String version = (String) props.get(VERSION_PROP);

                if (version == null) {
                    Logger.getLogger(HelperNMFPackage.class.getName()).log(
                            Level.WARNING, "The JAVA_VERSION property "
                            + "was not be found on release file: " + path);
                    continue;
                }

                String[] subs = version.replace("\"", "").split("\\.");

                if (subs.length < 3) {
                    Logger.getLogger(HelperNMFPackage.class.getName()).log(
                            Level.WARNING, "The JRE version '" + version
                            + "' could not be determined from the release file: "
                            + release.getAbsolutePath());
                    continue;
                }

                // Java version used to start with 1 until at least Java 8
                int java_version = (Integer.parseInt(subs[0]) != 1)
                        ? Integer.parseInt(subs[0])
                        : Integer.parseInt(subs[1]);

                String jre = dir.getAbsolutePath() + File.separator
                        + "bin" + File.separator + "java";

                File jreExec = new File(jre);

                if (!jreExec.exists()) {
                    Logger.getLogger(HelperNMFPackage.class.getName()).log(
                            Level.WARNING,
                            "The JRE could not be found in directory: " + path);
                    continue;
                } else {
                    // The file exists, make sure it is executable:
                    boolean isSet = jreExec.setExecutable(true, false);
                }

                if (java_version == recommended) {
                    // Perfect, just return it directly!
                    Logger.getLogger(HelperNMFPackage.class.getName()).log(
                            Level.INFO, "The JRE version " + java_version
                            + " was successfully found in directory:"
                            + "\n          >> " + jre);

                    return jre;
                }

                // The version is outside boundaries?
                if (java_version < min && java_version > max) {
                    continue;
                }
                bestJRE = jre;
                // The code here will need to be improved...
                // The objective should be to return the
                // highest available version within the choices
            } catch (FileNotFoundException ex) {
                Logger.getLogger(HelperNMFPackage.class.getName()).log(
                        Level.WARNING, "Something went wrong...", ex);
            } catch (IOException ex) {
                Logger.getLogger(HelperNMFPackage.class.getName()).log(
                        Level.WARNING, "Something went wrong...", ex);
            }
        }

        return bestJRE;
    }

}
