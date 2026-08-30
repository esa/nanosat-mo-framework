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
package esa.mo.nmf.provider;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.InvalidArgumentException;
import org.ccsds.moims.mo.mal.InternalException;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.UnknownException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.platform.structures.NearbyPosition;
import org.ccsds.moims.mo.platform.structures.NearbyPositionList;
import org.ccsds.moims.mo.platform.structures.Position;
import org.ccsds.moims.mo.platform.structures.PositionExtraDetails;


/**
 * Representing the Geofences set on a satellite. 
 */
public class Geofence {

    private static final Logger LOGGER = Logger.getLogger(MCRaspberryPiAdapter.class.getName());

    private final NanoSatMOSupervisorRaspberryPiImpl supervisor;

    private ArrayList<GeofenceData> geofences = new ArrayList<GeofenceData>();

    public Geofence(NanoSatMOSupervisorRaspberryPiImpl supervisor) {
        this.supervisor = supervisor;
    }

    /**
     * Add a new Geofence.
     * 
     * @param geofenceRaw Geofence raw data. App id can be long value greater than 0. Latitude and Longite are accepting 6 decimal digits.
     *                    Range cannot be less than 1. When startWhenInsideRange is true, the App is started when entering the range, 
     *                    and stopped when leaving. Inverted when set false.
     *                    Format: ADD:[Long appId]:[String latitude]:[String longitude]:[Float range]:[Boolean startWhenInsideRange]
     *                    Example: ADD:2:40.123456:50.123456:100.5:true
     */
    public void add(String[] geofenceRaw) {
        GeofenceData geofence = GeofenceData.createInstance(geofenceRaw);

        if (geofence != null) {
            this.geofences.add(geofence);
            // this.addNearbyPosition(geofence);
        }
    }

    /**
     * Remove a certain Geofence entry.
     * 
     * @param geofenceRaw Geofence raw data. App id can be long value greater than 0. Latitude and Longite are accepting 6 decimal digits.
     *                    Range cannot be less than 1. When startWhenInsideRange is true, the App is started when entering the range, 
     *                    and stopped when leaving. Inverted when set false.
     *                    Format: ADD:[Long appId]:[String latitude]:[String longitude]:[Float range]:[Boolean startWhenInsideRange]
     *                    Example: ADD:2:40.123456:50.123456:100.5:true
     */
    public void remove(String[] geofenceRaw) {
        this.geofences.forEach(geofence -> {
            if (geofence.equals(geofenceRaw)) {
                // removeNearbyPosition(geofence);
                geofences.remove(geofence);
            }
        });
    }

    /**
     * Remove all Geofences for a certain App.
     * 
     * @param geofenceRaw Geofence raw data. 
     *                    Format: REMOVEAPP:[Long appId]
     *                    Example: REMOVEAPP:2
     */
    public void removeApp(String[] geofenceRaw) {

        this.geofences.forEach(geofence -> {
            if (geofence.getAppId() == Long.parseLong(geofenceRaw[1])) {
                // removeNearbyPosition(geofence);
                geofences.remove(geofence);
            }
        });
    }

    /**
     * Remove all Geofences set for this instance.
     * 
     */
    public void removeAll() {
        this.geofences.forEach(geofence -> {
            // removeNearbyPosition(geofence);
        });
        this.geofences = new ArrayList<GeofenceData>();
    }

    private void addNearbyPosition(GeofenceData geofence) {
        try {
            NearbyPositionList nearbyPositionDefinitionList = new NearbyPositionList();

            Identifier name = new Identifier(String.valueOf(geofence.appId)); // TODO: add random chars to make name unique
            String description = String.valueOf(geofence.appId);
            Float distanceBoundary = geofence.range * 1000;
            PositionExtraDetails positionExtraDetails = new PositionExtraDetails();
            Position position = new Position(
                Float.parseFloat(geofence.lat),
                Float.parseFloat(geofence.lon),
                0f,
                positionExtraDetails
            );
            NearbyPosition newNearbyPosition = new NearbyPosition(name, description, distanceBoundary, position);
            nearbyPositionDefinitionList.add(newNearbyPosition);

            LongList objInstIds = this.supervisor.getPlatformServices().getGPSService().addNearbyPosition(nearbyPositionDefinitionList);
            
            geofence.setNearbyPositionObjInstIds(objInstIds);
            this.geofences.add(geofence);

            LOGGER.log(Level.INFO, "Created new Geofence and successfully added new NearbyPosition!"); 

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error creating new NearbyPosition: " + ex.getMessage()); 
        }
    }

    private void removeNearbyPosition(GeofenceData geofence) {
        try {
            if (geofence.getNearbyPositionObjInstIds() == null) {
                this.geofences.remove(geofence);
                LOGGER.log(Level.INFO, "Removed Geofence, no NearbyPositions found"); 
                return;
            }

            this.supervisor.getPlatformServices().getGPSService().removeNearbyPosition(geofence.getNearbyPositionObjInstIds());

            this.geofences.remove(geofence);
            LOGGER.log(Level.INFO, "Removed Geofence and NearbyPosition!"); 

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error removing NearbyPosition: " + ex.getMessage()); 
        }
    }

    public void startApp(Long appId) {
        try {
            LongList ids = new LongList();
            ids.add(appId);
            this.supervisor.getAppsLauncherService().runApp(null, null);
        } catch (MALInteractionException | MALException | UnknownException
                | InvalidArgumentException | InternalException ex) {            
            LOGGER.log(Level.SEVERE, "Failed to start App: " + ex.getMessage());
        }
    }

