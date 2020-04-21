/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2016      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under the European Space Agency Public License, Version 2.0
 *  You may not use this file except in compliance with the License.
 *
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ----------------------------------------------------------------------------
 */
package opssat.simulator.orekit;

import org.hipparchus.RealFieldElement;
import org.orekit.attitudes.Attitude;
import org.orekit.attitudes.AttitudeProvider;
import org.orekit.attitudes.FieldAttitude;
import org.orekit.frames.Frame;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.FieldAbsoluteDate;
import org.orekit.utils.FieldPVCoordinatesProvider;
import org.orekit.utils.PVCoordinatesProvider;

/**
 * Class for wrapping changing providers
 *
 *
 * @author Kevin Otto <Kevin@KevinOtto.de>
 */
public class AttitudeProviderWrapper implements AttitudeProvider
{

  private AttitudeProvider provider;

  public AttitudeProvider getProvider()
  {
    return provider;
  }

  public void setProvider(AttitudeProvider provider)
  {
    this.provider = provider;
  }

  public AttitudeProviderWrapper(AttitudeProvider provider)
  {
    this.provider = provider;
  }

  @Override
  public Attitude getAttitude(PVCoordinatesProvider pvProv, AbsoluteDate date, Frame frame)
  {
    return this.provider.getAttitude(pvProv, date, frame);
  }

  @Override
  public <T extends RealFieldElement<T>> FieldAttitude<T> getAttitude(
      FieldPVCoordinatesProvider<T> pvProv, FieldAbsoluteDate<T> date, Frame frame)
  {
    return this.provider.getAttitude(pvProv, date, frame);
  }
}
