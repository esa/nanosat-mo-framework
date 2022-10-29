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

import esa.mo.helpertools.connections.ConnectionProvider;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.platform.PlatformHelper;
import org.ccsds.moims.mo.platform.opticaldatareceiver.OpticalDataReceiverHelper;
import org.ccsds.moims.mo.platform.opticaldatareceiver.provider.OpticalDataReceiverInheritanceSkeleton;
import org.ccsds.moims.mo.platform.opticaldatareceiver.provider.RecordSamplesInteraction;

/**
 * Optical Data Receiver service Provider.
 */
public class OpticalDataReceiverProviderServiceImpl extends OpticalDataReceiverInheritanceSkeleton {

    private MALProvider opticalDataReceiverServiceProvider;
    private boolean initialiased = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    private OpticalDataReceiverAdapterInterface adapter;
    public static double MAX_RECORDING_DURATION = 10.0; // 10 seconds

    /**
     * Initializes the Optical Receiver Provider service
     *
     * @param adapter The Optical Data RX adapter
     * @throws MALException On initialisation error.
     */
    public synchronized void init(OpticalDataReceiverAdapterInterface adapter) throws MALException {
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

            if (MALContextFactory.lookupArea(PlatformHelper.PLATFORM_AREA_NAME,
                    PlatformHelper.PLATFORM_AREA_VERSION)
                    .getServiceByName(OpticalDataReceiverHelper.OPTICALDATARECEIVER_SERVICE_NAME) == null) {
                OpticalDataReceiverHelper.init(MALContextFactory.getElementFactoryRegistry());
            }
        }

        // Shut down old service transport
        if (null != opticalDataReceiverServiceProvider) {
            connection.closeAll();
        }

        this.adapter = adapter;
        opticalDataReceiverServiceProvider = connection.startService(
                OpticalDataReceiverHelper.OPTICALDATARECEIVER_SERVICE_NAME.toString(),
                OpticalDataReceiverHelper.OPTICALDATARECEIVER_SERVICE, this);

        initialiased = true;
        timestamp = System.currentTimeMillis() - timestamp;
        Logger.getLogger(OpticalDataReceiverProviderServiceImpl.class.getName()).info(
                "Optical Data Receiver service: READY! (" + timestamp + " ms)");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != opticalDataReceiverServiceProvider) {
                opticalDataReceiverServiceProvider.close();
            }
            connection.closeAll();
        } catch (MALException ex) {
            Logger.getLogger(OpticalDataReceiverProviderServiceImpl.class.getName()).log(
                    Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    @Override
    public void recordSamples(Duration recordingDuration, RecordSamplesInteraction interaction) 
            throws MALInteractionException, MALException {
        if (!adapter.isUnitAvailable()) {
            // TODO Add error code to the service spec
            throw new MALInteractionException(new MALStandardError(
                    PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null));
        }
        if (recordingDuration == null || recordingDuration.getValue() == 0.0) {
            // TODO Add error code to the service spec
            interaction.sendError(new MALStandardError(new UInteger(0), null));
            return;
        }
        if (recordingDuration.getValue() > MAX_RECORDING_DURATION) {
            interaction.sendError(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, 
                    new Duration(MAX_RECORDING_DURATION)));
            return;
        }
        interaction.sendAcknowledgement();
        byte[] data = adapter.recordOpticalReceiverData(recordingDuration);
        if (data == null) {
            // TODO Add error code to the service spec
            interaction.sendError(new MALStandardError(new UInteger(0), null));
            return;
        }

        interaction.sendResponse(new Blob(data));
    }

}
