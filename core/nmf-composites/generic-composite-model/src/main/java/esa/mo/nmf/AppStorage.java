/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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
 */package esa.mo.nmf;

import esa.mo.helpertools.helpers.HelperMisc;
import java.io.File;

/**
 * The {@code AppStorage} class allows the retrieval of directory paths to 
 * store files by the Apps. The App developer should always use this class to
 * retrieve the directory paths to store files. Storing files somewhere else in
 * the system is not recommended.
 * <p>
 * App developers are strongly recommended to use the following folders to store
 * their files:
 * <ul>
 * <li>cache</li>
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

    private final static String FOLDER_USERDATA = "user-data";

    private final static String FOLDER_CACHE = "cache";

    private final static String FOLDER_APP_INTERNAL = "app-internal";

    private final static String FOLDER_USER_NMF_DIR = "." + FOLDER_APPS;

    private final static String FOLDER_PACKAGES = "packages";
    private final static String FOLDER_USER_DATABASES = "databases";
    private final static String FOLDER_NMF = "nmf";
    
    /**
     * Returns the absolute path to the main directory of this App. The use of
     * this folder is discouraged. The developer should use the user-data
     * directory or the cache directory to store files.
     *
     * @return The main directory for this App.
     */
    @Deprecated
    public static File getAppMainDir() {
        StringBuilder path = pathToUserAppDir();

        // Create it if it does not exist...
        File directory = new File(path.toString());

        if (!directory.exists()) {
            directory.mkdirs();
        }

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

        if (!directory.exists()) {
            directory.mkdirs();
        }

        return directory;
    }

    /**
     * Returns the absolute path to the application-specific Cache directory.
     * 
     * The system might automatically delete files in this directory as disk 
     * space is needed elsewhere on the device.
     * The returned path may change over time if the calling app is moved to an
     * external storage device.
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

        if (!directory.exists()) {
            directory.mkdirs();
        }

        return directory;
    }

    private static StringBuilder pathToUserAppDir() {
        StringBuilder path = new StringBuilder();
        path.append((String) System.getProperty("user.home"));
        path.append(File.separator);
        path.append(FOLDER_USER_NMF_DIR);
        path.append(File.separator);
        path.append(System.getProperty(HelperMisc.PROP_MO_APP_NAME));
        path.append(File.separator);
        return path;
    }
}
