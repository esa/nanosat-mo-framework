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

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;

/**
 * Abstract class that a class providing values for OBSW parameters has to extend.
 *
 * @author Tanguy Soto
 */
public abstract class OBSWParameterValuesProvider {
    /**
     * The logger
     */
    private static final Logger LOGGER = Logger.getLogger(OBSWParameterValuesProvider.class.getName());

    /**
     * The map of OBSW parameters for which we have to provide values for. The parameters are
     * accessible by their name.
     */
    protected final HashMap<Identifier, OBSWParameter> parameterMap;

    /**
     * Creates a new instance of OBSWParameterValuesProvider.
     * 
     * @param parameterMap The map of OBSW parameters for which we have to provide values for
     */
    public OBSWParameterValuesProvider(HashMap<Identifier, OBSWParameter> parameterMap) {
        if (parameterMap == null) {
            LOGGER.log(Level.SEVERE, "Parameters map provided is null, initilazing with empty parameters map");
            parameterMap = new HashMap<>();
        }
        this.parameterMap = parameterMap;
    }

    /**
     * Returns a value for the given parameter name.
     *
     * @param identifier Name of the parameter
     * @return The value or null if the parameter name is unknown or a problem happened while fetching the value
     */
    public abstract Attribute getValue(Identifier identifier);

    /**
     * Sets a new value for the given parameter name.
     * 
     * @param rawValue The new value of the parameter
     * @param identifier Name of the parameter
     * @return True if parameter is set, false otherwise.
     */
    public abstract Boolean setValue(Attribute rawValue, Identifier identifier);

}
