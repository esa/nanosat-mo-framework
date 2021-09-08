package esa.mo.apps.autonomy.planner.converter.mp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ccsds.moims.mo.mal.structures.Time;
import esa.esoc.hso.osa.apsi.application.planner.PlanningProblem;
import esa.esoc.hso.osa.apsi.ddlparser.DDLCompiler;
import esa.esoc.hso.osa.apsi.trf.framework.domain.defaultdomain.Domain;
import esa.esoc.hso.osa.apsi.trf.framework.time.stp.TPBound;
import esa.esoc.hso.osa.apsi.trf.kernel.component.Value;
import esa.esoc.hso.osa.apsi.trf.kernel.domain.DomainTimeline;
import esa.esoc.hso.osa.apsi.trf.kernel.time.TemporalModule;
import esa.esoc.hso.osa.apsi.trf.kernel.time.TimeInstant;
import esa.mo.apps.autonomy.util.FileUtils;
import esa.mo.apps.autonomy.util.CoordinateConverter;
import esa.mo.apps.autonomy.vw.VisibilityWindowItem;

/**
 * MPConverter is used convert to and from APSI planning data structures
 */
public class MPConverter {

    private static final Logger LOGGER = Logger.getLogger(MPConverter.class.getName());

    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private static final String DURATION_REGEX = "(\\d\\d)\\.(\\d\\d\\d)\\.(\\d\\d)\\.(\\d\\d)\\.(\\d\\d)\\.(\\d\\d\\d)";

    private static final String LINE_SEPARATOR = System.lineSeparator();

    private static final String DEFAULT_PLANNING_PATH = "planning";

    private static final String DEFAULT_DDL_FILE = "apsi" + File.separator + "OPS_SAT_Domain_edt.ddl";

    private static File planningDirectory = null;

    private static String ddlFile = null;

    private static String minLockingDuration = "00.000.00.00.20.000";
    private static String maxLockingDuration = "00.000.00.00.20.000";
    private static String minLockedDuration = "00.000.00.00.10.000";
    private static String maxLockedDuration = "00.000.00.00.10.000";
    private static String minMakePicDuration = "00.000.00.00.04.000";
    private static String maxMakePicDuration = "00.000.00.00.04.000";

    private static long lockingDuration = 10000l;

    private MPConverter() {}

    /**
     * Creates planning folder, reads configuration properties and checks for required DDL file
     */
    public static void init() {
        // Read from properties file
        String planningFilesPath = System.getProperty("autonomy.app.planning", DEFAULT_PLANNING_PATH);
        planningDirectory = FileUtils.createDirectory(planningFilesPath, "APSI planning files", DEFAULT_PLANNING_PATH);

        ddlFile = System.getProperty("apsi.ddl.file", DEFAULT_DDL_FILE);

        minLockingDuration = System.getProperty("apsi.locking.duration.min", minLockingDuration);
        maxLockingDuration = System.getProperty("apsi.locking.duration.max", maxLockingDuration);
        minLockedDuration = System.getProperty("apsi.locked.duration.min", minLockedDuration);
        maxLockedDuration = System.getProperty("apsi.locked.duration.max", maxLockedDuration);
        minMakePicDuration = System.getProperty("apsi.makepic.duration.min", minMakePicDuration);
        maxMakePicDuration = System.getProperty("apsi.makepic.duration.max", maxMakePicDuration);

        lockingDuration = getAPSIDuration(maxLockingDuration);

        try {
            DDLCompiler compiler = new DDLCompiler(ddlFile);
            compiler.compileDomain();
            LOGGER.info(String.format("APSI DDL file loaded from %s", new File(ddlFile).getAbsolutePath()));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
            System.exit(1);
        }
    }

    public static PlanningProblem convert(Time release, Time deadline, List<GoalItem> goals, List<VisibilityWindowItem> visibilityWindows, List<TimelineItem> plannedTimeline, String filePrefix) throws Exception {
        String pdlString = createPDL(release, deadline, goals, visibilityWindows, plannedTimeline);

        String problemPath = writePDL(filePrefix, pdlString);

        DDLCompiler compiler = new DDLCompiler(problemPath);

        return new PlanningProblem(compiler.compileDomain(), compiler.importProblem());
    }

