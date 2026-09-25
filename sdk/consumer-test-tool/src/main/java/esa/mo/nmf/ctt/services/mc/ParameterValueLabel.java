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

import java.io.Serializable;

/**
 * Class that extends the basic Swing label class to add in automatic handling
 * for displaying an update and also calculating the transmission delay for the
 * specific update. It also interacts with the DelayManager to calculate the
 * total delay.
 */
public class ParameterValueLabel implements Serializable {

    private String labelValue = "";
    private boolean inError = false;

    /**
     * Default constructor.
     */
    public ParameterValueLabel() {
    }

    /**
     * Returns the current textual value.
     *
     * @return the label value
     */
    public String getLabelValue() {
        return labelValue;
    }

    /**
     * Returns whether the current value represents an error.
     *
     * @return {@code true} if the value is in error
     */
    public boolean isInError() {
        return inError;
    }

    /**
     * Sets a new value and its error state.
     *
     * @param newVal the new value
     * @param isError whether the value represents an error
     */
    public void setNewValue(final String newVal, final boolean isError) {
        boolean updatelabel = true;
        inError = isError;

        // display the new value
        if (updatelabel) {
            labelValue = newVal;
        }
    }

    /**
     * Resets the value to empty and clears the error state.
     */
    public void reset() {
        inError = false;
    }
}
