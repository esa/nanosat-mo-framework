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
import org.ccsds.moims.mo.com.directory.DirectoryHelper;
import org.ccsds.moims.mo.com.directory.consumer.DirectoryStub;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.helpertools.misc.ConsumerServiceImpl;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 * Consumer of the COM Directory service.
 *
 * @author Cesar Coelho
 */
public class DirectoryConsumerServiceImpl extends ConsumerServiceImpl {

    private final URI providerURI;

    private DirectoryStub directoryService = null;

    /**
     * Creates the Directory service consumer and starts the consumer connection.
     *
     * @param providerURI the URI of the Directory service provider
     * @param authenticationId the authentication id of the logged in user
     * @param localNamePrefix the prefix for the local name of the consumer
     * @throws MALException if the consumer cannot be created
     * @throws MalformedURLException if a provided URI is malformed
     * @throws MALInteractionException if the service returns an error
     */
    public DirectoryConsumerServiceImpl(final URI providerURI, final Blob authenticationId,
            final String localNamePrefix) throws MALException, MalformedURLException, MALInteractionException {
        this.connectionDetails = null;
        this.providerURI = providerURI;

        // Close old connection
        if (tmConsumer != null) {
            try {
                tmConsumer.close();
            } catch (MALException ex) {
                Logger.getLogger(DirectoryConsumerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        IdentifierList domain = new IdentifierList();
        domain.add(new Identifier("*"));

        tmConsumer = connection.startService(providerURI, null, domain,
                DirectoryHelper.DIRECTORY_SERVICE, authenticationId, localNamePrefix);

        this.directoryService = new DirectoryStub(tmConsumer);
    }

    /**
     * Creates the Directory service consumer with no authentication id nor local name prefix.
     *
     * @param providerURI the URI of the Directory service provider
     * @throws MALException if the consumer cannot be created
     * @throws MalformedURLException if a provided URI is malformed
     * @throws MALInteractionException if the service returns an error
     */
    public DirectoryConsumerServiceImpl(final URI providerURI)
            throws MALException, MalformedURLException, MALInteractionException {
        this(providerURI, null, null);
    }

    /**
     * Returns the URI of the Directory service provider.
     *
     * @return the provider URI
     */
    public URI getProviderURI() {
        return this.providerURI;
    }

    @Override
    public Object generateServiceStub(MALConsumer tmConsumer) {
        return new DirectoryStub(tmConsumer);
    }

    @Override
    public Object getStub() {
        return this.getDirectoryStub();
    }

    /**
     * Returns the Directory service stub used to invoke operations on the provider.
     *
     * @return the Directory service stub
     */
    public DirectoryStub getDirectoryStub() {
        return this.directoryService;
    }
}