    public static List<TimelineItem> convert(DomainTimeline domainTimeline, String filePrefix) throws IOException {
        writePlan(filePrefix, domainTimeline.toString());

        List<TimelineItem> timelineItems = new ArrayList<>();

        TemporalModule temporalModule = ((Domain)domainTimeline.getReasoner()).getTemporalModule();

        // Timelines
        // MT   [0] TakePicture(10,51,id_0,T,T)
        // GPS  [1]
        // ATT  [2] Locking(10,51) -> setAttitude
        // CAM  [3] MakePic(10,51,id_0)-> takePicture
        // CLAS [4]
        // MEM  [5]

        LinkedList<?> transitions = domainTimeline.getTransitions();
        LinkedList<?> tuples = domainTimeline.getTuples();

        for (int i = 0; i < transitions.size(); i++) {
            Value[] values = (Value[]) tuples.get(i);

            TimeInstant instant = (TimeInstant)transitions.get(i);

            TPBound bound = (TPBound)temporalModule.createElementQuery("TIME_POINT_BOUND");
            bound.setTimeInstant(instant);
            temporalModule.askQuery(bound);

            for (int j = 0; j < values.length; j++) {
                Value value = values[j];
                if (value == null) continue;

                String action = value.toString();

                // Parse activity + arguments
                String regex = "(\\w+)\\((\\w+(?:,\\w+)*)*\\)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(action);

                if (matcher.matches()) {
                    String activity = matcher.group(1);
                    String[] arguments = matcher.group(2) != null ? matcher.group(2).split(",") : null;
                    TimelineItem timelineItem = new TimelineItem(activity, bound.getLowerBound(), bound.getUpperBound(), arguments);
                    timelineItems.add(timelineItem);
                }
            }
        }

        return timelineItems;
    }


    public static String formatISO8601(Time timestamp) {
        return formatISO8601(timestamp.getValue());
    }

    public static String formatISO8601(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(date);
    }

    public static Time getISO8601Time(String iso8601) throws ParseException {
        return new Time(getISO8601Timestamp(iso8601));
    }

