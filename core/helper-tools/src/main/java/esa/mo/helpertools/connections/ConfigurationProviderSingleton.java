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
 * Holds the configurations for a provider.
 */
public final class ConfigurationProviderSingleton {

    private static final ConfigurationProviderSingleton INSTANCE = new ConfigurationProviderSingleton();
    private static boolean isInitialized = false;
    private static Identifier NETWORK = new Identifier();
    private static SessionType SESSION;
    private static final IdentifierList DOMAIN = new IdentifierList();
    private static final Identifier SESSION_NAME = HelperMisc.SESSION_NAME;

    private ConfigurationProviderSingleton() {
        // Exists only to defeat instantiation.
    }

    public static IdentifierList getDomain() {
        initializeIfNeeded();
        return DOMAIN;
    }

    public static Identifier getNetwork() {
        initializeIfNeeded();
        return NETWORK;
    }

    public static SessionType getSession() {
        initializeIfNeeded();
        return SESSION;
    }
    
    public static Identifier getSourceSessionName() {
        return SESSION_NAME;
    }

    private static synchronized void initializeIfNeeded(){
        if (!isInitialized){
            init();
            isInitialized = true;
        }
    }
    

    public static ConfigurationProviderSingleton getInstance() {
        return INSTANCE;
    }

    /**
     * Initializes the class ConfigurationProviderSingleton
     * with the values made available in the properties files.
     * This includes the generation of the domain from the PROPERTY_DOMAIN
     * property or from a composition of the properties: ORGANIZATION_NAME,
     * MISSION_NAME, MO_APP_NAME
     *
     * It also generates the network zone field from the properties:
     * ORGANIZATION_NAME, MISSION_NAME, NETWORK_ZONE, DEVICE_NAME
     *
     * Additionally, it sets the session to SessionType.LIVE
     */
    private static void init() {
        if (System.getProperty(HelperMisc.PROP_ORGANIZATION_NAME) == null) {  // The property does not exist? 
            HelperMisc.loadPropertiesFile(); // try to load the properties from the file...
        }

        // ------------------------Domain------------------------
        if (System.getProperty(HelperMisc.PROP_DOMAIN) != null) {
            // Get directly the domain from the property
            DOMAIN.addAll(HelperMisc.domainId2domain(System.getProperty(HelperMisc.PROP_DOMAIN)));
        } else {
            // Or generate it for the provider
            if (System.getProperty(HelperMisc.PROP_ORGANIZATION_NAME) != null) {  // Include the name of the organization in the Domain
                DOMAIN.add(new Identifier(System.getProperty(HelperMisc.PROP_ORGANIZATION_NAME)));
            } else {
                DOMAIN.add(new Identifier("domainNotFoundInPropertiesFile"));
            }

            if (System.getProperty(HelperMisc.PROP_MISSION_NAME) != null) {  // Include the name of the mission in the Domain
                DOMAIN.add(new Identifier(System.getProperty(HelperMisc.PROP_MISSION_NAME)));
            }

            if (System.getProperty(HelperMisc.PROP_MO_APP_NAME) != null) {  // Include the name of the app in the Domain
                DOMAIN.add(new Identifier(System.getProperty(HelperMisc.PROP_MO_APP_NAME)));
            }
        }

        // -------------------------------------------------------
        // ------------------------Network------------------------
        String networkString = "";
        if (System.getProperty(HelperMisc.PROP_NETWORK) != null) {
            networkString = System.getProperty(HelperMisc.PROP_NETWORK);
        } else {
            if (System.getProperty(HelperMisc.PROP_ORGANIZATION_NAME) != null) {
                networkString = networkString.concat(System.getProperty(HelperMisc.PROP_ORGANIZATION_NAME));
            } else {
                networkString += "OrganizationName";
            }

            networkString += ".";

            if (System.getProperty(HelperMisc.PROP_MISSION_NAME) != null) {
                networkString = networkString.concat(System.getProperty(HelperMisc.PROP_MISSION_NAME));
            } else {
                networkString += "MissionName";
            }

            networkString += ".";

            if (System.getProperty(HelperMisc.PROP_NETWORK_ZONE) != null) {
                networkString = networkString.concat(System.getProperty(HelperMisc.PROP_NETWORK_ZONE));
            } else {
                networkString += "NetworkZone";
            }

            networkString += ".";

            if (System.getProperty(HelperMisc.PROP_DEVICE_NAME) != null) {
                networkString = networkString.concat(System.getProperty(HelperMisc.PROP_DEVICE_NAME));
            } else {
                networkString += "DeviceName";
            }
        }

        NETWORK = new Identifier(networkString);

        // -------------------------------------------------------
        SESSION = SessionType.LIVE;
    }

}
