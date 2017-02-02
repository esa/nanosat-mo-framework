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
package esa.mo.platform.impl.provider.gen;

import esa.mo.helpertools.connections.ConfigurationProvider;
import esa.mo.helpertools.connections.ConnectionProvider;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.platform.PlatformHelper;
import org.ccsds.moims.mo.platform.magnetometer.MagnetometerHelper;
import org.ccsds.moims.mo.platform.magnetometer.provider.MagnetometerInheritanceSkeleton;
import org.ccsds.moims.mo.platform.magnetometer.structures.MagneticFieldInstance;

/**
 *
 */
public class MagnetometerProviderServiceImpl extends MagnetometerInheritanceSkeleton {

    private MALProvider magnetometerServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    private final ConfigurationProvider configuration = new ConfigurationProvider();
    private MagnetometerAdapterInterface adapter;

    /**
     * Initializes the Software-defined Radio service.
     *
     * @param adapter The Camera adapter
     * @throws MALException On initialisation error.
     */
    public synchronized void init(MagnetometerAdapterInterface adapter) throws MALException {
        if (!initialiased) {

            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(PlatformHelper.PLATFORM_AREA_NAME, PlatformHelper.PLATFORM_AREA_VERSION) == null) {
                PlatformHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            try {
                MagnetometerHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) { // nothing to be done..
            }
        }

        // Shut down old service transport
        if (null != magnetometerServiceProvider) {
            connection.closeAll();
        }

        this.adapter = adapter;
        magnetometerServiceProvider = connection.startService(MagnetometerHelper.MAGNETOMETER_SERVICE_NAME.toString(), MagnetometerHelper.MAGNETOMETER_SERVICE, this);

        running = true;
        initialiased = true;
        Logger.getLogger(MagnetometerProviderServiceImpl.class.getName()).info("Optical Data Receiver service READY");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != magnetometerServiceProvider) {
                magnetometerServiceProvider.close();
            }

            connection.closeAll();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(MagnetometerProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    @Override
    public MagneticFieldInstance getMagneticField(MALInteraction interaction) throws MALInteractionException, MALException {
        return adapter.getMagneticField();
    }

}