    public static long getISO8601Timestamp(String iso8601) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.parse(iso8601).getTime();
    }

    public static long getAPSIDuration(String duration) {
        Pattern pattern = Pattern.compile(DURATION_REGEX);
        Matcher matcher = pattern.matcher(duration);

        if (matcher.matches()) {
            int years = Integer.parseInt(matcher.group(1));
            int days = Integer.parseInt(matcher.group(2));
            int hours = Integer.parseInt(matcher.group(3));
            int minutes = Integer.parseInt(matcher.group(4));
            int seconds = Integer.parseInt(matcher.group(5));
            int milliseconds = Integer.parseInt(matcher.group(6));

            return years * 365 * 24 * 60 * 60 * 1000l +
                   days * 24 * 60 * 60 * 1000l +
                   hours * 60 * 60 * 1000l +
                   minutes * 60 * 1000l +
                   seconds * 1000l +
                   milliseconds;

        } else {
            LOGGER.warning("Cannot parse " + maxLockedDuration);
        }
        return 0l;
    }

    private static String createPDL(Time release, Time deadline, List<GoalItem> goals, List<VisibilityWindowItem> visibilityWindows, List<TimelineItem> plannedTimeline) {
        String formattedRelease = formatISO8601(release);
        String formattedDeadline = formatISO8601(deadline);

        // Release and deadline windows
        String releaseWindow = "RELEASE [" + formattedRelease + "," + formattedRelease + "]";
        String deadlineWindow = "DEADLINE [" + formattedDeadline + "," + formattedDeadline + "]";

        StringBuilder stringBuilder = new StringBuilder();

        appendStaticData(stringBuilder, releaseWindow, deadlineWindow);
        stringBuilder.append(LINE_SEPARATOR);
        appendPlannedActivities(stringBuilder, plannedTimeline);
        stringBuilder.append(LINE_SEPARATOR);
        appendVisibilityWindows(stringBuilder, visibilityWindows);
        stringBuilder.append(LINE_SEPARATOR);
        stringBuilder.append(
            "    init1 BEFORE vw0;" + LINE_SEPARATOR +
            "    vw0 BEFORE end1;" + LINE_SEPARATOR
        );
        stringBuilder.append(LINE_SEPARATOR);
        appendGoals(stringBuilder, goals);
        stringBuilder.append("}");

        // Log created PDL for easier debugging
        // LOGGER.info(stringBuilder.toString());

        return stringBuilder.toString();
    }

    private static void appendStaticData(StringBuilder stringBuilder, String releaseWindow, String deadlineWindow) {
        stringBuilder.append(
            "INCLUDE \"" + ddlFile + "\";" + LINE_SEPARATOR +
            LINE_SEPARATOR +
            "PROBLEM Take_Picture (DOMAIN Observe_Area) {" + LINE_SEPARATOR +
            "    INIT_VALUE MEM.tl = 132.0;" + LINE_SEPARATOR +
            "    init0 <fact> MT.tl.MTIdle();" + LINE_SEPARATOR +
            "    init1 <fact> GPS.tl.NotVisible();" + LINE_SEPARATOR +
            "    init2 <fact> ATT.tl.Unlocked();" + LINE_SEPARATOR +
            "    init3 <fact> CAM.tl.CamIdle();" + LINE_SEPARATOR +
            "    init4 <fact> CLAS.tl.ClassIdle();" + LINE_SEPARATOR +
            "    init0 " + releaseWindow + ";" + LINE_SEPARATOR +
            "    init1 " + releaseWindow + ";" + LINE_SEPARATOR +
            "    init2 " + releaseWindow + ";" + LINE_SEPARATOR +
            "    init3 " + releaseWindow + ";" + LINE_SEPARATOR +
            "    init4 " + releaseWindow + ";" + LINE_SEPARATOR +
            "    end0 <fact> MT.tl.MTIdle();" + LINE_SEPARATOR +
            "    end1 <fact> GPS.tl.NotVisible();" + LINE_SEPARATOR +
            "    end2 <fact> ATT.tl.Unlocked();" + LINE_SEPARATOR +
            "    end3 <fact> CAM.tl.CamIdle();" + LINE_SEPARATOR +
            "    end4 <fact> CLAS.tl.ClassIdle();" + LINE_SEPARATOR +
            "    end0 " + deadlineWindow + ";" + LINE_SEPARATOR +
            "    end1 " + deadlineWindow + ";" + LINE_SEPARATOR +
            "    end2 " + deadlineWindow + ";" + LINE_SEPARATOR +
            "    end3 " + deadlineWindow + ";" + LINE_SEPARATOR +
            "    end4 " + deadlineWindow + ";" + LINE_SEPARATOR +
            "    init0 BEFORE end0;" + LINE_SEPARATOR +
            "    init1 BEFORE end1;" + LINE_SEPARATOR +
            "    init2 BEFORE end2;" + LINE_SEPARATOR +
            "    init3 BEFORE end3;" + LINE_SEPARATOR +
            "    init4 BEFORE end4;" + LINE_SEPARATOR
        );
    }

    private static void appendPlannedActivities(StringBuilder stringBuilder, List<TimelineItem> plannedTimeline) {
        for (TimelineItem plannedItem : plannedTimeline) {
            String activityId = plannedItem.getId();
            switch (plannedItem.getName()) {
                case "Locking": {
                    String lockingStart = formatISO8601(plannedItem.getLowerBound());
                    String lockingEnd = formatISO8601(plannedItem.getUpperBound());
                    String lockedStart = formatISO8601(plannedItem.getLowerBound() + lockingDuration);
                    String lockedEnd = formatISO8601(plannedItem.getUpperBound() + lockingDuration);
                    String arguments = String.join(",", plannedItem.getArguments());

                    stringBuilder.append(String.format("    activity_%s_1 <fact> ATT.tl.Locking(%s);", activityId, arguments));
                    stringBuilder.append(LINE_SEPARATOR);
                    stringBuilder.append(String.format("    activity_%s_1 RELEASE [%s,%s];", activityId, lockingStart, lockingEnd));
                    stringBuilder.append(LINE_SEPARATOR);
                    stringBuilder.append(String.format("    activity_%s_1 DURATION [%s,%s];", activityId, minLockingDuration, maxLockingDuration));
                    stringBuilder.append(LINE_SEPARATOR);
                    stringBuilder.append(String.format("    activity_%s_2 <fact> ATT.tl.Locked(%s);", activityId, arguments));
                    stringBuilder.append(LINE_SEPARATOR);
                    stringBuilder.append(String.format("    activity_%s_2 RELEASE [%s,%s];", activityId, lockedStart, lockedEnd));
                    stringBuilder.append(LINE_SEPARATOR);
                    stringBuilder.append(String.format("    activity_%s_2 DURATION [%s,%s];", activityId, minLockedDuration, maxLockedDuration));
                    stringBuilder.append(LINE_SEPARATOR);
                    break;
                }
                case "MakePic": {
                    String makePicStart = formatISO8601(plannedItem.getLowerBound());
                    String makePicEnd = formatISO8601(plannedItem.getUpperBound());
                    String arguments = String.join(",", plannedItem.getArguments());

                    stringBuilder.append(String.format("    activity_%s_1 <fact> CAM.tl.MakePic(%s);", activityId, arguments));
                    stringBuilder.append(LINE_SEPARATOR);
                    stringBuilder.append(String.format("    activity_%s_1 RELEASE [%s,%s];", activityId, makePicStart, makePicEnd));
                    stringBuilder.append(LINE_SEPARATOR);
                    stringBuilder.append(String.format("    activity_%s_1 DURATION [%s,%s];", activityId, minMakePicDuration, maxMakePicDuration));
                    stringBuilder.append(LINE_SEPARATOR);
                    break;
                }
                default:
                    break;
            }
        }
    }

    private static void appendVisibilityWindows(StringBuilder stringBuilder, List<VisibilityWindowItem> visibilityWindows) {
        for (int index = 0; index < visibilityWindows.size(); index++) {
            VisibilityWindowItem visibilityWindow = visibilityWindows.get(index);
            String latitude = CoordinateConverter.toPlannerCoordinate(Double.toString(visibilityWindow.getLatitude()));
            String longitude = CoordinateConverter.toPlannerCoordinate(Double.toString(visibilityWindow.getLongitude()));
            String visibilityPosition = String.format("%s,%s", latitude, longitude);
            String visibilityWindowStart = formatISO8601(visibilityWindow.getStart());
            String visibilityWindowEnd = formatISO8601(visibilityWindow.getEnd());
            stringBuilder.append(String.format("    vw%d <fact> STATIC GPS.tl.Visible(%s) AT [%s,%s];", index, visibilityPosition, visibilityWindowStart, visibilityWindowEnd));
            stringBuilder.append(LINE_SEPARATOR);
        }
    }

    private static void appendGoals(StringBuilder stringBuilder, List<GoalItem> goals) {
        for (int index = 0; index < goals.size(); index++) {
            GoalItem goal = goals.get(index);
            String name = goal.getName();
            String arguments = String.join(",", goal.getArguments());
            stringBuilder.append(String.format("    tp%d <goal> MT.tl.%s(%s);", index, name, arguments));
            stringBuilder.append(LINE_SEPARATOR);
        }
    }

    private static String writePDL(String filePrefix, String pdlString) throws IOException {
        String pdlPath = writeToTempFile(pdlString, filePrefix, "_problem.pdl");
        LOGGER.info("Created PDL file at " + pdlPath);
        return pdlPath;
    }

    private static String writePlan(String filePrefix, String planString) throws IOException {
        String planPath = writeToTempFile(planString, filePrefix, "_plan.txt");
        LOGGER.info("Created Plan file at " + planPath);
        return planPath;
    }

    private static String writeToTempFile(String string, String prefix, String suffix) throws IOException {
        int iteration = 1;
        File file = new File(planningDirectory, prefix + "_iter" + iteration + suffix);
        while (file.exists()) {
            iteration++;
            file = new File(planningDirectory, prefix + "_iter" + iteration + suffix);
        }

        String path = file.getPath();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(string);
        }
        return file.getAbsolutePath();
    }
}
