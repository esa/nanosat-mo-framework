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

    StringList getListOfPackages() throws IOException;

    void install(final String packageName);

    void uninstall(final String packageName, final boolean keepUserData);

    void upgrade(final String packageName);

    boolean isPackageInstalled(final String packageName);

    boolean checkPackageIntegrity(final String packageName) throws UnsupportedOperationException;

    String getPublicKey(String value);

}
