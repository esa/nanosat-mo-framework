/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
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
package esa.mo.com.impl.util;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Singleton class for generating Unique Object Instance IDs.
 *
 * @author Kevin Otto <Kevin@KevinOtto.de>
 */
public class ObjectInstanceIdGenerator
{

  private static ObjectInstanceIdGenerator instance;

  private final long EPOCH2020 = 1577836800;
  private final Random RNG = new Random(System.nanoTime());
  private final AtomicInteger objectInstanceIdCounter = new AtomicInteger(0);

  private long lastObjectInstanceIdTimeStamp;

  /**
   * Returns the Singleton instance of ObjectInstanceIdGenerator. The instance will be generated if
   * necessary.
   *
   * @return Singleton instance of ObjectInstanceIdGenerator
   */
  public static ObjectInstanceIdGenerator getInstance()
  {
    if (instance == null) {
      instance = new ObjectInstanceIdGenerator();
    }
    return instance;
  }

  /**
   * Creates a new unique actionID.
   *
   * ID consists of 40 bit timestamp, 12 bit counter and 12 bit random Number. The counter counts
   * the amount of generated IDs in the current Millisecond.
   *
   * @return a Unique action ID
   */
  public synchronized long generateObjectInstanceId()
  {
    long time = System.currentTimeMillis();

    if (lastObjectInstanceIdTimeStamp != time) {
      lastObjectInstanceIdTimeStamp = time;
      objectInstanceIdCounter.set(0);
    }
    int newId = objectInstanceIdCounter.getAndIncrement();
    return ((time - EPOCH2020) << 24) + (newId << 12) + RNG.nextInt(4096);
  }
}
