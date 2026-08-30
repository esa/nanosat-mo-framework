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

public class NmfAppModel {

    private final String name;
    private final String description;
    private final String category;
    private int running;
    private int installationCounter;

    public NmfAppModel(String name, String description, String category, boolean running) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.running = (running ? 1 : 0);
        this.installationCounter = 1;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public int getRunning() {
        return running;
    }

    public int getInstallationCounter() {
        return installationCounter;
    }

    public void increaseInstalledAndRunning() {
        this.running += 1;
        this.installationCounter += 1;
    }

    public void increaseInstalled() {
        this.installationCounter += 1;
    }

    public String getRunningCounter() {
        return this.running + "/" + this.installationCounter;
    }

}
