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
package esa.mo.sm.impl.util;

import esa.mo.nmf.nmfpackage.metadata.InvalidPackageException;
import java.io.IOException;
import java.util.List;
import org.ccsds.moims.mo.mal.structures.StringList;

/**
 * An interface to be implemented in order to allow the Package Management
 * service to manage packages.
 *
 * @author Cesar Coelho
 */
public interface PMBackend {

    /**
     * Returns the list of packages available on the provider.
     *
     * @return The names of the available packages.
     * @throws IOException if the list of packages could not be retrieved.
     */
    StringList getListOfPackages() throws IOException;

    /**
     * Installs a package, granting its App the permissions it declares that
     * ground acknowledged.
     *
     * @param packageName The name of the package.
     * @param acknowledged The permissions that ground acknowledged; null for none.
     * @throws IOException if the package could not be installed.
     */
    void install(final String packageName, final List<String> acknowledged) throws IOException;

    /**
     * Uninstalls a package.
     *
     * @param packageName The name of the package to uninstall.
     * @param keepUserData Whether the user data of the package is kept.
     * @throws IOException if the package could not be uninstalled.
     */
    void uninstall(final String packageName, final boolean keepUserData) throws IOException;

    /**
     * Updates a package, granting its App the permissions it declares that
     * ground acknowledged.
     *
     * @param packageName The name of the package.
     * @param acknowledged The permissions that ground acknowledged; null for none.
     * @throws IOException if the package could not be updated.
     */
    void update(final String packageName, final List<String> acknowledged) throws IOException;

    /**
     * Returns the permissions that the App in a package declares.
     *
     * @param packageName The name of the package.
     * @return The declared permissions, empty if the App declares none; null
     * if the package is not an App.
     * @throws IOException if the package cannot be read.
     */
    List<String> getDeclaredPermissions(final String packageName) throws IOException;

    /**
     * Stops the App of a package if it is running, and checks that no process
     * still runs it, wherever that process came from.
     *
     * @param packageName The name of the package.
     * @return True if no process runs the App of the package, or if the
     * package is not an App; false if a process still runs it.
     */
    boolean stopApp(final String packageName);

    /**
     * Checks if a package is currently installed.
     *
     * @param packageName The name of the package.
     * @return True if the package is installed, false otherwise.
     */
    boolean isPackageInstalled(final String packageName);

    /**
     * Checks whether installing a package would replace a package that is
     * installed, so that installing it must be refused. An App package replaces
     * the App of its name, whatever the version, except the same SNAPSHOT
     * version, which may be installed again over itself. Any other package
     * replaces only the same final (non-SNAPSHOT) version of itself.
     *
     * @param packageName The name of the package.
     * @return True if installing the package would replace an installed one.
     */
    boolean replacesInstalledPackage(final String packageName);

    /**
     * Returns the currently installed version of a package.
     *
     * @param packageName The name of the package.
     * @return The installed version, or null if the package is not installed
     * or the version cannot be determined.
     */
    String getPackageVersion(final String packageName);

    /**
     * Checks that a package file is a valid package, before anything else is
     * asked about it. A package is not to be trusted: its metadata, such as its
     * name, must have the form that the backend relies on. The default accepts
     * every package, for a backend that does not read package files.
     *
     * @param packageName The name of the package.
     * @throws InvalidPackageException if the package is not valid, with the reason.
     * @throws IOException if the package could not be read.
     */
    default void checkPackageIsValid(final String packageName) throws IOException {
    }

    /**
     * Checks the integrity of a package: whether each of its files matches the
     * checksum that its metadata lists. This checks the content against the
     * metadata, not who made the package.
     *
     * @param packageName The name of the package.
     * @return True if the package integrity is good, false otherwise.
     */
    boolean checkPackageIntegrity(final String packageName);

    /**
     * Returns whether a package delivers a software baseline component (the NMF
     * core, the mission JARs or a Java runtime). Such packages are shipped with
     * install and activated with the setPrimaryBaseline action; they cannot be
     * updated in place.
     *
     * @param packageName The name of the package.
     * @return True if the package is a baseline component.
     * @throws IOException if the package cannot be read.
     */
    boolean isBaselineComponent(final String packageName) throws IOException;

    /**
     * Returns the SHA-256 checksum of a package file, as stored on the provider,
     * so that it can be compared with the checksum computed on ground.
     *
     * @param packageName The name of the package.
     * @return The checksum as 64 lowercase hexadecimal digits.
     * @throws IOException if the package file cannot be read.
     */
    String getPackageChecksum(String packageName) throws IOException;

}
