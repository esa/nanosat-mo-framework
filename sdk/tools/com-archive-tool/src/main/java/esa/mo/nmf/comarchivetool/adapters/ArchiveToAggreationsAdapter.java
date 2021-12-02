package esa.mo.nmf.comarchivetool.adapters;

import esa.mo.nmf.comarchivetool.TimestampedAggregationValue;
import esa.mo.nmf.comarchivetool.TimestampedParameterValue;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.aggregation.AggregationHelper;
import org.ccsds.moims.mo.mc.aggregation.structures.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArchiveToAggreationsAdapter extends ArchiveAdapter implements QueryStatusProvider {

    private static final Logger LOGGER = Logger.getLogger(ArchiveToAggreationsAdapter.class.getName());

    /**
     * True if the query is over (response or any error received)
     */
    private boolean isQueryOver = false;

    /**
     * Map from Aggregation Definition ID to AggregationDefinition
     */
    private final Map<Long, List<TimestampedAggregationValue>> aggregationValues = new HashMap<>();

    /**
     * Map from Aggregation Definition ID to AggregationDefinition
     */
    private final Map<Long, AggregationDefinitionDetails> aggregationDefinitions = new HashMap<>();

    public Map<Long, List<TimestampedAggregationValue>> getAggregationValues() {
        return aggregationValues;
    }

    public Map<Long, AggregationDefinitionDetails> getAggregationDefinitions() {
        return aggregationDefinitions;
    }

    @Override
    public boolean isQueryOver() {
        return isQueryOver;
    }

    private synchronized void setIsQueryOver(boolean isQueryOver) {
        this.isQueryOver = isQueryOver;
    }

    @Override
    public void queryUpdateReceived(MALMessageHeader msgHeader, ObjectType objType,
                                    IdentifierList domain, ArchiveDetailsList objDetails, ElementList objBodies,
                                    Map qosProperties) {
        processObjects(objType, objDetails, objBodies);
    }

    @Override
    public void queryResponseReceived(MALMessageHeader msgHeader, ObjectType objType,
                                      IdentifierList domain, ArchiveDetailsList objDetails, ElementList objBodies,
                                      Map qosProperties) {
        processObjects(objType, objDetails, objBodies);

        setIsQueryOver(true);
    }

    /**
     * Fills the maps based on the type of the object
     *
     * @param type        Type of the objects to be processed
     * @param detailsList Archive details of the objects
     * @param bodiesList  Bodies of the objects
     */
    private void processObjects(ObjectType type, ArchiveDetailsList detailsList, ElementList bodiesList) {
        if (detailsList == null) {
            return;
        }

        if (AggregationHelper.AGGREGATIONVALUEINSTANCE_OBJECT_TYPE.equals(type)) {

            //details .details.related ; timestamp ; bodieslist AggregationValueList

            for (int i = 0; i < detailsList.size(); ++i) {

                AggregationValue agValue = (AggregationValue) bodiesList.get(i);
                Long agDefinitionId = detailsList.get(i).getDetails().getRelated();
                if (aggregationValues.containsKey(agDefinitionId)) {
                    aggregationValues.get(agDefinitionId).add(new TimestampedAggregationValue(
                            agValue, detailsList.get(i).getTimestamp()
                    ));
                } else {
                    List<TimestampedAggregationValue> list = new ArrayList<>();
                    list.add(new TimestampedAggregationValue(agValue, detailsList.get(i).getTimestamp()));
                    aggregationValues.put(agDefinitionId, list);
                }
            }
        } else if (AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE.equals(type)) {
            for (int i = 0; i < detailsList.size(); ++i) {
                aggregationDefinitions.put(detailsList.get(i).getInstId(), (AggregationDefinitionDetails) bodiesList.get(i));
            }
        }
    }

    @Override
    public void queryAckErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
                                      Map qosProperties) {
        LOGGER.log(Level.SEVERE, "queryAckErrorReceived", error);
        setIsQueryOver(true);
    }

    @Override
    public void queryUpdateErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
                                         Map qosProperties) {
        LOGGER.log(Level.SEVERE, "queryUpdateErrorReceived", error);
        setIsQueryOver(true);
    }

    @Override
    public void queryResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
                                           Map qosProperties) {
        LOGGER.log(Level.SEVERE, "queryResponseErrorReceived", error);
        setIsQueryOver(true);
    }

}
