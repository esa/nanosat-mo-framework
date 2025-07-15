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
package esa.mo.nmf.clitool.adapters;

import esa.mo.nmf.clitool.TimestampedParameterValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.structures.HeterogeneousList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.parameter.ParameterServiceInfo;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;

/**
 * Archive adapter that retrieves available parameter names and their values.
 *
 * @author marcel.mikolajko
 */
public class ArchiveToParametersAdapter extends ArchiveAdapter implements QueryStatusProvider {

    private static final Logger LOGGER = Logger.getLogger(ArchiveToAppListAdapter.class.getName());

    /**
     * True if the query is over (response or any error received)
     */
    private boolean isQueryOver = false;

    /**
     * MC.Parameter.ParameterIdentity object type
     */
    private final ObjectType parameterIdentityType = ParameterServiceInfo.PARAMETERIDENTITY_OBJECT_TYPE;

    /**
     * MC.Parameter.ParameterDefinition object type
     */
    private final ObjectType parameterDefinitionType = ParameterServiceInfo.PARAMETERDEFINITION_OBJECT_TYPE;

    /**
     * MC.Parameter.ParameterValueInstance object type
     */
    private final ObjectType parameterValueType = ParameterServiceInfo.PARAMETERVALUEINSTANCE_OBJECT_TYPE;

    private final Map<IdentifierList, List<Identifier>> parameterIdentities = new HashMap<>();
    private final Map<IdentifierList, Map<Long, Identifier>> identitiesMap = new HashMap<>();
    private final Map<IdentifierList, Map<Long, Long>> definitionsMap = new HashMap<>();

    /**
     * Map from ParameterDefinition ID to a list of the parameter values segregated based on the domain
     */
    private final Map<IdentifierList, Map<Long, List<TimestampedParameterValue>>> valuesMap = new HashMap<>();

    /**
     * Map from parameter name to a list of it's values segregated based on the domain
     */
    private final Map<IdentifierList, Map<Identifier, List<TimestampedParameterValue>>> parameterValues = new HashMap<>();

    @Override
    public void queryResponseReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain,
        ArchiveDetailsList objDetails, HeterogeneousList objBodies, Map qosProperties) {
        if (objDetails == null) {
            setIsQueryOver(true);
            return;
        }
        processObjects(objType, objDetails, objBodies, domain);

        for (IdentifierList domainKey : valuesMap.keySet()) {
            if (!parameterValues.containsKey(domainKey)) {
                parameterValues.put(domainKey, new HashMap<>());
            }

            Map<Long, List<TimestampedParameterValue>> parameters = valuesMap.get(domainKey);
            for (Map.Entry<Long, List<TimestampedParameterValue>> entry : parameters.entrySet()) {
                Identifier identity = identitiesMap.get(domainKey).get(definitionsMap.get(domainKey).get(entry.getKey()));
                parameterValues.get(domainKey).put(identity, entry.getValue());
            }
        }

        setIsQueryOver(true);
    }

    @Override
    public void queryUpdateReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain,
        ArchiveDetailsList objDetails, HeterogeneousList objBodies, Map qosProperties) {
        processObjects(objType, objDetails, objBodies, domain);
    }

    /**
     * Fills the maps based on the type of the object
     * @param type Type of the objects to be processed
     * @param detailsList Archive details of the objects
     * @param bodiesList Bodies of the objects
     */
    private void processObjects(ObjectType type, ArchiveDetailsList detailsList,
            HeterogeneousList bodiesList, IdentifierList domain) {
        if (!parameterIdentities.containsKey(domain)) {
            parameterIdentities.put(domain, new ArrayList<>());
        }

        if (!identitiesMap.containsKey(domain)) {
            identitiesMap.put(domain, new HashMap<>());
        }

        if (!definitionsMap.containsKey(domain)) {
            definitionsMap.put(domain, new HashMap<>());
        }

        if (!valuesMap.containsKey(domain)) {
            valuesMap.put(domain, new HashMap<>());
        }

        if (type == null || type.equals(parameterIdentityType)) {
            for (int i = 0; i < detailsList.size(); ++i) {
                identitiesMap.get(domain).put(detailsList.get(i).getInstId(), (Identifier) bodiesList.get(i));
                parameterIdentities.get(domain).add((Identifier) bodiesList.get(i));
            }
        } else if (type.equals(parameterDefinitionType)) {
            for (ArchiveDetails archiveDetails : detailsList) {
                definitionsMap.get(domain).put(archiveDetails.getInstId(), archiveDetails.getLinks().getRelated());
            }
        } else if (type.equals(parameterValueType)) {
            for (int i = 0; i < detailsList.size(); ++i) {
                if (valuesMap.get(domain).containsKey(detailsList.get(i).getLinks().getRelated())) {
                    valuesMap.get(domain).get(detailsList.get(i).getLinks().getRelated()).add(
                        new TimestampedParameterValue((ParameterValue) bodiesList.get(i), detailsList.get(i).getTimestamp()));
                } else {
                    List<TimestampedParameterValue> values = new ArrayList<>();
                    values.add(new TimestampedParameterValue((ParameterValue) bodiesList.get(i), detailsList.get(i).getTimestamp()));
                    valuesMap.get(domain).put(detailsList.get(i).getLinks().getRelated(), values);
                }
            }
        }
    }

    @Override
    public void queryAckErrorReceived(MALMessageHeader msgHeader, MOErrorException error, Map qosProperties) {
        LOGGER.log(Level.SEVERE, "queryAckErrorReceived", error);
        setIsQueryOver(true);
    }

    @Override
    public void queryUpdateErrorReceived(MALMessageHeader msgHeader, MOErrorException error, Map qosProperties) {
        LOGGER.log(Level.SEVERE, "queryUpdateErrorReceived", error);
        setIsQueryOver(true);
    }

    @Override
    public void queryResponseErrorReceived(MALMessageHeader msgHeader, MOErrorException error, Map qosProperties) {
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

    public Map<IdentifierList, List<Identifier>> getParameterIdentities() {
        return parameterIdentities;
    }

    public Map<IdentifierList, Map<Identifier, List<TimestampedParameterValue>>> getParameterValues() {
        return parameterValues;
    }

    public Map<IdentifierList, Map<Long, Identifier>> getIdentitiesMap() {
        return identitiesMap;
    }
}
