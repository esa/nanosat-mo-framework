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
package esa.mo.ground.restservice;

/**
 * Class for containing ground track information
 *
 * @author Kevin Otto <Kevin@KevinOtto.de>
 */
public class GroundTrack
{

  private final long id;
  private final PositionAndTime[] trackPoints;

  public GroundTrack(long id, PositionAndTime[] track)
  {
    this.id = id;
    this.trackPoints = track;
  }

  public long getId()
  {
    return id;
  }

  public PositionAndTime[] getTrack()
  {
    return trackPoints;
  }

}
