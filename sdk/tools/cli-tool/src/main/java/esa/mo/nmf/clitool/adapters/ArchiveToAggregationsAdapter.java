package esa.mo.nmf.clitool.adapters;

import esa.mo.nmf.clitool.TimestampedAggregationValue;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.aggregation.AggregationHelper;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetails;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArchiveToAggregationsAdapter extends ArchiveAdapter implements QueryStatusProvider {

    private static final Logger LOGGER = Logger.getLogger(ArchiveToAggregationsAdapter.class.getName());

    /**
     * True if the query is over (response or any error received)
     */
    private boolean isQueryOver = false;

    private final Map<IdentifierList, Map<Long, List<TimestampedAggregationValue>>> aggregationValues = new HashMap<>();
    private final Map<IdentifierList, Map<Long, AggregationDefinitionDetails>> aggregationDefinitions = new HashMap<>();

    public Map<IdentifierList, Map<Long, List<TimestampedAggregationValue>>> getAggregationValues() {
        return aggregationValues;
    }

    public Map<IdentifierList, Map<Long, AggregationDefinitionDetails>> getAggregationDefinitions() {
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
    public void queryUpdateReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain,
            ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {
        processObjects(objType, objDetails, objBodies, domain);
    }

    @Override
    public void queryResponseReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain,
            ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {
        if (objDetails == null) {
            setIsQueryOver(true);
            return;
        }
        processObjects(objType, objDetails, objBodies, domain);

        setIsQueryOver(true);
    }

    /**
     * Fills the maps based on the type of the object
     *
     * @param type Type of the objects to be processed
     * @param detailsList Archive details of the objects
     * @param bodiesList Bodies of the objects
     */
    private void processObjects(ObjectType type, ArchiveDetailsList detailsList, ElementList bodiesList,
            IdentifierList domain) {
        if (detailsList == null) {
            return;
        }

        if (!aggregationValues.containsKey(domain)) {
            aggregationValues.put(domain, new HashMap<>());
        }

        if (!aggregationDefinitions.containsKey(domain)) {
            aggregationDefinitions.put(domain, new HashMap<>());
        }

        if (AggregationHelper.AGGREGATIONVALUEINSTANCE_OBJECT_TYPE.equals(type)) {
            for (int i = 0; i < detailsList.size(); ++i) {
                AggregationValue value = (AggregationValue) bodiesList.get(i);
                Long definitionId = detailsList.get(i).getDetails().getRelated();
                if (aggregationValues.get(domain).containsKey(definitionId)) {
                    aggregationValues.get(domain).get(definitionId).add(new TimestampedAggregationValue(value,
                            detailsList.get(i).getTimestamp()));
                } else {
                    List<TimestampedAggregationValue> list = new ArrayList<>();
                    list.add(new TimestampedAggregationValue(value, detailsList.get(i).getTimestamp()));
                    aggregationValues.get(domain).put(definitionId, list);
                }
            }
        } else if (AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE.equals(type)) {
            for (int i = 0; i < detailsList.size(); ++i) {
                aggregationDefinitions.get(domain).put(detailsList.get(i).getInstId(),
                        (AggregationDefinitionDetails) bodiesList.get(i));
            }
        }
    }

    @Override
    public void queryAckErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
        LOGGER.log(Level.SEVERE, "queryAckErrorReceived", error);
        setIsQueryOver(true);
    }

    @Override
    public void queryUpdateErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
        LOGGER.log(Level.SEVERE, "queryUpdateErrorReceived", error);
        setIsQueryOver(true);
    }

    @Override
    public void queryResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
        LOGGER.log(Level.SEVERE, "queryResponseErrorReceived", error);
        setIsQueryOver(true);
    }

}
