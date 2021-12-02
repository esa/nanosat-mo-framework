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
package esa.mo.nmf.comarchivetool.adapters;

import esa.mo.nmf.comarchivetool.TimestampedParameterValue;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.parameter.ParameterHelper;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Archive adapter that retrieves available parameter names and their values.
 *
 * @author marcel.mikolajko
 */
public class ArchiveToParametersAdapter extends ArchiveAdapter implements QueryStatusProvider  {

    private static final Logger LOGGER = Logger.getLogger(ArchiveToAppListAdapter.class.getName());

    /**
     * True if the query is over (response or any error received)
     */
    private boolean isQueryOver = false;

    /**
     * MC.Parameter.ParameterIdentity object type
     */
    private final ObjectType parameterIdentityType = ParameterHelper.PARAMETERIDENTITY_OBJECT_TYPE;

    /**
     * MC.Parameter.ParameterDefinition object type
     */
    private final ObjectType parameterDefinitionType = ParameterHelper.PARAMETERDEFINITION_OBJECT_TYPE;

    /**
     * MC.Parameter.ParameterValueInstance object type
     */
    private final ObjectType parameterValueType = ParameterHelper.PARAMETERVALUEINSTANCE_OBJECT_TYPE;

    /**
     * Map from ParameterIdentity ID to it's name
     */
    private final Map<Long, Identifier> identitiesMap = new HashMap<>();

    /**
     * Map from ParameterDefinition ID to it's related ParameterIdentity ID
     */
    private final Map<Long, Long> definitionsMap = new HashMap<>();

    /**
     * Map from ParameterDefinition ID to a list of the parameter values
     */
    private final Map<Long, List<TimestampedParameterValue>> valuesMap = new HashMap<>();

    /**
     * Map from Parameter ID to a list of the parameter values
     */
    private final Map<Long, List<TimestampedParameterValue>> parameterValuesMap = new HashMap<>();


    @Override
    public void queryResponseReceived(MALMessageHeader msgHeader, ObjectType objType,
                                      IdentifierList domain, ArchiveDetailsList objDetails, ElementList objBodies,
                                      Map qosProperties) {
        processObjects(objType, objDetails, objBodies);

        for(Map.Entry<Long, List<TimestampedParameterValue>> entry : valuesMap.entrySet()) {
            Long parameterId = definitionsMap.get(entry.getKey());

            if(parameterValuesMap.containsKey(parameterId)) {
                parameterValuesMap.get(parameterId).addAll(entry.getValue());
            } else {
                parameterValuesMap.put(parameterId, entry.getValue());
            }
        }

        setIsQueryOver(true);
    }

    @Override
    public void queryUpdateReceived(MALMessageHeader msgHeader, ObjectType objType,
                                    IdentifierList domain, ArchiveDetailsList objDetails, ElementList objBodies,
                                    Map qosProperties) {
        processObjects(objType, objDetails, objBodies);
    }

    /**
     * Fills the maps based on the type of the object
     * @param type Type of the objects to be processed
     * @param detailsList Archive details of the objects
     * @param bodiesList Bodies of the objects
     */
    private void processObjects(ObjectType type, ArchiveDetailsList detailsList, ElementList bodiesList) {
        if(type == null || type.equals(parameterIdentityType)) {
            for(int i = 0; i < detailsList.size(); ++i) {
                identitiesMap.put(detailsList.get(i).getInstId(), (Identifier) bodiesList.get(i));
            }
        } else if(type.equals(parameterDefinitionType)) {
            for (ArchiveDetails archiveDetails : detailsList) {
                definitionsMap.put(archiveDetails.getInstId(), archiveDetails.getDetails().getRelated());
            }
        } else if(type.equals(parameterValueType)) {
            for(int i = 0; i < detailsList.size(); ++i) {
                if(valuesMap.containsKey(detailsList.get(i).getDetails().getRelated())) {
                    valuesMap.get(detailsList.get(i).getDetails().getRelated())
                             .add(new TimestampedParameterValue((ParameterValue) bodiesList.get(i), detailsList.get(i).getTimestamp()));
                } else {
                    List<TimestampedParameterValue> values = new ArrayList<>();
                    values.add(new TimestampedParameterValue((ParameterValue) bodiesList.get(i), detailsList.get(i).getTimestamp()));
                    valuesMap.put(detailsList.get(i).getDetails().getRelated(), values);
                }
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

    @Override
    public boolean isQueryOver() {
        return isQueryOver;
    }

    private synchronized void setIsQueryOver(boolean isQueryOver) {
        this.isQueryOver = isQueryOver;
    }

    public List<Identifier> getParameterIdentities() {
        return (List<Identifier>) identitiesMap.values();
    }

    public Map<Long, List<TimestampedParameterValue>> getParameterValuesMap() {
        return parameterValuesMap;
    }

    public Identifier getParameterIdentifierByDefinitionId(Long definitionId)
    {
        return identitiesMap.get(definitionsMap.get(definitionId));
    }

    public Long getParameterDefinitionIdByIdentifer(Identifier identifier)
    {
        for(Map.Entry entry: identitiesMap.entrySet())
        {
            if(Objects.equals(entry.getValue(), identifier))
            {
                return (Long) entry.getKey();
            }
        }
        return null;
    }
}
