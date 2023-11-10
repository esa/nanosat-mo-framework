/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft â€“ v2.4
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
package esa.mo.common.impl.consumer;

import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.common.directory.DirectoryHelper;
import org.ccsds.moims.mo.common.directory.consumer.DirectoryStub;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.helpertools.misc.ConsumerServiceImpl;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 * @author Cesar Coelho
 */
public class DirectoryConsumerServiceImpl extends ConsumerServiceImpl {

    private final URI providerURI;

    private DirectoryStub directoryService = null;

    public DirectoryConsumerServiceImpl(final URI providerURI) throws MALException, MalformedURLException,
        MALInteractionException {
        this(providerURI, null, null);
    }

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

        tmConsumer = connection.startService(providerURI, null, domain, DirectoryHelper.DIRECTORY_SERVICE,
            authenticationId, localNamePrefix);

        this.directoryService = new DirectoryStub(tmConsumer);
    }

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

    public DirectoryStub getDirectoryStub() {
        return this.directoryService;
    }
}
