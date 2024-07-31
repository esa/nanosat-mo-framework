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
package esa.mo.nmf.nanosatmosupervisor.parameter;

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import esa.mo.nmf.nanosatmosupervisor.MCSupervisorBasicAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads the list of OBSW aggregation from the aggregation XML file and provides it as a map.
 *
 * @author Tanguy Soto
 */
public class AggregationLister {
    private static final Logger LOGGER = Logger.getLogger(MCSupervisorBasicAdapter.class.getName());

    /**
     * Tag to read elements
     */
    private static final String TAG_AGGREGATION = "aggregation";

    /**
     * Tag to read elements
     */
    private static final String TAG_PARAMETER = "parameter";

    /**
     * XML attribute name
     */
    private static final String ATTRIBUTE_ID = "id";

    /**
     * XML attribute name
     */
    private static final String ATTRIBUTE_DYNAMIC = "dynamic";

    /**
     * XML attribute name
     */
    private static final String ATTRIBUTE_BUILTIN = "builtin";

    /**
     * XML attribute name
     */
    private static final String ATTRIBUTE_NAME = "name";

    /**
     * XML attribute name
     */
    private static final String ATTRIBUTE_CATEGORY = "category";

    /**
     * XML attribute name
     */
    private static final String ATTRIBUTE_DESCRIPTION = "description";

    /**
     * XML attribute name
     */
    private static final String ATTRIBUTE_UPDATE_INTERVAL = "updateInterval";

    /**
     * XML attribute name
     */
    private static final String ATTRIBUTE_GENERATION_ENABLED = "generationEnabled";

    /**
     * XML attribute name
     */
    private static final String ATTRIBUTE_REF = "ref";

    /**
     * Map of aggregations defined in the aggregation XML file so they can be accessed by aggregation
     * id.
     */
    private final Map<Long, OBSWAggregation> aggregationMap;

    /**
     * Initializes this object with the list of aggregations defined in the aggregations file, using
     * the map of parameters to resolve parameter references.
     *
     * @param aggregations The aggregations file to read.
     * @param parameterLister Object providing the list of OBSW parameter
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public AggregationLister(InputStream aggregations, ParameterLister parameterLister) throws IOException,
        SAXException, ParserConfigurationException {
        LOGGER.log(Level.INFO, "Loading OBSW aggregations");
        aggregationMap = readAggregations(aggregations, parameterLister);
    }

    /**
     * Returns the OBSW aggregations, mapped by aggregation ID.
     * 
     * @return The aggregations
     */
    public Map<Long, OBSWAggregation> getAggregationMap() {
        return aggregationMap;
    }

    /**
     * Reads the aggregations XML file to create the aggregations defined there, using the map of
     * parameters to resolve parameter references.
     *
     * @param aggregations The XML file to read.
     * @param parameterLister Object providing the list of OBSW parameter
     * @return The map of aggregations found (by ID).
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    private Map<Long, OBSWAggregation> readAggregations(InputStream aggregations, ParameterLister parameterLister)
        throws ParserConfigurationException, IOException, SAXException {
        Map<Long, OBSWAggregation> map = new HashMap<>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(aggregations);

        NodeList aggregationNodeList = document.getElementsByTagName(TAG_AGGREGATION);
        for (int i = 0; i < aggregationNodeList.getLength(); i++) {
            Node aggregationNode = aggregationNodeList.item(i);
            if (Node.ELEMENT_NODE == aggregationNode.getNodeType()) {
                Element aggregationElement = (Element) aggregationNode;

                String id = aggregationElement.getAttribute(ATTRIBUTE_ID);
                String dynamic = aggregationElement.getAttribute(ATTRIBUTE_DYNAMIC);
                String builtin = aggregationElement.getAttribute(ATTRIBUTE_BUILTIN);
                String name = aggregationElement.getAttribute(ATTRIBUTE_NAME);
                String category = aggregationElement.getAttribute(ATTRIBUTE_CATEGORY);
                String description = aggregationElement.getAttribute(ATTRIBUTE_DESCRIPTION);
                String updateInterval = aggregationElement.getAttribute(ATTRIBUTE_UPDATE_INTERVAL);
                String generationEnabled = aggregationElement.getAttribute(ATTRIBUTE_GENERATION_ENABLED);

                OBSWAggregation aggregation = new OBSWAggregation();
                aggregation.setId(Long.decode(id));
                aggregation.setDynamic(Boolean.parseBoolean(dynamic));
                aggregation.setBuiltin(Boolean.parseBoolean(builtin));
                aggregation.setName(name);
                aggregation.setCategory(category);
                aggregation.setDescription(description);
                if (updateInterval != null && !updateInterval.isEmpty()) {
                    aggregation.setUpdateInterval(Long.parseLong(updateInterval));
                }
                aggregation.setGenerationEnabled(Boolean.parseBoolean(generationEnabled));

                for (OBSWParameter p : getParameterList(parameterLister.getParameters(), aggregationElement)) {
                    aggregation.getParameters().add(p);
                }

                map.put(aggregation.getId(), aggregation);
            }
        }

        return map;
    }

    /**
     * Reads the parameters for a single aggregation XML element.
     *
     * @param parameterMap Map of OBSW parameters to resolve parameter references.
     * @param aggregationElement The XML node to get sub-elements.
     * @return The list of parameters that are referenced by aggregationElement.
     */
    private List<OBSWParameter> getParameterList(Map<Identifier, OBSWParameter> parameterMap,
        Element aggregationElement) {
        List<OBSWParameter> parameterList = new ArrayList<>();

        NodeList OBSWParameterNodeList = aggregationElement.getElementsByTagName(TAG_PARAMETER);

        for (int i = 0; i < OBSWParameterNodeList.getLength(); i++) {
            Node OBSWParameterNode = OBSWParameterNodeList.item(i);

            if (Node.ELEMENT_NODE == OBSWParameterNode.getNodeType()) {
                Element OBSWParameterElement = (Element) OBSWParameterNode;

                String paramName = OBSWParameterElement.getAttribute(ATTRIBUTE_REF);
                OBSWParameter parameter = parameterMap.get(new Identifier(paramName));
                // Only add references that can be found
                if (parameter != null) {
                    parameterList.add(parameter);
                }
            }
        }

        return parameterList;
    }
}
