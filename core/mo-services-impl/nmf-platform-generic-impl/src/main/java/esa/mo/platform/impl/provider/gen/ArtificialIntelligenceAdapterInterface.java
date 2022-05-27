/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
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
package esa.mo.platform.impl.provider.gen;

import java.io.IOException;

public interface ArtificialIntelligenceAdapterInterface {

    /**
     * Sets a model to the Artificial Intelligence device.
     *
     * @param modelPath The file path to the model.
     * @param weightsPath The file path to the weights of the model.
     * @throws IOException If the model was not set.
     */
    public void setModel(String modelPath, String weightsPath) throws IOException;

    /**
     * Executes the inference on the Artificial Intelligence device.
     *
     * @param inputPath The path to a folder with a set of files to be
     * processed.
     * @param outputPath The path to a folder to store the processed files.
     */
    public void executeInference(String inputPath, String outputPath);

}
