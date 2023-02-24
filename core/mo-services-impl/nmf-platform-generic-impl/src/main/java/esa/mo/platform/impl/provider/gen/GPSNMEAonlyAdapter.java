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

import esa.mo.helpertools.misc.Const;
import esa.mo.platform.impl.util.HelperGPS;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.platform.gps.structures.Position;
import org.ccsds.moims.mo.platform.gps.structures.SatelliteInfoList;

/**
 *
 * @author Cesar Coelho
 */
public abstract class GPSNMEAonlyAdapter implements GPSAdapterInterface {

    private static final Logger LOGGER = Logger.getLogger(GPSNMEAonlyAdapter.class.getName());
    private final int resultCacheValidityMs;

    private Position lastPosition = null;
    private SatelliteInfoList lastSatInfo = null;
    private long lastPositionTime = 0;
    private long lastSatInfoTime = 0;

    public GPSNMEAonlyAdapter() {
        resultCacheValidityMs = Integer.parseInt(System.getProperty(Const.PLATFORM_GNSS_CACHING_PERIOD, "1000"));
    }

    @Override
    public synchronized Position getCurrentPosition() {
        if (System.currentTimeMillis() - lastPositionTime < resultCacheValidityMs) {
            return lastPosition;
        }
        String nmeaLog = "";
        try {
            lastPosition = null;
            String fullNmeaResponse = this.getNMEASentence("LOG GPGGALONG\r\n");
            nmeaLog = HelperGPS.sanitizeNMEALog(fullNmeaResponse.trim());
            if (!nmeaLog.startsWith("$GPGGA")) {
                LOGGER.log(Level.SEVERE, "Unexpected response format: {0}", nmeaLog);
            } else {
                lastPosition = HelperGPS.gpggalong2Position(nmeaLog);
            }
        } catch (IOException ex) {
            LOGGER.log(Level.FINE, "The current position could not be retrieved! "
                    + "The receiver is likely offline or not returning proper position.", ex);
        }
        lastPositionTime = System.currentTimeMillis();
        return lastPosition;
    }

    @Override
    public SatelliteInfoList getSatelliteInfoList() {
        if (System.currentTimeMillis() - lastSatInfoTime < resultCacheValidityMs) {
            return lastSatInfo;
        }
        try {
            lastSatInfo = null;
            String nmeaLog = HelperGPS.sanitizeNMEALog(this.getNMEASentence("LOG GPGSV\r\n").trim());
            if (!nmeaLog.startsWith("$GPGSV")) {
                LOGGER.log(Level.SEVERE, "Unexpected response format: {0}", nmeaLog);
            } else {
                return HelperGPS.gpgsv2SatelliteInfoList(nmeaLog);
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        lastSatInfoTime = System.currentTimeMillis();
        return lastSatInfo;
    }

    @Override
    public String getBestXYZSentence() throws IOException {
        return HelperGPS.sanitizeNMEALog(this.getNMEASentence("LOG BESTXYZ\r\n"));
    }

    @Override
    public String getTIMEASentence() throws IOException {
        return HelperGPS.sanitizeNMEALog(this.getNMEASentence("LOG TIMEA\r\n"));
    }
}
