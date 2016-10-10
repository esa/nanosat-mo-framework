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
package esa.mo.fw.configurationtool.services.mc;

import java.io.Serializable;

/**
 * Class that extends the basic Swing label class to add in automatic handling for displaying an update and also
 * calculating the transmission delay for the specific update. It also interacts with the DelayManager to calculate the
 * total delay.
 */
public class ParameterValueLabel implements Serializable
{
  private String labelValue = "";
  private boolean inError = false;

  public ParameterValueLabel()
  {
  }

  public String getLabelValue()
  {
    return labelValue;
  }

  public boolean isInError()
  {
    return inError;
  }

  public void setNewValue(final String newVal, final long iDiff)
  {

//    boolean updatelabel = false;
    boolean updatelabel = true;
    inError = false;

    // display the new value
    if (updatelabel)
    {
      labelValue = newVal;
    }

  }

  public void reset()
  {
    inError = false;
  }
}
