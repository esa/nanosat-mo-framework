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

// TODO: implement Kubernetes API
public class KubernetesApi extends ContainerApi {

    public KubernetesApi() {
    }

    @Override
    public void run(String name, String[] keplerElements) throws IOException {

    }

    public void start(String name) throws IOException {
    }

    @Override
    public void stop(String name) throws IOException {

    }

    public String getIPAddress(String name) throws IOException {
        return null;
    }

    public void remove(String name) throws IOException {
    }

    @Override
    public String getLogs(String name) throws IOException {
        return "";
    }
}
