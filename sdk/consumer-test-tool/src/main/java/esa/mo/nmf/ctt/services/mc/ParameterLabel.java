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
package esa.mo.nmf.ctt.services.mc;

import java.awt.Color;
import java.io.Serializable;

/**
 *
 */
public class ParameterLabel extends javax.swing.JLabel implements Serializable {

    private final Color[] coloursBackground = new Color[]{Color.GREEN, Color.BLACK};
    private final Color[] coloursText = new Color[]{Color.BLACK, Color.WHITE};

    private final ParameterValueLabel value;
    private short counter;

    public ParameterLabel(final int index) {
        super();
        value = new ParameterValueLabel();
        counter = 1;
    }

    public void setNewValue(final String newVal, final boolean isError) {
        value.setNewValue(newVal, isError);
        displayValue();
    }

    public void displayValue() {
        String newVal = value.getLabelValue();

        // display the new value
        if (newVal.equals("")) {
            setText("");
        } else {
            setText(newVal);
        }

        // if we are in error we highlight the label in a different colour
        final int ii = Math.abs(counter % 2);

        if (!newVal.equals("")) {
            counter++;
        }

        if (value.isInError()) {
            setBackground(Color.RED);
        } else {
            setBackground(coloursBackground[ii]);
            if (newVal.equals("")) {
                setBackground(Color.WHITE);
            }
        }

        setForeground(coloursText[ii]);
    }

    public void setRed() {
        setBackground(Color.RED);
        setForeground(Color.BLACK);
    }

    public void reset() {
        value.reset();
        displayValue();
    }
}
