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

import java.io.IOException;

/**
 * Running the segments of a constellation on Kubernetes, which is not written
 * yet.
 * <p>
 * The methods report that rather than doing nothing: a segment that is silently
 * never created is a constellation that is silently empty.
 */
public class KubernetesApi extends ContainerApi {

    private static String notWritten() {
        return "The segments of a constellation cannot be run on Kubernetes "
                + "yet. Unset the " + TOOL_PROPERTY + " property to run them "
                + "with Docker.";
    }

    public KubernetesApi() {
    }

    @Override
    public void run(String name, String[] keplerElements, int spacecraftNode) throws IOException {
        throw new UnsupportedOperationException(notWritten());
    }

    public void start(String name) throws IOException {
        throw new UnsupportedOperationException(notWritten());
    }

    @Override
    public void stop(String name) throws IOException {
        throw new UnsupportedOperationException(notWritten());
    }

    public String getIPAddress(String name) throws IOException {
        throw new UnsupportedOperationException(notWritten());
    }

    public void remove(String name) throws IOException {
        throw new UnsupportedOperationException(notWritten());
    }

    @Override
    public String getLogs(String name) throws IOException {
        return "";
    }
}
