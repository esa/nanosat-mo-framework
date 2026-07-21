/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
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
package esa.mo.nmf.apps.broken;

import esa.mo.nmf.CloseAppListener;
import esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A deliberately misbehaving NMF app, used only by the testbeds to exercise
 * non-nominal paths. It exposes:
 * <ul>
 * <li>a parameter, an action and an aggregation that always fail on retrieval
 * (see {@link BrokenMCAdapter}), each paired with a healthy counterpart used as
 * a control;</li>
 * <li>a {@code shutdown.hang} action that arms the app to block during its next
 * shutdown, so the Supervisor's stop-timeout and force-kill path can be
 * observed.</li>
 * </ul>
 */
public class BrokenApp {

    private static final Logger LOGGER = Logger.getLogger(BrokenApp.class.getName());

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments.
     * @throws Exception if there is an error during initialisation.
     */
    public static void main(final String[] args) throws Exception {
        final NanoSatMOConnectorImpl connector = new NanoSatMOConnectorImpl();
        final BrokenMCAdapter adapter = new BrokenMCAdapter();
        connector.init(adapter);

        connector.setCloseAppListener(new CloseAppListener() {
            @Override
            public Boolean onClose() {
                long delayMs = adapter.getShutdownDelayMs();
                if (delayMs > 0) {
                    LOGGER.log(Level.WARNING, "onClose() blocking for {0} ms to simulate a slow "
                            + "(or hung) shutdown.", delayMs);
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
                return true;
            }
        });
    }

}
