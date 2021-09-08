package esa.mo.nmf.apps.monitoring;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.Instant;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mp.plandistribution.consumer.PlanDistributionAdapter;
import org.ccsds.moims.mo.mp.planedit.PlanEditHelper;
import org.ccsds.moims.mo.mp.planexecutioncontrol.consumer.PlanExecutionControlAdapter;
import org.ccsds.moims.mo.mp.planningrequest.consumer.PlanningRequestAdapter;
import org.ccsds.moims.mo.mp.structures.ActivityInstanceDetails;
import org.ccsds.moims.mo.mp.structures.ActivityUpdateDetails;
import org.ccsds.moims.mo.mp.structures.ActivityUpdateDetailsList;
import org.ccsds.moims.mo.mp.structures.PlanUpdateDetails;
import org.ccsds.moims.mo.mp.structures.PlanUpdateDetailsList;
import org.ccsds.moims.mo.mp.structures.PlanVersionDetails;
import org.ccsds.moims.mo.mp.structures.PlanVersionDetailsList;
import org.ccsds.moims.mo.mp.structures.RequestUpdateDetails;
import org.ccsds.moims.mo.mp.structures.RequestUpdateDetailsList;
import org.ccsds.moims.mo.mp.structures.TimeTrigger;
import org.ccsds.moims.mo.mp.structures.Trigger;
import esa.mo.apps.autonomy.planner.converter.mp.MPConverter;
import esa.mo.apps.autonomy.util.FileUtils;
import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.mp.impl.api.MPArchiveManager;
import esa.mo.mp.impl.com.COMObjectIdHelper;
import esa.mo.mp.impl.consumer.PlanDistributionConsumerServiceImpl;
import esa.mo.mp.impl.consumer.PlanExecutionControlConsumerServiceImpl;
import esa.mo.mp.impl.consumer.PlanningRequestConsumerServiceImpl;
import esa.mo.mp.impl.provider.PlanDistributionProviderServiceImpl;
import esa.mo.mp.impl.provider.PlanExecutionControlProviderServiceImpl;
import esa.mo.mp.impl.provider.PlanningRequestProviderServiceImpl;
import esa.mo.mp.impl.util.MOFactory;
import esa.mo.mp.impl.util.MPPolyFix;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.NMFInterface;

/**
 * MPExpermentLogger monitors Requests, Plans, Plan Statuses and Activities
 * and logs the responses in a table format to separate log files
 */
public class MPExperimentLogger {

    private static final Logger LOGGER = Logger.getLogger(MPExperimentLogger.class.getName());

    private static final String REQUEST_UPDATE_FORMAT = "%1$-24s|%2$-25s|%3$-19s|%4$-12s|%5$-15s|%6$-11s|%7$s";
    private static final String PLAN_UPDATE_FORMAT = "%1$-24s|%2$-18s|%3$-17s|%4$-16s|%5$-19s|%6$-15s|%7$-15s|%8$s";
    private static final String PLAN_STATUS_UPDATE_FORMAT = "%1$-24s|%2$-16s|%3$-13s|%4$-12s|%5$s";
    private static final String ACTIVITY_UPDATE_FORMAT = "%1$-24s|%2$-18s|%3$-16s|%4$-12s|%5$-16s|%6$-24s|%7$-25s|%8$-11s|%9$s";

    private static final String REQUEST_LOGS_SUFFIX = "PublishedRequestUpdates";
    private static final String PLAN_LOGS_SUFFIX = "PublishedPlanUpdates";
    private static final String PLAN_STATUS_LOGS_SUFFIX = "PublishedPlanStatusUpdates";
    private static final String ACTIVITY_LOGS_SUFFIX = "PublishedActivityUpdates";

    private static final String DEFAULT_MONITORING_PATH = "monitoring";
    private static File logsDirectory = null;

    private static MPArchiveManager archiveManager;

    private static String logPrefix = "AutonomyAppSession_";

