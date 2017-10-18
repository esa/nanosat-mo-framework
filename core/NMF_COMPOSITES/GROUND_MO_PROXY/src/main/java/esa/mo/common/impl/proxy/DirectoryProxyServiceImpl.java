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
package esa.mo.common.impl.proxy;

import esa.mo.common.impl.provider.DirectoryProviderServiceImpl;
import esa.mo.nmf.NMFConsumer;
import esa.mo.nmf.groundmoproxy.GroundMOProxy;
import java.net.MalformedURLException;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.common.directory.structures.PublishDetails;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 *
 */
public class DirectoryProxyServiceImpl extends DirectoryProviderServiceImpl {

    public void syncLocalDirectoryServiceWithCentral(final URI centralDirectoryServiceURI,
            final URI routedURI) throws MALException, MalformedURLException, MALInteractionException {
        ProviderSummaryList providers = NMFConsumer.retrieveProvidersFromDirectory(true, centralDirectoryServiceURI);
        GroundMOProxy.addProxyPrefix(providers, routedURI.getValue());

        // Clean the current list of provider that are available
        // on the Local Directory service
        this.withdrawAllProviders();

        for (ProviderSummary provider : providers) {
            PublishDetails pub = new PublishDetails();
            pub.setDomain(provider.getProviderKey().getDomain());
            pub.setNetwork(new Identifier("not_available"));
            pub.setProviderDetails(provider.getProviderDetails());
            pub.setProviderName(provider.getProviderName());
            pub.setServiceXML(null);
            pub.setSessionType(SessionType.LIVE);
            pub.setSourceSessionName(null);
            this.publishProvider(pub, null);
        }
    }
}
