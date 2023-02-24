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
package esa.mo.nmf.nanosatmosupervisor.parameter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValue;

import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.nmf.MCRegistration;

/**
 * Handles the provisioning of OBSW parameter.
 *
 * @author Tanguy Soto
 */
public class OBSWParameterManager {

    /**
     * The logger
     */
    private static final Logger LOGGER = Logger.getLogger(OBSWParameterManager.class.getName());

    /**
     * Default OBSW parameter report interval (seconds)
     */
    private static final int DEFAULT_REPORT_INTERVAL = 5;

    /**
     * Helper to read the OBSW parameter from datapool.
     */
    private final ParameterLister parameterLister;

    /**
     * Maps each parameter proxy (object instance id of the ParameterIdentity in
     * the supervisor) to the OBSW parameter it represents.
     */
    private Map<Long, OBSWParameter> proxyIdsToOBSWParams;

    /**
     * Provides the OBSW parameter values
     */
    private OBSWParameterValuesProvider valuesProvider;

    public OBSWParameterManager(InputStream datapool)
            throws IOException, JAXBException, XMLStreamException {
        // Read from provided inputstreams
        parameterLister = new ParameterLister(datapool);

        // Initialize the parameters proxies to OBSW parameter maps
        proxyIdsToOBSWParams = new HashMap<>();

        // Instantiate the value provider
        HashMap<Identifier, OBSWParameter> parameterMap = parameterLister.getParameters();
        String defaultClass = "esa.mo.nmf.nanosatmosupervisor.parameter.DummyValuesProvider";
        String valuesProviderClass = System.getProperty("nmf.supervisor.parameter.valuesprovider.impl", defaultClass);

        try {
            Constructor<?> c = Class.forName(valuesProviderClass).getConstructor(parameterMap.getClass());
            valuesProvider = (OBSWParameterValuesProvider) c.newInstance(new Object[]{parameterMap});
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,
                    "Error initializing the values provider. Using dummy values provider.", e);
            valuesProvider = new DummyValuesProvider(parameterMap);
        }
    }

    /**
     * Registers proxies for the OBSW parameters using the provided registration
     * object.
     *
     * @param registrationObject The registration object
     */
    public void registerParametersProxies(MCRegistration registrationObject) {
        // Sort parameters by id
        List<OBSWParameter> parameters
                = new ArrayList<>(parameterLister.getParameters().values());
        parameters.sort(Comparator.comparing(OBSWParameter::getId));

        // Create the parameter proxies definitions
        ParameterDefinitionDetailsList paramDefs = new ParameterDefinitionDetailsList();
        IdentifierList paramIdentifiers = new IdentifierList();

        for (OBSWParameter param : parameters) {
            paramDefs.add(new ParameterDefinitionDetails(param.getDescription(),
                    HelperAttributes.attributeName2typeShortForm(param.getType()).byteValue(), "", false,
                    new Duration(DEFAULT_REPORT_INTERVAL), null, null));
            paramIdentifiers.add(new Identifier(param.getName()));
        }

        // Register the parameter proxies
        LongList proxyIds = registrationObject.registerParameters(paramIdentifiers, paramDefs);
        if (proxyIds == null || proxyIds.size() != parameters.size()) {
            LOGGER.log(Level.SEVERE,
                    "Error while registering OBSW parameters proxies: returned IDs are null or some are missing");
            return;
        }
        for (int i = 0; i < proxyIds.size(); i++) {
            proxyIdsToOBSWParams.put(proxyIds.get(i), parameters.get(i));
        }
    }

    /**
     * Returns a value for a given OBSW parameter proxy.
     *
     * @param parameterID ID of the parameter proxy
     * @return The value
     */
    public Attribute getValue(Long parameterID) {
        Identifier obswParamIdentifier
                = new Identifier(proxyIdsToOBSWParams.get(parameterID).getName());
        return getValue(obswParamIdentifier);
    }

    /**
     * Sets a new value to a given OBSW parameter.
     *
     * @param newRawValue the new value
     * @return true if parameter is set, false otherwise.
     */
    public Boolean setValue(ParameterRawValue newRawValue) {
        Identifier obswParamIdentifier
                = new Identifier(proxyIdsToOBSWParams.get(newRawValue.getParamInstId()).getName());
        return setValue(newRawValue.getRawValue(), obswParamIdentifier);
    }

    /**
     * @param parameterID The parameter ID to test
     * @return true if the ID corresponds to one of the parameter proxies
     * registered by this class
     */
    public boolean isOBSWParameterProxy(Long parameterID) {
        return proxyIdsToOBSWParams.containsKey(parameterID);
    }

    /**
     * Returns a value for the given OBSW parameter.
     *
     * @param identifier Name of the parameter
     * @return The value
     */
    private Attribute getValue(Identifier identifier) {
        return valuesProvider.getValue(identifier);
    }

    /**
     * Sets a new value for the given OBSW parameter name.
     *
     * @param rawValue The new value of the parameter
     * @param identifier Name of the parameter
     * @return True if parameter is set, false otherwise.
     */
    private Boolean setValue(Attribute rawValue, Identifier identifier) {
        return valuesProvider.setValue(rawValue, identifier);
    }
}