    private static PlanningRequestConsumerServiceImpl planningRequestServiceConsumer;
    private static PlanDistributionConsumerServiceImpl planDistributionServiceConsumer;
    private static PlanExecutionControlConsumerServiceImpl planExecutionControlServiceConsumer;

    private MPExperimentLogger() {}

    public static void init(NMFInterface connector) {
        long timeNow = Instant.now().toEpochMilli();
        logPrefix = String.format("AutonomyAppSession_%s", MPConverter.formatISO8601(timeNow));

        String logsPath = System.getProperty("autonomy.app.monitoring", DEFAULT_MONITORING_PATH);
        logsDirectory = FileUtils.createDirectory(logsPath, "monitoring logs", DEFAULT_MONITORING_PATH);

        try {
            archiveManager = connector.getMPServices().getArchiveManager();
        } catch (NMFException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }

        try {
            PlanningRequestProviderServiceImpl planningRequestServiceProvider = connector.getMPServices()
                .getPlanningRequestService();
            PlanDistributionProviderServiceImpl planDistributionServiceProvider = connector.getMPServices()
                .getPlanDistributionService();
            PlanExecutionControlProviderServiceImpl planExecutionControlServiceProvider = connector.getMPServices()
                .getPlanExecutionControlService();

            // Consumer setup
            COMServicesConsumer comServicesConsumer = new COMServicesConsumer();

            SingleConnectionDetails prsConnection = planningRequestServiceProvider.getConnectionProvider().getConnectionDetails();
            planningRequestServiceConsumer = new PlanningRequestConsumerServiceImpl(prsConnection, comServicesConsumer);
            comServicesConsumer.init(planningRequestServiceConsumer.getConnectionConsumer());

            SingleConnectionDetails pdsConnection = planDistributionServiceProvider.getConnectionProvider().getConnectionDetails();
            planDistributionServiceConsumer = new PlanDistributionConsumerServiceImpl(pdsConnection, comServicesConsumer);
            comServicesConsumer.init(planDistributionServiceConsumer.getConnectionConsumer());

            SingleConnectionDetails pecConnection = planExecutionControlServiceProvider.getConnectionProvider().getConnectionDetails();
            planExecutionControlServiceConsumer = new PlanExecutionControlConsumerServiceImpl(pecConnection, comServicesConsumer);
            comServicesConsumer.init(planExecutionControlServiceConsumer.getConnectionConsumer());

            initRequestLogs();
            initPlanLogs();
            initPlanStatusLogs();
            initActivityLogs();

            monitorRequests();
            monitorPlans();
            monitorPlanStatuses();
            monitorActivities();
        } catch (MALInteractionException | MALException | MalformedURLException | NMFException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    private static void initRequestLogs() {
        Object[] columnHeaders = {
            "Timestamp", "Request Identity", "Request Version ID",
            "Plan Ref ID", "Request Status", "Error code", "Error info"
        };

        String header = String.format(REQUEST_UPDATE_FORMAT, columnHeaders);
        log(REQUEST_LOGS_SUFFIX, header);
    }

    private static void initPlanLogs() {
        Object[] columnHeaders = {
            "Timestamp", "Plan Identity", "Plan Identity ID",
            "Plan Version ID", "Planned Activities", "Planned Events",
            "Description", "Comments"
        };

        String header = String.format(PLAN_UPDATE_FORMAT, columnHeaders);
        log(PLAN_LOGS_SUFFIX, header);
    }

    private static void initPlanStatusLogs() {
        Object[] columnHeaders = {
            "Timestamp", "Plan Version ID", "Is alternate",
            "Plan Status", "Termination info"
        };

        String header = String.format(PLAN_STATUS_UPDATE_FORMAT, columnHeaders);
        log(PLAN_STATUS_LOGS_SUFFIX, header);
    }

    private static void initActivityLogs() {
        Object[] columnHeaders = {
            "Timestamp", "Activity Identity", "Plan Version ID",
            "Activity ID", "Activity Status", "Planned trigger",
            "Predicted or Actual time", "Error code", "Information"
        };

        String header = String.format(ACTIVITY_UPDATE_FORMAT, columnHeaders);
        log(ACTIVITY_LOGS_SUFFIX, header);
    }

    private static void monitorRequests() throws MALInteractionException, MALException {
        final Subscription subscription = MOFactory.createSubscription();
        planningRequestServiceConsumer.getPlanningRequestStub().monitorRequestsRegister(subscription, new PlanningRequestAdapter() {
            @Override
            public void monitorRequestsNotifyReceived(MALMessageHeader msgHeader, Identifier identifier, UpdateHeaderList headerList, RequestUpdateDetailsList updateList, Map qosProperties) {
                for (int index = 0; index < updateList.size(); index++) {
                    UpdateHeader updateHeader = headerList.get(index);
                    RequestUpdateDetails update = updateList.get(index);
                    logRequestUpdate(updateHeader, update);
                }
            }
        });
    }

    private static void monitorPlans() throws MALInteractionException, MALException {
        final Subscription subscription = MOFactory.createSubscription();
        planDistributionServiceConsumer.getPlanDistributionStub().monitorPlanRegister(subscription, new PlanDistributionAdapter() {
            @Override
            public void monitorPlanNotifyReceived(MALMessageHeader msgHeader, Identifier identifier, UpdateHeaderList headerList, PlanVersionDetailsList versionList, Map qosProperties) {
                for (int index = 0; index < versionList.size(); index++) {
                    UpdateHeader updateHeader = headerList.get(index);
                    PlanVersionDetails planVersion = versionList.get(index);
                    logPlanUpdate(updateHeader, planVersion);
                }
            }
        });
    }

    private static void monitorPlanStatuses() throws MALInteractionException, MALException {
        final Subscription subscription = MOFactory.createSubscription();
        planDistributionServiceConsumer.getPlanDistributionStub().monitorPlanStatusRegister(subscription, new PlanDistributionAdapter() {
            @Override
            public void monitorPlanStatusNotifyReceived(MALMessageHeader msgHeader, Identifier identifier, UpdateHeaderList headerList, ObjectIdList planVersionIdList, PlanUpdateDetailsList updateList, Map qosProperties) {
                for (int index = 0; index < updateList.size(); index++) {
                    ObjectId planVersionId = planVersionIdList.get(index);
                    PlanUpdateDetails update = updateList.get(index);
                    logPlanStatusUpdate(planVersionId, update);
                }
            }
        });
    }

    private static void monitorActivities() throws MALInteractionException, MALException {
        final Subscription subscription = MOFactory.createSubscription();
        planExecutionControlServiceConsumer.getPlanExecutionControlStub().monitorActivitiesRegister(subscription, new PlanExecutionControlAdapter() {
            @Override
            public void monitorActivitiesNotifyReceived(MALMessageHeader msgHeader, Identifier identifier, UpdateHeaderList headerList, ActivityUpdateDetailsList updateList, Map qosProperties) {
                for (int index = 0; index < updateList.size(); index++) {
                    UpdateHeader updateHeader = headerList.get(index);
                    ActivityUpdateDetails update = updateList.get(index);
                    logActivityUpdate(updateHeader, update);
                }
            }
        });
    }

    private static void logRequestUpdate(UpdateHeader updateHeader, RequestUpdateDetails update) {
        String identity = updateHeader.getKey().getFirstSubKey().getValue();

        Object[] data = {
            HelperTime.time2readableString(update.getTimestamp()),
            identity,
            COMObjectIdHelper.getInstanceId(update.getRequestId()),
            COMObjectIdHelper.getInstanceId(update.getPlanRef()),
            update.getStatus(),
            update.getErrCode(),
            update.getErrInfo()
        };

        String row = String.format(REQUEST_UPDATE_FORMAT, data);
        log(REQUEST_LOGS_SUFFIX, row);
    }

    private static void logPlanUpdate(UpdateHeader updateHeader, PlanVersionDetails planVersion) {
        String identity = updateHeader.getKey().getFirstSubKey().getValue();
        Long identityId = updateHeader.getKey().getSecondSubKey();
        Long versionId = updateHeader.getKey().getThirdSubKey();
        int plannedActivitiesCount = 0;
        int plannedEventsCount = 0;
        String planDescription = "";
        String planComments = "";
        if (planVersion != null) {
            if (planVersion.getItems() != null) {
                if (planVersion.getItems().getPlannedActivities() != null) {
                    plannedActivitiesCount = planVersion.getItems().getPlannedActivities().size();
                }
                if (planVersion.getItems().getPlannedEvents() != null) {
                    plannedEventsCount = planVersion.getItems().getPlannedEvents().size();
                }
            }
            if (planVersion.getInformation() != null) {
                planDescription = planVersion.getInformation().getDescription();
                planComments = planVersion.getInformation().getComments();
            }
        }

        Object[] data = {
            HelperTime.time2readableString(updateHeader.getTimestamp()),
            identity,
            identityId,
            versionId,
            plannedActivitiesCount,
            plannedEventsCount,
            planDescription,
            planComments
        };

        String row = String.format(PLAN_UPDATE_FORMAT, data);
        log(PLAN_LOGS_SUFFIX, row);
    }

    private static void logPlanStatusUpdate(ObjectId planVersionId, PlanUpdateDetails update) {
        Long versionId = COMObjectIdHelper.getInstanceId(planVersionId);
        Object[] data = {
            HelperTime.time2readableString(update.getTimestamp()),
            versionId,
            update.getIsAlternate(),
            update.getStatus(),
            update.getTerminationInfo()
        };

        String row = String.format(PLAN_STATUS_UPDATE_FORMAT, data);
        log(PLAN_STATUS_LOGS_SUFFIX, row);
    }

    private static void logActivityUpdate(UpdateHeader updateHeader, ActivityUpdateDetails update) {
        String identity = updateHeader.getKey().getFirstSubKey().getValue();
        Long instanceId = updateHeader.getKey().getThirdSubKey();
        String plannedTrigger = null;
        String actualTime = null;
        if (update.getStart() != null) {
            Trigger trigger = MPPolyFix.decode(update.getStart());
            actualTime = HelperTime.time2readableString(trigger.getTime());
            if (trigger instanceof TimeTrigger) {
                TimeTrigger timeTrigger = (TimeTrigger)trigger;
                plannedTrigger = HelperTime.time2readableString(timeTrigger.getTriggerTime());
            } else {
                plannedTrigger = "Unsupported type";
            }
        }

        ObjectId activityInstanceId = COMObjectIdHelper.getObjectId(instanceId, PlanEditHelper.ACTIVITYINSTANCE_OBJECT_TYPE);
        ActivityInstanceDetails activityInstance = archiveManager.ACTIVITY.getInstance(activityInstanceId);

        String info = "";
        if (activityInstance != null && activityInstance.getComments() != null) {
            info += activityInstance.getComments();
        }

        if (update.getErrInfo() != null) {
            info += info.length() > 0 ? " - " : "";
            info += update.getErrInfo();
        }

        Object[] data = {
            HelperTime.time2readableString(update.getTimestamp()),
            identity,
            COMObjectIdHelper.getInstanceId(update.getPlanVersionId()),
            instanceId,
            update.getStatus(),
            plannedTrigger,
            actualTime,
            update.getErrCode(),
            info
        };

        String row = String.format(ACTIVITY_UPDATE_FORMAT, data);
        log(ACTIVITY_LOGS_SUFFIX, row);
    }

    private static void log(String logSuffix, String text) {
        String textRow = text + "\n";
        File logFile = new File(logsDirectory, String.format("%s_%s.txt", logPrefix, logSuffix));
        try (FileOutputStream outputStream = new FileOutputStream(logFile, true)) {
            outputStream.write(textRow.getBytes());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }
}
