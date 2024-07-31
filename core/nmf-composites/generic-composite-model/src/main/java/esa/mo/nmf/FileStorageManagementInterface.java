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

import java.util.ArrayList;

/**
 * The FileManagementInterface interface provides a way to manage Files and 
 * Folders in the system.
 * 
 */
public interface FileStorageManagementInterface {

    /**
     * Lists the Folders available in the specified path
     *
     * @param path The path of the folder
     * @return A list of Folders
     */
    ArrayList<String> listFolders(final boolean path);

    /**
     * Creates a folder in the specified path
     *
     * @param path The path of the folder
     * @param name The name of the folder
     */
    void createFolder(final boolean path, final String name);

    /**
     * Renames a folder in the specified path
     *
     * @param path The path of the folder
     * @param oldName The old name of the folder
     * @param newName The new name of the folder
     */
    void renameFolder(final boolean path, final String oldName, final String newName);

    /**
     * The publishAlertEvent operation allows an external software entity to
     * publish Alert events through the Alert service
     *
     * @param path The path of the folder to get the information
     * @return Returns the object instance identifier of the published event.
     * If there is any error, then a null shall be returned instead
     */
    Long getFolderUsageStats(final String path);

}
