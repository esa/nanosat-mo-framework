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

import esa.mo.helpertools.helpers.HelperConnections;
import esa.mo.helpertools.helpers.HelperMisc;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Set;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IntegerList;
import org.ccsds.moims.mo.mal.structures.URI;

public class ServicesConnectionDetails {

    private HashMap<String, SingleConnectionDetails> services = new HashMap<String, SingleConnectionDetails>();

    public HashMap<String, SingleConnectionDetails> getServices() {
        return this.services;
    }

    public void setServices(HashMap<String, SingleConnectionDetails> services) {
        this.services = services;
    }
    
    public SingleConnectionDetails get(String string){
        return services.get(string);
    }

    public SingleConnectionDetails get(Identifier id){
        return services.get(id.toString());
    }

    /**
     * Loads the URIs from the default properties file
     *
     * @return The connection details object generated from the file
     * @throws java.net.MalformedURLException when the MALconsumer is not
     * initialized correctly
     */
    public ServicesConnectionDetails loadURIFromFiles() throws MalformedURLException {
        return this.loadURIFromFiles(null);
    }
    
    /**
     * Loads the URIs from a selected properties file
     *
     * @param filename The name of the file
     * @return The connection details object generated from the file
     * @throws java.net.MalformedURLException when the MALconsumer is not
     * initialized correctly
     */
    public ServicesConnectionDetails loadURIFromFiles(String filename) throws MalformedURLException {

        java.util.Properties uriProps = null;
        final String configFile;
        
        if (filename == null){
            configFile = System.getProperty("providerURI.properties", HelperMisc.PROVIDER_URIS_PROPERTIES_FILENAME);
        }else{
            configFile = filename;
        }
        
        final java.io.File file = new java.io.File(configFile);
        if (file.exists()) {
            uriProps = HelperMisc.loadProperties(file.toURI().toURL(), "providerURI.properties");
        }else{
            return null;
        }
        
        // Reading the values out of the properties file
        Set propKeys = uriProps.keySet();
        Object[] array = propKeys.toArray();

        for (int i = 0; i < array.length; i++) {
            String propString = array[i].toString();

            if (propString.endsWith(HelperConnections.SUFFIX_URI)) {  // Is it a URI property of some service?
                String serviceName = propString.substring(0, propString.length() - HelperConnections.SUFFIX_URI.length());  // Remove the URI part of it
                SingleConnectionDetails details = new SingleConnectionDetails();

                // Get the URI + Broker + Domain from the Properties
                details.setProviderURI(uriProps.getProperty(serviceName + HelperConnections.SUFFIX_URI));
                
                String brokerURI = uriProps.getProperty(serviceName + HelperConnections.SUFFIX_BROKER);
                details.setBrokerURI(brokerURI);

                if ("null".equals(brokerURI)){
                    details.setBrokerURI((URI) null);
                }

                details.setDomain(HelperMisc.domainId2domain(uriProps.getProperty(serviceName + HelperConnections.SUFFIX_DOMAIN)));
                String serviceKeyRaw = uriProps.getProperty(serviceName + HelperConnections.SUFFIX_SERVICE_KEY);

                if (serviceKeyRaw != null) {
                    String[] a = serviceKeyRaw.substring(1, serviceKeyRaw.length() - 1).split(", ");
                    IntegerList serviceKey = new IntegerList();
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
