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
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import esa.mo.helpertools.helpers.HelperAttributes;

/**
 * Provides dummy parameter values. It returns Attribute with default values of either 0 or null
 * depending on the actual type.
 *
 * @author Tanguy Soto
 */
public class DummyValuesProvider extends OBSWParameterValuesProvider {

  /**
   * Creates a new instance of DummyValuesProvider.
   * 
   * @param parameterMap The map of OBSW parameters for which we have to provide values for
   */
  public DummyValuesProvider(HashMap<Identifier, OBSWParameter> parameterMap) {
    super(parameterMap);
  }

  /** {@inheritDoc} */
  @Override
  public Attribute getValue(Identifier identifier) {
    if (!parameterMap.containsKey(identifier)) {
      return null;
    }
    OBSWParameter param = parameterMap.get(identifier);
    return HelperAttributes.attributeName2Attribute(param.getType());
  }
}
