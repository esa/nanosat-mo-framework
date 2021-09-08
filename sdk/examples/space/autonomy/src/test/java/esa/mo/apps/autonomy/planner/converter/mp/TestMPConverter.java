package esa.mo.apps.autonomy.planner.converter.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mp.structures.ActivityDetails;
import org.ccsds.moims.mo.mp.structures.ActivityNode;
import org.ccsds.moims.mo.mp.structures.ArgSpec;
import org.ccsds.moims.mo.mp.structures.ArgSpecList;
import org.ccsds.moims.mo.mp.structures.ArgType;
import org.ccsds.moims.mo.mp.structures.PositionExpression;
import org.ccsds.moims.mo.mp.structures.RequestVersionDetails;
import org.ccsds.moims.mo.mp.structures.SimpleActivityDetails;
import org.ccsds.moims.mo.mp.structures.TimeExpression;
import org.ccsds.moims.mo.mp.structures.TimeWindow;
import org.ccsds.moims.mo.mp.structures.TimeWindowList;
import org.ccsds.moims.mo.mp.structures.c_ActivityDetailsList;
import org.ccsds.moims.mo.mp.structures.c_Expression;
import org.junit.BeforeClass;
import org.junit.Test;
import esa.esoc.hso.osa.apsi.application.planner.Planner;
import esa.esoc.hso.osa.apsi.application.planner.PlanningProblem;
import esa.esoc.hso.osa.apsi.trf.kernel.application.ApplicationSolution;
import esa.esoc.hso.osa.apsi.trf.kernel.application.ApplicationSolvingParameters;
import esa.esoc.hso.osa.apsi.trf.kernel.application.ApplicationSolvingTypes;
import esa.esoc.hso.osa.apsi.trf.kernel.domain.DomainTimeline;
import esa.mo.apps.autonomy.util.CoordinateConverter;
import esa.mo.apps.autonomy.vw.VisibilityWindowItem;
import esa.mo.mp.impl.util.MPFactory;
import esa.mo.mp.impl.util.MPPolyFix;

public class TestMPConverter {

    private static final Logger LOGGER = Logger.getLogger(TestMPConverter.class.getName());

    @BeforeClass
    public static void setup() {
        System.setProperty("apsi.ddl.file", "src/test/resources/apsi/OPS_SAT_Domain_edt.ddl");
        System.setProperty("apsi.locking.duration.min", "00.000.00.00.20.000");
        System.setProperty("apsi.locking.duration.max", "00.000.00.00.20.000");
        System.setProperty("apsi.locked.duration.min", "00.000.00.00.10.000");
        System.setProperty("apsi.locked.duration.max", "00.000.00.00.10.000");
        System.setProperty("apsi.makepic.duration.min", "00.000.00.00.04.000");
        System.setProperty("apsi.makepic.duration.max", "00.000.00.00.04.000");
        MPConverter.init();
    }

