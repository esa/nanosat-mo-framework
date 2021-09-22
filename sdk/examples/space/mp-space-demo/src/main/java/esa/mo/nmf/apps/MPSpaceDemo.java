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
package esa.mo.nmf.apps;

import esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl;

/**
 * An NMF App that demonstrates MP services
 */
public class MPSpaceDemo {

    private final NanoSatMOConnectorImpl connector;

    public MPSpaceDemo() {
        MPSpaceDemoAdapter adapter = new MPSpaceDemoAdapter();
        connector = new NanoSatMOConnectorImpl();
        adapter.setNMF(connector);
        connector.init(adapter);
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String args[]) throws Exception {
        MPSpaceDemo test = new MPSpaceDemo();
    }
}
