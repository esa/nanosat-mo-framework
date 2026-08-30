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
    SIMULATOR_LITE("nmf-mission-simulator-lite", "Simulator, lite"),

    /**
     * The spacecraft simulator with the orbital mechanics library. It
     * propagates the orbit and answers the rest of the Platform services, at
     * the cost of the data the propagator carries.
     */
    SIMULATOR_OREKIT("nmf-mission-simulator-orekit", "Simulator, Orekit"),

    /**
     * The mission with no Platform services at all, for a segment that is only
     * to be talked to rather than flown.
     */
    BAREBONE("nmf-mission-barebone", "Barebone");

    private final String image;

    private final String label;

    SegmentImage(String image, String label) {
        this.image = image;
        this.label = label;
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
     * @return The image a constellation is made of unless another is chosen.
     */
    public static SegmentImage getDefault() {
        return SIMULATOR_LITE;
    }
}