    public void stopApp(Long appId) {
        try {
            // this.supervisor.getAppsLauncherService().stopApp(null, null);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to stop App: " + ex.getMessage());
        }

    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.geofences.size() == 0) {
            return null;
        }
        this.geofences.forEach(geofence -> {
            stringBuilder.append(geofence.toString());
        });
        return stringBuilder.toString();
    }


    /**
     * Representing a Geofence Data Object.
     */
    private static class GeofenceData {
        private long appId;
        private String lat;
        private String lon;
        private float range;
        private boolean startWhenInsideRange;
        private LongList nearbyPositionObjInstIds = null;

        private GeofenceData(String[] geofenceRaw) {
            this.appId = Long.parseLong(geofenceRaw[1]);
            this.lat = geofenceRaw[2];
            this.lon = geofenceRaw[3];
            this.range = Float.parseFloat(geofenceRaw[4]);
            this.startWhenInsideRange = Boolean.parseBoolean(geofenceRaw[5]);
        }

        /**
         * Factory method to create a GeofenceData instance if the provided raw data is valid.
         * 
         * @param geofenceRaw Geofence raw data. App ID is long value greater than 0 Latitude and Longite are accepting 6 decimal digits.
         *                    Range cannot be less than 1. When startWhenInsideRange is true, the App is started when entering the range, 
         *                    and stopped when leaving. Inverted when set false.
         *                    Format: ADD:[long appId]:[String latitude]:[String longitude]:[Float range]:[Boolean startWhenInsideRange]
         *                    Example: ADD:2:40.123456:50.123456:100.5:true
         * @return GeofenceData instance if data is valid, null if data is invalid.
         */
        public static GeofenceData createInstance(String[] geofenceRaw) {
            if (isValid(geofenceRaw)) {
                return new GeofenceData(geofenceRaw);
            } else {
                LOGGER.log(Level.SEVERE, "Failed to create Geofence. Invalid Data: " + geofenceRaw.toString());
                return null;
            }
        }

        public long getAppId() {
            return this.appId;
        }

        public LongList getNearbyPositionObjInstIds() {
            return this.nearbyPositionObjInstIds;
        }

        public void setNearbyPositionObjInstIds(LongList objInstIds) {
            this.nearbyPositionObjInstIds = objInstIds;
        }

        @Override
        public String toString() {
            return new StringBuilder()
                .append(this.appId).append(':')
                .append(this.lat).append(':')
                .append(this.lon).append(':')
                .append(this.range).append(':')
                .append(this.startWhenInsideRange).append(';').toString();
        }

        /**
         * Check if the provided raw Geofence data meets the requirements: 
         * 
         *      geofenceRaw[0]: action - ignored for validity check.
         *      geofenceRaw[1]: app ID - long value greater than 0.
         *      geofenceRaw[2]: latitude - two numbers followed by six numbers separated by a dot.
         *      geofenceRaw[3]: longitude - two numbers followed by six numbers separated by a dot.
         *      geofenceRaw[4]: range - float value not less than 1.0.
         *      geofenceRaw[5]: startWhenInsideRange - true or false.
         * 
         * @param geofenceRaw Geofence raw data. App id can be long value greater than 0. Latitude and Longite are accepting 6 decimal digits.
         *                    Range cannot be less than 1. When startWhenInsideRange is true, the App is started when entering the range, 
         *                    and stopped when leaving. Inverted when set false.
         *                    Format: ADD:[Long appId]:[String latitude]:[String longitude]:[Float range]:[Boolean startWhenInsideRange]
         *                    Example: ADD:2:40.123456:50.123456:100.5:true
         * @return true if valid, false when invalid
         */
        public static boolean isValid(String[] geofenceRaw) {
            try {
                if (Long.parseLong(geofenceRaw[1]) < 1) {
                    LOGGER.log(Level.INFO, "Invalid Geofence Data: app id");
                    return false;
                } else if (!geofenceRaw[2]
                        .matches("^(\\+|-)?(?:90(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-8][0-9])(?:(?:\\.[0-9]{1,6})?))$")) {
                    LOGGER.log(Level.INFO, "Invalid Geofence Data: latitude");
                    return false;
                } else if (!geofenceRaw[3].matches(
                        "^(\\+|-)?(?:180(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])(?:(?:\\.[0-9]{1,6})?))$")) {
                    LOGGER.log(Level.INFO, "Invalid Geofence Data: longitude");
                    return false;
                } else if (Float.parseFloat(geofenceRaw[4]) < 1.0) {
                    LOGGER.log(Level.INFO, "Invalid Geofence Data: range");
                    return false;
                } else if (!geofenceRaw[5].matches("([Tt][Rr][Uu][Ee]|[Ff][Aa][Ll][Ss][Ee])$")) {
                    LOGGER.log(Level.INFO, "Invalid Geofence Data: startWhenInsideRange");
                    return false;
                } else {
                    return true;
                }
            } catch (Exception ex) {
                LOGGER.log(Level.INFO, "Invalid Geofence Data: " + ex.getMessage());
                return false;
            }
        }

        /**
         * Compares this object to data provided in raw format.
         * 
         * @param geofenceRaw Geofence raw data.
         *                    Format: ADD:[Long appId]:[String latitude]:[String longitude]:[Float range]:[Boolean startWhenInsideRange]
         * @return true if equal, false if not equal
         */
        public boolean equals(String[] geofenceRaw) {
            if (this.appId != Long.parseLong(geofenceRaw[1])) {
                return false;
            } else if (this.lat != geofenceRaw[2]) {
                return false;
            } else if (this.lon != geofenceRaw[3]) {
                return false;
            } else if (this.range != Float.parseFloat(geofenceRaw[4])) {
                return false;
            } else if (this.startWhenInsideRange != Boolean.parseBoolean(geofenceRaw[5])) {
                return false;
            } else {
                return true;
            }
        }
    }

}
