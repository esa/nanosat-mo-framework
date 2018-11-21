/* ----------------------------------------------------------------------------
 * Copyright (C) 2018      European Space Agency
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
package esa.mo.nmf.groundmoproxy;

import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.sm.impl.consumer.HeartbeatConsumerServiceImpl;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.softwaremanagement.heartbeat.consumer.HeartbeatAdapter;

class GroundHeartbeatAdapter extends HeartbeatAdapter
{

  private static final Logger LOGGER = Logger.getLogger(GroundHeartbeatAdapter.class.getName());
  private static final long DELTA_ERROR = 2 * 1000; // 2 seconds = 2000 milliseconds
  private final long period; // In seconds
  private long lag; // In milliseconds
  private final Timer timer;
  private Time lastBeatAt = HelperTime.getTimestampMillis();
  private final GroundMOProxy moProxy;

  public GroundHeartbeatAdapter(final HeartbeatConsumerServiceImpl heartbeat,
      final GroundMOProxy moProxy) throws MALInteractionException, MALException
  {
    this.moProxy = moProxy;
    long timestamp = System.currentTimeMillis();
    double value = heartbeat.getHeartbeatStub().getPeriod().getValue();
    lag = System.currentTimeMillis() - timestamp;
    period = (long) (value * 1000);
    LOGGER.log(Level.INFO, "The provider is reachable! Beat period: {0} seconds", value);
    moProxy.setNmsAliveStatus(true);
    timer = new Timer("HeartbeatMonitorTimer");
    timer.scheduleAtFixedRate(new HeartbeatRefreshTask(moProxy, heartbeat), period, period);
  }

  @Override
  public synchronized void beatNotifyReceived(
      org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
      org.ccsds.moims.mo.mal.structures.Identifier _Identifier0,
      org.ccsds.moims.mo.mal.structures.UpdateHeaderList _UpdateHeaderList1,
      java.util.Map qosProperties)
  {
    synchronized (timer) {
      lastBeatAt = HelperTime.getTimestampMillis();
      final Time onboardTime = msgHeader.getTimestamp();
      final long iDiff = lastBeatAt.getValue() - onboardTime.getValue();
      LOGGER.log(Level.INFO,
          "(Clocks diff: {0} ms | Round-Trip Delay time: {1} ms | Last beat received at: {2})",
          new Object[]{iDiff, lag, HelperTime.time2readableString(lastBeatAt)});
      moProxy.setNmsAliveStatus(true);
    }
  }

  public FineTime getLastBeat()
  {
    return HelperTime.timeToFineTime(lastBeatAt);
  }

  private class HeartbeatRefreshTask extends TimerTask
  {

    private final GroundMOProxy moProxy;
    private final HeartbeatConsumerServiceImpl heartbeat;

    public HeartbeatRefreshTask(GroundMOProxy moProxy, HeartbeatConsumerServiceImpl heartbeat)
    {
      this.moProxy = moProxy;
      this.heartbeat = heartbeat;
    }
    int tryNumber = 0;

    @Override
    public void run()
    {
      synchronized (timer) {
        final Time currentTime = HelperTime.getTimestampMillis();
        // If the current time has passed the last beat + the beat period + a delta error
        long threshold = lastBeatAt.getValue() + period + DELTA_ERROR;
        if (currentTime.getValue() > threshold) {
          // Then the provider is unresponsive
          moProxy.setNmsAliveStatus(false);
          LOGGER.log(Level.INFO, "The heartbeat message from the provider was not received.");
          // Next time the heartbeat comes, trigger the lag measurement
          tryNumber = 3;
        } else {
          if (tryNumber >= 3) {
            // Every third try...
            try {
              long timestamp = System.currentTimeMillis();
              heartbeat.getHeartbeatStub().getPeriod();
              lag = System.currentTimeMillis() - timestamp; // Calculate the lag
            } catch (MALInteractionException | MALException ex) {
              LOGGER.log(Level.SEVERE, null, ex);
            }
            tryNumber = 0;
          }
          tryNumber++;
        }
      }
    }
  }

}
