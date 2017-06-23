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
package esa.mo.nmf;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.platform.impl.util.PlatformServicesConsumer;
import org.ccsds.moims.mo.com.structures.ObjectId;

/**
 * The interface that is exposed towards the app developer.
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
     * Adds a listener for when the app is requested to be closed.
     *
     * @param closeAppAdapter The adapter that will be called after a request to
     * close the app
     */
    void addCloseAppListener(CloseAppListener closeAppAdapter);

    /**
     * Closes the app gracefully.
     *
     * @param source The source object that triggered the close operation. Can
     * be null.
     */
    void closeGracefully(final ObjectId source);

}
