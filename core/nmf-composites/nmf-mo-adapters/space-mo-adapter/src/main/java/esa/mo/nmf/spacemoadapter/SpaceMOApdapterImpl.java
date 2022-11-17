/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
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
package esa.mo.nmf.spacemoadapter;

import esa.mo.common.impl.consumer.DirectoryConsumerServiceImpl;
import esa.mo.common.impl.util.HelperCommon;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.misc.Const;
import esa.mo.nmf.commonmoadapter.CommonMOAdapterImpl;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.common.directory.structures.ServiceFilter;
import org.ccsds.moims.mo.common.structures.ServiceKey;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.*;

/**
 * The implementation of the Space MO Adapter.
 *
 * @author Tanguy Soto
 */
public class SpaceMOApdapterImpl extends CommonMOAdapterImpl {

    /* Logger */
    private static final Logger LOGGER = Logger.getLogger(SpaceMOApdapterImpl.class.getName());

    /**
     * The constructor of this class
     *
     * @param connection The connection details of the provider
     */
    public SpaceMOApdapterImpl(final ConnectionConsumer connection) {
        super(connection);
        super.init();
    }

    /**
     * The constructor of this class
     *
     * @param connection The connection details of the provider
     * @param authenticationId authenticationId of the logged in user
     * @param localNamePrefix the prefix for the local name of the consumer
     */
    public SpaceMOApdapterImpl(final ConnectionConsumer connection, final Blob authenticationId,
        final String localNamePrefix) {
        super(connection, authenticationId, localNamePrefix);
        super.init();
    }

    /**
     * The constructor of this class
     *
     * @param providerDetails The Provider details. This object can be obtained from the Directory
     *        service
     */
    public SpaceMOApdapterImpl(final ProviderSummary providerDetails) {
        super(providerDetails);
        super.init();
    }

    /**
     * The constructor of this class
     *
     * @param providerDetails The Provider details. This object can be obtained from the Directory
     *                        service
     * @param authenticationId authenticationId of the logged in user
     * @param localNamePrefix the prefix for the local name of the consumer
     */
    public SpaceMOApdapterImpl(final ProviderSummary providerDetails, final Blob authenticationId,
        final String localNamePrefix) {
        super(providerDetails, authenticationId, localNamePrefix);
        super.init();
    }

    /**
     * Returns a instance of SpaceMOApdapterImpl that consumes the NMF supervisor provider
     * found in the central directory service.
     *
     * @param centralDirectoryServiceURI URI of the central directory service
     * @return The SpaceMOAdapter instance.
     */
    public static SpaceMOApdapterImpl forNMFSupervisor(URI centralDirectoryServiceURI) {
        return new SpaceMOApdapterImpl(getNMFProviderSummary(centralDirectoryServiceURI,
            Const.NANOSAT_MO_SUPERVISOR_NAME));
    }

    /**
     * Returns a instance of SpaceMOApdapterImpl that consumes the named space app
     * found in the central directory service.
     *
     * @param centralDirectoryServiceURI URI of the central directory service
     * @return The SpaceMOAdapter instance.
     */
    public static SpaceMOApdapterImpl forNMFApp(URI centralDirectoryServiceURI, String appName) {
        return new SpaceMOApdapterImpl(getNMFProviderSummary(centralDirectoryServiceURI,
            Const.NANOSAT_MO_APP_IDENTIFIER_PREFIX + appName));
    }

    /**
     * Look up the central directory to find the NMF provider details.
     * 
     * @return ProviderSummary of the NMF provider or null if not found
     */
    private static ProviderSummary getNMFProviderSummary(URI centralDirectoryServiceURI, String providerName) {
        // Create supervisor provider filter
        IdentifierList domain = new IdentifierList();
        domain.add(new Identifier("*"));
        ServiceKey sk = new ServiceKey(new UShort(0), new UShort(0), new UOctet((short) 0));
        ServiceFilter sf2 = new ServiceFilter(new Identifier(providerName), domain, new Identifier("*"), null,
            new Identifier("*"), sk, new UShortList());

        // Query directory service with filter
        try {
            DirectoryConsumerServiceImpl centralDirectory = new DirectoryConsumerServiceImpl(
                centralDirectoryServiceURI);
            ProviderSummaryList supervisorConnections = centralDirectory.getDirectoryStub().lookupProvider(sf2);
            if (supervisorConnections.size() == 1) {
                LOGGER.log(Level.INFO, String.format("Found %s provider", Const.NANOSAT_MO_SUPERVISOR_NAME));
                return HelperCommon.selectBestIPCTransport(supervisorConnections.get(0));
            } else if (supervisorConnections.size() > 1) {
                LOGGER.log(Level.SEVERE, String.format("Found multiple %s providers",
                    Const.NANOSAT_MO_SUPERVISOR_NAME));
            } else {
                LOGGER.log(Level.SEVERE, String.format("Couldn't find %s provider", Const.NANOSAT_MO_SUPERVISOR_NAME));
            }
        } catch (MALInteractionException | MALException | MalformedURLException e) {
            LOGGER.log(Level.SEVERE, "Error while looking up the central directory", e);

        }

        return null;
    }

}
