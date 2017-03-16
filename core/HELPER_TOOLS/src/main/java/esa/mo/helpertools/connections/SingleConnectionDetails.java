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
package esa.mo.helpertools.connections;

import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.IntegerList;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 * A holder for the details of the connection to a provider
 */
public class SingleConnectionDetails {

    private URI providerURI;
    private URI brokerURI;
    private IdentifierList domain;
    private IntegerList serviceKey = new IntegerList();

    public void setProviderURI(String providerURI) {
        this.providerURI = new URI(providerURI);
    }

    public void setProviderURI(URI providerURI) {
        this.providerURI = providerURI;
    }

    public void setBrokerURI(String brokerURI) {
        this.brokerURI = new URI(brokerURI);
    }

    public void setBrokerURI(URI brokerURI) {
        this.brokerURI = brokerURI;
    }

    public void setDomain(IdentifierList domain) {
        this.domain = domain;
    }

    public void setServiceKey(IntegerList serviceKey) {
        this.serviceKey = serviceKey;
    }

    public URI getProviderURI() {
        return this.providerURI;
    }

    public URI getBrokerURI() {
        return this.brokerURI;
    }

    @Deprecated
    public IdentifierList getDomain() {
        return this.domain;
    }

    public IntegerList getServiceKey() {
        return this.serviceKey;
    }
    
    @Override
    public String toString(){
        return "providerURI=" + providerURI + ", brokerURI=" + brokerURI + ", domain=" + domain;
    }

}
