/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
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
 */
package esa.mo.demo.provider.simplearchive;

import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.helpertools.connections.ConnectionProvider;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;

/**
 *
 * @author Cesar Coelho
 */
public class DemoSimpleArchive {

    private static final ArchiveProviderServiceImpl archiveService = new ArchiveProviderServiceImpl();

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        try {
            ConnectionProvider.resetURILinksFile();
            HelperMisc.loadPropertiesFile();
            archiveService.init(null);

        } catch (MALException ex) {
            Logger.getLogger(DemoSimpleArchive.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
