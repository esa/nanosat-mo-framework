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
package esa.mo.nmf;

import java.util.EventListener;


/**
 * The CloseAppListener provides a simple interface to be implemented by the
 * App developers in order to provide App-specific operations before closing 
 * the App.
 * 
 */
public interface CloseAppListener extends EventListener {
    
    /**
     * The onClose signature shall be called when the app is requested to be
     * closed.
     *
     * @return Returns true if everything was closed successfully, false 
     * otherwise.
     */
    public Boolean onClose();
    
}
