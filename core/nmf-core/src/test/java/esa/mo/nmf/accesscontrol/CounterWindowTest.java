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

import org.junit.Assert;
import org.junit.Test;

/**
 * Holds that a {@link CounterWindow} accepts each counter of a session once,
 * in order or a little out of order, and never twice.
 */
public class CounterWindowTest {

    @Test
    public void countersInOrderAreAccepted() {
        CounterWindow window = new CounterWindow();
        for (long c = 1; c <= 1000; c++) {
            Assert.assertTrue("Counter " + c + " in order", window.accept(c));
        }
    }

    @Test
    public void aCounterIsAcceptedOnlyOnce() {
        CounterWindow window = new CounterWindow();
        Assert.assertTrue(window.accept(5));
        Assert.assertFalse("The same counter a second time", window.accept(5));
    }

    @Test
    public void countersALittleOutOfOrderAreEachAcceptedOnce() {
        CounterWindow window = new CounterWindow();
        long[] arrival = {3, 1, 2, 6, 4, 5, 10, 9, 7, 8};
        for (long c : arrival) {
            Assert.assertTrue("Counter " + c + " out of order", window.accept(c));
        }
        for (long c : arrival) {
            Assert.assertFalse("Counter " + c + " again", window.accept(c));
        }
    }

    @Test
    public void aCounterOlderThanTheWindowIsRefused() {
        CounterWindow window = new CounterWindow();
        Assert.assertTrue(window.accept(1));
        Assert.assertTrue(window.accept(1 + CounterWindow.WIDTH));
        Assert.assertFalse("A counter that fell out of the window", window.accept(1));
        Assert.assertTrue("The oldest counter still inside the window", window.accept(2));
    }

    @Test
    public void aCounterOfZeroOrLessIsRefused() {
        CounterWindow window = new CounterWindow();
        Assert.assertFalse(window.accept(0));
        Assert.assertFalse(window.accept(-1));
    }

    @Test
    public void aLargeJumpClearsTheWindow() {
        CounterWindow window = new CounterWindow();
        for (long c = 1; c <= 10; c++) {
            window.accept(c);
        }
        Assert.assertTrue(window.accept(1_000_000));
        Assert.assertTrue("A counter inside the new window, not seen yet", window.accept(999_999));
        Assert.assertFalse("A counter from before the jump", window.accept(10));
    }
}
