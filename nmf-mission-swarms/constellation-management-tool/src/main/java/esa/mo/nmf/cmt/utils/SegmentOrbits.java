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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The orbits of the segments of a constellation, as a file gives them.
 * <p>
 * A line names a segment and the six Keplerian elements it flies:
 * <pre>
 * name;A[km];E;i[deg];RAAN[deg];ARG_PER[deg];TRUE_A[deg]
 * </pre>
 * Blank lines and lines opening with {@code #} are passed over, so a file can
 * carry a heading of its own.
 * <p>
 * The window and the command line both read a constellation this way, from here,
 * so that a file describes the same constellation whichever of them is given it.
 */
public class SegmentOrbits {

    /**
     * How many fields a line carries: the name of the segment and its six
     * Keplerian elements.
     */
    private static final int FIELDS = 7;

    /**
     * Reads the segments a file describes.
     *
     * @param file The file to read.
     * @return The name of each segment and the Keplerian elements it flies, in
     * the order the file gives them, so that the same file always raises the
     * same constellation.
     * @throws IOException if the file cannot be read.
     * @throws IllegalArgumentException if a line does not describe a segment.
     * The message says which line and why, and is meant to be shown to whoever
     * wrote the file.
     */
    public static Map<String, String[]> read(File file) throws IOException {
        Map<String, String[]> orbits = new LinkedHashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int number = 0;

            while ((line = reader.readLine()) != null) {
                number++;
                String trimmed = line.trim();

                if (trimmed.isEmpty() || trimmed.charAt(0) == '#') {
                    continue;
                }

                String[] values = trimmed.split(";");

                if (values.length != FIELDS) {
                    throw new IllegalArgumentException("Line " + number + " carries "
                            + values.length + " fields where a segment and its six Keplerian "
                            + "elements are " + FIELDS + ": " + trimmed);
                }

                String name = values[0].trim();

                if (name.isEmpty()) {
                    throw new IllegalArgumentException("The segment on line " + number
                            + " has no name: " + trimmed);
                }

                String[] keplerElements = new String[FIELDS - 1];

                for (int i = 0; i < keplerElements.length; i++) {
                    keplerElements[i] = element(values[i + 1], number, trimmed);
                }

                if (orbits.put(name, keplerElements) != null) {
                    throw new IllegalArgumentException("Two segments go by one name, which has to "
                            + "tell them apart: " + name);
                }
            }
        }

        if (orbits.isEmpty()) {
            throw new IllegalArgumentException("The file describes no segment at all: "
                    + file.getPath());
        }
        return orbits;
    }

    /**
     * Returns one Keplerian element, having made sure it is a number.
     * <p>
     * The element travels to the segment as text, in its environment, and a
     * word among the numbers would only be found once the simulator was running
     * and its orbit came out wrong.
     *
     * @param value The element as the file writes it.
     * @param number The line it was written on.
     * @param line The line itself, for the message.
     * @return The element, trimmed.
     * @throws IllegalArgumentException if it is not a number.
     */
    private static String element(String value, int number, String line) {
        String element = value.trim();

        try {
            Double.parseDouble(element);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("A Keplerian element on line " + number
                    + " is not a number: " + element);
        }
        return element;
    }

    private SegmentOrbits() {
    }
}
