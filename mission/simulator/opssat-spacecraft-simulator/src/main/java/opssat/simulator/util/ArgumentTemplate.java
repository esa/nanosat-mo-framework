/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2021      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
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
package opssat.simulator.util;

import java.io.Serializable;

/**
 *
 * @author Cezar Suteu
 */
public class ArgumentTemplate implements Comparable<ArgumentTemplate>, Serializable {

    String description;
    String argContent;

    public ArgumentTemplate(String description, String argContent) {
        this.description = description;
        this.argContent = argContent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArgContent() {
        return argContent;
    }

    public void setArgContent(String argContent) {
        this.argContent = argContent;
    }

    @Override
    public String toString() {
        return description + CommandDescriptor.SEPARATOR_DATAFILES + argContent;
    }

    @Override
    public int compareTo(ArgumentTemplate o) {
        return description.compareTo(o.getDescription());
    }

}
