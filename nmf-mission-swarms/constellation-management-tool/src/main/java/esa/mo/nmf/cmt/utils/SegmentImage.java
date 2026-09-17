/* ----------------------------------------------------------------------------
 * Copyright (C) 2022      European Space Agency
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
 *
 * Author: N Wiegand (https://github.com/Klabau)
 */
package esa.mo.nmf.cmt.utils;

/**
 * The image a segment of the constellation runs.
 * <p>
 * Each is built by a mission of the framework, from the Dockerfile beside the
 * module that generates its Space File System:
 * {@code mvn -pl <mission>/<module> install -Pdocker}. They take the orbit the
 * same way, through the environment, so a constellation can mix them.
 * <p>
 * The Raspberry Pi mission is not here on purpose: it is an implementation for
 * the hardware of a real spacecraft rather than a segment to simulate.
 */
public enum SegmentImage {

    /**
     * The spacecraft simulator without the orbital mechanics library. It works
     * the orbit out analytically and reports the position, which is all a
     * constellation of many segments usually needs.
     */
    SIMULATOR_LITE("nmf-mission-simulator-lite", "Lite Simulator", "lite"),

    /**
     * The spacecraft simulator with the orbital mechanics library. It
     * propagates the orbit and answers the rest of the Platform services, at
     * the cost of the data the propagator carries.
     */
    SIMULATOR_OREKIT("nmf-mission-simulator-orekit", "Orekit Simulator", "orekit"),

    /**
     * The mission with no Platform services at all, for a segment that is only
     * to be talked to rather than flown.
     */
    BAREBONE("nmf-mission-barebone", "Barebone", "barebone");

    private final String image;

    private final String label;

    private final String option;

    SegmentImage(String image, String label, String option) {
        this.image = image;
        this.label = label;
        this.option = option;
    }

    /**
     * @return The name of the image, as Docker knows it.
     */
    public String getImage() {
        return image;
    }

    /**
     * @return The name of the image as it is offered to the operator.
     */
    @Override
    public String toString() {
        return label;
    }

    /**
     * @return The name this image is given on the command line.
     */
    public String getOption() {
        return option;
    }

    /**
     * Returns the image that a command line names.
     *
     * @param option The name of the image, as the command line writes it.
     * @return The image of that name.
     * @throws IllegalArgumentException if no image goes by that name.
     */
    public static SegmentImage fromOption(String option) {
        for (SegmentImage image : values()) {
            if (image.option.equalsIgnoreCase(option)) {
                return image;
            }
        }
        throw new IllegalArgumentException("There is no image named: " + option);
    }

    /**
     * @return The names of every image, as the command line writes them.
     */
    public static String options() {
        StringBuilder builder = new StringBuilder();
        for (SegmentImage image : values()) {
            if (builder.length() != 0) {
                builder.append("|");
            }
            builder.append(image.option);
        }
        return builder.toString();
    }

    /**
     * @return The image a constellation is made of unless another is chosen.
     */
    public static SegmentImage getDefault() {
        return SIMULATOR_LITE;
    }
}
