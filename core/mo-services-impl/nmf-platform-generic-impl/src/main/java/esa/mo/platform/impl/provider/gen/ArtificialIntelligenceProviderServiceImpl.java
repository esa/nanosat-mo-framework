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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.platform.PlatformHelper;
import org.ccsds.moims.mo.platform.artificialintelligence.ArtificialIntelligenceHelper;
import org.ccsds.moims.mo.platform.artificialintelligence.provider.ArtificialIntelligenceInheritanceSkeleton;
import esa.mo.helpertools.connections.ConnectionProvider;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.platform.artificialintelligence.provider.DoComputerVisionInteraction;

/**
 * The ArtificialIntelligenceProviderServiceImpl class has the implementation of
 * the AI service.
 *
 * @author Cesar Coelho
 */
public class ArtificialIntelligenceProviderServiceImpl extends ArtificialIntelligenceInheritanceSkeleton {

    private static final Logger LOGGER = Logger.getLogger(ArtificialIntelligenceProviderServiceImpl.class.getName());
    private MALProvider aiServiceProvider;
    private boolean initialiased = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    private AIAdapterInterface adapter;
    private final static Long TIMESTAMP = System.currentTimeMillis();
    private final ArrayList<String> modelPaths = new ArrayList();

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param adapter The Artificial Intelligence adapter
     * @throws MALException On initialisation error.
     */
    public synchronized void init(AIAdapterInterface adapter) throws MALException {
        long timestamp = System.currentTimeMillis();
        
        if (!initialiased) {
            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(PlatformHelper.PLATFORM_AREA_NAME,
                    PlatformHelper.PLATFORM_AREA_VERSION) == null) {
                PlatformHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            try {
                ArtificialIntelligenceHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                // nothing to be done..
            }
        }

        // Shut down old service transport
        if (null != aiServiceProvider) {
            connection.closeAll();
        }

        this.adapter = adapter;
        aiServiceProvider = connection.startService(
                ArtificialIntelligenceHelper.ARTIFICIALINTELLIGENCE_SERVICE_NAME.toString(),
                ArtificialIntelligenceHelper.ARTIFICIALINTELLIGENCE_SERVICE, this);

        initialiased = true;
        timestamp = System.currentTimeMillis() - timestamp;
        LOGGER.info("Artificial Intelligence service: READY! (" + timestamp + " ms)");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != aiServiceProvider) {
                aiServiceProvider.close();
            }

            connection.closeAll();
        } catch (MALException ex) {
            LOGGER.log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    @Override
    public Long setModel(String modelPath, MALInteraction interaction) throws MALInteractionException, MALException {
        if (modelPath == null) {
            throw new MALException("The modelPath cannot be null!");
        }

        if (!modelPath.endsWith(".xml")) {
            throw new MALException("The model does not end with the file extension: .xml");
        }

        for (int i = 0; i < modelPaths.size(); i++) {
            String path = modelPaths.get(i);
            if (path.equals(modelPath)) {
                //Already exists... return the id:
                return TIMESTAMP + (long) i;
            }
        }

        modelPaths.add(modelPath);
        return TIMESTAMP + modelPaths.indexOf(modelPath);
    }

    @Override
    public String doInference(Long modelId, String inputTilesPath,
            MALInteraction interaction) throws MALInteractionException, MALException {
        if (modelId == null) {
            throw new MALException("The modelId is null!");
        }

        if (inputTilesPath == null) {
            throw new MALException("The inputTilesPath is null!");
        }

        // inputTilesPath needs to be an existing folder!!
        File inputTiles = new File(inputTilesPath);

        if (!inputTiles.exists()) {
            String msg = "The inputTilesPath does not exist in path: " + inputTilesPath;
            throw new MALInteractionException(
                    new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, msg)
            );
        }

        String modelPath = modelPaths.get((int) (modelId - TIMESTAMP));

        if (!modelPath.endsWith(".xml")) {
            throw new MALException("The model does not end with the file extension: .xml");
        }

        int endIndex = modelPath.length() - 4;
        String weightsPath = modelPath.substring(0, endIndex) + ".bin";

        try {
            String time = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String outputTilesPath = inputTiles.getParent() + File.separator + "output_tiles_" + time;

            Logger.getLogger(ArtificialIntelligenceProviderServiceImpl.class.getName()).log(
                    Level.INFO, "The AI service will do inference with the parameters:"
                    + "\n   >>       model: " + modelPath
                    + "\n   >>     weights: " + weightsPath
                    + "\n   >>  inputTiles: " + inputTilesPath
                    + "\n   >> outputTiles: " + outputTilesPath);

            adapter.executeInference(modelPath, weightsPath, inputTilesPath, outputTilesPath);

            Logger.getLogger(ArtificialIntelligenceProviderServiceImpl.class.getName()).log(
                    Level.INFO, "The inference has been completed successfully!");
            return outputTilesPath;
        } catch (IOException ex) {
            Logger.getLogger(ArtificialIntelligenceProviderServiceImpl.class.getName()).log(
                    Level.SEVERE, "The inference could not be performed!", ex);
        }

        throw new MALException("The inference could not be performed!");
    }

    @Override
    public void doComputerVision(String jsonPath, DoComputerVisionInteraction interaction) throws MALInteractionException, MALException {
        if (jsonPath == null) {
            throw new MALException("The jsonPath cannot be null!");
        }

        if (!jsonPath.endsWith(".json")) {
            throw new MALException("The file does not end with the file extension: .json");
        }

        File path = new File(jsonPath);

        if (!path.exists()) {
            String msg = "The file does not exist in path: " + path;
            throw new MALInteractionException(
                    new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, msg)
            );
        }

        if (interaction != null) {
            interaction.sendAcknowledgement();
        }

        boolean success = true;
        try {
            adapter.doComputerVision(jsonPath);
        } catch (IOException ex) {
            success = false;
        }

        if (interaction != null) {
            interaction.sendResponse(success);
        }
    }
}
