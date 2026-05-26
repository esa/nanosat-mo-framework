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
import org.ccsds.moims.mo.com.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.structures.HeterogeneousList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.parameter.ParameterServiceInfo;
import org.ccsds.moims.mo.mc.structures.ParameterDefinition;
import org.ccsds.moims.mo.mc.structures.ParameterValue;

/**
 * Archive adapter that retrieves available parameter names and their values.
 *
 * @author marcel.mikolajko
 */
public class ArchiveToParametersAdapter extends ArchiveAdapter implements QueryStatusProvider {

    private static final Logger LOGGER = Logger.getLogger(ArchiveToAppListAdapter.class.getName());

    private boolean isQueryOver = false;

    private final ObjectType parameterDefinitionType = ParameterServiceInfo.PARAMETERDEFINITION_OBJECT_TYPE;
    private final ObjectType parameterValueType = ParameterServiceInfo.PARAMETERVALUE_OBJECT_TYPE;

    /** Map: domain → list of parameter names. */
    private final Map<IdentifierList, List<Identifier>> parameterNames = new HashMap<>();

    /** Map: domain → (definitionId → name). */
    private final Map<IdentifierList, Map<Long, Identifier>> namesMap = new HashMap<>();

    /** Map: domain → (definitionId → list of timestamped values). */
    private final Map<IdentifierList, Map<Long, List<TimestampedParameterValue>>> valuesMap = new HashMap<>();

    /** Map: domain → (name → list of timestamped values). */
    private final Map<IdentifierList, Map<Identifier, List<TimestampedParameterValue>>> parameterValues = new HashMap<>();

    @Override
    public void queryResponseReceived(MALMessageHeader msgHeader, Map qosProperties) {
        for (IdentifierList domainKey : valuesMap.keySet()) {
            parameterValues.computeIfAbsent(domainKey, k -> new HashMap<>());

            for (Map.Entry<Long, List<TimestampedParameterValue>> entry : valuesMap.get(domainKey).entrySet()) {
                Identifier name = namesMap.get(domainKey).get(entry.getKey());
                parameterValues.get(domainKey).put(name, entry.getValue());
            }
        }
        setIsQueryOver(true);
    }

    @Override
    public void queryUpdateReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain,
        ArchiveDetailsList objDetails, HeterogeneousList objBodies, Map qosProperties) {
        processObjects(objType, objDetails, objBodies, domain);
    }

    private void processObjects(ObjectType type, ArchiveDetailsList detailsList,
            HeterogeneousList bodiesList, IdentifierList domain) {
        parameterNames.computeIfAbsent(domain, k -> new ArrayList<>());
        namesMap.computeIfAbsent(domain, k -> new HashMap<>());
        valuesMap.computeIfAbsent(domain, k -> new HashMap<>());

        if (type == null || type.equals(parameterDefinitionType)) {
            for (int i = 0; i < detailsList.size(); ++i) {
                Identifier name = ((ParameterDefinition) bodiesList.get(i)).getName();
                namesMap.get(domain).put(detailsList.get(i).getId(), name);
                parameterNames.get(domain).add(name);
            }
        } else if (type.equals(parameterValueType)) {
            for (int i = 0; i < detailsList.size(); ++i) {
                Long defId = detailsList.get(i).getLinks().getRelated();
                TimestampedParameterValue tv = new TimestampedParameterValue(
                        (ParameterValue) bodiesList.get(i), detailsList.get(i).getTimestamp());
                valuesMap.get(domain).computeIfAbsent(defId, k -> new ArrayList<>()).add(tv);
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

    public Map<IdentifierList, List<Identifier>> getParameterNames() {
        return parameterNames;
    }

    public Map<IdentifierList, Map<Identifier, List<TimestampedParameterValue>>> getParameterValues() {
        return parameterValues;
    }

    /** Returns a map from definition id to parameter name, for use when resolving aggregation parameter references. */
    public Map<IdentifierList, Map<Long, Identifier>> getNamesMap() {
        return namesMap;
    }
}
