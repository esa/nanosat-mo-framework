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
package esa.mo.nmf.mcadapters;

import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFProvider;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines the default MC set of every NMF Supervisor: the parameters and
 * actions that are present regardless of the mission, so that ground tooling
 * and cross-mission software can rely on them. The adapters returned here are
 * aggregated with the mission-specific adapter by an
 * {@link CompositeMCAdapter}.
 *
 * <p>
 * Parameter and action names follow a dotted hierarchy (e.g. {@code nmf.version},
 * {@code bootloader.primary.nmf-version}). Adding a default capability means
 * adding its adapter to {@link #create()}.
 *
 * @author Cesar Coelho
 */
public final class DefaultSupervisorAdapters {

    private DefaultSupervisorAdapters() {
    }

    /**
     * Creates the list of default Supervisor MC adapters.
     *
     * @param provider The Supervisor provider, used by adapters that report
     * action execution progress (e.g. the bootloader baseline commanding).
     * @return The default adapters, in registration order.
     */
    public static List<MonitorAndControlNMFAdapter> create(NMFProvider provider) {
        List<MonitorAndControlNMFAdapter> adapters = new ArrayList<>();
        adapters.add(new SupervisorInfoMCAdapter());
        adapters.add(new MemoryMCAdapter());
        adapters.add(new BootloaderMCAdapter(provider));
        return adapters;
    }
}
