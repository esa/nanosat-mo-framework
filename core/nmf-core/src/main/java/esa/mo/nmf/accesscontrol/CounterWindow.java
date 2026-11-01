/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2026      European Space Agency
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
package esa.mo.nmf.accesscontrol;

/**
 * The counters that one session of a caller has already used, so that a request
 * read off the wire is refused when it is sent again.
 * <p>
 * The window holds the highest counter it has accepted and which of the
 * {@value #WIDTH} before it it has seen, as in the anti-replay window of IPsec
 * (RFC 6479). A counter higher than the highest moves the window up; a counter
 * within the window is accepted once and then refused; a counter older than the
 * window, or one already seen, is refused.
 * <p>
 * A process signs with one session and one counter for all its threads, so its
 * requests may arrive out of the order in which their counters were taken, by
 * as many as are in flight at once. The window is wide enough for that.
 * <p>
 * The counters are kept in a ring of bits, one per counter modulo the width,
 * so that moving the window up clears only the counters it skips over.
 * <p>
 * It is not safe for use from several threads at once; the caller holds one per
 * session and guards it.
 */
public final class CounterWindow {

    /** How far out of order a request may arrive and still be accepted. */
    public static final int WIDTH = 4096;

    private static final int BITS_PER_WORD = 64;

    private long highest = 0;
    private final long[] ring = new long[WIDTH / BITS_PER_WORD];

    /**
     * Accepts a counter if it has not been seen and is not too old, and records
     * it. A counter of zero or less is never accepted, so the first request of
     * a session uses one.
     *
     * @param counter the counter of a request
     * @return true if the counter is accepted, false if it is a repeat or too old
     */
    public boolean accept(final long counter) {
        if (counter <= 0) {
            return false;
        }
        if (counter > highest) {
            if (counter - highest >= WIDTH) {
                java.util.Arrays.fill(ring, 0L);
            } else {
                for (long skipped = highest + 1; skipped < counter; skipped++) {
                    clear(skipped);
                }
            }
            highest = counter;
            set(counter);
            return true;
        }
        if (highest - counter >= WIDTH || isSet(counter)) {
            return false;
        }
        set(counter);
        return true;
    }

    private boolean isSet(final long counter) {
        int slot = (int) (counter % WIDTH);
        return (ring[slot / BITS_PER_WORD] & (1L << (slot % BITS_PER_WORD))) != 0;
    }

    private void set(final long counter) {
        int slot = (int) (counter % WIDTH);
        ring[slot / BITS_PER_WORD] |= 1L << (slot % BITS_PER_WORD);
    }

    private void clear(final long counter) {
        int slot = (int) (counter % WIDTH);
        ring[slot / BITS_PER_WORD] &= ~(1L << (slot % BITS_PER_WORD));
    }
}
