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
package esa.mo.nmf.cmt;

import esa.mo.nmf.cmt.utils.NanoSat;

/**
 * Told when the segments of the constellation have changed.
 * <p>
 * The tool reaches whoever is watching it through this interface rather than
 * through the window, so that it also runs where there is no window to refresh.
 */
public interface ConstellationListener {

    /**
     * Called once the segments of the constellation have been added or removed.
     */
    void constellationChanged();

    /**
     * Called as each segment is raised, before the rest of them are.
     * <p>
     * Raising a segment takes a moment, so a constellation of any size takes
     * several. This reports each one as it arrives, rather than leaving the
     * caller with nothing to show until the last of them is up.
     *
     * @param segment The segment that was just raised.
     */
    default void segmentRaised(NanoSat segment) {
    }
}
