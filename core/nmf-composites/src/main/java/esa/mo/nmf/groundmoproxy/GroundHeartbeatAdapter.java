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

import esa.mo.sm.impl.consumer.HeartbeatConsumerServiceImpl;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperTime;
import org.ccsds.moims.mo.mal.helpertools.misc.TaskScheduler;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.sm.heartbeat.consumer.BeatSubscriptionKeys;
import org.ccsds.moims.mo.sm.heartbeat.consumer.HeartbeatAdapter;

/**
 * Heartbeat adapter used on the ground side of a {@link GroundMOProxy}. It subscribes to the
 * spacecraft heartbeat, tracks the last received beat and updates the proxy's alive status.
 */
public class GroundHeartbeatAdapter extends HeartbeatAdapter {

    private static final Logger LOGGER = Logger.getLogger(GroundHeartbeatAdapter.class.getName());
    /** Tolerance, in milliseconds, added to the beat period before a beat is considered missed. */
    protected static final long DELTA_ERROR = 2 * 1000; // 2 seconds = 2000 milliseconds
    /** The heartbeat period, in milliseconds, as reported by the provider. */
    protected final long period; // In seconds
    /** The measured round-trip delay to the provider, in milliseconds. */
    protected long lag; // In milliseconds
    /** Scheduler running the periodic heartbeat-refresh task. */
    protected final TaskScheduler timer;
    /** Ground timestamp of the last received beat. */
    protected Time lastBeatAt = Time.now();
    /** On-board timestamp of the last received beat; {@code null} until the first beat. */
    protected Time lastBeatOBT = null; // Last beat in On-Board timestamp
    /** The ground proxy whose alive status is tracked. */
    protected final GroundMOProxy moProxy;
    /** The heartbeat consumer service connected to the spacecraft. */
    protected final HeartbeatConsumerServiceImpl heartbeat;

    /**
     * Creates the adapter, reads the heartbeat period from the provider, marks the proxy as
     * alive and starts the periodic heartbeat-refresh task.
     *
     * @param heartbeat the heartbeat consumer service connected to the spacecraft
     * @param moProxy the ground proxy whose alive status is tracked
     * @throws MALInteractionException if the heartbeat service returns an error
     * @throws MALException if a communication error occurs
     */
    public GroundHeartbeatAdapter(final HeartbeatConsumerServiceImpl heartbeat,
            final GroundMOProxy moProxy) throws MALInteractionException, MALException {
        this.moProxy = moProxy;
        this.heartbeat = heartbeat;
        long timestamp = System.currentTimeMillis();
        double value = heartbeat.getHeartbeatStub().getPeriod().getInSeconds();
        lag = System.currentTimeMillis() - timestamp;
        period = (long) (value * 1000);
        LOGGER.log(Level.INFO, "The provider is reachable! Beat period: {0} seconds", value);
        moProxy.setNmsAliveStatus(true);
        timer = new TaskScheduler(1);
        startHeartbeatRefreshTask();
    }

    /**
     * Starts the periodic task that checks whether beats are still being received and
     * remeasures the lag.
     */
    public void startHeartbeatRefreshTask() {
        timer.scheduleTask(new HeartbeatRefreshTask(moProxy, heartbeat), period, period, TimeUnit.MILLISECONDS, true);
    }

    /**
     * Stops the heartbeat-refresh task and releases the scheduler.
     */
    public void stop() {
        timer.resetScheduler();
    }

    @Override
    public synchronized void beatNotifyReceived(
            org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
            org.ccsds.moims.mo.mal.structures.Identifier subscriptionId,
            org.ccsds.moims.mo.mal.structures.UpdateHeader updateHeader,
            BeatSubscriptionKeys keys,
            java.util.Map qosProperties) {
        synchronized (timer) {
            lastBeatAt = Time.now();
            lastBeatOBT = msgHeader.getTimestamp();
            final long iDiff = lastBeatAt.getValue() - lastBeatOBT.getValue();
            LOGGER.log(Level.INFO,
                    "(Clocks diff: {0} ms | Round-Trip Delay time: {1} ms | Last beat received at: {2})",
                    new Object[]{iDiff, lag, HelperTime.time2readableString(lastBeatAt)});
            moProxy.setNmsAliveStatus(true);
        }
    }

    /**
     * Returns the ground timestamp of the last received beat.
     *
     * @return the ground timestamp of the last beat
     */
    public Time getLastBeat() {
        return lastBeatAt;
    }

    /**
     * Returns the on-board timestamp of the last received beat.
     *
     * @return the on-board timestamp of the last beat, or {@code null} if no beat was received yet
     */
    public Time getLastBeatOBT() {
        return lastBeatOBT;
    }

    private class HeartbeatRefreshTask extends Thread {

        private static final int LAG_MEASUREMENT_INTERVAL = 3;
        private final GroundMOProxy moProxy;
        private final HeartbeatConsumerServiceImpl heartbeat;
        private boolean lostHeartbeat = false;

        public HeartbeatRefreshTask(GroundMOProxy moProxy, HeartbeatConsumerServiceImpl heartbeat) {
            this.moProxy = moProxy;
            this.heartbeat = heartbeat;
        }

        int attemptCounter = 0;

        @Override
        public void run() {
            synchronized (timer) {
                final Time currentTime = Time.now();
                // If the current time has passed the last beat + the beat period + a delta error
                long threshold = lastBeatAt.getValue() + period + DELTA_ERROR;
                if (currentTime.getValue() > threshold) {
                    // Then the provider is unresponsive
                    moProxy.setNmsAliveStatus(false);
                    LOGGER.log(Level.FINE, "The heartbeat message from the provider was not received.");
                    if (!lostHeartbeat) {
                        LOGGER.log(Level.INFO, "Lost heartbeat from remote provider. Remote URI: {}, Routed URI: {}.",
                                new Object[]{moProxy.getRemoteCentralDirectoryServiceURI(), moProxy.getRoutedURI()});
                        lostHeartbeat = true;
                    }
                    // Next time the heartbeat comes, trigger the lag measurement
                    attemptCounter = LAG_MEASUREMENT_INTERVAL;
                } else {
                    if (lostHeartbeat) {
                        LOGGER.log(Level.INFO, "The heartbeat has recovered. Remote URI: {}, Routed URI: {}.",
                                new Object[]{moProxy.getRemoteCentralDirectoryServiceURI(), moProxy.getRoutedURI()});
                        lostHeartbeat = false;
                    }
                    if (attemptCounter >= LAG_MEASUREMENT_INTERVAL) {
                        try {
                            long timestamp = System.currentTimeMillis();
                            heartbeat.getHeartbeatStub().getPeriod();
                            lag = System.currentTimeMillis() - timestamp; // Calculate the lag
                        } catch (MALInteractionException | MALException ex) {
                            LOGGER.log(Level.SEVERE, null, ex);
                        }
                        attemptCounter = 0;
                    }
                    attemptCounter++;
                }
            }
        }
    }

}
