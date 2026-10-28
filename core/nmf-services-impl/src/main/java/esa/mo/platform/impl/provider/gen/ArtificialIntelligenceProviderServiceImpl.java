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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.InvalidArgumentException;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.UnknownException;
import org.ccsds.moims.mo.platform.DeviceInUseException;
import org.ccsds.moims.mo.platform.DeviceNotAvailableException;
import org.ccsds.moims.mo.platform.ExecutionFailedException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionProvider;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.platform.artificialintelligence.ArtificialIntelligenceHelper;
import org.ccsds.moims.mo.platform.artificialintelligence.ArtificialIntelligenceServiceInfo;
import org.ccsds.moims.mo.platform.artificialintelligence.provider.ArtificialIntelligenceInheritanceSkeleton;
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
     * Default constructor.
     */
    public ArtificialIntelligenceProviderServiceImpl() {
    }

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param adapter The Artificial Intelligence adapter
     * @throws MALException On initialisation error.
     */
    public synchronized void init(AIAdapterInterface adapter) throws MALException {
        long timestamp = System.currentTimeMillis();

        // Shut down old service transport
        if (null != aiServiceProvider) {
            connection.closeAll();
        }

        this.adapter = adapter;
        aiServiceProvider = connection.startService(
                ArtificialIntelligenceServiceInfo.ARTIFICIALINTELLIGENCE_SERVICE_NAME.toString(),
                ArtificialIntelligenceHelper.ARTIFICIALINTELLIGENCE_SERVICE, this);

        initialiased = true;
        timestamp = System.currentTimeMillis() - timestamp;
        LOGGER.log(Level.INFO, "Artificial Intelligence service: READY! ({0} ms)", timestamp);
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

    /**
     * Whether the given value holds a control character, such as a newline or a
     * NUL. Such a character cannot be part of a legitimate single path and is a
     * classic aid to command injection, so a path that holds one is refused.
     *
     * @param value the value to check
     * @return {@code true} if the value holds a control character
     */
    private static boolean hasControlCharacter(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isISOControl(value.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Long setModel(String modelPath, MALInteraction interaction) throws InvalidArgumentException, MALException {
        if (modelPath == null) {
            throw new InvalidArgumentException("The modelPath field must not be NULL");
        }

        if (!modelPath.endsWith(".xml")) {
            throw new InvalidArgumentException("The modelPath field must end with .xml");
        }

        // The path becomes an argument of the inference command, so it must be a
        // real model file and hold no control characters (NMF-2026-009).
        if (hasControlCharacter(modelPath)) {
            throw new InvalidArgumentException("The modelPath field must not contain a control character");
        }

        if (!new File(modelPath).isFile()) {
            throw new InvalidArgumentException("The modelPath field must refer to an existing file: " + modelPath);
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
            MALInteraction interaction) throws InvalidArgumentException, UnknownException,
            ExecutionFailedException, MALException {
        if (modelId == null) {
            throw new InvalidArgumentException("The modelId field must not be NULL");
        }

        if (inputTilesPath == null) {
            throw new InvalidArgumentException("The inputTilesPath field must not be NULL");
        }

        // The path becomes an argument of the inference command, so it must be a
        // real folder and hold no control characters (NMF-2026-009).
        if (hasControlCharacter(inputTilesPath)) {
            throw new InvalidArgumentException("The inputTilesPath field must not contain a control character");
        }

        // inputTilesPath needs to be an existing folder!!
        File inputTiles = new File(inputTilesPath);

        if (!inputTiles.isDirectory()) {
            String msg = "The inputTilesPath field must refer to an existing folder: " + inputTilesPath;
            throw new InvalidArgumentException(msg);
        }

        long index = modelId - TIMESTAMP;
        if (index < 0 || index >= modelPaths.size()) {
            throw new UnknownException(null);
        }
        String modelPath = modelPaths.get((int) index);

        if (!modelPath.endsWith(".xml")) {
            throw new InvalidArgumentException("The model of the modelId field must end with .xml");
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
            throw new ExecutionFailedException("The inference could not be performed: " + ex.getMessage());
        }
    }

    @Override
    public void doComputerVision(String jsonPath,
            DoComputerVisionInteraction interaction) throws InvalidArgumentException, ExecutionFailedException,
            DeviceNotAvailableException, DeviceInUseException, MALException {
        if (jsonPath == null) {
            throw new InvalidArgumentException("The jsonPath field must not be NULL");
        }

        if (!jsonPath.endsWith(".json")) {
            throw new InvalidArgumentException("The jsonPath field must end with .json");
        }

        if (hasControlCharacter(jsonPath)) {
            throw new InvalidArgumentException("The jsonPath field must not contain a control character");
        }

        File path = new File(jsonPath);

        if (!path.isFile()) {
            String msg = "The jsonPath field must refer to an existing file: " + path;
            throw new InvalidArgumentException(msg);
        }

        if (interaction != null) {
            interaction.sendAcknowledgement();
        }

        try {
            adapter.doComputerVision(jsonPath);
        } catch (IOException ex) {
            Logger.getLogger(ArtificialIntelligenceProviderServiceImpl.class.getName()).log(
                    Level.SEVERE, "The computer vision task failed!", ex);
            throw new ExecutionFailedException("The computer vision task failed: " + ex.getMessage());
        }

        if (interaction != null) {
            interaction.sendResponse();
        }
    }
}
