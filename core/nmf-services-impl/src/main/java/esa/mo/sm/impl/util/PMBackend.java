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

import java.io.IOException;
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
     * Installs the content of a package.
     *
     * @param packageName The name of the package to install.
     */
    void install(final String packageName);

    /**
     * Uninstalls a package.
     *
     * @param packageName The name of the package to uninstall.
     * @param keepUserData Whether the user data of the package is kept.
     */
    void uninstall(final String packageName, final boolean keepUserData);

    /**
     * Upgrades an installed package to the version of the supplied package.
     *
     * @param packageName The name of the package to upgrade to.
     */
    void upgrade(final String packageName);

    /**
     * Checks if a package is currently installed.
     *
     * @param packageName The name of the package.
     * @return True if the package is installed, false otherwise.
     */
    boolean isPackageInstalled(final String packageName);

    /**
     * Checks whether a final (non-SNAPSHOT) build of a package's version is
     * already installed, so installing it again must be rejected. A SNAPSHOT
     * version returns false: it is not final and may always be overridden.
     *
     * @param packageName The name of the package.
     * @return True if this final version is already installed.
     */
    boolean isFinalVersionInstalled(final String packageName);

    /**
     * Returns the currently installed version of a package.
     *
     * @param packageName The name of the package.
     * @return The installed version, or null if the package is not installed
     * or the version cannot be determined.
     */
    String getPackageVersion(final String packageName);

    /**
     * Checks the integrity of a package.
     *
     * @param packageName The name of the package.
     * @return True if the package integrity is good, false otherwise.
     * @throws UnsupportedOperationException if the backend does not support
     * integrity checks.
     */
    boolean checkPackageIntegrity(final String packageName) throws UnsupportedOperationException;

    /**
     * Returns whether a package delivers a software baseline component (the NMF
     * core, the mission JARs or a Java runtime). Such packages are shipped with
     * install and activated with the setPrimaryBaseline action; they cannot be
     * upgraded in place.
     *
     * @param packageName The name of the package.
     * @return True if the package is a baseline component.
     * @throws IOException if the package cannot be read.
     */
    boolean isBaselineComponent(final String packageName) throws IOException;

    /**
     * Returns the public key of a package.
     *
     * @param packageName The name of the package.
     * @return The public key.
     */
    String getPublicKey(String packageName);

}
