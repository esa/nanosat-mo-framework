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
package esa.mo.ground.cameraacquisotorground;

import esa.mo.com.impl.util.EventCOMObject;
import esa.mo.com.impl.util.EventReceivedListener;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.ground.restservice.GroundTrack;
import esa.mo.ground.restservice.Pass;
import esa.mo.ground.restservice.PositionAndTime;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.apps.CameraAcquisitorSystemCameraTargetHandler;
import esa.mo.nmf.apps.CameraAcquisitorSystemMCAdapter;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import esa.mo.nmf.sdk.OrekitResources;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.ccsds.moims.mo.com.activitytracking.ActivityTrackingHelper;
import org.ccsds.moims.mo.com.activitytracking.structures.ActivityAcceptance;
import org.ccsds.moims.mo.com.activitytracking.structures.ActivityExecution;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.action.structures.ActionInstanceDetails;
import org.ccsds.moims.mo.mc.alert.structures.AlertEventDetails;
import org.ccsds.moims.mo.mc.structures.AttributeValue;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePairList;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormat;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.data.DataProvidersManager;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Camera Acquisition System ground application. Handles communication between user front-end and
 * space application as well as all computationally expensive calculations.
 * This Demo should be used with Camera Acquisition System provider.
 */
@RestController
@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class CameraAcquisitorGround {

    private static final Logger LOGGER = Logger.getLogger(CameraAcquisitorGround.class.getName());
    private static final String PROVIDER_CAMERA_APP = "App: exp495";

    private GroundMOAdapterImpl gma;
    private final OrbitHandler orbitHandler;

    // some useful time constants
    private final long MINUTE_IN_SECONDS = 60;
    private final long HOUR_IN_SECONDS = MINUTE_IN_SECONDS * 60;
    private final long DAY_IN_SECONDS = HOUR_IN_SECONDS * 24;
    private final long FIVE_DAYS_IN_SECONDS = DAY_IN_SECONDS * 5;
    private final long YEAR_IN_SECONDS = DAY_IN_SECONDS * 356;

    // default values for space application parameters
    private final long DEFAULT_WORST_CASE_ROTATION_TIME_MS = 20000;
    private final long DEFAULT_WORST_CASE_ROTATION_TIME_SEC = DEFAULT_WORST_CASE_ROTATION_TIME_MS / 1000;

    private final long MAX_SIM_RANGE = YEAR_IN_SECONDS;
    private final long DEFAULT_GROUND_TRACK_DURATION = DAY_IN_SECONDS;
    private final long DEFAULT_STEPSIZE = MINUTE_IN_SECONDS / 2;
    private final double DEFAULT_MAX_ANGLE = 45.0;

    // Tree set to keep track of scheduled photographs and make checking for collisions easier
    private final TreeSet<AbsoluteDate> schedule = new TreeSet<>();

    // Hashmap containing the currently running actions and their status (current stage)
    private final HashMap<Long, ActionReport[]> activeActions = new HashMap<>();

    // cached values
    private PositionAndTime[] cachedTrack = new PositionAndTime[0];
    private AtomicLong counter = new AtomicLong(0);
    private final int NUM_TRIES = 5; // number of timeslots that will maximally be calculated.

    private TLE cachedTLE;
    private Instant lastTLEUpdate;

    //
    @PostConstruct
    private void start() {
        LOGGER.log(Level.INFO, "---STARTUP---");
    }

    /**
     * class containing all parameters needed for the space application
     */
    private static class Parameter {
        private static final String GAIN_RED = "gainRed";
        private static final String GAIN_GREEN = "gainGreen";
        private static final String GAIN_BLUE = "gainBlue";
        private static final String EXPOSURE_TYPE = "exposureType";
        private static final String CUSTOM_EXPOSURE_TIME = "exposureTime";
        private static final String WORST_CASE_ROTATION_TIME_MS = "worstCaseRotationTimeMS";
        private static final String ATTITUDE_SAFETY_MARGIN_MS = "attitudeSafetyMarginMS";
        private static final String PICTURE_WIDTH = "pictureWidth";
        private static final String PICTURE_HEIGHT = "pictureHeight";
        private static final String PICTURE_TYPE = "pictureType";
    }

    /**
     * Inner class for storing action progress (like current stage etc.)
     */
    private static class ActionReport {
        // number of the stage
        public int stage;

        // true if the stage was successful, false if not
        public boolean success;

        // error message (if success is true, this should be empty)
        public String error;

        /**
         *
         * @param stage   stage this report refers too.
         * @param success if the stage was executed successfully.
         * @param error   the corresponding error message if it was not successful.
         */
        public ActionReport(int stage, boolean success, String error) {
            this.stage = stage;
            this.success = success;
            this.error = error;
        }

        @Override
        public String toString() {
            return "Stage: " + stage + ", success: " + success + (success ? "" : ", ERROR: " + error);
        }

    }

    // Coordinates of esoc in darmstadt
    GeodeticPoint esoc = new GeodeticPoint(49.869987, 8.622770, 0);

    public CameraAcquisitorGround(ApplicationArguments args) {
        if (args.getSourceArgs().length == 0) {
            LOGGER.log(Level.SEVERE, "No directoryURI given! exiting now");
            System.exit(1);
        }
        LOGGER.log(Level.INFO, "Parsing arguements...");

        URI directoryURI = new URI(args.getSourceArgs()[0]);
        LOGGER.log(Level.INFO, "directoryURI = " + directoryURI);

        LOGGER.log(Level.INFO, "Initialization...");
        this.lastTLEUpdate = Instant.MIN;

        //setup orekit
        LOGGER.log(Level.INFO, "Setup Orekit");
        DataProvidersManager manager = DataProvidersManager.getInstance();
        manager.addProvider(OrekitResources.getOrekitData());

        LOGGER.log(Level.INFO, "Setup providers");
        ProviderSummaryList providers;
        //maybe this initialization can occur inside the methods in case you want to check
        //availability again and retrieve providers after init
        LOGGER.log(Level.INFO, "Retrieving providers...");
        while (gma == null) {
            try {
                //System.out.println("Retrieving providers...");
                providers = GroundMOAdapterImpl.retrieveProvidersFromDirectory(directoryURI);
                LOGGER.log(Level.INFO, "Finished retrieving providers");
                for (ProviderSummary provider : providers) {
                    LOGGER.log(Level.INFO, "Name: {0} ", provider.getProviderId().getValue());

                    if (provider.getProviderId().getValue().equals(PROVIDER_CAMERA_APP)) {
                        gma = new GroundMOAdapterImpl(provider);
                        break;
                    } else {
                        LOGGER.log(Level.WARNING, "Camera Acqisitor App not found! Retrying...");
                    }
                }
            } catch (MALException | MalformedURLException | MALInteractionException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        try {
            Subscription subscription = HelperCOM.generateSubscriptionCOMEvent("ActivityTrackingListener",
                ActivityTrackingHelper.EXECUTION_OBJECT_TYPE);
            gma.getCOMServices().getEventService().addEventReceivedListener(subscription,
                new EventReceivedListenerAdapter());
            setInitialParameters();

            // get previous requests
            ArchiveQueryList archiveQueryList = new ArchiveQueryList();
            ArchiveQuery archiveQuery = new ArchiveQuery();
            archiveQuery.setDomain(null);
            archiveQuery.setNetwork(null);
            archiveQuery.setProvider(null);
            archiveQuery.setRelated(0L);
            archiveQuery.setSource(null);
            archiveQuery.setStartTime(null);
            archiveQuery.setEndTime(null);
            archiveQuery.setSortFieldName(null);
            archiveQueryList.add(archiveQuery);

            GetAllArchiveAdapter archiveAdapter = new GetAllArchiveAdapter();
            gma.getCOMServices().getArchiveService().getArchiveStub().query(true, new ObjectType(new UShort(0),
                new UShort(0), new UOctet((short) 0), new UShort(0)), archiveQueryList, null, archiveAdapter);

            LOGGER.log(Level.INFO, "Finished getting archive entries!");
        } catch (MALException | MALInteractionException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        orbitHandler = new OrbitHandler(getTLE());
    }

    /**
     * Exposes the Action status over the REST API returns the status of the action with the given
     * actionID.
     *
     * @param actionID the action id of the action to request status of.
     * @return the status of the action with the given actionID
     */
    @GetMapping("/getActionStatus")
    public ActionReport[] getActionStatus(@RequestParam(value = "actionID") long actionID) {
        return activeActions.get(actionID);
    }

    /**
     * Schedules a photograph of the given target location at a given time.
     *
     * @param latitude  latitude of the target location.
     * @param longitude longitude of the target location.
     * @param timeStamp time at which the photograph should be taken.
     * @return the actionID that was assigned to space application action.
     */
    @PostMapping("/schedulePhotographPosition")
    public Long schedulePhotographPosition(@RequestParam(value = "latitude") double latitude, @RequestParam(
                                                                                                            value = "longitude") double longitude,
        @RequestParam(value = "timeStamp") String timeStamp) {

        AbsoluteDate scheduleDate = new AbsoluteDate(timeStamp, TimeScalesFactory.getUTC());

        // check if timeslot is available
        if (checkTimeSlot(scheduleDate)) {
            try {
                // add new action to schedule
                schedule.add(scheduleDate);

                IdentifierList idList = new IdentifierList();
                idList.add(new Identifier(CameraAcquisitorSystemCameraTargetHandler.ACTION_PHOTOGRAPH_LOCATION));

                ObjectInstancePairList objIds = gma.getMCServices().getActionService().getActionStub().listDefinition(
                    idList);
                if (objIds == null) {
                    LOGGER.log(Level.SEVERE, "Action does not exist, please check if space application is running");
                }
                AttributeValueList arguments = new AttributeValueList();
                arguments.add(new AttributeValue((Attribute) HelperAttributes.javaType2Attribute(latitude)));
                arguments.add(new AttributeValue((Attribute) HelperAttributes.javaType2Attribute(longitude)));
                arguments.add(new AttributeValue((Attribute) HelperAttributes.javaType2Attribute(timeStamp)));

                Long actionID = gma.launchAction(objIds.get(0).getObjDefInstanceId(), arguments);
                if (actionID == null) {
                    LOGGER.log(Level.SEVERE, "Action ID == null!");
                } else {
                    LOGGER.log(Level.INFO, "new Action: {0}", actionID);
                    activeActions.put(actionID,
                        new ActionReport[CameraAcquisitorSystemCameraTargetHandler.PHOTOGRAPH_LOCATION_STAGES]);
                }
                return actionID;

            } catch (NMFException | MALException | MALInteractionException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        LOGGER.log(Level.INFO, "Timeslot not available!");
        return null;
    }

    /**
     * calculates multiple time slots at which a photograph with the given parameters can be taken.
     * Takes current schedule into account. Returns at most NUM_TRIES time slots. Returns less if
     * MAX_SIM_RANGE is exceeded before NUM_TRIES slots have been found.
     *
     * @param latitude  latitude of the target
     * @param longitude longitude of the target
     * @param maxAngle  maximum angle between target and satellite.
     * @param timeMode  the time at which the photograph should be taken (ANY, DAY, NIGHT)
     * @return list of possible time slots
     */
    @GetMapping("/photographTime")
    public LinkedList<String> getTimeOfPhotograph(@RequestParam(value = "latitude") double latitude, @RequestParam(
                                                                                                                   value = "longitude") double longitude,
        @RequestParam(value = "maxAngle", defaultValue = "" + DEFAULT_MAX_ANGLE) double maxAngle, @RequestParam(
                                                                                                                value = "timeMode",
                                                                                                                defaultValue = "ANY") OrbitHandler.TimeModeEnum timeMode) {
        // reset propagator state
        orbitHandler.reset();
        AbsoluteDate simTime = CameraAcquisitorSystemMCAdapter.getNow();
        AbsoluteDate simEnd = simTime.shiftedBy(MAX_SIM_RANGE);

        LinkedList<String> results = new LinkedList();
        // check next NUM_TRIES passes
        while (simTime.compareTo(simEnd) < 0 && results.size() <= NUM_TRIES) {

            Pass pass = orbitHandler.getPassTime(latitude, longitude, maxAngle, timeMode, simTime,
                DEFAULT_WORST_CASE_ROTATION_TIME_SEC, MAX_SIM_RANGE);
            simTime = pass.getOptimalTime();

            // if timeslot available add to possible results
            // simTime is null, if start time > end time, in that case abort.
            if (simTime != null && checkTimeSlot(simTime)) {
                results.add(pass.getResultTime());
            } else if (simTime == null) {
                break;
            }
        }
        if (results.isEmpty()) {
            return null;
        }
        return results;
    }

    /**
     * calculates the ground track for the satellite and returns a list of locations and time stamps.
     *
     * @param duration how far into the future the ground track should be calculated in seconds
     * @param stepsize the amount of time between entries in the resulting list in seconds
     * @return
     */
    @GetMapping("/groundTrack")
    public GroundTrack groundTrack(@RequestParam(value = "duration", defaultValue = "" +
        DEFAULT_GROUND_TRACK_DURATION) long duration, @RequestParam(value = "stepsize", defaultValue = "" +
            DEFAULT_STEPSIZE) long stepsize) {
        AbsoluteDate now = CameraAcquisitorSystemMCAdapter.getNow();
        AbsoluteDate endDate = now.shiftedBy(duration);

        //cache for one hour.
        if (cachedTrack.length > 1 && (now.durationFrom(cachedTrack[0].orekitDate) < HOUR_IN_SECONDS || endDate
            .durationFrom(cachedTrack[cachedTrack.length - 1].orekitDate) < HOUR_IN_SECONDS)) {
            return new GroundTrack(counter.incrementAndGet(), cachedTrack);
        }

        PositionAndTime[] track = orbitHandler.getGroundTrack(now, endDate, stepsize);
        cachedTrack = track;
        return new GroundTrack(counter.incrementAndGet(), track);
    }

    /**
     * Requests the current TLE if the cached TLE is older than 1 hour and caches it. Than Returns the
     * cached TLE
     *
     * @return Cached TLE
     */
    private TLE getTLE() {
        if (lastTLEUpdate.until(Instant.now(), ChronoUnit.HOURS) < 1) {
            return cachedTLE;
        } else {
            return loadTLE();
        }
    }

    /**
     * loads the current TLE of OPS-SAT from celestark.com
     *
     * @return the current LTE or NULL if the site is not reachable
     */
    private TLE loadTLE() {
        try {
            URL url = new URL("https://celestrak.com/NORAD/elements/gp.php?CATNR=44878"); //opsat TLE
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String line0 = in.readLine();// only needed to remove first line
            String line1 = in.readLine();
            String line2 = in.readLine();

            in.close();
            lastTLEUpdate = Instant.now();
            cachedTLE = new TLE(line1, line2);
            return cachedTLE;
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "loadTLE {0}", ex.getMessage());
        }
        return null;
    }

    /**
     * checks if time slot is available
     *
     * @param scheduleDate slot to check
     * @return
     */
    private boolean checkTimeSlot(AbsoluteDate scheduleDate) {
        AbsoluteDate before = schedule.floor(scheduleDate);
        AbsoluteDate after = schedule.ceiling(scheduleDate);
        System.out.println("before " + before);
        System.out.println("after " + after);
        return (before == null || scheduleDate.durationFrom(before) > DEFAULT_WORST_CASE_ROTATION_TIME_SEC &&
            scheduleDate.compareTo(before) != 0) && (after == null || after.durationFrom(scheduleDate) >
                DEFAULT_WORST_CASE_ROTATION_TIME_SEC && scheduleDate.compareTo(after) != 0);
    }

    /**
     * sets the default parameters for the space application
     */
    private void setInitialParameters() {
        gma.setParameter(Parameter.ATTITUDE_SAFETY_MARGIN_MS, 1000000L);
        gma.setParameter(Parameter.CUSTOM_EXPOSURE_TIME, 1.0f);
        gma.setParameter(Parameter.EXPOSURE_TYPE, (byte) 0);//CUSTOM = 0, AUTOMATIC = 1
        gma.setParameter(Parameter.WORST_CASE_ROTATION_TIME_MS, DEFAULT_WORST_CASE_ROTATION_TIME_MS);

        gma.setParameter(Parameter.GAIN_RED, 1.0f);
        gma.setParameter(Parameter.GAIN_GREEN, 1.0f);
        gma.setParameter(Parameter.GAIN_BLUE, 1.0f);
        gma.setParameter(Parameter.PICTURE_WIDTH, 2048);
        gma.setParameter(Parameter.PICTURE_HEIGHT, 1944);
        gma.setParameter(Parameter.PICTURE_TYPE, PictureFormat.PNG.getOrdinal());
    }

    private void updateEvent(long actionID, int type, Object body) {
        if (type == ActivityAcceptance.TYPE_SHORT_FORM) {
            System.out.println("ActivityAcceptance");
            ActivityAcceptance event = (ActivityAcceptance) body;
        } else if (type == ActivityExecution.TYPE_SHORT_FORM) {
            System.out.println("ActivityExecution");
            ActivityExecution event = (ActivityExecution) body;
            int executionStage = (int) event.getExecutionStage().getValue();
            int stageCount = (int) event.getStageCount().getValue();
            boolean success = event.getSuccess();

            // update status of action
            if (activeActions.containsKey(actionID)) {
                synchronized (activeActions) {
                    // minus 2 because stage count starts at 1 and an extra stage (for message received) is added by the Framework
                    if (executionStage > 1 && executionStage < 6)
                        activeActions.get(actionID)[executionStage - 1] = new ActionReport(executionStage, success, "");
                    // if some other stage is successful, the command has also been transmitted to the satellite!
                    if (executionStage - 1 > 0 && executionStage != 7 && success) {
                        activeActions.get(actionID)[0] = new ActionReport(1, true, "");
                    }
                }
                LOGGER.log(Level.INFO, "Action State: {0}", Arrays.toString(activeActions.get(actionID)));
            }
            if (success) {
                LOGGER.log(Level.INFO, "Action Update: ID={0}, Execution Stage={1}", new Object[]{actionID,
                                                                                                  executionStage});
            } else {
                LOGGER.log(Level.WARNING, "Action Unsuccessful: ID={0}, Execution Stage={1}", new Object[]{actionID,
                                                                                                           executionStage});
            }

        } else if (type == AlertEventDetails.TYPE_SHORT_FORM) {
            System.out.println("AlertEventDetails");
            AlertEventDetails event = (AlertEventDetails) body;

            AttributeValueList attValues = event.getArgumentValues();

            StringBuilder messageToDisplay = new StringBuilder("ID: " + actionID + " ");

            if (attValues != null) {
                if (attValues.size() == 1) {
                    messageToDisplay.append(attValues.get(0).getValue().toString());
                }
                if (attValues.size() > 1) {
                    for (int i = 0; i < attValues.size(); i++) {
                        AttributeValue attValue = attValues.get(i);
                        messageToDisplay.append("[").append(i).append("] ").append(attValue.getValue().toString())
                            .append("\n");
                    }
                }
            }
            LOGGER.log(Level.WARNING, messageToDisplay.toString());
        }
    }

    /**
     * class for handling the receiving of messages from the space Application
     */
    private class EventReceivedListenerAdapter extends EventReceivedListener {

        @Override
        public void onDataReceived(EventCOMObject eventCOMObject) {

            if (eventCOMObject.getBody() == null) {
                return;
            }

            long actionID = eventCOMObject.getSource().getKey().getInstId();

            int type = eventCOMObject.getBody().getTypeShortForm();
            updateEvent(actionID, type, eventCOMObject.getBody());
        }
    }

    private class GetAllArchiveAdapter extends ArchiveAdapter {

        @Override
        public void queryResponseReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain,
            ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {
            if (objBodies != null) {
                int i = 0;
                for (Object objBody : objBodies) {
                    if (objBody instanceof ActionInstanceDetails) {
                        ActionInstanceDetails instance = ((ActionInstanceDetails) objBody);
                        try {
                            IdentifierList idList = new IdentifierList();
                            idList.add(new Identifier(
                                CameraAcquisitorSystemCameraTargetHandler.ACTION_PHOTOGRAPH_LOCATION));

                            ObjectInstancePairList objIds = gma.getMCServices().getActionService().getActionStub()
                                .listDefinition(idList);
                            if (objIds.size() > 0 && objIds.get(0).getObjDefInstanceId().longValue() == instance
                                .getDefInstId().longValue() && instance.getArgumentValues().size() == 3) {

                                String timestamp = instance.getArgumentValues().get(2).getValue().toString();
                                LOGGER.log(Level.INFO, "recovered action: " + timestamp + "\tID: " + objDetails.get(i)
                                    .getInstId());

                                activeActions.put(objDetails.get(i).getInstId(),
                                    new ActionReport[CameraAcquisitorSystemCameraTargetHandler.PHOTOGRAPH_LOCATION_STAGES]);

                                AbsoluteDate scheduleDate = new AbsoluteDate(timestamp, TimeScalesFactory.getUTC());

                                schedule.add(scheduleDate);
                            }
                        } catch (MALInteractionException | MALException ex) {
                            LOGGER.log(Level.SEVERE, ex.getMessage());
                        }
                    } else if (objBody instanceof ActivityAcceptance) {
                        ActivityAcceptance instance = ((ActivityAcceptance) objBody);
                        updateEvent(objDetails.get(i).getDetails().getSource().getKey().getInstId(), instance
                            .getTypeShortForm(), objBody);
                    } else if (objBody instanceof ActivityExecution) {
                        ActivityExecution instance = ((ActivityExecution) objBody);
                        updateEvent(objDetails.get(i).getDetails().getSource().getKey().getInstId(), instance
                            .getTypeShortForm(), objBody);
                    }
                    i++;
                }
            }
        }

        @Override
        public void queryUpdateReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain,
            ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {
            queryResponseReceived(msgHeader, objType, domain, objDetails, objBodies, qosProperties);
        }

    }

}
