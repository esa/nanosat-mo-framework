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
package esa.mo.sm.impl.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import esa.mo.helpertools.misc.TaskScheduler;

public class ProcessExecutionHandler
{

  public interface Callbacks
  {

    /**
     * Called to transmit chunk of stdout data.
     *
     * @param objId Associated object ID (meaningful in the callee context)
     * @param data  Chunk of stdout data.
     */
    void flushStdout(Long objId, String data);

    /**
     * Called to transmit chunk of stderr data.
     *
     * @param objId Associated object ID (meaningful in the callee context)
     * @param data  Chunk of stderr data.
     */
    void flushStderr(Long objId, String data);

    /**
     * Called when application exits.
     *
     * @param objId    Associated object ID (meaningful in the callee context)
     * @param exitCode Application exit code
     */
    void processStopped(Long objId, int exitCode);
  };

  private final TaskScheduler timer = new TaskScheduler(1);
  private static final int PERIOD_PUB = 2 * 1000; // Publish every 2 seconds
  private final Long objId;
  private Thread stdoutReader;
  private Thread stderrReader;
  private Process process = null;
  private Callbacks cb = null;
  private static final Logger LOGGER = Logger.getLogger(ProcessExecutionHandler.class.getName());

  public ProcessExecutionHandler(final Callbacks cb, final Long objId)
  {
    this.cb = cb;
    this.objId = objId;
  }

  public Long getObjId()
  {
    return objId;
  }

  public Process getProcess()
  {
    return process;
  }

  public void close()
  {
    timer.stopLast();
    process.destroy();
  }

  public void monitorProcess(final Process process)
  {
    this.process = process;
    final StringBuffer stdoutBuf = new StringBuffer();
    final StringBuffer stderrBuf = new StringBuffer();
    // Every PERIOD_PUB seconds, publish the String data
    timer.scheduleTask(new TimerTaskImpl(stdoutBuf, stderrBuf), 0, PERIOD_PUB, TimeUnit.MILLISECONDS, false);
    stdoutReader = createReaderThread(stdoutBuf, new BufferedReader(new InputStreamReader(
        process.getInputStream())));
    stderrReader = createReaderThread(stderrBuf, new BufferedReader(new InputStreamReader(
        process.getErrorStream())));
    stdoutReader.start();
    stderrReader.start();
    new Thread()
    {
      @Override
      public void run()
      {
        try {
          int exitCode = process.waitFor();
          cb.processStopped(objId, exitCode);
        } catch (InterruptedException ex) {
          // Thread interrupted, pretend the application exited succesfully
          cb.processStopped(objId, 0);
        }
      }
    }.start();
  }

  private Thread createReaderThread(final StringBuffer buf, final BufferedReader br)
  {
    return new Thread()
    {
      @Override
      public void run()
      {
        this.setName("ProcessExecutionHandler_ReaderThread");
        try {
          String line;
          while ((line = br.readLine()) != null) {
            buf.append(line);
            buf.append("\n");
          }
        } catch (IOException ex) {
          LOGGER.log(Level.INFO,
              "The stream of the process (objId: {0}) has been closed.", new Object[]{
                objId});
          close();
        }
      }
    };
  }

  private class TimerTaskImpl extends Thread
  {

    private final StringBuffer stdoutBuf;
    private final StringBuffer stderrBuf;

    public TimerTaskImpl(StringBuffer stdoutBuf, StringBuffer stderrBuf)
    {
      this.stdoutBuf = stdoutBuf;
      this.stderrBuf = stderrBuf;
    }

    @Override
    public void run()
    {
      String data = getBufferData(stdoutBuf);
      if (data != null) {
        cb.flushStdout(objId, data);
        LOGGER.log(Level.FINE, data);
      }
      data = getBufferData(stderrBuf);
      if (data != null) {
        cb.flushStderr(objId, data);
        LOGGER.log(Level.FINE, data);
      }

    }

    private String getBufferData(StringBuffer buffer)
    {
      // Change the buffer position
      int bufSize = buffer.length();
      if (bufSize != 0) {
        String dataToPropagate = buffer.substring(0, bufSize);
        buffer.delete(0, bufSize);
        return dataToPropagate;
      }
      return null;
    }
  }

}
