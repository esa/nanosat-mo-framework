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

import java.awt.Color;
import java.io.Serializable;

/**
 * Class that extends the basic Swing label class to add in automatic handling for displaying an update and also
 * calculating the transmission delay for the specific update. It also interacts with the DelayManager to calculate the
 * total delay.
 */
public class ParameterLabel extends javax.swing.JLabel implements Serializable
{
  private final Color[] colours = new Color[]
  {
    Color.GREEN, Color.BLACK, Color.BLACK, Color.GREEN
  };
  private final ParameterValueLabel value;
  private short oldval;

  public ParameterLabel(final int index)
  {
    super();
    value = new ParameterValueLabel();
    oldval = 1;
  }

  public void setNewValue(final String newVal, final long iDiff)
  {
    value.setNewValue(newVal, iDiff);
    displayValue();
  }

  public void displayValue()
  {
    String newVal = value.getLabelValue();
    
    // display the new value
    if ( newVal.equals("") ){
        setText("");
    }else{
        setText(String.valueOf(newVal));
   }

    // if we are in error we highlight the label in a different colour
    final int ii = Math.abs(oldval % 2);
    if ( !newVal.equals("") )
        oldval++;
    if (value.isInError())
    {
      setBackground(Color.RED);
    }
    else
    {
      setBackground(colours[2 + ii]);
      if ( newVal.equals("") )
          setBackground(Color.WHITE);
    }

    setForeground(colours[ii]);
  }

  public void setRed()
  {
      setBackground(Color.RED);
      setForeground(Color.BLACK);
  }

  public void reset()
  {
    value.reset();
    displayValue();
  }
}
