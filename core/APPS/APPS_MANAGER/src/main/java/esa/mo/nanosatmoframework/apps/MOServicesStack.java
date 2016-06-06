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
package esa.mo.nanosatmoframework.apps;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.common.impl.util.CommonServicesProvider;
import esa.mo.helpertools.connections.ConfigurationProvider;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.sm.impl.util.SMServicesProvider;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.common.configuration.ConfigurationHelper;
import org.ccsds.moims.mo.mal.MALException;

/**
 * A Provider of MO services composed by COM, M&C and Platform services. Selects
 * the transport layer based on the selected values of the properties file and
 * initializes all services automatically. Provides configuration persistence,
 * therefore the last state of the configuration of the MO services will be kept
 * upon restart. Additionally, the NanoSat MO Framework implements an
 * abstraction layer over the Back-End of some MO services to facilitate the
 * monitoring of the business logic of the app using the NanoSat MO Framework.
 *
 * @author Cesar Coelho
 */
public class MOServicesStack {

    private static final Long DEFAULT_PROVIDER_CONFIGURATION_OBJID = (long) 1;  // The objId of the configuration to be used by the provider
    private final ConfigurationProvider configuration = new ConfigurationProvider();
    private final COMServicesProvider comServices = new COMServicesProvider();
    private final CommonServicesProvider commonServices = new CommonServicesProvider();
    private final SMServicesProvider smServices = new SMServicesProvider();

    /**
     * MOAppsManager
     */
    public MOServicesStack() {
        ConnectionProvider.resetURILinksFile();
        HelperMisc.loadPropertiesFile();

        try {
            comServices.init();
            commonServices.init(comServices);
            smServices.init(comServices);
        } catch (MALException ex) {
            Logger.getLogger(MOServicesStack.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Automatically populate the Directory service with the entries from the URIs File
        commonServices.getDirectoryService().autoLoadURIsFile("MO Apps Manager");

        // Activate the previous configuration
        ObjectId confId = new ObjectId();  // Select the default configuration
        confId.setKey(new ObjectKey(configuration.getDomain(), DEFAULT_PROVIDER_CONFIGURATION_OBJID));
        confId.setType(ConfigurationHelper.PROVIDERCONFIGURATION_OBJECT_TYPE);

        Logger.getLogger(MOServicesStack.class.getName()).log(Level.INFO, "\nMO Apps Manager initialized!\n");
    }

}
