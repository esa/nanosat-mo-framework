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
/**
 * The composites of the NMF, built on {@link esa.mo.nmf.NMFProvider} and
 * {@link esa.mo.nmf.NMFConsumer}.
 * <ul>
 * <li>The providers: {@link esa.mo.nmf.composites.NanoSatMOConnectorImpl},
 * which an App creates to provide its services and consume the Supervisor's;
 * {@link esa.mo.nmf.composites.NanoSatMOSupervisor}, which a mission extends
 * for its Supervisor; and {@link esa.mo.nmf.composites.NanoSatMOMonolithic},
 * a single provider for standalone deployments that is not an App.</li>
 * <li>The adapters, which consume a provider through a simpler interface:
 * {@link esa.mo.nmf.composites.SpaceMOAdapterImpl} for an App that consumes
 * another App, and {@link esa.mo.nmf.composites.GroundMOAdapterImpl} for a
 * ground tool, both built on {@link esa.mo.nmf.composites.MOAdapterImpl},
 * with the listeners through which they deliver the data they receive.</li>
 * </ul>
 */
package esa.mo.nmf.composites;
