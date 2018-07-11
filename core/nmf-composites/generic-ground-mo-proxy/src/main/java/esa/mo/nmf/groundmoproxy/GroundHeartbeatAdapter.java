/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

/**
 *
 * @author Dominik Marszk
 */
class GroundHeartbeatAdapter extends HeartbeatAdapter {

  private static final long DELTA_ERROR = 2 * 1000; // 2 seconds = 2000 milliseconds
  private final long period; // In seconds
  private long lag; // In milliseconds
  private final Timer timer;
  private Time lastBeatAt = HelperTime.getTimestampMillis();
  private final GroundMOProxy moProxy;

  public GroundHeartbeatAdapter(final HeartbeatConsumerServiceImpl heartbeat, final GroundMOProxy moProxy) throws MALInteractionException, MALException {
    this.moProxy = moProxy;
    long timestamp = System.currentTimeMillis();
    double value = heartbeat.getHeartbeatStub().getPeriod().getValue();
    lag = System.currentTimeMillis() - timestamp;
    period = (long) (value * 1000);
    Logger.getLogger(GroundHeartbeatAdapter.class.getName()).log(Level.INFO, "The provider is reachable! Beat period: {0} seconds", value);
    moProxy.setNmsAliveStatus(true);
    timer = new Timer("HeartbeatMonitorTimer");
    timer.scheduleAtFixedRate(new HeartbeatRefreshTask(moProxy, heartbeat), period, period);
  }

  @Override
  public synchronized void beatNotifyReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader, org.ccsds.moims.mo.mal.structures.Identifier _Identifier0, org.ccsds.moims.mo.mal.structures.UpdateHeaderList _UpdateHeaderList1, java.util.Map qosProperties) {
    synchronized (timer) {
      lastBeatAt = HelperTime.getTimestampMillis();
      final Time onboardTime = msgHeader.getTimestamp();
      final long iDiff = lastBeatAt.getValue() - onboardTime.getValue();
      Logger.getLogger(GroundHeartbeatAdapter.class.getName()).log(Level.INFO, "(Clocks diff: {0} ms | Round-Trip Delay time: {1} ms | Last beat received at: {2})", new Object[]{iDiff, lag, HelperTime.time2readableString(lastBeatAt)});
      moProxy.setNmsAliveStatus(true);
    }
  }

  public FineTime getLastBeat() {
    return HelperTime.timeToFineTime(lastBeatAt);
  }

  private class HeartbeatRefreshTask extends TimerTask {

    private final GroundMOProxy moProxy;
    private final HeartbeatConsumerServiceImpl heartbeat;

    public HeartbeatRefreshTask(GroundMOProxy moProxy, HeartbeatConsumerServiceImpl heartbeat) {
      this.moProxy = moProxy;
      this.heartbeat = heartbeat;
    }
    int tryNumber = 0;

    @Override
    public void run() {
      synchronized (timer) {
        final Time currentTime = HelperTime.getTimestampMillis();
        // If the current time has passed the last beat + the beat period + a delta error
        long threshold = lastBeatAt.getValue() + period + DELTA_ERROR;
        if (currentTime.getValue() > threshold) {
          // Then the provider is unresponsive
          moProxy.setNmsAliveStatus(false);
          Logger.getLogger(GroundHeartbeatAdapter.class.getName()).log(Level.INFO, "The heartbeat message from the provider was not received.");
        } else {
          if (tryNumber == 3) {
            // Every third try...
            try {
              long timestamp = System.currentTimeMillis();
              heartbeat.getHeartbeatStub().getPeriod();
              lag = System.currentTimeMillis() - timestamp; // Calculate the lag
            } catch (MALInteractionException ex) {
              Logger.getLogger(GroundHeartbeatAdapter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALException ex) {
              Logger.getLogger(GroundHeartbeatAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
            tryNumber = 0;
          }
        }
        tryNumber++;
      }
    }
  }

}
