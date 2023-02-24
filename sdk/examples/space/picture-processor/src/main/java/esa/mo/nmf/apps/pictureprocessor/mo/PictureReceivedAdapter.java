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
package esa.mo.nmf.apps.pictureprocessor.mo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.platform.camera.consumer.CameraAdapter;
import org.ccsds.moims.mo.platform.camera.structures.Picture;

import esa.mo.nmf.apps.pictureprocessor.process.PictureProcessingExecutor;
import esa.mo.nmf.apps.pictureprocessor.process.ProcessEventListener;

public class PictureReceivedAdapter extends CameraAdapter {

    public static final Long INIFINTE_TIMEOUT = null;

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSSS")
        .withZone(ZoneId.systemDefault());

    private final Path outputFolder;
    private final PictureProcessingExecutor executor;

    public PictureReceivedAdapter(ProcessEventListener processEventListener, 
            Long processRequestId, Path outputFolder,
            Integer minProcessDurationSeconds, Integer maxProcessDurationSeconds) {
        this.outputFolder = outputFolder;
        this.executor = new PictureProcessingExecutor(
                processEventListener, processRequestId,
                minProcessDurationSeconds, maxProcessDurationSeconds
        );
    }

    @Override
    public void takePictureResponseReceived(MALMessageHeader msgHeader, 
            Picture picture, java.util.Map qosProperties) {
        Path outputFile = outputFolder.resolve(pictureFileName());

        if (!savePicture(outputFile, picture)) {
            return;
        }

        executor.processPicture(outputFile);
    }

    public void stopProcess() {
        executor.destroyProcess();
    }

    private static boolean savePicture(Path outputFile, Picture picture) {
        try {
            // Store it in a file!
            Files.write(outputFile, picture.getContent().getValue());
        } catch (IOException | MALException e) {
            Logger.getLogger(PictureReceivedAdapter.class.getName()).log(Level.SEVERE,
                "Picture could not be saved to file!", e);
            return false;
        }

        return true;
    }

    private static String pictureFileName() {
        return String.format("picture_%s.jpg", DATETIME_FORMATTER.format(Instant.now()));
    }

}
