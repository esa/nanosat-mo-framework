/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
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
package esa.mo.com.testbed;

import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.util.COMServicesProvider;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionProvider;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperMisc;

/**
 * Manages the lifecycle of COM service providers and consumers for the testbed.
 * Call {@link #setUp} before tests and {@link #tearDown} after.
 *
 * @author Cesar Coelho
 */
public class SetUpCOMServices {

    private static final Logger LOGGER = Logger.getLogger(SetUpCOMServices.class.getName());

    private static COMServicesProvider comServicesProvider = null;
    private static ArchiveConsumerServiceImpl archiveConsumer = null;

    public void setUp() throws IOException {
        HelperMisc.loadPropertiesFile();
        ConnectionProvider.resetURILinksFile();

        try {
            comServicesProvider = new COMServicesProvider();
            comServicesProvider.init();

            SingleConnectionDetails archiveDetails = comServicesProvider.getArchiveService()
                    .getConnection().getConnectionDetails();
            archiveConsumer = new ArchiveConsumerServiceImpl(archiveDetails);
        } catch (MALException | MalformedURLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void tearDown() {
        if (comServicesProvider != null) {
            comServicesProvider.closeAll();
        }
    }

    public COMServicesProvider getCOMServicesProvider() {
        return comServicesProvider;
    }

    public ArchiveConsumerServiceImpl getArchiveConsumer() {
        return archiveConsumer;
    }

}
