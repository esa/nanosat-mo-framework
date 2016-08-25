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

import esa.mo.helpertools.helpers.HelperMisc;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.SessionType;

/**
 *
 * @author Cesar Coelho
 */
public class ConfigurationProvider {

    private final Identifier network;
    private final SessionType session;
    private IdentifierList domain = new IdentifierList();
    public final static String PROPERTY_DOMAIN = "helpertools.configurations.provider.Domain";
    public final static String ORGANIZATION_NAME = "helpertools.configurations.provider.OrganizationName";
    public final static String MISSION_NAME = "helpertools.configurations.provider.MissionName";
    public final static String MO_APP_NAME = "helpertools.configurations.provider.MOappName";
    public final static String NETWORK_ZONE = "helpertools.configurations.provider.NetworkZone";
    public final static String DEVICE_NAME = "helpertools.configurations.provider.DeviceName";

    public IdentifierList getDomain() {
        return this.domain;
    }

    public Identifier getNetwork() {
        return this.network;
    }

    public SessionType getSession() {
        return this.session;
    }

    /**
     * Initializes the class with the values made available in the PROPERTIES.
     * This includes the generation of the domain from the PROPERTY_DOMAIN property
     * or from a composition of the properties: ORGANIZATION_NAME, MISSION_NAME, 
     * MO_APP_NAME
     *
     * It also generates the network zone field from the properties:
     * ORGANIZATION_NAME, MISSION_NAME, NETWORK_ZONE, DEVICE_NAME
     *
     * Additionally, it sets the session to SessionType.LIVE
     */
    public ConfigurationProvider() {

        if (System.getProperty(ORGANIZATION_NAME) == null) {  // The property does not exist? 
            HelperMisc.loadPropertiesFile(); // try to load the properties from the file...
        }

        // ------------------------Domain------------------------
        if (System.getProperty(PROPERTY_DOMAIN) != null) {
            // Get directly the domain from the property
            this.domain = HelperMisc.domainId2domain(System.getProperty(PROPERTY_DOMAIN));
        } else {
            // Or generate it for the provider
            if (System.getProperty(ORGANIZATION_NAME) != null) {  // Include the name of the organization in the Domain
                this.domain.add(new Identifier(System.getProperty(ORGANIZATION_NAME)));
            } else {
                this.domain.add(new Identifier("domainNotFoundInPropertiesFile"));
            }

            if (System.getProperty(MISSION_NAME) != null) {  // Include the name of the mission in the Domain
                this.domain.add(new Identifier(System.getProperty(MISSION_NAME)));
            }

            if (System.getProperty(MO_APP_NAME) != null) {  // Include the name of the app in the Domain
                this.domain.add(new Identifier(System.getProperty(MO_APP_NAME)));
            }
        }

        // -------------------------------------------------------
        // ------------------------Network------------------------
        String networkString = "";
        if (System.getProperty(ORGANIZATION_NAME) != null) {
            networkString = networkString.concat(System.getProperty(ORGANIZATION_NAME));
        } else {
            networkString += "OrganizationName";
        }

        networkString += ".";

        if (System.getProperty(MISSION_NAME) != null) {
            networkString = networkString.concat(System.getProperty(MISSION_NAME));
        } else {
            networkString += "MissionName";
        }

        networkString += ".";

        if (System.getProperty(NETWORK_ZONE) != null) {
            networkString = networkString.concat(System.getProperty(NETWORK_ZONE));
        } else {
            networkString += "NetworkZone";
        }

        networkString += ".";

        if (System.getProperty(DEVICE_NAME) != null) {
            networkString = networkString.concat(System.getProperty(DEVICE_NAME));
        } else {
            networkString += "DeviceName";
        }

        this.network = new Identifier(networkString);
        // -------------------------------------------------------

        this.session = SessionType.LIVE;

    }

}
