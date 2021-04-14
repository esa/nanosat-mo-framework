/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
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
package esa.mo.nmf;

import esa.mo.helpertools.helpers.HelperMisc;
import java.io.File;

/**
 * The {@code AppStorage} class allows the retrieval of directory paths to store
 * files by the Apps. The App developer should always use this class to retrieve
 * the directory paths to store files. Storing files somewhere else in the
 * system is not recommended.
 * <p>
 * App developers are strongly recommended to use the following folders to store
 * their files:
 * <ul>
 * <li>cache</li>
 * <li>nmf-internal</li>
 * <li>user-data</li>
 * </ul>
 *
 * The path to these directories can be retrieved using this class.
 *
 * @since 2.0.0
 * @author Cesar Coelho
 */
public class AppStorage {

    private final static String FOLDER_APPS = "nmf-apps";

    private final static String FOLDER_USER_NMF_DIR = "." + FOLDER_APPS;

    private final static String FOLDER_CACHE = "cache";

    private final static String FOLDER_NMF_INTERNAL = "nmf-internal";

    private final static String FOLDER_USERDATA = "user-data";

    /**
     * Returns the absolute path to the application-specific Cache directory.
     *
     * The system might automatically delete files in this directory as disk
     * space is needed elsewhere on the device. The returned path may change
     * over time if the calling app is moved to an external storage device.
     *
     * Apps require no extra permissions to read or write to the returned path,
     * since this path lives in their private storage.
     *
     * @return The cache directory for this app.
     */
    public static File getAppCacheDir() {
        StringBuilder path = pathToUserAppDir();
        path.append(FOLDER_CACHE);

        // Create it if it does not exist...
        File directory = new File(path.toString());
        AppStorage.mkDirAndSetPermissions(directory);
        return directory;
    }

    /**
     * Returns the absolute path to the application-specific NMF internal data
     * directory. This directory is used my the NMF, App developers are advised
     * to not touch the files in this directory.
     *
     * @return The NMF internal data directory for this app.
     */
    public static File getAppNMFInternalDir() {
        StringBuilder path = pathToUserAppDir();
        path.append(FOLDER_NMF_INTERNAL);

        // Create it if it does not exist...
        File directory = new File(path.toString());
        AppStorage.mkDirAndSetPermissions(directory);
        return directory;
    }

    /**
     * Returns the absolute path to the application-specific User Data
     * directory.
     *
     * The returned path may change over time if the calling app is moved to an
     * external storage device.
     *
     * Apps require no extra permissions to read or write to the returned path,
     * since this path lives in their private storage.
     *
     * @return The userdata directory for this app.
     */
    public static File getAppUserdataDir() {
        StringBuilder path = pathToUserAppDir();
        path.append(FOLDER_USERDATA);

        // Create it if it does not exist...
        File directory = new File(path.toString());
        AppStorage.mkDirAndSetPermissions(directory);
        return directory;
    }

    private static StringBuilder pathToUserAppDir() {
        StringBuilder path = new StringBuilder();
        path.append(System.getProperty("user.home"));
        path.append(File.separator);
        path.append(FOLDER_USER_NMF_DIR);
        path.append(File.separator);
        path.append(System.getProperty(HelperMisc.PROP_MO_APP_NAME));
        path.append(File.separator);

        // Create it if it does not exist...
        File directory = new File(path.toString());
        AppStorage.mkDirAndSetPermissions(directory);
        return path;
    }

    private static void mkDirAndSetPermissions(File directory) {
        if (!directory.exists()) {
            directory.mkdirs();
            directory.setExecutable(false, false);
            directory.setExecutable(true, true);
            directory.setReadable(false, false);
            directory.setReadable(true, true);
            directory.setWritable(false, false);
            directory.setWritable(true, true);
        }
    }
}
