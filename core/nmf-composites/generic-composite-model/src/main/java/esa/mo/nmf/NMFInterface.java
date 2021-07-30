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
package esa.mo.nmf;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.platform.impl.util.PlatformServicesConsumer;
import org.ccsds.moims.mo.com.structures.ObjectId;

/**
 * The interface that is exposed towards the application logic.
 *
 */
public interface NMFInterface extends SimpleMonitoringInterface {

    /**
     * Requests the COM services available in the NanoSat MO Framework provider.
     *
     * @return The COM services
     * @throws esa.mo.nmf.NMFException If the services are not available
     */
    COMServicesProvider getCOMServices() throws NMFException;

    /**
     * Requests the MC services available in the NanoSat MO Framework provider.
     *
     * @return The MC services
     * @throws esa.mo.nmf.NMFException If the services are not available
     */
    MCServicesProviderNMF getMCServices() throws NMFException;

    /**
     * Requests the Platform services available in the NanoSat MO Framework
     * provider.
     *
     * @return The Platform services
     * @throws esa.mo.nmf.NMFException If the services are not available
     */
    PlatformServicesConsumer getPlatformServices() throws NMFException;

    /**
     * Sets the listener for when the application is requested to be closed.
     *
     * @param closeAppAdapter The adapter that will be called after a request
     * close the app
     */
    void setCloseAppListener(final CloseAppListener closeAppAdapter);

    /**
     * Closes the application gracefully.
     *
     * @param source The source object that triggered the close operation. Can
     * be null.
     */
    void closeGracefully(final ObjectId source);

}
