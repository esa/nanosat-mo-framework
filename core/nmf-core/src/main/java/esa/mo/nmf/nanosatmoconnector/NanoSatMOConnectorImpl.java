/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
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
package esa.mo.nmf.nanosatmoconnector;

/**
 * The NanoSat MO Connector under its package name before 6.0.
 * <p>
 * Apps built against NMF 5.x create this class, so it stays so that they can
 * still be loaded. New Apps use
 * {@link esa.mo.nmf.composites.NanoSatMOConnectorImpl}.
 *
 * @deprecated Use {@link esa.mo.nmf.composites.NanoSatMOConnectorImpl}.
 */
@Deprecated(since = "6.0", forRemoval = true)
public class NanoSatMOConnectorImpl extends esa.mo.nmf.composites.NanoSatMOConnectorImpl {
}
