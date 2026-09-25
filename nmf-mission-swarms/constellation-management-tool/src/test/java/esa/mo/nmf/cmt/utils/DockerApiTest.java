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
package esa.mo.nmf.cmt.utils;

import java.io.File;
import java.io.IOException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * What the tool makes of what a command tells it.
 * <p>
 * Docker itself is not needed to ask this: what is being tested is how a
 * command that fails is answered, and any command that fails will do.
 */
public class DockerApiTest {

    @Before
    public void requireAShell() {
        Assume.assumeTrue("A shell is needed to run a command with",
                new File("/bin/bash").canExecute());
    }

    @Test
    public void aCommandThatSucceedsIsAnswered() throws IOException {
        assertEquals("hello\n", DockerApi.executeCommand("echo hello"));
    }

    @Test
    public void whatACommandSaysOfItselfIsKeptWhicheverStreamItUses() throws IOException {
        assertEquals("out\nerr\n", DockerApi.executeCommand("echo out; echo err >&2"));
    }

    @Test
    public void aCommandThatFailsIsAFailure() {
        try {
            DockerApi.executeCommand("echo 'Address already in use' >&2; exit 125");
            fail("A command that ends non-zero should not pass for a success");
        } catch (IOException ex) {
            assertTrue("It says how it ended: " + ex.getMessage(),
                    ex.getMessage().contains("125"));
            assertTrue("It says what the command said: " + ex.getMessage(),
                    ex.getMessage().contains("Address already in use"));
        }
    }

    @Test
    public void aFailureIsAFailureEvenWhenTheCommandSaidNothing() {
        try {
            DockerApi.executeCommand("exit 1");
            fail("A silent failure is still a failure");
        } catch (IOException ex) {
            assertTrue(ex.getMessage().contains("ended with 1"));
        }
    }

    @Test
    public void theAddressOfANodeCarriesItsNumber() {
        assertEquals("172.28.0.1", DockerApi.addressOf(1));
        assertEquals("172.28.0.255", DockerApi.addressOf(255));
        assertEquals("172.28.1.0", DockerApi.addressOf(256));
        assertEquals("172.28.255.253", DockerApi.addressOf(65533));
    }

    @Test
    public void aNodeWithNoAddressToItIsRefused() {
        for (int node : new int[]{0, -1, 65534}) {
            try {
                DockerApi.addressOf(node);
                fail("This node has no address on the network: " + node);
            } catch (IllegalArgumentException ex) {
                // What is expected of a node the subnet cannot carry.
            }
        }
    }
}
