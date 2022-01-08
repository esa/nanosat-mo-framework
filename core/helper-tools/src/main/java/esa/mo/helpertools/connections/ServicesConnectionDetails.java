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
package esa.mo.helpertools.connections;

import esa.mo.helpertools.helpers.HelperConnections;
import esa.mo.helpertools.helpers.HelperMisc;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IntegerList;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 * Holds the details of the service connections.
 */
public class ServicesConnectionDetails {

    private HashMap<String, SingleConnectionDetails> services = new HashMap<>();

    public HashMap<String, SingleConnectionDetails> getServices() {
        return this.services;
    }

    public void setServices(final HashMap<String, SingleConnectionDetails> services) {
        this.services = services;
    }

    public SingleConnectionDetails get(final String string) {
        return services.get(string);
    }

    public SingleConnectionDetails get(final Identifier id) {
        return services.get(id.toString());
    }

    public void add(final String key, final SingleConnectionDetails value) {
        services.put(key, value);
    }

    public void reset() {
        services.clear();
    }

    /**
     * Loads the URIs from the default properties file
     *
     * @return The connection details object generated from the file
     * @throws java.net.MalformedURLException when the MALconsumer is not
     * initialized correctly
     * @throws java.io.FileNotFoundException if the URI file has not been found
     */
    public ServicesConnectionDetails loadURIFromFiles() throws MalformedURLException, FileNotFoundException {
        return this.loadURIFromFiles(null);
    }

    /**
     * Loads the URIs from a selected properties file
     *
     * @param filename The name of the file
     * @return The connection details object generated from the file
     * @throws java.net.MalformedURLException when the MALconsumer is not
     * initialized correctly
     * @throws java.io.FileNotFoundException if the URI file has not been found
     */
    public ServicesConnectionDetails loadURIFromFiles(String filename) throws MalformedURLException, FileNotFoundException {

        if (filename == null) {
            filename = System.getProperty("providerURI.properties", HelperMisc.PROVIDER_URIS_PROPERTIES_FILENAME);
        }

        final File configFile = new File(filename);
        if (!configFile.exists()) {
            throw new FileNotFoundException(filename + " not found.");
        }

        final Properties uriProps = HelperMisc.loadProperties(configFile.toURI().toURL(), "providerURI.properties");
        return loadURIFromProperties(uriProps);
    }

    /**
     * Loads the URIs from a selected Java properties
     *
     * @param uriProps properties set containing the provider URIs
     * @return The connection details object generated from the file
     * @throws java.net.MalformedURLException when the MALconsumer is not
     * initialized correctly
     */
    public ServicesConnectionDetails loadURIFromProperties(final Properties uriProps) throws MalformedURLException {

        // Reading the values out of the properties file
        final Set propKeys = uriProps.keySet();
        final Object[] array = propKeys.toArray();

        for (int i = 0; i < array.length; i++) {
            final String propString = array[i].toString();

            if (propString.endsWith(HelperConnections.SUFFIX_URI)) {  // Is it a URI property of some service?
                final String serviceName = propString.substring(0, propString.length() - HelperConnections.SUFFIX_URI.length());  // Remove the URI part of it
                final SingleConnectionDetails details = new SingleConnectionDetails();

                // Get the URI + Broker + Domain from the Properties
                details.setProviderURI(uriProps.getProperty(serviceName + HelperConnections.SUFFIX_URI));

                final String brokerURI = uriProps.getProperty(serviceName + HelperConnections.SUFFIX_BROKER);
                details.setBrokerURI(brokerURI);

                if ("null".equals(brokerURI)) {
                    details.setBrokerURI((URI) null);
                }

                details.setDomain(HelperMisc.domainId2domain(uriProps.getProperty(serviceName + HelperConnections.SUFFIX_DOMAIN)));
                final String serviceKeyRaw = uriProps.getProperty(serviceName + HelperConnections.SUFFIX_SERVICE_KEY);

                if (serviceKeyRaw != null) {
                    // 1 in order to remove the '['
                    final String[] a = serviceKeyRaw.substring(1, serviceKeyRaw.length() - 1).split(", ");
                    final IntegerList serviceKey = new IntegerList();
                    serviceKey.add(Integer.parseInt(a[0]));
                    serviceKey.add(Integer.parseInt(a[1]));
                    serviceKey.add(Integer.parseInt(a[2]));
                    details.setServiceKey(serviceKey);
                }

                // Put it in the Hash Map
                services.put(serviceName, details);
            }
        }

        return this;
    }

}