    @Test
    public void testConverter() throws Exception {

        RequestVersionDetails requestVersion = createTestRequest();

        TimeWindow timeWindow = requestVersion.getValidityTime().get(0);
        Time goalStart = (Time)timeWindow.getStart().getValue();
        Time goalEnd = (Time)timeWindow.getEnd().getValue();

        Time release = goalStart;
        Time deadline = goalEnd;

        List<GoalItem> goals = new ArrayList<>();
        String latitude = CoordinateConverter.toPlannerCoordinate("10");
        String longitude = CoordinateConverter.toPlannerCoordinate("51");
        String[] arguments = { latitude, longitude, "2", "T", "T" };
        goals.add(new GoalItem("TakePicture", arguments));

        List<VisibilityWindowItem> visibilityWindows = new ArrayList<>();
        long visibilityStart = goalStart.getValue() + 10000;
        long visibilityEnd = goalStart.getValue() + 70000;
        visibilityWindows.add(new VisibilityWindowItem(10d, 51d, visibilityStart, visibilityEnd));

        String plannedLatitude = CoordinateConverter.toPlannerCoordinate("10");
        String plannedLongitude = CoordinateConverter.toPlannerCoordinate("51");

        long lockingStart = goalStart.getValue() + 2000;
        long lockingEnd = goalStart.getValue() + 7000;
        String[] lockingArguments = new String[] { plannedLatitude, plannedLongitude };

        long makePicStart = goalStart.getValue() + 22000;
        long makePicEnd = goalStart.getValue() + 27000;
        String[] makePicArguments = new String[] { plannedLatitude, plannedLongitude, "1" };

        List<TimelineItem> plannedTimeline = new ArrayList<>();
        plannedTimeline.add(new TimelineItem("1", "Locking", lockingStart, lockingEnd, lockingArguments));
        plannedTimeline.add(new TimelineItem("2", "MakePic", makePicStart, makePicEnd, makePicArguments));

        long timeNow = Instant.now().toEpochMilli();
        String filePrefix = String.format("Request_%s", MPConverter.formatISO8601(timeNow));

        PlanningProblem planningProblem = MPConverter.convert(release, deadline, goals, visibilityWindows, plannedTimeline, filePrefix);
        assertNotNull(planningProblem);
        assertEquals("Problem for Observe_Area", planningProblem.problem.getIdentifier());
        assertEquals(0, planningProblem.problem.getSubProblems().size());
        assertEquals(23, planningProblem.problem.getMetaAssertions().size());

        ApplicationSolvingParameters solvingParameters = new ApplicationSolvingParameters();
        solvingParameters.solving_type = ApplicationSolvingTypes.ALL;
        solvingParameters.max_number_of_solutions = 1;
        solvingParameters.garbage_collection = false;

        Planner planner = new Planner(planningProblem);

        Instant start = Instant.now();
        List<ApplicationSolution> solutions = planner.solve(solvingParameters);
        Instant finish = Instant.now();
        long solveTime = Duration.between(start, finish).toMillis();

        LOGGER.info("Planner solve time " + solveTime + "ms");
        LOGGER.info("Planner found " + solutions.size() + " solutions");

        assertFalse(solutions.isEmpty());

        ApplicationSolution solution = solutions.get(0);
        boolean solutionChosen = planner.chooseSolution(solution);

        if (solutionChosen) {
            DomainTimeline domainTimeline = planningProblem.domain.getDefaultDomainTimeline();

            List<TimelineItem> timelineItems = MPConverter.convert(domainTimeline, filePrefix);

            assertNotNull(timelineItems);
            assertFalse(timelineItems.isEmpty());
            System.out.println(timelineItems.toString().replaceAll(", ", ",\n"));
            assertEquals(22, timelineItems.size());

            boolean listChanged = timelineItems.removeAll(plannedTimeline);
            assertTrue(listChanged);
            assertEquals(20, timelineItems.size());
        }
    }

    private RequestVersionDetails createTestRequest() {
        RequestVersionDetails requestVersion = MPFactory.createRequestVersion();

        requestVersion.setDescription("Take picture at 10,51");

        TimeExpression windowStart = new TimeExpression("==", null, ArgType.TIME, new Time(1577836800000L));
        TimeExpression windowEnd = new TimeExpression("==", null, ArgType.TIME, new Time(1609459199000L));
        TimeWindow timeWindow = new TimeWindow(windowStart, windowEnd);

        TimeWindowList timeWindows = new TimeWindowList();
        timeWindows.add(timeWindow);
        requestVersion.setValidityTime(timeWindows);

        SimpleActivityDetails simpleActivity = new SimpleActivityDetails();
        simpleActivity.setActivityDef(null); // TODO

        PositionExpression positionExpression = new PositionExpression("==", null, ArgType.POSITION, new Union("10,51"));
        c_Expression expression = MPPolyFix.encode(positionExpression);
        ArgSpec positionArg = new ArgSpec(new Identifier("position"), expression, null);

        ArgSpecList argSpecList = new ArgSpecList();
        argSpecList.add(positionArg);
        simpleActivity.setArgSpecs(argSpecList);

        List<ActivityDetails> activityDetailsList = new ArrayList<>();
        activityDetailsList.add(simpleActivity);

        c_ActivityDetailsList activities = MPPolyFix.encodeActivityDetails(activityDetailsList);

        ActivityNode activityNode = new ActivityNode();
        activityNode.setComments("Take picture activity");

        activityNode.setActivities(activities);
        requestVersion.setActivities(activityNode);

        return requestVersion;
    }
}
