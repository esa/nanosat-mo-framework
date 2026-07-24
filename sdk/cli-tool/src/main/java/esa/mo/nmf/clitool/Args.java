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
package esa.mo.nmf.clitool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Minimal command-line argument parser.
 *
 * <p>
 * Callers consume named options and boolean flags first, then retrieve the
 * remaining tokens as positional arguments. Because consumption is destructive,
 * the order in which {@link #option} and {@link #flag} are called does not
 * matter — every named token is found by scanning the full list regardless of
 * its position.
 */
public class Args {

    private final List<String> tokens;

    /**
     * Creates an argument holder from the given tokens.
     *
     * @param tokens the command line tokens
     */
    public Args(String[] tokens) {
        this.tokens = new ArrayList<>(Arrays.asList(tokens));
    }

    /**
     * Finds the first occurrence of any of the given names, removes it and its
     * following value from the token list, and returns the value. Returns
     * {@code null} if none of the names is present.
     *
     * @param names the accepted names of the option (for example {@code -r}, {@code --remote})
     * @return the option value, or {@code null} if the option is not present
     */
    public String option(String... names) {
        for (int i = 0; i < tokens.size() - 1; i++) {
            for (String name : names) {
                if (tokens.get(i).equals(name)) {
                    tokens.remove(i);
                    return tokens.remove(i);
                }
            }
        }
        return null;
    }

    /**
     * Returns {@code true} and removes the token if any of the given names is
     * present; otherwise returns {@code false}.
     *
     * @param names the accepted names of the flag
     * @return {@code true} if the flag was present (and consumed)
     */
    public boolean flag(String... names) {
        for (int i = 0; i < tokens.size(); i++) {
            for (String name : names) {
                if (tokens.get(i).equals(name)) {
                    tokens.remove(i);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns all remaining tokens after named options and flags have been
     * consumed. These are the positional arguments for the current command.
     *
     * @return the remaining positional arguments
     */
    public List<String> positionals() {
        return new ArrayList<>(tokens);
    }

    /**
     * Returns the Nth remaining token (0-based), or {@code null} if there are
     * fewer than {@code index + 1} tokens left.
     *
     * @param index the 0-based index of the positional argument
     * @return the positional argument, or {@code null} if there is none at that index
     */
    public String positional(int index) {
        return index < tokens.size() ? tokens.get(index) : null;
    }

    /**
     * Returns whether there are no tokens left.
     *
     * @return {@code true} if no tokens remain
     */
    public boolean isEmpty() {
        return tokens.isEmpty();
    }
}
