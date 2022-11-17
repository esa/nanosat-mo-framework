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
package esa.mo.nmf.apps;

import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFInterface;
import esa.mo.nmf.annotations.Action;
import esa.mo.nmf.annotations.ActionParameter;
import esa.mo.nmf.annotations.Parameter;
import esa.mo.nmf.sdk.OrekitResources;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormat;
import org.orekit.data.DataProvidersManager;
import org.orekit.errors.OrekitException;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;

/**
 * Class for Interfacing with the Camera Acquisitor System. This class handles all Parameters and
 * forwards commands to the corresponding Classes that handle them.
 */
public class CameraAcquisitorSystemMCAdapter extends MonitorAndControlNMFAdapter {

    private static final Logger LOGGER = Logger.getLogger(CameraAcquisitorSystemMCAdapter.class.getName());

    private NMFInterface connector;

    private final CameraAcquisitorSystemCameraTargetHandler cameraTargetHandler;
    private final CameraAcquisitorSystemCameraHandler cameraHandler;
    private final CameraAcquisitorSystemGPSHandler gpsHandler;

    void recoverLastState() {
        this.cameraTargetHandler.recoverLastState();
    }

    public enum ExposureTypeModeEnum {
        CUSTOM, AUTOMATIC // maybe implement hdr?
    }

    // ----------------------------------- Parameters -----------------------------------------------

    @Parameter(description = "The red channel gain", generationEnabled = false)
    private float gainRed = 8.0f;

    @Parameter(description = "The green channel gain", generationEnabled = false)
    private float gainGreen = 8.0f;

    @Parameter(description = "The blue channel gain", generationEnabled = false)
    private float gainBlue = 8.0f;

    @Parameter(description = "The camera's exposure Type (CUSTOM = 0, AUTOMATIC = 1)", generationEnabled = false)
    private byte exposureType = 0;

    @Parameter(description = "The camera's exposure time (only used if exposureType is CUSTOM)",
               generationEnabled = false)
    private float exposureTime = 0.003f;

    @Parameter(description = "The maximum time (in Milliseconds) the Satellite will take to rotated if it's in the worst possible orientation",
               generationEnabled = false)
    private long worstCaseRotationTimeMS = 1000000;

    @Parameter(description = "The time (in Milliseconds) the Satelite will start the attitude control prior to reaching a target",
               generationEnabled = false)
    private long attitudeSaftyMarginMS = 20000;

    @Parameter(description = "The width (x resolution) of the picture taken by the camera", generationEnabled = false)
    private int pictureWidth = 2048;

    @Parameter(description = "The height (y resolution) of the picture taken by the camera", generationEnabled = false)
    private int pictureHeight = 1944;

    @Parameter(description = "The picture type to use (uses PictureFormat ENUM: RAW=0, RGB24=1, BMP=2, PNG=3, JPG=4)",
               generationEnabled = false)
    private int pictureType = 3;

    // ----------------------------------------------------------------------------------------------
    public PictureFormat getPictureType() {
        return PictureFormat.fromOrdinal(pictureType);
    }

    public float getGainRed() {
        return gainRed;
    }

    public float getGainGreen() {
        return gainGreen;
    }

    public float getGainBlue() {
        return gainBlue;
    }

    public ExposureTypeModeEnum getExposureType() {
        return ExposureTypeModeEnum.values()[exposureType];
    }

    public float getExposureTime() {
        return exposureTime;
    }

    public int getPictureWidth() {
        return pictureWidth;
    }

    public int getPictureHeight() {
        return pictureHeight;
    }

    public long getWorstCaseRotationTimeMS() {
        return worstCaseRotationTimeMS;
    }

    public long getWorstCaseRotationTimeSeconds() {
        return worstCaseRotationTimeMS / 1000;
    }

    public CameraAcquisitorSystemCameraHandler getCameraHandler() {
        return cameraHandler;
    }

    public CameraAcquisitorSystemCameraTargetHandler getCameraTargetHandler() {
        return cameraTargetHandler;
    }

    public CameraAcquisitorSystemGPSHandler getGpsHandler() {
        return gpsHandler;
    }

    public NMFInterface getConnector() {
        return connector;
    }

    public CameraAcquisitorSystemMCAdapter(final NMFInterface connector) {
        try {
            // load orekit-data wich is required for many parts of orekit to work.
            LOGGER.log(Level.INFO, "Loading orekit data");
            DataProvidersManager manager = DataProvidersManager.getInstance();
            manager.addProvider(OrekitResources.getOrekitData());
        } catch (OrekitException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialise Orekit:\n{0}", e.getMessage());
        }

        this.connector = connector;
        LOGGER.log(Level.INFO, "init cameraTargetHandler");
        this.cameraTargetHandler = new CameraAcquisitorSystemCameraTargetHandler(this);
        LOGGER.log(Level.INFO, "init gpsHandler");
        this.gpsHandler = new CameraAcquisitorSystemGPSHandler(this);
        LOGGER.log(Level.INFO, "init cameraHandler");
        this.cameraHandler = new CameraAcquisitorSystemCameraHandler(this);

    }

    @Action(description = "queues a new photograph target at the Specified Timestamp",
            stepCount = CameraAcquisitorSystemCameraTargetHandler.PHOTOGRAPH_LOCATION_STAGES,
            name = CameraAcquisitorSystemCameraTargetHandler.ACTION_PHOTOGRAPH_LOCATION)
    public UInteger photographLocation(Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction,
        @ActionParameter(name = "targetLatitude", rawUnit = "degree") Double targetLatitude, @ActionParameter(
                                                                                                              name = "targetLongitude",
                                                                                                              rawUnit = "degree") Double targetLongitude,
        @ActionParameter(name = "timeStamp") String timeStamp) {
        LOGGER.log(Level.SEVERE, "" + targetLatitude + " " + targetLongitude + " " + timeStamp);
        return this.cameraTargetHandler.photographLocation(targetLatitude, targetLongitude, timeStamp,
            actionInstanceObjId, reportProgress, interaction);
    }

    @Action(description = "takes a photograph immediately",
            stepCount = CameraAcquisitorSystemCameraHandler.PHOTOGRAPH_NOW_STAGES)
    public UInteger photographNow(Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {
        return this.cameraHandler.photographNow(actionInstanceObjId, reportProgress, interaction);
    }

    /**
     * creates an AbsoluteDate object, which contains the current time in UTC
     *
     * @return AbsoluteDate with current time in UTC
     */
    public static AbsoluteDate getNow() {
        Instant instant = Instant.now();
        TimeScale utc = TimeScalesFactory.getUTC();
        LocalDateTime time = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));

        return new AbsoluteDate(time.getYear(), time.getMonthValue(), time.getDayOfMonth(), time.getHour(), time
            .getMinute(), time.getSecond(), utc);
    }

    double getAttitudeSaftyMarginSeconds() {
        return this.attitudeSaftyMarginMS / 1000.0;
    }
}
