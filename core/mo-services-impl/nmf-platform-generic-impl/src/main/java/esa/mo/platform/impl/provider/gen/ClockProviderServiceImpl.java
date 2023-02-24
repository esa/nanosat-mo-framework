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
package esa.mo.platform.impl.provider.gen;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.platform.PlatformHelper;
import org.ccsds.moims.mo.platform.clock.ClockHelper;
import org.ccsds.moims.mo.platform.clock.provider.ClockInheritanceSkeleton;
import esa.mo.helpertools.connections.ConnectionProvider;

public class ClockProviderServiceImpl extends ClockInheritanceSkeleton {

    private static final Logger LOGGER = Logger.getLogger(ClockProviderServiceImpl.class.getName());
    private MALProvider clockServiceProvider;
    private boolean initialiased = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    private ClockAdapterInterface adapter;

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param adapter The Clock adapter
     * @throws MALException On initialisation error.
     */
    public synchronized void init(ClockAdapterInterface adapter) throws MALException {
        long timestamp = System.currentTimeMillis();
        
        if (!initialiased) {
            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(PlatformHelper.PLATFORM_AREA_NAME,
                    PlatformHelper.PLATFORM_AREA_VERSION) == null) {
                PlatformHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            try {
                ClockHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                // nothing to be done..
            }
        }

        // Shut down old service transport
        if (null != clockServiceProvider) {
            connection.closeAll();
        }

        this.adapter = adapter;
        clockServiceProvider = connection.startService(
                ClockHelper.CLOCK_SERVICE_NAME.toString(), ClockHelper.CLOCK_SERVICE, this);

        initialiased = true;
        timestamp = System.currentTimeMillis() - timestamp;
        LOGGER.info("Clock service: READY! (" + timestamp + " ms)");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != clockServiceProvider) {
                clockServiceProvider.close();
            }

            connection.closeAll();
        } catch (MALException ex) {
            LOGGER.log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    @Override
    public Time getTime(MALInteraction interaction) throws MALInteractionException, MALException {
        return this.adapter.getTime();
    }

    @Override
    public Integer getTimeFactor(MALInteraction interaction) throws MALInteractionException, MALException {
        return this.adapter.getTimeFactor();
    }
}
