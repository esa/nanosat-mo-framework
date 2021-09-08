package esa.mo.nmf.apps.controller;

import java.util.Map;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.platform.gps.consumer.GPSAdapter;
import org.ccsds.moims.mo.platform.gps.structures.TwoLineElementSet;

/**
 * TLEMonitor is used by MPCameraController to receive TLE
 */
public class TLEMonitor extends GPSAdapter {

    private TwoLineElementSet twoLineElement = null;

    @Override
    public void getTLEResponseReceived(MALMessageHeader msgHeader, TwoLineElementSet twoLineElement, Map qosProperties) {
        this.twoLineElement = twoLineElement;
        synchronized(this) {
            this.notifyAll();
        }
    }

    public TwoLineElementSet getTLE() {
        return twoLineElement;
    }
}
