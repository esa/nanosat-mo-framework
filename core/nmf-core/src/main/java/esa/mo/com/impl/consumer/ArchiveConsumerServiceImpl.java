/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft - v2.4
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
package esa.mo.com.impl.consumer;

import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.ArchiveHelper;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveStub;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.helpertools.misc.ConsumerServiceImpl;
import org.ccsds.moims.mo.mal.structures.Blob;

/**
 * Consumer of the COM Archive service.
 *
 * @author Cesar Coelho
 */
public class ArchiveConsumerServiceImpl extends ConsumerServiceImpl {

    private ArchiveStub archiveService = null;

    @Override
    public Object generateServiceStub(MALConsumer tmConsumer) {
        return new ArchiveStub(tmConsumer);
    }

    @Override
    public Object getStub() {
        return this.getArchiveStub();
    }

    /**
     * Returns the Archive service stub used to invoke operations on the provider.
     *
     * @return the Archive service stub
     */
    public ArchiveStub getArchiveStub() {
        return archiveService;
    }

    /**
     * Creates the Archive service consumer with no authentication id nor local name prefix.
     *
     * @param connectionDetails the connection details of the Archive service provider
     * @throws MALException if the consumer cannot be created
     * @throws MalformedURLException if a provided URI is malformed
     */
    public ArchiveConsumerServiceImpl(SingleConnectionDetails connectionDetails) throws MALException, MalformedURLException {
        this(connectionDetails, null, null);
    }

    /**
     * Creates the Archive service consumer and starts the consumer connection.
     *
     * @param connectionDetails the connection details of the Archive service provider
     * @param authenticationId the authentication id of the logged in user
     * @param localNamePrefix the prefix for the local name of the consumer
     * @throws MALException if the consumer cannot be created
     * @throws MalformedURLException if a provided URI is malformed
     */
    public ArchiveConsumerServiceImpl(SingleConnectionDetails connectionDetails, Blob authenticationId,
            String localNamePrefix) throws MALException, MalformedURLException {
        this.connectionDetails = connectionDetails;

        // Close old connection
        if (tmConsumer != null) {
            try {
                tmConsumer.close();
            } catch (MALException ex) {
                Logger.getLogger(ArchiveConsumerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        tmConsumer = connection.startService(this.connectionDetails.getProviderURI(),
                this.connectionDetails.getBrokerURI(),
                this.connectionDetails.getDomain(),
                ArchiveHelper.ARCHIVE_SERVICE,
                authenticationId,
                localNamePrefix);

        this.archiveService = new ArchiveStub(tmConsumer);

    }

}
