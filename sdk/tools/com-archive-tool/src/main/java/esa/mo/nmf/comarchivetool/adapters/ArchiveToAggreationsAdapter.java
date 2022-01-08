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
     * Map from Aggregation Definition Details ID to Aggregation values
     */
    private final Map<IdentifierList, Map<Long, List<TimestampedAggregationValue>>> aggregationValues = new HashMap<>();

    /**
     * Map from Aggregation Definition ID to AggregationDefinition
     */
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

    private synchronized void setIsQueryOver(final boolean isQueryOver) {
        this.isQueryOver = isQueryOver;
    }

    @Override
    public void queryUpdateReceived(final MALMessageHeader msgHeader, final ObjectType objType,
                                    final IdentifierList domain, final ArchiveDetailsList objDetails, final ElementList objBodies,
                                    final Map qosProperties) {
        processObjects(objType, objDetails, objBodies, domain);
    }

    @Override
    public void queryResponseReceived(final MALMessageHeader msgHeader, final ObjectType objType,
                                      final IdentifierList domain, final ArchiveDetailsList objDetails, final ElementList objBodies,
                                      final Map qosProperties) {
        if(objDetails == null) {
            setIsQueryOver(true);
            return;
        }
        processObjects(objType, objDetails, objBodies, domain);

        setIsQueryOver(true);
    }

    /**
     * Fills the maps based on the type of the object
     *
     * @param type        Type of the objects to be processed
     * @param detailsList Archive details of the objects
     * @param bodiesList  Bodies of the objects
     */
    private void processObjects(final ObjectType type, final ArchiveDetailsList detailsList, final ElementList bodiesList, final IdentifierList domain) {
        if (detailsList == null) {
            return;
        }

        if(!aggregationValues.containsKey(domain)) {
            aggregationValues.put(domain, new HashMap<>());
        }

        if(!aggregationDefinitions.containsKey(domain)) {
            aggregationDefinitions.put(domain, new HashMap<>());
        }

        if (AggregationHelper.AGGREGATIONVALUEINSTANCE_OBJECT_TYPE.equals(type)) {
            for (int i = 0; i < detailsList.size(); ++i) {
                final AggregationValue value = (AggregationValue) bodiesList.get(i);
                final Long definitionId = detailsList.get(i).getDetails().getRelated();
                if (aggregationValues.get(domain).containsKey(definitionId)) {
                    aggregationValues.get(domain).get(definitionId).add(new TimestampedAggregationValue(
                            value, detailsList.get(i).getTimestamp()
                    ));
                } else {
                    final List<TimestampedAggregationValue> list = new ArrayList<>();
                    list.add(new TimestampedAggregationValue(value, detailsList.get(i).getTimestamp()));
                    aggregationValues.get(domain).put(definitionId, list);
                }
            }
        } else if (AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE.equals(type)) {
            for (int i = 0; i < detailsList.size(); ++i) {
                aggregationDefinitions.get(domain).put(detailsList.get(i).getInstId(), (AggregationDefinitionDetails) bodiesList.get(i));
            }
        }
    }

    @Override
    public void queryAckErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error,
                                      final Map qosProperties) {
        LOGGER.log(Level.SEVERE, "queryAckErrorReceived", error);
        setIsQueryOver(true);
    }

    @Override
    public void queryUpdateErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error,
                                         final Map qosProperties) {
        LOGGER.log(Level.SEVERE, "queryUpdateErrorReceived", error);
        setIsQueryOver(true);
    }

    @Override
    public void queryResponseErrorReceived(final MALMessageHeader msgHeader, final MALStandardError error,
                                           final Map qosProperties) {
        LOGGER.log(Level.SEVERE, "queryResponseErrorReceived", error);
        setIsQueryOver(true);
    }

}
