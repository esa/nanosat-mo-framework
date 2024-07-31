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
package esa.mo.nmf.groundmoproxy;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VirtualSPPURIsManager {

    private static Random random = new Random();

    private final static String PROTOCOL_SPP = "malspp";
    private final static String APID_QUALIFIER = "247";
    private final int apidRangeStart;
    private final int apidRangeEnd;
    private final static int SOURDEID_RANGE_START = 0; // Set by SPP
    private final static int SOURDEID_RANGE_END = 255;
    private final HashMap<String, String> virtualAPIDsMap = new HashMap<>();
    private final HashMap<String, String> reverseMap = new HashMap<>();
    private final AtomicInteger uniqueAPID;
    private final AtomicInteger uniqueSourceId;
    private final Object MUTEX = new Object();

    public VirtualSPPURIsManager(int apidRangeStart, int apidRangeEnd) {
        this.apidRangeStart = apidRangeStart;
        this.apidRangeEnd = apidRangeEnd;

        int apid = random.nextInt((apidRangeEnd - apidRangeStart) + 1) + apidRangeStart;
        int sourceId = random.nextInt((SOURDEID_RANGE_END - SOURDEID_RANGE_START) + 1) + SOURDEID_RANGE_START;

        uniqueAPID = new AtomicInteger(apid);
        uniqueSourceId = new AtomicInteger(sourceId);
    }

    public String getURI(String virtualSPPURI) {
        String reverse;

        synchronized (MUTEX) {
            reverse = reverseMap.get(virtualSPPURI);

            if (reverse == null) {
                Logger.getLogger(VirtualSPPURIsManager.class.getName()).log(Level.SEVERE,
                    "The reverse APID for virtualSPPURI: " + virtualSPPURI + " could not be found!");
            }
        }

        return reverse;
    }

    public String getVirtualSPPURI(String uriFrom) {
        String virtualAPID;

        synchronized (MUTEX) {
            virtualAPID = virtualAPIDsMap.get(uriFrom);

            if (virtualAPID == null) { // If it does not exist...
                virtualAPID = this.generateNewSPPURI();
                virtualAPIDsMap.put(uriFrom, virtualAPID);
                reverseMap.put(virtualAPID, uriFrom);
            }

            Logger.getLogger(VirtualSPPURIsManager.class.getName()).log(Level.FINE, "The virtualAPID is: " +
                virtualAPID);
        }

        return virtualAPID;
    }

    private String generateNewSPPURI() {
        int sourceId = uniqueSourceId.getAndIncrement();
        int apid = uniqueAPID.get();

        if (sourceId > SOURDEID_RANGE_END) {
            uniqueSourceId.set(SOURDEID_RANGE_START);
            sourceId = SOURDEID_RANGE_START;
            apid = uniqueAPID.incrementAndGet();

            if (apid > apidRangeEnd) {
                uniqueAPID.set(apidRangeStart);
                apid = apidRangeStart;
            }
        }

        return PROTOCOL_SPP + ":" + APID_QUALIFIER + "/" + apid + "/" + sourceId;
    }

    public static int getAPIDFromVirtualSPPURI(final String virtualSPPURI) {
        String[] str = virtualSPPURI.split("/");
        return Integer.parseInt(str[1]);
    }

}
